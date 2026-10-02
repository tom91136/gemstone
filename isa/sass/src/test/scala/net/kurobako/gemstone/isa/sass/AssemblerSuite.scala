package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*

class AssemblerSuite extends munit.FunSuite {
  private lazy val code = SASSOracleWords.read(
    CopyAddFixtures.copyAdd
  ).flatMap(_._2).toArray

  test("parsing creates generated cases and printing preserves typed operands") {
    val parsed = Parser.parse("@!P0 MOV R1, 0x7;").fold(fail(_), identity)
    assert(parsed match { case _: Instruction.Mov => true; case _ => false })
    assertEquals(Parser.parse(Printer.print(parsed).fold(fail(_), identity)), Right(parsed))
  }

  test("compiler-produced instructions re-encode and printed assembly assembles byte exactly") {
    SASSCodeWords.words(code, 89).fold(fail(_), identity).foreach { (address, bytes) =>
      val decoded = Disassembler.decode(bytes, "SM89", address).fold(fail(_), identity)
      assertEquals(Assembler.encode(decoded).fold(fail(_), _.toVec), bytes.toVec, clues(address, decoded))
      val text = Printer.print(decoded).fold(fail(_), identity)
      assertEquals(Assembler.assemble(text, "SM89", address).fold(fail(_), _.toVec), bytes.toVec, clues(text))
    }
  }

  test("assembler constructs new machine instructions without a prior encoding") {
    val source  = "MOV R3, R7;"
    val encoded = Assembler.assemble(source, "SM89").fold(fail(_), identity)
    val decoded = Disassembler.decode(encoded, "SM89").fold(fail(_), identity)
    assertEquals(decoded.operation, Parser.parse(source).fold(fail(_), identity))
    assert(decoded.operation match { case _: Instruction.Mov => true; case _ => false })
  }

  Vec(
    "SM35" -> "MOV R3, R7;",
    "SM35" -> "IADD R3, R7, R8;",
    "SM50" -> "MOV R3, R7;",
    "SM50" -> "IADD R3, R7, R8;",
    "SM89" -> "NOP;",
    "SM89" -> "EXIT;",
    "SM89" -> "@P0 MOV R3, R7;",
    "SM89" -> "IADD3 R3, R7, R8, RZ;",
    "SM89" -> "FADD R3, R7, R8;",
    "SM89" -> "IMAD R3, R7, R8, R9;"
  ).foreach { (target, source) =>
    test(s"plain assembly: $target $source") {
      val bytes   = Assembler.assemble(source, target).fold(error => fail(s"$target $source: $error"), identity)
      val decoded = Disassembler.decode(bytes, target).fold(fail(_), identity)
      assertEquals(Printer.print(decoded.operation), Printer.print(Parser.parse(source).fold(fail(_), identity)))
    }
  }

  test("ordinary signed integer and floating immediates preserve their numeric meaning") {
    Vec(
      ("SM35", "IADD R3, R7, -1;"),
      ("SM50", "IADD R3, R7, -1;")
    ).foreach { (target, source) =>
      val bytes   = Assembler.assemble(source, target).fold(error => fail(s"$target $source: $error"), identity)
      val decoded = Disassembler.decode(bytes, target).fold(fail(_), identity)
      assert(decoded.operands.last match {
        case Operand.Unary(Operand.Bits(value, _), true, false, false) => value == 1
        case _                                                         => false
      })
    }

    Vec(
      "1.0"  -> BigInt("3f800000", 16),
      "-1.0" -> BigInt("bf800000", 16)
    ).foreach { (literal, expectedBits) =>
      val floating = Assembler.assemble(s"FADD R3, R7, $literal;", "SM89").fold(fail(_), identity)
      val decoded  = Disassembler.decode(floating, "SM89").fold(fail(_), identity)
      assert(decoded.operands.last match {
        case Operand.FloatingBits(FloatEncoding.F32Imm, width, bits) => width.raw == 32 && bits == expectedBits
        case _                                                       => false
      })
    }
  }

  test("double immediates encode their stored high bits and reject lost payload bits") {
    val bytes   = Assembler.assemble("DADD R2, R4, 1.0;", "SM89").fold(fail(_), identity)
    val decoded = Disassembler.decode(bytes, "SM89").fold(fail(_), identity)
    assert(decoded.operands.last match {
      case Operand.FloatingBits(FloatEncoding.F64Imm, width, bits) => width.raw == 64 &&
        bits == BigInt("3ff0000000000000", 16)
      case _ => false
    })
    assert(Assembler.assemble("DADD R2, R4, bits<F64Imm>:64(0x3ff0000000000001);", "SM89").isLeft)
  }

  test("explicit always-true PT guard is equivalent to the unguarded binary form") {
    val bytes   = Assembler.assemble("@PT MOV R3, R7;", "SM89").fold(fail(_), identity)
    val decoded = Disassembler.decode(bytes, "SM89").fold(fail(_), identity)
    assertEquals(decoded.guard, None)
  }

  test("annotations cannot silently override edits or target and address context") {
    val decoded = Disassembler.decode(code.take(16), "SM89").fold(fail(_), identity)
    val printed = Printer.print(decoded).fold(fail(_), identity)
    assert(Assembler.assemble(printed, "SM80").isLeft)
    assert(Assembler.assemble(printed, "SM89", 16).isLeft)
    val altered = printed match {
      case s"$_// encoding: $data" => s"NOP; // encoding: $data"
      case _                       => fail("missing annotation")
    }
    assert(Assembler.assemble(altered, "SM89").isLeft)
  }

  test("empty opcode qualifiers are rejected") {
    assert(Parser.parse("MOV. R1, R2;").isLeft)
    assert(Parser.parse("MOV..U32 R1, R2;").isLeft)
  }

  test("negative addresses and unknown targets are rejected") {
    assert(Assembler.assemble("NOP;", "SM89", -1).isLeft)
    assert(Assembler.assemble("NOP;", "SM999").isLeft)
  }
}
