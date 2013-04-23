# Ficus #
Ficus is a lightweight companion to Typesafe config that makes it more Scala-friendly.

Ficus adds a `getAs[A]` method to a normal [Typesafe Config](http://typesafehub.github.io/config/latest/api/com/typesafe/config/Config.html) so you can do things like `config.getAs[Option[Int]]` or `config.getAs[List[String]]`. It is implemented with type classes so that it is easily extensible and many silly mistakes can be caught by the compiler.

[![Build Status](https://secure.travis-ci.org/ceedubs/ficus.png?branch=master)](http://travis-ci.org/ceedubs/ficus)

# Examples #
```scala
import com.typesafe.config.{Config, ConfigFactory}
import net.ceedubs.ficus.FicusConfig._

class Examples {
  val config: Config = ConfigFactory.load() // standard Typesafe Config

  // Note: explicit typing isn't necessary. It's just in these examples to make it clear what the return types are.
  // This line could instead be: val appName = config.getAs[String]("app.name")
  val appName: String = config.getAs[String]("app.name") // equivalent to config.getString("app.name")

  // config.getAs[Option[Boolean]]("preloadCache") will return None if preloadCache isn't defined in the config
  val preloadCache: Boolean = config.getAs[Option[Boolean]]("preloadCache").getOrElse(false)

  val adminUserIds: Set[Long] = config.getAs[Set[Long]]("adminIds")
}
```

For more detailed examples and how they match up with what's defined in a config file, see [the example spec](https://github.com/ceedubs/ficus/blob/master/src/test/scala/net/ceedubs/ficus/Examples.scala).

# Adding the dependency #
Add the following to your SBT build file (most likely build.sbt or project/build.scala):
```scala
libraryDependencies += "net.ceedubs" %% "ficus" % "0.1.0"
```
Currently Ficus is cross-built against Scala 2.10.x. If you would like it to be cross-built against an older version of Scala, create a GitHub issue, and I will most likely be able to do that.

# Imports #
The easiest way to start using Ficus config is to just `import net.ceedubs.ficus.FicusConfig._` as was done in the Examples section. This will import all of the implicit values you need to start easily grabbing basic types out of config using the `getAs` method that will become available on Typesafe `Config` objects.

If you would like to be more judicial about what you import (either to prevent namespace pollution or to potentially speed up compile times), you are free to specify which imports you need.

You will definitely need to `import net.ceedubs.ficus.FicusConfig.toFicusConfig`, which will provide an implicit conversion from Typesafe `Config` to `FicusConfig`, giving you the `getAs` method.

You will then need a [ValueReader](https://github.com/ceedubs/ficus/blob/master/src/main/scala/net/ceedubs/ficus/readers/ValueReader.scala) for each type that you want to grab using `getAs`. You can choose whether you would like to get the reader via an import or a mixin Trait. For example, if you want to be able to call `getAs[String]`, you can either `import net.ceedubs.ficus.FicusConfig.stringValueReader` or you can add `with net.ceedubs.ficus.readers.StringReader` to your class definition.

If instead you want to be able to call `getAs[Option[String]]`, you would need to bring an implicit `ValueReader` for `Option` into scope (via `import net.ceedubs.ficus.FicusConfig.optionValueReader` for example), but then you would also need to bring the `String` value reader into scope as described above, since the `Option` value reader delegates through to the relevant value reader after checking that a config value exists at the given path.

_Don't worry_. It will be obvious if you forgot to bring the right value reader into scope, because the compiler will give you an error.

# Extracting a custom type #
When you call `getAs[String]("somePath")`, Ficus config knows how to extract a String because there is an implicit `ValueReader[String]` in scope. If you would like, you can even teach it how to extract a `Foo` from the config using `getAs[Foo]("fooPath")` if you create your own `ValueReader[Foo]`. You could pass this Foo extractor explicitly to the `getAs` method, but most likely you just want to make it implicit. For an example of a custom value reader, see the `ValueReader[ServiceConfig]` defined in [ExampleSpec](https://github.com/ceedubs/ficus/blob/master/src/test/scala/net/ceedubs/ficus/ExampleSpec.scala#L44-L53).
