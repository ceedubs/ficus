package net.ceedubs.ficus
package readers

import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._
import org.scalacheck.{Gen, Prop}

class DurationReadersSpec extends Spec with DurationReaders { def is = s2"""
  The finite duration reader should
    read a millisecond value $readMillis
    read a minute value $readMinutes
  """

  def readMillis = prop { i: Int =>
    val cfg = ConfigFactory.parseString(s"myValue = $i")
    finiteDurationReader.read(cfg, "myValue") must beEqualTo(i millis)
  }

  def readMinutes = Prop.forAll(Gen.choose(1.5e-8.toInt, 1.5e8.toInt)) { i: Int =>
    val cfg = ConfigFactory.parseString("myValue = \"" + i + " minutes\"")
    finiteDurationReader.read(cfg, "myValue") must beEqualTo(i minutes)
  }

}
