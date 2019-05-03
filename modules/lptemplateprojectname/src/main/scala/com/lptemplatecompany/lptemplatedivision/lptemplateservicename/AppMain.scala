package com.lptemplatecompany.lptemplatedivision.lptemplateservicename

import cats.instances.string._
import cats.syntax.either._
import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.config.{Config, Context}
import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.syntax.IOSyntax
import com.lptemplatecompany.lptemplatedivision.shared.interpreter.Info
import com.lptemplatecompany.lptemplatedivision.shared.log4zio.Logger
import scalaz.zio.console.Console
import scalaz.zio.interop.catz._
import scalaz.zio.{App, ZIO}

/**
  * All resources, such as temporary directories and the expanded files, are cleaned up when no longer
  * required. This is implemented using `cats.effect.Resource`.
  */
object AppMain
  extends App
    with IOSyntax {

  private def getLogger: AIO[Logger[AIO]] =
    Logger.slf4j[AIO]

  override def run(args: List[String]): ZIO[Environment, Nothing, Int] = {
    //    val v: ZIO[AppEnv, Nothing, Int] =
    //      getLogger
    //        .flatMap(
    //          log =>
    //            program
    //              .tapBoth(
    //                e => log.error(s"Application failed: $e"),
    //                _ => log.info("Application terminated with no error indication")
    //              )
    //              .fold(_ => 1, _ => 0)
    //        )
    //        .either
    //        .map(_.fold(_ => 1, identity))
    //    v
    val value: ZIO[Console with AppEnv, Nothing, Int] =
    program.foldM(
      err => console.putStrLn(s"Execution failed with: $err") *> ZIO.succeed(1),
      _ => ZIO.succeed(0)
    )
    value
    ??? // TODO
  }

  private def program: AIO[Unit] =
    for {
      log <- getLogger
      cfg <- Config.load
      info <- Info.of[AIO, Config](cfg, log)
      _ <- info.logEnvironment
      _ <- log.info(cfg.toString)
      outcome <- runApp(cfg, log)
    } yield outcome

  private def runApp(cfg: Config, log: Logger[AIO]): AIO[Unit] =
    Context.create(cfg, log)
      .use {
        ctx =>
          ctx.service.run
      }

}

/////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////

object AppEnv {
  trait Service {
    def config: AIO[Config]
  }
}

trait AppEnv {
  def env: AppEnv.Service
}

////

// Production capability
trait AppEnvLive extends AppEnv {
  override lazy val env: AppEnv.Service =
    new AppEnv.Service {
      override def config: AIO[Config] =
        ???
    }
}

object AppEnvLive extends AppEnvLive

// Helpers
object db {
  def config: ZIO[AppEnv, AppError, Config] =
    ZIO.accessM(_.env.config)
}

////

// Test capability
trait AppEnvTest extends AppEnv {
  override val env: AppEnv.Service =
    new AppEnv.Service {
      override def config: AIO[Config] =
        ???
    }
}

object AppEnvTest extends AppEnvTest

////

//println(testWriteRead(1).provide(AppEnvTest).runSync())

///////////////////////
///////////////////////
///////////////////////
///////////////////////
///////////////////////
///////////////////////

object Main extends App {

  override def run(args: List[String]): ZIO[Console, Nothing, Int] = {
    val pgm: ZIO[Any, AppError, String] =
      for {
        cfg <- ZIO.fromEither("pureconfig.loadConfig[Config]".asRight[AppError])

        //        program <- transactorR.use {
        //          transactor =>
        //            server.provideSome[Console] {
        //              base =>
        //                new Clock with Console with Blocking with DoobieTodoRepository {
        //                  override protected def xa: doobie.Transactor[Task] = transactor
        //
        //                  override val scheduler: Scheduler.Service[Any] = base.scheduler
        //                  override val console: Console.Service[Any] = base.console
        //                  override val clock: Clock.Service[Any] = base.clock
        //                  override val blocking: Blocking.Service[Any] = base.blocking
        //                }
        //            }
        //        }
      } yield cfg

    val value: ZIO[Console, Nothing, Int] =
      pgm.foldM(
        err => console.putStrLn(s"Execution failed with: $err") *> ZIO.succeed(1),
        _ => ZIO.succeed(0)
      )
    value
  }

}

////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////

import scalaz.zio.{App, IO, ZIO}
import scala.io.StdIn

//  "org.scalaz" %% "scalaz-zio" % "1.0-RC3"

object SimpleMain extends App {
  sealed trait AppError
  case object NoValue extends AppError

  def valueOf(key: String): ZIO[Map[String, String], AppError, String] =
    ZIO.accessM {
      env =>
        ZIO.fromEither(env.get(key).toRight(NoValue))
    }

  val program: ZIO[Map[String, String], AppError, Unit] =
    for {
      key <- IO.effectTotal(StdIn.readLine())
      v <- valueOf(key)
      _ <- IO.effectTotal(println(v))
    } yield ()

  def run(args: List[String]): ZIO[Environment, Nothing, Int] = {
    val value: ZIO[Any, Nothing, Int] =
      program.provide(Map("42" -> "Foo"))
        .fold(_ => 1, _ => 0)

    value
  }
}
