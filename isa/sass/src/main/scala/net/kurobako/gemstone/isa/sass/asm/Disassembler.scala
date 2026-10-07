package net.kurobako.gemstone.isa.sass.asm

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.sass.*
import net.kurobako.gemstone.isa.sass.CodeWords.BundleControl as Control
import net.kurobako.gemstone.isa.sass.asm.Program.Statement.*

/** Binary frontend producing programs that `Printer` and `Assembler` round-trip. */
class Disassembler(target: Target) {
  def decode(bytes: Vec[Byte]): Either[Error, Program] =
    CodeSection.decode(bytes.toArray, target).left.map(value => Error.Disassembly(Error.detail(value))).map {
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
