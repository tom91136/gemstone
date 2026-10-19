package net.kurobako.gemstone.isa.sass

import scala.annotation.tailrec

/** Operand refinements used by generated instruction forms. Each accepts its leaf kind through unary flags and
  * selectors, retaining the complete operand.
  */
object InstructionOperand {
  object Kind {
    def apply[A <: Operand](description: String, accepts: Operand => Boolean, refined: Operand => A): Kind[A] =
      new Kind(description, accepts, refined)
  }

  class Kind[A <: Operand] private (
      description: String,
      accepts: Operand => Boolean,
      refined: Operand => A
  ) {
    def from(value: Operand): Either[String, A] =
      Either.cond(admits(value), refined(value), s"expected $description operand")
    def unapply(value: Operand): Option[A] = from(value).toOption

    def admits(value: Operand): Boolean = accepts(leaf(value))

    /** `value` as this kind, throwing IllegalArgumentException when it does not admit it. */
    def unsafe(value: Operand): A =
      if (admits(value)) refined(value) else throw IllegalArgumentException(s"expected $description operand")
  }

  @tailrec
  private def leaf(value: Operand): Operand = value match {
    case Operand.Unary(value = inner) => leaf(inner)
    case Operand.Qualified(inner, _)  => leaf(inner)
    case other                        => other
  }

  val Register: Kind[Register]   = Kind("register", { case Operand.Register(_, _) => true; case _ => false }, identity)
  val Predicate: Kind[Predicate] = Kind("predicate", { case _: Operand.Predicate => true; case _ => false }, identity)
  val Immediate: Kind[Immediate] = Kind(
    "immediate",
    {
      case Operand.Integer(_) | Operand.Bits(_, _) | _: Operand.FloatingBits | _: Operand.Floating |
          Operand.FloatingSymbol(_) => true
      case _ => false
    },
    identity
  )
  val Address: Kind[Address]           = Kind("address", { case _: Operand.Address => true; case _ => false }, identity)
  val ConstantBank: Kind[ConstantBank] =
    Kind("constant-bank", { case Operand.ConstantBank(_, _) => true; case _ => false }, identity)
  val SpecialRegister: Kind[SpecialRegister] =
    Kind("special-register", { case Operand.SpecialRegister(_) => true; case _ => false }, identity)
  val Group: Kind[Group]   = Kind("group", { case Operand.Group(_) => true; case _ => false }, identity)
  val Symbol: Kind[Symbol] = Kind("symbol", { case Operand.Symbol(_) => true; case _ => false }, identity)

  opaque type Register <: Operand        = Operand
  opaque type Predicate <: Operand       = Operand
  opaque type Immediate <: Operand       = Operand
  opaque type Address <: Operand         = Operand
  opaque type ConstantBank <: Operand    = Operand
  opaque type SpecialRegister <: Operand = Operand
  opaque type Group <: Operand           = Operand
  opaque type Symbol <: Operand          = Operand
}
