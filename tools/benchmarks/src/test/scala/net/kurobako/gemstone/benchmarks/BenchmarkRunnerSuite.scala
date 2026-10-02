package net.kurobako.gemstone.benchmarks

import net.kurobako.gemstone.constraint.*

class BenchmarkRunnerSuite extends munit.FunSuite {
  test("catalog names are nonempty and unique") {
    assert(PlatformBenchmarks.names.toVector.nonEmpty)
    assertEquals(PlatformBenchmarks.names.toVector.distinct, PlatformBenchmarks.names.toVector)
  }

  test("every listed benchmark dispatches to its own argument parser") {
    PlatformBenchmarks.names.foreach { name =>
      val help = intercept[IllegalArgumentException] {
        PlatformBenchmarks.run(BenchmarkInvocation(name, Vector("--help")))
      }
      assert(help.getMessage.contains(s"Usage: $name"), help.getMessage)
    }
  }

  test("unknown cases fail before measurement") {
    val error = intercept[IllegalArgumentException] {
      BenchmarkRunner.parse(BenchmarkName("missing"), Vector.empty)
    }
    assert(error.getMessage.contains("unknown benchmark: missing"))
  }

  test("runner rejects negative warmups and nonpositive sample counts") {
    for (arguments <- Vector(Vector("--warmup", "-1"), Vector("--samples", "0"), Vector("--samples", "-1")))
      intercept[IllegalArgumentException] {
        BenchmarkRunner.parse(BenchmarkName("ptx-execution"), arguments)
      }
  }

  test("runner reports all invalid options together before measurement") {
    val error = intercept[IllegalArgumentException] {
      BenchmarkRunner.parse(BenchmarkName("ptx-execution"), Vector("--warmup", "-1", "--samples", "0"))
    }
    assert(error.getMessage.contains("warmups must be non-negative"))
    assert(error.getMessage.contains("samples must be positive"))
  }

  test("runner options are typed and leave case arguments intact") {
    val options = BenchmarkRunner.parse(
      BenchmarkName("ptx-execution"),
      Vector("--warmup", "2", "--samples", "3", "--", "--iterations", "17")
    )
    assertEquals(
      options,
      BenchmarkOptions(
        2,
        3,
        BenchmarkInvocation(BenchmarkName("ptx-execution"), Vector("--iterations", "17"))
      )
    )
    assertEquals(
      BenchmarkRunner.result(options, 1, 99L)("schema").str,
      "gemstone-benchmark-v1"
    )
  }
}
