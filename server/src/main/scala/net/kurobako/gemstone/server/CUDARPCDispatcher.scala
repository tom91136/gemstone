package net.kurobako.gemstone.server

import net.kurobako.gemstone.cuda.CUDAError
import net.kurobako.gemstone.generated.GeneratedCUDARPC
import net.kurobako.gemstone.rpc.RPCRequest
import net.kurobako.gemstone.rpc.RPCResponse
import net.kurobako.gemstone.vendor.CUDAEmulator

/** Transport-independent CUDA request receiver shared by JVM and Scala Native server endpoints. */
final class CUDARPCDispatcher private (implementation: CUDAEmulator) {
  def dispatch(request: RPCRequest): RPCResponse =
    try GeneratedCUDARPC.Dispatcher.dispatch(request, implementation)
    catch {
      case scala.util.control.NonFatal(error) =>
        RPCResponse.Failure(CUDAError(999, s"server failure: ${error.getMessage}"))
    }

  def dispatch(request: RPCRequest, context: Long): RPCResponse =
    implementation.withContext(context)(dispatch(request))
}

object CUDARPCDispatcher {
  def emulated: CUDARPCDispatcher = emulated(() => net.kurobako.gemstone.execution.ExecutionControl.default)

  def emulated(control: () => net.kurobako.gemstone.execution.ExecutionControl): CUDARPCDispatcher =
    CUDARPCDispatcher(CUDAEmulator(PTXCUDAKernelExecution(control)))
}
