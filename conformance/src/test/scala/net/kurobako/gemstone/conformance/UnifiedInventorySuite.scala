package net.kurobako.gemstone.conformance

import scala.concurrent.duration.*

final class UnifiedInventorySuite extends munit.FunSuite {
  test("CTest profiles preserve argument boundaries, environment values, and deadlines") {
    val entries = UnifiedInventory.read(
      """{"tests":[{"name":"sycl/runtime","command":["/work/build/unified_suite_sycl","--case=a b"],
      "properties":[{"name":"TIMEOUT","value":42},{"name":"ENVIRONMENT","value":["KEY=value=with spaces"]}]},
      {"name":"disabled","properties":[{"name":"DISABLED","value":true}]}]}"""
    )
    assertEquals(
      entries,
      Vector(UnifiedInventory.Entry(
        "sycl/runtime",
        Vector("/work/build/unified_suite_sycl", "--case=a b"),
        Map("KEY" -> "value=with spaces"),
        42.seconds
      ))
    )
  }
  test("empty or malformed CTest inventories fail closed") {
    val _ = intercept[IllegalArgumentException](UnifiedInventory.read("""{"tests":[]}"""))
    val _ = intercept[IllegalArgumentException](UnifiedInventory.read("""{"tests":[{"name":"bad","command":[]}]}"""))
    val _ = intercept[IllegalArgumentException](UnifiedInventory.read(
      """{"tests":[{"name":"bad","command":["runner"],"properties":[{"name":"TIMEOUT","value":0}]}]}"""
    ))
  }

  private def entry(backend: String, name: String, profile: String = "full") =
    UnifiedInventory.Entry(
      s"$backend/$name",
      Vector(s"/artifact/$backend", s"--profile=$profile", s"--case=$name"),
      Map.empty,
      120.seconds
    )

  test("portable inventory alignment preserves fixed vendor probes and capability environments") {
    val name = "algorithms/copy/f32/n4096"
    UnifiedInventory.assertAligned(Vector(
      "cuda" -> Vector(entry("cuda", name), entry("cuda", "edge-cases/runtime/cuda/fixed/n1")),
      "hip"  -> Vector(
        entry("hip", name).copy(command = entry("hip", name).command.patch(2, Vector("--filter=^algorithms/[^/]*/f32/"), 0)),
        entry("hip", "algorithms/roc-thrust-semantic/fixed/i32/n11"),
        entry("hip", "async/hip/copy/i32/n1")
      ),
      "sycl" -> Vector(entry("sycl", name).copy(environment = Map("IGC_EnableDPEmulation" -> "1")))
    ))
  }

  test("portable inventory alignment rejects missing sizes and parameter drift") {
    val first = entry("cuda", "algorithms/copy/f32/n1024")
    val large = entry("cuda", "algorithms/copy/f32/n4096")
    val sycl  = entry("sycl", "algorithms/copy/f32/n1024")
    val _     = intercept[IllegalArgumentException] {
      UnifiedInventory.assertAligned(Vector("cuda" -> Vector(first, large), "sycl" -> Vector(sycl)))
    }
    val _ = intercept[IllegalArgumentException] {
      UnifiedInventory.assertAligned(Vector(
        "cuda" -> Vector(first),
        "sycl" -> Vector(sycl.copy(command = sycl.command :+ "--stress-iterations=1"))
      ))
    }
    val _ = intercept[IllegalArgumentException] {
      UnifiedInventory.assertAligned(Vector("cuda" -> Vector(first), "sycl" -> Vector(sycl.copy(timeout = 1.second))))
    }
  }

  test("emulated GEMMs reserve CPU resources uniformly across providers") {
    for (provider <- Vector("cuda", "hip", "sycl-levelzero", "sycl-opencl")) {
      val large = s"unified/$provider/edge-cases/blas/padded-gemm/f32/n1024"
      assertEquals(CTestMatrix.cpuSlots(large, "gemstone", 32), 32)
      assertEquals(CTestMatrix.cpuSlots(large, "oracle", 32), 1)
      assertEquals(CTestMatrix.cpuSlots(large, "gemstone", 8), 8)
      assertEquals(CTestMatrix.cpuSlots(s"unified/$provider/blas/gemm/f64/1024x513x342", "gemstone", 32), 32)
      assertEquals(CTestMatrix.cpuSlots(s"unified/$provider/edge-cases/blas/padded-gemm/f32/n257", "gemstone", 32), 32)
      assertEquals(
        CTestMatrix.cpuSlots(s"unified/$provider/edge-cases/blas/negative-stride-axpy/f32/n1024", "gemstone", 32),
        1
      )
      assertEquals(CTestMatrix.cpuSlots(s"unified/$provider/algorithms/copy/i32/n4096", "gemstone", 32), 1)
    }
  }

}
