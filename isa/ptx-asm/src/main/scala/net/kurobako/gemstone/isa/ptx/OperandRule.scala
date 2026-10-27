package net.kurobako.gemstone.isa.ptx

import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*

/** A prose rule on an immediate operand, from the document section `section`. */
case class OperandRule(
    opcodes: Set[String],
    any: Set[String],
    all: Set[String],
    none: Set[String],
    position: Int,
    kind: String,
    values: Vec[Long],
    elements: Boolean,
    section: String
) {
  def applies(opcode: String, modifiers: Set[String]): Boolean =
    opcodes.contains(opcode) && (any.isEmpty || any.exists(modifiers)) && all.forall(modifiers) &&
      !none.exists(modifiers)

  def check(value: BigInt): Option[OperandViolation] = kind match {
    case "range" =>
      Option.when(value < values(0) || value > values(1))(OperandViolation.OutOfRange(value, values(0), values(1)))
    case "multiple" => Option.when(value % values(0) != 0)(OperandViolation.NotMultiple(value, values(0)))
    case "oneof"    => Option.when(!values.exists(value == BigInt(_)))(OperandViolation.NotOneOf(value, values))
    case _          =>
      val limit = values.headOption
      Option.when(value <= 0 || (value & (value - 1)) != 0 || limit.exists(value > _))(
        OperandViolation.NotPowerOfTwo(value, limit)
      )
  }
}
