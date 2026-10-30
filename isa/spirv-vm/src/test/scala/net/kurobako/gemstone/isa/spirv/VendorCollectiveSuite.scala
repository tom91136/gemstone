package net.kurobako.gemstone.isa.spirv

import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.bits.*

class VendorCollectiveSuite extends Suite {

  // glslangValidator -V --target-env vulkan1.1 of an 8-invocation compute shader whose first six invocations
  // reduce and scan through GL_AMD_shader_ballot's *InvocationsNonUniformAMD functions, and whose every invocation
  // partitions by parity through GL_NV_shader_subgroup_partitioned's subgroupPartitionNV
  private val module =
    spv"""
    ; Version: 1.3
    OpCapability Shader
    OpCapability Groups
    OpCapability GroupNonUniformPartitionedEXT
    OpExtension "SPV_AMD_shader_ballot"
    OpExtension "SPV_NV_shader_subgroup_partitioned"
    %1 = OpExtInstImport "GLSL.std.450"
    OpMemoryModel Logical GLSL450
    OpEntryPoint GLCompute %main "main" %gl_LocalInvocationID
    OpExecutionMode %main LocalSize 8 1 1
    OpDecorate %gl_LocalInvocationID BuiltIn LocalInvocationId
    OpDecorate %_arr_int_uint_8 ArrayStride 4
    OpDecorate %_arr_uint_uint_8 ArrayStride 4
    OpDecorate %_arr_float_uint_8 ArrayStride 4
    OpDecorate %_arr_int_uint_8_0 ArrayStride 4
    OpDecorate %_arr_uint_uint_8_0 ArrayStride 4
    OpDecorate %_arr_float_uint_8_0 ArrayStride 4
    OpDecorate %_arr_v4uint_uint_8 ArrayStride 16
    OpDecorate %B Block
    OpMemberDecorate %B 0 Offset 0
    OpMemberDecorate %B 1 Offset 32
    OpMemberDecorate %B 2 Offset 64
    OpMemberDecorate %B 3 Offset 96
    OpMemberDecorate %B 4 Offset 128
    OpMemberDecorate %B 5 Offset 160
    OpMemberDecorate %B 6 Offset 192
    OpDecorate %_ Binding 0
    OpDecorate %_ DescriptorSet 0
    OpDecorate %gl_WorkGroupSize BuiltIn WorkgroupSize
    %void = OpTypeVoid
    %3 = OpTypeFunction %void
    %uint = OpTypeInt 32 0
    %_ptr_Function_uint = OpTypePointer Function %uint
    %v3uint = OpTypeVector %uint 3
    %_ptr_Input_v3uint = OpTypePointer Input %v3uint
    %gl_LocalInvocationID = OpVariable %_ptr_Input_v3uint Input
    %uint_0 = OpConstant %uint 0
    %_ptr_Input_uint = OpTypePointer Input %uint
    %uint_6 = OpConstant %uint 6
    %bool = OpTypeBool
    %int = OpTypeInt 32 1
    %uint_8 = OpConstant %uint 8
    %_arr_int_uint_8 = OpTypeArray %int %uint_8
    %_arr_uint_uint_8 = OpTypeArray %uint %uint_8
    %float = OpTypeFloat 32
    %_arr_float_uint_8 = OpTypeArray %float %uint_8
    %_arr_int_uint_8_0 = OpTypeArray %int %uint_8
    %_arr_uint_uint_8_0 = OpTypeArray %uint %uint_8
    %_arr_float_uint_8_0 = OpTypeArray %float %uint_8
    %v4uint = OpTypeVector %uint 4
    %_arr_v4uint_uint_8 = OpTypeArray %v4uint %uint_8
    %B = OpTypeStruct %_arr_int_uint_8 %_arr_uint_uint_8 %_arr_float_uint_8 %_arr_int_uint_8_0 %_arr_uint_uint_8_0 %_arr_float_uint_8_0 %_arr_v4uint_uint_8
    %_ptr_StorageBuffer_B = OpTypePointer StorageBuffer %B
    %_ = OpVariable %_ptr_StorageBuffer_B StorageBuffer
    %int_3 = OpConstant %int 3
    %int_0 = OpConstant %int 0
    %_ptr_StorageBuffer_int = OpTypePointer StorageBuffer %int
    %uint_3 = OpConstant %uint 3
    %int_4 = OpConstant %int 4
    %int_1 = OpConstant %int 1
    %_ptr_StorageBuffer_uint = OpTypePointer StorageBuffer %uint
    %int_5 = OpConstant %int 5
    %int_2 = OpConstant %int 2
    %_ptr_StorageBuffer_float = OpTypePointer StorageBuffer %float
    %int_6 = OpConstant %int 6
    %uint_1 = OpConstant %uint 1
    %_ptr_StorageBuffer_v4uint = OpTypePointer StorageBuffer %v4uint
    %gl_WorkGroupSize = OpConstantComposite %v3uint %uint_8 %uint_1 %uint_1
    %main = OpFunction %void None %3
    %5 = OpLabel
    %l = OpVariable %_ptr_Function_uint Function
    %14 = OpAccessChain %_ptr_Input_uint %gl_LocalInvocationID %uint_0
    %15 = OpLoad %uint %14
    OpStore %l %15
    %16 = OpLoad %uint %l
    %19 = OpULessThan %bool %16 %uint_6
    OpSelectionMerge %21 None
    OpBranchConditional %19 %20 %21
    %20 = OpLabel
    %37 = OpLoad %uint %l
    %39 = OpLoad %uint %l
    %41 = OpAccessChain %_ptr_StorageBuffer_int %_ %int_0 %39
    %42 = OpLoad %int %41
    %44 = OpGroupIAddNonUniformAMD %int %uint_3 Reduce %42
    %45 = OpAccessChain %_ptr_StorageBuffer_int %_ %int_3 %37
    OpStore %45 %44
    %47 = OpLoad %uint %l
    %49 = OpLoad %uint %l
    %51 = OpAccessChain %_ptr_StorageBuffer_uint %_ %int_1 %49
    %52 = OpLoad %uint %51
    %53 = OpGroupUMinNonUniformAMD %uint %uint_3 Reduce %52
    %54 = OpLoad %uint %l
    %55 = OpAccessChain %_ptr_StorageBuffer_uint %_ %int_1 %54
    %56 = OpLoad %uint %55
    %57 = OpGroupUMaxNonUniformAMD %uint %uint_3 Reduce %56
    %58 = OpIAdd %uint %53 %57
    %59 = OpAccessChain %_ptr_StorageBuffer_uint %_ %int_4 %47
    OpStore %59 %58
    %61 = OpLoad %uint %l
    %63 = OpLoad %uint %l
    %65 = OpAccessChain %_ptr_StorageBuffer_float %_ %int_2 %63
    %66 = OpLoad %float %65
    %67 = OpGroupFAddNonUniformAMD %float %uint_3 InclusiveScan %66
    %68 = OpAccessChain %_ptr_StorageBuffer_float %_ %int_5 %61
    OpStore %68 %67
    %69 = OpLoad %uint %l
    %70 = OpLoad %uint %l
    %71 = OpAccessChain %_ptr_StorageBuffer_int %_ %int_0 %70
    %72 = OpLoad %int %71
    %73 = OpGroupSMinNonUniformAMD %int %uint_3 ExclusiveScan %72
    %74 = OpLoad %uint %l
    %75 = OpAccessChain %_ptr_StorageBuffer_int %_ %int_0 %74
    %76 = OpLoad %int %75
    %77 = OpGroupSMaxNonUniformAMD %int %uint_3 Reduce %76
    %78 = OpIAdd %int %73 %77
    %79 = OpAccessChain %_ptr_StorageBuffer_int %_ %int_3 %69
    %80 = OpLoad %int %79
    %81 = OpIAdd %int %80 %78
    %82 = OpAccessChain %_ptr_StorageBuffer_int %_ %int_3 %69
    OpStore %82 %81
    %83 = OpLoad %uint %l
    %84 = OpLoad %uint %l
    %85 = OpAccessChain %_ptr_StorageBuffer_float %_ %int_2 %84
    %86 = OpLoad %float %85
    %87 = OpGroupFMinNonUniformAMD %float %uint_3 Reduce %86
    %88 = OpLoad %uint %l
    %89 = OpAccessChain %_ptr_StorageBuffer_float %_ %int_2 %88
    %90 = OpLoad %float %89
    %91 = OpGroupFMaxNonUniformAMD %float %uint_3 Reduce %90
    %92 = OpFAdd %float %87 %91
    %93 = OpAccessChain %_ptr_StorageBuffer_float %_ %int_5 %83
    %94 = OpLoad %float %93
    %95 = OpFAdd %float %94 %92
    %96 = OpAccessChain %_ptr_StorageBuffer_float %_ %int_5 %83
    OpStore %96 %95
    OpBranch %21
    %21 = OpLabel
    %98 = OpLoad %uint %l
    %99 = OpLoad %uint %l
    %100 = OpAccessChain %_ptr_StorageBuffer_uint %_ %int_1 %99
    %101 = OpLoad %uint %100
    %103 = OpBitwiseAnd %uint %101 %uint_1
    %104 = OpGroupNonUniformPartitionEXT %v4uint %103
    %106 = OpAccessChain %_ptr_StorageBuffer_v4uint %_ %int_6 %98
    OpStore %106 %104
    OpReturn
    OpFunctionEnd
  """

  test("SPV_AMD_shader_ballot reduces and scans active lanes; partitions ballot lanes with equal values") {
    val memory = RegionMemory()
    val data   = memory.allocate(320L)
    writeWords(memory, data, 0, Vec(3L, -1L, 4L, -1L, 5L, -9L, 2L, 6L).map(_ & 0xffffffffL)*)
    writeWords(memory, data, 32, 10L, 20L, 5L, 7L, 30L, 1L, 9L, 9L)
    writeWords(memory, data, 64, Vec.tabulate(8)(index => single(index + 1f))*)
    assertEquals(run(module, "main", memory, Vec(buffer(data)), local = testDimensions(8)), Right(()))
    // the exclusive minimum scan starts from the largest int, which wraps when lane 0 adds to it
    assertEquals(
      words(memory, data, offset = 96, count = 8),
      Vec(-2147483643L, 9L, 5L, 5L, 5L, 5L, 0L, 0L).map(_ & 0xffffffffL)
    )
    assertEquals(words(memory, data, offset = 128, count = 8), Vec(31L, 31L, 31L, 31L, 31L, 31L, 0L, 0L))
    assertEquals(
      words(memory, data, offset = 160, count = 8),
      Vec(8f, 10f, 13f, 17f, 22f, 28f).map(single(_)) ++ Vec(0L, 0L)
    )
    val even = Vec(0x13L, 0L, 0L, 0L)
    val odd  = Vec(0xecL, 0L, 0L, 0L)
    assertEquals(
      Vec.tabulate(8)(lane => words(memory, data, 192 + lane * 16, 4)),
      Vec(even, even, odd, odd, even, odd, odd, odd)
    )
  }
}
