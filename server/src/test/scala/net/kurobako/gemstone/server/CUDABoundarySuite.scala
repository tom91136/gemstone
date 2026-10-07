package net.kurobako.gemstone.server

import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.server.*
import scala.concurrent.duration.*
import scala.util.Using

/** Exercises the native CUDA boundary against whichever Scala server is under test. */
class CUDABoundarySuite extends munit.FunSuite {
  test("native CUDA client reports an unavailable execution service") {
    val directory = native.directory
    Using.resource(Server.start(Server.Settings(parallelism = 2))) { server =>
      val result = native.run(
        Vec.nonEmpty(directory.resolve("cuda_execution_unavailable_smoke").toString),
        directory,
        native.serverEnvironment(server.endpoint, server.client.next, Map("LD_LIBRARY_PATH" -> directory.toString)),
        10.seconds
      )
      assertEquals(result.exit, 0, result.diagnostic)
    }
  }
}
