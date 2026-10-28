// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu

import javax.annotation.processing.Generated

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.collection.*

@Generated(Array("sbt codegen"))
private object ScalarMemoryInstructionDefs {
  case object SAtcProbe extends Opcode("s_atc_probe", 869)

  case object SAtcProbeBuffer extends Opcode("s_atc_probe_buffer", 870)

  case object SAtomicAdd extends Opcode("s_atomic_add", 871)

  case object SAtomicAddX2 extends Opcode("s_atomic_add_x2", 872)

  case object SAtomicAnd extends Opcode("s_atomic_and", 873)

  case object SAtomicAndX2 extends Opcode("s_atomic_and_x2", 874)

  case object SAtomicCmpswap extends Opcode("s_atomic_cmpswap", 875)

  case object SAtomicCmpswapX2 extends Opcode("s_atomic_cmpswap_x2", 876)

  case object SAtomicDec extends Opcode("s_atomic_dec", 877)

  case object SAtomicDecX2 extends Opcode("s_atomic_dec_x2", 878)

  case object SAtomicInc extends Opcode("s_atomic_inc", 879)

  case object SAtomicIncX2 extends Opcode("s_atomic_inc_x2", 880)

  case object SAtomicOr extends Opcode("s_atomic_or", 881)

  case object SAtomicOrX2 extends Opcode("s_atomic_or_x2", 882)

  case object SAtomicSmax extends Opcode("s_atomic_smax", 883)

  case object SAtomicSmaxX2 extends Opcode("s_atomic_smax_x2", 884)

  case object SAtomicSmin extends Opcode("s_atomic_smin", 885)

  case object SAtomicSminX2 extends Opcode("s_atomic_smin_x2", 886)

  case object SAtomicSub extends Opcode("s_atomic_sub", 887)

  case object SAtomicSubX2 extends Opcode("s_atomic_sub_x2", 888)

  case object SAtomicSwap extends Opcode("s_atomic_swap", 889)

  case object SAtomicSwapX2 extends Opcode("s_atomic_swap_x2", 890)

  case object SAtomicUmax extends Opcode("s_atomic_umax", 891)

  case object SAtomicUmaxX2 extends Opcode("s_atomic_umax_x2", 892)

  case object SAtomicUmin extends Opcode("s_atomic_umin", 893)

  case object SAtomicUminX2 extends Opcode("s_atomic_umin_x2", 894)

  case object SAtomicXor extends Opcode("s_atomic_xor", 895)

  case object SAtomicXorX2 extends Opcode("s_atomic_xor_x2", 896)

  case object SBufferAtomicAdd extends Opcode("s_buffer_atomic_add", 926)

  case object SBufferAtomicAddX2 extends Opcode("s_buffer_atomic_add_x2", 927)

  case object SBufferAtomicAnd extends Opcode("s_buffer_atomic_and", 928)

  case object SBufferAtomicAndX2 extends Opcode("s_buffer_atomic_and_x2", 929)

  case object SBufferAtomicCmpswap extends Opcode("s_buffer_atomic_cmpswap", 930)

  case object SBufferAtomicCmpswapX2 extends Opcode("s_buffer_atomic_cmpswap_x2", 931)

  case object SBufferAtomicDec extends Opcode("s_buffer_atomic_dec", 932)

  case object SBufferAtomicDecX2 extends Opcode("s_buffer_atomic_dec_x2", 933)

  case object SBufferAtomicInc extends Opcode("s_buffer_atomic_inc", 934)

  case object SBufferAtomicIncX2 extends Opcode("s_buffer_atomic_inc_x2", 935)

  case object SBufferAtomicOr extends Opcode("s_buffer_atomic_or", 936)

  case object SBufferAtomicOrX2 extends Opcode("s_buffer_atomic_or_x2", 937)

  case object SBufferAtomicSmax extends Opcode("s_buffer_atomic_smax", 938)

  case object SBufferAtomicSmaxX2 extends Opcode("s_buffer_atomic_smax_x2", 939)

  case object SBufferAtomicSmin extends Opcode("s_buffer_atomic_smin", 940)

  case object SBufferAtomicSminX2 extends Opcode("s_buffer_atomic_smin_x2", 941)

  case object SBufferAtomicSub extends Opcode("s_buffer_atomic_sub", 942)

  case object SBufferAtomicSubX2 extends Opcode("s_buffer_atomic_sub_x2", 943)

  case object SBufferAtomicSwap extends Opcode("s_buffer_atomic_swap", 944)

  case object SBufferAtomicSwapX2 extends Opcode("s_buffer_atomic_swap_x2", 945)

  case object SBufferAtomicUmax extends Opcode("s_buffer_atomic_umax", 946)

  case object SBufferAtomicUmaxX2 extends Opcode("s_buffer_atomic_umax_x2", 947)

  case object SBufferAtomicUmin extends Opcode("s_buffer_atomic_umin", 948)

  case object SBufferAtomicUminX2 extends Opcode("s_buffer_atomic_umin_x2", 949)

  case object SBufferAtomicXor extends Opcode("s_buffer_atomic_xor", 950)

  case object SBufferAtomicXorX2 extends Opcode("s_buffer_atomic_xor_x2", 951)

  case object SBufferLoadB128 extends Opcode("s_buffer_load_b128", 952)

  case object SBufferLoadB256 extends Opcode("s_buffer_load_b256", 953)

  case object SBufferLoadB32 extends Opcode("s_buffer_load_b32", 954)

  case object SBufferLoadB512 extends Opcode("s_buffer_load_b512", 955)

  case object SBufferLoadB64 extends Opcode("s_buffer_load_b64", 956)

  case object SBufferLoadB96 extends Opcode("s_buffer_load_b96", 957)

  case object SBufferLoadDword extends Opcode("s_buffer_load_dword", 958)

  case object SBufferLoadDwordx16 extends Opcode("s_buffer_load_dwordx16", 959)

  case object SBufferLoadDwordx2 extends Opcode("s_buffer_load_dwordx2", 960)

  case object SBufferLoadDwordx4 extends Opcode("s_buffer_load_dwordx4", 961)

  case object SBufferLoadDwordx8 extends Opcode("s_buffer_load_dwordx8", 962)

  case object SBufferLoadI16 extends Opcode("s_buffer_load_i16", 963)

  case object SBufferLoadI8 extends Opcode("s_buffer_load_i8", 964)

  case object SBufferLoadU16 extends Opcode("s_buffer_load_u16", 965)

  case object SBufferLoadU8 extends Opcode("s_buffer_load_u8", 966)

  case object SBufferPrefetchData extends Opcode("s_buffer_prefetch_data", 967)

  case object SBufferStoreDword extends Opcode("s_buffer_store_dword", 968)

  case object SBufferStoreDwordx2 extends Opcode("s_buffer_store_dwordx2", 969)

  case object SBufferStoreDwordx4 extends Opcode("s_buffer_store_dwordx4", 970)

  case object SDcacheDiscard extends Opcode("s_dcache_discard", 1063)

  case object SDcacheDiscardX2 extends Opcode("s_dcache_discard_x2", 1064)

  case object SDcacheInv extends Opcode("s_dcache_inv", 1065)

  case object SDcacheInvVol extends Opcode("s_dcache_inv_vol", 1066)

  case object SDcacheWb extends Opcode("s_dcache_wb", 1067)

  case object SDcacheWbVol extends Opcode("s_dcache_wb_vol", 1068)

  case object SGetWaveidInWorkgroup extends Opcode("s_get_waveid_in_workgroup", 1092)

  case object SGl1Inv extends Opcode("s_gl1_inv", 1095)

  case object SLoadB128 extends Opcode("s_load_b128", 1099)

  case object SLoadB256 extends Opcode("s_load_b256", 1100)

  case object SLoadB32 extends Opcode("s_load_b32", 1101)

  case object SLoadB512 extends Opcode("s_load_b512", 1102)

  case object SLoadB64 extends Opcode("s_load_b64", 1103)

  case object SLoadB96 extends Opcode("s_load_b96", 1104)

  case object SLoadDword extends Opcode("s_load_dword", 1105)

  case object SLoadDwordx16 extends Opcode("s_load_dwordx16", 1106)

  case object SLoadDwordx2 extends Opcode("s_load_dwordx2", 1107)

  case object SLoadDwordx4 extends Opcode("s_load_dwordx4", 1108)

  case object SLoadDwordx8 extends Opcode("s_load_dwordx8", 1109)

  case object SLoadI16 extends Opcode("s_load_i16", 1110)

  case object SLoadI8 extends Opcode("s_load_i8", 1111)

  case object SLoadU16 extends Opcode("s_load_u16", 1112)

  case object SLoadU8 extends Opcode("s_load_u8", 1113)

  case object SMemrealtime extends Opcode("s_memrealtime", 1130)

  case object SMemtime extends Opcode("s_memtime", 1131)

  case object SPrefetchData extends Opcode("s_prefetch_data", 1188)

  case object SPrefetchDataPcRel extends Opcode("s_prefetch_data_pc_rel", 1189)

  case object SPrefetchInst extends Opcode("s_prefetch_inst", 1190)

  case object SPrefetchInstPcRel extends Opcode("s_prefetch_inst_pc_rel", 1191)

  case object SScratchLoadDword extends Opcode("s_scratch_load_dword", 1200)

  case object SScratchLoadDwordx2 extends Opcode("s_scratch_load_dwordx2", 1201)

  case object SScratchLoadDwordx4 extends Opcode("s_scratch_load_dwordx4", 1202)

  case object SScratchStoreDword extends Opcode("s_scratch_store_dword", 1203)

  case object SScratchStoreDwordx2 extends Opcode("s_scratch_store_dwordx2", 1204)

  case object SScratchStoreDwordx4 extends Opcode("s_scratch_store_dwordx4", 1205)

  case object SStoreDword extends Opcode("s_store_dword", 1231)

  case object SStoreDwordx2 extends Opcode("s_store_dwordx2", 1232)

  case object SStoreDwordx4 extends Opcode("s_store_dwordx4", 1233)

  val definitions: Vec[Opcode] = Vec(
    SAtcProbe,
    SAtcProbeBuffer,
    SAtomicAdd,
    SAtomicAddX2,
    SAtomicAnd,
    SAtomicAndX2,
    SAtomicCmpswap,
    SAtomicCmpswapX2,
    SAtomicDec,
    SAtomicDecX2,
    SAtomicInc,
    SAtomicIncX2,
    SAtomicOr,
    SAtomicOrX2,
    SAtomicSmax,
    SAtomicSmaxX2,
    SAtomicSmin,
    SAtomicSminX2,
    SAtomicSub,
    SAtomicSubX2,
    SAtomicSwap,
    SAtomicSwapX2,
    SAtomicUmax,
    SAtomicUmaxX2,
    SAtomicUmin,
    SAtomicUminX2,
    SAtomicXor,
    SAtomicXorX2,
    SBufferAtomicAdd,
    SBufferAtomicAddX2,
    SBufferAtomicAnd,
    SBufferAtomicAndX2,
    SBufferAtomicCmpswap,
    SBufferAtomicCmpswapX2,
    SBufferAtomicDec,
    SBufferAtomicDecX2,
    SBufferAtomicInc,
    SBufferAtomicIncX2,
    SBufferAtomicOr,
    SBufferAtomicOrX2,
    SBufferAtomicSmax,
    SBufferAtomicSmaxX2,
    SBufferAtomicSmin,
    SBufferAtomicSminX2,
    SBufferAtomicSub,
    SBufferAtomicSubX2,
    SBufferAtomicSwap,
    SBufferAtomicSwapX2,
    SBufferAtomicUmax,
    SBufferAtomicUmaxX2,
    SBufferAtomicUmin,
    SBufferAtomicUminX2,
    SBufferAtomicXor,
    SBufferAtomicXorX2,
    SBufferLoadB128,
    SBufferLoadB256,
    SBufferLoadB32,
    SBufferLoadB512,
    SBufferLoadB64,
    SBufferLoadB96,
    SBufferLoadDword,
    SBufferLoadDwordx16,
    SBufferLoadDwordx2,
    SBufferLoadDwordx4,
    SBufferLoadDwordx8,
    SBufferLoadI16,
    SBufferLoadI8,
    SBufferLoadU16,
    SBufferLoadU8,
    SBufferPrefetchData,
    SBufferStoreDword,
    SBufferStoreDwordx2,
    SBufferStoreDwordx4,
    SDcacheDiscard,
    SDcacheDiscardX2,
    SDcacheInv,
    SDcacheInvVol,
    SDcacheWb,
    SDcacheWbVol,
    SGetWaveidInWorkgroup,
    SGl1Inv,
    SLoadB128,
    SLoadB256,
    SLoadB32,
    SLoadB512,
    SLoadB64,
    SLoadB96,
    SLoadDword,
    SLoadDwordx16,
    SLoadDwordx2,
    SLoadDwordx4,
    SLoadDwordx8,
    SLoadI16,
    SLoadI8,
    SLoadU16,
    SLoadU8,
    SMemrealtime,
    SMemtime,
    SPrefetchData,
    SPrefetchDataPcRel,
    SPrefetchInst,
    SPrefetchInstPcRel,
    SScratchLoadDword,
    SScratchLoadDwordx2,
    SScratchLoadDwordx4,
    SScratchStoreDword,
    SScratchStoreDwordx2,
    SScratchStoreDwordx4,
    SStoreDword,
    SStoreDwordx2,
    SStoreDwordx4
  )
}
