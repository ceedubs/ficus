package ceedubs.config
package readers

import com.typesafe.config.ConfigFactory

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
    "read a set of doubles" ! readDoubleSet

  def readStringList = {
    val cfg = ConfigFactory.parseString("""myValue = ["a", "b", "c"]""")
    castingListValueReader[String].get(cfg, "myValue") must beEqualTo(List("a", "b", "c"))
  }

  def readBooleanList = {
    val cfg = ConfigFactory.parseString("myValue = [true, false, true]")
    castingListValueReader[Boolean].get(cfg, "myValue") must beEqualTo(List(true, false, true))
  }

  def readIntList = {
    val cfg = ConfigFactory.parseString("myValue = [1, 2, 3]")
    castingListValueReader[Boolean].get(cfg, "myValue") must beEqualTo(List(1, 2, 3))
  }

  def readLongList = {
    val cfg = ConfigFactory.parseString("myValue = [2147483648, 2, 3]")
    castingListValueReader[Long].get(cfg, "myValue") must beEqualTo(List(2147483648L, 2, 3))
  }

  def readDoubleList = {
    val cfg = ConfigFactory.parseString("myValue = [0.1, 2.3, 3.4]")
    castingListValueReader[Long].get(cfg, "myValue") must beEqualTo(List(0.1, 2.3, 3.4))
  }

  def readStringSet = {
    val cfg = ConfigFactory.parseString("""myValue = ["a", "b", "c", "c"]""")
    castingSetValueReader[String].get(cfg, "myValue") must beEqualTo(Set("a", "b", "c"))
  }

  def readBooleanSet = {
    val cfg = ConfigFactory.parseString("myValue = [true, false, true]")
    castingSetValueReader[Boolean].get(cfg, "myValue") must beEqualTo(Set(true, false))
  }

  def readIntSet = {
    val cfg = ConfigFactory.parseString("myValue = [1, 2, 3, 3]")
    castingSetValueReader[Int].get(cfg, "myValue") must beEqualTo(Set(1, 2, 3))
  }

  def readLongSet = {
    val cfg = ConfigFactory.parseString("myValue = [2147483648, 2, 3]")
    castingSetValueReader[Long].get(cfg, "myValue") must beEqualTo(Set(2147483648L, 2, 3))
  }

  def readDoubleSet = {
    val cfg = ConfigFactory.parseString("myValue = [0.1, 2.3, 3.4]")
    castingSetValueReader[Long].get(cfg, "myValue") must beEqualTo(Set(0.1, 2.3, 3.4))
  }
}
