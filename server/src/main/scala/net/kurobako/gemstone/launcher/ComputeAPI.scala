package net.kurobako.gemstone.launcher

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
