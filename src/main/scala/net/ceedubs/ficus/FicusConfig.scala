package net.ceedubs.ficus

import com.typesafe.config.Config
import net.ceedubs.ficus.readers.{AllValueReaderInstances, ValueReader}

trait FicusConfig {
  def config: Config

  def as[A](path: String)(implicit reader: ValueReader[A]): A = reader.read(config, path)

  def apply[A](key: ConfigKey[A])(implicit reader: ValueReader[A]): A = as[A](key.path)
}

case class SimpleFicusConfig(config: Config) extends FicusConfig

object FicusConfig extends AllValueReaderInstances {
  implicit def toFicusConfig(config: Config): FicusConfig = SimpleFicusConfig(config)
}
