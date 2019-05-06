package com.lptemplatecompany.lptemplatedivision.shared.log4zio

import cats.Applicative
import cats.effect.Sync
import cats.implicits._

trait Logger[+F[_]] {
  def error(message: => String): F[Unit]
  def warn(message: => String): F[Unit]
  def info(message: => String): F[Unit]
  def debug(message: => String): F[Unit]
}

object Logger {
  def slf4j[F[_] : Applicative, G[_] : Sync]: G[Logger[F]] =
    Sync[G].delay(org.slf4j.LoggerFactory.getLogger(getClass))
      .map {
        slf =>
          new Logger[F] {
            override def error(message: => String): F[Unit] =
              safely(slf.error(message), message)

            override def warn(message: => String): F[Unit] =
              safely(slf.warn(message), message)

            override def info(message: => String): F[Unit] =
              safely(slf.info(message), message)

            override def debug(message: => String): F[Unit] =
              safely(slf.debug(message), message)

            def safely(op: => Unit, message: String): F[Unit] =
              Either.catchNonFatal(op)
                .pure[F]
                .map(_.fold(e => println(s"PANIC: $e\nAttempted message: $message"), identity))
          }
      }
}
