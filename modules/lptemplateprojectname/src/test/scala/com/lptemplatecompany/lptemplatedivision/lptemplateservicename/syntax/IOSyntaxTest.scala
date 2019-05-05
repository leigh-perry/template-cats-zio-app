package com.lptemplatecompany.lptemplatedivision.lptemplateservicename.syntax

import cats.syntax.either._
import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.AppError
import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.AppError.DirectoryDeleteFailed
import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.stub.appenvTest
import com.lptemplatecompany.lptemplatedivision.shared.testsupport.TestSupport
import minitest.SimpleTestSuite
import minitest.laws.Checkers

object IOSyntaxTest
  extends SimpleTestSuite
    with IOSyntax
    with Checkers
    with TestSupport {

  test("exception catching as message") {
    check1 {
      v: String =>
        ((throw new RuntimeException(v)): Int).failWithMsg(s"message $v")
          .runSync(appenvTest.Test)
          .shouldSatisfy {
            case Left(List(AppError.ExceptionEncountered(s))) =>
              s.contains("RuntimeException") &&
                s.contains(s"message $v")
            case _ =>
              false
          }
    }
  }

  test("exception catching as AppError") {
    check1 {
      v: String =>
        ((throw new RuntimeException(v)): Int).failWith(DirectoryDeleteFailed(v))
          .runSync(appenvTest.Test)
          .shouldBe(List(DirectoryDeleteFailed(v)).asLeft)
    }
  }

  test("success path") {
    check1 {
      v: Int =>
        v.failWithMsg(s"message $v")
          .runSync(appenvTest.Test)
          .shouldBe(v.asRight)
    }
  }
}
