package com.lptemplatecompany.lptemplatedivision.lptemplateservicename
package config

import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.config.appenv.AppEnv
import zio.clock.Clock

/** Overall environment for ZIO application */
trait RuntimeEnv
  extends AppEnv
    with Clock
// with Console
// with System
// with Random
// with Blocking

object RuntimeEnv {
  def live(service: AppEnv.Service): RuntimeEnv with Clock.Live =
    new RuntimeEnv
      with Clock.Live {
      override val appEnv: AppEnv.Service =
        service
    }
  // with Console.Live
  // with System.Live
  // with Random.Live
  // with Blocking.Live
}
