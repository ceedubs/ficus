package net.ceedubs.ficus
package readers

import com.typesafe.config.ConfigFactory
import org.scalacheck.Prop

class StringReaderSpec extends Spec with StringReader { def is =
  "The String value reader should" ^
    "read a String" ! readString

  def readString = Prop.forAll(jsonStringValue) { string: String =>
    val cfg = ConfigFactory.parseString("myValue = \"" + string + "\"")
    stringValueReader.read(cfg, "myValue") must beEqualTo(string)
  }
}
