package net.ceedubs.ficus
package readers

import com.typesafe.config.ConfigFactory

case class Foo(myValue: Boolean)

class CaseClassReadersSpec extends Spec with AnyValReaders { def is =
  "A case class reader should" ^
    "hydrate a case class" ! hydrateCaseClass

  def hydrateCaseClass = {
    val cfg = ConfigFactory.parseString("foo { myValue = true }")
    CaseClassReaderMacros.hydrateTheCaseClass[Foo](cfg, "foo") must_== Foo(true)
  }
}
