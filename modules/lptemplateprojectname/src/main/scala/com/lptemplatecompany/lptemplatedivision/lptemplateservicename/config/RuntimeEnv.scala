package com.lptemplatecompany.lptemplatedivision.lptemplateservicename.config

import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.config.appenv.AppEnv
import scalaz.zio.clock.Clock

/** Overall environment for ZIO application */
trait RuntimeEnv
  extends AppEnv
    with Clock
// with Console
// with System
// with Random
// with Blocking

object RuntimeEnv {
  object Live
    extends RuntimeEnv
      with appenv.AppEnv.Live
      with Clock.Live
  // with Console.Live
  // with System.Live
  // with Random.Live
  // with Blocking.Live
}
