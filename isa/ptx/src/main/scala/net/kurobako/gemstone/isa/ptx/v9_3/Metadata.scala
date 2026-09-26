package net.kurobako.gemstone.isa.ptx.v9_3

import Program.*

import net.kurobako.gemstone.NonEmptyString
import net.kurobako.gemstone.NonEmptyVec
import net.kurobako.gemstone.Vec

/** Specification metadata attached to one generated instruction definition. */
final case class Metadata(entries: NonEmptyVec[Metadata.Entry])

object Metadata {
  enum OperandConstraint {
    case Register
    case Address
    case VectorOperand
    case Symbol
    case Value
    case Unknown(sourceName: NonEmptyString)
  }

  opaque type ModifierGroup = Set[ModifierName]
  object ModifierGroup {
    inline def either(value: Set[ModifierName]): Either[String, ModifierGroup] =
      if (value.isEmpty) Left("modifier group must not be empty") else Right(value)

    inline def unsafe(value: Set[ModifierName]): ModifierGroup =
      either(value).fold(message => throw IllegalArgumentException(message), identity)

    def apply(first: ModifierName, rest: ModifierName*): ModifierGroup = (first +: rest).toSet

    extension (value: ModifierGroup) def raw: Set[ModifierName] = value
  }

  /** One normalized alternative from an instruction's syntax rubric. */
  final case class InstructionForm(
      opcode: Opcode,
      operands: Vec[OperandConstraint],
      requiredModifierGroups: Vec[ModifierGroup],
      optionalModifiers: Set[ModifierName],
      complete: Boolean
  )

  /** One PTX documentation section that defines the owning instruction. */
  final case class Entry(
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
