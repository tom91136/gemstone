// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu

import javax.annotation.processing.Generated

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.collection.*

@Generated(Array("sbt codegen"))
private object BufferInstructionDefs {
  case object BufferAtomicAdd extends Opcode("buffer_atomic_add", 0)

  case object BufferAtomicAddF32 extends Opcode("buffer_atomic_add_f32", 1)

  case object BufferAtomicAddF64 extends Opcode("buffer_atomic_add_f64", 2)

  case object BufferAtomicAddU32 extends Opcode("buffer_atomic_add_u32", 3)

  case object BufferAtomicAddU64 extends Opcode("buffer_atomic_add_u64", 4)

  case object BufferAtomicAddX2 extends Opcode("buffer_atomic_add_x2", 5)

  case object BufferAtomicAnd extends Opcode("buffer_atomic_and", 6)

  case object BufferAtomicAndB32 extends Opcode("buffer_atomic_and_b32", 7)

  case object BufferAtomicAndB64 extends Opcode("buffer_atomic_and_b64", 8)

  case object BufferAtomicAndX2 extends Opcode("buffer_atomic_and_x2", 9)

  case object BufferAtomicCmpswap extends Opcode("buffer_atomic_cmpswap", 10)

  case object BufferAtomicCmpswapB32 extends Opcode("buffer_atomic_cmpswap_b32", 11)

  case object BufferAtomicCmpswapB64 extends Opcode("buffer_atomic_cmpswap_b64", 12)

  case object BufferAtomicCmpswapF32 extends Opcode("buffer_atomic_cmpswap_f32", 13)

  case object BufferAtomicCmpswapX2 extends Opcode("buffer_atomic_cmpswap_x2", 14)

  case object BufferAtomicCondSubU32 extends Opcode("buffer_atomic_cond_sub_u32", 15)

  case object BufferAtomicCsub extends Opcode("buffer_atomic_csub", 16)

  case object BufferAtomicCsubU32 extends Opcode("buffer_atomic_csub_u32", 17)

  case object BufferAtomicDec extends Opcode("buffer_atomic_dec", 18)

  case object BufferAtomicDecU32 extends Opcode("buffer_atomic_dec_u32", 19)

  case object BufferAtomicDecU64 extends Opcode("buffer_atomic_dec_u64", 20)

  case object BufferAtomicDecX2 extends Opcode("buffer_atomic_dec_x2", 21)

  case object BufferAtomicFcmpswap extends Opcode("buffer_atomic_fcmpswap", 22)

  case object BufferAtomicFcmpswapX2 extends Opcode("buffer_atomic_fcmpswap_x2", 23)

  case object BufferAtomicFmax extends Opcode("buffer_atomic_fmax", 24)

  case object BufferAtomicFmaxX2 extends Opcode("buffer_atomic_fmax_x2", 25)

  case object BufferAtomicFmin extends Opcode("buffer_atomic_fmin", 26)

  case object BufferAtomicFminX2 extends Opcode("buffer_atomic_fmin_x2", 27)

  case object BufferAtomicInc extends Opcode("buffer_atomic_inc", 28)

  case object BufferAtomicIncU32 extends Opcode("buffer_atomic_inc_u32", 29)

  case object BufferAtomicIncU64 extends Opcode("buffer_atomic_inc_u64", 30)

  case object BufferAtomicIncX2 extends Opcode("buffer_atomic_inc_x2", 31)

  case object BufferAtomicMaxF32 extends Opcode("buffer_atomic_max_f32", 32)

  case object BufferAtomicMaxF64 extends Opcode("buffer_atomic_max_f64", 33)

  case object BufferAtomicMaxI32 extends Opcode("buffer_atomic_max_i32", 34)

  case object BufferAtomicMaxI64 extends Opcode("buffer_atomic_max_i64", 35)

  case object BufferAtomicMaxNumF32 extends Opcode("buffer_atomic_max_num_f32", 36)

  case object BufferAtomicMaxNumF64 extends Opcode("buffer_atomic_max_num_f64", 37)

  case object BufferAtomicMaxU32 extends Opcode("buffer_atomic_max_u32", 38)

  case object BufferAtomicMaxU64 extends Opcode("buffer_atomic_max_u64", 39)

  case object BufferAtomicMinF32 extends Opcode("buffer_atomic_min_f32", 40)

  case object BufferAtomicMinF64 extends Opcode("buffer_atomic_min_f64", 41)

  case object BufferAtomicMinI32 extends Opcode("buffer_atomic_min_i32", 42)

  case object BufferAtomicMinI64 extends Opcode("buffer_atomic_min_i64", 43)

  case object BufferAtomicMinNumF32 extends Opcode("buffer_atomic_min_num_f32", 44)

  case object BufferAtomicMinNumF64 extends Opcode("buffer_atomic_min_num_f64", 45)

  case object BufferAtomicMinU32 extends Opcode("buffer_atomic_min_u32", 46)

  case object BufferAtomicMinU64 extends Opcode("buffer_atomic_min_u64", 47)

  case object BufferAtomicOr extends Opcode("buffer_atomic_or", 48)

  case object BufferAtomicOrB32 extends Opcode("buffer_atomic_or_b32", 49)

  case object BufferAtomicOrB64 extends Opcode("buffer_atomic_or_b64", 50)

  case object BufferAtomicOrX2 extends Opcode("buffer_atomic_or_x2", 51)

  case object BufferAtomicPkAddBf16 extends Opcode("buffer_atomic_pk_add_bf16", 52)

  case object BufferAtomicPkAddF16 extends Opcode("buffer_atomic_pk_add_f16", 53)

  case object BufferAtomicRsub extends Opcode("buffer_atomic_rsub", 54)

  case object BufferAtomicRsubX2 extends Opcode("buffer_atomic_rsub_x2", 55)

  case object BufferAtomicSmax extends Opcode("buffer_atomic_smax", 56)

  case object BufferAtomicSmaxX2 extends Opcode("buffer_atomic_smax_x2", 57)

  case object BufferAtomicSmin extends Opcode("buffer_atomic_smin", 58)

  case object BufferAtomicSminX2 extends Opcode("buffer_atomic_smin_x2", 59)

  case object BufferAtomicSub extends Opcode("buffer_atomic_sub", 60)

  case object BufferAtomicSubClampU32 extends Opcode("buffer_atomic_sub_clamp_u32", 61)

  case object BufferAtomicSubU32 extends Opcode("buffer_atomic_sub_u32", 62)

  case object BufferAtomicSubU64 extends Opcode("buffer_atomic_sub_u64", 63)

  case object BufferAtomicSubX2 extends Opcode("buffer_atomic_sub_x2", 64)

  case object BufferAtomicSwap extends Opcode("buffer_atomic_swap", 65)

  case object BufferAtomicSwapB32 extends Opcode("buffer_atomic_swap_b32", 66)

  case object BufferAtomicSwapB64 extends Opcode("buffer_atomic_swap_b64", 67)

  case object BufferAtomicSwapX2 extends Opcode("buffer_atomic_swap_x2", 68)

  case object BufferAtomicUmax extends Opcode("buffer_atomic_umax", 69)

  case object BufferAtomicUmaxX2 extends Opcode("buffer_atomic_umax_x2", 70)

  case object BufferAtomicUmin extends Opcode("buffer_atomic_umin", 71)

  case object BufferAtomicUminX2 extends Opcode("buffer_atomic_umin_x2", 72)

  case object BufferAtomicXor extends Opcode("buffer_atomic_xor", 73)

  case object BufferAtomicXorB32 extends Opcode("buffer_atomic_xor_b32", 74)

  case object BufferAtomicXorB64 extends Opcode("buffer_atomic_xor_b64", 75)

  case object BufferAtomicXorX2 extends Opcode("buffer_atomic_xor_x2", 76)

  case object BufferGl0Inv extends Opcode("buffer_gl0_inv", 77)

  case object BufferGl1Inv extends Opcode("buffer_gl1_inv", 78)

  case object BufferInv extends Opcode("buffer_inv", 79)

  case object BufferInvl2 extends Opcode("buffer_invl2", 80)

  case object BufferLoadB128 extends Opcode("buffer_load_b128", 81)

  case object BufferLoadB32 extends Opcode("buffer_load_b32", 82)

  case object BufferLoadB64 extends Opcode("buffer_load_b64", 83)

  case object BufferLoadB96 extends Opcode("buffer_load_b96", 84)

  case object BufferLoadD16B16 extends Opcode("buffer_load_d16_b16", 85)

  case object BufferLoadD16FormatX extends Opcode("buffer_load_d16_format_x", 86)

  case object BufferLoadD16FormatXy extends Opcode("buffer_load_d16_format_xy", 87)

  case object BufferLoadD16FormatXyz extends Opcode("buffer_load_d16_format_xyz", 88)

  case object BufferLoadD16FormatXyzw extends Opcode("buffer_load_d16_format_xyzw", 89)

  case object BufferLoadD16HiB16 extends Opcode("buffer_load_d16_hi_b16", 90)

  case object BufferLoadD16HiFormatX extends Opcode("buffer_load_d16_hi_format_x", 91)

  case object BufferLoadD16HiI8 extends Opcode("buffer_load_d16_hi_i8", 92)

  case object BufferLoadD16HiU8 extends Opcode("buffer_load_d16_hi_u8", 93)

  case object BufferLoadD16I8 extends Opcode("buffer_load_d16_i8", 94)

  case object BufferLoadD16U8 extends Opcode("buffer_load_d16_u8", 95)

  case object BufferLoadDword extends Opcode("buffer_load_dword", 96)

  case object BufferLoadDwordx2 extends Opcode("buffer_load_dwordx2", 97)

  case object BufferLoadDwordx3 extends Opcode("buffer_load_dwordx3", 98)

  case object BufferLoadDwordx4 extends Opcode("buffer_load_dwordx4", 99)

  case object BufferLoadFormatD16HiX extends Opcode("buffer_load_format_d16_hi_x", 100)

  case object BufferLoadFormatD16X extends Opcode("buffer_load_format_d16_x", 101)

  case object BufferLoadFormatD16Xy extends Opcode("buffer_load_format_d16_xy", 102)

  case object BufferLoadFormatD16Xyz extends Opcode("buffer_load_format_d16_xyz", 103)

  case object BufferLoadFormatD16Xyzw extends Opcode("buffer_load_format_d16_xyzw", 104)

  case object BufferLoadFormatX extends Opcode("buffer_load_format_x", 105)

  case object BufferLoadFormatXy extends Opcode("buffer_load_format_xy", 106)

  case object BufferLoadFormatXyz extends Opcode("buffer_load_format_xyz", 107)

  case object BufferLoadFormatXyzw extends Opcode("buffer_load_format_xyzw", 108)

  case object BufferLoadI16 extends Opcode("buffer_load_i16", 109)

  case object BufferLoadI8 extends Opcode("buffer_load_i8", 110)

  case object BufferLoadSbyte extends Opcode("buffer_load_sbyte", 111)

  case object BufferLoadSbyteD16 extends Opcode("buffer_load_sbyte_d16", 112)

  case object BufferLoadSbyteD16Hi extends Opcode("buffer_load_sbyte_d16_hi", 113)

  case object BufferLoadShortD16 extends Opcode("buffer_load_short_d16", 114)

  case object BufferLoadShortD16Hi extends Opcode("buffer_load_short_d16_hi", 115)

  case object BufferLoadSshort extends Opcode("buffer_load_sshort", 116)

  case object BufferLoadU16 extends Opcode("buffer_load_u16", 117)

  case object BufferLoadU8 extends Opcode("buffer_load_u8", 118)

  case object BufferLoadUbyte extends Opcode("buffer_load_ubyte", 119)

  case object BufferLoadUbyteD16 extends Opcode("buffer_load_ubyte_d16", 120)

  case object BufferLoadUbyteD16Hi extends Opcode("buffer_load_ubyte_d16_hi", 121)

  case object BufferLoadUshort extends Opcode("buffer_load_ushort", 122)

  case object BufferStoreB128 extends Opcode("buffer_store_b128", 123)

  case object BufferStoreB16 extends Opcode("buffer_store_b16", 124)

  case object BufferStoreB32 extends Opcode("buffer_store_b32", 125)

  case object BufferStoreB64 extends Opcode("buffer_store_b64", 126)

  case object BufferStoreB8 extends Opcode("buffer_store_b8", 127)

  case object BufferStoreB96 extends Opcode("buffer_store_b96", 128)

  case object BufferStoreByte extends Opcode("buffer_store_byte", 129)

  case object BufferStoreByteD16Hi extends Opcode("buffer_store_byte_d16_hi", 130)

  case object BufferStoreD16FormatX extends Opcode("buffer_store_d16_format_x", 131)

  case object BufferStoreD16FormatXy extends Opcode("buffer_store_d16_format_xy", 132)

  case object BufferStoreD16FormatXyz extends Opcode("buffer_store_d16_format_xyz", 133)

  case object BufferStoreD16FormatXyzw extends Opcode("buffer_store_d16_format_xyzw", 134)

  case object BufferStoreD16HiB16 extends Opcode("buffer_store_d16_hi_b16", 135)

  case object BufferStoreD16HiB8 extends Opcode("buffer_store_d16_hi_b8", 136)

  case object BufferStoreD16HiFormatX extends Opcode("buffer_store_d16_hi_format_x", 137)

  case object BufferStoreDword extends Opcode("buffer_store_dword", 138)

  case object BufferStoreDwordx2 extends Opcode("buffer_store_dwordx2", 139)

  case object BufferStoreDwordx3 extends Opcode("buffer_store_dwordx3", 140)

  case object BufferStoreDwordx4 extends Opcode("buffer_store_dwordx4", 141)

  case object BufferStoreFormatD16HiX extends Opcode("buffer_store_format_d16_hi_x", 142)

  case object BufferStoreFormatD16X extends Opcode("buffer_store_format_d16_x", 143)

  case object BufferStoreFormatD16Xy extends Opcode("buffer_store_format_d16_xy", 144)

  case object BufferStoreFormatD16Xyz extends Opcode("buffer_store_format_d16_xyz", 145)

  case object BufferStoreFormatD16Xyzw extends Opcode("buffer_store_format_d16_xyzw", 146)

  case object BufferStoreFormatX extends Opcode("buffer_store_format_x", 147)

  case object BufferStoreFormatXy extends Opcode("buffer_store_format_xy", 148)

  case object BufferStoreFormatXyz extends Opcode("buffer_store_format_xyz", 149)

  case object BufferStoreFormatXyzw extends Opcode("buffer_store_format_xyzw", 150)

  case object BufferStoreLdsDword extends Opcode("buffer_store_lds_dword", 151)

  case object BufferStoreShort extends Opcode("buffer_store_short", 152)

  case object BufferStoreShortD16Hi extends Opcode("buffer_store_short_d16_hi", 153)

  case object BufferWbinvl1 extends Opcode("buffer_wbinvl1", 154)

  case object BufferWbinvl1Sc extends Opcode("buffer_wbinvl1_sc", 155)

  case object BufferWbinvl1Vol extends Opcode("buffer_wbinvl1_vol", 156)

  case object BufferWbl2 extends Opcode("buffer_wbl2", 157)

  case object TbufferLoadD16FormatX extends Opcode("tbuffer_load_d16_format_x", 1337)

  case object TbufferLoadD16FormatXy extends Opcode("tbuffer_load_d16_format_xy", 1338)

  case object TbufferLoadD16FormatXyz extends Opcode("tbuffer_load_d16_format_xyz", 1339)

  case object TbufferLoadD16FormatXyzw extends Opcode("tbuffer_load_d16_format_xyzw", 1340)

  case object TbufferLoadFormatD16X extends Opcode("tbuffer_load_format_d16_x", 1341)

  case object TbufferLoadFormatD16Xy extends Opcode("tbuffer_load_format_d16_xy", 1342)

  case object TbufferLoadFormatD16Xyz extends Opcode("tbuffer_load_format_d16_xyz", 1343)

  case object TbufferLoadFormatD16Xyzw extends Opcode("tbuffer_load_format_d16_xyzw", 1344)

  case object TbufferLoadFormatX extends Opcode("tbuffer_load_format_x", 1345)

  case object TbufferLoadFormatXy extends Opcode("tbuffer_load_format_xy", 1346)

  case object TbufferLoadFormatXyz extends Opcode("tbuffer_load_format_xyz", 1347)

  case object TbufferLoadFormatXyzw extends Opcode("tbuffer_load_format_xyzw", 1348)

  case object TbufferStoreD16FormatX extends Opcode("tbuffer_store_d16_format_x", 1349)

  case object TbufferStoreD16FormatXy extends Opcode("tbuffer_store_d16_format_xy", 1350)

  case object TbufferStoreD16FormatXyz extends Opcode("tbuffer_store_d16_format_xyz", 1351)

  case object TbufferStoreD16FormatXyzw extends Opcode("tbuffer_store_d16_format_xyzw", 1352)

  case object TbufferStoreFormatD16X extends Opcode("tbuffer_store_format_d16_x", 1353)

  case object TbufferStoreFormatD16Xy extends Opcode("tbuffer_store_format_d16_xy", 1354)

  case object TbufferStoreFormatD16Xyz extends Opcode("tbuffer_store_format_d16_xyz", 1355)

  case object TbufferStoreFormatD16Xyzw extends Opcode("tbuffer_store_format_d16_xyzw", 1356)

  case object TbufferStoreFormatX extends Opcode("tbuffer_store_format_x", 1357)

  case object TbufferStoreFormatXy extends Opcode("tbuffer_store_format_xy", 1358)

  case object TbufferStoreFormatXyz extends Opcode("tbuffer_store_format_xyz", 1359)

  case object TbufferStoreFormatXyzw extends Opcode("tbuffer_store_format_xyzw", 1360)

  val definitions: Vec[Opcode] = Vec(
    BufferAtomicAdd,
    BufferAtomicAddF32,
    BufferAtomicAddF64,
    BufferAtomicAddU32,
    BufferAtomicAddU64,
    BufferAtomicAddX2,
    BufferAtomicAnd,
    BufferAtomicAndB32,
    BufferAtomicAndB64,
    BufferAtomicAndX2,
    BufferAtomicCmpswap,
    BufferAtomicCmpswapB32,
    BufferAtomicCmpswapB64,
    BufferAtomicCmpswapF32,
    BufferAtomicCmpswapX2,
    BufferAtomicCondSubU32,
    BufferAtomicCsub,
    BufferAtomicCsubU32,
    BufferAtomicDec,
    BufferAtomicDecU32,
    BufferAtomicDecU64,
    BufferAtomicDecX2,
    BufferAtomicFcmpswap,
    BufferAtomicFcmpswapX2,
    BufferAtomicFmax,
    BufferAtomicFmaxX2,
    BufferAtomicFmin,
    BufferAtomicFminX2,
    BufferAtomicInc,
    BufferAtomicIncU32,
    BufferAtomicIncU64,
    BufferAtomicIncX2,
    BufferAtomicMaxF32,
    BufferAtomicMaxF64,
    BufferAtomicMaxI32,
    BufferAtomicMaxI64,
    BufferAtomicMaxNumF32,
    BufferAtomicMaxNumF64,
    BufferAtomicMaxU32,
    BufferAtomicMaxU64,
    BufferAtomicMinF32,
    BufferAtomicMinF64,
    BufferAtomicMinI32,
    BufferAtomicMinI64,
    BufferAtomicMinNumF32,
    BufferAtomicMinNumF64,
    BufferAtomicMinU32,
    BufferAtomicMinU64,
    BufferAtomicOr,
    BufferAtomicOrB32,
    BufferAtomicOrB64,
    BufferAtomicOrX2,
    BufferAtomicPkAddBf16,
    BufferAtomicPkAddF16,
    BufferAtomicRsub,
    BufferAtomicRsubX2,
    BufferAtomicSmax,
    BufferAtomicSmaxX2,
    BufferAtomicSmin,
    BufferAtomicSminX2,
    BufferAtomicSub,
    BufferAtomicSubClampU32,
    BufferAtomicSubU32,
    BufferAtomicSubU64,
    BufferAtomicSubX2,
    BufferAtomicSwap,
    BufferAtomicSwapB32,
    BufferAtomicSwapB64,
    BufferAtomicSwapX2,
    BufferAtomicUmax,
    BufferAtomicUmaxX2,
    BufferAtomicUmin,
    BufferAtomicUminX2,
    BufferAtomicXor,
    BufferAtomicXorB32,
    BufferAtomicXorB64,
    BufferAtomicXorX2,
    BufferGl0Inv,
    BufferGl1Inv,
    BufferInv,
    BufferInvl2,
    BufferLoadB128,
    BufferLoadB32,
    BufferLoadB64,
    BufferLoadB96,
    BufferLoadD16B16,
    BufferLoadD16FormatX,
    BufferLoadD16FormatXy,
    BufferLoadD16FormatXyz,
    BufferLoadD16FormatXyzw,
    BufferLoadD16HiB16,
    BufferLoadD16HiFormatX,
    BufferLoadD16HiI8,
    BufferLoadD16HiU8,
    BufferLoadD16I8,
    BufferLoadD16U8,
    BufferLoadDword,
    BufferLoadDwordx2,
    BufferLoadDwordx3,
    BufferLoadDwordx4,
    BufferLoadFormatD16HiX,
    BufferLoadFormatD16X,
    BufferLoadFormatD16Xy,
    BufferLoadFormatD16Xyz,
    BufferLoadFormatD16Xyzw,
    BufferLoadFormatX,
    BufferLoadFormatXy,
    BufferLoadFormatXyz,
    BufferLoadFormatXyzw,
    BufferLoadI16,
    BufferLoadI8,
    BufferLoadSbyte,
    BufferLoadSbyteD16,
    BufferLoadSbyteD16Hi,
    BufferLoadShortD16,
    BufferLoadShortD16Hi,
    BufferLoadSshort,
    BufferLoadU16,
    BufferLoadU8,
    BufferLoadUbyte,
    BufferLoadUbyteD16,
    BufferLoadUbyteD16Hi,
    BufferLoadUshort,
    BufferStoreB128,
    BufferStoreB16,
    BufferStoreB32,
    BufferStoreB64,
    BufferStoreB8,
    BufferStoreB96,
    BufferStoreByte,
    BufferStoreByteD16Hi,
    BufferStoreD16FormatX,
    BufferStoreD16FormatXy,
    BufferStoreD16FormatXyz,
    BufferStoreD16FormatXyzw,
    BufferStoreD16HiB16,
    BufferStoreD16HiB8,
    BufferStoreD16HiFormatX,
    BufferStoreDword,
    BufferStoreDwordx2,
    BufferStoreDwordx3,
    BufferStoreDwordx4,
    BufferStoreFormatD16HiX,
    BufferStoreFormatD16X,
    BufferStoreFormatD16Xy,
    BufferStoreFormatD16Xyz,
    BufferStoreFormatD16Xyzw,
    BufferStoreFormatX,
    BufferStoreFormatXy,
    BufferStoreFormatXyz,
    BufferStoreFormatXyzw,
    BufferStoreLdsDword,
    BufferStoreShort,
    BufferStoreShortD16Hi,
    BufferWbinvl1,
    BufferWbinvl1Sc,
    BufferWbinvl1Vol,
    BufferWbl2,
    TbufferLoadD16FormatX,
    TbufferLoadD16FormatXy,
    TbufferLoadD16FormatXyz,
    TbufferLoadD16FormatXyzw,
    TbufferLoadFormatD16X,
    TbufferLoadFormatD16Xy,
    TbufferLoadFormatD16Xyz,
    TbufferLoadFormatD16Xyzw,
    TbufferLoadFormatX,
    TbufferLoadFormatXy,
    TbufferLoadFormatXyz,
    TbufferLoadFormatXyzw,
    TbufferStoreD16FormatX,
    TbufferStoreD16FormatXy,
    TbufferStoreD16FormatXyz,
    TbufferStoreD16FormatXyzw,
    TbufferStoreFormatD16X,
    TbufferStoreFormatD16Xy,
    TbufferStoreFormatD16Xyz,
    TbufferStoreFormatD16Xyzw,
    TbufferStoreFormatX,
    TbufferStoreFormatXy,
    TbufferStoreFormatXyz,
    TbufferStoreFormatXyzw
  )
}
