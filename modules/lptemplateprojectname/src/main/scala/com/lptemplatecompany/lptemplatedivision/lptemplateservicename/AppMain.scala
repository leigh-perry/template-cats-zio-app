package com.lptemplatecompany.lptemplatedivision.lptemplateservicename

import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.config.{Config, Context}
import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.syntax.IOSyntax
import com.lptemplatecompany.lptemplatedivision.shared.interpreter.Info
import com.lptemplatecompany.lptemplatedivision.shared.log4zio.Logger
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

  override def run(args: List[String]): ZIO[Environment, Nothing, Int] =
    getLogger
      .flatMap(
        log =>
          program
            .tapBoth(
              e => log.error(s"Application failed: $e"),
              _ => log.info("Application terminated with no error indication")
            )
            .fold(_ => 1, _ => 0)
      )
      .either
      .map(_.fold(_ => 1, identity))

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
