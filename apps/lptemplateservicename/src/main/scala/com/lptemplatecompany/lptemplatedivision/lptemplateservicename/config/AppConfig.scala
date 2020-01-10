package com.lptemplatecompany.lptemplatedivision.lptemplateservicename.config

import zio.config.ConfigDescriptor.string
import zio.config._

/**
 * Overall application configuration
 */
final case class AppConfig(inputPath: String, outputPath: String)

object AppConfig {
  val descriptor: ConfigDescriptor[String, String, AppConfig] =
    (
      string("INPUT_PATH") |@|
        string("OUTPUT_PATH")
    )(AppConfig.apply, AppConfig.unapply)
}
