package net.kurobako.gemstone.isa.amdgpu

import VMIR.*
import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class AMDGPULinkedGeometrySuite extends Suite {
  for (function <- Seq("__ockl_get_local_id", "__ockl_get_group_id", "__ockl_get_local_size"))
    test(s"$function executes its linked body after LLVM specializes away the dimension argument") {
      import InstructionSource.{VectorRegister as Register, *}
      val memory     = RegionMemory()
      val allocation = AllocationId(1)
      memory.allocate(allocation, 512)
      val address = memory.address(allocation, 0L).value.raw
      val entry   = Vec(
        Instruction.SMove64(6, Constant(address)),
        Instruction.VMove(31, Register(0)),
        Instruction.VBinary(VectorBinaryOperation.ShiftLeft32, 8, Constant(2), Register(0), false),
        Instruction.VMove(0, Constant(31)),
        Instruction.VMove(1, Constant(0xdeadbeefL)),
        Instruction.SMove64(4, Constant(40)),
        Instruction.SSwapPc(30, Scalar(4)),
        Instruction.VBinary(VectorBinaryOperation.Or32, 2, Register(0), Register(1), false),
        Instruction.Store(2, MemoryAddress.Global(8, Some(6), 0), 4, false),
        Instruction.End
      )
      val body = if (function == "__ockl_get_local_id")
        Instruction.VBinary(VectorBinaryOperation.And32, 0, Constant(1023), Register(31), false)
      else Instruction.VMove(0, Constant(7))
      val operations = entry ++ Vec(body, Instruction.VMove(1, Constant(0)), Instruction.SSetPc(Scalar(30)))
      val source     = codeKernel(target = Target("gfx803"), functionTargets = Map(40L -> function))
      val task       = KernelTask.validated("kernel", Dimensions(128, 1, 1), Dimensions.One, Vec.empty).value
      assertEquals(runIR(operations, memory, source, task), Right(()))
      for (lane <- 0 until 128)
        assertEquals(
          memory.getInt(allocation, (lane * 4).unsafe[Int >= 0]),
          if (function == "__ockl_get_local_id") lane else 7
        )
    }
}
