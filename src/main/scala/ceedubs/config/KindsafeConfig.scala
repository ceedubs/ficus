package ceedubs.config

import com.typesafe.config.Config
import ceedubs.config.readers.{AllValueReaderInstances, ValueReader}

trait KindsafeConfig {
  def config: Config

  def getAs[A](path: String)(implicit grabber: ValueReader[A]): A = grabber.get(config, path)
}

case class SimpleKindsafeConfig(config: Config) extends KindsafeConfig

object KindsafeConfig extends AllValueReaderInstances {
  implicit def toKindsafeConfig(config: Config): KindsafeConfig = SimpleKindsafeConfig(config)
}
