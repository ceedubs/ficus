# Ficus #
Ficus is a lightweight companion to Typesafe config that makes it more Scala-friendly.

Ficus adds an `as[A]` method to a normal [Typesafe Config](http://typesafehub.github.io/config/latest/api/com/typesafe/config/Config.html) so you can do things like `config.as[Option[Int]]`, `config.as[List[String]]`, or even `config.as[MyClass]`. It is implemented with type classes so that it is easily extensible and many silly mistakes can be caught by the compiler.

[![Build Status](https://secure.travis-ci.org/ceedubs/ficus.png?branch=master)](http://travis-ci.org/ceedubs/ficus)

# Examples #
```scala
import net.ceedubs.ficus.Ficus._

case class SomeCaseClass(foo: String, bar: Int, baz: Option[FiniteDuration])

class Examples {
  val config: Config = ConfigFactory.load() // standard Typesafe Config

  // Note: explicit typing isn't necessary. It's just in these examples to make it clear what the return types are.
  // This line could instead be: val appName = config.as[String]("app.name")
  val appName: String = config.as[String]("app.name") // equivalent to config.getString("app.name")

  // config.as[Option[Boolean]]("preloadCache") will return None if preloadCache isn't defined in the config
  val preloadCache: Boolean = config.as[Option[Boolean]]("preloadCache").getOrElse(false)

  val adminUserIds: Set[Long] = config.as[Set[Long]]("adminIds")

  // something such as "15 minutes" can be converted to a FiniteDuration
  val retryInterval: FiniteDuration = config.as[FiniteDuration]("retryInterval")

  // can hydrate most arbitrary types
  // it first tries to use an apply method on the companion object and falls back to the primary constructor
  // if values are not in the config, they will fall back to the default value on the class/apply method
  import net.ceedubs.ficus.readers.ArbitraryTypeReader._
  val someCaseClass: SomeCaseClass = config.as[SomeCaseClass]("someCaseClass")
}
```

For more detailed examples and how they match up with what's defined in a config file, see [the example spec](https://github.com/ceedubs/ficus/blob/master/src/test/scala/net/ceedubs/ficus/ExampleSpec.scala).

# Adding the dependency #
You most likely already have the Sonatype OSS Releases repository defined in your build, but if you don't, add this to your SBT build file (most likely build.sbt or project/build.scala):
```scala
resolvers ++= Seq(
  "Sonatype OSS Releases"  at "http://oss.sonatype.org/content/repositories/releases/",
)
```

Now add the Ficus dependency to your build SBT file as well:
```scala
// for Scala 2.10.x
libraryDependencies += "net.ceedubs" %% "ficus" % "1.0.1"

// for Scala 2.11.x
libraryDependencies += "net.ceedubs" %% "ficus" % "1.1.2"
```
If you want to take advantage of Ficus's ability to automatically hydrate arbitrary traits and classes from configuration, you need to be on Scala version 2.10.2 or higer, because this functionality depends on implicit macros.

Release notes are available on the [Ficus wiki](https://github.com/ceedubs/ficus/wiki).

# Built-in readers #
Out of the box, Ficus can read most types from config:
* Primitives (`Boolean`, `Int`, `Long`, `Double`)
* `String`
* `Option[A]`
* Collections (`List[A]`, `Set[A]`, `Map[String, A]`, `Array[A]`, etc. All types with a CanBuildFrom instance are supported)
* `Config` and `ConfigValue` (Typesafe config/value)
* `FiniteDuration`
* `BigDecimal` and `BigInt`
* Most arbitrary classes (as well as traits that have an apply method for instantiation). See [Arbitrary type support](#arbitrary-type-support)

In this context, `A` means any type for which a `ValueReader` is already defined. For example, `Option[String]` is supported out of the box because `String` is. If you want to be able to extract an `Option[Foo[A]]` for some some type `Foo` that doesn't meet the supported type requirements (for example, this `Foo` has a type parameter), the option part is taken care of, but you will need to provide the implementation for extracting a `Foo[A]` from config. See [Custom extraction](#custom-extraction).

# Imports #
The easiest way to start using Ficus config is to just `import net.ceedubs.ficus.Ficus._` as was done in the Examples section. This will import all of the implicit values you need to start easily grabbing most basic types out of config using the `as` method that will become available on Typesafe `Config` objects.

To enable Ficus's macro-based reading of case classes and other types, you can also import `net.ceedubs.ficus.readers.ArbitraryTypeReader._`. See [Arbitrary type support](#arbitrary-type-support)

If you would like to be more judicial about what you import (either to prevent namespace pollution or to potentially speed up compile times), you are free to specify which imports you need.

You will probably want to `import net.ceedubs.ficus.Ficus.toFicusConfig`, which will provide an implicit conversion from Typesafe `Config` to `FicusConfig`, giving you the `as` method.

You will then need a [ValueReader](https://github.com/ceedubs/ficus/blob/master/src/main/scala/net/ceedubs/ficus/readers/ValueReader.scala) for each type that you want to grab using `as`. You can choose whether you would like to get the reader via an import or a mixin Trait. For example, if you want to be able to call `as[String]`, you can either `import net.ceedubs.ficus.FicusConfig.stringValueReader` or you can add `with net.ceedubs.ficus.readers.StringReader` to your class definition.

If instead you want to be able to call `as[Option[String]]`, you would need to bring an implicit `ValueReader` for `Option` into scope (via `import net.ceedubs.ficus.FicusConfig.optionValueReader` for example), but then you would also need to bring the `String` value reader into scope as described above, since the `Option` value reader delegates through to the relevant value reader after checking that a config value exists at the given path.

_Don't worry_. It will be obvious if you forgot to bring the right value reader into scope, because the compiler will give you an error.

# Arbitrary type support #

## Supported types ##
* Traits or classes whose companion object has an appropriate apply method. This includes **case classes** (and even nested case classes).
    - The apply method must not take type parameters and its return type must match the trait or class
* Classes that have a primary constructor with no type parameters

If the apply method or constructor used has default arguments, Ficus will fall back to those for values not in the configuration.

If it exists, a valid apply method will be used instead of a constructor.

If Ficus doesn't know how to read an arbitrary type, it will provide a helpful **compile-time** error message explaining why. It won't risk guessing incorrectly.

Arbitrary type support requires Scala 2.10.2 or higher, because it takes advantage of implicit macros. To enable it, import `net.ceedubs.ficus.readers.ArbitraryTypeReader._`. Note that having the arbitrary type reader in scope can cause some implicit shadowing that you might not expect. If you define `MyClass` and define an `implicit val myClassReader: ValueReader[MyClass]` in the `MyClass` companion object, the arbitray type reader will still win the implicit prioritization battle unless you specifically `import MyClass.myClassReader`.

# Custom extraction #
When you call `as[String]("somePath")`, Ficus config knows how to extract a String because there is an implicit `ValueReader[String]` in scope. If you would like, you can even teach it how to extract a `Foo` from the config using `as[Foo]("fooPath")` if you create your own `ValueReader[Foo]`. You could pass this Foo extractor explicitly to the `as` method, but most likely you just want to make it implicit. For an example of a custom value reader, see the `ValueReader[ServiceConfig]` defined in [ExampleSpec](https://github.com/ceedubs/ficus/blob/master/src/test/scala/net/ceedubs/ficus/ExampleSpec.scala).

# Contributions #

Many thanks to all of [those who have contributed](https://github.com/ceedubs/ficus/blob/master/CONTRIBUTORS.md) to Ficus.

Would you like to contribute to Ficus? Pull requests are welcome and encouraged! Please note that contributions will be under the [MIT license](https://github.com/ceedubs/ficus/blob/master/LICENSE). Please provide unit tests along with code contributions.
