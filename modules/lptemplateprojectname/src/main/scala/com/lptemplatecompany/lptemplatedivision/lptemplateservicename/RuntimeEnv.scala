package com.lptemplatecompany.lptemplatedivision.lptemplateservicename

import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.config.appenv.AppEnv
import scalaz.zio.blocking.Blocking
import scalaz.zio.clock.Clock
import scalaz.zio.console.Console
import scalaz.zio.random.Random
import scalaz.zio.system.System

trait RuntimeEnv
  extends AppEnv
    with Clock with Console with System with Random with Blocking // DefaultRuntime.Environment

