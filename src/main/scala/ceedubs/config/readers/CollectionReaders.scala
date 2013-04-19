package ceedubs.config.readers

import com.typesafe.config.Config
import collection.JavaConverters._

trait CollectionReaders {
  implicit val StringCastable = castableCollectionKind[String]
  implicit val BooleanCastable = castableCollectionKind[Boolean]
  implicit val IntCastable = castableCollectionKind[Int]
  implicit val LongCastable = castableCollectionKind[Long]
  implicit val DoubleCastable = castableCollectionKind[Double]
  
  protected def castableCollectionKind[A]: CastableCollectionKind[A] = new CastableCollectionKind[A] {}
  
  implicit def castingListValueReader[A](implicit evidence: CastableCollectionKind[A]): ValueReader[List[A]] = new ValueReader[List[A]] {
    def get(config: Config, path: String): List[A] = config.getList(path).unwrapped().asScala.toList.asInstanceOf[List[A]]
  }

  implicit def castingSetValueReader[A](implicit evidence: CastableCollectionKind[A]): ValueReader[Set[A]] = new ValueReader[Set[A]] {
    def get(config: Config, path: String): Set[A] = config.getList(path).unwrapped().asScala.toSet.asInstanceOf[Set[A]]
  }
}

trait CastableCollectionKind[A]
