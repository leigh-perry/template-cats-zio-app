package com.lptemplatecompany.lptemplatedivision.lptemplateservicename.syntax

import cats.syntax.either._
import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.AppError
import scalaz.zio.{DefaultRuntime, Task}

final class IOSyntaxSafeOps[A](a: => A) {
  def failWith(err: AppError): Task[A] =
    Task(a)
      .mapError(_ => err)

  def failWithMsg(message: String): Task[A] =
    Task(a)
      .mapError(t => AppError.exception(message, t))
}

trait ToIOSyntaxSafeOps {
  implicit def ops[A](a: => A): IOSyntaxSafeOps[A] =
    new IOSyntaxSafeOps[A](a)
}

////

final class IOSyntaxSafeOpsTask[A](io: Task[A]) extends DefaultRuntime {
  def runSync(): Either[Throwable, A] =
    unsafeRunSync(io)
      .fold(_.squash.asLeft, _.asRight)

}

trait ToIOSyntaxSafeOpsTask {
  implicit def ops[A](io: Task[A]): IOSyntaxSafeOpsTask[A] =
    new IOSyntaxSafeOpsTask[A](io)
}

////

trait IOSyntax
  extends ToIOSyntaxSafeOps
    with ToIOSyntaxSafeOpsTask

object aiosyntaxinstances
  extends IOSyntax
