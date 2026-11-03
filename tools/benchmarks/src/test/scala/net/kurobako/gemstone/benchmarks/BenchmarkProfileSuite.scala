package net.kurobako.gemstone.benchmarks

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import net.kurobako.gemstone.benchmarks.BenchmarkArguments.LaunchControl
import net.kurobako.gemstone.profile.Architecture
import net.kurobako.gemstone.profile.ProfileStream
import net.kurobako.gemstone.profile.Profiler
import net.kurobako.gemstone.testing.Suite

class BenchmarkProfileSuite extends Suite {
  private def benchmark(name: String, arguments: Vector[String]): LaunchControl => BenchmarkSample =
    PlatformBenchmarks.parse(name, arguments).fold(fail(_), identity)

  private def architecture(name: String): Architecture =
    Architecture.named(name).getOrElse(fail(s"unknown architecture $name"))

  Vector(
    ("ptx-vector-add", Vector("--iterations", "1"), "sm_89"),
    ("ptx-integer-alu", Vector("--iterations", "1"), "sm_89"),
    (
      "ptx-calls",
      Vector("--iterations", "1", "--lanes", "2", "--groups", "1", "--workers", "1", "--calls", "1"),
      "sm_89"
    ),
    ("ptx-tensor", Vector("--iterations", "1", "--products", "1"), "sm_89"),
    (
      "nvidia-control-flow",
      Vector("--isa", "ptx", "--iterations", "1", "--lanes", "65", "--groups", "3", "--workers", "2", "--rounds", "1"),
      "sm_89"
    ),
    (
      "nvidia-control-flow",
      Vector("--isa", "sass", "--iterations", "1", "--lanes", "65", "--groups", "3", "--workers", "2", "--rounds", "1"),
      "sm_89"
    ),
    ("cuda-cloverleaf-flux", Vector("--iterations", "1"), "sm_89"),
    ("ptx-program-lifecycle", Vector("--operation", "launch", "--entries", "2", "--iterations", "1"), "sm_89"),
    ("ptx-bulk-copy", Vector.empty, "sm_89"),
    (
      "spirv-execution",
      Vector("--iterations", "1", "--groups", "1", "--instructions", "1", "--workers", "1"),
      "gfx1030"
    ),
    ("spirv-cooperative-matrix", Vector("--iterations", "1", "--products", "1"), "gfx1030"),
    ("spirv-image-sampling", Vector("--iterations", "1", "--rounds", "1"), "gfx1030"),
    (
      "opencl-cts-async-copy",
      Vector("--iterations", "1", "--groups", "1", "--local-size", "2", "--stride", "1"),
      "gfx1030"
    ),
    ("amdgpu-vector-copy", Vector("--iterations", "1", "--groups", "1"), "gfx1036"),
    ("amdgpu-dwordx4-copy", Vector("--iterations", "1"), "gfx1036"),
    ("amdgpu-alu", Vector("--iterations", "1", "--instructions", "1", "--mode", "binary"), "gfx1036"),
    ("amdgpu-atomic", Vector("--iterations", "1"), "gfx1036"),
    ("hip-cloverleaf-flux", Vector("--iterations", "1"), "gfx1036")
  ).foreach { (name, arguments, architectureName) =>
    test(s"$name ${arguments.mkString(" ")} profile round-trips through the stream format") {
      val recorded = BenchmarkRunner.record(benchmark(name, arguments)).fold(fail(_), identity)
      val bytes    = ByteArrayOutputStream()
      val out      = DataOutputStream(bytes)
      recorded.write(out)
      out.flush()
      val readBack =
        ProfileStream.read(DataInputStream(ByteArrayInputStream(bytes.toByteArray))).fold(fail(_), identity)
      val arch    = architecture(architectureName)
      val profile = Profiler.run(recorded, arch)
      assertEquals(Profiler.run(readBack, arch), profile)
      assert(profile.total.issues > 0)
    }
  }

  Vector(
    ("ptx-cache", Vector("--workers", "2", "--images", "1", "--entries", "2", "--iterations", "2")),
    ("ptx-program-lifecycle", Vector("--operation", "decode", "--entries", "2", "--iterations", "1")),
    ("amdgpu-dispatch-memory", Vector("--iterations", "1"))
  ).foreach { (name, arguments) =>
    test(s"$name ${arguments.mkString(" ")} has no launch to profile") {
      assert(BenchmarkRunner.record(benchmark(name, arguments)).isLeft)
    }
  }
}
