package net.kurobako.gemstone.launcher

import java.nio.file.Path

enum ComputeAPI(val id: String) {
  case CUDA      extends ComputeAPI("cuda")
  case HIP       extends ComputeAPI("hip")
  case HSA       extends ComputeAPI("hsa")
  case OpenCL    extends ComputeAPI("opencl")
  case LevelZero extends ComputeAPI("levelzero")
  case SYCL      extends ComputeAPI("sycl")
}

object ComputeAPI {
  def parse(value: String): Either[LaunchError, ComputeAPI] =
    values.find(_.id == value.toLowerCase).toRight(LaunchError(s"unsupported compute API: $value"))
}

final case class Endpoint(port: Int, token: String) {
  require(port >= 1 && port <= 65535, s"invalid RPC port: $port")
  require(
    token.length == 64 && token.forall(character => character.isDigit || (character >= 'a' && character <= 'f')),
    "RPC token must contain exactly 64 lowercase hexadecimal characters"
  )
}

final case class RuntimeLayout(
    cudaDriver: Option[Path] = None,
    hipRuntime: Option[Path] = None,
    hsaRuntime: Option[Path] = None,
    openCLICD: Option[Path] = None,
    levelZeroDriver: Option[Path] = None
)

final case class Environment(set: Map[String, String], unset: Set[String]) {
  def applyTo(target: java.util.Map[String, String]): Unit = {
    unset.foreach(target.remove)
    set.foreach { case (name, value) => target.put(name, value) }
  }
}

final case class UsageSnapshot(cudaRequests: Long, executionRequests: Long) {
  def +(other: UsageSnapshot): UsageSnapshot =
    UsageSnapshot(cudaRequests + other.cudaRequests, executionRequests + other.executionRequests)
}

enum RequiredEvidence {
  case CUDA, Execution

  def satisfied(before: UsageSnapshot, after: UsageSnapshot): Boolean = this match {
    case CUDA      => after.cudaRequests > before.cudaRequests
    case Execution => after.executionRequests > before.executionRequests
  }
}

final case class LaunchPlan(
    api: ComputeAPI,
    environment: Environment,
    requiredEvidence: RequiredEvidence
)

final case class LaunchError(message: String)

object Launch {
  private val RemovedVendorOverrides = Set(
    "HSA_ENABLE_DTIF",
    "HSA_TOOLS_LIB",
    "OCL_ICD_VENDORS",
    "OCL_ICD_FILENAMES",
    "ONEAPI_DEVICE_SELECTOR",
    "SYCL_DEVICE_FILTER",
    "ZE_ENABLE_ALT_DRIVERS"
  )

  def plan(
      api: ComputeAPI,
      layout: RuntimeLayout,
      endpoint: Endpoint,
      inherited: Map[String, String]
  ): Either[LaunchError, LaunchPlan] = {
    val common = Map(
      "GEMSTONE_RPC_PORT"  -> endpoint.port.toString,
      "GEMSTONE_RPC_TOKEN" -> endpoint.token,
      "GEMSTONE_REQUIRED"  -> "1"
    ) ++ inherited.get("GEMSTONE_RECORD").filter(_.nonEmpty).map("GEMSTONE_RECORD" -> _)
    val base = Environment(common, RemovedVendorOverrides)
    api match {
      case ComputeAPI.CUDA =>
        preload("CUDA Driver", api, layout.cudaDriver, base, inherited, RequiredEvidence.CUDA)
      case ComputeAPI.HIP =>
        preload("HIP Runtime", api, layout.hipRuntime, base, inherited, RequiredEvidence.Execution)
      case ComputeAPI.HSA =>
        preload("HSA Runtime", api, layout.hsaRuntime, base, inherited, RequiredEvidence.Execution)
      case ComputeAPI.OpenCL =>
        openCL(api, layout, base)
      case ComputeAPI.LevelZero =>
        levelZero(api, layout, base)
      case ComputeAPI.SYCL =>
        (layout.levelZeroDriver, layout.openCLICD) match {
          case (Some(_), None)    => levelZero(api, layout, base).map(withSYCLSelector(_, "level_zero:gpu"))
          case (None, Some(_))    => openCL(api, layout, base).map(withSYCLSelector(_, "opencl:gpu"))
          case (Some(_), Some(_)) => Left(LaunchError("SYCL requires exactly one Gemstone backend provider"))
          case (None, None)       => Left(LaunchError("SYCL backend provider is absent"))
        }
    }
  }

  private def levelZero(
      api: ComputeAPI,
      layout: RuntimeLayout,
      base: Environment
  ): Either[LaunchError, LaunchPlan] =
    provider("Level Zero Driver", layout.levelZeroDriver).map { library =>
      LaunchPlan(
        api,
        base.copy(set =
          base.set ++ Map(
            "ZE_ENABLE_ALT_DRIVERS"        -> library.toString,
            "ZE_ENABLE_LOADER_DEBUG_TRACE" -> "0"
          )
        ),
        RequiredEvidence.Execution
      )
    }

  private def openCL(
      api: ComputeAPI,
      layout: RuntimeLayout,
      base: Environment
  ): Either[LaunchError, LaunchPlan] =
    provider("OpenCL ICD", layout.openCLICD).map { directory =>
      LaunchPlan(
        api,
        base.copy(set = base.set.updated("OCL_ICD_VENDORS", directory.toString)),
        RequiredEvidence.Execution
      )
    }

  private def withSYCLSelector(plan: LaunchPlan, selector: String): LaunchPlan =
    plan.copy(environment =
      plan.environment.copy(set =
        plan.environment.set.updated(
          "ONEAPI_DEVICE_SELECTOR",
          selector
        )
      )
    )

  private def preload(
      name: String,
      api: ComputeAPI,
      path: Option[Path],
      base: Environment,
      inherited: Map[String, String],
      evidence: RequiredEvidence
  ): Either[LaunchError, LaunchPlan] =
    provider(name, path).map { library =>
      val directory   = Option(library.getParent).getOrElse(Path.of("."))
      val libraryPath = inherited.get("LD_LIBRARY_PATH").filter(_.nonEmpty)
        .fold(directory.toString)(value => s"$directory:$value")
      LaunchPlan(
        api,
        base.copy(set =
          base.set ++ Map(
            "LD_PRELOAD"      -> library.toString,
            "LD_LIBRARY_PATH" -> libraryPath
          )
        ),
        evidence
      )
    }

  private def provider(name: String, path: Option[Path]): Either[LaunchError, Path] =
    path.toRight(LaunchError(s"$name provider is absent"))
}
