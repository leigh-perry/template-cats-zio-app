package com.lptemplatecompany.lptemplatedivision.lptemplateservicename
package config

import com.lptemplatecompany.lptemplatedivision.shared.log4zio.Logger
import scalaz.zio.{UIO, ZIO}

object appenv {
  trait AppEnv {
    def appEnv: AppEnv.Service
  }

  object AppEnv {
    trait Service {
      def config: AIO[Config]
      def logger: AIO[Logger[AIO]]
    }
  }

  ////

  // shortcuts
  def config: AIO[Config] =
    ZIO.accessM(_.appEnv.config)

  def logger: AIO[Logger[AIO]] =
    ZIO.accessM(_.appEnv.logger)

  ////

  def service(cfg: Config, log: Logger[UIO]): AppEnv.Service =
    new AppEnv.Service {
      override def config: AIO[Config] =
        AIO(cfg)
      override def logger: AIO[Logger[AIO]] =
        AIO(log)
    }
}
