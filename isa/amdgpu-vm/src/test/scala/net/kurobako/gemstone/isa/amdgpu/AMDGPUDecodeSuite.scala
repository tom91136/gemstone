package net.kurobako.gemstone.isa.amdgpu

import VMIR.*
import fixtures.*
import laneRuns.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.testing.Suite
import net.kurobako.gemstone.vm.*

class AMDGPUDecodeSuite extends Suite {
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

  test("s_sendmsg_rtn_b64 with MSG_RTN_GET_REALTIME reads the real-time clock, and no other reply is modelled") {
    // llvm-mc -arch=amdgcn -mcpu=<target> -show-encoding: s_sendmsg_rtn_b64 s[0:1], sendmsg(MSG_RTN_GET_REALTIME)
    val realTime = Instruction.SReadClock(0, Clock.RealTime)
    for (target <- Vec(Target("gfx1100"), Target("gfx1150"), Target("gfx1170"), Target("gfx1200"))) {
      assertEquals(
        decoded(target, 0x83, 0x4d, 0x80, 0xbe),
        realTime,
        target.name
      )
      assert(VMCompiler.decodeInstruction(octets(0x80, 0x4d, 0x80, 0xbe), target).isLeft, target.name)
      assert(VMCompiler.decodeInstruction(octets(0x83, 0x4c, 0x80, 0xbe), target).isLeft, target.name)
    }
  }

  test("scheduling hints, explicit waits and code-end padding decode as waits") {
    // s_waitcnt_lgkmcnt null, 0 (Tensile gfx1030 GEMM), s_code_end, s_setprio.
    Vec(
      Target("gfx1030") -> Vec(0x00, 0x00, 0x7d, 0xbd),
      Target("gfx1036") -> Vec(0x00, 0x00, 0x9f, 0xbf),
      Target("gfx900")  -> Vec(0x41, 0x31, 0x8f, 0xbf)
    ).foreach { case (target, bytes) => assertEquals(decoded(target, bytes*), Instruction.Wait) }
  }

  test("trace, performance-level, wake-up and cache-maintenance hints decode as waits (CDNA3 ISA 12.5, 12.6)") {
    // LLVM gfx900: s_ttracedata, s_icache_inv, s_incperflevel 1, s_decperflevel 1, s_wakeup, s_sethalt 0,
    // s_setkill 0, s_cbranch_cdbgsys 2, s_cbranch_cdbguser 2, s_cbranch_cdbgsys_or_user 2,
    // s_cbranch_cdbgsys_and_user 2 (taken only under a debugger), s_dcache_wb, s_dcache_wb_vol,
    // s_dcache_discard s[2:3], 0x0, s_dcache_discard_x2 s[2:3], 0x0, s_atc_probe 0, s[2:3], 0x0,
    // s_atc_probe_buffer 0, s[4:7], 0x0, ds_nop, v_nop, v_clrexcp; gfx1030: s_ttracedata_imm 1, s_version 0,
    // s_waitcnt_vmcnt null, 0, s_waitcnt_expcnt null, 0, v_pipeflush; gfx1100: s_wait_event 0, s_wait_idle.
    Vec(
      Target("gfx900")  -> Vec(0x00, 0x00, 0x96, 0xbf),
      Target("gfx900")  -> Vec(0x00, 0x00, 0x93, 0xbf),
      Target("gfx900")  -> Vec(0x01, 0x00, 0x94, 0xbf),
      Target("gfx900")  -> Vec(0x01, 0x00, 0x95, 0xbf),
      Target("gfx900")  -> Vec(0x00, 0x00, 0x83, 0xbf),
      Target("gfx900")  -> Vec(0x00, 0x00, 0x8d, 0xbf),
      Target("gfx900")  -> Vec(0x00, 0x00, 0x8b, 0xbf),
      Target("gfx900")  -> Vec(0x02, 0x00, 0x97, 0xbf),
      Target("gfx900")  -> Vec(0x02, 0x00, 0x98, 0xbf),
      Target("gfx900")  -> Vec(0x02, 0x00, 0x99, 0xbf),
      Target("gfx900")  -> Vec(0x02, 0x00, 0x9a, 0xbf),
      Target("gfx900")  -> Vec(0x00, 0x00, 0x84, 0xc0, 0x00, 0x00, 0x00, 0x00),
      Target("gfx900")  -> Vec(0x00, 0x00, 0x8c, 0xc0, 0x00, 0x00, 0x00, 0x00),
      Target("gfx900")  -> Vec(0x01, 0x00, 0xa2, 0xc0, 0x00, 0x00, 0x00, 0x00),
      Target("gfx900")  -> Vec(0x01, 0x00, 0xa6, 0xc0, 0x00, 0x00, 0x00, 0x00),
      Target("gfx900")  -> Vec(0x01, 0x00, 0x9a, 0xc0, 0x00, 0x00, 0x00, 0x00),
      Target("gfx900")  -> Vec(0x02, 0x00, 0x9e, 0xc0, 0x00, 0x00, 0x00, 0x00),
      Target("gfx900")  -> Vec(0x00, 0x00, 0x28, 0xd8, 0x00, 0x00, 0x00, 0x00),
      Target("gfx900")  -> Vec(0x00, 0x00, 0x00, 0x7e),
      Target("gfx900")  -> Vec(0x00, 0x6a, 0x00, 0x7e),
      Target("gfx1030") -> Vec(0x01, 0x00, 0xa8, 0xbf),
      Target("gfx1030") -> Vec(0x00, 0x00, 0x80, 0xb0),
      Target("gfx1030") -> Vec(0x00, 0x00, 0x7d, 0xbc),
      Target("gfx1030") -> Vec(0x00, 0x00, 0xfd, 0xbc),
      Target("gfx1030") -> Vec(0x00, 0x36, 0x00, 0x7e),
      Target("gfx1100") -> Vec(0x00, 0x00, 0x8b, 0xbf),
      Target("gfx1100") -> Vec(0x00, 0x00, 0x8a, 0xbf)
    ).foreach { case (target, bytes) => assertEquals(decoded(target, bytes*), Instruction.Wait, clues(target, bytes)) }
    // LLVM gfx900: s_dcache_inv, s_dcache_inv_vol; gfx1030: s_gl1_inv. Scalar loads after them must not reuse
    // bytes read before.
    Vec(
      Target("gfx900")  -> Vec(0x00, 0x00, 0x80, 0xc0, 0x00, 0x00, 0x00, 0x00),
      Target("gfx900")  -> Vec(0x00, 0x00, 0x88, 0xc0, 0x00, 0x00, 0x00, 0x00),
      Target("gfx1030") -> Vec(0x00, 0x00, 0x7c, 0xf4, 0x00, 0x00, 0x00, 0x00)
    ).foreach { case (target, bytes) =>
      assertEquals(decoded(target, bytes*), Instruction.CacheInvalidate, clues(target, bytes))
    }
    // LLVM gfx1030 and gfx1100: s_round_mode 0x1 writes MODE's FP_ROUND field.
    Vec(Target("gfx1030") -> Vec(0x01, 0x00, 0xa4, 0xbf), Target("gfx1100") -> Vec(0x01, 0x00, 0x91, 0xbf)).foreach {
      case (target, bytes) =>
        assertEquals(decoded(target, bytes*), Instruction.SSetMode(0, 4, InstructionSource.Constant(1L)))
    }
    // LLVM gfx900: s_sethalt 1 halts the wave, which nothing resumes.
    assert(VMCompiler.decodeInstruction(Vec(0x01, 0x00, 0x8d, 0xbf).map(_.toByte), Target("gfx900")).exists {
      case Instruction.Halt(_) => true
      case _                   => false
    })
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
        case Instruction.SLoad(offset = decodedOffset, scalarOffset = None) =>
          assertEquals(decodedOffset, offset, target)
        case other => fail(s"$target: unexpected instruction $other")
      }
  }

  test("VOP3-encoded comparisons decode their scalar mask destination") {
    // gfx900 CMPX reuses the VOPC opcode; gfx1036 lg-f32 comes from miniBUDE.
    assertEquals(
      decoded(Target("gfx900"), 0x36, 0x00, 0xda, 0xd0, 0x0c, 0x17, 0x02, 0x00),
      Instruction.VCompare(
        VectorComparison(VectorComparisonKind.Unsigned32, VectorComparisonRelation.Equal, updateExec = true),
        54,
        InstructionSource.VectorRegister(12),
        InstructionSource.VectorRegister(11)
      )
    )
    assertEquals(
      decoded(Target("gfx1036"), 0x08, 0x00, 0x05, 0xd4, 0x80, 0x38, 0x02, 0x00),
      Instruction.VCompare(
        VectorComparison(VectorComparisonKind.Float32, VectorComparisonRelation.OrderedNotEqual, updateExec = false),
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

  test("codes outside an operand domain are decode errors, not SGPR reads") {
    // buffer_load_dword v1, v2, s[4:7], s125 offen: GFX9 and CDNA2 have no null register to read here
    val bufferLoad = octets(0x00, 0x10, 0x00, 0xe0, 0x02, 0x01, 0x01, 0x7d)
    Vec(Target("gfx900") -> "gfx9", Target("gfx90a") -> "cdna2").foreach { case (target, family) =>
      assert(
        VMCompiler.decodeInstruction(bufferLoad, target).left.exists(
          _.contains(s"SOFFSET code 125 is outside the ssrc_nolit domain on $family")
        ),
        target.name
      )
      assert(
        VMCompiler.decodeInstruction(octets(0x00, 0x10, 0x00, 0xe0, 0x02, 0x01, 0x01, 0x80), target).isRight,
        target.name
      )
    }
    // s_mov_b32 s2, reserved source code 209 on RDNA3
    assert(VMCompiler.decodeInstruction(octets(0xd1, 0x00, 0x82, 0xbe), Target("gfx1100")).left.exists(
      _.contains("SSRC0 code 209 is outside the src domain on rdna3")
    ))
  }
}
