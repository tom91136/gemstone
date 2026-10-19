package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

object DecodedInstruction {
  case class Machine(
      target: Target,
      form: String,
      opcode: String,
      bindings: Map[String, BigInt],
      fields: Map[String, BigInt]
  )

}

/** An instruction located in a code section. The generated operation owns all semantic operands; machine information
  * retains encoding choices that assembly syntax does not express.
  */
case class DecodedInstruction(
    address: Int >= 0,
    operation: Instruction,
    encodingWord: Option[BigInt],
    machine: Option[DecodedInstruction.Machine]
) derives CanEqual {
  def mnemonic: NonEmpty[String]           = operation.mnemonic
  def modifiers: Vec[Instruction.Modifier] = operation.modifiers
  def operands: Vec[Operand]               = operation.operands
  def guard: Option[Instruction.Guard]     = operation.guard

  def rewrite(operation: Instruction): DecodedInstruction =
    DecodedInstruction(address, operation, None, None)

  def rewrite(
      mnemonic: NonEmpty[String] = this.mnemonic,
      modifiers: Vec[Instruction.Modifier] = this.modifiers,
      operands: Vec[Operand] = this.operands,
      guard: Option[Instruction.Guard] = this.guard
  ): DecodedInstruction =
    InstructionDefinitions.decode(mnemonic.raw, modifiers, operands, guard)
      .fold(
        error => throw IllegalArgumentException(error),
        value => DecodedInstruction(address, value, None, None)
      )
}
