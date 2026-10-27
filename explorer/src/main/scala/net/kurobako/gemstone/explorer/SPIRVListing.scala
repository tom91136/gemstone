package net.kurobako.gemstone.explorer

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.spirv.*
import net.kurobako.gemstone.isa.spirv.asm.*

/** SPIR-V instruction addresses are instruction indexes within the module, and the VM lowers one op per module
  * instruction, so the pc it reports is the index a listing names.
  */
private object SPIRVListing {
  def lines(image: Vec[Byte]): Either[String, Vec[ProgramLine]] =
    ISA.decode(image).left.map(_.message.raw).map { module =>
      module.instructions.zipWithIndex.map { (instruction, index) =>
        ProgramLine(None, index.toLong, Printer.render(module, instruction), instruction.definition.opcode)
      }
    }
}
