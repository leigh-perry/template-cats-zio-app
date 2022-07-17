import sbt._

object Dependencies {
  object Version {
    val kindProjectorVersion = "0.10.3"

    val cats = "2.6.1"
    val catsEffect = "3.1.1"
    val catsMouse = "1.0.11"

    val zio = "1.0.16"
    val zioConfig = "1.0.10"
    val zioCats = "3.3.0"

    val logback = "1.2.11"
    val log4zio = "1.0.10"
  }

  val cats = "org.typelevel" %% "cats-core" % Version.cats
  val catsEffect = "org.typelevel" %% "cats-effect" % Version.catsEffect
  val catsMouse = "org.typelevel" %% "mouse" % Version.catsMouse

  val zio = "dev.zio" %% "zio" % Version.zio
  val zioConfig = "dev.zio" %% "zio-config" % Version.zioConfig
  val zioCats = "dev.zio" %% "zio-interop-cats" % Version.zioCats

  val logback = "ch.qos.logback" % "logback-classic" % Version.logback
  val log4zio = "com.github.leigh-perry" %% "log4zio-slf4j" % Version.log4zio

  val zioTest = "dev.zio" %% "zio-test" % Version.zio
  val zioTestSbt = "dev.zio" %% "zio-test-sbt" % Version.zio
}
