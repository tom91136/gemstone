package net.kurobako.gemstone.isa.amdgpu.common

import net.kurobako.gemstone.isa.amdgpu.common.generated.Database

final class KnownInstructionSuite extends munit.FunSuite {
  test("every normalized opcode has a generated typed case and lossless syntax conversion") {
    val opcodes = Database.instructions.map(_.name).distinct.sorted

    assertEquals(opcodes.size, 2407)
    opcodes.foreach { opcode =>
      val syntax = Instruction(
        Opcode.unsafe(opcode),
        Vector(Operand.IntegerLiteral(BigInt(17), 10)),
        Vector(Modifier("test", Some("value")))
      )
      val known = KnownInstruction.fromSyntax(syntax).fold(error => fail(s"$opcode: $error"), identity)
      assertEquals(known.name, opcode)
      assertEquals(known.syntax, syntax)
    }
  }

  test("generated runtime class names are portable to case-insensitive filesystems") {
    val classNames = Database.instructions.map(_.name).distinct.map { opcode =>
      KnownInstruction.fromSyntax(Instruction(Opcode.unsafe(opcode))).toOption.get.getClass.getName.toLowerCase
    }
    assertEquals(classNames.distinct.size, classNames.size)
  }

  test("an unknown opcode is not coerced into a known instruction") {
    val syntax = Instruction(Opcode.unsafe("gemstone_unknown"))
    assertEquals(
      KnownInstruction.fromSyntax(syntax),
      Left(InstructionDecodeError.UnknownOpcode("gemstone_unknown"))
    )
  }
}
