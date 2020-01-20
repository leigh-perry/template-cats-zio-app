import Dependencies._

val Scala_213 = "2.13.1"
val Scala_212 = "2.12.10"
//val Scala_211 = "2.11.12"

////

val projectName = "lptemplatedivision-lptemplateprojectname"
val organisationName = "lptemplatecompany"

//inThisBuild(
//  List(
//    organization := "com.github.leigh-perry",
//    homepage := Some(url("https://github.com/leigh-perry/${projectName.toLowerCase}")),
//    licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
//    developers :=
//      List(
//        Developer(
//          "leigh-perry",
//          "Leigh Perry",
//          "lperry.breakpoint@gmail.com",
//          url("https://leigh-perry.github.io")
//        )
//      )
//  )
//)

lazy val gitCommitAuthor: String = {
  import sys.process._

  val stdout = new StringBuilder
  val stderr = new StringBuilder
  val status = "git log -1 --pretty=%an/%ae" ! ProcessLogger(stdout.append(_), stderr.append(_))

  val s = stdout.toString
  if (status == 0) {
    val trimmed = s.trim
    if (trimmed.length == 0) "(none)" else trimmed
  } else {
    "(error retrieving git commit author)"
  }
}

lazy val compilerPlugins =
  List(
    compilerPlugin("org.typelevel" %% "kind-projector" % Version.kindProjectorVersion)
  )

lazy val commonSettings =
  Seq(
    scalaVersion := Scala_213,
    scalacOptions ++= commonScalacOptions(scalaVersion.value),
    fork in Test := true,
    testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework")),
    name := projectName,
    organization := organisationName,
    updateOptions := updateOptions.value.withGigahorse(false),
    libraryDependencies ++=
      Seq(
        zioTest % Test,
        zioTestSbt % Test
      ) ++ compilerPlugins,
    //test in assembly := {},
    //assemblyJarName in assembly := assemblyJar,
    assemblyMergeStrategy in assembly := {
      case x @ _ =>
        val blacklist = List("zio/BuildInfo$.class", "module-info.class")
        if (blacklist.exists(x.toString.contains(_)))
          MergeStrategy.discard
        else {
          val oldStrategy = (assemblyMergeStrategy in assembly).value
          oldStrategy(x)
        }
    }
  )

lazy val crossBuiltCommonSettings = commonSettings ++ Seq(
  crossScalaVersions := Seq(Scala_212, Scala_213)
)

lazy val shared =
  module("shared")
    .settings(
      libraryDependencies ++=
        Seq(
          cats,
          catsEffect,
          zio,
          ziocats,
          logback,
          zioConfig
        )
    )
    .enablePlugins(BuildInfoPlugin)
    .settings(
      buildInfoKeys :=
        Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion) ++
          Seq[BuildInfoKey](
            libraryDependencies,
            BuildInfoKey.action("buildTime")(java.time.LocalDateTime.now),
            BuildInfoKey.action("gitCommitIdentifier")(git.gitHeadCommit.value.get),
            BuildInfoKey.action("gitHashShort")(git.gitHeadCommit.value.get.substring(0, 8)),
            BuildInfoKey.action("gitBranch")(git.gitCurrentBranch.value),
            BuildInfoKey.action("gitCommitAuthor")(gitCommitAuthor),
            BuildInfoKey.action("gitCommitDate")(git.gitHeadCommitDate.value.get),
            BuildInfoKey.action("gitMessage")(git.gitHeadMessage.value.get),
            BuildInfoKey.action("gitUncommittedChanges")(git.gitUncommittedChanges.value)
          ),
      buildInfoPackage := "com.lptemplatecompany.lptemplatedivision.lptemplateservicename"
    )

lazy val lptemplateservicename =
  app("lptemplateservicename")
    .dependsOn(shared % testDependencies)
    .settings(
      libraryDependencies ++=
        Seq(
          log4zio
        )
    )
    .enablePlugins(BuildInfoPlugin)

lazy val allModules = List(shared, lptemplateservicename)

lazy val root =
  project
    .in(file("."))
    .settings(commonSettings)
    .settings(skip in publish := true, crossScalaVersions := List())
    .aggregate(allModules.map(x => x: ProjectReference): _*)
    .dependsOn(shared, lptemplateservicename)
    .enablePlugins(sbtdocker.DockerPlugin)
    .settings(dockerSettings)

addCommandAlias("fmt", "all scalafmtSbt scalafmt test:scalafmt")
addCommandAlias("fmtcheck", "all scalafmtSbtCheck scalafmtCheck test:scalafmtCheck")

////

def module(moduleName: String): Project =
  Project(moduleName, file("modules/" + moduleName))
    .settings(crossBuiltCommonSettings)
    .settings(name += s"-$moduleName")

def app(appName: String): Project =
  Project(appName, file("apps/" + appName))
    .settings(crossBuiltCommonSettings)
    .settings(name += s"-$appName")

def versionDependentExtraScalacOptions(scalaVersion: String) =
  CrossVersion.partialVersion(scalaVersion) match {
    case Some((2, minor)) if minor < 13 =>
      Seq("-Yno-adapted-args", "-Xfuture", "-Ypartial-unification")
    case _ => Nil
  }

def commonScalacOptions(scalaVersion: String) =
  Seq(
    "-encoding",
    "UTF-8",
    "-feature",
    "-language:existentials",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-language:experimental.macros",
    "-unchecked",
    "-Ywarn-dead-code",
    "-Ywarn-numeric-widen",
    "-Ywarn-value-discard",
    //"-Xfatal-warnings",
    "-deprecation",
    "-Xlint:-unused,_"
  ) ++
    versionDependentExtraScalacOptions(scalaVersion)

val testDependencies = "compile->compile;test->test"

////

// // Create assembly only for top level project
aggregate in assembly := false

lazy val dockerSettings =
  Seq(
    dockerfile in docker := {
      // The assembly task generates a fat JAR file
      val artifact: File = assembly.value
      val artifactTargetPath = s"/app/${artifact.name}"

      new Dockerfile {
        from("openjdk:11-jre-slim")
        add(new File("docker-components/run_app.sh"), "/app/run_app.sh")
        add(artifact, artifactTargetPath)
        entryPoint("/app/run_app.sh", artifactTargetPath)
      }
    },
    buildOptions in docker := BuildOptions(cache = false),
    imageNames in docker :=
      Seq(
        // Sets the latest tag
        ImageName(s"${organization.value}/$projectName:latest"),
        // Sets a name with a tag that contains the project version
        ImageName(
          repository = name.value,
          tag = Some(s"v${version.value}")
        )
      )
  )
