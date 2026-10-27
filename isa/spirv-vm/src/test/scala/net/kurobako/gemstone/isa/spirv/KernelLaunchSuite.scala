package net.kurobako.gemstone.isa.spirv

import fixtures.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
import scala.collection.mutable

class KernelLaunchSuite extends munit.FunSuite {
  private val stored = 0x12345678L

  private val Accumulate = """%previous = OpLoad %uint %slot
                              |%sum = OpIAdd %uint %previous %value
                              |OpStore %slot %sum""".stripMargin

  /** Every work-item applies `update` to `%value` at `%out[builtIn.x]`: a store, or with [[Accumulate]] an add. */
  private def fill(
      builtIn: String = "GlobalInvocationId",
      lanes: Int = 3,
      update: String = "OpStore %slot %value",
      extra: String = ""
  ): Module = spv"""
    $openCL
    OpEntryPoint Kernel %main "fill" %builtin
    OpDecorate %builtin BuiltIn $builtIn
    %void = OpTypeVoid
    %uint = OpTypeInt 32 0
    %ulong = OpTypeInt 64 0
    %id_vector = OpTypeVector %ulong $lanes
    %id_vector_ptr = OpTypePointer Input %id_vector
    %uint_ptr = OpTypePointer CrossWorkgroup %uint
    %fn = OpTypeFunction %void %uint_ptr %uint
    %device = OpConstant %uint ${Scope.Device}
    %relaxed = OpConstant %uint ${MemorySemantics.Relaxed}
    %builtin = OpVariable %id_vector_ptr Input
    %main = OpFunction %void None %fn
    %out = OpFunctionParameter %uint_ptr
    %value = OpFunctionParameter %uint
    %entry = OpLabel
    %ids = OpLoad %id_vector %builtin
    %index = OpCompositeExtract %ulong %ids 0
    %slot = OpPtrAccessChain %uint_ptr %out %index
    $update
    OpReturn
    OpFunctionEnd
    $extra
  """

  private def launch(region: AllocationId)(using munit.Location): KernelTask =
    task("fill", Vec(buffer(region, 4L), scalar(stored, 4)), local = testDimensions(4), groups = testDimensions(2))

  /** 40 bytes: one guard word, eight work-items from byte 4, one guard word. */
  private def filled(values: Seq[Long]): Vec[Long] = Vec(0L) ++ Vec.from(values) ++ Vec(0L)

  test("GlobalInvocationId indexes the typed buffer and scalar arguments") {
    val memory = RegionMemory()
    val region = memory.allocate(40L)
    assertEquals(execute(load(fill()).value, launch(region), memory), Right(()))
    assertEquals(words(memory, region, offset = 0, count = 10), filled(Seq.fill(8)(stored)))
  }

  test("the instruction budget stops a launch that exceeds it") {
    val memory = RegionMemory()
    val region = memory.allocate(40L)
    val fault  = execute(
      load(fill()).value,
      launch(region),
      memory,
      TaskControl(TaskControl.Limits(maxInstructions = 1L))
    ).leftValue
    assert(fault.message.contains("instruction budget of 1"), fault.message)
  }

  test("an atomic in a function the entry point never calls keeps workgroups parallel") {
    val unrelated = """%unrelated = OpFunction %void None %fn
                      |%unrelated_out = OpFunctionParameter %uint_ptr
                      |%unrelated_value = OpFunctionParameter %uint
                      |%unrelated_entry = OpLabel
                      |OpAtomicStore %unrelated_out %device %relaxed %unrelated_value
                      |OpReturn
                      |OpFunctionEnd""".stripMargin
    val workgroups = RecordingWorkgroups()
    val memory     = RegionMemory()
    val region     = memory.allocate(40L)
    assertEquals(execute(load(fill(extra = unrelated), workgroups).value, launch(region), memory), Right(()))
    assertEquals(words(memory, region, offset = 0, count = 10), filled(Seq.fill(8)(stored)))
    assertEquals(workgroups.calls.get(), 1)
  }

  test("suspended host reads keep each lane's IDs, arguments and values until the reads flush") {
    for (schedule <- Seq[Workgroups](Workgroups.Sequential, RecordingWorkgroups())) {
      val backing = RegionMemory()
      val region  = backing.allocate(40L)
      writeWords(backing, region, 4, (0L until 8L)*)
      // even words are available immediately, odd ones suspend until the host flushes reads
      class MixedMemory extends ForwardingMemory(backing) {
        private val pending                                = mutable.Set.empty[Long]
        private val ready                                  = mutable.Set.empty[Long]
        override def defersHostReads: Boolean              = true
        override def flushHostReads(): Either[Fault, Unit] = {
          val _ = ready.addAll(pending)
          pending.clear()
          Right(())
        }
        override def read(region: AllocationId, offset: Long >= 0, size: Int >= 0): Either[Fault, Vec[Byte]] =
          awaitRead(readDeferred(region, offset, size))
        override def readDeferred(
            region: AllocationId,
            offset: Long >= 0,
            size: Int >= 0
        ): AddressSpace.ReadResult[Vec[Byte]] =
          if (offset.raw % 8L == 0L || ready.contains(offset)) backing.read(region, offset, size)
          else {
            val _ = pending.add(offset)
            Left(AddressSpace.Pending)
          }
        override def readValueDeferred(
            region: AllocationId,
            offset: Long >= 0,
            size: Int in 1 ~ 8
        ): AddressSpace.ReadResult[Long] =
          readDeferred(region, offset, size.widen[Int >= 0]).map(bytes =>
            LittleEndian.decode(bytes, 0, size.widen[Int in 0 ~ 8])
          )
      }
      val program = load(fill(update = Accumulate), schedule).value
      assertEquals(execute(program, launch(region), MixedMemory()), Right(()))
      assertEquals(words(backing, region, offset = 0, count = 10), filled((0L until 8L).map(stored + _)))
    }
  }

  test("WorkgroupSize reads the launch local size") {
    val memory = RegionMemory()
    val region = memory.allocate(40L)
    assertEquals(execute(load(fill("WorkgroupSize")).value, launch(region), memory), Right(()))
    assertEquals(
      words(memory, region, offset = 0, count = 10),
      filled(Seq.tabulate(8)(index => if (index == 4) stored else 0L))
    )
  }

  test("legacy BuiltIn 35 decodes from a binary and reads the linear workgroup id") {
    val module = spv"""
      $openCL
      OpEntryPoint Kernel %main "linear" %global_linear %group_linear
      OpDecorate %global_linear BuiltIn GlobalLinearId
      OpDecorate %group_linear BuiltIn WorkgroupLinearId
      %void = OpTypeVoid
      %ulong = OpTypeInt 64 0
      %ulong_input = OpTypePointer Input %ulong
      %ulong_ptr = OpTypePointer CrossWorkgroup %ulong
      %fn = OpTypeFunction %void %ulong_ptr
      %global_linear = OpVariable %ulong_input Input
      %group_linear = OpVariable %ulong_input Input
      %main = OpFunction %void None %fn
      %out = OpFunctionParameter %ulong_ptr
      %entry = OpLabel
      %index = OpLoad %ulong %global_linear
      %group = OpLoad %ulong %group_linear
      %slot = OpPtrAccessChain %ulong_ptr %out %index
      OpStore %slot %group
      OpReturn
      OpFunctionEnd
    """
    val binary  = ISA.encode(module).fold(error => fail(error.message), identity)
    val decoded = ISA.decode(binary).fold(error => fail(error.message), identity)
    assert(decoded.instructions.contains(
      Instruction.Decorate(
        decoded.instructions.collect { case Instruction.Decorate(target, _) => target }.last,
        operand(OperandKind.Decoration, Decoration.BuiltIn, 35)
      )
    ))
    val memory = RegionMemory()
    val out    = memory.allocate(8L * 24)
    val launch = task("linear", Vec(buffer(out)), local = testDimensions(2), groups = testDimensions(2, 3, 2))
    assertEquals(execute(load(decoded).value, launch, memory), Right(()))
    val expected = Vec.tabulate(24)(index => index / 2 % 2 + 2 * (index / 4 % 3 + 3 * (index / 12)))
    assertEquals(
      Vec.tabulate(24)(index => LittleEndian.decode(memory.bytes(out), index * 8, 8)),
      expected.map(_.toLong)
    )
  }

  test("a BuiltIn vector with the wrong lane count is rejected at launch") {
    val module = fill(lanes = 2)
    val vector = module.instructions.collectFirst { case Instruction.TypeVector(id, _, _) => id.words.head.raw }
      .getOrElse(fail("missing OpTypeVector"))
    val memory = RegionMemory()
    val region = memory.allocate(40L)
    assertEquals(
      execute(loadUnchecked(module).value, launch(region), memory),
      Left(Fault.InvalidRequest(
        s"SPIR-V BuiltIn ${BuiltIn.GlobalInvocationId} requires 3 integer lanes, result type %$vector has 2"
      ))
    )
  }

  test("an explicit subgroup size materialises subgroup built-ins") {
    def module(builtIn: String, explicitSize: Boolean, undefined: Boolean): Module = spv"""
      $openCL
      OpEntryPoint Kernel %main "subgroups" %global_id %subgroup
      ${if (explicitSize) "OpExecutionMode %main SubgroupSize 32" else ""}
      OpDecorate %global_id BuiltIn GlobalInvocationId
      OpDecorate %subgroup BuiltIn $builtIn
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %ulong = OpTypeInt 64 0
      %ulong3 = OpTypeVector %ulong 3
      %ulong3_ptr = OpTypePointer Input %ulong3
      %uint_input_ptr = OpTypePointer Input %uint
      %uint_ptr = OpTypePointer CrossWorkgroup %uint
      %fn = OpTypeFunction %void %uint_ptr
      %global_id = OpVariable %ulong3_ptr Input
      %subgroup = OpVariable %uint_input_ptr Input
      %undefined = OpUndef %uint
      %main = OpFunction %void None %fn
      %out = OpFunctionParameter %uint_ptr
      %entry = OpLabel
      %ids = OpLoad %ulong3 %global_id
      %index = OpCompositeExtract %ulong %ids 0
      %value = OpLoad %uint %subgroup
      %slot = OpPtrAccessChain %uint_ptr %out %index
      OpStore %slot ${if (undefined) "%undefined" else "%value"}
      OpReturn
      OpFunctionEnd
    """
    val halves = Vec.fill(32)(0L) ++ Vec.fill(32)(1L)
    for (
      (builtIn, explicitSize, undefined, expected) <- Seq(
        ("SubgroupId", true, false, halves),
        ("SubgroupId", false, false, halves),
        ("NumSubgroups", true, false, Vec.fill(64)(2L)),
        ("NumEnqueuedSubgroups", true, false, Vec.fill(64)(2L)),
        ("SubgroupId", true, true, Vec.fill(64)(0L))
      )
    ) {
      val memory = RegionMemory()
      val out    = memory.allocate(256L)
      assertEquals(
        run(module(builtIn, explicitSize, undefined), "subgroups", memory, Vec(buffer(out)), testDimensions(64)),
        Right(())
      )
      assertEquals(
        words(memory, out, offset = 0, count = 64),
        expected,
        s"$builtIn explicit=$explicitSize undefined=$undefined"
      )
    }
  }
}
