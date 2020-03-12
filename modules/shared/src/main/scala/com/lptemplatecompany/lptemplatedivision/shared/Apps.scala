package com.lptemplatecompany.lptemplatedivision.shared

import java.io.{ PrintWriter, StringWriter }

import zio.blocking.Blocking
import zio.{ Has, IO, ZIO, ZLayer }

object Apps {
  private val blocker: IO[Nothing, Blocking] =
    toIO(Blocking.live)

  def toIO[E, A <: Has[_]](layer: ZLayer[Any, E, A]): IO[E, A] =
    ZIO.environment.provideLayer[E, Any, A](layer)

  def block[E, A](io: IO[E, A]): IO[E, A] =
    blocker.flatMap {
      _.get.blocking(io)
    }

  def effectBlock[E, A](effect: => A): IO[Throwable, A] =
    blocker.flatMap {
      _.get.effectBlocking(effect)
    }

  def className(o: AnyRef): String =
    o.getClass.getSimpleName.replaceAll("\\$", "")

  def stackTrace(e: Throwable): String = {
    val sw = new StringWriter
    e.printStackTrace(new PrintWriter(sw))
    sw.toString
      .trim
      .replaceAll("\tat ", "    <- ")
      .replaceAll("\t", "    ")
  }

  def loggable(value: String): String =
    value
      .replaceAll("\n", """\\n""")
      .replaceAll("\r", """\\r""")
      .replaceAll("\t", """\\t""")

}
