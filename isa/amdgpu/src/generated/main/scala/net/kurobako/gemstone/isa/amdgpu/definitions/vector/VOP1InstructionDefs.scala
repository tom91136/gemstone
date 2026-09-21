// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Regenerate with: sbt codegen
// scalafix:off GemstoneNoAssume; values were validated by the generator
package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Vec
import Instruction.*
import Program.*

@javax.annotation.processing.Generated(Array("sbt codegen"))
private[amdgpu] object VOP1InstructionDefs {
  case object VAccvgprMovB32 extends Definition(
        Opcode.assume("v_accvgpr_mov_b32"),
        decoder(form(2)((operands, mods) => Instruction.VAccvgprMovB32(operands(0), operands(1), mods))),
        1361
      )
  case object VBfrevB32 extends Definition(
        Opcode.assume("v_bfrev_b32"),
        decoder(form(2)((operands, mods) => Instruction.VBfrevB32(operands(0), operands(1), mods))),
        1403
      )
  case object VCeilF16 extends Definition(
        Opcode.assume("v_ceil_f16"),
        decoder(form(2)((operands, mods) => Instruction.VCeilF16(operands(0), operands(1), mods))),
        1406
      )
  case object VCeilF32 extends Definition(
        Opcode.assume("v_ceil_f32"),
        decoder(form(2)((operands, mods) => Instruction.VCeilF32(operands(0), operands(1), mods))),
        1407
      )
  case object VCeilF64 extends Definition(
        Opcode.assume("v_ceil_f64"),
        decoder(form(2)((operands, mods) => Instruction.VCeilF64(operands(0), operands(1), mods))),
        1408
      )
  case object VClrexcp extends Definition(
        Opcode.assume("v_clrexcp"),
        decoder(form(0)((_, mods) => Instruction.VClrexcp(mods))),
        1409
      )
  case object VClsI32 extends Definition(
        Opcode.assume("v_cls_i32"),
        decoder(form(2)((operands, mods) => Instruction.VClsI32(operands(0), operands(1), mods))),
        1410
      )
  case object VClzI32U32 extends Definition(
        Opcode.assume("v_clz_i32_u32"),
        decoder(form(2)((operands, mods) => Instruction.VClzI32U32(operands(0), operands(1), mods))),
        1411
      )
  case object VCosBf16 extends Definition(
        Opcode.assume("v_cos_bf16"),
        decoder(form(2)((operands, mods) => Instruction.VCosBf16(operands(0), operands(1), mods))),
        1642
      )
  case object VCosF16 extends Definition(
        Opcode.assume("v_cos_f16"),
        decoder(form(2)((operands, mods) => Instruction.VCosF16(operands(0), operands(1), mods))),
        1643
      )
  case object VCosF32 extends Definition(
        Opcode.assume("v_cos_f32"),
        decoder(form(2)((operands, mods) => Instruction.VCosF32(operands(0), operands(1), mods))),
        1644
      )
  case object VCtzI32B32 extends Definition(
        Opcode.assume("v_ctz_i32_b32"),
        decoder(form(2)((operands, mods) => Instruction.VCtzI32B32(operands(0), operands(1), mods))),
        1645
      )
  case object VCvtF16Bf8 extends Definition(
        Opcode.assume("v_cvt_f16_bf8"),
        decoder(form(2)((operands, mods) => Instruction.VCvtF16Bf8(operands(0), operands(1), mods))),
        1650
      )
  case object VCvtF16F32 extends Definition(
        Opcode.assume("v_cvt_f16_f32"),
        decoder(form(2)((operands, mods) => Instruction.VCvtF16F32(operands(0), operands(1), mods))),
        1651
      )
  case object VCvtF16Fp8 extends Definition(
        Opcode.assume("v_cvt_f16_fp8"),
        decoder(form(2)((operands, mods) => Instruction.VCvtF16Fp8(operands(0), operands(1), mods))),
        1652
      )
  case object VCvtF16I16 extends Definition(
        Opcode.assume("v_cvt_f16_i16"),
        decoder(form(2)((operands, mods) => Instruction.VCvtF16I16(operands(0), operands(1), mods))),
        1653
      )
  case object VCvtF16U16 extends Definition(
        Opcode.assume("v_cvt_f16_u16"),
        decoder(form(2)((operands, mods) => Instruction.VCvtF16U16(operands(0), operands(1), mods))),
        1654
      )
  case object VCvtF32Bf16 extends Definition(
        Opcode.assume("v_cvt_f32_bf16"),
        decoder(form(2)((operands, mods) => Instruction.VCvtF32Bf16(operands(0), operands(1), mods))),
        1655
      )
  case object VCvtF32Bf8 extends Definition(
        Opcode.assume("v_cvt_f32_bf8"),
        decoder(form(2)((operands, mods) => Instruction.VCvtF32Bf8(operands(0), operands(1), mods))),
        1656
      )
  case object VCvtF32F16 extends Definition(
        Opcode.assume("v_cvt_f32_f16"),
        decoder(form(2)((operands, mods) => Instruction.VCvtF32F16(operands(0), operands(1), mods))),
        1657
      )
  case object VCvtF32F64 extends Definition(
        Opcode.assume("v_cvt_f32_f64"),
        decoder(form(2)((operands, mods) => Instruction.VCvtF32F64(operands(0), operands(1), mods))),
        1658
      )
  case object VCvtF32Fp8 extends Definition(
        Opcode.assume("v_cvt_f32_fp8"),
        decoder(form(2)((operands, mods) => Instruction.VCvtF32Fp8(operands(0), operands(1), mods))),
        1659
      )
  case object VCvtF32I32 extends Definition(
        Opcode.assume("v_cvt_f32_i32"),
        decoder(form(2)((operands, mods) => Instruction.VCvtF32I32(operands(0), operands(1), mods))),
        1660
      )
  case object VCvtF32U32 extends Definition(
        Opcode.assume("v_cvt_f32_u32"),
        decoder(form(2)((operands, mods) => Instruction.VCvtF32U32(operands(0), operands(1), mods))),
        1661
      )
  case object VCvtF32Ubyte0 extends Definition(
        Opcode.assume("v_cvt_f32_ubyte0"),
        decoder(form(2)((operands, mods) => Instruction.VCvtF32Ubyte0(operands(0), operands(1), mods))),
        1662
      )
  case object VCvtF32Ubyte1 extends Definition(
        Opcode.assume("v_cvt_f32_ubyte1"),
        decoder(form(2)((operands, mods) => Instruction.VCvtF32Ubyte1(operands(0), operands(1), mods))),
        1663
      )
  case object VCvtF32Ubyte2 extends Definition(
        Opcode.assume("v_cvt_f32_ubyte2"),
        decoder(form(2)((operands, mods) => Instruction.VCvtF32Ubyte2(operands(0), operands(1), mods))),
        1664
      )
  case object VCvtF32Ubyte3 extends Definition(
        Opcode.assume("v_cvt_f32_ubyte3"),
        decoder(form(2)((operands, mods) => Instruction.VCvtF32Ubyte3(operands(0), operands(1), mods))),
        1665
      )
  case object VCvtF64F32 extends Definition(
        Opcode.assume("v_cvt_f64_f32"),
        decoder(form(2)((operands, mods) => Instruction.VCvtF64F32(operands(0), operands(1), mods))),
        1666
      )
  case object VCvtF64I32 extends Definition(
        Opcode.assume("v_cvt_f64_i32"),
        decoder(form(2)((operands, mods) => Instruction.VCvtF64I32(operands(0), operands(1), mods))),
        1667
      )
  case object VCvtF64U32 extends Definition(
        Opcode.assume("v_cvt_f64_u32"),
        decoder(form(2)((operands, mods) => Instruction.VCvtF64U32(operands(0), operands(1), mods))),
        1668
      )
  case object VCvtFloorI32F32 extends Definition(
        Opcode.assume("v_cvt_floor_i32_f32"),
        decoder(form(2)((operands, mods) => Instruction.VCvtFloorI32F32(operands(0), operands(1), mods))),
        1669
      )
  case object VCvtFlrI32F32 extends Definition(
        Opcode.assume("v_cvt_flr_i32_f32"),
        decoder(form(2)((operands, mods) => Instruction.VCvtFlrI32F32(operands(0), operands(1), mods))),
        1670
      )
  case object VCvtI16F16 extends Definition(
        Opcode.assume("v_cvt_i16_f16"),
        decoder(form(2)((operands, mods) => Instruction.VCvtI16F16(operands(0), operands(1), mods))),
        1671
      )
  case object VCvtI32F32 extends Definition(
        Opcode.assume("v_cvt_i32_f32"),
        decoder(form(2)((operands, mods) => Instruction.VCvtI32F32(operands(0), operands(1), mods))),
        1672
      )
  case object VCvtI32F64 extends Definition(
        Opcode.assume("v_cvt_i32_f64"),
        decoder(form(2)((operands, mods) => Instruction.VCvtI32F64(operands(0), operands(1), mods))),
        1673
      )
  case object VCvtI32I16 extends Definition(
        Opcode.assume("v_cvt_i32_i16"),
        decoder(form(2)((operands, mods) => Instruction.VCvtI32I16(operands(0), operands(1), mods))),
        1674
      )
  case object VCvtNearestI32F32 extends Definition(
        Opcode.assume("v_cvt_nearest_i32_f32"),
        decoder(form(2)((operands, mods) => Instruction.VCvtNearestI32F32(operands(0), operands(1), mods))),
        1675
      )
  case object VCvtNormI16F16 extends Definition(
        Opcode.assume("v_cvt_norm_i16_f16"),
        decoder(form(2)((operands, mods) => Instruction.VCvtNormI16F16(operands(0), operands(1), mods))),
        1676
      )
  case object VCvtNormU16F16 extends Definition(
        Opcode.assume("v_cvt_norm_u16_f16"),
        decoder(form(2)((operands, mods) => Instruction.VCvtNormU16F16(operands(0), operands(1), mods))),
        1677
      )
  case object VCvtOffF32I4 extends Definition(
        Opcode.assume("v_cvt_off_f32_i4"),
        decoder(form(2)((operands, mods) => Instruction.VCvtOffF32I4(operands(0), operands(1), mods))),
        1678
      )
  case object VCvtPkF16Bf8 extends Definition(
        Opcode.assume("v_cvt_pk_f16_bf8"),
        decoder(form(2)((operands, mods) => Instruction.VCvtPkF16Bf8(operands(0), operands(1), mods))),
        1682
      )
  case object VCvtPkF16Fp8 extends Definition(
        Opcode.assume("v_cvt_pk_f16_fp8"),
        decoder(form(2)((operands, mods) => Instruction.VCvtPkF16Fp8(operands(0), operands(1), mods))),
        1684
      )
  case object VCvtPkF32Bf8 extends Definition(
        Opcode.assume("v_cvt_pk_f32_bf8"),
        decoder(form(2)((operands, mods) => Instruction.VCvtPkF32Bf8(operands(0), operands(1), mods))),
        1685
      )
  case object VCvtPkF32Fp8 extends Definition(
        Opcode.assume("v_cvt_pk_f32_fp8"),
        decoder(form(2)((operands, mods) => Instruction.VCvtPkF32Fp8(operands(0), operands(1), mods))),
        1686
      )
  case object VCvtRpiI32F32 extends Definition(
        Opcode.assume("v_cvt_rpi_i32_f32"),
        decoder(form(2)((operands, mods) => Instruction.VCvtRpiI32F32(operands(0), operands(1), mods))),
        1706
      )
  case object VCvtU16F16 extends Definition(
        Opcode.assume("v_cvt_u16_f16"),
        decoder(form(2)((operands, mods) => Instruction.VCvtU16F16(operands(0), operands(1), mods))),
        1809
      )
  case object VCvtU32F32 extends Definition(
        Opcode.assume("v_cvt_u32_f32"),
        decoder(form(2)((operands, mods) => Instruction.VCvtU32F32(operands(0), operands(1), mods))),
        1810
      )
  case object VCvtU32F64 extends Definition(
        Opcode.assume("v_cvt_u32_f64"),
        decoder(form(2)((operands, mods) => Instruction.VCvtU32F64(operands(0), operands(1), mods))),
        1811
      )
  case object VCvtU32U16 extends Definition(
        Opcode.assume("v_cvt_u32_u16"),
        decoder(form(2)((operands, mods) => Instruction.VCvtU32U16(operands(0), operands(1), mods))),
        1812
      )
  case object VExpBf16 extends Definition(
        Opcode.assume("v_exp_bf16"),
        decoder(form(2)((operands, mods) => Instruction.VExpBf16(operands(0), operands(1), mods))),
        1874
      )
  case object VExpF16 extends Definition(
        Opcode.assume("v_exp_f16"),
        decoder(form(2)((operands, mods) => Instruction.VExpF16(operands(0), operands(1), mods))),
        1875
      )
  case object VExpF32 extends Definition(
        Opcode.assume("v_exp_f32"),
        decoder(form(2)((operands, mods) => Instruction.VExpF32(operands(0), operands(1), mods))),
        1876
      )
  case object VExpLegacyF32 extends Definition(
        Opcode.assume("v_exp_legacy_f32"),
        decoder(form(2)((operands, mods) => Instruction.VExpLegacyF32(operands(0), operands(1), mods))),
        1877
      )
  case object VFfbhI32 extends Definition(
        Opcode.assume("v_ffbh_i32"),
        decoder(form(2)((operands, mods) => Instruction.VFfbhI32(operands(0), operands(1), mods))),
        1878
      )
  case object VFfbhU32 extends Definition(
        Opcode.assume("v_ffbh_u32"),
        decoder(form(2)((operands, mods) => Instruction.VFfbhU32(operands(0), operands(1), mods))),
        1879
      )
  case object VFfblB32 extends Definition(
        Opcode.assume("v_ffbl_b32"),
        decoder(form(2)((operands, mods) => Instruction.VFfblB32(operands(0), operands(1), mods))),
        1880
      )
  case object VFloorF16 extends Definition(
        Opcode.assume("v_floor_f16"),
        decoder(form(2)((operands, mods) => Instruction.VFloorF16(operands(0), operands(1), mods))),
        1881
      )
  case object VFloorF32 extends Definition(
        Opcode.assume("v_floor_f32"),
        decoder(form(2)((operands, mods) => Instruction.VFloorF32(operands(0), operands(1), mods))),
        1882
      )
  case object VFloorF64 extends Definition(
        Opcode.assume("v_floor_f64"),
        decoder(form(2)((operands, mods) => Instruction.VFloorF64(operands(0), operands(1), mods))),
        1883
      )
  case object VFractF16 extends Definition(
        Opcode.assume("v_fract_f16"),
        decoder(form(2)((operands, mods) => Instruction.VFractF16(operands(0), operands(1), mods))),
        1907
      )
  case object VFractF32 extends Definition(
        Opcode.assume("v_fract_f32"),
        decoder(form(2)((operands, mods) => Instruction.VFractF32(operands(0), operands(1), mods))),
        1908
      )
  case object VFractF64 extends Definition(
        Opcode.assume("v_fract_f64"),
        decoder(form(2)((operands, mods) => Instruction.VFractF64(operands(0), operands(1), mods))),
        1909
      )
  case object VFrexpExpI16F16 extends Definition(
        Opcode.assume("v_frexp_exp_i16_f16"),
        decoder(form(2)((operands, mods) => Instruction.VFrexpExpI16F16(operands(0), operands(1), mods))),
        1910
      )
  case object VFrexpExpI32F32 extends Definition(
        Opcode.assume("v_frexp_exp_i32_f32"),
        decoder(form(2)((operands, mods) => Instruction.VFrexpExpI32F32(operands(0), operands(1), mods))),
        1911
      )
  case object VFrexpExpI32F64 extends Definition(
        Opcode.assume("v_frexp_exp_i32_f64"),
        decoder(form(2)((operands, mods) => Instruction.VFrexpExpI32F64(operands(0), operands(1), mods))),
        1912
      )
  case object VFrexpMantF16 extends Definition(
        Opcode.assume("v_frexp_mant_f16"),
        decoder(form(2)((operands, mods) => Instruction.VFrexpMantF16(operands(0), operands(1), mods))),
        1913
      )
  case object VFrexpMantF32 extends Definition(
        Opcode.assume("v_frexp_mant_f32"),
        decoder(form(2)((operands, mods) => Instruction.VFrexpMantF32(operands(0), operands(1), mods))),
        1914
      )
  case object VFrexpMantF64 extends Definition(
        Opcode.assume("v_frexp_mant_f64"),
        decoder(form(2)((operands, mods) => Instruction.VFrexpMantF64(operands(0), operands(1), mods))),
        1915
      )
  case object VLogBf16 extends Definition(
        Opcode.assume("v_log_bf16"),
        decoder(form(2)((operands, mods) => Instruction.VLogBf16(operands(0), operands(1), mods))),
        1933
      )
  case object VLogClampF32 extends Definition(
        Opcode.assume("v_log_clamp_f32"),
        decoder(form(2)((operands, mods) => Instruction.VLogClampF32(operands(0), operands(1), mods))),
        1934
      )
  case object VLogF16 extends Definition(
        Opcode.assume("v_log_f16"),
        decoder(form(2)((operands, mods) => Instruction.VLogF16(operands(0), operands(1), mods))),
        1935
      )
  case object VLogF32 extends Definition(
        Opcode.assume("v_log_f32"),
        decoder(form(2)((operands, mods) => Instruction.VLogF32(operands(0), operands(1), mods))),
        1936
      )
  case object VLogLegacyF32 extends Definition(
        Opcode.assume("v_log_legacy_f32"),
        decoder(form(2)((operands, mods) => Instruction.VLogLegacyF32(operands(0), operands(1), mods))),
        1937
      )
  case object VMovB16 extends Definition(
        Opcode.assume("v_mov_b16"),
        decoder(form(2)((operands, mods) => Instruction.VMovB16(operands(0), operands(1), mods))),
        2131
      )
  case object VMovB32 extends Definition(
        Opcode.assume("v_mov_b32"),
        decoder(form(2)((operands, mods) => Instruction.VMovB32(operands(0), operands(1), mods))),
        2132
      )
  case object VMovB64 extends Definition(
        Opcode.assume("v_mov_b64"),
        decoder(form(2)((operands, mods) => Instruction.VMovB64(operands(0), operands(1), mods))),
        2133
      )
  case object VMovreldB32 extends Definition(
        Opcode.assume("v_movreld_b32"),
        decoder(form(2)((operands, mods) => Instruction.VMovreldB32(operands(0), operands(1), mods))),
        2134
      )
  case object VMovrelsB32 extends Definition(
        Opcode.assume("v_movrels_b32"),
        decoder(form(2)((operands, mods) => Instruction.VMovrelsB32(operands(0), operands(1), mods))),
        2135
      )
  case object VMovrelsd2B32 extends Definition(
        Opcode.assume("v_movrelsd_2_b32"),
        decoder(form(2)((operands, mods) => Instruction.VMovrelsd2B32(operands(0), operands(1), mods))),
        2136
      )
  case object VMovrelsdB32 extends Definition(
        Opcode.assume("v_movrelsd_b32"),
        decoder(form(2)((operands, mods) => Instruction.VMovrelsdB32(operands(0), operands(1), mods))),
        2137
      )
  case object VNop extends Definition(
        Opcode.assume("v_nop"),
        decoder(form(0)((_, mods) => Instruction.VNop(mods))),
        2158
      )
  case object VNotB16 extends Definition(
        Opcode.assume("v_not_b16"),
        decoder(form(2)((operands, mods) => Instruction.VNotB16(operands(0), operands(1), mods))),
        2159
      )
  case object VNotB32 extends Definition(
        Opcode.assume("v_not_b32"),
        decoder(form(2)((operands, mods) => Instruction.VNotB32(operands(0), operands(1), mods))),
        2160
      )
  case object VPermlane16SwapB32 extends Definition(
        Opcode.assume("v_permlane16_swap_b32"),
        decoder(form(2)((operands, mods) => Instruction.VPermlane16SwapB32(operands(0), operands(1), mods))),
        2170
      )
  case object VPermlane32SwapB32 extends Definition(
        Opcode.assume("v_permlane32_swap_b32"),
        decoder(form(2)((operands, mods) => Instruction.VPermlane32SwapB32(operands(0), operands(1), mods))),
        2172
      )
  case object VPermlane64B32 extends Definition(
        Opcode.assume("v_permlane64_b32"),
        decoder(form(2)((operands, mods) => Instruction.VPermlane64B32(operands(0), operands(1), mods))),
        2173
      )
  case object VPipeflush extends Definition(
        Opcode.assume("v_pipeflush"),
        decoder(form(0)((_, mods) => Instruction.VPipeflush(mods))),
        2181
      )
  case object VPrngB32 extends Definition(
        Opcode.assume("v_prng_b32"),
        decoder(form(2)((operands, mods) => Instruction.VPrngB32(operands(0), operands(1), mods))),
        2227
      )
  case object VRcpBf16 extends Definition(
        Opcode.assume("v_rcp_bf16"),
        decoder(form(2)((operands, mods) => Instruction.VRcpBf16(operands(0), operands(1), mods))),
        2229
      )
  case object VRcpClampF32 extends Definition(
        Opcode.assume("v_rcp_clamp_f32"),
        decoder(form(2)((operands, mods) => Instruction.VRcpClampF32(operands(0), operands(1), mods))),
        2230
      )
  case object VRcpClampF64 extends Definition(
        Opcode.assume("v_rcp_clamp_f64"),
        decoder(form(2)((operands, mods) => Instruction.VRcpClampF64(operands(0), operands(1), mods))),
        2231
      )
  case object VRcpF16 extends Definition(
        Opcode.assume("v_rcp_f16"),
        decoder(form(2)((operands, mods) => Instruction.VRcpF16(operands(0), operands(1), mods))),
        2232
      )
  case object VRcpF32 extends Definition(
        Opcode.assume("v_rcp_f32"),
        decoder(form(2)((operands, mods) => Instruction.VRcpF32(operands(0), operands(1), mods))),
        2233
      )
  case object VRcpF64 extends Definition(
        Opcode.assume("v_rcp_f64"),
        decoder(form(2)((operands, mods) => Instruction.VRcpF64(operands(0), operands(1), mods))),
        2234
      )
  case object VRcpIflagF32 extends Definition(
        Opcode.assume("v_rcp_iflag_f32"),
        decoder(form(2)((operands, mods) => Instruction.VRcpIflagF32(operands(0), operands(1), mods))),
        2235
      )
  case object VRcpLegacyF32 extends Definition(
        Opcode.assume("v_rcp_legacy_f32"),
        decoder(form(2)((operands, mods) => Instruction.VRcpLegacyF32(operands(0), operands(1), mods))),
        2236
      )
  case object VReadfirstlaneB32 extends Definition(
        Opcode.assume("v_readfirstlane_b32"),
        decoder(form(2)((operands, mods) => Instruction.VReadfirstlaneB32(operands(0), operands(1), mods))),
        2237
      )
  case object VRndneF16 extends Definition(
        Opcode.assume("v_rndne_f16"),
        decoder(form(2)((operands, mods) => Instruction.VRndneF16(operands(0), operands(1), mods))),
        2240
      )
  case object VRndneF32 extends Definition(
        Opcode.assume("v_rndne_f32"),
        decoder(form(2)((operands, mods) => Instruction.VRndneF32(operands(0), operands(1), mods))),
        2241
      )
  case object VRndneF64 extends Definition(
        Opcode.assume("v_rndne_f64"),
        decoder(form(2)((operands, mods) => Instruction.VRndneF64(operands(0), operands(1), mods))),
        2242
      )
  case object VRsqBf16 extends Definition(
        Opcode.assume("v_rsq_bf16"),
        decoder(form(2)((operands, mods) => Instruction.VRsqBf16(operands(0), operands(1), mods))),
        2243
      )
  case object VRsqClampF32 extends Definition(
        Opcode.assume("v_rsq_clamp_f32"),
        decoder(form(2)((operands, mods) => Instruction.VRsqClampF32(operands(0), operands(1), mods))),
        2244
      )
  case object VRsqClampF64 extends Definition(
        Opcode.assume("v_rsq_clamp_f64"),
        decoder(form(2)((operands, mods) => Instruction.VRsqClampF64(operands(0), operands(1), mods))),
        2245
      )
  case object VRsqF16 extends Definition(
        Opcode.assume("v_rsq_f16"),
        decoder(form(2)((operands, mods) => Instruction.VRsqF16(operands(0), operands(1), mods))),
        2246
      )
  case object VRsqF32 extends Definition(
        Opcode.assume("v_rsq_f32"),
        decoder(form(2)((operands, mods) => Instruction.VRsqF32(operands(0), operands(1), mods))),
        2247
      )
  case object VRsqF64 extends Definition(
        Opcode.assume("v_rsq_f64"),
        decoder(form(2)((operands, mods) => Instruction.VRsqF64(operands(0), operands(1), mods))),
        2248
      )
  case object VRsqLegacyF32 extends Definition(
        Opcode.assume("v_rsq_legacy_f32"),
        decoder(form(2)((operands, mods) => Instruction.VRsqLegacyF32(operands(0), operands(1), mods))),
        2249
      )
  case object VSatPk4I4I8 extends Definition(
        Opcode.assume("v_sat_pk4_i4_i8"),
        decoder(form(2)((operands, mods) => Instruction.VSatPk4I4I8(operands(0), operands(1), mods))),
        2264
      )
  case object VSatPk4U4U8 extends Definition(
        Opcode.assume("v_sat_pk4_u4_u8"),
        decoder(form(2)((operands, mods) => Instruction.VSatPk4U4U8(operands(0), operands(1), mods))),
        2265
      )
  case object VSatPkU8I16 extends Definition(
        Opcode.assume("v_sat_pk_u8_i16"),
        decoder(form(2)((operands, mods) => Instruction.VSatPkU8I16(operands(0), operands(1), mods))),
        2266
      )
  case object VScreenPartition4seB32 extends Definition(
        Opcode.assume("v_screen_partition_4se_b32"),
        decoder(form(2)((operands, mods) => Instruction.VScreenPartition4seB32(operands(0), operands(1), mods))),
        2267
      )
  case object VSinBf16 extends Definition(
        Opcode.assume("v_sin_bf16"),
        decoder(form(2)((operands, mods) => Instruction.VSinBf16(operands(0), operands(1), mods))),
        2268
      )
  case object VSinF16 extends Definition(
        Opcode.assume("v_sin_f16"),
        decoder(form(2)((operands, mods) => Instruction.VSinF16(operands(0), operands(1), mods))),
        2269
      )
  case object VSinF32 extends Definition(
        Opcode.assume("v_sin_f32"),
        decoder(form(2)((operands, mods) => Instruction.VSinF32(operands(0), operands(1), mods))),
        2270
      )
  case object VSqrtBf16 extends Definition(
        Opcode.assume("v_sqrt_bf16"),
        decoder(form(2)((operands, mods) => Instruction.VSqrtBf16(operands(0), operands(1), mods))),
        2299
      )
  case object VSqrtF16 extends Definition(
        Opcode.assume("v_sqrt_f16"),
        decoder(form(2)((operands, mods) => Instruction.VSqrtF16(operands(0), operands(1), mods))),
        2300
      )
  case object VSqrtF32 extends Definition(
        Opcode.assume("v_sqrt_f32"),
        decoder(form(2)((operands, mods) => Instruction.VSqrtF32(operands(0), operands(1), mods))),
        2301
      )
  case object VSqrtF64 extends Definition(
        Opcode.assume("v_sqrt_f64"),
        decoder(form(2)((operands, mods) => Instruction.VSqrtF64(operands(0), operands(1), mods))),
        2302
      )
  case object VSwapB16 extends Definition(
        Opcode.assume("v_swap_b16"),
        decoder(form(2)((operands, mods) => Instruction.VSwapB16(operands(0), operands(1), mods))),
        2325
      )
  case object VSwapB32 extends Definition(
        Opcode.assume("v_swap_b32"),
        decoder(
          form(2)((operands, mods) => Instruction.VSwapB32_2(operands(0), operands(1), mods)),
          form(4)((operands, mods) => Instruction.VSwapB32_4(operands(0), operands(1), operands(2), operands(3), mods))
        ),
        2326
      )
  case object VSwaprelB32 extends Definition(
        Opcode.assume("v_swaprel_b32"),
        decoder(form(2)((operands, mods) => Instruction.VSwaprelB32(operands(0), operands(1), mods))),
        2327
      )
  case object VTanhBf16 extends Definition(
        Opcode.assume("v_tanh_bf16"),
        decoder(form(2)((operands, mods) => Instruction.VTanhBf16(operands(0), operands(1), mods))),
        2353
      )
  case object VTanhF16 extends Definition(
        Opcode.assume("v_tanh_f16"),
        decoder(form(2)((operands, mods) => Instruction.VTanhF16(operands(0), operands(1), mods))),
        2354
      )
  case object VTanhF32 extends Definition(
        Opcode.assume("v_tanh_f32"),
        decoder(form(2)((operands, mods) => Instruction.VTanhF32(operands(0), operands(1), mods))),
        2355
      )
  case object VTruncF16 extends Definition(
        Opcode.assume("v_trunc_f16"),
        decoder(form(2)((operands, mods) => Instruction.VTruncF16(operands(0), operands(1), mods))),
        2357
      )
  case object VTruncF32 extends Definition(
        Opcode.assume("v_trunc_f32"),
        decoder(form(2)((operands, mods) => Instruction.VTruncF32(operands(0), operands(1), mods))),
        2358
      )
  case object VTruncF64 extends Definition(
        Opcode.assume("v_trunc_f64"),
        decoder(form(2)((operands, mods) => Instruction.VTruncF64(operands(0), operands(1), mods))),
        2359
      )

  val definitions: Vec[Definition] = Vec(
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
