package net.kurobako.gemstone.benchmarks

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

final class BenchmarkRunnerSuite extends munit.FunSuite {
  test("catalog names are stable and unique") {
    assert(PlatformBenchmarks.names.nonEmpty)
    assertEquals(PlatformBenchmarks.names.distinct, PlatformBenchmarks.names)
  }

  test("unknown cases fail before measurement") {
    intercept[IllegalArgumentException](BenchmarkRunner.main(Array("run", "missing")))
  }

  test("runner options are typed and leave case arguments intact") {
    val options = BenchmarkRunner.parse(
      BenchmarkName("ptx-execution"),
      Vector("--warmup", "2", "--samples", "3", "--", "--iterations", "17")
    )
    assertEquals(
      options,
      BenchmarkOptions(2, 3, BenchmarkInvocation(BenchmarkName("ptx-execution"), Vector("--iterations", "17")))
    )
    assertEquals(BenchmarkRunner.result(options, 1, 99L)("schema").str, "gemstone-benchmark-v1")
  }
}
