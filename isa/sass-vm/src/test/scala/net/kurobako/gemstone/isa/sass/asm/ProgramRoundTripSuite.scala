package net.kurobako.gemstone.isa.sass.asm

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.testing.Suite
import net.kurobako.gemstone.text.*

class ProgramRoundTripSuite extends Suite {
  private def target(sm: Int): Target         = Target.parse(s"SM$sm").fold(fail(_), identity(_))
  private def parsed(source: String): Program =
    Parser.parseProgram(source).fold(error => fail(error.message), identity(_))
  private def assembled(program: Program, sm: Int): Vec[Byte] =
    Assembler(target(sm)).encode(program).fold(error => fail(error.message), identity(_))

  private val sms = Vec(35, 50, 89)

  test("relaying an edited program moves preserved encodings and the branch targets that name them") {
    val code    = Fixture.copyAddCubin
    val module  = Cubin.decode(code).fold(fail(_), identity(_))
    val text    = module.sections(module.kernels.head.section).bytes
    val program = Disassembler(target(89)).decode(text).fold(error => fail(error.message), identity(_))
    val extra   = Parser.parse("IADD3 R7, R7, 0x1, RZ").fold(fail(_), identity(_))
    val edited  = program.copy(statements = program.statements.patch(1, Vec(Program.Statement.Operation(extra)), 0))
    assert(Assembler(target(89)).encode(edited).isLeft)
    val relaid  = Assembler(target(89)).relaid(edited).fold(error => fail(error.message), identity(_))
    val encoded = Assembler(target(89)).encode(relaid).fold(error => fail(error.message), identity(_))
    assertEquals(encoded.size, text.size + 16)
    val branches = CodeSection.decode(Vec.toArray(encoded), target(89)).fold(fail(_), identity(_)).instructions
      .filter(_.mnemonic.raw == "BRA")
    assertEquals(
      branches.map(branch => branch.operands.collect { case Operand.Bits(value, _) => value }),
      Vec(Vec(BigInt(0xb0)))
    )
    assertEquals(branches.map(_.address.raw), Vec(0xb0))
  }

  test("relaying re-encodes a branch whose target moved further than the branch did") {
    val code    = assembled(parsed(".target SM89\nBRA 0x20;\nNOP;\nEXIT;\n"), 89)
    val program = Disassembler(target(89)).decode(code).fold(error => fail(error.message), identity(_))
    val extra   = Parser.parse("NOP").fold(fail(_), identity(_))
    val edited  = program.copy(statements = program.statements.patch(1, Vec(Program.Statement.Operation(extra)), 0))
    val encoded = Assembler(target(89)).relaid(edited).flatMap(Assembler(target(89)).encode(_))
      .fold(error => fail(error.message), identity(_))
    val branch = CodeSection.decode(Vec.toArray(encoded), target(89)).fold(fail(_), identity(_)).instructions.head
    assertEquals(branch.operands.collect { case Operand.Bits(value, _) => value }, Vec(BigInt(0x30)))
  }

  sms.foreach { sm =>
    test(s"SM$sm bytes survive program and text round trips, including legacy controls") {
      val original = Fixture.saxpy(sm)
      // Set the otherwise unused Maxwell high bit independently of the instruction encodings.
      val bytes   = if (sm == 50) original.updated(7, (original(7) | 0x80).toByte) else original
      val program = Disassembler(target(sm)).decode(bytes).fold(error => fail(error.message), identity(_))
      val text    = Printer.print(program).fold(error => fail(error.message), identity(_))
      assertEquals(assembled(parsed(text), sm), bytes)
      if (sm < 70) assert(text.contains(".bundle "))
      assert(text.contains("// encoding:"))
    }

    test(s"SM$sm rejects a truncated section") {
      val bytes = Fixture.saxpy(sm).dropRight(1)
      assert(Disassembler(target(sm)).decode(bytes).isLeft)
    }

    test(s"SM$sm labels cross bundle boundaries using physical instruction addresses") {
      val count  = if (sm == 35) 7 else if (sm == 50) 3 else 1
      val source = "entry: BRA destination;\n" + Vec.fill(count - 1)("MOV R3, R7;\n").mkString +
        "destination: BRA entry;\n"
      val program = parsed(source)
      val printed = Printer.print(program).fold(error => fail(error.message), identity(_))
      assert(printed.contains("BRA destination;"))
      val bytes                             = assembled(parsed(printed), sm)
      val section                           = CodeSection.decode(bytes.toArray, target(sm)).fold(fail(_), identity(_))
      val expected                          = if (sm == 35) 72 else if (sm == 50) 40 else 16
      def integer(operand: Operand): BigInt = operand match {
        case Operand.Integer(value) => value
        case Operand.Bits(value, _) => value
        case other                  => fail(s"expected numeric branch destination, got $other")
      }
      assertEquals(integer(section.instructions.head.operands.last), BigInt(expected))
      assertEquals(integer(section.instructions(count).operands.last), BigInt(if (sm < 70) 8 else 0))
      assertEquals(bytes.size, if (sm == 35) 128 else if (sm == 50) 64 else 32)
    }
  }

  test("duplicate and undefined labels are typed errors") {
    assert(Parser.parseProgram("again: NOP;\nagain: NOP;").left.exists {
      case Error.DuplicateLabel(name) => name.raw == "again"
      case _                          => false
    })
    assert(Assembler(target(89)).encode(parsed("BRA missing;")).left.exists {
      case Error.UndefinedLabel(name) => name.raw == "missing"
      case _                          => false
    })
  }

  test("target directives and retained annotations cannot be assembled for another target") {
    val program = parsed(".target SM89\nMOV R3, R7;")
    assert(Assembler(target(50)).encode(program).left.exists {
      case Error.TargetMismatch(_, _) => true
      case _                          => false
    })
    val bytes   = Fixture.saxpy(89)
    val decoded = Disassembler(target(89)).decode(bytes).fold(error => fail(error.message), identity(_))
    assert(Assembler(target(50)).encode(decoded.copy(target = None)).isLeft)
  }

  test("explicit controls reject incomplete bundles, invalid widths and the wrong target") {
    assert(Assembler(target(35)).encode(parsed(".bundle kepler 0000000000000000\nMOV R3, R7;")).isLeft)
    assert(Parser.parseProgram(".bundle kepler 00").isLeft)
    assert(Parser.parseProgram(".bundle maxwell 0 200000 000000 000000").isLeft)
    assert(Parser.parseProgram(".bundle maxwell 2 000000 000000 000000").isLeft)
    assert(Assembler(target(89)).encode(parsed(".bundle kepler 0000000000000000\nMOV R3, R7;")).isLeft)
    assert(Assembler(target(50)).encode(parsed(".bundle maxwell 0 000000 000000 000000")).isLeft)
    assert(Assembler(target(50)).encode(parsed("MOV R3, R7;\n.bundle maxwell 0 000000 000000 000000")).isLeft)
    assert(Assembler(
      target(50)
    ).encode(parsed(".bundle maxwell 0 000000 000000 000000\n.bundle maxwell 0 000000 000000 000000")).isLeft)
  }

  test("fresh instructions honour explicit Kepler bytes and Maxwell slot controls") {
    Vec(
      (35, ".bundle kepler 0123456789abcdef", 7),
      (50, ".bundle maxwell 1 0007f6 0007f1 0007f2", 3)
    ).foreach { (sm, directive, slots) =>
      val program   = parsed(directive + "\n" + Vec.fill(slots)("MOV R3, R7;").mkString("\n"))
      val bytes     = assembled(program, sm)
      val section   = CodeWords.split(bytes.toArray, target(sm).generation).fold(fail(_), identity(_))
      val requested = program.statements.collectFirst { case Program.Statement.Bundle(control) => control }
      assertEquals(Some(section.bundles.head.control), requested)
      val decoded = Disassembler(target(sm)).decode(bytes).fold(error => fail(error.message), identity(_))
      val text    = Printer.print(decoded).fold(error => fail(error.message), identity(_))
      assertEquals(assembled(parsed(text), sm), bytes)
    }
  }

  test("explicit Maxwell controls must satisfy each instruction's fixed scheduling fields") {
    // MOV requires both variable-latency barrier selectors to be seven and the coupled bit to be zero.
    Vec("000001", "012345", "1fffff").foreach { invalid =>
      val source = s".bundle maxwell 0 $invalid 0007f1 0007f2\n" +
        Vec.fill(3)("MOV R3, R7;").mkString("\n")
      assert(Assembler(target(50)).encode(parsed(source)).isLeft)
    }
  }

  test("Maxwell control validation compares canonical immediates and guards") {
    val source = ".bundle maxwell 0 0007f6 0007f1 0007f2\n" +
      "IADD R3, R7, -1;\n@PT MOV R3, R7;\nMOV R3, R7;"
    val bytes   = assembled(parsed(source), 50)
    val decoded = Disassembler(target(50)).decode(bytes).fold(error => fail(error.message), identity(_))
    val printed = Printer.print(decoded).fold(error => fail(error.message), identity(_))
    assertEquals(assembled(parsed(printed), 50), bytes)
  }

  test("guarded symbolic destinations print and resolve without changing the guard") {
    val program = parsed("@!P0 BRA .done; MOV R3, R7;\n.done: EXIT;")
    val text    = Printer.print(program).fold(error => fail(error.message), identity(_))
    assert(text.contains("@!P0 BRA .done;"))
    val decoded  = CodeSection.decode(assembled(parsed(text), 89).toArray, target(89)).fold(fail(_), identity(_))
    val expected = Parser.parse("@!P0 BRA 0x20;").fold(fail(_), identity(_))
    assertEquals(Printer.print(decoded.instructions.head.operation), Printer.print(expected))
  }

  test("architectural symbols remain ordinary operands") {
    val program    = parsed("P2R R0, PR;")
    val operations = program.statements.collect { case operation: Program.Statement.Operation => operation }
    assertEquals(operations.size, 1)
    assertEquals(operations.head.reference, None)
    assertEquals(operations.head.instruction.operands.last, Operand.Symbol("PR"))
    assertEquals(Printer.print(program), Right("P2R R0, PR;\n"))
  }

  test("retained annotations reject operation edits and instruction relocation") {
    val bytes       = Fixture.saxpy(89)
    val program     = Disassembler(target(89)).decode(bytes).fold(error => fail(error.message), identity(_))
    val replacement = Parser.parse("NOP;").fold(fail(_), identity(_))
    val edited      = program.copy(statements = program.statements.map {
      case operation: Program.Statement.Operation => operation.copy(instruction = replacement)
      case statement                              => statement
    })
    assert(Assembler(target(89)).encode(edited).isLeft)
    assert(Printer.print(edited).isLeft)
    val shifted = program.copy(statements = Program.Statement.Operation(replacement) +: program.statements)
    assert(Assembler(target(89)).encode(shifted).isLeft)
  }

  test("an edited Maxwell bundle cannot silently contradict retained instruction controls") {
    val bytes   = Fixture.saxpy(50)
    val program = Disassembler(target(50)).decode(bytes).fold(error => fail(error.message), identity(_))
    val changed = program.statements.map {
      case Program.Statement.Bundle(CodeWords.BundleControl.Maxwell(slots, reserved)) =>
        val first = (slots.head ^ 1).unsafe[CodeWords.ControlBits]
        Program.Statement.Bundle(CodeWords.BundleControl.Maxwell(slots.updated(0, first), reserved))
      case other => other
    }
    assert(Assembler(target(50)).encode(program.copy(statements = changed)).isLeft)
  }

  test("unsupported directives and misplaced encoding annotations are rejected") {
    assert(Parser.parseProgram(".section .text").isLeft)
    assert(Parser.parseProgram("// encoding: {}").isLeft)
    assert(Parser.parseProgram(".target SM89\n.target SM89").isLeft)
    assertEquals(Printer.print(parsed("// ordinary comment\n")), Right(""))
  }

  test("syntax errors locate the failing statement within its line") {
    def position(source: String) = Parser.parseProgram(source).left.toOption.collect {
      case Error.Syntax(error) => error.position
    }
    assertEquals(position("NOP;\n  NOP; BOGUS R0;"), Some(SourcePosition(2, 8)))
    assertEquals(position("start: NOP;\nhere:  BOGUS;"), Some(SourcePosition(2, 8)))
    assertEquals(position("NOP;\n// encoding: {}"), Some(SourcePosition(2, 1)))
  }
}
