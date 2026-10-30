package net.kurobako.gemstone.isa.spirv

import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*

/** Declarations and barriers from extensions no toolchain at hand emits for ordinary sources, hand-assembled from their
  * specifications: SPV_KHR_constant_data, SPV_EXT_replicated_composites, SPV_INTEL_long_composites,
  * SPV_EXT_long_vector, OpTypeOpaque, SPV_INTEL_unstructured_loop_controls, named barriers and SPV_EXT_split_barrier.
  */
class ExtensionDeclarationSuite extends Suite {
  private def kernel(capabilities: String, declarations: String, body: String, count: Int): Vec[Long] = {
    val module = spv"""
      $capabilities
      $openCL
      OpEntryPoint Kernel %main "main"
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %uchar = OpTypeInt 8 0
      %ulong = OpTypeInt 64 0
      %uint_ptr = OpTypePointer CrossWorkgroup %uint
      %fn = OpTypeFunction %void %uint_ptr
      ${Vec.tabulate(count)(index => s"%ulong_$index = OpConstant %ulong $index").mkString("\n")}
      $declarations
      %main = OpFunction %void None %fn
      %out = OpFunctionParameter %uint_ptr
      %entry = OpLabel
      $body
      OpReturn
      OpFunctionEnd
    """
    val memory = RegionMemory()
    val out    = memory.allocate((count * 4L).unsafe[Long > 0])
    assertEquals(run(module, "main", memory, Vec(buffer(out))), Right(()))
    words(memory, out, 0, count)
  }

  private def store(index: Int, value: String): String =
    s"""%slot_$index = OpInBoundsPtrAccessChain %uint_ptr %out %ulong_$index
       |OpStore %slot_$index $value""".stripMargin

  test("OpConstantDataKHR packs narrow elements lowest first, and replicated composites repeat one constituent") {
    val values = kernel(
      """OpCapability ConstantDataKHR
        |OpCapability ReplicatedCompositesEXT
        |OpExtension "SPV_KHR_constant_data"
        |OpExtension "SPV_EXT_replicated_composites"""".stripMargin,
      """%uint_5 = OpConstant %uint 5
        |%uint_3 = OpConstant %uint 3
        |%text_type = OpTypeArray %uchar %uint_5
        |%text = OpConstantDataKHR %text_type 0x6c6c6548 0x0000006f
        |%v3uint = OpTypeVector %uint 3
        |%pair = OpTypeArray %v3uint %uint_3
        |%seven = OpConstant %uint 7
        |%sevens = OpConstantCompositeReplicateEXT %v3uint %seven
        |%rows = OpConstantCompositeReplicateEXT %pair %sevens
        |%spec = OpSpecConstantCompositeReplicateEXT %v3uint %uint_3""".stripMargin,
      Vec(
        "%first = OpCompositeExtract %uchar %text 0",
        "%first_word = OpUConvert %uint %first",
        store(0, "%first_word"),
        "%last = OpCompositeExtract %uchar %text 4",
        "%last_word = OpUConvert %uint %last",
        store(1, "%last_word"),
        "%row = OpCompositeExtract %uint %rows 2 1",
        store(2, "%row"),
        "%runtime = OpCompositeConstructReplicateEXT %pair %spec",
        "%element = OpCompositeExtract %uint %runtime 1 2",
        store(3, "%element")
      ).mkString("\n"),
      4
    )
    assertEquals(values, Vec(0x48L, 0x6fL, 7L, 3L))
  }

  test("SPV_INTEL_long_composites continuations extend the type, constant and construction they follow") {
    val values = kernel(
      """OpCapability LongCompositesINTEL
        |OpExtension "SPV_INTEL_long_composites"""".stripMargin,
      """%wide = OpTypeStruct %uint %uint
        |OpTypeStructContinuedINTEL %uint
        |OpTypeStructContinuedINTEL %uint
        |%one = OpConstant %uint 1
        |%two = OpConstant %uint 2
        |%three = OpConstant %uint 3
        |%four = OpConstant %uint 4
        |%value = OpConstantComposite %wide %one %two
        |OpConstantCompositeContinuedINTEL %three
        |OpConstantCompositeContinuedINTEL %four""".stripMargin,
      Vec(
        "%fourth = OpCompositeExtract %uint %value 3",
        store(0, "%fourth"),
        "%built = OpCompositeConstruct %wide %four %three",
        "OpCompositeConstructContinuedINTEL %two %one",
        "%built_fourth = OpCompositeExtract %uint %built 3",
        store(1, "%built_fourth")
      ).mkString("\n"),
      2
    )
    assertEquals(values, Vec(4L, 1L))
  }

  test("OpTypeVectorIdEXT sizes a vector by a constant, and OpTypeOpaque declares a pointee without members") {
    val values = kernel(
      """OpCapability LongVectorEXT
        |OpExtension "SPV_EXT_long_vector"""".stripMargin,
      """%uint_5 = OpConstant %uint 5
        |%v5uint = OpTypeVectorIdEXT %uint %uint_5
        |%opaque = OpTypeOpaque "struct.Foo"
        |%opaque_ptr = OpTypePointer CrossWorkgroup %opaque
        |%null = OpConstantNull %opaque_ptr
        |%bool = OpTypeBool
        |%nine = OpConstant %uint 9
        |%ten = OpConstant %uint 10""".stripMargin,
      Vec(
        "%vector = OpCompositeConstruct %v5uint %nine %nine %nine %nine %ten",
        "%doubled = OpIAdd %v5uint %vector %vector",
        "%fifth = OpCompositeExtract %uint %doubled 4",
        store(0, "%fifth"),
        "%cast = OpBitcast %opaque_ptr %out",
        "%null_equal = OpPtrEqual %bool %cast %null",
        "%flag = OpSelect %uint %null_equal %nine %ten",
        store(1, "%flag")
      ).mkString("\n"),
      2
    )
    assertEquals(values, Vec(20L, 10L))
  }

  test("OpFreezeKHR fixes poison at the zero the VM gives it, and OpBitcastExtractEXT reads a bit field") {
    val values = kernel(
      """OpCapability PoisonFreezeKHR
        |OpCapability BitcastExtractEXT
        |OpExtension "SPV_KHR_poison_freeze"
        |OpExtension "SPV_EXT_ocp_microscaling_types"""".stripMargin,
      """%poison = OpPoisonKHR %uint
        |%ushort = OpTypeInt 16 0
        |%v2uint = OpTypeVector %uint 2
        |%v2uchar = OpTypeVector %uchar 2
        |%word = OpConstant %uint 0x12345678
        |%other = OpConstant %uint 0xfedcba98
        |%words = OpConstantComposite %v2uint %word %other
        |%twelve = OpConstant %uint 12
        |%forty = OpConstant %uint 40""".stripMargin,
      Vec(
        "%frozen = OpFreezeKHR %uint %poison",
        store(0, "%frozen"),
        "%again = OpFreezeKHR %uint %word",
        store(1, "%again"),
        "%field = OpBitcastExtractEXT %ushort %word %twelve",
        "%field_word = OpUConvert %uint %field",
        store(2, "%field_word"),
        "%fields = OpBitcastExtractEXT %v2uchar %words %twelve",
        "%second = OpCompositeExtract %uchar %fields 1",
        "%second_word = OpUConvert %uint %second",
        store(3, "%second_word"),
        "%outside = OpBitcastExtractEXT %ushort %word %forty",
        "%outside_word = OpUConvert %uint %outside",
        store(4, "%outside_word")
      ).mkString("\n"),
      5
    )
    assertEquals(values, Vec(0L, 0x12345678L, 0x2345L, 0xcbL, 0L))
  }

  test("OpAbortKHR ends the launch with its message") {
    val module = spv"""
      OpCapability AbortKHR
      OpExtension "SPV_KHR_abort"
      $openCL
      OpEntryPoint Kernel %main "main"
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %fn = OpTypeFunction %void
      %code = OpConstant %uint 0xdead
      %main = OpFunction %void None %fn
      %entry = OpLabel
      OpAbortKHR %uint %code
      OpFunctionEnd
    """
    val result = run(module, "main", RegionMemory())
    assert(
      result.left.exists(_.message.raw.contains("OpAbortKHR terminated the invocation with message [dead]")),
      result
    )
  }

  test("OpLoopControlINTEL hints its loop without changing it") {
    val values = kernel(
      """OpCapability UnstructuredLoopControlsINTEL
        |OpExtension "SPV_INTEL_unstructured_loop_controls"""".stripMargin,
      """%bool = OpTypeBool
        |%zero = OpConstant %uint 0
        |%one = OpConstant %uint 1
        |%four = OpConstant %uint 4""".stripMargin,
      Vec(
        "OpBranch %header",
        "%header = OpLabel",
        "%i = OpPhi %uint %zero %entry %next %header",
        "%next = OpIAdd %uint %i %one",
        "%more = OpULessThan %bool %next %four",
        "OpLoopControlINTEL 4 2",
        "OpBranchConditional %more %header %exit",
        "%exit = OpLabel",
        store(0, "%next")
      ).mkString("\n"),
      1
    )
    assertEquals(values, Vec(4L))
  }

  /** Each of eight invocations in two four-lane subgroups writes its id to workgroup memory, synchronises through
    * `barrier`, then reads its neighbour's.
    */
  private def exchange(capabilities: String, declarations: String, barrier: String): Vec[Long] = {
    val module = spv"""
      $capabilities
      $openCL
      OpEntryPoint Kernel %main "main" %lid
      OpExecutionMode %main SubgroupSize 4
      OpDecorate %lid BuiltIn LocalInvocationIndex
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %ulong = OpTypeInt 64 0
      %uint_8 = OpConstant %uint 8
      %one = OpConstant %uint 1
      %seven = OpConstant %uint 7
      %workgroup = OpConstant %uint 2
      %subgroups = OpConstant %uint 2
      %acquire_release = OpConstant %uint 0x108
      %release = OpConstant %uint 0x104
      %acquire = OpConstant %uint 0x102
      %shared_type = OpTypeArray %uint %uint_8
      %shared_ptr = OpTypePointer Workgroup %shared_type
      %local_uint_ptr = OpTypePointer Workgroup %uint
      %uint_ptr = OpTypePointer CrossWorkgroup %uint
      %lid_ptr = OpTypePointer Input %uint
      %lid = OpVariable %lid_ptr Input
      %shared = OpVariable %shared_ptr Workgroup
      $declarations
      %fn = OpTypeFunction %void %uint_ptr
      %main = OpFunction %void None %fn
      %out = OpFunctionParameter %uint_ptr
      %entry = OpLabel
      %index = OpLoad %uint %lid
      %mine = OpInBoundsAccessChain %local_uint_ptr %shared %index
      OpStore %mine %index
      $barrier
      %next = OpIAdd %uint %index %one
      %wrapped = OpBitwiseAnd %uint %next %seven
      %theirs = OpInBoundsAccessChain %local_uint_ptr %shared %wrapped
      %read = OpLoad %uint %theirs
      %slot = OpInBoundsPtrAccessChain %uint_ptr %out %index
      OpStore %slot %read
      OpReturn
      OpFunctionEnd
    """
    val memory = RegionMemory()
    val out    = memory.allocate(32L)
    assertEquals(run(module, "main", memory, Vec(buffer(out)), local = testDimensions(8)), Right(()))
    words(memory, out, offset = 0, count = 8)
  }

  test("a named barrier over every subgroup of the workgroup synchronises it") {
    assertEquals(
      exchange(
        "OpCapability NamedBarrier",
        "%named_type = OpTypeNamedBarrier",
        """%named = OpNamedBarrierInitialize %named_type %subgroups
          |OpMemoryNamedBarrier %named %workgroup %acquire_release""".stripMargin
      ),
      Vec.tabulate(8)(index => ((index + 1) % 8).toLong)
    )
  }

  test("a split barrier's wait synchronises the invocations that arrived") {
    assertEquals(
      exchange(
        """OpCapability SplitBarrierEXT
          |OpExtension "SPV_EXT_split_barrier"""".stripMargin,
        "",
        """OpControlBarrierArriveEXT %workgroup %workgroup %release
          |OpControlBarrierWaitEXT %workgroup %workgroup %acquire""".stripMargin
      ),
      Vec.tabulate(8)(index => ((index + 1) % 8).toLong)
    )
  }

  test("SPV_INTEL_function_variants selects the variants whose conditions hold for a device in no registry") {
    // the conditions forward-reference specialisation constants, as multi-target modules may
    val module = spv"""
      OpCapability SpecConditionalINTEL
      OpCapability FunctionVariantsINTEL
      OpConditionalCapabilityINTEL %matches_target Int64Atomics
      OpExtension "SPV_INTEL_function_variants"
      OpConditionalExtensionINTEL %runs_kernels "SPV_KHR_expect_assume"
      $openCL
      OpConditionalEntryPointINTEL %matches_target Kernel %targeted "main"
      OpConditionalEntryPointINTEL %runs_kernels Kernel %fallback "main"
      %void = OpTypeVoid
      %bool = OpTypeBool
      %uint = OpTypeInt 32 0
      %uint_ptr = OpTypePointer CrossWorkgroup %uint
      %fn = OpTypeFunction %void %uint_ptr
      %one = OpConstant %uint 1
      %two = OpConstant %uint 2
      %matches_target = OpSpecConstantTargetINTEL %bool 7
      %runs_kernels = OpSpecConstantCapabilitiesINTEL %bool Kernel Addresses
      %traces_rays = OpSpecConstantCapabilitiesINTEL %bool Kernel RayTracingKHR
      %targeted = OpFunction %void None %fn
      %targeted_out = OpFunctionParameter %uint_ptr
      %targeted_entry = OpLabel
      OpStore %targeted_out %one
      OpReturn
      OpFunctionEnd
      %fallback = OpFunction %void None %fn
      %out = OpFunctionParameter %uint_ptr
      %entry = OpLabel
      %chosen = OpConditionalCopyObjectINTEL %uint %matches_target %one %traces_rays %one %runs_kernels %two
      OpStore %out %chosen
      OpReturn
      OpFunctionEnd
    """
    val memory = RegionMemory()
    val out    = memory.allocate(4L)
    assertEquals(run(module, "main", memory, Vec(buffer(out))), Right(()))
    assertEquals(words(memory, out, offset = 0, count = 1), Vec(2L))
  }

  test("a conditional copy selects the operand of the first condition that holds at run time") {
    val module = spv"""
      OpCapability SpecConditionalINTEL
      OpCapability FunctionVariantsINTEL
      OpExtension "SPV_INTEL_function_variants"
      $openCL
      OpEntryPoint Kernel %kernel "main"
      %void = OpTypeVoid
      %bool = OpTypeBool
      %uint = OpTypeInt 32 0
      %uint_ptr = OpTypePointer CrossWorkgroup %uint
      %fn = OpTypeFunction %void %uint_ptr
      %zero = OpConstant %uint 0
      %one = OpConstant %uint 1
      %two = OpConstant %uint 2
      %kernel = OpFunction %void None %fn
      %out = OpFunctionParameter %uint_ptr
      %entry = OpLabel
      OpStore %out %one
      %value = OpLoad %uint %out
      %is_zero = OpIEqual %bool %value %zero
      %is_one = OpIEqual %bool %value %one
      %chosen = OpConditionalCopyObjectINTEL %uint %is_zero %zero %is_one %two
      OpStore %out %chosen
      OpReturn
      OpFunctionEnd
    """
    val memory = RegionMemory()
    val out    = memory.allocate(4L)
    assertEquals(run(module, "main", memory, Vec(buffer(out))), Right(()))
    assertEquals(words(memory, out, offset = 0, count = 1), Vec(2L))
  }

  test("a conditional copy faults when no condition holds at run time") {
    val module = spv"""
      OpCapability SpecConditionalINTEL
      OpCapability FunctionVariantsINTEL
      OpExtension "SPV_INTEL_function_variants"
      $openCL
      OpEntryPoint Kernel %kernel "main"
      %void = OpTypeVoid
      %bool = OpTypeBool
      %uint = OpTypeInt 32 0
      %uint_ptr = OpTypePointer CrossWorkgroup %uint
      %fn = OpTypeFunction %void %uint_ptr
      %zero = OpConstant %uint 0
      %one = OpConstant %uint 1
      %two = OpConstant %uint 2
      %kernel = OpFunction %void None %fn
      %out = OpFunctionParameter %uint_ptr
      %entry = OpLabel
      OpStore %out %one
      %value = OpLoad %uint %out
      %is_zero = OpIEqual %bool %value %zero
      %is_two = OpIEqual %bool %value %two
      %chosen = OpConditionalCopyObjectINTEL %uint %is_zero %zero %is_two %two
      OpStore %out %chosen
      OpReturn
      OpFunctionEnd
    """
    val memory = RegionMemory()
    val out    = memory.allocate(4L)
    assert(run(module, "main", memory, Vec(buffer(out))).isLeft)
  }

  test("pipe storage holds a program-scope pipe that persists across the launches of its program") {
    val module = spv"""
      OpCapability Pipes
      OpCapability PipeStorage
      OpCapability GenericPointer
      $openCL
      OpEntryPoint Kernel %produce "produce"
      OpEntryPoint Kernel %consume "consume"
      OpName %storage "storage"
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %storage_type = OpTypePipeStorage
      %pipe_type = OpTypePipe ReadWrite
      %uint_ptr = OpTypePointer CrossWorkgroup %uint
      %private_ptr = OpTypePointer Function %uint
      %generic_ptr = OpTypePointer Generic %uint
      %fn = OpTypeFunction %void %uint_ptr
      %uint_4 = OpConstant %uint 4
      %uint_41 = OpConstant %uint 41
      %uint_42 = OpConstant %uint 42
      %storage = OpConstantPipeStorage %storage_type 4 4 3
      %produce = OpFunction %void None %fn
      %status = OpFunctionParameter %uint_ptr
      %produce_entry = OpLabel
      %packet = OpVariable %private_ptr Function
      %packet_generic = OpPtrCastToGeneric %generic_ptr %packet
      %pipe = OpCreatePipeFromPipeStorage %pipe_type %storage
      OpStore %packet %uint_41
      %first = OpWritePipe %uint %pipe %packet_generic %uint_4 %uint_4
      OpStore %packet %uint_42
      %second = OpWritePipe %uint %pipe %packet_generic %uint_4 %uint_4
      %count = OpGetNumPipePackets %uint %pipe %uint_4 %uint_4
      OpStore %status %count
      OpReturn
      OpFunctionEnd
      %consume = OpFunction %void None %fn
      %out = OpFunctionParameter %uint_ptr
      %consume_entry = OpLabel
      %received = OpVariable %private_ptr Function
      %received_generic = OpPtrCastToGeneric %generic_ptr %received
      %source = OpCreatePipeFromPipeStorage %pipe_type %storage
      %read = OpReadPipe %uint %source %received_generic %uint_4 %uint_4
      %value = OpLoad %uint %received
      OpStore %out %value
      OpReturn
      OpFunctionEnd
    """
    val memory  = RegionMemory()
    val status  = memory.allocate(4L)
    val out     = memory.allocate(4L)
    val storage = memory.allocate(28L)
    // the driver binds each program-scope global to an allocation of its own, which keeps it across launches
    val result = load(module).flatMap(program =>
      program.bindGlobal("storage", GlobalBinding.validated("storage", storage, 0L, 28L, 28L).value, memory)
        .flatMap(_ => execute(program, task("produce", Vec(buffer(status))), memory))
        .flatMap(_ => execute(program, task("consume", Vec(buffer(out))), memory))
    )
    assertEquals(result, Right(()))
    assertEquals(words(memory, status, offset = 0, count = 1), Vec(2L))
    assertEquals(words(memory, out, offset = 0, count = 1), Vec(41L))
  }
}
