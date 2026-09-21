package net.kurobako.gemstone.isa.ptx.v9_3

import VMIR.*

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import net.kurobako.gemstone.Vec

final class MachineSuite extends munit.FunSuite {
  test("an executable program is immutable and bounds checked") {
    val program = ExecutableProgram(Vec("first", "second"))
    assertEquals(program.at(Pc.Entry), Some("first"))
    assertEquals(program.at(Pc(2)), None)
  }

  test("lane masks support CUDA-sized blocks and set operations") {
    val mask = VMLaneMask.all(256).toOption.get
    assertEquals(mask.size, 256)
    val selected = mask.filterLanes(_ % 32 == 0)
    assertEquals(selected.size, 8)
    assertEquals(selected.union(mask.subtract(selected)).size, 256)
    assertEquals(selected.intersect(mask.subtract(selected)).size, 0)
  }

  test("lane mask exposes each warp's active bits") {
    val lanes = VMLaneMask.all(96).toOption.get
    assertEquals(lanes.warpBits(0), 0xffffffffL)
    assertEquals(lanes.warpBits(32), 0xffffffffL)
    assertEquals(lanes.warpBits(64), 0xffffffffL)
    assertEquals(lanes.filterLanes(_ != 70).warpBits(64), 0xffffffffL & ~(1L << 6))
  }

}
