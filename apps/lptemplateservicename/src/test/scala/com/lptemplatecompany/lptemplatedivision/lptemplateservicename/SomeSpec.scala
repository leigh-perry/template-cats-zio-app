package com.lptemplatecompany.lptemplatedivision.lptemplateservicename

import zio.test.Assertion._
import zio.test._
import zio.test.environment.{ TestEnvironment, TestRandom }

object SomeSpec extends DefaultRunnableSpec {
  def spec: Spec[TestEnvironment, TestFailure[Nothing], TestSuccess] =
    suite("Sample tests")(
      testM("Sample property test") {
        TestRandom.feedInts(0, 1, -1, Int.MaxValue, Int.MaxValue - 1, Int.MinValue, Int.MinValue + 1) *>
          check(
            Gen
              .anyInt
              .filter(_ != Int.MinValue)
          ) {
            i => assert(Math.abs(i))(isGreaterThanEqualTo(0))
          }
      }
    )
}
