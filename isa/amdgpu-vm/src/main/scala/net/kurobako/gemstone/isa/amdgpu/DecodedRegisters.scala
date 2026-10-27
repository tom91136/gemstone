package net.kurobako.gemstone.isa.amdgpu

import VMIR.*
import cats.syntax.either.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.domain.*

/** A register field of a decoded instruction, checked against its register bank. */
private object DecodedRegisters {
  extension (machine: InstructionDecoder.Decoded) {

    /** The value `field` holds in the domain the catalogue gives that field of this encoding. */
    def slot(field: EncodingField): Option[Slot] =
      DomainCatalogue.field(machine.encoding.isa, machine.encoding.format, machine.encoding.opcode, field)
        .flatMap(_.decode(machine(field)))

    def vector(field: EncodingField): Either[NonEmpty[String], VectorRegisterIndex] =
      machine(field).toInt.refine[VectorRegisterIndex]
        .leftMap(_ => s"${machine.definition} $field ${machine(field)} exceeds the vector register bank")

    /** The decode error when the field's domain defines no value at its code; none for unmodelled fields. */
    def outsideDomain(field: EncodingField): Option[String] =
      DomainCatalogue.field(machine.encoding.isa, machine.encoding.format, machine.encoding.opcode, field)
        .flatMap(_.undefined(machine.encoding.isa, field, machine(field)))

    def scalar(field: EncodingField): Either[NonEmpty[String], ScalarRegisterIndex] =
      machine(field).toInt.refine[ScalarRegisterIndex]
        .leftMap(_ => s"${machine.definition} $field ${machine(field)} exceeds the scalar register bank")

    /** A scalar read the field's domain must define, unless `absent` names its no-operand code. */
    def scalarOperand(field: EncodingField, absent: Long => Boolean = _ => false)
        : Either[NonEmpty[String], ScalarRegisterIndex] =
      scalar(field).flatMap { index =>
        outsideDomain(field) match {
          case Some(message) if !absent(machine(field)) => Left(message.unsafe[NonEmpty[String]])
          case _                                        => Right(index)
        }
      }
  }
}
