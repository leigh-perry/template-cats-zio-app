package com.lptemplatecompany.lptemplatedivision.lptemplateservicename
package config

import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.algebra.ServiceAlg
import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.interpreter.Service
import scalaz.zio.ZManaged

/**
  * Top level application resources held in a ZManaged[AppEnv, ...] so that proper cleanup happens
  * on program termination, whether clean or failure.
  */
// TODO how does this interact with zio.service idea?
final case class Context[F[_]] private(
  service: ServiceAlg[F]
)

object Context {
  def create: ZManaged[RuntimeEnv, AppError, Context[AIO]] =
    for {
      service <- Service.managed
    } yield new Context[AIO](service)
}
