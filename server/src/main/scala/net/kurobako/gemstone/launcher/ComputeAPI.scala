package net.kurobako.gemstone.launcher

import java.util.Locale
import scala.util.Properties

enum ComputeAPI(val id: String) {
  case CUDA      extends ComputeAPI("cuda")
  case HIP       extends ComputeAPI("hip")
  case HSA       extends ComputeAPI("hsa")
  case OpenCL    extends ComputeAPI("opencl")
  case LevelZero extends ComputeAPI("levelzero")
}

object ComputeAPI {

  def apply(value: String): Either[String, ComputeAPI] = values
    .find(_.id == value.toLowerCase(Locale.ROOT))
    .toRight(s"unsupported compute API: $value")

  def defaults: Set[ComputeAPI] =
    if (Properties.isLinux) Set(HSA, CUDA, OpenCL, LevelZero)
    else Set(HIP, CUDA, OpenCL, LevelZero)
}
