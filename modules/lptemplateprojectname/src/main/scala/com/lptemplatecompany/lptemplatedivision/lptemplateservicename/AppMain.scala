package com.lptemplatecompany.lptemplatedivision.lptemplateservicename

import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.config.{Config, Context}
import com.lptemplatecompany.lptemplatedivision.shared.interpreter.Info
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import scalaz.zio.interop.catz._
import scalaz.zio.{App, Task, ZIO}

/**
  * All resources, such as temporary directories and the expanded files, are cleaned up when no longer
  * required. This is implemented using `cats.effect.Resource`.
  */
object AppMain
  extends App {

  private def getLogger: Task[Logger[Task]] =
    Slf4jLogger.create[Task]

  override def run(args: List[String]): ZIO[Environment, Nothing, Int] =
    Slf4jLogger.create[Task]
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
      .map(_.fold(e => 1, identity))


  private def program: Task[Unit] =
    for {
      log <- getLogger
      cfg <- Config.load
      info <- Info.of[Task, Config](cfg, log)
      _ <- info.logEnvironment
      _ <- log.info(cfg.toString)
      outcome <- runApp(cfg, log)
    } yield outcome

  private def runApp(cfg: Config, log: Logger[Task]): Task[Unit] =
    Context.create(cfg, log)
      .use {
        ctx =>
          ctx.service.run
      }

}
