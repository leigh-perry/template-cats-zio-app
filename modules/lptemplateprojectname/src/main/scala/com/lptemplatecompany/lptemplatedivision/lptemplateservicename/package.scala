package com.lptemplatecompany.lptemplatedivision

import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.config.RuntimeEnv
import scalaz.zio.{IO, ZIO}

package object lptemplateservicename {

  type AIO[A] = ZIO[RuntimeEnv, AppError, A]

  object AIO {
    def apply[A](a: A): AIO[A] =
      IO.succeed(a)
  }

}
