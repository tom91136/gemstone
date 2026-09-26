package net.kurobako.gemstone.server

import net.kurobako.gemstone.*

import net.kurobako.gemstone.launcher.Environment

import scala.concurrent.duration.*
import scala.util.Using

/** Exercises the native CUDA boundary against whichever Scala server is under test. */
final class CUDABoundarySuite extends munit.FunSuite {
  test("native CUDA client reports an unavailable execution service") {
    val directory = NativeTestProcess.nativeDirectory
    Using.resource(GemstoneRPCServer.start()) { server =>
      val result = NativeTestProcess.run(
        NonEmptyVec(NoNulString.unsafe(directory.resolve("cuda_execution_unavailable_smoke").toString)),
        directory,
        Environment(
          Map(
            "LD_LIBRARY_PATH"     -> directory.toString,
            "GEMSTONE_RPC_PORT"   -> server.port.raw.toString,
            "GEMSTONE_RPC_CLIENT" -> (server.client.raw + 1L).toString
          ),
          Set.empty
        ),
        10.seconds
      )
      assertEquals(result.exit, 0, result.diagnostic)
    }
  }
}
