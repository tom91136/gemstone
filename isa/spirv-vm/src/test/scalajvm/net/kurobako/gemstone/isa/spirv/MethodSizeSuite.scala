package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.testing.*

class MethodSizeSuite extends MethodSizeCheck(classOf[Header], VMProgram.getClass) {
  // the generated grammar tables initialise once
  override protected def excluded(name: String): Boolean = name == "Instruction" || name == "ExtendedInstruction"
}
