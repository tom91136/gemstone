// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Regenerate with: sbt codegen
// scalafix:off GemstoneNoAssume; values were validated by the generator
package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Vec
import Instruction.*
import Program.*

@javax.annotation.processing.Generated(Array("sbt codegen"))
private[amdgpu] object ScalarMemoryInstructionDefs {
  case object SAtcProbe extends Definition(
        Opcode.assume("s_atc_probe"),
        decoder(form(3)((operands, mods) => Instruction.SAtcProbe(operands(0), operands(1), operands(2), mods))),
        868
      )
  case object SAtcProbeBuffer extends Definition(
        Opcode.assume("s_atc_probe_buffer"),
        decoder(form(3)((operands, mods) => Instruction.SAtcProbeBuffer(operands(0), operands(1), operands(2), mods))),
        869
      )
  case object SAtomicAdd extends Definition(
        Opcode.assume("s_atomic_add"),
        decoder(form(3)((operands, mods) => Instruction.SAtomicAdd(operands(0), operands(1), operands(2), mods))),
        870
      )
  case object SAtomicAddX2 extends Definition(
        Opcode.assume("s_atomic_add_x2"),
        decoder(form(3)((operands, mods) => Instruction.SAtomicAddX2(operands(0), operands(1), operands(2), mods))),
        871
      )
  case object SAtomicAnd extends Definition(
        Opcode.assume("s_atomic_and"),
        decoder(form(3)((operands, mods) => Instruction.SAtomicAnd(operands(0), operands(1), operands(2), mods))),
        872
      )
  case object SAtomicAndX2 extends Definition(
        Opcode.assume("s_atomic_and_x2"),
        decoder(form(3)((operands, mods) => Instruction.SAtomicAndX2(operands(0), operands(1), operands(2), mods))),
        873
      )
  case object SAtomicCmpswap extends Definition(
        Opcode.assume("s_atomic_cmpswap"),
        decoder(form(3)((operands, mods) => Instruction.SAtomicCmpswap(operands(0), operands(1), operands(2), mods))),
        874
      )
  case object SAtomicCmpswapX2 extends Definition(
        Opcode.assume("s_atomic_cmpswap_x2"),
        decoder(form(3)((operands, mods) => Instruction.SAtomicCmpswapX2(operands(0), operands(1), operands(2), mods))),
        875
      )
  case object SAtomicDec extends Definition(
        Opcode.assume("s_atomic_dec"),
        decoder(form(3)((operands, mods) => Instruction.SAtomicDec(operands(0), operands(1), operands(2), mods))),
        876
      )
  case object SAtomicDecX2 extends Definition(
        Opcode.assume("s_atomic_dec_x2"),
        decoder(form(3)((operands, mods) => Instruction.SAtomicDecX2(operands(0), operands(1), operands(2), mods))),
        877
      )
  case object SAtomicInc extends Definition(
        Opcode.assume("s_atomic_inc"),
        decoder(form(3)((operands, mods) => Instruction.SAtomicInc(operands(0), operands(1), operands(2), mods))),
        878
      )
  case object SAtomicIncX2 extends Definition(
        Opcode.assume("s_atomic_inc_x2"),
        decoder(form(3)((operands, mods) => Instruction.SAtomicIncX2(operands(0), operands(1), operands(2), mods))),
        879
      )
  case object SAtomicOr extends Definition(
        Opcode.assume("s_atomic_or"),
        decoder(form(3)((operands, mods) => Instruction.SAtomicOr(operands(0), operands(1), operands(2), mods))),
        880
      )
  case object SAtomicOrX2 extends Definition(
        Opcode.assume("s_atomic_or_x2"),
        decoder(form(3)((operands, mods) => Instruction.SAtomicOrX2(operands(0), operands(1), operands(2), mods))),
        881
      )
  case object SAtomicSmax extends Definition(
        Opcode.assume("s_atomic_smax"),
        decoder(form(3)((operands, mods) => Instruction.SAtomicSmax(operands(0), operands(1), operands(2), mods))),
        882
      )
  case object SAtomicSmaxX2 extends Definition(
        Opcode.assume("s_atomic_smax_x2"),
        decoder(form(3)((operands, mods) => Instruction.SAtomicSmaxX2(operands(0), operands(1), operands(2), mods))),
        883
      )
  case object SAtomicSmin extends Definition(
        Opcode.assume("s_atomic_smin"),
        decoder(form(3)((operands, mods) => Instruction.SAtomicSmin(operands(0), operands(1), operands(2), mods))),
        884
      )
  case object SAtomicSminX2 extends Definition(
        Opcode.assume("s_atomic_smin_x2"),
        decoder(form(3)((operands, mods) => Instruction.SAtomicSminX2(operands(0), operands(1), operands(2), mods))),
        885
      )
  case object SAtomicSub extends Definition(
        Opcode.assume("s_atomic_sub"),
        decoder(form(3)((operands, mods) => Instruction.SAtomicSub(operands(0), operands(1), operands(2), mods))),
        886
      )
  case object SAtomicSubX2 extends Definition(
        Opcode.assume("s_atomic_sub_x2"),
        decoder(form(3)((operands, mods) => Instruction.SAtomicSubX2(operands(0), operands(1), operands(2), mods))),
        887
      )
  case object SAtomicSwap extends Definition(
        Opcode.assume("s_atomic_swap"),
        decoder(form(3)((operands, mods) => Instruction.SAtomicSwap(operands(0), operands(1), operands(2), mods))),
        888
      )
  case object SAtomicSwapX2 extends Definition(
        Opcode.assume("s_atomic_swap_x2"),
        decoder(form(3)((operands, mods) => Instruction.SAtomicSwapX2(operands(0), operands(1), operands(2), mods))),
        889
      )
  case object SAtomicUmax extends Definition(
        Opcode.assume("s_atomic_umax"),
        decoder(form(3)((operands, mods) => Instruction.SAtomicUmax(operands(0), operands(1), operands(2), mods))),
        890
      )
  case object SAtomicUmaxX2 extends Definition(
        Opcode.assume("s_atomic_umax_x2"),
        decoder(form(3)((operands, mods) => Instruction.SAtomicUmaxX2(operands(0), operands(1), operands(2), mods))),
        891
      )
  case object SAtomicUmin extends Definition(
        Opcode.assume("s_atomic_umin"),
        decoder(form(3)((operands, mods) => Instruction.SAtomicUmin(operands(0), operands(1), operands(2), mods))),
        892
      )
  case object SAtomicUminX2 extends Definition(
        Opcode.assume("s_atomic_umin_x2"),
        decoder(form(3)((operands, mods) => Instruction.SAtomicUminX2(operands(0), operands(1), operands(2), mods))),
        893
      )
  case object SAtomicXor extends Definition(
        Opcode.assume("s_atomic_xor"),
        decoder(form(3)((operands, mods) => Instruction.SAtomicXor(operands(0), operands(1), operands(2), mods))),
        894
      )
  case object SAtomicXorX2 extends Definition(
        Opcode.assume("s_atomic_xor_x2"),
        decoder(form(3)((operands, mods) => Instruction.SAtomicXorX2(operands(0), operands(1), operands(2), mods))),
        895
      )
  case object SBufferAtomicAdd extends Definition(
        Opcode.assume("s_buffer_atomic_add"),
        decoder(form(3)((operands, mods) => Instruction.SBufferAtomicAdd(operands(0), operands(1), operands(2), mods))),
        925
      )
  case object SBufferAtomicAddX2 extends Definition(
        Opcode.assume("s_buffer_atomic_add_x2"),
        decoder(form(3)((operands, mods) =>
          Instruction.SBufferAtomicAddX2(operands(0), operands(1), operands(2), mods)
        )),
        926
      )
  case object SBufferAtomicAnd extends Definition(
        Opcode.assume("s_buffer_atomic_and"),
        decoder(form(3)((operands, mods) => Instruction.SBufferAtomicAnd(operands(0), operands(1), operands(2), mods))),
        927
      )
  case object SBufferAtomicAndX2 extends Definition(
        Opcode.assume("s_buffer_atomic_and_x2"),
        decoder(form(3)((operands, mods) =>
          Instruction.SBufferAtomicAndX2(operands(0), operands(1), operands(2), mods)
        )),
        928
      )
  case object SBufferAtomicCmpswap extends Definition(
        Opcode.assume("s_buffer_atomic_cmpswap"),
        decoder(form(3)((operands, mods) =>
          Instruction.SBufferAtomicCmpswap(operands(0), operands(1), operands(2), mods)
        )),
        929
      )
  case object SBufferAtomicCmpswapX2 extends Definition(
        Opcode.assume("s_buffer_atomic_cmpswap_x2"),
        decoder(form(3)((operands, mods) =>
          Instruction.SBufferAtomicCmpswapX2(operands(0), operands(1), operands(2), mods)
        )),
        930
      )
  case object SBufferAtomicDec extends Definition(
        Opcode.assume("s_buffer_atomic_dec"),
        decoder(form(3)((operands, mods) => Instruction.SBufferAtomicDec(operands(0), operands(1), operands(2), mods))),
        931
      )
  case object SBufferAtomicDecX2 extends Definition(
        Opcode.assume("s_buffer_atomic_dec_x2"),
        decoder(form(3)((operands, mods) =>
          Instruction.SBufferAtomicDecX2(operands(0), operands(1), operands(2), mods)
        )),
        932
      )
  case object SBufferAtomicInc extends Definition(
        Opcode.assume("s_buffer_atomic_inc"),
        decoder(form(3)((operands, mods) => Instruction.SBufferAtomicInc(operands(0), operands(1), operands(2), mods))),
        933
      )
  case object SBufferAtomicIncX2 extends Definition(
        Opcode.assume("s_buffer_atomic_inc_x2"),
        decoder(form(3)((operands, mods) =>
          Instruction.SBufferAtomicIncX2(operands(0), operands(1), operands(2), mods)
        )),
        934
      )
  case object SBufferAtomicOr extends Definition(
        Opcode.assume("s_buffer_atomic_or"),
        decoder(form(3)((operands, mods) => Instruction.SBufferAtomicOr(operands(0), operands(1), operands(2), mods))),
        935
      )
  case object SBufferAtomicOrX2 extends Definition(
        Opcode.assume("s_buffer_atomic_or_x2"),
        decoder(form(3)((operands, mods) =>
          Instruction.SBufferAtomicOrX2(operands(0), operands(1), operands(2), mods)
        )),
        936
      )
  case object SBufferAtomicSmax extends Definition(
        Opcode.assume("s_buffer_atomic_smax"),
        decoder(form(3)((operands, mods) =>
          Instruction.SBufferAtomicSmax(operands(0), operands(1), operands(2), mods)
        )),
        937
      )
  case object SBufferAtomicSmaxX2 extends Definition(
        Opcode.assume("s_buffer_atomic_smax_x2"),
        decoder(form(3)((operands, mods) =>
          Instruction.SBufferAtomicSmaxX2(operands(0), operands(1), operands(2), mods)
        )),
        938
      )
  case object SBufferAtomicSmin extends Definition(
        Opcode.assume("s_buffer_atomic_smin"),
        decoder(form(3)((operands, mods) =>
          Instruction.SBufferAtomicSmin(operands(0), operands(1), operands(2), mods)
        )),
        939
      )
  case object SBufferAtomicSminX2 extends Definition(
        Opcode.assume("s_buffer_atomic_smin_x2"),
        decoder(form(3)((operands, mods) =>
          Instruction.SBufferAtomicSminX2(operands(0), operands(1), operands(2), mods)
        )),
        940
      )
  case object SBufferAtomicSub extends Definition(
        Opcode.assume("s_buffer_atomic_sub"),
        decoder(form(3)((operands, mods) => Instruction.SBufferAtomicSub(operands(0), operands(1), operands(2), mods))),
        941
      )
  case object SBufferAtomicSubX2 extends Definition(
        Opcode.assume("s_buffer_atomic_sub_x2"),
        decoder(form(3)((operands, mods) =>
          Instruction.SBufferAtomicSubX2(operands(0), operands(1), operands(2), mods)
        )),
        942
      )
  case object SBufferAtomicSwap extends Definition(
        Opcode.assume("s_buffer_atomic_swap"),
        decoder(form(3)((operands, mods) =>
          Instruction.SBufferAtomicSwap(operands(0), operands(1), operands(2), mods)
        )),
        943
      )
  case object SBufferAtomicSwapX2 extends Definition(
        Opcode.assume("s_buffer_atomic_swap_x2"),
        decoder(form(3)((operands, mods) =>
          Instruction.SBufferAtomicSwapX2(operands(0), operands(1), operands(2), mods)
        )),
        944
      )
  case object SBufferAtomicUmax extends Definition(
        Opcode.assume("s_buffer_atomic_umax"),
        decoder(form(3)((operands, mods) =>
          Instruction.SBufferAtomicUmax(operands(0), operands(1), operands(2), mods)
        )),
        945
      )
  case object SBufferAtomicUmaxX2 extends Definition(
        Opcode.assume("s_buffer_atomic_umax_x2"),
        decoder(form(3)((operands, mods) =>
          Instruction.SBufferAtomicUmaxX2(operands(0), operands(1), operands(2), mods)
        )),
        946
      )
  case object SBufferAtomicUmin extends Definition(
        Opcode.assume("s_buffer_atomic_umin"),
        decoder(form(3)((operands, mods) =>
          Instruction.SBufferAtomicUmin(operands(0), operands(1), operands(2), mods)
        )),
        947
      )
  case object SBufferAtomicUminX2 extends Definition(
        Opcode.assume("s_buffer_atomic_umin_x2"),
        decoder(form(3)((operands, mods) =>
          Instruction.SBufferAtomicUminX2(operands(0), operands(1), operands(2), mods)
        )),
        948
      )
  case object SBufferAtomicXor extends Definition(
        Opcode.assume("s_buffer_atomic_xor"),
        decoder(form(3)((operands, mods) => Instruction.SBufferAtomicXor(operands(0), operands(1), operands(2), mods))),
        949
      )
  case object SBufferAtomicXorX2 extends Definition(
        Opcode.assume("s_buffer_atomic_xor_x2"),
        decoder(form(3)((operands, mods) =>
          Instruction.SBufferAtomicXorX2(operands(0), operands(1), operands(2), mods)
        )),
        950
      )
  case object SBufferLoadB128 extends Definition(
        Opcode.assume("s_buffer_load_b128"),
        decoder(form(3)((operands, mods) => Instruction.SBufferLoadB128(operands(0), operands(1), operands(2), mods))),
        951
      )
  case object SBufferLoadB256 extends Definition(
        Opcode.assume("s_buffer_load_b256"),
        decoder(form(3)((operands, mods) => Instruction.SBufferLoadB256(operands(0), operands(1), operands(2), mods))),
        952
      )
  case object SBufferLoadB32 extends Definition(
        Opcode.assume("s_buffer_load_b32"),
        decoder(form(3)((operands, mods) => Instruction.SBufferLoadB32(operands(0), operands(1), operands(2), mods))),
        953
      )
  case object SBufferLoadB512 extends Definition(
        Opcode.assume("s_buffer_load_b512"),
        decoder(form(3)((operands, mods) => Instruction.SBufferLoadB512(operands(0), operands(1), operands(2), mods))),
        954
      )
  case object SBufferLoadB64 extends Definition(
        Opcode.assume("s_buffer_load_b64"),
        decoder(form(3)((operands, mods) => Instruction.SBufferLoadB64(operands(0), operands(1), operands(2), mods))),
        955
      )
  case object SBufferLoadB96 extends Definition(
        Opcode.assume("s_buffer_load_b96"),
        decoder(form(3)((operands, mods) => Instruction.SBufferLoadB96(operands(0), operands(1), operands(2), mods))),
        956
      )
  case object SBufferLoadDword extends Definition(
        Opcode.assume("s_buffer_load_dword"),
        decoder(form(3)((operands, mods) => Instruction.SBufferLoadDword(operands(0), operands(1), operands(2), mods))),
        957
      )
  case object SBufferLoadDwordx16 extends Definition(
        Opcode.assume("s_buffer_load_dwordx16"),
        decoder(form(3)((operands, mods) =>
          Instruction.SBufferLoadDwordx16(operands(0), operands(1), operands(2), mods)
        )),
        958
      )
  case object SBufferLoadDwordx2 extends Definition(
        Opcode.assume("s_buffer_load_dwordx2"),
        decoder(form(3)((operands, mods) =>
          Instruction.SBufferLoadDwordx2(operands(0), operands(1), operands(2), mods)
        )),
        959
      )
  case object SBufferLoadDwordx4 extends Definition(
        Opcode.assume("s_buffer_load_dwordx4"),
        decoder(form(3)((operands, mods) =>
          Instruction.SBufferLoadDwordx4(operands(0), operands(1), operands(2), mods)
        )),
        960
      )
  case object SBufferLoadDwordx8 extends Definition(
        Opcode.assume("s_buffer_load_dwordx8"),
        decoder(form(3)((operands, mods) =>
          Instruction.SBufferLoadDwordx8(operands(0), operands(1), operands(2), mods)
        )),
        961
      )
  case object SBufferLoadI16 extends Definition(
        Opcode.assume("s_buffer_load_i16"),
        decoder(form(3)((operands, mods) => Instruction.SBufferLoadI16(operands(0), operands(1), operands(2), mods))),
        962
      )
  case object SBufferLoadI8 extends Definition(
        Opcode.assume("s_buffer_load_i8"),
        decoder(form(3)((operands, mods) => Instruction.SBufferLoadI8(operands(0), operands(1), operands(2), mods))),
        963
      )
  case object SBufferLoadU16 extends Definition(
        Opcode.assume("s_buffer_load_u16"),
        decoder(form(3)((operands, mods) => Instruction.SBufferLoadU16(operands(0), operands(1), operands(2), mods))),
        964
      )
  case object SBufferLoadU8 extends Definition(
        Opcode.assume("s_buffer_load_u8"),
        decoder(form(3)((operands, mods) => Instruction.SBufferLoadU8(operands(0), operands(1), operands(2), mods))),
        965
      )
  case object SBufferPrefetchData extends Definition(
        Opcode.assume("s_buffer_prefetch_data"),
        decoder(form(4)((operands, mods) =>
          Instruction.SBufferPrefetchData(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        966
      )
  case object SBufferStoreDword extends Definition(
        Opcode.assume("s_buffer_store_dword"),
        decoder(form(3)((operands, mods) =>
          Instruction.SBufferStoreDword(operands(0), operands(1), operands(2), mods)
        )),
        967
      )
  case object SBufferStoreDwordx2 extends Definition(
        Opcode.assume("s_buffer_store_dwordx2"),
        decoder(form(3)((operands, mods) =>
          Instruction.SBufferStoreDwordx2(operands(0), operands(1), operands(2), mods)
        )),
        968
      )
  case object SBufferStoreDwordx4 extends Definition(
        Opcode.assume("s_buffer_store_dwordx4"),
        decoder(form(3)((operands, mods) =>
          Instruction.SBufferStoreDwordx4(operands(0), operands(1), operands(2), mods)
        )),
        969
      )
  case object SDcacheDiscard extends Definition(
        Opcode.assume("s_dcache_discard"),
        decoder(form(2)((operands, mods) => Instruction.SDcacheDiscard(operands(0), operands(1), mods))),
        1062
      )
  case object SDcacheDiscardX2 extends Definition(
        Opcode.assume("s_dcache_discard_x2"),
        decoder(form(2)((operands, mods) => Instruction.SDcacheDiscardX2(operands(0), operands(1), mods))),
        1063
      )
  case object SDcacheInv extends Definition(
        Opcode.assume("s_dcache_inv"),
        decoder(form(0)((_, mods) => Instruction.SDcacheInv(mods))),
        1064
      )
  case object SDcacheInvVol extends Definition(
        Opcode.assume("s_dcache_inv_vol"),
        decoder(form(0)((_, mods) => Instruction.SDcacheInvVol(mods))),
        1065
      )
  case object SDcacheWb extends Definition(
        Opcode.assume("s_dcache_wb"),
        decoder(form(0)((_, mods) => Instruction.SDcacheWb(mods))),
        1066
      )
  case object SDcacheWbVol extends Definition(
        Opcode.assume("s_dcache_wb_vol"),
        decoder(form(0)((_, mods) => Instruction.SDcacheWbVol(mods))),
        1067
      )
  case object SGetWaveidInWorkgroup extends Definition(
        Opcode.assume("s_get_waveid_in_workgroup"),
        decoder(form(1)((operands, mods) => Instruction.SGetWaveidInWorkgroup(operands(0), mods))),
        1091
      )
  case object SGl1Inv extends Definition(
        Opcode.assume("s_gl1_inv"),
        decoder(form(0)((_, mods) => Instruction.SGl1Inv(mods))),
        1094
      )
  case object SLoadB128 extends Definition(
        Opcode.assume("s_load_b128"),
        decoder(form(3)((operands, mods) => Instruction.SLoadB128(operands(0), operands(1), operands(2), mods))),
        1098
      )
  case object SLoadB256 extends Definition(
        Opcode.assume("s_load_b256"),
        decoder(form(3)((operands, mods) => Instruction.SLoadB256(operands(0), operands(1), operands(2), mods))),
        1099
      )
  case object SLoadB32 extends Definition(
        Opcode.assume("s_load_b32"),
        decoder(form(3)((operands, mods) => Instruction.SLoadB32(operands(0), operands(1), operands(2), mods))),
        1100
      )
  case object SLoadB512 extends Definition(
        Opcode.assume("s_load_b512"),
        decoder(form(3)((operands, mods) => Instruction.SLoadB512(operands(0), operands(1), operands(2), mods))),
        1101
      )
  case object SLoadB64 extends Definition(
        Opcode.assume("s_load_b64"),
        decoder(form(3)((operands, mods) => Instruction.SLoadB64(operands(0), operands(1), operands(2), mods))),
        1102
      )
  case object SLoadB96 extends Definition(
        Opcode.assume("s_load_b96"),
        decoder(form(3)((operands, mods) => Instruction.SLoadB96(operands(0), operands(1), operands(2), mods))),
        1103
      )
  case object SLoadDword extends Definition(
        Opcode.assume("s_load_dword"),
        decoder(form(3)((operands, mods) => Instruction.SLoadDword(operands(0), operands(1), operands(2), mods))),
        1104
      )
  case object SLoadDwordx16 extends Definition(
        Opcode.assume("s_load_dwordx16"),
        decoder(form(3)((operands, mods) => Instruction.SLoadDwordx16(operands(0), operands(1), operands(2), mods))),
        1105
      )
  case object SLoadDwordx2 extends Definition(
        Opcode.assume("s_load_dwordx2"),
        decoder(form(3)((operands, mods) => Instruction.SLoadDwordx2(operands(0), operands(1), operands(2), mods))),
        1106
      )
  case object SLoadDwordx4 extends Definition(
        Opcode.assume("s_load_dwordx4"),
        decoder(form(3)((operands, mods) => Instruction.SLoadDwordx4(operands(0), operands(1), operands(2), mods))),
        1107
      )
  case object SLoadDwordx8 extends Definition(
        Opcode.assume("s_load_dwordx8"),
        decoder(form(3)((operands, mods) => Instruction.SLoadDwordx8(operands(0), operands(1), operands(2), mods))),
        1108
      )
  case object SLoadI16 extends Definition(
        Opcode.assume("s_load_i16"),
        decoder(form(3)((operands, mods) => Instruction.SLoadI16(operands(0), operands(1), operands(2), mods))),
        1109
      )
  case object SLoadI8 extends Definition(
        Opcode.assume("s_load_i8"),
        decoder(form(3)((operands, mods) => Instruction.SLoadI8(operands(0), operands(1), operands(2), mods))),
        1110
      )
  case object SLoadU16 extends Definition(
        Opcode.assume("s_load_u16"),
        decoder(form(3)((operands, mods) => Instruction.SLoadU16(operands(0), operands(1), operands(2), mods))),
        1111
      )
  case object SLoadU8 extends Definition(
        Opcode.assume("s_load_u8"),
        decoder(form(3)((operands, mods) => Instruction.SLoadU8(operands(0), operands(1), operands(2), mods))),
        1112
      )
  case object SMemrealtime extends Definition(
        Opcode.assume("s_memrealtime"),
        decoder(form(1)((operands, mods) => Instruction.SMemrealtime(operands(0), mods))),
        1129
      )
  case object SMemtime extends Definition(
        Opcode.assume("s_memtime"),
        decoder(form(1)((operands, mods) => Instruction.SMemtime(operands(0), mods))),
        1130
      )
  case object SPrefetchData extends Definition(
        Opcode.assume("s_prefetch_data"),
        decoder(form(4)((operands, mods) =>
          Instruction.SPrefetchData(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1187
      )
  case object SPrefetchDataPcRel extends Definition(
        Opcode.assume("s_prefetch_data_pc_rel"),
        decoder(form(3)((operands, mods) =>
          Instruction.SPrefetchDataPcRel(operands(0), operands(1), operands(2), mods)
        )),
        1188
      )
  case object SPrefetchInst extends Definition(
        Opcode.assume("s_prefetch_inst"),
        decoder(form(4)((operands, mods) =>
          Instruction.SPrefetchInst(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1189
      )
  case object SPrefetchInstPcRel extends Definition(
        Opcode.assume("s_prefetch_inst_pc_rel"),
        decoder(form(3)((operands, mods) =>
          Instruction.SPrefetchInstPcRel(operands(0), operands(1), operands(2), mods)
        )),
        1190
      )
  case object SScratchLoadDword extends Definition(
        Opcode.assume("s_scratch_load_dword"),
        decoder(form(3)((operands, mods) =>
          Instruction.SScratchLoadDword(operands(0), operands(1), operands(2), mods)
        )),
        1199
      )
  case object SScratchLoadDwordx2 extends Definition(
        Opcode.assume("s_scratch_load_dwordx2"),
        decoder(form(3)((operands, mods) =>
          Instruction.SScratchLoadDwordx2(operands(0), operands(1), operands(2), mods)
        )),
        1200
      )
  case object SScratchLoadDwordx4 extends Definition(
        Opcode.assume("s_scratch_load_dwordx4"),
        decoder(form(3)((operands, mods) =>
          Instruction.SScratchLoadDwordx4(operands(0), operands(1), operands(2), mods)
        )),
        1201
      )
  case object SScratchStoreDword extends Definition(
        Opcode.assume("s_scratch_store_dword"),
        decoder(form(3)((operands, mods) =>
          Instruction.SScratchStoreDword(operands(0), operands(1), operands(2), mods)
        )),
        1202
      )
  case object SScratchStoreDwordx2 extends Definition(
        Opcode.assume("s_scratch_store_dwordx2"),
        decoder(form(3)((operands, mods) =>
          Instruction.SScratchStoreDwordx2(operands(0), operands(1), operands(2), mods)
        )),
        1203
      )
  case object SScratchStoreDwordx4 extends Definition(
        Opcode.assume("s_scratch_store_dwordx4"),
        decoder(form(3)((operands, mods) =>
          Instruction.SScratchStoreDwordx4(operands(0), operands(1), operands(2), mods)
        )),
        1204
      )
  case object SStoreDword extends Definition(
        Opcode.assume("s_store_dword"),
        decoder(form(3)((operands, mods) => Instruction.SStoreDword(operands(0), operands(1), operands(2), mods))),
        1229
      )
  case object SStoreDwordx2 extends Definition(
        Opcode.assume("s_store_dwordx2"),
        decoder(form(3)((operands, mods) => Instruction.SStoreDwordx2(operands(0), operands(1), operands(2), mods))),
        1230
      )
  case object SStoreDwordx4 extends Definition(
        Opcode.assume("s_store_dwordx4"),
        decoder(form(3)((operands, mods) => Instruction.SStoreDwordx4(operands(0), operands(1), operands(2), mods))),
        1231
      )

  val definitions: Vec[Definition] = Vec(
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
