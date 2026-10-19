package net.kurobako.gemstone.isa.amdgpu

import VMIR.*
import laneRuns.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.testing.bits.*

/** Packed binary16 and bfloat16 atomic adds, which hipcc emits for `unsafeAtomicAdd(__half2*, __half2)` and
  * `__builtin_amdgcn_global_atomic_fadd_v2bf16` and `__builtin_amdgcn_ds_atomic_fadd_v2f16`/`_v2bf16`, e.g.
  * `global_atomic_pk_add_f16 v1, v3, s[2:3]` and `ds_pk_add_f16 v1, v4` under `--offload-arch=gfx942`.
  */
class AMDGPUPackedAtomicSuite extends munit.FunSuite {
  private val halves     = half(1.0f) | (half(0.5f) << 16)
  private val sums       = half(5.0f) | (half(2.5f) << 16)
  private val bfloats    = 0x3f80L | (0x3f00L << 16)
  private val bfloatSums = 0x40a0L | (0x4020L << 16)

  import InstructionSource.*

  /** Four lanes add v2 at the lanes' memory base, which holds lane 0's v2 first; each lane then reads the sum. */
  private def global(target: Target, code: Vec[Int], data: Long): Vec[Long] =
    laneResults(
      target,
      Vec.fill(4)(Vec(data, 0L)),
      program(target, octets(code*)) :+
        Instruction.Load(4, MemoryAddress.Global(3, Some(6), 0), 4, signed = false, LoadPlacement.Full),
      4
    )

  /** Four lanes add v2 at LDS address 0, which starts at zero; each lane then reads the sum. */
  private def local(target: Target, code: Vec[Int], data: Long, lanes: Int = 4): Vec[Long] =
    laneResults(
      target,
      Vec.fill(lanes)(Vec(data, 0L, 0L)),
      Vec(Instruction.Store(3, MemoryAddress.Local(3, 0), 4, high = false)) ++ program(target, octets(code*)) :+
        Instruction.Load(5, MemoryAddress.Local(3, 0), 4, signed = false, LoadPlacement.Full),
      5,
      groupSegmentSize = 64
    )

  // /opt/rocm/llvm/bin/llvm-mc -show-encoding
  for (
    (target, f16, bf16) <- Seq(
      (Target("gfx90a"), Vec(0x00, 0x80, 0x38, 0xdd, 0x03, 0x02, 0x06, 0x00), Vec.empty[Int]),
      (
        Target("gfx942"),
        Vec(0x00, 0x80, 0x38, 0xdd, 0x03, 0x02, 0x06, 0x00),
        Vec(0x00, 0x80, 0x48, 0xdd, 0x03, 0x02, 0x06, 0x00)
      ),
      (
        Target("gfx950"),
        Vec(0x00, 0x80, 0x38, 0xdd, 0x03, 0x02, 0x06, 0x00),
        Vec(0x00, 0x80, 0x48, 0xdd, 0x03, 0x02, 0x06, 0x00)
      ),
      (
        Target("gfx1201"),
        Vec(0x06, 0x40, 0x16, 0xee, 0x00, 0x00, 0x00, 0x01, 0x03, 0x00, 0x00, 0x00),
        Vec(0x06, 0x80, 0x16, 0xee, 0x00, 0x00, 0x00, 0x01, 0x03, 0x00, 0x00, 0x00)
      )
    )
  ) {
    test(s"$target global_atomic_pk_add_f16 v3, v2, s[6:7] adds both halves") {
      assertEquals(global(target, f16, halves), Vec.fill(4)(sums))
    }
    if (bf16.nonEmpty) test(s"$target global_atomic_pk_add_bf16 v3, v2, s[6:7] adds both halves") {
      assertEquals(global(target, bf16, bfloats), Vec.fill(4)(bfloatSums))
    }
  }

  test("global_atomic_pk_add_f16 with glc, sc0 or TH_ATOMIC_RETURN returns the old value") {
    val inputs = Vec(halves, 0L)
    for (
      (target, code) <- Seq(
        Target("gfx90a")  -> Vec(0x00, 0x80, 0x39, 0xdd, 0x03, 0x02, 0x06, 0x04),
        Target("gfx942")  -> Vec(0x00, 0x80, 0x39, 0xdd, 0x03, 0x02, 0x06, 0x04),
        Target("gfx1201") -> Vec(0x06, 0x40, 0x16, 0xee, 0x04, 0x00, 0x10, 0x01, 0x03, 0x00, 0x00, 0x00)
      )
    ) assertEquals(laneResult(target, inputs, program(target, octets(code*)), 4), halves, target)
  }

  test("gfx942 flat_atomic_pk_add_f16 v4, v[6:7], v2 sc0 reaches global memory through a flat address") {
    val target = Target("gfx942")
    val code   = Vec(0x00, 0x00, 0x39, 0xdd, 0x06, 0x02, 0x00, 0x04)
    val result = laneResults(
      target,
      Vec.fill(2)(Vec(halves, 0L)),
      Vec(Instruction.VMove(6, Scalar(6)), Instruction.VMove(7, Scalar(7))) ++ program(target, octets(code*)) :+
        Instruction.Load(4, MemoryAddress.Global(3, Some(6), 0), 4, signed = false, LoadPlacement.Full),
      4
    )
    assertEquals(result, Vec.fill(2)(half(3.0f) | (half(1.5f) << 16)))
  }

  for (
    (target, f16, f16Return, bf16, bf16Return) <- Seq(
      (
        Target("gfx942"),
        Vec(0x00, 0x00, 0x2e, 0xd8, 0x03, 0x02, 0x00, 0x00),
        Vec(0x00, 0x00, 0x6e, 0xd9, 0x03, 0x02, 0x00, 0x04),
        Vec(0x00, 0x00, 0x30, 0xd8, 0x03, 0x02, 0x00, 0x00),
        Vec(0x00, 0x00, 0x70, 0xd9, 0x03, 0x02, 0x00, 0x04)
      ),
      (
        Target("gfx950"),
        Vec(0x00, 0x00, 0x2e, 0xd8, 0x03, 0x02, 0x00, 0x00),
        Vec(0x00, 0x00, 0x6e, 0xd9, 0x03, 0x02, 0x00, 0x04),
        Vec(0x00, 0x00, 0x30, 0xd8, 0x03, 0x02, 0x00, 0x00),
        Vec(0x00, 0x00, 0x70, 0xd9, 0x03, 0x02, 0x00, 0x04)
      ),
      (
        Target("gfx1201"),
        Vec(0x00, 0x00, 0x68, 0xda, 0x03, 0x02, 0x00, 0x00),
        Vec(0x00, 0x00, 0xa8, 0xda, 0x03, 0x02, 0x00, 0x04),
        Vec(0x00, 0x00, 0x6c, 0xda, 0x03, 0x02, 0x00, 0x00),
        Vec(0x00, 0x00, 0xac, 0xda, 0x03, 0x02, 0x00, 0x04)
      )
    )
  ) {
    test(s"$target ds_pk_add_f16 and ds_pk_add_bf16 accumulate in LDS") {
      assertEquals(local(target, f16, halves), Vec.fill(4)(half(4.0f) | (half(2.0f) << 16)))
      assertEquals(local(target, bf16, bfloats), Vec.fill(4)(0x4080L | (0x4000L << 16)))
    }
    test(s"$target ds_pk_add_rtn_f16 and ds_pk_add_rtn_bf16 return the old value") {
      // lane 0 adds to zero and lane 1 then finds the first sum
      val first = laneResults(
        target,
        Vec.fill(2)(Vec(halves, 0L, 0L)),
        program(target, octets(f16Return*)),
        4,
        groupSegmentSize = 64
      )
      val second = laneResults(
        target,
        Vec.fill(2)(Vec(bfloats, 0L, 0L)),
        program(target, octets(bf16Return*)),
        4,
        groupSegmentSize = 64
      )
      assertEquals(first, Vec(0L, halves))
      assertEquals(second, Vec(0L, bfloats))
    }
  }
}
