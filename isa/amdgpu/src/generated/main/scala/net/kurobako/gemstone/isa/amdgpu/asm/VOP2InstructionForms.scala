// Generated from the pinned AMDGPU instruction forms. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu.asm
import net.kurobako.gemstone.isa.amdgpu.*
import Instruction.*
private object VOP2InstructionForms {
  val decoders: Map[Opcode, Decoder] = Map(
    VOP2InstructionDefs.VAddCoCiU32 -> decoder(form(5)((operands, mods) =>
      Instruction.VAddCoCiU32(operands(0), operands(1), operands(2), operands(3), operands(4), mods)
    )),
    VOP2InstructionDefs.VAddCoU32 -> decoder(form(4)((operands, mods) =>
      Instruction.VAddCoU32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP2InstructionDefs.VAddF16 ->
      decoder(form(3)((operands, mods) => Instruction.VAddF16(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VAddF32 ->
      decoder(form(3)((operands, mods) => Instruction.VAddF32(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VAddF64 ->
      decoder(form(3)((operands, mods) => Instruction.VAddF64(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VAddI32 -> decoder(
      form(3)((operands, mods) => Instruction.VAddI32_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) => Instruction.VAddI32_4(operands(0), operands(1), operands(2), operands(3), mods))
    ),
    VOP2InstructionDefs.VAddNcU32 ->
      decoder(form(3)((operands, mods) => Instruction.VAddNcU32(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VAddNcU64 ->
      decoder(form(3)((operands, mods) => Instruction.VAddNcU64(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VAddU16 ->
      decoder(form(3)((operands, mods) => Instruction.VAddU16(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VAddU32 -> decoder(
      form(3)((operands, mods) => Instruction.VAddU32_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) => Instruction.VAddU32_4(operands(0), operands(1), operands(2), operands(3), mods))
    ),
    VOP2InstructionDefs.VAddcCoU32 -> decoder(form(5)((operands, mods) =>
      Instruction.VAddcCoU32(operands(0), operands(1), operands(2), operands(3), operands(4), mods)
    )),
    VOP2InstructionDefs.VAddcU32 -> decoder(form(5)((operands, mods) =>
      Instruction.VAddcU32(operands(0), operands(1), operands(2), operands(3), operands(4), mods)
    )),
    VOP2InstructionDefs.VAndB32 ->
      decoder(form(3)((operands, mods) => Instruction.VAndB32(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VAshrI32 ->
      decoder(form(3)((operands, mods) => Instruction.VAshrI32(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VAshrrevI16 ->
      decoder(form(3)((operands, mods) => Instruction.VAshrrevI16(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VAshrrevI32 ->
      decoder(form(3)((operands, mods) => Instruction.VAshrrevI32(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VBcntU32B32 ->
      decoder(form(3)((operands, mods) => Instruction.VBcntU32B32(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VBfmB32 ->
      decoder(form(3)((operands, mods) => Instruction.VBfmB32(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VCndmaskB32 -> decoder(form(4)((operands, mods) =>
      Instruction.VCndmaskB32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP2InstructionDefs.VCvtPkI16I32 ->
      decoder(form(3)((operands, mods) => Instruction.VCvtPkI16I32(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VCvtPkRtzF16F32_1 ->
      decoder(form(3)((operands, mods) => Instruction.VCvtPkRtzF16F32_1(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VCvtPkU16U32 ->
      decoder(form(3)((operands, mods) => Instruction.VCvtPkU16U32(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VCvtPkaccumU8F32 ->
      decoder(form(3)((operands, mods) => Instruction.VCvtPkaccumU8F32(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VCvtPknormI16F32_2 ->
      decoder(form(3)((operands, mods) => Instruction.VCvtPknormI16F32_2(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VCvtPknormU16F32_2 ->
      decoder(form(3)((operands, mods) => Instruction.VCvtPknormU16F32_2(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VCvtPkrtzF16F32_2 ->
      decoder(form(3)((operands, mods) => Instruction.VCvtPkrtzF16F32_2(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VDot2accF32F16 ->
      decoder(form(3)((operands, mods) => Instruction.VDot2accF32F16(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VDot2cF32Bf16 ->
      decoder(form(3)((operands, mods) => Instruction.VDot2cF32Bf16(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VDot2cF32F16 ->
      decoder(form(3)((operands, mods) => Instruction.VDot2cF32F16(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VDot2cI32I16 ->
      decoder(form(3)((operands, mods) => Instruction.VDot2cI32I16(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VDot4cI32I8 ->
      decoder(form(3)((operands, mods) => Instruction.VDot4cI32I8(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VDot8cI32I4 ->
      decoder(form(3)((operands, mods) => Instruction.VDot8cI32I4(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VFmaakF16 -> decoder(form(4)((operands, mods) =>
      Instruction.VFmaakF16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP2InstructionDefs.VFmaakF32 -> decoder(form(4)((operands, mods) =>
      Instruction.VFmaakF32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP2InstructionDefs.VFmaakF64 -> decoder(form(4)((operands, mods) =>
      Instruction.VFmaakF64(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP2InstructionDefs.VFmacDx9ZeroF32 ->
      decoder(form(3)((operands, mods) => Instruction.VFmacDx9ZeroF32(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VFmacF16 ->
      decoder(form(3)((operands, mods) => Instruction.VFmacF16(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VFmacF32 ->
      decoder(form(3)((operands, mods) => Instruction.VFmacF32(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VFmacF64 ->
      decoder(form(3)((operands, mods) => Instruction.VFmacF64(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VFmacLegacyF32 ->
      decoder(form(3)((operands, mods) => Instruction.VFmacLegacyF32(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VFmamkF16 -> decoder(form(4)((operands, mods) =>
      Instruction.VFmamkF16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP2InstructionDefs.VFmamkF32 -> decoder(form(4)((operands, mods) =>
      Instruction.VFmamkF32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP2InstructionDefs.VFmamkF64 -> decoder(form(4)((operands, mods) =>
      Instruction.VFmamkF64(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP2InstructionDefs.VLdexpF16 ->
      decoder(form(3)((operands, mods) => Instruction.VLdexpF16(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VLdexpF32 ->
      decoder(form(3)((operands, mods) => Instruction.VLdexpF32(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VLshlB32 ->
      decoder(form(3)((operands, mods) => Instruction.VLshlB32(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VLshlrevB16 ->
      decoder(form(3)((operands, mods) => Instruction.VLshlrevB16(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VLshlrevB32 ->
      decoder(form(3)((operands, mods) => Instruction.VLshlrevB32(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VLshlrevB64 ->
      decoder(form(3)((operands, mods) => Instruction.VLshlrevB64(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VLshrB32 ->
      decoder(form(3)((operands, mods) => Instruction.VLshrB32(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VLshrrevB16 ->
      decoder(form(3)((operands, mods) => Instruction.VLshrrevB16(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VLshrrevB32 ->
      decoder(form(3)((operands, mods) => Instruction.VLshrrevB32(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VMacF16 ->
      decoder(form(3)((operands, mods) => Instruction.VMacF16(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VMacF32 ->
      decoder(form(3)((operands, mods) => Instruction.VMacF32(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VMacLegacyF32 ->
      decoder(form(3)((operands, mods) => Instruction.VMacLegacyF32(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VMadakF16 -> decoder(form(4)((operands, mods) =>
      Instruction.VMadakF16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP2InstructionDefs.VMadakF32 -> decoder(form(4)((operands, mods) =>
      Instruction.VMadakF32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP2InstructionDefs.VMadmkF16 -> decoder(form(4)((operands, mods) =>
      Instruction.VMadmkF16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP2InstructionDefs.VMadmkF32 -> decoder(form(4)((operands, mods) =>
      Instruction.VMadmkF32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP2InstructionDefs.VMaxF16 ->
      decoder(form(3)((operands, mods) => Instruction.VMaxF16(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VMaxF32 ->
      decoder(form(3)((operands, mods) => Instruction.VMaxF32(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VMaxI16 ->
      decoder(form(3)((operands, mods) => Instruction.VMaxI16(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VMaxI32 ->
      decoder(form(3)((operands, mods) => Instruction.VMaxI32(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VMaxLegacyF32 ->
      decoder(form(3)((operands, mods) => Instruction.VMaxLegacyF32(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VMaxNumF16 ->
      decoder(form(3)((operands, mods) => Instruction.VMaxNumF16(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VMaxNumF32 ->
      decoder(form(3)((operands, mods) => Instruction.VMaxNumF32(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VMaxNumF64 ->
      decoder(form(3)((operands, mods) => Instruction.VMaxNumF64(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VMaxU16 ->
      decoder(form(3)((operands, mods) => Instruction.VMaxU16(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VMaxU32 ->
      decoder(form(3)((operands, mods) => Instruction.VMaxU32(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VMbcntHiU32B32 ->
      decoder(form(3)((operands, mods) => Instruction.VMbcntHiU32B32(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VMbcntLoU32B32 ->
      decoder(form(3)((operands, mods) => Instruction.VMbcntLoU32B32(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VMinF16 ->
      decoder(form(3)((operands, mods) => Instruction.VMinF16(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VMinF32 ->
      decoder(form(3)((operands, mods) => Instruction.VMinF32(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VMinI16 ->
      decoder(form(3)((operands, mods) => Instruction.VMinI16(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VMinI32 ->
      decoder(form(3)((operands, mods) => Instruction.VMinI32(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VMinLegacyF32 ->
      decoder(form(3)((operands, mods) => Instruction.VMinLegacyF32(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VMinNumF16 ->
      decoder(form(3)((operands, mods) => Instruction.VMinNumF16(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VMinNumF32 ->
      decoder(form(3)((operands, mods) => Instruction.VMinNumF32(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VMinNumF64 ->
      decoder(form(3)((operands, mods) => Instruction.VMinNumF64(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VMinU16 ->
      decoder(form(3)((operands, mods) => Instruction.VMinU16(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VMinU32 ->
      decoder(form(3)((operands, mods) => Instruction.VMinU32(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VMulDx9ZeroF32 ->
      decoder(form(3)((operands, mods) => Instruction.VMulDx9ZeroF32(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VMulF16 ->
      decoder(form(3)((operands, mods) => Instruction.VMulF16(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VMulF32 ->
      decoder(form(3)((operands, mods) => Instruction.VMulF32(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VMulF64 ->
      decoder(form(3)((operands, mods) => Instruction.VMulF64(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VMulHiI32I24 ->
      decoder(form(3)((operands, mods) => Instruction.VMulHiI32I24(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VMulHiU32U24 ->
      decoder(form(3)((operands, mods) => Instruction.VMulHiU32U24(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VMulI32I24 ->
      decoder(form(3)((operands, mods) => Instruction.VMulI32I24(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VMulLegacyF32 ->
      decoder(form(3)((operands, mods) => Instruction.VMulLegacyF32(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VMulLoU16 ->
      decoder(form(3)((operands, mods) => Instruction.VMulLoU16(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VMulU32U24 ->
      decoder(form(3)((operands, mods) => Instruction.VMulU32U24(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VMulU64 ->
      decoder(form(3)((operands, mods) => Instruction.VMulU64(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VOrB32 ->
      decoder(form(3)((operands, mods) => Instruction.VOrB32(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VPkFmacF16 ->
      decoder(form(3)((operands, mods) => Instruction.VPkFmacF16(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VReadlaneB32 ->
      decoder(form(3)((operands, mods) => Instruction.VReadlaneB32(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VSubCoCiU32 -> decoder(form(5)((operands, mods) =>
      Instruction.VSubCoCiU32(operands(0), operands(1), operands(2), operands(3), operands(4), mods)
    )),
    VOP2InstructionDefs.VSubCoU32 -> decoder(form(4)((operands, mods) =>
      Instruction.VSubCoU32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP2InstructionDefs.VSubF16 ->
      decoder(form(3)((operands, mods) => Instruction.VSubF16(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VSubF32 ->
      decoder(form(3)((operands, mods) => Instruction.VSubF32(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VSubI32 -> decoder(
      form(3)((operands, mods) => Instruction.VSubI32_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) => Instruction.VSubI32_4(operands(0), operands(1), operands(2), operands(3), mods))
    ),
    VOP2InstructionDefs.VSubNcU32 ->
      decoder(form(3)((operands, mods) => Instruction.VSubNcU32(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VSubNcU64 ->
      decoder(form(3)((operands, mods) => Instruction.VSubNcU64(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VSubU16 ->
      decoder(form(3)((operands, mods) => Instruction.VSubU16(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VSubU32 -> decoder(
      form(3)((operands, mods) => Instruction.VSubU32_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) => Instruction.VSubU32_4(operands(0), operands(1), operands(2), operands(3), mods))
    ),
    VOP2InstructionDefs.VSubbCoU32 -> decoder(form(5)((operands, mods) =>
      Instruction.VSubbCoU32(operands(0), operands(1), operands(2), operands(3), operands(4), mods)
    )),
    VOP2InstructionDefs.VSubbU32 -> decoder(form(5)((operands, mods) =>
      Instruction.VSubbU32(operands(0), operands(1), operands(2), operands(3), operands(4), mods)
    )),
    VOP2InstructionDefs.VSubbrevCoU32 -> decoder(form(5)((operands, mods) =>
      Instruction.VSubbrevCoU32(operands(0), operands(1), operands(2), operands(3), operands(4), mods)
    )),
    VOP2InstructionDefs.VSubbrevU32 -> decoder(form(5)((operands, mods) =>
      Instruction.VSubbrevU32(operands(0), operands(1), operands(2), operands(3), operands(4), mods)
    )),
    VOP2InstructionDefs.VSubrevCoCiU32 -> decoder(form(5)((operands, mods) =>
      Instruction.VSubrevCoCiU32(operands(0), operands(1), operands(2), operands(3), operands(4), mods)
    )),
    VOP2InstructionDefs.VSubrevCoU32 -> decoder(form(4)((operands, mods) =>
      Instruction.VSubrevCoU32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP2InstructionDefs.VSubrevF16 ->
      decoder(form(3)((operands, mods) => Instruction.VSubrevF16(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VSubrevF32 ->
      decoder(form(3)((operands, mods) => Instruction.VSubrevF32(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VSubrevI32 -> decoder(form(4)((operands, mods) =>
      Instruction.VSubrevI32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    VOP2InstructionDefs.VSubrevNcU32 ->
      decoder(form(3)((operands, mods) => Instruction.VSubrevNcU32(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VSubrevU16 ->
      decoder(form(3)((operands, mods) => Instruction.VSubrevU16(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VSubrevU32 -> decoder(
      form(3)((operands, mods) => Instruction.VSubrevU32_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) => Instruction.VSubrevU32_4(operands(0), operands(1), operands(2), operands(3), mods))
    ),
    VOP2InstructionDefs.VWritelaneB32 ->
      decoder(form(3)((operands, mods) => Instruction.VWritelaneB32(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VXnorB32 ->
      decoder(form(3)((operands, mods) => Instruction.VXnorB32(operands(0), operands(1), operands(2), mods))),
    VOP2InstructionDefs.VXorB32 ->
      decoder(form(3)((operands, mods) => Instruction.VXorB32(operands(0), operands(1), operands(2), mods)))
  )
}
