package net.ceedubs.ficus.readers

import com.typesafe.config.Config

trait OptionReader {
  implicit def optionValueReader[A](implicit ValueReader: ValueReader[A]): ValueReader[Option[A]] = new ValueReader[Option[A]] {
    def read(config: Config, path: String): Option[A] = {
      if (config.hasPath(path)) {
        Some(ValueReader.read(config, path))
      } else {
        None
      }
    }
  }
}

object OptionReader extends OptionReader
