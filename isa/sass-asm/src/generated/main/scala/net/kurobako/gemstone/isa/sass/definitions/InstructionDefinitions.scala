// Generated SASS instruction decoding by mnemonic. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.sass
import javax.annotation.processing.Generated

import net.kurobako.gemstone.collection.*
import Instruction.*
@Generated(Array("sbt codegen"))
object InstructionDefinitions {
  lazy val decoders: Vec[FormDecoder] = Vec(
    InstructionDefinitions0.decoders,
    InstructionDefinitions1.decoders,
    InstructionDefinitions2.decoders,
    InstructionDefinitions3.decoders,
    InstructionDefinitions4.decoders,
    InstructionDefinitions5.decoders,
    InstructionDefinitions6.decoders
  ).flatten
  private lazy val byMnemonic: Map[String, Vec[FormDecoder]] = decoders.groupBy(_.definition.mnemonic.raw)
  def decode(
      mnemonic: String,
      modifiers: Vec[Modifier],
      operands: Vec[Operand],
      guard: Option[Guard]
  ): Either[String, Instruction] = {
    val decoders = byMnemonic.getOrElse(mnemonic, Vec.empty[FormDecoder])
    decoders.view.flatMap(_.decode(operands)).headOption.map(Instruction(_, modifiers, guard))
      .toRight(if (decoders.isEmpty) s"unknown SASS mnemonic $mnemonic"
      else s"invalid operands for $mnemonic: $operands")
  }
}
