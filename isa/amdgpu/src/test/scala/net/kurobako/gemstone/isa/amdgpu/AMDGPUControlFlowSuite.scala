package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Constraint.Positive

import net.kurobako.gemstone.Constraint.NonEmptyString

import VirtualMachine.*

import CodeObject.*
import VMIR.*
import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.binary.{Bits, ByteRange}
import net.kurobako.gemstone.binary.IEEE754
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.Dimensions

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets
import java.util.concurrent.Executors

final class AMDGPUControlFlowSuite extends munit.FunSuite with CodeObjectTestSupport {
  test("vector condition masks follow the wave width") {
    val mask = 0x8000000100000001L
    assertEquals(VMInterpreter.waveConditionMask(mask, LaneCount(32)), 1L)
    assertEquals(VMInterpreter.waveConditionMask(mask, LaneCount(64)), mask)
  }

  test("gfx900 VOP3 mask bit counts compose across both halves of a wave") {
    val executionMask = 0xf000000f0000000fL
    assertEquals(VMInterpreter.mbCount(-1L, executionMask, 0, false, 0L), 0L)
    assertEquals(VMInterpreter.mbCount(-1L, executionMask, 4, false, 0L), 4L)
    assertEquals(VMInterpreter.mbCount(-1L, executionMask, 32, false, 0L), 4L)
    assertEquals(VMInterpreter.mbCount(-1L, executionMask, 60, true, 8L), 12L)
    assertEquals(VMInterpreter.mbCount(-1L, executionMask, 64 - 1, true, 8L), 15L)
    assertEquals(
      VMCompiler.validate(
        Vec[Byte](
          0x03,
          0x00,
          0x8c.toByte,
          0xd2.toByte,
          0xc1.toByte,
          0x00,
          0x01,
          0x00,
          0x03,
          0x00,
          0x8d.toByte,
          0xd2.toByte,
          0xc1.toByte,
          0x06,
          0x02,
          0x00,
          0x00,
          0x00,
          0x81.toByte,
          0xbf.toByte
        ),
        Target("gfx900")
      ),
      Right(())
    )
  }

  test("gfx900 bit reverse accepts canonical inline floating constants as raw bits") {
    assertEquals(VMInterpreter.bitReverse32(0x3f000000L), 0x000000fcL)
    assertEquals(
      VMCompiler.validate(
        Vec[Byte](
          0xf0.toByte,
          0x58,
          0x08,
          0x7e,
          0x00,
          0x00,
          0x81.toByte,
          0xbf.toByte
        ),
        Target("gfx900")
      ),
      Right(())
    )
  }

  test("gfx900 unsigned 24-bit vector multiply and multiply-add mask their multiplicands") {
    assertEquals(VMInterpreter.unsignedMultiply24(0x1ffffffL, 2L), 0x1fffffeL)
    assertEquals(VMInterpreter.unsignedMultiplyAdd24(0xffffffL, 2L, 3L), 0x2000001L)
    assertEquals(
      VMCompiler.validate(
        Vec[Byte](
          0x94.toByte,
          0x00,
          0x08,
          0x10,
          0x05,
          0x00,
          0xc3.toByte,
          0xd1.toByte,
          0x00,
          0x29,
          0x15,
          0x04,
          0x00,
          0x00,
          0x81.toByte,
          0xbf.toByte
        ),
        Target("gfx900")
      ),
      Right(())
    )
  }

  test("gfx900 stride-64 DS pairs decode their independently scaled offsets") {
    assertEquals(
      VMCompiler.validate(
        Vec[Byte](
          0x00,
          0x14,
          0x1e,
          0xd8.toByte,
          0x05,
          0x02,
          0x01,
          0x00,
          0x00,
          0x00,
          0x81.toByte,
          0xbf.toByte
        ),
        Target("gfx900")
      ),
      Right(())
    )
  }

  test("gfx900 DS bpermute selects a source lane from a byte address") {
    assertEquals(VMInterpreter.bpermuteSourceLane(0L), 0)
    assertEquals(VMInterpreter.bpermuteSourceLane(4L * 37L), 37)
    assertEquals(VMInterpreter.bpermuteSourceLane(4L * 69L), 5)
    assertEquals(VMInterpreter.bpermuteSourceLane(0L, 124L), 31)
    assertEquals(VMInterpreter.bpermuteSourceLane(4L * 37L, 124L), 4)
    assertEquals(
      VMCompiler.validate(
        Vec[Byte](
          0x00,
          0x00,
          0x7e,
          0xd8.toByte,
          0x04,
          0x05,
          0x00,
          0x06,
          0x00,
          0x00,
          0x81.toByte,
          0xbf.toByte
        ),
        Target("gfx900")
      ),
      Right(())
    )
  }

  test("gfx1036 DS permute selects a destination lane from a byte address") {
    assertEquals(VMInterpreter.permuteDestinationLane(0L, 0L, WaveSize(32)), 0)
    assertEquals(VMInterpreter.permuteDestinationLane(4L * 29L, 0L, WaveSize(32)), 29)
    assertEquals(VMInterpreter.permuteDestinationLane(0L, 124L, WaveSize(32)), 31)
    assertEquals(VMInterpreter.permuteDestinationLane(4L * 37L, 124L, WaveSize(32)), 4)
    assertEquals(
      VMCompiler.decodeInstruction(
        Vec[Byte](0x00, 0x00, 0xc8.toByte, 0xda.toByte, 0x01, 0x02, 0x00, 0x05),
        Target("gfx1036")
      ),
      Right(Instruction.DSPermute(5, 1, 2, 0))
    )
  }

  test("gfx900 DS operations strip the local address-space tag") {
    assertEquals(VMInterpreter.ldsOffset(0x18000018L), 0x18L)
    assertEquals(VMInterpreter.ldsOffset(0x00000018L), 0x18L)
    assertEquals(VMInterpreter.ldsOffset(0x00010018L), 0x18L)
  }

  test("gfx900 reverse unsigned vector subtraction wraps to 32 bits") {
    assertEquals(VMInterpreter.unsignedSubtractReverse32(20L, 19L), 0xffffffffL)
    assertEquals(VMInterpreter.unsignedSubtractReverse32(20L, 99L), 79L)
    assertEquals(
      VMCompiler.validate(
        Vec[Byte](0x94.toByte, 0x06, 0x0a, 0x6c, 0x00, 0x00, 0x81.toByte, 0xbf.toByte),
        Target("gfx900")
      ),
      Right(())
    )
  }

  test("gfx900 signed 24-bit vector multiply sign-extends both operands") {
    assertEquals(VMInterpreter.signedMultiply24(0xffffd8L, 0xfffffeL), 80L)
    assertEquals(VMInterpreter.signedMultiply24(0x1000001L, 3L), 3L)
    assertEquals(
      VMCompiler.validate(
        Vec[Byte](
          0xff.toByte,
          0x00,
          0x12,
          0x0c,
          0xd8.toByte,
          0xff.toByte,
          0xff.toByte,
          0xff.toByte,
          0x00,
          0x00,
          0x81.toByte,
          0xbf.toByte
        ),
        Target("gfx900")
      ),
      Right(())
    )
  }

  test("gfx900 DPP routes quad, row-rotate, and row-broadcast sources") {
    assertEquals(
      Vec.tabulate(8)(lane => VMInterpreter.dppQuadSourceLane(lane, 0xb1)),
      Vec(1, 0, 3, 2, 5, 4, 7, 6)
    )
    assert((0 until 64).forall(lane => VMInterpreter.dppLaneEnabled(lane, 0xf, 0xf)))
    assert(!VMInterpreter.dppLaneEnabled(0, 0xe, 0xf))
    assert(!VMInterpreter.dppLaneEnabled(1, 0xf, 0xd))
    assertEquals(
      Vec(0, 4, 15, 16).map(VMInterpreter.dppSourceLane(_, 0x124)),
      Vec(Some(12), Some(0), Some(11), Some(28))
    )
    assertEquals(
      Vec(0, 16, 32, 48).map(VMInterpreter.dppSourceLane(_, 0x142)),
      Vec(None, Some(15), None, Some(47))
    )
    assertEquals(
      Vec(0, 16, 32, 48).map(VMInterpreter.dppSourceLane(_, 0x143)),
      Vec(None, None, Some(31), Some(31))
    )
    assertEquals(
      VMCompiler.validate(
        Vec[Byte](
          0xfa.toByte,
          0x02,
          0x06,
          0x7e,
          0x01,
          0xb1.toByte,
          0x00,
          0xff.toByte,
          0xfa.toByte,
          0x02,
          0x0c,
          0x7e,
          0x05,
          0x24,
          0x01,
          0xff.toByte,
          0xfa.toByte,
          0x02,
          0x0c,
          0x7e,
          0x05,
          0x28,
          0x01,
          0xff.toByte,
          0xfa.toByte,
          0x02,
          0x0c,
          0x7e,
          0x05,
          0x42,
          0x01,
          0xff.toByte,
          0xfa.toByte,
          0x02,
          0x0c,
          0x7e,
          0x05,
          0x43,
          0x01,
          0xff.toByte,
          0x00,
          0x00,
          0x81.toByte,
          0xbf.toByte
        ),
        Target("gfx900")
      ),
      Right(())
    )
  }

  test("gfx900 resolves VCC branches by byte address across mixed instruction widths") {
    val code = Vec[Byte](
      0x80.toByte,
      0x02,
      0x00,
      0x7e,
      0x81.toByte,
      0x00,
      0x98.toByte,
      0x7d,
      0x01,
      0x00,
      0x86.toByte,
      0xbf.toByte,
      0x01,
      0x00,
      0x82.toByte,
      0xbf.toByte,
      0xff.toByte,
      0xff.toByte,
      0x82.toByte,
      0xbf.toByte,
      0x00,
      0x00,
      0x81.toByte,
      0xbf.toByte
    )
    val kernel   = CodeObjectKernel("branch", Target("gfx900"), 0, 0, 0, 0x80, 0x0a, code)
    val dispatch = KernelTask.validated(
      NonEmptyString("branch"),
      Dimensions.One,
      Dimensions.One,
      Vec.empty
    ).toOption.get

    val executable = compileKernel(kernel)
    assertEquals(executeKernel(executable, dispatch, TestMemory()), Right(()))
    val limited =
      TaskControl.forScheduler(TaskLimits(maxInstructions = 1L, quantum = 1))
    assert(
      executeKernel(executable, dispatch, TestMemory(), limited).left
        .exists(_.message.contains("instruction budget of 1"))
    )
  }

  test("gfx1036 releases a workgroup barrier after an earlier wave ends") {
    // The first wave takes the barrier path (local ids 0..31); the second
    // wave exits before it (local ids 32..63).  A hardware workgroup barrier
    // may be reached by the surviving cohort after the other wave has
    // completed, so runGroup must not restart or reject the completed wave.
    val code = Vec[Byte](
      0x6a,
      0x00,
      0xc1.toByte,
      0xd4.toByte,
      0x00,
      0x41,
      0x01,
      0x00,
      0x01,
      0x00,
      0x87.toByte,
      0xbf.toByte,
      0x00,
      0x00,
      0x81.toByte,
      0xbf.toByte,
      0x00,
      0x00,
      0x8a.toByte,
      0xbf.toByte,
      0x00,
      0x00,
      0x81.toByte,
      0xbf.toByte
    )
    val kernel = CodeObjectKernel(
      "divergent_barrier",
      Target("gfx1036"),
      0,
      0,
      0,
      computePgmRsrc2 = 0x880,
      kernelCodeProperties = 0,
      code
    )
    val dispatch = KernelTask.validated(
      NonEmptyString("divergent_barrier"),
      testDimensions(64, 1, 1),
      Dimensions.One,
      Vec.empty
    ).toOption.get

    assertEquals(executeKernel(compileKernel(kernel), dispatch, TestMemory()), Right(()))
  }

}
