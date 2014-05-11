package net.ceedubs.ficus

import com.typesafe.config.ConfigFactory
import Ficus.{ booleanValueReader, optionValueReader, toFicusConfig }

class FicusConfigSpec extends Spec { def is = s2"""
  A Ficus config should
    be implicitly converted from a Typesafe config $implicitlyConverted
    read a value with a value reader $readAValue
    get an existing value as a Some $getAsSome
    get a missing value as a None $getAsNone
    accept a CongigKey and return the appropriate type $acceptAConfigKey
  """

  def implicitlyConverted = {
    val cfg = ConfigFactory.parseString("myValue = true")
    cfg.as[Boolean]("myValue") must beTrue
  }

  def readAValue = prop { b: Boolean =>
    val cfg = ConfigFactory.parseString(s"myValue = $b")
    cfg.as[Boolean]("myValue") must beEqualTo(b)
  }

  def getAsSome = prop { b: Boolean =>
    val cfg = ConfigFactory.parseString(s"myValue = $b")
    cfg.getAs[Boolean]("myValue") must beSome(b)
  }

  def getAsNone = {
    val cfg = ConfigFactory.parseString("myValue = true")
    cfg.getAs[Boolean]("nonValue") must beNone
  }

  def acceptAConfigKey = prop { b: Boolean =>
    val cfg = ConfigFactory.parseString(s"myValue = $b")
    val key: ConfigKey[Boolean] = SimpleConfigKey("myValue")
    cfg(key) must beEqualTo(b)
  }
}
