package net.kurobako.gemstone.isa.spirv

import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.testing.*

class PrivateBoundsSuite extends munit.FunSuite {
  private val OutOfRange = Seq(-1, 4)

  for {
    storage <- Seq("Private", "Function")
    index   <- OutOfRange
    shader  <- booleans
  } test(s"$storage vector index $index ${if (shader) "is robust in GLCompute" else "faults in a Kernel"}") {
    val output = if (shader) "Uniform" else "CrossWorkgroup"
    val local  = "%local = OpVariable %local_ptr " + storage
    val header =
      if (!shader) """OpEntryPoint Kernel %main "bounds""""
      else s"""OpEntryPoint GLCompute %main "bounds" %out ${if (storage == "Private") "%local" else ""}
              |OpDecorate %block BufferBlock
              |OpMemberDecorate %block 0 Offset 0
              |OpMemberDecorate %block 1 Offset 4
              |OpDecorate %out DescriptorSet 0
              |OpDecorate %out Binding 0""".stripMargin
    val module = spv"""
      ${if (shader) vulkan else openCL}
      $header
      OpExecutionMode %main LocalSize 1 1 1
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %int = OpTypeInt 32 1
      %uint4 = OpTypeVector %uint 4
      %block = OpTypeStruct %uint %uint
      %block_ptr = OpTypePointer $output %block
      %out_uint_ptr = OpTypePointer $output %uint
      %local_ptr = OpTypePointer $storage %uint4
      %local_uint_ptr = OpTypePointer $storage %uint
      %fn = OpTypeFunction %void ${if (shader) "" else "%block_ptr"}
      %int_0 = OpConstant %int 0
      %int_1 = OpConstant %int 1
      %index = OpConstant %int $index
      %stored = OpConstant %uint 99
      %c11 = OpConstant %uint 11
      %c22 = OpConstant %uint 22
      %c33 = OpConstant %uint 33
      %c44 = OpConstant %uint 44
      %initial = OpConstantComposite %uint4 %c11 %c22 %c33 %c44
      ${if (shader) "%out = OpVariable %block_ptr Uniform" else ""}
      ${if (storage == "Private") local else ""}
      %main = OpFunction %void None %fn
      ${if (shader) "" else "%out = OpFunctionParameter %block_ptr"}
      %entry = OpLabel
      ${if (storage == "Function") local else ""}
      OpStore %local %initial
      %element = OpAccessChain %local_uint_ptr %local %index
      OpStore %element %stored
      %loaded = OpLoad %uint %element
      %lane_ptr = OpAccessChain %local_uint_ptr %local %int_1
      %lane = OpLoad %uint %lane_ptr
      %out_0 = OpAccessChain %out_uint_ptr %out %int_0
      OpStore %out_0 %loaded
      %out_1 = OpAccessChain %out_uint_ptr %out %int_1
      OpStore %out_1 %lane
      OpReturn
      OpFunctionEnd
    """
    val memory = RegionMemory()
    val out    = memory.allocate(8L)
    val result = run(module, "bounds", memory, Vec(buffer(out)))
    if (shader) {
      assertEquals(result, Right(()))
      assertEquals(words(memory, out, offset = 0, count = 2), Vec(0L, 22L))
    } else {
      assert(result.isLeft, result)
      assertEquals(words(memory, out, offset = 0, count = 2), Vec(0L, 0L))
    }
  }
}
