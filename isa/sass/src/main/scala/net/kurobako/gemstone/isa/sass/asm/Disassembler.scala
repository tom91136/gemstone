package net.kurobako.gemstone.isa.sass.asm

import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.*
import net.kurobako.gemstone.isa.sass.SASSCodeWords.BundleControl as Control
import net.kurobako.gemstone.isa.sass.asm.Program.Statement.*

/** Public binary/text frontend. Execution and printing consume the same generated instruction cases. */
object Disassembler {
  def decode(bytes: Array[Byte], target: String, address: Int = 0): Either[String, DecodedInstruction] =
    InstructionDecoder.decodeTyped(address, bytes, target)

  def disassemble(bytes: Array[Byte], target: String, address: Int = 0): Either[String, String] =
    decode(bytes, target, address).flatMap(Printer.print(_))
}

class Disassembler(target: Target)
    extends isa.Decoder[Vec[Byte], Program, Error] {
  override def decode(bytes: Vec[Byte]): Either[Error, Program] =
    CodeSection.decode(bytes.toArray, target.sm.raw).left.map(value => Error.Disassembly(Error.detail(value))).map {
      section =>
        val offsets    = section.layout.bundles.scanLeft(0)((offset, bundle) => offset + bundle.words.size)
        val statements = section.layout.bundles.zip(offsets).flatMap { (bundle, index) =>
          val control = bundle.control match {
            case Control.Modern => Vec.empty[Program.Statement]
            case value          => Vec[Program.Statement](Bundle(value))
          }
          val operations = section.instructions.slice(index, index + bundle.words.size).map { instruction =>
            Operation(instruction.operation, Some(instruction)): Program.Statement
          }
          control ++ operations
        }
        Program(Some(target), statements)
    }
}
