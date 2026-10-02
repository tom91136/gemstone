// Generated from the pinned AMDGPU instruction forms. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu.asm
import net.kurobako.gemstone.isa.amdgpu.*
import Instruction.*
private object SOPCInstructionForms {
  val decoders: Map[Opcode, Decoder] = Map(
    SOPCInstructionDefs.SBitcmp0B32 ->
      decoder(form(2)((operands, mods) => Instruction.SBitcmp0B32(operands(0), operands(1), mods))),
    SOPCInstructionDefs.SBitcmp0B64 ->
      decoder(form(2)((operands, mods) => Instruction.SBitcmp0B64(operands(0), operands(1), mods))),
    SOPCInstructionDefs.SBitcmp1B32 ->
      decoder(form(2)((operands, mods) => Instruction.SBitcmp1B32(operands(0), operands(1), mods))),
    SOPCInstructionDefs.SBitcmp1B64 ->
      decoder(form(2)((operands, mods) => Instruction.SBitcmp1B64(operands(0), operands(1), mods))),
    SOPCInstructionDefs.SCmpEqF16 ->
      decoder(form(2)((operands, mods) => Instruction.SCmpEqF16(operands(0), operands(1), mods))),
    SOPCInstructionDefs.SCmpEqF32 ->
      decoder(form(2)((operands, mods) => Instruction.SCmpEqF32(operands(0), operands(1), mods))),
    SOPCInstructionDefs.SCmpEqI32 ->
      decoder(form(2)((operands, mods) => Instruction.SCmpEqI32(operands(0), operands(1), mods))),
    SOPCInstructionDefs.SCmpEqU32 ->
      decoder(form(2)((operands, mods) => Instruction.SCmpEqU32(operands(0), operands(1), mods))),
    SOPCInstructionDefs.SCmpEqU64 ->
      decoder(form(2)((operands, mods) => Instruction.SCmpEqU64(operands(0), operands(1), mods))),
    SOPCInstructionDefs.SCmpGeF16 ->
      decoder(form(2)((operands, mods) => Instruction.SCmpGeF16(operands(0), operands(1), mods))),
    SOPCInstructionDefs.SCmpGeF32 ->
      decoder(form(2)((operands, mods) => Instruction.SCmpGeF32(operands(0), operands(1), mods))),
    SOPCInstructionDefs.SCmpGeI32 ->
      decoder(form(2)((operands, mods) => Instruction.SCmpGeI32(operands(0), operands(1), mods))),
    SOPCInstructionDefs.SCmpGeU32 ->
      decoder(form(2)((operands, mods) => Instruction.SCmpGeU32(operands(0), operands(1), mods))),
    SOPCInstructionDefs.SCmpGtF16 ->
      decoder(form(2)((operands, mods) => Instruction.SCmpGtF16(operands(0), operands(1), mods))),
    SOPCInstructionDefs.SCmpGtF32 ->
      decoder(form(2)((operands, mods) => Instruction.SCmpGtF32(operands(0), operands(1), mods))),
    SOPCInstructionDefs.SCmpGtI32 ->
      decoder(form(2)((operands, mods) => Instruction.SCmpGtI32(operands(0), operands(1), mods))),
    SOPCInstructionDefs.SCmpGtU32 ->
      decoder(form(2)((operands, mods) => Instruction.SCmpGtU32(operands(0), operands(1), mods))),
    SOPCInstructionDefs.SCmpLeF16 ->
      decoder(form(2)((operands, mods) => Instruction.SCmpLeF16(operands(0), operands(1), mods))),
    SOPCInstructionDefs.SCmpLeF32 ->
      decoder(form(2)((operands, mods) => Instruction.SCmpLeF32(operands(0), operands(1), mods))),
    SOPCInstructionDefs.SCmpLeI32 ->
      decoder(form(2)((operands, mods) => Instruction.SCmpLeI32(operands(0), operands(1), mods))),
    SOPCInstructionDefs.SCmpLeU32 ->
      decoder(form(2)((operands, mods) => Instruction.SCmpLeU32(operands(0), operands(1), mods))),
    SOPCInstructionDefs.SCmpLgF16 ->
      decoder(form(2)((operands, mods) => Instruction.SCmpLgF16(operands(0), operands(1), mods))),
    SOPCInstructionDefs.SCmpLgF32 ->
      decoder(form(2)((operands, mods) => Instruction.SCmpLgF32(operands(0), operands(1), mods))),
    SOPCInstructionDefs.SCmpLgI32 ->
      decoder(form(2)((operands, mods) => Instruction.SCmpLgI32(operands(0), operands(1), mods))),
    SOPCInstructionDefs.SCmpLgU32 ->
      decoder(form(2)((operands, mods) => Instruction.SCmpLgU32(operands(0), operands(1), mods))),
    SOPCInstructionDefs.SCmpLgU64 ->
      decoder(form(2)((operands, mods) => Instruction.SCmpLgU64(operands(0), operands(1), mods))),
    SOPCInstructionDefs.SCmpLtF16 ->
      decoder(form(2)((operands, mods) => Instruction.SCmpLtF16(operands(0), operands(1), mods))),
    SOPCInstructionDefs.SCmpLtF32 ->
      decoder(form(2)((operands, mods) => Instruction.SCmpLtF32(operands(0), operands(1), mods))),
    SOPCInstructionDefs.SCmpLtI32 ->
      decoder(form(2)((operands, mods) => Instruction.SCmpLtI32(operands(0), operands(1), mods))),
    SOPCInstructionDefs.SCmpLtU32 ->
      decoder(form(2)((operands, mods) => Instruction.SCmpLtU32(operands(0), operands(1), mods))),
    SOPCInstructionDefs.SCmpNeqF16 ->
      decoder(form(2)((operands, mods) => Instruction.SCmpNeqF16(operands(0), operands(1), mods))),
    SOPCInstructionDefs.SCmpNeqF32 ->
      decoder(form(2)((operands, mods) => Instruction.SCmpNeqF32(operands(0), operands(1), mods))),
    SOPCInstructionDefs.SCmpNgeF16 ->
      decoder(form(2)((operands, mods) => Instruction.SCmpNgeF16(operands(0), operands(1), mods))),
    SOPCInstructionDefs.SCmpNgeF32 ->
      decoder(form(2)((operands, mods) => Instruction.SCmpNgeF32(operands(0), operands(1), mods))),
    SOPCInstructionDefs.SCmpNgtF16 ->
      decoder(form(2)((operands, mods) => Instruction.SCmpNgtF16(operands(0), operands(1), mods))),
    SOPCInstructionDefs.SCmpNgtF32 ->
      decoder(form(2)((operands, mods) => Instruction.SCmpNgtF32(operands(0), operands(1), mods))),
    SOPCInstructionDefs.SCmpNleF16 ->
      decoder(form(2)((operands, mods) => Instruction.SCmpNleF16(operands(0), operands(1), mods))),
    SOPCInstructionDefs.SCmpNleF32 ->
      decoder(form(2)((operands, mods) => Instruction.SCmpNleF32(operands(0), operands(1), mods))),
    SOPCInstructionDefs.SCmpNlgF16 ->
      decoder(form(2)((operands, mods) => Instruction.SCmpNlgF16(operands(0), operands(1), mods))),
    SOPCInstructionDefs.SCmpNlgF32 ->
      decoder(form(2)((operands, mods) => Instruction.SCmpNlgF32(operands(0), operands(1), mods))),
    SOPCInstructionDefs.SCmpNltF16 ->
      decoder(form(2)((operands, mods) => Instruction.SCmpNltF16(operands(0), operands(1), mods))),
    SOPCInstructionDefs.SCmpNltF32 ->
      decoder(form(2)((operands, mods) => Instruction.SCmpNltF32(operands(0), operands(1), mods))),
    SOPCInstructionDefs.SCmpOF16 ->
      decoder(form(2)((operands, mods) => Instruction.SCmpOF16(operands(0), operands(1), mods))),
    SOPCInstructionDefs.SCmpOF32 ->
      decoder(form(2)((operands, mods) => Instruction.SCmpOF32(operands(0), operands(1), mods))),
    SOPCInstructionDefs.SCmpUF16 ->
      decoder(form(2)((operands, mods) => Instruction.SCmpUF16(operands(0), operands(1), mods))),
    SOPCInstructionDefs.SCmpUF32 ->
      decoder(form(2)((operands, mods) => Instruction.SCmpUF32(operands(0), operands(1), mods))),
    SOPCInstructionDefs.SSetGprIdxOn ->
      decoder(form(2)((operands, mods) => Instruction.SSetGprIdxOn(operands(0), operands(1), mods))),
    SOPCInstructionDefs.SSetvskip ->
      decoder(form(2)((operands, mods) => Instruction.SSetvskip(operands(0), operands(1), mods)))
  )
}
