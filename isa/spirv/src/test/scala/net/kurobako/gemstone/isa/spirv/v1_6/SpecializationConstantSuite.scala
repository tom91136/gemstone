package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.spirv.v1_6.execution.*

class SpecializationConstantSuite extends munit.FunSuite {
  private def evaluate(width: Int, opcode: Int, left: Long, right: Long): Long = {
    val integer              = operand(OperandKind.IdResult, 1)
    val a                    = operand(OperandKind.IdResult, 2)
    val b                    = operand(OperandKind.IdResult, 3)
    val out                  = operand(OperandKind.IdResult, 4)
    def literal(value: Long) =
      if (width == 64) operand(OperandKind.LiteralContextDependentNumber, value.toInt, (value >>> 32).toInt)
      else operand(OperandKind.LiteralContextDependentNumber, value.toInt)
    val module = Module(
      Header(ISA.version, Word(0), Word(5), Word(0)),
      Vec(
        Instruction.TypeInt(
          integer,
          operand(OperandKind.LiteralInteger, width),
          operand(OperandKind.LiteralInteger, 1)
        ),
        Instruction.SpecConstant(integer, a, literal(left)),
        Instruction.SpecConstant(integer, b, literal(right)),
        Instruction.SpecConstantOp(
          integer,
          out,
          operand(OperandKind.LiteralSpecConstantOpInteger, opcode),
          if (opcode == 126 || opcode == 200) Vec(a) else Vec(a, b)
        )
      ),
      Endianness.Little
    )
    val compiled = VMCompiler.compileUnchecked(module).fold(error => fail(error.message), identity)
    compiled.constantData(4) match {
      case VMIR.Constant.Bits(_, values) => values.head
      case other                         => fail(s"unexpected specialization result: $other")
    }
  }

  test("integer specialization arithmetic wraps to the declared width") {
    for (width <- Seq(8, 16, 32, 64)) {
      val mask = if (width == 64) -1L else (1L << width) - 1
      assertEquals(evaluate(width, 128, mask, 2), 1L)
      assertEquals(evaluate(width, 130, 0, 1), mask)
      assertEquals(evaluate(width, 132, mask, 2), mask - 1)
      assertEquals(evaluate(width, 126, 1, 0), mask)
      assertEquals(evaluate(width, 200, mask, 0), 0L)
      assertEquals(evaluate(width, 134, mask, 2), mask >>> 1)
      assertEquals(evaluate(width, 137, mask, 2), 1L)
      assertEquals(evaluate(width, 135, mask - 4, 2), mask - 1) // -5 / 2 == -2
      assertEquals(evaluate(width, 138, mask - 4, 2), mask)     // -5 % 2 == -1
      assertEquals(evaluate(width, 139, mask - 4, 2), 1L)
      assertEquals(evaluate(width, 139, 5, mask - 1), mask) // 5 mod -2 == -1
      assertEquals(evaluate(width, 194, mask, 1), mask >>> 1)
      assertEquals(evaluate(width, 195, mask, 1), mask)
      assertEquals(evaluate(width, 196, 1, width - 1), 1L << (width - 1))
      assertEquals(evaluate(width, 197, 5, 3), 7L)
      assertEquals(evaluate(width, 198, 5, 3), 6L)
      assertEquals(evaluate(width, 199, 5, 3), 1L)
    }
  }

  test("vector specialization computes each lane with width truncation") {
    val module = Module(
      Header(ISA.version, Word(0), Word(8), Word(0)),
      Vec(
        Instruction.TypeInt(
          operand(OperandKind.IdResult, 1),
          operand(OperandKind.LiteralInteger, 8),
          operand(OperandKind.LiteralInteger, 0)
        ),
        Instruction.TypeVector(
          operand(OperandKind.IdResult, 2),
          operand(OperandKind.IdRef, 1),
          operand(OperandKind.LiteralInteger, 2)
        ),
        Instruction.Constant(
          operand(OperandKind.IdRef, 1),
          operand(OperandKind.IdResult, 3),
          operand(OperandKind.LiteralContextDependentNumber, 255)
        ),
        Instruction.Constant(
          operand(OperandKind.IdRef, 1),
          operand(OperandKind.IdResult, 4),
          operand(OperandKind.LiteralContextDependentNumber, 2)
        ),
        Instruction.SpecConstantComposite(
          operand(OperandKind.IdRef, 2),
          operand(OperandKind.IdResult, 5),
          Vec(operand(OperandKind.IdRef, 3), operand(OperandKind.IdRef, 4))
        ),
        Instruction.SpecConstantComposite(
          operand(OperandKind.IdRef, 2),
          operand(OperandKind.IdResult, 6),
          Vec(operand(OperandKind.IdRef, 4), operand(OperandKind.IdRef, 4))
        ),
        Instruction.SpecConstantOp(
          operand(OperandKind.IdRef, 2),
          operand(OperandKind.IdResult, 7),
          operand(OperandKind.LiteralSpecConstantOpInteger, 128),
          Vec(operand(OperandKind.IdRef, 5), operand(OperandKind.IdRef, 6))
        )
      ),
      Endianness.Little
    )
    val compiled = VMCompiler.compileUnchecked(module).fold(error => fail(error.message), identity)
    assertEquals(compiled.constantData(7), VMIR.Constant.Bits(2, Vec(1L, 4L)))
  }
}
