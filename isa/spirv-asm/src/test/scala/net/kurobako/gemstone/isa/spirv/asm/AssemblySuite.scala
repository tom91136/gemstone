package net.kurobako.gemstone.isa.spirv.asm

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.spirv.*

class AssemblySuite extends munit.FunSuite {
  private def parse(source: String): Module     = Parser.parse(source).fold(error => fail(error.toString), identity)
  private def encode(module: Module): Vec[Byte] = ISA.encode(module).fold(error => fail(error.toString), identity)
  private def roundTrip(module: Module): Unit   = {
    val source = Printer.render(module)
    assertEquals(parse(source), module, source)
    assertEquals(encode(parse(source)), encode(module), source)
    assertEquals(
      ISA.decode(encode(module)).map(Printer.render(_)).map(parse(_)),
      Right(module.copy(comments = Vec.empty))
    )
  }

  test("every generated opcode survives binary, typed AST, and printed assembly in both endiannesses") {
    Instruction.definitions.foreach { definition =>
      def fixture(kind: OperandKind): Vec[Word] =
        if (kind.category == OperandKind.Category.Id) Vec(Word(1))
        else if (kind == OperandKind.LiteralString) Vec(Word(0))
        else if (kind.category == OperandKind.Category.Composite) Vec(Word(1), Word(1))
        else Instruction.operandKinds.get(kind) match {
          case Some(info) if info.enumerants.nonEmpty =>
            val selected = info.enumerants.minBy(value => (value.parameterKinds.size, value.value))
            Vec(Word(selected.value.toInt)) ++ selected.parameterKinds.flatMap(fixture(_))
          case _ => Vec(Word(0))
        }
      val words = definition match {
        case Instruction.Definition.TypeInt   => Vec(Word(1), Word(32), Word(0))
        case Instruction.Definition.TypeFloat => Vec(Word(1), Word(32))
        case _                                =>
          Vec.from(definition.shapes.view.filter(_.quantifier == OperandQuantifier.One).flatMap(shape =>
            fixture(shape.kind)
          ))
      }
      val raw         = RawInstruction(definition.opcodeNumber, words)
      val instruction = Instruction.decode(raw).fold(error => fail(s"${definition.opcode}: $error"), identity)
      Endianness.values.foreach { order =>
        val module   = Module(Header(ISA.version, Word(0), Word(2), Word(0)), Vec(instruction), order)
        val bytes    = encode(module)
        val decoded  = ISA.decode(bytes).fold(error => fail(s"${definition.opcode}: $error"), identity)
        val source   = Printer.render(decoded)
        val reparsed = Parser.parse(source).fold(error => fail(s"${definition.opcode}: $error\n$source"), identity)
        assertEquals(reparsed, module, definition.opcode)
        assertEquals(encode(reparsed), bytes, definition.opcode)
      }
    }
  }

  test("source comments stay attached to instruction boundaries but do not alter binary semantics") {
    val module =
      parse("; module note\nOpCapability Shader ; capability note\n; next\nOpMemoryModel Logical GLSL450\n; tail")
    assertEquals(
      module.comments,
      Vec(
        Module.Comment(0, "module note"),
        Module.Comment(1, "capability note"),
        Module.Comment(1, "next"),
        Module.Comment(2, "tail")
      )
    )
    roundTrip(module)
    assertEquals(encode(module), encode(module.copy(comments = Vec.empty)))
  }

  test("standard assembly has independently specified instruction words") {
    val module = parse("""OpCapability Shader
      |OpMemoryModel Logical GLSL450
      |OpEntryPoint GLCompute %3 "main"
      |OpExecutionMode %3 LocalSize 1 2 3
      |%1 = OpTypeVoid
      |%2 = OpTypeFunction %1
      |%3 = OpFunction %1 None %2
      |%4 = OpLabel
      |OpReturn
      |OpFunctionEnd""".stripMargin)
    assertEquals(
      module.instructions.map(_.definition),
      Vec(
        Instruction.Definition.Capability,
        Instruction.Definition.MemoryModel,
        Instruction.Definition.EntryPoint,
        Instruction.Definition.ExecutionMode,
        Instruction.Definition.TypeVoid,
        Instruction.Definition.TypeFunction,
        Instruction.Definition.Function,
        Instruction.Definition.Label,
        Instruction.Definition.Return,
        Instruction.Definition.FunctionEnd
      )
    )
    module.instructions.head match {
      case Instruction.Capability(capability) => assertEquals(capability.words, Vec(Word(Capability.Shader)))
      case other                              => fail(s"expected typed capability instruction, found $other")
    }
    // SPIR-V words from the specification, independent of generated opcode metadata.
    val expected = Vec(
      0x00020011, 1, 0x0003000e, 0, 1, 0x0005000f, 5, 3, 0x6e69616d, 0,
      0x00060010, 3, 17, 1, 2, 3, 0x00020013, 1, 0x00030021, 2, 1,
      0x00050036, 1, 3, 0, 2, 0x000200f8, 4, 0x000100fd, 0x00010038
    )
    val actual = module.instructions.flatMap { instruction =>
      val raw = Instruction.encode(instruction)
      Vec((raw.wordCount.raw << 16) | raw.opcodeNumber.toInt) ++ raw.operandWords.map(_.raw)
    }
    assertEquals(actual, expected)
    assertEquals(module.header.bound, Word(5))
    roundTrip(module)
  }

  test("symbolic and numeric IDs coexist without collisions, including forward references") {
    val module = parse("""OpName %later "value"
      |%1 = OpTypeInt 32 0
      |%later = OpConstant %1 42""".stripMargin)
    assertEquals(module.instructions(0).operands.head.words, Vec(Word(2)))
    assertEquals(module.instructions(2).operands(1).words, Vec(Word(2)))
    roundTrip(module)
  }

  test("masks carry ordered parameters, including repeated and optional operands") {
    val module = parse("""%1 = OpTypeInt 32 0
      |%2 = OpTypePointer Function %1
      |%3 = OpVariable %2 Function
      |%4 = OpLoad %1 %3 Volatile|Aligned 16
      |OpStore %3 %4 Aligned|Nontemporal 8
      |OpDecorate %3 LinkageAttributes "symbol" Export""".stripMargin)
    assertEquals(
      module.instructions(3).operands.last.words,
      Vec(Word(MemoryAccess.Volatile | MemoryAccess.Aligned), Word(16))
    )
    assertEquals(
      module.instructions(4).operands.last.words,
      Vec(Word(MemoryAccess.Aligned | MemoryAccess.Nontemporal), Word(8))
    )
    roundTrip(module)
  }

  test("strings preserve escapes, UTF-8, whitespace, comments and instruction-looking text") {
    val text    = "quote \" slash \\ ; OpReturn\nλ"
    val escaped = text.replace("\\", "\\\\").replace("\"", "\\\"")
    val module  = parse(s"; ignored\n%1=OpString \"$escaped\" ; comment\nOpNop OpNop")
    assertEquals(LiteralString.decode(module.instructions.head.operands(1).words), text)
    assertEquals(module.instructions.size, 3)
    roundTrip(module)
  }

  test("headers, byte order and context-dependent words survive exact binary round trips") {
    val module = parse("""%1 = OpTypeFloat 32
      |%2 = OpConstant %1 !0xff800123
      |%3 = OpConstant %1 -0.0
      |%4 = OpTypeInt 64 0
      |%5 = OpConstant %4 18446744073709551615
      |%6 = OpTypeInt 16 1
      |%7 = OpConstant %6 0xffff""".stripMargin)
    assertEquals(module.instructions(1).operands.last.words, Vec(Word(0xff800123)))
    assertEquals(module.instructions(2).operands.last.words, Vec(Word(Int.MinValue)))
    assertEquals(module.instructions(4).operands.last.words, Vec(Word(-1), Word(-1)))
    assertEquals(module.instructions(6).operands.last.words, Vec(Word(-1)))
    Seq(Endianness.Little, Endianness.Big).foreach { order =>
      roundTrip(module.copy(header = Header(Version(1, 3), Word(0xdeadbeef), Word(128), Word(0)), endianness = order))
    }
  }

  test("extended instruction names and specialization opcodes use generated tables") {
    val module = parse("""%1 = OpExtInstImport "GLSL.std.450"
      |%2 = OpTypeFloat 32
      |%3 = OpConstant %2 1.5
      |%4 = OpExtInst %2 %1 Sqrt %3
      |%5 = OpTypeInt 32 0
      |%6 = OpSpecConstant %5 3
      |%7 = OpSpecConstantOp %5 IAdd %6 %6""".stripMargin)
    assertEquals(module.instructions(3).operands(3).words, Vec(Word(ExtendedInstruction.GlslStd450.Sqrt)))
    assert(Printer.render(module).contains("Sqrt"))
    assertEquals(module.instructions.last.operands(2).words, Vec(Word(Instruction.Definition.IAdd.opcodeNumber.toInt)))
    roundTrip(module)
  }

  test("64-bit switches preserve all case words even when the generic operand split is ambiguous") {
    val module = parse("""%1 = OpTypeInt 64 0
      |%2 = OpConstant %1 4294967297
      |OpSwitch %2 %3 4294967297 %4 8589934594 %5""".stripMargin)
    assertEquals(Instruction.encode(module.instructions.last).operandWords, Vec(2, 3, 1, 1, 4, 2, 2, 5).map(Word(_)))
    roundTrip(module)
  }

  test("hexadecimal NaNs and infinities retain payloads for all scalar floating widths") {
    val module = parse("""%1 = OpTypeFloat 16
      |%2 = OpConstant %1 -0x1.8p+16
      |%3 = OpTypeFloat 32
      |%4 = OpConstant %3 -0x1.0002p+128
      |%5 = OpTypeFloat 64
      |%6 = OpConstant %5 0x1.8p+1024
      |%7 = OpConstant %3 0x1p+128""".stripMargin)
    assertEquals(module.instructions(1).operands.last.words, Vec(Word(0xfe00)))
    assertEquals(module.instructions(3).operands.last.words, Vec(Word(0xff800100)))
    assertEquals(module.instructions(5).operands.last.words, Vec(Word(0), Word(0x7ff80000)))
    assertEquals(module.instructions(6).operands.last.words, Vec(Word(0x7f800000)))
    roundTrip(module)
  }

  test("grammar aliases resolve to canonical typed instructions and operand enums") {
    val module = parse("""%1 = OpTypeInt 32 1
      |%2 = OpConstant %1 1
      |%3 = OpTypeVector %1 4
      |%4 = OpConstantComposite %3 %2 %2 %2 %2
      |%5 = OpSDotKHR %1 %4 %4
      |OpDecorate %5 NonUniformEXT""".stripMargin)
    assertEquals(module.instructions(4).definition, Instruction.Definition.SDot)
    module.instructions(4) match {
      case Instruction.SDot(packedVectorFormat = format) => assertEquals(format, None)
      case other                                         => fail(s"expected typed dot instruction, found $other")
    }
    assertEquals(module.instructions.last.operands.last.words, Vec(Word(Decoration.NonUniform)))
    roundTrip(module)
  }

  test("raw string padding preserves following enum parameters") {
    val module = Module(
      Header(ISA.version, Word(0), Word(2), Word(0)),
      Vec(Instruction.Decorate(
        Operand(OperandKind.IdRef, Vec.nonEmpty(Word(1))),
        // The byte after 'a' terminates the string; retain the remaining nonzero padding bytes.
        Operand(
          OperandKind.Decoration,
          Vec.nonEmpty(Word(Decoration.LinkageAttributes), Word(0xdead0061), Word(LinkageType.Export))
        )
      )),
      Endianness.Little
    )
    roundTrip(module)
  }

  test("explicit floating formats preserve raw bits and reject literals with an unimplemented encoding") {
    val declaration = "%1 = OpTypeFloat 16 BFloat16KHR\n"
    val module      = parse(declaration + "%2 = OpConstant %1 !0x3f80")
    assertEquals(module.instructions(1).operands.last.words, Vec(Word(0x3f80)))
    roundTrip(module)
    assert(Parser.parse(declaration + "%2 = OpConstant %1 1.0").isLeft)
    val error = Parser.parse("OpMemoryModel Logical\nOpNop\nOpNop").swap.toOption.get
    assertEquals(error.position.line.raw, 2)
  }

  test("malformed assembly reports source locations without throwing") {
    val cases = Seq(
      "OpUnknown",
      "%1 = OpReturn",
      "OpTypeVoid",
      "%0 = OpTypeVoid",
      "%4294967295 = OpTypeVoid",
      "%1 = OpTypeVoid\n%1 = OpTypeBool",
      "OpCapability NotACapability",
      "OpCapability Shader|Kernel",
      "OpMemoryModel Logical",
      "%1 = OpTypeInt 0 0",
      "%1 = OpTypeInt 8 0\n%2 = OpConstant %1 256",
      "OpNop 7",
      "%1 = OpString \"unfinished",
      "%1 = OpString \"nul\u0000\"",
      "; Bound: 1\n%1 = OpTypeVoid",
      "; Version: 0.6\nOpNop",
      "\"\""
    )
    cases.foreach(source => assert(Parser.parse(source).isLeft, source))
    val error = Parser.parse("OpNop\n  OpUnknown").swap.toOption.get
    assertEquals(error.position.line.raw, 2)
    assertEquals(error.position.column.raw, 3)
  }
}
