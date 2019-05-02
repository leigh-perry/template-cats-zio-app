package com.lptemplatecompany.lptemplatedivision.lptemplateservicename
package syntax

import scalaz.zio.{Task, UIO}

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

trait IOSyntax
  extends ToIOSyntaxSafeOps
    with ToIOSyntaxSafeOpsTask

object iosyntaxinstances
  extends IOSyntax
