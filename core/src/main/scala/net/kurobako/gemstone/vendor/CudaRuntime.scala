package net.kurobako.gemstone.vendor

import net.kurobako.gemstone.generated.GeneratedApi
import net.kurobako.gemstone.runtime.RuntimeState

enum CudaError(val value: Int) {
  case Success             extends CudaError(0)
  case InvalidValue        extends CudaError(1)
  case InitializationError extends CudaError(3)
  case Unknown             extends CudaError(999)
}

final case class ApiResult[+A](value: Option[A], error: CudaError)

final class CudaRuntime(runtime: RuntimeState) extends GeneratedApi.Handler {
  def getDeviceCount: ApiResult[Int] = ApiResult(Some(runtime.deviceCount), CudaError.Success)

  override def cudaGetDeviceCount(): GeneratedApi.DispatchResult =
    GeneratedApi.DispatchResult(CudaError.Success.value, runtime.deviceCount.toLong)

  override def gemsiAddI32(left: Int, right: Int): GeneratedApi.DispatchResult =
    GeneratedApi.DispatchResult(CudaError.Success.value, (left + right).toLong)
}

object CudaRuntime {
  val emulated: CudaRuntime = new CudaRuntime(RuntimeState.singleEmulatedDevice)
}
