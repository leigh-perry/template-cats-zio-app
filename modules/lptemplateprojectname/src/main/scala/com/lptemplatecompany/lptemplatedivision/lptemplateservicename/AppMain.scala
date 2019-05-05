package com.lptemplatecompany.lptemplatedivision.lptemplateservicename

import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.config.appenv.AppEnv
import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.config.{Config, Context, appenv}
import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.syntax.IOSyntax
import com.lptemplatecompany.lptemplatedivision.shared.interpreter.Info
import com.lptemplatecompany.lptemplatedivision.shared.log4zio.Logger
import scalaz.zio.blocking.Blocking
import scalaz.zio.clock.Clock
import scalaz.zio.console.Console
import scalaz.zio.interop.catz._
import scalaz.zio.random.Random
import scalaz.zio.system.System
import scalaz.zio.{App, ZIO}

/** Overall environment for ZIO application */
trait RuntimeEnv
  extends AppEnv
    with Clock with Console with System with Random with Blocking

object RuntimeEnv {
  object Live
    extends RuntimeEnv
      with appenv.AppEnv.Live
      with Clock.Live
      with Console.Live
      with System.Live
      with Random.Live
      with Blocking.Live
}

////

/**
  * All resources, such as temporary directories and the expanded files, are cleaned up when no longer
  * required. This is implemented using `zio.Managed`.
  */
object AppMain
  extends App
    with IOSyntax {

  override def run(args: List[String]): ZIO[Environment, Nothing, Int] =
    program.provide(RuntimeEnv.Live)
      .fold(_ => 1, _ => 0)

  private def program: AIO[Unit] =
    for {
      cfg <- appenv.config
      log <- appenv.logger
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
      }.tapBoth(
      e => log.error(s"Application failed: $e"),
      _ => log.info("Application terminated with no error indication")
    )

}
