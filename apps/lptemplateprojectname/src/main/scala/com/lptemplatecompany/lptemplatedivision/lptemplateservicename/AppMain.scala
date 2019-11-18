package com.lptemplatecompany.lptemplatedivision.lptemplateservicename

import cats.syntax.option._
import com.leighperry.log4zio.Log
import com.leighperry.log4zio.Log.SafeLog
import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.config.AppConfig
import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.interpreter.Info
import zio.blocking.Blocking
import zio.interop.catz._
import zio.system.System
import zio.{ App, UIO, ZIO }

final case class ProgramConfig(inputPath: String, outputPath: String)

object AppMain extends App {

  final case class AppEnv(
    log: Log.Service[Nothing, String],
    config: Config.Service,
    spark: Spark.Service
  ) extends SafeLog[String]
    with Config
    with Spark
    with Blocking.Live

  val appName = "LPTEMPLATESERVICENAME"
  override def run(args: List[String]): ZIO[zio.ZEnv, Nothing, Int] =
    for {
      logsvc <- Log.console[String](appName.some)
      log = logsvc.log

      pgm = for {
        config <- Config.make
        spark <- Spark.local(appName)
        _ <- Application.execute.provide(AppEnv(log, config.config, spark.spark))
      } yield ()

      exitCode <- pgm.foldM(
        e => log.error(s"Application failed: $e") *> ZIO.succeed(1),
        _ => log.info("Application terminated with no error indication") *> ZIO.succeed(0)
      )
    } yield exitCode
}

////

trait Config {
  def config: Config.Service
}

object Config {
  trait Service {
    def config: UIO[AppConfig]
  }

  def make: ZIO[System, AppError, Config] =
    AppConfig
      .load
      .map(
        cfg =>
          new Config {
            override def config: Service =
              new Service {
                override def config: UIO[AppConfig] =
                  ZIO.succeed(cfg)
              }
          }
      )
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
    def spark: UIO[SparkSession]
  }

  def make(session: => SparkSession): ZIO[Blocking, Throwable, Spark] =
    zio
      .blocking
      .effectBlocking(session)
      .map(
        sparkSession =>
          new Spark {
            override def spark: Service =
              new Service {
                override def spark: UIO[SparkSession] =
                  ZIO.succeed(sparkSession)
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
  val logSomething: ZIO[SafeLog[String] with Config, Nothing, Unit] =
    for {
      cfg <- ZIO.accessM[Config](_.config.config)
      log <- Log.stringLog
      _ <- log.info(s"Executing with parameters ${cfg.kafka} without sparkSession")
    } yield ()

  val runSparkJob: ZIO[SafeLog[String] with Spark with Blocking, Throwable, Unit] =
    for {
      session <- ZIO.accessM[Spark](_.spark.spark)
      result <- zio.blocking.effectBlocking(session.slowOp("SELECT something"))
      log <- Log.stringLog
      _ <- log.info(s"Executed something with spark ${session.version}: $result")
    } yield ()

  val processData: ZIO[SafeLog[String] with Spark with Config, Throwable, Unit] =
    for {
      cfg <- ZIO.accessM[Config](_.config.config)
      spark <- ZIO.accessM[Spark](_.spark.spark)
      log <- Log.stringLog
      _ <- log.info(s"Executing ${cfg.kafka} using ${spark.version}")
    } yield ()

  val execute: ZIO[SafeLog[String] with Spark with Config with Blocking, AppError, Unit] =
    for {
      log <- Log.stringLog
      cfg <- ZIO.accessM[Config](_.config.config)
      info <- Info.of[UIO, AppConfig](cfg, log)
      _ <- info.logEnvironment
      _ <- runSparkJob.mapError(AppError.exception)
      _ <- processData.mapError(AppError.exception)
    } yield ()
}
