package net.kurobako.gemstone.isa.ptx

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
    case Unknown
  }

  type ModifierGroup = ModifierGroup.Type
  object ModifierGroup extends Newtype[NonEmpty[Set[Modifier]]] {
    def apply(
        first: Modifier,
        rest: Modifier*
    ): ModifierGroup = (first +: rest).toSet.unsafe[ModifierGroup]
  }

  /** One normalised alternative from an instruction's syntax rubric; `unorderedModifiers` may appear in any order among
    * themselves.
    */
  case class InstructionForm(
      operands: Vec[OperandConstraint],
      requiredModifierGroups: Vec[ModifierGroup],
      optionalModifiers: Set[Modifier],
      unorderedModifiers: Set[Modifier],
      complete: Boolean
  )

  /** One PTX documentation section that defines the owning instruction; `modifierTargets` raises the minimum target for
    * instructions carrying every modifier of a combination.
    */
  case class Entry(
      introduced: Version,
      minimumTarget: Target,
      modifierTargets: Map[Set[Modifier], Target],
      forms: Vec[InstructionForm]
  )
}

/** Specification metadata attached to one generated instruction definition. */
case class Metadata(entries: NonEmpty[Vec[Metadata.Entry]])
