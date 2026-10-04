package net.kurobako.gemstone.benchmarks

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.ptx.v9_3.*
import net.kurobako.gemstone.isa.sass.*

class NVIDIAExecutionBenchmarkSuite extends munit.FunSuite {
  for (isa <- List("ptx", "sass"))
    test(s"$isa control flow verifies partial warps across concurrent groups") {
      val result = NVIDIAControlFlowBenchmark.run(Array(
        "--isa",
        isa,
        "--iterations",
        "2",
        "--lanes",
        "65",
        "--groups",
        "3",
        "--workers",
        "2",
        "--rounds",
        "3"
      ))
      assertEquals(result.operations.raw, 2L)
    }

  test("nested calls verify divergent lanes and shared exchange across partial warps") {
    val result = PTXCallBenchmark.run(Array(
      "--iterations",
      "2",
      "--lanes",
      "66",
      "--groups",
      "3",
      "--workers",
      "2",
      "--calls",
      "3"
    ))
    assertEquals(result.operations.raw, 2L)
  }
}
