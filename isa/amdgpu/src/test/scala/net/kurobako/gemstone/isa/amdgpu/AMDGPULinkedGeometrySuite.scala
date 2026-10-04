package net.kurobako.gemstone.isa.amdgpu

import ELF.*
import VMIR.*
import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class AMDGPULinkedGeometrySuite extends munit.FunSuite {
  for (function <- Seq("__ockl_get_local_id", "__ockl_get_group_id", "__ockl_get_local_size"))
    test(s"$function executes its linked body after LLVM specializes away the dimension argument") {
      import InstructionSource.{VectorRegister as Register, *}
      val memory     = TestMemory()
      val allocation = AllocationId(1)
      memory.allocate(allocation, 512)
      val address = memory.address(allocation, 0L).toOption.get.raw
      val entry   = Vec(
        Instruction.SMove64(6, Constant(address)),
        Instruction.VMove(31, Register(0)),
        Instruction.VBinary(VectorBinaryOperation.ShiftLeft32, 8, Constant(2), Register(0), false),
        Instruction.VMove(0, Constant(31)),
        Instruction.VMove(1, Constant(0xdeadbeefL)),
        Instruction.SMove64(4, Constant(40)),
        Instruction.SSwapPc(30, Scalar(4)),
        Instruction.VBinary(VectorBinaryOperation.Or32, 2, Register(0), Register(1), false),
        Instruction.GlobalStore(8, Some(6), 2, 4, 0),
        Instruction.End
      )
      val body = if (function == "__ockl_get_local_id")
        Instruction.VBinary(VectorBinaryOperation.And32, 0, Constant(1023), Register(31), false)
      else Instruction.VMove(0, Constant(7))
      val operations = entry ++ Vec(body, Instruction.VMove(1, Constant(0)), Instruction.SSetPc(Scalar(30)))
      val offsets    = Vec.tabulate(operations.size)(_ * 4)
      val source     = CodeObjectKernel(
        name = "geometry",
        target = Target("gfx803"),
        kernargSize = 0,
        groupSegmentSize = 0,
        privateSegmentSize = 0,
        computePgmRsrc2 = 0,
        kernelCodeProperties = 0,
        code = Vec.empty,
        functionTargets = Map(40L -> function)
      )
      val kernel = Kernel(source, operations, offsets.zipWithIndex.toMap, offsets, 0)
      val task   = KernelTask.validated("geometry", Dimensions(128, 1, 1), Dimensions.One, Vec.empty).toOption.get
      assertEquals(executeKernel(kernel, task, memory), Right(()))
      for (lane <- 0 until 128)
        assertEquals(
          memory.getInt(allocation, (lane * 4).unsafe[Int >= 0]),
          if (function == "__ockl_get_local_id") lane else 7
        )
    }

  test("gfx803 MADAK consumes the embedded literal without decoding it as an instruction") {
    val encoded = Vec(1, 5, 6, 0x30, 0, 0, 0, 0x3f).map(_.toByte)
    assertEquals(
      VMCompiler.decodeInstruction(encoded, Target("gfx803")),
      Right(Instruction.VMadAkF32(
        3,
        InstructionSource.VectorRegister(1),
        InstructionSource.VectorRegister(2),
        0x3f000000L
      ))
    )
    assertEquals(VMCompiler.validate(encoded ++ Vec(0, 0, 0x81, 0xbf).map(_.toByte), Target("gfx803")), Right(()))
    assert(VMCompiler.decodeInstruction(encoded.take(4), Target("gfx803")).isLeft)
  }

  test("gfx803 immediate hardware-register writes consume their literal") {
    val encoded = Vec(1, 9, 0, 0xba, 3, 0, 0, 0).map(_.toByte)
    assertEquals(VMCompiler.validate(encoded ++ Vec(0, 0, 0x81, 0xbf).map(_.toByte), Target("gfx803")), Right(()))
    assert(VMCompiler.decodeInstruction(encoded.take(4), Target("gfx803")).isLeft)
  }
}
