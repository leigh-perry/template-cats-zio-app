package com.lptemplatecompany.lptemplatedivision.lptemplateservicename

import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.config.appenv.AppEnv
import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.config.{Config, Context, RuntimeEnv, appenv}
import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.interpreter.Info
import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.syntax.IOSyntax
import com.lptemplatecompany.lptemplatedivision.shared.log4zio.Logger
import scalaz.zio.interop.catz._
import scalaz.zio.{App, IO, UIO, ZIO}

/**
  * All resources, such as temporary directories and the expanded files, are cleaned up when no longer
  * required. This is implemented using `zio.Managed`.
  */
object AppMain
  extends App
    with IOSyntax {

  override def run(args: List[String]): ZIO[Environment, Nothing, Int] =
    resolvedProgram
      .fold(_ => 1, _ => 0)

  private def resolvedProgram: IO[AppError, Unit] =
    for {
      cfg <- Config.load
      log <- Logger.slf4j[UIO]
      resolved <- program.provide(RuntimeEnv.live(appEnvService(cfg, log)))
    } yield resolved

  private def program: AIO[Unit] =
    for {
      cfg <- appenv.config
      log <- appenv.logger
      info <- Info.of
      _ <- info.logEnvironment
      _ <- log.info(cfg.toString)
      outcome <- runApp(log)
    } yield outcome

  private def runApp(log: Logger[AIO]): AIO[Unit] =
    Context.create
      .use {
        ctx =>
          ctx.service.run
      }.tapBoth(
      e => log.error(s"Application failed: $e"),
      _ => log.info("Application terminated with no error indication")
    )

  private def appEnvService(cfg: Config, log: Logger[UIO]): AppEnv.Service =
    new AppEnv.Service {
      override def config: AIO[Config] =
        AIO(cfg)
      override def logger: AIO[Logger[AIO]] =
        AIO(log)
    }

}
