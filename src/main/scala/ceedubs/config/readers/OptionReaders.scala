package ceedubs.config.readers

import com.typesafe.config.Config

trait OptionReaders {
  implicit def optionValueReader[A](implicit ValueReader: ValueReader[A]): ValueReader[Option[A]] = new ValueReader[Option[A]] {
    def get(config: Config, path: String): Option[A] = {
      if (config.hasPath(path)) {
        Some(ValueReader.get(config, path))
      } else {
        None
      }
    }
  }
}
