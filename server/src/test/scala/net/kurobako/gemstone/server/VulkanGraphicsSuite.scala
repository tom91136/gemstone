package net.kurobako.gemstone.server

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.server.protocol.*
import net.kurobako.gemstone.server.recording.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.*
import scala.concurrent.duration.*
import scala.util.Using

/** Native rendering assertions run against the real Scala execution service. */
class VulkanGraphicsSuite extends Suite {
  override def baseTimeout: FiniteDuration = 360.seconds
  test("Vulkan image operations and graphics snapshots execute through RPC") {
    val directory  = native.directory
    val executable = directory.resolve("gemstone_vulkan_runtime_test")
    files.temporary { root =>
      val journal  = root.resolve("graphics.gmj")
      val settings = Server.Settings(limits = TaskControl.Limits(1000000000L), parallelism = 2, record = Some(journal))
      Using.resource(Server.start(settings)) { server =>
        val result = native.run(
          command = Vec.nonEmpty(executable.toString, "[graphics-execution]"),
          directory = directory,
          changes = native.serverEnvironment(server.endpoint, server.client),
          timeout = 300.seconds
        )
        assertEquals(result.exit, 0, result.diagnostic)
      }
      Using.resource(Journal.open(journal)) { reader =>
        val operations = reader.events.filter(_.kind == Journal.Kind.Request).map(e => Frame.operation(reader.frame(e)))
        assert(operations.contains("SubmitGraphics"), "graphics tests did not submit graphics work over RPC")
      }
    }
  }
}
