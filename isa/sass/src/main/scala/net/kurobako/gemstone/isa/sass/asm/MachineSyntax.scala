package net.kurobako.gemstone.isa.sass.asm

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.*
import scala.util.Try
import upickle.default.*

/** Lossless assembly annotations name field values and residual bits; they never substitute an opaque byte string for
  * encoding. Annotated input is encoded and decoded again before its displayed instruction is accepted.
  */
private object MachineSyntax {
  case class Annotation(
      target: String,
      address: Int,
      form: String,
      formId: Option[Int],
      opcode: String,
      bindings: Map[String, BigInt],
      fields: Map[String, BigInt],
      residual: BigInt
  ) derives ReadWriter

  def print(instruction: DecodedInstruction, machine: DecodedInstruction.Machine): Either[String, String] =
    describe(instruction, machine).map(write(_))

  def describe(instruction: DecodedInstruction, machine: DecodedInstruction.Machine): Either[String, Annotation] =
    for {
      database <- NVDisasm.forTarget(machine.target).toRight(s"unknown SASS target ${machine.target}")
      form     <- database.byForm.get(machine.form.name -> machine.form.formId)
        .toRight(s"unknown SASS form ${machine.form.name}")
      word <- instruction.encodingWord.toRight("lossless assembly requires encoding information")
    } yield Annotation(
      machine.target,
      instruction.address.raw,
      form.name,
      form.formId,
      machine.opcode,
      machine.bindings,
      machine.fields,
      word & ~form.assignedMask
    )

  def parse(text: String): Either[String, Annotation] =
    Try(read[Annotation](text)).toEither.left.map(error => s"invalid encoding annotation: ${error.getMessage}")

  def decode(
      source: String,
      annotation: Annotation
  ): Either[String, (bytes: Array[Byte], instruction: DecodedInstruction)] = for {
    expected     <- InstructionSyntax.parse(source)
    bytes        <- encode(annotation)
    actual       <- InstructionDecoder.decodeTyped(annotation.address, bytes, annotation.target)
    expectedText <- InstructionSyntax.print(expected)
    actualText   <- InstructionSyntax.print(actual.operation)
    _            <- Either.cond(
      expectedText == actualText,
      (),
      "encoding annotation does not describe the printed instruction"
    )
  } yield (bytes, actual)

  def encode(annotation: Annotation): Either[String, Array[Byte]] = for {
    database <- NVDisasm.forTarget(annotation.target).toRight(s"unknown SASS target ${annotation.target}")
    form <- database.byForm.get(annotation.form -> annotation.formId).toRight(s"unknown SASS form ${annotation.form}")
    _    <- Either.cond(annotation.address >= 0, (), "negative instruction address")
    mask = form.assignedMask
    _ <- Either.cond(
      annotation.residual >= 0 && annotation.residual.bitLength <= database.width &&
        (annotation.residual & mask) == 0,
      (),
      "invalid residual instruction bits"
    )
    preserved <- form.fields.foldLeft[Either[String, BigInt]](Right(annotation.residual)) { (result, field) =>
      for {
        word  <- result
        value <- annotation.fields.get(field.name).toRight(s"missing preserved field ${field.name}")
        _     <- Either.cond(
          value >= 0 && value.bitLength <= field.positions.size,
          (),
          s"invalid preserved field ${field.name}"
        )
      } yield field.positions.zipWithIndex.foldLeft(word) { case (bits, (physical, logical)) =>
        if (value.testBit(logical)) bits.setBit(physical) else bits
      }
    }
    encoded <- Encoder.encode(database, form, annotation.opcode, annotation.bindings, Some(preserved))
  } yield encoded
}
