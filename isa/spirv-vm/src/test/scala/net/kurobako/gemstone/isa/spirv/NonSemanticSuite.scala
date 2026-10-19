package net.kurobako.gemstone.isa.spirv

import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.testing.*

class NonSemanticSuite extends munit.FunSuite {

  // clang -x cl -cl-std=CL1.2 -target spir64 -emit-llvm -fsigned-zeros -fno-slp-vectorize -g -c debug.cl, then
  // llvm-spirv --spirv-ext=+SPV_KHR_subgroup_rotate,+SPV_KHR_bit_instructions,+SPV_INTEL_subgroups,+SPV_KHR_linkonce_odr
  // (oneAPI 2026.1.0, LLVM 22.1.0) and spirv-dis, with OpenCL.DebugInfo.100 instructions and enumerants as numbers, of
  //   kernel void saturate(global int *a, global uint *b) {
  //     size_t i = get_global_id(0);
  //     b[i] = convert_uint_sat(a[i]);
  //   }
  private val debugged = """
    OpCapability Addresses
    OpCapability Linkage
    OpCapability Kernel
    OpCapability Int64
    %2 = OpExtInstImport "OpenCL.DebugInfo.100"
    %1 = OpExtInstImport "OpenCL.std"
    OpMemoryModel Physical64 OpenCL
    OpEntryPoint Kernel %11 "saturate" %__spirv_BuiltInGlobalInvocationId
    %45 = OpString "debug.cl"
    %48 = OpString "int"
    %52 = OpString "uint"
    %53 = OpString "unsigned int"
    %55 = OpString "opencl-c-base.h"
    %60 = OpString "size_t"
    %61 = OpString "unsigned long"
    %65 = OpString "saturate"
    %66 = OpString ""
    %68 = OpString "a"
    %70 = OpString "b"
    %72 = OpString "__clang_ocl_kern_imp_saturate"
    %76 = OpString "i"
    OpSource OpenCL_C 102000
    OpName %__spirv_BuiltInGlobalInvocationId "__spirv_BuiltInGlobalInvocationId"
    OpName %__clang_ocl_kern_imp_saturate "__clang_ocl_kern_imp_saturate"
    OpModuleProcessed "Debug info producer: clang based Intel(R) oneAPI DPC++/C++ Compiler 2026.1.0 (2026.1.0.20260617)"
    OpDecorate %__spirv_BuiltInGlobalInvocationId LinkageAttributes "__spirv_BuiltInGlobalInvocationId" Import
    OpDecorate %__spirv_BuiltInGlobalInvocationId Constant
    OpDecorate %__spirv_BuiltInGlobalInvocationId BuiltIn GlobalInvocationId
    OpDecorate %12 FuncParamAttr NoCapture
    OpDecorate %12 FuncParamAttr NoWrite
    OpDecorate %12 Alignment 4
    OpDecorate %13 FuncParamAttr NoCapture
    OpDecorate %13 Alignment 4
    OpDecorate %__clang_ocl_kern_imp_saturate LinkageAttributes "__clang_ocl_kern_imp_saturate" Export
    OpDecorate %32 FuncParamAttr NoCapture
    OpDecorate %32 FuncParamAttr NoWrite
    OpDecorate %32 Alignment 4
    OpDecorate %33 FuncParamAttr NoCapture
    OpDecorate %33 Alignment 4
    %ulong = OpTypeInt 64 0
    %uint = OpTypeInt 32 0
    %ulong_0 = OpConstant %ulong 0
    %uint_32 = OpConstant %uint 32
    %uint_64 = OpConstant %uint 64
    %v3ulong = OpTypeVector %ulong 3
    %_ptr_Input_v3ulong = OpTypePointer Input %v3ulong
    %void = OpTypeVoid
    %_ptr_CrossWorkgroup_uint = OpTypePointer CrossWorkgroup %uint
    %10 = OpTypeFunction %void %_ptr_CrossWorkgroup_uint %_ptr_CrossWorkgroup_uint
    %bool = OpTypeBool
    %__spirv_BuiltInGlobalInvocationId = OpVariable %_ptr_Input_v3ulong Input
    %true = OpConstantTrue %bool
    %15 = OpExtInst %void %2 0
    %46 = OpExtInst %void %2 35 %45
    %47 = OpExtInst %void %2 1 65536 4 %46 3
    %50 = OpExtInst %void %2 2 %48 %uint_32 4
    %51 = OpExtInst %void %2 3 %50 5 0
    %54 = OpExtInst %void %2 2 %53 %uint_32 6
    %56 = OpExtInst %void %2 35 %55
    %57 = OpExtInst %void %2 7 %52 %54 %56 0 0 %47
    %58 = OpExtInst %void %2 3 %57 5 0
    %59 = OpExtInst %void %2 8 0 %15 %51 %58
    %63 = OpExtInst %void %2 2 %61 %uint_64 6
    %64 = OpExtInst %void %2 7 %60 %63 %56 0 0 %47
    %67 = OpExtInst %void %2 20 %65 %59 %46 1 0 %47 %66 8328 1 %11
    %69 = OpExtInst %void %2 26 %68 %51 %46 1 0 %67 0 1
    %71 = OpExtInst %void %2 26 %70 %58 %46 1 0 %67 0 2
    %73 = OpExtInst %void %2 20 %65 %59 %46 1 0 %47 %72 8328 1 %__clang_ocl_kern_imp_saturate
    %74 = OpExtInst %void %2 26 %68 %51 %46 1 0 %73 0 1
    %75 = OpExtInst %void %2 26 %70 %58 %46 1 0 %73 0 2
    %77 = OpExtInst %void %2 26 %76 %64 %46 2 0 %73 0
    %78 = OpExtInst %void %2 30 8 0
    %79 = OpExtInst %void %2 30 5
    %80 = OpExtInst %void %2 30 6
    %81 = OpExtInst %void %2 31 %78 %79 %80
    %83 = OpExtInst %void %2 25 1 %67
    %11 = OpFunction %void None %10
    %12 = OpFunctionParameter %_ptr_CrossWorkgroup_uint
    %13 = OpFunctionParameter %_ptr_CrossWorkgroup_uint
    %14 = OpLabel
    %82 = OpExtInst %void %2 23 %67
    OpLine %45 0 0
    %16 = OpExtInst %void %2 29 %69 %12 %81
    %17 = OpExtInst %void %2 29 %71 %13 %81
    %84 = OpExtInst %void %2 23 %73 %83
    %18 = OpExtInst %void %2 29 %74 %12 %81
    %19 = OpExtInst %void %2 29 %75 %13 %81
    OpLine %45 2 14
    %20 = OpLoad %v3ulong %__spirv_BuiltInGlobalInvocationId Aligned 32
    %21 = OpCompositeExtract %ulong %20 0
    %25 = OpSelect %ulong %true %21 %ulong_0
    OpLine %45 0 0
    %26 = OpExtInst %void %2 29 %77 %25 %81
    OpLine %45 3 27
    %27 = OpInBoundsPtrAccessChain %_ptr_CrossWorkgroup_uint %12 %25
    %28 = OpLoad %uint %27 Aligned 4
    OpLine %45 3 10
    %29 = OpSatConvertSToU %uint %28
    OpLine %45 3 3
    %30 = OpInBoundsPtrAccessChain %_ptr_CrossWorkgroup_uint %13 %25
    OpLine %45 3 8
    OpStore %30 %29 Aligned 4
    %85 = OpExtInst %void %2 23 %67
    OpLine %45 4 1
    OpReturn
    OpFunctionEnd
    %__clang_ocl_kern_imp_saturate = OpFunction %void Inline %10
    %32 = OpFunctionParameter %_ptr_CrossWorkgroup_uint
    %33 = OpFunctionParameter %_ptr_CrossWorkgroup_uint
    %34 = OpLabel
    %86 = OpExtInst %void %2 23 %73
    OpLine %45 0 0
    %35 = OpExtInst %void %2 29 %74 %32 %81
    %36 = OpExtInst %void %2 29 %75 %33 %81
    OpLine %45 2 14
    %37 = OpLoad %v3ulong %__spirv_BuiltInGlobalInvocationId Aligned 32
    %38 = OpCompositeExtract %ulong %37 0
    %39 = OpSelect %ulong %true %38 %ulong_0
    OpLine %45 0 0
    %40 = OpExtInst %void %2 29 %77 %39 %81
    OpLine %45 3 27
    %41 = OpInBoundsPtrAccessChain %_ptr_CrossWorkgroup_uint %32 %39
    %42 = OpLoad %uint %41 Aligned 4
    OpLine %45 3 10
    %43 = OpSatConvertSToU %uint %42
    OpLine %45 3 3
    %44 = OpInBoundsPtrAccessChain %_ptr_CrossWorkgroup_uint %33 %39
    OpLine %45 3 8
    OpStore %44 %43 Aligned 4
    OpLine %45 4 1
    OpReturn
    OpFunctionEnd
  """

  test("a kernel built with -g ignores its OpenCL.DebugInfo.100 instructions") {
    val inputs = Seq(-5L, 7L, 0x80000000L, 0x7fffffffL)
    val memory = RegionMemory()
    val a      = memory.allocate(littleEndian(inputs, 32))
    val b      = memory.allocate(Vec.fill(16)(0x55.toByte))
    assertEquals(
      run(assemble(debugged), "saturate", memory, Vec(buffer(a), buffer(b)), local = testDimensions(4)),
      Right(())
    )
    assertEquals(words(memory, b, offset = 0, count = 4), Vec(0L, 7L, 0L, 0x7fffffffL))
  }

  // SPIR-V 3.3.4 OpExtInstImport: every OpExtInst of a set named NonSemantic.* can be ignored
  test("OpExtInst of a NonSemantic set has no effect wherever it appears") {
    val module = spv"""
      $openCL
      %info = OpExtInstImport "NonSemantic.Gemstone.Test"
      OpEntryPoint Kernel %main "main"
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %ptr = OpTypePointer CrossWorkgroup %uint
      %fn = OpTypeFunction %void %ptr
      %seven = OpConstant %uint 7
      %global = OpExtInst %void %info 1 %seven
      %main = OpFunction %void None %fn
      %out = OpFunctionParameter %ptr
      %entry = OpLabel
      %local = OpExtInst %void %info 2 %global %out
      OpStore %out %seven
      %after = OpExtInst %uint %info 3 %local %seven
      OpReturn
      OpFunctionEnd
    """
    val memory = RegionMemory()
    val out    = memory.allocate(4L)
    assertEquals(run(module, "main", memory, Vec(buffer(out))), Right(()))
    assertEquals(word(memory, out, 0), 7L)
  }

  // SPV_KHR_relaxed_extended_instruction: glslang -gV emits OpExtInstWithForwardRefsKHR for the debug type of a
  // self-referential buffer reference; only non-semantic sets may use it
  private def forward(set: String): Module = spv"""
    $openCL
    %info = OpExtInstImport "$set"
    OpEntryPoint Kernel %main "main"
    %void = OpTypeVoid
    %uint = OpTypeInt 32 0
    %ptr = OpTypePointer CrossWorkgroup %uint
    %fn = OpTypeFunction %void %ptr
    %seven = OpConstant %uint 7
    %early = OpExtInstWithForwardRefsKHR %void %info 1 %late
    %late = OpExtInst %void %info 2 %seven
    %main = OpFunction %void None %fn
    %out = OpFunctionParameter %ptr
    %entry = OpLabel
    %local = OpExtInstWithForwardRefsKHR %void %info 3 %after %early
    OpStore %out %seven
    %after = OpExtInst %void %info 4 %local
    OpReturn
    OpFunctionEnd
  """

  test("OpExtInstWithForwardRefsKHR of a NonSemantic set has no effect") {
    val memory = RegionMemory()
    val out    = memory.allocate(4L)
    assertEquals(run(forward("NonSemantic.Shader.DebugInfo.100"), "main", memory, Vec(buffer(out))), Right(()))
    assertEquals(word(memory, out, 0), 7L)
  }

  test("OpExtInstWithForwardRefsKHR of a semantic set faults") {
    val memory = RegionMemory()
    val out    = memory.allocate(4L)
    assert(run(forward("OpenCL.std"), "main", memory, Vec(buffer(out))).isLeft)
  }
}
