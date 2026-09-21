// Generated from the pinned Mesa ACO and AMD machine-readable ISA inputs. Regenerate with: sbt codegen
// scalafix:off GemstoneNoAssume; values were validated by the generator
package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Vec
import Instruction.*
import Program.*

@javax.annotation.processing.Generated(Array("sbt codegen"))
private[amdgpu] object FlatInstructionDefs {
  case object ClusterLoadAsyncToLdsB128 extends Definition(
        Opcode.assume("cluster_load_async_to_lds_b128"),
        decoder(form(3)((operands, mods) =>
          Instruction.ClusterLoadAsyncToLdsB128(operands(0), operands(1), operands(2), mods)
        )),
        157
      )
  case object ClusterLoadAsyncToLdsB32 extends Definition(
        Opcode.assume("cluster_load_async_to_lds_b32"),
        decoder(form(3)((operands, mods) =>
          Instruction.ClusterLoadAsyncToLdsB32(operands(0), operands(1), operands(2), mods)
        )),
        158
      )
  case object ClusterLoadAsyncToLdsB64 extends Definition(
        Opcode.assume("cluster_load_async_to_lds_b64"),
        decoder(form(3)((operands, mods) =>
          Instruction.ClusterLoadAsyncToLdsB64(operands(0), operands(1), operands(2), mods)
        )),
        159
      )
  case object ClusterLoadAsyncToLdsB8 extends Definition(
        Opcode.assume("cluster_load_async_to_lds_b8"),
        decoder(form(3)((operands, mods) =>
          Instruction.ClusterLoadAsyncToLdsB8(operands(0), operands(1), operands(2), mods)
        )),
        160
      )
  case object ClusterLoadB128 extends Definition(
        Opcode.assume("cluster_load_b128"),
        decoder(form(3)((operands, mods) => Instruction.ClusterLoadB128(operands(0), operands(1), operands(2), mods))),
        161
      )
  case object ClusterLoadB32 extends Definition(
        Opcode.assume("cluster_load_b32"),
        decoder(form(3)((operands, mods) => Instruction.ClusterLoadB32(operands(0), operands(1), operands(2), mods))),
        162
      )
  case object ClusterLoadB64 extends Definition(
        Opcode.assume("cluster_load_b64"),
        decoder(form(3)((operands, mods) => Instruction.ClusterLoadB64(operands(0), operands(1), operands(2), mods))),
        163
      )
  case object FlatAtomicAdd extends Definition(
        Opcode.assume("flat_atomic_add"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicAdd_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicAdd_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicAdd_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        404
      )
  case object FlatAtomicAddF32 extends Definition(
        Opcode.assume("flat_atomic_add_f32"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicAddF32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicAddF32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicAddF32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        405
      )
  case object FlatAtomicAddF64 extends Definition(
        Opcode.assume("flat_atomic_add_f64"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicAddF64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicAddF64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicAddF64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        406
      )
  case object FlatAtomicAddU32 extends Definition(
        Opcode.assume("flat_atomic_add_u32"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicAddU32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicAddU32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicAddU32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        407
      )
  case object FlatAtomicAddU64 extends Definition(
        Opcode.assume("flat_atomic_add_u64"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicAddU64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicAddU64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicAddU64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        408
      )
  case object FlatAtomicAddX2 extends Definition(
        Opcode.assume("flat_atomic_add_x2"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicAddX2_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicAddX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicAddX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        409
      )
  case object FlatAtomicAnd extends Definition(
        Opcode.assume("flat_atomic_and"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicAnd_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicAnd_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicAnd_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        410
      )
  case object FlatAtomicAndB32 extends Definition(
        Opcode.assume("flat_atomic_and_b32"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicAndB32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicAndB32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicAndB32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        411
      )
  case object FlatAtomicAndB64 extends Definition(
        Opcode.assume("flat_atomic_and_b64"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicAndB64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicAndB64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicAndB64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        412
      )
  case object FlatAtomicAndX2 extends Definition(
        Opcode.assume("flat_atomic_and_x2"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicAndX2_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicAndX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicAndX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        413
      )
  case object FlatAtomicCmpswap extends Definition(
        Opcode.assume("flat_atomic_cmpswap"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicCmpswap_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicCmpswap_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicCmpswap_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        414
      )
  case object FlatAtomicCmpswapB32 extends Definition(
        Opcode.assume("flat_atomic_cmpswap_b32"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicCmpswapB32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicCmpswapB32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicCmpswapB32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        415
      )
  case object FlatAtomicCmpswapB64 extends Definition(
        Opcode.assume("flat_atomic_cmpswap_b64"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicCmpswapB64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicCmpswapB64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicCmpswapB64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        416
      )
  case object FlatAtomicCmpswapF32 extends Definition(
        Opcode.assume("flat_atomic_cmpswap_f32"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicCmpswapF32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicCmpswapF32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicCmpswapF32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        417
      )
  case object FlatAtomicCmpswapX2 extends Definition(
        Opcode.assume("flat_atomic_cmpswap_x2"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicCmpswapX2_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicCmpswapX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicCmpswapX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        418
      )
  case object FlatAtomicCondSubU32 extends Definition(
        Opcode.assume("flat_atomic_cond_sub_u32"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicCondSubU32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicCondSubU32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicCondSubU32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        419
      )
  case object FlatAtomicDec extends Definition(
        Opcode.assume("flat_atomic_dec"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicDec_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicDec_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicDec_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        420
      )
  case object FlatAtomicDecU32 extends Definition(
        Opcode.assume("flat_atomic_dec_u32"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicDecU32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicDecU32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicDecU32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        421
      )
  case object FlatAtomicDecU64 extends Definition(
        Opcode.assume("flat_atomic_dec_u64"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicDecU64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicDecU64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicDecU64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        422
      )
  case object FlatAtomicDecX2 extends Definition(
        Opcode.assume("flat_atomic_dec_x2"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicDecX2_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicDecX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicDecX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        423
      )
  case object FlatAtomicFcmpswap extends Definition(
        Opcode.assume("flat_atomic_fcmpswap"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicFcmpswap_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicFcmpswap_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicFcmpswap_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        424
      )
  case object FlatAtomicFcmpswapX2 extends Definition(
        Opcode.assume("flat_atomic_fcmpswap_x2"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicFcmpswapX2_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicFcmpswapX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicFcmpswapX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        425
      )
  case object FlatAtomicFmax extends Definition(
        Opcode.assume("flat_atomic_fmax"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicFmax_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicFmax_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicFmax_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        426
      )
  case object FlatAtomicFmaxX2 extends Definition(
        Opcode.assume("flat_atomic_fmax_x2"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicFmaxX2_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicFmaxX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicFmaxX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        427
      )
  case object FlatAtomicFmin extends Definition(
        Opcode.assume("flat_atomic_fmin"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicFmin_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicFmin_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicFmin_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        428
      )
  case object FlatAtomicFminX2 extends Definition(
        Opcode.assume("flat_atomic_fmin_x2"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicFminX2_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicFminX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicFminX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        429
      )
  case object FlatAtomicInc extends Definition(
        Opcode.assume("flat_atomic_inc"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicInc_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicInc_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicInc_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        430
      )
  case object FlatAtomicIncU32 extends Definition(
        Opcode.assume("flat_atomic_inc_u32"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicIncU32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicIncU32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicIncU32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        431
      )
  case object FlatAtomicIncU64 extends Definition(
        Opcode.assume("flat_atomic_inc_u64"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicIncU64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicIncU64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicIncU64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        432
      )
  case object FlatAtomicIncX2 extends Definition(
        Opcode.assume("flat_atomic_inc_x2"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicIncX2_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicIncX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicIncX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        433
      )
  case object FlatAtomicMaxF32 extends Definition(
        Opcode.assume("flat_atomic_max_f32"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicMaxF32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicMaxF32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicMaxF32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        434
      )
  case object FlatAtomicMaxF64 extends Definition(
        Opcode.assume("flat_atomic_max_f64"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicMaxF64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicMaxF64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicMaxF64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        435
      )
  case object FlatAtomicMaxI32 extends Definition(
        Opcode.assume("flat_atomic_max_i32"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicMaxI32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicMaxI32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicMaxI32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        436
      )
  case object FlatAtomicMaxI64 extends Definition(
        Opcode.assume("flat_atomic_max_i64"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicMaxI64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicMaxI64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicMaxI64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        437
      )
  case object FlatAtomicMaxNumF32 extends Definition(
        Opcode.assume("flat_atomic_max_num_f32"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicMaxNumF32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicMaxNumF32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicMaxNumF32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        438
      )
  case object FlatAtomicMaxNumF64 extends Definition(
        Opcode.assume("flat_atomic_max_num_f64"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicMaxNumF64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicMaxNumF64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicMaxNumF64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        439
      )
  case object FlatAtomicMaxU32 extends Definition(
        Opcode.assume("flat_atomic_max_u32"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicMaxU32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicMaxU32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicMaxU32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        440
      )
  case object FlatAtomicMaxU64 extends Definition(
        Opcode.assume("flat_atomic_max_u64"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicMaxU64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicMaxU64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicMaxU64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        441
      )
  case object FlatAtomicMinF32 extends Definition(
        Opcode.assume("flat_atomic_min_f32"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicMinF32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicMinF32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicMinF32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        442
      )
  case object FlatAtomicMinF64 extends Definition(
        Opcode.assume("flat_atomic_min_f64"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicMinF64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicMinF64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicMinF64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        443
      )
  case object FlatAtomicMinI32 extends Definition(
        Opcode.assume("flat_atomic_min_i32"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicMinI32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicMinI32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicMinI32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        444
      )
  case object FlatAtomicMinI64 extends Definition(
        Opcode.assume("flat_atomic_min_i64"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicMinI64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicMinI64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicMinI64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        445
      )
  case object FlatAtomicMinNumF32 extends Definition(
        Opcode.assume("flat_atomic_min_num_f32"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicMinNumF32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicMinNumF32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicMinNumF32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        446
      )
  case object FlatAtomicMinNumF64 extends Definition(
        Opcode.assume("flat_atomic_min_num_f64"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicMinNumF64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicMinNumF64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicMinNumF64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        447
      )
  case object FlatAtomicMinU32 extends Definition(
        Opcode.assume("flat_atomic_min_u32"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicMinU32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicMinU32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicMinU32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        448
      )
  case object FlatAtomicMinU64 extends Definition(
        Opcode.assume("flat_atomic_min_u64"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicMinU64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicMinU64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicMinU64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        449
      )
  case object FlatAtomicOr extends Definition(
        Opcode.assume("flat_atomic_or"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicOr_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicOr_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicOr_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        450
      )
  case object FlatAtomicOrB32 extends Definition(
        Opcode.assume("flat_atomic_or_b32"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicOrB32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicOrB32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicOrB32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        451
      )
  case object FlatAtomicOrB64 extends Definition(
        Opcode.assume("flat_atomic_or_b64"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicOrB64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicOrB64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicOrB64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        452
      )
  case object FlatAtomicOrX2 extends Definition(
        Opcode.assume("flat_atomic_or_x2"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicOrX2_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicOrX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicOrX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        453
      )
  case object FlatAtomicPkAddBf16 extends Definition(
        Opcode.assume("flat_atomic_pk_add_bf16"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicPkAddBf16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicPkAddBf16_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicPkAddBf16_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        454
      )
  case object FlatAtomicPkAddF16 extends Definition(
        Opcode.assume("flat_atomic_pk_add_f16"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicPkAddF16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicPkAddF16_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicPkAddF16_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        455
      )
  case object FlatAtomicSmax extends Definition(
        Opcode.assume("flat_atomic_smax"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicSmax_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicSmax_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicSmax_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        456
      )
  case object FlatAtomicSmaxX2 extends Definition(
        Opcode.assume("flat_atomic_smax_x2"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicSmaxX2_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicSmaxX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicSmaxX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        457
      )
  case object FlatAtomicSmin extends Definition(
        Opcode.assume("flat_atomic_smin"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicSmin_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicSmin_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicSmin_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        458
      )
  case object FlatAtomicSminX2 extends Definition(
        Opcode.assume("flat_atomic_smin_x2"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicSminX2_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicSminX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicSminX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        459
      )
  case object FlatAtomicSub extends Definition(
        Opcode.assume("flat_atomic_sub"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicSub_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicSub_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicSub_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        460
      )
  case object FlatAtomicSubClampU32 extends Definition(
        Opcode.assume("flat_atomic_sub_clamp_u32"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicSubClampU32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicSubClampU32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicSubClampU32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        461
      )
  case object FlatAtomicSubU32 extends Definition(
        Opcode.assume("flat_atomic_sub_u32"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicSubU32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicSubU32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicSubU32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        462
      )
  case object FlatAtomicSubU64 extends Definition(
        Opcode.assume("flat_atomic_sub_u64"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicSubU64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicSubU64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicSubU64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        463
      )
  case object FlatAtomicSubX2 extends Definition(
        Opcode.assume("flat_atomic_sub_x2"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicSubX2_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicSubX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicSubX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        464
      )
  case object FlatAtomicSwap extends Definition(
        Opcode.assume("flat_atomic_swap"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicSwap_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicSwap_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicSwap_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        465
      )
  case object FlatAtomicSwapB32 extends Definition(
        Opcode.assume("flat_atomic_swap_b32"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicSwapB32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicSwapB32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicSwapB32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        466
      )
  case object FlatAtomicSwapB64 extends Definition(
        Opcode.assume("flat_atomic_swap_b64"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicSwapB64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicSwapB64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicSwapB64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        467
      )
  case object FlatAtomicSwapX2 extends Definition(
        Opcode.assume("flat_atomic_swap_x2"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicSwapX2_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicSwapX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicSwapX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        468
      )
  case object FlatAtomicUmax extends Definition(
        Opcode.assume("flat_atomic_umax"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicUmax_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicUmax_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicUmax_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        469
      )
  case object FlatAtomicUmaxX2 extends Definition(
        Opcode.assume("flat_atomic_umax_x2"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicUmaxX2_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicUmaxX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicUmaxX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        470
      )
  case object FlatAtomicUmin extends Definition(
        Opcode.assume("flat_atomic_umin"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicUmin_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicUmin_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicUmin_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        471
      )
  case object FlatAtomicUminX2 extends Definition(
        Opcode.assume("flat_atomic_umin_x2"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicUminX2_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicUminX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicUminX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        472
      )
  case object FlatAtomicXor extends Definition(
        Opcode.assume("flat_atomic_xor"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicXor_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicXor_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicXor_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        473
      )
  case object FlatAtomicXorB32 extends Definition(
        Opcode.assume("flat_atomic_xor_b32"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicXorB32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicXorB32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicXorB32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        474
      )
  case object FlatAtomicXorB64 extends Definition(
        Opcode.assume("flat_atomic_xor_b64"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicXorB64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicXorB64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicXorB64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        475
      )
  case object FlatAtomicXorX2 extends Definition(
        Opcode.assume("flat_atomic_xor_x2"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatAtomicXorX2_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatAtomicXorX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.FlatAtomicXorX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        476
      )
  case object FlatLoadB128 extends Definition(
        Opcode.assume("flat_load_b128"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatLoadB128_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatLoadB128_3(operands(0), operands(1), operands(2), mods))
        ),
        477
      )
  case object FlatLoadB32 extends Definition(
        Opcode.assume("flat_load_b32"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatLoadB32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatLoadB32_3(operands(0), operands(1), operands(2), mods))
        ),
        478
      )
  case object FlatLoadB64 extends Definition(
        Opcode.assume("flat_load_b64"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatLoadB64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatLoadB64_3(operands(0), operands(1), operands(2), mods))
        ),
        479
      )
  case object FlatLoadB96 extends Definition(
        Opcode.assume("flat_load_b96"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatLoadB96_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatLoadB96_3(operands(0), operands(1), operands(2), mods))
        ),
        480
      )
  case object FlatLoadD16B16 extends Definition(
        Opcode.assume("flat_load_d16_b16"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatLoadD16B16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatLoadD16B16_3(operands(0), operands(1), operands(2), mods))
        ),
        481
      )
  case object FlatLoadD16HiB16 extends Definition(
        Opcode.assume("flat_load_d16_hi_b16"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatLoadD16HiB16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatLoadD16HiB16_3(operands(0), operands(1), operands(2), mods))
        ),
        482
      )
  case object FlatLoadD16HiI8 extends Definition(
        Opcode.assume("flat_load_d16_hi_i8"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatLoadD16HiI8_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatLoadD16HiI8_3(operands(0), operands(1), operands(2), mods))
        ),
        483
      )
  case object FlatLoadD16HiU8 extends Definition(
        Opcode.assume("flat_load_d16_hi_u8"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatLoadD16HiU8_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatLoadD16HiU8_3(operands(0), operands(1), operands(2), mods))
        ),
        484
      )
  case object FlatLoadD16I8 extends Definition(
        Opcode.assume("flat_load_d16_i8"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatLoadD16I8_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatLoadD16I8_3(operands(0), operands(1), operands(2), mods))
        ),
        485
      )
  case object FlatLoadD16U8 extends Definition(
        Opcode.assume("flat_load_d16_u8"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatLoadD16U8_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatLoadD16U8_3(operands(0), operands(1), operands(2), mods))
        ),
        486
      )
  case object FlatLoadDword extends Definition(
        Opcode.assume("flat_load_dword"),
        decoder(form(2)((operands, mods) => Instruction.FlatLoadDword(operands(0), operands(1), mods))),
        487
      )
  case object FlatLoadDwordx2 extends Definition(
        Opcode.assume("flat_load_dwordx2"),
        decoder(form(2)((operands, mods) => Instruction.FlatLoadDwordx2(operands(0), operands(1), mods))),
        488
      )
  case object FlatLoadDwordx3 extends Definition(
        Opcode.assume("flat_load_dwordx3"),
        decoder(form(2)((operands, mods) => Instruction.FlatLoadDwordx3(operands(0), operands(1), mods))),
        489
      )
  case object FlatLoadDwordx4 extends Definition(
        Opcode.assume("flat_load_dwordx4"),
        decoder(form(2)((operands, mods) => Instruction.FlatLoadDwordx4(operands(0), operands(1), mods))),
        490
      )
  case object FlatLoadI16 extends Definition(
        Opcode.assume("flat_load_i16"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatLoadI16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatLoadI16_3(operands(0), operands(1), operands(2), mods))
        ),
        491
      )
  case object FlatLoadI8 extends Definition(
        Opcode.assume("flat_load_i8"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatLoadI8_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatLoadI8_3(operands(0), operands(1), operands(2), mods))
        ),
        492
      )
  case object FlatLoadMonitorB128 extends Definition(
        Opcode.assume("flat_load_monitor_b128"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatLoadMonitorB128_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatLoadMonitorB128_3(operands(0), operands(1), operands(2), mods))
        ),
        493
      )
  case object FlatLoadMonitorB32 extends Definition(
        Opcode.assume("flat_load_monitor_b32"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatLoadMonitorB32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatLoadMonitorB32_3(operands(0), operands(1), operands(2), mods))
        ),
        494
      )
  case object FlatLoadMonitorB64 extends Definition(
        Opcode.assume("flat_load_monitor_b64"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatLoadMonitorB64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatLoadMonitorB64_3(operands(0), operands(1), operands(2), mods))
        ),
        495
      )
  case object FlatLoadSbyte extends Definition(
        Opcode.assume("flat_load_sbyte"),
        decoder(form(2)((operands, mods) => Instruction.FlatLoadSbyte(operands(0), operands(1), mods))),
        496
      )
  case object FlatLoadSbyteD16 extends Definition(
        Opcode.assume("flat_load_sbyte_d16"),
        decoder(form(2)((operands, mods) => Instruction.FlatLoadSbyteD16(operands(0), operands(1), mods))),
        497
      )
  case object FlatLoadSbyteD16Hi extends Definition(
        Opcode.assume("flat_load_sbyte_d16_hi"),
        decoder(form(2)((operands, mods) => Instruction.FlatLoadSbyteD16Hi(operands(0), operands(1), mods))),
        498
      )
  case object FlatLoadShortD16 extends Definition(
        Opcode.assume("flat_load_short_d16"),
        decoder(form(2)((operands, mods) => Instruction.FlatLoadShortD16(operands(0), operands(1), mods))),
        499
      )
  case object FlatLoadShortD16Hi extends Definition(
        Opcode.assume("flat_load_short_d16_hi"),
        decoder(form(2)((operands, mods) => Instruction.FlatLoadShortD16Hi(operands(0), operands(1), mods))),
        500
      )
  case object FlatLoadSshort extends Definition(
        Opcode.assume("flat_load_sshort"),
        decoder(form(2)((operands, mods) => Instruction.FlatLoadSshort(operands(0), operands(1), mods))),
        501
      )
  case object FlatLoadU16 extends Definition(
        Opcode.assume("flat_load_u16"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatLoadU16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatLoadU16_3(operands(0), operands(1), operands(2), mods))
        ),
        502
      )
  case object FlatLoadU8 extends Definition(
        Opcode.assume("flat_load_u8"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatLoadU8_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatLoadU8_3(operands(0), operands(1), operands(2), mods))
        ),
        503
      )
  case object FlatLoadUbyte extends Definition(
        Opcode.assume("flat_load_ubyte"),
        decoder(form(2)((operands, mods) => Instruction.FlatLoadUbyte(operands(0), operands(1), mods))),
        504
      )
  case object FlatLoadUbyteD16 extends Definition(
        Opcode.assume("flat_load_ubyte_d16"),
        decoder(form(2)((operands, mods) => Instruction.FlatLoadUbyteD16(operands(0), operands(1), mods))),
        505
      )
  case object FlatLoadUbyteD16Hi extends Definition(
        Opcode.assume("flat_load_ubyte_d16_hi"),
        decoder(form(2)((operands, mods) => Instruction.FlatLoadUbyteD16Hi(operands(0), operands(1), mods))),
        506
      )
  case object FlatLoadUshort extends Definition(
        Opcode.assume("flat_load_ushort"),
        decoder(form(2)((operands, mods) => Instruction.FlatLoadUshort(operands(0), operands(1), mods))),
        507
      )
  case object FlatPrefetchB8 extends Definition(
        Opcode.assume("flat_prefetch_b8"),
        decoder(
          form(1)((operands, mods) => Instruction.FlatPrefetchB8_1(operands(0), mods)),
          form(2)((operands, mods) => Instruction.FlatPrefetchB8_2(operands(0), operands(1), mods))
        ),
        508
      )
  case object FlatStoreB128 extends Definition(
        Opcode.assume("flat_store_b128"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatStoreB128_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatStoreB128_3(operands(0), operands(1), operands(2), mods))
        ),
        509
      )
  case object FlatStoreB16 extends Definition(
        Opcode.assume("flat_store_b16"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatStoreB16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatStoreB16_3(operands(0), operands(1), operands(2), mods))
        ),
        510
      )
  case object FlatStoreB32 extends Definition(
        Opcode.assume("flat_store_b32"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatStoreB32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatStoreB32_3(operands(0), operands(1), operands(2), mods))
        ),
        511
      )
  case object FlatStoreB64 extends Definition(
        Opcode.assume("flat_store_b64"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatStoreB64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatStoreB64_3(operands(0), operands(1), operands(2), mods))
        ),
        512
      )
  case object FlatStoreB8 extends Definition(
        Opcode.assume("flat_store_b8"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatStoreB8_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatStoreB8_3(operands(0), operands(1), operands(2), mods))
        ),
        513
      )
  case object FlatStoreB96 extends Definition(
        Opcode.assume("flat_store_b96"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatStoreB96_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatStoreB96_3(operands(0), operands(1), operands(2), mods))
        ),
        514
      )
  case object FlatStoreByte extends Definition(
        Opcode.assume("flat_store_byte"),
        decoder(form(2)((operands, mods) => Instruction.FlatStoreByte(operands(0), operands(1), mods))),
        515
      )
  case object FlatStoreByteD16Hi extends Definition(
        Opcode.assume("flat_store_byte_d16_hi"),
        decoder(form(2)((operands, mods) => Instruction.FlatStoreByteD16Hi(operands(0), operands(1), mods))),
        516
      )
  case object FlatStoreD16HiB16 extends Definition(
        Opcode.assume("flat_store_d16_hi_b16"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatStoreD16HiB16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatStoreD16HiB16_3(operands(0), operands(1), operands(2), mods))
        ),
        517
      )
  case object FlatStoreD16HiB8 extends Definition(
        Opcode.assume("flat_store_d16_hi_b8"),
        decoder(
          form(2)((operands, mods) => Instruction.FlatStoreD16HiB8_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.FlatStoreD16HiB8_3(operands(0), operands(1), operands(2), mods))
        ),
        518
      )
  case object FlatStoreDword extends Definition(
        Opcode.assume("flat_store_dword"),
        decoder(form(2)((operands, mods) => Instruction.FlatStoreDword(operands(0), operands(1), mods))),
        519
      )
  case object FlatStoreDwordx2 extends Definition(
        Opcode.assume("flat_store_dwordx2"),
        decoder(form(2)((operands, mods) => Instruction.FlatStoreDwordx2(operands(0), operands(1), mods))),
        520
      )
  case object FlatStoreDwordx3 extends Definition(
        Opcode.assume("flat_store_dwordx3"),
        decoder(form(2)((operands, mods) => Instruction.FlatStoreDwordx3(operands(0), operands(1), mods))),
        521
      )
  case object FlatStoreDwordx4 extends Definition(
        Opcode.assume("flat_store_dwordx4"),
        decoder(form(2)((operands, mods) => Instruction.FlatStoreDwordx4(operands(0), operands(1), mods))),
        522
      )
  case object FlatStoreShort extends Definition(
        Opcode.assume("flat_store_short"),
        decoder(form(2)((operands, mods) => Instruction.FlatStoreShort(operands(0), operands(1), mods))),
        523
      )
  case object FlatStoreShortD16Hi extends Definition(
        Opcode.assume("flat_store_short_d16_hi"),
        decoder(form(2)((operands, mods) => Instruction.FlatStoreShortD16Hi(operands(0), operands(1), mods))),
        524
      )
  case object GlobalAtomicAdd extends Definition(
        Opcode.assume("global_atomic_add"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicAdd_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicAdd_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        525
      )
  case object GlobalAtomicAddF32 extends Definition(
        Opcode.assume("global_atomic_add_f32"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicAddF32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicAddF32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        526
      )
  case object GlobalAtomicAddF64 extends Definition(
        Opcode.assume("global_atomic_add_f64"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicAddF64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicAddF64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        527
      )
  case object GlobalAtomicAddU32 extends Definition(
        Opcode.assume("global_atomic_add_u32"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicAddU32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicAddU32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        528
      )
  case object GlobalAtomicAddU64 extends Definition(
        Opcode.assume("global_atomic_add_u64"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicAddU64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicAddU64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        529
      )
  case object GlobalAtomicAddX2 extends Definition(
        Opcode.assume("global_atomic_add_x2"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicAddX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicAddX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        530
      )
  case object GlobalAtomicAnd extends Definition(
        Opcode.assume("global_atomic_and"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicAnd_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicAnd_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        531
      )
  case object GlobalAtomicAndB32 extends Definition(
        Opcode.assume("global_atomic_and_b32"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicAndB32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicAndB32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        532
      )
  case object GlobalAtomicAndB64 extends Definition(
        Opcode.assume("global_atomic_and_b64"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicAndB64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicAndB64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        533
      )
  case object GlobalAtomicAndX2 extends Definition(
        Opcode.assume("global_atomic_and_x2"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicAndX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicAndX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        534
      )
  case object GlobalAtomicCmpswap extends Definition(
        Opcode.assume("global_atomic_cmpswap"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicCmpswap_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicCmpswap_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        535
      )
  case object GlobalAtomicCmpswapB32 extends Definition(
        Opcode.assume("global_atomic_cmpswap_b32"),
        decoder(
          form(3)((operands, mods) =>
            Instruction.GlobalAtomicCmpswapB32_3(operands(0), operands(1), operands(2), mods)
          ),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicCmpswapB32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        536
      )
  case object GlobalAtomicCmpswapB64 extends Definition(
        Opcode.assume("global_atomic_cmpswap_b64"),
        decoder(
          form(3)((operands, mods) =>
            Instruction.GlobalAtomicCmpswapB64_3(operands(0), operands(1), operands(2), mods)
          ),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicCmpswapB64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        537
      )
  case object GlobalAtomicCmpswapF32 extends Definition(
        Opcode.assume("global_atomic_cmpswap_f32"),
        decoder(
          form(3)((operands, mods) =>
            Instruction.GlobalAtomicCmpswapF32_3(operands(0), operands(1), operands(2), mods)
          ),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicCmpswapF32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        538
      )
  case object GlobalAtomicCmpswapX2 extends Definition(
        Opcode.assume("global_atomic_cmpswap_x2"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicCmpswapX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicCmpswapX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        539
      )
  case object GlobalAtomicCondSubU32 extends Definition(
        Opcode.assume("global_atomic_cond_sub_u32"),
        decoder(
          form(3)((operands, mods) =>
            Instruction.GlobalAtomicCondSubU32_3(operands(0), operands(1), operands(2), mods)
          ),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicCondSubU32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        540
      )
  case object GlobalAtomicCsub extends Definition(
        Opcode.assume("global_atomic_csub"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicCsub_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicCsub_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        541
      )
  case object GlobalAtomicCsubU32 extends Definition(
        Opcode.assume("global_atomic_csub_u32"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicCsubU32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicCsubU32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        542
      )
  case object GlobalAtomicDec extends Definition(
        Opcode.assume("global_atomic_dec"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicDec_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicDec_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        543
      )
  case object GlobalAtomicDecU32 extends Definition(
        Opcode.assume("global_atomic_dec_u32"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicDecU32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicDecU32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        544
      )
  case object GlobalAtomicDecU64 extends Definition(
        Opcode.assume("global_atomic_dec_u64"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicDecU64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicDecU64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        545
      )
  case object GlobalAtomicDecX2 extends Definition(
        Opcode.assume("global_atomic_dec_x2"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicDecX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicDecX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        546
      )
  case object GlobalAtomicFcmpswap extends Definition(
        Opcode.assume("global_atomic_fcmpswap"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicFcmpswap_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicFcmpswap_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        547
      )
  case object GlobalAtomicFcmpswapX2 extends Definition(
        Opcode.assume("global_atomic_fcmpswap_x2"),
        decoder(
          form(3)((operands, mods) =>
            Instruction.GlobalAtomicFcmpswapX2_3(operands(0), operands(1), operands(2), mods)
          ),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicFcmpswapX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        548
      )
  case object GlobalAtomicFmax extends Definition(
        Opcode.assume("global_atomic_fmax"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicFmax_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicFmax_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        549
      )
  case object GlobalAtomicFmaxX2 extends Definition(
        Opcode.assume("global_atomic_fmax_x2"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicFmaxX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicFmaxX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        550
      )
  case object GlobalAtomicFmin extends Definition(
        Opcode.assume("global_atomic_fmin"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicFmin_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicFmin_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        551
      )
  case object GlobalAtomicFminX2 extends Definition(
        Opcode.assume("global_atomic_fmin_x2"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicFminX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicFminX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        552
      )
  case object GlobalAtomicInc extends Definition(
        Opcode.assume("global_atomic_inc"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicInc_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicInc_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        553
      )
  case object GlobalAtomicIncU32 extends Definition(
        Opcode.assume("global_atomic_inc_u32"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicIncU32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicIncU32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        554
      )
  case object GlobalAtomicIncU64 extends Definition(
        Opcode.assume("global_atomic_inc_u64"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicIncU64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicIncU64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        555
      )
  case object GlobalAtomicIncX2 extends Definition(
        Opcode.assume("global_atomic_inc_x2"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicIncX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicIncX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        556
      )
  case object GlobalAtomicMaxF32 extends Definition(
        Opcode.assume("global_atomic_max_f32"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicMaxF32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicMaxF32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        557
      )
  case object GlobalAtomicMaxF64 extends Definition(
        Opcode.assume("global_atomic_max_f64"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicMaxF64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicMaxF64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        558
      )
  case object GlobalAtomicMaxI32 extends Definition(
        Opcode.assume("global_atomic_max_i32"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicMaxI32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicMaxI32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        559
      )
  case object GlobalAtomicMaxI64 extends Definition(
        Opcode.assume("global_atomic_max_i64"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicMaxI64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicMaxI64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        560
      )
  case object GlobalAtomicMaxNumF32 extends Definition(
        Opcode.assume("global_atomic_max_num_f32"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicMaxNumF32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicMaxNumF32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        561
      )
  case object GlobalAtomicMaxNumF64 extends Definition(
        Opcode.assume("global_atomic_max_num_f64"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicMaxNumF64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicMaxNumF64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        562
      )
  case object GlobalAtomicMaxU32 extends Definition(
        Opcode.assume("global_atomic_max_u32"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicMaxU32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicMaxU32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        563
      )
  case object GlobalAtomicMaxU64 extends Definition(
        Opcode.assume("global_atomic_max_u64"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicMaxU64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicMaxU64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        564
      )
  case object GlobalAtomicMinF32 extends Definition(
        Opcode.assume("global_atomic_min_f32"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicMinF32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicMinF32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        565
      )
  case object GlobalAtomicMinF64 extends Definition(
        Opcode.assume("global_atomic_min_f64"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicMinF64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicMinF64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        566
      )
  case object GlobalAtomicMinI32 extends Definition(
        Opcode.assume("global_atomic_min_i32"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicMinI32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicMinI32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        567
      )
  case object GlobalAtomicMinI64 extends Definition(
        Opcode.assume("global_atomic_min_i64"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicMinI64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicMinI64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        568
      )
  case object GlobalAtomicMinNumF32 extends Definition(
        Opcode.assume("global_atomic_min_num_f32"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicMinNumF32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicMinNumF32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        569
      )
  case object GlobalAtomicMinNumF64 extends Definition(
        Opcode.assume("global_atomic_min_num_f64"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicMinNumF64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicMinNumF64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        570
      )
  case object GlobalAtomicMinU32 extends Definition(
        Opcode.assume("global_atomic_min_u32"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicMinU32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicMinU32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        571
      )
  case object GlobalAtomicMinU64 extends Definition(
        Opcode.assume("global_atomic_min_u64"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicMinU64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicMinU64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        572
      )
  case object GlobalAtomicOr extends Definition(
        Opcode.assume("global_atomic_or"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicOr_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicOr_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        573
      )
  case object GlobalAtomicOrB32 extends Definition(
        Opcode.assume("global_atomic_or_b32"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicOrB32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicOrB32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        574
      )
  case object GlobalAtomicOrB64 extends Definition(
        Opcode.assume("global_atomic_or_b64"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicOrB64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicOrB64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        575
      )
  case object GlobalAtomicOrX2 extends Definition(
        Opcode.assume("global_atomic_or_x2"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicOrX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicOrX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        576
      )
  case object GlobalAtomicOrderedAddB64 extends Definition(
        Opcode.assume("global_atomic_ordered_add_b64"),
        decoder(
          form(3)((operands, mods) =>
            Instruction.GlobalAtomicOrderedAddB64_3(operands(0), operands(1), operands(2), mods)
          ),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicOrderedAddB64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        577
      )
  case object GlobalAtomicPkAddBf16 extends Definition(
        Opcode.assume("global_atomic_pk_add_bf16"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicPkAddBf16_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicPkAddBf16_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        578
      )
  case object GlobalAtomicPkAddF16 extends Definition(
        Opcode.assume("global_atomic_pk_add_f16"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicPkAddF16_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicPkAddF16_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        579
      )
  case object GlobalAtomicSmax extends Definition(
        Opcode.assume("global_atomic_smax"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicSmax_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicSmax_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        580
      )
  case object GlobalAtomicSmaxX2 extends Definition(
        Opcode.assume("global_atomic_smax_x2"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicSmaxX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicSmaxX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        581
      )
  case object GlobalAtomicSmin extends Definition(
        Opcode.assume("global_atomic_smin"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicSmin_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicSmin_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        582
      )
  case object GlobalAtomicSminX2 extends Definition(
        Opcode.assume("global_atomic_smin_x2"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicSminX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicSminX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        583
      )
  case object GlobalAtomicSub extends Definition(
        Opcode.assume("global_atomic_sub"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicSub_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicSub_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        584
      )
  case object GlobalAtomicSubClampU32 extends Definition(
        Opcode.assume("global_atomic_sub_clamp_u32"),
        decoder(
          form(3)((operands, mods) =>
            Instruction.GlobalAtomicSubClampU32_3(operands(0), operands(1), operands(2), mods)
          ),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicSubClampU32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        585
      )
  case object GlobalAtomicSubU32 extends Definition(
        Opcode.assume("global_atomic_sub_u32"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicSubU32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicSubU32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        586
      )
  case object GlobalAtomicSubU64 extends Definition(
        Opcode.assume("global_atomic_sub_u64"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicSubU64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicSubU64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        587
      )
  case object GlobalAtomicSubX2 extends Definition(
        Opcode.assume("global_atomic_sub_x2"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicSubX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicSubX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        588
      )
  case object GlobalAtomicSwap extends Definition(
        Opcode.assume("global_atomic_swap"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicSwap_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicSwap_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        589
      )
  case object GlobalAtomicSwapB32 extends Definition(
        Opcode.assume("global_atomic_swap_b32"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicSwapB32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicSwapB32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        590
      )
  case object GlobalAtomicSwapB64 extends Definition(
        Opcode.assume("global_atomic_swap_b64"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicSwapB64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicSwapB64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        591
      )
  case object GlobalAtomicSwapX2 extends Definition(
        Opcode.assume("global_atomic_swap_x2"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicSwapX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicSwapX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        592
      )
  case object GlobalAtomicUmax extends Definition(
        Opcode.assume("global_atomic_umax"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicUmax_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicUmax_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        593
      )
  case object GlobalAtomicUmaxX2 extends Definition(
        Opcode.assume("global_atomic_umax_x2"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicUmaxX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicUmaxX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        594
      )
  case object GlobalAtomicUmin extends Definition(
        Opcode.assume("global_atomic_umin"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicUmin_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicUmin_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        595
      )
  case object GlobalAtomicUminX2 extends Definition(
        Opcode.assume("global_atomic_umin_x2"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicUminX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicUminX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        596
      )
  case object GlobalAtomicXor extends Definition(
        Opcode.assume("global_atomic_xor"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicXor_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicXor_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        597
      )
  case object GlobalAtomicXorB32 extends Definition(
        Opcode.assume("global_atomic_xor_b32"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicXorB32_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicXorB32_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        598
      )
  case object GlobalAtomicXorB64 extends Definition(
        Opcode.assume("global_atomic_xor_b64"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicXorB64_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicXorB64_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        599
      )
  case object GlobalAtomicXorX2 extends Definition(
        Opcode.assume("global_atomic_xor_x2"),
        decoder(
          form(3)((operands, mods) => Instruction.GlobalAtomicXorX2_3(operands(0), operands(1), operands(2), mods)),
          form(4)((operands, mods) =>
            Instruction.GlobalAtomicXorX2_4(operands(0), operands(1), operands(2), operands(3), mods)
          )
        ),
        600
      )
  case object GlobalInv extends Definition(
        Opcode.assume("global_inv"),
        decoder(form(0)((_, mods) => Instruction.GlobalInv(mods))),
        601
      )
  case object GlobalLoadAddtidB32 extends Definition(
        Opcode.assume("global_load_addtid_b32"),
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadAddtidB32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadAddtidB32_3(operands(0), operands(1), operands(2), mods))
        ),
        602
      )
  case object GlobalLoadAsyncToLdsB128 extends Definition(
        Opcode.assume("global_load_async_to_lds_b128"),
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadAsyncToLdsB128_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) =>
            Instruction.GlobalLoadAsyncToLdsB128_3(operands(0), operands(1), operands(2), mods)
          )
        ),
        603
      )
  case object GlobalLoadAsyncToLdsB32 extends Definition(
        Opcode.assume("global_load_async_to_lds_b32"),
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadAsyncToLdsB32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) =>
            Instruction.GlobalLoadAsyncToLdsB32_3(operands(0), operands(1), operands(2), mods)
          )
        ),
        604
      )
  case object GlobalLoadAsyncToLdsB64 extends Definition(
        Opcode.assume("global_load_async_to_lds_b64"),
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadAsyncToLdsB64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) =>
            Instruction.GlobalLoadAsyncToLdsB64_3(operands(0), operands(1), operands(2), mods)
          )
        ),
        605
      )
  case object GlobalLoadAsyncToLdsB8 extends Definition(
        Opcode.assume("global_load_async_to_lds_b8"),
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadAsyncToLdsB8_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadAsyncToLdsB8_3(operands(0), operands(1), operands(2), mods))
        ),
        606
      )
  case object GlobalLoadB128 extends Definition(
        Opcode.assume("global_load_b128"),
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadB128_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadB128_3(operands(0), operands(1), operands(2), mods))
        ),
        607
      )
  case object GlobalLoadB32 extends Definition(
        Opcode.assume("global_load_b32"),
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadB32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadB32_3(operands(0), operands(1), operands(2), mods))
        ),
        608
      )
  case object GlobalLoadB64 extends Definition(
        Opcode.assume("global_load_b64"),
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadB64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadB64_3(operands(0), operands(1), operands(2), mods))
        ),
        609
      )
  case object GlobalLoadB96 extends Definition(
        Opcode.assume("global_load_b96"),
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadB96_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadB96_3(operands(0), operands(1), operands(2), mods))
        ),
        610
      )
  case object GlobalLoadBlock extends Definition(
        Opcode.assume("global_load_block"),
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadBlock_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadBlock_3(operands(0), operands(1), operands(2), mods))
        ),
        611
      )
  case object GlobalLoadD16B16 extends Definition(
        Opcode.assume("global_load_d16_b16"),
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadD16B16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadD16B16_3(operands(0), operands(1), operands(2), mods))
        ),
        612
      )
  case object GlobalLoadD16HiB16 extends Definition(
        Opcode.assume("global_load_d16_hi_b16"),
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadD16HiB16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadD16HiB16_3(operands(0), operands(1), operands(2), mods))
        ),
        613
      )
  case object GlobalLoadD16HiI8 extends Definition(
        Opcode.assume("global_load_d16_hi_i8"),
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadD16HiI8_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadD16HiI8_3(operands(0), operands(1), operands(2), mods))
        ),
        614
      )
  case object GlobalLoadD16HiU8 extends Definition(
        Opcode.assume("global_load_d16_hi_u8"),
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadD16HiU8_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadD16HiU8_3(operands(0), operands(1), operands(2), mods))
        ),
        615
      )
  case object GlobalLoadD16I8 extends Definition(
        Opcode.assume("global_load_d16_i8"),
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadD16I8_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadD16I8_3(operands(0), operands(1), operands(2), mods))
        ),
        616
      )
  case object GlobalLoadD16U8 extends Definition(
        Opcode.assume("global_load_d16_u8"),
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadD16U8_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadD16U8_3(operands(0), operands(1), operands(2), mods))
        ),
        617
      )
  case object GlobalLoadDword extends Definition(
        Opcode.assume("global_load_dword"),
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadDword_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadDword_3(operands(0), operands(1), operands(2), mods))
        ),
        618
      )
  case object GlobalLoadDwordAddtid extends Definition(
        Opcode.assume("global_load_dword_addtid"),
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadDwordAddtid_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadDwordAddtid_3(operands(0), operands(1), operands(2), mods))
        ),
        619
      )
  case object GlobalLoadDwordx2 extends Definition(
        Opcode.assume("global_load_dwordx2"),
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadDwordx2_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadDwordx2_3(operands(0), operands(1), operands(2), mods))
        ),
        620
      )
  case object GlobalLoadDwordx3 extends Definition(
        Opcode.assume("global_load_dwordx3"),
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadDwordx3_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadDwordx3_3(operands(0), operands(1), operands(2), mods))
        ),
        621
      )
  case object GlobalLoadDwordx4 extends Definition(
        Opcode.assume("global_load_dwordx4"),
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadDwordx4_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadDwordx4_3(operands(0), operands(1), operands(2), mods))
        ),
        622
      )
  case object GlobalLoadI16 extends Definition(
        Opcode.assume("global_load_i16"),
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadI16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadI16_3(operands(0), operands(1), operands(2), mods))
        ),
        623
      )
  case object GlobalLoadI8 extends Definition(
        Opcode.assume("global_load_i8"),
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadI8_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadI8_3(operands(0), operands(1), operands(2), mods))
        ),
        624
      )
  case object GlobalLoadLdsDword extends Definition(
        Opcode.assume("global_load_lds_dword"),
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadLdsDword_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadLdsDword_3(operands(0), operands(1), operands(2), mods))
        ),
        625
      )
  case object GlobalLoadLdsDwordx3 extends Definition(
        Opcode.assume("global_load_lds_dwordx3"),
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadLdsDwordx3_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadLdsDwordx3_3(operands(0), operands(1), operands(2), mods))
        ),
        626
      )
  case object GlobalLoadLdsDwordx4 extends Definition(
        Opcode.assume("global_load_lds_dwordx4"),
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadLdsDwordx4_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadLdsDwordx4_3(operands(0), operands(1), operands(2), mods))
        ),
        627
      )
  case object GlobalLoadLdsSbyte extends Definition(
        Opcode.assume("global_load_lds_sbyte"),
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadLdsSbyte_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadLdsSbyte_3(operands(0), operands(1), operands(2), mods))
        ),
        628
      )
  case object GlobalLoadLdsSshort extends Definition(
        Opcode.assume("global_load_lds_sshort"),
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadLdsSshort_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadLdsSshort_3(operands(0), operands(1), operands(2), mods))
        ),
        629
      )
  case object GlobalLoadLdsUbyte extends Definition(
        Opcode.assume("global_load_lds_ubyte"),
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadLdsUbyte_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadLdsUbyte_3(operands(0), operands(1), operands(2), mods))
        ),
        630
      )
  case object GlobalLoadLdsUshort extends Definition(
        Opcode.assume("global_load_lds_ushort"),
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadLdsUshort_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadLdsUshort_3(operands(0), operands(1), operands(2), mods))
        ),
        631
      )
  case object GlobalLoadMonitorB128 extends Definition(
        Opcode.assume("global_load_monitor_b128"),
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadMonitorB128_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadMonitorB128_3(operands(0), operands(1), operands(2), mods))
        ),
        632
      )
  case object GlobalLoadMonitorB32 extends Definition(
        Opcode.assume("global_load_monitor_b32"),
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadMonitorB32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadMonitorB32_3(operands(0), operands(1), operands(2), mods))
        ),
        633
      )
  case object GlobalLoadMonitorB64 extends Definition(
        Opcode.assume("global_load_monitor_b64"),
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadMonitorB64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadMonitorB64_3(operands(0), operands(1), operands(2), mods))
        ),
        634
      )
  case object GlobalLoadSbyte extends Definition(
        Opcode.assume("global_load_sbyte"),
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadSbyte_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadSbyte_3(operands(0), operands(1), operands(2), mods))
        ),
        635
      )
  case object GlobalLoadSbyteD16 extends Definition(
        Opcode.assume("global_load_sbyte_d16"),
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadSbyteD16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadSbyteD16_3(operands(0), operands(1), operands(2), mods))
        ),
        636
      )
  case object GlobalLoadSbyteD16Hi extends Definition(
        Opcode.assume("global_load_sbyte_d16_hi"),
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadSbyteD16Hi_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadSbyteD16Hi_3(operands(0), operands(1), operands(2), mods))
        ),
        637
      )
  case object GlobalLoadShortD16 extends Definition(
        Opcode.assume("global_load_short_d16"),
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadShortD16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadShortD16_3(operands(0), operands(1), operands(2), mods))
        ),
        638
      )
  case object GlobalLoadShortD16Hi extends Definition(
        Opcode.assume("global_load_short_d16_hi"),
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadShortD16Hi_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadShortD16Hi_3(operands(0), operands(1), operands(2), mods))
        ),
        639
      )
  case object GlobalLoadSshort extends Definition(
        Opcode.assume("global_load_sshort"),
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadSshort_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadSshort_3(operands(0), operands(1), operands(2), mods))
        ),
        640
      )
  case object GlobalLoadTr16B128 extends Definition(
        Opcode.assume("global_load_tr16_b128"),
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadTr16B128_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadTr16B128_3(operands(0), operands(1), operands(2), mods))
        ),
        641
      )
  case object GlobalLoadTr4B64 extends Definition(
        Opcode.assume("global_load_tr4_b64"),
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadTr4B64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadTr4B64_3(operands(0), operands(1), operands(2), mods))
        ),
        642
      )
  case object GlobalLoadTr6B96 extends Definition(
        Opcode.assume("global_load_tr6_b96"),
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadTr6B96_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadTr6B96_3(operands(0), operands(1), operands(2), mods))
        ),
        643
      )
  case object GlobalLoadTr8B64 extends Definition(
        Opcode.assume("global_load_tr8_b64"),
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadTr8B64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadTr8B64_3(operands(0), operands(1), operands(2), mods))
        ),
        644
      )
  case object GlobalLoadTrB128 extends Definition(
        Opcode.assume("global_load_tr_b128"),
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadTrB128_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadTrB128_3(operands(0), operands(1), operands(2), mods))
        ),
        645
      )
  case object GlobalLoadTrB64 extends Definition(
        Opcode.assume("global_load_tr_b64"),
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadTrB64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadTrB64_3(operands(0), operands(1), operands(2), mods))
        ),
        646
      )
  case object GlobalLoadU16 extends Definition(
        Opcode.assume("global_load_u16"),
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadU16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadU16_3(operands(0), operands(1), operands(2), mods))
        ),
        647
      )
  case object GlobalLoadU8 extends Definition(
        Opcode.assume("global_load_u8"),
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadU8_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadU8_3(operands(0), operands(1), operands(2), mods))
        ),
        648
      )
  case object GlobalLoadUbyte extends Definition(
        Opcode.assume("global_load_ubyte"),
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadUbyte_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadUbyte_3(operands(0), operands(1), operands(2), mods))
        ),
        649
      )
  case object GlobalLoadUbyteD16 extends Definition(
        Opcode.assume("global_load_ubyte_d16"),
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadUbyteD16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadUbyteD16_3(operands(0), operands(1), operands(2), mods))
        ),
        650
      )
  case object GlobalLoadUbyteD16Hi extends Definition(
        Opcode.assume("global_load_ubyte_d16_hi"),
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadUbyteD16Hi_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadUbyteD16Hi_3(operands(0), operands(1), operands(2), mods))
        ),
        651
      )
  case object GlobalLoadUshort extends Definition(
        Opcode.assume("global_load_ushort"),
        decoder(
          form(2)((operands, mods) => Instruction.GlobalLoadUshort_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.GlobalLoadUshort_3(operands(0), operands(1), operands(2), mods))
        ),
        652
      )
  case object GlobalPrefetchB8 extends Definition(
        Opcode.assume("global_prefetch_b8"),
        decoder(form(2)((operands, mods) => Instruction.GlobalPrefetchB8(operands(0), operands(1), mods))),
        653
      )
  case object GlobalStoreAddtidB32 extends Definition(
        Opcode.assume("global_store_addtid_b32"),
        decoder(form(2)((operands, mods) => Instruction.GlobalStoreAddtidB32(operands(0), operands(1), mods))),
        654
      )
  case object GlobalStoreAsyncFromLdsB128 extends Definition(
        Opcode.assume("global_store_async_from_lds_b128"),
        decoder(form(3)((operands, mods) =>
          Instruction.GlobalStoreAsyncFromLdsB128(operands(0), operands(1), operands(2), mods)
        )),
        655
      )
  case object GlobalStoreAsyncFromLdsB32 extends Definition(
        Opcode.assume("global_store_async_from_lds_b32"),
        decoder(form(3)((operands, mods) =>
          Instruction.GlobalStoreAsyncFromLdsB32(operands(0), operands(1), operands(2), mods)
        )),
        656
      )
  case object GlobalStoreAsyncFromLdsB64 extends Definition(
        Opcode.assume("global_store_async_from_lds_b64"),
        decoder(form(3)((operands, mods) =>
          Instruction.GlobalStoreAsyncFromLdsB64(operands(0), operands(1), operands(2), mods)
        )),
        657
      )
  case object GlobalStoreAsyncFromLdsB8 extends Definition(
        Opcode.assume("global_store_async_from_lds_b8"),
        decoder(form(3)((operands, mods) =>
          Instruction.GlobalStoreAsyncFromLdsB8(operands(0), operands(1), operands(2), mods)
        )),
        658
      )
  case object GlobalStoreB128 extends Definition(
        Opcode.assume("global_store_b128"),
        decoder(form(3)((operands, mods) => Instruction.GlobalStoreB128(operands(0), operands(1), operands(2), mods))),
        659
      )
  case object GlobalStoreB16 extends Definition(
        Opcode.assume("global_store_b16"),
        decoder(form(3)((operands, mods) => Instruction.GlobalStoreB16(operands(0), operands(1), operands(2), mods))),
        660
      )
  case object GlobalStoreB32 extends Definition(
        Opcode.assume("global_store_b32"),
        decoder(form(3)((operands, mods) => Instruction.GlobalStoreB32(operands(0), operands(1), operands(2), mods))),
        661
      )
  case object GlobalStoreB64 extends Definition(
        Opcode.assume("global_store_b64"),
        decoder(form(3)((operands, mods) => Instruction.GlobalStoreB64(operands(0), operands(1), operands(2), mods))),
        662
      )
  case object GlobalStoreB8 extends Definition(
        Opcode.assume("global_store_b8"),
        decoder(form(3)((operands, mods) => Instruction.GlobalStoreB8(operands(0), operands(1), operands(2), mods))),
        663
      )
  case object GlobalStoreB96 extends Definition(
        Opcode.assume("global_store_b96"),
        decoder(form(3)((operands, mods) => Instruction.GlobalStoreB96(operands(0), operands(1), operands(2), mods))),
        664
      )
  case object GlobalStoreBlock extends Definition(
        Opcode.assume("global_store_block"),
        decoder(form(3)((operands, mods) => Instruction.GlobalStoreBlock(operands(0), operands(1), operands(2), mods))),
        665
      )
  case object GlobalStoreByte extends Definition(
        Opcode.assume("global_store_byte"),
        decoder(form(3)((operands, mods) => Instruction.GlobalStoreByte(operands(0), operands(1), operands(2), mods))),
        666
      )
  case object GlobalStoreByteD16Hi extends Definition(
        Opcode.assume("global_store_byte_d16_hi"),
        decoder(form(3)((operands, mods) =>
          Instruction.GlobalStoreByteD16Hi(operands(0), operands(1), operands(2), mods)
        )),
        667
      )
  case object GlobalStoreD16HiB16 extends Definition(
        Opcode.assume("global_store_d16_hi_b16"),
        decoder(form(3)((operands, mods) =>
          Instruction.GlobalStoreD16HiB16(operands(0), operands(1), operands(2), mods)
        )),
        668
      )
  case object GlobalStoreD16HiB8 extends Definition(
        Opcode.assume("global_store_d16_hi_b8"),
        decoder(form(3)((operands, mods) =>
          Instruction.GlobalStoreD16HiB8(operands(0), operands(1), operands(2), mods)
        )),
        669
      )
  case object GlobalStoreDword extends Definition(
        Opcode.assume("global_store_dword"),
        decoder(form(3)((operands, mods) => Instruction.GlobalStoreDword(operands(0), operands(1), operands(2), mods))),
        670
      )
  case object GlobalStoreDwordAddtid extends Definition(
        Opcode.assume("global_store_dword_addtid"),
        decoder(form(2)((operands, mods) => Instruction.GlobalStoreDwordAddtid(operands(0), operands(1), mods))),
        671
      )
  case object GlobalStoreDwordx2 extends Definition(
        Opcode.assume("global_store_dwordx2"),
        decoder(form(3)((operands, mods) =>
          Instruction.GlobalStoreDwordx2(operands(0), operands(1), operands(2), mods)
        )),
        672
      )
  case object GlobalStoreDwordx3 extends Definition(
        Opcode.assume("global_store_dwordx3"),
        decoder(form(3)((operands, mods) =>
          Instruction.GlobalStoreDwordx3(operands(0), operands(1), operands(2), mods)
        )),
        673
      )
  case object GlobalStoreDwordx4 extends Definition(
        Opcode.assume("global_store_dwordx4"),
        decoder(form(3)((operands, mods) =>
          Instruction.GlobalStoreDwordx4(operands(0), operands(1), operands(2), mods)
        )),
        674
      )
  case object GlobalStoreShort extends Definition(
        Opcode.assume("global_store_short"),
        decoder(form(3)((operands, mods) => Instruction.GlobalStoreShort(operands(0), operands(1), operands(2), mods))),
        675
      )
  case object GlobalStoreShortD16Hi extends Definition(
        Opcode.assume("global_store_short_d16_hi"),
        decoder(form(3)((operands, mods) =>
          Instruction.GlobalStoreShortD16Hi(operands(0), operands(1), operands(2), mods)
        )),
        676
      )
  case object GlobalWb extends Definition(
        Opcode.assume("global_wb"),
        decoder(form(0)((_, mods) => Instruction.GlobalWb(mods))),
        677
      )
  case object GlobalWbinv extends Definition(
        Opcode.assume("global_wbinv"),
        decoder(form(0)((_, mods) => Instruction.GlobalWbinv(mods))),
        678
      )
  case object ScratchLoadB128 extends Definition(
        Opcode.assume("scratch_load_b128"),
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadB128_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadB128_3(operands(0), operands(1), operands(2), mods))
        ),
        1284
      )
  case object ScratchLoadB32 extends Definition(
        Opcode.assume("scratch_load_b32"),
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadB32_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadB32_3(operands(0), operands(1), operands(2), mods))
        ),
        1285
      )
  case object ScratchLoadB64 extends Definition(
        Opcode.assume("scratch_load_b64"),
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadB64_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadB64_3(operands(0), operands(1), operands(2), mods))
        ),
        1286
      )
  case object ScratchLoadB96 extends Definition(
        Opcode.assume("scratch_load_b96"),
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadB96_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadB96_3(operands(0), operands(1), operands(2), mods))
        ),
        1287
      )
  case object ScratchLoadBlock extends Definition(
        Opcode.assume("scratch_load_block"),
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadBlock_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadBlock_3(operands(0), operands(1), operands(2), mods))
        ),
        1288
      )
  case object ScratchLoadD16B16 extends Definition(
        Opcode.assume("scratch_load_d16_b16"),
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadD16B16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadD16B16_3(operands(0), operands(1), operands(2), mods))
        ),
        1289
      )
  case object ScratchLoadD16HiB16 extends Definition(
        Opcode.assume("scratch_load_d16_hi_b16"),
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadD16HiB16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadD16HiB16_3(operands(0), operands(1), operands(2), mods))
        ),
        1290
      )
  case object ScratchLoadD16HiI8 extends Definition(
        Opcode.assume("scratch_load_d16_hi_i8"),
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadD16HiI8_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadD16HiI8_3(operands(0), operands(1), operands(2), mods))
        ),
        1291
      )
  case object ScratchLoadD16HiU8 extends Definition(
        Opcode.assume("scratch_load_d16_hi_u8"),
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadD16HiU8_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadD16HiU8_3(operands(0), operands(1), operands(2), mods))
        ),
        1292
      )
  case object ScratchLoadD16I8 extends Definition(
        Opcode.assume("scratch_load_d16_i8"),
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadD16I8_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadD16I8_3(operands(0), operands(1), operands(2), mods))
        ),
        1293
      )
  case object ScratchLoadD16U8 extends Definition(
        Opcode.assume("scratch_load_d16_u8"),
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadD16U8_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadD16U8_3(operands(0), operands(1), operands(2), mods))
        ),
        1294
      )
  case object ScratchLoadDword extends Definition(
        Opcode.assume("scratch_load_dword"),
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadDword_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadDword_3(operands(0), operands(1), operands(2), mods))
        ),
        1295
      )
  case object ScratchLoadDwordx2 extends Definition(
        Opcode.assume("scratch_load_dwordx2"),
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadDwordx2_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadDwordx2_3(operands(0), operands(1), operands(2), mods))
        ),
        1296
      )
  case object ScratchLoadDwordx3 extends Definition(
        Opcode.assume("scratch_load_dwordx3"),
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadDwordx3_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadDwordx3_3(operands(0), operands(1), operands(2), mods))
        ),
        1297
      )
  case object ScratchLoadDwordx4 extends Definition(
        Opcode.assume("scratch_load_dwordx4"),
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadDwordx4_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadDwordx4_3(operands(0), operands(1), operands(2), mods))
        ),
        1298
      )
  case object ScratchLoadI16 extends Definition(
        Opcode.assume("scratch_load_i16"),
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadI16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadI16_3(operands(0), operands(1), operands(2), mods))
        ),
        1299
      )
  case object ScratchLoadI8 extends Definition(
        Opcode.assume("scratch_load_i8"),
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadI8_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadI8_3(operands(0), operands(1), operands(2), mods))
        ),
        1300
      )
  case object ScratchLoadLdsDword extends Definition(
        Opcode.assume("scratch_load_lds_dword"),
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadLdsDword_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadLdsDword_3(operands(0), operands(1), operands(2), mods))
        ),
        1301
      )
  case object ScratchLoadLdsSbyte extends Definition(
        Opcode.assume("scratch_load_lds_sbyte"),
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadLdsSbyte_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadLdsSbyte_3(operands(0), operands(1), operands(2), mods))
        ),
        1302
      )
  case object ScratchLoadLdsSshort extends Definition(
        Opcode.assume("scratch_load_lds_sshort"),
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadLdsSshort_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadLdsSshort_3(operands(0), operands(1), operands(2), mods))
        ),
        1303
      )
  case object ScratchLoadLdsUbyte extends Definition(
        Opcode.assume("scratch_load_lds_ubyte"),
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadLdsUbyte_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadLdsUbyte_3(operands(0), operands(1), operands(2), mods))
        ),
        1304
      )
  case object ScratchLoadLdsUshort extends Definition(
        Opcode.assume("scratch_load_lds_ushort"),
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadLdsUshort_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadLdsUshort_3(operands(0), operands(1), operands(2), mods))
        ),
        1305
      )
  case object ScratchLoadSbyte extends Definition(
        Opcode.assume("scratch_load_sbyte"),
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadSbyte_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadSbyte_3(operands(0), operands(1), operands(2), mods))
        ),
        1306
      )
  case object ScratchLoadSbyteD16 extends Definition(
        Opcode.assume("scratch_load_sbyte_d16"),
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadSbyteD16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadSbyteD16_3(operands(0), operands(1), operands(2), mods))
        ),
        1307
      )
  case object ScratchLoadSbyteD16Hi extends Definition(
        Opcode.assume("scratch_load_sbyte_d16_hi"),
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadSbyteD16Hi_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadSbyteD16Hi_3(operands(0), operands(1), operands(2), mods))
        ),
        1308
      )
  case object ScratchLoadShortD16 extends Definition(
        Opcode.assume("scratch_load_short_d16"),
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadShortD16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadShortD16_3(operands(0), operands(1), operands(2), mods))
        ),
        1309
      )
  case object ScratchLoadShortD16Hi extends Definition(
        Opcode.assume("scratch_load_short_d16_hi"),
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadShortD16Hi_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadShortD16Hi_3(operands(0), operands(1), operands(2), mods))
        ),
        1310
      )
  case object ScratchLoadSshort extends Definition(
        Opcode.assume("scratch_load_sshort"),
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadSshort_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadSshort_3(operands(0), operands(1), operands(2), mods))
        ),
        1311
      )
  case object ScratchLoadU16 extends Definition(
        Opcode.assume("scratch_load_u16"),
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadU16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadU16_3(operands(0), operands(1), operands(2), mods))
        ),
        1312
      )
  case object ScratchLoadU8 extends Definition(
        Opcode.assume("scratch_load_u8"),
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadU8_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadU8_3(operands(0), operands(1), operands(2), mods))
        ),
        1313
      )
  case object ScratchLoadUbyte extends Definition(
        Opcode.assume("scratch_load_ubyte"),
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadUbyte_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadUbyte_3(operands(0), operands(1), operands(2), mods))
        ),
        1314
      )
  case object ScratchLoadUbyteD16 extends Definition(
        Opcode.assume("scratch_load_ubyte_d16"),
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadUbyteD16_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadUbyteD16_3(operands(0), operands(1), operands(2), mods))
        ),
        1315
      )
  case object ScratchLoadUbyteD16Hi extends Definition(
        Opcode.assume("scratch_load_ubyte_d16_hi"),
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadUbyteD16Hi_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadUbyteD16Hi_3(operands(0), operands(1), operands(2), mods))
        ),
        1316
      )
  case object ScratchLoadUshort extends Definition(
        Opcode.assume("scratch_load_ushort"),
        decoder(
          form(2)((operands, mods) => Instruction.ScratchLoadUshort_2(operands(0), operands(1), mods)),
          form(3)((operands, mods) => Instruction.ScratchLoadUshort_3(operands(0), operands(1), operands(2), mods))
        ),
        1317
      )
  case object ScratchStoreB128 extends Definition(
        Opcode.assume("scratch_store_b128"),
        decoder(form(3)((operands, mods) => Instruction.ScratchStoreB128(operands(0), operands(1), operands(2), mods))),
        1318
      )
  case object ScratchStoreB16 extends Definition(
        Opcode.assume("scratch_store_b16"),
        decoder(form(3)((operands, mods) => Instruction.ScratchStoreB16(operands(0), operands(1), operands(2), mods))),
        1319
      )
  case object ScratchStoreB32 extends Definition(
        Opcode.assume("scratch_store_b32"),
        decoder(form(3)((operands, mods) => Instruction.ScratchStoreB32(operands(0), operands(1), operands(2), mods))),
        1320
      )
  case object ScratchStoreB64 extends Definition(
        Opcode.assume("scratch_store_b64"),
        decoder(form(3)((operands, mods) => Instruction.ScratchStoreB64(operands(0), operands(1), operands(2), mods))),
        1321
      )
  case object ScratchStoreB8 extends Definition(
        Opcode.assume("scratch_store_b8"),
        decoder(form(3)((operands, mods) => Instruction.ScratchStoreB8(operands(0), operands(1), operands(2), mods))),
        1322
      )
  case object ScratchStoreB96 extends Definition(
        Opcode.assume("scratch_store_b96"),
        decoder(form(3)((operands, mods) => Instruction.ScratchStoreB96(operands(0), operands(1), operands(2), mods))),
        1323
      )
  case object ScratchStoreBlock extends Definition(
        Opcode.assume("scratch_store_block"),
        decoder(form(3)((operands, mods) =>
          Instruction.ScratchStoreBlock(operands(0), operands(1), operands(2), mods)
        )),
        1324
      )
  case object ScratchStoreByte extends Definition(
        Opcode.assume("scratch_store_byte"),
        decoder(form(3)((operands, mods) => Instruction.ScratchStoreByte(operands(0), operands(1), operands(2), mods))),
        1325
      )
  case object ScratchStoreByteD16Hi extends Definition(
        Opcode.assume("scratch_store_byte_d16_hi"),
        decoder(form(3)((operands, mods) =>
          Instruction.ScratchStoreByteD16Hi(operands(0), operands(1), operands(2), mods)
        )),
        1326
      )
  case object ScratchStoreD16HiB16 extends Definition(
        Opcode.assume("scratch_store_d16_hi_b16"),
        decoder(form(3)((operands, mods) =>
          Instruction.ScratchStoreD16HiB16(operands(0), operands(1), operands(2), mods)
        )),
        1327
      )
  case object ScratchStoreD16HiB8 extends Definition(
        Opcode.assume("scratch_store_d16_hi_b8"),
        decoder(form(3)((operands, mods) =>
          Instruction.ScratchStoreD16HiB8(operands(0), operands(1), operands(2), mods)
        )),
        1328
      )
  case object ScratchStoreDword extends Definition(
        Opcode.assume("scratch_store_dword"),
        decoder(form(3)((operands, mods) =>
          Instruction.ScratchStoreDword(operands(0), operands(1), operands(2), mods)
        )),
        1329
      )
  case object ScratchStoreDwordx2 extends Definition(
        Opcode.assume("scratch_store_dwordx2"),
        decoder(form(3)((operands, mods) =>
          Instruction.ScratchStoreDwordx2(operands(0), operands(1), operands(2), mods)
        )),
        1330
      )
  case object ScratchStoreDwordx3 extends Definition(
        Opcode.assume("scratch_store_dwordx3"),
        decoder(form(3)((operands, mods) =>
          Instruction.ScratchStoreDwordx3(operands(0), operands(1), operands(2), mods)
        )),
        1331
      )
  case object ScratchStoreDwordx4 extends Definition(
        Opcode.assume("scratch_store_dwordx4"),
        decoder(form(3)((operands, mods) =>
          Instruction.ScratchStoreDwordx4(operands(0), operands(1), operands(2), mods)
        )),
        1332
      )
  case object ScratchStoreShort extends Definition(
        Opcode.assume("scratch_store_short"),
        decoder(form(3)((operands, mods) =>
          Instruction.ScratchStoreShort(operands(0), operands(1), operands(2), mods)
        )),
        1333
      )
  case object ScratchStoreShortD16Hi extends Definition(
        Opcode.assume("scratch_store_short_d16_hi"),
        decoder(form(3)((operands, mods) =>
          Instruction.ScratchStoreShortD16Hi(operands(0), operands(1), operands(2), mods)
        )),
        1334
      )

  val definitions: Vec[Definition] = Vec(
    ClusterLoadAsyncToLdsB128,
    ClusterLoadAsyncToLdsB32,
    ClusterLoadAsyncToLdsB64,
    ClusterLoadAsyncToLdsB8,
    ClusterLoadB128,
    ClusterLoadB32,
    ClusterLoadB64,
    FlatAtomicAdd,
    FlatAtomicAddF32,
    FlatAtomicAddF64,
    FlatAtomicAddU32,
    FlatAtomicAddU64,
    FlatAtomicAddX2,
    FlatAtomicAnd,
    FlatAtomicAndB32,
    FlatAtomicAndB64,
    FlatAtomicAndX2,
    FlatAtomicCmpswap,
    FlatAtomicCmpswapB32,
    FlatAtomicCmpswapB64,
    FlatAtomicCmpswapF32,
    FlatAtomicCmpswapX2,
    FlatAtomicCondSubU32,
    FlatAtomicDec,
    FlatAtomicDecU32,
    FlatAtomicDecU64,
    FlatAtomicDecX2,
    FlatAtomicFcmpswap,
    FlatAtomicFcmpswapX2,
    FlatAtomicFmax,
    FlatAtomicFmaxX2,
    FlatAtomicFmin,
    FlatAtomicFminX2,
    FlatAtomicInc,
    FlatAtomicIncU32,
    FlatAtomicIncU64,
    FlatAtomicIncX2,
    FlatAtomicMaxF32,
    FlatAtomicMaxF64,
    FlatAtomicMaxI32,
    FlatAtomicMaxI64,
    FlatAtomicMaxNumF32,
    FlatAtomicMaxNumF64,
    FlatAtomicMaxU32,
    FlatAtomicMaxU64,
    FlatAtomicMinF32,
    FlatAtomicMinF64,
    FlatAtomicMinI32,
    FlatAtomicMinI64,
    FlatAtomicMinNumF32,
    FlatAtomicMinNumF64,
    FlatAtomicMinU32,
    FlatAtomicMinU64,
    FlatAtomicOr,
    FlatAtomicOrB32,
    FlatAtomicOrB64,
    FlatAtomicOrX2,
    FlatAtomicPkAddBf16,
    FlatAtomicPkAddF16,
    FlatAtomicSmax,
    FlatAtomicSmaxX2,
    FlatAtomicSmin,
    FlatAtomicSminX2,
    FlatAtomicSub,
    FlatAtomicSubClampU32,
    FlatAtomicSubU32,
    FlatAtomicSubU64,
    FlatAtomicSubX2,
    FlatAtomicSwap,
    FlatAtomicSwapB32,
    FlatAtomicSwapB64,
    FlatAtomicSwapX2,
    FlatAtomicUmax,
    FlatAtomicUmaxX2,
    FlatAtomicUmin,
    FlatAtomicUminX2,
    FlatAtomicXor,
    FlatAtomicXorB32,
    FlatAtomicXorB64,
    FlatAtomicXorX2,
    FlatLoadB128,
    FlatLoadB32,
    FlatLoadB64,
    FlatLoadB96,
    FlatLoadD16B16,
    FlatLoadD16HiB16,
    FlatLoadD16HiI8,
    FlatLoadD16HiU8,
    FlatLoadD16I8,
    FlatLoadD16U8,
    FlatLoadDword,
    FlatLoadDwordx2,
    FlatLoadDwordx3,
    FlatLoadDwordx4,
    FlatLoadI16,
    FlatLoadI8,
    FlatLoadMonitorB128,
    FlatLoadMonitorB32,
    FlatLoadMonitorB64,
    FlatLoadSbyte,
    FlatLoadSbyteD16,
    FlatLoadSbyteD16Hi,
    FlatLoadShortD16,
    FlatLoadShortD16Hi,
    FlatLoadSshort,
    FlatLoadU16,
    FlatLoadU8,
    FlatLoadUbyte,
    FlatLoadUbyteD16,
    FlatLoadUbyteD16Hi,
    FlatLoadUshort,
    FlatPrefetchB8,
    FlatStoreB128,
    FlatStoreB16,
    FlatStoreB32,
    FlatStoreB64,
    FlatStoreB8,
    FlatStoreB96,
    FlatStoreByte,
    FlatStoreByteD16Hi,
    FlatStoreD16HiB16,
    FlatStoreD16HiB8,
    FlatStoreDword,
    FlatStoreDwordx2,
    FlatStoreDwordx3,
    FlatStoreDwordx4,
    FlatStoreShort,
    FlatStoreShortD16Hi,
    GlobalAtomicAdd,
    GlobalAtomicAddF32,
    GlobalAtomicAddF64,
    GlobalAtomicAddU32,
    GlobalAtomicAddU64,
    GlobalAtomicAddX2,
    GlobalAtomicAnd,
    GlobalAtomicAndB32,
    GlobalAtomicAndB64,
    GlobalAtomicAndX2,
    GlobalAtomicCmpswap,
    GlobalAtomicCmpswapB32,
    GlobalAtomicCmpswapB64,
    GlobalAtomicCmpswapF32,
    GlobalAtomicCmpswapX2,
    GlobalAtomicCondSubU32,
    GlobalAtomicCsub,
    GlobalAtomicCsubU32,
    GlobalAtomicDec,
    GlobalAtomicDecU32,
    GlobalAtomicDecU64,
    GlobalAtomicDecX2,
    GlobalAtomicFcmpswap,
    GlobalAtomicFcmpswapX2,
    GlobalAtomicFmax,
    GlobalAtomicFmaxX2,
    GlobalAtomicFmin,
    GlobalAtomicFminX2,
    GlobalAtomicInc,
    GlobalAtomicIncU32,
    GlobalAtomicIncU64,
    GlobalAtomicIncX2,
    GlobalAtomicMaxF32,
    GlobalAtomicMaxF64,
    GlobalAtomicMaxI32,
    GlobalAtomicMaxI64,
    GlobalAtomicMaxNumF32,
    GlobalAtomicMaxNumF64,
    GlobalAtomicMaxU32,
    GlobalAtomicMaxU64,
    GlobalAtomicMinF32,
    GlobalAtomicMinF64,
    GlobalAtomicMinI32,
    GlobalAtomicMinI64,
    GlobalAtomicMinNumF32,
    GlobalAtomicMinNumF64,
    GlobalAtomicMinU32,
    GlobalAtomicMinU64,
    GlobalAtomicOr,
    GlobalAtomicOrB32,
    GlobalAtomicOrB64,
    GlobalAtomicOrX2,
    GlobalAtomicOrderedAddB64,
    GlobalAtomicPkAddBf16,
    GlobalAtomicPkAddF16,
    GlobalAtomicSmax,
    GlobalAtomicSmaxX2,
    GlobalAtomicSmin,
    GlobalAtomicSminX2,
    GlobalAtomicSub,
    GlobalAtomicSubClampU32,
    GlobalAtomicSubU32,
    GlobalAtomicSubU64,
    GlobalAtomicSubX2,
    GlobalAtomicSwap,
    GlobalAtomicSwapB32,
    GlobalAtomicSwapB64,
    GlobalAtomicSwapX2,
    GlobalAtomicUmax,
    GlobalAtomicUmaxX2,
    GlobalAtomicUmin,
    GlobalAtomicUminX2,
    GlobalAtomicXor,
    GlobalAtomicXorB32,
    GlobalAtomicXorB64,
    GlobalAtomicXorX2,
    GlobalInv,
    GlobalLoadAddtidB32,
    GlobalLoadAsyncToLdsB128,
    GlobalLoadAsyncToLdsB32,
    GlobalLoadAsyncToLdsB64,
    GlobalLoadAsyncToLdsB8,
    GlobalLoadB128,
    GlobalLoadB32,
    GlobalLoadB64,
    GlobalLoadB96,
    GlobalLoadBlock,
    GlobalLoadD16B16,
    GlobalLoadD16HiB16,
    GlobalLoadD16HiI8,
    GlobalLoadD16HiU8,
    GlobalLoadD16I8,
    GlobalLoadD16U8,
    GlobalLoadDword,
    GlobalLoadDwordAddtid,
    GlobalLoadDwordx2,
    GlobalLoadDwordx3,
    GlobalLoadDwordx4,
    GlobalLoadI16,
    GlobalLoadI8,
    GlobalLoadLdsDword,
    GlobalLoadLdsDwordx3,
    GlobalLoadLdsDwordx4,
    GlobalLoadLdsSbyte,
    GlobalLoadLdsSshort,
    GlobalLoadLdsUbyte,
    GlobalLoadLdsUshort,
    GlobalLoadMonitorB128,
    GlobalLoadMonitorB32,
    GlobalLoadMonitorB64,
    GlobalLoadSbyte,
    GlobalLoadSbyteD16,
    GlobalLoadSbyteD16Hi,
    GlobalLoadShortD16,
    GlobalLoadShortD16Hi,
    GlobalLoadSshort,
    GlobalLoadTr16B128,
    GlobalLoadTr4B64,
    GlobalLoadTr6B96,
    GlobalLoadTr8B64,
    GlobalLoadTrB128,
    GlobalLoadTrB64,
    GlobalLoadU16,
    GlobalLoadU8,
    GlobalLoadUbyte,
    GlobalLoadUbyteD16,
    GlobalLoadUbyteD16Hi,
    GlobalLoadUshort,
    GlobalPrefetchB8,
    GlobalStoreAddtidB32,
    GlobalStoreAsyncFromLdsB128,
    GlobalStoreAsyncFromLdsB32,
    GlobalStoreAsyncFromLdsB64,
    GlobalStoreAsyncFromLdsB8,
    GlobalStoreB128,
    GlobalStoreB16,
    GlobalStoreB32,
    GlobalStoreB64,
    GlobalStoreB8,
    GlobalStoreB96,
    GlobalStoreBlock,
    GlobalStoreByte,
    GlobalStoreByteD16Hi,
    GlobalStoreD16HiB16,
    GlobalStoreD16HiB8,
    GlobalStoreDword,
    GlobalStoreDwordAddtid,
    GlobalStoreDwordx2,
    GlobalStoreDwordx3,
    GlobalStoreDwordx4,
    GlobalStoreShort,
    GlobalStoreShortD16Hi,
    GlobalWb,
    GlobalWbinv,
    ScratchLoadB128,
    ScratchLoadB32,
    ScratchLoadB64,
    ScratchLoadB96,
    ScratchLoadBlock,
    ScratchLoadD16B16,
    ScratchLoadD16HiB16,
    ScratchLoadD16HiI8,
    ScratchLoadD16HiU8,
    ScratchLoadD16I8,
    ScratchLoadD16U8,
    ScratchLoadDword,
    ScratchLoadDwordx2,
    ScratchLoadDwordx3,
    ScratchLoadDwordx4,
    ScratchLoadI16,
    ScratchLoadI8,
    ScratchLoadLdsDword,
    ScratchLoadLdsSbyte,
    ScratchLoadLdsSshort,
    ScratchLoadLdsUbyte,
    ScratchLoadLdsUshort,
    ScratchLoadSbyte,
    ScratchLoadSbyteD16,
    ScratchLoadSbyteD16Hi,
    ScratchLoadShortD16,
    ScratchLoadShortD16Hi,
    ScratchLoadSshort,
    ScratchLoadU16,
    ScratchLoadU8,
    ScratchLoadUbyte,
    ScratchLoadUbyteD16,
    ScratchLoadUbyteD16Hi,
    ScratchLoadUshort,
    ScratchStoreB128,
    ScratchStoreB16,
    ScratchStoreB32,
    ScratchStoreB64,
    ScratchStoreB8,
    ScratchStoreB96,
    ScratchStoreBlock,
    ScratchStoreByte,
    ScratchStoreByteD16Hi,
    ScratchStoreD16HiB16,
    ScratchStoreD16HiB8,
    ScratchStoreDword,
    ScratchStoreDwordx2,
    ScratchStoreDwordx3,
    ScratchStoreDwordx4,
    ScratchStoreShort,
    ScratchStoreShortD16Hi
  )
}
