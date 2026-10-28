// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu

import javax.annotation.processing.Generated

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.collection.*

@Generated(Array("sbt codegen"))
private object VOP2InstructionDefs {
  case object VAddCoCiU32 extends Opcode("v_add_co_ci_u32", 1371)

  case object VAddCoU32 extends Opcode("v_add_co_u32", 1372)

  case object VAddF16 extends Opcode("v_add_f16", 1373)

  case object VAddF32 extends Opcode("v_add_f32", 1374)

  case object VAddF64 extends Opcode("v_add_f64", 1375)

  case object VAddI32 extends Opcode("v_add_i32", 1377)

  case object VAddNcU32 extends Opcode("v_add_nc_u32", 1386)

  case object VAddNcU64 extends Opcode("v_add_nc_u64", 1387)

  case object VAddU16 extends Opcode("v_add_u16", 1388)

  case object VAddU32 extends Opcode("v_add_u32", 1389)

  case object VAddcCoU32 extends Opcode("v_addc_co_u32", 1390)

  case object VAddcU32 extends Opcode("v_addc_u32", 1391)

  case object VAndB32 extends Opcode("v_and_b32", 1395)

  case object VAshrI32 extends Opcode("v_ashr_i32", 1397)

  case object VAshrrevI16 extends Opcode("v_ashrrev_i16", 1401)

  case object VAshrrevI32 extends Opcode("v_ashrrev_i32", 1402)

  case object VBcntU32B32 extends Opcode("v_bcnt_u32_b32", 1404)

  case object VBfmB32 extends Opcode("v_bfm_b32", 1408)

  case object VCndmaskB32 extends Opcode("v_cndmask_b32", 1687)

  case object VCvtPkI16I32 extends Opcode("v_cvt_pk_i16_i32", 1736)

  case object VCvtPkRtzF16F32_1 extends Opcode("v_cvt_pk_rtz_f16_f32", 1741)

  case object VCvtPkU16U32 extends Opcode("v_cvt_pk_u16_u32", 1743)

  case object VCvtPkaccumU8F32 extends Opcode("v_cvt_pkaccum_u8_f32", 1745)

  case object VCvtPknormI16F32_2 extends Opcode("v_cvt_pknorm_i16_f32", 1747)

  case object VCvtPknormU16F32_2 extends Opcode("v_cvt_pknorm_u16_f32", 1749)

  case object VCvtPkrtzF16F32_2 extends Opcode("v_cvt_pkrtz_f16_f32", 1750)

  case object VDot2accF32F16 extends Opcode("v_dot2acc_f32_f16", 1872)

  case object VDot2cF32Bf16 extends Opcode("v_dot2c_f32_bf16", 1873)

  case object VDot2cF32F16 extends Opcode("v_dot2c_f32_f16", 1874)

  case object VDot2cI32I16 extends Opcode("v_dot2c_i32_i16", 1875)

  case object VDot4cI32I8 extends Opcode("v_dot4c_i32_i8", 1883)

  case object VDot8cI32I4 extends Opcode("v_dot8c_i32_i4", 1887)

  case object VFmaakF16 extends Opcode("v_fmaak_f16", 1941)

  case object VFmaakF32 extends Opcode("v_fmaak_f32", 1942)

  case object VFmaakF64 extends Opcode("v_fmaak_f64", 1943)

  case object VFmacDx9ZeroF32 extends Opcode("v_fmac_dx9_zero_f32", 1944)

  case object VFmacF16 extends Opcode("v_fmac_f16", 1945)

  case object VFmacF32 extends Opcode("v_fmac_f32", 1946)

  case object VFmacF64 extends Opcode("v_fmac_f64", 1947)

  case object VFmacLegacyF32 extends Opcode("v_fmac_legacy_f32", 1948)

  case object VFmamkF16 extends Opcode("v_fmamk_f16", 1949)

  case object VFmamkF32 extends Opcode("v_fmamk_f32", 1950)

  case object VFmamkF64 extends Opcode("v_fmamk_f64", 1951)

  case object VLdexpF16 extends Opcode("v_ldexp_f16", 1981)

  case object VLdexpF32 extends Opcode("v_ldexp_f32", 1982)

  case object VLshlB32 extends Opcode("v_lshl_b32", 1992)

  case object VLshlrevB16 extends Opcode("v_lshlrev_b16", 1995)

  case object VLshlrevB32 extends Opcode("v_lshlrev_b32", 1996)

  case object VLshlrevB64 extends Opcode("v_lshlrev_b64", 1997)

  case object VLshrB32 extends Opcode("v_lshr_b32", 1998)

  case object VLshrrevB16 extends Opcode("v_lshrrev_b16", 2000)

  case object VLshrrevB32 extends Opcode("v_lshrrev_b32", 2001)

  case object VMacF16 extends Opcode("v_mac_f16", 2003)

  case object VMacF32 extends Opcode("v_mac_f32", 2004)

  case object VMacLegacyF32 extends Opcode("v_mac_legacy_f32", 2005)

  case object VMadakF16 extends Opcode("v_madak_f16", 2028)

  case object VMadakF32 extends Opcode("v_madak_f32", 2029)

  case object VMadmkF16 extends Opcode("v_madmk_f16", 2030)

  case object VMadmkF32 extends Opcode("v_madmk_f32", 2031)

  case object VMaxF16 extends Opcode("v_max_f16", 2040)

  case object VMaxF32 extends Opcode("v_max_f32", 2041)

  case object VMaxI16 extends Opcode("v_max_i16", 2043)

  case object VMaxI32 extends Opcode("v_max_i32", 2044)

  case object VMaxLegacyF32 extends Opcode("v_max_legacy_f32", 2046)

  case object VMaxNumF16 extends Opcode("v_max_num_f16", 2047)

  case object VMaxNumF32 extends Opcode("v_max_num_f32", 2048)

  case object VMaxNumF64 extends Opcode("v_max_num_f64", 2049)

  case object VMaxU16 extends Opcode("v_max_u16", 2050)

  case object VMaxU32 extends Opcode("v_max_u32", 2051)

  case object VMbcntHiU32B32 extends Opcode("v_mbcnt_hi_u32_b32", 2066)

  case object VMbcntLoU32B32 extends Opcode("v_mbcnt_lo_u32_b32", 2067)

  case object VMinF16 extends Opcode("v_min_f16", 2154)

  case object VMinF32 extends Opcode("v_min_f32", 2155)

  case object VMinI16 extends Opcode("v_min_i16", 2157)

  case object VMinI32 extends Opcode("v_min_i32", 2158)

  case object VMinLegacyF32 extends Opcode("v_min_legacy_f32", 2160)

  case object VMinNumF16 extends Opcode("v_min_num_f16", 2161)

  case object VMinNumF32 extends Opcode("v_min_num_f32", 2162)

  case object VMinNumF64 extends Opcode("v_min_num_f64", 2163)

  case object VMinU16 extends Opcode("v_min_u16", 2164)

  case object VMinU32 extends Opcode("v_min_u32", 2165)

  case object VMulDx9ZeroF32 extends Opcode("v_mul_dx9_zero_f32", 2190)

  case object VMulF16 extends Opcode("v_mul_f16", 2191)

  case object VMulF32 extends Opcode("v_mul_f32", 2192)

  case object VMulF64 extends Opcode("v_mul_f64", 2193)

  case object VMulHiI32I24 extends Opcode("v_mul_hi_i32_i24", 2195)

  case object VMulHiU32U24 extends Opcode("v_mul_hi_u32_u24", 2197)

  case object VMulI32I24 extends Opcode("v_mul_i32_i24", 2198)

  case object VMulLegacyF32 extends Opcode("v_mul_legacy_f32", 2199)

  case object VMulLoU16 extends Opcode("v_mul_lo_u16", 2201)

  case object VMulU32U24 extends Opcode("v_mul_u32_u24", 2203)

  case object VMulU64 extends Opcode("v_mul_u64", 2204)

  case object VOrB32 extends Opcode("v_or_b32", 2211)

  case object VPkFmacF16 extends Opcode("v_pk_fmac_f16", 2246)

  case object VReadlaneB32 extends Opcode("v_readlane_b32", 2294)

  case object VSubCoCiU32 extends Opcode("v_sub_co_ci_u32", 2358)

  case object VSubCoU32 extends Opcode("v_sub_co_u32", 2359)

  case object VSubF16 extends Opcode("v_sub_f16", 2360)

  case object VSubF32 extends Opcode("v_sub_f32", 2361)

  case object VSubI32 extends Opcode("v_sub_i32", 2363)

  case object VSubNcU32 extends Opcode("v_sub_nc_u32", 2367)

  case object VSubNcU64 extends Opcode("v_sub_nc_u64", 2368)

  case object VSubU16 extends Opcode("v_sub_u16", 2369)

  case object VSubU32 extends Opcode("v_sub_u32", 2370)

  case object VSubbCoU32 extends Opcode("v_subb_co_u32", 2371)

  case object VSubbU32 extends Opcode("v_subb_u32", 2372)

  case object VSubbrevCoU32 extends Opcode("v_subbrev_co_u32", 2373)

  case object VSubbrevU32 extends Opcode("v_subbrev_u32", 2374)

  case object VSubrevCoCiU32 extends Opcode("v_subrev_co_ci_u32", 2375)

  case object VSubrevCoU32 extends Opcode("v_subrev_co_u32", 2376)

  case object VSubrevF16 extends Opcode("v_subrev_f16", 2377)

  case object VSubrevF32 extends Opcode("v_subrev_f32", 2378)

  case object VSubrevI32 extends Opcode("v_subrev_i32", 2379)

  case object VSubrevNcU32 extends Opcode("v_subrev_nc_u32", 2380)

  case object VSubrevU16 extends Opcode("v_subrev_u16", 2381)

  case object VSubrevU32 extends Opcode("v_subrev_u32", 2382)

  case object VWritelaneB32 extends Opcode("v_writelane_b32", 2461)

  case object VXnorB32 extends Opcode("v_xnor_b32", 2463)

  case object VXorB32 extends Opcode("v_xor_b32", 2466)

  val definitions: Vec[Opcode] = Vec(
    VAddCoCiU32,
    VAddCoU32,
    VAddF16,
    VAddF32,
    VAddF64,
    VAddI32,
    VAddNcU32,
    VAddNcU64,
    VAddU16,
    VAddU32,
    VAddcCoU32,
    VAddcU32,
    VAndB32,
    VAshrI32,
    VAshrrevI16,
    VAshrrevI32,
    VBcntU32B32,
    VBfmB32,
    VCndmaskB32,
    VCvtPkI16I32,
    VCvtPkRtzF16F32_1,
    VCvtPkU16U32,
    VCvtPkaccumU8F32,
    VCvtPknormI16F32_2,
    VCvtPknormU16F32_2,
    VCvtPkrtzF16F32_2,
    VDot2accF32F16,
    VDot2cF32Bf16,
    VDot2cF32F16,
    VDot2cI32I16,
    VDot4cI32I8,
    VDot8cI32I4,
    VFmaakF16,
    VFmaakF32,
    VFmaakF64,
    VFmacDx9ZeroF32,
    VFmacF16,
    VFmacF32,
    VFmacF64,
    VFmacLegacyF32,
    VFmamkF16,
    VFmamkF32,
    VFmamkF64,
    VLdexpF16,
    VLdexpF32,
    VLshlB32,
    VLshlrevB16,
    VLshlrevB32,
    VLshlrevB64,
    VLshrB32,
    VLshrrevB16,
    VLshrrevB32,
    VMacF16,
    VMacF32,
    VMacLegacyF32,
    VMadakF16,
    VMadakF32,
    VMadmkF16,
    VMadmkF32,
    VMaxF16,
    VMaxF32,
    VMaxI16,
    VMaxI32,
    VMaxLegacyF32,
    VMaxNumF16,
    VMaxNumF32,
    VMaxNumF64,
    VMaxU16,
    VMaxU32,
    VMbcntHiU32B32,
    VMbcntLoU32B32,
    VMinF16,
    VMinF32,
    VMinI16,
    VMinI32,
    VMinLegacyF32,
    VMinNumF16,
    VMinNumF32,
    VMinNumF64,
    VMinU16,
    VMinU32,
    VMulDx9ZeroF32,
    VMulF16,
    VMulF32,
    VMulF64,
    VMulHiI32I24,
    VMulHiU32U24,
    VMulI32I24,
    VMulLegacyF32,
    VMulLoU16,
    VMulU32U24,
    VMulU64,
    VOrB32,
    VPkFmacF16,
    VReadlaneB32,
    VSubCoCiU32,
    VSubCoU32,
    VSubF16,
    VSubF32,
    VSubI32,
    VSubNcU32,
    VSubNcU64,
    VSubU16,
    VSubU32,
    VSubbCoU32,
    VSubbU32,
    VSubbrevCoU32,
    VSubbrevU32,
    VSubrevCoCiU32,
    VSubrevCoU32,
    VSubrevF16,
    VSubrevF32,
    VSubrevI32,
    VSubrevNcU32,
    VSubrevU16,
    VSubrevU32,
    VWritelaneB32,
    VXnorB32,
    VXorB32
  )
}
