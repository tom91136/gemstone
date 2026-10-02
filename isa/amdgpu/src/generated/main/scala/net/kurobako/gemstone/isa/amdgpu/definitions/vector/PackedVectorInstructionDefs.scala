// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.collection.*

@javax.annotation.processing.Generated(Array("sbt codegen"))
private object PackedVectorInstructionDefs {
  case object VAccvgprRead extends Opcode("v_accvgpr_read", 1366)

  case object VAccvgprReadB32 extends Opcode("v_accvgpr_read_b32", 1367)

  case object VAccvgprWrite extends Opcode("v_accvgpr_write", 1368)

  case object VAccvgprWriteB32 extends Opcode("v_accvgpr_write_b32", 1369)

  case object VDot2F32Bf16 extends Opcode("v_dot2_f32_bf16", 1868)

  case object VDot2F32F16 extends Opcode("v_dot2_f32_f16", 1869)

  case object VDot2I32I16 extends Opcode("v_dot2_i32_i16", 1870)

  case object VDot2U32U16 extends Opcode("v_dot2_u32_u16", 1871)

  case object VDot4F32Bf8Bf8 extends Opcode("v_dot4_f32_bf8_bf8", 1876)

  case object VDot4F32Bf8Fp8 extends Opcode("v_dot4_f32_bf8_fp8", 1877)

  case object VDot4F32Fp8Bf8 extends Opcode("v_dot4_f32_fp8_bf8", 1878)

  case object VDot4F32Fp8Fp8 extends Opcode("v_dot4_f32_fp8_fp8", 1879)

  case object VDot4I32I8 extends Opcode("v_dot4_i32_i8", 1880)

  case object VDot4I32Iu8 extends Opcode("v_dot4_i32_iu8", 1881)

  case object VDot4U32U8 extends Opcode("v_dot4_u32_u8", 1882)

  case object VDot8I32I4 extends Opcode("v_dot8_i32_i4", 1884)

  case object VDot8I32Iu4 extends Opcode("v_dot8_i32_iu4", 1885)

  case object VDot8U32U4 extends Opcode("v_dot8_u32_u4", 1886)

  case object VFmaMixF32 extends Opcode("v_fma_mix_f32", 1935)

  case object VFmaMixF32Bf16 extends Opcode("v_fma_mix_f32_bf16", 1936)

  case object VFmaMixhiBf16 extends Opcode("v_fma_mixhi_bf16", 1937)

  case object VFmaMixhiF16 extends Opcode("v_fma_mixhi_f16", 1938)

  case object VFmaMixloBf16 extends Opcode("v_fma_mixlo_bf16", 1939)

  case object VFmaMixloF16 extends Opcode("v_fma_mixlo_f16", 1940)

  case object VMadMixF32 extends Opcode("v_mad_mix_f32", 2018)

  case object VMadMixhiF16 extends Opcode("v_mad_mixhi_f16", 2019)

  case object VMadMixloF16 extends Opcode("v_mad_mixlo_f16", 2020)

  case object VPkAddBf16 extends Opcode("v_pk_add_bf16", 2230)

  case object VPkAddF16 extends Opcode("v_pk_add_f16", 2231)

  case object VPkAddF32 extends Opcode("v_pk_add_f32", 2232)

  case object VPkAddF64 extends Opcode("v_pk_add_f64", 2233)

  case object VPkAddI16 extends Opcode("v_pk_add_i16", 2234)

  case object VPkAddMaxI16 extends Opcode("v_pk_add_max_i16", 2235)

  case object VPkAddMaxU16 extends Opcode("v_pk_add_max_u16", 2236)

  case object VPkAddMinI16 extends Opcode("v_pk_add_min_i16", 2237)

  case object VPkAddMinU16 extends Opcode("v_pk_add_min_u16", 2238)

  case object VPkAddNcU64 extends Opcode("v_pk_add_nc_u64", 2239)

  case object VPkAddU16 extends Opcode("v_pk_add_u16", 2240)

  case object VPkAshrrevI16 extends Opcode("v_pk_ashrrev_i16", 2241)

  case object VPkFmaBf16 extends Opcode("v_pk_fma_bf16", 2242)

  case object VPkFmaF16 extends Opcode("v_pk_fma_f16", 2243)

  case object VPkFmaF32 extends Opcode("v_pk_fma_f32", 2244)

  case object VPkFmaF64 extends Opcode("v_pk_fma_f64", 2245)

  case object VPkLshlAddU64 extends Opcode("v_pk_lshl_add_u64", 2247)

  case object VPkLshlrevB16 extends Opcode("v_pk_lshlrev_b16", 2248)

  case object VPkLshrrevB16 extends Opcode("v_pk_lshrrev_b16", 2249)

  case object VPkMadI16 extends Opcode("v_pk_mad_i16", 2250)

  case object VPkMadU16 extends Opcode("v_pk_mad_u16", 2251)

  case object VPkMax3I16 extends Opcode("v_pk_max3_i16", 2252)

  case object VPkMax3NumF16 extends Opcode("v_pk_max3_num_f16", 2253)

  case object VPkMax3U16 extends Opcode("v_pk_max3_u16", 2254)

  case object VPkMaxF16 extends Opcode("v_pk_max_f16", 2255)

  case object VPkMaxI16 extends Opcode("v_pk_max_i16", 2256)

  case object VPkMaxNumBf16 extends Opcode("v_pk_max_num_bf16", 2257)

  case object VPkMaxNumF16 extends Opcode("v_pk_max_num_f16", 2258)

  case object VPkMaxNumF64 extends Opcode("v_pk_max_num_f64", 2259)

  case object VPkMaxU16 extends Opcode("v_pk_max_u16", 2260)

  case object VPkMaximum3F16 extends Opcode("v_pk_maximum3_f16", 2261)

  case object VPkMaximumF16 extends Opcode("v_pk_maximum_f16", 2262)

  case object VPkMin3I16 extends Opcode("v_pk_min3_i16", 2263)

  case object VPkMin3NumF16 extends Opcode("v_pk_min3_num_f16", 2264)

  case object VPkMin3U16 extends Opcode("v_pk_min3_u16", 2265)

  case object VPkMinF16 extends Opcode("v_pk_min_f16", 2266)

  case object VPkMinI16 extends Opcode("v_pk_min_i16", 2267)

  case object VPkMinNumBf16 extends Opcode("v_pk_min_num_bf16", 2268)

  case object VPkMinNumF16 extends Opcode("v_pk_min_num_f16", 2269)

  case object VPkMinNumF64 extends Opcode("v_pk_min_num_f64", 2270)

  case object VPkMinU16 extends Opcode("v_pk_min_u16", 2271)

  case object VPkMinimum3F16 extends Opcode("v_pk_minimum3_f16", 2272)

  case object VPkMinimumF16 extends Opcode("v_pk_minimum_f16", 2273)

  case object VPkMovB32 extends Opcode("v_pk_mov_b32", 2274)

  case object VPkMulBf16 extends Opcode("v_pk_mul_bf16", 2275)

  case object VPkMulF16 extends Opcode("v_pk_mul_f16", 2276)

  case object VPkMulF32 extends Opcode("v_pk_mul_f32", 2277)

  case object VPkMulF64 extends Opcode("v_pk_mul_f64", 2278)

  case object VPkMulLoU16 extends Opcode("v_pk_mul_lo_u16", 2279)

  case object VPkSubI16 extends Opcode("v_pk_sub_i16", 2280)

  case object VPkSubNcU64 extends Opcode("v_pk_sub_nc_u64", 2281)

  case object VPkSubU16 extends Opcode("v_pk_sub_u16", 2282)

  val definitions: Vec[Opcode] = Vec(
    VAccvgprRead,
    VAccvgprReadB32,
    VAccvgprWrite,
    VAccvgprWriteB32,
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
    VPkAddF64,
    VPkAddI16,
    VPkAddMaxI16,
    VPkAddMaxU16,
    VPkAddMinI16,
    VPkAddMinU16,
    VPkAddNcU64,
    VPkAddU16,
    VPkAshrrevI16,
    VPkFmaBf16,
    VPkFmaF16,
    VPkFmaF32,
    VPkFmaF64,
    VPkLshlAddU64,
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
    VPkMaxNumF64,
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
    VPkMinNumF64,
    VPkMinU16,
    VPkMinimum3F16,
    VPkMinimumF16,
    VPkMovB32,
    VPkMulBf16,
    VPkMulF16,
    VPkMulF32,
    VPkMulF64,
    VPkMulLoU16,
    VPkSubI16,
    VPkSubNcU64,
    VPkSubU16
  )
}
