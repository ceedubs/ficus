package net.ceedubs.ficus
package readers

import com.typesafe.config.ConfigFactory
import FicusConfig._

class ArbitraryTypeReaderSpec extends Spec { def is =
  "A arbitrary type reader should" ^
    "instantiate with a single-param apply method" ! instantiateSingleParamApply ^
    "instantiate with no apply method but a single constructor with a single param" ! instantiateSingleParamConstructor ^
    "instantiate with a multi-param apply method" ! instantiateMultiParamApply ^
    "instantiate with no apply method but a single constructor with multiple params" ! instantiateMultiParamConstructor ^
    "instantiate with multiple apply methods if only one returns the correct type" ! multipleApply ^
    "use another implicit value reader for a field" ! withOptionField ^
    "fall back to a default value on an apply method" ! fallBackToApplyMethodDefaultValue ^
    "fall back to a default value on a constructor arg" ! fallBackToConstructorDefaultValue ^
    "ignore a default value on an apply method if a value is in config" ! ignoreApplyParamDefault ^
    "ignore a default value in a constructor if a value is in config" ! ignoreConstructorParamDefault

  import ArbitraryTypeReaderSpec._

  def instantiateSingleParamApply = {
    val cfg = ConfigFactory.parseString("""simple { foo2 = "foo" }""")
    val instance: WithSimpleCompanionApply = arbitraryTypeValueReader[WithSimpleCompanionApply].read(cfg, "simple")
    instance.foo must_== "foo"
  }

  def instantiateSingleParamConstructor = {
    val cfg = ConfigFactory.parseString("""singleParam { foo = "foo" }""")
    val instance: ClassWithSingleParam = arbitraryTypeValueReader[ClassWithSingleParam].read(cfg, "singleParam")
    instance.getFoo must_== "foo"
  }

  def instantiateMultiParamApply = {
    val cfg = ConfigFactory.parseString(
      """
        |multi {
        |  foo = "foo"
        |  bar = 3
        |}""".stripMargin)
    val instance: WithMultiCompanionApply = arbitraryTypeValueReader[WithMultiCompanionApply].read(cfg, "multi")
    (instance.foo must_== "foo") and (instance.bar must_== 3)
  }

  def instantiateMultiParamConstructor = {
    val cfg = ConfigFactory.parseString(
      """
        |multi {
        |  foo = "foo"
        |  bar = 3
        |}""".stripMargin)
    val instance: ClassWithMultipleParams = arbitraryTypeValueReader[ClassWithMultipleParams].read(cfg, "multi")
    (instance.foo must_== "foo") and (instance.bar must_== 3)
  }

  def multipleApply = {
    val cfg = ConfigFactory.parseString("""withMultipleApply { foo = "foo" }""")
    val instance: WithMultipleApplyMethods = arbitraryTypeValueReader[WithMultipleApplyMethods].read(cfg, "withMultipleApply")
    instance.foo must_== "foo"
  }

  def fallBackToApplyMethodDefaultValue = {
    val cfg = ConfigFactory.parseString("withDefault { }")
    arbitraryTypeValueReader[WithDefault].read(cfg, "withDefault").foo must_== "defaultFoo"
  }

  def fallBackToConstructorDefaultValue = {
    val cfg = ConfigFactory.parseString("withDefault { }")
    arbitraryTypeValueReader[ClassWithDefault].read(cfg, "withDefault").foo must_== "defaultFoo"
  }

  def withOptionField = {
    val cfg = ConfigFactory.parseString("""withOption { option = "here" }""")
    arbitraryTypeValueReader[WithOption].read(cfg, "withOption").option must_== Some("here")
  }

  def ignoreApplyParamDefault = {
    val cfg = ConfigFactory.parseString("""withDefault { foo = "notDefault" }""")
    arbitraryTypeValueReader[WithDefault].read(cfg, "withDefault").foo must_== "notDefault"
  }

  def ignoreConstructorParamDefault = {
    val cfg = ConfigFactory.parseString("""withDefault { foo = "notDefault" }""")
    arbitraryTypeValueReader[ClassWithDefault].read(cfg, "withDefault").foo must_== "notDefault"
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

  class ClassWithSingleParam(foo: String) {
    def getFoo = foo
  }

  class ClassWithMultipleParams(val foo: String, val bar: Int)

  class ClassWithDefault(val foo: String = "defaultFoo")

  trait WithMultipleApplyMethods {
    def foo: String
  }

  object WithMultipleApplyMethods {

    def apply(foo: Option[String]): Option[WithMultipleApplyMethods] = foo map { f =>
      new WithMultipleApplyMethods {
        def foo: String = f
      }
    }

    def apply(foo: String): WithMultipleApplyMethods = {
      val _foo = foo
      new WithMultipleApplyMethods {
        def foo: String = _foo
      }
    }
  }
}