package com.lptemplatecompany.lptemplatedivision.shared

import scalaz.zio.{IO, Task, ZIO}

final class GenIOSyntaxSafeOps[R, E](op: => Unit) {
  def safely: ZIO[R, Nothing, Unit] =
    Task(op)
      .catchAll(e => ZIO.succeedLazy(println(s"PANIC: $e")))
}

trait ToGenIOSyntaxSafeOps {
  implicit def implToGenIOSyntaxSafeOps[R, E](op: => Unit): GenIOSyntaxSafeOps[R, E] =
    new GenIOSyntaxSafeOps[R, E](op)
}

////

final class GenIOSyntaxSafeOpsIO[R, E](io: ZIO[R, E, Unit]) {
  def safely: ZIO[R, Nothing, Unit] =
    io.catchAll(e => IO.succeedLazy(println(s"PANIC: $e")))
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
