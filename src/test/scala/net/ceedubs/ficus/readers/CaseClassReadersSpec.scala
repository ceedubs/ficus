package net.ceedubs.ficus
package readers

import com.typesafe.config.ConfigFactory
import net.ceedubs.ficus.FicusConfig._

object CaseClassReadersSpec {
  case class SimpleCaseClass(bool: Boolean)
  case class MultipleFields(string: String, long: Long)
  case class WithOption(option: Option[String])
  case class WithNestedCaseClass(simple: SimpleCaseClass)
  case class ValueClass(int: Int) extends AnyVal
  case class WithNestedValueClass(valueClass: ValueClass)
  case class WithDefault(string: String = "bar")
  case class Foo(bool: Boolean, intOpt: Option[Int], withNestedCaseClass: WithNestedCaseClass,
                 withNestedValueClass: WithNestedValueClass)
}

class CaseClassReadersSpec extends Spec { def is =
  "A case class reader should" ^
    "be able to be used implicitly" ! useImplicitly ^
    "hydrate a simple case class" ! hydrateSimpleCaseClass ^
    "hydrate a case class with multiple fields" ! multipleFields ^
    "use another implicit value reader for a field" ! withOptionField ^
    "read a nested case class" ! withNestedCaseClass ^
    "read a top-level value class" ! topLevelValueClass ^
    "read a nested value class" ! nestedValueClass ^
    "fall back to a default value" ! fallbackToDefault ^
    "do a combination of these things" ! combination

  import CaseClassReadersSpec._

  def useImplicitly = {
    val cfg = ConfigFactory.parseString("simple { bool = false }")
    cfg.as[SimpleCaseClass]("simple") must_== SimpleCaseClass(bool = false)
  }

  def hydrateSimpleCaseClass = {
    val cfg = ConfigFactory.parseString("simple { bool = true }")
    cfg.as[SimpleCaseClass]("simple") must_== SimpleCaseClass(bool = true)
  }

  def multipleFields = {
    val cfg = ConfigFactory.parseString(
      """
        |multipleFields {
        |  string = "foo"
        |  long = 42
        |}
      """.stripMargin)
    cfg.as[MultipleFields]("multipleFields") must_== MultipleFields(string = "foo", long = 42)
  }

  def withOptionField = {
    val cfg = ConfigFactory.parseString("""withOption { option = "here" }""")
    cfg.as[WithOption]("withOption") must_== WithOption(Some("here"))
  }

  def withNestedCaseClass = {
    val cfg = ConfigFactory.parseString(
      """
        |withNested {
        |  simple {
        |    bool = true
        |  }
        |}
      """.stripMargin)
    cfg.as[WithNestedCaseClass]("withNested") must_== WithNestedCaseClass(
      simple = SimpleCaseClass(bool = true))
  }

  def topLevelValueClass = {
    val cfg = ConfigFactory.parseString("valueClass { int = 3 }")
    cfg.as[ValueClass]("valueClass") must_== ValueClass(3)
  }

  def nestedValueClass = {
    val cfg = ConfigFactory.parseString(
      """
        |withNestedValueClass {
        |  valueClass {
        |    int = 5
        |  }
        |}
      """.stripMargin)
    cfg.as[WithNestedValueClass]("withNestedValueClass") must_== WithNestedValueClass(
      valueClass = ValueClass(int = 5))
  }

  def fallbackToDefault = {
    val cfg = ConfigFactory.parseString("""withDefault { }""")
    cfg.as[WithDefault]("withDefault") must_== WithDefault()
  }

  def combination = {
    val cfg = ConfigFactory.parseString(
      """
        |foo {
        |  bool = true
        |  withNestedCaseClass {
        |    simple {
        |      bool = false
        |    }
        |  }
        |  withNestedValueClass = {
        |    valueClass {
        |      int = 0
        |    }
        |  }
        |}
      """.stripMargin)
    cfg.as[Foo]("foo") must_== Foo(
      bool = true,
      intOpt = None,
      withNestedCaseClass = WithNestedCaseClass(simple = SimpleCaseClass(bool = false)),
      withNestedValueClass = WithNestedValueClass(ValueClass(int = 0))
    )
  }

}
