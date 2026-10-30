package net.kurobako.gemstone.isa.spirv

import fixtures.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.bits.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*

class AtomicSuite extends Suite {
  private val AtomicWidths = Seq(32, 64)

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
    width            <- AtomicWidths
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
      Vec(single(2.5f), single(1.5f), single(1.5f)).flatMap(LittleEndian.toVec(_, 4))
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
      Vec(single(5f), single(3f), single(1f)).flatMap(LittleEndian.toVec(_, 4))
    )
  }

  /** A kernel that runs `access` through `%moved`, a pointer `offset` bytes past `%target`. */
  private def offsetKernel(element: String, offset: Int, access: String): Module = kernel(
    element,
    s"""%offset = OpConstant %ulong $offset
       |%value = OpConstant $element 1""".stripMargin,
    s"""%address = OpConvertPtrToU %ulong %target
       |%shifted = OpIAdd %ulong %address %offset
       |%moved = OpConvertUToPtr %ptr %shifted
       |$access""".stripMargin
  )

  private def offsetRun(element: String, offset: Int, access: String)(using munit.Location) = {
    val memory = RegionMemory()
    val target = memory.allocate(32L)
    run(offsetKernel(element, offset, access), "atomic", memory, Vec(buffer(target)))
  }

  for {
    (element, size) <- Seq("%uint" -> 4, "%ulong" -> 8)
    (name, access)  <- Seq(
      "OpAtomicIAdd"            -> s"%old = OpAtomicIAdd $element %moved %scope %semantics %value",
      "OpAtomicLoad"            -> s"%old = OpAtomicLoad $element %moved %scope %semantics",
      "OpAtomicStore"           -> "OpAtomicStore %moved %scope %semantics %value",
      "OpAtomicCompareExchange" ->
        s"%old = OpAtomicCompareExchange $element %moved %scope %semantics %semantics %value %value"
    )
  } {
    test(s"$size-byte $name at a multiple of its size runs") {
      assertEquals(offsetRun(element, size, access), Right(()))
    }
    test(s"$size-byte $name at an odd multiple of ${size / 2} bytes faults as misaligned") {
      val result = offsetRun(element, size / 2, access)
      assert(result.left.exists { case Fault.Misaligned(_) => true; case _ => false }, result)
    }
  }

  test("loads and stores that declare byte alignment run at any offset") {
    assertEquals(
      offsetRun("%ulong", 3, "%loaded = OpLoad %ulong %moved Aligned 1\nOpStore %moved %loaded Aligned 1"),
      Right(())
    )
  }
}
