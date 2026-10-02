// Generated from the pinned AMDGPU instruction forms. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu.asm
import net.kurobako.gemstone.isa.amdgpu.*
import Instruction.*
private object SOPKInstructionForms {
  val decoders: Map[Opcode, Decoder] = Map(
    SOPKInstructionDefs.SAddkCoI32 ->
      decoder(form(2)((operands, mods) => Instruction.SAddkCoI32(operands(0), operands(1), mods))),
    SOPKInstructionDefs.SAddkI32 ->
      decoder(form(2)((operands, mods) => Instruction.SAddkI32(operands(0), operands(1), mods))),
    SOPKInstructionDefs.SCallB64 ->
      decoder(form(2)((operands, mods) => Instruction.SCallB64(operands(0), operands(1), mods))),
    SOPKInstructionDefs.SCallI64 ->
      decoder(form(2)((operands, mods) => Instruction.SCallI64(operands(0), operands(1), mods))),
    SOPKInstructionDefs.SCbranchIFork ->
      decoder(form(2)((operands, mods) => Instruction.SCbranchIFork(operands(0), operands(1), mods))),
    SOPKInstructionDefs.SCmovkI32 ->
      decoder(form(2)((operands, mods) => Instruction.SCmovkI32(operands(0), operands(1), mods))),
    SOPKInstructionDefs.SCmpkEqI32 ->
      decoder(form(2)((operands, mods) => Instruction.SCmpkEqI32(operands(0), operands(1), mods))),
    SOPKInstructionDefs.SCmpkEqU32 ->
      decoder(form(2)((operands, mods) => Instruction.SCmpkEqU32(operands(0), operands(1), mods))),
    SOPKInstructionDefs.SCmpkGeI32 ->
      decoder(form(2)((operands, mods) => Instruction.SCmpkGeI32(operands(0), operands(1), mods))),
    SOPKInstructionDefs.SCmpkGeU32 ->
      decoder(form(2)((operands, mods) => Instruction.SCmpkGeU32(operands(0), operands(1), mods))),
    SOPKInstructionDefs.SCmpkGtI32 ->
      decoder(form(2)((operands, mods) => Instruction.SCmpkGtI32(operands(0), operands(1), mods))),
    SOPKInstructionDefs.SCmpkGtU32 ->
      decoder(form(2)((operands, mods) => Instruction.SCmpkGtU32(operands(0), operands(1), mods))),
    SOPKInstructionDefs.SCmpkLeI32 ->
      decoder(form(2)((operands, mods) => Instruction.SCmpkLeI32(operands(0), operands(1), mods))),
    SOPKInstructionDefs.SCmpkLeU32 ->
      decoder(form(2)((operands, mods) => Instruction.SCmpkLeU32(operands(0), operands(1), mods))),
    SOPKInstructionDefs.SCmpkLgI32 ->
      decoder(form(2)((operands, mods) => Instruction.SCmpkLgI32(operands(0), operands(1), mods))),
    SOPKInstructionDefs.SCmpkLgU32 ->
      decoder(form(2)((operands, mods) => Instruction.SCmpkLgU32(operands(0), operands(1), mods))),
    SOPKInstructionDefs.SCmpkLtI32 ->
      decoder(form(2)((operands, mods) => Instruction.SCmpkLtI32(operands(0), operands(1), mods))),
    SOPKInstructionDefs.SCmpkLtU32 ->
      decoder(form(2)((operands, mods) => Instruction.SCmpkLtU32(operands(0), operands(1), mods))),
    SOPKInstructionDefs.SGetregB32 ->
      decoder(form(2)((operands, mods) => Instruction.SGetregB32(operands(0), operands(1), mods))),
    SOPKInstructionDefs.SMovkI32 ->
      decoder(form(2)((operands, mods) => Instruction.SMovkI32(operands(0), operands(1), mods))),
    SOPKInstructionDefs.SMulkI32 ->
      decoder(form(2)((operands, mods) => Instruction.SMulkI32(operands(0), operands(1), mods))),
    SOPKInstructionDefs.SSetregB32 ->
      decoder(form(2)((operands, mods) => Instruction.SSetregB32(operands(0), operands(1), mods))),
    SOPKInstructionDefs.SSetregImm32B32 ->
      decoder(form(2)((operands, mods) => Instruction.SSetregImm32B32(operands(0), operands(1), mods))),
    SOPKInstructionDefs.SSubvectorLoopBegin ->
      decoder(form(2)((operands, mods) => Instruction.SSubvectorLoopBegin(operands(0), operands(1), mods))),
    SOPKInstructionDefs.SSubvectorLoopEnd ->
      decoder(form(2)((operands, mods) => Instruction.SSubvectorLoopEnd(operands(0), operands(1), mods))),
    SOPKInstructionDefs.SVersion -> decoder(form(1)((operands, mods) => Instruction.SVersion(operands(0), mods))),
    SOPKInstructionDefs.SWaitcntExpcnt ->
      decoder(form(2)((operands, mods) => Instruction.SWaitcntExpcnt(operands(0), operands(1), mods))),
    SOPKInstructionDefs.SWaitcntLgkmcnt ->
      decoder(form(2)((operands, mods) => Instruction.SWaitcntLgkmcnt(operands(0), operands(1), mods))),
    SOPKInstructionDefs.SWaitcntVmcnt ->
      decoder(form(2)((operands, mods) => Instruction.SWaitcntVmcnt(operands(0), operands(1), mods))),
    SOPKInstructionDefs.SWaitcntVscnt ->
      decoder(form(2)((operands, mods) => Instruction.SWaitcntVscnt(operands(0), operands(1), mods)))
  )
}
