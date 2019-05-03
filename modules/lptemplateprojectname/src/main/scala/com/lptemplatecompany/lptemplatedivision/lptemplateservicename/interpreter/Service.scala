package com.lptemplatecompany.lptemplatedivision
package lptemplateservicename
package interpreter

import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.algebra.ServiceAlg
import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.config.Config
import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.syntax.IOSyntax
import com.lptemplatecompany.lptemplatedivision.shared.Apps
import com.lptemplatecompany.lptemplatedivision.shared.log4zio.Logger
import scalaz.zio.clock.Clock
import scalaz.zio.duration.Duration
import scalaz.zio.interop.catz._
import scalaz.zio.{IO, Managed, ZManaged}

/**
  * The real-infrastructure implementation for the top level service
  */
class Service private(cfg: Config, log: Logger[AIO], tempDir: String)
  extends ServiceAlg[AIO] {

  import scala.concurrent.duration.DurationInt

  override def run: AIO[Unit] =
    log.info(s"Starting in $tempDir") *>
      Clock.Live.clock.sleep(Duration.fromScala(2.seconds)) <*
      log.info(s"Finishing in $tempDir")

}

object Service {
  def resource(cfg: Config, log: Logger[AIO]): ZManaged[AppEnv, AppError, ServiceAlg[AIO]] =
    for {
      tempDir <- FileSystem.tempDirectoryScope(log)
      svc <- Managed.fromEffect(AIO(new Service(cfg, log, tempDir): ServiceAlg[AIO]))
    } yield svc
}

//// sample code only

import java.io.File
import java.nio.file.{Files, Path}
import java.util.UUID

import cats.syntax.monadError._

object FileSystem
  extends IOSyntax {

  def tempDirectoryScope(log: Logger[AIO]): ZManaged[AppEnv, AppError, String] =
    Apps.managed(
      for {
        file <- FileSystem.createTempDir
        _ <- log.info(s"Created temp directory $file")
      } yield file
    )(
      dir =>
        FileSystem.deleteFileOrDirectory(dir) *>
          log.info(s"Removed temp directory $dir")
    )

  def tempFilename(extension: Option[String]): AIO[String] =
    UUID.randomUUID.toString
      .failWithMsg("UUID.randomUUID failed")
      .map(name => extension.fold(name)(ext => s"$name.$ext"))

  def baseTempDir: AIO[String] =
    System.getProperty("java.io.tmpdir")
      .failWithMsg("Could not get tmpdir")

  def deleteFileOrDirectory(filepath: String): AIO[Unit] =
    delete(new File(filepath))
      .failWithMsg(s"Could not delete $filepath")
      .ensure(AppError.DirectoryDeleteFailed(filepath))(identity)
      .unit

  def createTempDir[A]: AIO[String] =
    for {
      base <- baseTempDir
      parent <- IO.succeed(Path.of(base))
      tempPath <- Files.createTempDirectory(parent, "workspace").failWithMsg("Could not create temp dir")
      tempDir <- tempPath.toFile.getAbsolutePath.failWithMsg(s"Could not resolve $tempPath")
    } yield tempDir

  //// internal impure code

  private def delete(file: File): Boolean = {
    if (file.isDirectory) {
      // Delete the contents of the directory first
      val children = file.list
      for (element <- children) {
        delete(new File(file, element))
      }
    }
    file.delete
  }

}
