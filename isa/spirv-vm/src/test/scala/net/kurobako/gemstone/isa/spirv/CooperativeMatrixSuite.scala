package net.kurobako.gemstone.isa.spirv

import fixtures.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.bits.*
import net.kurobako.gemstone.vm.*
import scala.reflect.ClassTag

class CooperativeMatrixSuite extends Suite {

  // icpx 2026.1 -fsycl -O2 of a joint_matrix kernel on a 16-lane sub-group: an 8x16 float accumulator filled with 1,
  // bfloat16 8x16 A and 16x16 B loaded row-major with stride 16, joint_matrix_mad, joint_matrix_apply doubling every
  // element, and a row-major store with stride 16
  private val jointMatrix =
    spv"""
    ; Version: 1.4
    OpCapability Addresses
    OpCapability Kernel
    OpCapability Int64
    OpCapability Int16
    OpCapability GenericPointer
    OpCapability Int8
    OpCapability SubgroupDispatch
    OpCapability FPFastMathModeINTEL
    OpCapability CooperativeMatrixKHR
    OpExtension "SPV_INTEL_fp_fast_math_mode"
    OpExtension "SPV_KHR_cooperative_matrix"
    %1 = OpExtInstImport "OpenCL.std"
    OpMemoryModel Physical64 OpenCL
    OpEntryPoint Kernel %6 "_ZTSZ4mainEUlN4sycl3_V17nd_itemILi2EEEE_"
    OpExecutionMode %6 SubgroupSize 16
    OpDecorate %7 Alignment 2
    OpDecorate %8 Alignment 2
    OpDecorate %9 Alignment 4
    OpDecorate %22 Alignment 8
    OpDecorate %52 NoSignedWrap
    OpDecorate %52 NoUnsignedWrap
    %uchar = OpTypeInt 8 0
    %uint = OpTypeInt 32 0
    %ulong = OpTypeInt 64 0
    %ushort = OpTypeInt 16 0
    %uint_3 = OpConstant %uint 3
    %uint_8 = OpConstant %uint 8
    %uint_16 = OpConstant %uint 16
    %uint_2 = OpConstant %uint 2
    %ulong_0 = OpConstant %ulong 0
    %uint_0 = OpConstant %uint 0
    %ulong_16 = OpConstant %ulong 16
    %uint_1 = OpConstant %uint 1
    %void = OpTypeVoid
    %_ptr_CrossWorkgroup_uchar = OpTypePointer CrossWorkgroup %uchar
    %5 = OpTypeFunction %void %_ptr_CrossWorkgroup_uchar %_ptr_CrossWorkgroup_uchar %_ptr_CrossWorkgroup_uchar
    %float = OpTypeFloat 32
    %20 = OpTypeCooperativeMatrixKHR %float %uint_3 %uint_8 %uint_16 %uint_2
    %_ptr_Function_20 = OpTypePointer Function %20
    %_ptr_Generic_uchar = OpTypePointer Generic %uchar
    %_ptr_Function_uchar = OpTypePointer Function %uchar
    %joint_matrix = OpTypeStruct %20
    %_ptr_Function_struct_sycl___V1__ext__oneapi__experimental__matrix__joint_matrix_1 = OpTypePointer Function %joint_matrix
    %_ptr_Generic_20 = OpTypePointer Generic %20
    %42 = OpTypeCooperativeMatrixKHR %ushort %uint_3 %uint_8 %uint_16 %uint_0
    %47 = OpTypeCooperativeMatrixKHR %ushort %uint_3 %uint_16 %uint_16 %uint_1
    %bool = OpTypeBool
    %_ptr_Generic_float = OpTypePointer Generic %float
    %_ptr_CrossWorkgroup_float = OpTypePointer CrossWorkgroup %float
    %float_1 = OpConstant %float 1
    %float_2 = OpConstant %float 2
    %6 = OpFunction %void None %5
    %7 = OpFunctionParameter %_ptr_CrossWorkgroup_uchar
    %8 = OpFunctionParameter %_ptr_CrossWorkgroup_uchar
    %9 = OpFunctionParameter %_ptr_CrossWorkgroup_uchar
    %10 = OpLabel
    %22 = OpVariable %_ptr_Function_20 Function
    %24 = OpPtrCastToGeneric %_ptr_Generic_uchar %7
    %25 = OpPtrCastToGeneric %_ptr_Generic_uchar %8
    %27 = OpBitcast %_ptr_Function_uchar %22
    OpLifetimeStart %27 8
    %29 = OpCompositeConstruct %20 %float_1
    %32 = OpBitcast %_ptr_Function_struct_sycl___V1__ext__oneapi__experimental__matrix__joint_matrix_1 %22
    %36 = OpInBoundsPtrAccessChain %_ptr_Function_20 %32 %ulong_0 %uint_0
    %38 = OpPtrCastToGeneric %_ptr_Generic_20 %36
    %39 = OpPtrCastToGeneric %_ptr_Generic_20 %36
    %40 = OpGenericCastToPtrExplicit %_ptr_CrossWorkgroup_uchar %24 CrossWorkgroup
    %44 = OpCooperativeMatrixLoadKHR %42 %40 %uint_0 %ulong_16 None
    %45 = OpGenericCastToPtrExplicit %_ptr_CrossWorkgroup_uchar %25 CrossWorkgroup
    %48 = OpCooperativeMatrixLoadKHR %47 %45 %uint_0 %ulong_16 None
    %49 = OpCooperativeMatrixMulAddKHR %20 %44 %48 %29 MatrixAAndBBFloat16ComponentsINTEL
    OpStore %36 %49 Aligned 8
    OpBranch %11
    %11 = OpLabel
    %51 = OpPhi %20 %49 %10 %50 %12
    %53 = OpPhi %uint %uint_0 %10 %52 %12
    %54 = OpSConvert %ulong %53
    %55 = OpCooperativeMatrixLengthKHR %ulong %51
    %57 = OpUGreaterThan %bool %55 %54
    OpBranchConditional %57 %12 %13
    %12 = OpLabel
    %59 = OpAccessChain %_ptr_Generic_float %38 %54
    %60 = OpLoad %float %59 Aligned 4
    %62 = OpFMul %float %60 %float_2
    %63 = OpAccessChain %_ptr_Generic_float %39 %54
    OpStore %63 %62 Aligned 4
    %52 = OpIAdd %uint %53 %uint_1
    %50 = OpLoad %20 %36 Aligned 8
    OpBranch %11
    %13 = OpLabel
    %66 = OpPtrCastToGeneric %_ptr_Generic_uchar %9
    %67 = OpGenericCastToPtrExplicit %_ptr_CrossWorkgroup_uchar %66 CrossWorkgroup
    %68 = OpLoad %20 %36 Aligned 8
    %70 = OpBitcast %_ptr_CrossWorkgroup_float %67
    OpCooperativeMatrixStoreKHR %70 %68 %uint_0 %ulong_16 None
    %71 = OpBitcast %_ptr_Function_uchar %22
    OpLifetimeStop %71 8
    OpReturn
    OpFunctionEnd
  """

  private inline def grid[A: ClassTag](
      rows: Int,
      columns: Int
  )(inline value: (Int, Int) => A): Vec[Vec[A]] =
    Vec.tabulate(rows)(row => Vec.tabulate(columns)(column => value(row, column)))

  test("a SYCL joint_matrix multiply-accumulate of bfloat16 matrices loads, multiplies, scales and stores") {
    val memory = RegionMemory()
    val a      = memory.allocate(8L * 16 * 2)
    val b      = memory.allocate(16L * 16 * 2)
    val c      = memory.allocate(8L * 16 * 4)
    val left   = grid(rows = 8, columns = 16)((i, k) => ((i + 2 * k) % 5 - 2).toFloat)
    val right  = grid(rows = 16, columns = 16)((k, j) => ((3 * k + j) % 7 - 3).toFloat / 2)
    left.flatten.zipWithIndex.foreach((value, at) => LittleEndian.write(bfloat16(value), memory.array(a), at * 2, 2))
    right.flatten.zipWithIndex.foreach((value, at) => LittleEndian.write(bfloat16(value), memory.array(b), at * 2, 2))
    assertEquals(
      run(
        jointMatrix,
        "_ZTSZ4mainEUlN4sycl3_V17nd_itemILi2EEEE_",
        memory,
        Vec(buffer(a), buffer(b), buffer(c)),
        local = testDimensions(16)
      ),
      Right(())
    )
    val expected =
      grid(rows = 8, columns = 16)((i, j) => single(2 * (1 + (0 until 16).map(k => left(i)(k) * right(k)(j)).sum)))
    assertEquals(words(memory, c, offset = 0, count = 128), expected.flatten)
  }

  private val integers = spv"""
    OpCapability CooperativeMatrixKHR
    OpExtension "SPV_KHR_cooperative_matrix"
    $openCL
    OpEntryPoint Kernel %main "main"
    %void = OpTypeVoid
    %uchar = OpTypeInt 8 0
    %uint = OpTypeInt 32 0
    %ulong = OpTypeInt 64 0
    %scope = OpConstant %uint 3
    %two = OpConstant %uint 2
    %three = OpConstant %uint 3
    %use_a = OpConstant %uint 0
    %use_b = OpConstant %uint 1
    %use_c = OpConstant %uint 2
    %row_major = OpConstant %uint 0
    %column_major = OpConstant %uint 1
    %stride_a = OpConstant %uint 3
    %stride_b = OpConstant %uint 4
    %stride_c = OpConstant %uint 2
    %a_type = OpTypeCooperativeMatrixKHR %uchar %scope %two %three %use_a
    %b_type = OpTypeCooperativeMatrixKHR %uchar %scope %three %two %use_b
    %c_type = OpTypeCooperativeMatrixKHR %uint %scope %two %two %use_c
    %uchar_ptr = OpTypePointer CrossWorkgroup %uchar
    %uint_ptr = OpTypePointer CrossWorkgroup %uint
    %fn = OpTypeFunction %void %uchar_ptr %uchar_ptr %uint_ptr %uint_ptr %uint_ptr
    %near_max = OpConstant %uint 0x7ffffff0
    %main = OpFunction %void None %fn
    %a_in = OpFunctionParameter %uchar_ptr
    %b_in = OpFunctionParameter %uchar_ptr
    %wrapped_out = OpFunctionParameter %uint_ptr
    %saturated_out = OpFunctionParameter %uint_ptr
    %length_out = OpFunctionParameter %uint_ptr
    %entry = OpLabel
    %a = OpCooperativeMatrixLoadKHR %a_type %a_in %row_major %stride_a
    %b = OpCooperativeMatrixLoadKHR %b_type %b_in %column_major %stride_b
    %c = OpCompositeConstruct %c_type %near_max
    %unsigned = OpCooperativeMatrixMulAddKHR %c_type %a %b %c
    OpCooperativeMatrixStoreKHR %wrapped_out %unsigned %row_major %stride_c
    %saturated = OpCooperativeMatrixMulAddKHR %c_type %a %b %c MatrixASignedComponentsKHR|MatrixBSignedComponentsKHR|MatrixCSignedComponentsKHR|MatrixResultSignedComponentsKHR|SaturatingAccumulationKHR
    OpCooperativeMatrixStoreKHR %saturated_out %saturated %column_major %stride_c
    %length = OpCooperativeMatrixLengthKHR %uint %c_type
    OpStore %length_out %length
    OpReturn
    OpFunctionEnd
  """

  test("integer multiply-accumulate extends by its flags, wraps or saturates, and loads and stores by layout") {
    val memory    = RegionMemory()
    val a         = memory.allocate(6L)
    val b         = memory.allocate(8L)
    val wrapped   = memory.allocate(16L)
    val saturated = memory.allocate(16L)
    val length    = memory.allocate(4L)
    // A is 2x3 row-major; B is 3x2 column-major with columns 4 bytes apart
    val left  = Vec(Vec(1, -2, 3), Vec(-128, 127, 5))
    val right = Vec(Vec(2, -1), Vec(-3, 4), Vec(100, -100))
    left.flatten.zipWithIndex.foreach((value, at) => memory.array(a)(at) = value.toByte)
    (0 until 3).foreach(k => (0 until 2).foreach(j => memory.array(b)(j * 4 + k) = right(k)(j).toByte))
    assertEquals(
      run(integers, "main", memory, Vec(buffer(a), buffer(b), buffer(wrapped), buffer(saturated), buffer(length))),
      Right(())
    )
    def unsigned(value: Int): Long = value & 0xffL
    val products     = grid(rows = 2, columns = 2)((i, j) => (0 until 3).map(k => left(i)(k).toLong * right(k)(j)).sum)
    val zeroExtended =
      grid(rows = 2, columns = 2)((i, j) => (0 until 3).map(k => unsigned(left(i)(k)) * unsigned(right(k)(j))).sum)
    assertEquals(
      words(memory, wrapped, offset = 0, count = 4),
      zeroExtended.flatten.map(sum => (sum + 0x7ffffff0L) & 0xffffffffL)
    )
    val clamped = products.map(_.map(sum => math.min(sum + 0x7ffffff0L, Int.MaxValue.toLong) & 0xffffffffL))
    assertEquals(
      words(memory, saturated, offset = 0, count = 4),
      Vec(clamped(0)(0), clamped(1)(0), clamped(0)(1), clamped(1)(1))
    )
    assertEquals(words(memory, length, offset = 0, count = 1), Vec(4L))
  }

  private def pitched(strideLoad: String, strideStore: String) = spv"""
    OpCapability CooperativeMatrixKHR
    OpCapability Int64
    OpExtension "SPV_KHR_cooperative_matrix"
    $openCL
    OpEntryPoint Kernel %main "main"
    %void = OpTypeVoid
    %uint = OpTypeInt 32 0
    %ulong = OpTypeInt 64 0
    %scope = OpConstant %uint 3
    %two = OpConstant %uint 2
    %use_c = OpConstant %uint 2
    %row_major = OpConstant %uint 0
    %huge_load = OpConstant %ulong $strideLoad
    %huge_store = OpConstant %ulong $strideStore
    %c_type = OpTypeCooperativeMatrixKHR %uint %scope %two %two %use_c
    %uint_ptr = OpTypePointer CrossWorkgroup %uint
    %fn = OpTypeFunction %void %uint_ptr %uint_ptr
    %main = OpFunction %void None %fn
    %in = OpFunctionParameter %uint_ptr
    %out = OpFunctionParameter %uint_ptr
    %entry = OpLabel
    %loaded = OpCooperativeMatrixLoadKHR %c_type %in %row_major %huge_load
    OpCooperativeMatrixStoreKHR %out %loaded %row_major %huge_store
    OpReturn
    OpFunctionEnd
  """

  for {
    (name, stride) <-
      Vec("an overflowing product" -> "2305843009213693952", "a stride with the top bit set" -> "18446744073709551615")
    (access, load, store) <- Vec(("load", stride, "2"), ("store", "2", stride))
  } test(s"a cooperative matrix $access through $name faults rather than skipping components") {
    val memory = RegionMemory()
    val input  = memory.allocate(16L)
    val output = memory.allocate(16L)
    writeWords(memory, input, 0, 1L, 2L, 3L, 4L)
    assert(run(pitched(load, store), "main", memory, Vec(buffer(input), buffer(output))).isLeft)
  }

  // glslangValidator -V --target-env vulkan1.1 of a GL_NV_cooperative_matrix shader: half 16x16 A loaded row-major and
  // B column-major with stride 16, a float accumulator of 0.5, coopMatMulAddNV, every element tripled through
  // length() and indexing, and a row-major store
  private val nvidia = spv"""
    ; Version: 1.3
    OpCapability Shader
    OpCapability Float16
    OpCapability StorageBuffer16BitAccess
    OpCapability VulkanMemoryModel
    OpCapability CooperativeMatrixNV
    OpExtension "SPV_KHR_vulkan_memory_model"
    OpExtension "SPV_NV_cooperative_matrix"
    %1 = OpExtInstImport "GLSL.std.450"
    OpMemoryModel Logical Vulkan
    OpEntryPoint GLCompute %main "main"
    OpExecutionMode %main LocalSize 32 1 1
    OpDecorate %_runtimearr_half ArrayStride 2
    OpDecorate %A Block
    OpMemberDecorate %A 0 Offset 0
    OpDecorate %_ Binding 0
    OpDecorate %_ DescriptorSet 0
    OpDecorate %_runtimearr_half_0 ArrayStride 2
    OpDecorate %B Block
    OpMemberDecorate %B 0 Offset 0
    OpDecorate %__0 Binding 1
    OpDecorate %__0 DescriptorSet 0
    OpDecorate %_runtimearr_float ArrayStride 4
    OpDecorate %C Block
    OpMemberDecorate %C 0 Offset 0
    OpDecorate %__1 Binding 2
    OpDecorate %__1 DescriptorSet 0
    OpDecorate %gl_WorkGroupSize BuiltIn WorkgroupSize
    %void = OpTypeVoid
    %3 = OpTypeFunction %void
    %float = OpTypeFloat 32
    %uint = OpTypeInt 32 0
    %uint_3 = OpConstant %uint 3
    %uint_16 = OpConstant %uint 16
    %10 = OpTypeCooperativeMatrixNV %float %uint_3 %uint_16 %uint_16
    %_ptr_Function_10 = OpTypePointer Function %10
    %float_0_5 = OpConstant %float 0.5
    %14 = OpConstantComposite %10 %float_0_5
    %half = OpTypeFloat 16
    %16 = OpTypeCooperativeMatrixNV %half %uint_3 %uint_16 %uint_16
    %_ptr_Function_16 = OpTypePointer Function %16
    %_runtimearr_half = OpTypeRuntimeArray %half
    %A = OpTypeStruct %_runtimearr_half
    %_ptr_StorageBuffer_A = OpTypePointer StorageBuffer %A
    %_ = OpVariable %_ptr_StorageBuffer_A StorageBuffer
    %int = OpTypeInt 32 1
    %int_0 = OpConstant %int 0
    %uint_0 = OpConstant %uint 0
    %_ptr_StorageBuffer_half = OpTypePointer StorageBuffer %half
    %bool = OpTypeBool
    %false = OpConstantFalse %bool
    %_runtimearr_half_0 = OpTypeRuntimeArray %half
    %B = OpTypeStruct %_runtimearr_half_0
    %_ptr_StorageBuffer_B = OpTypePointer StorageBuffer %B
    %__0 = OpVariable %_ptr_StorageBuffer_B StorageBuffer
    %true = OpConstantTrue %bool
    %_ptr_Function_int = OpTypePointer Function %int
    %51 = OpSpecConstantOp %uint CooperativeMatrixLengthNV %10
    %52 = OpSpecConstantOp %int IAdd %51 %int_0
    %_ptr_Function_float = OpTypePointer Function %float
    %float_3 = OpConstant %float 3
    %int_1 = OpConstant %int 1
    %_runtimearr_float = OpTypeRuntimeArray %float
    %C = OpTypeStruct %_runtimearr_float
    %_ptr_StorageBuffer_C = OpTypePointer StorageBuffer %C
    %__1 = OpVariable %_ptr_StorageBuffer_C StorageBuffer
    %_ptr_StorageBuffer_float = OpTypePointer StorageBuffer %float
    %v3uint = OpTypeVector %uint 3
    %uint_32 = OpConstant %uint 32
    %uint_1 = OpConstant %uint 1
    %gl_WorkGroupSize = OpConstantComposite %v3uint %uint_32 %uint_1 %uint_1
    %main = OpFunction %void None %3
    %5 = OpLabel
    %mc = OpVariable %_ptr_Function_10 Function
    %ma = OpVariable %_ptr_Function_16 Function
    %mb = OpVariable %_ptr_Function_16 Function
    %i = OpVariable %_ptr_Function_int Function
    OpStore %mc %14
    %27 = OpAccessChain %_ptr_StorageBuffer_half %_ %int_0 %uint_0
    %30 = OpCooperativeMatrixLoadNV %16 %27 %uint_16 %false None
    OpStore %ma %30
    %36 = OpAccessChain %_ptr_StorageBuffer_half %__0 %int_0 %uint_0
    %38 = OpCooperativeMatrixLoadNV %16 %36 %uint_16 %true None
    OpStore %mb %38
    %39 = OpLoad %16 %ma
    %40 = OpLoad %16 %mb
    %41 = OpLoad %10 %mc
    %42 = OpCooperativeMatrixMulAddNV %10 %39 %40 %41
    OpStore %mc %42
    OpStore %i %int_0
    OpBranch %45
    %45 = OpLabel
    OpLoopMerge %47 %48 None
    OpBranch %49
    %49 = OpLabel
    %50 = OpLoad %int %i
    %53 = OpSLessThan %bool %50 %52
    OpBranchConditional %53 %46 %47
    %46 = OpLabel
    %54 = OpLoad %int %i
    %55 = OpLoad %int %i
    %57 = OpAccessChain %_ptr_Function_float %mc %55
    %58 = OpLoad %float %57
    %60 = OpFMul %float %58 %float_3
    %61 = OpAccessChain %_ptr_Function_float %mc %54
    OpStore %61 %60
    OpBranch %48
    %48 = OpLabel
    %62 = OpLoad %int %i
    %64 = OpIAdd %int %62 %int_1
    OpStore %i %64
    OpBranch %45
    %47 = OpLabel
    %65 = OpLoad %10 %mc
    %71 = OpAccessChain %_ptr_StorageBuffer_float %__1 %int_0 %uint_0
    OpCooperativeMatrixStoreNV %71 %65 %uint_16 %false None
    OpReturn
    OpFunctionEnd
  """

  // glslangValidator -V --target-env vulkan1.3 of a GL_KHR_cooperative_matrix shader: half 16x16 A loaded row-major
  // from a uvec4 buffer with a stride of 2 uvec4s, B column-major from a half buffer with stride 16, an accumulator of
  // 0.25, coopMatMulAdd, a scale by 2 and a row-major store
  private val khronos = spv"""
    ; Version: 1.6
    OpCapability Shader
    OpCapability Float16
    OpCapability StorageBuffer16BitAccess
    OpCapability VulkanMemoryModel
    OpCapability CooperativeMatrixKHR
    OpExtension "SPV_KHR_cooperative_matrix"
    %1 = OpExtInstImport "GLSL.std.450"
    OpMemoryModel Logical Vulkan
    OpEntryPoint GLCompute %main "main" %_ %__0 %__1
    OpExecutionMode %main LocalSize 32 1 1
    OpDecorate %_runtimearr_v4uint ArrayStride 16
    OpDecorate %A Block
    OpMemberDecorate %A 0 Offset 0
    OpDecorate %_ Binding 0
    OpDecorate %_ DescriptorSet 0
    OpDecorate %_runtimearr_half ArrayStride 2
    OpDecorate %B Block
    OpMemberDecorate %B 0 Offset 0
    OpDecorate %__0 Binding 1
    OpDecorate %__0 DescriptorSet 0
    OpDecorate %_runtimearr_float ArrayStride 4
    OpDecorate %C Block
    OpMemberDecorate %C 0 Offset 0
    OpDecorate %__1 Binding 2
    OpDecorate %__1 DescriptorSet 0
    %void = OpTypeVoid
    %3 = OpTypeFunction %void
    %float = OpTypeFloat 32
    %uint = OpTypeInt 32 0
    %uint_3 = OpConstant %uint 3
    %uint_16 = OpConstant %uint 16
    %uint_2 = OpConstant %uint 2
    %11 = OpTypeCooperativeMatrixKHR %float %uint_3 %uint_16 %uint_16 %uint_2
    %_ptr_Function_11 = OpTypePointer Function %11
    %float_0_25 = OpConstant %float 0.25
    %15 = OpConstantComposite %11 %float_0_25
    %half = OpTypeFloat 16
    %uint_0 = OpConstant %uint 0
    %18 = OpTypeCooperativeMatrixKHR %half %uint_3 %uint_16 %uint_16 %uint_0
    %_ptr_Function_18 = OpTypePointer Function %18
    %v4uint = OpTypeVector %uint 4
    %_runtimearr_v4uint = OpTypeRuntimeArray %v4uint
    %A = OpTypeStruct %_runtimearr_v4uint
    %_ptr_StorageBuffer_A = OpTypePointer StorageBuffer %A
    %_ = OpVariable %_ptr_StorageBuffer_A StorageBuffer
    %int = OpTypeInt 32 1
    %int_0 = OpConstant %int 0
    %_ptr_StorageBuffer_v4uint = OpTypePointer StorageBuffer %v4uint
    %uint_1 = OpConstant %uint 1
    %32 = OpTypeCooperativeMatrixKHR %half %uint_3 %uint_16 %uint_16 %uint_1
    %_ptr_Function_32 = OpTypePointer Function %32
    %_runtimearr_half = OpTypeRuntimeArray %half
    %B = OpTypeStruct %_runtimearr_half
    %_ptr_StorageBuffer_B = OpTypePointer StorageBuffer %B
    %__0 = OpVariable %_ptr_StorageBuffer_B StorageBuffer
    %_ptr_StorageBuffer_half = OpTypePointer StorageBuffer %half
    %int_1 = OpConstant %int 1
    %float_2 = OpConstant %float 2
    %_runtimearr_float = OpTypeRuntimeArray %float
    %C = OpTypeStruct %_runtimearr_float
    %_ptr_StorageBuffer_C = OpTypePointer StorageBuffer %C
    %__1 = OpVariable %_ptr_StorageBuffer_C StorageBuffer
    %_ptr_StorageBuffer_float = OpTypePointer StorageBuffer %float
    %v3uint = OpTypeVector %uint 3
    %uint_32 = OpConstant %uint 32
    %59 = OpConstantComposite %v3uint %uint_32 %uint_1 %uint_1
    %main = OpFunction %void None %3
    %5 = OpLabel
    %mc = OpVariable %_ptr_Function_11 Function
    %ma = OpVariable %_ptr_Function_18 Function
    %mb = OpVariable %_ptr_Function_32 Function
    OpStore %mc %15
    %29 = OpAccessChain %_ptr_StorageBuffer_v4uint %_ %int_0 %uint_0
    %30 = OpCooperativeMatrixLoadKHR %18 %29 %int_0 %uint_2 None
    OpStore %ma %30
    %40 = OpAccessChain %_ptr_StorageBuffer_half %__0 %int_0 %uint_0
    %42 = OpCooperativeMatrixLoadKHR %32 %40 %int_1 %uint_16 None
    OpStore %mb %42
    %43 = OpLoad %18 %ma
    %44 = OpLoad %32 %mb
    %45 = OpLoad %11 %mc
    %46 = OpCooperativeMatrixMulAddKHR %11 %43 %44 %45
    OpStore %mc %46
    %47 = OpLoad %11 %mc
    %49 = OpMatrixTimesScalar %11 %47 %float_2
    OpStore %mc %49
    %50 = OpLoad %11 %mc
    %56 = OpAccessChain %_ptr_StorageBuffer_float %__1 %int_0 %uint_0
    OpCooperativeMatrixStoreKHR %56 %50 %int_0 %uint_16 None
    OpReturn
    OpFunctionEnd
  """

  private def halves(memory: RegionMemory, region: AllocationId, values: Vec[Float]): Unit =
    values.zipWithIndex.foreach((value, at) =>
      LittleEndian.write(IEEE754.fromDouble(value.toDouble, 16), memory.array(region), at * 2, 2)
    )

  for (
    (name, module, scale, addend) <- Seq(
      ("SPV_NV_cooperative_matrix", nvidia, 3f, 0.5f),
      ("SPV_KHR_cooperative_matrix", khronos, 2f, 0.25f)
    )
  )
    test(s"$name shaders load by layout, multiply-accumulate binary16 into binary32 and store") {
      val memory = RegionMemory()
      val a      = memory.allocate(512L)
      val b      = memory.allocate(512L)
      val c      = memory.allocate(1024L)
      val left   = grid(rows = 16, columns = 16)((i, k) => ((i + k) % 4).toFloat - 1.5f)
      val right  = grid(rows = 16, columns = 16)((k, j) => ((k * j) % 3).toFloat * 0.25f)
      halves(memory, a, left.flatten)
      halves(memory, b, Vec.tabulate(16)(j => Vec.tabulate(16)(k => right(k)(j))).flatten)
      assertEquals(
        run(module, "main", memory, Vec(buffer(a), buffer(b), buffer(c)), local = testDimensions(32)),
        Right(())
      )
      val expected =
        grid(rows = 16, columns = 16)((i, j) =>
          single(scale * (addend + (0 until 16).map(k => left(i)(k) * right(k)(j)).sum))
        )
      assertEquals(words(memory, c, offset = 0, count = 256), expected.flatten)
    }

  // hand-assembled from SPV_QCOM_cooperative_matrix_conversion, which Qualcomm's compilers emit
  test("QCOM conversions build a matrix from each lane's row, hand each lane its column, and recast arrays") {
    val module = spv"""
      OpCapability CooperativeMatrixKHR
      OpCapability CooperativeMatrixConversionQCOM
      OpExtension "SPV_KHR_cooperative_matrix"
      OpExtension "SPV_QCOM_cooperative_matrix_conversion"
      $openCL
      OpEntryPoint Kernel %main "qcom" %lid
      OpExecutionMode %main SubgroupSize 4
      OpDecorate %lid BuiltIn LocalInvocationIndex
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %half = OpTypeFloat 16
      %uint_0 = OpConstant %uint 0
      %uint_1 = OpConstant %uint 1
      %uint_2 = OpConstant %uint 2
      %uint_3 = OpConstant %uint 3
      %uint_4 = OpConstant %uint 4
      %uint_8 = OpConstant %uint 8
      %row_type = OpTypeArray %uint %uint_4
      %pair_type = OpTypeArray %uint %uint_2
      %halves_type = OpTypeArray %half %uint_8
      %accumulator = OpTypeCooperativeMatrixKHR %uint %uint_3 %uint_4 %uint_4 %uint_2
      %b_matrix = OpTypeCooperativeMatrixKHR %uint %uint_3 %uint_4 %uint_4 %uint_1
      %rows_ptr = OpTypePointer CrossWorkgroup %row_type
      %pairs_ptr = OpTypePointer CrossWorkgroup %pair_type
      %halves_ptr = OpTypePointer CrossWorkgroup %halves_type
      %lid_ptr = OpTypePointer Input %uint
      %lid = OpVariable %lid_ptr Input
      %fn = OpTypeFunction %void %rows_ptr %rows_ptr %rows_ptr %pairs_ptr %halves_ptr
      %main = OpFunction %void None %fn
      %in = OpFunctionParameter %rows_ptr
      %rows_out = OpFunctionParameter %rows_ptr
      %columns_out = OpFunctionParameter %rows_ptr
      %pairs_out = OpFunctionParameter %pairs_ptr
      %halves_out = OpFunctionParameter %halves_ptr
      %entry = OpLabel
      %lane = OpLoad %uint %lid
      %in_slot = OpPtrAccessChain %rows_ptr %in %lane
      %row = OpLoad %row_type %in_slot
      %matrix = OpCompositeConstructCoopMatQCOM %accumulator %row
      %doubled = OpIAdd %accumulator %matrix %matrix
      %back = OpCompositeExtractCoopMatQCOM %row_type %doubled
      %rows_slot = OpPtrAccessChain %rows_ptr %rows_out %lane
      OpStore %rows_slot %back
      %as_b = OpCompositeConstructCoopMatQCOM %b_matrix %row
      %column = OpCompositeExtractCoopMatQCOM %row_type %as_b
      %columns_slot = OpPtrAccessChain %rows_ptr %columns_out %lane
      OpStore %columns_slot %column
      %halves = OpBitCastArrayQCOM %halves_type %row
      %halves_slot = OpPtrAccessChain %halves_ptr %halves_out %lane
      OpStore %halves_slot %halves
      %middle = OpExtractSubArrayQCOM %pair_type %row %uint_1
      %pairs_slot = OpPtrAccessChain %pairs_ptr %pairs_out %lane
      OpStore %pairs_slot %middle
      OpReturn
      OpFunctionEnd
    """
    val memory  = RegionMemory()
    val in      = memory.allocate(64L)
    val rows    = memory.allocate(64L)
    val columns = memory.allocate(64L)
    val pairs   = memory.allocate(32L)
    val recast  = memory.allocate(64L)
    writeWords(memory, in, 0, (0 until 16).map(index => 100L * (index / 4) + index % 4)*)
    assertEquals(
      run(
        module,
        "qcom",
        memory,
        Vec(buffer(in), buffer(rows), buffer(columns), buffer(pairs), buffer(recast)),
        local = testDimensions(4)
      ),
      Right(())
    )
    assertEquals(
      words(memory, rows, offset = 0, count = 16),
      Vec.tabulate(16)(index => 2 * (100L * (index / 4) + index % 4))
    )
    // as a B matrix, lane l's array is column l, so lane l extracts it back
    assertEquals(
      words(memory, columns, offset = 0, count = 16),
      Vec.tabulate(16)(index => 100L * (index / 4) + index % 4)
    )
    assertEquals(
      words(memory, pairs, offset = 0, count = 8),
      Vec.tabulate(4)(lane => Vec(100L * lane + 1, 100L * lane + 2)).flatten
    )
    assertEquals(words(memory, recast, offset = 0, count = 16), words(memory, in, offset = 0, count = 16))
  }

  // glslangValidator -V --target-env vulkan1.3 of a GL_NV_cooperative_matrix2 shader: a 16x8 float accumulator loaded
  // row-major, coopMatTransposeNV into an 8x16 B matrix and a use conversion into an A matrix, both stored row-major
  private val conversions = spv"""
    ; Version: 1.6
    OpCapability Shader
    OpCapability VulkanMemoryModel
    OpCapability CooperativeMatrixConversionsNV
    OpCapability CooperativeMatrixKHR
    OpExtension "SPV_KHR_cooperative_matrix"
    OpExtension "SPV_NV_cooperative_matrix2"
    %1 = OpExtInstImport "GLSL.std.450"
    OpMemoryModel Logical Vulkan
    OpEntryPoint GLCompute %main "main" %_ %__0
    OpExecutionMode %main LocalSize 32 1 1
    OpDecorate %_runtimearr_float ArrayStride 4
    OpDecorate %A Block
    OpMemberDecorate %A 0 Offset 0
    OpDecorate %_ Binding 0
    OpDecorate %_ DescriptorSet 0
    OpDecorate %_runtimearr_float_0 ArrayStride 4
    OpDecorate %B Block
    OpMemberDecorate %B 0 Offset 0
    OpDecorate %__0 Binding 1
    OpDecorate %__0 DescriptorSet 0
    %void = OpTypeVoid
    %3 = OpTypeFunction %void
    %float = OpTypeFloat 32
    %uint = OpTypeInt 32 0
    %uint_3 = OpConstant %uint 3
    %uint_16 = OpConstant %uint 16
    %uint_8 = OpConstant %uint 8
    %uint_2 = OpConstant %uint 2
    %12 = OpTypeCooperativeMatrixKHR %float %uint_3 %uint_16 %uint_8 %uint_2
    %_ptr_Function_12 = OpTypePointer Function %12
    %_runtimearr_float = OpTypeRuntimeArray %float
    %A = OpTypeStruct %_runtimearr_float
    %_ptr_StorageBuffer_A = OpTypePointer StorageBuffer %A
    %_ = OpVariable %_ptr_StorageBuffer_A StorageBuffer
    %int = OpTypeInt 32 1
    %int_0 = OpConstant %int 0
    %uint_0 = OpConstant %uint 0
    %_ptr_StorageBuffer_float = OpTypePointer StorageBuffer %float
    %uint_1 = OpConstant %uint 1
    %26 = OpTypeCooperativeMatrixKHR %float %uint_3 %uint_8 %uint_16 %uint_1
    %_ptr_Function_26 = OpTypePointer Function %26
    %31 = OpTypeCooperativeMatrixKHR %float %uint_3 %uint_16 %uint_8 %uint_0
    %_ptr_Function_31 = OpTypePointer Function %31
    %_runtimearr_float_0 = OpTypeRuntimeArray %float
    %B = OpTypeStruct %_runtimearr_float_0
    %_ptr_StorageBuffer_B = OpTypePointer StorageBuffer %B
    %__0 = OpVariable %_ptr_StorageBuffer_B StorageBuffer
    %uint_128 = OpConstant %uint 128
    %v3uint = OpTypeVector %uint 3
    %uint_32 = OpConstant %uint 32
    %47 = OpConstantComposite %v3uint %uint_32 %uint_1 %uint_1
    %main = OpFunction %void None %3
    %5 = OpLabel
    %acc = OpVariable %_ptr_Function_12 Function
    %t = OpVariable %_ptr_Function_26 Function
    %m = OpVariable %_ptr_Function_31 Function
    %23 = OpAccessChain %_ptr_StorageBuffer_float %_ %int_0 %uint_0
    %24 = OpCooperativeMatrixLoadKHR %12 %23 %int_0 %uint_8 None
    OpStore %acc %24
    %29 = OpLoad %12 %acc
    %30 = OpCooperativeMatrixTransposeNV %26 %29
    OpStore %t %30
    %34 = OpLoad %12 %acc
    %35 = OpCooperativeMatrixConvertNV %31 %34
    OpStore %m %35
    %36 = OpLoad %26 %t
    %41 = OpAccessChain %_ptr_StorageBuffer_float %__0 %int_0 %uint_0
    OpCooperativeMatrixStoreKHR %41 %36 %int_0 %uint_16 None
    %42 = OpLoad %31 %m
    %44 = OpAccessChain %_ptr_StorageBuffer_float %__0 %int_0 %uint_128
    OpCooperativeMatrixStoreKHR %44 %42 %int_0 %uint_8 None
    OpReturn
    OpFunctionEnd
  """

  test("a cooperative matrix load of a type that is not a matrix fails when the module loads") {
    val module = spv"""
      OpCapability Shader
      OpCapability VulkanMemoryModel
      OpCapability CooperativeMatrixKHR
      OpExtension "SPV_KHR_cooperative_matrix"
      OpMemoryModel Logical Vulkan
      OpEntryPoint GLCompute %main "main"
      OpExecutionMode %main LocalSize 32 1 1
      %void = OpTypeVoid
      %fn = OpTypeFunction %void
      %float = OpTypeFloat 32
      %uint = OpTypeInt 32 0
      %ulong = OpTypeInt 64 0
      %uint_0 = OpConstant %uint 0
      %ulong_16 = OpConstant %ulong 16
      %ptr = OpTypePointer Function %float
      %pointer = OpUndef %ptr
      %main = OpFunction %void None %fn
      %entry = OpLabel
      %load = OpCooperativeMatrixLoadKHR %float %pointer %uint_0 %ulong_16 None
      OpReturn
      OpFunctionEnd
    """
    val at = module.instructions.indexWhere(_.opcode.raw == "OpCooperativeMatrixLoadKHR")
    assertEquals(
      SPIRVLowering.failures(module, environment(module)).map(_(at)),
      Right(Some(Fault.InvalidRequest("OpCooperativeMatrixLoadKHR type %4 is not a cooperative matrix")))
    )
  }

  test("SPV_NV_cooperative_matrix2 transposes accumulators into B matrices and converts their use in place") {
    val memory = RegionMemory()
    val a      = memory.allocate(512L)
    val b      = memory.allocate(1024L)
    writeWords(memory, a, 0, (0 until 128).map(index => single(index.toFloat))*)
    assertEquals(run(conversions, "main", memory, Vec(buffer(a), buffer(b)), local = testDimensions(32)), Right(()))
    assertEquals(
      words(memory, b, offset = 0, count = 128),
      grid(rows = 8, columns = 16)((row, column) => single((column * 8 + row).toFloat)).flatten
    )
    assertEquals(words(memory, b, offset = 512, count = 128), Vec.tabulate(128)(index => single(index.toFloat)))
  }
}
