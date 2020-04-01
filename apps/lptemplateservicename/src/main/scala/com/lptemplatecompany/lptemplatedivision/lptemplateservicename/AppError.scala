package com.lptemplatecompany.lptemplatedivision.lptemplateservicename

import com.lptemplatecompany.lptemplatedivision.shared.Apps
import zio.config.ReadError

sealed trait AppError

/** The ADT of error types for the application */
object AppError {
  final case class InvalidConfiguration(errors: ReadError[String]) extends AppError
  final case class ExceptionEncountered(message: String) extends AppError
  final case class DirectoryDeleteFailed(dir: String) extends AppError

  def exception(e: Throwable): AppError =
    ExceptionEncountered(s"Exception: ${Apps.stackTrace(e)}")

  def exception(message: String, e: Throwable): AppError =
    ExceptionEncountered(s"Exception $message: ${Apps.stackTrace(e)}")
}
