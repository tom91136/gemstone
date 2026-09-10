package net.kurobako.gemstone.server

import net.kurobako.gemstone.execution.ExecutionLimits
import java.nio.file.{Files, Path}
import java.security.SecureRandom
import java.util.HexFormat
import java.util.concurrent.TimeUnit
import java.util.Properties

/** The native driver client exercises the real CUDA shim, authenticated transport and PTX execution together. */
final class CUDACrossWorkgroupSuite extends munit.FunSuite {
  private def runtimeDirectory: Path = {
    val resource = Option(getClass.getResourceAsStream("/native-runtimes.properties"))
      .getOrElse(fail("CUDA runtime test resource was not generated"))
    val properties = Properties()
    try properties.load(resource)
    finally resource.close()
    val path = Path.of(properties.getProperty("cuda"))
    assert(Files.isDirectory(path), s"generated CUDA runtime directory is missing: $path")
    path
  }

  for (workers <- Seq(1, math.max(1, math.min(4, Runtime.getRuntime.availableProcessors()))).distinct)
    test(s"native CUDA 128-bit payload publication, compaction and atomic counts with $workers workers") {
      val directory = runtimeDirectory
      val runtime   = directory.resolve("libcuda.so.1")
      val client    = directory.resolve("cuda_cross_workgroup_test")
      assert(Files.isRegularFile(runtime), s"CUDA runtime is missing: $runtime")
      assert(Files.isExecutable(client), s"CUDA test executable is missing: $client")
      val bytes = Array.ofDim[Byte](32)
      SecureRandom().nextBytes(bytes)
      val token  = HexFormat.of().formatHex(bytes)
      val server = GemstoneRPCServer.start(
        token,
        ExecutionLimits(1000000, 32),
        Map("GEMSTONE_EXECUTION_PARALLELISM" -> workers.toString)
      )
      try {
        val builder = ProcessBuilder(client.toString).inheritIO()
        builder.environment().put("GEMSTONE_RPC_PORT", server.port.toString)
        builder.environment().put("GEMSTONE_RPC_TOKEN", token)
        builder.environment().put("GEMSTONE_CUDA_TEST_LIBRARY", runtime.toString)
        val process = builder.start()
        try {
          assert(process.waitFor(90, TimeUnit.SECONDS), "native CUDA cross-workgroup test timed out")
          assertEquals(process.exitValue(), 0)
          assert(server.usage.cudaRequests > 0, "native client did not use the authenticated CUDA transport")
          assertEquals(server.usage.executionRequests, 0L)
        } finally if (process.isAlive) { val _ = process.destroyForcibly() }
      } finally server.close()

    }
}
