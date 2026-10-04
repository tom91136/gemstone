package net.kurobako.gemstone.isa.sass

import java.nio.ByteBuffer
import java.nio.ByteOrder
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

class CubinTextRelocationSuite extends munit.FunSuite {
  private val symbol: NonEmpty[String] = "switch_table"
  private val address                  = GuestAddress(0x1fffffff8L)

  private def fixture(sm: Int, addend: Option[Long], binary: Boolean = false): (Cubin.Module, DecodedFunction) = {
    val parameterBase: Int >= 0 = if (sm == 35) 0x140 else 0x160
    val width                   = if (sm < 70) 8 else 16
    val mov                     = if (sm < 70) "MOV32I" else "MOV"
    val body                    = Vec(
      (mov, Vec("R6", "0")),
      (mov, Vec("R7", "0")),
      ("LDC.64", Vec("R4", s"c[0x0][0x${parameterBase.raw.toHexString}]")),
      ("STG.E.64", Vec("[R4]", "R6")),
      ("EXIT", Vec.empty[String])
    ).zipWithIndex.map { case ((op, operands), index) =>
      val instruction = SASSFixture.instruction(index * width, op, operands)
      if (!binary || index >= 2) instruction
      else {
        // Compiler MOV32I/MOV encodings from the indirect-device-function cubins,
        // with destination fields changed to the output pair R6:R7.
        val word = if (sm == 35) 0x74000000001fc000L | ((index + 6).toLong << 2) | 2L
        else if (sm < 70) 0x010000000007f000L | (index + 6).toLong
        else 0x0000000000007802L | ((index + 6).toLong << 16)
        val control = if (sm >= 70) LittleEndian.toVec(0x000fe40000000f00L, 8)
        else if (sm >= 50) LittleEndian.toVec(if (index == 1) 0x7f5L else if (sm == 50) 0x7f1L else 0x7e1L, 3)
        else Vec.empty[Byte]
        val bytes = LittleEndian.toVec(word, 8) ++ control
        InstructionDecoder.decodeTyped(instruction.address, bytes.toArray, s"SM$sm").fold(fail(_), identity)
      }
    }
    val base   = SASSFixture.module(sm, Vec(SASSFixture.Kernel("relocated", parameterBase, Vec((0, 8)))))
    val text   = base.sections.head.copy(size = 80, bytes = Vec.fill(80)(0.toByte))
    val global =
      Cubin.Section(base.sections.nonNegativeSize, ".nv.global.init", 1, 3L, 48, 0, 0, Vec.fill(48)(0.toByte))
    val names   = Vec.from(("\u0000" + symbol.raw + "\u0000").getBytes(java.nio.charset.StandardCharsets.UTF_8))
    val strings = Cubin.Section(global.index + 1, ".strtab", 3, 0, names.nonNegativeSize, 0, 0, names)
    val bytes   = ByteBuffer.allocate(48).order(ByteOrder.LITTLE_ENDIAN)
    bytes.putInt(24, 1).put(28, 1.toByte).putShort(30, global.index.raw.toShort).putLong(32, 16L).putLong(40, 32L)
    val symbols = Cubin.Section(strings.index + 1, ".symtab", 2, 0, 48, strings.index, 0, Vec.from(bytes.array()))
    // CUDA R_CUDA_32I_LO/HI, with RELA addends added to the complete guest pointer before splitting.
    val lowKind = if (sm < 50) 0x0dL else if (sm < 70) 0x2bL else 0x38L
    val entries = Vec((0L, lowKind), (width.toLong, lowKind + 1)).flatMap { case (offset, kind) =>
      LittleEndian.toVec(offset, 8) ++ LittleEndian.toVec((1L << 32) | kind, 8) ++
        addend.fold(Vec.empty[Byte])(LittleEndian.toVec(_, 8))
    }
    val relocations = Cubin.Section(
      symbols.index + 1,
      ".rel.text.relocated",
      if (addend.isDefined) 4 else 9,
      0,
      entries.nonNegativeSize,
      symbols.index,
      text.index.raw,
      entries
    )
    val module = base.copy(
      sections = base.sections.updated(0, text) ++ Vec(global, strings, symbols, relocations),
      globals = Vec(Cubin.Global(symbol, Vec.fill(32)(0.toByte), offset = 16))
    )
    (module, DecodedFunction("relocated", body))
  }

  for {
    sm     <- Vec(35, 50, 60, 70)
    addend <- Vec(None, Some(16L), Some(-8L))
    binary <- Vec(false, true)
  } test(s"sm_$sm text address halves resolve bound globals with addend $addend, binary=$binary") {
    val (module, function) = fixture(sm, addend, binary)
    val compiled           = LoadedProgram.compile(module, module.kernels.head, Vec(function), Map(symbol -> address))
      .fold(error => fail(error.message), identity)
    val memory = SASSFixture.Execution()
    val output = memory.allocate(8L)
    val task   = SASSFixture.task(
      "relocated",
      LaunchGeometry.Dimensions.One,
      LaunchGeometry.Dimensions.One,
      Vec(KernelTask.Argument.Memory(output, 0L))
    )
    memory.run(compiled, task).fold(error => fail(error.message), identity)
    val expected = address.raw + addend.getOrElse(0L)
    assertEquals(memory.readWords(output, 2), Vec(expected & 0xffffffffL, expected >>> 32))
  }

  test("text relocation metadata and unsupported targets fail before instruction execution") {
    val (module, function)                                                        = fixture(50, None)
    val table                                                                     = module.sections.last
    def tableBytes(offset: Long = 0L, info: Long = (1L << 32) | 0x2bL): Vec[Byte] =
      LittleEndian.toVec(offset, 8) ++ LittleEndian.toVec(info, 8) ++ table.bytes.drop(16)
    def withTable(bytes: Vec[Byte], link: Int >= 0 = table.link): Cubin.Module =
      module.copy(sections =
        module.sections.updated(table.index.raw, table.copy(bytes = bytes, size = bytes.nonNegativeSize, link = link))
      )
    val malformed = Vec(
      withTable(table.bytes.dropRight(1))                       -> "relocation",
      withTable(tableBytes(offset = 80L))                       -> "exceeds",
      withTable(tableBytes(offset = 1L))                        -> "misaligns",
      withTable(tableBytes(offset = 40L))                       -> "decoded instruction",
      withTable(tableBytes(info = (1L << 32) | 0x99L))          -> "kind",
      withTable(tableBytes(info = (99L << 32) | 0x2bL))         -> "symbol",
      withTable(tableBytes(info = (0xffffffffL << 32) | 0x2bL)) -> "symbol",
      withTable(table.bytes, 0)                                 -> "symbol table",
      withTable(table.bytes.take(16) ++ table.bytes.take(16))   -> "overlapping",
      module.copy(globals = Vec.empty)                          -> "target"
    )
    malformed.foreach { case (invalid, expected) =>
      assert(
        VMCompiler.lower(invalid, invalid.kernels.head, Vec(function), Set(symbol))
          .left.exists(_.raw.contains(expected)),
        clues(expected)
      )
    }
    assert(VMCompiler.lower(module, module.kernels.head, Vec(function)).left.exists(_.raw.contains("unbound")))
    val nonzero = function.copy(instructions =
      function.instructions.updated(
        0,
        SASSFixture.instruction(0, "MOV32I", Vec("R6", "4"))
      )
    )
    assert(VMCompiler.lower(module, module.kernels.head, Vec(nonzero), Set(symbol))
      .left.exists(_.raw.contains("implicit")))
    val wrongInstruction = function.copy(instructions =
      function.instructions.updated(
        0,
        SASSFixture.instruction(0, "IADD", Vec("R6", "R6", "1"))
      )
    )
    assert(VMCompiler.lower(module, module.kernels.head, Vec(wrongInstruction), Set(symbol))
      .left.exists(_.raw.contains("MOV")))
    val symbols = module.sections(table.link.raw)
    val bytes   = symbols.bytes.toArray
    ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).put(28, 2.toByte).putShort(30, 0.toShort)
    val external =
      module.copy(sections = module.sections.updated(symbols.index.raw, symbols.copy(bytes = Vec.from(bytes))))
    assert(VMCompiler.lower(external, module.kernels.head, Vec(function), Set(symbol))
      .left.exists(_.raw.contains("target")))
  }
}
