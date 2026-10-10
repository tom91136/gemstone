// Generated SASS instruction definitions and checked operand constructors. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.sass
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import Instruction.*
@javax.annotation.processing.Generated(Array("sbt codegen"))
object InstructionDefinitions6 {
  object UviaddRegRegImm extends Definition("UviaddRegRegImm", "UVIADD", Vec("uviadd__URuIUR_URIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.UviaddRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object UviaddRegRegReg extends Definition("UviaddRegRegReg", "UVIADD", Vec("uviadd__URURUR_UUU")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.UviaddRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UvimnmxRegRegImmPred extends Definition("UvimnmxRegRegImmPred", "UVIMNMX", Vec("uvimnmx__URIR_URsIUR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UvimnmxRegRegImmPred(dst, srcA, sb, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UvimnmxRegRegRegPred extends Definition("UvimnmxRegRegRegPred", "UVIMNMX", Vec("uvimnmx__UUU_URURUR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UvimnmxRegRegRegPred(dst, srcA, srcB, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UvirtcountImm
      extends Definition("UvirtcountImm", "UVIRTCOUNT", Vec("uvirtcount__imm", "uvirtcount_one__imm")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sa)) => Some(Instruction.UvirtcountImm(sa, modifiers, guard))
        case _ => None
      }
  }
  object UvirtcountReg extends Definition("UvirtcountReg", "UVIRTCOUNT", Vec("uvirtcount__UR", "uvirtcount_one__UR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA)) => Some(Instruction.UvirtcountReg(srcA, modifiers, guard))
        case _ => None
      }
  }
  object VabsdiffRegPredRegAddressImmReg
      extends Definition("VabsdiffRegPredRegAddressImmReg", "VABSDIFF", Vec("vabsdiff__RCxR_RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC)
            ) =>
          Some(Instruction.VabsdiffRegPredRegAddressImmReg(dst, pu, srcA, srcB, sb_offset, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VabsdiffRegPredRegConstReg
      extends Definition("VabsdiffRegPredRegConstReg", "VABSDIFF", Vec("vabsdiff__RCR_RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VabsdiffRegPredRegConstReg(dst, pu, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VabsdiffRegPredRegImmReg
      extends Definition("VabsdiffRegPredRegImmReg", "VABSDIFF", Vec("vabsdiff__RuIR_RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VabsdiffRegPredRegImmReg(dst, pu, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VabsdiffRegPredRegRegAddressImm
      extends Definition("VabsdiffRegPredRegRegAddressImm", "VABSDIFF", Vec("vabsdiff__RRCx_RRCx")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Address(srcC),
              InstructionOperand.Immediate(sc_offset)
            ) =>
          Some(Instruction.VabsdiffRegPredRegRegAddressImm(dst, pu, srcA, srcB, srcC, sc_offset, modifiers, guard))
        case _ => None
      }
  }
  object VabsdiffRegPredRegRegConst
      extends Definition("VabsdiffRegPredRegRegConst", "VABSDIFF", Vec("vabsdiff__RRC_RRC")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(sc)
            ) => Some(Instruction.VabsdiffRegPredRegRegConst(dst, pu, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object VabsdiffRegPredRegRegImm
      extends Definition("VabsdiffRegPredRegRegImm", "VABSDIFF", Vec("vabsdiff__RRuI_RRI")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.VabsdiffRegPredRegRegImm(dst, pu, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object VabsdiffRegPredRegRegReg extends Definition(
        "VabsdiffRegPredRegRegReg",
        "VABSDIFF",
        Vec("vabsdiff__RRR_RRR", "vabsdiff__RRU_RRU", "vabsdiff__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VabsdiffRegPredRegRegReg(dst, pu, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VabsdiffRegRegAddressImmReg
      extends Definition("VabsdiffRegRegAddressImmReg", "VABSDIFF", Vec("vabsdiff__RCxR_RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VabsdiffRegRegAddressImmReg(dst, srcA, srcB, sb_offset, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VabsdiffRegRegConstReg extends Definition("VabsdiffRegRegConstReg", "VABSDIFF", Vec("vabsdiff__RCR_RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VabsdiffRegRegConstReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VabsdiffRegRegImmReg extends Definition(
        "VabsdiffRegRegImmReg",
        "VABSDIFF",
        Vec(
          "Imm VABSDIFF",
          "Imm_VABSDIFF",
          "SImm_VABSDIFF",
          "a32 Imm VABSDIFF",
          "a32_Imm_VABSDIFF",
          "a32_SImm_VABSDIFF",
          "a8 Imm VABSDIFF",
          "a8_Imm_VABSDIFF",
          "a8_SImm_VABSDIFF",
          "vabsdiff__RuIR_RIR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VabsdiffRegRegImmReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VabsdiffRegRegRegAddressImm
      extends Definition("VabsdiffRegRegRegAddressImm", "VABSDIFF", Vec("vabsdiff__RRCx_RRCx")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Address(srcC),
              InstructionOperand.Immediate(sc_offset)
            ) => Some(Instruction.VabsdiffRegRegRegAddressImm(dst, srcA, srcB, srcC, sc_offset, modifiers, guard))
        case _ => None
      }
  }
  object VabsdiffRegRegRegConst extends Definition("VabsdiffRegRegRegConst", "VABSDIFF", Vec("vabsdiff__RRC_RRC")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(sc)
            ) => Some(Instruction.VabsdiffRegRegRegConst(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object VabsdiffRegRegRegImm extends Definition("VabsdiffRegRegRegImm", "VABSDIFF", Vec("vabsdiff__RRuI_RRI")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.VabsdiffRegRegRegImm(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object VabsdiffRegRegRegReg extends Definition(
        "VabsdiffRegRegRegReg",
        "VABSDIFF",
        Vec(
          "VABSDIFF",
          "a32 VABSDIFF",
          "a32 b32 VABSDIFF",
          "a32 b8 VABSDIFF",
          "a32_VABSDIFF",
          "a32_b32_VABSDIFF",
          "a32_b8_VABSDIFF",
          "a8 VABSDIFF",
          "a8 b32 VABSDIFF",
          "a8 b8 VABSDIFF",
          "a8_VABSDIFF",
          "a8_b32_VABSDIFF",
          "a8_b8_VABSDIFF",
          "b32 VABSDIFF",
          "b32_VABSDIFF",
          "b8 VABSDIFF",
          "b8_VABSDIFF",
          "vabsdiff__RRR_RRR",
          "vabsdiff__RRU_RRU",
          "vabsdiff__RUR_RUR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VabsdiffRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Vabsdiff2RegRegImmReg extends Definition("Vabsdiff2RegRegImmReg", "VABSDIFF2", Vec("Imm VABSDIFF2")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Vabsdiff2RegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Vabsdiff2RegRegRegReg extends Definition("Vabsdiff2RegRegRegReg", "VABSDIFF2", Vec("VABSDIFF2")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Vabsdiff2RegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Vabsdiff4RegPredRegAddressImmReg
      extends Definition("Vabsdiff4RegPredRegAddressImmReg", "VABSDIFF4", Vec("vabsdiff4__RCxR_RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC)
            ) =>
          Some(Instruction.Vabsdiff4RegPredRegAddressImmReg(dst, pu, srcA, srcB, sb_offset, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Vabsdiff4RegPredRegConstReg
      extends Definition("Vabsdiff4RegPredRegConstReg", "VABSDIFF4", Vec("vabsdiff4__RCR_RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Vabsdiff4RegPredRegConstReg(dst, pu, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Vabsdiff4RegPredRegImmReg
      extends Definition("Vabsdiff4RegPredRegImmReg", "VABSDIFF4", Vec("vabsdiff4__RuIR_RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Vabsdiff4RegPredRegImmReg(dst, pu, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Vabsdiff4RegPredRegRegAddressImm
      extends Definition("Vabsdiff4RegPredRegRegAddressImm", "VABSDIFF4", Vec("vabsdiff4__RRCx_RRCx")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Address(srcC),
              InstructionOperand.Immediate(sc_offset)
            ) =>
          Some(Instruction.Vabsdiff4RegPredRegRegAddressImm(dst, pu, srcA, srcB, srcC, sc_offset, modifiers, guard))
        case _ => None
      }
  }
  object Vabsdiff4RegPredRegRegConst
      extends Definition("Vabsdiff4RegPredRegRegConst", "VABSDIFF4", Vec("vabsdiff4__RRC_RRC")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(sc)
            ) => Some(Instruction.Vabsdiff4RegPredRegRegConst(dst, pu, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object Vabsdiff4RegPredRegRegImm
      extends Definition("Vabsdiff4RegPredRegRegImm", "VABSDIFF4", Vec("vabsdiff4__RRuI_RRI")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.Vabsdiff4RegPredRegRegImm(dst, pu, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object Vabsdiff4RegPredRegRegReg extends Definition(
        "Vabsdiff4RegPredRegRegReg",
        "VABSDIFF4",
        Vec("vabsdiff4__RRR_RRR", "vabsdiff4__RRU_RRU", "vabsdiff4__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Vabsdiff4RegPredRegRegReg(dst, pu, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Vabsdiff4RegRegAddressImmReg
      extends Definition("Vabsdiff4RegRegAddressImmReg", "VABSDIFF4", Vec("vabsdiff4__RCxR_RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Vabsdiff4RegRegAddressImmReg(dst, srcA, srcB, sb_offset, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Vabsdiff4RegRegConstReg extends Definition("Vabsdiff4RegRegConstReg", "VABSDIFF4", Vec("vabsdiff4__RCR_RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Vabsdiff4RegRegConstReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Vabsdiff4RegRegImmReg extends Definition(
        "Vabsdiff4RegRegImmReg",
        "VABSDIFF4",
        Vec("Imm VABSDIFF4", "Imm_VABSDIFF4", "SImm_VABSDIFF4", "vabsdiff4__RuIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Vabsdiff4RegRegImmReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Vabsdiff4RegRegRegAddressImm
      extends Definition("Vabsdiff4RegRegRegAddressImm", "VABSDIFF4", Vec("vabsdiff4__RRCx_RRCx")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Address(srcC),
              InstructionOperand.Immediate(sc_offset)
            ) => Some(Instruction.Vabsdiff4RegRegRegAddressImm(dst, srcA, srcB, srcC, sc_offset, modifiers, guard))
        case _ => None
      }
  }
  object Vabsdiff4RegRegRegConst extends Definition("Vabsdiff4RegRegRegConst", "VABSDIFF4", Vec("vabsdiff4__RRC_RRC")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(sc)
            ) => Some(Instruction.Vabsdiff4RegRegRegConst(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object Vabsdiff4RegRegRegImm extends Definition("Vabsdiff4RegRegRegImm", "VABSDIFF4", Vec("vabsdiff4__RRuI_RRI")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.Vabsdiff4RegRegRegImm(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object Vabsdiff4RegRegRegReg extends Definition(
        "Vabsdiff4RegRegRegReg",
        "VABSDIFF4",
        Vec("VABSDIFF4", "vabsdiff4__RRR_RRR", "vabsdiff4__RRU_RRU", "vabsdiff4__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Vabsdiff4RegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VaddRegRegReg extends Definition(
        "VaddRegRegReg",
        "VADD",
        Vec(
          "Imm VADD",
          "Imm_VADD",
          "SImm_VADD",
          "a32 Imm VADD",
          "a32_Imm_VADD",
          "a32_SImm_VADD",
          "a8 Imm VADD",
          "a8_Imm_VADD",
          "a8_SImm_VADD"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VaddRegRegReg(dst, srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VaddRegRegRegReg extends Definition(
        "VaddRegRegRegReg",
        "VADD",
        Vec(
          "VADD",
          "a32 VADD",
          "a32 b32 VADD",
          "a32 b8 VADD",
          "a32_VADD",
          "a32_b32_VADD",
          "a32_b8_VADD",
          "a8 VADD",
          "a8 b32 VADD",
          "a8 b8 VADD",
          "a8_VADD",
          "a8_b32_VADD",
          "a8_b8_VADD",
          "b32 VADD",
          "b32_VADD",
          "b8 VADD",
          "b8_VADD"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VaddRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Vadd2RegRegReg extends Definition("Vadd2RegRegReg", "VADD2", Vec("Imm VADD2")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Vadd2RegRegReg(dst, srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Vadd2RegRegRegReg extends Definition("Vadd2RegRegRegReg", "VADD2", Vec("VADD2")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Vadd2RegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Vadd4RegRegReg extends Definition("Vadd4RegRegReg", "VADD4", Vec("Imm VADD4")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Vadd4RegRegReg(dst, srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Vadd4RegRegRegReg extends Definition("Vadd4RegRegRegReg", "VADD4", Vec("VADD4")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Vadd4RegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VhmnmxRegRegAddressImmRegPred
      extends Definition("VhmnmxRegRegAddressImmRegPred", "VHMNMX", Vec("vhmnmx__RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.VhmnmxRegRegAddressImmRegPred(dst, srcA, srcB, sb_offset, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object VhmnmxRegRegConstRegPred extends Definition("VhmnmxRegRegConstRegPred", "VHMNMX", Vec("vhmnmx__RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.VhmnmxRegRegConstRegPred(dst, srcA, sb, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object VhmnmxRegRegImmImmRegPred extends Definition("VhmnmxRegRegImmImmRegPred", "VHMNMX", Vec("vhmnmx__RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Immediate(sb1),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.VhmnmxRegRegImmImmRegPred(dst, srcA, sb, sb1, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object VhmnmxRegRegRegRegPred
      extends Definition("VhmnmxRegRegRegRegPred", "VHMNMX", Vec("vhmnmx__RRR", "vhmnmx__RUR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.VhmnmxRegRegRegRegPred(dst, srcA, srcB, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object ViaddRegRegAddressImm extends Definition("ViaddRegRegAddressImm", "VIADD", Vec("viadd__RCxR_RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.ViaddRegRegAddressImm(dst, srcA, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object ViaddRegRegConst extends Definition("ViaddRegRegConst", "VIADD", Vec("viadd__RCR_RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb)
            ) => Some(Instruction.ViaddRegRegConst(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object ViaddRegRegImm extends Definition("ViaddRegRegImm", "VIADD", Vec("viadd__RuIR_RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.ViaddRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object ViaddRegRegReg extends Definition("ViaddRegRegReg", "VIADD", Vec("viadd__RRR_RRR", "viadd__RUR_RUR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.ViaddRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object ViaddmnmxRegRegAddressImmRegPred
      extends Definition("ViaddmnmxRegRegAddressImmRegPred", "VIADDMNMX", Vec("viaddmnmx__RCxR_RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) =>
          Some(Instruction.ViaddmnmxRegRegAddressImmRegPred(dst, srcA, srcB, sb_offset, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object ViaddmnmxRegRegConstRegPred
      extends Definition("ViaddmnmxRegRegConstRegPred", "VIADDMNMX", Vec("viaddmnmx__RCR_RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.ViaddmnmxRegRegConstRegPred(dst, srcA, sb, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object ViaddmnmxRegRegImmRegPred
      extends Definition("ViaddmnmxRegRegImmRegPred", "VIADDMNMX", Vec("viaddmnmx__RuIR_RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.ViaddmnmxRegRegImmRegPred(dst, srcA, sb, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object ViaddmnmxRegRegRegAddressImmPred
      extends Definition("ViaddmnmxRegRegRegAddressImmPred", "VIADDMNMX", Vec("viaddmnmx__RRCx_RRCx")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Address(srcC),
              InstructionOperand.Immediate(sc_offset),
              InstructionOperand.Predicate(pp)
            ) =>
          Some(Instruction.ViaddmnmxRegRegRegAddressImmPred(dst, srcA, srcB, srcC, sc_offset, pp, modifiers, guard))
        case _ => None
      }
  }
  object ViaddmnmxRegRegRegConstPred
      extends Definition("ViaddmnmxRegRegRegConstPred", "VIADDMNMX", Vec("viaddmnmx__RRC_RRC")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(sc),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.ViaddmnmxRegRegRegConstPred(dst, srcA, srcB, sc, pp, modifiers, guard))
        case _ => None
      }
  }
  object ViaddmnmxRegRegRegImmPred
      extends Definition("ViaddmnmxRegRegRegImmPred", "VIADDMNMX", Vec("viaddmnmx__RRuI_RRI")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.ViaddmnmxRegRegRegImmPred(dst, srcA, srcB, sc, pp, modifiers, guard))
        case _ => None
      }
  }
  object ViaddmnmxRegRegRegRegPred extends Definition(
        "ViaddmnmxRegRegRegRegPred",
        "VIADDMNMX",
        Vec("viaddmnmx__RRR_RRR", "viaddmnmx__RRU_RRU", "viaddmnmx__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.ViaddmnmxRegRegRegRegPred(dst, srcA, srcB, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object VildRegAddress extends Definition("VildRegAddress", "VILD", Vec("I VILD", "VILD")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Address(srcA)) =>
          Some(Instruction.VildRegAddress(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object VimnmxRegPredPredRegAddressImmPred
      extends Definition("VimnmxRegPredPredRegAddressImmPred", "VIMNMX", Vec("vimnmx_pred__RCxR_RCxR")) {
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
          Some(Instruction.VimnmxRegPredPredRegAddressImmPred(dst, pu, pv, srcA, srcB, sb_offset, pp, modifiers, guard))
        case _ => None
      }
  }
  object VimnmxRegPredPredRegConstPred
      extends Definition("VimnmxRegPredPredRegConstPred", "VIMNMX", Vec("vimnmx_pred__RCR_RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.VimnmxRegPredPredRegConstPred(dst, pu, pv, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object VimnmxRegPredPredRegImmPred
      extends Definition("VimnmxRegPredPredRegImmPred", "VIMNMX", Vec("vimnmx__RIR_RsIR", "vimnmx_pred__RIR_RsIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.VimnmxRegPredPredRegImmPred(dst, pu, pv, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object VimnmxRegPredPredRegRegPred extends Definition(
        "VimnmxRegPredPredRegRegPred",
        "VIMNMX",
        Vec("vimnmx__RRR_RRR", "vimnmx__RUR_RUR", "vimnmx_pred__RRR_RRR", "vimnmx_pred__RUR_RUR")
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
            ) => Some(Instruction.VimnmxRegPredPredRegRegPred(dst, pu, pv, srcA, srcB, pp, modifiers, guard))
        case _ => None
      }
  }
  object VimnmxRegRegAddressImmPred
      extends Definition("VimnmxRegRegAddressImmPred", "VIMNMX", Vec("vimnmx__RCxR_RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.VimnmxRegRegAddressImmPred(dst, srcA, srcB, sb_offset, pp, modifiers, guard))
        case _ => None
      }
  }
  object VimnmxRegRegConstPred extends Definition("VimnmxRegRegConstPred", "VIMNMX", Vec("vimnmx__RCR_RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.VimnmxRegRegConstPred(dst, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object VimnmxRegRegImmPred
      extends Definition("VimnmxRegRegImmPred", "VIMNMX", Vec("vimnmx__RIR_RsIR", "vimnmx_nopred__RIR_RsIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.VimnmxRegRegImmPred(dst, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object VimnmxRegRegRegPred extends Definition(
        "VimnmxRegRegRegPred",
        "VIMNMX",
        Vec("vimnmx__RRR_RRR", "vimnmx__RUR_RUR", "vimnmx_nopred__RRR_RRR", "vimnmx_nopred__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.VimnmxRegRegRegPred(dst, srcA, srcB, pp, modifiers, guard))
        case _ => None
      }
  }
  object Vimnmx3RegRegAddressImmRegPred
      extends Definition("Vimnmx3RegRegAddressImmRegPred", "VIMNMX3", Vec("vimnmx3__RCxR_RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) =>
          Some(Instruction.Vimnmx3RegRegAddressImmRegPred(dst, srcA, srcB, sb_offset, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object Vimnmx3RegRegConstRegPred extends Definition("Vimnmx3RegRegConstRegPred", "VIMNMX3", Vec("vimnmx3__RCR_RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Vimnmx3RegRegConstRegPred(dst, srcA, sb, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object Vimnmx3RegRegImmRegPred extends Definition("Vimnmx3RegRegImmRegPred", "VIMNMX3", Vec("vimnmx3__RuIR_RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Vimnmx3RegRegImmRegPred(dst, srcA, sb, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object Vimnmx3RegRegRegRegPred
      extends Definition("Vimnmx3RegRegRegRegPred", "VIMNMX3", Vec("vimnmx3__RRR_RRR", "vimnmx3__RUR_RUR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Vimnmx3RegRegRegRegPred(dst, srcA, srcB, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object VmadRegRegImmReg extends Definition("VmadRegRegImmReg", "VMAD", Vec("Imm_VMAD")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VmadRegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VmadRegRegReg extends Definition(
        "VmadRegRegReg",
        "VMAD",
        Vec(
          "Imm VMAD",
          "SImm_VMAD",
          "a32 Imm VMAD",
          "a32_Imm_VMAD",
          "a32_SImm_VMAD",
          "a8 Imm VMAD",
          "a8_Imm_VMAD",
          "a8_SImm_VMAD"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VmadRegRegReg(dst, srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VmadRegRegRegReg extends Definition(
        "VmadRegRegRegReg",
        "VMAD",
        Vec(
          "VMAD",
          "a32 VMAD",
          "a32 b32 VMAD",
          "a32 b8 VMAD",
          "a32_VMAD",
          "a32_b32_VMAD",
          "a32_b8_VMAD",
          "a8 VMAD",
          "a8 b32 VMAD",
          "a8 b8 VMAD",
          "a8_VMAD",
          "a8_b32_VMAD",
          "a8_b8_VMAD",
          "b32 VMAD",
          "b32_VMAD",
          "b8 VMAD",
          "b8_VMAD"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VmadRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VmnmxRegRegImmReg extends Definition(
        "VmnmxRegRegImmReg",
        "VMNMX",
        Vec(
          "Imm VMNMX",
          "Imm_VMNMX",
          "SImm_VMNMX",
          "a32 Imm VMNMX",
          "a32_Imm_VMNMX",
          "a32_SImm_VMNMX",
          "a8 Imm VMNMX",
          "a8_Imm_VMNMX",
          "a8_SImm_VMNMX"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VmnmxRegRegImmReg(dst, srcA, sImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VmnmxRegRegRegReg extends Definition(
        "VmnmxRegRegRegReg",
        "VMNMX",
        Vec(
          "VMNMX",
          "a32 VMNMX",
          "a32 b32 VMNMX",
          "a32 b8 VMNMX",
          "a32_VMNMX",
          "a32_b32_VMNMX",
          "a32_b8_VMNMX",
          "a8 VMNMX",
          "a8 b32 VMNMX",
          "a8 b8 VMNMX",
          "a8_VMNMX",
          "a8_b32_VMNMX",
          "a8_b8_VMNMX",
          "b32 VMNMX",
          "b32_VMNMX",
          "b8 VMNMX",
          "b8_VMNMX"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VmnmxRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Vmnmx2RegRegImmReg extends Definition("Vmnmx2RegRegImmReg", "VMNMX2", Vec("Imm VMNMX2")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Vmnmx2RegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Vmnmx2RegRegRegReg extends Definition("Vmnmx2RegRegRegReg", "VMNMX2", Vec("VMNMX2")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Vmnmx2RegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Vmnmx4RegRegImmReg extends Definition("Vmnmx4RegRegImmReg", "VMNMX4", Vec("Imm VMNMX4")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Vmnmx4RegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Vmnmx4RegRegRegReg extends Definition("Vmnmx4RegRegRegReg", "VMNMX4", Vec("VMNMX4")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Vmnmx4RegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VoteImm extends Definition("VoteImm", "VOTE", Vec("VOTE.VTG")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(uImm)) => Some(Instruction.VoteImm(uImm, modifiers, guard))
        case _ => None
      }
  }
  object VotePredPred extends Definition("VotePredPred", "VOTE", Vec("VOTE")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(dstPredicate), InstructionOperand.Predicate(pa)) =>
          Some(Instruction.VotePredPred(dstPredicate, pa, modifiers, guard))
        case _ => None
      }
  }
  object VoteRegPredPred extends Definition("VoteRegPredPred", "VOTE", Vec("VOTE", "vote_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.VoteRegPredPred(dst, dstPredicate, pa, modifiers, guard))
        case _ => None
      }
  }
  object VoteuRegPredPred extends Definition("VoteuRegPredPred", "VOTEU", Vec("voteu_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.VoteuRegPredPred(dst, uPu, pp, modifiers, guard))
        case _ => None
      }
  }
  object VoteVtgImm extends Definition("VoteVtgImm", "VOTE_VTG", Vec("vote_vtg_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sa)) => Some(Instruction.VoteVtgImm(sa, modifiers, guard))
        case _ => None
      }
  }
  object VoteVtgPredImm extends Definition("VoteVtgPredImm", "VOTE_VTG", Vec("vote_vtg_cmp_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pu), InstructionOperand.Immediate(sa)) =>
          Some(Instruction.VoteVtgPredImm(pu, sa, modifiers, guard))
        case _ => None
      }
  }
  object VoteVtgPredPredImmPred extends Definition("VoteVtgPredPredImmPred", "VOTE_VTG", Vec("vote_vtg_bop_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Immediate(sa),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.VoteVtgPredPredImmPred(pu, pv, sa, pp, modifiers, guard))
        case _ => None
      }
  }
  object Vsel2RegRegImmPred extends Definition("Vsel2RegRegImmPred", "VSEL2", Vec("Imm VSEL2")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.Vsel2RegRegImmPred(dst, srcA, uImm, pa, modifiers, guard))
        case _ => None
      }
  }
  object Vsel2RegRegRegPred extends Definition("Vsel2RegRegRegPred", "VSEL2", Vec("VSEL2")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.Vsel2RegRegRegPred(dst, srcA, srcB, pa, modifiers, guard))
        case _ => None
      }
  }
  object Vsel4RegRegImmPred extends Definition("Vsel4RegRegImmPred", "VSEL4", Vec("Imm VSEL4")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.Vsel4RegRegImmPred(dst, srcA, uImm, pa, modifiers, guard))
        case _ => None
      }
  }
  object Vsel4RegRegRegPred extends Definition("Vsel4RegRegRegPred", "VSEL4", Vec("VSEL4")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.Vsel4RegRegRegPred(dst, srcA, srcB, pa, modifiers, guard))
        case _ => None
      }
  }
  object VsetRegRegImmReg extends Definition(
        "VsetRegRegImmReg",
        "VSET",
        Vec(
          "Imm VSET",
          "Imm_VSET",
          "SImm_VSET",
          "a32 Imm VSET",
          "a32_Imm_VSET",
          "a32_SImm_VSET",
          "a8 Imm VSET",
          "a8_Imm_VSET",
          "a8_SImm_VSET"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VsetRegRegImmReg(dst, srcA, sImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VsetRegRegRegReg extends Definition(
        "VsetRegRegRegReg",
        "VSET",
        Vec(
          "VSET",
          "a32 VSET",
          "a32 b32 VSET",
          "a32 b8 VSET",
          "a32_VSET",
          "a32_b32_VSET",
          "a32_b8_VSET",
          "a8 VSET",
          "a8 b32 VSET",
          "a8 b8 VSET",
          "a8_VSET",
          "a8_b32_VSET",
          "a8_b8_VSET",
          "b32 VSET",
          "b32_VSET",
          "b8 VSET",
          "b8_VSET"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VsetRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Vset2RegRegImmReg extends Definition("Vset2RegRegImmReg", "VSET2", Vec("Imm VSET2")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Vset2RegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Vset2RegRegRegReg extends Definition("Vset2RegRegRegReg", "VSET2", Vec("VSET2")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Vset2RegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Vset4RegRegImmReg extends Definition("Vset4RegRegImmReg", "VSET4", Vec("Imm VSET4")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Vset4RegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Vset4RegRegRegReg extends Definition("Vset4RegRegRegReg", "VSET4", Vec("VSET4")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Vset4RegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VsetpPredPredRegImmPred extends Definition(
        "VsetpPredPredRegImmPred",
        "VSETP",
        Vec(
          "Imm VSETP",
          "Imm_VSETP_bop",
          "SImm_VSETP_bop",
          "a32 Imm VSETP",
          "a32_Imm_VSETP_bop",
          "a32_SImm_VSETP_bop",
          "a8 Imm VSETP",
          "a8_Imm_VSETP_bop",
          "a8_SImm_VSETP_bop"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sImm),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.VsetpPredPredRegImmPred(dstPredicate, nPd, srcA, sImm, pa, modifiers, guard))
        case _ => None
      }
  }
  object VsetpPredPredRegRegPred extends Definition(
        "VsetpPredPredRegRegPred",
        "VSETP",
        Vec(
          "VSETP",
          "VSETP_bop",
          "a32 VSETP",
          "a32 b32 VSETP",
          "a32 b8 VSETP",
          "a32_VSETP_bop",
          "a32_b32_VSETP_bop",
          "a32_b8_VSETP_bop",
          "a8 VSETP",
          "a8 b32 VSETP",
          "a8 b8 VSETP",
          "a8_VSETP_bop",
          "a8_b32_VSETP_bop",
          "a8_b8_VSETP_bop",
          "b32 VSETP",
          "b32_VSETP_bop",
          "b8 VSETP",
          "b8_VSETP_bop"
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
            ) => Some(Instruction.VsetpPredPredRegRegPred(dstPredicate, nPd, srcA, srcB, pa, modifiers, guard))
        case _ => None
      }
  }
  object VsetpPredRegImm extends Definition(
        "VsetpPredRegImm",
        "VSETP",
        Vec(
          "Imm_VSETP",
          "NoBop Imm VSETP",
          "NoBop a32 Imm VSETP",
          "NoBop a8 Imm VSETP",
          "SImm_VSETP",
          "a32_Imm_VSETP",
          "a32_SImm_VSETP",
          "a8_Imm_VSETP",
          "a8_SImm_VSETP"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sImm)
            ) => Some(Instruction.VsetpPredRegImm(dstPredicate, srcA, sImm, modifiers, guard))
        case _ => None
      }
  }
  object VsetpPredRegReg extends Definition(
        "VsetpPredRegReg",
        "VSETP",
        Vec(
          "NoBop VSETP",
          "NoBop a32 VSETP",
          "NoBop a32 b32 VSETP",
          "NoBop a32 b8 VSETP",
          "NoBop a8 VSETP",
          "NoBop a8 b32 VSETP",
          "NoBop a8 b8 VSETP",
          "NoBop b32 VSETP",
          "NoBop b8 VSETP",
          "VSETP",
          "a32_VSETP",
          "a32_b32_VSETP",
          "a32_b8_VSETP",
          "a8_VSETP",
          "a8_b32_VSETP",
          "a8_b8_VSETP",
          "b32_VSETP",
          "b8_VSETP"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.VsetpPredRegReg(dstPredicate, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object VshlRegRegImmReg extends Definition(
        "VshlRegRegImmReg",
        "VSHL",
        Vec(
          "Imm VSHL",
          "Imm_VSHL",
          "SImm_VSHL",
          "a32 Imm VSHL",
          "a32_Imm_VSHL",
          "a32_SImm_VSHL",
          "a8 Imm VSHL",
          "a8_Imm_VSHL",
          "a8_SImm_VSHL"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VshlRegRegImmReg(dst, srcA, sImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VshlRegRegRegReg extends Definition(
        "VshlRegRegRegReg",
        "VSHL",
        Vec(
          "VSHL",
          "a32 VSHL",
          "a32 b32 VSHL",
          "a32 b8 VSHL",
          "a32_VSHL",
          "a32_b32_VSHL",
          "a32_b8_VSHL",
          "a8 VSHL",
          "a8 b32 VSHL",
          "a8 b8 VSHL",
          "a8_VSHL",
          "a8_b32_VSHL",
          "a8_b8_VSHL",
          "b32 VSHL",
          "b32_VSHL",
          "b8 VSHL",
          "b8_VSHL"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VshlRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VshrRegRegImmReg extends Definition(
        "VshrRegRegImmReg",
        "VSHR",
        Vec(
          "Imm VSHL",
          "Imm_VSHR",
          "SImm_VSHR",
          "a32 Imm VSHL",
          "a32_Imm_VSHR",
          "a32_SImm_VSHR",
          "a8 Imm VSHL",
          "a8_Imm_VSHR",
          "a8_SImm_VSHR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VshrRegRegImmReg(dst, srcA, sImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VshrRegRegRegReg extends Definition(
        "VshrRegRegRegReg",
        "VSHR",
        Vec(
          "VSHL",
          "VSHR",
          "a32 VSHL",
          "a32 b32 VSHL",
          "a32 b8 VSHL",
          "a32_VSHR",
          "a32_b32_VSHR",
          "a32_b8_VSHR",
          "a8 VSHL",
          "a8 b32 VSHL",
          "a8 b8 VSHL",
          "a8_VSHR",
          "a8_b32_VSHR",
          "a8_b8_VSHR",
          "b32 VSHL",
          "b32_VSHR",
          "b8 VSHL",
          "b8_VSHR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VshrRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object WarpgroupNoOperands
      extends Definition("WarpgroupNoOperands", "WARPGROUP", Vec("warpgroup_arrive_", "warpgroup_wait_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.WarpgroupNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object WarpgroupImm extends Definition("WarpgroupImm", "WARPGROUP", Vec("warpgroup_depbar_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(gsbcnt)) => Some(Instruction.WarpgroupImm(gsbcnt, modifiers, guard))
        case _ => None
      }
  }
  object WarpgroupsetReg
      extends Definition("WarpgroupsetReg", "WARPGROUPSET", Vec("warpgroupset__AC", "warpgroupset__SB")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcB)) => Some(Instruction.WarpgroupsetReg(srcB, modifiers, guard))
        case _ => None
      }
  }
  object WarpsyncNoOperands extends Definition("WarpsyncNoOperands", "WARPSYNC", Vec("warpsync__RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.WarpsyncNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object WarpsyncAddressImm extends Definition("WarpsyncAddressImm", "WARPSYNC", Vec("warpsync__RCXR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcB), InstructionOperand.Immediate(sb_offset)) =>
          Some(Instruction.WarpsyncAddressImm(srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object WarpsyncConst extends Definition("WarpsyncConst", "WARPSYNC", Vec("warpsync__RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(sb)) => Some(Instruction.WarpsyncConst(sb, modifiers, guard))
        case _ => None
      }
  }
  object WarpsyncImm extends Definition(
        "WarpsyncImm",
        "WARPSYNC",
        Vec("warpsync__RIR", "warpsync_collective__RIR", "warpsync_rel__RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb)) => Some(Instruction.WarpsyncImm(sb, modifiers, guard))
        case _ => None
      }
  }
  object WarpsyncPred extends Definition("WarpsyncPred", "WARPSYNC", Vec("warpsync__RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp)) => Some(Instruction.WarpsyncPred(pp, modifiers, guard))
        case _ => None
      }
  }
  object WarpsyncPredAddressImm extends Definition("WarpsyncPredAddressImm", "WARPSYNC", Vec("warpsync__RCXR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pp),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.WarpsyncPredAddressImm(pp, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object WarpsyncPredConst extends Definition("WarpsyncPredConst", "WARPSYNC", Vec("warpsync__RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.ConstantBank(sb)) =>
          Some(Instruction.WarpsyncPredConst(pp, sb, modifiers, guard))
        case _ => None
      }
  }
  object WarpsyncPredImm extends Definition(
        "WarpsyncPredImm",
        "WARPSYNC",
        Vec("warpsync__RIR", "warpsync_collective__RIR", "warpsync_rel__RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.WarpsyncPredImm(pp, sb, modifiers, guard))
        case _ => None
      }
  }
  object WarpsyncPredReg extends Definition("WarpsyncPredReg", "WARPSYNC", Vec("warpsync__RRR", "warpsync__RUR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.Register(srcA)) =>
          Some(Instruction.WarpsyncPredReg(pp, srcA, modifiers, guard))
        case _ => None
      }
  }
  object WarpsyncPredRegImm
      extends Definition("WarpsyncPredRegImm", "WARPSYNC", Vec("warpsync_collective__RRR", "warpsync_rel__RRR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pp),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sImm)
            ) => Some(Instruction.WarpsyncPredRegImm(pp, srcA, sImm, modifiers, guard))
        case _ => None
      }
  }
  object WarpsyncReg extends Definition("WarpsyncReg", "WARPSYNC", Vec("warpsync__RRR", "warpsync__RUR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA)) => Some(Instruction.WarpsyncReg(srcA, modifiers, guard))
        case _ => None
      }
  }
  object WarpsyncRegImm
      extends Definition("WarpsyncRegImm", "WARPSYNC", Vec("warpsync_collective__RRR", "warpsync_rel__RRR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA), InstructionOperand.Immediate(sImm)) =>
          Some(Instruction.WarpsyncRegImm(srcA, sImm, modifiers, guard))
        case _ => None
      }
  }
  object XmadRegRegConstReg extends Definition("XmadRegRegConstReg", "XMAD", Vec("ConstB_XMAD")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.XmadRegRegConstReg(dst, srcA, srcConst, srcC, modifiers, guard))
        case _ => None
      }
  }
  object XmadRegRegImmReg extends Definition("XmadRegRegImmReg", "XMAD", Vec("ImmB_XMAD", "SImmB_XMAD")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.XmadRegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object XmadRegRegRegConst extends Definition("XmadRegRegRegConst", "XMAD", Vec("ConstC_XMAD")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.XmadRegRegRegConst(dst, srcA, srcB, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object XmadRegRegRegReg extends Definition("XmadRegRegRegReg", "XMAD", Vec("XMAD")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.XmadRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object YieldNoOperands extends Definition("YieldNoOperands", "YIELD", Vec("yield_inst_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.YieldNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object YieldPred extends Definition("YieldPred", "YIELD", Vec("yield_inst_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp)) => Some(Instruction.YieldPred(pp, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x002NoOperands extends Definition("Hir0x002NoOperands", "__HIR0X002", Vec("ttucctl_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.Hir0x002NoOperands(modifiers, guard))
        case _ => None
      }
  }
  object Hir0x003NoOperands extends Definition("Hir0x003NoOperands", "__HIR0X003", Vec("ttuclose_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.Hir0x003NoOperands(modifiers, guard))
        case _ => None
      }
  }
  object Hir0x003Pred extends Definition("Hir0x003Pred", "__HIR0X003", Vec("ttuclose_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pu)) => Some(Instruction.Hir0x003Pred(pu, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x004NoOperands extends Definition("Hir0x004NoOperands", "__HIR0X004", Vec("ttugo_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.Hir0x004NoOperands(modifiers, guard))
        case _ => None
      }
  }
  object Hir0x005PredRegRegImm
      extends Definition("Hir0x005PredRegRegImm", "__HIR0X005", Vec("ttuld_", "ttuld__close", "ttuld__no_close")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Immediate(immU16)
            ) => Some(Instruction.Hir0x005PredRegRegImm(pu, rd2, dst, immU16, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x005RegRegImm
      extends Definition("Hir0x005RegRegImm", "__HIR0X005", Vec("ttuld_", "ttuld__close", "ttuld__no_close")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Immediate(immU16)
            ) => Some(Instruction.Hir0x005RegRegImm(rd2, dst, immU16, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x006Imm extends Definition("Hir0x006Imm", "__HIR0X006", Vec("ttumacrofuse_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb)) => Some(Instruction.Hir0x006Imm(sb, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x007NoOperands extends Definition("Hir0x007NoOperands", "__HIR0X007", Vec("ttuopen_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.Hir0x007NoOperands(modifiers, guard))
        case _ => None
      }
  }
  object Hir0x008ImmRegReg extends Definition("Hir0x008ImmRegReg", "__HIR0X008", Vec("ttust_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Immediate(immU16),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Hir0x008ImmRegReg(immU16, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x020NoOperands extends Definition("Hir0x020NoOperands", "__HIR0X020", Vec("ide_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.Hir0x020NoOperands(modifiers, guard))
        case _ => None
      }
  }
  object Hir0x020Imm extends Definition("Hir0x020Imm", "__HIR0X020", Vec("ide_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb)) => Some(Instruction.Hir0x020Imm(sb, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x120AddressImm extends Definition("Hir0x120AddressImm", "__HIR0X120", Vec("nanotrap__CX")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcB), InstructionOperand.Immediate(sb_offset)) =>
          Some(Instruction.Hir0x120AddressImm(srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x120Const extends Definition("Hir0x120Const", "__HIR0X120", Vec("nanotrap__C")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(sb)) => Some(Instruction.Hir0x120Const(sb, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x120Imm extends Definition("Hir0x120Imm", "__HIR0X120", Vec("nanotrap__I")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb)) => Some(Instruction.Hir0x120Imm(sb, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x120PredAddressImm extends Definition("Hir0x120PredAddressImm", "__HIR0X120", Vec("nanotrap__CX")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pp),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.Hir0x120PredAddressImm(pp, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x120PredConst extends Definition("Hir0x120PredConst", "__HIR0X120", Vec("nanotrap__C")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.ConstantBank(sb)) =>
          Some(Instruction.Hir0x120PredConst(pp, sb, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x120PredImm extends Definition("Hir0x120PredImm", "__HIR0X120", Vec("nanotrap__I")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.Hir0x120PredImm(pp, sb, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x120PredReg extends Definition("Hir0x120PredReg", "__HIR0X120", Vec("nanotrap__R", "nanotrap__U")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.Register(srcB)) =>
          Some(Instruction.Hir0x120PredReg(pp, srcB, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x120Reg extends Definition("Hir0x120Reg", "__HIR0X120", Vec("nanotrap__R", "nanotrap__U")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcB)) => Some(Instruction.Hir0x120Reg(srcB, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x130AddressAddressAddressAddressAddressAddressPred extends Definition(
        "Hir0x130AddressAddressAddressAddressAddressAddressPred",
        "__HIR0X130",
        Vec(
          "utcmxqmma_1cta_scale__A_gdesc",
          "utcmxqmma_1cta_scale__A_tmem",
          "utcmxqmma_1cta_scale_one__A_gdesc",
          "utcmxqmma_1cta_scale_one__A_tmem",
          "utcmxqmma_2cta_scale__A_gdesc",
          "utcmxqmma_2cta_scale__A_tmem",
          "utcmxqmma_2cta_scale_one__A_gdesc",
          "utcmxqmma_2cta_scale_one__A_tmem"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Address(srcC),
              InstructionOperand.Address(uRe),
              InstructionOperand.Address(uRh),
              InstructionOperand.Address(uRi),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.Hir0x130AddressAddressAddressAddressAddressAddressPred(
            srcA,
            srcB,
            srcC,
            uRe,
            uRh,
            uRi,
            uPp,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object Hir0x150RegRegRegRegPredRegRegReg
      extends Definition("Hir0x150RegRegRegRegPredRegRegReg", "__HIR0X150", Vec("mxqmma_scale_")) {
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
          Some(Instruction.Hir0x150RegRegRegRegPredRegRegReg(dst, srcA, srcB, srcC, uPp, re, rh, uRi, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x150RegRegRegRegRegRegReg
      extends Definition("Hir0x150RegRegRegRegRegRegReg", "__HIR0X150", Vec("mxqmma_scale_")) {
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
            ) => Some(Instruction.Hir0x150RegRegRegRegRegRegReg(dst, srcA, srcB, srcC, re, rh, uRi, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x160Reg extends Definition("Hir0x160Reg", "__HIR0X160", Vec("utcstsws_", "utcstsws_one_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcB)) => Some(Instruction.Hir0x160Reg(srcB, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x170Reg extends Definition("Hir0x170Reg", "__HIR0X170", Vec("utcldsws_", "utcldsws_one_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst)) => Some(Instruction.Hir0x170Reg(dst, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x1a0RegRegRegRegImmImm
      extends Definition("Hir0x1a0RegRegRegRegImmImm", "__HIR0X1A0", Vec("genmetadata_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(vecidx2),
              InstructionOperand.Immediate(mdidx)
            ) => Some(Instruction.Hir0x1a0RegRegRegRegImmImm(dst, srcA, srcB, srcC, vecidx2, mdidx, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x1b0RegRegRegReg extends Definition("Hir0x1b0RegRegRegReg", "__HIR0X1B0", Vec("gather_bitextract_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Hir0x1b0RegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x1e0NoOperands extends Definition("Hir0x1e0NoOperands", "__HIR0X1E0", Vec("rtt_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.Hir0x1e0NoOperands(modifiers, guard))
        case _ => None
      }
  }
  object Hir0x1f0ImmReg extends Definition("Hir0x1f0ImmReg", "__HIR0X1F0", Vec("r2b_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(barname), InstructionOperand.Register(srcB)) =>
          Some(Instruction.Hir0x1f0ImmReg(barname, srcB, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x1f2Reg extends Definition("Hir0x1f2Reg", "__HIR0X1F2", Vec("ucgabar_get_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst)) => Some(Instruction.Hir0x1f2Reg(dst, modifiers, guard))
        case _ => None
      }
  }
  object Hir0x1f3Reg extends Definition("Hir0x1f3Reg", "__HIR0X1F3", Vec("ucgabar_set_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcB)) => Some(Instruction.Hir0x1f3Reg(srcB, modifiers, guard))
        case _ => None
      }
  }
  val definitions: Vec[Definition] = Vec(
    UviaddRegRegImm,
    UviaddRegRegReg,
    UvimnmxRegRegImmPred,
    UvimnmxRegRegRegPred,
    UvirtcountImm,
    UvirtcountReg,
    VabsdiffRegPredRegAddressImmReg,
    VabsdiffRegPredRegConstReg,
    VabsdiffRegPredRegImmReg,
    VabsdiffRegPredRegRegAddressImm,
    VabsdiffRegPredRegRegConst,
    VabsdiffRegPredRegRegImm,
    VabsdiffRegPredRegRegReg,
    VabsdiffRegRegAddressImmReg,
    VabsdiffRegRegConstReg,
    VabsdiffRegRegImmReg,
    VabsdiffRegRegRegAddressImm,
    VabsdiffRegRegRegConst,
    VabsdiffRegRegRegImm,
    VabsdiffRegRegRegReg,
    Vabsdiff2RegRegImmReg,
    Vabsdiff2RegRegRegReg,
    Vabsdiff4RegPredRegAddressImmReg,
    Vabsdiff4RegPredRegConstReg,
    Vabsdiff4RegPredRegImmReg,
    Vabsdiff4RegPredRegRegAddressImm,
    Vabsdiff4RegPredRegRegConst,
    Vabsdiff4RegPredRegRegImm,
    Vabsdiff4RegPredRegRegReg,
    Vabsdiff4RegRegAddressImmReg,
    Vabsdiff4RegRegConstReg,
    Vabsdiff4RegRegImmReg,
    Vabsdiff4RegRegRegAddressImm,
    Vabsdiff4RegRegRegConst,
    Vabsdiff4RegRegRegImm,
    Vabsdiff4RegRegRegReg,
    VaddRegRegReg,
    VaddRegRegRegReg,
    Vadd2RegRegReg,
    Vadd2RegRegRegReg,
    Vadd4RegRegReg,
    Vadd4RegRegRegReg,
    VhmnmxRegRegAddressImmRegPred,
    VhmnmxRegRegConstRegPred,
    VhmnmxRegRegImmImmRegPred,
    VhmnmxRegRegRegRegPred,
    ViaddRegRegAddressImm,
    ViaddRegRegConst,
    ViaddRegRegImm,
    ViaddRegRegReg,
    ViaddmnmxRegRegAddressImmRegPred,
    ViaddmnmxRegRegConstRegPred,
    ViaddmnmxRegRegImmRegPred,
    ViaddmnmxRegRegRegAddressImmPred,
    ViaddmnmxRegRegRegConstPred,
    ViaddmnmxRegRegRegImmPred,
    ViaddmnmxRegRegRegRegPred,
    VildRegAddress,
    VimnmxRegPredPredRegAddressImmPred,
    VimnmxRegPredPredRegConstPred,
    VimnmxRegPredPredRegImmPred,
    VimnmxRegPredPredRegRegPred,
    VimnmxRegRegAddressImmPred,
    VimnmxRegRegConstPred,
    VimnmxRegRegImmPred,
    VimnmxRegRegRegPred,
    Vimnmx3RegRegAddressImmRegPred,
    Vimnmx3RegRegConstRegPred,
    Vimnmx3RegRegImmRegPred,
    Vimnmx3RegRegRegRegPred,
    VmadRegRegImmReg,
    VmadRegRegReg,
    VmadRegRegRegReg,
    VmnmxRegRegImmReg,
    VmnmxRegRegRegReg,
    Vmnmx2RegRegImmReg,
    Vmnmx2RegRegRegReg,
    Vmnmx4RegRegImmReg,
    Vmnmx4RegRegRegReg,
    VoteImm,
    VotePredPred,
    VoteRegPredPred,
    VoteuRegPredPred,
    VoteVtgImm,
    VoteVtgPredImm,
    VoteVtgPredPredImmPred,
    Vsel2RegRegImmPred,
    Vsel2RegRegRegPred,
    Vsel4RegRegImmPred,
    Vsel4RegRegRegPred,
    VsetRegRegImmReg,
    VsetRegRegRegReg,
    Vset2RegRegImmReg,
    Vset2RegRegRegReg,
    Vset4RegRegImmReg,
    Vset4RegRegRegReg,
    VsetpPredPredRegImmPred,
    VsetpPredPredRegRegPred,
    VsetpPredRegImm,
    VsetpPredRegReg,
    VshlRegRegImmReg,
    VshlRegRegRegReg,
    VshrRegRegImmReg,
    VshrRegRegRegReg,
    WarpgroupNoOperands,
    WarpgroupImm,
    WarpgroupsetReg,
    WarpsyncNoOperands,
    WarpsyncAddressImm,
    WarpsyncConst,
    WarpsyncImm,
    WarpsyncPred,
    WarpsyncPredAddressImm,
    WarpsyncPredConst,
    WarpsyncPredImm,
    WarpsyncPredReg,
    WarpsyncPredRegImm,
    WarpsyncReg,
    WarpsyncRegImm,
    XmadRegRegConstReg,
    XmadRegRegImmReg,
    XmadRegRegRegConst,
    XmadRegRegRegReg,
    YieldNoOperands,
    YieldPred,
    Hir0x002NoOperands,
    Hir0x003NoOperands,
    Hir0x003Pred,
    Hir0x004NoOperands,
    Hir0x005PredRegRegImm,
    Hir0x005RegRegImm,
    Hir0x006Imm,
    Hir0x007NoOperands,
    Hir0x008ImmRegReg,
    Hir0x020NoOperands,
    Hir0x020Imm,
    Hir0x120AddressImm,
    Hir0x120Const,
    Hir0x120Imm,
    Hir0x120PredAddressImm,
    Hir0x120PredConst,
    Hir0x120PredImm,
    Hir0x120PredReg,
    Hir0x120Reg,
    Hir0x130AddressAddressAddressAddressAddressAddressPred,
    Hir0x150RegRegRegRegPredRegRegReg,
    Hir0x150RegRegRegRegRegRegReg,
    Hir0x160Reg,
    Hir0x170Reg,
    Hir0x1a0RegRegRegRegImmImm,
    Hir0x1b0RegRegRegReg,
    Hir0x1e0NoOperands,
    Hir0x1f0ImmReg,
    Hir0x1f2Reg,
    Hir0x1f3Reg
  )
}
