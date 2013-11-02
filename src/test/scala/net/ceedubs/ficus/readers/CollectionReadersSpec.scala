package net.ceedubs.ficus
package readers

import com.typesafe.config.ConfigFactory
import AnyValReaders.{booleanValueReader, doubleValueReader, intValueReader, longValueReader}
import StringReader.stringValueReader
import ConfigSerializerOps._
import org.scalacheck.util.Buildable

class CollectionReadersSpec extends Spec with CollectionReaders { def is = s2"""
  The collection value readers should
    read a list $readList
    read a set $readSet
    read an array $readArray
    read an indexed sequence $readIndexedSeq
    read a vector $readVector
    read an iterable $readIterable
  """

  import CollectionReaderSpec._

  def readList = readCollection[List]

  def readSet = readCollection[Set]

  def readArray = readCollection[Array]

  def readIndexedSeq = readCollection[IndexedSeq]

  def readVector = readCollection[Vector]

  def readIterable = {
    implicit def iterableSerializer[A: ConfigSerializer]: ConfigSerializer[Iterable[A]] = ConfigSerializer.iterableSerializer
    readCollection[Iterable]
  }

  protected def readCollection[C[_]](implicit BS: Buildable[String, C], SS: ConfigSerializer[C[String]], RS: ValueReader[C[String]],
                                     BB: Buildable[Boolean, C], SB: ConfigSerializer[C[Boolean]], RB: ValueReader[C[Boolean]],
                                     BI: Buildable[Int, C], SI: ConfigSerializer[C[Int]], RI: ValueReader[C[Int]],
                                     BL: Buildable[Long, C], SL: ConfigSerializer[C[Long]], RL: ValueReader[C[Long]],
                                     BD: Buildable[Double, C], SD: ConfigSerializer[C[Double]], RD: ValueReader[C[Double]]) = {

    val readsStrings = prop { strings: C[String] =>
      val cfg = ConfigFactory.parseString("myValue = " + strings.asConfigValue)
      RS.read(cfg, "myValue") must beEqualTo(strings)
    }

    val readsBooleans = prop { booleans: C[Boolean] =>
      val cfg = ConfigFactory.parseString("myValue = " + booleans.asConfigValue)
      RB.read(cfg, "myValue") must beEqualTo(booleans)
    }

    val readsInts = prop { ints: C[Int] =>
      val cfg = ConfigFactory.parseString("myValue = " + ints.asConfigValue)
      RI.read(cfg, "myValue") must beEqualTo(ints)
    }

    val readsLongs = prop { longs: C[Long] =>
      val cfg = ConfigFactory.parseString("myValue = " + longs.asConfigValue)
      RL.read(cfg, "myValue") must beEqualTo(longs)
    }

    val readsDoubles = prop { doubles: C[Double] =>
      val cfg = ConfigFactory.parseString("myValue = " + doubles.asConfigValue)
      RD.read(cfg, "myValue") must beEqualTo(doubles)
    }

    readsStrings and readsBooleans and readsInts and readsLongs and readsDoubles
  }

}

object CollectionReaderSpec {
  import scala.collection._

  implicit def buildableIndexedSeq[T]: Buildable[T, IndexedSeq] = new Buildable[T, IndexedSeq] {
    def builder = IndexedSeq.newBuilder[T]
  }

  implicit def buildableVector[T]: Buildable[T, Vector] = new Buildable[T, Vector] {
    def builder = Vector.newBuilder[T]
  }

  implicit def buildableIterable[T]: Buildable[T, Iterable] = new Buildable[T, Iterable] {
    def builder = new mutable.ListBuffer[T]
  }
}
