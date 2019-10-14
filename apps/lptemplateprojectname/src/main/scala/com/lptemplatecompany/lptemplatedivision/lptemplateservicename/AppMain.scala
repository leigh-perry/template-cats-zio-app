package com.lptemplatecompany.lptemplatedivision.lptemplateservicename

import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.config.AppConfig
import com.lptemplatecompany.lptemplatedivision.shared.log4zio.Log
import zio.blocking.Blocking
import zio.console.Console
import zio.system.System
import zio.{ App, UIO, ZIO }

final case class ProgramConfig(inputPath: String, outputPath: String)

object AppMain extends App {

  final case class AppEnv(logging: Logging.Service, config: Config.Service, spark: Spark.Service)
    extends Logging
    with Config
    with Spark
    with Blocking.Live

  val appName = "LPTEMPLATESERVICENAME"
  override def run(args: List[String]): ZIO[Environment, Nothing, Int] =
    for {
      logging <- Logging.make(appName)
      log <- logging.logging.logging

      pgm = for {
        config <- Config.make
        spark <- Spark.local(appName)
        _ <- Application.execute.provide(AppEnv(logging.logging, config.config, spark.spark))
      } yield ()

      exitCode <- pgm.foldM(
        e => log.error(s"Application failed: $e") *> ZIO.succeed(1),
        _ => log.info("Application terminated with no error indication") *> ZIO.succeed(0)
      )
    } yield exitCode
}

////

trait Logging {
  def logging: Logging.Service
}

object Logging {
  trait Service {
    def logging: UIO[Log]
  }

  def make(prefix: String): ZIO[System, Nothing, Logging] =
    Log
      .slf4j(prefix)
      .map(
        cfg =>
          new Logging {
            override def logging: Service =
              new Service {
                override def logging: UIO[Log] =
                  ZIO.succeed(cfg)
              }
          }
      )
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
  val logSomething: ZIO[Logging with Config, Nothing, Unit] =
    for {
      cfg <- ZIO.accessM[Config](_.config.config)
      log <- ZIO.accessM[Logging](_.logging.logging)
      _ <- log.info(s"Executing with parameters ${cfg.kafka} without sparkSession")
    } yield ()

  val runSparkJob: ZIO[Logging with Spark with Blocking, Throwable, Unit] =
    for {
      session <- ZIO.accessM[Spark](_.spark.spark)
      result <- zio.blocking.effectBlocking(session.slowOp("SELECT something"))
      log <- ZIO.accessM[Logging](_.logging.logging)
      _ <- log.info(s"Executed something with spark ${session.version}: $result")
    } yield ()

  val processData: ZIO[Logging with Spark with Config, Throwable, Unit] =
    for {
      conf <- ZIO.accessM[Config](_.config.config)
      spark <- ZIO.accessM[Spark](_.spark.spark)
      log <- ZIO.accessM[Logging](_.logging.logging)
      _ <- log.info(s"Executing ${conf.kafka} using ${spark.version}")
    } yield ()

  // TODO remove Throwable
  val execute: ZIO[Logging with Spark with Config with Blocking, AppError, Unit] =
    for {
      _ <- logSomething.mapError(AppError.exception)
      _ <- runSparkJob.mapError(AppError.exception)
      _ <- processData.mapError(AppError.exception)
    } yield ()
}
