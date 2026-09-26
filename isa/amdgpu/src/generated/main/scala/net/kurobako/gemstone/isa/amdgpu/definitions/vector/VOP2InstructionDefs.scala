// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Vec
import Instruction.*
import Program.*

@javax.annotation.processing.Generated(Array("sbt codegen"))
private[amdgpu] object VOP2InstructionDefs {
  case object VAddCoCiU32 extends Definition(
        Opcode("v_add_co_ci_u32"),
        decoder(form(5)((operands, mods) =>
          Instruction.VAddCoCiU32(operands(0), operands(1), operands(2), operands(3), operands(4), mods)
        )),
        1365
      )
  case object VAddCoU32 extends Definition(
        Opcode("v_add_co_u32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VAddCoU32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1366
      )
  case object VAddF16 extends Definition(
        Opcode("v_add_f16"),
        decoder(form(3)((operands, mods) => Instruction.VAddF16(operands(0), operands(1), operands(2), mods))),
        1367
      )
  case object VAddF32 extends Definition(
        Opcode("v_add_f32"),
        decoder(form(3)((operands, mods) => Instruction.VAddF32(operands(0), operands(1), operands(2), mods))),
        1368
      )
  case object VAddF64 extends Definition(
        Opcode("v_add_f64"),
        decoder(form(3)((operands, mods) => Instruction.VAddF64(operands(0), operands(1), operands(2), mods))),
        1369
      )
  case object VAddNcU32 extends Definition(
        Opcode("v_add_nc_u32"),
        decoder(form(3)((operands, mods) => Instruction.VAddNcU32(operands(0), operands(1), operands(2), mods))),
        1381
      )
  case object VAddNcU64 extends Definition(
        Opcode("v_add_nc_u64"),
        decoder(form(3)((operands, mods) => Instruction.VAddNcU64(operands(0), operands(1), operands(2), mods))),
        1382
      )
  case object VAddU16 extends Definition(
        Opcode("v_add_u16"),
        decoder(form(3)((operands, mods) => Instruction.VAddU16(operands(0), operands(1), operands(2), mods))),
        1383
      )
  case object VAddU32 extends Definition(
        Opcode("v_add_u32"),
        decoder(
          form(3)((operands, mods) => Instruction.VAddU32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) => Instruction.VAddU32_4(operands(0), operands(1), operands(2), operands(3), mods))
        ),
        1384
      )
  case object VAddcCoU32 extends Definition(
        Opcode("v_addc_co_u32"),
        decoder(form(5)((operands, mods) =>
          Instruction.VAddcCoU32(operands(0), operands(1), operands(2), operands(3), operands(4), mods)
        )),
        1385
      )
  case object VAndB32 extends Definition(
        Opcode("v_and_b32"),
        decoder(form(3)((operands, mods) => Instruction.VAndB32(operands(0), operands(1), operands(2), mods))),
        1389
      )
  case object VAshrI32 extends Definition(
        Opcode("v_ashr_i32"),
        decoder(form(3)((operands, mods) => Instruction.VAshrI32(operands(0), operands(1), operands(2), mods))),
        1391
      )
  case object VAshrrevI16 extends Definition(
        Opcode("v_ashrrev_i16"),
        decoder(form(3)((operands, mods) => Instruction.VAshrrevI16(operands(0), operands(1), operands(2), mods))),
        1395
      )
  case object VAshrrevI32 extends Definition(
        Opcode("v_ashrrev_i32"),
        decoder(form(3)((operands, mods) => Instruction.VAshrrevI32(operands(0), operands(1), operands(2), mods))),
        1396
      )
  case object VCndmaskB32 extends Definition(
        Opcode("v_cndmask_b32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VCndmaskB32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1641
      )
  case object VCvtPkRtzF16F32_1 extends Definition(
        Opcode("v_cvt_pk_rtz_f16_f32"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtPkRtzF16F32_1(operands(0), operands(1), operands(2), mods)
        )),
        1695
      )
  case object VCvtPkrtzF16F32_2 extends Definition(
        Opcode("v_cvt_pkrtz_f16_f32"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtPkrtzF16F32_2(operands(0), operands(1), operands(2), mods)
        )),
        1704
      )
  case object VDot2accF32F16 extends Definition(
        Opcode("v_dot2acc_f32_f16"),
        decoder(form(3)((operands, mods) => Instruction.VDot2accF32F16(operands(0), operands(1), operands(2), mods))),
        1827
      )
  case object VDot2cF32Bf16 extends Definition(
        Opcode("v_dot2c_f32_bf16"),
        decoder(form(3)((operands, mods) => Instruction.VDot2cF32Bf16(operands(0), operands(1), operands(2), mods))),
        1828
      )
  case object VDot2cF32F16 extends Definition(
        Opcode("v_dot2c_f32_f16"),
        decoder(
          form(3)((operands, mods) => Instruction.VDot2cF32F16_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.VDot2cF32F16_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        1829
      )
  case object VDot2cI32I16 extends Definition(
        Opcode("v_dot2c_i32_i16"),
        decoder(form(3)((operands, mods) => Instruction.VDot2cI32I16(operands(0), operands(1), operands(2), mods))),
        1830
      )
  case object VDot4cI32I8 extends Definition(
        Opcode("v_dot4c_i32_i8"),
        decoder(
          form(3)((operands, mods) => Instruction.VDot4cI32I8_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.VDot4cI32I8_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        1838
      )
  case object VDot8cI32I4 extends Definition(
        Opcode("v_dot8c_i32_i4"),
        decoder(form(3)((operands, mods) => Instruction.VDot8cI32I4(operands(0), operands(1), operands(2), mods))),
        1842
      )
  case object VFmaakF16 extends Definition(
        Opcode("v_fmaak_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VFmaakF16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1896
      )
  case object VFmaakF32 extends Definition(
        Opcode("v_fmaak_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VFmaakF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1897
      )
  case object VFmaakF64 extends Definition(
        Opcode("v_fmaak_f64"),
        decoder(form(4)((operands, mods) =>
          Instruction.VFmaakF64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1898
      )
  case object VFmacDx9ZeroF32 extends Definition(
        Opcode("v_fmac_dx9_zero_f32"),
        decoder(form(3)((operands, mods) => Instruction.VFmacDx9ZeroF32(operands(0), operands(1), operands(2), mods))),
        1899
      )
  case object VFmacF16 extends Definition(
        Opcode("v_fmac_f16"),
        decoder(form(3)((operands, mods) => Instruction.VFmacF16(operands(0), operands(1), operands(2), mods))),
        1900
      )
  case object VFmacF32 extends Definition(
        Opcode("v_fmac_f32"),
        decoder(form(3)((operands, mods) => Instruction.VFmacF32(operands(0), operands(1), operands(2), mods))),
        1901
      )
  case object VFmacF64 extends Definition(
        Opcode("v_fmac_f64"),
        decoder(form(3)((operands, mods) => Instruction.VFmacF64(operands(0), operands(1), operands(2), mods))),
        1902
      )
  case object VFmacLegacyF32 extends Definition(
        Opcode("v_fmac_legacy_f32"),
        decoder(form(3)((operands, mods) => Instruction.VFmacLegacyF32(operands(0), operands(1), operands(2), mods))),
        1903
      )
  case object VFmamkF16 extends Definition(
        Opcode("v_fmamk_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VFmamkF16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1904
      )
  case object VFmamkF32 extends Definition(
        Opcode("v_fmamk_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VFmamkF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1905
      )
  case object VFmamkF64 extends Definition(
        Opcode("v_fmamk_f64"),
        decoder(form(4)((operands, mods) =>
          Instruction.VFmamkF64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1906
      )
  case object VLdexpF16 extends Definition(
        Opcode("v_ldexp_f16"),
        decoder(form(3)((operands, mods) => Instruction.VLdexpF16(operands(0), operands(1), operands(2), mods))),
        1929
      )
  case object VLshlB32 extends Definition(
        Opcode("v_lshl_b32"),
        decoder(form(3)((operands, mods) => Instruction.VLshlB32(operands(0), operands(1), operands(2), mods))),
        1940
      )
  case object VLshlrevB16 extends Definition(
        Opcode("v_lshlrev_b16"),
        decoder(form(3)((operands, mods) => Instruction.VLshlrevB16(operands(0), operands(1), operands(2), mods))),
        1943
      )
  case object VLshlrevB32 extends Definition(
        Opcode("v_lshlrev_b32"),
        decoder(form(3)((operands, mods) => Instruction.VLshlrevB32(operands(0), operands(1), operands(2), mods))),
        1944
      )
  case object VLshlrevB64 extends Definition(
        Opcode("v_lshlrev_b64"),
        decoder(form(3)((operands, mods) => Instruction.VLshlrevB64(operands(0), operands(1), operands(2), mods))),
        1945
      )
  case object VLshrB32 extends Definition(
        Opcode("v_lshr_b32"),
        decoder(form(3)((operands, mods) => Instruction.VLshrB32(operands(0), operands(1), operands(2), mods))),
        1947
      )
  case object VLshrrevB16 extends Definition(
        Opcode("v_lshrrev_b16"),
        decoder(form(3)((operands, mods) => Instruction.VLshrrevB16(operands(0), operands(1), operands(2), mods))),
        1949
      )
  case object VLshrrevB32 extends Definition(
        Opcode("v_lshrrev_b32"),
        decoder(form(3)((operands, mods) => Instruction.VLshrrevB32(operands(0), operands(1), operands(2), mods))),
        1950
      )
  case object VMacF16 extends Definition(
        Opcode("v_mac_f16"),
        decoder(
          form(3)((operands, mods) => Instruction.VMacF16_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) => Instruction.VMacF16_4(operands(0), operands(1), operands(2), operands(3), mods))
        ),
        1952
      )
  case object VMacF32 extends Definition(
        Opcode("v_mac_f32"),
        decoder(
          form(3)((operands, mods) => Instruction.VMacF32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) => Instruction.VMacF32_4(operands(0), operands(1), operands(2), operands(3), mods))
        ),
        1953
      )
  case object VMacLegacyF32 extends Definition(
        Opcode("v_mac_legacy_f32"),
        decoder(
          form(3)((operands, mods) => Instruction.VMacLegacyF32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.VMacLegacyF32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        1954
      )
  case object VMadakF16 extends Definition(
        Opcode("v_madak_f16"),
        decoder(
          form(3)((operands, mods) => Instruction.VMadakF16_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) => Instruction.VMadakF16_4(operands(0), operands(1), operands(2), operands(3), mods))
        ),
        1977
      )
  case object VMadakF32 extends Definition(
        Opcode("v_madak_f32"),
        decoder(
          form(3)((operands, mods) => Instruction.VMadakF32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) => Instruction.VMadakF32_4(operands(0), operands(1), operands(2), operands(3), mods))
        ),
        1978
      )
  case object VMadmkF16 extends Definition(
        Opcode("v_madmk_f16"),
        decoder(
          form(3)((operands, mods) => Instruction.VMadmkF16_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) => Instruction.VMadmkF16_4(operands(0), operands(1), operands(2), operands(3), mods))
        ),
        1979
      )
  case object VMadmkF32 extends Definition(
        Opcode("v_madmk_f32"),
        decoder(
          form(3)((operands, mods) => Instruction.VMadmkF32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) => Instruction.VMadmkF32_4(operands(0), operands(1), operands(2), operands(3), mods))
        ),
        1980
      )
  case object VMaxF16 extends Definition(
        Opcode("v_max_f16"),
        decoder(form(3)((operands, mods) => Instruction.VMaxF16(operands(0), operands(1), operands(2), mods))),
        1989
      )
  case object VMaxF32 extends Definition(
        Opcode("v_max_f32"),
        decoder(form(3)((operands, mods) => Instruction.VMaxF32(operands(0), operands(1), operands(2), mods))),
        1990
      )
  case object VMaxI16 extends Definition(
        Opcode("v_max_i16"),
        decoder(form(3)((operands, mods) => Instruction.VMaxI16(operands(0), operands(1), operands(2), mods))),
        1993
      )
  case object VMaxI32 extends Definition(
        Opcode("v_max_i32"),
        decoder(form(3)((operands, mods) => Instruction.VMaxI32(operands(0), operands(1), operands(2), mods))),
        1994
      )
  case object VMaxLegacyF32 extends Definition(
        Opcode("v_max_legacy_f32"),
        decoder(form(3)((operands, mods) => Instruction.VMaxLegacyF32(operands(0), operands(1), operands(2), mods))),
        1996
      )
  case object VMaxNumF16 extends Definition(
        Opcode("v_max_num_f16"),
        decoder(form(3)((operands, mods) => Instruction.VMaxNumF16(operands(0), operands(1), operands(2), mods))),
        1997
      )
  case object VMaxNumF32 extends Definition(
        Opcode("v_max_num_f32"),
        decoder(form(3)((operands, mods) => Instruction.VMaxNumF32(operands(0), operands(1), operands(2), mods))),
        1998
      )
  case object VMaxNumF64 extends Definition(
        Opcode("v_max_num_f64"),
        decoder(form(3)((operands, mods) => Instruction.VMaxNumF64(operands(0), operands(1), operands(2), mods))),
        1999
      )
  case object VMaxU16 extends Definition(
        Opcode("v_max_u16"),
        decoder(form(3)((operands, mods) => Instruction.VMaxU16(operands(0), operands(1), operands(2), mods))),
        2000
      )
  case object VMaxU32 extends Definition(
        Opcode("v_max_u32"),
        decoder(form(3)((operands, mods) => Instruction.VMaxU32(operands(0), operands(1), operands(2), mods))),
        2001
      )
  case object VMbcntHiU32B32 extends Definition(
        Opcode("v_mbcnt_hi_u32_b32"),
        decoder(form(3)((operands, mods) => Instruction.VMbcntHiU32B32(operands(0), operands(1), operands(2), mods))),
        2016
      )
  case object VMinF16 extends Definition(
        Opcode("v_min_f16"),
        decoder(form(3)((operands, mods) => Instruction.VMinF16(operands(0), operands(1), operands(2), mods))),
        2104
      )
  case object VMinF32 extends Definition(
        Opcode("v_min_f32"),
        decoder(form(3)((operands, mods) => Instruction.VMinF32(operands(0), operands(1), operands(2), mods))),
        2105
      )
  case object VMinI16 extends Definition(
        Opcode("v_min_i16"),
        decoder(form(3)((operands, mods) => Instruction.VMinI16(operands(0), operands(1), operands(2), mods))),
        2108
      )
  case object VMinI32 extends Definition(
        Opcode("v_min_i32"),
        decoder(form(3)((operands, mods) => Instruction.VMinI32(operands(0), operands(1), operands(2), mods))),
        2109
      )
  case object VMinLegacyF32 extends Definition(
        Opcode("v_min_legacy_f32"),
        decoder(form(3)((operands, mods) => Instruction.VMinLegacyF32(operands(0), operands(1), operands(2), mods))),
        2111
      )
  case object VMinNumF16 extends Definition(
        Opcode("v_min_num_f16"),
        decoder(form(3)((operands, mods) => Instruction.VMinNumF16(operands(0), operands(1), operands(2), mods))),
        2112
      )
  case object VMinNumF32 extends Definition(
        Opcode("v_min_num_f32"),
        decoder(form(3)((operands, mods) => Instruction.VMinNumF32(operands(0), operands(1), operands(2), mods))),
        2113
      )
  case object VMinNumF64 extends Definition(
        Opcode("v_min_num_f64"),
        decoder(form(3)((operands, mods) => Instruction.VMinNumF64(operands(0), operands(1), operands(2), mods))),
        2114
      )
  case object VMinU16 extends Definition(
        Opcode("v_min_u16"),
        decoder(form(3)((operands, mods) => Instruction.VMinU16(operands(0), operands(1), operands(2), mods))),
        2115
      )
  case object VMinU32 extends Definition(
        Opcode("v_min_u32"),
        decoder(form(3)((operands, mods) => Instruction.VMinU32(operands(0), operands(1), operands(2), mods))),
        2116
      )
  case object VMulDx9ZeroF32 extends Definition(
        Opcode("v_mul_dx9_zero_f32"),
        decoder(form(3)((operands, mods) => Instruction.VMulDx9ZeroF32(operands(0), operands(1), operands(2), mods))),
        2141
      )
  case object VMulF16 extends Definition(
        Opcode("v_mul_f16"),
        decoder(form(3)((operands, mods) => Instruction.VMulF16(operands(0), operands(1), operands(2), mods))),
        2142
      )
  case object VMulF32 extends Definition(
        Opcode("v_mul_f32"),
        decoder(form(3)((operands, mods) => Instruction.VMulF32(operands(0), operands(1), operands(2), mods))),
        2143
      )
  case object VMulF64 extends Definition(
        Opcode("v_mul_f64"),
        decoder(form(3)((operands, mods) => Instruction.VMulF64(operands(0), operands(1), operands(2), mods))),
        2144
      )
  case object VMulHiI32I24 extends Definition(
        Opcode("v_mul_hi_i32_i24"),
        decoder(form(3)((operands, mods) => Instruction.VMulHiI32I24(operands(0), operands(1), operands(2), mods))),
        2147
      )
  case object VMulHiU32U24 extends Definition(
        Opcode("v_mul_hi_u32_u24"),
        decoder(form(3)((operands, mods) => Instruction.VMulHiU32U24(operands(0), operands(1), operands(2), mods))),
        2149
      )
  case object VMulI32I24 extends Definition(
        Opcode("v_mul_i32_i24"),
        decoder(form(3)((operands, mods) => Instruction.VMulI32I24(operands(0), operands(1), operands(2), mods))),
        2150
      )
  case object VMulLegacyF32 extends Definition(
        Opcode("v_mul_legacy_f32"),
        decoder(form(3)((operands, mods) => Instruction.VMulLegacyF32(operands(0), operands(1), operands(2), mods))),
        2151
      )
  case object VMulLoU16 extends Definition(
        Opcode("v_mul_lo_u16"),
        decoder(form(3)((operands, mods) => Instruction.VMulLoU16(operands(0), operands(1), operands(2), mods))),
        2153
      )
  case object VMulU32U24 extends Definition(
        Opcode("v_mul_u32_u24"),
        decoder(form(3)((operands, mods) => Instruction.VMulU32U24(operands(0), operands(1), operands(2), mods))),
        2155
      )
  case object VMulU64 extends Definition(
        Opcode("v_mul_u64"),
        decoder(form(3)((operands, mods) => Instruction.VMulU64(operands(0), operands(1), operands(2), mods))),
        2156
      )
  case object VOrB32 extends Definition(
        Opcode("v_or_b32"),
        decoder(form(3)((operands, mods) => Instruction.VOrB32(operands(0), operands(1), operands(2), mods))),
        2163
      )
  case object VPkFmacF16 extends Definition(
        Opcode("v_pk_fmac_f16"),
        decoder(form(3)((operands, mods) => Instruction.VPkFmacF16(operands(0), operands(1), operands(2), mods))),
        2195
      )
  case object VReadlaneB32 extends Definition(
        Opcode("v_readlane_b32"),
        decoder(form(3)((operands, mods) => Instruction.VReadlaneB32(operands(0), operands(1), operands(2), mods))),
        2238
      )
  case object VSubCoCiU32 extends Definition(
        Opcode("v_sub_co_ci_u32"),
        decoder(form(5)((operands, mods) =>
          Instruction.VSubCoCiU32(operands(0), operands(1), operands(2), operands(3), operands(4), mods)
        )),
        2303
      )
  case object VSubCoU32 extends Definition(
        Opcode("v_sub_co_u32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VSubCoU32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2304
      )
  case object VSubF16 extends Definition(
        Opcode("v_sub_f16"),
        decoder(form(3)((operands, mods) => Instruction.VSubF16(operands(0), operands(1), operands(2), mods))),
        2305
      )
  case object VSubF32 extends Definition(
        Opcode("v_sub_f32"),
        decoder(form(3)((operands, mods) => Instruction.VSubF32(operands(0), operands(1), operands(2), mods))),
        2306
      )
  case object VSubNcU32 extends Definition(
        Opcode("v_sub_nc_u32"),
        decoder(form(3)((operands, mods) => Instruction.VSubNcU32(operands(0), operands(1), operands(2), mods))),
        2312
      )
  case object VSubNcU64 extends Definition(
        Opcode("v_sub_nc_u64"),
        decoder(form(3)((operands, mods) => Instruction.VSubNcU64(operands(0), operands(1), operands(2), mods))),
        2313
      )
  case object VSubU16 extends Definition(
        Opcode("v_sub_u16"),
        decoder(form(3)((operands, mods) => Instruction.VSubU16(operands(0), operands(1), operands(2), mods))),
        2314
      )
  case object VSubU32 extends Definition(
        Opcode("v_sub_u32"),
        decoder(
          form(3)((operands, mods) => Instruction.VSubU32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) => Instruction.VSubU32_4(operands(0), operands(1), operands(2), operands(3), mods))
        ),
        2315
      )
  case object VSubbCoU32 extends Definition(
        Opcode("v_subb_co_u32"),
        decoder(form(5)((operands, mods) =>
          Instruction.VSubbCoU32(operands(0), operands(1), operands(2), operands(3), operands(4), mods)
        )),
        2316
      )
  case object VSubbrevCoU32 extends Definition(
        Opcode("v_subbrev_co_u32"),
        decoder(form(5)((operands, mods) =>
          Instruction.VSubbrevCoU32(operands(0), operands(1), operands(2), operands(3), operands(4), mods)
        )),
        2317
      )
  case object VSubrevCoCiU32 extends Definition(
        Opcode("v_subrev_co_ci_u32"),
        decoder(form(5)((operands, mods) =>
          Instruction.VSubrevCoCiU32(operands(0), operands(1), operands(2), operands(3), operands(4), mods)
        )),
        2318
      )
  case object VSubrevCoU32 extends Definition(
        Opcode("v_subrev_co_u32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VSubrevCoU32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2319
      )
  case object VSubrevF16 extends Definition(
        Opcode("v_subrev_f16"),
        decoder(form(3)((operands, mods) => Instruction.VSubrevF16(operands(0), operands(1), operands(2), mods))),
        2320
      )
  case object VSubrevF32 extends Definition(
        Opcode("v_subrev_f32"),
        decoder(form(3)((operands, mods) => Instruction.VSubrevF32(operands(0), operands(1), operands(2), mods))),
        2321
      )
  case object VSubrevNcU32 extends Definition(
        Opcode("v_subrev_nc_u32"),
        decoder(form(3)((operands, mods) => Instruction.VSubrevNcU32(operands(0), operands(1), operands(2), mods))),
        2322
      )
  case object VSubrevU16 extends Definition(
        Opcode("v_subrev_u16"),
        decoder(form(3)((operands, mods) => Instruction.VSubrevU16(operands(0), operands(1), operands(2), mods))),
        2323
      )
  case object VSubrevU32 extends Definition(
        Opcode("v_subrev_u32"),
        decoder(
          form(3)((operands, mods) => Instruction.VSubrevU32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.VSubrevU32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        2324
      )
  case object VWritelaneB32 extends Definition(
        Opcode("v_writelane_b32"),
        decoder(
          form(3)((operands, mods) => Instruction.VWritelaneB32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.VWritelaneB32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        2400
      )
  case object VXnorB32 extends Definition(
        Opcode("v_xnor_b32"),
        decoder(form(3)((operands, mods) => Instruction.VXnorB32(operands(0), operands(1), operands(2), mods))),
        2403
      )
  case object VXorB32 extends Definition(
        Opcode("v_xor_b32"),
        decoder(form(3)((operands, mods) => Instruction.VXorB32(operands(0), operands(1), operands(2), mods))),
        2406
      )

  val definitions: Vec[Definition] = Vec(
    VAddCoCiU32,
    VAddCoU32,
    VAddF16,
    VAddF32,
    VAddF64,
    VAddNcU32,
    VAddNcU64,
    VAddU16,
    VAddU32,
    VAddcCoU32,
    VAndB32,
    VAshrI32,
    VAshrrevI16,
    VAshrrevI32,
    VCndmaskB32,
    VCvtPkRtzF16F32_1,
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
    VSubNcU32,
    VSubNcU64,
    VSubU16,
    VSubU32,
    VSubbCoU32,
    VSubbrevCoU32,
    VSubrevCoCiU32,
    VSubrevCoU32,
    VSubrevF16,
    VSubrevF32,
    VSubrevNcU32,
    VSubrevU16,
    VSubrevU32,
    VWritelaneB32,
    VXnorB32,
    VXorB32
  )
}
