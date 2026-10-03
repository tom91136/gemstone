package net.kurobako.gemstone.isa.amdgpu

import ELF.*
import VMIR.*
import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class ExecutionOperandSuite extends munit.FunSuite {
  // Decoder field tests live in the modifier/memory suites. This fixture exercises
  // lowered instructions through register state and observable memory writes.
  private def run(target: Target, instruction: Instruction, initial: Int = 0x11223344): Int =
    runSequence(target, Vec(instruction), initial)

  private def runSequence(target: Target, operations: Vec[Instruction], initial: Int = 0x11223344): Int = {
    val memory     = TestMemory()
    val allocation = AllocationId(1L)
    memory.allocate(allocation, 16)
    memory.putInt(allocation, 8, 0x89ab8080)
    val address      = memory.address(allocation, 0L).toOption.get.raw
    val instructions = Vec(
      Instruction.VMove(0, InstructionSource.Constant(address & 0xffffffffL)),
      Instruction.VMove(1, InstructionSource.Constant(address >>> 32)),
      Instruction.VMove(2, InstructionSource.Constant(initial.toLong & 0xffffffffL))
    ) ++ operations ++ Vec(
      Instruction.GlobalStore(0, None, 2, 4, 0),
      Instruction.End
    )
    val offsets = Vec.tabulate(instructions.size)(_ * 4)
    val source  = CodeObjectKernel(
      name = "operand",
      target = target,
      kernargSize = 0,
      groupSegmentSize = 0,
      privateSegmentSize = 0,
      computePgmRsrc2 = 0,
      kernelCodeProperties = 0,
      code = Vec.empty
    )
    val kernel = Kernel(source, instructions, offsets.zipWithIndex.toMap, offsets, 0)
    val task   = KernelTask.validated("operand", Dimensions.One, Dimensions.One, Vec.empty).toOption.get
    assertEquals(executeKernel(kernel, task, memory), Right(()))
    assertEquals(memory.getInt(allocation, 8), 0x89ab8080)
    memory.getInt(allocation, 0)
  }

  test("memory transfers can end at the final vector register") {
    val target = Target("gfx900")
    for (width <- Seq[Int in (1, 2, 4)](1, 2, 4)) {
      val load = Instruction.GlobalLoad(255, 0, None, width.widen[Int in 1 ~ 16], 8, false)
      val expected = width.raw match {
        case 1 => 0x80
        case 2 => 0x8080
        case _ => 0x89ab8080
      }
      assertEquals(runSequence(target, Vec(load,
        Instruction.VMove(2, InstructionSource.VectorRegister(255)))), expected)
    }
  }

  test("wave32 mask writes decode and execute at the last scalar slot") {
    val target = Target("gfx1030")
    for (text <- Seq("v_add_co_u32 v2, exec_hi, v3, v4", "v_cmp_eq_u32 exec_hi, v3, v4")) {
      val bytes = asm.Assembler.encode(asm.Parser.parse(text).toOption.get, target).toOption.get
      assert(VMCompiler.decodeInstruction(bytes, target).isRight, text)
    }
    val operation = Instruction.VAddCarry(2, 127, InstructionSource.Constant(1), InstructionSource.Constant(1), None)
    assert(VMRegisterBounds.validate(operation, 32).isRight)
    assert(VMRegisterBounds.validate(operation, 64).isLeft)
    assertEquals(run(target, operation), 2)
  }

  test("64-bit vector operations read live EXEC and VCC values") {
    val target = Target("gfx900")
    for (scalar <- Seq[ScalarRegisterIndex](106, 126)) {
      val initialize = Instruction.SMove64(scalar, InstructionSource.Constant(1))
      for (operation <- Seq(
        Instruction.VShiftRight64(2, InstructionSource.Constant(0), InstructionSource.Scalar(scalar)),
        Instruction.VShiftLeft64(2, InstructionSource.Constant(0), InstructionSource.Scalar(scalar)),
        Instruction.VShiftRightArithmetic64(2, InstructionSource.Constant(0), InstructionSource.Scalar(scalar)),
        Instruction.VBinary64Modified(VectorBinary64Operation.Add, 2, InstructionSource.Scalar(scalar),
          InstructionSource.Constant(0), false, false, false, false)
      )) assertEquals(runSequence(target, Vec(initialize, operation)), 1)
      val fullMask = Instruction.SMove64(scalar, InstructionSource.Constant(0x1234567800000001L))
      assertEquals(runSequence(target, Vec(fullMask,
        Instruction.VShiftRight64(2, InstructionSource.Constant(32), InstructionSource.Scalar(scalar)))), 0x12345678)
      val high = scalar.offsetBy(1)
      val highRead = Instruction.VShiftRight64(2, InstructionSource.Constant(0), InstructionSource.Scalar(high))
      assert(VMRegisterBounds.validate(highRead).isRight)
      assertEquals(runSequence(target, Vec(fullMask, highRead)), 0x12345678)
    }
  }

  test("scalar bank validation preserves EXEC and VCC special sources") {
    val target = Target("gfx900")
    for (register <- Seq[ScalarRegisterIndex](106, 107, 126, 127)) {
      val move = Instruction.SMove64(4, InstructionSource.Scalar(register))
      assert(VMRegisterBounds.validate(move).isRight)
      val expected = if (register.raw == 126) 1 else 0
      assertEquals(runSequence(target, Vec(move, Instruction.VMove(2, InstructionSource.Scalar(4)))), expected)
    }
    val writeExec = Instruction.SMove64(126, InstructionSource.Constant(1))
    assert(VMRegisterBounds.validate(writeExec).isRight)
    assertEquals(run(target, writeExec), 0x11223344)
  }

  test("ldexp executes with a signed scalar exponent in v255") {
    for (exponent <- Seq(3, -2)) {
      val operations = Vec(
        Instruction.VMove(4, InstructionSource.Constant(0L)),
        Instruction.VMove(5, InstructionSource.Constant(0x3ff80000L)), // 1.5
        Instruction.VMove(255, InstructionSource.Constant(exponent.toLong)),
        Instruction.VBinary64Modified(
          VectorBinary64Operation.LoadExponent, 2,
          InstructionSource.VectorRegister(4), InstructionSource.VectorRegister(255),
          false, false, false, false
        ),
        Instruction.VMove(2, InstructionSource.VectorRegister(3))
      )
      val expected = (java.lang.Double.doubleToRawLongBits(Math.scalb(1.5, exponent)) >>> 32).toInt
      assertEquals(runSequence(Target("gfx900"), operations), expected)
    }
  }

  private val destinations = Seq(
    (SubDwordSelection.Byte0, 0x00000080L, 0xffffff80L, 0x11223380L),
    (SubDwordSelection.Byte1, 0x00008000L, 0xffff8000L, 0x11228044L),
    (SubDwordSelection.Byte2, 0x00800000L, 0xff800000L, 0x11803344L),
    (SubDwordSelection.Byte3, 0x80000000L, 0x80000000L, 0x80223344L),
    (SubDwordSelection.Word0, 0x00008080L, 0xffff8080L, 0x11228080L),
    (SubDwordSelection.Word1, 0x80800000L, 0x80800000L, 0x80803344L),
    (SubDwordSelection.DWord, 0xffff8080L, 0xffff8080L, 0xffff8080L)
  )

  for (target <- Seq(Target("gfx900"), Target("gfx1030"))) {
    test(s"$target SDWA destination placement pads, sign-extends, or preserves each selection") {
      for {
        (selection, pad, signed, preserved) <- destinations
        (unused, expected)                  <- Seq(
          SubDwordDestinationUnused.Pad        -> pad,
          SubDwordDestinationUnused.SignExtend -> signed,
          SubDwordDestinationUnused.Preserve   -> preserved
        )
      } {
        val instruction = Instruction.VBinarySubDword(
          VectorSubDwordOperation.Or32,
          2,
          InstructionSource.Constant(0xffff8080L),
          InstructionSource.Constant(0L),
          selection,
          unused,
          SubDwordSelection.DWord,
          SubDwordSelection.DWord,
          false,
          false
        )
        assertEquals(run(target, instruction), expected.toInt, clues(selection, unused))
      }
    }

    test(s"$target SDWA source selection and sign extension feed the operation") {
      val instruction = Instruction.VBinarySubDword(
        VectorSubDwordOperation.AddU32,
        2,
        InstructionSource.Constant(0x00800000L),
        InstructionSource.Constant(0x00000300L),
        SubDwordSelection.DWord,
        SubDwordDestinationUnused.Pad,
        SubDwordSelection.Byte2,
        SubDwordSelection.Byte1,
        true,
        false
      )
      assertEquals(run(target, instruction), -125)
    }

    test(s"$target narrow loads sign-extend and D16 loads preserve the other half") {
      assertEquals(run(target, Instruction.GlobalLoadSigned(2, 0, None, 1, 8)), -128)
      assertEquals(run(target, Instruction.GlobalLoadSigned(2, 0, None, 2, 8)), -32640)
      assertEquals(run(target, Instruction.GlobalLoadD16(2, 0, None, 8, false)), 0x11228080)
      assertEquals(run(target, Instruction.GlobalLoadD16(2, 0, None, 8, true)), 0x80803344)
    }

    test(s"$target packed arithmetic retains signedness, lane overflow, and source selection") {
      val cases = Seq(
        (VectorPackedBinaryOperation.MaximumI16, 0x80007fffL, 0x7fff8000L, true, true, 0x7fff7fffL),
        (VectorPackedBinaryOperation.MaximumU16, 0x80007fffL, 0x7fff8000L, true, true, 0x80008000L),
        (VectorPackedBinaryOperation.AddU16, 0xffff0001L, 0x00010002L, true, true, 0x00000003L),
        (VectorPackedBinaryOperation.MultiplyLowU16, 0x00020003L, 0x00050007L, false, true, 0x000f0015L),
        (VectorPackedBinaryOperation.MultiplyLowU16, 0x00020003L, 0x00050007L, true, false, 0x000e0015L)
      )
      cases.foreach { (operation, left, right, leftHigh, rightHigh, expected) =>
        assertEquals(
          run(
            target,
            Instruction.VPackedBinary(
              operation,
              2,
              InstructionSource.Constant(left),
              InstructionSource.Constant(right),
              leftHigh,
              rightHigh
            )
          ),
          expected.toInt,
          clues(operation, leftHigh, rightHigh)
        )
      }
    }
  }
  test("unsigned byte dot product uses all four lanes and wraps the accumulator") {
    for (
      (left, right, accumulator, expected) <- Seq(
        (0x04030201L, 0x08070605L, 11L, 81L),
        (0xffffffffL, 0xffffffffL, 0xfffffff0L, 260084L),
        (0x80000000L, 0x02000000L, 9L, 265L)
      )
    ) assertEquals(
      run(
        Target("gfx1030"),
        Instruction.VDot4U32U8(
          2,
          InstructionSource.Constant(left),
          InstructionSource.Constant(right),
          InstructionSource.Constant(accumulator)
        )
      ),
      expected.toInt
    )
  }

  test(
    "mixed FMA selects half inputs independently, applies absolute before negate, and preserves the destination half"
  ) {
    // A.high = -2, B is f32 3, C.low = -0.5. abs(A), -abs(C) => 2*3-0.5 = 5.5.
    val a = InstructionSource.Constant(0xc0003c00L)
    val b = InstructionSource.Constant(0x40400000L)
    val c = InstructionSource.Constant(0x4400b800L)
    assertEquals(run(Target("gfx900"), Instruction.VFmaMixF32(2, a, b, c, 1, 5, 4, 5, false)), 0x40b00000)
    assertEquals(run(Target("gfx900"), Instruction.VFmaMixF32(2, a, b, c, 1, 5, 4, 5, true)), 0x3f800000)
    assertEquals(run(Target("gfx900"), Instruction.VFmaMixF16(2, a, b, c, 1, 5, 4, 5, false, false)), 0x11224580)
    assertEquals(run(Target("gfx900"), Instruction.VFmaMixF16(2, a, b, c, 1, 5, 4, 5, false, true)), 0x45803344)
    assertEquals(run(Target("gfx900"), Instruction.VFmaMixF16(2, a, b, c, 1, 5, 4, 5, true, true)), 0x3c003344)
  }

  for (
    (kind, values) <- Seq(
      VectorComparisonKind.ClassFloat32 -> Seq(0x7f800001L, 0x7fc00001L, 0xff800000L, 0xbf800000L, 0x80000001L,
        0x80000000L, 0L, 1L, 0x3f800000L, 0x7f800000L),
      VectorComparisonKind.ClassFloat64 -> Seq(
        0x7ff0000000000001L, 0x7ff8000000000001L, 0xfff0000000000000L,
        0xbff0000000000000L, 0x8000000000000001L, 0x8000000000000000L, 0L, 1L, 0x3ff0000000000000L, 0x7ff0000000000000L
      )
    )
  ) test(s"$kind distinguishes all ten floating classes and tests membership in the supplied mask") {
    values.zipWithIndex.foreach { (bits, classBit) =>
      for (mask <- Seq(1L << classBit, 1023L ^ (1L << classBit))) {
        val instructions = Vec(
          Instruction.SMove(12, InstructionSource.Constant(bits & 0xffffffffL)),
          Instruction.SMove(13, InstructionSource.Constant(bits >>> 32)),
          Instruction.VCompareTo(
            10,
            VectorComparison(kind, VectorComparisonRelation.Class, false),
            InstructionSource.Scalar(12),
            InstructionSource.Constant(mask),
            false,
            false,
            false,
            false
          ),
          Instruction.VMove(2, InstructionSource.Scalar(10))
        )
        assertEquals(
          runSequence(Target("gfx900"), instructions),
          if (mask == (1L << classBit)) 1 else 0,
          clues(bits, mask)
        )
      }
    }
  }

}
