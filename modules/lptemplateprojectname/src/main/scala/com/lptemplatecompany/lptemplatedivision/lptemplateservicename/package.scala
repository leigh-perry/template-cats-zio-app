package com.lptemplatecompany.lptemplatedivision

import scalaz.zio.IO

package object lptemplateservicename {

  type AIO[A] = IO[AppError, A]

  object AIO {
    def apply[A](a: A): AIO[A] =
      IO.succeed(a)
  }

}
