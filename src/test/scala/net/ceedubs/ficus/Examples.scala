package net.ceedubs.ficus

import com.typesafe.config.{Config, ConfigFactory}
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import scala.concurrent.duration.FiniteDuration

case class SomeCaseClass(foo: String, bar: Int, baz: Option[FiniteDuration])

class Examples {
  val config: Config = ConfigFactory.load() // standard Typesafe Config

  // Note: explicit typing isn't necessary. It's just in these examples to make it clear what the return types are.
  // This line could instead be: val appName = config.to[String]("app.name")
  val appName: String = config.to[String]("app.name") // equivalent to config.getString("app.name")

  // config.to[Option[Boolean]]("preloadCache") will return None if preloadCache isn't defined in the config
  val preloadCache: Boolean = config.to[Option[Boolean]]("preloadCache").getOrElse(false)

  val adminUserIds: Set[Long] = config.to[Set[Long]]("adminIds")

  // something such to "15 minutes" can be converted to a FiniteDuration
  val retryInterval: FiniteDuration = config.to[FiniteDuration]("retryInterval")

  // can hydrate most arbitrary types
  // it first tries to use an apply method on the companion object and falls back to the primary constructor
  // if values are not in the config, they will fall back to the default value on the class/apply method
  val someCaseClass: SomeCaseClass = config.to[SomeCaseClass]("someCaseClass")
}
