// Generated SASS instruction definitions and checked operand constructors. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.sass
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import Instruction.*
@javax.annotation.processing.Generated(Array("sbt codegen"))
object InstructionDefinitions11 {
  object SuldPredRegAddressRegImm extends Definition(
        "SuldPredRegAddressRegImm",
        "SULD",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("suld_d_urc_", "suld_d_urc_weakonly_", "suld_p_urc_", "suld_p_urc_weakonly_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.SuldPredRegAddressRegImm(pu, dst, srcA, srcC, tid8, modifiers, guard))
        case _ => None
      }
  }
  object SuldPredRegAddressRegReg extends Definition(
        "SuldPredRegAddressRegReg",
        "SULD",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register)
        ),
        Vec("suld_d_reg_", "suld_d_urc_", "suld_p_reg_", "suld_p_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe)
            ) => Some(Instruction.SuldPredRegAddressRegReg(pu, dst, srcA, srcC, uRe, modifiers, guard))
        case _ => None
      }
  }
  object SuldPredRegAddressRegRegImm extends Definition(
        "SuldPredRegAddressRegRegImm",
        "SULD",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("suld_d_urc_", "suld_p_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.SuldPredRegAddressRegRegImm(pu, dst, srcA, srcC, uRe, tid8, modifiers, guard))
        case _ => None
      }
  }
  object SuldPredRegAddressRegRegReg extends Definition(
        "SuldPredRegAddressRegRegReg",
        "SULD",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcC1", OperandKind.Register),
          Slot("srcC2", OperandKind.Register),
          Slot("uRe", OperandKind.Register)
        ),
        Vec("suld_d_r_urc_", "suld_p_r_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC1),
              InstructionOperand.Register(srcC2),
              InstructionOperand.Register(uRe)
            ) => Some(Instruction.SuldPredRegAddressRegRegReg(pu, dst, srcA, srcC1, srcC2, uRe, modifiers, guard))
        case _ => None
      }
  }
  object SuldRegAddressImm extends Definition(
        "SuldRegAddressImm",
        "SULD",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address), Slot("suid", OperandKind.Immediate)),
        Vec("SULD_D_IMM", "SULD_P_IMM")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Immediate(suid)
            ) => Some(Instruction.SuldRegAddressImm(dst, srcA, suid, modifiers, guard))
        case _ => None
      }
  }
  object SuldRegAddressImmImm extends Definition(
        "SuldRegAddressImmImm",
        "SULD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("bank5", OperandKind.Immediate),
          Slot("tsPtrIdx", OperandKind.Immediate)
        ),
        Vec("suld_d_imm_", "suld_d_imm_weakonly_", "suld_p_imm_", "suld_p_imm_weakonly_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(tsPtrIdx)
            ) => Some(Instruction.SuldRegAddressImmImm(dst, srcA, bank5, tsPtrIdx, modifiers, guard))
        case _ => None
      }
  }
  object SuldRegAddressImmImmImm extends Definition(
        "SuldRegAddressImmImmImm",
        "SULD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("bank5", OperandKind.Immediate),
          Slot("hdrtblbase6", OperandKind.Immediate),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("suld_d_tid_", "suld_d_tid_weakonly_", "suld_p_tid_", "suld_p_tid_weakonly_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(hdrtblbase6),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.SuldRegAddressImmImmImm(dst, srcA, bank5, hdrtblbase6, tid8, modifiers, guard))
        case _ => None
      }
  }
  object SuldRegAddressReg extends Definition(
        "SuldRegAddressReg",
        "SULD",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address), Slot("srcC", OperandKind.Register)),
        Vec(
          "SULD_D_REG",
          "SULD_P_REG",
          "suld_d_reg_",
          "suld_d_reg_weakonly_",
          "suld_d_urc_",
          "suld_p_reg_",
          "suld_p_reg_weakonly_",
          "suld_p_urc_"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.SuldRegAddressReg(dst, srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object SuldRegAddressRegImm extends Definition(
        "SuldRegAddressRegImm",
        "SULD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("suld_d_urc_", "suld_d_urc_weakonly_", "suld_p_urc_", "suld_p_urc_weakonly_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.SuldRegAddressRegImm(dst, srcA, srcC, tid8, modifiers, guard))
        case _ => None
      }
  }
  object SuldRegAddressRegReg extends Definition(
        "SuldRegAddressRegReg",
        "SULD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register)
        ),
        Vec("suld_d_reg_", "suld_d_urc_", "suld_p_reg_", "suld_p_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe)
            ) => Some(Instruction.SuldRegAddressRegReg(dst, srcA, srcC, uRe, modifiers, guard))
        case _ => None
      }
  }
  object SuldRegAddressRegRegImm extends Definition(
        "SuldRegAddressRegRegImm",
        "SULD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("suld_d_urc_", "suld_p_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.SuldRegAddressRegRegImm(dst, srcA, srcC, uRe, tid8, modifiers, guard))
        case _ => None
      }
  }
  object SuldRegAddressRegRegReg extends Definition(
        "SuldRegAddressRegRegReg",
        "SULD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcC1", OperandKind.Register),
          Slot("srcC2", OperandKind.Register),
          Slot("uRe", OperandKind.Register)
        ),
        Vec("suld_d_r_urc_", "suld_p_r_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC1),
              InstructionOperand.Register(srcC2),
              InstructionOperand.Register(uRe)
            ) => Some(Instruction.SuldRegAddressRegRegReg(dst, srcA, srcC1, srcC2, uRe, modifiers, guard))
        case _ => None
      }
  }
  object SuldgaRegAddressConstPred extends Definition(
        "SuldgaRegAddressConstPred",
        "SULDGA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("pcl", OperandKind.Predicate)
        ),
        Vec("SULDGA.B cimm")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Predicate(pcl)
            ) => Some(Instruction.SuldgaRegAddressConstPred(dst, srcA, srcConst, pcl, modifiers, guard))
        case _ => None
      }
  }
  object SuldgaRegAddressRegPred extends Definition(
        "SuldgaRegAddressRegPred",
        "SULDGA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("pcl", OperandKind.Predicate)
        ),
        Vec("SULDGA.B")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pcl)
            ) => Some(Instruction.SuldgaRegAddressRegPred(dst, srcA, srcB, pcl, modifiers, guard))
        case _ => None
      }
  }
  object SuldgaaguPipeRegAddressConstPred extends Definition(
        "SuldgaaguPipeRegAddressConstPred",
        "SULDGAagu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("pcl", OperandKind.Predicate)
        ),
        Vec("SULDGA.B cimm")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Predicate(pcl)
            ) => Some(Instruction.SuldgaaguPipeRegAddressConstPred(dst, srcA, srcConst, pcl, modifiers, guard))
        case _ => None
      }
  }
  object SuldgaaguPipeRegAddressRegPred extends Definition(
        "SuldgaaguPipeRegAddressRegPred",
        "SULDGAagu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("pcl", OperandKind.Predicate)
        ),
        Vec("SULDGA.B")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pcl)
            ) => Some(Instruction.SuldgaaguPipeRegAddressRegPred(dst, srcA, srcB, pcl, modifiers, guard))
        case _ => None
      }
  }
  object SuldmioPipePredRegAddressImm extends Definition(
        "SuldmioPipePredRegAddressImm",
        "SULDmio_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("suid", OperandKind.Immediate)
        ),
        Vec("SULD_D_IMM", "SULD_P_IMM")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Immediate(suid)
            ) => Some(Instruction.SuldmioPipePredRegAddressImm(dstPredicate, dst, srcA, suid, modifiers, guard))
        case _ => None
      }
  }
  object SuldmioPipePredRegAddressImmImm extends Definition(
        "SuldmioPipePredRegAddressImmImm",
        "SULDmio_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("bank5", OperandKind.Immediate),
          Slot("tsPtrIdx", OperandKind.Immediate)
        ),
        Vec("suld_d_imm_", "suld_d_imm_weakonly_", "suld_p_imm_", "suld_p_imm_weakonly_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(tsPtrIdx)
            ) => Some(Instruction.SuldmioPipePredRegAddressImmImm(pu, dst, srcA, bank5, tsPtrIdx, modifiers, guard))
        case _ => None
      }
  }
  object SuldmioPipePredRegAddressImmImmImm extends Definition(
        "SuldmioPipePredRegAddressImmImmImm",
        "SULDmio_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("bank5", OperandKind.Immediate),
          Slot("hdrtblbase6", OperandKind.Immediate),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("suld_d_tid_", "suld_d_tid_weakonly_", "suld_p_tid_", "suld_p_tid_weakonly_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(hdrtblbase6),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.SuldmioPipePredRegAddressImmImmImm(
            pu,
            dst,
            srcA,
            bank5,
            hdrtblbase6,
            tid8,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object SuldmioPipePredRegAddressReg extends Definition(
        "SuldmioPipePredRegAddressReg",
        "SULDmio_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcC", OperandKind.Register)
        ),
        Vec(
          "SULD_D_REG",
          "SULD_P_REG",
          "suld_d_reg_",
          "suld_d_reg_weakonly_",
          "suld_d_urc_",
          "suld_p_reg_",
          "suld_p_reg_weakonly_",
          "suld_p_urc_"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.SuldmioPipePredRegAddressReg(dstPredicate, dst, srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object SuldmioPipePredRegAddressRegImm extends Definition(
        "SuldmioPipePredRegAddressRegImm",
        "SULDmio_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("suld_d_urc_", "suld_d_urc_weakonly_", "suld_p_urc_", "suld_p_urc_weakonly_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.SuldmioPipePredRegAddressRegImm(pu, dst, srcA, srcC, tid8, modifiers, guard))
        case _ => None
      }
  }
  object SuldmioPipePredRegAddressRegReg extends Definition(
        "SuldmioPipePredRegAddressRegReg",
        "SULDmio_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register)
        ),
        Vec("suld_d_reg_", "suld_d_urc_", "suld_p_reg_", "suld_p_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe)
            ) => Some(Instruction.SuldmioPipePredRegAddressRegReg(pu, dst, srcA, srcC, uRe, modifiers, guard))
        case _ => None
      }
  }
  object SuldmioPipePredRegAddressRegRegImm extends Definition(
        "SuldmioPipePredRegAddressRegRegImm",
        "SULDmio_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("suld_d_urc_", "suld_p_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.SuldmioPipePredRegAddressRegRegImm(pu, dst, srcA, srcC, uRe, tid8, modifiers, guard))
        case _ => None
      }
  }
  object SuldmioPipePredRegAddressRegRegReg extends Definition(
        "SuldmioPipePredRegAddressRegRegReg",
        "SULDmio_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcC1", OperandKind.Register),
          Slot("srcC2", OperandKind.Register),
          Slot("uRe", OperandKind.Register)
        ),
        Vec("suld_d_r_urc_", "suld_p_r_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC1),
              InstructionOperand.Register(srcC2),
              InstructionOperand.Register(uRe)
            ) =>
          Some(Instruction.SuldmioPipePredRegAddressRegRegReg(pu, dst, srcA, srcC1, srcC2, uRe, modifiers, guard))
        case _ => None
      }
  }
  object SuldmioPipeRegAddressImm extends Definition(
        "SuldmioPipeRegAddressImm",
        "SULDmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address), Slot("suid", OperandKind.Immediate)),
        Vec("SULD_D_IMM", "SULD_P_IMM")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Immediate(suid)
            ) => Some(Instruction.SuldmioPipeRegAddressImm(dst, srcA, suid, modifiers, guard))
        case _ => None
      }
  }
  object SuldmioPipeRegAddressImmImm extends Definition(
        "SuldmioPipeRegAddressImmImm",
        "SULDmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("bank5", OperandKind.Immediate),
          Slot("tsPtrIdx", OperandKind.Immediate)
        ),
        Vec("suld_d_imm_", "suld_d_imm_weakonly_", "suld_p_imm_", "suld_p_imm_weakonly_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(tsPtrIdx)
            ) => Some(Instruction.SuldmioPipeRegAddressImmImm(dst, srcA, bank5, tsPtrIdx, modifiers, guard))
        case _ => None
      }
  }
  object SuldmioPipeRegAddressImmImmImm extends Definition(
        "SuldmioPipeRegAddressImmImmImm",
        "SULDmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("bank5", OperandKind.Immediate),
          Slot("hdrtblbase6", OperandKind.Immediate),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("suld_d_tid_", "suld_d_tid_weakonly_", "suld_p_tid_", "suld_p_tid_weakonly_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(hdrtblbase6),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.SuldmioPipeRegAddressImmImmImm(dst, srcA, bank5, hdrtblbase6, tid8, modifiers, guard))
        case _ => None
      }
  }
  object SuldmioPipeRegAddressReg extends Definition(
        "SuldmioPipeRegAddressReg",
        "SULDmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address), Slot("srcC", OperandKind.Register)),
        Vec(
          "SULD_D_REG",
          "SULD_P_REG",
          "suld_d_reg_",
          "suld_d_reg_weakonly_",
          "suld_d_urc_",
          "suld_p_reg_",
          "suld_p_reg_weakonly_",
          "suld_p_urc_"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.SuldmioPipeRegAddressReg(dst, srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object SuldmioPipeRegAddressRegImm extends Definition(
        "SuldmioPipeRegAddressRegImm",
        "SULDmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("suld_d_urc_", "suld_d_urc_weakonly_", "suld_p_urc_", "suld_p_urc_weakonly_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.SuldmioPipeRegAddressRegImm(dst, srcA, srcC, tid8, modifiers, guard))
        case _ => None
      }
  }
  object SuldmioPipeRegAddressRegReg extends Definition(
        "SuldmioPipeRegAddressRegReg",
        "SULDmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register)
        ),
        Vec("suld_d_reg_", "suld_d_urc_", "suld_p_reg_", "suld_p_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe)
            ) => Some(Instruction.SuldmioPipeRegAddressRegReg(dst, srcA, srcC, uRe, modifiers, guard))
        case _ => None
      }
  }
  object SuldmioPipeRegAddressRegRegImm extends Definition(
        "SuldmioPipeRegAddressRegRegImm",
        "SULDmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("suld_d_urc_", "suld_p_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.SuldmioPipeRegAddressRegRegImm(dst, srcA, srcC, uRe, tid8, modifiers, guard))
        case _ => None
      }
  }
  object SuldmioPipeRegAddressRegRegReg extends Definition(
        "SuldmioPipeRegAddressRegRegReg",
        "SULDmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcC1", OperandKind.Register),
          Slot("srcC2", OperandKind.Register),
          Slot("uRe", OperandKind.Register)
        ),
        Vec("suld_d_r_urc_", "suld_p_r_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC1),
              InstructionOperand.Register(srcC2),
              InstructionOperand.Register(uRe)
            ) => Some(Instruction.SuldmioPipeRegAddressRegRegReg(dst, srcA, srcC1, srcC2, uRe, modifiers, guard))
        case _ => None
      }
  }
  object SuqueryPredRegAddressImmImm extends Definition(
        "SuqueryPredRegAddressImmImm",
        "SUQUERY",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("bank5", OperandKind.Immediate),
          Slot("tsPtrIdx", OperandKind.Immediate)
        ),
        Vec("suquery_imm_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(tsPtrIdx)
            ) => Some(Instruction.SuqueryPredRegAddressImmImm(pu, dst, srcA, bank5, tsPtrIdx, modifiers, guard))
        case _ => None
      }
  }
  object SuqueryPredRegAddressImmImmImm extends Definition(
        "SuqueryPredRegAddressImmImmImm",
        "SUQUERY",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("bank5", OperandKind.Immediate),
          Slot("hdrtblbase6", OperandKind.Immediate),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("suquery_tid_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(hdrtblbase6),
              InstructionOperand.Immediate(tid8)
            ) =>
          Some(Instruction.SuqueryPredRegAddressImmImmImm(pu, dst, srcA, bank5, hdrtblbase6, tid8, modifiers, guard))
        case _ => None
      }
  }
  object SuqueryPredRegAddressReg extends Definition(
        "SuqueryPredRegAddressReg",
        "SUQUERY",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("suquery_reg_", "suquery_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.SuqueryPredRegAddressReg(pu, dst, srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object SuqueryPredRegAddressRegImm extends Definition(
        "SuqueryPredRegAddressRegImm",
        "SUQUERY",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("suquery_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.SuqueryPredRegAddressRegImm(pu, dst, srcA, srcC, tid8, modifiers, guard))
        case _ => None
      }
  }
  object SuqueryPredRegAddressRegReg extends Definition(
        "SuqueryPredRegAddressRegReg",
        "SUQUERY",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register)
        ),
        Vec("suquery_reg_", "suquery_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe)
            ) => Some(Instruction.SuqueryPredRegAddressRegReg(pu, dst, srcA, srcC, uRe, modifiers, guard))
        case _ => None
      }
  }
  object SuqueryPredRegAddressRegRegImm extends Definition(
        "SuqueryPredRegAddressRegRegImm",
        "SUQUERY",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("suquery_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.SuqueryPredRegAddressRegRegImm(pu, dst, srcA, srcC, uRe, tid8, modifiers, guard))
        case _ => None
      }
  }
  object SuqueryPredRegAddressRegRegReg extends Definition(
        "SuqueryPredRegAddressRegRegReg",
        "SUQUERY",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcC1", OperandKind.Register),
          Slot("srcC2", OperandKind.Register),
          Slot("uRe", OperandKind.Register)
        ),
        Vec("suquery_r_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC1),
              InstructionOperand.Register(srcC2),
              InstructionOperand.Register(uRe)
            ) => Some(Instruction.SuqueryPredRegAddressRegRegReg(pu, dst, srcA, srcC1, srcC2, uRe, modifiers, guard))
        case _ => None
      }
  }
  object SuqueryRegAddressImmImm extends Definition(
        "SuqueryRegAddressImmImm",
        "SUQUERY",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("bank5", OperandKind.Immediate),
          Slot("tsPtrIdx", OperandKind.Immediate)
        ),
        Vec("suquery_imm_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(tsPtrIdx)
            ) => Some(Instruction.SuqueryRegAddressImmImm(dst, srcA, bank5, tsPtrIdx, modifiers, guard))
        case _ => None
      }
  }
  object SuqueryRegAddressImmImmImm extends Definition(
        "SuqueryRegAddressImmImmImm",
        "SUQUERY",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("bank5", OperandKind.Immediate),
          Slot("hdrtblbase6", OperandKind.Immediate),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("suquery_tid_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(hdrtblbase6),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.SuqueryRegAddressImmImmImm(dst, srcA, bank5, hdrtblbase6, tid8, modifiers, guard))
        case _ => None
      }
  }
  object SuqueryRegAddressReg extends Definition(
        "SuqueryRegAddressReg",
        "SUQUERY",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address), Slot("srcC", OperandKind.Register)),
        Vec("suquery_reg_", "suquery_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.SuqueryRegAddressReg(dst, srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object SuqueryRegAddressRegImm extends Definition(
        "SuqueryRegAddressRegImm",
        "SUQUERY",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("suquery_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.SuqueryRegAddressRegImm(dst, srcA, srcC, tid8, modifiers, guard))
        case _ => None
      }
  }
  object SuqueryRegAddressRegReg extends Definition(
        "SuqueryRegAddressRegReg",
        "SUQUERY",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register)
        ),
        Vec("suquery_reg_", "suquery_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe)
            ) => Some(Instruction.SuqueryRegAddressRegReg(dst, srcA, srcC, uRe, modifiers, guard))
        case _ => None
      }
  }
  object SuqueryRegAddressRegRegImm extends Definition(
        "SuqueryRegAddressRegRegImm",
        "SUQUERY",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("suquery_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.SuqueryRegAddressRegRegImm(dst, srcA, srcC, uRe, tid8, modifiers, guard))
        case _ => None
      }
  }
  object SuqueryRegAddressRegRegReg extends Definition(
        "SuqueryRegAddressRegRegReg",
        "SUQUERY",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcC1", OperandKind.Register),
          Slot("srcC2", OperandKind.Register),
          Slot("uRe", OperandKind.Register)
        ),
        Vec("suquery_r_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC1),
              InstructionOperand.Register(srcC2),
              InstructionOperand.Register(uRe)
            ) => Some(Instruction.SuqueryRegAddressRegRegReg(dst, srcA, srcC1, srcC2, uRe, modifiers, guard))
        case _ => None
      }
  }
  object SuquerymioPipePredRegAddressImmImm extends Definition(
        "SuquerymioPipePredRegAddressImmImm",
        "SUQUERYmio_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("bank5", OperandKind.Immediate),
          Slot("tsPtrIdx", OperandKind.Immediate)
        ),
        Vec("suquery_imm_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(tsPtrIdx)
            ) => Some(Instruction.SuquerymioPipePredRegAddressImmImm(pu, dst, srcA, bank5, tsPtrIdx, modifiers, guard))
        case _ => None
      }
  }
  object SuquerymioPipePredRegAddressImmImmImm extends Definition(
        "SuquerymioPipePredRegAddressImmImmImm",
        "SUQUERYmio_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("bank5", OperandKind.Immediate),
          Slot("hdrtblbase6", OperandKind.Immediate),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("suquery_tid_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(hdrtblbase6),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.SuquerymioPipePredRegAddressImmImmImm(
            pu,
            dst,
            srcA,
            bank5,
            hdrtblbase6,
            tid8,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object SuquerymioPipePredRegAddressReg extends Definition(
        "SuquerymioPipePredRegAddressReg",
        "SUQUERYmio_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("suquery_reg_", "suquery_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.SuquerymioPipePredRegAddressReg(pu, dst, srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object SuquerymioPipePredRegAddressRegImm extends Definition(
        "SuquerymioPipePredRegAddressRegImm",
        "SUQUERYmio_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("suquery_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.SuquerymioPipePredRegAddressRegImm(pu, dst, srcA, srcC, tid8, modifiers, guard))
        case _ => None
      }
  }
  object SuquerymioPipePredRegAddressRegReg extends Definition(
        "SuquerymioPipePredRegAddressRegReg",
        "SUQUERYmio_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register)
        ),
        Vec("suquery_reg_", "suquery_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe)
            ) => Some(Instruction.SuquerymioPipePredRegAddressRegReg(pu, dst, srcA, srcC, uRe, modifiers, guard))
        case _ => None
      }
  }
  object SuquerymioPipePredRegAddressRegRegImm extends Definition(
        "SuquerymioPipePredRegAddressRegRegImm",
        "SUQUERYmio_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("suquery_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe),
              InstructionOperand.Immediate(tid8)
            ) =>
          Some(Instruction.SuquerymioPipePredRegAddressRegRegImm(pu, dst, srcA, srcC, uRe, tid8, modifiers, guard))
        case _ => None
      }
  }
  object SuquerymioPipePredRegAddressRegRegReg extends Definition(
        "SuquerymioPipePredRegAddressRegRegReg",
        "SUQUERYmio_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcC1", OperandKind.Register),
          Slot("srcC2", OperandKind.Register),
          Slot("uRe", OperandKind.Register)
        ),
        Vec("suquery_r_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC1),
              InstructionOperand.Register(srcC2),
              InstructionOperand.Register(uRe)
            ) =>
          Some(Instruction.SuquerymioPipePredRegAddressRegRegReg(pu, dst, srcA, srcC1, srcC2, uRe, modifiers, guard))
        case _ => None
      }
  }
  object SuquerymioPipeRegAddressImmImm extends Definition(
        "SuquerymioPipeRegAddressImmImm",
        "SUQUERYmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("bank5", OperandKind.Immediate),
          Slot("tsPtrIdx", OperandKind.Immediate)
        ),
        Vec("suquery_imm_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(tsPtrIdx)
            ) => Some(Instruction.SuquerymioPipeRegAddressImmImm(dst, srcA, bank5, tsPtrIdx, modifiers, guard))
        case _ => None
      }
  }
  object SuquerymioPipeRegAddressImmImmImm extends Definition(
        "SuquerymioPipeRegAddressImmImmImm",
        "SUQUERYmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("bank5", OperandKind.Immediate),
          Slot("hdrtblbase6", OperandKind.Immediate),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("suquery_tid_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(hdrtblbase6),
              InstructionOperand.Immediate(tid8)
            ) =>
          Some(Instruction.SuquerymioPipeRegAddressImmImmImm(dst, srcA, bank5, hdrtblbase6, tid8, modifiers, guard))
        case _ => None
      }
  }
  object SuquerymioPipeRegAddressReg extends Definition(
        "SuquerymioPipeRegAddressReg",
        "SUQUERYmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address), Slot("srcC", OperandKind.Register)),
        Vec("suquery_reg_", "suquery_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.SuquerymioPipeRegAddressReg(dst, srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object SuquerymioPipeRegAddressRegImm extends Definition(
        "SuquerymioPipeRegAddressRegImm",
        "SUQUERYmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("suquery_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.SuquerymioPipeRegAddressRegImm(dst, srcA, srcC, tid8, modifiers, guard))
        case _ => None
      }
  }
  object SuquerymioPipeRegAddressRegReg extends Definition(
        "SuquerymioPipeRegAddressRegReg",
        "SUQUERYmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register)
        ),
        Vec("suquery_reg_", "suquery_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe)
            ) => Some(Instruction.SuquerymioPipeRegAddressRegReg(dst, srcA, srcC, uRe, modifiers, guard))
        case _ => None
      }
  }
  object SuquerymioPipeRegAddressRegRegImm extends Definition(
        "SuquerymioPipeRegAddressRegRegImm",
        "SUQUERYmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("suquery_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.SuquerymioPipeRegAddressRegRegImm(dst, srcA, srcC, uRe, tid8, modifiers, guard))
        case _ => None
      }
  }
  object SuquerymioPipeRegAddressRegRegReg extends Definition(
        "SuquerymioPipeRegAddressRegRegReg",
        "SUQUERYmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcC1", OperandKind.Register),
          Slot("srcC2", OperandKind.Register),
          Slot("uRe", OperandKind.Register)
        ),
        Vec("suquery_r_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC1),
              InstructionOperand.Register(srcC2),
              InstructionOperand.Register(uRe)
            ) => Some(Instruction.SuquerymioPipeRegAddressRegRegReg(dst, srcA, srcC1, srcC2, uRe, modifiers, guard))
        case _ => None
      }
  }
  object SuredAddressRegImm extends Definition(
        "SuredAddressRegImm",
        "SURED",
        Vec(Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register), Slot("suid", OperandKind.Immediate)),
        Vec("SURED_D_IMM", "SURED_P_IMM")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(suid)
            ) => Some(Instruction.SuredAddressRegImm(srcA, srcB, suid, modifiers, guard))
        case _ => None
      }
  }
  object SuredAddressRegImmImm extends Definition(
        "SuredAddressRegImmImm",
        "SURED",
        Vec(
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("tsPtrIdx", OperandKind.Immediate)
        ),
        Vec("sured_imm_", "sured_imm_weakonly_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(tsPtrIdx)
            ) => Some(Instruction.SuredAddressRegImmImm(srcA, srcB, bank5, tsPtrIdx, modifiers, guard))
        case _ => None
      }
  }
  object SuredAddressRegImmImmImm extends Definition(
        "SuredAddressRegImmImmImm",
        "SURED",
        Vec(
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("hdrtblbase6", OperandKind.Immediate),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("sured_tid_", "sured_tid_weakonly_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(hdrtblbase6),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.SuredAddressRegImmImmImm(srcA, srcB, bank5, hdrtblbase6, tid8, modifiers, guard))
        case _ => None
      }
  }
  object SuredAddressRegReg extends Definition(
        "SuredAddressRegReg",
        "SURED",
        Vec(Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register), Slot("srcC", OperandKind.Register)),
        Vec("SURED_D_REG", "SURED_P_REG", "sured_reg_", "sured_reg_weakonly_", "sured_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.SuredAddressRegReg(srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object SuredAddressRegRegImm extends Definition(
        "SuredAddressRegRegImm",
        "SURED",
        Vec(
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("sured_urc_", "sured_urc_weakonly_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.SuredAddressRegRegImm(srcA, srcB, srcC, tid8, modifiers, guard))
        case _ => None
      }
  }
  object SuredAddressRegRegReg extends Definition(
        "SuredAddressRegRegReg",
        "SURED",
        Vec(
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register)
        ),
        Vec("sured_reg_", "sured_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe)
            ) => Some(Instruction.SuredAddressRegRegReg(srcA, srcB, srcC, uRe, modifiers, guard))
        case _ => None
      }
  }
  object SuredAddressRegRegRegImm extends Definition(
        "SuredAddressRegRegRegImm",
        "SURED",
        Vec(
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("sured_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.SuredAddressRegRegRegImm(srcA, srcB, srcC, uRe, tid8, modifiers, guard))
        case _ => None
      }
  }
  object SuredAddressRegRegRegReg extends Definition(
        "SuredAddressRegRegRegReg",
        "SURED",
        Vec(
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC1", OperandKind.Register),
          Slot("srcC2", OperandKind.Register),
          Slot("uRe", OperandKind.Register)
        ),
        Vec("sured_r_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC1),
              InstructionOperand.Register(srcC2),
              InstructionOperand.Register(uRe)
            ) => Some(Instruction.SuredAddressRegRegRegReg(srcA, srcB, srcC1, srcC2, uRe, modifiers, guard))
        case _ => None
      }
  }
  object SuredmioPipeAddressRegImm extends Definition(
        "SuredmioPipeAddressRegImm",
        "SUREDmio_pipe",
        Vec(Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register), Slot("suid", OperandKind.Immediate)),
        Vec("SURED_D_IMM", "SURED_P_IMM")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(suid)
            ) => Some(Instruction.SuredmioPipeAddressRegImm(srcA, srcB, suid, modifiers, guard))
        case _ => None
      }
  }
  object SuredmioPipeAddressRegImmImm extends Definition(
        "SuredmioPipeAddressRegImmImm",
        "SUREDmio_pipe",
        Vec(
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("tsPtrIdx", OperandKind.Immediate)
        ),
        Vec("sured_imm_", "sured_imm_weakonly_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(tsPtrIdx)
            ) => Some(Instruction.SuredmioPipeAddressRegImmImm(srcA, srcB, bank5, tsPtrIdx, modifiers, guard))
        case _ => None
      }
  }
  object SuredmioPipeAddressRegImmImmImm extends Definition(
        "SuredmioPipeAddressRegImmImmImm",
        "SUREDmio_pipe",
        Vec(
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("hdrtblbase6", OperandKind.Immediate),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("sured_tid_", "sured_tid_weakonly_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(hdrtblbase6),
              InstructionOperand.Immediate(tid8)
            ) =>
          Some(Instruction.SuredmioPipeAddressRegImmImmImm(srcA, srcB, bank5, hdrtblbase6, tid8, modifiers, guard))
        case _ => None
      }
  }
  object SuredmioPipeAddressRegReg extends Definition(
        "SuredmioPipeAddressRegReg",
        "SUREDmio_pipe",
        Vec(Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register), Slot("srcC", OperandKind.Register)),
        Vec("SURED_D_REG", "SURED_P_REG", "sured_reg_", "sured_reg_weakonly_", "sured_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.SuredmioPipeAddressRegReg(srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object SuredmioPipeAddressRegRegImm extends Definition(
        "SuredmioPipeAddressRegRegImm",
        "SUREDmio_pipe",
        Vec(
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("sured_urc_", "sured_urc_weakonly_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.SuredmioPipeAddressRegRegImm(srcA, srcB, srcC, tid8, modifiers, guard))
        case _ => None
      }
  }
  object SuredmioPipeAddressRegRegReg extends Definition(
        "SuredmioPipeAddressRegRegReg",
        "SUREDmio_pipe",
        Vec(
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register)
        ),
        Vec("sured_reg_", "sured_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe)
            ) => Some(Instruction.SuredmioPipeAddressRegRegReg(srcA, srcB, srcC, uRe, modifiers, guard))
        case _ => None
      }
  }
  object SuredmioPipeAddressRegRegRegImm extends Definition(
        "SuredmioPipeAddressRegRegRegImm",
        "SUREDmio_pipe",
        Vec(
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("sured_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.SuredmioPipeAddressRegRegRegImm(srcA, srcB, srcC, uRe, tid8, modifiers, guard))
        case _ => None
      }
  }
  object SuredmioPipeAddressRegRegRegReg extends Definition(
        "SuredmioPipeAddressRegRegRegReg",
        "SUREDmio_pipe",
        Vec(
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC1", OperandKind.Register),
          Slot("srcC2", OperandKind.Register),
          Slot("uRe", OperandKind.Register)
        ),
        Vec("sured_r_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC1),
              InstructionOperand.Register(srcC2),
              InstructionOperand.Register(uRe)
            ) => Some(Instruction.SuredmioPipeAddressRegRegRegReg(srcA, srcB, srcC1, srcC2, uRe, modifiers, guard))
        case _ => None
      }
  }
  object SustAddressRegImm extends Definition(
        "SustAddressRegImm",
        "SUST",
        Vec(Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register), Slot("suid", OperandKind.Immediate)),
        Vec("SUST_D_IMM", "SUST_P_IMM")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(suid)
            ) => Some(Instruction.SustAddressRegImm(srcA, srcB, suid, modifiers, guard))
        case _ => None
      }
  }
  object SustAddressRegImmImm extends Definition(
        "SustAddressRegImmImm",
        "SUST",
        Vec(
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("tsPtrIdx", OperandKind.Immediate)
        ),
        Vec("sust_d_imm_", "sust_d_imm_weakonly_", "sust_p_imm_", "sust_p_imm_weakonly_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(tsPtrIdx)
            ) => Some(Instruction.SustAddressRegImmImm(srcA, srcB, bank5, tsPtrIdx, modifiers, guard))
        case _ => None
      }
  }
  object SustAddressRegImmImmImm extends Definition(
        "SustAddressRegImmImmImm",
        "SUST",
        Vec(
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("hdrtblbase6", OperandKind.Immediate),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("sust_d_tid_", "sust_d_tid_weakonly_", "sust_p_tid_", "sust_p_tid_weakonly_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(hdrtblbase6),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.SustAddressRegImmImmImm(srcA, srcB, bank5, hdrtblbase6, tid8, modifiers, guard))
        case _ => None
      }
  }
  object SustAddressRegReg extends Definition(
        "SustAddressRegReg",
        "SUST",
        Vec(Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register), Slot("srcC", OperandKind.Register)),
        Vec(
          "SUST_D_REG",
          "SUST_P_REG",
          "sust_d_reg_",
          "sust_d_reg_weakonly_",
          "sust_d_urc_",
          "sust_p_reg_",
          "sust_p_reg_weakonly_",
          "sust_p_urc_"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.SustAddressRegReg(srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object SustAddressRegRegImm extends Definition(
        "SustAddressRegRegImm",
        "SUST",
        Vec(
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("sust_d_urc_", "sust_d_urc_weakonly_", "sust_p_urc_", "sust_p_urc_weakonly_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.SustAddressRegRegImm(srcA, srcB, srcC, tid8, modifiers, guard))
        case _ => None
      }
  }
  object SustAddressRegRegReg extends Definition(
        "SustAddressRegRegReg",
        "SUST",
        Vec(
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register)
        ),
        Vec("sust_d_reg_", "sust_d_urc_", "sust_p_reg_", "sust_p_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe)
            ) => Some(Instruction.SustAddressRegRegReg(srcA, srcB, srcC, uRe, modifiers, guard))
        case _ => None
      }
  }
  object SustAddressRegRegRegImm extends Definition(
        "SustAddressRegRegRegImm",
        "SUST",
        Vec(
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("sust_d_urc_", "sust_p_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.SustAddressRegRegRegImm(srcA, srcB, srcC, uRe, tid8, modifiers, guard))
        case _ => None
      }
  }
  object SustAddressRegRegRegReg extends Definition(
        "SustAddressRegRegRegReg",
        "SUST",
        Vec(
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC1", OperandKind.Register),
          Slot("srcC2", OperandKind.Register),
          Slot("uRe", OperandKind.Register)
        ),
        Vec("sust_d_r_urc_", "sust_p_r_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC1),
              InstructionOperand.Register(srcC2),
              InstructionOperand.Register(uRe)
            ) => Some(Instruction.SustAddressRegRegRegReg(srcA, srcB, srcC1, srcC2, uRe, modifiers, guard))
        case _ => None
      }
  }
  object SustgaAddressConstRegPred extends Definition(
        "SustgaAddressConstRegPred",
        "SUSTGA",
        Vec(
          Slot("srcA", OperandKind.Address),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register),
          Slot("pcl", OperandKind.Predicate)
        ),
        Vec("SUSTGA.B cimm", "SUSTGA.P cimm")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pcl)
            ) => Some(Instruction.SustgaAddressConstRegPred(srcA, srcConst, srcC, pcl, modifiers, guard))
        case _ => None
      }
  }
  object SustgaAddressRegRegPred extends Definition(
        "SustgaAddressRegRegPred",
        "SUSTGA",
        Vec(
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("pcl", OperandKind.Predicate)
        ),
        Vec("SUSTGA.B", "SUSTGA.P")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pcl)
            ) => Some(Instruction.SustgaAddressRegRegPred(srcA, srcB, srcC, pcl, modifiers, guard))
        case _ => None
      }
  }
  object SustgaaguPipeAddressConstRegPred extends Definition(
        "SustgaaguPipeAddressConstRegPred",
        "SUSTGAagu_pipe",
        Vec(
          Slot("srcA", OperandKind.Address),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register),
          Slot("pcl", OperandKind.Predicate)
        ),
        Vec("SUSTGA.B cimm", "SUSTGA.P cimm")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pcl)
            ) => Some(Instruction.SustgaaguPipeAddressConstRegPred(srcA, srcConst, srcC, pcl, modifiers, guard))
        case _ => None
      }
  }
  object SustgaaguPipeAddressRegRegPred extends Definition(
        "SustgaaguPipeAddressRegRegPred",
        "SUSTGAagu_pipe",
        Vec(
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("pcl", OperandKind.Predicate)
        ),
        Vec("SUSTGA.B", "SUSTGA.P")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pcl)
            ) => Some(Instruction.SustgaaguPipeAddressRegRegPred(srcA, srcB, srcC, pcl, modifiers, guard))
        case _ => None
      }
  }
  object SustmioPipeAddressRegImm extends Definition(
        "SustmioPipeAddressRegImm",
        "SUSTmio_pipe",
        Vec(Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register), Slot("suid", OperandKind.Immediate)),
        Vec("SUST_D_IMM", "SUST_P_IMM")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(suid)
            ) => Some(Instruction.SustmioPipeAddressRegImm(srcA, srcB, suid, modifiers, guard))
        case _ => None
      }
  }
  object SustmioPipeAddressRegImmImm extends Definition(
        "SustmioPipeAddressRegImmImm",
        "SUSTmio_pipe",
        Vec(
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("tsPtrIdx", OperandKind.Immediate)
        ),
        Vec("sust_d_imm_", "sust_d_imm_weakonly_", "sust_p_imm_", "sust_p_imm_weakonly_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(tsPtrIdx)
            ) => Some(Instruction.SustmioPipeAddressRegImmImm(srcA, srcB, bank5, tsPtrIdx, modifiers, guard))
        case _ => None
      }
  }
  object SustmioPipeAddressRegImmImmImm extends Definition(
        "SustmioPipeAddressRegImmImmImm",
        "SUSTmio_pipe",
        Vec(
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("hdrtblbase6", OperandKind.Immediate),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("sust_d_tid_", "sust_d_tid_weakonly_", "sust_p_tid_", "sust_p_tid_weakonly_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(hdrtblbase6),
              InstructionOperand.Immediate(tid8)
            ) =>
          Some(Instruction.SustmioPipeAddressRegImmImmImm(srcA, srcB, bank5, hdrtblbase6, tid8, modifiers, guard))
        case _ => None
      }
  }
  object SustmioPipeAddressRegReg extends Definition(
        "SustmioPipeAddressRegReg",
        "SUSTmio_pipe",
        Vec(Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register), Slot("srcC", OperandKind.Register)),
        Vec(
          "SUST_D_REG",
          "SUST_P_REG",
          "sust_d_reg_",
          "sust_d_reg_weakonly_",
          "sust_d_urc_",
          "sust_p_reg_",
          "sust_p_reg_weakonly_",
          "sust_p_urc_"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.SustmioPipeAddressRegReg(srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object SustmioPipeAddressRegRegImm extends Definition(
        "SustmioPipeAddressRegRegImm",
        "SUSTmio_pipe",
        Vec(
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("sust_d_urc_", "sust_d_urc_weakonly_", "sust_p_urc_", "sust_p_urc_weakonly_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.SustmioPipeAddressRegRegImm(srcA, srcB, srcC, tid8, modifiers, guard))
        case _ => None
      }
  }
  object SustmioPipeAddressRegRegReg extends Definition(
        "SustmioPipeAddressRegRegReg",
        "SUSTmio_pipe",
        Vec(
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register)
        ),
        Vec("sust_d_reg_", "sust_d_urc_", "sust_p_reg_", "sust_p_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe)
            ) => Some(Instruction.SustmioPipeAddressRegRegReg(srcA, srcB, srcC, uRe, modifiers, guard))
        case _ => None
      }
  }
  object SustmioPipeAddressRegRegRegImm extends Definition(
        "SustmioPipeAddressRegRegRegImm",
        "SUSTmio_pipe",
        Vec(
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("sust_d_urc_", "sust_p_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.SustmioPipeAddressRegRegRegImm(srcA, srcB, srcC, uRe, tid8, modifiers, guard))
        case _ => None
      }
  }
  object SustmioPipeAddressRegRegRegReg extends Definition(
        "SustmioPipeAddressRegRegRegReg",
        "SUSTmio_pipe",
        Vec(
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC1", OperandKind.Register),
          Slot("srcC2", OperandKind.Register),
          Slot("uRe", OperandKind.Register)
        ),
        Vec("sust_d_r_urc_", "sust_p_r_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC1),
              InstructionOperand.Register(srcC2),
              InstructionOperand.Register(uRe)
            ) => Some(Instruction.SustmioPipeAddressRegRegRegReg(srcA, srcB, srcC1, srcC2, uRe, modifiers, guard))
        case _ => None
      }
  }
  object SyncNoOperands extends Definition("SyncNoOperands", "SYNC", Vec(), Vec("SYNC")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.SyncNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object SyncsNoOperands extends Definition("SyncsNoOperands", "SYNCS", Vec(), Vec("syncs_cctl_all_", "syncs_flush_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.SyncsNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object SyncsAddress
      extends Definition("SyncsAddress", "SYNCS", Vec(Slot("srcA", OperandKind.Address)), Vec("syncs_cctl_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA)) => Some(Instruction.SyncsAddress(srcA, modifiers, guard))
        case _ => None
      }
  }
  object SyncsAddressReg extends Definition(
        "SyncsAddressReg",
        "SYNCS",
        Vec(Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register)),
        Vec("syncs_tcnt_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA), InstructionOperand.Register(srcB)) =>
          Some(Instruction.SyncsAddressReg(srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object SyncsPredAddressReg extends Definition(
        "SyncsPredAddressReg",
        "SYNCS",
        Vec(Slot("pu", OperandKind.Predicate), Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register)),
        Vec("syncs_phasechk_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.SyncsPredAddressReg(pu, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object SyncsRegAddress extends Definition(
        "SyncsRegAddress",
        "SYNCS",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address)),
        Vec("syncs_ld_", "syncs_uniform_ld_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Address(srcA)) =>
          Some(Instruction.SyncsRegAddress(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object SyncsRegAddressReg extends Definition(
        "SyncsRegAddressReg",
        "SYNCS",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register)),
        Vec("syncs_arrive_", "syncs_uniform_exch_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.SyncsRegAddressReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object SyncsRegAddressRegReg extends Definition(
        "SyncsRegAddressRegReg",
        "SYNCS",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("syncs_uniform_cas_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.SyncsRegAddressRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object SyncsmioPipeNoOperands
      extends Definition("SyncsmioPipeNoOperands", "SYNCSmio_pipe", Vec(), Vec("syncs_cctl_all_", "syncs_flush_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.SyncsmioPipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object SyncsmioPipeAddress extends Definition(
        "SyncsmioPipeAddress",
        "SYNCSmio_pipe",
        Vec(Slot("srcA", OperandKind.Address)),
        Vec("syncs_cctl_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA)) => Some(Instruction.SyncsmioPipeAddress(srcA, modifiers, guard))
        case _ => None
      }
  }
  object SyncsmioPipeAddressReg extends Definition(
        "SyncsmioPipeAddressReg",
        "SYNCSmio_pipe",
        Vec(Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register)),
        Vec("syncs_tcnt_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA), InstructionOperand.Register(srcB)) =>
          Some(Instruction.SyncsmioPipeAddressReg(srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object SyncsmioPipePredAddressReg extends Definition(
        "SyncsmioPipePredAddressReg",
        "SYNCSmio_pipe",
        Vec(Slot("pu", OperandKind.Predicate), Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register)),
        Vec("syncs_phasechk_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.SyncsmioPipePredAddressReg(pu, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object SyncsmioPipeRegAddress extends Definition(
        "SyncsmioPipeRegAddress",
        "SYNCSmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address)),
        Vec("syncs_ld_", "syncs_uniform_ld_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Address(srcA)) =>
          Some(Instruction.SyncsmioPipeRegAddress(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object SyncsmioPipeRegAddressReg extends Definition(
        "SyncsmioPipeRegAddressReg",
        "SYNCSmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register)),
        Vec("syncs_arrive_", "syncs_uniform_exch_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.SyncsmioPipeRegAddressReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object SyncsmioPipeRegAddressRegReg extends Definition(
        "SyncsmioPipeRegAddressRegReg",
        "SYNCSmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("syncs_uniform_cas_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.SyncsmioPipeRegAddressRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object SyncbruPipeNoOperands extends Definition("SyncbruPipeNoOperands", "SYNCbru_pipe", Vec(), Vec("SYNC")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.SyncbruPipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object TexPredRegRegRegImm extends Definition(
        "TexPredRegRegRegImm",
        "TEX",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("tid", OperandKind.Immediate)
        ),
        Vec("TEX", "TEX_B")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid)
            ) => Some(Instruction.TexPredRegRegRegImm(dstPredicate, dst, srcA, srcB, tid, modifiers, guard))
        case _ => None
      }
  }
  object TexPredRegRegRegImmImm extends Definition(
        "TexPredRegRegRegImmImm",
        "TEX",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("tid", OperandKind.Immediate),
          Slot("smp", OperandKind.Immediate)
        ),
        Vec("TEX", "TEX_B", "TEX_B_legacy_style", "TEX_legacy_style")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(smp)
            ) => Some(Instruction.TexPredRegRegRegImmImm(dstPredicate, dst, srcA, srcB, tid, smp, modifiers, guard))
        case _ => None
      }
  }
  object TexPredRegRegRegImmImmImm extends Definition(
        "TexPredRegRegRegImmImmImm",
        "TEX",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("tid", OperandKind.Immediate),
          Slot("smp", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("TEX_B_legacy_style", "TEX_legacy_style")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(smp),
              InstructionOperand.Immediate(wmsk)
            ) =>
          Some(Instruction.TexPredRegRegRegImmImmImm(dstPredicate, dst, srcA, srcB, tid, smp, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TexPredRegRegRegReg extends Definition(
        "TexPredRegRegRegReg",
        "TEX",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register)
        ),
        Vec("tex_b_noConst_", "tex_scr_b_noConst_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.TexPredRegRegRegReg(pu, rd2, dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object TexPredRegRegRegRegImm extends Definition(
        "TexPredRegRegRegRegImm",
        "TEX",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("tex_b_noConst_", "tex_scr_b_noConst_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TexPredRegRegRegRegImm(pu, rd2, dst, srcA, srcB, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TexPredRegRegRegRegImmImm extends Definition(
        "TexPredRegRegRegRegImmImm",
        "TEX",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("tsPtrIdx", OperandKind.Immediate)
        ),
        Vec("tex_", "tex_b_", "tex_scr_", "tex_scr_b_")
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
          Some(Instruction.TexPredRegRegRegRegImmImm(pu, rd2, dst, srcA, srcB, bank5, tsPtrIdx, modifiers, guard))
        case _ => None
      }
  }
  object TexPredRegRegRegRegImmImmImm extends Definition(
        "TexPredRegRegRegRegImmImmImm",
        "TEX",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("hdrtblbase6", OperandKind.Immediate),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("tex_", "tex_b_", "tex_b_tid_", "tex_scr_", "tex_scr_b_", "tex_scr_b_tid_")
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
            ) => Some(Instruction.TexPredRegRegRegRegImmImmImm(
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
  object TexPredRegRegRegRegImmImmImmImm extends Definition(
        "TexPredRegRegRegRegImmImmImmImm",
        "TEX",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("hdrtblbase6", OperandKind.Immediate),
          Slot("tid8", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("tex_b_tid_", "tex_scr_b_tid_")
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
              InstructionOperand.Immediate(tid8),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TexPredRegRegRegRegImmImmImmImm(
            pu,
            rd2,
            dst,
            srcA,
            srcB,
            bank5,
            hdrtblbase6,
            tid8,
            wmsk,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object TexPredRegRegRegRegReg extends Definition(
        "TexPredRegRegRegRegReg",
        "TEX",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("tex_b_noConst_", "tex_scr_b_noConst_", "tex_scr_urc_", "tex_urc_")
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
            ) => Some(Instruction.TexPredRegRegRegRegReg(pu, rd2, dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object TexPredRegRegRegRegRegImm extends Definition(
        "TexPredRegRegRegRegRegImm",
        "TEX",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("tex_b_noConst_", "tex_scr_b_noConst_", "tex_scr_urc_", "tex_urc_")
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
            ) => Some(Instruction.TexPredRegRegRegRegRegImm(pu, rd2, dst, srcA, srcB, srcC, tid8, modifiers, guard))
        case _ => None
      }
  }
  object TexPredRegRegRegRegRegImmImm extends Definition(
        "TexPredRegRegRegRegRegImmImm",
        "TEX",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("tex_scr_urc_", "tex_urc_")
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
              InstructionOperand.Immediate(tid8),
              InstructionOperand.Immediate(wmsk)
            ) =>
          Some(Instruction.TexPredRegRegRegRegRegImmImm(pu, rd2, dst, srcA, srcB, srcC, tid8, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TexPredRegRegRegRegRegReg extends Definition(
        "TexPredRegRegRegRegRegReg",
        "TEX",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register)
        ),
        Vec("tex_b_urc_", "tex_scr_b_urc_", "tex_scr_urc_", "tex_urc_")
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
              InstructionOperand.Register(uRe)
            ) => Some(Instruction.TexPredRegRegRegRegRegReg(pu, rd2, dst, srcA, srcB, srcC, uRe, modifiers, guard))
        case _ => None
      }
  }
  object TexPredRegRegRegRegRegRegImm extends Definition(
        "TexPredRegRegRegRegRegRegImm",
        "TEX",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("tex_b_urc_", "tex_scr_b_urc_", "tex_scr_urc_", "tex_urc_")
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
              InstructionOperand.Register(uRe),
              InstructionOperand.Immediate(tid8)
            ) =>
          Some(Instruction.TexPredRegRegRegRegRegRegImm(pu, rd2, dst, srcA, srcB, srcC, uRe, tid8, modifiers, guard))
        case _ => None
      }
  }
  object TexPredRegRegRegRegRegRegImmImm extends Definition(
        "TexPredRegRegRegRegRegRegImmImm",
        "TEX",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("tex_scr_urc_", "tex_urc_")
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
              InstructionOperand.Register(uRe),
              InstructionOperand.Immediate(tid8),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TexPredRegRegRegRegRegRegImmImm(
            pu,
            rd2,
            dst,
            srcA,
            srcB,
            srcC,
            uRe,
            tid8,
            wmsk,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object TexRegRegRegImm extends Definition(
        "TexRegRegRegImm",
        "TEX",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("tid", OperandKind.Immediate)
        ),
        Vec("TEX", "TEX_B")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid)
            ) => Some(Instruction.TexRegRegRegImm(dst, srcA, srcB, tid, modifiers, guard))
        case _ => None
      }
  }
  object TexRegRegRegImmImm extends Definition(
        "TexRegRegRegImmImm",
        "TEX",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("tid", OperandKind.Immediate),
          Slot("smp", OperandKind.Immediate)
        ),
        Vec("TEX", "TEX B", "TEX_B", "TEX_B_legacy_style", "TEX_legacy_style")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(smp)
            ) => Some(Instruction.TexRegRegRegImmImm(dst, srcA, srcB, tid, smp, modifiers, guard))
        case _ => None
      }
  }
  object TexRegRegRegImmImmImm extends Definition(
        "TexRegRegRegImmImmImm",
        "TEX",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("tid", OperandKind.Immediate),
          Slot("smp", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("TEX B legacy style", "TEX legacy style", "TEX_B_legacy_style", "TEX_legacy_style")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(smp),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TexRegRegRegImmImmImm(dst, srcA, srcB, tid, smp, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TexRegRegRegReg extends Definition(
        "TexRegRegRegReg",
        "TEX",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register)
        ),
        Vec("tex_b_noConst_", "tex_scr_b_noConst_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.TexRegRegRegReg(rd2, dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object TexRegRegRegRegImm extends Definition(
        "TexRegRegRegRegImm",
        "TEX",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("tex_b_noConst_", "tex_scr_b_noConst_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TexRegRegRegRegImm(rd2, dst, srcA, srcB, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TexRegRegRegRegImmImm extends Definition(
        "TexRegRegRegRegImmImm",
        "TEX",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("tsPtrIdx", OperandKind.Immediate)
        ),
        Vec("tex_", "tex_b_", "tex_scr_", "tex_scr_b_")
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
            ) => Some(Instruction.TexRegRegRegRegImmImm(rd2, dst, srcA, srcB, bank5, tsPtrIdx, modifiers, guard))
        case _ => None
      }
  }
  object TexRegRegRegRegImmImmImm extends Definition(
        "TexRegRegRegRegImmImmImm",
        "TEX",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("hdrtblbase6", OperandKind.Immediate),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("tex_", "tex_b_", "tex_b_tid_", "tex_scr_", "tex_scr_b_", "tex_scr_b_tid_")
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
            ) =>
          Some(Instruction.TexRegRegRegRegImmImmImm(rd2, dst, srcA, srcB, bank5, hdrtblbase6, tid8, modifiers, guard))
        case _ => None
      }
  }
  object TexRegRegRegRegImmImmImmImm extends Definition(
        "TexRegRegRegRegImmImmImmImm",
        "TEX",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("hdrtblbase6", OperandKind.Immediate),
          Slot("tid8", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("tex_b_tid_", "tex_scr_b_tid_")
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
              InstructionOperand.Immediate(tid8),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TexRegRegRegRegImmImmImmImm(
            rd2,
            dst,
            srcA,
            srcB,
            bank5,
            hdrtblbase6,
            tid8,
            wmsk,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object TexRegRegRegRegReg extends Definition(
        "TexRegRegRegRegReg",
        "TEX",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("tex_b_noConst_", "tex_scr_b_noConst_", "tex_scr_urc_", "tex_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.TexRegRegRegRegReg(rd2, dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object TexRegRegRegRegRegImm extends Definition(
        "TexRegRegRegRegRegImm",
        "TEX",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("tex_b_noConst_", "tex_scr_b_noConst_", "tex_scr_urc_", "tex_urc_")
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
            ) => Some(Instruction.TexRegRegRegRegRegImm(rd2, dst, srcA, srcB, srcC, tid8, modifiers, guard))
        case _ => None
      }
  }
  object TexRegRegRegRegRegImmImm extends Definition(
        "TexRegRegRegRegRegImmImm",
        "TEX",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("tex_scr_urc_", "tex_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(tid8),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TexRegRegRegRegRegImmImm(rd2, dst, srcA, srcB, srcC, tid8, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TexRegRegRegRegRegReg extends Definition(
        "TexRegRegRegRegRegReg",
        "TEX",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register)
        ),
        Vec("tex_b_urc_", "tex_scr_b_urc_", "tex_scr_urc_", "tex_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe)
            ) => Some(Instruction.TexRegRegRegRegRegReg(rd2, dst, srcA, srcB, srcC, uRe, modifiers, guard))
        case _ => None
      }
  }
  object TexRegRegRegRegRegRegImm extends Definition(
        "TexRegRegRegRegRegRegImm",
        "TEX",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("tex_b_urc_", "tex_scr_b_urc_", "tex_scr_urc_", "tex_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.TexRegRegRegRegRegRegImm(rd2, dst, srcA, srcB, srcC, uRe, tid8, modifiers, guard))
        case _ => None
      }
  }
  object TexRegRegRegRegRegRegImmImm extends Definition(
        "TexRegRegRegRegRegRegImmImm",
        "TEX",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("tex_scr_urc_", "tex_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe),
              InstructionOperand.Immediate(tid8),
              InstructionOperand.Immediate(wmsk)
            ) =>
          Some(Instruction.TexRegRegRegRegRegRegImmImm(rd2, dst, srcA, srcB, srcC, uRe, tid8, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TexdepbarImm
      extends Definition("TexdepbarImm", "TEXDEPBAR", Vec(Slot("uImm", OperandKind.Immediate)), Vec("TEXDEPBAR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(uImm)) => Some(Instruction.TexdepbarImm(uImm, modifiers, guard))
        case _ => None
      }
  }
  object TexdepbarbruPipeImm extends Definition(
        "TexdepbarbruPipeImm",
        "TEXDEPBARbru_pipe",
        Vec(Slot("uImm", OperandKind.Immediate)),
        Vec("TEXDEPBAR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(uImm)) => Some(Instruction.TexdepbarbruPipeImm(uImm, modifiers, guard))
        case _ => None
      }
  }
  object TexsRegRegRegRegImm extends Definition(
        "TexsRegRegRegRegImm",
        "TEXS",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("tid", OperandKind.Immediate)
        ),
        Vec("TEXS", "TEXS_F16", "TEXS_F16_RZ", "TEXS_RZ")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid)
            ) => Some(Instruction.TexsRegRegRegRegImm(rd2, dst, srcA, srcB, tid, modifiers, guard))
        case _ => None
      }
  }
  object TexsmioPipeRegRegRegRegImm extends Definition(
        "TexsmioPipeRegRegRegRegImm",
        "TEXSmio_pipe",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("tid", OperandKind.Immediate)
        ),
        Vec("TEXS", "TEXS_F16", "TEXS_F16_RZ", "TEXS_RZ")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid)
            ) => Some(Instruction.TexsmioPipeRegRegRegRegImm(rd2, dst, srcA, srcB, tid, modifiers, guard))
        case _ => None
      }
  }
  object TexmioPipePredRegRegRegImm extends Definition(
        "TexmioPipePredRegRegRegImm",
        "TEXmio_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("tid", OperandKind.Immediate)
        ),
        Vec("TEX", "TEX_B")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid)
            ) => Some(Instruction.TexmioPipePredRegRegRegImm(dstPredicate, dst, srcA, srcB, tid, modifiers, guard))
        case _ => None
      }
  }
  object TexmioPipePredRegRegRegImmImm extends Definition(
        "TexmioPipePredRegRegRegImmImm",
        "TEXmio_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("tid", OperandKind.Immediate),
          Slot("smp", OperandKind.Immediate)
        ),
        Vec("TEX", "TEX_B", "TEX_B_legacy_style", "TEX_legacy_style")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(smp)
            ) =>
          Some(Instruction.TexmioPipePredRegRegRegImmImm(dstPredicate, dst, srcA, srcB, tid, smp, modifiers, guard))
        case _ => None
      }
  }
  object TexmioPipePredRegRegRegImmImmImm extends Definition(
        "TexmioPipePredRegRegRegImmImmImm",
        "TEXmio_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("tid", OperandKind.Immediate),
          Slot("smp", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("TEX_B_legacy_style", "TEX_legacy_style")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(smp),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TexmioPipePredRegRegRegImmImmImm(
            dstPredicate,
            dst,
            srcA,
            srcB,
            tid,
            smp,
            wmsk,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object TexmioPipePredRegRegRegReg extends Definition(
        "TexmioPipePredRegRegRegReg",
        "TEXmio_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register)
        ),
        Vec("tex_b_noConst_", "tex_scr_b_noConst_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.TexmioPipePredRegRegRegReg(pu, rd2, dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object TexmioPipePredRegRegRegRegImm extends Definition(
        "TexmioPipePredRegRegRegRegImm",
        "TEXmio_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("tex_b_noConst_", "tex_scr_b_noConst_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TexmioPipePredRegRegRegRegImm(pu, rd2, dst, srcA, srcB, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TexmioPipePredRegRegRegRegImmImm extends Definition(
        "TexmioPipePredRegRegRegRegImmImm",
        "TEXmio_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("tsPtrIdx", OperandKind.Immediate)
        ),
        Vec("tex_", "tex_b_", "tex_scr_", "tex_scr_b_")
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
            ) => Some(Instruction.TexmioPipePredRegRegRegRegImmImm(
            pu,
            rd2,
            dst,
            srcA,
            srcB,
            bank5,
            tsPtrIdx,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object TexmioPipePredRegRegRegRegImmImmImm extends Definition(
        "TexmioPipePredRegRegRegRegImmImmImm",
        "TEXmio_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("hdrtblbase6", OperandKind.Immediate),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("tex_", "tex_b_", "tex_b_tid_", "tex_scr_", "tex_scr_b_", "tex_scr_b_tid_")
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
            ) => Some(Instruction.TexmioPipePredRegRegRegRegImmImmImm(
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
  object TexmioPipePredRegRegRegRegImmImmImmImm extends Definition(
        "TexmioPipePredRegRegRegRegImmImmImmImm",
        "TEXmio_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("hdrtblbase6", OperandKind.Immediate),
          Slot("tid8", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("tex_b_tid_", "tex_scr_b_tid_")
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
              InstructionOperand.Immediate(tid8),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TexmioPipePredRegRegRegRegImmImmImmImm(
            pu,
            rd2,
            dst,
            srcA,
            srcB,
            bank5,
            hdrtblbase6,
            tid8,
            wmsk,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object TexmioPipePredRegRegRegRegReg extends Definition(
        "TexmioPipePredRegRegRegRegReg",
        "TEXmio_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("tex_b_noConst_", "tex_scr_b_noConst_", "tex_scr_urc_", "tex_urc_")
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
            ) => Some(Instruction.TexmioPipePredRegRegRegRegReg(pu, rd2, dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object TexmioPipePredRegRegRegRegRegImm extends Definition(
        "TexmioPipePredRegRegRegRegRegImm",
        "TEXmio_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("tex_b_noConst_", "tex_scr_b_noConst_", "tex_scr_urc_", "tex_urc_")
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
          Some(Instruction.TexmioPipePredRegRegRegRegRegImm(pu, rd2, dst, srcA, srcB, srcC, tid8, modifiers, guard))
        case _ => None
      }
  }
  object TexmioPipePredRegRegRegRegRegImmImm extends Definition(
        "TexmioPipePredRegRegRegRegRegImmImm",
        "TEXmio_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("tex_scr_urc_", "tex_urc_")
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
              InstructionOperand.Immediate(tid8),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TexmioPipePredRegRegRegRegRegImmImm(
            pu,
            rd2,
            dst,
            srcA,
            srcB,
            srcC,
            tid8,
            wmsk,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object TexmioPipePredRegRegRegRegRegReg extends Definition(
        "TexmioPipePredRegRegRegRegRegReg",
        "TEXmio_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register)
        ),
        Vec("tex_b_urc_", "tex_scr_b_urc_", "tex_scr_urc_", "tex_urc_")
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
              InstructionOperand.Register(uRe)
            ) =>
          Some(Instruction.TexmioPipePredRegRegRegRegRegReg(pu, rd2, dst, srcA, srcB, srcC, uRe, modifiers, guard))
        case _ => None
      }
  }
  object TexmioPipePredRegRegRegRegRegRegImm extends Definition(
        "TexmioPipePredRegRegRegRegRegRegImm",
        "TEXmio_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("tex_b_urc_", "tex_scr_b_urc_", "tex_scr_urc_", "tex_urc_")
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
              InstructionOperand.Register(uRe),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.TexmioPipePredRegRegRegRegRegRegImm(
            pu,
            rd2,
            dst,
            srcA,
            srcB,
            srcC,
            uRe,
            tid8,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object TexmioPipePredRegRegRegRegRegRegImmImm extends Definition(
        "TexmioPipePredRegRegRegRegRegRegImmImm",
        "TEXmio_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("tex_scr_urc_", "tex_urc_")
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
              InstructionOperand.Register(uRe),
              InstructionOperand.Immediate(tid8),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TexmioPipePredRegRegRegRegRegRegImmImm(
            pu,
            rd2,
            dst,
            srcA,
            srcB,
            srcC,
            uRe,
            tid8,
            wmsk,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object TexmioPipeRegRegRegImm extends Definition(
        "TexmioPipeRegRegRegImm",
        "TEXmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("tid", OperandKind.Immediate)
        ),
        Vec("TEX", "TEX_B")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid)
            ) => Some(Instruction.TexmioPipeRegRegRegImm(dst, srcA, srcB, tid, modifiers, guard))
        case _ => None
      }
  }
  object TexmioPipeRegRegRegImmImm extends Definition(
        "TexmioPipeRegRegRegImmImm",
        "TEXmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("tid", OperandKind.Immediate),
          Slot("smp", OperandKind.Immediate)
        ),
        Vec("TEX", "TEX_B", "TEX_B_legacy_style", "TEX_legacy_style")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(smp)
            ) => Some(Instruction.TexmioPipeRegRegRegImmImm(dst, srcA, srcB, tid, smp, modifiers, guard))
        case _ => None
      }
  }
  object TexmioPipeRegRegRegImmImmImm extends Definition(
        "TexmioPipeRegRegRegImmImmImm",
        "TEXmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("tid", OperandKind.Immediate),
          Slot("smp", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("TEX_B_legacy_style", "TEX_legacy_style")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(smp),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TexmioPipeRegRegRegImmImmImm(dst, srcA, srcB, tid, smp, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TexmioPipeRegRegRegReg extends Definition(
        "TexmioPipeRegRegRegReg",
        "TEXmio_pipe",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register)
        ),
        Vec("tex_b_noConst_", "tex_scr_b_noConst_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.TexmioPipeRegRegRegReg(rd2, dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object TexmioPipeRegRegRegRegImm extends Definition(
        "TexmioPipeRegRegRegRegImm",
        "TEXmio_pipe",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("tex_b_noConst_", "tex_scr_b_noConst_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TexmioPipeRegRegRegRegImm(rd2, dst, srcA, srcB, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TexmioPipeRegRegRegRegImmImm extends Definition(
        "TexmioPipeRegRegRegRegImmImm",
        "TEXmio_pipe",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("tsPtrIdx", OperandKind.Immediate)
        ),
        Vec("tex_", "tex_b_", "tex_scr_", "tex_scr_b_")
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
            ) => Some(Instruction.TexmioPipeRegRegRegRegImmImm(rd2, dst, srcA, srcB, bank5, tsPtrIdx, modifiers, guard))
        case _ => None
      }
  }
  object TexmioPipeRegRegRegRegImmImmImm extends Definition(
        "TexmioPipeRegRegRegRegImmImmImm",
        "TEXmio_pipe",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("hdrtblbase6", OperandKind.Immediate),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("tex_", "tex_b_", "tex_b_tid_", "tex_scr_", "tex_scr_b_", "tex_scr_b_tid_")
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
            ) => Some(Instruction.TexmioPipeRegRegRegRegImmImmImm(
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
  object TexmioPipeRegRegRegRegImmImmImmImm extends Definition(
        "TexmioPipeRegRegRegRegImmImmImmImm",
        "TEXmio_pipe",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("hdrtblbase6", OperandKind.Immediate),
          Slot("tid8", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("tex_b_tid_", "tex_scr_b_tid_")
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
              InstructionOperand.Immediate(tid8),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TexmioPipeRegRegRegRegImmImmImmImm(
            rd2,
            dst,
            srcA,
            srcB,
            bank5,
            hdrtblbase6,
            tid8,
            wmsk,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object TexmioPipeRegRegRegRegReg extends Definition(
        "TexmioPipeRegRegRegRegReg",
        "TEXmio_pipe",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("tex_b_noConst_", "tex_scr_b_noConst_", "tex_scr_urc_", "tex_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.TexmioPipeRegRegRegRegReg(rd2, dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object TexmioPipeRegRegRegRegRegImm extends Definition(
        "TexmioPipeRegRegRegRegRegImm",
        "TEXmio_pipe",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("tex_b_noConst_", "tex_scr_b_noConst_", "tex_scr_urc_", "tex_urc_")
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
            ) => Some(Instruction.TexmioPipeRegRegRegRegRegImm(rd2, dst, srcA, srcB, srcC, tid8, modifiers, guard))
        case _ => None
      }
  }
  object TexmioPipeRegRegRegRegRegImmImm extends Definition(
        "TexmioPipeRegRegRegRegRegImmImm",
        "TEXmio_pipe",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("tex_scr_urc_", "tex_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(tid8),
              InstructionOperand.Immediate(wmsk)
            ) =>
          Some(Instruction.TexmioPipeRegRegRegRegRegImmImm(rd2, dst, srcA, srcB, srcC, tid8, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TexmioPipeRegRegRegRegRegReg extends Definition(
        "TexmioPipeRegRegRegRegRegReg",
        "TEXmio_pipe",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register)
        ),
        Vec("tex_b_urc_", "tex_scr_b_urc_", "tex_scr_urc_", "tex_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe)
            ) => Some(Instruction.TexmioPipeRegRegRegRegRegReg(rd2, dst, srcA, srcB, srcC, uRe, modifiers, guard))
        case _ => None
      }
  }
  object TexmioPipeRegRegRegRegRegRegImm extends Definition(
        "TexmioPipeRegRegRegRegRegRegImm",
        "TEXmio_pipe",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("tex_b_urc_", "tex_scr_b_urc_", "tex_scr_urc_", "tex_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe),
              InstructionOperand.Immediate(tid8)
            ) =>
          Some(Instruction.TexmioPipeRegRegRegRegRegRegImm(rd2, dst, srcA, srcB, srcC, uRe, tid8, modifiers, guard))
        case _ => None
      }
  }
  object TexmioPipeRegRegRegRegRegRegImmImm extends Definition(
        "TexmioPipeRegRegRegRegRegRegImmImm",
        "TEXmio_pipe",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("tex_scr_urc_", "tex_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe),
              InstructionOperand.Immediate(tid8),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TexmioPipeRegRegRegRegRegRegImmImm(
            rd2,
            dst,
            srcA,
            srcB,
            srcC,
            uRe,
            tid8,
            wmsk,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object TextexPipeRegRegRegImmImm extends Definition(
        "TextexPipeRegRegRegImmImm",
        "TEXtex_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("tid", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("TEX", "TEX B")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TextexPipeRegRegRegImmImm(dst, srcA, srcB, tid, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TextexPipeRegRegRegImmImmImm extends Definition(
        "TextexPipeRegRegRegImmImmImm",
        "TEXtex_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("tid", OperandKind.Immediate),
          Slot("smp", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("TEX B legacy style", "TEX legacy style")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(smp),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TextexPipeRegRegRegImmImmImm(dst, srcA, srcB, tid, smp, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TldPredRegRegRegImm extends Definition(
        "TldPredRegRegRegImm",
        "TLD",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("tid", OperandKind.Immediate)
        ),
        Vec("TLD", "TLD_B")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid)
            ) => Some(Instruction.TldPredRegRegRegImm(dstPredicate, dst, srcA, srcB, tid, modifiers, guard))
        case _ => None
      }
  }
  object TldPredRegRegRegImmImm extends Definition(
        "TldPredRegRegRegImmImm",
        "TLD",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("tid", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("TLD", "TLD_B")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TldPredRegRegRegImmImm(dstPredicate, dst, srcA, srcB, tid, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TldPredRegRegRegReg extends Definition(
        "TldPredRegRegRegReg",
        "TLD",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register)
        ),
        Vec("tld_b_noConst_", "tld_scr_b_noConst_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.TldPredRegRegRegReg(pu, rd2, dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object TldPredRegRegRegRegImm extends Definition(
        "TldPredRegRegRegRegImm",
        "TLD",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("tld_b_noConst_", "tld_scr_b_noConst_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TldPredRegRegRegRegImm(pu, rd2, dst, srcA, srcB, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TldPredRegRegRegRegImmImm extends Definition(
        "TldPredRegRegRegRegImmImm",
        "TLD",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("tsPtrIdx", OperandKind.Immediate)
        ),
        Vec("tld_", "tld_b_", "tld_scr_", "tld_scr_b_")
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
          Some(Instruction.TldPredRegRegRegRegImmImm(pu, rd2, dst, srcA, srcB, bank5, tsPtrIdx, modifiers, guard))
        case _ => None
      }
  }
  object TldPredRegRegRegRegImmImmImm extends Definition(
        "TldPredRegRegRegRegImmImmImm",
        "TLD",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("hdrtblbase6", OperandKind.Immediate),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("tld_", "tld_b_", "tld_b_tid_", "tld_scr_", "tld_scr_b_", "tld_scr_b_tid_")
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
            ) => Some(Instruction.TldPredRegRegRegRegImmImmImm(
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
  object TldPredRegRegRegRegImmImmImmImm extends Definition(
        "TldPredRegRegRegRegImmImmImmImm",
        "TLD",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("hdrtblbase6", OperandKind.Immediate),
          Slot("tid8", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("tld_b_tid_", "tld_scr_b_tid_")
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
              InstructionOperand.Immediate(tid8),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TldPredRegRegRegRegImmImmImmImm(
            pu,
            rd2,
            dst,
            srcA,
            srcB,
            bank5,
            hdrtblbase6,
            tid8,
            wmsk,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object TldPredRegRegRegRegReg extends Definition(
        "TldPredRegRegRegRegReg",
        "TLD",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("tld_b_noConst_", "tld_scr_b_noConst_", "tld_scr_urc_", "tld_urc_")
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
            ) => Some(Instruction.TldPredRegRegRegRegReg(pu, rd2, dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object TldPredRegRegRegRegRegImm extends Definition(
        "TldPredRegRegRegRegRegImm",
        "TLD",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("tld_b_noConst_", "tld_scr_b_noConst_", "tld_scr_urc_", "tld_urc_")
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
            ) => Some(Instruction.TldPredRegRegRegRegRegImm(pu, rd2, dst, srcA, srcB, srcC, tid8, modifiers, guard))
        case _ => None
      }
  }
  object TldPredRegRegRegRegRegImmImm extends Definition(
        "TldPredRegRegRegRegRegImmImm",
        "TLD",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("tld_scr_urc_", "tld_urc_")
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
              InstructionOperand.Immediate(tid8),
              InstructionOperand.Immediate(wmsk)
            ) =>
          Some(Instruction.TldPredRegRegRegRegRegImmImm(pu, rd2, dst, srcA, srcB, srcC, tid8, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TldPredRegRegRegRegRegReg extends Definition(
        "TldPredRegRegRegRegRegReg",
        "TLD",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register)
        ),
        Vec("tld_b_urc_", "tld_scr_b_urc_", "tld_scr_urc_", "tld_urc_")
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
              InstructionOperand.Register(uRe)
            ) => Some(Instruction.TldPredRegRegRegRegRegReg(pu, rd2, dst, srcA, srcB, srcC, uRe, modifiers, guard))
        case _ => None
      }
  }
  object TldPredRegRegRegRegRegRegImm extends Definition(
        "TldPredRegRegRegRegRegRegImm",
        "TLD",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("tld_b_urc_", "tld_scr_b_urc_", "tld_scr_urc_", "tld_urc_")
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
              InstructionOperand.Register(uRe),
              InstructionOperand.Immediate(tid8)
            ) =>
          Some(Instruction.TldPredRegRegRegRegRegRegImm(pu, rd2, dst, srcA, srcB, srcC, uRe, tid8, modifiers, guard))
        case _ => None
      }
  }
  object TldPredRegRegRegRegRegRegImmImm extends Definition(
        "TldPredRegRegRegRegRegRegImmImm",
        "TLD",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("tld_scr_urc_", "tld_urc_")
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
              InstructionOperand.Register(uRe),
              InstructionOperand.Immediate(tid8),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TldPredRegRegRegRegRegRegImmImm(
            pu,
            rd2,
            dst,
            srcA,
            srcB,
            srcC,
            uRe,
            tid8,
            wmsk,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object TldRegRegRegImm extends Definition(
        "TldRegRegRegImm",
        "TLD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("tid", OperandKind.Immediate)
        ),
        Vec("TLD", "TLD_B")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid)
            ) => Some(Instruction.TldRegRegRegImm(dst, srcA, srcB, tid, modifiers, guard))
        case _ => None
      }
  }
  object TldRegRegRegImmImm extends Definition(
        "TldRegRegRegImmImm",
        "TLD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("tid", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("TLD", "TLD B", "TLD_B")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TldRegRegRegImmImm(dst, srcA, srcB, tid, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TldRegRegRegReg extends Definition(
        "TldRegRegRegReg",
        "TLD",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register)
        ),
        Vec("tld_b_noConst_", "tld_scr_b_noConst_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.TldRegRegRegReg(rd2, dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object TldRegRegRegRegImm extends Definition(
        "TldRegRegRegRegImm",
        "TLD",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("tld_b_noConst_", "tld_scr_b_noConst_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TldRegRegRegRegImm(rd2, dst, srcA, srcB, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TldRegRegRegRegImmImm extends Definition(
        "TldRegRegRegRegImmImm",
        "TLD",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("tsPtrIdx", OperandKind.Immediate)
        ),
        Vec("tld_", "tld_b_", "tld_scr_", "tld_scr_b_")
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
            ) => Some(Instruction.TldRegRegRegRegImmImm(rd2, dst, srcA, srcB, bank5, tsPtrIdx, modifiers, guard))
        case _ => None
      }
  }
  object TldRegRegRegRegImmImmImm extends Definition(
        "TldRegRegRegRegImmImmImm",
        "TLD",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("hdrtblbase6", OperandKind.Immediate),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("tld_", "tld_b_", "tld_b_tid_", "tld_scr_", "tld_scr_b_", "tld_scr_b_tid_")
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
            ) =>
          Some(Instruction.TldRegRegRegRegImmImmImm(rd2, dst, srcA, srcB, bank5, hdrtblbase6, tid8, modifiers, guard))
        case _ => None
      }
  }
  object TldRegRegRegRegImmImmImmImm extends Definition(
        "TldRegRegRegRegImmImmImmImm",
        "TLD",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("hdrtblbase6", OperandKind.Immediate),
          Slot("tid8", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("tld_b_tid_", "tld_scr_b_tid_")
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
              InstructionOperand.Immediate(tid8),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TldRegRegRegRegImmImmImmImm(
            rd2,
            dst,
            srcA,
            srcB,
            bank5,
            hdrtblbase6,
            tid8,
            wmsk,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object TldRegRegRegRegReg extends Definition(
        "TldRegRegRegRegReg",
        "TLD",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("tld_b_noConst_", "tld_scr_b_noConst_", "tld_scr_urc_", "tld_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.TldRegRegRegRegReg(rd2, dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object TldRegRegRegRegRegImm extends Definition(
        "TldRegRegRegRegRegImm",
        "TLD",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("tld_b_noConst_", "tld_scr_b_noConst_", "tld_scr_urc_", "tld_urc_")
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
            ) => Some(Instruction.TldRegRegRegRegRegImm(rd2, dst, srcA, srcB, srcC, tid8, modifiers, guard))
        case _ => None
      }
  }
  object TldRegRegRegRegRegImmImm extends Definition(
        "TldRegRegRegRegRegImmImm",
        "TLD",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("tld_scr_urc_", "tld_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(tid8),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TldRegRegRegRegRegImmImm(rd2, dst, srcA, srcB, srcC, tid8, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TldRegRegRegRegRegReg extends Definition(
        "TldRegRegRegRegRegReg",
        "TLD",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register)
        ),
        Vec("tld_b_urc_", "tld_scr_b_urc_", "tld_scr_urc_", "tld_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe)
            ) => Some(Instruction.TldRegRegRegRegRegReg(rd2, dst, srcA, srcB, srcC, uRe, modifiers, guard))
        case _ => None
      }
  }
  object TldRegRegRegRegRegRegImm extends Definition(
        "TldRegRegRegRegRegRegImm",
        "TLD",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("tld_b_urc_", "tld_scr_b_urc_", "tld_scr_urc_", "tld_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.TldRegRegRegRegRegRegImm(rd2, dst, srcA, srcB, srcC, uRe, tid8, modifiers, guard))
        case _ => None
      }
  }
  object TldRegRegRegRegRegRegImmImm extends Definition(
        "TldRegRegRegRegRegRegImmImm",
        "TLD",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("tld_scr_urc_", "tld_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe),
              InstructionOperand.Immediate(tid8),
              InstructionOperand.Immediate(wmsk)
            ) =>
          Some(Instruction.TldRegRegRegRegRegRegImmImm(rd2, dst, srcA, srcB, srcC, uRe, tid8, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object Tld4PredRegRegRegImm extends Definition(
        "Tld4PredRegRegRegImm",
        "TLD4",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("tid", OperandKind.Immediate)
        ),
        Vec("TLD4", "TLD4_B")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid)
            ) => Some(Instruction.Tld4PredRegRegRegImm(dstPredicate, dst, srcA, srcB, tid, modifiers, guard))
        case _ => None
      }
  }
  object Tld4PredRegRegRegImmImm extends Definition(
        "Tld4PredRegRegRegImmImm",
        "TLD4",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("tid", OperandKind.Immediate),
          Slot("smp", OperandKind.Immediate)
        ),
        Vec("TLD4", "TLD4_B", "TLD4_B_Legacy_Style", "TLD4_Legacy_Style")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(smp)
            ) => Some(Instruction.Tld4PredRegRegRegImmImm(dstPredicate, dst, srcA, srcB, tid, smp, modifiers, guard))
        case _ => None
      }
  }
  object Tld4PredRegRegRegImmImmImm extends Definition(
        "Tld4PredRegRegRegImmImmImm",
        "TLD4",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("tid", OperandKind.Immediate),
          Slot("smp", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("TLD4_B_Legacy_Style", "TLD4_Legacy_Style")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(smp),
              InstructionOperand.Immediate(wmsk)
            ) =>
          Some(Instruction.Tld4PredRegRegRegImmImmImm(dstPredicate, dst, srcA, srcB, tid, smp, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object Tld4PredRegRegRegReg extends Definition(
        "Tld4PredRegRegRegReg",
        "TLD4",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register)
        ),
        Vec("tld4_b_noConst_", "tld4_scr_b_noConst_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.Tld4PredRegRegRegReg(pu, rd2, dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object Tld4PredRegRegRegRegImm extends Definition(
        "Tld4PredRegRegRegRegImm",
        "TLD4",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("tld4_b_noConst_", "tld4_scr_b_noConst_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.Tld4PredRegRegRegRegImm(pu, rd2, dst, srcA, srcB, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object Tld4PredRegRegRegRegImmImm extends Definition(
        "Tld4PredRegRegRegRegImmImm",
        "TLD4",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("tsPtrIdx", OperandKind.Immediate)
        ),
        Vec("tld4_", "tld4_b_", "tld4_scr_", "tld4_scr_b_")
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
          Some(Instruction.Tld4PredRegRegRegRegImmImm(pu, rd2, dst, srcA, srcB, bank5, tsPtrIdx, modifiers, guard))
        case _ => None
      }
  }
  object Tld4PredRegRegRegRegImmImmImm extends Definition(
        "Tld4PredRegRegRegRegImmImmImm",
        "TLD4",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("hdrtblbase6", OperandKind.Immediate),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("tld4_", "tld4_b_", "tld4_b_tid_", "tld4_scr_", "tld4_scr_b_", "tld4_scr_b_tid_")
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
            ) => Some(Instruction.Tld4PredRegRegRegRegImmImmImm(
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
  object Tld4PredRegRegRegRegImmImmImmImm extends Definition(
        "Tld4PredRegRegRegRegImmImmImmImm",
        "TLD4",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("hdrtblbase6", OperandKind.Immediate),
          Slot("tid8", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("tld4_b_tid_", "tld4_scr_b_tid_")
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
              InstructionOperand.Immediate(tid8),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.Tld4PredRegRegRegRegImmImmImmImm(
            pu,
            rd2,
            dst,
            srcA,
            srcB,
            bank5,
            hdrtblbase6,
            tid8,
            wmsk,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object Tld4PredRegRegRegRegReg extends Definition(
        "Tld4PredRegRegRegRegReg",
        "TLD4",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("tld4_b_noConst_", "tld4_scr_b_noConst_", "tld4_scr_urc_", "tld4_urc_")
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
            ) => Some(Instruction.Tld4PredRegRegRegRegReg(pu, rd2, dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Tld4PredRegRegRegRegRegImm extends Definition(
        "Tld4PredRegRegRegRegRegImm",
        "TLD4",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("tld4_b_noConst_", "tld4_scr_b_noConst_", "tld4_scr_urc_", "tld4_urc_")
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
            ) => Some(Instruction.Tld4PredRegRegRegRegRegImm(pu, rd2, dst, srcA, srcB, srcC, tid8, modifiers, guard))
        case _ => None
      }
  }
  object Tld4PredRegRegRegRegRegImmImm extends Definition(
        "Tld4PredRegRegRegRegRegImmImm",
        "TLD4",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("tld4_scr_urc_", "tld4_urc_")
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
              InstructionOperand.Immediate(tid8),
              InstructionOperand.Immediate(wmsk)
            ) =>
          Some(Instruction.Tld4PredRegRegRegRegRegImmImm(pu, rd2, dst, srcA, srcB, srcC, tid8, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object Tld4PredRegRegRegRegRegReg extends Definition(
        "Tld4PredRegRegRegRegRegReg",
        "TLD4",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register)
        ),
        Vec("tld4_b_urc_", "tld4_scr_b_urc_", "tld4_scr_urc_", "tld4_urc_")
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
              InstructionOperand.Register(uRe)
            ) => Some(Instruction.Tld4PredRegRegRegRegRegReg(pu, rd2, dst, srcA, srcB, srcC, uRe, modifiers, guard))
        case _ => None
      }
  }
  object Tld4PredRegRegRegRegRegRegImm extends Definition(
        "Tld4PredRegRegRegRegRegRegImm",
        "TLD4",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("tld4_b_urc_", "tld4_scr_b_urc_", "tld4_scr_urc_", "tld4_urc_")
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
              InstructionOperand.Register(uRe),
              InstructionOperand.Immediate(tid8)
            ) =>
          Some(Instruction.Tld4PredRegRegRegRegRegRegImm(pu, rd2, dst, srcA, srcB, srcC, uRe, tid8, modifiers, guard))
        case _ => None
      }
  }
  object Tld4PredRegRegRegRegRegRegImmImm extends Definition(
        "Tld4PredRegRegRegRegRegRegImmImm",
        "TLD4",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("tld4_scr_urc_", "tld4_urc_")
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
              InstructionOperand.Register(uRe),
              InstructionOperand.Immediate(tid8),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.Tld4PredRegRegRegRegRegRegImmImm(
            pu,
            rd2,
            dst,
            srcA,
            srcB,
            srcC,
            uRe,
            tid8,
            wmsk,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object Tld4RegRegRegImm extends Definition(
        "Tld4RegRegRegImm",
        "TLD4",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("tid", OperandKind.Immediate)
        ),
        Vec("TLD4", "TLD4_B")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid)
            ) => Some(Instruction.Tld4RegRegRegImm(dst, srcA, srcB, tid, modifiers, guard))
        case _ => None
      }
  }
  object Tld4RegRegRegImmImm extends Definition(
        "Tld4RegRegRegImmImm",
        "TLD4",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("tid", OperandKind.Immediate),
          Slot("smp", OperandKind.Immediate)
        ),
        Vec("TLD4", "TLD4 B", "TLD4_B", "TLD4_B_Legacy_Style", "TLD4_Legacy_Style")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(smp)
            ) => Some(Instruction.Tld4RegRegRegImmImm(dst, srcA, srcB, tid, smp, modifiers, guard))
        case _ => None
      }
  }
  object Tld4RegRegRegImmImmImm extends Definition(
        "Tld4RegRegRegImmImmImm",
        "TLD4",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("tid", OperandKind.Immediate),
          Slot("smp", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("TLD4 B Legacy Style", "TLD4 Legacy Style", "TLD4_B_Legacy_Style", "TLD4_Legacy_Style")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(smp),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.Tld4RegRegRegImmImmImm(dst, srcA, srcB, tid, smp, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object Tld4RegRegRegReg extends Definition(
        "Tld4RegRegRegReg",
        "TLD4",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register)
        ),
        Vec("tld4_b_noConst_", "tld4_scr_b_noConst_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.Tld4RegRegRegReg(rd2, dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object Tld4RegRegRegRegImm extends Definition(
        "Tld4RegRegRegRegImm",
        "TLD4",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("tld4_b_noConst_", "tld4_scr_b_noConst_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.Tld4RegRegRegRegImm(rd2, dst, srcA, srcB, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object Tld4RegRegRegRegImmImm extends Definition(
        "Tld4RegRegRegRegImmImm",
        "TLD4",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("tsPtrIdx", OperandKind.Immediate)
        ),
        Vec("tld4_", "tld4_b_", "tld4_scr_", "tld4_scr_b_")
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
            ) => Some(Instruction.Tld4RegRegRegRegImmImm(rd2, dst, srcA, srcB, bank5, tsPtrIdx, modifiers, guard))
        case _ => None
      }
  }
  object Tld4RegRegRegRegImmImmImm extends Definition(
        "Tld4RegRegRegRegImmImmImm",
        "TLD4",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("hdrtblbase6", OperandKind.Immediate),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("tld4_", "tld4_b_", "tld4_b_tid_", "tld4_scr_", "tld4_scr_b_", "tld4_scr_b_tid_")
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
            ) =>
          Some(Instruction.Tld4RegRegRegRegImmImmImm(rd2, dst, srcA, srcB, bank5, hdrtblbase6, tid8, modifiers, guard))
        case _ => None
      }
  }
  object Tld4RegRegRegRegImmImmImmImm extends Definition(
        "Tld4RegRegRegRegImmImmImmImm",
        "TLD4",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("hdrtblbase6", OperandKind.Immediate),
          Slot("tid8", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("tld4_b_tid_", "tld4_scr_b_tid_")
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
              InstructionOperand.Immediate(tid8),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.Tld4RegRegRegRegImmImmImmImm(
            rd2,
            dst,
            srcA,
            srcB,
            bank5,
            hdrtblbase6,
            tid8,
            wmsk,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object Tld4RegRegRegRegReg extends Definition(
        "Tld4RegRegRegRegReg",
        "TLD4",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("tld4_b_noConst_", "tld4_scr_b_noConst_", "tld4_scr_urc_", "tld4_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Tld4RegRegRegRegReg(rd2, dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Tld4RegRegRegRegRegImm extends Definition(
        "Tld4RegRegRegRegRegImm",
        "TLD4",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("tld4_b_noConst_", "tld4_scr_b_noConst_", "tld4_scr_urc_", "tld4_urc_")
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
            ) => Some(Instruction.Tld4RegRegRegRegRegImm(rd2, dst, srcA, srcB, srcC, tid8, modifiers, guard))
        case _ => None
      }
  }
  object Tld4RegRegRegRegRegImmImm extends Definition(
        "Tld4RegRegRegRegRegImmImm",
        "TLD4",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("tld4_scr_urc_", "tld4_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(tid8),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.Tld4RegRegRegRegRegImmImm(rd2, dst, srcA, srcB, srcC, tid8, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object Tld4RegRegRegRegRegReg extends Definition(
        "Tld4RegRegRegRegRegReg",
        "TLD4",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register)
        ),
        Vec("tld4_b_urc_", "tld4_scr_b_urc_", "tld4_scr_urc_", "tld4_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe)
            ) => Some(Instruction.Tld4RegRegRegRegRegReg(rd2, dst, srcA, srcB, srcC, uRe, modifiers, guard))
        case _ => None
      }
  }
  object Tld4RegRegRegRegRegRegImm extends Definition(
        "Tld4RegRegRegRegRegRegImm",
        "TLD4",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("tld4_b_urc_", "tld4_scr_b_urc_", "tld4_scr_urc_", "tld4_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.Tld4RegRegRegRegRegRegImm(rd2, dst, srcA, srcB, srcC, uRe, tid8, modifiers, guard))
        case _ => None
      }
  }
  object Tld4RegRegRegRegRegRegImmImm extends Definition(
        "Tld4RegRegRegRegRegRegImmImm",
        "TLD4",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("tld4_scr_urc_", "tld4_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe),
              InstructionOperand.Immediate(tid8),
              InstructionOperand.Immediate(wmsk)
            ) =>
          Some(Instruction.Tld4RegRegRegRegRegRegImmImm(rd2, dst, srcA, srcB, srcC, uRe, tid8, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object Tld4sRegRegRegRegImm extends Definition(
        "Tld4sRegRegRegRegImm",
        "TLD4S",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("tid", OperandKind.Immediate)
        ),
        Vec("TLD4S", "TLD4S_F16")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid)
            ) => Some(Instruction.Tld4sRegRegRegRegImm(rd2, dst, srcA, srcB, tid, modifiers, guard))
        case _ => None
      }
  }
  object Tld4smioPipeRegRegRegRegImm extends Definition(
        "Tld4smioPipeRegRegRegRegImm",
        "TLD4Smio_pipe",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("tid", OperandKind.Immediate)
        ),
        Vec("TLD4S", "TLD4S_F16")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid)
            ) => Some(Instruction.Tld4smioPipeRegRegRegRegImm(rd2, dst, srcA, srcB, tid, modifiers, guard))
        case _ => None
      }
  }
  object Tld4mioPipePredRegRegRegImm extends Definition(
        "Tld4mioPipePredRegRegRegImm",
        "TLD4mio_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("tid", OperandKind.Immediate)
        ),
        Vec("TLD4", "TLD4_B")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid)
            ) => Some(Instruction.Tld4mioPipePredRegRegRegImm(dstPredicate, dst, srcA, srcB, tid, modifiers, guard))
        case _ => None
      }
  }
  object Tld4mioPipePredRegRegRegImmImm extends Definition(
        "Tld4mioPipePredRegRegRegImmImm",
        "TLD4mio_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("tid", OperandKind.Immediate),
          Slot("smp", OperandKind.Immediate)
        ),
        Vec("TLD4", "TLD4_B", "TLD4_B_Legacy_Style", "TLD4_Legacy_Style")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(smp)
            ) =>
          Some(Instruction.Tld4mioPipePredRegRegRegImmImm(dstPredicate, dst, srcA, srcB, tid, smp, modifiers, guard))
        case _ => None
      }
  }
  object Tld4mioPipePredRegRegRegImmImmImm extends Definition(
        "Tld4mioPipePredRegRegRegImmImmImm",
        "TLD4mio_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("tid", OperandKind.Immediate),
          Slot("smp", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("TLD4_B_Legacy_Style", "TLD4_Legacy_Style")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(smp),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.Tld4mioPipePredRegRegRegImmImmImm(
            dstPredicate,
            dst,
            srcA,
            srcB,
            tid,
            smp,
            wmsk,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object Tld4mioPipePredRegRegRegReg extends Definition(
        "Tld4mioPipePredRegRegRegReg",
        "TLD4mio_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register)
        ),
        Vec("tld4_b_noConst_", "tld4_scr_b_noConst_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.Tld4mioPipePredRegRegRegReg(pu, rd2, dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object Tld4mioPipePredRegRegRegRegImm extends Definition(
        "Tld4mioPipePredRegRegRegRegImm",
        "TLD4mio_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("tld4_b_noConst_", "tld4_scr_b_noConst_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.Tld4mioPipePredRegRegRegRegImm(pu, rd2, dst, srcA, srcB, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object Tld4mioPipePredRegRegRegRegImmImm extends Definition(
        "Tld4mioPipePredRegRegRegRegImmImm",
        "TLD4mio_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("tsPtrIdx", OperandKind.Immediate)
        ),
        Vec("tld4_", "tld4_b_", "tld4_scr_", "tld4_scr_b_")
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
            ) => Some(Instruction.Tld4mioPipePredRegRegRegRegImmImm(
            pu,
            rd2,
            dst,
            srcA,
            srcB,
            bank5,
            tsPtrIdx,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object Tld4mioPipePredRegRegRegRegImmImmImm extends Definition(
        "Tld4mioPipePredRegRegRegRegImmImmImm",
        "TLD4mio_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("hdrtblbase6", OperandKind.Immediate),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("tld4_", "tld4_b_", "tld4_b_tid_", "tld4_scr_", "tld4_scr_b_", "tld4_scr_b_tid_")
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
            ) => Some(Instruction.Tld4mioPipePredRegRegRegRegImmImmImm(
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
  object Tld4mioPipePredRegRegRegRegImmImmImmImm extends Definition(
        "Tld4mioPipePredRegRegRegRegImmImmImmImm",
        "TLD4mio_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("hdrtblbase6", OperandKind.Immediate),
          Slot("tid8", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("tld4_b_tid_", "tld4_scr_b_tid_")
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
              InstructionOperand.Immediate(tid8),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.Tld4mioPipePredRegRegRegRegImmImmImmImm(
            pu,
            rd2,
            dst,
            srcA,
            srcB,
            bank5,
            hdrtblbase6,
            tid8,
            wmsk,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object Tld4mioPipePredRegRegRegRegReg extends Definition(
        "Tld4mioPipePredRegRegRegRegReg",
        "TLD4mio_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("tld4_b_noConst_", "tld4_scr_b_noConst_", "tld4_scr_urc_", "tld4_urc_")
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
            ) => Some(Instruction.Tld4mioPipePredRegRegRegRegReg(pu, rd2, dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Tld4mioPipePredRegRegRegRegRegImm extends Definition(
        "Tld4mioPipePredRegRegRegRegRegImm",
        "TLD4mio_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("tld4_b_noConst_", "tld4_scr_b_noConst_", "tld4_scr_urc_", "tld4_urc_")
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
          Some(Instruction.Tld4mioPipePredRegRegRegRegRegImm(pu, rd2, dst, srcA, srcB, srcC, tid8, modifiers, guard))
        case _ => None
      }
  }
  object Tld4mioPipePredRegRegRegRegRegImmImm extends Definition(
        "Tld4mioPipePredRegRegRegRegRegImmImm",
        "TLD4mio_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("tld4_scr_urc_", "tld4_urc_")
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
              InstructionOperand.Immediate(tid8),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.Tld4mioPipePredRegRegRegRegRegImmImm(
            pu,
            rd2,
            dst,
            srcA,
            srcB,
            srcC,
            tid8,
            wmsk,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object Tld4mioPipePredRegRegRegRegRegReg extends Definition(
        "Tld4mioPipePredRegRegRegRegRegReg",
        "TLD4mio_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register)
        ),
        Vec("tld4_b_urc_", "tld4_scr_b_urc_", "tld4_scr_urc_", "tld4_urc_")
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
              InstructionOperand.Register(uRe)
            ) =>
          Some(Instruction.Tld4mioPipePredRegRegRegRegRegReg(pu, rd2, dst, srcA, srcB, srcC, uRe, modifiers, guard))
        case _ => None
      }
  }
  object Tld4mioPipePredRegRegRegRegRegRegImm extends Definition(
        "Tld4mioPipePredRegRegRegRegRegRegImm",
        "TLD4mio_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("tld4_b_urc_", "tld4_scr_b_urc_", "tld4_scr_urc_", "tld4_urc_")
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
              InstructionOperand.Register(uRe),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.Tld4mioPipePredRegRegRegRegRegRegImm(
            pu,
            rd2,
            dst,
            srcA,
            srcB,
            srcC,
            uRe,
            tid8,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object Tld4mioPipePredRegRegRegRegRegRegImmImm extends Definition(
        "Tld4mioPipePredRegRegRegRegRegRegImmImm",
        "TLD4mio_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("tld4_scr_urc_", "tld4_urc_")
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
              InstructionOperand.Register(uRe),
              InstructionOperand.Immediate(tid8),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.Tld4mioPipePredRegRegRegRegRegRegImmImm(
            pu,
            rd2,
            dst,
            srcA,
            srcB,
            srcC,
            uRe,
            tid8,
            wmsk,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object Tld4mioPipeRegRegRegImm extends Definition(
        "Tld4mioPipeRegRegRegImm",
        "TLD4mio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("tid", OperandKind.Immediate)
        ),
        Vec("TLD4", "TLD4_B")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid)
            ) => Some(Instruction.Tld4mioPipeRegRegRegImm(dst, srcA, srcB, tid, modifiers, guard))
        case _ => None
      }
  }
  object Tld4mioPipeRegRegRegImmImm extends Definition(
        "Tld4mioPipeRegRegRegImmImm",
        "TLD4mio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("tid", OperandKind.Immediate),
          Slot("smp", OperandKind.Immediate)
        ),
        Vec("TLD4", "TLD4_B", "TLD4_B_Legacy_Style", "TLD4_Legacy_Style")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(smp)
            ) => Some(Instruction.Tld4mioPipeRegRegRegImmImm(dst, srcA, srcB, tid, smp, modifiers, guard))
        case _ => None
      }
  }
  object Tld4mioPipeRegRegRegImmImmImm extends Definition(
        "Tld4mioPipeRegRegRegImmImmImm",
        "TLD4mio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("tid", OperandKind.Immediate),
          Slot("smp", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("TLD4_B_Legacy_Style", "TLD4_Legacy_Style")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(smp),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.Tld4mioPipeRegRegRegImmImmImm(dst, srcA, srcB, tid, smp, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object Tld4mioPipeRegRegRegReg extends Definition(
        "Tld4mioPipeRegRegRegReg",
        "TLD4mio_pipe",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register)
        ),
        Vec("tld4_b_noConst_", "tld4_scr_b_noConst_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.Tld4mioPipeRegRegRegReg(rd2, dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object Tld4mioPipeRegRegRegRegImm extends Definition(
        "Tld4mioPipeRegRegRegRegImm",
        "TLD4mio_pipe",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("tld4_b_noConst_", "tld4_scr_b_noConst_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.Tld4mioPipeRegRegRegRegImm(rd2, dst, srcA, srcB, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object Tld4mioPipeRegRegRegRegImmImm extends Definition(
        "Tld4mioPipeRegRegRegRegImmImm",
        "TLD4mio_pipe",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("tsPtrIdx", OperandKind.Immediate)
        ),
        Vec("tld4_", "tld4_b_", "tld4_scr_", "tld4_scr_b_")
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
            ) =>
          Some(Instruction.Tld4mioPipeRegRegRegRegImmImm(rd2, dst, srcA, srcB, bank5, tsPtrIdx, modifiers, guard))
        case _ => None
      }
  }
  object Tld4mioPipeRegRegRegRegImmImmImm extends Definition(
        "Tld4mioPipeRegRegRegRegImmImmImm",
        "TLD4mio_pipe",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("hdrtblbase6", OperandKind.Immediate),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("tld4_", "tld4_b_", "tld4_b_tid_", "tld4_scr_", "tld4_scr_b_", "tld4_scr_b_tid_")
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
            ) => Some(Instruction.Tld4mioPipeRegRegRegRegImmImmImm(
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
  object Tld4mioPipeRegRegRegRegImmImmImmImm extends Definition(
        "Tld4mioPipeRegRegRegRegImmImmImmImm",
        "TLD4mio_pipe",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("hdrtblbase6", OperandKind.Immediate),
          Slot("tid8", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("tld4_b_tid_", "tld4_scr_b_tid_")
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
              InstructionOperand.Immediate(tid8),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.Tld4mioPipeRegRegRegRegImmImmImmImm(
            rd2,
            dst,
            srcA,
            srcB,
            bank5,
            hdrtblbase6,
            tid8,
            wmsk,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object Tld4mioPipeRegRegRegRegReg extends Definition(
        "Tld4mioPipeRegRegRegRegReg",
        "TLD4mio_pipe",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("tld4_b_noConst_", "tld4_scr_b_noConst_", "tld4_scr_urc_", "tld4_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Tld4mioPipeRegRegRegRegReg(rd2, dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Tld4mioPipeRegRegRegRegRegImm extends Definition(
        "Tld4mioPipeRegRegRegRegRegImm",
        "TLD4mio_pipe",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("tld4_b_noConst_", "tld4_scr_b_noConst_", "tld4_scr_urc_", "tld4_urc_")
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
            ) => Some(Instruction.Tld4mioPipeRegRegRegRegRegImm(rd2, dst, srcA, srcB, srcC, tid8, modifiers, guard))
        case _ => None
      }
  }
  object Tld4mioPipeRegRegRegRegRegImmImm extends Definition(
        "Tld4mioPipeRegRegRegRegRegImmImm",
        "TLD4mio_pipe",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("tld4_scr_urc_", "tld4_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(tid8),
              InstructionOperand.Immediate(wmsk)
            ) =>
          Some(Instruction.Tld4mioPipeRegRegRegRegRegImmImm(rd2, dst, srcA, srcB, srcC, tid8, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object Tld4mioPipeRegRegRegRegRegReg extends Definition(
        "Tld4mioPipeRegRegRegRegRegReg",
        "TLD4mio_pipe",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register)
        ),
        Vec("tld4_b_urc_", "tld4_scr_b_urc_", "tld4_scr_urc_", "tld4_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe)
            ) => Some(Instruction.Tld4mioPipeRegRegRegRegRegReg(rd2, dst, srcA, srcB, srcC, uRe, modifiers, guard))
        case _ => None
      }
  }
  val definitions: Vec[Definition] = Vec(
    SuldPredRegAddressRegImm,
    SuldPredRegAddressRegReg,
    SuldPredRegAddressRegRegImm,
    SuldPredRegAddressRegRegReg,
    SuldRegAddressImm,
    SuldRegAddressImmImm,
    SuldRegAddressImmImmImm,
    SuldRegAddressReg,
    SuldRegAddressRegImm,
    SuldRegAddressRegReg,
    SuldRegAddressRegRegImm,
    SuldRegAddressRegRegReg,
    SuldgaRegAddressConstPred,
    SuldgaRegAddressRegPred,
    SuldgaaguPipeRegAddressConstPred,
    SuldgaaguPipeRegAddressRegPred,
    SuldmioPipePredRegAddressImm,
    SuldmioPipePredRegAddressImmImm,
    SuldmioPipePredRegAddressImmImmImm,
    SuldmioPipePredRegAddressReg,
    SuldmioPipePredRegAddressRegImm,
    SuldmioPipePredRegAddressRegReg,
    SuldmioPipePredRegAddressRegRegImm,
    SuldmioPipePredRegAddressRegRegReg,
    SuldmioPipeRegAddressImm,
    SuldmioPipeRegAddressImmImm,
    SuldmioPipeRegAddressImmImmImm,
    SuldmioPipeRegAddressReg,
    SuldmioPipeRegAddressRegImm,
    SuldmioPipeRegAddressRegReg,
    SuldmioPipeRegAddressRegRegImm,
    SuldmioPipeRegAddressRegRegReg,
    SuqueryPredRegAddressImmImm,
    SuqueryPredRegAddressImmImmImm,
    SuqueryPredRegAddressReg,
    SuqueryPredRegAddressRegImm,
    SuqueryPredRegAddressRegReg,
    SuqueryPredRegAddressRegRegImm,
    SuqueryPredRegAddressRegRegReg,
    SuqueryRegAddressImmImm,
    SuqueryRegAddressImmImmImm,
    SuqueryRegAddressReg,
    SuqueryRegAddressRegImm,
    SuqueryRegAddressRegReg,
    SuqueryRegAddressRegRegImm,
    SuqueryRegAddressRegRegReg,
    SuquerymioPipePredRegAddressImmImm,
    SuquerymioPipePredRegAddressImmImmImm,
    SuquerymioPipePredRegAddressReg,
    SuquerymioPipePredRegAddressRegImm,
    SuquerymioPipePredRegAddressRegReg,
    SuquerymioPipePredRegAddressRegRegImm,
    SuquerymioPipePredRegAddressRegRegReg,
    SuquerymioPipeRegAddressImmImm,
    SuquerymioPipeRegAddressImmImmImm,
    SuquerymioPipeRegAddressReg,
    SuquerymioPipeRegAddressRegImm,
    SuquerymioPipeRegAddressRegReg,
    SuquerymioPipeRegAddressRegRegImm,
    SuquerymioPipeRegAddressRegRegReg,
    SuredAddressRegImm,
    SuredAddressRegImmImm,
    SuredAddressRegImmImmImm,
    SuredAddressRegReg,
    SuredAddressRegRegImm,
    SuredAddressRegRegReg,
    SuredAddressRegRegRegImm,
    SuredAddressRegRegRegReg,
    SuredmioPipeAddressRegImm,
    SuredmioPipeAddressRegImmImm,
    SuredmioPipeAddressRegImmImmImm,
    SuredmioPipeAddressRegReg,
    SuredmioPipeAddressRegRegImm,
    SuredmioPipeAddressRegRegReg,
    SuredmioPipeAddressRegRegRegImm,
    SuredmioPipeAddressRegRegRegReg,
    SustAddressRegImm,
    SustAddressRegImmImm,
    SustAddressRegImmImmImm,
    SustAddressRegReg,
    SustAddressRegRegImm,
    SustAddressRegRegReg,
    SustAddressRegRegRegImm,
    SustAddressRegRegRegReg,
    SustgaAddressConstRegPred,
    SustgaAddressRegRegPred,
    SustgaaguPipeAddressConstRegPred,
    SustgaaguPipeAddressRegRegPred,
    SustmioPipeAddressRegImm,
    SustmioPipeAddressRegImmImm,
    SustmioPipeAddressRegImmImmImm,
    SustmioPipeAddressRegReg,
    SustmioPipeAddressRegRegImm,
    SustmioPipeAddressRegRegReg,
    SustmioPipeAddressRegRegRegImm,
    SustmioPipeAddressRegRegRegReg,
    SyncNoOperands,
    SyncsNoOperands,
    SyncsAddress,
    SyncsAddressReg,
    SyncsPredAddressReg,
    SyncsRegAddress,
    SyncsRegAddressReg,
    SyncsRegAddressRegReg,
    SyncsmioPipeNoOperands,
    SyncsmioPipeAddress,
    SyncsmioPipeAddressReg,
    SyncsmioPipePredAddressReg,
    SyncsmioPipeRegAddress,
    SyncsmioPipeRegAddressReg,
    SyncsmioPipeRegAddressRegReg,
    SyncbruPipeNoOperands,
    TexPredRegRegRegImm,
    TexPredRegRegRegImmImm,
    TexPredRegRegRegImmImmImm,
    TexPredRegRegRegReg,
    TexPredRegRegRegRegImm,
    TexPredRegRegRegRegImmImm,
    TexPredRegRegRegRegImmImmImm,
    TexPredRegRegRegRegImmImmImmImm,
    TexPredRegRegRegRegReg,
    TexPredRegRegRegRegRegImm,
    TexPredRegRegRegRegRegImmImm,
    TexPredRegRegRegRegRegReg,
    TexPredRegRegRegRegRegRegImm,
    TexPredRegRegRegRegRegRegImmImm,
    TexRegRegRegImm,
    TexRegRegRegImmImm,
    TexRegRegRegImmImmImm,
    TexRegRegRegReg,
    TexRegRegRegRegImm,
    TexRegRegRegRegImmImm,
    TexRegRegRegRegImmImmImm,
    TexRegRegRegRegImmImmImmImm,
    TexRegRegRegRegReg,
    TexRegRegRegRegRegImm,
    TexRegRegRegRegRegImmImm,
    TexRegRegRegRegRegReg,
    TexRegRegRegRegRegRegImm,
    TexRegRegRegRegRegRegImmImm,
    TexdepbarImm,
    TexdepbarbruPipeImm,
    TexsRegRegRegRegImm,
    TexsmioPipeRegRegRegRegImm,
    TexmioPipePredRegRegRegImm,
    TexmioPipePredRegRegRegImmImm,
    TexmioPipePredRegRegRegImmImmImm,
    TexmioPipePredRegRegRegReg,
    TexmioPipePredRegRegRegRegImm,
    TexmioPipePredRegRegRegRegImmImm,
    TexmioPipePredRegRegRegRegImmImmImm,
    TexmioPipePredRegRegRegRegImmImmImmImm,
    TexmioPipePredRegRegRegRegReg,
    TexmioPipePredRegRegRegRegRegImm,
    TexmioPipePredRegRegRegRegRegImmImm,
    TexmioPipePredRegRegRegRegRegReg,
    TexmioPipePredRegRegRegRegRegRegImm,
    TexmioPipePredRegRegRegRegRegRegImmImm,
    TexmioPipeRegRegRegImm,
    TexmioPipeRegRegRegImmImm,
    TexmioPipeRegRegRegImmImmImm,
    TexmioPipeRegRegRegReg,
    TexmioPipeRegRegRegRegImm,
    TexmioPipeRegRegRegRegImmImm,
    TexmioPipeRegRegRegRegImmImmImm,
    TexmioPipeRegRegRegRegImmImmImmImm,
    TexmioPipeRegRegRegRegReg,
    TexmioPipeRegRegRegRegRegImm,
    TexmioPipeRegRegRegRegRegImmImm,
    TexmioPipeRegRegRegRegRegReg,
    TexmioPipeRegRegRegRegRegRegImm,
    TexmioPipeRegRegRegRegRegRegImmImm,
    TextexPipeRegRegRegImmImm,
    TextexPipeRegRegRegImmImmImm,
    TldPredRegRegRegImm,
    TldPredRegRegRegImmImm,
    TldPredRegRegRegReg,
    TldPredRegRegRegRegImm,
    TldPredRegRegRegRegImmImm,
    TldPredRegRegRegRegImmImmImm,
    TldPredRegRegRegRegImmImmImmImm,
    TldPredRegRegRegRegReg,
    TldPredRegRegRegRegRegImm,
    TldPredRegRegRegRegRegImmImm,
    TldPredRegRegRegRegRegReg,
    TldPredRegRegRegRegRegRegImm,
    TldPredRegRegRegRegRegRegImmImm,
    TldRegRegRegImm,
    TldRegRegRegImmImm,
    TldRegRegRegReg,
    TldRegRegRegRegImm,
    TldRegRegRegRegImmImm,
    TldRegRegRegRegImmImmImm,
    TldRegRegRegRegImmImmImmImm,
    TldRegRegRegRegReg,
    TldRegRegRegRegRegImm,
    TldRegRegRegRegRegImmImm,
    TldRegRegRegRegRegReg,
    TldRegRegRegRegRegRegImm,
    TldRegRegRegRegRegRegImmImm,
    Tld4PredRegRegRegImm,
    Tld4PredRegRegRegImmImm,
    Tld4PredRegRegRegImmImmImm,
    Tld4PredRegRegRegReg,
    Tld4PredRegRegRegRegImm,
    Tld4PredRegRegRegRegImmImm,
    Tld4PredRegRegRegRegImmImmImm,
    Tld4PredRegRegRegRegImmImmImmImm,
    Tld4PredRegRegRegRegReg,
    Tld4PredRegRegRegRegRegImm,
    Tld4PredRegRegRegRegRegImmImm,
    Tld4PredRegRegRegRegRegReg,
    Tld4PredRegRegRegRegRegRegImm,
    Tld4PredRegRegRegRegRegRegImmImm,
    Tld4RegRegRegImm,
    Tld4RegRegRegImmImm,
    Tld4RegRegRegImmImmImm,
    Tld4RegRegRegReg,
    Tld4RegRegRegRegImm,
    Tld4RegRegRegRegImmImm,
    Tld4RegRegRegRegImmImmImm,
    Tld4RegRegRegRegImmImmImmImm,
    Tld4RegRegRegRegReg,
    Tld4RegRegRegRegRegImm,
    Tld4RegRegRegRegRegImmImm,
    Tld4RegRegRegRegRegReg,
    Tld4RegRegRegRegRegRegImm,
    Tld4RegRegRegRegRegRegImmImm,
    Tld4sRegRegRegRegImm,
    Tld4smioPipeRegRegRegRegImm,
    Tld4mioPipePredRegRegRegImm,
    Tld4mioPipePredRegRegRegImmImm,
    Tld4mioPipePredRegRegRegImmImmImm,
    Tld4mioPipePredRegRegRegReg,
    Tld4mioPipePredRegRegRegRegImm,
    Tld4mioPipePredRegRegRegRegImmImm,
    Tld4mioPipePredRegRegRegRegImmImmImm,
    Tld4mioPipePredRegRegRegRegImmImmImmImm,
    Tld4mioPipePredRegRegRegRegReg,
    Tld4mioPipePredRegRegRegRegRegImm,
    Tld4mioPipePredRegRegRegRegRegImmImm,
    Tld4mioPipePredRegRegRegRegRegReg,
    Tld4mioPipePredRegRegRegRegRegRegImm,
    Tld4mioPipePredRegRegRegRegRegRegImmImm,
    Tld4mioPipeRegRegRegImm,
    Tld4mioPipeRegRegRegImmImm,
    Tld4mioPipeRegRegRegImmImmImm,
    Tld4mioPipeRegRegRegReg,
    Tld4mioPipeRegRegRegRegImm,
    Tld4mioPipeRegRegRegRegImmImm,
    Tld4mioPipeRegRegRegRegImmImmImm,
    Tld4mioPipeRegRegRegRegImmImmImmImm,
    Tld4mioPipeRegRegRegRegReg,
    Tld4mioPipeRegRegRegRegRegImm,
    Tld4mioPipeRegRegRegRegRegImmImm,
    Tld4mioPipeRegRegRegRegRegReg
  )
}
