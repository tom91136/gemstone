package net.kurobako.gemstone.launcher

import net.kurobako.gemstone.server.execution.ExecutionConfiguration
import net.kurobako.gemstone.server.transport.{ClientId, RPCPort}

import com.monovore.decline.Command

import java.nio.file.Path

final class LaunchSuite extends munit.FunSuite {
  private val endpoint = Endpoint(RPCPort(43123), ClientId(7L))
  private val root     = Path.of("/opt/gemstone")
  private val layout   = RuntimeLayout(root, RuntimePlatform.Linux)

  test("API lists are comma-separated sets") {
    assertEquals(
      ComputeAPI.parseAll("hip,hsa,cuda,opencl,levelzero"),
      Right(ComputeAPI.values.toSet)
    )
    assert(ComputeAPI.parseAll("sycl").isLeft)
    assert(ComputeAPI.parseAll("cuda,").isLeft)
  }

  test("platform defaults model the native runtime stack") {
    assertEquals(
      ComputeAPI.defaults(RuntimePlatform.Linux),
      Set(ComputeAPI.HSA, ComputeAPI.CUDA, ComputeAPI.OpenCL, ComputeAPI.LevelZero)
    )
    assertEquals(
      ComputeAPI.defaults(RuntimePlatform.Windows),
      Set(ComputeAPI.HIP, ComputeAPI.CUDA, ComputeAPI.OpenCL, ComputeAPI.LevelZero)
    )
  }

  test("launcher accepts the public run syntax") {
    val parsed = Command("run", "test launcher options")(GemstoneLauncher.options)
      .parse(Seq("--apis", "hip,hsa", "--", "application", "argument"))
      .fold(help => fail(s"$help"), value => value)

    assertEquals(parsed.apis, Set(ComputeAPI.HIP, ComputeAPI.HSA))
    assertEquals(parsed.serverPort, None)
    assertEquals(parsed.executable.value, "application")
    assertEquals(parsed.arguments.map(argument => s"$argument").toVector, Vector("argument"))
  }

  test("launcher accepts a shared server port") {
    val parsed = Command("run", "test launcher options")(GemstoneLauncher.options)
      .parse(Seq("--server-port", "43123", "--", "application"))
      .fold(help => fail(s"$help"), value => value)

    assertEquals(parsed.serverPort, Some(RPCPort(43123)))
  }

  test("all selected APIs coexist in one environment") {
    val plan = Launch.plan(
      ComputeAPI.values.toSet,
      layout,
      endpoint,
      Map("LD_PRELOAD" -> "/existing.so", "LD_LIBRARY_PATH" -> "/vendor/lib")
    ).fold(error => fail(error.message), value => value)

    assertEquals(
      plan.environment.set("LD_PRELOAD"),
      s"${root.resolve("libcuda.so.1")}:${root.resolve("libamdhip64.so.7")}:${root.resolve("libhsa-runtime64.so.1")}:${root.resolve("libOpenCL_gemstone.so.1")}:/existing.so"
    )
    assertEquals(plan.environment.set("LD_LIBRARY_PATH"), s"$root:/vendor/lib")
    assertEquals(plan.environment.set("OCL_ICD_FILENAMES"), s"${root.resolve("libOpenCL_gemstone.so.1")}")
    assertEquals(plan.environment.set("ZE_ENABLE_ALT_DRIVERS"), s"${root.resolve("libze_gemstone.so.1")}")
    assertEquals(plan.environment.set("GEMSTONE_RPC_PORT"), s"${endpoint.port.value}")
    assertEquals(plan.environment.set("GEMSTONE_RPC_CLIENT"), s"${endpoint.client.value}")
    assert(plan.environment.unset("CONFORMANCE_DEVICE"))
  }

  test("HIP and HSA are independently selectable") {
    val combinations = Vector(
      Set(ComputeAPI.CUDA),
      Set(ComputeAPI.HIP),
      Set(ComputeAPI.HSA),
      Set(ComputeAPI.HIP, ComputeAPI.HSA)
    )
    combinations.foreach { apis =>
      val plan = Launch.plan(apis, layout, endpoint, Map.empty).fold(error => fail(error.message), value => value)
      assertEquals(plan.apis, apis)
    }
  }

  test("conformance target context configures virtual architectures") {
    val cuda = Launch.plan(
      Set(ComputeAPI.CUDA),
      layout,
      endpoint,
      Map("CONFORMANCE_TARGET_BACKEND" -> "cuda", "CONFORMANCE_TARGET_ARCHITECTURE" -> "sm_89")
    ).fold(error => fail(error.message), value => value)
    assertEquals(cuda.environment.set.get("GEMSTONE_CUDA_TARGET"), Some("sm_89"))

    val hsa = Launch.plan(
      Set(ComputeAPI.HSA),
      layout,
      endpoint,
      Map("CONFORMANCE_TARGET_BACKEND" -> "hip", "CONFORMANCE_TARGET_ARCHITECTURE" -> "gfx1030")
    ).fold(error => fail(error.message), value => value)
    assertEquals(hsa.environment.set.get("GEMSTONE_AMDGPU_TARGET"), Some("gfx1030"))
  }

  test("an empty API set is rejected") {
    assert(Launch.plan(Set.empty, layout, endpoint, Map.empty).isLeft)
  }

  test("process-scoped execution profiles reject malformed values before launch") {
    val result = GemstoneProcess.run(
      Set(ComputeAPI.CUDA),
      layout,
      Map(ExecutionConfiguration.MaxInstructionsEnvironment -> "not-a-number")
    )(_ => 0)
    assert(result.isLeft)
    assert(result.swap.exists(_.message.contains("invalid execution profile")))
  }
}
