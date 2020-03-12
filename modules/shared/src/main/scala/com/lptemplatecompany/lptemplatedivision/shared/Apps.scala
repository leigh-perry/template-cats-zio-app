package com.lptemplatecompany.lptemplatedivision.shared

import java.io.{ PrintWriter, StringWriter }

import zio.blocking.Blocking
import zio.{ IO, ZIO }

object Apps {
  private val blocker: IO[Nothing, Blocking] =
    ZIO.environment.provideLayer(zio.blocking.Blocking.live)

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
