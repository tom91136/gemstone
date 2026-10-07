package net.kurobako.gemstone.isa.amdgpu.asm

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.*
import net.kurobako.gemstone.isa.amdgpu.asm.Instruction as AMDGPUInstruction

object Program {
  enum Statement {
    case Comment(text: NonEmpty[String])
    case Instruction(value: AMDGPUInstruction)
    case InstructionPair(
        left: AMDGPUInstruction,
        right: AMDGPUInstruction
    )
    case Label(name: String)
    case Directive(name: String, arguments: String)
  }
}

/** A parsed AMDGPU assembly program. Source locations are deliberately kept out of the semantic tree. */
case class Program(statements: Vec[Program.Statement])
