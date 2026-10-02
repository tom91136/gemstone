// Generated from the pinned AMDGPU instruction forms. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu.asm
import net.kurobako.gemstone.isa.amdgpu.*
import Instruction.*
private object FlatInstructionForms {
  val decoders: Map[Opcode, Decoder] = Map(
    FlatInstructionDefs.ClusterLoadAsyncToLdsB128 -> decoder(form(3)((operands, mods) =>
      Instruction.ClusterLoadAsyncToLdsB128(operands(0), operands(1), operands(2), mods)
    )),
    FlatInstructionDefs.ClusterLoadAsyncToLdsB32 -> decoder(form(3)((operands, mods) =>
      Instruction.ClusterLoadAsyncToLdsB32(operands(0), operands(1), operands(2), mods)
    )),
    FlatInstructionDefs.ClusterLoadAsyncToLdsB64 -> decoder(form(3)((operands, mods) =>
      Instruction.ClusterLoadAsyncToLdsB64(operands(0), operands(1), operands(2), mods)
    )),
    FlatInstructionDefs.ClusterLoadAsyncToLdsB8 -> decoder(form(3)((operands, mods) =>
      Instruction.ClusterLoadAsyncToLdsB8(operands(0), operands(1), operands(2), mods)
    )),
    FlatInstructionDefs.ClusterLoadB128 ->
      decoder(form(3)((operands, mods) => Instruction.ClusterLoadB128(operands(0), operands(1), operands(2), mods))),
    FlatInstructionDefs.ClusterLoadB32 ->
      decoder(form(3)((operands, mods) => Instruction.ClusterLoadB32(operands(0), operands(1), operands(2), mods))),
    FlatInstructionDefs.ClusterLoadB64 ->
      decoder(form(3)((operands, mods) => Instruction.ClusterLoadB64(operands(0), operands(1), operands(2), mods))),
    FlatInstructionDefs.FlatAtomicAdd -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicAdd_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicAdd_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) => Instruction.FlatAtomicAdd_4(operands(0), operands(1), operands(2), operands(3), mods))
    ),
    FlatInstructionDefs.FlatAtomicAddF32 -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicAddF32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicAddF32_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicAddF32_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicAddF64 -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicAddF64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicAddF64_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicAddF64_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicAddU32 -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicAddU32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicAddU32_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicAddU32_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicAddU64 -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicAddU64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicAddU64_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicAddU64_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicAddX2 -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicAddX2_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicAddX2_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicAddX2_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicAnd -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicAnd_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicAnd_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) => Instruction.FlatAtomicAnd_4(operands(0), operands(1), operands(2), operands(3), mods))
    ),
    FlatInstructionDefs.FlatAtomicAndB32 -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicAndB32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicAndB32_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicAndB32_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicAndB64 -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicAndB64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicAndB64_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicAndB64_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicAndX2 -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicAndX2_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicAndX2_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicAndX2_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicCmpswap -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicCmpswap_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicCmpswap_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicCmpswap_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicCmpswapB32 -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicCmpswapB32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicCmpswapB32_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicCmpswapB32_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicCmpswapB64 -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicCmpswapB64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicCmpswapB64_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicCmpswapB64_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicCmpswapF32 -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicCmpswapF32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicCmpswapF32_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicCmpswapF32_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicCmpswapX2 -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicCmpswapX2_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicCmpswapX2_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicCmpswapX2_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicCondSubU32 -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicCondSubU32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicCondSubU32_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicCondSubU32_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicDec -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicDec_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicDec_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) => Instruction.FlatAtomicDec_4(operands(0), operands(1), operands(2), operands(3), mods))
    ),
    FlatInstructionDefs.FlatAtomicDecU32 -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicDecU32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicDecU32_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicDecU32_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicDecU64 -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicDecU64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicDecU64_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicDecU64_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicDecX2 -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicDecX2_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicDecX2_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicDecX2_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicFcmpswap -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicFcmpswap_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicFcmpswap_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicFcmpswap_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicFcmpswapX2 -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicFcmpswapX2_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicFcmpswapX2_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicFcmpswapX2_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicFmax -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicFmax_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicFmax_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicFmax_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicFmaxX2 -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicFmaxX2_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicFmaxX2_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicFmaxX2_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicFmin -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicFmin_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicFmin_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicFmin_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicFminX2 -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicFminX2_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicFminX2_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicFminX2_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicInc -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicInc_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicInc_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) => Instruction.FlatAtomicInc_4(operands(0), operands(1), operands(2), operands(3), mods))
    ),
    FlatInstructionDefs.FlatAtomicIncU32 -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicIncU32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicIncU32_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicIncU32_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicIncU64 -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicIncU64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicIncU64_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicIncU64_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicIncX2 -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicIncX2_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicIncX2_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicIncX2_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicMaxF32 -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicMaxF32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicMaxF32_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicMaxF32_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicMaxF64 -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicMaxF64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicMaxF64_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicMaxF64_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicMaxI32 -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicMaxI32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicMaxI32_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicMaxI32_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicMaxI64 -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicMaxI64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicMaxI64_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicMaxI64_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicMaxNumF32 -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicMaxNumF32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicMaxNumF32_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicMaxNumF32_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicMaxNumF64 -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicMaxNumF64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicMaxNumF64_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicMaxNumF64_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicMaxU32 -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicMaxU32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicMaxU32_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicMaxU32_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicMaxU64 -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicMaxU64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicMaxU64_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicMaxU64_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicMinF32 -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicMinF32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicMinF32_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicMinF32_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicMinF64 -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicMinF64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicMinF64_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicMinF64_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicMinI32 -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicMinI32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicMinI32_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicMinI32_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicMinI64 -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicMinI64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicMinI64_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicMinI64_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicMinNumF32 -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicMinNumF32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicMinNumF32_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicMinNumF32_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicMinNumF64 -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicMinNumF64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicMinNumF64_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicMinNumF64_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicMinU32 -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicMinU32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicMinU32_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicMinU32_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicMinU64 -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicMinU64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicMinU64_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicMinU64_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicOr -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicOr_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicOr_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) => Instruction.FlatAtomicOr_4(operands(0), operands(1), operands(2), operands(3), mods))
    ),
    FlatInstructionDefs.FlatAtomicOrB32 -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicOrB32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicOrB32_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicOrB32_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicOrB64 -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicOrB64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicOrB64_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicOrB64_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicOrX2 -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicOrX2_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicOrX2_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicOrX2_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicPkAddBf16 -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicPkAddBf16_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicPkAddBf16_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicPkAddBf16_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicPkAddF16 -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicPkAddF16_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicPkAddF16_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicPkAddF16_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicSmax -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicSmax_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicSmax_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicSmax_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicSmaxX2 -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicSmaxX2_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicSmaxX2_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicSmaxX2_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicSmin -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicSmin_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicSmin_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicSmin_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicSminX2 -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicSminX2_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicSminX2_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicSminX2_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicSub -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicSub_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicSub_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) => Instruction.FlatAtomicSub_4(operands(0), operands(1), operands(2), operands(3), mods))
    ),
    FlatInstructionDefs.FlatAtomicSubClampU32 -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicSubClampU32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicSubClampU32_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicSubClampU32_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicSubU32 -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicSubU32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicSubU32_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicSubU32_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicSubU64 -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicSubU64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicSubU64_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicSubU64_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicSubX2 -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicSubX2_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicSubX2_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicSubX2_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicSwap -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicSwap_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicSwap_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicSwap_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicSwapB32 -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicSwapB32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicSwapB32_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicSwapB32_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicSwapB64 -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicSwapB64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicSwapB64_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicSwapB64_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicSwapX2 -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicSwapX2_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicSwapX2_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicSwapX2_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicUmax -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicUmax_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicUmax_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicUmax_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicUmaxX2 -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicUmaxX2_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicUmaxX2_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicUmaxX2_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicUmin -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicUmin_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicUmin_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicUmin_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicUminX2 -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicUminX2_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicUminX2_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicUminX2_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicXor -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicXor_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicXor_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) => Instruction.FlatAtomicXor_4(operands(0), operands(1), operands(2), operands(3), mods))
    ),
    FlatInstructionDefs.FlatAtomicXorB32 -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicXorB32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicXorB32_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicXorB32_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicXorB64 -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicXorB64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicXorB64_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicXorB64_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatAtomicXorX2 -> decoder(
      form(2)((operands, mods) => Instruction.FlatAtomicXorX2_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatAtomicXorX2_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.FlatAtomicXorX2_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.FlatLoadB128 -> decoder(
      form(2)((operands, mods) => Instruction.FlatLoadB128_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatLoadB128_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.FlatLoadB32 -> decoder(
      form(2)((operands, mods) => Instruction.FlatLoadB32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatLoadB32_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.FlatLoadB64 -> decoder(
      form(2)((operands, mods) => Instruction.FlatLoadB64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatLoadB64_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.FlatLoadB96 -> decoder(
      form(2)((operands, mods) => Instruction.FlatLoadB96_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatLoadB96_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.FlatLoadD16B16 -> decoder(
      form(2)((operands, mods) => Instruction.FlatLoadD16B16_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatLoadD16B16_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.FlatLoadD16HiB16 -> decoder(
      form(2)((operands, mods) => Instruction.FlatLoadD16HiB16_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatLoadD16HiB16_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.FlatLoadD16HiI8 -> decoder(
      form(2)((operands, mods) => Instruction.FlatLoadD16HiI8_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatLoadD16HiI8_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.FlatLoadD16HiU8 -> decoder(
      form(2)((operands, mods) => Instruction.FlatLoadD16HiU8_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatLoadD16HiU8_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.FlatLoadD16I8 -> decoder(
      form(2)((operands, mods) => Instruction.FlatLoadD16I8_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatLoadD16I8_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.FlatLoadD16U8 -> decoder(
      form(2)((operands, mods) => Instruction.FlatLoadD16U8_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatLoadD16U8_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.FlatLoadDword ->
      decoder(form(2)((operands, mods) => Instruction.FlatLoadDword(operands(0), operands(1), mods))),
    FlatInstructionDefs.FlatLoadDwordx2 ->
      decoder(form(2)((operands, mods) => Instruction.FlatLoadDwordx2(operands(0), operands(1), mods))),
    FlatInstructionDefs.FlatLoadDwordx3 ->
      decoder(form(2)((operands, mods) => Instruction.FlatLoadDwordx3(operands(0), operands(1), mods))),
    FlatInstructionDefs.FlatLoadDwordx4 ->
      decoder(form(2)((operands, mods) => Instruction.FlatLoadDwordx4(operands(0), operands(1), mods))),
    FlatInstructionDefs.FlatLoadI16 -> decoder(
      form(2)((operands, mods) => Instruction.FlatLoadI16_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatLoadI16_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.FlatLoadI8 -> decoder(
      form(2)((operands, mods) => Instruction.FlatLoadI8_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatLoadI8_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.FlatLoadMonitorB128 -> decoder(
      form(2)((operands, mods) => Instruction.FlatLoadMonitorB128_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatLoadMonitorB128_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.FlatLoadMonitorB32 -> decoder(
      form(2)((operands, mods) => Instruction.FlatLoadMonitorB32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatLoadMonitorB32_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.FlatLoadMonitorB64 -> decoder(
      form(2)((operands, mods) => Instruction.FlatLoadMonitorB64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatLoadMonitorB64_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.FlatLoadSbyte ->
      decoder(form(2)((operands, mods) => Instruction.FlatLoadSbyte(operands(0), operands(1), mods))),
    FlatInstructionDefs.FlatLoadSbyteD16 ->
      decoder(form(2)((operands, mods) => Instruction.FlatLoadSbyteD16(operands(0), operands(1), mods))),
    FlatInstructionDefs.FlatLoadSbyteD16Hi ->
      decoder(form(2)((operands, mods) => Instruction.FlatLoadSbyteD16Hi(operands(0), operands(1), mods))),
    FlatInstructionDefs.FlatLoadShortD16 ->
      decoder(form(2)((operands, mods) => Instruction.FlatLoadShortD16(operands(0), operands(1), mods))),
    FlatInstructionDefs.FlatLoadShortD16Hi ->
      decoder(form(2)((operands, mods) => Instruction.FlatLoadShortD16Hi(operands(0), operands(1), mods))),
    FlatInstructionDefs.FlatLoadSshort ->
      decoder(form(2)((operands, mods) => Instruction.FlatLoadSshort(operands(0), operands(1), mods))),
    FlatInstructionDefs.FlatLoadU16 -> decoder(
      form(2)((operands, mods) => Instruction.FlatLoadU16_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatLoadU16_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.FlatLoadU8 -> decoder(
      form(2)((operands, mods) => Instruction.FlatLoadU8_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatLoadU8_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.FlatLoadUbyte ->
      decoder(form(2)((operands, mods) => Instruction.FlatLoadUbyte(operands(0), operands(1), mods))),
    FlatInstructionDefs.FlatLoadUbyteD16 ->
      decoder(form(2)((operands, mods) => Instruction.FlatLoadUbyteD16(operands(0), operands(1), mods))),
    FlatInstructionDefs.FlatLoadUbyteD16Hi ->
      decoder(form(2)((operands, mods) => Instruction.FlatLoadUbyteD16Hi(operands(0), operands(1), mods))),
    FlatInstructionDefs.FlatLoadUshort ->
      decoder(form(2)((operands, mods) => Instruction.FlatLoadUshort(operands(0), operands(1), mods))),
    FlatInstructionDefs.FlatPrefetchB8 -> decoder(
      form(1)((operands, mods) => Instruction.FlatPrefetchB8_1(operands(0), mods)),
      form(2)((operands, mods) => Instruction.FlatPrefetchB8_2(operands(0), operands(1), mods))
    ),
    FlatInstructionDefs.FlatStoreB128 -> decoder(
      form(2)((operands, mods) => Instruction.FlatStoreB128_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatStoreB128_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.FlatStoreB16 -> decoder(
      form(2)((operands, mods) => Instruction.FlatStoreB16_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatStoreB16_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.FlatStoreB32 -> decoder(
      form(2)((operands, mods) => Instruction.FlatStoreB32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatStoreB32_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.FlatStoreB64 -> decoder(
      form(2)((operands, mods) => Instruction.FlatStoreB64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatStoreB64_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.FlatStoreB8 -> decoder(
      form(2)((operands, mods) => Instruction.FlatStoreB8_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatStoreB8_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.FlatStoreB96 -> decoder(
      form(2)((operands, mods) => Instruction.FlatStoreB96_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatStoreB96_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.FlatStoreByte ->
      decoder(form(2)((operands, mods) => Instruction.FlatStoreByte(operands(0), operands(1), mods))),
    FlatInstructionDefs.FlatStoreByteD16Hi ->
      decoder(form(2)((operands, mods) => Instruction.FlatStoreByteD16Hi(operands(0), operands(1), mods))),
    FlatInstructionDefs.FlatStoreD16HiB16 -> decoder(
      form(2)((operands, mods) => Instruction.FlatStoreD16HiB16_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatStoreD16HiB16_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.FlatStoreD16HiB8 -> decoder(
      form(2)((operands, mods) => Instruction.FlatStoreD16HiB8_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.FlatStoreD16HiB8_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.FlatStoreDword ->
      decoder(form(2)((operands, mods) => Instruction.FlatStoreDword(operands(0), operands(1), mods))),
    FlatInstructionDefs.FlatStoreDwordx2 ->
      decoder(form(2)((operands, mods) => Instruction.FlatStoreDwordx2(operands(0), operands(1), mods))),
    FlatInstructionDefs.FlatStoreDwordx3 ->
      decoder(form(2)((operands, mods) => Instruction.FlatStoreDwordx3(operands(0), operands(1), mods))),
    FlatInstructionDefs.FlatStoreDwordx4 ->
      decoder(form(2)((operands, mods) => Instruction.FlatStoreDwordx4(operands(0), operands(1), mods))),
    FlatInstructionDefs.FlatStoreShort ->
      decoder(form(2)((operands, mods) => Instruction.FlatStoreShort(operands(0), operands(1), mods))),
    FlatInstructionDefs.FlatStoreShortD16Hi ->
      decoder(form(2)((operands, mods) => Instruction.FlatStoreShortD16Hi(operands(0), operands(1), mods))),
    FlatInstructionDefs.GlobalAtomicAdd -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicAdd_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicAdd_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicAddF32 -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicAddF32_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicAddF32_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicAddF64 -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicAddF64_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicAddF64_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicAddU32 -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicAddU32_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicAddU32_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicAddU64 -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicAddU64_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicAddU64_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicAddX2 -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicAddX2_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicAddX2_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicAnd -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicAnd_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicAnd_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicAndB32 -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicAndB32_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicAndB32_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicAndB64 -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicAndB64_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicAndB64_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicAndX2 -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicAndX2_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicAndX2_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicCmpswap -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicCmpswap_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicCmpswap_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicCmpswapB32 -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicCmpswapB32_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicCmpswapB32_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicCmpswapB64 -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicCmpswapB64_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicCmpswapB64_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicCmpswapF32 -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicCmpswapF32_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicCmpswapF32_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicCmpswapX2 -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicCmpswapX2_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicCmpswapX2_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicCondSubU32 -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicCondSubU32_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicCondSubU32_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicCsub -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicCsub_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicCsub_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicCsubU32 -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicCsubU32_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicCsubU32_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicDec -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicDec_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicDec_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicDecU32 -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicDecU32_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicDecU32_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicDecU64 -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicDecU64_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicDecU64_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicDecX2 -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicDecX2_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicDecX2_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicFcmpswap -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicFcmpswap_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicFcmpswap_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicFcmpswapX2 -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicFcmpswapX2_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicFcmpswapX2_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicFmax -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicFmax_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicFmax_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicFmaxX2 -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicFmaxX2_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicFmaxX2_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicFmin -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicFmin_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicFmin_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicFminX2 -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicFminX2_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicFminX2_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicInc -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicInc_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicInc_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicIncU32 -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicIncU32_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicIncU32_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicIncU64 -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicIncU64_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicIncU64_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicIncX2 -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicIncX2_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicIncX2_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicMaxF32 -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicMaxF32_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicMaxF32_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicMaxF64 -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicMaxF64_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicMaxF64_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicMaxI32 -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicMaxI32_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicMaxI32_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicMaxI64 -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicMaxI64_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicMaxI64_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicMaxNumF32 -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicMaxNumF32_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicMaxNumF32_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicMaxNumF64 -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicMaxNumF64_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicMaxNumF64_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicMaxU32 -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicMaxU32_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicMaxU32_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicMaxU64 -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicMaxU64_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicMaxU64_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicMinF32 -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicMinF32_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicMinF32_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicMinF64 -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicMinF64_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicMinF64_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicMinI32 -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicMinI32_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicMinI32_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicMinI64 -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicMinI64_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicMinI64_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicMinNumF32 -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicMinNumF32_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicMinNumF32_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicMinNumF64 -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicMinNumF64_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicMinNumF64_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicMinU32 -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicMinU32_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicMinU32_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicMinU64 -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicMinU64_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicMinU64_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicOr -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicOr_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicOr_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicOrB32 -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicOrB32_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicOrB32_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicOrB64 -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicOrB64_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicOrB64_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicOrX2 -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicOrX2_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicOrX2_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicOrderedAddB64 -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicOrderedAddB64_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicOrderedAddB64_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicPkAddBf16 -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicPkAddBf16_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicPkAddBf16_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicPkAddF16 -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicPkAddF16_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicPkAddF16_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicSmax -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicSmax_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicSmax_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicSmaxX2 -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicSmaxX2_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicSmaxX2_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicSmin -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicSmin_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicSmin_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicSminX2 -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicSminX2_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicSminX2_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicSub -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicSub_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicSub_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicSubClampU32 -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicSubClampU32_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicSubClampU32_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicSubU32 -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicSubU32_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicSubU32_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicSubU64 -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicSubU64_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicSubU64_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicSubX2 -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicSubX2_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicSubX2_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicSwap -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicSwap_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicSwap_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicSwapB32 -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicSwapB32_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicSwapB32_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicSwapB64 -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicSwapB64_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicSwapB64_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicSwapX2 -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicSwapX2_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicSwapX2_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicUmax -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicUmax_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicUmax_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicUmaxX2 -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicUmaxX2_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicUmaxX2_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicUmin -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicUmin_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicUmin_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicUminX2 -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicUminX2_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicUminX2_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicXor -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicXor_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicXor_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicXorB32 -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicXorB32_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicXorB32_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicXorB64 -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicXorB64_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicXorB64_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalAtomicXorX2 -> decoder(
      form(3)((operands, mods) => Instruction.GlobalAtomicXorX2_3(operands(0), operands(1), operands(2), mods)),
      form(4)((operands, mods) =>
        Instruction.GlobalAtomicXorX2_4(operands(0), operands(1), operands(2), operands(3), mods)
      )
    ),
    FlatInstructionDefs.GlobalInv -> decoder(form(0)((_, mods) => Instruction.GlobalInv(mods))),
    FlatInstructionDefs.GlobalLoadAddtidB32 -> decoder(
      form(2)((operands, mods) => Instruction.GlobalLoadAddtidB32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.GlobalLoadAddtidB32_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.GlobalLoadAsyncToLdsB128 -> decoder(
      form(2)((operands, mods) => Instruction.GlobalLoadAsyncToLdsB128_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.GlobalLoadAsyncToLdsB128_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.GlobalLoadAsyncToLdsB32 -> decoder(
      form(2)((operands, mods) => Instruction.GlobalLoadAsyncToLdsB32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.GlobalLoadAsyncToLdsB32_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.GlobalLoadAsyncToLdsB64 -> decoder(
      form(2)((operands, mods) => Instruction.GlobalLoadAsyncToLdsB64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.GlobalLoadAsyncToLdsB64_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.GlobalLoadAsyncToLdsB8 -> decoder(
      form(2)((operands, mods) => Instruction.GlobalLoadAsyncToLdsB8_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.GlobalLoadAsyncToLdsB8_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.GlobalLoadB128 -> decoder(
      form(2)((operands, mods) => Instruction.GlobalLoadB128_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.GlobalLoadB128_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.GlobalLoadB32 -> decoder(
      form(2)((operands, mods) => Instruction.GlobalLoadB32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.GlobalLoadB32_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.GlobalLoadB64 -> decoder(
      form(2)((operands, mods) => Instruction.GlobalLoadB64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.GlobalLoadB64_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.GlobalLoadB96 -> decoder(
      form(2)((operands, mods) => Instruction.GlobalLoadB96_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.GlobalLoadB96_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.GlobalLoadBlock -> decoder(
      form(2)((operands, mods) => Instruction.GlobalLoadBlock_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.GlobalLoadBlock_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.GlobalLoadD16B16 -> decoder(
      form(2)((operands, mods) => Instruction.GlobalLoadD16B16_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.GlobalLoadD16B16_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.GlobalLoadD16HiB16 -> decoder(
      form(2)((operands, mods) => Instruction.GlobalLoadD16HiB16_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.GlobalLoadD16HiB16_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.GlobalLoadD16HiI8 -> decoder(
      form(2)((operands, mods) => Instruction.GlobalLoadD16HiI8_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.GlobalLoadD16HiI8_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.GlobalLoadD16HiU8 -> decoder(
      form(2)((operands, mods) => Instruction.GlobalLoadD16HiU8_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.GlobalLoadD16HiU8_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.GlobalLoadD16I8 -> decoder(
      form(2)((operands, mods) => Instruction.GlobalLoadD16I8_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.GlobalLoadD16I8_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.GlobalLoadD16U8 -> decoder(
      form(2)((operands, mods) => Instruction.GlobalLoadD16U8_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.GlobalLoadD16U8_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.GlobalLoadDword -> decoder(
      form(2)((operands, mods) => Instruction.GlobalLoadDword_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.GlobalLoadDword_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.GlobalLoadDwordAddtid -> decoder(
      form(2)((operands, mods) => Instruction.GlobalLoadDwordAddtid_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.GlobalLoadDwordAddtid_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.GlobalLoadDwordx2 -> decoder(
      form(2)((operands, mods) => Instruction.GlobalLoadDwordx2_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.GlobalLoadDwordx2_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.GlobalLoadDwordx3 -> decoder(
      form(2)((operands, mods) => Instruction.GlobalLoadDwordx3_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.GlobalLoadDwordx3_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.GlobalLoadDwordx4 -> decoder(
      form(2)((operands, mods) => Instruction.GlobalLoadDwordx4_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.GlobalLoadDwordx4_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.GlobalLoadI16 -> decoder(
      form(2)((operands, mods) => Instruction.GlobalLoadI16_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.GlobalLoadI16_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.GlobalLoadI8 -> decoder(
      form(2)((operands, mods) => Instruction.GlobalLoadI8_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.GlobalLoadI8_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.GlobalLoadLdsDword -> decoder(
      form(2)((operands, mods) => Instruction.GlobalLoadLdsDword_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.GlobalLoadLdsDword_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.GlobalLoadLdsDwordx3 -> decoder(
      form(2)((operands, mods) => Instruction.GlobalLoadLdsDwordx3_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.GlobalLoadLdsDwordx3_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.GlobalLoadLdsDwordx4 -> decoder(
      form(2)((operands, mods) => Instruction.GlobalLoadLdsDwordx4_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.GlobalLoadLdsDwordx4_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.GlobalLoadLdsSbyte -> decoder(
      form(2)((operands, mods) => Instruction.GlobalLoadLdsSbyte_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.GlobalLoadLdsSbyte_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.GlobalLoadLdsSshort -> decoder(
      form(2)((operands, mods) => Instruction.GlobalLoadLdsSshort_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.GlobalLoadLdsSshort_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.GlobalLoadLdsUbyte -> decoder(
      form(2)((operands, mods) => Instruction.GlobalLoadLdsUbyte_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.GlobalLoadLdsUbyte_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.GlobalLoadLdsUshort -> decoder(
      form(2)((operands, mods) => Instruction.GlobalLoadLdsUshort_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.GlobalLoadLdsUshort_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.GlobalLoadMonitorB128 -> decoder(
      form(2)((operands, mods) => Instruction.GlobalLoadMonitorB128_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.GlobalLoadMonitorB128_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.GlobalLoadMonitorB32 -> decoder(
      form(2)((operands, mods) => Instruction.GlobalLoadMonitorB32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.GlobalLoadMonitorB32_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.GlobalLoadMonitorB64 -> decoder(
      form(2)((operands, mods) => Instruction.GlobalLoadMonitorB64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.GlobalLoadMonitorB64_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.GlobalLoadSbyte -> decoder(
      form(2)((operands, mods) => Instruction.GlobalLoadSbyte_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.GlobalLoadSbyte_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.GlobalLoadSbyteD16 -> decoder(
      form(2)((operands, mods) => Instruction.GlobalLoadSbyteD16_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.GlobalLoadSbyteD16_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.GlobalLoadSbyteD16Hi -> decoder(
      form(2)((operands, mods) => Instruction.GlobalLoadSbyteD16Hi_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.GlobalLoadSbyteD16Hi_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.GlobalLoadShortD16 -> decoder(
      form(2)((operands, mods) => Instruction.GlobalLoadShortD16_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.GlobalLoadShortD16_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.GlobalLoadShortD16Hi -> decoder(
      form(2)((operands, mods) => Instruction.GlobalLoadShortD16Hi_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.GlobalLoadShortD16Hi_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.GlobalLoadSshort -> decoder(
      form(2)((operands, mods) => Instruction.GlobalLoadSshort_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.GlobalLoadSshort_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.GlobalLoadTr16B128 -> decoder(
      form(2)((operands, mods) => Instruction.GlobalLoadTr16B128_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.GlobalLoadTr16B128_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.GlobalLoadTr4B64 -> decoder(
      form(2)((operands, mods) => Instruction.GlobalLoadTr4B64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.GlobalLoadTr4B64_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.GlobalLoadTr6B96 -> decoder(
      form(2)((operands, mods) => Instruction.GlobalLoadTr6B96_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.GlobalLoadTr6B96_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.GlobalLoadTr8B64 -> decoder(
      form(2)((operands, mods) => Instruction.GlobalLoadTr8B64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.GlobalLoadTr8B64_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.GlobalLoadTrB128 -> decoder(
      form(2)((operands, mods) => Instruction.GlobalLoadTrB128_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.GlobalLoadTrB128_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.GlobalLoadTrB64 -> decoder(
      form(2)((operands, mods) => Instruction.GlobalLoadTrB64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.GlobalLoadTrB64_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.GlobalLoadU16 -> decoder(
      form(2)((operands, mods) => Instruction.GlobalLoadU16_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.GlobalLoadU16_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.GlobalLoadU8 -> decoder(
      form(2)((operands, mods) => Instruction.GlobalLoadU8_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.GlobalLoadU8_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.GlobalLoadUbyte -> decoder(
      form(2)((operands, mods) => Instruction.GlobalLoadUbyte_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.GlobalLoadUbyte_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.GlobalLoadUbyteD16 -> decoder(
      form(2)((operands, mods) => Instruction.GlobalLoadUbyteD16_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.GlobalLoadUbyteD16_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.GlobalLoadUbyteD16Hi -> decoder(
      form(2)((operands, mods) => Instruction.GlobalLoadUbyteD16Hi_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.GlobalLoadUbyteD16Hi_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.GlobalLoadUshort -> decoder(
      form(2)((operands, mods) => Instruction.GlobalLoadUshort_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.GlobalLoadUshort_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.GlobalPrefetchB8 ->
      decoder(form(2)((operands, mods) => Instruction.GlobalPrefetchB8(operands(0), operands(1), mods))),
    FlatInstructionDefs.GlobalStoreAddtidB32 ->
      decoder(form(2)((operands, mods) => Instruction.GlobalStoreAddtidB32(operands(0), operands(1), mods))),
    FlatInstructionDefs.GlobalStoreAsyncFromLdsB128 -> decoder(form(3)((operands, mods) =>
      Instruction.GlobalStoreAsyncFromLdsB128(operands(0), operands(1), operands(2), mods)
    )),
    FlatInstructionDefs.GlobalStoreAsyncFromLdsB32 -> decoder(form(3)((operands, mods) =>
      Instruction.GlobalStoreAsyncFromLdsB32(operands(0), operands(1), operands(2), mods)
    )),
    FlatInstructionDefs.GlobalStoreAsyncFromLdsB64 -> decoder(form(3)((operands, mods) =>
      Instruction.GlobalStoreAsyncFromLdsB64(operands(0), operands(1), operands(2), mods)
    )),
    FlatInstructionDefs.GlobalStoreAsyncFromLdsB8 -> decoder(form(3)((operands, mods) =>
      Instruction.GlobalStoreAsyncFromLdsB8(operands(0), operands(1), operands(2), mods)
    )),
    FlatInstructionDefs.GlobalStoreB128 ->
      decoder(form(3)((operands, mods) => Instruction.GlobalStoreB128(operands(0), operands(1), operands(2), mods))),
    FlatInstructionDefs.GlobalStoreB16 ->
      decoder(form(3)((operands, mods) => Instruction.GlobalStoreB16(operands(0), operands(1), operands(2), mods))),
    FlatInstructionDefs.GlobalStoreB32 ->
      decoder(form(3)((operands, mods) => Instruction.GlobalStoreB32(operands(0), operands(1), operands(2), mods))),
    FlatInstructionDefs.GlobalStoreB64 ->
      decoder(form(3)((operands, mods) => Instruction.GlobalStoreB64(operands(0), operands(1), operands(2), mods))),
    FlatInstructionDefs.GlobalStoreB8 ->
      decoder(form(3)((operands, mods) => Instruction.GlobalStoreB8(operands(0), operands(1), operands(2), mods))),
    FlatInstructionDefs.GlobalStoreB96 ->
      decoder(form(3)((operands, mods) => Instruction.GlobalStoreB96(operands(0), operands(1), operands(2), mods))),
    FlatInstructionDefs.GlobalStoreBlock ->
      decoder(form(3)((operands, mods) => Instruction.GlobalStoreBlock(operands(0), operands(1), operands(2), mods))),
    FlatInstructionDefs.GlobalStoreByte ->
      decoder(form(3)((operands, mods) => Instruction.GlobalStoreByte(operands(0), operands(1), operands(2), mods))),
    FlatInstructionDefs.GlobalStoreByteD16Hi -> decoder(form(3)((operands, mods) =>
      Instruction.GlobalStoreByteD16Hi(operands(0), operands(1), operands(2), mods)
    )),
    FlatInstructionDefs.GlobalStoreD16HiB16 -> decoder(form(3)((operands, mods) =>
      Instruction.GlobalStoreD16HiB16(operands(0), operands(1), operands(2), mods)
    )),
    FlatInstructionDefs.GlobalStoreD16HiB8 ->
      decoder(form(3)((operands, mods) => Instruction.GlobalStoreD16HiB8(operands(0), operands(1), operands(2), mods))),
    FlatInstructionDefs.GlobalStoreDword ->
      decoder(form(3)((operands, mods) => Instruction.GlobalStoreDword(operands(0), operands(1), operands(2), mods))),
    FlatInstructionDefs.GlobalStoreDwordAddtid ->
      decoder(form(2)((operands, mods) => Instruction.GlobalStoreDwordAddtid(operands(0), operands(1), mods))),
    FlatInstructionDefs.GlobalStoreDwordx2 ->
      decoder(form(3)((operands, mods) => Instruction.GlobalStoreDwordx2(operands(0), operands(1), operands(2), mods))),
    FlatInstructionDefs.GlobalStoreDwordx3 ->
      decoder(form(3)((operands, mods) => Instruction.GlobalStoreDwordx3(operands(0), operands(1), operands(2), mods))),
    FlatInstructionDefs.GlobalStoreDwordx4 ->
      decoder(form(3)((operands, mods) => Instruction.GlobalStoreDwordx4(operands(0), operands(1), operands(2), mods))),
    FlatInstructionDefs.GlobalStoreShort ->
      decoder(form(3)((operands, mods) => Instruction.GlobalStoreShort(operands(0), operands(1), operands(2), mods))),
    FlatInstructionDefs.GlobalStoreShortD16Hi -> decoder(form(3)((operands, mods) =>
      Instruction.GlobalStoreShortD16Hi(operands(0), operands(1), operands(2), mods)
    )),
    FlatInstructionDefs.GlobalWb -> decoder(form(0)((_, mods) => Instruction.GlobalWb(mods))),
    FlatInstructionDefs.GlobalWbinv -> decoder(form(0)((_, mods) => Instruction.GlobalWbinv(mods))),
    FlatInstructionDefs.ScratchLoadB128 -> decoder(
      form(2)((operands, mods) => Instruction.ScratchLoadB128_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.ScratchLoadB128_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.ScratchLoadB32 -> decoder(
      form(2)((operands, mods) => Instruction.ScratchLoadB32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.ScratchLoadB32_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.ScratchLoadB64 -> decoder(
      form(2)((operands, mods) => Instruction.ScratchLoadB64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.ScratchLoadB64_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.ScratchLoadB96 -> decoder(
      form(2)((operands, mods) => Instruction.ScratchLoadB96_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.ScratchLoadB96_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.ScratchLoadBlock -> decoder(
      form(2)((operands, mods) => Instruction.ScratchLoadBlock_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.ScratchLoadBlock_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.ScratchLoadD16B16 -> decoder(
      form(2)((operands, mods) => Instruction.ScratchLoadD16B16_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.ScratchLoadD16B16_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.ScratchLoadD16HiB16 -> decoder(
      form(2)((operands, mods) => Instruction.ScratchLoadD16HiB16_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.ScratchLoadD16HiB16_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.ScratchLoadD16HiI8 -> decoder(
      form(2)((operands, mods) => Instruction.ScratchLoadD16HiI8_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.ScratchLoadD16HiI8_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.ScratchLoadD16HiU8 -> decoder(
      form(2)((operands, mods) => Instruction.ScratchLoadD16HiU8_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.ScratchLoadD16HiU8_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.ScratchLoadD16I8 -> decoder(
      form(2)((operands, mods) => Instruction.ScratchLoadD16I8_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.ScratchLoadD16I8_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.ScratchLoadD16U8 -> decoder(
      form(2)((operands, mods) => Instruction.ScratchLoadD16U8_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.ScratchLoadD16U8_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.ScratchLoadDword -> decoder(
      form(2)((operands, mods) => Instruction.ScratchLoadDword_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.ScratchLoadDword_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.ScratchLoadDwordx2 -> decoder(
      form(2)((operands, mods) => Instruction.ScratchLoadDwordx2_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.ScratchLoadDwordx2_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.ScratchLoadDwordx3 -> decoder(
      form(2)((operands, mods) => Instruction.ScratchLoadDwordx3_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.ScratchLoadDwordx3_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.ScratchLoadDwordx4 -> decoder(
      form(2)((operands, mods) => Instruction.ScratchLoadDwordx4_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.ScratchLoadDwordx4_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.ScratchLoadI16 -> decoder(
      form(2)((operands, mods) => Instruction.ScratchLoadI16_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.ScratchLoadI16_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.ScratchLoadI8 -> decoder(
      form(2)((operands, mods) => Instruction.ScratchLoadI8_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.ScratchLoadI8_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.ScratchLoadLdsDword -> decoder(
      form(2)((operands, mods) => Instruction.ScratchLoadLdsDword_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.ScratchLoadLdsDword_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.ScratchLoadLdsSbyte -> decoder(
      form(2)((operands, mods) => Instruction.ScratchLoadLdsSbyte_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.ScratchLoadLdsSbyte_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.ScratchLoadLdsSshort -> decoder(
      form(2)((operands, mods) => Instruction.ScratchLoadLdsSshort_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.ScratchLoadLdsSshort_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.ScratchLoadLdsUbyte -> decoder(
      form(2)((operands, mods) => Instruction.ScratchLoadLdsUbyte_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.ScratchLoadLdsUbyte_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.ScratchLoadLdsUshort -> decoder(
      form(2)((operands, mods) => Instruction.ScratchLoadLdsUshort_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.ScratchLoadLdsUshort_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.ScratchLoadSbyte -> decoder(
      form(2)((operands, mods) => Instruction.ScratchLoadSbyte_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.ScratchLoadSbyte_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.ScratchLoadSbyteD16 -> decoder(
      form(2)((operands, mods) => Instruction.ScratchLoadSbyteD16_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.ScratchLoadSbyteD16_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.ScratchLoadSbyteD16Hi -> decoder(
      form(2)((operands, mods) => Instruction.ScratchLoadSbyteD16Hi_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.ScratchLoadSbyteD16Hi_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.ScratchLoadShortD16 -> decoder(
      form(2)((operands, mods) => Instruction.ScratchLoadShortD16_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.ScratchLoadShortD16_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.ScratchLoadShortD16Hi -> decoder(
      form(2)((operands, mods) => Instruction.ScratchLoadShortD16Hi_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.ScratchLoadShortD16Hi_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.ScratchLoadSshort -> decoder(
      form(2)((operands, mods) => Instruction.ScratchLoadSshort_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.ScratchLoadSshort_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.ScratchLoadU16 -> decoder(
      form(2)((operands, mods) => Instruction.ScratchLoadU16_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.ScratchLoadU16_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.ScratchLoadU8 -> decoder(
      form(2)((operands, mods) => Instruction.ScratchLoadU8_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.ScratchLoadU8_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.ScratchLoadUbyte -> decoder(
      form(2)((operands, mods) => Instruction.ScratchLoadUbyte_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.ScratchLoadUbyte_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.ScratchLoadUbyteD16 -> decoder(
      form(2)((operands, mods) => Instruction.ScratchLoadUbyteD16_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.ScratchLoadUbyteD16_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.ScratchLoadUbyteD16Hi -> decoder(
      form(2)((operands, mods) => Instruction.ScratchLoadUbyteD16Hi_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.ScratchLoadUbyteD16Hi_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.ScratchLoadUshort -> decoder(
      form(2)((operands, mods) => Instruction.ScratchLoadUshort_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.ScratchLoadUshort_3(operands(0), operands(1), operands(2), mods))
    ),
    FlatInstructionDefs.ScratchStoreB128 ->
      decoder(form(3)((operands, mods) => Instruction.ScratchStoreB128(operands(0), operands(1), operands(2), mods))),
    FlatInstructionDefs.ScratchStoreB16 ->
      decoder(form(3)((operands, mods) => Instruction.ScratchStoreB16(operands(0), operands(1), operands(2), mods))),
    FlatInstructionDefs.ScratchStoreB32 ->
      decoder(form(3)((operands, mods) => Instruction.ScratchStoreB32(operands(0), operands(1), operands(2), mods))),
    FlatInstructionDefs.ScratchStoreB64 ->
      decoder(form(3)((operands, mods) => Instruction.ScratchStoreB64(operands(0), operands(1), operands(2), mods))),
    FlatInstructionDefs.ScratchStoreB8 ->
      decoder(form(3)((operands, mods) => Instruction.ScratchStoreB8(operands(0), operands(1), operands(2), mods))),
    FlatInstructionDefs.ScratchStoreB96 ->
      decoder(form(3)((operands, mods) => Instruction.ScratchStoreB96(operands(0), operands(1), operands(2), mods))),
    FlatInstructionDefs.ScratchStoreBlock ->
      decoder(form(3)((operands, mods) => Instruction.ScratchStoreBlock(operands(0), operands(1), operands(2), mods))),
    FlatInstructionDefs.ScratchStoreByte ->
      decoder(form(3)((operands, mods) => Instruction.ScratchStoreByte(operands(0), operands(1), operands(2), mods))),
    FlatInstructionDefs.ScratchStoreByteD16Hi -> decoder(form(3)((operands, mods) =>
      Instruction.ScratchStoreByteD16Hi(operands(0), operands(1), operands(2), mods)
    )),
    FlatInstructionDefs.ScratchStoreD16HiB16 -> decoder(form(3)((operands, mods) =>
      Instruction.ScratchStoreD16HiB16(operands(0), operands(1), operands(2), mods)
    )),
    FlatInstructionDefs.ScratchStoreD16HiB8 -> decoder(form(3)((operands, mods) =>
      Instruction.ScratchStoreD16HiB8(operands(0), operands(1), operands(2), mods)
    )),
    FlatInstructionDefs.ScratchStoreDword ->
      decoder(form(3)((operands, mods) => Instruction.ScratchStoreDword(operands(0), operands(1), operands(2), mods))),
    FlatInstructionDefs.ScratchStoreDwordx2 -> decoder(form(3)((operands, mods) =>
      Instruction.ScratchStoreDwordx2(operands(0), operands(1), operands(2), mods)
    )),
    FlatInstructionDefs.ScratchStoreDwordx3 -> decoder(form(3)((operands, mods) =>
      Instruction.ScratchStoreDwordx3(operands(0), operands(1), operands(2), mods)
    )),
    FlatInstructionDefs.ScratchStoreDwordx4 -> decoder(form(3)((operands, mods) =>
      Instruction.ScratchStoreDwordx4(operands(0), operands(1), operands(2), mods)
    )),
    FlatInstructionDefs.ScratchStoreShort ->
      decoder(form(3)((operands, mods) => Instruction.ScratchStoreShort(operands(0), operands(1), operands(2), mods))),
    FlatInstructionDefs.ScratchStoreShortD16Hi -> decoder(form(3)((operands, mods) =>
      Instruction.ScratchStoreShortD16Hi(operands(0), operands(1), operands(2), mods)
    ))
  )
}
