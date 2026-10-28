// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu

import javax.annotation.processing.Generated

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.collection.*

@Generated(Array("sbt codegen"))
private object VOP3InstructionDefs {
  case object VAdd3U32 extends Opcode("v_add3_u32", 1370)

  case object VAddI16 extends Opcode("v_add_i16", 1376)

  case object VAddLshlU32 extends Opcode("v_add_lshl_u32", 1378)

  case object VAddMaxI32 extends Opcode("v_add_max_i32", 1379)

  case object VAddMaxU32 extends Opcode("v_add_max_u32", 1380)

  case object VAddMinI32 extends Opcode("v_add_min_i32", 1381)

  case object VAddMinU32 extends Opcode("v_add_min_u32", 1382)

  case object VAddNcI16 extends Opcode("v_add_nc_i16", 1383)

  case object VAddNcI32 extends Opcode("v_add_nc_i32", 1384)

  case object VAddNcU16 extends Opcode("v_add_nc_u16", 1385)

  case object VAlignbitB32 extends Opcode("v_alignbit_b32", 1392)

  case object VAlignbyteB32 extends Opcode("v_alignbyte_b32", 1393)

  case object VAndB16 extends Opcode("v_and_b16", 1394)

  case object VAndOrB32 extends Opcode("v_and_or_b32", 1396)

  case object VAshrI64 extends Opcode("v_ashr_i64", 1398)

  case object VAshrPkI8I32 extends Opcode("v_ashr_pk_i8_i32", 1399)

  case object VAshrPkU8I32 extends Opcode("v_ashr_pk_u8_i32", 1400)

  case object VAshrrevI64 extends Opcode("v_ashrrev_i64", 1403)

  case object VBfeI32 extends Opcode("v_bfe_i32", 1405)

  case object VBfeU32 extends Opcode("v_bfe_u32", 1406)

  case object VBfiB32 extends Opcode("v_bfi_b32", 1407)

  case object VBitop3B16 extends Opcode("v_bitop3_b16", 1410)

  case object VBitop3B32 extends Opcode("v_bitop3_b32", 1411)

  case object VCndmaskB16 extends Opcode("v_cndmask_b16", 1686)

  case object VCubeidF32 extends Opcode("v_cubeid_f32", 1692)

  case object VCubemaF32 extends Opcode("v_cubema_f32", 1693)

  case object VCubescF32 extends Opcode("v_cubesc_f32", 1694)

  case object VCubetcF32 extends Opcode("v_cubetc_f32", 1695)

  case object VCvtPkBf16F32 extends Opcode("v_cvt_pk_bf16_f32", 1725)

  case object VCvtPkBf8F16 extends Opcode("v_cvt_pk_bf8_f16", 1726)

  case object VCvtPkBf8F32 extends Opcode("v_cvt_pk_bf8_f32", 1727)

  case object VCvtPkF16F32 extends Opcode("v_cvt_pk_f16_f32", 1729)

  case object VCvtPkFp8F16 extends Opcode("v_cvt_pk_fp8_f16", 1733)

  case object VCvtPkFp8F32 extends Opcode("v_cvt_pk_fp8_f32", 1734)

  case object VCvtPkI16F32 extends Opcode("v_cvt_pk_i16_f32", 1735)

  case object VCvtPkNormI16F16_1 extends Opcode("v_cvt_pk_norm_i16_f16", 1737)

  case object VCvtPkNormI16F32_1 extends Opcode("v_cvt_pk_norm_i16_f32", 1738)

  case object VCvtPkNormU16F16_1 extends Opcode("v_cvt_pk_norm_u16_f16", 1739)

  case object VCvtPkNormU16F32_1 extends Opcode("v_cvt_pk_norm_u16_f32", 1740)

  case object VCvtPkU16F32 extends Opcode("v_cvt_pk_u16_f32", 1742)

  case object VCvtPkU8F32 extends Opcode("v_cvt_pk_u8_f32", 1744)

  case object VCvtPknormI16F16_2 extends Opcode("v_cvt_pknorm_i16_f16", 1746)

  case object VCvtPknormU16F16_2 extends Opcode("v_cvt_pknorm_u16_f16", 1748)

  case object VCvtScalePk16Bf16Bf6 extends Opcode("v_cvt_scale_pk16_bf16_bf6", 1752)

  case object VCvtScalePk16Bf16Fp6 extends Opcode("v_cvt_scale_pk16_bf16_fp6", 1753)

  case object VCvtScalePk16F16Bf6 extends Opcode("v_cvt_scale_pk16_f16_bf6", 1754)

  case object VCvtScalePk16F16Fp6 extends Opcode("v_cvt_scale_pk16_f16_fp6", 1755)

  case object VCvtScalePk16F32Bf6 extends Opcode("v_cvt_scale_pk16_f32_bf6", 1756)

  case object VCvtScalePk16F32Fp6 extends Opcode("v_cvt_scale_pk16_f32_fp6", 1757)

  case object VCvtScalePk8Bf16Bf8 extends Opcode("v_cvt_scale_pk8_bf16_bf8", 1758)

  case object VCvtScalePk8Bf16Fp4 extends Opcode("v_cvt_scale_pk8_bf16_fp4", 1759)

  case object VCvtScalePk8Bf16Fp8 extends Opcode("v_cvt_scale_pk8_bf16_fp8", 1760)

  case object VCvtScalePk8F16Bf8 extends Opcode("v_cvt_scale_pk8_f16_bf8", 1761)

  case object VCvtScalePk8F16Fp4 extends Opcode("v_cvt_scale_pk8_f16_fp4", 1762)

  case object VCvtScalePk8F16Fp8 extends Opcode("v_cvt_scale_pk8_f16_fp8", 1763)

  case object VCvtScalePk8F32Bf8 extends Opcode("v_cvt_scale_pk8_f32_bf8", 1764)

  case object VCvtScalePk8F32Fp4 extends Opcode("v_cvt_scale_pk8_f32_fp4", 1765)

  case object VCvtScalePk8F32Fp8 extends Opcode("v_cvt_scale_pk8_f32_fp8", 1766)

  case object VCvtScalef322xpk16Bf6F32 extends Opcode("v_cvt_scalef32_2xpk16_bf6_f32", 1767)

  case object VCvtScalef322xpk16Fp6F32 extends Opcode("v_cvt_scalef32_2xpk16_fp6_f32", 1768)

  case object VCvtScalef32F16Bf8 extends Opcode("v_cvt_scalef32_f16_bf8", 1769)

  case object VCvtScalef32F16Fp8 extends Opcode("v_cvt_scalef32_f16_fp8", 1770)

  case object VCvtScalef32F32Bf8 extends Opcode("v_cvt_scalef32_f32_bf8", 1771)

  case object VCvtScalef32F32Fp8 extends Opcode("v_cvt_scalef32_f32_fp8", 1772)

  case object VCvtScalef32Pk16Bf6Bf16 extends Opcode("v_cvt_scalef32_pk16_bf6_bf16", 1773)

  case object VCvtScalef32Pk16Bf6F16 extends Opcode("v_cvt_scalef32_pk16_bf6_f16", 1774)

  case object VCvtScalef32Pk16Bf6F32 extends Opcode("v_cvt_scalef32_pk16_bf6_f32", 1775)

  case object VCvtScalef32Pk16Fp6Bf16 extends Opcode("v_cvt_scalef32_pk16_fp6_bf16", 1776)

  case object VCvtScalef32Pk16Fp6F16 extends Opcode("v_cvt_scalef32_pk16_fp6_f16", 1777)

  case object VCvtScalef32Pk16Fp6F32 extends Opcode("v_cvt_scalef32_pk16_fp6_f32", 1778)

  case object VCvtScalef32Pk32Bf16Bf6 extends Opcode("v_cvt_scalef32_pk32_bf16_bf6", 1779)

  case object VCvtScalef32Pk32Bf16Fp6 extends Opcode("v_cvt_scalef32_pk32_bf16_fp6", 1780)

  case object VCvtScalef32Pk32Bf6Bf16 extends Opcode("v_cvt_scalef32_pk32_bf6_bf16", 1781)

  case object VCvtScalef32Pk32Bf6F16 extends Opcode("v_cvt_scalef32_pk32_bf6_f16", 1782)

  case object VCvtScalef32Pk32F16Bf6 extends Opcode("v_cvt_scalef32_pk32_f16_bf6", 1783)

  case object VCvtScalef32Pk32F16Fp6 extends Opcode("v_cvt_scalef32_pk32_f16_fp6", 1784)

  case object VCvtScalef32Pk32F32Bf6 extends Opcode("v_cvt_scalef32_pk32_f32_bf6", 1785)

  case object VCvtScalef32Pk32F32Fp6 extends Opcode("v_cvt_scalef32_pk32_f32_fp6", 1786)

  case object VCvtScalef32Pk32Fp6Bf16 extends Opcode("v_cvt_scalef32_pk32_fp6_bf16", 1787)

  case object VCvtScalef32Pk32Fp6F16 extends Opcode("v_cvt_scalef32_pk32_fp6_f16", 1788)

  case object VCvtScalef32Pk8Bf8Bf16 extends Opcode("v_cvt_scalef32_pk8_bf8_bf16", 1789)

  case object VCvtScalef32Pk8Bf8F16 extends Opcode("v_cvt_scalef32_pk8_bf8_f16", 1790)

  case object VCvtScalef32Pk8Bf8F32 extends Opcode("v_cvt_scalef32_pk8_bf8_f32", 1791)

  case object VCvtScalef32Pk8Fp4Bf16 extends Opcode("v_cvt_scalef32_pk8_fp4_bf16", 1792)

  case object VCvtScalef32Pk8Fp4F16 extends Opcode("v_cvt_scalef32_pk8_fp4_f16", 1793)

  case object VCvtScalef32Pk8Fp4F32 extends Opcode("v_cvt_scalef32_pk8_fp4_f32", 1794)

  case object VCvtScalef32Pk8Fp8Bf16 extends Opcode("v_cvt_scalef32_pk8_fp8_bf16", 1795)

  case object VCvtScalef32Pk8Fp8F16 extends Opcode("v_cvt_scalef32_pk8_fp8_f16", 1796)

  case object VCvtScalef32Pk8Fp8F32 extends Opcode("v_cvt_scalef32_pk8_fp8_f32", 1797)

  case object VCvtScalef32PkBf16Bf8 extends Opcode("v_cvt_scalef32_pk_bf16_bf8", 1798)

  case object VCvtScalef32PkBf16Fp4 extends Opcode("v_cvt_scalef32_pk_bf16_fp4", 1799)

  case object VCvtScalef32PkBf16Fp8 extends Opcode("v_cvt_scalef32_pk_bf16_fp8", 1800)

  case object VCvtScalef32PkBf8Bf16 extends Opcode("v_cvt_scalef32_pk_bf8_bf16", 1801)

  case object VCvtScalef32PkBf8F16 extends Opcode("v_cvt_scalef32_pk_bf8_f16", 1802)

  case object VCvtScalef32PkBf8F32 extends Opcode("v_cvt_scalef32_pk_bf8_f32", 1803)

  case object VCvtScalef32PkF16Bf8 extends Opcode("v_cvt_scalef32_pk_f16_bf8", 1804)

  case object VCvtScalef32PkF16Fp4 extends Opcode("v_cvt_scalef32_pk_f16_fp4", 1805)

  case object VCvtScalef32PkF16Fp8 extends Opcode("v_cvt_scalef32_pk_f16_fp8", 1806)

  case object VCvtScalef32PkF32Bf8 extends Opcode("v_cvt_scalef32_pk_f32_bf8", 1807)

  case object VCvtScalef32PkF32Fp4 extends Opcode("v_cvt_scalef32_pk_f32_fp4", 1808)

  case object VCvtScalef32PkF32Fp8 extends Opcode("v_cvt_scalef32_pk_f32_fp8", 1809)

  case object VCvtScalef32PkFp4Bf16 extends Opcode("v_cvt_scalef32_pk_fp4_bf16", 1810)

  case object VCvtScalef32PkFp4F16 extends Opcode("v_cvt_scalef32_pk_fp4_f16", 1811)

  case object VCvtScalef32PkFp4F32 extends Opcode("v_cvt_scalef32_pk_fp4_f32", 1812)

  case object VCvtScalef32PkFp8Bf16 extends Opcode("v_cvt_scalef32_pk_fp8_bf16", 1813)

  case object VCvtScalef32PkFp8F16 extends Opcode("v_cvt_scalef32_pk_fp8_f16", 1814)

  case object VCvtScalef32PkFp8F32 extends Opcode("v_cvt_scalef32_pk_fp8_f32", 1815)

  case object VCvtScalef32SrBf8Bf16 extends Opcode("v_cvt_scalef32_sr_bf8_bf16", 1816)

  case object VCvtScalef32SrBf8F16 extends Opcode("v_cvt_scalef32_sr_bf8_f16", 1817)

  case object VCvtScalef32SrBf8F32 extends Opcode("v_cvt_scalef32_sr_bf8_f32", 1818)

  case object VCvtScalef32SrFp8Bf16 extends Opcode("v_cvt_scalef32_sr_fp8_bf16", 1819)

  case object VCvtScalef32SrFp8F16 extends Opcode("v_cvt_scalef32_sr_fp8_f16", 1820)

  case object VCvtScalef32SrFp8F32 extends Opcode("v_cvt_scalef32_sr_fp8_f32", 1821)

  case object VCvtScalef32SrPk16Bf6Bf16 extends Opcode("v_cvt_scalef32_sr_pk16_bf6_bf16", 1822)

  case object VCvtScalef32SrPk16Bf6F16 extends Opcode("v_cvt_scalef32_sr_pk16_bf6_f16", 1823)

  case object VCvtScalef32SrPk16Bf6F32 extends Opcode("v_cvt_scalef32_sr_pk16_bf6_f32", 1824)

  case object VCvtScalef32SrPk16Fp6Bf16 extends Opcode("v_cvt_scalef32_sr_pk16_fp6_bf16", 1825)

  case object VCvtScalef32SrPk16Fp6F16 extends Opcode("v_cvt_scalef32_sr_pk16_fp6_f16", 1826)

  case object VCvtScalef32SrPk16Fp6F32 extends Opcode("v_cvt_scalef32_sr_pk16_fp6_f32", 1827)

  case object VCvtScalef32SrPk32Bf6Bf16 extends Opcode("v_cvt_scalef32_sr_pk32_bf6_bf16", 1828)

  case object VCvtScalef32SrPk32Bf6F16 extends Opcode("v_cvt_scalef32_sr_pk32_bf6_f16", 1829)

  case object VCvtScalef32SrPk32Bf6F32 extends Opcode("v_cvt_scalef32_sr_pk32_bf6_f32", 1830)

  case object VCvtScalef32SrPk32Fp6Bf16 extends Opcode("v_cvt_scalef32_sr_pk32_fp6_bf16", 1831)

  case object VCvtScalef32SrPk32Fp6F16 extends Opcode("v_cvt_scalef32_sr_pk32_fp6_f16", 1832)

  case object VCvtScalef32SrPk32Fp6F32 extends Opcode("v_cvt_scalef32_sr_pk32_fp6_f32", 1833)

  case object VCvtScalef32SrPk8Bf8Bf16 extends Opcode("v_cvt_scalef32_sr_pk8_bf8_bf16", 1834)

  case object VCvtScalef32SrPk8Bf8F16 extends Opcode("v_cvt_scalef32_sr_pk8_bf8_f16", 1835)

  case object VCvtScalef32SrPk8Bf8F32 extends Opcode("v_cvt_scalef32_sr_pk8_bf8_f32", 1836)

  case object VCvtScalef32SrPk8Fp4Bf16 extends Opcode("v_cvt_scalef32_sr_pk8_fp4_bf16", 1837)

  case object VCvtScalef32SrPk8Fp4F16 extends Opcode("v_cvt_scalef32_sr_pk8_fp4_f16", 1838)

  case object VCvtScalef32SrPk8Fp4F32 extends Opcode("v_cvt_scalef32_sr_pk8_fp4_f32", 1839)

  case object VCvtScalef32SrPk8Fp8Bf16 extends Opcode("v_cvt_scalef32_sr_pk8_fp8_bf16", 1840)

  case object VCvtScalef32SrPk8Fp8F16 extends Opcode("v_cvt_scalef32_sr_pk8_fp8_f16", 1841)

  case object VCvtScalef32SrPk8Fp8F32 extends Opcode("v_cvt_scalef32_sr_pk8_fp8_f32", 1842)

  case object VCvtScalef32SrPkFp4Bf16 extends Opcode("v_cvt_scalef32_sr_pk_fp4_bf16", 1843)

  case object VCvtScalef32SrPkFp4F16 extends Opcode("v_cvt_scalef32_sr_pk_fp4_f16", 1844)

  case object VCvtScalef32SrPkFp4F32 extends Opcode("v_cvt_scalef32_sr_pk_fp4_f32", 1845)

  case object VCvtSrBf16F32 extends Opcode("v_cvt_sr_bf16_f32", 1846)

  case object VCvtSrBf8F16 extends Opcode("v_cvt_sr_bf8_f16", 1847)

  case object VCvtSrBf8F32 extends Opcode("v_cvt_sr_bf8_f32", 1848)

  case object VCvtSrF16F32 extends Opcode("v_cvt_sr_f16_f32", 1849)

  case object VCvtSrFp8F16 extends Opcode("v_cvt_sr_fp8_f16", 1850)

  case object VCvtSrFp8F32 extends Opcode("v_cvt_sr_fp8_f32", 1851)

  case object VCvtSrPkBf16F32 extends Opcode("v_cvt_sr_pk_bf16_f32", 1852)

  case object VCvtSrPkF16F32 extends Opcode("v_cvt_sr_pk_f16_f32", 1853)

  case object VDivFixupF16 extends Opcode("v_div_fixup_f16", 1858)

  case object VDivFixupF32 extends Opcode("v_div_fixup_f32", 1859)

  case object VDivFixupF64 extends Opcode("v_div_fixup_f64", 1860)

  case object VDivFixupLegacyF16 extends Opcode("v_div_fixup_legacy_f16", 1861)

  case object VDivFmasF32 extends Opcode("v_div_fmas_f32", 1862)

  case object VDivFmasF64 extends Opcode("v_div_fmas_f64", 1863)

  case object VDivScaleF32 extends Opcode("v_div_scale_f32", 1864)

  case object VDivScaleF64 extends Opcode("v_div_scale_f64", 1865)

  case object VDot2Bf16Bf16 extends Opcode("v_dot2_bf16_bf16", 1866)

  case object VDot2F16F16 extends Opcode("v_dot2_f16_f16", 1867)

  case object VFmaDx9ZeroF32 extends Opcode("v_fma_dx9_zero_f32", 1929)

  case object VFmaF16 extends Opcode("v_fma_f16", 1930)

  case object VFmaF32 extends Opcode("v_fma_f32", 1931)

  case object VFmaF64 extends Opcode("v_fma_f64", 1932)

  case object VFmaLegacyF16 extends Opcode("v_fma_legacy_f16", 1933)

  case object VFmaLegacyF32 extends Opcode("v_fma_legacy_f32", 1934)

  case object VInterpMovF32 extends Opcode("v_interp_mov_f32", 1962)

  case object VInterpP1F32 extends Opcode("v_interp_p1_f32", 1969)

  case object VInterpP1llF16 extends Opcode("v_interp_p1ll_f16", 1970)

  case object VInterpP1lvF16 extends Opcode("v_interp_p1lv_f16", 1971)

  case object VInterpP2F16 extends Opcode("v_interp_p2_f16", 1972)

  case object VInterpP2F32 extends Opcode("v_interp_p2_f32", 1975)

  case object VInterpP2HiF16 extends Opcode("v_interp_p2_hi_f16", 1977)

  case object VInterpP2LegacyF16 extends Opcode("v_interp_p2_legacy_f16", 1978)

  case object VLdexpF64 extends Opcode("v_ldexp_f64", 1983)

  case object VLerpU8 extends Opcode("v_lerp_u8", 1984)

  case object VLshlAddU32 extends Opcode("v_lshl_add_u32", 1990)

  case object VLshlAddU64 extends Opcode("v_lshl_add_u64", 1991)

  case object VLshlB64 extends Opcode("v_lshl_b64", 1993)

  case object VLshlOrB32 extends Opcode("v_lshl_or_b32", 1994)

  case object VLshrB64 extends Opcode("v_lshr_b64", 1999)

  case object VLshrrevB64 extends Opcode("v_lshrrev_b64", 2002)

  case object VMadCoI64I32 extends Opcode("v_mad_co_i64_i32", 2006)

  case object VMadCoU64U32 extends Opcode("v_mad_co_u64_u32", 2007)

  case object VMadF16 extends Opcode("v_mad_f16", 2008)

  case object VMadF32 extends Opcode("v_mad_f32", 2009)

  case object VMadI16 extends Opcode("v_mad_i16", 2010)

  case object VMadI32I16 extends Opcode("v_mad_i32_i16", 2011)

  case object VMadI32I24 extends Opcode("v_mad_i32_i24", 2012)

  case object VMadI64I32 extends Opcode("v_mad_i64_i32", 2013)

  case object VMadLegacyF16 extends Opcode("v_mad_legacy_f16", 2014)

  case object VMadLegacyF32 extends Opcode("v_mad_legacy_f32", 2015)

  case object VMadLegacyI16 extends Opcode("v_mad_legacy_i16", 2016)

  case object VMadLegacyU16 extends Opcode("v_mad_legacy_u16", 2017)

  case object VMadNcI64I32 extends Opcode("v_mad_nc_i64_i32", 2021)

  case object VMadNcU64U32 extends Opcode("v_mad_nc_u64_u32", 2022)

  case object VMadU16 extends Opcode("v_mad_u16", 2023)

  case object VMadU32 extends Opcode("v_mad_u32", 2024)

  case object VMadU32U16 extends Opcode("v_mad_u32_u16", 2025)

  case object VMadU32U24 extends Opcode("v_mad_u32_u24", 2026)

  case object VMadU64U32 extends Opcode("v_mad_u64_u32", 2027)

  case object VMax3F16 extends Opcode("v_max3_f16", 2032)

  case object VMax3F32 extends Opcode("v_max3_f32", 2033)

  case object VMax3I16 extends Opcode("v_max3_i16", 2034)

  case object VMax3I32 extends Opcode("v_max3_i32", 2035)

  case object VMax3NumF16 extends Opcode("v_max3_num_f16", 2036)

  case object VMax3NumF32 extends Opcode("v_max3_num_f32", 2037)

  case object VMax3U16 extends Opcode("v_max3_u16", 2038)

  case object VMax3U32 extends Opcode("v_max3_u32", 2039)

  case object VMaxF64 extends Opcode("v_max_f64", 2042)

  case object VMaxI64 extends Opcode("v_max_i64", 2045)

  case object VMaxU64 extends Opcode("v_max_u64", 2052)

  case object VMaximum3F16 extends Opcode("v_maximum3_f16", 2053)

  case object VMaximum3F32 extends Opcode("v_maximum3_f32", 2054)

  case object VMaximumF16 extends Opcode("v_maximum_f16", 2055)

  case object VMaximumF32 extends Opcode("v_maximum_f32", 2056)

  case object VMaximumF64 extends Opcode("v_maximum_f64", 2057)

  case object VMaximumminimumF16 extends Opcode("v_maximumminimum_f16", 2058)

  case object VMaximumminimumF32 extends Opcode("v_maximumminimum_f32", 2059)

  case object VMaxminF16 extends Opcode("v_maxmin_f16", 2060)

  case object VMaxminF32 extends Opcode("v_maxmin_f32", 2061)

  case object VMaxminI32 extends Opcode("v_maxmin_i32", 2062)

  case object VMaxminNumF16 extends Opcode("v_maxmin_num_f16", 2063)

  case object VMaxminNumF32 extends Opcode("v_maxmin_num_f32", 2064)

  case object VMaxminU32 extends Opcode("v_maxmin_u32", 2065)

  case object VMed3F16 extends Opcode("v_med3_f16", 2068)

  case object VMed3F32 extends Opcode("v_med3_f32", 2069)

  case object VMed3I16 extends Opcode("v_med3_i16", 2070)

  case object VMed3I32 extends Opcode("v_med3_i32", 2071)

  case object VMed3NumF16 extends Opcode("v_med3_num_f16", 2072)

  case object VMed3NumF32 extends Opcode("v_med3_num_f32", 2073)

  case object VMed3U16 extends Opcode("v_med3_u16", 2074)

  case object VMed3U32 extends Opcode("v_med3_u32", 2075)

  case object VMin3F16 extends Opcode("v_min3_f16", 2146)

  case object VMin3F32 extends Opcode("v_min3_f32", 2147)

  case object VMin3I16 extends Opcode("v_min3_i16", 2148)

  case object VMin3I32 extends Opcode("v_min3_i32", 2149)

  case object VMin3NumF16 extends Opcode("v_min3_num_f16", 2150)

  case object VMin3NumF32 extends Opcode("v_min3_num_f32", 2151)

  case object VMin3U16 extends Opcode("v_min3_u16", 2152)

  case object VMin3U32 extends Opcode("v_min3_u32", 2153)

  case object VMinF64 extends Opcode("v_min_f64", 2156)

  case object VMinI64 extends Opcode("v_min_i64", 2159)

  case object VMinU64 extends Opcode("v_min_u64", 2166)

  case object VMinimum3F16 extends Opcode("v_minimum3_f16", 2167)

  case object VMinimum3F32 extends Opcode("v_minimum3_f32", 2168)

  case object VMinimumF16 extends Opcode("v_minimum_f16", 2169)

  case object VMinimumF32 extends Opcode("v_minimum_f32", 2170)

  case object VMinimumF64 extends Opcode("v_minimum_f64", 2171)

  case object VMinimummaximumF16 extends Opcode("v_minimummaximum_f16", 2172)

  case object VMinimummaximumF32 extends Opcode("v_minimummaximum_f32", 2173)

  case object VMinmaxF16 extends Opcode("v_minmax_f16", 2174)

  case object VMinmaxF32 extends Opcode("v_minmax_f32", 2175)

  case object VMinmaxI32 extends Opcode("v_minmax_i32", 2176)

  case object VMinmaxNumF16 extends Opcode("v_minmax_num_f16", 2177)

  case object VMinmaxNumF32 extends Opcode("v_minmax_num_f32", 2178)

  case object VMinmaxU32 extends Opcode("v_minmax_u32", 2179)

  case object VMqsadPkU16U8 extends Opcode("v_mqsad_pk_u16_u8", 2187)

  case object VMqsadU32U8 extends Opcode("v_mqsad_u32_u8", 2188)

  case object VMsadU8 extends Opcode("v_msad_u8", 2189)

  case object VMulHiI32 extends Opcode("v_mul_hi_i32", 2194)

  case object VMulHiU32 extends Opcode("v_mul_hi_u32", 2196)

  case object VMulLoI32 extends Opcode("v_mul_lo_i32", 2200)

  case object VMulLoU32 extends Opcode("v_mul_lo_u32", 2202)

  case object VMullitF32 extends Opcode("v_mullit_f32", 2205)

  case object VOr3B32 extends Opcode("v_or3_b32", 2209)

  case object VOrB16 extends Opcode("v_or_b16", 2210)

  case object VPackB32F16 extends Opcode("v_pack_b32_f16", 2212)

  case object VPermB32 extends Opcode("v_perm_b32", 2213)

  case object VPermPk16B4U4 extends Opcode("v_perm_pk16_b4_u4", 2214)

  case object VPermPk16B6U4 extends Opcode("v_perm_pk16_b6_u4", 2215)

  case object VPermPk16B8U4 extends Opcode("v_perm_pk16_b8_u4", 2216)

  case object VPermlane16B32 extends Opcode("v_permlane16_b32", 2217)

  case object VPermlane16VarB32 extends Opcode("v_permlane16_var_b32", 2219)

  case object VPermlaneBcastB32 extends Opcode("v_permlane_bcast_b32", 2222)

  case object VPermlaneDownB32 extends Opcode("v_permlane_down_b32", 2223)

  case object VPermlaneIdxGenB32 extends Opcode("v_permlane_idx_gen_b32", 2224)

  case object VPermlaneUpB32 extends Opcode("v_permlane_up_b32", 2225)

  case object VPermlaneXorB32 extends Opcode("v_permlane_xor_b32", 2226)

  case object VPermlanex16B32 extends Opcode("v_permlanex16_b32", 2227)

  case object VPermlanex16VarB32 extends Opcode("v_permlanex16_var_b32", 2228)

  case object VQsadPkU16U8 extends Opcode("v_qsad_pk_u16_u8", 2284)

  case object VSExpF16 extends Opcode("v_s_exp_f16", 2305)

  case object VSExpF32 extends Opcode("v_s_exp_f32", 2306)

  case object VSLogF16 extends Opcode("v_s_log_f16", 2307)

  case object VSLogF32 extends Opcode("v_s_log_f32", 2308)

  case object VSRcpF16 extends Opcode("v_s_rcp_f16", 2309)

  case object VSRcpF32 extends Opcode("v_s_rcp_f32", 2310)

  case object VSRsqF16 extends Opcode("v_s_rsq_f16", 2311)

  case object VSRsqF32 extends Opcode("v_s_rsq_f32", 2312)

  case object VSSqrtF16 extends Opcode("v_s_sqrt_f16", 2313)

  case object VSSqrtF32 extends Opcode("v_s_sqrt_f32", 2314)

  case object VSadHiU8 extends Opcode("v_sad_hi_u8", 2315)

  case object VSadU16 extends Opcode("v_sad_u16", 2316)

  case object VSadU32 extends Opcode("v_sad_u32", 2317)

  case object VSadU8 extends Opcode("v_sad_u8", 2318)

  case object VSubI16 extends Opcode("v_sub_i16", 2362)

  case object VSubNcI16 extends Opcode("v_sub_nc_i16", 2364)

  case object VSubNcI32 extends Opcode("v_sub_nc_i32", 2365)

  case object VSubNcU16 extends Opcode("v_sub_nc_u16", 2366)

  case object VTrigPreopF64 extends Opcode("v_trig_preop_f64", 2414)

  case object VXadU32 extends Opcode("v_xad_u32", 2462)

  case object VXor3B32 extends Opcode("v_xor3_b32", 2464)

  case object VXorB16 extends Opcode("v_xor_b16", 2465)

  val definitions: Vec[Opcode] = Vec(
    VAdd3U32,
    VAddI16,
    VAddLshlU32,
    VAddMaxI32,
    VAddMaxU32,
    VAddMinI32,
    VAddMinU32,
    VAddNcI16,
    VAddNcI32,
    VAddNcU16,
    VAlignbitB32,
    VAlignbyteB32,
    VAndB16,
    VAndOrB32,
    VAshrI64,
    VAshrPkI8I32,
    VAshrPkU8I32,
    VAshrrevI64,
    VBfeI32,
    VBfeU32,
    VBfiB32,
    VBitop3B16,
    VBitop3B32,
    VCndmaskB16,
    VCubeidF32,
    VCubemaF32,
    VCubescF32,
    VCubetcF32,
    VCvtPkBf16F32,
    VCvtPkBf8F16,
    VCvtPkBf8F32,
    VCvtPkF16F32,
    VCvtPkFp8F16,
    VCvtPkFp8F32,
    VCvtPkI16F32,
    VCvtPkNormI16F16_1,
    VCvtPkNormI16F32_1,
    VCvtPkNormU16F16_1,
    VCvtPkNormU16F32_1,
    VCvtPkU16F32,
    VCvtPkU8F32,
    VCvtPknormI16F16_2,
    VCvtPknormU16F16_2,
    VCvtScalePk16Bf16Bf6,
    VCvtScalePk16Bf16Fp6,
    VCvtScalePk16F16Bf6,
    VCvtScalePk16F16Fp6,
    VCvtScalePk16F32Bf6,
    VCvtScalePk16F32Fp6,
    VCvtScalePk8Bf16Bf8,
    VCvtScalePk8Bf16Fp4,
    VCvtScalePk8Bf16Fp8,
    VCvtScalePk8F16Bf8,
    VCvtScalePk8F16Fp4,
    VCvtScalePk8F16Fp8,
    VCvtScalePk8F32Bf8,
    VCvtScalePk8F32Fp4,
    VCvtScalePk8F32Fp8,
    VCvtScalef322xpk16Bf6F32,
    VCvtScalef322xpk16Fp6F32,
    VCvtScalef32F16Bf8,
    VCvtScalef32F16Fp8,
    VCvtScalef32F32Bf8,
    VCvtScalef32F32Fp8,
    VCvtScalef32Pk16Bf6Bf16,
    VCvtScalef32Pk16Bf6F16,
    VCvtScalef32Pk16Bf6F32,
    VCvtScalef32Pk16Fp6Bf16,
    VCvtScalef32Pk16Fp6F16,
    VCvtScalef32Pk16Fp6F32,
    VCvtScalef32Pk32Bf16Bf6,
    VCvtScalef32Pk32Bf16Fp6,
    VCvtScalef32Pk32Bf6Bf16,
    VCvtScalef32Pk32Bf6F16,
    VCvtScalef32Pk32F16Bf6,
    VCvtScalef32Pk32F16Fp6,
    VCvtScalef32Pk32F32Bf6,
    VCvtScalef32Pk32F32Fp6,
    VCvtScalef32Pk32Fp6Bf16,
    VCvtScalef32Pk32Fp6F16,
    VCvtScalef32Pk8Bf8Bf16,
    VCvtScalef32Pk8Bf8F16,
    VCvtScalef32Pk8Bf8F32,
    VCvtScalef32Pk8Fp4Bf16,
    VCvtScalef32Pk8Fp4F16,
    VCvtScalef32Pk8Fp4F32,
    VCvtScalef32Pk8Fp8Bf16,
    VCvtScalef32Pk8Fp8F16,
    VCvtScalef32Pk8Fp8F32,
    VCvtScalef32PkBf16Bf8,
    VCvtScalef32PkBf16Fp4,
    VCvtScalef32PkBf16Fp8,
    VCvtScalef32PkBf8Bf16,
    VCvtScalef32PkBf8F16,
    VCvtScalef32PkBf8F32,
    VCvtScalef32PkF16Bf8,
    VCvtScalef32PkF16Fp4,
    VCvtScalef32PkF16Fp8,
    VCvtScalef32PkF32Bf8,
    VCvtScalef32PkF32Fp4,
    VCvtScalef32PkF32Fp8,
    VCvtScalef32PkFp4Bf16,
    VCvtScalef32PkFp4F16,
    VCvtScalef32PkFp4F32,
    VCvtScalef32PkFp8Bf16,
    VCvtScalef32PkFp8F16,
    VCvtScalef32PkFp8F32,
    VCvtScalef32SrBf8Bf16,
    VCvtScalef32SrBf8F16,
    VCvtScalef32SrBf8F32,
    VCvtScalef32SrFp8Bf16,
    VCvtScalef32SrFp8F16,
    VCvtScalef32SrFp8F32,
    VCvtScalef32SrPk16Bf6Bf16,
    VCvtScalef32SrPk16Bf6F16,
    VCvtScalef32SrPk16Bf6F32,
    VCvtScalef32SrPk16Fp6Bf16,
    VCvtScalef32SrPk16Fp6F16,
    VCvtScalef32SrPk16Fp6F32,
    VCvtScalef32SrPk32Bf6Bf16,
    VCvtScalef32SrPk32Bf6F16,
    VCvtScalef32SrPk32Bf6F32,
    VCvtScalef32SrPk32Fp6Bf16,
    VCvtScalef32SrPk32Fp6F16,
    VCvtScalef32SrPk32Fp6F32,
    VCvtScalef32SrPk8Bf8Bf16,
    VCvtScalef32SrPk8Bf8F16,
    VCvtScalef32SrPk8Bf8F32,
    VCvtScalef32SrPk8Fp4Bf16,
    VCvtScalef32SrPk8Fp4F16,
    VCvtScalef32SrPk8Fp4F32,
    VCvtScalef32SrPk8Fp8Bf16,
    VCvtScalef32SrPk8Fp8F16,
    VCvtScalef32SrPk8Fp8F32,
    VCvtScalef32SrPkFp4Bf16,
    VCvtScalef32SrPkFp4F16,
    VCvtScalef32SrPkFp4F32,
    VCvtSrBf16F32,
    VCvtSrBf8F16,
    VCvtSrBf8F32,
    VCvtSrF16F32,
    VCvtSrFp8F16,
    VCvtSrFp8F32,
    VCvtSrPkBf16F32,
    VCvtSrPkF16F32,
    VDivFixupF16,
    VDivFixupF32,
    VDivFixupF64,
    VDivFixupLegacyF16,
    VDivFmasF32,
    VDivFmasF64,
    VDivScaleF32,
    VDivScaleF64,
    VDot2Bf16Bf16,
    VDot2F16F16,
    VFmaDx9ZeroF32,
    VFmaF16,
    VFmaF32,
    VFmaF64,
    VFmaLegacyF16,
    VFmaLegacyF32,
    VInterpMovF32,
    VInterpP1F32,
    VInterpP1llF16,
    VInterpP1lvF16,
    VInterpP2F16,
    VInterpP2F32,
    VInterpP2HiF16,
    VInterpP2LegacyF16,
    VLdexpF64,
    VLerpU8,
    VLshlAddU32,
    VLshlAddU64,
    VLshlB64,
    VLshlOrB32,
    VLshrB64,
    VLshrrevB64,
    VMadCoI64I32,
    VMadCoU64U32,
    VMadF16,
    VMadF32,
    VMadI16,
    VMadI32I16,
    VMadI32I24,
    VMadI64I32,
    VMadLegacyF16,
    VMadLegacyF32,
    VMadLegacyI16,
    VMadLegacyU16,
    VMadNcI64I32,
    VMadNcU64U32,
    VMadU16,
    VMadU32,
    VMadU32U16,
    VMadU32U24,
    VMadU64U32,
    VMax3F16,
    VMax3F32,
    VMax3I16,
    VMax3I32,
    VMax3NumF16,
    VMax3NumF32,
    VMax3U16,
    VMax3U32,
    VMaxF64,
    VMaxI64,
    VMaxU64,
    VMaximum3F16,
    VMaximum3F32,
    VMaximumF16,
    VMaximumF32,
    VMaximumF64,
    VMaximumminimumF16,
    VMaximumminimumF32,
    VMaxminF16,
    VMaxminF32,
    VMaxminI32,
    VMaxminNumF16,
    VMaxminNumF32,
    VMaxminU32,
    VMed3F16,
    VMed3F32,
    VMed3I16,
    VMed3I32,
    VMed3NumF16,
    VMed3NumF32,
    VMed3U16,
    VMed3U32,
    VMin3F16,
    VMin3F32,
    VMin3I16,
    VMin3I32,
    VMin3NumF16,
    VMin3NumF32,
    VMin3U16,
    VMin3U32,
    VMinF64,
    VMinI64,
    VMinU64,
    VMinimum3F16,
    VMinimum3F32,
    VMinimumF16,
    VMinimumF32,
    VMinimumF64,
    VMinimummaximumF16,
    VMinimummaximumF32,
    VMinmaxF16,
    VMinmaxF32,
    VMinmaxI32,
    VMinmaxNumF16,
    VMinmaxNumF32,
    VMinmaxU32,
    VMqsadPkU16U8,
    VMqsadU32U8,
    VMsadU8,
    VMulHiI32,
    VMulHiU32,
    VMulLoI32,
    VMulLoU32,
    VMullitF32,
    VOr3B32,
    VOrB16,
    VPackB32F16,
    VPermB32,
    VPermPk16B4U4,
    VPermPk16B6U4,
    VPermPk16B8U4,
    VPermlane16B32,
    VPermlane16VarB32,
    VPermlaneBcastB32,
    VPermlaneDownB32,
    VPermlaneIdxGenB32,
    VPermlaneUpB32,
    VPermlaneXorB32,
    VPermlanex16B32,
    VPermlanex16VarB32,
    VQsadPkU16U8,
    VSExpF16,
    VSExpF32,
    VSLogF16,
    VSLogF32,
    VSRcpF16,
    VSRcpF32,
    VSRsqF16,
    VSRsqF32,
    VSSqrtF16,
    VSSqrtF32,
    VSadHiU8,
    VSadU16,
    VSadU32,
    VSadU8,
    VSubI16,
    VSubNcI16,
    VSubNcI32,
    VSubNcU16,
    VTrigPreopF64,
    VXadU32,
    VXor3B32,
    VXorB16
  )
}
