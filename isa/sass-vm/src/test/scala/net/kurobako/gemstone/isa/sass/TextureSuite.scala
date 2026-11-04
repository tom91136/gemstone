package net.kurobako.gemstone.isa.sass

import java.nio.ByteBuffer
import java.nio.ByteOrder
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.sass.Harness.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.texture.*

/** ptxas output of one PTX source for every architecture: each form family keeps its register conventions. */
class TextureSuite extends Suite {
  import TextureFixtures.*

  private val Surfaces = Vec(50, 70, 75, 90, 120)
  private val Textures = 35 +: Surfaces
  private val Table    = AllocationId(100)
  private val S32x1    = TexelFormat(ChannelKind.Signed, width = 32, channels = 1)

  private def le(words: Long*): Array[Byte] = {
    val buffer = ByteBuffer.allocate(words.length * 4).order(ByteOrder.LITTLE_ENDIAN)
    words.foreach(word => buffer.putInt(word.toInt))
    buffer.array()
  }

  private def floatWords(values: Float*): Array[Byte] = le(values.map(java.lang.Float.floatToRawIntBits(_).toLong)*)

  private def bits(value: Float): Long = java.lang.Float.floatToRawIntBits(value).toLong & 0xffffffffL

  private def surface(format: TexelFormat, width: Int, height: Int = 1, dimensions: Int = 1) =
    descriptor(format, width, height, dimensions = dimensions, kind = ObjectKind.Surface)

  private def kernel(name: String, sm: Int, edit: String => String): Kernel =
    load(sm, Fixture.normalise(edit(Fixture.listing(s"${name}Sm$sm"))), name)

  private class Setup(data: Array[Byte], descriptor: TextureDescriptor, handle: Int = 1) {
    val memory: Memory       = Memory()
    val region: AllocationId = memory.allocate(Vec.unsafeWrapArray(data))
    memory.allocate(Table, 64 * (handle + 1), GuestAddress(TextureLayout.Table.BaseAddress))
    Array.copy(
      descriptor.encode,
      0,
      memory.array(Table),
      (TextureTable.entryAddress(handle.toLong).get - TextureLayout.Table.BaseAddress).toInt,
      64
    )

    def run(name: String, sm: Int, outputWords: Int, edit: String => String = identity, threads: Int = 1): Vec[Long] = {
      val result =
        kernel(name, sm, edit).run(threads, memory = memory)(Arg.u64(handle.toLong), Arg.zeros(outputWords * 4))
          .fold(error => fail(error.message), identity)
      result.words(1)
    }

    def contents: Vec[Byte] = Vec.unsafeWrapArray(memory.array(region))
  }

  private def texels(count: Int)(f: Int => Long): Array[Byte] = le((0 until count).map(f)*)

  for (sm <- Textures) {
    test(s"sm_$sm integer fetch of a 2D texture takes x then y") {
      val setup =
        Setup(texels(16)(i => 1 + (i % 4) + 10 * (i / 4)), descriptor(S32x1, width = 4, height = 4, dimensions = 2))
      assertEquals(setup.run("fetch2d", sm, 4), Vec(32L, 0L, 0L, 1L))
    }

    test(s"sm_$sm integer fetch of a 3D texture takes x, y then z") {
      val setup = Setup(
        texels(64)(i => 1 + (i % 4) + 10 * ((i / 4) % 4) + 100 * (i / 16)),
        descriptor(S32x1, width = 4, height = 4, depth = 4, dimensions = 3)
      )
      assertEquals(setup.run("fetch3d", sm, 4), Vec(232L, 0L, 0L, 1L))
    }

    test(s"sm_$sm integer fetch of a 1D texture") {
      val setup = Setup(texels(4)(_ + 1), descriptor(S32x1, 4))
      assertEquals(setup.run("fetch1d", sm, 4), Vec(3L, 0L, 0L, 1L))
    }

    test(s"sm_$sm 1D sampling returns the float texel and fills the missing channels") {
      val setup = Setup(floatWords(10f, 20f, 30f, 40f), descriptor(F32x1, 4))
      assertEquals(setup.run("samp1d", sm, 4), Vec(bits(30f), 0L, 0L, bits(1f)))
    }

    test(s"sm_$sm 2D sampling with a green-only mask") {
      val format = TexelFormat(ChannelKind.Float, width = 32, channels = 2)
      val data   =
        floatWords((0 until 16).flatMap(i => Seq((i % 4 + 10 * (i / 4)).toFloat, 100f + i % 4 + 10 * (i / 4)))*)
      assertEquals(Setup(data, descriptor(format, 4, 4, dimensions = 2)).run("samp2d_g", sm, 1), Vec(bits(121f)))
    }

    test(s"sm_$sm gather returns the blue channel of the footprint") {
      val data = (0 until 16).flatMap(i => Seq(0, 0, i % 4 + 10 * (i / 4), 0).map(_.toByte)).toArray
      assertEquals(
        Setup(data, descriptor(U8x4, 4, 4, dimensions = 2)).run("gather_b", sm, 4),
        Vec(31L, 32L, 22L, 21L)
      )
    }

    test(s"sm_$sm dimension queries") {
      val setup = Setup(Array.ofDim[Byte](96), descriptor(U8x1, width = 4, height = 2, depth = 3, dimensions = 3))
      assertEquals(setup.run("query_dims", sm, 3), Vec(4L, 2L, 3L))
    }
  }

  test("sm_120 uniform handle followed by a zero sampler register") {
    val setup = Setup(floatWords(10f, 20f, 30f, 40f), descriptor(F32x1, 4))
    assertEquals(
      setup.run("samp1d", 120, 4, _.replace("UR4, 2D", "UR4, URZ, 2D")),
      Vec(bits(30f), 0L, 0L, bits(1f))
    )
    assertEquals(
      Setup(texels(4)(_ + 1), descriptor(S32x1, 4)).run("fetch1d", 120, 4, _.replace("UR4, 1D", "UR4, URZ, 1D")),
      Vec(3L, 0L, 0L, 1L)
    )
  }

  test("sm_120 separate non-zero sampler register is rejected") {
    assert(kernel("samp1d", 120, _.replace("UR4, 2D", "UR4, UR5, 2D")).lower(8, 8).left.exists(
      _.contains("separate sampler handle")
    ))
    assert(kernel("fetch1d", 120, _.replace("UR4, 1D", "UR4, UR5, 1D")).lower(8, 8).left.exists(
      _.contains("separate sampler handle")
    ))
  }

  for (sm <- Surfaces) {
    test(s"sm_$sm byte surface load clamps and zero-extends") {
      val setup = Setup((1 to 16).map(_.toByte).toArray, surface(U8x1, width = 8, height = 2, dimensions = 2))
      assertEquals(setup.run("s_ld8", sm, 1), Vec(15L))
    }

    test(s"sm_$sm 64-bit surface load fills a register pair") {
      val format = TexelFormat(ChannelKind.Unsigned, width = 32, channels = 2)
      val setup  = Setup(le(1, 2, 3, 4, 5, 6, 7, 8), surface(format, 4))
      assertEquals(setup.run("s_ld64", sm, 2), Vec(3L, 4L))
    }

    test(s"sm_$sm 128-bit surface load") {
      val format = TexelFormat(ChannelKind.Unsigned, width = 32, channels = 4)
      val setup  = Setup(le((1 to 16).map(_.toLong)*), surface(format, 4))
      assertEquals(setup.run("s_ld128", sm, 4), Vec(5L, 6L, 7L, 8L))
    }

    test(s"sm_$sm surface store takes byte x coordinates and drops zero-mode out-of-range writes") {
      val setup = Setup(Array.ofDim[Byte](32), surface(U32x1, width = 4, height = 2, dimensions = 2))
      assertEquals(setup.run("s_st32", sm, 1), Vec(0xdeadbeefL))
      assertEquals(setup.contents.toArray.toVector, le(0, 0, 0, 0, 0, 0, 0xdeadbeefL, 0).toVector)
    }

    test(s"sm_$sm surface reductions") {
      val setup = Setup(le(7, 0, 0, 0), surface(U32x1, width = 4, height = 1, dimensions = 2))
      val _     = setup.run("s_red", sm, 1)
      assertEquals(setup.contents.toArray.toVector.take(4), le(12).toVector)
    }
  }

  private class KeplerSetup(
      data: Array[Byte],
      format: TexelFormat,
      width: Int,
      height: Int = 1,
      depth: Int = 1,
      pitch: Int = TextureLayout.Kepler.Alignment
  ) {
    private val Data    = AllocationId(101)
    private val Records = AllocationId(102)
    private val base    = 0x8000000000L
    val memory: Memory  = Memory()
    memory.allocate(Data, data.length, GuestAddress(base))
    memory.allocate(Records, 64, GuestAddress(TextureLayout.KeplerTable.BaseAddress))
    Array.copy(data, 0, memory.array(Data), 0, data.length)

    private val record = ByteBuffer.allocate(TextureLayout.Kepler.Bytes).order(ByteOrder.LITTLE_ENDIAN)
    record.putInt(TextureLayout.Kepler.Base, (base / TextureLayout.Kepler.Alignment).toInt)
    record.putInt(TextureLayout.Kepler.Format, 1)
    record.putInt(TextureLayout.Kepler.LimitX, width * format.texelBytes - 1)
    record.putInt(TextureLayout.Kepler.Pitch, pitch)
    record.putInt(TextureLayout.Kepler.LimitY, height - 1)
    record.putInt(TextureLayout.Kepler.LimitZ, depth - 1)
    record.putInt(TextureLayout.Kepler.Height, height)
    Array.copy(record.array(), 0, memory.array(Records), 0, TextureLayout.Kepler.Bytes)

    def run(name: String, outputWords: Int, threads: Int = 1): Vec[Long] =
      kernel(name, 35, identity).run(threads, memory = memory)(
        Arg.u64(TextureLayout.KeplerTable.BaseAddress),
        Arg.zeros(outputWords * 4)
      ).fold(error => fail(error.message), identity).words(1)

    def contents: Vector[Byte] = memory.array(Data).toVector
  }

  private def padded(rows: Int, pitch: Int = TextureLayout.Kepler.Alignment)(row: Int => Array[Byte]): Array[Byte] = {
    val bytes = Array.ofDim[Byte](rows * pitch)
    (0 until rows).foreach(y => Array.copy(row(y), 0, bytes, y * pitch, row(y).length))
    bytes
  }

  test("sm_35 byte surface load reads a pitched 2D surface") {
    val data = padded(2)(y => (0 until 8).map(x => (1 + x + 8 * y).toByte).toArray)
    assertEquals(KeplerSetup(data, U8x1, 8, 2).run("s_ld8", 1), Vec(15L))
  }

  test("sm_35 64-bit surface load fills a register pair") {
    val format = TexelFormat(ChannelKind.Unsigned, width = 32, channels = 2)
    assertEquals(KeplerSetup(le(1, 2, 3, 4, 5, 6, 7, 8), format, 4).run("s_ld64", 2), Vec(3L, 4L))
  }

  test("sm_35 128-bit surface load") {
    val format = TexelFormat(ChannelKind.Unsigned, width = 32, channels = 4)
    assertEquals(KeplerSetup(le((1 to 16).map(_.toLong)*), format, 4).run("s_ld128", 4), Vec(5L, 6L, 7L, 8L))
  }

  test("sm_35 surface store takes byte x coordinates and drops zero-mode out-of-range writes") {
    val setup = KeplerSetup(Array.ofDim[Byte](512), U32x1, 4, 2)
    assertEquals(setup.run("s_st32", 1), Vec(0xdeadbeefL))
    assertEquals(
      setup.contents,
      padded(2)(y => if (y == 1) le(0, 0, 0xdeadbeefL, 0) else Array.ofDim[Byte](16)).toVector
    )
  }

  test("sm_35 surface reduction traps when a thread's x coordinate is past the surface") {
    val setup  = KeplerSetup(Array.ofDim[Byte](512), U32x1, 1, 2)
    val result = kernel("s_red", 35, identity).run(5, memory = setup.memory)(
      Arg.u64(TextureLayout.KeplerTable.BaseAddress),
      Arg.zeros(4)
    )
    assert(result.isLeft)
  }

  private val Mask32    = 0xffffffffL
  private val Ops       = Vector("add", "min", "max", "and", "or")
  private val U32x2     = TexelFormat(ChannelKind.Unsigned, width = 32, channels = 2)
  private val Initial   = Vector(0x05L, 0x12L, 0x80000000L, 0xffffffffL)
  private val Initial64 = Vector(0x00000001ffffffffL, 0x10L, 0x123456789L, 0L)

  private val zeroMode: Long => Option[Long]   = thread => Some(thread).filter(_ < 4)
  private val clampMode: Long => Option[Long]  = thread => Some(math.min(thread, 3L))
  private val trapMasked: Long => Option[Long] = thread => Some(thread & 3)

  private def combine(op: String, a: Long, b: Long): Long = op match {
    case "add" => a + b
    case "min" => math.min(a, b)
    case "max" => math.max(a, b)
    case "and" => a & b
    case "or"  => a | b
  }

  private def reduced(op: String, initial: Vector[Long], mask: Long)(target: Long => Option[Long]): Vector[Long] =
    initial.indices.toVector.map { element =>
      (0L until 8L)
        .filter(thread => target(thread).contains(element.toLong))
        .map(_ + 1)
        .foldLeft(initial(element))(combine(op, _, _)) & mask
    }

  private def elements(bytes: Array[Byte], texelBytes: Int): Vector[Long] = {
    val buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
    (0 until bytes.length / texelBytes).toVector.map { element =>
      if (texelBytes == 8) buffer.getLong(element * 8) else buffer.getInt(element * 4) & Mask32
    }
  }

  private def le64(words: Vector[Long]): Array[Byte] = {
    val buffer = ByteBuffer.allocate(words.length * 8).order(ByteOrder.LITTLE_ENDIAN)
    words.foreach(word => buffer.putLong(word))
    buffer.array()
  }

  for (op <- Ops) {
    test(s"sm_35 $op surface reduction drops zero-mode out-of-range threads") {
      val setup = KeplerSetup(le(Initial*), U32x1, 4)
      val _     = setup.run(s"red_${op}_b32_zero", 1, threads = 8)
      assertEquals(elements(setup.contents.toArray, 4), reduced(op, Initial, Mask32)(zeroMode))
    }

    test(s"sm_35 $op surface reduction folds clamp-mode out-of-range threads into the last element") {
      val setup = KeplerSetup(le(Initial*), U32x1, 4)
      val _     = setup.run(s"red_${op}_b32_clamp", 1, threads = 8)
      assertEquals(elements(setup.contents.toArray, 4), reduced(op, Initial, Mask32)(clampMode))
    }

    test(s"sm_50 $op surface reduction drops zero-mode out-of-range threads") {
      val setup = Setup(le(Initial*), surface(U32x1, width = 4))
      val _     = setup.run(s"red_${op}_b32_zero", 50, 1, threads = 8)
      assertEquals(elements(setup.contents.toArray, 4), reduced(op, Initial, Mask32)(zeroMode))
    }

    test(s"sm_50 $op surface reduction folds clamp-mode out-of-range threads into the last element") {
      val setup = Setup(le(Initial*), surface(U32x1, width = 4))
      val _     = setup.run(s"red_${op}_b32_clamp", 50, 1, threads = 8)
      assertEquals(elements(setup.contents.toArray, 4), reduced(op, Initial, Mask32)(clampMode))
    }

    test(s"sm_50 $op surface reduction with masked coordinates stays in bounds under trap mode") {
      val setup = Setup(le(Initial*), surface(U32x1, width = 4))
      val _     = setup.run(s"red_${op}_b32", 50, 1, threads = 8)
      assertEquals(elements(setup.contents.toArray, 4), reduced(op, Initial, Mask32)(trapMasked))
    }
  }

  test("sm_35 64-bit add surface reduction drops zero-mode out-of-range threads") {
    val setup = KeplerSetup(le64(Initial64), U32x2, 4)
    val _     = setup.run("red_add_b64_zero", 1, threads = 8)
    assertEquals(elements(setup.contents.toArray, 8), reduced("add", Initial64, -1L)(zeroMode))
  }

  test("sm_35 64-bit add surface reduction folds clamp-mode out-of-range threads into the last element") {
    val setup = KeplerSetup(le64(Initial64), U32x2, 4)
    val _     = setup.run("red_add_b64_clamp", 1, threads = 8)
    assertEquals(elements(setup.contents.toArray, 8), reduced("add", Initial64, -1L)(clampMode))
  }

  test("sm_50 64-bit add surface reduction drops zero-mode out-of-range threads") {
    val setup = Setup(le64(Initial64), surface(U32x2, width = 4))
    val _     = setup.run("red_add_b64_zero", 50, 1, threads = 8)
    assertEquals(elements(setup.contents.toArray, 8), reduced("add", Initial64, -1L)(zeroMode))
  }

  test("sm_50 64-bit add surface reduction folds clamp-mode out-of-range threads into the last element") {
    val setup = Setup(le64(Initial64), surface(U32x2, width = 4))
    val _     = setup.run("red_add_b64_clamp", 50, 1, threads = 8)
    assertEquals(elements(setup.contents.toArray, 8), reduced("add", Initial64, -1L)(clampMode))
  }

  test("sm_50 64-bit add surface reduction with masked coordinates stays in bounds under trap mode") {
    val setup = Setup(le64(Initial64), surface(U32x2, width = 4))
    val _     = setup.run("red_add_b64", 50, 1, threads = 8)
    assertEquals(elements(setup.contents.toArray, 8), reduced("add", Initial64, -1L)(trapMasked))
  }
}
