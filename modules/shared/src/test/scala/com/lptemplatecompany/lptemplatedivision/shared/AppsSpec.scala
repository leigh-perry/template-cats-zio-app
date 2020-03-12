package com.lptemplatecompany.lptemplatedivision.shared

import zio.test.Assertion._
import zio.test._
import zio.test.environment.TestEnvironment

object AppsSpec extends DefaultRunnableSpec {
  def spec: Spec[TestEnvironment, TestFailure[Nothing], TestSuccess] =
    suite("Apps")(
      test("className") {
        assert(Apps.className(Apps), equalTo("Apps"))
      }
    )
}
