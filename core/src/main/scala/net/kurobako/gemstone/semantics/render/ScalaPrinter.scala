package net.kurobako.gemstone.semantics.render

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import scala.util.matching.Regex

/** Terms and names as Scala over [[prelude]]. */
private[gemstone] object ScalaPrinter {
  private enum Side derives CanEqual {
    case Left, Right
  }

  private val Plain = "[A-Za-z_][A-Za-z0-9_]*".r

  def identifier(name: String): String = name match {
    case Plain() => name
    case _       => s"`$name`"
  }

  /** A package-qualified reference to a type or object, such as an enum case's. */
  private val Qualified = raw"\b((?:[a-z][A-Za-z0-9_]*\.)+)([A-Z][A-Za-z0-9_]*)".r

  /** The outermost objects `code` refers to, fully qualified, which it reads unqualified once imported. */
  def owners(code: String): Vec[String] = Qualified.findAllMatchIn(code).map(found => found.matched).toVec

  private def unqualified(code: String): String =
    Qualified.replaceAllIn(code, found => Regex.quoteReplacement(found.group(2)))

  def literal(value: Long): String =
    // a single cleared bit reads as the complement of that bit
    if (value < 0 && ~value >= 0xff && (~value & (~value - 1)) == 0L)
      s"~${literal(~value)}"
    else suffixed(value)

  /** `value` as a Scala literal, a `Long` one where an `Int` cannot hold it. */
  private def suffixed(value: Long): String =
    if (value > Int.MaxValue || value < Int.MinValue) s"${Term.literal(value)}L" else Term.literal(value)

  def show(tpe: Type): String = tpe match {
    case Type.Integer(width, signed)  => s"${if (signed) "SInt" else "UInt"}[$width]"
    case Type.Float(width)            => s"Float$width"
    case Type.Encoded(format)         => Type.name(format)
    case Type.Bool                    => "Bool"
    case Type.Ranged(base, intervals) =>
      def interval(low: Long, high: Long): String =
        if (low == high) suffixed(low) else s"${suffixed(low)} ~ ${suffixed(high)}"
      intervals match {
        case Vec((low, high)) => s"${show(base)} in ${interval(low, high)}"
        case _ => s"${show(base)} in ${intervals.map((low, high) => s"(${interval(low, high)})").mkString(" | ")}"
      }
  }

  private def precedence(operator: String): Int = operator match {
    case "*" | "/" | "%"         => 9
    case "+" | "-"               => 8
    case "<<" | ">>" | ">>>"     => 7
    case "<" | "<=" | ">" | ">=" => 6
    case "==" | "!="             => 5
    case "&"                     => 4
    case "^"                     => 3
    case "|"                     => 2
    case "&&"                    => 1
    case _                       => 0
  }

  private val Arithmetic  = Set("*", "/", "%", "+", "-")
  private val Additive    = Set("+", "-")
  private val Comparison  = Set("<", "<=", ">", ">=", "==", "!=")
  private val Associative = Set("+", "*", "&", "|", "^", "&&", "||")

  /** `term` with each method of a selection applied to its branches instead, whose types Scala would otherwise join. */
  private def pushed(term: Term): Term = term match {
    case Term.Call(method, Vec(receiver)) if method.startsWith(".") =>
      pushed(receiver) match {
        case Term.Select(condition, whenTrue, whenFalse) =>
          Term.Select(
            condition,
            pushed(Term.Call(method, Vec(whenTrue))),
            pushed(Term.Call(method, Vec(whenFalse)))
          )
        case other => Term.Call(method, Vec(other))
      }
    case _ => term
  }

  /** Whether `term` prints as a selection, which needs parentheses as an operand. */
  private def selection(term: Term): Boolean = pushed(term) match {
    case Term.Select(_, _, _) => true
    case _                    => false
  }

  /** `body` as the statements of a braced block, one a line, indented. */
  private def block(body: Vec[Term.Step]): String = {
    val lines = body.map { step =>
      val statement = show(step.value).replace("\n", "\n  ")
      if (step.name.isEmpty) statement else s"val ${identifier(step.name)} = $statement"
    }
    lines.mkString("{\n  ", "\n  ", "\n}")
  }

  def show(term: Term): String = {
    def atom(term: Term): String = term match {
      case _ if selection(term) => s"(${show(term)})"
      case Term.Variable(_) | Term.Literal(_) | Term.Call(_, _) | Term.Primitive(_, _, _, _, _) | Term.Constant(_) |
          Term.Context(_, _, _, _, _, _, _, _) =>
        show(term)
      case _ => s"(${show(term)})"
    }
    def operand(parent: String, child: Term, side: Side): String = child match {
      case Term.Binary(operator, _, _) =>
        val bare =
          (Arithmetic(operator) && precedence(operator) > precedence(parent)) ||
            ((parent == "&&" || parent == "||") && Comparison(operator)) ||
            (operator == parent && Associative(operator) && side == Side.Left) ||
            (side == Side.Left && Additive(operator) && Additive(parent))
        if (bare) show(child) else s"(${show(child)})"
      case _ if selection(child) => s"(${show(child)})"
      case _                     => show(child)
    }
    term match {
      case Term.Variable(name)                                                                => identifier(name)
      case Term.Literal(value)                                                                => literal(value)
      case Term.Constant(code)                                                                => unqualified(code)
      case call @ Term.Call(method, Vec(_)) if method.startsWith(".") && pushed(call) != call => show(pushed(call))
      case Term.Call(method, Vec(receiver)) if method.startsWith(".") => s"${atom(receiver)}$method"
      case Term.Call(function, arguments)                 => arguments.map(show(_)).mkString(s"$function(", ", ", ")")
      case Term.Primitive(owner, method, arguments, _, _) =>
        arguments.map(show(_)).mkString(s"${Term.simple(owner)}.$method(", ", ", ")")
      case Term.Context(context = context, method = method, typeArguments = typeArguments, arguments = arguments) =>
        val types = if (typeArguments.isEmpty) "" else typeArguments.map(show(_)).mkString("[", ", ", "]")
        arguments.map(show(_)).mkString(s"${identifier(context)}.$method$types(", ", ", ")")
      case Term.Unary(operator, value)                              => s"$operator${atom(value)}"
      case Term.Binary("+", left, Term.Literal(value)) if value < 0 =>
        s"${operand("-", left, Side.Left)} - ${literal(-value)}"
      case Term.Binary("+", left, Term.Binary("-", Term.Literal(0L), right)) =>
        s"${operand("-", left, Side.Left)} - ${operand("-", right, Side.Right)}"
      case Term.Binary("-", Term.Literal(0L), right) => s"-${atom(right)}"
      case Term.Binary(operator, left, right)        =>
        s"${operand(operator, left, Side.Left)} $operator ${operand(operator, right, Side.Right)}"
      case Term.Select(condition, whenTrue, whenFalse) =>
        s"if (${show(condition)}) ${show(whenTrue)} else ${show(whenFalse)}"
      case Term.Let(_, _, _)                 => show(term.expand)
      case Term.Each(variable, source, body) => s"for (${identifier(variable)} <- ${show(source)}) ${block(body)}"
      case Term.When(condition, body)        => s"if (${show(condition)}) ${block(body)}"
    }
  }
}
