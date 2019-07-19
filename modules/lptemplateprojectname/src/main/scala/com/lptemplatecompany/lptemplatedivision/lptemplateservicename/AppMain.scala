package com.lptemplatecompany.lptemplatedivision.lptemplateservicename

import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.config.{Config, RuntimeEnv, appenv}
import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.interpreter.Info
import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.syntax.IOSyntax
import com.lptemplatecompany.lptemplatedivision.shared.log4zio.Logger
import scalaz.zio.interop.catz._
import scalaz.zio.{App, Task, UIO, ZIO}

/**
  * All resources, such as temporary directories and the expanded files, are cleaned up when no longer
  * required. This is implemented using `zio.Managed`.
  */
object AppMain
  extends App
    with IOSyntax {

  override def run(args: List[String]): ZIO[Environment, Nothing, Int] =
    resolvedProgram
      .fold(
        e => {
          println(s"Application failed: $e")
          1
        },
        _ => {
          println("Application terminated with no error indication")
          0
        }
      )

  /**
    * To prevent repeated evaluation of environmental dependencies, pre-compute them and
    * build services from these instances
    */
  private def resolvedProgram: AIO[Unit] =
    for {
      cfg <- Config.load
      log <- Logger.slf4j[UIO, Task].asAIO
      resolved <- program.provide(RuntimeEnv.live(appenv.service(cfg, log)))
    } yield resolved

  private def program: RAIO[Unit] =
    for {
      cfg <- appenv.config
      log <- appenv.logger
      info <- Info.of
      _ <- info.logEnvironment
      _ <- log.info(cfg.toString)
      outcome <- runApp(log)
    } yield outcome

  private def runApp(log: Logger[UIO]): RAIO[Unit] =
    for {
      ctx <- appenv.context
      _ <- ctx.use(_.service.run)
    } yield ()
}
