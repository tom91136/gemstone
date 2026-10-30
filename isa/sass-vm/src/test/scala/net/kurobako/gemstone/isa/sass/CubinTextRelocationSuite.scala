package net.kurobako.gemstone.isa.sass

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.Harness.*
import net.kurobako.gemstone.isa.sass.Targets.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.Suite
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*

class CubinTextRelocationSuite extends Suite {
  private val symbol: NonEmpty[String] = "switch_table"
  private val address                  = GuestAddress(0x1fffffff8L)
  private val Flags                    = Vec(false, true)

  private def fixture(sm: Int, addend: Option[Long], binary: Boolean = false, uniform: Boolean = false)
      : (Cubin.Module, DecodedFunction) = {
    val parameterBase: Int >= 0 = if (sm == 35) 0x140 else 0x160
    val width                   = if (sm < 70) 8 else 16
    val mov                     = if (sm < 70) "MOV32I" else "MOV"
    val bank                    = s"c[0x0][0x${parameterBase.raw.toHexString}]"
    // CUDA 11.8 sm_75 and later load a global's address into a uniform pair.
    val listing = Listing.program(
      if (uniform) s"UMOV UR6, 0; UMOV UR7, 0; LDC.64 R4, $bank; MOV R6, UR6; MOV R7, UR7; STG.E.64 [R4], R6; EXIT"
      else s"$mov R6, 0; $mov R7, 0; LDC.64 R4, $bank; STG.E.64 [R4], R6; EXIT",
      "relocated"
    ).value.instructions
    val body = listing.zipWithIndex.map { (parsed, index) =>
      val instruction = parsed.copy(address = (index * width).unsafe[Int >= 0])
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
        InstructionDecoder.decode(instruction.address, bytes.toArray, target(sm)).fold(fail(_), identity)
      }
    }
    val base   = Harness.module(sm, Vec(Entry("relocated", parameterBase, Vec(8))))
    val size   = math.max(80, listing.size * width)
    val text   = base.sections.head.copy(size = size.unsafe[Int >= 0], bytes = Vec.fill(size)(0.toByte))
    val global =
      Cubin.Section(base.sections.nonNegativeSize, ".nv.global.init", 1, 3L, 48, 0, 0, Vec.fill(48)(0.toByte))
    val names   = Vec.from(("\u0000" + symbol.raw + "\u0000").getBytes(StandardCharsets.UTF_8))
    val strings = Cubin.Section(global.index + 1, ".strtab", 3, 0, names.nonNegativeSize, 0, 0, names)
    val bytes   = ByteBuffer.allocate(48).order(ByteOrder.LITTLE_ENDIAN)
    bytes.putInt(24, 1).put(28, 1.toByte).putShort(30, global.index.toShort).putLong(32, 16L).putLong(40, 32L)
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
      text.index,
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
    binary <- Flags
  } test(s"sm_$sm text address halves resolve bound globals with addend $addend, binary=$binary") {
    val (module, function) = fixture(sm, addend, binary)
    val compiled           = VMCompiler.compile(module, module.kernels.head, Vec(function), Map(symbol -> address))
      .fold(error => fail(error.raw), identity)
    val memory = Memory()
    val output = memory.allocate(8L)
    val task   = KernelTask.validated(
      "relocated",
      LaunchGeometry.Dimensions.One,
      LaunchGeometry.Dimensions.One,
      Vec(KernelTask.Argument.Memory(output, 0L))
    ).value
    memory.run(compiled, task).fold(error => fail(error.message), identity)
    val expected = address.raw + addend.getOrElse(0L)
    assertEquals(memory.readWords(output, 2), Vec(expected & 0xffffffffL, expected >>> 32))
  }

  for (addend <- Vec(None, Some(16L)))
    test(s"sm_75 uniform address halves resolve bound globals with addend $addend") {
      val (module, function) = fixture(75, addend, uniform = true)
      val compiled           = VMCompiler.compile(module, module.kernels.head, Vec(function), Map(symbol -> address))
        .fold(error => fail(error.raw), identity)
      val memory = Memory()
      val output = memory.allocate(8L)
      val task   = KernelTask.validated(
        "relocated",
        LaunchGeometry.Dimensions.One,
        LaunchGeometry.Dimensions.One,
        Vec(KernelTask.Argument.Memory(output, 0L))
      ).value
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
        module.sections.updated(table.index, table.copy(bytes = bytes, size = bytes.nonNegativeSize, link = link))
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
        lowerStatements(invalid, invalid.kernels.head, Vec(function), Map(symbol -> address))
          .left.exists(_.raw.contains(expected)),
        clues(expected)
      )
    }
    assert(lowerStatements(module, module.kernels.head, Vec(function)).left.exists(_.raw.contains("unbound")))
    val nonzero = function.copy(instructions =
      function.instructions.updated(
        0,
        Listing.program("MOV32I R6, 4").value.instructions.head
      )
    )
    assert(lowerStatements(module, module.kernels.head, Vec(nonzero), Map(symbol -> address))
      .left.exists(_.raw.contains("implicit")))
    val wrongInstruction = function.copy(instructions =
      function.instructions.updated(
        0,
        Listing.program("IADD R6, R6, 1").value.instructions.head
      )
    )
    assert(lowerStatements(module, module.kernels.head, Vec(wrongInstruction), Map(symbol -> address))
      .left.exists(_.raw.contains("MOV")))
    val symbols = module.sections(table.link)
    val bytes   = symbols.bytes.toArray
    ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).put(28, 2.toByte).putShort(30, 0.toShort)
    val external =
      module.copy(sections = module.sections.updated(symbols.index, symbols.copy(bytes = Vec.from(bytes))))
    assert(lowerStatements(external, module.kernels.head, Vec(function), Map(symbol -> address))
      .left.exists(_.raw.contains("target")))
  }

  // ptxas calls the driver's malloc through JCAL (sm_35 R_CUDA_ABS32_26, sm_50 R_CUDA_ABS32_32) or CALL.ABS.NOINC
  // (sm_70 R_CUDA_ABS47_34) relocated to an undefined function, with the size and result in R4:R5.
  for ((sm, kind) <- Vec(35 -> 0x0cL, 50 -> 0x2aL, 70 -> 0x3aL))
    test(s"sm_$sm calls relocated to the driver's malloc allocate device memory") {
      val width   = if (sm < 70) 8 else 16
      val call    = if (sm < 70) "JCAL 0x0" else "CALL.ABS.NOINC 0x0"
      val bank    = if (sm < 70) "c[0x0][0x140]" else "c[0x0][0x160]"
      val listing = Listing.program(
        s"MOV R4, 0x10; MOV R5, RZ; $call; MOV R6, 0x2a; ST.E [R4], R6; LD.E R7, [R4]; LDC.64 R2, $bank; ST.E [R2], R7; EXIT",
        "relocated"
      ).value.instructions.zipWithIndex.map((instruction, index) => instruction.copy(address = (index * width).unsafe))
      val base    = Harness.module(sm, Vec(Entry("relocated", if (sm < 70) 0x140 else 0x160, Vec(8))))
      val size    = listing.size * width
      val text    = base.sections.head.copy(size = size.unsafe[Int >= 0], bytes = Vec.fill(size)(0.toByte))
      val names   = Vec.from("\u0000malloc\u0000".getBytes(StandardCharsets.UTF_8))
      val strings = Cubin.Section(base.sections.nonNegativeSize, ".strtab", 3, 0, names.nonNegativeSize, 0, 0, names)
      val bytes   = ByteBuffer.allocate(48).order(ByteOrder.LITTLE_ENDIAN)
      bytes.putInt(24, 1).put(28, 0x12.toByte)
      val symbols = Cubin.Section(strings.index + 1, ".symtab", 2, 0, 48, strings.index, 0, Vec.from(bytes.array()))
      val entries = LittleEndian.toVec(2L * width, 8) ++ LittleEndian.toVec((1L << 32) | kind, 8)
      val table   =
        Cubin.Section(symbols.index + 1, ".rel.text.relocated", 9, 0, 16, symbols.index, text.index, entries)
      val module   = base.copy(sections = base.sections.updated(0, text) ++ Vec(strings, symbols, table))
      val compiled = VMCompiler.compile(module, module.kernels.head, Vec(DecodedFunction("relocated", listing)))
        .fold(error => fail(error.raw), identity)
      val memory = Memory()
      val output = memory.allocate(8L)
      val task   = KernelTask.validated(
        "relocated",
        LaunchGeometry.Dimensions.One,
        LaunchGeometry.Dimensions.One,
        Vec(KernelTask.Argument.Memory(output, 0L))
      ).value
      memory.run(compiled, task).fold(error => fail(error.message), identity)
      assertEquals(memory.readWords(output, 1), Vec(0x2aL))
    }
}
