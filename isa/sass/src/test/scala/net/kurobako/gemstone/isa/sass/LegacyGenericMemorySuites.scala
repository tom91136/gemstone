package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

/** Compiler-backed Maxwell/Pascal generic-memory form with an unobservable LEA auxiliary predicate. */

class LegacyGenericMemorySuites extends munit.FunSuite {
  private def hasInstruction(
      instruction: DecodedInstruction,
      mnemonic: String,
      modifiers: Vec[Instruction.Modifier] = Vec.empty
  ): Boolean = instruction.mnemonic.raw == mnemonic && instruction.modifiers == modifiers

  private def typedInstruction(
      address: Int,
      mnemonic: String,
      operands: Vec[Operand],
      guard: Option[Instruction.Guard] = None
  ): DecodedInstruction = {
    val parts = mnemonic.split('.').toVec
    DecodedInstruction(
      address.unsafe[Int >= 0],
      Instruction.decode(parts.head, parts.drop(1).map(Instruction.Modifier(_)), operands, guard)
        .fold(error => fail(error), identity),
      None,
      None
    )
  }

  {
    val addressKernels = Vec(
      SASSFixture.Kernel("local_store", 320, Vec((0, 8), (8, 4)), registers = 7),
      SASSFixture.Kernel("local_load", 320, Vec((0, 8), (8, 4)), registers = 8),
      SASSFixture.Kernel("shared_store", 320, Vec((0, 8), (8, 4)), registers = 7, shared = 16),
      SASSFixture.Kernel("shared_load", 320, Vec((0, 8), (8, 4)), registers = 6, shared = 16),
      SASSFixture.Kernel("global_store", 320, Vec((0, 8), (8, 4)), registers = 4),
      SASSFixture.Kernel("global_load", 320, Vec((0, 8), (8, 8)), registers = 6)
    )
    def fixtureModule(sm: Int): Cubin.Module = SASSFixture.module(sm, addressKernels)

    val values      = Vec(0x00000000, 0x7fc00001, 0x80000000, 0xffffffff, 0x12345678)
    val carryBase   = 0x1fffffffcL
    val carryTarget = 0x200000000L

    def rewrite(sm: Int, function: DecodedFunction): DecodedFunction =
      VMAddressAnalysis.rewrite(sm, function)

    def isHigh(instruction: DecodedInstruction): Boolean =
      instruction.mnemonic.raw == "LEA" && instruction.modifiers == Vec(Instruction.Modifier.HI, Instruction.Modifier.X)

    def guard(text: String): Option[Instruction.Guard] =
      Parser.parse(s"$text NOP;").fold(error => fail(error), _.guard)

    def isMemory(instruction: DecodedInstruction): Boolean =
      Set("LD", "ST")(instruction.mnemonic.raw) &&
        instruction.modifiers.headOption.contains(Instruction.Modifier.E)

    def scalar32(value: Int): KernelTask.Argument.Scalar =
      KernelTask.Argument.Scalar(LittleEndian.toVec(value.toLong & 0xffffffffL, 4))

    def scalar64(value: Long): KernelTask.Argument.Scalar =
      KernelTask.Argument.Scalar(LittleEndian.toVec(value, 8))

    def memory(id: AllocationId): KernelTask.Argument.Memory =
      KernelTask.Argument.Memory(id, 0L)

    def intAt(bytes: Vec[Byte]): Int =
      LittleEndian.decode(bytes, 0, 4).toInt

    def run(
        service: SASSFixture.Execution,
        program: LoadedProgram.Compiled,
        name: String,
        args: Vec[KernelTask.Argument]
    ): Unit = {
      val task = KernelTask.validated(
        name.unsafe[NonEmpty[String]],
        LaunchGeometry.Dimensions.One,
        LaunchGeometry.Dimensions.One,
        Vec.from(args)
      ).fold(fail(_), identity)
      service.run(program, task).fold(error => fail(error.message), identity)
    }

    for (sm <- Vec(50, 60))
      test(s"sm_$sm generic-address rewrite rejects escaped predicates, mismatched guards, and addresses") {
        val function = ListingDecoder.parse(
          if (sm == 50) GenericMemoryFixtures.legacyGenericAddressGlobalLoadSm50
          else GenericMemoryFixtures.legacyGenericAddressGlobalLoadSm60,
          "global_load",
          sm
        ).fold(fail(_), _.head)
        val highIndex = function.instructions.indexWhere(hasInstruction(
          _,
          "LEA",
          Vec(Instruction.Modifier.HI, Instruction.Modifier.X)
        ))
        val memoryIndex = function.instructions.indexWhere(hasInstruction(_, "LD", Vec(Instruction.Modifier.E)))
        assert(highIndex >= 0 && memoryIndex > highIndex)
        val high      = function.instructions(highIndex)
        val memory    = function.instructions(memoryIndex)
        val predicate = high.operands.head

        val implicitPredicateRead = SASSFixture.instruction(
          memory.address.raw + 1,
          "P2R",
          Vec("R8", "PR", "RZ", "0x7f"),
          None
        )
        val withPredicateBankRead = function.copy(instructions =
          function.instructions.patch(
            memoryIndex + 1,
            Vec(implicitPredicateRead),
            0
          )
        )
        val _ = intercept[IllegalArgumentException](rewrite(sm, withPredicateBankRead))

        val enteringBranch = SASSFixture.instruction(
          high.address.raw - 1,
          "BRA",
          Vec(s"0x${high.address.raw.toHexString}"),
          None
        )
        val withEnteringBranch = function.copy(instructions = enteringBranch +: function.instructions)
        val _                  = intercept[IllegalArgumentException](rewrite(sm, withEnteringBranch))

        val clobberAddress = typedInstruction(
          high.address.raw + 1,
          "MOV",
          Vec(high.operands(1), SASSFixture.operand("RZ"))
        )
        val withAddressClobber = function.copy(instructions =
          function.instructions.patch(
            highIndex + 1,
            Vec(clobberAddress),
            0
          )
        )
        val _ = intercept[IllegalArgumentException](rewrite(sm, withAddressClobber))

        val wrongPredicate = function.copy(instructions =
          function.instructions.updated(
            memoryIndex,
            memory.rewrite(operands = memory.operands.updated(memory.operands.size - 1, SASSFixture.operand("P1")))
          )
        )
        val _ = intercept[IllegalArgumentException](rewrite(sm, wrongPredicate))

        val wrongAddress = function.copy(instructions =
          function.instructions.updated(
            memoryIndex,
            memory.rewrite(operands = memory.operands.updated(1, SASSFixture.operand("[RZ]")))
          )
        )
        val _ = intercept[IllegalArgumentException](rewrite(sm, wrongAddress))

        val wrongHighPair = function.copy(instructions =
          function.instructions.updated(
            highIndex,
            high.rewrite(operands = high.operands.updated(1, SASSFixture.operand("R7")))
          )
        )
        val _ = intercept[IllegalArgumentException](rewrite(sm, wrongHighPair))

        val negatedPredicate = predicate match {
          case value: Operand.Predicate => value.copy(negated = !value.negated)
          case other                    => fail(s"expected auxiliary predicate, found $other")
        }
        val negatedEscape = typedInstruction(
          memory.address.raw + 8,
          "SEL",
          Vec(
            SASSFixture.operand("R8"),
            SASSFixture.operand("RZ"),
            Operand.Bits(BigInt(1), 0),
            negatedPredicate
          )
        )
        val withNegatedEscape =
          function.copy(instructions = function.instructions.patch(memoryIndex + 1, Vec(negatedEscape), 0))
        val _ = intercept[IllegalArgumentException](rewrite(sm, withNegatedEscape))

        val guardedOverwrite = typedInstruction(
          high.address.raw + 8,
          "ISETP.EQ.AND",
          Vec(
            predicate,
            SASSFixture.operand("PT"),
            SASSFixture.operand("RZ"),
            SASSFixture.operand("RZ"),
            SASSFixture.operand("PT")
          ),
          Parser.parse("@P1 NOP;").toOption.get.guard
        )
        val withPredicatedOverwrite = function.copy(
          instructions = function.instructions.patch(highIndex + 1, Vec(guardedOverwrite), 0)
        )
        val _ = intercept[IllegalArgumentException](rewrite(sm, withPredicatedOverwrite))

        val afterConsumerBranch     = SASSFixture.instruction(memory.address.raw + 8, "BRA", Vec("0x100"), None)
        val withAfterConsumerBranch = function.copy(
          instructions = function.instructions.patch(memoryIndex + 1, Vec(afterConsumerBranch), 0)
        )
        val _ = intercept[IllegalArgumentException](rewrite(sm, withAfterConsumerBranch))

        val wrongGuard = function.copy(instructions =
          function.instructions.updated(
            memoryIndex,
            memory.rewrite(guard = guard("@P1"))
          )
        )
        val _ = intercept[IllegalArgumentException](rewrite(sm, wrongGuard))

        val escapedRead = typedInstruction(
          high.address.raw + 8,
          "SEL",
          Vec(SASSFixture.operand("R8"), SASSFixture.operand("RZ"), SASSFixture.operand("RZ"), predicate)
        )
        val observable = function.copy(instructions = function.instructions.patch(highIndex + 1, Vec(escapedRead), 0))
        val _          = intercept[IllegalArgumentException](rewrite(sm, observable))
      }

    for (sm <- Vec(50, 60))
      test(s"CUDA 11.8 sm_$sm generic global/shared/local load and store preserve all bits") {
        val module = fixtureModule(sm)
        assertEquals(module.sm.raw, sm)

        val listings = if (sm == 50) Vec(
          "global_load"  -> GenericMemoryFixtures.legacyGenericAddressGlobalLoadSm50,
          "global_store" -> GenericMemoryFixtures.legacyGenericAddressGlobalStoreSm50,
          "shared_load"  -> GenericMemoryFixtures.legacyGenericAddressSharedLoadSm50,
          "shared_store" -> GenericMemoryFixtures.legacyGenericAddressSharedStoreSm50,
          "local_load"   -> GenericMemoryFixtures.legacyGenericAddressLocalLoadSm50,
          "local_store"  -> GenericMemoryFixtures.legacyGenericAddressLocalStoreSm50
        )
        else Vec(
          "global_load"  -> GenericMemoryFixtures.legacyGenericAddressGlobalLoadSm60,
          "global_store" -> GenericMemoryFixtures.legacyGenericAddressGlobalStoreSm60,
          "shared_load"  -> GenericMemoryFixtures.legacyGenericAddressSharedLoadSm60,
          "shared_store" -> GenericMemoryFixtures.legacyGenericAddressSharedStoreSm60,
          "local_load"   -> GenericMemoryFixtures.legacyGenericAddressLocalLoadSm60,
          "local_store"  -> GenericMemoryFixtures.legacyGenericAddressLocalStoreSm60
        )
        val lowered = listings.map { case (name, listing) =>
          val kernel    = module.kernels.find(_.name == name).getOrElse(fail(s"missing $name"))
          val function  = ListingDecoder.parse(listing, name, sm).fold(fail(_), _.head)
          val high      = function.instructions.find(isHigh(_)).getOrElse(fail(s"$name lacks LEA.HI.X"))
          val auxiliary = high.operands.head match {
            case value @ Operand.Predicate(Some(_), false, false) => value
            case _                                                => fail(s"$name has an invalid auxiliary predicate")
          }
          val genericMemory = function.instructions.find(isMemory(_)).getOrElse(fail(s"$name lacks generic LD/ST"))
          assertEquals(genericMemory.operands.last, high.operands.head, name)
          assertEquals(function.instructions.count(isHigh(_)), 1, name)
          val rewritten     = VMAddressAnalysis.rewrite(sm, function)
          val rewrittenHigh = rewritten.instructions.find(isHigh(_)).getOrElse(fail(s"$name lost LEA.HI.X"))
          assertEquals(rewrittenHigh.operands.head, high.operands(1), name)
          val rewrittenMemory = rewritten.instructions.find(isMemory(_))
            .getOrElse(fail(s"$name lost generic memory instruction"))
          assert(!rewrittenMemory.operands.contains(auxiliary), name)
          val source = SASSFixture(module, kernel, Vec(function))
          name -> source
        }.toMap

        locally {
          val service  = SASSFixture.Execution()
          val programs = lowered.map { case (name, source) =>
            name -> service.compile(source).fold(error => fail(error.message), identity)
          }

          val target = service.allocate(8L)
          assertEquals(service.registerAllocationAddress(target, GuestAddress(carryTarget)), Right(()))
          val globalOutput = service.allocate(4L)
          values.foreach { value =>
            assert(service.write(
              target,
              0L,
              LittleEndian.toVec(value.toLong & 0xffffffffL, 4)
            ).isRight)
            run(
              service,
              programs("global_load"),
              "global_load",
              Vec(
                scalar64(carryBase),
                memory(globalOutput)
              )
            )
            assertEquals(
              intAt(service.read(globalOutput, 0L, 4).toOption.get),
              value,
              s"global load offset carry value=0x${value.toHexString}"
            )
          }

          values.foreach { value =>
            assert(service.write(
              target,
              0L,
              LittleEndian.toVec(0L, 4)
            ).isRight)
            run(service, programs("global_store"), "global_store", Vec(scalar64(carryBase), scalar32(value)))
            assertEquals(
              intAt(service.read(target, 0L, 4).toOption.get),
              value,
              s"global store offset carry value=0x${value.toHexString}"
            )
          }

          val spaceOutput = service.allocate(4L)
          for {
            space     <- Vec("shared", "local")
            operation <- Vec("load", "store")
            value     <- values
          } {
            run(
              service,
              programs(s"${space}_$operation"),
              s"${space}_$operation",
              Vec(memory(spaceOutput), scalar32(value))
            )
            assertEquals(
              intAt(service.read(spaceOutput, 0L, 4).toOption.get),
              value,
              s"$space generic $operation value=0x${value.toHexString}"
            )
          }

          val storeName     = "global_store"
          val storeKernel   = module.kernels.find(_.name == storeName).get
          val storeFunction = ListingDecoder.parse(
            if (sm == 50) GenericMemoryFixtures.legacyGenericAddressGlobalStoreSm50
            else GenericMemoryFixtures.legacyGenericAddressGlobalStoreSm60,
            storeName,
            sm
          ).fold(fail(_), _.head)
          val lowIndex  = storeFunction.instructions.indexWhere(hasInstruction(_, "LEA"))
          val highIndex = storeFunction.instructions.indexWhere(
            hasInstruction(_, "LEA", Vec(Instruction.Modifier.HI, Instruction.Modifier.X))
          )
          val memoryIndex = storeFunction.instructions.indexWhere(hasInstruction(_, "ST", Vec(Instruction.Modifier.E)))
          val guardedInstructions = storeFunction.instructions.zipWithIndex.map { case (instruction, index) =>
            if (Set(lowIndex, highIndex, memoryIndex)(index))
              instruction.rewrite(guard = guard("@P1"))
            else instruction
          }
          val falsePredicate = SASSFixture.instruction(
            storeFunction.instructions.head.address.raw - 8,
            "ISETP.EQ.AND",
            Vec("P1", "PT", "RZ", "0x1", "PT"),
            None
          )
          val guardedFunction = storeFunction.copy(instructions = falsePredicate +: guardedInstructions)
          assert(rewrite(sm, guardedFunction).instructions.exists(isHigh(_)))
          val guardedSource  = SASSFixture(module, storeKernel, Vec(guardedFunction))
          val guardedProgram = service.compile(guardedSource).fold(error => fail(error.message), identity)
          val guardedTarget  = target
          val sentinel       = 0x89abcdef
          assert(service.write(
            guardedTarget,
            0L,
            LittleEndian.toVec(sentinel.toLong & 0xffffffffL, 4)
          ).isRight)
          run(service, guardedProgram, storeName, Vec(scalar64(carryBase), scalar32(0x12345678)))
          assertEquals(
            intAt(service.read(guardedTarget, 0L, 4).toOption.get),
            sentinel,
            "inactive guard must suppress the generic store"
          )
          val truePredicateFunction = guardedFunction.copy(instructions =
            guardedFunction.instructions.updated(
              0,
              guardedFunction.instructions.head.rewrite(
                operands = guardedFunction.instructions.head.operands.updated(3, SASSFixture.operand("RZ"))
              )
            )
          )
          val truePredicateSource  = SASSFixture(module, storeKernel, Vec(truePredicateFunction))
          val truePredicateProgram = service.compile(truePredicateSource).fold(error => fail(error.message), identity)
          run(service, truePredicateProgram, storeName, Vec(scalar64(carryBase), scalar32(0x12345678)))
          assertEquals(
            intAt(service.read(guardedTarget, 0L, 4).toOption.get),
            0x12345678,
            "active guard must permit the generic store"
          )
        }
      }
  }

  {
    val byteKernels = Vec(
      SASSFixture.Kernel("local_u8", 320, Vec((0, 8), (8, 4)), registers = 12),
      SASSFixture.Kernel("shared_u8", 320, Vec((0, 8), (8, 4)), registers = 12, shared = 16),
      SASSFixture.Kernel("global_u8_barrier", 320, Vec((0, 8), (8, 4), (16, 8)), registers = 8),
      SASSFixture.Kernel("global_s8", 320, Vec((0, 8), (8, 4), (16, 8)), registers = 8),
      SASSFixture.Kernel("global_u8", 320, Vec((0, 8), (8, 4), (16, 8)), registers = 8)
    )
    def fixtureModule(sm: Int): Cubin.Module = SASSFixture.module(sm, byteKernels)

    def scalar32(value: Int): KernelTask.Argument.Scalar =
      KernelTask.Argument.Scalar(LittleEndian.toVec(value.toLong & 0xffffffffL, 4))

    def scalar64(value: Long): KernelTask.Argument.Scalar =
      KernelTask.Argument.Scalar(LittleEndian.toVec(value, 8))

    def memory(id: AllocationId): KernelTask.Argument.Memory =
      KernelTask.Argument.Memory(id, 0L)

    def run(
        service: SASSFixture.Execution,
        program: LoadedProgram.Compiled,
        name: String,
        args: Vec[KernelTask.Argument]
    ): Unit = {
      val task = KernelTask.validated(
        name.unsafe[NonEmpty[String]],
        LaunchGeometry.Dimensions.One,
        LaunchGeometry.Dimensions.One,
        Vec.from(args)
      ).fold(fail(_), identity)
      service.run(program, task).fold(error => fail(error.message), identity)
    }

    def read(service: SASSFixture.Execution, id: AllocationId, offset: Int, length: Int): Vec[Byte] =
      service.read(id, offset.toLong.unsafe[Long >= 0], length.unsafe[Int >= 0]).fold(
        error => fail(error.message),
        identity
      )

    def intAt(bytes: Vec[Byte], offset: Int = 0): Int =
      LittleEndian.decode(bytes, offset, 4).toInt

    val sourceValue = 0xdeadbe80
    val canary      = 0x3c.toByte
    val initial     = Vec.from(Vec.fill[Byte](64)(canary))

    for (sm <- Vec(50, 60))
      test(s"CUDA 11.8 sm_$sm generic byte stores preserve neighboring bytes and loads extend correctly") {
        val module = fixtureModule(sm)
        assertEquals(module.sm.raw, sm)
        val entries = if (sm == 50) Vec(
          ("global_u8", GenericMemoryFixtures.legacyGenericMemoryExtensionsByteGlobalU8Sm50),
          ("global_s8", GenericMemoryFixtures.legacyGenericMemoryExtensionsByteGlobalS8Sm50),
          ("global_u8_barrier", GenericMemoryFixtures.legacyGenericMemoryExtensionsByteGlobalU8BarrierSm50),
          ("shared_u8", GenericMemoryFixtures.legacyGenericMemoryExtensionsByteSharedU8Sm50),
          ("local_u8", GenericMemoryFixtures.legacyGenericMemoryExtensionsByteLocalU8Sm50)
        )
        else Vec(
          ("global_u8", GenericMemoryFixtures.legacyGenericMemoryExtensionsByteGlobalU8Sm60),
          ("global_s8", GenericMemoryFixtures.legacyGenericMemoryExtensionsByteGlobalS8Sm60),
          ("global_u8_barrier", GenericMemoryFixtures.legacyGenericMemoryExtensionsByteGlobalU8BarrierSm60),
          ("shared_u8", GenericMemoryFixtures.legacyGenericMemoryExtensionsByteSharedU8Sm60),
          ("local_u8", GenericMemoryFixtures.legacyGenericMemoryExtensionsByteLocalU8Sm60)
        )
        val programs = entries.map { case (name, listing) =>
          val kernel   = module.kernels.find(_.name == name).getOrElse(fail(s"missing $name"))
          val function = ListingDecoder.parse(listing, name, sm).fold(fail(_), _.head)
          name -> SASSFixture(module, kernel, Vec(function))
        }

        val globalU8 = ListingDecoder.parse(
          if (sm == 50) GenericMemoryFixtures.legacyGenericMemoryExtensionsByteGlobalU8Sm50
          else GenericMemoryFixtures.legacyGenericMemoryExtensionsByteGlobalU8Sm60,
          "global_u8",
          sm
        ).fold(fail(_), _.head)
        val byteStore = globalU8.instructions.find(hasInstruction(
          _,
          "ST",
          Vec(
            Instruction.Modifier.E,
            Instruction.Modifier.CG,
            Instruction.Modifier.U8
          )
        )).getOrElse(fail("missing generic U8 store"))
        val byteLoad = globalU8.instructions.find(hasInstruction(
          _,
          "LD",
          Vec(
            Instruction.Modifier.E,
            Instruction.Modifier.CG,
            Instruction.Modifier.U8
          )
        )).getOrElse(fail("missing generic U8 load"))
        val high = globalU8.instructions.find(hasInstruction(
          _,
          "LEA",
          Vec(
            Instruction.Modifier.HI,
            Instruction.Modifier.X
          )
        )).getOrElse(fail("missing LEA.HI.X"))
        assertEquals(byteStore.operands.last, high.operands.head)
        assertEquals(byteLoad.operands.last, high.operands.head)

        val barrierFunction = ListingDecoder.parse(
          if (sm == 50) GenericMemoryFixtures.legacyGenericMemoryExtensionsByteGlobalU8BarrierSm50
          else GenericMemoryFixtures.legacyGenericMemoryExtensionsByteGlobalU8BarrierSm60,
          "global_u8_barrier",
          sm
        ).fold(fail(_), _.head)
        val barrierIndex =
          barrierFunction.instructions.indexWhere(hasInstruction(_, "MEMBAR", Vec(Instruction.Modifier.CTA)))
        val barrierHighIndex = barrierFunction.instructions.indexWhere { instruction =>
          hasInstruction(instruction, "LEA", Vec(Instruction.Modifier.HI, Instruction.Modifier.X)) &&
          instruction.operands.head == Operand.Predicate(Some(1), false, false)
        }
        val barrierLoadIndex = barrierFunction.instructions.indexWhere(hasInstruction(
          _,
          "LD",
          Vec(
            Instruction.Modifier.E,
            Instruction.Modifier.CG,
            Instruction.Modifier.U8
          )
        ))
        assert(barrierHighIndex >= 0 && barrierIndex > barrierHighIndex && barrierLoadIndex > barrierIndex)
        assertEquals(
          barrierFunction.instructions(barrierLoadIndex).operands.last,
          barrierFunction.instructions(barrierHighIndex).operands.head
        )
        val pointerLow = barrierFunction.instructions.find(hasInstruction(
          _,
          "ST",
          Vec(
            Instruction.Modifier.E,
            Instruction.Modifier.CG,
            Instruction.Modifier.U8
          )
        )).get.operands.head match {
          case Operand.Address(Some(base), _, _, _, _, _) => base
          case other                                      => fail(s"expected byte-store address, found $other")
        }
        val carryIndex = barrierFunction.instructions.indexWhere(hasInstruction(_, "IADD32I"))
        assert(carryIndex >= 0)
        val carry          = barrierFunction.instructions(carryIndex)
        val corruptPointer = barrierFunction.copy(instructions =
          barrierFunction.instructions.updated(
            carryIndex,
            carry.rewrite(operands = carry.operands.updated(0, Operand.Qualified(pointerLow, Vec(OperandSelector.CC))))
          )
        )
        val _ = intercept[IllegalArgumentException](VMAddressAnalysis.rewrite(sm, corruptPointer))

        locally {
          val service = SASSFixture.Execution()
          val loaded  = programs.map { case (name, source) =>
            name -> service.compile(source)
              .fold(error => fail(error.message), identity)
          }.toMap
          val base   = 0x5fffffff0L + sm.toLong * 0x100000000L
          val source = service.allocate(64L)
          assertEquals(service.registerAllocationAddress(source, GuestAddress(base)), Right(()))
          val output = service.allocate(8L)

          def verifyGlobal(name: String, expectedLoad: Int): Unit = {
            assert(service.write(source, 0L, initial).isRight)
            assert(service.write(output, 0L, Vec.from(Vec.fill[Byte](8)(0.toByte))).isRight)
            run(service, loaded(name), name, Vec(scalar64(base), scalar32(sourceValue), memory(output)))
            val result = read(service, output, 0, 8)
            assertEquals(intAt(result), expectedLoad, s"$name byte-load extension")
            assertEquals(intAt(result, 4), sourceValue, s"$name retains full-width parameter value")
            val actual   = read(service, source, 0, 64)
            val expected = initial.updated(32, 0x80.toByte)
            assertEquals(actual, expected, s"$name changes only byte at offset 0x20")
          }

          verifyGlobal("global_u8", 0x00000080)
          verifyGlobal("global_s8", 0xffffff80)
          verifyGlobal("global_u8_barrier", 0x00000080)

          for ((name, expected) <- Vec("shared_u8" -> 0x005a80a5, "local_u8" -> 0x005a80a5)) {
            assert(service.write(output, 0L, Vec.from(Vec.fill[Byte](8)(0.toByte))).isRight)
            run(service, loaded(name), name, Vec(memory(output), scalar32(sourceValue)))
            val result = read(service, output, 0, 8)
            assertEquals(intAt(result), expected, s"$name preserves adjacent canaries around byte offset 4")
            assertEquals(intAt(result, 4), sourceValue, s"$name retains full-width parameter value")
          }
        }
      }
  }

  {
    def fixtureModule: Cubin.Module = SASSFixture.module(
      60,
      Vec(
        SASSFixture.Kernel("local_store", 320, Vec((0, 8), (8, 4)), registers = 7),
        SASSFixture.Kernel("local_load", 320, Vec((0, 8), (8, 4)), registers = 8),
        SASSFixture.Kernel("shared_store", 320, Vec((0, 8), (8, 4)), registers = 7, shared = 16),
        SASSFixture.Kernel("shared_load", 320, Vec((0, 8), (8, 4)), registers = 6, shared = 16),
        SASSFixture.Kernel("global_store", 320, Vec((0, 8), (8, 4)), registers = 4),
        SASSFixture.Kernel("global_load", 320, Vec((0, 8), (8, 8)), registers = 6)
      )
    )

    test("retain an independent scalar bridge while a generic load crosses the 32-bit address boundary") {
      val module   = fixtureModule
      val function = ListingDecoder.parse(
        GenericMemoryFixtures.legacyGenericAddressGlobalLoadSm60,
        "global_load",
        60
      ).fold(fail(_), _.head)
      val lowIndex  = function.instructions.indexWhere(hasInstruction(_, "LEA"))
      val highIndex = function.instructions.indexWhere(hasInstruction(
        _,
        "LEA",
        Vec(
          Instruction.Modifier.HI,
          Instruction.Modifier.X
        )
      ))
      val loadIndex  = function.instructions.indexWhere(hasInstruction(_, "LD", Vec(Instruction.Modifier.E)))
      val storeIndex = function.instructions.indexWhere(hasInstruction(_, "STG", Vec(Instruction.Modifier.E)))
      assert(lowIndex >= 0 && highIndex == lowIndex + 1 && loadIndex > highIndex && storeIndex > loadIndex)

      val bridgeValue = 0x1234
      val bridge      = SASSFixture.instruction(
        function.instructions(lowIndex).address.raw + 4,
        "MOV",
        Vec("R22", s"0x${bridgeValue.toHexString}"),
        None
      )
      val clobber = bridge.rewrite(operands = bridge.operands.updated(0, function.instructions(highIndex).operands(1)))
      val invalidCarryBridge = bridge.rewrite(SASSFixture.instruction(
        bridge.address.raw,
        "IADD.X",
        Vec("R22", "R0", "RZ")
      ).operation)
      val _ = intercept[IllegalArgumentException](
        VMAddressAnalysis.rewrite(
          60,
          function.copy(instructions = function.instructions.patch(highIndex, Vec(clobber), 0))
        )
      )
      val _ = intercept[IllegalArgumentException](
        VMAddressAnalysis.rewrite(
          60,
          function.copy(instructions = function.instructions.patch(highIndex, Vec(invalidCarryBridge), 0))
        )
      )
      val withBridge        = function.copy(instructions = function.instructions.patch(highIndex, Vec(bridge), 0))
      val adjustedLoadIndex = loadIndex + 1
      val load              = withBridge.instructions(adjustedLoadIndex)
      val addAfterLoad      = SASSFixture.instruction(
        load.address.raw + 4,
        "IADD",
        Vec("R23", "R22", "R4"),
        None
      )
      val withResult         = withBridge.instructions.patch(adjustedLoadIndex + 1, Vec(addAfterLoad), 0)
      val adjustedStoreIndex = storeIndex + 2
      val withObservedResult = withResult.updated(
        adjustedStoreIndex,
        withResult(adjustedStoreIndex).rewrite(
          operands = withResult(adjustedStoreIndex).operands.updated(1, SASSFixture.operand("R23"))
        )
      )
      val kernel = module.kernels.find(_.name == "global_load").fold(fail("missing global_load kernel"))(identity)
      val source =
        SASSFixture(module, kernel, Vec(function.copy(instructions = withObservedResult)))

      val baseAddress   = 0x1fffffffcL
      val targetAddress = 0x200000000L
      val loadedValue   = 0x12345678
      locally {
        val session = SASSFixture.Execution()
        val target  = session.allocate(4L)
        val output  = session.allocate(4L)
        assertEquals(session.registerAllocationAddress(target, GuestAddress(targetAddress)), Right(()))
        assert(session.write(target, 0L, LittleEndian.toVec(loadedValue.toLong, 4)).isRight)
        val program = session.compile(source)
          .fold(error => fail(error.message), identity)
        val task = KernelTask.validated(
          "global_load",
          LaunchGeometry.Dimensions.One,
          LaunchGeometry.Dimensions.One,
          Vec(
            KernelTask.Argument.Scalar(LittleEndian.toVec(baseAddress, 8)),
            KernelTask.Argument.Memory(output, 0L)
          )
        ).fold(fail(_), identity)
        session.run(program, task).fold(error => fail(error.message), identity)
        assertEquals(
          LittleEndian.decode(session.read(output, 0L, 4).toOption.get, 0, 4).toInt,
          loadedValue + bridgeValue
        )
      }
    }
  }

  {
    val compoundSpaceKernels = Vec(
      SASSFixture.Kernel("compound_local", 320, Vec((0, 8), (8, 4)), registers = 8),
      SASSFixture.Kernel("compound_shared", 320, Vec((0, 8), (8, 4)), registers = 8, shared = 16),
      SASSFixture.Kernel("compound_global", 320, Vec((0, 8), (8, 8), (16, 4)), registers = 7)
    )
    val vectorGenericKernels = Vec(
      SASSFixture.Kernel("repeated_store", 320, Vec((0, 8), (8, 4), (12, 4)), registers = 4),
      SASSFixture.Kernel("repeated_load", 320, Vec((0, 8), (8, 8)), registers = 8),
      SASSFixture.Kernel("repeated_store_then_load", 320, Vec((0, 8), (8, 4)), registers = 7),
      SASSFixture.Kernel("load_v4_u32", 320, Vec((0, 8), (8, 8)), registers = 8),
      SASSFixture.Kernel("load_v2_u64", 320, Vec((0, 8), (8, 8)), registers = 8),
      SASSFixture.Kernel("load_v2_u32", 320, Vec((0, 8), (8, 8)), registers = 6),
      SASSFixture.Kernel("store_v4_u32", 320, Vec((0, 8), (8, 4), (12, 4), (16, 4), (20, 4)), registers = 8),
      SASSFixture.Kernel("store_v2_u64", 320, Vec((0, 8), (8, 8), (16, 8)), registers = 8),
      SASSFixture.Kernel("store_v2_u32", 320, Vec((0, 8), (8, 4), (12, 4)), registers = 6)
    )
    def fixtureModule(sm: Int, vectorGeneric: Boolean): Cubin.Module =
      SASSFixture.module(sm, if (vectorGeneric) vectorGenericKernels else compoundSpaceKernels)

    def scalar32(value: Int): KernelTask.Argument.Scalar =
      KernelTask.Argument.Scalar(LittleEndian.toVec(value.toLong & 0xffffffffL, 4))

    def scalar64(value: Long): KernelTask.Argument.Scalar =
      KernelTask.Argument.Scalar(LittleEndian.toVec(value, 8))

    def memory(id: AllocationId): KernelTask.Argument.Memory =
      KernelTask.Argument.Memory(id, 0L)

    def run(
        service: SASSFixture.Execution,
        program: LoadedProgram.Compiled,
        name: String,
        args: Vec[KernelTask.Argument]
    ): Unit = {
      val task = KernelTask.validated(
        name.unsafe[NonEmpty[String]],
        LaunchGeometry.Dimensions.One,
        LaunchGeometry.Dimensions.One,
        Vec.from(args)
      ).fold(fail(_), identity)
      service.run(program, task).fold(error => fail(error.message), identity)
    }

    def words(values: Int*): Vec[Byte] =
      Vec.from(values.toVec.flatMap(value => LittleEndian.toVec(value.toLong & 0xffffffffL, 4)))

    def longWords(values: Long*): Vec[Byte] =
      Vec.from(values.toVec.flatMap(value => LittleEndian.toVec(value, 8)))

    def bytes(service: SASSFixture.Execution, id: AllocationId, offset: Int, length: Int): Vec[Byte] =
      service.read(id, offset.toLong.unsafe[Long >= 0], length.unsafe[Int >= 0]).fold(
        error => fail(error.message),
        identity
      )

    for (sm <- Vec(50, 60))
      test(s"CUDA 11.8 sm_$sm generic vector widths and repeated P consumers preserve memory results") {
        val vectorModule = fixtureModule(sm, vectorGeneric = true)
        val spacesModule = fixtureModule(sm, vectorGeneric = false)
        assertEquals(vectorModule.sm.raw, sm)
        assertEquals(spacesModule.sm.raw, sm)

        def lower(module: Cubin.Module, name: String, listing: String): SASSFixture = {
          val kernel   = module.kernels.find(_.name == name).getOrElse(fail(s"missing $name"))
          val function = ListingDecoder.parse(
            listing,
            name,
            sm
          ).fold(fail(_), _.head)
          val source = SASSFixture(module, kernel, Vec(function))
          source
        }

        val entries = if (sm == 50) Vec(
          "store_v2_u32"             -> GenericMemoryFixtures.legacyGenericMemoryExtensionsStoreV2U32Sm50,
          "store_v2_u64"             -> GenericMemoryFixtures.legacyGenericMemoryExtensionsStoreV2U64Sm50,
          "store_v4_u32"             -> GenericMemoryFixtures.legacyGenericMemoryExtensionsStoreV4U32Sm50,
          "load_v2_u32"              -> GenericMemoryFixtures.legacyGenericMemoryExtensionsLoadV2U32Sm50,
          "load_v2_u64"              -> GenericMemoryFixtures.legacyGenericMemoryExtensionsLoadV2U64Sm50,
          "load_v4_u32"              -> GenericMemoryFixtures.legacyGenericMemoryExtensionsLoadV4U32Sm50,
          "repeated_store_then_load" -> GenericMemoryFixtures.legacyGenericMemoryExtensionsRepeatedStoreThenLoadSm50
        )
        else Vec(
          "store_v2_u32"             -> GenericMemoryFixtures.legacyGenericMemoryExtensionsStoreV2U32Sm60,
          "store_v2_u64"             -> GenericMemoryFixtures.legacyGenericMemoryExtensionsStoreV2U64Sm60,
          "store_v4_u32"             -> GenericMemoryFixtures.legacyGenericMemoryExtensionsStoreV4U32Sm60,
          "load_v2_u32"              -> GenericMemoryFixtures.legacyGenericMemoryExtensionsLoadV2U32Sm60,
          "load_v2_u64"              -> GenericMemoryFixtures.legacyGenericMemoryExtensionsLoadV2U64Sm60,
          "load_v4_u32"              -> GenericMemoryFixtures.legacyGenericMemoryExtensionsLoadV4U32Sm60,
          "repeated_store_then_load" -> GenericMemoryFixtures.legacyGenericMemoryExtensionsRepeatedStoreThenLoadSm60
        )
        val spaceEntries = if (sm == 50) Vec(
          "compound_global" -> GenericMemoryFixtures.legacyGenericMemoryExtensionsCompoundGlobalSm50,
          "compound_shared" -> GenericMemoryFixtures.legacyGenericMemoryExtensionsCompoundSharedSm50,
          "compound_local"  -> GenericMemoryFixtures.legacyGenericMemoryExtensionsCompoundLocalSm50
        )
        else Vec(
          "compound_global" -> GenericMemoryFixtures.legacyGenericMemoryExtensionsCompoundGlobalSm60,
          "compound_shared" -> GenericMemoryFixtures.legacyGenericMemoryExtensionsCompoundSharedSm60,
          "compound_local"  -> GenericMemoryFixtures.legacyGenericMemoryExtensionsCompoundLocalSm60
        )
        val programs = entries.map { case (name, listing) =>
          name -> lower(vectorModule, name, listing)
        } ++ spaceEntries.map { case (name, listing) =>
          name -> lower(spacesModule, name, listing)
        }

        locally {
          val service = SASSFixture.Execution()
          val loaded  = programs.map { case (name, source) =>
            name -> service.compile(source)
              .fold(error => fail(error.message), identity)
          }.toMap

          val base   = 0x400000000L + sm.toLong * 0x10000L
          val source = service.allocate(528L)
          assertEquals(service.registerAllocationAddress(source, GuestAddress(base)), Right(()))
          val vectorOutput = service.allocate(32L)
          val scalarOutput = service.allocate(4L)

          val pointer = scalar64(base)
          val data32  = Vec(0x00000000, 0x7fc00001, 0x80000000, 0xffffffff)
          val data64  = Vec(0x0123456789abcdefL, 0xfedcba9876543210L)

          run(service, loaded("store_v2_u32"), "store_v2_u32", Vec(pointer, scalar32(data32(0)), scalar32(data32(1))))
          assertEquals(bytes(service, source, 512, 8), words(data32(0), data32(1)))
          run(service, loaded("store_v2_u64"), "store_v2_u64", Vec(pointer, scalar64(data64(0)), scalar64(data64(1))))
          assertEquals(bytes(service, source, 512, 16), longWords(data64(0), data64(1)))
          run(
            service,
            loaded("store_v4_u32"),
            "store_v4_u32",
            Vec(pointer) ++ data32.map(scalar32(_))
          )
          assertEquals(bytes(service, source, 512, 16), words(data32*))

          assert(service.write(source, 512L, words(data32*)).isRight)
          run(service, loaded("load_v2_u32"), "load_v2_u32", Vec(pointer, memory(vectorOutput)))
          assertEquals(bytes(service, vectorOutput, 0, 8), words(data32.take(2)*))
          run(service, loaded("load_v4_u32"), "load_v4_u32", Vec(pointer, memory(vectorOutput)))
          assertEquals(bytes(service, vectorOutput, 0, 16), words(data32*))

          assert(service.write(source, 512L, longWords(data64*)).isRight)
          run(service, loaded("load_v2_u64"), "load_v2_u64", Vec(pointer, memory(vectorOutput)))
          assertEquals(bytes(service, vectorOutput, 0, 16), longWords(data64*))

          val aliasValue = 0x89abcdef
          run(
            service,
            loaded("repeated_store_then_load"),
            "repeated_store_then_load",
            Vec(pointer, scalar32(aliasValue))
          )
          assertEquals(bytes(service, source, 512, 4), words(aliasValue), "the aliased generic store writes offset 512")
          assertEquals(bytes(service, source, 0, 4), words(aliasValue), "the later generic load feeds the output store")

          val compoundInput = Vec(0x01020304, 0x7fc00001, 0xffffffff)
          for (
            (name, value) <- Vec(
              "compound_global" -> compoundInput(0),
              "compound_shared" -> compoundInput(1),
              "compound_local"  -> compoundInput(2)
            )
          ) {
            val args = Vec(memory(scalarOutput), scalar32(value))
            run(service, loaded(name), name, if (name == "compound_global") pointer +: args else args)
            val expected = if (name == "compound_global") value else value + value
            assertEquals(bytes(service, scalarOutput, 0, 4), words(expected), name)
          }
        }
      }

    for (sm <- Vec(50, 60))
      test(s"sm_$sm generic memory extension rejects a clobbered address between reused-predicate consumers") {
        val module   = fixtureModule(sm, vectorGeneric = false)
        val function = ListingDecoder.parse(
          if (sm == 50) GenericMemoryFixtures.legacyGenericMemoryExtensionsCompoundGlobalSm50
          else GenericMemoryFixtures.legacyGenericMemoryExtensionsCompoundGlobalSm60,
          "compound_global",
          sm
        ).fold(fail(_), _.head)
        val lowIndex  = function.instructions.indexWhere(hasInstruction(_, "LEA"))
        val consumers = function.instructions.indices.filter { index =>
          val instruction = function.instructions(index)
          (hasInstruction(instruction, "LD", Vec(Instruction.Modifier.E, Instruction.Modifier.CG)) ||
            hasInstruction(instruction, "ST", Vec(Instruction.Modifier.E, Instruction.Modifier.CG))) &&
          instruction.operands.lastOption.exists {
            case Operand.Predicate(Some(_), false, false) => true
            case _                                        => false
          }
        }
        assertEquals(consumers.size, 2)
        val lowAddress = function.instructions(lowIndex).operands.head match {
          case Operand.Qualified(register, selectors) if selectors.contains(OperandSelector.CC) => register
          case register                                                                         => register
        }
        val clobber = SASSFixture.instruction(
          function.instructions(consumers.last).address.raw - 1,
          "MOV",
          Vec(OperandPrinter.print(lowAddress).fold(fail(_), identity), "RZ"),
          None
        )
        val clobbered = function.copy(instructions = function.instructions.patch(consumers.last, Vec(clobber), 0))
        val _         = intercept[IllegalArgumentException](VMAddressAnalysis.rewrite(sm, clobbered))

        val vector         = fixtureModule(sm, vectorGeneric = true)
        val vectorKernel   = vector.kernels.find(_.name == "store_v4_u32").getOrElse(fail("missing store_v4_u32"))
        val vectorFunction = ListingDecoder.parse(
          if (sm == 50) GenericMemoryFixtures.legacyGenericMemoryExtensionsStoreV4U32Sm50
          else GenericMemoryFixtures.legacyGenericMemoryExtensionsStoreV4U32Sm60,
          "store_v4_u32",
          sm
        ).fold(fail(_), _.head)
        val widthIndex = vectorFunction.instructions.indexWhere(hasInstruction(
          _,
          "ST",
          Vec(
            Instruction.Modifier.E,
            Instruction.Modifier.CG,
            Instruction.Modifier.`128`
          )
        ))
        assert(widthIndex >= 0)
        val _ = intercept[IllegalArgumentException] {
          val original         = vectorFunction.instructions(widthIndex)
          val unsupportedWidth = vectorFunction.copy(instructions =
            vectorFunction.instructions.updated(
              widthIndex,
              original.rewrite(modifiers =
                original.modifiers.updated(
                  original.modifiers.size - 1,
                  Instruction.Modifier("256")
                )
              )
            )
          )
          VMAddressAnalysis.rewrite(sm, unsupportedWidth)
        }
        assertEquals(vectorKernel.name, "store_v4_u32")
        assertEquals(module.sm.raw, sm)
      }
  }
}
