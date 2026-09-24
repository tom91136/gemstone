package net.kurobako.gemstone.launcher

import net.kurobako.gemstone.server.execution.ExecutionService
import net.kurobako.gemstone.server.transport.Transport.*

import com.monovore.decline.Command

import java.nio.file.Path
import scala.util.Properties

final class LaunchSuite extends munit.FunSuite {
  private val port   = RPCPort(43123)
  private val client = ClientId(7L)
  private val root   = Path.of("/opt/gemstone")
  private val layout = RuntimeLayout(root)

  private def parse(arguments: String*): Either[String, Launcher.Options] =
    Command("run", "test launcher options")(Launcher.options).parse(arguments).left.map(help => help.toString)

  test("API lists are comma-separated sets") {
    def apis(value: String) = parse("--apis", value, "--", "application").map(_.apis)
    assertEquals(apis("hip,hsa,cuda,opencl,vulkan,levelzero"), Right(ComputeAPI.values.toSet))
    assert(apis("sycl").isLeft)
    assert(apis("cuda,").isLeft)
  }

  test("platform defaults model the native runtime stack") {
    val expected =
      if (Properties.isLinux) Set(ComputeAPI.HSA, ComputeAPI.CUDA, ComputeAPI.OpenCL, ComputeAPI.LevelZero)
      else Set(ComputeAPI.HIP, ComputeAPI.CUDA, ComputeAPI.OpenCL, ComputeAPI.LevelZero)
    assertEquals(ComputeAPI.defaults, expected)
  }

  test("launcher accepts the public run syntax") {
    val parsed = parse("--apis", "hip,hsa", "--", "application", "argument")
      .fold(help => fail(help), value => value)

    assertEquals(parsed.apis, Set(ComputeAPI.HIP, ComputeAPI.HSA))
    assertEquals(parsed.serverPort, None)
    assertEquals(parsed.executable.value, "application")
    assertEquals(parsed.arguments.map(argument => s"$argument").toVector, Vector("argument"))
  }

  test("launcher accepts a shared server port") {
    val parsed = parse("--server-port", "43123", "--", "application")
      .fold(help => fail(help), value => value)

    assertEquals(parsed.serverPort, Some(RPCPort(43123)))
  }

  test("all selected APIs coexist in one environment") {
    val environment = Launcher.plan(
      ComputeAPI.values.toSet,
      layout,
      port,
      client,
      Map("LD_PRELOAD" -> "/existing.so", "LD_LIBRARY_PATH" -> "/vendor/lib")
    ).fold(error => fail(error), value => value)

    assertEquals(
      environment.set("LD_PRELOAD"),
      s"${layout.library(ComputeAPI.CUDA)}:${layout.library(ComputeAPI.HIP)}:${layout.library(ComputeAPI.HSA)}:${layout.library(ComputeAPI.OpenCL)}:/existing.so"
    )
    assertEquals(environment.set("LD_LIBRARY_PATH"), s"$root:${root.resolve("vulkan")}:/vendor/lib")
    assertEquals(environment.set("OCL_ICD_FILENAMES"), s"${layout.library(ComputeAPI.OpenCL)}")
    assertEquals(environment.set("OCL_ICD_VENDORS"), s"${layout.library(ComputeAPI.OpenCL)}")
    assertEquals(environment.set("ZE_ENABLE_ALT_DRIVERS"), s"${layout.library(ComputeAPI.LevelZero)}")
    assertEquals(environment.set("GEMSTONE_RPC_PORT"), s"${port.value}")
    assertEquals(environment.set("GEMSTONE_RPC_CLIENT"), s"${client.value}")
  }

  test("Vulkan loader directory is absent when Vulkan is not selected") {
    val environment = Launcher.plan(
      Set(ComputeAPI.CUDA),
      layout,
      port,
      client,
      Map.empty
    ).fold(error => fail(error), value => value)
    assertEquals(environment.set("LD_LIBRARY_PATH"), s"$root")
    assert(!environment.set("LD_LIBRARY_PATH").contains(s"${root.resolve("vulkan")}"))
  }

  test("HIP and HSA are independently selectable") {
    val combinations = Vector(
      Set(ComputeAPI.CUDA),
      Set(ComputeAPI.HIP),
      Set(ComputeAPI.HSA),
      Set(ComputeAPI.HIP, ComputeAPI.HSA)
    )
    combinations.foreach { apis =>
      assert(Launcher.plan(apis, layout, port, client, Map.empty).isRight)
    }
  }

  test("an empty API set is rejected") {
    assert(Launcher.plan(Set.empty, layout, port, client, Map.empty).isLeft)
  }

  test("process-scoped execution profiles reject malformed values before launch") {
    val result = Launcher.launch(
      Set(ComputeAPI.CUDA),
      layout,
      Map(ExecutionService.MaxInstructionsEnvironment -> "not-a-number")
    )(_ => 0)
    assert(result.isLeft)
    assert(result.swap.exists(_.contains("invalid execution profile")))
  }
}
