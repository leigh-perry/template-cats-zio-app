package com.lptemplatecompany.lptemplatedivision.lptemplateservicename.syntax

import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.{AIO, AppError}
import scalaz.zio.{Task, ZIO}

object AIOSyntax {

  def asLeftAIO[A](e: AppError): AIO[A] =
    ZIO.fail(e)

  def asRightAIO[A](a: => A): AIO[A] =
    ZIO.succeed(a)
}

////

final class AIOSyntaxEitherOps[A](e: => Either[AppError, A]) {
  def asAIO: AIO[A] =
    ZIO.fromEither(e)
}

trait ToAIOSyntaxEitherOps {
  implicit def `Ops for AIOSyntax of IO`[A](e: => Either[AppError, A]): AIOSyntaxEitherOps[A] =
    new AIOSyntaxEitherOps[A](e)
}

////

final class AIOSyntaxAppErrorOps(val e: AppError) extends AnyVal {
  def asAIO[A]: AIO[A] =
    AIOSyntax.asLeftAIO[A](e)
}

trait ToAIOSyntaxAppErrorOps {
  implicit def `Ops for AIOSyntax of AppError`[A](e: AppError): AIOSyntaxAppErrorOps =
    new AIOSyntaxAppErrorOps(e)
}

////

final class AIOSyntaxAOps[A](a: => A) {
  def asAIO: AIO[A] =
    AIOSyntax.asRightAIO(a)
}

trait ToAIOSyntaxAOps {
  implicit def `Ops for AIOSyntax of A`[A](a: => A): AIOSyntaxAOps[A] =
    new AIOSyntaxAOps[A](a)
}

////

final class IOSyntaxSafeOps[A](a: => A) {
  def failWith(err: AppError): AIO[A] =
    Task(a)
      .mapError(_ => err)

  def failWithMsg(message: String): AIO[A] =
    Task(a)
      .mapError(AppError.exception(message, _))
}

trait ToIOSyntaxSafeOps {
  implicit def implToIOSyntaxSafeOps[A](a: => A): IOSyntaxSafeOps[A] =
    new IOSyntaxSafeOps[A](a)
}

////

final class IOSyntaxSafeOpsTask[A](t: Task[A]) {
  def asAIO: AIO[A] =
    t.mapError(AppError.exception(_))
}

trait ToIOSyntaxSafeOpsTask {
  implicit def implToIOSyntaxSafeOpsTask[A](t: Task[A]): IOSyntaxSafeOpsTask[A] =
    new IOSyntaxSafeOpsTask[A](t)
}

////

trait AIOSyntax
  extends ToAIOSyntaxEitherOps
    with ToAIOSyntaxAppErrorOps
    with ToAIOSyntaxAOps
    with ToIOSyntaxSafeOps
    with ToIOSyntaxSafeOpsTask

object aiosyntaxinstances
  extends AIOSyntax
