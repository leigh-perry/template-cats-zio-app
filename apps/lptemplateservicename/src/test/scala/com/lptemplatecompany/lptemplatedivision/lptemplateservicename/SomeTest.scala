package com.lptemplatecompany.lptemplatedivision.lptemplateservicename

import com.lptemplatecompany.lptemplatedivision.shared.testsupport.TestSupport
import org.scalacheck.Properties
import zio.ZIO

object SomeTest extends Properties("SomeTest") with TestSupport {
  property("abs") = forAllZIO(genFor[Int].filter(_ != Int.MinValue)) {
    i =>
      ZIO.succeed(Math.abs(i) >= 0)
  }

}
