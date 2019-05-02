package com.lptemplatecompany.lptemplatedivision.shared

import scalaz.zio.{IO, Task, UIO}

final class GenIOSyntaxSafeOps[E](op: => Unit) {
  def safely: UIO[Unit] =
    Task(op)
      .catchAll(e => IO.succeedLazy(println(s"PANIC: $e")))
}

trait ToGenIOSyntaxSafeOps {
  implicit def implToGenIOSyntaxSafeOps[E](op: => Unit): GenIOSyntaxSafeOps[E] =
    new GenIOSyntaxSafeOps[E](op)
}

////

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
  extends ToGenIOSyntaxSafeOps
  with ToGenIOSyntaxSafeOpsIO

object geniosyntaxinstances
  extends GenIOSyntax
