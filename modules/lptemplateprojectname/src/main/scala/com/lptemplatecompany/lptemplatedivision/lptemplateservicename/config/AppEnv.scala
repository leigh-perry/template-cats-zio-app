package com.lptemplatecompany.lptemplatedivision.lptemplateservicename
package config

import com.lptemplatecompany.lptemplatedivision.shared.log4zio.Logger
import zio.{UIO, ZIO, ZManaged}

object appenv {
  trait AppEnv {
    def appEnv: AppEnv.Service
  }

  object AppEnv {
    trait Service {
      def config: AIO[Config]
      def logger: AIO[Logger[UIO]]
      def context: AIO[ZManaged[RuntimeEnv, AppError, Context[ZIO[RuntimeEnv, AppError, *]]]]
    }
  }

  ////

  // shortcuts
  def config: ZIO[RuntimeEnv, AppError, Config] =
    ZIO.accessM(_.appEnv.config)

  def logger: ZIO[RuntimeEnv, AppError, Logger[UIO]] =
    ZIO.accessM(_.appEnv.logger)

  def context: ZIO[RuntimeEnv, AppError, ZManaged[RuntimeEnv, AppError, Context[ZIO[RuntimeEnv, AppError, *]]]] =
    ZIO.accessM(_.appEnv.context)

  ////

  def service(cfg: Config, log: Logger[UIO]): AppEnv.Service =
    new AppEnv.Service {
      override def config: UIO[Config] =
        UIO(cfg) // UIO since cannot fail
      override def logger: UIO[Logger[UIO]] =
        UIO(log) // UIO since cannot fail
      override def context: AIO[ZManaged[RuntimeEnv, AppError, Context[ZIO[RuntimeEnv, AppError, *]]]] =
        AIO(Context.create)
    }
}
