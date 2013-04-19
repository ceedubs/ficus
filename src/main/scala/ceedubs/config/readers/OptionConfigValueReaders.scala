package ceedubs.config.readers

import com.typesafe.config.Config

trait OptionConfigValueReaders {
  implicit def optionValueReader[A](implicit ValueReader: ConfigValueReader[A]): ConfigValueReader[Option[A]] = new ConfigValueReader[Option[A]] {
    def get(config: Config, path: String): Option[A] = {
      if (config.hasPath(path)) {
        Some(ValueReader.get(config, path))
      } else {
        None
      }
    }
  }
}
