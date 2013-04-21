package net.ceedubs.ficus
package readers

import com.typesafe.config.ConfigFactory

class StringReaderSpec extends Spec with StringReader { def is =
  "The String value reader should" ^
    "read a String" ! readString

  def readString = {
    val cfg = ConfigFactory.parseString("myValue = \"test\"")
    stringValueReader.get(cfg, "myValue") must beEqualTo("test")
  }
}
