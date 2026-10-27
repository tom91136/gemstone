package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.testing.*

class MethodSizeSuite extends MethodSizeCheck(VMSemantics.getClass) {
  // rendering runs once per definition, not per executed instruction
  override protected def excluded(name: String): Boolean =
    name == "StepRenderer" || name == "MachineSemantics" || name == "TensorMemoryRender"

  test("MachineSemantics render methods stay under 16 KB") {
    val oversized = bytecode.methodSizes(MachineSemantics.getClass).filter((method, size) =>
      method.startsWith("render") && size >= 16000
    )
    assertEquals(oversized, Map.empty[String, Int])
  }
}
