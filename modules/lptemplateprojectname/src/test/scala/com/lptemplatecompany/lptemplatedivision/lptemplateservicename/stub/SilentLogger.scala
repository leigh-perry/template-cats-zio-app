package com.lptemplatecompany.lptemplatedivision.lptemplateservicename.stub

import cats.effect.Sync
import cats.syntax.applicative._
import io.chrisdavenport.log4cats.Logger

object SilentLogger {

  def create[F[_] : Sync]: F[Logger[F]] =
    new Logger[F] {
      override def error(t: Throwable)(message: => String): F[Unit] =
        ().pure
      override def warn(t: Throwable)(message: => String): F[Unit] =
        ().pure
      override def info(t: Throwable)(message: => String): F[Unit] =
        ().pure
      override def debug(t: Throwable)(message: => String): F[Unit] =
        ().pure
      override def trace(t: Throwable)(message: => String): F[Unit] =
        ().pure
      override def error(message: => String): F[Unit] =
        ().pure
      override def warn(message: => String): F[Unit] =
        ().pure
      override def info(message: => String): F[Unit] =
        ().pure
      override def debug(message: => String): F[Unit] =
        ().pure
      override def trace(message: => String): F[Unit] =
        ().pure
    }.pure
}