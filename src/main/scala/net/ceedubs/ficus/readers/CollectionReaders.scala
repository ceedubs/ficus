package net.ceedubs.ficus.readers

import com.typesafe.config.Config
import collection.JavaConverters._
import scala.reflect.ClassTag

trait CollectionReaders {
  import CollectionReaderUtil._

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

  implicit def delegatingArrayReader[A : ClassTag : ValueReader]: ValueReader[Array[A]] = fromListReader[A, Array](_.toArray)

  implicit def delegatingIndexedSeqReader[A : ValueReader]: ValueReader[IndexedSeq[A]] = fromListReader[A, IndexedSeq](_.toIndexedSeq)

  implicit def delegatingIterableReader[A : ValueReader]: ValueReader[Iterable[A]] = fromListReader[A, Iterable](_.toIterable)

  implicit def delegatingVectorReader[A : ValueReader]: ValueReader[Vector[A]] = fromListReader[A, Vector](_.toVector)
}

object CollectionReaders extends CollectionReaders

object CollectionReaderUtil {
  def fromListReader[A, C[_]](f: List[A] => C[A])(implicit listReader: ValueReader[List[A]]): ValueReader[C[A]] = new ValueReader[C[A]] {
    def read(config: Config, path: String): C[A] = f(listReader.read(config, path))
  }
}