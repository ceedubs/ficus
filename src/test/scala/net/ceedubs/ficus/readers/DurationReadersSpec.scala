package net.ceedubs.ficus
package readers

import com.typesafe.config.ConfigFactory
import scala.concurrent.duration._

class DurationReadersSpec extends Spec with DurationReaders { def is =
  "The finite duration reader should" ^
    "read a millisecond value" ! readMillis ^
    "read a minute value" ! readMinute

  def readMillis = {
    val cfg = ConfigFactory.parseString("myValue = 15")
    finiteDurationReader.get(cfg, "myValue") must beEqualTo(15 millis)
  }

  def readMinute = {
    val cfg = ConfigFactory.parseString("myValue = \"15 minutes\"")
    finiteDurationReader.get(cfg, "myValue") must beEqualTo(15 minutes)
  }

}
