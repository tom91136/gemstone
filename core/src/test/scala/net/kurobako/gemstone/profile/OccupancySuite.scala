package net.kurobako.gemstone.profile

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.*

class OccupancySuite extends Suite {

  /** `cudaOccMaxActiveBlocksPerMultiprocessor` from `cuda_occupancy.h` of CUDA 13.3, for an RTX 4060 Ti (8.9) with
    * shared memory opted in up to the per-block maximum: workgroup lanes, registers per lane, dynamic shared bytes,
    * workgroups per multiprocessor, and the limiting factors, W for warps, R registers, S shared memory and B blocks.
    */
  private val calculator =
    """
      |32 0 0 24 B, 32 0 1000 24 B, 32 0 16384 5 S, 32 0 48000 2 S, 32 0 100352 1 S
      |32 32 0 24 B, 32 32 1000 24 B, 32 32 16384 5 S, 32 32 48000 2 S, 32 32 100352 1 S
      |32 40 0 24 B, 32 40 1000 24 B, 32 40 16384 5 S, 32 40 48000 2 S, 32 40 100352 1 S
      |32 64 0 24 B, 32 64 1000 24 B, 32 64 16384 5 S, 32 64 48000 2 S, 32 64 100352 1 S
      |32 72 0 24 B, 32 72 1000 24 B, 32 72 16384 5 S, 32 72 48000 2 S, 32 72 100352 1 S
      |32 128 0 16 R, 32 128 1000 16 R, 32 128 16384 5 S, 32 128 48000 2 S, 32 128 100352 1 S
      |32 255 0 8 R, 32 255 1000 8 R, 32 255 16384 5 S, 32 255 48000 2 S, 32 255 100352 1 S
      |96 0 0 16 W, 96 0 1000 16 W, 96 0 16384 5 S, 96 0 48000 2 S, 96 0 100352 1 S
      |96 32 0 16 W, 96 32 1000 16 W, 96 32 16384 5 S, 96 32 48000 2 S, 96 32 100352 1 S
      |96 40 0 16 WR, 96 40 1000 16 WR, 96 40 16384 5 S, 96 40 48000 2 S, 96 40 100352 1 S
      |96 64 0 10 R, 96 64 1000 10 R, 96 64 16384 5 S, 96 64 48000 2 S, 96 64 100352 1 S
      |96 72 0 9 R, 96 72 1000 9 R, 96 72 16384 5 S, 96 72 48000 2 S, 96 72 100352 1 S
      |96 128 0 5 R, 96 128 1000 5 R, 96 128 16384 5 RS, 96 128 48000 2 S, 96 128 100352 1 S
      |96 255 0 2 R, 96 255 1000 2 R, 96 255 16384 2 R, 96 255 48000 2 RS, 96 255 100352 1 S
      |128 0 0 12 W, 128 0 1000 12 W, 128 0 16384 5 S, 128 0 48000 2 S, 128 0 100352 1 S
      |128 32 0 12 W, 128 32 1000 12 W, 128 32 16384 5 S, 128 32 48000 2 S, 128 32 100352 1 S
      |128 40 0 12 WR, 128 40 1000 12 WR, 128 40 16384 5 S, 128 40 48000 2 S, 128 40 100352 1 S
      |128 64 0 8 R, 128 64 1000 8 R, 128 64 16384 5 S, 128 64 48000 2 S, 128 64 100352 1 S
      |128 72 0 7 R, 128 72 1000 7 R, 128 72 16384 5 S, 128 72 48000 2 S, 128 72 100352 1 S
      |128 128 0 4 R, 128 128 1000 4 R, 128 128 16384 4 R, 128 128 48000 2 S, 128 128 100352 1 S
      |128 255 0 2 R, 128 255 1000 2 R, 128 255 16384 2 R, 128 255 48000 2 RS, 128 255 100352 1 S
      |256 0 0 6 W, 256 0 1000 6 W, 256 0 16384 5 S, 256 0 48000 2 S, 256 0 100352 1 S
      |256 32 0 6 W, 256 32 1000 6 W, 256 32 16384 5 S, 256 32 48000 2 S, 256 32 100352 1 S
      |256 40 0 6 WR, 256 40 1000 6 WR, 256 40 16384 5 S, 256 40 48000 2 S, 256 40 100352 1 S
      |256 64 0 4 R, 256 64 1000 4 R, 256 64 16384 4 R, 256 64 48000 2 S, 256 64 100352 1 S
      |256 72 0 3 R, 256 72 1000 3 R, 256 72 16384 3 R, 256 72 48000 2 S, 256 72 100352 1 S
      |256 128 0 2 R, 256 128 1000 2 R, 256 128 16384 2 R, 256 128 48000 2 RS, 256 128 100352 1 S
      |256 255 0 1 R, 256 255 1000 1 R, 256 255 16384 1 R, 256 255 48000 1 R, 256 255 100352 1 RS
      |640 0 0 2 W, 640 0 1000 2 W, 640 0 16384 2 W, 640 0 48000 2 WS, 640 0 100352 1 S
      |640 32 0 2 W, 640 32 1000 2 W, 640 32 16384 2 W, 640 32 48000 2 WS, 640 32 100352 1 S
      |640 40 0 2 WR, 640 40 1000 2 WR, 640 40 16384 2 WR, 640 40 48000 2 WRS, 640 40 100352 1 S
      |640 64 0 1 R, 640 64 1000 1 R, 640 64 16384 1 R, 640 64 48000 1 R, 640 64 100352 1 RS
      |640 72 0 1 R, 640 72 1000 1 R, 640 72 16384 1 R, 640 72 48000 1 R, 640 72 100352 1 RS
      |640 128 0 0 -, 640 128 1000 0 -, 640 128 16384 0 -, 640 128 48000 0 -, 640 128 100352 0 -
      |640 255 0 0 -, 640 255 1000 0 -, 640 255 16384 0 -, 640 255 48000 0 -, 640 255 100352 0 -
      |1024 0 0 1 W, 1024 0 1000 1 W, 1024 0 16384 1 W, 1024 0 48000 1 W, 1024 0 100352 1 WS
      |1024 32 0 1 W, 1024 32 1000 1 W, 1024 32 16384 1 W, 1024 32 48000 1 W, 1024 32 100352 1 WS
      |1024 40 0 1 WR, 1024 40 1000 1 WR, 1024 40 16384 1 WR, 1024 40 48000 1 WR, 1024 40 100352 1 WRS
      |1024 64 0 1 WR, 1024 64 1000 1 WR, 1024 64 16384 1 WR, 1024 64 48000 1 WR, 1024 64 100352 1 WRS
      |1024 72 0 0 -, 1024 72 1000 0 -, 1024 72 16384 0 -, 1024 72 48000 0 -, 1024 72 100352 0 -
      |1024 128 0 0 -, 1024 128 1000 0 -, 1024 128 16384 0 -, 1024 128 48000 0 -, 1024 128 100352 0 -
      |1024 255 0 0 -, 1024 255 1000 0 -, 1024 255 16384 0 -, 1024 255 48000 0 -, 1024 255 100352 0 -
      |""".stripMargin

  private val letters = Map(
    'W' -> Occupancy.Limiter.Subgroups,
    'R' -> Occupancy.Limiter.Registers,
    'S' -> Occupancy.Limiter.Shared,
    'B' -> Occupancy.Limiter.Workgroups
  )

  test("sm_89 occupancy matches the CUDA occupancy calculator") {
    calculator.split("[,\n]").map(_.trim).filter(_.nonEmpty).foreach { row =>
      val Array(lanes, registers, shared, workgroups, limiters) = row.split(' ')
      val occupancy                                             = Occupancy.of(
        Architecture.sm89.limits,
        KernelResources(lanes.toInt.unsafe, 32, Some(registers.toInt.unsafe), shared.toLong.unsafe)
      )
      assertEquals(occupancy.workgroups, workgroups.toInt, row)
      if (limiters != "-") assertEquals(occupancy.limiters, limiters.map(letters).toSet, row)
    }
  }

  test("a kernel without stated registers is bounded by its other resources") {
    val occupancy = Occupancy.of(Architecture.sm89.limits, KernelResources(256, 32, None, 0L))
    assertEquals((occupancy.workgroups, occupancy.subgroups, occupancy.fraction), (6, 48, 1.0))
    assertEquals(occupancy.limiters, Set(Occupancy.Limiter.Subgroups))
  }

  test("an RDNA2 wave32 kernel is bounded by VGPRs in 16-register granules per SIMD") {
    val occupancy = Occupancy.of(Architecture.gfx1030.limits, KernelResources(256, 32, Some(100), 0L))
    assertEquals(occupancy.bounds(Occupancy.Limiter.Registers), 32768 / (112 * 32) * 4 / 8)
    assertEquals(occupancy.limiters, Set(Occupancy.Limiter.Registers))
  }

  /** RTX 4060 Ti (sm_89), driver 615.78.08, CUDA 13.3, ncu 2026.2.1, probe `occupancy.cu` in the Nextcloud probe kit.
    */
  private val hardwareTable =
    """
      |registers_light 32 14 16 0 0 1024 32768 128 32 48 24 50
      |registers_mid 32 48 48 0 0 1024 32768 40 32 48 24 50
      |registers_heavy 32 124 124 0 0 1024 16384 16 16 48 24 33.33
      |registers_capped 32 64 64 0 0 1024 32768 32 32 48 24 50
      |shared_static_8k 32 12 16 8192 0 1024 102400 128 11 48 24 22.92
      |shared_static_40k 32 12 16 40960 0 1024 102400 128 2 48 24 4.17
      |registers_light 128 14 16 0 0 1024 32768 32 32 12 24 100
      |registers_mid 128 48 48 0 0 1024 32768 10 32 12 24 83.33
      |registers_heavy 128 124 124 0 0 1024 16384 4 16 12 24 33.33
      |registers_capped 128 64 64 0 0 1024 32768 8 32 12 24 66.67
      |shared_static_8k 128 12 16 8192 0 1024 102400 32 11 12 24 91.67
      |shared_static_40k 128 12 16 40960 0 1024 102400 32 2 12 24 16.67
      |registers_light 256 14 16 0 0 1024 16384 16 16 6 24 100
      |registers_mid 256 48 48 0 0 1024 16384 5 16 6 24 83.33
      |registers_heavy 256 124 124 0 0 1024 8192 2 8 6 24 33.33
      |registers_capped 256 64 64 0 0 1024 16384 4 16 6 24 66.67
      |shared_static_8k 256 12 16 8192 0 1024 65536 16 7 6 24 100
      |shared_static_40k 256 12 16 40960 0 1024 102400 16 2 6 24 33.33
      |registers_light 512 14 16 0 0 1024 8192 8 8 3 24 100
      |registers_mid 512 48 48 0 0 1024 8192 2 8 3 24 66.67
      |registers_heavy 512 124 124 0 0 1024 8192 1 8 3 24 33.33
      |registers_capped 512 64 64 0 0 1024 8192 2 8 3 24 66.67
      |shared_static_8k 512 12 16 8192 0 1024 65536 8 7 3 24 100
      |shared_static_40k 512 12 16 40960 0 1024 102400 8 2 3 24 66.67
      |registers_light 1024 14 16 0 0 1024 8192 4 8 1 24 66.67
      |registers_mid 1024 48 48 0 0 1024 8192 1 8 1 24 66.67
      |registers_capped 1024 64 64 0 0 1024 8192 1 8 1 24 66.67
      |shared_static_8k 1024 12 16 8192 0 1024 16384 4 1 1 24 66.67
      |shared_static_40k 1024 12 16 40960 0 1024 65536 4 1 1 24 66.67
      |shared_dynamic 64 12 16 0 0 1024 32768 64 32 24 24 100
      |shared_dynamic 256 12 16 0 0 1024 16384 16 16 6 24 100
      |shared_dynamic 64 12 16 0 1024 1024 65536 64 32 24 24 100
      |shared_dynamic 256 12 16 0 1024 1024 32768 16 16 6 24 100
      |shared_dynamic 64 12 16 0 16384 1024 102400 64 5 24 24 20.83
      |shared_dynamic 256 12 16 0 16384 1024 102400 16 5 6 24 83.33
      |shared_dynamic 64 12 16 0 33792 1024 102400 64 2 24 24 8.33
      |shared_dynamic 256 12 16 0 33792 1024 102400 16 2 6 24 33.33
      |shared_dynamic 64 12 16 0 65536 1024 102400 64 1 24 24 4.17
      |shared_dynamic 256 12 16 0 65536 1024 102400 16 1 6 24 16.67
      |shared_dynamic 64 12 16 0 101376 1024 102400 64 1 24 24 4.17
      |shared_dynamic 256 12 16 0 101376 1024 102400 16 1 6 24 16.67
      |""".stripMargin

  private case class Measure(
      limReg: Option[Int],
      limShm: Option[Int],
      limWarp: Option[Int],
      limBlk: Option[Int],
      occPct: Double
  ) derives CanEqual

  hardwareTable.split('\n').map(_.trim).filter(_.nonEmpty).foreach { row =>
    val Array(kernel, block, cubin, _, static, dynamic, _, config, limReg, limShm, limWarp, limBlk, occPct) =
      row.split(' '): @unchecked
    test(s"hardware: $kernel $block $dynamic") {
      val occupancy = Occupancy.of(
        Architecture.sm89.limits,
        KernelResources(block.toInt.unsafe, 32, Some(cubin.toInt.unsafe), (static.toLong + dynamic.toLong).unsafe)
      )
      val expected = Measure(
        Some(limReg.toInt),
        Option.when(config == "102400")(limShm.toInt),
        Some(limWarp.toInt),
        Some(limBlk.toInt),
        occPct.toDouble
      )
      val actual = Measure(
        occupancy.bounds.get(Occupancy.Limiter.Registers),
        if (config == "102400") occupancy.bounds.get(Occupancy.Limiter.Shared) else None,
        occupancy.bounds.get(Occupancy.Limiter.Subgroups),
        occupancy.bounds.get(Occupancy.Limiter.Workgroups),
        math.round(occupancy.fraction * 10000) / 100.0
      )
      assertEquals(actual, expected)
    }
  }
}
