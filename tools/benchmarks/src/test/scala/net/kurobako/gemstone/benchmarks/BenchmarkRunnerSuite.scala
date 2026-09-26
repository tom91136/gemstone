package net.kurobako.gemstone.benchmarks

import net.kurobako.gemstone.{NonNegativeInt, NonNegativeLong, PositiveInt}

final class BenchmarkRunnerSuite extends munit.FunSuite {
  test("catalog names are stable and unique") {
    assert(PlatformBenchmarks.names.toVector.nonEmpty)
    assertEquals(PlatformBenchmarks.names.toVector.distinct, PlatformBenchmarks.names.toVector)
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
      BenchmarkOptions(
        NonNegativeInt(2),
        PositiveInt(3),
        BenchmarkInvocation(BenchmarkName("ptx-execution"), Vector("--iterations", "17"))
      )
    )
    assertEquals(
      BenchmarkRunner.result(options, NonNegativeInt(1), NonNegativeLong(99L))("schema").str,
      "gemstone-benchmark-v1"
    )
  }
}
