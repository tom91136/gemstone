package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.amdgpu.asm.*
import net.kurobako.gemstone.string.*

class InstructionSuite extends munit.FunSuite {
  test("every opcode accepts exactly its operand counts") {
    assertEquals(InstructionDefs.definitions.map(_.mnemonic).distinct.size, 2467)
    assertEquals(InstructionDefs.definitions.map(Instruction.operandCounts(_).size).sum, 2952)
    InstructionDefs.definitions.foreach { definition =>
      val counts   = Instruction.operandCounts(definition)
      val accepted = counts.toSet
      (0 to counts.max + 1).foreach { count =>
        val operands = Vec.tabulate(count)(index => Operand.IntegerLiteral(BigInt(index + 1), 10))
        val modifier = Modifier("test", Some("value"))
        Instruction.decode(definition, operands, Vec(modifier)) match {
          case Right(instruction) =>
            assert(accepted(count), definition.toString)
            assertEquals(instruction.definition, definition)
            assertEquals(instruction.operands, operands)
            assertEquals(instruction.mods, Vec(modifier))
          case Left(error) =>
            assert(!accepted(count), definition.toString)
            assertEquals(
              error,
              DecodeError(
                definition.mnemonic,
                DecodeProblem.InvalidOperandCount(counts, count)
              )
            )
        }
      }
    }
  }

  test("opcode objects are portable to case-insensitive filesystems") {
    val classNames = InstructionDefs.definitions.map(_.getClass.getName.toLowerCaseRoot)
    assertEquals(classNames.distinct.size, classNames.size)
  }

  test("unknown opcodes and bad operand counts are rejected by name") {
    assertEquals(
      Instruction.decode("gemstone_unknown", Vec.empty),
      Left(DecodeError("gemstone_unknown", DecodeProblem.UnknownOpcode))
    )
    assertEquals(
      Instruction.decode("s_branch", Vec.empty),
      Left(DecodeError("s_branch", DecodeProblem.InvalidOperandCount(Vec(1), 0)))
    )
    assertEquals(
      Instruction.decode("v_mov_b32_e64", Vec.empty).left.map(_.toString),
      Left(DecodeError("v_mov_b32", DecodeProblem.InvalidOperandCount(Vec(2), 0)).toString)
    )
  }
}
