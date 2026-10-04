package net.kurobako.gemstone.isa.amdgpu

import ELF.*
import VMIR.*
import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class AMDGPUIntegerClampSuite extends munit.FunSuite {
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
    clamp <- Seq(false, true)
  }
    test(s"gfx803 $name clamp=$clamp retains the carry mask") {
      import InstructionSource.*
      val bytes = Vec(2, if (clamp) 0x88 else 8, opcode, 0xd1, 3, 9, if (opcode >= 0x1c) 0x2a else 2, 0).map(_.toByte)
      val operation  = VMCompiler.decodeInstruction(bytes, Target("gfx803")).fold(error => fail(error.raw), identity)
      val memory     = TestMemory()
      val allocation = AllocationId(1)
      memory.allocate(allocation, 8)
      val address    = memory.address(allocation, 0L).toOption.get.raw
      val operations = Vec(
        Instruction.SMove64(6, Constant(address)),
        Instruction.SMove64(10, Constant(input.toLong)),
        Instruction.VMove(3, Constant(left)),
        Instruction.VMove(4, Constant(right)),
        operation,
        Instruction.VMove(3, Scalar(8)),
        Instruction.GlobalStore(0, Some(6), 2, 8, 0),
        Instruction.End
      )
      val offsets = Vec.tabulate(operations.size)(_ * 4)
      val source  = CodeObjectKernel("clamp", Target("gfx803"), 0, 0, 0, 0, 0, Vec.empty)
      val kernel  = Kernel(source, operations, offsets.zipWithIndex.toMap, offsets, 0)
      val task    = KernelTask.validated("clamp", Dimensions.One, Dimensions.One, Vec.empty).toOption.get
      assertEquals(executeKernel(kernel, task, memory), Right(()))
      assertEquals(memory.getInt(allocation, 0), (if (clamp) saturated else wrapped).toInt)
      assertEquals(memory.getInt(allocation, 4), carry)
    }
}
