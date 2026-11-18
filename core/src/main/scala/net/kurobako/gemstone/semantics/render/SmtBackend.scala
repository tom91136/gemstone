package net.kurobako.gemstone.semantics.render

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import scala.annotation.tailrec
import scala.collection.mutable
import scala.reflect.ClassTag

/** SMT-LIB encoding of definitions for equivalence queries: bit-vectors for integers, the floating-point theory for
  * floating point, and bounded arrays for memory.
  *
  * SMT-LIB cannot read a floating-point value's bits back, so every floating-point value carries the bit-vector its
  * operation produced, tied to the operation's result by an equality. The payload of a NaN the rendered semantics
  * leaves open is then the solver's choice, as it is on the machine.
  */
object SmtBackend extends Backend {

  private enum Value derives CanEqual {
    case Numeral(value: Long)
    case Bitvector(sexp: String, width: Int, signed: Boolean)
    case Floating(sexp: String, bits: String, format: FpFormat)
    case Truth(sexp: String)
  }

  private case class FpFormat(exponent: Int, significand: Int) derives CanEqual {
    val width: Int     = exponent + significand
    val signBit: Long  = 1L << (width - 1)
    val quietBit: Long = 1L << (significand - 2)
    val infinity: Long = ((1L << exponent) - 1L) << (significand - 1)
    val one: Long      = ((1L << (exponent - 1)) - 1L) << (significand - 1)
    val sort: String   = s"(_ FloatingPoint $exponent $significand)"
    val toSort: String = s"(_ to_fp $exponent $significand)"
  }

  private object FpFormat {
    def of(width: Int): Option[FpFormat] = width match {
      case 16 => Some(FpFormat(exponent = 5, significand = 11))
      case 32 => Some(FpFormat(exponent = 8, significand = 24))
      case 64 => Some(FpFormat(exponent = 11, significand = 53))
      case _  => None
    }
  }

  private case class Encoding(
      lines: List[String],
      results: List[(name: String, compared: String, tpe: Type)],
      accessed: Set[String],
      stored: Map[String, String],
      floating: Boolean
  )

  private val Memory = "(Array (_ BitVec 64) (_ BitVec 8))"

  /** The constant banks, which nothing stores to: one memory of every bank's bytes, a bank above its offsets. */
  private val Constants    = "Constants"
  private val Converted    = raw"\.to\[(UInt|SInt)\[(\d+)\]\]".r
  private val Constructed  = raw"(UInt|SInt)\[(\d+)\]".r
  private val FromFloating = raw"(?:FloatingPoint|Float)\[(\d+)\]".r
  private val Suffixed     = "Float(16|32|64)".r
  private val Extreme      = raw"(min|max)\.([us])".r

  private val Arithmetic = Map(
    "+" -> "bvadd",
    "-" -> "bvsub",
    "*" -> "bvmul",
    "&" -> "bvand",
    "|" -> "bvor",
    "^" -> "bvxor"
  )
  private val Logic       = Map("&" -> "and", "|" -> "or", "^" -> "xor")
  private val Conditional = Map("&&" -> "and", "||" -> "or")
  private val Orders      = Map(
    "<"  -> ("bvult", "bvslt"),
    "<=" -> ("bvule", "bvsle"),
    ">"  -> ("bvugt", "bvsgt"),
    ">=" -> ("bvuge", "bvsge")
  )

  def show(definition: Definition): String = (for {
    encoding <- encode(definition, "")
    header   <- declarations(definition.parameters, encoding.accessed.toList)
  } yield (header ++ encoding.lines).mkString("\n")).fold(
    reason => throw IllegalArgumentException(reason),
    identity
  )

  def source(definition: Definition): String = show(definition)

  /** A query that is unsatisfiable exactly when `left` and `right` agree on every input where both encode, on their
    * final memory as well when `memory` is live-out.
    */
  def equivalence(left: Definition, right: Definition, memory: Boolean): Either[String, String] = for {
    l          <- encode(left, "l_")
    r          <- encode(right, "r_")
    parameters <- union(left.parameters, right.parameters)
    pairs      <- pairResults(l.results, r.results)
    header     <- declarations(parameters, (l.accessed ++ r.accessed).toList)
  } yield {
    val differences = pairs.map((a, b) => s"(distinct $a $b)") ++
      (if (memory) (l.stored.keys ++ r.stored.keys).toList.distinct else Nil).map(space =>
        s"(distinct ${finalOf(l, space)} ${finalOf(r, space)})"
      )
    val values = parameters.toList.map(parameter => symbol(parameter.name))
    val logic  = if (l.floating || r.floating) "(set-logic QF_AUFBVFP)" else "(set-logic QF_ABV)"
    (List(logic, "(set-option :produce-models true)") ++
      header ++ l.lines ++ r.lines ++
      List(assertion(differences), "(check-sat)") ++
      (if (values.isEmpty) Nil else List(s"(get-value (${values.mkString(" ")}))"))).mkString("\n")
  }

  private def encode(definition: Definition, prefix: String): Either[String, Encoding] =
    Encoder(prefix, definition).run()

  private class Encoder(prefix: String, definition: Definition) {
    import Value.*

    private val inputs   = definition.parameters.map(_.name).toSet
    private val values   = mutable.HashMap.empty[String, Value]
    private val lines    = mutable.ArrayBuffer.empty[String]
    private val accessed = mutable.LinkedHashSet.empty[String]
    private val stored   = mutable.LinkedHashMap.empty[String, String]
    private val formats  = mutable.HashSet.empty[FpFormat]
    private val counter  = Iterator.from(0)

    def run(): Either[String, Encoding] = for {
      _       <- collect(definition.parameters)(declare(_))
      _       <- collect(definition.body)(step => statement(step.target, step.term, None))
      _       <- collect(definition.requirements)(requirement(_))
      results <- collect(definition.results)(output(_))
    } yield Encoding(lines.toList, results.toList, accessed.toSet, stored.toMap, formats.nonEmpty)

    private def emit(line: String): Unit = lines.append(line)

    private def declare(parameter: (name: String, tpe: Type)): Either[String, Unit] =
      parameter.tpe.unranged match {
        case Type.Float(width) =>
          FpFormat.of(width).toRight(s"binary$width has no SMT floating-point format").map(format =>
            values(parameter.name) = fromBits(symbol(parameter.name), format)
          )
        case other =>
          shapeOf(other).map { case (width, signed) =>
            values(parameter.name) = Bitvector(symbol(parameter.name), width, signed)
          }
      }

    private def requirement(term: Term): Either[String, Unit] = for {
      value     <- expression(term)
      condition <- conditionOf(term, value)
    } yield emit(s"(assert $condition)")

    private def output(result: (name: String, tpe: Type, value: Term)): Either[String, (String, String, Type)] =
      result.tpe.unranged match {
        case Type.Float(width) => for {
            format <- FpFormat.of(width).toRight(s"binary$width has no SMT floating-point format")
            value  <- floatingTerm(result.value)
          } yield {
            val target = outputSymbol(result.name)
            emit(s"(define-fun $target () ${format.sort} ${value.sexp})")
            (result.name, value.bits, result.tpe)
          }
        case other => for {
            shape <- shapeOf(other)
            value <- expression(result.value)
          } yield {
            val target = outputSymbol(result.name)
            emit(s"(define-fun $target () (_ BitVec ${shape._1}) ${bits(value, shape._1)})")
            (result.name, target, result.tpe)
          }
      }

    private def outputSymbol(name: String): String =
      if (prefix.isEmpty && inputs(name)) symbol(s"result_$name") else symbol(prefix + name)

    private def statement(target: Definition.Target, term: Term, guard: Option[String]): Either[String, Unit] =
      target match {
        case Definition.Target.Binding(name) => binding(name, term)
        case Definition.Target.Method(name)  => binding(name, term)
        case Definition.Target.Effect        => effect(term, guard)
      }

    private def binding(name: String, term: Term): Either[String, Unit] =
      expression(term).map { value =>
        values(name) = define(symbol(prefix + name), value)
      }

    @tailrec
    private def define(target: String, value: Value): Value = value match {
      case Numeral(number)                => define(target, Bitvector(literal(number, 64), 64, signed = true))
      case Bitvector(sexp, width, signed) =>
        emit(s"(define-fun $target () (_ BitVec $width) $sexp)")
        Bitvector(target, width, signed)
      case Floating(sexp, bits, format) =>
        emit(s"(define-fun $target () ${format.sort} $sexp)")
        Floating(target, bits, format)
      case Truth(sexp) =>
        emit(s"(define-fun $target () Bool $sexp)")
        Truth(target)
    }

    private def effect(term: Term, guard: Option[String]): Either[String, Unit] = term match {
      case Term.Select(condition, inner, Term.Constant("()")) =>
        expression(condition).flatMap(value => conditionOf(condition, value)).flatMap(condition =>
          effect(inner, Some(conjoin(guard, condition)))
        )
      case Term.When(condition, body) =>
        expression(condition).flatMap(value => conditionOf(condition, value)).flatMap { condition =>
          val inner = Some(conjoin(guard, condition))
          collect(body)(step =>
            statement(
              if (step.name.isEmpty) Definition.Target.Effect else Definition.Target.Binding(step.name),
              step.value,
              inner
            )
          ).map(_ => ())
        }
      case Term.Context(
            contextType = contextType,
            method = "store",
            typeArguments = Vec(tpe),
            arguments = Vec(space, address, value)
          ) if isMachine(contextType) =>
        for {
          width <- whole(term, tpe.bits)
          key   <- spaceKey(space)
          a     <- expression(address)
          v     <- expression(value)
        } yield store(key, bits(a, 64), bits(v, width), width, guard)
      case Term.Context(contextType = contextType, method = "storeBytes", arguments = Vec(space, address, data))
          if isMachine(contextType) =>
        for {
          key   <- spaceKey(space)
          a     <- expression(address)
          d     <- expression(data)
          width <- d match {
            case Bitvector(_, width, _) => whole(term, width)
            case _                      => Left(s"${term.show} stores a value that is not a bit-vector")
          }
        } yield store(key, bits(a, 64), bits(d, width), width, guard)
      case other => Left(s"${other.show} is not a supported effect")
    }

    private def store(space: String, address: String, data: String, width: Int, guard: Option[String]): Unit = {
      accessed += space
      val previous = current(space)
      val updated  = (0 until width / 8).foldLeft(previous) { (array, index) =>
        val byte = if (width == 8) data else s"((_ extract ${8 * index + 7} ${8 * index}) $data)"
        s"(store $array ${offset(address, index)} $byte)"
      }
      val target = symbol(s"${prefix}memory_${spaceName(space)}_${counter.next()}")
      val value  = guard.fold(updated)(condition => s"(ite $condition $updated $previous)")
      emit(s"(define-fun $target () $Memory $value)")
      stored(space) = target
    }

    private def read(space: String, address: String, width: Int): String = {
      accessed += space
      val array = current(space)
      (0 until width / 8).map(index => s"(select $array ${offset(address, index)})").reverse.reduceLeft {
        (high, low) => s"(concat $high $low)"
      }
    }

    private def current(space: String): String = stored.getOrElse(space, initial(space))

    private def expression(term: Term): Either[String, Value] = term match {
      case Term.Variable(name)                => values.get(name).toRight(s"$name is unbound")
      case Term.Literal(number)               => Right(Numeral(number))
      case Term.Unary(operator, value)        => expression(value).flatMap(unary(operator, _))
      case Term.Binary(operator, left, right) =>
        for {
          l <- expression(left)
          r <- expression(right)
          v <- binary(operator, l, r)
        } yield v
      case Term.Call(function, arguments) => collect(arguments)(expression(_)).flatMap(call(function, _))
      case Term.Primitive(owner, method, arguments, _, result) =>
        primitive(Term.simple(owner), method, arguments, result)
      case Term.Select(condition, whenTrue, whenFalse) =>
        for {
          c <- expression(condition).flatMap(conditionOf(condition, _))
          t <- expression(whenTrue)
          f <- expression(whenFalse)
          v <- select(c, t, f)
        } yield v
      case Term.Context(
            contextType = contextType,
            method = "load",
            typeArguments = Vec(tpe),
            arguments = Vec(space, address)
          ) if isMachine(contextType) =>
        for {
          key   <- spaceKey(space)
          a     <- expression(address)
          shape <- shapeOf(tpe)
        } yield Bitvector(read(key, bits(a, 64), shape._1), shape._1, shape._2)
      case Term.Context(
            contextType = contextType,
            method = "load",
            typeArguments = Vec(tpe),
            arguments = Vec(bank, at)
          ) if Term.simple(contextType) == Constants =>
        for {
          b     <- expression(bank)
          a     <- expression(at)
          shape <- shapeOf(tpe)
        } yield Bitvector(read(Constants, s"(concat ${bits(b, 32)} ${bits(a, 32)})", shape._1), shape._1, shape._2)
      case other => Left(s"${other.show} is not supported")
    }

    private def floatingTerm(term: Term): Either[String, Value.Floating] =
      expression(term).flatMap {
        case value @ Floating(_, _, _) => Right(value)
        case _                         => Left(s"${term.show} is not a floating-point value")
      }

    private def conditionOf(term: Term, value: Value): Either[String, String] =
      asCondition(value).left.map(reason => s"${term.show}: $reason")

    private def asCondition(value: Value): Either[String, String] = value match {
      case Truth(sexp)               => Right(sexp)
      case Bitvector(sexp, width, _) => Right(s"(distinct $sexp ${literal(0, width)})")
      case Numeral(number)           => Right(if (number != 0) "true" else "false")
      case Floating(_, _, _)         => Left("a floating-point value is not a condition")
    }

    private def select(condition: String, whenTrue: Value, whenFalse: Value): Either[String, Value] =
      (whenTrue, whenFalse) match {
        case (Floating(a, ab, af), Floating(b, bb, bf)) =>
          if (af != bf) Left("a selection mixes floating-point formats")
          else Right(Floating(s"(ite $condition $a $b)", s"(ite $condition $ab $bb)", af))
        case (Truth(a), Truth(b))                           => Right(Truth(s"(ite $condition $a $b)"))
        case _ if floating(whenTrue) || floating(whenFalse) =>
          Left("a selection mixes a floating-point value with another kind")
        case _ =>
          val (width, signed) = common(Vec(whenTrue, whenFalse))
          Right(Bitvector(s"(ite $condition ${bits(whenTrue, width)} ${bits(whenFalse, width)})", width, signed))
      }

    private def unary(operator: String, value: Value): Either[String, Value] = operator match {
      case "!"       => asCondition(value).map(condition => Truth(s"(not $condition)"))
      case "~" | "-" =>
        if (floating(value)) Left(s"unary $operator is not supported on floating point")
        else {
          val (width, signed) = common(Vec(value))
          val name            = if (operator == "~") "bvnot" else "bvneg"
          Right(Bitvector(s"($name ${bits(value, width)})", width, signed))
        }
      case _ => Left(s"unary $operator is not supported")
    }

    private def binary(operator: String, left: Value, right: Value): Either[String, Value] =
      if (floating(left) || floating(right)) Left(s"operator $operator is not supported on floating point")
      else (left, right) match {
        case (Truth(l), Truth(r)) if Logic.contains(operator) =>
          Right(Truth(s"(${Logic(operator)} $l $r)"))
        case _ if Conditional.contains(operator) =>
          for {
            l <- asCondition(left)
            r <- asCondition(right)
          } yield Truth(s"(${Conditional(operator)} $l $r)")
        case _ if Arithmetic.contains(operator) =>
          val (width, signed) = common(Vec(left, right))
          Right(Bitvector(s"(${Arithmetic(operator)} ${bits(left, width)} ${bits(right, width)})", width, signed))
        case _ if operator == "<<" || operator.startsWith(">>") =>
          Right(shift(operator, left, right))
        case _ if operator == "==" || operator == "!=" || Orders.contains(operator) =>
          compare(operator, left, right)
        case _ => Left(s"operator $operator is not supported")
      }

    private def shift(operator: String, value: Value, amount: Value): Value = {
      val (width, signed) = common(Vec(value))
      val count           = s"(bvand ${bits(amount, 64)} ${literal(63, 64)})"
      val kind            = operator match {
        case "<<"  => "bvshl"
        case ">>u" => "bvlshr"
        case ">>s" => "bvashr"
        case _     => if (signed) "bvashr" else "bvlshr"
      }
      Bitvector(fit(s"($kind ${bits(value, 64)} $count)", 64, signed, width), width, signed)
    }

    private def compare(operator: String, left: Value, right: Value): Either[String, Value] = operator match {
      case "==" | "!=" =>
        val (_, l, r) = align(None, left, right)
        Right(Truth(if (operator == "==") s"(= $l $r)" else s"(distinct $l $r)"))
      case _ =>
        val signs = Vec(left, right).flatMap(shape(_)).map(_._2).distinct
        if (signs.size > 1) Left(s"operator $operator compares operands of different signedness")
        else {
          val signed                     = signs.headOption.getOrElse(true)
          val (_, l, r)                  = align(Some(signed), left, right)
          val (unsignedName, signedName) = Orders(operator)
          Right(Truth(s"(${if (signed) signedName else unsignedName} $l $r)"))
        }
    }

    /** Both operands at one width, wide enough that their comparison is exact. */
    private def align(ordering: Option[Boolean], left: Value, right: Value): (Int, String, String) = {
      val shapes = Vec(left, right).flatMap(shape(_))
      val mixed  = shapes.map(_._2).distinct.size > 1
      val width  =
        if (shapes.isEmpty) 64
        else
          ordering match {
            case Some(true) => shapes.map { case (width, signed) => if (signed) width else width + 1 }.max
            case _ if mixed => 64
            case _          => shapes.map(_._1).max
          }
      (width, bits(left, width), bits(right, width))
    }

    private def call(function: String, arguments: Vec[Value]): Either[String, Value] = (function, arguments) match {
      case (Converted(kind, width), Vec(argument))              => Right(resized(kind == "SInt", width, argument))
      case (Constructed(kind, width), Vec(argument))            => Right(resized(kind == "SInt", width, argument))
      case (FromFloating(width), Vec(argument))                 => reinterpret(width.toInt, argument)
      case (Suffixed(width), Vec(argument))                     => reinterpret(width.toInt, argument)
      case (".asFloat", Vec(argument @ Bitvector(_, width, _))) => reinterpret(width, argument)
      case (".asFloat", _)                                      => Left(".asFloat takes a bit-vector operand")
      case (".bits", Vec(Floating(_, bits, format)))            => Right(Bitvector(bits, format.width, signed = false))
      case (Extreme(kind, letter), Vec(left, right))            =>
        if (floating(left) || floating(right)) Left(s"$function takes integer operands")
        else {
          val signed            = letter == "s"
          val (width, l, r)     = align(Some(signed), left, right)
          val less              = if (signed) "bvsle" else "bvule"
          val (whenLess, other) = if (kind == "min") (l, r) else (r, l)
          Right(Bitvector(s"(ite ($less $l $r) $whenLess $other)", width, signed))
        }
      case ("blend", Vec(mask, set, clear)) =>
        if (Vec(mask, set, clear).exists(floating)) Left("blend takes integer operands")
        else {
          val (width, signed) = common(Vec(mask, set, clear))
          val c               = bits(clear, width)
          Right(Bitvector(
            s"(bvxor $c (bvand (bvxor ${bits(set, width)} $c) ${bits(mask, width)}))",
            width,
            signed
          ))
        }
      case (other, _) => Left(s"call $other is not supported")
    }

    private def reinterpret(width: Int, argument: Value): Either[String, Value] = FpFormat.of(width) match {
      case Some(format) => Right(fromBits(bits(argument, width), format))
      case None         => Left(s"binary$width has no SMT floating-point format")
    }

    private def primitive(owner: String, method: String, arguments: Vec[Term], result: Type): Either[String, Value] =
      (owner, method) match {
        case ("FloatArithmetic", "add")              => nvidiaBinary("fp.add", arguments, result)
        case ("FloatArithmetic", "multiply")         => nvidiaBinary("fp.mul", arguments, result)
        case ("FloatArithmetic", "fusedMultiplyAdd") => nvidiaFused(arguments, result)
        case ("FloatArithmetic", "canonical")        => canonical(arguments, result)
        case ("FloatArithmetic", "saturate")         => saturate(arguments, result)
        case ("FloatArithmetic", "flush")            => flushed(arguments, result)
        case ("Floats", "add")                       => amdBinary("fp.add", arguments, result)
        case ("Floats", "sub")                       => amdBinary("fp.sub", arguments, result)
        case ("Floats", "mul")                       => amdBinary("fp.mul", arguments, result)
        case ("Floats", "fma")                       => amdFused(arguments, result)
        case _                                       => Left(s"primitive $owner.$method is not supported")
      }

    /** An NVIDIA sum or product: the IEEE result, with a NaN operand quieted and taken from the right first, and the
      * default NaN where the operation is invalid.
      */
    private def nvidiaBinary(operator: String, arguments: Vec[Term], result: Type): Either[String, Value] = for {
      format <- formatOf(result)
      left   <- floatingArgument(arguments, 0)
      right  <- floatingArgument(arguments, 1)
    } yield {
      val core = operation(s"($operator RNE ${left.sexp} ${right.sexp})", format)
      fromBits(
        s"(ite (fp.isNaN ${right.sexp}) ${quieted(right.bits, format)} " +
          s"(ite (fp.isNaN ${left.sexp}) ${quieted(left.bits, format)} " +
          s"(ite (fp.isNaN ${core.sexp}) ${literal(nvidiaNaN(format), format.width)} ${core.bits})))",
        format
      )
    }

    /** An NVIDIA fused multiply-add, rounded as the instruction asks. Binary64 takes a NaN from the right factor, the
      * addend, then the left factor, quieted. Narrower formats leave the payload of a NaN operand's result to the host,
      * and give the default NaN where the operation is invalid.
      */
    private def nvidiaFused(arguments: Vec[Term], result: Type): Either[String, Value] = for {
      format   <- formatOf(result)
      left     <- floatingArgument(arguments, 0)
      right    <- floatingArgument(arguments, 1)
      addend   <- floatingArgument(arguments, 2)
      rounding <- roundingArgument(arguments, 3)
    } yield {
      val core    = operation(s"(fp.fma $rounding ${left.sexp} ${right.sexp} ${addend.sexp})", format)
      val default = literal(nvidiaNaN(format), format.width)
      if (format.width == 64)
        fromBits(
          s"(ite (fp.isNaN ${right.sexp}) ${quieted(right.bits, format)} " +
            s"(ite (fp.isNaN ${addend.sexp}) ${quieted(addend.bits, format)} " +
            s"(ite (fp.isNaN ${left.sexp}) ${quieted(left.bits, format)} " +
            s"(ite (fp.isNaN ${core.sexp}) $default ${core.bits}))))",
          format
        )
      else {
        val operandNaN = s"(or (fp.isNaN ${left.sexp}) (fp.isNaN ${right.sexp}) (fp.isNaN ${addend.sexp}))"
        fromBits(s"(ite (and (fp.isNaN ${core.sexp}) (not $operandNaN)) $default ${core.bits})", format)
      }
    }

    /** Any NaN becomes the instruction's canonical NaN; binary64 keeps its operands. */
    private def canonical(arguments: Vec[Term], result: Type): Either[String, Value] = for {
      format <- formatOf(result)
      value  <- floatingArgument(arguments, 0)
    } yield fromBits(
      if (format.width == 64) value.bits
      else s"(ite (fp.isNaN ${value.sexp}) ${literal(nvidiaNaN(format), format.width)} ${value.bits})",
      format
    )

    /** The value clamped to [0, 1], with NaN as zero. */
    private def saturate(arguments: Vec[Term], result: Type): Either[String, Value] = for {
      format <- formatOf(result)
      value  <- floatingArgument(arguments, 0)
    } yield {
      val zero = constant(0L, format)
      val one  = constant(format.one, format)
      fromBits(
        s"(ite (or (fp.isNaN ${value.sexp}) (fp.leq ${value.sexp} ${zero.sexp})) ${zero.bits} " +
          s"(ite (fp.geq ${value.sexp} ${one.sexp}) ${one.bits} ${value.bits}))",
        format
      )
    }

    /** A subnormal becomes zero of its sign. */
    private def flushed(arguments: Vec[Term], result: Type): Either[String, Value] = for {
      format <- formatOf(result)
      value  <- floatingArgument(arguments, 0)
    } yield fromBits(
      s"(ite (fp.isSubnormal ${value.sexp}) (bvand ${value.bits} ${literal(format.signBit, format.width)}) " +
        s"${value.bits})",
      format
    )

    /** An AMDGPU sum, difference or product, which quiets a NaN operand on the left first. The right operand of a
      * binary16 difference propagates negated, and an invalid operation gives the default NaN.
      */
    private def amdBinary(operator: String, arguments: Vec[Term], result: Type): Either[String, Value] = for {
      format   <- formatOf(result)
      left     <- floatingArgument(arguments, 0)
      right    <- floatingArgument(arguments, 1)
      rounding <- fieldRounding(arguments, 2, format)
    } yield {
      val core    = operation(s"($operator $rounding ${left.sexp} ${right.sexp})", format)
      val negated = if (operator == "fp.sub" && format.width == 16)
        s"(bvxor ${right.bits} ${literal(format.signBit, 16)})"
      else right.bits
      fromBits(
        s"(ite (fp.isNaN ${left.sexp}) ${quieted(left.bits, format)} " +
          s"(ite (fp.isNaN ${right.sexp}) ${quieted(negated, format)} " +
          s"(ite (fp.isNaN ${core.sexp}) ${literal(amdNaN(format), format.width)} ${core.bits})))",
        format
      )
    }

    /** An AMDGPU fused multiply-add: a NaN factor wins, an invalid product is the default NaN, then a NaN addend. */
    private def amdFused(arguments: Vec[Term], result: Type): Either[String, Value] = for {
      format   <- formatOf(result)
      left     <- floatingArgument(arguments, 0)
      right    <- floatingArgument(arguments, 1)
      addend   <- floatingArgument(arguments, 2)
      rounding <- fieldRounding(arguments, 3, format)
    } yield {
      val core    = operation(s"(fp.fma $rounding ${left.sexp} ${right.sexp} ${addend.sexp})", format)
      val invalid = s"(or (and (fp.isZero ${left.sexp}) (fp.isInfinite ${right.sexp})) " +
        s"(and (fp.isInfinite ${left.sexp}) (fp.isZero ${right.sexp})))"
      fromBits(
        s"(ite (fp.isNaN ${left.sexp}) ${quieted(left.bits, format)} " +
          s"(ite (fp.isNaN ${right.sexp}) ${quieted(right.bits, format)} " +
          s"(ite $invalid ${literal(amdNaN(format), format.width)} " +
          s"(ite (fp.isNaN ${addend.sexp}) ${quieted(addend.bits, format)} " +
          s"(ite (fp.isNaN ${core.sexp}) ${literal(amdNaN(format), format.width)} ${core.bits})))))",
        format
      )
    }

    /** A floating-point operation whose bit pattern only its result determines; the bits are a fresh constant tied to
      * the operation by the equality the theory would otherwise give `fp.to_ieee_bv`.
      */
    private def operation(sexp: String, format: FpFormat): Value.Floating = {
      val name = symbol(s"${prefix}fp${counter.next()}")
      emit(s"(declare-const $name (_ BitVec ${format.width}))")
      emit(s"(assert (= $sexp (${format.toSort} $name)))")
      remember(format)
      Floating(sexp, name, format)
    }

    private def fromBits(bits: String, format: FpFormat): Value.Floating = {
      remember(format)
      Floating(s"(${format.toSort} $bits)", bits, format)
    }

    private def constant(bits: Long, format: FpFormat): Value.Floating =
      fromBits(literal(bits, format.width), format)

    private def remember(format: FpFormat): Unit = {
      val _ = formats += format
    }

    private def quieted(bits: String, format: FpFormat): String =
      s"(bvor $bits ${literal(format.quietBit, format.width)})"

    private def formatOf(tpe: Type): Either[String, FpFormat] = tpe.unranged match {
      case Type.Float(width) => FpFormat.of(width).toRight(s"binary$width has no SMT floating-point format")
      case other             => Left(s"$other is not a floating-point type")
    }

    private def floatingArgument(arguments: Vec[Term], index: Int): Either[String, Value.Floating] =
      arguments.lift(index).toRight(s"the primitive has no argument $index").flatMap(floatingTerm(_))

    /** The rounding a MODE field selects: 0 nearest even, 1 up, 2 down, 3 toward zero, in the low two bits of the
      * argument for binary32 and the next two for the other widths.
      */
    private def fieldRounding(arguments: Vec[Term], index: Int, format: FpFormat): Either[String, String] =
      arguments.lift(index).toRight(s"the primitive has no rounding argument $index").flatMap(expression(_)).map {
        value =>
          val low   = if (format.width == 32) 0 else 2
          val field = s"((_ extract ${low + 1} $low) ${bits(value, 32)})"
          s"(ite (= $field #b00) RNE (ite (= $field #b01) RTP (ite (= $field #b10) RTN RTZ)))"
      }

    private def roundingArgument(arguments: Vec[Term], index: Int): Either[String, String] =
      arguments.lift(index).toRight(s"the primitive has no argument $index").flatMap {
        case Term.Constant(code) =>
          code.substring(code.lastIndexOf('.') + 1) match {
            case "RN" | "RNI" => Right("RNE")
            case "RM" | "RMI" => Right("RTN")
            case "RP" | "RPI" => Right("RTP")
            case "RZ" | "RZI" => Right("RTZ")
            case other        => Left(s"rounding mode $other is not supported")
          }
        case other => Left(s"${other.show} names no rounding mode")
      }

    private def resized(signed: Boolean, width: String, argument: Value): Value = {
      val to = width.toInt
      Bitvector(bits(argument, to), to, signed)
    }

    private def shape(value: Value): Option[(Int, Boolean)] = value match {
      case Numeral(_)                => None
      case Bitvector(_, width, sign) => Some((width, sign))
      case Floating(_, _, _)         => None
      case Truth(_)                  => Some((1, false))
    }

    private def floating(value: Value): Boolean = value match {
      case Floating(_, _, _) => true
      case _                 => false
    }

    private def common(values: Vec[Value]): (Int, Boolean) = {
      val shapes = values.flatMap(shape(_))
      if (shapes.isEmpty) (64, true) else (shapes.map(_._1).max, shapes.forall(_._2))
    }

    private def bits(value: Value, width: Int): String = value match {
      case Numeral(number)               => literal(number, width)
      case Bitvector(sexp, from, signed) => fit(sexp, from, signed, width)
      case Floating(_, bits, format)     => fit(bits, format.width, signed = false, width)
      case Truth(sexp)                   => s"(ite $sexp ${literal(1, width)} ${literal(0, width)})"
    }
  }

  private def collect[A, B: ClassTag](items: Vec[A])(step: A => Either[String, B]): Either[String, Vec[B]] =
    items.foldLeft[Either[String, Vector[B]]](Right(Vector.empty)) { (done, item) =>
      for {
        found <- done
        next  <- step(item)
      } yield found :+ next
    }.map(Vec.from)

  private def declarations(
      parameters: Vec[(name: String, tpe: Type)],
      spaces: List[String]
  ): Either[String, List[String]] =
    collect(parameters)(parameter =>
      declarationSort(parameter.tpe).map(sort => s"(declare-const ${symbol(parameter.name)} $sort)")
    ).map(_.toList ++ spaces.map(space => s"(declare-const ${initial(space)} $Memory)"))

  private def declarationSort(tpe: Type): Either[String, String] = tpe.unranged match {
    case Type.Integer(width, _) => Right(s"(_ BitVec $width)")
    case Type.Bool              => Right("(_ BitVec 1)")
    // a floating-point parameter is a register's bits, so both sides of a query see the same NaN payload
    case Type.Float(width) =>
      FpFormat.of(width).toRight(s"binary$width has no SMT floating-point format").map(_.width).map(width =>
        s"(_ BitVec $width)"
      )
    case other => Left(s"$other is not an integer, truth or floating-point value")
  }

  private def shapeOf(tpe: Type): Either[String, (Int, Boolean)] = tpe.unranged match {
    case Type.Integer(width, signed) => Right((width, signed))
    case Type.Bool                   => Right((1, false))
    case other                       => Left(s"$other is not an integer or a truth value")
  }

  /** The negative quiet NaN an AMDGPU invalid operation gives at every width, as gfx1036 produces it. */
  private def amdNaN(format: FpFormat): Long = format.signBit | format.infinity | format.quietBit

  /** NVIDIA's NaN for the format: canonical at binary16 and binary32, and the negative quiet NaN at binary64 on an
    * invalid operation, as sm_89 produces them.
    */
  private def nvidiaNaN(format: FpFormat): Long = format.width match {
    case 16 => 0x7fffL
    case 32 => 0x7fffffffL
    case _  => format.signBit | format.infinity | format.quietBit
  }

  private def assertion(differences: List[String]): String = differences match {
    case Nil          => "(assert false)"
    case List(single) => s"(assert $single)"
    case several      => s"(assert (or ${several.mkString(" ")}))"
  }

  private def union(
      left: Vec[(name: String, tpe: Type)],
      right: Vec[(name: String, tpe: Type)]
  ): Either[String, Vec[(name: String, tpe: Type)]] = {
    val known = left.map(parameter => parameter.name -> parameter.tpe.unranged).toMap
    right.find(parameter => known.get(parameter.name).exists(_ != parameter.tpe.unranged)) match {
      case Some(parameter) => Left(s"${parameter.name} has two types")
      case None            => Right(left ++ right.filterNot(parameter => known.contains(parameter.name)))
    }
  }

  private def pairResults(
      left: List[(name: String, compared: String, tpe: Type)],
      right: List[(name: String, compared: String, tpe: Type)]
  ): Either[String, List[(String, String)]] =
    if (left.map(_.name).toSet != right.map(_.name).toSet) Left("the definitions have different results")
    else {
      val symbols = right.map(result => result.name -> (result.compared, result.tpe.unranged)).toMap
      left.find(result => symbols(result.name)._2 != result.tpe.unranged) match {
        case Some(result) => Left(s"result ${result.name} has two types")
        case None         => Right(left.map(result => (result.compared, symbols(result.name)._1)))
      }
    }

  private def finalOf(encoding: Encoding, space: String): String =
    encoding.stored.getOrElse(space, initial(space))

  private def whole(term: Term, width: Int): Either[String, Int] =
    if (width % 8 == 0) Right(width) else Left(s"${term.show} stores a value that is not a whole number of bytes")

  private def fit(sexp: String, from: Int, signed: Boolean, to: Int): String =
    if (from == to) sexp
    else if (from > to) s"((_ extract ${to - 1} 0) $sexp)"
    else if (signed) s"((_ sign_extend ${to - from}) $sexp)"
    else s"((_ zero_extend ${to - from}) $sexp)"

  private def literal(value: Long, width: Int): String = {
    val masked = if (width >= 64) value else value & ((1L << width) - 1)
    s"(_ bv${java.lang.Long.toUnsignedString(masked)} $width)"
  }

  private def offset(address: String, index: Int): String =
    if (index == 0) address else s"(bvadd $address ${literal(index, 64)})"

  private def conjoin(guard: Option[String], condition: String): String =
    guard.fold(condition)(outer => s"(and $outer $condition)")

  private def isMachine(contextType: String): Boolean = contextType == RenderMachine.Machine.contextType

  private def spaceKey(term: Term): Either[String, String] = term match {
    case Term.Constant(code) => Right(code)
    case other               => Left(s"${other.show} is not a memory space")
  }

  private def spaceName(key: String): String = key.substring(key.lastIndexOf('.') + 1)

  private def initial(space: String): String = symbol(s"memory_${spaceName(space)}")

  private def symbol(name: String): String =
    if (name.matches("[A-Za-z~!@$%^&*_+=<>.?/-][A-Za-z0-9~!@$%^&*_+=<>.?/-]*")) name else s"|$name|"
}
