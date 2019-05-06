package com.lptemplatecompany.lptemplatedivision.shared.log4zio

import cats.syntax.applicative._
import cats.syntax.either._
import cats.syntax.flatMap._
import cats.syntax.functor._
import cats.{Applicative, Monad}

trait Logger[+F[_]] {
  def error(message: => String): F[Unit]
  def warn(message: => String): F[Unit]
  def info(message: => String): F[Unit]
  def debug(message: => String): F[Unit]
}

object Logger {
  def apply[F[_]](implicit ev: Logger[F]): Logger[F] = ev

  def slf4j[F[_] : Monad]: F[Logger[F]] =
    for {
      log <- Applicative[F].pure(org.slf4j.LoggerFactory.getLogger(getClass))
      logger <- Applicative[F].pure(
        new Logger[F] {
          override def error(message: => String): F[Unit] =
            safely(log.error(message), message)

          override def warn(message: => String): F[Unit] =
            safely(log.warn(message), message)

          override def info(message: => String): F[Unit] =
            safely(log.info(message), message)

          override def debug(message: => String): F[Unit] =
            safely(log.debug(message), message)

          def safely(op: => Unit, message: String): F[Unit] =
            Either.catchNonFatal(op)
              .pure[F]
              .map(_.fold(e => println(s"PANIC: $e\nAttempted message: $message"), identity))
        }
      )
    } yield logger
}
