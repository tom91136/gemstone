package net.kurobako.gemstone.vendor

import net.kurobako.gemstone.Col

import net.kurobako.gemstone.abi.CountedBy
import net.kurobako.gemstone.abi.One
import net.kurobako.gemstone.abi.Pointer
import net.kurobako.gemstone.cuda.CUDAModule
import net.kurobako.gemstone.cuda.CUDAStatus
import net.kurobako.gemstone.cuda.DevicePtr
import net.kurobako.gemstone.cuda.ModuleImage
import net.kurobako.gemstone.cuda.SizeT
import net.kurobako.gemstone.generated.GeneratedCUDARPC
import net.kurobako.gemstone.rpc.RPCRequest
import net.kurobako.gemstone.rpc.RPCResponse
import net.kurobako.gemstone.rpc.RPCTransport

class CUDAEmulatorServerSuite extends munit.FunSuite {
  test("unimplemented Driver methods return CUDA_ERROR_NOT_SUPPORTED locally") {
    val module = Pointer.output[CUDAModule, One](1)
    assertEquals(
      new CUDAEmulatorServer().cuModuleLoadData(module, ModuleImage(Col.empty[Byte])),
      CUDAStatus.NotSupported
    )
    assertEquals(module.snapshot, None)
  }

  test("generated client and dispatcher round-trip allocation and bytes") {
    val implementation = new CUDAEmulatorServer
    val transport      = new RPCTransport {
      override val schemaFingerprint: String = GeneratedCUDARPC.Dispatcher.schemaFingerprint
      override def call(request: RPCRequest) = GeneratedCUDARPC.Dispatcher.dispatch(request, implementation)
    }
    val client = new GeneratedCUDARPC.Client(transport)

    val allocated = Pointer.output[DevicePtr, One](1)
    assertEquals(client.cudaMalloc(allocated, SizeT(4L)), CUDAStatus.Success)
    val pointer = allocated.snapshot.flatMap(_.headOption).getOrElse(fail("cudaMalloc did not initialize its output"))
    assert(pointer.address != DevicePtr.Null.address, "cudaMalloc returned the null pointer")
    val input = Pointer.input[Byte, CountedBy["ByteCount"]](Col[Byte](10, 20, 30, 40))
    assertEquals(
      client.cuMemcpyHtoD_v2(pointer, input, SizeT(4L)),
      CUDAStatus.Success
    )
    val output = Pointer.output[Byte, CountedBy["ByteCount"]](4)
    assertEquals(client.cuMemcpyDtoH_v2(output, pointer, SizeT(4L)), CUDAStatus.Success)
    assertEquals(output.snapshot.map(_.toSeq).getOrElse(Seq.empty), Seq[Byte](10, 20, 30, 40))
    assertEquals(client.cudaFree(pointer), CUDAStatus.Success)
  }

  test("generated dispatcher rejects a correct ID paired with the wrong name") {
    val server       = new CUDAEmulatorServer
    val validRequest = new scala.collection.mutable.ArrayBuffer[RPCRequest]
    val capture      = new RPCTransport {
      override val schemaFingerprint: String = GeneratedCUDARPC.SchemaFingerprint
      override def call(request: RPCRequest) = {
        validRequest += request
        GeneratedCUDARPC.Dispatcher.dispatch(request, server)
      }
    }
    val count = Pointer.output[Int, One](1)
    assertEquals(new GeneratedCUDARPC.Client(capture).cudaGetDeviceCount(count), CUDAStatus.Success)
    assertEquals(count.snapshot.flatMap(_.headOption), Some(1))
    val request = validRequest.head.copy(functionName = "cuDeviceGetCount")
    GeneratedCUDARPC.Dispatcher.dispatch(request, server) match {
      case RPCResponse.Failure(_) => ()
      case response               => fail(s"accepted mismatched identity: $response")
    }
  }

  test("generated client rejects a schema mismatch before making a call") {
    val transport = new RPCTransport {
      override val schemaFingerprint: String = "wrong"
      override def call(request: RPCRequest) = fail(s"unexpected call: $request")
    }
    intercept[IllegalArgumentException](new GeneratedCUDARPC.Client(transport))
  }
}
