package net.ceedubs.ficus
package readers

import com.typesafe.config.ConfigFactory
import net.ceedubs.ficus.FicusConfig._

case class Foo(bool: Boolean, intOpt: Option[Int], bar: Bar, baz: Baz)
case class Bar(string: String) extends AnyVal
case class Baz(int: Int) extends AnyVal

class CaseClassReadersSpec extends Spec { def is =
  "A case class reader should" ^
    "hydrate a case class" ! hydrateCaseClass ^
    "be able to be used implicitly" ! useImplicitly

  val testConf = ConfigFactory.parseString(
    """
      |foo {
      |  bool = true
      |  intOpt = 3
      |  bar {
      |    string = "bar"
      |  }
      |  baz {
      |    int = 5
      |  }
      |}
      |
    """.stripMargin)

  val expected = Foo(bool = true, intOpt = Some(3), bar = Bar("bar"), baz = Baz(5))

  def hydrateCaseClass = {
    caseClassValueReader[Foo].read(testConf, "foo") must_== expected
  }

  def useImplicitly = {
    testConf.as[Foo]("foo") must_== expected
  }
}
