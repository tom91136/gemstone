// Generated SASS instruction definitions and checked operand constructors. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.sass
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import Instruction.*
@javax.annotation.processing.Generated(Array("sbt codegen"))
object InstructionDefinitions5 {
  object IaddintPipeRegPredRegImm extends Definition(
        "IaddintPipeRegPredRegImm",
        "IADDint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate)
        ),
        Vec("iadd_64_imm__RsIR_RIR", "iadd_imm__RsIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.IaddintPipeRegPredRegImm(dst, pu, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object IaddintPipeRegPredRegImmPred extends Definition(
        "IaddintPipeRegPredRegImmPred",
        "IADDint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("iadd_64_x_imm__RIR_RIR", "iadd_x_imm__RIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.IaddintPipeRegPredRegImmPred(dst, pu, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object IaddintPipeRegPredRegImmReg extends Definition(
        "IaddintPipeRegPredRegImmReg",
        "IADDint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("iadd_xor_imm__RsIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.IaddintPipeRegPredRegImmReg(dst, pu, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object IaddintPipeRegPredRegImmRegPred extends Definition(
        "IaddintPipeRegPredRegImmRegPred",
        "IADDint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("iadd_x_xor_imm__RIR_RIR")
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
            ) => Some(Instruction.IaddintPipeRegPredRegImmRegPred(dst, pu, srcA, sb, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object IaddintPipeRegPredRegReg extends Definition(
        "IaddintPipeRegPredRegReg",
        "IADDint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register)
        ),
        Vec("iadd_64_noimm__RRR_RRR", "iadd_64_noimm__RUR_RUR", "iadd_noimm__RRR_RRR", "iadd_noimm__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.IaddintPipeRegPredRegReg(dst, pu, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object IaddintPipeRegPredRegRegPred extends Definition(
        "IaddintPipeRegPredRegRegPred",
        "IADDint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
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
            ) => Some(Instruction.IaddintPipeRegPredRegRegPred(dst, pu, srcA, srcB, pp, modifiers, guard))
        case _ => None
      }
  }
  object IaddintPipeRegPredRegRegReg extends Definition(
        "IaddintPipeRegPredRegRegReg",
        "IADDint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("iadd_xor_noimm__RRR_RRR", "iadd_xor_noimm__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.IaddintPipeRegPredRegRegReg(dst, pu, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object IaddintPipeRegPredRegRegRegPred extends Definition(
        "IaddintPipeRegPredRegRegRegPred",
        "IADDint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
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
            ) => Some(Instruction.IaddintPipeRegPredRegRegRegPred(dst, pu, srcA, srcB, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object IaddintPipeRegRegAddressImm extends Definition(
        "IaddintPipeRegRegAddressImm",
        "IADDint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("iadd_noimm__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.IaddintPipeRegRegAddressImm(dst, srcA, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object IaddintPipeRegRegAddressImmPred extends Definition(
        "IaddintPipeRegRegAddressImmPred",
        "IADDint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("iadd_x_noimm__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.IaddintPipeRegRegAddressImmPred(dst, srcA, srcB, sb_offset, pp, modifiers, guard))
        case _ => None
      }
  }
  object IaddintPipeRegRegConst extends Definition(
        "IaddintPipeRegRegConst",
        "IADDint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank)
        ),
        Vec("iadd_noimm__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb)
            ) => Some(Instruction.IaddintPipeRegRegConst(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object IaddintPipeRegRegConstPred extends Definition(
        "IaddintPipeRegRegConstPred",
        "IADDint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("iadd_x_noimm__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.IaddintPipeRegRegConstPred(dst, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object IaddintPipeRegRegImm extends Definition(
        "IaddintPipeRegRegImm",
        "IADDint_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("iadd_64_imm__RsIR_RIR", "iadd_imm__RsIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.IaddintPipeRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object IaddintPipeRegRegImmPred extends Definition(
        "IaddintPipeRegRegImmPred",
        "IADDint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("iadd_64_x_imm__RIR_RIR", "iadd_x_imm__RIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.IaddintPipeRegRegImmPred(dst, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object IaddintPipeRegRegImmReg extends Definition(
        "IaddintPipeRegRegImmReg",
        "IADDint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("iadd_xor_imm__RsIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.IaddintPipeRegRegImmReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object IaddintPipeRegRegImmRegPred extends Definition(
        "IaddintPipeRegRegImmRegPred",
        "IADDint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("iadd_x_xor_imm__RIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.IaddintPipeRegRegImmRegPred(dst, srcA, sb, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object IaddintPipeRegRegReg extends Definition(
        "IaddintPipeRegRegReg",
        "IADDint_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("iadd_64_noimm__RRR_RRR", "iadd_64_noimm__RUR_RUR", "iadd_noimm__RRR_RRR", "iadd_noimm__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.IaddintPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object IaddintPipeRegRegRegPred extends Definition(
        "IaddintPipeRegRegRegPred",
        "IADDint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("iadd_64_x_noimm__RRR_RRR", "iadd_64_x_noimm__RUR_RUR", "iadd_x_noimm__RRR_RRR", "iadd_x_noimm__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.IaddintPipeRegRegRegPred(dst, srcA, srcB, pp, modifiers, guard))
        case _ => None
      }
  }
  object IaddintPipeRegRegRegReg extends Definition(
        "IaddintPipeRegRegRegReg",
        "IADDint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("iadd_xor_noimm__RRR_RRR", "iadd_xor_noimm__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.IaddintPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object IaddintPipeRegRegRegRegPred extends Definition(
        "IaddintPipeRegRegRegRegPred",
        "IADDint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
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
            ) => Some(Instruction.IaddintPipeRegRegRegRegPred(dst, srcA, srcB, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object IaddxluPipeRegRegConst extends Definition(
        "IaddxluPipeRegRegConst",
        "IADDxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const IADD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.IaddxluPipeRegRegConst(dst, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object IaddxluPipeRegRegImm extends Definition(
        "IaddxluPipeRegRegImm",
        "IADDxlu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("Imm IADD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.IaddxluPipeRegRegImm(dst, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object IaddxluPipeRegRegReg extends Definition(
        "IaddxluPipeRegRegReg",
        "IADDxlu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("IADD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.IaddxluPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object IcmpRegRegConstReg extends Definition(
        "IcmpRegRegConstReg",
        "ICMP",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
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
  object IcmpRegRegImmReg extends Definition(
        "IcmpRegRegImmReg",
        "ICMP",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm ICMP", "Imm ICMP U", "Imm_ICMP", "Imm_ICMP_U")
      ) {
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
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
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
  object IcmpRegRegRegReg extends Definition(
        "IcmpRegRegRegReg",
        "ICMP",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("ICMP", "ICMP U", "ICMP_U")
      ) {
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
  object IcmpfauPipeRegRegConstReg extends Definition(
        "IcmpfauPipeRegRegConstReg",
        "ICMPfau_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Const ICMP", "Const ICMP U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.IcmpfauPipeRegRegConstReg(dst, srcA, srcConst, srcC, modifiers, guard))
        case _ => None
      }
  }
  object IcmpfauPipeRegRegImmReg extends Definition(
        "IcmpfauPipeRegRegImmReg",
        "ICMPfau_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm ICMP", "Imm ICMP U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.IcmpfauPipeRegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object IcmpfauPipeRegRegRegConst extends Definition(
        "IcmpfauPipeRegRegRegConst",
        "ICMPfau_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const1 ICMP", "Const1 ICMP U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.IcmpfauPipeRegRegRegConst(dst, srcA, srcB, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object IcmpfauPipeRegRegRegReg extends Definition(
        "IcmpfauPipeRegRegRegReg",
        "ICMPfau_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("ICMP", "ICMP U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.IcmpfauPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object IcmpfxuPipeRegRegConstReg extends Definition(
        "IcmpfxuPipeRegRegConstReg",
        "ICMPfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Const_ICMP", "Const_ICMP_U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.IcmpfxuPipeRegRegConstReg(dst, srcA, srcConst, srcC, modifiers, guard))
        case _ => None
      }
  }
  object IcmpfxuPipeRegRegImmReg extends Definition(
        "IcmpfxuPipeRegRegImmReg",
        "ICMPfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm_ICMP", "Imm_ICMP_U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.IcmpfxuPipeRegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object IcmpfxuPipeRegRegRegConst extends Definition(
        "IcmpfxuPipeRegRegRegConst",
        "ICMPfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const1_ICMP", "Const1_ICMP_U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.IcmpfxuPipeRegRegRegConst(dst, srcA, srcB, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object IcmpfxuPipeRegRegRegReg extends Definition(
        "IcmpfxuPipeRegRegRegReg",
        "ICMPfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("ICMP", "ICMP_U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.IcmpfxuPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object IcmpxluPipeRegRegConstReg extends Definition(
        "IcmpxluPipeRegRegConstReg",
        "ICMPxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Const ICMP", "Const ICMP U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.IcmpxluPipeRegRegConstReg(dst, srcA, srcConst, srcC, modifiers, guard))
        case _ => None
      }
  }
  object IcmpxluPipeRegRegImmReg extends Definition(
        "IcmpxluPipeRegRegImmReg",
        "ICMPxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm ICMP", "Imm ICMP U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.IcmpxluPipeRegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object IcmpxluPipeRegRegRegConst extends Definition(
        "IcmpxluPipeRegRegRegConst",
        "ICMPxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const1 ICMP", "Const1 ICMP U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.IcmpxluPipeRegRegRegConst(dst, srcA, srcB, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object IcmpxluPipeRegRegRegReg extends Definition(
        "IcmpxluPipeRegRegRegReg",
        "ICMPxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("ICMP", "ICMP U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.IcmpxluPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object IdeNoOperands extends Definition("IdeNoOperands", "IDE", Vec(), Vec("IDE", "IDE_DI", "IDE_EN", "ide_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.IdeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object IdeImm extends Definition(
        "IdeImm",
        "IDE",
        Vec(Slot("sb", OperandKind.Immediate)),
        Vec("IDE", "IDE_DI", "IDE_EN", "ide_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb)) => Some(Instruction.IdeImm(sb, modifiers, guard))
        case _ => None
      }
  }
  object IdebruPipeNoOperands extends Definition("IdebruPipeNoOperands", "IDEbru_pipe", Vec(), Vec("IDE")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.IdebruPipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object IdebruPipeImm
      extends Definition("IdebruPipeImm", "IDEbru_pipe", Vec(Slot("uImm", OperandKind.Immediate)), Vec("IDE")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(uImm)) => Some(Instruction.IdebruPipeImm(uImm, modifiers, guard))
        case _ => None
      }
  }
  object IdefxuPipeNoOperands
      extends Definition("IdefxuPipeNoOperands", "IDEfxu_pipe", Vec(), Vec("IDE_DI", "IDE_EN")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.IdefxuPipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object IdefxuPipeImm extends Definition(
        "IdefxuPipeImm",
        "IDEfxu_pipe",
        Vec(Slot("uImm", OperandKind.Immediate)),
        Vec("IDE_DI", "IDE_EN")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(uImm)) => Some(Instruction.IdefxuPipeImm(uImm, modifiers, guard))
        case _ => None
      }
  }
  object IdeintPipeNoOperands extends Definition("IdeintPipeNoOperands", "IDEint_pipe", Vec(), Vec("ide_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.IdeintPipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object IdeintPipeImm
      extends Definition("IdeintPipeImm", "IDEint_pipe", Vec(Slot("sb", OperandKind.Immediate)), Vec("ide_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb)) => Some(Instruction.IdeintPipeImm(sb, modifiers, guard))
        case _ => None
      }
  }
  object IdpRegRegAddressImmReg extends Definition(
        "IdpRegRegAddressImmReg",
        "IDP",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("idp_2a__CXb", "idp_4a__CXb")
      ) {
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
  object IdpRegRegConstReg extends Definition(
        "IdpRegRegConstReg",
        "IDP",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("IDP_4A_c", "IDP_c", "idp_2a__C", "idp_4a__C")
      ) {
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
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
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
  object Idp4aRegRegAddressImmReg extends Definition(
        "Idp4aRegRegAddressImmReg",
        "IDP4A",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("idp4a__CXb")
      ) {
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
  object Idp4aRegRegConstReg extends Definition(
        "Idp4aRegRegConstReg",
        "IDP4A",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("IDP4A_c", "idp4a__C")
      ) {
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
  object Idp4aRegRegRegReg extends Definition(
        "Idp4aRegRegRegReg",
        "IDP4A",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("IDP4A", "idp4a__R", "idp4a__URb")
      ) {
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
  object Idp4afmaiPipeRegRegConstReg extends Definition(
        "Idp4afmaiPipeRegRegConstReg",
        "IDP4Afmai_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("IDP4A_c")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Idp4afmaiPipeRegRegConstReg(dst, srcA, srcConst, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Idp4afmaiPipeRegRegRegReg extends Definition(
        "Idp4afmaiPipeRegRegRegReg",
        "IDP4Afmai_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("IDP4A")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Idp4afmaiPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Idp4afmalighterPipeRegRegAddressImmReg extends Definition(
        "Idp4afmalighterPipeRegRegAddressImmReg",
        "IDP4Afmalighter_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("idp4a__CXb")
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
          Some(Instruction.Idp4afmalighterPipeRegRegAddressImmReg(dst, srcA, srcB, sb_offset, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Idp4afmalighterPipeRegRegConstReg extends Definition(
        "Idp4afmalighterPipeRegRegConstReg",
        "IDP4Afmalighter_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("idp4a__C")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Idp4afmalighterPipeRegRegConstReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Idp4afmalighterPipeRegRegRegReg extends Definition(
        "Idp4afmalighterPipeRegRegRegReg",
        "IDP4Afmalighter_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("idp4a__R", "idp4a__URb")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Idp4afmalighterPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Idp4afxuPipeRegRegConstReg extends Definition(
        "Idp4afxuPipeRegRegConstReg",
        "IDP4Afxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("IDP4A_c")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Idp4afxuPipeRegRegConstReg(dst, srcA, srcConst, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Idp4afxuPipeRegRegRegReg extends Definition(
        "Idp4afxuPipeRegRegRegReg",
        "IDP4Afxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("IDP4A")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Idp4afxuPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Idp4aintPipeRegRegAddressImmReg extends Definition(
        "Idp4aintPipeRegRegAddressImmReg",
        "IDP4Aint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("idp4a__CXb")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Idp4aintPipeRegRegAddressImmReg(dst, srcA, srcB, sb_offset, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Idp4aintPipeRegRegConstReg extends Definition(
        "Idp4aintPipeRegRegConstReg",
        "IDP4Aint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("idp4a__C")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Idp4aintPipeRegRegConstReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Idp4aintPipeRegRegRegReg extends Definition(
        "Idp4aintPipeRegRegRegReg",
        "IDP4Aint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("idp4a__R", "idp4a__URb")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Idp4aintPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object IdpfmaiPipeRegRegConstReg extends Definition(
        "IdpfmaiPipeRegRegConstReg",
        "IDPfmai_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("IDP_4A_c", "IDP_c")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.IdpfmaiPipeRegRegConstReg(dst, srcA, srcConst, srcC, modifiers, guard))
        case _ => None
      }
  }
  object IdpfmaiPipeRegRegRegReg extends Definition(
        "IdpfmaiPipeRegRegRegReg",
        "IDPfmai_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("IDP_2A", "IDP_4A")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.IdpfmaiPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object IdpfmalighterPipeRegRegAddressImmReg extends Definition(
        "IdpfmalighterPipeRegRegAddressImmReg",
        "IDPfmalighter_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("idp_2a__CXb", "idp_4a__CXb")
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
          Some(Instruction.IdpfmalighterPipeRegRegAddressImmReg(dst, srcA, srcB, sb_offset, srcC, modifiers, guard))
        case _ => None
      }
  }
  object IdpfmalighterPipeRegRegConstReg extends Definition(
        "IdpfmalighterPipeRegRegConstReg",
        "IDPfmalighter_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("idp_2a__C", "idp_4a__C")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.IdpfmalighterPipeRegRegConstReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object IdpfmalighterPipeRegRegRegReg extends Definition(
        "IdpfmalighterPipeRegRegRegReg",
        "IDPfmalighter_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("idp_2a__R", "idp_2a__URb", "idp_4a__R", "idp_4a__URb")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.IdpfmalighterPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object IdpfxuPipeRegRegConstReg extends Definition(
        "IdpfxuPipeRegRegConstReg",
        "IDPfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("IDP_4A_c", "IDP_c")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.IdpfxuPipeRegRegConstReg(dst, srcA, srcConst, srcC, modifiers, guard))
        case _ => None
      }
  }
  object IdpfxuPipeRegRegRegReg extends Definition(
        "IdpfxuPipeRegRegRegReg",
        "IDPfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("IDP_2A", "IDP_4A")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.IdpfxuPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object IdpintPipeRegRegAddressImmReg extends Definition(
        "IdpintPipeRegRegAddressImmReg",
        "IDPint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("idp_2a__CXb", "idp_4a__CXb")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.IdpintPipeRegRegAddressImmReg(dst, srcA, srcB, sb_offset, srcC, modifiers, guard))
        case _ => None
      }
  }
  object IdpintPipeRegRegConstReg extends Definition(
        "IdpintPipeRegRegConstReg",
        "IDPint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("idp_2a__C", "idp_4a__C")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.IdpintPipeRegRegConstReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object IdpintPipeRegRegRegReg extends Definition(
        "IdpintPipeRegRegRegReg",
        "IDPint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("idp_2a__R", "idp_2a__URb", "idp_4a__R", "idp_4a__URb")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.IdpintPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object IgmmaRegAddressReg extends Definition(
        "IgmmaRegAddressReg",
        "IGMMA",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address), Slot("srcC", OperandKind.Register)),
        Vec("igmma_URa_Rc_")
      ) {
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
  object IgmmaRegAddressRegPred extends Definition(
        "IgmmaRegAddressRegPred",
        "IGMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("igmma_URa_Rc_")
      ) {
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
  object IgmmaRegAddressRegPredReg extends Definition(
        "IgmmaRegAddressRegPredReg",
        "IGMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("re", OperandKind.Register)
        ),
        Vec("igmma_sparse_URa_Rc_")
      ) {
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
  object IgmmaRegAddressRegPredRegImm extends Definition(
        "IgmmaRegAddressRegPredRegImm",
        "IGMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("re", OperandKind.Register),
          Slot("id", OperandKind.Immediate)
        ),
        Vec("igmma_sparse_URa_Rc_")
      ) {
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
  object IgmmaRegAddressRegReg extends Definition(
        "IgmmaRegAddressRegReg",
        "IGMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("igmma_URa_Rb_Rc_", "igmma_sparse_URa_Rc_")
      ) {
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
  object IgmmaRegAddressRegRegImm extends Definition(
        "IgmmaRegAddressRegRegImm",
        "IGMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("re", OperandKind.Register),
          Slot("id", OperandKind.Immediate)
        ),
        Vec("igmma_sparse_URa_Rc_")
      ) {
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
  object IgmmaRegAddressRegRegPred extends Definition(
        "IgmmaRegAddressRegRegPred",
        "IGMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("igmma_URa_Rb_Rc_")
      ) {
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
  object IgmmaRegAddressRegRegPredReg extends Definition(
        "IgmmaRegAddressRegRegPredReg",
        "IGMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("re", OperandKind.Register)
        ),
        Vec("igmma_sparse_URa_Rb_Rc_")
      ) {
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
  object IgmmaRegAddressRegRegPredRegImm extends Definition(
        "IgmmaRegAddressRegRegPredRegImm",
        "IGMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("re", OperandKind.Register),
          Slot("id", OperandKind.Immediate)
        ),
        Vec("igmma_sparse_URa_Rb_Rc_")
      ) {
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
  object IgmmaRegAddressRegRegReg extends Definition(
        "IgmmaRegAddressRegRegReg",
        "IGMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("re", OperandKind.Register)
        ),
        Vec("igmma_sparse_URa_Rb_Rc_")
      ) {
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
  object IgmmaRegAddressRegRegRegImm extends Definition(
        "IgmmaRegAddressRegRegRegImm",
        "IGMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("re", OperandKind.Register),
          Slot("id", OperandKind.Immediate)
        ),
        Vec("igmma_sparse_URa_Rb_Rc_")
      ) {
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
  object IgmmaRegRegAddressReg extends Definition(
        "IgmmaRegRegAddressReg",
        "IGMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("igmma_Ra_URb_Rc_")
      ) {
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
  object IgmmaRegRegAddressRegPred extends Definition(
        "IgmmaRegRegAddressRegPred",
        "IGMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("igmma_Ra_URb_Rc_")
      ) {
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
  object IgmmaRegRegAddressRegPredReg extends Definition(
        "IgmmaRegRegAddressRegPredReg",
        "IGMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("re", OperandKind.Register)
        ),
        Vec("igmma_sparse_Ra_URb_Rc_")
      ) {
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
  object IgmmaRegRegAddressRegPredRegImm extends Definition(
        "IgmmaRegRegAddressRegPredRegImm",
        "IGMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("re", OperandKind.Register),
          Slot("id", OperandKind.Immediate)
        ),
        Vec("igmma_sparse_Ra_URb_Rc_")
      ) {
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
  object IgmmaRegRegAddressRegReg extends Definition(
        "IgmmaRegRegAddressRegReg",
        "IGMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("re", OperandKind.Register)
        ),
        Vec("igmma_sparse_Ra_URb_Rc_")
      ) {
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
  object IgmmaRegRegAddressRegRegImm extends Definition(
        "IgmmaRegRegAddressRegRegImm",
        "IGMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("re", OperandKind.Register),
          Slot("id", OperandKind.Immediate)
        ),
        Vec("igmma_sparse_Ra_URb_Rc_")
      ) {
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
  object IgmmamioPipeRegAddressReg extends Definition(
        "IgmmamioPipeRegAddressReg",
        "IGMMAmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address), Slot("srcC", OperandKind.Register)),
        Vec("igmma_URa_Rc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.IgmmamioPipeRegAddressReg(dst, srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object IgmmamioPipeRegAddressRegPred extends Definition(
        "IgmmamioPipeRegAddressRegPred",
        "IGMMAmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("igmma_URa_Rc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.IgmmamioPipeRegAddressRegPred(dst, srcA, srcC, uPp, modifiers, guard))
        case _ => None
      }
  }
  object IgmmamioPipeRegAddressRegPredReg extends Definition(
        "IgmmamioPipeRegAddressRegPredReg",
        "IGMMAmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("re", OperandKind.Register)
        ),
        Vec("igmma_sparse_URa_Rc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Register(re)
            ) => Some(Instruction.IgmmamioPipeRegAddressRegPredReg(dst, srcA, srcC, uPp, re, modifiers, guard))
        case _ => None
      }
  }
  object IgmmamioPipeRegAddressRegPredRegImm extends Definition(
        "IgmmamioPipeRegAddressRegPredRegImm",
        "IGMMAmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("re", OperandKind.Register),
          Slot("id", OperandKind.Immediate)
        ),
        Vec("igmma_sparse_URa_Rc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Register(re),
              InstructionOperand.Immediate(id)
            ) => Some(Instruction.IgmmamioPipeRegAddressRegPredRegImm(dst, srcA, srcC, uPp, re, id, modifiers, guard))
        case _ => None
      }
  }
  object IgmmamioPipeRegAddressRegReg extends Definition(
        "IgmmamioPipeRegAddressRegReg",
        "IGMMAmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("igmma_URa_Rb_Rc_", "igmma_sparse_URa_Rc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.IgmmamioPipeRegAddressRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object IgmmamioPipeRegAddressRegRegImm extends Definition(
        "IgmmamioPipeRegAddressRegRegImm",
        "IGMMAmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("re", OperandKind.Register),
          Slot("id", OperandKind.Immediate)
        ),
        Vec("igmma_sparse_URa_Rc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(re),
              InstructionOperand.Immediate(id)
            ) => Some(Instruction.IgmmamioPipeRegAddressRegRegImm(dst, srcA, srcC, re, id, modifiers, guard))
        case _ => None
      }
  }
  object IgmmamioPipeRegAddressRegRegPred extends Definition(
        "IgmmamioPipeRegAddressRegRegPred",
        "IGMMAmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("igmma_URa_Rb_Rc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.IgmmamioPipeRegAddressRegRegPred(dst, srcA, srcB, srcC, uPp, modifiers, guard))
        case _ => None
      }
  }
  object IgmmamioPipeRegAddressRegRegPredReg extends Definition(
        "IgmmamioPipeRegAddressRegRegPredReg",
        "IGMMAmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("re", OperandKind.Register)
        ),
        Vec("igmma_sparse_URa_Rb_Rc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Register(re)
            ) => Some(Instruction.IgmmamioPipeRegAddressRegRegPredReg(dst, srcA, srcB, srcC, uPp, re, modifiers, guard))
        case _ => None
      }
  }
  object IgmmamioPipeRegAddressRegRegPredRegImm extends Definition(
        "IgmmamioPipeRegAddressRegRegPredRegImm",
        "IGMMAmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("re", OperandKind.Register),
          Slot("id", OperandKind.Immediate)
        ),
        Vec("igmma_sparse_URa_Rb_Rc_")
      ) {
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
            ) =>
          Some(Instruction.IgmmamioPipeRegAddressRegRegPredRegImm(dst, srcA, srcB, srcC, uPp, re, id, modifiers, guard))
        case _ => None
      }
  }
  object IgmmamioPipeRegAddressRegRegReg extends Definition(
        "IgmmamioPipeRegAddressRegRegReg",
        "IGMMAmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("re", OperandKind.Register)
        ),
        Vec("igmma_sparse_URa_Rb_Rc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(re)
            ) => Some(Instruction.IgmmamioPipeRegAddressRegRegReg(dst, srcA, srcB, srcC, re, modifiers, guard))
        case _ => None
      }
  }
  object IgmmamioPipeRegAddressRegRegRegImm extends Definition(
        "IgmmamioPipeRegAddressRegRegRegImm",
        "IGMMAmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("re", OperandKind.Register),
          Slot("id", OperandKind.Immediate)
        ),
        Vec("igmma_sparse_URa_Rb_Rc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(re),
              InstructionOperand.Immediate(id)
            ) => Some(Instruction.IgmmamioPipeRegAddressRegRegRegImm(dst, srcA, srcB, srcC, re, id, modifiers, guard))
        case _ => None
      }
  }
  object IgmmamioPipeRegRegAddressReg extends Definition(
        "IgmmamioPipeRegRegAddressReg",
        "IGMMAmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("igmma_Ra_URb_Rc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.IgmmamioPipeRegRegAddressReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object IgmmamioPipeRegRegAddressRegPred extends Definition(
        "IgmmamioPipeRegRegAddressRegPred",
        "IGMMAmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("igmma_Ra_URb_Rc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.IgmmamioPipeRegRegAddressRegPred(dst, srcA, srcB, srcC, uPp, modifiers, guard))
        case _ => None
      }
  }
  object IgmmamioPipeRegRegAddressRegPredReg extends Definition(
        "IgmmamioPipeRegRegAddressRegPredReg",
        "IGMMAmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("re", OperandKind.Register)
        ),
        Vec("igmma_sparse_Ra_URb_Rc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Register(re)
            ) => Some(Instruction.IgmmamioPipeRegRegAddressRegPredReg(dst, srcA, srcB, srcC, uPp, re, modifiers, guard))
        case _ => None
      }
  }
  object IgmmamioPipeRegRegAddressRegPredRegImm extends Definition(
        "IgmmamioPipeRegRegAddressRegPredRegImm",
        "IGMMAmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("re", OperandKind.Register),
          Slot("id", OperandKind.Immediate)
        ),
        Vec("igmma_sparse_Ra_URb_Rc_")
      ) {
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
            ) =>
          Some(Instruction.IgmmamioPipeRegRegAddressRegPredRegImm(dst, srcA, srcB, srcC, uPp, re, id, modifiers, guard))
        case _ => None
      }
  }
  object IgmmamioPipeRegRegAddressRegReg extends Definition(
        "IgmmamioPipeRegRegAddressRegReg",
        "IGMMAmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("re", OperandKind.Register)
        ),
        Vec("igmma_sparse_Ra_URb_Rc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(re)
            ) => Some(Instruction.IgmmamioPipeRegRegAddressRegReg(dst, srcA, srcB, srcC, re, modifiers, guard))
        case _ => None
      }
  }
  object IgmmamioPipeRegRegAddressRegRegImm extends Definition(
        "IgmmamioPipeRegRegAddressRegRegImm",
        "IGMMAmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("re", OperandKind.Register),
          Slot("id", OperandKind.Immediate)
        ),
        Vec("igmma_sparse_Ra_URb_Rc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(re),
              InstructionOperand.Immediate(id)
            ) => Some(Instruction.IgmmamioPipeRegRegAddressRegRegImm(dst, srcA, srcB, srcC, re, id, modifiers, guard))
        case _ => None
      }
  }
  object ImadRegPredRegAddressImmReg extends Definition(
        "ImadRegPredRegAddressImmReg",
        "IMAD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
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
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
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
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
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
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
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
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
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
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
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
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Address),
          Slot("sc_offset", OperandKind.Immediate)
        ),
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
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Address),
          Slot("sc_offset", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
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
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.ConstantBank)
        ),
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
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.ConstantBank),
          Slot("pp", OperandKind.Predicate)
        ),
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
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
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
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
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
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
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
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
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
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
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
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
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
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
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
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
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
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Address),
          Slot("sc_offset", OperandKind.Immediate)
        ),
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
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Address),
          Slot("sc_offset", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
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
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.ConstantBank)
        ),
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
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.ConstantBank),
          Slot("pp", OperandKind.Predicate)
        ),
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
  object ImadRegRegRegImm extends Definition(
        "ImadRegRegRegImm",
        "IMAD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate)
        ),
        Vec("imad__RRsI_RRI", "imad_pseudo__RRsI_RRI")
      ) {
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
  object ImadRegRegRegImmPred extends Definition(
        "ImadRegRegRegImmPred",
        "IMAD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("imad_x__RRsI_RRI", "imad_x_pseudo__RRsI_RRI")
      ) {
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
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
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
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
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
  object Imad32iRegRegImmReg extends Definition(
        "Imad32iRegRegImmReg",
        "IMAD32I",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("IMAD32I")
      ) {
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
  object Imad32ifmaxPipeRegRegImmReg extends Definition(
        "Imad32ifmaxPipeRegRegImmReg",
        "IMAD32IfmaX_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("IMAD32I")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Imad32ifmaxPipeRegRegImmReg(dst, srcA, sImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Imad32imioPipeRegRegImmReg extends Definition(
        "Imad32imioPipeRegRegImmReg",
        "IMAD32Imio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("IMAD32I")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Imad32imioPipeRegRegImmReg(dst, srcA, sImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object ImadspRegRegConstReg extends Definition(
        "ImadspRegRegConstReg",
        "IMADSP",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Const IMADSP", "Const IMADSP SD", "Const_IMADSP")
      ) {
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
  object ImadspRegRegImmReg extends Definition(
        "ImadspRegRegImmReg",
        "IMADSP",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm IMADSP", "Imm IMADSP SD", "Imm_IMADSP")
      ) {
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
  object ImadspRegRegRegConst extends Definition(
        "ImadspRegRegRegConst",
        "IMADSP",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const1 IMADSP", "Const1 IMADSP SD", "Const1_IMADSP")
      ) {
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
  object ImadspRegRegRegReg extends Definition(
        "ImadspRegRegRegReg",
        "IMADSP",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("IMADSP", "IMADSP SD")
      ) {
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
  object ImadspfmaxPipeRegRegConstReg extends Definition(
        "ImadspfmaxPipeRegRegConstReg",
        "IMADSPfmaX_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Const IMADSP", "Const IMADSP SD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.ImadspfmaxPipeRegRegConstReg(dst, srcA, srcConst, srcC, modifiers, guard))
        case _ => None
      }
  }
  object ImadspfmaxPipeRegRegImmReg extends Definition(
        "ImadspfmaxPipeRegRegImmReg",
        "IMADSPfmaX_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm IMADSP", "Imm IMADSP SD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.ImadspfmaxPipeRegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object ImadspfmaxPipeRegRegRegConst extends Definition(
        "ImadspfmaxPipeRegRegRegConst",
        "IMADSPfmaX_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const1 IMADSP", "Const1 IMADSP SD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.ImadspfmaxPipeRegRegRegConst(dst, srcA, srcB, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object ImadspfmaxPipeRegRegRegReg extends Definition(
        "ImadspfmaxPipeRegRegRegReg",
        "IMADSPfmaX_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("IMADSP", "IMADSP SD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.ImadspfmaxPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object ImadspmioPipeRegRegConstReg extends Definition(
        "ImadspmioPipeRegRegConstReg",
        "IMADSPmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Const_IMADSP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.ImadspmioPipeRegRegConstReg(dst, srcA, srcConst, srcC, modifiers, guard))
        case _ => None
      }
  }
  object ImadspmioPipeRegRegImmReg extends Definition(
        "ImadspmioPipeRegRegImmReg",
        "IMADSPmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm_IMADSP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.ImadspmioPipeRegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object ImadspmioPipeRegRegRegConst extends Definition(
        "ImadspmioPipeRegRegRegConst",
        "IMADSPmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const1_IMADSP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.ImadspmioPipeRegRegRegConst(dst, srcA, srcB, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object ImadspmioPipeRegRegRegReg extends Definition(
        "ImadspmioPipeRegRegRegReg",
        "IMADSPmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("IMADSP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.ImadspmioPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object ImadfmaxPipeRegRegConstReg extends Definition(
        "ImadfmaxPipeRegRegConstReg",
        "IMADfmaX_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Const IMAD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.ImadfmaxPipeRegRegConstReg(dst, srcA, srcConst, srcC, modifiers, guard))
        case _ => None
      }
  }
  object ImadfmaxPipeRegRegImmReg extends Definition(
        "ImadfmaxPipeRegRegImmReg",
        "IMADfmaX_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm IMAD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.ImadfmaxPipeRegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object ImadfmaxPipeRegRegRegConst extends Definition(
        "ImadfmaxPipeRegRegRegConst",
        "IMADfmaX_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const1 IMAD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.ImadfmaxPipeRegRegRegConst(dst, srcA, srcB, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object ImadfmaxPipeRegRegRegReg extends Definition(
        "ImadfmaxPipeRegRegRegReg",
        "IMADfmaX_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("IMAD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.ImadfmaxPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object ImadfmalighterPipeRegPredRegAddressImmReg extends Definition(
        "ImadfmalighterPipeRegPredRegAddressImmReg",
        "IMADfmalighter_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
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
            ) => Some(Instruction.ImadfmalighterPipeRegPredRegAddressImmReg(
            dst,
            pu,
            srcA,
            srcB,
            sb_offset,
            srcC,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object ImadfmalighterPipeRegPredRegAddressImmRegPred extends Definition(
        "ImadfmalighterPipeRegPredRegAddressImmRegPred",
        "IMADfmalighter_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
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
            ) => Some(Instruction.ImadfmalighterPipeRegPredRegAddressImmRegPred(
            dst,
            pu,
            srcA,
            srcB,
            sb_offset,
            srcC,
            pp,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object ImadfmalighterPipeRegPredRegConstReg extends Definition(
        "ImadfmalighterPipeRegPredRegConstReg",
        "IMADfmalighter_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
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
            ) => Some(Instruction.ImadfmalighterPipeRegPredRegConstReg(dst, pu, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object ImadfmalighterPipeRegPredRegConstRegPred extends Definition(
        "ImadfmalighterPipeRegPredRegConstRegPred",
        "IMADfmalighter_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
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
            ) =>
          Some(Instruction.ImadfmalighterPipeRegPredRegConstRegPred(dst, pu, srcA, sb, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object ImadfmalighterPipeRegPredRegImmReg extends Definition(
        "ImadfmalighterPipeRegPredRegImmReg",
        "IMADfmalighter_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
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
            ) => Some(Instruction.ImadfmalighterPipeRegPredRegImmReg(dst, pu, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object ImadfmalighterPipeRegPredRegImmRegPred extends Definition(
        "ImadfmalighterPipeRegPredRegImmRegPred",
        "IMADfmalighter_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
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
            ) => Some(Instruction.ImadfmalighterPipeRegPredRegImmRegPred(dst, pu, srcA, sb, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object ImadfmalighterPipeRegPredRegRegAddressImm extends Definition(
        "ImadfmalighterPipeRegPredRegRegAddressImm",
        "IMADfmalighter_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Address),
          Slot("sc_offset", OperandKind.Immediate)
        ),
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
            ) => Some(Instruction.ImadfmalighterPipeRegPredRegRegAddressImm(
            dst,
            pu,
            srcA,
            srcB,
            srcC,
            sc_offset,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object ImadfmalighterPipeRegPredRegRegAddressImmPred extends Definition(
        "ImadfmalighterPipeRegPredRegRegAddressImmPred",
        "IMADfmalighter_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Address),
          Slot("sc_offset", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
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
            ) => Some(Instruction.ImadfmalighterPipeRegPredRegRegAddressImmPred(
            dst,
            pu,
            srcA,
            srcB,
            srcC,
            sc_offset,
            pp,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object ImadfmalighterPipeRegPredRegRegConst extends Definition(
        "ImadfmalighterPipeRegPredRegRegConst",
        "IMADfmalighter_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.ConstantBank)
        ),
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
            ) => Some(Instruction.ImadfmalighterPipeRegPredRegRegConst(dst, pu, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object ImadfmalighterPipeRegPredRegRegConstPred extends Definition(
        "ImadfmalighterPipeRegPredRegRegConstPred",
        "IMADfmalighter_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.ConstantBank),
          Slot("pp", OperandKind.Predicate)
        ),
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
            ) =>
          Some(Instruction.ImadfmalighterPipeRegPredRegRegConstPred(dst, pu, srcA, srcB, sc, pp, modifiers, guard))
        case _ => None
      }
  }
  object ImadfmalighterPipeRegPredRegRegReg extends Definition(
        "ImadfmalighterPipeRegPredRegRegReg",
        "IMADfmalighter_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
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
            ) => Some(Instruction.ImadfmalighterPipeRegPredRegRegReg(dst, pu, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object ImadfmalighterPipeRegPredRegRegRegPred extends Definition(
        "ImadfmalighterPipeRegPredRegRegRegPred",
        "IMADfmalighter_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
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
            ) =>
          Some(Instruction.ImadfmalighterPipeRegPredRegRegRegPred(dst, pu, srcA, srcB, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object ImadfmalighterPipeRegRegAddressImmReg extends Definition(
        "ImadfmalighterPipeRegRegAddressImmReg",
        "IMADfmalighter_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
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
            ) =>
          Some(Instruction.ImadfmalighterPipeRegRegAddressImmReg(dst, srcA, srcB, sb_offset, srcC, modifiers, guard))
        case _ => None
      }
  }
  object ImadfmalighterPipeRegRegAddressImmRegPred extends Definition(
        "ImadfmalighterPipeRegRegAddressImmRegPred",
        "IMADfmalighter_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
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
            ) => Some(Instruction.ImadfmalighterPipeRegRegAddressImmRegPred(
            dst,
            srcA,
            srcB,
            sb_offset,
            srcC,
            pp,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object ImadfmalighterPipeRegRegConstReg extends Definition(
        "ImadfmalighterPipeRegRegConstReg",
        "IMADfmalighter_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec(
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
            ) => Some(Instruction.ImadfmalighterPipeRegRegConstReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object ImadfmalighterPipeRegRegConstRegPred extends Definition(
        "ImadfmalighterPipeRegRegConstRegPred",
        "IMADfmalighter_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
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
            ) => Some(Instruction.ImadfmalighterPipeRegRegConstRegPred(dst, srcA, sb, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object ImadfmalighterPipeRegRegImmReg extends Definition(
        "ImadfmalighterPipeRegRegImmReg",
        "IMADfmalighter_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec(
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
            ) => Some(Instruction.ImadfmalighterPipeRegRegImmReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object ImadfmalighterPipeRegRegImmRegPred extends Definition(
        "ImadfmalighterPipeRegRegImmRegPred",
        "IMADfmalighter_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
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
            ) => Some(Instruction.ImadfmalighterPipeRegRegImmRegPred(dst, srcA, sb, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object ImadfmalighterPipeRegRegRegAddressImm extends Definition(
        "ImadfmalighterPipeRegRegRegAddressImm",
        "IMADfmalighter_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Address),
          Slot("sc_offset", OperandKind.Immediate)
        ),
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
            ) =>
          Some(Instruction.ImadfmalighterPipeRegRegRegAddressImm(dst, srcA, srcB, srcC, sc_offset, modifiers, guard))
        case _ => None
      }
  }
  object ImadfmalighterPipeRegRegRegAddressImmPred extends Definition(
        "ImadfmalighterPipeRegRegRegAddressImmPred",
        "IMADfmalighter_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Address),
          Slot("sc_offset", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
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
            ) => Some(Instruction.ImadfmalighterPipeRegRegRegAddressImmPred(
            dst,
            srcA,
            srcB,
            srcC,
            sc_offset,
            pp,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object ImadfmalighterPipeRegRegRegConst extends Definition(
        "ImadfmalighterPipeRegRegRegConst",
        "IMADfmalighter_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.ConstantBank)
        ),
        Vec(
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
            ) => Some(Instruction.ImadfmalighterPipeRegRegRegConst(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object ImadfmalighterPipeRegRegRegConstPred extends Definition(
        "ImadfmalighterPipeRegRegRegConstPred",
        "IMADfmalighter_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.ConstantBank),
          Slot("pp", OperandKind.Predicate)
        ),
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
            ) => Some(Instruction.ImadfmalighterPipeRegRegRegConstPred(dst, srcA, srcB, sc, pp, modifiers, guard))
        case _ => None
      }
  }
  object ImadfmalighterPipeRegRegRegImm extends Definition(
        "ImadfmalighterPipeRegRegRegImm",
        "IMADfmalighter_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate)
        ),
        Vec("imad__RRsI_RRI", "imad_pseudo__RRsI_RRI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.ImadfmalighterPipeRegRegRegImm(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object ImadfmalighterPipeRegRegRegImmPred extends Definition(
        "ImadfmalighterPipeRegRegRegImmPred",
        "IMADfmalighter_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("imad_x__RRsI_RRI", "imad_x_pseudo__RRsI_RRI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.ImadfmalighterPipeRegRegRegImmPred(dst, srcA, srcB, sc, pp, modifiers, guard))
        case _ => None
      }
  }
  object ImadfmalighterPipeRegRegRegReg extends Definition(
        "ImadfmalighterPipeRegRegRegReg",
        "IMADfmalighter_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec(
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
            ) => Some(Instruction.ImadfmalighterPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object ImadfmalighterPipeRegRegRegRegPred extends Definition(
        "ImadfmalighterPipeRegRegRegRegPred",
        "IMADfmalighter_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
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
            ) => Some(Instruction.ImadfmalighterPipeRegRegRegRegPred(dst, srcA, srcB, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object ImadmioPipeRegRegConstReg extends Definition(
        "ImadmioPipeRegRegConstReg",
        "IMADmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Const_IMAD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.ImadmioPipeRegRegConstReg(dst, srcA, srcConst, srcC, modifiers, guard))
        case _ => None
      }
  }
  object ImadmioPipeRegRegImmReg extends Definition(
        "ImadmioPipeRegRegImmReg",
        "IMADmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm_IMAD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.ImadmioPipeRegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object ImadmioPipeRegRegRegConst extends Definition(
        "ImadmioPipeRegRegRegConst",
        "IMADmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const1_IMAD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.ImadmioPipeRegRegRegConst(dst, srcA, srcB, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object ImadmioPipeRegRegRegReg extends Definition(
        "ImadmioPipeRegRegRegReg",
        "IMADmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("IMAD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.ImadmioPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object ImmaRegRegRegReg extends Definition(
        "ImmaRegRegRegReg",
        "IMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
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
  object ImmaRegRegRegRegPred extends Definition(
        "ImmaRegRegRegRegPred",
        "IMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("imma_")
      ) {
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
  object ImmaRegRegRegRegPredRegImm extends Definition(
        "ImmaRegRegRegRegPredRegImm",
        "IMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("re", OperandKind.Register),
          Slot("id", OperandKind.Immediate)
        ),
        Vec("imma_sp_")
      ) {
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
  object ImmaRegRegRegRegRegImm extends Definition(
        "ImmaRegRegRegRegRegImm",
        "IMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("re", OperandKind.Register),
          Slot("id", OperandKind.Immediate)
        ),
        Vec("imma_sp_")
      ) {
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
  object ImmaintPipeRegRegRegReg extends Definition(
        "ImmaintPipeRegRegRegReg",
        "IMMAint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("imma_", "imma_8816_16_8_", "imma_8816_8_8_", "imma_8832_4_4_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.ImmaintPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object ImmaintPipeRegRegRegRegPred extends Definition(
        "ImmaintPipeRegRegRegRegPred",
        "IMMAint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("imma_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.ImmaintPipeRegRegRegRegPred(dst, srcA, srcB, srcC, uPp, modifiers, guard))
        case _ => None
      }
  }
  object ImmaintPipeRegRegRegRegPredRegImm extends Definition(
        "ImmaintPipeRegRegRegRegPredRegImm",
        "IMMAint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("re", OperandKind.Register),
          Slot("id", OperandKind.Immediate)
        ),
        Vec("imma_sp_")
      ) {
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
            ) =>
          Some(Instruction.ImmaintPipeRegRegRegRegPredRegImm(dst, srcA, srcB, srcC, uPp, re, id, modifiers, guard))
        case _ => None
      }
  }
  object ImmaintPipeRegRegRegRegRegImm extends Definition(
        "ImmaintPipeRegRegRegRegRegImm",
        "IMMAint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("re", OperandKind.Register),
          Slot("id", OperandKind.Immediate)
        ),
        Vec("imma_sp_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(re),
              InstructionOperand.Immediate(id)
            ) => Some(Instruction.ImmaintPipeRegRegRegRegRegImm(dst, srcA, srcB, srcC, re, id, modifiers, guard))
        case _ => None
      }
  }
  object ImnmxPredPredRegRegImmPred extends Definition(
        "ImnmxPredPredRegRegImmPred",
        "IMNMX",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
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
  object ImnmxPredPredRegRegImmPredPred extends Definition(
        "ImnmxPredPredRegRegImmPredPred",
        "IMNMX",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate),
          Slot("pq", OperandKind.Predicate)
        ),
        Vec("imnmx_64__RIR_RsIR", "imnmx__RIR_RsIR")
      ) {
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
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
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
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pp", OperandKind.Predicate),
          Slot("pq", OperandKind.Predicate)
        ),
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
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
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
  object ImnmxPredRegRegImmPredPred extends Definition(
        "ImnmxPredRegRegImmPredPred",
        "IMNMX",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate),
          Slot("pq", OperandKind.Predicate)
        ),
        Vec("imnmx_64__RIR_RsIR", "imnmx__RIR_RsIR")
      ) {
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
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
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
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pp", OperandKind.Predicate),
          Slot("pq", OperandKind.Predicate)
        ),
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
  object ImnmxRegRegAddressImmPred extends Definition(
        "ImnmxRegRegAddressImmPred",
        "IMNMX",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("imnmx__RCxR_RCxR")
      ) {
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
  object ImnmxRegRegConstPred extends Definition(
        "ImnmxRegRegConstPred",
        "IMNMX",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("Const IMNMX", "Const_IMNMX", "imnmx__RCR_RCR")
      ) {
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
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
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
  object ImnmxRegRegImmPredPred extends Definition(
        "ImnmxRegRegImmPredPred",
        "IMNMX",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate),
          Slot("pq", OperandKind.Predicate)
        ),
        Vec("imnmx_64__RIR_RsIR", "imnmx__RIR_RsIR")
      ) {
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
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pa", OperandKind.Predicate)
        ),
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
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pp", OperandKind.Predicate),
          Slot("pq", OperandKind.Predicate)
        ),
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
  object ImnmxfauPipeRegRegConstPred extends Definition(
        "ImnmxfauPipeRegRegConstPred",
        "IMNMXfau_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Const IMNMX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.ImnmxfauPipeRegRegConstPred(dst, srcA, srcConst, pa, modifiers, guard))
        case _ => None
      }
  }
  object ImnmxfauPipeRegRegImmPred extends Definition(
        "ImnmxfauPipeRegRegImmPred",
        "IMNMXfau_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Imm IMNMX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.ImnmxfauPipeRegRegImmPred(dst, srcA, uImm, pa, modifiers, guard))
        case _ => None
      }
  }
  object ImnmxfauPipeRegRegRegPred extends Definition(
        "ImnmxfauPipeRegRegRegPred",
        "IMNMXfau_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("IMNMX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.ImnmxfauPipeRegRegRegPred(dst, srcA, srcB, pa, modifiers, guard))
        case _ => None
      }
  }
  object ImnmxfxuPipeRegRegConstPred extends Definition(
        "ImnmxfxuPipeRegRegConstPred",
        "IMNMXfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Const_IMNMX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.ImnmxfxuPipeRegRegConstPred(dst, srcA, srcConst, pa, modifiers, guard))
        case _ => None
      }
  }
  object ImnmxfxuPipeRegRegImmPred extends Definition(
        "ImnmxfxuPipeRegRegImmPred",
        "IMNMXfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Imm_IMNMX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.ImnmxfxuPipeRegRegImmPred(dst, srcA, uImm, pa, modifiers, guard))
        case _ => None
      }
  }
  object ImnmxfxuPipeRegRegRegPred extends Definition(
        "ImnmxfxuPipeRegRegRegPred",
        "IMNMXfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("IMNMX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.ImnmxfxuPipeRegRegRegPred(dst, srcA, srcB, pa, modifiers, guard))
        case _ => None
      }
  }
  object ImnmxintPipePredPredRegRegImmPred extends Definition(
        "ImnmxintPipePredPredRegRegImmPred",
        "IMNMXint_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
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
            ) => Some(Instruction.ImnmxintPipePredPredRegRegImmPred(pu, pv, dst, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object ImnmxintPipePredPredRegRegImmPredPred extends Definition(
        "ImnmxintPipePredPredRegRegImmPredPred",
        "IMNMXint_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate),
          Slot("pq", OperandKind.Predicate)
        ),
        Vec("imnmx_64__RIR_RsIR", "imnmx__RIR_RsIR")
      ) {
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
            ) =>
          Some(Instruction.ImnmxintPipePredPredRegRegImmPredPred(pu, pv, dst, srcA, sb, pp, pq, modifiers, guard))
        case _ => None
      }
  }
  object ImnmxintPipePredPredRegRegRegPred extends Definition(
        "ImnmxintPipePredPredRegRegRegPred",
        "IMNMXint_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
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
            ) => Some(Instruction.ImnmxintPipePredPredRegRegRegPred(pu, pv, dst, srcA, srcB, pp, modifiers, guard))
        case _ => None
      }
  }
  object ImnmxintPipePredPredRegRegRegPredPred extends Definition(
        "ImnmxintPipePredPredRegRegRegPredPred",
        "IMNMXint_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pp", OperandKind.Predicate),
          Slot("pq", OperandKind.Predicate)
        ),
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
            ) =>
          Some(Instruction.ImnmxintPipePredPredRegRegRegPredPred(pu, pv, dst, srcA, srcB, pp, pq, modifiers, guard))
        case _ => None
      }
  }
  object ImnmxintPipePredRegRegImmPred extends Definition(
        "ImnmxintPipePredRegRegImmPred",
        "IMNMXint_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
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
            ) => Some(Instruction.ImnmxintPipePredRegRegImmPred(pu, dst, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object ImnmxintPipePredRegRegImmPredPred extends Definition(
        "ImnmxintPipePredRegRegImmPredPred",
        "IMNMXint_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate),
          Slot("pq", OperandKind.Predicate)
        ),
        Vec("imnmx_64__RIR_RsIR", "imnmx__RIR_RsIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Predicate(pq)
            ) => Some(Instruction.ImnmxintPipePredRegRegImmPredPred(pu, dst, srcA, sb, pp, pq, modifiers, guard))
        case _ => None
      }
  }
  object ImnmxintPipePredRegRegRegPred extends Definition(
        "ImnmxintPipePredRegRegRegPred",
        "IMNMXint_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
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
            ) => Some(Instruction.ImnmxintPipePredRegRegRegPred(pu, dst, srcA, srcB, pp, modifiers, guard))
        case _ => None
      }
  }
  object ImnmxintPipePredRegRegRegPredPred extends Definition(
        "ImnmxintPipePredRegRegRegPredPred",
        "IMNMXint_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pp", OperandKind.Predicate),
          Slot("pq", OperandKind.Predicate)
        ),
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
            ) => Some(Instruction.ImnmxintPipePredRegRegRegPredPred(pu, dst, srcA, srcB, pp, pq, modifiers, guard))
        case _ => None
      }
  }
  object ImnmxintPipeRegRegAddressImmPred extends Definition(
        "ImnmxintPipeRegRegAddressImmPred",
        "IMNMXint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("imnmx__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.ImnmxintPipeRegRegAddressImmPred(dst, srcA, srcB, sb_offset, pp, modifiers, guard))
        case _ => None
      }
  }
  object ImnmxintPipeRegRegConstPred extends Definition(
        "ImnmxintPipeRegRegConstPred",
        "IMNMXint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("imnmx__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.ImnmxintPipeRegRegConstPred(dst, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object ImnmxintPipeRegRegImmPred extends Definition(
        "ImnmxintPipeRegRegImmPred",
        "IMNMXint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("imnmx_64_nopred__RIR_RsIR", "imnmx__RIR_RsIR", "imnmx__RsIR_RIR", "imnmx_nopred__RIR_RsIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.ImnmxintPipeRegRegImmPred(dst, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object ImnmxintPipeRegRegImmPredPred extends Definition(
        "ImnmxintPipeRegRegImmPredPred",
        "IMNMXint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate),
          Slot("pq", OperandKind.Predicate)
        ),
        Vec("imnmx_64__RIR_RsIR", "imnmx__RIR_RsIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Predicate(pq)
            ) => Some(Instruction.ImnmxintPipeRegRegImmPredPred(dst, srcA, sb, pp, pq, modifiers, guard))
        case _ => None
      }
  }
  object ImnmxintPipeRegRegRegPred extends Definition(
        "ImnmxintPipeRegRegRegPred",
        "IMNMXint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec(
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
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.ImnmxintPipeRegRegRegPred(dst, srcA, srcB, pp, modifiers, guard))
        case _ => None
      }
  }
  object ImnmxintPipeRegRegRegPredPred extends Definition(
        "ImnmxintPipeRegRegRegPredPred",
        "IMNMXint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pp", OperandKind.Predicate),
          Slot("pq", OperandKind.Predicate)
        ),
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
            ) => Some(Instruction.ImnmxintPipeRegRegRegPredPred(dst, srcA, srcB, pp, pq, modifiers, guard))
        case _ => None
      }
  }
  object ImnmxxluPipeRegRegConstPred extends Definition(
        "ImnmxxluPipeRegRegConstPred",
        "IMNMXxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Const IMNMX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.ImnmxxluPipeRegRegConstPred(dst, srcA, srcConst, pa, modifiers, guard))
        case _ => None
      }
  }
  object ImnmxxluPipeRegRegImmPred extends Definition(
        "ImnmxxluPipeRegRegImmPred",
        "IMNMXxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Imm IMNMX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.ImnmxxluPipeRegRegImmPred(dst, srcA, uImm, pa, modifiers, guard))
        case _ => None
      }
  }
  object ImnmxxluPipeRegRegRegPred extends Definition(
        "ImnmxxluPipeRegRegRegPred",
        "IMNMXxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("IMNMX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.ImnmxxluPipeRegRegRegPred(dst, srcA, srcB, pa, modifiers, guard))
        case _ => None
      }
  }
  object ImulRegPredRegAddressImm extends Definition(
        "ImulRegPredRegAddressImm",
        "IMUL",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("imul_wide__RCxR_RCxR")
      ) {
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
  object ImulRegPredRegConst extends Definition(
        "ImulRegPredRegConst",
        "IMUL",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank)
        ),
        Vec("imul_wide__RCR_RCR")
      ) {
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
  object ImulRegPredRegImm extends Definition(
        "ImulRegPredRegImm",
        "IMUL",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate)
        ),
        Vec("imul_wide__RsIR_RIR")
      ) {
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
  object ImulRegPredRegReg extends Definition(
        "ImulRegPredRegReg",
        "IMUL",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register)
        ),
        Vec("imul_wide__RRR_RRR", "imul_wide__RUR_RUR")
      ) {
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
  object ImulRegRegAddressImm extends Definition(
        "ImulRegRegAddressImm",
        "IMUL",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("imul__RCxR_RCxR", "imul_wide__RCxR_RCxR")
      ) {
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
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank)
        ),
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
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
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
  object ImulRegRegImmReg extends Definition(
        "ImulRegRegImmReg",
        "IMUL",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("imul_lo_xor__RsIR_RIR", "imul_wide_xor__RsIR_RIR")
      ) {
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
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
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
  object ImulRegRegRegImm extends Definition(
        "ImulRegRegRegImm",
        "IMUL",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate)
        ),
        Vec("imul_lo_xor__RRsI_RRI")
      ) {
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
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
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
  object Imul32iRegPredRegImm extends Definition(
        "Imul32iRegPredRegImm",
        "IMUL32I",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate)
        ),
        Vec("imul32i_wide__RsIR_RIR")
      ) {
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
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
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
  object Imul32ifmaxPipeRegRegImm extends Definition(
        "Imul32ifmaxPipeRegRegImm",
        "IMUL32IfmaX_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sImm", OperandKind.Immediate)),
        Vec("IMUL32I")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sImm)
            ) => Some(Instruction.Imul32ifmaxPipeRegRegImm(dst, srcA, sImm, modifiers, guard))
        case _ => None
      }
  }
  object Imul32ifmalighterPipeRegPredRegImm extends Definition(
        "Imul32ifmalighterPipeRegPredRegImm",
        "IMUL32Ifmalighter_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate)
        ),
        Vec("imul32i_wide__RsIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.Imul32ifmalighterPipeRegPredRegImm(dst, pu, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object Imul32ifmalighterPipeRegRegImm extends Definition(
        "Imul32ifmalighterPipeRegRegImm",
        "IMUL32Ifmalighter_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("imul32i_lo__RsIR_RIR", "imul32i_wide__RsIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.Imul32ifmalighterPipeRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object Imul32imioPipeRegRegImm extends Definition(
        "Imul32imioPipeRegRegImm",
        "IMUL32Imio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sImm", OperandKind.Immediate)),
        Vec("IMUL32I")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sImm)
            ) => Some(Instruction.Imul32imioPipeRegRegImm(dst, srcA, sImm, modifiers, guard))
        case _ => None
      }
  }
  object ImulfmaxPipeRegRegConst extends Definition(
        "ImulfmaxPipeRegRegConst",
        "IMULfmaX_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const IMUL")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.ImulfmaxPipeRegRegConst(dst, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object ImulfmaxPipeRegRegImm extends Definition(
        "ImulfmaxPipeRegRegImm",
        "IMULfmaX_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("Imm IMUL")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.ImulfmaxPipeRegRegImm(dst, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object ImulfmaxPipeRegRegReg extends Definition(
        "ImulfmaxPipeRegRegReg",
        "IMULfmaX_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("IMUL")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.ImulfmaxPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object ImulfmalighterPipeRegPredRegAddressImm extends Definition(
        "ImulfmalighterPipeRegPredRegAddressImm",
        "IMULfmalighter_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("imul_wide__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) =>
          Some(Instruction.ImulfmalighterPipeRegPredRegAddressImm(dst, pu, srcA, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object ImulfmalighterPipeRegPredRegConst extends Definition(
        "ImulfmalighterPipeRegPredRegConst",
        "IMULfmalighter_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank)
        ),
        Vec("imul_wide__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb)
            ) => Some(Instruction.ImulfmalighterPipeRegPredRegConst(dst, pu, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object ImulfmalighterPipeRegPredRegImm extends Definition(
        "ImulfmalighterPipeRegPredRegImm",
        "IMULfmalighter_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate)
        ),
        Vec("imul_wide__RsIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.ImulfmalighterPipeRegPredRegImm(dst, pu, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object ImulfmalighterPipeRegPredRegReg extends Definition(
        "ImulfmalighterPipeRegPredRegReg",
        "IMULfmalighter_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register)
        ),
        Vec("imul_wide__RRR_RRR", "imul_wide__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.ImulfmalighterPipeRegPredRegReg(dst, pu, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object ImulfmalighterPipeRegRegAddressImm extends Definition(
        "ImulfmalighterPipeRegRegAddressImm",
        "IMULfmalighter_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("imul__RCxR_RCxR", "imul_wide__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.ImulfmalighterPipeRegRegAddressImm(dst, srcA, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object ImulfmalighterPipeRegRegConst extends Definition(
        "ImulfmalighterPipeRegRegConst",
        "IMULfmalighter_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank)
        ),
        Vec("imul__RCR_RCR", "imul_wide__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb)
            ) => Some(Instruction.ImulfmalighterPipeRegRegConst(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object ImulfmalighterPipeRegRegImm extends Definition(
        "ImulfmalighterPipeRegRegImm",
        "IMULfmalighter_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("imul__RsIR_RIR", "imul_wide__RsIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.ImulfmalighterPipeRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object ImulfmalighterPipeRegRegImmReg extends Definition(
        "ImulfmalighterPipeRegRegImmReg",
        "IMULfmalighter_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("imul_lo_xor__RsIR_RIR", "imul_wide_xor__RsIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.ImulfmalighterPipeRegRegImmReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object ImulfmalighterPipeRegRegReg extends Definition(
        "ImulfmalighterPipeRegRegReg",
        "IMULfmalighter_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("imul__RRR_RRR", "imul__RUR_RUR", "imul_wide__RRR_RRR", "imul_wide__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.ImulfmalighterPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  val definitions: Vec[Definition] = Vec(
    IaddintPipeRegPredRegImm,
    IaddintPipeRegPredRegImmPred,
    IaddintPipeRegPredRegImmReg,
    IaddintPipeRegPredRegImmRegPred,
    IaddintPipeRegPredRegReg,
    IaddintPipeRegPredRegRegPred,
    IaddintPipeRegPredRegRegReg,
    IaddintPipeRegPredRegRegRegPred,
    IaddintPipeRegRegAddressImm,
    IaddintPipeRegRegAddressImmPred,
    IaddintPipeRegRegConst,
    IaddintPipeRegRegConstPred,
    IaddintPipeRegRegImm,
    IaddintPipeRegRegImmPred,
    IaddintPipeRegRegImmReg,
    IaddintPipeRegRegImmRegPred,
    IaddintPipeRegRegReg,
    IaddintPipeRegRegRegPred,
    IaddintPipeRegRegRegReg,
    IaddintPipeRegRegRegRegPred,
    IaddxluPipeRegRegConst,
    IaddxluPipeRegRegImm,
    IaddxluPipeRegRegReg,
    IcmpRegRegConstReg,
    IcmpRegRegImmReg,
    IcmpRegRegRegConst,
    IcmpRegRegRegReg,
    IcmpfauPipeRegRegConstReg,
    IcmpfauPipeRegRegImmReg,
    IcmpfauPipeRegRegRegConst,
    IcmpfauPipeRegRegRegReg,
    IcmpfxuPipeRegRegConstReg,
    IcmpfxuPipeRegRegImmReg,
    IcmpfxuPipeRegRegRegConst,
    IcmpfxuPipeRegRegRegReg,
    IcmpxluPipeRegRegConstReg,
    IcmpxluPipeRegRegImmReg,
    IcmpxluPipeRegRegRegConst,
    IcmpxluPipeRegRegRegReg,
    IdeNoOperands,
    IdeImm,
    IdebruPipeNoOperands,
    IdebruPipeImm,
    IdefxuPipeNoOperands,
    IdefxuPipeImm,
    IdeintPipeNoOperands,
    IdeintPipeImm,
    IdpRegRegAddressImmReg,
    IdpRegRegConstReg,
    IdpRegRegRegReg,
    Idp4aRegRegAddressImmReg,
    Idp4aRegRegConstReg,
    Idp4aRegRegRegReg,
    Idp4afmaiPipeRegRegConstReg,
    Idp4afmaiPipeRegRegRegReg,
    Idp4afmalighterPipeRegRegAddressImmReg,
    Idp4afmalighterPipeRegRegConstReg,
    Idp4afmalighterPipeRegRegRegReg,
    Idp4afxuPipeRegRegConstReg,
    Idp4afxuPipeRegRegRegReg,
    Idp4aintPipeRegRegAddressImmReg,
    Idp4aintPipeRegRegConstReg,
    Idp4aintPipeRegRegRegReg,
    IdpfmaiPipeRegRegConstReg,
    IdpfmaiPipeRegRegRegReg,
    IdpfmalighterPipeRegRegAddressImmReg,
    IdpfmalighterPipeRegRegConstReg,
    IdpfmalighterPipeRegRegRegReg,
    IdpfxuPipeRegRegConstReg,
    IdpfxuPipeRegRegRegReg,
    IdpintPipeRegRegAddressImmReg,
    IdpintPipeRegRegConstReg,
    IdpintPipeRegRegRegReg,
    IgmmaRegAddressReg,
    IgmmaRegAddressRegPred,
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
    IgmmamioPipeRegAddressReg,
    IgmmamioPipeRegAddressRegPred,
    IgmmamioPipeRegAddressRegPredReg,
    IgmmamioPipeRegAddressRegPredRegImm,
    IgmmamioPipeRegAddressRegReg,
    IgmmamioPipeRegAddressRegRegImm,
    IgmmamioPipeRegAddressRegRegPred,
    IgmmamioPipeRegAddressRegRegPredReg,
    IgmmamioPipeRegAddressRegRegPredRegImm,
    IgmmamioPipeRegAddressRegRegReg,
    IgmmamioPipeRegAddressRegRegRegImm,
    IgmmamioPipeRegRegAddressReg,
    IgmmamioPipeRegRegAddressRegPred,
    IgmmamioPipeRegRegAddressRegPredReg,
    IgmmamioPipeRegRegAddressRegPredRegImm,
    IgmmamioPipeRegRegAddressRegReg,
    IgmmamioPipeRegRegAddressRegRegImm,
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
    Imad32ifmaxPipeRegRegImmReg,
    Imad32imioPipeRegRegImmReg,
    ImadspRegRegConstReg,
    ImadspRegRegImmReg,
    ImadspRegRegRegConst,
    ImadspRegRegRegReg,
    ImadspfmaxPipeRegRegConstReg,
    ImadspfmaxPipeRegRegImmReg,
    ImadspfmaxPipeRegRegRegConst,
    ImadspfmaxPipeRegRegRegReg,
    ImadspmioPipeRegRegConstReg,
    ImadspmioPipeRegRegImmReg,
    ImadspmioPipeRegRegRegConst,
    ImadspmioPipeRegRegRegReg,
    ImadfmaxPipeRegRegConstReg,
    ImadfmaxPipeRegRegImmReg,
    ImadfmaxPipeRegRegRegConst,
    ImadfmaxPipeRegRegRegReg,
    ImadfmalighterPipeRegPredRegAddressImmReg,
    ImadfmalighterPipeRegPredRegAddressImmRegPred,
    ImadfmalighterPipeRegPredRegConstReg,
    ImadfmalighterPipeRegPredRegConstRegPred,
    ImadfmalighterPipeRegPredRegImmReg,
    ImadfmalighterPipeRegPredRegImmRegPred,
    ImadfmalighterPipeRegPredRegRegAddressImm,
    ImadfmalighterPipeRegPredRegRegAddressImmPred,
    ImadfmalighterPipeRegPredRegRegConst,
    ImadfmalighterPipeRegPredRegRegConstPred,
    ImadfmalighterPipeRegPredRegRegReg,
    ImadfmalighterPipeRegPredRegRegRegPred,
    ImadfmalighterPipeRegRegAddressImmReg,
    ImadfmalighterPipeRegRegAddressImmRegPred,
    ImadfmalighterPipeRegRegConstReg,
    ImadfmalighterPipeRegRegConstRegPred,
    ImadfmalighterPipeRegRegImmReg,
    ImadfmalighterPipeRegRegImmRegPred,
    ImadfmalighterPipeRegRegRegAddressImm,
    ImadfmalighterPipeRegRegRegAddressImmPred,
    ImadfmalighterPipeRegRegRegConst,
    ImadfmalighterPipeRegRegRegConstPred,
    ImadfmalighterPipeRegRegRegImm,
    ImadfmalighterPipeRegRegRegImmPred,
    ImadfmalighterPipeRegRegRegReg,
    ImadfmalighterPipeRegRegRegRegPred,
    ImadmioPipeRegRegConstReg,
    ImadmioPipeRegRegImmReg,
    ImadmioPipeRegRegRegConst,
    ImadmioPipeRegRegRegReg,
    ImmaRegRegRegReg,
    ImmaRegRegRegRegPred,
    ImmaRegRegRegRegPredRegImm,
    ImmaRegRegRegRegRegImm,
    ImmaintPipeRegRegRegReg,
    ImmaintPipeRegRegRegRegPred,
    ImmaintPipeRegRegRegRegPredRegImm,
    ImmaintPipeRegRegRegRegRegImm,
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
    ImnmxfauPipeRegRegConstPred,
    ImnmxfauPipeRegRegImmPred,
    ImnmxfauPipeRegRegRegPred,
    ImnmxfxuPipeRegRegConstPred,
    ImnmxfxuPipeRegRegImmPred,
    ImnmxfxuPipeRegRegRegPred,
    ImnmxintPipePredPredRegRegImmPred,
    ImnmxintPipePredPredRegRegImmPredPred,
    ImnmxintPipePredPredRegRegRegPred,
    ImnmxintPipePredPredRegRegRegPredPred,
    ImnmxintPipePredRegRegImmPred,
    ImnmxintPipePredRegRegImmPredPred,
    ImnmxintPipePredRegRegRegPred,
    ImnmxintPipePredRegRegRegPredPred,
    ImnmxintPipeRegRegAddressImmPred,
    ImnmxintPipeRegRegConstPred,
    ImnmxintPipeRegRegImmPred,
    ImnmxintPipeRegRegImmPredPred,
    ImnmxintPipeRegRegRegPred,
    ImnmxintPipeRegRegRegPredPred,
    ImnmxxluPipeRegRegConstPred,
    ImnmxxluPipeRegRegImmPred,
    ImnmxxluPipeRegRegRegPred,
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
    Imul32ifmaxPipeRegRegImm,
    Imul32ifmalighterPipeRegPredRegImm,
    Imul32ifmalighterPipeRegRegImm,
    Imul32imioPipeRegRegImm,
    ImulfmaxPipeRegRegConst,
    ImulfmaxPipeRegRegImm,
    ImulfmaxPipeRegRegReg,
    ImulfmalighterPipeRegPredRegAddressImm,
    ImulfmalighterPipeRegPredRegConst,
    ImulfmalighterPipeRegPredRegImm,
    ImulfmalighterPipeRegPredRegReg,
    ImulfmalighterPipeRegRegAddressImm,
    ImulfmalighterPipeRegRegConst,
    ImulfmalighterPipeRegRegImm,
    ImulfmalighterPipeRegRegImmReg,
    ImulfmalighterPipeRegRegReg
  )
}
