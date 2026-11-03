package net.kurobako.gemstone.benchmarks

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.Suite

class BenchmarkRunnerSuite extends Suite {
  private def parse(arguments: String*): BenchmarkRunner.Action =
    BenchmarkRunner.command.parse(arguments).fold(help => fail(help.toString), identity)

  test("case names are unique") {
    assertEquals(PlatformBenchmarks.cases.map(_.name).distinct, PlatformBenchmarks.cases.map(_.name))
  }

  test("runner options are typed and leave case arguments to the case") {
    parse("run", "ptx-vector-add", "--warmup", "2", "--samples", "3", "--no-jfr", "--", "--iterations", "17") match {
      case BenchmarkRunner.Action.Run(options, _) =>
        assertEquals(
          options,
          BenchmarkRunner.Options(2, 3, "ptx-vector-add", Vector("--iterations", "17"), false, false)
        )
      case action => fail(s"unexpected action $action")
    }
    assert(BenchmarkRunner.command.parse(Vector("run", "missing")).isLeft)
    assert(BenchmarkRunner.command.parse(Vector("run", "ptx-vector-add", "--", "--iterations", "0")).isLeft)
  }

  test("observe attaches a profile recorder to each launch") {
    parse("run", "ptx-vector-add", "--observe", "--", "--iterations", "2") match {
      case BenchmarkRunner.Action.Run(options, _) => assert(options.observe)
      case action                                 => fail(s"unexpected action $action")
    }
    assert(BenchmarkRunner.launch(true)().observer.nonEmpty)
    assert(BenchmarkRunner.launch(false)().observer.isEmpty)
  }

  test("profile defaults the architecture from the platform") {
    parse("profile", "amdgpu-atomic", "--", "--iterations", "1") match {
      case BenchmarkRunner.Action.Profile(options, _) => assertEquals(options.architecture.name, "gfx1036")
      case action                                     => fail(s"unexpected action $action")
    }
    parse("profile", "ptx-vector-add", "--", "--iterations", "1") match {
      case BenchmarkRunner.Action.Profile(options, _) => assertEquals(options.architecture.name, "sm_89")
      case action                                     => fail(s"unexpected action $action")
    }
    parse("profile", "spirv-execution", "--architecture", "gfx1030", "--", "--iterations", "1") match {
      case BenchmarkRunner.Action.Profile(options, _) => assertEquals(options.architecture.name, "gfx1030")
      case action                                     => fail(s"unexpected action $action")
    }
  }

  test("profile rejects missing or unknown architectures, cases without a VM and conflicting streams") {
    assert(BenchmarkRunner.command.parse(Vector("profile", "spirv-execution", "--iterations", "1")).isLeft)
    assert(BenchmarkRunner.command.parse(Vector("profile", "opencl-cts-async-copy")).isLeft)
    assert(BenchmarkRunner.command.parse(Vector("profile", "journal-inspection")).isLeft)
    assert(BenchmarkRunner.command.parse(Vector("profile", "spirv-execution", "--architecture", "sm_99")).isLeft)
    assert(
      BenchmarkRunner.command
        .parse(Vector("profile", "ptx-vector-add", "--stream", "a.bin", "--replay", "b.bin"))
        .isLeft
    )
  }

  test("results serialise measured work and keep unavailable allocation counts null") {
    val options = BenchmarkRunner.Options(2, 3, "ptx-vector-add", Vector.empty, true, false)
    val sample  = BenchmarkSample(9007199254740993L, 37L, Some(12345L), BenchmarkSample.AllocationScope.Workers)
    val result  = BenchmarkRunner.result(options, 1, sample, 123L)
    assertEquals(result("schema").str, "gemstone-benchmark-v2")
    assertEquals(result("elapsed_nanos").str, "9007199254740993")
    assertEquals(result("operation_count").str, "37")
    assertEquals(result("allocated_bytes").str, "12345")
    assertEquals(result("allocation_scope").str, "workers")
    assertEquals(result("started_epoch_millis").str, "123")
    val unavailable = BenchmarkRunner.result(options, 0, Measurement((), 27L, None).sample(3L))
    assertEquals(unavailable("allocated_bytes"), ujson.Null)
    assertEquals(unavailable("allocation_scope").str, "unavailable")
  }

  Vector(
    ("ptx-vector-add", Vector("--iterations", "2"), 2L, false),
    (
      "ptx-calls",
      Vector("--iterations", "2", "--lanes", "66", "--groups", "3", "--workers", "2", "--calls", "3"),
      2L,
      false
    ),
    (
      "nvidia-control-flow",
      Vector("--isa", "ptx", "--iterations", "2", "--lanes", "65", "--groups", "3", "--workers", "2", "--rounds", "3"),
      2L,
      false
    ),
    (
      "nvidia-control-flow",
      Vector("--isa", "sass", "--iterations", "2", "--lanes", "65", "--groups", "3", "--workers", "2", "--rounds", "3"),
      2L,
      false
    ),
    ("ptx-program-lifecycle", Vector("--entries", "2", "--images", "2", "--iterations", "3"), 3L, false),
    ("ptx-program-lifecycle", Vector("--operation", "launch", "--entries", "2", "--iterations", "3"), 3L, false),
    ("ptx-cache", Vector("--workers", "2", "--images", "1", "--entries", "2", "--iterations", "2"), 4L, true),
    (
      "spirv-execution",
      Vector("--iterations", "3", "--groups", "7", "--instructions", "5", "--workers", "2"),
      3L,
      false
    ),
    (
      "spirv-execution",
      Vector("--mode", "calls", "--iterations", "3", "--groups", "7", "--instructions", "5", "--workers", "2"),
      3L,
      false
    ),
    ("amdgpu-vector-copy", Vector("--iterations", "2", "--groups", "2"), 2L, false),
    ("amdgpu-dwordx4-copy", Vector("--iterations", "2"), 2L, false),
    ("amdgpu-alu", Vector("--iterations", "2", "--instructions", "4", "--mode", "binary"), 2L, false),
    ("amdgpu-atomic", Vector("--iterations", "3"), 3L, false),
    ("amdgpu-dispatch-memory", Vector("--iterations", "37"), 37L, false),
    ("session-memory", Vector("--iterations", "17", "--allocations", "3", "--workers", "2"), 34L, true),
    ("session-memory", Vector("--iterations", "17", "--allocations", "3", "--workers", "2", "--shared"), 34L, true),
    ("rpc-roundtrip", Vector("--transport", "tcp", "--iterations", "8"), 8L, false),
    ("rpc-roundtrip", Vector("--transport", "uds", "--iterations", "8"), 8L, false)
  ).foreach { (name, arguments, operations, workers) =>
    test(s"$name ${arguments.mkString(" ")} verifies its work") {
      parse(Vector("run", name, "--") ++ arguments*) match {
        case BenchmarkRunner.Action.Run(_, benchmark) =>
          val sample = benchmark(BenchmarkRunner.launch(false))
          assertEquals(sample.operations.raw, operations)
          assertEquals(
            sample.allocationScope,
            if (sample.allocatedBytes.isEmpty) BenchmarkSample.AllocationScope.Unavailable
            else if (workers) BenchmarkSample.AllocationScope.Workers
            else BenchmarkSample.AllocationScope.CallingThread
          )
        case action => fail(s"unexpected action $action")
      }
    }
  }
}
