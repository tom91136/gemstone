// Generated from the pinned AMDGPU instruction forms. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu.asm
import net.kurobako.gemstone.isa.amdgpu.*
import Instruction.*
private object VOP1InstructionForms {
  val decoders: Map[Opcode, Decoder] = Map(
    VOP1InstructionDefs.VAccvgprMovB32 ->
      decoder(form(2)((operands, mods) => Instruction.VAccvgprMovB32(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VBfrevB32 ->
      decoder(form(2)((operands, mods) => Instruction.VBfrevB32(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VCeilF16 ->
      decoder(form(2)((operands, mods) => Instruction.VCeilF16(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VCeilF32 ->
      decoder(form(2)((operands, mods) => Instruction.VCeilF32(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VCeilF64 ->
      decoder(form(2)((operands, mods) => Instruction.VCeilF64(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VClrexcp -> decoder(form(0)((_, mods) => Instruction.VClrexcp(mods))),
    VOP1InstructionDefs.VClsI32 ->
      decoder(form(2)((operands, mods) => Instruction.VClsI32(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VClzI32U32 ->
      decoder(form(2)((operands, mods) => Instruction.VClzI32U32(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VCosBf16 ->
      decoder(form(2)((operands, mods) => Instruction.VCosBf16(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VCosF16 ->
      decoder(form(2)((operands, mods) => Instruction.VCosF16(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VCosF32 ->
      decoder(form(2)((operands, mods) => Instruction.VCosF32(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VCtzI32B32 ->
      decoder(form(2)((operands, mods) => Instruction.VCtzI32B32(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VCvtF16Bf8 ->
      decoder(form(2)((operands, mods) => Instruction.VCvtF16Bf8(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VCvtF16F32 ->
      decoder(form(2)((operands, mods) => Instruction.VCvtF16F32(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VCvtF16Fp8 ->
      decoder(form(2)((operands, mods) => Instruction.VCvtF16Fp8(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VCvtF16I16 ->
      decoder(form(2)((operands, mods) => Instruction.VCvtF16I16(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VCvtF16U16 ->
      decoder(form(2)((operands, mods) => Instruction.VCvtF16U16(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VCvtF32Bf16 ->
      decoder(form(2)((operands, mods) => Instruction.VCvtF32Bf16(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VCvtF32Bf8 ->
      decoder(form(2)((operands, mods) => Instruction.VCvtF32Bf8(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VCvtF32F16 ->
      decoder(form(2)((operands, mods) => Instruction.VCvtF32F16(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VCvtF32F64 ->
      decoder(form(2)((operands, mods) => Instruction.VCvtF32F64(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VCvtF32Fp8 ->
      decoder(form(2)((operands, mods) => Instruction.VCvtF32Fp8(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VCvtF32I32 ->
      decoder(form(2)((operands, mods) => Instruction.VCvtF32I32(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VCvtF32U32 ->
      decoder(form(2)((operands, mods) => Instruction.VCvtF32U32(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VCvtF32Ubyte0 ->
      decoder(form(2)((operands, mods) => Instruction.VCvtF32Ubyte0(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VCvtF32Ubyte1 ->
      decoder(form(2)((operands, mods) => Instruction.VCvtF32Ubyte1(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VCvtF32Ubyte2 ->
      decoder(form(2)((operands, mods) => Instruction.VCvtF32Ubyte2(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VCvtF32Ubyte3 ->
      decoder(form(2)((operands, mods) => Instruction.VCvtF32Ubyte3(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VCvtF64F32 ->
      decoder(form(2)((operands, mods) => Instruction.VCvtF64F32(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VCvtF64I32 ->
      decoder(form(2)((operands, mods) => Instruction.VCvtF64I32(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VCvtF64U32 ->
      decoder(form(2)((operands, mods) => Instruction.VCvtF64U32(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VCvtFloorI32F32 ->
      decoder(form(2)((operands, mods) => Instruction.VCvtFloorI32F32(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VCvtFlrI32F32 ->
      decoder(form(2)((operands, mods) => Instruction.VCvtFlrI32F32(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VCvtI16F16 ->
      decoder(form(2)((operands, mods) => Instruction.VCvtI16F16(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VCvtI32F32 ->
      decoder(form(2)((operands, mods) => Instruction.VCvtI32F32(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VCvtI32F64 ->
      decoder(form(2)((operands, mods) => Instruction.VCvtI32F64(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VCvtI32I16 ->
      decoder(form(2)((operands, mods) => Instruction.VCvtI32I16(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VCvtNearestI32F32 ->
      decoder(form(2)((operands, mods) => Instruction.VCvtNearestI32F32(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VCvtNormI16F16 ->
      decoder(form(2)((operands, mods) => Instruction.VCvtNormI16F16(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VCvtNormU16F16 ->
      decoder(form(2)((operands, mods) => Instruction.VCvtNormU16F16(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VCvtOffF32I4 ->
      decoder(form(2)((operands, mods) => Instruction.VCvtOffF32I4(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VCvtPkF16Bf8 ->
      decoder(form(2)((operands, mods) => Instruction.VCvtPkF16Bf8(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VCvtPkF16Fp8 ->
      decoder(form(2)((operands, mods) => Instruction.VCvtPkF16Fp8(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VCvtPkF32Bf8 ->
      decoder(form(2)((operands, mods) => Instruction.VCvtPkF32Bf8(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VCvtPkF32Fp8 ->
      decoder(form(2)((operands, mods) => Instruction.VCvtPkF32Fp8(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VCvtRpiI32F32 ->
      decoder(form(2)((operands, mods) => Instruction.VCvtRpiI32F32(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VCvtU16F16 ->
      decoder(form(2)((operands, mods) => Instruction.VCvtU16F16(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VCvtU32F32 ->
      decoder(form(2)((operands, mods) => Instruction.VCvtU32F32(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VCvtU32F64 ->
      decoder(form(2)((operands, mods) => Instruction.VCvtU32F64(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VCvtU32U16 ->
      decoder(form(2)((operands, mods) => Instruction.VCvtU32U16(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VExpBf16 ->
      decoder(form(2)((operands, mods) => Instruction.VExpBf16(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VExpF16 ->
      decoder(form(2)((operands, mods) => Instruction.VExpF16(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VExpF32 ->
      decoder(form(2)((operands, mods) => Instruction.VExpF32(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VExpLegacyF32 ->
      decoder(form(2)((operands, mods) => Instruction.VExpLegacyF32(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VFfbhI32 ->
      decoder(form(2)((operands, mods) => Instruction.VFfbhI32(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VFfbhU32 ->
      decoder(form(2)((operands, mods) => Instruction.VFfbhU32(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VFfblB32 ->
      decoder(form(2)((operands, mods) => Instruction.VFfblB32(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VFloorF16 ->
      decoder(form(2)((operands, mods) => Instruction.VFloorF16(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VFloorF32 ->
      decoder(form(2)((operands, mods) => Instruction.VFloorF32(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VFloorF64 ->
      decoder(form(2)((operands, mods) => Instruction.VFloorF64(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VFractF16 ->
      decoder(form(2)((operands, mods) => Instruction.VFractF16(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VFractF32 ->
      decoder(form(2)((operands, mods) => Instruction.VFractF32(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VFractF64 ->
      decoder(form(2)((operands, mods) => Instruction.VFractF64(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VFrexpExpI16F16 ->
      decoder(form(2)((operands, mods) => Instruction.VFrexpExpI16F16(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VFrexpExpI32F32 ->
      decoder(form(2)((operands, mods) => Instruction.VFrexpExpI32F32(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VFrexpExpI32F64 ->
      decoder(form(2)((operands, mods) => Instruction.VFrexpExpI32F64(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VFrexpMantF16 ->
      decoder(form(2)((operands, mods) => Instruction.VFrexpMantF16(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VFrexpMantF32 ->
      decoder(form(2)((operands, mods) => Instruction.VFrexpMantF32(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VFrexpMantF64 ->
      decoder(form(2)((operands, mods) => Instruction.VFrexpMantF64(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VIllegal -> decoder(form(0)((_, mods) => Instruction.VIllegal(mods))),
    VOP1InstructionDefs.VLogBf16 ->
      decoder(form(2)((operands, mods) => Instruction.VLogBf16(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VLogClampF32 ->
      decoder(form(2)((operands, mods) => Instruction.VLogClampF32(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VLogF16 ->
      decoder(form(2)((operands, mods) => Instruction.VLogF16(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VLogF32 ->
      decoder(form(2)((operands, mods) => Instruction.VLogF32(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VLogLegacyF32 ->
      decoder(form(2)((operands, mods) => Instruction.VLogLegacyF32(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VMovB16 ->
      decoder(form(2)((operands, mods) => Instruction.VMovB16(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VMovB32 ->
      decoder(form(2)((operands, mods) => Instruction.VMovB32(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VMovB64 ->
      decoder(form(2)((operands, mods) => Instruction.VMovB64(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VMovreldB32 ->
      decoder(form(2)((operands, mods) => Instruction.VMovreldB32(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VMovrelsB32 ->
      decoder(form(2)((operands, mods) => Instruction.VMovrelsB32(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VMovrelsd2B32 ->
      decoder(form(2)((operands, mods) => Instruction.VMovrelsd2B32(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VMovrelsdB32 ->
      decoder(form(2)((operands, mods) => Instruction.VMovrelsdB32(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VNop -> decoder(form(0)((_, mods) => Instruction.VNop(mods))),
    VOP1InstructionDefs.VNotB16 ->
      decoder(form(2)((operands, mods) => Instruction.VNotB16(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VNotB32 ->
      decoder(form(2)((operands, mods) => Instruction.VNotB32(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VPermlane16SwapB32 ->
      decoder(form(2)((operands, mods) => Instruction.VPermlane16SwapB32(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VPermlane32SwapB32 ->
      decoder(form(2)((operands, mods) => Instruction.VPermlane32SwapB32(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VPermlane64B32 ->
      decoder(form(2)((operands, mods) => Instruction.VPermlane64B32(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VPipeflush -> decoder(form(0)((_, mods) => Instruction.VPipeflush(mods))),
    VOP1InstructionDefs.VPrngB32 ->
      decoder(form(2)((operands, mods) => Instruction.VPrngB32(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VRcpBf16 ->
      decoder(form(2)((operands, mods) => Instruction.VRcpBf16(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VRcpClampF32 ->
      decoder(form(2)((operands, mods) => Instruction.VRcpClampF32(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VRcpClampF64 ->
      decoder(form(2)((operands, mods) => Instruction.VRcpClampF64(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VRcpF16 ->
      decoder(form(2)((operands, mods) => Instruction.VRcpF16(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VRcpF32 ->
      decoder(form(2)((operands, mods) => Instruction.VRcpF32(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VRcpF64 ->
      decoder(form(2)((operands, mods) => Instruction.VRcpF64(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VRcpIflagF32 ->
      decoder(form(2)((operands, mods) => Instruction.VRcpIflagF32(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VRcpLegacyF32 ->
      decoder(form(2)((operands, mods) => Instruction.VRcpLegacyF32(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VReadfirstlaneB32 ->
      decoder(form(2)((operands, mods) => Instruction.VReadfirstlaneB32(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VRndneF16 ->
      decoder(form(2)((operands, mods) => Instruction.VRndneF16(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VRndneF32 ->
      decoder(form(2)((operands, mods) => Instruction.VRndneF32(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VRndneF64 ->
      decoder(form(2)((operands, mods) => Instruction.VRndneF64(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VRsqBf16 ->
      decoder(form(2)((operands, mods) => Instruction.VRsqBf16(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VRsqClampF32 ->
      decoder(form(2)((operands, mods) => Instruction.VRsqClampF32(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VRsqClampF64 ->
      decoder(form(2)((operands, mods) => Instruction.VRsqClampF64(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VRsqF16 ->
      decoder(form(2)((operands, mods) => Instruction.VRsqF16(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VRsqF32 ->
      decoder(form(2)((operands, mods) => Instruction.VRsqF32(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VRsqF64 ->
      decoder(form(2)((operands, mods) => Instruction.VRsqF64(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VRsqLegacyF32 ->
      decoder(form(2)((operands, mods) => Instruction.VRsqLegacyF32(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VSatPk4I4I8 ->
      decoder(form(2)((operands, mods) => Instruction.VSatPk4I4I8(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VSatPk4U4U8 ->
      decoder(form(2)((operands, mods) => Instruction.VSatPk4U4U8(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VSatPkU8I16 ->
      decoder(form(2)((operands, mods) => Instruction.VSatPkU8I16(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VScreenPartition4seB32 ->
      decoder(form(2)((operands, mods) => Instruction.VScreenPartition4seB32(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VSinBf16 ->
      decoder(form(2)((operands, mods) => Instruction.VSinBf16(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VSinF16 ->
      decoder(form(2)((operands, mods) => Instruction.VSinF16(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VSinF32 ->
      decoder(form(2)((operands, mods) => Instruction.VSinF32(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VSqrtBf16 ->
      decoder(form(2)((operands, mods) => Instruction.VSqrtBf16(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VSqrtF16 ->
      decoder(form(2)((operands, mods) => Instruction.VSqrtF16(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VSqrtF32 ->
      decoder(form(2)((operands, mods) => Instruction.VSqrtF32(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VSqrtF64 ->
      decoder(form(2)((operands, mods) => Instruction.VSqrtF64(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VSwapB16 ->
      decoder(form(2)((operands, mods) => Instruction.VSwapB16(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VSwapB32 ->
      decoder(form(2)((operands, mods) => Instruction.VSwapB32(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VSwaprelB32 ->
      decoder(form(2)((operands, mods) => Instruction.VSwaprelB32(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VTanhBf16 ->
      decoder(form(2)((operands, mods) => Instruction.VTanhBf16(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VTanhF16 ->
      decoder(form(2)((operands, mods) => Instruction.VTanhF16(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VTanhF32 ->
      decoder(form(2)((operands, mods) => Instruction.VTanhF32(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VTruncF16 ->
      decoder(form(2)((operands, mods) => Instruction.VTruncF16(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VTruncF32 ->
      decoder(form(2)((operands, mods) => Instruction.VTruncF32(operands(0), operands(1), mods))),
    VOP1InstructionDefs.VTruncF64 ->
      decoder(form(2)((operands, mods) => Instruction.VTruncF64(operands(0), operands(1), mods)))
  )
}
