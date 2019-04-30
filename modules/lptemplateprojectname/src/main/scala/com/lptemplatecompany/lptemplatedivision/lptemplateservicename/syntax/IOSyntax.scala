package com.lptemplatecompany.lptemplatedivision.lptemplateservicename.syntax

import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.AppError
import scalaz.zio.{IO, Task}

final class IOSyntaxSafeOps[A](a: => A) {
  def failWith(err: AppError): Task[A] =
    Task(a)
      .mapError(_ => err)

  def failWithMsg(message: String): Task[A] =
    Task(a)
      .mapError(t => AppError.exception(message, t))
}

trait ToIOSyntaxSafeOps {
  implicit def implToIOSyntaxSafeOps[A](a: => A): IOSyntaxSafeOps[A] =
    new IOSyntaxSafeOps[A](a)
}

////

final class IOSyntaxSafeOpsTask[A](t: Task[A]) {
  def asIO: IO[AppError, A] =
    t.mapError(AppError.exception(_))
}

trait ToIOSyntaxSafeOpsTask {
  implicit def implToIOSyntaxSafeOpsTask[A](t: Task[A]): IOSyntaxSafeOpsTask[A] =
    new IOSyntaxSafeOpsTask[A](t)
}

////

trait IOSyntax
  extends ToIOSyntaxSafeOps
    with ToIOSyntaxSafeOpsTask

object aiosyntaxinstances
  extends IOSyntax
