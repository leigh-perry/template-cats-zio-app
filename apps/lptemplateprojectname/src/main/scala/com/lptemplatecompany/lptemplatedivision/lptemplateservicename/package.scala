package com.lptemplatecompany.lptemplatedivision

import zio.IO

package object lptemplateservicename {

  /** Application error type */
  type AIO[A] = IO[AppError, A]

  object AIO {
    def apply[A](a: A): AIO[A] =
      IO.succeed(a)
  }

}