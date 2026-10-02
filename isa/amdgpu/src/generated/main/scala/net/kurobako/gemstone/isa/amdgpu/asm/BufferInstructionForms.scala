// Generated from the pinned AMDGPU instruction forms. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu.asm
import net.kurobako.gemstone.isa.amdgpu.*
import Instruction.*
private object BufferInstructionForms {
  val decoders: Map[Opcode, Decoder] = Map(
    BufferInstructionDefs.BufferAtomicAdd -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicAdd(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicAddF32 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicAddF32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicAddF64 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicAddF64(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicAddU32 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicAddU32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicAddU64 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicAddU64(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicAddX2 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicAddX2(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicAnd -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicAnd(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicAndB32 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicAndB32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicAndB64 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicAndB64(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicAndX2 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicAndX2(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicCmpswap -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicCmpswap(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicCmpswapB32 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicCmpswapB32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicCmpswapB64 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicCmpswapB64(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicCmpswapF32 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicCmpswapF32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicCmpswapX2 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicCmpswapX2(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicCondSubU32 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicCondSubU32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicCsub -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicCsub(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicCsubU32 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicCsubU32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicDec -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicDec(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicDecU32 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicDecU32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicDecU64 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicDecU64(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicDecX2 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicDecX2(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicFcmpswap -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicFcmpswap(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicFcmpswapX2 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicFcmpswapX2(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicFmax -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicFmax(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicFmaxX2 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicFmaxX2(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicFmin -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicFmin(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicFminX2 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicFminX2(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicInc -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicInc(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicIncU32 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicIncU32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicIncU64 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicIncU64(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicIncX2 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicIncX2(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicMaxF32 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicMaxF32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicMaxF64 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicMaxF64(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicMaxI32 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicMaxI32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicMaxI64 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicMaxI64(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicMaxNumF32 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicMaxNumF32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicMaxNumF64 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicMaxNumF64(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicMaxU32 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicMaxU32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicMaxU64 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicMaxU64(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicMinF32 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicMinF32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicMinF64 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicMinF64(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicMinI32 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicMinI32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicMinI64 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicMinI64(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicMinNumF32 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicMinNumF32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicMinNumF64 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicMinNumF64(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicMinU32 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicMinU32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicMinU64 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicMinU64(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicOr -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicOr(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicOrB32 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicOrB32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicOrB64 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicOrB64(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicOrX2 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicOrX2(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicPkAddBf16 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicPkAddBf16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicPkAddF16 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicPkAddF16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicRsub -> decoder(form(0)((_, mods) => Instruction.BufferAtomicRsub(mods))),
    BufferInstructionDefs.BufferAtomicRsubX2 -> decoder(form(0)((_, mods) => Instruction.BufferAtomicRsubX2(mods))),
    BufferInstructionDefs.BufferAtomicSmax -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicSmax(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicSmaxX2 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicSmaxX2(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicSmin -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicSmin(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicSminX2 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicSminX2(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicSub -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicSub(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicSubClampU32 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicSubClampU32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicSubU32 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicSubU32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicSubU64 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicSubU64(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicSubX2 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicSubX2(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicSwap -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicSwap(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicSwapB32 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicSwapB32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicSwapB64 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicSwapB64(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicSwapX2 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicSwapX2(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicUmax -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicUmax(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicUmaxX2 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicUmaxX2(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicUmin -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicUmin(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicUminX2 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicUminX2(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicXor -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicXor(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicXorB32 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicXorB32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicXorB64 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicXorB64(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferAtomicXorX2 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferAtomicXorX2(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferGl0Inv -> decoder(form(0)((_, mods) => Instruction.BufferGl0Inv(mods))),
    BufferInstructionDefs.BufferGl1Inv -> decoder(form(0)((_, mods) => Instruction.BufferGl1Inv(mods))),
    BufferInstructionDefs.BufferInv -> decoder(form(0)((_, mods) => Instruction.BufferInv(mods))),
    BufferInstructionDefs.BufferInvl2 -> decoder(form(0)((_, mods) => Instruction.BufferInvl2(mods))),
    BufferInstructionDefs.BufferLoadB128 -> decoder(
      form(3)((operands, mods) => Instruction.BufferLoadB128_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.BufferLoadB128_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    BufferInstructionDefs.BufferLoadB32 -> decoder(
      form(3)((operands, mods) => Instruction.BufferLoadB32_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) => Instruction.BufferLoadB32_4(operands(0), operands(1), operands(2), operands(3), mods))
    ),
    BufferInstructionDefs.BufferLoadB64 -> decoder(
      form(3)((operands, mods) => Instruction.BufferLoadB64_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) => Instruction.BufferLoadB64_4(operands(0), operands(1), operands(2), operands(3), mods))
    ),
    BufferInstructionDefs.BufferLoadB96 -> decoder(
      form(3)((operands, mods) => Instruction.BufferLoadB96_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) => Instruction.BufferLoadB96_4(operands(0), operands(1), operands(2), operands(3), mods))
    ),
    BufferInstructionDefs.BufferLoadD16B16 -> decoder(
      form(3)((operands, mods) => Instruction.BufferLoadD16B16_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.BufferLoadD16B16_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    BufferInstructionDefs.BufferLoadD16FormatX -> decoder(
      form(3)((operands, mods) => Instruction.BufferLoadD16FormatX_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.BufferLoadD16FormatX_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    BufferInstructionDefs.BufferLoadD16FormatXy -> decoder(
      form(3)((operands, mods) => Instruction.BufferLoadD16FormatXy_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.BufferLoadD16FormatXy_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    BufferInstructionDefs.BufferLoadD16FormatXyz -> decoder(
      form(3)((operands, mods) => Instruction.BufferLoadD16FormatXyz_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.BufferLoadD16FormatXyz_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    BufferInstructionDefs.BufferLoadD16FormatXyzw -> decoder(
      form(3)((operands, mods) => Instruction.BufferLoadD16FormatXyzw_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.BufferLoadD16FormatXyzw_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    BufferInstructionDefs.BufferLoadD16HiB16 -> decoder(
      form(3)((operands, mods) => Instruction.BufferLoadD16HiB16_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.BufferLoadD16HiB16_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    BufferInstructionDefs.BufferLoadD16HiFormatX -> decoder(
      form(3)((operands, mods) => Instruction.BufferLoadD16HiFormatX_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.BufferLoadD16HiFormatX_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    BufferInstructionDefs.BufferLoadD16HiI8 -> decoder(
      form(3)((operands, mods) => Instruction.BufferLoadD16HiI8_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.BufferLoadD16HiI8_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    BufferInstructionDefs.BufferLoadD16HiU8 -> decoder(
      form(3)((operands, mods) => Instruction.BufferLoadD16HiU8_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.BufferLoadD16HiU8_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    BufferInstructionDefs.BufferLoadD16I8 -> decoder(
      form(3)((operands, mods) => Instruction.BufferLoadD16I8_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.BufferLoadD16I8_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    BufferInstructionDefs.BufferLoadD16U8 -> decoder(
      form(3)((operands, mods) => Instruction.BufferLoadD16U8_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.BufferLoadD16U8_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    BufferInstructionDefs.BufferLoadDword -> decoder(
      form(3)((operands, mods) => Instruction.BufferLoadDword_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.BufferLoadDword_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    BufferInstructionDefs.BufferLoadDwordx2 -> decoder(
      form(3)((operands, mods) => Instruction.BufferLoadDwordx2_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.BufferLoadDwordx2_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    BufferInstructionDefs.BufferLoadDwordx3 -> decoder(
      form(3)((operands, mods) => Instruction.BufferLoadDwordx3_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.BufferLoadDwordx3_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    BufferInstructionDefs.BufferLoadDwordx4 -> decoder(
      form(3)((operands, mods) => Instruction.BufferLoadDwordx4_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.BufferLoadDwordx4_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    BufferInstructionDefs.BufferLoadFormatD16HiX -> decoder(
      form(3)((operands, mods) => Instruction.BufferLoadFormatD16HiX_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.BufferLoadFormatD16HiX_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    BufferInstructionDefs.BufferLoadFormatD16X -> decoder(
      form(3)((operands, mods) => Instruction.BufferLoadFormatD16X_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.BufferLoadFormatD16X_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    BufferInstructionDefs.BufferLoadFormatD16Xy -> decoder(
      form(3)((operands, mods) => Instruction.BufferLoadFormatD16Xy_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.BufferLoadFormatD16Xy_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    BufferInstructionDefs.BufferLoadFormatD16Xyz -> decoder(
      form(3)((operands, mods) => Instruction.BufferLoadFormatD16Xyz_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.BufferLoadFormatD16Xyz_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    BufferInstructionDefs.BufferLoadFormatD16Xyzw -> decoder(
      form(3)((operands, mods) => Instruction.BufferLoadFormatD16Xyzw_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.BufferLoadFormatD16Xyzw_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    BufferInstructionDefs.BufferLoadFormatX -> decoder(
      form(3)((operands, mods) => Instruction.BufferLoadFormatX_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.BufferLoadFormatX_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    BufferInstructionDefs.BufferLoadFormatXy -> decoder(
      form(3)((operands, mods) => Instruction.BufferLoadFormatXy_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.BufferLoadFormatXy_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    BufferInstructionDefs.BufferLoadFormatXyz -> decoder(
      form(3)((operands, mods) => Instruction.BufferLoadFormatXyz_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.BufferLoadFormatXyz_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    BufferInstructionDefs.BufferLoadFormatXyzw -> decoder(
      form(3)((operands, mods) => Instruction.BufferLoadFormatXyzw_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.BufferLoadFormatXyzw_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    BufferInstructionDefs.BufferLoadI16 -> decoder(
      form(3)((operands, mods) => Instruction.BufferLoadI16_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) => Instruction.BufferLoadI16_4(operands(0), operands(1), operands(2), operands(3), mods))
    ),
    BufferInstructionDefs.BufferLoadI8 -> decoder(
      form(3)((operands, mods) => Instruction.BufferLoadI8_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) => Instruction.BufferLoadI8_4(operands(0), operands(1), operands(2), operands(3), mods))
    ),
    BufferInstructionDefs.BufferLoadSbyte -> decoder(
      form(3)((operands, mods) => Instruction.BufferLoadSbyte_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.BufferLoadSbyte_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    BufferInstructionDefs.BufferLoadSbyteD16 -> decoder(
      form(3)((operands, mods) => Instruction.BufferLoadSbyteD16_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.BufferLoadSbyteD16_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    BufferInstructionDefs.BufferLoadSbyteD16Hi -> decoder(
      form(3)((operands, mods) => Instruction.BufferLoadSbyteD16Hi_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.BufferLoadSbyteD16Hi_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    BufferInstructionDefs.BufferLoadShortD16 -> decoder(
      form(3)((operands, mods) => Instruction.BufferLoadShortD16_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.BufferLoadShortD16_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    BufferInstructionDefs.BufferLoadShortD16Hi -> decoder(
      form(3)((operands, mods) => Instruction.BufferLoadShortD16Hi_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.BufferLoadShortD16Hi_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    BufferInstructionDefs.BufferLoadSshort -> decoder(
      form(3)((operands, mods) => Instruction.BufferLoadSshort_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.BufferLoadSshort_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    BufferInstructionDefs.BufferLoadU16 -> decoder(
      form(3)((operands, mods) => Instruction.BufferLoadU16_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) => Instruction.BufferLoadU16_4(operands(0), operands(1), operands(2), operands(3), mods))
    ),
    BufferInstructionDefs.BufferLoadU8 -> decoder(
      form(3)((operands, mods) => Instruction.BufferLoadU8_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) => Instruction.BufferLoadU8_4(operands(0), operands(1), operands(2), operands(3), mods))
    ),
    BufferInstructionDefs.BufferLoadUbyte -> decoder(
      form(3)((operands, mods) => Instruction.BufferLoadUbyte_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.BufferLoadUbyte_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    BufferInstructionDefs.BufferLoadUbyteD16 -> decoder(
      form(3)((operands, mods) => Instruction.BufferLoadUbyteD16_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.BufferLoadUbyteD16_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    BufferInstructionDefs.BufferLoadUbyteD16Hi -> decoder(
      form(3)((operands, mods) => Instruction.BufferLoadUbyteD16Hi_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.BufferLoadUbyteD16Hi_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    BufferInstructionDefs.BufferLoadUshort -> decoder(
      form(3)((operands, mods) => Instruction.BufferLoadUshort_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.BufferLoadUshort_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    BufferInstructionDefs.BufferStoreB128 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferStoreB128(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferStoreB16 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferStoreB16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferStoreB32 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferStoreB32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferStoreB64 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferStoreB64(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferStoreB8 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferStoreB8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferStoreB96 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferStoreB96(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferStoreByte -> decoder(form(4)((operands, mods) =>
      Instruction.BufferStoreByte(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferStoreByteD16Hi -> decoder(form(4)((operands, mods) =>
      Instruction.BufferStoreByteD16Hi(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferStoreD16FormatX -> decoder(form(4)((operands, mods) =>
      Instruction.BufferStoreD16FormatX(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferStoreD16FormatXy -> decoder(form(4)((operands, mods) =>
      Instruction.BufferStoreD16FormatXy(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferStoreD16FormatXyz -> decoder(form(4)((operands, mods) =>
      Instruction.BufferStoreD16FormatXyz(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferStoreD16FormatXyzw -> decoder(form(4)((operands, mods) =>
      Instruction.BufferStoreD16FormatXyzw(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferStoreD16HiB16 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferStoreD16HiB16(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferStoreD16HiB8 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferStoreD16HiB8(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferStoreD16HiFormatX -> decoder(form(4)((operands, mods) =>
      Instruction.BufferStoreD16HiFormatX(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferStoreDword -> decoder(form(4)((operands, mods) =>
      Instruction.BufferStoreDword(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferStoreDwordx2 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferStoreDwordx2(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferStoreDwordx3 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferStoreDwordx3(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferStoreDwordx4 -> decoder(form(4)((operands, mods) =>
      Instruction.BufferStoreDwordx4(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferStoreFormatD16HiX -> decoder(form(4)((operands, mods) =>
      Instruction.BufferStoreFormatD16HiX(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferStoreFormatD16X -> decoder(form(4)((operands, mods) =>
      Instruction.BufferStoreFormatD16X(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferStoreFormatD16Xy -> decoder(form(4)((operands, mods) =>
      Instruction.BufferStoreFormatD16Xy(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferStoreFormatD16Xyz -> decoder(form(4)((operands, mods) =>
      Instruction.BufferStoreFormatD16Xyz(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferStoreFormatD16Xyzw -> decoder(form(4)((operands, mods) =>
      Instruction.BufferStoreFormatD16Xyzw(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferStoreFormatX -> decoder(form(4)((operands, mods) =>
      Instruction.BufferStoreFormatX(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferStoreFormatXy -> decoder(form(4)((operands, mods) =>
      Instruction.BufferStoreFormatXy(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferStoreFormatXyz -> decoder(form(4)((operands, mods) =>
      Instruction.BufferStoreFormatXyz(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferStoreFormatXyzw -> decoder(form(4)((operands, mods) =>
      Instruction.BufferStoreFormatXyzw(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferStoreLdsDword ->
      decoder(form(2)((operands, mods) => Instruction.BufferStoreLdsDword(operands(0), operands(1), mods))),
    BufferInstructionDefs.BufferStoreShort -> decoder(form(4)((operands, mods) =>
      Instruction.BufferStoreShort(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferStoreShortD16Hi -> decoder(form(4)((operands, mods) =>
      Instruction.BufferStoreShortD16Hi(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.BufferWbinvl1 -> decoder(form(0)((_, mods) => Instruction.BufferWbinvl1(mods))),
    BufferInstructionDefs.BufferWbinvl1Sc -> decoder(form(0)((_, mods) => Instruction.BufferWbinvl1Sc(mods))),
    BufferInstructionDefs.BufferWbinvl1Vol -> decoder(form(0)((_, mods) => Instruction.BufferWbinvl1Vol(mods))),
    BufferInstructionDefs.BufferWbl2 -> decoder(form(0)((_, mods) => Instruction.BufferWbl2(mods))),
    BufferInstructionDefs.TbufferLoadD16FormatX -> decoder(form(4)((operands, mods) =>
      Instruction.TbufferLoadD16FormatX(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.TbufferLoadD16FormatXy -> decoder(form(4)((operands, mods) =>
      Instruction.TbufferLoadD16FormatXy(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.TbufferLoadD16FormatXyz -> decoder(form(4)((operands, mods) =>
      Instruction.TbufferLoadD16FormatXyz(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.TbufferLoadD16FormatXyzw -> decoder(form(4)((operands, mods) =>
      Instruction.TbufferLoadD16FormatXyzw(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.TbufferLoadFormatD16X -> decoder(form(4)((operands, mods) =>
      Instruction.TbufferLoadFormatD16X(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.TbufferLoadFormatD16Xy -> decoder(form(4)((operands, mods) =>
      Instruction.TbufferLoadFormatD16Xy(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.TbufferLoadFormatD16Xyz -> decoder(form(4)((operands, mods) =>
      Instruction.TbufferLoadFormatD16Xyz(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.TbufferLoadFormatD16Xyzw -> decoder(form(4)((operands, mods) =>
      Instruction.TbufferLoadFormatD16Xyzw(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.TbufferLoadFormatX -> decoder(form(4)((operands, mods) =>
      Instruction.TbufferLoadFormatX(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.TbufferLoadFormatXy -> decoder(form(4)((operands, mods) =>
      Instruction.TbufferLoadFormatXy(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.TbufferLoadFormatXyz -> decoder(form(4)((operands, mods) =>
      Instruction.TbufferLoadFormatXyz(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.TbufferLoadFormatXyzw -> decoder(form(4)((operands, mods) =>
      Instruction.TbufferLoadFormatXyzw(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.TbufferStoreD16FormatX -> decoder(form(4)((operands, mods) =>
      Instruction.TbufferStoreD16FormatX(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.TbufferStoreD16FormatXy -> decoder(form(4)((operands, mods) =>
      Instruction.TbufferStoreD16FormatXy(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.TbufferStoreD16FormatXyz -> decoder(form(4)((operands, mods) =>
      Instruction.TbufferStoreD16FormatXyz(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.TbufferStoreD16FormatXyzw -> decoder(form(4)((operands, mods) =>
      Instruction.TbufferStoreD16FormatXyzw(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.TbufferStoreFormatD16X -> decoder(form(4)((operands, mods) =>
      Instruction.TbufferStoreFormatD16X(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.TbufferStoreFormatD16Xy -> decoder(form(4)((operands, mods) =>
      Instruction.TbufferStoreFormatD16Xy(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.TbufferStoreFormatD16Xyz -> decoder(form(4)((operands, mods) =>
      Instruction.TbufferStoreFormatD16Xyz(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.TbufferStoreFormatD16Xyzw -> decoder(form(4)((operands, mods) =>
      Instruction.TbufferStoreFormatD16Xyzw(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.TbufferStoreFormatX -> decoder(form(4)((operands, mods) =>
      Instruction.TbufferStoreFormatX(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.TbufferStoreFormatXy -> decoder(form(4)((operands, mods) =>
      Instruction.TbufferStoreFormatXy(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.TbufferStoreFormatXyz -> decoder(form(4)((operands, mods) =>
      Instruction.TbufferStoreFormatXyz(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    BufferInstructionDefs.TbufferStoreFormatXyzw -> decoder(form(4)((operands, mods) =>
      Instruction.TbufferStoreFormatXyzw(operands(0), operands(1), operands(2), operands(3), mods)
    ))
  )
}
