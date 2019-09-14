package com.lptemplatecompany.lptemplatedivision.shared

import zio.{IO, Task, ZIO}

final class GenIOSyntaxSafeOps[R, E](op: => Unit) {
  def safely: ZIO[R, Nothing, Unit] =
    Task(op)
      .catchAll[R, Nothing, Unit](e => ZIO.effectTotal(println(s"PANIC: $e")))
}

trait ToGenIOSyntaxSafeOps {
  implicit def implToGenIOSyntaxSafeOps[R, E](op: => Unit): GenIOSyntaxSafeOps[R, E] =
    new GenIOSyntaxSafeOps[R, E](op)
}

////

final class GenIOSyntaxSafeOpsIO[R, E](io: ZIO[R, E, Unit]) {
  def safely: ZIO[R, Nothing, Unit] =
    io.catchAll(e => IO.effectTotal(println(s"PANIC: $e")))
}

trait ToGenIOSyntaxSafeOpsIO {
  implicit def implToGenIOSyntaxSafeOpsIO[R, E](io: ZIO[R, E, Unit]): GenIOSyntaxSafeOpsIO[R, E] =
    new GenIOSyntaxSafeOpsIO[R, E](io)
}

////

trait GenIOSyntax
  extends ToGenIOSyntaxSafeOps
    with ToGenIOSyntaxSafeOpsIO

object geniosyntaxinstances
  extends GenIOSyntax
