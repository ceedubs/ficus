package net.ceedubs.ficus

import com.typesafe.config.Config
import net.ceedubs.ficus.readers.{AllValueReaderInstances, ValueReader}

trait FicusConfig {
  def config: Config

  def getAs[A](path: String)(implicit reader: ValueReader[A]): A = reader.get(config, path)
}

case class SimpleFicusConfig(config: Config) extends FicusConfig

object FicusConfig extends AllValueReaderInstances {
  implicit def toFicusConfig(config: Config): FicusConfig = SimpleFicusConfig(config)
}
