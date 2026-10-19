package net.kurobako.gemstone.isa.spirv

import fixtures.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.bits.*

class CooperativeVectorSuite extends munit.FunSuite {

  // glslangValidator -V --target-env vulkan1.3 of a GL_NV_cooperative_vector shader on two invocations: each loads
  // eight halves, multiplies them by a row-major 4x8 half matrix with stride 24 plus a half bias, by the same memory
  // read column-major with stride 8, and by a transposed InferencingOptimal E4M3 matrix after converting the input to
  // E4M3; stores the three float vectors, accumulates the outer product of the first product, as halves, with the
  // input into a TrainingOptimal float matrix and sums the second product into the same buffer
  private val floating =
    spv"""
    ; Version: 1.6
    OpCapability Shader
    OpCapability Float16
    OpCapability Int8
    OpCapability StorageBuffer16BitAccess
    OpCapability StorageBuffer8BitAccess
    OpCapability VulkanMemoryModel
    OpCapability CooperativeVectorNV
    OpCapability CooperativeVectorTrainingNV
    OpExtension "SPV_NV_cooperative_vector"
    %1 = OpExtInstImport "GLSL.std.450"
    OpMemoryModel Logical Vulkan
    OpEntryPoint GLCompute %main "main" %gl_LocalInvocationID %inp %mat %fp8 %outp %grad
    OpExecutionMode %main LocalSize 2 1 1
    OpDecorate %gl_LocalInvocationID BuiltIn LocalInvocationId
    OpDecorate %_runtimearr_half ArrayStride 2
    OpDecorate %Input Block
    OpMemberDecorate %Input 0 Offset 0
    OpDecorate %inp Binding 0
    OpDecorate %inp DescriptorSet 0
    OpDecorate %_runtimearr_half_0 ArrayStride 2
    OpDecorate %Matrix Block
    OpMemberDecorate %Matrix 0 Offset 0
    OpDecorate %mat Binding 1
    OpDecorate %mat DescriptorSet 0
    OpDecorate %_runtimearr_uchar ArrayStride 1
    OpDecorate %Fp8 Block
    OpMemberDecorate %Fp8 0 Offset 0
    OpDecorate %fp8 Binding 2
    OpDecorate %fp8 DescriptorSet 0
    OpDecorate %_runtimearr_float ArrayStride 4
    OpDecorate %Output Block
    OpMemberDecorate %Output 0 Offset 0
    OpDecorate %outp Binding 3
    OpDecorate %outp DescriptorSet 0
    OpDecorate %_runtimearr_float_0 ArrayStride 4
    OpDecorate %Grad Block
    OpMemberDecorate %Grad 0 Offset 0
    OpDecorate %grad Binding 4
    OpDecorate %grad DescriptorSet 0
    %void = OpTypeVoid
    %3 = OpTypeFunction %void
    %uint = OpTypeInt 32 0
    %_ptr_Function_uint = OpTypePointer Function %uint
    %v3uint = OpTypeVector %uint 3
    %_ptr_Input_v3uint = OpTypePointer Input %v3uint
    %gl_LocalInvocationID = OpVariable %_ptr_Input_v3uint Input
    %uint_0 = OpConstant %uint 0
    %_ptr_Input_uint = OpTypePointer Input %uint
    %half = OpTypeFloat 16
    %uint_8 = OpConstant %uint 8
    %18 = OpTypeVectorIdEXT %half %uint_8
    %_ptr_Function_18 = OpTypePointer Function %18
    %_runtimearr_half = OpTypeRuntimeArray %half
    %Input = OpTypeStruct %_runtimearr_half
    %_ptr_StorageBuffer_Input = OpTypePointer StorageBuffer %Input
    %inp = OpVariable %_ptr_StorageBuffer_Input StorageBuffer
    %int = OpTypeInt 32 1
    %int_0 = OpConstant %int 0
    %_ptr_StorageBuffer__runtimearr_half = OpTypePointer StorageBuffer %_runtimearr_half
    %uint_16 = OpConstant %uint 16
    %float = OpTypeFloat 32
    %uint_4 = OpConstant %uint 4
    %37 = OpTypeVectorIdEXT %float %uint_4
    %_ptr_Function_37 = OpTypePointer Function %37
    %_runtimearr_half_0 = OpTypeRuntimeArray %half
    %Matrix = OpTypeStruct %_runtimearr_half_0
    %_ptr_StorageBuffer_Matrix = OpTypePointer StorageBuffer %Matrix
    %mat = OpVariable %_ptr_StorageBuffer_Matrix StorageBuffer
    %_ptr_StorageBuffer__runtimearr_half_0 = OpTypePointer StorageBuffer %_runtimearr_half_0
    %uint_128 = OpConstant %uint 128
    %bool = OpTypeBool
    %false = OpConstantFalse %bool
    %uint_24 = OpConstant %uint 24
    %int_1 = OpConstant %int 1
    %int_1000491002 = OpConstant %int 1000491002
    %uchar = OpTypeInt 8 0
    %_runtimearr_uchar = OpTypeRuntimeArray %uchar
    %Fp8 = OpTypeStruct %_runtimearr_uchar
    %_ptr_StorageBuffer_Fp8 = OpTypePointer StorageBuffer %Fp8
    %fp8 = OpVariable %_ptr_StorageBuffer_Fp8 StorageBuffer
    %_ptr_StorageBuffer__runtimearr_uchar = OpTypePointer StorageBuffer %_runtimearr_uchar
    %int_2 = OpConstant %int 2
    %true = OpConstantTrue %bool
    %_runtimearr_float = OpTypeRuntimeArray %float
    %Output = OpTypeStruct %_runtimearr_float
    %_ptr_StorageBuffer_Output = OpTypePointer StorageBuffer %Output
    %outp = OpVariable %_ptr_StorageBuffer_Output StorageBuffer
    %_ptr_StorageBuffer__runtimearr_float = OpTypePointer StorageBuffer %_runtimearr_float
    %uint_48 = OpConstant %uint 48
    %uint_32 = OpConstant %uint 32
    %98 = OpTypeVectorIdEXT %half %uint_4
    %_ptr_Function_98 = OpTypePointer Function %98
    %_runtimearr_float_0 = OpTypeRuntimeArray %float
    %Grad = OpTypeStruct %_runtimearr_float_0
    %_ptr_StorageBuffer_Grad = OpTypePointer StorageBuffer %Grad
    %grad = OpVariable %_ptr_StorageBuffer_Grad StorageBuffer
    %_ptr_StorageBuffer__runtimearr_float_0 = OpTypePointer StorageBuffer %_runtimearr_float_0
    %int_3 = OpConstant %int 3
    %uint_2 = OpConstant %uint 2
    %uint_1 = OpConstant %uint 1
    %118 = OpConstantComposite %v3uint %uint_2 %uint_1 %uint_1
    %main = OpFunction %void None %3
    %5 = OpLabel
    %lane = OpVariable %_ptr_Function_uint Function
    %tempArg = OpVariable %_ptr_Function_18 Function
    %v = OpVariable %_ptr_Function_18 Function
    %tempArg_0 = OpVariable %_ptr_Function_37 Function
    %r = OpVariable %_ptr_Function_37 Function
    %tempArg_1 = OpVariable %_ptr_Function_37 Function
    %s = OpVariable %_ptr_Function_37 Function
    %tempArg_2 = OpVariable %_ptr_Function_37 Function
    %t = OpVariable %_ptr_Function_37 Function
    %h = OpVariable %_ptr_Function_98 Function
    %w = OpVariable %_ptr_Function_37 Function
    %14 = OpAccessChain %_ptr_Input_uint %gl_LocalInvocationID %uint_0
    %15 = OpLoad %uint %14
    OpStore %lane %15
    %28 = OpAccessChain %_ptr_StorageBuffer__runtimearr_half %inp %int_0
    %30 = OpLoad %uint %lane
    %31 = OpIMul %uint %uint_16 %30
    %32 = OpCooperativeVectorLoadNV %18 %28 %31 None
    OpStore %tempArg %32
    %34 = OpLoad %18 %tempArg
    OpStore %v %34
    %40 = OpLoad %18 %v
    %46 = OpAccessChain %_ptr_StorageBuffer__runtimearr_half_0 %mat %int_0
    %47 = OpAccessChain %_ptr_StorageBuffer__runtimearr_half_0 %mat %int_0
    %52 = OpCooperativeVectorMatrixMulAddNV %37 %40 %int_0 %46 %uint_0 %int_0 %47 %uint_128 %int_0 %uint_4 %uint_8 %int_0 %false %uint_24
    OpStore %tempArg_0 %52
    %54 = OpLoad %37 %tempArg_0
    OpStore %r %54
    %56 = OpLoad %18 %v
    %57 = OpAccessChain %_ptr_StorageBuffer__runtimearr_half_0 %mat %int_0
    %59 = OpCooperativeVectorMatrixMulNV %37 %56 %int_0 %57 %uint_0 %int_0 %uint_4 %uint_8 %int_1 %false %uint_8
    OpStore %tempArg_1 %59
    %61 = OpLoad %37 %tempArg_1
    OpStore %s %61
    %63 = OpLoad %18 %v
    %71 = OpAccessChain %_ptr_StorageBuffer__runtimearr_uchar %fp8 %int_0
    %74 = OpCooperativeVectorMatrixMulNV %37 %63 %int_1000491002 %71 %uint_0 %int_1000491002 %uint_4 %uint_8 %int_2 %true %uint_0
    OpStore %tempArg_2 %74
    %76 = OpLoad %37 %tempArg_2
    OpStore %t %76
    %77 = OpLoad %37 %r
    %83 = OpAccessChain %_ptr_StorageBuffer__runtimearr_float %outp %int_0
    %85 = OpLoad %uint %lane
    %86 = OpIMul %uint %uint_48 %85
    OpCooperativeVectorStoreNV %83 %86 %77 None
    %87 = OpLoad %37 %s
    %88 = OpAccessChain %_ptr_StorageBuffer__runtimearr_float %outp %int_0
    %89 = OpLoad %uint %lane
    %90 = OpIMul %uint %uint_48 %89
    %91 = OpIAdd %uint %90 %uint_16
    OpCooperativeVectorStoreNV %88 %91 %87 None
    %92 = OpLoad %37 %t
    %93 = OpAccessChain %_ptr_StorageBuffer__runtimearr_float %outp %int_0
    %94 = OpLoad %uint %lane
    %95 = OpIMul %uint %uint_48 %94
    %97 = OpIAdd %uint %95 %uint_32
    OpCooperativeVectorStoreNV %93 %97 %92 None
    %101 = OpLoad %37 %r
    %102 = OpFConvert %98 %101
    OpStore %h %102
    %103 = OpLoad %98 %h
    %104 = OpLoad %18 %v
    %110 = OpAccessChain %_ptr_StorageBuffer__runtimearr_float_0 %grad %int_0
    OpCooperativeVectorOuterProductAccumulateNV %110 %uint_0 %103 %104 %int_3 %int_1 %uint_0
    %113 = OpLoad %37 %s
    OpStore %w %113
    %114 = OpLoad %37 %w
    %115 = OpAccessChain %_ptr_StorageBuffer__runtimearr_float_0 %grad %int_0
    OpCooperativeVectorReduceSumAccumulateNV %115 %uint_128 %114
    OpReturn
    OpFunctionEnd
  """

  // glslangValidator -V --target-env vulkan1.3 of a GL_NV_cooperative_vector shader: two uints of packed int8 times a
  // row-major int8 matrix plus an int32 bias, eight int32 lanes saturated to int8 times the same matrix, and eight
  // floats rounded and saturated to uint8 times the matrix read column-major as uint8; stores the three products and
  // sums the first into memory
  private val integer =
    spv"""
    ; Version: 1.6
    OpCapability Shader
    OpCapability Int8
    OpCapability StorageBuffer8BitAccess
    OpCapability VulkanMemoryModel
    OpCapability CooperativeVectorNV
    OpCapability CooperativeVectorTrainingNV
    OpExtension "SPV_NV_cooperative_vector"
    %1 = OpExtInstImport "GLSL.std.450"
    OpMemoryModel Logical Vulkan
    OpEntryPoint GLCompute %main "main" %inp %mat %bias %outp
    OpExecutionMode %main LocalSize 1 1 1
    OpDecorate %_runtimearr_uint ArrayStride 4
    OpDecorate %Input Block
    OpMemberDecorate %Input 0 Offset 0
    OpDecorate %inp Binding 0
    OpDecorate %inp DescriptorSet 0
    OpDecorate %_runtimearr_char ArrayStride 1
    OpDecorate %Matrix Block
    OpMemberDecorate %Matrix 0 Offset 0
    OpDecorate %mat Binding 1
    OpDecorate %mat DescriptorSet 0
    OpDecorate %_runtimearr_int ArrayStride 4
    OpDecorate %Bias Block
    OpMemberDecorate %Bias 0 Offset 0
    OpDecorate %bias Binding 2
    OpDecorate %bias DescriptorSet 0
    OpDecorate %_runtimearr_int_0 ArrayStride 4
    OpDecorate %Output Block
    OpMemberDecorate %Output 0 Offset 0
    OpDecorate %outp Binding 3
    OpDecorate %outp DescriptorSet 0
    %void = OpTypeVoid
    %3 = OpTypeFunction %void
    %uint = OpTypeInt 32 0
    %uint_2 = OpConstant %uint 2
    %8 = OpTypeVectorIdEXT %uint %uint_2
    %_ptr_Function_8 = OpTypePointer Function %8
    %_runtimearr_uint = OpTypeRuntimeArray %uint
    %Input = OpTypeStruct %_runtimearr_uint
    %_ptr_StorageBuffer_Input = OpTypePointer StorageBuffer %Input
    %inp = OpVariable %_ptr_StorageBuffer_Input StorageBuffer
    %int = OpTypeInt 32 1
    %int_0 = OpConstant %int 0
    %_ptr_StorageBuffer__runtimearr_uint = OpTypePointer StorageBuffer %_runtimearr_uint
    %uint_0 = OpConstant %uint 0
    %uint_4 = OpConstant %uint 4
    %24 = OpTypeVectorIdEXT %int %uint_4
    %_ptr_Function_24 = OpTypePointer Function %24
    %int_1000491000 = OpConstant %int 1000491000
    %char = OpTypeInt 8 1
    %_runtimearr_char = OpTypeRuntimeArray %char
    %Matrix = OpTypeStruct %_runtimearr_char
    %_ptr_StorageBuffer_Matrix = OpTypePointer StorageBuffer %Matrix
    %mat = OpVariable %_ptr_StorageBuffer_Matrix StorageBuffer
    %_ptr_StorageBuffer__runtimearr_char = OpTypePointer StorageBuffer %_runtimearr_char
    %int_3 = OpConstant %int 3
    %_runtimearr_int = OpTypeRuntimeArray %int
    %Bias = OpTypeStruct %_runtimearr_int
    %_ptr_StorageBuffer_Bias = OpTypePointer StorageBuffer %Bias
    %bias = OpVariable %_ptr_StorageBuffer_Bias StorageBuffer
    %_ptr_StorageBuffer__runtimearr_int = OpTypePointer StorageBuffer %_runtimearr_int
    %int_5 = OpConstant %int 5
    %uint_8 = OpConstant %uint 8
    %bool = OpTypeBool
    %false = OpConstantFalse %bool
    %50 = OpTypeVectorIdEXT %int %uint_8
    %_ptr_Function_50 = OpTypePointer Function %50
    %float = OpTypeFloat 32
    %64 = OpTypeVectorIdEXT %float %uint_8
    %_ptr_Function_64 = OpTypePointer Function %64
    %uint_40 = OpConstant %uint 40
    %72 = OpTypeVectorIdEXT %uint %uint_4
    %_ptr_Function_72 = OpTypePointer Function %72
    %int_7 = OpConstant %int 7
    %int_1 = OpConstant %int 1
    %_runtimearr_int_0 = OpTypeRuntimeArray %int
    %Output = OpTypeStruct %_runtimearr_int_0
    %_ptr_StorageBuffer_Output = OpTypePointer StorageBuffer %Output
    %outp = OpVariable %_ptr_StorageBuffer_Output StorageBuffer
    %_ptr_StorageBuffer__runtimearr_int_0 = OpTypePointer StorageBuffer %_runtimearr_int_0
    %uint_16 = OpConstant %uint 16
    %uint_32 = OpConstant %uint 32
    %uint_48 = OpConstant %uint 48
    %v3uint = OpTypeVector %uint 3
    %uint_1 = OpConstant %uint 1
    %100 = OpConstantComposite %v3uint %uint_1 %uint_1 %uint_1
    %main = OpFunction %void None %3
    %5 = OpLabel
    %tempArg = OpVariable %_ptr_Function_8 Function
    %p = OpVariable %_ptr_Function_8 Function
    %tempArg_0 = OpVariable %_ptr_Function_24 Function
    %r = OpVariable %_ptr_Function_24 Function
    %tempArg_1 = OpVariable %_ptr_Function_50 Function
    %q = OpVariable %_ptr_Function_50 Function
    %tempArg_2 = OpVariable %_ptr_Function_24 Function
    %s = OpVariable %_ptr_Function_24 Function
    %tempArg_3 = OpVariable %_ptr_Function_64 Function
    %f = OpVariable %_ptr_Function_64 Function
    %tempArg_4 = OpVariable %_ptr_Function_72 Function
    %u = OpVariable %_ptr_Function_72 Function
    %18 = OpAccessChain %_ptr_StorageBuffer__runtimearr_uint %inp %int_0
    %20 = OpCooperativeVectorLoadNV %8 %18 %uint_0 None
    OpStore %tempArg %20
    %22 = OpLoad %8 %tempArg
    OpStore %p %22
    %27 = OpLoad %8 %p
    %35 = OpAccessChain %_ptr_StorageBuffer__runtimearr_char %mat %int_0
    %42 = OpAccessChain %_ptr_StorageBuffer__runtimearr_int %bias %int_0
    %47 = OpCooperativeVectorMatrixMulAddNV %24 %27 %int_1000491000 %35 %uint_0 %int_3 %42 %uint_0 %int_5 %uint_4 %uint_8 %int_0 %false %uint_8 MatrixResultSignedComponentsKHR
    OpStore %tempArg_0 %47
    %49 = OpLoad %24 %tempArg_0
    OpStore %r %49
    %53 = OpAccessChain %_ptr_StorageBuffer__runtimearr_uint %inp %int_0
    %54 = OpCooperativeVectorLoadNV %50 %53 %uint_8 None
    OpStore %tempArg_1 %54
    %56 = OpLoad %50 %tempArg_1
    OpStore %q %56
    %58 = OpLoad %50 %q
    %59 = OpAccessChain %_ptr_StorageBuffer__runtimearr_char %mat %int_0
    %60 = OpCooperativeVectorMatrixMulNV %24 %58 %int_3 %59 %uint_0 %int_3 %uint_4 %uint_8 %int_0 %false %uint_8 MatrixBSignedComponentsKHR|MatrixResultSignedComponentsKHR
    OpStore %tempArg_2 %60
    %62 = OpLoad %24 %tempArg_2
    OpStore %s %62
    %67 = OpAccessChain %_ptr_StorageBuffer__runtimearr_uint %inp %int_0
    %69 = OpCooperativeVectorLoadNV %64 %67 %uint_40 None
    OpStore %tempArg_3 %69
    %71 = OpLoad %64 %tempArg_3
    OpStore %f %71
    %75 = OpLoad %64 %f
    %77 = OpAccessChain %_ptr_StorageBuffer__runtimearr_char %mat %int_0
    %79 = OpCooperativeVectorMatrixMulNV %72 %75 %int_7 %77 %uint_0 %int_7 %uint_4 %uint_8 %int_1 %false %uint_4
    OpStore %tempArg_4 %79
    %81 = OpLoad %72 %tempArg_4
    OpStore %u %81
    %82 = OpLoad %24 %r
    %88 = OpAccessChain %_ptr_StorageBuffer__runtimearr_int_0 %outp %int_0
    OpCooperativeVectorStoreNV %88 %uint_0 %82 None
    %89 = OpLoad %24 %s
    %90 = OpAccessChain %_ptr_StorageBuffer__runtimearr_int_0 %outp %int_0
    OpCooperativeVectorStoreNV %90 %uint_16 %89 None
    %92 = OpLoad %72 %u
    %93 = OpAccessChain %_ptr_StorageBuffer__runtimearr_int_0 %outp %int_0
    OpCooperativeVectorStoreNV %93 %uint_32 %92 None
    %95 = OpLoad %24 %r
    %96 = OpAccessChain %_ptr_StorageBuffer__runtimearr_int_0 %outp %int_0
    OpCooperativeVectorReduceSumAccumulateNV %96 %uint_48 %95
    OpReturn
    OpFunctionEnd
  """

  private def halves(memory: RegionMemory, region: AllocationId, values: Seq[Double]): Unit =
    values.zipWithIndex.foreach((value, at) =>
      LittleEndian.write(IEEE754.fromDouble(value, 16), memory.array(region), at * 2, 2)
    )

  private def bytes(memory: RegionMemory, region: AllocationId, values: Seq[Int]): Unit =
    values.zipWithIndex.foreach((value, at) => LittleEndian.write(value.toLong & 0xffL, memory.array(region), at, 1))

  private def half(value: Double): Double = IEEE754.toDouble(IEEE754.fromDouble(value, 16), 16)

  test("float products read row-major, column-major and transposed E4M3 matrices, and training ops accumulate") {
    val memory = RegionMemory()
    val input  = memory.allocate(64L)
    val matrix = memory.allocate(256L)
    val fp8    = memory.allocate(64L)
    val output = memory.allocate(128L)
    val grad   = memory.allocate(256L)
    val x      = Vec.tabulate(2)(lane =>
      Vec.tabulate(8)(k =>
        if (k == 7) Vec(1.0625, 1.1875)(lane) else if (k == 6 && lane == 1) 500.0 else (k - 3 + lane) * 0.5
      )
    )
    // E4M3 rounds ties to even and saturates at 448
    val narrowed = x.map(_.map {
      case 1.0625 => 1.0
      case 1.1875 => 1.25
      case 500.0  => 448.0
      case value  => value
    })
    val h     = Vec.tabulate(72)(at => ((at % 7) - 3) * 0.25)
    val codes = Vec(
      0x38 -> 1.0,
      0x30 -> 0.5,
      0x3c -> 1.5,
      0x40 -> 2.0,
      0xb8 -> -1.0,
      0x01 -> 0.001953125,
      0x34 -> 0.75,
      0xc4 -> -3.0
    )
    val q = Vec.tabulate(32)(at => codes(at % 8))
    halves(memory, input, x.flatten)
    halves(memory, matrix, h)
    bytes(memory, fp8, q.map(_._1))
    assertEquals(
      run(
        floating,
        "main",
        memory,
        Vec(buffer(input), buffer(matrix), buffer(fp8), buffer(output), buffer(grad)),
        local = testDimensions(2)
      ),
      Right(())
    )
    val r = x.map(v => Vec.tabulate(4)(i => (0 until 8).map(k => h(i * 12 + k) * v(k)).sum + h(64 + i)))
    val s = x.map(v => Vec.tabulate(4)(i => (0 until 8).map(k => h(k * 4 + i) * v(k)).sum))
    val t = narrowed.map(v => Vec.tabulate(4)(i => (0 until 8).map(k => q(k * 4 + i)._2 * v(k)).sum))
    assertEquals(
      words(memory, output, offset = 0, count = 24),
      Vec.tabulate(2)(lane => (r(lane) ++ s(lane) ++ t(lane)).map(value => single(value.toFloat))).flatten
    )
    val outer =
      Vec.tabulate(32)(at => (0 until 2).map(lane => half(r(lane)(at / 8).toFloat.toDouble) * x(lane)(at % 8)).sum)
    val sums = Vec.tabulate(4)(i => s(0)(i).toFloat + s(1)(i).toFloat)
    assertEquals(
      words(memory, grad, offset = 0, count = 36),
      outer.map(value => single(value.toFloat)) ++ sums.map(single)
    )
  }

  test("integer products unpack bytes, saturate conversions, wrap at the result width and accumulate") {
    val memory = RegionMemory()
    val input  = memory.allocate(128L)
    val matrix = memory.allocate(64L)
    val bias   = memory.allocate(16L)
    val output = memory.allocate(64L)
    val packed = Vec(1, -2, 3, -4, 127, -128, 5, -6)
    val lanes  = Vec(300, -300, 5, -5, 127, -129, 0, 64)
    val reals  = Vec(0.5f, 1.5f, 2.5f, -3f, 255.4f, 300f, 254.5f, 7.49f)
    val m      = Vec.tabulate(32)(at => ((at * 37 + 11) % 256).toByte.toInt)
    val biases = Vec(1000, -1000, 7, Int.MaxValue)
    val words0 = Vec.tabulate(2)(word =>
      (0 until 4).map(at => (packed(word * 4 + at).toLong & 0xffL) << (at * 8)).reduce(_ | _)
    )
    writeWords(memory, input, 0, (words0 ++ lanes.map(_.toLong & 0xffffffffL) ++ reals.map(single))*)
    bytes(memory, matrix, m)
    writeWords(memory, bias, 0, biases.map(_.toLong & 0xffffffffL)*)
    writeWords(memory, output, 48, 1L, 2L, 3L, 4L)
    assertEquals(
      run(integer, "main", memory, Vec(buffer(input), buffer(matrix), buffer(bias), buffer(output))),
      Right(())
    )
    val saturated = lanes.map(value => math.max(-128, math.min(127, value)))
    val rounded   = Vec(0, 2, 2, 0, 255, 255, 254, 7)
    val r         = Vec.tabulate(4)(i => (0 until 8).map(k => m(i * 8 + k) * packed(k)).sum + biases(i))
    val s         = Vec.tabulate(4)(i => (0 until 8).map(k => m(i * 8 + k) * saturated(k)).sum)
    val u         = Vec.tabulate(4)(i => (0 until 8).map(k => (m(k * 4 + i) & 0xff) * rounded(k)).sum)
    assertEquals(
      words(memory, output, offset = 0, count = 16),
      (r ++ s ++ u ++ r.zipWithIndex.map((value, at) => value + at + 1)).map(_.toLong & 0xffffffffL)
    )
  }
}
