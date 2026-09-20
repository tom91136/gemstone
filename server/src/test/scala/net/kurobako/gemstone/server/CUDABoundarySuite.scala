package net.kurobako.gemstone.server

import net.kurobako.gemstone.NoNul
import net.kurobako.gemstone.launcher.Environment

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import scala.concurrent.duration.*
import scala.util.Using

/** Exercises the native CUDA boundary against whichever Scala server is under test. */
final class CUDABoundarySuite extends munit.FunSuite {
  test("native CUDA client reports an unavailable execution service") {
    val directory = NativeTestProcess.nativeDirectory
    Using.resource(GemstoneRPCServer.start()) { server =>
      val result = NativeTestProcess.run(
        Vector(
          directory.resolve("cuda_execution_unavailable_smoke").toString.refineUnsafe[NoNul]
        ).refineUnsafe[MinLength[1]],
        directory,
        Environment(
          Map(
            "LD_LIBRARY_PATH"     -> directory.toString,
            "GEMSTONE_RPC_PORT"   -> server.port.value.toString,
            "GEMSTONE_RPC_CLIENT" -> (server.client.value + 1L).toString
          ),
          Set.empty
        ),
        10.seconds
      )
      assertEquals(result.exit, 0, result.diagnostic)
    }
  }
}
