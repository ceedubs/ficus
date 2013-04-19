package ceedubs.config

import com.typesafe.config.ConfigFactory
import KindsafeConfig.{ BooleanValueReader, toKindsafeConfig }

class KindsafeConfigSpec extends Spec { def is =
  "A Kindsafe config should" ^
    "be implicitly converted from a Typesafe config" ! implicitlyConverted

  def implicitlyConverted = {
    val cfg = ConfigFactory.parseString("myValue = true")
    cfg.getAs[Boolean]("myValue") must beTrue
  }
}
