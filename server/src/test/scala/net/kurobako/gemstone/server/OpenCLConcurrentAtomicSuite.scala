package net.kurobako.gemstone.server

import java.nio.file.Files
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.server.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.*
import scala.concurrent.duration.*
import scala.util.Using

/** Two concurrent dispatches add to the same host memory with workgroup-scope atomics through the OpenCL shim. */
class OpenCLConcurrentAtomicSuite extends Suite {
  override def baseTimeout: FiniteDuration = 120.seconds

  for (
    name <- Vec(
      "OpenCL workgroup-scope atomics on fine-grained SVM are shared by concurrent dispatches",
      "OpenCL workgroup-scope atomics on a host pointer buffer are shared by concurrent dispatches"
    )
  )
    test(name) {
      val directory = native.directory
      val client    = directory.resolve("gemstone_opencl_concurrent_atomics")
      assert(Files.isExecutable(client), s"missing native OpenCL client: $client")
      Using.resource(Server.start(Server.Settings(limits = TaskControl.Limits(100000000L), parallelism = 2))) {
        server =>
          val result = native.run(
            Vec.nonEmpty(client.toString, name),
            directory,
            native.serverEnvironment(server.endpoint, server.client, native.providerEnvironment),
            90.seconds
          )
          assertEquals(result.exit, 0, result.diagnostic)
      }
    }
}
