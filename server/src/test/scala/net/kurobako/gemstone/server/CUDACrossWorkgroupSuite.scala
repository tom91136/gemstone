package net.kurobako.gemstone.server

import net.kurobako.gemstone.Constraint.NoNulString

import net.kurobako.gemstone.Constraint.Positive
import net.kurobako.gemstone.NonEmptyVec
import net.kurobako.gemstone.vm.TaskLimits
import java.nio.file.Files
import scala.concurrent.duration.*
import net.kurobako.gemstone.launcher.Environment
import scala.util.Using

/** The native driver client exercises the CUDA shim, shared execution transport and PTX execution together. */
final class CUDACrossWorkgroupSuite extends munit.FunSuite {
  private def runtimeDirectory = {
    val path = NativeTestProcess.nativeDirectory
    assert(Files.isDirectory(path), s"generated CUDA runtime directory is missing: $path")
    path
  }

  for (workers <- Seq(1, math.max(1, math.min(4, sys.runtime.availableProcessors()))).distinct)
    test(s"native CUDA 128-bit payload publication, compaction and atomic counts with $workers workers") {
      val directory = runtimeDirectory
      val runtime   = directory.resolve("libcuda.so.1")
      val client    = directory.resolve("cuda_cross_workgroup_test")
      assert(Files.isRegularFile(runtime), s"CUDA runtime is missing: $runtime")
      assert(Files.isExecutable(client), s"CUDA test executable is missing: $client")
      Using.resource(GemstoneRPCServer.start(
        TaskLimits(1000000L, 32),
        Map("GEMSTONE_EXECUTION_PARALLELISM" -> workers.toString)
      )) { server =>
        val result = NativeTestProcess.run(
          NonEmptyVec(
            NoNulString.unsafe(client.toString),
            NoNulString.unsafe("~[unified-memory]")
          ),
          directory,
          Environment(
            Map(
              "GEMSTONE_RPC_PORT"      -> server.port.raw.toString,
              "GEMSTONE_RPC_CLIENT"    -> server.client.raw.toString,
              "GEMSTONE_TEST_LIBRARY"  -> runtime.toString,
              "GEMSTONE_SYSTEM_PAGING" -> "1"
            ),
            Set.empty
          ),
          90.seconds
        )
        assertEquals(result.exit, 0, result.diagnostic)
        assert(server.usage > 0, "native CUDA client did not use the shared execution transport")
      }

    }

  // One process per case keeps unsupported registration from hiding later cases.
  // These explicit allocations/registrations must not depend on system paging.
  for (
    name <- Seq(
      "CUDA managed interior aliases preserve host edits between launches",
      "CUDA pinned interior aliases preserve host edits between launches",
      "CUDA registered stack subrange supports kernels and unregister",
      "CUDA registered heap subrange supports kernels and unregister",
      "CUDA managed pointer graph preserves interior targets and pointer bytes",
      "CUDA managed device pointer publication preserves host addresses",
      "CUDA runtime host registration supports a borrowed stack range"
    )
  ) test(name) {
    val directory = runtimeDirectory
    Using.resource(GemstoneRPCServer.start(TaskLimits(1000000L, 32), Map.empty)) {
      server =>
        val result = NativeTestProcess.run(
          NonEmptyVec(
            NoNulString.unsafe(directory.resolve("cuda_cross_workgroup_test").toString),
            NoNulString.unsafe(name)
          ),
          directory,
          Environment(
            Map(
              "GEMSTONE_RPC_PORT"     -> server.port.raw.toString,
              "GEMSTONE_RPC_CLIENT"   -> server.client.raw.toString,
              "GEMSTONE_TEST_LIBRARY" -> directory.resolve("libcuda.so.1").toString
            ),
            Set("GEMSTONE_SYSTEM_PAGING")
          ),
          30.seconds
        )
        assertEquals(result.exit, 0, result.diagnostic)
    }
  }

}
