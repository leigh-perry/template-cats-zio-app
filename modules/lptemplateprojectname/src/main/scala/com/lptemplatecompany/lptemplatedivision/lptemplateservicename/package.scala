package com.lptemplatecompany.lptemplatedivision

import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.config.appenv.AppEnv
import scalaz.zio.{IO, ZIO}

package object lptemplateservicename {

  type AIO[A] = ZIO[AppEnv, AppError, A]

  object AIO {
    def apply[A](a: A): AIO[A] =
      IO.succeed(a)
  }

}
