package net.kurobako.gemstone.launcher

import net.kurobako.gemstone.server.GemstoneRPCServer

import java.nio.file.Path
import scala.util.Using

final class ProcessExitSuite extends munit.FunSuite {
  private val layout = RuntimeLayout(Path.of("/unused"))
  private val apis   = Set(ComputeAPI.HSA)

  test("child exit status is preserved") {
    assertEquals(Launcher.launch(apis, layout, Map.empty)(_ => 7), Right(7))
  }

  test("child receives the selected provider environment") {
    val result = Launcher.launch(apis, layout, Map.empty) { environment =>
      environment.set("LD_PRELOAD")
    }
    assertEquals(result, Right(layout.absolute.library(ComputeAPI.HSA).toString))
  }

  test("child receives an isolated client from a shared server") {
    Using.resource(GemstoneRPCServer.start()) { server =>
      val result = Launcher.launch(apis, layout, Map.empty, Some(server.port)) { environment =>
        val first  = environment.set("GEMSTONE_RPC_CLIENT")
        val second = Launcher.launch(apis, layout, Map.empty, Some(server.port)) { other =>
          assertEquals(other.set("GEMSTONE_RPC_PORT"), server.port.value.toString)
          other.set("GEMSTONE_RPC_CLIENT")
        }
        first -> second
      }
      assert(result.exists { case (first, second) =>
        first != server.client.value.toString && second.exists(_ != first)
      })
    }
  }
}
