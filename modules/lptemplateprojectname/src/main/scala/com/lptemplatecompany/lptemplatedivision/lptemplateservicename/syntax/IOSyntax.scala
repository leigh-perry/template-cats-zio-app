package com.lptemplatecompany.lptemplatedivision.lptemplateservicename
package syntax

import scalaz.zio.{IO, Task, UIO}

final class IOSyntaxSafeOps[A](a: => A) {
  def failWith(err: AppError): AIO[A] =
    Task(a)
      .mapError(_ => err)

  def failWithMsg(message: String): AIO[A] =
    Task(a)
      .mapError(t => AppError.exception(message, t))
}

trait ToIOSyntaxSafeOps {
  implicit def implToIOSyntaxSafeOps[A](a: => A): IOSyntaxSafeOps[A] =
    new IOSyntaxSafeOps[A](a)
}

////

final class IOSyntaxSafeOpsTask[A](t: Task[A]) {
  def asIO: AIO[A] =
    t.mapError(e => AppError.exception(e))
}

trait ToIOSyntaxSafeOpsTask {
  implicit def implToIOSyntaxSafeOpsTask[A](t: Task[A]): IOSyntaxSafeOpsTask[A] =
    new IOSyntaxSafeOpsTask[A](t)
}

////

final class IOSyntaxSafeOpsUIO[A](u: UIO[A]) {
  def asIO: AIO[A] =
    u
}

trait ToIOSyntaxSafeOpsUIO {
  implicit def implToIOSyntaxSafeOpsUIO[A](u: UIO[A]): IOSyntaxSafeOpsUIO[A] =
    new IOSyntaxSafeOpsUIO[A](u)
}

////

final class IOSyntaxSafeOpsIO[E](io: IO[E, Unit]) {
  def safely: UIO[Unit] =
    io.catchAll(e => IO.succeedLazy(println(s"PANIC: $e")))
}

trait ToIOSyntaxSafeOpsIO {
  implicit def implToIOSyntaxSafeOpsIO[E](io: IO[E, Unit]): IOSyntaxSafeOpsIO[E] =
    new IOSyntaxSafeOpsIO[E](io)
}

////

trait IOSyntax
  extends ToIOSyntaxSafeOps
    with ToIOSyntaxSafeOpsTask
    with ToIOSyntaxSafeOpsIO

object aiosyntaxinstances
  extends IOSyntax
