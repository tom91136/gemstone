// Generated from the pinned AMDGPU instruction forms. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.amdgpu.asm
import net.kurobako.gemstone.isa.amdgpu.*
import Instruction.*
private object VectorCompareInstructionForms {
  val decoders: Map[Opcode, Decoder] = Map(
    VectorCompareInstructionDefs.VCmpClassF16 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpClassF16(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpClassF32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpClassF32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpClassF64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpClassF64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpEqF16 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpEqF16(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpEqF32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpEqF32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpEqF64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpEqF64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpEqI16 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpEqI16(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpEqI32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpEqI32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpEqI64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpEqI64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpEqU16 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpEqU16(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpEqU32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpEqU32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpEqU64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpEqU64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpFF16 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpFF16(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpFF32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpFF32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpFF64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpFF64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpFI16 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpFI16(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpFI32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpFI32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpFI64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpFI64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpFU16 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpFU16(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpFU32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpFU32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpFU64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpFU64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpGeF16 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpGeF16(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpGeF32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpGeF32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpGeF64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpGeF64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpGeI16 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpGeI16(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpGeI32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpGeI32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpGeI64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpGeI64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpGeU16 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpGeU16(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpGeU32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpGeU32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpGeU64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpGeU64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpGtF16 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpGtF16(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpGtF32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpGtF32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpGtF64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpGtF64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpGtI16 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpGtI16(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpGtI32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpGtI32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpGtI64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpGtI64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpGtU16 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpGtU16(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpGtU32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpGtU32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpGtU64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpGtU64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpLeF16 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpLeF16(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpLeF32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpLeF32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpLeF64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpLeF64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpLeI16 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpLeI16(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpLeI32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpLeI32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpLeI64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpLeI64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpLeU16 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpLeU16(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpLeU32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpLeU32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpLeU64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpLeU64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpLgF16 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpLgF16(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpLgF32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpLgF32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpLgF64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpLgF64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpLtF16 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpLtF16(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpLtF32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpLtF32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpLtF64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpLtF64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpLtI16 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpLtI16(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpLtI32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpLtI32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpLtI64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpLtI64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpLtU16 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpLtU16(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpLtU32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpLtU32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpLtU64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpLtU64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpNeI16 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpNeI16(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpNeI32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpNeI32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpNeI64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpNeI64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpNeU16 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpNeU16(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpNeU32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpNeU32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpNeU64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpNeU64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpNeqF16 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpNeqF16(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpNeqF32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpNeqF32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpNeqF64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpNeqF64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpNgeF16 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpNgeF16(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpNgeF32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpNgeF32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpNgeF64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpNgeF64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpNgtF16 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpNgtF16(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpNgtF32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpNgtF32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpNgtF64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpNgtF64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpNleF16 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpNleF16(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpNleF32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpNleF32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpNleF64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpNleF64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpNlgF16 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpNlgF16(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpNlgF32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpNlgF32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpNlgF64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpNlgF64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpNltF16 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpNltF16(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpNltF32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpNltF32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpNltF64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpNltF64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpOF16 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpOF16(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpOF32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpOF32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpOF64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpOF64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpTF16 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpTF16(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpTF32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpTF32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpTF64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpTF64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpTI16 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpTI16(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpTI32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpTI32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpTI64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpTI64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpTU16 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpTU16(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpTU32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpTU32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpTU64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpTU64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpTruF16 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpTruF16(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpTruF32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpTruF32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpTruF64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpTruF64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpUF16 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpUF16(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpUF32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpUF32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpUF64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpUF64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsEqF32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsEqF32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsEqF64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsEqF64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsFF32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsFF32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsFF64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsFF64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsGeF32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsGeF32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsGeF64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsGeF64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsGtF32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsGtF32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsGtF64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsGtF64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsLeF32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsLeF32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsLeF64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsLeF64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsLgF32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsLgF32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsLgF64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsLgF64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsLtF32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsLtF32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsLtF64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsLtF64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsNeqF32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsNeqF32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsNeqF64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsNeqF64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsNgeF32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsNgeF32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsNgeF64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsNgeF64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsNgtF32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsNgtF32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsNgtF64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsNgtF64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsNleF32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsNleF32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsNleF64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsNleF64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsNlgF32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsNlgF32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsNlgF64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsNlgF64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsNltF32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsNltF32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsNltF64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsNltF64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsOF32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsOF32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsOF64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsOF64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsTruF32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsTruF32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsTruF64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsTruF64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsUF32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsUF32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsUF64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsUF64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsxEqF32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsxEqF32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsxEqF64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsxEqF64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsxFF32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsxFF32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsxFF64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsxFF64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsxGeF32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsxGeF32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsxGeF64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsxGeF64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsxGtF32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsxGtF32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsxGtF64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsxGtF64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsxLeF32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsxLeF32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsxLeF64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsxLeF64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsxLgF32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsxLgF32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsxLgF64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsxLgF64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsxLtF32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsxLtF32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsxLtF64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsxLtF64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsxNeqF32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsxNeqF32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsxNeqF64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsxNeqF64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsxNgeF32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsxNgeF32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsxNgeF64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsxNgeF64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsxNgtF32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsxNgtF32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsxNgtF64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsxNgtF64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsxNleF32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsxNleF32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsxNleF64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsxNleF64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsxNlgF32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsxNlgF32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsxNlgF64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsxNlgF64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsxNltF32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsxNltF32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsxNltF64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsxNltF64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsxOF32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsxOF32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsxOF64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsxOF64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsxTruF32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsxTruF32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsxTruF64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsxTruF64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsxUF32 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsxUF32(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpsxUF64 ->
      decoder(form(3)((operands, mods) => Instruction.VCmpsxUF64(operands(0), operands(1), operands(2), mods))),
    VectorCompareInstructionDefs.VCmpxClassF16 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxClassF16_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxClassF16_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxClassF32 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxClassF32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxClassF32_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxClassF64 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxClassF64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxClassF64_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxEqF16 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxEqF16_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxEqF16_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxEqF32 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxEqF32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxEqF32_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxEqF64 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxEqF64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxEqF64_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxEqI16 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxEqI16_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxEqI16_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxEqI32 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxEqI32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxEqI32_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxEqI64 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxEqI64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxEqI64_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxEqU16 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxEqU16_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxEqU16_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxEqU32 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxEqU32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxEqU32_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxEqU64 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxEqU64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxEqU64_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxFF16 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxFF16_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxFF16_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxFF32 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxFF32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxFF32_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxFF64 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxFF64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxFF64_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxFI16 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxFI16_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxFI16_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxFI32 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxFI32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxFI32_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxFI64 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxFI64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxFI64_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxFU16 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxFU16_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxFU16_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxFU32 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxFU32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxFU32_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxFU64 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxFU64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxFU64_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxGeF16 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxGeF16_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxGeF16_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxGeF32 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxGeF32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxGeF32_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxGeF64 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxGeF64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxGeF64_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxGeI16 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxGeI16_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxGeI16_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxGeI32 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxGeI32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxGeI32_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxGeI64 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxGeI64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxGeI64_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxGeU16 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxGeU16_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxGeU16_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxGeU32 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxGeU32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxGeU32_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxGeU64 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxGeU64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxGeU64_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxGtF16 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxGtF16_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxGtF16_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxGtF32 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxGtF32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxGtF32_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxGtF64 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxGtF64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxGtF64_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxGtI16 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxGtI16_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxGtI16_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxGtI32 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxGtI32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxGtI32_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxGtI64 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxGtI64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxGtI64_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxGtU16 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxGtU16_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxGtU16_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxGtU32 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxGtU32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxGtU32_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxGtU64 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxGtU64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxGtU64_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxLeF16 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxLeF16_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxLeF16_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxLeF32 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxLeF32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxLeF32_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxLeF64 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxLeF64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxLeF64_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxLeI16 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxLeI16_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxLeI16_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxLeI32 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxLeI32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxLeI32_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxLeI64 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxLeI64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxLeI64_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxLeU16 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxLeU16_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxLeU16_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxLeU32 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxLeU32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxLeU32_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxLeU64 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxLeU64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxLeU64_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxLgF16 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxLgF16_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxLgF16_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxLgF32 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxLgF32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxLgF32_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxLgF64 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxLgF64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxLgF64_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxLtF16 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxLtF16_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxLtF16_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxLtF32 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxLtF32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxLtF32_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxLtF64 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxLtF64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxLtF64_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxLtI16 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxLtI16_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxLtI16_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxLtI32 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxLtI32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxLtI32_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxLtI64 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxLtI64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxLtI64_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxLtU16 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxLtU16_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxLtU16_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxLtU32 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxLtU32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxLtU32_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxLtU64 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxLtU64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxLtU64_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxNeI16 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxNeI16_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxNeI16_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxNeI32 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxNeI32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxNeI32_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxNeI64 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxNeI64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxNeI64_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxNeU16 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxNeU16_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxNeU16_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxNeU32 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxNeU32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxNeU32_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxNeU64 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxNeU64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxNeU64_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxNeqF16 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxNeqF16_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxNeqF16_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxNeqF32 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxNeqF32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxNeqF32_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxNeqF64 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxNeqF64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxNeqF64_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxNgeF16 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxNgeF16_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxNgeF16_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxNgeF32 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxNgeF32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxNgeF32_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxNgeF64 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxNgeF64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxNgeF64_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxNgtF16 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxNgtF16_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxNgtF16_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxNgtF32 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxNgtF32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxNgtF32_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxNgtF64 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxNgtF64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxNgtF64_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxNleF16 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxNleF16_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxNleF16_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxNleF32 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxNleF32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxNleF32_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxNleF64 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxNleF64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxNleF64_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxNlgF16 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxNlgF16_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxNlgF16_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxNlgF32 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxNlgF32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxNlgF32_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxNlgF64 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxNlgF64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxNlgF64_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxNltF16 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxNltF16_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxNltF16_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxNltF32 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxNltF32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxNltF32_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxNltF64 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxNltF64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxNltF64_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxOF16 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxOF16_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxOF16_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxOF32 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxOF32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxOF32_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxOF64 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxOF64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxOF64_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxTF16 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxTF16_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxTF16_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxTF32 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxTF32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxTF32_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxTF64 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxTF64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxTF64_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxTI16 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxTI16_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxTI16_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxTI32 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxTI32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxTI32_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxTI64 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxTI64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxTI64_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxTU16 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxTU16_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxTU16_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxTU32 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxTU32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxTU32_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxTU64 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxTU64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxTU64_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxTruF16 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxTruF16_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxTruF16_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxTruF32 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxTruF32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxTruF32_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxTruF64 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxTruF64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxTruF64_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxUF16 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxUF16_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxUF16_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxUF32 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxUF32_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxUF32_3(operands(0), operands(1), operands(2), mods))
    ),
    VectorCompareInstructionDefs.VCmpxUF64 -> decoder(
      form(2)((operands, mods) => Instruction.VCmpxUF64_2(operands(0), operands(1), mods)),
      form(3)((operands, mods) => Instruction.VCmpxUF64_3(operands(0), operands(1), operands(2), mods))
    )
  )
}
