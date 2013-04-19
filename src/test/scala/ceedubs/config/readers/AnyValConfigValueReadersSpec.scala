package ceedubs.config.readers

import com.typesafe.config.ConfigFactory
import ceedubs.config.Spec

class AnyValConfigValueReadersSpec extends Spec with AnyValConfigValueReaders { def is =
  "The Boolean value reader should" ^
    "read a true" ! booleanReadTrue ^
    "read a false" ! booleanReadFalse ^
                                      end ^
  "The Int value reader should" ^
    "read an int" ! readInt ^
                            end ^
  "The Long value reader should" ^
    "read a long" ! readLong

  def booleanReadTrue = {
    val cfg = ConfigFactory.parseString("myValue = true")
    BooleanValueReader.get(cfg, "myValue") must beTrue
  }

  def booleanReadFalse = {
    val cfg = ConfigFactory.parseString("myValue = false")
    BooleanValueReader.get(cfg, "myValue") must beFalse
  }

  def readInt = {
    val cfg = ConfigFactory.parseString("myValue = 4")
    IntValueReader.get(cfg, "myValue") must beEqualTo(4)
  }

  def readLong = {
    val cfg = ConfigFactory.parseString("myValue = 4123098334081023948")
    LongValueReader.get(cfg, "myValue") must beEqualTo(4123098334081023948L)
  }

}
