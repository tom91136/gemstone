package net.kurobako.gemstone.server

import net.kurobako.gemstone.cuda.CUDA
import net.kurobako.gemstone.cuda.CUDAError
import net.kurobako.gemstone.generated.GeneratedCUDARPC
import net.kurobako.gemstone.rpc.RPCRequest
import net.kurobako.gemstone.rpc.RPCResponse
import net.kurobako.gemstone.vendor.CUDAEmulatorServer

/** Transport-independent CUDA request receiver shared by JVM and Scala Native server endpoints. */
final class CUDAServer(implementation: CUDA) {
  def dispatch(request: RPCRequest): RPCResponse =
    try GeneratedCUDARPC.Dispatcher.dispatch(request, implementation)
    catch {
      case error: Throwable => RPCResponse.Failure(CUDAError(999, s"server failure: ${error.getMessage}"))
    }
}

object CUDAServer {
  def emulated: CUDAServer = CUDAServer(CUDAEmulatorServer(PTXCUDAKernelExecution()))
}
