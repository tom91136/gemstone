package net.kurobako.gemstone.server

import net.kurobako.gemstone.string.*
import scala.util.Properties

object API {

  def apply(value: String): Either[String, API] = values
    .find(_.id == value.toLowerCaseRoot)
    .toRight(s"unsupported compute API: $value")

  def defaults: Set[API] =
    if (Properties.isLinux) Set(HSA, CUDA, OpenCL, LevelZero)
    else Set(HIP, CUDA, OpenCL, LevelZero)
}

enum API(val id: String, val unixLib: String, val windowsLib: String) {
  case CUDA      extends API("cuda", "libcuda.so.1", "cuda.dll")
  case HIP       extends API("hip", "libamdhip64.so.7", "amdhip64.dll")
  case HSA       extends API("hsa", "libhsa-runtime64.so.1", "hsa-runtime64.dll")
  case OpenCL    extends API("opencl", "libOpenCL_gemstone.so.1", "OpenCL_gemstone.dll")
  case Vulkan    extends API("vulkan", "vulkan/libvulkan.so.1", "vulkan/vulkan.dll")
  case LevelZero extends API("levelzero", "libze_gemstone.so.1", "ze_gemstone.dll")

  def lib: String = if (Properties.isWin) windowsLib else unixLib
}
