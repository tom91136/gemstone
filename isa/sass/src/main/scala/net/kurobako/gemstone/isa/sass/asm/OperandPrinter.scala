package net.kurobako.gemstone.isa.sass.asm

import cats.syntax.all.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.*
import net.kurobako.gemstone.isa.sass.asm.*
import scala.util.Try

/** Canonical text for the structured SASS operand model. */
object OperandPrinter {
  def print(operand: Operand): Either[String, String] =
    render(operand).flatMap { text =>
      SASSOperandParser.parse(text).flatMap { parsed =>
        Either.cond(roundTrips(operand, parsed), text, s"operand has no lossless SASS spelling: $operand")
      }
    }

  private def render(operand: Operand): Either[String, String] = operand match {
    case Operand.Register(bank, index) =>
      if (bank == RegisterBank.R && index.raw == 255) Right("RZ")
      else if (bank == RegisterBank.UR && index.raw == 63) Right("URZ")
      else if (RegisterBank.capacity(bank).nonEmpty || identifier(bank.spelling)) Right(s"${bank.spelling}${index.raw}")
      else Left(s"unsupported SASS register bank $bank")
    case Operand.Integer(value)     => Right(value.toString)
    case Operand.Bits(value, width) =>
      if (value < 0) Left("bit-pattern immediates must be nonnegative")
      else if (width.raw == 0) Right(hex(value))
      else if (value.bitLength > width.raw) Left(s"bit pattern does not fit ${width.raw} bits")
      else Right(s"bits<${width.raw}>(${hex(value)})")
    case Operand.Symbol(value) =>
      if (identifier(value)) Right(value) else Left(s"invalid SASS symbol $value")
    case Operand.Group(values) =>
      values.toVec.traverse(render(_)).map(_.mkString("{", ", ", "}"))
    case Operand.Decorated(value, attributes) =>
      for {
        _     <- Either.cond(attributes.nonEmpty, (), "empty SASS operand decoration has no textual form")
        text  <- render(value)
        flags <- attributes.toVec.traverse { (name, flag) =>
          if (
            Set(
              OperandDecoration.Not,
              OperandDecoration.Negate,
              OperandDecoration.Invert,
              OperandDecoration.Absolute
            )(name)
          )
            truth(flag).map(value => name -> value)
          else Left(s"unsupported SASS operand attribute $name")
        }
        result <- decorate(text, flags.toMap)
      } yield result
    case Operand.Predicate(index, uniform, negated) =>
      val prefix = if (uniform) "UP" else "P"
      val name   = index.fold(if (uniform) "UPT" else "PT")(value => s"$prefix${value.raw}")
      Right((if (negated) "!" else "") + name)
    case Operand.SpecialRegister(name) =>
      if (specialRegister(name.spelling)) Right(name.spelling)
      else Left(s"invalid SASS special register ${name.spelling}")
    case Operand.FloatingBits(format, width, bits) =>
      val expectedWidth = format.width
      if (expectedWidth.exists(_.raw != width.raw))
        Left(s"unsupported floating format and width ${format.spelling}:${width.raw}")
      else if (bits < 0 || bits.bitLength > width.raw) Left(s"floating payload does not fit ${width.raw} bits")
      else Right(s"bits<${format.spelling}>:${width.raw}(${hex(bits)})")
    case Operand.Floating(value, spelling, negativeZero) =>
      if (negativeZero && value != 0) Left("negative-zero marker requires a zero value")
      else {
        val magnitude = spelling.stripPrefix("-")
        val valid     = Try(BigDecimal(magnitude)).toOption.contains(value.abs)
        if (!valid) Left(s"floating spelling $spelling does not represent $value")
        else Right((if (negativeZero || value < 0) "-" else "") + magnitude)
      }
    case Operand.FloatingSymbol(name) =>
      name match {
        case FloatingConstant.INF | FloatingConstant.QNAN | FloatingConstant.SNAN | FloatingConstant.NAN |
            FloatingConstant.`+INF` | FloatingConstant.`-INF` | FloatingConstant.`+QNAN` | FloatingConstant.`-QNAN` |
            FloatingConstant.`+SNAN` | FloatingConstant.`-SNAN` | FloatingConstant.`-NAN` => Right(name.spelling)
        case _ => Left(s"unsupported SASS floating symbol ${name.spelling}")
      }
    case Operand.ConstantBank(bank, offset) =>
      if (bank < 0) Left("constant-bank index must be nonnegative")
      else renderConstantOffset(offset).map(value => s"c[$bank][$value]")
    case address: Operand.Address                      => renderAddress(address)
    case Operand.Unary(value, negative, not, absolute) =>
      if (!negative && !not && !absolute) Left("empty SASS unary decoration has no textual form")
      else render(value).map(text => decorateUnary(text, negative, not, absolute))
    case Operand.Qualified(value, selectors) =>
      if (selectors.isEmpty) Left("SASS operand selectors must be nonempty")
      else if (!selectors.forall(selector => suffix(selector.spelling))) Left("invalid SASS operand selector")
      else render(value).map(text => s"$text.${selectors.map(_.spelling).mkString(".")}")
  }

  private def renderConstantOffset(value: Operand): Either[String, String] = value match {
    case address: Operand.Address                               => renderAddress(address, bracket = false)
    case Operand.Qualified(address: Operand.Address, selectors) =>
      renderAddress(address, bracket = false).map(text => s"$text.${selectors.map(_.spelling).mkString(".")}")
    case other => render(other)
  }

  private def renderAddress(address: Operand.Address, bracket: Boolean = true): Either[String, String] = {
    val mode = address.mode match {
      case Some(value) if AddressMode.scale(value).contains(address.scale.raw) => Right(Some(value.spelling))
      case Some(value)                    => Left(s"unsupported or inconsistent SASS address mode ${value.spelling}")
      case None if address.scale.raw == 1 => Right(None)
      case None                           => Left(s"SASS address scale ${address.scale.raw} has no textual mode")
    }
    if (address.base.isEmpty && address.index.nonEmpty)
      Left("SASS address index cannot be represented without a base register")
    else mode.flatMap { selectedMode =>
      val base = address.base.map { register =>
        s"${register.bank.spelling}${register.index.raw}" + selectedMode.fold("")(value => s".$value")
      }
      val terms = base.toVec ++ address.index.map(register => s"${register.bank.spelling}${register.index.raw}").toVec
      val registers  = terms.mkString("+")
      val expression =
        if (address.displacement == 0 && terms.nonEmpty) registers
        else if (address.displacement < 0) registers + s"-${hex(-address.displacement)}"
        else if (terms.nonEmpty) registers + s"+${hex(address.displacement)}"
        else hex(address.displacement)
      val body = if (expression.isEmpty) "0" else expression
      address.descriptor match {
        case Some(descriptor) =>
          render(descriptor).map(printedDescriptor => s"desc[$printedDescriptor][$body]")
        case None => Right(if (bracket) s"[$body]" else body)
      }
    }
  }

  private def truth(value: Operand): Either[String, Boolean] = value match {
    case Operand.Integer(0) | Operand.Bits(0, _) => Right(false)
    case Operand.Integer(1) | Operand.Bits(1, _) => Right(true)
    case _                                       => Left("SASS operand flags must be integer 0 or 1")
  }

  private def decorate(text: String, flags: Map[OperandDecoration, Boolean]): Either[String, String] =
    if (flags.getOrElse(OperandDecoration.Not, false) && flags.getOrElse(OperandDecoration.Invert, false))
      Left("SASS operand cannot combine not and invert decorations")
    else Right(decorateUnary(
      text,
      flags.getOrElse(OperandDecoration.Negate, false),
      flags.getOrElse(OperandDecoration.Not, false) || flags.getOrElse(OperandDecoration.Invert, false),
      flags.getOrElse(OperandDecoration.Absolute, false)
    ))

  private def decorateUnary(text: String, negative: Boolean, not: Boolean, absolute: Boolean): String =
    (if (not) "!" else "") + (if (negative) "-" else "") +
      (if (absolute) s"|$text|" else text)

  private def identifier(value: String): Boolean =
    value.headOption.exists(character => character.isLetter || character == '_') &&
      value.forall(character => character.isLetterOrDigit || "_$".contains(character))

  private def suffix(value: String): Boolean =
    value.nonEmpty && value.forall(character => character.isLetterOrDigit || "_$".contains(character))

  private def specialRegister(value: String): Boolean =
    value.startsWith("SR") || value.startsWith("UR_TID") || value.startsWith("UR_CTAID")

  private def roundTrips(expected: Operand, actual: Operand): Boolean =
    expected == actual ||
      ((expected, actual) match {
        case (Operand.Integer(value), _) if value < 0 =>
          actual == Operand.Unary(Operand.Integer(-value), negative = true, not = false, absolute = false)
        case (Operand.Floating(value, spelling, negativeZero), _) if value < 0 || negativeZero =>
          val magnitude = spelling.stripPrefix("-")
          actual == Operand.Unary(
            Operand.Floating(value.abs, magnitude, negativeZero = false),
            negative = true,
            not = false,
            absolute = false
          )
        case (Operand.FloatingSymbol(FloatingConstant.`+INF`), _) => actual ==
            Operand.FloatingSymbol(FloatingConstant.INF)
        case (Operand.FloatingSymbol(FloatingConstant.`-INF`), _) =>
          actual ==
            Operand.Unary(Operand.FloatingSymbol(FloatingConstant.INF), negative = true, not = false, absolute = false)
        case (Operand.FloatingSymbol(FloatingConstant.`+QNAN`), _) => actual ==
            Operand.FloatingSymbol(FloatingConstant.QNAN)
        case (Operand.FloatingSymbol(FloatingConstant.`-QNAN`), _) =>
          actual ==
            Operand.Unary(Operand.FloatingSymbol(FloatingConstant.QNAN), negative = true, not = false, absolute = false)
        case (Operand.FloatingSymbol(FloatingConstant.`+SNAN`), _) => actual ==
            Operand.FloatingSymbol(FloatingConstant.SNAN)
        case (Operand.FloatingSymbol(FloatingConstant.`-SNAN`), _) =>
          actual ==
            Operand.Unary(Operand.FloatingSymbol(FloatingConstant.SNAN), negative = true, not = false, absolute = false)
        case (Operand.FloatingSymbol(FloatingConstant.`-NAN`), _) =>
          actual ==
            Operand.Unary(Operand.FloatingSymbol(FloatingConstant.NAN), negative = true, not = false, absolute = false)
        case (Operand.Unary(Operand.Predicate(index, uniform, negated), negative, not, false), _) =>
          actual == Operand.Predicate(index, uniform, negated ^ negative ^ not)
        case (Operand.Decorated(value, attributes), _) =>
          val flags = attributes.toVec.flatMap { (name, flag) =>
            truth(flag).toOption.map(value => name -> value)
          }.toMap
          val negative = flags.getOrElse(OperandDecoration.Negate, false)
          val not = flags.getOrElse(OperandDecoration.Not, false) || flags.getOrElse(OperandDecoration.Invert, false)
          val absolute = flags.getOrElse(OperandDecoration.Absolute, false)
          roundTrips(Operand.Unary(value, negative, not, absolute), actual)
        case (Operand.Group(left), Operand.Group(right)) =>
          left.size == right.size && left.zip(right).forall((a, b) => roundTrips(a, b))
        case (Operand.ConstantBank(leftBank, leftOffset), Operand.ConstantBank(rightBank, rightOffset)) =>
          leftBank == rightBank && roundTrips(leftOffset, rightOffset)
        case (Operand.Qualified(left, leftSelectors), Operand.Qualified(right, rightSelectors)) =>
          leftSelectors == rightSelectors && roundTrips(left, right)
        case (
              Operand.Unary(left, leftNegative, leftNot, leftAbsolute),
              Operand.Unary(right, rightNegative, rightNot, rightAbsolute)
            ) =>
          leftNegative == rightNegative && leftNot == rightNot && leftAbsolute == rightAbsolute &&
          roundTrips(left, right)
        case (left: Operand.Address, right: Operand.Address) =>
          left.base == right.base && left.index == right.index && left.displacement == right.displacement &&
          left.scale == right.scale && left.mode == right.mode &&
          ((left.descriptor, right.descriptor) match {
            case (Some(a), Some(b)) => roundTrips(a, b)
            case (None, None)       => true
            case _                  => false
          })
        case _ => false
      })

  private def hex(value: BigInt): String = s"0x${value.toString(16)}"
}
