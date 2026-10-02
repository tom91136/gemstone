// Generated from the pinned AMDGPU instruction forms. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu.asm
import net.kurobako.gemstone.isa.amdgpu.*
import Instruction.*
private object MatrixInstructionForms {
  val decoders: Map[Opcode, Decoder] = Map(
    MatrixInstructionDefs.SWaitTensorcnt ->
      decoder(form(1)((operands, mods) => Instruction.SWaitTensorcnt(operands(0), mods))),
    MatrixInstructionDefs.TensorLoadToLds -> decoder(form(4)((operands, mods) =>
      Instruction.TensorLoadToLds(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.TensorSave -> decoder(form(1)((operands, mods) => Instruction.TensorSave(operands(0), mods))),
    MatrixInstructionDefs.TensorStop -> decoder(form(0)((_, mods) => Instruction.TensorStop(mods))),
    MatrixInstructionDefs.TensorStoreFromLds -> decoder(form(4)((operands, mods) =>
      Instruction.TensorStoreFromLds(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaF3216x16x128F8f6f4 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaF3216x16x128F8f6f4(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaF3216x16x16Bf16 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaF3216x16x16Bf16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaF3216x16x16F16_1 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaF3216x16x16F16_1(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaF3216x16x16bf161k -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaF3216x16x16bf161k(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaF3216x16x16f16_2 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaF3216x16x16f16_2(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaF3216x16x14bF32 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaF3216x16x14bF32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaF3216x16x1f32 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaF3216x16x1f32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaF3216x16x2bf16 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaF3216x16x2bf16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaF3216x16x32Bf16 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaF3216x16x32Bf16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaF3216x16x32Bf8Bf8 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaF3216x16x32Bf8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaF3216x16x32Bf8Fp8 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaF3216x16x32Bf8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaF3216x16x32F16 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaF3216x16x32F16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaF3216x16x32Fp8Bf8 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaF3216x16x32Fp8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaF3216x16x32Fp8Fp8 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaF3216x16x32Fp8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaF3216x16x44bBf16 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaF3216x16x44bBf16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaF3216x16x44bF16 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaF3216x16x44bF16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaF3216x16x4F32_1 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaF3216x16x4F32_1(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaF3216x16x4bf161k -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaF3216x16x4bf161k(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaF3216x16x4f16 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaF3216x16x4f16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaF3216x16x4f32_2 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaF3216x16x4f32_2(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaF3216x16x8Xf32 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaF3216x16x8Xf32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaF3216x16x8bf16 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaF3216x16x8bf16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaF3232x32x16Bf16 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaF3232x32x16Bf16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaF3232x32x16Bf8Bf8 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaF3232x32x16Bf8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaF3232x32x16Bf8Fp8 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaF3232x32x16Bf8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaF3232x32x16F16 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaF3232x32x16F16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaF3232x32x16Fp8Bf8 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaF3232x32x16Fp8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaF3232x32x16Fp8Fp8 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaF3232x32x16Fp8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaF3232x32x12bF32 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaF3232x32x12bF32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaF3232x32x1f32 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaF3232x32x1f32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaF3232x32x2F32_1 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaF3232x32x2F32_1(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaF3232x32x2bf16 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaF3232x32x2bf16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaF3232x32x2f32_2 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaF3232x32x2f32_2(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaF3232x32x42bBf16 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaF3232x32x42bBf16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaF3232x32x42bF16 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaF3232x32x42bF16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaF3232x32x4Xf32 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaF3232x32x4Xf32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaF3232x32x4bf16 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaF3232x32x4bf16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaF3232x32x4bf161k -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaF3232x32x4bf161k(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaF3232x32x4f16 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaF3232x32x4f16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaF3232x32x64F8f6f4 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaF3232x32x64F8f6f4(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaF3232x32x8Bf16 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaF3232x32x8Bf16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaF3232x32x8F16_1 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaF3232x32x8F16_1(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaF3232x32x8bf161k -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaF3232x32x8bf161k(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaF3232x32x8f16_2 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaF3232x32x8f16_2(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaF324x4x116bF32 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaF324x4x116bF32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaF324x4x1f32 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaF324x4x1f32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaF324x4x2bf16 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaF324x4x2bf16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaF324x4x416bBf16 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaF324x4x416bBf16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaF324x4x416bF16 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaF324x4x416bF16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaF324x4x4bf161k -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaF324x4x4bf161k(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaF324x4x4f16 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaF324x4x4f16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaF6416x16x4F64_1 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaF6416x16x4F64_1(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaF6416x16x4f64_2 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaF6416x16x4f64_2(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaF644x4x44bF64 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaF644x4x44bF64(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaF644x4x4f64 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaF644x4x4f64(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaI3216x16x16i8 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaI3216x16x16i8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaI3216x16x32I8 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaI3216x16x32I8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaI3216x16x44bI8 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaI3216x16x44bI8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaI3216x16x4i8 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaI3216x16x4i8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaI3216x16x64I8 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaI3216x16x64I8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaI3232x32x16I8 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaI3232x32x16I8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaI3232x32x32I8 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaI3232x32x32I8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaI3232x32x42bI8 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaI3232x32x42bI8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaI3232x32x4i8 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaI3232x32x4i8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaI3232x32x8i8 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaI3232x32x8i8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaI324x4x416bI8 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaI324x4x416bI8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaI324x4x4i8 -> decoder(form(4)((operands, mods) =>
      Instruction.VMfmaI324x4x4i8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VMfmaLdScaleB32 ->
      decoder(form(2)((operands, mods) => Instruction.VMfmaLdScaleB32(operands(0), operands(1), mods))),
    MatrixInstructionDefs.VMfmaScaleF3216x16x128F8f6f4 -> decoder(form(6)((operands, mods) =>
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
    MatrixInstructionDefs.VMfmaScaleF3232x32x64F8f6f4 -> decoder(form(6)((operands, mods) =>
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
    MatrixInstructionDefs.VSmfmacF3216x16x128Bf8Bf8 -> decoder(form(4)((operands, mods) =>
      Instruction.VSmfmacF3216x16x128Bf8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VSmfmacF3216x16x128Bf8Fp8 -> decoder(form(4)((operands, mods) =>
      Instruction.VSmfmacF3216x16x128Bf8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VSmfmacF3216x16x128Fp8Bf8 -> decoder(form(4)((operands, mods) =>
      Instruction.VSmfmacF3216x16x128Fp8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VSmfmacF3216x16x128Fp8Fp8 -> decoder(form(4)((operands, mods) =>
      Instruction.VSmfmacF3216x16x128Fp8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VSmfmacF3216x16x32Bf16 -> decoder(form(4)((operands, mods) =>
      Instruction.VSmfmacF3216x16x32Bf16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VSmfmacF3216x16x32F16 -> decoder(form(4)((operands, mods) =>
      Instruction.VSmfmacF3216x16x32F16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VSmfmacF3216x16x64Bf16 -> decoder(form(4)((operands, mods) =>
      Instruction.VSmfmacF3216x16x64Bf16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VSmfmacF3216x16x64Bf8Bf8 -> decoder(form(4)((operands, mods) =>
      Instruction.VSmfmacF3216x16x64Bf8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VSmfmacF3216x16x64Bf8Fp8 -> decoder(form(4)((operands, mods) =>
      Instruction.VSmfmacF3216x16x64Bf8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VSmfmacF3216x16x64F16 -> decoder(form(4)((operands, mods) =>
      Instruction.VSmfmacF3216x16x64F16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VSmfmacF3216x16x64Fp8Bf8 -> decoder(form(4)((operands, mods) =>
      Instruction.VSmfmacF3216x16x64Fp8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VSmfmacF3216x16x64Fp8Fp8 -> decoder(form(4)((operands, mods) =>
      Instruction.VSmfmacF3216x16x64Fp8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VSmfmacF3232x32x16Bf16 -> decoder(form(4)((operands, mods) =>
      Instruction.VSmfmacF3232x32x16Bf16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VSmfmacF3232x32x16F16 -> decoder(form(4)((operands, mods) =>
      Instruction.VSmfmacF3232x32x16F16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VSmfmacF3232x32x32Bf16 -> decoder(form(4)((operands, mods) =>
      Instruction.VSmfmacF3232x32x32Bf16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VSmfmacF3232x32x32Bf8Bf8 -> decoder(form(4)((operands, mods) =>
      Instruction.VSmfmacF3232x32x32Bf8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VSmfmacF3232x32x32Bf8Fp8 -> decoder(form(4)((operands, mods) =>
      Instruction.VSmfmacF3232x32x32Bf8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VSmfmacF3232x32x32F16 -> decoder(form(4)((operands, mods) =>
      Instruction.VSmfmacF3232x32x32F16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VSmfmacF3232x32x32Fp8Bf8 -> decoder(form(4)((operands, mods) =>
      Instruction.VSmfmacF3232x32x32Fp8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VSmfmacF3232x32x32Fp8Fp8 -> decoder(form(4)((operands, mods) =>
      Instruction.VSmfmacF3232x32x32Fp8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VSmfmacF3232x32x64Bf8Bf8 -> decoder(form(4)((operands, mods) =>
      Instruction.VSmfmacF3232x32x64Bf8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VSmfmacF3232x32x64Bf8Fp8 -> decoder(form(4)((operands, mods) =>
      Instruction.VSmfmacF3232x32x64Bf8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VSmfmacF3232x32x64Fp8Bf8 -> decoder(form(4)((operands, mods) =>
      Instruction.VSmfmacF3232x32x64Fp8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VSmfmacF3232x32x64Fp8Fp8 -> decoder(form(4)((operands, mods) =>
      Instruction.VSmfmacF3232x32x64Fp8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VSmfmacI3216x16x128I8 -> decoder(form(4)((operands, mods) =>
      Instruction.VSmfmacI3216x16x128I8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VSmfmacI3216x16x64I8 -> decoder(form(4)((operands, mods) =>
      Instruction.VSmfmacI3216x16x64I8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VSmfmacI3232x32x32I8 -> decoder(form(4)((operands, mods) =>
      Instruction.VSmfmacI3232x32x32I8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VSmfmacI3232x32x64I8 -> decoder(form(4)((operands, mods) =>
      Instruction.VSmfmacI3232x32x64I8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VSwmmacBf1616x16x32Bf16 -> decoder(form(4)((operands, mods) =>
      Instruction.VSwmmacBf1616x16x32Bf16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VSwmmacBf1616x16x64Bf16 -> decoder(form(4)((operands, mods) =>
      Instruction.VSwmmacBf1616x16x64Bf16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VSwmmacBf16f3216x16x64Bf16 -> decoder(form(4)((operands, mods) =>
      Instruction.VSwmmacBf16f3216x16x64Bf16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VSwmmacF1616x16x128Bf8Bf8 -> decoder(form(4)((operands, mods) =>
      Instruction.VSwmmacF1616x16x128Bf8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VSwmmacF1616x16x128Bf8Fp8 -> decoder(form(4)((operands, mods) =>
      Instruction.VSwmmacF1616x16x128Bf8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VSwmmacF1616x16x128Fp8Bf8 -> decoder(form(4)((operands, mods) =>
      Instruction.VSwmmacF1616x16x128Fp8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VSwmmacF1616x16x128Fp8Fp8 -> decoder(form(4)((operands, mods) =>
      Instruction.VSwmmacF1616x16x128Fp8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VSwmmacF1616x16x32F16 -> decoder(form(4)((operands, mods) =>
      Instruction.VSwmmacF1616x16x32F16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VSwmmacF1616x16x64F16 -> decoder(form(4)((operands, mods) =>
      Instruction.VSwmmacF1616x16x64F16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VSwmmacF3216x16x128Bf8Bf8 -> decoder(form(4)((operands, mods) =>
      Instruction.VSwmmacF3216x16x128Bf8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VSwmmacF3216x16x128Bf8Fp8 -> decoder(form(4)((operands, mods) =>
      Instruction.VSwmmacF3216x16x128Bf8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VSwmmacF3216x16x128Fp8Bf8 -> decoder(form(4)((operands, mods) =>
      Instruction.VSwmmacF3216x16x128Fp8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VSwmmacF3216x16x128Fp8Fp8 -> decoder(form(4)((operands, mods) =>
      Instruction.VSwmmacF3216x16x128Fp8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VSwmmacF3216x16x32Bf16 -> decoder(form(4)((operands, mods) =>
      Instruction.VSwmmacF3216x16x32Bf16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VSwmmacF3216x16x32Bf8Bf8 -> decoder(form(4)((operands, mods) =>
      Instruction.VSwmmacF3216x16x32Bf8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VSwmmacF3216x16x32Bf8Fp8 -> decoder(form(4)((operands, mods) =>
      Instruction.VSwmmacF3216x16x32Bf8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VSwmmacF3216x16x32F16 -> decoder(form(4)((operands, mods) =>
      Instruction.VSwmmacF3216x16x32F16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VSwmmacF3216x16x32Fp8Bf8 -> decoder(form(4)((operands, mods) =>
      Instruction.VSwmmacF3216x16x32Fp8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VSwmmacF3216x16x32Fp8Fp8 -> decoder(form(4)((operands, mods) =>
      Instruction.VSwmmacF3216x16x32Fp8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VSwmmacF3216x16x64Bf16 -> decoder(form(4)((operands, mods) =>
      Instruction.VSwmmacF3216x16x64Bf16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VSwmmacF3216x16x64F16 -> decoder(form(4)((operands, mods) =>
      Instruction.VSwmmacF3216x16x64F16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VSwmmacI3216x16x128Iu8 -> decoder(form(4)((operands, mods) =>
      Instruction.VSwmmacI3216x16x128Iu8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VSwmmacI3216x16x32Iu4 -> decoder(form(4)((operands, mods) =>
      Instruction.VSwmmacI3216x16x32Iu4(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VSwmmacI3216x16x32Iu8 -> decoder(form(4)((operands, mods) =>
      Instruction.VSwmmacI3216x16x32Iu8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VSwmmacI3216x16x64Iu4 -> decoder(form(4)((operands, mods) =>
      Instruction.VSwmmacI3216x16x64Iu4(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VWmmaBf1616x16x16Bf16 -> decoder(form(4)((operands, mods) =>
      Instruction.VWmmaBf1616x16x16Bf16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VWmmaBf1616x16x32Bf16 -> decoder(form(4)((operands, mods) =>
      Instruction.VWmmaBf1616x16x32Bf16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VWmmaBf16f3216x16x32Bf16 -> decoder(form(4)((operands, mods) =>
      Instruction.VWmmaBf16f3216x16x32Bf16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VWmmaF1616x16x128Bf8Bf8 -> decoder(form(4)((operands, mods) =>
      Instruction.VWmmaF1616x16x128Bf8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VWmmaF1616x16x128Bf8Fp8 -> decoder(form(4)((operands, mods) =>
      Instruction.VWmmaF1616x16x128Bf8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VWmmaF1616x16x128Fp8Bf8 -> decoder(form(4)((operands, mods) =>
      Instruction.VWmmaF1616x16x128Fp8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VWmmaF1616x16x128Fp8Fp8 -> decoder(form(4)((operands, mods) =>
      Instruction.VWmmaF1616x16x128Fp8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VWmmaF1616x16x16F16 -> decoder(form(4)((operands, mods) =>
      Instruction.VWmmaF1616x16x16F16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VWmmaF1616x16x32F16 -> decoder(form(4)((operands, mods) =>
      Instruction.VWmmaF1616x16x32F16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VWmmaF1616x16x64Bf8Bf8 -> decoder(form(4)((operands, mods) =>
      Instruction.VWmmaF1616x16x64Bf8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VWmmaF1616x16x64Bf8Fp8 -> decoder(form(4)((operands, mods) =>
      Instruction.VWmmaF1616x16x64Bf8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VWmmaF1616x16x64Fp8Bf8 -> decoder(form(4)((operands, mods) =>
      Instruction.VWmmaF1616x16x64Fp8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VWmmaF1616x16x64Fp8Fp8 -> decoder(form(4)((operands, mods) =>
      Instruction.VWmmaF1616x16x64Fp8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VWmmaF3216x16x128Bf8Bf8 -> decoder(form(4)((operands, mods) =>
      Instruction.VWmmaF3216x16x128Bf8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VWmmaF3216x16x128Bf8Fp8 -> decoder(form(4)((operands, mods) =>
      Instruction.VWmmaF3216x16x128Bf8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VWmmaF3216x16x128F8f6f4 -> decoder(form(4)((operands, mods) =>
      Instruction.VWmmaF3216x16x128F8f6f4(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VWmmaF3216x16x128Fp8Bf8 -> decoder(form(4)((operands, mods) =>
      Instruction.VWmmaF3216x16x128Fp8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VWmmaF3216x16x128Fp8Fp8 -> decoder(form(4)((operands, mods) =>
      Instruction.VWmmaF3216x16x128Fp8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VWmmaF3216x16x16Bf16 -> decoder(form(4)((operands, mods) =>
      Instruction.VWmmaF3216x16x16Bf16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VWmmaF3216x16x16Bf8Bf8 -> decoder(form(4)((operands, mods) =>
      Instruction.VWmmaF3216x16x16Bf8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VWmmaF3216x16x16Bf8Fp8 -> decoder(form(4)((operands, mods) =>
      Instruction.VWmmaF3216x16x16Bf8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VWmmaF3216x16x16F16 -> decoder(form(4)((operands, mods) =>
      Instruction.VWmmaF3216x16x16F16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VWmmaF3216x16x16Fp8Bf8 -> decoder(form(4)((operands, mods) =>
      Instruction.VWmmaF3216x16x16Fp8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VWmmaF3216x16x16Fp8Fp8 -> decoder(form(4)((operands, mods) =>
      Instruction.VWmmaF3216x16x16Fp8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VWmmaF3216x16x32Bf16 -> decoder(form(4)((operands, mods) =>
      Instruction.VWmmaF3216x16x32Bf16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VWmmaF3216x16x32F16 -> decoder(form(4)((operands, mods) =>
      Instruction.VWmmaF3216x16x32F16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VWmmaF3216x16x4F32 -> decoder(form(4)((operands, mods) =>
      Instruction.VWmmaF3216x16x4F32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VWmmaF3216x16x64Bf8Bf8 -> decoder(form(4)((operands, mods) =>
      Instruction.VWmmaF3216x16x64Bf8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VWmmaF3216x16x64Bf8Fp8 -> decoder(form(4)((operands, mods) =>
      Instruction.VWmmaF3216x16x64Bf8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VWmmaF3216x16x64Fp8Bf8 -> decoder(form(4)((operands, mods) =>
      Instruction.VWmmaF3216x16x64Fp8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VWmmaF3216x16x64Fp8Fp8 -> decoder(form(4)((operands, mods) =>
      Instruction.VWmmaF3216x16x64Fp8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VWmmaF3232x16x128F4 -> decoder(form(4)((operands, mods) =>
      Instruction.VWmmaF3232x16x128F4(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VWmmaF6416x16x4F64 -> decoder(form(4)((operands, mods) =>
      Instruction.VWmmaF6416x16x4F64(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VWmmaI3216x16x16Iu4 -> decoder(form(4)((operands, mods) =>
      Instruction.VWmmaI3216x16x16Iu4(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VWmmaI3216x16x16Iu8 -> decoder(form(4)((operands, mods) =>
      Instruction.VWmmaI3216x16x16Iu8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VWmmaI3216x16x32Iu4 -> decoder(form(4)((operands, mods) =>
      Instruction.VWmmaI3216x16x32Iu4(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VWmmaI3216x16x64Iu8 -> decoder(form(4)((operands, mods) =>
      Instruction.VWmmaI3216x16x64Iu8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    MatrixInstructionDefs.VWmmaLdScale16PairedB64 ->
      decoder(form(2)((operands, mods) => Instruction.VWmmaLdScale16PairedB64(operands(0), operands(1), mods))),
    MatrixInstructionDefs.VWmmaLdScalePairedB32 ->
      decoder(form(2)((operands, mods) => Instruction.VWmmaLdScalePairedB32(operands(0), operands(1), mods))),
    MatrixInstructionDefs.VWmmaScale16F3216x16x128F8f6f4 -> decoder(form(6)((operands, mods) =>
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
    MatrixInstructionDefs.VWmmaScale16F3232x16x128F4 -> decoder(form(6)((operands, mods) =>
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
    MatrixInstructionDefs.VWmmaScaleF3216x16x128F8f6f4 -> decoder(form(6)((operands, mods) =>
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
    MatrixInstructionDefs.VWmmaScaleF3232x16x128F4 -> decoder(form(6)((operands, mods) =>
      Instruction.VWmmaScaleF3232x16x128F4(
        operands(0),
        operands(1),
        operands(2),
        operands(3),
        operands(4),
        operands(5),
        mods
      )
    ))
  )
}
