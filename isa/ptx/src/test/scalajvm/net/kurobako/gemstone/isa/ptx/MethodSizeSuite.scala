package net.kurobako.gemstone.isa.ptx

import net.kurobako.gemstone.testing.*

class MethodSizeSuite extends munit.FunSuite {
  // margin under HotSpot's HugeMethodLimit so small additions do not silently disable compilation of a hot path
  private val Limit = 7000

  for (
    owner <- Seq(
      VMInstructionCompiler.getClass,
      VMMemoryCompiler.getClass,
      VMTensorCompiler.getClass,
      VMCompiler.getClass,
      Lowering.getClass,
      classOf[Lowering.Scope]
    )
  ) test(s"${owner.getName} has no method too large for the JIT") {
    assertEquals(bytecode.methodSizes(owner).filter((_, size) => size > Limit), Map.empty[String, Int])
  }
}
