package net.kurobako.gemstone.conformance

import ConformanceSupport.*
import net.kurobako.gemstone.launcher.ComputeAPI
import net.kurobako.gemstone.launcher.RuntimeLayout

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future as JavaFuture
import scala.concurrent.duration.*
import scala.jdk.CollectionConverters.*

/** The vendor-neutral bounded BLAS smoke harness, paired through HIP for every selected GFX target. */
final class RocBLASConformanceSuite extends munit.FunSuite {
  override val munitTimeout: Duration = 30.minutes

  private enum Coverage(val id: String) {
    case All    extends Coverage("all")
    case Level1 extends Coverage("level1")
    case Level2 extends Coverage("level2")
    case Level3 extends Coverage("level3")
  }

  private enum NumericType(val id: String) {
    case Half   extends NumericType("f16")
    case Float  extends NumericType("f32")
    case Double extends NumericType("f64")
    case Int8   extends NumericType("i8")
  }

  private final case class TargetCase(target: String, numericType: NumericType, coverage: Coverage, identity: String)

  private val rocBLASPin = pin("rocblas")
  private val source     = prepareArchive(
    s"rocblas-${rocBLASPin.commit}",
    s"https://github.com/ROCm/rocBLAS/archive/${rocBLASPin.commit}.tar.gz",
    rocBLASPin.sha256
  )
  private val buildRoot      = target.resolve(s"blas-smoke-${rocBLASPin.commit}")
  private val executable     = buildRoot.resolve("rocblas_smoke")
  private val program        = root.resolve("conformance/blas/programs/rocblas_smoke.cpp")
  private val commonInclude  = root.resolve("conformance/blas/include")
  private val adapterInclude = root.resolve("conformance/blas/adapters")
  private val targetMatrix   = amdgpuTargetMatrix
  private val hipcc          = sys.env.get("GEMSTONE_HIPCC").filter(_.nonEmpty).map(Path.of(_))
    .getOrElse(Path.of("/usr/bin/hipcc"))
  private val rocmRoot = sys.env.get("ROCM_PATH").filter(_.nonEmpty).map(Path.of(_)).getOrElse {
    Vector(Path.of("/opt/rocm"), Path.of("/usr")).find(Files.isDirectory(_)).getOrElse(Path.of("/opt/rocm"))
  }
  private val rocBLASLibrary = sys.env.get("GEMSTONE_ROCBLAS_LIBRARY").filter(_.nonEmpty).map(Path.of(_)).getOrElse {
    Vector(
      Path.of("/opt/rocm/lib/librocblas.so"),
      Path.of("/opt/rocm/lib/librocblas.so.5"),
      Path.of("/usr/lib64/librocblas.so"),
      Path.of("/usr/lib64/librocblas.so.5"),
      Path.of("/usr/lib64/librocblas.so.5.1"),
      Path.of("/usr/lib/librocblas.so"),
      Path.of("/usr/lib/librocblas.so.5")
    ).find(Files.isRegularFile(_)).getOrElse(Path.of("/opt/rocm/lib/librocblas.so"))
  }
  private val tensileLibrary =
    sys.env.get("GEMSTONE_ROCBLAS_TENSILE_LIBPATH").filter(_.nonEmpty).map(Path.of(_)).orElse {
      Vector(
        rocBLASLibrary.getParent.resolve("rocblas/library"),
        rocmRoot.resolve("rocblas/library"),
        rocmRoot.resolve("lib/rocblas/library")
      ).find(Files.isDirectory(_))
    }
  private val matrixSize = sys.env.get("GEMSTONE_ROCBLAS_MATRIX_SIZE").filter(_.nonEmpty).map { raw =>
    val value = raw.toInt
    require(value >= 2 && value <= 128, s"GEMSTONE_ROCBLAS_MATRIX_SIZE must be between 2 and 128, found $value")
    value
  }

  require(Files.isExecutable(hipcc), s"HIP compiler is absent: $hipcc; set GEMSTONE_HIPCC")
  require(Files.isDirectory(rocmRoot), s"ROCm root is absent: $rocmRoot; set ROCM_PATH")
  require(
    Files.isRegularFile(rocBLASLibrary),
    s"rocBLAS library is absent: $rocBLASLibrary; set GEMSTONE_ROCBLAS_LIBRARY"
  )
  require(Files.isRegularFile(program), s"BLAS smoke source is absent: $program")

  // The installed package carries the shared library but not the generated
  // rocBLAS export/version headers. Reuse the existing pinned-header staging
  // helper; no rocBLAS client or gtest build is needed by this lane.
  prepareClientInstall()

  private val clientInclude = target.resolve(s"rocblas-${rocBLASPin.commit}/client-install/include")
  private val numericTypes  = configuredNumericTypes()
  private val coverages     = configuredCoverage()
  private val cases         = targetMatrix.targets.flatMap { target =>
    numericTypes.flatMap { numericType =>
      coverages.map(coverage => TargetCase(target, numericType, coverage, s"$target/${numericType.id}/${coverage.id}"))
    }
  }
  private val selected =
    select(cases.zipWithIndex.map { case (testCase, index) => index -> testCase.identity }, "ROCBLAS")
      .map { case (index, _) => cases(index) }
  private val jobs = sys.env.get("GEMSTONE_ROCBLAS_JOBS")
    .fold(Math.min(4, Runtime.getRuntime.availableProcessors))(_.toInt)
  private var executor   = Option.empty[ExecutorService]
  private var executions = Map.empty[String, JavaFuture[Unit]]

  override def beforeAll(): Unit = {
    require(jobs >= 1 && jobs <= 32, s"GEMSTONE_ROCBLAS_JOBS must be between 1 and 32, found $jobs")
    compileHarness()
    require(Files.isExecutable(executable), s"rocBLAS smoke client was not built: $executable")
    val pool = Executors.newFixedThreadPool(jobs)
    executor = Some(pool)
    executions = selected.map { testCase =>
      val task: Callable[Unit] = () => runCase(testCase)
      testCase.identity -> pool.submit(task)
    }.toMap
  }

  override def afterAll(): Unit = executor.foreach(_.shutdown())

  selected.foreach { testCase =>
    test(s"rocblas/${testCase.identity}") {
      await(testCase.identity)
    }
  }

  private def await(identity: String): Unit =
    try executions.getOrElse(identity, fail(s"parallel test was not scheduled: $identity")).get()
    catch {
      case error: ExecutionException => throw error.getCause
    }

  private def compileHarness(): Unit = {
    Files.createDirectories(buildRoot)
    val rocblasInclude = source.resolve("library/include")
    val command        = Vector(hipcc.toString, "-std=c++17", "-O2") ++
      targetMatrix.targets.map(target => s"--offload-arch=$target") ++
      Vector(
        s"-I${cmakePath(commonInclude)}",
        s"-I${cmakePath(adapterInclude)}",
        s"-I${cmakePath(rocblasInclude)}",
        s"-I${cmakePath(clientInclude.resolve("rocblas"))}",
        s"-I${cmakePath(clientInclude.resolve("rocblas/internal"))}",
        program.toString,
        s"-L${cmakePath(rocBLASLibrary.getParent)}",
        s"-L${cmakePath(rocmRoot.resolve("lib"))}",
        s"-Wl,-rpath,${cmakePath(rocBLASLibrary.getParent)}",
        "-lamdhip64",
        s"-Wl,-l:${rocBLASLibrary.getFileName}",
        "-o",
        executable.toString
      )
    runChecked(command, root)
  }

  private def runCase(testCase: TargetCase): Unit = {
    val result = runPaired(
      ComputeAPI.HIP,
      runtimeLayout,
      Vector(executable.toString, testCase.coverage.id, testCase.numericType.id),
      buildRoot,
      oracleEnvironment,
      gemstoneEnvironment(testCase.target),
      timeout = 10.minutes
    )
    if result.oracleRan then
      require(result.oracle.exit == 0, s"oracle/${testCase.identity}:\n${result.oracle.diagnostic}")
    require(result.gemstone.exit == 0, s"gemstone/${testCase.identity}:\n${result.gemstone.diagnostic}")
  }

  private def runtimeLayout: RuntimeLayout = RuntimeLayout(
    hipRuntime = sys.env.get("GEMSTONE_HIP_RUNTIME").filter(_.nonEmpty).map(Path.of(_)),
    hsaRuntime = sys.env.get("GEMSTONE_HSA_RUNTIME").filter(_.nonEmpty).map(Path.of(_))
  )

  private def sharedEnvironment: Map[String, String] =
    Map("LD_LIBRARY_PATH" -> libraryPath(Vector(
      rocBLASLibrary.getParent,
      rocmRoot.resolve("lib"),
      rocmRoot.resolve("lib64")
    ))) ++ tensileLibrary.map(path => "ROCBLAS_TENSILE_LIBPATH" -> path.toString) ++
      matrixSize.map(value => "GEMSTONE_BLAS_MATRIX_SIZE" -> value.toString)

  private def oracleEnvironment: Map[String, String] = sharedEnvironment

  private def gemstoneEnvironment(target: String): Map[String, String] =
    sharedEnvironment + ("GEMSTONE_AMDGPU_TARGET" -> target)

  private def configuredCoverage(): Vector[Coverage] =
    sys.env.get("GEMSTONE_ROCBLAS_LEVELS").filter(_.nonEmpty).fold {
      Vector(Coverage.All)
    } { raw =>
      val parsed = raw.split("[,|]").iterator.map(_.trim.toLowerCase).filter(_.nonEmpty).map {
        case "all"    => Coverage.All
        case "level1" => Coverage.Level1
        case "level2" => Coverage.Level2
        case "level3" => Coverage.Level3
        case value    => throw IllegalArgumentException(s"invalid rocBLAS smoke level: $value")
      }.toVector.distinct
      require(parsed.nonEmpty, "GEMSTONE_ROCBLAS_LEVELS must contain at least one level")
      parsed
    }

  private def configuredNumericTypes(): Vector[NumericType] =
    sys.env.get("GEMSTONE_ROCBLAS_TYPES").filter(_.nonEmpty).fold {
      // Integer GEMM is a vendor extension with different operand and result
      // types, so it is opt-in until the common mixed-precision contract is
      // added. Half is included even though each backend may skip operations
      // absent from its public API.
      Vector(NumericType.Float, NumericType.Double, NumericType.Half)
    } { raw =>
      val parsed = raw.split("[,|]").iterator.map(_.trim.toLowerCase).filter(_.nonEmpty).map {
        case "half" | "f16"   => NumericType.Half
        case "float" | "f32"  => NumericType.Float
        case "double" | "f64" => NumericType.Double
        case "int8" | "i8"    => NumericType.Int8
        case value            => throw IllegalArgumentException(s"invalid rocBLAS smoke numeric type: $value")
      }.toVector.distinct
      require(parsed.nonEmpty, "GEMSTONE_ROCBLAS_TYPES must contain at least one type")
      parsed
    }

  private def cmakePath(path: Path): String =
    path.toAbsolutePath.normalize.toString.replace('\\', '/')

  /** Stage the pinned public rocBLAS headers and the two generated package headers required by rocblas.h. */
  private def prepareClientInstall(): Unit = {
    val clientInstall = target.resolve(s"rocblas-${rocBLASPin.commit}/client-install")
    val marker        = clientInstall.resolve(".gemstone-client-install")
    val markerValue   = s"${rocBLASPin.sha256}\nclient-install-v2\n"
    if (!Files.isRegularFile(marker) || Files.readString(marker, StandardCharsets.UTF_8) != markerValue) {
      if (Files.exists(clientInstall)) {
        val tree = Files.walk(clientInstall)
        try tree.sorted(java.util.Comparator.reverseOrder()).iterator().asScala.foreach(Files.delete)
        finally tree.close()
      }
      val sourceInclude = source.resolve("library/include")
      val includeRoot   = clientInstall.resolve("include/rocblas")
      Files.createDirectories(includeRoot)
      val files = Files.walk(sourceInclude)
      try files.iterator().asScala.filter(Files.isRegularFile(_)).foreach { file =>
          val destination = includeRoot.resolve(sourceInclude.relativize(file))
          Files.createDirectories(destination.getParent)
          Files.createSymbolicLink(destination, file)
        }
      finally files.close()
      val internal = includeRoot.resolve("internal")
      Files.writeString(
        internal.resolve("rocblas-export.h"),
        """#ifndef ROCBLAS_EXPORT_H
#define ROCBLAS_EXPORT_H
#define ROCBLAS_EXPORT
#define ROCBLAS_NO_EXPORT
#define ROCBLAS_DEPRECATED __attribute__((deprecated))
#define ROCBLAS_DEPRECATED_EXPORT ROCBLAS_EXPORT ROCBLAS_DEPRECATED
#define ROCBLAS_DEPRECATED_NO_EXPORT ROCBLAS_NO_EXPORT ROCBLAS_DEPRECATED
#endif
""",
        StandardCharsets.UTF_8
      )
      Files.writeString(
        internal.resolve("rocblas-version.h"),
        """#ifndef ROCBLAS_VERSION_H
#define ROCBLAS_VERSION_H
#define ROCBLAS_VERSION_MAJOR 5
#define ROCBLAS_VERSION_MINOR 1
#define ROCBLAS_VERSION_PATCH 1
#define ROCBLAS_VERSION_TWEAK 0
#define ROCBLAS_TENSILE_COMMIT_ID "", ""
#endif
""",
        StandardCharsets.UTF_8
      )
      val _ = Files.writeString(marker, markerValue, StandardCharsets.UTF_8)
    }
  }
}
