package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.semantics.render.*

class MachineSemanticsSuite extends munit.FunSuite {
  private val opcodes = Vec(
    "OpLabel",
    "OpBranch",
    "OpBranchConditional",
    "OpSwitch",
    "OpPhi",
    "OpFunctionCall",
    "OpFunctionPointerCallINTEL",
    "OpReturn",
    "OpReturnValue",
    "OpKill",
    "OpTerminateInvocation",
    "OpUnreachable",
    "OpAbortKHR"
  )

  private val composites = Vec(
    "OpCompositeExtract",
    "OpCompositeConstruct",
    "OpCompositeConstructReplicateEXT",
    "OpCompositeInsert",
    "OpVectorShuffle",
    "OpVectorExtractDynamic",
    "OpVectorInsertDynamic",
    "OpCopyObject",
    "OpCopyLogical"
  )

  private val pointers = Vec(
    "OpAccessChain",
    "OpInBoundsAccessChain",
    "OpPtrAccessChain",
    "OpInBoundsPtrAccessChain",
    "OpConvertPtrToU",
    "OpConvertUToPtr",
    "OpPtrCastToGeneric",
    "OpGenericCastToPtr",
    "OpGenericCastToPtrExplicit",
    "OpArrayLength"
  )

  private def shown(opcode: String): String = MachineSemantics.machine(opcode).headOption match {
    case Some((_, Right(definition))) => ScalaBackend.show(definition)
    case other                        => fail(s"$opcode does not render: $other")
  }

  test("every control instruction has one machine definition") {
    opcodes.foreach { opcode =>
      assertEquals(MachineSemantics.machine(opcode).size, 1, opcode)
      assert(MachineSemantics.machine(opcode).forall(_.outcome.isRight), opcode)
    }
  }

  test("instructions without machine semantics have no definition") {
    assert(MachineSemantics.machine("OpIAdd").isEmpty)
    assert(MachineSemantics.machine("OpAtomicIAdd").isEmpty)
  }

  test("a scalar load, store and sized copy each have one machine definition") {
    Vec("OpLoad", "OpStore", "OpCopyMemorySized").foreach { opcode =>
      assertEquals(MachineSemantics.machine(opcode).size, 1, opcode)
      assert(MachineSemantics.machine(opcode).forall(_.outcome.isRight), opcode)
    }
  }

  test("a scalar load renders as a read of the pointer's address") {
    assertEquals(
      shown("OpLoad"),
      """def OpLoad(pointer: UInt[64])(using machine: Machine): (result: UInt[32]) =
        |  (result = machine.load[UInt[32]](Space.Generic, pointer))""".stripMargin
    )
  }

  test("a scalar store renders as a write to the pointer's address") {
    assertEquals(
      shown("OpStore"),
      """def OpStore(pointer: UInt[64], value: UInt[32])(using machine: Machine): Unit = {
        |  machine.store[UInt[32]](Space.Generic, pointer, value)
        |  ()
        |}""".stripMargin
    )
  }

  test("a sized copy renders as a byte copy between the pointers") {
    assertEquals(
      shown("OpCopyMemorySized"),
      """def OpCopyMemorySized(source: UInt[64], size: UInt[64], target: UInt[64])(using machine: Machine): Unit = {
        |  val v0 = machine.loadBytes(Space.Generic, source, size)
        |  machine.storeBytes(Space.Generic, target, v0)
        |  ()
        |}""".stripMargin
    )
  }

  test("a group copy renders as a request to copy the elements, which returns the event") {
    assertEquals(
      shown("OpGroupAsyncCopy"),
      """def OpGroupAsyncCopy(scope: UInt[32], destination: UInt[64], source: UInt[64], count: UInt[64], stride: UInt[64])(using copies: Copies): Unit = {
        |  val _ = copies.groupAsyncCopy(scope, destination, source, count, stride)
        |  ()
        |}""".stripMargin
    )
  }

  test("an untyped group copy takes its element size as an operand") {
    assert(shown("OpUntypedGroupAsyncCopyKHR").contains("elementBytes"), shown("OpUntypedGroupAsyncCopyKHR"))
  }

  test("a group wait renders as a request on the copies, which are complete when issued") {
    assertEquals(
      shown("OpGroupWaitEvents"),
      """def OpGroupWaitEvents(scope: UInt[32], count: UInt[64], events: UInt[64])(using copies: Copies): Unit = {
        |  copies.waitEvents(scope, count, events)
        |  ()
        |}""".stripMargin
    )
  }

  test("untyped access chains render as the offset arithmetic of their typed forms") {
    assertEquals(
      shown("OpUntypedAccessChainKHR"),
      """def OpUntypedAccessChainKHR(base: UInt[64], index: UInt[32]): (result: UInt[64]) =
        |  (result = base + 4 + (index.to[UInt[64]] * 4).to[UInt[64]])""".stripMargin
    )
    assertEquals(
      shown("OpUntypedPtrAccessChainKHR"),
      """def OpUntypedPtrAccessChainKHR(base: UInt[64], element: UInt[32]): (result: UInt[64]) =
        |  (result = base + (element.to[UInt[64]] * 20).to[UInt[64]] + 4)""".stripMargin
    )
  }

  test("a copy and the atomic accesses each have one machine definition") {
    Vec("OpCopyMemory", "OpAtomicLoad", "OpAtomicStore", "OpAtomicFlagTestAndSet", "OpAtomicFlagClear").foreach {
      opcode =>
        assertEquals(MachineSemantics.machine(opcode).size, 1, opcode)
        assert(MachineSemantics.machine(opcode).forall(_.outcome.isRight), opcode)
    }
  }

  test("a copy renders as a request to copy the pointee") {
    assertEquals(
      shown("OpCopyMemory"),
      """def OpCopyMemory(target: UInt[64], source: UInt[64])(using copies: Copies): Unit = {
        |  copies.copy(target, source)
        |  ()
        |}""".stripMargin
    )
  }

  test("a conditional copy renders as the operand of the first condition that holds") {
    assertEquals(
      shown("OpConditionalCopyObjectINTEL"),
      """def OpConditionalCopyObjectINTEL(condition0: Bool, condition1: Bool, operand1: UInt[32], operand0: UInt[32])(using machine: Machine): (result: UInt[32]) = {
        |  machine.require(condition0 || condition1, "OpConditionalCopyObjectINTEL has no operand whose condition holds")
        |  (result = if (condition0) operand0 else operand1)
        |}""".stripMargin
    )
  }

  test("a prefetch renders as a check that its pointer is an address") {
    assertEquals(
      shown("OpUntypedPrefetchKHR"),
      """def OpUntypedPrefetchKHR(pointer: UInt[64])(using machine: Machine, pointers: Pointers): Unit = {
        |  machine.require(pointers.isAddress(pointer), "pointer is an address")
        |  ()
        |}""".stripMargin
    )
    assertEquals(
      shown("OpSubgroupBlockPrefetchINTEL"),
      """def OpSubgroupBlockPrefetchINTEL(pointer: UInt[64])(using machine: Machine, pointers: Pointers): Unit = {
        |  machine.require(pointers.isAddress(pointer), "pointer is an address")
        |  ()
        |}""".stripMargin
    )
  }

  test("an atomic load renders as an atomic read of the pointer's address") {
    assertEquals(
      shown("OpAtomicLoad"),
      """def OpAtomicLoad(pointer: UInt[64], scope: UInt[32], semantics: UInt[32])(using atomics: Atomics): (result: UInt[32]) =
        |  (result = atomics.load[UInt[32]](pointer, scope, semantics))""".stripMargin
    )
  }

  test("an atomic store renders as an atomic write to the pointer's address") {
    assertEquals(
      shown("OpAtomicStore"),
      """def OpAtomicStore(pointer: UInt[64], value: UInt[32], scope: UInt[32], semantics: UInt[32])(using atomics: Atomics): Unit = {
        |  atomics.store(pointer, value, scope, semantics)
        |  ()
        |}""".stripMargin
    )
  }

  test("a flag test-and-set and clear render as requests on the flag") {
    assertEquals(
      shown("OpAtomicFlagTestAndSet"),
      """def OpAtomicFlagTestAndSet(pointer: UInt[64], scope: UInt[32], semantics: UInt[32])(using atomics: Atomics): (result: Bool) =
        |  (result = atomics.flagTestAndSet(pointer, scope, semantics))""".stripMargin
    )
    assertEquals(
      shown("OpAtomicFlagClear"),
      """def OpAtomicFlagClear(pointer: UInt[64], scope: UInt[32], semantics: UInt[32])(using atomics: Atomics): Unit = {
        |  atomics.flagClear(pointer, scope, semantics)
        |  ()
        |}""".stripMargin
    )
  }

  test("each barrier instruction has one machine definition") {
    Vec(
      "OpControlBarrier",
      "OpControlBarrierArriveEXT",
      "OpControlBarrierWaitEXT",
      "OpMemoryBarrier",
      "OpMemoryNamedBarrier",
      "OpNamedBarrierInitialize"
    ).foreach { opcode =>
      assertEquals(MachineSemantics.machine(opcode).size, 1, opcode)
      assert(MachineSemantics.machine(opcode).forall(_.outcome.isRight), opcode)
    }
  }

  test("a control barrier and a memory barrier render as requests to the barriers") {
    assertEquals(
      shown("OpControlBarrier"),
      """def OpControlBarrier(execution: UInt[32], memory: UInt[32], semantics: UInt[32])(using barriers: Barriers): Unit = {
        |  barriers.controlBarrier(execution, memory, semantics)
        |  ()
        |}""".stripMargin
    )
    assertEquals(
      shown("OpMemoryBarrier"),
      """def OpMemoryBarrier(scope: UInt[32], semantics: UInt[32])(using barriers: Barriers): Unit = {
        |  barriers.fence(scope, semantics)
        |  ()
        |}""".stripMargin
    )
  }

  test("a conditional branch renders as a branch to either block") {
    assertEquals(
      shown("OpBranchConditional"),
      """def OpBranchConditional(condition: Bool)(using machine: Machine): Unit = {
        |  machine.branch(condition, 2, 3)
        |  ()
        |}""".stripMargin
    )
  }

  test("a phi renders as the value of the edge from the predecessor, which must exist") {
    assertEquals(
      shown("OpPhi"),
      """def OpPhi(first: UInt[32], second: UInt[32])(using machine: Machine, control: Control): (result: UInt[32]) = {
        |  machine.require(control.predecessor() == UInt[64](4) || control.predecessor() == UInt[64](6), "OpPhi %2 incoming edge")
        |  val v0 = if (control.predecessor() == UInt[64](4)) first else second
        |  (result = v0)
        |}""".stripMargin
    )
  }

  test("a call renders as a request to run the function with its arguments") {
    assertEquals(
      shown("OpFunctionCall"),
      """def OpFunctionCall(first: UInt[32], second: UInt[32])(using control: Control): Unit = {
        |  control.call(3, first, second)
        |  ()
        |}""".stripMargin
    )
  }

  test("a switch renders its cases and default block") {
    assertEquals(
      shown("OpSwitch"),
      """def OpSwitch(selector: UInt[32])(using control: Control): Unit = {
        |  control.switch(selector.to[UInt[64]], Seq(0L -> 3, 1L -> 4), 2)
        |  ()
        |}""".stripMargin
    )
  }

  test("returns, kills and aborts render as requests, and unreachable code as a failed check") {
    assertEquals(
      shown("OpReturnValue"),
      """def OpReturnValue(value: UInt[32])(using control: Control): Unit = {
        |  control.returnValue(value)
        |  ()
        |}""".stripMargin
    )
    assertEquals(
      shown("OpUnreachable"),
      """def OpUnreachable()(using machine: Machine): Unit = {
        |  machine.require(false, "OpUnreachable")
        |  ()
        |}""".stripMargin
    )
  }

  test("each composite instruction has one machine definition") {
    composites.foreach { opcode =>
      assertEquals(MachineSemantics.machine(opcode).size, 1, opcode)
      assert(MachineSemantics.machine(opcode).forall(_.outcome.isRight), opcode)
    }
  }

  test("an extraction renders as the leaf it selects") {
    assertEquals(
      shown("OpCompositeExtract"),
      """def OpCompositeExtract(composite_2: UInt[32]): (result: UInt[32]) =
        |  (result = composite_2)""".stripMargin
    )
  }

  test("an insertion renders as the leaves of the composite with the selected one replaced") {
    assertEquals(
      shown("OpCompositeInsert"),
      """def OpCompositeInsert(composite_0: UInt[32], composite_2: UInt[32], composite_3: UInt[32], inserted: UInt[32]): (result_0: UInt[32], result_1: UInt[32], result_2: UInt[32], result_3: UInt[32]) =
        |  (
        |    result_0 = composite_0,
        |    result_1 = inserted,
        |    result_2 = composite_2,
        |    result_3 = composite_3
        |  )""".stripMargin
    )
  }

  test("a shuffle renders as the leaves it selects, and zero for an undefined component") {
    assertEquals(
      shown("OpVectorShuffle"),
      """def OpVectorShuffle(left_0: UInt[32], left_3: UInt[32], right_1: UInt[32]): (result_0: UInt[32], result_1: UInt[32], result_2: UInt[32], result_3: UInt[32]) =
        |  (
        |    result_0 = left_3,
        |    result_1 = left_0,
        |    result_2 = right_1,
        |    result_3 = UInt[32](0)
        |  )""".stripMargin
    )
  }

  test("a transpose renders as the leaves of the matrix in column order") {
    assertEquals(
      shown("OpTranspose"),
      """def OpTranspose(matrix_0: UInt[32], matrix_1: UInt[32], matrix_2: UInt[32], matrix_3: UInt[32], matrix_4: UInt[32], matrix_5: UInt[32]): (result_0: UInt[32], result_1: UInt[32], result_2: UInt[32], result_3: UInt[32], result_4: UInt[32], result_5: UInt[32]) =
        |  (
        |    result_0 = matrix_0,
        |    result_1 = matrix_2,
        |    result_2 = matrix_4,
        |    result_3 = matrix_1,
        |    result_4 = matrix_3,
        |    result_5 = matrix_5
        |  )""".stripMargin
    )
  }

  test("a dynamic extraction checks its index against the vector") {
    assertEquals(
      shown("OpVectorExtractDynamic"),
      """def OpVectorExtractDynamic(vector_0: UInt[32], vector_1: UInt[32], vector_2: UInt[32], vector_3: UInt[32], index: UInt[32])(using machine: Machine): (result: UInt[32]) = {
        |  machine.require(index < 4, "OpVectorExtractDynamic index")
        |  (result = if (index == UInt[32](0)) vector_0 else if (index == UInt[32](1)) vector_1 else if (index == UInt[32](2)) vector_2 else vector_3)
        |}""".stripMargin
    )
  }

  test("an expectation renders as a copy of its value, and an undefined value as zero") {
    assertEquals(
      Vec(shown("OpExpectKHR"), shown("OpUndef"), shown("OpPoisonKHR")),
      Vec(
        """def OpExpectKHR(value_0: UInt[32], value_1: UInt[32], value_2: UInt[32], value_3: UInt[32]): (result_0: UInt[32], result_1: UInt[32], result_2: UInt[32], result_3: UInt[32]) =
          |  (
          |    result_0 = value_0,
          |    result_1 = value_1,
          |    result_2 = value_2,
          |    result_3 = value_3
          |  )""".stripMargin,
        """def OpUndef(): (result_0: UInt[32], result_1: UInt[32], result_2: UInt[32], result_3: UInt[32]) =
          |  (
          |    result_0 = UInt[32](0),
          |    result_1 = UInt[32](0),
          |    result_2 = UInt[32](0),
          |    result_3 = UInt[32](0)
          |  )""".stripMargin,
        """def OpPoisonKHR(): (result_0: UInt[32], result_1: UInt[32], result_2: UInt[32], result_3: UInt[32]) =
          |  (
          |    result_0 = UInt[32](0),
          |    result_1 = UInt[32](0),
          |    result_2 = UInt[32](0),
          |    result_3 = UInt[32](0)
          |  )""".stripMargin
      )
    )
  }

  test("a variable renders as private storage, with its initialiser when it has one") {
    val forms = MachineSemantics.machine("OpVariable").map {
      case (_, Right(definition)) => ScalaBackend.show(definition)
      case (instance, other)      => fail(s"$instance does not render: $other")
    }
    assertEquals(
      forms,
      Vec(
        """def OpVariable()(using variables: Variables): (result: UInt[64]) =
          |  (result = variables.variable())""".stripMargin,
        """def OpVariable(initialiser: UInt[32])(using variables: Variables): (result: UInt[64]) =
          |  (result = variables.initialisedVariable(initialiser))""".stripMargin
      )
    )
  }

  test("a clock read renders as the clock of its scope, split into two words for a vector") {
    val forms = MachineSemantics.machine("OpReadClockKHR").map {
      case (_, Right(definition)) => ScalaBackend.show(definition)
      case (instance, other)      => fail(s"$instance does not render: $other")
    }
    assertEquals(
      forms,
      Vec(
        """def OpReadClockKHR(scope: UInt[32])(using clocks: Clocks): (result: UInt[64]) =
          |  (result = clocks.readClock(scope))""".stripMargin,
        """def OpReadClockKHR(scope: UInt[32])(using clocks: Clocks): (result_0: UInt[32], result_1: UInt[32]) = {
          |  val v0 = clocks.readClock(scope)
          |  (
          |    result_0 = v0.to[UInt[32]],
          |    result_1 = (v0 >> 32).to[UInt[32]]
          |  )
          |}""".stripMargin
      )
    )
  }

  test("each pointer instruction has one machine definition") {
    pointers.foreach { opcode =>
      assertEquals(MachineSemantics.machine(opcode).size, 1, opcode)
      assert(MachineSemantics.machine(opcode).forall(_.outcome.isRight), opcode)
    }
  }

  test("an access chain renders as offset arithmetic over the type layout") {
    assertEquals(
      shown("OpAccessChain"),
      """def OpAccessChain(base: UInt[64], index: UInt[32]): (result: UInt[64]) =
        |  (result = base + 4 + (index.to[UInt[64]] * 4).to[UInt[64]])""".stripMargin
    )
    assertEquals(
      shown("OpPtrAccessChain"),
      """def OpPtrAccessChain(base: UInt[64], element: UInt[32]): (result: UInt[64]) =
        |  (result = base + (element.to[UInt[64]] * 20).to[UInt[64]] + 4)""".stripMargin
    )
  }

  test("pointer conversions and casts render as moves of the address") {
    assertEquals(
      shown("OpConvertPtrToU"),
      """def OpConvertPtrToU(pointer: UInt[64]): (result: UInt[64]) =
        |  (result = pointer)""".stripMargin
    )
    assertEquals(
      shown("OpPtrCastToGeneric"),
      """def OpPtrCastToGeneric(pointer: UInt[64]): (result: UInt[64]) =
        |  (result = pointer)""".stripMargin
    )
    assertEquals(
      shown("OpGenericCastToPtr"),
      """def OpGenericCastToPtr(pointer: UInt[64]): (result: UInt[64]) =
        |  (result = Addresses.retained(pointer, UInt[64](5), UInt[64](0)))""".stripMargin
    )
  }

  private val groups = Vec(
    "OpGroupAll",
    "OpGroupAny",
    "OpGroupBroadcast",
    "OpGroupIAdd",
    "OpGroupFAdd",
    "OpGroupLogicalAndKHR",
    "OpGroupIAddNonUniformAMD",
    "OpGroupNonUniformElect",
    "OpGroupNonUniformAll",
    "OpGroupNonUniformAny",
    "OpGroupNonUniformAllEqual",
    "OpGroupNonUniformBroadcast",
    "OpGroupNonUniformBroadcastFirst",
    "OpGroupNonUniformBallot",
    "OpGroupNonUniformInverseBallot",
    "OpGroupNonUniformBallotBitExtract",
    "OpGroupNonUniformBallotBitCount",
    "OpGroupNonUniformBallotFindLSB",
    "OpGroupNonUniformBallotFindMSB",
    "OpGroupNonUniformShuffle",
    "OpGroupNonUniformShuffleXor",
    "OpGroupNonUniformShuffleUp",
    "OpGroupNonUniformShuffleDown",
    "OpGroupNonUniformRotateKHR",
    "OpGroupNonUniformQuadBroadcast",
    "OpGroupNonUniformQuadSwap",
    "OpGroupNonUniformPartitionEXT",
    "OpGroupNonUniformIAdd",
    "OpGroupNonUniformFMax",
    "OpGroupNonUniformLogicalXor",
    "OpSubgroupAllKHR",
    "OpSubgroupAnyKHR",
    "OpSubgroupAllEqualKHR",
    "OpSubgroupBallotKHR",
    "OpSubgroupFirstInvocationKHR",
    "OpSubgroupReadInvocationKHR",
    "OpSubgroupShuffleINTEL",
    "OpSubgroupShuffleXorINTEL",
    "OpSubgroupShuffleUpINTEL",
    "OpSubgroupShuffleDownINTEL"
  )

  private def shownAs(opcode: String, instance: String): String =
    MachineSemantics.machine(opcode).find(_.instance == instance) match {
      case Some((_, Right(definition))) => ScalaBackend.show(definition)
      case other                        => fail(s"$opcode $instance does not render: $other")
    }

  test("each group instruction has a machine definition") {
    groups.foreach { opcode =>
      assert(MachineSemantics.machine(opcode).nonEmpty, opcode)
      assert(MachineSemantics.machine(opcode).forall(_.outcome.isRight), opcode)
    }
  }

  test("a subgroup reduction renders as a fold of the values of the invocations") {
    assertEquals(
      shown("OpGroupNonUniformIAdd"),
      """def OpGroupNonUniformIAdd(value: UInt[32])(using lanes: Lanes, groups: Groups): (result: UInt[32]) =
        |  (result = groups.fold[UInt[32]](Combine.IntegerAdd, lanes.gather(value), UInt[64](0), lanes.size()))""".stripMargin
    )
    assertEquals(
      shown("OpGroupNonUniformFMax"),
      """def OpGroupNonUniformFMax(value: Float32)(using lanes: Lanes, groups: Groups): (result: Float32) =
        |  (result = groups.foldFirst[Float32](Combine.FloatingMax, lanes.gather(value), UInt[64](0), lanes.size()))""".stripMargin
    )
  }

  test("scans fold the invocations up to this one and clustered reductions fold its cluster") {
    assertEquals(
      shownAs("OpGroupNonUniformIAdd", "OpGroupNonUniformIAdd(uint32, inclusive scan)"),
      """def OpGroupNonUniformIAdd(value: UInt[32])(using lanes: Lanes, groups: Groups): (result: UInt[32]) =
        |  (result = groups.fold[UInt[32]](Combine.IntegerAdd, lanes.gather(value), UInt[64](0), lanes.index() + 1))""".stripMargin
    )
    assertEquals(
      shownAs("OpGroupNonUniformIAdd", "OpGroupNonUniformIAdd(uint32, exclusive scan)"),
      """def OpGroupNonUniformIAdd(value: UInt[32])(using lanes: Lanes, groups: Groups): (result: UInt[32]) =
        |  (result = groups.fold[UInt[32]](Combine.IntegerAdd, lanes.gather(value), UInt[64](0), lanes.index()))""".stripMargin
    )
    assertEquals(
      shownAs("OpGroupNonUniformIAdd", "OpGroupNonUniformIAdd(uint32, clustered)"),
      """def OpGroupNonUniformIAdd(value: UInt[32], size: UInt[32])(using lanes: Lanes, groups: Groups): (result: UInt[32]) = {
        |  val v0 = lanes.index()
        |  val v1 = lanes.peer(lanes.gather(size), v0)
        |  (result = groups.fold[UInt[32]](Combine.IntegerAdd, lanes.gather(value), v0 - v0 % v1, v0 - v0 % v1 + v1))
        |}""".stripMargin
    )
  }

  test("a subgroup shuffle renders as a read of the value of another invocation") {
    assertEquals(
      shown("OpGroupNonUniformShuffle"),
      """def OpGroupNonUniformShuffle(value: UInt[32], id: UInt[32])(using lanes: Lanes): (result: UInt[32]) = {
        |  val v0 = lanes.index()
        |  val v1 = lanes.peer(lanes.gather(id), v0)
        |  val v2 = lanes.gather(value)
        |  (result = if (lanes.active(v1) != UInt[64](0)) lanes.peer(v2, v1).to[UInt[32]] else lanes.peer(v2, v0).to[UInt[32]])
        |}""".stripMargin
    )
    assertEquals(
      shown("OpGroupNonUniformShuffleXor"),
      """def OpGroupNonUniformShuffleXor(value: UInt[32], mask: UInt[32])(using lanes: Lanes): (result: UInt[32]) = {
        |  val v0 = lanes.index()
        |  val v1 = v0 ^ lanes.peer(lanes.gather(mask), v0)
        |  val v2 = lanes.gather(value)
        |  (result = if (lanes.active(v1) != UInt[64](0)) lanes.peer(v2, v1).to[UInt[32]] else lanes.peer(v2, v0).to[UInt[32]])
        |}""".stripMargin
    )
    assertEquals(
      shown("OpSubgroupShuffleUpINTEL"),
      """def OpSubgroupShuffleUpINTEL(previous: UInt[32], current: UInt[32], delta: UInt[32])(using lanes: Lanes, groups: Groups): (result: UInt[32]) = {
        |  val v0 = lanes.index()
        |  val v1 = v0 - lanes.peer(lanes.gather(delta), v0)
        |  val v2 = v1.to[SInt[64]]
        |  val v3 = v2 < 0
        |  val v4 = groups.launched()
        |  val v5 = v4.to[SInt[64]] <= v2
        |  val v6 = if (v3) v1 + v4 else if (v5) v1 - v4 else v1
        |  (result = if (v3 || v5) lanes.peer(lanes.gather(previous), v6).to[UInt[32]] else lanes.peer(lanes.gather(current), v6).to[UInt[32]])
        |}""".stripMargin
    )
  }

  test("a workgroup broadcast renders as the value of the invocation that the local id names") {
    assertEquals(
      shown("OpGroupBroadcast"),
      """def OpGroupBroadcast(execution: UInt[32], localId: UInt[32], value: UInt[32])(using lanes: Lanes, groups: Groups): (result: UInt[32]) =
        |  (result = lanes.peer(lanes.gather(value), groups.lane(execution, localId)).to[UInt[32]])""".stripMargin
    )
  }

  test("a partition renders the invocations that hold the value of the calling one in each word of the ballot") {
    assertEquals(
      shown("OpGroupNonUniformPartitionEXT"),
      """def OpGroupNonUniformPartitionEXT(value: UInt[32])(using lanes: Lanes, groups: Groups): (result_0: UInt[32], result_1: UInt[32], result_2: UInt[32], result_3: UInt[32]) = {
        |  val v0 = lanes.gather(value)
        |  val v1 = lanes.peer(v0, lanes.index())
        |  (
        |    result_0 = groups.matching(v0, v1, UInt[64](0), None),
        |    result_1 = groups.matching(v0, v1, UInt[64](32), None),
        |    result_2 = groups.matching(v0, v1, UInt[64](64), None),
        |    result_3 = groups.matching(v0, v1, UInt[64](96), None)
        |  )
        |}""".stripMargin
    )
  }

  test("an all-equal renders as the bitwise or of the values compared with their bitwise and") {
    assertEquals(
      shown("OpGroupNonUniformAllEqual"),
      """def OpGroupNonUniformAllEqual(value: UInt[32])(using lanes: Lanes, groups: Groups): (result: Bool) = {
        |  val v0 = lanes.gather(value)
        |  val v1 = lanes.size()
        |  (result = groups.fold[UInt[32]](Combine.BitwiseOr, v0, UInt[64](0), v1) == groups.fold[UInt[32]](Combine.BitwiseAnd, v0, UInt[64](0), v1))
        |}""".stripMargin
    )
  }

  test("votes and elections fold the predicates and compare the index with the leader") {
    assertEquals(
      shown("OpGroupNonUniformAll"),
      """def OpGroupNonUniformAll(predicate: Bool)(using lanes: Lanes, groups: Groups): (result: Bool) =
        |  (result = groups.fold[Bool](Combine.LogicalAnd, lanes.gather(predicate), UInt[64](0), lanes.size()))""".stripMargin
    )
    assertEquals(
      shown("OpGroupNonUniformElect"),
      """def OpGroupNonUniformElect()(using lanes: Lanes, groups: Groups): (result: Bool) =
        |  (result = lanes.index() == groups.leader())""".stripMargin
    )
    assert(shown("OpSubgroupAnyKHR").contains("Combine.LogicalOr"), shown("OpSubgroupAnyKHR"))
  }

  test("a ballot renders as the bits of the predicates for each group of 32 invocations") {
    assertEquals(
      shown("OpGroupNonUniformBallot"),
      """def OpGroupNonUniformBallot(predicate: Bool)(using lanes: Lanes, groups: Groups): (result_0: UInt[32], result_1: UInt[32], result_2: UInt[32], result_3: UInt[32]) = {
        |  val v0 = lanes.gather(predicate)
        |  (
        |    result_0 = groups.bits(v0, UInt[64](0)),
        |    result_1 = groups.bits(v0, UInt[64](32)),
        |    result_2 = groups.bits(v0, UInt[64](64)),
        |    result_3 = groups.bits(v0, UInt[64](96))
        |  )
        |}""".stripMargin
    )
    val count = shown("OpGroupNonUniformBallotBitCount")
    assert(count.contains("lanes.size()") && count.contains(".bitCount"), count)
    assert(shown("OpGroupNonUniformBallotFindLSB").contains("trailingZeros"), shown("OpGroupNonUniformBallotFindLSB"))
  }

  test("a determinant renders as the expansion of its elements in double precision") {
    assertEquals(
      shown("GLSL.std.450 Determinant"),
      """def `GLSL.std.450 Determinant`(matrix_0: Float32, matrix_1: Float32, matrix_2: Float32, matrix_3: Float32): (result: Float32) =
        |  (result = Matrices.narrow(Matrices.subtract(Matrices.multiply(Matrices.widen(matrix_0.bits.to[UInt[64]], UInt[64](32)), Matrices.widen(matrix_3.bits.to[UInt[64]], UInt[64](32))), Matrices.multiply(Matrices.widen(matrix_2.bits.to[UInt[64]], UInt[64](32)), Matrices.widen(matrix_1.bits.to[UInt[64]], UInt[64](32)))), UInt[64](32)).to[UInt[32]].asFloat)""".stripMargin
    )
  }

  test("an inverse renders each element as its cofactor over the determinant") {
    assertEquals(
      shown("GLSL.std.450 MatrixInverse"),
      """def `GLSL.std.450 MatrixInverse`(matrix_0: Float32, matrix_1: Float32, matrix_2: Float32, matrix_3: Float32): (result_0: Float32, result_1: Float32, result_2: Float32, result_3: Float32) = {
        |  val v0 = Matrices.widen(matrix_3.bits.to[UInt[64]], UInt[64](32))
        |  val v1 = Matrices.widen(matrix_0.bits.to[UInt[64]], UInt[64](32))
        |  val v2 = Matrices.widen(matrix_2.bits.to[UInt[64]], UInt[64](32))
        |  val v3 = Matrices.widen(matrix_1.bits.to[UInt[64]], UInt[64](32))
        |  val v4 = Matrices.subtract(Matrices.multiply(v1, v0), Matrices.multiply(v2, v3))
        |  (
        |    result_0 = Matrices.narrow(Matrices.divide(v0, v4), UInt[64](32)).to[UInt[32]].asFloat,
        |    result_1 = Matrices.narrow(Matrices.divide(Matrices.negate(v3), v4), UInt[64](32)).to[UInt[32]].asFloat,
        |    result_2 = Matrices.narrow(Matrices.divide(Matrices.negate(v2), v4), UInt[64](32)).to[UInt[32]].asFloat,
        |    result_3 = Matrices.narrow(Matrices.divide(v1, v4), UInt[64](32)).to[UInt[32]].asFloat
        |  )
        |}""".stripMargin
    )
  }

  test("a printf renders as a request to the printf buffer, with a zero result") {
    assertEquals(
      shown("OpenCL.std printf"),
      """def `OpenCL.std printf`(format: UInt[64], argument: UInt[32])(using printing: Printing): (result: UInt[32]) = {
        |  printing.printf(format, argument)
        |  (result = UInt[32](0))
        |}""".stripMargin
    )
  }

  test("a shuffle renders each lane as a select over the lanes by its mask") {
    assertEquals(
      shown("OpenCL.std shuffle"),
      """def `OpenCL.std shuffle`(vector_0: Float32, vector_1: Float32, vector_2: Float32, vector_3: Float32, mask_0: UInt[32], mask_1: UInt[32], mask_2: UInt[32], mask_3: UInt[32]): (result_0: Float32, result_1: Float32, result_2: Float32, result_3: Float32) =
        |  (
        |    result_0 = if (mask_0 % 4 == UInt[32](0)) vector_0.bits.asFloat else if (mask_0 % 4 == UInt[32](1)) vector_1.bits.asFloat else if (mask_0 % 4 == UInt[32](2)) vector_2.bits.asFloat else vector_3.bits.asFloat,
        |    result_1 = if (mask_1 % 4 == UInt[32](0)) vector_0.bits.asFloat else if (mask_1 % 4 == UInt[32](1)) vector_1.bits.asFloat else if (mask_1 % 4 == UInt[32](2)) vector_2.bits.asFloat else vector_3.bits.asFloat,
        |    result_2 = if (mask_2 % 4 == UInt[32](0)) vector_0.bits.asFloat else if (mask_2 % 4 == UInt[32](1)) vector_1.bits.asFloat else if (mask_2 % 4 == UInt[32](2)) vector_2.bits.asFloat else vector_3.bits.asFloat,
        |    result_3 = if (mask_3 % 4 == UInt[32](0)) vector_0.bits.asFloat else if (mask_3 % 4 == UInt[32](1)) vector_1.bits.asFloat else if (mask_3 % 4 == UInt[32](2)) vector_2.bits.asFloat else vector_3.bits.asFloat
        |  )""".stripMargin
    )
    assert(shown("OpenCL.std shuffle2").contains("mask_0 % 8"), shown("OpenCL.std shuffle2"))
  }

  test("a vector load and store render as requests on the vector memory") {
    assertEquals(
      shown("OpenCL.std vloadn"),
      """def `OpenCL.std vloadn`(offset: UInt[64], pointer: UInt[64])(using vectors: Vectors): (result_0: Float32, result_1: Float32) =
        |  (
        |    result_0 = FloatingPoint[32](vectors.loaded(0, offset, pointer, 2, 4)),
        |    result_1 = FloatingPoint[32](vectors.loaded(1, offset, pointer, 2, 4))
        |  )""".stripMargin
    )
    assertEquals(
      shown("OpenCL.std vstoren"),
      """def `OpenCL.std vstoren`(offset: UInt[64], pointer: UInt[64], data_0: Float32, data_1: Float32)(using vectors: Vectors): Unit = {
        |  vectors.stored(offset, pointer, 2, 4, data_0, data_1)
        |  ()
        |}""".stripMargin
    )
  }

  test("an image fetch renders as a read of each texel component") {
    assertEquals(
      shown("OpImageFetch"),
      """def OpImageFetch(image: UInt[64], coordinate_0: UInt[32], coordinate_1: UInt[32])(using images: Images): (result_0: Float32, result_1: Float32, result_2: Float32, result_3: Float32) =
        |  (
        |    result_0 = images.fetch(0, image, coordinate_0, coordinate_1),
        |    result_1 = images.fetch(1, image, coordinate_0, coordinate_1),
        |    result_2 = images.fetch(2, image, coordinate_0, coordinate_1),
        |    result_3 = images.fetch(3, image, coordinate_0, coordinate_1)
        |  )""".stripMargin
    )
  }

  test("an image write renders as an effect on the image") {
    assertEquals(
      shown("OpImageWrite"),
      """def OpImageWrite(image: UInt[64], coordinate_0: UInt[32], coordinate_1: UInt[32], texel_0: Float32, texel_1: Float32, texel_2: Float32, texel_3: Float32)(using images: Images): Unit = {
        |  images.write(image, coordinate_0, coordinate_1, texel_0, texel_1, texel_2, texel_3)
        |  ()
        |}""".stripMargin
    )
  }

  test("an implicit level of detail sample renders its descriptor reads, levels and filter taps") {
    assertEquals(
      shown("OpImageSampleImplicitLod"),
      """def OpImageSampleImplicitLod(image: UInt[64], coordinate_0: Float32, coordinate_1: Float32)(using fragments: Fragments, images: Images, machine: Machine): (result_0: Float32, result_1: Float32, result_2: Float32, result_3: Float32) = {
        |  fragments.partials(124)
        |  val v0 = images.dimensions(image)
        |  val v1 = images.arrayed(image)
        |  val v2 = images.cube(image)
        |  images.numeric(coordinate_0.bits.to[UInt[64]], coordinate_1.bits.to[UInt[64]])
        |  for (t3 <- fragments.indices(UInt[64](2))) {
        |    fragments.setPartial(UInt[64](16) + t3, if (0 != 0) Sampling.converted(if (t3 == UInt[64](0)) coordinate_0.bits.to[UInt[64]] else coordinate_1.bits.to[UInt[64]]) else if (t3 == UInt[64](0)) coordinate_0.bits.to[UInt[64]] else coordinate_1.bits.to[UInt[64]])
        |  }
        |  val v3 = images.gradients(16, UInt[64](2), 30)
        |  images.samplerWords(image, 0)
        |  val v4 = images.formatWord(image)
        |  val v5 = images.levelCount(image)
        |  fragments.setPartial(UInt[64](48), UInt[64](1))
        |  for (t7 <- fragments.indices(v0 + v1)) {
        |    fragments.setPartial(UInt[64](48), fragments.partial(UInt[64](48)) & Sampling.finite(fragments.partial(UInt[64](16) + t7)))
        |  }
        |  machine.require((((v0 + v1).to[SInt[64]] <= 2).to[UInt[1]].to[UInt[64]] & fragments.partial(UInt[64](48))) != UInt[64](0), "sample coordinates")
        |  for (t8 <- fragments.indices(v0)) {
        |    fragments.setPartial(UInt[64](24) + t8, Sampling.widen(fragments.partial(UInt[64](16) + t8)))
        |  }
        |  if (v2 != UInt[64](0)) {
        |    fragments.setPartial(UInt[64](27), Sampling.face(fragments.partial(UInt[64](24)), fragments.partial(UInt[64](25)), fragments.partial(UInt[64](26))))
        |    fragments.setPartial(UInt[64](28), Sampling.faceS(fragments.partial(UInt[64](24)), fragments.partial(UInt[64](25)), fragments.partial(UInt[64](26))))
        |    fragments.setPartial(UInt[64](29), Sampling.faceT(fragments.partial(UInt[64](24)), fragments.partial(UInt[64](25)), fragments.partial(UInt[64](26))))
        |  }
        |  machine.require(v2 == UInt[64](0) || fragments.partial(UInt[64](4)) == UInt[64](0), "cube coordinates")
        |  machine.require((v3 | ((fragments.partial(UInt[64](0)) == fragments.partial(UInt[64](1))).to[UInt[1]].to[UInt[64]] | Sampling.bounded(fragments.partial(UInt[64](9)), fragments.partial(UInt[64](10))))) != UInt[64](0), "implicit level of detail")
        |  if (v3 != UInt[64](0)) {
        |    images.level(image, UInt[64](0), 56)
        |  }
        |  fragments.setPartial(UInt[64](50), Sampling.lambda(fragments.partial(UInt[64](9)), fragments.partial(UInt[64](10)), if (v3 != UInt[64](0)) Sampling.detail(Sampling.scaled(if (v2 != UInt[64](0)) Sampling.gradientS(fragments.partial(UInt[64](24)), fragments.partial(UInt[64](25)), fragments.partial(UInt[64](26)), fragments.partial(UInt[64](31)), fragments.partial(UInt[64](32)), fragments.partial(UInt[64](33))) else fragments.partial(UInt[64](31)), fragments.partial(UInt[64](56)), fragments.partial(UInt[64](4))), Sampling.scaled(if (v2 != UInt[64](0)) Sampling.gradientT(fragments.partial(UInt[64](24)), fragments.partial(UInt[64](25)), fragments.partial(UInt[64](26)), fragments.partial(UInt[64](31)), fragments.partial(UInt[64](32)), fragments.partial(UInt[64](33))) else fragments.partial(UInt[64](32)), fragments.partial(UInt[64](57)), fragments.partial(UInt[64](4))), Sampling.scaled(if (v2 != UInt[64](0)) UInt[64](0) else fragments.partial(UInt[64](33)), fragments.partial(UInt[64](58)), fragments.partial(UInt[64](4))), Sampling.scaled(if (v2 != UInt[64](0)) Sampling.gradientS(fragments.partial(UInt[64](24)), fragments.partial(UInt[64](25)), fragments.partial(UInt[64](26)), fragments.partial(UInt[64](34)), fragments.partial(UInt[64](35)), fragments.partial(UInt[64](36))) else fragments.partial(UInt[64](34)), fragments.partial(UInt[64](56)), fragments.partial(UInt[64](4))), Sampling.scaled(if (v2 != UInt[64](0)) Sampling.gradientT(fragments.partial(UInt[64](24)), fragments.partial(UInt[64](25)), fragments.partial(UInt[64](26)), fragments.partial(UInt[64](34)), fragments.partial(UInt[64](35)), fragments.partial(UInt[64](36))) else fragments.partial(UInt[64](35)), fragments.partial(UInt[64](57)), fragments.partial(UInt[64](4))), Sampling.scaled(if (v2 != UInt[64](0)) UInt[64](0) else fragments.partial(UInt[64](36)), fragments.partial(UInt[64](58)), fragments.partial(UInt[64](4)))) else UInt[64](0), Sampling.bias(fragments.partial(UInt[64](8)), UInt[64](0))))
        |  fragments.setPartial(UInt[64](51), if (Sampling.positive(fragments.partial(UInt[64](50))) != UInt[64](0)) fragments.partial(UInt[64](0)) else fragments.partial(UInt[64](1)))
        |  fragments.setPartial(UInt[64](52), Sampling.selected(fragments.partial(UInt[64](50)), v5))
        |  fragments.setPartial(UInt[64](53), UInt[64](if (fragments.partial(UInt[64](11)) == UInt[64](0)) 1 else 2))
        |  machine.require(fragments.partial(UInt[64](51)) == UInt[64](0) || fragments.partial(UInt[64](51)) == UInt[64](1), "sample filter")
        |  machine.require(fragments.partial(UInt[64](51)) == UInt[64](0) || Sampling.integral(v4.to[UInt[28]].to[UInt[64]]) == UInt[64](0), "integer filter")
        |  machine.require((images.vulkan() | (UInt[1](0) == (fragments.partial(UInt[64](5)) != UInt[64](0)).to[UInt[1]]).to[UInt[1]].to[UInt[64]]) != UInt[64](0), "depth comparison")
        |  machine.require((v3 | ((v5 == UInt[64](1)).to[UInt[1]].to[UInt[64]] | Sampling.fixed(fragments.partial(UInt[64](9)), fragments.partial(UInt[64](10))))) != UInt[64](0), "implicit mip selection")
        |  for (t9 <- fragments.indices(UInt[64](if (fragments.partial(UInt[64](53)) == UInt[64](2)) 2 else 1))) {
        |    images.level(image, Sampling.level(fragments.partial(UInt[64](52)), v5, fragments.partial(UInt[64](53)), t9), 56)
        |    fragments.setPartial(UInt[64](108), Sampling.texelSpace(if (v2 != UInt[64](0)) fragments.partial(UInt[64](28)) else fragments.partial(UInt[64](24)), fragments.partial(UInt[64](56)), fragments.partial(UInt[64](37)), fragments.partial(UInt[64](4))))
        |    fragments.setPartial(UInt[64](109), if (v0 == UInt[64](1)) UInt[64](0x3fe0000000000000L) else Sampling.texelSpace(if (v2 != UInt[64](0)) fragments.partial(UInt[64](29)) else fragments.partial(UInt[64](25)), fragments.partial(UInt[64](57)), fragments.partial(UInt[64](38)), fragments.partial(UInt[64](4))))
        |    fragments.setPartial(UInt[64](110), if (v0 == UInt[64](3) && v2 == UInt[64](0)) Sampling.texelSpace(fragments.partial(UInt[64](26)), fragments.partial(UInt[64](58)), fragments.partial(UInt[64](39)), fragments.partial(UInt[64](4))) else UInt[64](0x3fe0000000000000L))
        |    fragments.setPartial(UInt[64](114), Sampling.components(v4.to[UInt[28]].to[UInt[64]]))
        |    fragments.setPartial(UInt[64](115), if (v1 != UInt[64](0)) Sampling.layer(fragments.partial(UInt[64](16) + v0), if (v2 != UInt[64](0)) fragments.partial(UInt[64](59)) / 6 else fragments.partial(UInt[64](59))) else UInt[64](0))
        |    fragments.setPartial(UInt[64](111), if (fragments.partial(UInt[64](51)) == UInt[64](0)) Sampling.nearest(fragments.partial(UInt[64](108))) else Sampling.lowest(fragments.partial(UInt[64](108))))
        |    fragments.setPartial(UInt[64](112), if (fragments.partial(UInt[64](51)) == UInt[64](0)) Sampling.nearest(fragments.partial(UInt[64](109))) else Sampling.lowest(fragments.partial(UInt[64](109))))
        |    fragments.setPartial(UInt[64](113), if (fragments.partial(UInt[64](51)) == UInt[64](0)) Sampling.nearest(fragments.partial(UInt[64](110))) else Sampling.lowest(fragments.partial(UInt[64](110))))
        |    for (t10 <- fragments.indices(UInt[64](if (fragments.partial(UInt[64](51)) == UInt[64](0)) 1 else if (v0 == UInt[64](3) && v2 == UInt[64](0)) 8 else 4))) {
        |      fragments.setPartial(UInt[64](116), fragments.partial(UInt[64](111)) + t10.to[UInt[1]].to[UInt[64]])
        |      fragments.setPartial(UInt[64](117), fragments.partial(UInt[64](112)) + (t10 >> 1).to[UInt[1]].to[UInt[64]])
        |      fragments.setPartial(UInt[64](118), fragments.partial(UInt[64](113)) + (t10 >> 2))
        |      fragments.setPartial(UInt[64](119), Sampling.address(fragments.partial(UInt[64](116)), fragments.partial(UInt[64](56)), fragments.partial(UInt[64](2))))
        |      fragments.setPartial(UInt[64](120), Sampling.address(fragments.partial(UInt[64](117)), fragments.partial(UInt[64](57)), fragments.partial(UInt[64](3))))
        |      fragments.setPartial(UInt[64](121), if (v0 == UInt[64](3) && v2 == UInt[64](0)) Sampling.address(fragments.partial(UInt[64](118)), fragments.partial(UInt[64](58)), fragments.partial(UInt[64](12))) else UInt[64](0))
        |      fragments.setPartial(UInt[64](122), ((fragments.partial(UInt[64](119)) | fragments.partial(UInt[64](120)) | fragments.partial(UInt[64](121))).to[SInt[64]] < 0).to[UInt[64]])
        |      fragments.setPartial(UInt[64](123), if (v2 != UInt[64](0)) Sampling.cubeTaps(fragments.partial(UInt[64](27)), fragments.partial(UInt[64](116)), fragments.partial(UInt[64](117)), fragments.partial(UInt[64](56)), (fragments.partial(UInt[64](51)) == UInt[64](1)).to[UInt[64]]) else UInt[64](1))
        |      machine.require(v2 == UInt[64](0) || (fragments.partial(UInt[64](56)) == fragments.partial(UInt[64](57)) && (fragments.partial(UInt[64](59)) >= 6 && fragments.partial(UInt[64](59)) % 6 == UInt[64](0))), "cube image faces")
        |      for (t11 <- fragments.indices(UInt[64](4))) {
        |        fragments.setPartial(UInt[64](68) + (t10 * 4 + t11), UInt[64](0))
        |      }
        |      for (t12 <- fragments.indices(fragments.partial(UInt[64](123)))) {
        |        for (t13 <- fragments.indices(UInt[64](4))) {
        |          fragments.setPartial(UInt[64](64) + t13, Sampling.border(fragments.partial(UInt[64](7)), fragments.partial(UInt[64](114)), t13))
        |        }
        |        if ((v2 | (fragments.partial(UInt[64](122)) == UInt[64](0)).to[UInt[1]].to[UInt[64]]) != UInt[64](0)) {
        |          images.texel(if (v2 != UInt[64](0)) fragments.partial(UInt[64](115)) * 6 + Sampling.cubeFace(fragments.partial(UInt[64](27)), fragments.partial(UInt[64](116)), fragments.partial(UInt[64](117)), fragments.partial(UInt[64](56)), (fragments.partial(UInt[64](51)) == UInt[64](1)).to[UInt[64]], t12) else fragments.partial(UInt[64](115)), if (v2 != UInt[64](0)) Sampling.cubeX(fragments.partial(UInt[64](27)), fragments.partial(UInt[64](116)), fragments.partial(UInt[64](117)), fragments.partial(UInt[64](56)), (fragments.partial(UInt[64](51)) == UInt[64](1)).to[UInt[64]], t12) else fragments.partial(UInt[64](119)), if (v2 != UInt[64](0)) Sampling.cubeY(fragments.partial(UInt[64](27)), fragments.partial(UInt[64](116)), fragments.partial(UInt[64](117)), fragments.partial(UInt[64](56)), (fragments.partial(UInt[64](51)) == UInt[64](1)).to[UInt[64]], t12) else fragments.partial(UInt[64](121)) * fragments.partial(UInt[64](57)) + fragments.partial(UInt[64](120)), 64)
        |        }
        |        for (t14 <- fragments.indices(UInt[64](4))) {
        |          fragments.setPartial(UInt[64](68) + (t10 * 4 + t14), Sampling.accumulated(fragments.partial(UInt[64](68) + (t10 * 4 + t14)), fragments.partial(UInt[64](64) + t14), fragments.partial(UInt[64](123)), v2))
        |        }
        |      }
        |    }
        |    for (t15 <- fragments.indices(UInt[64](4))) {
        |      fragments.setPartial(UInt[64](100) + (t9 * 4 + t15), if (fragments.partial(UInt[64](51)) == UInt[64](0)) fragments.partial(UInt[64](68) + t15) else if (v0 == UInt[64](3) && v2 == UInt[64](0)) Sampling.lerp(Sampling.bilinear(fragments.partial(UInt[64](68) + t15), fragments.partial(UInt[64](72) + t15), fragments.partial(UInt[64](76) + t15), fragments.partial(UInt[64](80) + t15), Sampling.fraction(fragments.partial(UInt[64](108)), fragments.partial(UInt[64](111))), Sampling.fraction(fragments.partial(UInt[64](109)), fragments.partial(UInt[64](112)))), Sampling.bilinear(fragments.partial(UInt[64](84) + t15), fragments.partial(UInt[64](88) + t15), fragments.partial(UInt[64](92) + t15), fragments.partial(UInt[64](96) + t15), Sampling.fraction(fragments.partial(UInt[64](108)), fragments.partial(UInt[64](111))), Sampling.fraction(fragments.partial(UInt[64](109)), fragments.partial(UInt[64](112)))), Sampling.fraction(fragments.partial(UInt[64](110)), fragments.partial(UInt[64](113)))) else Sampling.bilinear(fragments.partial(UInt[64](68) + t15), fragments.partial(UInt[64](72) + t15), fragments.partial(UInt[64](76) + t15), fragments.partial(UInt[64](80) + t15), Sampling.fraction(fragments.partial(UInt[64](108)), fragments.partial(UInt[64](111))), Sampling.fraction(fragments.partial(UInt[64](109)), fragments.partial(UInt[64](112)))))
        |    }
        |  }
        |  for (t16 <- fragments.indices(UInt[64](4))) {
        |    fragments.setPartial(UInt[64](100) + t16, Sampling.mixed(fragments.partial(UInt[64](100) + t16), fragments.partial(UInt[64](104) + t16), if (fragments.partial(UInt[64](53)) == UInt[64](2)) Sampling.between(fragments.partial(UInt[64](52))) else UInt[64](0)))
        |  }
        |  images.swizzle(image, UInt[64](100))
        |  val v6 = Sampling.packed(Sampling.integral(v4.to[UInt[28]].to[UInt[64]]), fragments.partial(UInt[64](100)))
        |  val v7 = Sampling.packed(Sampling.integral(v4.to[UInt[28]].to[UInt[64]]), fragments.partial(UInt[64](101)))
        |  val v8 = Sampling.packed(Sampling.integral(v4.to[UInt[28]].to[UInt[64]]), fragments.partial(UInt[64](102)))
        |  val v9 = Sampling.packed(Sampling.integral(v4.to[UInt[28]].to[UInt[64]]), fragments.partial(UInt[64](103)))
        |  (
        |    result_0 = v6.to[UInt[32]].asFloat,
        |    result_1 = v7.to[UInt[32]].asFloat,
        |    result_2 = v8.to[UInt[32]].asFloat,
        |    result_3 = v9.to[UInt[32]].asFloat
        |  )
        |}""".stripMargin
    )
  }

  test("an explicit level of detail sample with a depth reference renders the comparison of each tap") {
    assertEquals(
      shown("OpImageSampleDrefExplicitLod"),
      """def OpImageSampleDrefExplicitLod(image: UInt[64], coordinate_0: Float32, coordinate_1: Float32, reference: Float32, lod: Float32)(using fragments: Fragments, images: Images, machine: Machine): (result: Float32) = {
        |  fragments.partials(124)
        |  val v0 = images.dimensions(image)
        |  val v1 = images.arrayed(image)
        |  val v2 = images.cube(image)
        |  images.numeric(coordinate_0.bits.to[UInt[64]], coordinate_1.bits.to[UInt[64]])
        |  for (t3 <- fragments.indices(UInt[64](2))) {
        |    fragments.setPartial(UInt[64](16) + t3, if (0 != 0) Sampling.converted(if (t3 == UInt[64](0)) coordinate_0.bits.to[UInt[64]] else coordinate_1.bits.to[UInt[64]]) else if (t3 == UInt[64](0)) coordinate_0.bits.to[UInt[64]] else coordinate_1.bits.to[UInt[64]])
        |  }
        |  fragments.setPartial(UInt[64](49), Sampling.reference(reference.bits.to[UInt[64]], UInt[64](0x3f800000)))
        |  images.samplerWords(image, 0)
        |  val v3 = images.formatWord(image)
        |  val v4 = images.levelCount(image)
        |  fragments.setPartial(UInt[64](48), UInt[64](1))
        |  for (t6 <- fragments.indices(v0 + v1)) {
        |    fragments.setPartial(UInt[64](48), fragments.partial(UInt[64](48)) & Sampling.finite(fragments.partial(UInt[64](16) + t6)))
        |  }
        |  machine.require((((v0 + v1).to[SInt[64]] <= 2).to[UInt[1]].to[UInt[64]] & fragments.partial(UInt[64](48))) != UInt[64](0), "sample coordinates")
        |  for (t7 <- fragments.indices(v0)) {
        |    fragments.setPartial(UInt[64](24) + t7, Sampling.widen(fragments.partial(UInt[64](16) + t7)))
        |  }
        |  if (v2 != UInt[64](0)) {
        |    fragments.setPartial(UInt[64](27), Sampling.face(fragments.partial(UInt[64](24)), fragments.partial(UInt[64](25)), fragments.partial(UInt[64](26))))
        |    fragments.setPartial(UInt[64](28), Sampling.faceS(fragments.partial(UInt[64](24)), fragments.partial(UInt[64](25)), fragments.partial(UInt[64](26))))
        |    fragments.setPartial(UInt[64](29), Sampling.faceT(fragments.partial(UInt[64](24)), fragments.partial(UInt[64](25)), fragments.partial(UInt[64](26))))
        |  }
        |  machine.require(v2 == UInt[64](0) || fragments.partial(UInt[64](4)) == UInt[64](0), "cube coordinates")
        |  machine.require(1 == 1, "sample LOD scalar")
        |  fragments.setPartial(UInt[64](50), Sampling.lambda(fragments.partial(UInt[64](9)), fragments.partial(UInt[64](10)), Sampling.widen(lod.bits.to[UInt[64]]), Sampling.bias(fragments.partial(UInt[64](8)), UInt[64](0))))
        |  fragments.setPartial(UInt[64](51), if (Sampling.positive(fragments.partial(UInt[64](50))) != UInt[64](0)) fragments.partial(UInt[64](0)) else fragments.partial(UInt[64](1)))
        |  fragments.setPartial(UInt[64](52), Sampling.selected(fragments.partial(UInt[64](50)), v4))
        |  fragments.setPartial(UInt[64](53), UInt[64](if (fragments.partial(UInt[64](11)) == UInt[64](0)) 1 else 2))
        |  machine.require(fragments.partial(UInt[64](51)) == UInt[64](0) || fragments.partial(UInt[64](51)) == UInt[64](1), "sample filter")
        |  machine.require(fragments.partial(UInt[64](51)) == UInt[64](0) || Sampling.integral(v3.to[UInt[28]].to[UInt[64]]) == UInt[64](0), "integer filter")
        |  machine.require((images.vulkan() | (UInt[1](1) == (fragments.partial(UInt[64](5)) != UInt[64](0)).to[UInt[1]]).to[UInt[1]].to[UInt[64]]) != UInt[64](0), "depth comparison")
        |  for (t8 <- fragments.indices(UInt[64](if (fragments.partial(UInt[64](53)) == UInt[64](2)) 2 else 1))) {
        |    images.level(image, Sampling.level(fragments.partial(UInt[64](52)), v4, fragments.partial(UInt[64](53)), t8), 56)
        |    fragments.setPartial(UInt[64](108), Sampling.texelSpace(if (v2 != UInt[64](0)) fragments.partial(UInt[64](28)) else fragments.partial(UInt[64](24)), fragments.partial(UInt[64](56)), fragments.partial(UInt[64](37)), fragments.partial(UInt[64](4))))
        |    fragments.setPartial(UInt[64](109), if (v0 == UInt[64](1)) UInt[64](0x3fe0000000000000L) else Sampling.texelSpace(if (v2 != UInt[64](0)) fragments.partial(UInt[64](29)) else fragments.partial(UInt[64](25)), fragments.partial(UInt[64](57)), fragments.partial(UInt[64](38)), fragments.partial(UInt[64](4))))
        |    fragments.setPartial(UInt[64](110), if (v0 == UInt[64](3) && v2 == UInt[64](0)) Sampling.texelSpace(fragments.partial(UInt[64](26)), fragments.partial(UInt[64](58)), fragments.partial(UInt[64](39)), fragments.partial(UInt[64](4))) else UInt[64](0x3fe0000000000000L))
        |    fragments.setPartial(UInt[64](114), Sampling.components(v3.to[UInt[28]].to[UInt[64]]))
        |    fragments.setPartial(UInt[64](115), if (v1 != UInt[64](0)) Sampling.layer(fragments.partial(UInt[64](16) + v0), if (v2 != UInt[64](0)) fragments.partial(UInt[64](59)) / 6 else fragments.partial(UInt[64](59))) else UInt[64](0))
        |    fragments.setPartial(UInt[64](111), if (fragments.partial(UInt[64](51)) == UInt[64](0)) Sampling.nearest(fragments.partial(UInt[64](108))) else Sampling.lowest(fragments.partial(UInt[64](108))))
        |    fragments.setPartial(UInt[64](112), if (fragments.partial(UInt[64](51)) == UInt[64](0)) Sampling.nearest(fragments.partial(UInt[64](109))) else Sampling.lowest(fragments.partial(UInt[64](109))))
        |    fragments.setPartial(UInt[64](113), if (fragments.partial(UInt[64](51)) == UInt[64](0)) Sampling.nearest(fragments.partial(UInt[64](110))) else Sampling.lowest(fragments.partial(UInt[64](110))))
        |    for (t9 <- fragments.indices(UInt[64](if (fragments.partial(UInt[64](51)) == UInt[64](0)) 1 else if (v0 == UInt[64](3) && v2 == UInt[64](0)) 8 else 4))) {
        |      fragments.setPartial(UInt[64](116), fragments.partial(UInt[64](111)) + t9.to[UInt[1]].to[UInt[64]])
        |      fragments.setPartial(UInt[64](117), fragments.partial(UInt[64](112)) + (t9 >> 1).to[UInt[1]].to[UInt[64]])
        |      fragments.setPartial(UInt[64](118), fragments.partial(UInt[64](113)) + (t9 >> 2))
        |      fragments.setPartial(UInt[64](119), Sampling.address(fragments.partial(UInt[64](116)), fragments.partial(UInt[64](56)), fragments.partial(UInt[64](2))))
        |      fragments.setPartial(UInt[64](120), Sampling.address(fragments.partial(UInt[64](117)), fragments.partial(UInt[64](57)), fragments.partial(UInt[64](3))))
        |      fragments.setPartial(UInt[64](121), if (v0 == UInt[64](3) && v2 == UInt[64](0)) Sampling.address(fragments.partial(UInt[64](118)), fragments.partial(UInt[64](58)), fragments.partial(UInt[64](12))) else UInt[64](0))
        |      fragments.setPartial(UInt[64](122), ((fragments.partial(UInt[64](119)) | fragments.partial(UInt[64](120)) | fragments.partial(UInt[64](121))).to[SInt[64]] < 0).to[UInt[64]])
        |      fragments.setPartial(UInt[64](123), if (v2 != UInt[64](0)) Sampling.cubeTaps(fragments.partial(UInt[64](27)), fragments.partial(UInt[64](116)), fragments.partial(UInt[64](117)), fragments.partial(UInt[64](56)), (fragments.partial(UInt[64](51)) == UInt[64](1)).to[UInt[64]]) else UInt[64](1))
        |      machine.require(v2 == UInt[64](0) || (fragments.partial(UInt[64](56)) == fragments.partial(UInt[64](57)) && (fragments.partial(UInt[64](59)) >= 6 && fragments.partial(UInt[64](59)) % 6 == UInt[64](0))), "cube image faces")
        |      for (t10 <- fragments.indices(UInt[64](4))) {
        |        fragments.setPartial(UInt[64](68) + (t9 * 4 + t10), UInt[64](0))
        |      }
        |      for (t11 <- fragments.indices(fragments.partial(UInt[64](123)))) {
        |        for (t12 <- fragments.indices(UInt[64](4))) {
        |          fragments.setPartial(UInt[64](64) + t12, Sampling.border(fragments.partial(UInt[64](7)), fragments.partial(UInt[64](114)), t12))
        |        }
        |        if ((v2 | (fragments.partial(UInt[64](122)) == UInt[64](0)).to[UInt[1]].to[UInt[64]]) != UInt[64](0)) {
        |          images.texel(if (v2 != UInt[64](0)) fragments.partial(UInt[64](115)) * 6 + Sampling.cubeFace(fragments.partial(UInt[64](27)), fragments.partial(UInt[64](116)), fragments.partial(UInt[64](117)), fragments.partial(UInt[64](56)), (fragments.partial(UInt[64](51)) == UInt[64](1)).to[UInt[64]], t11) else fragments.partial(UInt[64](115)), if (v2 != UInt[64](0)) Sampling.cubeX(fragments.partial(UInt[64](27)), fragments.partial(UInt[64](116)), fragments.partial(UInt[64](117)), fragments.partial(UInt[64](56)), (fragments.partial(UInt[64](51)) == UInt[64](1)).to[UInt[64]], t11) else fragments.partial(UInt[64](119)), if (v2 != UInt[64](0)) Sampling.cubeY(fragments.partial(UInt[64](27)), fragments.partial(UInt[64](116)), fragments.partial(UInt[64](117)), fragments.partial(UInt[64](56)), (fragments.partial(UInt[64](51)) == UInt[64](1)).to[UInt[64]], t11) else fragments.partial(UInt[64](121)) * fragments.partial(UInt[64](57)) + fragments.partial(UInt[64](120)), 64)
        |        }
        |        for (t13 <- fragments.indices(UInt[64](1))) {
        |          fragments.setPartial(UInt[64](68) + (t9 * 4 + t13), Sampling.accumulated(fragments.partial(UInt[64](68) + (t9 * 4 + t13)), Sampling.compared(fragments.partial(UInt[64](6)), fragments.partial(UInt[64](49)), fragments.partial(UInt[64](64) + t13), v3.to[UInt[28]].to[UInt[64]]), fragments.partial(UInt[64](123)), v2))
        |        }
        |      }
        |    }
        |    for (t14 <- fragments.indices(UInt[64](1))) {
        |      fragments.setPartial(UInt[64](100) + (t8 * 4 + t14), if (fragments.partial(UInt[64](51)) == UInt[64](0)) fragments.partial(UInt[64](68) + t14) else if (v0 == UInt[64](3) && v2 == UInt[64](0)) Sampling.lerp(Sampling.bilinear(fragments.partial(UInt[64](68) + t14), fragments.partial(UInt[64](72) + t14), fragments.partial(UInt[64](76) + t14), fragments.partial(UInt[64](80) + t14), Sampling.fraction(fragments.partial(UInt[64](108)), fragments.partial(UInt[64](111))), Sampling.fraction(fragments.partial(UInt[64](109)), fragments.partial(UInt[64](112)))), Sampling.bilinear(fragments.partial(UInt[64](84) + t14), fragments.partial(UInt[64](88) + t14), fragments.partial(UInt[64](92) + t14), fragments.partial(UInt[64](96) + t14), Sampling.fraction(fragments.partial(UInt[64](108)), fragments.partial(UInt[64](111))), Sampling.fraction(fragments.partial(UInt[64](109)), fragments.partial(UInt[64](112)))), Sampling.fraction(fragments.partial(UInt[64](110)), fragments.partial(UInt[64](113)))) else Sampling.bilinear(fragments.partial(UInt[64](68) + t14), fragments.partial(UInt[64](72) + t14), fragments.partial(UInt[64](76) + t14), fragments.partial(UInt[64](80) + t14), Sampling.fraction(fragments.partial(UInt[64](108)), fragments.partial(UInt[64](111))), Sampling.fraction(fragments.partial(UInt[64](109)), fragments.partial(UInt[64](112)))))
        |    }
        |  }
        |  for (t15 <- fragments.indices(UInt[64](1))) {
        |    fragments.setPartial(UInt[64](100) + t15, Sampling.mixed(fragments.partial(UInt[64](100) + t15), fragments.partial(UInt[64](104) + t15), if (fragments.partial(UInt[64](53)) == UInt[64](2)) Sampling.between(fragments.partial(UInt[64](52))) else UInt[64](0)))
        |  }
        |  val v5 = Sampling.packed(UInt[64](0), fragments.partial(UInt[64](100)))
        |  (result = v5.to[UInt[32]].asFloat)
        |}""".stripMargin
    )
  }

  test("a gather renders the swizzled component of each of its four taps") {
    assertEquals(
      shown("OpImageGather"),
      """def OpImageGather(image: UInt[64], coordinate_0: Float32, coordinate_1: Float32, component: UInt[32])(using fragments: Fragments, images: Images, machine: Machine): (result_0: Float32, result_1: Float32, result_2: Float32, result_3: Float32) = {
        |  fragments.partials(124)
        |  val v0 = images.dimensions(image)
        |  val v1 = images.arrayed(image)
        |  val v2 = images.cube(image)
        |  images.numeric(coordinate_0.bits.to[UInt[64]], coordinate_1.bits.to[UInt[64]])
        |  for (t3 <- fragments.indices(UInt[64](2))) {
        |    fragments.setPartial(UInt[64](16) + t3, if (0 != 0) Sampling.converted(if (t3 == UInt[64](0)) coordinate_0.bits.to[UInt[64]] else coordinate_1.bits.to[UInt[64]]) else if (t3 == UInt[64](0)) coordinate_0.bits.to[UInt[64]] else coordinate_1.bits.to[UInt[64]])
        |  }
        |  machine.require(component < 4, "gather component")
        |  machine.require(((v0 == UInt[64](2)).to[UInt[1]].to[UInt[64]] | v2) != UInt[64](0), "gather image")
        |  images.samplerWords(image, 0)
        |  val v3 = images.formatWord(image)
        |  val v4 = images.levelCount(image)
        |  fragments.setPartial(UInt[64](48), UInt[64](1))
        |  for (t6 <- fragments.indices(v0 + v1)) {
        |    fragments.setPartial(UInt[64](48), fragments.partial(UInt[64](48)) & Sampling.finite(fragments.partial(UInt[64](16) + t6)))
        |  }
        |  machine.require((((v0 + v1).to[SInt[64]] <= 2).to[UInt[1]].to[UInt[64]] & fragments.partial(UInt[64](48))) != UInt[64](0), "sample coordinates")
        |  for (t7 <- fragments.indices(v0)) {
        |    fragments.setPartial(UInt[64](24) + t7, Sampling.widen(fragments.partial(UInt[64](16) + t7)))
        |  }
        |  if (v2 != UInt[64](0)) {
        |    fragments.setPartial(UInt[64](27), Sampling.face(fragments.partial(UInt[64](24)), fragments.partial(UInt[64](25)), fragments.partial(UInt[64](26))))
        |    fragments.setPartial(UInt[64](28), Sampling.faceS(fragments.partial(UInt[64](24)), fragments.partial(UInt[64](25)), fragments.partial(UInt[64](26))))
        |    fragments.setPartial(UInt[64](29), Sampling.faceT(fragments.partial(UInt[64](24)), fragments.partial(UInt[64](25)), fragments.partial(UInt[64](26))))
        |  }
        |  machine.require(v2 == UInt[64](0) || fragments.partial(UInt[64](4)) == UInt[64](0), "cube coordinates")
        |  fragments.setPartial(UInt[64](41), UInt[64](1))
        |  fragments.setPartial(UInt[64](42), UInt[64](1))
        |  fragments.setPartial(UInt[64](43), UInt[64](1))
        |  fragments.setPartial(UInt[64](44), UInt[64](1))
        |  if (0 != 0) {
        |    images.level(image, UInt[64](0), 56)
        |  }
        |  fragments.setPartial(UInt[64](50), Sampling.lambda(fragments.partial(UInt[64](9)), fragments.partial(UInt[64](10)), if (0 != 0) Sampling.detail(Sampling.scaled(if (v2 != UInt[64](0)) Sampling.gradientS(fragments.partial(UInt[64](24)), fragments.partial(UInt[64](25)), fragments.partial(UInt[64](26)), fragments.partial(UInt[64](31)), fragments.partial(UInt[64](32)), fragments.partial(UInt[64](33))) else fragments.partial(UInt[64](31)), fragments.partial(UInt[64](56)), fragments.partial(UInt[64](4))), Sampling.scaled(if (v2 != UInt[64](0)) Sampling.gradientT(fragments.partial(UInt[64](24)), fragments.partial(UInt[64](25)), fragments.partial(UInt[64](26)), fragments.partial(UInt[64](31)), fragments.partial(UInt[64](32)), fragments.partial(UInt[64](33))) else fragments.partial(UInt[64](32)), fragments.partial(UInt[64](57)), fragments.partial(UInt[64](4))), Sampling.scaled(if (v2 != UInt[64](0)) UInt[64](0) else fragments.partial(UInt[64](33)), fragments.partial(UInt[64](58)), fragments.partial(UInt[64](4))), Sampling.scaled(if (v2 != UInt[64](0)) Sampling.gradientS(fragments.partial(UInt[64](24)), fragments.partial(UInt[64](25)), fragments.partial(UInt[64](26)), fragments.partial(UInt[64](34)), fragments.partial(UInt[64](35)), fragments.partial(UInt[64](36))) else fragments.partial(UInt[64](34)), fragments.partial(UInt[64](56)), fragments.partial(UInt[64](4))), Sampling.scaled(if (v2 != UInt[64](0)) Sampling.gradientT(fragments.partial(UInt[64](24)), fragments.partial(UInt[64](25)), fragments.partial(UInt[64](26)), fragments.partial(UInt[64](34)), fragments.partial(UInt[64](35)), fragments.partial(UInt[64](36))) else fragments.partial(UInt[64](35)), fragments.partial(UInt[64](57)), fragments.partial(UInt[64](4))), Sampling.scaled(if (v2 != UInt[64](0)) UInt[64](0) else fragments.partial(UInt[64](36)), fragments.partial(UInt[64](58)), fragments.partial(UInt[64](4)))) else UInt[64](0), Sampling.bias(fragments.partial(UInt[64](8)), UInt[64](0))))
        |  fragments.setPartial(UInt[64](51), UInt[64](1))
        |  fragments.setPartial(UInt[64](52), Sampling.selected(fragments.partial(UInt[64](50)), v4))
        |  fragments.setPartial(UInt[64](53), UInt[64](0))
        |  machine.require(fragments.partial(UInt[64](51)) == UInt[64](0) || fragments.partial(UInt[64](51)) == UInt[64](1), "sample filter")
        |  machine.require((images.vulkan() | (UInt[1](0) == (fragments.partial(UInt[64](5)) != UInt[64](0)).to[UInt[1]]).to[UInt[1]].to[UInt[64]]) != UInt[64](0), "depth comparison")
        |  for (t8 <- fragments.indices(UInt[64](if (fragments.partial(UInt[64](53)) == UInt[64](2)) 2 else 1))) {
        |    images.level(image, Sampling.level(fragments.partial(UInt[64](52)), v4, fragments.partial(UInt[64](53)), t8), 56)
        |    fragments.setPartial(UInt[64](108), Sampling.texelSpace(if (v2 != UInt[64](0)) fragments.partial(UInt[64](28)) else fragments.partial(UInt[64](24)), fragments.partial(UInt[64](56)), fragments.partial(UInt[64](37)), fragments.partial(UInt[64](4))))
        |    fragments.setPartial(UInt[64](109), if (v0 == UInt[64](1)) UInt[64](0x3fe0000000000000L) else Sampling.texelSpace(if (v2 != UInt[64](0)) fragments.partial(UInt[64](29)) else fragments.partial(UInt[64](25)), fragments.partial(UInt[64](57)), fragments.partial(UInt[64](38)), fragments.partial(UInt[64](4))))
        |    fragments.setPartial(UInt[64](110), if (v0 == UInt[64](3) && v2 == UInt[64](0)) Sampling.texelSpace(fragments.partial(UInt[64](26)), fragments.partial(UInt[64](58)), fragments.partial(UInt[64](39)), fragments.partial(UInt[64](4))) else UInt[64](0x3fe0000000000000L))
        |    fragments.setPartial(UInt[64](114), Sampling.components(v3.to[UInt[28]].to[UInt[64]]))
        |    fragments.setPartial(UInt[64](115), if (v1 != UInt[64](0)) Sampling.layer(fragments.partial(UInt[64](16) + v0), if (v2 != UInt[64](0)) fragments.partial(UInt[64](59)) / 6 else fragments.partial(UInt[64](59))) else UInt[64](0))
        |    fragments.setPartial(UInt[64](111), if (fragments.partial(UInt[64](51)) == UInt[64](0)) Sampling.nearest(fragments.partial(UInt[64](108))) else Sampling.lowest(fragments.partial(UInt[64](108))))
        |    fragments.setPartial(UInt[64](112), if (fragments.partial(UInt[64](51)) == UInt[64](0)) Sampling.nearest(fragments.partial(UInt[64](109))) else Sampling.lowest(fragments.partial(UInt[64](109))))
        |    fragments.setPartial(UInt[64](113), if (fragments.partial(UInt[64](51)) == UInt[64](0)) Sampling.nearest(fragments.partial(UInt[64](110))) else Sampling.lowest(fragments.partial(UInt[64](110))))
        |    for (t9 <- fragments.indices(UInt[64](4))) {
        |      fragments.setPartial(UInt[64](116), fragments.partial(UInt[64](111)) + fragments.partial(UInt[64](40) + t9 * 2))
        |      fragments.setPartial(UInt[64](117), fragments.partial(UInt[64](112)) + fragments.partial(UInt[64](40) + (t9 * 2 + 1)))
        |      fragments.setPartial(UInt[64](118), fragments.partial(UInt[64](113)) + 0)
        |      fragments.setPartial(UInt[64](119), Sampling.address(fragments.partial(UInt[64](116)), fragments.partial(UInt[64](56)), fragments.partial(UInt[64](2))))
        |      fragments.setPartial(UInt[64](120), Sampling.address(fragments.partial(UInt[64](117)), fragments.partial(UInt[64](57)), fragments.partial(UInt[64](3))))
        |      fragments.setPartial(UInt[64](121), if (v0 == UInt[64](3) && v2 == UInt[64](0)) Sampling.address(fragments.partial(UInt[64](118)), fragments.partial(UInt[64](58)), fragments.partial(UInt[64](12))) else UInt[64](0))
        |      fragments.setPartial(UInt[64](122), ((fragments.partial(UInt[64](119)) | fragments.partial(UInt[64](120)) | fragments.partial(UInt[64](121))).to[SInt[64]] < 0).to[UInt[64]])
        |      fragments.setPartial(UInt[64](123), UInt[64](1))
        |      machine.require(v2 == UInt[64](0) || (fragments.partial(UInt[64](56)) == fragments.partial(UInt[64](57)) && (fragments.partial(UInt[64](59)) >= 6 && fragments.partial(UInt[64](59)) % 6 == UInt[64](0))), "cube image faces")
        |      for (t10 <- fragments.indices(UInt[64](4))) {
        |        fragments.setPartial(UInt[64](68) + (t9 * 4 + t10), UInt[64](0))
        |      }
        |      for (t11 <- fragments.indices(fragments.partial(UInt[64](123)))) {
        |        for (t12 <- fragments.indices(UInt[64](4))) {
        |          fragments.setPartial(UInt[64](64) + t12, Sampling.border(fragments.partial(UInt[64](7)), fragments.partial(UInt[64](114)), t12))
        |        }
        |        if ((v2 | (fragments.partial(UInt[64](122)) == UInt[64](0)).to[UInt[1]].to[UInt[64]]) != UInt[64](0)) {
        |          images.texel(if (v2 != UInt[64](0)) fragments.partial(UInt[64](115)) * 6 + Sampling.cubeFace(fragments.partial(UInt[64](27)), fragments.partial(UInt[64](116)), fragments.partial(UInt[64](117)), fragments.partial(UInt[64](56)), (fragments.partial(UInt[64](51)) == UInt[64](1)).to[UInt[64]], t11) else fragments.partial(UInt[64](115)), if (v2 != UInt[64](0)) Sampling.cubeX(fragments.partial(UInt[64](27)), fragments.partial(UInt[64](116)), fragments.partial(UInt[64](117)), fragments.partial(UInt[64](56)), (fragments.partial(UInt[64](51)) == UInt[64](1)).to[UInt[64]], t11) else fragments.partial(UInt[64](119)), if (v2 != UInt[64](0)) Sampling.cubeY(fragments.partial(UInt[64](27)), fragments.partial(UInt[64](116)), fragments.partial(UInt[64](117)), fragments.partial(UInt[64](56)), (fragments.partial(UInt[64](51)) == UInt[64](1)).to[UInt[64]], t11) else fragments.partial(UInt[64](121)) * fragments.partial(UInt[64](57)) + fragments.partial(UInt[64](120)), 64)
        |        }
        |        for (t13 <- fragments.indices(UInt[64](4))) {
        |          fragments.setPartial(UInt[64](68) + (t9 * 4 + t13), Sampling.accumulated(fragments.partial(UInt[64](68) + (t9 * 4 + t13)), fragments.partial(UInt[64](64) + t13), fragments.partial(UInt[64](123)), v2))
        |        }
        |      }
        |      images.swizzle(image, UInt[64](68) + t9 * 4)
        |    }
        |    for (t14 <- fragments.indices(UInt[64](4))) {
        |      fragments.setPartial(UInt[64](100) + (t8 * 4 + t14), fragments.partial(UInt[64](68) + (t14 * 4 + component.to[UInt[64]])))
        |    }
        |  }
        |  for (t15 <- fragments.indices(UInt[64](4))) {
        |    fragments.setPartial(UInt[64](100) + t15, Sampling.mixed(fragments.partial(UInt[64](100) + t15), fragments.partial(UInt[64](104) + t15), if (fragments.partial(UInt[64](53)) == UInt[64](2)) Sampling.between(fragments.partial(UInt[64](52))) else UInt[64](0)))
        |  }
        |  val v5 = Sampling.packed(Sampling.integral(v3.to[UInt[28]].to[UInt[64]]), fragments.partial(UInt[64](100)))
        |  val v6 = Sampling.packed(Sampling.integral(v3.to[UInt[28]].to[UInt[64]]), fragments.partial(UInt[64](101)))
        |  val v7 = Sampling.packed(Sampling.integral(v3.to[UInt[28]].to[UInt[64]]), fragments.partial(UInt[64](102)))
        |  val v8 = Sampling.packed(Sampling.integral(v3.to[UInt[28]].to[UInt[64]]), fragments.partial(UInt[64](103)))
        |  (
        |    result_0 = v5.to[UInt[32]].asFloat,
        |    result_1 = v6.to[UInt[32]].asFloat,
        |    result_2 = v7.to[UInt[32]].asFloat,
        |    result_3 = v8.to[UInt[32]].asFloat
        |  )
        |}""".stripMargin
    )
  }

  test("a level of detail query renders the gradients, the sampler range and the reported levels") {
    assertEquals(
      shown("OpImageQueryLod"),
      """def OpImageQueryLod(coordinate_0: Float32, coordinate_1: Float32, image: UInt[64])(using fragments: Fragments, images: Images, machine: Machine): (result_0: Float32, result_1: Float32) = {
        |  fragments.partials(124)
        |  images.numeric(coordinate_0.bits.to[UInt[64]], coordinate_1.bits.to[UInt[64]])
        |  val v0 = images.dimensions(image)
        |  val _ = images.arrayed(image)
        |  val v2 = images.cube(image)
        |  machine.require(v0.to[SInt[64]] <= 2, "query coordinates")
        |  for (t3 <- fragments.indices(UInt[64](2))) {
        |    fragments.setPartial(UInt[64](16) + t3, if (t3 == UInt[64](0)) coordinate_0.bits.to[UInt[64]] else coordinate_1.bits.to[UInt[64]])
        |  }
        |  val v3 = images.gradients(16, UInt[64](2), 30)
        |  images.sampler(image)
        |  images.level(image, UInt[64](0), 56)
        |  val v4 = images.levelCount(image)
        |  val v5 = images.samplerWord(image, 8)
        |  val v6 = images.samplerWord(image, 9)
        |  val v7 = images.samplerWord(image, 10)
        |  val v8 = images.samplerWord(image, 11)
        |  for (t10 <- fragments.indices(v0)) {
        |    fragments.setPartial(UInt[64](24) + t10, Sampling.widen(fragments.partial(UInt[64](16) + t10)))
        |  }
        |  if (v2 != UInt[64](0)) {
        |    fragments.setPartial(UInt[64](27), Sampling.face(fragments.partial(UInt[64](24)), fragments.partial(UInt[64](25)), fragments.partial(UInt[64](26))))
        |    fragments.setPartial(UInt[64](28), Sampling.faceS(fragments.partial(UInt[64](24)), fragments.partial(UInt[64](25)), fragments.partial(UInt[64](26))))
        |    fragments.setPartial(UInt[64](29), Sampling.faceT(fragments.partial(UInt[64](24)), fragments.partial(UInt[64](25)), fragments.partial(UInt[64](26))))
        |  }
        |  val v9 = Matrices.narrow(Sampling.reported(v8, Sampling.clamped(v6, v7, v4, Matrices.add(if (v3 != UInt[64](0)) Sampling.detail(Sampling.scaled(if (v2 != UInt[64](0)) Sampling.gradientS(fragments.partial(UInt[64](24)), fragments.partial(UInt[64](25)), fragments.partial(UInt[64](26)), fragments.partial(UInt[64](31)), fragments.partial(UInt[64](32)), fragments.partial(UInt[64](33))) else fragments.partial(UInt[64](31)), fragments.partial(UInt[64](56)), UInt[64](0)), Sampling.scaled(if (v2 != UInt[64](0)) Sampling.gradientT(fragments.partial(UInt[64](24)), fragments.partial(UInt[64](25)), fragments.partial(UInt[64](26)), fragments.partial(UInt[64](31)), fragments.partial(UInt[64](32)), fragments.partial(UInt[64](33))) else fragments.partial(UInt[64](32)), fragments.partial(UInt[64](57)), UInt[64](0)), Sampling.scaled(if (v2 != UInt[64](0)) UInt[64](0) else fragments.partial(UInt[64](33)), fragments.partial(UInt[64](58)), UInt[64](0)), Sampling.scaled(if (v2 != UInt[64](0)) Sampling.gradientS(fragments.partial(UInt[64](24)), fragments.partial(UInt[64](25)), fragments.partial(UInt[64](26)), fragments.partial(UInt[64](34)), fragments.partial(UInt[64](35)), fragments.partial(UInt[64](36))) else fragments.partial(UInt[64](34)), fragments.partial(UInt[64](56)), UInt[64](0)), Sampling.scaled(if (v2 != UInt[64](0)) Sampling.gradientT(fragments.partial(UInt[64](24)), fragments.partial(UInt[64](25)), fragments.partial(UInt[64](26)), fragments.partial(UInt[64](34)), fragments.partial(UInt[64](35)), fragments.partial(UInt[64](36))) else fragments.partial(UInt[64](35)), fragments.partial(UInt[64](57)), UInt[64](0)), Sampling.scaled(if (v2 != UInt[64](0)) UInt[64](0) else fragments.partial(UInt[64](36)), fragments.partial(UInt[64](58)), UInt[64](0))) else UInt[64](-0x10000000000000L), Sampling.queried(v5)))), UInt[64](32))
        |  val v10 = Matrices.narrow(Matrices.add(if (v3 != UInt[64](0)) Sampling.detail(Sampling.scaled(if (v2 != UInt[64](0)) Sampling.gradientS(fragments.partial(UInt[64](24)), fragments.partial(UInt[64](25)), fragments.partial(UInt[64](26)), fragments.partial(UInt[64](31)), fragments.partial(UInt[64](32)), fragments.partial(UInt[64](33))) else fragments.partial(UInt[64](31)), fragments.partial(UInt[64](56)), UInt[64](0)), Sampling.scaled(if (v2 != UInt[64](0)) Sampling.gradientT(fragments.partial(UInt[64](24)), fragments.partial(UInt[64](25)), fragments.partial(UInt[64](26)), fragments.partial(UInt[64](31)), fragments.partial(UInt[64](32)), fragments.partial(UInt[64](33))) else fragments.partial(UInt[64](32)), fragments.partial(UInt[64](57)), UInt[64](0)), Sampling.scaled(if (v2 != UInt[64](0)) UInt[64](0) else fragments.partial(UInt[64](33)), fragments.partial(UInt[64](58)), UInt[64](0)), Sampling.scaled(if (v2 != UInt[64](0)) Sampling.gradientS(fragments.partial(UInt[64](24)), fragments.partial(UInt[64](25)), fragments.partial(UInt[64](26)), fragments.partial(UInt[64](34)), fragments.partial(UInt[64](35)), fragments.partial(UInt[64](36))) else fragments.partial(UInt[64](34)), fragments.partial(UInt[64](56)), UInt[64](0)), Sampling.scaled(if (v2 != UInt[64](0)) Sampling.gradientT(fragments.partial(UInt[64](24)), fragments.partial(UInt[64](25)), fragments.partial(UInt[64](26)), fragments.partial(UInt[64](34)), fragments.partial(UInt[64](35)), fragments.partial(UInt[64](36))) else fragments.partial(UInt[64](35)), fragments.partial(UInt[64](57)), UInt[64](0)), Sampling.scaled(if (v2 != UInt[64](0)) UInt[64](0) else fragments.partial(UInt[64](36)), fragments.partial(UInt[64](58)), UInt[64](0))) else UInt[64](-0x10000000000000L), Sampling.queried(v5)), UInt[64](32))
        |  (
        |    result_0 = v9.to[UInt[32]].asFloat,
        |    result_1 = v10.to[UInt[32]].asFloat
        |  )
        |}""".stripMargin
    )
  }

  test("a cooperative matrix length renders as its component count") {
    assertEquals(
      shown("OpCooperativeMatrixLengthKHR"),
      """def OpCooperativeMatrixLengthKHR(): (result: UInt[32]) =
        |  (result = UInt[32](4))""".stripMargin
    )
  }

  test("a cooperative matrix transpose renders as a permutation of its components") {
    assertEquals(
      shown("OpCooperativeMatrixTransposeNV"),
      """def OpCooperativeMatrixTransposeNV(source_0: Float32, source_1: Float32, source_2: Float32, source_3: Float32, source_4: Float32, source_5: Float32): (result_0: Float32, result_1: Float32, result_2: Float32, result_3: Float32, result_4: Float32, result_5: Float32) =
        |  (
        |    result_0 = source_0,
        |    result_1 = source_3,
        |    result_2 = source_1,
        |    result_3 = source_4,
        |    result_4 = source_2,
        |    result_5 = source_5
        |  )""".stripMargin
    )
  }

  private def shownAll(opcode: String): Vec[String] = MachineSemantics.machine(opcode).map {
    case (_, Right(definition)) => ScalaBackend.show(definition)
    case other                  => fail(s"$opcode does not render: $other")
  }

  test("a per-element operation renders a call of its function for each component, in column order") {
    assertEquals(
      shownAll("OpCooperativeMatrixPerElementOpNV"),
      Vec(
        """def OpCooperativeMatrixPerElementOpNV(source_0: Float32, source_1: Float32, source_2: Float32, source_3: Float32, source_4: Float32, source_5: Float32)(using control: Control): (result_0: Float32, result_1: Float32, result_2: Float32, result_3: Float32, result_4: Float32, result_5: Float32) = {
          |  val v0 = control.called(4, 0, 0, source_0)
          |  val v1 = control.called(4, 1, 0, source_3)
          |  val v2 = control.called(4, 0, 1, source_1)
          |  val v3 = control.called(4, 1, 1, source_4)
          |  val v4 = control.called(4, 0, 2, source_2)
          |  val v5 = control.called(4, 1, 2, source_5)
          |  (
          |    result_0 = v0.to[UInt[32]].asFloat,
          |    result_1 = v2.to[UInt[32]].asFloat,
          |    result_2 = v4.to[UInt[32]].asFloat,
          |    result_3 = v1.to[UInt[32]].asFloat,
          |    result_4 = v3.to[UInt[32]].asFloat,
          |    result_5 = v5.to[UInt[32]].asFloat
          |  )
          |}""".stripMargin,
        """def OpCooperativeMatrixPerElementOpNV(operand_0: Float32, operand_1: Float32, operand_2: Float32, operand_3: Float32, operand_4: Float32, operand_5: Float32, source_0: Float32, source_1: Float32, source_2: Float32, source_3: Float32, source_4: Float32, source_5: Float32)(using control: Control): (result_0: Float32, result_1: Float32, result_2: Float32, result_3: Float32, result_4: Float32, result_5: Float32) = {
          |  val v0 = control.called(4, 0, 0, source_0, operand_0)
          |  val v1 = control.called(4, 1, 0, source_3, operand_3)
          |  val v2 = control.called(4, 0, 1, source_1, operand_1)
          |  val v3 = control.called(4, 1, 1, source_4, operand_4)
          |  val v4 = control.called(4, 0, 2, source_2, operand_2)
          |  val v5 = control.called(4, 1, 2, source_5, operand_5)
          |  (
          |    result_0 = v0.to[UInt[32]].asFloat,
          |    result_1 = v2.to[UInt[32]].asFloat,
          |    result_2 = v4.to[UInt[32]].asFloat,
          |    result_3 = v1.to[UInt[32]].asFloat,
          |    result_4 = v3.to[UInt[32]].asFloat,
          |    result_5 = v5.to[UInt[32]].asFloat
          |  )
          |}""".stripMargin
      )
    )
  }

  test("a matrix reduction renders the calls that fold each group of components") {
    assertEquals(
      shownAll("OpCooperativeMatrixReduceNV"),
      Vec(
        """def OpCooperativeMatrixReduceNV(source_0: Float32, source_1: Float32, source_2: Float32, source_3: Float32, source_4: Float32, source_5: Float32)(using control: Control): (result_0: Float32, result_1: Float32, result_2: Float32, result_3: Float32, result_4: Float32, result_5: Float32) = {
          |  val v0 = control.called(5, source_0, source_1)
          |  val v1 = control.called(5, v0, source_2)
          |  val v2 = control.called(5, source_3, source_4)
          |  val v3 = control.called(5, v2, source_5)
          |  val v4 = v1.to[UInt[32]].asFloat
          |  val v5 = v3.to[UInt[32]].asFloat
          |  (
          |    result_0 = v4,
          |    result_1 = v4,
          |    result_2 = v4,
          |    result_3 = v5,
          |    result_4 = v5,
          |    result_5 = v5
          |  )
          |}""".stripMargin,
        """def OpCooperativeMatrixReduceNV(source_0: Float32, source_1: Float32, source_2: Float32, source_3: Float32, source_4: Float32, source_5: Float32)(using control: Control): (result_0: Float32, result_1: Float32, result_2: Float32, result_3: Float32, result_4: Float32, result_5: Float32) = {
          |  val v0 = control.called(5, source_0, source_3)
          |  val v1 = control.called(5, source_1, source_4)
          |  val v2 = control.called(5, source_2, source_5)
          |  val v3 = v0.to[UInt[32]].asFloat
          |  val v4 = v1.to[UInt[32]].asFloat
          |  val v5 = v2.to[UInt[32]].asFloat
          |  (
          |    result_0 = v3,
          |    result_1 = v4,
          |    result_2 = v5,
          |    result_3 = v3,
          |    result_4 = v4,
          |    result_5 = v5
          |  )
          |}""".stripMargin,
        """def OpCooperativeMatrixReduceNV(source_0: Float32, source_1: Float32, source_2: Float32, source_3: Float32, source_4: Float32, source_5: Float32)(using control: Control): (result_0: Float32, result_1: Float32, result_2: Float32, result_3: Float32, result_4: Float32, result_5: Float32) = {
          |  val v0 = control.called(5, source_0, source_1)
          |  val v1 = control.called(5, v0, source_2)
          |  val v2 = control.called(5, v1, source_3)
          |  val v3 = control.called(5, v2, source_4)
          |  val v4 = control.called(5, v3, source_5)
          |  val v5 = v4.to[UInt[32]].asFloat
          |  (
          |    result_0 = v5,
          |    result_1 = v5,
          |    result_2 = v5,
          |    result_3 = v5,
          |    result_4 = v5,
          |    result_5 = v5
          |  )
          |}""".stripMargin,
        """def OpCooperativeMatrixReduceNV(source_0: Float32, source_1: Float32, source_2: Float32, source_3: Float32)(using control: Control): (result: Float32) = {
          |  val v0 = control.called(5, source_0, source_2)
          |  val v1 = control.called(5, v0, source_1)
          |  val v2 = control.called(5, v1, source_3)
          |  (result = v2.to[UInt[32]].asFloat)
          |}""".stripMargin
      )
    )
  }

  test("a cooperative matrix store renders as a store of each component") {
    assertEquals(
      shown("OpCooperativeMatrixStoreKHR"),
      """def OpCooperativeMatrixStoreKHR(source_0: Float32, source_1: Float32, source_2: Float32, source_3: Float32, layout: UInt[32], stride: UInt[32], pointer: UInt[64])(using machine: Machine, fragments: Fragments): Unit = {
        |  machine.require(layout < 2, "cooperative matrix layout")
        |  machine.store[UInt[32]](Space.Generic, pointer, source_0.bits)
        |  machine.store[UInt[32]](Space.Generic, pointer + (if (layout == UInt[32](1)) stride.to[UInt[64]] * fragments.strideBytes(pointer, 4) else UInt[64](4)), source_1.bits)
        |  machine.store[UInt[32]](Space.Generic, pointer + (if (layout == UInt[32](1)) UInt[64](4) else stride.to[UInt[64]] * fragments.strideBytes(pointer, 4)), source_2.bits)
        |  machine.store[UInt[32]](Space.Generic, pointer + (stride.to[UInt[64]] * fragments.strideBytes(pointer, 4) + 4), source_3.bits)
        |  ()
        |}""".stripMargin
    )
  }

  test("a cooperative matrix load renders as a load of each component") {
    assertEquals(
      shown("OpCooperativeMatrixLoadNV"),
      """def OpCooperativeMatrixLoadNV(columnMajor: UInt[32], stride: UInt[32], pointer: UInt[64])(using machine: Machine, fragments: Fragments): (result_0: Float32, result_1: Float32, result_2: Float32, result_3: Float32) = {
        |  val v0 = columnMajor != UInt[32](0)
        |  val v1 = stride.to[UInt[64]] * fragments.strideBytes(pointer, 4)
        |  (
        |    result_0 = machine.load[UInt[32]](Space.Generic, pointer).asFloat,
        |    result_1 = machine.load[UInt[32]](Space.Generic, pointer + (if (v0) v1 else UInt[64](4))).asFloat,
        |    result_2 = machine.load[UInt[32]](Space.Generic, pointer + (if (v0) UInt[64](4) else v1)).asFloat,
        |    result_3 = machine.load[UInt[32]](Space.Generic, pointer + (v1 + 4)).asFloat
        |  )
        |}""".stripMargin
    )
  }

  test("a matrix constructed from lines renders as a request for the rows of the subgroup") {
    assertEquals(
      shown("OpCompositeConstructCoopMatQCOM"),
      """def OpCompositeConstructCoopMatQCOM(lines_0: UInt[32], lines_1: UInt[32])(using groups: Groups): (result_0: UInt[32], result_1: UInt[32], result_2: UInt[32], result_3: UInt[32]) =
        |  (
        |    result_0 = groups.matrixRows(0, 2, 2, 0, 32, 32, lines_0, lines_1).to[UInt[32]],
        |    result_1 = groups.matrixRows(1, 2, 2, 0, 32, 32, lines_0, lines_1).to[UInt[32]],
        |    result_2 = groups.matrixRows(2, 2, 2, 0, 32, 32, lines_0, lines_1).to[UInt[32]],
        |    result_3 = groups.matrixRows(3, 2, 2, 0, 32, 32, lines_0, lines_1).to[UInt[32]]
        |  )""".stripMargin
    )
    assertEquals(
      shownAs("OpCompositeConstructCoopMatQCOM", "OpCompositeConstructCoopMatQCOM(B)"),
      """def OpCompositeConstructCoopMatQCOM(lines_0: UInt[32], lines_1: UInt[32])(using groups: Groups): (result_0: UInt[32], result_1: UInt[32], result_2: UInt[32], result_3: UInt[32]) =
        |  (
        |    result_0 = groups.matrixRows(0, 2, 2, 1, 32, 32, lines_0, lines_1).to[UInt[32]],
        |    result_1 = groups.matrixRows(1, 2, 2, 1, 32, 32, lines_0, lines_1).to[UInt[32]],
        |    result_2 = groups.matrixRows(2, 2, 2, 1, 32, 32, lines_0, lines_1).to[UInt[32]],
        |    result_3 = groups.matrixRows(3, 2, 2, 1, 32, 32, lines_0, lines_1).to[UInt[32]]
        |  )""".stripMargin
    )
  }

  test("a line extracted from a matrix renders as the leaves of the invocation's own lane") {
    assertEquals(
      shown("OpCompositeExtractCoopMatQCOM"),
      """def OpCompositeExtractCoopMatQCOM(matrix_0: UInt[32], matrix_1: UInt[32], matrix_2: UInt[32], matrix_3: UInt[32])(using groups: Groups): (result_0: UInt[32], result_1: UInt[32]) =
        |  (
        |    result_0 = groups.ownLeaf(0, 2, 2, 0, matrix_0, matrix_1, matrix_2, matrix_3).to[UInt[32]],
        |    result_1 = groups.ownLeaf(1, 2, 2, 0, matrix_0, matrix_1, matrix_2, matrix_3).to[UInt[32]]
        |  )""".stripMargin
    )
  }

  test("a cooperative vector product renders its loops over the rows and the columns") {
    assertEquals(
      shown("OpCooperativeVectorMatrixMulNV"),
      """def OpCooperativeVectorMatrixMulNV(input_0: Float32, input_1: Float32, input_2: Float32, input_3: Float32, rows: UInt[32], columns: UInt[32], inputKind: UInt[32], matrixKind: UInt[32], matrixOffset: UInt[32], layout: UInt[32], transpose: UInt[32], matrix: UInt[64])(using machine: Machine, fragments: Fragments): (result_0: Float32, result_1: Float32, result_2: Float32, result_3: Float32) = {
        |  machine.require(rows == UInt[32](4) && columns > 0, "cooperative vector product shape")
        |  machine.require(VectorKinds.defined(inputKind.to[UInt[64]]) != UInt[64](0), "cooperative vector input kind")
        |  machine.require(VectorKinds.defined(matrixKind.to[UInt[64]]) != UInt[64](0), "cooperative vector matrix kind")
        |  machine.require(layout < 4, "cooperative vector layout")
        |  machine.require(layout > 1 || transpose == UInt[32](0), "cooperative vector transpose")
        |  machine.require(layout > 1, "cooperative vector stride")
        |  machine.require(VectorKinds.packed(inputKind.to[UInt[64]]) == UInt[64](0), "cooperative vector packed input")
        |  machine.require((VectorKinds.packed(inputKind.to[UInt[64]]) | (columns == UInt[32](4)).to[UInt[1]].to[UInt[64]]) != UInt[64](0), "cooperative vector input length")
        |  fragments.partials(8)
        |  for (t0 <- fragments.indices(columns.to[UInt[64]])) {
        |    fragments.setPartial(UInt[64](4) + t0, VectorKinds.realOf(if (VectorKinds.packed(inputKind.to[UInt[64]]) != UInt[64](0)) ((if ((t0 >> 2) == UInt[64](0)) input_0.bits else if ((t0 >> 2) == UInt[64](1)) input_1.bits else if ((t0 >> 2) == UInt[64](2)) input_2.bits else input_3.bits) >> t0.to[UInt[2]].to[UInt[64]] * 8).to[UInt[8]].to[UInt[64]] else VectorKinds.fromFloat(if (t0 == UInt[64](0)) input_0.bits.to[UInt[64]] else if (t0 == UInt[64](1)) input_1.bits.to[UInt[64]] else if (t0 == UInt[64](2)) input_2.bits.to[UInt[64]] else input_3.bits.to[UInt[64]], UInt[64](32), inputKind.to[UInt[64]]), inputKind.to[UInt[64]]))
        |  }
        |  for (t1 <- fragments.indices(rows.to[UInt[64]])) {
        |    fragments.setPartial(t1, UInt[64](0))
        |  }
        |  for (t2 <- fragments.indices(rows.to[UInt[64]])) {
        |    for (t3 <- fragments.indices(columns.to[UInt[64]])) {
        |      val v0 = fragments.loadElement(matrix, matrixOffset.to[UInt[64]] + (if (layout < 2) if (layout == UInt[32](0)) t2 * 0 + t3 * VectorKinds.bytes(matrixKind.to[UInt[64]]) else t3 * 0 + t2 * VectorKinds.bytes(matrixKind.to[UInt[64]]) else if (transpose != UInt[32](0)) (t3 * rows.to[UInt[64]] + t2) * VectorKinds.bytes(matrixKind.to[UInt[64]]) else (t2 * columns.to[UInt[64]] + t3) * VectorKinds.bytes(matrixKind.to[UInt[64]])), VectorKinds.bytes(matrixKind.to[UInt[64]]))
        |      fragments.setPartial(t2, Matrices.add(fragments.partial(t2), Matrices.multiply(VectorKinds.realOf(v0, matrixKind.to[UInt[64]]), fragments.partial(UInt[64](4) + t3))))
        |    }
        |  }
        |  val v1 = Matrices.narrow(fragments.partial(UInt[64](0)), UInt[64](32))
        |  val v2 = Matrices.narrow(fragments.partial(UInt[64](1)), UInt[64](32))
        |  val v3 = Matrices.narrow(fragments.partial(UInt[64](2)), UInt[64](32))
        |  val v4 = Matrices.narrow(fragments.partial(UInt[64](3)), UInt[64](32))
        |  (
        |    result_0 = v1.to[UInt[32]].asFloat,
        |    result_1 = v2.to[UInt[32]].asFloat,
        |    result_2 = v3.to[UInt[32]].asFloat,
        |    result_3 = v4.to[UInt[32]].asFloat
        |  )
        |}""".stripMargin
    )
  }

  test("a cooperative vector product with a bias starts each row from its bias") {
    assertEquals(
      shown("OpCooperativeVectorMatrixMulAddNV"),
      """def OpCooperativeVectorMatrixMulAddNV(input_0: Float32, input_1: Float32, input_2: Float32, input_3: Float32, rows: UInt[32], columns: UInt[32], inputKind: UInt[32], matrixKind: UInt[32], biasKind: UInt[32], biasOffset: UInt[32], matrixOffset: UInt[32], layout: UInt[32], transpose: UInt[32], bias: UInt[64], matrix: UInt[64])(using machine: Machine, fragments: Fragments): (result_0: Float32, result_1: Float32, result_2: Float32, result_3: Float32) = {
        |  machine.require(rows == UInt[32](4) && columns > 0, "cooperative vector product shape")
        |  machine.require(VectorKinds.defined(inputKind.to[UInt[64]]) != UInt[64](0), "cooperative vector input kind")
        |  machine.require(VectorKinds.defined(matrixKind.to[UInt[64]]) != UInt[64](0), "cooperative vector matrix kind")
        |  machine.require(layout < 4, "cooperative vector layout")
        |  machine.require(layout > 1 || transpose == UInt[32](0), "cooperative vector transpose")
        |  machine.require(layout > 1, "cooperative vector stride")
        |  machine.require(VectorKinds.packed(inputKind.to[UInt[64]]) == UInt[64](0), "cooperative vector packed input")
        |  machine.require((VectorKinds.packed(inputKind.to[UInt[64]]) | (columns == UInt[32](4)).to[UInt[1]].to[UInt[64]]) != UInt[64](0), "cooperative vector input length")
        |  machine.require(VectorKinds.defined(biasKind.to[UInt[64]]) != UInt[64](0), "cooperative vector bias kind")
        |  fragments.partials(8)
        |  for (t0 <- fragments.indices(columns.to[UInt[64]])) {
        |    fragments.setPartial(UInt[64](4) + t0, VectorKinds.realOf(if (VectorKinds.packed(inputKind.to[UInt[64]]) != UInt[64](0)) ((if ((t0 >> 2) == UInt[64](0)) input_0.bits else if ((t0 >> 2) == UInt[64](1)) input_1.bits else if ((t0 >> 2) == UInt[64](2)) input_2.bits else input_3.bits) >> t0.to[UInt[2]].to[UInt[64]] * 8).to[UInt[8]].to[UInt[64]] else VectorKinds.fromFloat(if (t0 == UInt[64](0)) input_0.bits.to[UInt[64]] else if (t0 == UInt[64](1)) input_1.bits.to[UInt[64]] else if (t0 == UInt[64](2)) input_2.bits.to[UInt[64]] else input_3.bits.to[UInt[64]], UInt[64](32), inputKind.to[UInt[64]]), inputKind.to[UInt[64]]))
        |  }
        |  for (t1 <- fragments.indices(rows.to[UInt[64]])) {
        |    val v0 = fragments.loadElement(bias, biasOffset.to[UInt[64]] + t1 * VectorKinds.bytes(biasKind.to[UInt[64]]), VectorKinds.bytes(biasKind.to[UInt[64]]))
        |    fragments.setPartial(t1, VectorKinds.realOf(v0, biasKind.to[UInt[64]]))
        |  }
        |  for (t3 <- fragments.indices(rows.to[UInt[64]])) {
        |    for (t4 <- fragments.indices(columns.to[UInt[64]])) {
        |      val v1 = fragments.loadElement(matrix, matrixOffset.to[UInt[64]] + (if (layout < 2) if (layout == UInt[32](0)) t3 * 0 + t4 * VectorKinds.bytes(matrixKind.to[UInt[64]]) else t4 * 0 + t3 * VectorKinds.bytes(matrixKind.to[UInt[64]]) else if (transpose != UInt[32](0)) (t4 * rows.to[UInt[64]] + t3) * VectorKinds.bytes(matrixKind.to[UInt[64]]) else (t3 * columns.to[UInt[64]] + t4) * VectorKinds.bytes(matrixKind.to[UInt[64]])), VectorKinds.bytes(matrixKind.to[UInt[64]]))
        |      fragments.setPartial(t3, Matrices.add(fragments.partial(t3), Matrices.multiply(VectorKinds.realOf(v1, matrixKind.to[UInt[64]]), fragments.partial(UInt[64](4) + t4))))
        |    }
        |  }
        |  val v2 = Matrices.narrow(fragments.partial(UInt[64](0)), UInt[64](32))
        |  val v3 = Matrices.narrow(fragments.partial(UInt[64](1)), UInt[64](32))
        |  val v4 = Matrices.narrow(fragments.partial(UInt[64](2)), UInt[64](32))
        |  val v5 = Matrices.narrow(fragments.partial(UInt[64](3)), UInt[64](32))
        |  (
        |    result_0 = v2.to[UInt[32]].asFloat,
        |    result_1 = v3.to[UInt[32]].asFloat,
        |    result_2 = v4.to[UInt[32]].asFloat,
        |    result_3 = v5.to[UInt[32]].asFloat
        |  )
        |}""".stripMargin
    )
  }

  test("a cooperative vector load renders as a read of each component") {
    assertEquals(
      shown("OpCooperativeVectorLoadNV"),
      """def OpCooperativeVectorLoadNV(pointer: UInt[64], offset: UInt[32])(using fragments: Fragments): (result_0: Float32, result_1: Float32, result_2: Float32, result_3: Float32) =
        |  (
        |    result_0 = fragments.vectorLoad(0, pointer, offset),
        |    result_1 = fragments.vectorLoad(1, pointer, offset),
        |    result_2 = fragments.vectorLoad(2, pointer, offset),
        |    result_3 = fragments.vectorLoad(3, pointer, offset)
        |  )""".stripMargin
    )
  }

  test("a pipe write renders as a call on the pipe") {
    assertEquals(
      shown("OpWritePipe"),
      """def OpWritePipe(pipe: UInt[64], packet: UInt[64])(using pipes: Pipes): (result: UInt[32]) = {
        |  val v0 = pipes.write(pipe, packet)
        |  (result = v0)
        |}""".stripMargin
    )
  }

  test("a pipe reservation renders as a call on the pipe") {
    assertEquals(
      shown("OpReserveReadPipePackets"),
      """def OpReserveReadPipePackets(pipe: UInt[64], count: UInt[32])(using pipes: Pipes): (result: UInt[64]) = {
        |  val v0 = pipes.reserveRead(pipe, count)
        |  (result = v0)
        |}""".stripMargin
    )
  }

  test("a group pipe reservation renders as a call on the pipe under its scope") {
    assertEquals(
      shown("OpGroupReserveWritePipePackets"),
      """def OpGroupReserveWritePipePackets(scope: UInt[32], pipe: UInt[64], count: UInt[32])(using pipes: Pipes): (result: UInt[64]) = {
        |  val v0 = pipes.groupReserveWrite(scope, pipe, count)
        |  (result = v0)
        |}""".stripMargin
    )
  }

  test("a device enqueue renders as a call on the device queue") {
    assertEquals(
      shown("OpGetDefaultQueue"),
      """def OpGetDefaultQueue()(using deviceQueues: DeviceQueues): (result: UInt[64]) = {
        |  val v0 = deviceQueues.defaultQueue()
        |  (result = v0)
        |}""".stripMargin
    )
    assertEquals(
      shown("OpRetainEvent"),
      """def OpRetainEvent(event: UInt[64])(using deviceQueues: DeviceQueues): Unit = {
        |  deviceQueues.retainEvent(event)
        |  ()
        |}""".stripMargin
    )
  }

  test("a tensor layout update renders as a call on the layout handle") {
    assertEquals(
      shown("OpTensorLayoutSetClampValueNV"),
      """def OpTensorLayoutSetClampValueNV(layout: UInt[64], value: UInt[32])(using tensors: TensorAddressing): (result: UInt[64]) = {
        |  val v0 = tensors.setLayoutClampValue(layout, value)
        |  (result = v0)
        |}""".stripMargin
    )
  }

  test("each vendor memory instruction has one machine definition") {
    Vec(
      "OpConvertHandleToImageINTEL",
      "OpConvertHandleToSamplerINTEL",
      "OpRawAccessChainNV",
      "OpPredicatedLoadINTEL",
      "OpPredicatedStoreINTEL",
      "OpMaskedGatherINTEL",
      "OpMaskedScatterINTEL",
      "OpConvertHandleToSampledImageINTEL",
      "OpBitCastArrayQCOM",
      "OpExtractSubArrayQCOM"
    ).foreach { opcode =>
      assertEquals(MachineSemantics.machine(opcode).size, 1, opcode)
      assert(MachineSemantics.machine(opcode).forall(_.outcome.isRight), opcode)
    }
  }

  test("a handle conversion renders as the address the handle holds") {
    assertEquals(
      shown("OpConvertHandleToImageINTEL"),
      """def OpConvertHandleToImageINTEL(handle: UInt[64]): (result: UInt[64]) =
        |  (result = handle)""".stripMargin
    )
    assertEquals(
      shown("OpConvertHandleToSamplerINTEL"),
      """def OpConvertHandleToSamplerINTEL(handle: UInt[64]): (result: UInt[64]) =
        |  (result = handle)""".stripMargin
    )
  }

  test("a raw access chain renders as the base moved by strides and offset") {
    assertEquals(
      shown("OpRawAccessChainNV"),
      """def OpRawAccessChainNV(base: UInt[64], stride: UInt[64], element: UInt[64], offset: UInt[64]): (result: UInt[64]) =
        |  (result = base + (stride * element + offset))""".stripMargin
    )
  }

  test("a predicated load and store render under their predicate") {
    assertEquals(
      shown("OpPredicatedLoadINTEL"),
      """def OpPredicatedLoadINTEL(predicate: Bool, pointer: UInt[64], default: UInt[32])(using machine: Machine): (result: UInt[32]) =
        |  (result = if (predicate) machine.load[UInt[32]](Space.Generic, pointer) else default)""".stripMargin
    )
    assertEquals(
      shown("OpPredicatedStoreINTEL"),
      """def OpPredicatedStoreINTEL(predicate: Bool, value: UInt[32], pointer: UInt[64])(using machine: Machine): Unit = {
        |  if (predicate) machine.store[UInt[32]](Space.Generic, pointer, value) else ()
        |  ()
        |}""".stripMargin
    )
  }

  test("a sampled handle, a bit cast and a sub-array render as requests on the images, copies and leaves") {
    assertEquals(
      shown("OpConvertHandleToSampledImageINTEL"),
      """def OpConvertHandleToSampledImageINTEL(handle: UInt[64])(using images: Images): (result: UInt[64]) =
        |  (result = images.handledSampledImage(0, handle))""".stripMargin
    )
    assertEquals(
      shown("OpBitCastArrayQCOM"),
      """def OpBitCastArrayQCOM(source_0: UInt[32], source_1: UInt[32]): (result: UInt[64]) =
        |  (result = source_0.to[UInt[64]] | (source_1.to[UInt[64]] << 32))""".stripMargin
    )
    assertEquals(
      shown("OpExtractSubArrayQCOM"),
      """def OpExtractSubArrayQCOM(source_0: UInt[32], source_1: UInt[32], source_2: UInt[32], source_3: UInt[32], index: UInt[32])(using machine: Machine): (result_0: UInt[32], result_1: UInt[32]) = {
        |  machine.require(index < 3, "OpExtractSubArrayQCOM index")
        |  val v0 = index.to[UInt[64]] + 1
        |  (
        |    result_0 = if (index == UInt[32](0)) source_0 else if (index == UInt[32](1)) source_1 else if (index == UInt[32](2)) source_2 else source_3,
        |    result_1 = if (v0 == UInt[64](0)) source_0 else if (v0 == UInt[64](1)) source_1 else if (v0 == UInt[64](2)) source_2 else source_3
        |  )
        |}""".stripMargin
    )
  }

  test("a subgroup block read and write render each component at the element of the invocation's lane") {
    assertEquals(
      shown("OpSubgroupBlockReadINTEL"),
      """def OpSubgroupBlockReadINTEL(pointer: UInt[64])(using machine: Machine, groups: Groups): (result_0: UInt[32], result_1: UInt[32], result_2: UInt[32], result_3: UInt[32]) = {
        |  val v0 = groups.subgroupSize()
        |  val v1 = groups.subgroupLocal()
        |  (
        |    result_0 = machine.load[UInt[32]](Space.Generic, pointer + (UInt[64](0) * v0 + v1) * 4),
        |    result_1 = machine.load[UInt[32]](Space.Generic, pointer + (UInt[64](1) * v0 + v1) * 4),
        |    result_2 = machine.load[UInt[32]](Space.Generic, pointer + (UInt[64](2) * v0 + v1) * 4),
        |    result_3 = machine.load[UInt[32]](Space.Generic, pointer + (UInt[64](3) * v0 + v1) * 4)
        |  )
        |}""".stripMargin
    )
    assertEquals(
      shown("OpSubgroupBlockWriteINTEL"),
      """def OpSubgroupBlockWriteINTEL(data_0: UInt[32], data_1: UInt[32], data_2: UInt[32], data_3: UInt[32], pointer: UInt[64])(using machine: Machine, groups: Groups): Unit = {
        |  machine.store[UInt[32]](Space.Generic, pointer + (UInt[64](0) * groups.subgroupSize() + groups.subgroupLocal()) * 4, data_0)
        |  machine.store[UInt[32]](Space.Generic, pointer + (UInt[64](1) * groups.subgroupSize() + groups.subgroupLocal()) * 4, data_1)
        |  machine.store[UInt[32]](Space.Generic, pointer + (UInt[64](2) * groups.subgroupSize() + groups.subgroupLocal()) * 4, data_2)
        |  machine.store[UInt[32]](Space.Generic, pointer + (UInt[64](3) * groups.subgroupSize() + groups.subgroupLocal()) * 4, data_3)
        |  ()
        |}""".stripMargin
    )
  }

  test("a masked gather and scatter render per lane under their masks") {
    assertEquals(
      shown("OpMaskedGatherINTEL"),
      """def OpMaskedGatherINTEL(pointers_0: UInt[64], pointers_1: UInt[64], pointers_2: UInt[64], pointers_3: UInt[64], mask_0: Bool, mask_1: Bool, mask_2: Bool, mask_3: Bool, fill_0: UInt[32], fill_1: UInt[32], fill_2: UInt[32], fill_3: UInt[32])(using machine: Machine): (result_0: UInt[32], result_1: UInt[32], result_2: UInt[32], result_3: UInt[32]) =
        |  (
        |    result_0 = if (mask_0) machine.load[UInt[32]](Space.Generic, pointers_0) else fill_0,
        |    result_1 = if (mask_1) machine.load[UInt[32]](Space.Generic, pointers_1) else fill_1,
        |    result_2 = if (mask_2) machine.load[UInt[32]](Space.Generic, pointers_2) else fill_2,
        |    result_3 = if (mask_3) machine.load[UInt[32]](Space.Generic, pointers_3) else fill_3
        |  )""".stripMargin
    )
    assertEquals(
      shown("OpMaskedScatterINTEL"),
      """def OpMaskedScatterINTEL(pointers_0: UInt[64], pointers_1: UInt[64], pointers_2: UInt[64], pointers_3: UInt[64], mask_0: Bool, mask_1: Bool, mask_2: Bool, mask_3: Bool, input_0: UInt[32], input_1: UInt[32], input_2: UInt[32], input_3: UInt[32])(using machine: Machine): Unit = {
        |  if (mask_0) machine.store[UInt[32]](Space.Generic, pointers_0, input_0) else ()
        |  if (mask_1) machine.store[UInt[32]](Space.Generic, pointers_1, input_1) else ()
        |  if (mask_2) machine.store[UInt[32]](Space.Generic, pointers_2, input_2) else ()
        |  if (mask_3) machine.store[UInt[32]](Space.Generic, pointers_3, input_3) else ()
        |  ()
        |}""".stripMargin
    )
  }

  test("a subgroup image block read renders each component at the texel of the invocation's column") {
    assertEquals(
      shown("OpSubgroupImageBlockReadINTEL"),
      """def OpSubgroupImageBlockReadINTEL(coordinate_0: UInt[32], coordinate_1: UInt[32], image: UInt[64])(using images: Images, fragments: Fragments, groups: Groups): (result_0: UInt[32], result_1: UInt[32], result_2: UInt[32], result_3: UInt[32]) = {
        |  images.numeric(coordinate_0.to[UInt[64]], coordinate_1.to[UInt[64]])
        |  fragments.partials(11)
        |  images.level(image, UInt[64](0), 4)
        |  for (t0 <- fragments.indices(UInt[64](4))) {
        |    if ((coordinate_0.to[SInt[32]].to[UInt[64]] + groups.subgroupLocal() * 4).to[SInt[64]] >= 0 && (coordinate_0.to[SInt[32]].to[UInt[64]] + groups.subgroupLocal() * 4 + 4).to[SInt[64]] <= (fragments.partial(UInt[64](4)) * IntelBlockSemantics.texelBytes(fragments.partial(UInt[64](10)))).to[SInt[64]] && ((coordinate_1.to[SInt[32]].to[UInt[64]] + t0).to[SInt[64]] >= 0 && (coordinate_1.to[SInt[32]].to[UInt[64]] + t0).to[SInt[64]] < fragments.partial(UInt[64](5)).to[SInt[64]])) {
        |      val v0 = images.loadLevel((coordinate_1.to[SInt[32]].to[UInt[64]] + t0) * fragments.partial(UInt[64](9)) * IntelBlockSemantics.texelBytes(fragments.partial(UInt[64](10))) + (coordinate_0.to[SInt[32]].to[UInt[64]] + groups.subgroupLocal() * 4), UInt[64](4))
        |      fragments.setPartial(t0, v0)
        |    }
        |  }
        |  val v1 = fragments.partial(UInt[64](0))
        |  val v2 = fragments.partial(UInt[64](1))
        |  val v3 = fragments.partial(UInt[64](2))
        |  val v4 = fragments.partial(UInt[64](3))
        |  (
        |    result_0 = v1.to[UInt[32]],
        |    result_1 = v2.to[UInt[32]],
        |    result_2 = v3.to[UInt[32]],
        |    result_3 = v4.to[UInt[32]]
        |  )
        |}""".stripMargin
    )
  }

  test("a rank 1 tensor read renders as the descriptor loads and the bounded element reads") {
    assertEquals(
      shown("OpTensorReadARM"),
      """def OpTensorReadARM(tensor: UInt[64], coordinates: UInt[32])(using fragments: Fragments, tensors: TensorAddressing): (result: UInt[32]) = {
        |  fragments.partials(4)
        |  for (t0 <- fragments.indices(UInt[64](3))) {
        |    val v0 = fragments.loadElement(tensor, t0 * 8, UInt[64](8))
        |    fragments.setPartial(t0, v0)
        |  }
        |  for (t2 <- fragments.indices(UInt[64](1))) {
        |    fragments.setPartial(t2 + 3, UInt[64](0))
        |    if ((UInt[1](1) & (coordinates.to[UInt[64]] + t2 < fragments.partial(UInt[64](1))).to[UInt[1]]) != UInt[1](0)) {
        |      val v1 = tensors.loadAbsolute(fragments.partial(UInt[64](0)), UInt[64](0) + (coordinates.to[UInt[64]] + t2) * fragments.partial(UInt[64](2)), UInt[64](4), 1)
        |      fragments.setPartial(t2 + 3, v1)
        |    }
        |  }
        |  val v2 = fragments.partial(UInt[64](3))
        |  (result = v2.to[UInt[32]])
        |}""".stripMargin
    )
  }

  test("a rank 1 tensor write renders as the descriptor loads and the bounded element writes") {
    assertEquals(
      shown("OpTensorWriteARM"),
      """def OpTensorWriteARM(value: UInt[32], tensor: UInt[64], coordinates: UInt[32])(using fragments: Fragments, tensors: TensorAddressing): Unit = {
        |  fragments.partials(3)
        |  for (t0 <- fragments.indices(UInt[64](3))) {
        |    val v0 = fragments.loadElement(tensor, t0 * 8, UInt[64](8))
        |    fragments.setPartial(t0, v0)
        |  }
        |  for (t2 <- fragments.indices(UInt[64](1))) {
        |    if ((UInt[1](1) & (coordinates.to[UInt[64]] + t2 < fragments.partial(UInt[64](1))).to[UInt[1]]) != UInt[1](0)) {
        |      tensors.storeAbsolute(fragments.partial(UInt[64](0)), UInt[64](0) + (coordinates.to[UInt[64]] + t2) * fragments.partial(UInt[64](2)), UInt[64](4), value.to[UInt[64]], 3)
        |    }
        |  }
        |  ()
        |}""".stripMargin
    )
  }
}
