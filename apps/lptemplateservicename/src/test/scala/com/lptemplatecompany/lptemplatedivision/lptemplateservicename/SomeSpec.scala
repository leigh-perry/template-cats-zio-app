package com.lptemplatecompany.lptemplatedivision.lptemplateservicename

import com.lptemplatecompany.lptemplatedivision.shared.testsupport.BaseSpec
import zio.test.Assertion._
import zio.test.{ check, _ }

object SomeSpec
  extends BaseSpec(
    suite("Some tests or other")(
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
