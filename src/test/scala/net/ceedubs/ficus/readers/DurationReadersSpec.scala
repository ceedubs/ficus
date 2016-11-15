package net.ceedubs.ficus
package readers

import com.typesafe.config.ConfigFactory

import scala.concurrent.duration._
import org.scalacheck.{Gen, Prop}

class DurationReadersSpec extends Spec with DurationReaders { def is = s2"""
  The finite duration reader should
    read a millisecond value $readMillis
    read a minute value $readMinutes
    read a days value into days $readDaysUnit
  """

  def readMillis = prop { i: Int =>
    val cfg = ConfigFactory.parseString(s"myValue = $i")
    finiteDurationReader.read(cfg, "myValue") must beEqualTo(i millis)
  }

  def readMinutes = Prop.forAll(Gen.choose(-1.5e8.toInt, 1.5e8.toInt)) { i: Int =>
    val cfg = ConfigFactory.parseString("myValue = \"" + i + " minutes\"")
    finiteDurationReader.read(cfg, "myValue") must beEqualTo(i minutes)
  }

  def readDaysUnit = Prop.forAll(Gen.choose(-106580, 106580)) { i: Int =>
    val str = i + " day" + (if (i == 1) "" else "s")
    val cfg = ConfigFactory.parseString(s"""myValue = "$str" """)
    finiteDurationReader.read(cfg, "myValue").toString must beEqualTo(str)
  }
}
