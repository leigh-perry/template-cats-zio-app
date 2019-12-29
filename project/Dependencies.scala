import sbt._

object Dependencies {
  object Version {
    val kindProjectorVersion = "0.10.3"

    val cats = "2.1.0"
    val catsEffect = "2.0.0"

    val zio = "1.0.0-RC17"
    val ziocats = "2.0.0.0-RC10"

    val logback = "1.2.3"
    val conduction = "0.4.2"
    val log4zio = "0.2.4"

    val scalacheck = "1.14.3"
  }

  val cats = "org.typelevel" %% "cats-core" % Version.cats
  val catsEffect = "org.typelevel" %% "cats-effect" % Version.catsEffect

  val zio = "dev.zio" %% "zio" % Version.zio
  val ziocats = "dev.zio" %% "zio-interop-cats" % Version.ziocats

  val logback = "ch.qos.logback" % "logback-classic" % Version.logback
  val conduction = "com.github.leigh-perry" %% "conduction-core" % Version.conduction
  val log4zio = "com.github.leigh-perry" %% "log4zio-slf4j" % Version.log4zio

  val scalacheck = "org.scalacheck" %% "scalacheck" % Version.scalacheck
  val catsLaws = "org.typelevel" %% "cats-laws" % Version.cats
}
