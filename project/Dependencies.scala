import sbt._

object Dependencies {
  object Version {
    val kindProjectorVersion = "0.10.3"

    val cats = "2.1.0"
    val catsEffect = "2.0.0"

    val zio = "1.0.0-RC17"
    val zioConfig = "1.0.0-RC10"
    val ziocats = "2.0.0.0-RC10"

    val logback = "1.2.3"
    val log4zio = "0.2.5"
  }

  val cats = "org.typelevel" %% "cats-core" % Version.cats
  val catsEffect = "org.typelevel" %% "cats-effect" % Version.catsEffect

  val zio = "dev.zio" %% "zio" % Version.zio
  val zioConfig = "dev.zio" %% "zio-config" % Version.zioConfig
  val ziocats = "dev.zio" %% "zio-interop-cats" % Version.ziocats

  val logback = "ch.qos.logback" % "logback-classic" % Version.logback
  val log4zio = "com.github.leigh-perry" %% "log4zio-slf4j" % Version.log4zio

  val zioTest = "dev.zio" %% "zio-test" % Version.zio
  val zioTestSbt = "dev.zio" %% "zio-test-sbt" % Version.zio
}
