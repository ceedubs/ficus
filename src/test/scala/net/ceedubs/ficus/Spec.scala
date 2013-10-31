package net.ceedubs.ficus

import org.specs2.specification.{FormattingFragments, FragmentsBuilder, BaseSpecification}
import org.specs2.matcher.MustMatchers
import org.specs2.ScalaCheck

trait Spec extends BaseSpecification with MustMatchers with FragmentsBuilder with FormattingFragments with ScalaCheck
