package com.lptemplatecompany.lptemplatedivision.lptemplateservicename.stub

import cats.effect.Sync
import cats.syntax.applicative._
import com.lptemplatecompany.lptemplatedivision.shared.log4zio.Logger

object SilentLogger {

  def create[F[_] : Sync]: F[Logger[F]] =
    new Logger[F] {
      override def error(message: => String): F[Unit] =
        ().pure

      override def warn(message: => String): F[Unit] =
        ().pure

      override def info(message: => String): F[Unit] =
        ().pure

      override def debug(message: => String): F[Unit] =
        ().pure
    }.pure
}
