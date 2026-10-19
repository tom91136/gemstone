package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.sass.Harness.*
import net.kurobako.gemstone.isa.sass.Targets.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.testing.either.*

class HalfSuite extends munit.FunSuite {
  private val Flags = Vec(false, true)

  private def kernel(name: String, function: String, sm: Int = 89): Kernel = load(sm, Fixture.listing(name), function)

  private def operands(text: String): Vec[Operand] = OperandParser.parseMany(text).value

  private def replace(kernel: Kernel, mnemonic: String, text: String): Kernel =
    kernel.map(i => if (i.mnemonic.raw == mnemonic) i.rewrite(Parser.parse(text).value) else i)

  private def binary16(bits: Long): Float = IEEE754.binary16ToFloat(Binary16.low(bits))

  /** One thread per case: `out[i] = f(a[i], b[i], c[i])` over 32-bit words. */
  private def elementwise(kernel: Kernel, inputs: Int, cases: Vec[Vec[Long]], expected: Vec[Long]): Unit = {
    val arguments = (0 until inputs).map(input => Arg.Buffer(words(cases.map(_(input)))))
    assertEquals(kernel.launch(cases.size)((Arg.zeros(cases.size * 4) +: arguments)*).words(0), expected)
  }

  private def threeInput(kernel: Kernel, cases: Vec[(Long, Long, Long, Long)]): Unit =
    elementwise(kernel, 3, cases.map((a, b, c, _) => Vec(a, b, c)), cases.map(_._4))

  private def twoInput(kernel: Kernel, printed: String, cases: Vec[(Long, Long, Long)]): Unit = {
    assert(kernel.function.instructions.exists(i => Printer.print(i).toOption.exists(_.stripSuffix(";") == printed)))
    elementwise(kernel, 2, cases.map((a, b, _) => Vec(a, b)), cases.map(_._3))
  }

  private def pair(value: Int): Long = value.toLong | (value.toLong << 16)

  private val hfma2 = kernel("hfma2Sm89", "hfma2_vector")

  test("compiler-produced sm_89 HFMA2 is fused and canonicalises NaNs") {
    threeInput(
      hfma2,
      Vec(
        (0x5f90, 0xc7c0, 0x037c, 0xeb53),
        (0x630a, 0x82b3, 0x45c0, 0x45b7),
        (0xc65e, 0xc957, 0x712f, 0x7137),
        (0xb86d, 0xcc76, 0x8fc2, 0x48ef),
        (0x0001, 0x3800, 0x0000, 0x0000),
        (0x8000, 0x3c00, 0x8000, 0x8000),
        (0x7e01, 0x3c00, 0x0000, 0x7fff),
        (0x7c00, 0x0000, 0x3c00, 0x7fff)
      ).map((a, b, c, result) => (pair(a), pair(b), pair(c), pair(result)))
    )
  }

  test("compiler-produced HFMA2 H0_H0 selectors duplicate the selected lane") {
    threeInput(
      kernel("hfma2SelectedSm89", "hfma2_selected"),
      Vec(
        (0x3c003c00L, 0x40003c00L, 0xfc004900L, 0xfc004a00L),
        (0x04000001L, 0x38003c00L, 0x00000000L, 0x00000000L),
        (0x00007e01L, 0x3c000000L, 0x00000000L, 0x7fff7fffL)
      )
    )
  }

  test("HFMA2 H1_H1 sources can alias the destination while reading a separate addend") {
    val aliased = hfma2.map { instruction =>
      if (instruction.mnemonic.raw == "HFMA2") instruction.rewrite(operands = operands("R2, R2.H1_H1, R5, R6"))
      else if (instruction.mnemonic.raw == "STG") instruction.rewrite(operands = operands("[R8.64], R2"))
      else instruction
    }
    threeInput(
      aliased,
      Vec((0x3c004000L, 0x42004200L, 0x3c003c00L, 0x44004400L), (0x35550000L, 0x35553555L, 0x00003555L, 0x2f1c371cL))
    )
  }

  // ptxas emits HFMA2.MMA for fma.rn.f16x2 (__hfma2) on sm_80 and later, so every multiplier and accumulator form
  // computes the same fused product as HFMA2.
  test("HFMA2.MMA multiplier and accumulator forms match HFMA2") {
    Vec(
      "R6, R2, R3, R6",
      "R6, R2.H0_H0, RZ, R6",
      "R6, R2, RZ, R7",
      "R6, R2, 2, 1, R6",
      "R6, R2.H0_H0, 1, 1, R6",
      "R6, R2, 1, 1, R7.H0_H0"
    ).foreach { form =>
      val mma   = replace(hfma2, "HFMA2", s"HFMA2.MMA $form;").instructions(8, 8, 8, 8)
      val fused = replace(hfma2, "HFMA2", s"HFMA2 $form;").instructions(8, 8, 8, 8)
      assertEquals(mma, fused, form)
    }
  }

  test("HFMA2.MMA accumulator alias matches the fused packed operation for special values") {
    twoInput(
      kernel("hfma2MmaAliasSm89Patched", "hfma2_mma_probe"),
      "HFMA2.MMA R5, R2, 1, 1, R5",
      NumericCases.packedAdd
    )
  }

  test("HFMA2.MMA unit multipliers support a separate accumulator") {
    twoInput(
      kernel("hfma2MmaNonaliasSm89Patched", "hfma2_mma_probe"),
      "HFMA2.MMA R9, R2, 1, 1, R5",
      NumericCases.packedAdd
    )
  }

  test("four-operand zero-multiplier HFMA2.MMA matches compiler-produced HFMA2") {
    twoInput(
      kernel("hfma2MmaZeroAliasSm89Patched", "hfma2_mma_probe"),
      "HFMA2.MMA R5, R2, RZ, R5",
      Vec(
        (0x3c003c00L, 0xc0004000L, 0xc0004000L),
        (0x7e013c00L, 0x3c007e01L, 0x7fff7fffL),
        (0x7c003c00L, 0x3c007c00L, 0x7fff7c00L),
        (0x00018000L, 0x80000001L, 0x00000001L),
        (0x00008000L, 0x80000000L, 0x00000000L),
        (0x80008000L, 0x80008000L, 0x80008000L),
        (0x7e017e01L, 0x7e017e01L, 0x7fff7fffL),
        (0x3c003c00L, 0x00010001L, 0x00010001L)
      )
    )
  }

  for {
    binary <- Flags
    fused  <- Flags
  }
    test(s"HFMA2 mma=$fused immediate addends preserve special values (${if (binary) "binary" else "text"} frontend)") {
      Vec(
        "38004000" -> 0x38004000L,
        "7c003c00" -> 0x7c003c00L,
        "fc007c00" -> 0xfc007c00L,
        "80000000" -> 0x80000000L,
        "7c01fc01" -> 0x7fff7fffL,
        "7e013c00" -> 0x7fff3c00L,
        "fe017e01" -> 0x7fff7fffL
      ).foreach { (immediate, expected) =>
        val listing = Fixture.listing(s"hfma2Constant${immediate.capitalize}Sm89Patched")
        val text    = load(89, listing, "hfma2_mma_probe")
        val decoded =
          if (!binary) text
          else {
            val instructions = Listing.words(listing, "hfma2_mma_probe", 89).value.map((address, bytes) =>
              InstructionDecoder.decode(address, bytes, target(89)).value
            )
            text.copy(functions = Vec(DecodedFunction("hfma2_mma_probe", instructions)))
          }
        val selected =
          if (fused) decoded
          else decoded.map { instruction =>
            instruction.operation.form match {
              case _: Form.Hfma2 => instruction.rewrite(modifiers = Vec.empty[Instruction.Modifier])
              case _             => instruction
            }
          }
        assertEquals(selected.launch(1)(Arg.zeros(4), Arg.zeros(4), Arg.zeros(4)).words(0), Vec(expected), immediate)
      }
    }

  private val hsetp2Cases = Vec(
    (0x38003c00L, 0x3c003800L, 0x78007001L),
    (0x3c003800L, 0x38003c00L, 0x70007800L),
    (0x00007e01L, 0x00003c00L, 0x00000001L),
    (0x00003c00L, 0x7e010000L, 0xfc020001L),
    (0x00000001L, 0x3c000000L, 0x78000001L),
    (0x3c008000L, 0x3c000000L, 0x78007801L),
    (0x3c000000L, 0x00010001L, 0x00027800L),
    (0x00007c00L, 0x38007e01L, 0x70000001L),
    (0x00003c00L, 0x00007e01L, 0x00000001L)
  )

  test("compiler-produced selected-lane HSETP2.GEU matches half unordered comparisons") {
    twoInput(kernel("hsetp2Sm89", "hsetp2_probe"), "HSETP2.GEU.AND P0, PT, R2.H0_H0, R5.H0_H0, PT", hsetp2Cases)
  }

  test("raw-register HSETP2.GEU uses the low comparison result") {
    twoInput(kernel("hsetp2GeuRawSm89Patched", "hsetp2_probe"), "HSETP2.GEU.AND P0, PT, R2, R5, PT", hsetp2Cases)
  }

  private val hsetp2Ne = kernel("hsetp2NeRawSm89Patched", "hsetp2_ne_probe")

  test("raw-register HSETP2.NE compares the low half with ordered NaN semantics") {
    twoInput(
      hsetp2Ne,
      "HSETP2.NE.AND P0, PT, R2, RZ, PT",
      Vec(
        (0x3c000000L, 0L, 0x7800L),
        (0x3c008000L, 0L, 0x7800L),
        (0x3c000001L, 0L, 0x7801L),
        (0x7e013c00L, 0L, 0xfc03L),
        (0x3c007e01L, 0L, 0x7800L),
        (0x7c000000L, 0L, 0xf800L),
        (0x00007c00L, 0L, 1L),
        (0x0000fc00L, 0L, 1L),
        (0x7e010000L, 0L, 0xfc02L)
      )
    )
  }

  test("HSETP2 rejects unknown selectors and a combination without its predicate") {
    Vec(
      "HSETP2.NE.AND P0, PT, R2, RZ",
      "HSETP2.NE.AND P0, PT, R2.H0_H1, RZ, PT"
    ).foreach(form => assert(replace(hsetp2Ne, "HSETP2", s"$form;").lower(8, 8, 8).isLeft, form))
  }

  private val geuCases = Vec(
    (0x38003c00, 0x3c003800), // low true, high false
    (0x3c003800, 0x38003c00), // low false, high true
    (0x00007e01, 0x3c000000), // low NaN gives unordered GEU true
    (0x3c000000, 0x7e010000), // high NaN gives unordered GEU true
    (0x7e017e01, 0x3c003c00),
    (0x80000000, 0x00008000), // signed zeros compare equal
    (0x00010000, 0x00000001), // low zero < minimum subnormal; high subnormal > zero
    (0x7c003c00, 0x7e017c00), // low finite < infinity; high infinity vs NaN
    (0x00003c00, 0x00003c00)
  )

  private def dualOutput(sm: Int, left: String, predicate: String = "PT"): Kernel =
    kernel(s"hsetp2TwoSm$sm", "hsetp2_two", sm).map { instruction =>
      if (instruction.mnemonic.raw == "HSETP2")
        instruction.rewrite(operands =
          instruction.operands.updated(2, operands(left).head).updated(4, operands(predicate).head)
        )
      else instruction
    }

  private def checkDualOutput(sm: Int, duplicateLow: Boolean): Unit = {
    def geu(a: Int, b: Int): Long = {
      val (x, y) = (binary16(a.toLong), binary16(b.toLong))
      if (x.isNaN || y.isNaN || x >= y) 1L else 0L
    }
    val run = dualOutput(sm, if (duplicateLow) "R2.H0_H0" else "R2").launch(geuCases.size)(
      Arg.zeros(256),
      Arg.Buffer(words(geuCases.map(_._1.toLong))),
      Arg.Buffer(words(geuCases.map(_._2.toLong)))
    )
    val low  = geuCases.map((left, right) => geu(left & 0xffff, right & 0xffff))
    val high = geuCases.map((left, right) => geu(if (duplicateLow) left & 0xffff else left >>> 16, right >>> 16))
    assertEquals(run.words(0), low.padTo(32, 0L) ++ high.padTo(32, 0L))
  }

  test("dual-output HSETP2.GEU maps low and high half comparisons independently") {
    checkDualOutput(89, duplicateLow = false)
  }

  test("dual-output HSETP2.GEU H0_H0 duplicates the left low half into both comparisons") {
    checkDualOutput(89, duplicateLow = true)
  }

  test("dual-output HSETP2 combines a predicate input") {
    assert(dualOutput(89, "R2", "P2").lower(8, 8, 8).isRight)
  }

  test("CUDA 11.8 sm_70 compiler form preserves both outputs in the shared NVIDIA VM") {
    checkDualOutput(70, duplicateLow = false)
  }

  private val addReference = kernel("hfma2MmaAddReferenceSm89", "hfma2_add_reference")

  test("compiler-produced packed HADD2 matches half addition and canonicalises NaNs") {
    twoInput(addReference, "HADD2 R9, R2, R5", NumericCases.packedAdd)
  }

  test("HADD2 duplicates selected H0 and H1 lanes across both packed results") {
    val cases = Vec(
      (0x42003c00L, 0x45004400L, 0x45004500L, 0x48004800L),
      (0x40003c00L, 0x42004000L, 0x42004200L, 0x45004500L)
    )
    twoInput(
      kernel("hadd2SelectedLowSm89", "hadd2_selected_low"),
      "HADD2 R9, R2.H0_H0, R5.H0_H0",
      cases.map((a, b, low, _) => (a, b, low))
    )
    twoInput(
      kernel("hadd2SelectedHighSm89", "hadd2_selected_high"),
      "HADD2 R9, R2.H0_H0, R5.H0_H0",
      cases.map((a, b, _, high) => (a, b, high))
    )
  }

  test("HADD2 H1_H1 selectors duplicate the selected high lane") {
    twoInput(
      addReference.map(i =>
        if (i.mnemonic.raw == "HADD2") i.rewrite(operands = operands("R9, R2.H1_H1, R5.H1_H1")) else i
      ),
      "HADD2 R9, R2.H1_H1, R5.H1_H1",
      Vec((0x42003c00L, 0x45004400L, 0x48004800L), (0x40003c00L, 0x42004000L, 0x45004500L))
    )
  }

  test("HADD2 can alias its destination with the first source over the packed NaN table") {
    val aliased = addReference.map { instruction =>
      if (instruction.mnemonic.raw == "HADD2") instruction.rewrite(operands = operands("R2, R2, R5"))
      else if (instruction.mnemonic.raw == "STG") instruction.rewrite(operands = operands("[R6.64], R2"))
      else instruction
    }
    twoInput(aliased, "HADD2 R2, R2, R5", NumericCases.packedAdd)
  }

  private val halves =
    Vec(0x0000, 0x8000, 0x0001, 0x03ff, 0x0400, 0x3c00, 0xbc00, 0x7c00, 0xfc00, 0x7c01, 0xfc01, 0x7e00, 0xfe00, 0x3555)

  // pinned rather than derived from the binary16 conversion the VM itself uses
  private val widened = Vec(
    0x00000000, 0x80000000, 0x33800000, 0x387fc000, 0x38800000, 0x3f800000, 0xbf800000,
    0x7f800000, 0xff800000, 0x7fffffff, 0x7fffffff, 0x7fffffff, 0x7fffffff, 0x3eaaa000
  )

  test("CUDA 13.3 sm_75 HADD2.F32 accepts the selected negative-zero source") {
    val packed = halves.grouped(2).map(pair => pair(0).toLong | (pair(1).toLong << 16)).toVec
    val run    = kernel("hadd2F32Sm75", "probe", 75).launch(1, halves.size / 2)(
      Arg.zeros(halves.size * 4),
      Arg.Buffer(words(packed))
    )
    assertEquals(run.words(0).map(_.toInt), widened)
  }

  test("CUDA 13.3 sm_89 HADD2.F32 converts selected half lanes including IEEE edge values") {
    val run =
      kernel(
        "hadd2Sm89",
        "convert_half"
      ).launch(1, halves.size / 2)(Arg.zeros(halves.size * 4), Arg.Buffer(words(halves.map(_.toLong), 2)))
    assertEquals(run.words(0).map(_.toInt), widened)
  }

  private val scalarHadd2 = kernel("hadd2F32ToHalfSm75Patched", "probe", 75)

  // ptxas 11.8 and 13.3 lower __float2half_rz to HADD2 R0, R2.F32, -RZ.H0_H0 for sm_60 to sm_75, so .F32 truncates
  private val towardZero = NumericCases.f32ToF16.map { (input, nearest) =>
    input ->
      (input match {
        case 0x3f803000L               => 0x3c01L
        case 0x477ff000L | 0x7f7fffffL => 0x7bffL
        case 0xc77ff000L | 0xff7fffffL => 0xfbffL
        case _                         => nearest
      })
  }

  test("sm_75 scalar-float HADD2 truncates to and duplicates the packed half result") {
    val inputs = towardZero.map(_._1)
    val run    = scalarHadd2.launch(1, inputs.size)(Arg.zeros(inputs.size * 4), Arg.Buffer(words(inputs)))
    assertEquals(run.words(0), towardZero.map(_._2 * 0x00010001L))
  }

  // .F32 truncates the source to binary16 before the packed addition, so a positive zero addend only turns -0 into +0.
  test("scalar-float HADD2 converts on later architectures and with a positive zero addend") {
    val inputs              = towardZero.map(_._1)
    def run(kernel: Kernel) =
      kernel.launch(1, inputs.size)(Arg.zeros(inputs.size * 4), Arg.Buffer(words(inputs))).words(0)
    assertEquals(run(scalarHadd2.copy(sm = 89)), towardZero.map(_._2 * 0x00010001L))
    val positiveZero = scalarHadd2.map { i =>
      if (i.mnemonic.raw == "HADD2" && i.modifiers.isEmpty && i.operands.head == operands("R0").head)
        i.rewrite(operands = i.operands.updated(2, operands("RZ.H0_H0").head))
      else i
    }
    assertEquals(
      run(positiveZero),
      towardZero.map((_, half) => (if (half == 0x8000L) 0L else half) * 0x00010001L)
    )
  }

  private val hmul2 = kernel("hmul2Sm89", "hmul2_vector")

  test("compiler-produced sm_89 HMUL2 matches binary16 lane products and NVIDIA NaN canonicalisation") {
    twoInput(hmul2, "HMUL2 R9, R2, R5", NumericCases.packedMultiply)
  }

  test("compiler-produced HMUL2 H0_H0 selectors duplicate the selected half") {
    twoInput(
      kernel("hmul2SelectedSm89", "hmul2_selected"),
      "HMUL2 R9, R2.H0_H0, R5.H0_H0",
      Vec(
        (0x3c004000L, 0x40003c00L, 0x44004400L),
        (0x04000001L, 0x38003c00L, 0x00000000L),
        (0x00007e01L, 0x3c000000L, 0x7fff7fffL)
      )
    )
  }

  test("HMUL2 H1_H1 selectors and a destination alias retain both operand values") {
    val aliased = hmul2.map { instruction =>
      if (instruction.mnemonic.raw == "HMUL2") instruction.rewrite(operands = operands("R2, R2.H1_H1, R5.H1_H1"))
      else if (instruction.mnemonic.raw == "STG") instruction.rewrite(operands = operands("[R6.64], R2"))
      else instruction
    }
    twoInput(
      aliased,
      "HMUL2 R2, R2.H1_H1, R5.H1_H1",
      Vec(
        (0x40003c00L, 0x40004000L, 0x44004400L),
        (0x35550000L, 0x35550000L, 0x2f1c2f1cL),
        (0x3c007e01L, 0x3c003c00L, 0x3c003c00L)
      )
    )
  }

  private val hmul2F32 = kernel("hmul2F32Sm75Patched", "hmul2_f32_probe", 75)

  private def products(a: Int, b: Int): Vec[Int] = {
    def product(left: Int, right: Int): Int = {
      val result = binary16(left.toLong) * binary16(right.toLong)
      if (java.lang.Float.isNaN(result)) 0x7fffffff else java.lang.Float.floatToRawIntBits(result)
    }
    Vec(
      product(a & 0xffff, b & 0xffff),
      product(a & 0xffff, b >>> 16),
      product(a >>> 16, b & 0xffff),
      product(a >>> 16, b >>> 16)
    )
  }

  private def multiplyF32(kernel: Kernel, inputs: Vec[(Int, Int)]): Vec[Vec[Int]] = kernel.launch(inputs.size)(
    Arg.zeros(inputs.size * 16),
    Arg.Buffer(words(inputs.map(_._1.toLong & 0xffffffffL))),
    Arg.Buffer(words(inputs.map(_._2.toLong & 0xffffffffL)))
  ).words(0).map(_.toInt).grouped(4).toVec

  test("sm_75 HMUL2.F32 low/high selected products match the source-equivalent oracle") {
    val cases = Vec(
      0x40003c00 -> 0x44004200, // distinct lanes: 1/2 multiplied by 3/4
      0x80000000 -> 0xbc003c00, // signed zeros times +/-1
      0x03ff0001 -> 0x3c000001, // subnormal and normal lanes
      0xfc007c00 -> 0x7c00fc00, // infinities and sign changes
      0x7d557e01 -> 0xfe017c01, // signalling and quiet NaNs in both sources
      0x7c000000 -> 0x00007c00, // zero * infinity
      0x3555bc00 -> 0x3c008001
    )
    assertEquals(multiplyF32(hmul2F32, cases), cases.map(products(_, _)))
  }

  test("sm_75 HMUL2.F32 reads selected inputs before an aliased destination write") {
    val r13     = Operand.Register(RegisterBank.R, 13)
    val r2      = Operand.Register(RegisterBank.R, 2)
    val aliased = hmul2F32.map { instruction =>
      if (instruction.mnemonic.raw == "HMUL2" && instruction.operands.headOption.contains(r13))
        instruction.rewrite(operands = instruction.operands.updated(0, r2))
      else if (instruction.mnemonic.raw == "STG" && instruction.operands.lastOption.contains(r13))
        instruction.rewrite(operands = instruction.operands.updated(instruction.operands.size - 1, r2))
      else instruction
    }
    val inputs = Vec(0x40003c00 -> 0x44004200, 0x03ff0001 -> 0x3c000001)
    assertEquals(multiplyF32(aliased, inputs).map(_.head), inputs.map(products(_, _).head))
  }

  test("HMUL2.F32 lowers on other architectures and rejects mixed selector forms") {
    assert(hmul2F32.copy(sm = 89).lower(8, 8, 8).isRight)
    val mixed = hmul2F32.map { i =>
      if (i.mnemonic.raw == "HMUL2" && i.operands(1) == operands("R2.H0_H0").head)
        i.rewrite(operands = i.operands.updated(1, operands("R2.H0_H1").head))
      else i
    }
    assert(mixed.lower(8, 8, 8).left.exists(_.contains("HMUL2.F32 sources select one half with H0_H0 or H1_H1")))
  }

  private val hmnmx2Cases = Vec(
    (0x40003c00L, 0x3c004000L, 0x40004000L),
    (0x49003c00L, 0x3c004200L, 0x49004200L),
    (0x3c004800L, 0x3c004000L, 0x40004800L),
    (0x41003c00L, 0x3c004000L, 0x41004000L),
    (0x40008000L, 0x3c000000L, 0x40000000L),
    (0x3c000000L, 0x40008000L, 0x3c000000L),
    (0x40000000L, 0x3c000000L, 0x40000000L),
    (0x40008000L, 0x3c008000L, 0x40008000L),
    (0x40007e01L, 0x3c004000L, 0x40004000L),
    (0x42003c00L, 0x40007e01L, 0x42003c00L),
    (0x4000fc01L, 0x3c004000L, 0x40004000L),
    (0x42003c00L, 0x40007d55L, 0x42003c00L),
    (0x40007e01L, 0x3c00ffc1L, 0x40007fffL),
    (0x4000fc01L, 0x3c007d55L, 0x40007fffL),
    (0x7c003c00L, 0xfc004000L, 0x7c004000L),
    (0x40000001L, 0x3c008000L, 0x40000001L),
    (0x7e013c00L, 0x3c003c00L, 0x3c003c00L),
    (0xfc013c00L, 0x3c003c00L, 0x3c003c00L),
    (0x7d553c00L, 0x3c004000L, 0x40004000L)
  )

  private def hmnmx2(kernel: Kernel, expected: Vec[Long], inPlace: Boolean = false): Unit = {
    val (a, b) = (Arg.Buffer(words(hmnmx2Cases.map(_._1))), Arg.Buffer(words(hmnmx2Cases.map(_._2))))
    val run    = if (inPlace) kernel.launch(hmnmx2Cases.size)(a, b)
    else kernel.launch(hmnmx2Cases.size)(a, b, Arg.zeros(expected.size * 4))
    assertEquals(run.words(if (inPlace) 0 else 2).take(expected.size), expected)
  }

  test("HMNMX2 H0 selectors implement ordered min and max with measured NaN and signed-zero behaviour") {
    val minimum = Vec(
      0x3c00L, 0x3c00L, 0x4000L, 0x3c00L, 0x8000L, 0x8000L, 0x0000L, 0x8000L, 0x4000L, 0x3c00L, 0x4000L,
      0x3c00L, 0x7fffL, 0x7fffL, 0x3c00L, 0x8000L, 0x3c00L, 0x3c00L, 0x3c00L
    )
    val maximum = Vec(
      0x4000L, 0x4200L, 0x4800L, 0x4000L, 0x0000L, 0x0000L, 0x0000L, 0x8000L, 0x4000L, 0x3c00L, 0x4000L,
      0x3c00L, 0x7fffL, 0x7fffL, 0x4000L, 0x0001L, 0x3c00L, 0x3c00L, 0x4000L
    )
    hmnmx2(
      kernel("hmnmx2SelectSm80", "hmnmx2_select_probe", 80),
      minimum.zip(maximum).flatMap((min, max) => Vec(min, max))
    )
  }

  private val hmnmx2Mixed = kernel("hmnmx2MixedSm80", "hmnmx2_mixed_probe", 80)

  test("HMNMX2 mixed packed source computes both halves and handles source/destination alias") {
    hmnmx2(hmnmx2Mixed, hmnmx2Cases.map(_._3))
    hmnmx2(kernel("hmnmx2MixedAliasSm80Patched", "hmnmx2_mixed_alias_probe", 80), hmnmx2Cases.map(_._3), inPlace = true)
  }

  test("HMNMX2 lowering rejects unverified selectors and modifiers") {
    assert(hmnmx2Mixed.map(i =>
      if (i.mnemonic.raw == "HMNMX2") i.rewrite(operands = i.operands.updated(2, operands("R5.H0_H1").head)) else i
    )
      .lower(8, 8, 8).isLeft)
    assert(hmnmx2Mixed.map(i =>
      if (i.mnemonic.raw == "HMNMX2") i.rewrite(modifiers = i.modifiers :+ Instruction.Modifier("RN")) else i
    )
      .lower(8, 8, 8).isLeft)
  }
}
