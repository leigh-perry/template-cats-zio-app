package com.lptemplatecompany.lptemplatedivision.shared

import com.lptemplatecompany.lptemplatedivision.shared.testsupport.TestSupport
import org.scalacheck.Properties

object AppsTest extends Properties("AppsTest") with TestSupport {
  property("Missing Configured[IO, List[Endpoint]]") = simpleTest {
    Apps.className(Apps).shouldBe("Apps") && Apps.className(AppsTest).shouldBe("AppsTest")
  }

}
