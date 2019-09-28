package com.lptemplatecompany.lptemplatedivision.lptemplateservicename
package syntax

import cats.syntax.either._
import com.lptemplatecompany.lptemplatedivision.lptemplateservicename.AppError.DirectoryDeleteFailed
import com.lptemplatecompany.lptemplatedivision.shared.testsupport.TestSupport
import com.lptemplatecompany.lptemplatedivision.shared.{ Apps, AppsTest }
import org.scalacheck.Prop.forAll
import org.scalacheck.Properties

object AIOSyntaxTest extends Properties("AppsTest") with TestSupport with AIOSyntax {
  property("Missing Configured[IO, List[Endpoint]]") = simpleTest {
    Apps.className(Apps).shouldBe("Apps") && Apps.className(AppsTest).shouldBe("AppsTest")
  }

  property("exception catching as message") = forAll(genFor[String]) {
    v: String =>
      ((throw new RuntimeException(v)): Int)
        .failWithMsg(s"message $v")
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

  property("exception catching as AppError") = forAll(genFor[String]) {
    v: String =>
      ((throw new RuntimeException(v)): Int)
        .failWith(DirectoryDeleteFailed(v))
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

  property("success path") = forAll(genFor[Int]) {
    v: Int =>
      v.failWithMsg(s"message $v")
        .runSync()
        .shouldBe(v.asRight)
  }
}
