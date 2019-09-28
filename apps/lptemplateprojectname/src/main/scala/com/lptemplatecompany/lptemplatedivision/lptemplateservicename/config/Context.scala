package com.lptemplatecompany.lptemplatedivision.lptemplateservicename
package config

import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.algebra.ServiceAlg
import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.interpreter.Service
import com.lptemplatecompany.lptemplatedivision.shared.log4zio.Logger
import zio.Managed

/**
 * Top level application resources held in a ZManaged[AppEnv, ...] so that proper cleanup happens
 * on program termination, whether clean or failure.
 */
final case class Context[F[_]] private (
  service: ServiceAlg[F]
)

object Context {
  def create(cfg: Config, log: Logger[AIO]): Managed[AppError, Context[AIO]] =
    for {
      service <- Service.managed(cfg, log)
    } yield new Context[AIO](service)
}
