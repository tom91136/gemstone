// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Do not edit by hand.
// scalafix:off GemstoneNoAssume; values were validated by the generator
package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Vec

private[amdgpu] object PackedVectorInstructionDefs {
  case object VAccvgprRead extends InstructionDef(
        AMDGPUOpcode.assume("v_accvgpr_read"),
        decoder(form(2)((operands, mods) => Instruction.VAccvgprRead(operands(0), operands(1), mods))),
        1362
      )
  case object VAccvgprWrite extends InstructionDef(
        AMDGPUOpcode.assume("v_accvgpr_write"),
        decoder(form(2)((operands, mods) => Instruction.VAccvgprWrite(operands(0), operands(1), mods))),
        1363
      )
  case object VDot2F32Bf16 extends InstructionDef(
        AMDGPUOpcode.assume("v_dot2_f32_bf16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VDot2F32Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1823
      )
  case object VDot2F32F16 extends InstructionDef(
        AMDGPUOpcode.assume("v_dot2_f32_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VDot2F32F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1824
      )
  case object VDot2I32I16 extends InstructionDef(
        AMDGPUOpcode.assume("v_dot2_i32_i16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VDot2I32I16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1825
      )
  case object VDot2U32U16 extends InstructionDef(
        AMDGPUOpcode.assume("v_dot2_u32_u16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VDot2U32U16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1826
      )
  case object VDot4F32Bf8Bf8 extends InstructionDef(
        AMDGPUOpcode.assume("v_dot4_f32_bf8_bf8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VDot4F32Bf8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1831
      )
  case object VDot4F32Bf8Fp8 extends InstructionDef(
        AMDGPUOpcode.assume("v_dot4_f32_bf8_fp8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VDot4F32Bf8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1832
      )
  case object VDot4F32Fp8Bf8 extends InstructionDef(
        AMDGPUOpcode.assume("v_dot4_f32_fp8_bf8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VDot4F32Fp8Bf8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1833
      )
  case object VDot4F32Fp8Fp8 extends InstructionDef(
        AMDGPUOpcode.assume("v_dot4_f32_fp8_fp8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VDot4F32Fp8Fp8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1834
      )
  case object VDot4I32I8 extends InstructionDef(
        AMDGPUOpcode.assume("v_dot4_i32_i8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VDot4I32I8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1835
      )
  case object VDot4I32Iu8 extends InstructionDef(
        AMDGPUOpcode.assume("v_dot4_i32_iu8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VDot4I32Iu8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1836
      )
  case object VDot4U32U8 extends InstructionDef(
        AMDGPUOpcode.assume("v_dot4_u32_u8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VDot4U32U8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1837
      )
  case object VDot8I32I4 extends InstructionDef(
        AMDGPUOpcode.assume("v_dot8_i32_i4"),
        decoder(form(4)((operands, mods) =>
          Instruction.VDot8I32I4(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1839
      )
  case object VDot8I32Iu4 extends InstructionDef(
        AMDGPUOpcode.assume("v_dot8_i32_iu4"),
        decoder(form(4)((operands, mods) =>
          Instruction.VDot8I32Iu4(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1840
      )
  case object VDot8U32U4 extends InstructionDef(
        AMDGPUOpcode.assume("v_dot8_u32_u4"),
        decoder(form(4)((operands, mods) =>
          Instruction.VDot8U32U4(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1841
      )
  case object VFmaMixF32 extends InstructionDef(
        AMDGPUOpcode.assume("v_fma_mix_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VFmaMixF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1890
      )
  case object VFmaMixF32Bf16 extends InstructionDef(
        AMDGPUOpcode.assume("v_fma_mix_f32_bf16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VFmaMixF32Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1891
      )
  case object VFmaMixhiBf16 extends InstructionDef(
        AMDGPUOpcode.assume("v_fma_mixhi_bf16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VFmaMixhiBf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1892
      )
  case object VFmaMixhiF16 extends InstructionDef(
        AMDGPUOpcode.assume("v_fma_mixhi_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VFmaMixhiF16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1893
      )
  case object VFmaMixloBf16 extends InstructionDef(
        AMDGPUOpcode.assume("v_fma_mixlo_bf16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VFmaMixloBf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1894
      )
  case object VFmaMixloF16 extends InstructionDef(
        AMDGPUOpcode.assume("v_fma_mixlo_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VFmaMixloF16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1895
      )
  case object VMadMixF32 extends InstructionDef(
        AMDGPUOpcode.assume("v_mad_mix_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMadMixF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1967
      )
  case object VMadMixhiF16 extends InstructionDef(
        AMDGPUOpcode.assume("v_mad_mixhi_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMadMixhiF16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1968
      )
  case object VMadMixloF16 extends InstructionDef(
        AMDGPUOpcode.assume("v_mad_mixlo_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMadMixloF16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1969
      )
  case object VPkAddBf16 extends InstructionDef(
        AMDGPUOpcode.assume("v_pk_add_bf16"),
        decoder(form(3)((operands, mods) => Instruction.VPkAddBf16(operands(0), operands(1), operands(2), mods))),
        2182
      )
  case object VPkAddF16 extends InstructionDef(
        AMDGPUOpcode.assume("v_pk_add_f16"),
        decoder(form(3)((operands, mods) => Instruction.VPkAddF16(operands(0), operands(1), operands(2), mods))),
        2183
      )
  case object VPkAddF32 extends InstructionDef(
        AMDGPUOpcode.assume("v_pk_add_f32"),
        decoder(form(3)((operands, mods) => Instruction.VPkAddF32(operands(0), operands(1), operands(2), mods))),
        2184
      )
  case object VPkAddI16 extends InstructionDef(
        AMDGPUOpcode.assume("v_pk_add_i16"),
        decoder(form(3)((operands, mods) => Instruction.VPkAddI16(operands(0), operands(1), operands(2), mods))),
        2185
      )
  case object VPkAddMaxI16 extends InstructionDef(
        AMDGPUOpcode.assume("v_pk_add_max_i16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VPkAddMaxI16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2186
      )
  case object VPkAddMaxU16 extends InstructionDef(
        AMDGPUOpcode.assume("v_pk_add_max_u16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VPkAddMaxU16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2187
      )
  case object VPkAddMinI16 extends InstructionDef(
        AMDGPUOpcode.assume("v_pk_add_min_i16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VPkAddMinI16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2188
      )
  case object VPkAddMinU16 extends InstructionDef(
        AMDGPUOpcode.assume("v_pk_add_min_u16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VPkAddMinU16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2189
      )
  case object VPkAddU16 extends InstructionDef(
        AMDGPUOpcode.assume("v_pk_add_u16"),
        decoder(form(3)((operands, mods) => Instruction.VPkAddU16(operands(0), operands(1), operands(2), mods))),
        2190
      )
  case object VPkAshrrevI16 extends InstructionDef(
        AMDGPUOpcode.assume("v_pk_ashrrev_i16"),
        decoder(form(3)((operands, mods) => Instruction.VPkAshrrevI16(operands(0), operands(1), operands(2), mods))),
        2191
      )
  case object VPkFmaBf16 extends InstructionDef(
        AMDGPUOpcode.assume("v_pk_fma_bf16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VPkFmaBf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2192
      )
  case object VPkFmaF16 extends InstructionDef(
        AMDGPUOpcode.assume("v_pk_fma_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VPkFmaF16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2193
      )
  case object VPkFmaF32 extends InstructionDef(
        AMDGPUOpcode.assume("v_pk_fma_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VPkFmaF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2194
      )
  case object VPkLshlrevB16 extends InstructionDef(
        AMDGPUOpcode.assume("v_pk_lshlrev_b16"),
        decoder(form(3)((operands, mods) => Instruction.VPkLshlrevB16(operands(0), operands(1), operands(2), mods))),
        2196
      )
  case object VPkLshrrevB16 extends InstructionDef(
        AMDGPUOpcode.assume("v_pk_lshrrev_b16"),
        decoder(form(3)((operands, mods) => Instruction.VPkLshrrevB16(operands(0), operands(1), operands(2), mods))),
        2197
      )
  case object VPkMadI16 extends InstructionDef(
        AMDGPUOpcode.assume("v_pk_mad_i16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VPkMadI16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2198
      )
  case object VPkMadU16 extends InstructionDef(
        AMDGPUOpcode.assume("v_pk_mad_u16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VPkMadU16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2199
      )
  case object VPkMax3I16 extends InstructionDef(
        AMDGPUOpcode.assume("v_pk_max3_i16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VPkMax3I16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2200
      )
  case object VPkMax3NumF16 extends InstructionDef(
        AMDGPUOpcode.assume("v_pk_max3_num_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VPkMax3NumF16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2201
      )
  case object VPkMax3U16 extends InstructionDef(
        AMDGPUOpcode.assume("v_pk_max3_u16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VPkMax3U16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2202
      )
  case object VPkMaxF16 extends InstructionDef(
        AMDGPUOpcode.assume("v_pk_max_f16"),
        decoder(form(3)((operands, mods) => Instruction.VPkMaxF16(operands(0), operands(1), operands(2), mods))),
        2203
      )
  case object VPkMaxI16 extends InstructionDef(
        AMDGPUOpcode.assume("v_pk_max_i16"),
        decoder(form(3)((operands, mods) => Instruction.VPkMaxI16(operands(0), operands(1), operands(2), mods))),
        2204
      )
  case object VPkMaxNumBf16 extends InstructionDef(
        AMDGPUOpcode.assume("v_pk_max_num_bf16"),
        decoder(form(3)((operands, mods) => Instruction.VPkMaxNumBf16(operands(0), operands(1), operands(2), mods))),
        2205
      )
  case object VPkMaxNumF16 extends InstructionDef(
        AMDGPUOpcode.assume("v_pk_max_num_f16"),
        decoder(form(3)((operands, mods) => Instruction.VPkMaxNumF16(operands(0), operands(1), operands(2), mods))),
        2206
      )
  case object VPkMaxU16 extends InstructionDef(
        AMDGPUOpcode.assume("v_pk_max_u16"),
        decoder(form(3)((operands, mods) => Instruction.VPkMaxU16(operands(0), operands(1), operands(2), mods))),
        2207
      )
  case object VPkMaximum3F16 extends InstructionDef(
        AMDGPUOpcode.assume("v_pk_maximum3_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VPkMaximum3F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2208
      )
  case object VPkMaximumF16 extends InstructionDef(
        AMDGPUOpcode.assume("v_pk_maximum_f16"),
        decoder(form(3)((operands, mods) => Instruction.VPkMaximumF16(operands(0), operands(1), operands(2), mods))),
        2209
      )
  case object VPkMin3I16 extends InstructionDef(
        AMDGPUOpcode.assume("v_pk_min3_i16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VPkMin3I16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2210
      )
  case object VPkMin3NumF16 extends InstructionDef(
        AMDGPUOpcode.assume("v_pk_min3_num_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VPkMin3NumF16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2211
      )
  case object VPkMin3U16 extends InstructionDef(
        AMDGPUOpcode.assume("v_pk_min3_u16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VPkMin3U16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2212
      )
  case object VPkMinF16 extends InstructionDef(
        AMDGPUOpcode.assume("v_pk_min_f16"),
        decoder(form(3)((operands, mods) => Instruction.VPkMinF16(operands(0), operands(1), operands(2), mods))),
        2213
      )
  case object VPkMinI16 extends InstructionDef(
        AMDGPUOpcode.assume("v_pk_min_i16"),
        decoder(form(3)((operands, mods) => Instruction.VPkMinI16(operands(0), operands(1), operands(2), mods))),
        2214
      )
  case object VPkMinNumBf16 extends InstructionDef(
        AMDGPUOpcode.assume("v_pk_min_num_bf16"),
        decoder(form(3)((operands, mods) => Instruction.VPkMinNumBf16(operands(0), operands(1), operands(2), mods))),
        2215
      )
  case object VPkMinNumF16 extends InstructionDef(
        AMDGPUOpcode.assume("v_pk_min_num_f16"),
        decoder(form(3)((operands, mods) => Instruction.VPkMinNumF16(operands(0), operands(1), operands(2), mods))),
        2216
      )
  case object VPkMinU16 extends InstructionDef(
        AMDGPUOpcode.assume("v_pk_min_u16"),
        decoder(form(3)((operands, mods) => Instruction.VPkMinU16(operands(0), operands(1), operands(2), mods))),
        2217
      )
  case object VPkMinimum3F16 extends InstructionDef(
        AMDGPUOpcode.assume("v_pk_minimum3_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VPkMinimum3F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2218
      )
  case object VPkMinimumF16 extends InstructionDef(
        AMDGPUOpcode.assume("v_pk_minimum_f16"),
        decoder(form(3)((operands, mods) => Instruction.VPkMinimumF16(operands(0), operands(1), operands(2), mods))),
        2219
      )
  case object VPkMovB32 extends InstructionDef(
        AMDGPUOpcode.assume("v_pk_mov_b32"),
        decoder(form(3)((operands, mods) => Instruction.VPkMovB32(operands(0), operands(1), operands(2), mods))),
        2220
      )
  case object VPkMulBf16 extends InstructionDef(
        AMDGPUOpcode.assume("v_pk_mul_bf16"),
        decoder(form(3)((operands, mods) => Instruction.VPkMulBf16(operands(0), operands(1), operands(2), mods))),
        2221
      )
  case object VPkMulF16 extends InstructionDef(
        AMDGPUOpcode.assume("v_pk_mul_f16"),
        decoder(form(3)((operands, mods) => Instruction.VPkMulF16(operands(0), operands(1), operands(2), mods))),
        2222
      )
  case object VPkMulF32 extends InstructionDef(
        AMDGPUOpcode.assume("v_pk_mul_f32"),
        decoder(form(3)((operands, mods) => Instruction.VPkMulF32(operands(0), operands(1), operands(2), mods))),
        2223
      )
  case object VPkMulLoU16 extends InstructionDef(
        AMDGPUOpcode.assume("v_pk_mul_lo_u16"),
        decoder(form(3)((operands, mods) => Instruction.VPkMulLoU16(operands(0), operands(1), operands(2), mods))),
        2224
      )
  case object VPkSubI16 extends InstructionDef(
        AMDGPUOpcode.assume("v_pk_sub_i16"),
        decoder(form(3)((operands, mods) => Instruction.VPkSubI16(operands(0), operands(1), operands(2), mods))),
        2225
      )
  case object VPkSubU16 extends InstructionDef(
        AMDGPUOpcode.assume("v_pk_sub_u16"),
        decoder(form(3)((operands, mods) => Instruction.VPkSubU16(operands(0), operands(1), operands(2), mods))),
        2226
      )

  val definitions: Vec[InstructionDef] = Vec(
    VAccvgprRead,
    VAccvgprWrite,
    VDot2F32Bf16,
    VDot2F32F16,
    VDot2I32I16,
    VDot2U32U16,
    VDot4F32Bf8Bf8,
    VDot4F32Bf8Fp8,
    VDot4F32Fp8Bf8,
    VDot4F32Fp8Fp8,
    VDot4I32I8,
    VDot4I32Iu8,
    VDot4U32U8,
    VDot8I32I4,
    VDot8I32Iu4,
    VDot8U32U4,
    VFmaMixF32,
    VFmaMixF32Bf16,
    VFmaMixhiBf16,
    VFmaMixhiF16,
    VFmaMixloBf16,
    VFmaMixloF16,
    VMadMixF32,
    VMadMixhiF16,
    VMadMixloF16,
    VPkAddBf16,
    VPkAddF16,
    VPkAddF32,
    VPkAddI16,
    VPkAddMaxI16,
    VPkAddMaxU16,
    VPkAddMinI16,
    VPkAddMinU16,
    VPkAddU16,
    VPkAshrrevI16,
    VPkFmaBf16,
    VPkFmaF16,
    VPkFmaF32,
    VPkLshlrevB16,
    VPkLshrrevB16,
    VPkMadI16,
    VPkMadU16,
    VPkMax3I16,
    VPkMax3NumF16,
    VPkMax3U16,
    VPkMaxF16,
    VPkMaxI16,
    VPkMaxNumBf16,
    VPkMaxNumF16,
    VPkMaxU16,
    VPkMaximum3F16,
    VPkMaximumF16,
    VPkMin3I16,
    VPkMin3NumF16,
    VPkMin3U16,
    VPkMinF16,
    VPkMinI16,
    VPkMinNumBf16,
    VPkMinNumF16,
    VPkMinU16,
    VPkMinimum3F16,
    VPkMinimumF16,
    VPkMovB32,
    VPkMulBf16,
    VPkMulF16,
    VPkMulF32,
    VPkMulLoU16,
    VPkSubI16,
    VPkSubU16
  )
}
