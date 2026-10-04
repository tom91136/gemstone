package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

class CubinConstantSuite extends munit.FunSuite {
  test("unused catalogues with external device relocations bind but remain non-executable") {
    val initial = Vec.fill(8)(0.toByte)
    val base    = SASSFixture.module(
      120,
      Vec(SASSFixture.Kernel("unresolved_call", 0x380, Vec((0, 8)), registers = 8)),
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
      bank.index.raw,
      LittleEndian.toVec(0L, 8) ++ LittleEndian.toVec((1L << 32) | 2L, 8) ++ LittleEndian.toVec(0L, 8)
    )
    val module       = base.copy(sections = base.sections ++ Vec(strings, symbols, relocation))
    val instructions = Vec("MOV R2,123;", "LDC.64 R4,c[0x0][0x380];", "STG.E [R4.64],R2;", "EXIT;")
      .zipWithIndex.map { case (text, index) =>
        DecodedInstruction((index * 16).unsafe[Int >= 0], Parser.parse(text).fold(fail(_), identity), None, None)
      }
    val fixture = CubinBackendFixture.Fixture(module, Vec(DecodedFunction("unresolved_call", instructions)))
    CubinBackendFixture.session { (service, fixtures) =>
      val program = service.load(CubinBackendFixture.encoding, fixtures.image(fixture))
        .fold(error => fail(error.message), identity)
      val name: NonEmpty[String] = s"__sass_const${bank.index}"
      assert(service.symbols(program).toOption.get.globals.exists(_.name == name))
      val global  = service.global(program, name).fold(error => fail(error.message), identity)
      val storage = service.registerAllocation(8L, AllocationKind.Shared).toOption.get
      assert(service.write(storage, 0L, global.initial).isRight)
      assertEquals(service.bindGlobal(program, name, storage, 0L), Right(()))
      val task = KernelTask.validated(
        "unresolved_call": NonEmpty[String],
        LaunchGeometry.Dimensions.One,
        LaunchGeometry.Dimensions.One,
        Vec(KernelTask.Argument.Memory(storage, 0L))
      ).toOption.get
      val completion = service.launch(program, task).fold(error => fail(error.message), identity)
      service.completion(completion) match {
        case Right(CompletionStatus.Failed(error: ExecutionFailure.Unsupported)) =>
          assert(error.message.contains(target), error.message)
        case other => fail(s"expected unsupported relocation failure, got $other")
      }
      assertEquals(service.read(storage, 0L, 8), Right(initial))
    }
  }

  for {
    (sm, text) <- Vec(35 -> ConstantFixtures.constantSm35, 89 -> ConstantFixtures.constantSm89)
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
      val base    = SASSFixture.module(
        sm,
        Vec(SASSFixture.Kernel(
          "constant_read",
          if (sm == 35) 0x140 else 0x160,
          Vec((0, 8)),
          registers = if (sm == 35) 6 else 8
        )),
        constants = Vec(".nv.constant3" -> initial)
      )
      val bankIndex = base.sections.find(_.name == ".nv.constant3").get.index
      val module    = base.copy(globals = Vec(Cubin.Global("values", initial, Some(bankIndex))))
      val functions = ListingDecoder.parse(
        text.replace(original, instruction),
        "constant_read",
        sm
      ).fold(fail(_), identity)
      val lowered = CubinBackendFixture.lower(module, module.kernels.head, functions).fold(fail(_), identity)
      CubinBackendFixture.session { (service, fixtures) =>
        val catalog =
          service.load(CubinBackendFixture.encoding, fixtures.image(lowered)).fold(e => fail(e.message), identity)
        assert(service.symbols(catalog).toOption.get.globals.exists(_.name == ("values": NonEmpty[String])))
        val named = service.global(catalog, "values": NonEmpty[String]).fold(e => fail(e.message), identity)
        assertEquals(named.size.raw, 8L)
        assertEquals(named.initial.size, 0)
        val alias = named.alias.getOrElse(fail("named constant has no bank alias"))
        val bank  = service.global(catalog, alias.name).fold(e => fail(e.message), identity)
        assert(alias.offset.raw + named.size.raw <= bank.size.raw)
        val storage = service.registerAllocation(bank.size.raw.unsafe[Long > 0], AllocationKind.Shared).toOption.get
        assert(service.write(storage, 0L, bank.initial).isRight)
        val output  = service.registerAllocation(8L, AllocationKind.Shared).toOption.get
        val program = service.load(CubinBackendFixture.encoding, fixtures.image(lowered))
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
  for {
    destinationFirst <- Vec(false, true)
    dynamic          <- Vec(false, true)
  } test(s"absolute data relocation survives binding order=$destinationFirst and dynamic=$dynamic") {
    val initial = Vec.fill(8)(0.toByte) ++ LittleEndian.toVec(4L, 8)
    val base    = SASSFixture.module(
      80,
      Vec(SASSFixture.Kernel("relocated_read", 0x160, Vec((0, 8)), registers = 8)),
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
    symbolBytes.putInt(24, 1).put(28, 1.toByte).putShort(30, global.index.raw.toShort).putLong(40, 8L)
    val symbols = Cubin.Section(global.index + 1, ".symtab", 2, 0, 48, strings.index, 0, Vec.from(symbolBytes.array()))
    // CUDA ELF relocation type 2 is an absolute 64-bit data pointer; REL stores its addend in the bank.
    val relocation = Cubin.Section(
      symbols.index + 1,
      ".rel.nv.constant4",
      9,
      0,
      16,
      symbols.index,
      bank.index.raw,
      LittleEndian.toVec(8L, 8) ++ LittleEndian.toVec((1L << 32) | 2L, 8)
    )
    val module     = base.copy(sections = base.sections ++ Vec(strings, global, symbols, relocation))
    val operations = Vec(
      "MOV"    -> "R0,8",
      "LDC.64" -> (if (dynamic) "R2,c[0x4][R0]" else "R2,c[0x4][0x8]"),
      "LDC.64" -> "R4,c[0x0][0x160]",
      "LDG.E"  -> "R6,[R2.64]",
      "STG.E"  -> "[R4.64],R6",
      "EXIT"   -> ""
    )
    val instructions = operations.zipWithIndex.map { case ((opcode, operands), index) =>
      DecodedInstruction(
        (index * 16).unsafe[Int >= 0],
        Parser.parse(s"$opcode $operands;").fold(fail(_), identity),
        None,
        None
      )
    }
    val functions = Vec(DecodedFunction("relocated_read", instructions))
    CubinBackendFixture.session { (service, fixtures) =>
      val program =
        service.load(CubinBackendFixture.encoding, fixtures.image(CubinBackendFixture.Fixture(module, functions)))
          .fold(e => fail(e.message), identity)
      val storage = service.registerAllocation(24L, AllocationKind.Shared).toOption.get
      val table   = service.registerAllocation(24L, AllocationKind.Shared).toOption.get
      val output  = service.registerAllocation(4L, AllocationKind.Shared).toOption.get
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
    assert(Cubin.constantRelocations(module.copy(sections = module.sections.updated(relocation.index.raw, invalid)))
      .left.exists(_.contains("unsupported constant relocation kind")))
  }

}
