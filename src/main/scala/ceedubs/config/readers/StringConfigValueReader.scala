package ceedubs.config.readers

import com.typesafe.config.Config

trait StringConfigValueReader {
  implicit val StringValueReader: ConfigValueReader[String] = new ConfigValueReader[String] {
    def get(config: Config, path: String): String = config.getString(path)
  }
}
