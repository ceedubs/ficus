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
    "read a double as an int" ! readDoubleAsInt ^
                            end ^
  "The Long value reader should" ^
    "read a long" ! readLong ^
    "read an int as a long" ! readIntAsLong ^
                             end ^
  "The Double value reader should" ^
    "read a double" ! readDouble ^
    "read an int as a double" ! readIntAsDouble

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
  
  def readDoubleAsInt = {
    val cfg = ConfigFactory.parseString("myValue = 109.3")
    intValueReader.read(cfg, "myValue") must beEqualTo(109)
  } 

  def readLong = {
    val cfg = ConfigFactory.parseString("myValue = 4123098334081023948")
    longValueReader.read(cfg, "myValue") must beEqualTo(4123098334081023948L)
  }
  
  def readIntAsLong = {
    val cfg = ConfigFactory.parseString("myValue = 10")
    longValueReader.read(cfg, "myValue") must beEqualTo(10L)
  }

  def readDouble = {
    val cfg = ConfigFactory.parseString("myValue = 1.234")
    doubleValueReader.read(cfg, "myValue") must beEqualTo(1.234)
  }

 def readIntAsDouble = {
    val cfg = ConfigFactory.parseString("myValue = 42")
    doubleValueReader.read(cfg, "myValue") must beEqualTo(42.0)
  }
}
