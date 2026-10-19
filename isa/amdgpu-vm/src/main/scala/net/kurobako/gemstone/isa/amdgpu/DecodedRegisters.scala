package net.kurobako.gemstone.isa.amdgpu

import VMIR.*
import cats.syntax.either.*
import net.kurobako.gemstone.constraint.*

/** A register field of a decoded instruction, checked against its register bank. */
private object DecodedRegisters {
  extension (machine: InstructionDecoder.Decoded) {
    def vector(field: EncodingField): Either[NonEmpty[String], VectorRegisterIndex] =
      machine(field).toInt.refine[VectorRegisterIndex]
        .leftMap(_ => s"${machine.definition} $field ${machine(field)} exceeds the vector register bank")

    def scalar(field: EncodingField): Either[NonEmpty[String], ScalarRegisterIndex] =
      machine(field).toInt.refine[ScalarRegisterIndex]
        .leftMap(_ => s"${machine.definition} $field ${machine(field)} exceeds the scalar register bank")
  }
}
