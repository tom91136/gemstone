package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import scala.reflect.TypeTest

/** Structural operand queries shared by the SASS compiler families. */
private object VMOperand {
  def is[A <: Instruction](instruction: Instruction)(using test: TypeTest[Instruction, A]): Boolean =
    test.unapply(instruction).isDefined

  /** Vendor extensions can be preserved by the decoder and printer without acquiring execution semantics. */
  def hasUnknownDomain(value: Operand): Boolean = value match {
    case Operand.Register(_: RegisterBank.Unknown, _)         => true
    case Operand.SpecialRegister(_: SpecialRegister.Unknown)  => true
    case Operand.FloatingSymbol(_: FloatingConstant.Unknown)  => true
    case Operand.FloatingBits(_: FloatEncoding.Unknown, _, _) => true
    case Operand.Address(base, index, _, _, mode, descriptor) =>
      base.exists(hasUnknownDomain(_)) || index.exists(hasUnknownDomain(_)) ||
      mode.exists { case AddressMode.Unknown(_) => true; case _ => false } || descriptor.exists(hasUnknownDomain(_))
    case Operand.Qualified(inner, selectors) =>
      selectors.exists { case OperandSelector.Unknown(_) => true; case _ => false } || hasUnknownDomain(inner)
    case Operand.Unary(inner, _, _, _)   => hasUnknownDomain(inner)
    case Operand.ConstantBank(_, offset) => hasUnknownDomain(offset)
    case Operand.Group(values)           => values.exists(hasUnknownDomain(_))
    case _                               => false
  }

  // Normalise symbolic zero registers independently of each target's physical encoding.
  val Zero: Operand.Register        = Operand.Register(RegisterBank.R, 255)
  val UniformZero: Operand.Register = Operand.Register(RegisterBank.UR, 255)
  val True: Operand.Predicate       = Operand.Predicate(None, false, false)

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
  def isZero(value: Operand): Boolean                         = base(value) == Zero || base(value) == UniformZero
  def isRegister(value: Operand, bank: RegisterBank = RegisterBank.R, allowZero: Boolean = false): Boolean =
    base(value) match {
      case Operand.Register(actual, index) => actual == bank && index.raw < RegisterBank.capacity(bank).getOrElse(0) &&
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
    case Operand.Unary(_, true, _, _) => true
    case _                            => false
  }

  def positive(value: Operand): Operand = value match {
    case Operand.Unary(inner, true, false, false)         => inner
    case Operand.Unary(inner, true, complement, absolute) => Operand.Unary(inner, false, complement, absolute)
    case other                                            => other
  }
  def registerIndex(value: Operand): Int in 0 ~ 255 = base(value) match {
    case Operand.Register(_, index) => index
    case _                          => throw IllegalArgumentException(s"expected register, found $value")
  }
  def isPredicate(value: Operand, allowNegated: Boolean = false, allowUniform: Boolean = false): Boolean = value match {
    case Operand.Predicate(_, uniform, negated) =>
      (allowNegated || !negated) && (allowUniform || !uniform)
    case _ => false
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
    case Operand.Unary(_, _, true, _) => true
    case _                            => false
  }
  def unaryValue(value: Operand): Operand = value match {
    case Operand.Unary(inner, _, _, _) => inner
    case other                         => other
  }
  @annotation.tailrec
  def numericFloating(value: Operand): Boolean = value match {
    case _: Operand.Integer | _: Operand.Floating | _: Operand.FloatingSymbol => true
    case Operand.FloatingBits(_, width, _) if width.raw == 64                 => true
    case Operand.Unary(inner, _, _, _)                                        => numericFloating(inner)
    case _                                                                    => false
  }
  def plainAddress(value: Operand): Boolean = value match {
    case Operand.Address(Some(reg), None, displacement, scale, None, None) if scale.raw == 1 =>
      displacement == 0 && isRegister(reg)
    case _ => false
  }

}
