package net.kurobako.gemstone.isa.ptx

import execution.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

/** Instructions as nvcc 13.3 (`nvcc -O3 -arch=sm_XX -ptx`) and clang 22 (`clang++ -x cuda --cuda-gpu-arch=sm_XX -S
  * --cuda-device-only -O3`) emit them for ordinary CUDA C++ or through the CUDA and CCCL headers, each named after its
  * source, with the results the PTX ISA 13.3 specifies.
  */
class EmittedInstructionSuite extends munit.FunSuite {

  /** The 64-bit words `body` stores from `%out` on, after running on one thread with `%in` addressing `inputs`. */
  private def run(registers: String, body: String, inputs: Vec[Long], outputs: Int, target: String = "sm_90")(using
      munit.Location
  ): Vec[Long] = runWords("emitted", target, registers, body, inputs, outputs, bytes = 8)

  private def f32(value: Float): Long = java.lang.Float.floatToRawIntBits(value).toLong & 0xffffffffL

  // __floats2half2_rn and __floats2bfloat162_rn (cuda_fp16.hpp:625, cuda_bf16.hpp:555): the first source fills the
  // upper half.
  for (
    (spelling, expected) <- Vec(
      "cvt.rn.f16x2.f32"  -> Vec(0x3e00c000L, 0x3c013c01L),
      "cvt.rn.bf16x2.f32" -> Vec(0x3fc0c000L, 0x3f803f80L)
    )
  )
    test(s"$spelling packs the first source into the upper half") {
      val result = run(
        ".reg .f32 %a, %b, %c; .reg .b32 %d;",
        s"""ld.global.f32 %a, [%in]; ld.global.f32 %b, [%in+8]; ld.global.f32 %c, [%in+16];
           |$spelling %d, %a, %b; st.global.u64 [%out], 0; st.global.b32 [%out], %d;
           |$spelling %d, %c, %c; st.global.u64 [%out+8], 0; st.global.b32 [%out+8], %d;""".stripMargin,
        Vec(f32(1.5f), f32(-2.0f), f32(1.0f + 3.0f / 4096.0f + 1.0f / 1024.0f / 8.0f)),
        2
      )
      assertEquals(result, expected)
    }

  // `(short)(a * b + c)`: clang, and nvcc for sm_100a and sm_120.
  for (kind <- Vec("s16", "u16"))
    test(s"mad.lo.$kind keeps the low 16 bits of the product and sum") {
      val result = run(
        ".reg .b16 %a, %b, %c, %d;",
        s"""ld.global.u16 %a, [%in]; ld.global.u16 %b, [%in+8]; ld.global.u16 %c, [%in+16];
           |mad.lo.$kind %d, %a, %b, %c; st.global.u64 [%out], 0; st.global.u16 [%out], %d;""".stripMargin,
        Vec(300L, 300L, 7L),
        1
      )
      assertEquals(result, Vec((300L * 300L + 7L) & 0xffffL))
    }

  // __sad and __usad: d = c + |a - b|.
  test("sad.s32 and sad.u32 add the absolute difference to the third source") {
    val result = run(
      ".reg .b32 %a, %b, %c, %d;",
      """ld.global.u32 %a, [%in]; ld.global.u32 %b, [%in+8]; ld.global.u32 %c, [%in+16];
        |sad.s32 %d, %a, %b, %c; st.global.u64 [%out], 0; st.global.u32 [%out], %d;
        |sad.u32 %d, %a, %b, %c; st.global.u64 [%out+8], 0; st.global.u32 [%out+8], %d;""".stripMargin,
      Vec(3L, 0xfffffffbL, 10L),
      2
    )
    assertEquals(result, Vec(18L, (10L + 0xfffffffbL - 3L) & 0xffffffffL))
  }

  // nvcc expands the SIMD compare and min/max intrinsics with lop3; the table selects F(0xf0, 0xcc, 0xaa).
  for (table <- Vec(0x8e, 0x4d, 0x71, 0xb2))
    test(f"lop3.b32 applies its table 0x$table%02x to each bit") {
      val result = run(
        ".reg .b32 %d;",
        f"""lop3.b32 %%d, 0xf0f0f0f0, 0xcccccccc, 0xaaaaaaaa, 0x$table%02x;
           |st.global.u64 [%%out], 0; st.global.b32 [%%out], %%d;""".stripMargin,
        Vec.empty,
        1
      )
      assertEquals(result, Vec(table.toLong * 0x01010101L))
    }

  // atomicInc and atomicDec: inc wraps to 0 once the old value reaches the operand, dec wraps to the operand at 0 or
  // above it.
  for (
    (spelling, old, operand, stored) <- Vec(
      ("atom.global.inc.u32", 4L, 5L, 5L),
      ("atom.global.inc.u32", 5L, 5L, 0L),
      ("atom.global.dec.u32", 0L, 5L, 5L),
      ("atom.global.dec.u32", 7L, 5L, 5L),
      ("atom.global.dec.u32", 3L, 5L, 2L)
    )
  )
    test(s"$spelling of $old by $operand returns the old value and stores $stored") {
      val result = run(
        ".reg .b32 %v, %r;",
        s"""st.global.u64 [%out], $old; mov.u32 %v, $operand;
           |$spelling %r, [%out], %v; st.global.u64 [%out+8], 0; st.global.u32 [%out+8], %r;""".stripMargin,
        Vec.empty,
        2
      )
      assertEquals(result, Vec(stored, old))
    }

  // cuda::atomic_ref<int>::fetch_add, relaxed and sequentially consistent.
  for (spelling <- Vec("atom.add.relaxed.gpu.s32", "atom.add.acquire.gpu.s32", "atom.cta.add.s32"))
    test(s"$spelling adds signed words") {
      val result = run(
        ".reg .b32 %v, %r;",
        s"""st.global.u64 [%out], 5; mov.u32 %v, -7;
           |$spelling %r, [%out], %v; st.global.u64 [%out+8], 0; st.global.u32 [%out+8], %r;""".stripMargin,
        Vec.empty,
        2
      )
      assertEquals(result, Vec(0xfffffffeL, 5L))
    }

  // cooperative_groups/details/sync.h:164-184, and inline reductions such as cuda::ptx::red_async's.
  for (
    (spelling, old, operand, stored) <- Vec(
      ("red.or.release.cta.b32", 0x0fL, 0xf0L, 0xffL),
      ("red.and.relaxed.cta.b32", 0x0fL, 0x3cL, 0x0cL),
      ("red.and.release.cta.b32", 0x0fL, 0x3cL, 0x0cL),
      ("red.relaxed.gpu.global.add.u32", 5L, 7L, 12L),
      ("red.global.add.u32", 5L, 7L, 12L)
    )
  )
    test(s"$spelling stores the reduction without a result") {
      val result = run(
        ".reg .b32 %v;",
        s"st.global.u64 [%out], $old; mov.u32 %v, $operand; $spelling [%out], %v;",
        Vec.empty,
        1
      )
      assertEquals(result, Vec(stored))
    }

  // __fns (PTX ISA 9.7.1.18 examples).
  for (
    (
      base,
      offset,
      expected
    ) <- Vec((3, 1, 3L), (3, -1, 3L), (2, 1, 3L), (2, -1, 1L), (0, 0, 0xffffffffL), (31, 2, 0xffffffffL))
  )
    test(s"fns.b32 finds set bit $offset from bit $base of 0xaaaaaaaa") {
      val result = run(
        ".reg .b32 %d;",
        s"fns.b32 %d, 0xaaaaaaaa, $base, $offset; st.global.u64 [%out], 0; st.global.b32 [%out], %d;",
        Vec.empty,
        1
      )
      assertEquals(result, Vec(expected))
    }

  // __dp2a_lo and __dp2a_hi (sm_61_intrinsics.hpp:81-149).
  test("dp2a multiplies two halves by the low or high byte pair and accumulates") {
    val result = run(
      ".reg .b32 %a, %b, %c, %d;",
      """ld.global.u32 %a, [%in]; ld.global.u32 %b, [%in+8]; ld.global.u32 %c, [%in+16];
        |dp2a.lo.s32.s32 %d, %a, %b, %c; st.global.u64 [%out], 0; st.global.u32 [%out], %d;
        |dp2a.hi.u32.u32 %d, %a, %b, %c; st.global.u64 [%out+8], 0; st.global.u32 [%out+8], %d;""".stripMargin,
      Vec(0xfffe0003L, 0x0405ff02L, 100L),
      2
    )
    assertEquals(result, Vec((100L + 3 * 2 + -2 * -1) & 0xffffffffL, (100L + 3L * 5 + 0xfffeL * 4) & 0xffffffffL))
  }

  // __vabsdiffu4, __vsadu4, __vsadu2 and __vaddss4, whose .sat precedes the types, under nvcc; the rest of the SIMD
  // intrinsics under clang (__clang_cuda_device_functions.h:1070-1546).
  for (
    (spelling, a, b, c, expected) <- Vec(
      ("vabsdiff4.u32.u32.u32", 0x01020304L, 0x04030201L, 0L, 0x03010103L),
      ("vabsdiff4.u32.u32.u32.add", 0x01020304L, 0x04030201L, 10L, 18L),
      ("vabsdiff2.u32.u32.u32.add", 0x00010009L, 0x00050002L, 10L, 21L),
      ("vadd2.u32.u32.u32", 0xffff0001L, 0x00020003L, 0L, 0x00010004L),
      ("vadd2.u32.u32.u32.sat", 0xffff0001L, 0x00020003L, 0L, 0xffff0004L),
      ("vadd2.s32.s32.s32.sat", 0x7fff8000L, 0x0001ffffL, 0L, 0x7fff8000L),
      ("vadd4.u32.u32.u32.sat", 0xff010203L, 0x02010101L, 0L, 0xff020304L),
      ("vadd4.sat.s32.s32.s32", 0x7f80ff01L, 0x01ff0101L, 0L, 0x7f800002L),
      ("vsub4.s32.s32.s32.sat", 0x80017f00L, 0x01ff8101L, 0L, 0x80027fffL),
      ("vavrg2.u32.u32.u32", 0x00030004L, 0x00040004L, 0L, 0x00040004L),
      ("vavrg4.s32.s32.s32", 0xfd030004L, 0x00040004L, 0L, 0xfe040004L),
      ("vmax4.s32.s32.s32", 0x80017f00L, 0x01ff8101L, 0L, 0x01017f01L),
      ("vmin2.u32.u32.u32", 0x00030009L, 0x00050002L, 0L, 0x00030002L),
      ("vset2.u32.u32.lt", 0x00010009L, 0x00050002L, 0L, 0x00010000L),
      ("vset4.s32.s32.ge", 0x80017f00L, 0x01ff8101L, 0L, 0x00010100L),
      ("vset4.u32.u32.ne.add", 0x01020304L, 0x01020305L, 3L, 4L)
    )
  )
    test(f"$spelling of 0x$a%08x and 0x$b%08x") {
      val result = run(
        ".reg .b32 %a, %b, %c, %d;",
        s"""ld.global.u32 %a, [%in]; ld.global.u32 %b, [%in+8]; ld.global.u32 %c, [%in+16];
           |$spelling %d, %a, %b, %c; st.global.u64 [%out], 0; st.global.u32 [%out], %d;""".stripMargin,
        Vec(a, b, c),
        1
      )
      assertEquals(result, Vec(expected))
    }

  // __heq2, __hlt2_mask, __hisnan and __hisnan2 (cuda_fp16.hpp:1986, 2128, 3232, 3218), and their bfloat16 forms on
  // sm_90 (cuda_bf16.hpp).
  for (
    (spelling, a, b, expected) <- Vec(
      ("set.eq.f16x2.f16x2", 0x3c004000L, 0x3c003c00L, 0x3c000000L),
      ("set.lt.u32.f16x2", 0x3c004000L, 0x40004000L, 0xffff0000L),
      ("set.nan.f16x2.f16x2", 0x7e003c00L, 0x7e003c00L, 0x3c000000L),
      ("set.nan.bf16x2.bf16x2", 0x7fc03f80L, 0x7fc03f80L, 0x3f800000L)
    )
  )
    test(s"$spelling compares each half") {
      val result = run(
        ".reg .b32 %a, %b, %d;",
        s"""ld.global.u32 %a, [%in]; ld.global.u32 %b, [%in+8];
           |$spelling %d, %a, %b; st.global.u64 [%out], 0; st.global.b32 [%out], %d;""".stripMargin,
        Vec(a, b),
        1
      )
      assertEquals(result, Vec(expected))
    }

  for (
    (spelling, a, expected) <- Vec(
      ("set.nan.f16.f16", 0x7e00L, 0x3c00L),
      ("set.nan.f16.f16", 0x3c00L, 0L),
      ("set.nan.bf16.bf16", 0x7fc0L, 0x3f80L)
    )
  )
    test(f"$spelling of 0x$a%04x") {
      val result = run(
        ".reg .b16 %a, %d;",
        s"""ld.global.u16 %a, [%in];
           |$spelling %d, %a, %a; st.global.u64 [%out], 0; st.global.b16 [%out], %d;""".stripMargin,
        Vec(a),
        1
      )
      assertEquals(result, Vec(expected))
    }

  // CUDA 11.8's SIMD intrinsics (sm_20_intrinsics.hpp), built for the sm_50 to sm_70 conformance artifacts.
  for ((comparison, expected) <- Vec("ge" -> 0xffffffffL, "lt" -> 0L, "gt" -> 0xffffffffL))
    test(s"set.$comparison.s32.s32 writes all ones when true") {
      val result = run(
        ".reg .b32 %a, %b, %d;",
        s"""ld.global.u32 %a, [%in]; ld.global.u32 %b, [%in+8];
           |set.$comparison.s32.s32 %d, %a, %b; st.global.u64 [%out], 0; st.global.u32 [%out], %d;""".stripMargin,
        Vec(5L, 0xfffffffdL),
        1
      )
      assertEquals(result, Vec(expected))
    }

  // clang emits isspacep for every generic pointer it cannot place (__clang_cuda_runtime_wrapper.h:306).
  test("isspacep tells global, shared and local addresses apart") {
    val result = run(
      """.shared .align 4 .u32 cell[1];
        |.local .align 4 .u32 slot[1];
        |.reg .u64 %s, %l;
        |.reg .pred %p;
        |.reg .b32 %r;""".stripMargin,
      """cvta.shared.u64 %s, cell;
        |cvta.local.u64 %l, slot;
        |isspacep.global %p, %out; selp.u32 %r, 1, 0, %p; st.global.u64 [%out], 0; st.global.u32 [%out], %r;
        |isspacep.shared %p, %out; selp.u32 %r, 1, 0, %p; st.global.u64 [%out+8], 0; st.global.u32 [%out+8], %r;
        |isspacep.shared %p, %s; selp.u32 %r, 1, 0, %p; st.global.u64 [%out+16], 0; st.global.u32 [%out+16], %r;
        |isspacep.global %p, %s; selp.u32 %r, 1, 0, %p; st.global.u64 [%out+24], 0; st.global.u32 [%out+24], %r;
        |isspacep.local %p, %l; selp.u32 %r, 1, 0, %p; st.global.u64 [%out+32], 0; st.global.u32 [%out+32], %r;
        |isspacep.global %p, %l; selp.u32 %r, 1, 0, %p; st.global.u64 [%out+40], 0; st.global.u32 [%out+40], %r;""".stripMargin,
      Vec.empty,
      6
    )
    assertEquals(result, Vec(1L, 0L, 1L, 0L, 1L, 0L))
  }

  // Inline in int8 quantisation code such as CUTLASS's; the second source fills the low lane.
  test("cvt.pack.sat clamps two words to the narrow type and packs them over the third source") {
    val result = run(
      ".reg .b32 %a, %b, %c, %d;",
      """ld.global.u32 %a, [%in]; ld.global.u32 %b, [%in+8]; ld.global.u32 %c, [%in+16];
        |cvt.pack.sat.s8.s32.b32 %d, %a, %b, %c; st.global.u64 [%out], 0; st.global.u32 [%out], %d;
        |cvt.pack.sat.u16.s32 %d, %b, %a; st.global.u64 [%out+8], 0; st.global.u32 [%out+8], %d;""".stripMargin,
      Vec(300L, 0xffffff38L, 0x12345678L),
      2
    )
    assertEquals(result, Vec(0x56787f80L, 0x0000012cL))
  }

  // __nv_cvt_float2_to_fp8x2, __nv_cvt_halfraw2_to_fp8x2 and __nv_cvt_fp8x2_to_halfraw2 (cuda_fp8.hpp:252-572), sm_89 on:
  // round to nearest even, finite values beyond the format saturate, and the first source fills the upper byte.
  for (
    (format, packed, half) <- Vec(
      ("e4m3x2", 0x3cc0L, 0x3e00c000L),
      ("e5m2x2", 0x3ec0L, 0x3e00c000L)
    )
  )
    test(s"cvt between f32, f16x2 and $format") {
      val result = run(
        ".reg .f32 %a, %b, %big; .reg .b16 %d; .reg .b32 %h, %w;",
        s"""ld.global.f32 %a, [%in]; ld.global.f32 %b, [%in+8]; ld.global.f32 %big, [%in+16];
           |cvt.rn.satfinite.$format.f32 %d, %a, %b; st.global.u64 [%out], 0; st.global.b16 [%out], %d;
           |cvt.rn.f16x2.$format %w, %d; st.global.u64 [%out+8], 0; st.global.b32 [%out+8], %w;
           |mov.b32 %h, 0x3e00c000; cvt.rn.satfinite.$format.f16x2 %d, %h; st.global.u64 [%out+16], 0; st.global.b16 [%out+16], %d;
           |cvt.rn.satfinite.$format.f32 %d, %big, %big; st.global.u64 [%out+24], 0; st.global.b16 [%out+24], %d;""".stripMargin,
        Vec(f32(1.5f), f32(-2.0f), f32(1.0e6f)),
        4
      )
      assertEquals(result, Vec(packed, half, packed, if (format == "e4m3x2") 0x7e7eL else 0x7b7bL))
    }
}
