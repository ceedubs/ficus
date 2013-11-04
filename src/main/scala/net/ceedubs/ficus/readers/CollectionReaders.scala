package net.ceedubs.ficus.readers

import com.typesafe.config.{ConfigUtil, Config}
import collection.JavaConverters._
import scala.reflect.ClassTag

trait CollectionReaders {

  private[this] val DummyPathValue: String = "collection-entry-path"

  implicit def delegatingSetValueReader[A : ValueReader]: ValueReader[Set[A]] = delegatingListValueReader[A].map(_.toSet)

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
      val relativeConfig = config.getConfig(path)
      relativeConfig.root().entrySet().asScala map { entry =>
        val key = entry.getKey
        key -> entryReader.read(relativeConfig, ConfigUtil.quoteString(key))
      } toMap
    }
  }

  implicit def delegatingArrayReader[A : ClassTag : ValueReader]: ValueReader[Array[A]] = delegatingListValueReader[A].map(_.toArray)

  implicit def delegatingIndexedSeqReader[A : ValueReader]: ValueReader[IndexedSeq[A]] = delegatingListValueReader[A].map(_.toIndexedSeq)

  implicit def delegatingIterableReader[A : ValueReader]: ValueReader[Iterable[A]] = delegatingListValueReader[A].map(_.toIterable)

  implicit def delegatingVectorReader[A : ValueReader]: ValueReader[Vector[A]] = delegatingListValueReader[A].map(_.toVector)
}

object CollectionReaders extends CollectionReaders