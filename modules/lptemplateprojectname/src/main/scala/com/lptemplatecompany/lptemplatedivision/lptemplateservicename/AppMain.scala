package com.lptemplatecompany.lptemplatedivision.lptemplateservicename

import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.config.{Config, Context, RuntimeEnv, appenv}
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

/**
  * All resources, such as temporary directories and the expanded files, are cleaned up when no longer
  * required. This is implemented using `cats.effect.Resource`.
  */
object AppMain
  extends App
    with IOSyntax {

  //extends Console.Live with Logger.Live with KVStore.Live
  object RuntimeEnvLive
    extends RuntimeEnv
      with appenv.AppEnv.Live
      with Clock.Live
      with Console.Live
      with System.Live
      with Random.Live
      with Blocking.Live

  override def run(args: List[String]): ZIO[Environment, Nothing, Int] =
    program.provide(RuntimeEnvLive)
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
