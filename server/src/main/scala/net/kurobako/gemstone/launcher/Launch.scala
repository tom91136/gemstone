package net.kurobako.gemstone.launcher

import net.kurobako.gemstone.server.transport.{ClientId, RPCPort}

import java.nio.file.Path
import java.util.Locale

private[gemstone] enum ComputeAPI(val id: String) {
  case CUDA      extends ComputeAPI("cuda")
  case HIP       extends ComputeAPI("hip")
  case HSA       extends ComputeAPI("hsa")
  case OpenCL    extends ComputeAPI("opencl")
  case LevelZero extends ComputeAPI("levelzero")
}

private[gemstone] object ComputeAPI {
  def parse(value: String): Either[LaunchError, ComputeAPI] =
    values.find(_.id == value.toLowerCase(Locale.ROOT))
      .toRight(LaunchError(s"unsupported compute API: $value"))

  def parseAll(value: String): Either[LaunchError, Set[ComputeAPI]] = {
    val names = value.split(",", -1).toVector
    if (names.exists(_.isEmpty)) Left(LaunchError("--apis must be a comma-separated non-empty API list"))
    else names.foldLeft[Either[LaunchError, Set[ComputeAPI]]](Right(Set.empty)) { (result, name) =>
      for {
        parsed <- result
        api    <- parse(name)
      } yield parsed + api
    }
  }

  def defaults(platform: RuntimePlatform): Set[ComputeAPI] = platform match {
    case RuntimePlatform.Linux => Set(HSA, CUDA, OpenCL, LevelZero)
    case _                     => Set(HIP, CUDA, OpenCL, LevelZero)
  }
}

private[gemstone] final case class Endpoint(port: RPCPort, client: ClientId)

private[gemstone] final case class RuntimeLayout(
    directory: Path,
    platform: RuntimePlatform = RuntimePlatform.current
) {
  private[launcher] def absolute: RuntimeLayout = copy(directory = directory.toAbsolutePath.normalize())
  def library(api: ComputeAPI): Path            = directory.resolve(platform match {
    case RuntimePlatform.Windows => api match {
        case ComputeAPI.CUDA      => "cuda.dll"
        case ComputeAPI.HIP       => "amdhip64.dll"
        case ComputeAPI.HSA       => "hsa-runtime64.dll"
        case ComputeAPI.OpenCL    => "OpenCL_gemstone.dll"
        case ComputeAPI.LevelZero => "ze_gemstone.dll"
      }
    case _ => api match {
        case ComputeAPI.CUDA      => "libcuda.so.1"
        case ComputeAPI.HIP       => "libamdhip64.so.7"
        case ComputeAPI.HSA       => "libhsa-runtime64.so.1"
        case ComputeAPI.OpenCL    => "libOpenCL_gemstone.so.1"
        case ComputeAPI.LevelZero => "libze_gemstone.so.1"
      }
  })
}

private[gemstone] final case class Environment(set: Map[String, String], unset: Set[String]) {
  def applyTo(target: java.util.Map[String, String]): Unit = {
    unset.foreach(target.remove(_))
    set.foreach { case (name, value) => target.put(name, value) }
  }
}

private[gemstone] final case class LaunchPlan(
    apis: Set[ComputeAPI],
    environment: Environment
)

private[gemstone] final case class LaunchError(message: String)

private[gemstone] object Launch {
  private val RemovedOverrides = Set(
    "CONFORMANCE_DEVICE",
    "GEMSTONE_RECORD",
    "HSA_ENABLE_DTIF",
    "HSA_TOOLS_LIB",
    "OCL_ICD_VENDORS",
    "OCL_ICD_FILENAMES",
    "ONEAPI_DEVICE_SELECTOR",
    "SYCL_DEVICE_FILTER",
    "ZE_ENABLE_ALT_DRIVERS"
  )

  def plan(
      apis: Set[ComputeAPI],
      layout: RuntimeLayout,
      endpoint: Endpoint,
      inherited: Map[String, String]
  ): Either[LaunchError, LaunchPlan] =
    if (apis.isEmpty) Left(LaunchError("at least one compute API must be enabled"))
    else {
      val runtime = layout.absolute
      val preload = ComputeAPI.values.iterator.filter(apis).collect {
        case api @ (ComputeAPI.CUDA | ComputeAPI.HIP | ComputeAPI.HSA | ComputeAPI.OpenCL) => runtime.library(api)
      }.toVector
      val directories          = preload.flatMap(path => Option(path.getParent)).distinct
      val inheritedPreload     = inherited.get("LD_PRELOAD").filter(_.nonEmpty).toVector
      val inheritedLibraryPath = inherited.get("LD_LIBRARY_PATH").filter(_.nonEmpty).toVector
      val common               = Map(
        "GEMSTONE_RPC_PORT"   -> s"${endpoint.port.value}",
        "GEMSTONE_RPC_CLIENT" -> s"${endpoint.client.value}"
      )
      val dynamic = Map.newBuilder[String, String]
      val conformanceTarget = for {
        backend      <- inherited.get("CONFORMANCE_TARGET_BACKEND")
        architecture <- inherited.get("CONFORMANCE_TARGET_ARCHITECTURE").filter(_.nonEmpty)
      } yield backend -> architecture
      conformanceTarget.foreach {
        case ("cuda", architecture)
            if apis(ComputeAPI.CUDA) && !inherited.contains("GEMSTONE_CUDA_TARGET") &&
              !inherited.contains("GEMSTONE_CUDA_TARGET_ARCH") =>
          dynamic += "GEMSTONE_CUDA_TARGET" -> architecture
        case ("hip" | "hsa", architecture)
            if (apis(ComputeAPI.HIP) || apis(ComputeAPI.HSA)) && !inherited.contains("GEMSTONE_AMDGPU_TARGET") =>
          dynamic += "GEMSTONE_AMDGPU_TARGET" -> architecture
        case _ =>
      }
      if (preload.nonEmpty) dynamic += "LD_PRELOAD" -> (preload.map(path => s"$path") ++ inheritedPreload).mkString(":")
      if (directories.nonEmpty)
        dynamic += "LD_LIBRARY_PATH" -> (directories.map(path => s"$path") ++ inheritedLibraryPath).mkString(":")
      if (apis(ComputeAPI.OpenCL))
        dynamic += "OCL_ICD_FILENAMES" -> s"${runtime.library(ComputeAPI.OpenCL)}"
      if (apis(ComputeAPI.LevelZero)) {
        dynamic += "ZE_ENABLE_ALT_DRIVERS"        -> s"${runtime.library(ComputeAPI.LevelZero)}"
        dynamic += "ZE_ENABLE_LOADER_DEBUG_TRACE" -> "0"
      }
      Right(LaunchPlan(apis, Environment(common ++ dynamic.result(), RemovedOverrides)))
    }
}
