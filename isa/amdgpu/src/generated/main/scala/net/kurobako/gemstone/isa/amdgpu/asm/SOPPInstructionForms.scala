// Generated from the pinned AMDGPU instruction forms. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu.asm
import net.kurobako.gemstone.isa.amdgpu.*
import Instruction.*
private object SOPPInstructionForms {
  val decoders: Map[Opcode, Decoder] = Map(
    SOPPInstructionDefs.SBarrier -> decoder(form(0)((_, mods) => Instruction.SBarrier(mods))),
    SOPPInstructionDefs.SBarrierLeave -> decoder(form(0)((_, mods) => Instruction.SBarrierLeave(mods))),
    SOPPInstructionDefs.SBarrierWait ->
      decoder(form(1)((operands, mods) => Instruction.SBarrierWait(operands(0), mods))),
    SOPPInstructionDefs.SBranch -> decoder(form(1)((operands, mods) => Instruction.SBranch(operands(0), mods))),
    SOPPInstructionDefs.SCbranchCdbgsys ->
      decoder(form(1)((operands, mods) => Instruction.SCbranchCdbgsys(operands(0), mods))),
    SOPPInstructionDefs.SCbranchCdbgsysAndUser ->
      decoder(form(1)((operands, mods) => Instruction.SCbranchCdbgsysAndUser(operands(0), mods))),
    SOPPInstructionDefs.SCbranchCdbgsysOrUser ->
      decoder(form(1)((operands, mods) => Instruction.SCbranchCdbgsysOrUser(operands(0), mods))),
    SOPPInstructionDefs.SCbranchCdbguser ->
      decoder(form(1)((operands, mods) => Instruction.SCbranchCdbguser(operands(0), mods))),
    SOPPInstructionDefs.SCbranchExecnz ->
      decoder(form(1)((operands, mods) => Instruction.SCbranchExecnz(operands(0), mods))),
    SOPPInstructionDefs.SCbranchExecz ->
      decoder(form(1)((operands, mods) => Instruction.SCbranchExecz(operands(0), mods))),
    SOPPInstructionDefs.SCbranchScc0 ->
      decoder(form(1)((operands, mods) => Instruction.SCbranchScc0(operands(0), mods))),
    SOPPInstructionDefs.SCbranchScc1 ->
      decoder(form(1)((operands, mods) => Instruction.SCbranchScc1(operands(0), mods))),
    SOPPInstructionDefs.SCbranchVccnz ->
      decoder(form(1)((operands, mods) => Instruction.SCbranchVccnz(operands(0), mods))),
    SOPPInstructionDefs.SCbranchVccz ->
      decoder(form(1)((operands, mods) => Instruction.SCbranchVccz(operands(0), mods))),
    SOPPInstructionDefs.SClause -> decoder(form(1)((operands, mods) => Instruction.SClause(operands(0), mods))),
    SOPPInstructionDefs.SCodeEnd -> decoder(form(0)((_, mods) => Instruction.SCodeEnd(mods))),
    SOPPInstructionDefs.SDecperflevel ->
      decoder(form(1)((operands, mods) => Instruction.SDecperflevel(operands(0), mods))),
    SOPPInstructionDefs.SDelayAlu -> decoder(form(1)((operands, mods) => Instruction.SDelayAlu(operands(0), mods))),
    SOPPInstructionDefs.SDenormMode -> decoder(form(1)((operands, mods) => Instruction.SDenormMode(operands(0), mods))),
    SOPPInstructionDefs.SEndpgm -> decoder(
      form(0)((_, mods) => Instruction.SEndpgm_0(mods)),
      form(1)((operands, mods) => Instruction.SEndpgm_1(operands(0), mods))
    ),
    SOPPInstructionDefs.SEndpgmOrderedPsDone -> decoder(form(0)((_, mods) => Instruction.SEndpgmOrderedPsDone(mods))),
    SOPPInstructionDefs.SEndpgmSaved -> decoder(form(0)((_, mods) => Instruction.SEndpgmSaved(mods))),
    SOPPInstructionDefs.SIcacheInv -> decoder(form(0)((_, mods) => Instruction.SIcacheInv(mods))),
    SOPPInstructionDefs.SIncperflevel ->
      decoder(form(1)((operands, mods) => Instruction.SIncperflevel(operands(0), mods))),
    SOPPInstructionDefs.SInstPrefetch ->
      decoder(form(1)((operands, mods) => Instruction.SInstPrefetch(operands(0), mods))),
    SOPPInstructionDefs.SMonitorSleep ->
      decoder(form(1)((operands, mods) => Instruction.SMonitorSleep(operands(0), mods))),
    SOPPInstructionDefs.SNop -> decoder(form(1)((operands, mods) => Instruction.SNop(operands(0), mods))),
    SOPPInstructionDefs.SRoundMode -> decoder(form(1)((operands, mods) => Instruction.SRoundMode(operands(0), mods))),
    SOPPInstructionDefs.SSendmsg -> decoder(form(1)((operands, mods) => Instruction.SSendmsg(operands(0), mods))),
    SOPPInstructionDefs.SSendmsghalt ->
      decoder(form(1)((operands, mods) => Instruction.SSendmsghalt(operands(0), mods))),
    SOPPInstructionDefs.SSetGprIdxMode ->
      decoder(form(1)((operands, mods) => Instruction.SSetGprIdxMode(operands(0), mods))),
    SOPPInstructionDefs.SSetGprIdxOff -> decoder(form(0)((_, mods) => Instruction.SSetGprIdxOff(mods))),
    SOPPInstructionDefs.SSetInstPrefetchDistance ->
      decoder(form(1)((operands, mods) => Instruction.SSetInstPrefetchDistance(operands(0), mods))),
    SOPPInstructionDefs.SSetValuCoexecMode ->
      decoder(form(1)((operands, mods) => Instruction.SSetValuCoexecMode(operands(0), mods))),
    SOPPInstructionDefs.SSetVgprMsb -> decoder(form(1)((operands, mods) => Instruction.SSetVgprMsb(operands(0), mods))),
    SOPPInstructionDefs.SSethalt -> decoder(form(1)((operands, mods) => Instruction.SSethalt(operands(0), mods))),
    SOPPInstructionDefs.SSetkill -> decoder(form(1)((operands, mods) => Instruction.SSetkill(operands(0), mods))),
    SOPPInstructionDefs.SSetprio -> decoder(form(1)((operands, mods) => Instruction.SSetprio(operands(0), mods))),
    SOPPInstructionDefs.SSetprioIncWg ->
      decoder(form(1)((operands, mods) => Instruction.SSetprioIncWg(operands(0), mods))),
    SOPPInstructionDefs.SSingleuseVdst -> decoder(form(0)((_, mods) => Instruction.SSingleuseVdst(mods))),
    SOPPInstructionDefs.SSleep -> decoder(form(1)((operands, mods) => Instruction.SSleep(operands(0), mods))),
    SOPPInstructionDefs.STrap -> decoder(form(1)((operands, mods) => Instruction.STrap(operands(0), mods))),
    SOPPInstructionDefs.STtracedata -> decoder(form(0)((_, mods) => Instruction.STtracedata(mods))),
    SOPPInstructionDefs.STtracedataImm ->
      decoder(form(1)((operands, mods) => Instruction.STtracedataImm(operands(0), mods))),
    SOPPInstructionDefs.SWaitAlu -> decoder(form(1)((operands, mods) => Instruction.SWaitAlu(operands(0), mods))),
    SOPPInstructionDefs.SWaitAsynccnt ->
      decoder(form(1)((operands, mods) => Instruction.SWaitAsynccnt(operands(0), mods))),
    SOPPInstructionDefs.SWaitBvhcnt -> decoder(form(1)((operands, mods) => Instruction.SWaitBvhcnt(operands(0), mods))),
    SOPPInstructionDefs.SWaitDscnt -> decoder(form(1)((operands, mods) => Instruction.SWaitDscnt(operands(0), mods))),
    SOPPInstructionDefs.SWaitEvent -> decoder(form(1)((operands, mods) => Instruction.SWaitEvent(operands(0), mods))),
    SOPPInstructionDefs.SWaitExpcnt -> decoder(form(1)((operands, mods) => Instruction.SWaitExpcnt(operands(0), mods))),
    SOPPInstructionDefs.SWaitIdle -> decoder(form(0)((_, mods) => Instruction.SWaitIdle(mods))),
    SOPPInstructionDefs.SWaitKmcnt -> decoder(form(1)((operands, mods) => Instruction.SWaitKmcnt(operands(0), mods))),
    SOPPInstructionDefs.SWaitLoadcnt ->
      decoder(form(1)((operands, mods) => Instruction.SWaitLoadcnt(operands(0), mods))),
    SOPPInstructionDefs.SWaitLoadcntDscnt ->
      decoder(form(1)((operands, mods) => Instruction.SWaitLoadcntDscnt(operands(0), mods))),
    SOPPInstructionDefs.SWaitSamplecnt ->
      decoder(form(1)((operands, mods) => Instruction.SWaitSamplecnt(operands(0), mods))),
    SOPPInstructionDefs.SWaitStorecnt ->
      decoder(form(1)((operands, mods) => Instruction.SWaitStorecnt(operands(0), mods))),
    SOPPInstructionDefs.SWaitStorecntDscnt ->
      decoder(form(1)((operands, mods) => Instruction.SWaitStorecntDscnt(operands(0), mods))),
    SOPPInstructionDefs.SWaitXcnt -> decoder(form(1)((operands, mods) => Instruction.SWaitXcnt(operands(0), mods))),
    SOPPInstructionDefs.SWaitcnt -> decoder(form(1)((operands, mods) => Instruction.SWaitcnt(operands(0), mods))),
    SOPPInstructionDefs.SWaitcntDepctr ->
      decoder(form(1)((operands, mods) => Instruction.SWaitcntDepctr(operands(0), mods))),
    SOPPInstructionDefs.SWakeup -> decoder(form(0)((_, mods) => Instruction.SWakeup(mods)))
  )
}
