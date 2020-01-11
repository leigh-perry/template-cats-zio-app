package com.lptemplatecompany.lptemplatedivision.lptemplateservicename

import com.lptemplatecompany.lptemplatedivision.shared.testsupport.BaseSpec
import zio.test.Assertion._
import zio.test.{ check, _ }

object AppsTest
  extends BaseSpec(
    suite("AppsTest")(
      testM("dummy property test") {
        check(
          Gen
            .anyInt
            .filter(_ != Int.MinValue)
        ) {
          i =>
            assert(Math.abs(i), isGreaterThan(0))
        }
      }
    )
  )
