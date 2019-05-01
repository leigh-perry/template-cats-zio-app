package com.lptemplatecompany.lptemplatedivision.lptemplateservicename

import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.syntax.IOSyntax
import scalaz.zio.{IO, Managed}

object IOApps
  extends IOSyntax {

  def managed[E, A](acquire: IO[E, A])(release: A => IO[E, Unit]): Managed[E, A] =
    Managed.make(acquire)(release(_).safely)
}
