package net.kurobako.gemstone.isa.ptx

import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*

/** Why an immediate operand is outside the values its position accepts. */
enum OperandViolation derives CanEqual {
  case Malformed(raw: String)
  case OutOfRange(value: BigInt, min: Long, max: Long)
  case NotMultiple(value: BigInt, divisor: Long)
  case NotOneOf(value: BigInt, allowed: Vec[Long])
  case NotPowerOfTwo(value: BigInt, limit: Option[Long])

  /** An integer constant that needs more bits than the operand's type has. */
  case TooWide(value: BigInt, tpe: String)

  /** A constant of a kind the operand's type does not take, such as an integer constant for a floating-point type. */
  case WrongKind(constant: String, tpe: String)

  def message: String = this match {
    case Malformed(raw)                 => s"$raw is not a PTX constant"
    case OutOfRange(value, min, max)    => s"$value is outside $min..$max"
    case NotMultiple(value, divisor)    => s"$value is not a multiple of $divisor"
    case NotOneOf(value, allowed)       => s"$value is not one of ${allowed.mkString(", ")}"
    case NotPowerOfTwo(value, None)     => s"$value is not a power of two"
    case NotPowerOfTwo(value, Some(up)) => s"$value is not a power of two up to $up"
    case TooWide(value, tpe)            => s"$value does not fit .$tpe"
    case WrongKind(constant, tpe)       => s"$constant cannot be a .$tpe operand"
  }
}
