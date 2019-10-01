package com.lptemplatecompany.lptemplatedivision.lptemplateservicename

import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.config.AppConfig
import com.lptemplatecompany.lptemplatedivision.shared.log4zio.Log
import zio.blocking.Blocking
import zio.{ App, ZIO }

//// Stubs

trait SparkSession {
  def sql(value: "select * from SOMETHING"): DataFrame =
    new DataFrame {}

  def version: String = 
    "dummy"

}
trait DataFrame

////

/**
 * Support for loading stuff the app needs in order to get started
 */
object Bootstrap {
  trait ConfigSupport {
    def load: ZIO[Any, AppError, AppConfig]
  }

  object ConfigSupport {
    trait Live extends ConfigSupport {
      override def load: ZIO[Any, AppError, AppConfig] =
        AppConfig.load
    }

    object Live extends Live
  }

  object TestConfiguration {
    trait Test extends ConfigSupport {
      override def load: ZIO[Any, AppError, AppConfig] =
        ZIO.succeed(
          AppConfig.defaults
        )
    }

    object Test extends Test
  }

  ////

  trait LogSupport {
    def load(prefix: String): ZIO[Any, Nothing, Log]
  }

  object LogSupport {
    trait Live extends LogSupport {
      override def load(prefix: String): ZIO[Any, Nothing, Log] =
        Log.slf4j(prefix)
    }

    object Live extends Live
  }

  object TestLogSupport {
    trait Test extends LogSupport {
      override def load(prefix: String): ZIO[Any, Nothing, Log] =
        Log.slf4j(prefix)
    }

    object Test extends Test
  }

  ////

  trait SparkSupport {
    def sparkSession(name: String): ZIO[Blocking, Throwable, SparkSession]
  }

  object SparkSupport {
    trait Live extends SparkSupport {
      override def sparkSession(name: String): ZIO[Blocking, Throwable, SparkSession] =
        ZIO.accessM {
          _.blocking
            .effectBlocking(
              //SparkSession.builder().appName(name).enableHiveSupport().getOrCreate()
              new SparkSession {}
            )
        }
    }

    object Live extends Live
  }

  object TestSparkSupport {
    trait Test extends SparkSupport {
      override def sparkSession(name: String): ZIO[Blocking, Throwable, SparkSession] =
        zio
          .blocking
          .effectBlocking(
            //SparkSession.builder().appName(name).master("local").getOrCreate()
            new SparkSession {}
          )
    }

    object Test extends Test
  }

}

////

/**
 * Stuff loaded in the bootstrap phase and is now available to the app while it is running
 */
object AppRuntime {

  trait Config {
    val cfg: AppConfig
  }

  trait Logging {
    val log: Log
  }

  trait Spark {
    val sparkSession: SparkSession
  }

  case class All(
    cfg: AppConfig,
    log: Log,
    sparkSession: SparkSession
  ) extends Config
    with Logging
    with Spark

}

////

object Main extends App {

  sealed trait MainAppError
  final case class ConfigLoadError(message: AppError) extends MainAppError
  final case class ExceptionEncountered(exception: Throwable) extends MainAppError

  override def run(args: List[String]): ZIO[Environment, Nothing, Int] =
    for {
      log <- Log.slf4j(prefix = "TESTING")
      r <- resolvedProgram(log).foldM(
        e => log.error(s"Application failed: $e") *> ZIO.succeed(1),
        _ => log.info("Application terminated with no error indication") *> ZIO.succeed(0)
      )
    } yield r

  final case class SomeResult()

  def resolvedProgram(log: Log): ZIO[Any, MainAppError, Unit] =
    for {
      // Config is loaded separately in order to provide to `program`
      cfg <- Bootstrap.ConfigSupport.Live.load.mapError(ConfigLoadError)
      sparkSession <- Bootstrap
        .SparkSupport
        .Live
        .sparkSession("TESTING")
        .mapError(ExceptionEncountered)
        .provide(Blocking.Live)
      resolved <- program.provide(
        new AppRuntime.All(cfg, log, sparkSession) with Blocking.Live {}
      )
    } yield resolved

  def program: ZIO[
    AppRuntime.Config with AppRuntime.Logging with AppRuntime.Spark with Blocking,
    MainAppError,
    Unit
  ] =
    ZIO.accessM {
      env =>
        for {
          r <- doSomethingBlocking(env.cfg)
          _ <- doSomethingSlow(env.cfg).mapError(ExceptionEncountered)
          _ <- env.log.info(env.sparkSession.version)
        } yield ()
    }

  def doSomethingSlow(cfg: AppConfig): ZIO[AppRuntime.Spark, Throwable, DataFrame] =
    ZIO.accessM {
      env =>
        ZIO.effect(env.sparkSession.sql("select * from SOMETHING"))
    }

  def doSomethingBlocking(cfg: AppConfig): ZIO[Blocking, MainAppError, SomeResult] =
    zio
      .blocking
      .effectBlocking {
        Thread.sleep(1000)
      }
      .bimap(ExceptionEncountered, _ => SomeResult())
}
