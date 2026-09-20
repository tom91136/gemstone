package net.kurobako.gemstone.isa.ptx.v9_3

import Program.*

import net.kurobako.gemstone.NoNul
import net.kurobako.gemstone.Vec

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

/** Specification metadata attached to one generated instruction definition. */
final case class Metadata(entries: Vec[Metadata.Entry] :| MinLength[1])

object Metadata {
  enum OperandConstraint {
    case Register
    case Address
    case VectorOperand
    case Symbol
    case Value
    case Unknown(sourceName: String :| MinLength[1])
  }

  /** One normalized alternative from an instruction's syntax rubric. */
  final case class InstructionForm(
      opcode: Opcode,
      operands: Vec[OperandConstraint],
      requiredModifierGroups: Vec[Set[ModifierName] :| MinLength[1]],
      optionalModifiers: Set[ModifierName],
      complete: Boolean
  )

  /** One PTX documentation section that defines the owning instruction. */
  final case class Entry(
      id: String :| (MinLength[1] & NoNul),
      heading: String :| (MinLength[1] & NoNul),
      title: String :| (MinLength[1] & NoNul),
      category: String :| (MinLength[1] & NoNul),
      syntax: Vec[String :| (MinLength[1] & NoNul)] :| MinLength[1],
      introduced: ISA.Version,
      minimumTarget: ISA.Target,
      forms: Vec[InstructionForm]
  )
}
