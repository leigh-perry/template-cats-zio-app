package com.lptemplatecompany.lptemplatedivision
package shared

import java.io.{PrintWriter, StringWriter}

import scalaz.zio.{IO, Managed}

object Apps
  extends GenIOSyntax {

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


  /** Handle the need for `release` action to be error-free (UIO) */
  def managed[E, A](acquire: IO[E, A])(release: A => IO[E, Unit]): Managed[E, A] =
    Managed.make(acquire)(release(_).safely)

}
