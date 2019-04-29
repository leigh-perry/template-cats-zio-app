package com.lptemplatecompany.lptemplatedivision.lptemplateservicename.syntax

import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.AppError
import scalaz.zio.Exit.{Failure, Success}
import scalaz.zio.{DefaultRuntime, FiberFailure, Task}

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
      //.toEither // TODO - this wraps in FiberFailure
    unsafeRunSync(io) match {
      case Success(value) => Right(value)
      case Failure(cause) => Left(cause.squash)
    }

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
