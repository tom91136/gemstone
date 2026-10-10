package net.kurobako.gemstone.isa.spirv

import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.testing.*

class FragmentQuadSuite extends munit.FunSuite {
  private def divergentFlow(flow: String): String = flow match {
    case "selection"                   => """
        %below        = OpFOrdLessThan %bool %x %float_3
        OpSelectionMerge %merge None
        OpBranchConditional %below %divergent %merge
        %divergent    = OpLabel
        %divergent_dx = OpDPdx %float %squared
        OpBranch %merge
        %merge        = OpLabel
      """
    case "loop" | "break" | "continue" =>
      val exit =
        if (flow == "break") """
          OpBranch %body
          %body         = OpLabel
          %running      = OpFOrdLessThan %bool %i %x
          OpSelectionMerge %guarded None
          OpBranchConditional %running %guarded %merge
          %guarded      = OpLabel
        """
        else """
          %running      = OpFOrdLessThan %bool %i %x
          OpBranchConditional %running %body %merge
          %body         = OpLabel
        """
      val skip =
        if (flow == "continue") """
          %early        = OpFOrdLessThan %bool %i %float_3
          OpSelectionMerge %guarded None
          OpBranchConditional %early %continue %guarded
          %guarded      = OpLabel
        """
        else ""
      s"""
        OpBranch %header
        %header       = OpLabel
        %i            = OpPhi %float %float_0 %entry %next %continue
        OpLoopMerge %merge %continue None
        $exit
        $skip
        %divergent_dx = OpDPdx %float %squared
        OpBranch %continue
        %continue     = OpLabel
        %next         = OpFAdd %float %i %float_1
        OpBranch %header
        %merge        = OpLabel
      """
    case "call"         => "%called = OpFunctionCall %void %callee"
    case "return_value" => "%called = OpFunctionCall %float %callee"
    case _              => ""
  }

  private def callee(flow: String): String = {
    val (result, signature, exit) =
      if (flow == "call") ("%void", "%fn", "OpReturn") else ("%float", "%float_fn", "OpReturnValue %value")
    if (flow != "call" && flow != "return_value") ""
    else s"""
      %callee       = OpFunction $result None $signature
      %callee_entry = OpLabel
      %value        = OpLoad %float %in
      %small        = OpFOrdLessThan %bool %value %float_3
      OpSelectionMerge %callee_merge None
      OpBranchConditional %small %early_exit %callee_merge
      %early_exit   = OpLabel
      $exit
      %callee_merge = OpLabel
      %callee_dy    = OpDPdy %float %value
      $exit
      OpFunctionEnd
    """
  }

  private def module(operation: String, repeated: Boolean, flow: String): Module = spv"""
    OpCapability DerivativeControl
    $vulkan
    OpEntryPoint Fragment %main "main" %in %out %buffer %helper
    OpExecutionMode %main OriginUpperLeft
    OpDecorate %in Location 0
    OpDecorate %out Location 0
    OpDecorate %helper BuiltIn HelperInvocation
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
    %float_fn   = OpTypeFunction %float
    %float_1    = OpConstant %float 1
    %float_0    = OpConstant %float 0
    %float_3    = OpConstant %float 3
    %uint       = OpTypeInt 32 0
    %block      = OpTypeStruct %uint %float
    %block_ptr  = OpTypePointer StorageBuffer %block
    %uint_ptr   = OpTypePointer StorageBuffer %uint
    %float_ptr  = OpTypePointer StorageBuffer %float
    %bool       = OpTypeBool
    %bool_ptr   = OpTypePointer Input %bool
    %uint_0     = OpConstant %uint 0
    %uint_1     = OpConstant %uint 1
    %in         = OpVariable %in_ptr Input
    %out        = OpVariable %out_ptr Output
    %buffer     = OpVariable %block_ptr StorageBuffer
    %helper     = OpVariable %bool_ptr Input
    %main       = OpFunction %void None %fn
    %entry      = OpLabel
    %x          = OpLoad %float %in
    ; helpers may read their own outputs even though raster output is discarded
    OpStore %out %x
    %reloaded   = OpLoad %float %out
    %squared    = OpFMul %float %reloaded %reloaded
    ${divergentFlow(flow)}
    %derivative = Op$operation %float %squared
    ${if (repeated) "%repeated = OpDPdyFine %float %derivative" else ""}
    OpStore %out ${if (repeated) "%repeated" else "%derivative"}
    %counter    = OpAccessChain %uint_ptr %buffer %uint_0
    %previous   = OpAtomicIAdd %uint %counter %uint_1 %uint_0 %uint_1
    %last       = OpAccessChain %float_ptr %buffer %uint_1
    OpStore %last %x
    OpReturn
    OpFunctionEnd
    ${callee(flow)}
  """

  private val expectations = Seq(
    "DPdx"         -> Seq(3f, 3f, 16f, 16f),
    "DPdy"         -> Seq(8f, 21f, 8f, 21f),
    "Fwidth"       -> Seq(11f, 24f, 24f, 37f),
    "DPdxFine"     -> Seq(3f, 3f, 16f, 16f),
    "DPdyFine"     -> Seq(8f, 21f, 8f, 21f),
    "FwidthFine"   -> Seq(11f, 24f, 24f, 37f),
    "DPdxCoarse"   -> Seq.fill(4)(3f),
    "DPdyCoarse"   -> Seq.fill(4)(8f),
    "FwidthCoarse" -> Seq.fill(4)(11f)
  )
  private val flows = Seq("uniform", "selection", "loop", "break", "continue", "call", "return_value")

  private val cases = expectations.zipWithIndex.map { case ((operation, expected), index) =>
    (operation, expected, false, flows(index % flows.size))
  } ++ Seq("loop", "call").map(flow => ("DPdxFine", Seq.fill(4)(13f), true, flow)) ++
    Seq(
      "DPdyFine"     -> "uniform",
      "FwidthFine"   -> "selection",
      "DPdxFine"     -> "return_value",
      "DPdxCoarse"   -> "loop",
      "DPdyCoarse"   -> "break",
      "FwidthCoarse" -> "continue",
      "DPdxCoarse"   -> "call"
    ).map((operation, flow) => (operation, expectations.toMap.apply(operation), false, flow))

  for ((operation, expected, repeated, flow) <- cases) {
    val chain = if (repeated) s"Op$operation then OpDPdyFine" else s"Op$operation"
    test(s"$chain after $flow flow uses nonlinear lane values and suppresses helper side effects") {
      val memory = RegionMemory()
      val region = memory.allocate(96L)
      writeWords(memory, region, 16, Seq(1f, 2f, 3f, 5f, 2f, 4f, 6f, 10f).map(floatBits)*)
      writeWords(memory, region, 8, 0xfeedL)
      writeWords(memory, region, 88, 0xbeefL)
      assertEquals(memory.write(region, 80L, Vec[Byte](0, 1, 0, 1, 0, 1, 0, 1)), Right(()))
      val arguments = Vec(
        buffer(region, 0L, Some(8L)),
        buffer(region, 16L, Some(32L)),
        buffer(region, 48L, Some(32L)),
        buffer(region, 80L, Some(8L))
      )
      assertEquals(
        run(module(operation, repeated, flow), "main", memory, arguments, testDimensions(4), testDimensions(2)),
        Right(())
      )
      assertEquals(
        words(memory, region, offset = 48, count = 8),
        Vec.from((expected ++ expected.map(_ * 4)).map(floatBits))
      )
      assertEquals(words(memory, region, offset = 0, count = 2), Vec(4L, floatBits(6f)))
      assertEquals(word(memory, region, 8), 0xfeedL)
      assertEquals(word(memory, region, 88), 0xbeefL)
    }
  }
}
