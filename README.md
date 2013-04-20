# Ficus #
Ficus is a lightweight companion to Typesafe config that makes it more Scala-friendly.

Ficus adds a `getAs[A]` method to a normal [Typesafe Config](http://typesafehub.github.io/config/latest/api/com/typesafe/config/Config.html) so you can do things like `config.getAs[Option[Int]]` or `config.getAs[List[String]]`. It is implemented with type classes so that it is easily extensible and many silly mistakes can be caught by the compiler.

[![Build Status](https://secure.travis-ci.org/ceedubs/ficus.png?branch=master)](http://travis-ci.org/ceedubs/ficus)

# Examples #
For now, see [the example spec](https://github.com/ceedubs/ficus/blob/master/src/test/scala/ceedubs/ficus/Examples.scala)
