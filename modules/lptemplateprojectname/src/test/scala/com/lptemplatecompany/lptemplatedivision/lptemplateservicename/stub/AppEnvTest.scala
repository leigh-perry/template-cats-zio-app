package com.lptemplatecompany.lptemplatedivision.lptemplateservicename
package stub

import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.config.appenv.AppEnv
import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.config.{Config, RuntimeEnv}
import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.syntax.IOSyntax
import com.lptemplatecompany.lptemplatedivision.shared.log4zio.Logger
import scalaz.zio.clock.Clock
import scalaz.zio.interop.catz._
import scalaz.zio.{Task, UIO}

object appenvTest {

  trait Test
    extends AppEnv
      with IOSyntax {

    override val appEnv =
      new AppEnv.Service {
        override def config: UIO[Config] =
          UIO(Config.defaults)
        override def logger: AIO[Logger[UIO]] =
          Logger.slf4j[UIO, Task]
            .asAIO
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
