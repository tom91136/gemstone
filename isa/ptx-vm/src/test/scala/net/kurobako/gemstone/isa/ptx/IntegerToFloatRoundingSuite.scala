package net.kurobako.gemstone.isa.ptx

import execution.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.memory.*

class IntegerToFloatRoundingSuite extends munit.FunSuite {
  private def run(source: String, input: Long, width: ScalarBitWidth): Vec[Long] = {
    val kernel = compileKernel(source)
    val bytes  = width.toScalarByteWidth.widen[Int > 0] * 4
    val memory = DeviceMemory(bytes)
    val start  = memory.start
    assertEquals(
      execute(
        kernel,
        1,
        1,
        Map(
          ("output": NonEmpty[String]) -> Argument.fromLong(start.raw),
          ("input": NonEmpty[String])  -> Argument.fromLong(input)
        ),
        memory
      ),
      Right(())
    )
    (0 until 4).map { index =>
      memory.readLittleEndian(
        start.offset(index.toLong * width.toScalarByteWidth),
        width.toScalarByteWidth
      ).toOption.get
    }.toVec
  }

  test("directed signed 64-bit to binary32 conversion rounds around 2^24") {
    val source = ".version 8.0\n.target sm_80\n.address_size 64\n" +
      ".visible .entry convert(.param .u64 output, .param .s64 input) {\n" +
      ".reg .u64 %p; .reg .s64 %i; .reg .f32 %a, %b, %c, %d;\n" +
      "ld.param.u64 %p, [output]; ld.param.s64 %i, [input];\n" +
      "cvt.rn.f32.s64 %a, %i; cvt.rm.f32.s64 %b, %i;\n" +
      "cvt.rp.f32.s64 %c, %i; cvt.rz.f32.s64 %d, %i;\n" +
      "st.global.f32 [%p], %a; st.global.f32 [%p+4], %b;\n" +
      "st.global.f32 [%p+8], %c; st.global.f32 [%p+12], %d; ret; }"
    assertEquals(run(source, 16777217L, 32), Vec(0x4b800000L, 0x4b800000L, 0x4b800001L, 0x4b800000L))
    assertEquals(run(source, -16777217L, 32), Vec(0xcb800000L, 0xcb800001L, 0xcb800000L, 0xcb800000L))
    val justAboveHalfway = (1L << 62) + (1L << 38) + 1L
    assertEquals(
      run(source, justAboveHalfway, 32),
      Vec(0x5e800001L, 0x5e800000L, 0x5e800001L, 0x5e800000L)
    )

    val unsigned = source.replace(".param .s64 input", ".param .u64 input")
      .replace(".reg .s64 %i", ".reg .u64 %i")
      .replace("ld.param.s64 %i", "ld.param.u64 %i")
      .replace(".f32.s64", ".f32.u64")
    assertEquals(run(unsigned, -1L, 32), Vec(0x5f800000L, 0x5f7fffffL, 0x5f800000L, 0x5f7fffffL))
  }

  test("directed signed and unsigned 64-bit to binary64 conversion keeps exact 64-bit ordering") {
    val signed = ".version 8.0\n.target sm_80\n.address_size 64\n" +
      ".visible .entry convert(.param .u64 output, .param .s64 input) {\n" +
      ".reg .u64 %p; .reg .s64 %i; .reg .f64 %a, %b, %c, %d;\n" +
      "ld.param.u64 %p, [output]; ld.param.s64 %i, [input];\n" +
      "cvt.rn.f64.s64 %a, %i; cvt.rm.f64.s64 %b, %i;\n" +
      "cvt.rp.f64.s64 %c, %i; cvt.rz.f64.s64 %d, %i;\n" +
      "st.global.f64 [%p], %a; st.global.f64 [%p+8], %b;\n" +
      "st.global.f64 [%p+16], %c; st.global.f64 [%p+24], %d; ret; }"
    assertEquals(
      run(signed, 9007199254740993L, 64),
      Vec(0x4340000000000000L, 0x4340000000000000L, 0x4340000000000001L, 0x4340000000000000L)
    )
    assertEquals(
      run(signed, -9007199254740993L, 64),
      Vec(0xc340000000000000L, 0xc340000000000001L, 0xc340000000000000L, 0xc340000000000000L)
    )

    val unsigned = signed.replace(".param .s64 input", ".param .u64 input")
      .replace(".reg .s64 %i", ".reg .u64 %i")
      .replace("ld.param.s64 %i", "ld.param.u64 %i")
      .replace(".f64.s64", ".f64.u64")
    assertEquals(
      run(unsigned, -1L, 64),
      Vec(0x43f0000000000000L, 0x43efffffffffffffL, 0x43f0000000000000L, 0x43efffffffffffffL)
    )
  }
}
