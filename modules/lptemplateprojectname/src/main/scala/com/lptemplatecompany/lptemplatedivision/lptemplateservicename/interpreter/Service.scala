package com.lptemplatecompany.lptemplatedivision.lptemplateservicename.interpreter

import cats.effect.Resource
import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.AppError
import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.algebra.ServiceAlg
import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.config.Config
import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.syntax.IOSyntax
import io.chrisdavenport.log4cats.Logger
import scalaz.zio.Task
import scalaz.zio.clock.Clock
import scalaz.zio.duration.Duration
import scalaz.zio.interop.catz._

/**
  * The real-infrastructure implementation for the top level service
  */
class Service private(cfg: Config, log: Logger[Task], tempDir: String)
  extends ServiceAlg[Task] {

  import scala.concurrent.duration.DurationInt

  override def run: Task[Unit] =
    log.info(s"Starting in $tempDir") *>
      Clock.Live.clock.sleep(Duration.fromScala(10.seconds)) <*
      log.info(s"Finishing in $tempDir")

}

object Service {
  def resource(cfg: Config, log: Logger[Task]): Resource[Task, ServiceAlg[Task]] =
    for {
      tempDir <- FileSystem.tempDirectoryScope(log)
      svc <- Resource.liftF(Task(new Service(cfg, log, tempDir): ServiceAlg[Task]))
    } yield svc
}

//// sample code only

import java.io.File
import java.nio.file.{Files, Path}
import java.util.UUID

import cats.syntax.monadError._

object FileSystem
  extends IOSyntax {

  def tempDirectoryScope(log: Logger[Task]): Resource[Task, String] =
    Resource.make {
      for {
        file <- FileSystem.createTempDir
        _ <- log.info(s"Created temp directory $file")
      } yield file
    } {
      dir =>
        FileSystem.deleteFileOrDirectory(dir) *>
          log.info(s"Removed temp directory $dir")
    }

  def tempFilename(extension: Option[String]): Task[String] =
    UUID.randomUUID.toString
      .failWithMsg("UUID.randomUUID failed")
      .map(name => extension.fold(name)(ext => s"$name.$ext"))

  def baseTempDir: Task[String] =
    System.getProperty("java.io.tmpdir")
      .failWithMsg("Could not get tmpdir")

  def deleteFileOrDirectory(filepath: String): Task[Unit] =
    delete(new File(filepath))
      .failWithMsg(s"Could not delete $filepath")
      .ensure(AppError.DirectoryDeleteFailed(filepath))(identity)
      .unit

  def createTempDir[A]: Task[String] =
    for {
      base <- baseTempDir
      parent <- Task(Path.of(base))
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