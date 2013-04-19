package ceedubs.config.readers

import com.typesafe.config.Config

trait ConfigReader {
  implicit val ConfigValueReader: ValueReader[Config] = new ValueReader[Config] {
    def get(config: Config, path: String): Config = config.getConfig(path)
  }
}
