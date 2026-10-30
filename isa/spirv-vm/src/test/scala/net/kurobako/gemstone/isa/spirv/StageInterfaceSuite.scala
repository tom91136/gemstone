package net.kurobako.gemstone.isa.spirv

import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.bits.*
import net.kurobako.gemstone.testing.either.*

class StageInterfaceSuite extends Suite {
  private def origin(model: String): String = if (model == "Fragment") "OpExecutionMode %main OriginUpperLeft" else ""

  private def square(model: String): Module = spv"""
    $vulkan
    OpEntryPoint $model %main "main" %in %out
    ${origin(model)}
    OpDecorate %in Location 0
    OpDecorate %out Location 0
    %void    = OpTypeVoid
    %float   = OpTypeFloat 32
    %in_ptr  = OpTypePointer Input %float
    %out_ptr = OpTypePointer Output %float
    %fn      = OpTypeFunction %void
    %in      = OpVariable %in_ptr Input
    %out     = OpVariable %out_ptr Output
    %main    = OpFunction %void None %fn
    %entry   = OpLabel
    %x       = OpLoad %float %in
    %squared = OpFMul %float %x %x
    OpStore %out %squared
    OpReturn
    OpFunctionEnd
  """

  for (model <- Seq("Vertex", "Fragment")) test(s"$model keeps invocation input and output records distinct") {
    val memory = RegionMemory()
    val region = memory.allocate(24L)
    writeWords(memory, region, 0, single(2f), single(3f), single(4f))
    assertEquals(
      run(square(model), "main", memory, Vec(buffer(region), buffer(region, 12L)), groups = testDimensions(3)),
      Right(())
    )
    assertEquals(words(memory, region, offset = 12, count = 3), Vec(single(4f), single(9f), single(16f)))
  }

  test("VulkanCompute validation rejects graphics entry points") {
    for (model <- Seq("Vertex", "Fragment"))
      assertNotEquals(
        Validation.validate(square(model), Validation.Environment.VulkanCompute),
        ValidationOutcome.Accepted
      )
  }

  for {
    model          <- Seq("Vertex", "Fragment")
    (shape, count) <- Seq("float" -> 1, "vec4" -> 4, "matrix" -> 8, "array" -> 16)
  } test(s"$model initialises every $shape output record on every launch") {
    val module = spv"""
      $vulkan
      OpEntryPoint $model %main "main" %out
      ${origin(model)}
      OpDecorate %out Location 0
      %void         = OpTypeVoid
      %float        = OpTypeFloat 32
      %vec4         = OpTypeVector %float 4
      %matrix       = OpTypeMatrix %vec4 2
      %uint         = OpTypeInt 32 0
      %two          = OpConstant %uint 2
      %array        = OpTypeArray %matrix %two
      %float_one    = OpConstant %float 1
      %vec4_one     = OpConstantComposite %vec4 %float_one %float_one %float_one %float_one
      %matrix_one   = OpConstantComposite %matrix %vec4_one %vec4_one
      %array_one    = OpConstantComposite %array %matrix_one %matrix_one
      %out_ptr      = OpTypePointer Output %$shape
      %out          = OpVariable %out_ptr Output %${shape}_one
      %fn           = OpTypeFunction %void
      %main         = OpFunction %void None %fn
      %entry        = OpLabel
      OpReturn
      OpFunctionEnd
    """
    val size    = 3 * count * 4 + 8
    val memory  = RegionMemory()
    val region  = memory.allocate(Vec.fill(size)(0x5a.toByte))
    val program = load(module).value
    val launch  = task("main", Vec(buffer(region, 4L)), groups = testDimensions(3))
    for (_ <- 0 until 2) {
      java.util.Arrays.fill(memory.array(region), 0x5a.toByte)
      assertEquals(execute(program, launch, memory), Right(()))
      assertEquals(words(memory, region, 4, 3 * count), Vec.fill(3 * count)(single(1f)))
      assertEquals(word(memory, region, 0), 0x5a5a5a5aL)
      assertEquals(word(memory, region, size - 4), 0x5a5a5a5aL)
    }
  }
}
