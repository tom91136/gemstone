package net.kurobako.gemstone.conformance

import java.nio.charset.StandardCharsets
import java.nio.file.Files

final class MiniappDefinitionSuite extends munit.FunSuite {
  private val catalog = MiniappCatalog()
  import catalog.*

  test("miniapps/matrix-definitions") {
    val expected = Map(
      Workload.BabelStream -> Set("cuda", "hip", "opencl", "sycl", "omp-target", "kokkos", "openacc", "std-indices"),
      Workload.MiniBUDE    -> Set("cuda", "hip", "opencl", "sycl", "omp-target", "kokkos", "openacc", "std-indices"),
      Workload.CloverLeaf  ->
        Set("cuda", "hip", "sycl-acc", "sycl-usm", "omp-target", "kokkos", "openacc", "std-indices"),
      Workload.TeaLeaf -> Set("cuda", "hip", "sycl-acc", "sycl-usm", "omp-target", "kokkos", "std-indices")
    )
    require(apps.forall(app => app.ports.map(_.id).toSet == expected(app.workload)))
    require(apps.flatMap(_.ports).count(_ == Port.OpenACC) == 3)
    require(apps.flatMap(_.ports).count(_ == Port.StdPar) == 4)
    require(apps.flatMap(_.ports).count(_ == Port.OMPTarget) == 4)
    require(apps.flatMap(_.ports).count(_ == Port.Kokkos) == 4)
  }

  test("miniapps/artifact-keys-are-portable") {
    val hipDefinitions = apps.flatMap(app => app.ports.filter(_.backend == "hip").map(app -> _)).distinct
    val allKey         = artifactKey("hip", hipDefinitions)
    val reducedKey     = artifactKey("hip", hipDefinitions.take(1))
    require(allKey.length < 100)
    require(reducedKey.length < 100)
    require(allKey != reducedKey)
  }

  test("miniapps/container-build-keeps-model-compilers-isolated") {
    val script = Files.readString(buildScript, StandardCharsets.UTF_8)
    require(script.contains("model_host_cxx=\"$host_cxx\""))
    require(script.contains("model_host_cxx=/usr/bin/amdclang++"))
    require(!script.linesIterator.exists(_.trim == "host_cxx=/usr/bin/amdclang++"))
    require(script.contains("build_one \"$specification\""))
    require(!script.contains("build_one \"$specification\" &"))
    require(script.contains("-G Ninja"))
    require(script.contains("cmake --build \"$build\" --parallel \"$build_jobs\""))
    require(script.contains("CMAKE_BUILD_PARALLEL_LEVEL"))
    require(script.contains("kokkos_source=\"/work/source/kokkos/$app-$model\""))
  }

  test("miniapps/HIP routes are platform-aware and share one HIP oracle") {
    for (port <- Vector(Port.HIP, Port.Kokkos, Port.StdPar)) {
      val linux = supportedProviders(port, net.kurobako.gemstone.launcher.RuntimePlatform.Linux)
      assertEquals(linux, Vector(Provider.HIP, Provider.HIPHSA))
      assertEquals(linux.map(_.oracleProviderId).distinct, Vector("hip"))
      assertEquals(
        supportedProviders(port, net.kurobako.gemstone.launcher.RuntimePlatform.Windows),
        Vector(Provider.HIP)
      )
    }
    assertEquals(
      supportedProviders(Port.OMPTarget, net.kurobako.gemstone.launcher.RuntimePlatform.Linux),
      Vector(Provider.HSA)
    )
    assertEquals(targets(Provider.HIPHSA), targets(Provider.HIP))
  }

  test("miniapps/models-honor-the-configured-interpreter-profile") {
    require(targets(Provider.SYCLOpenCL) == Vector(None))
    require(targets(Provider.SYCLLevelZero) == Vector(None))
    require(
      !executionEnvironment(Port.Kokkos).contains("GEMSTONE_EXECUTION_PARALLELISM"),
      "Kokkos must honor the selected execution worker count"
    )
    require(
      !executionEnvironment(Port.StdPar).contains("GEMSTONE_EXECUTION_PARALLELISM"),
      "StdPar must honor the selected execution worker count"
    )
  }
}
