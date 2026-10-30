package net.kurobako.gemstone.isa.spirv

import assembly.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.testing.Suite

class FreshIdSuite extends Suite {
  test("fresh ids start at the bound and raise it past themselves") {
    val module = spv"""
      $openCL
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
    """
    val bound = module.header.bound.raw
    val fresh = module.freshIds(2)
    assertEquals(fresh.ids, Vec(bound, bound + 1))
    assertEquals(fresh.module.header.bound.raw, bound + 2)
    assertEquals(fresh.module.instructions, module.instructions)
    assertEquals(module.freshIds(0).ids, Vec.empty[Int])
  }
}
