package net.ceedubs.ficus

import com.typesafe.config.ConfigFactory
import FicusConfig.{ booleanValueReader, toFicusConfig }

class FicusConfigSpec extends Spec { def is =
  "A Ficus config should" ^
    "be implicitly converted from a Typesafe config" ! implicitlyConverted ^
    "accept a CongigKey and return the appropriate type" ! acceptAConfigKey

  def implicitlyConverted = {
    val cfg = ConfigFactory.parseString("myValue = true")
    cfg.as[Boolean]("myValue") must beTrue
  }

  def acceptAConfigKey = {
    val cfg = ConfigFactory.parseString("myValue = true")
    val key: ConfigKey[Boolean] = SimpleConfigKey("myValue")
    cfg(key) must beTrue
  }
}
