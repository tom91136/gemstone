// Generated SASS instruction definitions and checked operand constructors. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.sass
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import Instruction.*
@javax.annotation.processing.Generated(Array("sbt codegen"))
object InstructionDefinitions2 {
  object IgmmaRegAddressRegPredReg
      extends Definition("IgmmaRegAddressRegPredReg", "IGMMA", Vec("igmma_sparse_URa_Rc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Register(re)
            ) => Some(Instruction.IgmmaRegAddressRegPredReg(dst, srcA, srcC, uPp, re, modifiers, guard))
        case _ => None
      }
  }
  object IgmmaRegAddressRegPredRegImm
      extends Definition("IgmmaRegAddressRegPredRegImm", "IGMMA", Vec("igmma_sparse_URa_Rc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Register(re),
              InstructionOperand.Immediate(id)
            ) => Some(Instruction.IgmmaRegAddressRegPredRegImm(dst, srcA, srcC, uPp, re, id, modifiers, guard))
        case _ => None
      }
  }
  object IgmmaRegAddressRegReg
      extends Definition("IgmmaRegAddressRegReg", "IGMMA", Vec("igmma_URa_Rb_Rc_", "igmma_sparse_URa_Rc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.IgmmaRegAddressRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object IgmmaRegAddressRegRegImm extends Definition("IgmmaRegAddressRegRegImm", "IGMMA", Vec("igmma_sparse_URa_Rc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(re),
              InstructionOperand.Immediate(id)
            ) => Some(Instruction.IgmmaRegAddressRegRegImm(dst, srcA, srcC, re, id, modifiers, guard))
        case _ => None
      }
  }
  object IgmmaRegAddressRegRegPred extends Definition("IgmmaRegAddressRegRegPred", "IGMMA", Vec("igmma_URa_Rb_Rc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.IgmmaRegAddressRegRegPred(dst, srcA, srcB, srcC, uPp, modifiers, guard))
        case _ => None
      }
  }
  object IgmmaRegAddressRegRegPredReg
      extends Definition("IgmmaRegAddressRegRegPredReg", "IGMMA", Vec("igmma_sparse_URa_Rb_Rc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Register(re)
            ) => Some(Instruction.IgmmaRegAddressRegRegPredReg(dst, srcA, srcB, srcC, uPp, re, modifiers, guard))
        case _ => None
      }
  }
  object IgmmaRegAddressRegRegPredRegImm
      extends Definition("IgmmaRegAddressRegRegPredRegImm", "IGMMA", Vec("igmma_sparse_URa_Rb_Rc_")) {
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
            ) => Some(Instruction.IgmmaRegAddressRegRegPredRegImm(dst, srcA, srcB, srcC, uPp, re, id, modifiers, guard))
        case _ => None
      }
  }
  object IgmmaRegAddressRegRegReg
      extends Definition("IgmmaRegAddressRegRegReg", "IGMMA", Vec("igmma_sparse_URa_Rb_Rc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(re)
            ) => Some(Instruction.IgmmaRegAddressRegRegReg(dst, srcA, srcB, srcC, re, modifiers, guard))
        case _ => None
      }
  }
  object IgmmaRegAddressRegRegRegImm
      extends Definition("IgmmaRegAddressRegRegRegImm", "IGMMA", Vec("igmma_sparse_URa_Rb_Rc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(re),
              InstructionOperand.Immediate(id)
            ) => Some(Instruction.IgmmaRegAddressRegRegRegImm(dst, srcA, srcB, srcC, re, id, modifiers, guard))
        case _ => None
      }
  }
  object IgmmaRegRegAddressReg extends Definition("IgmmaRegRegAddressReg", "IGMMA", Vec("igmma_Ra_URb_Rc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.IgmmaRegRegAddressReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object IgmmaRegRegAddressRegPred extends Definition("IgmmaRegRegAddressRegPred", "IGMMA", Vec("igmma_Ra_URb_Rc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.IgmmaRegRegAddressRegPred(dst, srcA, srcB, srcC, uPp, modifiers, guard))
        case _ => None
      }
  }
  object IgmmaRegRegAddressRegPredReg
      extends Definition("IgmmaRegRegAddressRegPredReg", "IGMMA", Vec("igmma_sparse_Ra_URb_Rc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Register(re)
            ) => Some(Instruction.IgmmaRegRegAddressRegPredReg(dst, srcA, srcB, srcC, uPp, re, modifiers, guard))
        case _ => None
      }
  }
  object IgmmaRegRegAddressRegPredRegImm
      extends Definition("IgmmaRegRegAddressRegPredRegImm", "IGMMA", Vec("igmma_sparse_Ra_URb_Rc_")) {
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
            ) => Some(Instruction.IgmmaRegRegAddressRegPredRegImm(dst, srcA, srcB, srcC, uPp, re, id, modifiers, guard))
        case _ => None
      }
  }
  object IgmmaRegRegAddressRegReg
      extends Definition("IgmmaRegRegAddressRegReg", "IGMMA", Vec("igmma_sparse_Ra_URb_Rc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(re)
            ) => Some(Instruction.IgmmaRegRegAddressRegReg(dst, srcA, srcB, srcC, re, modifiers, guard))
        case _ => None
      }
  }
  object IgmmaRegRegAddressRegRegImm
      extends Definition("IgmmaRegRegAddressRegRegImm", "IGMMA", Vec("igmma_sparse_Ra_URb_Rc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(re),
              InstructionOperand.Immediate(id)
            ) => Some(Instruction.IgmmaRegRegAddressRegRegImm(dst, srcA, srcB, srcC, re, id, modifiers, guard))
        case _ => None
      }
  }
  object ImadRegPredRegAddressImmReg extends Definition(
        "ImadRegPredRegAddressImmReg",
        "IMAD",
        Vec("imad_hi__RCxR_RCxR", "imad_hi_pseudo__RCxR_RCxR", "imad_wide__RCxR_RCxR", "imad_wide_pseudo__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.ImadRegPredRegAddressImmReg(dst, pu, srcA, srcB, sb_offset, srcC, modifiers, guard))
        case _ => None
      }
  }
  object ImadRegPredRegAddressImmRegPred extends Definition(
        "ImadRegPredRegAddressImmRegPred",
        "IMAD",
        Vec(
          "imad_hi_x__RCxR_RCxR",
          "imad_hi_x_pseudo__RCxR_RCxR",
          "imad_wide_x__RCxR_RCxR",
          "imad_wide_x_pseudo__RCxR_RCxR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) =>
          Some(Instruction.ImadRegPredRegAddressImmRegPred(dst, pu, srcA, srcB, sb_offset, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object ImadRegPredRegConstReg extends Definition(
        "ImadRegPredRegConstReg",
        "IMAD",
        Vec("imad_hi__RCR_RCR", "imad_hi_pseudo__RCR_RCR", "imad_wide__RCR_RCR", "imad_wide_pseudo__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.ImadRegPredRegConstReg(dst, pu, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object ImadRegPredRegConstRegPred extends Definition(
        "ImadRegPredRegConstRegPred",
        "IMAD",
        Vec("imad_hi_x__RCR_RCR", "imad_hi_x_pseudo__RCR_RCR", "imad_wide_x__RCR_RCR", "imad_wide_x_pseudo__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.ImadRegPredRegConstRegPred(dst, pu, srcA, sb, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object ImadRegPredRegImmReg extends Definition(
        "ImadRegPredRegImmReg",
        "IMAD",
        Vec("imad_hi__RsIR_RIR", "imad_hi_pseudo__RsIR_RIR", "imad_wide__RsIR_RIR", "imad_wide_pseudo__RsIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.ImadRegPredRegImmReg(dst, pu, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object ImadRegPredRegImmRegPred extends Definition(
        "ImadRegPredRegImmRegPred",
        "IMAD",
        Vec(
          "imad_hi_x__RsIR_RIR",
          "imad_hi_x_pseudo__RsIR_RIR",
          "imad_wide_x__RsIR_RIR",
          "imad_wide_x_pseudo__RsIR_RIR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.ImadRegPredRegImmRegPred(dst, pu, srcA, sb, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object ImadRegPredRegRegAddressImm extends Definition(
        "ImadRegPredRegRegAddressImm",
        "IMAD",
        Vec("imad_hi__RRCx_RRCx", "imad_hi_pseudo__RRCx_RRCx", "imad_wide__RRCx_RRCx", "imad_wide_pseudo__RRCx_RRCx")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Address(srcC),
              InstructionOperand.Immediate(sc_offset)
            ) => Some(Instruction.ImadRegPredRegRegAddressImm(dst, pu, srcA, srcB, srcC, sc_offset, modifiers, guard))
        case _ => None
      }
  }
  object ImadRegPredRegRegAddressImmPred extends Definition(
        "ImadRegPredRegRegAddressImmPred",
        "IMAD",
        Vec(
          "imad_hi_x__RRCx_RRCx",
          "imad_hi_x_pseudo__RRCx_RRCx",
          "imad_wide_x__RRCx_RRCx",
          "imad_wide_x_pseudo__RRCx_RRCx"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Address(srcC),
              InstructionOperand.Immediate(sc_offset),
              InstructionOperand.Predicate(pp)
            ) =>
          Some(Instruction.ImadRegPredRegRegAddressImmPred(dst, pu, srcA, srcB, srcC, sc_offset, pp, modifiers, guard))
        case _ => None
      }
  }
  object ImadRegPredRegRegConst extends Definition(
        "ImadRegPredRegRegConst",
        "IMAD",
        Vec("imad_hi__RRC_RRC", "imad_hi_pseudo__RRC_RRC", "imad_wide__RRC_RRC", "imad_wide_pseudo__RRC_RRC")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(sc)
            ) => Some(Instruction.ImadRegPredRegRegConst(dst, pu, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object ImadRegPredRegRegConstPred extends Definition(
        "ImadRegPredRegRegConstPred",
        "IMAD",
        Vec("imad_hi_x__RRC_RRC", "imad_hi_x_pseudo__RRC_RRC", "imad_wide_x__RRC_RRC", "imad_wide_x_pseudo__RRC_RRC")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(sc),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.ImadRegPredRegRegConstPred(dst, pu, srcA, srcB, sc, pp, modifiers, guard))
        case _ => None
      }
  }
  object ImadRegPredRegRegReg extends Definition(
        "ImadRegPredRegRegReg",
        "IMAD",
        Vec(
          "imad_hi__RRR_RRR",
          "imad_hi__RRU_RRU",
          "imad_hi__RUR_RUR",
          "imad_hi_pseudo__RRR_RRR",
          "imad_hi_pseudo__RRU_RRU",
          "imad_hi_pseudo__RUR_RUR",
          "imad_wide__RRR_RRR",
          "imad_wide__RRU_RRU",
          "imad_wide__RUR_RUR",
          "imad_wide_pseudo__RRR_RRR",
          "imad_wide_pseudo__RRU_RRU",
          "imad_wide_pseudo__RUR_RUR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.ImadRegPredRegRegReg(dst, pu, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object ImadRegPredRegRegRegPred extends Definition(
        "ImadRegPredRegRegRegPred",
        "IMAD",
        Vec(
          "imad_hi_x__RRR_RRR",
          "imad_hi_x__RRU_RRU",
          "imad_hi_x__RUR_RUR",
          "imad_hi_x_pseudo__RRR_RRR",
          "imad_hi_x_pseudo__RRU_RRU",
          "imad_hi_x_pseudo__RUR_RUR",
          "imad_wide_x__RRR_RRR",
          "imad_wide_x__RRU_RRU",
          "imad_wide_x__RUR_RUR",
          "imad_wide_x_pseudo__RRR_RRR",
          "imad_wide_x_pseudo__RRU_RRU",
          "imad_wide_x_pseudo__RUR_RUR"
        )
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
            ) => Some(Instruction.ImadRegPredRegRegRegPred(dst, pu, srcA, srcB, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object ImadRegRegAddressImmReg extends Definition(
        "ImadRegRegAddressImmReg",
        "IMAD",
        Vec(
          "imad__RCxR_RCxR",
          "imad_hi__RCxR_RCxR",
          "imad_hi_pseudo__RCxR_RCxR",
          "imad_pseudo__RCxR_RCxR",
          "imad_wide__RCxR_RCxR",
          "imad_wide_pseudo__RCxR_RCxR"
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
            ) => Some(Instruction.ImadRegRegAddressImmReg(dst, srcA, srcB, sb_offset, srcC, modifiers, guard))
        case _ => None
      }
  }
  object ImadRegRegAddressImmRegPred extends Definition(
        "ImadRegRegAddressImmRegPred",
        "IMAD",
        Vec(
          "imad_hi_x__RCxR_RCxR",
          "imad_hi_x_pseudo__RCxR_RCxR",
          "imad_wide_x__RCxR_RCxR",
          "imad_wide_x_pseudo__RCxR_RCxR",
          "imad_x__RCxR_RCxR",
          "imad_x_pseudo__RCxR_RCxR"
        )
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
            ) => Some(Instruction.ImadRegRegAddressImmRegPred(dst, srcA, srcB, sb_offset, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object ImadRegRegConstReg extends Definition(
        "ImadRegRegConstReg",
        "IMAD",
        Vec(
          "Const IMAD",
          "Const_IMAD",
          "imad__RCR_RCR",
          "imad_hi__RCR_RCR",
          "imad_hi_pseudo__RCR_RCR",
          "imad_pseudo__RCR_RCR",
          "imad_wide__RCR_RCR",
          "imad_wide_pseudo__RCR_RCR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.ImadRegRegConstReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object ImadRegRegConstRegPred extends Definition(
        "ImadRegRegConstRegPred",
        "IMAD",
        Vec(
          "imad_hi_x__RCR_RCR",
          "imad_hi_x_pseudo__RCR_RCR",
          "imad_wide_x__RCR_RCR",
          "imad_wide_x_pseudo__RCR_RCR",
          "imad_x__RCR_RCR",
          "imad_x_pseudo__RCR_RCR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.ImadRegRegConstRegPred(dst, srcA, sb, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object ImadRegRegImmReg extends Definition(
        "ImadRegRegImmReg",
        "IMAD",
        Vec(
          "Imm IMAD",
          "Imm_IMAD",
          "imad__RsIR_RIR",
          "imad_hi__RsIR_RIR",
          "imad_hi_pseudo__RsIR_RIR",
          "imad_pseudo__RsIR_RIR",
          "imad_wide__RsIR_RIR",
          "imad_wide_pseudo__RsIR_RIR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.ImadRegRegImmReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object ImadRegRegImmRegPred extends Definition(
        "ImadRegRegImmRegPred",
        "IMAD",
        Vec(
          "imad_hi_x__RsIR_RIR",
          "imad_hi_x_pseudo__RsIR_RIR",
          "imad_wide_x__RsIR_RIR",
          "imad_wide_x_pseudo__RsIR_RIR",
          "imad_x__RsIR_RIR",
          "imad_x_pseudo__RsIR_RIR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.ImadRegRegImmRegPred(dst, srcA, sb, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object ImadRegRegRegAddressImm extends Definition(
        "ImadRegRegRegAddressImm",
        "IMAD",
        Vec(
          "imad__RRCx_RRCx",
          "imad_hi__RRCx_RRCx",
          "imad_hi_pseudo__RRCx_RRCx",
          "imad_pseudo__RRCx_RRCx",
          "imad_wide__RRCx_RRCx",
          "imad_wide_pseudo__RRCx_RRCx"
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
            ) => Some(Instruction.ImadRegRegRegAddressImm(dst, srcA, srcB, srcC, sc_offset, modifiers, guard))
        case _ => None
      }
  }
  object ImadRegRegRegAddressImmPred extends Definition(
        "ImadRegRegRegAddressImmPred",
        "IMAD",
        Vec(
          "imad_hi_x__RRCx_RRCx",
          "imad_hi_x_pseudo__RRCx_RRCx",
          "imad_wide_x__RRCx_RRCx",
          "imad_wide_x_pseudo__RRCx_RRCx",
          "imad_x__RRCx_RRCx",
          "imad_x_pseudo__RRCx_RRCx"
        )
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
            ) => Some(Instruction.ImadRegRegRegAddressImmPred(dst, srcA, srcB, srcC, sc_offset, pp, modifiers, guard))
        case _ => None
      }
  }
  object ImadRegRegRegConst extends Definition(
        "ImadRegRegRegConst",
        "IMAD",
        Vec(
          "Const1 IMAD",
          "Const1_IMAD",
          "imad__RRC_RRC",
          "imad_hi__RRC_RRC",
          "imad_hi_pseudo__RRC_RRC",
          "imad_pseudo__RRC_RRC",
          "imad_wide__RRC_RRC",
          "imad_wide_pseudo__RRC_RRC"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(sc)
            ) => Some(Instruction.ImadRegRegRegConst(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object ImadRegRegRegConstPred extends Definition(
        "ImadRegRegRegConstPred",
        "IMAD",
        Vec(
          "imad_hi_x__RRC_RRC",
          "imad_hi_x_pseudo__RRC_RRC",
          "imad_wide_x__RRC_RRC",
          "imad_wide_x_pseudo__RRC_RRC",
          "imad_x__RRC_RRC",
          "imad_x_pseudo__RRC_RRC"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(sc),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.ImadRegRegRegConstPred(dst, srcA, srcB, sc, pp, modifiers, guard))
        case _ => None
      }
  }
  object ImadRegRegRegImm
      extends Definition("ImadRegRegRegImm", "IMAD", Vec("imad__RRsI_RRI", "imad_pseudo__RRsI_RRI")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.ImadRegRegRegImm(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object ImadRegRegRegImmPred
      extends Definition("ImadRegRegRegImmPred", "IMAD", Vec("imad_x__RRsI_RRI", "imad_x_pseudo__RRsI_RRI")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.ImadRegRegRegImmPred(dst, srcA, srcB, sc, pp, modifiers, guard))
        case _ => None
      }
  }
  object ImadRegRegRegReg extends Definition(
        "ImadRegRegRegReg",
        "IMAD",
        Vec(
          "IMAD",
          "imad__RRR_RRR",
          "imad__RRU_RRU",
          "imad__RUR_RUR",
          "imad_hi__RRR_RRR",
          "imad_hi__RRU_RRU",
          "imad_hi__RUR_RUR",
          "imad_hi_pseudo__RRR_RRR",
          "imad_hi_pseudo__RRU_RRU",
          "imad_hi_pseudo__RUR_RUR",
          "imad_pseudo__RRR_RRR",
          "imad_pseudo__RRU_RRU",
          "imad_pseudo__RUR_RUR",
          "imad_wide__RRR_RRR",
          "imad_wide__RRU_RRU",
          "imad_wide__RUR_RUR",
          "imad_wide_pseudo__RRR_RRR",
          "imad_wide_pseudo__RRU_RRU",
          "imad_wide_pseudo__RUR_RUR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.ImadRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object ImadRegRegRegRegPred extends Definition(
        "ImadRegRegRegRegPred",
        "IMAD",
        Vec(
          "imad_hi_x__RRR_RRR",
          "imad_hi_x__RRU_RRU",
          "imad_hi_x__RUR_RUR",
          "imad_hi_x_pseudo__RRR_RRR",
          "imad_hi_x_pseudo__RRU_RRU",
          "imad_hi_x_pseudo__RUR_RUR",
          "imad_wide_x__RRR_RRR",
          "imad_wide_x__RRU_RRU",
          "imad_wide_x__RUR_RUR",
          "imad_wide_x_pseudo__RRR_RRR",
          "imad_wide_x_pseudo__RRU_RRU",
          "imad_wide_x_pseudo__RUR_RUR",
          "imad_x__RRR_RRR",
          "imad_x__RRU_RRU",
          "imad_x__RUR_RUR",
          "imad_x_pseudo__RRR_RRR",
          "imad_x_pseudo__RRU_RRU",
          "imad_x_pseudo__RUR_RUR"
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
            ) => Some(Instruction.ImadRegRegRegRegPred(dst, srcA, srcB, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object Imad32iRegRegImmReg extends Definition("Imad32iRegRegImmReg", "IMAD32I", Vec("IMAD32I")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Imad32iRegRegImmReg(dst, srcA, sImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object ImadspRegRegConstReg
      extends Definition("ImadspRegRegConstReg", "IMADSP", Vec("Const IMADSP", "Const IMADSP SD", "Const_IMADSP")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.ImadspRegRegConstReg(dst, srcA, srcConst, srcC, modifiers, guard))
        case _ => None
      }
  }
  object ImadspRegRegImmReg
      extends Definition("ImadspRegRegImmReg", "IMADSP", Vec("Imm IMADSP", "Imm IMADSP SD", "Imm_IMADSP")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.ImadspRegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object ImadspRegRegRegConst
      extends Definition("ImadspRegRegRegConst", "IMADSP", Vec("Const1 IMADSP", "Const1 IMADSP SD", "Const1_IMADSP")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.ImadspRegRegRegConst(dst, srcA, srcB, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object ImadspRegRegRegReg extends Definition("ImadspRegRegRegReg", "IMADSP", Vec("IMADSP", "IMADSP SD")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.ImadspRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object ImmaRegRegRegReg extends Definition(
        "ImmaRegRegRegReg",
        "IMMA",
        Vec("imma_", "imma_8816_16_8_", "imma_8816_8_8_", "imma_8832_4_4_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.ImmaRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object ImmaRegRegRegRegPred extends Definition("ImmaRegRegRegRegPred", "IMMA", Vec("imma_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.ImmaRegRegRegRegPred(dst, srcA, srcB, srcC, uPp, modifiers, guard))
        case _ => None
      }
  }
  object ImmaRegRegRegRegPredRegImm extends Definition("ImmaRegRegRegRegPredRegImm", "IMMA", Vec("imma_sp_")) {
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
            ) => Some(Instruction.ImmaRegRegRegRegPredRegImm(dst, srcA, srcB, srcC, uPp, re, id, modifiers, guard))
        case _ => None
      }
  }
  object ImmaRegRegRegRegRegImm extends Definition("ImmaRegRegRegRegRegImm", "IMMA", Vec("imma_sp_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(re),
              InstructionOperand.Immediate(id)
            ) => Some(Instruction.ImmaRegRegRegRegRegImm(dst, srcA, srcB, srcC, re, id, modifiers, guard))
        case _ => None
      }
  }
  object ImnmxPredPredRegRegImmPred extends Definition(
        "ImnmxPredPredRegRegImmPred",
        "IMNMX",
        Vec("imnmx_64_nopred__RIR_RsIR", "imnmx_nopred__RIR_RsIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.ImnmxPredPredRegRegImmPred(pu, pv, dst, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object ImnmxPredPredRegRegImmPredPred
      extends Definition("ImnmxPredPredRegRegImmPredPred", "IMNMX", Vec("imnmx_64__RIR_RsIR", "imnmx__RIR_RsIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Predicate(pq)
            ) => Some(Instruction.ImnmxPredPredRegRegImmPredPred(pu, pv, dst, srcA, sb, pp, pq, modifiers, guard))
        case _ => None
      }
  }
  object ImnmxPredPredRegRegRegPred extends Definition(
        "ImnmxPredPredRegRegRegPred",
        "IMNMX",
        Vec("imnmx_64_nopred__RRR_RRR", "imnmx_64_nopred__RUR_RUR", "imnmx_nopred__RRR_RRR", "imnmx_nopred__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.ImnmxPredPredRegRegRegPred(pu, pv, dst, srcA, srcB, pp, modifiers, guard))
        case _ => None
      }
  }
  object ImnmxPredPredRegRegRegPredPred extends Definition(
        "ImnmxPredPredRegRegRegPredPred",
        "IMNMX",
        Vec("imnmx_64__RRR_RRR", "imnmx_64__RUR_RUR", "imnmx__RRR_RRR", "imnmx__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Predicate(pq)
            ) => Some(Instruction.ImnmxPredPredRegRegRegPredPred(pu, pv, dst, srcA, srcB, pp, pq, modifiers, guard))
        case _ => None
      }
  }
  object ImnmxPredRegRegImmPred extends Definition(
        "ImnmxPredRegRegImmPred",
        "IMNMX",
        Vec("imnmx_64_nopred__RIR_RsIR", "imnmx_nopred__RIR_RsIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.ImnmxPredRegRegImmPred(pu, dst, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object ImnmxPredRegRegImmPredPred
      extends Definition("ImnmxPredRegRegImmPredPred", "IMNMX", Vec("imnmx_64__RIR_RsIR", "imnmx__RIR_RsIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Predicate(pq)
            ) => Some(Instruction.ImnmxPredRegRegImmPredPred(pu, dst, srcA, sb, pp, pq, modifiers, guard))
        case _ => None
      }
  }
  object ImnmxPredRegRegRegPred extends Definition(
        "ImnmxPredRegRegRegPred",
        "IMNMX",
        Vec("imnmx_64_nopred__RRR_RRR", "imnmx_64_nopred__RUR_RUR", "imnmx_nopred__RRR_RRR", "imnmx_nopred__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.ImnmxPredRegRegRegPred(pu, dst, srcA, srcB, pp, modifiers, guard))
        case _ => None
      }
  }
  object ImnmxPredRegRegRegPredPred extends Definition(
        "ImnmxPredRegRegRegPredPred",
        "IMNMX",
        Vec("imnmx_64__RRR_RRR", "imnmx_64__RUR_RUR", "imnmx__RRR_RRR", "imnmx__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Predicate(pq)
            ) => Some(Instruction.ImnmxPredRegRegRegPredPred(pu, dst, srcA, srcB, pp, pq, modifiers, guard))
        case _ => None
      }
  }
  object ImnmxRegRegAddressImmPred extends Definition("ImnmxRegRegAddressImmPred", "IMNMX", Vec("imnmx__RCxR_RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.ImnmxRegRegAddressImmPred(dst, srcA, srcB, sb_offset, pp, modifiers, guard))
        case _ => None
      }
  }
  object ImnmxRegRegConstPred
      extends Definition("ImnmxRegRegConstPred", "IMNMX", Vec("Const IMNMX", "Const_IMNMX", "imnmx__RCR_RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.ImnmxRegRegConstPred(dst, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object ImnmxRegRegImmPred extends Definition(
        "ImnmxRegRegImmPred",
        "IMNMX",
        Vec(
          "Imm IMNMX",
          "Imm_IMNMX",
          "imnmx_64_nopred__RIR_RsIR",
          "imnmx__RIR_RsIR",
          "imnmx__RsIR_RIR",
          "imnmx_nopred__RIR_RsIR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.ImnmxRegRegImmPred(dst, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object ImnmxRegRegImmPredPred
      extends Definition("ImnmxRegRegImmPredPred", "IMNMX", Vec("imnmx_64__RIR_RsIR", "imnmx__RIR_RsIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Predicate(pq)
            ) => Some(Instruction.ImnmxRegRegImmPredPred(dst, srcA, sb, pp, pq, modifiers, guard))
        case _ => None
      }
  }
  object ImnmxRegRegRegPred extends Definition(
        "ImnmxRegRegRegPred",
        "IMNMX",
        Vec(
          "IMNMX",
          "imnmx_64_nopred__RRR_RRR",
          "imnmx_64_nopred__RUR_RUR",
          "imnmx__RRR_RRR",
          "imnmx__RUR_RUR",
          "imnmx_nopred__RRR_RRR",
          "imnmx_nopred__RUR_RUR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.ImnmxRegRegRegPred(dst, srcA, srcB, pa, modifiers, guard))
        case _ => None
      }
  }
  object ImnmxRegRegRegPredPred extends Definition(
        "ImnmxRegRegRegPredPred",
        "IMNMX",
        Vec("imnmx_64__RRR_RRR", "imnmx_64__RUR_RUR", "imnmx__RRR_RRR", "imnmx__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Predicate(pq)
            ) => Some(Instruction.ImnmxRegRegRegPredPred(dst, srcA, srcB, pp, pq, modifiers, guard))
        case _ => None
      }
  }
  object ImulRegPredRegAddressImm extends Definition("ImulRegPredRegAddressImm", "IMUL", Vec("imul_wide__RCxR_RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.ImulRegPredRegAddressImm(dst, pu, srcA, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object ImulRegPredRegConst extends Definition("ImulRegPredRegConst", "IMUL", Vec("imul_wide__RCR_RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb)
            ) => Some(Instruction.ImulRegPredRegConst(dst, pu, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object ImulRegPredRegImm extends Definition("ImulRegPredRegImm", "IMUL", Vec("imul_wide__RsIR_RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.ImulRegPredRegImm(dst, pu, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object ImulRegPredRegReg
      extends Definition("ImulRegPredRegReg", "IMUL", Vec("imul_wide__RRR_RRR", "imul_wide__RUR_RUR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.ImulRegPredRegReg(dst, pu, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object ImulRegRegAddressImm
      extends Definition("ImulRegRegAddressImm", "IMUL", Vec("imul__RCxR_RCxR", "imul_wide__RCxR_RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.ImulRegRegAddressImm(dst, srcA, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object ImulRegRegConst extends Definition(
        "ImulRegRegConst",
        "IMUL",
        Vec("Const IMUL", "Const_IMUL", "imul__RCR_RCR", "imul_wide__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb)
            ) => Some(Instruction.ImulRegRegConst(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object ImulRegRegImm extends Definition(
        "ImulRegRegImm",
        "IMUL",
        Vec("Imm IMUL", "Imm_IMUL", "imul__RsIR_RIR", "imul_wide__RsIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.ImulRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object ImulRegRegImmReg
      extends Definition("ImulRegRegImmReg", "IMUL", Vec("imul_lo_xor__RsIR_RIR", "imul_wide_xor__RsIR_RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.ImulRegRegImmReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object ImulRegRegReg extends Definition(
        "ImulRegRegReg",
        "IMUL",
        Vec("IMUL", "imul__RRR_RRR", "imul__RUR_RUR", "imul_wide__RRR_RRR", "imul_wide__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.ImulRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object ImulRegRegRegImm extends Definition("ImulRegRegRegImm", "IMUL", Vec("imul_lo_xor__RRsI_RRI")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.ImulRegRegRegImm(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object ImulRegRegRegReg extends Definition(
        "ImulRegRegRegReg",
        "IMUL",
        Vec(
          "imul_lo_xor__RRR_RRR",
          "imul_lo_xor__RRU_RRU",
          "imul_lo_xor__RUR_RUR",
          "imul_wide_xor__RRR_RRR",
          "imul_wide_xor__RRU_RRU",
          "imul_wide_xor__RUR_RUR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.ImulRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Imul32iRegPredRegImm extends Definition("Imul32iRegPredRegImm", "IMUL32I", Vec("imul32i_wide__RsIR_RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.Imul32iRegPredRegImm(dst, pu, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object Imul32iRegRegImm extends Definition(
        "Imul32iRegRegImm",
        "IMUL32I",
        Vec("IMUL32I", "imul32i_lo__RsIR_RIR", "imul32i_wide__RsIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.Imul32iRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object IpaRegAddress extends Definition("IpaRegAddress", "IPA", Vec("I IPA 2", "IPA 2", "ipa_ur_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Address(srcA)) =>
          Some(Instruction.IpaRegAddress(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object IpaRegAddressImm extends Definition("IpaRegAddressImm", "IPA", Vec("ipa_ur_offset__IPA_URa_Ib")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.IpaRegAddressImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object IpaRegAddressPred extends Definition("IpaRegAddressPred", "IPA", Vec("IPA_2")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Predicate(ps)
            ) => Some(Instruction.IpaRegAddressPred(dst, srcA, ps, modifiers, guard))
        case _ => None
      }
  }
  object IpaRegAddressReg extends Definition("IpaRegAddressReg", "IPA", Vec("ipa_ur_offset__IPA_URa_Rb")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.IpaRegAddressReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object IpaRegAddressRegReg extends Definition("IpaRegAddressRegReg", "IPA", Vec("I IPA 1", "IPA 1")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.IpaRegAddressRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object IpaRegAddressRegRegPred extends Definition("IpaRegAddressRegRegPred", "IPA", Vec("IPA_1")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(ps)
            ) => Some(Instruction.IpaRegAddressRegRegPred(dst, srcA, srcB, srcC, ps, modifiers, guard))
        case _ => None
      }
  }
  object IpaRegImm extends Definition("IpaRegImm", "IPA", Vec("I_IPA_2", "ipa_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(attr)) =>
          Some(Instruction.IpaRegImm(dst, attr, modifiers, guard))
        case _ => None
      }
  }
  object IpaRegImmConst extends Definition("IpaRegImmConst", "IPA", Vec("ipa_offset__IPA_C")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Immediate(attr),
              InstructionOperand.ConstantBank(sb)
            ) => Some(Instruction.IpaRegImmConst(dst, attr, sb, modifiers, guard))
        case _ => None
      }
  }
  object IpaRegImmImm extends Definition("IpaRegImmImm", "IPA", Vec("ipa_offset__IPA_Ib", "ipa_offset__IPA_Imm")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Immediate(attr),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.IpaRegImmImm(dst, attr, sb, modifiers, guard))
        case _ => None
      }
  }
  object IpaRegImmPred extends Definition("IpaRegImmPred", "IPA", Vec("I_IPA_2")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Predicate(ps)
            ) => Some(Instruction.IpaRegImmPred(dst, uImm, ps, modifiers, guard))
        case _ => None
      }
  }
  object IpaRegImmReg extends Definition("IpaRegImmReg", "IPA", Vec("ipa_offset__IPA_Rb")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Immediate(attr),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.IpaRegImmReg(dst, attr, srcB, modifiers, guard))
        case _ => None
      }
  }
  object IpaRegImmRegReg extends Definition("IpaRegImmRegReg", "IPA", Vec("I_IPA_1")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.IpaRegImmRegReg(dst, uImm, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object IpaRegImmRegRegPred extends Definition("IpaRegImmRegRegPred", "IPA", Vec("I_IPA_1")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(ps)
            ) => Some(Instruction.IpaRegImmRegRegPred(dst, uImm, srcB, srcC, ps, modifiers, guard))
        case _ => None
      }
  }
  object IpaRegPredAddress extends Definition("IpaRegPredAddress", "IPA", Vec("ipa_ur_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Address(srcA)
            ) => Some(Instruction.IpaRegPredAddress(dst, pu, srcA, modifiers, guard))
        case _ => None
      }
  }
  object IpaRegPredAddressImm extends Definition("IpaRegPredAddressImm", "IPA", Vec("ipa_ur_offset__IPA_URa_Ib")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Address(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.IpaRegPredAddressImm(dst, pu, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object IpaRegPredAddressReg extends Definition("IpaRegPredAddressReg", "IPA", Vec("ipa_ur_offset__IPA_URa_Rb")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.IpaRegPredAddressReg(dst, pu, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object IpaRegPredImm extends Definition("IpaRegPredImm", "IPA", Vec("ipa_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Immediate(attr)
            ) => Some(Instruction.IpaRegPredImm(dst, pu, attr, modifiers, guard))
        case _ => None
      }
  }
  object IpaRegPredImmConst extends Definition("IpaRegPredImmConst", "IPA", Vec("ipa_offset__IPA_C")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Immediate(attr),
              InstructionOperand.ConstantBank(sb)
            ) => Some(Instruction.IpaRegPredImmConst(dst, pu, attr, sb, modifiers, guard))
        case _ => None
      }
  }
  object IpaRegPredImmImm
      extends Definition("IpaRegPredImmImm", "IPA", Vec("ipa_offset__IPA_Ib", "ipa_offset__IPA_Imm")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Immediate(attr),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.IpaRegPredImmImm(dst, pu, attr, sb, modifiers, guard))
        case _ => None
      }
  }
  object IpaRegPredImmReg extends Definition("IpaRegPredImmReg", "IPA", Vec("ipa_offset__IPA_Rb")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Immediate(attr),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.IpaRegPredImmReg(dst, pu, attr, srcB, modifiers, guard))
        case _ => None
      }
  }
  object IsadRegRegConstReg extends Definition("IsadRegRegConstReg", "ISAD", Vec("Const ISAD")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.IsadRegRegConstReg(dst, srcA, srcConst, srcC, modifiers, guard))
        case _ => None
      }
  }
  object IsadRegRegImmReg extends Definition("IsadRegRegImmReg", "ISAD", Vec("Imm ISAD")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.IsadRegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object IsadRegRegRegConst extends Definition("IsadRegRegRegConst", "ISAD", Vec("Const1 ISAD")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.IsadRegRegRegConst(dst, srcA, srcB, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object IsadRegRegRegReg extends Definition("IsadRegRegRegReg", "ISAD", Vec("ISAD")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.IsadRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object IsberdRegAddress extends Definition("IsberdRegAddress", "ISBERD", Vec("ISBERD", "isberd_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Address(srcA)) =>
          Some(Instruction.IsberdRegAddress(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object IsbewrAddressReg extends Definition("IsbewrAddressReg", "ISBEWR", Vec("isbewr_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA), InstructionOperand.Register(srcB)) =>
          Some(Instruction.IsbewrAddressReg(srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object IscaddRegPredRegAddressImmImm
      extends Definition("IscaddRegPredRegAddressImmImm", "ISCADD", Vec("iscadd_noimm__RCxR_RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Immediate(scaleU5)
            ) =>
          Some(Instruction.IscaddRegPredRegAddressImmImm(dst, pu, srcA, srcB, sb_offset, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object IscaddRegPredRegConstImm
      extends Definition("IscaddRegPredRegConstImm", "ISCADD", Vec("iscadd_noimm__RCR_RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.IscaddRegPredRegConstImm(dst, pu, srcA, sb, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object IscaddRegPredRegImmImm extends Definition("IscaddRegPredRegImmImm", "ISCADD", Vec("iscadd_imm__RIR_RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.IscaddRegPredRegImmImm(dst, pu, srcA, sb, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object IscaddRegPredRegRegImm
      extends Definition("IscaddRegPredRegRegImm", "ISCADD", Vec("iscadd_noimm__RRR_RRR", "iscadd_noimm__RUR_RUR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.IscaddRegPredRegRegImm(dst, pu, srcA, srcB, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object IscaddRegRegAddressImmImm
      extends Definition("IscaddRegRegAddressImmImm", "ISCADD", Vec("iscadd_noimm__RCxR_RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.IscaddRegRegAddressImmImm(dst, srcA, srcB, sb_offset, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object IscaddRegRegConstImm extends Definition(
        "IscaddRegRegConstImm",
        "ISCADD",
        Vec("Const ISCADD", "Const_ISCADD", "iscadd_noimm__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.IscaddRegRegConstImm(dst, srcA, sb, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object IscaddRegRegImmImm
      extends Definition("IscaddRegRegImmImm", "ISCADD", Vec("Imm ISCADD", "Imm_ISCADD", "iscadd_imm__RIR_RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.IscaddRegRegImmImm(dst, srcA, sb, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object IscaddRegRegRegImm extends Definition(
        "IscaddRegRegRegImm",
        "ISCADD",
        Vec("ISCADD", "iscadd_noimm__RRR_RRR", "iscadd_noimm__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.IscaddRegRegRegImm(dst, srcA, srcB, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object Iscadd32iRegPredRegImmImm
      extends Definition("Iscadd32iRegPredRegImmImm", "ISCADD32I", Vec("iscadd32i__RIR_RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.Iscadd32iRegPredRegImmImm(dst, pu, srcA, sb, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object Iscadd32iRegRegImmImm
      extends Definition("Iscadd32iRegRegImmImm", "ISCADD32I", Vec("ISCADD32I", "iscadd32i__RIR_RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.Iscadd32iRegRegImmImm(dst, srcA, sb, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object IsetRegRegConst extends Definition(
        "IsetRegRegConst",
        "ISET",
        Vec("NoBop Const ISET", "NoBop Const ISET U", "NoBop_Const_ISET", "NoBop_Const_ISET_U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.IsetRegRegConst(dst, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object IsetRegRegConstPred extends Definition(
        "IsetRegRegConstPred",
        "ISET",
        Vec("Const ISET", "Const ISET U", "Const_ISET", "Const_ISET_U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.IsetRegRegConstPred(dst, srcA, srcConst, pa, modifiers, guard))
        case _ => None
      }
  }
  object IsetRegRegImm extends Definition(
        "IsetRegRegImm",
        "ISET",
        Vec("NoBop Imm ISET", "NoBop Imm ISET U", "NoBop_Imm_ISET", "NoBop_Imm_ISET_U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.IsetRegRegImm(dst, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object IsetRegRegImmPred
      extends Definition("IsetRegRegImmPred", "ISET", Vec("Imm ISET", "Imm ISET U", "Imm_ISET", "Imm_ISET_U")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.IsetRegRegImmPred(dst, srcA, uImm, pa, modifiers, guard))
        case _ => None
      }
  }
  object IsetRegRegReg
      extends Definition("IsetRegRegReg", "ISET", Vec("NoBop ISET", "NoBop ISET U", "NoBop_ISET", "NoBop_ISET_U")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.IsetRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object IsetRegRegRegPred extends Definition("IsetRegRegRegPred", "ISET", Vec("ISET", "ISET U", "ISET_U")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.IsetRegRegRegPred(dst, srcA, srcB, pa, modifiers, guard))
        case _ => None
      }
  }
  object IsetpPredPredRegAddressImmPred
      extends Definition("IsetpPredPredRegAddressImmPred", "ISETP", Vec("isetp__RCxR_RCxR", "isetp__RCxR_RCxR_noEX")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.IsetpPredPredRegAddressImmPred(pu, pv, srcA, srcB, sb_offset, pp, modifiers, guard))
        case _ => None
      }
  }
  object IsetpPredPredRegAddressImmPredPred extends Definition(
        "IsetpPredPredRegAddressImmPredPred",
        "ISETP",
        Vec("isetp__RCxR_RCxR", "isetp__RCxR_RCxR_EX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Predicate(pr)
            ) =>
          Some(Instruction.IsetpPredPredRegAddressImmPredPred(pu, pv, srcA, srcB, sb_offset, pp, pr, modifiers, guard))
        case _ => None
      }
  }
  object IsetpPredPredRegConstPred extends Definition(
        "IsetpPredPredRegConstPred",
        "ISETP",
        Vec(
          "Const ISETP",
          "Const ISETP U",
          "Const_ISETP",
          "Const_ISETP_U",
          "isetp__RCR_RC",
          "isetp__RCR_RCR",
          "isetp__RCR_RCR_noEX"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.IsetpPredPredRegConstPred(dstPredicate, nPd, srcA, srcConst, pa, modifiers, guard))
        case _ => None
      }
  }
  object IsetpPredPredRegConstPredPred extends Definition(
        "IsetpPredPredRegConstPredPred",
        "ISETP",
        Vec("isetp__RCR_RC", "isetp__RCR_RCR", "isetp__RCR_RCR_EX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Predicate(pr)
            ) => Some(Instruction.IsetpPredPredRegConstPredPred(pu, pv, srcA, sb, pp, pr, modifiers, guard))
        case _ => None
      }
  }
  object IsetpPredPredRegImmPred extends Definition(
        "IsetpPredPredRegImmPred",
        "ISETP",
        Vec(
          "Imm ISETP",
          "Imm ISETP U",
          "Imm_ISETP",
          "Imm_ISETP_U",
          "isetp_64__RsIR_RIR_noEX",
          "isetp__RsIR_RI",
          "isetp__RsIR_RIR",
          "isetp__RsIR_RIR_noEX"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.IsetpPredPredRegImmPred(dstPredicate, nPd, srcA, uImm, pa, modifiers, guard))
        case _ => None
      }
  }
  object IsetpPredPredRegImmPredPred extends Definition(
        "IsetpPredPredRegImmPredPred",
        "ISETP",
        Vec("isetp_64__RsIR_RIR_EX", "isetp__RsIR_RI", "isetp__RsIR_RIR", "isetp__RsIR_RIR_EX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Predicate(pr)
            ) => Some(Instruction.IsetpPredPredRegImmPredPred(pu, pv, srcA, sb, pp, pr, modifiers, guard))
        case _ => None
      }
  }
  object IsetpPredPredRegRegPred extends Definition(
        "IsetpPredPredRegRegPred",
        "ISETP",
        Vec(
          "ISETP",
          "ISETP U",
          "ISETP_U",
          "isetp_64__RRR_RRR_noEX",
          "isetp_64__RUR_RUR_noEX",
          "isetp__RRR_RR",
          "isetp__RRR_RRR",
          "isetp__RRR_RRR_noEX",
          "isetp__RUR_RUR",
          "isetp__RUR_RUR_noEX"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.IsetpPredPredRegRegPred(dstPredicate, nPd, srcA, srcB, pa, modifiers, guard))
        case _ => None
      }
  }
  object IsetpPredPredRegRegPredPred extends Definition(
        "IsetpPredPredRegRegPredPred",
        "ISETP",
        Vec(
          "isetp_64__RRR_RRR_EX",
          "isetp_64__RUR_RUR_EX",
          "isetp__RRR_RR",
          "isetp__RRR_RRR",
          "isetp__RRR_RRR_EX",
          "isetp__RUR_RUR",
          "isetp__RUR_RUR_EX"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Predicate(pr)
            ) => Some(Instruction.IsetpPredPredRegRegPredPred(pu, pv, srcA, srcB, pp, pr, modifiers, guard))
        case _ => None
      }
  }
  object IsetpPredRegAddressImm extends Definition(
        "IsetpPredRegAddressImm",
        "ISETP",
        Vec("isetp_simple__RCxR_RCxR", "isetp_simple__RCxR_RCxR_noEX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.IsetpPredRegAddressImm(pu, srcA, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object IsetpPredRegAddressImmPred extends Definition(
        "IsetpPredRegAddressImmPred",
        "ISETP",
        Vec("isetp_simple__RCxR_RCxR", "isetp_simple__RCxR_RCxR_EX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Predicate(pr)
            ) => Some(Instruction.IsetpPredRegAddressImmPred(pu, srcA, srcB, sb_offset, pr, modifiers, guard))
        case _ => None
      }
  }
  object IsetpPredRegConst extends Definition(
        "IsetpPredRegConst",
        "ISETP",
        Vec(
          "NoBop Const ISETP",
          "NoBop Const ISETP U",
          "NoBop_Const_ISETP",
          "NoBop_Const_ISETP_U",
          "isetp_simple__RCR_RC",
          "isetp_simple__RCR_RCR",
          "isetp_simple__RCR_RCR_noEX"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.IsetpPredRegConst(dstPredicate, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object IsetpPredRegConstPred extends Definition(
        "IsetpPredRegConstPred",
        "ISETP",
        Vec("isetp_simple__RCR_RC", "isetp_simple__RCR_RCR", "isetp_simple__RCR_RCR_EX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Predicate(pr)
            ) => Some(Instruction.IsetpPredRegConstPred(pu, srcA, sb, pr, modifiers, guard))
        case _ => None
      }
  }
  object IsetpPredRegImm extends Definition(
        "IsetpPredRegImm",
        "ISETP",
        Vec(
          "NoBop Imm ISETP",
          "NoBop Imm ISETP U",
          "NoBop_Imm_ISETP",
          "NoBop_Imm_ISETP_U",
          "isetp_64_simple__RsIR_RIR_noEX",
          "isetp_simple__RsIR_RI",
          "isetp_simple__RsIR_RIR",
          "isetp_simple__RsIR_RIR_noEX"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.IsetpPredRegImm(dstPredicate, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object IsetpPredRegImmPred extends Definition(
        "IsetpPredRegImmPred",
        "ISETP",
        Vec(
          "isetp_64_simple__RsIR_RIR_EX",
          "isetp_simple__RsIR_RI",
          "isetp_simple__RsIR_RIR",
          "isetp_simple__RsIR_RIR_EX"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(pr)
            ) => Some(Instruction.IsetpPredRegImmPred(pu, srcA, sb, pr, modifiers, guard))
        case _ => None
      }
  }
  object IsetpPredRegReg extends Definition(
        "IsetpPredRegReg",
        "ISETP",
        Vec(
          "NoBop ISETP",
          "NoBop ISETP U",
          "NoBop_ISETP",
          "NoBop_ISETP_U",
          "isetp_64_simple__RRR_RRR_noEX",
          "isetp_64_simple__RUR_RUR_noEX",
          "isetp_simple__RRR_RR",
          "isetp_simple__RRR_RRR",
          "isetp_simple__RRR_RRR_noEX",
          "isetp_simple__RUR_RUR",
          "isetp_simple__RUR_RUR_noEX"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.IsetpPredRegReg(dstPredicate, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object IsetpPredRegRegPred extends Definition(
        "IsetpPredRegRegPred",
        "ISETP",
        Vec(
          "isetp_64_simple__RRR_RRR_EX",
          "isetp_64_simple__RUR_RUR_EX",
          "isetp_simple__RRR_RR",
          "isetp_simple__RRR_RRR",
          "isetp_simple__RRR_RRR_EX",
          "isetp_simple__RUR_RUR",
          "isetp_simple__RUR_RUR_EX"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pr)
            ) => Some(Instruction.IsetpPredRegRegPred(pu, srcA, srcB, pr, modifiers, guard))
        case _ => None
      }
  }
  object IsubRegConstReg extends Definition("IsubRegConstReg", "ISUB", Vec("Const ISUB1")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcA)
            ) => Some(Instruction.IsubRegConstReg(dst, srcConst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object IsubRegImmReg extends Definition("IsubRegImmReg", "ISUB", Vec("Imm ISUB1")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcA)
            ) => Some(Instruction.IsubRegImmReg(dst, uImm, srcA, modifiers, guard))
        case _ => None
      }
  }
  object IsubRegRegConst extends Definition("IsubRegRegConst", "ISUB", Vec("Const ISUB")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.IsubRegRegConst(dst, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object IsubRegRegImm extends Definition("IsubRegRegImm", "ISUB", Vec("Imm ISUB")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.IsubRegRegImm(dst, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object IsubRegRegReg extends Definition("IsubRegRegReg", "ISUB", Vec("ISUB")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.IsubRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object JcalConst extends Definition("JcalConst", "JCAL", Vec("JCAL c", "JCAL_c")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(srcConst)) => Some(Instruction.JcalConst(srcConst, modifiers, guard))
        case _ => None
      }
  }
  object JcalImm extends Definition("JcalImm", "JCAL", Vec("JCAL")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sImm)) => Some(Instruction.JcalImm(sImm, modifiers, guard))
        case _ => None
      }
  }
  object JmpConst
      extends Definition("JmpConst", "JMP", Vec("JMP_c", "jmp_const_", "jmp_const__CONV_DIV", "jmp_const__U")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(sa)) => Some(Instruction.JmpConst(sa, modifiers, guard))
        case _ => None
      }
  }
  object JmpImm extends Definition(
        "JmpImm",
        "JMP",
        Vec(
          "JMP",
          "jmp_imm_",
          "jmp_imm__CONV_DIV",
          "jmp_imm__U",
          "jmp_imm_rel_",
          "jmp_imm_rel__CONV_DIV",
          "jmp_imm_rel__U"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sa)) => Some(Instruction.JmpImm(sa, modifiers, guard))
        case _ => None
      }
  }
  object JmpPredConst extends Definition(
        "JmpPredConst",
        "JMP",
        Vec("jmp_const_", "jmp_const__CONV_DIV", "jmp_const__U", "jmp_const_uniform_pred_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.ConstantBank(sa)) =>
          Some(Instruction.JmpPredConst(pp, sa, modifiers, guard))
        case _ => None
      }
  }
  object JmpPredImm extends Definition(
        "JmpPredImm",
        "JMP",
        Vec(
          "jmp_imm_",
          "jmp_imm__CONV_DIV",
          "jmp_imm__U",
          "jmp_imm_rel_",
          "jmp_imm_rel__CONV_DIV",
          "jmp_imm_rel__U",
          "jmp_imm_uniform_pred_",
          "jmp_imm_uniform_pred_rel_"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.Immediate(sa)) =>
          Some(Instruction.JmpPredImm(pp, sa, modifiers, guard))
        case _ => None
      }
  }
  object JmpPredPredConst extends Definition("JmpPredPredConst", "JMP", Vec("jmp_const_uniform_pred_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pp),
              InstructionOperand.Predicate(uPq),
              InstructionOperand.ConstantBank(sa)
            ) => Some(Instruction.JmpPredPredConst(pp, uPq, sa, modifiers, guard))
        case _ => None
      }
  }
  object JmpPredPredImm
      extends Definition("JmpPredPredImm", "JMP", Vec("jmp_imm_uniform_pred_", "jmp_imm_uniform_pred_rel_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pp),
              InstructionOperand.Predicate(uPq),
              InstructionOperand.Immediate(sa)
            ) => Some(Instruction.JmpPredPredImm(pp, uPq, sa, modifiers, guard))
        case _ => None
      }
  }
  object JmpPredRegConst extends Definition("JmpPredRegConst", "JMP", Vec("jmp_const_uniform_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pp),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(sa)
            ) => Some(Instruction.JmpPredRegConst(pp, srcB, sa, modifiers, guard))
        case _ => None
      }
  }
  object JmpPredRegImm extends Definition("JmpPredRegImm", "JMP", Vec("jmp_imm_uniform_", "jmp_imm_uniform_rel_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pp),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sa)
            ) => Some(Instruction.JmpPredRegImm(pp, srcB, sa, modifiers, guard))
        case _ => None
      }
  }
  object JmpRegConst extends Definition("JmpRegConst", "JMP", Vec("jmp_const_uniform_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcB), InstructionOperand.ConstantBank(sa)) =>
          Some(Instruction.JmpRegConst(srcB, sa, modifiers, guard))
        case _ => None
      }
  }
  object JmpRegImm extends Definition("JmpRegImm", "JMP", Vec("jmp_imm_uniform_", "jmp_imm_uniform_rel_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcB), InstructionOperand.Immediate(sa)) =>
          Some(Instruction.JmpRegImm(srcB, sa, modifiers, guard))
        case _ => None
      }
  }
  object JmpSymbolConst extends Definition("JmpSymbolConst", "JMP", Vec("JMP_c")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Symbol(testCC), InstructionOperand.ConstantBank(srcConst)) =>
          Some(Instruction.JmpSymbolConst(testCC, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object JmpSymbolImm extends Definition("JmpSymbolImm", "JMP", Vec("JMP")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Symbol(testCC), InstructionOperand.Immediate(sImm)) =>
          Some(Instruction.JmpSymbolImm(testCC, sImm, modifiers, guard))
        case _ => None
      }
  }
  object JmpSymbolImmImm extends Definition("JmpSymbolImmImm", "JMP", Vec("JMP c")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Symbol(srcConst),
              InstructionOperand.Immediate(constBank),
              InstructionOperand.Immediate(immConstOffset)
            ) => Some(Instruction.JmpSymbolImmImm(srcConst, constBank, immConstOffset, modifiers, guard))
        case _ => None
      }
  }
  object JmpSymbolSymbolImmImm extends Definition("JmpSymbolSymbolImmImm", "JMP", Vec("JMP c")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Symbol(testCC),
              InstructionOperand.Symbol(srcConst),
              InstructionOperand.Immediate(constBank),
              InstructionOperand.Immediate(immConstOffset)
            ) => Some(Instruction.JmpSymbolSymbolImmImm(testCC, srcConst, constBank, immConstOffset, modifiers, guard))
        case _ => None
      }
  }
  object JmxConst extends Definition("JmxConst", "JMX", Vec("JMX_c")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(srcConst)) => Some(Instruction.JmxConst(srcConst, modifiers, guard))
        case _ => None
      }
  }
  object JmxPredReg extends Definition("JmxPredReg", "JMX", Vec("jmx_", "jmx_rel_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.Register(srcA)) =>
          Some(Instruction.JmxPredReg(pp, srcA, modifiers, guard))
        case _ => None
      }
  }
  object JmxPredRegImm extends Definition("JmxPredRegImm", "JMX", Vec("jmx_", "jmx_rel_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pp),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(ra_offset)
            ) => Some(Instruction.JmxPredRegImm(pp, srcA, ra_offset, modifiers, guard))
        case _ => None
      }
  }
  object JmxReg extends Definition("JmxReg", "JMX", Vec("JMX", "jmx_", "jmx_rel_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA)) => Some(Instruction.JmxReg(srcA, modifiers, guard))
        case _ => None
      }
  }
  object JmxRegImm extends Definition("JmxRegImm", "JMX", Vec("JMX", "jmx_", "jmx_rel_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA), InstructionOperand.Immediate(ra_offset)) =>
          Some(Instruction.JmxRegImm(srcA, ra_offset, modifiers, guard))
        case _ => None
      }
  }
  object JmxSymbolConst extends Definition("JmxSymbolConst", "JMX", Vec("JMX_c")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Symbol(testCC), InstructionOperand.ConstantBank(srcConst)) =>
          Some(Instruction.JmxSymbolConst(testCC, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object JmxSymbolImmAddress extends Definition("JmxSymbolImmAddress", "JMX", Vec("JMX c")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Symbol(srcConst),
              InstructionOperand.Immediate(constBank),
              InstructionOperand.Address(srcA)
            ) => Some(Instruction.JmxSymbolImmAddress(srcConst, constBank, srcA, modifiers, guard))
        case _ => None
      }
  }
  object JmxSymbolReg extends Definition("JmxSymbolReg", "JMX", Vec("JMX")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Symbol(testCC), InstructionOperand.Register(srcA)) =>
          Some(Instruction.JmxSymbolReg(testCC, srcA, modifiers, guard))
        case _ => None
      }
  }
  object JmxSymbolRegImm extends Definition("JmxSymbolRegImm", "JMX", Vec("JMX")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Symbol(testCC),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sImm)
            ) => Some(Instruction.JmxSymbolRegImm(testCC, srcA, sImm, modifiers, guard))
        case _ => None
      }
  }
  object JmxSymbolSymbolImmAddress extends Definition("JmxSymbolSymbolImmAddress", "JMX", Vec("JMX c")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Symbol(testCC),
              InstructionOperand.Symbol(srcConst),
              InstructionOperand.Immediate(constBank),
              InstructionOperand.Address(srcA)
            ) => Some(Instruction.JmxSymbolSymbolImmAddress(testCC, srcConst, constBank, srcA, modifiers, guard))
        case _ => None
      }
  }
  object JmxuPredReg extends Definition("JmxuPredReg", "JMXU", Vec("jmxu_", "jmxu_rel_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.Register(srcA)) =>
          Some(Instruction.JmxuPredReg(pp, srcA, modifiers, guard))
        case _ => None
      }
  }
  object JmxuPredRegImm extends Definition("JmxuPredRegImm", "JMXU", Vec("jmxu_", "jmxu_rel_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pp),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uR_offset)
            ) => Some(Instruction.JmxuPredRegImm(pp, srcA, uR_offset, modifiers, guard))
        case _ => None
      }
  }
  object JmxuReg extends Definition("JmxuReg", "JMXU", Vec("jmxu_", "jmxu_rel_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA)) => Some(Instruction.JmxuReg(srcA, modifiers, guard))
        case _ => None
      }
  }
  object JmxuRegImm extends Definition("JmxuRegImm", "JMXU", Vec("jmxu_", "jmxu_rel_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA), InstructionOperand.Immediate(uR_offset)) =>
          Some(Instruction.JmxuRegImm(srcA, uR_offset, modifiers, guard))
        case _ => None
      }
  }
  object KilNoOperands extends Definition("KilNoOperands", "KIL", Vec("BRK", "KIL")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.KilNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object KillNoOperands extends Definition("KillNoOperands", "KILL", Vec("kill_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.KillNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object KillPred extends Definition("KillPred", "KILL", Vec("kill_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp)) => Some(Instruction.KillPred(pp, modifiers, guard))
        case _ => None
      }
  }
  object LdPredRegAddress extends Definition(
        "LdPredRegAddress",
        "LD",
        Vec(
          "ld_weakonly_zd__sImmOffset",
          "ld_weakonly_zd__uImmOffset",
          "ld_weakonly_zd_uniform__Ra32",
          "ld_weakonly_zd_uniform__Ra64",
          "ld_weakonly_zd_uniform__RaRZ",
          "ld_zd__sImmOffset",
          "ld_zd__uImmOffset",
          "ld_zd_uniform__Ra32",
          "ld_zd_uniform__Ra64",
          "ld_zd_uniform__RaRZ"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA)
            ) => Some(Instruction.LdPredRegAddress(pu, dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object LdPredRegAddressPred extends Definition(
        "LdPredRegAddressPred",
        "LD",
        Vec(
          "ld_weakonly_zd__sImmOffset",
          "ld_weakonly_zd__uImmOffset",
          "ld_weakonly_zd_uniform__Ra32",
          "ld_weakonly_zd_uniform__Ra64",
          "ld_weakonly_zd_uniform__RaRZ",
          "ld_zd__sImmOffset",
          "ld_zd__uImmOffset",
          "ld_zd_uniform__Ra32",
          "ld_zd_uniform__Ra64",
          "ld_zd_uniform__RaRZ"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Predicate(pnz)
            ) => Some(Instruction.LdPredRegAddressPred(pu, dst, srcA, pnz, modifiers, guard))
        case _ => None
      }
  }
  object LdRegAddress extends Definition(
        "LdRegAddress",
        "LD",
        Vec(
          "I LD",
          "I_LD",
          "LD",
          "ld__sImmOffset",
          "ld__uImmOffset",
          "ld_memdesc__Ra64",
          "ld_uniform__Ra32",
          "ld_uniform__Ra64",
          "ld_uniform__RaRZ",
          "ld_weakonly__sImmOffset",
          "ld_weakonly__uImmOffset",
          "ld_weakonly_uniform__Ra32",
          "ld_weakonly_uniform__Ra64",
          "ld_weakonly_uniform__RaRZ"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Address(srcA)) =>
          Some(Instruction.LdRegAddress(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object LdRegAddressPred extends Definition(
        "LdRegAddressPred",
        "LD",
        Vec(
          "I_LD",
          "LD",
          "ld__sImmOffset",
          "ld__uImmOffset",
          "ld_memdesc__Ra64",
          "ld_uniform__Ra32",
          "ld_uniform__Ra64",
          "ld_uniform__RaRZ",
          "ld_weakonly__sImmOffset",
          "ld_weakonly__uImmOffset",
          "ld_weakonly_uniform__Ra32",
          "ld_weakonly_uniform__Ra64",
          "ld_weakonly_uniform__RaRZ"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Predicate(pnz)
            ) => Some(Instruction.LdRegAddressPred(dst, srcA, pnz, modifiers, guard))
        case _ => None
      }
  }
  object LdcRegAddressAddress
      extends Definition("LdcRegAddressAddress", "LDC", Vec("ldc_ur__URRzI", "ldc_ur__URnonRzI")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Address(srcB)
            ) => Some(Instruction.LdcRegAddressAddress(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object LdcRegConst extends Definition(
        "LdcRegConst",
        "LDC",
        Vec("I LDC", "I LDC 64", "I_LDC", "I_LDC_64", "LDC", "LDC 64", "LDC_64", "ldc__RaNonRZ", "ldc__RaRZ")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(sa)) =>
          Some(Instruction.LdcRegConst(dst, sa, modifiers, guard))
        case _ => None
      }
  }
  object LdcuRegAddress extends Definition("LdcuRegAddress", "LDCU", Vec("ldcu_ur_offs_optional_upx_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Address(srcA)) =>
          Some(Instruction.LdcuRegAddress(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object LdcuRegAddressAddress extends Definition("LdcuRegAddressAddress", "LDCU", Vec("ldcu_const_RCxR_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Address(srcB)
            ) => Some(Instruction.LdcuRegAddressAddress(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object LdcuRegAddressPred extends Definition("LdcuRegAddressPred", "LDCU", Vec("ldcu_ur_offs_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.LdcuRegAddressPred(dst, srcA, uPp, modifiers, guard))
        case _ => None
      }
  }
  object LdcuRegConst extends Definition("LdcuRegConst", "LDCU", Vec("ldcu_const_RCR_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(sa)) =>
          Some(Instruction.LdcuRegConst(dst, sa, modifiers, guard))
        case _ => None
      }
  }
  object LdcuRegRegAddress extends Definition("LdcuRegRegAddress", "LDCU", Vec("ldcu_256_ur_offs_optional_upx_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(uRd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA)
            ) => Some(Instruction.LdcuRegRegAddress(uRd2, dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object LdcuRegRegAddressAddress extends Definition("LdcuRegRegAddressAddress", "LDCU", Vec("ldcu_256_const_RCxR_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(uRd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Address(srcB)
            ) => Some(Instruction.LdcuRegRegAddressAddress(uRd2, dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object LdcuRegRegAddressAddressImm
      extends Definition("LdcuRegRegAddressAddressImm", "LDCU", Vec("ldcu_256_const_RCxR_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(uRd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(word_mask)
            ) => Some(Instruction.LdcuRegRegAddressAddressImm(uRd2, dst, srcA, srcB, word_mask, modifiers, guard))
        case _ => None
      }
  }
  object LdcuRegRegAddressImm
      extends Definition("LdcuRegRegAddressImm", "LDCU", Vec("ldcu_256_ur_offs_optional_upx_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(uRd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Immediate(word_mask)
            ) => Some(Instruction.LdcuRegRegAddressImm(uRd2, dst, srcA, word_mask, modifiers, guard))
        case _ => None
      }
  }
  object LdcuRegRegAddressImmPred extends Definition("LdcuRegRegAddressImmPred", "LDCU", Vec("ldcu_256_ur_offs_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(uRd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Immediate(word_mask),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.LdcuRegRegAddressImmPred(uRd2, dst, srcA, word_mask, uPp, modifiers, guard))
        case _ => None
      }
  }
  object LdcuRegRegAddressPred extends Definition("LdcuRegRegAddressPred", "LDCU", Vec("ldcu_256_ur_offs_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(uRd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.LdcuRegRegAddressPred(uRd2, dst, srcA, uPp, modifiers, guard))
        case _ => None
      }
  }
  object LdcuRegRegConst extends Definition("LdcuRegRegConst", "LDCU", Vec("ldcu_256_const_RCR_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(uRd2),
              InstructionOperand.Register(dst),
              InstructionOperand.ConstantBank(sa)
            ) => Some(Instruction.LdcuRegRegConst(uRd2, dst, sa, modifiers, guard))
        case _ => None
      }
  }
  object LdcuRegRegConstImm extends Definition("LdcuRegRegConstImm", "LDCU", Vec("ldcu_256_const_RCR_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(uRd2),
              InstructionOperand.Register(dst),
              InstructionOperand.ConstantBank(sa),
              InstructionOperand.Immediate(word_mask)
            ) => Some(Instruction.LdcuRegRegConstImm(uRd2, dst, sa, word_mask, modifiers, guard))
        case _ => None
      }
  }
  object LdgPredRegAddress extends Definition(
        "LdgPredRegAddress",
        "LDG",
        Vec(
          "I_LDG_SPARSE",
          "LDG_SPARSE",
          "ldg__sImmOffset",
          "ldg__uImmOffset",
          "ldg_memdesc__Ra64",
          "ldg_uniform__Ra32",
          "ldg_uniform__Ra64",
          "ldg_uniform__RaRZ",
          "ldg_weakonly__sImmOffset",
          "ldg_weakonly__uImmOffset",
          "ldg_weakonly_uniform__Ra32",
          "ldg_weakonly_uniform__Ra64",
          "ldg_weakonly_uniform__RaRZ",
          "ldg_weakonly_zd__sImmOffset",
          "ldg_weakonly_zd__uImmOffset",
          "ldg_weakonly_zd_uniform__Ra32",
          "ldg_weakonly_zd_uniform__Ra64",
          "ldg_weakonly_zd_uniform__RaRZ",
          "ldg_zd__sImmOffset",
          "ldg_zd__uImmOffset",
          "ldg_zd_uniform__Ra32",
          "ldg_zd_uniform__Ra64",
          "ldg_zd_uniform__RaRZ"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA)
            ) => Some(Instruction.LdgPredRegAddress(dstPredicate, dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object LdgPredRegAddressPred extends Definition(
        "LdgPredRegAddressPred",
        "LDG",
        Vec(
          "ldg__sImmOffset",
          "ldg__uImmOffset",
          "ldg_memdesc__Ra64",
          "ldg_uniform__Ra32",
          "ldg_uniform__Ra64",
          "ldg_uniform__RaRZ",
          "ldg_weakonly__sImmOffset",
          "ldg_weakonly__uImmOffset",
          "ldg_weakonly_uniform__Ra32",
          "ldg_weakonly_uniform__Ra64",
          "ldg_weakonly_uniform__RaRZ",
          "ldg_weakonly_zd__sImmOffset",
          "ldg_weakonly_zd__uImmOffset",
          "ldg_weakonly_zd_uniform__Ra32",
          "ldg_weakonly_zd_uniform__Ra64",
          "ldg_weakonly_zd_uniform__RaRZ",
          "ldg_zd__sImmOffset",
          "ldg_zd__uImmOffset",
          "ldg_zd_uniform__Ra32",
          "ldg_zd_uniform__Ra64",
          "ldg_zd_uniform__RaRZ"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Predicate(pnz)
            ) => Some(Instruction.LdgPredRegAddressPred(pu, dst, srcA, pnz, modifiers, guard))
        case _ => None
      }
  }
  object LdgRegAddress extends Definition(
        "LdgRegAddress",
        "LDG",
        Vec(
          "I_LDG",
          "LDG",
          "LDG CG CS",
          "LDG CT",
          "ldg__sImmOffset",
          "ldg__uImmOffset",
          "ldg_memdesc__Ra64",
          "ldg_uniform__Ra32",
          "ldg_uniform__Ra64",
          "ldg_uniform__RaRZ",
          "ldg_weakonly__sImmOffset",
          "ldg_weakonly__uImmOffset",
          "ldg_weakonly_uniform__Ra32",
          "ldg_weakonly_uniform__Ra64",
          "ldg_weakonly_uniform__RaRZ"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Address(srcA)) =>
          Some(Instruction.LdgRegAddress(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object LdgRegAddressPred extends Definition(
        "LdgRegAddressPred",
        "LDG",
        Vec(
          "ldg__sImmOffset",
          "ldg__uImmOffset",
          "ldg_memdesc__Ra64",
          "ldg_uniform__Ra32",
          "ldg_uniform__Ra64",
          "ldg_uniform__RaRZ",
          "ldg_weakonly__sImmOffset",
          "ldg_weakonly__uImmOffset",
          "ldg_weakonly_uniform__Ra32",
          "ldg_weakonly_uniform__Ra64",
          "ldg_weakonly_uniform__RaRZ"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Predicate(pnz)
            ) => Some(Instruction.LdgRegAddressPred(dst, srcA, pnz, modifiers, guard))
        case _ => None
      }
  }
  object LdgRegRegAddress extends Definition(
        "LdgRegRegAddress",
        "LDG",
        Vec(
          "ldg_256_memdesc__Ra64",
          "ldg_256_rml2_memdesc__Ra64",
          "ldg_256_rml2_uniform__Ra32",
          "ldg_256_rml2_uniform__Ra64",
          "ldg_256_rml2_uniform__RaRZ",
          "ldg_256_uniform__Ra32",
          "ldg_256_uniform__Ra64",
          "ldg_256_uniform__RaRZ"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA)
            ) => Some(Instruction.LdgRegRegAddress(rd2, dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object LdgRegRegAddressImm extends Definition(
        "LdgRegRegAddressImm",
        "LDG",
        Vec(
          "ldg_256_memdesc__Ra64",
          "ldg_256_rml2_memdesc__Ra64",
          "ldg_256_rml2_uniform__Ra32",
          "ldg_256_rml2_uniform__Ra64",
          "ldg_256_rml2_uniform__RaRZ",
          "ldg_256_uniform__Ra32",
          "ldg_256_uniform__Ra64",
          "ldg_256_uniform__RaRZ"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Immediate(word_mask)
            ) => Some(Instruction.LdgRegRegAddressImm(rd2, dst, srcA, word_mask, modifiers, guard))
        case _ => None
      }
  }
  object LdgRegRegAddressImmPred extends Definition(
        "LdgRegRegAddressImmPred",
        "LDG",
        Vec(
          "ldg_256_memdesc__Ra64",
          "ldg_256_rml2_memdesc__Ra64",
          "ldg_256_rml2_uniform__Ra32",
          "ldg_256_rml2_uniform__Ra64",
          "ldg_256_rml2_uniform__RaRZ",
          "ldg_256_uniform__Ra32",
          "ldg_256_uniform__Ra64",
          "ldg_256_uniform__RaRZ"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Immediate(word_mask),
              InstructionOperand.Predicate(pnz)
            ) => Some(Instruction.LdgRegRegAddressImmPred(rd2, dst, srcA, word_mask, pnz, modifiers, guard))
        case _ => None
      }
  }
  object LdgRegRegAddressPred extends Definition(
        "LdgRegRegAddressPred",
        "LDG",
        Vec(
          "ldg_256_memdesc__Ra64",
          "ldg_256_rml2_memdesc__Ra64",
          "ldg_256_rml2_uniform__Ra32",
          "ldg_256_rml2_uniform__Ra64",
          "ldg_256_rml2_uniform__RaRZ",
          "ldg_256_uniform__Ra32",
          "ldg_256_uniform__Ra64",
          "ldg_256_uniform__RaRZ"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Predicate(pnz)
            ) => Some(Instruction.LdgRegRegAddressPred(rd2, dst, srcA, pnz, modifiers, guard))
        case _ => None
      }
  }
  object LdgdepbarNoOperands extends Definition("LdgdepbarNoOperands", "LDGDEPBAR", Vec("ldgdepbar_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.LdgdepbarNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object LdgmcRegAddress extends Definition(
        "LdgmcRegAddress",
        "LDGMC",
        Vec(
          "ldgmc_fp__Ra32",
          "ldgmc_fp__Ra64",
          "ldgmc_fp__RaRZ",
          "ldgmc_fp__memdesc",
          "ldgmc_int__Ra32",
          "ldgmc_int__Ra64",
          "ldgmc_int__RaRZ",
          "ldgmc_int__memdesc"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Address(srcA)) =>
          Some(Instruction.LdgmcRegAddress(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object LdgstsAddressAddress extends Definition(
        "LdgstsAddressAddress",
        "LDGSTS",
        Vec(
          "ldgsts__RR32U",
          "ldgsts__RR64U",
          "ldgsts__RUR",
          "ldgsts__desc_RRU",
          "ldgsts_memdesc_",
          "ldgsts_no_ra__RRU",
          "ldgsts_no_ra__RUR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcB), InstructionOperand.Address(srcA)) =>
          Some(Instruction.LdgstsAddressAddress(srcB, srcA, modifiers, guard))
        case _ => None
      }
  }
  object LdgstsAddressAddressPred extends Definition(
        "LdgstsAddressAddressPred",
        "LDGSTS",
        Vec(
          "ldgsts__RR32U",
          "ldgsts__RR64U",
          "ldgsts__RUR",
          "ldgsts__desc_RRU",
          "ldgsts_memdesc_",
          "ldgsts_no_ra__RRU",
          "ldgsts_no_ra__RUR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcB),
              InstructionOperand.Address(srcA),
              InstructionOperand.Predicate(pnz)
            ) => Some(Instruction.LdgstsAddressAddressPred(srcB, srcA, pnz, modifiers, guard))
        case _ => None
      }
  }
  object LdlRegAddress extends Definition(
        "LdlRegAddress",
        "LDL",
        Vec("I LDL", "I_LDL", "LDL", "ldl__sImmOffset", "ldl__uImmOffset", "ldl_memdesc_", "ldl_uniform_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Address(srcA)) =>
          Some(Instruction.LdlRegAddress(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object LdsPredRegAddress extends Definition(
        "LdsPredRegAddress",
        "LDS",
        Vec("lds_zd__sImmOffset", "lds_zd__uImmOffset", "lds_zd_uniform_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA)
            ) => Some(Instruction.LdsPredRegAddress(pu, dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object LdsRegAddress extends Definition(
        "LdsRegAddress",
        "LDS",
        Vec("I LDS", "I_LDS", "LDS", "lds__sImmOffset", "lds__uImmOffset", "lds_uniform_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Address(srcA)) =>
          Some(Instruction.LdsRegAddress(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object LdslkPredRegAddress extends Definition("LdslkPredRegAddress", "LDSLK", Vec("I LDSLK", "LDSLK")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA)
            ) => Some(Instruction.LdslkPredRegAddress(dstPredicate, dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object LdsmRegAddress extends Definition(
        "LdsmRegAddress",
        "LDSM",
        Vec(
          "ldsm__UR_sI_R",
          "ldsm__URsIR",
          "ldsm__URuIR",
          "ldsm__sImmOffset",
          "ldsm__uImmOffset",
          "ldsm_pseudo_ops__UR_sI_R",
          "ldsm_pseudo_ops__URsIR",
          "ldsm_pseudo_ops__URuIR",
          "ldsm_pseudo_ops__sImmOffset",
          "ldsm_pseudo_ops__uImmOffset"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Address(srcA)) =>
          Some(Instruction.LdsmRegAddress(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object LdtRegAddress extends Definition("LdtRegAddress", "LDT", Vec("ldt_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Address(srcB)) =>
          Some(Instruction.LdtRegAddress(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object LdtmRegAddress extends Definition("LdtmRegAddress", "LDTM", Vec("ldtm_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Address(srcB)) =>
          Some(Instruction.LdtmRegAddress(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object LdtmRegRegAddress extends Definition("LdtmRegRegAddress", "LDTM", Vec("ldtm_stat_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcB)
            ) => Some(Instruction.LdtmRegRegAddress(rd2, dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object LdtramRegAddress extends Definition("LdtramRegAddress", "LDTRAM", Vec("ldtram_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Address(srcA)) =>
          Some(Instruction.LdtramRegAddress(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object LeaPredRegRegConst extends Definition("LeaPredRegRegConst", "LEA", Vec("LEA_LO_CONST")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.LeaPredRegRegConst(dstPredicate, dst, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object LeaPredRegRegConstImm extends Definition("LeaPredRegRegConstImm", "LEA", Vec("LEA_LO_CONST")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Immediate(uImm5)
            ) => Some(Instruction.LeaPredRegRegConstImm(dstPredicate, dst, srcA, srcConst, uImm5, modifiers, guard))
        case _ => None
      }
  }
  object LeaPredRegRegConstReg extends Definition("LeaPredRegRegConstReg", "LEA", Vec("LEA_HI_CONST")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.LeaPredRegRegConstReg(dstPredicate, dst, srcA, srcConst, srcC, modifiers, guard))
        case _ => None
      }
  }
  object LeaPredRegRegConstRegImm extends Definition("LeaPredRegRegConstRegImm", "LEA", Vec("LEA_HI_CONST")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(uImm5)
            ) =>
          Some(Instruction.LeaPredRegRegConstRegImm(dstPredicate, dst, srcA, srcConst, srcC, uImm5, modifiers, guard))
        case _ => None
      }
  }
  object LeaPredRegRegImm extends Definition("LeaPredRegRegImm", "LEA", Vec("LEA_LO_IMM")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm20)
            ) => Some(Instruction.LeaPredRegRegImm(dstPredicate, dst, srcA, uImm20, modifiers, guard))
        case _ => None
      }
  }
  object LeaPredRegRegImmImm extends Definition("LeaPredRegRegImmImm", "LEA", Vec("LEA_LO_IMM")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm20),
              InstructionOperand.Immediate(uImm5)
            ) => Some(Instruction.LeaPredRegRegImmImm(dstPredicate, dst, srcA, uImm20, uImm5, modifiers, guard))
        case _ => None
      }
  }
  object LeaPredRegRegReg extends Definition("LeaPredRegRegReg", "LEA", Vec("LEA_LO_REG")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.LeaPredRegRegReg(dstPredicate, dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object LeaPredRegRegRegImm extends Definition("LeaPredRegRegRegImm", "LEA", Vec("LEA_LO_REG")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(uImm5)
            ) => Some(Instruction.LeaPredRegRegRegImm(dstPredicate, dst, srcA, srcB, uImm5, modifiers, guard))
        case _ => None
      }
  }
  object LeaPredRegRegRegReg extends Definition("LeaPredRegRegRegReg", "LEA", Vec("LEA_HI_REG")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.LeaPredRegRegRegReg(dstPredicate, dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object LeaPredRegRegRegRegImm extends Definition("LeaPredRegRegRegRegImm", "LEA", Vec("LEA_HI_REG")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(uImm5)
            ) => Some(Instruction.LeaPredRegRegRegRegImm(dstPredicate, dst, srcA, srcB, srcC, uImm5, modifiers, guard))
        case _ => None
      }
  }
  object LeaRegPredRegAddressImmImm extends Definition(
        "LeaRegPredRegAddressImmImm",
        "LEA",
        Vec("lea_hi_noimm_sx32__RCxR_RCxR", "lea_lo_noimm__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.LeaRegPredRegAddressImmImm(dst, pu, srcA, srcB, sb_offset, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object LeaRegPredRegAddressImmImmPred extends Definition(
        "LeaRegPredRegAddressImmImmPred",
        "LEA",
        Vec("lea_hi_noimm_sx32_x__RCxR_RCxR", "lea_lo_noimm_x__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Immediate(scaleU5),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.LeaRegPredRegAddressImmImmPred(
            dst,
            pu,
            srcA,
            srcB,
            sb_offset,
            scaleU5,
            pp,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object LeaRegPredRegAddressImmRegImm
      extends Definition("LeaRegPredRegAddressImmRegImm", "LEA", Vec("lea_hi_noimm__RCxR_RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.LeaRegPredRegAddressImmRegImm(
            dst,
            pu,
            srcA,
            srcB,
            sb_offset,
            srcC,
            scaleU5,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object LeaRegPredRegAddressImmRegImmPred
      extends Definition("LeaRegPredRegAddressImmRegImmPred", "LEA", Vec("lea_hi_noimm_x__RCxR_RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(scaleU5),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.LeaRegPredRegAddressImmRegImmPred(
            dst,
            pu,
            srcA,
            srcB,
            sb_offset,
            srcC,
            scaleU5,
            pp,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object LeaRegPredRegConstImm
      extends Definition("LeaRegPredRegConstImm", "LEA", Vec("lea_hi_noimm_sx32__RCR_RCR", "lea_lo_noimm__RCR_RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.LeaRegPredRegConstImm(dst, pu, srcA, sb, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object LeaRegPredRegConstImmPred extends Definition(
        "LeaRegPredRegConstImmPred",
        "LEA",
        Vec("lea_hi_noimm_sx32_x__RCR_RCR", "lea_lo_noimm_x__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Immediate(scaleU5),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.LeaRegPredRegConstImmPred(dst, pu, srcA, sb, scaleU5, pp, modifiers, guard))
        case _ => None
      }
  }
  object LeaRegPredRegConstRegImm extends Definition("LeaRegPredRegConstRegImm", "LEA", Vec("lea_hi_noimm__RCR_RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.LeaRegPredRegConstRegImm(dst, pu, srcA, sb, srcC, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object LeaRegPredRegConstRegImmPred
      extends Definition("LeaRegPredRegConstRegImmPred", "LEA", Vec("lea_hi_noimm_x__RCR_RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(scaleU5),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.LeaRegPredRegConstRegImmPred(dst, pu, srcA, sb, srcC, scaleU5, pp, modifiers, guard))
        case _ => None
      }
  }
  object LeaRegPredRegImmImm
      extends Definition("LeaRegPredRegImmImm", "LEA", Vec("lea_hi_imm_sx32__RuIR_RIR", "lea_lo_imm__RuIR_RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.LeaRegPredRegImmImm(dst, pu, srcA, sb, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object LeaRegPredRegImmImmPred extends Definition(
        "LeaRegPredRegImmImmPred",
        "LEA",
        Vec("lea_hi_imm_sx32_x__RuIR_RIR", "lea_lo_imm_x__RuIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Immediate(scaleU5),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.LeaRegPredRegImmImmPred(dst, pu, srcA, sb, scaleU5, pp, modifiers, guard))
        case _ => None
      }
  }
  object LeaRegPredRegImmRegImm extends Definition("LeaRegPredRegImmRegImm", "LEA", Vec("lea_hi_imm__RuIR_RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.LeaRegPredRegImmRegImm(dst, pu, srcA, sb, srcC, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object LeaRegPredRegImmRegImmPred
      extends Definition("LeaRegPredRegImmRegImmPred", "LEA", Vec("lea_hi_imm_x__RuIR_RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(scaleU5),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.LeaRegPredRegImmRegImmPred(dst, pu, srcA, sb, srcC, scaleU5, pp, modifiers, guard))
        case _ => None
      }
  }
  object LeaRegPredRegRegImm extends Definition(
        "LeaRegPredRegRegImm",
        "LEA",
        Vec(
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
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.LeaRegPredRegRegImm(dst, pu, srcA, srcB, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object LeaRegPredRegRegImmImm extends Definition("LeaRegPredRegRegImmImm", "LEA", Vec("lea_hi_imm__RRuI_RRI")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.LeaRegPredRegRegImmImm(dst, pu, srcA, srcB, sc, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object LeaRegPredRegRegImmImmPred
      extends Definition("LeaRegPredRegRegImmImmPred", "LEA", Vec("lea_hi_imm_x__RRuI_RRI")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc),
              InstructionOperand.Immediate(scaleU5),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.LeaRegPredRegRegImmImmPred(dst, pu, srcA, srcB, sc, scaleU5, pp, modifiers, guard))
        case _ => None
      }
  }
  object LeaRegPredRegRegImmPred extends Definition(
        "LeaRegPredRegRegImmPred",
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
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(scaleU5),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.LeaRegPredRegRegImmPred(dst, pu, srcA, srcB, scaleU5, pp, modifiers, guard))
        case _ => None
      }
  }
  object LeaRegPredRegRegRegImm
      extends Definition("LeaRegPredRegRegRegImm", "LEA", Vec("lea_hi_noimm__RRR_RRR", "lea_hi_noimm__RUR_RUR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.LeaRegPredRegRegRegImm(dst, pu, srcA, srcB, srcC, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object LeaRegPredRegRegRegImmPred extends Definition(
        "LeaRegPredRegRegRegImmPred",
        "LEA",
        Vec("lea_hi_noimm_x__RRR_RRR", "lea_hi_noimm_x__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(scaleU5),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.LeaRegPredRegRegRegImmPred(dst, pu, srcA, srcB, srcC, scaleU5, pp, modifiers, guard))
        case _ => None
      }
  }
  object LeaRegRegAddressImmImm extends Definition(
        "LeaRegRegAddressImmImm",
        "LEA",
        Vec("lea_hi_noimm_sx32__RCxR_RCxR", "lea_lo_noimm__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.LeaRegRegAddressImmImm(dst, srcA, srcB, sb_offset, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object LeaRegRegAddressImmImmPred extends Definition(
        "LeaRegRegAddressImmImmPred",
        "LEA",
        Vec("lea_hi_noimm_sx32_x__RCxR_RCxR", "lea_lo_noimm_x__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Immediate(scaleU5),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.LeaRegRegAddressImmImmPred(dst, srcA, srcB, sb_offset, scaleU5, pp, modifiers, guard))
        case _ => None
      }
  }
  object LeaRegRegAddressImmRegImm
      extends Definition("LeaRegRegAddressImmRegImm", "LEA", Vec("lea_hi_noimm__RCxR_RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(scaleU5)
            ) =>
          Some(Instruction.LeaRegRegAddressImmRegImm(dst, srcA, srcB, sb_offset, srcC, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object LeaRegRegAddressImmRegImmPred
      extends Definition("LeaRegRegAddressImmRegImmPred", "LEA", Vec("lea_hi_noimm_x__RCxR_RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(scaleU5),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.LeaRegRegAddressImmRegImmPred(
            dst,
            srcA,
            srcB,
            sb_offset,
            srcC,
            scaleU5,
            pp,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object LeaRegRegConst extends Definition("LeaRegRegConst", "LEA", Vec("LEA_LO_CONST")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.LeaRegRegConst(dst, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object LeaRegRegConstImm extends Definition(
        "LeaRegRegConstImm",
        "LEA",
        Vec("LEA_LO_CONST", "lea_hi_noimm_sx32__RCR_RCR", "lea_lo_noimm__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.LeaRegRegConstImm(dst, srcA, sb, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object LeaRegRegConstImmPred extends Definition(
        "LeaRegRegConstImmPred",
        "LEA",
        Vec("lea_hi_noimm_sx32_x__RCR_RCR", "lea_lo_noimm_x__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Immediate(scaleU5),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.LeaRegRegConstImmPred(dst, srcA, sb, scaleU5, pp, modifiers, guard))
        case _ => None
      }
  }
  object LeaRegRegConstReg extends Definition("LeaRegRegConstReg", "LEA", Vec("LEA_HI_CONST")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.LeaRegRegConstReg(dst, srcA, srcConst, srcC, modifiers, guard))
        case _ => None
      }
  }
  object LeaRegRegConstRegImm
      extends Definition("LeaRegRegConstRegImm", "LEA", Vec("LEA_HI_CONST", "lea_hi_noimm__RCR_RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.LeaRegRegConstRegImm(dst, srcA, sb, srcC, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object LeaRegRegConstRegImmPred
      extends Definition("LeaRegRegConstRegImmPred", "LEA", Vec("lea_hi_noimm_x__RCR_RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(scaleU5),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.LeaRegRegConstRegImmPred(dst, srcA, sb, srcC, scaleU5, pp, modifiers, guard))
        case _ => None
      }
  }
  object LeaRegRegImm extends Definition("LeaRegRegImm", "LEA", Vec("LEA_LO_IMM")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm20)
            ) => Some(Instruction.LeaRegRegImm(dst, srcA, uImm20, modifiers, guard))
        case _ => None
      }
  }
  object LeaRegRegImmImm extends Definition(
        "LeaRegRegImmImm",
        "LEA",
        Vec("LEA_LO_IMM", "lea_hi_imm_sx32__RuIR_RIR", "lea_lo_imm__RuIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.LeaRegRegImmImm(dst, srcA, sb, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object LeaRegRegImmImmPred
      extends Definition("LeaRegRegImmImmPred", "LEA", Vec("lea_hi_imm_sx32_x__RuIR_RIR", "lea_lo_imm_x__RuIR_RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Immediate(scaleU5),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.LeaRegRegImmImmPred(dst, srcA, sb, scaleU5, pp, modifiers, guard))
        case _ => None
      }
  }
  object LeaRegRegImmRegImm extends Definition("LeaRegRegImmRegImm", "LEA", Vec("lea_hi_imm__RuIR_RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.LeaRegRegImmRegImm(dst, srcA, sb, srcC, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object LeaRegRegImmRegImmPred extends Definition("LeaRegRegImmRegImmPred", "LEA", Vec("lea_hi_imm_x__RuIR_RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(scaleU5),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.LeaRegRegImmRegImmPred(dst, srcA, sb, srcC, scaleU5, pp, modifiers, guard))
        case _ => None
      }
  }
  object LeaRegRegReg extends Definition("LeaRegRegReg", "LEA", Vec("LEA_LO_REG")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.LeaRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  val definitions: Vec[Definition] = Vec(
    IgmmaRegAddressRegPredReg,
    IgmmaRegAddressRegPredRegImm,
    IgmmaRegAddressRegReg,
    IgmmaRegAddressRegRegImm,
    IgmmaRegAddressRegRegPred,
    IgmmaRegAddressRegRegPredReg,
    IgmmaRegAddressRegRegPredRegImm,
    IgmmaRegAddressRegRegReg,
    IgmmaRegAddressRegRegRegImm,
    IgmmaRegRegAddressReg,
    IgmmaRegRegAddressRegPred,
    IgmmaRegRegAddressRegPredReg,
    IgmmaRegRegAddressRegPredRegImm,
    IgmmaRegRegAddressRegReg,
    IgmmaRegRegAddressRegRegImm,
    ImadRegPredRegAddressImmReg,
    ImadRegPredRegAddressImmRegPred,
    ImadRegPredRegConstReg,
    ImadRegPredRegConstRegPred,
    ImadRegPredRegImmReg,
    ImadRegPredRegImmRegPred,
    ImadRegPredRegRegAddressImm,
    ImadRegPredRegRegAddressImmPred,
    ImadRegPredRegRegConst,
    ImadRegPredRegRegConstPred,
    ImadRegPredRegRegReg,
    ImadRegPredRegRegRegPred,
    ImadRegRegAddressImmReg,
    ImadRegRegAddressImmRegPred,
    ImadRegRegConstReg,
    ImadRegRegConstRegPred,
    ImadRegRegImmReg,
    ImadRegRegImmRegPred,
    ImadRegRegRegAddressImm,
    ImadRegRegRegAddressImmPred,
    ImadRegRegRegConst,
    ImadRegRegRegConstPred,
    ImadRegRegRegImm,
    ImadRegRegRegImmPred,
    ImadRegRegRegReg,
    ImadRegRegRegRegPred,
    Imad32iRegRegImmReg,
    ImadspRegRegConstReg,
    ImadspRegRegImmReg,
    ImadspRegRegRegConst,
    ImadspRegRegRegReg,
    ImmaRegRegRegReg,
    ImmaRegRegRegRegPred,
    ImmaRegRegRegRegPredRegImm,
    ImmaRegRegRegRegRegImm,
    ImnmxPredPredRegRegImmPred,
    ImnmxPredPredRegRegImmPredPred,
    ImnmxPredPredRegRegRegPred,
    ImnmxPredPredRegRegRegPredPred,
    ImnmxPredRegRegImmPred,
    ImnmxPredRegRegImmPredPred,
    ImnmxPredRegRegRegPred,
    ImnmxPredRegRegRegPredPred,
    ImnmxRegRegAddressImmPred,
    ImnmxRegRegConstPred,
    ImnmxRegRegImmPred,
    ImnmxRegRegImmPredPred,
    ImnmxRegRegRegPred,
    ImnmxRegRegRegPredPred,
    ImulRegPredRegAddressImm,
    ImulRegPredRegConst,
    ImulRegPredRegImm,
    ImulRegPredRegReg,
    ImulRegRegAddressImm,
    ImulRegRegConst,
    ImulRegRegImm,
    ImulRegRegImmReg,
    ImulRegRegReg,
    ImulRegRegRegImm,
    ImulRegRegRegReg,
    Imul32iRegPredRegImm,
    Imul32iRegRegImm,
    IpaRegAddress,
    IpaRegAddressImm,
    IpaRegAddressPred,
    IpaRegAddressReg,
    IpaRegAddressRegReg,
    IpaRegAddressRegRegPred,
    IpaRegImm,
    IpaRegImmConst,
    IpaRegImmImm,
    IpaRegImmPred,
    IpaRegImmReg,
    IpaRegImmRegReg,
    IpaRegImmRegRegPred,
    IpaRegPredAddress,
    IpaRegPredAddressImm,
    IpaRegPredAddressReg,
    IpaRegPredImm,
    IpaRegPredImmConst,
    IpaRegPredImmImm,
    IpaRegPredImmReg,
    IsadRegRegConstReg,
    IsadRegRegImmReg,
    IsadRegRegRegConst,
    IsadRegRegRegReg,
    IsberdRegAddress,
    IsbewrAddressReg,
    IscaddRegPredRegAddressImmImm,
    IscaddRegPredRegConstImm,
    IscaddRegPredRegImmImm,
    IscaddRegPredRegRegImm,
    IscaddRegRegAddressImmImm,
    IscaddRegRegConstImm,
    IscaddRegRegImmImm,
    IscaddRegRegRegImm,
    Iscadd32iRegPredRegImmImm,
    Iscadd32iRegRegImmImm,
    IsetRegRegConst,
    IsetRegRegConstPred,
    IsetRegRegImm,
    IsetRegRegImmPred,
    IsetRegRegReg,
    IsetRegRegRegPred,
    IsetpPredPredRegAddressImmPred,
    IsetpPredPredRegAddressImmPredPred,
    IsetpPredPredRegConstPred,
    IsetpPredPredRegConstPredPred,
    IsetpPredPredRegImmPred,
    IsetpPredPredRegImmPredPred,
    IsetpPredPredRegRegPred,
    IsetpPredPredRegRegPredPred,
    IsetpPredRegAddressImm,
    IsetpPredRegAddressImmPred,
    IsetpPredRegConst,
    IsetpPredRegConstPred,
    IsetpPredRegImm,
    IsetpPredRegImmPred,
    IsetpPredRegReg,
    IsetpPredRegRegPred,
    IsubRegConstReg,
    IsubRegImmReg,
    IsubRegRegConst,
    IsubRegRegImm,
    IsubRegRegReg,
    JcalConst,
    JcalImm,
    JmpConst,
    JmpImm,
    JmpPredConst,
    JmpPredImm,
    JmpPredPredConst,
    JmpPredPredImm,
    JmpPredRegConst,
    JmpPredRegImm,
    JmpRegConst,
    JmpRegImm,
    JmpSymbolConst,
    JmpSymbolImm,
    JmpSymbolImmImm,
    JmpSymbolSymbolImmImm,
    JmxConst,
    JmxPredReg,
    JmxPredRegImm,
    JmxReg,
    JmxRegImm,
    JmxSymbolConst,
    JmxSymbolImmAddress,
    JmxSymbolReg,
    JmxSymbolRegImm,
    JmxSymbolSymbolImmAddress,
    JmxuPredReg,
    JmxuPredRegImm,
    JmxuReg,
    JmxuRegImm,
    KilNoOperands,
    KillNoOperands,
    KillPred,
    LdPredRegAddress,
    LdPredRegAddressPred,
    LdRegAddress,
    LdRegAddressPred,
    LdcRegAddressAddress,
    LdcRegConst,
    LdcuRegAddress,
    LdcuRegAddressAddress,
    LdcuRegAddressPred,
    LdcuRegConst,
    LdcuRegRegAddress,
    LdcuRegRegAddressAddress,
    LdcuRegRegAddressAddressImm,
    LdcuRegRegAddressImm,
    LdcuRegRegAddressImmPred,
    LdcuRegRegAddressPred,
    LdcuRegRegConst,
    LdcuRegRegConstImm,
    LdgPredRegAddress,
    LdgPredRegAddressPred,
    LdgRegAddress,
    LdgRegAddressPred,
    LdgRegRegAddress,
    LdgRegRegAddressImm,
    LdgRegRegAddressImmPred,
    LdgRegRegAddressPred,
    LdgdepbarNoOperands,
    LdgmcRegAddress,
    LdgstsAddressAddress,
    LdgstsAddressAddressPred,
    LdlRegAddress,
    LdsPredRegAddress,
    LdsRegAddress,
    LdslkPredRegAddress,
    LdsmRegAddress,
    LdtRegAddress,
    LdtmRegAddress,
    LdtmRegRegAddress,
    LdtramRegAddress,
    LeaPredRegRegConst,
    LeaPredRegRegConstImm,
    LeaPredRegRegConstReg,
    LeaPredRegRegConstRegImm,
    LeaPredRegRegImm,
    LeaPredRegRegImmImm,
    LeaPredRegRegReg,
    LeaPredRegRegRegImm,
    LeaPredRegRegRegReg,
    LeaPredRegRegRegRegImm,
    LeaRegPredRegAddressImmImm,
    LeaRegPredRegAddressImmImmPred,
    LeaRegPredRegAddressImmRegImm,
    LeaRegPredRegAddressImmRegImmPred,
    LeaRegPredRegConstImm,
    LeaRegPredRegConstImmPred,
    LeaRegPredRegConstRegImm,
    LeaRegPredRegConstRegImmPred,
    LeaRegPredRegImmImm,
    LeaRegPredRegImmImmPred,
    LeaRegPredRegImmRegImm,
    LeaRegPredRegImmRegImmPred,
    LeaRegPredRegRegImm,
    LeaRegPredRegRegImmImm,
    LeaRegPredRegRegImmImmPred,
    LeaRegPredRegRegImmPred,
    LeaRegPredRegRegRegImm,
    LeaRegPredRegRegRegImmPred,
    LeaRegRegAddressImmImm,
    LeaRegRegAddressImmImmPred,
    LeaRegRegAddressImmRegImm,
    LeaRegRegAddressImmRegImmPred,
    LeaRegRegConst,
    LeaRegRegConstImm,
    LeaRegRegConstImmPred,
    LeaRegRegConstReg,
    LeaRegRegConstRegImm,
    LeaRegRegConstRegImmPred,
    LeaRegRegImm,
    LeaRegRegImmImm,
    LeaRegRegImmImmPred,
    LeaRegRegImmRegImm,
    LeaRegRegImmRegImmPred,
    LeaRegRegReg
  )
}
