package com.lptemplatecompany.lptemplatedivision.shared

import scalaz.zio.{IO, UIO}

final class GenIOSyntaxSafeOpsIO[E](io: IO[E, Unit]) {
  def safely: UIO[Unit] =
    io.catchAll(e => IO.succeedLazy(println(s"PANIC: $e")))
}

trait ToGenIOSyntaxSafeOpsIO {
  implicit def implToGenIOSyntaxSafeOpsIO[E](io: IO[E, Unit]): GenIOSyntaxSafeOpsIO[E] =
    new GenIOSyntaxSafeOpsIO[E](io)
}

////

trait GenIOSyntax
  extends ToGenIOSyntaxSafeOpsIO

object geniosyntaxinstances
  extends GenIOSyntax
