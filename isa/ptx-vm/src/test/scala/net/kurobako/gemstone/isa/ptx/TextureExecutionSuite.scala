package net.kurobako.gemstone.isa.ptx

import execution.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.texture.*
import net.kurobako.gemstone.vm.*

class TextureExecutionSuite extends Suite {
  import TextureFixtures.*

  private val OutputOffset = 4096L

  private class TextureMemory extends DeviceMemory(8192, GuestAddress(Base)) {
    allocate(AllocationId(2), 4096, GuestAddress(TextureLayout.Table.BaseAddress))

    def put(address: Long, bytes: Array[Byte]): Unit =
      assertEquals(writeBytes(GuestAddress(address), Vec.unsafeWrapArray(bytes)), Right(()))

    def install(handle: Int, descriptor: TextureDescriptor): Unit =
      put(TextureTable.entryAddress(handle.toLong).get, descriptor.encode)

    def outputWords(count: Int): Vec[Long] = words(OutputOffset, count)
  }

  private def floatBits(values: Float*): Vector[Long] =
    values.toVector.map(value => java.lang.Float.floatToRawIntBits(value).toLong & 0xffffffffL)

  private def le(values: Float*): Array[Byte] = {
    val buffer = ByteBuffer.allocate(values.length * 4).order(ByteOrder.LITTLE_ENDIAN)
    values.foreach(buffer.putFloat(_))
    buffer.array()
  }

  private def run(body: String, memory: TextureMemory, handle: Long = 1L, threads: Int > 0 = 1): Either[Fault, Unit] = {
    val source = s""".version 8.0
      .target sm_80
      .address_size 64
      .visible .entry k(.param .u64 out, .param .u64 h) {
        .reg .b32 %r<16>;
        .reg .b16 %rs<8>;
        .reg .f32 %f<16>;
        .reg .b64 %rd<8>;
        ld.param.u64 %rd1, [out];
        ld.param.u64 %rd2, [h];
        $body
        ret;
      }"""
    execute(
      compileKernel(source),
      1,
      threads,
      Map(
        ("out": NonEmpty[String]) -> Argument.fromLong(Base + OutputOffset),
        ("h": NonEmpty[String])   -> Argument.fromLong(handle)
      ),
      memory
    )
  }

  private def memoryWith(data: Array[Byte], descriptors: (Int, TextureDescriptor)*): TextureMemory = {
    val memory = TextureMemory()
    memory.put(Base, data)
    descriptors.foreach((handle, descriptor) => memory.install(handle, descriptor))
    memory
  }

  private val ramp = le(10f, 20f, 30f, 40f)

  private val storeFour = "st.global.v4.b32 [%rd1], {%r0, %r1, %r2, %r3};"

  test("tex.1d.v4.f32.f32 point samples a texel and fills the missing channels") {
    val memory = memoryWith(ramp, 1 -> descriptor(F32x1, 4))
    val body   = s"""mov.f32 %f4, 0f40200000;
      tex.1d.v4.f32.f32 {%r0, %r1, %r2, %r3}, [%rd2, {%f4}];
      $storeFour"""
    assertEquals(run(body, memory), Right(()))
    assertEquals(memory.outputWords(4), Vec.from(floatBits(30f, 0f, 0f, 1f)))
  }

  test("tex.1d.v4.f32.f32 filters linearly between texels") {
    val memory = memoryWith(ramp, 1 -> descriptor(F32x1, 4, filter = FilterMode.Linear))
    val body   = s"""mov.f32 %f4, 0f40000000;
      tex.1d.v4.f32.f32 {%r0, %r1, %r2, %r3}, [%rd2, {%f4}];
      $storeFour"""
    assertEquals(run(body, memory), Right(()))
    assertEquals(memory.outputWords(1), Vec.from(floatBits(25f)))
  }

  test("tex.2d.v4.s32.f32 returns integer texels") {
    val data   = memoryOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16).bytes
    val memory = memoryWith(data, 1 -> descriptor(U8x4, width = 2, height = 2, dimensions = 2))
    val body   = s"""mov.f32 %f4, 0f3FC00000;
      mov.f32 %f5, 0f3FC00000;
      tex.2d.v4.s32.f32 {%r0, %r1, %r2, %r3}, [%rd2, {%f4, %f5}];
      $storeFour"""
    assertEquals(run(body, memory), Right(()))
    assertEquals(memory.outputWords(4), Vec(13L, 14L, 15L, 16L))
  }

  test("tex.2d.v4.f32.f32 reads normalised coordinates") {
    val memory = memoryWith(
      le(1f, 2f, 3f, 4f),
      1 -> descriptor(F32x1, width = 2, height = 2, dimensions = 2, normalised = true)
    )
    val body = s"""mov.f32 %f4, 0f3F400000;
      mov.f32 %f5, 0f3F400000;
      tex.2d.v4.f32.f32 {%r0, %r1, %r2, %r3}, [%rd2, {%f4, %f5}];
      $storeFour"""
    assertEquals(run(body, memory), Right(()))
    assertEquals(memory.outputWords(1), Vec.from(floatBits(4f)))
  }

  test("tex.3d.v4.f32.s32 fetches by integer coordinates from a padded coordinate vector") {
    val memory = memoryWith(
      le(1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f),
      1 -> descriptor(F32x1, width = 2, height = 2, depth = 2, dimensions = 3)
    )
    val body = s"""mov.u32 %r8, 1;
      mov.u32 %r9, 0;
      mov.u32 %r10, 1;
      tex.3d.v4.f32.s32 {%r0, %r1, %r2, %r3}, [%rd2, {%r8, %r9, %r10, %r10}];
      $storeFour"""
    assertEquals(run(body, memory), Right(()))
    assertEquals(memory.outputWords(1), Vec.from(floatBits(6f)))
  }

  test("tex.1d.v4.s32.s32 fetches signed 32-bit pairs") {
    val buffer = ByteBuffer.allocate(16).order(ByteOrder.LITTLE_ENDIAN)
    buffer.putInt(-1).putInt(7).putInt(1 << 20).putInt(-(1 << 20))
    val format = TexelFormat(ChannelKind.Signed, width = 32, channels = 2)
    val memory = memoryWith(buffer.array(), 1 -> descriptor(format, 2, address = AddressMode.Border))
    val body   = s"""mov.u32 %r8, 1;
      tex.1d.v4.s32.s32 {%r0, %r1, %r2, %r3}, [%rd2, {%r8}];
      $storeFour"""
    assertEquals(run(body, memory), Right(()))
    assertEquals(memory.outputWords(4), Vec(1L << 20, 0xfff00000L, 0L, 1L))
  }

  test("tex.1d.v4.s32.s32 returns raw 32-bit integers under a normalised read") {
    val buffer = ByteBuffer.allocate(16).order(ByteOrder.LITTLE_ENDIAN)
    buffer.putInt(-1).putInt(7).putInt(1 << 20).putInt(-(1 << 20))
    val format = TexelFormat(ChannelKind.Signed, width = 32, channels = 2)
    val memory = memoryWith(
      buffer.array(),
      1 -> descriptor(format, 2, address = AddressMode.Border, readMode = ReadMode.NormalisedFloat)
    )
    val body = s"""mov.u32 %r8, 0;
      tex.1d.v4.s32.s32 {%r0, %r1, %r2, %r3}, [%rd2, {%r8}];
      $storeFour"""
    assertEquals(run(body, memory), Right(()))
    assertEquals(memory.outputWords(4), Vec(0xffffffffL, 7L, 0L, 1L))
  }

  test("tex.1d.v2.s32.s32 fetches signed 32-bit pairs") {
    val buffer = ByteBuffer.allocate(16).order(ByteOrder.LITTLE_ENDIAN)
    buffer.putInt(-1).putInt(7).putInt(1 << 20).putInt(-(1 << 20))
    val format = TexelFormat(ChannelKind.Signed, width = 32, channels = 2)
    val memory = memoryWith(buffer.array(), 1 -> descriptor(format, 2, address = AddressMode.Border))
    val body   = """mov.u32 %r8, 0;
      tex.1d.v2.s32.s32 {%r0, %r1}, [%rd2, {%r8}];
      st.global.v2.b32 [%rd1], {%r0, %r1};"""
    assertEquals(run(body, memory), Right(()))
    assertEquals(memory.outputWords(2), Vec(0xffffffffL, 7L))
  }

  test("tex.1d.v4.s32.s32 with a register coordinate stores scalar words") {
    val buffer = ByteBuffer.allocate(16).order(ByteOrder.LITTLE_ENDIAN)
    buffer.putInt(-1).putInt(7).putInt(1 << 20).putInt(-(1 << 20))
    val format = TexelFormat(ChannelKind.Signed, width = 32, channels = 2)
    val memory = memoryWith(buffer.array(), 1 -> descriptor(format, 2, address = AddressMode.Border))
    val body   = """mov.u32 %r1, 0;
      tex.1d.v4.s32.s32 {%r3, %r4, %r5, %r6}, [%rd2, {%r1}];
      st.global.b32 [%rd1], %r3;
      st.global.b32 [%rd1+4], %r4;
      mov.u32 %r1, 1;
      tex.1d.v4.s32.s32 {%r3, %r4, %r5, %r6}, [%rd2, {%r1}];
      st.global.b32 [%rd1+8], %r3;
      st.global.b32 [%rd1+12], %r4;"""
    assertEquals(run(body, memory), Right(()))
    assertEquals(memory.outputWords(4), Vec(0xffffffffL, 7L, 1L << 20, 0xfff00000L))
  }

  test("tex.1d.v4.s32.s32 fetches linear memory with a pitch spanning the whole buffer") {
    val buffer = ByteBuffer.allocate(16).order(ByteOrder.LITTLE_ENDIAN)
    buffer.putInt(-1).putInt(7).putInt(1 << 20).putInt(-(1 << 20))
    val format     = TexelFormat(ChannelKind.Signed, width = 32, channels = 2)
    val descriptor = TextureFixtures
      .descriptor(format, 2, address = AddressMode.Border, pitch = 16, slicePitch = 16)
      .copy(resource = ResourceKind.Linear)
    val memory = memoryWith(buffer.array(), 1 -> descriptor)
    val body   = """mov.u32 %r1, %tid.x;
      tex.1d.v4.s32.s32 {%r3, %r4, %r5, %r6}, [%rd2, {%r1}];
      shl.b32 %r7, %r1, 3;
      cvt.u64.u32 %rd3, %r7;
      add.s64 %rd4, %rd1, %rd3;
      st.global.b32 [%rd4], %r3;
      st.global.b32 [%rd4+4], %r4;"""
    assertEquals(run(body, memory, threads = 2), Right(()))
    assertEquals(memory.outputWords(4), Vec(0xffffffffL, 7L, 1L << 20, 0xfff00000L))
  }

  test("tex.1d.v2.s32.s32 fetches signed pairs under clamp addressing") {
    val buffer = ByteBuffer.allocate(16).order(ByteOrder.LITTLE_ENDIAN)
    buffer.putInt(-1).putInt(7).putInt(1 << 20).putInt(-(1 << 20))
    val format = TexelFormat(ChannelKind.Signed, width = 32, channels = 2)
    val memory = memoryWith(buffer.array(), 1 -> descriptor(format, 2, address = AddressMode.Clamp))
    val body   = """mov.u32 %r8, 0;
      tex.1d.v2.s32.s32 {%r0, %r1}, [%rd2, {%r8}];
      st.global.v2.b32 [%rd1], {%r0, %r1};"""
    assertEquals(run(body, memory), Right(()))
    assertEquals(memory.outputWords(2), Vec(0xffffffffL, 7L))
  }

  test("tex.1d.v4.u32.s32 applies the address mode to out-of-range fetches") {
    val memory = memoryWith(memoryOf(7, 8, 9, 10).bytes, 1 -> descriptor(U8x1, 4))
    val body   = s"""mov.u32 %r8, 9;
      tex.1d.v4.u32.s32 {%r0, %r1, %r2, %r3}, [%rd2, {%r8}];
      $storeFour"""
    assertEquals(run(body, memory), Right(()))
    assertEquals(memory.outputWords(4), Vec(10L, 0L, 0L, 1L))
  }

  test("tld4 gathers the requested component of the footprint") {
    val memory = memoryWith(
      le(1f, 2f, 3f, 4f),
      1 -> descriptor(F32x1, width = 2, height = 2, dimensions = 2)
    )
    val body = s"""mov.f32 %f4, 0f3F800000;
      mov.f32 %f5, 0f3F800000;
      tld4.r.2d.v4.f32.f32 {%r0, %r1, %r2, %r3}, [%rd2, {%f4, %f5}];
      $storeFour"""
    assertEquals(run(body, memory), Right(()))
    assertEquals(memory.outputWords(4), Vec.from(floatBits(3f, 4f, 2f, 1f)))
  }

  test("txq reports the extent of the object") {
    val memory =
      memoryWith(Array.ofDim[Byte](96), 1 -> descriptor(F32x1, width = 3, height = 2, depth = 4, dimensions = 3))
    val body = s"""txq.width.b32 %r0, [%rd2];
      txq.height.b32 %r1, [%rd2];
      txq.depth.b32 %r2, [%rd2];
      txq.channel_data_type.b32 %r3, [%rd2];
      $storeFour"""
    assertEquals(run(body, memory), Right(()))
    assertEquals(memory.outputWords(4), Vec(3L, 2L, 4L, 0xfL))
  }

  test("an unregistered texture handle faults") {
    val memory = memoryWith(ramp, 1 -> descriptor(F32x1, 4))
    val body   = s"""mov.f32 %f4, 0f3F800000;
      tex.1d.v4.f32.f32 {%r0, %r1, %r2, %r3}, [%rd2, {%f4}];
      $storeFour"""
    assert(run(body, memory, handle = 5L).isLeft)
    assert(run(body, memory, handle = 0L).isLeft)
  }

  test("a surface handle is not a texture") {
    val memory = memoryWith(ramp, 1 -> descriptor(F32x1, 4, kind = ObjectKind.Surface))
    val body   = s"""mov.f32 %f4, 0f3F800000;
      tex.1d.v4.f32.f32 {%r0, %r1, %r2, %r3}, [%rd2, {%f4}];
      $storeFour"""
    assert(run(body, memory).isLeft)
  }

  private def surface(width: Int, height: Int = 1, depth: Int = 1, dimensions: Int = 1) =
    descriptor(U32x1, width, height, depth, dimensions = dimensions, kind = ObjectKind.Surface)

  test("suld.b.1d.b32 reads a word by byte offset") {
    val memory = memoryWith(le(1f, 2f, 3f, 4f), 1 -> surface(4))
    val body   = s"""mov.u32 %r8, 8;
      suld.b.1d.b32.trap {%r0}, [%rd2, {%r8}];
      $storeFour"""
    assertEquals(run(body, memory), Right(()))
    assertEquals(memory.outputWords(1), Vec.from(floatBits(3f)))
  }

  test("suld.b.2d.v4.b8 reads four bytes into registers") {
    val memory = memoryWith(
      memoryOf(1, 2, 3, 4, 5, 6, 7, 8).bytes,
      1 -> descriptor(U8x1, width = 4, height = 2, dimensions = 2, kind = ObjectKind.Surface)
    )
    val body = s"""mov.u32 %r8, 0;
      mov.u32 %r9, 1;
      suld.b.2d.v4.b8.trap {%rs0, %rs1, %rs2, %rs3}, [%rd2, {%r8, %r9}];
      cvt.u32.u16 %r0, %rs0;
      cvt.u32.u16 %r1, %rs1;
      cvt.u32.u16 %r2, %rs2;
      cvt.u32.u16 %r3, %rs3;
      $storeFour"""
    assertEquals(run(body, memory), Right(()))
    assertEquals(memory.outputWords(1), Vec(5L))
  }

  test("suld.b.3d.v2.b32 reads a pair of words") {
    val memory =
      memoryWith(le(1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f), 1 -> surface(width = 2, height = 2, depth = 2, dimensions = 3))
    val body = s"""mov.u32 %r8, 0;
      mov.u32 %r9, 1;
      mov.u32 %r10, 1;
      suld.b.3d.v2.b32.trap {%r0, %r1}, [%rd2, {%r8, %r9, %r10, %r10}];
      $storeFour"""
    assertEquals(run(body, memory), Right(()))
    assertEquals(memory.outputWords(2), Vec.from(floatBits(7f, 8f)))
  }

  test("sust.b.2d.v4.b32 writes a texel") {
    val memory =
      memoryWith(
        Array.ofDim[Byte](64),
        1 -> descriptor(U32x1, width = 4, height = 4, dimensions = 2, kind = ObjectKind.Surface)
      )
    val body = """mov.u32 %r0, 11;
      mov.u32 %r1, 12;
      mov.u32 %r2, 13;
      mov.u32 %r3, 14;
      mov.u32 %r8, 0;
      mov.u32 %r9, 2;
      sust.b.2d.v4.b32.trap [%rd2, {%r8, %r9}], {%r0, %r1, %r2, %r3};"""
    assertEquals(run(body, memory), Right(()))
    assertEquals(memory.words(2 * 16, 4), Vec(11L, 12L, 13L, 14L))
  }

  test("surface boundary modes clamp, zero or trap out-of-range accesses") {
    val data = memoryOf(1, 2, 3, 4)
    for ((mode, expected) <- Vector("clamp" -> Right(4L), "zero" -> Right(0L), "trap" -> Left(()))) {
      val memory = memoryWith(data.bytes, 1 -> descriptor(U8x1, 4, kind = ObjectKind.Surface))
      val body   = s"""mov.u32 %r8, 100;
        suld.b.1d.b8.$mode {%rs0}, [%rd2, {%r8}];
        cvt.u32.u16 %r0, %rs0;
        st.global.b32 [%rd1], %r0;"""
      val result = run(body, memory)
      expected match {
        case Right(value) =>
          assertEquals(result, Right(()))
          assertEquals(memory.outputWords(1), Vec(value))
        case Left(_) => assert(result.isLeft)
      }
    }
  }

  test("zero-mode stores outside the surface are dropped") {
    val memory = memoryWith(Array.ofDim[Byte](16), 1 -> surface(4))
    val body   = """mov.u32 %r0, 5;
      mov.u32 %r8, 400;
      sust.b.1d.b32.zero [%rd2, {%r8}], {%r0};"""
    assertEquals(run(body, memory), Right(()))
    assertEquals(memory.words(0, 4), Vec(0L, 0L, 0L, 0L))
  }

  test("sured.b.add.1d.u32 adds into the surface") {
    val memory = memoryWith(le(0f), 1 -> surface(1))
    memory.put(Base, Array[Byte](5, 0, 0, 0))
    val body = """mov.u32 %r0, 3;
      mov.u32 %r8, 0;
      sured.b.add.1d.u32.trap [%rd2, {%r8}], %r0;
      sured.b.add.1d.u32.trap [%rd2, {%r8}], %r0;"""
    assertEquals(run(body, memory), Right(()))
    assertEquals(memory.word(0), 11L)
  }

  test("sured.b.max.1d.u64 keeps the larger value") {
    val memory = memoryWith(Array.ofDim[Byte](16), 1 -> surface(2))
    val body   = """mov.u64 %rd4, 4294967297;
      mov.u32 %r8, 0;
      sured.b.max.1d.u64.trap [%rd2, {%r8}], %rd4;"""
    assertEquals(run(body, memory), Right(()))
    assertEquals(memory.readLittleEndian(GuestAddress(Base), 8), Right(4294967297L))
  }

  test("sured.b.min.1d.u32 keeps the smaller value") {
    val memory = memoryWith(memoryOf(5, 0, 0, 0).bytes, 1 -> surface(1))
    val body   = """mov.u32 %r0, 3;
      mov.u32 %r8, 0;
      sured.b.min.1d.u32.trap [%rd2, {%r8}], %r0;"""
    assertEquals(run(body, memory), Right(()))
    assertEquals(memory.word(0), 3L)
  }

  test("sured.b.and.1d.b32 and sured.b.or.1d.b32 combine bits into the surface") {
    val memory = memoryWith(memoryOf(12, 0, 0, 0).bytes, 1 -> surface(1))
    val body   = """mov.u32 %r0, 10;
      mov.u32 %r8, 0;
      sured.b.and.1d.b32.trap [%rd2, {%r8}], %r0;
      mov.u32 %r0, 1;
      sured.b.or.1d.b32.trap [%rd2, {%r8}], %r0;"""
    assertEquals(run(body, memory), Right(()))
    assertEquals(memory.word(0), 9L)
  }

  test("sured.b.add.2d.u32 addresses the row by y") {
    val memory = memoryWith(
      Array.tabulate[Byte](64)(i => if (i == 32) 5 else 0),
      1 -> descriptor(U32x1, width = 4, height = 4, dimensions = 2, kind = ObjectKind.Surface)
    )
    val body = """mov.u32 %r0, 3;
      mov.u32 %r8, 0;
      mov.u32 %r9, 2;
      sured.b.add.2d.u32.trap [%rd2, {%r8, %r9}], %r0;"""
    assertEquals(run(body, memory), Right(()))
    assertEquals(memory.words(2 * 16, 4), Vec(8L, 0L, 0L, 0L))
  }

  test("sured.b.add.1d.u32.trap faults on an out-of-range coordinate and leaves the surface unchanged") {
    val memory = memoryWith(Array.ofDim[Byte](16), 1 -> surface(4))
    val body   = """mov.u32 %r0, 5;
      mov.u32 %r8, 400;
      sured.b.add.1d.u32.trap [%rd2, {%r8}], %r0;"""
    assert(run(body, memory).isLeft)
    assertEquals(memory.words(0, 4), Vec(0L, 0L, 0L, 0L))
  }

  test("sured.b.add.1d.u32.zero drops out-of-range reductions") {
    val memory = memoryWith(Array.ofDim[Byte](16), 1 -> surface(4))
    val body   = """mov.u32 %r0, 5;
      mov.u32 %r8, 400;
      sured.b.add.1d.u32.zero [%rd2, {%r8}], %r0;"""
    assertEquals(run(body, memory), Right(()))
    assertEquals(memory.words(0, 4), Vec(0L, 0L, 0L, 0L))
  }

  test("sured.b.add.1d.u32.clamp reduces the last element for an out-of-range coordinate") {
    val memory = memoryWith(Array.ofDim[Byte](16), 1 -> surface(4))
    val body   = """mov.u32 %r0, 5;
      mov.u32 %r8, 400;
      sured.b.add.1d.u32.clamp [%rd2, {%r8}], %r0;"""
    assertEquals(run(body, memory), Right(()))
    assertEquals(memory.words(0, 4), Vec(0L, 0L, 0L, 5L))
  }
}
