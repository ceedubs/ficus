package net.ceedubs.ficus
package readers

import scala.language.experimental.macros
import com.typesafe.config.Config

trait CaseClassReader {
  implicit def caseClassValueReader[T <: Product]: ValueReader[T] = macro CaseClassReaderMacros.caseClassValueReader[T]
}

object CaseClassReader extends CaseClassReader

object CaseClassReaderMacros extends ReflectionUtils {
  import scala.reflect.macros.Context

  def caseClassValueReader[T <: Product : c.WeakTypeTag](c: Context): c.Expr[ValueReader[T]] = {
    import c.universe._

    reify {
      new ValueReader[T] {
        def read(config: Config, path: String): T = hydrateCaseClassImpl[T](c)(
          config = c.Expr[Config](Ident(newTermName("config"))),
          path = c.Expr[String](Ident(newTermName("path")))).splice
      }
    }
  }

  // TODO should look for default values and fall back to them
  def hydrateCaseClassImpl[T <: Product : c.WeakTypeTag](c: Context)(config: c.Expr[Config], path: c.Expr[String]): c.Expr[T] = {
    import c.universe._

    if (!weakTypeOf[T].typeSymbol.asClass.isCaseClass) {
      c.abort(c.enclosingPosition, "Must be a type class!")
    } else {
      val constructorArgs = accessors[T](c.universe) map { m =>
        val name = c.literal(m.name.decoded)
        val returnType: Type = m.typeSignatureIn(weakTypeOf[T]) match {
          case NullaryMethodType(tpe) => tpe
        }
        val key = reify(path.splice + "." + name.splice)

        val reader = c.inferImplicitValue(appliedType(weakTypeOf[ValueReader[_]].typeConstructor, List(returnType)), silent = false)
        val argValue = readConfigValue(c)(config, key, reader)
        argValue.tree
      }
      val tApply = Select(
        Ident(weakTypeOf[T].typeSymbol.companionSymbol),
        newTermName("apply"))
      c.Expr[T](Apply(tApply, constructorArgs))
    }
  }

  def readConfigValue[T: c.universe.WeakTypeTag](c: Context)(config: c.Expr[Config], path: c.Expr[String], reader: c.Tree): c.Expr[T] = {
    import c.universe._
    val readerRead = Select(reader, newTermName("read"))
    c.Expr[T](Apply(readerRead, List(config.tree, path.tree)))
  }
}

trait ReflectionUtils {
  import scala.reflect.api.Universe

  def accessors[A: u.WeakTypeTag](u: Universe) = {
    import u._

    u.weakTypeOf[A].declarations.collect {
      case acc: MethodSymbol if acc.isCaseAccessor => acc
    }.toList
  }

  def printfTree(u: Universe)(format: String, trees: u.Tree*) = {
    import u._

    Apply(
      Select(reify(Predef).tree, newTermName("printf")),
      Literal(Constant(format)) :: trees.toList
    )
  }
}
