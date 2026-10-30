package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.catalogue.*
import net.kurobako.gemstone.isa.sass.Targets.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.testing.Suite
import net.kurobako.gemstone.testing.either.*

class AssemblerSuite extends Suite {
  private lazy val code = Listing.words(Fixture.listing("copyAddSm89"), "copy_add", 89).value.flatMap(_._2).toArray

  test("parsing creates generated cases and printing preserves typed operands") {
    val parsed = Parser.parse("@!P0 MOV R1, 0x7;").fold(fail(_), identity)
    assert(parsed.form match { case _: Form.Mov => true; case _ => false })
    assertEquals(Parser.parse(Printer.print(parsed).fold(fail(_), identity)), Right(parsed))
  }

  test("compiler-produced instructions re-encode and printed assembly assembles byte exactly") {
    codeWords(code, 89).fold(fail(_), identity).foreach { (address, bytes) =>
      val decoded = disassemble(bytes, 89, address).fold(fail(_), identity)
      assertEquals(Assembler.encode(decoded).fold(fail(_), _.toVec), bytes.toVec, clues(address, decoded))
      val text = Printer.print(decoded).fold(fail(_), identity)
      assertEquals(Assembler.assemble(text, target(89), address).fold(fail(_), _.toVec), bytes.toVec, clues(text))
    }
  }

  test("assembler constructs new machine instructions without a prior encoding") {
    val source  = "MOV R3, R7;"
    val encoded = Assembler.assemble(source, target(89)).fold(fail(_), identity)
    val decoded = disassemble(encoded, 89).fold(fail(_), identity)
    assertEquals(decoded.operation, Parser.parse(source).fold(fail(_), identity))
    assert(decoded.operation.form match { case _: Form.Mov => true; case _ => false })
  }

  Vec.nonEmpty(
    35 -> "MOV R3, R7;",
    35 -> "IADD R3, R7, R8;",
    50 -> "MOV R3, R7;",
    50 -> "IADD R3, R7, R8;",
    89 -> "NOP;",
    89 -> "EXIT;",
    89 -> "@P0 MOV R3, R7;",
    89 -> "IADD3 R3, R7, R8, RZ;",
    89 -> "FADD R3, R7, R8;",
    89 -> "IMAD R3, R7, R8, R9;",
    89 -> "LDG.E R2, [R2.64];",
    89 -> "STG.E [R4.64], R7;",
    89 -> "LDG.E R2, desc[UR4][R2.64];",
    89 -> "LDG.E R2, desc[UR4][R2.64+0x0];",
    89 -> "STG.E desc[UR4][R4.64+0x8], R7;"
  ).foreach { (sm, source) =>
    test(s"plain assembly: SM$sm $source") {
      val bytes   = Assembler.assemble(source, target(sm)).fold(error => fail(s"SM$sm $source: $error"), identity)
      val decoded = disassemble(bytes, sm).fold(fail(_), identity)
      assertEquals(Printer.print(decoded.operation), Printer.print(Parser.parse(source).fold(fail(_), identity)))
    }
  }

  test("ordinary signed integer and floating immediates preserve their numeric meaning") {
    Vec.nonEmpty(
      (35, "IADD R3, R7, -1;"),
      (50, "IADD R3, R7, -1;")
    ).foreach { (sm, source) =>
      val bytes   = Assembler.assemble(source, target(sm)).fold(error => fail(s"SM$sm $source: $error"), identity)
      val decoded = disassemble(bytes, sm).fold(fail(_), identity)
      assert(decoded.operands.last match {
        case Operand.Unary(Operand.Bits(value, _), true, false, false) => value == 1
        case _                                                         => false
      })
    }

    Vec.nonEmpty(
      "1.0"  -> BigInt("3f800000", 16),
      "-1.0" -> BigInt("bf800000", 16)
    ).foreach { (literal, expectedBits) =>
      for (sm <- Vec.nonEmpty(35, 50, 89)) {
        val floating = Assembler.assemble(s"FADD R3, R7, $literal;", target(sm)).fold(fail(_), identity)
        val decoded  = disassemble(floating, sm).fold(fail(_), identity)
        assert(decoded.operands.last match {
          case Operand.FloatingBits(FloatEncoding.F32Imm, width, bits) => width == 32 && bits == expectedBits
          case _                                                       => false
        })
      }
    }
  }

  test("double immediates encode their stored high bits and reject lost payload bits") {
    val bytes   = Assembler.assemble("DADD R2, R4, 1.0;", target(89)).fold(fail(_), identity)
    val decoded = disassemble(bytes, 89).fold(fail(_), identity)
    assert(decoded.operands.last match {
      case Operand.FloatingBits(FloatEncoding.F64Imm, width, bits) => width == 64 &&
        bits == BigInt("3ff0000000000000", 16)
      case _ => false
    })
    assert(Assembler.assemble("DADD R2, R4, bits<F64Imm>:64(0x3ff0000000000001);", target(89)).isLeft)
  }

  test("explicit always-true PT guard is equivalent to the unguarded binary form") {
    val bytes   = Assembler.assemble("@PT MOV R3, R7;", target(89)).fold(fail(_), identity)
    val decoded = disassemble(bytes, 89).fold(fail(_), identity)
    assertEquals(decoded.guard, None)
  }

  test("annotations cannot silently override edits or target and address context") {
    val decoded = disassemble(code.take(16), 89).fold(fail(_), identity)
    val printed = Printer.print(decoded).fold(fail(_), identity)
    assert(Assembler.assemble(printed, target(80)).isLeft)
    assert(Assembler.assemble(printed, target(89), 16).isLeft)
    val altered = printed match {
      case s"$_// encoding: $data" => s"NOP; // encoding: $data"
      case _                       => fail("missing annotation")
    }
    assert(Assembler.assemble(altered, target(89)).isLeft)
  }

  test("empty opcode qualifiers are rejected") {
    assert(Parser.parse("MOV. R1, R2;").isLeft)
    assert(Parser.parse("MOV..U32 R1, R2;").isLeft)
  }

  test("targets without recovered tables are rejected") {
    assert(Target.parse("SM999").isLeft)
    assert(Target.parse("sm_90a").isLeft)
  }
}
