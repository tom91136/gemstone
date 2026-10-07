package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.testing.*

class MethodSizeSuite extends munit.FunSuite {
  // margin under HotSpot's HugeMethodLimit so small additions do not silently disable compilation of a hot path
  private val Limit = 7000

  for (
    owner <- Seq(
      VMSemantics.getClass,
      VMArithmetic.getClass,
      VMInterpreter.getClass,
      Class.forName("net.kurobako.gemstone.isa.nvidia.VMInterpreter$Execution"),
      classOf[VMWriteBuffer],
      VMTensor.getClass,
      VMBuiltins.getClass,
      VMLaneMask.getClass,
      classOf[VMLaneMask]
    )
  ) test(s"${owner.getName} has no method too large for the JIT") {
    assertEquals(bytecode.methodSizes(owner).filter((_, size) => size > Limit), Map.empty[String, Int])
  }
}
