package net.ceedubs.ficus.readers

import scala.quoted.*

import com.typesafe.config.Config

trait ArbitraryTypeReader {
  implicit inline def arbitraryTypeValueReader[T]: Generated[ValueReader[T]] =
    ${ ArbitraryTypeReaderMacros.arbitraryTypeValueReader[T] }
}

object ArbitraryTypeReader extends ArbitraryTypeReader

object ArbitraryTypeReaderMacros {
  def arbitraryTypeValueReader[T](using Quotes, Type[T]): Expr[Generated[ValueReader[T]]] = {
    import quotes.reflect.*
    '{
      Generated(new ValueReader[T] {
        def read(config: Config, path: String): T = ${
          instantiateFromConfig[T](config = '{ config }, path = '{ path }, mapper = '{ NameMapper() })
        }
      })
    }
  }

  def instantiateFromConfig[T](
      config: Expr[Config],
      path: Expr[String],
      mapper: Expr[NameMapper],
      default: Option[Expr[T]] = None
  )(using Quotes, Type[T]): Expr[T] = {
    import quotes.reflect.*
    val tTypeTree                                    = TypeTree.of[T]
    val typeSymbol: Symbol                           = tTypeTree.symbol
    val isCase                                       = typeSymbol.flags.is(Flags.Case)
    val hasCompanion                                 = typeSymbol.companionClass != Symbol.noSymbol && typeSymbol.companionClass.isDefinedInCurrentRun
    def getApplyO(companion: Symbol): Option[DefDef] =
      companion.memberMethod("apply").filter(_.isDefDef).map(_.tree).collect{ case d: DefDef => d }.find(_.returnTpt.symbol == tTypeTree.symbol)
    val companionHasApply                            = hasCompanion && getApplyO(typeSymbol.companionClass).isDefined
    def defaultIfNoPath(expr: Expr[T]): Expr[T]      = default match {
      case Some(d) => '{ if (! $config.hasPath($path)) $d else $expr }
      case None    => expr
    }
//    val isClassDef = typeSymbol.isClassDef

    if (companionHasApply) {
      val classDef: ClassDef            = typeSymbol.tree.asInstanceOf[ClassDef]
      val companionApply: DefDef        = getApplyO(typeSymbol.companionClass).get
      println(s"companionApply.returnTpt = ${companionApply.returnTpt}")
      println(s"companionApply.returnTpt.symbol = ${companionApply.returnTpt.symbol}")
      println(s"companionApply.returnTpt.tpe = ${companionApply.returnTpt.tpe}")
      println(s"tTypeTree.symbol = ${tTypeTree.symbol}")
      println(s"tTypeTree.symbol == companionApply.returnTpt.symbol ${tTypeTree.symbol == companionApply.returnTpt.symbol}")
//      println(s"companionApply.returnTpt = ${companionApply.returnTpt.tpe.symbol}")
      val companionApplySymbol: Symbol  = companionApply.symbol
      val params: List[TermParamClause] = companionApply.termParamss
      val filledParams: List[Term]      = params.head.params.map { case v: ValDef =>
        val nextPath: Expr[String]   = '{ (if ($path == ".") "" else $path + ".") + $mapper.map(${ Expr(v.name) }) }
//        v.asExpr match { case '{ $expr: t } => instantiateFromConfig[t](config, nextPath, mapper)(using summon[Quotes], v.tpt.tpe.asType.asInstanceOf[Type[t]]).asTerm }
        type X
        val tpe                      = v.tpt.tpe.asType.asInstanceOf[Type[X]]
        val default: Option[Expr[X]] = v.rhs.map(_.asExpr.asInstanceOf[Expr[X]])
        if (default.isDefined) println(s"Have a default of $default")
        instantiateFromConfig[X](config, nextPath, mapper, default)(using summon[Quotes], tpe)
          .asInstanceOf[Expr[Any]]
          .asTerm
      }
      val res                           = Apply(Ref(companionApplySymbol), filledParams)
      defaultIfNoPath(res.asExpr.asInstanceOf[Expr[T]])
    } else {
      val leafReader: Expr[ValueReader[T]] = Implicits.search(TypeTree.of[ValueReader[T]].tpe) match {
        case succ: ImplicitSearchSuccess =>
//          report.throwError(s"Actually succeeded, but could recurse...\n tree=${succ.tree}\nsymbol=${succ.tree.symbol}\nexpr=${succ.tree.asExpr.show}")
          succ.tree.asExpr.asInstanceOf[Expr[ValueReader[T]]]
//        case succ: ImplicitSearchSuccess => Ref(succ.tree.symbol).asExpr.asInstanceOf[Expr[ValueReader[T]]]
        case fail: ImplicitSearchFailure => report.throwError(fail.explanation)
      }
      defaultIfNoPath('{ $leafReader.read($config, $path) })
    }

  }
}
