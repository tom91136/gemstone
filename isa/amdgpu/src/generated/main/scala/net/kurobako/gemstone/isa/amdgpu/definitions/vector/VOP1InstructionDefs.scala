// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.collection.*

@javax.annotation.processing.Generated(Array("sbt codegen"))
private object VOP1InstructionDefs {
  case object VAccvgprMovB32 extends Opcode("v_accvgpr_mov_b32", 1365)

  case object VBfrevB32 extends Opcode("v_bfrev_b32", 1409)

  case object VCeilF16 extends Opcode("v_ceil_f16", 1412)

  case object VCeilF32 extends Opcode("v_ceil_f32", 1413)

  case object VCeilF64 extends Opcode("v_ceil_f64", 1414)

  case object VClrexcp extends Opcode("v_clrexcp", 1415)

  case object VClsI32 extends Opcode("v_cls_i32", 1416)

  case object VClzI32U32 extends Opcode("v_clz_i32_u32", 1417)

  case object VCosBf16 extends Opcode("v_cos_bf16", 1688)

  case object VCosF16 extends Opcode("v_cos_f16", 1689)

  case object VCosF32 extends Opcode("v_cos_f32", 1690)

  case object VCtzI32B32 extends Opcode("v_ctz_i32_b32", 1691)

  case object VCvtF16Bf8 extends Opcode("v_cvt_f16_bf8", 1696)

  case object VCvtF16F32 extends Opcode("v_cvt_f16_f32", 1697)

  case object VCvtF16Fp8 extends Opcode("v_cvt_f16_fp8", 1698)

  case object VCvtF16I16 extends Opcode("v_cvt_f16_i16", 1699)

  case object VCvtF16U16 extends Opcode("v_cvt_f16_u16", 1700)

  case object VCvtF32Bf16 extends Opcode("v_cvt_f32_bf16", 1701)

  case object VCvtF32Bf8 extends Opcode("v_cvt_f32_bf8", 1702)

  case object VCvtF32F16 extends Opcode("v_cvt_f32_f16", 1703)

  case object VCvtF32F64 extends Opcode("v_cvt_f32_f64", 1704)

  case object VCvtF32Fp8 extends Opcode("v_cvt_f32_fp8", 1705)

  case object VCvtF32I32 extends Opcode("v_cvt_f32_i32", 1706)

  case object VCvtF32U32 extends Opcode("v_cvt_f32_u32", 1707)

  case object VCvtF32Ubyte0 extends Opcode("v_cvt_f32_ubyte0", 1708)

  case object VCvtF32Ubyte1 extends Opcode("v_cvt_f32_ubyte1", 1709)

  case object VCvtF32Ubyte2 extends Opcode("v_cvt_f32_ubyte2", 1710)

  case object VCvtF32Ubyte3 extends Opcode("v_cvt_f32_ubyte3", 1711)

  case object VCvtF64F32 extends Opcode("v_cvt_f64_f32", 1712)

  case object VCvtF64I32 extends Opcode("v_cvt_f64_i32", 1713)

  case object VCvtF64U32 extends Opcode("v_cvt_f64_u32", 1714)

  case object VCvtFloorI32F32 extends Opcode("v_cvt_floor_i32_f32", 1715)

  case object VCvtFlrI32F32 extends Opcode("v_cvt_flr_i32_f32", 1716)

  case object VCvtI16F16 extends Opcode("v_cvt_i16_f16", 1717)

  case object VCvtI32F32 extends Opcode("v_cvt_i32_f32", 1718)

  case object VCvtI32F64 extends Opcode("v_cvt_i32_f64", 1719)

  case object VCvtI32I16 extends Opcode("v_cvt_i32_i16", 1720)

  case object VCvtNearestI32F32 extends Opcode("v_cvt_nearest_i32_f32", 1721)

  case object VCvtNormI16F16 extends Opcode("v_cvt_norm_i16_f16", 1722)

  case object VCvtNormU16F16 extends Opcode("v_cvt_norm_u16_f16", 1723)

  case object VCvtOffF32I4 extends Opcode("v_cvt_off_f32_i4", 1724)

  case object VCvtPkF16Bf8 extends Opcode("v_cvt_pk_f16_bf8", 1728)

  case object VCvtPkF16Fp8 extends Opcode("v_cvt_pk_f16_fp8", 1730)

  case object VCvtPkF32Bf8 extends Opcode("v_cvt_pk_f32_bf8", 1731)

  case object VCvtPkF32Fp8 extends Opcode("v_cvt_pk_f32_fp8", 1732)

  case object VCvtRpiI32F32 extends Opcode("v_cvt_rpi_i32_f32", 1751)

  case object VCvtU16F16 extends Opcode("v_cvt_u16_f16", 1854)

  case object VCvtU32F32 extends Opcode("v_cvt_u32_f32", 1855)

  case object VCvtU32F64 extends Opcode("v_cvt_u32_f64", 1856)

  case object VCvtU32U16 extends Opcode("v_cvt_u32_u16", 1857)

  case object VExpBf16 extends Opcode("v_exp_bf16", 1919)

  case object VExpF16 extends Opcode("v_exp_f16", 1920)

  case object VExpF32 extends Opcode("v_exp_f32", 1921)

  case object VExpLegacyF32 extends Opcode("v_exp_legacy_f32", 1922)

  case object VFfbhI32 extends Opcode("v_ffbh_i32", 1923)

  case object VFfbhU32 extends Opcode("v_ffbh_u32", 1924)

  case object VFfblB32 extends Opcode("v_ffbl_b32", 1925)

  case object VFloorF16 extends Opcode("v_floor_f16", 1926)

  case object VFloorF32 extends Opcode("v_floor_f32", 1927)

  case object VFloorF64 extends Opcode("v_floor_f64", 1928)

  case object VFractF16 extends Opcode("v_fract_f16", 1952)

  case object VFractF32 extends Opcode("v_fract_f32", 1953)

  case object VFractF64 extends Opcode("v_fract_f64", 1954)

  case object VFrexpExpI16F16 extends Opcode("v_frexp_exp_i16_f16", 1955)

  case object VFrexpExpI32F32 extends Opcode("v_frexp_exp_i32_f32", 1956)

  case object VFrexpExpI32F64 extends Opcode("v_frexp_exp_i32_f64", 1957)

  case object VFrexpMantF16 extends Opcode("v_frexp_mant_f16", 1958)

  case object VFrexpMantF32 extends Opcode("v_frexp_mant_f32", 1959)

  case object VFrexpMantF64 extends Opcode("v_frexp_mant_f64", 1960)

  case object VIllegal extends Opcode("v_illegal", 1961)

  case object VLogBf16 extends Opcode("v_log_bf16", 1985)

  case object VLogClampF32 extends Opcode("v_log_clamp_f32", 1986)

  case object VLogF16 extends Opcode("v_log_f16", 1987)

  case object VLogF32 extends Opcode("v_log_f32", 1988)

  case object VLogLegacyF32 extends Opcode("v_log_legacy_f32", 1989)

  case object VMovB16 extends Opcode("v_mov_b16", 2180)

  case object VMovB32 extends Opcode("v_mov_b32", 2181)

  case object VMovB64 extends Opcode("v_mov_b64", 2182)

  case object VMovreldB32 extends Opcode("v_movreld_b32", 2183)

  case object VMovrelsB32 extends Opcode("v_movrels_b32", 2184)

  case object VMovrelsd2B32 extends Opcode("v_movrelsd_2_b32", 2185)

  case object VMovrelsdB32 extends Opcode("v_movrelsd_b32", 2186)

  case object VNop extends Opcode("v_nop", 2206)

  case object VNotB16 extends Opcode("v_not_b16", 2207)

  case object VNotB32 extends Opcode("v_not_b32", 2208)

  case object VPermlane16SwapB32 extends Opcode("v_permlane16_swap_b32", 2218)

  case object VPermlane32SwapB32 extends Opcode("v_permlane32_swap_b32", 2220)

  case object VPermlane64B32 extends Opcode("v_permlane64_b32", 2221)

  case object VPipeflush extends Opcode("v_pipeflush", 2229)

  case object VPrngB32 extends Opcode("v_prng_b32", 2283)

  case object VRcpBf16 extends Opcode("v_rcp_bf16", 2285)

  case object VRcpClampF32 extends Opcode("v_rcp_clamp_f32", 2286)

  case object VRcpClampF64 extends Opcode("v_rcp_clamp_f64", 2287)

  case object VRcpF16 extends Opcode("v_rcp_f16", 2288)

  case object VRcpF32 extends Opcode("v_rcp_f32", 2289)

  case object VRcpF64 extends Opcode("v_rcp_f64", 2290)

  case object VRcpIflagF32 extends Opcode("v_rcp_iflag_f32", 2291)

  case object VRcpLegacyF32 extends Opcode("v_rcp_legacy_f32", 2292)

  case object VReadfirstlaneB32 extends Opcode("v_readfirstlane_b32", 2293)

  case object VRndneF16 extends Opcode("v_rndne_f16", 2295)

  case object VRndneF32 extends Opcode("v_rndne_f32", 2296)

  case object VRndneF64 extends Opcode("v_rndne_f64", 2297)

  case object VRsqBf16 extends Opcode("v_rsq_bf16", 2298)

  case object VRsqClampF32 extends Opcode("v_rsq_clamp_f32", 2299)

  case object VRsqClampF64 extends Opcode("v_rsq_clamp_f64", 2300)

  case object VRsqF16 extends Opcode("v_rsq_f16", 2301)

  case object VRsqF32 extends Opcode("v_rsq_f32", 2302)

  case object VRsqF64 extends Opcode("v_rsq_f64", 2303)

  case object VRsqLegacyF32 extends Opcode("v_rsq_legacy_f32", 2304)

  case object VSatPk4I4I8 extends Opcode("v_sat_pk4_i4_i8", 2319)

  case object VSatPk4U4U8 extends Opcode("v_sat_pk4_u4_u8", 2320)

  case object VSatPkU8I16 extends Opcode("v_sat_pk_u8_i16", 2321)

  case object VScreenPartition4seB32 extends Opcode("v_screen_partition_4se_b32", 2322)

  case object VSinBf16 extends Opcode("v_sin_bf16", 2323)

  case object VSinF16 extends Opcode("v_sin_f16", 2324)

  case object VSinF32 extends Opcode("v_sin_f32", 2325)

  case object VSqrtBf16 extends Opcode("v_sqrt_bf16", 2354)

  case object VSqrtF16 extends Opcode("v_sqrt_f16", 2355)

  case object VSqrtF32 extends Opcode("v_sqrt_f32", 2356)

  case object VSqrtF64 extends Opcode("v_sqrt_f64", 2357)

  case object VSwapB16 extends Opcode("v_swap_b16", 2383)

  case object VSwapB32 extends Opcode("v_swap_b32", 2384)

  case object VSwaprelB32 extends Opcode("v_swaprel_b32", 2385)

  case object VTanhBf16 extends Opcode("v_tanh_bf16", 2411)

  case object VTanhF16 extends Opcode("v_tanh_f16", 2412)

  case object VTanhF32 extends Opcode("v_tanh_f32", 2413)

  case object VTruncF16 extends Opcode("v_trunc_f16", 2415)

  case object VTruncF32 extends Opcode("v_trunc_f32", 2416)

  case object VTruncF64 extends Opcode("v_trunc_f64", 2417)

  val definitions: Vec[Opcode] = Vec(
    VAccvgprMovB32,
    VBfrevB32,
    VCeilF16,
    VCeilF32,
    VCeilF64,
    VClrexcp,
    VClsI32,
    VClzI32U32,
    VCosBf16,
    VCosF16,
    VCosF32,
    VCtzI32B32,
    VCvtF16Bf8,
    VCvtF16F32,
    VCvtF16Fp8,
    VCvtF16I16,
    VCvtF16U16,
    VCvtF32Bf16,
    VCvtF32Bf8,
    VCvtF32F16,
    VCvtF32F64,
    VCvtF32Fp8,
    VCvtF32I32,
    VCvtF32U32,
    VCvtF32Ubyte0,
    VCvtF32Ubyte1,
    VCvtF32Ubyte2,
    VCvtF32Ubyte3,
    VCvtF64F32,
    VCvtF64I32,
    VCvtF64U32,
    VCvtFloorI32F32,
    VCvtFlrI32F32,
    VCvtI16F16,
    VCvtI32F32,
    VCvtI32F64,
    VCvtI32I16,
    VCvtNearestI32F32,
    VCvtNormI16F16,
    VCvtNormU16F16,
    VCvtOffF32I4,
    VCvtPkF16Bf8,
    VCvtPkF16Fp8,
    VCvtPkF32Bf8,
    VCvtPkF32Fp8,
    VCvtRpiI32F32,
    VCvtU16F16,
    VCvtU32F32,
    VCvtU32F64,
    VCvtU32U16,
    VExpBf16,
    VExpF16,
    VExpF32,
    VExpLegacyF32,
    VFfbhI32,
    VFfbhU32,
    VFfblB32,
    VFloorF16,
    VFloorF32,
    VFloorF64,
    VFractF16,
    VFractF32,
    VFractF64,
    VFrexpExpI16F16,
    VFrexpExpI32F32,
    VFrexpExpI32F64,
    VFrexpMantF16,
    VFrexpMantF32,
    VFrexpMantF64,
    VIllegal,
    VLogBf16,
    VLogClampF32,
    VLogF16,
    VLogF32,
    VLogLegacyF32,
    VMovB16,
    VMovB32,
    VMovB64,
    VMovreldB32,
    VMovrelsB32,
    VMovrelsd2B32,
    VMovrelsdB32,
    VNop,
    VNotB16,
    VNotB32,
    VPermlane16SwapB32,
    VPermlane32SwapB32,
    VPermlane64B32,
    VPipeflush,
    VPrngB32,
    VRcpBf16,
    VRcpClampF32,
    VRcpClampF64,
    VRcpF16,
    VRcpF32,
    VRcpF64,
    VRcpIflagF32,
    VRcpLegacyF32,
    VReadfirstlaneB32,
    VRndneF16,
    VRndneF32,
    VRndneF64,
    VRsqBf16,
    VRsqClampF32,
    VRsqClampF64,
    VRsqF16,
    VRsqF32,
    VRsqF64,
    VRsqLegacyF32,
    VSatPk4I4I8,
    VSatPk4U4U8,
    VSatPkU8I16,
    VScreenPartition4seB32,
    VSinBf16,
    VSinF16,
    VSinF32,
    VSqrtBf16,
    VSqrtF16,
    VSqrtF32,
    VSqrtF64,
    VSwapB16,
    VSwapB32,
    VSwaprelB32,
    VTanhBf16,
    VTanhF16,
    VTanhF32,
    VTruncF16,
    VTruncF32,
    VTruncF64
  )
}
