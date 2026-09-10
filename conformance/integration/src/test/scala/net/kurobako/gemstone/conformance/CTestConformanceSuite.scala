package net.kurobako.gemstone.conformance

import ConformanceSupport.*
import net.kurobako.gemstone.launcher.*
import net.kurobako.gemstone.execution.ExecutionLimits
import net.kurobako.gemstone.server.GemstoneRPCServer

import java.nio.file.Files
import java.nio.file.Path
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import scala.concurrent.duration.*
import scala.jdk.CollectionConverters.*

/** One lifecycle bridge; CTest supplies every individual conformance verdict and all case scheduling. */
final class CTestConformanceSuite extends munit.FunSuite {
  override val munitTimeout: Duration = 24.hours

  test("conformance/ctest") {
    val report =
      sys.env.get("GEMSTONE_CONFORMANCE_REPORT_DIR").fold(target)(Path.of(_)).resolve("ctest")
    Files.createDirectories(report)
    val tokenBytes = Array.ofDim[Byte](32)
    java.security.SecureRandom().nextBytes(tokenBytes)
    val token  = java.util.HexFormat.of().formatHex(tokenBytes)
    val limits = ExecutionLimits.fromEnvironment(Map("GEMSTONE_EXECUTION_MAX_INSTRUCTIONS" -> "2000000000") ++
      TestProfile.environment).fold(error => fail(error), identity)
    val server = GemstoneRPCServer.start(token, limits, TestProfile.environment)
    try {
      val jobs = TestProfile.environment.getOrElse(
        "GEMSTONE_EXECUTION_PARALLELISM",
        Runtime.getRuntime.availableProcessors().toString
      ).toInt
      val matrix = CTestMatrix(report, Endpoint(server.port, token), jobs)
      try {
        emitUnified(matrix)
        MiniappCatalog().emitCTest(matrix)
        CTSCatalog().emitCTest(matrix)
        require(matrix.size > 0, "the selected conformance inventory is empty")
      } finally matrix.close()
      Files.writeString(
        report.resolve("profile.json"),
        ujson.write(
          ujson.Obj(
            "file"    -> TestProfile.current.file.toString,
            "oracles" -> ujson.Arr.from(TestProfile.current.oracles.map(value =>
              ujson.Obj(
                "provider" -> value.provider,
                "target"   -> value.target,
                "resource" -> value.resource
              )
            ))
          ),
          indent = 2
        )
      )

      Files.deleteIfExists(report.resolve("results.xml"))
      Files.deleteIfExists(report.resolve("comparisons.xml"))
      val result = run(
        Vector(
          "ctest",
          "--test-dir",
          report.toString,
          "--parallel",
          (jobs + TestProfile.current.oracles.map(_.resource).distinct.size).toString,
          "--resource-spec-file",
          matrix.resourceFile.toString,
          "--output-on-failure",
          "--output-junit",
          report.resolve("results.xml").toString
        ),
        root,
        timeout = 24.hours
      )
      val comparisons = run(
        Vector(
          "python3",
          root.resolve("conformance/tools/case-run.py").toString,
          "--compare",
          report.resolve("cases.jsonl").toString
        ),
        root
      )
      assertEquals(result.exit, 0, result.diagnostic)
      assertEquals(comparisons.exit, 0, comparisons.diagnostic)
    } finally server.close()
  }

  private def artifact(backend: String): Path = {
    val source = root.resolve("conformance/unified")
    val files  = Files.walk(source)
    val inputs = try files.iterator.asScala.filter(path =>
        Files.isRegularFile(path) &&
          (path.getFileName.toString == "CMakeLists.txt" ||
            Vector(".hpp", ".h", ".cpp", ".c", ".cu", ".cmake", ".in", ".sh").exists(path.toString.endsWith))
      ).toVector.sortBy(_.toString)
    finally files.close()
    val digest = MessageDigest.getInstance("SHA-256")
    (inputs ++ Vector(
      root.resolve("conformance/tools/container-common.sh"),
      root.resolve("conformance/tools/runtime-bundle.sh"),
      root.resolve("conformance/containers/Containerfile")
    )).foreach { path =>
      digest.update(root.relativize(path).toString.getBytes(StandardCharsets.UTF_8))
      digest.update(Files.readAllBytes(path))
    }
    val environment = TestProfile.environment.filter { case (key, _) =>
      key.startsWith("UNIFIED_SUITE_") || key.endsWith("_IMAGE") ||
      Set("GEMSTONE_CUDA_ARCH", "GEMSTONE_AMDGPU_TARGETS").contains(key)
    }
    digest.update(environment.toVector.sorted.mkString("\n").getBytes(StandardCharsets.UTF_8))
    val hash   = java.util.HexFormat.of().formatHex(digest.digest()).take(16)
    val base   = sys.env.get("GEMSTONE_CONFORMANCE_ARTIFACTS").fold(target.resolve("artifacts"))(Path.of(_))
    val output = base.resolve("unified").resolve(backend).resolve(hash)
    if
      !Files.isRegularFile(output.resolve("ctest-inventory.json")) ||
      !Files.isRegularFile(output.resolve("manifest.tsv"))
    then
      runChecked(
        Vector("bash", source.resolve("tools/container-build.sh").toString, backend, output.toString),
        root,
        TestProfile.current.environment ++ TestProfile.current.buildEnvironment ++ environment
      )
    output
  }

  private def emitUnified(matrix: CTestMatrix): Unit = {
    def backendAPI(backend: String): ComputeAPI = backend match {
      case "cuda"   => ComputeAPI.CUDA
      case "hip"    => ComputeAPI.HIP
      case "hsa"    => ComputeAPI.HSA
      case "opencl" => ComputeAPI.OpenCL
      case "sycl"   => ComputeAPI.SYCL
      case "host"   => ComputeAPI.HSA
    }
    val backends = sys.env.getOrElse(
      "GEMSTONE_CONFORMANCE_BACKENDS",
      "host,cuda,hip,hsa,opencl,sycl"
    ).split(',').toVector.filter(_.nonEmpty)
    require(backends.forall(Set("host", "cuda", "hip", "hsa", "opencl", "sycl")), "invalid conformance backend")
    val inventories = Vector.newBuilder[(String, Vector[UnifiedInventory.Entry])]
    backends.foreach { backend =>
      val providers = backend match {
        case "sycl" => Vector("sycl-levelzero", "sycl-opencl")
        case "hip"  => HIPRuntimeRoute.supported(RuntimePlatform.current).toVector.map(_.id)
        case _      => Vector(backend)
      }
      if providers.exists(provider => maySelectPrefix(s"unified/$provider/")) then
        try {
          val output    = artifact(backend)
          val inventory = UnifiedInventory.read(Files.readString(output.resolve("ctest-inventory.json")))
          inventories += backend -> inventory
          providers.foreach { provider =>
            val selected = select(inventory.zipWithIndex.map { case (entry, index) =>
              index -> s"unified/$provider/${entry.name.stripPrefix(backend + "/")}"
            })
            selected.foreach { case (index, name) =>
              val entry      = inventory(index)
              val executable = Path.of(entry.command.head).getFileName.toString
              val command    = Vector(
                "bash",
                root.resolve("conformance/unified/tools/run-relocated.sh").toString,
                output.toString,
                backend,
                "--runner",
                executable
              ) ++ entry.command.tail
              require(
                backend == "host" || entry.command.exists(_.startsWith("--case=")),
                "artifact uses obsolete aggregate tests; rebuild it"
              )
              val api = backend match {
                case "cuda"   => ComputeAPI.CUDA
                case "hip"    => ComputeAPI.HIP
                case "hsa"    => ComputeAPI.HSA
                case "opencl" => ComputeAPI.OpenCL
                case _        => ComputeAPI.SYCL
              }
              val sycl = if backend == "sycl" then
                Map(
                  "ONEAPI_DEVICE_SELECTOR" -> (if provider == "sycl-opencl" then "opencl:gpu" else "level_zero:gpu"),
                  "UR_ADAPTERS_FORCE_LOAD" -> syclAdapter(output, provider == "sycl-opencl").get.toString
                )
              else Map.empty[String, String]
              val layout         = if backend == "host" then RuntimeLayout() else providerLayout(provider, output)
              val nativeProvider = if provider == "hip-hsa" then "hip" else provider
              val nativeName = if provider == "hip-hsa" then name.replace("unified/hip-hsa/", "unified/hip/") else name
              matrix.add(
                name,
                provider,
                api,
                layout,
                command,
                root,
                entry.environment ++ sycl ++ targetEnvironment(backend, output),
                entry.timeout.toSeconds.toDouble,
                oracleName = Some(nativeName),
                oracleProvider = Some(nativeProvider),
                runtimeRoute = runtimeProvenance(layout)
              )
            }
          }
        } catch {
          case scala.util.control.NonFatal(error) =>
            matrix.add(
              s"unified/$backend/fixture-build",
              backend,
              backendAPI(backend),
              RuntimeLayout(),
              Vector.empty,
              root,
              Map.empty,
              120.0,
              buildFailure = Some(error.toString)
            )
        }
    }
    UnifiedInventory.assertAligned(inventories.result())
  }
  private def targetEnvironment(backend: String, artifact: Path): Map[String, String] = {
    val variable = backend match {
      case "cuda"        => Some("GEMSTONE_CUDA_TARGET")
      case "hip" | "hsa" => Some("GEMSTONE_AMDGPU_TARGET")
      case _             => None
    }
    variable.flatMap { name =>
      val manifest = Files.readString(artifact.resolve("manifest.tsv")).linesIterator.flatMap { line =>
        line.split("\t", 2) match {
          case Array(key, value) => Some(key -> value)
          case _                 => None
        }
      }.toMap
      val targets = manifest.get("target-archs").orElse(manifest.get("target-arch"))
        .toVector.flatMap(_.split("[,;]")).filter(_.nonEmpty)
      val selected = sys.env.get(name).filter(_.nonEmpty).orElse(if targets.size == 1 then targets.headOption else None)
      require(targets.size <= 1 || selected.nonEmpty, s"artifact contains multiple targets; set $name")
      selected.map(name -> _)
    }.toMap
  }

  private def providerLayout(provider: String, artifact: Path): RuntimeLayout = {
    def required(variable: String, name: String): Path = providerPath(variable, name)
      .getOrElse(throw IllegalStateException(s"provider $name is absent; set $variable"))
    provider match {
      case "cuda" => RuntimeLayout(cudaDriver = Some(required("GEMSTONE_CUDA_DRIVER", "libcuda.so.1")))
      case "hip"  => RuntimeLayout(
          hipRuntime = Some(required("GEMSTONE_HIP_RUNTIME", "libamdhip64.so.7")),
          hsaRuntime = providerPath("GEMSTONE_HSA_RUNTIME", "libhsa-runtime64.so.1")
        )
      case "hip-hsa" =>
        val vendor = artifact.resolve("lib/libamdhip64.so.7")
        require(Files.isRegularFile(vendor), s"artifact vendor HIP runtime is absent: $vendor")
        RuntimeLayout(
          hipRuntime = Some(vendor),
          hsaRuntime = Some(required("GEMSTONE_HSA_RUNTIME", "libhsa-runtime64.so.1")),
          hipRoute = HIPRuntimeRoute.VendorHIPOnHSA
        )
      case "hsa" => RuntimeLayout(hsaRuntime = Some(required("GEMSTONE_HSA_RUNTIME", "libhsa-runtime64.so.1")))
      case "sycl-levelzero" =>
        RuntimeLayout(levelZeroDriver = Some(required("GEMSTONE_LEVELZERO_DRIVER", "libze_gemstone.so.1")))
      case "sycl-opencl" | "opencl" => RuntimeLayout(
          openCLICD = Some(openCLICD.getOrElse(throw IllegalStateException("set GEMSTONE_OPENCL_ICD"))),
          openCLLoader =
            Some(preferredOpenCLLoader(artifact).getOrElse(throw IllegalStateException("OpenCL loader is absent")))
        )
      case _ => throw IllegalArgumentException(s"unknown provider: $provider")
    }
  }
}
