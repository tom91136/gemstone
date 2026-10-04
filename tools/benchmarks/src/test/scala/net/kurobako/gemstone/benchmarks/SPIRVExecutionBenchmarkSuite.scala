package net.kurobako.gemstone.benchmarks

import net.kurobako.gemstone.constraint.*

class SPIRVExecutionBenchmarkSuite extends munit.FunSuite {
  for {
    mode                                            <- List("arithmetic", "calls")
    (iterations, groups, instructions, parallelism) <- List((1, 1, 1, 1), (3, 7, 5, 2))
  }
    test(
      s"verify every SPIR-V $mode invocation: dispatches=$iterations groups=$groups additions=$instructions workers=$parallelism"
    ) {
      val sample = SPIRVExecutionBenchmark.run(Array(
        "--mode",
        mode,
        "--iterations",
        iterations.toString,
        "--groups",
        groups.toString,
        "--instructions",
        instructions.toString,
        "--parallelism",
        parallelism.toString
      ))
      assertEquals(sample.operations.raw, iterations.toLong)
      assert(sample.allocationScope != BenchmarkSample.AllocationScope.Workers)
    }
}
