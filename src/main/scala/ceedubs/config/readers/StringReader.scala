package ceedubs.config.readers

import com.typesafe.config.Config

trait StringReader {
  implicit val StringValueReader: ValueReader[String] = new ValueReader[String] {
    def get(config: Config, path: String): String = config.getString(path)
  }
}
