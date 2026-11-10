package net.kurobako.gemstone.semantics.render

import net.kurobako.gemstone.collection.*
import scala.annotation.tailrec
import scala.collection.mutable
import scala.reflect.ClassTag

/** SMT-LIB bit-vector encoding of integer definitions for equivalence queries. */
object SmtBackend extends Backend {

  private enum Value derives CanEqual {
    case Numeral(value: Long)
    case Bitvector(sexp: String, width: Int, signed: Boolean)
    case Truth(sexp: String)
  }

  private case class Encoding(
      lines: List[String],
      results: List[(String, String)],
      accessed: Set[String],
      stored: Map[String, String]
  )

  private val Memory      = "(Array (_ BitVec 64) (_ BitVec 8))"
  private val Converted   = raw"\.to\[(UInt|SInt)\[(\d+)\]\]".r
  private val Constructed = raw"(UInt|SInt)\[(\d+)\]".r
  private val Extreme     = raw"(min|max)\.([us])".r

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

  def show(definition: Definition): String = encode(definition, "").fold(
    reason => throw IllegalArgumentException(reason),
    encoding => (declarations(definition.parameters, encoding.accessed.toList) ++ encoding.lines).mkString("\n")
  )

  def source(definition: Definition): String = show(definition)

  /** A query that is unsatisfiable exactly when `left` and `right` agree on every input where both encode. */
  def equivalence(left: Definition, right: Definition): Either[String, String] = for {
    l          <- encode(left, "l_")
    r          <- encode(right, "r_")
    parameters <- union(left.parameters, right.parameters)
    pairs      <- pairResults(l.results, r.results)
  } yield {
    val spaces      = (l.accessed ++ r.accessed).toList
    val differences = pairs.map { case (a, b) => s"(distinct $a $b)" } ++
      (l.stored.keys ++ r.stored.keys).toList.distinct.map(space =>
        s"(distinct ${finalOf(l, space)} ${finalOf(r, space)})"
      )
    val values = parameters.toList.map(parameter => symbol(parameter.name))
    (List("(set-logic QF_ABV)", "(set-option :produce-models true)") ++
      declarations(parameters, spaces) ++ l.lines ++ r.lines ++
      List(assertion(differences), "(check-sat)") ++
      (if (values.isEmpty) Nil else List(s"(get-value (${values.mkString(" ")}))"))).mkString("\n")
  }

  private def encode(definition: Definition, prefix: String): Either[String, Encoding] =
    Encoder(prefix, definition).run()

  private class Encoder(prefix: String, definition: Definition) {
    private val inputs   = definition.parameters.map(_.name).toSet
    private val values   = mutable.HashMap.empty[String, Value]
    private val lines    = mutable.ArrayBuffer.empty[String]
    private val accessed = mutable.LinkedHashSet.empty[String]
    private val stored   = mutable.LinkedHashMap.empty[String, String]
    private val counter  = Iterator.from(0)

    def run(): Either[String, Encoding] = for {
      _       <- collect(definition.parameters)(declare(_))
      _       <- collect(definition.body)(step => statement(step.target, step.term, None))
      _       <- collect(definition.requirements)(requirement(_))
      results <- collect(definition.results)(output(_))
    } yield Encoding(lines.toList, results.toList, accessed.toSet, stored.toMap)

    private def emit(line: String): Unit = lines.append(line)

    private def declare(parameter: (name: String, tpe: Type)): Either[String, Unit] =
      shapeOf(parameter.tpe).map { case (width, signed) =>
        values(parameter.name) = Value.Bitvector(symbol(parameter.name), width, signed)
      }

    private def requirement(term: Term): Either[String, Unit] =
      expression(term).map(value => emit(s"(assert ${truth(value)})"))

    private def output(result: (name: String, tpe: Type, value: Term)): Either[String, (String, String)] = for {
      shape <- shapeOf(result.tpe)
      value <- expression(result.value)
    } yield {
      val target = outputSymbol(result.name)
      emit(s"(define-fun $target () (_ BitVec ${shape._1}) ${bits(value, shape._1)})")
      result.name -> target
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
      case Value.Numeral(number) => define(target, Value.Bitvector(literal(number, 64), 64, signed = true))
      case Value.Bitvector(sexp, width, signed) =>
        emit(s"(define-fun $target () (_ BitVec $width) $sexp)")
        Value.Bitvector(target, width, signed)
      case Value.Truth(sexp) =>
        emit(s"(define-fun $target () Bool $sexp)")
        Value.Truth(target)
    }

    private def effect(term: Term, guard: Option[String]): Either[String, Unit] = term match {
      case Term.Select(condition, inner, Term.Constant("()")) =>
        expression(condition).flatMap(value => effect(inner, Some(conjoin(guard, truth(value)))))
      case Term.When(condition, body) =>
        expression(condition).flatMap { value =>
          val inner = Some(conjoin(guard, truth(value)))
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
            case Value.Bitvector(_, width, _) => whole(term, width)
            case _                            => Left(s"${term.show} stores a value that is not a bit-vector")
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
      case Term.Literal(number)               => Right(Value.Numeral(number))
      case Term.Unary(operator, value)        => expression(value).flatMap(unary(operator, _))
      case Term.Binary(operator, left, right) =>
        for {
          l <- expression(left)
          r <- expression(right)
          v <- binary(operator, l, r)
        } yield v
      case Term.Call(function, arguments)              => collect(arguments)(expression(_)).flatMap(call(function, _))
      case Term.Select(condition, whenTrue, whenFalse) =>
        for {
          c <- expression(condition)
          t <- expression(whenTrue)
          f <- expression(whenFalse)
        } yield select(truth(c), t, f)
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
        } yield Value.Bitvector(read(key, bits(a, 64), shape._1), shape._1, shape._2)
      case other => Left(s"${other.show} is not supported")
    }
  }

  private def collect[A, B: ClassTag](items: Vec[A])(step: A => Either[String, B]): Either[String, Vec[B]] =
    items.foldLeft[Either[String, Vector[B]]](Right(Vector.empty)) { (done, item) =>
      for {
        found <- done
        next  <- step(item)
      } yield found :+ next
    }.map(Vec.from)

  private def declarations(parameters: Vec[(name: String, tpe: Type)], spaces: List[String]): List[String] =
    parameters.toList.map(parameter => s"(declare-const ${symbol(parameter.name)} (_ BitVec ${parameter.tpe.bits}))") ++
      spaces.map(space => s"(declare-const ${initial(space)} $Memory)")

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
      left: List[(String, String)],
      right: List[(String, String)]
  ): Either[String, List[(String, String)]] =
    if (left.map(_._1).toSet != right.map(_._1).toSet) Left("the definitions have different results")
    else {
      val symbols = right.toMap
      Right(left.map { case (name, output) => (output, symbols(name)) })
    }

  private def finalOf(encoding: Encoding, space: String): String =
    encoding.stored.getOrElse(space, initial(space))

  private def shapeOf(tpe: Type): Either[String, (Int, Boolean)] = tpe.unranged match {
    case Type.Integer(width, signed) => Right((width, signed))
    case Type.Bool                   => Right((1, false))
    case other                       => Left(s"$other is not an integer or a truth value")
  }

  private def whole(term: Term, width: Int): Either[String, Int] =
    if (width % 8 == 0) Right(width) else Left(s"${term.show} stores a value that is not a whole number of bytes")

  private def resized(signed: Boolean, width: String, argument: Value): Value = {
    val to = width.toInt
    Value.Bitvector(bits(argument, to), to, signed)
  }

  private def shape(value: Value): Option[(Int, Boolean)] = value match {
    case Value.Numeral(_)                  => None
    case Value.Bitvector(_, width, signed) => Some((width, signed))
    case Value.Truth(_)                    => Some((1, false))
  }

  private def common(values: Vec[Value]): (Int, Boolean) = {
    val shapes = values.flatMap(shape(_))
    if (shapes.isEmpty) (64, true) else (shapes.map(_._1).max, shapes.forall(_._2))
  }

  private def bits(value: Value, width: Int): String = value match {
    case Value.Numeral(number)               => literal(number, width)
    case Value.Bitvector(sexp, from, signed) => fit(sexp, from, signed, width)
    case Value.Truth(sexp)                   => s"(ite $sexp ${literal(1, width)} ${literal(0, width)})"
  }

  private def truth(value: Value): String = value match {
    case Value.Numeral(number)           => if (number != 0) "true" else "false"
    case Value.Bitvector(sexp, width, _) => s"(distinct $sexp ${literal(0, width)})"
    case Value.Truth(sexp)               => sexp
  }

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

  private def select(condition: String, whenTrue: Value, whenFalse: Value): Value = (whenTrue, whenFalse) match {
    case (Value.Truth(a), Value.Truth(b)) => Value.Truth(s"(ite $condition $a $b)")
    case _                                =>
      val (width, signed) = common(Vec(whenTrue, whenFalse))
      Value.Bitvector(s"(ite $condition ${bits(whenTrue, width)} ${bits(whenFalse, width)})", width, signed)
  }

  private def unary(operator: String, value: Value): Either[String, Value] = operator match {
    case "!"       => Right(Value.Truth(s"(not ${truth(value)})"))
    case "~" | "-" =>
      val (width, signed) = common(Vec(value))
      val name            = if (operator == "~") "bvnot" else "bvneg"
      Right(Value.Bitvector(s"($name ${bits(value, width)})", width, signed))
    case _ => Left(s"unary $operator is not supported")
  }

  private def binary(operator: String, left: Value, right: Value): Either[String, Value] = (left, right) match {
    case (Value.Truth(l), Value.Truth(r)) if Logic.contains(operator) =>
      Right(Value.Truth(s"(${Logic(operator)} $l $r)"))
    case _ if Conditional.contains(operator) =>
      Right(Value.Truth(s"(${Conditional(operator)} ${truth(left)} ${truth(right)})"))
    case _ if Arithmetic.contains(operator) =>
      val (width, signed) = common(Vec(left, right))
      Right(Value.Bitvector(s"(${Arithmetic(operator)} ${bits(left, width)} ${bits(right, width)})", width, signed))
    case _ if operator == "<<" || operator.startsWith(">>")                     => Right(shift(operator, left, right))
    case _ if operator == "==" || operator == "!=" || Orders.contains(operator) => compare(operator, left, right)
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
    Value.Bitvector(fit(s"($kind ${bits(value, 64)} $count)", 64, signed, width), width, signed)
  }

  private def compare(operator: String, left: Value, right: Value): Either[String, Value] = operator match {
    case "==" | "!=" =>
      val (_, l, r) = align(None, left, right)
      Right(Value.Truth(if (operator == "==") s"(= $l $r)" else s"(distinct $l $r)"))
    case _ =>
      val signs = Vec(left, right).flatMap(shape(_)).map(_._2).distinct
      if (signs.size > 1) Left(s"operator $operator compares operands of different signedness")
      else {
        val signed                     = signs.headOption.getOrElse(true)
        val (_, l, r)                  = align(Some(signed), left, right)
        val (unsignedName, signedName) = Orders(operator)
        Right(Value.Truth(s"(${if (signed) signedName else unsignedName} $l $r)"))
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
    case (Converted(kind, width), Vec(argument))   => Right(resized(kind == "SInt", width, argument))
    case (Constructed(kind, width), Vec(argument)) => Right(resized(kind == "SInt", width, argument))
    case (Extreme(kind, letter), Vec(left, right)) =>
      val signed            = letter == "s"
      val (width, l, r)     = align(Some(signed), left, right)
      val less              = if (signed) "bvsle" else "bvule"
      val (whenLess, other) = if (kind == "min") (l, r) else (r, l)
      Right(Value.Bitvector(s"(ite ($less $l $r) $whenLess $other)", width, signed))
    case ("blend", Vec(mask, set, clear)) =>
      val (width, signed) = common(Vec(mask, set, clear))
      val c               = bits(clear, width)
      Right(Value.Bitvector(
        s"(bvxor $c (bvand (bvxor ${bits(set, width)} $c) ${bits(mask, width)}))",
        width,
        signed
      ))
    case (other, _) => Left(s"call $other is not supported")
  }

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
