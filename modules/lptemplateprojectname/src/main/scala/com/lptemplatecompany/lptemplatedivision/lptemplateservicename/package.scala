package com.lptemplatecompany.lptemplatedivision

import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.config.RuntimeEnv
import scalaz.zio.{IO, ZIO}

package object lptemplateservicename {

  /** Application error type */
  type AIO[A] = IO[AppError, A]

  /** Application error type with enviroment dependency */
  type RAIO[A] = ZIO[RuntimeEnv, AppError, A]

  object RAIO {
    def apply[A](a: A): RAIO[A] =
      IO.succeed(a)
  }

}
