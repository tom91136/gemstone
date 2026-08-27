package net.kurobako.gemstone.isa.amdgpu.common

final class InstructionSuite extends munit.FunSuite {
  test("every generated form descriptor decodes directly to its parameterized enum case") {
    val opcodes = Instructions.map(_.opcode).distinct.sorted
    assertEquals(opcodes.size, 2407)
    Instructions.foreach { definition =>
      definition.decoder.operandCounts.foreach { operandCount =>
        val operands = operandsFor(operandCount)
        val decoded  = definition.decoder(operands, Vector(Modifier("test", Some("value"))))
        assertEquals(decoded.size, 1, s"${definition.toString}: ambiguous or missing form")
        val instruction = decoded.head
        assertEquals(instruction.definition, definition)
        assertEquals(instruction.opcode, definition.opcode)
        assertEquals(instruction.decoder, definition.decoder)
        assertEquals(instruction.meta, definition.meta)
        assertEquals(instruction.operands, operands)
        assertEquals(instruction.mods, Vector(Modifier("test", Some("value"))))
      }
    }
  }

  test("generated runtime class names are portable to case-insensitive filesystems") {
    val classNames = Instructions.flatMap { definition =>
      definition.decoder.operandCounts.flatMap { operandCount =>
        definition.decoder(operandsFor(operandCount), Vector.empty).map(_.getClass.getName.toLowerCase)
      }
    }
    assertEquals(classNames.distinct.size, classNames.size)
  }

  test("unknown opcodes and bad operand counts cannot inhabit the AST") {
    assertEquals(
      Instruction.decode("gemstone_unknown", Vector.empty),
      Left(InstructionDecodeError.UnknownOpcode("gemstone_unknown"))
    )
    Instruction.decode("s_branch", Vector.empty) match {
      case Left(InstructionDecodeError.InvalidOperandCount("s_branch", Vector(1), 0)) => ()
      case other => fail(s"expected an exact arity error, found $other")
    }
  }

  private def operandsFor(operandCount: Int): Vector[Operand] =
    Vector.tabulate(operandCount)(index => Operand.IntegerLiteral(BigInt(index + 1), 10))
}
