package com.lptemplatecompany.lptemplatedivision.lptemplateservicename
package interpreter

import cats.instances.list._
import cats.instances.order._
import cats.instances.string._
import cats.syntax.applicative._
import cats.syntax.traverse._
import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.config.appenv
import com.lptemplatecompany.lptemplatedivision.shared.Apps
import com.lptemplatecompany.lptemplatedivision.shared.algebra.InfoAlg
import scalaz.zio.interop.catz._

/**
  * The real-infrastructure implementation for logging of application information, typically at
  * application startup
  *
  * C = config class
  */
class Info
  extends InfoAlg[AIO] {

  import scala.collection.JavaConverters._

  override def systemProperties: AIO[Map[String, String]] =
    System.getProperties.asScala.toMap.pure[AIO]

  override def environmentVariables: AIO[Map[String, String]] =
    System.getenv.asScala.toMap.pure[AIO]

  override def logBanner: AIO[Unit] =
    for {
      log <- appenv.logger
      r <- log.info(banner)
    } yield r

  override def logMap(m: Map[String, String]): AIO[Unit] =
    for {
      log <- appenv.logger
      r <- m.toList
        .sortBy(_._1)
        .traverse(e => log.info(formatMapEntry(e)))
        .unit
    } yield r

  override def logConfig: AIO[Unit] =
    for {
      log <- appenv.logger
      cfg <- appenv.config
      r <- log.info(s"Configuration $cfg")
    } yield r

  override def logSeparator: AIO[Unit] =
    for {
      log <- appenv.logger
      r <- log.info(separator)
    } yield r

  override def logTitle(title: String): AIO[Unit] =
    for {
      log <- appenv.logger
      r <- log.info(title)
    } yield r

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
       |  library-dependencies  : ${BuildInfo.libraryDependencies}"""
      .stripMargin

  private val separator = "================================================================================"

  private def formatMapEntry(e: (String, String)): String =
    s"${e._1}=${Apps.loggable(e._2)}"
}

object Info {
  def of: AIO[Info] =
    AIO(new Info)
}
