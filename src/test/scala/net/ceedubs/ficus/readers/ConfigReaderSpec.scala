package net.ceedubs.ficus
package readers

import com.typesafe.config.ConfigFactory

class ConfigReaderSpec extends Spec with ConfigReader with AnyValReaders { def is =
  "The Config value reader should" ^
    "read a config" ! readConfig

  def readConfig = {
    val cfg = ConfigFactory.parseString(
      """
        |myConfig {
        |  myValue = true
        |}
      """.stripMargin)
    configValueReader.get(cfg, "myConfig").getBoolean("myValue") must beTrue
  }
}
