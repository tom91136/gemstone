package net.kurobako.gemstone.server

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.string.*

object API {
  def apply(value: String): Either[String, API] =
    values.find(_.id == value.toLowerCaseRoot).toRight(s"unsupported compute API: $value")

  val defaults: NonEmpty[Set[API]] = Set(HSA, CUDA, OpenCL, Vulkan, LevelZero).unsafe
}

enum API(val id: String, val lib: String) derives CanEqual {
  case CUDA      extends API("cuda", "libcuda.so.1")
  case HIP       extends API("hip", "libamdhip64.so.7")
  case HSA       extends API("hsa", "libhsa-runtime64.so.1")
  case OpenCL    extends API("opencl", "libOpenCL_gemstone.so.1")
  case Vulkan    extends API("vulkan", "vulkan/libvulkan.so.1")
  case LevelZero extends API("levelzero", "libze_gemstone.so.1")
}
