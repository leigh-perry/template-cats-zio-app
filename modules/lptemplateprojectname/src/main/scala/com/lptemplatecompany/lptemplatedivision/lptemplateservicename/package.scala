package com.lptemplatecompany.lptemplatedivision

import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.config.appenv.AppEnv
import scalaz.zio.blocking.Blocking
import scalaz.zio.clock.Clock
import scalaz.zio.console.Console
import scalaz.zio.random.Random
import scalaz.zio.system.System
import scalaz.zio.{IO, ZIO}

package object lptemplateservicename {

  type AppEnvType =
    AppEnv
      with Clock with Console with System with Random with Blocking // DefaultRuntime.Environment

  type AIO[A] = ZIO[AppEnvType, AppError, A]

  object AIO {
    def apply[A](a: A): AIO[A] =
      IO.succeed(a)
  }

}
