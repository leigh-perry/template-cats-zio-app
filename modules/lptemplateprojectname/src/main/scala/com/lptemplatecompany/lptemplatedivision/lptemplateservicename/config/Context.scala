package com.lptemplatecompany.lptemplatedivision.lptemplateservicename
package config

import scalaz.zio.Task
import scalaz.zio.interop.catz._
import cats.effect.Resource
import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.algebra.ServiceAlg
import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.interpreter.Service
import io.chrisdavenport.log4cats.Logger

/**
  * Top level application resources held in a Resource[...] so that proper cleanup happens
  * on program termination, whether clean or failure.
  */
final case class Context[F[_]] private(
  service: ServiceAlg[F]
)

object Context {
  def create(cfg: Config, log: Logger[Task]): Resource[Task, Context[Task]] =
    for {
      service <- Service.resource(cfg, log)
    } yield new Context[Task](service)
}
