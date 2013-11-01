package net.ceedubs.ficus

import org.specs2.specification.{FormattingFragments, FragmentsBuilder, BaseSpecification}
import org.specs2.matcher.MustMatchers
import org.specs2.ScalaCheck
import org.scalacheck.Gen

trait Spec extends BaseSpecification with MustMatchers with FragmentsBuilder with FormattingFragments with ScalaCheck {
  val jsonStringValue = Spec.jsonStringValue
}

object Spec {
  private[this] val SpecialJsonCharacters = Set('\\', '"')

  private[this] val JsonStringChars = ('\u0020' to '\u007E').filterNot(SpecialJsonCharacters.contains)

  val jsonStringValue = Gen.listOf(JsonStringChars).map(_.mkString)
}