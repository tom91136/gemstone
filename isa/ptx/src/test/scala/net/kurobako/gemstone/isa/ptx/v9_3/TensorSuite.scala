package net.kurobako.gemstone.isa.ptx.v9_3

import execution.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.memory.*

class TensorSuite extends munit.FunSuite {
  private def registers(
      prefix: String,
      count: Int
  ): String                                = (0 until count).map(i => s"%$prefix$i").mkString("{", ",", "}")
  private def source(body: String): String = s""".version 8.0
.target sm_90
.address_size 64
.visible .entry tensor(.param .u64 data) {
.reg .u64 %base,%ap,%bp,%cp,%offset;
.reg .u32 %lane,%warp,%r<8>,%a<8>,%b<8>;
.reg .f32 %c<8>;
.reg .pred %skip;
.shared .align 16 .b8 tile[1024];
ld.param.u64 %base,[data];
mov.u32 %lane,%laneid;
$body
ret;
}"""

  private def run(
      text: String,
      memory: ArrayCheckedMemory,
      lanes: Int = 32
  ): Either[VMFault, Unit] = {
    val kernel = compileKernel(text)
    execute(
      kernel,
      1,
      lanes.unsafe[Int > 0],
      Map(("data": NonEmpty[String]) -> Argument.fromLong(memory.allocation.start.raw)),
      memory
    )
  }

  private def writeHalf(memory: ArrayCheckedMemory, offset: Int, value: Float): Unit = {
    val bits = IEEE754.floatToBinary16(value).raw
    assertEquals(memory.writeInt8(memory.allocation.start.offset(offset.toLong), bits.toByte), Right(()))
    assertEquals(memory.writeInt8(memory.allocation.start.offset(offset.toLong + 1), (bits >>> 8).toByte), Right(()))
  }
  private def writeFloat(memory: ArrayCheckedMemory, offset: Int, value: Float): Unit =
    assertEquals(
      memory.writeInt32(memory.allocation.start.offset(offset.toLong), java.lang.Float.floatToRawIntBits(value)),
      Right(())
    )
  private def readFloat(memory: ArrayCheckedMemory, offset: Int): Float =
    java.lang.Float.intBitsToFloat(memory.readInt32(memory.allocation.start.offset(offset.toLong)).toOption.get)

  for {
    (m, n)  <- Vec((16, 16), (8, 32), (32, 8))
    aLayout <- Vec("row", "col")
    bLayout <- Vec("row", "col")
    cLayout <- Vec("row", "col")
  } test(s"WMMA $m x $n, $aLayout/$bLayout/$cLayout, padded strides, two warps, in-place accumulator") {
    val memory                                         = ArrayCheckedMemory.allocate(32768)
    def a(row: Int, col: Int, warp: Int): Float        = ((row * 3 + col + warp) % 7 - 3).toFloat
    def b(row: Int, col: Int): Float                   = ((row + col * 2)        % 5 - 2).toFloat
    def c(row: Int, col: Int): Float                   = (row - col).toFloat
    def index(row: Int, col: Int, layout: String): Int = if (layout == "row") row * 64 + col else col * 64 + row
    for (warp <- 0 until 2) {
      for {
        r <- 0 until m
        k <- 0 until 16
      } writeHalf(memory, warp * 16384 + index(r, k, aLayout) * 2, a(r, k, warp))
      for {
        k   <- 0 until 16
        col <- 0 until n
      } writeHalf(memory, warp * 16384 + 4096 + index(k, col, bLayout) * 2, b(k, col))
      for {
        r   <- 0 until m
        col <- 0 until n
      } writeFloat(memory, warp * 16384 + 8192 + index(r, col, cLayout) * 4, c(r, col))
    }
    val shape = s"m${m}n${n}k16"
    val text  = source(s"""
mov.u32 %warp,%tid.x;
shr.u32 %warp,%warp,5;
mul.wide.u32 %offset,%warp,16384;
add.u64 %ap,%base,%offset;
add.u64 %bp,%ap,4096;
add.u64 %cp,%ap,8192;
wmma.load.a.sync.aligned.$aLayout.$shape.global.f16 ${registers("a", 8)},[%ap],64;
wmma.load.b.sync.aligned.$bLayout.$shape.global.f16 ${registers("b", 8)},[%bp],64;
wmma.load.c.sync.aligned.$cLayout.$shape.global.f32 ${registers("c", 8)},[%cp],64;
wmma.mma.sync.aligned.$aLayout.$bLayout.$shape.f32.f32 ${registers("c", 8)},${registers("a", 8)},${registers(
        "b",
        8
      )},${registers("c", 8)};
wmma.store.d.sync.aligned.$cLayout.$shape.global.f32 [%cp],${registers("c", 8)},64;
""")
    assertEquals(run(text, memory, 64), Right(()))
    for {
      warp <- 0 until 2
      r    <- 0 until m
      col  <- 0 until n
    } {
      val expected = (0 until 16).foldLeft(c(r, col))((sum, k) => sum + a(r, k, warp) * b(k, col))
      assertEquals(readFloat(memory, warp * 16384 + 8192 + index(r, col, cLayout) * 4), expected)
    }
  }

  for {
    count     <- Vec(1, 2, 4)
    transpose <- Vec(false, true)
  }
    test(s"ldmatrix x$count transpose=$transpose has the specified lane/register mapping") {
      val memory = ArrayCheckedMemory.allocate(1024)
      val trans  = if (transpose) ".trans" else ""
      val load   = (0 until count).map(i => s"""
add.u32 %r0,%lane,${i * 64};
add.u32 %r1,%r0,32;
mul.wide.u32 %offset,%lane,2;
add.u64 %ap,%offset,tile;
add.u64 %ap,%ap,${i * 128};
st.shared.b16 [%ap],%r0;
add.u64 %ap,%ap,64;
st.shared.b16 [%ap],%r1;
""").mkString
      val stores = (0 until count).map(i => s"st.global.b32 [%cp+${i * 128}],%a$i;").mkString("\n")
      val text   = source(s"""$load
mul.wide.u32 %offset,%lane,16;
add.u64 %ap,%offset,tile;
ldmatrix.sync.aligned.m8n8.x$count$trans.shared.b16 ${registers("a", count)},[%ap];
mul.wide.u32 %offset,%lane,4;
add.u64 %cp,%base,%offset;
$stores
""")
      assertEquals(run(text, memory), Right(()))
      for {
        matrix <- 0 until count
        lane   <- 0 until 32
      } {
        val row  = lane / 4
        val col  = lane % 4 * 2
        val low  = matrix * 64 + (if (transpose) col * 8 + row else row * 8 + col)
        val high = low + (if (transpose) 8 else 1)
        assertEquals(
          memory.readInt32(memory.allocation.start.offset(matrix * 128L + lane * 4L)),
          Right(low | (high << 16))
        )
      }
    }

  test("mma m16n8k16 consumes ldmatrix fragments and writes an in-place accumulator") {
    val memory = ArrayCheckedMemory.allocate(512, GuestAddress(0x100000000L))
    // Four A tiles filled with 1, 2, 3, 4; two transposed B tiles with 5, 6.
    // Every output distinguishes the upper/lower A rows and both K tiles.
    val fills = Vec(0x3c003c00, 0x40004000, 0x42004200, 0x44004400, 0x45004500, 0x46004600).zipWithIndex.map {
      (bits, tile) =>
        s"st.shared.b32 [%ap+${tile * 128}],${bits.toLong & 0xffffffffL};"
    }.mkString("\n")
    val zero = (0 until 4).map(i => s"mov.f32 %c$i,0f3f800000;").mkString("\n")
    val text = source(s"""
mul.wide.u32 %offset,%lane,4;
add.u64 %ap,%offset,tile;
$fills
mul.wide.u32 %offset,%lane,16;
add.u64 %ap,%offset,tile;
ldmatrix.sync.aligned.m8n8.x4.shared.b16 ${registers("a", 4)},[%ap];
add.u64 %ap,%ap,512;
ldmatrix.sync.aligned.m8n8.x2.trans.shared.b16 ${registers("b", 2)},[%ap];
$zero
mma.sync.aligned.m16n8k16.row.col.f32.f16.f16.f32 ${registers("c", 4)},${registers("a", 4)},${registers(
        "b",
        2
      )},${registers("c", 4)};
mul.wide.u32 %offset,%lane,16;
add.u64 %cp,%base,%offset;
st.global.v4.f32 [%cp],${registers("c", 4)};
""")
    assertEquals(run(text, memory), Right(()))
    for {
      lane <- 0 until 32
      i    <- 0 until 4
    }
      assertEquals(readFloat(memory, lane * 16 + i * 4), if (i < 2) 185f else 273f)
  }

  test("tensor collectives reject partial and divergent warps") {
    val body   = "ldmatrix.sync.aligned.m8n8.x1.shared.b16 {%a0},[tile];"
    val memory = ArrayCheckedMemory.allocate(4)
    assert(run(source(body), memory, 16).left.exists(_.message.contains("all 32 lanes")))
    assert(run(
      source(s"setp.ne.u32 %skip,%lane,0; @%skip bra END; $body END:"),
      memory
    ).left.exists(_.message.contains("all 32 lanes")))
  }

  test("tensor lowering rejects wrong fragment sizes and unsupported qualifiers") {
    assert(VMCompiler.compile(source("ldmatrix.sync.aligned.m8n8.x2.shared.b16 {%a0},[tile];")).isLeft)
    assert(VMCompiler.compile(source("ldmatrix.sync.aligned.m16n16.x1.shared.b8 {%a0},[tile];")).isLeft)
    assert(VMCompiler.compile(source("ldmatrix.sync.aligned.m8n8.x1.shared.b16 %a0,[tile];")).isLeft)
    val fragments = s"${registers("c", 4)},${registers("a", 2)},${registers("b", 1)},${registers("c", 4)};"
    for (suffix <- Vec("f32.f16.f16.f16", "rn.f32.f16.f16.f32", "satfinite.satfinite.s32.s8.u8.s32"))
      assert(VMCompiler.compile(source(s"mma.sync.aligned.m16n8k8.row.col.$suffix $fragments")).isLeft)
  }

  test("tensor lowering rejects zero dimensions without throwing") {
    val fragments = s"${registers("c", 4)},${registers("a", 2)},${registers("b", 1)},${registers("c", 4)};"
    for (shape <- Vec("m0n8k8", "m16n0k8", "m16n8k0"))
      assert(VMCompiler.compile(source(s"mma.sync.aligned.$shape.row.col.f32.f16.f16.f32 $fragments")).isLeft)
  }

  test("tensor opcode parsing preserves optional memory scopes and qualifier order") {
    val destinations = registers("a", 8)
    for (scope <- Vec("", ".shared::cta")) {
      val opcode = s"wmma.load.a.sync.aligned.row.m16n16k16$scope.f16"
      assert(VMCompiler.compile(source(s"$opcode $destinations,[%ap];")).isRight)
    }
    assert(VMCompiler.compile(source("ldmatrix.sync.aligned.m8n8.x1.shared.trans.b16 {%a0},[%ap];")).isLeft)
  }

  for (
    (kind, k, aCount, bCount, bits) <- Vec(
      ("f16", 8, 2, 1, 0x3c003c00L),
      ("bf16", 8, 2, 1, 0x3f803f80L),
      ("bf16", 16, 4, 2, 0x3f803f80L),
      ("tf32", 4, 2, 1, 0x3f800000L),
      ("tf32", 8, 4, 2, 0x3f800000L)
    )
  ) test(s"mma m16n8k$k $kind precision and fragment sizes") {
    val initialize = (0 until aCount).map(i => s"mov.b32 %a$i,$bits;") ++
      (0 until bCount).map(i => s"mov.b32 %b$i,$bits;") ++
      (0 until 4).map(i => s"mov.f32 %c$i,0f40000000;")
    val text = source(s"""${initialize.mkString("\n")}
mma.sync.aligned.m16n8k$k.row.col.f32.$kind.$kind.f32 ${registers("c", 4)},${registers("a", aCount)},${registers(
        "b",
        bCount
      )},${registers("c", 4)};
mul.wide.u32 %offset,%lane,16;
add.u64 %cp,%base,%offset;
st.global.v4.f32 [%cp],${registers("c", 4)};
""")
    val memory = ArrayCheckedMemory.allocate(512, GuestAddress(0x100000000L))
    assertEquals(run(text, memory), Right(()))
    for (i <- 0 until 128) assertEquals(readFloat(memory, i * 4), (k + 2).toFloat)
  }

  for (saturate <- Vec(false, true)) test(s"integer mma overflow saturate=$saturate") {
    val initialize = (0 until 2).map(i => s"mov.b32 %a$i,0x7f7f7f7f;") ++
      Vec("mov.b32 %b0,0xffffffff;") ++ (0 until 4).map(i => s"mov.b32 %r$i,0x7fffffff;")
    val sat  = if (saturate) ".satfinite" else ""
    val text = source(s"""${initialize.mkString("\n")}
mma.sync.aligned.m16n8k16.row.col$sat.s32.s8.u8.s32 ${registers("r", 4)},${registers("a", 2)},${registers(
        "b",
        1
      )},${registers("r", 4)};
mul.wide.u32 %offset,%lane,16;
add.u64 %cp,%base,%offset;
st.global.v4.b32 [%cp],${registers("r", 4)};
""")
    val memory = ArrayCheckedMemory.allocate(512, GuestAddress(0x100000000L))
    assertEquals(run(text, memory), Right(()))
    val expected = if (saturate) Int.MaxValue else (Int.MaxValue.toLong + 16L * 127 * 255).toInt
    for (i <- 0 until 128) assertEquals(memory.readInt32(memory.allocation.start.offset(i * 4L)), Right(expected))
  }

  test("TF32 conversion rounds ties away from zero and preserves special values") {
    val values   = Vec(0x3f801000, 0xbf801000, 0x3f800fff, 0x7f800000, 0xff800000, 0x80000000, 0x7f800001)
    val expected = Vec(0x3f802000, 0xbf802000, 0x3f800000, 0x7f800000, 0xff800000, 0x80000000, 0x7fc00000)
    val body     = values.zipWithIndex.map { (bits, i) =>
      s"mov.b32 %r0,${bits.toLong & 0xffffffffL}; cvt.rna.tf32.f32 %r1,%r0; st.global.b32 [%base+${i * 4}],%r1;"
    }.mkString("\n")
    val memory = ArrayCheckedMemory.allocate(28)
    assertEquals(run(source(body), memory, 1), Right(()))
    expected.zipWithIndex.foreach { (bits, i) =>
      assertEquals(memory.readInt32(memory.allocation.start.offset(i * 4L)), Right(bits))
    }
  }

  for ((m, k) <- Vec((8, 4), (16, 4), (16, 8), (16, 16))) test(s"FP64 MMA m${m}n8k$k") {
    val ac         = m * k / 32
    val bc         = k / 4
    val cc         = m / 4
    val initialize = (0 until ac).map(i => s"mov.f64 %da$i,0d3ff8000000000000;") ++
      (0 until bc).map(i => s"mov.f64 %db$i,0d4000000000000000;") ++
      (0 until cc).map(i => s"mov.f64 %dc$i,0d3ff0000000000000;")
    val stores = (0 until cc).map(i => s"st.global.f64 [%cp+${i * 8}],%dc$i;").mkString("\n")
    val text   = source(s""".reg .f64 %da<8>,%db<4>,%dc<4>;
${initialize.mkString("\n")}
mma.sync.aligned.m${m}n8k$k.row.col.f64.f64.f64.f64 ${registers("dc", cc)},${registers("da", ac)},${registers(
        "db",
        bc
      )},${registers("dc", cc)};
mul.wide.u32 %offset,%lane,${cc * 8};
add.u64 %cp,%base,%offset;
$stores
""")
    val memory = ArrayCheckedMemory.allocate((m * 8 * 8).unsafe[Int >= 0])
    assertEquals(run(text, memory), Right(()))
    val expected = java.lang.Double.doubleToRawLongBits(k * 3.0 + 1.0)
    for (i <- 0 until m * 8) {
      assertEquals(memory.readInt32(memory.allocation.start.offset(i * 8L)), Right(expected.toInt))
      assertEquals(memory.readInt32(memory.allocation.start.offset(i * 8L + 4)), Right((expected >>> 32).toInt))
    }
  }

  for (transpose <- Vec(false, true)) test(s"stmatrix writes the specified half-word layout transpose=$transpose") {
    val trans = if (transpose) ".trans" else ""
    val text  = source(s"""
add.u32 %r0,%lane,32;
shl.b32 %r0,%r0,16;
or.b32 %r0,%r0,%lane;
mul.wide.u32 %offset,%lane,16;
add.u64 %ap,%offset,tile;
stmatrix.sync.aligned.m8n8.x1$trans.shared.b16 [%ap],{%r0};
mul.wide.u32 %offset,%lane,4;
add.u64 %ap,%offset,tile;
ld.shared.b32 %r1,[%ap];
add.u64 %cp,%base,%offset;
st.global.b32 [%cp],%r1;
""")
    val memory = ArrayCheckedMemory.allocate(128)
    assertEquals(run(text, memory), Right(()))
    for {
      row <- 0 until 8
      col <- 0 until 8
    } {
      val lane    = if (transpose) col * 4 + row / 2 else row * 4 + col / 2
      val part    = if (transpose) row % 2 else col % 2
      val address = memory.allocation.start.offset((row * 8 + col) * 2L)
      val low     = memory.readInt8(address).toOption.get.toInt & 255
      val high    = memory.readInt8(address.offset(1L)).toOption.get.toInt & 255
      assertEquals(low | (high << 8), lane + part * 32)
    }
  }

  test("WMMA rejects nonuniform pointers, misalignment and out-of-bounds loads") {
    val load   = s"wmma.load.a.sync.aligned.row.m16n16k16.global.f16 ${registers("a", 8)},[%ap],16;"
    val memory = ArrayCheckedMemory.allocate(32)
    assert(run(source(s"mul.wide.u32 %offset,%lane,32; add.u64 %ap,%base,%offset; $load"), memory)
      .left.exists(_.message.contains("uniform")))
    assert(run(source(s"add.u64 %ap,%base,2; $load"), memory).left.exists(_.message.contains("aligned")))
    assert(run(source(s"mov.u64 %ap,%base; $load"), memory).left.exists {
      case _: VMFault.Memory => true
      case _                 => false
    })
  }
}
