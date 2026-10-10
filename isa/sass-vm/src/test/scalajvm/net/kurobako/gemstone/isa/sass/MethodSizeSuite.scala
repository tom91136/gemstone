package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.testing.*

class MethodSizeSuite extends munit.FunSuite {
  // margin under HotSpot's HugeMethodLimit so small additions do not silently disable compilation of a hot path
  private val Limit = 7000

  for (
    owner <- Seq(
      VMIntegerCompiler.getClass,
      VMFloatingCompiler.getClass,
      VMPredicateCompiler.getClass,
      VMMemoryCompiler.getClass,
      VMTensorCompiler.getClass,
      VMControlCompiler.getClass,
      VMConstantBank.getClass,
      VMConditionCodeForms.getClass,
      VMCompilationContext.getClass,
      VMCompiler.getClass
    )
  ) test(s"${owner.getName} has no method too large for the JIT") {
    assertEquals(bytecode.methodSizes(owner).filter((_, size) => size > Limit), Map.empty[String, Int])
  }
}
