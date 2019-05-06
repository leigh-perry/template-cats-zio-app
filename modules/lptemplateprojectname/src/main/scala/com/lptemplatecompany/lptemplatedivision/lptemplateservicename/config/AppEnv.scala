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
      def logger: AIO[Logger[UIO]]
    }
  }

  ////

  // shortcuts
  def config: RAIO[Config] =
    ZIO.accessM(_.appEnv.config)

  def logger: RAIO[Logger[UIO]] =
    ZIO.accessM(_.appEnv.logger)

  ////

  def service(cfg: Config, log: Logger[UIO]): AppEnv.Service =
    new AppEnv.Service {
      override def config: UIO[Config] =
        UIO(cfg)  // UIO since cannot fail
      override def logger: UIO[Logger[UIO]] =
        UIO(log)  // UIO since cannot fail
    }
}
