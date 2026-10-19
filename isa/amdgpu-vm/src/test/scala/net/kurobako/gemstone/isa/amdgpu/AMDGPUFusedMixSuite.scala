package net.kurobako.gemstone.isa.amdgpu

import laneRuns.*
import net.kurobako.gemstone.collection.*

/** CDNA's VOP3P opcode 32, which AMD's documents name V_MAD_MIX_F32, fuses: LLVM spells it `v_fma_mix_f32` from gfx908
  * on and hipcc emits it for `(float)h * (float)h + f` on gfx942 (`v_fma_mix_f32 v1, v2, v3, v4 op_sel_hi:[1,1,0]`).
  * Bytes from ROCm 7.1.1 `llvm-mc -triple=amdgcn -mcpu=<target> -show-encoding` for `v_fma_mix_f32 v5, v2, v3, v4`. The
  * square of 1 + 2^-12 less one is 2^-11 + 2^-24 fused and 2^-11 with the product rounded first.
  */
class AMDGPUFusedMixSuite extends munit.FunSuite {
  for (target <- Seq(Target("gfx908"), Target("gfx90a"), Target("gfx942"), Target("gfx950")))
    test(s"$target v_fma_mix_f32 rounds once") {
      val result = laneResults(
        target,
        Vec(Vec(0x3f800800L, 0x3f800800L, 0xbf800000L)),
        program(target, octets(0x05, 0x00, 0xa0, 0xd3, 0x02, 0x07, 0x12, 0x04)),
        5,
        wave32 = false
      ).head
      assertEquals(result, 0x3a000400L)
    }
}
