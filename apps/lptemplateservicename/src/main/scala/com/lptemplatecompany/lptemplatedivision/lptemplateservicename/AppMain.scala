package com.lptemplatecompany.lptemplatedivision.lptemplateservicename

import cats.syntax.option._
import com.leighperry.log4zio.Log
import com.leighperry.log4zio.Log.SafeLog
import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.config.AppConfig
import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.interpreter.Info
import zio.blocking.Blocking
import zio.config.Config
import zio.{App, UIO, ZEnv, ZIO}

object AppMain extends App {

  final case class AppEnv(
    log: Log.Service[Nothing, String],
    config: Config.Service[AppConfig],
    spark: Spark.Service
  ) extends SafeLog[String]
    with Spark
    with Config[AppConfig]
    with Blocking.Live

  val appName = "LPTEMPLATESERVICENAME"

  override def run(args: List[String]): ZIO[ZEnv, Nothing, Int] =
    for {
      logsvc <- Log.console[String](appName.some)
      log = logsvc.log

      pgm = for {
        config <- Config.fromEnv(AppConfig.descriptor, None)
        spark <- Spark.local(appName)
        _ <- Application.program.provide(AppEnv(log, config.config, spark.spark))
      } yield ()

      exitCode <- pgm.foldM(
        e => log.error(s"Application failed: $e") *> ZIO.succeed(1),
        _ => log.info("Application terminated with no error indication") *> ZIO.succeed(0)
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
  def spark: Spark.Service
}

object Spark {
  trait Service {
    def sparkSession: SparkSession
  }

  def make(session: => SparkSession): ZIO[Blocking, Throwable, Spark] =
    zio
      .blocking
      .effectBlocking(session)
      .map(
        session =>
          new Spark {
            override def spark: Service =
              new Service {
                override def sparkSession: SparkSession =
                  session
              }
          }
      )

  def local(name: String): ZIO[Blocking, Throwable, Spark] =
    make {
      // As a real-world example:
      //    SparkSession.builder().appName(name).master("local").getOrCreate()
      SparkSession(name)
    }

  def cluster(name: String): ZIO[Blocking, Throwable, Spark] =
    make {
      // As a real-world example:
      //    SparkSession.builder().appName(name).enableHiveSupport().getOrCreate()
      SparkSession(name)
    }
}

////

// The core application
object Application {
  import Log.stringLog

  val logProgramConfig: ZIO[Config[AppConfig] with SafeLog[String], Nothing, Unit] =
    for {
      log <- stringLog
      cfg <- zio.config.config[AppConfig]
      _ <- log.info(s"Executing parameters ${cfg.inputPath} and ${cfg.outputPath} without sparkSession")
    } yield ()

  val runSparkJob: ZIO[Spark with SafeLog[String] with Blocking, Throwable, Unit] =
    for {
      log <- stringLog
      spark <- ZIO.access[Spark](_.spark.sparkSession)
      _ <- log.info(s"Executing something with spark ${spark.version}")
      result <- zio.blocking.effectBlocking(spark.slowOp("SELECT something"))
      _ <- log.info(s"Executed something with spark ${spark.version}: $result")
    } yield ()

  val processData: ZIO[Spark with Config[AppConfig] with SafeLog[String], Throwable, Unit] =
    for {
      log <- stringLog
      cfg <- zio.config.config[AppConfig]
      spark <- ZIO.access[Spark](_.spark.sparkSession)
      _ <- log.info(s"Executing ${cfg.inputPath} and ${cfg.outputPath} using ${spark.version}")
    } yield ()

  import zio.interop.catz._

  val program: ZIO[Spark with Config[AppConfig] with SafeLog[String] with Blocking, Throwable, Unit] =
    for {
      log <- stringLog
      cfg <- zio.config.config[AppConfig]
      info <- Info.of[UIO, AppConfig](cfg, log, Info.keyBasedObfuscation(List("password", "credential")))
      _ <- info.logEnvironment
      _ <- logProgramConfig
      _ <- runSparkJob
      _ <- processData
    } yield ()
}
