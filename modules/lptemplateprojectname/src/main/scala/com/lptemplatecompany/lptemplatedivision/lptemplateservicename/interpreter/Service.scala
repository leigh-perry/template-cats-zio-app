package com.lptemplatecompany.lptemplatedivision
package lptemplateservicename
package interpreter

import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.algebra.ServiceAlg
import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.config.{RuntimeEnv, appenv}
import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.syntax.IOSyntax
import com.lptemplatecompany.lptemplatedivision.shared.Apps
import com.lptemplatecompany.lptemplatedivision.shared.log4zio.Logger
import scalaz.zio.duration.Duration
import scalaz.zio.interop.catz._
import scalaz.zio.{IO, Managed, UIO, ZManaged, clock}

/**
  * The real-infrastructure implementation for the top level service
  */
class Service private(tempDir: String)
  extends ServiceAlg[RAIO] {

  import scala.concurrent.duration.DurationInt

  override def run: RAIO[Unit] =
    for {
      log <- appenv.logger
      r <- log.info(s"Starting in $tempDir")
      _ <- clock.sleep(Duration.fromScala(2.seconds))
      _ <- log.info(s"Finishing in $tempDir")
    } yield r

}

object Service {
  def managed: ZManaged[RuntimeEnv, AppError, ServiceAlg[RAIO]] =
    for {
      log <- Managed.fromEffect(appenv.logger)
      tempDir <- FileSystem.tempDirectoryScope(log)
      svc <- Managed.fromEffect(RAIO(new Service(tempDir): ServiceAlg[RAIO]))
    } yield svc
}

//// sample code only

import java.io.File
import java.nio.file.{Files, Path}
import java.util.UUID

import cats.syntax.monadError._

object FileSystem
  extends IOSyntax {

  def tempDirectoryScope(log: Logger[UIO]): ZManaged[RuntimeEnv, AppError, String] =
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

  def tempFilename(extension: Option[String]): RAIO[String] =
    UUID.randomUUID.toString
      .failWithMsg("UUID.randomUUID failed")
      .map(name => extension.fold(name)(ext => s"$name.$ext"))

  def baseTempDir: RAIO[String] =
    System.getProperty("java.io.tmpdir")
      .failWithMsg("Could not get tmpdir")

  def deleteFileOrDirectory(filepath: String): RAIO[Unit] =
    delete(new File(filepath))
      .failWithMsg(s"Could not delete $filepath")
      .ensure(AppError.DirectoryDeleteFailed(filepath))(identity)
      .unit

  def createTempDir[A]: RAIO[String] =
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
