package com.lptemplatecompany.lptemplatedivision.lptemplateservicename

import cats.Show
import cats.data.NonEmptyChain
import cats.instances.string._
import cats.syntax.show._
import com.leighperry.conduction.config.ConfiguredError
import com.lptemplatecompany.lptemplatedivision.shared.Apps

sealed trait AppError extends Throwable {
  override def toString: String =
    this.show
}

/**
  * The ADT of error types for the application. IO requires a Throwable subclass.
  */
object AppError {
  final case class InvalidConfiguration(errors: NonEmptyChain[ConfiguredError]) extends AppError
  final case class ExceptionEncountered(message: String) extends AppError
  final case class DirectoryDeleteFailed(dir: String) extends AppError

  def exception(message: String, e: Throwable): AppError =
    ExceptionEncountered(s"Exception $message: ${Apps.stackTrace(e)}")

  implicit val showAppError: Show[AppError] =
    Show.show {
      (t: AppError) => {
        val extra: String =
          t match {
            case InvalidConfiguration(errors) => errors.show
            case ExceptionEncountered(message) => message.show
            case DirectoryDeleteFailed(dir) => dir.show
          }
        s"${Apps.className(t)}: $extra"
      }
    }
}
