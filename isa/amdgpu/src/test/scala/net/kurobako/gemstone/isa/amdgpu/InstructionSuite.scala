package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.amdgpu.asm.*
import net.kurobako.gemstone.string.*

class InstructionSuite extends munit.FunSuite {
  test("every opcode accepts exactly its operand counts") {
    assertEquals(Opcode.definitions.map(_.opcode).distinct.size, 2467)
    assertEquals(Opcode.definitions.map(Instruction.operandCounts(_).size).sum, 2952)
    Opcode.definitions.foreach { definition =>
      val counts = Instruction.operandCounts(definition)
      (0 to counts.max + 1).foreach { count =>
        val operands = Vec.tabulate(count)(index => Operand.IntegerLiteral(BigInt(index + 1), 10))
        val modifier = Modifier("test", Some("value"))
        Instruction.decode(definition, operands, Vec(modifier)) match {
          case Right(instruction) =>
            assert(counts.contains(count), definition.toString)
            assertEquals(instruction.definition, definition)
            assertEquals(instruction.operands, operands)
            assertEquals(instruction.mods, Vec(modifier))
          case Left(error) =>
            assert(!counts.contains(count), definition.toString)
            assertEquals(error, Instruction.DecodeError.InvalidOperandCount(definition.opcode, counts, count))
        }
      }
    }
  }

  test("opcode objects are portable to case-insensitive filesystems") {
    val classNames = Opcode.definitions.map(_.getClass.getName.toLowerCaseRoot)
    assertEquals(classNames.distinct.size, classNames.size)
  }

  test("unknown opcodes and bad operand counts are rejected by name") {
    assertEquals(
      Instruction.decode("gemstone_unknown", Vec.empty),
      Left(Instruction.DecodeError.UnknownOpcode("gemstone_unknown"))
    )
    assertEquals(
      Instruction.decode("s_branch", Vec.empty),
      Left(Instruction.DecodeError.InvalidOperandCount("s_branch", Vec(1), 0))
    )
    assertEquals(
      Instruction.decode("v_mov_b32_e64", Vec.empty).left.map(_.toString),
      Left(Instruction.DecodeError.InvalidOperandCount("v_mov_b32", Vec(2), 0).toString)
    )
  }
}
