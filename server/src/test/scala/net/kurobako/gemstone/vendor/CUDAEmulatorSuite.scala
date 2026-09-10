package net.kurobako.gemstone.vendor

import net.kurobako.gemstone.Col

import net.kurobako.gemstone.abi.CountedBy
import net.kurobako.gemstone.abi.One
import net.kurobako.gemstone.abi.Pointer
import net.kurobako.gemstone.cuda.Context
import net.kurobako.gemstone.cuda.Device
import net.kurobako.gemstone.cuda.UInt32
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
import java.nio.ByteBuffer

class CUDAEmulatorSuite extends munit.FunSuite {
  private final class TestTransport(
      override val schemaFingerprint: String,
      invocation: RPCRequest => RPCResponse
  ) extends RPCTransport {
    override def call(request: RPCRequest): RPCResponse = invocation(request)
  }

  test("independent context handles survive another context being destroyed") {
    val implementation    = CUDAEmulator()
    def create(): Context = {
      val output = Pointer.output[Context, One](1)
      assertEquals(implementation.cuCtxCreate_v2(output, UInt32(0), Device(0)), CUDAStatus.Success)
      output.snapshot.flatMap(_.headOption).getOrElse(fail("missing context"))
    }
    val first  = create()
    val second = create()
    assert(first != second)
    assertEquals(implementation.cuCtxDestroy_v2(second), CUDAStatus.Success)
    assertEquals(implementation.cuCtxDestroy_v2(second), CUDAStatus.InvalidContext)
    assertEquals(implementation.cuCtxDestroy_v2(first), CUDAStatus.Success)
    val third = create()
    assert(third != first && third != second)
    assertEquals(implementation.cuCtxDestroy_v2(first), CUDAStatus.InvalidContext)
    assertEquals(implementation.cuCtxDestroy_v2(third), CUDAStatus.Success)
  }

  test("destroying a context invalidates only resources created while it was current") {
    val implementation    = CUDAEmulator()
    def create(): Context = {
      val output = Pointer.output[Context, One](1)
      assertEquals(implementation.cuCtxCreate_v2(output, UInt32(0), Device(0)), CUDAStatus.Success)
      output.snapshot.flatMap(_.headOption).getOrElse(fail("missing context"))
    }
    def allocate(): DevicePtr = {
      val output = Pointer.output[DevicePtr, One](1)
      assertEquals(implementation.cuMemAlloc_v2(output, SizeT(4)), CUDAStatus.Success)
      output.snapshot.flatMap(_.headOption).getOrElse(fail("missing allocation"))
    }
    def write(pointer: DevicePtr, value: Byte): CUDAStatus =
      implementation.cuMemcpyHtoD_v2(
        pointer,
        Pointer.input[Byte, CountedBy["ByteCount"]](Col(value, value, value, value)),
        SizeT(4)
      )

    val firstContext  = create()
    val firstPointer  = allocate()
    val secondContext = create()
    val secondPointer = allocate()
    assertEquals(implementation.cuCtxDestroy_v2(firstContext), CUDAStatus.Success)
    assertEquals(write(firstPointer, 1), CUDAStatus.InvalidValue)
    assertEquals(write(secondPointer, 2), CUDAStatus.Success)
    assertEquals(implementation.cuCtxDestroy_v2(secondContext), CUDAStatus.Success)
    assertEquals(write(secondPointer, 3), CUDAStatus.InvalidValue)
  }

  test("context destruction unloads modules, functions, and module globals") {
    val implementation = CUDAEmulator(PTXCUDAKernelExecution())
    val context        = Pointer.output[Context, One](1)
    assertEquals(implementation.cuCtxCreate_v2(context, UInt32(0), Device(0)), CUDAStatus.Success)
    val owner  = context.snapshot.flatMap(_.headOption).getOrElse(fail("missing context"))
    val source =
      ".version 8.0\n.target sm_80\n.address_size 64\n" +
        ".visible .global .align 4 .u32 value = 7;\n.visible .entry kernel(){ret;}\n"
    val moduleOut = Pointer.output[CUDAModule, One](1)
    assertEquals(
      implementation.cuModuleLoadData(moduleOut, ModuleImage(Col.from(source.getBytes(StandardCharsets.UTF_8)))),
      CUDAStatus.Success
    )
    val module      = moduleOut.snapshot.flatMap(_.headOption).getOrElse(fail("missing module"))
    val functionOut = Pointer.output[net.kurobako.gemstone.cuda.KernelFunction, One](1)
    assertEquals(implementation.cuModuleGetFunction(functionOut, module, FunctionName("kernel")), CUDAStatus.Success)
    val pointerOut = Pointer.output[DevicePtr, One](1)
    val sizeOut    = Pointer.output[SizeT, One](1)
    assertEquals(
      implementation.cuModuleGetGlobal_v2(pointerOut, sizeOut, module, FunctionName("value")),
      CUDAStatus.Success
    )
    val pointer = pointerOut.snapshot.flatMap(_.headOption).getOrElse(fail("missing global"))
    assertEquals(implementation.cuCtxDestroy_v2(owner), CUDAStatus.Success)
    assertEquals(
      implementation.cuModuleGetFunction(functionOut, module, FunctionName("kernel")),
      CUDAStatus.InvalidContext
    )
    val output = Pointer.output[Byte, CountedBy["ByteCount"]](4)
    assertEquals(implementation.cuMemcpyDtoH_v2(output, pointer, SizeT(4)), CUDAStatus.InvalidValue)
  }

  test("one CUDA batch resolves every registered function and returns its launch signature") {
    val implementation = CUDAEmulator(PTXCUDAKernelExecution())
    val context        = Pointer.output[Context, One](1)
    assertEquals(implementation.cuCtxCreate_v2(context, UInt32(0), Device(0)), CUDAStatus.Success)
    val source =
      ".version 8.0\n.target sm_80\n.address_size 64\n" +
        ".visible .entry first(.param .u32 x){ret;}\n.visible .entry second(.param .u64 y){ret;}\n"
    val moduleOut = Pointer.output[CUDAModule, One](1)
    assertEquals(
      implementation.cuModuleLoadData(moduleOut, ModuleImage(Col.from(source.getBytes(StandardCharsets.UTF_8)))),
      CUDAStatus.Success
    )
    val module = moduleOut.snapshot.flatMap(_.headOption).getOrElse(fail("missing module"))
    val first  = "first".getBytes(StandardCharsets.UTF_8)
    val second = "second".getBytes(StandardCharsets.UTF_8)
    val input  = ByteBuffer.allocate(4 + 4 + first.length + 4 + second.length)
      .putInt(2).putInt(first.length).put(first).putInt(second.length).put(second).array()
    val result = Pointer.output[ModuleImage, One](1)
    assertEquals(
      implementation.__cudaGemstoneResolveFunctions(result, module, ModuleImage(Col.unsafeFromArray(input))),
      CUDAStatus.Success
    )
    val bytes   = result.snapshot.flatMap(_.headOption).map(x => Col.toArray(x.bytes)).getOrElse(fail("missing batch"))
    val decoded = ByteBuffer.wrap(bytes)
    assertEquals(decoded.getInt(), 2)
    assert(decoded.getLong() != 0L); assertEquals(decoded.getInt(), 1); assertEquals(decoded.getInt(), 4)
    assert(decoded.getLong() != 0L); assertEquals(decoded.getInt(), 1); assertEquals(decoded.getInt(), 8)
    assert(!decoded.hasRemaining)
    assertEquals(
      implementation.__cudaGemstoneResolveFunctions(
        result,
        module,
        ModuleImage(Col.unsafeFromArray(ByteBuffer.allocate(4).putInt(1).array()))
      ),
      CUDAStatus.InvalidValue
    )
  }

  test("unimplemented Driver methods return CUDA_ERROR_NOT_SUPPORTED locally") {
    val implementation = CUDAEmulator()
    val context        = Pointer.output[Context, One](1)
    assertEquals(implementation.cuCtxCreate_v2(context, UInt32(0), Device(0)), CUDAStatus.Success)
    val module = Pointer.output[CUDAModule, One](1)
    assertEquals(
      implementation.cuModuleLoadData(module, ModuleImage(Col.empty[Byte])),
      CUDAStatus.NotSupported
    )
    assertEquals(module.snapshot, None)
  }

  test("generated client and dispatcher round-trip allocation and bytes") {
    val implementation = CUDAEmulator()
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
    val server       = CUDAEmulator()
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
    val server  = CUDAEmulator(PTXCUDAKernelExecution())
    val context = Pointer.output[Context, One](1)
    assertEquals(server.cuCtxCreate_v2(context, UInt32(0), Device(0)), CUDAStatus.Success)
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
