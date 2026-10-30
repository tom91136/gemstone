package net.kurobako.gemstone.isa.spirv

import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.bits.*

class IntelArithmeticSuite extends Suite {

  // glslangValidator -V of a GL_INTEL_shader_integer_functions2 compute shader reading `int a[2]; uint b[2]` and
  // writing `int s[6]; uint u[8]; uvec2 v` through countLeadingZeros, countTrailingZeros, absoluteDifference,
  // addSaturate, subtractSaturate, average, averageRounded and multiply32x16
  private val integerFunctions = spv"""
    ; Version: 1.0
    OpCapability Shader
    OpCapability IntegerFunctions2INTEL
    OpExtension "SPV_INTEL_shader_integer_functions2"
    %1 = OpExtInstImport "GLSL.std.450"
    OpMemoryModel Logical GLSL450
    OpEntryPoint GLCompute %main "main"
    OpExecutionMode %main LocalSize 1 1 1
    OpDecorate %_arr_int_uint_6 ArrayStride 4
    OpDecorate %_arr_uint_uint_8 ArrayStride 4
    OpDecorate %Out BufferBlock
    OpMemberDecorate %Out 0 Offset 0
    OpMemberDecorate %Out 1 Offset 24
    OpMemberDecorate %Out 2 Offset 56
    OpDecorate %_ Binding 1
    OpDecorate %_ DescriptorSet 0
    OpDecorate %_arr_int_uint_2 ArrayStride 4
    OpDecorate %_arr_uint_uint_2 ArrayStride 4
    OpDecorate %In BufferBlock
    OpMemberDecorate %In 0 Offset 0
    OpMemberDecorate %In 1 Offset 8
    OpDecorate %__0 Binding 0
    OpDecorate %__0 DescriptorSet 0
    %void = OpTypeVoid
    %3 = OpTypeFunction %void
    %int = OpTypeInt 32 1
    %uint = OpTypeInt 32 0
    %uint_6 = OpConstant %uint 6
    %_arr_int_uint_6 = OpTypeArray %int %uint_6
    %uint_8 = OpConstant %uint 8
    %_arr_uint_uint_8 = OpTypeArray %uint %uint_8
    %v2uint = OpTypeVector %uint 2
    %Out = OpTypeStruct %_arr_int_uint_6 %_arr_uint_uint_8 %v2uint
    %_ptr_Uniform_Out = OpTypePointer Uniform %Out
    %_ = OpVariable %_ptr_Uniform_Out Uniform
    %int_1 = OpConstant %int 1
    %int_0 = OpConstant %int 0
    %uint_2 = OpConstant %uint 2
    %_arr_int_uint_2 = OpTypeArray %int %uint_2
    %_arr_uint_uint_2 = OpTypeArray %uint %uint_2
    %In = OpTypeStruct %_arr_int_uint_2 %_arr_uint_uint_2
    %_ptr_Uniform_In = OpTypePointer Uniform %In
    %__0 = OpVariable %_ptr_Uniform_In Uniform
    %_ptr_Uniform_uint = OpTypePointer Uniform %uint
    %int_2 = OpConstant %int 2
    %_ptr_Uniform_int = OpTypePointer Uniform %int
    %int_3 = OpConstant %int 3
    %int_4 = OpConstant %int 4
    %int_5 = OpConstant %int 5
    %int_6 = OpConstant %int 6
    %int_7 = OpConstant %int 7
    %_ptr_Uniform_v2uint = OpTypePointer Uniform %v2uint
    %main = OpFunction %void None %3
    %5 = OpLabel
    %25 = OpAccessChain %_ptr_Uniform_uint %__0 %int_1 %int_0
    %26 = OpLoad %uint %25
    %27 = OpUCountLeadingZerosINTEL %uint %26
    %28 = OpAccessChain %_ptr_Uniform_uint %_ %int_1 %int_0
    OpStore %28 %27
    %29 = OpAccessChain %_ptr_Uniform_uint %__0 %int_1 %int_1
    %30 = OpLoad %uint %29
    %31 = OpUCountTrailingZerosINTEL %uint %30
    %32 = OpAccessChain %_ptr_Uniform_uint %_ %int_1 %int_1
    OpStore %32 %31
    %35 = OpAccessChain %_ptr_Uniform_int %__0 %int_0 %int_0
    %36 = OpLoad %int %35
    %37 = OpAccessChain %_ptr_Uniform_int %__0 %int_0 %int_1
    %38 = OpLoad %int %37
    %39 = OpAbsISubINTEL %uint %36 %38
    %40 = OpAccessChain %_ptr_Uniform_uint %_ %int_1 %int_2
    OpStore %40 %39
    %42 = OpAccessChain %_ptr_Uniform_uint %__0 %int_1 %int_0
    %43 = OpLoad %uint %42
    %44 = OpAccessChain %_ptr_Uniform_uint %__0 %int_1 %int_1
    %45 = OpLoad %uint %44
    %46 = OpAbsUSubINTEL %uint %43 %45
    %47 = OpAccessChain %_ptr_Uniform_uint %_ %int_1 %int_3
    OpStore %47 %46
    %48 = OpAccessChain %_ptr_Uniform_int %__0 %int_0 %int_0
    %49 = OpLoad %int %48
    %50 = OpAccessChain %_ptr_Uniform_int %__0 %int_0 %int_1
    %51 = OpLoad %int %50
    %52 = OpIAddSatINTEL %int %49 %51
    %53 = OpAccessChain %_ptr_Uniform_int %_ %int_0 %int_0
    OpStore %53 %52
    %55 = OpAccessChain %_ptr_Uniform_uint %__0 %int_1 %int_0
    %56 = OpLoad %uint %55
    %57 = OpAccessChain %_ptr_Uniform_uint %__0 %int_1 %int_1
    %58 = OpLoad %uint %57
    %59 = OpUAddSatINTEL %uint %56 %58
    %60 = OpAccessChain %_ptr_Uniform_uint %_ %int_1 %int_4
    OpStore %60 %59
    %61 = OpAccessChain %_ptr_Uniform_int %__0 %int_0 %int_0
    %62 = OpLoad %int %61
    %63 = OpAccessChain %_ptr_Uniform_int %__0 %int_0 %int_1
    %64 = OpLoad %int %63
    %65 = OpISubSatINTEL %int %62 %64
    %66 = OpAccessChain %_ptr_Uniform_int %_ %int_0 %int_1
    OpStore %66 %65
    %68 = OpAccessChain %_ptr_Uniform_uint %__0 %int_1 %int_0
    %69 = OpLoad %uint %68
    %70 = OpAccessChain %_ptr_Uniform_uint %__0 %int_1 %int_1
    %71 = OpLoad %uint %70
    %72 = OpUSubSatINTEL %uint %69 %71
    %73 = OpAccessChain %_ptr_Uniform_uint %_ %int_1 %int_5
    OpStore %73 %72
    %74 = OpAccessChain %_ptr_Uniform_int %__0 %int_0 %int_0
    %75 = OpLoad %int %74
    %76 = OpAccessChain %_ptr_Uniform_int %__0 %int_0 %int_1
    %77 = OpLoad %int %76
    %78 = OpIAverageINTEL %int %75 %77
    %79 = OpAccessChain %_ptr_Uniform_int %_ %int_0 %int_2
    OpStore %79 %78
    %81 = OpAccessChain %_ptr_Uniform_uint %__0 %int_1 %int_0
    %82 = OpLoad %uint %81
    %83 = OpAccessChain %_ptr_Uniform_uint %__0 %int_1 %int_1
    %84 = OpLoad %uint %83
    %85 = OpUAverageINTEL %uint %82 %84
    %86 = OpAccessChain %_ptr_Uniform_uint %_ %int_1 %int_6
    OpStore %86 %85
    %87 = OpAccessChain %_ptr_Uniform_int %__0 %int_0 %int_0
    %88 = OpLoad %int %87
    %89 = OpAccessChain %_ptr_Uniform_int %__0 %int_0 %int_1
    %90 = OpLoad %int %89
    %91 = OpIAverageRoundedINTEL %int %88 %90
    %92 = OpAccessChain %_ptr_Uniform_int %_ %int_0 %int_3
    OpStore %92 %91
    %94 = OpAccessChain %_ptr_Uniform_uint %__0 %int_1 %int_0
    %95 = OpLoad %uint %94
    %96 = OpAccessChain %_ptr_Uniform_uint %__0 %int_1 %int_1
    %97 = OpLoad %uint %96
    %98 = OpUAverageRoundedINTEL %uint %95 %97
    %99 = OpAccessChain %_ptr_Uniform_uint %_ %int_1 %int_7
    OpStore %99 %98
    %100 = OpAccessChain %_ptr_Uniform_int %__0 %int_0 %int_0
    %101 = OpLoad %int %100
    %102 = OpAccessChain %_ptr_Uniform_int %__0 %int_0 %int_1
    %103 = OpLoad %int %102
    %104 = OpIMul32x16INTEL %int %101 %103
    %105 = OpAccessChain %_ptr_Uniform_int %_ %int_0 %int_4
    OpStore %105 %104
    %106 = OpAccessChain %_ptr_Uniform_uint %__0 %int_1 %int_0
    %107 = OpLoad %uint %106
    %108 = OpAccessChain %_ptr_Uniform_uint %__0 %int_1 %int_1
    %109 = OpLoad %uint %108
    %110 = OpUMul32x16INTEL %uint %107 %109
    %111 = OpBitcast %int %110
    %112 = OpAccessChain %_ptr_Uniform_int %_ %int_0 %int_5
    OpStore %112 %111
    %113 = OpAccessChain %_ptr_Uniform_uint %__0 %int_1 %int_0
    %114 = OpLoad %uint %113
    %115 = OpAccessChain %_ptr_Uniform_uint %__0 %int_1 %int_1
    %116 = OpLoad %uint %115
    %117 = OpCompositeConstruct %v2uint %114 %116
    %118 = OpAccessChain %_ptr_Uniform_uint %__0 %int_1 %int_1
    %119 = OpLoad %uint %118
    %120 = OpAccessChain %_ptr_Uniform_uint %__0 %int_1 %int_0
    %121 = OpLoad %uint %120
    %122 = OpCompositeConstruct %v2uint %119 %121
    %123 = OpUAddSatINTEL %v2uint %117 %122
    %125 = OpAccessChain %_ptr_Uniform_v2uint %_ %int_2
    OpStore %125 %123
    OpReturn
    OpFunctionEnd
  """

  test("SPV_INTEL_shader_integer_functions2 saturates, averages and multiplies by 16-bit halves at 32 bits") {
    val memory = RegionMemory()
    val in     = memory.allocate(16L)
    val out    = memory.allocate(64L)
    writeWords(memory, in, 0, -2147483600L & 0xffffffffL, 0x00018001L, 0xffff0010L, 0x00012340L)
    assertEquals(run(integerFunctions, "main", memory, Vec(buffer(in), buffer(out))), Right(()))
    def signed(value: Long): Long = value & 0xffffffffL
    assertEquals(
      words(memory, out, offset = 0, count = 16),
      Vec(
        signed(-2147385295L),
        signed(-2147483648L),
        signed(-1073692648L),
        signed(-1073692647L),
        2145910832L,
        0xdcc23400L,
        0L,
        6L,
        0x80017fd1L,
        0xfffddcd0L,
        0xffffffffL,
        0xfffddcd0L,
        0x800011a8L,
        0x800011a8L,
        0xffffffffL,
        0xffffffffL
      )
    )
  }

  // the shape llvm-spirv --spirv-ext=+all gives __spirv_BitwiseFunctionINTEL, __spirv_ConvertFToBF16INTEL,
  // __spirv_ConvertBF16ToFINTEL and __spirv_RoundFToTF32INTEL calls from OpenCL C
  private def conversions(body: String, count: Int): Vec[Long] = {
    val module = spv"""
      OpCapability TernaryBitwiseFunctionINTEL
      OpCapability BFloat16ConversionINTEL
      OpCapability TensorFloat32RoundingINTEL
      OpExtension "SPV_INTEL_ternary_bitwise_function"
      OpExtension "SPV_INTEL_bfloat16_conversion"
      OpExtension "SPV_INTEL_tensor_float32_conversion"
      $openCL
      OpEntryPoint Kernel %main "intel"
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %ushort = OpTypeInt 16 0
      %ulong = OpTypeInt 64 0
      %float = OpTypeFloat 32
      %v2float = OpTypeVector %float 2
      %v2ushort = OpTypeVector %ushort 2
      %uint_ptr = OpTypePointer CrossWorkgroup %uint
      %fn = OpTypeFunction %void %uint_ptr
      %x = OpConstant %uint 0x12345678
      %y = OpConstant %uint 0x9abcdef0
      %z = OpConstant %uint 0x0f0f0f0f
      %select = OpConstant %uint 0xca
      %parity = OpConstant %uint 0x96
      %one = OpConstant %float 1
      %approx_pi = OpConstant %float 3.3
      %tie_even = OpConstant %float 0x1.01p+0
      %tie_odd = OpConstant %float 0x1.03p+0
      %pairs = OpConstantComposite %v2float %tie_even %tie_odd
      %bf_neg = OpConstant %ushort 0xc049
      %bf_inf = OpConstant %ushort 0x7f80
      %bfloats = OpConstantComposite %v2ushort %bf_neg %bf_inf
      %tf_even = OpConstant %float 0x1.002p+0
      %tf_odd = OpConstant %float 0x1.006p+0
      ${Vec.tabulate(count)(index => s"%ulong_$index = OpConstant %ulong $index").mkString("\n")}
      %main = OpFunction %void None %fn
      %out = OpFunctionParameter %uint_ptr
      %entry = OpLabel
      $body
      OpReturn
      OpFunctionEnd
    """
    val memory = RegionMemory()
    val out    = memory.allocate((count * 4L).unsafe[Long > 0])
    assertEquals(run(module, "intel", memory, Vec(buffer(out))), Right(()))
    words(memory, out, 0, count)
  }

  private def store(index: Int, value: String, bits: String = ""): String =
    (if (bits.isEmpty) "" else s"%bits_$index = OpBitcast %uint $value\n") +
      s"""%slot_$index = OpInBoundsPtrAccessChain %uint_ptr %out %ulong_$index
         |OpStore %slot_$index ${if (bits.isEmpty) value else s"%bits_$index"}""".stripMargin

  private def widen(index: Int, value: String): String =
    s"%wide_$index = OpUConvert %uint $value\n" + store(index, s"%wide_$index")

  test("OpBitwiseFunctionINTEL indexes its lookup table by the bits of A, B and C, A lowest") {
    assertEquals(
      conversions(
        Vec(
          "%bitselect = OpBitwiseFunctionINTEL %uint %x %y %z %select",
          store(0, "%bitselect"),
          "%xor = OpBitwiseFunctionINTEL %uint %x %y %z %parity",
          store(1, "%xor")
        ).mkString("\n"),
        2
      ),
      Vec(0x1a3c5e70L, 0x87878787L)
    )
  }

  test("bfloat16 and TF32 conversions round to nearest even and widen exactly") {
    assertEquals(
      conversions(
        Vec(
          "%b_one = OpConvertFToBF16INTEL %ushort %one",
          widen(0, "%b_one"),
          "%b_pi = OpConvertFToBF16INTEL %ushort %approx_pi",
          widen(1, "%b_pi"),
          "%b_pairs = OpConvertFToBF16INTEL %v2ushort %pairs",
          "%b_even = OpCompositeExtract %ushort %b_pairs 0",
          widen(2, "%b_even"),
          "%b_odd = OpCompositeExtract %ushort %b_pairs 1",
          widen(3, "%b_odd"),
          "%f_back = OpConvertBF16ToFINTEL %v2float %bfloats",
          "%f_neg = OpCompositeExtract %float %f_back 0",
          store(4, "%f_neg", "bits"),
          "%f_inf = OpCompositeExtract %float %f_back 1",
          store(5, "%f_inf", "bits"),
          "%t_even = OpRoundFToTF32INTEL %float %tf_even",
          store(6, "%t_even", "bits"),
          "%t_odd = OpRoundFToTF32INTEL %float %tf_odd",
          store(7, "%t_odd", "bits")
        ).mkString("\n"),
        8
      ),
      Vec(0x3f80L, 0x4053L, 0x3f80L, 0x3f82L, 0xc0490000L, 0x7f800000L, 0x3f800000L, 0x3f804000L)
    )
  }

  test("OpFDot2MixAcc32VALVE and OpFDot2MixAcc16VALVE sum binary16 products into the accumulator's width") {
    val module = spv"""
      OpCapability DotProductFloat16AccFloat32VALVE
      OpCapability DotProductFloat16AccFloat16VALVE
      OpExtension "SPV_VALVE_mixed_float_dot_product"
      $openCL
      OpEntryPoint Kernel %main "dot"
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %ushort = OpTypeInt 16 0
      %ulong = OpTypeInt 64 0
      %half = OpTypeFloat 16
      %float = OpTypeFloat 32
      %v2half = OpTypeVector %half 2
      %uint_ptr = OpTypePointer CrossWorkgroup %uint
      %fn = OpTypeFunction %void %uint_ptr
      %h_max = OpConstant %half 0x1.ffcp+15
      %h_two = OpConstant %half 2
      %h_third = OpConstant %half 0x1.554p-2
      %h_three = OpConstant %half 3
      %a = OpConstantComposite %v2half %h_max %h_third
      %b = OpConstantComposite %v2half %h_two %h_three
      %acc32 = OpConstant %float 0.5
      %acc16 = OpConstant %half -0x1.ffcp+15
      %one = OpConstant %ulong 1
      %main = OpFunction %void None %fn
      %out = OpFunctionParameter %uint_ptr
      %entry = OpLabel
      %wide = OpFDot2MixAcc32VALVE %float %a %b %acc32
      %wide_bits = OpBitcast %uint %wide
      OpStore %out %wide_bits
      %narrow = OpFDot2MixAcc16VALVE %half %a %b %acc16
      %narrow_bits = OpBitcast %ushort %narrow
      %narrow_word = OpUConvert %uint %narrow_bits
      %slot = OpInBoundsPtrAccessChain %uint_ptr %out %one
      OpStore %slot %narrow_word
      OpReturn
      OpFunctionEnd
    """
    val memory = RegionMemory()
    val out    = memory.allocate(8L)
    assertEquals(run(module, "dot", memory, Vec(buffer(out))), Right(()))
    // 65504 * 2 overflows binary16 before the accumulator brings the binary16 sum back to its largest finite value
    assertEquals(words(memory, out, offset = 0, count = 2), Vec(single(131009.5f), 0x7bffL))
  }

  test("OpFDot4MixAcc32VALVE sums E4M3 and E5M2 products into a binary32 accumulator with one rounding") {
    val module = spv"""
      OpCapability Float8EXT
      OpCapability DotProductFloat8AccFloat32VALVE
      OpExtension "SPV_EXT_float8"
      OpExtension "SPV_VALVE_mixed_float_dot_product"
      $openCL
      OpEntryPoint Kernel %main "dot"
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %uchar = OpTypeInt 8 0
      %ulong = OpTypeInt 64 0
      %e4m3 = OpTypeFloat 8 Float8E4M3EXT
      %e5m2 = OpTypeFloat 8 Float8E5M2EXT
      %float = OpTypeFloat 32
      %v4e4m3 = OpTypeVector %e4m3 4
      %v4e5m2 = OpTypeVector %e5m2 4
      %uint_ptr = OpTypePointer CrossWorkgroup %uint
      %fn = OpTypeFunction %void %uint_ptr
      %m_max = OpConstant %uchar 0x7e
      %m_one = OpConstant %uchar 0x38
      %m_zero = OpConstant %uchar 0
      %m_nan = OpConstant %uchar 0x7f
      %q_max = OpConstant %uchar 0x7b
      %q_one = OpConstant %uchar 0x3c
      %q_inf = OpConstant %uchar 0x7c
      %acc = OpConstant %float 0.5
      %one = OpConstant %ulong 1
      %two = OpConstant %ulong 2
      %main = OpFunction %void None %fn
      %out = OpFunctionParameter %uint_ptr
      %entry = OpLabel
      %a0 = OpBitcast %e4m3 %m_max
      %a1 = OpBitcast %e4m3 %m_one
      %a3 = OpBitcast %e4m3 %m_zero
      %b0 = OpBitcast %e5m2 %q_max
      %b1 = OpBitcast %e5m2 %q_one
      %b3 = OpBitcast %e5m2 %m_zero
      %a = OpCompositeConstruct %v4e4m3 %a0 %a1 %a1 %a3
      %b = OpCompositeConstruct %v4e5m2 %b0 %b1 %b1 %b3
      %mixed = OpFDot4MixAcc32VALVE %float %a %b %acc
      %mixed_bits = OpBitcast %uint %mixed
      OpStore %out %mixed_bits
      %n0 = OpBitcast %e4m3 %m_nan
      %n = OpCompositeConstruct %v4e4m3 %n0 %a1 %a1 %a1
      %same = OpCompositeConstruct %v4e4m3 %a1 %a1 %a1 %a1
      %nan = OpFDot4MixAcc32VALVE %float %n %same %acc
      %nan_bits = OpBitcast %uint %nan
      %slot1 = OpInBoundsPtrAccessChain %uint_ptr %out %one
      OpStore %slot1 %nan_bits
      %i0 = OpBitcast %e5m2 %q_inf
      %i = OpCompositeConstruct %v4e5m2 %i0 %b1 %b1 %b1
      %inf = OpFDot4MixAcc32VALVE %float %same %i %acc
      %inf_bits = OpBitcast %uint %inf
      %slot2 = OpInBoundsPtrAccessChain %uint_ptr %out %two
      OpStore %slot2 %inf_bits
      OpReturn
      OpFunctionEnd
    """
    val memory = RegionMemory()
    val out    = memory.allocate(12L)
    assertEquals(run(module, "dot", memory, Vec(buffer(out))), Right(()))
    // 448 * 57344 + 1 + 1 + 0.5 is 25690114.5, which binary32 rounds once to even; summing in binary32 would not
    val bits = words(memory, out, offset = 0, count = 3)
    assertEquals(bits(0), single(25690114f))
    assert(java.lang.Float.intBitsToFloat(bits(1).toInt).isNaN)
    assertEquals(bits(2), single(Float.PositiveInfinity))
  }
}
