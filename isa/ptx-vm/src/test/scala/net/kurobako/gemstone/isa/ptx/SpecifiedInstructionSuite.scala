package net.kurobako.gemstone.isa.ptx

import execution.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

/** Valid PTX that nvcc and clang do not emit for ordinary source but inline assembly and hand-written PTX use, with the
  * results the PTX ISA 13.3 pseudocode gives. Where ptxas lowers an instruction differently from the pseudocode from
  * sm_70, the inputs keep to where both agree, or the results are the hardware's where they cannot;
  * conformance/unified/src/cuda/video.cu runs the same forms on hardware.
  */
class SpecifiedInstructionSuite extends munit.FunSuite {
  private val Sections = java.util.regex.Pattern.compile("; ")

  private type Expected = (Long, Long, Long) => Long

  /** The 32-bit words `body` stores from `%out`, after running on one thread with `%in` addressing `inputs`. */
  private def run(registers: String, body: String, inputs: Vec[Long], outputs: Int, target: String = "sm_90")(using
      munit.Location
  ): Vec[Long] =
    runWords(
      "specified",
      target,
      s""".reg .b32 %a, %b, %c, %d;
        |  $registers""".stripMargin,
      s"""ld.global.u32 %a, [%in];
        |  ld.global.u32 %b, [%in+4];
        |  ld.global.u32 %c, [%in+8];
        |  $body""".stripMargin,
      inputs,
      outputs,
      bytes = 4
    )

  private val words = Vec(
    0x00000000L, 0x00000001L, 0x7fffffffL, 0x80000000L, 0xffffffffL, 0x01020304L, 0x807f80ffL,
    0x7f017f80L, 0x0000ffffL, 0xffff0000L, 0x12345678L, 0xdeadbeefL, 0x00000021L, 0x0000001fL
  )

  private def addend(a: Long, b: Long): Long = ((a * 0x9e3779b9L) ^ (b >>> 3) ^ 0x5a5a00a5L) & 0xffffffffL

  private def pairs: Vec[(Long, Long)] = for {
    a <- words
    b <- words
  } yield (a, b)

  // The pseudocode's partSelectSignExtend: a byte, half-word or word, sign- or zero-extended.
  private def part(word: Long, select: String, signed: Boolean): Long = {
    val (offset, bits) = select match {
      case "b0" => (0, 8)
      case "b1" => (8, 8)
      case "b2" => (16, 8)
      case "b3" => (24, 8)
      case "h0" => (0, 16)
      case "h1" => (16, 16)
      case _    => (0, 32)
    }
    val field = (word >>> offset) & ((1L << bits) - 1L)
    if (signed && (field >>> (bits - 1)) != 0L) field - (1L << bits) else field
  }

  private def clamp(value: Long, signed: Boolean, bits: Int): Long =
    if (signed) value.max(-(1L << (bits - 1))).min((1L << (bits - 1)) - 1L) else value.max(0L).min((1L << bits) - 1L)

  private def merge(value: Long, c: Long, select: String): Long = {
    val (offset, bits) = select match {
      case "b0" => (0, 8)
      case "b1" => (8, 8)
      case "b2" => (16, 8)
      case "b3" => (24, 8)
      case "h0" => (0, 16)
      case _    => (16, 16)
    }
    val mask = ((1L << bits) - 1L) << offset
    ((value << offset) & mask) | (c & ~mask & 0xffffffffL)
  }

  private def s34(value: Long): Long = (value << 30) >> 30

  // 9.7.19.1: the s34 intermediate, saturated to the destination part, then a second operation or a merge.
  private val scalar: Vec[(String, Expected)] = Vec(
    "vadd.u32.u32.u32 %d, %a, %b;" ->
      ((a, b, _) =>
        part(a, "", signed = false) + part(b, "", signed = false)
      ),
    "vadd.s32.s32.s32.sat %d, %a.h1, %b.b2;" ->
      ((a, b, _) =>
        clamp(part(a, "h1", signed = true) + part(b, "b2", signed = true), signed = true, bits = 32)
      ),
    "vadd.u32.u32.u32.add %d, %a.b0, %b.b3, %c;" ->
      ((a, b, c) =>
        part(a, "b0", signed = false) + part(b, "b3", signed = false) + c
      ),
    "vsub.s32.u32.s32.min %d, %a.h0, %b.h1, %c;" ->
      ((a, b, c) =>
        (part(a, "h0", signed = false) - part(b, "h1", signed = true)).min(c.toInt.toLong)
      ),
    "vabsdiff.u32.u32.u32.max %d, %a, %b.b1, %c;" ->
      ((a, b, c) => (part(a, "", signed = false) - part(b, "b1", signed = false)).abs.max(c)),
    "vmin.s32.s32.s32 %d, %a, %b;" ->
      ((a, b, _) =>
        part(a, "", signed = true).min(part(b, "", signed = true))
      ),
    "vmax.u32.u32.s32.sat %d, %a.b1, %b.h1;" ->
      ((a, b, _) =>
        clamp(part(a, "b1", signed = false).max(part(b, "h1", signed = true)), signed = false, bits = 32)
      ),
    "mov.b32 %d, %c; vadd.u32.u32.u32 %d.b1, %a, %b, %d;"           -> ((a, b, c) => merge(a + b, c, "b1")),
    "mov.b32 %d, %c; vadd.u32.u32.u32.sat %d.h0, %a.h1, %b.h0, %d;" ->
      ((a, b, c) =>
        merge(clamp(part(a, "h1", signed = false) + part(b, "h0", signed = false), signed = false, bits = 16), c, "h0")
      ),
    "mov.b32 %d, %c; vabsdiff.s32.s32.s32.sat %d.h1, %a.b0, %b.b2, %d;" ->
      ((a, b, c) =>
        merge(
          clamp((part(a, "b0", signed = true) - part(b, "b2", signed = true)).abs, signed = true, bits = 16),
          c,
          "h1"
        )
      ),
    "vmin.s32.s32.s32.sat.add %d, %a, %b, %c;" ->
      ((a, b, c) =>
        clamp(part(a, "", signed = true).min(part(b, "", signed = true)), signed = true, bits = 32) + c.toInt
      ),
    "vshl.u32.u32.u32.clamp %d, %a, %b;"            -> ((a, b, _) => s34(a << b.min(32L))),
    "vshr.s32.s32.u32.wrap %d, %a, %b;"             -> ((a, b, _) => part(a, "", signed = true) >> (b & 31L)),
    "vshr.u32.u32.u32.clamp.add %d, %a, %b.b0, %c;" -> ((a, b, c) => (a >> part(b, "b0", signed = false).min(32L)) + c),
    "vshl.s32.s32.u32.sat.wrap %d, %a.h0, %b.b0;"   ->
      ((a, b, _) =>
        clamp(s34(part(a, "h0", signed = true) << (part(b, "b0", signed = false) & 31L)), signed = true, bits = 32)
      ),
    "vset.u32.s32.lt %d, %a, %b;" ->
      ((a, b, _) =>
        if (part(a, "", signed = false) < part(b, "", signed = true)) 1L else 0L
      ),
    "vset.u32.u32.ne.add %d, %a.b2, %b.b2, %c;" ->
      ((a, b, c) =>
        (if (part(a, "b2", signed = false) != part(b, "b2", signed = false)) 1L else 0L) + c
      ),
    "mov.b32 %d, %c; vset.s32.s32.ge %d.h1, %a, %b, %d;" ->
      ((a, b, c) =>
        merge(if (part(a, "", signed = true) >= part(b, "", signed = true)) 1L else 0L, c, "h1")
      )
  )

  for ((spelling, expected) <- scalar) test(s"$spelling") {
    val instruction = Sections.split(spelling).last
    pairs.foreach { (a, b) =>
      val c = addend(a, b)
      assertEquals(
        run("", s"$spelling st.global.u32 [%out], %d;", Vec(a, b, c), 1),
        Vec(expected(a, b, c) & 0xffffffffL),
        f"$instruction of 0x$a%x, 0x$b%x and 0x$c%x"
      )
    }
  }

  // 9.7.19.1.3: tmp[127:0] = ta * tb, negated as ~tmp + 1; c extended by the final sign; then scaled and saturated.
  private def videoMad(
      a: BigInt,
      b: BigInt,
      c: Long,
      signedFinal: Boolean,
      negateProduct: Boolean,
      negateC: Boolean,
      plusOne: Boolean,
      shift: Int,
      saturate: Boolean,
      signedAddend: Boolean = false
  ): Long = {
    val addend  = if (negateC) ~c & 0xffffffffL else c
    val wide    = if (signedFinal || signedAddend) BigInt(addend.toInt) else BigInt(addend)
    val product = if (negateProduct) -(a * b) - 1 else a * b
    val sum     = product + wide + (if (plusOne || negateProduct || negateC) 1 else 0)
    val result  = ((sum >> shift) & ((BigInt(1) << 64) - 1)).toLong
    if (!saturate) result & 0xffffffffL
    else if (signedFinal) result.max(Int.MinValue.toLong).min(Int.MaxValue.toLong) & 0xffffffffL
    else if (java.lang.Long.compareUnsigned(result, 0xffffffffL) > 0) 0xffffffffL
    else result
  }

  private val mads: Vec[(String, Expected)] = Vec(
    "vmad.s32.s32.u32 %d, %a.h0, %b.b1, %c;" ->
      ((a, b, c) =>
        videoMad(
          part(a, "h0", signed = true),
          part(b, "b1", signed = false),
          c,
          signedFinal = true,
          negateProduct = false,
          negateC = false,
          plusOne = false,
          shift = 0,
          saturate = false
        )
      ),
    "vmad.u32.u32.u32.sat.shr15 %d, -%a.h1, %b.h0, %c;" ->
      ((a, b, c) =>
        videoMad(
          part(a, "h1", signed = false),
          part(b, "h0", signed = false),
          c,
          signedFinal = true,
          negateProduct = true,
          negateC = false,
          plusOne = false,
          shift = 15,
          saturate = true
        )
      ),
    // the pseudocode zero-extends an unsigned addend, which the hardware sign-extends from sm_70
    "vmad.u32.u32.u32.shr7 %d, %a.h0, %b.h0, %c;" ->
      ((a, b, c) =>
        videoMad(
          part(a, "h0", signed = false),
          part(b, "h0", signed = false),
          c,
          signedFinal = false,
          negateProduct = false,
          negateC = false,
          plusOne = false,
          shift = 7,
          saturate = false,
          signedAddend = true
        )
      ),
    "vmad.s32.s32.s32.sat %d, %a, %b, -%c;" ->
      ((a, b, c) =>
        videoMad(
          part(a, "", signed = true),
          part(b, "", signed = true),
          c,
          signedFinal = true,
          negateProduct = false,
          negateC = true,
          plusOne = false,
          shift = 0,
          saturate = true
        )
      ),
    "vmad.u32.u32.u32.po %d, %a.b0, %b.b0, %c;" ->
      ((a, b, c) =>
        videoMad(
          part(a, "b0", signed = false),
          part(b, "b0", signed = false),
          c,
          signedFinal = false,
          negateProduct = false,
          negateC = false,
          plusOne = true,
          shift = 0,
          saturate = false
        )
      ),
    // the pseudocode zero-extends an unsigned addend, which the hardware sign-extends from sm_70
    "vmad.u32.u32.u32.sat %d, %a, %b, %c;" ->
      ((a, b, c) =>
        videoMad(
          part(a, "", signed = false),
          part(b, "", signed = false),
          c,
          signedFinal = false,
          negateProduct = false,
          negateC = false,
          plusOne = false,
          shift = 0,
          saturate = true,
          signedAddend = true
        )
      )
  )

  for ((spelling, expected) <- mads) test(s"$spelling") {
    pairs.foreach { (a, b) =>
      val c = addend(a, b)
      assertEquals(
        run("", s"$spelling st.global.u32 [%out], %d;", Vec(a, b, c), 1),
        Vec(expected(a, b, c) & 0xffffffffL),
        f"$spelling of 0x$a%x, 0x$b%x and 0x$c%x"
      )
    }
  }

  private def narrow(value: Long, signed: Boolean): Long = if (signed) (value << 40) >> 40 else value & 0xffffffL

  // 9.7.1.6 and 9.7.1.7: t = a * b of 24-bit values; t<31..0> or t<47..16>, plus c; .sat clamps the signed sum.
  private val integers: Vec[(String, Expected)] = Vec(
    "mul24.lo.s32 %d, %a, %b;"     -> ((a, b, _) => narrow(a, signed = true) * narrow(b, signed = true)),
    "mul24.lo.u32 %d, %a, %b;"     -> ((a, b, _) => narrow(a, signed = false) * narrow(b, signed = false)),
    "mul24.hi.s32 %d, %a, %b;"     -> ((a, b, _) => (narrow(a, signed = true) * narrow(b, signed = true)) >> 16),
    "mul24.hi.u32 %d, %a, %b;"     -> ((a, b, _) => (narrow(a, signed = false) * narrow(b, signed = false)) >> 16),
    "mad24.lo.s32 %d, %a, %b, %c;" -> ((a, b, c) => narrow(a, signed = true) * narrow(b, signed = true) + c),
    "mad24.hi.u32 %d, %a, %b, %c;" ->
      ((a, b, c) =>
        ((narrow(a, signed = false) * narrow(b, signed = false)) >> 16) + c
      ),
    "mad24.hi.sat.s32 %d, %a, %b, %c;" ->
      ((a, b, c) =>
        clamp(
          ((narrow(a, signed = true) * narrow(b, signed = true)) >> 16).toInt.toLong + c.toInt,
          signed = true,
          bits = 32
        )
      ),
    "mad.hi.u32 %d, %a, %b, %c;"     -> ((a, b, c) => ((a * b) >>> 32) + c),
    "mad.hi.s32 %d, %a, %b, %c;"     -> ((a, b, c) => ((a.toInt.toLong * b.toInt) >> 32) + c),
    "mad.hi.sat.s32 %d, %a, %b, %c;" ->
      ((a, b, c) =>
        clamp(((a.toInt.toLong * b.toInt) >> 32) + c.toInt, signed = true, bits = 32)
      ),
    "slct.s32.s32 %d, %a, %b, %c;" -> ((a, b, c) => if (c.toInt >= 0) a else b),
    "slct.b32.s32 %d, %c, %b, %a;" -> ((a, b, c) => if (a.toInt >= 0) c else b)
  )

  for ((spelling, expected) <- integers) test(s"$spelling") {
    pairs.foreach { (a, b) =>
      val c = addend(a, b)
      assertEquals(
        run("", s"$spelling st.global.u32 [%out], %d;", Vec(a, b, c), 1),
        Vec(expected(a, b, c) & 0xffffffffL),
        f"$spelling of 0x$a%x, 0x$b%x and 0x$c%x"
      )
    }
  }

  test("mad.wide.s32, mad.wide.s16 and mad.wide.u16 sign- or zero-extend before the full product") {
    pairs.foreach { (a, b) =>
      val c      = addend(a, b)
      val wide   = (c << 32) | (a ^ b)
      val result = run(
        ".reg .b64 %w, %r; .reg .b16 %x, %y;",
        s"""mov.b64 %w, $wide; mad.wide.s32 %r, %a, %b, %w; st.global.u64 [%out], %r;
           |cvt.u16.u32 %x, %a; cvt.u16.u32 %y, %b;
           |mad.wide.s16 %d, %x, %y, %c; st.global.u32 [%out+8], %d;
           |mad.wide.u16 %d, %x, %y, %c; st.global.u32 [%out+12], %d;""".stripMargin,
        Vec(a, b, c),
        4
      )
      val product = a.toInt.toLong * b.toInt + wide
      assertEquals(
        result,
        Vec(
          product & 0xffffffffL,
          product >>> 32,
          (a.toShort.toLong * b.toShort + c) & 0xffffffffL,
          ((a & 0xffffL) * (b & 0xffffL) + c) & 0xffffffffL
        ),
        f"0x$a%x 0x$b%x 0x$c%x"
      )
    }
  }

  private val floats = Vec(0x00000000L, 0x80000000L, 0x3f800000L, 0xbf800000L, 0x00000001L, 0x807fffffL, 0x00800000L,
    0x7f7fffffL, 0x7f800000L, 0xff800000L, 0x7fc00000L, 0xffc00001L)

  private val floatTests = Vec("finite", "infinite", "number", "notanumber", "normal", "subnormal")

  private def isNaN(bits: Long): Boolean = IEEE754.isNaN(bits, 32)

  test("testp classifies binary32 and binary64 values, zeros as normal") {
    floats.foreach { bits =>
      val magnitude = bits & 0x7fffffffL
      val exponent  = bits & 0x7f800000L
      val wide      = java.lang.Double.doubleToRawLongBits(java.lang.Float.intBitsToFloat(bits.toInt).toDouble)
      val body      = floatTests.zipWithIndex.map { (test, index) =>
        s"""testp.$test.f32 %p, %f; selp.u32 %d, 1, 0, %p; st.global.u32 [%out+${index * 8}], %d;
           |testp.$test.f64 %p, %fd; selp.u32 %d, 1, 0, %p; st.global.u32 [%out+${index * 8 + 4}], %d;""".stripMargin
      }.mkString("\n")
      val result = run(
        ".reg .pred %p; .reg .f32 %f; .reg .f64 %fd;",
        s"mov.b32 %f, %a; mov.b64 %fd, $wide;\n$body",
        Vec(bits, 0L, 0L),
        12
      )
      val finite = magnitude < 0x7f800000L
      val f32    = Vec(
        finite,
        magnitude == 0x7f800000L,
        !isNaN(bits),
        isNaN(bits),
        finite && (exponent != 0L || magnitude == 0L),
        exponent == 0L && magnitude != 0L
      )
      val f64 = f32.updated(4, finite).updated(5, false)
      assertEquals(
        result,
        f32.zip(f64).flatMap((single, double) => Vec(if (single) 1L else 0L, if (double) 1L else 0L)),
        f"0x$bits%08x"
      )
    }
  }

  test("slct on a binary32 condition takes NaN as negative and either zero, and a flushed subnormal, as not") {
    floats.foreach { bits =>
      val result = run(
        ".reg .f32 %f;",
        """mov.b32 %f, %c; slct.u32.f32 %d, %a, %b, %f; st.global.u32 [%out], %d;
          |slct.ftz.u32.f32 %d, %a, %b, %f; st.global.u32 [%out+4], %d;""".stripMargin,
        Vec(11L, 22L, bits),
        2
      )
      val magnitude = bits & 0x7fffffffL
      val negative  = (bits & 0x80000000L) != 0L
      assertEquals(
        result,
        Vec(
          if (!isNaN(bits) && (!negative || magnitude == 0L)) 11L else 22L,
          if (!isNaN(bits) && (!negative || magnitude < 0x00800000L)) 11L else 22L
        ),
        f"0x$bits%08x"
      )
    }
  }

  private val flagValues = Vec(0L, 1L)

  // 9.7.6.1 and 9.7.6.2: the comparison combined with {!}c by the Boolean operator.
  test("set and setp combine the comparison with a predicate by and, or and xor") {
    for {
      (a, b) <- pairs
      flag   <- flagValues
    } {
      val result = run(
        ".reg .pred %p, %q; .reg .f32 %x, %y, %r;",
        """setp.ne.u32 %p, %c, 0; mov.b32 %x, %a; mov.b32 %y, %b;
          |set.lt.and.u32.s32 %d, %a, %b, %p; st.global.u32 [%out], %d;
          |set.ltu.or.f32.f32 %r, %x, %y, !%p; mov.b32 %d, %r; st.global.u32 [%out+4], %d;
          |set.ge.xor.s32.f32 %d, %x, %y, %p; st.global.u32 [%out+8], %d;
          |setp.hi.and.u32 %q, %a, %b, !%p; selp.u32 %d, 1, 0, %q; st.global.u32 [%out+12], %d;
          |setp.equ.or.f32 %q, %x, %y, %p; selp.u32 %d, 1, 0, %q; st.global.u32 [%out+16], %d;
          |setp.ls.xor.u32 %q, %a, %b, %p; selp.u32 %d, 1, 0, %q; st.global.u32 [%out+20], %d;
          |set.lo.u32.u32 %d, %a, %b; st.global.u32 [%out+24], %d;""".stripMargin,
        Vec(a, b, flag),
        7
      )
      val p                     = flag != 0L
      val (x, y)                = (java.lang.Float.intBitsToFloat(a.toInt), java.lang.Float.intBitsToFloat(b.toInt))
      val nan                   = isNaN(a) || isNaN(b)
      def truth(value: Boolean) = if (value) 0xffffffffL else 0L
      assertEquals(
        result,
        Vec(
          truth(a.toInt < b.toInt && p),
          if (nan || x < y || !p) 0x3f800000L else 0L,
          truth((!nan && x >= y) != p),
          if (a > b && !p) 1L else 0L,
          if (nan || x == y || p) 1L else 0L,
          if ((a <= b) != p) 1L else 0L,
          truth(a < b)
        ),
        f"0x$a%x 0x$b%x $p"
      )
    }
  }

  // 9.7.9.22: tf32 keeps the upper 19 bits; ptxas rounds .rna by adding half a step to finite bit patterns, so a NaN
  // payload is truncated, and .satfinite takes the top exponent down one step.
  // add, sub and mul .sat.f16x2 clamp each half to [0, 1], as __hadd2_sat, __hsub2_sat and __hmul2_sat use them.
  test("saturated f16x2 sums, differences and products clamp each half to the unit interval") {
    val result = run(
      "",
      """add.sat.f16x2 %d, %a, %b; st.global.u32 [%out], %d;
        |sub.sat.f16x2 %d, %a, %b; st.global.u32 [%out+4], %d;
        |mul.sat.f16x2 %d, %a, %b; st.global.u32 [%out+8], %d;
        |mul.sat.f16x2 %d, %c, %c; st.global.u32 [%out+12], %d;""".stripMargin,
      // a = (0.75, -0.5), b = (0.5, 0.25), c = (-0.0, NaN)
      Vec(0x3a00b800L, 0x38003400L, 0x80007e00L),
      4
    )
    // halves high then low: negative results and NaN clamp to +0
    assertEquals(result, Vec(0x3c000000L, 0x34000000L, 0x36000000L, 0L))
  }

  // neg and abs of half pairs act on each half's sign bit, as __hneg2 and __habs2 in cuda_fp16.hpp use them.
  test("neg and abs of f16x2 and bf16x2 pairs negate and clear each half's sign") {
    val result = run(
      "",
      """neg.f16x2 %d, %a; st.global.u32 [%out], %d;
        |abs.f16x2 %d, %a; st.global.u32 [%out+4], %d;
        |neg.bf16x2 %d, %b; st.global.u32 [%out+8], %d;
        |abs.bf16x2 %d, %b; st.global.u32 [%out+12], %d;""".stripMargin,
      Vec(0xbc000001L, 0x3f80c000L, 0L),
      4
    )
    assertEquals(result, Vec(0x3c008001L, 0x3c000001L, 0xbf804000L, 0x3f804000L))
  }

  // bf16 has binary32's 8-bit exponent: infinities and finite values whose top bits would make a binary16 NaN
  test("neg and abs of bf16x2 pairs keep infinities and wide-exponent finite values") {
    val result = run(
      "",
      """neg.bf16x2 %d, %a; st.global.u32 [%out], %d;
        |abs.bf16x2 %d, %a; st.global.u32 [%out+4], %d;
        |neg.bf16x2 %d, %b; st.global.u32 [%out+8], %d;
        |abs.bf16x2 %d, %b; st.global.u32 [%out+12], %d;""".stripMargin,
      Vec(0x7f80ff80L, 0x7c01fc00L, 0L),
      4
    )
    assertEquals(result, Vec(0xff807f80L, 0x7f807f80L, 0xfc017c00L, 0x7c017c00L))
  }

  // 6.5.1: a register wider than the destination format holds the result extended by the format's signedness.
  test("integer cvt into a wider register extends by the destination format") {
    val result = run(
      "",
      """cvt.sat.s8.s32 %d, %a; st.global.u32 [%out], %d;
        |cvt.s16.u32 %d, %b; st.global.u32 [%out+4], %d;
        |cvt.sat.u8.s32 %d, %c; st.global.u32 [%out+8], %d;
        |cvt.s8.s32 %d, %c; st.global.u32 [%out+12], %d;""".stripMargin,
      Vec(0x80000000L, 0x00018000L, 0x000001f0L),
      4,
      "sm_70"
    )
    assertEquals(result, Vec(0xffffff80L, 0xffff8000L, 0xffL, 0xfffffff0L))
  }

  test("cvt.rna, cvt.rn and cvt.rz to tf32 round at the 13th bit") {
    val inputs = floats ++ Vec(0x3f801000L, 0x3f803000L, 0x3f800fffL, 0xbf801001L, 0x7f7ff000L, 0x7f800001L)
    inputs.foreach { bits =>
      val result = run(
        ".reg .f32 %f;",
        """mov.b32 %f, %a;
          |cvt.rna.tf32.f32 %d, %f; st.global.u32 [%out], %d;
          |cvt.rna.satfinite.tf32.f32 %d, %f; st.global.u32 [%out+4], %d;
          |cvt.rn.tf32.f32 %d, %f; st.global.u32 [%out+8], %d;
          |cvt.rz.tf32.f32 %d, %f; st.global.u32 [%out+12], %d;
          |cvt.rn.relu.satfinite.tf32.f32 %d, %f; st.global.u32 [%out+16], %d;""".stripMargin,
        Vec(bits, 0L, 0L),
        5,
        "sm_100"
      )
      val magnitude = bits & 0x7fffffffL
      val sign      = bits & 0x80000000L
      val away      = if (magnitude >= 0x7f800000L) bits & 0xffffe000L else (bits + 0x1000L) & 0xffffe000L
      val even      = {
        val dropped = magnitude & 0x1fffL
        val kept    = magnitude & ~0x1fffL
        sign | (if (dropped > 0x1000L || (dropped == 0x1000L && (kept & 0x2000L) != 0L)) kept + 0x2000L else kept)
      }
      val relu = if (sign != 0L) 0L else if ((even & 0x7fffffffL) >= 0x7f800000L) 0x7f7fe000L else even
      assertEquals(
        result,
        if (isNaN(bits)) Vec(away, away - 0x2000L, 0x7fffffffL, 0x7fffffffL, 0x7fffffffL)
        else
          Vec(
            away,
            if ((away & 0x7f800000L) == 0x7f800000L) away - 0x2000L else away,
            if (magnitude == 0x7f800000L) bits else even,
            sign | (if (magnitude == 0x7f800000L) 0x7f800000L else magnitude & ~0x1fffL),
            relu
          ),
        f"0x$bits%08x"
      )
    }
  }

  // 9.7.9.22: .rs rounds toward zero unless the random bits added to the discarded bits carry out of them; binary16
  // takes the low 13 bits of each half of rbits, bfloat16 each half whole; beyond the largest binade it overflows.
  test("cvt.rs rounds binary16 and bfloat16 pairs by their random bits") {
    val one   = 0x3f800000L
    val cases = Vec(
      // 1 + 2^-11: half a binary16 step above 1, so the carry needs half of the 13 random bits.
      (one | 0x1000L, 0x0fffL, 0x3c00L),
      (one | 0x1000L, 0x1000L, 0x3c01L),
      (one | 0x1fffL, 0x0001L, 0x3c01L),
      (one | 0x1fffL, 0x0000L, 0x3c00L),
      (0xbf801000L, 0x1000L, 0xbc01L),
      (0x7f7fffffL, 0x0000L, 0x7c00L),
      (0x477fefffL, 0x1fffL, 0x7c00L),
      (0x7fc00000L, 0x0000L, 0x7fffL)
    )
    cases.foreach { (bits, random, expected) =>
      val result = run(
        ".reg .f32 %f;",
        """mov.b32 %f, %a;
          |cvt.rs.f16x2.f32 %d, %f, %f, %b; st.global.u32 [%out], %d;
          |cvt.rs.satfinite.f16x2.f32 %d, %f, %f, %b; st.global.u32 [%out+4], %d;
          |cvt.rs.bf16x2.f32 %d, %f, %f, %c; st.global.u32 [%out+8], %d;""".stripMargin,
        Vec(bits, (random << 16) | random | 0xe000e000L, 0x80000000L | (bits & 0xffffL)),
        3,
        "sm_100a"
      )
      val saturated = if (expected == 0x7c00L) 0x7bffL else expected
      val bf16High  = if ((bits & 0x7fffffffL) > 0x7f800000L) 0x7fffL
      else (bits >>> 16) + (if ((bits & 0xffffL) + 0x8000L >= 0x10000L) 1L else 0L)
      val bf16Low = if ((bits & 0x7fffffffL) > 0x7f800000L) 0x7fffL
      else (bits >>> 16) + (if ((bits & 0xffffL) * 2L >= 0x10000L) 1L else 0L)
      assertEquals(
        result,
        Vec(expected << 16 | expected, saturated << 16 | saturated, bf16High << 16 | bf16Low),
        f"0x$bits%08x with 0x$random%04x"
      )
    }
  }

  // 9.7.1.22: the low b bits of a, extended from bit b-1; b of 0 gives 0, and 32 or more keeps a under .clamp and wraps
  // otherwise.
  test("szext extends a field of a width the second source gives") {
    for (
      (mode, tpe, a, b, expected) <- Vec(
        ("clamp", "s32", 0xf0L, 8L, 0xfffffff0L),
        ("clamp", "u32", 0xf0L, 8L, 0xf0L),
        ("clamp", "s32", 0x12345678L, 40L, 0x12345678L),
        ("wrap", "u32", 0xffffffffL, 0L, 0L),
        ("wrap", "s32", 0xffffL, 48L, 0xffffffffL),
        ("wrap", "u32", 0x12345678L, 32L, 0L),
        ("clamp", "s32", 0x7fL, 7L, 0xffffffffL)
      )
    )
      assertEquals(
        run("", s"szext.$mode.$tpe %d, %a, %b; st.global.u32 [%out], %d;", Vec(a, b, 0L), 1, "sm_70"),
        Vec(expected),
        s"szext.$mode.$tpe 0x${a.toHexString}, $b"
      )
  }

  // 9.7.1.5: the low or high 64 bits of the carry-less product, then exclusive-or with c.
  test("clmad.lo and clmad.hi give the halves of the carry-less product added without carries") {
    val result = run(
      ".reg .b64 %x, %y, %z, %r;",
      """mov.b64 %x, 3; mov.b64 %y, 5; mov.b64 %z, 16;
        |clmad.lo.u64 %r, %x, %y, %z; st.global.u64 [%out], %r;
        |mov.b64 %x, 0x8000000000000001; mov.b64 %y, 0xc000000000000003;
        |clmad.lo.u64 %r, %x, %y, 0; st.global.u64 [%out+8], %r;
        |clmad.hi.u64 %r, %x, %y, 1; st.global.u64 [%out+16], %r;""".stripMargin,
      Vec(0L, 0L, 0L),
      6,
      "sm_80"
    )
    // (2^63 + 1) * (2^63 + 2^62 + 3) carry-less: 2^126 + 2^125 + 2^64 + 2^63 xor 2^63 + 2^62 + 3
    assertEquals(result, Vec(0x1fL, 0L, 0x00000003L, 0x40000000L, 0L, 0x60000000L))
  }

  // 9.7.3.11: three-input min and max take the pair first and then the third, of magnitudes under .abs.
  test("min and max of three binary32 values") {
    val result = run(
      ".reg .f32 %x, %y, %z, %r;",
      """mov.b32 %x, %a; mov.b32 %y, %b; mov.b32 %z, %c;
        |min.f32 %r, %x, %y, %z; st.global.f32 [%out], %r;
        |max.f32 %r, %x, %y, %z; st.global.f32 [%out+4], %r;
        |min.abs.f32 %r, %x, %y, %z; st.global.f32 [%out+8], %r;
        |max.NaN.f32 %r, %x, %y, %z; st.global.f32 [%out+12], %r;
        |max.abs.f32 %r, %x, %y, %z; st.global.f32 [%out+16], %r;""".stripMargin,
      Vec(0xc0400000L, 0x7fc00000L, 0x3f800000L),
      5,
      "sm_100"
    )
    // -3, NaN and 1: the NaN drops out except under .NaN
    assertEquals(result, Vec(0xc0400000L, 0x3f800000L, 0x3f800000L, 0x7fffffffL, 0x40400000L))
  }

  // 9.7.8.6: the predicate is whether the table's result is nonzero, combined with q.
  test("lop3 with a Boolean operation sets a predicate from its result") {
    val result = run(
      ".reg .pred %p, %q, %t;",
      """setp.ne.u32 %q, %c, 0;
        |lop3.and.b32 %d|%p, %a, %b, 0, 0x80, %q; selp.u32 %d, 1, 0, %p; st.global.u32 [%out], %d;
        |lop3.or.b32 _|%p, %a, %b, 0, 0x80, %q; selp.u32 %d, 1, 0, %p; st.global.u32 [%out+4], %d;
        |lop3.or.b32 %d|%t, %a, %b, %c, 0x96, !%q; st.global.u32 [%out+8], %d; selp.u32 %d, 1, 0, %t;
        |st.global.u32 [%out+12], %d;""".stripMargin,
      Vec(0xf0L, 0x3cL, 0L),
      4
    )
    assertEquals(result, Vec(0L, 0L, 0xccL, 1L))
  }

  // 9.7.14.16.19 and .21: every VM mbarrier has the v0 layout, whose report is zero and whose conditional phase
  // completes with the primary one.
  test("mbarrier waits report nothing under the v0 layout, which check_layout confirms") {
    val result = run(
      ".reg .pred %p, %r, %v0, %v1; .reg .b64 %state, %value; .shared .b64 barrier;",
      """mbarrier.init.layout::v0.shared::cta.b64 [barrier], 1;
        |mbarrier.arrive.shared::cta.b64 %state, [barrier];
        |mbarrier.try_wait.phase_type::primary.shared::cta.b64 %p|%r, %value, [barrier], %state;
        |selp.u32 %d, 1, 0, %p; st.global.u32 [%out], %d; selp.u32 %d, 1, 0, %r; st.global.u32 [%out+4], %d;
        |cvt.u32.u64 %d, %value; st.global.u32 [%out+8], %d;
        |mbarrier.test_wait.parity.phase_type::conditional.shared::cta.b64 %p, [barrier], 0;
        |selp.u32 %d, 1, 0, %p; st.global.u32 [%out+12], %d;
        |mbarrier.check_layout.layout::v0.shared::cta.b64 %v0, [barrier];
        |mbarrier.check_layout.layout::v1.shared::cta.b64 %v1, [barrier];
        |selp.u32 %d, 1, 0, %v0; st.global.u32 [%out+16], %d; selp.u32 %d, 1, 0, %v1; st.global.u32 [%out+20], %d;""".stripMargin,
      Vec(0L, 0L, 0L),
      6
    )
    assertEquals(result, Vec(1L, 0L, 0L, 1L, 1L, 0L))
  }

  // 9.7.9.31 and 9.7.13.15: the VM keeps no caches, so a policy is opaque and the proxy and restricted fences order
  // nothing further.
  test("createpolicy, membar.proxy and restricted release fences change no other state") {
    val result = run(
      ".reg .b64 %policy;",
      """createpolicy.fractional.L2::evict_last.b64 %policy, 1.0;
        |membar.proxy.alias; fence.release.sync_restrict::shared::cta.cluster;
        |ld.global.L2::cache_hint.u32 %d, [%in], %policy; st.global.u32 [%out], %d;""".stripMargin,
      Vec(0x1234L, 0L, 0L),
      1
    )
    assertEquals(result, Vec(0x1234L))
  }

  // 9.7.13.5: each element of a vector atomic is an atomic operation of its own, and a cache policy only hints
  test("vector, half-precision extremum and cache-hinted atomics update each element") {
    val result = run(
      ".reg .f32 %x, %y; .reg .b32 %h, %k; .reg .b16 %s; .reg .b64 %policy;",
      """mov.b32 %x, 0x3fc00000; mov.b32 %y, 0x40000000;
        |atom.global.add.v2.f32 {%x, %y}, [%in], {%x, %y};
        |ld.global.v2.f32 {%x, %y}, [%in]; st.global.v2.f32 [%out], {%x, %y};
        |atom.global.min.noftz.f16x2 %h, [%in+8], 0x42003800; st.global.u32 [%out+8], %h;
        |ld.global.u32 %h, [%in+8]; st.global.u32 [%out+12], %h;
        |mov.b16 %s, 0x4040; atom.global.max.noftz.bf16 %s, [%in+12], %s; ld.global.u32 %h, [%in+12];
        |st.global.u32 [%out+16], %h;
        |createpolicy.fractional.L2::evict_last.b64 %policy, 0.5;
        |atom.global.L2::cache_hint.add.u32 %k, [%in+16], 5, %policy; ld.global.u32 %h, [%in+16];
        |st.global.u32 [%out+20], %h;""".stripMargin,
      Vec(0x40400000L, 0x40800000L, 0x3c004000L, 0x3f803f80L, 7L),
      6
    )
    // 3 + 1.5 and 4 + 2; the low half 2 and 0.5 give 0.5, the high 1 and 3 give 1; bfloat16 max(1, 3) in the low half
    assertEquals(result, Vec(0x40900000L, 0x40c00000L, 0x3c004000L, 0x3c003800L, 0x3f804040L, 12L))
  }

  // 9.7.4.4 and 9.7.3.6: .relu clamps a negative result to zero; f32x2 rounds each word's fused result
  test("fma.relu clamps half-precision results and fma.f32x2 rounds each word") {
    val result = run(
      ".reg .b32 %h; .reg .b64 %p, %q, %r, %t; .reg .b16 %s;",
      """mov.b32 %h, 0x3c00bc00; fma.rn.relu.f16x2 %h, %h, 0x40004000, 0; st.global.u32 [%out], %h;
        |mov.b16 %s, 0xbf80; fma.rn.relu.bf16 %s, %s, %s, 0xc040; cvt.u32.u16 %h, %s; st.global.u32 [%out+4], %h;
        |mov.b64 %p, 0x404000003f800001; mov.b64 %q, 0x3f0000003f800001; mov.b64 %r, 0xbf80000000000000;
        |fma.rp.f32x2 %t, %p, %q, %r; st.global.u64 [%out+8], %t;
        |fma.rn.f32x2 %t, %p, %q, %r; st.global.u64 [%out+16], %t;""".stripMargin,
      Vec(0L, 0L, 0L),
      6,
      "sm_100"
    )
    // 1 * 2 and -1 * 2; -1 * -1 - 3 is -2; (1 + 2^-23)^2 = 1 + 2^-22 + 2^-46 rounds up or to even, and 3 * 0.5 - 1
    assertEquals(result, Vec(0x40000000L, 0L, 0x3f800003L, 0x3f000000L, 0x3f800002L, 0x3f000000L))
  }

  // 9.7.7.2 and 9.7.6.1: the low halves' result goes to p, the high halves' to q, each combined with the predicate
  test("setp and set on a pair of halves combine each half's comparison") {
    val result = run(
      ".reg .pred %p, %q, %t;",
      """setp.lt.f16x2 %p|%q, %a, %b; selp.u32 %d, 1, 0, %p; st.global.u32 [%out], %d;
        |selp.u32 %d, 1, 0, %q; st.global.u32 [%out+4], %d;
        |setp.ne.u32 %t, %c, 0;
        |setp.lt.or.bf16x2 %p|%q, %a, %b, %t; selp.u32 %d, 1, 0, %p; st.global.u32 [%out+8], %d;
        |set.lt.and.u32.f16x2 %d, %a, %b, %t; st.global.u32 [%out+12], %d;
        |set.gt.xor.f16x2.f16x2 %d, %a, %b, !%t; st.global.u32 [%out+16], %d;""".stripMargin,
      Vec(0x3c004000L, 0x40003c00L, 1L),
      5
    )
    // halves (2, 1) against (1, 2): low 2 < 1 fails, high 1 < 2 holds
    assertEquals(result, Vec(0L, 1L, 1L, 0xffff0000L, 0x00003c00L))
  }

  // 9.7.9.9: a b128 access moves both words of a register at once, in any state space below system scope
  test("weak b128 loads and stores copy sixteen bytes") {
    val result = run(
      ".reg .b128 %w;",
      "ld.global.b128 %w, [%in]; st.global.b128 [%out], %w;",
      Vec(0x11L, 0x22L, 0x33L, 0x44L),
      4
    )
    assertEquals(result, Vec(0x11L, 0x22L, 0x33L, 0x44L))
  }

  // 9.7.9.26.4.1: .ignore_oob copies the middle of the chunk and leaves the ignored destination bytes indeterminate
  test("cp.async.bulk.ignore_oob skips the bytes it ignores at each end") {
    val words  = Vec.tabulate(8)(index => 0x01010101L * (index + 1))
    val result = run(
      ".shared .align 16 .b8 staged[32]; .shared .align 8 .b64 bar; .reg .b64 %token; .reg .pred %p; .reg .u32 %i;",
      """mbarrier.init.shared::cta.b64 [bar], 1;
        |mov.u32 %i, 0;
        |FILL: st.shared.u32 [staged+0], 0xaaaaaaaa; st.shared.u32 [staged+4], 0xaaaaaaaa;
        |st.shared.u32 [staged+20], 0xaaaaaaaa; st.shared.u32 [staged+24], 0xaaaaaaaa;
        |st.shared.u32 [staged+28], 0xaaaaaaaa;
        |cp.async.bulk.shared::cta.global.mbarrier::complete_tx::bytes.ignore_oob [staged], [%in], 32, 4, 8, [bar];
        |mbarrier.arrive.expect_tx.shared::cta.b64 %token, [bar], 32;
        |WAIT: mbarrier.try_wait.parity.shared::cta.b64 %p, [bar], 0; @!%p bra WAIT;
        |ld.shared.v4.u32 {%a, %b, %c, %d}, [staged]; st.global.v4.u32 [%out], {%a, %b, %c, %d};
        |ld.shared.v4.u32 {%a, %b, %c, %d}, [staged+16]; st.global.v4.u32 [%out+16], {%a, %b, %c, %d};""".stripMargin,
      words,
      8
    )
    assertEquals(result, Vec(0xaaaaaaaaL) ++ words.slice(1, 6) ++ Vec(0xaaaaaaaaL, 0xaaaaaaaaL))
  }

  // 9.7.9.22: pairs packed high over low; formats without NaN saturate to their largest magnitude, ue8m0 rounds to a
  // power of two, s2f6 is a byte of 6 fraction bits divided by its scale factor, and widening multiplies by it
  test("cvt between binary32, half pairs and the 4-, 6- and 8-bit pair formats") {
    val result = run(
      ".reg .f32 %x, %y; .reg .b16 %h; .reg .b8 %q; .reg .b32 %w;",
      """mov.b32 %x, 0x3fc00000; mov.b32 %y, 0xbe800000;
        |cvt.rn.satfinite.e2m1x2.f32 %q, %x, %y; cvt.u32.u8 %d, %q; st.global.u32 [%out], %d;
        |mov.b32 %x, 0x40e00000; mov.b32 %y, 0xbf800000;
        |cvt.rn.satfinite.relu.e2m1x2.f32 %q, %x, %y; cvt.u32.u8 %d, %q; st.global.u32 [%out+4], %d;
        |mov.b32 %w, 0x38; cvt.u8.u32 %q, %w; cvt.rn.f16x2.e2m1x2 %d, %q; st.global.u32 [%out+8], %d;
        |mov.b32 %x, 0x40400000; mov.b32 %y, 0x3f400000;
        |cvt.rp.satfinite.ue8m0x2.f32 %h, %x, %y; cvt.u32.u16 %d, %h; st.global.u32 [%out+12], %d;
        |cvt.rz.satfinite.ue8m0x2.f32 %h, %x, %y; cvt.u32.u16 %d, %h; st.global.u32 [%out+16], %d;
        |mov.b16 %h, 0x8180; cvt.rn.bf16x2.ue8m0x2 %d, %h; st.global.u32 [%out+20], %d;
        |mov.b32 %x, 0x3fc00000; mov.b32 %y, 0xc0400000;
        |cvt.rn.satfinite.s2f6x2.f32 %h, %x, %y; cvt.u32.u16 %d, %h; st.global.u32 [%out+24], %d;
        |cvt.rn.satfinite.scaled::n2::ue8m0.s2f6x2.f32 %h, %x, %y, 0x807f; cvt.u32.u16 %d, %h;
        |st.global.u32 [%out+28], %d;
        |mov.b16 %h, 0x60c0; cvt.rn.bf16x2.s2f6x2 %d, %h; st.global.u32 [%out+32], %d;
        |cvt.rn.scaled::n2::ue8m0.bf16x2.s2f6x2 %d, %h, 0x8080; st.global.u32 [%out+36], %d;
        |mov.b32 %w, 0x3fc0bf80; cvt.rn.satfinite.e4m3x2.bf16x2 %h, %w; cvt.u32.u16 %d, %h; st.global.u32 [%out+40], %d;
        |cvt.rn.bf16x2.e4m3x2 %d, %h; st.global.u32 [%out+44], %d;
        |mov.b32 %w, 0x3e00bc00; cvt.rn.satfinite.e2m3x2.f16x2 %h, %w; cvt.u32.u16 %d, %h; st.global.u32 [%out+48], %d;
        |""".stripMargin,
      Vec(0L, 0L, 0L),
      13,
      "sm_100a"
    )
    assertEquals(
      result,
      Vec(
        0x38L, // 1.5 and -0.25, which rounds to the even -0
        0x70L, // 7 saturates to 6, and relu takes -1 to 0
        0x3e008000L,
        0x817fL, // 3 and 0.75 rounded up to 4 and 1
        0x807eL, // and down to 2 and 0.5
        0x40804000L,
        0x6080L, // 1.5 is 96 sixty-fourths, and -3 saturates to -2
        0x3080L, // the high value halved by its scale factor
        0x3fc0bf80L,
        0x4040c000L,
        0x3cb8L,
        0x3fc0bf80L,
        0x0c28L // 1.5 and -1 in E2M3, each in the low six bits of its byte
      )
    )
  }

  // 9.7.10.3: brx.idx branches to the label its index selects from a .branchtargets list, as nvcc lowers a switch
  test("brx.idx branches through a branch target list") {
    for ((index, expected) <- Vec(0L -> 10L, 1L -> 11L, 2L -> 12L))
      assertEquals(
        run(
          ".reg .pred %p;",
          """targets: .branchtargets L0, L1, L2;
            |setp.ne.u32 %p, %c, 0;
            |brx.idx %a, targets;
            |L0: mov.u32 %d, 10; bra DONE;
            |L1: mov.u32 %d, 11; bra DONE;
            |L2: mov.u32 %d, 12;
            |DONE: @%p brx.idx.uni %b, targets;
            |st.global.u32 [%out], %d;""".stripMargin,
          Vec(index, 0L, 0L),
          1,
          "sm_70"
        ),
        Vec(expected),
        s"index $index"
      )
  }
}
