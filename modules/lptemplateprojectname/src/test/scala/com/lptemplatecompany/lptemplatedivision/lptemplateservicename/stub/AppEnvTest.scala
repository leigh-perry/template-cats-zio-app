package com.lptemplatecompany.lptemplatedivision.lptemplateservicename.stub

import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.config.appenv.AppEnv
import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.config.{Config, RuntimeEnv}
import com.lptemplatecompany.lptemplatedivision.shared.log4zio.Logger
import scalaz.zio.UIO
import scalaz.zio.clock.Clock
import scalaz.zio.interop.catz._

object appenvTest {

  trait Test extends AppEnv {
    override val appEnv =
      new AppEnv.Service {
        override def config: UIO[Config] =
          UIO(Config.defaults)
        override def logger: UIO[Logger[UIO]] =
          Logger.slf4j[UIO]
      }
  }

  object Test
    extends RuntimeEnv
      with Test
      with Clock.Live
  //      with Console.Live
  //      with System.Live
  //      with Random.Live
  //      with Blocking.Live
}
