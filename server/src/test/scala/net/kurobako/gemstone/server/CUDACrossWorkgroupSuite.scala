package net.kurobako.gemstone.server

import net.kurobako.gemstone.NoNul
import net.kurobako.gemstone.execution.ExecutionLimits
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
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
        ExecutionLimits(1000000, 32),
        Map("GEMSTONE_EXECUTION_PARALLELISM" -> workers.toString)
      )) { server =>
        val result = NativeTestProcess.run(
          Vector(client.toString.refineUnsafe[NoNul]).refineUnsafe[MinLength[1]],
          directory,
          Environment(
            Map(
              "GEMSTONE_RPC_PORT"          -> server.port.value.toString,
              "GEMSTONE_RPC_CLIENT"        -> server.client.value.toString,
              "GEMSTONE_CUDA_TEST_LIBRARY" -> runtime.toString
            ),
            Set.empty
          ),
          90.seconds
        )
        assertEquals(result.exit, 0, result.diagnostic)
        assert(server.usage.executionRequests > 0, "native CUDA client did not use the shared execution transport")
      }

    }
}
