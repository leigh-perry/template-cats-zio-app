package com.lptemplatecompany.lptemplatedivision.shared.algebra

import cats.Monad
import cats.syntax.functor._
import cats.syntax.flatMap._


/**
  * Tagless final abstraction for logging of application information, typically at
  * application startup
  *
  * @tparam F the target effect
  */
trait InfoAlg[F[_]] {
  def systemProperties: F[Map[String, String]]
  def environmentVariables: F[Map[String, String]]
  def logBanner: F[Unit]
  def logSeparator: F[Unit]
  def logTitle(title: String): F[Unit]
  def logMap(m: Map[String, String]): F[Unit]
  def logConfig: F[Unit]

  ////

  def logContents(title: String, m: Map[String, String])(implicit F: Monad[F]): F[Unit] =
    for {
      _ <- logSeparator
      _ <- logTitle(title)
      _ <- logSeparator
      _ <- logMap(m)
    } yield ()

  def logEnvironment(implicit F: Monad[F]): F[Unit] =
    for {
      _ <- logBanner
      sps <- systemProperties
      evs <- environmentVariables
      _ <- logContents("System properties", sps)
      _ <- logContents("Environment variables", evs)
      _ <- logSeparator
    } yield ()

}
