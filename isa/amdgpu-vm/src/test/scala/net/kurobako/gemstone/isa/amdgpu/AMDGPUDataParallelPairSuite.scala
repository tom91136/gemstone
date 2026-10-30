package net.kurobako.gemstone.isa.amdgpu

import laneRuns.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.testing.Suite
import net.kurobako.gemstone.testing.bits.*

/** DPP on register pairs, which CDNA2 onwards allows with row_newbcast: `__builtin_amdgcn_update_dpp` on a double with
  * control 0x151 emits `v_mov_b64_dpp v[6:7], v[0:1] row_newbcast:1` under `hipcc --offload-arch=gfx942` and two
  * `v_mov_b32_dpp ... row_newbcast:1` under gfx90a.
  */
class AMDGPUDataParallelPairSuite extends Suite {

  /** Lane `n` holds n + 0.5 in v[2:3], 2.0 in v[4:5] and 3.0 in v[6:7]; each lane's v4 and v5 afterwards. */
  private def run(target: Target, code: Vec[Int]): Vec[(Long, Long)] = {
    val inputs = Vec.tabulate(64) { lane =>
      val value = double(lane + 0.5)
      Vec(value & 0xffffffffL, value >>> 32, double(2.0) & 0xffffffffL, double(2.0) >>> 32, 0L, double(3.0) >>> 32)
    }
    val operations = program(target, octets(code*))
    laneResults(target, inputs, operations, 4).zip(laneResults(target, inputs, operations, 5))
  }

  private def pair(low: Long, high: Long): Double = IEEE754.binary64ToDouble(low | (high << 32))

  // /opt/rocm/llvm/bin/llvm-mc -show-encoding
  for (target <- Seq(Target("gfx942"), Target("gfx950")))
    test(s"$target v_mov_b64_dpp v[4:5], v[2:3] row_newbcast:1 broadcasts lane 1 of each row") {
      val result = run(target, Vec(0xfa, 0x70, 0x08, 0x7e, 0x02, 0x51, 0x01, 0xff))
      assertEquals(result.map((low, high) => pair(low, high)), Vec.tabulate(64)(lane => (lane & ~15) + 1.5))
    }

  for (target <- Seq(Target("gfx90a"), Target("gfx942")))
    test(s"$target v_ceil_f64_dpp v[4:5], v[2:3] row_newbcast:1 rounds the broadcast pair") {
      val result = run(target, Vec(0xfa, 0x30, 0x08, 0x7e, 0x02, 0x51, 0x01, 0xff))
      assertEquals(result.map((low, high) => pair(low, high)), Vec.tabulate(64)(lane => (lane & ~15) + 2.0))
    }

  test("gfx90a v_fmac_f64_dpp v[4:5], v[2:3], v[6:7] row_newbcast:2 accumulates the broadcast product") {
    val result = run(Target("gfx90a"), Vec(0xfa, 0x0c, 0x08, 0x08, 0x02, 0x52, 0x01, 0xff))
    assertEquals(result.map((low, high) => pair(low, high)), Vec.tabulate(64)(lane => ((lane & ~15) + 2.5) * 3.0 + 2.0))
  }

  test("gfx90a v_cvt_i32_f64_dpp v4, v[2:3] row_newbcast:3 narrows the broadcast pair") {
    val result = run(Target("gfx90a"), Vec(0xfa, 0x06, 0x08, 0x7e, 0x02, 0x53, 0x01, 0xff))
    assertEquals(result.map(_._1), Vec.tabulate(64)(lane => (lane & ~15) + 3L))
  }

  test("gfx90a v_mov_b32_dpp v4, v2 row_newbcast:1 broadcasts lane 1 of each row") {
    val result = run(Target("gfx90a"), Vec(0xfa, 0x02, 0x08, 0x7e, 0x02, 0x51, 0x01, 0xff))
    assertEquals(result.map(_._1), Vec.tabulate(64)(lane => double((lane & ~15) + 1.5) & 0xffffffffL))
  }
}
