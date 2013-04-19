package ceedubs.config.readers

import ceedubs.config.Spec
import com.typesafe.config.ConfigFactory

class OptionReadersSpec extends Spec with OptionReaders with AnyValReaders { def is =
  "An option value reader should" ^
    "wrap an existing value in a Some" ! optionSome ^
    "return a None for a non-existing value" ! optionNone

  def optionSome = {
    val cfg = ConfigFactory.parseString("myValue = true")
    optionValueReader[Boolean].get(cfg, "myValue") must beSome(true)
  }

  def optionNone = {
    val cfg = ConfigFactory.parseString("")
    optionValueReader[Boolean].get(cfg, "myValue") must beNone
  }
}
