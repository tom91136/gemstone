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

  test("probed latencies exist for sm_89 only") {
    assertEquals(NvidiaLatency.measured(90, Lds32), None)
    assertEquals(NvidiaLatency.measured(89, Fadd32), None)
  }

  test("an instruction text names its producer by mnemonic, ignoring predicate and modifiers") {
    assertEquals(NvidiaLatency.producer("@!P0 FFMA R9, R9, R0, c[0][0x180];"), Some(Ffma32))
    assertEquals(NvidiaLatency.producer("IADD3.X R7, RZ, c[0][0x174], RZ, P0, !PT;"), Some(Iadd32))
    assertEquals(NvidiaLatency.producer("MUFU.RCP R8, R8;"), Some(Rcp32))
    assertEquals(NvidiaLatency.producer("MUFU.RSQ R8, R8;"), None)
    assertEquals(NvidiaLatency.producer("ISETP.GE.AND P0, PT, R4, c[0][0x178], PT;"), None)
  }

  test("the multiprocessor version comes from an sm_ name only") {
    assertEquals(NvidiaLatency.sm(Architecture.sm89), Some(89))
    assertEquals(NvidiaLatency.sm(Architecture.gfx1036), None)
  }
}
