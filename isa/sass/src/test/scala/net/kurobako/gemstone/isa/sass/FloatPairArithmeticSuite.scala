package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.asm.*

class FloatPairArithmeticSuite extends SyntheticSASSExecutionSuiteBase {
  // CUDA 13.2 ptxas: add.f32x2 with the first input's two words explicitly exchanged.
  private val encoding = LittleEndian.toVec(0x000000080606724bL, 8) ++ LittleEndian.toVec(0x004fca0000020000L, 8)
  private def decoded(sm: Int, bytes: Vec[Byte] = encoding): DecodedInstruction =
    InstructionDecoder.decodeTyped(0, bytes.toArray, s"SM$sm").fold(fail(_), identity)

  private def runPair(
      left: String,
      right: String,
      expected: NonEmpty[Vec[Int]],
      values: NonEmpty[Vec[Int]] = Vec.nonEmpty(0x3fa00000, 0x41280000, 0x40400000, 0xc0800000),
      opcode: String = "FADD2",
      sm: Int = 100,
      binary: Boolean = false
  ): Unit = executeSynthetic(
    body = Vec.nonEmpty(("LDC.64", "R2,c[0x0][0x160]", None)) ++
      values.zipWithIndex.map((value, index) =>
        ("MOV", s"R${index + 6},0x${Integer.toUnsignedString(value, 16)}", None)
      ) ++
      Vec.nonEmpty((opcode, s"R6,$left,$right", None), ("STG.E.64", "[R2.64],R6", None), ("EXIT", "", None)),
    expected = expected,
    sm = sm,
    rewrite = instruction =>
      if (binary && (instruction.operation match { case _: Instruction.Fadd2 => true; case _ => false }))
        instruction.copy(operation = decoded(sm).operation)
      else instruction
  )

  for (sm <- Vec.nonEmpty(100, 103)) {
    test(s"sm$sm binary and assembly preserve pair swizzles and round trip") {
      val instruction = decoded(sm)
      val parsed      = Parser.parse("FADD2 R6,R6.F32x2.LO_HI,R8.F32x2.HI_LO;").fold(fail(_), identity)
      assertEquals(instruction.operation, parsed)
      val encoded   = Assembler.encode(parsed, s"SM$sm", 0).fold(fail(_), identity)
      val roundTrip = InstructionDecoder.decodeTyped(0, encoded, s"SM$sm").fold(fail(_), identity)
      assertEquals(roundTrip.operation, instruction.operation)
    }
    for (binary <- Vec.nonEmpty(false, true)) test(s"sm$sm FADD2 swaps first pair before aliased write binary=$binary") {
      runPair(
        left = "R6.F32x2.LO_HI",
        right = "R8.F32x2",
        expected = Vec.nonEmpty(0x41580000, 0xc0300000),
        sm = sm,
        binary = binary
      )
    }
  }

  test("FADD2 normal and swapped second pair keep lanes independent") {
    runPair(left = "R6.F32x2.HI_LO", right = "R8.F32x2", expected = Vec.nonEmpty(0x40880000, 0x40d00000))
    runPair(left = "R6.F32x2", right = "R8.F32x2.LO_HI", expected = Vec.nonEmpty(0xc0300000, 0x41580000))
  }
  for (sm <- Vec.nonEmpty(100, 103))
    test(s"sm$sm executes MiniBUDE's binary FMUL2 with aliased operands") {
      // Clang MiniBUDE, assembled by CUDA 13.3 ptxas: FMUL2 R10,R10.F32x2,R10.F32x2.
      val bytes     = LittleEndian.toVec(0x0000000a0a0a724aL, 8) ++ LittleEndian.toVec(0x000fe20000000000L, 8)
      val operation = decoded(sm, bytes).operation
      executeSynthetic(
        body = Vec.nonEmpty(
          ("LDC.64", "R2,c[0x0][0x160]", None),
          ("MOV", "R10,0x3fa00000", None),
          ("MOV", "R11,0xc1280000", None),
          ("FMUL2", "R10,R10.F32x2,R10.F32x2", None),
          ("STG.E.64", "[R2.64],R10", None),
          ("EXIT", "", None)
        ),
        expected = Vec.nonEmpty(0x3fc80000, 0x42dc8000),
        sm = sm,
        rewrite = instruction =>
          instruction.operation match {
            case _: Instruction.Fmul2 => instruction.copy(operation = operation)
            case _                    => instruction
          }
      )
    }

  test("FMUL2 multiplies normal, swapped and broadcast operands before aliased writes") {
    runPair(left = "R6.F32x2", right = "R8.F32x2", expected = Vec.nonEmpty(0x40700000, 0xc2280000), opcode = "FMUL2")
    runPair(
      left = "R6.F32x2.LO_HI",
      right = "R8.F32x2",
      expected = Vec.nonEmpty(0x41fc0000, 0xc0a00000),
      opcode = "FMUL2"
    )
    runPair(left = "R6.F32x2", right = "R9.F32", expected = Vec.nonEmpty(0xc0a00000, 0xc2280000), opcode = "FMUL2")
  }

  test("FADD2 scalar selectors broadcast one register without consuming its neighbour") {
    runPair(left = "R7.F32", right = "R8.F32x2", expected = Vec.nonEmpty(0x41580000, 0x40d00000))
    runPair(left = "R6.F32x2", right = "R9.F32", expected = Vec.nonEmpty(0xc0300000, 0x40d00000))
  }
  test("FADD2 FTZ applies independently to inputs and results") {
    runPair(
      left = "R6.F32x2",
      right = "R8.F32x2",
      expected = Vec.nonEmpty(1, 0x80000001),
      values = Vec.nonEmpty(1, 0x80000001, 0x80000000, 0x80000000)
    )
    runPair(
      left = "R6.F32x2",
      right = "R8.F32x2",
      expected = Vec.nonEmpty(0, 0x80000000),
      values = Vec.nonEmpty(1, 0x80000001, 0x80000000, 0x80000000),
      opcode = "FADD2.FTZ"
    )
    runPair(
      left = "R6.F32x2",
      right = "R8.F32x2",
      expected = Vec.nonEmpty(0x80000000, 0),
      values = Vec.nonEmpty(0x00800000, 0x00800001, 0x80800001, 0x80800000),
      opcode = "FADD2.FTZ"
    )
  }
  test("FADD2 preserves pair sign modifiers, immediate broadcasts and uniform zero") {
    runPair(left = "-R6.F32x2", right = "|R8.F32x2|", expected = Vec.nonEmpty(0x3fe00000, 0xc0d00000))
    runPair(left = "R6.F32x2", right = "2.0", expected = Vec.nonEmpty(0x40500000, 0x41480000))
    runPair(left = "R6.F32x2", right = "URZ.F32x2", expected = Vec.nonEmpty(0x3fa00000, 0x41280000))
  }

  test("FADD2 rejects misaligned, out-of-range pairs and unsupported rounding") {
    val module = SASSFixture.module(100, Vec(SASSFixture.Kernel("probe", 0, Vec.empty)))
    for (
      (opcode, operands) <- Vec.nonEmpty(
        "FADD2"    -> Vec("R7", "R6.F32x2", "R8.F32x2"),
        "FADD2"    -> Vec("R6", "R7.F32x2", "R8.F32x2"),
        "FADD2"    -> Vec("R6", "R254.F32x2", "R8.F32x2"),
        "FADD2"    -> Vec("R6", "R6.F32x2", "UR80.F32x2"),
        "FADD2.RM" -> Vec("R6", "R6.F32x2", "R8.F32x2")
      )
    ) {
      val function = DecodedFunction(
        "probe",
        Vec.nonEmpty(
          SASSFixture.instruction(0, opcode, operands),
          SASSFixture.instruction(16, "EXIT", Vec.empty)
        )
      )
      assert(SASSFixture.lower(module, module.kernels.head, Vec(function)).isLeft, clues(opcode, operands))
    }
  }

}
