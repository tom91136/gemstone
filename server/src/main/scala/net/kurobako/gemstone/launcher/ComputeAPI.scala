package net.kurobako.gemstone.launcher

import java.util.Locale
import scala.util.Properties

enum ComputeAPI(val id: String, val unixLib: String, val windowsLib: String) {
  case CUDA      extends ComputeAPI("cuda", "libcuda.so.1", "cuda.dll")
  case HIP       extends ComputeAPI("hip", "libamdhip64.so.7", "amdhip64.dll")
  case HSA       extends ComputeAPI("hsa", "libhsa-runtime64.so.1", "hsa-runtime64.dll")
  case OpenCL    extends ComputeAPI("opencl", "libOpenCL_gemstone.so.1", "OpenCL_gemstone.dll")
  case Vulkan    extends ComputeAPI("vulkan", "vulkan/libvulkan.so.1", "vulkan/vulkan.dll")
  case LevelZero extends ComputeAPI("levelzero", "libze_gemstone.so.1", "ze_gemstone.dll")

  def lib: String = if (Properties.isWin) windowsLib else unixLib
}

object ComputeAPI {

  def apply(value: String): Either[String, ComputeAPI] = values
    .find(_.id == value.toLowerCase(Locale.ROOT))
    .toRight(s"unsupported compute API: $value")

  def defaults: Set[ComputeAPI] =
    if (Properties.isLinux) Set(HSA, CUDA, OpenCL, LevelZero)
    else Set(HIP, CUDA, OpenCL, LevelZero)
}
