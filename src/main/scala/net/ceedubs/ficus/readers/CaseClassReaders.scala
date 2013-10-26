package net.ceedubs.ficus
package readers

import scala.language.experimental.macros

trait CaseClassReader {
  implicit def caseClassValueReader[T <: Product]: ValueReader[T] = macro CaseClassReaderMacros.caseClassValueReader[T]
}

object CaseClassReader extends CaseClassReader

object CaseClassReaderMacros {
  import scala.reflect.macros.Context

  def caseClassValueReader[T <: Product : c.WeakTypeTag](c: Context): c.Expr[ValueReader[T]] = {
    import c.universe._
    val tpe = weakTypeOf[T]
    if (!tpe.typeSymbol.asClass.isCaseClass) {
      c.abort(c.enclosingPosition, "Must be a type class!")
    } else {
      CompanionApplyReaderMacros.companionApplyValueReader[T](c)
    }
  }
}
