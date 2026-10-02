package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*

object CubinSuiteFixtures {
  def saxpyModule(sm: Int): Cubin.Module = {
    val metadata = SaxpyFixtures.parameters(sm)
    SASSFixture.module(
      sm,
      Vec(SASSFixture.Kernel(
        "saxpy",
        metadata.base,
        Vec(0 -> 8, 8 -> 8, 16 -> 4, 20 -> 4),
        registers = metadata.registers
      ))
    )
  }

  def module: Cubin.Module            = saxpyModule(86)
  def functions: Vec[DecodedFunction] =
    ListingDecoder.parse(SaxpyFixtures.listings(86), "saxpy").fold(
      error => throw IllegalStateException(error),
      identity
    )
}
