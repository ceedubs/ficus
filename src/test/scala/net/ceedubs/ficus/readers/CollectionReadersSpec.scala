package net.ceedubs.ficus
package readers

import com.typesafe.config.ConfigFactory
import AnyValReaders.{booleanValueReader, doubleValueReader, intValueReader, longValueReader}
import StringReader.stringValueReader
import ConfigSerializerOps._
import org.scalacheck.util.Buildable
import org.scalacheck.Arbitrary
import CollectionReaderSpec._

class CollectionReadersSpec extends Spec with CollectionReaders { def is = s2"""
  The collection value readers should
    read a list ${readCollection[List]}
    read a set ${readCollection[Set]}
    read an array ${readCollection[Array]}
    read an indexed sequence ${readCollection[IndexedSeq]}
    read a vector ${readCollection[Vector]}
    read an iterable $readIterable
    read a map with strings as keys $readStringMap
    read a map nested in another object $readNestedMap
    read a collection when used directly $readCollectionUsedDirectly
  """

  def readIterable = {
    implicit def iterableSerializer[A: ConfigSerializer]: ConfigSerializer[Iterable[A]] = ConfigSerializer.iterableSerializer
    readCollection[Iterable]
  }

  def readStringMap = {
    def reads[A: Arbitrary : ValueReader: ConfigSerializer] = prop { map: Map[String, A] =>
      val cfg = ConfigFactory.parseString(s"myValue = ${map.asConfigValue}")
      mapValueReader[A].read(cfg, "myValue") must beEqualTo(map)
    }

    reads[String] and reads[Boolean] and reads[Int] and reads[Long] and reads[Double]
  }

  def readNestedMap = {
    val cfg = ConfigFactory.parseString(
      """
        |wrapper {
        |  myValue {
        |    item1 = "value1"
        |    item2 = "value2"
        |  }
        |}
      """.stripMargin)
    mapValueReader[String].read(cfg, "wrapper.myValue") must beEqualTo(Map("item1" -> "value1", "item2" -> "value2"))
  }

  protected def readCollection[C[_]](implicit BS: Buildable[String, C], SS: ConfigSerializer[C[String]], RS: ValueReader[C[String]],
                                     BB: Buildable[Boolean, C], SB: ConfigSerializer[C[Boolean]], RB: ValueReader[C[Boolean]],
                                     BI: Buildable[Int, C], SI: ConfigSerializer[C[Int]], RI: ValueReader[C[Int]],
                                     BL: Buildable[Long, C], SL: ConfigSerializer[C[Long]], RL: ValueReader[C[Long]],
                                     BD: Buildable[Double, C], SD: ConfigSerializer[C[Double]], RD: ValueReader[C[Double]]) = {

    def reads[V](implicit arb: Arbitrary[C[V]], serializer: ConfigSerializer[C[V]], reader: ValueReader[C[V]]) = {
      prop { values: C[V] =>
        val cfg = ConfigFactory.parseString(s"myValue = ${values.asConfigValue}")
        reader.read(cfg, "myValue") must beEqualTo(values)
      }
    }

    reads[String] and reads[Boolean] and reads[Int] and reads[Long] and reads[Double]
  }

  def readCollectionUsedDirectly = {
    val cfg = ConfigFactory.parseString("set: [1, 2, 2, 3]")
    traversableReader[Set, Int].read(cfg, "set") must beEqualTo(Set(1, 2, 3))
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
