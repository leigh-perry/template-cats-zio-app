package com.lptemplatecompany.lptemplatedivision.lptemplateservicename
package syntax

import cats.syntax.either._
import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.AppError.DirectoryDeleteFailed
import com.lptemplatecompany.lptemplatedivision.shared.testsupport.TestSupport
import minitest.SimpleTestSuite
import minitest.laws.Checkers

object AIOSyntaxTest
  extends SimpleTestSuite
    with AIOSyntax
    with Checkers
    with TestSupport {

  test("exception catching as message") {
    check1 {
      v: String =>
        ((throw new RuntimeException(v)): Int).failWithMsg(s"message $v")
          .runSync()
          .shouldSatisfy(
            // New zio returns the exception twice in a list
            _.fold(
              _.headOption.fold(false) {
                case AppError.ExceptionEncountered(s) =>
                  s.contains("RuntimeException") &&
                    s.contains(s"message $v:")
                case _ =>
                  false
              },
              _ => false
            )
          )
    }
  }

  test("exception catching as AppError") {
    check1 {
      v: String =>
        ((throw new RuntimeException(v)): Int).failWith(DirectoryDeleteFailed(v))
          .runSync()
          .shouldSatisfy(
            // New zio returns the exception twice in a list
            _.fold(
              _.headOption.fold(false) {
                case AppError.DirectoryDeleteFailed(_) => true
                case _ => false
              },
              _ => false
            )
          )
    }
  }

  test("success path") {
    check1 {
      v: Int =>
        v.failWithMsg(s"message $v")
          .runSync()
          .shouldBe(v.asRight)
    }
  }

}