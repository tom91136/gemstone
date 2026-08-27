package net.kurobako.gemstone.vendor

import net.kurobako.gemstone.generated.GeneratedAPI
import net.kurobako.gemstone.runtime.RuntimeState

enum CUDAError(val value: Int) {
  case Success             extends CUDAError(0)
  case InvalidValue        extends CUDAError(1)
  case InitializationError extends CUDAError(3)
  case Unknown             extends CUDAError(999)
}

final class CUDARuntime(runtime: RuntimeState) extends GeneratedAPI.Handler {
  override def cudaGetDeviceCount(): GeneratedAPI.DispatchResult =
    GeneratedAPI.DispatchResult(CUDAError.Success.value, runtime.deviceCount.toLong)

  override def gemstoneAddI32(left: Int, right: Int): GeneratedAPI.DispatchResult =
    GeneratedAPI.DispatchResult(CUDAError.Success.value, (left + right).toLong)
}

object CUDARuntime {
  val emulated: CUDARuntime = new CUDARuntime(RuntimeState.singleEmulatedDevice)
}
