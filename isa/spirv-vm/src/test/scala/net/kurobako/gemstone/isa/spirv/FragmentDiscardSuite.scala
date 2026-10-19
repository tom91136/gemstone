package net.kurobako.gemstone.isa.spirv

import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.bits.*

class FragmentDiscardSuite extends munit.FunSuite {
  // SPIR-V 3.3.17: OpTerminateInvocation stops the invocation as OpKill does
  private def discard(terminator: String) = s"""
    %discarded = OpLoad %float %in
    %small     = OpFOrdLessThan %bool %discarded %float_3
    OpSelectionMerge %kept None
    OpBranchConditional %small %killed %kept
    %killed    = OpLabel
    $terminator
    %kept      = OpLabel
  """

  private def callee(terminator: String) = s"""
    %nested       = OpFunction %void None %fn
    %nested_entry = OpLabel
    ${discard(terminator)}
    OpReturn
    OpFunctionEnd
  """

  private def module(nested: Boolean, derivative: Boolean, terminator: String): Module = spv"""
    $vulkan
    OpEntryPoint Fragment %main "main" %in %out %buffer %helper %mask
    OpExecutionMode %main OriginUpperLeft
    OpDecorate %in Location 0
    OpDecorate %out Location 0
    OpDecorate %helper BuiltIn HelperInvocation
    OpDecorate %mask BuiltIn SampleMask
    OpDecorate %buffer Binding 0
    OpDecorate %buffer DescriptorSet 0
    OpDecorate %block Block
    OpMemberDecorate %block 0 Offset 0
    OpMemberDecorate %block 1 Offset 4
    %void       = OpTypeVoid
    %float      = OpTypeFloat 32
    %in_ptr     = OpTypePointer Input %float
    %out_ptr    = OpTypePointer Output %float
    %fn         = OpTypeFunction %void
    %uint       = OpTypeInt 32 0
    %block      = OpTypeStruct %uint %uint
    %block_ptr  = OpTypePointer StorageBuffer %block
    %uint_ptr   = OpTypePointer StorageBuffer %uint
    %bool       = OpTypeBool
    %bool_ptr   = OpTypePointer Input %bool
    %uint_0     = OpConstant %uint 0
    %uint_1     = OpConstant %uint 1
    %float_3    = OpConstant %float 3
    %all_lanes  = OpConstant %uint 0xffffffff
    %mask_type  = OpTypeArray %uint %uint_1
    %mask_ptr   = OpTypePointer Output %mask_type
    %mask_init  = OpConstantComposite %mask_type %all_lanes
    %in         = OpVariable %in_ptr Input
    %out        = OpVariable %out_ptr Output
    %buffer     = OpVariable %block_ptr StorageBuffer
    %helper     = OpVariable %bool_ptr Input
    %mask       = OpVariable %mask_ptr Output %mask_init
    %main       = OpFunction %void None %fn
    %entry      = OpLabel
    %before     = OpAccessChain %uint_ptr %buffer %uint_0
    %entered    = OpAtomicIAdd %uint %before %uint_1 %uint_0 %uint_1
    ${if (nested) "%call = OpFunctionCall %void %nested" else discard(terminator)}
    %x          = OpLoad %float %in
    ${if (derivative) "%dx = OpDPdx %float %x" else ""}
    OpStore %out ${if (derivative) "%dx" else "%x"}
    %after      = OpAccessChain %uint_ptr %buffer %uint_1
    %survived   = OpAtomicIAdd %uint %after %uint_1 %uint_0 %uint_1
    OpReturn
    OpFunctionEnd
    ${if (nested) callee(terminator) else ""}
  """

  private val patterns = Seq(
    "some"  -> Seq(1f, 2f, 3f, 5f),
    "every" -> Seq.fill(4)(1f),
    "no"    -> Seq.fill(4)(5f)
  )

  private val Terminators = Seq("OpKill", "OpTerminateInvocation")

  private val kills = pairwise(Seq(false, true), Seq(false, true), Seq(false, true), patterns)

  // partial kills before a derivative, inline and nested, with and without helper lanes
  private val partialDerivatives = for {
    nested  <- booleans
    helpers <- booleans
  } yield (nested, true, helpers, patterns.head)

  for {
    (nested, derivative, helpers, (pattern, inputs)) <- kills ++ partialDerivatives.filterNot(kills.toSet)
    terminator                                       <- Terminators
  } test(
    s"$terminator of $pattern lanes, nested $nested, derivative $derivative, helpers $helpers keeps earlier work"
  ) {
    val memory = RegionMemory()
    val region = memory.allocate(128L)
    val values = inputs ++ inputs.reverse
    writeWords(memory, region, 16, values.map(single)*)
    val flags = Seq.tabulate(8)(i => if (helpers && i % 2 == 1) 1.toByte else 0.toByte)
    assertEquals(memory.write(region, 80L, Vec.from(flags)), Right(()))
    val arguments = Vec(
      buffer(region, 0L, Some(8L)),
      buffer(region, 16L, Some(32L)),
      buffer(region, 48L, Some(32L)),
      buffer(region, 80L, Some(8L)),
      buffer(region, 88L, Some(32L))
    )
    val local  = testDimensions(if (derivative) 4 else 1)
    val groups = testDimensions(if (derivative) 2 else 8)
    assertEquals(run(module(nested, derivative, terminator), "main", memory, arguments, local, groups), Right(()))
    assertEquals(word(memory, region, 0), flags.count(_ == 0).toLong)
    assertEquals(word(memory, region, 4), values.zip(flags).count((v, h) => v >= 3 && h == 0).toLong)
    for ((v, i) <- values.zipWithIndex) {
      assertEquals(word(memory, region, 88 + i * 4), if (v < 3) 0L else 0xffffffffL)
      if (v < 3) assertEquals(word(memory, region, 48 + i * 4), 0L)
    }
  }
}
