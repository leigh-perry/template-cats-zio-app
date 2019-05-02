import sbt._

object Dependencies {

  object Version {
    val scala = "2.12.8"

    val cats = "1.6.0"
    val catsEffect = "1.3.0"

    val zio = "1.0-RC4"

    val logback = "1.2.3"
    val conduction = "0.1.1"
    
    val minitest = "2.4.0"
    val scalacheck = "1.14.0"
  }

  val cats = "org.typelevel" %% "cats-core" % Version.cats
  val catsEffect = "org.typelevel" %% "cats-effect" % Version.catsEffect

  val zio = "org.scalaz" %% "scalaz-zio" % Version.zio
  val ziocats = "org.scalaz" %% "scalaz-zio-interop-cats" % Version.zio

  val logback = "ch.qos.logback" % "logback-classic" % Version.logback
  val conduction = "com.github.leigh-perry" %% "conduction" % Version.conduction

  val minitest = "io.monix" %% "minitest" % Version.minitest
  val minitestLaws = "io.monix" %% "minitest-laws" % Version.minitest
  val scalacheck = "org.scalacheck" %% "scalacheck" % Version.scalacheck
  val catsLaws = "org.typelevel" %% "cats-laws" % Version.cats

  val scalaReflect = "org.scala-lang" % "scala-reflect" % Version.scala
}
