package net.kurobako.gemstone.isa.amdgpu

import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class AMDGPUControlFlowSuite extends munit.FunSuite {
  // Machine bytes are fixed ISA fixtures: deriving them from our encoder would hide decoder errors.

  test("vector condition masks follow the wave width") {
    val mask = 0x8000000100000001L
    assertEquals(VMArithmetic.waveConditionMask(mask, 32), 1L)
    assertEquals(VMArithmetic.waveConditionMask(mask, 64), mask)
  }

  test("gfx900 resolves VCC branches by byte address across mixed instruction widths") {
    val code     = words(0x7e000280, 0x7d980081, 0xbf860001, 0xbf820001, 0xbf82ffff, 0xbf810000)
    val kernel   = codeKernel(computePgmRsrc2 = 0x80, kernelCodeProperties = 0x0a, code = code)
    val dispatch = singleTask()

    val executable = compileKernel(kernel)
    assertEquals(executeKernel(executable, dispatch, RegionMemory()), Right(()))
    val limited =
      TaskControl(TaskControl.Limits(maxInstructions = 1L))
    assert(
      executeKernel(executable, dispatch, RegionMemory(), limited).left
        .exists(_.message.contains("instruction budget of 1"))
    )
  }

  test("RDNA4 split barriers keep arrivals, completion bits, and phases independent") {
    val barriers = VMState.WorkgroupBarriers(3)
    assertEquals(barriers.signal(-1, 0), Right(true))
    assertEquals(barriers.await(-1, 0), Right(false))
    assertEquals(barriers.signal(-2, 1), Right(true))
    assertEquals(barriers.signal(-1, 1), Right(false))
    assertEquals(barriers.await(-1, 0), Right(false))
    barriers.drop(2)
    assertEquals(barriers.await(-1, 0), Right(true))
    assertEquals(barriers.await(-1, 0), Right(false))
    assertEquals(barriers.await(-1, 1), Right(true))
    assertEquals(barriers.await(-2, 1), Right(false))
    assertEquals(barriers.signal(-2, 0), Right(false))
    assertEquals(barriers.await(-2, 1), Right(true))
    assertEquals(barriers.signal(-1, 1), Right(true))
    assertEquals(barriers.signal(-1, 0), Right(false))
    assertEquals(barriers.await(-1, 0), Right(true))
    assertEquals(barriers.await(0, 0), Right(true))
    assert(barriers.signal(1, 0).isLeft)
  }

  test("RDNA4 signal and wait retain distinct execution operations") {
    // LLVM gfx12 machine fixtures for the implicit workgroup barrier (-1).
    assertEquals(
      VMCompiler.decodeInstruction(Vec[Byte](0xc1.toByte, 0x4e, 0x80.toByte, 0xbe.toByte), Target("gfx1200")),
      Right(VMIR.Instruction.BarrierSignal(VMIR.InstructionSource.Constant(-1L), false))
    )
    assertEquals(
      VMCompiler.decodeInstruction(Vec[Byte](0xff.toByte, 0xff.toByte, 0x94.toByte, 0xbf.toByte), Target("gfx1200")),
      Right(VMIR.Instruction.BarrierWait(-1))
    )
  }

  test("gfx1036 releases a workgroup barrier after an earlier wave ends") {
    // The first wave takes the barrier path (local ids 0..31); the second
    // wave exits before it (local ids 32..63).  A hardware workgroup barrier
    // may be reached by the surviving cohort after the other wave has
    // completed, so runGroup must not restart or reject the completed wave.
    val code     = words(0xd4c1006a, 0x00014100, 0xbf870001, 0xbf810000, 0xbf8a0000, 0xbf810000)
    val kernel   = codeKernel(target = Target("gfx1036"), computePgmRsrc2 = 0x880, code = code)
    val dispatch = KernelTask.validated(
      "divergent_barrier",
      testDimensions(64, 1, 1),
      Dimensions.One,
      Vec.empty
    ).value

    assertEquals(executeKernel(compileKernel(kernel), dispatch, RegionMemory()), Right(()))
  }

}
