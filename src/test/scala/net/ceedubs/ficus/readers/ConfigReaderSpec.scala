package net.ceedubs.ficus
package readers

import com.typesafe.config.ConfigFactory

class ConfigReaderSpec extends Spec with ConfigReader with AnyValReaders { def is =
  "The Config value reader should" ^
    "read a config" ! readConfig

  def readConfig = prop { i: Int =>
    val cfg = ConfigFactory.parseString(
      s"""
        |myConfig {
        |  myValue = $i
        |}
      """.stripMargin)
    configValueReader.read(cfg, "myConfig").getInt("myValue") must beEqualTo(i)
  }
}
