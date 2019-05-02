import Dependencies._

val projectName = "lptemplatedivision-lptemplateservicename"

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

lazy val commonSettings =
  ProjectDefaults.settings ++
    Seq(
      name := projectName,
      organization := "com.lptemplatecompany",
      scalaVersion := Dependencies.Version.scala
    )

val tests = "compile->compile;test->test"

lazy val shared =
  module(
    id = "shared",
    deps =
      Seq(
        cats,
        zio,
        ziocats,
        logback,
        conduction,
        minitest % "test",
        minitestLaws % "test",
        scalacheck % "test",
        catsLaws % "test",
      )
  ).enablePlugins(BuildInfoPlugin)
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
            BuildInfoKey.action("gitUncommittedChanges")(git.gitUncommittedChanges.value),
          ),
      buildInfoPackage := "com.lptemplatecompany.lptemplatedivision.lptemplateservicename"
    )

lazy val lptemplateprojectname =
  module(
    id = "lptemplateprojectname",
    deps =
      Seq(
      )
  )
    .enablePlugins(GitVersioning)
    .dependsOn(shared % tests)
    .settings(
      name := "lptemplateprojectname",
      buildInfoPackage := "com.lptemplatecompany.lptemplatedivision.lptemplateservicename",
      Test / run / fork := true,
    )

lazy val root =
  (project in file("."))
    .aggregate(shared, lptemplateprojectname)
    .dependsOn(shared, lptemplateprojectname)
    .enablePlugins(sbtdocker.DockerPlugin)
    .settings(inConfig(Test)(baseAssemblySettings))

val DockerTest = config("docker-int") extend Test

def module(id: String, settings: Seq[Def.Setting[_]] = commonSettings, deps: Seq[ModuleID] = Vector()): Project = {
  Project(id = id, base = file(s"modules/$id"))
    .settings(settings)
    .settings(
      name := s"$projectName-$id",
      libraryDependencies ++= deps ++ Seq("org.scala-lang" % "scala-reflect" % "2.12.8")
    )
}
