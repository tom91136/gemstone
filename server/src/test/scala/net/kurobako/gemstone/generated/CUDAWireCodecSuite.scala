package net.kurobako.gemstone.generated

import net.kurobako.gemstone.Col
import net.kurobako.gemstone.cuda.*
import net.kurobako.gemstone.rpc.*
import java.nio.{ByteBuffer, ByteOrder}

final class CUDAWireCodecSuite extends munit.FunSuite {
  private object Callbacks extends GeneratedCUDAWireCodec.CallbackResolver {
    def host(id: Long): CUhostFn           = throw IllegalArgumentException(s"unexpected callback $id")
    def stream(id: Long): CUstreamCallback = throw IllegalArgumentException(s"unexpected callback $id")
  }
  private def method(types: WireType*): RPCMethod = RPCMethod(
    "fixture",
    MessageId(0L, 0L),
    Col.from(types.map(t => RPCParameter(t.protocolName, t))),
    WireType.UnitValue
  )

  test("byte-exact big-endian response into a bounded slice") {
    val values = Col[WireValue](
      WireValue.UInt8Value(UInt8(255)),
      WireValue.Int32Value(0x12345678),
      WireValue.DevicePtrValue(DevicePtr(-2L)),
      WireValue.ByteBufferValue(Col[Byte](3, 4))
    )
    val expected = Array[Byte](-1, 0x12, 0x34, 0x56, 0x78, -1, -1, -1, -1, -1, -1, -1, -2, 0, 0, 0, 2, 3, 4)
    assertEquals(GeneratedCUDAWireCodec.encodedSize(values, 19), Right(19))
    val bytes  = Array.fill[Byte](23)(99)
    val buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
    val _      = buffer.position(2)
    val _      = buffer.limit(21)
    assertEquals(GeneratedCUDAWireCodec.writeValues(values, buffer, 19), Right(()))
    assertEquals(buffer.position(), 21)
    assertEquals(buffer.order(), ByteOrder.LITTLE_ENDIAN)
    assertEquals(bytes.toSeq, (Array[Byte](99, 99) ++ expected ++ Array[Byte](99, 99)).toSeq)
  }

  test("failed response preflight preserves bytes and position") {
    val values = Col[WireValue](WireValue.Int32Value(123), WireValue.KernelParametersValue(KernelParameters(Col.empty)))
    val bytes  = Array.fill[Byte](16)(42)
    val buffer = ByteBuffer.wrap(bytes)
    assert(GeneratedCUDAWireCodec.writeValues(values, buffer, 16).isLeft)
    assertEquals(buffer.position(), 0)
    assert(bytes.forall(_ == 42))
    assert(GeneratedCUDAWireCodec.writeValues(Col(WireValue.Int32Value(1)), buffer, 3).isLeft)
    assert(GeneratedCUDAWireCodec.writeValues(Col(WireValue.Int32Value(1)), buffer.asReadOnlyBuffer(), 16).isLeft)
    assertEquals(buffer.position(), 0)
    assert(bytes.forall(_ == 42))
  }

  test("request decoder rejects truncated, trailing and oversized bytes") {
    val m = method(WireType.ByteBuffer)
    for (
      bytes <- Seq(
        Array[Byte](0, 0, 0),
        Array[Byte](0, 0, 0, -1),
        Array[Byte](0, 0, 0, 2, 1),
        Array[Byte](0, 0, 0, 0, 1)
      )
    )
      assert(GeneratedCUDAWireCodec.decodeArguments(m, bytes, Callbacks, 16).isLeft)
    assert(GeneratedCUDAWireCodec.decodeArguments(m, Array[Byte](0, 0, 0, 1, 7), Callbacks, 4).isLeft)
    assertEquals(
      GeneratedCUDAWireCodec.decodeArguments(m, Array[Byte](0, 0, 0, 1, 7), Callbacks, 5),
      Right(Col[WireValue](WireValue.ByteBufferValue(Col[Byte](7))))
    )
  }

  test("kernel count bounded before allocating, SizeT rejects unsigned overflow") {
    val m = method(WireType.KernelParameters)
    assert(GeneratedCUDAWireCodec.decodeArguments(m, Array[Byte](0x7f, -1, -1, -1), Callbacks, 4).isLeft)
    assert(GeneratedCUDAWireCodec.decodeArguments(m, Array[Byte](0, 0, 0, 1, 0, 0, 0, 0), Callbacks, 8, 0).isLeft)
    assert(GeneratedCUDAWireCodec.decodeArguments(method(WireType.SizeT), Array.fill[Byte](8)(-1), Callbacks, 8).isLeft)
  }
}
