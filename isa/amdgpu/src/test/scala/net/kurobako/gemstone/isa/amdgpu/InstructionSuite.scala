package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.asm.*
import net.kurobako.gemstone.isa.amdgpu.asm.Program.*
import net.kurobako.gemstone.string.*

class InstructionSuite extends munit.FunSuite {
  test("every generated form descriptor decodes directly to its parameterized enum case") {
    val opcodes = Opcode.definitions.map(_.opcode).distinct.sorted
    assertEquals(opcodes.size, 2467)
    Opcode.definitions.foreach { definition =>
      Instruction.operandCounts(definition).foreach { operandCount =>
        val operands = operandsFor(operandCount)
        val modifier = Modifier("test", Some("value"))
        val decoded  = Instruction.decode(definition, operands, Vec(modifier))
        assertEquals(decoded.size, 1, s"${definition.toString}: ambiguous or missing form")
        val instruction = decoded.head
        assertEquals(instruction.definition, definition)
        assertEquals(instruction.opcode, definition.opcode)
        assertEquals(instruction.meta, definition.meta)
        assertEquals(instruction.operands, operands)
        assertEquals(instruction.mods, Seq(modifier))
      }
    }
  }

  test("generated runtime class names are portable to case-insensitive filesystems") {
    val classNames = Opcode.definitions.flatMap { definition =>
      Instruction.operandCounts(definition).flatMap { operandCount =>
        Instruction.decode(
          definition,
          operandsFor(operandCount),
          Vec.empty[Modifier]
        ).map(_.getClass.getName.toLowerCaseRoot)
      }
    }
    assertEquals(classNames.distinct.size, classNames.size)
  }

  test("unknown opcodes and bad operand counts cannot inhabit the AST") {
    assertEquals(
      Instruction.decode("gemstone_unknown", Vec.empty),
      Left(Instruction.DecodeError.UnknownOpcode("gemstone_unknown"))
    )
    Instruction.decode("s_branch", Vec.empty) match {
      case Left(Instruction.DecodeError.InvalidOperandCount("s_branch", Vec(1), 0)) => ()
      case other => fail(s"expected an exact arity error, found $other")
    }
  }

  private def operandsFor(operandCount: Int): Vec[Operand] =
    Vec.tabulate(operandCount)(index => Operand.IntegerLiteral(BigInt(index + 1), 10))
}
