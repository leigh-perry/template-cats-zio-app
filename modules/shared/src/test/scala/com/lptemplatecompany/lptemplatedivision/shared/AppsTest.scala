package com.lptemplatecompany.lptemplatedivision.shared

import minitest.SimpleTestSuite
import minitest.laws.Checkers

object AppsTest extends SimpleTestSuite with Checkers {
  test("class name deduction") {
    assertEquals(Apps.className(Apps), "Apps")
    assertEquals(Apps.className(AppsTest), "AppsTest")
  }
}
