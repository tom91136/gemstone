// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu

import javax.annotation.processing.Generated

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.collection.*

@Generated(Array("sbt codegen"))
private object MatrixInstructionDefs {
  case object SWaitTensorcnt extends Opcode("s_wait_tensorcnt", 1266)

  case object TensorLoadToLds extends Opcode("tensor_load_to_lds", 1361)

  case object TensorSave extends Opcode("tensor_save", 1362)

  case object TensorStop extends Opcode("tensor_stop", 1363)

  case object TensorStoreFromLds extends Opcode("tensor_store_from_lds", 1364)

  case object VMfmaF3216x16x128F8f6f4 extends Opcode("v_mfma_f32_16x16x128_f8f6f4", 2076)

  case object VMfmaF3216x16x16Bf16 extends Opcode("v_mfma_f32_16x16x16_bf16", 2077)

  case object VMfmaF3216x16x16F16_1 extends Opcode("v_mfma_f32_16x16x16_f16", 2078)

  case object VMfmaF3216x16x16bf161k extends Opcode("v_mfma_f32_16x16x16bf16_1k", 2079)

  case object VMfmaF3216x16x16f16_2 extends Opcode("v_mfma_f32_16x16x16f16", 2080)

  case object VMfmaF3216x16x14bF32 extends Opcode("v_mfma_f32_16x16x1_4b_f32", 2081)

  case object VMfmaF3216x16x1f32 extends Opcode("v_mfma_f32_16x16x1f32", 2082)

  case object VMfmaF3216x16x2bf16 extends Opcode("v_mfma_f32_16x16x2bf16", 2083)

  case object VMfmaF3216x16x32Bf16 extends Opcode("v_mfma_f32_16x16x32_bf16", 2084)

  case object VMfmaF3216x16x32Bf8Bf8 extends Opcode("v_mfma_f32_16x16x32_bf8_bf8", 2085)

  case object VMfmaF3216x16x32Bf8Fp8 extends Opcode("v_mfma_f32_16x16x32_bf8_fp8", 2086)

  case object VMfmaF3216x16x32F16 extends Opcode("v_mfma_f32_16x16x32_f16", 2087)

  case object VMfmaF3216x16x32Fp8Bf8 extends Opcode("v_mfma_f32_16x16x32_fp8_bf8", 2088)

  case object VMfmaF3216x16x32Fp8Fp8 extends Opcode("v_mfma_f32_16x16x32_fp8_fp8", 2089)

  case object VMfmaF3216x16x44bBf16 extends Opcode("v_mfma_f32_16x16x4_4b_bf16", 2090)

  case object VMfmaF3216x16x44bF16 extends Opcode("v_mfma_f32_16x16x4_4b_f16", 2091)

  case object VMfmaF3216x16x4F32_1 extends Opcode("v_mfma_f32_16x16x4_f32", 2092)

  case object VMfmaF3216x16x4bf161k extends Opcode("v_mfma_f32_16x16x4bf16_1k", 2093)

  case object VMfmaF3216x16x4f16 extends Opcode("v_mfma_f32_16x16x4f16", 2094)

  case object VMfmaF3216x16x4f32_2 extends Opcode("v_mfma_f32_16x16x4f32", 2095)

  case object VMfmaF3216x16x8Xf32 extends Opcode("v_mfma_f32_16x16x8_xf32", 2096)

  case object VMfmaF3216x16x8bf16 extends Opcode("v_mfma_f32_16x16x8bf16", 2097)

  case object VMfmaF3232x32x16Bf16 extends Opcode("v_mfma_f32_32x32x16_bf16", 2098)

  case object VMfmaF3232x32x16Bf8Bf8 extends Opcode("v_mfma_f32_32x32x16_bf8_bf8", 2099)

  case object VMfmaF3232x32x16Bf8Fp8 extends Opcode("v_mfma_f32_32x32x16_bf8_fp8", 2100)

  case object VMfmaF3232x32x16F16 extends Opcode("v_mfma_f32_32x32x16_f16", 2101)

  case object VMfmaF3232x32x16Fp8Bf8 extends Opcode("v_mfma_f32_32x32x16_fp8_bf8", 2102)

  case object VMfmaF3232x32x16Fp8Fp8 extends Opcode("v_mfma_f32_32x32x16_fp8_fp8", 2103)

  case object VMfmaF3232x32x12bF32 extends Opcode("v_mfma_f32_32x32x1_2b_f32", 2104)

  case object VMfmaF3232x32x1f32 extends Opcode("v_mfma_f32_32x32x1f32", 2105)

  case object VMfmaF3232x32x2F32_1 extends Opcode("v_mfma_f32_32x32x2_f32", 2106)

  case object VMfmaF3232x32x2bf16 extends Opcode("v_mfma_f32_32x32x2bf16", 2107)

  case object VMfmaF3232x32x2f32_2 extends Opcode("v_mfma_f32_32x32x2f32", 2108)

  case object VMfmaF3232x32x42bBf16 extends Opcode("v_mfma_f32_32x32x4_2b_bf16", 2109)

  case object VMfmaF3232x32x42bF16 extends Opcode("v_mfma_f32_32x32x4_2b_f16", 2110)

  case object VMfmaF3232x32x4Xf32 extends Opcode("v_mfma_f32_32x32x4_xf32", 2111)

  case object VMfmaF3232x32x4bf16 extends Opcode("v_mfma_f32_32x32x4bf16", 2112)

  case object VMfmaF3232x32x4bf161k extends Opcode("v_mfma_f32_32x32x4bf16_1k", 2113)

  case object VMfmaF3232x32x4f16 extends Opcode("v_mfma_f32_32x32x4f16", 2114)

  case object VMfmaF3232x32x64F8f6f4 extends Opcode("v_mfma_f32_32x32x64_f8f6f4", 2115)

  case object VMfmaF3232x32x8Bf16 extends Opcode("v_mfma_f32_32x32x8_bf16", 2116)

  case object VMfmaF3232x32x8F16_1 extends Opcode("v_mfma_f32_32x32x8_f16", 2117)

  case object VMfmaF3232x32x8bf161k extends Opcode("v_mfma_f32_32x32x8bf16_1k", 2118)

  case object VMfmaF3232x32x8f16_2 extends Opcode("v_mfma_f32_32x32x8f16", 2119)

  case object VMfmaF324x4x116bF32 extends Opcode("v_mfma_f32_4x4x1_16b_f32", 2120)

  case object VMfmaF324x4x1f32 extends Opcode("v_mfma_f32_4x4x1f32", 2121)

  case object VMfmaF324x4x2bf16 extends Opcode("v_mfma_f32_4x4x2bf16", 2122)

  case object VMfmaF324x4x416bBf16 extends Opcode("v_mfma_f32_4x4x4_16b_bf16", 2123)

  case object VMfmaF324x4x416bF16 extends Opcode("v_mfma_f32_4x4x4_16b_f16", 2124)

  case object VMfmaF324x4x4bf161k extends Opcode("v_mfma_f32_4x4x4bf16_1k", 2125)

  case object VMfmaF324x4x4f16 extends Opcode("v_mfma_f32_4x4x4f16", 2126)

  case object VMfmaF6416x16x4F64_1 extends Opcode("v_mfma_f64_16x16x4_f64", 2127)

  case object VMfmaF6416x16x4f64_2 extends Opcode("v_mfma_f64_16x16x4f64", 2128)

  case object VMfmaF644x4x44bF64 extends Opcode("v_mfma_f64_4x4x4_4b_f64", 2129)

  case object VMfmaF644x4x4f64 extends Opcode("v_mfma_f64_4x4x4f64", 2130)

  case object VMfmaI3216x16x16i8 extends Opcode("v_mfma_i32_16x16x16i8", 2131)

  case object VMfmaI3216x16x32I8 extends Opcode("v_mfma_i32_16x16x32_i8", 2132)

  case object VMfmaI3216x16x44bI8 extends Opcode("v_mfma_i32_16x16x4_4b_i8", 2133)

  case object VMfmaI3216x16x4i8 extends Opcode("v_mfma_i32_16x16x4i8", 2134)

  case object VMfmaI3216x16x64I8 extends Opcode("v_mfma_i32_16x16x64_i8", 2135)

  case object VMfmaI3232x32x16I8 extends Opcode("v_mfma_i32_32x32x16_i8", 2136)

  case object VMfmaI3232x32x32I8 extends Opcode("v_mfma_i32_32x32x32_i8", 2137)

  case object VMfmaI3232x32x42bI8 extends Opcode("v_mfma_i32_32x32x4_2b_i8", 2138)

  case object VMfmaI3232x32x4i8 extends Opcode("v_mfma_i32_32x32x4i8", 2139)

  case object VMfmaI3232x32x8i8 extends Opcode("v_mfma_i32_32x32x8i8", 2140)

  case object VMfmaI324x4x416bI8 extends Opcode("v_mfma_i32_4x4x4_16b_i8", 2141)

  case object VMfmaI324x4x4i8 extends Opcode("v_mfma_i32_4x4x4i8", 2142)

  case object VMfmaLdScaleB32 extends Opcode("v_mfma_ld_scale_b32", 2143)

  case object VMfmaScaleF3216x16x128F8f6f4 extends Opcode("v_mfma_scale_f32_16x16x128_f8f6f4", 2144)

  case object VMfmaScaleF3232x32x64F8f6f4 extends Opcode("v_mfma_scale_f32_32x32x64_f8f6f4", 2145)

  case object VSmfmacF3216x16x128Bf8Bf8 extends Opcode("v_smfmac_f32_16x16x128_bf8_bf8", 2326)

  case object VSmfmacF3216x16x128Bf8Fp8 extends Opcode("v_smfmac_f32_16x16x128_bf8_fp8", 2327)

  case object VSmfmacF3216x16x128Fp8Bf8 extends Opcode("v_smfmac_f32_16x16x128_fp8_bf8", 2328)

  case object VSmfmacF3216x16x128Fp8Fp8 extends Opcode("v_smfmac_f32_16x16x128_fp8_fp8", 2329)

  case object VSmfmacF3216x16x32Bf16 extends Opcode("v_smfmac_f32_16x16x32_bf16", 2330)

  case object VSmfmacF3216x16x32F16 extends Opcode("v_smfmac_f32_16x16x32_f16", 2331)

  case object VSmfmacF3216x16x64Bf16 extends Opcode("v_smfmac_f32_16x16x64_bf16", 2332)

  case object VSmfmacF3216x16x64Bf8Bf8 extends Opcode("v_smfmac_f32_16x16x64_bf8_bf8", 2333)

  case object VSmfmacF3216x16x64Bf8Fp8 extends Opcode("v_smfmac_f32_16x16x64_bf8_fp8", 2334)

  case object VSmfmacF3216x16x64F16 extends Opcode("v_smfmac_f32_16x16x64_f16", 2335)

  case object VSmfmacF3216x16x64Fp8Bf8 extends Opcode("v_smfmac_f32_16x16x64_fp8_bf8", 2336)

  case object VSmfmacF3216x16x64Fp8Fp8 extends Opcode("v_smfmac_f32_16x16x64_fp8_fp8", 2337)

  case object VSmfmacF3232x32x16Bf16 extends Opcode("v_smfmac_f32_32x32x16_bf16", 2338)

  case object VSmfmacF3232x32x16F16 extends Opcode("v_smfmac_f32_32x32x16_f16", 2339)

  case object VSmfmacF3232x32x32Bf16 extends Opcode("v_smfmac_f32_32x32x32_bf16", 2340)

  case object VSmfmacF3232x32x32Bf8Bf8 extends Opcode("v_smfmac_f32_32x32x32_bf8_bf8", 2341)

  case object VSmfmacF3232x32x32Bf8Fp8 extends Opcode("v_smfmac_f32_32x32x32_bf8_fp8", 2342)

  case object VSmfmacF3232x32x32F16 extends Opcode("v_smfmac_f32_32x32x32_f16", 2343)

  case object VSmfmacF3232x32x32Fp8Bf8 extends Opcode("v_smfmac_f32_32x32x32_fp8_bf8", 2344)

  case object VSmfmacF3232x32x32Fp8Fp8 extends Opcode("v_smfmac_f32_32x32x32_fp8_fp8", 2345)

  case object VSmfmacF3232x32x64Bf8Bf8 extends Opcode("v_smfmac_f32_32x32x64_bf8_bf8", 2346)

  case object VSmfmacF3232x32x64Bf8Fp8 extends Opcode("v_smfmac_f32_32x32x64_bf8_fp8", 2347)

  case object VSmfmacF3232x32x64Fp8Bf8 extends Opcode("v_smfmac_f32_32x32x64_fp8_bf8", 2348)

  case object VSmfmacF3232x32x64Fp8Fp8 extends Opcode("v_smfmac_f32_32x32x64_fp8_fp8", 2349)

  case object VSmfmacI3216x16x128I8 extends Opcode("v_smfmac_i32_16x16x128_i8", 2350)

  case object VSmfmacI3216x16x64I8 extends Opcode("v_smfmac_i32_16x16x64_i8", 2351)

  case object VSmfmacI3232x32x32I8 extends Opcode("v_smfmac_i32_32x32x32_i8", 2352)

  case object VSmfmacI3232x32x64I8 extends Opcode("v_smfmac_i32_32x32x64_i8", 2353)

  case object VSwmmacBf1616x16x32Bf16 extends Opcode("v_swmmac_bf16_16x16x32_bf16", 2386)

  case object VSwmmacBf1616x16x64Bf16 extends Opcode("v_swmmac_bf16_16x16x64_bf16", 2387)

  case object VSwmmacBf16f3216x16x64Bf16 extends Opcode("v_swmmac_bf16f32_16x16x64_bf16", 2388)

  case object VSwmmacF1616x16x128Bf8Bf8 extends Opcode("v_swmmac_f16_16x16x128_bf8_bf8", 2389)

  case object VSwmmacF1616x16x128Bf8Fp8 extends Opcode("v_swmmac_f16_16x16x128_bf8_fp8", 2390)

  case object VSwmmacF1616x16x128Fp8Bf8 extends Opcode("v_swmmac_f16_16x16x128_fp8_bf8", 2391)

  case object VSwmmacF1616x16x128Fp8Fp8 extends Opcode("v_swmmac_f16_16x16x128_fp8_fp8", 2392)

  case object VSwmmacF1616x16x32F16 extends Opcode("v_swmmac_f16_16x16x32_f16", 2393)

  case object VSwmmacF1616x16x64F16 extends Opcode("v_swmmac_f16_16x16x64_f16", 2394)

  case object VSwmmacF3216x16x128Bf8Bf8 extends Opcode("v_swmmac_f32_16x16x128_bf8_bf8", 2395)

  case object VSwmmacF3216x16x128Bf8Fp8 extends Opcode("v_swmmac_f32_16x16x128_bf8_fp8", 2396)

  case object VSwmmacF3216x16x128Fp8Bf8 extends Opcode("v_swmmac_f32_16x16x128_fp8_bf8", 2397)

  case object VSwmmacF3216x16x128Fp8Fp8 extends Opcode("v_swmmac_f32_16x16x128_fp8_fp8", 2398)

  case object VSwmmacF3216x16x32Bf16 extends Opcode("v_swmmac_f32_16x16x32_bf16", 2399)

  case object VSwmmacF3216x16x32Bf8Bf8 extends Opcode("v_swmmac_f32_16x16x32_bf8_bf8", 2400)

  case object VSwmmacF3216x16x32Bf8Fp8 extends Opcode("v_swmmac_f32_16x16x32_bf8_fp8", 2401)

  case object VSwmmacF3216x16x32F16 extends Opcode("v_swmmac_f32_16x16x32_f16", 2402)

  case object VSwmmacF3216x16x32Fp8Bf8 extends Opcode("v_swmmac_f32_16x16x32_fp8_bf8", 2403)

  case object VSwmmacF3216x16x32Fp8Fp8 extends Opcode("v_swmmac_f32_16x16x32_fp8_fp8", 2404)

  case object VSwmmacF3216x16x64Bf16 extends Opcode("v_swmmac_f32_16x16x64_bf16", 2405)

  case object VSwmmacF3216x16x64F16 extends Opcode("v_swmmac_f32_16x16x64_f16", 2406)

  case object VSwmmacI3216x16x128Iu8 extends Opcode("v_swmmac_i32_16x16x128_iu8", 2407)

  case object VSwmmacI3216x16x32Iu4 extends Opcode("v_swmmac_i32_16x16x32_iu4", 2408)

  case object VSwmmacI3216x16x32Iu8 extends Opcode("v_swmmac_i32_16x16x32_iu8", 2409)

  case object VSwmmacI3216x16x64Iu4 extends Opcode("v_swmmac_i32_16x16x64_iu4", 2410)

  case object VWmmaBf1616x16x16Bf16 extends Opcode("v_wmma_bf16_16x16x16_bf16", 2418)

  case object VWmmaBf1616x16x32Bf16 extends Opcode("v_wmma_bf16_16x16x32_bf16", 2419)

  case object VWmmaBf16f3216x16x32Bf16 extends Opcode("v_wmma_bf16f32_16x16x32_bf16", 2420)

  case object VWmmaF1616x16x128Bf8Bf8 extends Opcode("v_wmma_f16_16x16x128_bf8_bf8", 2421)

  case object VWmmaF1616x16x128Bf8Fp8 extends Opcode("v_wmma_f16_16x16x128_bf8_fp8", 2422)

  case object VWmmaF1616x16x128Fp8Bf8 extends Opcode("v_wmma_f16_16x16x128_fp8_bf8", 2423)

  case object VWmmaF1616x16x128Fp8Fp8 extends Opcode("v_wmma_f16_16x16x128_fp8_fp8", 2424)

  case object VWmmaF1616x16x16F16 extends Opcode("v_wmma_f16_16x16x16_f16", 2425)

  case object VWmmaF1616x16x32F16 extends Opcode("v_wmma_f16_16x16x32_f16", 2426)

  case object VWmmaF1616x16x64Bf8Bf8 extends Opcode("v_wmma_f16_16x16x64_bf8_bf8", 2427)

  case object VWmmaF1616x16x64Bf8Fp8 extends Opcode("v_wmma_f16_16x16x64_bf8_fp8", 2428)

  case object VWmmaF1616x16x64Fp8Bf8 extends Opcode("v_wmma_f16_16x16x64_fp8_bf8", 2429)

  case object VWmmaF1616x16x64Fp8Fp8 extends Opcode("v_wmma_f16_16x16x64_fp8_fp8", 2430)

  case object VWmmaF3216x16x128Bf8Bf8 extends Opcode("v_wmma_f32_16x16x128_bf8_bf8", 2431)

  case object VWmmaF3216x16x128Bf8Fp8 extends Opcode("v_wmma_f32_16x16x128_bf8_fp8", 2432)

  case object VWmmaF3216x16x128F8f6f4 extends Opcode("v_wmma_f32_16x16x128_f8f6f4", 2433)

  case object VWmmaF3216x16x128Fp8Bf8 extends Opcode("v_wmma_f32_16x16x128_fp8_bf8", 2434)

  case object VWmmaF3216x16x128Fp8Fp8 extends Opcode("v_wmma_f32_16x16x128_fp8_fp8", 2435)

  case object VWmmaF3216x16x16Bf16 extends Opcode("v_wmma_f32_16x16x16_bf16", 2436)

  case object VWmmaF3216x16x16Bf8Bf8 extends Opcode("v_wmma_f32_16x16x16_bf8_bf8", 2437)

  case object VWmmaF3216x16x16Bf8Fp8 extends Opcode("v_wmma_f32_16x16x16_bf8_fp8", 2438)

  case object VWmmaF3216x16x16F16 extends Opcode("v_wmma_f32_16x16x16_f16", 2439)

  case object VWmmaF3216x16x16Fp8Bf8 extends Opcode("v_wmma_f32_16x16x16_fp8_bf8", 2440)

  case object VWmmaF3216x16x16Fp8Fp8 extends Opcode("v_wmma_f32_16x16x16_fp8_fp8", 2441)

  case object VWmmaF3216x16x32Bf16 extends Opcode("v_wmma_f32_16x16x32_bf16", 2442)

  case object VWmmaF3216x16x32F16 extends Opcode("v_wmma_f32_16x16x32_f16", 2443)

  case object VWmmaF3216x16x4F32 extends Opcode("v_wmma_f32_16x16x4_f32", 2444)

  case object VWmmaF3216x16x64Bf8Bf8 extends Opcode("v_wmma_f32_16x16x64_bf8_bf8", 2445)

  case object VWmmaF3216x16x64Bf8Fp8 extends Opcode("v_wmma_f32_16x16x64_bf8_fp8", 2446)

  case object VWmmaF3216x16x64Fp8Bf8 extends Opcode("v_wmma_f32_16x16x64_fp8_bf8", 2447)

  case object VWmmaF3216x16x64Fp8Fp8 extends Opcode("v_wmma_f32_16x16x64_fp8_fp8", 2448)

  case object VWmmaF3232x16x128F4 extends Opcode("v_wmma_f32_32x16x128_f4", 2449)

  case object VWmmaF6416x16x4F64 extends Opcode("v_wmma_f64_16x16x4_f64", 2450)

  case object VWmmaI3216x16x16Iu4 extends Opcode("v_wmma_i32_16x16x16_iu4", 2451)

  case object VWmmaI3216x16x16Iu8 extends Opcode("v_wmma_i32_16x16x16_iu8", 2452)

  case object VWmmaI3216x16x32Iu4 extends Opcode("v_wmma_i32_16x16x32_iu4", 2453)

  case object VWmmaI3216x16x64Iu8 extends Opcode("v_wmma_i32_16x16x64_iu8", 2454)

  case object VWmmaLdScale16PairedB64 extends Opcode("v_wmma_ld_scale16_paired_b64", 2455)

  case object VWmmaLdScalePairedB32 extends Opcode("v_wmma_ld_scale_paired_b32", 2456)

  case object VWmmaScale16F3216x16x128F8f6f4 extends Opcode("v_wmma_scale16_f32_16x16x128_f8f6f4", 2457)

  case object VWmmaScale16F3232x16x128F4 extends Opcode("v_wmma_scale16_f32_32x16x128_f4", 2458)

  case object VWmmaScaleF3216x16x128F8f6f4 extends Opcode("v_wmma_scale_f32_16x16x128_f8f6f4", 2459)

  case object VWmmaScaleF3232x16x128F4 extends Opcode("v_wmma_scale_f32_32x16x128_f4", 2460)

  val definitions: Vec[Opcode] = Vec(
    SWaitTensorcnt,
    TensorLoadToLds,
    TensorSave,
    TensorStop,
    TensorStoreFromLds,
    VMfmaF3216x16x128F8f6f4,
    VMfmaF3216x16x16Bf16,
    VMfmaF3216x16x16F16_1,
    VMfmaF3216x16x16bf161k,
    VMfmaF3216x16x16f16_2,
    VMfmaF3216x16x14bF32,
    VMfmaF3216x16x1f32,
    VMfmaF3216x16x2bf16,
    VMfmaF3216x16x32Bf16,
    VMfmaF3216x16x32Bf8Bf8,
    VMfmaF3216x16x32Bf8Fp8,
    VMfmaF3216x16x32F16,
    VMfmaF3216x16x32Fp8Bf8,
    VMfmaF3216x16x32Fp8Fp8,
    VMfmaF3216x16x44bBf16,
    VMfmaF3216x16x44bF16,
    VMfmaF3216x16x4F32_1,
    VMfmaF3216x16x4bf161k,
    VMfmaF3216x16x4f16,
    VMfmaF3216x16x4f32_2,
    VMfmaF3216x16x8Xf32,
    VMfmaF3216x16x8bf16,
    VMfmaF3232x32x16Bf16,
    VMfmaF3232x32x16Bf8Bf8,
    VMfmaF3232x32x16Bf8Fp8,
    VMfmaF3232x32x16F16,
    VMfmaF3232x32x16Fp8Bf8,
    VMfmaF3232x32x16Fp8Fp8,
    VMfmaF3232x32x12bF32,
    VMfmaF3232x32x1f32,
    VMfmaF3232x32x2F32_1,
    VMfmaF3232x32x2bf16,
    VMfmaF3232x32x2f32_2,
    VMfmaF3232x32x42bBf16,
    VMfmaF3232x32x42bF16,
    VMfmaF3232x32x4Xf32,
    VMfmaF3232x32x4bf16,
    VMfmaF3232x32x4bf161k,
    VMfmaF3232x32x4f16,
    VMfmaF3232x32x64F8f6f4,
    VMfmaF3232x32x8Bf16,
    VMfmaF3232x32x8F16_1,
    VMfmaF3232x32x8bf161k,
    VMfmaF3232x32x8f16_2,
    VMfmaF324x4x116bF32,
    VMfmaF324x4x1f32,
    VMfmaF324x4x2bf16,
    VMfmaF324x4x416bBf16,
    VMfmaF324x4x416bF16,
    VMfmaF324x4x4bf161k,
    VMfmaF324x4x4f16,
    VMfmaF6416x16x4F64_1,
    VMfmaF6416x16x4f64_2,
    VMfmaF644x4x44bF64,
    VMfmaF644x4x4f64,
    VMfmaI3216x16x16i8,
    VMfmaI3216x16x32I8,
    VMfmaI3216x16x44bI8,
    VMfmaI3216x16x4i8,
    VMfmaI3216x16x64I8,
    VMfmaI3232x32x16I8,
    VMfmaI3232x32x32I8,
    VMfmaI3232x32x42bI8,
    VMfmaI3232x32x4i8,
    VMfmaI3232x32x8i8,
    VMfmaI324x4x416bI8,
    VMfmaI324x4x4i8,
    VMfmaLdScaleB32,
    VMfmaScaleF3216x16x128F8f6f4,
    VMfmaScaleF3232x32x64F8f6f4,
    VSmfmacF3216x16x128Bf8Bf8,
    VSmfmacF3216x16x128Bf8Fp8,
    VSmfmacF3216x16x128Fp8Bf8,
    VSmfmacF3216x16x128Fp8Fp8,
    VSmfmacF3216x16x32Bf16,
    VSmfmacF3216x16x32F16,
    VSmfmacF3216x16x64Bf16,
    VSmfmacF3216x16x64Bf8Bf8,
    VSmfmacF3216x16x64Bf8Fp8,
    VSmfmacF3216x16x64F16,
    VSmfmacF3216x16x64Fp8Bf8,
    VSmfmacF3216x16x64Fp8Fp8,
    VSmfmacF3232x32x16Bf16,
    VSmfmacF3232x32x16F16,
    VSmfmacF3232x32x32Bf16,
    VSmfmacF3232x32x32Bf8Bf8,
    VSmfmacF3232x32x32Bf8Fp8,
    VSmfmacF3232x32x32F16,
    VSmfmacF3232x32x32Fp8Bf8,
    VSmfmacF3232x32x32Fp8Fp8,
    VSmfmacF3232x32x64Bf8Bf8,
    VSmfmacF3232x32x64Bf8Fp8,
    VSmfmacF3232x32x64Fp8Bf8,
    VSmfmacF3232x32x64Fp8Fp8,
    VSmfmacI3216x16x128I8,
    VSmfmacI3216x16x64I8,
    VSmfmacI3232x32x32I8,
    VSmfmacI3232x32x64I8,
    VSwmmacBf1616x16x32Bf16,
    VSwmmacBf1616x16x64Bf16,
    VSwmmacBf16f3216x16x64Bf16,
    VSwmmacF1616x16x128Bf8Bf8,
    VSwmmacF1616x16x128Bf8Fp8,
    VSwmmacF1616x16x128Fp8Bf8,
    VSwmmacF1616x16x128Fp8Fp8,
    VSwmmacF1616x16x32F16,
    VSwmmacF1616x16x64F16,
    VSwmmacF3216x16x128Bf8Bf8,
    VSwmmacF3216x16x128Bf8Fp8,
    VSwmmacF3216x16x128Fp8Bf8,
    VSwmmacF3216x16x128Fp8Fp8,
    VSwmmacF3216x16x32Bf16,
    VSwmmacF3216x16x32Bf8Bf8,
    VSwmmacF3216x16x32Bf8Fp8,
    VSwmmacF3216x16x32F16,
    VSwmmacF3216x16x32Fp8Bf8,
    VSwmmacF3216x16x32Fp8Fp8,
    VSwmmacF3216x16x64Bf16,
    VSwmmacF3216x16x64F16,
    VSwmmacI3216x16x128Iu8,
    VSwmmacI3216x16x32Iu4,
    VSwmmacI3216x16x32Iu8,
    VSwmmacI3216x16x64Iu4,
    VWmmaBf1616x16x16Bf16,
    VWmmaBf1616x16x32Bf16,
    VWmmaBf16f3216x16x32Bf16,
    VWmmaF1616x16x128Bf8Bf8,
    VWmmaF1616x16x128Bf8Fp8,
    VWmmaF1616x16x128Fp8Bf8,
    VWmmaF1616x16x128Fp8Fp8,
    VWmmaF1616x16x16F16,
    VWmmaF1616x16x32F16,
    VWmmaF1616x16x64Bf8Bf8,
    VWmmaF1616x16x64Bf8Fp8,
    VWmmaF1616x16x64Fp8Bf8,
    VWmmaF1616x16x64Fp8Fp8,
    VWmmaF3216x16x128Bf8Bf8,
    VWmmaF3216x16x128Bf8Fp8,
    VWmmaF3216x16x128F8f6f4,
    VWmmaF3216x16x128Fp8Bf8,
    VWmmaF3216x16x128Fp8Fp8,
    VWmmaF3216x16x16Bf16,
    VWmmaF3216x16x16Bf8Bf8,
    VWmmaF3216x16x16Bf8Fp8,
    VWmmaF3216x16x16F16,
    VWmmaF3216x16x16Fp8Bf8,
    VWmmaF3216x16x16Fp8Fp8,
    VWmmaF3216x16x32Bf16,
    VWmmaF3216x16x32F16,
    VWmmaF3216x16x4F32,
    VWmmaF3216x16x64Bf8Bf8,
    VWmmaF3216x16x64Bf8Fp8,
    VWmmaF3216x16x64Fp8Bf8,
    VWmmaF3216x16x64Fp8Fp8,
    VWmmaF3232x16x128F4,
    VWmmaF6416x16x4F64,
    VWmmaI3216x16x16Iu4,
    VWmmaI3216x16x16Iu8,
    VWmmaI3216x16x32Iu4,
    VWmmaI3216x16x64Iu8,
    VWmmaLdScale16PairedB64,
    VWmmaLdScalePairedB32,
    VWmmaScale16F3216x16x128F8f6f4,
    VWmmaScale16F3232x16x128F4,
    VWmmaScaleF3216x16x128F8f6f4,
    VWmmaScaleF3232x16x128F4
  )
}
