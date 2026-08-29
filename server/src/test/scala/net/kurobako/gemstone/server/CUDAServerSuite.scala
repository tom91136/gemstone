package net.kurobako.gemstone.server

import net.kurobako.gemstone.abi.One
import net.kurobako.gemstone.abi.Pointer
import net.kurobako.gemstone.cuda.CUDAStatus
import net.kurobako.gemstone.generated.GeneratedCUDARPC
import net.kurobako.gemstone.rpc.RPCRequest
import net.kurobako.gemstone.rpc.RPCTransport

class CUDAServerSuite extends munit.FunSuite {
  private final class TestTransport(invocation: RPCRequest => net.kurobako.gemstone.rpc.RPCResponse)
      extends RPCTransport {
    override val schemaFingerprint: String = GeneratedCUDARPC.SchemaFingerprint
    override def call(request: RPCRequest) = invocation(request)
  }

  test("transport-independent server dispatches generated CUDA requests") {
    val server    = CUDAServer.emulated
    val transport = TestTransport(server.dispatch)
    val count     = Pointer.output[Int, One](1)
    assertEquals(GeneratedCUDARPC.Client(transport).cuDeviceGetCount(count), CUDAStatus.Success)
    assertEquals(count.snapshot.flatMap(_.headOption), Some(1))
  }
}
