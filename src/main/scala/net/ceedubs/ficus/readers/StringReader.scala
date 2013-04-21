package net.ceedubs.ficus.readers

import com.typesafe.config.Config

trait StringReader {
  implicit val stringValueReader: ValueReader[String] = new ValueReader[String] {
    def get(config: Config, path: String): String = config.getString(path)
  }
}

object StringReader extends StringReader
