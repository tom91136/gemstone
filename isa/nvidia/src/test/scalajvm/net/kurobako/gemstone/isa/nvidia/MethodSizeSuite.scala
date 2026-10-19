package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.testing.*

class MethodSizeSuite extends MethodSizeCheck(VMSemantics.getClass) {
  // rendering runs once per definition, not per executed instruction
  override protected def excluded(name: String): Boolean = name == "StepRenderer"
}
