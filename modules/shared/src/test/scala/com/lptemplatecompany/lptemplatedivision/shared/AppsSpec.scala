package com.lptemplatecompany.lptemplatedivision.shared

import com.lptemplatecompany.lptemplatedivision.shared.testsupport.BaseSpec
import zio.test.Assertion._
import zio.test._

object AppsSpec
  extends BaseSpec(
    suite("Apps")(
      test("className") {
        assert(Apps.className(Apps), equalTo("Apps"))
      }
    )
  )
