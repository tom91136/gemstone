package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.*

object ISA {
  val version: Version                          = Instruction.version
  val instructions: Vec[Instruction.Definition] = Instruction.definitions

  def validate(program: Program, target: Target): ValidationOutcome[Validation.Issue] =
    Validation.validate(program, version, target)

}
