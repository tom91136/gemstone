package net.kurobako.gemstone.isa.amdgpu

import fixtures.*
import net.kurobako.gemstone.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.*

class AllocationAddressSuite extends Suite {
  // Machine bytes are fixed ISA fixtures: deriving them from our encoder would hide decoder errors.

  // gfx900: env+8 contains a nested pointer absent from direct args. Increment its pointee,
  // reload into output, and store the exact typed env pointer at output+8.
  private val code = words(
    0xc00a0100, 0x00000000, 0xbf8cc07f, 0xc0060202, 0x00000008, 0xbf8cc07f, 0xc0020284, 0x00000000, 0xbf8cc07f,
    0x800a810a, 0x7e000280, 0x7e02020a, 0xdc708000, 0x00080100, 0xbf8c0f70, 0xc0020284, 0x00000000, 0xbf8cc07f,
    0x7e02020a, 0xdc708000, 0x00060100, 0x7e000288, 0x7e020204, 0x7e040205, 0xdc748000, 0x00060100, 0xbf810000
  )
  private val env      = AllocationId(1)
  private val nested   = AllocationId(118)
  private val output   = AllocationId(2)
  private val kernel   = codeKernel(kernargSize = 16, computePgmRsrc2 = 0x80, kernelCodeProperties = 0x08, code = code)
  private val dispatch = singleTask(KernelTask.Argument.Memory(env, 0L), KernelTask.Argument.Memory(output, 0L))
  private def run(memory: RegionMemory, value: CodeObjectKernel = kernel) =
    executeKernel(compileKernel(value), dispatch, memory)

  test("nested native pointers dereference unpassed allocations without rewriting payload or scalar words") {
    val values = Seq(41L, 70L)
    for (base <- Seq(0x3d9d8000L, 0x7a0010001000L)) {
      val target = base + 0x10000L
      val memory = RegionMemory()
      memory.allocate(env, 32, GuestAddress(base))
      memory.allocate(nested, 16, GuestAddress(target))
      memory.allocate(output, 16, GuestAddress(base + 0x20000L))
      LittleEndian.write(
        target,
        memory.array(env),
        0,
        8
      ) // ordinary scalar equal to a pointer; must remain untouched
      LittleEndian.write(target, memory.array(env), 8, 8)
      val original = memory.array(env).clone()
      for (value <- values) {
        LittleEndian.write(value, memory.array(nested), 0, 4)
        assertEquals(run(memory), Right(()))
        assertEquals(LittleEndian.decode(memory.array(nested), 0, 4), value + 1)
        assertEquals(LittleEndian.decode(memory.array(output), 0, 4), value + 1)
        assertEquals(LittleEndian.decode(memory.array(output), 8, 8), base)
        assert(memory.array(env).sameElements(original))
      }
    }
  }
}
