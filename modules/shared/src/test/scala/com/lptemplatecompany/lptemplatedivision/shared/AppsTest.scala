package com.lptemplatecompany.lptemplatedivision.shared

import com.lptemplatecompany.lptemplatedivision.shared.testsupport.TestSupport
import org.scalacheck.Properties

object AppsTest extends Properties("AppsTest") with TestSupport {
  property("className") = simpleTest {
    Apps.className(Apps).shouldBe("Apps") && Apps.className(AppsTest).shouldBe("AppsTest")
  }

}
