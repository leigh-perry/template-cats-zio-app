package com.lptemplatecompany.lptemplatedivision.lptemplateservicename.interpreter

import cats.Monad
import cats.instances.list._
import cats.instances.order._
import cats.instances.string._
import cats.syntax.applicative._
import cats.syntax.traverse._
import com.leighperry.log4zio.Log
import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.BuildInfo
import com.lptemplatecompany.lptemplatedivision.shared.Apps
import com.lptemplatecompany.lptemplatedivision.shared.algebra.InfoAlg
import zio.UIO
import zio.interop.catz._

/**
 * The real-infrastructure implementation for logging of application information, typically at
 * application startup
 *
 * C = config class
 */
class Info[C](cfg: C, log: Log.Service[Nothing, String], valueTransform: ((String, String)) => String)
  extends InfoAlg[UIO] {

  import scala.collection.JavaConverters._

  override def systemProperties: UIO[Map[String, String]] =
    System.getProperties.asScala.toMap.pure[UIO]

  override def environmentVariables: UIO[Map[String, String]] =
    System.getenv.asScala.toMap.pure[UIO]

  override def logBanner: UIO[Unit] =
    log.info(banner)

  override def logMap(m: Map[String, String]): UIO[Unit] =
    m.toList
      .sortBy(_._1)
      .traverse(e => log.info(formatMapEntry(e)))
      .unit

  override def logConfig: UIO[Unit] =
    log.info(s"Configuration $cfg")

  override def logSeparator: UIO[Unit] =
    log.info(separator)

  override def logTitle(title: String): UIO[Unit] =
    log.info(title)

  private val banner =
    s"""${Apps.className(this)} process version: ${BuildInfo.version}
       |  scala-version         : ${BuildInfo.scalaVersion}
       |  sbt-version           : ${BuildInfo.sbtVersion}
       |  build-time            : ${BuildInfo.buildTime}
       |  git-commit            : ${BuildInfo.gitCommitIdentifier}
       |  gitCommitIdentifier   : ${BuildInfo.gitCommitIdentifier}
       |  gitHashShort          : ${BuildInfo.gitHashShort}
       |  gitBranch             : ${BuildInfo.gitBranch}
       |  gitCommitAuthor       : ${BuildInfo.gitCommitAuthor.replaceAll("/", " - ")}
       |  gitCommitDate         : ${BuildInfo.gitCommitDate}
       |  gitMessage            : ${Apps.loggable(BuildInfo.gitMessage.trim)}
       |  gitUncommittedChanges : ${BuildInfo.gitUncommittedChanges}
       |  library-dependencies  : ${BuildInfo.libraryDependencies}""".stripMargin

  private val separator =
    "================================================================================"

  private def formatMapEntry(e: (String, String)): String =
    s"${e._1}=${valueTransform((e._1, Apps.loggable(e._2)))}"
}

object Info {
  def of[F[_]: Monad, C](
    cfg: C,
    log: Log.Service[Nothing, String],
    valueTransform: ((String, String)) => String
  ): F[Info[C]] =
    new Info(cfg, log, valueTransform)
      .pure[F]

  def keyBasedObfuscation(prohibited: List[String]): ((String, String)) => String = {
    case (key, value) =>
      if (prohibited.exists(s => key.toLowerCase.contains(s.toLowerCase))) "********"
      else value
  }

}
