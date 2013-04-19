package ceedubs.config.readers

import com.typesafe.config.Config

trait AnyValConfigValueReaders {
  implicit val BooleanValueReader: ConfigValueReader[Boolean] = new ConfigValueReader[Boolean] {
    def get(config: Config, path: String): Boolean = config.getBoolean(path)
  }

  implicit val IntValueReader: ConfigValueReader[Int] = new ConfigValueReader[Int] {
    def get(config: Config, path: String): Int = config.getInt(path)
  }

  implicit val LongValueReader: ConfigValueReader[Long] = new ConfigValueReader[Long] {
    def get(config: Config, path: String): Long = config.getLong(path)
  }
}
