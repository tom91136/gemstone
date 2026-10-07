// Generated SASS instruction definitions and checked operand constructors. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.sass
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import Instruction.*
@javax.annotation.processing.Generated(Array("sbt codegen"))
object InstructionDefinitions3 {
  object LeaRegRegRegImm extends Definition(
        "LeaRegRegRegImm",
        "LEA",
        Vec(
          "LEA_LO_REG",
          "lea_hi_noimm_sx32__RRR_RRR",
          "lea_hi_noimm_sx32__RUR_RUR",
          "lea_lo_noimm__RRR_RRR",
          "lea_lo_noimm__RUR_RUR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.LeaRegRegRegImm(dst, srcA, srcB, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object LeaRegRegRegImmImm extends Definition("LeaRegRegRegImmImm", "LEA", Vec("lea_hi_imm__RRuI_RRI")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.LeaRegRegRegImmImm(dst, srcA, srcB, sc, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object LeaRegRegRegImmImmPred extends Definition("LeaRegRegRegImmImmPred", "LEA", Vec("lea_hi_imm_x__RRuI_RRI")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc),
              InstructionOperand.Immediate(scaleU5),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.LeaRegRegRegImmImmPred(dst, srcA, srcB, sc, scaleU5, pp, modifiers, guard))
        case _ => None
      }
  }
  object LeaRegRegRegImmPred extends Definition(
        "LeaRegRegRegImmPred",
        "LEA",
        Vec(
          "lea_hi_noimm_sx32_x__RRR_RRR",
          "lea_hi_noimm_sx32_x__RUR_RUR",
          "lea_lo_noimm_x__RRR_RRR",
          "lea_lo_noimm_x__RUR_RUR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(scaleU5),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.LeaRegRegRegImmPred(dst, srcA, srcB, scaleU5, pp, modifiers, guard))
        case _ => None
      }
  }
  object LeaRegRegRegReg extends Definition("LeaRegRegRegReg", "LEA", Vec("LEA_HI_REG")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.LeaRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object LeaRegRegRegRegImm extends Definition(
        "LeaRegRegRegRegImm",
        "LEA",
        Vec("LEA_HI_REG", "lea_hi_noimm__RRR_RRR", "lea_hi_noimm__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.LeaRegRegRegRegImm(dst, srcA, srcB, srcC, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object LeaRegRegRegRegImmPred
      extends Definition("LeaRegRegRegRegImmPred", "LEA", Vec("lea_hi_noimm_x__RRR_RRR", "lea_hi_noimm_x__RUR_RUR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(scaleU5),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.LeaRegRegRegRegImmPred(dst, srcA, srcB, srcC, scaleU5, pp, modifiers, guard))
        case _ => None
      }
  }
  object LepcReg extends Definition("LepcReg", "LEPC", Vec("LEPC", "lepc_", "lepc__RRR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst)) => Some(Instruction.LepcReg(dst, modifiers, guard))
        case _ => None
      }
  }
  object LepcRegImm extends Definition("LepcRegImm", "LEPC", Vec("lepc__R_I_R", "lepc_rel_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sImm58)) =>
          Some(Instruction.LepcRegImm(dst, sImm58, modifiers, guard))
        case _ => None
      }
  }
  object LongjmpNoOperands extends Definition("LongjmpNoOperands", "LONGJMP", Vec("BRK", "LONGJMP")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.LongjmpNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object LopPredRegRegAddressImm
      extends Definition("LopPredRegRegAddressImm", "LOP", Vec("lop_noimm_optionalPp__RCxR_RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.LopPredRegRegAddressImm(pu, dst, srcA, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object LopPredRegRegAddressImmPred
      extends Definition("LopPredRegRegAddressImmPred", "LOP", Vec("lop_noimm__RCxR_RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.LopPredRegRegAddressImmPred(pu, dst, srcA, srcB, sb_offset, pp, modifiers, guard))
        case _ => None
      }
  }
  object LopPredRegRegConst
      extends Definition("LopPredRegRegConst", "LOP", Vec("Const_LOP", "lop_noimm_optionalPp__RCR_RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.LopPredRegRegConst(dstPredicate, dst, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object LopPredRegRegConstPred extends Definition("LopPredRegRegConstPred", "LOP", Vec("lop_noimm__RCR_RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.LopPredRegRegConstPred(pu, dst, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object LopPredRegRegImm extends Definition("LopPredRegRegImm", "LOP", Vec("Imm_LOP", "lop_imm_optionalPp_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.LopPredRegRegImm(dstPredicate, dst, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object LopPredRegRegImmPred extends Definition("LopPredRegRegImmPred", "LOP", Vec("lop_imm_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.LopPredRegRegImmPred(pu, dst, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object LopPredRegRegReg extends Definition(
        "LopPredRegRegReg",
        "LOP",
        Vec("LOP", "lop_noimm_optionalPp__RRR_RRR", "lop_noimm_optionalPp__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.LopPredRegRegReg(dstPredicate, dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object LopPredRegRegRegPred
      extends Definition("LopPredRegRegRegPred", "LOP", Vec("lop_noimm__RRR_RRR", "lop_noimm__RUR_RUR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.LopPredRegRegRegPred(pu, dst, srcA, srcB, pp, modifiers, guard))
        case _ => None
      }
  }
  object LopRegRegAddressImm extends Definition("LopRegRegAddressImm", "LOP", Vec("lop_noimm_optionalPp__RCxR_RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.LopRegRegAddressImm(dst, srcA, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object LopRegRegAddressImmPred extends Definition("LopRegRegAddressImmPred", "LOP", Vec("lop_noimm__RCxR_RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.LopRegRegAddressImmPred(dst, srcA, srcB, sb_offset, pp, modifiers, guard))
        case _ => None
      }
  }
  object LopRegRegConst
      extends Definition("LopRegRegConst", "LOP", Vec("Const LOP", "Const_LOP", "lop_noimm_optionalPp__RCR_RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb)
            ) => Some(Instruction.LopRegRegConst(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object LopRegRegConstPred extends Definition("LopRegRegConstPred", "LOP", Vec("lop_noimm__RCR_RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.LopRegRegConstPred(dst, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object LopRegRegImm extends Definition("LopRegRegImm", "LOP", Vec("Imm LOP", "Imm_LOP", "lop_imm_optionalPp_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.LopRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object LopRegRegImmPred extends Definition("LopRegRegImmPred", "LOP", Vec("lop_imm_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.LopRegRegImmPred(dst, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object LopRegRegReg extends Definition(
        "LopRegRegReg",
        "LOP",
        Vec("LOP", "lop_noimm_optionalPp__RRR_RRR", "lop_noimm_optionalPp__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.LopRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object LopRegRegRegPred
      extends Definition("LopRegRegRegPred", "LOP", Vec("lop_noimm__RRR_RRR", "lop_noimm__RUR_RUR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.LopRegRegRegPred(dst, srcA, srcB, pp, modifiers, guard))
        case _ => None
      }
  }
  object Lop3PredRegRegAddressImmReg
      extends Definition("Lop3PredRegRegAddressImmReg", "LOP3", Vec("lop3_noimm_optionalPp__RCxR_RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Lop3PredRegRegAddressImmReg(pu, dst, srcA, srcB, sb_offset, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Lop3PredRegRegAddressImmRegImm
      extends Definition("Lop3PredRegRegAddressImmRegImm", "LOP3", Vec("lop3_lut_optionalPp__RCxR_RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(imm8)
            ) =>
          Some(Instruction.Lop3PredRegRegAddressImmRegImm(pu, dst, srcA, srcB, sb_offset, srcC, imm8, modifiers, guard))
        case _ => None
      }
  }
  object Lop3PredRegRegAddressImmRegImmPred
      extends Definition("Lop3PredRegRegAddressImmRegImmPred", "LOP3", Vec("lop3_lut__RCxR_RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(imm8),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Lop3PredRegRegAddressImmRegImmPred(
            pu,
            dst,
            srcA,
            srcB,
            sb_offset,
            srcC,
            imm8,
            pp,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object Lop3PredRegRegAddressImmRegPred
      extends Definition("Lop3PredRegRegAddressImmRegPred", "LOP3", Vec("lop3_noimm__RCxR_RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) =>
          Some(Instruction.Lop3PredRegRegAddressImmRegPred(pu, dst, srcA, srcB, sb_offset, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object Lop3PredRegRegConstReg
      extends Definition("Lop3PredRegRegConstReg", "LOP3", Vec("lop3_noimm_optionalPp__RCR_RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Lop3PredRegRegConstReg(pu, dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Lop3PredRegRegConstRegImm
      extends Definition("Lop3PredRegRegConstRegImm", "LOP3", Vec("lop3_lut_optionalPp__RCR_RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(imm8)
            ) => Some(Instruction.Lop3PredRegRegConstRegImm(pu, dst, srcA, sb, srcC, imm8, modifiers, guard))
        case _ => None
      }
  }
  object Lop3PredRegRegConstRegImmPred
      extends Definition("Lop3PredRegRegConstRegImmPred", "LOP3", Vec("lop3_lut__RCR_RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(imm8),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Lop3PredRegRegConstRegImmPred(pu, dst, srcA, sb, srcC, imm8, pp, modifiers, guard))
        case _ => None
      }
  }
  object Lop3PredRegRegConstRegPred
      extends Definition("Lop3PredRegRegConstRegPred", "LOP3", Vec("lop3_noimm__RCR_RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Lop3PredRegRegConstRegPred(pu, dst, srcA, sb, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object Lop3PredRegRegImmReg extends Definition("Lop3PredRegRegImmReg", "LOP3", Vec("lop3_imm_optionalPp__RIR_RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Lop3PredRegRegImmReg(pu, dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Lop3PredRegRegImmRegImm
      extends Definition("Lop3PredRegRegImmRegImm", "LOP3", Vec("lop3_lut_optionalPp__RuIR_RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(imm8)
            ) => Some(Instruction.Lop3PredRegRegImmRegImm(pu, dst, srcA, sb, srcC, imm8, modifiers, guard))
        case _ => None
      }
  }
  object Lop3PredRegRegImmRegImmPred
      extends Definition("Lop3PredRegRegImmRegImmPred", "LOP3", Vec("lop3_lut__RuIR_RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(imm8),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Lop3PredRegRegImmRegImmPred(pu, dst, srcA, sb, srcC, imm8, pp, modifiers, guard))
        case _ => None
      }
  }
  object Lop3PredRegRegImmRegPred extends Definition("Lop3PredRegRegImmRegPred", "LOP3", Vec("lop3_imm__RIR_RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Lop3PredRegRegImmRegPred(pu, dst, srcA, sb, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object Lop3PredRegRegRegReg extends Definition(
        "Lop3PredRegRegRegReg",
        "LOP3",
        Vec("LOP3", "lop3_noimm_optionalPp__RRR_RRR", "lop3_noimm_optionalPp__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Lop3PredRegRegRegReg(dstPredicate, dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Lop3PredRegRegRegRegImm extends Definition(
        "Lop3PredRegRegRegRegImm",
        "LOP3",
        Vec("LOP3_LUT", "lop3_lut_optionalPp__RRR_RRR", "lop3_lut_optionalPp__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(imm8)
            ) => Some(Instruction.Lop3PredRegRegRegRegImm(dstPredicate, dst, srcA, srcB, srcC, imm8, modifiers, guard))
        case _ => None
      }
  }
  object Lop3PredRegRegRegRegImmPred
      extends Definition("Lop3PredRegRegRegRegImmPred", "LOP3", Vec("lop3_lut__RRR_RRR", "lop3_lut__RUR_RUR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(imm8),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Lop3PredRegRegRegRegImmPred(pu, dst, srcA, srcB, srcC, imm8, pp, modifiers, guard))
        case _ => None
      }
  }
  object Lop3PredRegRegRegRegPred
      extends Definition("Lop3PredRegRegRegRegPred", "LOP3", Vec("lop3_noimm__RRR_RRR", "lop3_noimm__RUR_RUR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Lop3PredRegRegRegRegPred(pu, dst, srcA, srcB, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object Lop3RegRegAddressImmReg
      extends Definition("Lop3RegRegAddressImmReg", "LOP3", Vec("lop3_noimm_optionalPp__RCxR_RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Lop3RegRegAddressImmReg(dst, srcA, srcB, sb_offset, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Lop3RegRegAddressImmRegImm
      extends Definition("Lop3RegRegAddressImmRegImm", "LOP3", Vec("lop3_lut_optionalPp__RCxR_RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(imm8)
            ) => Some(Instruction.Lop3RegRegAddressImmRegImm(dst, srcA, srcB, sb_offset, srcC, imm8, modifiers, guard))
        case _ => None
      }
  }
  object Lop3RegRegAddressImmRegImmPred
      extends Definition("Lop3RegRegAddressImmRegImmPred", "LOP3", Vec("lop3_lut__RCxR_RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(imm8),
              InstructionOperand.Predicate(pp)
            ) =>
          Some(Instruction.Lop3RegRegAddressImmRegImmPred(dst, srcA, srcB, sb_offset, srcC, imm8, pp, modifiers, guard))
        case _ => None
      }
  }
  object Lop3RegRegAddressImmRegPred
      extends Definition("Lop3RegRegAddressImmRegPred", "LOP3", Vec("lop3_noimm__RCxR_RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Lop3RegRegAddressImmRegPred(dst, srcA, srcB, sb_offset, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object Lop3RegRegConstReg
      extends Definition("Lop3RegRegConstReg", "LOP3", Vec("LOP3_Bconst", "lop3_noimm_optionalPp__RCR_RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Lop3RegRegConstReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Lop3RegRegConstRegImm
      extends Definition("Lop3RegRegConstRegImm", "LOP3", Vec("LOP3_LUT_BConst", "lop3_lut_optionalPp__RCR_RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(imm8)
            ) => Some(Instruction.Lop3RegRegConstRegImm(dst, srcA, sb, srcC, imm8, modifiers, guard))
        case _ => None
      }
  }
  object Lop3RegRegConstRegImmPred extends Definition("Lop3RegRegConstRegImmPred", "LOP3", Vec("lop3_lut__RCR_RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(imm8),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Lop3RegRegConstRegImmPred(dst, srcA, sb, srcC, imm8, pp, modifiers, guard))
        case _ => None
      }
  }
  object Lop3RegRegConstRegPred extends Definition("Lop3RegRegConstRegPred", "LOP3", Vec("lop3_noimm__RCR_RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Lop3RegRegConstRegPred(dst, srcA, sb, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object Lop3RegRegImmReg
      extends Definition("Lop3RegRegImmReg", "LOP3", Vec("LOP3_BImm", "lop3_imm_optionalPp__RIR_RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Lop3RegRegImmReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Lop3RegRegImmRegImm
      extends Definition("Lop3RegRegImmRegImm", "LOP3", Vec("LOP3_LUT_BImm", "lop3_lut_optionalPp__RuIR_RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(imm8)
            ) => Some(Instruction.Lop3RegRegImmRegImm(dst, srcA, sb, srcC, imm8, modifiers, guard))
        case _ => None
      }
  }
  object Lop3RegRegImmRegImmPred extends Definition("Lop3RegRegImmRegImmPred", "LOP3", Vec("lop3_lut__RuIR_RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(imm8),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Lop3RegRegImmRegImmPred(dst, srcA, sb, srcC, imm8, pp, modifiers, guard))
        case _ => None
      }
  }
  object Lop3RegRegImmRegPred extends Definition("Lop3RegRegImmRegPred", "LOP3", Vec("lop3_imm__RIR_RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Lop3RegRegImmRegPred(dst, srcA, sb, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object Lop3RegRegRegReg extends Definition(
        "Lop3RegRegRegReg",
        "LOP3",
        Vec("LOP3", "lop3_noimm_optionalPp__RRR_RRR", "lop3_noimm_optionalPp__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Lop3RegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Lop3RegRegRegRegImm extends Definition(
        "Lop3RegRegRegRegImm",
        "LOP3",
        Vec("LOP3_LUT", "lop3_lut_optionalPp__RRR_RRR", "lop3_lut_optionalPp__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(imm8)
            ) => Some(Instruction.Lop3RegRegRegRegImm(dst, srcA, srcB, srcC, imm8, modifiers, guard))
        case _ => None
      }
  }
  object Lop3RegRegRegRegImmPred
      extends Definition("Lop3RegRegRegRegImmPred", "LOP3", Vec("lop3_lut__RRR_RRR", "lop3_lut__RUR_RUR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(imm8),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Lop3RegRegRegRegImmPred(dst, srcA, srcB, srcC, imm8, pp, modifiers, guard))
        case _ => None
      }
  }
  object Lop3RegRegRegRegPred
      extends Definition("Lop3RegRegRegRegPred", "LOP3", Vec("lop3_noimm__RRR_RRR", "lop3_noimm__RUR_RUR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Lop3RegRegRegRegPred(dst, srcA, srcB, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object Lop32iPredRegRegImm extends Definition("Lop32iPredRegRegImm", "LOP32I", Vec("lop32i_optionalPp_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.Lop32iPredRegRegImm(pu, dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object Lop32iPredRegRegImmPred extends Definition("Lop32iPredRegRegImmPred", "LOP32I", Vec("lop32i_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Lop32iPredRegRegImmPred(pu, dst, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object Lop32iRegRegImm extends Definition("Lop32iRegRegImm", "LOP32I", Vec("LOP32I", "lop32i_optionalPp_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.Lop32iRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object Lop32iRegRegImmPred extends Definition("Lop32iRegRegImmPred", "LOP32I", Vec("lop32i_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Lop32iRegRegImmPred(dst, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object MatchPredRegReg extends Definition("MatchPredRegReg", "MATCH", Vec("match__ALL")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA)
            ) => Some(Instruction.MatchPredRegReg(pu, dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object MatchRegReg extends Definition("MatchRegReg", "MATCH", Vec("match__ANY")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcA)) =>
          Some(Instruction.MatchRegReg(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object MembarNoOperands
      extends Definition("MembarNoOperands", "MEMBAR", Vec("MEMBAR", "membar_", "membar_async_", "membar_tma_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.MembarNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object MovAddressConst extends Definition("MovAddressConst", "MOV", Vec("mov_indexedRF_IRFd__Cb")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(dst), InstructionOperand.ConstantBank(sb)) =>
          Some(Instruction.MovAddressConst(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object MovAddressConstImm extends Definition("MovAddressConstImm", "MOV", Vec("mov_indexedRF_IRFd__Cb")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(dst),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Immediate(pixMaskU04)
            ) => Some(Instruction.MovAddressConstImm(dst, sb, pixMaskU04, modifiers, guard))
        case _ => None
      }
  }
  object MovAddressImm extends Definition("MovAddressImm", "MOV", Vec("mov_indexedRF_IRFd__Ib")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.MovAddressImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object MovAddressImmImm extends Definition("MovAddressImmImm", "MOV", Vec("mov_indexedRF_IRFd__Ib")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(dst),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Immediate(pixMaskU04)
            ) => Some(Instruction.MovAddressImmImm(dst, sb, pixMaskU04, modifiers, guard))
        case _ => None
      }
  }
  object MovAddressReg extends Definition("MovAddressReg", "MOV", Vec("mov_indexedRF_IRFd__Rb")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.MovAddressReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object MovAddressRegImm extends Definition("MovAddressRegImm", "MOV", Vec("mov_indexedRF_IRFd__Rb")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(dst),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(pixMaskU04)
            ) => Some(Instruction.MovAddressRegImm(dst, srcB, pixMaskU04, modifiers, guard))
        case _ => None
      }
  }
  object MovRegAddress extends Definition("MovRegAddress", "MOV", Vec("mov_indexedRF_Rd_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Address(srcB)) =>
          Some(Instruction.MovRegAddress(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object MovRegAddressImm extends Definition("MovRegAddressImm", "MOV", Vec("mov__RCx", "mov_indexedRF_Rd_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(pixMaskU04)
            ) => Some(Instruction.MovRegAddressImm(dst, srcB, pixMaskU04, modifiers, guard))
        case _ => None
      }
  }
  object MovRegAddressImmImm extends Definition("MovRegAddressImmImm", "MOV", Vec("mov__RCx")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Immediate(pixMaskU04)
            ) => Some(Instruction.MovRegAddressImmImm(dst, srcB, sb_offset, pixMaskU04, modifiers, guard))
        case _ => None
      }
  }
  object MovRegConst extends Definition("MovRegConst", "MOV", Vec("Const MOV", "Const_MOV", "mov__RC", "mov__RCR_RC")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(sb)) =>
          Some(Instruction.MovRegConst(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object MovRegConstImm
      extends Definition("MovRegConstImm", "MOV", Vec("Const MOV", "Const_MOV", "mov__RC", "mov__RCR_RC")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Immediate(pixMaskU04)
            ) => Some(Instruction.MovRegConstImm(dst, sb, pixMaskU04, modifiers, guard))
        case _ => None
      }
  }
  object MovRegImm
      extends Definition("MovRegImm", "MOV", Vec("Imm MOV", "Imm_MOV", "mov__RI", "mov__RuIR_RI", "mov_imm64_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.MovRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object MovRegImmImm extends Definition("MovRegImmImm", "MOV", Vec("Imm MOV", "Imm_MOV", "mov__RI", "mov__RuIR_RI")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Immediate(pixMaskU04)
            ) => Some(Instruction.MovRegImmImm(dst, sb, pixMaskU04, modifiers, guard))
        case _ => None
      }
  }
  object MovRegReg extends Definition(
        "MovRegReg",
        "MOV",
        Vec("MOV", "mov_64__RR", "mov_64__RU", "mov__RR", "mov__RRR_RR", "mov__RU", "mov_nonconformity__RU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.MovRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object MovRegRegImm extends Definition("MovRegRegImm", "MOV", Vec("MOV", "mov__RR", "mov__RRR_RR", "mov__RU")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(pixMaskU04)
            ) => Some(Instruction.MovRegRegImm(dst, srcB, pixMaskU04, modifiers, guard))
        case _ => None
      }
  }
  object Mov32iRegImm extends Definition("Mov32iRegImm", "MOV32I", Vec("MOV32I", "mov32i_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.Mov32iRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object Mov32iRegImmImm extends Definition("Mov32iRegImmImm", "MOV32I", Vec("MOV32I", "mov32i_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Immediate(pixMaskU04)
            ) => Some(Instruction.Mov32iRegImmImm(dst, sb, pixMaskU04, modifiers, guard))
        case _ => None
      }
  }
  object Mov64iurRegImm extends Definition("Mov64iurRegImm", "MOV64IUR", Vec("mov64iur_imm_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.Mov64iurRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object Mov64iurRegReg extends Definition("Mov64iurRegReg", "MOV64IUR", Vec("mov64iur_ur_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.Mov64iurRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object MovmRegReg extends Definition("MovmRegReg", "MOVM", Vec("movm_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcA)) =>
          Some(Instruction.MovmRegReg(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object MufuRegAddressImm extends Definition("MufuRegAddressImm", "MUFU", Vec("mufu__RCxR_RCx", "mufu_fp16__RCx")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.MufuRegAddressImm(dst, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object MufuRegConst extends Definition("MufuRegConst", "MUFU", Vec("mufu__RCR_RC", "mufu_fp16__RC")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(sb)) =>
          Some(Instruction.MufuRegConst(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object MufuRegImm extends Definition("MufuRegImm", "MUFU", Vec("mufu__RIR_RI", "mufu_fp16__RI")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.MufuRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object MufuRegImmImm extends Definition("MufuRegImmImm", "MUFU", Vec("mufu_fp16_simd__RI")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Immediate(sb1)
            ) => Some(Instruction.MufuRegImmImm(dst, sb, sb1, modifiers, guard))
        case _ => None
      }
  }
  object MufuRegReg extends Definition(
        "MufuRegReg",
        "MUFU",
        Vec(
          "MUFU",
          "mufu__RRR_RR",
          "mufu__RUR_RU",
          "mufu_fp16__RR",
          "mufu_fp16__RU",
          "mufu_fp16_simd__RR",
          "mufu_fp16_simd__RU",
          "mufu_fp16_swap__RR",
          "mufu_fp16_swap__RU"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcA)) =>
          Some(Instruction.MufuRegReg(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object NanosleepNoOperands extends Definition("NanosleepNoOperands", "NANOSLEEP", Vec("nanosleep_clear_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.NanosleepNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object NanosleepAddressImm extends Definition("NanosleepAddressImm", "NANOSLEEP", Vec("nanosleep__CX")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcB), InstructionOperand.Immediate(sb_offset)) =>
          Some(Instruction.NanosleepAddressImm(srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object NanosleepConst extends Definition("NanosleepConst", "NANOSLEEP", Vec("nanosleep__C")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(sb)) => Some(Instruction.NanosleepConst(sb, modifiers, guard))
        case _ => None
      }
  }
  object NanosleepImm extends Definition("NanosleepImm", "NANOSLEEP", Vec("nanosleep__I")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb)) => Some(Instruction.NanosleepImm(sb, modifiers, guard))
        case _ => None
      }
  }
  object NanosleepPred extends Definition("NanosleepPred", "NANOSLEEP", Vec("nanosleep_clear_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp)) => Some(Instruction.NanosleepPred(pp, modifiers, guard))
        case _ => None
      }
  }
  object NanosleepPredAddressImm extends Definition("NanosleepPredAddressImm", "NANOSLEEP", Vec("nanosleep__CX")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pp),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.NanosleepPredAddressImm(pp, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object NanosleepPredConst extends Definition("NanosleepPredConst", "NANOSLEEP", Vec("nanosleep__C")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.ConstantBank(sb)) =>
          Some(Instruction.NanosleepPredConst(pp, sb, modifiers, guard))
        case _ => None
      }
  }
  object NanosleepPredImm extends Definition("NanosleepPredImm", "NANOSLEEP", Vec("nanosleep__I")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.NanosleepPredImm(pp, sb, modifiers, guard))
        case _ => None
      }
  }
  object NanosleepPredReg extends Definition("NanosleepPredReg", "NANOSLEEP", Vec("nanosleep__R", "nanosleep__U")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.Register(srcB)) =>
          Some(Instruction.NanosleepPredReg(pp, srcB, modifiers, guard))
        case _ => None
      }
  }
  object NanosleepReg extends Definition("NanosleepReg", "NANOSLEEP", Vec("nanosleep__R", "nanosleep__U")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcB)) => Some(Instruction.NanosleepReg(srcB, modifiers, guard))
        case _ => None
      }
  }
  object NanotrapConst extends Definition("NanotrapConst", "NANOTRAP", Vec("nanotrap__C")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(sb)) => Some(Instruction.NanotrapConst(sb, modifiers, guard))
        case _ => None
      }
  }
  object NanotrapImm extends Definition("NanotrapImm", "NANOTRAP", Vec("nanotrap__I")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb)) => Some(Instruction.NanotrapImm(sb, modifiers, guard))
        case _ => None
      }
  }
  object NanotrapPredConst extends Definition("NanotrapPredConst", "NANOTRAP", Vec("nanotrap__C")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.ConstantBank(sb)) =>
          Some(Instruction.NanotrapPredConst(pp, sb, modifiers, guard))
        case _ => None
      }
  }
  object NanotrapPredImm extends Definition("NanotrapPredImm", "NANOTRAP", Vec("nanotrap__I")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.NanotrapPredImm(pp, sb, modifiers, guard))
        case _ => None
      }
  }
  object NanotrapPredReg extends Definition("NanotrapPredReg", "NANOTRAP", Vec("nanotrap__R")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.Register(srcB)) =>
          Some(Instruction.NanotrapPredReg(pp, srcB, modifiers, guard))
        case _ => None
      }
  }
  object NanotrapReg extends Definition("NanotrapReg", "NANOTRAP", Vec("nanotrap__R")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcB)) => Some(Instruction.NanotrapReg(srcB, modifiers, guard))
        case _ => None
      }
  }
  object NopNoOperands extends Definition("NopNoOperands", "NOP", Vec("NOP", "NOP trig", "NOP_trig", "nop_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.NopNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object NopImm extends Definition("NopImm", "NOP", Vec("NOP", "NOP trig", "NOP_trig")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(uImm)) => Some(Instruction.NopImm(uImm, modifiers, guard))
        case _ => None
      }
  }
  object NopSymbol extends Definition("NopSymbol", "NOP", Vec("NOP", "NOP trig", "NOP_trig")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Symbol(testCC)) => Some(Instruction.NopSymbol(testCC, modifiers, guard))
        case _ => None
      }
  }
  object NopSymbolImm extends Definition("NopSymbolImm", "NOP", Vec("NOP", "NOP trig", "NOP_trig")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Symbol(testCC), InstructionOperand.Immediate(uImm)) =>
          Some(Instruction.NopSymbolImm(testCC, uImm, modifiers, guard))
        case _ => None
      }
  }
  object OmmaRegRegRegRegPredRegRegReg extends Definition("OmmaRegRegRegRegPredRegRegReg", "OMMA", Vec("omma_scale_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Register(re),
              InstructionOperand.Register(rh),
              InstructionOperand.Register(uRi)
            ) =>
          Some(Instruction.OmmaRegRegRegRegPredRegRegReg(dst, srcA, srcB, srcC, uPp, re, rh, uRi, modifiers, guard))
        case _ => None
      }
  }
  object OmmaRegRegRegRegPredRegRegRegImm
      extends Definition("OmmaRegRegRegRegPredRegRegRegImm", "OMMA", Vec("omma_sp_scale_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Register(re),
              InstructionOperand.Register(rh),
              InstructionOperand.Register(uRi),
              InstructionOperand.Immediate(sparseId)
            ) => Some(Instruction.OmmaRegRegRegRegPredRegRegRegImm(
            dst,
            srcA,
            srcB,
            srcC,
            uPp,
            re,
            rh,
            uRi,
            sparseId,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object OmmaRegRegRegRegRegRegReg extends Definition("OmmaRegRegRegRegRegRegReg", "OMMA", Vec("omma_scale_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(re),
              InstructionOperand.Register(rh),
              InstructionOperand.Register(uRi)
            ) => Some(Instruction.OmmaRegRegRegRegRegRegReg(dst, srcA, srcB, srcC, re, rh, uRi, modifiers, guard))
        case _ => None
      }
  }
  object OmmaRegRegRegRegRegRegRegImm
      extends Definition("OmmaRegRegRegRegRegRegRegImm", "OMMA", Vec("omma_sp_scale_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(re),
              InstructionOperand.Register(rh),
              InstructionOperand.Register(uRi),
              InstructionOperand.Immediate(sparseId)
            ) =>
          Some(Instruction.OmmaRegRegRegRegRegRegRegImm(dst, srcA, srcB, srcC, re, rh, uRi, sparseId, modifiers, guard))
        case _ => None
      }
  }
  object OutReg extends Definition("OutReg", "OUT", Vec("out__FINAL", "out_final_rz_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA)) => Some(Instruction.OutReg(srcA, modifiers, guard))
        case _ => None
      }
  }
  object OutRegRegConst extends Definition("OutRegRegConst", "OUT", Vec("Const OUT", "Const_OUT")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.OutRegRegConst(dst, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object OutRegRegImm extends Definition("OutRegRegImm", "OUT", Vec("Imm OUT", "Imm_OUT", "out__EMIT_Imm")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.OutRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object OutRegRegReg
      extends Definition("OutRegRegReg", "OUT", Vec("OUT", "out__CUT", "out__EMIT_Rb", "out__EMIT_URb", "out__FINAL")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.OutRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object P2rReg extends Definition("P2rReg", "P2R", Vec("Simple_P2R")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst)) => Some(Instruction.P2rReg(dst, modifiers, guard))
        case _ => None
      }
  }
  object P2rRegRegConst extends Definition("P2rRegRegConst", "P2R", Vec("Const_P2R")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.P2rRegRegConst(dst, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object P2rRegRegImm extends Definition("P2rRegRegImm", "P2R", Vec("Imm_P2R")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.P2rRegRegImm(dst, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object P2rRegRegReg extends Definition("P2rRegRegReg", "P2R", Vec("P2R")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.P2rRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object P2rRegSymbol extends Definition("P2rRegSymbol", "P2R", Vec("Simple P2R", "Simple_P2R", "p2r_simple_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Symbol(pr)) =>
          Some(Instruction.P2rRegSymbol(dst, pr, modifiers, guard))
        case _ => None
      }
  }
  object P2rRegSymbolRegAddressImm extends Definition("P2rRegSymbolRegAddressImm", "P2R", Vec("p2r__RCxR_RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Symbol(pr),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.P2rRegSymbolRegAddressImm(dst, pr, srcA, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object P2rRegSymbolRegConst
      extends Definition("P2rRegSymbolRegConst", "P2R", Vec("Const P2R", "Const_P2R", "p2r__RCR_RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Symbol(pr),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb)
            ) => Some(Instruction.P2rRegSymbolRegConst(dst, pr, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object P2rRegSymbolRegImm
      extends Definition("P2rRegSymbolRegImm", "P2R", Vec("Imm P2R", "Imm_P2R", "p2r__RsIR_RIR", "p2r__RuIR_RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Symbol(pr),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.P2rRegSymbolRegImm(dst, pr, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object P2rRegSymbolRegReg
      extends Definition("P2rRegSymbolRegReg", "P2R", Vec("P2R", "p2r__RRR_RRR", "p2r__RUR_RUR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Symbol(pr),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.P2rRegSymbolRegReg(dst, pr, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object PbkConst extends Definition("PbkConst", "PBK", Vec("PBK_c", "SSY c")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(srcConst)) => Some(Instruction.PbkConst(srcConst, modifiers, guard))
        case _ => None
      }
  }
  object PbkImm extends Definition("PbkImm", "PBK", Vec("PBK", "SSY")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sImm)) => Some(Instruction.PbkImm(sImm, modifiers, guard))
        case _ => None
      }
  }
  object PcntConst extends Definition("PcntConst", "PCNT", Vec("PCNT_c", "SSY c")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(srcConst)) => Some(Instruction.PcntConst(srcConst, modifiers, guard))
        case _ => None
      }
  }
  object PcntImm extends Definition("PcntImm", "PCNT", Vec("PCNT", "SSY")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sImm)) => Some(Instruction.PcntImm(sImm, modifiers, guard))
        case _ => None
      }
  }
  object PexitImm extends Definition("PexitImm", "PEXIT", Vec("PEXIT")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sImm)) => Some(Instruction.PexitImm(sImm, modifiers, guard))
        case _ => None
      }
  }
  object PixldReg extends Definition("PixldReg", "PIXLD", Vec("PIXLD simple", "PIXLD_simple", "pixld_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst)) => Some(Instruction.PixldReg(dst, modifiers, guard))
        case _ => None
      }
  }
  object PixldRegAddress extends Definition("PixldRegAddress", "PIXLD", Vec("I PIXLD", "I_PIXLD", "PIXLD")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Address(srcA)) =>
          Some(Instruction.PixldRegAddress(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object PixldRegPred extends Definition("PixldRegPred", "PIXLD", Vec("PIXLD simple", "PIXLD_simple", "pixld_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Predicate(dstPredicate)) =>
          Some(Instruction.PixldRegPred(dst, dstPredicate, modifiers, guard))
        case _ => None
      }
  }
  object PixldRegPredAddress extends Definition("PixldRegPredAddress", "PIXLD", Vec("I PIXLD", "I_PIXLD", "PIXLD")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Address(srcA)
            ) => Some(Instruction.PixldRegPredAddress(dst, dstPredicate, srcA, modifiers, guard))
        case _ => None
      }
  }
  object PlongjmpConst extends Definition("PlongjmpConst", "PLONGJMP", Vec("PLONGJMP_c", "SSY c")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(srcConst)) =>
          Some(Instruction.PlongjmpConst(srcConst, modifiers, guard))
        case _ => None
      }
  }
  object PlongjmpImm extends Definition("PlongjmpImm", "PLONGJMP", Vec("PLONGJMP", "SSY")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sImm)) => Some(Instruction.PlongjmpImm(sImm, modifiers, guard))
        case _ => None
      }
  }
  object Plop3PredPredAddressImmPredImm
      extends Definition("Plop3PredPredAddressImmPredImm", "PLOP3", Vec("plop3_lut_1out_1reg__RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Predicate(pr),
              InstructionOperand.Immediate(uimm8)
            ) => Some(Instruction.Plop3PredPredAddressImmPredImm(pu, pp, srcB, sb_offset, pr, uimm8, modifiers, guard))
        case _ => None
      }
  }
  object Plop3PredPredAddressImmRegImm
      extends Definition("Plop3PredPredAddressImmRegImm", "PLOP3", Vec("plop3_lut_1out_2reg__RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(uimm8)
            ) => Some(Instruction.Plop3PredPredAddressImmRegImm(pu, pp, srcB, sb_offset, srcC, uimm8, modifiers, guard))
        case _ => None
      }
  }
  object Plop3PredPredConstPredImm
      extends Definition("Plop3PredPredConstPredImm", "PLOP3", Vec("plop3_lut_1out_1reg__RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pp),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Predicate(pr),
              InstructionOperand.Immediate(uimm8)
            ) => Some(Instruction.Plop3PredPredConstPredImm(pu, pp, sb, pr, uimm8, modifiers, guard))
        case _ => None
      }
  }
  object Plop3PredPredConstRegImm
      extends Definition("Plop3PredPredConstRegImm", "PLOP3", Vec("plop3_lut_1out_2reg__RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pp),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(uimm8)
            ) => Some(Instruction.Plop3PredPredConstRegImm(pu, pp, sb, srcC, uimm8, modifiers, guard))
        case _ => None
      }
  }
  object Plop3PredPredPredAddressImmPredImmImm
      extends Definition("Plop3PredPredPredAddressImmPredImmImm", "PLOP3", Vec("plop3_lut_2out_1reg__RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Predicate(pr),
              InstructionOperand.Immediate(uimm8),
              InstructionOperand.Immediate(vimm8)
            ) => Some(Instruction.Plop3PredPredPredAddressImmPredImmImm(
            pu,
            pv,
            pp,
            srcB,
            sb_offset,
            pr,
            uimm8,
            vimm8,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object Plop3PredPredPredAddressImmRegImmImm
      extends Definition("Plop3PredPredPredAddressImmRegImmImm", "PLOP3", Vec("plop3_lut_2out_2reg__RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(uimm8),
              InstructionOperand.Immediate(vimm8)
            ) => Some(Instruction.Plop3PredPredPredAddressImmRegImmImm(
            pu,
            pv,
            pp,
            srcB,
            sb_offset,
            srcC,
            uimm8,
            vimm8,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object Plop3PredPredPredConstPredImmImm
      extends Definition("Plop3PredPredPredConstPredImmImm", "PLOP3", Vec("plop3_lut_2out_1reg__RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Predicate(pp),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Predicate(pr),
              InstructionOperand.Immediate(uimm8),
              InstructionOperand.Immediate(vimm8)
            ) => Some(Instruction.Plop3PredPredPredConstPredImmImm(pu, pv, pp, sb, pr, uimm8, vimm8, modifiers, guard))
        case _ => None
      }
  }
  object Plop3PredPredPredConstRegImmImm
      extends Definition("Plop3PredPredPredConstRegImmImm", "PLOP3", Vec("plop3_lut_2out_2reg__RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Predicate(pp),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(uimm8),
              InstructionOperand.Immediate(vimm8)
            ) => Some(Instruction.Plop3PredPredPredConstRegImmImm(pu, pv, pp, sb, srcC, uimm8, vimm8, modifiers, guard))
        case _ => None
      }
  }
  object Plop3PredPredPredPred
      extends Definition("Plop3PredPredPredPred", "PLOP3", Vec("plop3_1out_", "plop3_1out_uniform_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Predicate(pq),
              InstructionOperand.Predicate(pr)
            ) => Some(Instruction.Plop3PredPredPredPred(pu, pp, pq, pr, modifiers, guard))
        case _ => None
      }
  }
  object Plop3PredPredPredPredImm
      extends Definition("Plop3PredPredPredPredImm", "PLOP3", Vec("plop3_lut_1out_", "plop3_lut_1out_uniform_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Predicate(pq),
              InstructionOperand.Predicate(pr),
              InstructionOperand.Immediate(uimm8)
            ) => Some(Instruction.Plop3PredPredPredPredImm(pu, pp, pq, pr, uimm8, modifiers, guard))
        case _ => None
      }
  }
  object Plop3PredPredPredPredPredImmImm extends Definition(
        "Plop3PredPredPredPredPredImmImm",
        "PLOP3",
        Vec("plop3_lut_2out_", "plop3_lut_2out_uniform_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Predicate(pq),
              InstructionOperand.Predicate(pr),
              InstructionOperand.Immediate(uimm8),
              InstructionOperand.Immediate(vimm8)
            ) => Some(Instruction.Plop3PredPredPredPredPredImmImm(pu, pv, pp, pq, pr, uimm8, vimm8, modifiers, guard))
        case _ => None
      }
  }
  object Plop3PredPredPredRegPredImmImm extends Definition(
        "Plop3PredPredPredRegPredImmImm",
        "PLOP3",
        Vec("plop3_lut_2out_1reg__RRR", "plop3_lut_2out_1reg__RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pr),
              InstructionOperand.Immediate(uimm8),
              InstructionOperand.Immediate(vimm8)
            ) => Some(Instruction.Plop3PredPredPredRegPredImmImm(pu, pv, pp, srcB, pr, uimm8, vimm8, modifiers, guard))
        case _ => None
      }
  }
  object Plop3PredPredPredRegRegImmImm extends Definition(
        "Plop3PredPredPredRegRegImmImm",
        "PLOP3",
        Vec("plop3_lut_2out_2reg__RRR", "plop3_lut_2out_2reg__RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(uimm8),
              InstructionOperand.Immediate(vimm8)
            ) => Some(Instruction.Plop3PredPredPredRegRegImmImm(pu, pv, pp, srcB, srcC, uimm8, vimm8, modifiers, guard))
        case _ => None
      }
  }
  object Plop3PredPredRegAddressImmRegImmImm
      extends Definition("Plop3PredPredRegAddressImmRegImmImm", "PLOP3", Vec("plop3_lut_2out_3reg__RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(uimm8),
              InstructionOperand.Immediate(vimm8)
            ) => Some(Instruction.Plop3PredPredRegAddressImmRegImmImm(
            pu,
            pv,
            srcA,
            srcB,
            sb_offset,
            srcC,
            uimm8,
            vimm8,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object Plop3PredPredRegConstRegImmImm
      extends Definition("Plop3PredPredRegConstRegImmImm", "PLOP3", Vec("plop3_lut_2out_3reg__RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(uimm8),
              InstructionOperand.Immediate(vimm8)
            ) =>
          Some(Instruction.Plop3PredPredRegConstRegImmImm(pu, pv, srcA, sb, srcC, uimm8, vimm8, modifiers, guard))
        case _ => None
      }
  }
  object Plop3PredPredRegPredImm extends Definition(
        "Plop3PredPredRegPredImm",
        "PLOP3",
        Vec("plop3_lut_1out_1reg__RRR", "plop3_lut_1out_1reg__RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pr),
              InstructionOperand.Immediate(uimm8)
            ) => Some(Instruction.Plop3PredPredRegPredImm(pu, pp, srcB, pr, uimm8, modifiers, guard))
        case _ => None
      }
  }
  object Plop3PredPredRegRegImm extends Definition(
        "Plop3PredPredRegRegImm",
        "PLOP3",
        Vec("plop3_lut_1out_2reg__RRR", "plop3_lut_1out_2reg__RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(uimm8)
            ) => Some(Instruction.Plop3PredPredRegRegImm(pu, pp, srcB, srcC, uimm8, modifiers, guard))
        case _ => None
      }
  }
  object Plop3PredPredRegRegRegImmImm extends Definition(
        "Plop3PredPredRegRegRegImmImm",
        "PLOP3",
        Vec("plop3_lut_2out_3reg__RRR", "plop3_lut_2out_3reg__RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(uimm8),
              InstructionOperand.Immediate(vimm8)
            ) =>
          Some(Instruction.Plop3PredPredRegRegRegImmImm(pu, pv, srcA, srcB, srcC, uimm8, vimm8, modifiers, guard))
        case _ => None
      }
  }
  object Plop3PredRegAddressImmRegImm
      extends Definition("Plop3PredRegAddressImmRegImm", "PLOP3", Vec("plop3_lut_1out_3reg__RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(uimm8)
            ) =>
          Some(Instruction.Plop3PredRegAddressImmRegImm(pu, srcA, srcB, sb_offset, srcC, uimm8, modifiers, guard))
        case _ => None
      }
  }
  object Plop3PredRegConstRegImm
      extends Definition("Plop3PredRegConstRegImm", "PLOP3", Vec("plop3_lut_1out_3reg__RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(uimm8)
            ) => Some(Instruction.Plop3PredRegConstRegImm(pu, srcA, sb, srcC, uimm8, modifiers, guard))
        case _ => None
      }
  }
  object Plop3PredRegRegRegImm extends Definition(
        "Plop3PredRegRegRegImm",
        "PLOP3",
        Vec("plop3_lut_1out_3reg__RRR", "plop3_lut_1out_3reg__RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(uimm8)
            ) => Some(Instruction.Plop3PredRegRegRegImm(pu, srcA, srcB, srcC, uimm8, modifiers, guard))
        case _ => None
      }
  }
  object PmtrigImm extends Definition("PmtrigImm", "PMTRIG", Vec("pmtrig_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(imm)) => Some(Instruction.PmtrigImm(imm, modifiers, guard))
        case _ => None
      }
  }
  object PmtrigPredImm extends Definition("PmtrigPredImm", "PMTRIG", Vec("pmtrig_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.Immediate(imm)) =>
          Some(Instruction.PmtrigPredImm(pp, imm, modifiers, guard))
        case _ => None
      }
  }
  object PopcRegAddressImm extends Definition("PopcRegAddressImm", "POPC", Vec("popc__RCxR_RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.PopcRegAddressImm(dst, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object PopcRegConst extends Definition("PopcRegConst", "POPC", Vec("Const_POPC", "popc__RCR_RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(sb)) =>
          Some(Instruction.PopcRegConst(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object PopcRegImm extends Definition("PopcRegImm", "POPC", Vec("Imm_POPC", "popc__RuIR_RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.PopcRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object PopcRegReg extends Definition("PopcRegReg", "POPC", Vec("POPC", "popc__RRR_RRR", "popc__RUR_RUR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.PopcRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object PopcRegRegConst extends Definition("PopcRegRegConst", "POPC", Vec("Const POPC")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.PopcRegRegConst(dst, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object PopcRegRegImm extends Definition("PopcRegRegImm", "POPC", Vec("Imm POPC")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.PopcRegRegImm(dst, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object PopcRegRegReg extends Definition("PopcRegRegReg", "POPC", Vec("POPC")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.PopcRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object PreexitNoOperands extends Definition("PreexitNoOperands", "PREEXIT", Vec("preexit_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.PreexitNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object PretConst extends Definition("PretConst", "PRET", Vec("PRET c", "PRET_c")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(srcConst)) => Some(Instruction.PretConst(srcConst, modifiers, guard))
        case _ => None
      }
  }
  object PretImm extends Definition("PretImm", "PRET", Vec("PRET")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sImm)) => Some(Instruction.PretImm(sImm, modifiers, guard))
        case _ => None
      }
  }
  object PrmtRegRegAddressImmReg extends Definition("PrmtRegRegAddressImmReg", "PRMT", Vec("prmt__RCxR_RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.PrmtRegRegAddressImmReg(dst, srcA, srcB, sb_offset, srcC, modifiers, guard))
        case _ => None
      }
  }
  object PrmtRegRegConstReg
      extends Definition("PrmtRegRegConstReg", "PRMT", Vec("Const PRMT", "Const_PRMT", "prmt__RCR_RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.PrmtRegRegConstReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object PrmtRegRegImmReg
      extends Definition("PrmtRegRegImmReg", "PRMT", Vec("Imm PRMT", "Imm_PRMT", "prmt__RuIR_RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.PrmtRegRegImmReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object PrmtRegRegRegAddressImm extends Definition("PrmtRegRegRegAddressImm", "PRMT", Vec("prmt__RRCx_RRCx")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Address(srcC),
              InstructionOperand.Immediate(sc_offset)
            ) => Some(Instruction.PrmtRegRegRegAddressImm(dst, srcA, srcB, srcC, sc_offset, modifiers, guard))
        case _ => None
      }
  }
  object PrmtRegRegRegConst
      extends Definition("PrmtRegRegRegConst", "PRMT", Vec("Const1 PRMT", "Const1_PRMT", "prmt__RRC_RRC")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(sc)
            ) => Some(Instruction.PrmtRegRegRegConst(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object PrmtRegRegRegImm extends Definition("PrmtRegRegRegImm", "PRMT", Vec("prmt__RRuI_RRI")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.PrmtRegRegRegImm(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object PrmtRegRegRegReg
      extends Definition("PrmtRegRegRegReg", "PRMT", Vec("PRMT", "prmt__RRR_RRR", "prmt__RRU_RRU", "prmt__RUR_RUR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.PrmtRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object PsetRegPredPred extends Definition("PsetRegPredPred", "PSET", Vec("NoBop PSET", "NoBop_PSET")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pc),
              InstructionOperand.Predicate(pb)
            ) => Some(Instruction.PsetRegPredPred(dst, pc, pb, modifiers, guard))
        case _ => None
      }
  }
  object PsetRegPredPredPred extends Definition("PsetRegPredPredPred", "PSET", Vec("PSET")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pc),
              InstructionOperand.Predicate(pb),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.PsetRegPredPredPred(dst, pc, pb, pa, modifiers, guard))
        case _ => None
      }
  }
  object PsetpPredPredPred
      extends Definition("PsetpPredPredPred", "PSETP", Vec("NoBop PSETP", "NoBop_PSETP", "psetp_simple_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(pc),
              InstructionOperand.Predicate(pb)
            ) => Some(Instruction.PsetpPredPredPred(dstPredicate, pc, pb, modifiers, guard))
        case _ => None
      }
  }
  object PsetpPredPredPredPredPred
      extends Definition("PsetpPredPredPredPredPred", "PSETP", Vec("PSETP", "psetp_", "psetp_uniform_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Predicate(pc),
              InstructionOperand.Predicate(pb),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.PsetpPredPredPredPredPred(dstPredicate, nPd, pc, pb, pa, modifiers, guard))
        case _ => None
      }
  }
  object Qadd4RegRegImm extends Definition("Qadd4RegRegImm", "QADD4", Vec("qadd4_rb_imm_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.Qadd4RegRegImm(dst, srcA, sc, modifiers, guard))
        case _ => None
      }
  }
  object Qadd4RegRegReg extends Definition("Qadd4RegRegReg", "QADD4", Vec("qadd4_rc_", "qadd4_urc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Qadd4RegRegReg(dst, srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Qfma4RegRegImmReg extends Definition("Qfma4RegRegImmReg", "QFMA4", Vec("qfma4_imm_rc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Qfma4RegRegImmReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Qfma4RegRegRegImm extends Definition("Qfma4RegRegRegImm", "QFMA4", Vec("qfma4_rb_imm_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.Qfma4RegRegRegImm(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object Qfma4RegRegRegReg
      extends Definition("Qfma4RegRegRegReg", "QFMA4", Vec("qfma4_rb_rc_", "qfma4_rb_urc_", "qfma4_urb_rc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Qfma4RegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object QgmmaRegAddressReg extends Definition("QgmmaRegAddressReg", "QGMMA", Vec("qgmma_URa_Rc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.QgmmaRegAddressReg(dst, srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object QgmmaRegAddressRegPred extends Definition("QgmmaRegAddressRegPred", "QGMMA", Vec("qgmma_URa_Rc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.QgmmaRegAddressRegPred(dst, srcA, srcC, uPp, modifiers, guard))
        case _ => None
      }
  }
  object QgmmaRegAddressRegPredReg
      extends Definition("QgmmaRegAddressRegPredReg", "QGMMA", Vec("qgmma_sparse_URa_Rc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Register(re)
            ) => Some(Instruction.QgmmaRegAddressRegPredReg(dst, srcA, srcC, uPp, re, modifiers, guard))
        case _ => None
      }
  }
  object QgmmaRegAddressRegPredRegImm
      extends Definition("QgmmaRegAddressRegPredRegImm", "QGMMA", Vec("qgmma_sparse_URa_Rc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Register(re),
              InstructionOperand.Immediate(id)
            ) => Some(Instruction.QgmmaRegAddressRegPredRegImm(dst, srcA, srcC, uPp, re, id, modifiers, guard))
        case _ => None
      }
  }
  object QgmmaRegAddressRegReg
      extends Definition("QgmmaRegAddressRegReg", "QGMMA", Vec("qgmma_URa_Rb_Rc_", "qgmma_sparse_URa_Rc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.QgmmaRegAddressRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object QgmmaRegAddressRegRegImm extends Definition("QgmmaRegAddressRegRegImm", "QGMMA", Vec("qgmma_sparse_URa_Rc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(re),
              InstructionOperand.Immediate(id)
            ) => Some(Instruction.QgmmaRegAddressRegRegImm(dst, srcA, srcC, re, id, modifiers, guard))
        case _ => None
      }
  }
  object QgmmaRegAddressRegRegPred extends Definition("QgmmaRegAddressRegRegPred", "QGMMA", Vec("qgmma_URa_Rb_Rc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.QgmmaRegAddressRegRegPred(dst, srcA, srcB, srcC, uPp, modifiers, guard))
        case _ => None
      }
  }
  object QgmmaRegAddressRegRegPredReg
      extends Definition("QgmmaRegAddressRegRegPredReg", "QGMMA", Vec("qgmma_sparse_URa_Rb_Rc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Register(re)
            ) => Some(Instruction.QgmmaRegAddressRegRegPredReg(dst, srcA, srcB, srcC, uPp, re, modifiers, guard))
        case _ => None
      }
  }
  object QgmmaRegAddressRegRegPredRegImm
      extends Definition("QgmmaRegAddressRegRegPredRegImm", "QGMMA", Vec("qgmma_sparse_URa_Rb_Rc_")) {
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
            ) => Some(Instruction.QgmmaRegAddressRegRegPredRegImm(dst, srcA, srcB, srcC, uPp, re, id, modifiers, guard))
        case _ => None
      }
  }
  object QgmmaRegAddressRegRegReg
      extends Definition("QgmmaRegAddressRegRegReg", "QGMMA", Vec("qgmma_sparse_URa_Rb_Rc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(re)
            ) => Some(Instruction.QgmmaRegAddressRegRegReg(dst, srcA, srcB, srcC, re, modifiers, guard))
        case _ => None
      }
  }
  object QgmmaRegAddressRegRegRegImm
      extends Definition("QgmmaRegAddressRegRegRegImm", "QGMMA", Vec("qgmma_sparse_URa_Rb_Rc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(re),
              InstructionOperand.Immediate(id)
            ) => Some(Instruction.QgmmaRegAddressRegRegRegImm(dst, srcA, srcB, srcC, re, id, modifiers, guard))
        case _ => None
      }
  }
  object QgmmaRegRegAddressReg extends Definition("QgmmaRegRegAddressReg", "QGMMA", Vec("qgmma_Ra_URb_Rc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.QgmmaRegRegAddressReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object QgmmaRegRegAddressRegPred extends Definition("QgmmaRegRegAddressRegPred", "QGMMA", Vec("qgmma_Ra_URb_Rc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.QgmmaRegRegAddressRegPred(dst, srcA, srcB, srcC, uPp, modifiers, guard))
        case _ => None
      }
  }
  object QgmmaRegRegAddressRegPredReg
      extends Definition("QgmmaRegRegAddressRegPredReg", "QGMMA", Vec("qgmma_sparse_Ra_URb_Rc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Register(re)
            ) => Some(Instruction.QgmmaRegRegAddressRegPredReg(dst, srcA, srcB, srcC, uPp, re, modifiers, guard))
        case _ => None
      }
  }
  object QgmmaRegRegAddressRegPredRegImm
      extends Definition("QgmmaRegRegAddressRegPredRegImm", "QGMMA", Vec("qgmma_sparse_Ra_URb_Rc_")) {
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
            ) => Some(Instruction.QgmmaRegRegAddressRegPredRegImm(dst, srcA, srcB, srcC, uPp, re, id, modifiers, guard))
        case _ => None
      }
  }
  object QgmmaRegRegAddressRegReg
      extends Definition("QgmmaRegRegAddressRegReg", "QGMMA", Vec("qgmma_sparse_Ra_URb_Rc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(re)
            ) => Some(Instruction.QgmmaRegRegAddressRegReg(dst, srcA, srcB, srcC, re, modifiers, guard))
        case _ => None
      }
  }
  object QgmmaRegRegAddressRegRegImm
      extends Definition("QgmmaRegRegAddressRegRegImm", "QGMMA", Vec("qgmma_sparse_Ra_URb_Rc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(re),
              InstructionOperand.Immediate(id)
            ) => Some(Instruction.QgmmaRegRegAddressRegRegImm(dst, srcA, srcB, srcC, re, id, modifiers, guard))
        case _ => None
      }
  }
  object QmmaRegRegRegReg extends Definition("QmmaRegRegRegReg", "QMMA", Vec("qmma_", "qmma_rowcol_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.QmmaRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object QmmaRegRegRegRegPred extends Definition("QmmaRegRegRegRegPred", "QMMA", Vec("qmma_", "qmma_rowcol_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.QmmaRegRegRegRegPred(dst, srcA, srcB, srcC, uPp, modifiers, guard))
        case _ => None
      }
  }
  object QmmaRegRegRegRegPredRegImm
      extends Definition("QmmaRegRegRegRegPredRegImm", "QMMA", Vec("qmma_sp_", "qmma_sp_rowcol_")) {
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
            ) => Some(Instruction.QmmaRegRegRegRegPredRegImm(dst, srcA, srcB, srcC, uPp, re, id, modifiers, guard))
        case _ => None
      }
  }
  object QmmaRegRegRegRegPredRegRegReg extends Definition("QmmaRegRegRegRegPredRegRegReg", "QMMA", Vec("qmma_scale_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Register(re),
              InstructionOperand.Register(rh),
              InstructionOperand.Register(uRi)
            ) =>
          Some(Instruction.QmmaRegRegRegRegPredRegRegReg(dst, srcA, srcB, srcC, uPp, re, rh, uRi, modifiers, guard))
        case _ => None
      }
  }
  object QmmaRegRegRegRegPredRegRegRegImm
      extends Definition("QmmaRegRegRegRegPredRegRegRegImm", "QMMA", Vec("qmma_sp_scale_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Register(re),
              InstructionOperand.Register(rh),
              InstructionOperand.Register(uRi),
              InstructionOperand.Immediate(sparseId)
            ) => Some(Instruction.QmmaRegRegRegRegPredRegRegRegImm(
            dst,
            srcA,
            srcB,
            srcC,
            uPp,
            re,
            rh,
            uRi,
            sparseId,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object QmmaRegRegRegRegRegImm
      extends Definition("QmmaRegRegRegRegRegImm", "QMMA", Vec("qmma_sp_", "qmma_sp_rowcol_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(re),
              InstructionOperand.Immediate(id)
            ) => Some(Instruction.QmmaRegRegRegRegRegImm(dst, srcA, srcB, srcC, re, id, modifiers, guard))
        case _ => None
      }
  }
  object QmmaRegRegRegRegRegRegReg extends Definition("QmmaRegRegRegRegRegRegReg", "QMMA", Vec("qmma_scale_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(re),
              InstructionOperand.Register(rh),
              InstructionOperand.Register(uRi)
            ) => Some(Instruction.QmmaRegRegRegRegRegRegReg(dst, srcA, srcB, srcC, re, rh, uRi, modifiers, guard))
        case _ => None
      }
  }
  object QmmaRegRegRegRegRegRegRegImm
      extends Definition("QmmaRegRegRegRegRegRegRegImm", "QMMA", Vec("qmma_sp_scale_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(re),
              InstructionOperand.Register(rh),
              InstructionOperand.Register(uRi),
              InstructionOperand.Immediate(sparseId)
            ) =>
          Some(Instruction.QmmaRegRegRegRegRegRegRegImm(dst, srcA, srcB, srcC, re, rh, uRi, sparseId, modifiers, guard))
        case _ => None
      }
  }
  object Qmul4RegRegImm extends Definition("Qmul4RegRegImm", "QMUL4", Vec("qmul4_imm_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.Qmul4RegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object Qmul4RegRegReg extends Definition("Qmul4RegRegReg", "QMUL4", Vec("qmul4_rb_", "qmul4_urb_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.Qmul4RegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object QspcPredAddress extends Definition(
        "QspcPredAddress",
        "QSPC",
        Vec(
          "qspc_PuOnly__RaNonRZ",
          "qspc_PuOnly__RaRZ",
          "qspc_urb_PuOnly__Ra32",
          "qspc_urb_PuOnly__Ra64",
          "qspc_urb_PuOnly__RaRZ"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pu), InstructionOperand.Address(srcA)) =>
          Some(Instruction.QspcPredAddress(pu, srcA, modifiers, guard))
        case _ => None
      }
  }
  object QspcPredRegAddress extends Definition(
        "QspcPredRegAddress",
        "QSPC",
        Vec("qspc__RaNonRZ", "qspc__RaRZ", "qspc_urb__Ra32", "qspc_urb__Ra64", "qspc_urb__RaRZ")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA)
            ) => Some(Instruction.QspcPredRegAddress(pu, dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object QspcRegAddress extends Definition(
        "QspcRegAddress",
        "QSPC",
        Vec(
          "qspc_RdOnly__RaNonRZ",
          "qspc_RdOnly__RaRZ",
          "qspc_urb_RdOnly__Ra32",
          "qspc_urb_RdOnly__Ra64",
          "qspc_urb_RdOnly__RaRZ"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Address(srcA)) =>
          Some(Instruction.QspcRegAddress(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object R2bImmReg extends Definition("R2bImmReg", "R2B", Vec("R2B", "r2b_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(barname), InstructionOperand.Register(srcB)) =>
          Some(Instruction.R2bImmReg(barname, srcB, modifiers, guard))
        case _ => None
      }
  }
  object R2pReg extends Definition("R2pReg", "R2P", Vec("Imm_R2P")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA)) => Some(Instruction.R2pReg(srcA, modifiers, guard))
        case _ => None
      }
  }
  object R2pRegConst extends Definition("R2pRegConst", "R2P", Vec("Const_R2P")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA), InstructionOperand.ConstantBank(srcConst)) =>
          Some(Instruction.R2pRegConst(srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object R2pRegImm extends Definition("R2pRegImm", "R2P", Vec("Imm_R2P")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA), InstructionOperand.Immediate(uImm)) =>
          Some(Instruction.R2pRegImm(srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object R2pRegReg extends Definition("R2pRegReg", "R2P", Vec("R2P")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA), InstructionOperand.Register(srcB)) =>
          Some(Instruction.R2pRegReg(srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object R2pSymbolReg extends Definition("R2pSymbolReg", "R2P", Vec("Imm R2P", "Imm_R2P", "r2p__RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Symbol(pR), InstructionOperand.Register(srcA)) =>
          Some(Instruction.R2pSymbolReg(pR, srcA, modifiers, guard))
        case _ => None
      }
  }
  object R2pSymbolRegAddressImm extends Definition("R2pSymbolRegAddressImm", "R2P", Vec("r2p__RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Symbol(pR),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.R2pSymbolRegAddressImm(pR, srcA, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object R2pSymbolRegConst extends Definition("R2pSymbolRegConst", "R2P", Vec("Const R2P", "Const_R2P", "r2p__RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Symbol(pR),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb)
            ) => Some(Instruction.R2pSymbolRegConst(pR, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object R2pSymbolRegImm extends Definition("R2pSymbolRegImm", "R2P", Vec("Imm R2P", "Imm_R2P", "r2p__RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Symbol(pR), InstructionOperand.Register(srcA), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.R2pSymbolRegImm(pR, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object R2pSymbolRegReg extends Definition("R2pSymbolRegReg", "R2P", Vec("R2P", "r2p__RRR", "r2p__RUR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Symbol(pR), InstructionOperand.Register(srcA), InstructionOperand.Register(srcB)) =>
          Some(Instruction.R2pSymbolRegReg(pR, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object R2urPredRegReg extends Definition(
        "R2urPredRegReg",
        "R2UR",
        Vec("r2ur__OR", "r2ur__noOR", "r2ur_nonconformity__OR", "r2ur_nonconformity__noOR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA)
            ) => Some(Instruction.R2urPredRegReg(pu, dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object R2urRegReg extends Definition("R2urRegReg", "R2UR", Vec("r2ur__noOR", "r2ur_nonconformity__noOR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcA)) =>
          Some(Instruction.R2urRegReg(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object RamNoOperands extends Definition("RamNoOperands", "RAM", Vec("RAM", "SAM")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.RamNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object RedAddressReg extends Definition(
        "RedAddressReg",
        "RED",
        Vec(
          "I RED",
          "I_RED",
          "RED",
          "red__RaNonRZ",
          "red__RaRZ",
          "red_fp__RaNonRZ",
          "red_fp__RaRZ",
          "red_fp_uniform__Ra32",
          "red_fp_uniform__Ra64",
          "red_fp_uniform__RaRZ",
          "red_fp_uniform__memdesc",
          "red_int__RaNonRZ",
          "red_int__RaRZ",
          "red_int_uniform__Ra32",
          "red_int_uniform__Ra64",
          "red_int_uniform__RaRZ",
          "red_int_uniform__memdesc",
          "red_uniform__Ra32",
          "red_uniform__Ra64",
          "red_uniform__RaRZ",
          "red_uniform__memdesc",
          "red_uniform_weakonly__Ra32",
          "red_uniform_weakonly__Ra64",
          "red_uniform_weakonly__RaRZ",
          "red_weakonly__RaNonRZ",
          "red_weakonly__RaRZ"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA), InstructionOperand.Register(srcB)) =>
          Some(Instruction.RedAddressReg(srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object RedasAddressReg
      extends Definition("RedasAddressReg", "REDAS", Vec("redas_64__Ra64", "redas_64__RaRZ", "redas__Ra32")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA), InstructionOperand.Register(srcB)) =>
          Some(Instruction.RedasAddressReg(srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object RedgAddressReg extends Definition(
        "RedgAddressReg",
        "REDG",
        Vec(
          "redg_fp__RaNonRZ",
          "redg_fp__RaRZ",
          "redg_fp_uniform__Ra32",
          "redg_fp_uniform__Ra64",
          "redg_fp_uniform__RaRZ",
          "redg_fp_uniform__memdesc",
          "redg_int__RaNonRZ",
          "redg_int__RaRZ",
          "redg_int_uniform__Ra32",
          "redg_int_uniform__Ra64",
          "redg_int_uniform__RaRZ",
          "redg_int_uniform__memdesc"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA), InstructionOperand.Register(srcB)) =>
          Some(Instruction.RedgAddressReg(srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object RedsAddressReg extends Definition(
        "RedsAddressReg",
        "REDS",
        Vec("atoms_reds__RaNonRZ", "atoms_reds__RaRZ", "atoms_reds_uniform_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA), InstructionOperand.Register(srcB)) =>
          Some(Instruction.RedsAddressReg(srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object ReduxRegReg extends Definition("ReduxRegReg", "REDUX", Vec("redux_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcA)) =>
          Some(Instruction.ReduxRegReg(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object RetNoOperands extends Definition("RetNoOperands", "RET", Vec("RET")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.RetNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object RetPredReg
      extends Definition("RetPredReg", "RET", Vec("ret_rel_reg_", "ret_rel_reg__RIR", "ret_rel_reg__URIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.Register(srcA)) =>
          Some(Instruction.RetPredReg(pp, srcA, modifiers, guard))
        case _ => None
      }
  }
  object RetPredRegImm extends Definition(
        "RetPredRegImm",
        "RET",
        Vec("ret__ABS", "ret__ABS_UR", "ret__REL", "ret__REL_UR", "ret_rel_", "ret_rel__RIR", "ret_rel__URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pp),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(ra_offset)
            ) => Some(Instruction.RetPredRegImm(pp, srcA, ra_offset, modifiers, guard))
        case _ => None
      }
  }
  object RetReg extends Definition("RetReg", "RET", Vec("ret_rel_reg_", "ret_rel_reg__RIR", "ret_rel_reg__URIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA)) => Some(Instruction.RetReg(srcA, modifiers, guard))
        case _ => None
      }
  }
  object RetRegImm extends Definition(
        "RetRegImm",
        "RET",
        Vec("ret__ABS", "ret__ABS_UR", "ret__REL", "ret__REL_UR", "ret_rel_", "ret_rel__RIR", "ret_rel__URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA), InstructionOperand.Immediate(ra_offset)) =>
          Some(Instruction.RetRegImm(srcA, ra_offset, modifiers, guard))
        case _ => None
      }
  }
  object RpcmovAddressImm
      extends Definition("RpcmovAddressImm", "RPCMOV", Vec("rpcmov_dstPc64__CXb", "rpcmov_dstPc_CXb_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcB), InstructionOperand.Immediate(sb_offset)) =>
          Some(Instruction.RpcmovAddressImm(srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object RpcmovConst extends Definition("RpcmovConst", "RPCMOV", Vec("rpcmov_dstPc64__Const", "rpcmov_dstPc_const_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(sb)) => Some(Instruction.RpcmovConst(sb, modifiers, guard))
        case _ => None
      }
  }
  object RpcmovImm extends Definition("RpcmovImm", "RPCMOV", Vec("rpcmov_dstPc64__Imm", "rpcmov_dstPc_imm_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb)) => Some(Instruction.RpcmovImm(sb, modifiers, guard))
        case _ => None
      }
  }
  object RpcmovReg extends Definition(
        "RpcmovReg",
        "RPCMOV",
        Vec("rpcmov_dstPc64__URb", "rpcmov_dstPc_", "rpcmov_dstPc_URb_", "rpcmov_srcPc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcB)) => Some(Instruction.RpcmovReg(srcB, modifiers, guard))
        case _ => None
      }
  }
  object RroRegConst extends Definition("RroRegConst", "RRO", Vec("Const RRO", "Const_RRO")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(srcConst)) =>
          Some(Instruction.RroRegConst(dst, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object RroRegImm extends Definition("RroRegImm", "RRO", Vec("Imm RRO", "Imm_RRO")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(uImm)) =>
          Some(Instruction.RroRegImm(dst, uImm, modifiers, guard))
        case _ => None
      }
  }
  object RroRegReg extends Definition("RroRegReg", "RRO", Vec("RRO")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.RroRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object RttNoOperands extends Definition("RttNoOperands", "RTT", Vec("RTT", "rtt_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.RttNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object S2rRegSpecial extends Definition("S2rRegSpecial", "S2R", Vec("S2R", "s2r_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.SpecialRegister(sR)) =>
          Some(Instruction.S2rRegSpecial(dst, sR, modifiers, guard))
        case _ => None
      }
  }
  object S2urRegSpecial extends Definition("S2urRegSpecial", "S2UR", Vec("s2ur_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.SpecialRegister(sRa)) =>
          Some(Instruction.S2urRegSpecial(dst, sRa, modifiers, guard))
        case _ => None
      }
  }
  object SamNoOperands extends Definition("SamNoOperands", "SAM", Vec("SAM")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.SamNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object ScatterRegRegRegRegImmImm
      extends Definition("ScatterRegRegRegRegImmImm", "SCATTER", Vec("scatter_", "scatter_combined_idxtype_extract_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(vecidx),
              InstructionOperand.Immediate(mask)
            ) => Some(Instruction.ScatterRegRegRegRegImmImm(dst, srcA, srcB, srcC, vecidx, mask, modifiers, guard))
        case _ => None
      }
  }
  object SelRegRegAddressImmPred extends Definition("SelRegRegAddressImmPred", "SEL", Vec("sel__RCxR_RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.SelRegRegAddressImmPred(dst, srcA, srcB, sb_offset, pp, modifiers, guard))
        case _ => None
      }
  }
  object SelRegRegConstPred
      extends Definition("SelRegRegConstPred", "SEL", Vec("Const SEL", "Const_SEL", "sel__RCR_RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.SelRegRegConstPred(dst, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object SelRegRegImmPred
      extends Definition("SelRegRegImmPred", "SEL", Vec("Imm SEL", "Imm_SEL", "sel_64__RIR", "sel__RuIR_RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.SelRegRegImmPred(dst, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object SelRegRegRegPred extends Definition(
        "SelRegRegRegPred",
        "SEL",
        Vec("SEL", "sel_64__RRR", "sel_64__RUR", "sel__RRR_RRR", "sel__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.SelRegRegRegPred(dst, srcA, srcB, pa, modifiers, guard))
        case _ => None
      }
  }
  val definitions: Vec[Definition] = Vec(
    LeaRegRegRegImm,
    LeaRegRegRegImmImm,
    LeaRegRegRegImmImmPred,
    LeaRegRegRegImmPred,
    LeaRegRegRegReg,
    LeaRegRegRegRegImm,
    LeaRegRegRegRegImmPred,
    LepcReg,
    LepcRegImm,
    LongjmpNoOperands,
    LopPredRegRegAddressImm,
    LopPredRegRegAddressImmPred,
    LopPredRegRegConst,
    LopPredRegRegConstPred,
    LopPredRegRegImm,
    LopPredRegRegImmPred,
    LopPredRegRegReg,
    LopPredRegRegRegPred,
    LopRegRegAddressImm,
    LopRegRegAddressImmPred,
    LopRegRegConst,
    LopRegRegConstPred,
    LopRegRegImm,
    LopRegRegImmPred,
    LopRegRegReg,
    LopRegRegRegPred,
    Lop3PredRegRegAddressImmReg,
    Lop3PredRegRegAddressImmRegImm,
    Lop3PredRegRegAddressImmRegImmPred,
    Lop3PredRegRegAddressImmRegPred,
    Lop3PredRegRegConstReg,
    Lop3PredRegRegConstRegImm,
    Lop3PredRegRegConstRegImmPred,
    Lop3PredRegRegConstRegPred,
    Lop3PredRegRegImmReg,
    Lop3PredRegRegImmRegImm,
    Lop3PredRegRegImmRegImmPred,
    Lop3PredRegRegImmRegPred,
    Lop3PredRegRegRegReg,
    Lop3PredRegRegRegRegImm,
    Lop3PredRegRegRegRegImmPred,
    Lop3PredRegRegRegRegPred,
    Lop3RegRegAddressImmReg,
    Lop3RegRegAddressImmRegImm,
    Lop3RegRegAddressImmRegImmPred,
    Lop3RegRegAddressImmRegPred,
    Lop3RegRegConstReg,
    Lop3RegRegConstRegImm,
    Lop3RegRegConstRegImmPred,
    Lop3RegRegConstRegPred,
    Lop3RegRegImmReg,
    Lop3RegRegImmRegImm,
    Lop3RegRegImmRegImmPred,
    Lop3RegRegImmRegPred,
    Lop3RegRegRegReg,
    Lop3RegRegRegRegImm,
    Lop3RegRegRegRegImmPred,
    Lop3RegRegRegRegPred,
    Lop32iPredRegRegImm,
    Lop32iPredRegRegImmPred,
    Lop32iRegRegImm,
    Lop32iRegRegImmPred,
    MatchPredRegReg,
    MatchRegReg,
    MembarNoOperands,
    MovAddressConst,
    MovAddressConstImm,
    MovAddressImm,
    MovAddressImmImm,
    MovAddressReg,
    MovAddressRegImm,
    MovRegAddress,
    MovRegAddressImm,
    MovRegAddressImmImm,
    MovRegConst,
    MovRegConstImm,
    MovRegImm,
    MovRegImmImm,
    MovRegReg,
    MovRegRegImm,
    Mov32iRegImm,
    Mov32iRegImmImm,
    Mov64iurRegImm,
    Mov64iurRegReg,
    MovmRegReg,
    MufuRegAddressImm,
    MufuRegConst,
    MufuRegImm,
    MufuRegImmImm,
    MufuRegReg,
    NanosleepNoOperands,
    NanosleepAddressImm,
    NanosleepConst,
    NanosleepImm,
    NanosleepPred,
    NanosleepPredAddressImm,
    NanosleepPredConst,
    NanosleepPredImm,
    NanosleepPredReg,
    NanosleepReg,
    NanotrapConst,
    NanotrapImm,
    NanotrapPredConst,
    NanotrapPredImm,
    NanotrapPredReg,
    NanotrapReg,
    NopNoOperands,
    NopImm,
    NopSymbol,
    NopSymbolImm,
    OmmaRegRegRegRegPredRegRegReg,
    OmmaRegRegRegRegPredRegRegRegImm,
    OmmaRegRegRegRegRegRegReg,
    OmmaRegRegRegRegRegRegRegImm,
    OutReg,
    OutRegRegConst,
    OutRegRegImm,
    OutRegRegReg,
    P2rReg,
    P2rRegRegConst,
    P2rRegRegImm,
    P2rRegRegReg,
    P2rRegSymbol,
    P2rRegSymbolRegAddressImm,
    P2rRegSymbolRegConst,
    P2rRegSymbolRegImm,
    P2rRegSymbolRegReg,
    PbkConst,
    PbkImm,
    PcntConst,
    PcntImm,
    PexitImm,
    PixldReg,
    PixldRegAddress,
    PixldRegPred,
    PixldRegPredAddress,
    PlongjmpConst,
    PlongjmpImm,
    Plop3PredPredAddressImmPredImm,
    Plop3PredPredAddressImmRegImm,
    Plop3PredPredConstPredImm,
    Plop3PredPredConstRegImm,
    Plop3PredPredPredAddressImmPredImmImm,
    Plop3PredPredPredAddressImmRegImmImm,
    Plop3PredPredPredConstPredImmImm,
    Plop3PredPredPredConstRegImmImm,
    Plop3PredPredPredPred,
    Plop3PredPredPredPredImm,
    Plop3PredPredPredPredPredImmImm,
    Plop3PredPredPredRegPredImmImm,
    Plop3PredPredPredRegRegImmImm,
    Plop3PredPredRegAddressImmRegImmImm,
    Plop3PredPredRegConstRegImmImm,
    Plop3PredPredRegPredImm,
    Plop3PredPredRegRegImm,
    Plop3PredPredRegRegRegImmImm,
    Plop3PredRegAddressImmRegImm,
    Plop3PredRegConstRegImm,
    Plop3PredRegRegRegImm,
    PmtrigImm,
    PmtrigPredImm,
    PopcRegAddressImm,
    PopcRegConst,
    PopcRegImm,
    PopcRegReg,
    PopcRegRegConst,
    PopcRegRegImm,
    PopcRegRegReg,
    PreexitNoOperands,
    PretConst,
    PretImm,
    PrmtRegRegAddressImmReg,
    PrmtRegRegConstReg,
    PrmtRegRegImmReg,
    PrmtRegRegRegAddressImm,
    PrmtRegRegRegConst,
    PrmtRegRegRegImm,
    PrmtRegRegRegReg,
    PsetRegPredPred,
    PsetRegPredPredPred,
    PsetpPredPredPred,
    PsetpPredPredPredPredPred,
    Qadd4RegRegImm,
    Qadd4RegRegReg,
    Qfma4RegRegImmReg,
    Qfma4RegRegRegImm,
    Qfma4RegRegRegReg,
    QgmmaRegAddressReg,
    QgmmaRegAddressRegPred,
    QgmmaRegAddressRegPredReg,
    QgmmaRegAddressRegPredRegImm,
    QgmmaRegAddressRegReg,
    QgmmaRegAddressRegRegImm,
    QgmmaRegAddressRegRegPred,
    QgmmaRegAddressRegRegPredReg,
    QgmmaRegAddressRegRegPredRegImm,
    QgmmaRegAddressRegRegReg,
    QgmmaRegAddressRegRegRegImm,
    QgmmaRegRegAddressReg,
    QgmmaRegRegAddressRegPred,
    QgmmaRegRegAddressRegPredReg,
    QgmmaRegRegAddressRegPredRegImm,
    QgmmaRegRegAddressRegReg,
    QgmmaRegRegAddressRegRegImm,
    QmmaRegRegRegReg,
    QmmaRegRegRegRegPred,
    QmmaRegRegRegRegPredRegImm,
    QmmaRegRegRegRegPredRegRegReg,
    QmmaRegRegRegRegPredRegRegRegImm,
    QmmaRegRegRegRegRegImm,
    QmmaRegRegRegRegRegRegReg,
    QmmaRegRegRegRegRegRegRegImm,
    Qmul4RegRegImm,
    Qmul4RegRegReg,
    QspcPredAddress,
    QspcPredRegAddress,
    QspcRegAddress,
    R2bImmReg,
    R2pReg,
    R2pRegConst,
    R2pRegImm,
    R2pRegReg,
    R2pSymbolReg,
    R2pSymbolRegAddressImm,
    R2pSymbolRegConst,
    R2pSymbolRegImm,
    R2pSymbolRegReg,
    R2urPredRegReg,
    R2urRegReg,
    RamNoOperands,
    RedAddressReg,
    RedasAddressReg,
    RedgAddressReg,
    RedsAddressReg,
    ReduxRegReg,
    RetNoOperands,
    RetPredReg,
    RetPredRegImm,
    RetReg,
    RetRegImm,
    RpcmovAddressImm,
    RpcmovConst,
    RpcmovImm,
    RpcmovReg,
    RroRegConst,
    RroRegImm,
    RroRegReg,
    RttNoOperands,
    S2rRegSpecial,
    S2urRegSpecial,
    SamNoOperands,
    ScatterRegRegRegRegImmImm,
    SelRegRegAddressImmPred,
    SelRegRegConstPred,
    SelRegRegImmPred,
    SelRegRegRegPred
  )
}
