package com.lptemplatecompany.lptemplatedivision.lptemplateservicename
package config

import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.algebra.ServiceAlg
import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.interpreter.Service
import io.chrisdavenport.log4cats.Logger
import scalaz.zio.Managed

/**
  * Top level application resources held in a Managed[...] so that proper cleanup happens
  * on program termination, whether clean or failure.
  */
final case class Context[F[_]] private(
  service: ServiceAlg[F]
)

object Context {
  def create(cfg: Config, log: Logger[AIO]): Managed[AppError, Context[AIO]] =
    for {
      service <- Service.resource(cfg, log)
    } yield new Context[AIO](service)
}
