package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*

object Metadata {
  enum OperandConstraint {
    case Register
    case Address
    case VectorOperand
    case Symbol
    case Value
    case Unknown(sourceName: NonEmpty[String])
  }

  type ModifierGroup = ModifierGroup.Type
  object ModifierGroup extends Newtype[NonEmpty[Set[NonEmpty[String]]]] {
    def apply(
        first: NonEmpty[String],
        rest: NonEmpty[String]*
    ): ModifierGroup = (first +: rest).toSet.unsafe[ModifierGroup]
  }

  /** One normalized alternative from an instruction's syntax rubric. */
  case class InstructionForm(
      opcode: NonEmpty[String],
      operands: Vec[OperandConstraint],
      requiredModifierGroups: Vec[ModifierGroup],
      optionalModifiers: Set[NonEmpty[String]],
      complete: Boolean
  )

  /** One PTX documentation section that defines the owning instruction. */
  case class Entry(
      id: NonEmpty[String],
      heading: NonEmpty[String],
      title: NonEmpty[String],
      category: NonEmpty[String],
      syntax: NonEmpty[Vec[NonEmpty[String]]],
      introduced: Version,
      minimumTarget: Target,
      forms: Vec[InstructionForm]
  )
}

/** Specification metadata attached to one generated instruction definition. */
case class Metadata(entries: NonEmpty[Vec[Metadata.Entry]])
