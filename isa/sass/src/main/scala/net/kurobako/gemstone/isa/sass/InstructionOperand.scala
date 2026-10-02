package net.kurobako.gemstone.isa.sass

import scala.annotation.tailrec

/** Operand refinements used by generated instruction forms. */
object InstructionOperand {

  @tailrec
  private def leaf(value: Operand): Operand = value match {
    case Operand.Unary(inner, _, _, _) => leaf(inner)
    case Operand.Qualified(inner, _)   => leaf(inner)
    case Operand.Decorated(inner, _)   => leaf(inner)
    case other                         => other
  }

  private def matches(value: Operand)(accepts: Operand => Boolean): Boolean = accepts(leaf(value))

  object Register {
    def from(value: Operand): Either[String, Register] =
      Either.cond(
        matches(value) { case Operand.Register(_, _) => true; case _ => false },
        value,
        "expected register operand"
      )
    def unapply(value: Operand): Option[Register] = from(value).toOption
  }

  object Predicate {
    def from(value: Operand): Either[String, Predicate] =
      Either.cond(
        matches(value) { case Operand.Predicate(_, _, _) => true; case _ => false },
        value,
        "expected predicate operand"
      )
    def unapply(value: Operand): Option[Predicate] = from(value).toOption
  }

  object Immediate {
    def from(value: Operand): Either[String, Immediate] =
      Either.cond(
        matches(value) {
          case Operand.Integer(_) | Operand.Bits(_, _) | Operand.FloatingBits(_, _, _) |
              Operand.Floating(_, _, _) | Operand.FloatingSymbol(_) => true
          case _ => false
        },
        value,
        "expected immediate operand"
      )
    def unapply(value: Operand): Option[Immediate] = from(value).toOption
  }

  object Address {
    def from(value: Operand): Either[String, Address] =
      Either.cond(
        matches(value) { case Operand.Address(_, _, _, _, _, _) => true; case _ => false },
        value,
        "expected address operand"
      )
    def unapply(value: Operand): Option[Address] = from(value).toOption
  }

  object ConstantBank {
    def from(value: Operand): Either[String, ConstantBank] =
      Either.cond(
        matches(value) { case Operand.ConstantBank(_, _) => true; case _ => false },
        value,
        "expected constant-bank operand"
      )
    def unapply(value: Operand): Option[ConstantBank] = from(value).toOption
  }

  object SpecialRegister {
    def from(value: Operand): Either[String, SpecialRegister] =
      Either.cond(
        matches(value) { case Operand.SpecialRegister(_) => true; case _ => false },
        value,
        "expected special-register operand"
      )
    def unapply(value: Operand): Option[SpecialRegister] = from(value).toOption
  }

  object Group {
    def from(value: Operand): Either[String, Group] =
      Either.cond(matches(value) { case Operand.Group(_) => true; case _ => false }, value, "expected group operand")
    def unapply(value: Operand): Option[Group] = from(value).toOption
  }

  object Symbol {
    def from(value: Operand): Either[String, Symbol] =
      Either.cond(matches(value) { case Operand.Symbol(_) => true; case _ => false }, value, "expected symbol operand")
    def unapply(value: Operand): Option[Symbol] = from(value).toOption
  }

  opaque type Register <: Operand        = Operand
  opaque type Predicate <: Operand       = Operand
  opaque type Immediate <: Operand       = Operand
  opaque type Address <: Operand         = Operand
  opaque type ConstantBank <: Operand    = Operand
  opaque type SpecialRegister <: Operand = Operand
  opaque type Group <: Operand           = Operand
  opaque type Symbol <: Operand          = Operand
}
