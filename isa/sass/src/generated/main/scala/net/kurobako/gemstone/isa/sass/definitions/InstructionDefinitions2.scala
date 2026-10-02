// Generated SASS instruction definitions and checked operand constructors. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.sass
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import Instruction.*
@javax.annotation.processing.Generated(Array("sbt codegen"))
object InstructionDefinitions2 {
  object F2fpRegImm extends Definition(
        "F2fpRegImm",
        "F2FP",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec(
          "f2fp_4b_upconvert__RIR",
          "f2fp_8b_upconvert__RIR",
          "f2fp_E8_upconvert__RIR",
          "f2fp_tf32__RIR",
          "f2fp_tf32_default_src__RIR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.F2fpRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object F2fpRegImmReg extends Definition(
        "F2fpRegImmReg",
        "F2FP",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.Immediate), Slot("srcC", OperandKind.Register)),
        Vec(
          "f2fp_4b_upconvert_scale__RIR",
          "f2fp_8b_upconvert_scale__RIR",
          "f2fp_f16_to_4b_downconvert__RIR",
          "f2fp_f16_to_8b_downconvert__RIR",
          "f2fp_f16_to_mx8_downconvert_scale__RIR",
          "f2fp_merge_c__RIR",
          "f2fp_merge_c_default_dst_src__RIR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.F2fpRegImmReg(dst, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object F2fpRegReg extends Definition(
        "F2fpRegReg",
        "F2FP",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec(
          "f2fp_4b_upconvert__RRR",
          "f2fp_4b_upconvert__RUR",
          "f2fp_8b_upconvert__RRR",
          "f2fp_8b_upconvert__RUR",
          "f2fp_E8_upconvert__RRR",
          "f2fp_E8_upconvert__RUR",
          "f2fp_tf32__RRR",
          "f2fp_tf32__RUR",
          "f2fp_tf32_default_src__RRR",
          "f2fp_tf32_default_src__RUR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.F2fpRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object F2fpRegRegAddressImm extends Definition(
        "F2fpRegRegAddressImm",
        "F2FP",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec(
          "f2fp__RCxR",
          "f2fp_default_dst_src__RCxR",
          "f2fp_f16_to_8b_downconvert__RRCx",
          "f2fp_merge_c__RRCx",
          "f2fp_merge_c_default_dst_src__RRCx"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.F2fpRegRegAddressImm(dst, srcA, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object F2fpRegRegAddressImmReg extends Definition(
        "F2fpRegRegAddressImmReg",
        "F2FP",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("f2fp_f32_to_8b_downconvert__RCxR", "f2fp_rs__RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.F2fpRegRegAddressImmReg(dst, srcA, srcB, sb_offset, srcC, modifiers, guard))
        case _ => None
      }
  }
  object F2fpRegRegConst extends Definition(
        "F2fpRegRegConst",
        "F2FP",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank)
        ),
        Vec(
          "f2fp__RCR",
          "f2fp_default_dst_src__RCR",
          "f2fp_f16_to_8b_downconvert__RRC",
          "f2fp_merge_c__RRC",
          "f2fp_merge_c_default_dst_src__RRC"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb)
            ) => Some(Instruction.F2fpRegRegConst(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object F2fpRegRegConstReg extends Definition(
        "F2fpRegRegConstReg",
        "F2FP",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("f2fp_f32_to_8b_downconvert__RCR", "f2fp_rs__RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.F2fpRegRegConstReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object F2fpRegRegImm extends Definition(
        "F2fpRegRegImm",
        "F2FP",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec(
          "f2fp_4b_upconvert_scale__RRI",
          "f2fp_8b_upconvert_scale__RRI",
          "f2fp__RIR",
          "f2fp_default_dst_src__RIR",
          "f2fp_f16_to_4b_downconvert__RRI",
          "f2fp_f16_to_8b_downconvert__RRI",
          "f2fp_f16_to_mx8_downconvert_scale__RRI",
          "f2fp_merge_c__RRI",
          "f2fp_merge_c_default_dst_src__RRI"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.F2fpRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object F2fpRegRegImmReg extends Definition(
        "F2fpRegRegImmReg",
        "F2FP",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec(
          "f2fp_f32_to_4b_downconvert__RIR",
          "f2fp_f32_to_8b_downconvert__RIR",
          "f2fp_f32_to_mx8_downconvert_scale__RIR",
          "f2fp_rs_16b__RIR",
          "f2fp_rs_8b4b__RIR",
          "f2fp_rs__RIR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.F2fpRegRegImmReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object F2fpRegRegReg extends Definition(
        "F2fpRegRegReg",
        "F2FP",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec(
          "f2fp_4b_upconvert_scale__RRR",
          "f2fp_4b_upconvert_scale__RRU",
          "f2fp_4b_upconvert_scale__RUR",
          "f2fp_8b_upconvert_scale__RRR",
          "f2fp_8b_upconvert_scale__RRU",
          "f2fp_8b_upconvert_scale__RUR",
          "f2fp__RRR",
          "f2fp__RUR",
          "f2fp_default_dst_src__RRR",
          "f2fp_default_dst_src__RUR",
          "f2fp_f16_to_4b_downconvert__RRR",
          "f2fp_f16_to_4b_downconvert__RRU",
          "f2fp_f16_to_4b_downconvert__RUR",
          "f2fp_f16_to_8b_downconvert__RRR",
          "f2fp_f16_to_8b_downconvert__RRU",
          "f2fp_f16_to_8b_downconvert__RUR",
          "f2fp_f16_to_mx8_downconvert_scale__RRR",
          "f2fp_f16_to_mx8_downconvert_scale__RRU",
          "f2fp_f16_to_mx8_downconvert_scale__RUR",
          "f2fp_merge_c__RRR",
          "f2fp_merge_c__RRU",
          "f2fp_merge_c__RUR",
          "f2fp_merge_c_default_dst_src__RRR",
          "f2fp_merge_c_default_dst_src__RRU",
          "f2fp_merge_c_default_dst_src__RUR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.F2fpRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object F2fpRegRegRegAddressImm extends Definition(
        "F2fpRegRegRegAddressImm",
        "F2FP",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Address),
          Slot("sc_offset", OperandKind.Immediate)
        ),
        Vec("f2fp_f32_to_8b_downconvert__RRCx", "f2fp_rs__RRCx")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Address(srcC),
              InstructionOperand.Immediate(sc_offset)
            ) => Some(Instruction.F2fpRegRegRegAddressImm(dst, srcA, srcB, srcC, sc_offset, modifiers, guard))
        case _ => None
      }
  }
  object F2fpRegRegRegConst extends Definition(
        "F2fpRegRegRegConst",
        "F2FP",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.ConstantBank)
        ),
        Vec("f2fp_f32_to_8b_downconvert__RRC", "f2fp_rs__RRC")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(sc)
            ) => Some(Instruction.F2fpRegRegRegConst(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object F2fpRegRegRegImm extends Definition(
        "F2fpRegRegRegImm",
        "F2FP",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate)
        ),
        Vec(
          "f2fp_f32_to_4b_downconvert__RRI",
          "f2fp_f32_to_8b_downconvert__RRI",
          "f2fp_f32_to_mx8_downconvert_scale__RRI",
          "f2fp_rs_16b__RRI",
          "f2fp_rs_8b4b__RRI",
          "f2fp_rs__RRI"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.F2fpRegRegRegImm(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object F2fpRegRegRegReg extends Definition(
        "F2fpRegRegRegReg",
        "F2FP",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec(
          "f2fp_f32_to_4b_downconvert__RRR",
          "f2fp_f32_to_4b_downconvert__RRU",
          "f2fp_f32_to_4b_downconvert__RUR",
          "f2fp_f32_to_8b_downconvert__RRR",
          "f2fp_f32_to_8b_downconvert__RRU",
          "f2fp_f32_to_8b_downconvert__RUR",
          "f2fp_f32_to_mx8_downconvert_scale__RRR",
          "f2fp_f32_to_mx8_downconvert_scale__RRU",
          "f2fp_f32_to_mx8_downconvert_scale__RUR",
          "f2fp_rs_16b__RRR",
          "f2fp_rs_16b__RRU",
          "f2fp_rs_16b__RUR",
          "f2fp_rs_8b4b__RRR",
          "f2fp_rs_8b4b__RRU",
          "f2fp_rs_8b4b__RUR",
          "f2fp_rs__RRR",
          "f2fp_rs__RRU",
          "f2fp_rs__RUR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.F2fpRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object F2fpintPipeRegAddressImm extends Definition(
        "F2fpintPipeRegAddressImm",
        "F2FPint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("f2fp_8b_upconvert__RCxR", "f2fp_tf32__RCxR", "f2fp_tf32_default_src__RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.F2fpintPipeRegAddressImm(dst, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object F2fpintPipeRegAddressImmReg extends Definition(
        "F2fpintPipeRegAddressImmReg",
        "F2FPint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("f2fp_f16_to_8b_downconvert__RCxR", "f2fp_merge_c__RCxR", "f2fp_merge_c_default_dst_src__RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.F2fpintPipeRegAddressImmReg(dst, srcB, sb_offset, srcC, modifiers, guard))
        case _ => None
      }
  }
  object F2fpintPipeRegConst extends Definition(
        "F2fpintPipeRegConst",
        "F2FPint_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.ConstantBank)),
        Vec("f2fp_8b_upconvert__RCR", "f2fp_tf32__RCR", "f2fp_tf32_default_src__RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(sb)) =>
          Some(Instruction.F2fpintPipeRegConst(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object F2fpintPipeRegConstReg extends Definition(
        "F2fpintPipeRegConstReg",
        "F2FPint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("f2fp_f16_to_8b_downconvert__RCR", "f2fp_merge_c__RCR", "f2fp_merge_c_default_dst_src__RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.F2fpintPipeRegConstReg(dst, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object F2fpintPipeRegImm extends Definition(
        "F2fpintPipeRegImm",
        "F2FPint_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec(
          "f2fp_4b_upconvert__RIR",
          "f2fp_8b_upconvert__RIR",
          "f2fp_E8_upconvert__RIR",
          "f2fp_tf32__RIR",
          "f2fp_tf32_default_src__RIR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.F2fpintPipeRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object F2fpintPipeRegImmReg extends Definition(
        "F2fpintPipeRegImmReg",
        "F2FPint_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.Immediate), Slot("srcC", OperandKind.Register)),
        Vec(
          "f2fp_4b_upconvert_scale__RIR",
          "f2fp_8b_upconvert_scale__RIR",
          "f2fp_f16_to_4b_downconvert__RIR",
          "f2fp_f16_to_8b_downconvert__RIR",
          "f2fp_f16_to_mx8_downconvert_scale__RIR",
          "f2fp_merge_c__RIR",
          "f2fp_merge_c_default_dst_src__RIR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.F2fpintPipeRegImmReg(dst, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object F2fpintPipeRegReg extends Definition(
        "F2fpintPipeRegReg",
        "F2FPint_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec(
          "f2fp_4b_upconvert__RRR",
          "f2fp_4b_upconvert__RUR",
          "f2fp_8b_upconvert__RRR",
          "f2fp_8b_upconvert__RUR",
          "f2fp_E8_upconvert__RRR",
          "f2fp_E8_upconvert__RUR",
          "f2fp_tf32__RRR",
          "f2fp_tf32__RUR",
          "f2fp_tf32_default_src__RRR",
          "f2fp_tf32_default_src__RUR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.F2fpintPipeRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object F2fpintPipeRegRegAddressImm extends Definition(
        "F2fpintPipeRegRegAddressImm",
        "F2FPint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec(
          "f2fp__RCxR",
          "f2fp_default_dst_src__RCxR",
          "f2fp_f16_to_8b_downconvert__RRCx",
          "f2fp_merge_c__RRCx",
          "f2fp_merge_c_default_dst_src__RRCx"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.F2fpintPipeRegRegAddressImm(dst, srcA, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object F2fpintPipeRegRegAddressImmReg extends Definition(
        "F2fpintPipeRegRegAddressImmReg",
        "F2FPint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("f2fp_f32_to_8b_downconvert__RCxR", "f2fp_rs__RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.F2fpintPipeRegRegAddressImmReg(dst, srcA, srcB, sb_offset, srcC, modifiers, guard))
        case _ => None
      }
  }
  object F2fpintPipeRegRegConst extends Definition(
        "F2fpintPipeRegRegConst",
        "F2FPint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank)
        ),
        Vec(
          "f2fp__RCR",
          "f2fp_default_dst_src__RCR",
          "f2fp_f16_to_8b_downconvert__RRC",
          "f2fp_merge_c__RRC",
          "f2fp_merge_c_default_dst_src__RRC"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb)
            ) => Some(Instruction.F2fpintPipeRegRegConst(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object F2fpintPipeRegRegConstReg extends Definition(
        "F2fpintPipeRegRegConstReg",
        "F2FPint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("f2fp_f32_to_8b_downconvert__RCR", "f2fp_rs__RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.F2fpintPipeRegRegConstReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object F2fpintPipeRegRegImm extends Definition(
        "F2fpintPipeRegRegImm",
        "F2FPint_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec(
          "f2fp_4b_upconvert_scale__RRI",
          "f2fp_8b_upconvert_scale__RRI",
          "f2fp__RIR",
          "f2fp_default_dst_src__RIR",
          "f2fp_f16_to_4b_downconvert__RRI",
          "f2fp_f16_to_8b_downconvert__RRI",
          "f2fp_f16_to_mx8_downconvert_scale__RRI",
          "f2fp_merge_c__RRI",
          "f2fp_merge_c_default_dst_src__RRI"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.F2fpintPipeRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object F2fpintPipeRegRegImmReg extends Definition(
        "F2fpintPipeRegRegImmReg",
        "F2FPint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec(
          "f2fp_f32_to_4b_downconvert__RIR",
          "f2fp_f32_to_8b_downconvert__RIR",
          "f2fp_f32_to_mx8_downconvert_scale__RIR",
          "f2fp_rs_16b__RIR",
          "f2fp_rs_8b4b__RIR",
          "f2fp_rs__RIR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.F2fpintPipeRegRegImmReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object F2fpintPipeRegRegReg extends Definition(
        "F2fpintPipeRegRegReg",
        "F2FPint_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec(
          "f2fp_4b_upconvert_scale__RRR",
          "f2fp_4b_upconvert_scale__RRU",
          "f2fp_4b_upconvert_scale__RUR",
          "f2fp_8b_upconvert_scale__RRR",
          "f2fp_8b_upconvert_scale__RRU",
          "f2fp_8b_upconvert_scale__RUR",
          "f2fp__RRR",
          "f2fp__RUR",
          "f2fp_default_dst_src__RRR",
          "f2fp_default_dst_src__RUR",
          "f2fp_f16_to_4b_downconvert__RRR",
          "f2fp_f16_to_4b_downconvert__RRU",
          "f2fp_f16_to_4b_downconvert__RUR",
          "f2fp_f16_to_8b_downconvert__RRR",
          "f2fp_f16_to_8b_downconvert__RRU",
          "f2fp_f16_to_8b_downconvert__RUR",
          "f2fp_f16_to_mx8_downconvert_scale__RRR",
          "f2fp_f16_to_mx8_downconvert_scale__RRU",
          "f2fp_f16_to_mx8_downconvert_scale__RUR",
          "f2fp_merge_c__RRR",
          "f2fp_merge_c__RRU",
          "f2fp_merge_c__RUR",
          "f2fp_merge_c_default_dst_src__RRR",
          "f2fp_merge_c_default_dst_src__RRU",
          "f2fp_merge_c_default_dst_src__RUR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.F2fpintPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object F2fpintPipeRegRegRegAddressImm extends Definition(
        "F2fpintPipeRegRegRegAddressImm",
        "F2FPint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Address),
          Slot("sc_offset", OperandKind.Immediate)
        ),
        Vec("f2fp_f32_to_8b_downconvert__RRCx", "f2fp_rs__RRCx")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Address(srcC),
              InstructionOperand.Immediate(sc_offset)
            ) => Some(Instruction.F2fpintPipeRegRegRegAddressImm(dst, srcA, srcB, srcC, sc_offset, modifiers, guard))
        case _ => None
      }
  }
  object F2fpintPipeRegRegRegConst extends Definition(
        "F2fpintPipeRegRegRegConst",
        "F2FPint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.ConstantBank)
        ),
        Vec("f2fp_f32_to_8b_downconvert__RRC", "f2fp_rs__RRC")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(sc)
            ) => Some(Instruction.F2fpintPipeRegRegRegConst(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object F2fpintPipeRegRegRegImm extends Definition(
        "F2fpintPipeRegRegRegImm",
        "F2FPint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate)
        ),
        Vec(
          "f2fp_f32_to_4b_downconvert__RRI",
          "f2fp_f32_to_8b_downconvert__RRI",
          "f2fp_f32_to_mx8_downconvert_scale__RRI",
          "f2fp_rs_16b__RRI",
          "f2fp_rs_8b4b__RRI",
          "f2fp_rs__RRI"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.F2fpintPipeRegRegRegImm(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object F2fpintPipeRegRegRegReg extends Definition(
        "F2fpintPipeRegRegRegReg",
        "F2FPint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec(
          "f2fp_f32_to_4b_downconvert__RRR",
          "f2fp_f32_to_4b_downconvert__RRU",
          "f2fp_f32_to_4b_downconvert__RUR",
          "f2fp_f32_to_8b_downconvert__RRR",
          "f2fp_f32_to_8b_downconvert__RRU",
          "f2fp_f32_to_8b_downconvert__RUR",
          "f2fp_f32_to_mx8_downconvert_scale__RRR",
          "f2fp_f32_to_mx8_downconvert_scale__RRU",
          "f2fp_f32_to_mx8_downconvert_scale__RUR",
          "f2fp_rs_16b__RRR",
          "f2fp_rs_16b__RRU",
          "f2fp_rs_16b__RUR",
          "f2fp_rs_8b4b__RRR",
          "f2fp_rs_8b4b__RRU",
          "f2fp_rs_8b4b__RUR",
          "f2fp_rs__RRR",
          "f2fp_rs__RRU",
          "f2fp_rs__RUR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.F2fpintPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object F2ffmaxPipeRegConst extends Definition(
        "F2ffmaxPipeRegConst",
        "F2FfmaX_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcConst", OperandKind.ConstantBank)),
        Vec("Const F2F 1", "Const F2F 1 16", "Const F2F 1 64", "Const F2F 2", "Const F2F 2 16", "Const F2F 2 64")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(srcConst)) =>
          Some(Instruction.F2ffmaxPipeRegConst(dst, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object F2ffmaxPipeRegImm extends Definition(
        "F2ffmaxPipeRegImm",
        "F2FfmaX_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("Imm F2F 1", "Imm F2F 1 16", "Imm F2F 1 64", "Imm F2F 2", "Imm F2F 2 16", "Imm F2F 2 64")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(uImm)) =>
          Some(Instruction.F2ffmaxPipeRegImm(dst, uImm, modifiers, guard))
        case _ => None
      }
  }
  object F2ffmaxPipeRegReg extends Definition(
        "F2ffmaxPipeRegReg",
        "F2FfmaX_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("F2F 1", "F2F 1 16", "F2F 1 64", "F2F 2", "F2F 2 16", "F2F 2 64")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.F2ffmaxPipeRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object F2fmioPipeRegAddressImm extends Definition(
        "F2fmioPipeRegAddressImm",
        "F2Fmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec(
          "f2f_f32_downconvert__RCXR_RCXR",
          "f2f_f32_upconvert__RCXR_RCXR",
          "f2f_f32_upconvert_swap__RCXR_RCXR",
          "f2f_f64_downconvert__RCXR_RCXR",
          "f2f_f64_upconvert__R_CX16_R_RCXR",
          "f2f_f64_upconvert__R_CX32_R_RCXR",
          "f2f_f64_upconvert_swap__R_CX16_R_RCXR",
          "f2f_f64_upconvert_swap__R_CX32_R_RCXR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.F2fmioPipeRegAddressImm(dst, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object F2fmioPipeRegConst extends Definition(
        "F2fmioPipeRegConst",
        "F2Fmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.ConstantBank)),
        Vec(
          "Const_F2F_1",
          "Const_F2F_1_16",
          "Const_F2F_1_64",
          "Const_F2F_2",
          "Const_F2F_2_16",
          "Const_F2F_2_64",
          "Const_F2F_2_64_16",
          "Const_F2F_2_64_32",
          "f2f_f32_downconvert__RCR_RCR",
          "f2f_f32_upconvert__RCR_RCR",
          "f2f_f32_upconvert_swap__RCR_RCR",
          "f2f_f64_downconvert__RCR_RCR",
          "f2f_f64_upconvert__R_C16_R_RCR",
          "f2f_f64_upconvert__R_C32_R_RCR",
          "f2f_f64_upconvert_swap__R_C16_R_RCR",
          "f2f_f64_upconvert_swap__R_C32_R_RCR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(sb)) =>
          Some(Instruction.F2fmioPipeRegConst(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object F2fmioPipeRegImm extends Definition(
        "F2fmioPipeRegImm",
        "F2Fmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec(
          "Imm_F2F_1",
          "Imm_F2F_1_16",
          "Imm_F2F_1_64",
          "Imm_F2F_2",
          "Imm_F2F_2_16",
          "Imm_F2F_2_64",
          "Imm_F2F_2_64_16",
          "Imm_F2F_2_64_32",
          "f2f_f32_downconvert__RIR_RIR",
          "f2f_f32_upconvert__RIR_RIR",
          "f2f_f32_upconvert_swap__RIR_RIR",
          "f2f_f64_downconvert__RIR_RIR",
          "f2f_f64_upconvert__R_32I_R_RIR",
          "f2f_f64_upconvert__R_I16_R_RIR",
          "f2f_f64_upconvert_swap__R_32I_R_RIR",
          "f2f_f64_upconvert_swap__R_I16_R_RIR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.F2fmioPipeRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object F2fmioPipeRegReg extends Definition(
        "F2fmioPipeRegReg",
        "F2Fmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec(
          "F2F_1",
          "F2F_1_16",
          "F2F_1_64",
          "F2F_2",
          "F2F_2_16",
          "F2F_2_64",
          "F2F_2_64_16",
          "F2F_2_64_32",
          "f2f_f32_downconvert__RRR_RRR",
          "f2f_f32_downconvert__RUR_RUR",
          "f2f_f32_upconvert__RRR_RRR",
          "f2f_f32_upconvert__RUR_RUR",
          "f2f_f32_upconvert_swap__RRR_RRR",
          "f2f_f32_upconvert_swap__RUR_RUR",
          "f2f_f64_downconvert__RRR_RRR",
          "f2f_f64_downconvert__RUR_RUR",
          "f2f_f64_upconvert__R_R16_R_RRR",
          "f2f_f64_upconvert__R_R32_R_RRR",
          "f2f_f64_upconvert__R_UR16_R_RUR",
          "f2f_f64_upconvert__R_UR32_R_RUR",
          "f2f_f64_upconvert_swap__R_R16_R_RRR",
          "f2f_f64_upconvert_swap__R_R32_R_RRR",
          "f2f_f64_upconvert_swap__R_UR16_R_RUR",
          "f2f_f64_upconvert_swap__R_UR32_R_RUR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.F2fmioPipeRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object F2iRegAddressImm extends Definition(
        "F2iRegAddressImm",
        "F2I",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec(
          "f2i_Rd64__CXb_16b",
          "f2i_Rd64__CXb_32b",
          "f2i_Rd64__CXb_64b",
          "f2i_Rd64__CXb_bf16",
          "f2i_Rd64_swap__CXb_16b",
          "f2i_Rd64_swap__CXb_32b",
          "f2i_Rd64_swap__CXb_64b",
          "f2i_Rd64_swap__CXb_bf16",
          "f2i__CXb_16b",
          "f2i__CXb_32b",
          "f2i__CXb_64b",
          "f2i__CXb_bf16",
          "f2i_swap__CXb_16b",
          "f2i_swap__CXb_32b",
          "f2i_swap__CXb_64b",
          "f2i_swap__CXb_bf16"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.F2iRegAddressImm(dst, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object F2iRegConst extends Definition(
        "F2iRegConst",
        "F2I",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.ConstantBank)),
        Vec(
          "Const F2I",
          "Const F2I 16",
          "Const F2I 64",
          "Const_F2I",
          "Const_F2I_16",
          "Const_F2I_64",
          "Const_F2I_I64",
          "f2i_Rd64__Cb_16b",
          "f2i_Rd64__Cb_32b",
          "f2i_Rd64__Cb_64b",
          "f2i_Rd64__Cb_bf16",
          "f2i_Rd64_swap__Cb_16b",
          "f2i_Rd64_swap__Cb_32b",
          "f2i_Rd64_swap__Cb_64b",
          "f2i_Rd64_swap__Cb_bf16",
          "f2i__Cb_16b",
          "f2i__Cb_32b",
          "f2i__Cb_64b",
          "f2i__Cb_bf16",
          "f2i_swap__Cb_16b",
          "f2i_swap__Cb_32b",
          "f2i_swap__Cb_64b",
          "f2i_swap__Cb_bf16"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(sb)) =>
          Some(Instruction.F2iRegConst(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object F2iRegImm extends Definition(
        "F2iRegImm",
        "F2I",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec(
          "Imm F2I",
          "Imm F2I 16",
          "Imm F2I 64",
          "Imm_F2I",
          "Imm_F2I_16",
          "Imm_F2I_64",
          "Imm_F2I_I64",
          "f2i_Rd64__IU_32b",
          "f2i_Rd64__Ib_16b",
          "f2i_Rd64__Ib_64b",
          "f2i_Rd64__Ib_bf16",
          "f2i_Rd64_swap__IU_32b",
          "f2i_Rd64_swap__Ib_16b",
          "f2i_Rd64_swap__Ib_64b",
          "f2i_Rd64_swap__Ib_bf16",
          "f2i__IU_32b",
          "f2i__Ib_16b",
          "f2i__Ib_64b",
          "f2i__Ib_bf16",
          "f2i_swap__IU_32b",
          "f2i_swap__Ib_16b",
          "f2i_swap__Ib_64b",
          "f2i_swap__Ib_bf16"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.F2iRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object F2iRegReg extends Definition(
        "F2iRegReg",
        "F2I",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec(
          "F2I",
          "F2I 16",
          "F2I 64",
          "F2I_16",
          "F2I_64",
          "F2I_I64",
          "f2i_Rd64__Rb_16b",
          "f2i_Rd64__Rb_32b",
          "f2i_Rd64__Rb_64b",
          "f2i_Rd64__Rb_bf16",
          "f2i_Rd64__URb_16b",
          "f2i_Rd64__URb_32b",
          "f2i_Rd64__URb_64b",
          "f2i_Rd64__URb_bf16",
          "f2i_Rd64_swap__Rb_16b",
          "f2i_Rd64_swap__Rb_32b",
          "f2i_Rd64_swap__Rb_64b",
          "f2i_Rd64_swap__Rb_bf16",
          "f2i_Rd64_swap__URb_16b",
          "f2i_Rd64_swap__URb_32b",
          "f2i_Rd64_swap__URb_64b",
          "f2i_Rd64_swap__URb_bf16",
          "f2i__Rb_16b",
          "f2i__Rb_32b",
          "f2i__Rb_64b",
          "f2i__Rb_bf16",
          "f2i__URb_16b",
          "f2i__URb_32b",
          "f2i__URb_64b",
          "f2i__URb_bf16",
          "f2i_swap__Rb_16b",
          "f2i_swap__Rb_32b",
          "f2i_swap__Rb_64b",
          "f2i_swap__Rb_bf16",
          "f2i_swap__URb_16b",
          "f2i_swap__URb_32b",
          "f2i_swap__URb_64b",
          "f2i_swap__URb_bf16"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.F2iRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object F2ipRegRegAddressImmReg extends Definition(
        "F2ipRegRegAddressImmReg",
        "F2IP",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("f2ip__RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.F2ipRegRegAddressImmReg(dst, srcA, srcB, sb_offset, srcC, modifiers, guard))
        case _ => None
      }
  }
  object F2ipRegRegConstReg extends Definition(
        "F2ipRegRegConstReg",
        "F2IP",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("f2ip__RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.F2ipRegRegConstReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object F2ipRegRegImmReg extends Definition(
        "F2ipRegRegImmReg",
        "F2IP",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("f2ip__RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.F2ipRegRegImmReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object F2ipRegRegRegAddressImm extends Definition(
        "F2ipRegRegRegAddressImm",
        "F2IP",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Address),
          Slot("sc_offset", OperandKind.Immediate)
        ),
        Vec("f2ip__RRCx")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Address(srcC),
              InstructionOperand.Immediate(sc_offset)
            ) => Some(Instruction.F2ipRegRegRegAddressImm(dst, srcA, srcB, srcC, sc_offset, modifiers, guard))
        case _ => None
      }
  }
  object F2ipRegRegRegConst extends Definition(
        "F2ipRegRegRegConst",
        "F2IP",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.ConstantBank)
        ),
        Vec("f2ip__RRC")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(sc)
            ) => Some(Instruction.F2ipRegRegRegConst(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object F2ipRegRegRegImm extends Definition(
        "F2ipRegRegRegImm",
        "F2IP",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate)
        ),
        Vec("f2ip__RRI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.F2ipRegRegRegImm(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object F2ipRegRegRegReg extends Definition(
        "F2ipRegRegRegReg",
        "F2IP",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("f2ip__RRR", "f2ip__RRU", "f2ip__RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.F2ipRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object F2ipintPipeRegRegAddressImmReg extends Definition(
        "F2ipintPipeRegRegAddressImmReg",
        "F2IPint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("f2ip__RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.F2ipintPipeRegRegAddressImmReg(dst, srcA, srcB, sb_offset, srcC, modifiers, guard))
        case _ => None
      }
  }
  object F2ipintPipeRegRegConstReg extends Definition(
        "F2ipintPipeRegRegConstReg",
        "F2IPint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("f2ip__RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.F2ipintPipeRegRegConstReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object F2ipintPipeRegRegImmReg extends Definition(
        "F2ipintPipeRegRegImmReg",
        "F2IPint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("f2ip__RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.F2ipintPipeRegRegImmReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object F2ipintPipeRegRegRegAddressImm extends Definition(
        "F2ipintPipeRegRegRegAddressImm",
        "F2IPint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Address),
          Slot("sc_offset", OperandKind.Immediate)
        ),
        Vec("f2ip__RRCx")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Address(srcC),
              InstructionOperand.Immediate(sc_offset)
            ) => Some(Instruction.F2ipintPipeRegRegRegAddressImm(dst, srcA, srcB, srcC, sc_offset, modifiers, guard))
        case _ => None
      }
  }
  object F2ipintPipeRegRegRegConst extends Definition(
        "F2ipintPipeRegRegRegConst",
        "F2IPint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.ConstantBank)
        ),
        Vec("f2ip__RRC")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(sc)
            ) => Some(Instruction.F2ipintPipeRegRegRegConst(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object F2ipintPipeRegRegRegImm extends Definition(
        "F2ipintPipeRegRegRegImm",
        "F2IPint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate)
        ),
        Vec("f2ip__RRI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.F2ipintPipeRegRegRegImm(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object F2ipintPipeRegRegRegReg extends Definition(
        "F2ipintPipeRegRegRegReg",
        "F2IPint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("f2ip__RRR", "f2ip__RRU", "f2ip__RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.F2ipintPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object F2ifmaxPipeRegConst extends Definition(
        "F2ifmaxPipeRegConst",
        "F2IfmaX_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcConst", OperandKind.ConstantBank)),
        Vec("Const F2I", "Const F2I 16", "Const F2I 64")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(srcConst)) =>
          Some(Instruction.F2ifmaxPipeRegConst(dst, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object F2ifmaxPipeRegImm extends Definition(
        "F2ifmaxPipeRegImm",
        "F2IfmaX_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("Imm F2I", "Imm F2I 16", "Imm F2I 64")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(uImm)) =>
          Some(Instruction.F2ifmaxPipeRegImm(dst, uImm, modifiers, guard))
        case _ => None
      }
  }
  object F2ifmaxPipeRegReg extends Definition(
        "F2ifmaxPipeRegReg",
        "F2IfmaX_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("F2I", "F2I 16", "F2I 64")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.F2ifmaxPipeRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object F2imioPipeRegAddressImm extends Definition(
        "F2imioPipeRegAddressImm",
        "F2Imio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec(
          "f2i_Rd64__CXb_16b",
          "f2i_Rd64__CXb_32b",
          "f2i_Rd64__CXb_64b",
          "f2i_Rd64__CXb_bf16",
          "f2i_Rd64_swap__CXb_16b",
          "f2i_Rd64_swap__CXb_32b",
          "f2i_Rd64_swap__CXb_64b",
          "f2i_Rd64_swap__CXb_bf16",
          "f2i__CXb_16b",
          "f2i__CXb_32b",
          "f2i__CXb_64b",
          "f2i__CXb_bf16",
          "f2i_swap__CXb_16b",
          "f2i_swap__CXb_32b",
          "f2i_swap__CXb_64b",
          "f2i_swap__CXb_bf16"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.F2imioPipeRegAddressImm(dst, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object F2imioPipeRegConst extends Definition(
        "F2imioPipeRegConst",
        "F2Imio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.ConstantBank)),
        Vec(
          "Const_F2I",
          "Const_F2I_16",
          "Const_F2I_64",
          "Const_F2I_I64",
          "f2i_Rd64__Cb_16b",
          "f2i_Rd64__Cb_32b",
          "f2i_Rd64__Cb_64b",
          "f2i_Rd64__Cb_bf16",
          "f2i_Rd64_swap__Cb_16b",
          "f2i_Rd64_swap__Cb_32b",
          "f2i_Rd64_swap__Cb_64b",
          "f2i_Rd64_swap__Cb_bf16",
          "f2i__Cb_16b",
          "f2i__Cb_32b",
          "f2i__Cb_64b",
          "f2i__Cb_bf16",
          "f2i_swap__Cb_16b",
          "f2i_swap__Cb_32b",
          "f2i_swap__Cb_64b",
          "f2i_swap__Cb_bf16"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(sb)) =>
          Some(Instruction.F2imioPipeRegConst(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object F2imioPipeRegImm extends Definition(
        "F2imioPipeRegImm",
        "F2Imio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec(
          "Imm_F2I",
          "Imm_F2I_16",
          "Imm_F2I_64",
          "Imm_F2I_I64",
          "f2i_Rd64__IU_32b",
          "f2i_Rd64__Ib_16b",
          "f2i_Rd64__Ib_64b",
          "f2i_Rd64__Ib_bf16",
          "f2i_Rd64_swap__IU_32b",
          "f2i_Rd64_swap__Ib_16b",
          "f2i_Rd64_swap__Ib_64b",
          "f2i_Rd64_swap__Ib_bf16",
          "f2i__IU_32b",
          "f2i__Ib_16b",
          "f2i__Ib_64b",
          "f2i__Ib_bf16",
          "f2i_swap__IU_32b",
          "f2i_swap__Ib_16b",
          "f2i_swap__Ib_64b",
          "f2i_swap__Ib_bf16"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.F2imioPipeRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object F2imioPipeRegReg extends Definition(
        "F2imioPipeRegReg",
        "F2Imio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec(
          "F2I",
          "F2I_16",
          "F2I_64",
          "F2I_I64",
          "f2i_Rd64__Rb_16b",
          "f2i_Rd64__Rb_32b",
          "f2i_Rd64__Rb_64b",
          "f2i_Rd64__Rb_bf16",
          "f2i_Rd64__URb_16b",
          "f2i_Rd64__URb_32b",
          "f2i_Rd64__URb_64b",
          "f2i_Rd64__URb_bf16",
          "f2i_Rd64_swap__Rb_16b",
          "f2i_Rd64_swap__Rb_32b",
          "f2i_Rd64_swap__Rb_64b",
          "f2i_Rd64_swap__Rb_bf16",
          "f2i_Rd64_swap__URb_16b",
          "f2i_Rd64_swap__URb_32b",
          "f2i_Rd64_swap__URb_64b",
          "f2i_Rd64_swap__URb_bf16",
          "f2i__Rb_16b",
          "f2i__Rb_32b",
          "f2i__Rb_64b",
          "f2i__Rb_bf16",
          "f2i__URb_16b",
          "f2i__URb_32b",
          "f2i__URb_64b",
          "f2i__URb_bf16",
          "f2i_swap__Rb_16b",
          "f2i_swap__Rb_32b",
          "f2i_swap__Rb_64b",
          "f2i_swap__Rb_bf16",
          "f2i_swap__URb_16b",
          "f2i_swap__URb_32b",
          "f2i_swap__URb_64b",
          "f2i_swap__URb_bf16"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.F2imioPipeRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object FaddRegRegAddressImm extends Definition(
        "FaddRegRegAddressImm",
        "FADD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcC", OperandKind.Address),
          Slot("sc_offset", OperandKind.Immediate)
        ),
        Vec("fadd__RRCx_RCx")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcC),
              InstructionOperand.Immediate(sc_offset)
            ) => Some(Instruction.FaddRegRegAddressImm(dst, srcA, srcC, sc_offset, modifiers, guard))
        case _ => None
      }
  }
  object FaddRegRegConst extends Definition(
        "FaddRegRegConst",
        "FADD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sc", OperandKind.ConstantBank)
        ),
        Vec("Const FADD", "Const_FADD", "fadd__RRC_RC")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sc)
            ) => Some(Instruction.FaddRegRegConst(dst, srcA, sc, modifiers, guard))
        case _ => None
      }
  }
  object FaddRegRegImm extends Definition(
        "FaddRegRegImm",
        "FADD",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sc", OperandKind.Immediate)),
        Vec("Imm FADD", "Imm_FADD", "fadd__RRI_RI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.FaddRegRegImm(dst, srcA, sc, modifiers, guard))
        case _ => None
      }
  }
  object FaddRegRegReg extends Definition(
        "FaddRegRegReg",
        "FADD",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("FADD", "fadd__RRR_RR", "fadd__RRU_RU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.FaddRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object Fadd2RegRegImm extends Definition(
        "Fadd2RegRegImm",
        "FADD2",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sc", OperandKind.Immediate)),
        Vec("fadd2_imm__RRI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.Fadd2RegRegImm(dst, srcA, sc, modifiers, guard))
        case _ => None
      }
  }
  object Fadd2RegRegReg extends Definition(
        "Fadd2RegRegReg",
        "FADD2",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcC", OperandKind.Register)),
        Vec("fadd2_rc__RRR", "fadd2_urc__RRU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Fadd2RegRegReg(dst, srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Fadd2fmalighterPipeRegRegImm extends Definition(
        "Fadd2fmalighterPipeRegRegImm",
        "FADD2fmalighter_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sc", OperandKind.Immediate)),
        Vec("fadd2_imm__RRI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.Fadd2fmalighterPipeRegRegImm(dst, srcA, sc, modifiers, guard))
        case _ => None
      }
  }
  object Fadd2fmalighterPipeRegRegReg extends Definition(
        "Fadd2fmalighterPipeRegRegReg",
        "FADD2fmalighter_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcC", OperandKind.Register)),
        Vec("fadd2_rc__RRR", "fadd2_urc__RRU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Fadd2fmalighterPipeRegRegReg(dst, srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Fadd32iRegRegImm extends Definition(
        "Fadd32iRegRegImm",
        "FADD32I",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sc", OperandKind.Immediate)),
        Vec("FADD32I", "fadd32i_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.Fadd32iRegRegImm(dst, srcA, sc, modifiers, guard))
        case _ => None
      }
  }
  object Fadd32ifauPipeRegRegImm extends Definition(
        "Fadd32ifauPipeRegRegImm",
        "FADD32Ifau_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("fImm", OperandKind.Immediate)),
        Vec("FADD32I")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(fImm)
            ) => Some(Instruction.Fadd32ifauPipeRegRegImm(dst, srcA, fImm, modifiers, guard))
        case _ => None
      }
  }
  object Fadd32ifmaxPipeRegRegImm extends Definition(
        "Fadd32ifmaxPipeRegRegImm",
        "FADD32IfmaX_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("fImm", OperandKind.Immediate)),
        Vec("FADD32I")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(fImm)
            ) => Some(Instruction.Fadd32ifmaxPipeRegRegImm(dst, srcA, fImm, modifiers, guard))
        case _ => None
      }
  }
  object Fadd32ifmaxlitewPipeRegRegImm extends Definition(
        "Fadd32ifmaxlitewPipeRegRegImm",
        "FADD32IfmaXliteW_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("fImm", OperandKind.Immediate)),
        Vec("FADD32I")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(fImm)
            ) => Some(Instruction.Fadd32ifmaxlitewPipeRegRegImm(dst, srcA, fImm, modifiers, guard))
        case _ => None
      }
  }
  object Fadd32ifmaiPipeRegRegImm extends Definition(
        "Fadd32ifmaiPipeRegRegImm",
        "FADD32Ifmai_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("fImm", OperandKind.Immediate)),
        Vec("FADD32I")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(fImm)
            ) => Some(Instruction.Fadd32ifmaiPipeRegRegImm(dst, srcA, fImm, modifiers, guard))
        case _ => None
      }
  }
  object Fadd32ifmalighterPipeRegRegImm extends Definition(
        "Fadd32ifmalighterPipeRegRegImm",
        "FADD32Ifmalighter_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sc", OperandKind.Immediate)),
        Vec("fadd32i_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.Fadd32ifmalighterPipeRegRegImm(dst, srcA, sc, modifiers, guard))
        case _ => None
      }
  }
  object Fadd32ifxuPipeRegRegImm extends Definition(
        "Fadd32ifxuPipeRegRegImm",
        "FADD32Ifxu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("fImm", OperandKind.Immediate)),
        Vec("FADD32I")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(fImm)
            ) => Some(Instruction.Fadd32ifxuPipeRegRegImm(dst, srcA, fImm, modifiers, guard))
        case _ => None
      }
  }
  object FaddfauPipeRegRegConst extends Definition(
        "FaddfauPipeRegRegConst",
        "FADDfau_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const FADD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.FaddfauPipeRegRegConst(dst, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object FaddfauPipeRegRegImm extends Definition(
        "FaddfauPipeRegRegImm",
        "FADDfau_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("Imm FADD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.FaddfauPipeRegRegImm(dst, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object FaddfauPipeRegRegReg extends Definition(
        "FaddfauPipeRegRegReg",
        "FADDfau_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("FADD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.FaddfauPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object FaddfmaxPipeRegRegConst extends Definition(
        "FaddfmaxPipeRegRegConst",
        "FADDfmaX_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const FADD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.FaddfmaxPipeRegRegConst(dst, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object FaddfmaxPipeRegRegImm extends Definition(
        "FaddfmaxPipeRegRegImm",
        "FADDfmaX_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("Imm FADD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.FaddfmaxPipeRegRegImm(dst, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object FaddfmaxPipeRegRegReg extends Definition(
        "FaddfmaxPipeRegRegReg",
        "FADDfmaX_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("FADD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.FaddfmaxPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object FaddfmaxlitewPipeRegRegConst extends Definition(
        "FaddfmaxlitewPipeRegRegConst",
        "FADDfmaXliteW_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const FADD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.FaddfmaxlitewPipeRegRegConst(dst, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object FaddfmaxlitewPipeRegRegImm extends Definition(
        "FaddfmaxlitewPipeRegRegImm",
        "FADDfmaXliteW_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("Imm FADD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.FaddfmaxlitewPipeRegRegImm(dst, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object FaddfmaxlitewPipeRegRegReg extends Definition(
        "FaddfmaxlitewPipeRegRegReg",
        "FADDfmaXliteW_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("FADD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.FaddfmaxlitewPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object FaddfmaiPipeRegRegConst extends Definition(
        "FaddfmaiPipeRegRegConst",
        "FADDfmai_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const_FADD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.FaddfmaiPipeRegRegConst(dst, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object FaddfmaiPipeRegRegImm extends Definition(
        "FaddfmaiPipeRegRegImm",
        "FADDfmai_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("Imm_FADD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.FaddfmaiPipeRegRegImm(dst, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object FaddfmaiPipeRegRegReg extends Definition(
        "FaddfmaiPipeRegRegReg",
        "FADDfmai_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("FADD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.FaddfmaiPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object FaddfmalighterPipeRegRegAddressImm extends Definition(
        "FaddfmalighterPipeRegRegAddressImm",
        "FADDfmalighter_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcC", OperandKind.Address),
          Slot("sc_offset", OperandKind.Immediate)
        ),
        Vec("fadd__RRCx_RCx")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcC),
              InstructionOperand.Immediate(sc_offset)
            ) => Some(Instruction.FaddfmalighterPipeRegRegAddressImm(dst, srcA, srcC, sc_offset, modifiers, guard))
        case _ => None
      }
  }
  object FaddfmalighterPipeRegRegConst extends Definition(
        "FaddfmalighterPipeRegRegConst",
        "FADDfmalighter_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sc", OperandKind.ConstantBank)
        ),
        Vec("fadd__RRC_RC")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sc)
            ) => Some(Instruction.FaddfmalighterPipeRegRegConst(dst, srcA, sc, modifiers, guard))
        case _ => None
      }
  }
  object FaddfmalighterPipeRegRegImm extends Definition(
        "FaddfmalighterPipeRegRegImm",
        "FADDfmalighter_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sc", OperandKind.Immediate)),
        Vec("fadd__RRI_RI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.FaddfmalighterPipeRegRegImm(dst, srcA, sc, modifiers, guard))
        case _ => None
      }
  }
  object FaddfmalighterPipeRegRegReg extends Definition(
        "FaddfmalighterPipeRegRegReg",
        "FADDfmalighter_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcC", OperandKind.Register)),
        Vec("fadd__RRR_RR", "fadd__RRU_RU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.FaddfmalighterPipeRegRegReg(dst, srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FaddfxuPipeRegRegConst extends Definition(
        "FaddfxuPipeRegRegConst",
        "FADDfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const_FADD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.FaddfxuPipeRegRegConst(dst, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object FaddfxuPipeRegRegImm extends Definition(
        "FaddfxuPipeRegRegImm",
        "FADDfxu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("Imm_FADD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.FaddfxuPipeRegRegImm(dst, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object FaddfxuPipeRegRegReg extends Definition(
        "FaddfxuPipeRegRegReg",
        "FADDfxu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("FADD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.FaddfxuPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object FccoPredPredRegConstReg extends Definition(
        "FccoPredPredRegConstReg",
        "FCCO",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Const FCCO")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.FccoPredPredRegConstReg(dstPredicate, nPd, srcA, srcConst, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FccoPredPredRegImmReg extends Definition(
        "FccoPredPredRegImmReg",
        "FCCO",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm FCCO")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.FccoPredPredRegImmReg(dstPredicate, nPd, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FccoPredPredRegRegConst extends Definition(
        "FccoPredPredRegRegConst",
        "FCCO",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const1 FCCO")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.FccoPredPredRegRegConst(dstPredicate, nPd, srcA, srcB, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object FccoPredPredRegRegReg extends Definition(
        "FccoPredPredRegRegReg",
        "FCCO",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("FCCO")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.FccoPredPredRegRegReg(dstPredicate, nPd, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FccoPredRegConstReg extends Definition(
        "FccoPredRegConstReg",
        "FCCO",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Const FCCO")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.FccoPredRegConstReg(dstPredicate, srcA, srcConst, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FccoPredRegImmReg extends Definition(
        "FccoPredRegImmReg",
        "FCCO",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm FCCO")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.FccoPredRegImmReg(dstPredicate, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FccoPredRegRegConst extends Definition(
        "FccoPredRegRegConst",
        "FCCO",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const1 FCCO")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.FccoPredRegRegConst(dstPredicate, srcA, srcB, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object FccoPredRegRegReg extends Definition(
        "FccoPredRegRegReg",
        "FCCO",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("FCCO")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.FccoPredRegRegReg(dstPredicate, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FccofmaxPipePredPredRegConstReg extends Definition(
        "FccofmaxPipePredPredRegConstReg",
        "FCCOfmaX_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Const FCCO")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC)
            ) =>
          Some(Instruction.FccofmaxPipePredPredRegConstReg(dstPredicate, nPd, srcA, srcConst, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FccofmaxPipePredPredRegImmReg extends Definition(
        "FccofmaxPipePredPredRegImmReg",
        "FCCOfmaX_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm FCCO")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.FccofmaxPipePredPredRegImmReg(dstPredicate, nPd, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FccofmaxPipePredPredRegRegConst extends Definition(
        "FccofmaxPipePredPredRegRegConst",
        "FCCOfmaX_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const1 FCCO")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(srcConst)
            ) =>
          Some(Instruction.FccofmaxPipePredPredRegRegConst(dstPredicate, nPd, srcA, srcB, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object FccofmaxPipePredPredRegRegReg extends Definition(
        "FccofmaxPipePredPredRegRegReg",
        "FCCOfmaX_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("FCCO")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.FccofmaxPipePredPredRegRegReg(dstPredicate, nPd, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FccofmaxPipePredRegConstReg extends Definition(
        "FccofmaxPipePredRegConstReg",
        "FCCOfmaX_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Const FCCO")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.FccofmaxPipePredRegConstReg(dstPredicate, srcA, srcConst, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FccofmaxPipePredRegImmReg extends Definition(
        "FccofmaxPipePredRegImmReg",
        "FCCOfmaX_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm FCCO")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.FccofmaxPipePredRegImmReg(dstPredicate, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FccofmaxPipePredRegRegConst extends Definition(
        "FccofmaxPipePredRegRegConst",
        "FCCOfmaX_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const1 FCCO")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.FccofmaxPipePredRegRegConst(dstPredicate, srcA, srcB, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object FccofmaxPipePredRegRegReg extends Definition(
        "FccofmaxPipePredRegRegReg",
        "FCCOfmaX_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("FCCO")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.FccofmaxPipePredRegRegReg(dstPredicate, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FchkPredRegAddressImm extends Definition(
        "FchkPredRegAddressImm",
        "FCHK",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("fchk__RCxR_RCx")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.FchkPredRegAddressImm(pu, srcA, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object FchkPredRegConst extends Definition(
        "FchkPredRegConst",
        "FCHK",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const FCHK", "Const_FCHK", "fchk__RCR_RC")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.FchkPredRegConst(dstPredicate, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object FchkPredRegImm extends Definition(
        "FchkPredRegImm",
        "FCHK",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate)
        ),
        Vec("Imm FCHK", "Imm_FCHK", "fchk__RIR_RI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.FchkPredRegImm(dstPredicate, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object FchkPredRegReg extends Definition(
        "FchkPredRegReg",
        "FCHK",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register)
        ),
        Vec("FCHK", "fchk__RRR_RR", "fchk__RUR_RU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.FchkPredRegReg(dstPredicate, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object FchkfmaxPipePredRegConst extends Definition(
        "FchkfmaxPipePredRegConst",
        "FCHKfmaX_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const FCHK")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.FchkfmaxPipePredRegConst(dstPredicate, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object FchkfmaxPipePredRegImm extends Definition(
        "FchkfmaxPipePredRegImm",
        "FCHKfmaX_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate)
        ),
        Vec("Imm FCHK")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.FchkfmaxPipePredRegImm(dstPredicate, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object FchkfmaxPipePredRegReg extends Definition(
        "FchkfmaxPipePredRegReg",
        "FCHKfmaX_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register)
        ),
        Vec("FCHK")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.FchkfmaxPipePredRegReg(dstPredicate, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object FchkfxuPipePredRegConst extends Definition(
        "FchkfxuPipePredRegConst",
        "FCHKfxu_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const_FCHK")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.FchkfxuPipePredRegConst(dstPredicate, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object FchkfxuPipePredRegImm extends Definition(
        "FchkfxuPipePredRegImm",
        "FCHKfxu_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate)
        ),
        Vec("Imm_FCHK")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.FchkfxuPipePredRegImm(dstPredicate, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object FchkfxuPipePredRegReg extends Definition(
        "FchkfxuPipePredRegReg",
        "FCHKfxu_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register)
        ),
        Vec("FCHK")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.FchkfxuPipePredRegReg(dstPredicate, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object FchkmioPipePredRegAddressImm extends Definition(
        "FchkmioPipePredRegAddressImm",
        "FCHKmio_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("fchk__RCxR_RCx")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.FchkmioPipePredRegAddressImm(pu, srcA, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object FchkmioPipePredRegConst extends Definition(
        "FchkmioPipePredRegConst",
        "FCHKmio_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank)
        ),
        Vec("fchk__RCR_RC")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb)
            ) => Some(Instruction.FchkmioPipePredRegConst(pu, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object FchkmioPipePredRegImm extends Definition(
        "FchkmioPipePredRegImm",
        "FCHKmio_pipe",
        Vec(Slot("pu", OperandKind.Predicate), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("fchk__RIR_RI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.FchkmioPipePredRegImm(pu, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object FchkmioPipePredRegReg extends Definition(
        "FchkmioPipePredRegReg",
        "FCHKmio_pipe",
        Vec(Slot("pu", OperandKind.Predicate), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("fchk__RRR_RR", "fchk__RUR_RU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.FchkmioPipePredRegReg(pu, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object FcmpRegRegConstReg extends Definition(
        "FcmpRegRegConstReg",
        "FCMP",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Const FCMP", "Const_FCMP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.FcmpRegRegConstReg(dst, srcA, srcConst, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FcmpRegRegImmReg extends Definition(
        "FcmpRegRegImmReg",
        "FCMP",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm FCMP", "Imm_FCMP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.FcmpRegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FcmpRegRegRegConst extends Definition(
        "FcmpRegRegRegConst",
        "FCMP",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const1 FCMP", "Const1_FCMP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.FcmpRegRegRegConst(dst, srcA, srcB, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object FcmpRegRegRegReg extends Definition(
        "FcmpRegRegRegReg",
        "FCMP",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("FCMP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.FcmpRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FcmpfauPipeRegRegConstReg extends Definition(
        "FcmpfauPipeRegRegConstReg",
        "FCMPfau_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Const FCMP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.FcmpfauPipeRegRegConstReg(dst, srcA, srcConst, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FcmpfauPipeRegRegImmReg extends Definition(
        "FcmpfauPipeRegRegImmReg",
        "FCMPfau_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm FCMP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.FcmpfauPipeRegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FcmpfauPipeRegRegRegConst extends Definition(
        "FcmpfauPipeRegRegRegConst",
        "FCMPfau_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const1 FCMP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.FcmpfauPipeRegRegRegConst(dst, srcA, srcB, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object FcmpfauPipeRegRegRegReg extends Definition(
        "FcmpfauPipeRegRegRegReg",
        "FCMPfau_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("FCMP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.FcmpfauPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FcmpfxuPipeRegRegConstReg extends Definition(
        "FcmpfxuPipeRegRegConstReg",
        "FCMPfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Const_FCMP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.FcmpfxuPipeRegRegConstReg(dst, srcA, srcConst, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FcmpfxuPipeRegRegImmReg extends Definition(
        "FcmpfxuPipeRegRegImmReg",
        "FCMPfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm_FCMP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.FcmpfxuPipeRegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FcmpfxuPipeRegRegRegConst extends Definition(
        "FcmpfxuPipeRegRegRegConst",
        "FCMPfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const1_FCMP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.FcmpfxuPipeRegRegRegConst(dst, srcA, srcB, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object FcmpfxuPipeRegRegRegReg extends Definition(
        "FcmpfxuPipeRegRegRegReg",
        "FCMPfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("FCMP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.FcmpfxuPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FcmpxluPipeRegRegConstReg extends Definition(
        "FcmpxluPipeRegRegConstReg",
        "FCMPxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Const FCMP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.FcmpxluPipeRegRegConstReg(dst, srcA, srcConst, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FcmpxluPipeRegRegImmReg extends Definition(
        "FcmpxluPipeRegRegImmReg",
        "FCMPxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm FCMP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.FcmpxluPipeRegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FcmpxluPipeRegRegRegConst extends Definition(
        "FcmpxluPipeRegRegRegConst",
        "FCMPxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const1 FCMP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.FcmpxluPipeRegRegRegConst(dst, srcA, srcB, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object FcmpxluPipeRegRegRegReg extends Definition(
        "FcmpxluPipeRegRegRegReg",
        "FCMPxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("FCMP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.FcmpxluPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FenceNoOperands extends Definition("FenceNoOperands", "FENCE", Vec(), Vec("fence_", "fence_g_", "fence_t_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.FenceNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object FencemioPipeNoOperands
      extends Definition("FencemioPipeNoOperands", "FENCEmio_pipe", Vec(), Vec("fence_", "fence_g_", "fence_t_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.FencemioPipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object FfmaRegRegAddressImmReg extends Definition(
        "FfmaRegRegAddressImmReg",
        "FFMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("ffma__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.FfmaRegRegAddressImmReg(dst, srcA, srcB, sb_offset, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FfmaRegRegConstReg extends Definition(
        "FfmaRegRegConstReg",
        "FFMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Const FFMA", "Const_FFMA", "ffma__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.FfmaRegRegConstReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FfmaRegRegImmReg extends Definition(
        "FfmaRegRegImmReg",
        "FFMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm FFMA", "Imm_FFMA", "ffma__RIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.FfmaRegRegImmReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FfmaRegRegRegAddressImm extends Definition(
        "FfmaRegRegRegAddressImm",
        "FFMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Address),
          Slot("sc_offset", OperandKind.Immediate)
        ),
        Vec("ffma__RRCx_RRCx")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Address(srcC),
              InstructionOperand.Immediate(sc_offset)
            ) => Some(Instruction.FfmaRegRegRegAddressImm(dst, srcA, srcB, srcC, sc_offset, modifiers, guard))
        case _ => None
      }
  }
  object FfmaRegRegRegConst extends Definition(
        "FfmaRegRegRegConst",
        "FFMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.ConstantBank)
        ),
        Vec("Const1 FFMA", "Const1_FFMA", "ffma__RRC_RRC")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(sc)
            ) => Some(Instruction.FfmaRegRegRegConst(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object FfmaRegRegRegImm extends Definition(
        "FfmaRegRegRegImm",
        "FFMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate)
        ),
        Vec("ffma__RRI_RRI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.FfmaRegRegRegImm(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object FfmaRegRegRegReg extends Definition(
        "FfmaRegRegRegReg",
        "FFMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("FFMA", "ffma__RRR_RRR", "ffma__RRU_RRU", "ffma__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.FfmaRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Ffma2RegRegImmReg extends Definition(
        "Ffma2RegRegImmReg",
        "FFMA2",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("ffma2_x_rc__RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Ffma2RegRegImmReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Ffma2RegRegRegImm extends Definition(
        "Ffma2RegRegRegImm",
        "FFMA2",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate)
        ),
        Vec("ffma2_rb_x__RRI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.Ffma2RegRegRegImm(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object Ffma2RegRegRegReg extends Definition(
        "Ffma2RegRegRegReg",
        "FFMA2",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("ffma2_rb_rc__RRR", "ffma2_rb_urc__RRU", "ffma2_urb_rc__RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Ffma2RegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Ffma2fmalighterPipeRegRegImmReg extends Definition(
        "Ffma2fmalighterPipeRegRegImmReg",
        "FFMA2fmalighter_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("ffma2_x_rc__RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Ffma2fmalighterPipeRegRegImmReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Ffma2fmalighterPipeRegRegRegImm extends Definition(
        "Ffma2fmalighterPipeRegRegRegImm",
        "FFMA2fmalighter_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate)
        ),
        Vec("ffma2_rb_x__RRI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.Ffma2fmalighterPipeRegRegRegImm(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object Ffma2fmalighterPipeRegRegRegReg extends Definition(
        "Ffma2fmalighterPipeRegRegRegReg",
        "FFMA2fmalighter_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("ffma2_rb_rc__RRR", "ffma2_rb_urc__RRU", "ffma2_urb_rc__RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Ffma2fmalighterPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Ffma32iRegRegImm extends Definition(
        "Ffma32iRegRegImm",
        "FFMA32I",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("fImm", OperandKind.Immediate)),
        Vec("FFMA32I 2", "FFMA32I_2")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(fImm)
            ) => Some(Instruction.Ffma32iRegRegImm(dst, srcA, fImm, modifiers, guard))
        case _ => None
      }
  }
  object Ffma32iRegRegImmReg extends Definition(
        "Ffma32iRegRegImmReg",
        "FFMA32I",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("FFMA32I", "ffma32i_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Ffma32iRegRegImmReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Ffma32ifauPipeRegRegImm extends Definition(
        "Ffma32ifauPipeRegRegImm",
        "FFMA32Ifau_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("fImm", OperandKind.Immediate)),
        Vec("FFMA32I 2")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(fImm)
            ) => Some(Instruction.Ffma32ifauPipeRegRegImm(dst, srcA, fImm, modifiers, guard))
        case _ => None
      }
  }
  object Ffma32ifauPipeRegRegImmReg extends Definition(
        "Ffma32ifauPipeRegRegImmReg",
        "FFMA32Ifau_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("fImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("FFMA32I")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(fImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Ffma32ifauPipeRegRegImmReg(dst, srcA, fImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Ffma32ifmaxPipeRegRegImm extends Definition(
        "Ffma32ifmaxPipeRegRegImm",
        "FFMA32IfmaX_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("fImm", OperandKind.Immediate)),
        Vec("FFMA32I 2")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(fImm)
            ) => Some(Instruction.Ffma32ifmaxPipeRegRegImm(dst, srcA, fImm, modifiers, guard))
        case _ => None
      }
  }
  object Ffma32ifmaxPipeRegRegImmReg extends Definition(
        "Ffma32ifmaxPipeRegRegImmReg",
        "FFMA32IfmaX_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("fImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("FFMA32I")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(fImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Ffma32ifmaxPipeRegRegImmReg(dst, srcA, fImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Ffma32ifmaxlitewPipeRegRegImm extends Definition(
        "Ffma32ifmaxlitewPipeRegRegImm",
        "FFMA32IfmaXliteW_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("fImm", OperandKind.Immediate)),
        Vec("FFMA32I 2")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(fImm)
            ) => Some(Instruction.Ffma32ifmaxlitewPipeRegRegImm(dst, srcA, fImm, modifiers, guard))
        case _ => None
      }
  }
  object Ffma32ifmaxlitewPipeRegRegImmReg extends Definition(
        "Ffma32ifmaxlitewPipeRegRegImmReg",
        "FFMA32IfmaXliteW_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("fImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("FFMA32I")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(fImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Ffma32ifmaxlitewPipeRegRegImmReg(dst, srcA, fImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Ffma32ifmaiPipeRegRegImm extends Definition(
        "Ffma32ifmaiPipeRegRegImm",
        "FFMA32Ifmai_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("fImm", OperandKind.Immediate)),
        Vec("FFMA32I_2")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(fImm)
            ) => Some(Instruction.Ffma32ifmaiPipeRegRegImm(dst, srcA, fImm, modifiers, guard))
        case _ => None
      }
  }
  object Ffma32ifmaiPipeRegRegImmReg extends Definition(
        "Ffma32ifmaiPipeRegRegImmReg",
        "FFMA32Ifmai_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("fImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("FFMA32I")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(fImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Ffma32ifmaiPipeRegRegImmReg(dst, srcA, fImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Ffma32ifmalighterPipeRegRegImmReg extends Definition(
        "Ffma32ifmalighterPipeRegRegImmReg",
        "FFMA32Ifmalighter_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("ffma32i_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Ffma32ifmalighterPipeRegRegImmReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Ffma32ifxuPipeRegRegImm extends Definition(
        "Ffma32ifxuPipeRegRegImm",
        "FFMA32Ifxu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("fImm", OperandKind.Immediate)),
        Vec("FFMA32I_2")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(fImm)
            ) => Some(Instruction.Ffma32ifxuPipeRegRegImm(dst, srcA, fImm, modifiers, guard))
        case _ => None
      }
  }
  object Ffma32ifxuPipeRegRegImmReg extends Definition(
        "Ffma32ifxuPipeRegRegImmReg",
        "FFMA32Ifxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("fImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("FFMA32I")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(fImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Ffma32ifxuPipeRegRegImmReg(dst, srcA, fImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FfmafauPipeRegRegConstReg extends Definition(
        "FfmafauPipeRegRegConstReg",
        "FFMAfau_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Const FFMA")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.FfmafauPipeRegRegConstReg(dst, srcA, srcConst, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FfmafauPipeRegRegImmReg extends Definition(
        "FfmafauPipeRegRegImmReg",
        "FFMAfau_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm FFMA")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.FfmafauPipeRegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FfmafauPipeRegRegRegConst extends Definition(
        "FfmafauPipeRegRegRegConst",
        "FFMAfau_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const1 FFMA")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.FfmafauPipeRegRegRegConst(dst, srcA, srcB, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object FfmafauPipeRegRegRegReg extends Definition(
        "FfmafauPipeRegRegRegReg",
        "FFMAfau_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("FFMA")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.FfmafauPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FfmafmaxPipeRegRegConstReg extends Definition(
        "FfmafmaxPipeRegRegConstReg",
        "FFMAfmaX_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Const FFMA")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.FfmafmaxPipeRegRegConstReg(dst, srcA, srcConst, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FfmafmaxPipeRegRegImmReg extends Definition(
        "FfmafmaxPipeRegRegImmReg",
        "FFMAfmaX_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm FFMA")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.FfmafmaxPipeRegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FfmafmaxPipeRegRegRegConst extends Definition(
        "FfmafmaxPipeRegRegRegConst",
        "FFMAfmaX_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const1 FFMA")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.FfmafmaxPipeRegRegRegConst(dst, srcA, srcB, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object FfmafmaxPipeRegRegRegReg extends Definition(
        "FfmafmaxPipeRegRegRegReg",
        "FFMAfmaX_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("FFMA")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.FfmafmaxPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FfmafmaxlitewPipeRegRegConstReg extends Definition(
        "FfmafmaxlitewPipeRegRegConstReg",
        "FFMAfmaXliteW_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Const FFMA")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.FfmafmaxlitewPipeRegRegConstReg(dst, srcA, srcConst, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FfmafmaxlitewPipeRegRegImmReg extends Definition(
        "FfmafmaxlitewPipeRegRegImmReg",
        "FFMAfmaXliteW_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm FFMA")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.FfmafmaxlitewPipeRegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FfmafmaxlitewPipeRegRegRegConst extends Definition(
        "FfmafmaxlitewPipeRegRegRegConst",
        "FFMAfmaXliteW_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const1 FFMA")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.FfmafmaxlitewPipeRegRegRegConst(dst, srcA, srcB, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object FfmafmaxlitewPipeRegRegRegReg extends Definition(
        "FfmafmaxlitewPipeRegRegRegReg",
        "FFMAfmaXliteW_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("FFMA")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.FfmafmaxlitewPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FfmafmaiPipeRegRegConstReg extends Definition(
        "FfmafmaiPipeRegRegConstReg",
        "FFMAfmai_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Const_FFMA")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.FfmafmaiPipeRegRegConstReg(dst, srcA, srcConst, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FfmafmaiPipeRegRegImmReg extends Definition(
        "FfmafmaiPipeRegRegImmReg",
        "FFMAfmai_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm_FFMA")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.FfmafmaiPipeRegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FfmafmaiPipeRegRegRegConst extends Definition(
        "FfmafmaiPipeRegRegRegConst",
        "FFMAfmai_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const1_FFMA")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.FfmafmaiPipeRegRegRegConst(dst, srcA, srcB, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object FfmafmaiPipeRegRegRegReg extends Definition(
        "FfmafmaiPipeRegRegRegReg",
        "FFMAfmai_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("FFMA")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.FfmafmaiPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FfmafmalighterPipeRegRegAddressImmReg extends Definition(
        "FfmafmalighterPipeRegRegAddressImmReg",
        "FFMAfmalighter_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("ffma__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC)
            ) =>
          Some(Instruction.FfmafmalighterPipeRegRegAddressImmReg(dst, srcA, srcB, sb_offset, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FfmafmalighterPipeRegRegConstReg extends Definition(
        "FfmafmalighterPipeRegRegConstReg",
        "FFMAfmalighter_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("ffma__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.FfmafmalighterPipeRegRegConstReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FfmafmalighterPipeRegRegImmReg extends Definition(
        "FfmafmalighterPipeRegRegImmReg",
        "FFMAfmalighter_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("ffma__RIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.FfmafmalighterPipeRegRegImmReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FfmafmalighterPipeRegRegRegAddressImm extends Definition(
        "FfmafmalighterPipeRegRegRegAddressImm",
        "FFMAfmalighter_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Address),
          Slot("sc_offset", OperandKind.Immediate)
        ),
        Vec("ffma__RRCx_RRCx")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Address(srcC),
              InstructionOperand.Immediate(sc_offset)
            ) =>
          Some(Instruction.FfmafmalighterPipeRegRegRegAddressImm(dst, srcA, srcB, srcC, sc_offset, modifiers, guard))
        case _ => None
      }
  }
  object FfmafmalighterPipeRegRegRegConst extends Definition(
        "FfmafmalighterPipeRegRegRegConst",
        "FFMAfmalighter_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.ConstantBank)
        ),
        Vec("ffma__RRC_RRC")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(sc)
            ) => Some(Instruction.FfmafmalighterPipeRegRegRegConst(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object FfmafmalighterPipeRegRegRegImm extends Definition(
        "FfmafmalighterPipeRegRegRegImm",
        "FFMAfmalighter_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate)
        ),
        Vec("ffma__RRI_RRI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.FfmafmalighterPipeRegRegRegImm(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object FfmafmalighterPipeRegRegRegReg extends Definition(
        "FfmafmalighterPipeRegRegRegReg",
        "FFMAfmalighter_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("ffma__RRR_RRR", "ffma__RRU_RRU", "ffma__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.FfmafmalighterPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FfmafxuPipeRegRegConstReg extends Definition(
        "FfmafxuPipeRegRegConstReg",
        "FFMAfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Const_FFMA")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.FfmafxuPipeRegRegConstReg(dst, srcA, srcConst, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FfmafxuPipeRegRegImmReg extends Definition(
        "FfmafxuPipeRegRegImmReg",
        "FFMAfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm_FFMA")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.FfmafxuPipeRegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FfmafxuPipeRegRegRegConst extends Definition(
        "FfmafxuPipeRegRegRegConst",
        "FFMAfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const1_FFMA")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.FfmafxuPipeRegRegRegConst(dst, srcA, srcB, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object FfmafxuPipeRegRegRegReg extends Definition(
        "FfmafxuPipeRegRegRegReg",
        "FFMAfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("FFMA")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.FfmafxuPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FhaddRegRegReg extends Definition(
        "FhaddRegRegReg",
        "FHADD",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcC", OperandKind.Register)),
        Vec("fhadd__RR", "fhadd__RU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.FhaddRegRegReg(dst, srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FhaddfmalighterPipeRegRegReg extends Definition(
        "FhaddfmalighterPipeRegRegReg",
        "FHADDfmalighter_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcC", OperandKind.Register)),
        Vec("fhadd__RR", "fhadd__RU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.FhaddfmalighterPipeRegRegReg(dst, srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FhfmaRegRegRegReg extends Definition(
        "FhfmaRegRegRegReg",
        "FHFMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("fhfma__RRR", "fhfma__RRU", "fhfma__RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.FhfmaRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FhfmafmalighterPipeRegRegRegReg extends Definition(
        "FhfmafmalighterPipeRegRegRegReg",
        "FHFMAfmalighter_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("fhfma__RRR", "fhfma__RRU", "fhfma__RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.FhfmafmalighterPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FloRegAddressImm extends Definition(
        "FloRegAddressImm",
        "FLO",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("flo__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.FloRegAddressImm(dst, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object FloRegConst extends Definition(
        "FloRegConst",
        "FLO",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.ConstantBank)),
        Vec("Const FLO", "Const_FLO", "flo__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(sb)) =>
          Some(Instruction.FloRegConst(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object FloRegImm extends Definition(
        "FloRegImm",
        "FLO",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("Imm FLO", "Imm_FLO", "flo__RuIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.FloRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object FloRegPredAddressImm extends Definition(
        "FloRegPredAddressImm",
        "FLO",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("flo__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.FloRegPredAddressImm(dst, pu, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object FloRegPredConst extends Definition(
        "FloRegPredConst",
        "FLO",
        Vec(Slot("dst", OperandKind.Register), Slot("pu", OperandKind.Predicate), Slot("sb", OperandKind.ConstantBank)),
        Vec("flo__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.ConstantBank(sb)
            ) => Some(Instruction.FloRegPredConst(dst, pu, sb, modifiers, guard))
        case _ => None
      }
  }
  object FloRegPredImm extends Definition(
        "FloRegPredImm",
        "FLO",
        Vec(Slot("dst", OperandKind.Register), Slot("pu", OperandKind.Predicate), Slot("sb", OperandKind.Immediate)),
        Vec("flo__RuIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.FloRegPredImm(dst, pu, sb, modifiers, guard))
        case _ => None
      }
  }
  object FloRegPredReg extends Definition(
        "FloRegPredReg",
        "FLO",
        Vec(Slot("dst", OperandKind.Register), Slot("pu", OperandKind.Predicate), Slot("srcB", OperandKind.Register)),
        Vec("flo__RRR_RRR", "flo__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.FloRegPredReg(dst, pu, srcB, modifiers, guard))
        case _ => None
      }
  }
  object FloRegReg extends Definition(
        "FloRegReg",
        "FLO",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("FLO", "flo__RRR_RRR", "flo__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.FloRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object FlomioPipeRegAddressImm extends Definition(
        "FlomioPipeRegAddressImm",
        "FLOmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("flo__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.FlomioPipeRegAddressImm(dst, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object FlomioPipeRegConst extends Definition(
        "FlomioPipeRegConst",
        "FLOmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.ConstantBank)),
        Vec("Const_FLO", "flo__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(sb)) =>
          Some(Instruction.FlomioPipeRegConst(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object FlomioPipeRegImm extends Definition(
        "FlomioPipeRegImm",
        "FLOmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("Imm_FLO", "flo__RuIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.FlomioPipeRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object FlomioPipeRegPredAddressImm extends Definition(
        "FlomioPipeRegPredAddressImm",
        "FLOmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("flo__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.FlomioPipeRegPredAddressImm(dst, pu, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object FlomioPipeRegPredConst extends Definition(
        "FlomioPipeRegPredConst",
        "FLOmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("pu", OperandKind.Predicate), Slot("sb", OperandKind.ConstantBank)),
        Vec("flo__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.ConstantBank(sb)
            ) => Some(Instruction.FlomioPipeRegPredConst(dst, pu, sb, modifiers, guard))
        case _ => None
      }
  }
  object FlomioPipeRegPredImm extends Definition(
        "FlomioPipeRegPredImm",
        "FLOmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("pu", OperandKind.Predicate), Slot("sb", OperandKind.Immediate)),
        Vec("flo__RuIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.FlomioPipeRegPredImm(dst, pu, sb, modifiers, guard))
        case _ => None
      }
  }
  object FlomioPipeRegPredReg extends Definition(
        "FlomioPipeRegPredReg",
        "FLOmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("pu", OperandKind.Predicate), Slot("srcB", OperandKind.Register)),
        Vec("flo__RRR_RRR", "flo__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.FlomioPipeRegPredReg(dst, pu, srcB, modifiers, guard))
        case _ => None
      }
  }
  object FlomioPipeRegReg extends Definition(
        "FlomioPipeRegReg",
        "FLOmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("FLO", "flo__RRR_RRR", "flo__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.FlomioPipeRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object FloxluPipeRegConst extends Definition(
        "FloxluPipeRegConst",
        "FLOxlu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcConst", OperandKind.ConstantBank)),
        Vec("Const FLO")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(srcConst)) =>
          Some(Instruction.FloxluPipeRegConst(dst, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object FloxluPipeRegImm extends Definition(
        "FloxluPipeRegImm",
        "FLOxlu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("Imm FLO")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(uImm)) =>
          Some(Instruction.FloxluPipeRegImm(dst, uImm, modifiers, guard))
        case _ => None
      }
  }
  object FloxluPipeRegReg extends Definition(
        "FloxluPipeRegReg",
        "FLOxlu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("FLO")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.FloxluPipeRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object FmnmxRegPredRegAddressImmPred extends Definition(
        "FmnmxRegPredRegAddressImmPred",
        "FMNMX",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("fmnmx_pred__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.FmnmxRegPredRegAddressImmPred(dst, pu, srcA, srcB, sb_offset, pp, modifiers, guard))
        case _ => None
      }
  }
  object FmnmxRegPredRegConstPred extends Definition(
        "FmnmxRegPredRegConstPred",
        "FMNMX",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("fmnmx_pred__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.FmnmxRegPredRegConstPred(dst, pu, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object FmnmxRegPredRegImmPred extends Definition(
        "FmnmxRegPredRegImmPred",
        "FMNMX",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("fmnmx_pred__RIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.FmnmxRegPredRegImmPred(dst, pu, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object FmnmxRegPredRegRegPred extends Definition(
        "FmnmxRegPredRegRegPred",
        "FMNMX",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("fmnmx_pred__RRR_RRR", "fmnmx_pred__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.FmnmxRegPredRegRegPred(dst, pu, srcA, srcB, pp, modifiers, guard))
        case _ => None
      }
  }
  object FmnmxRegRegAddressImmPred extends Definition(
        "FmnmxRegRegAddressImmPred",
        "FMNMX",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("fmnmx__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.FmnmxRegRegAddressImmPred(dst, srcA, srcB, sb_offset, pp, modifiers, guard))
        case _ => None
      }
  }
  object FmnmxRegRegConstPred extends Definition(
        "FmnmxRegRegConstPred",
        "FMNMX",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("Const FMNMX", "Const_FMNMX", "fmnmx__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.FmnmxRegRegConstPred(dst, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object FmnmxRegRegImmPred extends Definition(
        "FmnmxRegRegImmPred",
        "FMNMX",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("Imm FMNMX", "Imm_FMNMX", "fmnmx__RIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.FmnmxRegRegImmPred(dst, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object FmnmxRegRegRegPred extends Definition(
        "FmnmxRegRegRegPred",
        "FMNMX",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("FMNMX", "fmnmx__RRR_RRR", "fmnmx__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.FmnmxRegRegRegPred(dst, srcA, srcB, pa, modifiers, guard))
        case _ => None
      }
  }
  object Fmnmx3RegRegImmRegPred extends Definition(
        "Fmnmx3RegRegImmRegPred",
        "FMNMX3",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("fmnmx3__RIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Fmnmx3RegRegImmRegPred(dst, srcA, sb, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object Fmnmx3RegRegRegRegPred extends Definition(
        "Fmnmx3RegRegRegRegPred",
        "FMNMX3",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("fmnmx3__RRR_RRR", "fmnmx3__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Fmnmx3RegRegRegRegPred(dst, srcA, srcB, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object Fmnmx3intPipeRegRegImmRegPred extends Definition(
        "Fmnmx3intPipeRegRegImmRegPred",
        "FMNMX3int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("fmnmx3__RIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Fmnmx3intPipeRegRegImmRegPred(dst, srcA, sb, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object Fmnmx3intPipeRegRegRegRegPred extends Definition(
        "Fmnmx3intPipeRegRegRegRegPred",
        "FMNMX3int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("fmnmx3__RRR_RRR", "fmnmx3__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Fmnmx3intPipeRegRegRegRegPred(dst, srcA, srcB, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object FmnmxfauPipeRegRegConstPred extends Definition(
        "FmnmxfauPipeRegRegConstPred",
        "FMNMXfau_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Const FMNMX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.FmnmxfauPipeRegRegConstPred(dst, srcA, srcConst, pa, modifiers, guard))
        case _ => None
      }
  }
  object FmnmxfauPipeRegRegImmPred extends Definition(
        "FmnmxfauPipeRegRegImmPred",
        "FMNMXfau_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Imm FMNMX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.FmnmxfauPipeRegRegImmPred(dst, srcA, uImm, pa, modifiers, guard))
        case _ => None
      }
  }
  object FmnmxfauPipeRegRegRegPred extends Definition(
        "FmnmxfauPipeRegRegRegPred",
        "FMNMXfau_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("FMNMX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.FmnmxfauPipeRegRegRegPred(dst, srcA, srcB, pa, modifiers, guard))
        case _ => None
      }
  }
  object FmnmxfmaxPipeRegRegConstPred extends Definition(
        "FmnmxfmaxPipeRegRegConstPred",
        "FMNMXfmaX_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Const FMNMX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.FmnmxfmaxPipeRegRegConstPred(dst, srcA, srcConst, pa, modifiers, guard))
        case _ => None
      }
  }
  object FmnmxfmaxPipeRegRegImmPred extends Definition(
        "FmnmxfmaxPipeRegRegImmPred",
        "FMNMXfmaX_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Imm FMNMX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.FmnmxfmaxPipeRegRegImmPred(dst, srcA, uImm, pa, modifiers, guard))
        case _ => None
      }
  }
  object FmnmxfmaxPipeRegRegRegPred extends Definition(
        "FmnmxfmaxPipeRegRegRegPred",
        "FMNMXfmaX_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("FMNMX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.FmnmxfmaxPipeRegRegRegPred(dst, srcA, srcB, pa, modifiers, guard))
        case _ => None
      }
  }
  object FmnmxfxuPipeRegRegConstPred extends Definition(
        "FmnmxfxuPipeRegRegConstPred",
        "FMNMXfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Const_FMNMX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.FmnmxfxuPipeRegRegConstPred(dst, srcA, srcConst, pa, modifiers, guard))
        case _ => None
      }
  }
  object FmnmxfxuPipeRegRegImmPred extends Definition(
        "FmnmxfxuPipeRegRegImmPred",
        "FMNMXfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Imm_FMNMX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.FmnmxfxuPipeRegRegImmPred(dst, srcA, uImm, pa, modifiers, guard))
        case _ => None
      }
  }
  object FmnmxfxuPipeRegRegRegPred extends Definition(
        "FmnmxfxuPipeRegRegRegPred",
        "FMNMXfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("FMNMX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.FmnmxfxuPipeRegRegRegPred(dst, srcA, srcB, pa, modifiers, guard))
        case _ => None
      }
  }
  object FmnmxintPipeRegPredRegAddressImmPred extends Definition(
        "FmnmxintPipeRegPredRegAddressImmPred",
        "FMNMXint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("fmnmx_pred__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Predicate(pp)
            ) =>
          Some(Instruction.FmnmxintPipeRegPredRegAddressImmPred(dst, pu, srcA, srcB, sb_offset, pp, modifiers, guard))
        case _ => None
      }
  }
  object FmnmxintPipeRegPredRegConstPred extends Definition(
        "FmnmxintPipeRegPredRegConstPred",
        "FMNMXint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("fmnmx_pred__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.FmnmxintPipeRegPredRegConstPred(dst, pu, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object FmnmxintPipeRegPredRegImmPred extends Definition(
        "FmnmxintPipeRegPredRegImmPred",
        "FMNMXint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("fmnmx_pred__RIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.FmnmxintPipeRegPredRegImmPred(dst, pu, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object FmnmxintPipeRegPredRegRegPred extends Definition(
        "FmnmxintPipeRegPredRegRegPred",
        "FMNMXint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("fmnmx_pred__RRR_RRR", "fmnmx_pred__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.FmnmxintPipeRegPredRegRegPred(dst, pu, srcA, srcB, pp, modifiers, guard))
        case _ => None
      }
  }
  object FmnmxintPipeRegRegAddressImmPred extends Definition(
        "FmnmxintPipeRegRegAddressImmPred",
        "FMNMXint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("fmnmx__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.FmnmxintPipeRegRegAddressImmPred(dst, srcA, srcB, sb_offset, pp, modifiers, guard))
        case _ => None
      }
  }
  object FmnmxintPipeRegRegConstPred extends Definition(
        "FmnmxintPipeRegRegConstPred",
        "FMNMXint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("fmnmx__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.FmnmxintPipeRegRegConstPred(dst, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object FmnmxintPipeRegRegImmPred extends Definition(
        "FmnmxintPipeRegRegImmPred",
        "FMNMXint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("fmnmx__RIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.FmnmxintPipeRegRegImmPred(dst, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object FmnmxintPipeRegRegRegPred extends Definition(
        "FmnmxintPipeRegRegRegPred",
        "FMNMXint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("fmnmx__RRR_RRR", "fmnmx__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.FmnmxintPipeRegRegRegPred(dst, srcA, srcB, pp, modifiers, guard))
        case _ => None
      }
  }
  object FmulRegRegAddressImm extends Definition(
        "FmulRegRegAddressImm",
        "FMUL",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("fmul__RCxR_RCx")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.FmulRegRegAddressImm(dst, srcA, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object FmulRegRegConst extends Definition(
        "FmulRegRegConst",
        "FMUL",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank)
        ),
        Vec("Const FMUL", "Const_FMUL", "fmul__RCR_RC")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb)
            ) => Some(Instruction.FmulRegRegConst(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object FmulRegRegImm extends Definition(
        "FmulRegRegImm",
        "FMUL",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("Imm FMUL", "Imm_FMUL", "fmul__RIR_RI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.FmulRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object FmulRegRegReg extends Definition(
        "FmulRegRegReg",
        "FMUL",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("FMUL", "fmul__RRR_RR", "fmul__RUR_RU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.FmulRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object Fmul2RegRegImm extends Definition(
        "Fmul2RegRegImm",
        "FMUL2",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("fmul2_imm__RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.Fmul2RegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  val definitions: Vec[Definition] = Vec(
    F2fpRegImm,
    F2fpRegImmReg,
    F2fpRegReg,
    F2fpRegRegAddressImm,
    F2fpRegRegAddressImmReg,
    F2fpRegRegConst,
    F2fpRegRegConstReg,
    F2fpRegRegImm,
    F2fpRegRegImmReg,
    F2fpRegRegReg,
    F2fpRegRegRegAddressImm,
    F2fpRegRegRegConst,
    F2fpRegRegRegImm,
    F2fpRegRegRegReg,
    F2fpintPipeRegAddressImm,
    F2fpintPipeRegAddressImmReg,
    F2fpintPipeRegConst,
    F2fpintPipeRegConstReg,
    F2fpintPipeRegImm,
    F2fpintPipeRegImmReg,
    F2fpintPipeRegReg,
    F2fpintPipeRegRegAddressImm,
    F2fpintPipeRegRegAddressImmReg,
    F2fpintPipeRegRegConst,
    F2fpintPipeRegRegConstReg,
    F2fpintPipeRegRegImm,
    F2fpintPipeRegRegImmReg,
    F2fpintPipeRegRegReg,
    F2fpintPipeRegRegRegAddressImm,
    F2fpintPipeRegRegRegConst,
    F2fpintPipeRegRegRegImm,
    F2fpintPipeRegRegRegReg,
    F2ffmaxPipeRegConst,
    F2ffmaxPipeRegImm,
    F2ffmaxPipeRegReg,
    F2fmioPipeRegAddressImm,
    F2fmioPipeRegConst,
    F2fmioPipeRegImm,
    F2fmioPipeRegReg,
    F2iRegAddressImm,
    F2iRegConst,
    F2iRegImm,
    F2iRegReg,
    F2ipRegRegAddressImmReg,
    F2ipRegRegConstReg,
    F2ipRegRegImmReg,
    F2ipRegRegRegAddressImm,
    F2ipRegRegRegConst,
    F2ipRegRegRegImm,
    F2ipRegRegRegReg,
    F2ipintPipeRegRegAddressImmReg,
    F2ipintPipeRegRegConstReg,
    F2ipintPipeRegRegImmReg,
    F2ipintPipeRegRegRegAddressImm,
    F2ipintPipeRegRegRegConst,
    F2ipintPipeRegRegRegImm,
    F2ipintPipeRegRegRegReg,
    F2ifmaxPipeRegConst,
    F2ifmaxPipeRegImm,
    F2ifmaxPipeRegReg,
    F2imioPipeRegAddressImm,
    F2imioPipeRegConst,
    F2imioPipeRegImm,
    F2imioPipeRegReg,
    FaddRegRegAddressImm,
    FaddRegRegConst,
    FaddRegRegImm,
    FaddRegRegReg,
    Fadd2RegRegImm,
    Fadd2RegRegReg,
    Fadd2fmalighterPipeRegRegImm,
    Fadd2fmalighterPipeRegRegReg,
    Fadd32iRegRegImm,
    Fadd32ifauPipeRegRegImm,
    Fadd32ifmaxPipeRegRegImm,
    Fadd32ifmaxlitewPipeRegRegImm,
    Fadd32ifmaiPipeRegRegImm,
    Fadd32ifmalighterPipeRegRegImm,
    Fadd32ifxuPipeRegRegImm,
    FaddfauPipeRegRegConst,
    FaddfauPipeRegRegImm,
    FaddfauPipeRegRegReg,
    FaddfmaxPipeRegRegConst,
    FaddfmaxPipeRegRegImm,
    FaddfmaxPipeRegRegReg,
    FaddfmaxlitewPipeRegRegConst,
    FaddfmaxlitewPipeRegRegImm,
    FaddfmaxlitewPipeRegRegReg,
    FaddfmaiPipeRegRegConst,
    FaddfmaiPipeRegRegImm,
    FaddfmaiPipeRegRegReg,
    FaddfmalighterPipeRegRegAddressImm,
    FaddfmalighterPipeRegRegConst,
    FaddfmalighterPipeRegRegImm,
    FaddfmalighterPipeRegRegReg,
    FaddfxuPipeRegRegConst,
    FaddfxuPipeRegRegImm,
    FaddfxuPipeRegRegReg,
    FccoPredPredRegConstReg,
    FccoPredPredRegImmReg,
    FccoPredPredRegRegConst,
    FccoPredPredRegRegReg,
    FccoPredRegConstReg,
    FccoPredRegImmReg,
    FccoPredRegRegConst,
    FccoPredRegRegReg,
    FccofmaxPipePredPredRegConstReg,
    FccofmaxPipePredPredRegImmReg,
    FccofmaxPipePredPredRegRegConst,
    FccofmaxPipePredPredRegRegReg,
    FccofmaxPipePredRegConstReg,
    FccofmaxPipePredRegImmReg,
    FccofmaxPipePredRegRegConst,
    FccofmaxPipePredRegRegReg,
    FchkPredRegAddressImm,
    FchkPredRegConst,
    FchkPredRegImm,
    FchkPredRegReg,
    FchkfmaxPipePredRegConst,
    FchkfmaxPipePredRegImm,
    FchkfmaxPipePredRegReg,
    FchkfxuPipePredRegConst,
    FchkfxuPipePredRegImm,
    FchkfxuPipePredRegReg,
    FchkmioPipePredRegAddressImm,
    FchkmioPipePredRegConst,
    FchkmioPipePredRegImm,
    FchkmioPipePredRegReg,
    FcmpRegRegConstReg,
    FcmpRegRegImmReg,
    FcmpRegRegRegConst,
    FcmpRegRegRegReg,
    FcmpfauPipeRegRegConstReg,
    FcmpfauPipeRegRegImmReg,
    FcmpfauPipeRegRegRegConst,
    FcmpfauPipeRegRegRegReg,
    FcmpfxuPipeRegRegConstReg,
    FcmpfxuPipeRegRegImmReg,
    FcmpfxuPipeRegRegRegConst,
    FcmpfxuPipeRegRegRegReg,
    FcmpxluPipeRegRegConstReg,
    FcmpxluPipeRegRegImmReg,
    FcmpxluPipeRegRegRegConst,
    FcmpxluPipeRegRegRegReg,
    FenceNoOperands,
    FencemioPipeNoOperands,
    FfmaRegRegAddressImmReg,
    FfmaRegRegConstReg,
    FfmaRegRegImmReg,
    FfmaRegRegRegAddressImm,
    FfmaRegRegRegConst,
    FfmaRegRegRegImm,
    FfmaRegRegRegReg,
    Ffma2RegRegImmReg,
    Ffma2RegRegRegImm,
    Ffma2RegRegRegReg,
    Ffma2fmalighterPipeRegRegImmReg,
    Ffma2fmalighterPipeRegRegRegImm,
    Ffma2fmalighterPipeRegRegRegReg,
    Ffma32iRegRegImm,
    Ffma32iRegRegImmReg,
    Ffma32ifauPipeRegRegImm,
    Ffma32ifauPipeRegRegImmReg,
    Ffma32ifmaxPipeRegRegImm,
    Ffma32ifmaxPipeRegRegImmReg,
    Ffma32ifmaxlitewPipeRegRegImm,
    Ffma32ifmaxlitewPipeRegRegImmReg,
    Ffma32ifmaiPipeRegRegImm,
    Ffma32ifmaiPipeRegRegImmReg,
    Ffma32ifmalighterPipeRegRegImmReg,
    Ffma32ifxuPipeRegRegImm,
    Ffma32ifxuPipeRegRegImmReg,
    FfmafauPipeRegRegConstReg,
    FfmafauPipeRegRegImmReg,
    FfmafauPipeRegRegRegConst,
    FfmafauPipeRegRegRegReg,
    FfmafmaxPipeRegRegConstReg,
    FfmafmaxPipeRegRegImmReg,
    FfmafmaxPipeRegRegRegConst,
    FfmafmaxPipeRegRegRegReg,
    FfmafmaxlitewPipeRegRegConstReg,
    FfmafmaxlitewPipeRegRegImmReg,
    FfmafmaxlitewPipeRegRegRegConst,
    FfmafmaxlitewPipeRegRegRegReg,
    FfmafmaiPipeRegRegConstReg,
    FfmafmaiPipeRegRegImmReg,
    FfmafmaiPipeRegRegRegConst,
    FfmafmaiPipeRegRegRegReg,
    FfmafmalighterPipeRegRegAddressImmReg,
    FfmafmalighterPipeRegRegConstReg,
    FfmafmalighterPipeRegRegImmReg,
    FfmafmalighterPipeRegRegRegAddressImm,
    FfmafmalighterPipeRegRegRegConst,
    FfmafmalighterPipeRegRegRegImm,
    FfmafmalighterPipeRegRegRegReg,
    FfmafxuPipeRegRegConstReg,
    FfmafxuPipeRegRegImmReg,
    FfmafxuPipeRegRegRegConst,
    FfmafxuPipeRegRegRegReg,
    FhaddRegRegReg,
    FhaddfmalighterPipeRegRegReg,
    FhfmaRegRegRegReg,
    FhfmafmalighterPipeRegRegRegReg,
    FloRegAddressImm,
    FloRegConst,
    FloRegImm,
    FloRegPredAddressImm,
    FloRegPredConst,
    FloRegPredImm,
    FloRegPredReg,
    FloRegReg,
    FlomioPipeRegAddressImm,
    FlomioPipeRegConst,
    FlomioPipeRegImm,
    FlomioPipeRegPredAddressImm,
    FlomioPipeRegPredConst,
    FlomioPipeRegPredImm,
    FlomioPipeRegPredReg,
    FlomioPipeRegReg,
    FloxluPipeRegConst,
    FloxluPipeRegImm,
    FloxluPipeRegReg,
    FmnmxRegPredRegAddressImmPred,
    FmnmxRegPredRegConstPred,
    FmnmxRegPredRegImmPred,
    FmnmxRegPredRegRegPred,
    FmnmxRegRegAddressImmPred,
    FmnmxRegRegConstPred,
    FmnmxRegRegImmPred,
    FmnmxRegRegRegPred,
    Fmnmx3RegRegImmRegPred,
    Fmnmx3RegRegRegRegPred,
    Fmnmx3intPipeRegRegImmRegPred,
    Fmnmx3intPipeRegRegRegRegPred,
    FmnmxfauPipeRegRegConstPred,
    FmnmxfauPipeRegRegImmPred,
    FmnmxfauPipeRegRegRegPred,
    FmnmxfmaxPipeRegRegConstPred,
    FmnmxfmaxPipeRegRegImmPred,
    FmnmxfmaxPipeRegRegRegPred,
    FmnmxfxuPipeRegRegConstPred,
    FmnmxfxuPipeRegRegImmPred,
    FmnmxfxuPipeRegRegRegPred,
    FmnmxintPipeRegPredRegAddressImmPred,
    FmnmxintPipeRegPredRegConstPred,
    FmnmxintPipeRegPredRegImmPred,
    FmnmxintPipeRegPredRegRegPred,
    FmnmxintPipeRegRegAddressImmPred,
    FmnmxintPipeRegRegConstPred,
    FmnmxintPipeRegRegImmPred,
    FmnmxintPipeRegRegRegPred,
    FmulRegRegAddressImm,
    FmulRegRegConst,
    FmulRegRegImm,
    FmulRegRegReg,
    Fmul2RegRegImm
  )
}
