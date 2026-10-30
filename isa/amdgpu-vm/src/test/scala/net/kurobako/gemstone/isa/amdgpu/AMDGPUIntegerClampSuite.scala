package net.kurobako.gemstone.isa.amdgpu

import VMIR.*
import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*

class AMDGPUIntegerClampSuite extends Suite {
  private val clamps = Seq(false, true)

  // Fixed LLVM gfx803 VOP3 encodings: v2, s[8:9], v3, v4, optionally s[10:11].
  for {
    (opcode, name, left, right, input, saturated, wrapped, carry) <- Seq(
      (0x19, "add overflow", 0xffffffffL, 1L, 0, 0xffffffffL, 0L, 1),
      (0x19, "add in range", 7L, 3L, 0, 10L, 10L, 0),
      (0x1a, "subtract underflow", 0L, 1L, 0, 0L, 0xffffffffL, 1),
      (0x1a, "subtract in range", 8L, 3L, 0, 5L, 5L, 0),
      (0x1b, "reverse subtract underflow", 1L, 0L, 0, 0L, 0xffffffffL, 1),
      (0x1b, "reverse subtract in range", 3L, 8L, 0, 5L, 5L, 0),
      (0x1c, "add incoming carry", 0xffffffffL, 0L, 1, 0xffffffffL, 0L, 1),
      (0x1c, "add without incoming carry", 0xfffffffeL, 1L, 0, 0xffffffffL, 0xffffffffL, 0),
      (0x1d, "subtract incoming borrow", 1L, 1L, 1, 0L, 0xffffffffL, 1),
      (0x1d, "subtract without incoming borrow", 1L, 1L, 0, 0L, 0L, 0),
      (0x1e, "reverse subtract incoming borrow", 1L, 1L, 1, 0L, 0xffffffffL, 1),
      (0x1e, "reverse subtract without incoming borrow", 1L, 1L, 0, 0L, 0L, 0)
    )
    clamp <- clamps
  }
    test(s"gfx803 $name clamp=$clamp retains the carry mask") {
      import InstructionSource.*
      val bytes = Vec(2, if (clamp) 0x88 else 8, opcode, 0xd1, 3, 9, if (opcode >= 0x1c) 0x2a else 2, 0).map(_.toByte)
      val operation  = decoded(Target("gfx803"), bytes)
      val memory     = RegionMemory()
      val allocation = AllocationId(1)
      memory.allocate(allocation, 8)
      val address    = memory.address(allocation, 0L).value.raw
      val operations = Vec(
        Instruction.SMove64(6, Constant(address)),
        Instruction.SMove64(10, Constant(input.toLong)),
        Instruction.VMove(3, Constant(left)),
        Instruction.VMove(4, Constant(right)),
        operation,
        Instruction.VMove(3, Scalar(8)),
        Instruction.Store(2, MemoryAddress.Global(0, Some(6), 0), 8, false),
        Instruction.End
      )
      assertEquals(runIR(operations, memory, codeKernel(target = Target("gfx803"))), Right(()))
      assertEquals(memory.getInt(allocation, 0), (if (clamp) saturated else wrapped).toInt)
      assertEquals(memory.getInt(allocation, 4), carry)
    }
}
