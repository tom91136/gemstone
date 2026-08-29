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

/** The Khronos OpenCL CTS quick inventory, built once against the stock ICD loader and run unchanged in both lanes. */
final class OpenCLCTSConformanceSuite extends munit.FunSuite {
  override val munitTimeout: Duration = 30.minutes

  private final case class CTSCase(name: String, command: String)

  private val ctsVersion = version("opencl-cts")
  private val source     = prepareArchive(
    s"opencl-cts-$ctsVersion",
    s"https://github.com/KhronosGroup/OpenCL-CTS/archive/refs/tags/$ctsVersion.tar.gz",
    sha256("opencl-cts")
  )
  private val openCLHeaders = prepareArchive(
    s"opencl-headers-${version("opencl-headers")}",
    s"https://github.com/KhronosGroup/OpenCL-Headers/archive/refs/tags/v${version("opencl-headers")}.zip",
    sha256("opencl-headers")
  )
  private val spirvHeaders = prepareArchive(
    s"spirv-headers-${version("spirv-headers")}",
    s"https://github.com/KhronosGroup/SPIRV-Headers/archive/refs/tags/${version("spirv-headers")}.zip",
    sha256("spirv-headers")
  )
  private val buildRoot = target.resolve(s"opencl-cts-$ctsVersion/build")
  private val loader    = sys.env.get("GEMSTONE_OPENCL_LOADER").filter(_.nonEmpty).map(Path.of(_))
    .getOrElse(Path.of("/usr/lib64/libOpenCL.so"))

  require(Files.isRegularFile(loader), s"OpenCL loader is absent: $loader; set GEMSTONE_OPENCL_LOADER")
  configure(
    source,
    buildRoot,
    Vector(
      "-DCMAKE_BUILD_TYPE=Release",
      s"-DCL_INCLUDE_DIR=$openCLHeaders",
      s"-DCL_LIB_DIR=${loader.getParent}",
      s"-DOPENCL_LIBRARIES=$loader",
      s"-DSPIRV_INCLUDE_DIR=$spirvHeaders",
      "-DGL_IS_SUPPORTED=OFF",
      "-DGLES_IS_SUPPORTED=OFF",
      "-DVULKAN_IS_SUPPORTED=OFF"
    )
  )

  private val all      = parseQuickInventory(source.resolve("test_conformance/opencl_conformance_tests_quick.csv"))
  private val selected = select(all.zipWithIndex.map { case (entry, index) => index -> entry.name }, "OPENCL")
    .map { case (index, _) => all(index) }
  private val jobs = sys.env.get("GEMSTONE_OPENCL_JOBS")
    .fold(Math.min(8, Runtime.getRuntime.availableProcessors))(_.toInt)
  private var executor   = Option.empty[ExecutorService]
  private var executions = Map.empty[String, JavaFuture[Unit]]

  override def beforeAll(): Unit = {
    require(jobs >= 1 && jobs <= 32, s"GEMSTONE_OPENCL_JOBS must be between 1 and 32, found $jobs")
    runChecked(Vector("cmake", "--build", buildRoot.toString, "--parallel"), root)
    val pool = Executors.newFixedThreadPool(jobs)
    executor = Some(pool)
    executions = selected.map { entry =>
      val task: Callable[Unit] = () => runCase(entry)
      entry.name -> pool.submit(task)
    }.toMap
  }

  override def afterAll(): Unit = executor.foreach(_.shutdown())

  selected.foreach { entry =>
    test(s"opencl/${entry.name}") {
      await(entry.name)
    }
  }

  private def await(name: String): Unit =
    try executions.getOrElse(name, fail(s"parallel test was not scheduled: $name")).get()
    catch {
      case error: ExecutionException => throw error.getCause
    }

  private def runCase(entry: CTSCase): Unit = {
    val result = runPaired(
      ComputeAPI.OpenCL,
      RuntimeLayout(
        openCLICD = sys.env.get("GEMSTONE_OPENCL_ICD").filter(_.nonEmpty).map(Path.of(_))
      ),
      Vector("bash", "-c", entry.command),
      buildRoot.resolve("test_conformance"),
      oracleEnvironment = Map("CL_DEVICE_TYPE" -> "CL_DEVICE_TYPE_GPU"),
      gemstoneEnvironment = Map("CL_DEVICE_TYPE" -> "CL_DEVICE_TYPE_GPU")
    )
    require(result.oracle.exit == 0, s"oracle/${entry.name}:\n${result.oracle.diagnostic}")
    require(result.gemstone.exit == 0, s"gemstone/${entry.name}:\n${result.gemstone.diagnostic}")
  }

  private def parseQuickInventory(path: Path): Vector[CTSCase] = {
    val entries = Files.readAllLines(path, StandardCharsets.UTF_8).asScala.iterator.flatMap { raw =>
      val line = raw.trim
      if (line.isEmpty || line.startsWith("#")) None
      else
        line.split("\\s*,\\s*", 3) match {
          case Array(_, command) if command.startsWith("gl/")    => None
          case Array(name, command)                               => Some(CTSCase(name, command))
          case Array(device, name, command)
              if device == "CL_DEVICE_TYPE_GPU" && !command.startsWith("gl/") =>
            Some(CTSCase(name, command))
          case Array(device, _, _) if device.startsWith("CL_DEVICE_TYPE_") => None
          case fields => throw IllegalArgumentException(s"invalid OpenCL CTS quick entry: ${fields.mkString("|")}")
        }
    }.toVector
    require(entries.size == 57, s"expected 57 supported OpenCL CTS quick cases, found ${entries.size}")
    require(entries.map(_.name).distinct.size == entries.size, "OpenCL CTS quick case names are not unique")
    entries
  }
}
