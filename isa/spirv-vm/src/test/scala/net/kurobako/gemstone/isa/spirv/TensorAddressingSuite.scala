package net.kurobako.gemstone.isa.spirv

import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.bits.*

class TensorAddressingSuite extends Suite {

  // glslangValidator -V --target-env vulkan1.3 of a GL_NV_cooperative_matrix2 shader: an 8x8 float accumulator of 7
  // loaded from a 12x10 tensor sliced at (6, 4) with constant clamping to -1; stored through a 8x16 layout with row
  // stride 20 and a transposing view clipped to columns 1 to 6; stored through a 64-element layout and a 2x4x8 view
  // permuted (0, 2, 1); and two loads from a 5x3 tensor sliced at (-2, 1), repeating and mirroring, stored in order
  private val module = spv"""
    ; Version: 1.6
    OpCapability Shader
    OpCapability VulkanMemoryModel
    OpCapability CooperativeMatrixTensorAddressingNV
    OpCapability TensorAddressingNV
    OpCapability CooperativeMatrixKHR
    OpExtension "SPV_KHR_cooperative_matrix"
    OpExtension "SPV_NV_cooperative_matrix2"
    OpExtension "SPV_NV_tensor_addressing"
    %1 = OpExtInstImport "GLSL.std.450"
    OpMemoryModel Logical Vulkan
    OpEntryPoint GLCompute %main "main" %_ %__0 %__1
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
    OpDecorate %_runtimearr_float_1 ArrayStride 4
    OpDecorate %C Block
    OpMemberDecorate %C 0 Offset 0
    OpDecorate %__1 Binding 2
    OpDecorate %__1 DescriptorSet 0
    %void = OpTypeVoid
    %3 = OpTypeFunction %void
    %uint = OpTypeInt 32 0
    %uint_2 = OpConstant %uint 2
    %uint_1 = OpConstant %uint 1
    %9 = OpTypeTensorLayoutNV %uint_2 %uint_1
    %_ptr_Function_9 = OpTypePointer Function %9
    %int = OpTypeInt 32 1
    %int_2 = OpConstant %int 2
    %int_1 = OpConstant %int 1
    %uint_12 = OpConstant %uint 12
    %uint_10 = OpConstant %uint 10
    %uint_6 = OpConstant %uint 6
    %uint_8 = OpConstant %uint 8
    %uint_4 = OpConstant %uint 4
    %float = OpTypeFloat 32
    %float_n1 = OpConstant %float -1
    %uint_3 = OpConstant %uint 3
    %31 = OpTypeCooperativeMatrixKHR %float %uint_3 %uint_8 %uint_8 %uint_2
    %_ptr_Function_31 = OpTypePointer Function %31
    %float_7 = OpConstant %float 7
    %35 = OpConstantComposite %31 %float_7
    %_runtimearr_float = OpTypeRuntimeArray %float
    %A = OpTypeStruct %_runtimearr_float
    %_ptr_StorageBuffer_A = OpTypePointer StorageBuffer %A
    %_ = OpVariable %_ptr_StorageBuffer_A StorageBuffer
    %int_0 = OpConstant %int 0
    %uint_0 = OpConstant %uint 0
    %_ptr_StorageBuffer_float = OpTypePointer StorageBuffer %float
    %47 = OpTypeTensorLayoutNV %uint_2 %uint_0
    %_ptr_Function_47 = OpTypePointer Function %47
    %uint_16 = OpConstant %uint 16
    %uint_20 = OpConstant %uint 20
    %bool = OpTypeBool
    %false = OpConstantFalse %bool
    %61 = OpTypeTensorViewNV %uint_2 %false %uint_1 %uint_0
    %_ptr_Function_61 = OpTypePointer Function %61
    %_runtimearr_float_0 = OpTypeRuntimeArray %float
    %B = OpTypeStruct %_runtimearr_float_0
    %_ptr_StorageBuffer_B = OpTypePointer StorageBuffer %B
    %__0 = OpVariable %_ptr_StorageBuffer_B StorageBuffer
    %true = OpConstantTrue %bool
    %76 = OpTypeTensorViewNV %uint_3 %true %uint_0 %uint_2 %uint_1
    %_ptr_Function_76 = OpTypePointer Function %76
    %int_3 = OpConstant %int 3
    %uint_32 = OpConstant %uint 32
    %86 = OpTypeTensorLayoutNV %uint_1 %uint_2
    %_ptr_Function_86 = OpTypePointer Function %86
    %uint_64 = OpConstant %uint 64
    %uint_256 = OpConstant %uint 256
    %98 = OpTypeTensorLayoutNV %uint_2 %uint_3
    %_ptr_Function_98 = OpTypePointer Function %98
    %uint_5 = OpConstant %uint 5
    %uint_4294967294 = OpConstant %uint 4294967294
    %_runtimearr_float_1 = OpTypeRuntimeArray %float
    %C = OpTypeStruct %_runtimearr_float_1
    %_ptr_StorageBuffer_C = OpTypePointer StorageBuffer %C
    %__1 = OpVariable %_ptr_StorageBuffer_C StorageBuffer
    %120 = OpTypeTensorLayoutNV %uint_2 %uint_4
    %_ptr_Function_120 = OpTypePointer Function %120
    %int_4 = OpConstant %int 4
    %v3uint = OpTypeVector %uint 3
    %137 = OpConstantComposite %v3uint %uint_32 %uint_1 %uint_1
    %main = OpFunction %void None %3
    %5 = OpLabel
    %t = OpVariable %_ptr_Function_9 Function
    %m = OpVariable %_ptr_Function_31 Function
    %s = OpVariable %_ptr_Function_47 Function
    %v = OpVariable %_ptr_Function_61 Function
    %w = OpVariable %_ptr_Function_76 Function
    %r = OpVariable %_ptr_Function_86 Function
    %q = OpVariable %_ptr_Function_98 Function
    %n = OpVariable %_ptr_Function_31 Function
    %o = OpVariable %_ptr_Function_120 Function
    %15 = OpCreateTensorLayoutNV %9
    OpStore %t %15
    %16 = OpLoad %9 %t
    %19 = OpTensorLayoutSetDimensionNV %9 %16 %uint_12 %uint_10
    OpStore %t %19
    %20 = OpLoad %9 %t
    %24 = OpTensorLayoutSliceNV %9 %20 %uint_6 %uint_8 %uint_4 %uint_8
    OpStore %t %24
    %25 = OpLoad %9 %t
    %28 = OpBitcast %uint %float_n1
    %29 = OpTensorLayoutSetClampValueNV %9 %25 %28
    OpStore %t %29
    OpStore %m %35
    %43 = OpAccessChain %_ptr_StorageBuffer_float %_ %int_0 %uint_0
    %44 = OpLoad %9 %t
    %45 = OpLoad %31 %m
    %46 = OpCooperativeMatrixLoadTensorNV %31 %43 %45 %44 None None
    OpStore %m %46
    %50 = OpCreateTensorLayoutNV %47
    OpStore %s %50
    %51 = OpLoad %47 %s
    %53 = OpTensorLayoutSetDimensionNV %47 %51 %uint_8 %uint_16
    OpStore %s %53
    %54 = OpLoad %47 %s
    %56 = OpTensorLayoutSetStrideNV %47 %54 %uint_20 %uint_1
    OpStore %s %56
    %57 = OpLoad %47 %s
    %58 = OpTensorLayoutSetBlockSizeNV %47 %57 %uint_1 %uint_1
    OpStore %s %58
    %64 = OpCreateTensorViewNV %61
    OpStore %v %64
    %65 = OpLoad %61 %v
    %66 = OpTensorViewSetClipNV %61 %65 %uint_0 %uint_8 %uint_1 %uint_6
    OpStore %v %66
    %67 = OpLoad %31 %m
    %72 = OpAccessChain %_ptr_StorageBuffer_float %__0 %int_0 %uint_0
    %73 = OpLoad %47 %s
    %74 = OpLoad %61 %v
    OpCooperativeMatrixStoreTensorNV %72 %67 %73 None TensorView %74
    %80 = OpCreateTensorViewNV %76
    OpStore %w %80
    %81 = OpLoad %76 %w
    %82 = OpTensorViewSetDimensionNV %76 %81 %uint_2 %uint_4 %uint_8
    OpStore %w %82
    %83 = OpLoad %76 %w
    %85 = OpTensorViewSetStrideNV %76 %83 %uint_32 %uint_8 %uint_1
    OpStore %w %85
    %89 = OpCreateTensorLayoutNV %86
    OpStore %r %89
    %90 = OpLoad %86 %r
    %92 = OpTensorLayoutSetDimensionNV %86 %90 %uint_64
    OpStore %r %92
    %93 = OpLoad %31 %m
    %95 = OpAccessChain %_ptr_StorageBuffer_float %__0 %int_0 %uint_256
    %96 = OpLoad %86 %r
    %97 = OpLoad %76 %w
    OpCooperativeMatrixStoreTensorNV %95 %93 %96 None TensorView %97
    %101 = OpCreateTensorLayoutNV %98
    OpStore %q %101
    %102 = OpLoad %98 %q
    %104 = OpTensorLayoutSetDimensionNV %98 %102 %uint_5 %uint_3
    OpStore %q %104
    %105 = OpLoad %98 %q
    %107 = OpTensorLayoutSliceNV %98 %105 %uint_4294967294 %uint_8 %uint_1 %uint_8
    OpStore %q %107
    %109 = OpAccessChain %_ptr_StorageBuffer_float %_ %int_0 %uint_0
    %110 = OpLoad %98 %q
    %111 = OpLoad %31 %n
    %112 = OpCooperativeMatrixLoadTensorNV %31 %109 %111 %110 None None
    OpStore %n %112
    %113 = OpLoad %31 %n
    %118 = OpAccessChain %_ptr_StorageBuffer_float %__1 %int_0 %uint_0
    %119 = OpLoad %86 %r
    OpCooperativeMatrixStoreTensorNV %118 %113 %119 None None
    %124 = OpCreateTensorLayoutNV %120
    OpStore %o %124
    %125 = OpLoad %120 %o
    %126 = OpTensorLayoutSetDimensionNV %120 %125 %uint_5 %uint_3
    OpStore %o %126
    %127 = OpLoad %120 %o
    %128 = OpTensorLayoutSliceNV %120 %127 %uint_4294967294 %uint_8 %uint_1 %uint_8
    OpStore %o %128
    %129 = OpAccessChain %_ptr_StorageBuffer_float %_ %int_0 %uint_0
    %130 = OpLoad %120 %o
    %131 = OpLoad %31 %n
    %132 = OpCooperativeMatrixLoadTensorNV %31 %129 %131 %130 None None
    OpStore %n %132
    %133 = OpLoad %31 %n
    %134 = OpAccessChain %_ptr_StorageBuffer_float %__1 %int_0 %uint_64
    %135 = OpLoad %86 %r
    OpCooperativeMatrixStoreTensorNV %134 %133 %135 None None
    OpReturn
    OpFunctionEnd
  """

  private val Sentinel = single(-9f)

  test("tensor layouts slice, clamp, repeat and mirror, and views clip and permute tensor loads and stores") {
    val memory = RegionMemory()
    val a      = memory.allocate(480L)
    val b      = memory.allocate(1280L)
    val c      = memory.allocate(512L)
    writeWords(memory, a, 0, (0 until 120).map(at => single(at.toFloat))*)
    writeWords(memory, b, 0, Seq.fill(320)(Sentinel)*)
    assertEquals(
      run(module, "main", memory, Vec(buffer(a), buffer(b), buffer(c)), local = testDimensions(32)),
      Right(())
    )
    val loaded = Vec.tabulate(8)(row =>
      Vec.tabulate(8)(column => if (row < 6 && column < 6) ((row + 6) * 10 + column + 4).toFloat else -1f)
    )
    val clipped = (0 until 8).flatMap(row => (1 until 7).map(column => (row, column))).zipWithIndex.map {
      case ((row, column), linear) => ((linear % 8) * 20 + (linear / 8) % 16) -> loaded(row)(column)
    }.toMap
    val permuted = Vec.tabulate(64) { linear =>
      ((linear / 32) % 2) * 32 + (linear % 4) * 8 + (linear / 4) % 8
    }
    val expected = Vec.tabulate(256)(at => clipped.get(at).fold(Sentinel)(single)) ++
      Vec.tabulate(64)(at => single(loaded(permuted.indexOf(at) / 8)(permuted.indexOf(at) % 8)))
    assertEquals(words(memory, b, offset = 0, count = 320), expected)
    def mirror(coordinate: Int, extent: Int) = {
      val period = Math.floorMod(coordinate, 2 * extent - 2)
      if (period >= extent) 2 * extent - 2 - period else period
    }
    val repeated = Vec.tabulate(64)(at =>
      single((Math.floorMod(at / 8 - 2, 5) * 3 + Math.floorMod(at % 8 + 1, 3)).toFloat)
    )
    val mirrored = Vec.tabulate(64)(at => single((mirror(at / 8 - 2, 5) * 3 + mirror(at % 8 + 1, 3)).toFloat))
    assertEquals(words(memory, c, offset = 0, count = 128), repeated ++ mirrored)
  }
}
