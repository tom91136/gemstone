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
      opcode: String,
      bindings: Map[String, BigInt],
      fields: Map[String, BigInt],
      residual: BigInt
  ) derives ReadWriter

  private case class Resolved(target: Target, address: Int >= 0, database: NVDisasm.Database, form: NVDisasm.Form)

  def print(instruction: DecodedInstruction, machine: DecodedInstruction.Machine): Either[String, String] =
    describe(instruction, machine).map(write(_))

  def describe(instruction: DecodedInstruction, machine: DecodedInstruction.Machine): Either[String, Annotation] =
    for {
      form <- machine.target.database.byForm.get(machine.form).toRight(s"unknown SASS form ${machine.form}")
      word <- instruction.encodingWord.toRight("lossless assembly requires encoding information")
    } yield Annotation(
      machine.target.name,
      instruction.address,
      form.name,
      machine.opcode,
      machine.bindings,
      machine.fields,
      word & ~form.assignedMask
    )

  def parse(text: String): Either[String, Annotation] =
    Try(read[Annotation](text)).toEither.left.map(error => s"invalid encoding annotation: ${error.getMessage}")

  private def resolve(annotation: Annotation): Either[String, Resolved] = for {
    target  <- Target.parse(annotation.target)
    address <- annotation.address.refine[Int >= 0].left.map(_.raw)
    database = target.database
    form <- database.byForm.get(annotation.form).toRight(s"unknown SASS form ${annotation.form}")
  } yield Resolved(target, address, database, form)

  def decode(
      source: String,
      annotation: Annotation
  ): Either[String, (bytes: Array[Byte], instruction: DecodedInstruction)] = for {
    expected     <- InstructionSyntax.parse(source)
    resolved     <- resolve(annotation)
    bytes        <- encode(annotation, resolved)
    actual       <- InstructionDecoder.decode(resolved.address, bytes, resolved.target)
    expectedText <- InstructionSyntax.print(expected)
    actualText   <- InstructionSyntax.print(actual.operation)
    _ <- Either.cond(expectedText == actualText, (), "encoding annotation does not describe the printed instruction")
  } yield (bytes, actual)

  def encode(annotation: Annotation): Either[String, Array[Byte]] = resolve(annotation).flatMap(encode(annotation, _))

  private def encode(annotation: Annotation, resolved: Resolved): Either[String, Array[Byte]] = {
    val form = resolved.form
    for {
      _ <- Either.cond(
        annotation.residual >= 0 && annotation.residual.bitLength <= resolved.database.width &&
          (annotation.residual & form.assignedMask) == 0,
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
        } yield FieldDecoder.deposit(word, field.positions, value)
      }
      encoded <- Encoder.encode(resolved.database, form, annotation.opcode, annotation.bindings, Some(preserved))
    } yield encoded
  }
}
