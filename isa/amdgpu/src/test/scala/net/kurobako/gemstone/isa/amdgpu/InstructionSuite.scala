package net.kurobako.gemstone.isa.amdgpu

import Program.*

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import net.kurobako.gemstone.Vec

final class InstructionSuite extends munit.FunSuite {
  test("every generated form descriptor decodes directly to its parameterized enum case") {
    val opcodes = Instruction.definitions.map(_.opcode).distinct.sortBy(_.value)
    assertEquals(opcodes.size, 2407)
    Instruction.definitions.foreach { definition =>
      definition.decoder.operandCounts.foreach { operandCount =>
        val operands = operandsFor(operandCount)
        val modifier = Modifier(ModifierName("test"), Some(ModifierValue("value")))
        val decoded  = definition.decoder(operands, Vec(modifier))
        assertEquals(decoded.size, 1, s"${definition.toString}: ambiguous or missing form")
        val instruction = decoded.head
        assertEquals(instruction.definition, definition)
        assertEquals(instruction.opcode, definition.opcode)
        assertEquals(instruction.meta, definition.meta)
        assertEquals(instruction.operands, operands)
        assertEquals(instruction.mods.toSeq, Seq(modifier))
      }
    }
  }

  test("generated runtime class names are portable to case-insensitive filesystems") {
    val classNames = Instruction.definitions.flatMap { definition =>
      definition.decoder.operandCounts.flatMap { operandCount =>
        definition.decoder(operandsFor(operandCount), Vec.empty).map(_.getClass.getName.toLowerCase)
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
