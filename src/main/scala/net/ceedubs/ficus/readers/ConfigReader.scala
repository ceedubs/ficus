package net.ceedubs.ficus.readers

import com.typesafe.config.Config

trait ConfigReader {
  implicit val configValueReader: ValueReader[Config] = new ValueReader[Config] {
    def read(config: Config, path: String): Config = config.getConfig(path)
  }
}

object ConfigReader extends ConfigReader
