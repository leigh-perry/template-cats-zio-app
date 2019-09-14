import sbt._

object Dependencies {

  object Version {
    val scala = "2.12.10"

    val cats = "2.0.0"
    val catsEffect = "2.0.0"

    val zio = "1.0.0-RC12-1"
    val ziocats = "2.0.0.0-RC3"

    val logback = "1.2.3"
    val conduction = "0.2.1"
    
    val minitest = "2.7.0"
    val scalacheck = "1.14.0"
  }

  val cats = "org.typelevel" %% "cats-core" % Version.cats
  val catsEffect = "org.typelevel" %% "cats-effect" % Version.catsEffect

  val zio = "dev.zio" %% "zio" % Version.zio
  val ziocats = "dev.zio" %% "zio-interop-cats" % Version.ziocats

  val logback = "ch.qos.logback" % "logback-classic" % Version.logback
  val conduction = "com.github.leigh-perry" %% "conduction" % Version.conduction

  val minitest = "io.monix" %% "minitest" % Version.minitest
  val minitestLaws = "io.monix" %% "minitest-laws" % Version.minitest
  val scalacheck = "org.scalacheck" %% "scalacheck" % Version.scalacheck
  val catsLaws = "org.typelevel" %% "cats-laws" % Version.cats

  val scalaReflect = "org.scala-lang" % "scala-reflect" % Version.scala
}
