package com.lptemplatecompany.lptemplatedivision.shared

import com.lptemplatecompany.lptemplatedivision.shared.testsupport.BaseSpec
import zio.test.Assertion._
import zio.test._

object AppsTest
  extends BaseSpec(
    suite("AppsTest")(
      test("className") {
        assert(Apps.className(Apps), equalTo("Apps"))
      }
    )
  )
