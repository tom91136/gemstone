package net.kurobako.gemstone.isa.ptx.v9_3

import VMIR.*
import execution.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class ArithmeticExecutionSuite extends munit.FunSuite {
  // Exact bit patterns are independent of the VM's arithmetic and conversion helpers.
  private val cases: Vector[(String, String, Int where Between[1, 8], String, Long)] = Vector(
    (
      "addition propagates carry through an intermediate word",
      "b32",
      4,
      "add.cc.u32 %value, 0xffffffff, 1; addc.cc.u32 %value, 0xffffffff, 0; addc.u32 %value, 7, 9;",
      17L
    ),
    (
      "addition clears carry when the intermediate word does not overflow",
      "b32",
      4,
      "add.cc.u32 %value, 0xffffffff, 1; addc.cc.u32 %value, 3, 4; addc.u32 %value, 7, 9;",
      16L
    ),
    (
      "subtraction propagates borrow through an intermediate word",
      "b32",
      4,
      "sub.cc.u32 %value, 0, 1; subc.cc.u32 %value, 0, 0; subc.u32 %value, 9, 3;",
      5L
    ),
    (
      "subtraction clears borrow after the intermediate word covers it",
      "b32",
      4,
      "sub.cc.u32 %value, 0, 1; subc.cc.u32 %value, 3, 0; subc.u32 %value, 9, 3;",
      6L
    ),
    (
      "f32 fused cancellation",
      "f32",
      4,
      "fma.rn.f32 %value, 0f3f800001, 0f3f7ffffe, 0fbf800000;",
      0xa8800000L
    ),
    (
      "f64 fused cancellation",
      "f64",
      8,
      "fma.rn.f64 %value, 0d3ff0000000000001, 0d3feffffffffffffe, 0dbff0000000000000;",
      0xb970000000000000L
    ),
    (
      "bfloat16 halfway rounds to even below",
      "b16",
      2,
      "cvt.rn.bf16.f32 %value, 0f3f808000;",
      0x3f80L
    ),
    (
      "bfloat16 halfway rounds to even above",
      "b16",
      2,
      "cvt.rn.bf16.f32 %value, 0f3f818000;",
      0x3f82L
    ),
    (
      "byte permutation selects both operands",
      "b32",
      4,
      "prmt.b32 %value, 0x33221100, 0x77665544, 0x5410;",
      0x55441100L
    ),
    (
      "floating negate preserves negative zero",
      "f32",
      4,
      "neg.f32 %value, 0f00000000;",
      0x80000000L
    ),
    (
      "floating absolute clears the sign",
      "f32",
      4,
      "abs.f32 %value, 0fbfc00000;",
      0x3fc00000L
    ),
    (
      "signed bit extraction extends the field sign",
      "b32",
      4,
      "bfe.s32 %value, 0x0000f000, 12, 4;",
      0xffffffffL
    ),
    (
      "unsigned bit extraction clips at the source width",
      "b32",
      4,
      "bfe.u32 %value, 0xf0000000, 28, 8;",
      0x0000000fL
    ),
    (
      "zero-width bit extraction yields zero",
      "b32",
      4,
      "bfe.u32 %value, 0xffffffff, 7, 0;",
      0L
    ),
    (
      "bit insertion preserves bits outside the field",
      "b32",
      4,
      "bfi.b32 %value, 0xab, 0x12345678, 8, 8;",
      0x1234ab78L
    ),
    (
      "bit insertion clips at the destination width",
      "b32",
      4,
      "bfi.b32 %value, 0xab, 0x12345678, 28, 8;",
      0xb2345678L
    ),
    (
      "unsigned bit find includes the high bit",
      "b32",
      4,
      "bfind.u32 %value, 0x80000000;",
      31L
    ),
    (
      "signed bit find ignores replicated sign bits",
      "b32",
      4,
      "bfind.s32 %value, 0xfffffff7;",
      3L
    ),
    (
      "bit find reports an all-sign operand",
      "b32",
      4,
      "bfind.s32 %value, -1;",
      0xffffffffL
    ),
    (
      "packed half FMA keeps independent lanes",
      "b32",
      4,
      "fma.rn.f16x2 %value, 0xc2004000, 0x38004400, 0x4000bc00;",
      0x38004700L
    ),
    (
      "packed half saturation clamps each lane",
      "b32",
      4,
      "fma.rn.sat.f16x2 %value, 0xc2004000, 0x38004400, 0xbc003c00;",
      0x00003c00L
    ),
    (
      "packed half saturation maps NaN to positive zero",
      "b32",
      4,
      "fma.rn.sat.f16x2 %value, 0x38007e00, 0x3c003c00, 0;",
      0x38000000L
    )
  )

  cases.foreach { (name, registerType, bytes, instruction, expected) =>
    test(name) {
      val source = s""".version 8.0
         |.target sm_80
         |.address_size 64
         |.visible .entry arithmetic(.param .u64 output) {
         |  .reg .b64 %out;
         |  .reg .$registerType %value;
         |  ld.param.u64 %out, [output];
         |  $instruction
         |  st.global.b${bytes.raw * 8} [%out], %value;
         |  ret;
         |}
         |""".stripMargin
      val kernel = VMCompiler.compile(source).fold(error => fail(error.message), identity)
      val memory = ArrayCheckedMemory.allocate(8, GuestAddress(0x10000L))
      assertEquals(
        VMInterpreter.execute(
          kernel,
          LaunchGeometry.forLaunch(Dimensions.One, Dimensions.One).toOption.get,
          Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.allocation.start.raw)),
          memory
        ),
        Right(())
      )
      assertEquals(memory.readLittleEndian(memory.allocation.start, bytes), Right(expected))
    }
  }

  test("cvt.rn.f16.f64 rounds directly at a half midpoint") {
    val input  = java.lang.Double.doubleToRawLongBits(1 + math.pow(2, -11) + math.pow(2, -25))
    val source = s""".version 8.0
.target sm_80
.address_size 64
.visible .entry k(.param .u64 output) {
.reg .b64 %out;
.reg .f64 %d;
.reg .b16 %h;
ld.param.u64 %out, [output];
mov.f64 %d, 0d${java.lang.Long.toHexString(input)};
cvt.rn.f16.f64 %h, %d;
st.global.b16 [%out], %h;
ret;
}
"""
    val kernel = VMCompiler.compile(source).fold(error => fail(error.message), value => value)
    val memory = ArrayCheckedMemory.allocate(2, GuestAddress(0x10000L))
    assert(VMInterpreter.execute(
      kernel,
      LaunchGeometry.forLaunch(Dimensions.One, Dimensions.One).toOption.get,
      Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.allocation.start.raw)),
      memory
    ).isRight)
    assertEquals(memory.readLittleEndian(memory.allocation.start, 2), Right(0x3c01L))
  }

  test("execute signed and unsigned integer division and remainder") {
    val source = """
      |.version 8.0
      |.target sm_80
      |.address_size 64
      |.visible .entry divide(
      |  .param .s64 dividend,
      |  .param .u64 unsigned_dividend,
      |  .param .s64 divisor,
      |  .param .u64 unsigned_divisor,
      |  .param .u64 output
      |) {
      |  .reg .b64 %rd<9>;
      |  ld.param.s64 %rd0, [dividend];
      |  ld.param.u64 %rd1, [unsigned_dividend];
      |  ld.param.s64 %rd2, [divisor];
      |  ld.param.u64 %rd3, [unsigned_divisor];
      |  ld.param.u64 %rd4, [output];
      |  div.s64 %rd5, %rd0, %rd2;
      |  st.global.s64 [%rd4], %rd5;
      |  div.u64 %rd5, %rd1, %rd3;
      |  st.global.u64 [%rd4+8], %rd5;
      |  rem.s64 %rd6, %rd0, %rd2;
      |  st.global.s64 [%rd4+16], %rd6;
      |  rem.u64 %rd7, %rd1, %rd3;
      |  st.global.u64 [%rd4+24], %rd7;
      |  ret;
      |}
      |""".stripMargin
    val kernel = VMCompiler.compile(source).fold(error => fail(error.message), value => value)
    val memory =
      ArrayCheckedMemory.allocate(32, provenance = "integer-division")
    val result = VMInterpreter.execute(
      kernel,
      geometry(blocks = 1, threads = 1),
      Map(
        ("dividend": NonEmpty[String])          -> Argument.fromLong(-21L),
        ("unsigned_dividend": NonEmpty[String]) -> Argument.fromLong(-1L),
        ("divisor": NonEmpty[String])           -> Argument.fromLong(3L),
        ("unsigned_divisor": NonEmpty[String])  -> Argument.fromLong(2L),
        ("output": NonEmpty[String])            -> Argument.fromLong(memory.allocation.start.raw)
      ),
      memory
    )
    assert(result.isRight, result.left.toOption.map(_.message).getOrElse(""))
    assertEquals(memory.readLittleEndian(memory.allocation.start, 8).toOption.get, -7L)
    assertEquals(
      memory.readLittleEndian(memory.allocation.start.offset(8L), 8).toOption.get,
      java.lang.Long.divideUnsigned(-1L, 2L)
    )
    assertEquals(
      memory.readLittleEndian(memory.allocation.start.offset(16L), 8).toOption.get,
      0L
    )
    assertEquals(
      memory.readLittleEndian(memory.allocation.start.offset(24L), 8).toOption.get,
      java.lang.Long.remainderUnsigned(-1L, 2L)
    )
  }

  test("funnel shifts wrap or clamp the shift count") {
    val source = """
      |.version 8.0
      |.target sm_80
      |.address_size 64
      |.visible .entry choose(.param .u64 output) {
      |  .reg .b32 %r<7>;
      |  .reg .b64 %rd<2>;
      |  ld.param.u64 %rd0, [output];
      |  mov.b32 %r0, 0x12345679;
      |  mov.b32 %r1, 0x9abcdef3;
      |  shf.l.wrap.b32 %r2, %r0, %r1, 4;
      |  shf.r.wrap.b32 %r3, %r0, %r1, 4;
      |  shf.l.clamp.b32 %r4, %r0, %r1, 33;
      |  shf.r.clamp.b32 %r5, %r0, %r1, 33;
      |  st.global.u32 [%rd0], %r2;
      |  add.u64 %rd1, %rd0, 4;
      |  st.global.u32 [%rd1], %r3;
      |  add.u64 %rd1, %rd0, 8;
      |  st.global.u32 [%rd1], %r4;
      |  add.u64 %rd1, %rd0, 12;
      |  st.global.u32 [%rd1], %r5;
      |  ret;
      |}
      |""".stripMargin
    val kernel = VMCompiler.compile(source).fold(error => fail(error.message), identity)
    val memory =
      ArrayCheckedMemory.allocate(16, provenance = "funnel-shift")
    val result = VMInterpreter.execute(
      kernel,
      geometry(blocks = 1, threads = 1),
      Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.allocation.start.raw)),
      memory
    )
    assert(result.isRight, result.left.toOption.map(_.message).getOrElse(""))
    assertEquals(
      Vec.tabulate(4)(index => memory.readInt32(memory.allocation.start.offset(index.toLong * 4L)).toOption.get),
      Seq(0xabcdef31, 0x31234567, 0x12345679, 0x9abcdef3)
    )
  }

  test("wide signed 16-bit multiplication sign extends its operands") {
    val source = """
      |.version 8.0
      |.target sm_80
      |.address_size 64
      |.visible .entry choose(.param .u64 output) {
      |  .reg .s16 %rs<1>;
      |  .reg .b32 %r<1>;
      |  .reg .b64 %rd<1>;
      |  ld.param.u64 %rd0, [output];
      |  mov.s16 %rs0, -2;
      |  mul.wide.s16 %r0, %rs0, 3;
      |  st.global.u32 [%rd0], %r0;
      |  ret;
      |}
      |""".stripMargin
    val kernel = VMCompiler.compile(source).fold(error => fail(error.message), identity)
    val memory =
      ArrayCheckedMemory.allocate(4, provenance = "signed-wide-multiply")
    val result = VMInterpreter.execute(
      kernel,
      geometry(blocks = 1, threads = 1),
      Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.allocation.start.raw)),
      memory
    )
    assert(result.isRight, result.left.toOption.map(_.message).getOrElse(""))
    assertEquals(memory.readInt32(memory.allocation.start).toOption, Some(-6))
  }
}
