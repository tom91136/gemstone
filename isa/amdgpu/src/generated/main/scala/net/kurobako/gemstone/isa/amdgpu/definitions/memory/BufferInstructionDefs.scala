// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Constraint.NonEmptyString
import net.kurobako.gemstone.Vec
import Instruction.*
import Program.*

@javax.annotation.processing.Generated(Array("sbt codegen"))
private[amdgpu] object BufferInstructionDefs {
  case object BufferAtomicAdd extends Definition(
        NonEmptyString("buffer_atomic_add"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicAdd(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        0
      )
  case object BufferAtomicAddF32 extends Definition(
        NonEmptyString("buffer_atomic_add_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicAddF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1
      )
  case object BufferAtomicAddF64 extends Definition(
        NonEmptyString("buffer_atomic_add_f64"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicAddF64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        2
      )
  case object BufferAtomicAddU32 extends Definition(
        NonEmptyString("buffer_atomic_add_u32"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicAddU32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        3
      )
  case object BufferAtomicAddU64 extends Definition(
        NonEmptyString("buffer_atomic_add_u64"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicAddU64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        4
      )
  case object BufferAtomicAddX2 extends Definition(
        NonEmptyString("buffer_atomic_add_x2"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicAddX2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        5
      )
  case object BufferAtomicAnd extends Definition(
        NonEmptyString("buffer_atomic_and"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicAnd(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        6
      )
  case object BufferAtomicAndB32 extends Definition(
        NonEmptyString("buffer_atomic_and_b32"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicAndB32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        7
      )
  case object BufferAtomicAndB64 extends Definition(
        NonEmptyString("buffer_atomic_and_b64"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicAndB64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        8
      )
  case object BufferAtomicAndX2 extends Definition(
        NonEmptyString("buffer_atomic_and_x2"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicAndX2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        9
      )
  case object BufferAtomicCmpswap extends Definition(
        NonEmptyString("buffer_atomic_cmpswap"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicCmpswap(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        10
      )
  case object BufferAtomicCmpswapB32 extends Definition(
        NonEmptyString("buffer_atomic_cmpswap_b32"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicCmpswapB32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        11
      )
  case object BufferAtomicCmpswapB64 extends Definition(
        NonEmptyString("buffer_atomic_cmpswap_b64"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicCmpswapB64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        12
      )
  case object BufferAtomicCmpswapF32 extends Definition(
        NonEmptyString("buffer_atomic_cmpswap_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicCmpswapF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        13
      )
  case object BufferAtomicCmpswapX2 extends Definition(
        NonEmptyString("buffer_atomic_cmpswap_x2"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicCmpswapX2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        14
      )
  case object BufferAtomicCondSubU32 extends Definition(
        NonEmptyString("buffer_atomic_cond_sub_u32"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicCondSubU32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        15
      )
  case object BufferAtomicCsub extends Definition(
        NonEmptyString("buffer_atomic_csub"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicCsub(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        16
      )
  case object BufferAtomicCsubU32 extends Definition(
        NonEmptyString("buffer_atomic_csub_u32"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicCsubU32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        17
      )
  case object BufferAtomicDec extends Definition(
        NonEmptyString("buffer_atomic_dec"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicDec(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        18
      )
  case object BufferAtomicDecU32 extends Definition(
        NonEmptyString("buffer_atomic_dec_u32"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicDecU32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        19
      )
  case object BufferAtomicDecU64 extends Definition(
        NonEmptyString("buffer_atomic_dec_u64"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicDecU64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        20
      )
  case object BufferAtomicDecX2 extends Definition(
        NonEmptyString("buffer_atomic_dec_x2"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicDecX2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        21
      )
  case object BufferAtomicFcmpswap extends Definition(
        NonEmptyString("buffer_atomic_fcmpswap"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicFcmpswap(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        22
      )
  case object BufferAtomicFcmpswapX2 extends Definition(
        NonEmptyString("buffer_atomic_fcmpswap_x2"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicFcmpswapX2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        23
      )
  case object BufferAtomicFmax extends Definition(
        NonEmptyString("buffer_atomic_fmax"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicFmax(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        24
      )
  case object BufferAtomicFmaxX2 extends Definition(
        NonEmptyString("buffer_atomic_fmax_x2"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicFmaxX2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        25
      )
  case object BufferAtomicFmin extends Definition(
        NonEmptyString("buffer_atomic_fmin"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicFmin(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        26
      )
  case object BufferAtomicFminX2 extends Definition(
        NonEmptyString("buffer_atomic_fmin_x2"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicFminX2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        27
      )
  case object BufferAtomicInc extends Definition(
        NonEmptyString("buffer_atomic_inc"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicInc(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        28
      )
  case object BufferAtomicIncU32 extends Definition(
        NonEmptyString("buffer_atomic_inc_u32"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicIncU32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        29
      )
  case object BufferAtomicIncU64 extends Definition(
        NonEmptyString("buffer_atomic_inc_u64"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicIncU64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        30
      )
  case object BufferAtomicIncX2 extends Definition(
        NonEmptyString("buffer_atomic_inc_x2"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicIncX2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        31
      )
  case object BufferAtomicMaxF32 extends Definition(
        NonEmptyString("buffer_atomic_max_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicMaxF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        32
      )
  case object BufferAtomicMaxF64 extends Definition(
        NonEmptyString("buffer_atomic_max_f64"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicMaxF64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        33
      )
  case object BufferAtomicMaxI32 extends Definition(
        NonEmptyString("buffer_atomic_max_i32"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicMaxI32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        34
      )
  case object BufferAtomicMaxI64 extends Definition(
        NonEmptyString("buffer_atomic_max_i64"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicMaxI64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        35
      )
  case object BufferAtomicMaxNumF32 extends Definition(
        NonEmptyString("buffer_atomic_max_num_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicMaxNumF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        36
      )
  case object BufferAtomicMaxNumF64 extends Definition(
        NonEmptyString("buffer_atomic_max_num_f64"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicMaxNumF64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        37
      )
  case object BufferAtomicMaxU32 extends Definition(
        NonEmptyString("buffer_atomic_max_u32"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicMaxU32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        38
      )
  case object BufferAtomicMaxU64 extends Definition(
        NonEmptyString("buffer_atomic_max_u64"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicMaxU64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        39
      )
  case object BufferAtomicMinF32 extends Definition(
        NonEmptyString("buffer_atomic_min_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicMinF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        40
      )
  case object BufferAtomicMinF64 extends Definition(
        NonEmptyString("buffer_atomic_min_f64"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicMinF64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        41
      )
  case object BufferAtomicMinI32 extends Definition(
        NonEmptyString("buffer_atomic_min_i32"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicMinI32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        42
      )
  case object BufferAtomicMinI64 extends Definition(
        NonEmptyString("buffer_atomic_min_i64"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicMinI64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        43
      )
  case object BufferAtomicMinNumF32 extends Definition(
        NonEmptyString("buffer_atomic_min_num_f32"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicMinNumF32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        44
      )
  case object BufferAtomicMinNumF64 extends Definition(
        NonEmptyString("buffer_atomic_min_num_f64"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicMinNumF64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        45
      )
  case object BufferAtomicMinU32 extends Definition(
        NonEmptyString("buffer_atomic_min_u32"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicMinU32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        46
      )
  case object BufferAtomicMinU64 extends Definition(
        NonEmptyString("buffer_atomic_min_u64"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicMinU64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        47
      )
  case object BufferAtomicOr extends Definition(
        NonEmptyString("buffer_atomic_or"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicOr(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        48
      )
  case object BufferAtomicOrB32 extends Definition(
        NonEmptyString("buffer_atomic_or_b32"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicOrB32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        49
      )
  case object BufferAtomicOrB64 extends Definition(
        NonEmptyString("buffer_atomic_or_b64"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicOrB64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        50
      )
  case object BufferAtomicOrX2 extends Definition(
        NonEmptyString("buffer_atomic_or_x2"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicOrX2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        51
      )
  case object BufferAtomicPkAddBf16 extends Definition(
        NonEmptyString("buffer_atomic_pk_add_bf16"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicPkAddBf16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        52
      )
  case object BufferAtomicPkAddF16 extends Definition(
        NonEmptyString("buffer_atomic_pk_add_f16"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicPkAddF16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        53
      )
  case object BufferAtomicRsub extends Definition(
        NonEmptyString("buffer_atomic_rsub"),
        decoder(form(0)((_, mods) => Instruction.BufferAtomicRsub(mods))),
        54
      )
  case object BufferAtomicRsubX2 extends Definition(
        NonEmptyString("buffer_atomic_rsub_x2"),
        decoder(form(0)((_, mods) => Instruction.BufferAtomicRsubX2(mods))),
        55
      )
  case object BufferAtomicSmax extends Definition(
        NonEmptyString("buffer_atomic_smax"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicSmax(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        56
      )
  case object BufferAtomicSmaxX2 extends Definition(
        NonEmptyString("buffer_atomic_smax_x2"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicSmaxX2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        57
      )
  case object BufferAtomicSmin extends Definition(
        NonEmptyString("buffer_atomic_smin"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicSmin(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        58
      )
  case object BufferAtomicSminX2 extends Definition(
        NonEmptyString("buffer_atomic_smin_x2"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicSminX2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        59
      )
  case object BufferAtomicSub extends Definition(
        NonEmptyString("buffer_atomic_sub"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicSub(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        60
      )
  case object BufferAtomicSubClampU32 extends Definition(
        NonEmptyString("buffer_atomic_sub_clamp_u32"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicSubClampU32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        61
      )
  case object BufferAtomicSubU32 extends Definition(
        NonEmptyString("buffer_atomic_sub_u32"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicSubU32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        62
      )
  case object BufferAtomicSubU64 extends Definition(
        NonEmptyString("buffer_atomic_sub_u64"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicSubU64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        63
      )
  case object BufferAtomicSubX2 extends Definition(
        NonEmptyString("buffer_atomic_sub_x2"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicSubX2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        64
      )
  case object BufferAtomicSwap extends Definition(
        NonEmptyString("buffer_atomic_swap"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicSwap(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        65
      )
  case object BufferAtomicSwapB32 extends Definition(
        NonEmptyString("buffer_atomic_swap_b32"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicSwapB32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        66
      )
  case object BufferAtomicSwapB64 extends Definition(
        NonEmptyString("buffer_atomic_swap_b64"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicSwapB64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        67
      )
  case object BufferAtomicSwapX2 extends Definition(
        NonEmptyString("buffer_atomic_swap_x2"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicSwapX2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        68
      )
  case object BufferAtomicUmax extends Definition(
        NonEmptyString("buffer_atomic_umax"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicUmax(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        69
      )
  case object BufferAtomicUmaxX2 extends Definition(
        NonEmptyString("buffer_atomic_umax_x2"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicUmaxX2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        70
      )
  case object BufferAtomicUmin extends Definition(
        NonEmptyString("buffer_atomic_umin"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicUmin(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        71
      )
  case object BufferAtomicUminX2 extends Definition(
        NonEmptyString("buffer_atomic_umin_x2"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicUminX2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        72
      )
  case object BufferAtomicXor extends Definition(
        NonEmptyString("buffer_atomic_xor"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicXor(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        73
      )
  case object BufferAtomicXorB32 extends Definition(
        NonEmptyString("buffer_atomic_xor_b32"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicXorB32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        74
      )
  case object BufferAtomicXorB64 extends Definition(
        NonEmptyString("buffer_atomic_xor_b64"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicXorB64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        75
      )
  case object BufferAtomicXorX2 extends Definition(
        NonEmptyString("buffer_atomic_xor_x2"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferAtomicXorX2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        76
      )
  case object BufferGl0Inv extends Definition(
        NonEmptyString("buffer_gl0_inv"),
        decoder(form(0)((_, mods) => Instruction.BufferGl0Inv(mods))),
        77
      )
  case object BufferGl1Inv extends Definition(
        NonEmptyString("buffer_gl1_inv"),
        decoder(form(0)((_, mods) => Instruction.BufferGl1Inv(mods))),
        78
      )
  case object BufferInv extends Definition(
        NonEmptyString("buffer_inv"),
        decoder(form(0)((_, mods) => Instruction.BufferInv(mods))),
        79
      )
  case object BufferInvl2 extends Definition(
        NonEmptyString("buffer_invl2"),
        decoder(form(0)((_, mods) => Instruction.BufferInvl2(mods))),
        80
      )
  case object BufferLoadB128 extends Definition(
        NonEmptyString("buffer_load_b128"),
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadB128_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadB128_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        81
      )
  case object BufferLoadB32 extends Definition(
        NonEmptyString("buffer_load_b32"),
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadB32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadB32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        82
      )
  case object BufferLoadB64 extends Definition(
        NonEmptyString("buffer_load_b64"),
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadB64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadB64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        83
      )
  case object BufferLoadB96 extends Definition(
        NonEmptyString("buffer_load_b96"),
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadB96_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadB96_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        84
      )
  case object BufferLoadD16B16 extends Definition(
        NonEmptyString("buffer_load_d16_b16"),
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadD16B16_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadD16B16_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        85
      )
  case object BufferLoadD16FormatX extends Definition(
        NonEmptyString("buffer_load_d16_format_x"),
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadD16FormatX_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadD16FormatX_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        86
      )
  case object BufferLoadD16FormatXy extends Definition(
        NonEmptyString("buffer_load_d16_format_xy"),
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadD16FormatXy_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadD16FormatXy_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        87
      )
  case object BufferLoadD16FormatXyz extends Definition(
        NonEmptyString("buffer_load_d16_format_xyz"),
        decoder(
          form(3)((operands, mods) =>
            Instruction.BufferLoadD16FormatXyz_3(operands(0), operands(1), operands(2), mods)
          ),
          form(4)((operands, mods) =>
            Instruction.BufferLoadD16FormatXyz_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        88
      )
  case object BufferLoadD16FormatXyzw extends Definition(
        NonEmptyString("buffer_load_d16_format_xyzw"),
        decoder(
          form(3)((operands, mods) =>
            Instruction.BufferLoadD16FormatXyzw_3(operands(0), operands(1), operands(2), mods)
          ),
          form(4)((operands, mods) =>
            Instruction.BufferLoadD16FormatXyzw_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        89
      )
  case object BufferLoadD16HiB16 extends Definition(
        NonEmptyString("buffer_load_d16_hi_b16"),
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadD16HiB16_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadD16HiB16_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        90
      )
  case object BufferLoadD16HiFormatX extends Definition(
        NonEmptyString("buffer_load_d16_hi_format_x"),
        decoder(
          form(3)((operands, mods) =>
            Instruction.BufferLoadD16HiFormatX_3(operands(0), operands(1), operands(2), mods)
          ),
          form(4)((operands, mods) =>
            Instruction.BufferLoadD16HiFormatX_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        91
      )
  case object BufferLoadD16HiI8 extends Definition(
        NonEmptyString("buffer_load_d16_hi_i8"),
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadD16HiI8_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadD16HiI8_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        92
      )
  case object BufferLoadD16HiU8 extends Definition(
        NonEmptyString("buffer_load_d16_hi_u8"),
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadD16HiU8_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadD16HiU8_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        93
      )
  case object BufferLoadD16I8 extends Definition(
        NonEmptyString("buffer_load_d16_i8"),
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadD16I8_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadD16I8_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        94
      )
  case object BufferLoadD16U8 extends Definition(
        NonEmptyString("buffer_load_d16_u8"),
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadD16U8_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadD16U8_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        95
      )
  case object BufferLoadDword extends Definition(
        NonEmptyString("buffer_load_dword"),
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadDword_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadDword_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        96
      )
  case object BufferLoadDwordx2 extends Definition(
        NonEmptyString("buffer_load_dwordx2"),
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadDwordx2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadDwordx2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        97
      )
  case object BufferLoadDwordx3 extends Definition(
        NonEmptyString("buffer_load_dwordx3"),
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadDwordx3_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadDwordx3_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        98
      )
  case object BufferLoadDwordx4 extends Definition(
        NonEmptyString("buffer_load_dwordx4"),
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadDwordx4_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadDwordx4_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        99
      )
  case object BufferLoadFormatD16HiX extends Definition(
        NonEmptyString("buffer_load_format_d16_hi_x"),
        decoder(
          form(3)((operands, mods) =>
            Instruction.BufferLoadFormatD16HiX_3(operands(0), operands(1), operands(2), mods)
          ),
          form(4)((operands, mods) =>
            Instruction.BufferLoadFormatD16HiX_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        100
      )
  case object BufferLoadFormatD16X extends Definition(
        NonEmptyString("buffer_load_format_d16_x"),
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadFormatD16X_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadFormatD16X_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        101
      )
  case object BufferLoadFormatD16Xy extends Definition(
        NonEmptyString("buffer_load_format_d16_xy"),
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadFormatD16Xy_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadFormatD16Xy_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        102
      )
  case object BufferLoadFormatD16Xyz extends Definition(
        NonEmptyString("buffer_load_format_d16_xyz"),
        decoder(
          form(3)((operands, mods) =>
            Instruction.BufferLoadFormatD16Xyz_3(operands(0), operands(1), operands(2), mods)
          ),
          form(4)((operands, mods) =>
            Instruction.BufferLoadFormatD16Xyz_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        103
      )
  case object BufferLoadFormatD16Xyzw extends Definition(
        NonEmptyString("buffer_load_format_d16_xyzw"),
        decoder(
          form(3)((operands, mods) =>
            Instruction.BufferLoadFormatD16Xyzw_3(operands(0), operands(1), operands(2), mods)
          ),
          form(4)((operands, mods) =>
            Instruction.BufferLoadFormatD16Xyzw_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        104
      )
  case object BufferLoadFormatX extends Definition(
        NonEmptyString("buffer_load_format_x"),
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadFormatX_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadFormatX_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        105
      )
  case object BufferLoadFormatXy extends Definition(
        NonEmptyString("buffer_load_format_xy"),
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadFormatXy_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadFormatXy_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        106
      )
  case object BufferLoadFormatXyz extends Definition(
        NonEmptyString("buffer_load_format_xyz"),
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadFormatXyz_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadFormatXyz_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        107
      )
  case object BufferLoadFormatXyzw extends Definition(
        NonEmptyString("buffer_load_format_xyzw"),
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadFormatXyzw_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadFormatXyzw_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        108
      )
  case object BufferLoadI16 extends Definition(
        NonEmptyString("buffer_load_i16"),
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadI16_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadI16_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        109
      )
  case object BufferLoadI8 extends Definition(
        NonEmptyString("buffer_load_i8"),
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadI8_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadI8_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        110
      )
  case object BufferLoadSbyte extends Definition(
        NonEmptyString("buffer_load_sbyte"),
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadSbyte_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadSbyte_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        111
      )
  case object BufferLoadSbyteD16 extends Definition(
        NonEmptyString("buffer_load_sbyte_d16"),
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadSbyteD16_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadSbyteD16_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        112
      )
  case object BufferLoadSbyteD16Hi extends Definition(
        NonEmptyString("buffer_load_sbyte_d16_hi"),
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadSbyteD16Hi_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadSbyteD16Hi_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        113
      )
  case object BufferLoadShortD16 extends Definition(
        NonEmptyString("buffer_load_short_d16"),
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadShortD16_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadShortD16_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        114
      )
  case object BufferLoadShortD16Hi extends Definition(
        NonEmptyString("buffer_load_short_d16_hi"),
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadShortD16Hi_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadShortD16Hi_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        115
      )
  case object BufferLoadSshort extends Definition(
        NonEmptyString("buffer_load_sshort"),
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadSshort_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadSshort_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        116
      )
  case object BufferLoadU16 extends Definition(
        NonEmptyString("buffer_load_u16"),
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadU16_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadU16_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        117
      )
  case object BufferLoadU8 extends Definition(
        NonEmptyString("buffer_load_u8"),
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadU8_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadU8_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        118
      )
  case object BufferLoadUbyte extends Definition(
        NonEmptyString("buffer_load_ubyte"),
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadUbyte_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadUbyte_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        119
      )
  case object BufferLoadUbyteD16 extends Definition(
        NonEmptyString("buffer_load_ubyte_d16"),
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadUbyteD16_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadUbyteD16_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        120
      )
  case object BufferLoadUbyteD16Hi extends Definition(
        NonEmptyString("buffer_load_ubyte_d16_hi"),
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadUbyteD16Hi_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadUbyteD16Hi_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        121
      )
  case object BufferLoadUshort extends Definition(
        NonEmptyString("buffer_load_ushort"),
        decoder(
          form(3)((operands, mods) => Instruction.BufferLoadUshort_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.BufferLoadUshort_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        122
      )
  case object BufferStoreB128 extends Definition(
        NonEmptyString("buffer_store_b128"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreB128(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        123
      )
  case object BufferStoreB16 extends Definition(
        NonEmptyString("buffer_store_b16"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreB16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        124
      )
  case object BufferStoreB32 extends Definition(
        NonEmptyString("buffer_store_b32"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreB32(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        125
      )
  case object BufferStoreB64 extends Definition(
        NonEmptyString("buffer_store_b64"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreB64(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        126
      )
  case object BufferStoreB8 extends Definition(
        NonEmptyString("buffer_store_b8"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreB8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        127
      )
  case object BufferStoreB96 extends Definition(
        NonEmptyString("buffer_store_b96"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreB96(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        128
      )
  case object BufferStoreByte extends Definition(
        NonEmptyString("buffer_store_byte"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreByte(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        129
      )
  case object BufferStoreByteD16Hi extends Definition(
        NonEmptyString("buffer_store_byte_d16_hi"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreByteD16Hi(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        130
      )
  case object BufferStoreD16FormatX extends Definition(
        NonEmptyString("buffer_store_d16_format_x"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreD16FormatX(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        131
      )
  case object BufferStoreD16FormatXy extends Definition(
        NonEmptyString("buffer_store_d16_format_xy"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreD16FormatXy(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        132
      )
  case object BufferStoreD16FormatXyz extends Definition(
        NonEmptyString("buffer_store_d16_format_xyz"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreD16FormatXyz(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        133
      )
  case object BufferStoreD16FormatXyzw extends Definition(
        NonEmptyString("buffer_store_d16_format_xyzw"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreD16FormatXyzw(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        134
      )
  case object BufferStoreD16HiB16 extends Definition(
        NonEmptyString("buffer_store_d16_hi_b16"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreD16HiB16(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        135
      )
  case object BufferStoreD16HiB8 extends Definition(
        NonEmptyString("buffer_store_d16_hi_b8"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreD16HiB8(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        136
      )
  case object BufferStoreD16HiFormatX extends Definition(
        NonEmptyString("buffer_store_d16_hi_format_x"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreD16HiFormatX(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        137
      )
  case object BufferStoreDword extends Definition(
        NonEmptyString("buffer_store_dword"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreDword(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        138
      )
  case object BufferStoreDwordx2 extends Definition(
        NonEmptyString("buffer_store_dwordx2"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreDwordx2(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        139
      )
  case object BufferStoreDwordx3 extends Definition(
        NonEmptyString("buffer_store_dwordx3"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreDwordx3(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        140
      )
  case object BufferStoreDwordx4 extends Definition(
        NonEmptyString("buffer_store_dwordx4"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreDwordx4(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        141
      )
  case object BufferStoreFormatD16HiX extends Definition(
        NonEmptyString("buffer_store_format_d16_hi_x"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreFormatD16HiX(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        142
      )
  case object BufferStoreFormatD16X extends Definition(
        NonEmptyString("buffer_store_format_d16_x"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreFormatD16X(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        143
      )
  case object BufferStoreFormatD16Xy extends Definition(
        NonEmptyString("buffer_store_format_d16_xy"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreFormatD16Xy(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        144
      )
  case object BufferStoreFormatD16Xyz extends Definition(
        NonEmptyString("buffer_store_format_d16_xyz"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreFormatD16Xyz(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        145
      )
  case object BufferStoreFormatD16Xyzw extends Definition(
        NonEmptyString("buffer_store_format_d16_xyzw"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreFormatD16Xyzw(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        146
      )
  case object BufferStoreFormatX extends Definition(
        NonEmptyString("buffer_store_format_x"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreFormatX(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        147
      )
  case object BufferStoreFormatXy extends Definition(
        NonEmptyString("buffer_store_format_xy"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreFormatXy(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        148
      )
  case object BufferStoreFormatXyz extends Definition(
        NonEmptyString("buffer_store_format_xyz"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreFormatXyz(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        149
      )
  case object BufferStoreFormatXyzw extends Definition(
        NonEmptyString("buffer_store_format_xyzw"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreFormatXyzw(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        150
      )
  case object BufferStoreLdsDword extends Definition(
        NonEmptyString("buffer_store_lds_dword"),
        decoder(form(2)((operands, mods) => Instruction.BufferStoreLdsDword(operands(0), operands(1), mods))),
        151
      )
  case object BufferStoreShort extends Definition(
        NonEmptyString("buffer_store_short"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreShort(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        152
      )
  case object BufferStoreShortD16Hi extends Definition(
        NonEmptyString("buffer_store_short_d16_hi"),
        decoder(form(4)((operands, mods) =>
          Instruction.BufferStoreShortD16Hi(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        153
      )
  case object BufferWbinvl1 extends Definition(
        NonEmptyString("buffer_wbinvl1"),
        decoder(form(0)((_, mods) => Instruction.BufferWbinvl1(mods))),
        154
      )
  case object BufferWbinvl1Vol extends Definition(
        NonEmptyString("buffer_wbinvl1_vol"),
        decoder(form(0)((_, mods) => Instruction.BufferWbinvl1Vol(mods))),
        155
      )
  case object BufferWbl2 extends Definition(
        NonEmptyString("buffer_wbl2"),
        decoder(form(0)((_, mods) => Instruction.BufferWbl2(mods))),
        156
      )
  case object TbufferLoadD16FormatX extends Definition(
        NonEmptyString("tbuffer_load_d16_format_x"),
        decoder(form(4)((operands, mods) =>
          Instruction.TbufferLoadD16FormatX(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1335
      )
  case object TbufferLoadD16FormatXy extends Definition(
        NonEmptyString("tbuffer_load_d16_format_xy"),
        decoder(form(4)((operands, mods) =>
          Instruction.TbufferLoadD16FormatXy(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1336
      )
  case object TbufferLoadD16FormatXyz extends Definition(
        NonEmptyString("tbuffer_load_d16_format_xyz"),
        decoder(form(4)((operands, mods) =>
          Instruction.TbufferLoadD16FormatXyz(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1337
      )
  case object TbufferLoadD16FormatXyzw extends Definition(
        NonEmptyString("tbuffer_load_d16_format_xyzw"),
        decoder(form(4)((operands, mods) =>
          Instruction.TbufferLoadD16FormatXyzw(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1338
      )
  case object TbufferLoadFormatD16X extends Definition(
        NonEmptyString("tbuffer_load_format_d16_x"),
        decoder(form(4)((operands, mods) =>
          Instruction.TbufferLoadFormatD16X(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1339
      )
  case object TbufferLoadFormatD16Xy extends Definition(
        NonEmptyString("tbuffer_load_format_d16_xy"),
        decoder(form(4)((operands, mods) =>
          Instruction.TbufferLoadFormatD16Xy(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1340
      )
  case object TbufferLoadFormatD16Xyz extends Definition(
        NonEmptyString("tbuffer_load_format_d16_xyz"),
        decoder(form(4)((operands, mods) =>
          Instruction.TbufferLoadFormatD16Xyz(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1341
      )
  case object TbufferLoadFormatD16Xyzw extends Definition(
        NonEmptyString("tbuffer_load_format_d16_xyzw"),
        decoder(form(4)((operands, mods) =>
          Instruction.TbufferLoadFormatD16Xyzw(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1342
      )
  case object TbufferLoadFormatX extends Definition(
        NonEmptyString("tbuffer_load_format_x"),
        decoder(form(4)((operands, mods) =>
          Instruction.TbufferLoadFormatX(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1343
      )
  case object TbufferLoadFormatXy extends Definition(
        NonEmptyString("tbuffer_load_format_xy"),
        decoder(form(4)((operands, mods) =>
          Instruction.TbufferLoadFormatXy(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1344
      )
  case object TbufferLoadFormatXyz extends Definition(
        NonEmptyString("tbuffer_load_format_xyz"),
        decoder(form(4)((operands, mods) =>
          Instruction.TbufferLoadFormatXyz(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1345
      )
  case object TbufferLoadFormatXyzw extends Definition(
        NonEmptyString("tbuffer_load_format_xyzw"),
        decoder(form(4)((operands, mods) =>
          Instruction.TbufferLoadFormatXyzw(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1346
      )
  case object TbufferStoreD16FormatX extends Definition(
        NonEmptyString("tbuffer_store_d16_format_x"),
        decoder(form(4)((operands, mods) =>
          Instruction.TbufferStoreD16FormatX(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1347
      )
  case object TbufferStoreD16FormatXy extends Definition(
        NonEmptyString("tbuffer_store_d16_format_xy"),
        decoder(form(4)((operands, mods) =>
          Instruction.TbufferStoreD16FormatXy(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1348
      )
  case object TbufferStoreD16FormatXyz extends Definition(
        NonEmptyString("tbuffer_store_d16_format_xyz"),
        decoder(form(4)((operands, mods) =>
          Instruction.TbufferStoreD16FormatXyz(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1349
      )
  case object TbufferStoreD16FormatXyzw extends Definition(
        NonEmptyString("tbuffer_store_d16_format_xyzw"),
        decoder(form(4)((operands, mods) =>
          Instruction.TbufferStoreD16FormatXyzw(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1350
      )
  case object TbufferStoreFormatD16X extends Definition(
        NonEmptyString("tbuffer_store_format_d16_x"),
        decoder(form(4)((operands, mods) =>
          Instruction.TbufferStoreFormatD16X(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1351
      )
  case object TbufferStoreFormatD16Xy extends Definition(
        NonEmptyString("tbuffer_store_format_d16_xy"),
        decoder(form(4)((operands, mods) =>
          Instruction.TbufferStoreFormatD16Xy(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1352
      )
  case object TbufferStoreFormatD16Xyz extends Definition(
        NonEmptyString("tbuffer_store_format_d16_xyz"),
        decoder(form(4)((operands, mods) =>
          Instruction.TbufferStoreFormatD16Xyz(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1353
      )
  case object TbufferStoreFormatD16Xyzw extends Definition(
        NonEmptyString("tbuffer_store_format_d16_xyzw"),
        decoder(form(4)((operands, mods) =>
          Instruction.TbufferStoreFormatD16Xyzw(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1354
      )
  case object TbufferStoreFormatX extends Definition(
        NonEmptyString("tbuffer_store_format_x"),
        decoder(form(4)((operands, mods) =>
          Instruction.TbufferStoreFormatX(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1355
      )
  case object TbufferStoreFormatXy extends Definition(
        NonEmptyString("tbuffer_store_format_xy"),
        decoder(form(4)((operands, mods) =>
          Instruction.TbufferStoreFormatXy(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1356
      )
  case object TbufferStoreFormatXyz extends Definition(
        NonEmptyString("tbuffer_store_format_xyz"),
        decoder(form(4)((operands, mods) =>
          Instruction.TbufferStoreFormatXyz(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1357
      )
  case object TbufferStoreFormatXyzw extends Definition(
        NonEmptyString("tbuffer_store_format_xyzw"),
        decoder(form(4)((operands, mods) =>
          Instruction.TbufferStoreFormatXyzw(operands(0), operands(1), operands(2), operands(3), mods)
        )),
        1358
      )

  val definitions: Vec[Definition] = Vec(
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
