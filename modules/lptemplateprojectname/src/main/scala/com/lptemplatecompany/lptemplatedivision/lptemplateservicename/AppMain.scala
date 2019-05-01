package com.lptemplatecompany.lptemplatedivision.lptemplateservicename

import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.config.{Config, Context}
import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.syntax.IOSyntax
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
  extends App
    with IOSyntax {

  def loggerOf(l: Logger[Task]): Logger[AIO] =
    new Logger[AIO] {
      override def error(t: Throwable)(message: => String): AIO[Unit] = l.error(t)(message).asIO
      override def warn(t: Throwable)(message: => String): AIO[Unit] = l.warn(t)(message).asIO
      override def info(t: Throwable)(message: => String): AIO[Unit] = l.info(t)(message).asIO
      override def debug(t: Throwable)(message: => String): AIO[Unit] = l.debug(t)(message).asIO
      override def trace(t: Throwable)(message: => String): AIO[Unit] = l.trace(t)(message).asIO
      override def error(message: => String): AIO[Unit] = l.error(message).asIO
      override def warn(message: => String): AIO[Unit] = l.warn(message).asIO
      override def info(message: => String): AIO[Unit] = l.info(message).asIO
      override def debug(message: => String): AIO[Unit] = l.debug(message).asIO
      override def trace(message: => String): AIO[Unit] = l.trace(message).asIO
    }

  private def getLogger: Task[Logger[Task]] =
    Slf4jLogger.create[Task]

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
      logTask <- getLogger.asIO
      cfg <- Config.load
      log = loggerOf(logTask)
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
