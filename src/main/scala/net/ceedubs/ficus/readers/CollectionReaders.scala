package net.ceedubs.ficus.readers

import com.typesafe.config.Config
import collection.JavaConverters._

trait CollectionReaders {
  protected val DummyPathValue: String = "collection-entry-path"

  implicit def delegatingSetValueReader[A](implicit entryReader: ValueReader[A]): ValueReader[Set[A]] = new ValueReader[Set[A]] {
    def read(config: Config, path: String): Set[A] = delegatingListValueReader[A](entryReader).read(config, path).toSet
  }

  implicit def delegatingListValueReader[A](implicit entryReader: ValueReader[A]): ValueReader[List[A]] = new ValueReader[List[A]] {
    def read(config: Config, path: String): List[A] = {
      config.getList(path).asScala.toList map { entry =>
        val entryConfig = entry.atPath(DummyPathValue)
        entryReader.read(entryConfig, DummyPathValue)
      }
    }
  }

  implicit def delegatingMapValueReader[A](implicit entryReader: ValueReader[A]): ValueReader[Map[String, A]] = new ValueReader[Map[String, A]] {
    def read(config: Config, path: String): Map[String, A] = {
      config.getConfig(path).root().entrySet().asScala map { entry =>
        val key = entry.getKey
        key -> entryReader.read(config, s"$path.$key")
      } toMap
    }
  }
}

object CollectionReaders extends CollectionReaders