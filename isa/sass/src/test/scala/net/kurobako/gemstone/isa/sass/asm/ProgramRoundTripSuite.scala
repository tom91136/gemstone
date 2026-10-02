package net.kurobako.gemstone.isa.sass.asm

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.*
import net.kurobako.gemstone.isa.sass.fixtures.*

class ProgramRoundTripSuite extends munit.FunSuite {
  private def target(sm: Int): Target         = Target.parse(s"SM$sm").fold(fail(_), identity(_))
  private def parsed(source: String): Program =
    Parser.decode(source).fold(error => fail(error.message), identity(_))
  private def assembled(program: Program, sm: Int): Vec[Byte] =
    Assembler(target(sm)).encode(program).fold(error => fail(error.message), identity(_))

  private val fixtures = Vec(
    35 -> (
      "c01080109cb0a00806009c08003cc06402001c29003cc06426009c29003cc064" +
        "0a001c28003cc0640e009c28003cc064fe031c2a00008c6012249c2a005c985a" +
        "10808080acb8b80816249c2a005c905a10081c00000080e41a249c2a005cb85a" +
        "1e249c2a005cc85a26249c2a005ce85a14081c02000080e418081c04000080e4"
    ),
    50 -> (
      "f60720e200fc1c00010087008007984c000057020000c8f0020017020000c8f0" +
        "f10fc2fe42d81f0003002700807f104f020027000001004e000037001801305b"
    ),
    89 -> "247601ff000a0000ff008e0700e40f0019790200000000000021000000240e00"
  )

  fixtures.foreach { (sm, fixture) =>
    test(s"SM$sm bytes survive program and text round trips, including legacy controls") {
      val original = FixtureBytes.readHex(fixture).toVec
      // Set the otherwise unused Maxwell high bit independently of the instruction encodings.
      val bytes   = if (sm == 50) original.updated(7, (original(7) | 0x80).toByte) else original
      val program = Disassembler(target(sm)).decode(bytes).fold(error => fail(error.message), identity(_))
      val text    = Printer.encode(program).fold(error => fail(error.message), identity(_))
      assertEquals(assembled(parsed(text), sm), bytes)
      if (sm < 70) assert(text.contains(".bundle "))
      assert(text.contains("// encoding:"))
    }

    test(s"SM$sm rejects a truncated section") {
      val bytes = FixtureBytes.readHex(fixture).toVec.dropRight(1)
      assert(Disassembler(target(sm)).decode(bytes).isLeft)
    }

    test(s"SM$sm labels cross bundle boundaries using physical instruction addresses") {
      val count  = if (sm == 35) 7 else if (sm == 50) 3 else 1
      val source = "entry: BRA destination;\n" + Vec.fill(count - 1)("MOV R3, R7;\n").mkString +
        "destination: BRA entry;\n"
      val program = parsed(source)
      val printed = Printer.encode(program).fold(error => fail(error.message), identity(_))
      assert(printed.contains("BRA destination;"))
      val bytes                             = assembled(parsed(printed), sm)
      val section                           = CodeSection.decode(bytes.toArray, sm).fold(fail(_), identity(_))
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
    assert(Parser.decode("again: NOP;\nagain: NOP;").left.exists {
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
      case _: Error.TargetMismatch => true
      case _                       => false
    })
    val bytes   = FixtureBytes.readHex(fixtures.last._2).toVec
    val decoded = Disassembler(target(89)).decode(bytes).fold(error => fail(error.message), identity(_))
    assert(Assembler(target(50)).encode(decoded.copy(target = None)).isLeft)
  }

  test("explicit controls reject incomplete bundles, invalid widths and the wrong target") {
    assert(Assembler(target(35)).encode(parsed(".bundle kepler 0000000000000000\nMOV R3, R7;")).isLeft)
    assert(Parser.decode(".bundle kepler 00").isLeft)
    assert(Parser.decode(".bundle maxwell 0 200000 000000 000000").isLeft)
    assert(Parser.decode(".bundle maxwell 2 000000 000000 000000").isLeft)
    assert(Assembler(target(89)).encode(parsed(".bundle kepler 0000000000000000\nMOV R3, R7;")).isLeft)
    assert(Assembler(target(50)).encode(parsed(".bundle maxwell 0 000000 000000 000000")).isLeft)
    assert(Assembler(target(50)).encode(parsed("MOV R3, R7;\n.bundle maxwell 0 000000 000000 000000")).isLeft)
    assert(Assembler(
      target(50)
    ).encode(parsed(".bundle maxwell 0 000000 000000 000000\n.bundle maxwell 0 000000 000000 000000")).isLeft)
  }

  test("fresh instructions honor explicit Kepler bytes and Maxwell slot controls") {
    Vec(
      (35, ".bundle kepler 0123456789abcdef", 7),
      (50, ".bundle maxwell 1 0007f6 0007f1 0007f2", 3)
    ).foreach { (sm, directive, slots) =>
      val program   = parsed(directive + "\n" + Vec.fill(slots)("MOV R3, R7;").mkString("\n"))
      val bytes     = assembled(program, sm)
      val section   = SASSCodeWords.split(bytes.toArray, sm).fold(fail(_), identity(_))
      val requested = program.statements.collectFirst { case Program.Statement.Bundle(control) => control }
      assertEquals(Some(section.bundles.head.control), requested)
      val decoded = Disassembler(target(sm)).decode(bytes).fold(error => fail(error.message), identity(_))
      val text    = Printer.encode(decoded).fold(error => fail(error.message), identity(_))
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
    val printed = Printer.encode(decoded).fold(error => fail(error.message), identity(_))
    assertEquals(assembled(parsed(printed), 50), bytes)
  }

  test("guarded symbolic destinations print and resolve without changing the guard") {
    val program = parsed("@!P0 BRA .done; MOV R3, R7;\n.done: EXIT;")
    val text    = Printer.encode(program).fold(error => fail(error.message), identity(_))
    assert(text.contains("@!P0 BRA .done;"))
    val decoded  = CodeSection.decode(assembled(parsed(text), 89).toArray, 89).fold(fail(_), identity(_))
    val expected = Parser.parse("@!P0 BRA 0x20;").fold(fail(_), identity(_))
    assertEquals(Printer.print(decoded.instructions.head.operation), Printer.print(expected))
  }

  test("architectural symbols remain ordinary operands") {
    val program    = parsed("P2R R0, PR;")
    val operations = program.statements.collect { case operation: Program.Statement.Operation => operation }
    assertEquals(operations.size, 1)
    assertEquals(operations.head.references, Vec.empty[Program.LabelReference])
    assertEquals(operations.head.instruction.operands.last, Operand.Symbol("PR"))
    assertEquals(Printer.encode(program), Right("P2R R0, PR;\n"))
  }

  test("duplicate typed references cannot silently overwrite a destination") {
    val program    = parsed("loop: BRA loop;")
    val duplicated = program.copy(statements = program.statements.map {
      case operation: Program.Statement.Operation =>
        operation.copy(references = operation.references ++ operation.references)
      case statement => statement
    })
    assert(Assembler(target(89)).encode(duplicated).isLeft)
    assert(Printer.encode(duplicated).isLeft)
  }

  test("retained annotations reject operation edits and instruction relocation") {
    val bytes       = FixtureBytes.readHex(fixtures.last._2).toVec
    val program     = Disassembler(target(89)).decode(bytes).fold(error => fail(error.message), identity(_))
    val replacement = Parser.parse("NOP;").fold(fail(_), identity(_))
    val edited      = program.copy(statements = program.statements.map {
      case operation: Program.Statement.Operation => operation.copy(instruction = replacement)
      case statement                              => statement
    })
    assert(Assembler(target(89)).encode(edited).isLeft)
    assert(Printer.encode(edited).isLeft)
    val shifted = program.copy(statements = Program.Statement.Operation(replacement) +: program.statements)
    assert(Assembler(target(89)).encode(shifted).isLeft)
  }

  test("an edited Maxwell bundle cannot silently contradict retained instruction controls") {
    val bytes   = FixtureBytes.readHex(fixtures(1)._2).toVec
    val program = Disassembler(target(50)).decode(bytes).fold(error => fail(error.message), identity(_))
    val changed = program.statements.map {
      case Program.Statement.Bundle(SASSCodeWords.BundleControl.Maxwell(slots, reserved)) =>
        val first = SASSCodeWords.ControlWord((slots.head.value.raw ^ 1).unsafe[SASSCodeWords.ControlBits])
        Program.Statement.Bundle(SASSCodeWords.BundleControl.Maxwell(slots.updated(0, first), reserved))
      case other => other
    }
    assert(Assembler(target(50)).encode(program.copy(statements = changed)).isLeft)
  }

  test("unsupported directives and misplaced encoding annotations are rejected") {
    assert(Parser.decode(".section .text").isLeft)
    assert(Parser.decode("// encoding: {}").isLeft)
    assert(Parser.decode(".target SM89\n.target SM89").isLeft)
    assertEquals(Printer.encode(parsed("// ordinary comment\n")), Right(""))
  }
}
