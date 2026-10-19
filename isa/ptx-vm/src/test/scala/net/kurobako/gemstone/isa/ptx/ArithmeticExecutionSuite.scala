package net.kurobako.gemstone.isa.ptx

import execution.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.memory.*

class ArithmeticExecutionSuite extends munit.FunSuite {
  private val convertedOffsets = Vec(0L, 4L, 8L)

  for (space <- Vec("const", "global"))
    test(s"cvta $space preserves bound symbol offsets and round trips generic addresses") {
      val source = s""".version 8.0
        |.target sm_80
        |.address_size 64
        |.$space .u32 values[2];
        |.visible .entry convert(.param .u64 output) {
        | .reg .u64 %out, %generic, %specific;
        | .reg .u32 %first, %second;
        | ld.param.u64 %out, [output];
        | cvta.$space.u64 %generic, values+4;
        | ld.u32 %first, [%generic];
        | cvta.to.$space.u64 %specific, %generic;
        | ld.$space.u32 %second, [%specific];
        | st.global.u32 [%out], %first;
        | st.global.u32 [%out+4], %second;
        | ret;
        |}""".stripMargin
      val memory = DeviceMemory(16, GuestAddress(0x100000000L))
      assertEquals(memory.writeInt32(memory.start, 17), Right(()))
      assertEquals(memory.writeInt32(memory.start.offset(4), 42), Right(()))
      val kernel =
        compileKernel(source, Map(("values": NonEmpty[String]) -> memory.start))
      assertEquals(
        execute(
          kernel,
          1,
          1,
          Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.start.raw + 8)),
          memory
        ),
        Right(())
      )
      assertEquals(memory.readInt32(memory.start.offset(8)), Right(42))
      assertEquals(memory.readInt32(memory.start.offset(12)), Right(42))
    }

  test("kernel parameter addresses round trip and remain readable across device calls and launches") {
    val kernel = compileKernel(""".version 8.0
      |.target sm_80
      |.address_size 64
      |.func (.param .u32 result) read_value(.param .u64 pointer) {
      | .reg .u64 %pointer;
      | .reg .u32 %value;
      | ld.param.u64 %pointer, [pointer];
      | ld.u32 %value, [%pointer];
      | st.param.u32 [result], %value;
      | ret;
      |}
      |.visible .entry convert(.param .align 4 .b8 values[8], .param .u64 output) {
      | .reg .u64 %out, %generic, %specific;
      | .reg .u32 %first, %second, %third;
      | .param .u64 pointer;
      | .param .u32 result;
      | ld.param.u64 %out, [output];
      | cvta.param.u64 %generic, values+4;
      | ld.u32 %first, [%generic];
      | cvta.to.param.u64 %specific, %generic;
      | ld.param.u32 %second, [%specific];
      | st.param.u64 [pointer], %generic;
      | call (result), read_value, (pointer);
      | ld.param.u32 %third, [result];
      | st.global.u32 [%out], %first;
      | st.global.u32 [%out+4], %second;
      | st.global.u32 [%out+8], %third;
      | ret;
      |}""".stripMargin)
    val memory = DeviceMemory(12)
    for (expected <- Vec(42, 73)) {
      val arguments = Map(
        ("values": NonEmpty[String]) -> Argument.fromLong((expected.toLong << 32) | 17L),
        ("output": NonEmpty[String]) -> Argument.fromLong(memory.start.raw)
      )
      assertEquals(execute(kernel, 1, 1, arguments, memory), Right(()))
      for (offset <- convertedOffsets)
        assertEquals(memory.readInt32(memory.start.offset(offset)), Right(expected))
    }
  }

  test("generic parameter loads cannot cross the declared argument extent") {
    val kernel = compileKernel(""".version 8.0
      |.target sm_80
      |.address_size 64
      |.visible .entry bounds(.param .align 4 .b8 values[8]) {
      | .reg .u64 %pointer;
      | .reg .u32 %value;
      | cvta.param.u64 %pointer, values+6;
      | ld.u32 %value, [%pointer];
      | ret;
      |}""".stripMargin)
    assert(execute(
      kernel,
      1,
      1,
      Map(("values": NonEmpty[String]) -> Argument.fromLong(42)),
      DeviceMemory(4)
    ).isLeft)
  }

  // Exact bit patterns are independent of the VM's arithmetic and conversion helpers.
  private val cases: Vec[(String, String, Int in 1 ~ 8, String, Long)] = Vec(
    (
      "fma rounds toward positive infinity",
      "f32",
      4,
      "fma.rp.f32 %value, 0f3f800001, 0f3f800001, 0f00000000;",
      0x3f800003L
    ),
    ("absolute without ftz preserves a subnormal", "f32", 4, "abs.f32 %value, 0f80000001;", 0x1L),
    ("absolute ftz flushes a negative subnormal", "f32", 4, "abs.ftz.f32 %value, 0f80000001;", 0x0L),
    ("negate ftz preserves the zero sign", "f32", 4, "neg.ftz.f32 %value, 0f00000001;", 0x80000000L),
    ("addition ftz flushes inputs", "f32", 4, "add.ftz.f32 %value, 0f00000001, 0f00800000;", 0x800000L),
    ("multiply ftz flushes the result", "f32", 4, "mul.ftz.f32 %value, 0f00800000, 0f3f000000;", 0x0L),
    ("multiply without ftz retains a subnormal result", "f32", 4, "mul.f32 %value, 0f00800000, 0f3f000000;", 0x400000L),
    ("saturating addition clamps above one", "f32", 4, "add.sat.f32 %value, 0f40000000, 0f3f800000;", 0x3f800000L),
    ("saturating subtraction clamps below zero", "f32", 4, "sub.sat.f32 %value, 0f3f800000, 0f40000000;", 0x0L),
    ("saturating arithmetic maps NaN to positive zero", "f32", 4, "add.sat.f32 %value, 0f7fc00000, 0f3f800000;", 0x0L),
    (
      "saturating fused multiply add clamps its result",
      "f32",
      4,
      "fma.rn.sat.f32 %value, 0f40000000, 0f40000000, 0fbf800000;",
      0x3f800000L
    ),
    (
      "fused multiply add ftz flushes the addend",
      "f32",
      4,
      "fma.rn.ftz.f32 %value, 0f00000000, 0f3f800000, 0f80000001;",
      0x0L
    ),
    ("minimum chooses its numeric right operand", "f32", 4, "min.f32 %value, 0f7fc00001, 0f3fc00000;", 0x3fc00000L),
    ("maximum chooses its numeric left operand", "f32", 4, "max.f32 %value, 0f3fc00000, 0f7fc00001;", 0x3fc00000L),
    (
      "minimum NaN modifier propagates a canonical NaN",
      "f32",
      4,
      "min.NaN.f32 %value, 0f3fc00000, 0f7fc00001;",
      0x7fffffffL
    ),
    (
      "maximum NaN modifier propagates a canonical NaN",
      "f32",
      4,
      "max.NaN.f32 %value, 0f7fc00001, 0f3fc00000;",
      0x7fffffffL
    ),
    ("minimum of two NaNs is a canonical NaN", "f32", 4, "min.f32 %value, 0f7fc00001, 0fffc00001;", 0x7fffffffL),
    ("minimum selects negative zero", "f32", 4, "min.f32 %value, 0f00000000, 0f80000000;", 0x80000000L),
    ("maximum selects positive zero", "f32", 4, "max.f32 %value, 0f80000000, 0f00000000;", 0x0L),
    (
      "minimum xorsign abs combines magnitude and original signs",
      "f32",
      4,
      "min.xorsign.abs.f32 %value, 0fc0000000, 0f3fc00000;",
      0xbfc00000L
    ),
    (
      "maximum xorsign abs combines magnitude and original signs",
      "f32",
      4,
      "max.xorsign.abs.f32 %value, 0fc0000000, 0f3fc00000;",
      0xc0000000L
    ),
    (
      "xorsign leaves a NaN result unchanged",
      "f32",
      4,
      "min.NaN.xorsign.abs.f32 %value, 0ffffc0000, 0f3fc00000;",
      0x7fffffffL
    ),
    (
      "comparison ftz flushes inputs before comparison",
      "f32",
      4,
      ".reg .pred %p; setp.eq.ftz.f32 %p, 0f00000001, 0f00000000; selp.b32 %value, 1, 0, %p;",
      0x1L
    ),
    (
      "comparison without ftz distinguishes a subnormal from zero",
      "f32",
      4,
      ".reg .pred %p; setp.eq.f32 %p, 0f00000001, 0f00000000; selp.b32 %value, 1, 0, %p;",
      0x0L
    ),
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
      "64-bit addition propagates a carry from two maximal operands",
      "b64",
      8,
      "add.cc.u64 %value, 0xffffffffffffffff, 0xffffffffffffffff; addc.u64 %value, 7, 9;",
      17L
    ),
    (
      "64-bit incoming carry overflows an otherwise maximal sum",
      "b64",
      8,
      "add.cc.u64 %value, 0xffffffffffffffff, 1; addc.cc.u64 %value, 0xffffffffffffffff, 0; addc.u64 %value, 7, 9;",
      17L
    ),
    (
      "64-bit addition clears carry after an intermediate sum fits",
      "b64",
      8,
      "add.cc.u64 %value, 0xffffffffffffffff, 1; addc.cc.u64 %value, 3, 4; addc.u64 %value, 7, 9;",
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
    ("bfloat16 conversion keeps a low-payload NaN a NaN", "b16", 2, "cvt.rn.bf16.f32 %value, 0f7f800001;", 0x7fffL),
    ("bfloat16 conversion keeps an all-ones NaN a NaN", "b16", 2, "cvt.rn.bf16.f32 %value, 0f7fffffff;", 0x7fffL),
    ("half conversion rounds toward zero", "b16", 2, "cvt.rz.f16.f32 %value, 0f3f801fff;", 0x3c00L),
    ("half conversion rounds toward positive infinity", "b16", 2, "cvt.rp.f16.f32 %value, 0f3f800001;", 0x3c01L),
    ("half conversion rounds toward negative infinity", "b16", 2, "cvt.rm.f16.f32 %value, 0fbf800001;", 0xbc01L),
    ("bfloat16 conversion from f64 rounds once", "b16", 2, "cvt.rn.bf16.f64 %value, 0d3ff0100000000001;", 0x3f81L),
    ("f32 arithmetic yields the canonical NaN", "f32", 4, "add.f32 %value, 0f7f800001, 0f3f800000;", 0x7fffffffL),
    ("f16 conversion from f32 yields the canonical NaN", "b16", 2, "cvt.rn.f16.f32 %value, 0fffc00001;", 0x7fffL),
    (
      "f32 conversion from f16 yields the canonical NaN",
      "f32",
      4,
      ".reg .b16 %h; mov.b16 %h, 0x7e01; cvt.f32.f16 %value, %h;",
      0x7fffffffL
    ),
    ("f32 negate keeps the NaN payload", "f32", 4, "neg.f32 %value, 0f7fc00001;", 0xffc00001L),
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
      "floating absolute with ftz clears the sign",
      "f32",
      4,
      "abs.ftz.f32 %value, 0fbfc00000;",
      0x3fc00000L
    ),
    (
      "floating minimum with ftz compares values across zero",
      "f32",
      4,
      "min.ftz.f32 %value, 0fc0000000, 0f3fc00000;",
      0xc0000000L
    ),
    (
      "floating maximum with ftz compares values across zero",
      "f32",
      4,
      "max.ftz.f32 %value, 0fc0000000, 0f3fc00000;",
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
    ),
    ("saturating f32 conversion clamps above one", "f32", 4, "cvt.sat.f32.f32 %value, 0f3fc00000;", 0x3f800000L),
    ("saturating f32 conversion clamps below zero", "f32", 4, "cvt.sat.f32.f32 %value, 0fbfc00000;", 0L),
    ("saturating f32 conversion maps NaN to positive zero", "f32", 4, "cvt.sat.f32.f32 %value, 0f7fc00000;", 0L),
    ("saturating f16 conversion clamps above one", "b16", 2, "cvt.rn.sat.f16.f32 %value, 0f40000000;", 0x3c00L),
    ("saturating f32 conversion from s32 clamps", "f32", 4, "cvt.rn.sat.f32.s32 %value, 7;", 0x3f800000L),
    ("saturating u8 conversion from s32 clamps above", "b32", 4, "cvt.sat.u8.s32 %value, 300;", 0xffL),
    ("saturating u8 conversion from s32 clamps below", "b32", 4, "cvt.sat.u8.s32 %value, -5;", 0L),
    ("saturating s32 conversion from u32 clamps", "b32", 4, "cvt.sat.s32.u32 %value, 0xffffffff;", 0x7fffffffL),
    ("saturating u32 conversion from s64 clamps below", "b32", 4, "cvt.sat.u32.s64 %value, -1;", 0L),
    ("saturating s64 conversion from u64 clamps", "b64", 8, "cvt.sat.s64.u64 %value, -1;", Long.MaxValue),
    ("relu f16 conversion clamps negatives", "b16", 2, "cvt.rn.relu.f16.f32 %value, 0fc0000000;", 0L),
    ("relu f16 conversion keeps positives", "b16", 2, "cvt.rn.relu.f16.f32 %value, 0f40000000;", 0x4000L),
    ("relu bf16 conversion clamps negatives", "b16", 2, "cvt.rn.relu.bf16.f32 %value, 0fc0000000;", 0L),
    ("relu f16 conversion keeps NaN", "b16", 2, "cvt.rn.relu.f16.f32 %value, 0fffc00000;", 0x7fffL),
    ("saturating s32 addition clamps above", "b32", 4, "add.sat.s32 %value, 2147483647, 1;", 0x7fffffffL),
    ("saturating s32 addition clamps below", "b32", 4, "add.sat.s32 %value, -2147483648, -1;", 0x80000000L),
    ("saturating s32 subtraction clamps above", "b32", 4, "sub.sat.s32 %value, 2147483647, -1;", 0x7fffffffL),
    ("saturating s32 subtraction clamps below", "b32", 4, "sub.sat.s32 %value, -2147483648, 1;", 0x80000000L),
    ("saturating s32 addition is exact in range", "b32", 4, "add.sat.s32 %value, -7, 3;", 0xfffffffcL),
    ("f32 addition rounds toward zero", "f32", 4, "add.rz.f32 %value, 0f3f800000, 0f33800000;", 0x3f800000L),
    (
      "f32 addition rounds toward positive infinity",
      "f32",
      4,
      "add.rp.f32 %value, 0f3f800000, 0f33800000;",
      0x3f800001L
    ),
    (
      "f32 addition rounds toward negative infinity",
      "f32",
      4,
      "add.rm.f32 %value, 0fbf800000, 0fb3800000;",
      0xbf800001L
    ),
    (
      "f32 subtraction rounds toward positive infinity",
      "f32",
      4,
      "sub.rp.f32 %value, 0f3f800000, 0fb3800000;",
      0x3f800001L
    ),
    ("f32 subtraction rounds toward zero", "f32", 4, "sub.rz.f32 %value, 0f3f800000, 0fb3800000;", 0x3f800000L),
    (
      "f32 exact cancellation rounding down is negative zero",
      "f32",
      4,
      "sub.rm.f32 %value, 0f3f800000, 0f3f800000;",
      0x80000000L
    ),
    ("f32 exact cancellation rounding up is positive zero", "f32", 4, "sub.rp.f32 %value, 0f3f800000, 0f3f800000;", 0L),
    (
      "f32 multiplication rounds toward negative infinity",
      "f32",
      4,
      "mul.rm.f32 %value, 0f3f800001, 0f3f800001;",
      0x3f800002L
    ),
    (
      "f32 multiplication rounds toward positive infinity",
      "f32",
      4,
      "mul.rp.f32 %value, 0f3f800001, 0f3f800001;",
      0x3f800003L
    ),
    (
      "f32 multiplication rounding down keeps a positive zero",
      "f32",
      4,
      "mul.rm.f32 %value, 0f00000000, 0f3f800000;",
      0L
    ),
    (
      "f32 multiplication rounding up keeps a negative zero",
      "f32",
      4,
      "mul.rp.f32 %value, 0f80000000, 0f3f800000;",
      0x80000000L
    ),
    (
      "f32 multiplication toward zero keeps a negative zero",
      "f32",
      4,
      "mul.rz.f32 %value, 0f80000000, 0f3f800000;",
      0x80000000L
    ),
    ("f32 directed addition flushes subnormal inputs", "f32", 4, "add.rz.ftz.f32 %value, 0f00000001, 0f00000001;", 0L),
    ("f32 directed addition keeps subnormals without ftz", "f32", 4, "add.rz.f32 %value, 0f00000001, 0f00000001;", 2L),
    ("f32 directed addition saturates", "f32", 4, "add.rp.sat.f32 %value, 0f3f800000, 0f33800000;", 0x3f800000L),
    (
      "f64 addition rounds toward positive infinity",
      "f64",
      8,
      "add.rp.f64 %value, 0d3ff0000000000000, 0d3c30000000000000;",
      0x3ff0000000000001L
    ),
    (
      "f64 subtraction rounding down cancels to negative zero",
      "f64",
      8,
      "sub.rm.f64 %value, 0d3ff0000000000000, 0d3ff0000000000000;",
      0x8000000000000000L
    ),
    (
      "f64 multiplication rounds toward zero",
      "f64",
      8,
      "mul.rz.f64 %value, 0d3ff0000000000001, 0d3ff0000000000001;",
      0x3ff0000000000002L
    ),
    (
      "f64 multiplication rounds toward positive infinity",
      "f64",
      8,
      "mul.rp.f64 %value, 0d3ff0000000000001, 0d3ff0000000000001;",
      0x3ff0000000000003L
    )
  )

  private val saturatingConversionInputs = Vec[(String, String, Int in 1 ~ 8, String, Long)](
    ("s32", "b32", 4, "0f4f32d05e", 0x7fffffffL),
    ("s32", "b32", 4, "0fcf32d05e", 0x80000000L),
    ("s32", "b32", 4, "0f7fc00000", 0L),
    ("u32", "b32", 4, "0f4f800000", 0xffffffffL),
    ("u32", "b32", 4, "0fc0000000", 0L),
    ("u32", "b32", 4, "0fffc00000", 0L),
    ("s16", "b16", 2, "0f47800000", 0x7fffL),
    ("s64", "b64", 8, "0f7f000000", Long.MaxValue)
  )

  private val saturatingConversionCases = Vec("rni", "rzi", "rmi", "rpi").flatMap { rounding =>
    saturatingConversionInputs.map { (destination, registerType, bytes, input, expected) =>
      (
        s"cvt.$rounding.$destination.f32 saturates $input",
        registerType,
        bytes,
        s"cvt.$rounding.$destination.f32 %value, $input;",
        expected
      )
    }
  }

  private val logicalNegationFixedInputs = Vec(
    ("zero", "0", 1L),
    ("one", "1", 0L),
    ("negative", "-1", 0L)
  )

  private val logicalNegationCases = Vec(16, 32, 64).flatMap { width =>
    logicalNegationFixedInputs.appended(("high bit", s"0x${(BigInt(1) << (width - 1)).toString(16)}", 0L)).map {
      case (name, input, expected) =>
        (
          s"cnot.b$width maps $name to $expected with aliased operands",
          s"b$width",
          (width / 8).unsafe[Int where Between[1, 8]],
          s"mov.b$width %value, $input; cnot.b$width %value, %value;",
          expected
        )
    }
  }

  private val subtractAddCases: Vec[(String, String, Int where Between[1, 8], String, Long)] = Vec(
    ("video add-add", "b32", 4, "vadd.u32.u32.u32.add %value, 12, 9, 5;", 26L),
    ("video add-add wraps overflow", "b32", 4, "vadd.u32.u32.u32.add %value, 0xffffffff, 1, 3;", 3L),
    (
      "video add-add permits destination alias",
      "b32",
      4,
      "mov.u32 %value, 9; vadd.u32.u32.u32.add %value, 12, %value, %value;",
      30L
    ),
    ("video subtract-add", "b32", 4, "vsub.u32.u32.u32.add %value, 12, 9, 5;", 8L),
    ("video subtract-add wraps underflow", "b32", 4, "vsub.u32.u32.u32.add %value, 0, 2, 1;", 0xffffffffL),
    ("video subtract-add wraps overflow", "b32", 4, "vsub.u32.u32.u32.add %value, 0xffffffff, 1, 3;", 1L),
    (
      "video subtract-add permits destination alias",
      "b32",
      4,
      "mov.u32 %value, 9; vsub.u32.u32.u32.add %value, 12, %value, %value;",
      12L
    )
  )

  (cases ++ saturatingConversionCases ++ logicalNegationCases ++ subtractAddCases).foreach {
    (name, registerType, bytes, instruction, expected) =>
      test(name) {
        val source = s""".version 8.0
         |.target sm_80
         |.address_size 64
         |.visible .entry arithmetic(.param .u64 output) {
         |  .reg .b64 %out;
         |  .reg .$registerType %value;
         |  ld.param.u64 %out, [output];
         |  $instruction
         |  st.global.b${bytes * 8} [%out], %value;
         |  ret;
         |}
         |""".stripMargin
        val kernel = compileKernel(source)
        val memory = DeviceMemory(8, GuestAddress(0x10000L))
        assertEquals(
          execute(
            kernel,
            1,
            1,
            Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.start.raw)),
            memory
          ),
          Right(())
        )
        assertEquals(memory.readLittleEndian(memory.start, bytes), Right(expected))
      }
  }

  for (
    instruction <- Seq(
      "add.rz.f16 %value, 0x3c00, 0x3c00;",
      "add.rz.sat.f64 %value, 0d3ff0000000000000, 0d3ff0000000000000;",
      "mul.rm.ftz.f64 %value, 0d3ff0000000000000, 0d3ff0000000000000;",
      "fma.rn.relu.f32 %value, 0f3f800000, 0fbf800000, 0f00000000;"
    )
  ) test(s"unsupported floating form is rejected: $instruction") {
    val source =
      s".version 9.0\n.target sm_100\n.address_size 64\n.visible .entry k() { .reg .b32 %value; $instruction ret; }"
    VMCompiler.compile(source) match {
      case Left(VMCompiler.SourceError.Compilation(Lowering.Error.Unsupported(_, _))) => ()
      case other => fail(s"expected an unsupported-instruction error, got $other")
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
    val kernel = compileKernel(source)
    val memory = DeviceMemory(2, GuestAddress(0x10000L))
    assert(execute(
      kernel,
      1,
      1,
      Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.start.raw)),
      memory
    ).isRight)
    assertEquals(memory.readLittleEndian(memory.start, 2), Right(0x3c01L))
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
    val kernel = compileKernel(source)
    val memory =
      DeviceMemory(32)
    val result = execute(
      kernel,
      1,
      1,
      Map(
        ("dividend": NonEmpty[String])          -> Argument.fromLong(-21L),
        ("unsigned_dividend": NonEmpty[String]) -> Argument.fromLong(-1L),
        ("divisor": NonEmpty[String])           -> Argument.fromLong(3L),
        ("unsigned_divisor": NonEmpty[String])  -> Argument.fromLong(2L),
        ("output": NonEmpty[String])            -> Argument.fromLong(memory.start.raw)
      ),
      memory
    )
    assert(result.isRight, result.left.toOption.map(_.message).getOrElse(""))
    assertEquals(memory.readLittleEndian(memory.start, 8).toOption.get, -7L)
    assertEquals(
      memory.readLittleEndian(memory.start.offset(8L), 8).toOption.get,
      java.lang.Long.divideUnsigned(-1L, 2L)
    )
    assertEquals(
      memory.readLittleEndian(memory.start.offset(16L), 8).toOption.get,
      0L
    )
    assertEquals(
      memory.readLittleEndian(memory.start.offset(24L), 8).toOption.get,
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
    val kernel = compileKernel(source)
    val memory =
      DeviceMemory(16)
    val result = execute(
      kernel,
      1,
      1,
      Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.start.raw)),
      memory
    )
    assert(result.isRight, result.left.toOption.map(_.message).getOrElse(""))
    assertEquals(
      Vec.tabulate(4)(index => memory.readInt32(memory.start.offset(index.toLong * 4L)).toOption.get),
      Vec(0xabcdef31, 0x31234567, 0x12345679, 0x9abcdef3)
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
    val kernel = compileKernel(source)
    val memory =
      DeviceMemory(4)
    val result = execute(
      kernel,
      1,
      1,
      Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.start.raw)),
      memory
    )
    assert(result.isRight, result.left.toOption.map(_.message).getOrElse(""))
    assertEquals(memory.readInt32(memory.start).toOption, Some(-6))
  }
}
