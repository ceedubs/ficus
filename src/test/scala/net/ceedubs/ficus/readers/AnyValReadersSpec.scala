package net.ceedubs.ficus.readers

import com.typesafe.config.ConfigFactory
import net.ceedubs.ficus.Spec

class AnyValReadersSpec extends Spec with AnyValReaders { def is =
  "The Boolean value reader should" ^
    "read a true" ! booleanReadTrue ^
    "read a false" ! booleanReadFalse ^
                                      end ^
  "The Int value reader should" ^
    "read an int" ! readInt ^
                            end ^
  "The Long value reader should" ^
    "read a long" ! readLong ^
                             end ^
  "The Double value reader should" ^
    "read a double" ! readDouble

  def booleanReadTrue = {
    val cfg = ConfigFactory.parseString("myValue = true")
    booleanValueReader.read(cfg, "myValue") must beTrue
  }

  def booleanReadFalse = {
    val cfg = ConfigFactory.parseString("myValue = false")
    booleanValueReader.read(cfg, "myValue") must beFalse
  }

  def readInt = {
    val cfg = ConfigFactory.parseString("myValue = 4")
    intValueReader.read(cfg, "myValue") must beEqualTo(4)
  }

  def readLong = {
    val cfg = ConfigFactory.parseString("myValue = 4123098334081023948")
    longValueReader.read(cfg, "myValue") must beEqualTo(4123098334081023948L)
  }

  def readDouble = {
    val cfg = ConfigFactory.parseString("myValue = 1.234")
    doubleValueReader.read(cfg, "myValue") must beEqualTo(1.234)
  }

}
