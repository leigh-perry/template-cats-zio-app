package com.lptemplatecompany.lptemplatedivision.lptemplateservicename

import com.lptemplatecompany.lptemplatedivision.shared.testsupport.BaseSpec
import zio.test.Assertion._
import zio.test.environment.TestRandom
import zio.test.{check, _}

object SomeSpec
  extends BaseSpec(
    suite("Sample tests")(
      testM("Sample property test") {
        TestRandom.feedInts(0, 1, -1, Int.MaxValue, Int.MaxValue - 1, Int.MinValue, Int.MinValue + 1) *>
          check(
            Gen
              .anyInt
              .filter(_ != Int.MinValue)
          ) {
            i =>
              assert(Math.abs(i), isGreaterThanEqualTo(0))
          }
      }
    )
  )
