package net.ceedubs.ficus.readers

import com.typesafe.config.ConfigFactory
import net.ceedubs.ficus.Spec

class AnyValReadersSpec extends Spec with AnyValReaders { def is =
  "The Boolean value reader should" ^
    "read a boolean" ! readBoolean ^
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

  def readBoolean = check { b: Boolean =>
    val cfg = ConfigFactory.parseString(s"myValue = $b")
    booleanValueReader.read(cfg, "myValue") must beEqualTo(b)
  }

  def readInt = check { i: Int =>
    val cfg = ConfigFactory.parseString(s"myValue = $i")
    intValueReader.read(cfg, "myValue") must beEqualTo(i)
  }
  
  def readDoubleAsInt = check { d: Double =>
    (d >= Int.MinValue && d <= Int.MaxValue) ==> {
      val cfg = ConfigFactory.parseString(s"myValue = $d")
      intValueReader.read(cfg, "myValue") must beEqualTo(d.toInt)
    }
  }

  def readLong = check { l: Long =>
    val cfg = ConfigFactory.parseString(s"myValue = $l")
    longValueReader.read(cfg, "myValue") must beEqualTo(l)
  }
  
  def readIntAsLong = check { i: Int =>
    val cfg = ConfigFactory.parseString(s"myValue = $i")
    longValueReader.read(cfg, "myValue") must beEqualTo(i.toLong)
  }

  def readDouble = check { d: Double =>
    val cfg = ConfigFactory.parseString(s"myValue = $d")
    doubleValueReader.read(cfg, "myValue") must beEqualTo(d)
  }

 def readIntAsDouble = check { i: Int =>
    val cfg = ConfigFactory.parseString(s"myValue = $i")
    doubleValueReader.read(cfg, "myValue") must beEqualTo(i.toDouble)
  }
}
