package com.lptemplatecompany.lptemplatedivision.lptemplateservicename.stub

import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.AIO
import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.config.Config
import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.config.appenv.AppEnv
import com.lptemplatecompany.lptemplatedivision.shared.log4zio.Logger

object appenvTest {

  trait Test extends AppEnv {
    override val appEnv =
      new AppEnv.Service {
        override def config: AIO[Config] =
          ???
        override def logger: AIO[Logger[AIO]] =
          ???
      }
  }
  object Test extends Test
}

