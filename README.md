# config #
This is a lightweight companion to Typesafe config that makes it more Scala-friendly.

It adds a `getAs[A]` method to `Config`, so you can do things like `config.getAs[Option[Int]]` or `config.getAs[List[String]]` (a Scala `List` instead of a silly mutable Java `List`!). Type classes are used so that many silly mistakes can be caught by the compiler, and they also make it easy for you to define your own types that can be extracted from config!

I have not yet decided on a name for this project, so I am playfully calling it Kindsafe Config.

## Build & run ##

```sh
$ cd config
$ chmod u+x sbt
$ ./sbt
> +run
```
