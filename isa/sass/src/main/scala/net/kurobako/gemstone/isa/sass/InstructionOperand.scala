package net.kurobako.gemstone.isa.sass

import scala.annotation.tailrec

/** Operand refinements used by generated instruction forms. Each accepts its leaf kind through unary flags and
  * selectors, retaining the complete operand.
  */
object InstructionOperand {
  class Kind[A <: Operand] private[InstructionOperand] (
      description: String,
      accepts: Operand => Boolean,
      refined: Operand => A
  ) {
    def from(value: Operand): Either[String, A] =
      Either.cond(accepts(leaf(value)), refined(value), s"expected $description operand")
    def unapply(value: Operand): Option[A] = from(value).toOption
  }

  @tailrec
  private def leaf(value: Operand): Operand = value match {
    case Operand.Unary(inner, _, _, _) => leaf(inner)
    case Operand.Qualified(inner, _)   => leaf(inner)
    case other                         => other
  }

  val Register: Kind[Register]   = Kind("register", { case _: Operand.Register => true; case _ => false }, identity)
  val Predicate: Kind[Predicate] = Kind("predicate", { case _: Operand.Predicate => true; case _ => false }, identity)
  val Immediate: Kind[Immediate] = Kind(
    "immediate",
    {
      case _: Operand.Integer | _: Operand.Bits | _: Operand.FloatingBits | _: Operand.Floating |
          _: Operand.FloatingSymbol => true
      case _ => false
    },
    identity
  )
  val Address: Kind[Address]           = Kind("address", { case _: Operand.Address => true; case _ => false }, identity)
  val ConstantBank: Kind[ConstantBank] =
    Kind("constant-bank", { case _: Operand.ConstantBank => true; case _ => false }, identity)
  val SpecialRegister: Kind[SpecialRegister] =
    Kind("special-register", { case _: Operand.SpecialRegister => true; case _ => false }, identity)
  val Group: Kind[Group]   = Kind("group", { case _: Operand.Group => true; case _ => false }, identity)
  val Symbol: Kind[Symbol] = Kind("symbol", { case _: Operand.Symbol => true; case _ => false }, identity)

  opaque type Register <: Operand        = Operand
  opaque type Predicate <: Operand       = Operand
  opaque type Immediate <: Operand       = Operand
  opaque type Address <: Operand         = Operand
  opaque type ConstantBank <: Operand    = Operand
  opaque type SpecialRegister <: Operand = Operand
  opaque type Group <: Operand           = Operand
  opaque type Symbol <: Operand          = Operand
}
