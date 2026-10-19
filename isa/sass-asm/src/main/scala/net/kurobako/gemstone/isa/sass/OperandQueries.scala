package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.catalogue.*
import scala.reflect.TypeTest

/** Structural operand queries shared by the decoder, the assembler and the compiler families. */
private object OperandQueries {
  def is[A <: Form](instruction: Instruction)(using test: TypeTest[Form, A]): Boolean =
    test.unapply(instruction.form).isDefined

  /** Vendor extensions can be preserved by the decoder and printer without acquiring execution semantics. */
  /** The `cx` marker of a constant bank chosen by a uniform register, `cx[URa][offset]`. */
  val IndexedBank: Operand = Operand.Symbol("cx")

  /** Whether `value` is the `cx[URa]` half of an indexed constant bank, which the next operand addresses. */
  def indexedBank(value: Operand): Boolean = value match {
    case Operand.Address(descriptor = Some(IndexedBank)) => true
    case _                                               => false
  }

  def hasUnknownDomain(value: Operand): Boolean = value match {
    case Operand.Register(RegisterBank.Unknown(_), _)         => true
    case Operand.SpecialRegister(SpecialRegister.Unknown(_))  => true
    case Operand.FloatingSymbol(FloatingConstant.Unknown(_))  => true
    case Operand.FloatingBits(FloatEncoding.Unknown(_), _, _) => true
    case Operand.Address(base, index, _, _, mode, descriptor) =>
      base.exists(hasUnknownDomain(_)) || index.exists(hasUnknownDomain(_)) ||
      mode.exists { case AddressMode.Unknown(_) => true; case _ => false } || descriptor.exists(hasUnknownDomain(_))
    case Operand.Qualified(inner, selectors) =>
      selectors.exists { case OperandSelector.Unknown(_) => true; case _ => false } || hasUnknownDomain(inner)
    case Operand.Unary(value = inner)    => hasUnknownDomain(inner)
    case Operand.ConstantBank(_, offset) => hasUnknownDomain(offset)
    case Operand.Group(values)           => values.exists(hasUnknownDomain(_))
    case _                               => false
  }

  // Normalise symbolic zero registers independently of each target's physical encoding.
  val Zero: Operand.Register        = Operand.Register(RegisterBank.R, 255)
  val UniformZero: Operand.Register = Operand.Register(RegisterBank.UR, 255)
  val True: Operand.Predicate       = Operand.Predicate(None, uniform = false, negated = false)

  @annotation.tailrec
  def base(value: Operand): Operand = value match {
    case Operand.Qualified(inner, _) => base(inner)
    case other                       => other
  }

  def selectors(value: Operand): Vec[OperandSelector] = value match {
    case Operand.Qualified(inner, suffixes) => selectors(inner) ++ suffixes
    case _                                  => Vec.empty
  }

  /** Selectors other than the register-reuse hint. */
  def qualifiers(value: Operand): Vec[OperandSelector] = selectors(without(value, OperandSelector.reuse))

  def without(value: Operand, selector: OperandSelector): Operand = value match {
    case Operand.Qualified(inner, suffixes) =>
      val remaining = suffixes.filterNot(_ == selector)
      if (remaining.isEmpty) without(inner, selector)
      else Operand.Qualified(without(inner, selector), remaining)
    case other => other
  }

  def has(value: Operand, selector: OperandSelector): Boolean = selectors(value).contains(selector)

  /** Whether `value` is a GMMA scoreboard such as `gsb0`, which reads as a register of an unknown bank. */
  def isScoreboard(value: Operand): Boolean = value match {
    case Operand.Register(RegisterBank.Unknown(bank), _) => bank.raw == "gsb"
    case _                                               => false
  }

  /** The spelling of a GMMA scoreboard operand. */
  def scoreboard(value: Operand): String = value match {
    case Operand.Register(bank, index) => s"${bank.spelling}$index"
    case other                         => throw IllegalArgumentException(s"expected scoreboard, found $other")
  }
  def isZero(value: Operand): Boolean = base(value) == Zero || base(value) == UniformZero
  def isRegister(value: Operand, bank: RegisterBank = RegisterBank.R, allowZero: Boolean = false): Boolean =
    base(value) match {
      case Operand.Register(actual, index) => actual == bank && index < RegisterBank.capacity(bank).getOrElse(0) &&
        (allowZero || !isZero(value))
      case _ => false
    }

  def integer(value: Operand): Option[BigInt] = value match {
    case Operand.Integer(number)                  => Some(number)
    case Operand.Bits(number, _)                  => Some(number)
    case Operand.Unary(inner, true, false, false) => integer(inner).map(-_)
    case _                                        => None
  }

  def negative(value: Operand): Boolean = value match {
    case Operand.Unary(negative = true) => true
    case _                              => false
  }

  def positive(value: Operand): Operand = value match {
    case Operand.Unary(inner, true, false, false)         => inner
    case Operand.Unary(inner, true, complement, absolute) =>
      Operand.Unary(inner, negative = false, complement, absolute)
    case other => other
  }
  def registerIndex(value: Operand): Int in 0 ~ 255 = base(value) match {
    case Operand.Register(_, index) => index
    case _                          => throw IllegalArgumentException(s"expected register, found $value")
  }
  def isPredicate(value: Operand): Boolean = value match {
    case Operand.Predicate(_, uniform, negated) => !negated && !uniform
    case _                                      => false
  }
  def isPredicateAllowingNegated(value: Operand): Boolean = value match {
    case Operand.Predicate(_, uniform, _) => !uniform
    case _                                => false
  }
  def isPredicateAllowingUniform(value: Operand): Boolean = value match {
    case Operand.Predicate(_, _, negated) => !negated
    case _                                => false
  }
  def isPredicateAllowingBoth(value: Operand): Boolean = value match {
    case _: Operand.Predicate => true
    case _                    => false
  }
  def isAlwaysPredicate(value: Operand): Boolean = value match {
    case Operand.Predicate(None, _, false) => true
    case _                                 => false
  }
  def isNeverPredicate(value: Operand): Boolean = value match {
    case Operand.Predicate(None, _, true) => true
    case _                                => false
  }
  def complemented(value: Operand): Boolean = value match {
    case Operand.Unary(not = true) => true
    case _                         => false
  }
  def unaryValue(value: Operand): Operand = value match {
    case Operand.Unary(value = inner) => inner
    case other                        => other
  }
  @annotation.tailrec
  def numericFloating(value: Operand): Boolean = value match {
    case Operand.Integer(_) | _: Operand.Floating | Operand.FloatingSymbol(_) => true
    case Operand.FloatingBits(_, width, _) if width == 64                     => true
    case Operand.Unary(value = inner)                                         => numericFloating(inner)
    case _                                                                    => false
  }

}
