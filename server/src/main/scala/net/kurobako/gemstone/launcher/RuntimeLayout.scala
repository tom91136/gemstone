package net.kurobako.gemstone.launcher

import java.nio.file.Path
import scala.util.Properties

final case class RuntimeLayout(directory: Path) {
  def absolute: RuntimeLayout        = copy(directory = directory.toAbsolutePath.normalize())
  def library(api: ComputeAPI): Path = directory.resolve(
    if (Properties.isWin) api match {
      case ComputeAPI.CUDA      => "cuda.dll"
      case ComputeAPI.HIP       => "amdhip64.dll"
      case ComputeAPI.HSA       => "hsa-runtime64.dll"
      case ComputeAPI.OpenCL    => "OpenCL_gemstone.dll"
      case ComputeAPI.LevelZero => "ze_gemstone.dll"
    }
    else api match {
      case ComputeAPI.CUDA      => "libcuda.so.1"
      case ComputeAPI.HIP       => "libamdhip64.so.7"
      case ComputeAPI.HSA       => "libhsa-runtime64.so.1"
      case ComputeAPI.OpenCL    => "libOpenCL_gemstone.so.1"
      case ComputeAPI.LevelZero => "libze_gemstone.so.1"
    }
  )
}
