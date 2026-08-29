package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Col

final class InstructionSuite extends munit.FunSuite {
  test("every generated form descriptor decodes directly to its parameterized enum case") {
    val opcodes = Instruction.definitions.map(_.opcode).distinct.sorted
    assertEquals(opcodes.size, 2407)
    Instruction.definitions.foreach { definition =>
      definition.decoder.operandCounts.foreach { operandCount =>
        val operands = operandsFor(operandCount)
        val decoded  = definition.decoder(operands, Col(Modifier("test", Some("value"))))
        assertEquals(decoded.size, 1, s"${definition.toString}: ambiguous or missing form")
        val instruction = decoded.head
        assertEquals(instruction.definition, definition)
        assertEquals(instruction.opcode, definition.opcode)
        assertEquals(instruction.meta, definition.meta)
        assert(instruction.operands.sameElements(operands))
        assertEquals(instruction.mods.toSeq, Seq(Modifier("test", Some("value"))))
      }
    }
  }

  test("generated runtime class names are portable to case-insensitive filesystems") {
    val classNames = Instruction.definitions.flatMap { definition =>
      definition.decoder.operandCounts.flatMap { operandCount =>
        definition.decoder(operandsFor(operandCount), Col.empty).map(_.getClass.getName.toLowerCase)
      }
    }
    assertEquals(classNames.distinct.size, classNames.size)
  }

  test("unknown opcodes and bad operand counts cannot inhabit the AST") {
    assertEquals(
      Instruction.decode("gemstone_unknown", Col.empty),
      Left(InstructionDecodeError.UnknownOpcode("gemstone_unknown"))
    )
    Instruction.decode("s_branch", Col.empty) match {
      case Left(InstructionDecodeError.InvalidOperandCount("s_branch", Col(1), 0)) => ()
      case other => fail(s"expected an exact arity error, found $other")
    }
  }

  private def operandsFor(operandCount: Int): Col[Operand] =
    Col.tabulate(operandCount)(index => Operand.IntegerLiteral(BigInt(index + 1), 10))
}
