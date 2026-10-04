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
      BenchmarkRunner.result(
        options,
        1,
        BenchmarkSample(99L, 17L, Some(2048L), BenchmarkSample.AllocationScope.CallingThread)
      )("schema").str,
      "gemstone-benchmark-v2"
    )
  }
  test("result serializes measured work and explicit allocation scope without rescaling") {
    val options = BenchmarkOptions(2, 3, BenchmarkInvocation(BenchmarkName("ptx-execution"), Vector.empty))
    val sample  = BenchmarkSample(9007199254740993L, 37L, Some(12345L), BenchmarkSample.AllocationScope.Workers)
    val result  = BenchmarkRunner.result(options, 1, sample, 123L)
    assertEquals(result("elapsed_nanos").str, "9007199254740993")
    assertEquals(result("operation_count").str, "37")
    assertEquals(result("allocated_bytes").str, "12345")
    assertEquals(result("allocation_scope").str, "workers")
    assertEquals(result("started_epoch_millis").str, "123")
  }

  test("unsupported allocation metrics stay unavailable instead of appearing as zero") {
    val measured = Measurement((), 27L, None)
    val sample   = measured.sample(3L)
    assertEquals(sample, BenchmarkSample(27L, 3L, None, BenchmarkSample.AllocationScope.Unavailable))
    val options = BenchmarkOptions(0, 1, BenchmarkInvocation(BenchmarkName("ptx-execution"), Vector.empty))
    val result  = BenchmarkRunner.result(options, 0, sample)
    assertEquals(result("allocated_bytes"), ujson.Null)
    assertEquals(result("allocation_scope").str, "unavailable")
  }

  test("a measured case returns its actual operation count after verification") {
    val sample = PlatformBenchmarks.run(BenchmarkInvocation(
      BenchmarkName("amdgpu-dispatch-memory"),
      Vector("--iterations", "37")
    ))
    assertEquals(sample.operations.raw, 37L)
    assertEquals(
      sample.allocationScope,
      if (sample.allocatedBytes.isDefined) BenchmarkSample.AllocationScope.CallingThread
      else BenchmarkSample.AllocationScope.Unavailable
    )
  }

  Vector(
    ("ptx-execution", Vector("--mode", "tiny", "--measured", "2"), 2L),
    ("ptx-register-index", Vector("--kind", "numbered", "--names", "7", "--iterations", "19"), 19L),
    ("ptx-register-index", Vector("--kind", "named", "--names", "7", "--iterations", "19"), 19L),
    (
      "ptx-program-lifecycle",
      Vector("--operation", "decode", "--entries", "2", "--images", "2", "--iterations", "3"),
      3L
    ),
    ("ptx-program-lifecycle", Vector("--operation", "launch", "--entries", "2", "--iterations", "3"), 3L),
    ("amdgpu-execution", Vector("--measured", "2"), 2L),
    ("amdgpu-alu", Vector("--measured", "2", "--instructions", "4"), 2L),
    ("amdgpu-atomic", Vector("--iterations", "3"), 3L),
    ("amdgpu-vector-memory", Vector("--iterations", "2"), 2L),
    ("ptx-cache", Vector("--threads", "2", "--images", "1", "--entries", "2", "--iterations", "2"), 4L)
  ).foreach { case (name, arguments, operations) =>
    test(s"$name ${arguments.mkString(" ")} verifies work and reports its operation count") {
      val sample = PlatformBenchmarks.run(BenchmarkInvocation(name.unsafe[BenchmarkName], arguments))
      assertEquals(sample.operations.raw, operations)
      assertEquals(
        sample.allocationScope,
        if (sample.allocatedBytes.isEmpty) BenchmarkSample.AllocationScope.Unavailable
        else if (name == "ptx-cache") BenchmarkSample.AllocationScope.Workers
        else BenchmarkSample.AllocationScope.CallingThread
      )
    }
  }

}
