import sbt._

object Dependencies {
  object Version {
    val kindProjectorVersion = "0.10.3"

    val cats = "2.0.0"
    val catsEffect = "2.0.0"

    val zio = "1.0.0-RC13"
    val ziocats = "2.0.0.0-RC3"

    val logback = "1.2.3"
    val log4cats = "1.0.0"
    val conduction = "0.3.4"

    val scalacheck = "1.14.2"
  }

  val cats = "org.typelevel" %% "cats-core" % Version.cats
  val catsEffect = "org.typelevel" %% "cats-effect" % Version.catsEffect

  val zio = "dev.zio" %% "zio" % Version.zio
  val ziocats = "dev.zio" %% "zio-interop-cats" % Version.ziocats

  val logback = "ch.qos.logback" % "logback-classic" % Version.logback
  val conduction = "com.github.leigh-perry" %% "conduction-core" % Version.conduction

  val scalacheck = "org.scalacheck" %% "scalacheck" % Version.scalacheck
  val catsLaws = "org.typelevel" %% "cats-laws" % Version.cats
}
