package net.kurobako.gemstone.semantics.render

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import scala.annotation.tailrec

/** Types rendered terms over [[prelude]]. `+`, `-`, `*`, `<<`, `~` and unary `-` wrap at their operands' width, so a
  * semantics that truncates their result to that width reads as the bare operation, while one that needs the exact
  * value widens its operands first.
  */
private[gemstone] object Typer {
  private val Extension = raw"(zext|sext)(\d+)".r
  private val Counting  = raw"(clz|ctz|brev)(\d+)".r
  private val High      = raw"mulhi\.([us])(\d+)".r
  private val Extreme   = raw"(min|max)\.([us])".r
  private val Field     = raw"bfe\.([us])".r

  /** A typed term: `bits` bounds the value when it is non-negative and is 64 otherwise; `exact` is present when the
    * term wraps at its width but the semantics needs its exact value, which it computes over 64-bit operands.
    */
  private case class Typed(term: Term, tpe: Option[Type], bits: Int, exact: Option[Term]) {
    def signed: Boolean = tpe.exists {
      case Type.Integer(_, signed) => signed
      case _                       => false
    }
    def width: Int = tpe.fold(64) {
      case Type.Integer(width, _) => width
      case Type.Float(width)      => width
      case Type.Encoded(format)   => format.width
      case _                      => 1
    }
    def boolean: Boolean  = tpe.contains(Type.Bool)
    def floating: Boolean = tpe.exists {
      case Type.Float(_) | Type.Encoded(_) => true
      case _                               => false
    }

    /** The value as the semantics computes it, never wrapped. */
    def settled: Typed = exact.fold(this)(term => Typed(term, Some(Type.Integer(64, signed)), bits, None))

    /** The operand of an operation that must not wrap. */
    def widened: Term = exact.getOrElse(tpe match {
      case Some(Type.Integer(width, signed)) if width < 64 => convert(term, Type.Integer(64, signed))
      case Some(Type.Bool)                                 => convert(term, Type.unsigned(64))
      case _                                               => term
    })
  }

  private def literalBits(value: Long): Int =
    if (value < 0) 64 else math.max(1, 64 - java.lang.Long.numberOfLeadingZeros(value))

  private def convert(term: Term, to: Type): Term = Term.Call(s".to[${ScalaPrinter.show(to)}]", Vec(term))

  private def width(value: Int): Int in 1 ~ 64 = math.min(64, math.max(1, value)).unsafe[Int in 1 ~ 64]

  /** `value` as `tpe`, converting unless it already is: integers truncate or extend, floats and integers of one width
    * reinterpret their bits, and literals construct the type.
    */
  private def asType(value: Typed, tpe: Type): Term = (value.tpe, tpe.unranged) match {
    case (current, target) if current.contains(target) => value.term
    case (None, Type.Bool)                             => Term.Binary("!=", value.term, Term.Literal(0L))
    case (None, Type.Integer(width, true))             => Term.Call(s"SInt[$width]", Vec(value.term))
    case (None, Type.Integer(width, false))            => Term.Call(s"UInt[$width]", Vec(value.term))
    case (None, Type.Float(width))                     => Term.Call(s"FloatingPoint[$width]", Vec(value.term))
    case (Some(Type.Float(found)), _)                  =>
      asType(Typed(Term.Call(".bits", Vec(value.term)), Some(Type.unsigned(found)), found, None), tpe)
    case (Some(Type.Encoded(format)), _) =>
      val bits = Type.unsigned(Type.width(format.width))
      asType(Typed(Term.Call(".bits", Vec(value.term)), Some(bits), format.width, None), tpe)
    case (_, Type.Encoded(format)) =>
      Term.Call(Type.name(format), Vec(asType(value, Type.unsigned(Type.width(format.width)))))
    case (Some(Type.Integer(found, false)), Type.Float(width)) if found == width =>
      Term.Call(".asFloat", Vec(value.settled.term))
    case (Some(_), Type.Float(width)) =>
      Term.Call(".asFloat", Vec(asType(value, Type.unsigned(width))))
    case (Some(Type.Bool), Type.Bool)                                       => value.term
    case (Some(_), Type.Bool)                                               => nonZero(numeric(value).settled)
    case (Some(_), target @ Type.Integer(width, _)) if width <= value.width => convert(value.term, target)
    case (_, target)                                                        => convert(value.settled.term, target)
  }

  /** `value` typed over `types` as a destination of type `declared` holds it. */
  def settle(value: Term, types: Map[String, Type], declared: Option[Type]): (tpe: Type, term: Term) = {
    val found = typed(value, types)
    declared.map(_.unranged) match {
      case Some(Type.Integer(width, _)) if found.boolean                         => (Type.Bool, found.term)
      case Some(Type.Integer(width, _)) if found.tpe.nonEmpty && !found.floating =>
        val fitted = if (found.width == width.raw) found else truncated(found, width.raw)
        fitted.tpe match {
          case Some(tpe @ Type.Integer(bits, _)) if bits == width => (tpe, fitted.term)
          case _                                                  =>
            val target = Type.unsigned(width)
            (target, asType(fitted, target))
        }
      case Some(target) => (target, asType(found.settled, target))
      case None         =>
        val settled = found.settled
        (settled.tpe.getOrElse(Type.signed(64)), settled.tpe.fold(asType(found, Type.signed(64)))(_ => settled.term))
    }
  }

  /** `value` typed over `types`, as it computes. */
  def typedTerm(value: Term, types: Map[String, Type]): Term = typed(value, types).settled.term

  /** `value`, which must hold. */
  def condition(value: Term, types: Map[String, Type]): Term = boolean(typed(value, types))

  private def numeric(value: Typed): Typed =
    if (value.boolean) Typed(convert(value.term, Type.unsigned(1)), Some(Type.unsigned(1)), 1, None)
    else if (value.floating)
      Typed(Term.Call(".bits", Vec(value.term)), Some(Type.unsigned(width(value.width))), value.width, None)
    else value

  private def boolean(value: Typed): Term =
    if (value.boolean) value.term else nonZero(numeric(value).settled)

  /** Whether `value` is non-zero, against a zero of its own type. */
  private def nonZero(value: Typed): Term =
    Term.Binary(
      "!=",
      value.term,
      value.tpe.fold(Term.Literal(0L))(tpe => asType(Typed(Term.Literal(0L), None, 1, None), tpe))
    )

  /** The low `width` bits, unsigned. */
  private def truncated(value: Typed, width: Int): Typed = {
    val target = Type.unsigned(Typer.width(width))
    numeric(value) match {
      case Typed(Term.Literal(literal), None, _, _) =>
        val folded = Bits.truncate(literal, Typer.width(width))
        Typed(Term.Literal(folded), None, literalBits(folded), None)
      case operand if operand.tpe.isEmpty                                               => operand
      case operand if !operand.signed && operand.exact.isEmpty && operand.bits <= width => operand
      case operand if !operand.signed && operand.width == width                         =>
        Typed(operand.term, Some(target), width, None)
      case operand if operand.width >= width => Typed(convert(operand.term, target), Some(target), width, None)
      case operand if width == 64 && !operand.settled.signed => operand.settled
      case operand                                           =>
        Typed(convert(operand.settled.term, target), Some(target), width, None)
    }
  }

  /** The low `width` bits, sign-extended. */
  private def extended(value: Typed, width: Int): Typed = {
    val target = Type.signed(Typer.width(width))
    numeric(value) match {
      case operand if operand.tpe.isEmpty                                               => operand
      case operand if operand.signed && operand.exact.isEmpty && operand.width <= width => operand
      case operand if !operand.signed && operand.exact.isEmpty && operand.bits < width  => operand
      case operand if operand.signed && operand.width == width => Typed(operand.term, Some(target), 64, None)
      case operand if operand.width >= width => Typed(convert(operand.term, target), Some(target), 64, None)
      case operand                           => Typed(convert(operand.settled.term, target), Some(target), 64, None)
    }
  }

  /** A binary operation on two literals, evaluated as execution evaluates it. */
  private def folded(operator: String, left: Long, right: Long): Option[Long] = operator match {
    case "+"   => Some(left + right)
    case "-"   => Some(left - right)
    case "*"   => Some(left * right)
    case "&"   => Some(left & right)
    case "|"   => Some(left | right)
    case "^"   => Some(left ^ right)
    case "<<"  => Some(left << right.toInt)
    case ">>u" => Some(left >>> right.toInt)
    case ">>s" => Some(left >> right.toInt)
    case _     => None
  }

  /** A literal operand as the type `tpe` of the other operand, or as a 64-bit value of its signedness where it does not
    * fit, since operators apply to a literal only on their right.
    */
  private def literal(value: Typed, tpe: Type): Typed = (value.term, tpe.unranged) match {
    case (Term.Literal(number), Type.Integer(width, signed)) if value.tpe.isEmpty =>
      val fits =
        width.raw == 64 ||
          (if (signed) number >= -(1L << (width - 1)) && number < (1L << (width - 1))
           else number >= 0 && number < (1L << width))
      val target = if (fits) tpe.unranged else Type.Integer(64, signed)
      Typed(asType(value, target), Some(target), value.bits, None)
    // an untyped expression of literals, such as a selection, holds no more than its bound
    case (Term.Constant(_), _)                                 => value
    case (_, Type.Integer(width, signed)) if value.tpe.isEmpty =>
      val fits   = width.raw == 64 || (value.bits < 64 && value.bits <= width - (if (signed) 1 else 0))
      val target = if (fits) tpe.unranged else Type.Integer(64, signed)
      Typed(asType(value, target), Some(target), value.bits, None)
    case _ => value
  }

  /** Whether `value` holds in `tpe` without wrapping, as a literal must to be constructed there. */
  private def holds(value: Typed, tpe: Type): Boolean = value.tpe.nonEmpty || literal(value, tpe).tpe.forall(_ == tpe)

  /** A shift amount, which is unsigned. */
  private def unsigned(amount: Typed): Typed =
    if (amount.signed) Typed(convert(amount.term, Type.unsigned(64)), Some(Type.unsigned(64)), 64, None) else amount

  private def typed(term: Term, types: Map[String, Type]): Typed = term match {
    case Term.Binary(operator, Term.Literal(left), Term.Literal(right)) if folded(operator, left, right).nonEmpty =>
      typed(Term.Literal(folded(operator, left, right).getOrElse(0L)), types)
    case Term.Variable(name) =>
      types.get(name).map(_.unranged) match {
        case Some(tpe @ Type.Integer(width, signed)) => Typed(term, Some(tpe), if (signed) 64 else width, None)
        case Some(tpe @ Type.Float(width))           => Typed(term, Some(tpe), width, None)
        case Some(tpe @ Type.Encoded(format))        => Typed(term, Some(tpe), format.width, None)
        case Some(tpe)                               => Typed(term, Some(tpe), 1, None)
        case None                                    => Typed(term, Some(Type.unsigned(64)), 64, None)
      }
    case Term.Literal(value)                            => Typed(term, None, literalBits(value), None)
    case Term.Constant(_)                               => Typed(term, None, 64, None)
    case Term.Call(Extension("zext", bits), Vec(value)) => truncated(typed(value, types), bits.toInt)
    case Term.Call(Extension(_, bits), Vec(value))      => extended(typed(value, types), bits.toInt)
    case Term.Binary("&", value, Term.Literal(mask)) if mask > 0 && (mask & (mask + 1)) == 0L =>
      truncated(typed(value, types), literalBits(mask))
    case Term.Binary(operator @ ("+" | "-" | "*" | "<<"), left, right) =>
      arithmetic(operator, numeric(typed(left, types)), numeric(typed(right, types)))
    case Term.Binary(operator @ ("&" | "|" | "^"), left, right) =>
      bitwise(operator, typed(left, types), typed(right, types))
    case Term.Binary(operator @ (">>u" | ">>s"), left, right) =>
      val amount  = unsigned(numeric(typed(right, types)).settled)
      val shifted = literal(numeric(typed(left, types)).settled, Type.Integer(64, operator == ">>s"))
      val operand =
        if (operator == ">>u" && shifted.signed)
          Typed(convert(shifted.term, Type.unsigned(64)), Some(Type.unsigned(64)), 64, None)
        else if (operator == ">>s" && !shifted.signed && shifted.bits == 64)
          Typed(convert(shifted.term, Type.signed(64)), Some(Type.signed(64)), 64, None)
        else shifted
      val bits = amount.term match {
        case Term.Literal(count) if operand.bits < 64 => math.max(1, operand.bits - count.toInt)
        case _                                        => operand.bits
      }
      Typed(Term.Binary(">>", operand.term, amount.term), operand.tpe.orElse(Some(Type.unsigned(64))), bits, None)
    case Term.Binary(operator @ ("==" | "!="), left, right) =>
      val (l, r) = (typed(left, types), typed(right, types))
      (l, r.term, operator) match {
        case (value, Term.Literal(0L), "!=") if value.boolean => value
        case (value, Term.Literal(0L), "==") if value.boolean =>
          Typed(Term.Unary("!", value.term), Some(Type.Bool), 1, None)
        case _ =>
          val (x, y)    = (numeric(l).settled, numeric(r).settled)
          val (a, b, _) = unified(y.tpe.fold(x)(literal(x, _)), x.tpe.fold(y)(literal(y, _)))
          Typed(Term.Binary(operator, a.term, b.term), Some(Type.Bool), 1, None)
      }
    case Term.Binary(operator, left, right) if operator.startsWith("<") =>
      val signed                                        = operator.endsWith("s")
      val target                                        = Type.Integer(width(64), signed)
      def operand(settled: Typed): (Term, Option[Type]) =
        if (signed && !settled.signed && settled.bits == 64 && settled.tpe.nonEmpty)
          (convert(settled.term, target), Some(target))
        else if (!signed && settled.signed) (convert(settled.term, target), Some(target))
        else (settled.term, settled.tpe)
      val comparison               = operator.dropRight(1)
      val (a, b)                   = (numeric(typed(left, types)).settled, numeric(typed(right, types)).settled)
      val ((x, xType), (y, yType)) = (operand(a), operand(b))
      // operands of different types meet at 64 bits, as execution compares them
      val (l, r) =
        if (xType.nonEmpty && yType.nonEmpty && xType != yType)
          (
            if (a.tpe.contains(target)) a.term else convert(a.term, target),
            if (b.tpe.contains(target)) b.term else convert(b.term, target)
          )
        else (x, y)
      val ordered = l match {
        case Term.Literal(_) => Term.Binary(comparison.replace('<', '>'), r, l)
        case _               => Term.Binary(comparison, l, r)
      }
      Typed(ordered, Some(Type.Bool), 1, None)
    case Term.Binary(operator, left, right) =>
      val (l, r)                      = (numeric(typed(left, types)).settled, numeric(typed(right, types)).settled)
      val signed                      = operator.endsWith("s")
      def operand(value: Typed): Term =
        if (signed != value.signed && value.bits == 64 && value.tpe.nonEmpty)
          convert(value.term, Type.Integer(64, signed))
        else value.term
      Typed(
        Term.Binary(operator.stripSuffix("s").stripSuffix("u"), operand(l), operand(r)),
        l.tpe.orElse(r.tpe),
        l.bits,
        None
      )
    case Term.Unary("~", Term.Literal(value))      => typed(Term.Literal(~value), types)
    case Term.Unary("-", Term.Literal(value))      => typed(Term.Literal(-value), types)
    case Term.Unary(operator @ ("~" | "-"), value) =>
      val operand = typed(value, types)
      if (operand.boolean && operator == "~") Typed(Term.Unary("!", operand.term), Some(Type.Bool), 1, None)
      else {
        val number = numeric(operand)
        if (number.signed || number.width == 64 || number.tpe.isEmpty)
          Typed(Term.Unary(operator, number.term), number.tpe, 64, None)
        else Typed(Term.Unary(operator, number.term), number.tpe, 64, Some(Term.Unary(operator, number.widened)))
      }
    case Term.Unary(operator, value) =>
      val operand = numeric(typed(value, types)).settled
      Typed(Term.Unary(operator, operand.term), operand.tpe, 64, None)
    case Term.Select(condition, whenTrue, whenFalse) =>
      val (yes, no, tpe) = branches(typed(whenTrue, types).settled, typed(whenFalse, types).settled)
      val (a, b)         = tpe.fold((yes.term, no.term))(target => (asType(yes, target), asType(no, target)))
      Typed(Term.Select(boolean(typed(condition, types)), a, b), tpe, math.max(yes.bits, no.bits), None)
    case Term.Call(Counting(kind, bits), Vec(value)) =>
      val operand = truncated(typed(value, types), bits.toInt)
      val tpe     = Type.unsigned(width(bits.toInt))
      val method  = kind match {
        case "clz" => ".leadingZeros"
        case "ctz" => ".trailingZeros"
        case _     => ".reverseBits"
      }
      Typed(Term.Call(method, Vec(asType(operand, tpe))), Some(tpe), bits.toInt, None)
    case Term.Call("popc", Vec(value)) =>
      val operand = numeric(typed(value, types)).settled
      val counted = if (operand.signed || operand.tpe.isEmpty) asType(operand, Type.unsigned(64)) else operand.term
      Typed(
        Term.Call(".bitCount", Vec(counted)),
        operand.tpe.filterNot(_ => operand.signed).orElse(Some(Type.unsigned(64))),
        7,
        None
      )
    case Term.Call(High(kind, bits), Vec(left, right)) =>
      val tpe                        = Type.Integer(width(bits.toInt), kind == "s")
      def operand(value: Term): Term =
        if (kind == "s") asType(extended(typed(value, types), bits.toInt), tpe)
        else asType(truncated(typed(value, types), bits.toInt), tpe)
      Typed(
        Term.Call("multiplyHigh", Vec(operand(left), operand(right))),
        Some(tpe),
        if (kind == "s") 64 else bits.toInt,
        None
      )
    case Term.Call(Extreme(kind, signedness), Vec(left, right)) =>
      val (l, r) = (numeric(typed(left, types)).settled, numeric(typed(right, types)).settled)
      val signed = signedness == "s"
      val tpe    = (l.tpe, r.tpe) match {
        case (Some(a @ Type.Integer(_, s)), Some(b)) if a == b && s == signed   => a
        case (Some(a @ Type.Integer(_, s)), None) if s == signed && holds(r, a) => a
        case (None, Some(b @ Type.Integer(_, s))) if s == signed && holds(l, b) => b
        case _                                                                  => Type.Integer(64, signed)
      }
      Typed(
        Term.Call(kind, Vec(asType(l, tpe), asType(r, tpe))),
        Some(tpe),
        if (signed) 64 else math.max(l.bits, r.bits),
        None
      )
    case Term.Call(Field(signedness), Vec(value, offset, count)) =>
      val tpe       = Type.Integer(64, signedness == "s")
      val arguments = Vec(
        asType(numeric(typed(value, types)).settled, tpe),
        asType(numeric(typed(offset, types)).settled, Type.unsigned(64)),
        asType(numeric(typed(count, types)).settled, Type.unsigned(64))
      )
      Typed(Term.Call("bitFieldExtract", arguments), Some(tpe), 64, None)
    case Term.Call("blend", Vec(mask, whenSet, whenClear)) =>
      val (m, a) = (numeric(typed(mask, types)).settled, numeric(typed(whenSet, types)).settled)
      val b      = numeric(typed(whenClear, types)).settled
      val tpe    = a.tpe.orElse(b.tpe).orElse(m.tpe) match {
        case Some(tpe @ Type.Integer(_, signed)) if !Vec(m, a, b).forall(holds(_, tpe)) => Type.Integer(64, signed)
        case tpe => tpe.getOrElse(Type.unsigned(64))
      }
      Typed(Term.Call("blend", Vec(asType(m, tpe), asType(a, tpe), asType(b, tpe))), Some(tpe), 64, None)
    case Term.Call(function, arguments) =>
      Typed(
        Term.Call(function, arguments.map(argument => typed(argument, types).settled.term)),
        Some(Type.unsigned(64)),
        64,
        None
      )
    case Term.Primitive(owner, method, arguments, parameters, result) =>
      val converted = converting(arguments, parameters, types)
      val bits      = result match {
        case Type.Integer(width, false) => width.raw
        case _                          => 64
      }
      Typed(Term.Primitive(owner, method, converted, parameters, result), Some(result), bits, None)
    case Term.Context(contextType, context, method, typeArguments, arguments, parameters, result, effect) =>
      val converted = converting(arguments, parameters, types)
      val bits      = result match {
        case Some(Type.Integer(width, false)) => width.raw
        case _                                => 64
      }
      Typed(
        Term.Context(contextType, context, method, typeArguments, converted, parameters, result, effect),
        result,
        bits,
        None
      )
    case Term.Let(_, _, _) => typed(term.expand, types)
  }

  /** The branches of a selection at one type: truth values stay so unless the other branch is a number. */
  private def branches(yes: Typed, no: Typed): (Typed, Typed, Option[Type]) = {
    def truth(value: Typed): Boolean =
      value.boolean || (value.tpe.isEmpty && (value.term == Term.Literal(0L) || value.term == Term.Literal(1L)))
    val (x, y) = if (truth(yes) && truth(no)) (yes, no) else (numeric(yes), numeric(no))
    unified(y.tpe.fold(x)(literal(x, _)), x.tpe.fold(y)(literal(y, _)))
  }

  /** Both operands at the wider one's width, converting the narrower so it keeps its value; a literal on the left takes
    * the right's type.
    */
  @tailrec
  private def unified(left: Typed, right: Typed): (Typed, Typed, Option[Type]) = (left.tpe, right.tpe) match {
    case (None, None)                                                              => (left, right, None)
    case (None, Some(tpe @ Type.Integer(_, _))) if literal(left, tpe).tpe.nonEmpty => unified(literal(left, tpe), right)
    case (None, tpe)                                                               => (left, right, tpe)
    // a literal the left's type cannot hold widens the operation, which would otherwise wrap it
    case (Some(tpe @ Type.Integer(_, _)), None) if literal(right, tpe).tpe.exists(_ != tpe) =>
      val target = literal(right, tpe).tpe.getOrElse(tpe)
      (Typed(left.exact.getOrElse(convert(left.term, target)), Some(target), left.bits, None), right, Some(target))
    case (tpe, None)                                                      => (left, right, tpe)
    case (Some(Type.Integer(l, ls)), Some(Type.Integer(r, rs))) if l == r =>
      val target = Type.Integer(l, ls && rs)
      // a value of the other signedness keeps its exact value in 64 bits
      def lift(value: Typed): Typed =
        if (value.tpe.contains(target)) value
        else Typed(asType(value, target), Some(target), 64, Some(convert(value.widened, Type.Integer(64, ls && rs))))
      (lift(left), lift(right), Some(target))
    case (Some(Type.Integer(l, ls)), Some(Type.Integer(r, rs))) =>
      val signed                    = if (l > r) ls else rs
      val target                    = Type.Integer(width(math.max(l, r)), signed)
      def lift(value: Typed): Typed =
        if (value.width == math.max(l, r) && value.signed == signed) value
        else if (value.signed != signed)
          Typed(
            convert(value.settled.term, target),
            Some(target),
            64,
            Some(convert(value.widened, Type.Integer(64, signed)))
          )
        else
          Typed(
            convert(value.settled.term, target),
            Some(target),
            value.bits,
            Option.when(value.bits > math.max(l, r))(value.widened)
          )
      (lift(left), lift(right), Some(target))
    case (tpe, _) => (left.settled, right.settled, tpe)
  }

  private def converting(arguments: Vec[Term], parameters: Vec[Option[Type]], types: Map[String, Type]): Vec[Term] =
    arguments.zip(parameters).map {
      case (argument, Some(tpe)) => asType(typed(argument, types), tpe)
      case (argument, None)      => argument
    }

  private def integerWidth(tpe: Option[Type]): Int = tpe.fold(64) {
    case Type.Integer(width, _) => width.raw
    case _                      => 64
  }

  private def arithmetic(operator: String, left: Typed, right: Typed): Typed = {
    val (l, r, tpe) =
      if (operator == "<<")
        (literal(left, Type.unsigned(64)), unsigned(right.settled), left.tpe.orElse(Some(Type.unsigned(64))))
      else unified(left, right)
    val width  = integerWidth(tpe)
    val signed = tpe.exists {
      case Type.Integer(_, signed) => signed
      case _                       => false
    }
    val bits = operator match {
      case "+"  => if (l.bits == 64 || r.bits == 64) 64 else math.max(l.bits, r.bits) + 1
      case "*"  => math.min(64, l.bits + r.bits)
      case "<<" =>
        r.term match {
          case Term.Literal(count) if l.bits < 64 => math.min(64, l.bits + count.toInt)
          case _                                  => 64
        }
      case _ => 64
    }
    val term  = Term.Binary(operator, l.term, r.term)
    val exact = Option.when(width < 64 && tpe.nonEmpty && (bits > width || (operator == "-" && !signed)))(
      Term.Binary(operator, l.widened, if (operator == "<<") r.term else r.widened)
    )
    Typed(term, tpe, if (exact.nonEmpty) bits else if (signed) 64 else math.min(bits, width), exact)
  }

  private def bitwise(operator: String, left: Typed, right: Typed): Typed =
    if (left.boolean && right.boolean) {
      val logical = operator match {
        case "&" => "&&"
        case "|" => "||"
        case _   => "!="
      }
      Typed(Term.Binary(logical, left.term, right.term), Some(Type.Bool), 1, None)
    } else if (operator == "^" && left.boolean && right.term == Term.Literal(1L))
      Typed(Term.Unary("!", left.term), Some(Type.Bool), 1, None)
    else {
      val (x, y) = (numeric(left), numeric(right))
      // a mask cannot set bits the other operand lacks, so it never widens the operation
      val (l, r, tpe) = if (operator == "&" && x.tpe.nonEmpty && y.tpe.isEmpty) (x, y, x.tpe) else unified(x, y)
      val bits        = if (operator == "&") math.min(l.bits, r.bits) else math.max(l.bits, r.bits)
      val width       = integerWidth(tpe)
      val exact       = Option.when(bits > width && width < 64 && (l.exact.nonEmpty || r.exact.nonEmpty))(
        Term.Binary(operator, l.widened, r.widened)
      )
      Typed(Term.Binary(operator, l.term, r.term), tpe, bits, exact)
    }
}
