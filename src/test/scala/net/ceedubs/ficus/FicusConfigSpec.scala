package net.ceedubs.ficus

import com.typesafe.config.ConfigFactory
import FicusConfig.{ booleanValueReader, optionValueReader, toFicusConfig }

class FicusConfigSpec extends Spec { def is =
  "A Ficus config should" ^
    "be implicitly converted from a Typesafe config" ! implicitlyConverted ^
    "read a value with a value reader" ! readAValue ^
    "get a value as an option"
    "accept a CongigKey and return the appropriate type" ! acceptAConfigKey

  def implicitlyConverted = {
    val cfg = ConfigFactory.parseString("myValue = true")
    cfg.as[Boolean]("myValue") must beTrue
  }

  def readAValue = {
    val cfg = ConfigFactory.parseString("myValue = true")
    cfg.as[Boolean]("myValue") must beTrue
  }

  def getAsOption = {
    val cfg = ConfigFactory.parseString("myValue = true")
    (cfg.getAs[Boolean]("myValue") must beSome(true)) and (cfg.getAs[Boolean]("nonValue") must beNone)
  }

  def acceptAConfigKey = {
    val cfg = ConfigFactory.parseString("myValue = true")
    val key: ConfigKey[Boolean] = SimpleConfigKey("myValue")
    cfg(key) must beTrue
  }
}
