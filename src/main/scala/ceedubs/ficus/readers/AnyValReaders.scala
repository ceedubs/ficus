package ceedubs.ficus.readers

import com.typesafe.config.Config

trait AnyValReaders {
  implicit val booleanValueReader: ValueReader[Boolean] = new ValueReader[Boolean] {
    def get(config: Config, path: String): Boolean = config.getBoolean(path)
  }

  implicit val intValueReader: ValueReader[Int] = new ValueReader[Int] {
    def get(config: Config, path: String): Int = config.getInt(path)
  }

  implicit val longValueReader: ValueReader[Long] = new ValueReader[Long] {
    def get(config: Config, path: String): Long = config.getLong(path)
  }

  implicit val doubleValueReader: ValueReader[Double] = new ValueReader[Double] {
    def get(config: Config, path: String): Double = config.getDouble(path)
  }
}

object AnyValReaders extends AnyValReaders
