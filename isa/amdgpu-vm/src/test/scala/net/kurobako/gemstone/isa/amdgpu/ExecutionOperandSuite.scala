package net.kurobako.gemstone.isa.amdgpu

import EncodingConstants.*
import VMIR.*
import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class ExecutionOperandSuite extends munit.FunSuite {
  for (target <- Vec(Target("gfx900"), Target("gfx1036")))
    test(s"$target mask-producing instructions reset temporary masks and preserve architectural VCC width") {
      val zero                                          = InstructionSource.Constant(0L)
      val one                                           = InstructionSource.Constant(1L)
      def arithmetic(signal: Boolean): Vec[Instruction] = Vec(
        Instruction.VSubCarry(6, Fixed.VccLo, zero, if (signal) one else zero, None),
        Instruction.VSubReverseCarry(6, Fixed.VccLo, if (signal) one else zero, zero, None),
        Instruction.VMadU64U32(
          6,
          Fixed.VccLo,
          if (signal) one else zero,
          one,
          if (signal) InstructionSource.Constant(-1L) else zero
        ),
        Instruction.VMadI64I32(
          6,
          Fixed.VccLo,
          if (signal) one else zero,
          one,
          if (signal) InstructionSource.Constant(-1L) else zero
        ),
        Instruction.VDivScaleF32(
          6,
          Fixed.VccLo,
          InstructionSource.Floating(1.0),
          InstructionSource.Floating(if (signal) java.lang.Float.MIN_NORMAL.toDouble else 1.0),
          InstructionSource.Floating(1.0)
        ),
        Instruction.VDivScaleF64(
          6,
          Fixed.VccLo,
          InstructionSource.Floating(1.0),
          InstructionSource.Floating(if (signal) java.lang.Double.MIN_NORMAL else 1.0),
          InstructionSource.Floating(1.0)
        ),
        Instruction.VCompare(
          VectorComparison(VectorComparisonKind.Unsigned32, VectorComparisonRelation.Equal, false),
          Fixed.VccLo,
          zero,
          if (signal) zero else one,
          selection = Some(SubDwordSources(SubDwordSelection.DWord, SubDwordSelection.DWord, false, false))
        )
      )
      for {
        signal               <- Vec(false, true)
        operation            <- arithmetic(signal)
        (register, expected) <-
          Vec[(ScalarRegisterIndex, Int)](
            (Fixed.VccLo, (if (signal) 1 else 0)),
            (Fixed.VccHi, (if (target.isa.isRDNA) -1 else 0))
          )
      } {
        val operations = Vec(
          Instruction.SMove(Fixed.VccLo, InstructionSource.Constant(0xffffffffL)),
          Instruction.SMove(Fixed.VccHi, InstructionSource.Constant(0xffffffffL)),
          Instruction.VAddCarry(8, Fixed.VccLo, InstructionSource.Constant(0xffffffffL), one, None),
          operation,
          Instruction.VMove(2, InstructionSource.Scalar(register))
        )
        assertEquals(runSequence(target, operations), expected, clues(operation, register))
      }
    }

  test("OMOD follows the descriptor's IEEE and output-denormal modes and later MODE writes") {
    val target = Target("gfx900")
    // LLVM gfx900: v_mul_f32_e64 v2, v3, v4 mul:2 / div:2 / clamp mul:2; v_mul_f64 v[6:7], v[8:9], v[10:11] mul:2;
    // v_fma_f16 v2, v3, v4, v5 mul:2.
    val multiply2        = decoded(target, 0x02, 0x00, 0x05, 0xd1, 0x03, 0x09, 0x02, 0x08)
    val divide2          = decoded(target, 0x02, 0x00, 0x05, 0xd1, 0x03, 0x09, 0x02, 0x18)
    val clampedMultiply2 = decoded(target, 0x02, 0x80, 0x05, 0xd1, 0x03, 0x09, 0x02, 0x08)
    val double2          = decoded(target, 0x06, 0x00, 0x81, 0xd2, 0x08, 0x15, 0x02, 0x08)
    val half2            = decoded(target, 0x02, 0x00, 0x06, 0xd2, 0x03, 0x09, 0x16, 0x0c)
    val ieee             = 1 << 23
    def float(a: Float, b: Float, operation: Instruction, rsrc1: Int): Float =
      java.lang.Float.intBitsToFloat(runSequence(
        target,
        Vec(move(3, floatBits(a).toLong & 0xffffffffL), move(4, floatBits(b).toLong & 0xffffffffL), operation),
        computePgmRsrc1 = rsrc1
      ))
    def same(actual: Float, expected: Float): Unit =
      assertEquals(floatBits(actual), floatBits(expected), s"$actual != $expected")
    // HSA defaults: IEEE mode with output denormals, so the hardware ignores OMOD.
    same(float(1.5f, 1f, multiply2, CodeObjectKernel.DefaultComputePgmRsrc1), 1.5f)
    same(float(-0f, 1f, multiply2, CodeObjectKernel.DefaultComputePgmRsrc1), -0f)
    same(float(1.5f, 1f, multiply2, ieee), 1.5f)
    // FP32 denormal modes 3 (none flushed) and 2 (inputs flushed) keep output denormals, so OMOD is ignored.
    same(float(1.5f, 1f, multiply2, 3 << 16), 1.5f)
    same(float(1.5f, 1f, multiply2, 2 << 16), 1.5f)
    // Modes 0 and 1 flush output denormals: OMOD applies, flushes a denormal result and forces -0 to +0.
    same(float(1.5f, 1f, multiply2, 0), 3f)
    same(float(1.5f, 1f, multiply2, 1 << 16), 3f)
    same(float(-3f, 1f, divide2, 0), -1.5f)
    same(float(-0f, 1f, multiply2, 0), 0f)
    same(float(1e-40f, 1f, multiply2, 0), 0f)
    same(float(java.lang.Float.MIN_NORMAL, 1f, divide2, 0), 0f)
    same(float(0.75f, 1f, clampedMultiply2, 0), 1f)
    same(float(0.75f, 1f, clampedMultiply2, ieee), 0.75f)
    // FP16 and FP64 results follow FLOAT_DENORM_MODE_16_64 rather than the FP32 field.
    def doubleHigh(rsrc1: Int): Int = runSequence(
      target,
      Vec(
        Instruction.VMove64(8, InstructionSource.Constant(java.lang.Double.doubleToRawLongBits(1.5))),
        Instruction.VMove64(10, InstructionSource.Constant(java.lang.Double.doubleToRawLongBits(1.0))),
        double2,
        Instruction.VMove(2, InstructionSource.VectorRegister(7))
      ),
      computePgmRsrc1 = rsrc1
    )
    assertEquals(doubleHigh(3 << 16), 0x40080000)
    assertEquals(doubleHigh(3 << 18), 0x3ff80000)
    def half(rsrc1: Int): Int = runSequence(
      target,
      Vec(move(3, 0x3e00), move(4, 0x3c00), move(5, 0), half2),
      computePgmRsrc1 = rsrc1
    ) & 0xffff
    assertEquals(half(3 << 16), 0x4200)
    assertEquals(half(3 << 18), 0x3e00)
    // LLVM gfx900: s_setreg_imm32_b32 hwreg(HW_REG_MODE, 9, 1), 0; s_setreg_b32 hwreg(HW_REG_MODE, 4, 2), s0.
    val clearIEEE                                 = decoded(target, 0x41, 0x02, 0x00, 0xba, 0x00, 0x00, 0x00, 0x00)
    val writeDenormalMode                         = decoded(target, 0x01, 0x09, 0x00, 0xb9)
    def afterModeWrites(denormalMode: Int): Float = java.lang.Float.intBitsToFloat(runSequence(
      target,
      Vec(
        Instruction.SMove(0, InstructionSource.Constant(denormalMode)),
        clearIEEE,
        writeDenormalMode,
        move(3, floatBits(1.5f).toLong),
        move(4, floatBits(1f).toLong),
        multiply2
      )
    ))
    same(afterModeWrites(0), 3f)
    same(afterModeWrites(3), 1.5f)
  }

  test("GFX10 s_denorm_mode writes the FP_DENORM fields that decide OMOD") {
    val target = Target("gfx1030")
    // LLVM gfx1030: v_mul_f32_e64 v2, v3, v4 mul:2; s_denorm_mode 0 / 0xf.
    val multiply2                       = decoded(target, 0x02, 0x00, 0x08, 0xd5, 0x03, 0x09, 0x02, 0x08)
    def product(denormalMode: Int): Int = runSequence(
      target,
      Vec(
        decoded(target, denormalMode, 0x00, 0xa5, 0xbf),
        move(3, floatBits(1.5f).toLong),
        move(4, floatBits(1f).toLong),
        multiply2
      ),
      computePgmRsrc1 = 3 << 16
    )
    assertEquals(product(0), floatBits(3f))
    assertEquals(product(0xf), floatBits(1.5f))
  }

  test("f16 operations read inline float constants as binary16 in the low half and zero above") {
    def run(target: Target, a: Long, b: Long, operation: Instruction*): Int =
      runSequence(target, Vec(move(3, a), move(4, b)) ++ Vec.from(operation))
    val gfx900  = Target("gfx900")
    val gfx1030 = Target("gfx1030")
    // LLVM gfx900: v_add_f16 v2, 1.0, v3; v_fma_f16 v2, v3, 2.0, v4;
    // v_add_f16 v2, 0.15915494, v3; v_cvt_f32_f16 v2, -2.0; v_add_f16_sdwa v2, 1.0, v3 (WORD_0, preserve).
    assertEquals(run(gfx900, 0x4000, 0, decoded(gfx900, 0xf2, 0x06, 0x04, 0x3e)) & 0xffff, 0x4200)
    assertEquals(
      run(gfx900, 0x3e00, 0x3800, decoded(gfx900, 0x02, 0x00, 0x06, 0xd2, 0x03, 0xe9, 0x11, 0x04)) & 0xffff,
      0x4300
    )
    assertEquals(run(gfx900, 0, 0, decoded(gfx900, 0xf8, 0x06, 0x04, 0x3e)) & 0xffff, 0x3118)
    assertEquals(run(gfx900, 0, 0, decoded(gfx900, 0xf5, 0x16, 0x04, 0x7e)), floatBits(-2f))
    assertEquals(
      run(gfx900, 0x4000, 0, decoded(gfx900, 0xf9, 0x06, 0x04, 0x3e, 0xf2, 0x16, 0x84, 0x04)) & 0xffff,
      0x4200
    )
    // LLVM gfx900: v_cmp_lt_f16 vcc, 1.0, v3.
    val compare = decoded(gfx900, 0xf2, 0x06, 0x42, 0x7c)
    for ((value, expected) <- Seq(0x4000L -> 1, 0x3800L -> 0))
      assertEquals(
        run(gfx900, value, 0, compare, Instruction.VMove(2, InstructionSource.Scalar(Fixed.VccLo))) & 1,
        expected
      )
    // Packed f16 sources take the constant as 0:f16, so OP_SEL_HI=1 feeds zero to the high half on every generation.
    // LLVM gfx900: v_pk_mul_f16 v2, 0.5, v3 and op_sel_hi:[0,1]; gfx1030: v_pk_mul_f16 v2, 0.5, v3.
    assertEquals(
      run(gfx900, 0x40004400, 0, decoded(gfx900, 0x02, 0x40, 0x90, 0xd3, 0xf0, 0x06, 0x02, 0x18)),
      0x00004000
    )
    assertEquals(
      run(gfx900, 0x40004400, 0, decoded(gfx900, 0x02, 0x40, 0x90, 0xd3, 0xf0, 0x06, 0x02, 0x10)),
      0x3c004000
    )
    assertEquals(
      run(gfx1030, 0x40004400, 0, decoded(gfx1030, 0x02, 0x40, 0x10, 0xcc, 0xf0, 0x06, 0x02, 0x18)),
      0x00004000
    )
    // Integer 16-bit operations keep the single-precision pattern. LLVM gfx900: v_pk_add_u16 v2, 1.0, v3.
    assertEquals(
      run(gfx900, 0x00010002, 0, decoded(gfx900, 0x02, 0x40, 0x8a, 0xd3, 0xf2, 0x06, 0x02, 0x18)),
      0x3f810002
    )
    // LLVM gfx1030: v_add_f16 v2, 1.0, v3; v_fma_f16 v2, v3, 2.0, v4.
    assertEquals(run(gfx1030, 0x4000, 0, decoded(gfx1030, 0xf2, 0x06, 0x04, 0x64)) & 0xffff, 0x4200)
    assertEquals(
      run(gfx1030, 0x3e00, 0x3800, decoded(gfx1030, 0x02, 0x00, 0x4b, 0xd7, 0x03, 0xe9, 0x11, 0x04)) & 0xffff,
      0x4300
    )
  }

  test("VOP3-encoded f16 VOP1 and VOP2 opcodes apply modifiers, clamp, OMOD and the GFX9 high-half zeroing") {
    val target                                  = Target("gfx900")
    def run(a: Long, b: Long, bytes: Int*): Int =
      runSequence(target, Vec(move(3, a), move(4, b), decoded(target, bytes*)), computePgmRsrc1 = 0)
    def bits(value: Float): Long = floatBits(value).toLong & 0xffffffffL
    // LLVM gfx900: v_add_f16_e64 v2, v3, v4; v_add_f16_e64 v2, -v3, |v4| clamp; v_mul_f16_e64 v2, v3, v4 mul:2;
    // v_min_f16_e64 v2, -v3, v4; v_max_f16_e64 v2, v3, |v4|; v_cvt_f32_f16_e64 v2, -v3;
    // v_cvt_f16_f32_e64 v2, |v3| clamp; v_cvt_i16_f16_e64 v2, -v3.
    assertEquals(run(0xdead3c00L, 0x4000, 0x02, 0x00, 0x1f, 0xd1, 0x03, 0x09, 0x02, 0x00), 0x4200)
    assertEquals(run(0xb800, 0xb400, 0x02, 0x82, 0x1f, 0xd1, 0x03, 0x09, 0x02, 0x20), 0x3a00)
    assertEquals(run(0xc000, 0xb400, 0x02, 0x82, 0x1f, 0xd1, 0x03, 0x09, 0x02, 0x20), 0x3c00)
    assertEquals(run(0x3e00, 0x4000, 0x02, 0x00, 0x22, 0xd1, 0x03, 0x09, 0x02, 0x08), 0x4600)
    assertEquals(run(0x3c00, 0x3800, 0x02, 0x00, 0x2e, 0xd1, 0x03, 0x09, 0x02, 0x20), 0xbc00)
    assertEquals(run(0x3800, 0xc000, 0x02, 0x02, 0x2d, 0xd1, 0x03, 0x09, 0x02, 0x00), 0x4000)
    assertEquals(run(0x3e00, 0, 0x02, 0x00, 0x4b, 0xd1, 0x03, 0x01, 0x00, 0x20), floatBits(-1.5f))
    assertEquals(run(bits(-3f), 0, 0x02, 0x81, 0x4a, 0xd1, 0x03, 0x01, 0x00, 0x00), 0x3c00)
    assertEquals(run(0x4500, 0, 0x02, 0x00, 0x7c, 0xd1, 0x03, 0x01, 0x00, 0x20), 0xfffb)
  }

  test("GFX10 onwards preserves the high half of f16 results in e32 and e64 forms") {
    val target                             = Target("gfx1030")
    def run(operations: Instruction*): Int =
      runSequence(target, Vec(move(2, 0xabcd3c00L), move(3, 0x3e00), move(4, 0x4000)) ++ Vec.from(operations))
    // LLVM gfx1030: v_add_f16_e64 v2, v3, v4; v_add_f16 v2, v3, v4; v_fmac_f16_e64 v2, -v3, v4;
    // v_fmac_f16_e64 v2, v3, v4 clamp; v_fmac_f16 v2, v3, v4; v_cvt_f16_f32_e64 v2, v3; v_cvt_f16_f32 v2, v3.
    assertEquals(run(decoded(target, 0x02, 0x00, 0x32, 0xd5, 0x03, 0x09, 0x02, 0x00)), 0xabcd4300)
    assertEquals(run(decoded(target, 0x03, 0x09, 0x04, 0x64)), 0xabcd4300)
    assertEquals(run(decoded(target, 0x02, 0x00, 0x36, 0xd5, 0x03, 0x09, 0x02, 0x20)), 0xabcdc000)
    assertEquals(run(decoded(target, 0x02, 0x80, 0x36, 0xd5, 0x03, 0x09, 0x02, 0x00)), 0xabcd3c00)
    assertEquals(run(decoded(target, 0x03, 0x09, 0x04, 0x6c)), 0xabcd4400)
    assertEquals(
      run(move(3, floatBits(1.5f).toLong), decoded(target, 0x02, 0x00, 0x8a, 0xd5, 0x03, 0x01, 0x00, 0x00)),
      0xabcd3e00
    )
    assertEquals(run(move(3, floatBits(1.5f).toLong), decoded(target, 0x03, 0x15, 0x04, 0x7e)), 0xabcd3e00)
  }

  test("GFX11 VOP3 op_sel selects the f16 halves of sources and destination") {
    val target                                  = Target("gfx1100")
    def run(a: Long, b: Long, bytes: Int*): Int =
      runSequence(
        target,
        Vec(move(2, 0xabcd1234L), move(3, a), move(4, b), decoded(target, bytes*)),
        computePgmRsrc1 = 0
      )
    // LLVM gfx1100 real-true16: v_add_f16_e64 v2.h, v3.h, v4.l; v_cvt_f32_f16_e64 v2, v3.h; v_cvt_f16_f32_e64 v2.h, v3;
    // v_fmac_f16_e64 v2.h, v3.h, v4.h; v_mul_f16_e64 v2.h, v3.l, v4.l mul:2; v_add_f16_e64 v2.l, v3.l, v4.l.
    assertEquals(run(0x3c000000L, 0x4000, 0x02, 0x48, 0x32, 0xd5, 0x03, 0x09, 0x02, 0x00), 0x42001234)
    assertEquals(run(0x3e000000L, 0, 0x02, 0x08, 0x8b, 0xd5, 0x03, 0x01, 0x00, 0x00), floatBits(1.5f))
    assertEquals(run(floatBits(1.5f).toLong, 0, 0x02, 0x40, 0x8a, 0xd5, 0x03, 0x01, 0x00, 0x00), 0x3e001234)
    assertEquals(
      runSequence(
        target,
        Vec(
          move(2, 0x3c001234L),
          move(3, 0x3e000000L),
          move(4, 0x40000000L),
          decoded(target, 0x02, 0x58, 0x36, 0xd5, 0x03, 0x09, 0x02, 0x00)
        )
      ),
      0x44001234
    )
    assertEquals(run(0x3e00, 0x4000, 0x02, 0x40, 0x35, 0xd5, 0x03, 0x09, 0x02, 0x08), 0x46001234)
    assertEquals(run(0x3c00, 0x4000, 0x02, 0x00, 0x32, 0xd5, 0x03, 0x09, 0x02, 0x00), 0xabcd4200)
  }

  test("mixed-precision FMA and MAD sources read inline constants as binary16 only when op_sel_hi selects f16") {
    def run(target: Target, a: Float, b: Float, bytes: Int*): Int = runSequence(
      target,
      Vec(
        move(3, floatBits(a).toLong & 0xffffffffL),
        move(4, floatBits(b).toLong & 0xffffffffL),
        decoded(target, bytes*)
      )
    )
    for ((target, opcode) <- Seq(Target("gfx906") -> 0xd3, Target("gfx1030") -> 0xcc, Target("gfx900") -> 0xd3)) {
      val base = if (opcode == 0xd3) 0xa0 else 0x20
      // LLVM: v_fma_mix_f32 (gfx906, gfx1030) or v_mad_mix_f32 (gfx900) v2, 0x3c00, v3, v4 op_sel_hi:[1,0,0] and
      // v2, 1.0, v3, v4; v_*_mixlo_f16 v2, v3, 0x4000, v4 op_sel_hi:[0,1,0]; v_*_mixhi_f16 v2, v3, v4, 0x3800
      // op_sel_hi:[0,0,1].
      assertEquals(run(target, 2f, 0.5f, 0x02, 0x00, base, opcode, 0xf2, 0x06, 0x12, 0x0c), floatBits(2.5f), target)
      assertEquals(run(target, 2f, 0.5f, 0x02, 0x00, base, opcode, 0xf2, 0x06, 0x12, 0x04), floatBits(2.5f), target)
      assertEquals(run(target, 1.5f, 0.5f, 0x02, 0x00, base + 1, opcode, 0x03, 0xe9, 0x11, 0x14), 0x11224300, target)
      assertEquals(run(target, 1.5f, 2f, 0x02, 0x40, base + 2, opcode, 0x03, 0x09, 0xc2, 0x03), 0x43003344, target)
    }
    // V_MAD_MIX rounds the product before the addition. LLVM gfx900: v_mad_mix_f32 v2, v3, v4, v5.
    val operand = java.lang.Float.intBitsToFloat(0x3f800800)
    val target  = Target("gfx900")
    assertEquals(
      runSequence(
        target,
        Vec(
          move(3, floatBits(operand).toLong),
          move(4, floatBits(operand).toLong),
          move(5, floatBits(-1f).toLong & 0xffffffffL),
          decoded(target, 0x02, 0x00, 0xa0, 0xd3, 0x03, 0x09, 0x16, 0x04)
        )
      ),
      0x3a000000
    )
  }

  test("f32 and f64 operations read 1/(2*pi) as the hardware's exact patterns") {
    val target = Target("gfx900")
    // LLVM gfx900: v_add_f32 v2, 0.15915494, v3; v_add_f64 v[6:7], 0.15915494309189532, v[8:9] and 1.0, v[8:9].
    assertEquals(runSequence(target, Vec(move(3, 0), decoded(target, 0xf8, 0x06, 0x04, 0x02))), 0x3e22f983)
    def double(bytes: Int*)(word: VectorRegisterIndex): Int = runSequence(
      target,
      Vec(
        Instruction.VMove64(8, InstructionSource.Constant(0)),
        decoded(target, bytes*),
        Instruction.VMove(2, InstructionSource.VectorRegister(word))
      )
    )
    val inverseTwoPi = double(0x06, 0x00, 0x80, 0xd2, 0xf8, 0x10, 0x02, 0x00)
    assertEquals((inverseTwoPi(6), inverseTwoPi(7)), (0x6dc9c882, 0x3fc45f30))
    val one = double(0x06, 0x00, 0x80, 0xd2, 0xf2, 0x10, 0x02, 0x00)
    assertEquals((one(6), one(7)), (0, 0x3ff00000))
  }

  test("VOP3 clamp applies to f64, f16, ternary and integer results whose IR has no clamp of its own") {
    val target                                                        = Target("gfx900")
    def bits(value: Float): Long                                      = floatBits(value).toLong & 0xffffffffL
    def bits64(value: Double): Long                                   = java.lang.Double.doubleToRawLongBits(value)
    def run32(operation: Instruction, a: Long, b: Long, c: Long): Int =
      runSequence(target, Vec(move(3, a), move(4, b), move(5, c), operation))
    def run64(operation: Instruction, a: Long, b: Long, c: Long = 0L, low: Boolean = false): Int = runSequence(
      target,
      Vec(
        Instruction.VMove64(8, InstructionSource.Constant(a)),
        Instruction.VMove64(10, InstructionSource.Constant(b)),
        Instruction.VMove64(12, InstructionSource.Constant(c)),
        move(3, 0xffffffffL),
        move(4, 0xffffffffL),
        operation,
        Instruction.VMove(2, InstructionSource.VectorRegister(if (low) 6 else 7))
      )
    )
    // LLVM gfx900, each with clamp: v_add_f64 v[6:7], v[8:9], v[10:11]; v_fma_f16, v_min3_f32, v_mad_u32_u24,
    // v_mad_i32_i24 and v_mad_u16 v2, v3, v4, v5; v_mad_u64_u32 v[6:7], s[0:1], v3, v4, v[8:9];
    // v_pack_b32_f16 v2, v3, v4; v_div_scale_f32 v2, vcc, v3, v4, v5.
    assertEquals(
      run64(decoded(target, 0x06, 0x80, 0x80, 0xd2, 0x08, 0x15, 0x02, 0x00), bits64(1.5), bits64(0.25)),
      0x3ff00000
    )
    assertEquals(
      run32(decoded(target, 0x02, 0x80, 0x06, 0xd2, 0x03, 0x09, 0x16, 0x04), 0x3e00, 0x3c00, 0) & 0xffff,
      0x3c00
    )
    assertEquals(
      run32(decoded(target, 0x02, 0x80, 0xd0, 0xd1, 0x03, 0x09, 0x16, 0x04), bits(2f), bits(3f), bits(4f)),
      floatBits(1f)
    )
    assertEquals(run32(decoded(target, 0x02, 0x80, 0xc3, 0xd1, 0x03, 0x09, 0x16, 0x04), 0xffffff, 0xffffff, 0), -1)
    assertEquals(
      run32(decoded(target, 0x02, 0x80, 0xc2, 0xd1, 0x03, 0x09, 0x16, 0x04), 0x800000, 0x7fffff, 0),
      Int.MinValue
    )
    assertEquals(run32(decoded(target, 0x02, 0x80, 0x04, 0xd2, 0x03, 0x09, 0x16, 0x04), 0xffff, 2, 0) & 0xffff, 0xffff)
    assertEquals(run64(decoded(target, 0x06, 0x80, 0xe8, 0xd1, 0x03, 0x09, 0x22, 0x04), -1L, 0L), -1)
    assertEquals(run32(decoded(target, 0x02, 0x80, 0xa0, 0xd2, 0x03, 0x09, 0x02, 0x00), 0x3e00, 0xb800, 0), 0x3c00)
    assertEquals(
      run32(decoded(target, 0x02, 0xea, 0xe0, 0xd1, 0x03, 0x09, 0x16, 0x04), bits(1.5f), bits(2f), bits(3f)),
      floatBits(1f)
    )
    // RDNA keeps the clamp of the 64-bit multiply-add. LLVM gfx1030: v_mad_u64_u32 v[6:7], s0, v3, v4, v[8:9] clamp.
    val rdnaMultiplyAdd = runSequence(
      Target("gfx1030"),
      Vec(
        Instruction.VMove64(8, InstructionSource.Constant(-1L)),
        move(3, 0xffffffffL),
        move(4, 0xffffffffL),
        decoded(Target("gfx1030"), 0x06, 0x80, 0x76, 0xd5, 0x03, 0x09, 0x22, 0x04),
        Instruction.VMove(2, InstructionSource.VectorRegister(7))
      )
    )
    assertEquals(rdnaMultiplyAdd, -1)
    // LLVM gfx1030: v_add_nc_u16 v2, v3, v4 clamp.
    assertEquals(
      runSequence(
        Target("gfx1030"),
        Vec(move(3, 0xffff), move(4, 1), decoded(Target("gfx1030"), 0x02, 0x80, 0x03, 0xd7, 0x03, 0x09, 0x02, 0x00))
      ) & 0xffff,
      0xffff
    )
  }

  test("division, half-precision and packing VOP3 opcodes apply their source modifiers") {
    val target                                                        = Target("gfx900")
    def bits(value: Float): Long                                      = floatBits(value).toLong & 0xffffffffL
    def bits64(value: Double): Long                                   = java.lang.Double.doubleToRawLongBits(value)
    def run32(operation: Instruction, a: Long, b: Long, c: Long): Int =
      runSequence(target, Vec(move(3, a), move(4, b), move(5, c), operation))
    def run64(operation: Instruction, a: Double, b: Double, c: Double): Int = runSequence(
      target,
      Vec(
        Instruction.SMove64(Fixed.VccLo, InstructionSource.Constant(0)),
        Instruction.VMove64(8, InstructionSource.Constant(bits64(a))),
        Instruction.VMove64(10, InstructionSource.Constant(bits64(b))),
        Instruction.VMove64(12, InstructionSource.Constant(bits64(c))),
        operation,
        Instruction.VMove(2, InstructionSource.VectorRegister(7))
      )
    )
    // LLVM gfx900: v_div_scale_f32 v2, vcc, -v3, v4, v5; v_div_scale_f64 v[6:7], vcc, -v[8:9], v[10:11], v[12:13];
    // v_div_fmas_f64 v[6:7], -v[8:9], v[10:11], v[12:13]; v_div_fmas_f32 v2, v3, -v4, |v5|; v_fma_f16 v2, |v3|, v4, v5;
    // v_min3_f32 v2, -v3, v4, v5; v_pack_b32_f16 v2, -v3, |v4|, v2, v3, v4 and v2, v3, v4 op_sel:[1,0,0].
    assertEquals(
      run32(decoded(target, 0x02, 0x6a, 0xe0, 0xd1, 0x03, 0x09, 0x16, 0x24), bits(1.5f), bits(2f), bits(3f)),
      floatBits(-1.5f)
    )
    assertEquals(run64(decoded(target, 0x06, 0x6a, 0xe1, 0xd1, 0x08, 0x15, 0x32, 0x24), 1.5, 2.0, 3.0), 0xbff80000)
    assertEquals(run64(decoded(target, 0x06, 0x00, 0xe3, 0xd1, 0x08, 0x15, 0x32, 0x24), 1.5, 2.0, 0.5), 0xc0040000)
    val fmas = Vec(
      Instruction.SMove64(Fixed.VccLo, InstructionSource.Constant(0)),
      move(3, bits(1.5f)),
      move(4, bits(2f)),
      move(5, bits(-0.5f)),
      decoded(target, 0x02, 0x04, 0xe2, 0xd1, 0x03, 0x09, 0x16, 0x44)
    )
    assertEquals(runSequence(target, fmas), floatBits(-2.5f))
    assertEquals(
      run32(decoded(target, 0x02, 0x01, 0x06, 0xd2, 0x03, 0x09, 0x16, 0x04), 0xbe00, 0x3c00, 0) & 0xffff,
      0x3e00
    )
    assertEquals(
      run32(decoded(target, 0x02, 0x00, 0xd0, 0xd1, 0x03, 0x09, 0x16, 0x24), bits(2f), bits(3f), bits(4f)),
      floatBits(-2f)
    )
    assertEquals(run32(decoded(target, 0x02, 0x02, 0xa0, 0xd2, 0x03, 0x09, 0x02, 0x20), 0x3c00, 0xc000, 0), 0x4000bc00)
    assertEquals(run32(decoded(target, 0x02, 0x00, 0xa0, 0xd2, 0x03, 0x09, 0x02, 0x00), 0x3c00, 0x4000, 0), 0x40003c00)
    assertEquals(
      run32(decoded(target, 0x02, 0x08, 0xa0, 0xd2, 0x03, 0x09, 0x02, 0x00), 0x12343c00, 0x4000, 0),
      0x40001234
    )
  }

  test("MODE reads back through s_getreg, and GFX12 writes cannot enable the retired IEEE mode") {
    // LLVM gfx900: s_setreg_imm32_b32 hwreg(HW_REG_MODE, 4, 4), 5; s_getreg_b32 s1, hwreg(HW_REG_MODE, 4, 4);
    // s_getreg_b32 s1, hwreg(HW_REG_MODE, 0, 32); s_setreg_imm32_b32 hwreg(HW_REG_MODE, 9, 1), 1;
    // s_getreg_b32 s1, hwreg(HW_REG_MODE, 9, 1). gfx1200 encodes the same s_setreg as 0xb980.
    val gfx900                                              = Target("gfx900")
    def read(target: Target, operations: Instruction*): Int =
      runSequence(
        target,
        Vec.from(operations) :+ Instruction.VMove(2, InstructionSource.Scalar(1)),
        computePgmRsrc1 = 0
      )
    assertEquals(
      read(
        gfx900,
        decoded(gfx900, 0x01, 0x19, 0x00, 0xba, 0x05, 0x00, 0x00, 0x00),
        decoded(gfx900, 0x01, 0x19, 0x81, 0xb8)
      ),
      5
    )
    assertEquals(
      runSequence(
        gfx900,
        Vec(decoded(gfx900, 0x01, 0xf8, 0x81, 0xb8), Instruction.VMove(2, InstructionSource.Scalar(1))),
        computePgmRsrc1 = (1 << 23) | (0xa << 16)
      ),
      (1 << 9) | (0xa << 4)
    )
    assertEquals(
      read(
        gfx900,
        decoded(gfx900, 0x41, 0x02, 0x00, 0xba, 0x01, 0x00, 0x00, 0x00),
        decoded(gfx900, 0x41, 0x02, 0x81, 0xb8)
      ),
      1
    )
    val gfx1200 = Target("gfx1200")
    val setIEEE = decoded(gfx1200, 0x41, 0x02, 0x80, 0xb9, 0x01, 0x00, 0x00, 0x00)
    assertEquals(read(gfx1200, setIEEE, decoded(gfx1200, 0x41, 0x02, 0x81, 0xb8)), 0)
    // OMOD still applies after the write: FP32 output denormals stay flushed and GFX12 has no IEEE mode.
    assertEquals(
      runSequence(
        gfx1200,
        Vec(
          setIEEE,
          move(3, floatBits(1.5f).toLong),
          move(4, floatBits(1f).toLong),
          decoded(gfx1200, 0x02, 0x00, 0x08, 0xd5, 0x03, 0x09, 0x02, 0x08)
        ),
        computePgmRsrc1 = 0
      ),
      floatBits(3f)
    )
  }

  test("GCN V_CMPX writes its scalar destination as well as EXEC, while GFX10 onwards writes only EXEC") {
    // LLVM: gfx900 v_cmpx_eq_u32_e64 s[54:55], v12, v11 and v_cmpx_eq_u32_e32 vcc, v12, v11;
    // gfx1030 v_cmpx_eq_u32_e64 v12, v11 and v_cmpx_eq_u32_e32 v12, v11.
    val cases = Vec(
      (Target("gfx900"), Vec(0x36, 0x00, 0xda, 0xd0, 0x0c, 0x17, 0x02, 0x00), 54, true),
      (Target("gfx900"), Vec(0x0c, 0x17, 0xb4, 0x7d), Fixed.VccLo, true),
      (Target("gfx1030"), Vec(0x7e, 0x00, 0xd2, 0xd4, 0x0c, 0x17, 0x02, 0x00), Fixed.VccLo, false),
      (Target("gfx1030"), Vec(0x0c, 0x17, 0xa4, 0x7d), Fixed.VccLo, false)
    )
    val stale = 0x5a5a5a5aL
    for {
      (target, bytes, register, written) <- cases
      equal                              <- Vec(false, true)
    } {
      val mask       = register.unsafe[ScalarRegisterIndex]
      val operations = Vec(
        Instruction.SMove(mask, InstructionSource.Constant(stale)),
        Instruction.SMove(mask.offsetBy(1), InstructionSource.Constant(stale)),
        Instruction.SMove(20, InstructionSource.Scalar(Fixed.ExecLo)),
        move(11, 5),
        move(12, if (equal) 5 else 6),
        decoded(target, bytes.map(_.toByte)),
        Instruction.SMove(Fixed.ExecLo, InstructionSource.Scalar(20)),
        Instruction.VMove(2, InstructionSource.Scalar(mask))
      )
      val live     = runSequence(target, Vec(Instruction.VMove(2, InstructionSource.Scalar(Fixed.ExecLo))))
      val expected = if (!written) stale.toInt else if (equal) live else 0
      assertEquals(runSequence(target, operations), expected, clues(target, bytes, equal))
    }
  }

  test("scalar moves materialise inline floating constants at their operand width") {
    val target = Target("gfx1036")
    for (
      (encoding, value) <- Vec(
        0xf0 -> 0.5,
        0xf1 -> -0.5,
        0xf2 -> 1.0,
        0xf3 -> -1.0,
        0xf4 -> 2.0,
        0xf5 -> -2.0,
        0xf6 -> 4.0,
        0xf7 -> -4.0
      )
    ) {
      // RDNA2 s_mov_b64 s[26:27], inline; GCC uses this to construct its FP64 0.5 multiplier.
      val move64 = decoded(target, encoding, 0x04, 0x9a, 0xbe)
      val bits64 = java.lang.Double.doubleToRawLongBits(value)
      for ((register, expected) <- Vec[(ScalarRegisterIndex, Int)]((26, bits64.toInt), (27, (bits64 >>> 32).toInt)))
        assertEquals(
          runSequence(target, Vec(move64, Instruction.VMove(2, InstructionSource.Scalar(register)))),
          expected,
          clues(value, register)
        )
      val move32 = decoded(target, encoding, 0x03, 0x9a, 0xbe)
      assertEquals(
        runSequence(target, Vec(move32, Instruction.VMove(2, InstructionSource.Scalar(26)))),
        java.lang.Float.floatToRawIntBits(value.toFloat)
      )
    }
  }

  test("FP64 inline floats retain their identity through moves and arithmetic") {
    for (
      (operand, bits) <- Vec(
        InstructionSource.Floating(0.5)         -> java.lang.Double.doubleToRawLongBits(1.0),
        InstructionSource.Constant(1L)          -> 2L,
        InstructionSource.Constant(0x3f000000L) -> 0x7e000000L,
        InstructionSource.Literal(0x3fe00000L)  -> java.lang.Double.doubleToRawLongBits(1.0)
      )
    ) {
      val multiply = Instruction.VBinary64(
        VectorBinary64Operation.Multiply,
        4,
        operand,
        InstructionSource.Floating(2.0),
        SourceModifiers.Unmodified
      )
      for ((register, expected) <- Vec[(VectorRegisterIndex, Int)]((4, bits.toInt), (5, (bits >>> 32).toInt)))
        assertEquals(
          runSequence(
            Target("gfx1036"),
            Vec(multiply, Instruction.VMove(2, InstructionSource.VectorRegister(register)))
          ),
          expected,
          clues(operand, register)
        )
    }
    val target = Target("gfx942")
    val move   = decoded(target, 0xf0, 0x70, 0x06, 0x7e)
    for ((register, expected) <- Vec[(VectorRegisterIndex, Int)]((3, 0), (4, 0x3fe00000)))
      assertEquals(
        runSequence(target, Vec(move, Instruction.VMove(2, InstructionSource.VectorRegister(register)))),
        expected
      )
  }

  test("RDNA lane operations honour descriptor wave width, inactive lanes and wrapped selectors") {
    val target = Target("gfx1036")
    for (wave32 <- Vec(false, true)) {
      val width = if (wave32) 32 else 64
      for (selector <- Vec(31, 63, 95, -1))
        assertEquals(
          runSequence(
            target = target,
            operations = Vec(
              Instruction.VWriteLane(6, InstructionSource.Constant(11), InstructionSource.Constant(31)),
              Instruction.VWriteLane(6, InstructionSource.Constant(22), InstructionSource.Constant(selector.toLong)),
              Instruction.VReadLane(
                22,
                InstructionSource.VectorRegister(6),
                InstructionSource.Constant(selector & (width - 1))
              ),
              Instruction.VMove(2, InstructionSource.Scalar(22))
            ),
            wave32 = wave32
          ),
          22
        )
      assertEquals(
        runSequence(
          target = target,
          operations = Vec(
            Instruction.VWriteLane(6, InstructionSource.Constant(11), InstructionSource.Constant(31)),
            Instruction.VWriteLane(6, InstructionSource.Constant(22), InstructionSource.Constant(63)),
            Instruction.VReadLane(22, InstructionSource.VectorRegister(6), InstructionSource.Constant(31)),
            Instruction.VMove(2, InstructionSource.Scalar(22))
          ),
          wave32 = wave32
        ),
        if (wave32) 22 else 11
      )
    }
  }

  test("allocation-backed kernargs preserve the packet address and implicit bytes beyond the declared block") {
    for (declaredSize <- Vec[Int >= 0](0, 8)) {
      val memory     = RegionMemory()
      val allocation = AllocationId(1L)
      memory.allocate(allocation, 128)
      val bufferOffset: Long >= 0 = 16L
      val address                 = memory.address(allocation, bufferOffset).value.raw
      memory.putInt(allocation, 68, 0x12345678)
      // Dispatch pointer occupies s[0:1], original kernarg pointer s[2:3].
      val operations = Vec(
        Instruction.SLoad(dest = 4, count = 2, base = 0, offset = 40, scalarOffset = None),
        Instruction.SLoad(dest = 6, count = 1, base = 2, offset = 52, scalarOffset = None),
        Instruction.VMove(0, InstructionSource.Scalar(2)),
        Instruction.VMove(1, InstructionSource.Scalar(3)),
        Instruction.VMove(2, InstructionSource.Scalar(4)),
        Instruction.VMove(3, InstructionSource.Scalar(5)),
        Instruction.Store(2, MemoryAddress.Global(0, None, 0), 8, false),
        Instruction.VMove(2, InstructionSource.Scalar(6)),
        Instruction.Store(2, MemoryAddress.Global(0, None, 60), 4, false),
        Instruction.End
      )
      val kernel = irKernel(
        operations,
        codeKernel(
          target = Target("gfx1036"),
          kernargSize = declaredSize,
          kernelCodeProperties = 0xa
        )
      )
      def task(offset: Long >= 0) = KernelTask.validated(
        "kernel",
        Dimensions.One,
        Dimensions.One,
        Vec.empty,
        argumentBuffer = Some(KernelTask.ArgumentBuffer(allocation, offset))
      ).value
      assertEquals(executeKernel(kernel, task(bufferOffset), memory), Right(()))
      assertEquals(memory.readValue(allocation, bufferOffset, 8), Right(address))
      assertEquals(memory.getInt(allocation, 76), 0x12345678)
      assert(executeKernel(kernel, task(128L), memory).isLeft)
    }
  }

  test("scalar leading-bit opcodes distinguish unsigned zeros from signed sign runs") {
    val target = Target("gfx1036")
    // LLVM encodings: s_flbit_i32_b32 and s_flbit_i32, both s25, s25.
    for ((word, signed) <- Vec(0xbe991519 -> false, 0xbe991719 -> true)) {
      val operation = decoded(target, words(word))
      for (
        (value, unsignedResult, signedResult) <- Vec(
          (0, -1, -1),
          (-1, 0, -1),
          (1, 31, 31),
          (-2, 0, 31),
          (2, 30, 30),
          (-3, 0, 30),
          (Int.MaxValue, 1, 1),
          (Int.MinValue, 0, 1),
          (0x40000000, 1, 1),
          (0xc0000000, 0, 2),
          (0x00800000, 8, 8),
          (0xff7fffff, 0, 8)
        )
      ) assertEquals(
        runSequence(
          target,
          Vec(
            Instruction.SMove(25, InstructionSource.Constant(value)),
            operation,
            Instruction.VMove(2, InstructionSource.Scalar(25))
          )
        ),
        if (signed) signedResult else unsignedResult,
        clues(value, signed)
      )
    }
  }

  test("64-bit scalar leading-bit opcodes read both words and distinguish signed inputs") {
    val target = Target("gfx1036")
    // LLVM encodings: s_flbit_i32_b64 and s_flbit_i32_i64, both s26, s[26:27].
    for ((word, signed) <- Vec(0xbe9a161a -> false, 0xbe9a181a -> true)) {
      val operation = decoded(target, words(word))
      for (
        (value, unsignedResult, signedResult) <- Vec(
          (0L, -1, -1),
          (-1L, 0, -1),
          (1L, 63, 63),
          (-2L, 0, 63),
          (2L, 62, 62),
          (-3L, 0, 62),
          (Long.MaxValue, 1, 1),
          (Long.MinValue, 0, 1),
          (0x80000000L, 32, 32),
          (0x100000000L, 31, 31),
          (0xffffffff00000000L, 0, 32),
          (0xfffffffeffffffffL, 0, 31),
          (0xc000000000000000L, 0, 2)
        )
      ) assertEquals(
        runSequence(
          target,
          Vec(
            Instruction.SMove64(26, InstructionSource.Constant(value)),
            operation,
            Instruction.VMove(2, InstructionSource.Scalar(26))
          )
        ),
        if (signed) signedResult else unsignedResult,
        clues(value, signed)
      )
    }
  }

  test("SOPK comparisons use signed or unsigned 16-bit immediates across boundary values") {
    val target = Target("gfx1036")
    // LLVM instruction upper words for s_cmpk_* s22; the lower word is the immediate.
    val comparisons = Vec[(Int, Boolean, Int => Boolean)](
      (0xb196, true, _ == 0),
      (0xb216, true, _ != 0),
      (0xb296, true, _ > 0),
      (0xb316, true, _ >= 0),
      (0xb396, true, _ < 0),
      (0xb416, true, _ <= 0),
      (0xb496, false, _ == 0),
      (0xb516, false, _ != 0),
      (0xb596, false, _ > 0),
      (0xb616, false, _ >= 0),
      (0xb696, false, _ < 0),
      (0xb716, false, _ <= 0)
    )
    for {
      (encoding, signed, predicate) <- comparisons
      immediate                     <- Vec(0, 0x7fff, 0x8000, 0xffff)
    } {
      val operation = decoded(target, words((encoding << 16) | immediate))
      for (value <- Vec(Int.MinValue, -1, 0, 32767, 32768, 65535, 65536, Int.MaxValue)) {
        val left     = if (signed) value.toLong else Integer.toUnsignedLong(value)
        val right    = if (signed) immediate.toShort.toLong else immediate.toLong
        val expected = if (predicate(java.lang.Long.compare(left, right))) 1 else 0
        assertEquals(
          runSequence(
            target,
            Vec(
              Instruction.SMove(22, InstructionSource.Constant(Integer.toUnsignedLong(value))),
              operation,
              Instruction.VMove(2, InstructionSource.Condition(ScalarBranchCondition.SccNonZero))
            )
          ),
          expected,
          clues(encoding, immediate, value)
        )
      }
    }
  }

  test("condition sources read current SCC, VCCZ and EXECZ state") {
    val target = Target("gfx1036")
    // LLVM gfx1036 encodings of s_mov_b32 s22, src_scc/src_vccz/src_execz.
    for {
      (selector, condition) <- Vec(
        0xfd -> ScalarBranchCondition.SccNonZero,
        0xfb -> ScalarBranchCondition.VccZero,
        0xfc -> ScalarBranchCondition.ExecZero
      )
      expected <- Vec(0, 1)
    } {
      val move = decoded(target, selector, 0x03, 0x96, 0xbe)
      assertEquals(move, Instruction.SMove(22, InstructionSource.Condition(condition)))
      val setup = condition match {
        case ScalarBranchCondition.SccNonZero => Instruction.SCmp(
            ScalarCompareOperation.EqualU32,
            InstructionSource.Constant(expected.toLong),
            InstructionSource.Constant(1L)
          )
        case ScalarBranchCondition.VccZero => Instruction.SMove(Fixed.VccLo, InstructionSource.Constant(1L - expected))
        case _                             => Instruction.SMove(Fixed.ExecLo, InstructionSource.Constant(1L - expected))
      }
      for (operation <- Vec(move, Instruction.SMove64(22, InstructionSource.Condition(condition))))
        assertEquals(
          runSequence(
            target,
            Vec(
              setup,
              operation,
              Instruction.SMove(Fixed.ExecLo, InstructionSource.Constant(1L)),
              Instruction.VMove(2, InstructionSource.Scalar(22))
            )
          ),
          expected
        )
    }
  }

  for (
    (target, groupRegister, descriptor) <- Vec[(Target, ScalarRegisterIndex, Int)](
      (Target("gfx1100"), 15, 0x9e),
      (Target("gfx1200"), 117, 0x84)
    )
  )
    test(s"$target workgroup ID follows its kernel ABI register allocation") {
      val memory     = RegionMemory()
      val allocation = AllocationId(1L)
      memory.allocate(allocation, 12)
      val address    = memory.address(allocation, 0L).value.raw
      val operations = Vec(
        Instruction.SMove(6, InstructionSource.Constant(address & 0xffffffffL)),
        Instruction.SMove(7, InstructionSource.Constant(address >>> 32)),
        Instruction.VBinary(
          VectorBinaryOperation.AddU32,
          2,
          InstructionSource.Scalar(groupRegister),
          InstructionSource.Constant(10),
          false
        ),
        Instruction.VBinary(
          VectorBinaryOperation.ShiftLeft32,
          4,
          InstructionSource.Constant(2),
          InstructionSource.Scalar(groupRegister),
          false
        ),
        Instruction.Store(2, MemoryAddress.Global(4, Some(6), 0), 4, false),
        Instruction.End
      )
      // The real HSA probe requests 15 user SGPRs but initialises only the kernarg pointer.
      val source = codeKernel(target = target, computePgmRsrc2 = descriptor, kernelCodeProperties = 0x8)
      val task   = KernelTask.validated("kernel", Dimensions.One, Dimensions(3, 1, 1), Vec.empty).value
      assertEquals(runIR(operations, memory, source, task), Right(()))
      assertEquals(Vec[Int >= 0](0, 4, 8).map(offset => memory.getInt(allocation, offset)), Vec(10, 11, 12))
    }

  test("RDNA global and flat increment aliases preserve width and return operands") {
    // LLVM fixtures: *_atomic_inc_u32/u64 v4/v[4:5], v[0:1], v2/v[2:3], returning.
    for (
      (target, bytes, width, global) <- Vec[(Target, Vec[Int], Int in (4, 8), Boolean)](
        (Target("gfx1100"), Vec(0x00, 0x40, 0xfe, 0xdc, 0x00, 0x02, 0x7c, 0x04), 4, true),
        (Target("gfx1100"), Vec(0x00, 0x40, 0x32, 0xdd, 0x00, 0x02, 0x7c, 0x04), 8, true),
        (Target("gfx1100"), Vec(0x00, 0x40, 0xfc, 0xdc, 0x00, 0x02, 0x7c, 0x04), 4, false),
        (Target("gfx1100"), Vec(0x00, 0x40, 0x30, 0xdd, 0x00, 0x02, 0x7c, 0x04), 8, false),
        (Target("gfx1200"), Vec(0x7c, 0xc0, 0x0f, 0xee, 0x04, 0x00, 0x10, 0x01, 0x00, 0x00, 0x00, 0x00), 4, true),
        (Target("gfx1200"), Vec(0x7c, 0x00, 0x13, 0xee, 0x04, 0x00, 0x10, 0x01, 0x00, 0x00, 0x00, 0x00), 8, true),
        (Target("gfx1200"), Vec(0x7c, 0xc0, 0x0f, 0xec, 0x04, 0x00, 0x10, 0x01, 0x00, 0x00, 0x00, 0x00), 4, false),
        (Target("gfx1200"), Vec(0x7c, 0x00, 0x13, 0xec, 0x04, 0x00, 0x10, 0x01, 0x00, 0x00, 0x00, 0x00), 8, false)
      )
    ) {
      val address = if (global) MemoryAddress.Global(0, None, 0) else MemoryAddress.Flat(0, 0)
      assertEquals(
        VMCompiler.decodeInstruction(bytes.map(_.toByte), target),
        Right(Instruction.Atomic(AtomicOperation.IncrementWrap, 4, address, 2, width, true))
      )
    }
  }

  test("gfx906 fmac uses fused rounding in e32 and e64 encodings") {
    val target   = Target("gfx906")
    val value    = 1.0000001192092896f
    val previous = -1.000000238418579f
    val expected = java.lang.Float.floatToRawIntBits(Math.fma(value, value, previous))
    assert(expected != java.lang.Float.floatToRawIntBits(value * value + previous))
    // LLVM gfx906: v_fmac_f32_e32/e64 v2, v3, v4.
    for (bytes <- Vec(Vec(0x03, 0x09, 0x04, 0x76), Vec(0x02, 0x00, 0x3b, 0xd1, 0x03, 0x09, 0x02, 0x00))) {
      val fmac = decoded(target, bytes.map(_.toByte))
      assertEquals(
        runSequence(
          target = target,
          operations = Vec(
            Instruction.VMove(3, InstructionSource.Constant(java.lang.Float.floatToRawIntBits(value).toLong)),
            Instruction.VMove(4, InstructionSource.Constant(java.lang.Float.floatToRawIntBits(value).toLong)),
            fmac
          ),
          initial = java.lang.Float.floatToRawIntBits(previous)
        ),
        expected
      )
      for (unsupported <- Vec(Target("gfx9"), Target("gfx900"), Target("gfx904"), Target("gfx90c")))
        assert(VMCompiler.decodeInstruction(bytes.map(_.toByte), unsupported).isLeft, unsupported.name)
    }
  }

  test("CDNA and RDNA launch packed work-item coordinates across waves") {
    for (
      target <- Seq(
        Target("gfx90a"),
        Target("gfx942"),
        Target("gfx950"),
        Target("gfx1250"),
        Target("gfx1100"),
        Target("gfx1200")
      )
    ) {
      val memory     = RegionMemory()
      val allocation = AllocationId(1L)
      memory.allocate(allocation, 512)
      val address = memory.address(allocation, 0L).value.raw
      def binary(
          op: VectorBinaryOperation,
          dest: VectorRegisterIndex,
          a: InstructionSource,
          b: InstructionSource
      ): Instruction =
        Instruction.VBinary(op, dest, a, b, clamp = false)
      import InstructionSource.{VectorRegister as Register, *}
      val operations = Vec(
        Instruction.SMove64(6, Constant(address)),
        binary(VectorBinaryOperation.And32, 3, Register(0), Constant(1023)),
        binary(VectorBinaryOperation.ShiftRight32, 4, Constant(10), Register(0)),
        binary(VectorBinaryOperation.And32, 4, Register(4), Constant(1023)),
        binary(VectorBinaryOperation.ShiftLeft32, 4, Constant(3), Register(4)),
        binary(VectorBinaryOperation.ShiftRight32, 5, Constant(20), Register(0)),
        binary(VectorBinaryOperation.ShiftLeft32, 5, Constant(5), Register(5)),
        binary(VectorBinaryOperation.AddU32, 3, Register(3), Register(4)),
        binary(VectorBinaryOperation.AddU32, 3, Register(3), Register(5)),
        binary(VectorBinaryOperation.ShiftLeft32, 3, Constant(2), Register(3)),
        binary(VectorBinaryOperation.Or32, 0, Register(0), Constant(0x40000000)),
        Instruction.Store(0, MemoryAddress.Global(3, Some(6), 0), 4, false),
        Instruction.End
      )
      val task = KernelTask.validated("kernel", Dimensions(8, 4, 4), Dimensions.One, Vec.empty).value
      assertEquals(runIR(operations, memory, codeKernel(target = target, computePgmRsrc2 = 2 << 11), task), Right(()))
      for {
        z <- 0 until 4
        y <- 0 until 4
        x <- 0 until 8
      } {
        val offset = ((x + 8 * y + 32 * z) * 4).unsafe[Int >= 0]
        assertEquals(memory.getInt(allocation, offset), 0x40000000 | x | (y << 10) | (z << 20))
      }
    }
  }

  test("DPP routes source 0 of VOP1 and VOP2 operations across the live lanes of each wave") {
    val inactive                                 = 3
    def row(lane: Int, source: Int): Option[Int] = Option.when(source >= 0 && source < 16)((lane & ~15) | source)
    def octet(selectors: Int*)(lane: Int): Int   = (lane & ~7) | selectors(lane & 7)
    def add(source: Long): Long                  = source + 1000L
    def move(source: Long): Long                 = source
    // LLVM-encoded, v3 destination, v2 routed source, v4 second source; v2 holds the work-item index plus one.
    val cases = Seq[(Target, String, Vec[Int], Int => Option[Int], Long => Long)](
      (
        Target("gfx900"),
        "v_add_u32_dpp v3, v2, v4 row_shr:1 bound_ctrl:1",
        Vec(0xfa, 0x08, 0x06, 0x68, 0x02, 0x11, 0x09, 0xff),
        lane => row(lane, (lane & 15) - 1),
        add
      ),
      (
        Target("gfx900"),
        "v_add_u32_dpp v3, v2, v4 row_shr:1",
        Vec(0xfa, 0x08, 0x06, 0x68, 0x02, 0x11, 0x01, 0xff),
        lane => row(lane, (lane & 15) - 1),
        add
      ),
      (
        Target("gfx900"),
        "v_add_u32_dpp v3, v2, v4 row_shr:1 row_mask:0xe bound_ctrl:1",
        Vec(0xfa, 0x08, 0x06, 0x68, 0x02, 0x11, 0x09, 0xef),
        lane => row(lane, (lane & 15) - 1),
        add
      ),
      (
        Target("gfx900"),
        "v_not_b32_dpp v3, v2 row_shr:1 bound_ctrl:1",
        Vec(0xfa, 0x56, 0x06, 0x7e, 0x02, 0x11, 0x09, 0xff),
        lane => row(lane, (lane & 15) - 1),
        source => ~source & 0xffffffffL
      ),
      (
        Target("gfx900"),
        "v_mov_b32_dpp v3, v2 quad_perm:[1,0,3,2] bank_mask:0x5",
        Vec(0xfa, 0x02, 0x06, 0x7e, 0x02, 0xb1, 0x00, 0xf5),
        lane => Some(lane ^ 1),
        move
      ),
      (
        Target("gfx900"),
        "v_mov_b32_dpp v3, v2 row_ror:4",
        Vec(0xfa, 0x02, 0x06, 0x7e, 0x02, 0x24, 0x01, 0xff),
        lane => row(lane, (lane - 4) & 15),
        move
      ),
      (
        Target("gfx900"),
        "v_mov_b32_dpp v3, v2 row_mirror",
        Vec(0xfa, 0x02, 0x06, 0x7e, 0x02, 0x40, 0x01, 0xff),
        lane => row(lane, 15 - (lane & 15)),
        move
      ),
      (
        Target("gfx900"),
        "v_mov_b32_dpp v3, v2 row_half_mirror",
        Vec(0xfa, 0x02, 0x06, 0x7e, 0x02, 0x41, 0x01, 0xff),
        lane => Some((lane & ~7) | (7 - (lane & 7))),
        move
      ),
      (
        Target("gfx900"),
        "v_mov_b32_dpp v3, v2 wave_shl:1 bound_ctrl:1",
        Vec(0xfa, 0x02, 0x06, 0x7e, 0x02, 0x30, 0x09, 0xff),
        lane => Option.when(lane < 63)(lane + 1),
        move
      ),
      (
        Target("gfx900"),
        "v_mov_b32_dpp v3, v2 wave_shr:1 bound_ctrl:1",
        Vec(0xfa, 0x02, 0x06, 0x7e, 0x02, 0x38, 0x09, 0xff),
        lane => Option.when(lane > 0)(lane - 1),
        move
      ),
      (
        Target("gfx900"),
        "v_mov_b32_dpp v3, v2 wave_rol:1",
        Vec(0xfa, 0x02, 0x06, 0x7e, 0x02, 0x34, 0x01, 0xff),
        lane => Some((lane + 1) & 63),
        move
      ),
      (
        Target("gfx900"),
        "v_mov_b32_dpp v3, v2 wave_ror:1",
        Vec(0xfa, 0x02, 0x06, 0x7e, 0x02, 0x3c, 0x01, 0xff),
        lane => Some((lane - 1) & 63),
        move
      ),
      (
        Target("gfx900"),
        "v_mov_b32_dpp v3, v2 row_bcast:15 bound_ctrl:1",
        Vec(0xfa, 0x02, 0x06, 0x7e, 0x02, 0x42, 0x09, 0xff),
        lane => Option.when(lane >= 16)((lane & ~15) - 1),
        move
      ),
      (
        Target("gfx900"),
        "v_mov_b32_dpp v3, v2 row_bcast:15",
        Vec(0xfa, 0x02, 0x06, 0x7e, 0x02, 0x42, 0x01, 0xff),
        lane => Option.when(lane >= 16)((lane & ~15) - 1),
        move
      ),
      (
        Target("gfx900"),
        "v_mov_b32_dpp v3, v2 row_bcast:31 bound_ctrl:1",
        Vec(0xfa, 0x02, 0x06, 0x7e, 0x02, 0x43, 0x09, 0xff),
        lane => Option.when(lane >= 32)(31),
        move
      ),
      (
        Target("gfx90a"),
        "v_add_u32_dpp v3, v2, v4 row_newbcast:5",
        Vec(0xfa, 0x08, 0x06, 0x68, 0x02, 0x55, 0x01, 0xff),
        lane => row(lane, 5),
        add
      ),
      (
        Target("gfx1030"),
        "v_add_nc_u32_dpp v3, v2, v4 row_share:5",
        Vec(0xfa, 0x08, 0x06, 0x4a, 0x02, 0x55, 0x01, 0xff),
        lane => row(lane, 5),
        add
      ),
      (
        Target("gfx1030"),
        "v_add_nc_u32_dpp v3, v2, v4 row_xmask:6",
        Vec(0xfa, 0x08, 0x06, 0x4a, 0x02, 0x66, 0x01, 0xff),
        lane => row(lane, (lane & 15) ^ 6),
        add
      ),
      (
        Target("gfx1030"),
        "v_add_nc_u32_dpp v3, v2, v4 row_shr:1 bound_ctrl:1",
        Vec(0xfa, 0x08, 0x06, 0x4a, 0x02, 0x11, 0x09, 0xff),
        lane => row(lane, (lane & 15) - 1),
        add
      ),
      (
        Target("gfx1030"),
        "v_add_nc_u32_dpp v3, v2, v4 row_shr:1 fi:1",
        Vec(0xfa, 0x08, 0x06, 0x4a, 0x02, 0x11, 0x05, 0xff),
        lane => row(lane, (lane & 15) - 1),
        add
      ),
      (
        Target("gfx1030"),
        "v_add_nc_u32_dpp v3, v2, v4 row_mirror",
        Vec(0xfa, 0x08, 0x06, 0x4a, 0x02, 0x40, 0x01, 0xff),
        lane => row(lane, 15 - (lane & 15)),
        add
      ),
      (
        Target("gfx1030"),
        "v_add_nc_u32_dpp v3, v2, v4 dpp8:[7,6,5,4,3,2,1,0]",
        Vec(0xe9, 0x08, 0x06, 0x4a, 0x02, 0x77, 0x39, 0x05),
        lane => Some(octet(7, 6, 5, 4, 3, 2, 1, 0)(lane)),
        add
      ),
      (
        Target("gfx1030"),
        "v_add_nc_u32_dpp v3, v2, v4 dpp8:[3,3,0,1,4,4,7,2] fi:1",
        Vec(0xea, 0x08, 0x06, 0x4a, 0x02, 0x1b, 0x42, 0x5e),
        lane => Some(octet(3, 3, 0, 1, 4, 4, 7, 2)(lane)),
        add
      ),
      (
        Target("gfx1030"),
        "v_mov_b32_dpp v3, v2 dpp8:[1,2,3,4,5,6,7,0]",
        Vec(0xe9, 0x02, 0x06, 0x7e, 0x02, 0xd1, 0x58, 0x1f),
        lane => Some(octet(1, 2, 3, 4, 5, 6, 7, 0)(lane)),
        move
      )
    )
    for ((target, name, bytes, sourceLane, operation) <- cases) {
      val octets        = bytes.head == 0xe9 || bytes.head == 0xea
      val fetchInactive = if (octets) bytes.head == 0xea else target.isa.isRDNA && (bytes(6) & 0x04) != 0
      val boundControl  = !octets && (bytes(6) & 0x08) != 0
      val rowMask       = if (octets) 0xf else (bytes(7) >>> 4) & 0xf
      val bankMask      = if (octets) 0xf else bytes(7) & 0xf
      val waveSize      = if (target.isa.isRDNA) 32 else 64
      val memory        = RegionMemory()
      val allocation    = AllocationId(1L)
      memory.allocate(allocation, 256)
      val address = memory.address(allocation, 0L).value.raw
      import InstructionSource.{VectorRegister as Register, *}
      val operations = Vec(
        Instruction.SMove64(6, Constant(address)),
        Instruction.VBinary(VectorBinaryOperation.ShiftLeft32, 1, Constant(2), Register(0), false),
        Instruction.VBinary(VectorBinaryOperation.AddU32, 2, Register(0), Constant(1), false),
        Instruction.VMove(3, Constant(7)),
        Instruction.VMove(4, Constant(1000)),
        Instruction.SMove64(Fixed.ExecLo, Constant(~(1L << inactive))),
        decoded(target, bytes.map(_.toByte)),
        Instruction.SMove64(Fixed.ExecLo, Constant(-1L)),
        Instruction.Store(3, MemoryAddress.Global(1, Some(6), 0), 4, false),
        Instruction.End
      )
      val source = codeKernel(
        target = target,
        kernelCodeProperties = if (waveSize == 32) KernelCodeProperty.ENABLE_WAVEFRONT_SIZE32 else 0
      )
      val task = KernelTask.validated("kernel", Dimensions(64, 1, 1), Dimensions.One, Vec.empty).value
      assertEquals(runIR(operations, memory, source, task), Right(()), name)
      for (item <- 0 until 64) {
        val lane    = item % waveSize
        val base    = item - lane
        val enabled =
          lane != inactive &&
            (rowMask & (1 << (lane / 16))) != 0 &&
            (bankMask & (1 << ((lane >>> 2) & 3))) != 0
        val routed   = sourceLane(lane).filter(from => fetchInactive || from != inactive)
        val expected =
          if (!enabled) 7L
          else routed.map(from => operation(base + from + 1L))
            .getOrElse(if (boundControl || octets) operation(0L) else 7L)
        assertEquals(
          memory.getInt(allocation, (item * 4).unsafe[Int >= 0]).toLong & 0xffffffffL,
          expected,
          s"$name lane $item"
        )
      }
    }
  }

  for (
    (target, name, bytes) <- Seq(
      (Target("gfx1030"), "wave_shr:1", Vec(0xfa, 0x02, 0x06, 0x7e, 0x02, 0x38, 0x09, 0xff)),
      (Target("gfx1030"), "row_bcast:15", Vec(0xfa, 0x02, 0x06, 0x7e, 0x02, 0x42, 0x09, 0xff)),
      (Target("gfx1030"), "row_bcast:31", Vec(0xfa, 0x02, 0x06, 0x7e, 0x02, 0x43, 0x09, 0xff)),
      (Target("gfx900"), "row_share:5", Vec(0xfa, 0x02, 0x06, 0x7e, 0x02, 0x55, 0x01, 0xff)),
      (Target("gfx900"), "row_xmask:6", Vec(0xfa, 0x02, 0x06, 0x7e, 0x02, 0x66, 0x01, 0xff)),
      (Target("gfx90a"), "row_xmask:6", Vec(0xfa, 0x02, 0x06, 0x7e, 0x02, 0x66, 0x01, 0xff)),
      (Target("gfx900"), "row_shl:0", Vec(0xfa, 0x02, 0x06, 0x7e, 0x02, 0x00, 0x01, 0xff)),
      (Target("gfx900"), "unused control 0x131", Vec(0xfa, 0x02, 0x06, 0x7e, 0x02, 0x31, 0x01, 0xff))
    )
  ) test(s"$target rejects DPP $name") {
    val result = VMCompiler.decodeInstruction(bytes.map(_.toByte), target)
    assert(result.left.exists(_.contains("DPP control")), result)
  }

  test("gfx90a accumulator moves copy between accumulator registers") {
    val target = Target("gfx90a")
    // LLVM gfx90a: v_accvgpr_mov_b32 a1, a2.
    val move = decoded(target, 0x02, 0xa5, 0x02, 0x7e)
    assertEquals(
      runSequence(
        target = target,
        operations = Vec(
          Instruction.VAccumulatorWrite(1, InstructionSource.Constant(0x5a5a5a5aL)),
          Instruction.VAccumulatorWrite(2, InstructionSource.Constant(0x12345678L)),
          move,
          Instruction.VAccumulatorRead(2, 1)
        )
      ),
      0x12345678
    )
  }

  test("RDNA4 scalar arithmetic rounds unsigned conversion and preserves full-width multiplication") {
    val target  = Target("gfx1200")
    val convert = decoded(target, 1, 0x65, 0x85, 0xbe)
    for ((input, expected) <- Seq(0xffffffffL -> 0x4f800000, 16777217L -> 0x4b800000))
      assertEquals(
        runSequence(
          target,
          Vec(
            Instruction.SMove(1, InstructionSource.Constant(input)),
            convert,
            Instruction.VMove(2, InstructionSource.Scalar(5))
          )
        ),
        expected
      )
    val toInteger = decoded(target, 1, 0x67, 0x85, 0xbe)
    for ((input, expected) <- Seq(3.75f -> 3, -1.0f -> 0, Float.NaN -> 0, Float.PositiveInfinity -> -1))
      assertEquals(
        runSequence(
          target,
          Vec(
            Instruction.SMove(1, InstructionSource.Constant(java.lang.Float.floatToRawIntBits(input).toLong)),
            toInteger,
            Instruction.VMove(2, InstructionSource.Scalar(5))
          )
        ),
        expected
      )
    val multiply = decoded(target, 1, 2, 5, 0xa2)
    assertEquals(
      runSequence(
        target,
        Vec(
          Instruction.SMove(1, InstructionSource.Constant(0x3fc00000L)),
          Instruction.SMove(2, InstructionSource.Constant(0xc0000000L)),
          multiply,
          Instruction.VMove(2, InstructionSource.Scalar(5))
        )
      ),
      0xc0400000
    )
    val multiply64 = decoded(target, 0, 2, 0x84, 0xaa)
    for ((register, expected) <- Vec[(ScalarRegisterIndex, Int)]((4, -2), (5, -1)))
      assertEquals(
        runSequence(
          target,
          Vec(
            Instruction.SMove64(0, InstructionSource.Constant(-1)),
            Instruction.SMove64(2, InstructionSource.Constant(2)),
            multiply64,
            Instruction.VMove(2, InstructionSource.Scalar(register))
          )
        ),
        expected
      )
  }

  test("GCN scalar loads distinguish register, immediate, and combined offsets") {
    for (target <- Seq(Target("gfx900"), Target("gfx942"), Target("gfx950")))
      // LLVM s_load_dword s22,s[12:13] with s1+4, s1, and immediate8 offsets.
      for (
        (bytes, registerOffset) <- Seq(
          (Vec(0x86, 0x45, 2, 0xc0, 4, 0, 0, 2), 4),
          (Vec(0x86, 5, 0, 0xc0, 1, 0, 0, 0), 8),
          (Vec(0x86, 5, 2, 0xc0, 8, 0, 0, 0), 123)
        )
      ) {
        val load = decoded(target, bytes.map(_.toByte))
        assertEquals(
          runSequence(
            target,
            Vec(
              Instruction.VReadFirstLane(12, InstructionSource.VectorRegister(0)),
              Instruction.VReadFirstLane(13, InstructionSource.VectorRegister(1)),
              Instruction.SMove(1, InstructionSource.Constant(registerOffset)),
              load,
              Instruction.VMove(2, InstructionSource.Scalar(22))
            )
          ),
          0x89ab8080
        )
      }
  }

  test("GFX6 and GFX7 SMRD offsets count immediate dwords, register bytes, and GFX7 literal dwords") {
    // LLVM bonaire/tahiti: s_load_dword s22, s[12:13], 0x2 / s1 / 0x402 (bonaire only, trailing literal).
    val cases = Seq(
      (Target("gfx600"), Vec(0x02, 0x0d, 0x0b, 0xc0), 0, 0x89ab8080),
      (Target("gfx600"), Vec(0x01, 0x0c, 0x0b, 0xc0), 8, 0x89ab8080),
      (Target("gfx700"), Vec(0x02, 0x0d, 0x0b, 0xc0), 0, 0x89ab8080),
      (Target("gfx700"), Vec(0x01, 0x0c, 0x0b, 0xc0), 0x1008, 0x13579bdf),
      (Target("gfx700"), Vec(0xff, 0x0c, 0x0b, 0xc0, 0x02, 0x04, 0x00, 0x00), 0, 0x13579bdf)
    )
    for ((target, bytes, registerOffset, expected) <- cases) {
      val memory     = RegionMemory()
      val allocation = AllocationId(1L)
      memory.allocate(allocation, 0x1010)
      memory.putInt(allocation, 8, 0x89ab8080)
      memory.putInt(allocation, 0x1008, 0x13579bdf)
      val address    = memory.address(allocation, 0L).value.raw
      val operations = Vec(
        Instruction.SMove64(12, InstructionSource.Constant(address)),
        Instruction.SMove(1, InstructionSource.Constant(registerOffset)),
        decoded(target, bytes.map(_.toByte)),
        Instruction.VMove(2, InstructionSource.Scalar(22)),
        Instruction.Store(2, MemoryAddress.Global(1, Some(12), 0), 4, false),
        Instruction.End
      )
      assertEquals(runIR(operations, memory, codeKernel(target = target)), Right(()), clues(target, bytes))
      assertEquals(memory.getInt(allocation, 0), expected, clues(target, bytes))
    }
  }

  test("CDNA4 ternary bit operations use all three inputs and truncate 16-bit results") {
    val target = Target("gfx950")
    // LLVM encodings for XOR3, majority, and 16-bit conditional selection.
    for (
      (bytes, expected) <- Seq(
        (Vec(2, 2, 0x34, 0xd2, 3, 9, 0x16, 0xd4), 0x96969696),
        (Vec(2, 5, 0x34, 0xd2, 3, 9, 0x16, 0x1c), 0xe8e8e8e8),
        (Vec(2, 1, 0x33, 0xd2, 3, 9, 0x16, 0x5c), 0xcaca)
      )
    ) {
      val operation = decoded(target, bytes.map(_.toByte))
      assertEquals(
        runSequence(
          target,
          Vec(
            Instruction.VMove(3, InstructionSource.Constant(0xf0f0f0f0L)),
            Instruction.VMove(4, InstructionSource.Constant(0xccccccccL)),
            Instruction.VMove(5, InstructionSource.Constant(0xaaaaaaaaL)),
            operation
          )
        ),
        expected
      )
    }
  }

  test("RDNA4 numeric min/max preserves the numeric operand and signed zero") {
    val target = Target("gfx1200")
    // Fixed LLVM VOP2 min/max and VOP3 min with absolute/negate modifiers.
    for (
      (bytes, modified, maximum) <- Seq(
        (Vec(3, 9, 4, 0x2a), false, false),
        (Vec(3, 9, 4, 0x2c), false, true),
        (Vec(2, 1, 0x15, 0xd5, 3, 9, 2, 0x40), true, false)
      )
    ) {
      val operation = decoded(target, bytes.map(_.toByte))
      for ((left, right) <- Seq((Float.NaN, 2.0f), (2.0f, Float.NaN), (0.0f, -0.0f), (Float.NaN, Float.NaN))) {
        val a        = if (modified) math.abs(left) else left
        val b        = if (modified) -right else right
        val expected = if (a.isNaN) b else if (b.isNaN) a else if (maximum) math.max(a, b) else math.min(a, b)
        val actual   = runSequence(
          target,
          Vec(
            Instruction.VMove(3, InstructionSource.Constant(java.lang.Float.floatToRawIntBits(left).toLong)),
            Instruction.VMove(4, InstructionSource.Constant(java.lang.Float.floatToRawIntBits(right).toLong)),
            operation
          )
        )
        if (expected.isNaN) assert(java.lang.Float.intBitsToFloat(actual).isNaN)
        else assertEquals(actual, java.lang.Float.floatToRawIntBits(expected))
      }
    }
  }

  test("16-bit logical right shift excludes the upper source half") {
    // Fixed LLVM encoding: v_lshrrev_b16 v2, 8, v3.
    for (target <- Seq(Target("gfx1100"), Target("gfx1200"))) {
      val shift =
        decoded(target, 0x02, 0x00, 0x39, 0xd7, 0x88, 0x06, 0x02, 0x00)
      assertEquals(
        runSequence(
          target,
          Vec(
            Instruction.VMove(3, InstructionSource.Constant(0xa5ff8012L)),
            shift
          )
        ),
        0x80
      )
    }
  }

  test("RDNA4 scalar fused multiply-add reads the accumulator and instruction literals") {
    val target = Target("gfx1200")
    // LLVM encodings: fmac s0,s1,s2; fmaak s0,s1,s2,1.5; fmamk s0,s1,1.5,s2.
    for (
      (bytes, expected) <- Seq(
        (Vec(1, 2, 0x80, 0xa3), -5.5f),
        (Vec(1, 2, 0x80, 0xa2, 0, 0, 0xc0, 0x3f), -4.5f),
        (Vec(1, 2, 0, 0xa3, 0, 0, 0xc0, 0x3f), 0.0f)
      )
    ) {
      val operation = decoded(target, bytes.map(_.toByte))
      assertEquals(
        runSequence(
          target,
          Vec(
            Instruction.SMove(0, InstructionSource.Constant(0x3f000000L)),
            Instruction.SMove(1, InstructionSource.Constant(0x40000000L)),
            Instruction.SMove(2, InstructionSource.Constant(0xc0400000L)),
            operation,
            Instruction.VMove(2, InstructionSource.Scalar(0))
          )
        ),
        java.lang.Float.floatToRawIntBits(expected)
      )
      if (bytes.size == 8) assert(VMCompiler.decodeInstruction(bytes.take(4).map(_.toByte), target).isLeft)
    }
  }

  test("gfx803 double fractional part clamps the rounded result below one") {
    val target = Target("gfx803")
    // LLVM gfx803: v_fract_f64 v[6:7], v[4:5].
    val fraction = decoded(target, 0x04, 0x65, 0x0c, 0x7e)
    // LLVM's amdgcn_fract constant folding clamps both widths to the predecessor of one.
    for (
      (value, expected) <- Vec(
        1.75                        -> 0.75,
        -1.75                       -> 0.25,
        -0.0                        -> 0.0,
        -2.0                        -> 0.0,
        java.lang.Double.MIN_VALUE  -> java.lang.Double.MIN_VALUE,
        -java.lang.Double.MIN_VALUE -> Math.nextDown(1.0),
        Double.PositiveInfinity     -> Double.NaN,
        Double.NegativeInfinity     -> Double.NaN,
        Double.NaN                  -> Double.NaN
      )
    ) {
      val words = Vec[VectorRegisterIndex](6, 7).map { register =>
        runSequence(
          target,
          Vec(
            Instruction.VMove64(4, InstructionSource.Constant(java.lang.Double.doubleToRawLongBits(value))),
            fraction,
            Instruction.VMove(2, InstructionSource.VectorRegister(register))
          )
        )
      }
      val actual = java.lang.Double.longBitsToDouble((words(0).toLong & 0xffffffffL) | (words(1).toLong << 32))
      if (expected.isNaN) assert(actual.isNaN)
      else assertEquals(java.lang.Double.doubleToRawLongBits(actual), java.lang.Double.doubleToRawLongBits(expected))
    }
  }

  test("RDNA4 scalar float comparisons preserve unordered and signed-zero semantics in SCC") {
    val target = Target("gfx1200")
    // LLVM fixed s_cmp_{neq,ngt,eq}_f32 and s_cmp_neq_f16 encodings.
    for (
      (opcode, left, right, expected) <- Seq(
        (0x45, 0x7fc00000L, 0L, 7),
        (0x45, 0x3f800000L, 0L, 42),
        (0x4d, 0x7fc00000L, 0L, 42),
        (0x4d, 0x3f800000L, 0x3f800000L, 7),
        (0x4b, 0x7fc00000L, 0L, 42),
        (0x4b, 0x40000000L, 0x3f800000L, 7),
        (0x42, 0x80000000L, 0L, 42),
        (0x5d, 0x7e00L, 0L, 42),
        (0x5d, 0x8000L, 0L, 7)
      )
    ) {
      val comparison = decoded(target, 0, 1, opcode, 0xbf)
      assertEquals(
        runSequence(
          target,
          Vec(
            Instruction.SMove(0, InstructionSource.Constant(left)),
            Instruction.SMove(1, InstructionSource.Constant(right)),
            comparison,
            Instruction.SCSelect(8, InstructionSource.Constant(42), InstructionSource.Constant(7), 32),
            Instruction.VMove(2, InstructionSource.Scalar(8))
          )
        ),
        expected
      )
    }
  }

  test("RDNA4 narrow scalar loads zero extend and do not read past the allocation") {
    val target = Target("gfx1200")
    // LLVM s_load_u8/u16 s5,s[2:3],offset; allocation ends at byte 16.
    for ((encoding, offset, expected) <- Seq((0x21, 8, 0x80), (0x61, 8, 0x8080), (0x21, 15, 0), (0x61, 14, 0))) {
      val load = decoded(target, 0x41, encoding, 1, 0xf4, offset, 0, 0, 0xf8)
      assertEquals(
        runSequence(
          target,
          Vec(
            Instruction.VReadFirstLane(2, InstructionSource.VectorRegister(0)),
            Instruction.VReadFirstLane(3, InstructionSource.VectorRegister(1)),
            load,
            Instruction.VMove(2, InstructionSource.Scalar(5))
          )
        ),
        expected
      )
    }
  }

  test("RDNA scratch stores round trip register pairs without aliasing global memory") {
    for (
      (target, store, load) <- Seq(
        (Target("gfx1100"), Vec(8, 0, 0x6d, 0xdc, 0, 2, 0x7c, 0), Vec(8, 0, 0x55, 0xdc, 0, 0, 0x7c, 2)),
        (
          Target("gfx1200"),
          Vec(0x7c, 0xc0, 6, 0xed, 0, 0, 0, 1, 0, 8, 0, 0),
          Vec(0x7c, 0x40, 5, 0xed, 2, 0, 0, 0, 0, 8, 0, 0)
        )
      )
    )
      for ((register, expected) <- Vec[(VectorRegisterIndex, Int)]((2, 0x11223344), (3, 0x55667788)))
        assertEquals(
          runSequence(
            target,
            Vec(
              Instruction.VMove(3, InstructionSource.Constant(0x55667788L)),
              decoded(target, store.map(_.toByte)),
              Instruction.VMove(2, InstructionSource.Constant(0)),
              Instruction.VMove(3, InstructionSource.Constant(0)),
              decoded(target, load.map(_.toByte)),
              Instruction.VMove(2, InstructionSource.VectorRegister(register))
            )
          ),
          expected
        )
  }

  test("RDNA dual moves read both sources before either destination is written") {
    val target = Target("gfx1100")
    // LLVM gfx11 VOPD fixture, with source X set to v2: mov v255,v2 :: mov v6,v255.
    val dual = decoded(target, 0x02, 0x01, 0x10, 0xca, 0xff, 0x01, 0x06, 0xff)
    for ((register, expected) <- Vec[(VectorRegisterIndex, Int)]((255, 23), (6, 17)))
      assertEquals(
        runSequence(
          target,
          Vec(
            Instruction.VMove(255, InstructionSource.Constant(17)),
            dual,
            Instruction.VMove(2, InstructionSource.VectorRegister(register))
          ),
          initial = 23
        ),
        expected
      )
  }

  test("RDNA dual arithmetic uses a fused accumulator and consumes its shared literal") {
    val target     = Target("gfx1100")
    val arithmetic = decoded(target, 0x02, 0x05, 0x00, 0xc9, 0x03, 0x07, 0x06, 0xff)
    val literal    = decoded(target, 0xff, 0x00, 0x10, 0xca, 0x7c, 0x00, 0x06, 0xff, 0x56, 0x34, 0x12, 0xaf)
    val value      = 1.0000001192092896f
    val previous   = -1.000000238418579f
    for ((register, expected) <- Vec[(VectorRegisterIndex, Float)]((255, 4.0f), (6, Math.fma(value, value, previous))))
      assertEquals(
        runSequence(
          target,
          Vec(
            Instruction.VMove(3, InstructionSource.Constant(java.lang.Float.floatToRawIntBits(value).toLong)),
            Instruction.VMove(6, InstructionSource.Constant(java.lang.Float.floatToRawIntBits(previous).toLong)),
            arithmetic,
            Instruction.VMove(2, InstructionSource.VectorRegister(register))
          ),
          initial = java.lang.Float.floatToRawIntBits(2.0f)
        ),
        java.lang.Float.floatToRawIntBits(expected)
      )
    assertEquals(
      runSequence(target, Vec(literal, Instruction.VMove(2, InstructionSource.VectorRegister(255)))),
      0xaf123456
    )
  }

  test("gfx803 legacy unsigned short multiply-add resolves its alias and truncates the result") {
    // LLVM VI fixture: v_mad_u16 v5, -1, v2, v3 (opcode 0x1eb).
    val target = Target("gfx803")
    val mad    = decoded(target, 0x05, 0x00, 0xeb, 0xd1, 0xc1, 0x04, 0x0e, 0x04)
    assertEquals(
      runSequence(
        target = target,
        operations = Vec(
          Instruction.VMove(3, InstructionSource.Constant(0x12340003L)),
          Instruction.VMove(5, InstructionSource.Constant(0xaaaaaaaaL)),
          mad,
          Instruction.VMove(2, InstructionSource.VectorRegister(5))
        ),
        initial = 2
      ),
      1
    )
  }

  test("gfx942 packed float add selects dwords, negates inputs, and preserves overlapping pairs") {
    val target = Target("gfx942")
    // llvm-mc gfx942: v_pk_add_f32 v[4:5], v[4:5], v[6:7], with default,
    // swapped source selection, and independent low/high negation modifiers.
    for {
      (control, sources, low, high) <- Seq(
        (0x40, 0x18, 11.5f, 22.25f),
        (0x48, 0x10, 12.25f, 21.5f),
        (0x42, 0x38, 8.5f, -17.75f)
      )
      (register, expected) <- Vec[(VectorRegisterIndex, Float)]((4, low), (5, high))
    } {
      val add = decoded(target, 0x04, control, 0xb2, 0xd3, 0x04, 0x0d, 0x02, sources)
      assertEquals(
        runSequence(
          target,
          Vec.from(Vec[(VectorRegisterIndex, Float)]((4, 1.5f), (5, 2.25f), (6, 10.0f), (7, 20.0f)).map {
            case (index, value) =>
              Instruction.VMove(index, InstructionSource.Constant(java.lang.Float.floatToRawIntBits(value).toLong))
          }) ++ Vec(add, Instruction.VMove(2, InstructionSource.VectorRegister(register)))
        ),
        java.lang.Float.floatToRawIntBits(expected),
        clues(control, sources, register)
      )
    }
  }

  test("CDNA packed multiply and FMA preserve selection, negation, and overlapping pairs") {
    val target = Target("gfx942")
    // LLVM gfx942 fixtures: default multiply/FMA, then independently selected and negated FMA sources.
    for {
      (bytes, low, high) <- Seq(
        (Seq(0x04, 0x40, 0xb1, 0xd3, 0x04, 0x0d, 0x02, 0x18), 15.0f, 45.0f),
        (Seq(0x04, 0x40, 0xb0, 0xd3, 0x04, 0x0d, 0x22, 0x1c), 18.0f, 49.0f),
        (Seq(0x04, 0x2d, 0xb0, 0xd3, 0x04, 0x0d, 0x22, 0x54), -18.5f, -33.0f)
      )
      (register, expected) <- Vec[(VectorRegisterIndex, Float)]((4, low), (5, high))
    } {
      val operation = decoded(target, Vec.from(bytes.map(_.toByte)))
      assertEquals(
        runSequence(
          target,
          Vec.from(Vec[(
              VectorRegisterIndex,
              Float
          )]((4, 1.5f), (5, 2.25f), (6, 10.0f), (7, 20.0f), (8, 3.0f), (9, 4.0f)).map {
            case (index, value) =>
              Instruction.VMove(index, InstructionSource.Constant(java.lang.Float.floatToRawIntBits(value).toLong))
          }) ++ Vec(operation, Instruction.VMove(2, InstructionSource.VectorRegister(register)))
        ),
        java.lang.Float.floatToRawIntBits(expected),
        clues(bytes, register)
      )
    }
  }

  test("CDNA packed float arithmetic materialises inline constants as single precision") {
    // MiniBUDE's compiled charge terms: 1 - x, and 1 - x*y. Both results select
    // the constant's low dword while reading different dwords of the register pairs.
    // The multiply fixture covers a constant in the first operand as well.
    for {
      target             <- Vec(Target("gfx90a"), Target("gfx942"), Target("gfx950"))
      (bytes, low, high) <- Seq(
        (Seq(0x04, 0x41, 0xb2, 0xd3, 0x04, 0xe5, 0x01, 0x28), -0.5f, -1.25f),
        (Seq(0x04, 0x01, 0xb0, 0xd3, 0x04, 0x0d, 0xca, 0x3b), -14.0f, -44.0f),
        (Seq(0x04, 0x40, 0xb1, 0xd3, 0xf4, 0x08, 0x02, 0x10), 3.0f, 4.5f)
      )
      (register, expected) <- Vec[(VectorRegisterIndex, Float)]((4, low), (5, high))
    } {
      val operation = decoded(target, Vec.from(bytes.map(_.toByte)))
      assertEquals(
        runSequence(
          target,
          Vec.from(Vec[(VectorRegisterIndex, Float)]((4, 1.5f), (5, 2.25f), (6, 10.0f), (7, 20.0f)).map {
            case (index, value) =>
              Instruction.VMove(index, InstructionSource.Constant(java.lang.Float.floatToRawIntBits(value).toLong))
          }) ++ Vec(operation, Instruction.VMove(2, InstructionSource.VectorRegister(register)))
        ),
        java.lang.Float.floatToRawIntBits(expected),
        clues(target, bytes, register)
      )
    }
  }

  test("CDNA double FMA accumulates the destination and sub-dword subtraction selects bytes") {
    val target = Target("gfx942")
    // LLVM gfx942: v_fmac_f64_e32 v[4:5], v[6:7], v[8:9].
    val fma = decoded(target, 0x06, 0x11, 0x08, 0x08)
    for {
      (accumulator, a, b, result) <- Seq(
        (1.25, 1.5, 2.0, 4.25),
        // A separately rounded product loses the low bit and incorrectly yields zero.
        (-18014398509481984.0, 134217729.0, 134217727.0, -1.0)
      )
      expected = java.lang.Double.doubleToRawLongBits(result)
      (register, word) <- Vec[(VectorRegisterIndex, Int)]((4, expected.toInt), (5, (expected >>> 32).toInt))
    }
      assertEquals(
        runSequence(
          target,
          Vec.from(Vec[(VectorRegisterIndex, Double)]((4, accumulator), (6, a), (8, b)).map { case (index, value) =>
            Instruction.VMove64(index, InstructionSource.Constant(java.lang.Double.doubleToRawLongBits(value)))
          }) ++ Vec(fma, Instruction.VMove(2, InstructionSource.VectorRegister(register)))
        ),
        word
      )
    // LLVM gfx942: v_sub_u32_sdwa v2, v2, v3, BYTE_0 sources, DWORD destination.
    val subtract = decoded(target, 0xf9, 0x06, 0x04, 0x6a, 0x02, 0x16, 0x00, 0x00)
    assertEquals(
      runSequence(
        target,
        Vec(
          Instruction.VMove(3, InstructionSource.Constant(0xaaff0010L)),
          subtract
        ),
        initial = 0x12345609
      ),
      -7
    )
  }

  test("gfx803 fractional part rounds negative inputs down and accepts denormals") {
    val target = Target("gfx803")
    // LLVM gfx803: v_fract_f32 v2, v4.
    val fraction = decoded(target, 0x04, 0x37, 0x04, 0x7e)
    for (
      (value, expected) <- Vec(
        1.75f                      -> 0.75f,
        -1.75f                     -> 0.25f,
        -0.25f                     -> 0.75f,
        2.0f                       -> 0.0f,
        -2.0f                      -> 0.0f,
        -0.0f                      -> 0.0f,
        java.lang.Float.MIN_VALUE  -> java.lang.Float.MIN_VALUE,
        -java.lang.Float.MIN_VALUE -> Math.nextDown(1.0f),
        Float.PositiveInfinity     -> Float.NaN,
        Float.NegativeInfinity     -> Float.NaN,
        Float.NaN                  -> Float.NaN
      )
    ) {
      val actual = runSequence(
        target,
        Vec(
          Instruction.VMove(4, InstructionSource.Constant(java.lang.Float.floatToRawIntBits(value).toLong)),
          fraction
        )
      )
      if (expected.isNaN) assert(java.lang.Float.intBitsToFloat(actual).isNaN)
      else assertEquals(actual, java.lang.Float.floatToRawIntBits(expected))
    }
  }

  test("RDNA4 scalar float operations preserve signed zero, special values, and source modifiers") {
    val target = Target("gfx1200")
    // LLVM gfx1200: v_s_rcp_f32 s4, s6; s_trunc_f32 s4, s6.
    val truncate = decoded(target, 0x06, 0x62, 0x84, 0xbe)
    for (
      (value, expected) <- Seq(
        1.75f                  -> 1.0f,
        -1.75f                 -> -1.0f,
        -0.25f                 -> -0.0f,
        -0.0f                  -> -0.0f,
        Float.PositiveInfinity -> Float.PositiveInfinity,
        Float.NaN              -> Float.NaN
      )
    ) {
      val actual = runSequence(
        target,
        Vec(
          Instruction.SMove(6, InstructionSource.Constant(java.lang.Float.floatToRawIntBits(value).toLong)),
          truncate,
          Instruction.VMove(2, InstructionSource.Scalar(4))
        )
      )
      if (expected.isNaN) assert(java.lang.Float.intBitsToFloat(actual).isNaN)
      else assertEquals(actual, java.lang.Float.floatToRawIntBits(expected))
    }
    for (
      (control, modifier, input, expected) <- Seq(
        (0, 0, -4.0f, -0.25f),
        (0, 0x20, -4.0f, 0.25f),
        (1, 0, -4.0f, 0.25f),
        (0x80, 0, 0.5f, 1.0f),
        (0, 0, -0.0f, Float.NegativeInfinity)
      )
    ) {
      val reciprocal = decoded(target, 0x04, control, 0x84, 0xd6, 0x06, 0x00, 0x00, modifier)
      assertEquals(
        runSequence(
          target,
          Vec(
            Instruction.SMove(6, InstructionSource.Constant(java.lang.Float.floatToRawIntBits(input).toLong)),
            reciprocal,
            Instruction.VMove(2, InstructionSource.Scalar(4))
          )
        ),
        java.lang.Float.floatToRawIntBits(expected)
      )
    }
  }

  test("RDNA4 double numeric min/max ignores a single NaN and orders signed zero") {
    val target = Target("gfx1200")
    // LLVM gfx1200 min/max v[4:5], v[6:7], v[8:9].
    for {
      (opcode, left, right, expected) <- Seq(
        (0x1c, Double.NaN, 2.5, 2.5),
        (0x1c, -4.0, Double.NaN, -4.0),
        (0x1c, -0.0, 0.0, 0.0),
        (0x1a, -0.0, 0.0, -0.0),
        (0x1a, Double.NaN, -2.5, -2.5)
      )
      bits = java.lang.Double.doubleToRawLongBits(expected)
      (register, word) <- Vec[(VectorRegisterIndex, Int)]((4, bits.toInt), (5, (bits >>> 32).toInt))
    } {
      val operation = decoded(target, 0x06, 0x11, 0x08, opcode)
      assertEquals(
        runSequence(
          target,
          Vec(
            Instruction.VMove64(6, InstructionSource.Constant(java.lang.Double.doubleToRawLongBits(left))),
            Instruction.VMove64(8, InstructionSource.Constant(java.lang.Double.doubleToRawLongBits(right))),
            operation,
            Instruction.VMove(2, InstructionSource.VectorRegister(register))
          )
        ),
        word
      )
    }
  }

  test("gfx1200 VOP2 64-bit shift handles cross-word bits, masked counts, and overlapping pairs") {
    val target = Target("gfx1200")
    // llvm-mc gfx1200: v_lshlrev_b64_e32 v[4:5], s6, v[4:5].
    val shift = decoded(target, 0x06, 0x08, 0x08, 0x3e)
    for {
      value  <- Seq(0x0123456789abcdefL, -1L)
      amount <- Seq(0, 1, 31, 32, 63, 64, 65)
      expected = value << (amount & 63)
      (register, word) <- Vec[(VectorRegisterIndex, Int)]((4, expected.toInt), (5, (expected >>> 32).toInt))
    }
      assertEquals(
        runSequence(
          target,
          Vec(
            Instruction.VMove(4, InstructionSource.Constant(value & 0xffffffffL)),
            Instruction.VMove(5, InstructionSource.Constant(value >>> 32)),
            Instruction.SMove(6, InstructionSource.Constant(amount.toLong)),
            shift,
            Instruction.VMove(2, InstructionSource.VectorRegister(register))
          )
        ),
        word,
        clues(value, amount, register)
      )
  }

  test("gfx1200 scalar 64-bit add and subtract preserve SCC and handle carry, borrow, wrap, and overlapping pairs") {
    val target = Target("gfx1200")
    // llvm-mc gfx1200: s_{sub,add}_nc_u64 s[4:5], s[4:5], s[6:7].
    val subtract = decoded(target, 0x04, 0x06, 0x04, 0xaa)
    val add      = decoded(target, 0x04, 0x06, 0x84, 0xa9)
    for {
      (operation, left, right, expected) <- Seq(
        (subtract, 0x0000000200000000L, 1L, 0x00000001ffffffffL),
        (subtract, 0L, 1L, -1L),
        (subtract, 0x0123456789abcdefL, 0x100000000L, 0x0123456689abcdefL),
        (subtract, 5L, 5L, 0L),
        (add, 0x00000001ffffffffL, 1L, 0x0000000200000000L),
        (add, -1L, 1L, 0L),
        (add, 0x0123456789abcdefL, 0x100000000L, 0x0123456889abcdefL)
      )
      carry            <- Seq(false, true)
      (register, word) <-
        Vec[(ScalarRegisterIndex, Int)]((4, expected.toInt), (5, (expected >>> 32).toInt), (8, (if (carry) 1 else 0)))
    }
      assertEquals(
        runSequence(
          target,
          Vec(
            Instruction.SMove64(4, InstructionSource.Constant(left)),
            Instruction.SMove64(6, InstructionSource.Constant(right)),
            Instruction.SCmp(
              ScalarCompareOperation.EqualU32,
              InstructionSource.Constant(0L),
              InstructionSource.Constant(if (carry) 0L else 1L)
            ),
            operation,
            Instruction.SCSelect(8, InstructionSource.Constant(1L), InstructionSource.Constant(0L), 32),
            Instruction.VMove(2, InstructionSource.Scalar(register))
          )
        ),
        word,
        clues(operation, left, right, carry, register)
      )
  }

  test("gfx942 packed move selects complete dwords and reads overlapping sources before writing") {
    // llvm-mc gfx942 encodings: gather, in-place swap, scalar pair copy, and inline floats.
    for {
      (encoding, low, high) <- Seq(
        (Vec[Byte](0x02, 0x50, 0xb3.toByte, 0xd3.toByte, 0x04, 0x05, 0x02, 0x18), 0x76543210, 0x89abcdef),
        (Vec[Byte](0x02, 0x48, 0xb3.toByte, 0xd3.toByte, 0x02, 0x05, 0x02, 0x18), 0x89abcdef, 0x11223344),
        (Vec[Byte](0x02, 0x50, 0xb3.toByte, 0xd3.toByte, 0x04, 0x08, 0x00, 0x18), 0x01234567, 0x89abcdef),
        (Vec[Byte](0x02, 0x40, 0xb3.toByte, 0xd3.toByte, 0xf2.toByte, 0xe2.toByte, 0x01, 0x18), 0x3f800000, 0xbf000000)
      )
      (register, expected) <- Vec[(VectorRegisterIndex, Int)]((2, low), (3, high))
    } {
      val target = Target("gfx942")
      val move   = decoded(target, encoding)
      assertEquals(
        runSequence(
          target,
          Vec(
            Instruction.SMove(4, InstructionSource.Constant(0x01234567L)),
            Instruction.SMove(5, InstructionSource.Constant(0x89abcdefL)),
            Instruction.VMove(3, InstructionSource.Constant(0x89abcdefL)),
            Instruction.VMove(4, InstructionSource.Constant(0x76543210L)),
            Instruction.VMove(5, InstructionSource.Constant(0xfedcba98L)),
            move,
            Instruction.VMove(2, InstructionSource.VectorRegister(register))
          )
        ),
        expected,
        clues(encoding, register)
      )
    }
  }

  test("gfx942 shift-add preserves 64-bit inputs, overlapping pairs, and restricted shift counts") {
    val target = Target("gfx942")
    for {
      (left, shift, addend, expected) <- Seq(
        (0x00000001ffffffffL, 1, 0x0000000100000002L, 0x0000000500000000L),
        (-1L, 4, 17L, 1L),
        (0x1234567887654321L, 5, 0x1111111111111111L, 0x2345678998765432L),
        (0x1234567887654321L, 7, 0x1111111111111111L, 0x2345678998765432L),
        (1L, 12, 2L, 18L),
        (0x100000000L, 0, 1L, 0x100000001L)
      )
      (destination, vectorInput) <- Vec[(VectorRegisterIndex, Boolean)]((2, false), (5, true), (9, true))
      (register, word)           <- Vec[(VectorRegisterIndex, Int)](
        (destination, expected.toInt),
        (destination.offsetBy(1), (expected >>> 32).toInt)
      )
    } {
      // LLVM oracle: v_lshl_add_u64 v[2:3], s[4:5], v7, v[8:9].
      // Also overlap either input pair with the destination and use v[4:5].
      val instruction =
        decoded(target, destination.raw, 0x00, 0x08, 0xd2, 0x04, if (vectorInput) 0x0f else 0x0e, 0x22, 0x04)
      assertEquals(
        runSequence(
          target,
          Vec(
            Instruction.SMove(4, InstructionSource.Constant(left & 0xffffffffL)),
            Instruction.SMove(5, InstructionSource.Constant(left >>> 32)),
            Instruction.VMove(4, InstructionSource.Constant(left & 0xffffffffL)),
            Instruction.VMove(5, InstructionSource.Constant(left >>> 32)),
            Instruction.VMove(7, InstructionSource.Constant(shift.toLong)),
            Instruction.VMove(8, InstructionSource.Constant(addend & 0xffffffffL)),
            Instruction.VMove(9, InstructionSource.Constant(addend >>> 32)),
            instruction,
            Instruction.VMove(2, InstructionSource.VectorRegister(register))
          )
        ),
        word,
        clues(left, shift, addend, destination, register)
      )
    }
  }

  test("gfx942 64-bit move reads both source registers before writing an overlapping pair") {
    val target = Target("gfx942")
    // CDNA3 VOP1 encoding: v_mov_b64 v[3:4], v[2:3].
    val move = decoded(target, 0x02, 0x71, 0x06, 0x7e)
    for ((register, expected) <- Vec[(VectorRegisterIndex, Int)]((3, 0x11223344), (4, 0x89abcdef)))
      assertEquals(
        runSequence(
          target,
          Vec(
            Instruction.VMove(3, InstructionSource.Constant(0x89abcdefL)),
            move,
            Instruction.VMove(2, InstructionSource.VectorRegister(register))
          )
        ),
        expected
      )
  }

  private val rdna2 = Target("gfx1030")

  private def move(register: VectorRegisterIndex, value: Long): Instruction =
    Instruction.VMove(register, InstructionSource.Constant(value))

  private def floatBits(value: Float): Int = java.lang.Float.floatToRawIntBits(value)

  test("float-to-integer conversions saturate and convert NaN to zero") {
    // LLVM gfx1030: v_cvt_{u32_f64,i32_f64} v2, v[3:4]; v_cvt_{u32_f32,i16_f16} v2, v3.
    val unsignedFromDouble                               = decoded(rdna2, 0x03, 0x2b, 0x04, 0x7e)
    val signedFromDouble                                 = decoded(rdna2, 0x03, 0x07, 0x04, 0x7e)
    val unsignedFromFloat                                = decoded(rdna2, 0x03, 0x0f, 0x04, 0x7e)
    val shortFromHalf                                    = decoded(rdna2, 0x03, 0xa7, 0x04, 0x7e)
    def double(convert: Instruction, value: Double): Int = {
      val bits = java.lang.Double.doubleToRawLongBits(value)
      runSequence(rdna2, Vec(move(3, bits & 0xffffffffL), move(4, bits >>> 32), convert))
    }
    def single(convert: Instruction, bits: Long): Int = runSequence(rdna2, Vec(move(3, bits), convert))
    assertEquals(double(unsignedFromDouble, 3.75), 3)
    assertEquals(double(unsignedFromDouble, -1.0), 0)
    assertEquals(double(unsignedFromDouble, 5.0e9), -1)
    assertEquals(double(unsignedFromDouble, Double.NaN), 0)
    assertEquals(double(signedFromDouble, -5.0e9), Int.MinValue)
    assertEquals(double(signedFromDouble, Double.NaN), 0)
    assertEquals(single(unsignedFromFloat, floatBits(-3.0f).toLong & 0xffffffffL), 0)
    assertEquals(single(unsignedFromFloat, floatBits(Float.NaN).toLong), 0)
    assertEquals(single(unsignedFromFloat, floatBits(5.75f).toLong), 5)
    assertEquals(single(unsignedFromFloat, floatBits(Float.PositiveInfinity).toLong), -1)
    assertEquals(single(unsignedFromFloat, floatBits(Float.NegativeInfinity).toLong & 0xffffffffL), 0)
    assertEquals(single(unsignedFromFloat, floatBits(4294967296.0f).toLong), -1)
    // GFX10 keeps the high half of a 16-bit result's destination.
    assertEquals(single(shortFromHalf, 0x7bffL), 0x11227fff)
    assertEquals(single(shortFromHalf, 0xfbffL), 0x11228000)
    assertEquals(single(shortFromHalf, 0x7e00L), 0x11220000)
  }

  test("gfx900 multiply-high selects signedness and subtraction consumes only its own borrow") {
    val target = Target("gfx900")
    // LLVM gfx900: s_mul_hi_{u32,i32} s5, s1, s2; v_mul_hi_{u32,i32} v2, v3, v4.
    val scalarUnsigned = decoded(target, 0x01, 0x02, 0x05, 0x96)
    val scalarSigned   = decoded(target, 0x01, 0x02, 0x85, 0x96)
    val vectorUnsigned = decoded(target, 0x02, 0x00, 0x86, 0xd2, 0x03, 0x09, 0x02, 0x00)
    val vectorSigned   = decoded(target, 0x02, 0x00, 0x87, 0xd2, 0x03, 0x09, 0x02, 0x00)
    for {
      (left, right, unsigned, signed) <- Vec(
        (0xffffffffL, 0xffffffffL, 0xfffffffe, 0),
        (0x80000000L, 1L, 0, -1),
        (0x7fffffffL, 0x7fffffffL, 0x3fffffff, 0x3fffffff),
        (0x80000000L, 0x80000000L, 0x40000000, 0x40000000)
      )
      (scalar, vector, expected) <-
        Vec((scalarUnsigned, vectorUnsigned, unsigned), (scalarSigned, vectorSigned, signed))
    } {
      val scalarInputs = Vec(
        Instruction.SMove(1, InstructionSource.Constant(left)),
        Instruction.SMove(2, InstructionSource.Constant(right))
      )
      assertEquals(
        runSequence(target, scalarInputs ++ Vec(scalar, Instruction.VMove(2, InstructionSource.Scalar(5)))),
        expected,
        clues(scalar, left, right)
      )
      assertEquals(
        runSequence(target, Vec(move(3, left), move(4, right), vector)),
        expected,
        clues(vector, left, right)
      )
    }
    // LLVM gfx900: v_sub_co_u32_e32 v2, vcc, v3, v4; v_subb_co_u32_e32 v2, vcc, v3, v4, vcc.
    val subtract           = decoded(target, 0x03, 0x09, 0x04, 0x34)
    val subtractWithBorrow = decoded(target, 0x03, 0x09, 0x04, 0x3a)
    for {
      (operation, left, right, borrowIn, difference, borrowOut) <- Vec(
        (subtract, 9L, 4L, true, 5, 0),
        (subtract, 0L, 1L, false, -1, 1),
        (subtractWithBorrow, 9L, 4L, true, 4, 0),
        (subtractWithBorrow, 9L, 4L, false, 5, 0),
        (subtractWithBorrow, 0L, 0xffffffffL, false, 1, 1),
        (subtractWithBorrow, 0L, 0xffffffffL, true, 0, 1)
      )
      (read, expected) <-
        Vec(Vec.empty[Instruction] -> difference, Vec(Instruction.VMove(2, InstructionSource.Scalar(106))) -> borrowOut)
    }
      assertEquals(
        runSequence(
          target,
          Vec(
            Instruction.SMove64(106, InstructionSource.Constant(if (borrowIn) 1L else 0L)),
            move(3, left),
            move(4, right),
            operation
          ) ++ read
        ),
        expected,
        clues(operation, left, right, borrowIn)
      )
  }

  test("binary16 fused multiply-add rounds once") {
    // a * b + c is just above the binary16 halfway point, but a binary32 intermediate rounds it onto that point
    val (a, b, c)        = (0x3c01L, 0x3bffL, 0x0009L)
    def both(half: Long) = half | (half << 16)
    val sources          = Vec(move(3, a), move(4, b), move(5, c))
    // LLVM gfx1030: v_fma_f16, v_fmac_f16_e32, v_pk_fma_f16 and v_fma_mixlo_f16 op_sel_hi:[1,1,1] on v2, v3, v4(, v5).
    val fma    = decoded(rdna2, 0x02, 0x00, 0x4b, 0xd7, 0x03, 0x09, 0x16, 0x04)
    val fmac   = decoded(rdna2, 0x03, 0x09, 0x04, 0x6c)
    val packed = decoded(rdna2, 0x02, 0x40, 0x0e, 0xcc, 0x03, 0x09, 0x16, 0x1c)
    val mixed  = decoded(rdna2, 0x02, 0x40, 0x21, 0xcc, 0x03, 0x09, 0x16, 0x1c)
    assertEquals(runSequence(rdna2, sources :+ fma), 0x3c01)
    assertEquals(runSequence(rdna2, sources :+ fmac, initial = c.toInt), 0x3c01)
    assertEquals(runSequence(rdna2, Vec(move(3, both(a)), move(4, both(b)), move(5, both(c)), packed)), 0x3c013c01)
    assertEquals(runSequence(rdna2, sources :+ mixed), 0x11223c01)
  }

  test("division fused multiply-add rounds the product and addend once") {
    val value    = 1.0000001192092896f
    val previous = -1.000000238418579f
    // LLVM gfx1030: v_div_fmas_f32 v2, v3, v4, v5.
    val fmas = decoded(rdna2, 0x02, 0x00, 0x6f, 0xd5, 0x03, 0x09, 0x16, 0x04)
    assertEquals(
      runSequence(
        rdna2,
        Vec(
          move(3, floatBits(value).toLong),
          move(4, floatBits(value).toLong),
          move(5, floatBits(previous) & 0xffffffffL),
          fmas
        )
      ),
      floatBits(Math.fma(value, value, previous))
    )
  }

  test("binary16 and binary32 minimum and maximum order values and ignore a single NaN operand") {
    val (nan, one) = (0x7e00L, 0x3c00L)
    // LLVM gfx1030: v_min_f16_e32 and v_max_f16_e32 v2, v3, v4; v_pk_min_f16 v2, v3, v4; v_min3_f32 v2, v3, v4, v5.
    val minimum       = decoded(rdna2, 0x03, 0x09, 0x04, 0x74)
    val maximum       = decoded(rdna2, 0x03, 0x09, 0x04, 0x72)
    val packedMinimum = decoded(rdna2, 0x02, 0x40, 0x11, 0xcc, 0x03, 0x09, 0x02, 0x18)
    val minimum3      = decoded(rdna2, 0x02, 0x00, 0x51, 0xd5, 0x03, 0x09, 0x16, 0x04)
    // LLVM gfx900: v_min_f16_sdwa v2, v3, v4 dst_sel:DWORD dst_unused:UNUSED_PRESERVE src0_sel:WORD_0 src1_sel:WORD_0.
    val sdwaMinimum              = decoded(Target("gfx900"), 0xf9, 0x08, 0x04, 0x5c, 0x03, 0x16, 0x04, 0x04)
    val (onePointFive, minusTwo) = (0x3e00L, 0xc000L)
    for (
      (target, instruction, expected) <- Vec(
        (rdna2, minimum, 0x11220000L | minusTwo),
        (rdna2, maximum, 0x11220000L | onePointFive),
        (Target("gfx900"), sdwaMinimum, minusTwo)
      )
    )
      assertEquals(
        runSequence(target, Vec(move(3, onePointFive), move(4, minusTwo), instruction)),
        expected.toInt,
        clues(instruction)
      )
    for {
      instruction   <- Vec(minimum, maximum)
      (left, right) <- Vec((nan, one), (one, nan))
    }
      assertEquals(runSequence(rdna2, Vec(move(3, left), move(4, right), instruction)), 0x11223c00, clues(instruction))
    assertEquals(
      runSequence(rdna2, Vec(move(3, nan | (one << 16)), move(4, one | (nan << 16)), packedMinimum)),
      0x3c003c00
    )
    assertEquals(
      runSequence(
        rdna2,
        Vec(move(3, floatBits(2.0f).toLong), move(4, 0x7fc00000L), move(5, floatBits(1.0f).toLong), minimum3)
      ),
      floatBits(1.0f)
    )
  }

  test("DS increment and decrement wrap at the operand and DS float minimum ignores NaN") {
    // LLVM gfx1030: ds_inc_rtn_u32, ds_dec_rtn_u32 and ds_min_rtn_f32 v6, v3, v5.
    val increment = decoded(rdna2, 0x00, 0x00, 0x8c, 0xd8, 0x03, 0x05, 0x00, 0x06)
    val decrement = decoded(rdna2, 0x00, 0x00, 0x90, 0xd8, 0x03, 0x05, 0x00, 0x06)
    val minimum   = decoded(rdna2, 0x00, 0x00, 0xc8, 0xd8, 0x03, 0x05, 0x00, 0x06)
    def atomic(instruction: Instruction, old: Long, operand: Long): Int =
      runSequence(
        rdna2,
        Vec(
          move(3, 0L),
          move(4, old),
          Instruction.Store(4, MemoryAddress.Local(3, 0), 4, false),
          move(5, operand),
          instruction,
          Instruction.Load(2, MemoryAddress.Local(3, 0), 4, false, LoadPlacement.Full)
        ),
        groupSegmentSize = 4
      )
    for ((old, limit, expected) <- Vec((9L, 10L, 10), (10L, 10L, 0), (11L, 10L, 0)))
      assertEquals(atomic(increment, old, limit), expected, clues(old, limit))
    for ((old, limit, expected) <- Vec((5L, 7L, 4), (0L, 7L, 7), (8L, 7L, 7)))
      assertEquals(atomic(decrement, old, limit), expected, clues(old, limit))
    assertEquals(atomic(minimum, 0x7fc00000L, floatBits(1.0f).toLong), floatBits(1.0f))
  }

  test("argument pointer relocations patch packed kernel arguments") {
    val memory     = RegionMemory()
    val allocation = AllocationId(1L)
    memory.allocate(allocation, 16)
    val operations = Vec(
      Instruction.SLoad(dest = 4, count = 2, base = 0, offset = 8, scalarOffset = None),
      move(0, 0L),
      move(2, 0x5a5a5a5aL),
      Instruction.Store(2, MemoryAddress.Global(0, Some(4), 0), 4, false),
      Instruction.End
    )
    val task = KernelTask.validated(
      "kernel",
      Dimensions.One,
      Dimensions.One,
      Vec(KernelTask.Argument.Scalar(Vec.fill[Byte](16)(0).unsafe)),
      pointerRelocations =
        Vec(KernelTask.PointerRelocation(KernelTask.RelocationSource.Argument(0), 8L, allocation, 4L))
    ).value
    assertEquals(runIR(operations, memory, codeKernel(kernargSize = 16, kernelCodeProperties = 0x8), task), Right(()))
    assertEquals(memory.getInt(allocation, 4), 0x5a5a5a5a)
  }

  // Decoder field tests live in the modifier/memory suites. This fixture exercises
  // lowered instructions through register state and observable memory writes.
  private def run(target: Target, instruction: Instruction, initial: Int = 0x11223344): Int =
    runSequence(target, Vec(instruction), initial)

  private def runSequence(
      target: Target,
      operations: Vec[Instruction],
      initial: Int = 0x11223344,
      wave32: Boolean = true,
      groupSegmentSize: Int >= 0 = 0,
      computePgmRsrc1: Int = CodeObjectKernel.DefaultComputePgmRsrc1
  ): Int = {
    val memory     = RegionMemory()
    val allocation = AllocationId(1L)
    memory.allocate(allocation, 16)
    memory.putInt(allocation, 8, 0x89ab8080)
    val address      = memory.address(allocation, 0L).value.raw
    val instructions = Vec(
      Instruction.VMove(0, InstructionSource.Constant(address & 0xffffffffL)),
      Instruction.VMove(1, InstructionSource.Constant(address >>> 32)),
      Instruction.VMove(2, InstructionSource.Constant(initial.toLong & 0xffffffffL))
    ) ++ operations ++ Vec(
      Instruction.Store(2, MemoryAddress.Global(0, None, 0), 4, false),
      Instruction.End
    )
    val source = codeKernel(
      target = target,
      groupSegmentSize = groupSegmentSize,
      computePgmRsrc1 = computePgmRsrc1,
      kernelCodeProperties = if (wave32 && target.isa.isRDNA) KernelCodeProperty.ENABLE_WAVEFRONT_SIZE32 else 0
    )
    assertEquals(runIR(instructions, memory, source), Right(()))
    assertEquals(memory.getInt(allocation, 8), 0x89ab8080)
    memory.getInt(allocation, 0)
  }

  test("memory transfers can end at the final vector register") {
    val target = Target("gfx900")
    for (width <- Seq[Int in (1, 2, 4)](1, 2, 4)) {
      val load =
        Instruction.Load(255, MemoryAddress.Global(0, None, 8), width.widen[Int in 1 ~ 16], false, LoadPlacement.Full)
      val expected = width.raw match {
        case 1 => 0x80
        case 2 => 0x8080
        case _ => 0x89ab8080
      }
      assertEquals(
        runSequence(
          target,
          Vec(
            load,
            Instruction.VMove(2, InstructionSource.VectorRegister(255))
          )
        ),
        expected
      )
    }
  }

  test("wave32 mask writes decode and execute at the last scalar slot") {
    val target = Target("gfx1030")
    for (text <- Seq("v_add_co_u32 v2, exec_hi, v3, v4", "v_cmp_eq_u32 exec_hi, v3, v4")) {
      val bytes = asm.Assembler.encode(asm.Parser.parse(text).value, target).value
      assert(VMCompiler.decodeInstruction(bytes, target).isRight, text)
    }
    val operation = Instruction.VAddCarry(2, 127, InstructionSource.Constant(1), InstructionSource.Constant(1), None)
    assertEquals(run(target, operation), 2)
  }

  test("64-bit vector operations read live EXEC and VCC values") {
    val target = Target("gfx900")
    for (scalar <- Seq[ScalarRegisterIndex](106, 126)) {
      val initialise = Instruction.SMove64(scalar, InstructionSource.Constant(1))
      for (
        operation <- Seq(
          Instruction.VShiftRight64(2, InstructionSource.Constant(0), InstructionSource.Scalar(scalar)),
          Instruction.VShiftLeft64(2, InstructionSource.Constant(0), InstructionSource.Scalar(scalar)),
          Instruction.VShiftRightArithmetic64(2, InstructionSource.Constant(0), InstructionSource.Scalar(scalar)),
          Instruction.VBinary64(
            VectorBinary64Operation.Add,
            2,
            InstructionSource.Scalar(scalar),
            InstructionSource.Constant(0),
            SourceModifiers.Unmodified
          )
        )
      ) assertEquals(runSequence(target, Vec(initialise, operation)), 1)
      val fullMask = Instruction.SMove64(scalar, InstructionSource.Constant(0x1234567800000001L))
      assertEquals(
        runSequence(
          target,
          Vec(
            fullMask,
            Instruction.VShiftRight64(2, InstructionSource.Constant(32), InstructionSource.Scalar(scalar))
          )
        ),
        0x12345678
      )
      val highRead =
        Instruction.VShiftRight64(2, InstructionSource.Constant(0), InstructionSource.Scalar(scalar.offsetBy(1)))
      assertEquals(runSequence(target, Vec(fullMask, highRead)), 0x12345678)
    }
  }

  test("scalar 64-bit moves read EXEC and VCC special sources") {
    val target = Target("gfx900")
    for (register <- Seq[ScalarRegisterIndex](106, 107, 126, 127)) {
      val move     = Instruction.SMove64(4, InstructionSource.Scalar(register))
      val expected = if (register.raw == 126) 1 else 0
      assertEquals(runSequence(target, Vec(move, Instruction.VMove(2, InstructionSource.Scalar(4)))), expected)
    }
    assertEquals(run(target, Instruction.SMove64(126, InstructionSource.Constant(1))), 0x11223344)
  }

  test("ldexp executes with a signed scalar exponent in v255") {
    for (exponent <- Seq(3, -2)) {
      val operations = Vec(
        Instruction.VMove(4, InstructionSource.Constant(0L)),
        Instruction.VMove(5, InstructionSource.Constant(0x3ff80000L)), // 1.5
        Instruction.VMove(255, InstructionSource.Constant(exponent.toLong)),
        Instruction.VBinary64(
          VectorBinary64Operation.LoadExponent,
          2,
          InstructionSource.VectorRegister(4),
          InstructionSource.VectorRegister(255),
          SourceModifiers.Unmodified
        ),
        Instruction.VMove(2, InstructionSource.VectorRegister(3))
      )
      val expected = (java.lang.Double.doubleToRawLongBits(Math.scalb(1.5, exponent)) >>> 32).toInt
      assertEquals(runSequence(Target("gfx900"), operations), expected)
    }
  }

  private val destinations = Seq(
    (SubDwordSelection.Byte0, 0x00000080L, 0xffffff80L, 0x11223380L),
    (SubDwordSelection.Byte1, 0x00008000L, 0xffff8000L, 0x11228044L),
    (SubDwordSelection.Byte2, 0x00800000L, 0xff800000L, 0x11803344L),
    (SubDwordSelection.Byte3, 0x80000000L, 0x80000000L, 0x80223344L),
    (SubDwordSelection.Word0, 0x00008080L, 0xffff8080L, 0x11228080L),
    (SubDwordSelection.Word1, 0x80800000L, 0x80800000L, 0x80803344L),
    (SubDwordSelection.DWord, 0xffff8080L, 0xffff8080L, 0xffff8080L)
  )

  test("SDWA subtraction writes VCC only in the carry forms") {
    // LLVM: gfx900 v_subrev_u32_sdwa v2, v3, v4, v_subrev_co_u32_sdwa v2, vcc, v3, v4, and
    // gfx803 v_subrev_u32_sdwa v2, vcc, v3, v4, each with src0_sel:BYTE_0.
    for (
      (target, bytes, written) <- Seq(
        (Target("gfx900"), Vec(0xf9, 0x08, 0x04, 0x6c, 0x03, 0x16, 0x00, 0x06), false),
        (Target("gfx900"), Vec(0xf9, 0x08, 0x04, 0x36, 0x03, 0x16, 0x00, 0x06), true),
        (Target("gfx803"), Vec(0xf9, 0x08, 0x04, 0x36, 0x03, 0x16, 0x00, 0x06), true)
      )
    ) {
      val vcc = runSequence(
        target,
        Vec(
          Instruction.VMove(3, InstructionSource.Constant(2L)),
          Instruction.VMove(4, InstructionSource.Constant(1L)),
          Instruction.SMove(Fixed.VccLo, InstructionSource.Constant(0L)),
          decoded(target, bytes*),
          Instruction.VMove(2, InstructionSource.Scalar(Fixed.VccLo))
        )
      )
      assertEquals(vcc & 1, if (written) 1 else 0, clues(target, bytes))
    }
  }

  for (target <- Seq(Target("gfx900"), Target("gfx1030"))) {
    test(s"$target SDWA destination placement pads, sign-extends, or preserves each selection") {
      for {
        (selection, pad, signed, preserved) <- destinations
        (unused, expected)                  <- Seq(
          SubDwordDestinationUnused.Pad        -> pad,
          SubDwordDestinationUnused.SignExtend -> signed,
          SubDwordDestinationUnused.Preserve   -> preserved
        )
      } {
        val instruction = Instruction.VBinarySubDword(
          VectorSubDwordOperation.Or32,
          2,
          InstructionSource.Constant(0xffff8080L),
          InstructionSource.Constant(0L),
          selection,
          unused,
          SubDwordSelection.DWord,
          SubDwordSelection.DWord,
          false,
          false
        )
        assertEquals(run(target, instruction), expected.toInt, clues(selection, unused))
      }
    }

    test(s"$target SDWA source selection and sign extension feed the operation") {
      val instruction = Instruction.VBinarySubDword(
        VectorSubDwordOperation.AddU32,
        2,
        InstructionSource.Constant(0x00800000L),
        InstructionSource.Constant(0x00000300L),
        SubDwordSelection.DWord,
        SubDwordDestinationUnused.Pad,
        SubDwordSelection.Byte2,
        SubDwordSelection.Byte1,
        true,
        false
      )
      assertEquals(run(target, instruction), -125)
    }

    test(s"$target SDWA carries and borrows on the sign-extended dword") {
      def carryOut(operation: VectorSubDwordOperation, a: Long, b: Long): (Int, Int) = {
        val instruction = Instruction.VBinarySubDword(
          operation,
          2,
          InstructionSource.Constant(a),
          InstructionSource.Constant(b),
          SubDwordSelection.DWord,
          SubDwordDestinationUnused.Pad,
          SubDwordSelection.Byte0,
          SubDwordSelection.DWord,
          true,
          false
        )
        (
          run(target, instruction),
          runSequence(target, Vec(instruction, Instruction.VMove(2, InstructionSource.Scalar(Fixed.VccLo)))) & 1
        )
      }
      assertEquals(carryOut(VectorSubDwordOperation.AddCarryU32, 0xffL, 1L), (0, 1))
      assertEquals(carryOut(VectorSubDwordOperation.AddCarryU32, 0x7fL, 1L), (0x80, 0))
      assertEquals(carryOut(VectorSubDwordOperation.SubtractCarryU32, 0xffL, 1L), (-2, 0))
      assertEquals(carryOut(VectorSubDwordOperation.SubtractReverseCarryU32, 0xffL, 1L), (2, 1))
    }

    test(s"$target narrow loads sign-extend and D16 loads preserve the other half") {
      assertEquals(
        run(target, Instruction.Load(2, MemoryAddress.Global(0, None, 8), 1, true, LoadPlacement.Full)),
        -128
      )
      assertEquals(
        run(target, Instruction.Load(2, MemoryAddress.Global(0, None, 8), 2, true, LoadPlacement.Full)),
        -32640
      )
      assertEquals(
        run(target, Instruction.Load(2, MemoryAddress.Global(0, None, 8), 2, false, LoadPlacement.Low)),
        0x11228080
      )
      assertEquals(
        run(target, Instruction.Load(2, MemoryAddress.Global(0, None, 8), 2, false, LoadPlacement.High)),
        0x80803344
      )
    }

    test(s"$target packed arithmetic retains signedness, lane overflow, and source selection") {
      val cases = Seq(
        (VectorPackedBinaryOperation.MaximumI16, 0x80007fffL, 0x7fff8000L, true, true, 0x7fff7fffL),
        (VectorPackedBinaryOperation.MaximumU16, 0x80007fffL, 0x7fff8000L, true, true, 0x80008000L),
        (VectorPackedBinaryOperation.AddU16, 0xffff0001L, 0x00010002L, true, true, 0x00000003L),
        (VectorPackedBinaryOperation.MultiplyLowU16, 0x00020003L, 0x00050007L, false, true, 0x000f0015L),
        (VectorPackedBinaryOperation.MultiplyLowU16, 0x00020003L, 0x00050007L, true, false, 0x000e0015L)
      )
      cases.foreach { (operation, left, right, leftHigh, rightHigh, expected) =>
        assertEquals(
          run(
            target,
            Instruction.VPackedBinary(
              operation,
              2,
              InstructionSource.Constant(left),
              InstructionSource.Constant(right),
              leftHigh,
              rightHigh
            )
          ),
          expected.toInt,
          clues(operation, leftHigh, rightHigh)
        )
      }
    }
  }

  test("gfx900 SDWA comparisons apply absolute and negation modifiers to both sources") {
    val target = Target("gfx900")
    // LLVM gfx900: v_cmp_lt_f16_sdwa vcc, v3, -v4 / v3, |v4| / -v3, v4 (WORD_0), v_cmp_lt_f32_sdwa vcc, |v3|, v4.
    val cases = Vec(
      (Vec(0xf9, 0x08, 0x42, 0x7c, 0x03, 0x00, 0x04, 0x14), 0x3c00L, 0x4000L, 0),
      (Vec(0xf9, 0x08, 0x42, 0x7c, 0x03, 0x00, 0x04, 0x24), 0x3c00L, 0xc000L, 1),
      (Vec(0xf9, 0x08, 0x42, 0x7c, 0x03, 0x00, 0x14, 0x04), 0x3c00L, 0x3800L, 1),
      (
        Vec(0xf9, 0x08, 0x82, 0x7c, 0x03, 0x00, 0x26, 0x06),
        floatBits(-2f).toLong & 0xffffffffL,
        floatBits(1f).toLong,
        0
      )
    )
    for ((bytes, a, b, expected) <- cases) {
      val operations = Vec(
        move(3, a),
        move(4, b),
        decoded(target, bytes.map(_.toByte)),
        Instruction.VMove(2, InstructionSource.Scalar(Fixed.VccLo))
      )
      assertEquals(runSequence(target, operations) & 1, expected, bytes)
    }
  }

  test("unsigned byte dot product uses all four lanes and wraps the accumulator") {
    for (
      (left, right, accumulator, expected) <- Seq(
        (0x04030201L, 0x08070605L, 11L, 81L),
        (0xffffffffL, 0xffffffffL, 0xfffffff0L, 260084L),
        (0x80000000L, 0x02000000L, 9L, 265L)
      )
    ) assertEquals(
      run(
        Target("gfx1030"),
        Instruction.VDot4U32U8(
          2,
          InstructionSource.Constant(left),
          InstructionSource.Constant(right),
          InstructionSource.Constant(accumulator)
        )
      ),
      expected.toInt
    )
  }

  test(
    "mixed FMA selects half inputs independently, applies absolute before negate, and preserves the destination half"
  ) {
    // A.high = -2, B is f32 3, C.low = -0.5. abs(A), -abs(C) => 2*3-0.5 = 5.5.
    val a = InstructionSource.Constant(0xc0003c00L)
    val b = InstructionSource.Constant(0x40400000L)
    val c = InstructionSource.Constant(0x4400b800L)
    assertEquals(run(Target("gfx900"), Instruction.VFmaMixF32(2, a, b, c, 1, 5, 4, 5, false)), 0x40b00000)
    assertEquals(run(Target("gfx900"), Instruction.VFmaMixF32(2, a, b, c, 1, 5, 4, 5, true)), 0x3f800000)
    assertEquals(run(Target("gfx900"), Instruction.VFmaMixF16(2, a, b, c, 1, 5, 4, 5, false, false)), 0x11224580)
    assertEquals(run(Target("gfx900"), Instruction.VFmaMixF16(2, a, b, c, 1, 5, 4, 5, false, true)), 0x45803344)
    assertEquals(run(Target("gfx900"), Instruction.VFmaMixF16(2, a, b, c, 1, 5, 4, 5, true, true)), 0x3c003344)
  }

  for (
    (kind, values) <- Seq(
      VectorComparisonKind.ClassFloat32 -> Seq(0x7f800001L, 0x7fc00001L, 0xff800000L, 0xbf800000L, 0x80000001L,
        0x80000000L, 0L, 1L, 0x3f800000L, 0x7f800000L),
      VectorComparisonKind.ClassFloat64 -> Seq(
        0x7ff0000000000001L, 0x7ff8000000000001L, 0xfff0000000000000L,
        0xbff0000000000000L, 0x8000000000000001L, 0x8000000000000000L, 0L, 1L, 0x3ff0000000000000L, 0x7ff0000000000000L
      )
    )
  ) test(s"$kind distinguishes all ten floating classes and tests membership in the supplied mask") {
    values.zipWithIndex.foreach { (bits, classBit) =>
      for (mask <- Seq(1L << classBit, 1023L ^ (1L << classBit))) {
        val instructions = Vec(
          Instruction.SMove(12, InstructionSource.Constant(bits & 0xffffffffL)),
          Instruction.SMove(13, InstructionSource.Constant(bits >>> 32)),
          Instruction.VCompare(
            VectorComparison(kind, VectorComparisonRelation.Class, updateExec = false),
            10,
            InstructionSource.Scalar(12),
            InstructionSource.Constant(mask)
          ),
          Instruction.VMove(2, InstructionSource.Scalar(10))
        )
        assertEquals(
          runSequence(Target("gfx900"), instructions),
          if (mask == (1L << classBit)) 1 else 0,
          clues(bits, mask)
        )
      }
    }
  }

}
