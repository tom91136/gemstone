package net.kurobako.gemstone.isa.sass.asm

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.*

object Program {
  case class LabelReference(operand: Int >= 0, name: NonEmpty[String])

  enum Statement {
    case Label(name: NonEmpty[String])
    case Operation(
        instruction: Instruction,
        preserved: Option[DecodedInstruction] = None,
        reference: Option[LabelReference] = None
    )
    case Bundle(control: CodeWords.BundleControl)
  }
}

/** Assembly statements retain symbolic destinations and, when decoded, exact machine choices and bundle controls. */
case class Program(target: Option[Target], statements: Vec[Program.Statement])
