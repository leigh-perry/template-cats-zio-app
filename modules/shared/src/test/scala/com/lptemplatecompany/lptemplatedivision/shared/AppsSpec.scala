package com.lptemplatecompany.lptemplatedivision.shared

import com.lptemplatecompany.lptemplatedivision.shared.testsupport.BaseSpec
import zio.random
import zio.test.Assertion._
import zio.test._
import zio.test.environment.TestRandom

object AppsSpec
  extends BaseSpec(
    suite("AppsTest")(
      test("className") {
        assert(Apps.className(Apps), equalTo("Apps"))
      }
    )
  )
