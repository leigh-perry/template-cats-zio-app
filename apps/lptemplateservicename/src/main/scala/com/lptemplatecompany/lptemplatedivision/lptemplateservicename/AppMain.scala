package com.lptemplatecompany.lptemplatedivision.lptemplateservicename

import cats.syntax.option._
import com.leighperry.log4zio.Log
import com.leighperry.log4zio.Log.SafeLog
import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.config.AppConfig
import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.interpreter.Info
import com.lptemplatecompany.lptemplatedivision.shared.Apps
import zio.config.ZConfig
import zio.{ App, ExitCode, IO, UIO, ZEnv, ZIO }

object AppMain extends App {

  val appName = "LPTEMPLATESERVICENAME"

  override def run(args: List[String]): ZIO[ZEnv, Nothing, ExitCode] =
    for {
      log <- Log.console[String](appName.some)

      pgm = for {
        config <-
          Apps
            .toIO(zio.system.System.live >>> ZConfig.fromSystemEnv(AppConfig.descriptor))
            .mapError(AppError.InvalidConfiguration)

        cfg = config.get
        spark <- Spark.local(appName)
        _ <- new Application(cfg, log, spark).program
      } yield ()

      exitCode <- pgm.foldM(
        e => log.error(s"Application failed: $e") *> IO.succeed(ExitCode.failure),
        _ => log.info("Application terminated with no error indication") *> IO.succeed(ExitCode.success)
      )

    } yield exitCode
}

////

final case class SparkSession(name: String) {
  // stubs for the real Spark
  def slowOp(value: String): Unit =
    Thread.sleep(value.length * 100L)

  def version: String =
    "someVersion"
}

////

trait Spark {
  def sparkSession: SparkSession
}

object Spark {
  def make(session: => SparkSession): IO[Throwable, Spark] =
    Apps
      .effectBlocking(session)
      .map(
        session =>
          new Spark {
            override def sparkSession: SparkSession =
              session
          }
      )

  def local(name: String): IO[Throwable, Spark] =
    make {
      // As a real-world example:
      //    SparkSession.builder().appName(name).master("local").getOrCreate()
      SparkSession(name)
    }

  def cluster(name: String): IO[Throwable, Spark] =
    make {
      // As a real-world example:
      //    SparkSession.builder().appName(name).enableHiveSupport().getOrCreate()
      SparkSession(name)
    }
}

////

// The core application
class Application(cfg: AppConfig, log: SafeLog[String], spark: Spark) {
  val logProgramConfig: IO[Nothing, Unit] =
    log.info(s"Executing parameters ${cfg.inputPath} and ${cfg.outputPath} without sparkSession")

  val runSparkJob: IO[Throwable, Unit] =
    for {
      _ <- log.info(s"Executing something with spark ${spark.sparkSession.version}")
      result <- Apps.effectBlocking(spark.sparkSession.slowOp("SELECT something"))
      _ <- log.info(s"Executed something with spark ${spark.sparkSession.version}: $result")
    } yield ()

  val processData: IO[Throwable, Unit] =
    for {
      _ <- log.info(s"Executing ${cfg.inputPath} and ${cfg.outputPath} using ${spark.sparkSession.version}")
    } yield ()

  import zio.interop.catz._

  val program: IO[Throwable, Unit] =
    for {
      info <- Info.of[UIO, AppConfig](cfg, log, Info.keyBasedObfuscation(List("password", "credential")))
      _ <- info.logEnvironment
      _ <- logProgramConfig
      _ <- runSparkJob
      _ <- processData
    } yield ()
}
