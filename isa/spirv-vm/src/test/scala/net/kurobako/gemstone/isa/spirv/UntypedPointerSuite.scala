package net.kurobako.gemstone.isa.spirv

import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.bits.*
import net.kurobako.gemstone.vm.*

class UntypedPointerSuite extends munit.FunSuite {

  // llvm-spirv --spirv-ext=+SPV_KHR_untyped_pointers of clang -x cl -cl-std=CL1.2 -target spir64 -O1 for a kernel that
  // fills a private array, indexes a structure array, exchanges through local memory across a barrier, adds
  // atomically and stores modf's whole part through a pointer
  private val kernel =
    spv"""
    ; Version: 1.4
    OpCapability Addresses
    OpCapability Linkage
    OpCapability Kernel
    OpCapability Int64
    OpCapability Int8
    OpCapability UntypedPointersKHR
    OpExtension "SPV_KHR_untyped_pointers"
    %1 = OpExtInstImport "OpenCL.std"
    OpMemoryModel Physical64 OpenCL
    OpEntryPoint Kernel %40 "untyped" %__spirv_BuiltInGlobalInvocationId %__spirv_BuiltInLocalInvocationId %__spirv_BuiltInWorkgroupSize
    OpDecorate %__spirv_BuiltInGlobalInvocationId LinkageAttributes "__spirv_BuiltInGlobalInvocationId" Import
    OpDecorate %__spirv_BuiltInGlobalInvocationId Constant
    OpDecorate %__spirv_BuiltInGlobalInvocationId BuiltIn GlobalInvocationId
    OpDecorate %__spirv_BuiltInLocalInvocationId LinkageAttributes "__spirv_BuiltInLocalInvocationId" Import
    OpDecorate %__spirv_BuiltInLocalInvocationId Constant
    OpDecorate %__spirv_BuiltInLocalInvocationId BuiltIn LocalInvocationId
    OpDecorate %__spirv_BuiltInWorkgroupSize LinkageAttributes "__spirv_BuiltInWorkgroupSize" Import
    OpDecorate %__spirv_BuiltInWorkgroupSize Constant
    OpDecorate %__spirv_BuiltInWorkgroupSize BuiltIn WorkgroupSize
    OpDecorate %fill LinkageAttributes "fill" Export
    OpDecorate %13 FuncParamAttr NoCapture
    OpDecorate %27 NoSignedWrap
    OpDecorate %27 NoUnsignedWrap
    OpDecorate %25 NoSignedWrap
    OpDecorate %25 NoUnsignedWrap
    OpDecorate %41 Alignment 4
    OpDecorate %42 FuncParamAttr NoCapture
    OpDecorate %42 Alignment 4
    OpDecorate %43 FuncParamAttr NoCapture
    OpDecorate %43 FuncParamAttr NoWrite
    OpDecorate %43 Alignment 4
    OpDecorate %44 FuncParamAttr NoCapture
    OpDecorate %44 Alignment 4
    OpDecorate %52 Alignment 4
    OpDecorate %53 Alignment 4
    OpDecorate %60 NoSignedWrap
    OpDecorate %60 NoUnsignedWrap
    OpDecorate %81 NoSignedWrap
    OpDecorate %93 NoSignedWrap
    OpDecorate %110 NoSignedWrap
    OpDecorate %__clang_ocl_kern_imp_untyped LinkageAttributes "__clang_ocl_kern_imp_untyped" Export
    OpDecorate %118 Alignment 4
    OpDecorate %119 FuncParamAttr NoCapture
    OpDecorate %119 Alignment 4
    OpDecorate %120 FuncParamAttr NoCapture
    OpDecorate %120 FuncParamAttr NoWrite
    OpDecorate %120 Alignment 4
    OpDecorate %121 FuncParamAttr NoCapture
    OpDecorate %121 Alignment 4
    OpDecorate %126 Alignment 4
    OpDecorate %127 Alignment 4
    OpDecorate %145 NoSignedWrap
    OpDecorate %153 NoSignedWrap
    OpDecorate %167 NoSignedWrap
    OpDecorate %173 NoSignedWrap
    OpDecorate %173 NoUnsignedWrap
    %ulong = OpTypeInt 64 0
    %uint = OpTypeInt 32 0
    %uchar = OpTypeInt 8 0
    %uint_0 = OpConstant %uint 0
    %ulong_0 = OpConstant %ulong 0
    %ulong_1 = OpConstant %ulong 1
    %ulong_3 = OpConstant %ulong 3
    %ulong_4 = OpConstant %ulong 4
    %uint_4 = OpConstant %uint 4
    %uint_2 = OpConstant %uint 2
    %uint_272 = OpConstant %uint 272
    %ulong_16 = OpConstant %ulong 16
    %uint_1 = OpConstant %uint 1
    %uint_512 = OpConstant %uint 512
    %ulong_2 = OpConstant %ulong 2
    %v3ulong = OpTypeVector %ulong 3
    %_ptr_Input_v3ulong = OpTypePointer Input %v3ulong
    %void = OpTypeVoid
    %_ptr_Function = OpTypeUntypedPointerKHR Function
    %11 = OpTypeFunction %void %_ptr_Function %uint
    %bool = OpTypeBool
    %_ptr_CrossWorkgroup = OpTypeUntypedPointerKHR CrossWorkgroup
    %float = OpTypeFloat 32
    %_arr_float_ulong_3 = OpTypeArray %float %ulong_3
    %struct_pair_t = OpTypeStruct %uint %_arr_float_ulong_3
    %_ptr_Workgroup = OpTypeUntypedPointerKHR Workgroup
    %39 = OpTypeFunction %void %_ptr_CrossWorkgroup %_ptr_CrossWorkgroup %_ptr_CrossWorkgroup %_ptr_Workgroup %uint
    %_arr_uint_ulong_4 = OpTypeArray %uint %ulong_4
    %_ptr_Function__arr_uint_ulong_4 = OpTypePointer Function %_arr_uint_ulong_4
    %__spirv_BuiltInGlobalInvocationId = OpVariable %_ptr_Input_v3ulong Input
    %__spirv_BuiltInLocalInvocationId = OpVariable %_ptr_Input_v3ulong Input
    %__spirv_BuiltInWorkgroupSize = OpVariable %_ptr_Input_v3ulong Input
    %true = OpConstantTrue %bool
    %float_10 = OpConstant %float 10
    %float_2 = OpConstant %float 2
    %fill = OpFunction %void None %11
    %13 = OpFunctionParameter %_ptr_Function
    %14 = OpFunctionParameter %uint
    %15 = OpLabel
    %22 = OpSGreaterThan %bool %14 %uint_0
    OpBranchConditional %22 %16 %19
    %16 = OpLabel
    %23 = OpUConvert %ulong %14
    OpBranch %17
    %17 = OpLabel
    %26 = OpPhi %ulong %ulong_0 %16 %25 %17
    %27 = OpIMul %ulong %26 %26
    %28 = OpUntypedInBoundsPtrAccessChainKHR %_ptr_Function %uint %13 %26
    %29 = OpUConvert %uint %27
    OpStore %28 %29 Aligned 4
    %25 = OpIAdd %ulong %26 %ulong_1
    %32 = OpIEqual %bool %25 %23
    OpLoopMerge %_loopexit %17 DontUnroll
    OpBranchConditional %32 %_loopexit %17
    %_loopexit = OpLabel
    OpBranch %19
    %19 = OpLabel
    OpReturn
    OpFunctionEnd
    %40 = OpFunction %void None %39
    %41 = OpFunctionParameter %_ptr_CrossWorkgroup
    %42 = OpFunctionParameter %_ptr_CrossWorkgroup
    %43 = OpFunctionParameter %_ptr_CrossWorkgroup
    %44 = OpFunctionParameter %_ptr_Workgroup
    %45 = OpFunctionParameter %uint
    %46 = OpLabel
    %52 = OpVariable %_ptr_Function__arr_uint_ulong_4 Function
    %53 = OpUntypedVariableKHR %_ptr_Function Function %float
    %54 = OpLoad %v3ulong %__spirv_BuiltInGlobalInvocationId Aligned 32
    %55 = OpCompositeExtract %ulong %54 0
    %57 = OpSelect %ulong %true %55 %ulong_0
    %59 = OpBitcast %_ptr_Function %52
    OpLifetimeStart %59 16
    OpBranch %47
    %47 = OpLabel
    %61 = OpPhi %ulong %ulong_0 %46 %60 %47
    %62 = OpIAdd %ulong %61 %57
    %63 = OpUntypedInBoundsPtrAccessChainKHR %_ptr_Function %_arr_uint_ulong_4 %52 %ulong_0 %61
    %64 = OpUConvert %uint %62
    OpStore %63 %64 Aligned 4
    %60 = OpIAdd %ulong %61 %ulong_1
    %66 = OpIEqual %bool %60 %ulong_4
    OpLoopMerge %48 %47 DontUnroll
    OpBranchConditional %66 %48 %47
    %48 = OpLabel
    %67 = OpUConvert %uint %57
    %68 = OpLoad %v3ulong %__spirv_BuiltInLocalInvocationId Aligned 32
    %69 = OpCompositeExtract %ulong %68 0
    %70 = OpSelect %ulong %true %69 %ulong_0
    %71 = OpUConvert %uint %70
    %73 = OpSRem %uint %67 %uint_4
    %74 = OpSConvert %ulong %73
    %75 = OpUntypedInBoundsPtrAccessChainKHR %_ptr_Function %_arr_uint_ulong_4 %52 %ulong_0 %74
    %76 = OpLoad %uint %75 Aligned 4
    %77 = OpSConvert %ulong %67
    %78 = OpUntypedInBoundsPtrAccessChainKHR %_ptr_CrossWorkgroup %struct_pair_t %43 %77
    %79 = OpBitcast %_ptr_CrossWorkgroup %78
    %80 = OpLoad %uint %79 Aligned 4
    %81 = OpIAdd %uint %80 %76
    %82 = OpSConvert %ulong %71
    %83 = OpUntypedInBoundsPtrAccessChainKHR %_ptr_Workgroup %uint %44 %82
    OpStore %83 %81 Aligned 4
    OpControlBarrier %uint_2 %uint_2 %uint_272
    %86 = OpBitcast %_ptr_Function %53
    OpLifetimeStart %86 4
    %87 = OpUntypedInBoundsPtrAccessChainKHR %_ptr_CrossWorkgroup %float %42 %77
    %88 = OpLoad %float %87 Aligned 4
    %89 = OpExtInst %float %1 modf %88 %53
    %91 = OpUntypedInBoundsPtrAccessChainKHR %_ptr_CrossWorkgroup %uint %41 %ulong_16
    %93 = OpIAdd %uint %71 %uint_1
    %94 = OpSConvert %ulong %93
    %95 = OpLoad %v3ulong %__spirv_BuiltInWorkgroupSize Aligned 32
    %96 = OpCompositeExtract %ulong %95 0
    %97 = OpSelect %ulong %true %96 %ulong_1
    %98 = OpUMod %ulong %94 %97
    %99 = OpUntypedInBoundsPtrAccessChainKHR %_ptr_Workgroup %uint %44 %98
    %100 = OpLoad %uint %99 Aligned 4
    %102 = OpAtomicIAdd %uint %91 %uint_2 %uint_512 %100
    %103 = OpLoad %uint %99 Aligned 4
    %105 = OpUntypedInBoundsPtrAccessChainKHR %_ptr_CrossWorkgroup %struct_pair_t %43 %77 %uint_1 %ulong_2
    %106 = OpLoad %float %105 Aligned 4
    %108 = OpFMul %float %106 %float_10
    %109 = OpConvertFToS %uint %108
    %110 = OpIAdd %uint %103 %109
    %111 = OpUntypedInBoundsPtrAccessChainKHR %_ptr_CrossWorkgroup %uint %41 %77
    OpStore %111 %110 Aligned 4
    %112 = OpLoad %float %53 Aligned 4
    %114 = OpExtInst %float %1 mad %112 %float_2 %89
    OpStore %87 %114 Aligned 4
    %115 = OpBitcast %_ptr_Function %53
    OpLifetimeStop %115 4
    %116 = OpBitcast %_ptr_Function %52
    OpLifetimeStop %116 16
    OpReturn
    OpFunctionEnd
    %__clang_ocl_kern_imp_untyped = OpFunction %void Inline %39
    %118 = OpFunctionParameter %_ptr_CrossWorkgroup
    %119 = OpFunctionParameter %_ptr_CrossWorkgroup
    %120 = OpFunctionParameter %_ptr_CrossWorkgroup
    %121 = OpFunctionParameter %_ptr_Workgroup
    %122 = OpFunctionParameter %uint
    %123 = OpLabel
    %126 = OpVariable %_ptr_Function__arr_uint_ulong_4 Function
    %127 = OpUntypedVariableKHR %_ptr_Function Function %float
    %128 = OpLoad %v3ulong %__spirv_BuiltInGlobalInvocationId Aligned 32
    %129 = OpCompositeExtract %ulong %128 0
    %130 = OpSelect %ulong %true %129 %ulong_0
    %131 = OpBitcast %_ptr_Function %126
    OpLifetimeStart %131 16
    OpBranch %124
    %124 = OpLabel
    %174 = OpPhi %ulong %ulong_0 %123 %173 %124
    %175 = OpIAdd %ulong %174 %130
    %176 = OpUntypedInBoundsPtrAccessChainKHR %_ptr_Function %_arr_uint_ulong_4 %126 %ulong_0 %174
    %177 = OpUConvert %uint %175
    OpStore %176 %177 Aligned 4
    %173 = OpIAdd %ulong %174 %ulong_1
    %179 = OpIEqual %bool %173 %ulong_4
    OpLoopMerge %125 %124 DontUnroll
    OpBranchConditional %179 %125 %124
    %125 = OpLabel
    %132 = OpUConvert %uint %130
    %133 = OpLoad %v3ulong %__spirv_BuiltInLocalInvocationId Aligned 32
    %134 = OpCompositeExtract %ulong %133 0
    %135 = OpSelect %ulong %true %134 %ulong_0
    %136 = OpUConvert %uint %135
    %137 = OpSRem %uint %132 %uint_4
    %138 = OpSConvert %ulong %137
    %139 = OpUntypedInBoundsPtrAccessChainKHR %_ptr_Function %_arr_uint_ulong_4 %126 %ulong_0 %138
    %140 = OpLoad %uint %139 Aligned 4
    %141 = OpSConvert %ulong %132
    %142 = OpUntypedInBoundsPtrAccessChainKHR %_ptr_CrossWorkgroup %struct_pair_t %120 %141
    %143 = OpBitcast %_ptr_CrossWorkgroup %142
    %144 = OpLoad %uint %143 Aligned 4
    %145 = OpIAdd %uint %144 %140
    %146 = OpSConvert %ulong %136
    %147 = OpUntypedInBoundsPtrAccessChainKHR %_ptr_Workgroup %uint %121 %146
    OpStore %147 %145 Aligned 4
    OpControlBarrier %uint_2 %uint_2 %uint_272
    %148 = OpBitcast %_ptr_Function %127
    OpLifetimeStart %148 4
    %149 = OpUntypedInBoundsPtrAccessChainKHR %_ptr_CrossWorkgroup %float %119 %141
    %150 = OpLoad %float %149 Aligned 4
    %151 = OpExtInst %float %1 modf %150 %127
    %152 = OpUntypedInBoundsPtrAccessChainKHR %_ptr_CrossWorkgroup %uint %118 %ulong_16
    %153 = OpIAdd %uint %136 %uint_1
    %154 = OpSConvert %ulong %153
    %155 = OpLoad %v3ulong %__spirv_BuiltInWorkgroupSize Aligned 32
    %156 = OpCompositeExtract %ulong %155 0
    %157 = OpSelect %ulong %true %156 %ulong_1
    %158 = OpUMod %ulong %154 %157
    %159 = OpUntypedInBoundsPtrAccessChainKHR %_ptr_Workgroup %uint %121 %158
    %160 = OpLoad %uint %159 Aligned 4
    %161 = OpAtomicIAdd %uint %152 %uint_2 %uint_512 %160
    %162 = OpLoad %uint %159 Aligned 4
    %163 = OpUntypedInBoundsPtrAccessChainKHR %_ptr_CrossWorkgroup %struct_pair_t %120 %141 %uint_1 %ulong_2
    %164 = OpLoad %float %163 Aligned 4
    %165 = OpFMul %float %164 %float_10
    %166 = OpConvertFToS %uint %165
    %167 = OpIAdd %uint %162 %166
    %168 = OpUntypedInBoundsPtrAccessChainKHR %_ptr_CrossWorkgroup %uint %118 %141
    OpStore %168 %167 Aligned 4
    %169 = OpLoad %float %127 Aligned 4
    %170 = OpExtInst %float %1 mad %169 %float_2 %151
    OpStore %149 %170 Aligned 4
    %171 = OpBitcast %_ptr_Function %127
    OpLifetimeStop %171 4
    %172 = OpBitcast %_ptr_Function %126
    OpLifetimeStop %172 16
    OpReturn
    OpFunctionEnd
  """

  test("llvm-spirv's untyped pointers address private, local, global and structure data by their base types") {
    val memory = RegionMemory()
    val out    = memory.allocate(68L)
    val floats = memory.allocate(16L)
    val pairs  = memory.allocate(64L)
    val inputs = Vec(1.5f, -2.25f, 3.75f, 0.5f)
    writeWords(memory, floats, 0, inputs.map(single(_))*)
    (0 until 4).foreach(index =>
      writeWords(
        memory,
        pairs,
        index * 16,
        10L * index,
        single(index.toFloat),
        single(index + 0.5f),
        single(index + 0.25f)
      )
    )
    assertEquals(
      run(
        kernel,
        "untyped",
        memory,
        Vec(buffer(out), buffer(floats), buffer(pairs), KernelTask.Argument.Local(16L), scalar(4L, 4)),
        local = testDimensions(4)
      ),
      Right(())
    )
    assertEquals(words(memory, out, offset = 0, count = 4), Vec(14L, 36L, 58L, 32L))
    assertEquals(words(memory, out, offset = 64, count = 1), Vec(72L))
    assertEquals(words(memory, floats, offset = 0, count = 4), Vec(2.5f, -4.25f, 6.75f, 0.5f).map(single(_)))
  }

  // llvm-spirv --spirv-ext=+SPV_INTEL_variable_length_array of LLVM IR that saves the stack, fills a variable-length
  // i32 alloca with squares, restores the stack and allocates again
  private val typed = spv"""
    ; Version: 1.0
    OpCapability Addresses
    OpCapability Kernel
    OpCapability Int64
    OpCapability Int8
    OpCapability VariableLengthArrayINTEL
    OpExtension "SPV_INTEL_variable_length_array"
    %1 = OpExtInstImport "OpenCL.std"
    OpMemoryModel Physical64 OpenCL
    OpEntryPoint Kernel %6 "vla"
    OpDecorate %vla Alignment 4
    OpDecorate %again Alignment 4
    %uint = OpTypeInt 32 0
    %uchar = OpTypeInt 8 0
    %ulong = OpTypeInt 64 0
    %ulong_0 = OpConstant %ulong 0
    %ulong_1 = OpConstant %ulong 1
    %ulong_3 = OpConstant %ulong 3
    %uint_99 = OpConstant %uint 99
    %void = OpTypeVoid
    %_ptr_CrossWorkgroup_uint = OpTypePointer CrossWorkgroup %uint
    %5 = OpTypeFunction %void %_ptr_CrossWorkgroup_uint %uint
    %_ptr_Function_uchar = OpTypePointer Function %uchar
    %_ptr_Function_uint = OpTypePointer Function %uint
    %bool = OpTypeBool
    %6 = OpFunction %void None %5
    %out = OpFunctionParameter %_ptr_CrossWorkgroup_uint
    %n = OpFunctionParameter %uint
    %entry = OpLabel
    %sp = OpSaveMemoryINTEL %_ptr_Function_uchar
    %count = OpUConvert %ulong %n
    %vla = OpVariableLengthArrayINTEL %_ptr_Function_uint %count
    OpBranch %loop
    %loop = OpLabel
    %i = OpPhi %ulong %ulong_0 %entry %next %loop
    %ii = OpUConvert %uint %i
    %sq = OpIMul %uint %ii %ii
    %slot = OpInBoundsPtrAccessChain %_ptr_Function_uint %vla %i
    OpStore %slot %sq Aligned 4
    %next = OpIAdd %ulong %i %ulong_1
    %more = OpULessThan %bool %next %count
    OpBranchConditional %more %loop %done
    %done = OpLabel
    %last = OpInBoundsPtrAccessChain %_ptr_Function_uint %vla %ulong_3
    %v = OpLoad %uint %last Aligned 4
    %first = OpLoad %uint %vla Aligned 4
    %sum = OpIAdd %uint %v %first
    OpRestoreMemoryINTEL %sp
    %again = OpVariableLengthArrayINTEL %_ptr_Function_uint %count
    OpStore %again %uint_99 Aligned 4
    %w = OpLoad %uint %again Aligned 4
    %total = OpIAdd %uint %sum %w
    OpStore %out %total Aligned 4
    OpReturn
    OpFunctionEnd
    """

  test("variable-length arrays allocate at run time and release to a saved stack position") {
    val memory = RegionMemory()
    val out    = memory.allocate(4L)
    assertEquals(run(typed, "vla", memory, Vec(buffer(out), scalar(5L, 4))), Right(()))
    assertEquals(words(memory, out, offset = 0, count = 1), Vec(108L))
  }

  // the same with +SPV_KHR_untyped_pointers, where llvm-spirv gives the arrays untyped pointers but no element type
  private val untyped = spv"""
    ; Version: 1.0
    OpCapability Addresses
    OpCapability Kernel
    OpCapability Int64
    OpCapability Int8
    OpCapability UntypedPointersKHR
    OpCapability VariableLengthArrayINTEL
    OpExtension "SPV_INTEL_variable_length_array"
    OpExtension "SPV_KHR_untyped_pointers"
    %1 = OpExtInstImport "OpenCL.std"
    OpMemoryModel Physical64 OpenCL
    OpEntryPoint Kernel %6 "vla"
    OpDecorate %vla Alignment 4
    OpDecorate %again Alignment 4
    %uint = OpTypeInt 32 0
    %uchar = OpTypeInt 8 0
    %ulong = OpTypeInt 64 0
    %ulong_0 = OpConstant %ulong 0
    %ulong_1 = OpConstant %ulong 1
    %ulong_3 = OpConstant %ulong 3
    %uint_99 = OpConstant %uint 99
    %void = OpTypeVoid
    %_ptr_CrossWorkgroup = OpTypeUntypedPointerKHR CrossWorkgroup
    %5 = OpTypeFunction %void %_ptr_CrossWorkgroup %uint
    %_ptr_Function = OpTypeUntypedPointerKHR Function
    %bool = OpTypeBool
    %6 = OpFunction %void None %5
    %out = OpFunctionParameter %_ptr_CrossWorkgroup
    %n = OpFunctionParameter %uint
    %entry = OpLabel
    %sp = OpSaveMemoryINTEL %_ptr_Function
    %count = OpUConvert %ulong %n
    %vla = OpVariableLengthArrayINTEL %_ptr_Function %count
    OpBranch %loop
    %loop = OpLabel
    %i = OpPhi %ulong %ulong_0 %entry %next %loop
    %ii = OpUConvert %uint %i
    %sq = OpIMul %uint %ii %ii
    %slot = OpUntypedInBoundsPtrAccessChainKHR %_ptr_Function %uint %vla %i
    OpStore %slot %sq Aligned 4
    %next = OpIAdd %ulong %i %ulong_1
    %more = OpULessThan %bool %next %count
    OpBranchConditional %more %loop %done
    %done = OpLabel
    %last = OpUntypedInBoundsPtrAccessChainKHR %_ptr_Function %uint %vla %ulong_3
    %v = OpLoad %uint %last Aligned 4
    %first = OpLoad %uint %vla Aligned 4
    %sum = OpIAdd %uint %v %first
    OpRestoreMemoryINTEL %sp
    %again = OpVariableLengthArrayINTEL %_ptr_Function %count
    OpStore %again %uint_99 Aligned 4
    %w = OpLoad %uint %again Aligned 4
    %total = OpIAdd %uint %sum %w
    OpStore %out %total Aligned 4
    OpReturn
    OpFunctionEnd
    """

  // that module as SPV_INTEL_variable_length_array specifies it, with the element type the alloca had
  private val untypedElements = spv"""
    ; Version: 1.0
    OpCapability Addresses
    OpCapability Kernel
    OpCapability Int64
    OpCapability Int8
    OpCapability UntypedPointersKHR
    OpCapability VariableLengthArrayINTEL
    OpCapability UntypedVariableLengthArrayINTEL
    OpExtension "SPV_INTEL_variable_length_array"
    OpExtension "SPV_KHR_untyped_pointers"
    %1 = OpExtInstImport "OpenCL.std"
    OpMemoryModel Physical64 OpenCL
    OpEntryPoint Kernel %6 "vla"
    OpDecorate %vla Alignment 4
    OpDecorate %again Alignment 4
    %uint = OpTypeInt 32 0
    %uchar = OpTypeInt 8 0
    %ulong = OpTypeInt 64 0
    %ulong_0 = OpConstant %ulong 0
    %ulong_1 = OpConstant %ulong 1
    %ulong_3 = OpConstant %ulong 3
    %uint_99 = OpConstant %uint 99
    %void = OpTypeVoid
    %_ptr_CrossWorkgroup = OpTypeUntypedPointerKHR CrossWorkgroup
    %5 = OpTypeFunction %void %_ptr_CrossWorkgroup %uint
    %_ptr_Function = OpTypeUntypedPointerKHR Function
    %bool = OpTypeBool
    %6 = OpFunction %void None %5
    %out = OpFunctionParameter %_ptr_CrossWorkgroup
    %n = OpFunctionParameter %uint
    %entry = OpLabel
    %sp = OpSaveMemoryINTEL %_ptr_Function
    %count = OpUConvert %ulong %n
    %vla = OpUntypedVariableLengthArrayINTEL %_ptr_Function %uint %count
    OpBranch %loop
    %loop = OpLabel
    %i = OpPhi %ulong %ulong_0 %entry %next %loop
    %ii = OpUConvert %uint %i
    %sq = OpIMul %uint %ii %ii
    %slot = OpUntypedInBoundsPtrAccessChainKHR %_ptr_Function %uint %vla %i
    OpStore %slot %sq Aligned 4
    %next = OpIAdd %ulong %i %ulong_1
    %more = OpULessThan %bool %next %count
    OpBranchConditional %more %loop %done
    %done = OpLabel
    %last = OpUntypedInBoundsPtrAccessChainKHR %_ptr_Function %uint %vla %ulong_3
    %v = OpLoad %uint %last Aligned 4
    %first = OpLoad %uint %vla Aligned 4
    %sum = OpIAdd %uint %v %first
    OpRestoreMemoryINTEL %sp
    %again = OpUntypedVariableLengthArrayINTEL %_ptr_Function %uint %count
    OpStore %again %uint_99 Aligned 4
    %w = OpLoad %uint %again Aligned 4
    %total = OpIAdd %uint %sum %w
    OpStore %out %total Aligned 4
    OpReturn
    OpFunctionEnd
    """

  test("untyped variable-length arrays take their element type from OpUntypedVariableLengthArrayINTEL") {
    val memory = RegionMemory()
    val out    = memory.allocate(4L)
    assertEquals(run(untypedElements, "vla", memory, Vec(buffer(out), scalar(5L, 4))), Right(()))
    assertEquals(words(memory, out, offset = 0, count = 1), Vec(108L))
    val result = run(untyped, "vla", memory, Vec(buffer(out), scalar(5L, 4)))
    assert(result.left.exists(_.message.raw.contains("has no element type")), result)
  }
}
