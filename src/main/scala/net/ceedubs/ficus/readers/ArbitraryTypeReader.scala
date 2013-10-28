package net.ceedubs.ficus.readers

import com.typesafe.config.Config
import scala.language.experimental.macros
import scala.reflect.internal.{StdNames, SymbolTable, Definitions}

trait ArbitraryTypeReader {
  implicit def arbitraryTypeValueReader[T]: ValueReader[T] = macro ArbitraryTypeReaderMacros.arbitraryTypeValueReader[T]
}

object ArbitraryTypeReader extends ArbitraryTypeReader

object ArbitraryTypeReaderMacros {
  import scala.reflect.macros.Context

  def arbitraryTypeValueReader[T : c.WeakTypeTag](c: Context): c.Expr[ValueReader[T]] = {
    import c.universe._

    reify {
      new ValueReader[T] {
        def read(config: Config, path: String): T = instantiateFromConfig[T](c)(
          config = c.Expr[Config](Ident(newTermName("config"))),
          path = c.Expr[String](Ident(newTermName("path")))).splice
      }
    }
  }

  def instantiateFromConfig[T : c.WeakTypeTag](c: Context)(config: c.Expr[Config], path: c.Expr[String]): c.Expr[T] = {
    import c.universe._

    val tpe = weakTypeOf[T]

    def fail(reason: String) = c.abort(c.enclosingPosition, s"Cannot generate a config value reader for type $tpe, because $reason")

    val companionSymbol = tpe.typeSymbol.companionSymbol match {
      case NoSymbol => fail("it does not have a companion object with an apply method")
      case x => x
    }
    val applyMethod = companionSymbol.typeSignature.member(newTermName("apply")) match {
      case NoSymbol => fail("its companion object does not have an apply method")
      case x: TermSymbol if x.isOverloaded => fail("the apply method in its companion object is overloaded")
      case x: MethodSymbol => x
    }

    if (!(applyMethod.returnType <:< tpe)) fail(s"the apply method in its companion object returns type ${applyMethod.returnType} instead of $tpe")

    val instantiationArgs = extractMethodArgsFromConfig(c)(method = applyMethod,
      companionObjectMaybe = Some(companionSymbol), config = config, path = path, fail = fail)

    val tApply = Select(Ident(companionSymbol), applyMethod.name)
    c.Expr[T](Apply(tApply, instantiationArgs))
  }

  def extractMethodArgsFromConfig(c: Context)(method: c.universe.MethodSymbol, companionObjectMaybe: Option[c.Symbol],
                                              config: c.Expr[Config], path: c.Expr[String], fail: String => Nothing): List[c.Tree] = {
    import c.universe._

    val decodedMethodName = method.name.decoded

    if (!method.isPublic) fail(s"'$decodedMethodName' method is not public")
    method.paramss.head.zipWithIndex map { case (param, index) =>
      val name = param.name.decoded
      val nameExpr = c.literal(name)
      val returnType: Type = param.typeSignature
      val key = reify(path.splice + "." + nameExpr.splice)

      val readerType = appliedType(weakTypeOf[ValueReader[_]].typeConstructor, List(returnType))
      val reader = c.inferImplicitValue(readerType, silent = true) match {
        case EmptyTree => fail(s"an implicit value reader of type $readerType must be in scope to read parameter '$name' on '$decodedMethodName' method")
        case x => x
      }

      companionObjectMaybe.filter(_ => param.asTerm.isParamWithDefault) map { companionObject =>
        val optionReader = Apply(Select(reify(OptionReader).tree, newTermName("optionValueReader")), List(reader))
        val argValueMaybe = readConfigValue(c)(config, key, optionReader)
        Apply(Select(argValueMaybe.tree, newTermName("getOrElse")), List({
          // fall back to default value for param
          val u = c.universe.asInstanceOf[Definitions with SymbolTable with StdNames]
          val getter = u.nme.defaultGetterName(u.newTermName(decodedMethodName), index + 1)
          Select(Ident(companionObject), newTermName(getter.encoded))
        }))
      } getOrElse {
        val argValue = readConfigValue(c)(config, key, reader)
        argValue.tree
      }
    }
  }

  def readConfigValue[T: c.universe.WeakTypeTag](c: Context)(config: c.Expr[Config], path: c.Expr[String], reader: c.Tree): c.Expr[T] = {
    import c.universe._
    val readerRead = Select(reader, newTermName("read"))
    c.Expr[T](Apply(readerRead, List(config.tree, path.tree)))
  }
}
