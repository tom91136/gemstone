// Generated from the pinned AMDGPU instruction forms. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu.asm
import net.kurobako.gemstone.isa.amdgpu.*
import Instruction.*
private object PackedVectorInstructionForms {
  val decoders: Map[Opcode, Decoder] = Map(
    PackedVectorInstructionDefs.VAccvgprRead ->
      decoder(form(2)((operands, mods) => Instruction.VAccvgprRead(operands(0), operands(1), mods))),
    PackedVectorInstructionDefs.VAccvgprReadB32 ->
      decoder(form(2)((operands, mods) => Instruction.VAccvgprReadB32(operands(0), operands(1), mods))),
    PackedVectorInstructionDefs.VAccvgprWrite ->
      decoder(form(2)((operands, mods) => Instruction.VAccvgprWrite(operands(0), operands(1), mods))),
    PackedVectorInstructionDefs.VAccvgprWriteB32 ->
      decoder(form(2)((operands, mods) => Instruction.VAccvgprWriteB32(operands(0), operands(1), mods))),
    PackedVectorInstructionDefs.VDot2F32Bf16 -> decoder(form(4)((operands, mods) =>
      Instruction.VDot2F32Bf16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    PackedVectorInstructionDefs.VDot2F32F16 -> decoder(form(4)((operands, mods) =>
      Instruction.VDot2F32F16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    PackedVectorInstructionDefs.VDot2I32I16 -> decoder(form(4)((operands, mods) =>
      Instruction.VDot2I32I16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    PackedVectorInstructionDefs.VDot2U32U16 -> decoder(form(4)((operands, mods) =>
      Instruction.VDot2U32U16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    PackedVectorInstructionDefs.VDot4F32Bf8Bf8 -> decoder(form(4)((operands, mods) =>
      Instruction.VDot4F32Bf8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    PackedVectorInstructionDefs.VDot4F32Bf8Fp8 -> decoder(form(4)((operands, mods) =>
      Instruction.VDot4F32Bf8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    PackedVectorInstructionDefs.VDot4F32Fp8Bf8 -> decoder(form(4)((operands, mods) =>
      Instruction.VDot4F32Fp8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    PackedVectorInstructionDefs.VDot4F32Fp8Fp8 -> decoder(form(4)((operands, mods) =>
      Instruction.VDot4F32Fp8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    PackedVectorInstructionDefs.VDot4I32I8 -> decoder(form(4)((operands, mods) =>
      Instruction.VDot4I32I8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    PackedVectorInstructionDefs.VDot4I32Iu8 -> decoder(form(4)((operands, mods) =>
      Instruction.VDot4I32Iu8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    PackedVectorInstructionDefs.VDot4U32U8 -> decoder(form(4)((operands, mods) =>
      Instruction.VDot4U32U8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    PackedVectorInstructionDefs.VDot8I32I4 -> decoder(form(4)((operands, mods) =>
      Instruction.VDot8I32I4(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    PackedVectorInstructionDefs.VDot8I32Iu4 -> decoder(form(4)((operands, mods) =>
      Instruction.VDot8I32Iu4(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    PackedVectorInstructionDefs.VDot8U32U4 -> decoder(form(4)((operands, mods) =>
      Instruction.VDot8U32U4(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    PackedVectorInstructionDefs.VFmaMixF32 -> decoder(form(4)((operands, mods) =>
      Instruction.VFmaMixF32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    PackedVectorInstructionDefs.VFmaMixF32Bf16 -> decoder(form(4)((operands, mods) =>
      Instruction.VFmaMixF32Bf16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    PackedVectorInstructionDefs.VFmaMixhiBf16 -> decoder(form(4)((operands, mods) =>
      Instruction.VFmaMixhiBf16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    PackedVectorInstructionDefs.VFmaMixhiF16 -> decoder(form(4)((operands, mods) =>
      Instruction.VFmaMixhiF16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    PackedVectorInstructionDefs.VFmaMixloBf16 -> decoder(form(4)((operands, mods) =>
      Instruction.VFmaMixloBf16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    PackedVectorInstructionDefs.VFmaMixloF16 -> decoder(form(4)((operands, mods) =>
      Instruction.VFmaMixloF16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    PackedVectorInstructionDefs.VMadMixF32 -> decoder(form(4)((operands, mods) =>
      Instruction.VMadMixF32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    PackedVectorInstructionDefs.VMadMixhiF16 -> decoder(form(4)((operands, mods) =>
      Instruction.VMadMixhiF16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    PackedVectorInstructionDefs.VMadMixloF16 -> decoder(form(4)((operands, mods) =>
      Instruction.VMadMixloF16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    PackedVectorInstructionDefs.VPkAddBf16 ->
      decoder(form(3)((operands, mods) => Instruction.VPkAddBf16(operands(0), operands(1), operands(2), mods))),
    PackedVectorInstructionDefs.VPkAddF16 ->
      decoder(form(3)((operands, mods) => Instruction.VPkAddF16(operands(0), operands(1), operands(2), mods))),
    PackedVectorInstructionDefs.VPkAddF32 ->
      decoder(form(3)((operands, mods) => Instruction.VPkAddF32(operands(0), operands(1), operands(2), mods))),
    PackedVectorInstructionDefs.VPkAddF64 ->
      decoder(form(3)((operands, mods) => Instruction.VPkAddF64(operands(0), operands(1), operands(2), mods))),
    PackedVectorInstructionDefs.VPkAddI16 ->
      decoder(form(3)((operands, mods) => Instruction.VPkAddI16(operands(0), operands(1), operands(2), mods))),
    PackedVectorInstructionDefs.VPkAddMaxI16 -> decoder(form(4)((operands, mods) =>
      Instruction.VPkAddMaxI16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    PackedVectorInstructionDefs.VPkAddMaxU16 -> decoder(form(4)((operands, mods) =>
      Instruction.VPkAddMaxU16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    PackedVectorInstructionDefs.VPkAddMinI16 -> decoder(form(4)((operands, mods) =>
      Instruction.VPkAddMinI16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    PackedVectorInstructionDefs.VPkAddMinU16 -> decoder(form(4)((operands, mods) =>
      Instruction.VPkAddMinU16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    PackedVectorInstructionDefs.VPkAddNcU64 ->
      decoder(form(3)((operands, mods) => Instruction.VPkAddNcU64(operands(0), operands(1), operands(2), mods))),
    PackedVectorInstructionDefs.VPkAddU16 ->
      decoder(form(3)((operands, mods) => Instruction.VPkAddU16(operands(0), operands(1), operands(2), mods))),
    PackedVectorInstructionDefs.VPkAshrrevI16 ->
      decoder(form(3)((operands, mods) => Instruction.VPkAshrrevI16(operands(0), operands(1), operands(2), mods))),
    PackedVectorInstructionDefs.VPkFmaBf16 -> decoder(form(4)((operands, mods) =>
      Instruction.VPkFmaBf16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    PackedVectorInstructionDefs.VPkFmaF16 -> decoder(form(4)((operands, mods) =>
      Instruction.VPkFmaF16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    PackedVectorInstructionDefs.VPkFmaF32 -> decoder(form(4)((operands, mods) =>
      Instruction.VPkFmaF32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    PackedVectorInstructionDefs.VPkFmaF64 -> decoder(form(4)((operands, mods) =>
      Instruction.VPkFmaF64(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    PackedVectorInstructionDefs.VPkLshlAddU64 -> decoder(form(4)((operands, mods) =>
      Instruction.VPkLshlAddU64(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    PackedVectorInstructionDefs.VPkLshlrevB16 ->
      decoder(form(3)((operands, mods) => Instruction.VPkLshlrevB16(operands(0), operands(1), operands(2), mods))),
    PackedVectorInstructionDefs.VPkLshrrevB16 ->
      decoder(form(3)((operands, mods) => Instruction.VPkLshrrevB16(operands(0), operands(1), operands(2), mods))),
    PackedVectorInstructionDefs.VPkMadI16 -> decoder(form(4)((operands, mods) =>
      Instruction.VPkMadI16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    PackedVectorInstructionDefs.VPkMadU16 -> decoder(form(4)((operands, mods) =>
      Instruction.VPkMadU16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    PackedVectorInstructionDefs.VPkMax3I16 -> decoder(form(4)((operands, mods) =>
      Instruction.VPkMax3I16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    PackedVectorInstructionDefs.VPkMax3NumF16 -> decoder(form(4)((operands, mods) =>
      Instruction.VPkMax3NumF16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    PackedVectorInstructionDefs.VPkMax3U16 -> decoder(form(4)((operands, mods) =>
      Instruction.VPkMax3U16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    PackedVectorInstructionDefs.VPkMaxF16 ->
      decoder(form(3)((operands, mods) => Instruction.VPkMaxF16(operands(0), operands(1), operands(2), mods))),
    PackedVectorInstructionDefs.VPkMaxI16 ->
      decoder(form(3)((operands, mods) => Instruction.VPkMaxI16(operands(0), operands(1), operands(2), mods))),
    PackedVectorInstructionDefs.VPkMaxNumBf16 ->
      decoder(form(3)((operands, mods) => Instruction.VPkMaxNumBf16(operands(0), operands(1), operands(2), mods))),
    PackedVectorInstructionDefs.VPkMaxNumF16 ->
      decoder(form(3)((operands, mods) => Instruction.VPkMaxNumF16(operands(0), operands(1), operands(2), mods))),
    PackedVectorInstructionDefs.VPkMaxNumF64 ->
      decoder(form(3)((operands, mods) => Instruction.VPkMaxNumF64(operands(0), operands(1), operands(2), mods))),
    PackedVectorInstructionDefs.VPkMaxU16 ->
      decoder(form(3)((operands, mods) => Instruction.VPkMaxU16(operands(0), operands(1), operands(2), mods))),
    PackedVectorInstructionDefs.VPkMaximum3F16 -> decoder(form(4)((operands, mods) =>
      Instruction.VPkMaximum3F16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    PackedVectorInstructionDefs.VPkMaximumF16 ->
      decoder(form(3)((operands, mods) => Instruction.VPkMaximumF16(operands(0), operands(1), operands(2), mods))),
    PackedVectorInstructionDefs.VPkMin3I16 -> decoder(form(4)((operands, mods) =>
      Instruction.VPkMin3I16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    PackedVectorInstructionDefs.VPkMin3NumF16 -> decoder(form(4)((operands, mods) =>
      Instruction.VPkMin3NumF16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    PackedVectorInstructionDefs.VPkMin3U16 -> decoder(form(4)((operands, mods) =>
      Instruction.VPkMin3U16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    PackedVectorInstructionDefs.VPkMinF16 ->
      decoder(form(3)((operands, mods) => Instruction.VPkMinF16(operands(0), operands(1), operands(2), mods))),
    PackedVectorInstructionDefs.VPkMinI16 ->
      decoder(form(3)((operands, mods) => Instruction.VPkMinI16(operands(0), operands(1), operands(2), mods))),
    PackedVectorInstructionDefs.VPkMinNumBf16 ->
      decoder(form(3)((operands, mods) => Instruction.VPkMinNumBf16(operands(0), operands(1), operands(2), mods))),
    PackedVectorInstructionDefs.VPkMinNumF16 ->
      decoder(form(3)((operands, mods) => Instruction.VPkMinNumF16(operands(0), operands(1), operands(2), mods))),
    PackedVectorInstructionDefs.VPkMinNumF64 ->
      decoder(form(3)((operands, mods) => Instruction.VPkMinNumF64(operands(0), operands(1), operands(2), mods))),
    PackedVectorInstructionDefs.VPkMinU16 ->
      decoder(form(3)((operands, mods) => Instruction.VPkMinU16(operands(0), operands(1), operands(2), mods))),
    PackedVectorInstructionDefs.VPkMinimum3F16 -> decoder(form(4)((operands, mods) =>
      Instruction.VPkMinimum3F16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    PackedVectorInstructionDefs.VPkMinimumF16 ->
      decoder(form(3)((operands, mods) => Instruction.VPkMinimumF16(operands(0), operands(1), operands(2), mods))),
    PackedVectorInstructionDefs.VPkMovB32 ->
      decoder(form(3)((operands, mods) => Instruction.VPkMovB32(operands(0), operands(1), operands(2), mods))),
    PackedVectorInstructionDefs.VPkMulBf16 ->
      decoder(form(3)((operands, mods) => Instruction.VPkMulBf16(operands(0), operands(1), operands(2), mods))),
    PackedVectorInstructionDefs.VPkMulF16 ->
      decoder(form(3)((operands, mods) => Instruction.VPkMulF16(operands(0), operands(1), operands(2), mods))),
    PackedVectorInstructionDefs.VPkMulF32 ->
      decoder(form(3)((operands, mods) => Instruction.VPkMulF32(operands(0), operands(1), operands(2), mods))),
    PackedVectorInstructionDefs.VPkMulF64 ->
      decoder(form(3)((operands, mods) => Instruction.VPkMulF64(operands(0), operands(1), operands(2), mods))),
    PackedVectorInstructionDefs.VPkMulLoU16 ->
      decoder(form(3)((operands, mods) => Instruction.VPkMulLoU16(operands(0), operands(1), operands(2), mods))),
    PackedVectorInstructionDefs.VPkSubI16 ->
      decoder(form(3)((operands, mods) => Instruction.VPkSubI16(operands(0), operands(1), operands(2), mods))),
    PackedVectorInstructionDefs.VPkSubNcU64 ->
      decoder(form(3)((operands, mods) => Instruction.VPkSubNcU64(operands(0), operands(1), operands(2), mods))),
    PackedVectorInstructionDefs.VPkSubU16 ->
      decoder(form(3)((operands, mods) => Instruction.VPkSubU16(operands(0), operands(1), operands(2), mods)))
  )
}
