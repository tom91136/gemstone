package net.kurobako.gemstone.isa.amdgpu

import VMIR.*
import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.vm.*

class AMDGPUDecodeSuite extends munit.FunSuite {
  // Machine bytes are fixed ISA fixtures: deriving them from our encoder would hide decoder errors.

  test("scalar ALU operations decode their operation and operands") {
    Vec(
      (Target("gfx900"), Vec(0x05, 0x02, 0x84, 0xbe)) ->
        Instruction.SCSelect(4, InstructionSource.Scalar(5), InstructionSource.Scalar(4), 32),
      (Target("gfx900"), Vec(0x01, 0x0c, 0x84, 0xbe)) ->
        Instruction.SUnary(ScalarUnaryOperation.CountBits32, 4, InstructionSource.Scalar(1)),
      (Target("gfx900"), Vec(0x1e, 0x1d, 0x80, 0xbe))  -> Instruction.SSetPc(InstructionSource.Scalar(30)),
      (Target("gfx1036"), Vec(0x05, 0x06, 0x04, 0x8b)) ->
        Instruction.SBinary(ScalarBinaryOperation.OrNot32, 4, InstructionSource.Scalar(5), InstructionSource.Scalar(6)),
      (Target("gfx1036"), Vec(0x05, 0x06, 0x04, 0x8d)) ->
        Instruction.SBinary(ScalarBinaryOperation.Nor32, 4, InstructionSource.Scalar(5), InstructionSource.Scalar(6)),
      (Target("gfx1036"), Vec(0x04, 0x06, 0x02, 0x95)) -> Instruction.SBinary64(
        ScalarBinary64Operation.BitFieldExtractI64,
        2,
        InstructionSource.Scalar(4),
        InstructionSource.Scalar(6)
      )
    ).foreach { case ((target, bytes), expected) => assertEquals(decoded(target, bytes*), expected) }
  }

  test("scheduling hints, explicit waits and code-end padding decode as waits") {
    // s_waitcnt_lgkmcnt null, 0 (Tensile gfx1030 GEMM), s_code_end, s_setprio.
    Vec(
      Target("gfx1030") -> Vec(0x00, 0x00, 0x7d, 0xbd),
      Target("gfx1036") -> Vec(0x00, 0x00, 0x9f, 0xbf),
      Target("gfx900")  -> Vec(0x41, 0x31, 0x8f, 0xbf)
    ).foreach { case (target, bytes) => assertEquals(decoded(target, bytes*), Instruction.Wait) }
  }

  test("gfx1036 global atomics and stores retain their operand, width and return bit") {
    val target = Target("gfx1036")
    assertEquals(
      decoded(target, 0x00, 0x80, 0xf0, 0xdc, 0x01, 0x02, 0x7d, 0x00),
      Instruction.Atomic(AtomicOperation.IncrementWrap, 0, MemoryAddress.Global(1, None, 0), 2, 4, false)
    )
    assertEquals(
      decoded(target, 0x00, 0x80, 0xf1, 0xdc, 0x02, 0x03, 0x7d, 0x01),
      Instruction.Atomic(AtomicOperation.IncrementWrap, 1, MemoryAddress.Global(2, None, 0), 3, 4, true)
    )
    assertEquals(
      decoded(target, 0x00, 0x80, 0x78, 0xdc, 0x17, 0x0d, 0x7d, 0x00),
      Instruction.Store(13, MemoryAddress.Global(23, None, 0), 16, false)
    )
    assertEquals(
      decoded(target, 0x00, 0x80, 0x70, 0xdc, 0x40, 0x09, 0x7d, 0x00),
      Instruction.Store(9, MemoryAddress.Global(64, None, 0), 4, false)
    )
  }

  test("gfx1036 SMEM immediate offsets use the second-dword offset field") {
    val target = Target("gfx1036")
    assertEquals(
      decoded(target, 0x02, 0x02, 0x08, 0xf4, 0x10, 0x00, 0x00, 0xfa),
      Instruction.SLoad(dest = 8, count = 4, base = 4, offset = 16, scalarOffset = None)
    )
    assertEquals(
      decoded(target, 0x02, 0x00, 0x04, 0xf4, 0x00, 0x00, 0x00, 0xfa),
      Instruction.SLoad(dest = 0, count = 2, base = 4, offset = 0, scalarOffset = None)
    )
  }

  test("SMEM immediate offsets are unsigned 20-bit on GFX8 and signed from GFX9") {
    // LLVM: s_load_dword/s_load_b32 s22, s[12:13], -0x4 and 0xfffff; gfx803 ignores OFFSET bit 20.
    val cases = Seq(
      (Target("gfx803"), Vec(0x86, 0x05, 0x02, 0xc0, 0xff, 0xff, 0x0f, 0x00), 0xfffffL),
      (Target("gfx803"), Vec(0x86, 0x05, 0x02, 0xc0, 0xfc, 0xff, 0x1f, 0x00), 0xffffcL),
      (Target("gfx900"), Vec(0x86, 0x05, 0x02, 0xc0, 0xfc, 0xff, 0x1f, 0x00), -4L),
      (Target("gfx900"), Vec(0x86, 0x05, 0x02, 0xc0, 0xff, 0xff, 0x0f, 0x00), 0xfffffL),
      (Target("gfx942"), Vec(0x86, 0x05, 0x02, 0xc0, 0xfc, 0xff, 0x1f, 0x00), -4L),
      (Target("gfx1030"), Vec(0x86, 0x05, 0x00, 0xf4, 0xfc, 0xff, 0x1f, 0xfa), -4L),
      (Target("gfx1100"), Vec(0x86, 0x05, 0x00, 0xf4, 0xfc, 0xff, 0x1f, 0xf8), -4L),
      (Target("gfx1200"), Vec(0x86, 0x05, 0x00, 0xf4, 0xfc, 0xff, 0xff, 0xf8), -4L),
      (Target("gfx1200"), Vec(0x86, 0x05, 0x00, 0xf4, 0x00, 0x00, 0x10, 0xf8), 0x100000L)
    )
    for ((target, bytes, offset) <- cases)
      decoded(target, bytes.map(_.toByte)) match {
        case Instruction.SLoad(_, _, _, decodedOffset, None, _) => assertEquals(decodedOffset, offset, target)
        case other                                              => fail(s"$target: unexpected instruction $other")
      }
  }

  test("VOP3-encoded comparisons decode their scalar mask destination") {
    // gfx900 CMPX reuses the VOPC opcode; gfx1036 lg-f32 comes from miniBUDE.
    assertEquals(
      decoded(Target("gfx900"), 0x36, 0x00, 0xda, 0xd0, 0x0c, 0x17, 0x02, 0x00),
      Instruction.VCompare(
        VectorComparison(VectorComparisonKind.Unsigned32, VectorComparisonRelation.Equal, true),
        54,
        InstructionSource.VectorRegister(12),
        InstructionSource.VectorRegister(11)
      )
    )
    assertEquals(
      decoded(Target("gfx1036"), 0x08, 0x00, 0x05, 0xd4, 0x80, 0x38, 0x02, 0x00),
      Instruction.VCompare(
        VectorComparison(VectorComparisonKind.Float32, VectorComparisonRelation.OrderedNotEqual, false),
        8,
        InstructionSource.Constant(0),
        InstructionSource.VectorRegister(28)
      )
    )
  }

  test("gfx900 SDWA scalar selectors decode inline constants and scalar registers") {
    val target = Target("gfx900")
    assertEquals(
      decoded(target, 0xf9, 0x04, 0x0a, 0x4c, 0x81, 0x16, 0x86, 0x06),
      Instruction.VBinarySubDword(
        VectorSubDwordOperation.AddU16,
        5,
        InstructionSource.Constant(1),
        InstructionSource.VectorRegister(2),
        SubDwordSelection.DWord,
        SubDwordDestinationUnused.Preserve,
        SubDwordSelection.DWord,
        SubDwordSelection.DWord,
        false,
        false
      )
    )
    assertEquals(
      decoded(target, 0xf9, 0x06, 0x0a, 0x4c, 0x01, 0x16, 0x06, 0x86),
      Instruction.VBinarySubDword(
        VectorSubDwordOperation.AddU16,
        5,
        InstructionSource.VectorRegister(1),
        InstructionSource.Scalar(3),
        SubDwordSelection.DWord,
        SubDwordDestinationUnused.Preserve,
        SubDwordSelection.DWord,
        SubDwordSelection.DWord,
        false,
        false
      )
    )
    assertEquals(
      decoded(target, 0xf9, 0x04, 0x94, 0x7d, 0xc1, 0x86, 0x86, 0x06),
      Instruction.VCompare(
        VectorComparison(VectorComparisonKind.Unsigned32, VectorComparisonRelation.Equal, false),
        6,
        InstructionSource.Constant(-1),
        InstructionSource.VectorRegister(2),
        selection = Some(SubDwordSources(SubDwordSelection.DWord, SubDwordSelection.DWord, false, false))
      )
    )
  }

  test("gfx900 scalar destinations beyond the scalar bank fail decoding") {
    val target = Target("gfx900")
    assertEquals(
      decoded(target, 0x01, 0x05, 0x0a, 0x7e),
      Instruction.VReadFirstLane(5, InstructionSource.VectorRegister(1))
    )
    // VDST 0x85 is not an SGPR.
    assert(VMCompiler.decodeInstruction(Vec(0x01, 0x05, 0x0a, 0x7f).map(_.toByte), target).isLeft)
  }

  test("register bounds use the kernel wave size rather than the target default") {
    // v_cmp_eq_u32_e64 s127, v0, v1; s_endpgm: a wave64 mask needs s[127:128].
    val code   = Vec(0x7f, 0x00, 0x4a, 0xd4, 0x00, 0x03, 0x02, 0x02, 0x00, 0x00, 0xb0, 0xbf).map(_.toByte)
    val wave32 = codeKernel(
      target = Target("gfx1100"),
      kernelCodeProperties = EncodingConstants.KernelCodeProperty.ENABLE_WAVEFRONT_SIZE32,
      code = code
    )
    assert(VMCompiler().compile(wave32).isRight)
    assert(VMCompiler().compile(wave32.copy(kernelCodeProperties = 0)).isLeft)
  }
}
