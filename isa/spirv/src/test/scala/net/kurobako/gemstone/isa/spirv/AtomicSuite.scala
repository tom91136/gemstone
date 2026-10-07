package net.kurobako.gemstone.isa.spirv

import fixtures.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*

class AtomicSuite extends munit.FunSuite {
  private val semantics =
    MemorySemantics.SubgroupMemory | MemorySemantics.WorkgroupMemory | MemorySemantics.CrossWorkgroupMemory

  /** Kernel over one `CrossWorkgroup` pointer `%target` to `element`, with `%scope`, `%semantics` and indices. */
  private def kernel(element: String, constants: String, body: String): Module = spv"""
    $openCL
    OpEntryPoint Kernel %main "atomic"
    %void = OpTypeVoid
    %bool = OpTypeBool
    %uint = OpTypeInt 32 0
    %ulong = OpTypeInt 64 0
    %float = OpTypeFloat 32
    %ptr = OpTypePointer CrossWorkgroup $element
    %fn = OpTypeFunction %void %ptr
    %scope = OpConstant %uint ${Scope.Workgroup}
    %semantics = OpConstant %uint $semantics
    %zero = OpConstant %uint 0
    %one = OpConstant %uint 1
    %two = OpConstant %uint 2
    %three = OpConstant %uint 3
    $constants
    %main = OpFunction %void None %fn
    %target = OpFunctionParameter %ptr
    %entry = OpLabel
    $body
    OpReturn
    OpFunctionEnd
  """

  private def store(index: String, value: String): String =
    s"""%${value}_out = OpPtrAccessChain %ptr %target %$index
       |OpStore %${value}_out %$value""".stripMargin

  private def runKernel(module: Module, initial: Vec[Byte], size: Long > 0)(using munit.Location): Vec[Byte] = {
    val memory = RegionMemory()
    val target = memory.allocate(size)
    val _      = initial.copyToArray(memory.array(target))
    assertEquals(run(module, "atomic", memory, Vec(buffer(target))), Right(()))
    memory.bytes(target)
  }

  for {
    (storage, block) <- Seq("Uniform" -> "BufferBlock", "StorageBuffer" -> "Block")
    width            <- Seq(32, 64)
  } test(s"$width-bit descriptor atomics preserve prior values in $storage storage") {
    val slots  = (0 until 5).map(index => s"%slot$index = OpAccessChain %element_ptr %buffer %index0 %index$index")
    val module = spv"""
      $vulkan
      OpEntryPoint GLCompute %main "descriptor_atomics" %buffer
      OpExecutionMode %main LocalSize 1 1 1
      OpDecorate %array ArrayStride ${width / 8}
      OpDecorate %block $block
      OpMemberDecorate %block 0 Offset 0
      OpDecorate %buffer DescriptorSet 0
      OpDecorate %buffer Binding 0
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %element = OpTypeInt $width 0
      %array = OpTypeRuntimeArray %element
      %block = OpTypeStruct %array
      %block_ptr = OpTypePointer $storage %block
      %element_ptr = OpTypePointer $storage %element
      %fn = OpTypeFunction %void
      %device = OpConstant %uint ${Scope.Device}
      %relaxed = OpConstant %uint ${MemorySemantics.Relaxed}
      ${(0 until 5).map(index => s"%index$index = OpConstant %uint $index").mkString("\n")}
      %five = OpConstant %element 5
      %seven = OpConstant %element 7
      %buffer = OpVariable %block_ptr $storage
      %main = OpFunction %void None %fn
      %entry = OpLabel
      ${slots.mkString("\n")}
      OpAtomicStore %slot0 %device %relaxed %seven
      %loaded = OpAtomicLoad %element %slot0 %device %relaxed
      %exchanged = OpAtomicExchange %element %slot0 %device %relaxed %five
      %matched = OpAtomicCompareExchange %element %slot0 %device %relaxed %relaxed %seven %five
      %unmatched = OpAtomicCompareExchange %element %slot0 %device %relaxed %relaxed %five %five
      OpStore %slot1 %loaded
      OpStore %slot2 %exchanged
      OpStore %slot3 %matched
      OpStore %slot4 %unmatched
      OpReturn
      OpFunctionEnd
    """
    val bytes: Int in 0 ~ 8 = if (width == 32) 4 else 8
    val memory              = RegionMemory()
    val descriptor          = memory.allocate((5 * width / 8).toLong.refine[Long > 0].value)
    assertEquals(run(module, "descriptor_atomics", memory, Vec(buffer(descriptor))), Right(()))
    assertEquals(memory.bytes(descriptor), Vec(7L, 7L, 7L, 5L, 7L).flatMap(LittleEndian.toVec(_, bytes)))
  }

  test("atomic flag test-and-set returns the prior state and clear resets the flag") {
    val module = kernel(
      "%uint",
      "",
      s"""%first = OpAtomicFlagTestAndSet %bool %target %scope %semantics
         |%second = OpAtomicFlagTestAndSet %bool %target %scope %semantics
         |OpAtomicFlagClear %target %scope %semantics
         |%third = OpAtomicFlagTestAndSet %bool %target %scope %semantics
         |%first_set = OpSelect %uint %first %one %zero
         |%second_set = OpSelect %uint %second %one %zero
         |%third_set = OpSelect %uint %third %one %zero
         |${store("one", "first_set")}
         |${store("two", "second_set")}
         |${store("three", "third_set")}""".stripMargin
    )
    assertEquals(runKernel(module, Vec.empty, 16L), Vec(1L, 0L, 1L, 0L).flatMap(LittleEndian.toVec(_, 4)))
  }

  test("typed 64-bit atomic add and unsigned minimum return prior values") {
    val module = kernel(
      "%ulong",
      """%addend = OpConstant %ulong 3
        |%minimum = OpConstant %ulong 7""".stripMargin,
      s"""OpAtomicStore %target %scope %semantics %minimum
         |%added = OpAtomicIAdd %ulong %target %scope %semantics %addend
         |%minimised = OpAtomicUMin %ulong %target %scope %semantics %minimum
         |${store("one", "added")}
         |${store("two", "minimised")}""".stripMargin
    )
    assertEquals(
      runKernel(module, LittleEndian.toVec(10L, 8), 24L),
      Vec(7L, 7L, 10L).flatMap(LittleEndian.toVec(_, 8))
    )
  }

  test("integer atomic read-modify-write operations return the prior value") {
    for (
      (operation, initial, source, updated) <- Seq(
        ("OpAtomicIIncrement", 10L, None, 11L),
        ("OpAtomicIDecrement", 10L, None, 9L),
        ("OpAtomicISub", 10L, Some(3L), 7L),
        ("OpAtomicSMin", 1L, Some(0xffffffffL), 0xffffffffL),
        ("OpAtomicSMax", 0xffffffffL, Some(1L), 1L),
        ("OpAtomicUMax", 10L, Some(3L), 10L),
        ("OpAtomicAnd", 10L, Some(3L), 2L),
        ("OpAtomicOr", 10L, Some(3L), 11L),
        ("OpAtomicXor", 10L, Some(3L), 9L)
      )
    ) {
      val module = kernel(
        "%uint",
        source.fold("")(value => s"%source = OpConstant %uint $value"),
        s"""%old = $operation %uint %target %scope %semantics${source.fold("")(_ => " %source")}
           |${store("one", "old")}""".stripMargin
      )
      assertEquals(
        runKernel(module, LittleEndian.toVec(initial, 4), 8L),
        LittleEndian.toVec(updated, 4) ++ LittleEndian.toVec(initial, 4),
        operation
      )
    }
  }

  test("floating atomic load, store and exchange treat values as typed raw bits") {
    val module = kernel(
      "%float",
      """%first = OpConstant %float 1.5
        |%second = OpConstant %float 2.5""".stripMargin,
      s"""OpAtomicStore %target %scope %semantics %first
         |%loaded = OpAtomicLoad %float %target %scope %semantics
         |%exchanged = OpAtomicExchange %float %target %scope %semantics %second
         |${store("one", "loaded")}
         |${store("two", "exchanged")}""".stripMargin
    )
    assertEquals(
      runKernel(module, Vec.empty, 12L),
      Vec(floatBits(2.5f), floatBits(1.5f), floatBits(1.5f)).flatMap(LittleEndian.toVec(_, 4))
    )
  }

  test("floating atomic minimum and maximum return prior values") {
    val module = kernel(
      "%float",
      """%initial = OpConstant %float 3.0
        |%minimum = OpConstant %float 1.0
        |%maximum = OpConstant %float 5.0""".stripMargin,
      s"""OpAtomicStore %target %scope %semantics %initial
         |%old_minimum = OpAtomicFMinEXT %float %target %scope %semantics %minimum
         |%old_maximum = OpAtomicFMaxEXT %float %target %scope %semantics %maximum
         |${store("one", "old_minimum")}
         |${store("two", "old_maximum")}""".stripMargin
    )
    assertEquals(
      runKernel(module, Vec.empty, 12L),
      Vec(floatBits(5f), floatBits(3f), floatBits(1f)).flatMap(LittleEndian.toVec(_, 4))
    )
  }

  /** The first arriving workgroup publishes `seed` behind a release flag; later ones copy it if the flag is set. */
  private val publication = spv"""
    $openCL
    OpEntryPoint Kernel %main "publish" %workgroup_id
    OpDecorate %workgroup_id BuiltIn WorkgroupId
    %void = OpTypeVoid
    %bool = OpTypeBool
    %uint = OpTypeInt 32 0
    %ulong = OpTypeInt 64 0
    %ulong3 = OpTypeVector %ulong 3
    %ulong3_ptr = OpTypePointer Input %ulong3
    %uint_ptr = OpTypePointer CrossWorkgroup %uint
    %fn = OpTypeFunction %void %uint_ptr %uint_ptr %uint_ptr %uint_ptr %uint
    %zero = OpConstant %uint 0
    %one = OpConstant %uint 1
    %two = OpConstant %uint 2
    %device = OpConstant %uint ${Scope.Device}
    %release = OpConstant %uint ${MemorySemantics.CrossWorkgroupMemory | MemorySemantics.Release}
    %acquire = OpConstant %uint ${MemorySemantics.CrossWorkgroupMemory | MemorySemantics.Acquire}
    %acquire_release = OpConstant %uint ${MemorySemantics.CrossWorkgroupMemory | MemorySemantics.AcquireRelease}
    %workgroup_id = OpVariable %ulong3_ptr Input
    %main = OpFunction %void None %fn
    %records = OpFunctionParameter %uint_ptr
    %counter = OpFunctionParameter %uint_ptr
    %payload = OpFunctionParameter %uint_ptr
    %flag = OpFunctionParameter %uint_ptr
    %seed = OpFunctionParameter %uint
    %entry = OpLabel
    %ids = OpLoad %ulong3 %workgroup_id
    %id = OpCompositeExtract %ulong %ids 0
    %group = OpUConvert %uint %id
    %record = OpIMul %uint %group %two
    %ticket_out = OpPtrAccessChain %uint_ptr %records %record
    %observed_index = OpIAdd %uint %record %one
    %observed_out = OpPtrAccessChain %uint_ptr %records %observed_index
    %ticket = OpAtomicIAdd %uint %counter %device %acquire_release %one
    OpStore %ticket_out %ticket
    %first = OpIEqual %bool %ticket %zero
    OpSelectionMerge %done None
    OpBranchConditional %first %produce %consume
    %produce = OpLabel
    OpStore %payload %seed
    OpAtomicStore %flag %device %release %one
    OpStore %observed_out %seed
    OpBranch %done
    %consume = OpLabel
    %ready_flag = OpAtomicLoad %uint %flag %device %acquire
    %ready = OpINotEqual %bool %ready_flag %zero
    OpSelectionMerge %consumed None
    OpBranchConditional %ready %copy %skip
    %copy = OpLabel
    %published = OpLoad %uint %payload
    OpStore %observed_out %published
    OpBranch %consumed
    %skip = OpLabel
    OpStore %observed_out %zero
    OpBranch %consumed
    %consumed = OpLabel
    OpBranch %done
    %done = OpLabel
    OpReturn
    OpFunctionEnd
  """

  test("device-scope atomic election publishes a payload across workgroups without waiting for all groups") {
    for {
      groups   <- Vec[Int > 0](1, 2, 3, 5, 9)
      parallel <- Vec(false, true)
    } {
      val size   = 12 + groups.raw * 8
      val memory = RegionMemory()
      val region = memory.allocate(size.toLong.refine[Long > 0].value)
      for (seed <- Vec(17L, 91L)) {
        assertEquals(memory.write(region, 0L, Vec.fill(size)(0.toByte)), Right(()))
        val schedule = Interleaving()
        val program  = load(publication, if (parallel) schedule else Workgroups.Sequential).value
        val launch   = task(
          "publish",
          Vec(buffer(region, 12L), buffer(region), buffer(region, 4L), buffer(region, 8L), scalar(seed, 4)),
          groups = testDimensions(groups)
        )
        assertEquals(execute(program, launch, schedule.InterleavedMemory(memory)), Right(()))
        val published = words(memory, region, 0, size / 4)
        assertEquals(published.take(3), Vec(groups.raw.toLong, seed, 1L))
        val records = published.drop(3).grouped(2).toVector
        assertEquals(records.map(_.head).sorted, (0L until groups.raw.toLong).toVector)
        assert(records.forall(record => record(1) == 0L || record(1) == seed))
        assert(records.exists(_(1) == seed))
        if (parallel) assertEquals(schedule.calls.get(), if (groups > 1) 1 else 0)
        if (groups > 1 && !parallel) {
          val consumers = records.filter(_.head != 0L)
          assert(consumers.nonEmpty)
          assert(consumers.forall(_(1) == seed))
        }
        // a completed producer launch makes the flag visible without co-resident workgroups
        assertEquals(execute(program, launch, schedule.InterleavedMemory(memory)), Right(()))
        val consumed = words(memory, region, 0, size / 4)
        assertEquals(consumed.take(3), Vec(groups.raw.toLong * 2, seed, 1L))
        val observed = consumed.drop(3).grouped(2).toVector
        assertEquals(observed.map(_.head).sorted, (groups.raw.toLong until groups.raw.toLong * 2).toVector)
        assert(observed.forall(_(1) == seed))
      }
    }
  }
}
