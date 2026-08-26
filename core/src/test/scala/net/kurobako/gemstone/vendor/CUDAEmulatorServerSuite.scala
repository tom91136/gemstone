package net.kurobako.gemstone.vendor

import net.kurobako.gemstone.cuda.CudaResult
import net.kurobako.gemstone.cuda.DevicePtr
import net.kurobako.gemstone.cuda.HostInput
import net.kurobako.gemstone.cuda.SizeT
import net.kurobako.gemstone.generated.GeneratedCudaRpc
import net.kurobako.gemstone.rpc.RpcRequest
import net.kurobako.gemstone.rpc.RpcResponse
import net.kurobako.gemstone.rpc.RpcTransport

class CUDAEmulatorServerSuite extends munit.FunSuite {
  test("unimplemented Driver methods return CUDA_ERROR_NOT_SUPPORTED locally") {
    new CUDAEmulatorServer().cuLaunchKernel() match {
      case CudaResult.Failure(error) => assertEquals(error.code, 801)
      case null                      => fail("stub returned null")
    }
  }

  test("generated client and dispatcher round-trip allocation and bytes") {
    val server    = new CUDAEmulatorServer
    val transport = new RpcTransport {
      override val schemaFingerprint: String = GeneratedCudaRpc.Dispatcher.schemaFingerprint
      override def call(request: RpcRequest) = GeneratedCudaRpc.Dispatcher.dispatch(request, server)
    }
    val client = new GeneratedCudaRpc.Client(transport)

    val pointer = client.cudaMalloc(SizeT(4L)) match {
      case CudaResult.Success(value) => value
      case CudaResult.Failure(error) => fail(error.toString)
    }
    assert(pointer.address != DevicePtr.Null.address)
    assertEquals(
      client.cuMemcpyHtoD_v2(pointer, HostInput(IArray[Byte](10, 20, 30, 40))),
      CudaResult.Success(())
    )
    val output = client.cuMemcpyDtoH_v2(pointer, SizeT(4L)) match {
      case CudaResult.Success(value) => value
      case CudaResult.Failure(error) => fail(error.toString)
    }
    assertEquals(output.bytes.toSeq, Seq[Byte](10, 20, 30, 40))
    assertEquals(client.cudaFree(pointer), CudaResult.Success(()))
  }

  test("generated dispatcher rejects a correct ID paired with the wrong name") {
    val server       = new CUDAEmulatorServer
    val validRequest = new scala.collection.mutable.ArrayBuffer[RpcRequest]
    val capture      = new RpcTransport {
      override val schemaFingerprint: String = GeneratedCudaRpc.SchemaFingerprint
      override def call(request: RpcRequest) = {
        validRequest += request
        GeneratedCudaRpc.Dispatcher.dispatch(request, server)
      }
    }
    assertEquals(new GeneratedCudaRpc.Client(capture).cudaGetDeviceCount(), CudaResult.Success(1))
    val request = validRequest.head.copy(functionName = "cuDeviceGetCount")
    GeneratedCudaRpc.Dispatcher.dispatch(request, server) match {
      case RpcResponse.Failure(_) => ()
      case response               => fail(s"accepted mismatched identity: $response")
    }
  }

  test("generated client rejects a schema mismatch before making a call") {
    val transport = new RpcTransport {
      override val schemaFingerprint: String = "wrong"
      override def call(request: RpcRequest) = fail(s"unexpected call: $request")
    }
    intercept[IllegalArgumentException](new GeneratedCudaRpc.Client(transport))
  }
}
