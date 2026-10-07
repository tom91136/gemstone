package net.kurobako.gemstone.isa.amdgpu

import fixtures.*
import net.kurobako.gemstone.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.*

class ScalarNotSuite extends munit.FunSuite {
  // Machine bytes are fixed ISA fixtures: deriving them from our encoder would hide decoder errors.

  // AMD Vega ISA SOP1: complement all destination bits, SCC = (result != 0).
  // https://www.amd.com/content/dam/amd/en/documents/radeon-tech-docs/instruction-set-architectures/vega-shader-instruction-set-architecture.pdf
  private val program = Vec(
    0xc00a0100, 0x00000000, 0xbf8cc07f, 0xbf068080, 0xbe8900ff, 0x12345678, 0xbe880504, 0x850a8081, 0x7e000280,
    0x7e020208, 0x7e040209, 0xdc748000, 0x00060100, 0x7e000288, 0x7e02020a, 0xdc708000, 0x00060100, 0xbf810000
  )
  private val output = AllocationId(1)
  test("scalar NOT writes full width and sets or clears SCC independently of its previous value") {
    for {
      width   <- Seq(32, 64)
      initial <- Seq(false, true)
      input   <- Seq(-1L, 0L, 0xaaaaaaaa55555555L, 0xffffffffL)
    } {
      val instruction = if (width == 64) 0xbe880504 else 0xbe880404
      val cmp         = if (initial) 0xbf068080 else 0xbf078080
      val code        = words(program.updated(3, cmp).updated(6, instruction)*)
      val kernel      = codeKernel(kernargSize = 16, computePgmRsrc2 = 0x80, kernelCodeProperties = 0x08, code = code)
      val dispatch    = singleTask(
        KernelTask.Argument.Scalar(LittleEndian.toVec(input, 8).unsafe),
        KernelTask.Argument.Memory(output, 0L)
      )
      val memory = RegionMemory(output -> 16)
      assertEquals(
        executeKernel(compileKernel(kernel), dispatch, memory),
        Right(())
      )
      val result = if (width == 64) ~input else ~input & 0xffffffffL
      val stored = if (width == 64) result else 0x1234567800000000L | result
      assertEquals(LittleEndian.decode(memory.array(output), 0, 8), stored)
      assertEquals(
        LittleEndian.decode(memory.array(output), 8, 4),
        if (result == 0L) 0L else 1L
      )
    }
  }
}
