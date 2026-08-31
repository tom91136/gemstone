package net.kurobako.gemstone.launcher

import java.io.File
import java.nio.file.Path

final class LaunchSuite extends munit.FunSuite {
  private val endpoint = Endpoint(43123, "a" * 64)
  private val root     = Path.of("/opt/gemstone")
  private val layout   = RuntimeLayout(
    cudaDriver = Some(root.resolve("cuda/libcuda.so.1")),
    hipRuntime = Some(root.resolve("hip/libamdhip64.so")),
    hsaRuntime = Some(root.resolve("hsa/libhsa-runtime64.so.1")),
    openCLICD = Some(root.resolve("opencl/icd")),
    openCLLoader = Some(root.resolve("opencl/libOpenCL.so.1")),
    levelZeroDriver = Some(root.resolve("levelzero/libze_gemstone.so.1"))
  )

  test("CUDA uses the driver boundary without replacing the CUDA runtime") {
    val plan = Launch.plan(ComputeAPI.CUDA, layout, endpoint, Map("LD_LIBRARY_PATH" -> "/vendor/lib"))
      .fold(error => fail(error.message), identity)

    assertEquals(plan.environment.set("LD_PRELOAD"), root.resolve("cuda/libcuda.so.1").toString)
    assert(plan.environment.set("LD_LIBRARY_PATH").startsWith(root.resolve("cuda").toString))
    assert(!plan.environment.set.values.exists(_.contains("libcudart")))
    assertEquals(plan.requiredEvidence, RequiredEvidence.CUDA)
  }

  test("HIP and direct HSA select their drop-in runtime libraries") {
    val hip = Launch.plan(ComputeAPI.HIP, layout, endpoint, Map.empty).fold(error => fail(error.message), identity)
    val hsa = Launch.plan(ComputeAPI.HSA, layout, endpoint, Map.empty).fold(error => fail(error.message), identity)

    assertEquals(hip.environment.set("LD_PRELOAD"), root.resolve("hip/libamdhip64.so").toString)
    assertEquals(
      hip.environment.set("LD_LIBRARY_PATH"),
      s"${root.resolve("hip")}:${root.resolve("hsa")}"
    )
    assertEquals(hsa.environment.set("LD_PRELOAD"), root.resolve("hsa/libhsa-runtime64.so.1").toString)
    assert(hip.environment.unset.contains("HSA_ENABLE_DTIF"))
    assert(hsa.environment.unset.contains("HSA_TOOLS_LIB"))
  }

  test("OpenCL and Level Zero use their standard provider discovery") {
    val opencl =
      Launch.plan(ComputeAPI.OpenCL, layout, endpoint, Map.empty).fold(error => fail(error.message), identity)
    val level0 =
      Launch.plan(ComputeAPI.LevelZero, layout, endpoint, Map.empty).fold(error => fail(error.message), identity)

    assertEquals(opencl.environment.set("OCL_ICD_VENDORS"), root.resolve("opencl/icd").toString + File.separator)
    assertEquals(opencl.environment.set("LD_PRELOAD"), root.resolve("opencl/libOpenCL.so.1").toString)
    assertEquals(opencl.environment.set("LD_LIBRARY_PATH"), root.resolve("opencl").toString)
    assertEquals(
      level0.environment.set("ZE_ENABLE_ALT_DRIVERS"),
      root.resolve("levelzero/libze_gemstone.so.1").toString
    )
  }

  test("SYCL selects exactly one Gemstone backend") {
    val levelZero = Launch.plan(
      ComputeAPI.SYCL,
      layout.copy(openCLICD = None),
      endpoint,
      Map.empty
    ).fold(error => fail(error.message), identity)
    val openCL = Launch.plan(
      ComputeAPI.SYCL,
      layout.copy(levelZeroDriver = None),
      endpoint,
      Map.empty
    ).fold(error => fail(error.message), identity)

    assertEquals(levelZero.environment.set("ONEAPI_DEVICE_SELECTOR"), "level_zero:gpu")
    assertEquals(
      levelZero.environment.set("ZE_ENABLE_ALT_DRIVERS"),
      root.resolve("levelzero/libze_gemstone.so.1").toString
    )
    assertEquals(openCL.environment.set("ONEAPI_DEVICE_SELECTOR"), "opencl:gpu")
    assertEquals(openCL.environment.set("OCL_ICD_VENDORS"), root.resolve("opencl/icd").toString + File.separator)
    assert(Launch.plan(ComputeAPI.SYCL, layout, endpoint, Map.empty).isLeft)
  }

  test("every plan authenticates to Gemstone and removes ROCr-internal hooks") {
    ComputeAPI.values.foreach { api =>
      val selectedLayout = if (api == ComputeAPI.SYCL) layout.copy(openCLICD = None) else layout
      val plan = Launch.plan(api, selectedLayout, endpoint, Map.empty).fold(error => fail(error.message), identity)
      assertEquals(plan.environment.set("GEMSTONE_RPC_PORT"), endpoint.port.toString)
      assertEquals(plan.environment.set("GEMSTONE_RPC_TOKEN"), endpoint.token)
      assertEquals(plan.environment.set("GEMSTONE_REQUIRED"), "1")
      assert(plan.environment.unset.contains("HSA_ENABLE_DTIF"))
      assert(plan.environment.unset.contains("HSA_TOOLS_LIB"))
    }
  }

  test("a provider cannot be launched without its runtime asset") {
    val missing = layout.copy(cudaDriver = None)
    val result  = Launch.plan(ComputeAPI.CUDA, missing, endpoint, Map.empty)

    assert(result.isLeft)
    assert(result.swap.exists(_.message.contains("CUDA Driver")))
  }

  test("attestation rejects a successful process that did not use Gemstone") {
    val before = UsageSnapshot(cudaRequests = 4, executionRequests = 9)

    assert(!RequiredEvidence.CUDA.satisfied(before, before))
    assert(!RequiredEvidence.Execution.satisfied(before, before))
    assert(RequiredEvidence.CUDA.satisfied(before, before.copy(cudaRequests = 5)))
    assert(RequiredEvidence.Execution.satisfied(before, before.copy(executionRequests = 10)))
  }

  test("process-scoped attestation fails closed when the child never contacts Gemstone") {
    val result = GemstoneProcess.attested(ComputeAPI.CUDA, layout, Map.empty)(_ => 0)

    assert(result.isLeft)
    assert(result.swap.exists(_.message.contains("vendor GPU was used")))
  }
}
