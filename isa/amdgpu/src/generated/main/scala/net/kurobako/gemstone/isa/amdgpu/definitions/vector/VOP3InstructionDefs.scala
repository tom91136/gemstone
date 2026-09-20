// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Do not edit by hand.
// scalafix:off GemstoneNoAssume; values were validated by the generator
package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Vec

private[amdgpu] object VOP3InstructionDefs {
  case object VAdd3U32 extends InstructionDef(
        AMDGPUOpcode.assume("v_add3_u32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VAdd3U32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1364
      )
  case object VAddF64E64 extends InstructionDef(
        AMDGPUOpcode.assume("v_add_f64_e64"),
        decoder(form(3)((operands, mods) => Instruction.VAddF64E64(operands(0), operands(1), operands(2), mods))),
        1370
      )
  case object VAddI16 extends InstructionDef(
        AMDGPUOpcode.assume("v_add_i16"),
        decoder(form(3)((operands, mods) => Instruction.VAddI16(operands(0), operands(1), operands(2), mods))),
        1371
      )
  case object VAddI32 extends InstructionDef(
        AMDGPUOpcode.assume("v_add_i32"),
        decoder(
          form(3)((operands, mods) => Instruction.VAddI32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) => Instruction.VAddI32_4(operands(0), operands(1), operands(2), operands(3), mods))
        ),
        1372
      )
  case object VAddLshlU32 extends InstructionDef(
        AMDGPUOpcode.assume("v_add_lshl_u32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VAddLshlU32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1373
      )
  case object VAddMaxI32 extends InstructionDef(
        AMDGPUOpcode.assume("v_add_max_i32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VAddMaxI32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1374
      )
  case object VAddMaxU32 extends InstructionDef(
        AMDGPUOpcode.assume("v_add_max_u32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VAddMaxU32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1375
      )
  case object VAddMinI32 extends InstructionDef(
        AMDGPUOpcode.assume("v_add_min_i32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VAddMinI32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1376
      )
  case object VAddMinU32 extends InstructionDef(
        AMDGPUOpcode.assume("v_add_min_u32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VAddMinU32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1377
      )
  case object VAddNcI16 extends InstructionDef(
        AMDGPUOpcode.assume("v_add_nc_i16"),
        decoder(form(3)((operands, mods) => Instruction.VAddNcI16(operands(0), operands(1), operands(2), mods))),
        1378
      )
  case object VAddNcI32 extends InstructionDef(
        AMDGPUOpcode.assume("v_add_nc_i32"),
        decoder(form(3)((operands, mods) => Instruction.VAddNcI32(operands(0), operands(1), operands(2), mods))),
        1379
      )
  case object VAddNcU16 extends InstructionDef(
        AMDGPUOpcode.assume("v_add_nc_u16"),
        decoder(form(3)((operands, mods) => Instruction.VAddNcU16(operands(0), operands(1), operands(2), mods))),
        1380
      )
  case object VAlignbitB32 extends InstructionDef(
        AMDGPUOpcode.assume("v_alignbit_b32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VAlignbitB32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1386
      )
  case object VAlignbyteB32 extends InstructionDef(
        AMDGPUOpcode.assume("v_alignbyte_b32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VAlignbyteB32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1387
      )
  case object VAndB16 extends InstructionDef(
        AMDGPUOpcode.assume("v_and_b16"),
        decoder(form(3)((operands, mods) => Instruction.VAndB16(operands(0), operands(1), operands(2), mods))),
        1388
      )
  case object VAndOrB32 extends InstructionDef(
        AMDGPUOpcode.assume("v_and_or_b32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VAndOrB32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1390
      )
  case object VAshrI64 extends InstructionDef(
        AMDGPUOpcode.assume("v_ashr_i64"),
        decoder(form(3)((operands, mods) => Instruction.VAshrI64(operands(0), operands(1), operands(2), mods))),
        1392
      )
  case object VAshrPkI8I32 extends InstructionDef(
        AMDGPUOpcode.assume("v_ashr_pk_i8_i32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VAshrPkI8I32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1393
      )
  case object VAshrPkU8I32 extends InstructionDef(
        AMDGPUOpcode.assume("v_ashr_pk_u8_i32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VAshrPkU8I32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1394
      )
  case object VAshrrevI64 extends InstructionDef(
        AMDGPUOpcode.assume("v_ashrrev_i64"),
        decoder(form(3)((operands, mods) => Instruction.VAshrrevI64(operands(0), operands(1), operands(2), mods))),
        1397
      )
  case object VBcntU32B32 extends InstructionDef(
        AMDGPUOpcode.assume("v_bcnt_u32_b32"),
        decoder(form(3)((operands, mods) => Instruction.VBcntU32B32(operands(0), operands(1), operands(2), mods))),
        1398
      )
  case object VBfeI32 extends InstructionDef(
        AMDGPUOpcode.assume("v_bfe_i32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VBfeI32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1399
      )
  case object VBfeU32 extends InstructionDef(
        AMDGPUOpcode.assume("v_bfe_u32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VBfeU32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1400
      )
  case object VBfiB32 extends InstructionDef(
        AMDGPUOpcode.assume("v_bfi_b32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VBfiB32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1401
      )
  case object VBfmB32 extends InstructionDef(
        AMDGPUOpcode.assume("v_bfm_b32"),
        decoder(form(3)((operands, mods) => Instruction.VBfmB32(operands(0), operands(1), operands(2), mods))),
        1402
      )
  case object VBitop3B16 extends InstructionDef(
        AMDGPUOpcode.assume("v_bitop3_b16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VBitop3B16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1404
      )
  case object VBitop3B32 extends InstructionDef(
        AMDGPUOpcode.assume("v_bitop3_b32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VBitop3B32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1405
      )
  case object VCndmaskB16 extends InstructionDef(
        AMDGPUOpcode.assume("v_cndmask_b16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VCndmaskB16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1640
      )
  case object VCubeidF32 extends InstructionDef(
        AMDGPUOpcode.assume("v_cubeid_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VCubeidF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1646
      )
  case object VCubemaF32 extends InstructionDef(
        AMDGPUOpcode.assume("v_cubema_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VCubemaF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1647
      )
  case object VCubescF32 extends InstructionDef(
        AMDGPUOpcode.assume("v_cubesc_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VCubescF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1648
      )
  case object VCubetcF32 extends InstructionDef(
        AMDGPUOpcode.assume("v_cubetc_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VCubetcF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1649
      )
  case object VCvtPkBf16F32 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_pk_bf16_f32"),
        decoder(form(3)((operands, mods) => Instruction.VCvtPkBf16F32(operands(0), operands(1), operands(2), mods))),
        1679
      )
  case object VCvtPkBf8F16 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_pk_bf8_f16"),
        decoder(form(2)((operands, mods) => Instruction.VCvtPkBf8F16(operands(0), operands(1), mods))),
        1680
      )
  case object VCvtPkBf8F32 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_pk_bf8_f32"),
        decoder(form(3)((operands, mods) => Instruction.VCvtPkBf8F32(operands(0), operands(1), operands(2), mods))),
        1681
      )
  case object VCvtPkF16F32 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_pk_f16_f32"),
        decoder(form(3)((operands, mods) => Instruction.VCvtPkF16F32(operands(0), operands(1), operands(2), mods))),
        1683
      )
  case object VCvtPkFp8F16 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_pk_fp8_f16"),
        decoder(form(2)((operands, mods) => Instruction.VCvtPkFp8F16(operands(0), operands(1), mods))),
        1687
      )
  case object VCvtPkFp8F32 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_pk_fp8_f32"),
        decoder(form(3)((operands, mods) => Instruction.VCvtPkFp8F32(operands(0), operands(1), operands(2), mods))),
        1688
      )
  case object VCvtPkI16F32 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_pk_i16_f32"),
        decoder(form(3)((operands, mods) => Instruction.VCvtPkI16F32(operands(0), operands(1), operands(2), mods))),
        1689
      )
  case object VCvtPkI16I32 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_pk_i16_i32"),
        decoder(form(3)((operands, mods) => Instruction.VCvtPkI16I32(operands(0), operands(1), operands(2), mods))),
        1690
      )
  case object VCvtPkNormI16F16_1 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_pk_norm_i16_f16"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtPkNormI16F16_1(operands(0), operands(1), operands(2), mods)
        )),
        1691
      )
  case object VCvtPkNormI16F32_1 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_pk_norm_i16_f32"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtPkNormI16F32_1(operands(0), operands(1), operands(2), mods)
        )),
        1692
      )
  case object VCvtPkNormU16F16_1 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_pk_norm_u16_f16"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtPkNormU16F16_1(operands(0), operands(1), operands(2), mods)
        )),
        1693
      )
  case object VCvtPkNormU16F32_1 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_pk_norm_u16_f32"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtPkNormU16F32_1(operands(0), operands(1), operands(2), mods)
        )),
        1694
      )
  case object VCvtPkU16F32 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_pk_u16_f32"),
        decoder(form(3)((operands, mods) => Instruction.VCvtPkU16F32(operands(0), operands(1), operands(2), mods))),
        1696
      )
  case object VCvtPkU16U32 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_pk_u16_u32"),
        decoder(form(3)((operands, mods) => Instruction.VCvtPkU16U32(operands(0), operands(1), operands(2), mods))),
        1697
      )
  case object VCvtPkU8F32 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_pk_u8_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtPkU8F32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1698
      )
  case object VCvtPkaccumU8F32 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_pkaccum_u8_f32"),
        decoder(
          form(3)((operands, mods) => Instruction.VCvtPkaccumU8F32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.VCvtPkaccumU8F32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        1699
      )
  case object VCvtPknormI16F16_2 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_pknorm_i16_f16"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtPknormI16F16_2(operands(0), operands(1), operands(2), mods)
        )),
        1700
      )
  case object VCvtPknormI16F32_2 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_pknorm_i16_f32"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtPknormI16F32_2(operands(0), operands(1), operands(2), mods)
        )),
        1701
      )
  case object VCvtPknormU16F16_2 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_pknorm_u16_f16"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtPknormU16F16_2(operands(0), operands(1), operands(2), mods)
        )),
        1702
      )
  case object VCvtPknormU16F32_2 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_pknorm_u16_f32"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtPknormU16F32_2(operands(0), operands(1), operands(2), mods)
        )),
        1703
      )
  case object VCvtPkrtzF16F32E64 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_pkrtz_f16_f32_e64"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtPkrtzF16F32E64(operands(0), operands(1), operands(2), mods)
        )),
        1705
      )
  case object VCvtScalePk16Bf16Bf6 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scale_pk16_bf16_bf6"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalePk16Bf16Bf6(operands(0), operands(1), operands(2), mods)
        )),
        1707
      )
  case object VCvtScalePk16Bf16Fp6 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scale_pk16_bf16_fp6"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalePk16Bf16Fp6(operands(0), operands(1), operands(2), mods)
        )),
        1708
      )
  case object VCvtScalePk16F16Bf6 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scale_pk16_f16_bf6"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalePk16F16Bf6(operands(0), operands(1), operands(2), mods)
        )),
        1709
      )
  case object VCvtScalePk16F16Fp6 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scale_pk16_f16_fp6"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalePk16F16Fp6(operands(0), operands(1), operands(2), mods)
        )),
        1710
      )
  case object VCvtScalePk16F32Bf6 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scale_pk16_f32_bf6"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalePk16F32Bf6(operands(0), operands(1), operands(2), mods)
        )),
        1711
      )
  case object VCvtScalePk16F32Fp6 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scale_pk16_f32_fp6"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalePk16F32Fp6(operands(0), operands(1), operands(2), mods)
        )),
        1712
      )
  case object VCvtScalePk8Bf16Bf8 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scale_pk8_bf16_bf8"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalePk8Bf16Bf8(operands(0), operands(1), operands(2), mods)
        )),
        1713
      )
  case object VCvtScalePk8Bf16Fp4 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scale_pk8_bf16_fp4"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalePk8Bf16Fp4(operands(0), operands(1), operands(2), mods)
        )),
        1714
      )
  case object VCvtScalePk8Bf16Fp8 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scale_pk8_bf16_fp8"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalePk8Bf16Fp8(operands(0), operands(1), operands(2), mods)
        )),
        1715
      )
  case object VCvtScalePk8F16Bf8 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scale_pk8_f16_bf8"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalePk8F16Bf8(operands(0), operands(1), operands(2), mods)
        )),
        1716
      )
  case object VCvtScalePk8F16Fp4 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scale_pk8_f16_fp4"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalePk8F16Fp4(operands(0), operands(1), operands(2), mods)
        )),
        1717
      )
  case object VCvtScalePk8F16Fp8 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scale_pk8_f16_fp8"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalePk8F16Fp8(operands(0), operands(1), operands(2), mods)
        )),
        1718
      )
  case object VCvtScalePk8F32Bf8 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scale_pk8_f32_bf8"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalePk8F32Bf8(operands(0), operands(1), operands(2), mods)
        )),
        1719
      )
  case object VCvtScalePk8F32Fp4 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scale_pk8_f32_fp4"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalePk8F32Fp4(operands(0), operands(1), operands(2), mods)
        )),
        1720
      )
  case object VCvtScalePk8F32Fp8 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scale_pk8_f32_fp8"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalePk8F32Fp8(operands(0), operands(1), operands(2), mods)
        )),
        1721
      )
  case object VCvtScalef322xpk16Bf6F32 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_2xpk16_bf6_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef322xpk16Bf6F32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1722
      )
  case object VCvtScalef322xpk16Fp6F32 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_2xpk16_fp6_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef322xpk16Fp6F32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1723
      )
  case object VCvtScalef32F16Bf8 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_f16_bf8"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32F16Bf8(operands(0), operands(1), operands(2), mods)
        )),
        1724
      )
  case object VCvtScalef32F16Fp8 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_f16_fp8"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32F16Fp8(operands(0), operands(1), operands(2), mods)
        )),
        1725
      )
  case object VCvtScalef32F32Bf8 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_f32_bf8"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32F32Bf8(operands(0), operands(1), operands(2), mods)
        )),
        1726
      )
  case object VCvtScalef32F32Fp8 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_f32_fp8"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32F32Fp8(operands(0), operands(1), operands(2), mods)
        )),
        1727
      )
  case object VCvtScalef32Pk16Bf6Bf16 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_pk16_bf6_bf16"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32Pk16Bf6Bf16(operands(0), operands(1), operands(2), mods)
        )),
        1728
      )
  case object VCvtScalef32Pk16Bf6F16 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_pk16_bf6_f16"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32Pk16Bf6F16(operands(0), operands(1), operands(2), mods)
        )),
        1729
      )
  case object VCvtScalef32Pk16Bf6F32 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_pk16_bf6_f32"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32Pk16Bf6F32(operands(0), operands(1), operands(2), mods)
        )),
        1730
      )
  case object VCvtScalef32Pk16Fp6Bf16 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_pk16_fp6_bf16"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32Pk16Fp6Bf16(operands(0), operands(1), operands(2), mods)
        )),
        1731
      )
  case object VCvtScalef32Pk16Fp6F16 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_pk16_fp6_f16"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32Pk16Fp6F16(operands(0), operands(1), operands(2), mods)
        )),
        1732
      )
  case object VCvtScalef32Pk16Fp6F32 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_pk16_fp6_f32"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32Pk16Fp6F32(operands(0), operands(1), operands(2), mods)
        )),
        1733
      )
  case object VCvtScalef32Pk32Bf16Bf6 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_pk32_bf16_bf6"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32Pk32Bf16Bf6(operands(0), operands(1), operands(2), mods)
        )),
        1734
      )
  case object VCvtScalef32Pk32Bf16Fp6 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_pk32_bf16_fp6"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32Pk32Bf16Fp6(operands(0), operands(1), operands(2), mods)
        )),
        1735
      )
  case object VCvtScalef32Pk32Bf6Bf16 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_pk32_bf6_bf16"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32Pk32Bf6Bf16(operands(0), operands(1), operands(2), mods)
        )),
        1736
      )
  case object VCvtScalef32Pk32Bf6F16 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_pk32_bf6_f16"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32Pk32Bf6F16(operands(0), operands(1), operands(2), mods)
        )),
        1737
      )
  case object VCvtScalef32Pk32F16Bf6 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_pk32_f16_bf6"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32Pk32F16Bf6(operands(0), operands(1), operands(2), mods)
        )),
        1738
      )
  case object VCvtScalef32Pk32F16Fp6 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_pk32_f16_fp6"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32Pk32F16Fp6(operands(0), operands(1), operands(2), mods)
        )),
        1739
      )
  case object VCvtScalef32Pk32F32Bf6 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_pk32_f32_bf6"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32Pk32F32Bf6(operands(0), operands(1), operands(2), mods)
        )),
        1740
      )
  case object VCvtScalef32Pk32F32Fp6 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_pk32_f32_fp6"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32Pk32F32Fp6(operands(0), operands(1), operands(2), mods)
        )),
        1741
      )
  case object VCvtScalef32Pk32Fp6Bf16 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_pk32_fp6_bf16"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32Pk32Fp6Bf16(operands(0), operands(1), operands(2), mods)
        )),
        1742
      )
  case object VCvtScalef32Pk32Fp6F16 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_pk32_fp6_f16"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32Pk32Fp6F16(operands(0), operands(1), operands(2), mods)
        )),
        1743
      )
  case object VCvtScalef32Pk8Bf8Bf16 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_pk8_bf8_bf16"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32Pk8Bf8Bf16(operands(0), operands(1), operands(2), mods)
        )),
        1744
      )
  case object VCvtScalef32Pk8Bf8F16 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_pk8_bf8_f16"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32Pk8Bf8F16(operands(0), operands(1), operands(2), mods)
        )),
        1745
      )
  case object VCvtScalef32Pk8Bf8F32 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_pk8_bf8_f32"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32Pk8Bf8F32(operands(0), operands(1), operands(2), mods)
        )),
        1746
      )
  case object VCvtScalef32Pk8Fp4Bf16 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_pk8_fp4_bf16"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32Pk8Fp4Bf16(operands(0), operands(1), operands(2), mods)
        )),
        1747
      )
  case object VCvtScalef32Pk8Fp4F16 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_pk8_fp4_f16"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32Pk8Fp4F16(operands(0), operands(1), operands(2), mods)
        )),
        1748
      )
  case object VCvtScalef32Pk8Fp4F32 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_pk8_fp4_f32"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32Pk8Fp4F32(operands(0), operands(1), operands(2), mods)
        )),
        1749
      )
  case object VCvtScalef32Pk8Fp8Bf16 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_pk8_fp8_bf16"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32Pk8Fp8Bf16(operands(0), operands(1), operands(2), mods)
        )),
        1750
      )
  case object VCvtScalef32Pk8Fp8F16 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_pk8_fp8_f16"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32Pk8Fp8F16(operands(0), operands(1), operands(2), mods)
        )),
        1751
      )
  case object VCvtScalef32Pk8Fp8F32 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_pk8_fp8_f32"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32Pk8Fp8F32(operands(0), operands(1), operands(2), mods)
        )),
        1752
      )
  case object VCvtScalef32PkBf16Bf8 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_pk_bf16_bf8"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32PkBf16Bf8(operands(0), operands(1), operands(2), mods)
        )),
        1753
      )
  case object VCvtScalef32PkBf16Fp4 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_pk_bf16_fp4"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32PkBf16Fp4(operands(0), operands(1), operands(2), mods)
        )),
        1754
      )
  case object VCvtScalef32PkBf16Fp8 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_pk_bf16_fp8"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32PkBf16Fp8(operands(0), operands(1), operands(2), mods)
        )),
        1755
      )
  case object VCvtScalef32PkBf8Bf16 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_pk_bf8_bf16"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32PkBf8Bf16(operands(0), operands(1), operands(2), mods)
        )),
        1756
      )
  case object VCvtScalef32PkBf8F16 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_pk_bf8_f16"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32PkBf8F16(operands(0), operands(1), operands(2), mods)
        )),
        1757
      )
  case object VCvtScalef32PkBf8F32 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_pk_bf8_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32PkBf8F32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1758
      )
  case object VCvtScalef32PkF16Bf8 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_pk_f16_bf8"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32PkF16Bf8(operands(0), operands(1), operands(2), mods)
        )),
        1759
      )
  case object VCvtScalef32PkF16Fp4 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_pk_f16_fp4"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32PkF16Fp4(operands(0), operands(1), operands(2), mods)
        )),
        1760
      )
  case object VCvtScalef32PkF16Fp8 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_pk_f16_fp8"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32PkF16Fp8(operands(0), operands(1), operands(2), mods)
        )),
        1761
      )
  case object VCvtScalef32PkF32Bf8 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_pk_f32_bf8"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32PkF32Bf8(operands(0), operands(1), operands(2), mods)
        )),
        1762
      )
  case object VCvtScalef32PkF32Fp4 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_pk_f32_fp4"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32PkF32Fp4(operands(0), operands(1), operands(2), mods)
        )),
        1763
      )
  case object VCvtScalef32PkF32Fp8 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_pk_f32_fp8"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32PkF32Fp8(operands(0), operands(1), operands(2), mods)
        )),
        1764
      )
  case object VCvtScalef32PkFp4Bf16 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_pk_fp4_bf16"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32PkFp4Bf16(operands(0), operands(1), operands(2), mods)
        )),
        1765
      )
  case object VCvtScalef32PkFp4F16 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_pk_fp4_f16"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32PkFp4F16(operands(0), operands(1), operands(2), mods)
        )),
        1766
      )
  case object VCvtScalef32PkFp4F32 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_pk_fp4_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32PkFp4F32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1767
      )
  case object VCvtScalef32PkFp8Bf16 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_pk_fp8_bf16"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32PkFp8Bf16(operands(0), operands(1), operands(2), mods)
        )),
        1768
      )
  case object VCvtScalef32PkFp8F16 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_pk_fp8_f16"),
        decoder(form(3)((operands, mods) =>
          Instruction.VCvtScalef32PkFp8F16(operands(0), operands(1), operands(2), mods)
        )),
        1769
      )
  case object VCvtScalef32PkFp8F32 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_pk_fp8_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32PkFp8F32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1770
      )
  case object VCvtScalef32SrBf8Bf16 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_sr_bf8_bf16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32SrBf8Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1771
      )
  case object VCvtScalef32SrBf8F16 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_sr_bf8_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32SrBf8F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1772
      )
  case object VCvtScalef32SrBf8F32 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_sr_bf8_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32SrBf8F32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1773
      )
  case object VCvtScalef32SrFp8Bf16 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_sr_fp8_bf16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32SrFp8Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1774
      )
  case object VCvtScalef32SrFp8F16 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_sr_fp8_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32SrFp8F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1775
      )
  case object VCvtScalef32SrFp8F32 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_sr_fp8_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32SrFp8F32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1776
      )
  case object VCvtScalef32SrPk16Bf6Bf16 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_sr_pk16_bf6_bf16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32SrPk16Bf6Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1777
      )
  case object VCvtScalef32SrPk16Bf6F16 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_sr_pk16_bf6_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32SrPk16Bf6F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1778
      )
  case object VCvtScalef32SrPk16Bf6F32 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_sr_pk16_bf6_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32SrPk16Bf6F32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1779
      )
  case object VCvtScalef32SrPk16Fp6Bf16 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_sr_pk16_fp6_bf16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32SrPk16Fp6Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1780
      )
  case object VCvtScalef32SrPk16Fp6F16 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_sr_pk16_fp6_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32SrPk16Fp6F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1781
      )
  case object VCvtScalef32SrPk16Fp6F32 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_sr_pk16_fp6_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32SrPk16Fp6F32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1782
      )
  case object VCvtScalef32SrPk32Bf6Bf16 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_sr_pk32_bf6_bf16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32SrPk32Bf6Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1783
      )
  case object VCvtScalef32SrPk32Bf6F16 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_sr_pk32_bf6_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32SrPk32Bf6F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1784
      )
  case object VCvtScalef32SrPk32Bf6F32 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_sr_pk32_bf6_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32SrPk32Bf6F32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1785
      )
  case object VCvtScalef32SrPk32Fp6Bf16 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_sr_pk32_fp6_bf16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32SrPk32Fp6Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1786
      )
  case object VCvtScalef32SrPk32Fp6F16 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_sr_pk32_fp6_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32SrPk32Fp6F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1787
      )
  case object VCvtScalef32SrPk32Fp6F32 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_sr_pk32_fp6_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32SrPk32Fp6F32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1788
      )
  case object VCvtScalef32SrPk8Bf8Bf16 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_sr_pk8_bf8_bf16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32SrPk8Bf8Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1789
      )
  case object VCvtScalef32SrPk8Bf8F16 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_sr_pk8_bf8_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32SrPk8Bf8F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1790
      )
  case object VCvtScalef32SrPk8Bf8F32 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_sr_pk8_bf8_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32SrPk8Bf8F32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1791
      )
  case object VCvtScalef32SrPk8Fp4Bf16 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_sr_pk8_fp4_bf16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32SrPk8Fp4Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1792
      )
  case object VCvtScalef32SrPk8Fp4F16 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_sr_pk8_fp4_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32SrPk8Fp4F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1793
      )
  case object VCvtScalef32SrPk8Fp4F32 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_sr_pk8_fp4_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32SrPk8Fp4F32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1794
      )
  case object VCvtScalef32SrPk8Fp8Bf16 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_sr_pk8_fp8_bf16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32SrPk8Fp8Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1795
      )
  case object VCvtScalef32SrPk8Fp8F16 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_sr_pk8_fp8_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32SrPk8Fp8F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1796
      )
  case object VCvtScalef32SrPk8Fp8F32 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_sr_pk8_fp8_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32SrPk8Fp8F32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1797
      )
  case object VCvtScalef32SrPkFp4Bf16 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_sr_pk_fp4_bf16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32SrPkFp4Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1798
      )
  case object VCvtScalef32SrPkFp4F16 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_sr_pk_fp4_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32SrPkFp4F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1799
      )
  case object VCvtScalef32SrPkFp4F32 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_scalef32_sr_pk_fp4_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtScalef32SrPkFp4F32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1800
      )
  case object VCvtSrBf16F32 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_sr_bf16_f32"),
        decoder(form(3)((operands, mods) => Instruction.VCvtSrBf16F32(operands(0), operands(1), operands(2), mods))),
        1801
      )
  case object VCvtSrBf8F16 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_sr_bf8_f16"),
        decoder(form(3)((operands, mods) => Instruction.VCvtSrBf8F16(operands(0), operands(1), operands(2), mods))),
        1802
      )
  case object VCvtSrBf8F32 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_sr_bf8_f32"),
        decoder(form(3)((operands, mods) => Instruction.VCvtSrBf8F32(operands(0), operands(1), operands(2), mods))),
        1803
      )
  case object VCvtSrF16F32 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_sr_f16_f32"),
        decoder(form(3)((operands, mods) => Instruction.VCvtSrF16F32(operands(0), operands(1), operands(2), mods))),
        1804
      )
  case object VCvtSrFp8F16 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_sr_fp8_f16"),
        decoder(form(3)((operands, mods) => Instruction.VCvtSrFp8F16(operands(0), operands(1), operands(2), mods))),
        1805
      )
  case object VCvtSrFp8F32 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_sr_fp8_f32"),
        decoder(form(3)((operands, mods) => Instruction.VCvtSrFp8F32(operands(0), operands(1), operands(2), mods))),
        1806
      )
  case object VCvtSrPkBf16F32 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_sr_pk_bf16_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtSrPkBf16F32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1807
      )
  case object VCvtSrPkF16F32 extends InstructionDef(
        AMDGPUOpcode.assume("v_cvt_sr_pk_f16_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VCvtSrPkF16F32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1808
      )
  case object VDivFixupF16 extends InstructionDef(
        AMDGPUOpcode.assume("v_div_fixup_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VDivFixupF16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1813
      )
  case object VDivFixupF32 extends InstructionDef(
        AMDGPUOpcode.assume("v_div_fixup_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VDivFixupF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1814
      )
  case object VDivFixupF64 extends InstructionDef(
        AMDGPUOpcode.assume("v_div_fixup_f64"),
        decoder(form(4)((operands, mods) =>
          Instruction.VDivFixupF64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1815
      )
  case object VDivFixupLegacyF16 extends InstructionDef(
        AMDGPUOpcode.assume("v_div_fixup_legacy_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VDivFixupLegacyF16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1816
      )
  case object VDivFmasF32 extends InstructionDef(
        AMDGPUOpcode.assume("v_div_fmas_f32"),
        decoder(
          form(4)((operands, mods) =>
            Instruction.VDivFmasF32_4(operands(0), operands(1), operands(2), operands(3), mods)
          ),
          form(5)((operands, mods) =>
            Instruction.VDivFmasF32_5(operands(0), operands(1), operands(2), operands(3), operands(4), mods)
          )
        ),
        1817
      )
  case object VDivFmasF64 extends InstructionDef(
        AMDGPUOpcode.assume("v_div_fmas_f64"),
        decoder(
          form(4)((operands, mods) =>
            Instruction.VDivFmasF64_4(operands(0), operands(1), operands(2), operands(3), mods)
          ),
          form(5)((operands, mods) =>
            Instruction.VDivFmasF64_5(operands(0), operands(1), operands(2), operands(3), operands(4), mods)
          )
        ),
        1818
      )
  case object VDivScaleF32 extends InstructionDef(
        AMDGPUOpcode.assume("v_div_scale_f32"),
        decoder(form(5)((operands, mods) =>
          Instruction.VDivScaleF32(operands(0), operands(1), operands(2), operands(3), operands(4), mods)
        )),
        1819
      )
  case object VDivScaleF64 extends InstructionDef(
        AMDGPUOpcode.assume("v_div_scale_f64"),
        decoder(form(5)((operands, mods) =>
          Instruction.VDivScaleF64(operands(0), operands(1), operands(2), operands(3), operands(4), mods)
        )),
        1820
      )
  case object VDot2Bf16Bf16 extends InstructionDef(
        AMDGPUOpcode.assume("v_dot2_bf16_bf16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VDot2Bf16Bf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1821
      )
  case object VDot2F16F16 extends InstructionDef(
        AMDGPUOpcode.assume("v_dot2_f16_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VDot2F16F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1822
      )
  case object VFmaDx9ZeroF32 extends InstructionDef(
        AMDGPUOpcode.assume("v_fma_dx9_zero_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VFmaDx9ZeroF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1884
      )
  case object VFmaF16 extends InstructionDef(
        AMDGPUOpcode.assume("v_fma_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VFmaF16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1885
      )
  case object VFmaF32 extends InstructionDef(
        AMDGPUOpcode.assume("v_fma_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VFmaF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1886
      )
  case object VFmaF64 extends InstructionDef(
        AMDGPUOpcode.assume("v_fma_f64"),
        decoder(form(4)((operands, mods) =>
          Instruction.VFmaF64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1887
      )
  case object VFmaLegacyF16 extends InstructionDef(
        AMDGPUOpcode.assume("v_fma_legacy_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VFmaLegacyF16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1888
      )
  case object VFmaLegacyF32 extends InstructionDef(
        AMDGPUOpcode.assume("v_fma_legacy_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VFmaLegacyF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1889
      )
  case object VInterpMovF32 extends InstructionDef(
        AMDGPUOpcode.assume("v_interp_mov_f32"),
        decoder(
          form(2)((operands, mods) => Instruction.VInterpMovF32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VInterpMovF32_3(operands(0), operands(1), operands(2), mods))
        ),
        1916
      )
  case object VInterpP1F32 extends InstructionDef(
        AMDGPUOpcode.assume("v_interp_p1_f32"),
        decoder(
          form(2)((operands, mods) => Instruction.VInterpP1F32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VInterpP1F32_3(operands(0), operands(1), operands(2), mods))
        ),
        1920
      )
  case object VInterpP1llF16 extends InstructionDef(
        AMDGPUOpcode.assume("v_interp_p1ll_f16"),
        decoder(
          form(2)((operands, mods) => Instruction.VInterpP1llF16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.VInterpP1llF16_3(operands(0), operands(1), operands(2), mods))
        ),
        1921
      )
  case object VInterpP1lvF16 extends InstructionDef(
        AMDGPUOpcode.assume("v_interp_p1lv_f16"),
        decoder(
          form(3)((operands, mods) => Instruction.VInterpP1lvF16_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.VInterpP1lvF16_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        1922
      )
  case object VInterpP2F16 extends InstructionDef(
        AMDGPUOpcode.assume("v_interp_p2_f16"),
        decoder(
          form(3)((operands, mods) => Instruction.VInterpP2F16_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.VInterpP2F16_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        1923
      )
  case object VInterpP2F32 extends InstructionDef(
        AMDGPUOpcode.assume("v_interp_p2_f32"),
        decoder(
          form(3)((operands, mods) => Instruction.VInterpP2F32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.VInterpP2F32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        1925
      )
  case object VInterpP2HiF16 extends InstructionDef(
        AMDGPUOpcode.assume("v_interp_p2_hi_f16"),
        decoder(form(3)((operands, mods) => Instruction.VInterpP2HiF16(operands(0), operands(1), operands(2), mods))),
        1926
      )
  case object VInterpP2LegacyF16 extends InstructionDef(
        AMDGPUOpcode.assume("v_interp_p2_legacy_f16"),
        decoder(
          form(3)((operands, mods) => Instruction.VInterpP2LegacyF16_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.VInterpP2LegacyF16_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        1927
      )
  case object VLdexpF32 extends InstructionDef(
        AMDGPUOpcode.assume("v_ldexp_f32"),
        decoder(form(3)((operands, mods) => Instruction.VLdexpF32(operands(0), operands(1), operands(2), mods))),
        1930
      )
  case object VLdexpF64 extends InstructionDef(
        AMDGPUOpcode.assume("v_ldexp_f64"),
        decoder(form(3)((operands, mods) => Instruction.VLdexpF64(operands(0), operands(1), operands(2), mods))),
        1931
      )
  case object VLerpU8 extends InstructionDef(
        AMDGPUOpcode.assume("v_lerp_u8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VLerpU8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1932
      )
  case object VLshlAddU32 extends InstructionDef(
        AMDGPUOpcode.assume("v_lshl_add_u32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VLshlAddU32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1938
      )
  case object VLshlAddU64 extends InstructionDef(
        AMDGPUOpcode.assume("v_lshl_add_u64"),
        decoder(form(4)((operands, mods) =>
          Instruction.VLshlAddU64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1939
      )
  case object VLshlB64 extends InstructionDef(
        AMDGPUOpcode.assume("v_lshl_b64"),
        decoder(form(3)((operands, mods) => Instruction.VLshlB64(operands(0), operands(1), operands(2), mods))),
        1941
      )
  case object VLshlOrB32 extends InstructionDef(
        AMDGPUOpcode.assume("v_lshl_or_b32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VLshlOrB32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1942
      )
  case object VLshlrevB64E64 extends InstructionDef(
        AMDGPUOpcode.assume("v_lshlrev_b64_e64"),
        decoder(form(3)((operands, mods) => Instruction.VLshlrevB64E64(operands(0), operands(1), operands(2), mods))),
        1946
      )
  case object VLshrB64 extends InstructionDef(
        AMDGPUOpcode.assume("v_lshr_b64"),
        decoder(form(3)((operands, mods) => Instruction.VLshrB64(operands(0), operands(1), operands(2), mods))),
        1948
      )
  case object VLshrrevB64 extends InstructionDef(
        AMDGPUOpcode.assume("v_lshrrev_b64"),
        decoder(form(3)((operands, mods) => Instruction.VLshrrevB64(operands(0), operands(1), operands(2), mods))),
        1951
      )
  case object VMadCoI64I32 extends InstructionDef(
        AMDGPUOpcode.assume("v_mad_co_i64_i32"),
        decoder(form(5)((operands, mods) =>
          Instruction.VMadCoI64I32(operands(0), operands(1), operands(2), operands(3), operands(4), mods)
        )),
        1955
      )
  case object VMadCoU64U32 extends InstructionDef(
        AMDGPUOpcode.assume("v_mad_co_u64_u32"),
        decoder(form(5)((operands, mods) =>
          Instruction.VMadCoU64U32(operands(0), operands(1), operands(2), operands(3), operands(4), mods)
        )),
        1956
      )
  case object VMadF16 extends InstructionDef(
        AMDGPUOpcode.assume("v_mad_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMadF16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1957
      )
  case object VMadF32 extends InstructionDef(
        AMDGPUOpcode.assume("v_mad_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMadF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1958
      )
  case object VMadI16 extends InstructionDef(
        AMDGPUOpcode.assume("v_mad_i16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMadI16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1959
      )
  case object VMadI32I16 extends InstructionDef(
        AMDGPUOpcode.assume("v_mad_i32_i16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMadI32I16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1960
      )
  case object VMadI32I24 extends InstructionDef(
        AMDGPUOpcode.assume("v_mad_i32_i24"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMadI32I24(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1961
      )
  case object VMadI64I32 extends InstructionDef(
        AMDGPUOpcode.assume("v_mad_i64_i32"),
        decoder(form(5)((operands, mods) =>
          Instruction.VMadI64I32(operands(0), operands(1), operands(2), operands(3), operands(4), mods)
        )),
        1962
      )
  case object VMadLegacyF16 extends InstructionDef(
        AMDGPUOpcode.assume("v_mad_legacy_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMadLegacyF16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1963
      )
  case object VMadLegacyF32 extends InstructionDef(
        AMDGPUOpcode.assume("v_mad_legacy_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMadLegacyF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1964
      )
  case object VMadLegacyI16 extends InstructionDef(
        AMDGPUOpcode.assume("v_mad_legacy_i16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMadLegacyI16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1965
      )
  case object VMadLegacyU16 extends InstructionDef(
        AMDGPUOpcode.assume("v_mad_legacy_u16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMadLegacyU16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1966
      )
  case object VMadNcI64I32 extends InstructionDef(
        AMDGPUOpcode.assume("v_mad_nc_i64_i32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMadNcI64I32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1970
      )
  case object VMadNcU64U32 extends InstructionDef(
        AMDGPUOpcode.assume("v_mad_nc_u64_u32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMadNcU64U32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1971
      )
  case object VMadU16 extends InstructionDef(
        AMDGPUOpcode.assume("v_mad_u16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMadU16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1972
      )
  case object VMadU32 extends InstructionDef(
        AMDGPUOpcode.assume("v_mad_u32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMadU32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1973
      )
  case object VMadU32U16 extends InstructionDef(
        AMDGPUOpcode.assume("v_mad_u32_u16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMadU32U16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1974
      )
  case object VMadU32U24 extends InstructionDef(
        AMDGPUOpcode.assume("v_mad_u32_u24"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMadU32U24(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1975
      )
  case object VMadU64U32 extends InstructionDef(
        AMDGPUOpcode.assume("v_mad_u64_u32"),
        decoder(form(5)((operands, mods) =>
          Instruction.VMadU64U32(operands(0), operands(1), operands(2), operands(3), operands(4), mods)
        )),
        1976
      )
  case object VMax3F16 extends InstructionDef(
        AMDGPUOpcode.assume("v_max3_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMax3F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1981
      )
  case object VMax3F32 extends InstructionDef(
        AMDGPUOpcode.assume("v_max3_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMax3F32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1982
      )
  case object VMax3I16 extends InstructionDef(
        AMDGPUOpcode.assume("v_max3_i16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMax3I16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1983
      )
  case object VMax3I32 extends InstructionDef(
        AMDGPUOpcode.assume("v_max3_i32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMax3I32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1984
      )
  case object VMax3NumF16 extends InstructionDef(
        AMDGPUOpcode.assume("v_max3_num_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMax3NumF16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1985
      )
  case object VMax3NumF32 extends InstructionDef(
        AMDGPUOpcode.assume("v_max3_num_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMax3NumF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1986
      )
  case object VMax3U16 extends InstructionDef(
        AMDGPUOpcode.assume("v_max3_u16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMax3U16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1987
      )
  case object VMax3U32 extends InstructionDef(
        AMDGPUOpcode.assume("v_max3_u32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMax3U32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1988
      )
  case object VMaxF64 extends InstructionDef(
        AMDGPUOpcode.assume("v_max_f64"),
        decoder(form(3)((operands, mods) => Instruction.VMaxF64(operands(0), operands(1), operands(2), mods))),
        1991
      )
  case object VMaxF64E64 extends InstructionDef(
        AMDGPUOpcode.assume("v_max_f64_e64"),
        decoder(form(3)((operands, mods) => Instruction.VMaxF64E64(operands(0), operands(1), operands(2), mods))),
        1992
      )
  case object VMaxI64 extends InstructionDef(
        AMDGPUOpcode.assume("v_max_i64"),
        decoder(form(3)((operands, mods) => Instruction.VMaxI64(operands(0), operands(1), operands(2), mods))),
        1995
      )
  case object VMaxU64 extends InstructionDef(
        AMDGPUOpcode.assume("v_max_u64"),
        decoder(form(3)((operands, mods) => Instruction.VMaxU64(operands(0), operands(1), operands(2), mods))),
        2002
      )
  case object VMaximum3F16 extends InstructionDef(
        AMDGPUOpcode.assume("v_maximum3_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMaximum3F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2003
      )
  case object VMaximum3F32 extends InstructionDef(
        AMDGPUOpcode.assume("v_maximum3_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMaximum3F32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2004
      )
  case object VMaximumF16 extends InstructionDef(
        AMDGPUOpcode.assume("v_maximum_f16"),
        decoder(form(3)((operands, mods) => Instruction.VMaximumF16(operands(0), operands(1), operands(2), mods))),
        2005
      )
  case object VMaximumF32 extends InstructionDef(
        AMDGPUOpcode.assume("v_maximum_f32"),
        decoder(form(3)((operands, mods) => Instruction.VMaximumF32(operands(0), operands(1), operands(2), mods))),
        2006
      )
  case object VMaximumF64 extends InstructionDef(
        AMDGPUOpcode.assume("v_maximum_f64"),
        decoder(form(3)((operands, mods) => Instruction.VMaximumF64(operands(0), operands(1), operands(2), mods))),
        2007
      )
  case object VMaximumminimumF16 extends InstructionDef(
        AMDGPUOpcode.assume("v_maximumminimum_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMaximumminimumF16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2008
      )
  case object VMaximumminimumF32 extends InstructionDef(
        AMDGPUOpcode.assume("v_maximumminimum_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMaximumminimumF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2009
      )
  case object VMaxminF16 extends InstructionDef(
        AMDGPUOpcode.assume("v_maxmin_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMaxminF16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2010
      )
  case object VMaxminF32 extends InstructionDef(
        AMDGPUOpcode.assume("v_maxmin_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMaxminF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2011
      )
  case object VMaxminI32 extends InstructionDef(
        AMDGPUOpcode.assume("v_maxmin_i32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMaxminI32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2012
      )
  case object VMaxminNumF16 extends InstructionDef(
        AMDGPUOpcode.assume("v_maxmin_num_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMaxminNumF16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2013
      )
  case object VMaxminNumF32 extends InstructionDef(
        AMDGPUOpcode.assume("v_maxmin_num_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMaxminNumF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2014
      )
  case object VMaxminU32 extends InstructionDef(
        AMDGPUOpcode.assume("v_maxmin_u32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMaxminU32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2015
      )
  case object VMbcntHiU32B32E64 extends InstructionDef(
        AMDGPUOpcode.assume("v_mbcnt_hi_u32_b32_e64"),
        decoder(form(3)((operands, mods) =>
          Instruction.VMbcntHiU32B32E64(operands(0), operands(1), operands(2), mods)
        )),
        2017
      )
  case object VMbcntLoU32B32 extends InstructionDef(
        AMDGPUOpcode.assume("v_mbcnt_lo_u32_b32"),
        decoder(form(3)((operands, mods) => Instruction.VMbcntLoU32B32(operands(0), operands(1), operands(2), mods))),
        2018
      )
  case object VMed3F16 extends InstructionDef(
        AMDGPUOpcode.assume("v_med3_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMed3F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2019
      )
  case object VMed3F32 extends InstructionDef(
        AMDGPUOpcode.assume("v_med3_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMed3F32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2020
      )
  case object VMed3I16 extends InstructionDef(
        AMDGPUOpcode.assume("v_med3_i16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMed3I16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2021
      )
  case object VMed3I32 extends InstructionDef(
        AMDGPUOpcode.assume("v_med3_i32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMed3I32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2022
      )
  case object VMed3NumF16 extends InstructionDef(
        AMDGPUOpcode.assume("v_med3_num_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMed3NumF16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2023
      )
  case object VMed3NumF32 extends InstructionDef(
        AMDGPUOpcode.assume("v_med3_num_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMed3NumF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2024
      )
  case object VMed3U16 extends InstructionDef(
        AMDGPUOpcode.assume("v_med3_u16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMed3U16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2025
      )
  case object VMed3U32 extends InstructionDef(
        AMDGPUOpcode.assume("v_med3_u32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMed3U32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2026
      )
  case object VMin3F16 extends InstructionDef(
        AMDGPUOpcode.assume("v_min3_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMin3F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2096
      )
  case object VMin3F32 extends InstructionDef(
        AMDGPUOpcode.assume("v_min3_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMin3F32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2097
      )
  case object VMin3I16 extends InstructionDef(
        AMDGPUOpcode.assume("v_min3_i16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMin3I16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2098
      )
  case object VMin3I32 extends InstructionDef(
        AMDGPUOpcode.assume("v_min3_i32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMin3I32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2099
      )
  case object VMin3NumF16 extends InstructionDef(
        AMDGPUOpcode.assume("v_min3_num_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMin3NumF16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2100
      )
  case object VMin3NumF32 extends InstructionDef(
        AMDGPUOpcode.assume("v_min3_num_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMin3NumF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2101
      )
  case object VMin3U16 extends InstructionDef(
        AMDGPUOpcode.assume("v_min3_u16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMin3U16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2102
      )
  case object VMin3U32 extends InstructionDef(
        AMDGPUOpcode.assume("v_min3_u32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMin3U32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2103
      )
  case object VMinF64 extends InstructionDef(
        AMDGPUOpcode.assume("v_min_f64"),
        decoder(form(3)((operands, mods) => Instruction.VMinF64(operands(0), operands(1), operands(2), mods))),
        2106
      )
  case object VMinF64E64 extends InstructionDef(
        AMDGPUOpcode.assume("v_min_f64_e64"),
        decoder(form(3)((operands, mods) => Instruction.VMinF64E64(operands(0), operands(1), operands(2), mods))),
        2107
      )
  case object VMinI64 extends InstructionDef(
        AMDGPUOpcode.assume("v_min_i64"),
        decoder(form(3)((operands, mods) => Instruction.VMinI64(operands(0), operands(1), operands(2), mods))),
        2110
      )
  case object VMinU64 extends InstructionDef(
        AMDGPUOpcode.assume("v_min_u64"),
        decoder(form(3)((operands, mods) => Instruction.VMinU64(operands(0), operands(1), operands(2), mods))),
        2117
      )
  case object VMinimum3F16 extends InstructionDef(
        AMDGPUOpcode.assume("v_minimum3_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMinimum3F16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2118
      )
  case object VMinimum3F32 extends InstructionDef(
        AMDGPUOpcode.assume("v_minimum3_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMinimum3F32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2119
      )
  case object VMinimumF16 extends InstructionDef(
        AMDGPUOpcode.assume("v_minimum_f16"),
        decoder(form(3)((operands, mods) => Instruction.VMinimumF16(operands(0), operands(1), operands(2), mods))),
        2120
      )
  case object VMinimumF32 extends InstructionDef(
        AMDGPUOpcode.assume("v_minimum_f32"),
        decoder(form(3)((operands, mods) => Instruction.VMinimumF32(operands(0), operands(1), operands(2), mods))),
        2121
      )
  case object VMinimumF64 extends InstructionDef(
        AMDGPUOpcode.assume("v_minimum_f64"),
        decoder(form(3)((operands, mods) => Instruction.VMinimumF64(operands(0), operands(1), operands(2), mods))),
        2122
      )
  case object VMinimummaximumF16 extends InstructionDef(
        AMDGPUOpcode.assume("v_minimummaximum_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMinimummaximumF16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2123
      )
  case object VMinimummaximumF32 extends InstructionDef(
        AMDGPUOpcode.assume("v_minimummaximum_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMinimummaximumF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2124
      )
  case object VMinmaxF16 extends InstructionDef(
        AMDGPUOpcode.assume("v_minmax_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMinmaxF16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2125
      )
  case object VMinmaxF32 extends InstructionDef(
        AMDGPUOpcode.assume("v_minmax_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMinmaxF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2126
      )
  case object VMinmaxI32 extends InstructionDef(
        AMDGPUOpcode.assume("v_minmax_i32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMinmaxI32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2127
      )
  case object VMinmaxNumF16 extends InstructionDef(
        AMDGPUOpcode.assume("v_minmax_num_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMinmaxNumF16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2128
      )
  case object VMinmaxNumF32 extends InstructionDef(
        AMDGPUOpcode.assume("v_minmax_num_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMinmaxNumF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2129
      )
  case object VMinmaxU32 extends InstructionDef(
        AMDGPUOpcode.assume("v_minmax_u32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMinmaxU32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2130
      )
  case object VMqsadPkU16U8 extends InstructionDef(
        AMDGPUOpcode.assume("v_mqsad_pk_u16_u8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMqsadPkU16U8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2138
      )
  case object VMqsadU32U8 extends InstructionDef(
        AMDGPUOpcode.assume("v_mqsad_u32_u8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMqsadU32U8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2139
      )
  case object VMsadU8 extends InstructionDef(
        AMDGPUOpcode.assume("v_msad_u8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMsadU8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2140
      )
  case object VMulF64E64 extends InstructionDef(
        AMDGPUOpcode.assume("v_mul_f64_e64"),
        decoder(form(3)((operands, mods) => Instruction.VMulF64E64(operands(0), operands(1), operands(2), mods))),
        2145
      )
  case object VMulHiI32 extends InstructionDef(
        AMDGPUOpcode.assume("v_mul_hi_i32"),
        decoder(form(3)((operands, mods) => Instruction.VMulHiI32(operands(0), operands(1), operands(2), mods))),
        2146
      )
  case object VMulHiU32 extends InstructionDef(
        AMDGPUOpcode.assume("v_mul_hi_u32"),
        decoder(form(3)((operands, mods) => Instruction.VMulHiU32(operands(0), operands(1), operands(2), mods))),
        2148
      )
  case object VMulLoI32 extends InstructionDef(
        AMDGPUOpcode.assume("v_mul_lo_i32"),
        decoder(form(3)((operands, mods) => Instruction.VMulLoI32(operands(0), operands(1), operands(2), mods))),
        2152
      )
  case object VMulLoU32 extends InstructionDef(
        AMDGPUOpcode.assume("v_mul_lo_u32"),
        decoder(form(3)((operands, mods) => Instruction.VMulLoU32(operands(0), operands(1), operands(2), mods))),
        2154
      )
  case object VMullitF32 extends InstructionDef(
        AMDGPUOpcode.assume("v_mullit_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VMullitF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2157
      )
  case object VOr3B32 extends InstructionDef(
        AMDGPUOpcode.assume("v_or3_b32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VOr3B32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2161
      )
  case object VOrB16 extends InstructionDef(
        AMDGPUOpcode.assume("v_or_b16"),
        decoder(form(3)((operands, mods) => Instruction.VOrB16(operands(0), operands(1), operands(2), mods))),
        2162
      )
  case object VPackB32F16 extends InstructionDef(
        AMDGPUOpcode.assume("v_pack_b32_f16"),
        decoder(form(3)((operands, mods) => Instruction.VPackB32F16(operands(0), operands(1), operands(2), mods))),
        2164
      )
  case object VPermB32 extends InstructionDef(
        AMDGPUOpcode.assume("v_perm_b32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VPermB32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2165
      )
  case object VPermPk16B4U4 extends InstructionDef(
        AMDGPUOpcode.assume("v_perm_pk16_b4_u4"),
        decoder(form(4)((operands, mods) =>
          Instruction.VPermPk16B4U4(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2166
      )
  case object VPermPk16B6U4 extends InstructionDef(
        AMDGPUOpcode.assume("v_perm_pk16_b6_u4"),
        decoder(form(4)((operands, mods) =>
          Instruction.VPermPk16B6U4(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2167
      )
  case object VPermPk16B8U4 extends InstructionDef(
        AMDGPUOpcode.assume("v_perm_pk16_b8_u4"),
        decoder(form(4)((operands, mods) =>
          Instruction.VPermPk16B8U4(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2168
      )
  case object VPermlane16B32 extends InstructionDef(
        AMDGPUOpcode.assume("v_permlane16_b32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VPermlane16B32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2169
      )
  case object VPermlane16VarB32 extends InstructionDef(
        AMDGPUOpcode.assume("v_permlane16_var_b32"),
        decoder(form(3)((operands, mods) =>
          Instruction.VPermlane16VarB32(operands(0), operands(1), operands(2), mods)
        )),
        2171
      )
  case object VPermlaneBcastB32 extends InstructionDef(
        AMDGPUOpcode.assume("v_permlane_bcast_b32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VPermlaneBcastB32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2174
      )
  case object VPermlaneDownB32 extends InstructionDef(
        AMDGPUOpcode.assume("v_permlane_down_b32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VPermlaneDownB32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2175
      )
  case object VPermlaneIdxGenB32 extends InstructionDef(
        AMDGPUOpcode.assume("v_permlane_idx_gen_b32"),
        decoder(form(3)((operands, mods) =>
          Instruction.VPermlaneIdxGenB32(operands(0), operands(1), operands(2), mods)
        )),
        2176
      )
  case object VPermlaneUpB32 extends InstructionDef(
        AMDGPUOpcode.assume("v_permlane_up_b32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VPermlaneUpB32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2177
      )
  case object VPermlaneXorB32 extends InstructionDef(
        AMDGPUOpcode.assume("v_permlane_xor_b32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VPermlaneXorB32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2178
      )
  case object VPermlanex16B32 extends InstructionDef(
        AMDGPUOpcode.assume("v_permlanex16_b32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VPermlanex16B32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2179
      )
  case object VPermlanex16VarB32 extends InstructionDef(
        AMDGPUOpcode.assume("v_permlanex16_var_b32"),
        decoder(form(3)((operands, mods) =>
          Instruction.VPermlanex16VarB32(operands(0), operands(1), operands(2), mods)
        )),
        2180
      )
  case object VQsadPkU16U8 extends InstructionDef(
        AMDGPUOpcode.assume("v_qsad_pk_u16_u8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VQsadPkU16U8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2228
      )
  case object VReadlaneB32E64 extends InstructionDef(
        AMDGPUOpcode.assume("v_readlane_b32_e64"),
        decoder(form(3)((operands, mods) => Instruction.VReadlaneB32E64(operands(0), operands(1), operands(2), mods))),
        2239
      )
  case object VSExpF16 extends InstructionDef(
        AMDGPUOpcode.assume("v_s_exp_f16"),
        decoder(form(2)((operands, mods) => Instruction.VSExpF16(operands(0), operands(1), mods))),
        2250
      )
  case object VSExpF32 extends InstructionDef(
        AMDGPUOpcode.assume("v_s_exp_f32"),
        decoder(form(2)((operands, mods) => Instruction.VSExpF32(operands(0), operands(1), mods))),
        2251
      )
  case object VSLogF16 extends InstructionDef(
        AMDGPUOpcode.assume("v_s_log_f16"),
        decoder(form(2)((operands, mods) => Instruction.VSLogF16(operands(0), operands(1), mods))),
        2252
      )
  case object VSLogF32 extends InstructionDef(
        AMDGPUOpcode.assume("v_s_log_f32"),
        decoder(form(2)((operands, mods) => Instruction.VSLogF32(operands(0), operands(1), mods))),
        2253
      )
  case object VSRcpF16 extends InstructionDef(
        AMDGPUOpcode.assume("v_s_rcp_f16"),
        decoder(form(2)((operands, mods) => Instruction.VSRcpF16(operands(0), operands(1), mods))),
        2254
      )
  case object VSRcpF32 extends InstructionDef(
        AMDGPUOpcode.assume("v_s_rcp_f32"),
        decoder(form(2)((operands, mods) => Instruction.VSRcpF32(operands(0), operands(1), mods))),
        2255
      )
  case object VSRsqF16 extends InstructionDef(
        AMDGPUOpcode.assume("v_s_rsq_f16"),
        decoder(form(2)((operands, mods) => Instruction.VSRsqF16(operands(0), operands(1), mods))),
        2256
      )
  case object VSRsqF32 extends InstructionDef(
        AMDGPUOpcode.assume("v_s_rsq_f32"),
        decoder(form(2)((operands, mods) => Instruction.VSRsqF32(operands(0), operands(1), mods))),
        2257
      )
  case object VSSqrtF16 extends InstructionDef(
        AMDGPUOpcode.assume("v_s_sqrt_f16"),
        decoder(form(2)((operands, mods) => Instruction.VSSqrtF16(operands(0), operands(1), mods))),
        2258
      )
  case object VSSqrtF32 extends InstructionDef(
        AMDGPUOpcode.assume("v_s_sqrt_f32"),
        decoder(form(2)((operands, mods) => Instruction.VSSqrtF32(operands(0), operands(1), mods))),
        2259
      )
  case object VSadHiU8 extends InstructionDef(
        AMDGPUOpcode.assume("v_sad_hi_u8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VSadHiU8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2260
      )
  case object VSadU16 extends InstructionDef(
        AMDGPUOpcode.assume("v_sad_u16"),
        decoder(form(4)((operands, mods) =>
          Instruction.VSadU16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2261
      )
  case object VSadU32 extends InstructionDef(
        AMDGPUOpcode.assume("v_sad_u32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VSadU32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2262
      )
  case object VSadU8 extends InstructionDef(
        AMDGPUOpcode.assume("v_sad_u8"),
        decoder(form(4)((operands, mods) =>
          Instruction.VSadU8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2263
      )
  case object VSubI16 extends InstructionDef(
        AMDGPUOpcode.assume("v_sub_i16"),
        decoder(form(3)((operands, mods) => Instruction.VSubI16(operands(0), operands(1), operands(2), mods))),
        2307
      )
  case object VSubI32 extends InstructionDef(
        AMDGPUOpcode.assume("v_sub_i32"),
        decoder(
          form(3)((operands, mods) => Instruction.VSubI32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) => Instruction.VSubI32_4(operands(0), operands(1), operands(2), operands(3), mods))
        ),
        2308
      )
  case object VSubNcI16 extends InstructionDef(
        AMDGPUOpcode.assume("v_sub_nc_i16"),
        decoder(form(3)((operands, mods) => Instruction.VSubNcI16(operands(0), operands(1), operands(2), mods))),
        2309
      )
  case object VSubNcI32 extends InstructionDef(
        AMDGPUOpcode.assume("v_sub_nc_i32"),
        decoder(form(3)((operands, mods) => Instruction.VSubNcI32(operands(0), operands(1), operands(2), mods))),
        2310
      )
  case object VSubNcU16 extends InstructionDef(
        AMDGPUOpcode.assume("v_sub_nc_u16"),
        decoder(form(3)((operands, mods) => Instruction.VSubNcU16(operands(0), operands(1), operands(2), mods))),
        2311
      )
  case object VTrigPreopF64 extends InstructionDef(
        AMDGPUOpcode.assume("v_trig_preop_f64"),
        decoder(form(3)((operands, mods) => Instruction.VTrigPreopF64(operands(0), operands(1), operands(2), mods))),
        2356
      )
  case object VWritelaneB32E64 extends InstructionDef(
        AMDGPUOpcode.assume("v_writelane_b32_e64"),
        decoder(form(4)((operands, mods) =>
          Instruction.VWritelaneB32E64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2401
      )
  case object VXadU32 extends InstructionDef(
        AMDGPUOpcode.assume("v_xad_u32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VXadU32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2402
      )
  case object VXor3B32 extends InstructionDef(
        AMDGPUOpcode.assume("v_xor3_b32"),
        decoder(form(4)((operands, mods) =>
          Instruction.VXor3B32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2404
      )
  case object VXorB16 extends InstructionDef(
        AMDGPUOpcode.assume("v_xor_b16"),
        decoder(form(3)((operands, mods) => Instruction.VXorB16(operands(0), operands(1), operands(2), mods))),
        2405
      )

  val definitions: Vec[InstructionDef] = Vec(
    VAdd3U32,
    VAddF64E64,
    VAddI16,
    VAddI32,
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
    VBcntU32B32,
    VBfeI32,
    VBfeU32,
    VBfiB32,
    VBfmB32,
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
    VCvtPkI16I32,
    VCvtPkNormI16F16_1,
    VCvtPkNormI16F32_1,
    VCvtPkNormU16F16_1,
    VCvtPkNormU16F32_1,
    VCvtPkU16F32,
    VCvtPkU16U32,
    VCvtPkU8F32,
    VCvtPkaccumU8F32,
    VCvtPknormI16F16_2,
    VCvtPknormI16F32_2,
    VCvtPknormU16F16_2,
    VCvtPknormU16F32_2,
    VCvtPkrtzF16F32E64,
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
    VLdexpF32,
    VLdexpF64,
    VLerpU8,
    VLshlAddU32,
    VLshlAddU64,
    VLshlB64,
    VLshlOrB32,
    VLshlrevB64E64,
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
    VMaxF64E64,
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
    VMbcntHiU32B32E64,
    VMbcntLoU32B32,
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
    VMinF64E64,
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
    VMulF64E64,
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
    VReadlaneB32E64,
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
    VSubI32,
    VSubNcI16,
    VSubNcI32,
    VSubNcU16,
    VTrigPreopF64,
    VWritelaneB32E64,
    VXadU32,
    VXor3B32,
    VXorB16
  )
}
