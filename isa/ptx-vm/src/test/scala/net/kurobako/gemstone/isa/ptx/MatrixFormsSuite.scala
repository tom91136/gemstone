package net.kurobako.gemstone.isa.ptx

import execution.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.testing.Suite
import net.kurobako.gemstone.vm.*

/** Matrix forms of the architectures after sm_89, against layouts and values the PTX ISA documents. */
class MatrixFormsSuite extends Suite {
  private def source(target: String, body: String): String = s""".version 9.0
.target $target
.address_size 64
.visible .entry forms(.param .u64 data) {
.reg .u64 %base,%ap,%cp,%offset;
.reg .u32 %lane,%r<8>,%a<8>,%b<8>,%c<8>,%d<8>,%s<4>;
.reg .f64 %fa<8>,%fb<8>,%fc<8>;
.shared .align 16 .b8 tile[1024];
ld.param.u64 %base,[data];
mov.u32 %lane,%laneid;
$body
ret;
}"""

  private def run(text: String, memory: DeviceMemory): Either[Fault, Unit] = execute(
    compileKernel(text),
    1,
    32,
    Map(("data": NonEmpty[String]) -> Argument.fromLong(memory.start.raw)),
    memory
  )

  private def registers(
      prefix: String,
      count: Int
  ): String = (0 until count).map(i => s"%$prefix$i").mkString("{", ",", "}")
  private def byte(memory: DeviceMemory, offset: Long): Int =
    (memory.word(offset & ~3L) >>> (8 * (offset & 3L))).toInt & 0xff

  test("wmma loads take 16-byte strides, such as 24 half-precision elements, and reject others") {
    def load(stride: Int) = source(
      "sm_80",
      s"wmma.load.a.sync.aligned.row.m16n16k16.global.f16 ${registers("a", 8)},[%base],$stride;"
    )
    assertEquals(run(load(24), DeviceMemory(4096)), Right(()))
    assert(run(load(20), DeviceMemory(4096)).left.exists(_.message.contains("strides to 16")))
  }

  test("ldmatrix.m16n16 unpacks 4-bit elements to the middle of each byte, transposed") {
    // row r of the stored matrix packs elements e = (r + 3 * e) % 16, two to a byte, then 8 bytes of padding
    val packed = (0 until 16).map { row =>
      (0 until 8).map(pair => ((row + 6 * pair) % 16) | (((row + 3 * (2 * pair + 1)) % 16) << 4)).toVector
    }
    val stores = packed.zipWithIndex.flatMap((row, r) =>
      row.zipWithIndex.map((value, b) => s"st.shared.b8 [tile+${r * 16 + b}],$value;")
    ).mkString("\n")
    val memory = DeviceMemory(4096)
    val text   = source(
      "sm_100a",
      s"""$stores
mul.wide.u32 %offset,%lane,16;
add.u64 %ap,%offset,tile;
ldmatrix.sync.aligned.m16n16.x1.trans.shared.b8x16.b4x16_p64 ${registers("a", 2)},[%ap];
mul.wide.u32 %offset,%lane,8;
add.u64 %cp,%base,%offset;
st.global.b32 [%cp],%a0;
st.global.b32 [%cp+4],%a1;
"""
    )
    assertEquals(run(text, memory), Right(()))
    for {
      lane     <- 0 until 32
      register <- 0 until 2
      part     <- 0 until 4
    } {
      val row    = lane / 4 + 8 * register
      val column = lane % 4 * 4 + part
      // the fragment's row and column are the stored column and row
      val expected = ((column + 3 * row) % 16) << 2
      assertEquals(
        byte(memory, lane * 8L + register * 4L + part),
        expected,
        s"lane $lane register $register part $part"
      )
    }
  }

  test("stmatrix.m16n8 stores byte fragments transposed, one 16-byte row per column") {
    val memory = DeviceMemory(4096)
    val text   = source(
      "sm_120a",
      """mad.lo.u32 %a0,%lane,0x01010101,0x03020100;
mul.wide.u32 %offset,%lane,16;
add.u64 %ap,%offset,tile;
stmatrix.sync.aligned.m16n8.x1.trans.shared.b8 [%ap],{%a0};
bar.warp.sync 0xffffffff;
mul.wide.u32 %offset,%lane,4;
add.u64 %ap,%offset,tile;
ld.shared.b32 %r0,[%ap];
add.u64 %cp,%base,%offset;
st.global.b32 [%cp],%r0;
"""
    )
    assertEquals(run(text, memory), Right(()))
    // lane l holds bytes e0..e3 = l + 0..3; e0, e1 are row l / 4, columns l % 4 * 2 and the next; e2, e3 the same
    // columns of row l / 4 + 8; column c is stored as row c of 16 bytes, rows 0 to 7 provided by lanes 0 to 7
    for {
      lane <- 0 until 32
      part <- 0 until 4
    } {
      val row    = lane / 4 + 8 * (part / 2)
      val column = lane % 4 * 2 + part % 2
      assertEquals(byte(memory, column * 16L + row), (lane + part) & 0xff, s"lane $lane part $part")
    }
  }

  test("kind::f8f6f4 reads FP4 and FP6 elements from their byte containers") {
    // e2m1 1.5 is 0b0011 in the middle four bits; e3m2 2.0 is 0b010000 in the low six
    val memory = DeviceMemory(4096)
    val text   = source(
      "sm_120a",
      s"""${(0 until 4).map(i => s"mov.b32 %a$i,0x0c0c0c0c;").mkString("\n")}
${(0 until 2).map(i => s"mov.b32 %b$i,0x10101010;").mkString("\n")}
${(0 until 4).map(i => s"mov.b32 %c$i,0f3f800000;").mkString("\n")}
mma.sync.aligned.m16n8k32.row.col.kind::f8f6f4.f32.e2m1.e3m2.f32 ${registers("d", 4)},${registers("a", 4)},${registers(
          "b",
          2
        )},${registers("c", 4)};
mul.wide.u32 %offset,%lane,16;
add.u64 %cp,%base,%offset;
st.global.v4.b32 [%cp],${registers("d", 4)};
"""
    )
    assertEquals(run(text, memory), Right(()))
    for (index <- 0 until 128) assertEquals(memory.word(index * 4L), java.lang.Float.floatToRawIntBits(97f).toLong)
  }

  test("block scaling multiplies each row of A and column of B by the factors their selected lanes and bytes hold") {
    // A and B are all 1.0 (e4m3 0x38); lane l holds factor bytes 2^(l % 4) and, at byte 1, 2^(l / 4)
    val memory = DeviceMemory(4096)
    val text   = source(
      "sm_120a",
      s"""${(0 until 4).map(i => s"mov.b32 %a$i,0x38383838;").mkString("\n")}
${(0 until 2).map(i => s"mov.b32 %b$i,0x38383838;").mkString("\n")}
${(0 until 4).map(i => s"mov.b32 %c$i,0f00000000;").mkString("\n")}
and.b32 %r0,%lane,3;
add.u32 %r0,%r0,127;
shr.u32 %r1,%lane,2;
add.u32 %r1,%r1,127;
shl.b32 %r1,%r1,8;
or.b32 %s0,%r0,%r1;
mma.sync.aligned.m16n8k32.row.col.kind::mxf8f6f4.block_scale.scale_vec::1X.f32.e4m3.e4m3.f32.ue8m0 ${registers(
          "d",
          4
        )},${registers("a", 4)},${registers("b", 2)},${registers("c", 4)},%s0,{0,1},%s0,{1,2};
mul.wide.u32 %offset,%lane,16;
add.u64 %cp,%base,%offset;
st.global.v4.b32 [%cp],${registers("d", 4)};
"""
    )
    assertEquals(run(text, memory), Right(()))
    // row i's factor is byte 0 of lane 4 * (i % 8) + 2 + i / 8, 2^(2 + i / 8); column j's is byte 1 of lane 4 * j + 2,
    // 2^j; D = 32 * 2^(2 + i / 8) * 2^j
    for {
      lane  <- 0 until 32
      index <- 0 until 4
    } {
      val row    = lane / 4 + 8 * (index / 2)
      val column = lane % 4 * 2 + index % 2
      val value  = 32f * math.pow(2, 2 + row / 8 + column).toFloat
      assertEquals(memory.word(lane * 16L + index * 4L), java.lang.Float.floatToRawIntBits(value).toLong)
    }
  }

  test("f64 m16n8k16 chains a fused multiply-add per product from C") {
    // A[i][k] = i + k, B[k][j] = k - j, C[i][j] = i * j; every product and sum is exact
    val memory = DeviceMemory(8192)
    val text   = source(
      "sm_90",
      s"""shr.u32 %r6,%lane,2;
and.b32 %r7,%lane,3;
${(0 until 8).map(i =>
          s"add.u32 %r0,%r6,${(i % 2) * 8};\nadd.u32 %r1,%r7,${(i / 2) * 4};\nadd.u32 %r2,%r0,%r1;\ncvt.rn.f64.u32 %fa$i,%r2;"
        ).mkString("\n")}
${(0 until 4).map(i =>
          s"add.u32 %r1,%r7,${i * 4};\ncvt.rn.f64.u32 %fb$i,%r1;\ncvt.rn.f64.u32 %fc$i,%r6;\nsub.f64 %fb$i,%fb$i,%fc$i;"
        ).mkString("\n")}
${(0 until 4).map(i =>
          s"add.u32 %r0,%r6,${(i / 2) * 8};\nmad.lo.u32 %r1,%r7,2,${i % 2};\nmul.lo.u32 %r2,%r0,%r1;\ncvt.rn.f64.u32 %fc$i,%r2;"
        ).mkString("\n")}
mma.sync.aligned.m16n8k16.row.col.f64.f64.f64.f64 ${registers("fc", 4)},${registers("fa", 8)},${registers(
          "fb",
          4
        )},${registers("fc", 4)};
mul.wide.u32 %offset,%lane,32;
add.u64 %cp,%base,%offset;
st.global.v2.f64 [%cp],{%fc0,%fc1};
st.global.v2.f64 [%cp+16],{%fc2,%fc3};
"""
    )
    assertEquals(run(text, memory), Right(()))
    for {
      lane  <- 0 until 32
      index <- 0 until 4
    } {
      val row      = lane / 4 + 8 * (index / 2)
      val column   = lane % 4 * 2 + index % 2
      val expected = (0 until 16).map(k => (row + k).toDouble * (k - column)).sum + row * column
      val bits     = memory.word(lane * 32L + index * 8L) | (memory.word(lane * 32L + index * 8L + 4) << 32)
      assertEquals(java.lang.Double.longBitsToDouble(bits), expected, s"lane $lane index $index")
    }
  }
}
