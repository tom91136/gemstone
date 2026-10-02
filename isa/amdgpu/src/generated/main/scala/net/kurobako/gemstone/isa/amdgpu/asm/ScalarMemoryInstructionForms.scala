// Generated from the pinned AMDGPU instruction forms. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu.asm
import net.kurobako.gemstone.isa.amdgpu.*
import Instruction.*
private object ScalarMemoryInstructionForms {
  val decoders: Map[Opcode, Decoder] = Map(
    ScalarMemoryInstructionDefs.SAtcProbe ->
      decoder(form(3)((operands, mods) => Instruction.SAtcProbe(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SAtcProbeBuffer ->
      decoder(form(3)((operands, mods) => Instruction.SAtcProbeBuffer(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SAtomicAdd ->
      decoder(form(3)((operands, mods) => Instruction.SAtomicAdd(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SAtomicAddX2 ->
      decoder(form(3)((operands, mods) => Instruction.SAtomicAddX2(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SAtomicAnd ->
      decoder(form(3)((operands, mods) => Instruction.SAtomicAnd(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SAtomicAndX2 ->
      decoder(form(3)((operands, mods) => Instruction.SAtomicAndX2(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SAtomicCmpswap ->
      decoder(form(3)((operands, mods) => Instruction.SAtomicCmpswap(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SAtomicCmpswapX2 ->
      decoder(form(3)((operands, mods) => Instruction.SAtomicCmpswapX2(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SAtomicDec ->
      decoder(form(3)((operands, mods) => Instruction.SAtomicDec(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SAtomicDecX2 ->
      decoder(form(3)((operands, mods) => Instruction.SAtomicDecX2(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SAtomicInc ->
      decoder(form(3)((operands, mods) => Instruction.SAtomicInc(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SAtomicIncX2 ->
      decoder(form(3)((operands, mods) => Instruction.SAtomicIncX2(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SAtomicOr ->
      decoder(form(3)((operands, mods) => Instruction.SAtomicOr(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SAtomicOrX2 ->
      decoder(form(3)((operands, mods) => Instruction.SAtomicOrX2(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SAtomicSmax ->
      decoder(form(3)((operands, mods) => Instruction.SAtomicSmax(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SAtomicSmaxX2 ->
      decoder(form(3)((operands, mods) => Instruction.SAtomicSmaxX2(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SAtomicSmin ->
      decoder(form(3)((operands, mods) => Instruction.SAtomicSmin(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SAtomicSminX2 ->
      decoder(form(3)((operands, mods) => Instruction.SAtomicSminX2(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SAtomicSub ->
      decoder(form(3)((operands, mods) => Instruction.SAtomicSub(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SAtomicSubX2 ->
      decoder(form(3)((operands, mods) => Instruction.SAtomicSubX2(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SAtomicSwap ->
      decoder(form(3)((operands, mods) => Instruction.SAtomicSwap(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SAtomicSwapX2 ->
      decoder(form(3)((operands, mods) => Instruction.SAtomicSwapX2(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SAtomicUmax ->
      decoder(form(3)((operands, mods) => Instruction.SAtomicUmax(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SAtomicUmaxX2 ->
      decoder(form(3)((operands, mods) => Instruction.SAtomicUmaxX2(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SAtomicUmin ->
      decoder(form(3)((operands, mods) => Instruction.SAtomicUmin(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SAtomicUminX2 ->
      decoder(form(3)((operands, mods) => Instruction.SAtomicUminX2(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SAtomicXor ->
      decoder(form(3)((operands, mods) => Instruction.SAtomicXor(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SAtomicXorX2 ->
      decoder(form(3)((operands, mods) => Instruction.SAtomicXorX2(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SBufferAtomicAdd ->
      decoder(form(3)((operands, mods) => Instruction.SBufferAtomicAdd(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SBufferAtomicAddX2 ->
      decoder(form(3)((operands, mods) => Instruction.SBufferAtomicAddX2(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SBufferAtomicAnd ->
      decoder(form(3)((operands, mods) => Instruction.SBufferAtomicAnd(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SBufferAtomicAndX2 ->
      decoder(form(3)((operands, mods) => Instruction.SBufferAtomicAndX2(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SBufferAtomicCmpswap -> decoder(form(3)((operands, mods) =>
      Instruction.SBufferAtomicCmpswap(operands(0), operands(1), operands(2), mods)
    )),
    ScalarMemoryInstructionDefs.SBufferAtomicCmpswapX2 -> decoder(form(3)((operands, mods) =>
      Instruction.SBufferAtomicCmpswapX2(operands(0), operands(1), operands(2), mods)
    )),
    ScalarMemoryInstructionDefs.SBufferAtomicDec ->
      decoder(form(3)((operands, mods) => Instruction.SBufferAtomicDec(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SBufferAtomicDecX2 ->
      decoder(form(3)((operands, mods) => Instruction.SBufferAtomicDecX2(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SBufferAtomicInc ->
      decoder(form(3)((operands, mods) => Instruction.SBufferAtomicInc(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SBufferAtomicIncX2 ->
      decoder(form(3)((operands, mods) => Instruction.SBufferAtomicIncX2(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SBufferAtomicOr ->
      decoder(form(3)((operands, mods) => Instruction.SBufferAtomicOr(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SBufferAtomicOrX2 ->
      decoder(form(3)((operands, mods) => Instruction.SBufferAtomicOrX2(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SBufferAtomicSmax ->
      decoder(form(3)((operands, mods) => Instruction.SBufferAtomicSmax(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SBufferAtomicSmaxX2 -> decoder(form(3)((operands, mods) =>
      Instruction.SBufferAtomicSmaxX2(operands(0), operands(1), operands(2), mods)
    )),
    ScalarMemoryInstructionDefs.SBufferAtomicSmin ->
      decoder(form(3)((operands, mods) => Instruction.SBufferAtomicSmin(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SBufferAtomicSminX2 -> decoder(form(3)((operands, mods) =>
      Instruction.SBufferAtomicSminX2(operands(0), operands(1), operands(2), mods)
    )),
    ScalarMemoryInstructionDefs.SBufferAtomicSub ->
      decoder(form(3)((operands, mods) => Instruction.SBufferAtomicSub(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SBufferAtomicSubX2 ->
      decoder(form(3)((operands, mods) => Instruction.SBufferAtomicSubX2(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SBufferAtomicSwap ->
      decoder(form(3)((operands, mods) => Instruction.SBufferAtomicSwap(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SBufferAtomicSwapX2 -> decoder(form(3)((operands, mods) =>
      Instruction.SBufferAtomicSwapX2(operands(0), operands(1), operands(2), mods)
    )),
    ScalarMemoryInstructionDefs.SBufferAtomicUmax ->
      decoder(form(3)((operands, mods) => Instruction.SBufferAtomicUmax(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SBufferAtomicUmaxX2 -> decoder(form(3)((operands, mods) =>
      Instruction.SBufferAtomicUmaxX2(operands(0), operands(1), operands(2), mods)
    )),
    ScalarMemoryInstructionDefs.SBufferAtomicUmin ->
      decoder(form(3)((operands, mods) => Instruction.SBufferAtomicUmin(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SBufferAtomicUminX2 -> decoder(form(3)((operands, mods) =>
      Instruction.SBufferAtomicUminX2(operands(0), operands(1), operands(2), mods)
    )),
    ScalarMemoryInstructionDefs.SBufferAtomicXor ->
      decoder(form(3)((operands, mods) => Instruction.SBufferAtomicXor(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SBufferAtomicXorX2 ->
      decoder(form(3)((operands, mods) => Instruction.SBufferAtomicXorX2(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SBufferLoadB128 ->
      decoder(form(3)((operands, mods) => Instruction.SBufferLoadB128(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SBufferLoadB256 ->
      decoder(form(3)((operands, mods) => Instruction.SBufferLoadB256(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SBufferLoadB32 ->
      decoder(form(3)((operands, mods) => Instruction.SBufferLoadB32(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SBufferLoadB512 ->
      decoder(form(3)((operands, mods) => Instruction.SBufferLoadB512(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SBufferLoadB64 ->
      decoder(form(3)((operands, mods) => Instruction.SBufferLoadB64(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SBufferLoadB96 ->
      decoder(form(3)((operands, mods) => Instruction.SBufferLoadB96(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SBufferLoadDword ->
      decoder(form(3)((operands, mods) => Instruction.SBufferLoadDword(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SBufferLoadDwordx16 -> decoder(form(3)((operands, mods) =>
      Instruction.SBufferLoadDwordx16(operands(0), operands(1), operands(2), mods)
    )),
    ScalarMemoryInstructionDefs.SBufferLoadDwordx2 ->
      decoder(form(3)((operands, mods) => Instruction.SBufferLoadDwordx2(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SBufferLoadDwordx4 ->
      decoder(form(3)((operands, mods) => Instruction.SBufferLoadDwordx4(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SBufferLoadDwordx8 ->
      decoder(form(3)((operands, mods) => Instruction.SBufferLoadDwordx8(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SBufferLoadI16 ->
      decoder(form(3)((operands, mods) => Instruction.SBufferLoadI16(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SBufferLoadI8 ->
      decoder(form(3)((operands, mods) => Instruction.SBufferLoadI8(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SBufferLoadU16 ->
      decoder(form(3)((operands, mods) => Instruction.SBufferLoadU16(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SBufferLoadU8 ->
      decoder(form(3)((operands, mods) => Instruction.SBufferLoadU8(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SBufferPrefetchData -> decoder(form(4)((operands, mods) =>
      Instruction.SBufferPrefetchData(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ScalarMemoryInstructionDefs.SBufferStoreDword ->
      decoder(form(3)((operands, mods) => Instruction.SBufferStoreDword(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SBufferStoreDwordx2 -> decoder(form(3)((operands, mods) =>
      Instruction.SBufferStoreDwordx2(operands(0), operands(1), operands(2), mods)
    )),
    ScalarMemoryInstructionDefs.SBufferStoreDwordx4 -> decoder(form(3)((operands, mods) =>
      Instruction.SBufferStoreDwordx4(operands(0), operands(1), operands(2), mods)
    )),
    ScalarMemoryInstructionDefs.SDcacheDiscard ->
      decoder(form(2)((operands, mods) => Instruction.SDcacheDiscard(operands(0), operands(1), mods))),
    ScalarMemoryInstructionDefs.SDcacheDiscardX2 ->
      decoder(form(2)((operands, mods) => Instruction.SDcacheDiscardX2(operands(0), operands(1), mods))),
    ScalarMemoryInstructionDefs.SDcacheInv -> decoder(form(0)((_, mods) => Instruction.SDcacheInv(mods))),
    ScalarMemoryInstructionDefs.SDcacheInvVol -> decoder(form(0)((_, mods) => Instruction.SDcacheInvVol(mods))),
    ScalarMemoryInstructionDefs.SDcacheWb -> decoder(form(0)((_, mods) => Instruction.SDcacheWb(mods))),
    ScalarMemoryInstructionDefs.SDcacheWbVol -> decoder(form(0)((_, mods) => Instruction.SDcacheWbVol(mods))),
    ScalarMemoryInstructionDefs.SGetWaveidInWorkgroup ->
      decoder(form(1)((operands, mods) => Instruction.SGetWaveidInWorkgroup(operands(0), mods))),
    ScalarMemoryInstructionDefs.SGl1Inv -> decoder(form(0)((_, mods) => Instruction.SGl1Inv(mods))),
    ScalarMemoryInstructionDefs.SLoadB128 ->
      decoder(form(3)((operands, mods) => Instruction.SLoadB128(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SLoadB256 ->
      decoder(form(3)((operands, mods) => Instruction.SLoadB256(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SLoadB32 ->
      decoder(form(3)((operands, mods) => Instruction.SLoadB32(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SLoadB512 ->
      decoder(form(3)((operands, mods) => Instruction.SLoadB512(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SLoadB64 ->
      decoder(form(3)((operands, mods) => Instruction.SLoadB64(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SLoadB96 ->
      decoder(form(3)((operands, mods) => Instruction.SLoadB96(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SLoadDword ->
      decoder(form(3)((operands, mods) => Instruction.SLoadDword(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SLoadDwordx16 ->
      decoder(form(3)((operands, mods) => Instruction.SLoadDwordx16(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SLoadDwordx2 ->
      decoder(form(3)((operands, mods) => Instruction.SLoadDwordx2(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SLoadDwordx4 ->
      decoder(form(3)((operands, mods) => Instruction.SLoadDwordx4(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SLoadDwordx8 ->
      decoder(form(3)((operands, mods) => Instruction.SLoadDwordx8(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SLoadI16 ->
      decoder(form(3)((operands, mods) => Instruction.SLoadI16(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SLoadI8 ->
      decoder(form(3)((operands, mods) => Instruction.SLoadI8(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SLoadU16 ->
      decoder(form(3)((operands, mods) => Instruction.SLoadU16(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SLoadU8 ->
      decoder(form(3)((operands, mods) => Instruction.SLoadU8(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SMemrealtime ->
      decoder(form(1)((operands, mods) => Instruction.SMemrealtime(operands(0), mods))),
    ScalarMemoryInstructionDefs.SMemtime ->
      decoder(form(1)((operands, mods) => Instruction.SMemtime(operands(0), mods))),
    ScalarMemoryInstructionDefs.SPrefetchData -> decoder(form(4)((operands, mods) =>
      Instruction.SPrefetchData(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ScalarMemoryInstructionDefs.SPrefetchDataPcRel ->
      decoder(form(3)((operands, mods) => Instruction.SPrefetchDataPcRel(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SPrefetchInst -> decoder(form(4)((operands, mods) =>
      Instruction.SPrefetchInst(operands(0), operands(1), operands(2), operands(3), mods)
    )),
    ScalarMemoryInstructionDefs.SPrefetchInstPcRel ->
      decoder(form(3)((operands, mods) => Instruction.SPrefetchInstPcRel(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SScratchLoadDword ->
      decoder(form(3)((operands, mods) => Instruction.SScratchLoadDword(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SScratchLoadDwordx2 -> decoder(form(3)((operands, mods) =>
      Instruction.SScratchLoadDwordx2(operands(0), operands(1), operands(2), mods)
    )),
    ScalarMemoryInstructionDefs.SScratchLoadDwordx4 -> decoder(form(3)((operands, mods) =>
      Instruction.SScratchLoadDwordx4(operands(0), operands(1), operands(2), mods)
    )),
    ScalarMemoryInstructionDefs.SScratchStoreDword ->
      decoder(form(3)((operands, mods) => Instruction.SScratchStoreDword(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SScratchStoreDwordx2 -> decoder(form(3)((operands, mods) =>
      Instruction.SScratchStoreDwordx2(operands(0), operands(1), operands(2), mods)
    )),
    ScalarMemoryInstructionDefs.SScratchStoreDwordx4 -> decoder(form(3)((operands, mods) =>
      Instruction.SScratchStoreDwordx4(operands(0), operands(1), operands(2), mods)
    )),
    ScalarMemoryInstructionDefs.SStoreDword ->
      decoder(form(3)((operands, mods) => Instruction.SStoreDword(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SStoreDwordx2 ->
      decoder(form(3)((operands, mods) => Instruction.SStoreDwordx2(operands(0), operands(1), operands(2), mods))),
    ScalarMemoryInstructionDefs.SStoreDwordx4 ->
      decoder(form(3)((operands, mods) => Instruction.SStoreDwordx4(operands(0), operands(1), operands(2), mods)))
  )
}
