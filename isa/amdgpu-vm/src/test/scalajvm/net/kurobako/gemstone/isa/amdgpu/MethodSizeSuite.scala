package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.testing.*

class MethodSizeSuite extends MethodSizeCheck(classOf[ISA], VMInterpreter.getClass) {
  // rendering runs once per definition, and the generated opcode table initialises once
  override protected def excluded(name: String): Boolean = name == "LaneRenderer" || name == "InstructionDefs"
}
