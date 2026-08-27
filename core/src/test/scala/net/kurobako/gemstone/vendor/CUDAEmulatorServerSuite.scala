package net.kurobako.gemstone.vendor

import net.kurobako.gemstone.cuda.CUDAResult
import net.kurobako.gemstone.cuda.DevicePtr
import net.kurobako.gemstone.cuda.HostInput
import net.kurobako.gemstone.cuda.ModuleImage
import net.kurobako.gemstone.cuda.SizeT
import net.kurobako.gemstone.generated.GeneratedCUDARPC
import net.kurobako.gemstone.rpc.RPCRequest
import net.kurobako.gemstone.rpc.RPCResponse
import net.kurobako.gemstone.rpc.RPCTransport

class CUDAEmulatorServerSuite extends munit.FunSuite {
  test("unimplemented Driver methods return CUDA_ERROR_NOT_SUPPORTED locally") {
    new CUDAEmulatorServer().cuModuleLoadData(ModuleImage(IArray.empty[Byte])) match {
      case CUDAResult.Failure(error) => assertEquals(error.code, 801)
      case CUDAResult.Success(value) => fail(s"unsupported backend loaded module $value")
    }
  }

  test("generated client and dispatcher round-trip allocation and bytes") {
    val server    = new CUDAEmulatorServer
    val transport = new RPCTransport {
      override val schemaFingerprint: String = GeneratedCUDARPC.Dispatcher.schemaFingerprint
      override def call(request: RPCRequest) = GeneratedCUDARPC.Dispatcher.dispatch(request, server)
    }
    val client = new GeneratedCUDARPC.Client(transport)

    val pointer = client.cudaMalloc(SizeT(4L)) match {
      case CUDAResult.Success(value) => value
      case CUDAResult.Failure(error) => fail(error.toString)
    }
    assert(pointer.address != DevicePtr.Null.address)
    assertEquals(
      client.cuMemcpyHtoD_v2(pointer, HostInput(IArray[Byte](10, 20, 30, 40))),
      CUDAResult.Success(())
    )
    val output = client.cuMemcpyDtoH_v2(pointer, SizeT(4L)) match {
      case CUDAResult.Success(value) => value
      case CUDAResult.Failure(error) => fail(error.toString)
    }
    assertEquals(output.bytes.toSeq, Seq[Byte](10, 20, 30, 40))
    assertEquals(client.cudaFree(pointer), CUDAResult.Success(()))
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
    assertEquals(new GeneratedCUDARPC.Client(capture).cudaGetDeviceCount(), CUDAResult.Success(1))
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
