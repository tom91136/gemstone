// Generated from the pinned AMDGPU instruction forms. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu.asm
import net.kurobako.gemstone.isa.amdgpu.*
import Instruction.*
private object VOP3InstructionForms {
  val decoders: Map[Opcode, Decoder] = Map(
    VOP3InstructionDefs.VAdd3U32 -> decoder(form(4)((operands, mods) =>
      Instruction.VAdd3U32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VAddI16 ->
      decoder(form(3)((operands, mods) => Instruction.VAddI16(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VAddLshlU32 -> decoder(form(4)((operands, mods) =>
      Instruction.VAddLshlU32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VAddMaxI32 -> decoder(form(4)((operands, mods) =>
      Instruction.VAddMaxI32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VAddMaxU32 -> decoder(form(4)((operands, mods) =>
      Instruction.VAddMaxU32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VAddMinI32 -> decoder(form(4)((operands, mods) =>
      Instruction.VAddMinI32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VAddMinU32 -> decoder(form(4)((operands, mods) =>
      Instruction.VAddMinU32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VAddNcI16 ->
      decoder(form(3)((operands, mods) => Instruction.VAddNcI16(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VAddNcI32 ->
      decoder(form(3)((operands, mods) => Instruction.VAddNcI32(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VAddNcU16 ->
      decoder(form(3)((operands, mods) => Instruction.VAddNcU16(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VAlignbitB32 -> decoder(form(4)((operands, mods) =>
      Instruction.VAlignbitB32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VAlignbyteB32 -> decoder(form(4)((operands, mods) =>
      Instruction.VAlignbyteB32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VAndB16 ->
      decoder(form(3)((operands, mods) => Instruction.VAndB16(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VAndOrB32 -> decoder(form(4)((operands, mods) =>
      Instruction.VAndOrB32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VAshrI64 ->
      decoder(form(3)((operands, mods) => Instruction.VAshrI64(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VAshrPkI8I32 -> decoder(form(4)((operands, mods) =>
      Instruction.VAshrPkI8I32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VAshrPkU8I32 -> decoder(form(4)((operands, mods) =>
      Instruction.VAshrPkU8I32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VAshrrevI64 ->
      decoder(form(3)((operands, mods) => Instruction.VAshrrevI64(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VBfeI32 -> decoder(form(4)((operands, mods) =>
      Instruction.VBfeI32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VBfeU32 -> decoder(form(4)((operands, mods) =>
      Instruction.VBfeU32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VBfiB32 -> decoder(form(4)((operands, mods) =>
      Instruction.VBfiB32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VBitop3B16 -> decoder(form(4)((operands, mods) =>
      Instruction.VBitop3B16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VBitop3B32 -> decoder(form(4)((operands, mods) =>
      Instruction.VBitop3B32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VCndmaskB16 -> decoder(form(4)((operands, mods) =>
      Instruction.VCndmaskB16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VCubeidF32 -> decoder(form(4)((operands, mods) =>
      Instruction.VCubeidF32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VCubemaF32 -> decoder(form(4)((operands, mods) =>
      Instruction.VCubemaF32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VCubescF32 -> decoder(form(4)((operands, mods) =>
      Instruction.VCubescF32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VCubetcF32 -> decoder(form(4)((operands, mods) =>
      Instruction.VCubetcF32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VCvtPkBf16F32 ->
      decoder(form(3)((operands, mods) => Instruction.VCvtPkBf16F32(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VCvtPkBf8F16 ->
      decoder(form(2)((operands, mods) => Instruction.VCvtPkBf8F16(operands(0), operands(1), mods))),
    VOP3InstructionDefs.VCvtPkBf8F32 ->
      decoder(form(3)((operands, mods) => Instruction.VCvtPkBf8F32(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VCvtPkF16F32 ->
      decoder(form(3)((operands, mods) => Instruction.VCvtPkF16F32(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VCvtPkFp8F16 ->
      decoder(form(2)((operands, mods) => Instruction.VCvtPkFp8F16(operands(0), operands(1), mods))),
    VOP3InstructionDefs.VCvtPkFp8F32 ->
      decoder(form(3)((operands, mods) => Instruction.VCvtPkFp8F32(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VCvtPkI16F32 ->
      decoder(form(3)((operands, mods) => Instruction.VCvtPkI16F32(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VCvtPkNormI16F16_1 ->
      decoder(form(3)((operands, mods) => Instruction.VCvtPkNormI16F16_1(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VCvtPkNormI16F32_1 ->
      decoder(form(3)((operands, mods) => Instruction.VCvtPkNormI16F32_1(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VCvtPkNormU16F16_1 ->
      decoder(form(3)((operands, mods) => Instruction.VCvtPkNormU16F16_1(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VCvtPkNormU16F32_1 ->
      decoder(form(3)((operands, mods) => Instruction.VCvtPkNormU16F32_1(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VCvtPkU16F32 ->
      decoder(form(3)((operands, mods) => Instruction.VCvtPkU16F32(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VCvtPkU8F32 -> decoder(form(4)((operands, mods) =>
      Instruction.VCvtPkU8F32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VCvtPknormI16F16_2 ->
      decoder(form(3)((operands, mods) => Instruction.VCvtPknormI16F16_2(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VCvtPknormU16F16_2 ->
      decoder(form(3)((operands, mods) => Instruction.VCvtPknormU16F16_2(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VCvtScalePk16Bf16Bf6 -> decoder(form(3)((operands, mods) =>
      Instruction.VCvtScalePk16Bf16Bf6(operands(0), operands(1), operands(2), mods)
    )),
    VOP3InstructionDefs.VCvtScalePk16Bf16Fp6 -> decoder(form(3)((operands, mods) =>
      Instruction.VCvtScalePk16Bf16Fp6(operands(0), operands(1), operands(2), mods)
    )),
    VOP3InstructionDefs.VCvtScalePk16F16Bf6 -> decoder(form(3)((operands, mods) =>
      Instruction.VCvtScalePk16F16Bf6(operands(0), operands(1), operands(2), mods)
    )),
    VOP3InstructionDefs.VCvtScalePk16F16Fp6 -> decoder(form(3)((operands, mods) =>
      Instruction.VCvtScalePk16F16Fp6(operands(0), operands(1), operands(2), mods)
    )),
    VOP3InstructionDefs.VCvtScalePk16F32Bf6 -> decoder(form(3)((operands, mods) =>
      Instruction.VCvtScalePk16F32Bf6(operands(0), operands(1), operands(2), mods)
    )),
    VOP3InstructionDefs.VCvtScalePk16F32Fp6 -> decoder(form(3)((operands, mods) =>
      Instruction.VCvtScalePk16F32Fp6(operands(0), operands(1), operands(2), mods)
    )),
    VOP3InstructionDefs.VCvtScalePk8Bf16Bf8 -> decoder(form(3)((operands, mods) =>
      Instruction.VCvtScalePk8Bf16Bf8(operands(0), operands(1), operands(2), mods)
    )),
    VOP3InstructionDefs.VCvtScalePk8Bf16Fp4 -> decoder(form(3)((operands, mods) =>
      Instruction.VCvtScalePk8Bf16Fp4(operands(0), operands(1), operands(2), mods)
    )),
    VOP3InstructionDefs.VCvtScalePk8Bf16Fp8 -> decoder(form(3)((operands, mods) =>
      Instruction.VCvtScalePk8Bf16Fp8(operands(0), operands(1), operands(2), mods)
    )),
    VOP3InstructionDefs.VCvtScalePk8F16Bf8 ->
      decoder(form(3)((operands, mods) => Instruction.VCvtScalePk8F16Bf8(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VCvtScalePk8F16Fp4 ->
      decoder(form(3)((operands, mods) => Instruction.VCvtScalePk8F16Fp4(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VCvtScalePk8F16Fp8 ->
      decoder(form(3)((operands, mods) => Instruction.VCvtScalePk8F16Fp8(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VCvtScalePk8F32Bf8 ->
      decoder(form(3)((operands, mods) => Instruction.VCvtScalePk8F32Bf8(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VCvtScalePk8F32Fp4 ->
      decoder(form(3)((operands, mods) => Instruction.VCvtScalePk8F32Fp4(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VCvtScalePk8F32Fp8 ->
      decoder(form(3)((operands, mods) => Instruction.VCvtScalePk8F32Fp8(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VCvtScalef322xpk16Bf6F32 -> decoder(form(4)((operands, mods) =>
      Instruction.VCvtScalef322xpk16Bf6F32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VCvtScalef322xpk16Fp6F32 -> decoder(form(4)((operands, mods) =>
      Instruction.VCvtScalef322xpk16Fp6F32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32F16Bf8 ->
      decoder(form(3)((operands, mods) => Instruction.VCvtScalef32F16Bf8(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VCvtScalef32F16Fp8 ->
      decoder(form(3)((operands, mods) => Instruction.VCvtScalef32F16Fp8(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VCvtScalef32F32Bf8 ->
      decoder(form(3)((operands, mods) => Instruction.VCvtScalef32F32Bf8(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VCvtScalef32F32Fp8 ->
      decoder(form(3)((operands, mods) => Instruction.VCvtScalef32F32Fp8(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VCvtScalef32Pk16Bf6Bf16 -> decoder(form(3)((operands, mods) =>
      Instruction.VCvtScalef32Pk16Bf6Bf16(operands(0), operands(1), operands(2), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32Pk16Bf6F16 -> decoder(form(3)((operands, mods) =>
      Instruction.VCvtScalef32Pk16Bf6F16(operands(0), operands(1), operands(2), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32Pk16Bf6F32 -> decoder(form(3)((operands, mods) =>
      Instruction.VCvtScalef32Pk16Bf6F32(operands(0), operands(1), operands(2), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32Pk16Fp6Bf16 -> decoder(form(3)((operands, mods) =>
      Instruction.VCvtScalef32Pk16Fp6Bf16(operands(0), operands(1), operands(2), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32Pk16Fp6F16 -> decoder(form(3)((operands, mods) =>
      Instruction.VCvtScalef32Pk16Fp6F16(operands(0), operands(1), operands(2), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32Pk16Fp6F32 -> decoder(form(3)((operands, mods) =>
      Instruction.VCvtScalef32Pk16Fp6F32(operands(0), operands(1), operands(2), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32Pk32Bf16Bf6 -> decoder(form(3)((operands, mods) =>
      Instruction.VCvtScalef32Pk32Bf16Bf6(operands(0), operands(1), operands(2), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32Pk32Bf16Fp6 -> decoder(form(3)((operands, mods) =>
      Instruction.VCvtScalef32Pk32Bf16Fp6(operands(0), operands(1), operands(2), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32Pk32Bf6Bf16 -> decoder(form(3)((operands, mods) =>
      Instruction.VCvtScalef32Pk32Bf6Bf16(operands(0), operands(1), operands(2), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32Pk32Bf6F16 -> decoder(form(3)((operands, mods) =>
      Instruction.VCvtScalef32Pk32Bf6F16(operands(0), operands(1), operands(2), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32Pk32F16Bf6 -> decoder(form(3)((operands, mods) =>
      Instruction.VCvtScalef32Pk32F16Bf6(operands(0), operands(1), operands(2), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32Pk32F16Fp6 -> decoder(form(3)((operands, mods) =>
      Instruction.VCvtScalef32Pk32F16Fp6(operands(0), operands(1), operands(2), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32Pk32F32Bf6 -> decoder(form(3)((operands, mods) =>
      Instruction.VCvtScalef32Pk32F32Bf6(operands(0), operands(1), operands(2), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32Pk32F32Fp6 -> decoder(form(3)((operands, mods) =>
      Instruction.VCvtScalef32Pk32F32Fp6(operands(0), operands(1), operands(2), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32Pk32Fp6Bf16 -> decoder(form(3)((operands, mods) =>
      Instruction.VCvtScalef32Pk32Fp6Bf16(operands(0), operands(1), operands(2), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32Pk32Fp6F16 -> decoder(form(3)((operands, mods) =>
      Instruction.VCvtScalef32Pk32Fp6F16(operands(0), operands(1), operands(2), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32Pk8Bf8Bf16 -> decoder(form(3)((operands, mods) =>
      Instruction.VCvtScalef32Pk8Bf8Bf16(operands(0), operands(1), operands(2), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32Pk8Bf8F16 -> decoder(form(3)((operands, mods) =>
      Instruction.VCvtScalef32Pk8Bf8F16(operands(0), operands(1), operands(2), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32Pk8Bf8F32 -> decoder(form(3)((operands, mods) =>
      Instruction.VCvtScalef32Pk8Bf8F32(operands(0), operands(1), operands(2), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32Pk8Fp4Bf16 -> decoder(form(3)((operands, mods) =>
      Instruction.VCvtScalef32Pk8Fp4Bf16(operands(0), operands(1), operands(2), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32Pk8Fp4F16 -> decoder(form(3)((operands, mods) =>
      Instruction.VCvtScalef32Pk8Fp4F16(operands(0), operands(1), operands(2), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32Pk8Fp4F32 -> decoder(form(3)((operands, mods) =>
      Instruction.VCvtScalef32Pk8Fp4F32(operands(0), operands(1), operands(2), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32Pk8Fp8Bf16 -> decoder(form(3)((operands, mods) =>
      Instruction.VCvtScalef32Pk8Fp8Bf16(operands(0), operands(1), operands(2), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32Pk8Fp8F16 -> decoder(form(3)((operands, mods) =>
      Instruction.VCvtScalef32Pk8Fp8F16(operands(0), operands(1), operands(2), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32Pk8Fp8F32 -> decoder(form(3)((operands, mods) =>
      Instruction.VCvtScalef32Pk8Fp8F32(operands(0), operands(1), operands(2), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32PkBf16Bf8 -> decoder(form(3)((operands, mods) =>
      Instruction.VCvtScalef32PkBf16Bf8(operands(0), operands(1), operands(2), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32PkBf16Fp4 -> decoder(form(3)((operands, mods) =>
      Instruction.VCvtScalef32PkBf16Fp4(operands(0), operands(1), operands(2), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32PkBf16Fp8 -> decoder(form(3)((operands, mods) =>
      Instruction.VCvtScalef32PkBf16Fp8(operands(0), operands(1), operands(2), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32PkBf8Bf16 -> decoder(form(3)((operands, mods) =>
      Instruction.VCvtScalef32PkBf8Bf16(operands(0), operands(1), operands(2), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32PkBf8F16 -> decoder(form(3)((operands, mods) =>
      Instruction.VCvtScalef32PkBf8F16(operands(0), operands(1), operands(2), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32PkBf8F32 -> decoder(form(4)((operands, mods) =>
      Instruction.VCvtScalef32PkBf8F32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32PkF16Bf8 -> decoder(form(3)((operands, mods) =>
      Instruction.VCvtScalef32PkF16Bf8(operands(0), operands(1), operands(2), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32PkF16Fp4 -> decoder(form(3)((operands, mods) =>
      Instruction.VCvtScalef32PkF16Fp4(operands(0), operands(1), operands(2), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32PkF16Fp8 -> decoder(form(3)((operands, mods) =>
      Instruction.VCvtScalef32PkF16Fp8(operands(0), operands(1), operands(2), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32PkF32Bf8 -> decoder(form(3)((operands, mods) =>
      Instruction.VCvtScalef32PkF32Bf8(operands(0), operands(1), operands(2), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32PkF32Fp4 -> decoder(form(3)((operands, mods) =>
      Instruction.VCvtScalef32PkF32Fp4(operands(0), operands(1), operands(2), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32PkF32Fp8 -> decoder(form(3)((operands, mods) =>
      Instruction.VCvtScalef32PkF32Fp8(operands(0), operands(1), operands(2), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32PkFp4Bf16 -> decoder(form(3)((operands, mods) =>
      Instruction.VCvtScalef32PkFp4Bf16(operands(0), operands(1), operands(2), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32PkFp4F16 -> decoder(form(3)((operands, mods) =>
      Instruction.VCvtScalef32PkFp4F16(operands(0), operands(1), operands(2), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32PkFp4F32 -> decoder(form(4)((operands, mods) =>
      Instruction.VCvtScalef32PkFp4F32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32PkFp8Bf16 -> decoder(form(3)((operands, mods) =>
      Instruction.VCvtScalef32PkFp8Bf16(operands(0), operands(1), operands(2), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32PkFp8F16 -> decoder(form(3)((operands, mods) =>
      Instruction.VCvtScalef32PkFp8F16(operands(0), operands(1), operands(2), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32PkFp8F32 -> decoder(form(4)((operands, mods) =>
      Instruction.VCvtScalef32PkFp8F32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32SrBf8Bf16 -> decoder(form(4)((operands, mods) =>
      Instruction.VCvtScalef32SrBf8Bf16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32SrBf8F16 -> decoder(form(4)((operands, mods) =>
      Instruction.VCvtScalef32SrBf8F16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32SrBf8F32 -> decoder(form(4)((operands, mods) =>
      Instruction.VCvtScalef32SrBf8F32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32SrFp8Bf16 -> decoder(form(4)((operands, mods) =>
      Instruction.VCvtScalef32SrFp8Bf16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32SrFp8F16 -> decoder(form(4)((operands, mods) =>
      Instruction.VCvtScalef32SrFp8F16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32SrFp8F32 -> decoder(form(4)((operands, mods) =>
      Instruction.VCvtScalef32SrFp8F32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32SrPk16Bf6Bf16 -> decoder(form(4)((operands, mods) =>
      Instruction.VCvtScalef32SrPk16Bf6Bf16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32SrPk16Bf6F16 -> decoder(form(4)((operands, mods) =>
      Instruction.VCvtScalef32SrPk16Bf6F16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32SrPk16Bf6F32 -> decoder(form(4)((operands, mods) =>
      Instruction.VCvtScalef32SrPk16Bf6F32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32SrPk16Fp6Bf16 -> decoder(form(4)((operands, mods) =>
      Instruction.VCvtScalef32SrPk16Fp6Bf16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32SrPk16Fp6F16 -> decoder(form(4)((operands, mods) =>
      Instruction.VCvtScalef32SrPk16Fp6F16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32SrPk16Fp6F32 -> decoder(form(4)((operands, mods) =>
      Instruction.VCvtScalef32SrPk16Fp6F32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32SrPk32Bf6Bf16 -> decoder(form(4)((operands, mods) =>
      Instruction.VCvtScalef32SrPk32Bf6Bf16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32SrPk32Bf6F16 -> decoder(form(4)((operands, mods) =>
      Instruction.VCvtScalef32SrPk32Bf6F16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32SrPk32Bf6F32 -> decoder(form(4)((operands, mods) =>
      Instruction.VCvtScalef32SrPk32Bf6F32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32SrPk32Fp6Bf16 -> decoder(form(4)((operands, mods) =>
      Instruction.VCvtScalef32SrPk32Fp6Bf16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32SrPk32Fp6F16 -> decoder(form(4)((operands, mods) =>
      Instruction.VCvtScalef32SrPk32Fp6F16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32SrPk32Fp6F32 -> decoder(form(4)((operands, mods) =>
      Instruction.VCvtScalef32SrPk32Fp6F32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32SrPk8Bf8Bf16 -> decoder(form(4)((operands, mods) =>
      Instruction.VCvtScalef32SrPk8Bf8Bf16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32SrPk8Bf8F16 -> decoder(form(4)((operands, mods) =>
      Instruction.VCvtScalef32SrPk8Bf8F16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32SrPk8Bf8F32 -> decoder(form(4)((operands, mods) =>
      Instruction.VCvtScalef32SrPk8Bf8F32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32SrPk8Fp4Bf16 -> decoder(form(4)((operands, mods) =>
      Instruction.VCvtScalef32SrPk8Fp4Bf16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32SrPk8Fp4F16 -> decoder(form(4)((operands, mods) =>
      Instruction.VCvtScalef32SrPk8Fp4F16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32SrPk8Fp4F32 -> decoder(form(4)((operands, mods) =>
      Instruction.VCvtScalef32SrPk8Fp4F32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32SrPk8Fp8Bf16 -> decoder(form(4)((operands, mods) =>
      Instruction.VCvtScalef32SrPk8Fp8Bf16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32SrPk8Fp8F16 -> decoder(form(4)((operands, mods) =>
      Instruction.VCvtScalef32SrPk8Fp8F16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32SrPk8Fp8F32 -> decoder(form(4)((operands, mods) =>
      Instruction.VCvtScalef32SrPk8Fp8F32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32SrPkFp4Bf16 -> decoder(form(4)((operands, mods) =>
      Instruction.VCvtScalef32SrPkFp4Bf16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32SrPkFp4F16 -> decoder(form(4)((operands, mods) =>
      Instruction.VCvtScalef32SrPkFp4F16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VCvtScalef32SrPkFp4F32 -> decoder(form(4)((operands, mods) =>
      Instruction.VCvtScalef32SrPkFp4F32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VCvtSrBf16F32 ->
      decoder(form(3)((operands, mods) => Instruction.VCvtSrBf16F32(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VCvtSrBf8F16 ->
      decoder(form(3)((operands, mods) => Instruction.VCvtSrBf8F16(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VCvtSrBf8F32 ->
      decoder(form(3)((operands, mods) => Instruction.VCvtSrBf8F32(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VCvtSrF16F32 ->
      decoder(form(3)((operands, mods) => Instruction.VCvtSrF16F32(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VCvtSrFp8F16 ->
      decoder(form(3)((operands, mods) => Instruction.VCvtSrFp8F16(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VCvtSrFp8F32 ->
      decoder(form(3)((operands, mods) => Instruction.VCvtSrFp8F32(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VCvtSrPkBf16F32 -> decoder(form(4)((operands, mods) =>
      Instruction.VCvtSrPkBf16F32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VCvtSrPkF16F32 -> decoder(form(4)((operands, mods) =>
      Instruction.VCvtSrPkF16F32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VDivFixupF16 -> decoder(form(4)((operands, mods) =>
      Instruction.VDivFixupF16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VDivFixupF32 -> decoder(form(4)((operands, mods) =>
      Instruction.VDivFixupF32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VDivFixupF64 -> decoder(form(4)((operands, mods) =>
      Instruction.VDivFixupF64(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VDivFixupLegacyF16 -> decoder(form(4)((operands, mods) =>
      Instruction.VDivFixupLegacyF16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VDivFmasF32 -> decoder(form(4)((operands, mods) =>
      Instruction.VDivFmasF32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VDivFmasF64 -> decoder(form(4)((operands, mods) =>
      Instruction.VDivFmasF64(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VDivScaleF32 -> decoder(form(5)((operands, mods) =>
      Instruction.VDivScaleF32(operands(0), operands(1), operands(2), operands(3), operands(4), mods)
    )),
    VOP3InstructionDefs.VDivScaleF64 -> decoder(form(5)((operands, mods) =>
      Instruction.VDivScaleF64(operands(0), operands(1), operands(2), operands(3), operands(4), mods)
    )),
    VOP3InstructionDefs.VDot2Bf16Bf16 -> decoder(form(4)((operands, mods) =>
      Instruction.VDot2Bf16Bf16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VDot2F16F16 -> decoder(form(4)((operands, mods) =>
      Instruction.VDot2F16F16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VFmaDx9ZeroF32 -> decoder(form(4)((operands, mods) =>
      Instruction.VFmaDx9ZeroF32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VFmaF16 -> decoder(form(4)((operands, mods) =>
      Instruction.VFmaF16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VFmaF32 -> decoder(form(4)((operands, mods) =>
      Instruction.VFmaF32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VFmaF64 -> decoder(form(4)((operands, mods) =>
      Instruction.VFmaF64(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VFmaLegacyF16 -> decoder(form(4)((operands, mods) =>
      Instruction.VFmaLegacyF16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VFmaLegacyF32 -> decoder(form(4)((operands, mods) =>
      Instruction.VFmaLegacyF32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VInterpMovF32 ->
      decoder(form(3)((operands, mods) => Instruction.VInterpMovF32(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VInterpP1F32 ->
      decoder(form(3)((operands, mods) => Instruction.VInterpP1F32(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VInterpP1llF16 ->
      decoder(form(3)((operands, mods) => Instruction.VInterpP1llF16(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VInterpP1lvF16 -> decoder(form(4)((operands, mods) =>
      Instruction.VInterpP1lvF16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VInterpP2F16 -> decoder(form(4)((operands, mods) =>
      Instruction.VInterpP2F16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VInterpP2F32 -> decoder(
      form(3)((operands, mods) => Instruction.VInterpP2F32_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) => Instruction.VInterpP2F32_4(operands(0), operands(1), operands(2), operands(3), mods))
    ),
    VOP3InstructionDefs.VInterpP2HiF16 ->
      decoder(form(3)((operands, mods) => Instruction.VInterpP2HiF16(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VInterpP2LegacyF16 -> decoder(form(4)((operands, mods) =>
      Instruction.VInterpP2LegacyF16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VLdexpF64 ->
      decoder(form(3)((operands, mods) => Instruction.VLdexpF64(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VLerpU8 -> decoder(form(4)((operands, mods) =>
      Instruction.VLerpU8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VLshlAddU32 -> decoder(form(4)((operands, mods) =>
      Instruction.VLshlAddU32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VLshlAddU64 -> decoder(form(4)((operands, mods) =>
      Instruction.VLshlAddU64(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VLshlB64 ->
      decoder(form(3)((operands, mods) => Instruction.VLshlB64(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VLshlOrB32 -> decoder(form(4)((operands, mods) =>
      Instruction.VLshlOrB32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VLshrB64 ->
      decoder(form(3)((operands, mods) => Instruction.VLshrB64(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VLshrrevB64 ->
      decoder(form(3)((operands, mods) => Instruction.VLshrrevB64(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VMadCoI64I32 -> decoder(form(5)((operands, mods) =>
      Instruction.VMadCoI64I32(operands(0), operands(1), operands(2), operands(3), operands(4), mods)
    )),
    VOP3InstructionDefs.VMadCoU64U32 -> decoder(form(5)((operands, mods) =>
      Instruction.VMadCoU64U32(operands(0), operands(1), operands(2), operands(3), operands(4), mods)
    )),
    VOP3InstructionDefs.VMadF16 -> decoder(form(4)((operands, mods) =>
      Instruction.VMadF16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMadF32 -> decoder(form(4)((operands, mods) =>
      Instruction.VMadF32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMadI16 -> decoder(form(4)((operands, mods) =>
      Instruction.VMadI16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMadI32I16 -> decoder(form(4)((operands, mods) =>
      Instruction.VMadI32I16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMadI32I24 -> decoder(form(4)((operands, mods) =>
      Instruction.VMadI32I24(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMadI64I32 -> decoder(form(5)((operands, mods) =>
      Instruction.VMadI64I32(operands(0), operands(1), operands(2), operands(3), operands(4), mods)
    )),
    VOP3InstructionDefs.VMadLegacyF16 -> decoder(form(4)((operands, mods) =>
      Instruction.VMadLegacyF16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMadLegacyF32 -> decoder(form(4)((operands, mods) =>
      Instruction.VMadLegacyF32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMadLegacyI16 -> decoder(form(4)((operands, mods) =>
      Instruction.VMadLegacyI16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMadLegacyU16 -> decoder(form(4)((operands, mods) =>
      Instruction.VMadLegacyU16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMadNcI64I32 -> decoder(form(4)((operands, mods) =>
      Instruction.VMadNcI64I32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMadNcU64U32 -> decoder(form(4)((operands, mods) =>
      Instruction.VMadNcU64U32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMadU16 -> decoder(form(4)((operands, mods) =>
      Instruction.VMadU16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMadU32 -> decoder(form(4)((operands, mods) =>
      Instruction.VMadU32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMadU32U16 -> decoder(form(4)((operands, mods) =>
      Instruction.VMadU32U16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMadU32U24 -> decoder(form(4)((operands, mods) =>
      Instruction.VMadU32U24(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMadU64U32 -> decoder(form(5)((operands, mods) =>
      Instruction.VMadU64U32(operands(0), operands(1), operands(2), operands(3), operands(4), mods)
    )),
    VOP3InstructionDefs.VMax3F16 -> decoder(form(4)((operands, mods) =>
      Instruction.VMax3F16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMax3F32 -> decoder(form(4)((operands, mods) =>
      Instruction.VMax3F32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMax3I16 -> decoder(form(4)((operands, mods) =>
      Instruction.VMax3I16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMax3I32 -> decoder(form(4)((operands, mods) =>
      Instruction.VMax3I32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMax3NumF16 -> decoder(form(4)((operands, mods) =>
      Instruction.VMax3NumF16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMax3NumF32 -> decoder(form(4)((operands, mods) =>
      Instruction.VMax3NumF32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMax3U16 -> decoder(form(4)((operands, mods) =>
      Instruction.VMax3U16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMax3U32 -> decoder(form(4)((operands, mods) =>
      Instruction.VMax3U32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMaxF64 ->
      decoder(form(3)((operands, mods) => Instruction.VMaxF64(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VMaxI64 ->
      decoder(form(3)((operands, mods) => Instruction.VMaxI64(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VMaxU64 ->
      decoder(form(3)((operands, mods) => Instruction.VMaxU64(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VMaximum3F16 -> decoder(form(4)((operands, mods) =>
      Instruction.VMaximum3F16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMaximum3F32 -> decoder(form(4)((operands, mods) =>
      Instruction.VMaximum3F32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMaximumF16 ->
      decoder(form(3)((operands, mods) => Instruction.VMaximumF16(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VMaximumF32 ->
      decoder(form(3)((operands, mods) => Instruction.VMaximumF32(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VMaximumF64 ->
      decoder(form(3)((operands, mods) => Instruction.VMaximumF64(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VMaximumminimumF16 -> decoder(form(4)((operands, mods) =>
      Instruction.VMaximumminimumF16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMaximumminimumF32 -> decoder(form(4)((operands, mods) =>
      Instruction.VMaximumminimumF32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMaxminF16 -> decoder(form(4)((operands, mods) =>
      Instruction.VMaxminF16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMaxminF32 -> decoder(form(4)((operands, mods) =>
      Instruction.VMaxminF32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMaxminI32 -> decoder(form(4)((operands, mods) =>
      Instruction.VMaxminI32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMaxminNumF16 -> decoder(form(4)((operands, mods) =>
      Instruction.VMaxminNumF16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMaxminNumF32 -> decoder(form(4)((operands, mods) =>
      Instruction.VMaxminNumF32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMaxminU32 -> decoder(form(4)((operands, mods) =>
      Instruction.VMaxminU32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMed3F16 -> decoder(form(4)((operands, mods) =>
      Instruction.VMed3F16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMed3F32 -> decoder(form(4)((operands, mods) =>
      Instruction.VMed3F32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMed3I16 -> decoder(form(4)((operands, mods) =>
      Instruction.VMed3I16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMed3I32 -> decoder(form(4)((operands, mods) =>
      Instruction.VMed3I32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMed3NumF16 -> decoder(form(4)((operands, mods) =>
      Instruction.VMed3NumF16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMed3NumF32 -> decoder(form(4)((operands, mods) =>
      Instruction.VMed3NumF32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMed3U16 -> decoder(form(4)((operands, mods) =>
      Instruction.VMed3U16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMed3U32 -> decoder(form(4)((operands, mods) =>
      Instruction.VMed3U32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMin3F16 -> decoder(form(4)((operands, mods) =>
      Instruction.VMin3F16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMin3F32 -> decoder(form(4)((operands, mods) =>
      Instruction.VMin3F32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMin3I16 -> decoder(form(4)((operands, mods) =>
      Instruction.VMin3I16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMin3I32 -> decoder(form(4)((operands, mods) =>
      Instruction.VMin3I32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMin3NumF16 -> decoder(form(4)((operands, mods) =>
      Instruction.VMin3NumF16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMin3NumF32 -> decoder(form(4)((operands, mods) =>
      Instruction.VMin3NumF32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMin3U16 -> decoder(form(4)((operands, mods) =>
      Instruction.VMin3U16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMin3U32 -> decoder(form(4)((operands, mods) =>
      Instruction.VMin3U32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMinF64 ->
      decoder(form(3)((operands, mods) => Instruction.VMinF64(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VMinI64 ->
      decoder(form(3)((operands, mods) => Instruction.VMinI64(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VMinU64 ->
      decoder(form(3)((operands, mods) => Instruction.VMinU64(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VMinimum3F16 -> decoder(form(4)((operands, mods) =>
      Instruction.VMinimum3F16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMinimum3F32 -> decoder(form(4)((operands, mods) =>
      Instruction.VMinimum3F32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMinimumF16 ->
      decoder(form(3)((operands, mods) => Instruction.VMinimumF16(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VMinimumF32 ->
      decoder(form(3)((operands, mods) => Instruction.VMinimumF32(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VMinimumF64 ->
      decoder(form(3)((operands, mods) => Instruction.VMinimumF64(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VMinimummaximumF16 -> decoder(form(4)((operands, mods) =>
      Instruction.VMinimummaximumF16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMinimummaximumF32 -> decoder(form(4)((operands, mods) =>
      Instruction.VMinimummaximumF32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMinmaxF16 -> decoder(form(4)((operands, mods) =>
      Instruction.VMinmaxF16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMinmaxF32 -> decoder(form(4)((operands, mods) =>
      Instruction.VMinmaxF32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMinmaxI32 -> decoder(form(4)((operands, mods) =>
      Instruction.VMinmaxI32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMinmaxNumF16 -> decoder(form(4)((operands, mods) =>
      Instruction.VMinmaxNumF16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMinmaxNumF32 -> decoder(form(4)((operands, mods) =>
      Instruction.VMinmaxNumF32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMinmaxU32 -> decoder(form(4)((operands, mods) =>
      Instruction.VMinmaxU32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMqsadPkU16U8 -> decoder(form(4)((operands, mods) =>
      Instruction.VMqsadPkU16U8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMqsadU32U8 -> decoder(form(4)((operands, mods) =>
      Instruction.VMqsadU32U8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMsadU8 -> decoder(form(4)((operands, mods) =>
      Instruction.VMsadU8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VMulHiI32 ->
      decoder(form(3)((operands, mods) => Instruction.VMulHiI32(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VMulHiU32 ->
      decoder(form(3)((operands, mods) => Instruction.VMulHiU32(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VMulLoI32 ->
      decoder(form(3)((operands, mods) => Instruction.VMulLoI32(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VMulLoU32 ->
      decoder(form(3)((operands, mods) => Instruction.VMulLoU32(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VMullitF32 -> decoder(form(4)((operands, mods) =>
      Instruction.VMullitF32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VOr3B32 -> decoder(form(4)((operands, mods) =>
      Instruction.VOr3B32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VOrB16 ->
      decoder(form(3)((operands, mods) => Instruction.VOrB16(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VPackB32F16 ->
      decoder(form(3)((operands, mods) => Instruction.VPackB32F16(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VPermB32 -> decoder(form(4)((operands, mods) =>
      Instruction.VPermB32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VPermPk16B4U4 -> decoder(form(4)((operands, mods) =>
      Instruction.VPermPk16B4U4(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VPermPk16B6U4 -> decoder(form(4)((operands, mods) =>
      Instruction.VPermPk16B6U4(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VPermPk16B8U4 -> decoder(form(4)((operands, mods) =>
      Instruction.VPermPk16B8U4(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VPermlane16B32 -> decoder(form(4)((operands, mods) =>
      Instruction.VPermlane16B32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VPermlane16VarB32 ->
      decoder(form(3)((operands, mods) => Instruction.VPermlane16VarB32(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VPermlaneBcastB32 -> decoder(form(4)((operands, mods) =>
      Instruction.VPermlaneBcastB32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VPermlaneDownB32 -> decoder(form(4)((operands, mods) =>
      Instruction.VPermlaneDownB32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VPermlaneIdxGenB32 ->
      decoder(form(3)((operands, mods) => Instruction.VPermlaneIdxGenB32(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VPermlaneUpB32 -> decoder(form(4)((operands, mods) =>
      Instruction.VPermlaneUpB32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VPermlaneXorB32 -> decoder(form(4)((operands, mods) =>
      Instruction.VPermlaneXorB32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VPermlanex16B32 -> decoder(form(4)((operands, mods) =>
      Instruction.VPermlanex16B32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VPermlanex16VarB32 ->
      decoder(form(3)((operands, mods) => Instruction.VPermlanex16VarB32(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VQsadPkU16U8 -> decoder(form(4)((operands, mods) =>
      Instruction.VQsadPkU16U8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VSExpF16 ->
      decoder(form(2)((operands, mods) => Instruction.VSExpF16(operands(0), operands(1), mods))),
    VOP3InstructionDefs.VSExpF32 ->
      decoder(form(2)((operands, mods) => Instruction.VSExpF32(operands(0), operands(1), mods))),
    VOP3InstructionDefs.VSLogF16 ->
      decoder(form(2)((operands, mods) => Instruction.VSLogF16(operands(0), operands(1), mods))),
    VOP3InstructionDefs.VSLogF32 ->
      decoder(form(2)((operands, mods) => Instruction.VSLogF32(operands(0), operands(1), mods))),
    VOP3InstructionDefs.VSRcpF16 ->
      decoder(form(2)((operands, mods) => Instruction.VSRcpF16(operands(0), operands(1), mods))),
    VOP3InstructionDefs.VSRcpF32 ->
      decoder(form(2)((operands, mods) => Instruction.VSRcpF32(operands(0), operands(1), mods))),
    VOP3InstructionDefs.VSRsqF16 ->
      decoder(form(2)((operands, mods) => Instruction.VSRsqF16(operands(0), operands(1), mods))),
    VOP3InstructionDefs.VSRsqF32 ->
      decoder(form(2)((operands, mods) => Instruction.VSRsqF32(operands(0), operands(1), mods))),
    VOP3InstructionDefs.VSSqrtF16 ->
      decoder(form(2)((operands, mods) => Instruction.VSSqrtF16(operands(0), operands(1), mods))),
    VOP3InstructionDefs.VSSqrtF32 ->
      decoder(form(2)((operands, mods) => Instruction.VSSqrtF32(operands(0), operands(1), mods))),
    VOP3InstructionDefs.VSadHiU8 -> decoder(form(4)((operands, mods) =>
      Instruction.VSadHiU8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VSadU16 -> decoder(form(4)((operands, mods) =>
      Instruction.VSadU16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VSadU32 -> decoder(form(4)((operands, mods) =>
      Instruction.VSadU32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VSadU8 -> decoder(form(4)((operands, mods) =>
      Instruction.VSadU8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VSubI16 ->
      decoder(form(3)((operands, mods) => Instruction.VSubI16(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VSubNcI16 ->
      decoder(form(3)((operands, mods) => Instruction.VSubNcI16(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VSubNcI32 ->
      decoder(form(3)((operands, mods) => Instruction.VSubNcI32(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VSubNcU16 ->
      decoder(form(3)((operands, mods) => Instruction.VSubNcU16(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VTrigPreopF64 ->
      decoder(form(3)((operands, mods) => Instruction.VTrigPreopF64(operands(0), operands(1), operands(2), mods))),
    VOP3InstructionDefs.VXadU32 -> decoder(form(4)((operands, mods) =>
      Instruction.VXadU32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VXor3B32 -> decoder(form(4)((operands, mods) =>
      Instruction.VXor3B32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP3InstructionDefs.VXorB16 ->
      decoder(form(3)((operands, mods) => Instruction.VXorB16(operands(0), operands(1), operands(2), mods)))
  )
}
