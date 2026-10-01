package net.kurobako.gemstone.server

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.environment as env
import net.kurobako.gemstone.vm.*
import scala.concurrent.duration.*
import scala.util.Using

/** Native rendering assertions run against the real Scala execution service. */
class VulkanGraphicsSuite extends munit.FunSuite {
  override val munitTimeout: Duration = 360.seconds
  test("Vulkan image operations and graphics snapshots execute through RPC") {
    val directory  = native.directory
    val executable = directory.resolve("gemstone_vulkan_runtime_test")
    Using.resource(Server.start(Server.Settings(limits = TaskControl.Limits(1000000000L), parallelism = 2))) { server =>
      val result = native.run(
        command = Vec.nonEmpty(executable.toString, "[graphics-execution]"),
        directory = directory,
        changes = Launch.Environment(
          Map(env.RpcEndpoint -> server.endpoint.toString, env.RpcClient -> server.client.toString),
          Set.empty
        ),
        timeout = 300.seconds
      )
      assertEquals(result.exit, 0, result.diagnostic)
      assert(server.usage > 0, "graphics tests did not exercise RPC")
    }
  }
}
