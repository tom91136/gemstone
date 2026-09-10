package net.kurobako.gemstone.server

import java.nio.charset.StandardCharsets
import java.nio.file.Path
import java.util.concurrent.TimeUnit

final class CUDABoundarySuite extends munit.FunSuite {
  test("native CUDA client rejects an invalid server token") {
    val resource = getClass.getResourceAsStream("/native-runtimes.properties")
    require(resource != null, "CUDA native build path was not generated")
    val directory = try {
      val properties = java.util.Properties()
      properties.load(resource)
      Path.of(properties.getProperty("cuda"))
    } finally resource.close()
    val token  = java.util.UUID.randomUUID().toString.replace("-", "") * 2
    val server = GemstoneRPCServer.start(token)
    try {
      val builder = ProcessBuilder(directory.resolve("cuda_rpc_auth_smoke").toString).redirectErrorStream(true)
      builder.environment().put("LD_LIBRARY_PATH", directory.toString)
      builder.environment().put("GEMSTONE_RPC_PORT", server.port.toString)
      builder.environment().put("GEMSTONE_RPC_TOKEN", "0" * 64)
      val process  = builder.start()
      val complete = process.waitFor(10, TimeUnit.SECONDS)
      if !complete then {
        process.destroyForcibly()
        val _ = process.waitFor()
      }
      assert(complete, "native authentication check timed out")
      assertEquals(process.exitValue(), 0, String(process.getInputStream.readAllBytes(), StandardCharsets.UTF_8))
    } finally server.close()
  }
}
