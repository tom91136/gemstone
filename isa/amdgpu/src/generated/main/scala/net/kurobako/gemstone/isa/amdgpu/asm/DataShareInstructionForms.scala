// Generated from the pinned AMDGPU instruction forms. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu.asm
import net.kurobako.gemstone.isa.amdgpu.*
import Instruction.*
private object DataShareInstructionForms {
  val decoders: Map[Opcode, Decoder] = Map(
    DataShareInstructionDefs.DsAddF32 ->
      decoder(form(2)((operands, mods) => Instruction.DsAddF32(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsAddF64 ->
      decoder(form(2)((operands, mods) => Instruction.DsAddF64(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsAddGsRegRtn ->
      decoder(form(2)((operands, mods) => Instruction.DsAddGsRegRtn(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsAddRtnF32 ->
      decoder(form(3)((operands, mods) => Instruction.DsAddRtnF32(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsAddRtnF64 ->
      decoder(form(3)((operands, mods) => Instruction.DsAddRtnF64(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsAddRtnU32 ->
      decoder(form(3)((operands, mods) => Instruction.DsAddRtnU32(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsAddRtnU64 ->
      decoder(form(3)((operands, mods) => Instruction.DsAddRtnU64(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsAddSrc2F32 ->
      decoder(form(1)((operands, mods) => Instruction.DsAddSrc2F32(operands(0), mods))),
    DataShareInstructionDefs.DsAddSrc2U32 ->
      decoder(form(1)((operands, mods) => Instruction.DsAddSrc2U32(operands(0), mods))),
    DataShareInstructionDefs.DsAddSrc2U64 ->
      decoder(form(1)((operands, mods) => Instruction.DsAddSrc2U64(operands(0), mods))),
    DataShareInstructionDefs.DsAddU32 ->
      decoder(form(2)((operands, mods) => Instruction.DsAddU32(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsAddU64 ->
      decoder(form(2)((operands, mods) => Instruction.DsAddU64(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsAndB32 ->
      decoder(form(2)((operands, mods) => Instruction.DsAndB32(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsAndB64 ->
      decoder(form(2)((operands, mods) => Instruction.DsAndB64(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsAndRtnB32 ->
      decoder(form(3)((operands, mods) => Instruction.DsAndRtnB32(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsAndRtnB64 ->
      decoder(form(3)((operands, mods) => Instruction.DsAndRtnB64(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsAndSrc2B32 ->
      decoder(form(1)((operands, mods) => Instruction.DsAndSrc2B32(operands(0), mods))),
    DataShareInstructionDefs.DsAndSrc2B64 ->
      decoder(form(1)((operands, mods) => Instruction.DsAndSrc2B64(operands(0), mods))),
    DataShareInstructionDefs.DsAppend -> decoder(form(1)((operands, mods) => Instruction.DsAppend(operands(0), mods))),
    DataShareInstructionDefs.DsAtomicAsyncBarrierArriveB64 ->
      decoder(form(1)((operands, mods) => Instruction.DsAtomicAsyncBarrierArriveB64(operands(0), mods))),
    DataShareInstructionDefs.DsAtomicBarrierArriveRtnB64 -> decoder(form(3)((operands, mods) =>
      Instruction.DsAtomicBarrierArriveRtnB64(operands(0), operands(1), operands(2), mods)
    )),
    DataShareInstructionDefs.DsBpermuteB32 ->
      decoder(form(3)((operands, mods) => Instruction.DsBpermuteB32(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsBpermuteFiB32 ->
      decoder(form(3)((operands, mods) => Instruction.DsBpermuteFiB32(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsBvhStackPush4Pop1RtnB32 -> decoder(form(4)((operands, mods) =>
      Instruction.DsBvhStackPush4Pop1RtnB32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    DataShareInstructionDefs.DsBvhStackPush8Pop1RtnB32 -> decoder(form(4)((operands, mods) =>
      Instruction.DsBvhStackPush8Pop1RtnB32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    DataShareInstructionDefs.DsBvhStackPush8Pop2RtnB64 -> decoder(form(4)((operands, mods) =>
      Instruction.DsBvhStackPush8Pop2RtnB64(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    DataShareInstructionDefs.DsBvhStackRtnB32 -> decoder(form(4)((operands, mods) =>
      Instruction.DsBvhStackRtnB32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    DataShareInstructionDefs.DsCmpstB32 ->
      decoder(form(3)((operands, mods) => Instruction.DsCmpstB32(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsCmpstB64 ->
      decoder(form(3)((operands, mods) => Instruction.DsCmpstB64(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsCmpstF32 ->
      decoder(form(3)((operands, mods) => Instruction.DsCmpstF32(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsCmpstF64 ->
      decoder(form(3)((operands, mods) => Instruction.DsCmpstF64(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsCmpstRtnB32 -> decoder(form(4)((operands, mods) =>
      Instruction.DsCmpstRtnB32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    DataShareInstructionDefs.DsCmpstRtnB64 -> decoder(form(4)((operands, mods) =>
      Instruction.DsCmpstRtnB64(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    DataShareInstructionDefs.DsCmpstRtnF32 -> decoder(form(4)((operands, mods) =>
      Instruction.DsCmpstRtnF32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    DataShareInstructionDefs.DsCmpstRtnF64 -> decoder(form(4)((operands, mods) =>
      Instruction.DsCmpstRtnF64(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    DataShareInstructionDefs.DsCmpstoreB32 ->
      decoder(form(3)((operands, mods) => Instruction.DsCmpstoreB32(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsCmpstoreB64 ->
      decoder(form(3)((operands, mods) => Instruction.DsCmpstoreB64(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsCmpstoreF32 ->
      decoder(form(3)((operands, mods) => Instruction.DsCmpstoreF32(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsCmpstoreF64 ->
      decoder(form(3)((operands, mods) => Instruction.DsCmpstoreF64(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsCmpstoreRtnB32 -> decoder(form(4)((operands, mods) =>
      Instruction.DsCmpstoreRtnB32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    DataShareInstructionDefs.DsCmpstoreRtnB64 -> decoder(form(4)((operands, mods) =>
      Instruction.DsCmpstoreRtnB64(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    DataShareInstructionDefs.DsCmpstoreRtnF32 -> decoder(form(4)((operands, mods) =>
      Instruction.DsCmpstoreRtnF32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    DataShareInstructionDefs.DsCmpstoreRtnF64 -> decoder(form(4)((operands, mods) =>
      Instruction.DsCmpstoreRtnF64(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    DataShareInstructionDefs.DsCondSubRtnU32 ->
      decoder(form(3)((operands, mods) => Instruction.DsCondSubRtnU32(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsCondSubU32 ->
      decoder(form(2)((operands, mods) => Instruction.DsCondSubU32(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsCondxchg32RtnB128 ->
      decoder(form(0)((_, mods) => Instruction.DsCondxchg32RtnB128(mods))),
    DataShareInstructionDefs.DsCondxchg32RtnB64 ->
      decoder(form(3)((operands, mods) => Instruction.DsCondxchg32RtnB64(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsConsume ->
      decoder(form(1)((operands, mods) => Instruction.DsConsume(operands(0), mods))),
    DataShareInstructionDefs.DsDecRtnU32 ->
      decoder(form(3)((operands, mods) => Instruction.DsDecRtnU32(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsDecRtnU64 ->
      decoder(form(3)((operands, mods) => Instruction.DsDecRtnU64(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsDecSrc2U32 ->
      decoder(form(1)((operands, mods) => Instruction.DsDecSrc2U32(operands(0), mods))),
    DataShareInstructionDefs.DsDecSrc2U64 ->
      decoder(form(1)((operands, mods) => Instruction.DsDecSrc2U64(operands(0), mods))),
    DataShareInstructionDefs.DsDecU32 ->
      decoder(form(2)((operands, mods) => Instruction.DsDecU32(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsDecU64 ->
      decoder(form(2)((operands, mods) => Instruction.DsDecU64(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsDirectLoad ->
      decoder(form(1)((operands, mods) => Instruction.DsDirectLoad(operands(0), mods))),
    DataShareInstructionDefs.DsGwsBarrier ->
      decoder(form(1)((operands, mods) => Instruction.DsGwsBarrier(operands(0), mods))),
    DataShareInstructionDefs.DsGwsInit ->
      decoder(form(1)((operands, mods) => Instruction.DsGwsInit(operands(0), mods))),
    DataShareInstructionDefs.DsGwsSemaBr ->
      decoder(form(1)((operands, mods) => Instruction.DsGwsSemaBr(operands(0), mods))),
    DataShareInstructionDefs.DsGwsSemaP -> decoder(form(0)((_, mods) => Instruction.DsGwsSemaP(mods))),
    DataShareInstructionDefs.DsGwsSemaReleaseAll ->
      decoder(form(0)((_, mods) => Instruction.DsGwsSemaReleaseAll(mods))),
    DataShareInstructionDefs.DsGwsSemaV -> decoder(form(0)((_, mods) => Instruction.DsGwsSemaV(mods))),
    DataShareInstructionDefs.DsIncRtnU32 ->
      decoder(form(3)((operands, mods) => Instruction.DsIncRtnU32(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsIncRtnU64 ->
      decoder(form(3)((operands, mods) => Instruction.DsIncRtnU64(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsIncSrc2U32 ->
      decoder(form(1)((operands, mods) => Instruction.DsIncSrc2U32(operands(0), mods))),
    DataShareInstructionDefs.DsIncSrc2U64 ->
      decoder(form(1)((operands, mods) => Instruction.DsIncSrc2U64(operands(0), mods))),
    DataShareInstructionDefs.DsIncU32 ->
      decoder(form(2)((operands, mods) => Instruction.DsIncU32(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsIncU64 ->
      decoder(form(2)((operands, mods) => Instruction.DsIncU64(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsLoad2addrB32 ->
      decoder(form(2)((operands, mods) => Instruction.DsLoad2addrB32(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsLoad2addrB64 ->
      decoder(form(2)((operands, mods) => Instruction.DsLoad2addrB64(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsLoad2addrStride64B32 ->
      decoder(form(2)((operands, mods) => Instruction.DsLoad2addrStride64B32(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsLoad2addrStride64B64 ->
      decoder(form(2)((operands, mods) => Instruction.DsLoad2addrStride64B64(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsLoadAddtidB32 ->
      decoder(form(1)((operands, mods) => Instruction.DsLoadAddtidB32(operands(0), mods))),
    DataShareInstructionDefs.DsLoadB128 ->
      decoder(form(2)((operands, mods) => Instruction.DsLoadB128(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsLoadB32 ->
      decoder(form(2)((operands, mods) => Instruction.DsLoadB32(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsLoadB64 ->
      decoder(form(2)((operands, mods) => Instruction.DsLoadB64(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsLoadB96 ->
      decoder(form(2)((operands, mods) => Instruction.DsLoadB96(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsLoadI16 ->
      decoder(form(2)((operands, mods) => Instruction.DsLoadI16(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsLoadI8 ->
      decoder(form(2)((operands, mods) => Instruction.DsLoadI8(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsLoadI8D16 ->
      decoder(form(2)((operands, mods) => Instruction.DsLoadI8D16(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsLoadI8D16Hi ->
      decoder(form(2)((operands, mods) => Instruction.DsLoadI8D16Hi(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsLoadTr16B128 ->
      decoder(form(2)((operands, mods) => Instruction.DsLoadTr16B128(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsLoadTr4B64 ->
      decoder(form(2)((operands, mods) => Instruction.DsLoadTr4B64(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsLoadTr6B96 ->
      decoder(form(2)((operands, mods) => Instruction.DsLoadTr6B96(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsLoadTr8B64 ->
      decoder(form(2)((operands, mods) => Instruction.DsLoadTr8B64(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsLoadU16 ->
      decoder(form(2)((operands, mods) => Instruction.DsLoadU16(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsLoadU16D16 ->
      decoder(form(2)((operands, mods) => Instruction.DsLoadU16D16(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsLoadU16D16Hi ->
      decoder(form(2)((operands, mods) => Instruction.DsLoadU16D16Hi(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsLoadU8 ->
      decoder(form(2)((operands, mods) => Instruction.DsLoadU8(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsLoadU8D16 ->
      decoder(form(2)((operands, mods) => Instruction.DsLoadU8D16(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsLoadU8D16Hi ->
      decoder(form(2)((operands, mods) => Instruction.DsLoadU8D16Hi(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsMaxF32 ->
      decoder(form(2)((operands, mods) => Instruction.DsMaxF32(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsMaxF64 ->
      decoder(form(2)((operands, mods) => Instruction.DsMaxF64(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsMaxI32 ->
      decoder(form(2)((operands, mods) => Instruction.DsMaxI32(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsMaxI64 ->
      decoder(form(2)((operands, mods) => Instruction.DsMaxI64(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsMaxNumF32 ->
      decoder(form(2)((operands, mods) => Instruction.DsMaxNumF32(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsMaxNumF64 ->
      decoder(form(2)((operands, mods) => Instruction.DsMaxNumF64(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsMaxNumRtnF32 ->
      decoder(form(3)((operands, mods) => Instruction.DsMaxNumRtnF32(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsMaxNumRtnF64 ->
      decoder(form(3)((operands, mods) => Instruction.DsMaxNumRtnF64(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsMaxRtnF32 ->
      decoder(form(3)((operands, mods) => Instruction.DsMaxRtnF32(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsMaxRtnF64 ->
      decoder(form(3)((operands, mods) => Instruction.DsMaxRtnF64(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsMaxRtnI32 ->
      decoder(form(3)((operands, mods) => Instruction.DsMaxRtnI32(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsMaxRtnI64 ->
      decoder(form(3)((operands, mods) => Instruction.DsMaxRtnI64(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsMaxRtnU32 ->
      decoder(form(3)((operands, mods) => Instruction.DsMaxRtnU32(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsMaxRtnU64 ->
      decoder(form(3)((operands, mods) => Instruction.DsMaxRtnU64(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsMaxSrc2F32 ->
      decoder(form(1)((operands, mods) => Instruction.DsMaxSrc2F32(operands(0), mods))),
    DataShareInstructionDefs.DsMaxSrc2F64 ->
      decoder(form(1)((operands, mods) => Instruction.DsMaxSrc2F64(operands(0), mods))),
    DataShareInstructionDefs.DsMaxSrc2I32 ->
      decoder(form(1)((operands, mods) => Instruction.DsMaxSrc2I32(operands(0), mods))),
    DataShareInstructionDefs.DsMaxSrc2I64 ->
      decoder(form(1)((operands, mods) => Instruction.DsMaxSrc2I64(operands(0), mods))),
    DataShareInstructionDefs.DsMaxSrc2U32 ->
      decoder(form(1)((operands, mods) => Instruction.DsMaxSrc2U32(operands(0), mods))),
    DataShareInstructionDefs.DsMaxSrc2U64 ->
      decoder(form(1)((operands, mods) => Instruction.DsMaxSrc2U64(operands(0), mods))),
    DataShareInstructionDefs.DsMaxU32 ->
      decoder(form(2)((operands, mods) => Instruction.DsMaxU32(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsMaxU64 ->
      decoder(form(2)((operands, mods) => Instruction.DsMaxU64(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsMinF32 ->
      decoder(form(2)((operands, mods) => Instruction.DsMinF32(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsMinF64 ->
      decoder(form(2)((operands, mods) => Instruction.DsMinF64(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsMinI32 ->
      decoder(form(2)((operands, mods) => Instruction.DsMinI32(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsMinI64 ->
      decoder(form(2)((operands, mods) => Instruction.DsMinI64(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsMinNumF32 ->
      decoder(form(2)((operands, mods) => Instruction.DsMinNumF32(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsMinNumF64 ->
      decoder(form(2)((operands, mods) => Instruction.DsMinNumF64(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsMinNumRtnF32 ->
      decoder(form(3)((operands, mods) => Instruction.DsMinNumRtnF32(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsMinNumRtnF64 ->
      decoder(form(3)((operands, mods) => Instruction.DsMinNumRtnF64(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsMinRtnF32 ->
      decoder(form(3)((operands, mods) => Instruction.DsMinRtnF32(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsMinRtnF64 ->
      decoder(form(3)((operands, mods) => Instruction.DsMinRtnF64(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsMinRtnI32 ->
      decoder(form(3)((operands, mods) => Instruction.DsMinRtnI32(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsMinRtnI64 ->
      decoder(form(3)((operands, mods) => Instruction.DsMinRtnI64(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsMinRtnU32 ->
      decoder(form(3)((operands, mods) => Instruction.DsMinRtnU32(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsMinRtnU64 ->
      decoder(form(3)((operands, mods) => Instruction.DsMinRtnU64(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsMinSrc2F32 ->
      decoder(form(1)((operands, mods) => Instruction.DsMinSrc2F32(operands(0), mods))),
    DataShareInstructionDefs.DsMinSrc2F64 ->
      decoder(form(1)((operands, mods) => Instruction.DsMinSrc2F64(operands(0), mods))),
    DataShareInstructionDefs.DsMinSrc2I32 ->
      decoder(form(1)((operands, mods) => Instruction.DsMinSrc2I32(operands(0), mods))),
    DataShareInstructionDefs.DsMinSrc2I64 ->
      decoder(form(1)((operands, mods) => Instruction.DsMinSrc2I64(operands(0), mods))),
    DataShareInstructionDefs.DsMinSrc2U32 ->
      decoder(form(1)((operands, mods) => Instruction.DsMinSrc2U32(operands(0), mods))),
    DataShareInstructionDefs.DsMinSrc2U64 ->
      decoder(form(1)((operands, mods) => Instruction.DsMinSrc2U64(operands(0), mods))),
    DataShareInstructionDefs.DsMinU32 ->
      decoder(form(2)((operands, mods) => Instruction.DsMinU32(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsMinU64 ->
      decoder(form(2)((operands, mods) => Instruction.DsMinU64(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsMskorB32 ->
      decoder(form(3)((operands, mods) => Instruction.DsMskorB32(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsMskorB64 ->
      decoder(form(3)((operands, mods) => Instruction.DsMskorB64(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsMskorRtnB32 -> decoder(form(4)((operands, mods) =>
      Instruction.DsMskorRtnB32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    DataShareInstructionDefs.DsMskorRtnB64 -> decoder(form(4)((operands, mods) =>
      Instruction.DsMskorRtnB64(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    DataShareInstructionDefs.DsNop -> decoder(form(0)((_, mods) => Instruction.DsNop(mods))),
    DataShareInstructionDefs.DsOrB32 ->
      decoder(form(2)((operands, mods) => Instruction.DsOrB32(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsOrB64 ->
      decoder(form(2)((operands, mods) => Instruction.DsOrB64(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsOrRtnB32 ->
      decoder(form(3)((operands, mods) => Instruction.DsOrRtnB32(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsOrRtnB64 ->
      decoder(form(3)((operands, mods) => Instruction.DsOrRtnB64(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsOrSrc2B32 ->
      decoder(form(1)((operands, mods) => Instruction.DsOrSrc2B32(operands(0), mods))),
    DataShareInstructionDefs.DsOrSrc2B64 ->
      decoder(form(1)((operands, mods) => Instruction.DsOrSrc2B64(operands(0), mods))),
    DataShareInstructionDefs.DsOrderedCount ->
      decoder(form(2)((operands, mods) => Instruction.DsOrderedCount(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsParamLoad ->
      decoder(form(2)((operands, mods) => Instruction.DsParamLoad(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsPermuteB32 ->
      decoder(form(3)((operands, mods) => Instruction.DsPermuteB32(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsPkAddBf16 ->
      decoder(form(2)((operands, mods) => Instruction.DsPkAddBf16(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsPkAddF16 ->
      decoder(form(2)((operands, mods) => Instruction.DsPkAddF16(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsPkAddRtnBf16 ->
      decoder(form(3)((operands, mods) => Instruction.DsPkAddRtnBf16(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsPkAddRtnF16 ->
      decoder(form(3)((operands, mods) => Instruction.DsPkAddRtnF16(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsRead2B32 ->
      decoder(form(2)((operands, mods) => Instruction.DsRead2B32(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsRead2B64 ->
      decoder(form(2)((operands, mods) => Instruction.DsRead2B64(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsRead2st64B32 ->
      decoder(form(2)((operands, mods) => Instruction.DsRead2st64B32(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsRead2st64B64 ->
      decoder(form(2)((operands, mods) => Instruction.DsRead2st64B64(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsReadAddtidB32 ->
      decoder(form(1)((operands, mods) => Instruction.DsReadAddtidB32(operands(0), mods))),
    DataShareInstructionDefs.DsReadB128 ->
      decoder(form(2)((operands, mods) => Instruction.DsReadB128(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsReadB32 ->
      decoder(form(2)((operands, mods) => Instruction.DsReadB32(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsReadB64 ->
      decoder(form(2)((operands, mods) => Instruction.DsReadB64(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsReadB64TrB16 ->
      decoder(form(2)((operands, mods) => Instruction.DsReadB64TrB16(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsReadB64TrB4 ->
      decoder(form(2)((operands, mods) => Instruction.DsReadB64TrB4(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsReadB64TrB8 ->
      decoder(form(2)((operands, mods) => Instruction.DsReadB64TrB8(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsReadB96 ->
      decoder(form(2)((operands, mods) => Instruction.DsReadB96(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsReadB96TrB6 ->
      decoder(form(2)((operands, mods) => Instruction.DsReadB96TrB6(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsReadI16 ->
      decoder(form(2)((operands, mods) => Instruction.DsReadI16(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsReadI8 ->
      decoder(form(2)((operands, mods) => Instruction.DsReadI8(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsReadI8D16 ->
      decoder(form(2)((operands, mods) => Instruction.DsReadI8D16(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsReadI8D16Hi ->
      decoder(form(2)((operands, mods) => Instruction.DsReadI8D16Hi(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsReadU16 ->
      decoder(form(2)((operands, mods) => Instruction.DsReadU16(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsReadU16D16 ->
      decoder(form(2)((operands, mods) => Instruction.DsReadU16D16(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsReadU16D16Hi ->
      decoder(form(2)((operands, mods) => Instruction.DsReadU16D16Hi(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsReadU8 ->
      decoder(form(2)((operands, mods) => Instruction.DsReadU8(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsReadU8D16 ->
      decoder(form(2)((operands, mods) => Instruction.DsReadU8D16(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsReadU8D16Hi ->
      decoder(form(2)((operands, mods) => Instruction.DsReadU8D16Hi(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsRsubRtnU32 ->
      decoder(form(3)((operands, mods) => Instruction.DsRsubRtnU32(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsRsubRtnU64 ->
      decoder(form(3)((operands, mods) => Instruction.DsRsubRtnU64(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsRsubSrc2U32 ->
      decoder(form(1)((operands, mods) => Instruction.DsRsubSrc2U32(operands(0), mods))),
    DataShareInstructionDefs.DsRsubSrc2U64 ->
      decoder(form(1)((operands, mods) => Instruction.DsRsubSrc2U64(operands(0), mods))),
    DataShareInstructionDefs.DsRsubU32 ->
      decoder(form(2)((operands, mods) => Instruction.DsRsubU32(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsRsubU64 ->
      decoder(form(2)((operands, mods) => Instruction.DsRsubU64(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsStore2addrB32 ->
      decoder(form(3)((operands, mods) => Instruction.DsStore2addrB32(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsStore2addrB64 ->
      decoder(form(3)((operands, mods) => Instruction.DsStore2addrB64(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsStore2addrStride64B32 -> decoder(form(3)((operands, mods) =>
      Instruction.DsStore2addrStride64B32(operands(0), operands(1), operands(2), mods)
    )),
    DataShareInstructionDefs.DsStore2addrStride64B64 -> decoder(form(3)((operands, mods) =>
      Instruction.DsStore2addrStride64B64(operands(0), operands(1), operands(2), mods)
    )),
    DataShareInstructionDefs.DsStoreAddtidB32 ->
      decoder(form(1)((operands, mods) => Instruction.DsStoreAddtidB32(operands(0), mods))),
    DataShareInstructionDefs.DsStoreB128 ->
      decoder(form(2)((operands, mods) => Instruction.DsStoreB128(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsStoreB16 ->
      decoder(form(2)((operands, mods) => Instruction.DsStoreB16(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsStoreB16D16Hi ->
      decoder(form(2)((operands, mods) => Instruction.DsStoreB16D16Hi(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsStoreB32 ->
      decoder(form(2)((operands, mods) => Instruction.DsStoreB32(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsStoreB64 ->
      decoder(form(2)((operands, mods) => Instruction.DsStoreB64(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsStoreB8 ->
      decoder(form(2)((operands, mods) => Instruction.DsStoreB8(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsStoreB8D16Hi ->
      decoder(form(2)((operands, mods) => Instruction.DsStoreB8D16Hi(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsStoreB96 ->
      decoder(form(2)((operands, mods) => Instruction.DsStoreB96(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsStorexchg2addrRtnB32 -> decoder(form(4)((operands, mods) =>
      Instruction.DsStorexchg2addrRtnB32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    DataShareInstructionDefs.DsStorexchg2addrRtnB64 -> decoder(form(4)((operands, mods) =>
      Instruction.DsStorexchg2addrRtnB64(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    DataShareInstructionDefs.DsStorexchg2addrStride64RtnB32 -> decoder(form(4)((operands, mods) =>
      Instruction.DsStorexchg2addrStride64RtnB32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    DataShareInstructionDefs.DsStorexchg2addrStride64RtnB64 -> decoder(form(4)((operands, mods) =>
      Instruction.DsStorexchg2addrStride64RtnB64(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    DataShareInstructionDefs.DsStorexchgRtnB32 ->
      decoder(form(3)((operands, mods) => Instruction.DsStorexchgRtnB32(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsStorexchgRtnB64 ->
      decoder(form(3)((operands, mods) => Instruction.DsStorexchgRtnB64(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsSubClampRtnU32 ->
      decoder(form(3)((operands, mods) => Instruction.DsSubClampRtnU32(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsSubClampU32 ->
      decoder(form(2)((operands, mods) => Instruction.DsSubClampU32(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsSubGsRegRtn ->
      decoder(form(2)((operands, mods) => Instruction.DsSubGsRegRtn(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsSubRtnU32 ->
      decoder(form(3)((operands, mods) => Instruction.DsSubRtnU32(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsSubRtnU64 ->
      decoder(form(3)((operands, mods) => Instruction.DsSubRtnU64(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsSubSrc2U32 ->
      decoder(form(1)((operands, mods) => Instruction.DsSubSrc2U32(operands(0), mods))),
    DataShareInstructionDefs.DsSubSrc2U64 ->
      decoder(form(1)((operands, mods) => Instruction.DsSubSrc2U64(operands(0), mods))),
    DataShareInstructionDefs.DsSubU32 ->
      decoder(form(2)((operands, mods) => Instruction.DsSubU32(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsSubU64 ->
      decoder(form(2)((operands, mods) => Instruction.DsSubU64(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsSwizzleB32 ->
      decoder(form(2)((operands, mods) => Instruction.DsSwizzleB32(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsWrapRtnB32 -> decoder(form(4)((operands, mods) =>
      Instruction.DsWrapRtnB32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    DataShareInstructionDefs.DsWrite2B32 ->
      decoder(form(3)((operands, mods) => Instruction.DsWrite2B32(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsWrite2B64 ->
      decoder(form(3)((operands, mods) => Instruction.DsWrite2B64(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsWrite2st64B32 ->
      decoder(form(3)((operands, mods) => Instruction.DsWrite2st64B32(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsWrite2st64B64 ->
      decoder(form(3)((operands, mods) => Instruction.DsWrite2st64B64(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsWriteAddtidB32 ->
      decoder(form(1)((operands, mods) => Instruction.DsWriteAddtidB32(operands(0), mods))),
    DataShareInstructionDefs.DsWriteB128 ->
      decoder(form(2)((operands, mods) => Instruction.DsWriteB128(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsWriteB16 ->
      decoder(form(2)((operands, mods) => Instruction.DsWriteB16(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsWriteB16D16Hi ->
      decoder(form(2)((operands, mods) => Instruction.DsWriteB16D16Hi(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsWriteB32 ->
      decoder(form(2)((operands, mods) => Instruction.DsWriteB32(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsWriteB64 ->
      decoder(form(2)((operands, mods) => Instruction.DsWriteB64(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsWriteB8 ->
      decoder(form(2)((operands, mods) => Instruction.DsWriteB8(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsWriteB8D16Hi ->
      decoder(form(2)((operands, mods) => Instruction.DsWriteB8D16Hi(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsWriteB96 ->
      decoder(form(2)((operands, mods) => Instruction.DsWriteB96(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsWriteSrc2B32 ->
      decoder(form(1)((operands, mods) => Instruction.DsWriteSrc2B32(operands(0), mods))),
    DataShareInstructionDefs.DsWriteSrc2B64 ->
      decoder(form(1)((operands, mods) => Instruction.DsWriteSrc2B64(operands(0), mods))),
    DataShareInstructionDefs.DsWrxchg2RtnB32 -> decoder(form(4)((operands, mods) =>
      Instruction.DsWrxchg2RtnB32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    DataShareInstructionDefs.DsWrxchg2RtnB64 -> decoder(form(4)((operands, mods) =>
      Instruction.DsWrxchg2RtnB64(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    DataShareInstructionDefs.DsWrxchg2st64RtnB32 -> decoder(form(4)((operands, mods) =>
      Instruction.DsWrxchg2st64RtnB32(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    DataShareInstructionDefs.DsWrxchg2st64RtnB64 -> decoder(form(4)((operands, mods) =>
      Instruction.DsWrxchg2st64RtnB64(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    DataShareInstructionDefs.DsWrxchgRtnB32 ->
      decoder(form(3)((operands, mods) => Instruction.DsWrxchgRtnB32(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsWrxchgRtnB64 ->
      decoder(form(3)((operands, mods) => Instruction.DsWrxchgRtnB64(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsXorB32 ->
      decoder(form(2)((operands, mods) => Instruction.DsXorB32(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsXorB64 ->
      decoder(form(2)((operands, mods) => Instruction.DsXorB64(operands(0), operands(1), mods))),
    DataShareInstructionDefs.DsXorRtnB32 ->
      decoder(form(3)((operands, mods) => Instruction.DsXorRtnB32(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsXorRtnB64 ->
      decoder(form(3)((operands, mods) => Instruction.DsXorRtnB64(operands(0), operands(1), operands(2), mods))),
    DataShareInstructionDefs.DsXorSrc2B32 ->
      decoder(form(1)((operands, mods) => Instruction.DsXorSrc2B32(operands(0), mods))),
    DataShareInstructionDefs.DsXorSrc2B64 ->
      decoder(form(1)((operands, mods) => Instruction.DsXorSrc2B64(operands(0), mods)))
  )
}
