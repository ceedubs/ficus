package net.ceedubs.ficus

import org.specs2.matcher.MustMatchers
import org.specs2.{ScalaCheck, Specification}
import org.scalacheck.{Arbitrary, Gen}

trait Spec extends Specification with MustMatchers with ScalaCheck {
  val jsonStringValue = Spec.jsonStringValue
  implicit val jsonStringArbitrary: Arbitrary[String] = Arbitrary(jsonStringValue)
}

object Spec {
  private[this] val SpecialJsonCharacters = Set('\\', '"')

  private[this] val JsonStringChars = ('\u0020' to '\u007E').filterNot(SpecialJsonCharacters.contains)

  val jsonStringValue: Gen[String] = Gen.listOf(JsonStringChars).map(_.mkString)
}