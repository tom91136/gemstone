package net.kurobako.gemstone.isa.amdgpu

import VMIR.*
import laneRuns.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

/** gfx950's lane swaps (CDNA4 ISA, V_PERMLANE16_SWAP_B32), which hipcc emits for `__builtin_amdgcn_permlane16_swap`,
  * GFX12's DS_BPERMUTE_FI_B32 and V_SWAP_B16, and the ADDTID accesses, whose address is the lane's index past a base
  * (CDNA4 ISA, DS_READ_ADDTID_B32). Bytes from LLVM 22 `llvm-mc -triple=amdgcn -mcpu=<target> -show-encoding`, with
  * `-mattr=+real-true16` for V_SWAP_B16.
  */
class AMDGPULaneExchangeSuite extends munit.FunSuite {
  private val gfx950  = Target("gfx950")
  private val gfx1201 = Target("gfx1201")

  private def swapped(code: Vec[Int], result: Int, exec: Long = -1L): Vec[Long] = laneResults(
    gfx950,
    Vec.tabulate(64)(lane => Vec(100L + lane, 0L, 0L, 200L + lane)),
    program(gfx950, octets(code*)),
    result.unsafe[VectorRegisterIndex],
    exec = exec,
    wave32 = false
  )

  test(
    "V_PERMLANE16_SWAP_B32 and V_PERMLANE32_SWAP_B32 trade the source below each boundary for the destination above"
  ) {
    val rows16 = Vec(0x02, 0xb3, 0x0a, 0x7e)
    assertEquals(swapped(rows16, 2), Vec.tabulate(64)(lane => if ((lane & 16) == 0) 216L + lane else 100L + lane))
    assertEquals(swapped(rows16, 5), Vec.tabulate(64)(lane => if ((lane & 16) == 0) 200L + lane else 84L + lane))
    val rows32 = Vec(0x02, 0xb5, 0x0a, 0x7e)
    assertEquals(swapped(rows32, 2), Vec.tabulate(64)(lane => if (lane < 32) 232L + lane else 100L + lane))
    assertEquals(swapped(rows32, 5), Vec.tabulate(64)(lane => if (lane < 32) 200L + lane else 68L + lane))
  }

  test("V_PERMLANE16_SWAP_B32 reads inactive lanes under FI") {
    val fetching = Vec(0x05, 0x18, 0x99, 0xd1, 0x02, 0x01, 0x00, 0x00)
    assertEquals(swapped(fetching, 2, exec = 0xffffL).take(16), Vec.tabulate(16)(216L + _))
  }

  test("V_SWAP_B16 trades the halves the true16 operands name, within one register too") {
    val across = Vec(0x82, 0xcd, 0x0a, 0x7e)
    val inputs = Vec(0x33334444L, 0L, 0L, 0x11112222L)
    assertEquals(laneResult(gfx1201, inputs, program(gfx1201, octets(across*)), 5), 0x11113333L)
    assertEquals(laneResult(gfx1201, inputs, program(gfx1201, octets(across*)), 2), 0x22224444L)
    val within = Vec(0x05, 0xcd, 0x0a, 0x7f)
    assertEquals(laneResult(gfx1201, inputs, program(gfx1201, octets(within*)), 5), 0x22221111L)
  }

  test("DS_BPERMUTE_FI_B32 reads inactive lanes") {
    val code   = Vec(0x00, 0x00, 0x34, 0xdb, 0x02, 0x03, 0x00, 0x05)
    val inputs = Vec.tabulate(32)(lane => Vec(((lane ^ 1) * 4).toLong, 1000L + lane))
    val result = laneResults(gfx1201, inputs, program(gfx1201, octets(code*)), 5, exec = 0x55555555L)
    assertEquals(
      result.zipWithIndex.collect { case (value, lane) if lane % 2 == 0 => value },
      Vec.tabulate(16)(lane => 1001L + 2 * lane)
    )
  }

  test("the ADDTID accesses address each lane's dword past M0 or the scalar base") {
    val store  = Vec(0x10, 0x00, 0x3a, 0xd8, 0x00, 0x02, 0x00, 0x00, 0x00, 0x00, 0x6c, 0xd8, 0x03, 0x00, 0x00, 0x05)
    val inputs = Vec.tabulate(64)(lane => Vec(500L + lane, 16L + 4 * lane))
    assertEquals(
      laneResults(gfx950, inputs, program(gfx950, octets(store*)), 5, wave32 = false, groupSegmentSize = 512),
      Vec.tabulate(64)(500L + _)
    )
    val load   = Vec(0x06, 0x00, 0x0a, 0xee, 0x05, 0x00, 0x00, 0x00, 0x00, 0x04, 0x00, 0x00)
    val memory = Vec(Vec.tabulate(15)(index => if (index == 0) 0L else 9L + index)) ++ Vec.fill(7)(Vec(0L))
    assertEquals(laneResults(gfx1201, memory, program(gfx1201, octets(load*)), 5), Vec.tabulate(8)(10L + _))
  }
}
