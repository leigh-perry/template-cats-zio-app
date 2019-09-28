package com.lptemplatecompany.lptemplatedivision.lptemplateservicename.algebra

/**
 * Tagless final abstraction for the top level service
 *
 * @tparam F the target effect
 */
trait ServiceAlg[F[_]] {
  def run: F[Unit]
}
