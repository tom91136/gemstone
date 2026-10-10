// Generated SASS instruction definitions and checked operand constructors. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.sass
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import Instruction.*
@javax.annotation.processing.Generated(Array("sbt codegen"))
object InstructionDefinitions1 {
  object FcmpRegRegRegReg extends Definition("FcmpRegRegRegReg", "FCMP", Vec("FCMP")) {
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
  object FenceNoOperands extends Definition("FenceNoOperands", "FENCE", Vec("fence_", "fence_g_", "fence_t_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.FenceNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object FfmaRegRegAddressImmReg extends Definition("FfmaRegRegAddressImmReg", "FFMA", Vec("ffma__RCxR_RCxR")) {
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
  object FfmaRegRegConstReg
      extends Definition("FfmaRegRegConstReg", "FFMA", Vec("Const FFMA", "Const_FFMA", "ffma__RCR_RCR")) {
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
  object FfmaRegRegImmReg extends Definition("FfmaRegRegImmReg", "FFMA", Vec("Imm FFMA", "Imm_FFMA", "ffma__RIR_RIR")) {
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
  object FfmaRegRegRegAddressImm extends Definition("FfmaRegRegRegAddressImm", "FFMA", Vec("ffma__RRCx_RRCx")) {
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
  object FfmaRegRegRegConst
      extends Definition("FfmaRegRegRegConst", "FFMA", Vec("Const1 FFMA", "Const1_FFMA", "ffma__RRC_RRC")) {
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
  object FfmaRegRegRegImm extends Definition("FfmaRegRegRegImm", "FFMA", Vec("ffma__RRI_RRI")) {
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
  object FfmaRegRegRegReg
      extends Definition("FfmaRegRegRegReg", "FFMA", Vec("FFMA", "ffma__RRR_RRR", "ffma__RRU_RRU", "ffma__RUR_RUR")) {
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
  object Ffma2RegRegImmReg extends Definition("Ffma2RegRegImmReg", "FFMA2", Vec("ffma2_x_rc__RIR")) {
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
  object Ffma2RegRegRegImm extends Definition("Ffma2RegRegRegImm", "FFMA2", Vec("ffma2_rb_x__RRI")) {
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
  object Ffma32iRegRegImm extends Definition("Ffma32iRegRegImm", "FFMA32I", Vec("FFMA32I 2", "FFMA32I_2")) {
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
  object Ffma32iRegRegImmReg extends Definition("Ffma32iRegRegImmReg", "FFMA32I", Vec("FFMA32I", "ffma32i_")) {
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
  object FhaddRegRegReg extends Definition("FhaddRegRegReg", "FHADD", Vec("fhadd__RR", "fhadd__RU")) {
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
  object FhfmaRegRegRegReg
      extends Definition("FhfmaRegRegRegReg", "FHFMA", Vec("fhfma__RRR", "fhfma__RRU", "fhfma__RUR")) {
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
  object FloRegAddressImm extends Definition("FloRegAddressImm", "FLO", Vec("flo__RCxR_RCxR")) {
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
  object FloRegConst extends Definition("FloRegConst", "FLO", Vec("Const FLO", "Const_FLO", "flo__RCR_RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(sb)) =>
          Some(Instruction.FloRegConst(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object FloRegImm extends Definition("FloRegImm", "FLO", Vec("Imm FLO", "Imm_FLO", "flo__RuIR_RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.FloRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object FloRegPredAddressImm extends Definition("FloRegPredAddressImm", "FLO", Vec("flo__RCxR_RCxR")) {
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
  object FloRegPredConst extends Definition("FloRegPredConst", "FLO", Vec("flo__RCR_RCR")) {
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
  object FloRegPredImm extends Definition("FloRegPredImm", "FLO", Vec("flo__RuIR_RIR")) {
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
  object FloRegPredReg extends Definition("FloRegPredReg", "FLO", Vec("flo__RRR_RRR", "flo__RUR_RUR")) {
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
  object FloRegReg extends Definition("FloRegReg", "FLO", Vec("FLO", "flo__RRR_RRR", "flo__RUR_RUR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.FloRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object FmnmxRegPredRegAddressImmPred
      extends Definition("FmnmxRegPredRegAddressImmPred", "FMNMX", Vec("fmnmx_pred__RCxR_RCxR")) {
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
  object FmnmxRegPredRegConstPred extends Definition("FmnmxRegPredRegConstPred", "FMNMX", Vec("fmnmx_pred__RCR_RCR")) {
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
  object FmnmxRegPredRegImmPred extends Definition("FmnmxRegPredRegImmPred", "FMNMX", Vec("fmnmx_pred__RIR_RIR")) {
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
  object FmnmxRegPredRegRegPred
      extends Definition("FmnmxRegPredRegRegPred", "FMNMX", Vec("fmnmx_pred__RRR_RRR", "fmnmx_pred__RUR_RUR")) {
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
  object FmnmxRegRegAddressImmPred extends Definition("FmnmxRegRegAddressImmPred", "FMNMX", Vec("fmnmx__RCxR_RCxR")) {
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
  object FmnmxRegRegConstPred
      extends Definition("FmnmxRegRegConstPred", "FMNMX", Vec("Const FMNMX", "Const_FMNMX", "fmnmx__RCR_RCR")) {
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
  object FmnmxRegRegImmPred
      extends Definition("FmnmxRegRegImmPred", "FMNMX", Vec("Imm FMNMX", "Imm_FMNMX", "fmnmx__RIR_RIR")) {
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
  object FmnmxRegRegRegPred
      extends Definition("FmnmxRegRegRegPred", "FMNMX", Vec("FMNMX", "fmnmx__RRR_RRR", "fmnmx__RUR_RUR")) {
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
  object Fmnmx3RegRegImmRegPred extends Definition("Fmnmx3RegRegImmRegPred", "FMNMX3", Vec("fmnmx3__RIR_RIR")) {
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
  object Fmnmx3RegRegRegRegPred
      extends Definition("Fmnmx3RegRegRegRegPred", "FMNMX3", Vec("fmnmx3__RRR_RRR", "fmnmx3__RUR_RUR")) {
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
  object FmulRegRegAddressImm extends Definition("FmulRegRegAddressImm", "FMUL", Vec("fmul__RCxR_RCx")) {
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
  object FmulRegRegConst
      extends Definition("FmulRegRegConst", "FMUL", Vec("Const FMUL", "Const_FMUL", "fmul__RCR_RC")) {
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
  object FmulRegRegImm extends Definition("FmulRegRegImm", "FMUL", Vec("Imm FMUL", "Imm_FMUL", "fmul__RIR_RI")) {
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
  object FmulRegRegReg extends Definition("FmulRegRegReg", "FMUL", Vec("FMUL", "fmul__RRR_RR", "fmul__RUR_RU")) {
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
  object Fmul2RegRegImm extends Definition("Fmul2RegRegImm", "FMUL2", Vec("fmul2_imm__RIR")) {
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
  object Fmul2RegRegReg extends Definition("Fmul2RegRegReg", "FMUL2", Vec("fmul2_rb__RRR", "fmul2_urb__RUR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.Fmul2RegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object Fmul32iRegRegImm extends Definition("Fmul32iRegRegImm", "FMUL32I", Vec("FMUL32I", "fmul32i_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.Fmul32iRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object FootprintPredRegRegRegReg extends Definition(
        "FootprintPredRegRegRegReg",
        "FOOTPRINT",
        Vec("footprint_b_noConst_", "footprint_scr_b_noConst_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.FootprintPredRegRegRegReg(pu, rd2, dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object FootprintPredRegRegRegRegImmImm extends Definition(
        "FootprintPredRegRegRegRegImmImm",
        "FOOTPRINT",
        Vec("footprint_", "footprint_b_", "footprint_scr_", "footprint_scr_b_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(tsPtrIdx)
            ) =>
          Some(Instruction.FootprintPredRegRegRegRegImmImm(pu, rd2, dst, srcA, srcB, bank5, tsPtrIdx, modifiers, guard))
        case _ => None
      }
  }
  object FootprintPredRegRegRegRegImmImmImm extends Definition(
        "FootprintPredRegRegRegRegImmImmImm",
        "FOOTPRINT",
        Vec("footprint_b_tid_", "footprint_scr_b_tid_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(hdrtblbase6),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.FootprintPredRegRegRegRegImmImmImm(
            pu,
            rd2,
            dst,
            srcA,
            srcB,
            bank5,
            hdrtblbase6,
            tid8,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object FootprintPredRegRegRegRegReg extends Definition(
        "FootprintPredRegRegRegRegReg",
        "FOOTPRINT",
        Vec("footprint_b_urc_", "footprint_scr_b_urc_", "footprint_scr_uniform_", "footprint_uniform_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.FootprintPredRegRegRegRegReg(pu, rd2, dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FootprintPredRegRegRegRegRegImm extends Definition(
        "FootprintPredRegRegRegRegRegImm",
        "FOOTPRINT",
        Vec("footprint_scr_uniform_", "footprint_uniform_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(tid8)
            ) =>
          Some(Instruction.FootprintPredRegRegRegRegRegImm(pu, rd2, dst, srcA, srcB, srcC, tid8, modifiers, guard))
        case _ => None
      }
  }
  object FootprintRegRegRegReg extends Definition(
        "FootprintRegRegRegReg",
        "FOOTPRINT",
        Vec("footprint_b_noConst_", "footprint_scr_b_noConst_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.FootprintRegRegRegReg(rd2, dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object FootprintRegRegRegRegImmImm extends Definition(
        "FootprintRegRegRegRegImmImm",
        "FOOTPRINT",
        Vec("footprint_", "footprint_b_", "footprint_scr_", "footprint_scr_b_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(tsPtrIdx)
            ) => Some(Instruction.FootprintRegRegRegRegImmImm(rd2, dst, srcA, srcB, bank5, tsPtrIdx, modifiers, guard))
        case _ => None
      }
  }
  object FootprintRegRegRegRegImmImmImm extends Definition(
        "FootprintRegRegRegRegImmImmImm",
        "FOOTPRINT",
        Vec("footprint_b_tid_", "footprint_scr_b_tid_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(hdrtblbase6),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.FootprintRegRegRegRegImmImmImm(
            rd2,
            dst,
            srcA,
            srcB,
            bank5,
            hdrtblbase6,
            tid8,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object FootprintRegRegRegRegReg extends Definition(
        "FootprintRegRegRegRegReg",
        "FOOTPRINT",
        Vec("footprint_b_urc_", "footprint_scr_b_urc_", "footprint_scr_uniform_", "footprint_uniform_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.FootprintRegRegRegRegReg(rd2, dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FootprintRegRegRegRegRegImm extends Definition(
        "FootprintRegRegRegRegRegImm",
        "FOOTPRINT",
        Vec("footprint_scr_uniform_", "footprint_uniform_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.FootprintRegRegRegRegRegImm(rd2, dst, srcA, srcB, srcC, tid8, modifiers, guard))
        case _ => None
      }
  }
  object FrndRegAddressImm extends Definition(
        "FrndRegAddressImm",
        "FRND",
        Vec(
          "frnd__bf16_CXb",
          "frnd__f16_CXb",
          "frnd__f32_CXb",
          "frnd__f64_CXb",
          "frnd_swap__bf16_CXb",
          "frnd_swap__f16_CXb",
          "frnd_swap__f32_CXb",
          "frnd_swap__f64_CXb"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.FrndRegAddressImm(dst, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object FrndRegConst extends Definition(
        "FrndRegConst",
        "FRND",
        Vec(
          "frnd__bf16_C",
          "frnd__f16_C",
          "frnd__f32_C",
          "frnd__f64_C",
          "frnd_swap__bf16_C",
          "frnd_swap__f16_C",
          "frnd_swap__f32_C",
          "frnd_swap__f64_C"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(sb)) =>
          Some(Instruction.FrndRegConst(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object FrndRegImm extends Definition(
        "FrndRegImm",
        "FRND",
        Vec(
          "frnd__bf16_I",
          "frnd__f16_I",
          "frnd__f32_I",
          "frnd__f64_I",
          "frnd_swap__bf16_I",
          "frnd_swap__f16_I",
          "frnd_swap__f32_I",
          "frnd_swap__f64_I"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.FrndRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object FrndRegReg extends Definition(
        "FrndRegReg",
        "FRND",
        Vec(
          "frnd__bf16_R",
          "frnd__bf16_URb",
          "frnd__f16_R",
          "frnd__f16_URb",
          "frnd__f32_R",
          "frnd__f32_URb",
          "frnd__f64_R",
          "frnd__f64_URb",
          "frnd_swap__bf16_R",
          "frnd_swap__bf16_URb",
          "frnd_swap__f16_R",
          "frnd_swap__f16_URb",
          "frnd_swap__f32_R",
          "frnd_swap__f32_URb",
          "frnd_swap__f64_R",
          "frnd_swap__f64_URb"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.FrndRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object FselRegRegAddressImmPred extends Definition("FselRegRegAddressImmPred", "FSEL", Vec("fsel__RCxR_RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.FselRegRegAddressImmPred(dst, srcA, srcB, sb_offset, pp, modifiers, guard))
        case _ => None
      }
  }
  object FselRegRegConstPred extends Definition("FselRegRegConstPred", "FSEL", Vec("fsel__RCR_RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.FselRegRegConstPred(dst, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object FselRegRegImmPred extends Definition("FselRegRegImmPred", "FSEL", Vec("fsel__RIR_RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.FselRegRegImmPred(dst, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object FselRegRegRegPred extends Definition("FselRegRegRegPred", "FSEL", Vec("fsel__RRR_RRR", "fsel__RUR_RUR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.FselRegRegRegPred(dst, srcA, srcB, pp, modifiers, guard))
        case _ => None
      }
  }
  object FsetRegRegAddressImm extends Definition("FsetRegRegAddressImm", "FSET", Vec("fset_simple__RCxR_RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.FsetRegRegAddressImm(dst, srcA, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object FsetRegRegAddressImmPred extends Definition("FsetRegRegAddressImmPred", "FSET", Vec("fset__RCxR_RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.FsetRegRegAddressImmPred(dst, srcA, srcB, sb_offset, pp, modifiers, guard))
        case _ => None
      }
  }
  object FsetRegRegConst extends Definition(
        "FsetRegRegConst",
        "FSET",
        Vec("NoBop Const FSET", "NoBop_Const_FSET", "fset_simple__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb)
            ) => Some(Instruction.FsetRegRegConst(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object FsetRegRegConstPred
      extends Definition("FsetRegRegConstPred", "FSET", Vec("Const FSET", "Const_FSET", "fset__RCR_RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.FsetRegRegConstPred(dst, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object FsetRegRegImm
      extends Definition("FsetRegRegImm", "FSET", Vec("NoBop Imm FSET", "NoBop_Imm_FSET", "fset_simple__RIR_RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.FsetRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object FsetRegRegImmPred
      extends Definition("FsetRegRegImmPred", "FSET", Vec("Imm FSET", "Imm_FSET", "fset__RIR_RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.FsetRegRegImmPred(dst, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object FsetRegRegReg extends Definition(
        "FsetRegRegReg",
        "FSET",
        Vec("NoBop FSET", "NoBop_FSET", "fset_simple__RRR_RRR", "fset_simple__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.FsetRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object FsetRegRegRegPred
      extends Definition("FsetRegRegRegPred", "FSET", Vec("FSET", "fset__RRR_RRR", "fset__RUR_RUR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.FsetRegRegRegPred(dst, srcA, srcB, pa, modifiers, guard))
        case _ => None
      }
  }
  object FsetpPredPredRegAddressImmPred
      extends Definition("FsetpPredPredRegAddressImmPred", "FSETP", Vec("fsetp__RCxR_RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.FsetpPredPredRegAddressImmPred(pu, pv, srcA, srcB, sb_offset, pp, modifiers, guard))
        case _ => None
      }
  }
  object FsetpPredPredRegConstPred extends Definition(
        "FsetpPredPredRegConstPred",
        "FSETP",
        Vec("Const FSETP", "Const_FSETP", "fsetp__RCR_RC", "fsetp__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.FsetpPredPredRegConstPred(dstPredicate, nPd, srcA, srcConst, pa, modifiers, guard))
        case _ => None
      }
  }
  object FsetpPredPredRegImmPred extends Definition(
        "FsetpPredPredRegImmPred",
        "FSETP",
        Vec("Imm FSETP", "Imm_FSETP", "fsetp__RIR_RI", "fsetp__RIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.FsetpPredPredRegImmPred(dstPredicate, nPd, srcA, uImm, pa, modifiers, guard))
        case _ => None
      }
  }
  object FsetpPredPredRegRegPred extends Definition(
        "FsetpPredPredRegRegPred",
        "FSETP",
        Vec("FSETP", "fsetp__RRR_RR", "fsetp__RRR_RRR", "fsetp__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.FsetpPredPredRegRegPred(dstPredicate, nPd, srcA, srcB, pa, modifiers, guard))
        case _ => None
      }
  }
  object FsetpPredRegAddressImm extends Definition("FsetpPredRegAddressImm", "FSETP", Vec("fsetp_simple__RCxR_RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.FsetpPredRegAddressImm(pu, srcA, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object FsetpPredRegConst extends Definition(
        "FsetpPredRegConst",
        "FSETP",
        Vec("NoBop Const FSETP", "NoBop_Const_FSETP", "fsetp_simple__RCR_RC", "fsetp_simple__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.FsetpPredRegConst(dstPredicate, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object FsetpPredRegImm extends Definition(
        "FsetpPredRegImm",
        "FSETP",
        Vec("NoBop Imm FSETP", "NoBop_Imm_FSETP", "fsetp_simple__RIR_RI", "fsetp_simple__RIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.FsetpPredRegImm(dstPredicate, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object FsetpPredRegReg extends Definition(
        "FsetpPredRegReg",
        "FSETP",
        Vec("NoBop FSETP", "NoBop_FSETP", "fsetp_simple__RRR_RR", "fsetp_simple__RRR_RRR", "fsetp_simple__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.FsetpPredRegReg(dstPredicate, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object FswzRegRegReg extends Definition("FswzRegRegReg", "FSWZ", Vec("FSWZ")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.FswzRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object FswzaddRegRegReg extends Definition("FswzaddRegRegReg", "FSWZADD", Vec("FSWZADD", "fswzadd_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.FswzaddRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object GatherRegRegRegRegImm extends Definition("GatherRegRegRegRegImm", "GATHER", Vec("gather_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(mdidx)
            ) => Some(Instruction.GatherRegRegRegRegImm(dst, srcA, srcB, srcC, mdidx, modifiers, guard))
        case _ => None
      }
  }
  object GatherRegRegRegRegImmImm extends Definition("GatherRegRegRegRegImmImm", "GATHER", Vec("gather_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(mdidx),
              InstructionOperand.Immediate(dstbyte)
            ) => Some(Instruction.GatherRegRegRegRegImmImm(dst, srcA, srcB, srcC, mdidx, dstbyte, modifiers, guard))
        case _ => None
      }
  }
  object GatherRegRegRegRegImmImmImm extends Definition("GatherRegRegRegRegImmImmImm", "GATHER", Vec("gather_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(mdidx),
              InstructionOperand.Immediate(dstbyte),
              InstructionOperand.Immediate(srchalf)
            ) => Some(Instruction.GatherRegRegRegRegImmImmImm(
            dst,
            srcA,
            srcB,
            srcC,
            mdidx,
            dstbyte,
            srchalf,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object GetcrsptrReg extends Definition("GetcrsptrReg", "GETCRSPTR", Vec("GETCRSPTR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst)) => Some(Instruction.GetcrsptrReg(dst, modifiers, guard))
        case _ => None
      }
  }
  object GetlmembaseReg extends Definition("GetlmembaseReg", "GETLMEMBASE", Vec("GETLMEMBASE", "getlmembase_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst)) => Some(Instruction.GetlmembaseReg(dst, modifiers, guard))
        case _ => None
      }
  }
  object Hadd2NoOperands extends Definition("Hadd2NoOperands", "HADD2", Vec("hadd2__v2")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.Hadd2NoOperands(modifiers, guard))
        case _ => None
      }
  }
  object Hadd2Const extends Definition("Hadd2Const", "HADD2", Vec("hadd2__v0")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(sb)) => Some(Instruction.Hadd2Const(sb, modifiers, guard))
        case _ => None
      }
  }
  object Hadd2ImmImm extends Definition("Hadd2ImmImm", "HADD2", Vec("hadd2__v1")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb), InstructionOperand.Immediate(sb2)) =>
          Some(Instruction.Hadd2ImmImm(sb, sb2, modifiers, guard))
        case _ => None
      }
  }
  object Hadd2RegRegAddressImm
      extends Definition("Hadd2RegRegAddressImm", "HADD2", Vec("hadd2_F32__RCx", "hadd2__RCx")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcC),
              InstructionOperand.Immediate(sc_offset)
            ) => Some(Instruction.Hadd2RegRegAddressImm(dst, srcA, srcC, sc_offset, modifiers, guard))
        case _ => None
      }
  }
  object Hadd2RegRegConst extends Definition("Hadd2RegRegConst", "HADD2", Vec("hadd2_F32__RC", "hadd2__RC")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sc)
            ) => Some(Instruction.Hadd2RegRegConst(dst, srcA, sc, modifiers, guard))
        case _ => None
      }
  }
  object Hadd2RegRegImm extends Definition("Hadd2RegRegImm", "HADD2", Vec("hadd2_F32__RI")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.Hadd2RegRegImm(dst, srcA, sc, modifiers, guard))
        case _ => None
      }
  }
  object Hadd2RegRegImmImm
      extends Definition("Hadd2RegRegImmImm", "HADD2", Vec("hadd2_F32i_", "hadd2__RI", "hadd2_fixed__RI")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.Hadd2RegRegImmImm(dst, srcA, sb, sc, modifiers, guard))
        case _ => None
      }
  }
  object Hadd2RegRegReg extends Definition(
        "Hadd2RegRegReg",
        "HADD2",
        Vec(
          "hadd2_F32__RR",
          "hadd2_F32__RU",
          "hadd2_F32_fixed__RR",
          "hadd2_F32_fixed__RU",
          "hadd2__RR",
          "hadd2__RU",
          "hadd2_fixed__RR",
          "hadd2_fixed__RU"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Hadd2RegRegReg(dst, srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Hadd232iImmImm extends Definition("Hadd232iImmImm", "HADD2_32I", Vec("hadd2_32i_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(imm), InstructionOperand.Immediate(imm2)) =>
          Some(Instruction.Hadd232iImmImm(imm, imm2, modifiers, guard))
        case _ => None
      }
  }
  object Hadd232iRegRegImmImm
      extends Definition("Hadd232iRegRegImmImm", "HADD2_32I", Vec("hadd2_32i_", "hadd2_32i_fixed_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sc),
              InstructionOperand.Immediate(sc1)
            ) => Some(Instruction.Hadd232iRegRegImmImm(dst, srcA, sc, sc1, modifiers, guard))
        case _ => None
      }
  }
  object Hfma2NoOperands extends Definition("Hfma2NoOperands", "HFMA2", Vec("hfma2__v2")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.Hfma2NoOperands(modifiers, guard))
        case _ => None
      }
  }
  object Hfma2Const extends Definition("Hfma2Const", "HFMA2", Vec("hfma2__v0")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(sb)) => Some(Instruction.Hfma2Const(sb, modifiers, guard))
        case _ => None
      }
  }
  object Hfma2ImmImm extends Definition("Hfma2ImmImm", "HFMA2", Vec("hfma2__v1")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb), InstructionOperand.Immediate(sb2)) =>
          Some(Instruction.Hfma2ImmImm(sb, sb2, modifiers, guard))
        case _ => None
      }
  }
  object Hfma2RegRegAddressImmReg extends Definition(
        "Hfma2RegRegAddressImmReg",
        "HFMA2",
        Vec(
          "hfma2__RCxR",
          "hfma2_mma__RCxR",
          "hfma2_mma__RCxR_norelu",
          "hfma2_mma__RCxR_relu",
          "hfma2_mma_relu__RCxR",
          "hfma2_relu__RCxR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Hfma2RegRegAddressImmReg(dst, srcA, srcB, sb_offset, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Hfma2RegRegAddressImmRegPred extends Definition(
        "Hfma2RegRegAddressImmRegPred",
        "HFMA2",
        Vec("hfma2_mma__RCxR_relu", "hfma2_mma_relu__RCxR", "hfma2_relu__RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Hfma2RegRegAddressImmRegPred(dst, srcA, srcB, sb_offset, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object Hfma2RegRegConstReg extends Definition(
        "Hfma2RegRegConstReg",
        "HFMA2",
        Vec(
          "hfma2__RCR",
          "hfma2_mma__RCR",
          "hfma2_mma__RCR_norelu",
          "hfma2_mma__RCR_relu",
          "hfma2_mma_relu__RCR",
          "hfma2_relu__RCR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Hfma2RegRegConstReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Hfma2RegRegConstRegPred extends Definition(
        "Hfma2RegRegConstRegPred",
        "HFMA2",
        Vec("hfma2_mma__RCR_relu", "hfma2_mma_relu__RCR", "hfma2_relu__RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Hfma2RegRegConstRegPred(dst, srcA, sb, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object Hfma2RegRegImmImmReg extends Definition(
        "Hfma2RegRegImmImmReg",
        "HFMA2",
        Vec(
          "hfma2__RIR",
          "hfma2_fixed__RIR",
          "hfma2_mma__RIR",
          "hfma2_mma__RIR_norelu",
          "hfma2_mma__RIR_relu",
          "hfma2_mma_relu__RIR",
          "hfma2_relu__RIR",
          "hfma2_relu_fixed__RIR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Immediate(sb1),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Hfma2RegRegImmImmReg(dst, srcA, sb, sb1, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Hfma2RegRegImmImmRegPred extends Definition(
        "Hfma2RegRegImmImmRegPred",
        "HFMA2",
        Vec("hfma2_mma__RIR_relu", "hfma2_mma_relu__RIR", "hfma2_relu__RIR", "hfma2_relu_fixed__RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Immediate(sb1),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Hfma2RegRegImmImmRegPred(dst, srcA, sb, sb1, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object Hfma2RegRegRegAddressImm extends Definition(
        "Hfma2RegRegRegAddressImm",
        "HFMA2",
        Vec(
          "hfma2__RRCx",
          "hfma2_mma__RRCx",
          "hfma2_mma__RRCx_norelu",
          "hfma2_mma__RRCx_relu",
          "hfma2_mma_relu__RRCx",
          "hfma2_relu__RRCx"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Address(srcC),
              InstructionOperand.Immediate(sc_offset)
            ) => Some(Instruction.Hfma2RegRegRegAddressImm(dst, srcA, srcB, srcC, sc_offset, modifiers, guard))
        case _ => None
      }
  }
  object Hfma2RegRegRegAddressImmPred extends Definition(
        "Hfma2RegRegRegAddressImmPred",
        "HFMA2",
        Vec("hfma2_mma__RRCx_relu", "hfma2_mma_relu__RRCx", "hfma2_relu__RRCx")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Address(srcC),
              InstructionOperand.Immediate(sc_offset),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Hfma2RegRegRegAddressImmPred(dst, srcA, srcB, srcC, sc_offset, pp, modifiers, guard))
        case _ => None
      }
  }
  object Hfma2RegRegRegConst extends Definition(
        "Hfma2RegRegRegConst",
        "HFMA2",
        Vec(
          "HFMA2_CCST",
          "hfma2__RRC",
          "hfma2_mma__RRC",
          "hfma2_mma__RRC_norelu",
          "hfma2_mma__RRC_relu",
          "hfma2_mma_relu__RRC",
          "hfma2_relu__RRC"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(sc)
            ) => Some(Instruction.Hfma2RegRegRegConst(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object Hfma2RegRegRegConstPred extends Definition(
        "Hfma2RegRegRegConstPred",
        "HFMA2",
        Vec("hfma2_mma__RRC_relu", "hfma2_mma_relu__RRC", "hfma2_relu__RRC")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(sc),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Hfma2RegRegRegConstPred(dst, srcA, srcB, sc, pp, modifiers, guard))
        case _ => None
      }
  }
  object Hfma2RegRegRegImmImm extends Definition(
        "Hfma2RegRegRegImmImm",
        "HFMA2",
        Vec(
          "hfma2__RRI",
          "hfma2_fixed__RRI",
          "hfma2_mma__RRI",
          "hfma2_mma__RRI_norelu",
          "hfma2_mma__RRI_relu",
          "hfma2_mma_relu__RRI",
          "hfma2_relu__RRI",
          "hfma2_relu_fixed__RRI"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc),
              InstructionOperand.Immediate(sc1)
            ) => Some(Instruction.Hfma2RegRegRegImmImm(dst, srcA, srcB, sc, sc1, modifiers, guard))
        case _ => None
      }
  }
  object Hfma2RegRegRegImmImmPred extends Definition(
        "Hfma2RegRegRegImmImmPred",
        "HFMA2",
        Vec("hfma2_mma__RRI_relu", "hfma2_mma_relu__RRI", "hfma2_relu__RRI", "hfma2_relu_fixed__RRI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc),
              InstructionOperand.Immediate(sc1),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Hfma2RegRegRegImmImmPred(dst, srcA, srcB, sc, sc1, pp, modifiers, guard))
        case _ => None
      }
  }
  object Hfma2RegRegRegReg extends Definition(
        "Hfma2RegRegRegReg",
        "HFMA2",
        Vec(
          "hfma2__RRR",
          "hfma2__RRU",
          "hfma2__RUR",
          "hfma2_fixed__RRR",
          "hfma2_fixed__RRU",
          "hfma2_fixed__RUR",
          "hfma2_mma__RRR",
          "hfma2_mma__RRR_norelu",
          "hfma2_mma__RRR_relu",
          "hfma2_mma__RRU",
          "hfma2_mma__RRU_norelu",
          "hfma2_mma__RRU_relu",
          "hfma2_mma__RUR",
          "hfma2_mma__RUR_norelu",
          "hfma2_mma__RUR_relu",
          "hfma2_mma_relu__RRR",
          "hfma2_mma_relu__RRU",
          "hfma2_mma_relu__RUR",
          "hfma2_relu__RRR",
          "hfma2_relu__RRU",
          "hfma2_relu__RUR",
          "hfma2_relu_fixed__RRR",
          "hfma2_relu_fixed__RRU",
          "hfma2_relu_fixed__RUR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Hfma2RegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Hfma2RegRegRegRegPred extends Definition(
        "Hfma2RegRegRegRegPred",
        "HFMA2",
        Vec(
          "hfma2_mma__RRR_relu",
          "hfma2_mma__RRU_relu",
          "hfma2_mma__RUR_relu",
          "hfma2_mma_relu__RRR",
          "hfma2_mma_relu__RRU",
          "hfma2_mma_relu__RUR",
          "hfma2_relu__RRR",
          "hfma2_relu__RRU",
          "hfma2_relu__RUR",
          "hfma2_relu_fixed__RRR",
          "hfma2_relu_fixed__RRU",
          "hfma2_relu_fixed__RUR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Hfma2RegRegRegRegPred(dst, srcA, srcB, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object Hfma232iRegRegImmImm extends Definition("Hfma232iRegRegImmImm", "HFMA2_32I", Vec("HFMA2_32I_2")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(fImm),
              InstructionOperand.Immediate(fImm1)
            ) => Some(Instruction.Hfma232iRegRegImmImm(dst, srcA, fImm, fImm1, modifiers, guard))
        case _ => None
      }
  }
  object Hfma232iRegRegImmImmReg
      extends Definition("Hfma232iRegRegImmImmReg", "HFMA2_32I", Vec("HFMA2_32I", "hfma2_32i_", "hfma2_32i_fixed_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Immediate(sb1),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Hfma232iRegRegImmImmReg(dst, srcA, sb, sb1, srcC, modifiers, guard))
        case _ => None
      }
  }
  object HgmmaRegAddressReg extends Definition("HgmmaRegAddressReg", "HGMMA", Vec("hgmma_URa_Rc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.HgmmaRegAddressReg(dst, srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object HgmmaRegAddressRegPred extends Definition("HgmmaRegAddressRegPred", "HGMMA", Vec("hgmma_URa_Rc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.HgmmaRegAddressRegPred(dst, srcA, srcC, uPp, modifiers, guard))
        case _ => None
      }
  }
  object HgmmaRegAddressRegPredRegImm
      extends Definition("HgmmaRegAddressRegPredRegImm", "HGMMA", Vec("hgmma_sparse_URa_Rc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Register(re),
              InstructionOperand.Immediate(id)
            ) => Some(Instruction.HgmmaRegAddressRegPredRegImm(dst, srcA, srcC, uPp, re, id, modifiers, guard))
        case _ => None
      }
  }
  object HgmmaRegAddressRegReg extends Definition("HgmmaRegAddressRegReg", "HGMMA", Vec("hgmma_URa_Rb_Rc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.HgmmaRegAddressRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object HgmmaRegAddressRegRegImm extends Definition("HgmmaRegAddressRegRegImm", "HGMMA", Vec("hgmma_sparse_URa_Rc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(re),
              InstructionOperand.Immediate(id)
            ) => Some(Instruction.HgmmaRegAddressRegRegImm(dst, srcA, srcC, re, id, modifiers, guard))
        case _ => None
      }
  }
  object HgmmaRegAddressRegRegPred extends Definition("HgmmaRegAddressRegRegPred", "HGMMA", Vec("hgmma_URa_Rb_Rc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.HgmmaRegAddressRegRegPred(dst, srcA, srcB, srcC, uPp, modifiers, guard))
        case _ => None
      }
  }
  object HgmmaRegAddressRegRegPredRegImm
      extends Definition("HgmmaRegAddressRegRegPredRegImm", "HGMMA", Vec("hgmma_sparse_URa_Rb_Rc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Register(re),
              InstructionOperand.Immediate(id)
            ) => Some(Instruction.HgmmaRegAddressRegRegPredRegImm(dst, srcA, srcB, srcC, uPp, re, id, modifiers, guard))
        case _ => None
      }
  }
  object HgmmaRegAddressRegRegRegImm
      extends Definition("HgmmaRegAddressRegRegRegImm", "HGMMA", Vec("hgmma_sparse_URa_Rb_Rc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(re),
              InstructionOperand.Immediate(id)
            ) => Some(Instruction.HgmmaRegAddressRegRegRegImm(dst, srcA, srcB, srcC, re, id, modifiers, guard))
        case _ => None
      }
  }
  object HgmmaRegRegAddressReg extends Definition("HgmmaRegRegAddressReg", "HGMMA", Vec("hgmma_Ra_URb_Rc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.HgmmaRegRegAddressReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object HgmmaRegRegAddressRegPred extends Definition("HgmmaRegRegAddressRegPred", "HGMMA", Vec("hgmma_Ra_URb_Rc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.HgmmaRegRegAddressRegPred(dst, srcA, srcB, srcC, uPp, modifiers, guard))
        case _ => None
      }
  }
  object HgmmaRegRegAddressRegPredRegImm
      extends Definition("HgmmaRegRegAddressRegPredRegImm", "HGMMA", Vec("hgmma_sparse_Ra_URb_Rc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Register(re),
              InstructionOperand.Immediate(id)
            ) => Some(Instruction.HgmmaRegRegAddressRegPredRegImm(dst, srcA, srcB, srcC, uPp, re, id, modifiers, guard))
        case _ => None
      }
  }
  object HgmmaRegRegAddressRegRegImm
      extends Definition("HgmmaRegRegAddressRegRegImm", "HGMMA", Vec("hgmma_sparse_Ra_URb_Rc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(re),
              InstructionOperand.Immediate(id)
            ) => Some(Instruction.HgmmaRegRegAddressRegRegImm(dst, srcA, srcB, srcC, re, id, modifiers, guard))
        case _ => None
      }
  }
  object HmmaAddressRegRegAddress extends Definition("HmmaAddressRegRegAddress", "HMMA", Vec("hmma_x8_indexedRF_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Address(srcC)
            ) => Some(Instruction.HmmaAddressRegRegAddress(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object HmmaAddressRegRegAddressPred
      extends Definition("HmmaAddressRegRegAddressPred", "HMMA", Vec("hmma_x8_indexedRF_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Address(srcC),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.HmmaAddressRegRegAddressPred(dst, srcA, srcB, srcC, uPp, modifiers, guard))
        case _ => None
      }
  }
  object HmmaAddressRegRegAddressPredRegImm
      extends Definition("HmmaAddressRegRegAddressPredRegImm", "HMMA", Vec("hmma_sparse_indexedRF_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Address(srcC),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Register(re),
              InstructionOperand.Immediate(id)
            ) =>
          Some(Instruction.HmmaAddressRegRegAddressPredRegImm(dst, srcA, srcB, srcC, uPp, re, id, modifiers, guard))
        case _ => None
      }
  }
  object HmmaAddressRegRegAddressRegImm
      extends Definition("HmmaAddressRegRegAddressRegImm", "HMMA", Vec("hmma_sparse_indexedRF_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Address(srcC),
              InstructionOperand.Register(re),
              InstructionOperand.Immediate(id)
            ) => Some(Instruction.HmmaAddressRegRegAddressRegImm(dst, srcA, srcB, srcC, re, id, modifiers, guard))
        case _ => None
      }
  }
  object HmmaRegRegRegReg extends Definition(
        "HmmaRegRegRegReg",
        "HMMA",
        Vec(
          "hmma_848__444_f16_f16",
          "hmma_848__444_f32",
          "hmma_848__884_f16_f16",
          "hmma_848__884_f32",
          "hmma_848_old_row_col__444_f16_f16",
          "hmma_848_old_row_col__444_f32",
          "hmma_848_old_row_col__884_f16_f16",
          "hmma_848_old_row_col__884_f32",
          "hmma__444_f16_f16",
          "hmma__444_f32",
          "hmma__884_f16_f16",
          "hmma__884_f32",
          "hmma_old_row_col__444_f16_f16",
          "hmma_old_row_col__444_f32",
          "hmma_old_row_col__884_f16_f16",
          "hmma_old_row_col__884_f32",
          "hmma_x8_"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.HmmaRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object HmmaRegRegRegRegPred extends Definition("HmmaRegRegRegRegPred", "HMMA", Vec("hmma_x8_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.HmmaRegRegRegRegPred(dst, srcA, srcB, srcC, uPp, modifiers, guard))
        case _ => None
      }
  }
  object HmmaRegRegRegRegPredRegImm extends Definition("HmmaRegRegRegRegPredRegImm", "HMMA", Vec("hmma_sparse_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Register(re),
              InstructionOperand.Immediate(id)
            ) => Some(Instruction.HmmaRegRegRegRegPredRegImm(dst, srcA, srcB, srcC, uPp, re, id, modifiers, guard))
        case _ => None
      }
  }
  object HmmaRegRegRegRegRegImm extends Definition("HmmaRegRegRegRegRegImm", "HMMA", Vec("hmma_sparse_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(re),
              InstructionOperand.Immediate(id)
            ) => Some(Instruction.HmmaRegRegRegRegRegImm(dst, srcA, srcB, srcC, re, id, modifiers, guard))
        case _ => None
      }
  }
  object Hmnmx2RegPredPredRegAddressImmPred extends Definition(
        "Hmnmx2RegPredPredRegAddressImmPred",
        "HMNMX2",
        Vec("hmnmx2_pred__RCxR", "hmnmx2_pred_fixed__RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Predicate(pp)
            ) =>
          Some(Instruction.Hmnmx2RegPredPredRegAddressImmPred(dst, pu, pv, srcA, srcB, sb_offset, pp, modifiers, guard))
        case _ => None
      }
  }
  object Hmnmx2RegPredPredRegConstPred
      extends Definition("Hmnmx2RegPredPredRegConstPred", "HMNMX2", Vec("hmnmx2_pred__RCR", "hmnmx2_pred_fixed__RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Hmnmx2RegPredPredRegConstPred(dst, pu, pv, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object Hmnmx2RegPredPredRegImmImmPred extends Definition(
        "Hmnmx2RegPredPredRegImmImmPred",
        "HMNMX2",
        Vec("hmnmx2_pred__RIR", "hmnmx2_pred_fixed__RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Immediate(sb1),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Hmnmx2RegPredPredRegImmImmPred(dst, pu, pv, srcA, sb, sb1, pp, modifiers, guard))
        case _ => None
      }
  }
  object Hmnmx2RegPredPredRegRegPred extends Definition(
        "Hmnmx2RegPredPredRegRegPred",
        "HMNMX2",
        Vec("hmnmx2_pred__RRR", "hmnmx2_pred__RUR", "hmnmx2_pred_fixed__RRR", "hmnmx2_pred_fixed__RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Hmnmx2RegPredPredRegRegPred(dst, pu, pv, srcA, srcB, pp, modifiers, guard))
        case _ => None
      }
  }
  object Hmnmx2RegRegAddressImmPred
      extends Definition("Hmnmx2RegRegAddressImmPred", "HMNMX2", Vec("hmnmx2__RCxR", "hmnmx2_fixed__RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Hmnmx2RegRegAddressImmPred(dst, srcA, srcB, sb_offset, pp, modifiers, guard))
        case _ => None
      }
  }
  object Hmnmx2RegRegConstPred
      extends Definition("Hmnmx2RegRegConstPred", "HMNMX2", Vec("hmnmx2__RCR", "hmnmx2_fixed__RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Hmnmx2RegRegConstPred(dst, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object Hmnmx2RegRegImmImmPred
      extends Definition("Hmnmx2RegRegImmImmPred", "HMNMX2", Vec("hmnmx2__RIR", "hmnmx2_fixed__RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Immediate(sb1),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Hmnmx2RegRegImmImmPred(dst, srcA, sb, sb1, pp, modifiers, guard))
        case _ => None
      }
  }
  object Hmnmx2RegRegRegPred extends Definition(
        "Hmnmx2RegRegRegPred",
        "HMNMX2",
        Vec("hmnmx2__RRR", "hmnmx2__RUR", "hmnmx2_fixed__RRR", "hmnmx2_fixed__RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Hmnmx2RegRegRegPred(dst, srcA, srcB, pp, modifiers, guard))
        case _ => None
      }
  }
  object Hmul2NoOperands extends Definition("Hmul2NoOperands", "HMUL2", Vec("hmul2__v2")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.Hmul2NoOperands(modifiers, guard))
        case _ => None
      }
  }
  object Hmul2Const extends Definition("Hmul2Const", "HMUL2", Vec("hmul2__v0")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(sb)) => Some(Instruction.Hmul2Const(sb, modifiers, guard))
        case _ => None
      }
  }
  object Hmul2ImmImm extends Definition("Hmul2ImmImm", "HMUL2", Vec("hmul2__v1")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb), InstructionOperand.Immediate(sb2)) =>
          Some(Instruction.Hmul2ImmImm(sb, sb2, modifiers, guard))
        case _ => None
      }
  }
  object Hmul2RegRegAddressImm extends Definition("Hmul2RegRegAddressImm", "HMUL2", Vec("hmul2__RCx")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.Hmul2RegRegAddressImm(dst, srcA, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object Hmul2RegRegConst extends Definition("Hmul2RegRegConst", "HMUL2", Vec("hmul2__RC")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb)
            ) => Some(Instruction.Hmul2RegRegConst(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object Hmul2RegRegImmImm extends Definition("Hmul2RegRegImmImm", "HMUL2", Vec("hmul2__RI", "hmul2_fixed__RI")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Immediate(sb1)
            ) => Some(Instruction.Hmul2RegRegImmImm(dst, srcA, sb, sb1, modifiers, guard))
        case _ => None
      }
  }
  object Hmul2RegRegReg extends Definition(
        "Hmul2RegRegReg",
        "HMUL2",
        Vec("hmul2__RR", "hmul2__RU", "hmul2_fixed__RR", "hmul2_fixed__RU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.Hmul2RegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object Hmul232iImmImm extends Definition("Hmul232iImmImm", "HMUL2_32I", Vec("hmul2_32i_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(imm), InstructionOperand.Immediate(imm2)) =>
          Some(Instruction.Hmul232iImmImm(imm, imm2, modifiers, guard))
        case _ => None
      }
  }
  object Hmul232iRegRegImmImm
      extends Definition("Hmul232iRegRegImmImm", "HMUL2_32I", Vec("hmul2_32i_", "hmul2_32i_fixed_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Immediate(sb1)
            ) => Some(Instruction.Hmul232iRegRegImmImm(dst, srcA, sb, sb1, modifiers, guard))
        case _ => None
      }
  }
  object Hset2NoOperands extends Definition("Hset2NoOperands", "HSET2", Vec("hset2_bop__v2")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.Hset2NoOperands(modifiers, guard))
        case _ => None
      }
  }
  object Hset2Const extends Definition("Hset2Const", "HSET2", Vec("hset2_bop__v0")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(sb)) => Some(Instruction.Hset2Const(sb, modifiers, guard))
        case _ => None
      }
  }
  object Hset2ConstPred extends Definition("Hset2ConstPred", "HSET2", Vec("hset2__v0")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(sb), InstructionOperand.Predicate(ps)) =>
          Some(Instruction.Hset2ConstPred(sb, ps, modifiers, guard))
        case _ => None
      }
  }
  object Hset2ImmImm extends Definition("Hset2ImmImm", "HSET2", Vec("hset2_bop__v1")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb), InstructionOperand.Immediate(sb2)) =>
          Some(Instruction.Hset2ImmImm(sb, sb2, modifiers, guard))
        case _ => None
      }
  }
  object Hset2ImmImmPred extends Definition("Hset2ImmImmPred", "HSET2", Vec("hset2__v1")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Immediate(sb),
              InstructionOperand.Immediate(sb2),
              InstructionOperand.Predicate(ps)
            ) => Some(Instruction.Hset2ImmImmPred(sb, sb2, ps, modifiers, guard))
        case _ => None
      }
  }
  object Hset2Pred extends Definition("Hset2Pred", "HSET2", Vec("hset2__v2")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(ps)) => Some(Instruction.Hset2Pred(ps, modifiers, guard))
        case _ => None
      }
  }
  object Hset2RegRegAddressImm extends Definition("Hset2RegRegAddressImm", "HSET2", Vec("hset2_noBop__RCx")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcC),
              InstructionOperand.Immediate(sc_offset)
            ) => Some(Instruction.Hset2RegRegAddressImm(dst, srcA, srcC, sc_offset, modifiers, guard))
        case _ => None
      }
  }
  object Hset2RegRegAddressImmPred extends Definition("Hset2RegRegAddressImmPred", "HSET2", Vec("hset2__RCx")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcC),
              InstructionOperand.Immediate(sc_offset),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Hset2RegRegAddressImmPred(dst, srcA, srcC, sc_offset, pp, modifiers, guard))
        case _ => None
      }
  }
  object Hset2RegRegConst extends Definition("Hset2RegRegConst", "HSET2", Vec("hset2_noBop__RC")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sc)
            ) => Some(Instruction.Hset2RegRegConst(dst, srcA, sc, modifiers, guard))
        case _ => None
      }
  }
  object Hset2RegRegConstPred extends Definition("Hset2RegRegConstPred", "HSET2", Vec("hset2__RC")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sc),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Hset2RegRegConstPred(dst, srcA, sc, pp, modifiers, guard))
        case _ => None
      }
  }
  object Hset2RegRegImmImm
      extends Definition("Hset2RegRegImmImm", "HSET2", Vec("hset2_noBop__RI", "hset2_noBop_fixed__RI")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sc),
              InstructionOperand.Immediate(sc1)
            ) => Some(Instruction.Hset2RegRegImmImm(dst, srcA, sc, sc1, modifiers, guard))
        case _ => None
      }
  }
  object Hset2RegRegImmImmPred
      extends Definition("Hset2RegRegImmImmPred", "HSET2", Vec("hset2__RI", "hset2_fixed__RI")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sc),
              InstructionOperand.Immediate(sc1),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Hset2RegRegImmImmPred(dst, srcA, sc, sc1, pp, modifiers, guard))
        case _ => None
      }
  }
  object Hset2RegRegReg extends Definition(
        "Hset2RegRegReg",
        "HSET2",
        Vec("hset2_noBop__RR", "hset2_noBop__RU", "hset2_noBop_fixed__RR", "hset2_noBop_fixed__RU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Hset2RegRegReg(dst, srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Hset2RegRegRegPred extends Definition(
        "Hset2RegRegRegPred",
        "HSET2",
        Vec("hset2__RR", "hset2__RU", "hset2_fixed__RR", "hset2_fixed__RU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Hset2RegRegRegPred(dst, srcA, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object Hsetp2PredPredRegAddressImm
      extends Definition("Hsetp2PredPredRegAddressImm", "HSETP2", Vec("hsetp2_noBop__RCx")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcC),
              InstructionOperand.Immediate(sc_offset)
            ) => Some(Instruction.Hsetp2PredPredRegAddressImm(pu, pv, srcA, srcC, sc_offset, modifiers, guard))
        case _ => None
      }
  }
  object Hsetp2PredPredRegAddressImmPred
      extends Definition("Hsetp2PredPredRegAddressImmPred", "HSETP2", Vec("hsetp2__RCx")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcC),
              InstructionOperand.Immediate(sc_offset),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Hsetp2PredPredRegAddressImmPred(pu, pv, srcA, srcC, sc_offset, pp, modifiers, guard))
        case _ => None
      }
  }
  object Hsetp2PredPredRegConst
      extends Definition("Hsetp2PredPredRegConst", "HSETP2", Vec("NoBop_Const_HSETP2", "hsetp2_noBop__RC")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.Hsetp2PredPredRegConst(dstPredicate, nPd, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object Hsetp2PredPredRegConstPred
      extends Definition("Hsetp2PredPredRegConstPred", "HSETP2", Vec("Const_HSETP2", "hsetp2__RC")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.Hsetp2PredPredRegConstPred(dstPredicate, nPd, srcA, srcConst, pa, modifiers, guard))
        case _ => None
      }
  }
  object Hsetp2PredPredRegImmImm extends Definition(
        "Hsetp2PredPredRegImmImm",
        "HSETP2",
        Vec("NoBop_Imm_HSETP2", "hsetp2_noBop__RI", "hsetp2_noBop_fixed__RI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Immediate(uImmH0)
            ) => Some(Instruction.Hsetp2PredPredRegImmImm(dstPredicate, nPd, srcA, uImm, uImmH0, modifiers, guard))
        case _ => None
      }
  }
  object Hsetp2PredPredRegImmImmPred
      extends Definition("Hsetp2PredPredRegImmImmPred", "HSETP2", Vec("Imm_HSETP2", "hsetp2__RI", "hsetp2_fixed__RI")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Immediate(uImmH0),
              InstructionOperand.Predicate(pa)
            ) =>
          Some(Instruction.Hsetp2PredPredRegImmImmPred(dstPredicate, nPd, srcA, uImm, uImmH0, pa, modifiers, guard))
        case _ => None
      }
  }
  object Hsetp2PredPredRegReg extends Definition(
        "Hsetp2PredPredRegReg",
        "HSETP2",
        Vec("NoBop_HSETP2", "hsetp2_noBop__RR", "hsetp2_noBop__RU", "hsetp2_noBop_fixed__RR", "hsetp2_noBop_fixed__RU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.Hsetp2PredPredRegReg(dstPredicate, nPd, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object Hsetp2PredPredRegRegPred extends Definition(
        "Hsetp2PredPredRegRegPred",
        "HSETP2",
        Vec("HSETP2", "hsetp2__RR", "hsetp2__RU", "hsetp2_fixed__RR", "hsetp2_fixed__RU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.Hsetp2PredPredRegRegPred(dstPredicate, nPd, srcA, srcB, pa, modifiers, guard))
        case _ => None
      }
  }
  object I2fRegAddressImm extends Definition(
        "I2fRegAddressImm",
        "I2F",
        Vec(
          "i2f_Rd64__CX_16b",
          "i2f_Rd64__CX_32b",
          "i2f_Rd64__CX_64b",
          "i2f_Rd64__CX_8b",
          "i2f__CX_16b",
          "i2f__CX_32b",
          "i2f__CX_64b",
          "i2f__CX_8b"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.I2fRegAddressImm(dst, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object I2fRegConst extends Definition(
        "I2fRegConst",
        "I2F",
        Vec(
          "Const I2F",
          "Const I2F16",
          "Const_I2F",
          "Const_I2F16",
          "Const_I2F64",
          "Const_I2F_F64",
          "i2f_Rd64__Cb_16b",
          "i2f_Rd64__Cb_32b",
          "i2f_Rd64__Cb_64b",
          "i2f_Rd64__Cb_8b",
          "i2f__Cb_16b",
          "i2f__Cb_32b",
          "i2f__Cb_64b",
          "i2f__Cb_8b"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(sb)) =>
          Some(Instruction.I2fRegConst(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object I2fRegImm extends Definition(
        "I2fRegImm",
        "I2F",
        Vec(
          "Imm I2F",
          "Imm I2F16",
          "Imm_I2F",
          "Imm_I2F16",
          "Imm_I2F64",
          "Imm_I2F_F64",
          "i2f_Rd64__IS_32b",
          "i2f_Rd64__IS_64b",
          "i2f_Rd64__IU_32b",
          "i2f_Rd64__IU_64b",
          "i2f__IS_32b",
          "i2f__IS_64b",
          "i2f__IU_32b",
          "i2f__IU_64b"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.I2fRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object I2fRegReg extends Definition(
        "I2fRegReg",
        "I2F",
        Vec(
          "I2F",
          "I2F16",
          "I2F64",
          "I2F_F64",
          "i2f_Rd64__Rb_16b",
          "i2f_Rd64__Rb_32b",
          "i2f_Rd64__Rb_64b",
          "i2f_Rd64__Rb_8b",
          "i2f_Rd64__UR_16b",
          "i2f_Rd64__UR_32b",
          "i2f_Rd64__UR_64b",
          "i2f_Rd64__UR_8b",
          "i2f__Rb_16b",
          "i2f__Rb_32b",
          "i2f__Rb_64b",
          "i2f__Rb_8b",
          "i2f__UR_16b",
          "i2f__UR_32b",
          "i2f__UR_64b",
          "i2f__UR_8b"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.I2fRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object I2fpRegAddressImm extends Definition("I2fpRegAddressImm", "I2FP", Vec("i2fp__RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.I2fpRegAddressImm(dst, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object I2fpRegConst extends Definition("I2fpRegConst", "I2FP", Vec("i2fp__RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(sb)) =>
          Some(Instruction.I2fpRegConst(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object I2fpRegImm extends Definition("I2fpRegImm", "I2FP", Vec("i2fp__RsIR", "i2fp__RuIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.I2fpRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object I2fpRegReg
      extends Definition("I2fpRegReg", "I2FP", Vec("i2fp__RRR", "i2fp__RUR", "i2fp__Rbx2", "i2fp__URbx2")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.I2fpRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object I2iRegAddressImm extends Definition("I2iRegAddressImm", "I2I", Vec("i2i__RCxR_RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.I2iRegAddressImm(dst, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object I2iRegConst extends Definition(
        "I2iRegConst",
        "I2I",
        Vec("Const I2I", "Const I2I16", "Const_I2I", "Const_I2I16", "i2i__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(sb)) =>
          Some(Instruction.I2iRegConst(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object I2iRegImm
      extends Definition("I2iRegImm", "I2I", Vec("Imm I2I", "Imm I2I16", "Imm_I2I", "Imm_I2I16", "i2i__RsIR_RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.I2iRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object I2iRegReg extends Definition("I2iRegReg", "I2I", Vec("I2I", "I2I16", "i2i__RRR_RRR", "i2i__RUR_RUR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.I2iRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object I2ipRegRegAddressImmReg extends Definition(
        "I2ipRegRegAddressImmReg",
        "I2IP",
        Vec(
          "i2ip_24__RCxR_RCxR",
          "i2ip_24_relu__RCxR_RCxR",
          "i2ip_28__RCxR_RCxR",
          "i2ip_28_relu__RCxR_RCxR",
          "i2ip__RCxR_RCxR",
          "i2ip_relu__RCxR_RCxR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.I2ipRegRegAddressImmReg(dst, srcA, srcB, sb_offset, srcC, modifiers, guard))
        case _ => None
      }
  }
  object I2ipRegRegConstReg extends Definition(
        "I2ipRegRegConstReg",
        "I2IP",
        Vec(
          "i2ip_24__RCR_RCR",
          "i2ip_24_relu__RCR_RCR",
          "i2ip_28__RCR_RCR",
          "i2ip_28_relu__RCR_RCR",
          "i2ip__RCR_RCR",
          "i2ip_relu__RCR_RCR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.I2ipRegRegConstReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object I2ipRegRegImmReg extends Definition(
        "I2ipRegRegImmReg",
        "I2IP",
        Vec(
          "i2ip_24__RsIR_RIR",
          "i2ip_24_relu__RsIR_RIR",
          "i2ip_28__RsIR_RIR",
          "i2ip_28_relu__RsIR_RIR",
          "i2ip__RsIR_RIR",
          "i2ip_relu__RsIR_RIR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.I2ipRegRegImmReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object I2ipRegRegRegReg extends Definition(
        "I2ipRegRegRegReg",
        "I2IP",
        Vec(
          "i2ip_24__RRR_RRR",
          "i2ip_24__RUR_RUR",
          "i2ip_24_relu__RRR_RRR",
          "i2ip_24_relu__RUR_RUR",
          "i2ip_28__RRR_RRR",
          "i2ip_28__RUR_RUR",
          "i2ip_28_relu__RRR_RRR",
          "i2ip_28_relu__RUR_RUR",
          "i2ip__RRR_RRR",
          "i2ip__RUR_RUR",
          "i2ip_relu__RRR_RRR",
          "i2ip_relu__RUR_RUR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.I2ipRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object IabsRegAddressImm extends Definition("IabsRegAddressImm", "IABS", Vec("iabs__RCxR_Cx")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.IabsRegAddressImm(dst, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object IabsRegConst extends Definition("IabsRegConst", "IABS", Vec("iabs__RCR_C")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(sb)) =>
          Some(Instruction.IabsRegConst(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object IabsRegImm extends Definition("IabsRegImm", "IABS", Vec("iabs__RsIR_I")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.IabsRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object IabsRegReg extends Definition("IabsRegReg", "IABS", Vec("iabs__RRR_R", "iabs__RUR_UR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.IabsRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object IaddRegPredRegAddressImm extends Definition("IaddRegPredRegAddressImm", "IADD", Vec("iadd_noimm__RCxR_RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.IaddRegPredRegAddressImm(dst, pu, srcA, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object IaddRegPredRegAddressImmPred
      extends Definition("IaddRegPredRegAddressImmPred", "IADD", Vec("iadd_x_noimm__RCxR_RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.IaddRegPredRegAddressImmPred(dst, pu, srcA, srcB, sb_offset, pp, modifiers, guard))
        case _ => None
      }
  }
  object IaddRegPredRegConst extends Definition("IaddRegPredRegConst", "IADD", Vec("iadd_noimm__RCR_RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb)
            ) => Some(Instruction.IaddRegPredRegConst(dst, pu, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object IaddRegPredRegConstPred extends Definition("IaddRegPredRegConstPred", "IADD", Vec("iadd_x_noimm__RCR_RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.IaddRegPredRegConstPred(dst, pu, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object IaddRegPredRegImm
      extends Definition("IaddRegPredRegImm", "IADD", Vec("iadd_64_imm__RsIR_RIR", "iadd_imm__RsIR_RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.IaddRegPredRegImm(dst, pu, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object IaddRegPredRegImmPred
      extends Definition("IaddRegPredRegImmPred", "IADD", Vec("iadd_64_x_imm__RIR_RIR", "iadd_x_imm__RIR_RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.IaddRegPredRegImmPred(dst, pu, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object IaddRegPredRegImmReg extends Definition("IaddRegPredRegImmReg", "IADD", Vec("iadd_xor_imm__RsIR_RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.IaddRegPredRegImmReg(dst, pu, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object IaddRegPredRegImmRegPred
      extends Definition("IaddRegPredRegImmRegPred", "IADD", Vec("iadd_x_xor_imm__RIR_RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.IaddRegPredRegImmRegPred(dst, pu, srcA, sb, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object IaddRegPredRegReg extends Definition(
        "IaddRegPredRegReg",
        "IADD",
        Vec("iadd_64_noimm__RRR_RRR", "iadd_64_noimm__RUR_RUR", "iadd_noimm__RRR_RRR", "iadd_noimm__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.IaddRegPredRegReg(dst, pu, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object IaddRegPredRegRegPred extends Definition(
        "IaddRegPredRegRegPred",
        "IADD",
        Vec("iadd_64_x_noimm__RRR_RRR", "iadd_64_x_noimm__RUR_RUR", "iadd_x_noimm__RRR_RRR", "iadd_x_noimm__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.IaddRegPredRegRegPred(dst, pu, srcA, srcB, pp, modifiers, guard))
        case _ => None
      }
  }
  object IaddRegPredRegRegReg
      extends Definition("IaddRegPredRegRegReg", "IADD", Vec("iadd_xor_noimm__RRR_RRR", "iadd_xor_noimm__RUR_RUR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.IaddRegPredRegRegReg(dst, pu, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object IaddRegPredRegRegRegPred extends Definition(
        "IaddRegPredRegRegRegPred",
        "IADD",
        Vec("iadd_x_xor_noimm__RRR_RRR", "iadd_x_xor_noimm__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.IaddRegPredRegRegRegPred(dst, pu, srcA, srcB, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object IaddRegRegAddressImm extends Definition("IaddRegRegAddressImm", "IADD", Vec("iadd_noimm__RCxR_RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.IaddRegRegAddressImm(dst, srcA, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object IaddRegRegAddressImmPred
      extends Definition("IaddRegRegAddressImmPred", "IADD", Vec("iadd_x_noimm__RCxR_RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.IaddRegRegAddressImmPred(dst, srcA, srcB, sb_offset, pp, modifiers, guard))
        case _ => None
      }
  }
  object IaddRegRegConst
      extends Definition("IaddRegRegConst", "IADD", Vec("Const IADD", "Const_IADD", "iadd_noimm__RCR_RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb)
            ) => Some(Instruction.IaddRegRegConst(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object IaddRegRegConstPred extends Definition("IaddRegRegConstPred", "IADD", Vec("iadd_x_noimm__RCR_RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.IaddRegRegConstPred(dst, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object IaddRegRegImm extends Definition(
        "IaddRegRegImm",
        "IADD",
        Vec("Imm IADD", "Imm_IADD", "iadd_64_imm__RsIR_RIR", "iadd_imm__RsIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.IaddRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object IaddRegRegImmPred
      extends Definition("IaddRegRegImmPred", "IADD", Vec("iadd_64_x_imm__RIR_RIR", "iadd_x_imm__RIR_RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.IaddRegRegImmPred(dst, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object IaddRegRegImmReg extends Definition("IaddRegRegImmReg", "IADD", Vec("iadd_xor_imm__RsIR_RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.IaddRegRegImmReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object IaddRegRegImmRegPred extends Definition("IaddRegRegImmRegPred", "IADD", Vec("iadd_x_xor_imm__RIR_RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.IaddRegRegImmRegPred(dst, srcA, sb, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object IaddRegRegReg extends Definition(
        "IaddRegRegReg",
        "IADD",
        Vec("IADD", "iadd_64_noimm__RRR_RRR", "iadd_64_noimm__RUR_RUR", "iadd_noimm__RRR_RRR", "iadd_noimm__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.IaddRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object IaddRegRegRegPred extends Definition(
        "IaddRegRegRegPred",
        "IADD",
        Vec("iadd_64_x_noimm__RRR_RRR", "iadd_64_x_noimm__RUR_RUR", "iadd_x_noimm__RRR_RRR", "iadd_x_noimm__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.IaddRegRegRegPred(dst, srcA, srcB, pp, modifiers, guard))
        case _ => None
      }
  }
  object IaddRegRegRegReg
      extends Definition("IaddRegRegRegReg", "IADD", Vec("iadd_xor_noimm__RRR_RRR", "iadd_xor_noimm__RUR_RUR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.IaddRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object IaddRegRegRegRegPred extends Definition(
        "IaddRegRegRegRegPred",
        "IADD",
        Vec("iadd_x_xor_noimm__RRR_RRR", "iadd_x_xor_noimm__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.IaddRegRegRegRegPred(dst, srcA, srcB, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3RegPredPredRegAddressImmReg
      extends Definition("Iadd3RegPredPredRegAddressImmReg", "IADD3", Vec("iadd3_noimm__RCxR_RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC)
            ) =>
          Some(Instruction.Iadd3RegPredPredRegAddressImmReg(dst, pu, pv, srcA, srcB, sb_offset, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3RegPredPredRegAddressImmRegPredPred
      extends Definition("Iadd3RegPredPredRegAddressImmRegPredPred", "IADD3", Vec("iadd3_x_noimm__RCxR_RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Predicate(pq)
            ) => Some(Instruction.Iadd3RegPredPredRegAddressImmRegPredPred(
            dst,
            pu,
            pv,
            srcA,
            srcB,
            sb_offset,
            srcC,
            pp,
            pq,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object Iadd3RegPredPredRegConstReg
      extends Definition("Iadd3RegPredPredRegConstReg", "IADD3", Vec("iadd3_noimm__RCR_RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Iadd3RegPredPredRegConstReg(dst, pu, pv, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3RegPredPredRegConstRegPredPred
      extends Definition("Iadd3RegPredPredRegConstRegPredPred", "IADD3", Vec("iadd3_x_noimm__RCR_RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Predicate(pq)
            ) =>
          Some(Instruction.Iadd3RegPredPredRegConstRegPredPred(dst, pu, pv, srcA, sb, srcC, pp, pq, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3RegPredPredRegImmReg
      extends Definition("Iadd3RegPredPredRegImmReg", "IADD3", Vec("iadd3_imm__RsIR_RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Iadd3RegPredPredRegImmReg(dst, pu, pv, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3RegPredPredRegImmRegPredPred
      extends Definition("Iadd3RegPredPredRegImmRegPredPred", "IADD3", Vec("iadd3_x_imm__RIR_RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Predicate(pq)
            ) =>
          Some(Instruction.Iadd3RegPredPredRegImmRegPredPred(dst, pu, pv, srcA, sb, srcC, pp, pq, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3RegPredPredRegRegReg
      extends Definition("Iadd3RegPredPredRegRegReg", "IADD3", Vec("iadd3_noimm__RRR_RRR", "iadd3_noimm__RUR_RUR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Iadd3RegPredPredRegRegReg(dst, pu, pv, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3RegPredPredRegRegRegPredPred extends Definition(
        "Iadd3RegPredPredRegRegRegPredPred",
        "IADD3",
        Vec("iadd3_x_noimm__RRR_RRR", "iadd3_x_noimm__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Predicate(pq)
            ) =>
          Some(Instruction.Iadd3RegPredPredRegRegRegPredPred(dst, pu, pv, srcA, srcB, srcC, pp, pq, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3RegPredRegAddressImmReg
      extends Definition("Iadd3RegPredRegAddressImmReg", "IADD3", Vec("iadd3_noimm__RCxR_RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Iadd3RegPredRegAddressImmReg(dst, pu, srcA, srcB, sb_offset, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3RegPredRegAddressImmRegPredPred
      extends Definition("Iadd3RegPredRegAddressImmRegPredPred", "IADD3", Vec("iadd3_x_noimm__RCxR_RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Predicate(pq)
            ) => Some(Instruction.Iadd3RegPredRegAddressImmRegPredPred(
            dst,
            pu,
            srcA,
            srcB,
            sb_offset,
            srcC,
            pp,
            pq,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object Iadd3RegPredRegConstReg extends Definition("Iadd3RegPredRegConstReg", "IADD3", Vec("iadd3_noimm__RCR_RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Iadd3RegPredRegConstReg(dst, pu, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3RegPredRegConstRegPredPred
      extends Definition("Iadd3RegPredRegConstRegPredPred", "IADD3", Vec("iadd3_x_noimm__RCR_RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Predicate(pq)
            ) => Some(Instruction.Iadd3RegPredRegConstRegPredPred(dst, pu, srcA, sb, srcC, pp, pq, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3RegPredRegImmReg extends Definition("Iadd3RegPredRegImmReg", "IADD3", Vec("iadd3_imm__RsIR_RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Iadd3RegPredRegImmReg(dst, pu, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3RegPredRegImmRegPredPred
      extends Definition("Iadd3RegPredRegImmRegPredPred", "IADD3", Vec("iadd3_x_imm__RIR_RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Predicate(pq)
            ) => Some(Instruction.Iadd3RegPredRegImmRegPredPred(dst, pu, srcA, sb, srcC, pp, pq, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3RegPredRegRegReg
      extends Definition("Iadd3RegPredRegRegReg", "IADD3", Vec("iadd3_noimm__RRR_RRR", "iadd3_noimm__RUR_RUR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Iadd3RegPredRegRegReg(dst, pu, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3RegPredRegRegRegPredPred extends Definition(
        "Iadd3RegPredRegRegRegPredPred",
        "IADD3",
        Vec("iadd3_x_noimm__RRR_RRR", "iadd3_x_noimm__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Predicate(pq)
            ) => Some(Instruction.Iadd3RegPredRegRegRegPredPred(dst, pu, srcA, srcB, srcC, pp, pq, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3RegRegAddressImmReg
      extends Definition("Iadd3RegRegAddressImmReg", "IADD3", Vec("iadd3_noimm__RCxR_RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Iadd3RegRegAddressImmReg(dst, srcA, srcB, sb_offset, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3RegRegAddressImmRegPredPred
      extends Definition("Iadd3RegRegAddressImmRegPredPred", "IADD3", Vec("iadd3_x_noimm__RCxR_RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Predicate(pq)
            ) =>
          Some(Instruction.Iadd3RegRegAddressImmRegPredPred(dst, srcA, srcB, sb_offset, srcC, pp, pq, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3RegRegConstReg
      extends Definition("Iadd3RegRegConstReg", "IADD3", Vec("BConst_IADD3", "iadd3_noimm__RCR_RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Iadd3RegRegConstReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3RegRegConstRegPredPred
      extends Definition("Iadd3RegRegConstRegPredPred", "IADD3", Vec("iadd3_x_noimm__RCR_RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Predicate(pq)
            ) => Some(Instruction.Iadd3RegRegConstRegPredPred(dst, srcA, sb, srcC, pp, pq, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3RegRegImmReg extends Definition("Iadd3RegRegImmReg", "IADD3", Vec("BImm_IADD3", "iadd3_imm__RsIR_RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Iadd3RegRegImmReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3RegRegImmRegPredPred
      extends Definition("Iadd3RegRegImmRegPredPred", "IADD3", Vec("iadd3_x_imm__RIR_RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Predicate(pq)
            ) => Some(Instruction.Iadd3RegRegImmRegPredPred(dst, srcA, sb, srcC, pp, pq, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3RegRegRegReg
      extends Definition("Iadd3RegRegRegReg", "IADD3", Vec("IADD3", "iadd3_noimm__RRR_RRR", "iadd3_noimm__RUR_RUR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Iadd3RegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Iadd3RegRegRegRegPredPred extends Definition(
        "Iadd3RegRegRegRegPredPred",
        "IADD3",
        Vec("iadd3_x_noimm__RRR_RRR", "iadd3_x_noimm__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Predicate(pq)
            ) => Some(Instruction.Iadd3RegRegRegRegPredPred(dst, srcA, srcB, srcC, pp, pq, modifiers, guard))
        case _ => None
      }
  }
  object Iadd32iRegPredRegImm extends Definition("Iadd32iRegPredRegImm", "IADD32I", Vec("iadd32i_imm__RsIR_RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.Iadd32iRegPredRegImm(dst, pu, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object Iadd32iRegPredRegImmPred
      extends Definition("Iadd32iRegPredRegImmPred", "IADD32I", Vec("iadd32i_x_imm__RIR_RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Iadd32iRegPredRegImmPred(dst, pu, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object Iadd32iRegRegImm extends Definition("Iadd32iRegRegImm", "IADD32I", Vec("IADD32I", "iadd32i_imm__RsIR_RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.Iadd32iRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object Iadd32iRegRegImmPred extends Definition("Iadd32iRegRegImmPred", "IADD32I", Vec("iadd32i_x_imm__RIR_RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Iadd32iRegRegImmPred(dst, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object IcmpRegRegConstReg extends Definition(
        "IcmpRegRegConstReg",
        "ICMP",
        Vec("Const ICMP", "Const ICMP U", "Const_ICMP", "Const_ICMP_U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.IcmpRegRegConstReg(dst, srcA, srcConst, srcC, modifiers, guard))
        case _ => None
      }
  }
  object IcmpRegRegImmReg
      extends Definition("IcmpRegRegImmReg", "ICMP", Vec("Imm ICMP", "Imm ICMP U", "Imm_ICMP", "Imm_ICMP_U")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.IcmpRegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object IcmpRegRegRegConst extends Definition(
        "IcmpRegRegRegConst",
        "ICMP",
        Vec("Const1 ICMP", "Const1 ICMP U", "Const1_ICMP", "Const1_ICMP_U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.IcmpRegRegRegConst(dst, srcA, srcB, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object IcmpRegRegRegReg extends Definition("IcmpRegRegRegReg", "ICMP", Vec("ICMP", "ICMP U", "ICMP_U")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.IcmpRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object IdeNoOperands extends Definition("IdeNoOperands", "IDE", Vec("IDE", "IDE_DI", "IDE_EN", "ide_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.IdeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object IdeImm extends Definition("IdeImm", "IDE", Vec("IDE", "IDE_DI", "IDE_EN", "ide_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb)) => Some(Instruction.IdeImm(sb, modifiers, guard))
        case _ => None
      }
  }
  object IdpRegRegAddressImmReg extends Definition("IdpRegRegAddressImmReg", "IDP", Vec("idp_2a__CXb", "idp_4a__CXb")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.IdpRegRegAddressImmReg(dst, srcA, srcB, sb_offset, srcC, modifiers, guard))
        case _ => None
      }
  }
  object IdpRegRegConstReg
      extends Definition("IdpRegRegConstReg", "IDP", Vec("IDP_4A_c", "IDP_c", "idp_2a__C", "idp_4a__C")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.IdpRegRegConstReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object IdpRegRegRegReg extends Definition(
        "IdpRegRegRegReg",
        "IDP",
        Vec("IDP_2A", "IDP_4A", "idp_2a__R", "idp_2a__URb", "idp_4a__R", "idp_4a__URb")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.IdpRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Idp4aRegRegAddressImmReg extends Definition("Idp4aRegRegAddressImmReg", "IDP4A", Vec("idp4a__CXb")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Idp4aRegRegAddressImmReg(dst, srcA, srcB, sb_offset, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Idp4aRegRegConstReg extends Definition("Idp4aRegRegConstReg", "IDP4A", Vec("IDP4A_c", "idp4a__C")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Idp4aRegRegConstReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Idp4aRegRegRegReg extends Definition("Idp4aRegRegRegReg", "IDP4A", Vec("IDP4A", "idp4a__R", "idp4a__URb")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Idp4aRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object IgmmaRegAddressReg extends Definition("IgmmaRegAddressReg", "IGMMA", Vec("igmma_URa_Rc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.IgmmaRegAddressReg(dst, srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object IgmmaRegAddressRegPred extends Definition("IgmmaRegAddressRegPred", "IGMMA", Vec("igmma_URa_Rc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.IgmmaRegAddressRegPred(dst, srcA, srcC, uPp, modifiers, guard))
        case _ => None
      }
  }
  val definitions: Vec[Definition] = Vec(
    FcmpRegRegRegReg,
    FenceNoOperands,
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
    Ffma32iRegRegImm,
    Ffma32iRegRegImmReg,
    FhaddRegRegReg,
    FhfmaRegRegRegReg,
    FloRegAddressImm,
    FloRegConst,
    FloRegImm,
    FloRegPredAddressImm,
    FloRegPredConst,
    FloRegPredImm,
    FloRegPredReg,
    FloRegReg,
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
    FmulRegRegAddressImm,
    FmulRegRegConst,
    FmulRegRegImm,
    FmulRegRegReg,
    Fmul2RegRegImm,
    Fmul2RegRegReg,
    Fmul32iRegRegImm,
    FootprintPredRegRegRegReg,
    FootprintPredRegRegRegRegImmImm,
    FootprintPredRegRegRegRegImmImmImm,
    FootprintPredRegRegRegRegReg,
    FootprintPredRegRegRegRegRegImm,
    FootprintRegRegRegReg,
    FootprintRegRegRegRegImmImm,
    FootprintRegRegRegRegImmImmImm,
    FootprintRegRegRegRegReg,
    FootprintRegRegRegRegRegImm,
    FrndRegAddressImm,
    FrndRegConst,
    FrndRegImm,
    FrndRegReg,
    FselRegRegAddressImmPred,
    FselRegRegConstPred,
    FselRegRegImmPred,
    FselRegRegRegPred,
    FsetRegRegAddressImm,
    FsetRegRegAddressImmPred,
    FsetRegRegConst,
    FsetRegRegConstPred,
    FsetRegRegImm,
    FsetRegRegImmPred,
    FsetRegRegReg,
    FsetRegRegRegPred,
    FsetpPredPredRegAddressImmPred,
    FsetpPredPredRegConstPred,
    FsetpPredPredRegImmPred,
    FsetpPredPredRegRegPred,
    FsetpPredRegAddressImm,
    FsetpPredRegConst,
    FsetpPredRegImm,
    FsetpPredRegReg,
    FswzRegRegReg,
    FswzaddRegRegReg,
    GatherRegRegRegRegImm,
    GatherRegRegRegRegImmImm,
    GatherRegRegRegRegImmImmImm,
    GetcrsptrReg,
    GetlmembaseReg,
    Hadd2NoOperands,
    Hadd2Const,
    Hadd2ImmImm,
    Hadd2RegRegAddressImm,
    Hadd2RegRegConst,
    Hadd2RegRegImm,
    Hadd2RegRegImmImm,
    Hadd2RegRegReg,
    Hadd232iImmImm,
    Hadd232iRegRegImmImm,
    Hfma2NoOperands,
    Hfma2Const,
    Hfma2ImmImm,
    Hfma2RegRegAddressImmReg,
    Hfma2RegRegAddressImmRegPred,
    Hfma2RegRegConstReg,
    Hfma2RegRegConstRegPred,
    Hfma2RegRegImmImmReg,
    Hfma2RegRegImmImmRegPred,
    Hfma2RegRegRegAddressImm,
    Hfma2RegRegRegAddressImmPred,
    Hfma2RegRegRegConst,
    Hfma2RegRegRegConstPred,
    Hfma2RegRegRegImmImm,
    Hfma2RegRegRegImmImmPred,
    Hfma2RegRegRegReg,
    Hfma2RegRegRegRegPred,
    Hfma232iRegRegImmImm,
    Hfma232iRegRegImmImmReg,
    HgmmaRegAddressReg,
    HgmmaRegAddressRegPred,
    HgmmaRegAddressRegPredRegImm,
    HgmmaRegAddressRegReg,
    HgmmaRegAddressRegRegImm,
    HgmmaRegAddressRegRegPred,
    HgmmaRegAddressRegRegPredRegImm,
    HgmmaRegAddressRegRegRegImm,
    HgmmaRegRegAddressReg,
    HgmmaRegRegAddressRegPred,
    HgmmaRegRegAddressRegPredRegImm,
    HgmmaRegRegAddressRegRegImm,
    HmmaAddressRegRegAddress,
    HmmaAddressRegRegAddressPred,
    HmmaAddressRegRegAddressPredRegImm,
    HmmaAddressRegRegAddressRegImm,
    HmmaRegRegRegReg,
    HmmaRegRegRegRegPred,
    HmmaRegRegRegRegPredRegImm,
    HmmaRegRegRegRegRegImm,
    Hmnmx2RegPredPredRegAddressImmPred,
    Hmnmx2RegPredPredRegConstPred,
    Hmnmx2RegPredPredRegImmImmPred,
    Hmnmx2RegPredPredRegRegPred,
    Hmnmx2RegRegAddressImmPred,
    Hmnmx2RegRegConstPred,
    Hmnmx2RegRegImmImmPred,
    Hmnmx2RegRegRegPred,
    Hmul2NoOperands,
    Hmul2Const,
    Hmul2ImmImm,
    Hmul2RegRegAddressImm,
    Hmul2RegRegConst,
    Hmul2RegRegImmImm,
    Hmul2RegRegReg,
    Hmul232iImmImm,
    Hmul232iRegRegImmImm,
    Hset2NoOperands,
    Hset2Const,
    Hset2ConstPred,
    Hset2ImmImm,
    Hset2ImmImmPred,
    Hset2Pred,
    Hset2RegRegAddressImm,
    Hset2RegRegAddressImmPred,
    Hset2RegRegConst,
    Hset2RegRegConstPred,
    Hset2RegRegImmImm,
    Hset2RegRegImmImmPred,
    Hset2RegRegReg,
    Hset2RegRegRegPred,
    Hsetp2PredPredRegAddressImm,
    Hsetp2PredPredRegAddressImmPred,
    Hsetp2PredPredRegConst,
    Hsetp2PredPredRegConstPred,
    Hsetp2PredPredRegImmImm,
    Hsetp2PredPredRegImmImmPred,
    Hsetp2PredPredRegReg,
    Hsetp2PredPredRegRegPred,
    I2fRegAddressImm,
    I2fRegConst,
    I2fRegImm,
    I2fRegReg,
    I2fpRegAddressImm,
    I2fpRegConst,
    I2fpRegImm,
    I2fpRegReg,
    I2iRegAddressImm,
    I2iRegConst,
    I2iRegImm,
    I2iRegReg,
    I2ipRegRegAddressImmReg,
    I2ipRegRegConstReg,
    I2ipRegRegImmReg,
    I2ipRegRegRegReg,
    IabsRegAddressImm,
    IabsRegConst,
    IabsRegImm,
    IabsRegReg,
    IaddRegPredRegAddressImm,
    IaddRegPredRegAddressImmPred,
    IaddRegPredRegConst,
    IaddRegPredRegConstPred,
    IaddRegPredRegImm,
    IaddRegPredRegImmPred,
    IaddRegPredRegImmReg,
    IaddRegPredRegImmRegPred,
    IaddRegPredRegReg,
    IaddRegPredRegRegPred,
    IaddRegPredRegRegReg,
    IaddRegPredRegRegRegPred,
    IaddRegRegAddressImm,
    IaddRegRegAddressImmPred,
    IaddRegRegConst,
    IaddRegRegConstPred,
    IaddRegRegImm,
    IaddRegRegImmPred,
    IaddRegRegImmReg,
    IaddRegRegImmRegPred,
    IaddRegRegReg,
    IaddRegRegRegPred,
    IaddRegRegRegReg,
    IaddRegRegRegRegPred,
    Iadd3RegPredPredRegAddressImmReg,
    Iadd3RegPredPredRegAddressImmRegPredPred,
    Iadd3RegPredPredRegConstReg,
    Iadd3RegPredPredRegConstRegPredPred,
    Iadd3RegPredPredRegImmReg,
    Iadd3RegPredPredRegImmRegPredPred,
    Iadd3RegPredPredRegRegReg,
    Iadd3RegPredPredRegRegRegPredPred,
    Iadd3RegPredRegAddressImmReg,
    Iadd3RegPredRegAddressImmRegPredPred,
    Iadd3RegPredRegConstReg,
    Iadd3RegPredRegConstRegPredPred,
    Iadd3RegPredRegImmReg,
    Iadd3RegPredRegImmRegPredPred,
    Iadd3RegPredRegRegReg,
    Iadd3RegPredRegRegRegPredPred,
    Iadd3RegRegAddressImmReg,
    Iadd3RegRegAddressImmRegPredPred,
    Iadd3RegRegConstReg,
    Iadd3RegRegConstRegPredPred,
    Iadd3RegRegImmReg,
    Iadd3RegRegImmRegPredPred,
    Iadd3RegRegRegReg,
    Iadd3RegRegRegRegPredPred,
    Iadd32iRegPredRegImm,
    Iadd32iRegPredRegImmPred,
    Iadd32iRegRegImm,
    Iadd32iRegRegImmPred,
    IcmpRegRegConstReg,
    IcmpRegRegImmReg,
    IcmpRegRegRegConst,
    IcmpRegRegRegReg,
    IdeNoOperands,
    IdeImm,
    IdpRegRegAddressImmReg,
    IdpRegRegConstReg,
    IdpRegRegRegReg,
    Idp4aRegRegAddressImmReg,
    Idp4aRegRegConstReg,
    Idp4aRegRegRegReg,
    IgmmaRegAddressReg,
    IgmmaRegAddressRegPred
  )
}
