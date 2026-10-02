package net.kurobako.gemstone.server

import java.nio.file.Files
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.environment as env
import net.kurobako.gemstone.server.*
import net.kurobako.gemstone.vm.*
import scala.concurrent.duration.*
import scala.util.Using

/** The native driver client exercises the CUDA shim, shared execution transport and PTX execution together. */
class CUDACrossWorkgroupSuite extends munit.FunSuite {
  override val munitTimeout: Duration = 120.seconds

  private def runtimeDirectory = {
    val path = native.directory
    assert(Files.isDirectory(path), s"generated CUDA runtime directory is missing: $path")
    path
  }

  for (workers <- Vec(1, 4))
    test(s"native CUDA 128-bit payload publication, compaction and atomic counts with $workers workers") {
      val directory = runtimeDirectory
      val runtime   = directory.resolve("libcuda.so.1")
      val client    = directory.resolve("cuda_cross_workgroup_test")
      assert(Files.isRegularFile(runtime), s"CUDA runtime is missing: $runtime")
      assert(Files.isExecutable(client), s"CUDA test executable is missing: $client")
      Using.resource(Server.start(
        Server.Settings(limits = TaskControl.Limits(1000000L), parallelism = workers.unsafe[Int > 0])
      )) { server =>
        val result = native.run(
          Vec.nonEmpty(
            client.toString,
            "~[unified-memory]"
          ),
          directory,
          Launch.Environment(
            Map(
              env.RpcEndpoint  -> server.endpoint.toString,
              env.RpcClient    -> server.client.raw.toString,
              env.TestLibrary  -> runtime.toString,
              env.TargetArch   -> "sm_80",
              env.SystemPaging -> "1"
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
    name <- Vec(
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
    Using.resource(Server.start(Server.Settings(limits = TaskControl.Limits(1000000L), parallelism = 2))) {
      server =>
        val result = native.run(
          Vec.nonEmpty(
            directory.resolve("cuda_cross_workgroup_test").toString,
            name
          ),
          directory,
          Launch.Environment(
            Map(
              env.RpcEndpoint -> server.endpoint.toString,
              env.RpcClient   -> server.client.raw.toString,
              env.TestLibrary -> directory.resolve("libcuda.so.1").toString
            ),
            Set(env.SystemPaging)
          ),
          30.seconds
        )
        assertEquals(result.exit, 0, result.diagnostic)
    }
  }

}
