package net.ceedubs.ficus
package readers

import com.typesafe.config.ConfigFactory
import FicusConfig._

class ArbitraryTypeReaderSpec extends Spec { def is =
  "A companion apply reader should" ^
    "instantiate an instance with a single-param apply method" ! instantiateSingleParam ^
    "instantiate an instance with a multi-param apply method" ! instantiateMultiParam ^
    "use another implicit value reader for a field" ! withOptionField ^
    "fall back to a default value" ! fallBackToADefaultValue ^
    "ignore a default value if a value is in config" ! ignoreDefault

  import ArbitraryTypeReaderSpec._

  def instantiateSingleParam = {
    val cfg = ConfigFactory.parseString("""simple { foo2 = "foo" }""")
    val instance: WithSimpleCompanionApply = arbitraryTypeValueReader[WithSimpleCompanionApply].read(cfg, "simple")
    instance.foo must_== "foo"
  }

  def instantiateMultiParam = {
    val cfg = ConfigFactory.parseString(
      """
        |multi {
        |  foo = "foo"
        |  bar = 3
        |}""".stripMargin)
    val instance: WithMultiCompanionApply = arbitraryTypeValueReader[WithMultiCompanionApply].read(cfg, "multi")
    (instance.foo must_== "foo") and (instance.bar must_== 3)
  }

  def fallBackToADefaultValue = {
    val cfg = ConfigFactory.parseString("withDefault { }")
    arbitraryTypeValueReader[WithDefault].read(cfg, "withDefault").foo must_== "defaultFoo"
  }

  def withOptionField = {
    val cfg = ConfigFactory.parseString("""withOption { option = "here" }""")
    arbitraryTypeValueReader[WithOption].read(cfg, "withOption").option must_== Some("here")
  }

  def ignoreDefault = {
    val cfg = ConfigFactory.parseString("""withDefault { foo = "notDefault" }""")
    arbitraryTypeValueReader[WithDefault].read(cfg, "withDefault").foo must_== "notDefault"
  }
}

object ArbitraryTypeReaderSpec {
  trait WithSimpleCompanionApply {
    def foo: String
  }

  object WithSimpleCompanionApply {
    def apply(foo2: String): WithSimpleCompanionApply = new WithSimpleCompanionApply {
      val foo = foo2
    }
  }

  trait WithMultiCompanionApply {
    def foo: String
    def bar: Int
  }

  object WithMultiCompanionApply {
    def apply(foo: String, bar: Int): WithMultiCompanionApply = {
      val (_foo, _bar) = (foo, bar)
      new WithMultiCompanionApply {
        val foo = _foo
        val bar = _bar
      }
    }
  }

  trait WithDefault {
    def foo: String
  }

  object WithDefault {
    def apply(foo: String = "defaultFoo"): WithDefault = {
      val _foo = foo
      new WithDefault {
        val foo = _foo
      }
    }
  }

  trait WithOption {
    def option: Option[String]
  }

  object WithOption {
    def apply(option: Option[String]): WithOption = {
      val _option = option
      new WithOption {
        val option = _option
      }
    }
  }
}