package net.ceedubs.ficus.readers

import scala.util.Try
import com.typesafe.config.Config

trait TryReader {
  implicit def tryValueReader[A](implicit valueReader: ValueReader[A]): ValueReader[Try[A]] = new ValueReader[Try[A]] {
    def read(config: Config, path: String): Try[A] = Try(valueReader.read(config, path))
  }
}

object TryReader extends TryReader
