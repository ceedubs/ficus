package net.ceedubs.ficus
package readers

import com.typesafe.config.ConfigFactory
import AnyValReaders.{booleanValueReader, doubleValueReader, intValueReader, longValueReader}
import StringReader.stringValueReader

class CollectionReadersSpec extends Spec with CollectionReaders { def is =
  "The list value reader should" ^
    "read a list of strings" ! readStringList ^
    "read a list of booleans" ! readBooleanList ^
    "read a list of ints" ! readIntList ^
    "read a list of longs" ! readLongList ^
    "read a list of doubles" ! readDoubleList ^
                             end ^
  "The set value reader should" ^
    "read a set of strings" ! readStringSet ^
    "read a set of booleans" ! readBooleanSet ^
    "read a set of ints" ! readIntSet ^
    "read a set of longs" ! readLongSet ^
    "read a set of doubles" ! readDoubleSet ^
                            end ^
  "The map value reader should" ^
    "read a string map" ! readStringStringMap ^
    "read an boolean map" ! readStringBooleanMap ^
    "read an int map" ! readStringIntMap ^
    "read a long map" ! readStringLongMap ^
    "read a double map" ! readStringDoubleMap

  def readStringList = {
    val cfg = ConfigFactory.parseString("""myValue = ["a", "b", "c"]""")
    delegatingListValueReader[String].get(cfg, "myValue") must beEqualTo(List("a", "b", "c"))
  }

  def readBooleanList = {
    val cfg = ConfigFactory.parseString("myValue = [true, false, true]")
    delegatingListValueReader[Boolean].get(cfg, "myValue") must beEqualTo(List(true, false, true))
  }

  def readIntList = {
    val cfg = ConfigFactory.parseString("myValue = [1, 2, 3]")
    delegatingListValueReader[Int].get(cfg, "myValue") must beEqualTo(List(1, 2, 3))
  }

  def readLongList = {
    val cfg = ConfigFactory.parseString("myValue = [2147483648, 2, 3]")
    delegatingListValueReader[Long].get(cfg, "myValue") must beEqualTo(List(2147483648L, 2, 3))
  }

  def readDoubleList = {
    val cfg = ConfigFactory.parseString("myValue = [0.1, 2.3, 3.4]")
    delegatingListValueReader[Double].get(cfg, "myValue") must beEqualTo(List(0.1, 2.3, 3.4))
  }

  def readStringSet = {
    val cfg = ConfigFactory.parseString("""myValue = ["a", "b", "c", "c"]""")
    delegatingSetValueReader[String].get(cfg, "myValue") must beEqualTo(Set("a", "b", "c"))
  }

  def readBooleanSet = {
    val cfg = ConfigFactory.parseString("myValue = [true, false, true]")
    delegatingSetValueReader[Boolean].get(cfg, "myValue") must beEqualTo(Set(true, false))
  }

  def readIntSet = {
    val cfg = ConfigFactory.parseString("myValue = [1, 2, 3, 3]")
    delegatingSetValueReader[Int].get(cfg, "myValue") must beEqualTo(Set(1, 2, 3))
  }

  def readLongSet = {
    val cfg = ConfigFactory.parseString("myValue = [2147483648, 2, 3]")
    delegatingSetValueReader[Long].get(cfg, "myValue") must beEqualTo(Set(2147483648L, 2, 3))
  }

  def readDoubleSet = {
    val cfg = ConfigFactory.parseString("myValue = [0.1, 2.3, 3.4]")
    delegatingSetValueReader[Double].get(cfg, "myValue") must beEqualTo(Set(0.1, 2.3, 3.4))
  }

  def readStringStringMap = {
    val cfg = ConfigFactory.parseString(
      """
        |myValue {
        |  item1 = "value1"
        |  item2 = "value2"
        |}
      """.stripMargin)
    delegatingMapValueReader[String].get(cfg, "myValue") must beEqualTo(Map("item1" -> "value1", "item2" -> "value2"))
  }

  def readStringBooleanMap = {
    val cfg = ConfigFactory.parseString(
      """
        |myValue {
        |  item1 = true
        |  item2 = false
        |}
      """.stripMargin)
    delegatingMapValueReader[Boolean].get(cfg, "myValue") must beEqualTo(Map("item1" -> true, "item2" -> false))
  }

  def readStringIntMap = {
    val cfg = ConfigFactory.parseString(
      """
        |myValue {
        |  item1 = 0
        |  item2 = 2
        |}
      """.stripMargin)
    delegatingMapValueReader[Int].get(cfg, "myValue") must beEqualTo(Map("item1" -> 0, "item2" -> 2))
  }

  def readStringLongMap = {
    val cfg = ConfigFactory.parseString(
      """
        |myValue {
        |  item1 = 0
        |  item2 = 2147483648
        |}
      """.stripMargin)
    delegatingMapValueReader[Long].get(cfg, "myValue") must beEqualTo(Map("item1" -> 0L, "item2" -> 2147483648L))
  }

  def readStringDoubleMap = {
    val cfg = ConfigFactory.parseString(
      """
        |context {
        |  myValue {
        |    item1 = 0.0
        |    item2 = 1.03
        |  }
        |}
      """.stripMargin)
    delegatingMapValueReader[Double].get(cfg, "context.myValue") must beEqualTo(Map("item1" -> 0.0, "item2" -> 1.03))
  }

}
