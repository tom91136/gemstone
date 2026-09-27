package net.kurobako.gemstone.isa.ptx.v9_3

import Program.*

import net.kurobako.gemstone.Constraint.NonEmptyString
import net.kurobako.gemstone.NonEmptyVec
import net.kurobako.gemstone.Vec

/** Specification metadata attached to one generated instruction definition. */
case class Metadata(entries: NonEmptyVec[Metadata.Entry])

object Metadata {
  enum OperandConstraint {
    case Register
    case Address
    case VectorOperand
    case Symbol
    case Value
    case Unknown(sourceName: NonEmptyString)
  }

  opaque type ModifierGroup = Set[NonEmptyString]
  object ModifierGroup {
    inline def either(value: Set[NonEmptyString]): Either[String, ModifierGroup] =
      if (value.isEmpty) Left("modifier group must not be empty") else Right(value)

    inline def unsafe(value: Set[NonEmptyString]): ModifierGroup =
      either(value).fold(message => throw IllegalArgumentException(message), identity)

    def apply(first: NonEmptyString, rest: NonEmptyString*): ModifierGroup = (first +: rest).toSet

    extension (value: ModifierGroup) def raw: Set[NonEmptyString] = value
  }

  /** One normalized alternative from an instruction's syntax rubric. */
  case class InstructionForm(
      opcode: NonEmptyString,
      operands: Vec[OperandConstraint],
      requiredModifierGroups: Vec[ModifierGroup],
      optionalModifiers: Set[NonEmptyString],
      complete: Boolean
  )

  /** One PTX documentation section that defines the owning instruction. */
  case class Entry(
      id: NonEmptyString,
      heading: NonEmptyString,
      title: NonEmptyString,
      category: NonEmptyString,
      syntax: NonEmptyVec[NonEmptyString],
      introduced: ISA.Version,
      minimumTarget: ISA.Target,
      forms: Vec[InstructionForm]
  )
}
