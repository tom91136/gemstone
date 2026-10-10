package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.Harness.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.either.*

/** Maxwell/Pascal generic memory: CUDA 11.8 pairs LEA/LEA.HI.X whose auxiliary predicate is unobservable. */
class GenericMemorySuite extends munit.FunSuite {
  private def at(address: Int, text: String): DecodedInstruction =
    Listing.program(text).value.instructions.head.copy(address = address.unsafe[Int >= 0])

  private def operand(text: String): Operand = OperandParser.parse(text).value

  private def print(operand: Operand): String                = OperandPrinter.print(operand).value
  private def guard(text: String): Option[Instruction.Guard] = Parser.parse(s"$text NOP;").value.guard

  private def is(instruction: DecodedInstruction, mnemonic: String, modifiers: String*): Boolean =
    instruction.mnemonic.raw == mnemonic && instruction.modifiers.map(_.name) == Vec.from(modifiers)

  private def isHigh(instruction: DecodedInstruction): Boolean = is(instruction, "LEA", "HI", "X")

  private def rewritten(sm: Int, function: DecodedFunction): DecodedFunction = LegacyAddressRewrite(sm, function)

  private def assertRewrites(function: DecodedFunction, sm: Int): Unit =
    // the rewrite replaces the auxiliary-predicate destination with the high address register
    assert(rewritten(sm, function).instructions.find(isHigh(_)).get.operands.head match {
      case Operand.Register(RegisterBank.R, _) => true
      case _                                   => false
    })

  private def assertRejects(function: DecodedFunction, sm: Int): Unit = {
    val _ = intercept[IllegalArgumentException](rewritten(sm, function))
  }

  private def generic(name: String, sm: Int, function: String, shared: Int = 0): Kernel =
    load(
      sm,
      Fixture.listing(s"generic$name${if (sm == 60 && Fixture.exists(s"generic${name}Sm60")) "Sm60" else "Sm50"}"),
      function,
      shared.unsafe[Int >= 0]
    )

  private val values      = Vec(0x00000000, 0x7fc00001, 0x80000000, 0xffffffff, 0x12345678)
  private val carryBase   = 0x1fffffffcL
  private val carryTarget = 0x200000000L

  for (sm <- Vec(50, 60))
    test(s"sm_$sm generic-address rewrite rejects escaped predicates, mismatched guards, and addresses") {
      val function    = generic("AddressGlobalLoad", sm, "global_load").function
      val highIndex   = function.instructions.indexWhere(isHigh(_))
      val memoryIndex = function.instructions.indexWhere(is(_, "LD", "E"))
      assert(highIndex >= 0 && memoryIndex > highIndex)
      val (high, memory) = (function.instructions(highIndex), function.instructions(memoryIndex))
      val predicate      = print(high.operands.head)
      def inserted(index: Int, instruction: DecodedInstruction): DecodedFunction =
        function.copy(instructions = function.instructions.patch(index, Vec(instruction), 0))
      def replaced(index: Int, instruction: DecodedInstruction): DecodedFunction =
        function.copy(instructions = function.instructions.updated(index, instruction))
      val negated = high.operands.head match {
        case value: Operand.Predicate => print(value.copy(negated = !value.negated))
        case other                    => fail(s"expected auxiliary predicate, found $other")
      }
      Vec(
        inserted(memoryIndex + 1, at(memory.address.raw + 1, "P2R R8, PR, RZ, 0x7f")),
        inserted(0, at(high.address.raw - 1, s"BRA 0x${high.address.raw.toHexString}")),
        inserted(highIndex + 1, at(high.address.raw + 1, s"MOV ${print(high.operands(1))}, RZ")),
        replaced(
          memoryIndex,
          memory.rewrite(operands = memory.operands.updated(memory.operands.size - 1, operand("P1")))
        ),
        replaced(memoryIndex, memory.rewrite(operands = memory.operands.updated(1, operand("[RZ]")))),
        replaced(highIndex, high.rewrite(operands = high.operands.updated(1, operand("R7")))),
        inserted(memoryIndex + 1, at(memory.address.raw + 8, s"SEL R8, RZ, 0x1, $negated")),
        inserted(highIndex + 1, at(high.address.raw + 8, s"@P1 ISETP.EQ.AND $predicate, PT, RZ, RZ, PT")),
        inserted(memoryIndex + 1, at(memory.address.raw + 8, "BRA 0x100")),
        inserted(highIndex + 1, at(high.address.raw + 8, s"SEL R8, RZ, RZ, $predicate"))
      ).foreach(assertRejects(_, sm))
      val guardedConsumer = replaced(memoryIndex, memory.rewrite(guard = guard("@P1")))
      assertEquals(rewritten(sm, guardedConsumer).instructions(memoryIndex).guard, guard("@P1"))
      assertRejects(
        guardedConsumer.copy(instructions =
          guardedConsumer.instructions
            .updated(highIndex - 1, function.instructions(highIndex - 1).rewrite(guard = guard("@!P1")))
            .updated(highIndex, high.rewrite(guard = guard("@!P1")))
        ),
        sm
      )
    }

  for (sm <- Vec(50, 60))
    test(s"CUDA 11.8 sm_$sm generic global/shared/local load and store preserve all bits") {
      val kernels =
        Vec("global_load", "global_store", "shared_load", "shared_store", "local_load", "local_store").map { name =>
          val pascal = name.split('_').map(_.capitalize).mkString
          val kernel = generic(s"Address$pascal", sm, name, if (name.startsWith("shared")) 16 else 0)
          val high   = kernel.function.instructions.find(isHigh(_)).get
          val access = kernel.function.instructions.find(i =>
            Set("LD", "ST")(i.mnemonic.raw) && i.modifiers.headOption.contains(Instruction.Modifier.E)
          ).get
          assertEquals(access.operands.last, high.operands.head, name)
          assertEquals(kernel.function.instructions.count(isHigh(_)), 1, name)
          val result = rewritten(sm, kernel.function)
          assertEquals(result.instructions.find(isHigh(_)).get.operands.head, high.operands(1), name)
          assert(
            !result.instructions.find(i =>
              Set("LD", "ST")(i.mnemonic.raw) && i.modifiers.headOption.contains(Instruction.Modifier.E)
            ).get
              .operands.contains(high.operands.head),
            name
          )
          name -> kernel
        }.toMap
      val memory = Memory()
      val target = memory.allocate(Vec.fill(8)(0.toByte))
      memory.rebase(target, GuestAddress(carryTarget))
      val output                      = memory.allocate(Vec.fill(4)(0.toByte))
      def word(id: AllocationId): Int = memory.getInt(id, 0)
      values.foreach { value =>
        memory.putInt(target, 0, value)
        val _ = kernels("global_load").launch(1, memory = memory)(Arg.u64(carryBase), Arg.Region(output))
        assertEquals(word(output), value, s"global load offset carry value=0x${value.toHexString}")
        memory.putInt(target, 0, 0)
        val _ = kernels("global_store").launch(1, memory = memory)(Arg.u64(carryBase), Arg.u32(value))
        assertEquals(word(target), value, s"global store offset carry value=0x${value.toHexString}")
      }
      for {
        space     <- Vec("shared", "local")
        operation <- Vec("load", "store")
        value     <- values
      } {
        val _ = kernels(s"${space}_$operation").launch(1, memory = memory)(Arg.Region(output), Arg.u32(value))
        assertEquals(word(output), value, s"$space generic $operation value=0x${value.toHexString}")
      }
      val store      = kernels("global_store")
      val guardedSet = Set(
        store.function.instructions.indexWhere(is(_, "LEA")),
        store.function.instructions.indexWhere(isHigh(_)),
        store.function.instructions.indexWhere(is(_, "ST", "E"))
      )
      def guarded(condition: String): Kernel = store.copy(functions =
        Vec(store.function.copy(instructions =
          at(store.function.instructions.head.address.raw - 8, s"ISETP.EQ.AND P1, PT, RZ, $condition, PT") +:
            store.function.instructions.zipWithIndex.map((i, index) =>
              if (guardedSet(index)) i.rewrite(guard = guard("@P1")) else i
            )
        ))
      )
      assert(rewritten(sm, guarded("0x1").function).instructions.exists(isHigh(_)))
      memory.putInt(target, 0, 0x89abcdef)
      val _ = guarded("0x1").launch(1, memory = memory)(Arg.u64(carryBase), Arg.u32(0x12345678))
      assertEquals(word(target), 0x89abcdef, "inactive guard must suppress the generic store")
      val _ = guarded("RZ").launch(1, memory = memory)(Arg.u64(carryBase), Arg.u32(0x12345678))
      assertEquals(word(target), 0x12345678, "active guard must permit the generic store")
    }

  for (sm <- Vec(50, 60))
    test(s"CUDA 11.8 sm_$sm generic byte stores preserve neighbouring bytes and loads extend correctly") {
      def kernel(name: String): Kernel =
        generic(s"Byte${name.split('_').map(_.capitalize).mkString}", sm, name, if (name == "shared_u8") 16 else 0)
      val globalU8 = kernel("global_u8").function
      val high     = globalU8.instructions.find(isHigh(_)).get
      assertEquals(globalU8.instructions.find(is(_, "ST", "E", "CG", "U8")).get.operands.last, high.operands.head)
      assertEquals(globalU8.instructions.find(is(_, "LD", "E", "CG", "U8")).get.operands.last, high.operands.head)
      val barrier      = kernel("global_u8_barrier").function.instructions
      val barrierIndex = barrier.indexWhere(is(_, "MEMBAR", "CTA"))
      val highIndex    = barrier.indexWhere(i =>
        isHigh(i) && i.operands.head == Operand.Predicate(Some(1), uniform = false, negated = false)
      )
      val loadIndex = barrier.indexWhere(is(_, "LD", "E", "CG", "U8"))
      assert(highIndex >= 0 && barrierIndex > highIndex && loadIndex > barrierIndex)
      assertEquals(barrier(loadIndex).operands.last, barrier(highIndex).operands.head)
      val pointerLow = barrier.find(is(_, "ST", "E", "CG", "U8")).get.operands.head match {
        case Operand.Address(Some(base), _, _, _, _, _) => base
        case other                                      => fail(s"expected byte-store address, found $other")
      }
      val carryIndex = barrier.indexWhere(is(_, "IADD32I"))
      val carry      = barrier(carryIndex)
      val _          = intercept[IllegalArgumentException](rewritten(
        sm,
        DecodedFunction(
          "global_u8_barrier",
          barrier.updated(
            carryIndex,
            carry.rewrite(operands =
              carry.operands.updated(0, Operand.Qualified(pointerLow, Vec(OperandSelector.CC)))
            )
          )
        )
      ))
      val (sourceValue, canary) = (0xdeadbe80, 0x3c.toByte)
      val memory                = Memory()
      val base                  = 0x5fffffff0L + sm.toLong * 0x100000000L
      val source                = memory.allocate(Vec.fill(64)(canary))
      memory.rebase(source, GuestAddress(base))
      for (
        (
          name,
          extended
        ) <- Vec("global_u8" -> 0x00000080L, "global_s8" -> 0xffffff80L, "global_u8_barrier" -> 0x00000080L)
      ) {
        memory.write(source, 0L, Vec.fill(64)(canary)).value
        val run = kernel(name).launch(1, memory = memory)(Arg.u64(base), Arg.u32(sourceValue), Arg.zeros(8))
        assertEquals(
          run.words(2),
          Vec(extended, sourceValue.toLong & 0xffffffffL),
          s"$name load extension and parameter"
        )
        assertEquals(
          memory.bytes(source),
          Vec.fill(64)(canary).updated(32, 0x80.toByte),
          s"$name changes only byte 0x20"
        )
      }
      for (name <- Vec("shared_u8", "local_u8")) {
        val run = kernel(name).launch(1, memory = memory)(Arg.zeros(8), Arg.u32(sourceValue))
        assertEquals(
          run.words(0),
          Vec(0x005a80a5L, sourceValue.toLong & 0xffffffffL),
          s"$name preserves canaries around byte 4"
        )
      }
    }

  test("retain an independent scalar bridge while a generic load crosses the 32-bit address boundary") {
    val function     = generic("AddressGlobalLoad", 60, "global_load").function
    val instructions = function.instructions
    val lowIndex     = instructions.indexWhere(is(_, "LEA"))
    val highIndex    = instructions.indexWhere(isHigh(_))
    val loadIndex    = instructions.indexWhere(is(_, "LD", "E"))
    val storeIndex   = instructions.indexWhere(is(_, "STG", "E"))
    assert(lowIndex >= 0 && highIndex == lowIndex + 1 && loadIndex > highIndex && storeIndex > loadIndex)
    val bridgeAddress                                    = instructions(lowIndex).address.raw + 4
    def bridged(bridge: String): Vec[DecodedInstruction] =
      instructions.patch(highIndex, Vec(at(bridgeAddress, bridge)), 0)
    Vec(
      s"MOV ${print(OperandQueries.base(instructions(lowIndex).operands.head))}, 0x1234",
      "IADD.X R22, R0, RZ"
    ).foreach {
      invalid =>
        val _ = intercept[IllegalArgumentException](rewritten(60, function.copy(instructions = bridged(invalid))))
    }
    val withBridge = bridged("MOV R22, 0x1234")
    val withResult =
      withBridge.patch(loadIndex + 2, Vec(at(withBridge(loadIndex + 1).address.raw + 4, "IADD R23, R22, R4")), 0)
    val observed = withResult.updated(
      storeIndex + 2,
      withResult(storeIndex + 2).rewrite(operands = withResult(storeIndex + 2).operands.updated(1, operand("R23")))
    )
    val kernel =
      generic("AddressGlobalLoad", 60, "global_load").copy(functions = Vec(function.copy(instructions = observed)))
    val memory = Memory()
    val target = memory.allocate(words(Vec(0x12345678L)))
    memory.rebase(target, GuestAddress(0x200000000L))
    assertEquals(
      kernel.launch(1, memory = memory)(Arg.u64(0x1fffffffcL), Arg.zeros(4)).words(1),
      Vec(0x12345678L + 0x1234L)
    )
  }

  for (sm <- Vec(50, 60))
    test(s"CUDA 11.8 sm_$sm generic vector widths and repeated P consumers preserve memory results") {
      def vector(name: String): Kernel   = generic(name.split('_').map(_.capitalize).mkString, sm, name)
      def compound(name: String): Kernel =
        generic(s"Compound${name.capitalize}", sm, s"compound_$name", if (name == "shared") 16 else 0)
      val memory = Memory()
      val base   = 0x400000000L + sm.toLong * 0x10000L
      val source = memory.allocate(Vec.fill(528)(0.toByte))
      memory.rebase(source, GuestAddress(base))
      def slice(offset: Int, length: Int): Vec[Byte] = memory.bytes(source).slice(offset, offset + length)
      val data32                                     = Vec(0x00000000L, 0x7fc00001L, 0x80000000L, 0xffffffffL)
      val data64                                     = Vec(0x0123456789abcdefL, 0xfedcba9876543210L)
      val _ = vector("store_v2_u32").launch(1, memory = memory)(Arg.u64(base), Arg.u32(data32(0)), Arg.u32(data32(1)))
      assertEquals(slice(offset = 512, length = 8), words(data32.take(2)))
      val _ = vector("store_v2_u64").launch(1, memory = memory)(Arg.u64(base), Arg.u64(data64(0)), Arg.u64(data64(1)))
      assertEquals(slice(offset = 512, length = 16), words(data64, 8))
      val _ = vector("store_v4_u32").launch(1, memory = memory)((Arg.u64(base) +: data32.map(Arg.u32(_)))*)
      assertEquals(slice(offset = 512, length = 16), words(data32))
      memory.write(source, 512L, words(data32)).value
      assertEquals(
        vector("load_v2_u32").launch(1, memory = memory)(Arg.u64(base), Arg.zeros(32)).words(1).take(2),
        data32.take(2)
      )
      assertEquals(
        vector("load_v4_u32").launch(1, memory = memory)(Arg.u64(base), Arg.zeros(32)).words(1).take(4),
        data32
      )
      memory.write(source, 512L, words(data64, 8)).value
      assertEquals(
        vector("load_v2_u64").launch(
          1,
          memory = memory
        )(Arg.u64(base), Arg.zeros(32)).words(argument = 1, width = 8).take(2),
        data64
      )
      val _ = vector("repeated_store_then_load").launch(1, memory = memory)(Arg.u64(base), Arg.u32(0x89abcdefL))
      assertEquals(
        slice(offset = 512, length = 4),
        words(Vec(0x89abcdefL)),
        "the aliased generic store writes offset 512"
      )
      assertEquals(
        slice(offset = 0, length = 4),
        words(Vec(0x89abcdefL)),
        "the later generic load feeds the output store"
      )
      for ((name, value) <- Vec("global" -> 0x01020304L, "shared" -> 0x7fc00001L, "local" -> 0xffffffffL)) {
        val arguments = Vec(Arg.zeros(4), Arg.u32(value))
        val run       =
          compound(name).launch(1, memory = memory)((if (name == "global") Arg.u64(base) +: arguments else arguments)*)
        assertEquals(
          run.words(if (name == "global") 1 else 0),
          Vec(if (name == "global") value else (value + value) & 0xffffffffL),
          name
        )
      }
    }

  for (sm <- Vec(50, 60))
    test(s"sm_$sm generic memory extension rejects a clobbered address between reused-predicate consumers") {
      val function  = generic("CompoundGlobal", sm, "compound_global").function
      val consumers = function.instructions.indices.filter { index =>
        val i = function.instructions(index)
        (is(i, "LD", "E", "CG") || is(i, "ST", "E", "CG")) && i.operands.lastOption.exists {
          case Operand.Predicate(Some(_), false, false) => true
          case _                                        => false
        }
      }
      assertEquals(consumers.size, 2)
      val low = function.instructions(function.instructions.indexWhere(is(_, "LEA"))).operands.head match {
        case Operand.Qualified(register, selectors) if selectors.contains(OperandSelector.CC) => register
        case register                                                                         => register
      }
      val clobber = at(function.instructions(consumers.last).address.raw - 1, s"MOV ${print(low)}, RZ")
      val _       = intercept[IllegalArgumentException](rewritten(
        sm,
        function.copy(instructions = function.instructions.patch(consumers.last, Vec(clobber), 0))
      ))
      val vector = generic("StoreV4U32", sm, "store_v4_u32").function
      val width  = vector.instructions.indexWhere(is(_, "ST", "E", "CG", "128"))
      val wide   = vector.instructions(width)
      val _      = intercept[IllegalArgumentException](rewritten(
        sm,
        vector.copy(instructions =
          vector.instructions.updated(
            width,
            wide.rewrite(modifiers = wide.modifiers.updated(wide.modifiers.size - 1, Instruction.Modifier("256")))
          )
        )
      ))
    }

  for (sm <- Vec(50, 60)) test(s"sm_$sm a stack-bounds trap before a generic access ends its path") {
    check(
      sm,
      "MOV R1, c[0x0][0x20]; IADD32I R1, R1, -0x30; S2R R0, SR_LMEMHIOFF; ISETP.GE.U32.AND P0, PT, R1, R0, PT; " +
        "@P0 BRA 0x60; BPT.TRAP 0x1; " +
        "LDC.64 R2, c[0x0][0x160]; MOV R4, 42; LEA R8.CC, R2, RZ; LEA.HI.X P0, R9, R2, RZ, R3; ST.E [R8], R4, P0; EXIT",
      Vec(42)
    )
  }

  for {
    sm     <- Vec(50, 60)
    branch <- Vec("BRA.CC.EQ 0x70", "BRA CC.EQU, 0x70")
  } test(s"sm_$sm the auxiliary predicate proof follows the fallthrough of an unguarded $branch") {
    assertRejects(
      program(
        sm,
        "LDC.64 R2, c[0x0][0x160]; MOV R4, 42; LEA R8.CC, R2, RZ; LEA.HI.X P0, R9, R2, RZ, R3; ST.E [R8], R4, P0; " +
          s"$branch; SEL R5, RZ, 0x1, P0; EXIT"
      ).function,
      sm
    )
  }

  for (sm <- Vec(50, 60)) test(s"sm_$sm the auxiliary predicate proof follows calls and their returns") {
    check(
      sm,
      "LDC.64 R2, c[0x0][0x160]; MOV R4, 42; LEA R8.CC, R2, RZ; LEA.HI.X P0, R9, R2, RZ, R3; ST.E [R8], R4, P0; " +
        "CAL 0x70; EXIT; MOV R5, R4; RET",
      Vec(42)
    )
  }

  for (sm <- Vec(50, 60)) test(s"sm_$sm the auxiliary predicate proof follows indirect calls into function entries") {
    check(
      sm,
      "LDC.64 R2, c[0x0][0x160]; MOV R4, 42; MOV R8, 0x60; PRET 0x50; BRX R8, -0x48; EXIT; " +
        "LEA R6.CC, R2, RZ; LEA.HI.X P0, R7, R2, RZ, R3; ST.E [R6], R4, P0; RET; BRA 0xa0; NOP; NOP",
      Vec(42)
    )
  }

  for (sm <- Vec(50, 60)) test(s"sm$sm generic negative offset preserves constant high addend and carry") {
    val kernel = program(
      sm,
      "LDC.64 R2, c[0x0][0x140]; MOV R4, 12345; STG.E [R2], R4; IADD R6.CC, R2, 256; IADD.X R7, R3, RZ; LEA R8.CC, R6, -256; " +
        "LEA.HI.X P0, R9, R6, c[0x2][0x0], R7; LD.E R10, [R8], P0; ISETP.EQ.AND P0, PT, RZ, RZ, PT; STG.E [R2], R10; EXIT",
      base = 0x140
    ).copy(constants = Vec(".nv.constant2.kernel" -> LittleEndian.toVec(-1L, 4)))
    assertEquals(kernel.launch(1)(Arg.zeros(4)).words(0), Vec(12345L))
  }

  for (sm <- Vec(50, 60))
    test(s"sm_$sm complementary guarded address producers preserve separate lane values") {
      check(
        sm,
        """S2R R0, SR_TID.X
          |LDC.64 R10, c[0x0][0x160]
          |ISCADD R10.CC, R0, R10, 2
          |IADD.X R11, R11, RZ
          |IADD R4, R0, 10
          |STG.E [R10], R4
          |ISETP.LT.U32.AND P0, PT, R0, 2, PT
          |@P0 LEA R12.CC, R10, RZ
          |@P0 LEA.HI.X P1, R13, R10, RZ, R11
          |@!P0 LEA R14.CC, R10, RZ
          |@P0 LD.E.CG R4, [R12], P1
          |@!P0 LEA.HI.X P1, R15, R10, RZ, R11
          |@!P0 LD.E.CG R4, [R14], P1
          |ISETP.EQ.AND P0, PT, RZ, RZ, PT
          |ISETP.EQ.AND P1, PT, RZ, RZ, PT
          |IADD R4, R4, 1
          |STG.E [R10], R4
          |EXIT""".stripMargin,
        Vec(11, 12, 13, 14)
      )
    }

  for (sm <- Vec(50, 60))
    test(s"sm_$sm generic loads retain independent address arithmetic and the decoded LU cache alias") {
      check(
        sm,
        """LDC.64 R10, c[0x0][0x160]
          |MOV R6, 40
          |STG.E [R10], R6
          |LEA R12.CC, R10, RZ
          |MOV R11, R11
          |XMAD R7, RZ, RZ, RZ
          |FSETP.EQ.AND P2, PT, RZ, RZ, PT
          |LEA.HI.X P1, R13, R10, RZ, R11
          |ISETP.EQ.AND P2, PT, RZ, RZ, PT
          |ISCADD R4.CC, RZ, 2, 2
          |IADD.X R5, RZ, RZ
          |LEA R14.CC, R10, RZ
          |LEA.HI.X P2, R15, R10, RZ, R11
          |LD.E.LU R6, [R12], P1
          |LD.E.LU R7, [R14], P2
          |ISETP.EQ.AND P2, PT, RZ, RZ, PT
          |LOP.AND.NZ P1, RZ, R6, 42
          |SEL R6, R6, RZ, P1
          |IADD R6, R6, R4
          |STG.E [R10], R6
          |EXIT""".stripMargin,
        Vec(42)
      )
    }

  for (sm <- Vec(50, 60)) {
    test(s"sm$sm retains independent explicit loads between generic address halves") {
      def function(destination: String, opcode: String = "LDG.E"): DecodedFunction = DecodedFunction(
        "binary_search",
        Vec(
          at(0, "@P0 LEA R8.CC, R4, RZ"),
          at(8, s"@P0 $opcode $destination, [R2]"),
          at(16, "@P0 LEA.HI.X P2, R9, R4, RZ, R15"),
          at(24, "@P0 LD.E R8, [R8], P2"),
          at(32, "EXIT")
        )
      )
      assertRewrites(function("R5"), sm)
      assertEquals(rewritten(sm, function("R5")).instructions(1), function("R5").instructions(1))
      Vec("R4", "R8").foreach(destination => assertRejects(function(destination), sm))
      assertRejects(function("R3", "LDG.E.64"), sm)
      assertRewrites(function("R14", "LDG.E.64"), sm)
    }

    test(s"sm$sm retains scoreboard and block barriers before generic memory consumption") {
      val function = DecodedFunction(
        "scan_by_key_barriers",
        Vec(
          at(0, "LEA R8.CC, R5, RZ"),
          at(8, "LEA.HI.X P0, R9, R5, RZ, R16"),
          at(16, "DEPBAR.LE SB5, 3"),
          at(24, "STS [R29], R7"),
          at(32, "LDS.U.32 R22, [R26]"),
          at(40, "BAR.SYNC 0"),
          at(48, "MEMBAR.CTA"),
          at(56, "LD.E R14, [R8], P0"),
          at(64, "EXIT")
        )
      )
      assertRewrites(function, sm)
      val guarded = function.copy(instructions =
        function.instructions.updated(
          5,
          at(40, "@P0 BAR.SYNC 0")
        )
      )
      assertRejects(guarded, sm)
      val clobbered = function.copy(instructions =
        function.instructions.updated(
          4,
          at(32, "LDS.U.64 R8, [R26]")
        )
      )
      assertRejects(clobbered, sm)
    }

    test(s"sm$sm deadness follows a guarded predicate replacement on the consuming lanes") {
      def function(changedGuard: Boolean): DecodedFunction = DecodedFunction(
        "guarded_replacement",
        Vec(
          at(0, "LEA R8.CC, R4, RZ"),
          at(8, "LEA.HI.X P2, R9, R4, RZ, R15"),
          at(16, "LD.E R16, [R8], P2"),
          at(24, "BRA 0x20"),
          at(32, "@!P0 ISETP.EQ.AND P2, PT, RZ, RZ, PT"),
          if (changedGuard)
            at(40, "ISETP.EQ.AND P0, PT, RZ, RZ, PT")
          else at(40, "NOP"),
          at(48, "@!P0 SEL R18, RZ, RZ, P2"),
          at(56, "ISETP.EQ.AND P2, PT, RZ, RZ, PT"),
          at(64, "EXIT")
        )
      )
      assertRewrites(function(false), sm)
      assertRejects(function(true), sm)
      val original = function(false)
      Vec(Some("@P0"), None).foreach { guard =>
        val escaping = original.copy(instructions =
          original.instructions.updated(
            6,
            at(48, s"${guard.fold("")(_ + " ")}SEL R18, RZ, RZ, P2")
          )
        )
        assertRejects(escaping, sm)
      }
      Vec(
        at(40, "SHFL.IDX P0, R17, R24, 31, 31"),
        at(40, "LOP3.LUT.NZ P0, R17, R24, R25, R26, 0x80")
      ).foreach { overwrite =>
        assertRejects(original.copy(instructions = original.instructions.updated(5, overwrite)), sm)
      }
      val entered = original.copy(instructions =
        original.instructions :+
          at(72, "BRA 0x30")
      )
      assertRejects(entered, sm)
    }

    test(s"sm$sm keeps shuffle and logic effects separate from auxiliary predicate uses") {
      def function(destination: String): DecodedFunction = DecodedFunction(
        "scan_shuffle",
        Vec(
          at(0, "LEA R8.CC, R4, RZ"),
          at(8, "LEA.HI.X P2, R9, R4, RZ, R15"),
          at(16, s"SHFL.IDX PT, $destination, R24, 31, 31"),
          at(24, "LOP32I.AND R22, R18, 31"),
          at(32, "LD.E R16, [R8], P2"),
          at(40, "SHFL.IDX P2, R17, R24, 31, 31"),
          at(48, "SEL R18, RZ, RZ, P2"),
          at(56, "EXIT")
        )
      )
      assertRewrites(function("R17"), sm)
      Vec("R8", "R9").foreach(destination => assertRejects(function(destination), sm))
    }

    test(s"sm$sm preserves generic address provenance through a diamond") {
      def function(bridge: DecodedInstruction, bypass: Boolean = false): DecodedFunction = DecodedFunction(
        "generic_diamond",
        Vec(
          if (bypass) at(0, "@P3 BRA 0x40")
          else at(0, "NOP"),
          at(8, "LEA R8.CC, R4, RZ"),
          at(16, "LEA.HI.X P2, R9, R4, RZ, R15"),
          at(24, "ST.E.64 [R8], R16, P2"),
          at(32, "@P0 BRA 0x38"),
          bridge,
          at(48, "BRA 0x40"),
          at(56, "NOP"),
          at(64, "ST.E.64 [R8], R18, P2"),
          at(72, "EXIT")
        )
      )
      val independent = at(40, "MOV R18, RZ")
      assertRewrites(function(independent), sm)
      assertRejects(function(independent, bypass = true), sm)
      assertRejects(function(at(40, "MOV R8, RZ")), sm)
      assertRejects(
        function(at(40, "ISETP.EQ.AND P2, PT, RZ, RZ, PT")),
        sm
      )
      val guarded = function(independent).copy(instructions =
        function(independent).instructions.updated(
          8,
          at(64, "@P2 ST.E.64 [R8], R18, P2")
        )
      )
      assertRejects(guarded, sm)
    }

    test(s"sm$sm reconvergence setup preserves the generic address carry dependency") {
      def function(setup: String): DecodedFunction = DecodedFunction(
        "carry_setup",
        Vec(
          at(0, "LEA R8.CC, R4, RZ"),
          at(8, s"$setup 0x28"),
          at(16, "LEA.HI.X P2, R9, R4, RZ, R15"),
          at(24, "ST.E.64 [R8], R16, P2"),
          at(32, "SYNC"),
          at(40, "EXIT")
        )
      )
      assertRewrites(function("SSY"), sm)
      assertRejects(function("BRA"), sm)
    }

    test(s"sm$sm break edges retain predicate provenance at their join") {
      def function(replace: Boolean): DecodedFunction = DecodedFunction(
        "break_join",
        Vec(
          at(0, "PBK 0x40"),
          at(8, "LEA R8.CC, R4, RZ"),
          at(16, "LEA.HI.X P2, R9, R4, RZ, R15"),
          at(24, "ST.E.64 [R8], R16, P2"),
          at(32, "@P0 BRA 0x38"),
          if (replace) at(40, "ISETP.EQ.AND P2, PT, RZ, RZ, PT")
          else at(40, "MOV R18, RZ"),
          at(48, "BRK"),
          at(56, "BRK"),
          at(64, "ST.E.64 [R8], R18, P2"),
          at(72, "EXIT")
        )
      )
      assertRewrites(function(false), sm)
      assertRejects(function(true), sm)
    }

    test(s"sm$sm generic memory accepts uncached scalar and vector widths") {
      for (suffix <- Vec("U8", "S8", "U16", "S16", "64", "128")) {
        val function = DecodedFunction(
          "generic_width",
          Vec(
            at(0, "LEA R8.CC, R4, RZ"),
            at(8, "LEA.HI.X P2, R9, R4, RZ, R15"),
            at(16, s"LD.E.$suffix R16, [R8], P2"),
            at(24, "EXIT")
          )
        )
        assertRewrites(function, sm)
      }
    }

    test(s"sm$sm generic address proof retains independent legacy logical and extrema results") {
      for (
        (opcode, operands) <- Vec(
          "LOP.AND.NZ" -> Vec("P0", "R16", "R6", "1"),
          "LOP3.LUT"   -> Vec("R16", "R6", "R7", "R10", "0xfe"),
          "IMNMX"      -> Vec("R16", "R6", "R7", "!PT"),
          "MEMBAR.CTA" -> Vec.empty[String]
        )
      ) {
        val function = DecodedFunction(
          "independent_effects",
          Vec(
            at(0, "LEA R8.CC, R4, RZ"),
            at(8, s"$opcode ${operands.mkString(", ")}"),
            at(16, "LEA.HI.X P2, R9, R4, RZ, R15"),
            at(24, s"$opcode ${operands.mkString(", ")}"),
            at(32, "ST.E [R8], R16, P2"),
            at(40, "EXIT")
          )
        )
        assertRewrites(function, sm)
        if (operands.nonEmpty) {
          val destination = if (opcode == "LOP.AND.NZ") 1 else 0
          val clobber     = at(24, s"$opcode ${operands.updated(destination, "R8").mkString(", ")}")
          assertRejects(function.copy(instructions = function.instructions.updated(3, clobber)), sm)
        }
      }
    }

    test(s"sm$sm generic address halves preserve shared sources and permit high-only initialisation") {
      val function = DecodedFunction(
        "initialised_high",
        Vec(
          at(0, "LEA R8.CC, R4, RZ"),
          at(8, "MOV R15, RZ"),
          at(16, "LEA.HI.X P2, R9, R4, RZ, R15"),
          at(24, "LD.E R8, [R8], P2"),
          at(32, "EXIT")
        )
      )
      assertRewrites(function, sm)
      assertRejects(
        function.copy(instructions =
          function.instructions.updated(
            1,
            at(8, "MOV R4, RZ")
          )
        ),
        sm
      )
      assertRewrites(
        function.copy(instructions =
          function.instructions.updated(
            1,
            at(8, "MOV R4, R4")
          )
        ),
        sm
      )
    }
  }

  test("sm60 generic address proof permits independent IADD3 while preserving address and carry dependencies") {
    def function(destination: String, opcode: String = "IADD3"): DecodedFunction = DecodedFunction(
      "scan_by_key",
      Vec(
        at(0, "@!P1 LEA R8.CC, R6, 0x200"),
        at(8, s"$opcode $destination, R16, R5, R14"),
        at(16, "@!P1 LEA.HI.X P3, R9, R6, RZ, R7"),
        at(24, "IADD3 R5, R20, R5, R18"),
        at(32, "@!P1 ST.E.CG.128 [R8], R4, P3"),
        at(40, "EXIT")
      )
    )
    assertRewrites(function("R5"), 60)
    Vec("R6", "R8", "R5.CC").foreach(destination => assertRejects(function(destination), 60))
    assertRejects(function("R5", "IADD3.X"), 60)
  }

  test("sm60 generic address proof retains independent extended integer comparisons") {
    def function(destination: String): DecodedFunction = DecodedFunction(
      "copy_if",
      Vec(
        at(0, "@!P0 LEA R2.CC, R3, 0x100"),
        at(8, "@!P0 LEA.HI.X P5, R3, R3, RZ, R5"),
        at(16, "IADD RZ.CC, -RZ, R31"),
        at(24, s"ISETP.NE.X.AND $destination, PT, R4, RZ, PT"),
        at(32, "@!P0 ST.E.CG.64 [R2], R6, P5"),
        at(40, "EXIT")
      )
    )
    assertRewrites(function("P2"), 60)
    Vec("P0", "P5").foreach(destination => assertRejects(function(destination), 60))
  }

  for (crossing <- Vec(false, true))
    test(s"guarded address proof rejects changed guards and external entry: branch=$crossing") {
      val producer = Vec(
        at(0, "@P0 LEA R12.CC, R10, RZ"),
        at(8, "@P0 LEA.HI.X P1, R13, R10, RZ, R11"),
        at(16, "@P0 LD.E.CG R4, [R12], P1"),
        at(24, "@!P0 SEL R4, RZ, RZ, P1"),
        at(32, "ISETP.EQ.AND P1, PT, RZ, RZ, PT"),
        at(40, "EXIT")
      )
      val entered = if (crossing) producer :+ at(48, "BRA 0x18")
      else producer.take(3) ++
        Vec(at(20, "ISETP.EQ.AND P0, PT, RZ, RZ, PT")) ++
        producer.drop(3)
      intercept[IllegalArgumentException](LegacyAddressRewrite(50, DecodedFunction("guarded", entered)))
    }

  for (sm <- Vec(50, 60))
    test(s"sm$sm generic address proof retains global memory invalidation barriers") {
      def function(guard: Option[String]): DecodedFunction = DecodedFunction(
        "scan",
        Vec(
          at(0, "LEA R2.CC, R3, 0x100"),
          at(8, "LEA.HI.X P5, R3, R3, RZ, R5"),
          at(16, s"${guard.fold("")(_ + " ")}MEMBAR.GL.IVALLD"),
          at(24, "ST.E.CG.U8 [R2], R6, P5"),
          at(32, "EXIT")
        )
      )
      assertRewrites(function(None), sm)
      assertRejects(function(Some("@P5")), sm)
    }

  for (sm <- Vec(50, 60))
    test(s"sm$sm generic address proof permits independent special-register reads") {
      def function(destination: String): DecodedFunction = DecodedFunction(
        "scan",
        Vec(
          at(0, "LEA R2.CC, R3, 0x100"),
          at(8, "LEA.HI.X P5, R3, R3, RZ, R5"),
          at(16, s"S2R $destination, SR_LANEID"),
          at(24, "ST.E.CG [R2], R6, P5"),
          at(32, "EXIT")
        )
      )
      assertRewrites(function("R7"), sm)
      Vec("R2", "R3").foreach(destination => assertRejects(function(destination), sm))
    }

  for (sm <- Vec(50, 60)) {
    test(s"sm$sm retains a predicated shared store without exposing the auxiliary predicate") {
      def function(guard: String): DecodedFunction = DecodedFunction(
        "guarded_store",
        Vec(
          at(0, "@!P1 LEA R8.CC, R6, RZ"),
          at(8, "@!P1 LEA.HI.X P2, R9, R6, RZ, R7"),
          at(16, s"$guard STS [0x2c], R15"),
          at(24, "@!P1 ST.E.CG.64 [R8], R14, P2"),
          at(32, "EXIT")
        )
      )
      assertRewrites(function("@!P1"), sm)
      assertRejects(function("@P2"), sm)
    }

    test(s"sm$sm unconditional proven SYNC skips another path's auxiliary predicate read") {
      def function(guard: Option[String]): DecodedFunction = DecodedFunction(
        "sync_paths",
        Vec(
          at(0, "SSY 0x40"),
          at(8, "@P1 BRA 0x30"),
          at(16, "LEA R8.CC, R6, RZ"),
          at(24, "LEA.HI.X P2, R9, R6, RZ, R7"),
          at(32, "ST.E.CG.64 [R8], R14, P2"),
          at(40, s"${guard.fold("")(_ + " ")}SYNC"),
          at(48, "SEL R12, R6, R7, P2"),
          at(56, "SYNC"),
          at(64, "ISETP.EQ.AND P2, PT, RZ, RZ, PT"),
          at(72, "EXIT")
        )
      )
      assertRewrites(function(None), sm)
      assertRejects(function(Some("@P3")), sm)
    }
  }

  /** Instructions following the global load's memory consumer, 8 bytes apart, plus the printed auxiliary predicate. */
  private def tail(sm: Int)(build: (Int, String) => Vec[DecodedInstruction]): DecodedFunction = {
    val function    = generic("AddressGlobalLoad", sm, "global_load").function
    val memoryIndex = function.instructions.indexWhere(is(_, "LD", "E"))
    val highIndex   = function.instructions.indexWhere(isHigh(_))
    assert(highIndex >= 0 && highIndex < memoryIndex)
    val start = function.instructions(memoryIndex).address.raw + 8
    function.copy(instructions =
      function.instructions.take(memoryIndex + 1) ++ build(start, print(function.instructions(highIndex).operands.head))
    )
  }

  private def body(start: Int, statements: String*): Vec[DecodedInstruction] =
    Vec.from(statements).zipWithIndex.map((text, index) => at(start + index * 8, text))

  for (sm <- Vec(50, 60)) test(s"sm_$sm distinguishes vote predicate outputs from inputs") {
    for (prefix <- Vec("", "RZ, ")) {
      assertRewrites(
        tail(sm)((start, aux) => body(start, s"VOTE.ANY $prefix$aux, PT", s"@$aux MOV R8, RZ", "EXIT")),
        sm
      )
      assertRejects(tail(sm)((start, aux) => body(start, s"VOTE.ANY ${prefix}PT, $aux", "EXIT")), sm)
    }
  }

  for (sm <- Vec(50, 60))
    test(s"sm_$sm permits control-flow paths only when auxiliary P is dead on every successor") {
      def kill(aux: String): String = s"ISETP.EQ.AND $aux, PT, RZ, RZ, PT"
      def hex(address: Int): String = s"0x${address.toHexString}"
      // both sides of the conditional branch reach the unconditional definition
      assertRewrites(tail(sm)((start, aux) => body(start, s"@P1 BRA ${hex(start + 16)}", "NOP", kill(aux), "EXIT")), sm)
      // a later independently verified address sequence replaces the old auxiliary value
      val original = generic("AddressGlobalLoad", sm, "global_load").function.instructions
      val sequence = Vec(
        original.indexWhere(isHigh(_)) - 1,
        original.indexWhere(isHigh(_)),
        original.indexWhere(is(_, "LD", "E"))
      ).map(original(_))
      assertRewrites(
        tail(sm)((start, _) =>
          sequence.zipWithIndex.map((i, index) => i.copy(address = (start + index * 8).unsafe[Int >= 0])) :+
            at(start + 24, "EXIT")
        ),
        sm
      )
      // both SYNC successors, fallthrough and the SSY target, reach the kill
      assertRewrites(tail(sm)((start, aux) => body(start, s"SSY ${hex(start + 16)}", "SYNC", kill(aux), "EXIT")), sm)
      // one branch successor reads the old P before the kill; an EXIT on the other path is safe
      assertRejects(
        tail(sm)((start, aux) =>
          body(start, s"@P1 BRA ${hex(start + 24)}", "EXIT", "NOP", s"@$aux MOV R8, RZ", kill(aux), "EXIT")
        ),
        sm
      )
      // SYNC may reconverge at the alternate SSY target, so checking only its fallthrough is unsafe
      assertRejects(
        tail(sm)((start, aux) =>
          body(start, s"SSY ${hex(start + 32)}", "SYNC", kill(aux), "EXIT", s"@$aux MOV R8, RZ", kill(aux), "EXIT")
        ),
        sm
      )
      // a branch can skip the inner SSY, so the outer reconvergence target stays reachable from SYNC
      assertRejects(
        tail(sm)((start, aux) =>
          body(
            start,
            s"SSY ${hex(start + 64)}",
            s"@P1 BRA ${hex(start + 32)}",
            s"SSY ${hex(start + 48)}",
            s"BRA ${hex(start + 32)}",
            "SYNC",
            kill(aux),
            kill(aux),
            "EXIT",
            s"@$aux MOV R8, RZ",
            "EXIT"
          )
        ),
        sm
      )
      // a dominating inner setup stays active after a false predicated SYNC; the outer target is not a successor
      def nested(third: String): DecodedFunction = tail(sm)((start, aux) =>
        body(
          start,
          s"SSY ${hex(start + 64)}",
          s"SSY ${hex(start + 40)}",
          third,
          "SYNC",
          "NOP",
          kill(aux),
          "EXIT",
          "NOP",
          s"@$aux MOV R8, RZ",
          "EXIT"
        )
      )
      assertRewrites(nested("@P1 SYNC"), sm)
      // reentry while the token is still active
      assertRejects(
        tail(sm)((start, aux) =>
          body(
            start,
            s"SSY ${hex(start + 64)}",
            s"SSY ${hex(start + 40)}",
            s"@P1 BRA ${hex(start + 8)}",
            "SYNC",
            "NOP",
            kill(aux),
            "EXIT",
            "NOP",
            s"@$aux MOV R8, RZ",
            "EXIT"
          )
        ),
        sm
      )
      Vec(
        // branching on the auxiliary predicate exposes its value
        (start: Int, aux: String) => body(start, s"@$aux BRA ${hex(start + 16)}", kill(aux), "EXIT"),
        // P2R observes predicate state outside an explicit predicate operand
        (start: Int, aux: String) => body(start, "P2R R8, PR, RZ, 0x7f", kill(aux), "EXIT"),
        // a predicated definition is not a definite kill on paths where its guard is false
        (start: Int, aux: String) => body(start, s"@P1 ${kill(aux)}", s"@$aux MOV R8, RZ", kill(aux), "EXIT"),
        // unknown direct targets after the memory consumer
        (start: Int, aux: String) => body(start, "BRA 0xdeadbeef", kill(aux), "EXIT"),
        // legacy .S suffixes transfer control, so the following definition is not a guaranteed kill
        (start: Int, aux: String) => body(start, "NOP.S", kill(aux), "EXIT")
      ).foreach(build => assertRejects(tail(sm)(build), sm))
    }
}
