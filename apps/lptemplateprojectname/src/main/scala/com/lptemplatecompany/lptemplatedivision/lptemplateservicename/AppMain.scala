package com.lptemplatecompany.lptemplatedivision.lptemplateservicename

import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.config.{ Config, Context }
import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.interpreter.Info
import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.syntax.AIOSyntax
import com.lptemplatecompany.lptemplatedivision.shared.log4zio.Logger
import zio.interop.catz._
import zio.{ App, IO, Task, UIO, ZIO }

/**
 * All resources, such as temporary directories and the expanded files, are cleaned up when no longer
 * required. This is implemented using `zio.Managed`.
 */
object AppMain extends App with AIOSyntax {

  override def run(args: List[String]): ZIO[Environment, Nothing, Int] =
    program
      .fold(
        e => exitWith(1, s"Application failed: $e"),
        _ => exitWith(0, "Application terminated with no error indication")
      )

  /**
   * To prevent repeated evaluation of environmental dependencies, pre-compute them and
   * build services from these instances
   */
  private def program: AIO[Unit] =
    for {
      cfg <- Config.load
      log <- Logger.slf4j[UIO, Task].asAIO
      info <- Info.of[AIO, Config](cfg, log)
      _ <- info.logEnvironment
      _ <- log.info(cfg.toString)
      outcome <- runApp(cfg, log)
    } yield outcome

  private def runApp(cfg: Config, log: Logger[AIO]): IO[AppError, Unit] =
    for {
      ctx <- AIO(Context.create(cfg, log))
      _ <- ctx.use(_.service.run)
    } yield ()

  private def exitWith(code: Int, message: String) = {
    println(message)
    code
  }
}
