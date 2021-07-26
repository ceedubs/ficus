package net.ceedubs.ficus
package readers

import java.time.Period
import com.typesafe.config.{ Config, ConfigFactory }
import Ficus._

class PeriodReaderSpec extends Spec {
  val periodReaderExists = implicitly[ValueReader[Period]]
  def is = s2"""
  The PeriodReader should 
    read a Period in ISO-8601 format $readPeriod
    read a negative Period $readNegativePeriod
  """

  def readPeriod = {
    val cfg: Config      = ConfigFactory.parseString(s"""
         | foo {
         |    interval = "P1Y3M10D"
         | }
       """.stripMargin)
    val period: Period   = cfg.to("foo.interval")
    val expected = Period.of(1, 3, 10)
    period should beEqualTo(expected)
  }

  def readNegativePeriod = {
    val cfg      = ConfigFactory.parseString(s"""
         | foo {
         |    interval = "P-1Y10M3D"
         | }
       """.stripMargin)
    val period   = cfg.to[Period]("foo.interval")
    val expected = Period.of(-1, 10, 3)
    period should beEqualTo(expected)
  }
}
