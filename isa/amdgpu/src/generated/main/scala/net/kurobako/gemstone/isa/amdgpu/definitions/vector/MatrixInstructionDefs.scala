// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Regenerate with: sbt codegen
// scalafix:off GemstoneNoAssume; values were validated by the generator
package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Vec
import Instruction.*
import Program.*

@javax.annotation.processing.Generated(Array("sbt codegen"))
private[amdgpu] object MatrixInstructionDefs {
  case object SWaitTensorcnt extends Definition(
        Opcode.assume("s_wait_tensorcnt"),
        decoder(form(1)((operands, mods) => Instruction.SWaitTensorcnt(operands(0), mods))),
        1264
      )
  case object TensorLoadToLds extends Definition(
        Opcode.assume("tensor_load_to_lds"),
        decoder(form(4)((operands, mods) =>
          Instruction.TensorLoadToLds(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1359
      )
  case object TensorStoreFromLds extends Definition(
        Opcode.assume("tensor_store_from_lds"),
        decoder(form(4)((operands, mods) =>
          Instruction.TensorStoreFromLds(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1360
      )
  case object VMfmaF3216x16x128F8f6f4 extends Definition(
        Opcode.assume("v_mfma_f32_16x16x128_f8f6f4"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x128F8f6f4(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2027
      )
  case object VMfmaF3216x16x16Bf16 extends Definition(
        Opcode.assume("v_mfma_f32_16x16x16_bf16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x16Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2028
      )
  case object VMfmaF3216x16x16F16_1 extends Definition(
        Opcode.assume("v_mfma_f32_16x16x16_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x16F16_1(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2029
      )
  case object VMfmaF3216x16x16bf161k extends Definition(
        Opcode.assume("v_mfma_f32_16x16x16bf16_1k"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x16bf161k(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2030
      )
  case object VMfmaF3216x16x16f16_2 extends Definition(
        Opcode.assume("v_mfma_f32_16x16x16f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x16f16_2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2031
      )
  case object VMfmaF3216x16x14bF32 extends Definition(
        Opcode.assume("v_mfma_f32_16x16x1_4b_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x14bF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2032
      )
  case object VMfmaF3216x16x1f32 extends Definition(
        Opcode.assume("v_mfma_f32_16x16x1f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x1f32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2033
      )
  case object VMfmaF3216x16x2bf16 extends Definition(
        Opcode.assume("v_mfma_f32_16x16x2bf16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x2bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2034
      )
  case object VMfmaF3216x16x32Bf16 extends Definition(
        Opcode.assume("v_mfma_f32_16x16x32_bf16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x32Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2035
      )
  case object VMfmaF3216x16x32Bf8Bf8 extends Definition(
        Opcode.assume("v_mfma_f32_16x16x32_bf8_bf8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x32Bf8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2036
      )
  case object VMfmaF3216x16x32Bf8Fp8 extends Definition(
        Opcode.assume("v_mfma_f32_16x16x32_bf8_fp8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x32Bf8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2037
      )
  case object VMfmaF3216x16x32F16 extends Definition(
        Opcode.assume("v_mfma_f32_16x16x32_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x32F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2038
      )
  case object VMfmaF3216x16x32Fp8Bf8 extends Definition(
        Opcode.assume("v_mfma_f32_16x16x32_fp8_bf8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x32Fp8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2039
      )
  case object VMfmaF3216x16x32Fp8Fp8 extends Definition(
        Opcode.assume("v_mfma_f32_16x16x32_fp8_fp8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x32Fp8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2040
      )
  case object VMfmaF3216x16x44bBf16 extends Definition(
        Opcode.assume("v_mfma_f32_16x16x4_4b_bf16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x44bBf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2041
      )
  case object VMfmaF3216x16x44bF16 extends Definition(
        Opcode.assume("v_mfma_f32_16x16x4_4b_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x44bF16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2042
      )
  case object VMfmaF3216x16x4F32_1 extends Definition(
        Opcode.assume("v_mfma_f32_16x16x4_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x4F32_1(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2043
      )
  case object VMfmaF3216x16x4bf161k extends Definition(
        Opcode.assume("v_mfma_f32_16x16x4bf16_1k"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x4bf161k(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2044
      )
  case object VMfmaF3216x16x4f16 extends Definition(
        Opcode.assume("v_mfma_f32_16x16x4f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x4f16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2045
      )
  case object VMfmaF3216x16x4f32_2 extends Definition(
        Opcode.assume("v_mfma_f32_16x16x4f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x4f32_2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2046
      )
  case object VMfmaF3216x16x8Xf32 extends Definition(
        Opcode.assume("v_mfma_f32_16x16x8_xf32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x8Xf32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2047
      )
  case object VMfmaF3216x16x8bf16 extends Definition(
        Opcode.assume("v_mfma_f32_16x16x8bf16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3216x16x8bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2048
      )
  case object VMfmaF3232x32x16Bf16 extends Definition(
        Opcode.assume("v_mfma_f32_32x32x16_bf16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x16Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2049
      )
  case object VMfmaF3232x32x16Bf8Bf8 extends Definition(
        Opcode.assume("v_mfma_f32_32x32x16_bf8_bf8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x16Bf8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2050
      )
  case object VMfmaF3232x32x16Bf8Fp8 extends Definition(
        Opcode.assume("v_mfma_f32_32x32x16_bf8_fp8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x16Bf8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2051
      )
  case object VMfmaF3232x32x16F16 extends Definition(
        Opcode.assume("v_mfma_f32_32x32x16_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x16F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2052
      )
  case object VMfmaF3232x32x16Fp8Bf8 extends Definition(
        Opcode.assume("v_mfma_f32_32x32x16_fp8_bf8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x16Fp8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2053
      )
  case object VMfmaF3232x32x16Fp8Fp8 extends Definition(
        Opcode.assume("v_mfma_f32_32x32x16_fp8_fp8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x16Fp8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2054
      )
  case object VMfmaF3232x32x12bF32 extends Definition(
        Opcode.assume("v_mfma_f32_32x32x1_2b_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x12bF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2055
      )
  case object VMfmaF3232x32x1f32 extends Definition(
        Opcode.assume("v_mfma_f32_32x32x1f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x1f32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2056
      )
  case object VMfmaF3232x32x2F32_1 extends Definition(
        Opcode.assume("v_mfma_f32_32x32x2_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x2F32_1(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2057
      )
  case object VMfmaF3232x32x2bf16 extends Definition(
        Opcode.assume("v_mfma_f32_32x32x2bf16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x2bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2058
      )
  case object VMfmaF3232x32x2f32_2 extends Definition(
        Opcode.assume("v_mfma_f32_32x32x2f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x2f32_2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2059
      )
  case object VMfmaF3232x32x42bBf16 extends Definition(
        Opcode.assume("v_mfma_f32_32x32x4_2b_bf16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x42bBf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2060
      )
  case object VMfmaF3232x32x42bF16 extends Definition(
        Opcode.assume("v_mfma_f32_32x32x4_2b_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x42bF16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2061
      )
  case object VMfmaF3232x32x4Xf32 extends Definition(
        Opcode.assume("v_mfma_f32_32x32x4_xf32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x4Xf32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2062
      )
  case object VMfmaF3232x32x4bf16 extends Definition(
        Opcode.assume("v_mfma_f32_32x32x4bf16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x4bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2063
      )
  case object VMfmaF3232x32x4bf161k extends Definition(
        Opcode.assume("v_mfma_f32_32x32x4bf16_1k"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x4bf161k(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2064
      )
  case object VMfmaF3232x32x4f16 extends Definition(
        Opcode.assume("v_mfma_f32_32x32x4f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x4f16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2065
      )
  case object VMfmaF3232x32x64F8f6f4 extends Definition(
        Opcode.assume("v_mfma_f32_32x32x64_f8f6f4"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x64F8f6f4(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2066
      )
  case object VMfmaF3232x32x8Bf16 extends Definition(
        Opcode.assume("v_mfma_f32_32x32x8_bf16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x8Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2067
      )
  case object VMfmaF3232x32x8F16_1 extends Definition(
        Opcode.assume("v_mfma_f32_32x32x8_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x8F16_1(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2068
      )
  case object VMfmaF3232x32x8bf161k extends Definition(
        Opcode.assume("v_mfma_f32_32x32x8bf16_1k"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x8bf161k(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2069
      )
  case object VMfmaF3232x32x8f16_2 extends Definition(
        Opcode.assume("v_mfma_f32_32x32x8f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF3232x32x8f16_2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2070
      )
  case object VMfmaF324x4x116bF32 extends Definition(
        Opcode.assume("v_mfma_f32_4x4x1_16b_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF324x4x116bF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2071
      )
  case object VMfmaF324x4x1f32 extends Definition(
        Opcode.assume("v_mfma_f32_4x4x1f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF324x4x1f32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2072
      )
  case object VMfmaF324x4x2bf16 extends Definition(
        Opcode.assume("v_mfma_f32_4x4x2bf16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF324x4x2bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2073
      )
  case object VMfmaF324x4x416bBf16 extends Definition(
        Opcode.assume("v_mfma_f32_4x4x4_16b_bf16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF324x4x416bBf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2074
      )
  case object VMfmaF324x4x416bF16 extends Definition(
        Opcode.assume("v_mfma_f32_4x4x4_16b_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF324x4x416bF16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2075
      )
  case object VMfmaF324x4x4bf161k extends Definition(
        Opcode.assume("v_mfma_f32_4x4x4bf16_1k"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF324x4x4bf161k(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2076
      )
  case object VMfmaF324x4x4f16 extends Definition(
        Opcode.assume("v_mfma_f32_4x4x4f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF324x4x4f16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2077
      )
  case object VMfmaF6416x16x4F64_1 extends Definition(
        Opcode.assume("v_mfma_f64_16x16x4_f64"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF6416x16x4F64_1(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2078
      )
  case object VMfmaF6416x16x4f64_2 extends Definition(
        Opcode.assume("v_mfma_f64_16x16x4f64"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF6416x16x4f64_2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2079
      )
  case object VMfmaF644x4x44bF64 extends Definition(
        Opcode.assume("v_mfma_f64_4x4x4_4b_f64"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF644x4x44bF64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2080
      )
  case object VMfmaF644x4x4f64 extends Definition(
        Opcode.assume("v_mfma_f64_4x4x4f64"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaF644x4x4f64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2081
      )
  case object VMfmaI3216x16x16i8 extends Definition(
        Opcode.assume("v_mfma_i32_16x16x16i8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaI3216x16x16i8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2082
      )
  case object VMfmaI3216x16x32I8 extends Definition(
        Opcode.assume("v_mfma_i32_16x16x32_i8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaI3216x16x32I8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2083
      )
  case object VMfmaI3216x16x44bI8 extends Definition(
        Opcode.assume("v_mfma_i32_16x16x4_4b_i8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaI3216x16x44bI8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2084
      )
  case object VMfmaI3216x16x4i8 extends Definition(
        Opcode.assume("v_mfma_i32_16x16x4i8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaI3216x16x4i8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2085
      )
  case object VMfmaI3216x16x64I8 extends Definition(
        Opcode.assume("v_mfma_i32_16x16x64_i8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaI3216x16x64I8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2086
      )
  case object VMfmaI3232x32x16I8 extends Definition(
        Opcode.assume("v_mfma_i32_32x32x16_i8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaI3232x32x16I8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2087
      )
  case object VMfmaI3232x32x32I8 extends Definition(
        Opcode.assume("v_mfma_i32_32x32x32_i8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaI3232x32x32I8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2088
      )
  case object VMfmaI3232x32x42bI8 extends Definition(
        Opcode.assume("v_mfma_i32_32x32x4_2b_i8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaI3232x32x42bI8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2089
      )
  case object VMfmaI3232x32x4i8 extends Definition(
        Opcode.assume("v_mfma_i32_32x32x4i8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaI3232x32x4i8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2090
      )
  case object VMfmaI3232x32x8i8 extends Definition(
        Opcode.assume("v_mfma_i32_32x32x8i8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaI3232x32x8i8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2091
      )
  case object VMfmaI324x4x416bI8 extends Definition(
        Opcode.assume("v_mfma_i32_4x4x4_16b_i8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaI324x4x416bI8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2092
      )
  case object VMfmaI324x4x4i8 extends Definition(
        Opcode.assume("v_mfma_i32_4x4x4i8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMfmaI324x4x4i8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2093
      )
  case object VMfmaScaleF3216x16x128F8f6f4 extends Definition(
        Opcode.assume("v_mfma_scale_f32_16x16x128_f8f6f4"),
        decoder(form(6)((operands, mods) =>
          Instruction.VMfmaScaleF3216x16x128F8f6f4(
            operands(0),
            operands(1),
            operands(2),
            operands(3),
            operands(4),
            operands(5),
            mods
          )
        )),
        2094
      )
  case object VMfmaScaleF3232x32x64F8f6f4 extends Definition(
        Opcode.assume("v_mfma_scale_f32_32x32x64_f8f6f4"),
        decoder(form(6)((operands, mods) =>
          Instruction.VMfmaScaleF3232x32x64F8f6f4(
            operands(0),
            operands(1),
            operands(2),
            operands(3),
            operands(4),
            operands(5),
            mods
          )
        )),
        2095
      )
  case object VSmfmacF3216x16x128Bf8Bf8 extends Definition(
        Opcode.assume("v_smfmac_f32_16x16x128_bf8_bf8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VSmfmacF3216x16x128Bf8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2271
      )
  case object VSmfmacF3216x16x128Bf8Fp8 extends Definition(
        Opcode.assume("v_smfmac_f32_16x16x128_bf8_fp8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VSmfmacF3216x16x128Bf8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2272
      )
  case object VSmfmacF3216x16x128Fp8Bf8 extends Definition(
        Opcode.assume("v_smfmac_f32_16x16x128_fp8_bf8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VSmfmacF3216x16x128Fp8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2273
      )
  case object VSmfmacF3216x16x128Fp8Fp8 extends Definition(
        Opcode.assume("v_smfmac_f32_16x16x128_fp8_fp8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VSmfmacF3216x16x128Fp8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2274
      )
  case object VSmfmacF3216x16x32Bf16 extends Definition(
        Opcode.assume("v_smfmac_f32_16x16x32_bf16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VSmfmacF3216x16x32Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2275
      )
  case object VSmfmacF3216x16x32F16 extends Definition(
        Opcode.assume("v_smfmac_f32_16x16x32_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VSmfmacF3216x16x32F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2276
      )
  case object VSmfmacF3216x16x64Bf16 extends Definition(
        Opcode.assume("v_smfmac_f32_16x16x64_bf16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VSmfmacF3216x16x64Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2277
      )
  case object VSmfmacF3216x16x64Bf8Bf8 extends Definition(
        Opcode.assume("v_smfmac_f32_16x16x64_bf8_bf8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VSmfmacF3216x16x64Bf8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2278
      )
  case object VSmfmacF3216x16x64Bf8Fp8 extends Definition(
        Opcode.assume("v_smfmac_f32_16x16x64_bf8_fp8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VSmfmacF3216x16x64Bf8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2279
      )
  case object VSmfmacF3216x16x64F16 extends Definition(
        Opcode.assume("v_smfmac_f32_16x16x64_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VSmfmacF3216x16x64F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2280
      )
  case object VSmfmacF3216x16x64Fp8Bf8 extends Definition(
        Opcode.assume("v_smfmac_f32_16x16x64_fp8_bf8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VSmfmacF3216x16x64Fp8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2281
      )
  case object VSmfmacF3216x16x64Fp8Fp8 extends Definition(
        Opcode.assume("v_smfmac_f32_16x16x64_fp8_fp8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VSmfmacF3216x16x64Fp8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2282
      )
  case object VSmfmacF3232x32x16Bf16 extends Definition(
        Opcode.assume("v_smfmac_f32_32x32x16_bf16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VSmfmacF3232x32x16Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2283
      )
  case object VSmfmacF3232x32x16F16 extends Definition(
        Opcode.assume("v_smfmac_f32_32x32x16_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VSmfmacF3232x32x16F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2284
      )
  case object VSmfmacF3232x32x32Bf16 extends Definition(
        Opcode.assume("v_smfmac_f32_32x32x32_bf16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VSmfmacF3232x32x32Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2285
      )
  case object VSmfmacF3232x32x32Bf8Bf8 extends Definition(
        Opcode.assume("v_smfmac_f32_32x32x32_bf8_bf8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VSmfmacF3232x32x32Bf8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2286
      )
  case object VSmfmacF3232x32x32Bf8Fp8 extends Definition(
        Opcode.assume("v_smfmac_f32_32x32x32_bf8_fp8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VSmfmacF3232x32x32Bf8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2287
      )
  case object VSmfmacF3232x32x32F16 extends Definition(
        Opcode.assume("v_smfmac_f32_32x32x32_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VSmfmacF3232x32x32F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2288
      )
  case object VSmfmacF3232x32x32Fp8Bf8 extends Definition(
        Opcode.assume("v_smfmac_f32_32x32x32_fp8_bf8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VSmfmacF3232x32x32Fp8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2289
      )
  case object VSmfmacF3232x32x32Fp8Fp8 extends Definition(
        Opcode.assume("v_smfmac_f32_32x32x32_fp8_fp8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VSmfmacF3232x32x32Fp8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2290
      )
  case object VSmfmacF3232x32x64Bf8Bf8 extends Definition(
        Opcode.assume("v_smfmac_f32_32x32x64_bf8_bf8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VSmfmacF3232x32x64Bf8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2291
      )
  case object VSmfmacF3232x32x64Bf8Fp8 extends Definition(
        Opcode.assume("v_smfmac_f32_32x32x64_bf8_fp8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VSmfmacF3232x32x64Bf8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2292
      )
  case object VSmfmacF3232x32x64Fp8Bf8 extends Definition(
        Opcode.assume("v_smfmac_f32_32x32x64_fp8_bf8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VSmfmacF3232x32x64Fp8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2293
      )
  case object VSmfmacF3232x32x64Fp8Fp8 extends Definition(
        Opcode.assume("v_smfmac_f32_32x32x64_fp8_fp8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VSmfmacF3232x32x64Fp8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2294
      )
  case object VSmfmacI3216x16x128I8 extends Definition(
        Opcode.assume("v_smfmac_i32_16x16x128_i8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VSmfmacI3216x16x128I8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2295
      )
  case object VSmfmacI3216x16x64I8 extends Definition(
        Opcode.assume("v_smfmac_i32_16x16x64_i8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VSmfmacI3216x16x64I8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2296
      )
  case object VSmfmacI3232x32x32I8 extends Definition(
        Opcode.assume("v_smfmac_i32_32x32x32_i8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VSmfmacI3232x32x32I8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2297
      )
  case object VSmfmacI3232x32x64I8 extends Definition(
        Opcode.assume("v_smfmac_i32_32x32x64_i8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VSmfmacI3232x32x64I8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2298
      )
  case object VSwmmacBf1616x16x32Bf16 extends Definition(
        Opcode.assume("v_swmmac_bf16_16x16x32_bf16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacBf1616x16x32Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2328
      )
  case object VSwmmacBf1616x16x64Bf16 extends Definition(
        Opcode.assume("v_swmmac_bf16_16x16x64_bf16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacBf1616x16x64Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2329
      )
  case object VSwmmacBf16f3216x16x64Bf16 extends Definition(
        Opcode.assume("v_swmmac_bf16f32_16x16x64_bf16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacBf16f3216x16x64Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2330
      )
  case object VSwmmacF1616x16x128Bf8Bf8 extends Definition(
        Opcode.assume("v_swmmac_f16_16x16x128_bf8_bf8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacF1616x16x128Bf8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2331
      )
  case object VSwmmacF1616x16x128Bf8Fp8 extends Definition(
        Opcode.assume("v_swmmac_f16_16x16x128_bf8_fp8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacF1616x16x128Bf8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2332
      )
  case object VSwmmacF1616x16x128Fp8Bf8 extends Definition(
        Opcode.assume("v_swmmac_f16_16x16x128_fp8_bf8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacF1616x16x128Fp8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2333
      )
  case object VSwmmacF1616x16x128Fp8Fp8 extends Definition(
        Opcode.assume("v_swmmac_f16_16x16x128_fp8_fp8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacF1616x16x128Fp8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2334
      )
  case object VSwmmacF1616x16x32F16 extends Definition(
        Opcode.assume("v_swmmac_f16_16x16x32_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacF1616x16x32F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2335
      )
  case object VSwmmacF1616x16x64F16 extends Definition(
        Opcode.assume("v_swmmac_f16_16x16x64_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacF1616x16x64F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2336
      )
  case object VSwmmacF3216x16x128Bf8Bf8 extends Definition(
        Opcode.assume("v_swmmac_f32_16x16x128_bf8_bf8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacF3216x16x128Bf8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2337
      )
  case object VSwmmacF3216x16x128Bf8Fp8 extends Definition(
        Opcode.assume("v_swmmac_f32_16x16x128_bf8_fp8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacF3216x16x128Bf8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2338
      )
  case object VSwmmacF3216x16x128Fp8Bf8 extends Definition(
        Opcode.assume("v_swmmac_f32_16x16x128_fp8_bf8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacF3216x16x128Fp8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2339
      )
  case object VSwmmacF3216x16x128Fp8Fp8 extends Definition(
        Opcode.assume("v_swmmac_f32_16x16x128_fp8_fp8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacF3216x16x128Fp8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2340
      )
  case object VSwmmacF3216x16x32Bf16 extends Definition(
        Opcode.assume("v_swmmac_f32_16x16x32_bf16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacF3216x16x32Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2341
      )
  case object VSwmmacF3216x16x32Bf8Bf8 extends Definition(
        Opcode.assume("v_swmmac_f32_16x16x32_bf8_bf8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacF3216x16x32Bf8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2342
      )
  case object VSwmmacF3216x16x32Bf8Fp8 extends Definition(
        Opcode.assume("v_swmmac_f32_16x16x32_bf8_fp8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacF3216x16x32Bf8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2343
      )
  case object VSwmmacF3216x16x32F16 extends Definition(
        Opcode.assume("v_swmmac_f32_16x16x32_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacF3216x16x32F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2344
      )
  case object VSwmmacF3216x16x32Fp8Bf8 extends Definition(
        Opcode.assume("v_swmmac_f32_16x16x32_fp8_bf8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacF3216x16x32Fp8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2345
      )
  case object VSwmmacF3216x16x32Fp8Fp8 extends Definition(
        Opcode.assume("v_swmmac_f32_16x16x32_fp8_fp8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacF3216x16x32Fp8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2346
      )
  case object VSwmmacF3216x16x64Bf16 extends Definition(
        Opcode.assume("v_swmmac_f32_16x16x64_bf16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacF3216x16x64Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2347
      )
  case object VSwmmacF3216x16x64F16 extends Definition(
        Opcode.assume("v_swmmac_f32_16x16x64_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacF3216x16x64F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2348
      )
  case object VSwmmacI3216x16x128Iu8 extends Definition(
        Opcode.assume("v_swmmac_i32_16x16x128_iu8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacI3216x16x128Iu8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2349
      )
  case object VSwmmacI3216x16x32Iu4 extends Definition(
        Opcode.assume("v_swmmac_i32_16x16x32_iu4"),
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacI3216x16x32Iu4(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2350
      )
  case object VSwmmacI3216x16x32Iu8 extends Definition(
        Opcode.assume("v_swmmac_i32_16x16x32_iu8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacI3216x16x32Iu8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2351
      )
  case object VSwmmacI3216x16x64Iu4 extends Definition(
        Opcode.assume("v_swmmac_i32_16x16x64_iu4"),
        decoder(form(4)((operands, mods) =>
          Instruction.VSwmmacI3216x16x64Iu4(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2352
      )
  case object VWmmaBf1616x16x16Bf16 extends Definition(
        Opcode.assume("v_wmma_bf16_16x16x16_bf16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaBf1616x16x16Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2360
      )
  case object VWmmaBf1616x16x32Bf16 extends Definition(
        Opcode.assume("v_wmma_bf16_16x16x32_bf16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaBf1616x16x32Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2361
      )
  case object VWmmaBf16f3216x16x32Bf16 extends Definition(
        Opcode.assume("v_wmma_bf16f32_16x16x32_bf16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaBf16f3216x16x32Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2362
      )
  case object VWmmaF1616x16x128Bf8Bf8 extends Definition(
        Opcode.assume("v_wmma_f16_16x16x128_bf8_bf8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF1616x16x128Bf8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2363
      )
  case object VWmmaF1616x16x128Bf8Fp8 extends Definition(
        Opcode.assume("v_wmma_f16_16x16x128_bf8_fp8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF1616x16x128Bf8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2364
      )
  case object VWmmaF1616x16x128Fp8Bf8 extends Definition(
        Opcode.assume("v_wmma_f16_16x16x128_fp8_bf8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF1616x16x128Fp8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2365
      )
  case object VWmmaF1616x16x128Fp8Fp8 extends Definition(
        Opcode.assume("v_wmma_f16_16x16x128_fp8_fp8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF1616x16x128Fp8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2366
      )
  case object VWmmaF1616x16x16F16 extends Definition(
        Opcode.assume("v_wmma_f16_16x16x16_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF1616x16x16F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2367
      )
  case object VWmmaF1616x16x32F16 extends Definition(
        Opcode.assume("v_wmma_f16_16x16x32_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF1616x16x32F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2368
      )
  case object VWmmaF1616x16x64Bf8Bf8 extends Definition(
        Opcode.assume("v_wmma_f16_16x16x64_bf8_bf8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF1616x16x64Bf8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2369
      )
  case object VWmmaF1616x16x64Bf8Fp8 extends Definition(
        Opcode.assume("v_wmma_f16_16x16x64_bf8_fp8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF1616x16x64Bf8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2370
      )
  case object VWmmaF1616x16x64Fp8Bf8 extends Definition(
        Opcode.assume("v_wmma_f16_16x16x64_fp8_bf8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF1616x16x64Fp8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2371
      )
  case object VWmmaF1616x16x64Fp8Fp8 extends Definition(
        Opcode.assume("v_wmma_f16_16x16x64_fp8_fp8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF1616x16x64Fp8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2372
      )
  case object VWmmaF3216x16x128Bf8Bf8 extends Definition(
        Opcode.assume("v_wmma_f32_16x16x128_bf8_bf8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF3216x16x128Bf8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2373
      )
  case object VWmmaF3216x16x128Bf8Fp8 extends Definition(
        Opcode.assume("v_wmma_f32_16x16x128_bf8_fp8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF3216x16x128Bf8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2374
      )
  case object VWmmaF3216x16x128F8f6f4 extends Definition(
        Opcode.assume("v_wmma_f32_16x16x128_f8f6f4"),
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF3216x16x128F8f6f4(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2375
      )
  case object VWmmaF3216x16x128Fp8Bf8 extends Definition(
        Opcode.assume("v_wmma_f32_16x16x128_fp8_bf8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF3216x16x128Fp8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2376
      )
  case object VWmmaF3216x16x128Fp8Fp8 extends Definition(
        Opcode.assume("v_wmma_f32_16x16x128_fp8_fp8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF3216x16x128Fp8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2377
      )
  case object VWmmaF3216x16x16Bf16 extends Definition(
        Opcode.assume("v_wmma_f32_16x16x16_bf16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF3216x16x16Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2378
      )
  case object VWmmaF3216x16x16Bf8Bf8 extends Definition(
        Opcode.assume("v_wmma_f32_16x16x16_bf8_bf8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF3216x16x16Bf8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2379
      )
  case object VWmmaF3216x16x16Bf8Fp8 extends Definition(
        Opcode.assume("v_wmma_f32_16x16x16_bf8_fp8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF3216x16x16Bf8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2380
      )
  case object VWmmaF3216x16x16F16 extends Definition(
        Opcode.assume("v_wmma_f32_16x16x16_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF3216x16x16F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2381
      )
  case object VWmmaF3216x16x16Fp8Bf8 extends Definition(
        Opcode.assume("v_wmma_f32_16x16x16_fp8_bf8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF3216x16x16Fp8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2382
      )
  case object VWmmaF3216x16x16Fp8Fp8 extends Definition(
        Opcode.assume("v_wmma_f32_16x16x16_fp8_fp8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF3216x16x16Fp8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2383
      )
  case object VWmmaF3216x16x32Bf16 extends Definition(
        Opcode.assume("v_wmma_f32_16x16x32_bf16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF3216x16x32Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2384
      )
  case object VWmmaF3216x16x32F16 extends Definition(
        Opcode.assume("v_wmma_f32_16x16x32_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF3216x16x32F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2385
      )
  case object VWmmaF3216x16x4F32 extends Definition(
        Opcode.assume("v_wmma_f32_16x16x4_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF3216x16x4F32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2386
      )
  case object VWmmaF3216x16x64Bf8Bf8 extends Definition(
        Opcode.assume("v_wmma_f32_16x16x64_bf8_bf8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF3216x16x64Bf8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2387
      )
  case object VWmmaF3216x16x64Bf8Fp8 extends Definition(
        Opcode.assume("v_wmma_f32_16x16x64_bf8_fp8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF3216x16x64Bf8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2388
      )
  case object VWmmaF3216x16x64Fp8Bf8 extends Definition(
        Opcode.assume("v_wmma_f32_16x16x64_fp8_bf8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF3216x16x64Fp8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2389
      )
  case object VWmmaF3216x16x64Fp8Fp8 extends Definition(
        Opcode.assume("v_wmma_f32_16x16x64_fp8_fp8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF3216x16x64Fp8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2390
      )
  case object VWmmaF3232x16x128F4 extends Definition(
        Opcode.assume("v_wmma_f32_32x16x128_f4"),
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaF3232x16x128F4(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2391
      )
  case object VWmmaI3216x16x16Iu4 extends Definition(
        Opcode.assume("v_wmma_i32_16x16x16_iu4"),
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaI3216x16x16Iu4(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2392
      )
  case object VWmmaI3216x16x16Iu8 extends Definition(
        Opcode.assume("v_wmma_i32_16x16x16_iu8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaI3216x16x16Iu8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2393
      )
  case object VWmmaI3216x16x32Iu4 extends Definition(
        Opcode.assume("v_wmma_i32_16x16x32_iu4"),
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaI3216x16x32Iu4(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2394
      )
  case object VWmmaI3216x16x64Iu8 extends Definition(
        Opcode.assume("v_wmma_i32_16x16x64_iu8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VWmmaI3216x16x64Iu8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2395
      )
  case object VWmmaScale16F3216x16x128F8f6f4 extends Definition(
        Opcode.assume("v_wmma_scale16_f32_16x16x128_f8f6f4"),
        decoder(form(6)((operands, mods) =>
          Instruction.VWmmaScale16F3216x16x128F8f6f4(
            operands(0),
            operands(1),
            operands(2),
            operands(3),
            operands(4),
            operands(5),
            mods
          )
        )),
        2396
      )
  case object VWmmaScale16F3232x16x128F4 extends Definition(
        Opcode.assume("v_wmma_scale16_f32_32x16x128_f4"),
        decoder(form(6)((operands, mods) =>
          Instruction.VWmmaScale16F3232x16x128F4(
            operands(0),
            operands(1),
            operands(2),
            operands(3),
            operands(4),
            operands(5),
            mods
          )
        )),
        2397
      )
  case object VWmmaScaleF3216x16x128F8f6f4 extends Definition(
        Opcode.assume("v_wmma_scale_f32_16x16x128_f8f6f4"),
        decoder(form(6)((operands, mods) =>
          Instruction.VWmmaScaleF3216x16x128F8f6f4(
            operands(0),
            operands(1),
            operands(2),
            operands(3),
            operands(4),
            operands(5),
            mods
          )
        )),
        2398
      )
  case object VWmmaScaleF3232x16x128F4 extends Definition(
        Opcode.assume("v_wmma_scale_f32_32x16x128_f4"),
        decoder(form(6)((operands, mods) =>
          Instruction.VWmmaScaleF3232x16x128F4(
            operands(0),
            operands(1),
            operands(2),
            operands(3),
            operands(4),
            operands(5),
            mods
          )
        )),
        2399
      )

  val definitions: Vec[Definition] = Vec(
    SWaitTensorcnt,
    TensorLoadToLds,
    TensorStoreFromLds,
    VMfmaF3216x16x128F8f6f4,
    VMfmaF3216x16x16Bf16,
    VMfmaF3216x16x16F16_1,
    VMfmaF3216x16x16bf161k,
    VMfmaF3216x16x16f16_2,
    VMfmaF3216x16x14bF32,
    VMfmaF3216x16x1f32,
    VMfmaF3216x16x2bf16,
    VMfmaF3216x16x32Bf16,
    VMfmaF3216x16x32Bf8Bf8,
    VMfmaF3216x16x32Bf8Fp8,
    VMfmaF3216x16x32F16,
    VMfmaF3216x16x32Fp8Bf8,
    VMfmaF3216x16x32Fp8Fp8,
    VMfmaF3216x16x44bBf16,
    VMfmaF3216x16x44bF16,
    VMfmaF3216x16x4F32_1,
    VMfmaF3216x16x4bf161k,
    VMfmaF3216x16x4f16,
    VMfmaF3216x16x4f32_2,
    VMfmaF3216x16x8Xf32,
    VMfmaF3216x16x8bf16,
    VMfmaF3232x32x16Bf16,
    VMfmaF3232x32x16Bf8Bf8,
    VMfmaF3232x32x16Bf8Fp8,
    VMfmaF3232x32x16F16,
    VMfmaF3232x32x16Fp8Bf8,
    VMfmaF3232x32x16Fp8Fp8,
    VMfmaF3232x32x12bF32,
    VMfmaF3232x32x1f32,
    VMfmaF3232x32x2F32_1,
    VMfmaF3232x32x2bf16,
    VMfmaF3232x32x2f32_2,
    VMfmaF3232x32x42bBf16,
    VMfmaF3232x32x42bF16,
    VMfmaF3232x32x4Xf32,
    VMfmaF3232x32x4bf16,
    VMfmaF3232x32x4bf161k,
    VMfmaF3232x32x4f16,
    VMfmaF3232x32x64F8f6f4,
    VMfmaF3232x32x8Bf16,
    VMfmaF3232x32x8F16_1,
    VMfmaF3232x32x8bf161k,
    VMfmaF3232x32x8f16_2,
    VMfmaF324x4x116bF32,
    VMfmaF324x4x1f32,
    VMfmaF324x4x2bf16,
    VMfmaF324x4x416bBf16,
    VMfmaF324x4x416bF16,
    VMfmaF324x4x4bf161k,
    VMfmaF324x4x4f16,
    VMfmaF6416x16x4F64_1,
    VMfmaF6416x16x4f64_2,
    VMfmaF644x4x44bF64,
    VMfmaF644x4x4f64,
    VMfmaI3216x16x16i8,
    VMfmaI3216x16x32I8,
    VMfmaI3216x16x44bI8,
    VMfmaI3216x16x4i8,
    VMfmaI3216x16x64I8,
    VMfmaI3232x32x16I8,
    VMfmaI3232x32x32I8,
    VMfmaI3232x32x42bI8,
    VMfmaI3232x32x4i8,
    VMfmaI3232x32x8i8,
    VMfmaI324x4x416bI8,
    VMfmaI324x4x4i8,
    VMfmaScaleF3216x16x128F8f6f4,
    VMfmaScaleF3232x32x64F8f6f4,
    VSmfmacF3216x16x128Bf8Bf8,
    VSmfmacF3216x16x128Bf8Fp8,
    VSmfmacF3216x16x128Fp8Bf8,
    VSmfmacF3216x16x128Fp8Fp8,
    VSmfmacF3216x16x32Bf16,
    VSmfmacF3216x16x32F16,
    VSmfmacF3216x16x64Bf16,
    VSmfmacF3216x16x64Bf8Bf8,
    VSmfmacF3216x16x64Bf8Fp8,
    VSmfmacF3216x16x64F16,
    VSmfmacF3216x16x64Fp8Bf8,
    VSmfmacF3216x16x64Fp8Fp8,
    VSmfmacF3232x32x16Bf16,
    VSmfmacF3232x32x16F16,
    VSmfmacF3232x32x32Bf16,
    VSmfmacF3232x32x32Bf8Bf8,
    VSmfmacF3232x32x32Bf8Fp8,
    VSmfmacF3232x32x32F16,
    VSmfmacF3232x32x32Fp8Bf8,
    VSmfmacF3232x32x32Fp8Fp8,
    VSmfmacF3232x32x64Bf8Bf8,
    VSmfmacF3232x32x64Bf8Fp8,
    VSmfmacF3232x32x64Fp8Bf8,
    VSmfmacF3232x32x64Fp8Fp8,
    VSmfmacI3216x16x128I8,
    VSmfmacI3216x16x64I8,
    VSmfmacI3232x32x32I8,
    VSmfmacI3232x32x64I8,
    VSwmmacBf1616x16x32Bf16,
    VSwmmacBf1616x16x64Bf16,
    VSwmmacBf16f3216x16x64Bf16,
    VSwmmacF1616x16x128Bf8Bf8,
    VSwmmacF1616x16x128Bf8Fp8,
    VSwmmacF1616x16x128Fp8Bf8,
    VSwmmacF1616x16x128Fp8Fp8,
    VSwmmacF1616x16x32F16,
    VSwmmacF1616x16x64F16,
    VSwmmacF3216x16x128Bf8Bf8,
    VSwmmacF3216x16x128Bf8Fp8,
    VSwmmacF3216x16x128Fp8Bf8,
    VSwmmacF3216x16x128Fp8Fp8,
    VSwmmacF3216x16x32Bf16,
    VSwmmacF3216x16x32Bf8Bf8,
    VSwmmacF3216x16x32Bf8Fp8,
    VSwmmacF3216x16x32F16,
    VSwmmacF3216x16x32Fp8Bf8,
    VSwmmacF3216x16x32Fp8Fp8,
    VSwmmacF3216x16x64Bf16,
    VSwmmacF3216x16x64F16,
    VSwmmacI3216x16x128Iu8,
    VSwmmacI3216x16x32Iu4,
    VSwmmacI3216x16x32Iu8,
    VSwmmacI3216x16x64Iu4,
    VWmmaBf1616x16x16Bf16,
    VWmmaBf1616x16x32Bf16,
    VWmmaBf16f3216x16x32Bf16,
    VWmmaF1616x16x128Bf8Bf8,
    VWmmaF1616x16x128Bf8Fp8,
    VWmmaF1616x16x128Fp8Bf8,
    VWmmaF1616x16x128Fp8Fp8,
    VWmmaF1616x16x16F16,
    VWmmaF1616x16x32F16,
    VWmmaF1616x16x64Bf8Bf8,
    VWmmaF1616x16x64Bf8Fp8,
    VWmmaF1616x16x64Fp8Bf8,
    VWmmaF1616x16x64Fp8Fp8,
    VWmmaF3216x16x128Bf8Bf8,
    VWmmaF3216x16x128Bf8Fp8,
    VWmmaF3216x16x128F8f6f4,
    VWmmaF3216x16x128Fp8Bf8,
    VWmmaF3216x16x128Fp8Fp8,
    VWmmaF3216x16x16Bf16,
    VWmmaF3216x16x16Bf8Bf8,
    VWmmaF3216x16x16Bf8Fp8,
    VWmmaF3216x16x16F16,
    VWmmaF3216x16x16Fp8Bf8,
    VWmmaF3216x16x16Fp8Fp8,
    VWmmaF3216x16x32Bf16,
    VWmmaF3216x16x32F16,
    VWmmaF3216x16x4F32,
    VWmmaF3216x16x64Bf8Bf8,
    VWmmaF3216x16x64Bf8Fp8,
    VWmmaF3216x16x64Fp8Bf8,
    VWmmaF3216x16x64Fp8Fp8,
    VWmmaF3232x16x128F4,
    VWmmaI3216x16x16Iu4,
    VWmmaI3216x16x16Iu8,
    VWmmaI3216x16x32Iu4,
    VWmmaI3216x16x64Iu8,
    VWmmaScale16F3216x16x128F8f6f4,
    VWmmaScale16F3232x16x128F4,
    VWmmaScaleF3216x16x128F8f6f4,
    VWmmaScaleF3232x16x128F4
  )
}
