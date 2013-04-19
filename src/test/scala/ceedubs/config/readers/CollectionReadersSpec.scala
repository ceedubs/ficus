package ceedubs.config.readers

import ceedubs.config.Spec
import com.typesafe.config.ConfigFactory

class CollectionReadersSpec extends Spec with CollectionReaders { def is =
  "The List value reader should" ^
    "read a list" ! readList ^
                             end ^
    "The Set value reader should" ^
    "read a Set" ! readSet

  def readList = {
    val cfg = ConfigFactory.parseString("myValue = [1, 2, 3]")
    listValueReader[Int].get(cfg, "myValue") must beEqualTo(List(1, 2, 3))
  }

  def readSet = {
    val cfg = ConfigFactory.parseString("myValue = [1, 2, 3]")
    aSetValueReader[Int].get(cfg, "myValue") must beEqualTo(Set(1, 2, 3))
  }
}
