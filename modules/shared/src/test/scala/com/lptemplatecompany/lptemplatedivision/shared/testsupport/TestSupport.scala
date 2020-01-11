package com.lptemplatecompany.lptemplatedivision.shared.testsupport

import zio.duration._
import zio.test.environment.TestEnvironment
import zio.test.{ DefaultRunnableSpec, TestAspect, ZSpec }

abstract class BaseSpec(spec: => ZSpec[TestEnvironment, Any, String, Any])
  extends DefaultRunnableSpec(spec, List(TestAspect.timeout(1.minute)))
