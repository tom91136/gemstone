package net.kurobako.gemstone.semantics.render

import net.kurobako.gemstone.collection.*
import scala.annotation.tailrec
import scala.quoted.{Type as _, *}

/** How a primitive renders: the object and method its function applies, and each argument as an operand of the
  * semantics or as code for a value fixed per instruction. The layout is read from the function's tree.
  */
object PrimitiveLayout {
  enum Argument {
    case Operand(index: Int)
    case Fixed(code: String)
  }

  case class Layout(owner: String, method: String, arguments: Vec[Argument])

  /** A fixed argument as code; `prefix` qualifies an enum case. */
  def code[A](value: A, prefix: String): String = value match {
    case number: Long if number > Int.MaxValue || number < Int.MinValue => s"${number}L"
    case text: String                                                   => s"\"$text\""
    case other if prefix.nonEmpty                                       => s"$prefix.$other"
    case other                                                          => other.toString
  }

  /** The layout of `function` applied to the operands of the semantics, as a term. */
  def term(layout: Layout, operands: Vec[Term], parameters: Vec[Type], result: Type): Term = Term.Primitive(
    layout.owner,
    layout.method,
    layout.arguments.map {
      case Argument.Operand(index) => operands(index)
      case Argument.Fixed(code)    => Term.Constant(code)
    },
    layout.arguments.map {
      case Argument.Operand(index) => Some(parameters(index))
      case Argument.Fixed(_)       => None
    },
    result
  )

  /** The layout of `function`, which must apply one method of a static object to its parameters, each used once and
    * directly, and to values fixed per instruction.
    */
  inline def of[F](inline function: F): Layout = ${ read('function) }

  def read[F](function: Expr[F])(using Quotes): Expr[Layout] = {
    import quotes.reflect.*
    given CanEqual[Symbol, Symbol] = CanEqual.derived
    @tailrec
    def strip(term: Term): Term = term match {
      case Inlined(_, Nil, inner) => strip(inner)
      case Block(Nil, inner)      => strip(inner)
      case Typed(inner, _)        => strip(inner)
      case _                      => term
    }
    def fail(reason: String): Nothing =
      report.errorAndAbort(s"a primitive must apply one method of a static object: $reason", function)
    def path(symbol: Symbol): String    = symbol.fullName.replace("$", "")
    def static(symbol: Symbol): Boolean =
      symbol.isPackageDef || (symbol.flags.is(Flags.Module) && static(symbol.owner))
    strip(function.asTerm) match {
      case Lambda(parameters, body) =>
        val (applied, arguments) = strip(body) match {
          case Apply(applied, arguments) => (applied, arguments)
          case other                     => fail(other.show)
        }
        val method = applied.symbol
        if (!static(method.owner)) fail(s"${method.fullName} is not a member of a static object")
        val symbols        = parameters.map(_.symbol).toVector
        val argumentCount  = arguments.size
        val repeated       = arguments.exists { case Typed(Repeated(_, _), _) => true; case _ => false }
        val parameterNames =
          if (repeated) Vector.empty[String]
          else
            method.paramSymss.map(_.toVector).findLast(list => list.forall(_.isTerm) && list.size == argumentCount)
              .fold(Vector.empty[String])(_.map(_.name))
        class Mentions extends TreeAccumulator[Boolean] {
          def foldTree(found: Boolean, tree: Tree)(owner: Symbol): Boolean =
            found || symbols.contains(tree.symbol) || foldOverTree(found, tree)(owner)
        }
        def mentions(tree: Tree): Boolean = Mentions().foldTree(found = false, tree)(Symbol.spliceOwner)
        val laid                          = arguments.zipWithIndex.map { (argument, position) =>
          val stripped = strip(argument)
          val named    = stripped match {
            case NamedArg(name, _) => Some(name)
            case _                 => None
          }
          val name  = named.orElse(parameterNames.lift(position))
          val value = stripped match {
            case NamedArg(_, value) => strip(value)
            case other              => other
          }
          symbols.indexOf(value.symbol) match {
            case index if index >= 0 && named.isEmpty => '{ Argument.Operand(${ Expr(index) }) }
            case index if index >= 0                  => fail(s"operand ${value.show} is passed by name")
            case _                                    =>
              if (mentions(value)) fail(s"${value.show} transforms an operand")
              val widened = value.tpe.widen.dealias
              val prefix  =
                if (widened.typeSymbol.flags.is(Flags.Enum)) path(widened.typeSymbol.companionModule) else ""
              val rendered = '{ code(${ value.asExprOf[Any] }, ${ Expr(prefix) }) }
              name.fold('{ Argument.Fixed($rendered) })(label =>
                '{ Argument.Fixed(${ Expr(label) } + " = " + $rendered) }
              )
          }
        }
        if (
          parameters.indices.exists(index => arguments.count(argument => strip(argument).symbol == symbols(index)) != 1)
        )
          fail("each operand must be passed exactly once")
        '{ Layout(${ Expr(path(method.owner)) }, ${ Expr(method.name) }, Vec(${ Varargs(laid) }*)) }
      case other => fail(other.show)
    }
  }
}
