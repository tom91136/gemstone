package net.kurobako.gemstone.profile

import net.kurobako.gemstone.testing.*

class NvidiaLatencySuite extends Suite {
  import NvidiaLatency.Producer.*

  private val ptxas129 = "cuda_12.9.r12.9/compiler.36037853_0"
  private val ptxas133 = "cuda_13.3.r13.3/compiler.38244171_0"

  test("a fixed latency is the stall of its adjacent consumer, sourced from the ptxas build") {
    assertEquals(
      NvidiaLatency.fixed(90, Fma64),
      Some(Architecture.Sourced(8, Architecture.Source.Compiled("ptxas", ptxas133, "sm_90")))
    )
    assertEquals(
      NvidiaLatency.fixed(50, Fadd32),
      Some(Architecture.Sourced(6, Architecture.Source.Compiled("ptxas", ptxas129, "sm_50")))
    )
  }

  test("producers that the stall counts do not determine have no value") {
    assertEquals(NvidiaLatency.fixed(90, Fadd32), None)
    assertEquals(NvidiaLatency.fixed(120, Ffma32), None)
    assertEquals(NvidiaLatency.fixed(50, Imul32), None)
    assertEquals(NvidiaLatency.fixed(70, I2f32), None)
    assertEquals(NvidiaLatency.fixed(90, Ldg32), None)
    assertEquals(NvidiaLatency.fixed(35, Fadd32), None)
    assertEquals(NvidiaLatency.fixed(37, Fadd32), None)
  }

  test("sm_101 is harvested under the CUDA 12.9 ptxas") {
    val expected = Architecture.Source.Compiled("ptxas", ptxas129, "sm_101")
    assertEquals(NvidiaLatency.fixed(101, Shl32).map(_.source), Some(expected))
  }

  private val probed = Architecture.Source.Probed(
    "clock64 dependent chain, median over 101 launches, one thread",
    "RTX 4060 Ti, sm_89, 34 SMs",
    "NVIDIA 620.70.06",
    "CUDA 13.3"
  )

  test("sm_89 probed latencies carry the card, driver and toolkit") {
    assertEquals(NvidiaLatency.measured(89, Lds32), Some(Architecture.Sourced(24.0, probed)))
    assertEquals(NvidiaLatency.measured(89, Rcp32), Some(Architecture.Sourced(28.0, probed)))
  }

  private val probedS2r = Architecture.Source.Probed(
    "hand-assembled cubin: CS2R.32 SR_CLOCKLO bracket of an S2R or S2UR and its first consumer, bracket subtracted " +
      "with a control run, median over 201 launches in each of 3 runs, 1 to 34 blocks of 1 to 128 threads",
    "RTX 4060 Ti, sm_89, 34 SMs",
    "NVIDIA 620.70.06",
    "CUDA 11.6 cuda.h, cuModuleLoadData"
  )

  test("sm_89 special register reads are probed per register class") {
    assertEquals(NvidiaLatency.measured(89, S2rThread), Some(Architecture.Sourced(23.0, probedS2r)))
    assertEquals(NvidiaLatency.measured(89, S2rBlock), Some(Architecture.Sourced(26.0, probedS2r)))
    assertEquals(NvidiaLatency.measured(89, S2ur), Some(Architecture.Sourced(36.0, probedS2r)))
    assertEquals(NvidiaLatency.measured(90, S2rThread), None)
  }

  private val probedLdgConstant = Architecture.Source.Probed(
    "hand-assembled cubin: CS2R.32 SR_CLOCKLO bracket of a repeated LDG.E.CONSTANT of one address (L1 hit) and its " +
      "first consumer, control run subtraction, median over 201 launches in each of 3 runs, a warp per multiprocessor",
    "RTX 4060 Ti, sm_89, 34 SMs",
    "NVIDIA 620.70.06",
    "CUDA 11.6 cuda.h, cuModuleLoadData"
  )

  private val probedShflDown = Architecture.Source.Probed(
    "hand-assembled cubin: CS2R.32 SR_CLOCKLO bracket of a SHFL.DOWN of a full warp and its first consumer, control " +
      "run subtraction, median over 201 launches in each of 3 runs, a warp per multiprocessor",
    "RTX 4060 Ti, sm_89, 34 SMs",
    "NVIDIA 620.70.06",
    "CUDA 11.6 cuda.h, cuModuleLoadData"
  )

  private val probedDmulDadd = Architecture.Source.Probed(
    "hand-assembled cubin: CS2R.32 SR_CLOCKLO bracket of a DMUL or DADD and its first consumer, control run " +
      "subtraction, median over 201 launches in each of 3 runs, a warp per multiprocessor (4 warps give 48 to 104)",
    "RTX 4060 Ti, sm_89, 34 SMs",
    "NVIDIA 620.70.06",
    "CUDA 11.6 cuda.h, cuModuleLoadData"
  )

  private val probedRcp64H = Architecture.Source.Probed(
    "hand-assembled cubin: CS2R.32 SR_CLOCKLO bracket of a MUFU.RCP64H and its first consumer, control run " +
      "subtraction, median over 201 launches in each of 3 runs, a warp per multiprocessor",
    "RTX 4060 Ti, sm_89, 34 SMs",
    "NVIDIA 620.70.06",
    "CUDA 11.6 cuda.h, cuModuleLoadData"
  )

  test("sm_89 constant loads, shuffles and 64-bit arithmetic carry probed latencies") {
    assertEquals(NvidiaLatency.measured(89, LdgConstant), Some(Architecture.Sourced(32.0, probedLdgConstant)))
    assertEquals(NvidiaLatency.measured(89, ShflDown), Some(Architecture.Sourced(25.0, probedShflDown)))
    assertEquals(NvidiaLatency.measured(89, Dmul64), Some(Architecture.Sourced(48.0, probedDmulDadd)))
    assertEquals(NvidiaLatency.measured(89, Dadd64), Some(Architecture.Sourced(48.0, probedDmulDadd)))
    assertEquals(NvidiaLatency.measured(89, Rcp64H), Some(Architecture.Sourced(17.0, probedRcp64H)))
    assertEquals(NvidiaLatency.measured(90, LdgConstant), None)
  }

  private val probedF2i = Architecture.Source.Probed(
    "hand-assembled cubin: CS2R.32 SR_CLOCKLO bracket of the F2I of bitPackKernel at 0x2e0 and its first consumer, " +
      "control run subtraction, median over 201 launches in each of 3 runs, a warp per multiprocessor",
    "RTX 4060 Ti, sm_89, 34 SMs",
    "NVIDIA 620.70.06",
    "CUDA 11.6 cuda.h, cuModuleLoadData"
  )

  test("sm_89 float-to-integer conversion carries a probed latency") {
    assertEquals(NvidiaLatency.measured(89, F2i32), Some(Architecture.Sourced(17.0, probedF2i)))
    assertEquals(NvidiaLatency.measured(90, F2i32), None)
  }

  private val probedSettle = Architecture.Source.Probed(
    "hand-assembled cubin: CS2R.32 SR_CLOCKLO bracket of a BREV, FLO, FRND or POPC and its first consumer, control " +
      "run subtraction, median over 201 launches in each of 3 runs, a warp per multiprocessor",
    "RTX 4060 Ti, sm_89, 34 SMs",
    "NVIDIA 620.70.06",
    "CUDA 11.6 cuda.h, cuModuleLoadData"
  )

  private val probedFloSh = Architecture.Source.Probed(
    "hand-assembled cubin: CS2R.32 SR_CLOCKLO bracket of a FLO.U32.SH and its first consumer, control run " +
      "subtraction, median over 201 launches in each of 3 runs, a warp per multiprocessor",
    "RTX 4060 Ti, sm_89, 34 SMs",
    "NVIDIA 620.70.06",
    "CUDA 11.6 cuda.h, cuModuleLoadData"
  )

  test("sm_89 bit and float rounding conversions carry probed latencies") {
    assertEquals(NvidiaLatency.measured(89, Brev), Some(Architecture.Sourced(16.0, probedSettle)))
    assertEquals(NvidiaLatency.measured(89, Flo), Some(Architecture.Sourced(16.0, probedSettle)))
    assertEquals(NvidiaLatency.measured(89, Frnd), Some(Architecture.Sourced(16.0, probedSettle)))
    assertEquals(NvidiaLatency.measured(89, Popc), Some(Architecture.Sourced(16.0, probedSettle)))
    assertEquals(NvidiaLatency.measured(89, FloSh), Some(Architecture.Sourced(17.0, probedFloSh)))
    assertEquals(NvidiaLatency.measured(90, Brev), None)
  }

  test("a producer names only the measured bit and float rounding forms") {
    assertEquals(NvidiaLatency.producer("BREV R1, c[0][0x28];"), Some(Brev))
    assertEquals(NvidiaLatency.producer("FLO R1, c[0][0x28];"), Some(Flo))
    assertEquals(NvidiaLatency.producer("FRND R1, c[0][0x28];"), Some(Frnd))
    assertEquals(NvidiaLatency.producer("POPC R0, 0x8;"), Some(Popc))
    assertEquals(NvidiaLatency.producer("FLO.U32.SH R7, R12;"), Some(FloSh))
    assertEquals(NvidiaLatency.producer("FRND.F64 R4, R2;"), None)
    assertEquals(NvidiaLatency.producer("FRND.F16 R4, R2;"), None)
    assertEquals(NvidiaLatency.producer("UBREV UR4, UR5;"), None)
  }

  test("a producer names the instruction form it measures, not the whole mnemonic family") {
    assertEquals(NvidiaLatency.producer("LDG.E.CONSTANT R4, [R4.64];"), Some(LdgConstant))
    assertEquals(NvidiaLatency.producer("@!P1 LDG.E.CONSTANT R6, [R6.64];"), Some(LdgConstant))
    assertEquals(NvidiaLatency.producer("LDG.E.64.CONSTANT R2, [R2.64];"), Some(Ldg32))
    assertEquals(NvidiaLatency.producer("LDG.E R4, [R4.64];"), Some(Ldg32))
    assertEquals(NvidiaLatency.producer("SHFL.DOWN PT, R4, R7, 0x8, 0x1f;"), Some(ShflDown))
    assertEquals(NvidiaLatency.producer("SHFL.IDX PT, R3, R0, R2, 0x1f;"), None)
    assertEquals(NvidiaLatency.producer("DMUL R4, R4, R22;"), Some(Dmul64))
    assertEquals(NvidiaLatency.producer("DADD R4, R8, -c[0][0x180];"), Some(Dadd64))
    assertEquals(NvidiaLatency.producer("MUFU.RCP64H R17, R5;"), Some(Rcp64H))
    assertEquals(NvidiaLatency.producer("MUFU.RCP R8, R8;"), Some(Rcp32))
    assertEquals(NvidiaLatency.producer("F2I.FTZ.U32.TRUNC.NTZ R13, R12;"), Some(F2i32))
    assertEquals(NvidiaLatency.producer("F2I.TRUNC.NTZ R0, R2;"), Some(F2i32))
    assertEquals(NvidiaLatency.producer("F2I.F64.TRUNC R4, R2;"), None)
    assertEquals(NvidiaLatency.producer("F2I.U64.TRUNC R4, R2;"), None)
    assertEquals(NvidiaLatency.producer("F2I.S64.F64.TRUNC R4, R2;"), None)
  }

  test("a special register read names its producer by the register it reads") {
    assertEquals(NvidiaLatency.producer("S2R R0, SR_TID.X;"), Some(S2rThread))
    assertEquals(NvidiaLatency.producer("S2R R2, SR_TID.Y;"), Some(S2rThread))
    assertEquals(NvidiaLatency.producer("S2R R4, SR_TID.Z;"), Some(S2rThread))
    assertEquals(NvidiaLatency.producer("S2R R4, SR_LANEID;"), Some(S2rThread))
    assertEquals(NvidiaLatency.producer("S2R R3, SR_CTAID.X;"), Some(S2rBlock))
    assertEquals(NvidiaLatency.producer("S2R R15, SR_CTAID.Y;"), Some(S2rBlock))
    assertEquals(NvidiaLatency.producer("S2UR UR6, SR_CTAID.X;"), Some(S2ur))
    assertEquals(NvidiaLatency.producer("S2UR UR5, SR_CTAID.Y;"), Some(S2ur))
    assertEquals(NvidiaLatency.producer("@P0 S2R R3, SR_CTAID.X;"), Some(S2rBlock))
  }

  test("a special register that was not probed has no producer") {
    assertEquals(NvidiaLatency.producer("S2R R0, SR_CLOCKLO;"), None)
    assertEquals(NvidiaLatency.producer("S2R R0, SR_NTID.X;"), None)
    assertEquals(NvidiaLatency.producer("S2UR UR4, SR_CLOCKLO;"), None)
    assertEquals(NvidiaLatency.producer("S2R R0;"), None)
  }

  test("probed latencies exist for sm_89 only") {
    assertEquals(NvidiaLatency.measured(90, Lds32), None)
    assertEquals(NvidiaLatency.measured(89, Fadd32), None)
  }

  test("an instruction text names its producer by mnemonic, ignoring predicate and modifiers") {
    assertEquals(NvidiaLatency.producer("@!P0 FFMA R9, R9, R0, c[0][0x180];"), Some(Ffma32))
    assertEquals(NvidiaLatency.producer("IADD3.X R7, RZ, c[0][0x174], RZ, P0, !PT;"), Some(Iadd32))
    assertEquals(NvidiaLatency.producer("MUFU.RCP R8, R8;"), Some(Rcp32))
    assertEquals(NvidiaLatency.producer("MUFU.RSQ R8, R8;"), None)
    assertEquals(NvidiaLatency.producer("ISETP.GE.AND P0, PT, R4, c[0][0x178], PT;"), Some(Isetp))
  }

  test("a constant-bank DFMA operand selects the probed constant-form rate") {
    val constant = NvidiaLatency.operandRate(Architecture.sm89, "DFMA R6, R6, R4, c[0x0][0x178];")
    assertEquals(constant.map(rate => 32.0 / rate.value), Some(19.0))
    assertEquals(constant.map(_.source.kind), Some("probed"))
    val shorthand = NvidiaLatency.operandRate(Architecture.sm89, "DFMA R9, R9, R0, c[0][0x180];")
    assertEquals(shorthand.map(rate => 32.0 / rate.value), Some(19.0))
    assertEquals(NvidiaLatency.operandRate(Architecture.sm89, "DFMA R20, R18, R20, R10"), None)
    assertEquals(NvidiaLatency.operandRate(Architecture.gfx1036, "v_fma_f64 v[1:2], v[1:2], v[3:4], v[5:6]"), None)
  }

  test("the multiprocessor version comes from an sm_ name only") {
    assertEquals(NvidiaLatency.sm(Architecture.sm89), Some(89))
    assertEquals(NvidiaLatency.sm(Architecture.gfx1036), None)
  }
}
