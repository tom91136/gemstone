package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.Harness.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.rpc.ExecutionRPC.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
import scala.util.Using

class ConstantSuite extends munit.FunSuite {
  private val Flags = Vec(false, true)

  // cuda::barrier's init in conformance/unified/src/cuda/async_bulk.cu counts the block with ULDC.64 on sm_75
  for (sm <- Vec(75, 89)) test(s"sm_$sm 64-bit constant loads read launch dimensions in pairs") {
    val run = program(
      sm,
      "ULDC.64 UR4, c[0x0][0x0]; ULDC.64 UR6, c[0x0][0x8]; ULDC.64 UR8, c[0x0][0x10]; MOV R4, UR4; MOV R5, UR5; " +
        "MOV R6, UR6; MOV R7, UR7; MOV R8, UR8; MOV R9, UR9; LDC.64 R2, c[0x0][0x160]; STG.E.128 [R2.64], R4; " +
        "STG.E.64 [R2.64+0x10], R8; EXIT"
    ).launch(LaunchGeometry.Dimensions(4, 2, 3), LaunchGeometry.Dimensions(5, 6, 7))(Arg.zeros(24))
    assertEquals(run.words(0), Vec(4L, 2L, 3L, 5L, 6L, 7L))
  }

  test("kernel-scoped constant banks are constant sections for both decoding and binding") {
    Vec(".nv.constant3", ".nv.constant3.scoped", ".nv.constant12._Z6scopedv")
      .foreach(name => assert(Cubin.isConstantSection(name), name))
    Vec(".nv.constant0", ".nv.constant0.scoped", ".nv.constant", ".nv.constant03", ".nv.constant3x", ".nv.global")
      .foreach(name => assert(!Cubin.isConstantSection(name), name))
    val initial = LittleEndian.toVec(7L, 8)
    val module  = Harness.module(
      80,
      Vec(Entry("scoped", 0x160, Vec(8), registers = 8)),
      constants = Vec(".nv.constant3.scoped" -> initial)
    )
    val name    = Cubin.constantSymbol(module.sections.find(_.name == ".nv.constant3.scoped").get.index)
    val program = LoadedProgram(module, Map.empty, Workgroups.Sequential)
    assert(program.symbols.toOption.get.globals.exists(_.name == name))
    assertEquals(program.global(name).map(_.initial), Right(initial))
  }

  test("rebinding a global is idempotent for the same storage and rejected for different storage") {
    val module = Harness.module(
      80,
      Vec(Entry("scoped", 0x160, Vec(8), registers = 8)),
      constants = Vec(".nv.constant3" -> LittleEndian.toVec(7L, 8))
    )
    val name    = Cubin.constantSymbol(module.sections.find(_.name == ".nv.constant3").get.index)
    val fixture = Image(module, Vec.empty)
    session { (service, fixtures) =>
      val program = service.load(ProgramEncoding.CUDACubin, fixtures.image(fixture))
        .fold(error => fail(error.message), identity)
      val storage = service.registerAllocation(8).toOption.get
      val other   = service.registerAllocation(8).toOption.get
      assertEquals(service.bindGlobal(program, name, storage, 0L), Right(()))
      assertEquals(service.bindGlobal(program, name, storage, 0L), Right(()))
      assert(service.bindGlobal(program, name, other, 0L).isLeft)
    }
  }

  // ptxas binds driver system calls through bank 4; kernels run until they reach one, which traps.
  test("unused catalogues with external device system calls bind and leave their kernels executable") {
    val initial = Vec.fill(8)(0.toByte)
    val base    = Harness.module(
      120,
      Vec(Entry("unresolved_call", 0x380, Vec(8), registers = 8)),
      constants = Vec(".nv.constant4" -> initial)
    )
    val bank        = base.sections.find(_.name == ".nv.constant4").get
    val target      = "__cuda_syscall_cp_async_bulk_tensor_2d_tile_unicast"
    val names       = Vec.from(("\u0000" + target + "\u0000").getBytes(java.nio.charset.StandardCharsets.UTF_8))
    val strings     = Cubin.Section(base.sections.nonNegativeSize, ".strtab", 3, 0, names.nonNegativeSize, 0, 0, names)
    val symbolBytes = java.nio.ByteBuffer.allocate(48).order(java.nio.ByteOrder.LITTLE_ENDIAN)
    // ELF STT_FUNC, SHN_UNDEF; CUDA R_CUDA_64 references an external device function.
    symbolBytes.putInt(24, 1).put(28, 2.toByte)
    val symbols = Cubin.Section(
      strings.index + 1,
      ".symtab",
      2,
      0,
      48,
      strings.index,
      0,
      Vec.from(symbolBytes.array())
    )
    val relocation = Cubin.Section(
      symbols.index + 1,
      ".rela.nv.constant4",
      4,
      0,
      24,
      symbols.index,
      bank.index,
      LittleEndian.toVec(0L, 8) ++ LittleEndian.toVec((1L << 32) | 2L, 8) ++ LittleEndian.toVec(0L, 8)
    )
    val module  = base.copy(sections = base.sections ++ Vec(strings, symbols, relocation))
    val fixture =
      Image(
        module,
        Vec(Listing.program("MOV R2, 123; LDC.64 R4, c[0x0][0x380]; STG.E [R4.64], R2; EXIT", "unresolved_call").value)
      )
    session { (service, fixtures) =>
      val program = service.load(ProgramEncoding.CUDACubin, fixtures.image(fixture))
        .fold(error => fail(error.message), identity)
      val name: NonEmpty[String] = s"__sass_const${bank.index}"
      assert(service.symbols(program).toOption.get.globals.exists(_.name == name))
      val global  = service.global(program, name).fold(error => fail(error.message), identity)
      val storage = service.registerAllocation(8).toOption.get
      assert(service.write(storage, 0L, global.initial).isRight)
      assertEquals(service.bindGlobal(program, name, storage, 0L), Right(()))
      val task = KernelTask.validated(
        "unresolved_call": NonEmpty[String],
        LaunchGeometry.Dimensions.One,
        LaunchGeometry.Dimensions.One,
        Vec(KernelTask.Argument.Memory(storage, 0L))
      ).toOption.get
      val completion = service.launch(program, task).fold(error => fail(error.message), identity)
      assertEquals(service.completion(completion), Right(CompletionStatus.Succeeded))
      assertEquals(service.read(storage, 0L, 8), Right(LittleEndian.toVec(123L, 8)))
    }
  }

  for {
    sm <- Vec(35, 89)
    destination = if (sm == 35) "R4" else "R5"
    index       = if (sm == 35) "R5" else "R0"
    original    = s"LDC $destination, c[0x3][$index]"
    (access, instruction, selected) <- Vec(
      ("dynamic", original, None),
      ("static zero", s"LDC $destination, c[0x3][0x0]", Some(0)),
      ("static offset", s"LDC $destination, c[0x3][0x4]", Some(1)),
      ("zero register offset", s"LDC $destination, c[0x3][RZ+0x4]", Some(1)),
      ("constant operand", s"MOV $destination, c[0x3][0x4]", Some(1))
    )
  }
    test(s"sm_$sm $access constant reads observe bound storage updates") {
      val initial = LittleEndian.toVec(3L, 4) ++ LittleEndian.toVec(4L, 4)
      val base    = Harness.module(
        sm,
        Vec(Entry("constant_read", parameterBase(sm).unsafe, Vec(8), registers = if (sm == 35) 6 else 8)),
        constants = Vec(".nv.constant3" -> initial)
      )
      val bankIndex = base.sections.find(_.name == ".nv.constant3").get.index
      val module    = base.copy(globals = Vec(Cubin.Global("values", initial, Some(bankIndex))))
      val functions = Vec(Listing.function(
        Fixture.listing(s"constantSm$sm").replace(original, instruction),
        "constant_read",
        sm
      ).value)
      val lowered = Image(module, functions)
      session { (service, fixtures) =>
        val catalogue =
          service.load(ProgramEncoding.CUDACubin, fixtures.image(lowered)).fold(e => fail(e.message), identity)
        assert(service.symbols(catalogue).toOption.get.globals.exists(_.name == ("values": NonEmpty[String])))
        val named = service.global(catalogue, "values": NonEmpty[String]).fold(e => fail(e.message), identity)
        assertEquals(named.size.raw, 8L)
        assertEquals(named.initial.size, 0)
        val alias = named.alias.getOrElse(fail("named constant has no bank alias"))
        val bank  = service.global(catalogue, alias.name).fold(e => fail(e.message), identity)
        assert(alias.offset.raw + named.size <= bank.size)
        val storage = service.registerAllocation(bank.size.toInt.unsafe[Int > 0]).toOption.get
        assert(service.write(storage, 0L, bank.initial).isRight)
        val output  = service.registerAllocation(8).toOption.get
        val program = service.load(ProgramEncoding.CUDACubin, fixtures.image(lowered))
          .fold(e => fail(e.message), identity)
        assert(service.bindGlobal(program, alias.name, storage, 0L).isRight)
        val task = KernelTask.validated(
          "constant_read": NonEmpty[String],
          LaunchGeometry.Dimensions(2, 1, 1),
          LaunchGeometry.Dimensions.One,
          Vec(KernelTask.Argument.Memory(output, 0L))
        ).toOption.get
        for (values <- Vec(Vec(17, 29), Vec(31, 47))) {
          val bytes = Vec.from(values).flatMap(n => LittleEndian.toVec(n.toLong, 4))
          assert(service.write(storage, alias.offset, bytes).isRight)
          val completion = service.launch(program, task).fold(e => fail(e.message), identity)
          assertEquals(service.completion(completion), Right(CompletionStatus.Succeeded))
          val expected = selected.fold(values)(index => Vec.fill(2)(values(index)))
            .flatMap(value => LittleEndian.toVec(value.toLong, 4))
          assertEquals(service.read(output, 0L, 8), Right(expected))
        }
      }
    }
  // CUDA 13.3 ptxas loads the driver's malloc from a bank 4 pointer relocated to the undefined function and calls it
  // with CALL.ABS.NOINC, size and result in R4:R5.
  test("bank pointers relocated to the driver's malloc reach it through an absolute register call") {
    val base = Harness.module(
      89,
      Vec(Entry("driver_call", 0x160, Vec(8), registers = 12)),
      constants = Vec(".nv.constant4" -> Vec.fill(8)(0.toByte))
    )
    val bank        = base.sections.find(_.name == ".nv.constant4").get
    val names       = Vec.from("\u0000malloc\u0000".getBytes(java.nio.charset.StandardCharsets.UTF_8))
    val strings     = Cubin.Section(base.sections.nonNegativeSize, ".strtab", 3, 0, names.nonNegativeSize, 0, 0, names)
    val symbolBytes = java.nio.ByteBuffer.allocate(48).order(java.nio.ByteOrder.LITTLE_ENDIAN)
    symbolBytes.putInt(24, 1).put(28, 0x12.toByte)
    val symbols =
      Cubin.Section(strings.index + 1, ".symtab", 2, 0, 48, strings.index, 0, Vec.from(symbolBytes.array()))
    val relocation = Cubin.Section(
      symbols.index + 1,
      ".rel.nv.constant4",
      9,
      0,
      16,
      symbols.index,
      bank.index,
      LittleEndian.toVec(0L, 8) ++ LittleEndian.toVec((1L << 32) | 2L, 8)
    )
    val module = base.copy(sections = base.sections ++ Vec(strings, symbols, relocation))
    assertEquals(
      Cubin.relocateConstants(module, Map.empty).map(_.sections(bank.index).bytes),
      Right(LittleEndian.toVec(Cubin.DriverFunctions("malloc"), 8))
    )
    val functions = Vec(Listing.program(
      "LDC.64 R2, c[0x4][0x0]; MOV R4, 0x10; MOV R5, RZ; CALL.ABS.NOINC R2; MOV R6, 0x2a; ST.E [R4.64], R6; " +
        "LD.E R7, [R4.64]; LDC.64 R8, c[0x0][0x160]; STG.E [R8.64], R7; EXIT",
      "driver_call"
    ).value)
    session { (service, fixtures) =>
      val program =
        service.load(ProgramEncoding.CUDACubin, fixtures.image(Image(module, functions)))
          .fold(e => fail(e.message), identity)
      val storage = service.registerAllocation(8).toOption.get
      val output  = service.registerAllocation(4).toOption.get
      assert(service.bindGlobal(program, s"__sass_const${bank.index}", storage, 0L).isRight)
      val task = KernelTask.validated(
        "driver_call": NonEmpty[String],
        LaunchGeometry.Dimensions.One,
        LaunchGeometry.Dimensions.One,
        Vec(KernelTask.Argument.Memory(output, 0L))
      ).toOption.get
      val completion = service.launch(program, task).fold(e => fail(e.message), identity)
      assertEquals(service.completion(completion), Right(CompletionStatus.Succeeded))
      assertEquals(service.read(output, 0L, 4), Right(LittleEndian.toVec(42L, 4)))
    }
  }

  for {
    destinationFirst <- Flags
    dynamic          <- Flags
  } test(s"absolute data relocation survives binding order=$destinationFirst and dynamic=$dynamic") {
    val initial = Vec.fill(8)(0.toByte) ++ LittleEndian.toVec(4L, 8)
    val base    = Harness.module(
      80,
      Vec(Entry("relocated_read", 0x160, Vec(8), registers = 8)),
      constants = Vec(".nv.constant4" -> initial),
      globals = Vec(Cubin.Global("table", LittleEndian.toVec(17L, 4) ++ LittleEndian.toVec(29L, 4)))
    )
    val bank    = base.sections.find(_.name == ".nv.constant4").get
    val strings = Cubin.Section(
      base.sections.nonNegativeSize,
      ".strtab",
      3,
      0,
      7,
      0,
      0,
      Vec.from("\u0000table\u0000".getBytes(java.nio.charset.StandardCharsets.UTF_8))
    )
    val global      = Cubin.Section(strings.index + 1, ".nv.global.init", 1, 0, 8, 0, 0, base.globals.head.bytes)
    val symbolBytes = java.nio.ByteBuffer.allocate(48).order(java.nio.ByteOrder.LITTLE_ENDIAN)
    symbolBytes.putInt(24, 1).put(28, 1.toByte).putShort(30, global.index.toShort).putLong(40, 8L)
    val symbols =
      Cubin.Section(global.index + 1, ".symtab", 2, 0, 48, strings.index, 0, Vec.from(symbolBytes.array()))
    // CUDA ELF relocation type 2 is an absolute 64-bit data pointer; REL stores its addend in the bank.
    val relocation = Cubin.Section(
      symbols.index + 1,
      ".rel.nv.constant4",
      9,
      0,
      16,
      symbols.index,
      bank.index,
      LittleEndian.toVec(8L, 8) ++ LittleEndian.toVec((1L << 32) | 2L, 8)
    )
    val module    = base.copy(sections = base.sections ++ Vec(strings, global, symbols, relocation))
    val load      = if (dynamic) "c[0x4][R0]" else "c[0x4][0x8]"
    val functions = Vec(Listing.program(
      s"MOV R0, 8; LDC.64 R2, $load; LDC.64 R4, c[0x0][0x160]; LDG.E R6, [R2.64]; STG.E [R4.64], R6; EXIT",
      "relocated_read"
    ).value)
    session { (service, fixtures) =>
      val program =
        service.load(ProgramEncoding.CUDACubin, fixtures.image(Image(module, functions)))
          .fold(e => fail(e.message), identity)
      val storage = service.registerAllocation(24).toOption.get
      val table   = service.registerAllocation(24).toOption.get
      val output  = service.registerAllocation(4).toOption.get
      assert(service.write(storage, 8L, initial).isRight)
      assert(service.write(table, 16L, base.globals.head.bytes).isRight)
      val bindings = Vec[(NonEmpty[String], AllocationId, Long >= 0)](
        (s"__sass_const${bank.index}", storage, 8L),
        ("table", table, 16L)
      )
      (if (destinationFirst) bindings else bindings.reverse).foreach { case (name, allocation, offset) =>
        assert(service.bindGlobal(program, name, allocation, offset).isRight)
      }
      val task = KernelTask.validated(
        "relocated_read": NonEmpty[String],
        LaunchGeometry.Dimensions.One,
        LaunchGeometry.Dimensions.One,
        Vec(KernelTask.Argument.Memory(output, 0L))
      ).toOption.get
      for (value <- Vec(29, 47)) {
        assert(service.write(table, 20L, LittleEndian.toVec(value.toLong, 4)).isRight)
        val completion = service.launch(program, task).fold(e => fail(e.message), identity)
        assertEquals(service.completion(completion), Right(CompletionStatus.Succeeded))
        assertEquals(service.read(output, 0L, 4), Right(LittleEndian.toVec(value.toLong, 4)))
      }
      val pointer = java.nio.ByteBuffer.wrap(service.read(storage, 16L, 8).toOption.get.toArray)
        .order(java.nio.ByteOrder.LITTLE_ENDIAN).getLong()
      assert(service.write(storage, 16L, LittleEndian.toVec(pointer - 4L, 8)).isRight)
      bindings.foreach { case (name, allocation, offset) =>
        assert(service.bindGlobal(program, name, allocation, offset).isRight)
      }
      val completion = service.launch(program, task).fold(e => fail(e.message), identity)
      assertEquals(service.completion(completion), Right(CompletionStatus.Succeeded))
      assertEquals(service.read(output, 0L, 4), Right(LittleEndian.toVec(17L, 4)))
    }
    assert(Cubin.relocateConstants(module, Map.empty).left.exists(_.contains("unbound")))
    val invalid = relocation.copy(bytes = LittleEndian.toVec(8L, 8) ++ LittleEndian.toVec((1L << 32) | 99L, 8))
    assert(Cubin.constantRelocations(module.copy(sections = module.sections.updated(relocation.index, invalid)))
      .left.exists(_.contains("unsupported constant relocation kind")))
  }

  test("indexed constant loads read scalar and relocated pointer launch arguments") {
    check(
      86,
      """MOV R0, 20
        |LDC R4, c[0x0][R0+0x160]
        |MOV R2, 0
        |LDC.64 R2, c[0x0][R2+0x160]
        |S2R R0, SR_TID.X
        |IMAD.WIDE.U32 R2, R0, 4, R2
        |STG.E [R2.64], R4
        |EXIT""".stripMargin,
      Vec(4, 4, 4, 4)
    )
  }

  test("wide uniform constants retain all four parameter words") {
    check(
      120,
      """LDCU.128 UR4, c[0x0][0x160]
        |MOV.64 R10, UR4
        |MOV R4, UR4
        |MOV R5, UR5
        |LOP3.LUT R4, R4, UR6, RZ, 0x3c, !PT
        |LOP3.LUT R5, R5, UR7, RZ, 0x3c, !PT
        |STG.E.64 [R10], R4
        |EXIT""".stripMargin,
      Vec(0, 0)
    )
  }

  test("Blackwell launch dimensions retain the ABI padding before grid dimensions") {
    check(
      120,
      """LDC.64 R10, c[0x0][0x160]
        |LDC R4, c[0x0][0x360]
        |LDC R5, c[0x0][0x364]
        |LDC R6, c[0x0][0x368]
        |LDC R7, c[0x0][0x370]
        |LDC R8, c[0x0][0x374]
        |LDC R9, c[0x0][0x378]
        |STG.E.128 [R10], R4
        |STG.E.64 [R10+16], R8
        |EXIT""".stripMargin,
      Vec(6, 1, 1, 2, 3, 4),
      grid = LaunchGeometry.Dimensions(2, 3, 4)
    )
  }

  test("unapplied constant-bank relocations fail explicitly") {
    val saxpy      = load(86, Fixture.listing("saxpySm86"), "saxpy")
    val module     = saxpy.module(Vec(8, 8, 4, 4))
    val constant   = module.sections.find(_.name.startsWith(".nv.constant0")).get
    val relocation =
      Cubin.Section(module.sections.nonNegativeSize, ".rel" + constant.name, 9, 0, 16, 0, constant.index, Vec.empty)
    val relocated = module.copy(sections = module.sections :+ relocation)
    assert(lowerStatements(relocated, relocated.kernels.head, saxpy.functions).left.exists(_.contains("relocations")))
  }

  private case class Image(module: Cubin.Module, functions: Vec[DecodedFunction])

  /** Serves supplied disassembly through the cubin program's compilation and execution path. */
  private class Images extends Backend {
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private var images                 = Vec.empty[Image]
    override val format: ProgramFormat = ProgramFormat.CUDACubin
    def image(image: Image): Vec[Byte] = {
      val index = images.size
      images = images :+ image
      LittleEndian.toVec(index.toLong, 4)
    }
    override def load(bytes: Vec[Byte]): Either[Fault, Backend.Program] = {
      val image = images(LittleEndian.decodeInt32(bytes, 0))
      Right(LoadedProgram(
        image.module,
        image.module.kernels.map(_.name -> image.functions).toMap,
        Workgroups.Sequential
      ))
    }
  }

  private def session[A](body: (ExecutionSession, Images) => A): A = {
    val images = Images()
    Using.resource(ExecutionSession(List(images)))(service => body(service, images))
  }
}
