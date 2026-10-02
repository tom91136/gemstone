package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*

class MachineSuite extends munit.FunSuite {
  test("an executable program looks up valid PCs and rejects out-of-range PCs") {
    val program = ExecutableProgram(Vec("first", "second"))
    assertEquals(program.at(Pc.Entry), Some("first"))
    assertEquals(program.at(Pc(2)), None)
  }

  test("lane masks support CUDA-sized blocks and set operations") {
    val mask = VMLaneMask.all(256).toOption.get
    assertEquals(mask.size, 256)
    val selected  = mask.filterLanes(_ % 32 == 0)
    val remaining = mask.subtract(selected)
    val reunited  = selected.union(remaining)
    val overlap   = selected.intersect(remaining)
    (0 until 256).foreach { lane =>
      assertEquals(selected.contains(lane), lane  % 32 == 0, s"selected lane $lane")
      assertEquals(remaining.contains(lane), lane % 32 != 0, s"remaining lane $lane")
      assert(reunited.contains(lane), s"missing reunited lane $lane")
      assert(!overlap.contains(lane), s"unexpected overlapping lane $lane")
    }
  }

  test("lane mask exposes each warp's active bits") {
    val lanes = VMLaneMask.all(96).toOption.get
    assertEquals(lanes.warpBits(0), 0xffffffffL)
    assertEquals(lanes.warpBits(32), 0xffffffffL)
    assertEquals(lanes.warpBits(64), 0xffffffffL)
    assertEquals(lanes.filterLanes(_ != 70).warpBits(64), 0xffffffffL & ~(1L << 6))
  }

}
