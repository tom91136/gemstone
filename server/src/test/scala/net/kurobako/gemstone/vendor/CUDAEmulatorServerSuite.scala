package net.kurobako.gemstone.vendor

import net.kurobako.gemstone.Col

import net.kurobako.gemstone.abi.CountedBy
import net.kurobako.gemstone.abi.One
import net.kurobako.gemstone.abi.Pointer
import net.kurobako.gemstone.cuda.CUDAModule
import net.kurobako.gemstone.cuda.CUDAStatus
import net.kurobako.gemstone.cuda.DevicePtr
import net.kurobako.gemstone.cuda.FunctionName
import net.kurobako.gemstone.cuda.ModuleImage
import net.kurobako.gemstone.cuda.SizeT
import net.kurobako.gemstone.generated.GeneratedCUDARPC
import net.kurobako.gemstone.rpc.RPCRequest
import net.kurobako.gemstone.rpc.RPCResponse
import net.kurobako.gemstone.rpc.RPCTransport
import net.kurobako.gemstone.server.PTXCUDAKernelExecution

import java.nio.charset.StandardCharsets

class CUDAEmulatorServerSuite extends munit.FunSuite {
  private final class TestTransport(
      override val schemaFingerprint: String,
      invocation: RPCRequest => RPCResponse
  ) extends RPCTransport {
    override def call(request: RPCRequest): RPCResponse = invocation(request)
  }

  test("unimplemented Driver methods return CUDA_ERROR_NOT_SUPPORTED locally") {
    val module = Pointer.output[CUDAModule, One](1)
    assertEquals(
      CUDAEmulatorServer().cuModuleLoadData(module, ModuleImage(Col.empty[Byte])),
      CUDAStatus.NotSupported
    )
    assertEquals(module.snapshot, None)
  }

  test("generated client and dispatcher round-trip allocation and bytes") {
    val implementation = CUDAEmulatorServer()
    val transport      = TestTransport(
      GeneratedCUDARPC.Dispatcher.schemaFingerprint,
      GeneratedCUDARPC.Dispatcher.dispatch(_, implementation)
    )
    val client = GeneratedCUDARPC.Client(transport)

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
    val server       = CUDAEmulatorServer()
    val validRequest = scala.collection.mutable.ArrayBuffer[RPCRequest]()
    val capture      = TestTransport(
      GeneratedCUDARPC.SchemaFingerprint,
      request => {
        validRequest += request
        GeneratedCUDARPC.Dispatcher.dispatch(request, server)
      }
    )
    val count = Pointer.output[Int, One](1)
    assertEquals(GeneratedCUDARPC.Client(capture).cudaGetDeviceCount(count), CUDAStatus.Success)
    assertEquals(count.snapshot.flatMap(_.headOption), Some(1))
    val request = validRequest.head.copy(functionName = "cuDeviceGetCount")
    GeneratedCUDARPC.Dispatcher.dispatch(request, server) match {
      case RPCResponse.Failure(_) => ()
      case response               => fail(s"accepted mismatched identity: $response")
    }
  }

  test("generated client rejects a schema mismatch before making a call") {
    val transport = TestTransport("wrong", request => fail(s"unexpected call: $request"))
    intercept[IllegalArgumentException](GeneratedCUDARPC.Client(transport))
  }

  test("PTX module globals preserve size, initializer bytes, and module ownership") {
    val source =
      """|.version 8.0
         |.target sm_80
         |.address_size 64
         |.visible .global .align 4 .u32 values[2] = {
         |  0x11223344, 0xaabbccdd
         |};
         |.const .align 4 .u32 constant = 0x55667788;
         |""".stripMargin
    val server    = CUDAEmulatorServer(PTXCUDAKernelExecution())
    val moduleOut = Pointer.output[CUDAModule, One](1)
    assertEquals(
      server.cuModuleLoadData(moduleOut, ModuleImage(Col.from(source.getBytes(StandardCharsets.UTF_8)))),
      CUDAStatus.Success
    )
    val module     = moduleOut.snapshot.flatMap(_.headOption).getOrElse(fail("module handle was not initialized"))
    val pointerOut = Pointer.output[DevicePtr, One](1)
    val sizeOut    = Pointer.output[SizeT, One](1)
    assertEquals(
      server.cuModuleGetGlobal_v2(pointerOut, sizeOut, module, FunctionName("values")),
      CUDAStatus.Success
    )
    assertEquals(sizeOut.snapshot.flatMap(_.headOption).map(_.toLong), Some(8L))
    val pointer = pointerOut.snapshot.flatMap(_.headOption).getOrElse(fail("global pointer was not initialized"))
    val bytes   = Pointer.output[Byte, CountedBy["ByteCount"]](8)
    assertEquals(server.cuMemcpyDtoH_v2(bytes, pointer, SizeT(8L)), CUDAStatus.Success)
    assertEquals(
      bytes.snapshot.map(_.toSeq).getOrElse(Seq.empty),
      Seq[Byte](0x44, 0x33, 0x22, 0x11, 0xdd.toByte, 0xcc.toByte, 0xbb.toByte, 0xaa.toByte)
    )
    assertEquals(
      server.cuModuleGetGlobal_v2(pointerOut, sizeOut, module, FunctionName("constant")),
      CUDAStatus.Success
    )
    assertEquals(sizeOut.snapshot.flatMap(_.headOption).map(_.toLong), Some(4L))
    assertEquals(
      server.__cudaGemstoneRegisterGlobal(
        module,
        FunctionName("registered"),
        ModuleImage(Col[Byte](1, 2, 3))
      ),
      CUDAStatus.Success
    )
    assertEquals(
      server.cuModuleGetGlobal_v2(pointerOut, sizeOut, module, FunctionName("registered")),
      CUDAStatus.Success
    )
    assertEquals(sizeOut.snapshot.flatMap(_.headOption).map(_.toLong), Some(3L))
    assertEquals(
      server.cuModuleGetGlobal_v2(pointerOut, sizeOut, module, FunctionName("missing")),
      CUDAStatus(500)
    )
    assertEquals(server.cuModuleUnload(module), CUDAStatus.Success)
    assertEquals(server.cuMemcpyDtoH_v2(bytes, pointer, SizeT(8L)), CUDAStatus.InvalidValue)
  }
}
