// Generated SASS instruction definitions and checked operand constructors. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.sass
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import Instruction.*
@javax.annotation.processing.Generated(Array("sbt codegen"))
object InstructionDefinitions12 {
  object TldmioPipePredRegRegRegReg extends Definition(
        "TldmioPipePredRegRegRegReg",
        "TLDmio_pipe",
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
            ) => Some(Instruction.TldmioPipePredRegRegRegReg(pu, rd2, dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object TldmioPipePredRegRegRegRegImm extends Definition(
        "TldmioPipePredRegRegRegRegImm",
        "TLDmio_pipe",
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
            ) => Some(Instruction.TldmioPipePredRegRegRegRegImm(pu, rd2, dst, srcA, srcB, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TldmioPipePredRegRegRegRegImmImm extends Definition(
        "TldmioPipePredRegRegRegRegImmImm",
        "TLDmio_pipe",
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
            ) => Some(Instruction.TldmioPipePredRegRegRegRegImmImm(
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
  object TldmioPipePredRegRegRegRegImmImmImm extends Definition(
        "TldmioPipePredRegRegRegRegImmImmImm",
        "TLDmio_pipe",
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
            ) => Some(Instruction.TldmioPipePredRegRegRegRegImmImmImm(
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
  object TldmioPipePredRegRegRegRegImmImmImmImm extends Definition(
        "TldmioPipePredRegRegRegRegImmImmImmImm",
        "TLDmio_pipe",
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
            ) => Some(Instruction.TldmioPipePredRegRegRegRegImmImmImmImm(
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
  object TldmioPipePredRegRegRegRegReg extends Definition(
        "TldmioPipePredRegRegRegRegReg",
        "TLDmio_pipe",
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
            ) => Some(Instruction.TldmioPipePredRegRegRegRegReg(pu, rd2, dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object TldmioPipePredRegRegRegRegRegImm extends Definition(
        "TldmioPipePredRegRegRegRegRegImm",
        "TLDmio_pipe",
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
            ) =>
          Some(Instruction.TldmioPipePredRegRegRegRegRegImm(pu, rd2, dst, srcA, srcB, srcC, tid8, modifiers, guard))
        case _ => None
      }
  }
  object TldmioPipePredRegRegRegRegRegImmImm extends Definition(
        "TldmioPipePredRegRegRegRegRegImmImm",
        "TLDmio_pipe",
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
            ) => Some(Instruction.TldmioPipePredRegRegRegRegRegImmImm(
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
  object TldmioPipePredRegRegRegRegRegReg extends Definition(
        "TldmioPipePredRegRegRegRegRegReg",
        "TLDmio_pipe",
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
            ) =>
          Some(Instruction.TldmioPipePredRegRegRegRegRegReg(pu, rd2, dst, srcA, srcB, srcC, uRe, modifiers, guard))
        case _ => None
      }
  }
  object TldmioPipePredRegRegRegRegRegRegImm extends Definition(
        "TldmioPipePredRegRegRegRegRegRegImm",
        "TLDmio_pipe",
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
            ) => Some(Instruction.TldmioPipePredRegRegRegRegRegRegImm(
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
  object TldmioPipePredRegRegRegRegRegRegImmImm extends Definition(
        "TldmioPipePredRegRegRegRegRegRegImmImm",
        "TLDmio_pipe",
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
            ) => Some(Instruction.TldmioPipePredRegRegRegRegRegRegImmImm(
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
  object TldmioPipeRegRegRegImm extends Definition(
        "TldmioPipeRegRegRegImm",
        "TLDmio_pipe",
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
            ) => Some(Instruction.TldmioPipeRegRegRegImm(dst, srcA, srcB, tid, modifiers, guard))
        case _ => None
      }
  }
  object TldmioPipeRegRegRegImmImm extends Definition(
        "TldmioPipeRegRegRegImmImm",
        "TLDmio_pipe",
        Vec(
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
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TldmioPipeRegRegRegImmImm(dst, srcA, srcB, tid, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TldmioPipeRegRegRegReg extends Definition(
        "TldmioPipeRegRegRegReg",
        "TLDmio_pipe",
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
            ) => Some(Instruction.TldmioPipeRegRegRegReg(rd2, dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object TldmioPipeRegRegRegRegImm extends Definition(
        "TldmioPipeRegRegRegRegImm",
        "TLDmio_pipe",
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
            ) => Some(Instruction.TldmioPipeRegRegRegRegImm(rd2, dst, srcA, srcB, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TldmioPipeRegRegRegRegImmImm extends Definition(
        "TldmioPipeRegRegRegRegImmImm",
        "TLDmio_pipe",
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
            ) => Some(Instruction.TldmioPipeRegRegRegRegImmImm(rd2, dst, srcA, srcB, bank5, tsPtrIdx, modifiers, guard))
        case _ => None
      }
  }
  object TldmioPipeRegRegRegRegImmImmImm extends Definition(
        "TldmioPipeRegRegRegRegImmImmImm",
        "TLDmio_pipe",
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
            ) => Some(Instruction.TldmioPipeRegRegRegRegImmImmImm(
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
  object TldmioPipeRegRegRegRegImmImmImmImm extends Definition(
        "TldmioPipeRegRegRegRegImmImmImmImm",
        "TLDmio_pipe",
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
            ) => Some(Instruction.TldmioPipeRegRegRegRegImmImmImmImm(
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
  object TldmioPipeRegRegRegRegReg extends Definition(
        "TldmioPipeRegRegRegRegReg",
        "TLDmio_pipe",
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
            ) => Some(Instruction.TldmioPipeRegRegRegRegReg(rd2, dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object TldmioPipeRegRegRegRegRegImm extends Definition(
        "TldmioPipeRegRegRegRegRegImm",
        "TLDmio_pipe",
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
            ) => Some(Instruction.TldmioPipeRegRegRegRegRegImm(rd2, dst, srcA, srcB, srcC, tid8, modifiers, guard))
        case _ => None
      }
  }
  object TldmioPipeRegRegRegRegRegImmImm extends Definition(
        "TldmioPipeRegRegRegRegRegImmImm",
        "TLDmio_pipe",
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
            ) =>
          Some(Instruction.TldmioPipeRegRegRegRegRegImmImm(rd2, dst, srcA, srcB, srcC, tid8, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TldmioPipeRegRegRegRegRegReg extends Definition(
        "TldmioPipeRegRegRegRegRegReg",
        "TLDmio_pipe",
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
            ) => Some(Instruction.TldmioPipeRegRegRegRegRegReg(rd2, dst, srcA, srcB, srcC, uRe, modifiers, guard))
        case _ => None
      }
  }
  object TldmioPipeRegRegRegRegRegRegImm extends Definition(
        "TldmioPipeRegRegRegRegRegRegImm",
        "TLDmio_pipe",
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
            ) =>
          Some(Instruction.TldmioPipeRegRegRegRegRegRegImm(rd2, dst, srcA, srcB, srcC, uRe, tid8, modifiers, guard))
        case _ => None
      }
  }
  object TldmioPipeRegRegRegRegRegRegImmImm extends Definition(
        "TldmioPipeRegRegRegRegRegRegImmImm",
        "TLDmio_pipe",
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
            ) => Some(Instruction.TldmioPipeRegRegRegRegRegRegImmImm(
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
  object TldtexPipeRegRegRegImmImm extends Definition(
        "TldtexPipeRegRegRegImmImm",
        "TLDtex_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("tid", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("TLD", "TLD B")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TldtexPipeRegRegRegImmImm(dst, srcA, srcB, tid, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TmmlRegRegImm extends Definition(
        "TmmlRegRegImm",
        "TMML",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("tid", OperandKind.Immediate)),
        Vec("TMML")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(tid)
            ) => Some(Instruction.TmmlRegRegImm(dst, srcA, tid, modifiers, guard))
        case _ => None
      }
  }
  object TmmlRegRegImmImm extends Definition(
        "TmmlRegRegImmImm",
        "TMML",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("tid", OperandKind.Immediate),
          Slot("smp", OperandKind.Immediate)
        ),
        Vec("TMML", "TMML_Legacy_Style")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(smp)
            ) => Some(Instruction.TmmlRegRegImmImm(dst, srcA, tid, smp, modifiers, guard))
        case _ => None
      }
  }
  object TmmlRegRegImmImmImm extends Definition(
        "TmmlRegRegImmImmImm",
        "TMML",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("tid", OperandKind.Immediate),
          Slot("smp", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("TMML Legacy Style", "TMML_Legacy_Style")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(smp),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TmmlRegRegImmImmImm(dst, srcA, tid, smp, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TmmlRegRegRegImm extends Definition(
        "TmmlRegRegRegImm",
        "TMML",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("tid", OperandKind.Immediate)
        ),
        Vec("TMML", "TMML_B")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid)
            ) => Some(Instruction.TmmlRegRegRegImm(dst, srcA, srcB, tid, modifiers, guard))
        case _ => None
      }
  }
  object TmmlRegRegRegImmImm extends Definition(
        "TmmlRegRegRegImmImm",
        "TMML",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("tid", OperandKind.Immediate),
          Slot("smp", OperandKind.Immediate)
        ),
        Vec("TMML", "TMML.B", "TMML_B", "TMML_B_Legacy_Style", "TMML_Legacy_Style")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(smp)
            ) => Some(Instruction.TmmlRegRegRegImmImm(dst, srcA, srcB, tid, smp, modifiers, guard))
        case _ => None
      }
  }
  object TmmlRegRegRegImmImmImm extends Definition(
        "TmmlRegRegRegImmImmImm",
        "TMML",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("tid", OperandKind.Immediate),
          Slot("smp", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("TMML Legacy Style", "TMML.B Legacy Style", "TMML_B_Legacy_Style", "TMML_Legacy_Style")
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
            ) => Some(Instruction.TmmlRegRegRegImmImmImm(dst, srcA, srcB, tid, smp, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TmmlRegRegRegReg extends Definition(
        "TmmlRegRegRegReg",
        "TMML",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register)
        ),
        Vec("tmml_b_noConst_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.TmmlRegRegRegReg(rd2, dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object TmmlRegRegRegRegImm extends Definition(
        "TmmlRegRegRegRegImm",
        "TMML",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("tmml_b_noConst_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TmmlRegRegRegRegImm(rd2, dst, srcA, srcB, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TmmlRegRegRegRegImmImm extends Definition(
        "TmmlRegRegRegRegImmImm",
        "TMML",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("tsPtrIdx", OperandKind.Immediate)
        ),
        Vec("tmml_", "tmml_b_")
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
            ) => Some(Instruction.TmmlRegRegRegRegImmImm(rd2, dst, srcA, srcB, bank5, tsPtrIdx, modifiers, guard))
        case _ => None
      }
  }
  object TmmlRegRegRegRegImmImmImm extends Definition(
        "TmmlRegRegRegRegImmImmImm",
        "TMML",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("hdrtblbase6", OperandKind.Immediate),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("tmml_", "tmml_b_", "tmml_b_tid_")
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
          Some(Instruction.TmmlRegRegRegRegImmImmImm(rd2, dst, srcA, srcB, bank5, hdrtblbase6, tid8, modifiers, guard))
        case _ => None
      }
  }
  object TmmlRegRegRegRegImmImmImmImm extends Definition(
        "TmmlRegRegRegRegImmImmImmImm",
        "TMML",
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
        Vec("tmml_b_tid_")
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
            ) => Some(Instruction.TmmlRegRegRegRegImmImmImmImm(
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
  object TmmlRegRegRegRegReg extends Definition(
        "TmmlRegRegRegRegReg",
        "TMML",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("tmml_b_urc_", "tmml_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.TmmlRegRegRegRegReg(rd2, dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object TmmlRegRegRegRegRegImm extends Definition(
        "TmmlRegRegRegRegRegImm",
        "TMML",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("tmml_b_urc_", "tmml_urc_")
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
            ) => Some(Instruction.TmmlRegRegRegRegRegImm(rd2, dst, srcA, srcB, srcC, tid8, modifiers, guard))
        case _ => None
      }
  }
  object TmmlRegRegRegRegRegImmImm extends Definition(
        "TmmlRegRegRegRegRegImmImm",
        "TMML",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("tmml_urc_")
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
            ) => Some(Instruction.TmmlRegRegRegRegRegImmImm(rd2, dst, srcA, srcB, srcC, tid8, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TmmlmioPipeRegRegImm extends Definition(
        "TmmlmioPipeRegRegImm",
        "TMMLmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("tid", OperandKind.Immediate)),
        Vec("TMML")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(tid)
            ) => Some(Instruction.TmmlmioPipeRegRegImm(dst, srcA, tid, modifiers, guard))
        case _ => None
      }
  }
  object TmmlmioPipeRegRegImmImm extends Definition(
        "TmmlmioPipeRegRegImmImm",
        "TMMLmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("tid", OperandKind.Immediate),
          Slot("smp", OperandKind.Immediate)
        ),
        Vec("TMML", "TMML_Legacy_Style")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(smp)
            ) => Some(Instruction.TmmlmioPipeRegRegImmImm(dst, srcA, tid, smp, modifiers, guard))
        case _ => None
      }
  }
  object TmmlmioPipeRegRegImmImmImm extends Definition(
        "TmmlmioPipeRegRegImmImmImm",
        "TMMLmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("tid", OperandKind.Immediate),
          Slot("smp", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("TMML_Legacy_Style")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(smp),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TmmlmioPipeRegRegImmImmImm(dst, srcA, tid, smp, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TmmlmioPipeRegRegRegImm extends Definition(
        "TmmlmioPipeRegRegRegImm",
        "TMMLmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("tid", OperandKind.Immediate)
        ),
        Vec("TMML", "TMML_B")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid)
            ) => Some(Instruction.TmmlmioPipeRegRegRegImm(dst, srcA, srcB, tid, modifiers, guard))
        case _ => None
      }
  }
  object TmmlmioPipeRegRegRegImmImm extends Definition(
        "TmmlmioPipeRegRegRegImmImm",
        "TMMLmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("tid", OperandKind.Immediate),
          Slot("smp", OperandKind.Immediate)
        ),
        Vec("TMML", "TMML_B", "TMML_B_Legacy_Style", "TMML_Legacy_Style")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(smp)
            ) => Some(Instruction.TmmlmioPipeRegRegRegImmImm(dst, srcA, srcB, tid, smp, modifiers, guard))
        case _ => None
      }
  }
  object TmmlmioPipeRegRegRegImmImmImm extends Definition(
        "TmmlmioPipeRegRegRegImmImmImm",
        "TMMLmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("tid", OperandKind.Immediate),
          Slot("smp", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("TMML_B_Legacy_Style", "TMML_Legacy_Style")
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
            ) => Some(Instruction.TmmlmioPipeRegRegRegImmImmImm(dst, srcA, srcB, tid, smp, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TmmlmioPipeRegRegRegReg extends Definition(
        "TmmlmioPipeRegRegRegReg",
        "TMMLmio_pipe",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register)
        ),
        Vec("tmml_b_noConst_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.TmmlmioPipeRegRegRegReg(rd2, dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object TmmlmioPipeRegRegRegRegImm extends Definition(
        "TmmlmioPipeRegRegRegRegImm",
        "TMMLmio_pipe",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("tmml_b_noConst_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TmmlmioPipeRegRegRegRegImm(rd2, dst, srcA, srcB, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TmmlmioPipeRegRegRegRegImmImm extends Definition(
        "TmmlmioPipeRegRegRegRegImmImm",
        "TMMLmio_pipe",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("tsPtrIdx", OperandKind.Immediate)
        ),
        Vec("tmml_", "tmml_b_")
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
          Some(Instruction.TmmlmioPipeRegRegRegRegImmImm(rd2, dst, srcA, srcB, bank5, tsPtrIdx, modifiers, guard))
        case _ => None
      }
  }
  object TmmlmioPipeRegRegRegRegImmImmImm extends Definition(
        "TmmlmioPipeRegRegRegRegImmImmImm",
        "TMMLmio_pipe",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("hdrtblbase6", OperandKind.Immediate),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("tmml_", "tmml_b_", "tmml_b_tid_")
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
            ) => Some(Instruction.TmmlmioPipeRegRegRegRegImmImmImm(
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
  object TmmlmioPipeRegRegRegRegImmImmImmImm extends Definition(
        "TmmlmioPipeRegRegRegRegImmImmImmImm",
        "TMMLmio_pipe",
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
        Vec("tmml_b_tid_")
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
            ) => Some(Instruction.TmmlmioPipeRegRegRegRegImmImmImmImm(
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
  object TmmlmioPipeRegRegRegRegReg extends Definition(
        "TmmlmioPipeRegRegRegRegReg",
        "TMMLmio_pipe",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("tmml_b_urc_", "tmml_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.TmmlmioPipeRegRegRegRegReg(rd2, dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object TmmlmioPipeRegRegRegRegRegImm extends Definition(
        "TmmlmioPipeRegRegRegRegRegImm",
        "TMMLmio_pipe",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("tmml_b_urc_", "tmml_urc_")
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
            ) => Some(Instruction.TmmlmioPipeRegRegRegRegRegImm(rd2, dst, srcA, srcB, srcC, tid8, modifiers, guard))
        case _ => None
      }
  }
  object TmmlmioPipeRegRegRegRegRegImmImm extends Definition(
        "TmmlmioPipeRegRegRegRegRegImmImm",
        "TMMLmio_pipe",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("tmml_urc_")
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
          Some(Instruction.TmmlmioPipeRegRegRegRegRegImmImm(rd2, dst, srcA, srcB, srcC, tid8, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TmmltexPipeRegRegImmImm extends Definition(
        "TmmltexPipeRegRegImmImm",
        "TMMLtex_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("tid", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("TMML")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TmmltexPipeRegRegImmImm(dst, srcA, tid, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TmmltexPipeRegRegImmImmImm extends Definition(
        "TmmltexPipeRegRegImmImmImm",
        "TMMLtex_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("tid", OperandKind.Immediate),
          Slot("smp", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("TMML Legacy Style")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(smp),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TmmltexPipeRegRegImmImmImm(dst, srcA, tid, smp, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TmmltexPipeRegRegRegImmImm extends Definition(
        "TmmltexPipeRegRegRegImmImm",
        "TMMLtex_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("tid", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("TMML", "TMML.B")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TmmltexPipeRegRegRegImmImm(dst, srcA, srcB, tid, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TmmltexPipeRegRegRegImmImmImm extends Definition(
        "TmmltexPipeRegRegRegImmImmImm",
        "TMMLtex_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("tid", OperandKind.Immediate),
          Slot("smp", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("TMML Legacy Style", "TMML.B Legacy Style")
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
            ) => Some(Instruction.TmmltexPipeRegRegRegImmImmImm(dst, srcA, srcB, tid, smp, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TxaRegRegImm extends Definition(
        "TxaRegRegImm",
        "TXA",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("tid", OperandKind.Immediate)),
        Vec("TXA")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(tid)
            ) => Some(Instruction.TxaRegRegImm(dst, srcA, tid, modifiers, guard))
        case _ => None
      }
  }
  object TxaRegRegImmImm extends Definition(
        "TxaRegRegImmImm",
        "TXA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("tid", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("TXA")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TxaRegRegImmImm(dst, srcA, tid, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TxaRegRegImmImmImm extends Definition(
        "TxaRegRegImmImmImm",
        "TXA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("tid", OperandKind.Immediate),
          Slot("smp", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("TXA Legacy Style", "TXA_Legacy_Style")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(smp),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TxaRegRegImmImmImm(dst, srcA, tid, smp, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TxamioPipeRegRegImm extends Definition(
        "TxamioPipeRegRegImm",
        "TXAmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("tid", OperandKind.Immediate)),
        Vec("TXA")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(tid)
            ) => Some(Instruction.TxamioPipeRegRegImm(dst, srcA, tid, modifiers, guard))
        case _ => None
      }
  }
  object TxamioPipeRegRegImmImm extends Definition(
        "TxamioPipeRegRegImmImm",
        "TXAmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("tid", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("TXA")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TxamioPipeRegRegImmImm(dst, srcA, tid, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TxamioPipeRegRegImmImmImm extends Definition(
        "TxamioPipeRegRegImmImmImm",
        "TXAmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("tid", OperandKind.Immediate),
          Slot("smp", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("TXA_Legacy_Style")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(smp),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TxamioPipeRegRegImmImmImm(dst, srcA, tid, smp, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TxatexPipeRegRegImmImm extends Definition(
        "TxatexPipeRegRegImmImm",
        "TXAtex_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("tid", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("TXA")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TxatexPipeRegRegImmImm(dst, srcA, tid, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TxatexPipeRegRegImmImmImm extends Definition(
        "TxatexPipeRegRegImmImmImm",
        "TXAtex_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("tid", OperandKind.Immediate),
          Slot("smp", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("TXA Legacy Style")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(smp),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TxatexPipeRegRegImmImmImm(dst, srcA, tid, smp, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TxdPredRegRegRegImm extends Definition(
        "TxdPredRegRegRegImm",
        "TXD",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("tid", OperandKind.Immediate)
        ),
        Vec("TXD", "TXD.B")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid)
            ) => Some(Instruction.TxdPredRegRegRegImm(dstPredicate, dst, srcA, srcB, tid, modifiers, guard))
        case _ => None
      }
  }
  object TxdPredRegRegRegImmImm extends Definition(
        "TxdPredRegRegRegImmImm",
        "TXD",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("tid", OperandKind.Immediate),
          Slot("smp", OperandKind.Immediate)
        ),
        Vec("TXD", "TXD.B", "TXD_B_Legacy_Style", "TXD_Legacy_Style")
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
            ) => Some(Instruction.TxdPredRegRegRegImmImm(dstPredicate, dst, srcA, srcB, tid, smp, modifiers, guard))
        case _ => None
      }
  }
  object TxdPredRegRegRegImmImmImm extends Definition(
        "TxdPredRegRegRegImmImmImm",
        "TXD",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("tid", OperandKind.Immediate),
          Slot("smp", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("TXD_B_Legacy_Style", "TXD_Legacy_Style")
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
          Some(Instruction.TxdPredRegRegRegImmImmImm(dstPredicate, dst, srcA, srcB, tid, smp, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TxdPredRegRegRegReg extends Definition(
        "TxdPredRegRegRegReg",
        "TXD",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register)
        ),
        Vec("txd_b_noConst_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.TxdPredRegRegRegReg(pu, rd2, dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object TxdPredRegRegRegRegImm extends Definition(
        "TxdPredRegRegRegRegImm",
        "TXD",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("txd_b_noConst_")
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
            ) => Some(Instruction.TxdPredRegRegRegRegImm(pu, rd2, dst, srcA, srcB, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TxdPredRegRegRegRegImmImm extends Definition(
        "TxdPredRegRegRegRegImmImm",
        "TXD",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("tsPtrIdx", OperandKind.Immediate)
        ),
        Vec("txd_", "txd_b_")
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
          Some(Instruction.TxdPredRegRegRegRegImmImm(pu, rd2, dst, srcA, srcB, bank5, tsPtrIdx, modifiers, guard))
        case _ => None
      }
  }
  object TxdPredRegRegRegRegImmImmImm extends Definition(
        "TxdPredRegRegRegRegImmImmImm",
        "TXD",
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
        Vec("txd_", "txd_b_", "txd_b_tid_")
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
            ) => Some(Instruction.TxdPredRegRegRegRegImmImmImm(
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
  object TxdPredRegRegRegRegImmImmImmImm extends Definition(
        "TxdPredRegRegRegRegImmImmImmImm",
        "TXD",
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
        Vec("txd_b_tid_")
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
            ) => Some(Instruction.TxdPredRegRegRegRegImmImmImmImm(
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
  object TxdPredRegRegRegRegReg extends Definition(
        "TxdPredRegRegRegRegReg",
        "TXD",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("txd_b_noConst_", "txd_urc_")
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
            ) => Some(Instruction.TxdPredRegRegRegRegReg(pu, rd2, dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object TxdPredRegRegRegRegRegImm extends Definition(
        "TxdPredRegRegRegRegRegImm",
        "TXD",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("txd_b_noConst_", "txd_urc_")
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
            ) => Some(Instruction.TxdPredRegRegRegRegRegImm(pu, rd2, dst, srcA, srcB, srcC, tid8, modifiers, guard))
        case _ => None
      }
  }
  object TxdPredRegRegRegRegRegImmImm extends Definition(
        "TxdPredRegRegRegRegRegImmImm",
        "TXD",
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
        Vec("txd_urc_")
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
          Some(Instruction.TxdPredRegRegRegRegRegImmImm(pu, rd2, dst, srcA, srcB, srcC, tid8, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TxdPredRegRegRegRegRegReg extends Definition(
        "TxdPredRegRegRegRegRegReg",
        "TXD",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register)
        ),
        Vec("txd_b_urc_", "txd_urc_")
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
            ) => Some(Instruction.TxdPredRegRegRegRegRegReg(pu, rd2, dst, srcA, srcB, srcC, uRe, modifiers, guard))
        case _ => None
      }
  }
  object TxdPredRegRegRegRegRegRegImm extends Definition(
        "TxdPredRegRegRegRegRegRegImm",
        "TXD",
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
        Vec("txd_b_urc_", "txd_urc_")
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
          Some(Instruction.TxdPredRegRegRegRegRegRegImm(pu, rd2, dst, srcA, srcB, srcC, uRe, tid8, modifiers, guard))
        case _ => None
      }
  }
  object TxdPredRegRegRegRegRegRegImmImm extends Definition(
        "TxdPredRegRegRegRegRegRegImmImm",
        "TXD",
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
        Vec("txd_urc_")
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
            ) => Some(Instruction.TxdPredRegRegRegRegRegRegImmImm(
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
  object TxdRegRegRegImm extends Definition(
        "TxdRegRegRegImm",
        "TXD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("tid", OperandKind.Immediate)
        ),
        Vec("TXD", "TXD.B")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid)
            ) => Some(Instruction.TxdRegRegRegImm(dst, srcA, srcB, tid, modifiers, guard))
        case _ => None
      }
  }
  object TxdRegRegRegImmImm extends Definition(
        "TxdRegRegRegImmImm",
        "TXD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("tid", OperandKind.Immediate),
          Slot("smp", OperandKind.Immediate)
        ),
        Vec("TXD", "TXD.B", "TXD_B_Legacy_Style", "TXD_Legacy_Style")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(smp)
            ) => Some(Instruction.TxdRegRegRegImmImm(dst, srcA, srcB, tid, smp, modifiers, guard))
        case _ => None
      }
  }
  object TxdRegRegRegImmImmImm extends Definition(
        "TxdRegRegRegImmImmImm",
        "TXD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("tid", OperandKind.Immediate),
          Slot("smp", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("TXD Legacy Style", "TXD.B Legacy Style", "TXD_B_Legacy_Style", "TXD_Legacy_Style")
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
            ) => Some(Instruction.TxdRegRegRegImmImmImm(dst, srcA, srcB, tid, smp, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TxdRegRegRegReg extends Definition(
        "TxdRegRegRegReg",
        "TXD",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register)
        ),
        Vec("txd_b_noConst_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.TxdRegRegRegReg(rd2, dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object TxdRegRegRegRegImm extends Definition(
        "TxdRegRegRegRegImm",
        "TXD",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("txd_b_noConst_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TxdRegRegRegRegImm(rd2, dst, srcA, srcB, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TxdRegRegRegRegImmImm extends Definition(
        "TxdRegRegRegRegImmImm",
        "TXD",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("tsPtrIdx", OperandKind.Immediate)
        ),
        Vec("txd_", "txd_b_")
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
            ) => Some(Instruction.TxdRegRegRegRegImmImm(rd2, dst, srcA, srcB, bank5, tsPtrIdx, modifiers, guard))
        case _ => None
      }
  }
  object TxdRegRegRegRegImmImmImm extends Definition(
        "TxdRegRegRegRegImmImmImm",
        "TXD",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("hdrtblbase6", OperandKind.Immediate),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("txd_", "txd_b_", "txd_b_tid_")
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
          Some(Instruction.TxdRegRegRegRegImmImmImm(rd2, dst, srcA, srcB, bank5, hdrtblbase6, tid8, modifiers, guard))
        case _ => None
      }
  }
  object TxdRegRegRegRegImmImmImmImm extends Definition(
        "TxdRegRegRegRegImmImmImmImm",
        "TXD",
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
        Vec("txd_b_tid_")
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
            ) => Some(Instruction.TxdRegRegRegRegImmImmImmImm(
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
  object TxdRegRegRegRegReg extends Definition(
        "TxdRegRegRegRegReg",
        "TXD",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("txd_b_noConst_", "txd_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.TxdRegRegRegRegReg(rd2, dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object TxdRegRegRegRegRegImm extends Definition(
        "TxdRegRegRegRegRegImm",
        "TXD",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("txd_b_noConst_", "txd_urc_")
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
            ) => Some(Instruction.TxdRegRegRegRegRegImm(rd2, dst, srcA, srcB, srcC, tid8, modifiers, guard))
        case _ => None
      }
  }
  object TxdRegRegRegRegRegImmImm extends Definition(
        "TxdRegRegRegRegRegImmImm",
        "TXD",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("txd_urc_")
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
            ) => Some(Instruction.TxdRegRegRegRegRegImmImm(rd2, dst, srcA, srcB, srcC, tid8, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TxdRegRegRegRegRegReg extends Definition(
        "TxdRegRegRegRegRegReg",
        "TXD",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register)
        ),
        Vec("txd_b_urc_", "txd_urc_")
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
            ) => Some(Instruction.TxdRegRegRegRegRegReg(rd2, dst, srcA, srcB, srcC, uRe, modifiers, guard))
        case _ => None
      }
  }
  object TxdRegRegRegRegRegRegImm extends Definition(
        "TxdRegRegRegRegRegRegImm",
        "TXD",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("txd_b_urc_", "txd_urc_")
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
            ) => Some(Instruction.TxdRegRegRegRegRegRegImm(rd2, dst, srcA, srcB, srcC, uRe, tid8, modifiers, guard))
        case _ => None
      }
  }
  object TxdRegRegRegRegRegRegImmImm extends Definition(
        "TxdRegRegRegRegRegRegImmImm",
        "TXD",
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
        Vec("txd_urc_")
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
          Some(Instruction.TxdRegRegRegRegRegRegImmImm(rd2, dst, srcA, srcB, srcC, uRe, tid8, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TxdmioPipePredRegRegRegImm extends Definition(
        "TxdmioPipePredRegRegRegImm",
        "TXDmio_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("tid", OperandKind.Immediate)
        ),
        Vec("TXD", "TXD.B")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid)
            ) => Some(Instruction.TxdmioPipePredRegRegRegImm(dstPredicate, dst, srcA, srcB, tid, modifiers, guard))
        case _ => None
      }
  }
  object TxdmioPipePredRegRegRegImmImm extends Definition(
        "TxdmioPipePredRegRegRegImmImm",
        "TXDmio_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("tid", OperandKind.Immediate),
          Slot("smp", OperandKind.Immediate)
        ),
        Vec("TXD", "TXD.B", "TXD_B_Legacy_Style", "TXD_Legacy_Style")
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
          Some(Instruction.TxdmioPipePredRegRegRegImmImm(dstPredicate, dst, srcA, srcB, tid, smp, modifiers, guard))
        case _ => None
      }
  }
  object TxdmioPipePredRegRegRegImmImmImm extends Definition(
        "TxdmioPipePredRegRegRegImmImmImm",
        "TXDmio_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("tid", OperandKind.Immediate),
          Slot("smp", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("TXD_B_Legacy_Style", "TXD_Legacy_Style")
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
            ) => Some(Instruction.TxdmioPipePredRegRegRegImmImmImm(
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
  object TxdmioPipePredRegRegRegReg extends Definition(
        "TxdmioPipePredRegRegRegReg",
        "TXDmio_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register)
        ),
        Vec("txd_b_noConst_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.TxdmioPipePredRegRegRegReg(pu, rd2, dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object TxdmioPipePredRegRegRegRegImm extends Definition(
        "TxdmioPipePredRegRegRegRegImm",
        "TXDmio_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("txd_b_noConst_")
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
            ) => Some(Instruction.TxdmioPipePredRegRegRegRegImm(pu, rd2, dst, srcA, srcB, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TxdmioPipePredRegRegRegRegImmImm extends Definition(
        "TxdmioPipePredRegRegRegRegImmImm",
        "TXDmio_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("tsPtrIdx", OperandKind.Immediate)
        ),
        Vec("txd_", "txd_b_")
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
            ) => Some(Instruction.TxdmioPipePredRegRegRegRegImmImm(
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
  object TxdmioPipePredRegRegRegRegImmImmImm extends Definition(
        "TxdmioPipePredRegRegRegRegImmImmImm",
        "TXDmio_pipe",
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
        Vec("txd_", "txd_b_", "txd_b_tid_")
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
            ) => Some(Instruction.TxdmioPipePredRegRegRegRegImmImmImm(
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
  object TxdmioPipePredRegRegRegRegImmImmImmImm extends Definition(
        "TxdmioPipePredRegRegRegRegImmImmImmImm",
        "TXDmio_pipe",
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
        Vec("txd_b_tid_")
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
            ) => Some(Instruction.TxdmioPipePredRegRegRegRegImmImmImmImm(
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
  object TxdmioPipePredRegRegRegRegReg extends Definition(
        "TxdmioPipePredRegRegRegRegReg",
        "TXDmio_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("txd_b_noConst_", "txd_urc_")
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
            ) => Some(Instruction.TxdmioPipePredRegRegRegRegReg(pu, rd2, dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object TxdmioPipePredRegRegRegRegRegImm extends Definition(
        "TxdmioPipePredRegRegRegRegRegImm",
        "TXDmio_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("txd_b_noConst_", "txd_urc_")
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
          Some(Instruction.TxdmioPipePredRegRegRegRegRegImm(pu, rd2, dst, srcA, srcB, srcC, tid8, modifiers, guard))
        case _ => None
      }
  }
  object TxdmioPipePredRegRegRegRegRegImmImm extends Definition(
        "TxdmioPipePredRegRegRegRegRegImmImm",
        "TXDmio_pipe",
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
        Vec("txd_urc_")
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
            ) => Some(Instruction.TxdmioPipePredRegRegRegRegRegImmImm(
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
  object TxdmioPipePredRegRegRegRegRegReg extends Definition(
        "TxdmioPipePredRegRegRegRegRegReg",
        "TXDmio_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register)
        ),
        Vec("txd_b_urc_", "txd_urc_")
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
          Some(Instruction.TxdmioPipePredRegRegRegRegRegReg(pu, rd2, dst, srcA, srcB, srcC, uRe, modifiers, guard))
        case _ => None
      }
  }
  object TxdmioPipePredRegRegRegRegRegRegImm extends Definition(
        "TxdmioPipePredRegRegRegRegRegRegImm",
        "TXDmio_pipe",
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
        Vec("txd_b_urc_", "txd_urc_")
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
            ) => Some(Instruction.TxdmioPipePredRegRegRegRegRegRegImm(
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
  object TxdmioPipePredRegRegRegRegRegRegImmImm extends Definition(
        "TxdmioPipePredRegRegRegRegRegRegImmImm",
        "TXDmio_pipe",
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
        Vec("txd_urc_")
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
            ) => Some(Instruction.TxdmioPipePredRegRegRegRegRegRegImmImm(
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
  object TxdmioPipeRegRegRegImm extends Definition(
        "TxdmioPipeRegRegRegImm",
        "TXDmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("tid", OperandKind.Immediate)
        ),
        Vec("TXD", "TXD.B")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid)
            ) => Some(Instruction.TxdmioPipeRegRegRegImm(dst, srcA, srcB, tid, modifiers, guard))
        case _ => None
      }
  }
  object TxdmioPipeRegRegRegImmImm extends Definition(
        "TxdmioPipeRegRegRegImmImm",
        "TXDmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("tid", OperandKind.Immediate),
          Slot("smp", OperandKind.Immediate)
        ),
        Vec("TXD", "TXD.B", "TXD_B_Legacy_Style", "TXD_Legacy_Style")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(smp)
            ) => Some(Instruction.TxdmioPipeRegRegRegImmImm(dst, srcA, srcB, tid, smp, modifiers, guard))
        case _ => None
      }
  }
  object TxdmioPipeRegRegRegImmImmImm extends Definition(
        "TxdmioPipeRegRegRegImmImmImm",
        "TXDmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("tid", OperandKind.Immediate),
          Slot("smp", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("TXD_B_Legacy_Style", "TXD_Legacy_Style")
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
            ) => Some(Instruction.TxdmioPipeRegRegRegImmImmImm(dst, srcA, srcB, tid, smp, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TxdmioPipeRegRegRegReg extends Definition(
        "TxdmioPipeRegRegRegReg",
        "TXDmio_pipe",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register)
        ),
        Vec("txd_b_noConst_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.TxdmioPipeRegRegRegReg(rd2, dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object TxdmioPipeRegRegRegRegImm extends Definition(
        "TxdmioPipeRegRegRegRegImm",
        "TXDmio_pipe",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("txd_b_noConst_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TxdmioPipeRegRegRegRegImm(rd2, dst, srcA, srcB, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TxdmioPipeRegRegRegRegImmImm extends Definition(
        "TxdmioPipeRegRegRegRegImmImm",
        "TXDmio_pipe",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("tsPtrIdx", OperandKind.Immediate)
        ),
        Vec("txd_", "txd_b_")
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
            ) => Some(Instruction.TxdmioPipeRegRegRegRegImmImm(rd2, dst, srcA, srcB, bank5, tsPtrIdx, modifiers, guard))
        case _ => None
      }
  }
  object TxdmioPipeRegRegRegRegImmImmImm extends Definition(
        "TxdmioPipeRegRegRegRegImmImmImm",
        "TXDmio_pipe",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("hdrtblbase6", OperandKind.Immediate),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("txd_", "txd_b_", "txd_b_tid_")
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
            ) => Some(Instruction.TxdmioPipeRegRegRegRegImmImmImm(
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
  object TxdmioPipeRegRegRegRegImmImmImmImm extends Definition(
        "TxdmioPipeRegRegRegRegImmImmImmImm",
        "TXDmio_pipe",
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
        Vec("txd_b_tid_")
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
            ) => Some(Instruction.TxdmioPipeRegRegRegRegImmImmImmImm(
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
  object TxdmioPipeRegRegRegRegReg extends Definition(
        "TxdmioPipeRegRegRegRegReg",
        "TXDmio_pipe",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("txd_b_noConst_", "txd_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.TxdmioPipeRegRegRegRegReg(rd2, dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object TxdmioPipeRegRegRegRegRegImm extends Definition(
        "TxdmioPipeRegRegRegRegRegImm",
        "TXDmio_pipe",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("txd_b_noConst_", "txd_urc_")
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
            ) => Some(Instruction.TxdmioPipeRegRegRegRegRegImm(rd2, dst, srcA, srcB, srcC, tid8, modifiers, guard))
        case _ => None
      }
  }
  object TxdmioPipeRegRegRegRegRegImmImm extends Definition(
        "TxdmioPipeRegRegRegRegRegImmImm",
        "TXDmio_pipe",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("txd_urc_")
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
          Some(Instruction.TxdmioPipeRegRegRegRegRegImmImm(rd2, dst, srcA, srcB, srcC, tid8, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TxdmioPipeRegRegRegRegRegReg extends Definition(
        "TxdmioPipeRegRegRegRegRegReg",
        "TXDmio_pipe",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register)
        ),
        Vec("txd_b_urc_", "txd_urc_")
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
            ) => Some(Instruction.TxdmioPipeRegRegRegRegRegReg(rd2, dst, srcA, srcB, srcC, uRe, modifiers, guard))
        case _ => None
      }
  }
  object TxdmioPipeRegRegRegRegRegRegImm extends Definition(
        "TxdmioPipeRegRegRegRegRegRegImm",
        "TXDmio_pipe",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("txd_b_urc_", "txd_urc_")
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
          Some(Instruction.TxdmioPipeRegRegRegRegRegRegImm(rd2, dst, srcA, srcB, srcC, uRe, tid8, modifiers, guard))
        case _ => None
      }
  }
  object TxdmioPipeRegRegRegRegRegRegImmImm extends Definition(
        "TxdmioPipeRegRegRegRegRegRegImmImm",
        "TXDmio_pipe",
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
        Vec("txd_urc_")
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
            ) => Some(Instruction.TxdmioPipeRegRegRegRegRegRegImmImm(
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
  object TxdtexPipeRegRegRegImmImm extends Definition(
        "TxdtexPipeRegRegRegImmImm",
        "TXDtex_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("tid", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("TXD", "TXD.B")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TxdtexPipeRegRegRegImmImm(dst, srcA, srcB, tid, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TxdtexPipeRegRegRegImmImmImm extends Definition(
        "TxdtexPipeRegRegRegImmImmImm",
        "TXDtex_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("tid", OperandKind.Immediate),
          Slot("smp", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("TXD Legacy Style", "TXD.B Legacy Style")
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
            ) => Some(Instruction.TxdtexPipeRegRegRegImmImmImm(dst, srcA, srcB, tid, smp, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TxqRegRegImm extends Definition(
        "TxqRegRegImm",
        "TXQ",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("tid", OperandKind.Immediate)),
        Vec("TXQ", "TXQ_B")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(tid)
            ) => Some(Instruction.TxqRegRegImm(dst, srcA, tid, modifiers, guard))
        case _ => None
      }
  }
  object TxqRegRegImmImm extends Definition(
        "TxqRegRegImmImm",
        "TXQ",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("query", OperandKind.Immediate),
          Slot("tid", OperandKind.Immediate)
        ),
        Vec("TXQ", "TXQ.B", "TXQ_B", "TXQ_B_Imm", "TXQ_Imm")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(query),
              InstructionOperand.Immediate(tid)
            ) => Some(Instruction.TxqRegRegImmImm(dst, srcA, query, tid, modifiers, guard))
        case _ => None
      }
  }
  object TxqRegRegImmImmImm extends Definition(
        "TxqRegRegImmImmImm",
        "TXQ",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("query", OperandKind.Immediate),
          Slot("tid", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec(
          "TXQ Imm",
          "TXQ Legacy Style",
          "TXQ.B Imm",
          "TXQ.B Legacy Style",
          "TXQ_B_Imm",
          "TXQ_B_Legacy_Style",
          "TXQ_Imm",
          "TXQ_Legacy_Style"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(query),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TxqRegRegImmImmImm(dst, srcA, query, tid, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TxqRegRegImmImmImmImm extends Definition(
        "TxqRegRegImmImmImmImm",
        "TXQ",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("query", OperandKind.Immediate),
          Slot("tid", OperandKind.Immediate),
          Slot("smp", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("TXQ Imm Legacy Style", "TXQ.B Imm Legacy Style", "TXQ_B_Imm_Legacy_Style", "TXQ_Imm_Legacy_Style")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(query),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(smp),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TxqRegRegImmImmImmImm(dst, srcA, query, tid, smp, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TxqRegRegRegImm extends Definition(
        "TxqRegRegRegImm",
        "TXQ",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("txq_b_noConst_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TxqRegRegRegImm(rd2, dst, srcA, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TxqRegRegRegImmImmImm extends Definition(
        "TxqRegRegRegImmImmImm",
        "TXQ",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("tsPtrIdx", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("txq_", "txq_b_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(tsPtrIdx),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TxqRegRegRegImmImmImm(rd2, dst, srcA, bank5, tsPtrIdx, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TxqRegRegRegImmImmImmImm extends Definition(
        "TxqRegRegRegImmImmImmImm",
        "TXQ",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("hdrtblbase6", OperandKind.Immediate),
          Slot("tid8", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("txq_b_tid_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(hdrtblbase6),
              InstructionOperand.Immediate(tid8),
              InstructionOperand.Immediate(wmsk)
            ) =>
          Some(Instruction.TxqRegRegRegImmImmImmImm(rd2, dst, srcA, bank5, hdrtblbase6, tid8, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TxqRegRegRegRegImm extends Definition(
        "TxqRegRegRegRegImm",
        "TXQ",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("txq_b_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TxqRegRegRegRegImm(rd2, dst, srcA, srcC, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TxqRegRegRegRegImmImm extends Definition(
        "TxqRegRegRegRegImmImm",
        "TXQ",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("txq_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(tid8),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TxqRegRegRegRegImmImm(rd2, dst, srcA, srcC, tid8, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TxqmioPipeRegRegImm extends Definition(
        "TxqmioPipeRegRegImm",
        "TXQmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("tid", OperandKind.Immediate)),
        Vec("TXQ", "TXQ_B")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(tid)
            ) => Some(Instruction.TxqmioPipeRegRegImm(dst, srcA, tid, modifiers, guard))
        case _ => None
      }
  }
  object TxqmioPipeRegRegImmImm extends Definition(
        "TxqmioPipeRegRegImmImm",
        "TXQmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("query", OperandKind.Immediate),
          Slot("tid", OperandKind.Immediate)
        ),
        Vec("TXQ", "TXQ_B", "TXQ_B_Imm", "TXQ_Imm")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(query),
              InstructionOperand.Immediate(tid)
            ) => Some(Instruction.TxqmioPipeRegRegImmImm(dst, srcA, query, tid, modifiers, guard))
        case _ => None
      }
  }
  object TxqmioPipeRegRegImmImmImm extends Definition(
        "TxqmioPipeRegRegImmImmImm",
        "TXQmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("query", OperandKind.Immediate),
          Slot("tid", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("TXQ_B_Imm", "TXQ_B_Legacy_Style", "TXQ_Imm", "TXQ_Legacy_Style")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(query),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TxqmioPipeRegRegImmImmImm(dst, srcA, query, tid, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TxqmioPipeRegRegImmImmImmImm extends Definition(
        "TxqmioPipeRegRegImmImmImmImm",
        "TXQmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("query", OperandKind.Immediate),
          Slot("tid", OperandKind.Immediate),
          Slot("smp", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("TXQ_B_Imm_Legacy_Style", "TXQ_Imm_Legacy_Style")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(query),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(smp),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TxqmioPipeRegRegImmImmImmImm(dst, srcA, query, tid, smp, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TxqmioPipeRegRegRegImm extends Definition(
        "TxqmioPipeRegRegRegImm",
        "TXQmio_pipe",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("txq_b_noConst_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TxqmioPipeRegRegRegImm(rd2, dst, srcA, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TxqmioPipeRegRegRegImmImmImm extends Definition(
        "TxqmioPipeRegRegRegImmImmImm",
        "TXQmio_pipe",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("tsPtrIdx", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("txq_", "txq_b_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(tsPtrIdx),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TxqmioPipeRegRegRegImmImmImm(rd2, dst, srcA, bank5, tsPtrIdx, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TxqmioPipeRegRegRegImmImmImmImm extends Definition(
        "TxqmioPipeRegRegRegImmImmImmImm",
        "TXQmio_pipe",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("hdrtblbase6", OperandKind.Immediate),
          Slot("tid8", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("txq_b_tid_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(hdrtblbase6),
              InstructionOperand.Immediate(tid8),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TxqmioPipeRegRegRegImmImmImmImm(
            rd2,
            dst,
            srcA,
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
  object TxqmioPipeRegRegRegRegImm extends Definition(
        "TxqmioPipeRegRegRegRegImm",
        "TXQmio_pipe",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("txq_b_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TxqmioPipeRegRegRegRegImm(rd2, dst, srcA, srcC, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TxqmioPipeRegRegRegRegImmImm extends Definition(
        "TxqmioPipeRegRegRegRegImmImm",
        "TXQmio_pipe",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("txq_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(tid8),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TxqmioPipeRegRegRegRegImmImm(rd2, dst, srcA, srcC, tid8, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TxqtexPipeRegRegImmImm extends Definition(
        "TxqtexPipeRegRegImmImm",
        "TXQtex_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("tid", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("TXQ", "TXQ.B")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TxqtexPipeRegRegImmImm(dst, srcA, tid, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TxqtexPipeRegRegImmImmImm extends Definition(
        "TxqtexPipeRegRegImmImmImm",
        "TXQtex_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("query", OperandKind.Immediate),
          Slot("tid", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("TXQ Imm", "TXQ Legacy Style", "TXQ.B Imm", "TXQ.B Legacy Style")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(query),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TxqtexPipeRegRegImmImmImm(dst, srcA, query, tid, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TxqtexPipeRegRegImmImmImmImm extends Definition(
        "TxqtexPipeRegRegImmImmImmImm",
        "TXQtex_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("query", OperandKind.Immediate),
          Slot("tid", OperandKind.Immediate),
          Slot("smp", OperandKind.Immediate),
          Slot("wmsk", OperandKind.Immediate)
        ),
        Vec("TXQ Imm Legacy Style", "TXQ.B Imm Legacy Style")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(query),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(smp),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TxqtexPipeRegRegImmImmImmImm(dst, srcA, query, tid, smp, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object UblkcpAddressAddressReg extends Definition(
        "UblkcpAddressAddressReg",
        "UBLKCP",
        Vec(Slot("srcB", OperandKind.Address), Slot("srcA", OperandKind.Address), Slot("srcC", OperandKind.Register)),
        Vec("ublkcp_", "ublkcp_one_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcB),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.UblkcpAddressAddressReg(srcB, srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object UblkcpAddressAddressRegAddress extends Definition(
        "UblkcpAddressAddressRegAddress",
        "UBLKCP",
        Vec(
          Slot("srcB", OperandKind.Address),
          Slot("srcA", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Address)
        ),
        Vec("ublkcp_desc_", "ublkcp_desc_one_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcB),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Address(uRe)
            ) => Some(Instruction.UblkcpAddressAddressRegAddress(srcB, srcA, srcC, uRe, modifiers, guard))
        case _ => None
      }
  }
  object UblkcpudpPipeAddressAddressReg extends Definition(
        "UblkcpudpPipeAddressAddressReg",
        "UBLKCPudp_pipe",
        Vec(Slot("srcB", OperandKind.Address), Slot("srcA", OperandKind.Address), Slot("srcC", OperandKind.Register)),
        Vec("ublkcp_", "ublkcp_one_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcB),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.UblkcpudpPipeAddressAddressReg(srcB, srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object UblkcpudpPipeAddressAddressRegAddress extends Definition(
        "UblkcpudpPipeAddressAddressRegAddress",
        "UBLKCPudp_pipe",
        Vec(
          Slot("srcB", OperandKind.Address),
          Slot("srcA", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Address)
        ),
        Vec("ublkcp_desc_", "ublkcp_desc_one_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcB),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Address(uRe)
            ) => Some(Instruction.UblkcpudpPipeAddressAddressRegAddress(srcB, srcA, srcC, uRe, modifiers, guard))
        case _ => None
      }
  }
  object UblkpfAddressReg extends Definition(
        "UblkpfAddressReg",
        "UBLKPF",
        Vec(Slot("srcA", OperandKind.Address), Slot("srcC", OperandKind.Register)),
        Vec("ublkpf__UUU", "ublkpf_one__UUU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA), InstructionOperand.Register(srcC)) =>
          Some(Instruction.UblkpfAddressReg(srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object UblkpfAddressRegAddress extends Definition(
        "UblkpfAddressRegAddress",
        "UBLKPF",
        Vec(Slot("srcA", OperandKind.Address), Slot("srcC", OperandKind.Register), Slot("uRe", OperandKind.Address)),
        Vec("ublkpf__UUU_desc", "ublkpf_one__UUU_desc")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Address(uRe)
            ) => Some(Instruction.UblkpfAddressRegAddress(srcA, srcC, uRe, modifiers, guard))
        case _ => None
      }
  }
  object UblkpfudpPipeAddressReg extends Definition(
        "UblkpfudpPipeAddressReg",
        "UBLKPFudp_pipe",
        Vec(Slot("srcA", OperandKind.Address), Slot("srcC", OperandKind.Register)),
        Vec("ublkpf__UUU", "ublkpf_one__UUU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA), InstructionOperand.Register(srcC)) =>
          Some(Instruction.UblkpfudpPipeAddressReg(srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object UblkpfudpPipeAddressRegAddress extends Definition(
        "UblkpfudpPipeAddressRegAddress",
        "UBLKPFudp_pipe",
        Vec(Slot("srcA", OperandKind.Address), Slot("srcC", OperandKind.Register), Slot("uRe", OperandKind.Address)),
        Vec("ublkpf__UUU_desc", "ublkpf_one__UUU_desc")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Address(uRe)
            ) => Some(Instruction.UblkpfudpPipeAddressRegAddress(srcA, srcC, uRe, modifiers, guard))
        case _ => None
      }
  }
  object UblkredAddressAddressReg extends Definition(
        "UblkredAddressAddressReg",
        "UBLKRED",
        Vec(Slot("srcB", OperandKind.Address), Slot("srcA", OperandKind.Address), Slot("srcC", OperandKind.Register)),
        Vec("ublkred_", "ublkred_one_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcB),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.UblkredAddressAddressReg(srcB, srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object UblkredAddressAddressRegAddress extends Definition(
        "UblkredAddressAddressRegAddress",
        "UBLKRED",
        Vec(
          Slot("srcB", OperandKind.Address),
          Slot("srcA", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Address)
        ),
        Vec("ublkred_desc_", "ublkred_desc_one_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcB),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Address(uRe)
            ) => Some(Instruction.UblkredAddressAddressRegAddress(srcB, srcA, srcC, uRe, modifiers, guard))
        case _ => None
      }
  }
  object UblkredudpPipeAddressAddressReg extends Definition(
        "UblkredudpPipeAddressAddressReg",
        "UBLKREDudp_pipe",
        Vec(Slot("srcB", OperandKind.Address), Slot("srcA", OperandKind.Address), Slot("srcC", OperandKind.Register)),
        Vec("ublkred_", "ublkred_one_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcB),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.UblkredudpPipeAddressAddressReg(srcB, srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object UblkredudpPipeAddressAddressRegAddress extends Definition(
        "UblkredudpPipeAddressAddressRegAddress",
        "UBLKREDudp_pipe",
        Vec(
          Slot("srcB", OperandKind.Address),
          Slot("srcA", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Address)
        ),
        Vec("ublkred_desc_", "ublkred_desc_one_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcB),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Address(uRe)
            ) => Some(Instruction.UblkredudpPipeAddressAddressRegAddress(srcB, srcA, srcC, uRe, modifiers, guard))
        case _ => None
      }
  }
  object UbmskRegRegImm extends Definition(
        "UbmskRegRegImm",
        "UBMSK",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("ubmsk__URuIUR_URI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.UbmskRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object UbmskRegRegReg extends Definition(
        "UbmskRegRegReg",
        "UBMSK",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("ubmsk__URURUR_URUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.UbmskRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UbmskudpPipeRegRegImm extends Definition(
        "UbmskudpPipeRegRegImm",
        "UBMSKudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("ubmsk__URuIUR_URI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.UbmskudpPipeRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object UbmskudpPipeRegRegReg extends Definition(
        "UbmskudpPipeRegRegReg",
        "UBMSKudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("ubmsk__URURUR_URUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.UbmskudpPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UbrevRegImm extends Definition(
        "UbrevRegImm",
        "UBREV",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("ubrev__URuIUR_URI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.UbrevRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object UbrevRegReg extends Definition(
        "UbrevRegReg",
        "UBREV",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("ubrev__URURUR_URUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.UbrevRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UbrevudpPipeRegImm extends Definition(
        "UbrevudpPipeRegImm",
        "UBREVudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("ubrev__URuIUR_URI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.UbrevudpPipeRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object UbrevudpPipeRegReg extends Definition(
        "UbrevudpPipeRegReg",
        "UBREVudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("ubrev__URURUR_URUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.UbrevudpPipeRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object Ucgabararv_4e92c8d663b7NoOperands
      extends Definition("Ucgabararv_4e92c8d663b7NoOperands", "UCGABARARV", Vec(), Vec("ucgabararrive_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.Ucgabararv_4e92c8d663b7NoOperands(modifiers, guard))
        case _ => None
      }
  }
  object UcgabararvudpPipe_6660a077ec17NoOperands extends Definition(
        "UcgabararvudpPipe_6660a077ec17NoOperands",
        "UCGABARARVudp_pipe",
        Vec(),
        Vec("ucgabararrive_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.UcgabararvudpPipe_6660a077ec17NoOperands(modifiers, guard))
        case _ => None
      }
  }
  object UcgabargetReg
      extends Definition("UcgabargetReg", "UCGABARGET", Vec(Slot("dst", OperandKind.Register)), Vec("ucgabarget_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst)) => Some(Instruction.UcgabargetReg(dst, modifiers, guard))
        case _ => None
      }
  }
  object UcgabargetudpPipeReg extends Definition(
        "UcgabargetudpPipeReg",
        "UCGABARGETudp_pipe",
        Vec(Slot("dst", OperandKind.Register)),
        Vec("ucgabarget_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst)) => Some(Instruction.UcgabargetudpPipeReg(dst, modifiers, guard))
        case _ => None
      }
  }
  object UcgabarsetReg
      extends Definition("UcgabarsetReg", "UCGABARSET", Vec(Slot("srcB", OperandKind.Register)), Vec("ucgabarset_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcB)) => Some(Instruction.UcgabarsetReg(srcB, modifiers, guard))
        case _ => None
      }
  }
  object UcgabarsetudpPipeReg extends Definition(
        "UcgabarsetudpPipeReg",
        "UCGABARSETudp_pipe",
        Vec(Slot("srcB", OperandKind.Register)),
        Vec("ucgabarset_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcB)) => Some(Instruction.UcgabarsetudpPipeReg(srcB, modifiers, guard))
        case _ => None
      }
  }
  object Ucgabarwait_83a66759c33cNoOperands
      extends Definition("Ucgabarwait_83a66759c33cNoOperands", "UCGABARWAIT", Vec(), Vec("ucgabarwait_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.Ucgabarwait_83a66759c33cNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object UcgabarwaitudpPipe_e2f72cc439b3NoOperands extends Definition(
        "UcgabarwaitudpPipe_e2f72cc439b3NoOperands",
        "UCGABARWAITudp_pipe",
        Vec(),
        Vec("ucgabarwait_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.UcgabarwaitudpPipe_e2f72cc439b3NoOperands(modifiers, guard))
        case _ => None
      }
  }
  object UcgabarArv_2a103ba9b90bNoOperands
      extends Definition("UcgabarArv_2a103ba9b90bNoOperands", "UCGABAR_ARV", Vec(), Vec("ucgabar_arrive_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.UcgabarArv_2a103ba9b90bNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object UcgabarArvudpPipe_1cc606698836NoOperands extends Definition(
        "UcgabarArvudpPipe_1cc606698836NoOperands",
        "UCGABAR_ARVudp_pipe",
        Vec(),
        Vec("ucgabar_arrive_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.UcgabarArvudpPipe_1cc606698836NoOperands(modifiers, guard))
        case _ => None
      }
  }
  object UcgabarWait_c3921c43e665NoOperands
      extends Definition("UcgabarWait_c3921c43e665NoOperands", "UCGABAR_WAIT", Vec(), Vec("ucgabar_wait_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.UcgabarWait_c3921c43e665NoOperands(modifiers, guard))
        case _ => None
      }
  }
  object UcgabarWaitudpPipe_10b5909b104bNoOperands extends Definition(
        "UcgabarWaitudpPipe_10b5909b104bNoOperands",
        "UCGABAR_WAITudp_pipe",
        Vec(),
        Vec("ucgabar_wait_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.UcgabarWaitudpPipe_10b5909b104bNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object UcleaRegPredRegImmImm extends Definition(
        "UcleaRegPredRegImmImm",
        "UCLEA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uPu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("constSizeU04", OperandKind.Immediate)
        ),
        Vec("uclea__Imm")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Immediate(constSizeU04)
            ) => Some(Instruction.UcleaRegPredRegImmImm(dst, uPu, srcA, sb, constSizeU04, modifiers, guard))
        case _ => None
      }
  }
  object UcleaRegPredRegRegImm extends Definition(
        "UcleaRegPredRegRegImm",
        "UCLEA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uPu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("constSizeU04", OperandKind.Immediate)
        ),
        Vec("uclea__URb")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(constSizeU04)
            ) => Some(Instruction.UcleaRegPredRegRegImm(dst, uPu, srcA, srcB, constSizeU04, modifiers, guard))
        case _ => None
      }
  }
  object UcleaudpPipeRegPredRegImmImm extends Definition(
        "UcleaudpPipeRegPredRegImmImm",
        "UCLEAudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uPu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("constSizeU04", OperandKind.Immediate)
        ),
        Vec("uclea__Imm")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Immediate(constSizeU04)
            ) => Some(Instruction.UcleaudpPipeRegPredRegImmImm(dst, uPu, srcA, sb, constSizeU04, modifiers, guard))
        case _ => None
      }
  }
  object UcleaudpPipeRegPredRegRegImm extends Definition(
        "UcleaudpPipeRegPredRegRegImm",
        "UCLEAudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uPu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("constSizeU04", OperandKind.Immediate)
        ),
        Vec("uclea__URb")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(constSizeU04)
            ) => Some(Instruction.UcleaudpPipeRegPredRegRegImm(dst, uPu, srcA, srcB, constSizeU04, modifiers, guard))
        case _ => None
      }
  }
  object Uf2fRegImm extends Definition(
        "Uf2fRegImm",
        "UF2F",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("uf2f_f32_downconvert__URIR_URIR", "uf2f_f32_upconvert__URIR_URIR", "uf2f_f32_upconvert_swap__URIR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.Uf2fRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object Uf2fRegReg extends Definition(
        "Uf2fRegReg",
        "UF2F",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("uf2f_f32_downconvert__UUU_UUU", "uf2f_f32_upconvert__UUU_UUU", "uf2f_f32_upconvert_swap__UUU_UUU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.Uf2fRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object Uf2fpRegImm extends Definition(
        "Uf2fpRegImm",
        "UF2FP",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("uf2fp_8b_upconvert__URIUR", "uf2fp_tf32__URIUR", "uf2fp_tf32_default_src__URIUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.Uf2fpRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object Uf2fpRegImmReg extends Definition(
        "Uf2fpRegImmReg",
        "UF2FP",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.Immediate), Slot("srcC", OperandKind.Register)),
        Vec("uf2fp_f16_to_8b_downconvert__URIUR", "uf2fp_merge_c__URIUR", "uf2fp_merge_c_default_dst_src__URIUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Uf2fpRegImmReg(dst, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Uf2fpRegReg extends Definition(
        "Uf2fpRegReg",
        "UF2FP",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("uf2fp_8b_upconvert__URURUR", "uf2fp_tf32__URURUR", "uf2fp_tf32_default_src__URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.Uf2fpRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object Uf2fpRegRegImm extends Definition(
        "Uf2fpRegRegImm",
        "UF2FP",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec(
          "uf2fp__URIUR",
          "uf2fp_default_dst_src__URIUR",
          "uf2fp_f16_to_8b_downconvert__URURI",
          "uf2fp_merge_c__URURI",
          "uf2fp_merge_c_default_dst_src__URURI"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.Uf2fpRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object Uf2fpRegRegImmReg extends Definition(
        "Uf2fpRegRegImmReg",
        "UF2FP",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("uf2fp_f32_to_8b_downconvert__URIUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Uf2fpRegRegImmReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Uf2fpRegRegReg extends Definition(
        "Uf2fpRegRegReg",
        "UF2FP",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec(
          "uf2fp__URURUR",
          "uf2fp_default_dst_src__URURUR",
          "uf2fp_f16_to_8b_downconvert__URURUR",
          "uf2fp_merge_c__URURUR",
          "uf2fp_merge_c_default_dst_src__URURUR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.Uf2fpRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object Uf2fpRegRegRegImm extends Definition(
        "Uf2fpRegRegRegImm",
        "UF2FP",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate)
        ),
        Vec("uf2fp_f32_to_8b_downconvert__URURI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.Uf2fpRegRegRegImm(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object Uf2fpRegRegRegReg extends Definition(
        "Uf2fpRegRegRegReg",
        "UF2FP",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("uf2fp_f32_to_8b_downconvert__URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Uf2fpRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Uf2fpudpPipeRegImm extends Definition(
        "Uf2fpudpPipeRegImm",
        "UF2FPudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("uf2fp_8b_upconvert__URIUR", "uf2fp_tf32__URIUR", "uf2fp_tf32_default_src__URIUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.Uf2fpudpPipeRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object Uf2fpudpPipeRegImmReg extends Definition(
        "Uf2fpudpPipeRegImmReg",
        "UF2FPudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.Immediate), Slot("srcC", OperandKind.Register)),
        Vec("uf2fp_f16_to_8b_downconvert__URIUR", "uf2fp_merge_c__URIUR", "uf2fp_merge_c_default_dst_src__URIUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Uf2fpudpPipeRegImmReg(dst, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Uf2fpudpPipeRegReg extends Definition(
        "Uf2fpudpPipeRegReg",
        "UF2FPudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("uf2fp_8b_upconvert__URURUR", "uf2fp_tf32__URURUR", "uf2fp_tf32_default_src__URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.Uf2fpudpPipeRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object Uf2fpudpPipeRegRegImm extends Definition(
        "Uf2fpudpPipeRegRegImm",
        "UF2FPudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec(
          "uf2fp__URIUR",
          "uf2fp_default_dst_src__URIUR",
          "uf2fp_f16_to_8b_downconvert__URURI",
          "uf2fp_merge_c__URURI",
          "uf2fp_merge_c_default_dst_src__URURI"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.Uf2fpudpPipeRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object Uf2fpudpPipeRegRegImmReg extends Definition(
        "Uf2fpudpPipeRegRegImmReg",
        "UF2FPudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("uf2fp_f32_to_8b_downconvert__URIUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Uf2fpudpPipeRegRegImmReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Uf2fpudpPipeRegRegReg extends Definition(
        "Uf2fpudpPipeRegRegReg",
        "UF2FPudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec(
          "uf2fp__URURUR",
          "uf2fp_default_dst_src__URURUR",
          "uf2fp_f16_to_8b_downconvert__URURUR",
          "uf2fp_merge_c__URURUR",
          "uf2fp_merge_c_default_dst_src__URURUR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.Uf2fpudpPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object Uf2fpudpPipeRegRegRegImm extends Definition(
        "Uf2fpudpPipeRegRegRegImm",
        "UF2FPudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate)
        ),
        Vec("uf2fp_f32_to_8b_downconvert__URURI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.Uf2fpudpPipeRegRegRegImm(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object Uf2fpudpPipeRegRegRegReg extends Definition(
        "Uf2fpudpPipeRegRegRegReg",
        "UF2FPudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("uf2fp_f32_to_8b_downconvert__URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Uf2fpudpPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Uf2fudpPipeRegImm extends Definition(
        "Uf2fudpPipeRegImm",
        "UF2Fudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("uf2f_f32_downconvert__URIR_URIR", "uf2f_f32_upconvert__URIR_URIR", "uf2f_f32_upconvert_swap__URIR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.Uf2fudpPipeRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object Uf2fudpPipeRegReg extends Definition(
        "Uf2fudpPipeRegReg",
        "UF2Fudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("uf2f_f32_downconvert__UUU_UUU", "uf2f_f32_upconvert__UUU_UUU", "uf2f_f32_upconvert_swap__UUU_UUU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.Uf2fudpPipeRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object Uf2iRegImm extends Definition(
        "Uf2iRegImm",
        "UF2I",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec(
          "uf2i__IU_32b",
          "uf2i__Ib_16b",
          "uf2i__Ib_bf16",
          "uf2i_swap__IU_32b",
          "uf2i_swap__Ib_16b",
          "uf2i_swap__Ib_bf16"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.Uf2iRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object Uf2iRegReg extends Definition(
        "Uf2iRegReg",
        "UF2I",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec(
          "uf2i__URb_16b",
          "uf2i__URb_32b",
          "uf2i__URb_bf16",
          "uf2i_swap__URb_16b",
          "uf2i_swap__URb_32b",
          "uf2i_swap__URb_bf16"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.Uf2iRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object Uf2ipRegRegImmReg extends Definition(
        "Uf2ipRegRegImmReg",
        "UF2IP",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("uf2ip__URIUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Uf2ipRegRegImmReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Uf2ipRegRegRegImm extends Definition(
        "Uf2ipRegRegRegImm",
        "UF2IP",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate)
        ),
        Vec("uf2ip__RRI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.Uf2ipRegRegRegImm(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object Uf2ipRegRegRegReg extends Definition(
        "Uf2ipRegRegRegReg",
        "UF2IP",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("uf2ip__UUU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Uf2ipRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Uf2ipudpPipeRegRegImmReg extends Definition(
        "Uf2ipudpPipeRegRegImmReg",
        "UF2IPudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("uf2ip__URIUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Uf2ipudpPipeRegRegImmReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Uf2ipudpPipeRegRegRegImm extends Definition(
        "Uf2ipudpPipeRegRegRegImm",
        "UF2IPudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate)
        ),
        Vec("uf2ip__RRI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.Uf2ipudpPipeRegRegRegImm(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object Uf2ipudpPipeRegRegRegReg extends Definition(
        "Uf2ipudpPipeRegRegRegReg",
        "UF2IPudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("uf2ip__UUU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Uf2ipudpPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Uf2iudpPipeRegImm extends Definition(
        "Uf2iudpPipeRegImm",
        "UF2Iudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec(
          "uf2i__IU_32b",
          "uf2i__Ib_16b",
          "uf2i__Ib_bf16",
          "uf2i_swap__IU_32b",
          "uf2i_swap__Ib_16b",
          "uf2i_swap__Ib_bf16"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.Uf2iudpPipeRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object Uf2iudpPipeRegReg extends Definition(
        "Uf2iudpPipeRegReg",
        "UF2Iudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec(
          "uf2i__URb_16b",
          "uf2i__URb_32b",
          "uf2i__URb_bf16",
          "uf2i_swap__URb_16b",
          "uf2i_swap__URb_32b",
          "uf2i_swap__URb_bf16"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.Uf2iudpPipeRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UfaddRegRegImm extends Definition(
        "UfaddRegRegImm",
        "UFADD",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sc", OperandKind.Immediate)),
        Vec("ufadd__URURsI_URURI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.UfaddRegRegImm(dst, srcA, sc, modifiers, guard))
        case _ => None
      }
  }
  object UfaddRegRegReg extends Definition(
        "UfaddRegRegReg",
        "UFADD",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcC", OperandKind.Register)),
        Vec("ufadd__URURUR_UUU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.UfaddRegRegReg(dst, srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object UfaddudpPipeRegRegImm extends Definition(
        "UfaddudpPipeRegRegImm",
        "UFADDudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sc", OperandKind.Immediate)),
        Vec("ufadd__URURsI_URURI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.UfaddudpPipeRegRegImm(dst, srcA, sc, modifiers, guard))
        case _ => None
      }
  }
  object UfaddudpPipeRegRegReg extends Definition(
        "UfaddudpPipeRegRegReg",
        "UFADDudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcC", OperandKind.Register)),
        Vec("ufadd__URURUR_UUU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.UfaddudpPipeRegRegReg(dst, srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object UffmaRegRegImmReg extends Definition(
        "UffmaRegRegImmReg",
        "UFFMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("uffma__URIUR_URIUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.UffmaRegRegImmReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object UffmaRegRegRegImm extends Definition(
        "UffmaRegRegRegImm",
        "UFFMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate)
        ),
        Vec("uffma__URURI_URURI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.UffmaRegRegRegImm(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object UffmaRegRegRegReg extends Definition(
        "UffmaRegRegRegReg",
        "UFFMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("uffma__UUU_UUU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.UffmaRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object UffmaudpPipeRegRegImmReg extends Definition(
        "UffmaudpPipeRegRegImmReg",
        "UFFMAudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("uffma__URIUR_URIUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.UffmaudpPipeRegRegImmReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object UffmaudpPipeRegRegRegImm extends Definition(
        "UffmaudpPipeRegRegRegImm",
        "UFFMAudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate)
        ),
        Vec("uffma__URURI_URURI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.UffmaudpPipeRegRegRegImm(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object UffmaudpPipeRegRegRegReg extends Definition(
        "UffmaudpPipeRegRegRegReg",
        "UFFMAudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("uffma__UUU_UUU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.UffmaudpPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object UfhaddRegRegReg extends Definition(
        "UfhaddRegRegReg",
        "UFHADD",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcC", OperandKind.Register)),
        Vec("ufhadd__UUU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.UfhaddRegRegReg(dst, srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object UfhaddudpPipeRegRegReg extends Definition(
        "UfhaddudpPipeRegRegReg",
        "UFHADDudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcC", OperandKind.Register)),
        Vec("ufhadd__UUU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.UfhaddudpPipeRegRegReg(dst, srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object UfhfmaRegRegRegReg extends Definition(
        "UfhfmaRegRegRegReg",
        "UFHFMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("ufhfma__UUU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.UfhfmaRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object UfhfmaudpPipeRegRegRegReg extends Definition(
        "UfhfmaudpPipeRegRegRegReg",
        "UFHFMAudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("ufhfma__UUU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.UfhfmaudpPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object UfloRegImm extends Definition(
        "UfloRegImm",
        "UFLO",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("uflo__URuIUR_URuIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.UfloRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object UfloRegPredImm extends Definition(
        "UfloRegPredImm",
        "UFLO",
        Vec(Slot("dst", OperandKind.Register), Slot("uPu", OperandKind.Predicate), Slot("sb", OperandKind.Immediate)),
        Vec("uflo__URuIUR_URuIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.UfloRegPredImm(dst, uPu, sb, modifiers, guard))
        case _ => None
      }
  }
  object UfloRegPredReg extends Definition(
        "UfloRegPredReg",
        "UFLO",
        Vec(Slot("dst", OperandKind.Register), Slot("uPu", OperandKind.Predicate), Slot("srcB", OperandKind.Register)),
        Vec("uflo__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.UfloRegPredReg(dst, uPu, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UfloRegReg extends Definition(
        "UfloRegReg",
        "UFLO",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("uflo__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.UfloRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UfloudpPipeRegImm extends Definition(
        "UfloudpPipeRegImm",
        "UFLOudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("uflo__URuIUR_URuIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.UfloudpPipeRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object UfloudpPipeRegPredImm extends Definition(
        "UfloudpPipeRegPredImm",
        "UFLOudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("uPu", OperandKind.Predicate), Slot("sb", OperandKind.Immediate)),
        Vec("uflo__URuIUR_URuIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.UfloudpPipeRegPredImm(dst, uPu, sb, modifiers, guard))
        case _ => None
      }
  }
  object UfloudpPipeRegPredReg extends Definition(
        "UfloudpPipeRegPredReg",
        "UFLOudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("uPu", OperandKind.Predicate), Slot("srcB", OperandKind.Register)),
        Vec("uflo__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.UfloudpPipeRegPredReg(dst, uPu, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UfloudpPipeRegReg extends Definition(
        "UfloudpPipeRegReg",
        "UFLOudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("uflo__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.UfloudpPipeRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UfmnmxRegPredRegImmPred extends Definition(
        "UfmnmxRegPredRegImmPred",
        "UFMNMX",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uPu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ufmnmx_pred__URsIUR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UfmnmxRegPredRegImmPred(dst, uPu, srcA, sb, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UfmnmxRegPredRegRegPred extends Definition(
        "UfmnmxRegPredRegRegPred",
        "UFMNMX",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uPu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ufmnmx_pred__URURUR_UUU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UfmnmxRegPredRegRegPred(dst, uPu, srcA, srcB, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UfmnmxRegRegImmPred extends Definition(
        "UfmnmxRegRegImmPred",
        "UFMNMX",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ufmnmx__URsIUR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UfmnmxRegRegImmPred(dst, srcA, sb, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UfmnmxRegRegRegPred extends Definition(
        "UfmnmxRegRegRegPred",
        "UFMNMX",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ufmnmx__URURUR_UUU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UfmnmxRegRegRegPred(dst, srcA, srcB, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UfmnmxudpPipeRegPredRegImmPred extends Definition(
        "UfmnmxudpPipeRegPredRegImmPred",
        "UFMNMXudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uPu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ufmnmx_pred__URsIUR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UfmnmxudpPipeRegPredRegImmPred(dst, uPu, srcA, sb, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UfmnmxudpPipeRegPredRegRegPred extends Definition(
        "UfmnmxudpPipeRegPredRegRegPred",
        "UFMNMXudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uPu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ufmnmx_pred__URURUR_UUU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UfmnmxudpPipeRegPredRegRegPred(dst, uPu, srcA, srcB, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UfmnmxudpPipeRegRegImmPred extends Definition(
        "UfmnmxudpPipeRegRegImmPred",
        "UFMNMXudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ufmnmx__URsIUR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UfmnmxudpPipeRegRegImmPred(dst, srcA, sb, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UfmnmxudpPipeRegRegRegPred extends Definition(
        "UfmnmxudpPipeRegRegRegPred",
        "UFMNMXudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ufmnmx__URURUR_UUU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UfmnmxudpPipeRegRegRegPred(dst, srcA, srcB, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UfmulRegRegImm extends Definition(
        "UfmulRegRegImm",
        "UFMUL",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("ufmul__URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.UfmulRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object UfmulRegRegReg extends Definition(
        "UfmulRegRegReg",
        "UFMUL",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("ufmul__UUU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.UfmulRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UfmuludpPipeRegRegImm extends Definition(
        "UfmuludpPipeRegRegImm",
        "UFMULudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("ufmul__URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.UfmuludpPipeRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object UfmuludpPipeRegRegReg extends Definition(
        "UfmuludpPipeRegRegReg",
        "UFMULudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("ufmul__UUU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.UfmuludpPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UfrndRegImm extends Definition(
        "UfrndRegImm",
        "UFRND",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec(
          "ufrnd__bf16_I",
          "ufrnd__f16_I",
          "ufrnd__f32_I",
          "ufrnd_swap__bf16_I",
          "ufrnd_swap__f16_I",
          "ufrnd_swap__f32_I"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.UfrndRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object UfrndRegReg extends Definition(
        "UfrndRegReg",
        "UFRND",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec(
          "ufrnd__bf16_URb",
          "ufrnd__f16_URb",
          "ufrnd__f32_URb",
          "ufrnd_swap__bf16_URb",
          "ufrnd_swap__f16_URb",
          "ufrnd_swap__f32_URb"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.UfrndRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UfrndudpPipeRegImm extends Definition(
        "UfrndudpPipeRegImm",
        "UFRNDudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec(
          "ufrnd__bf16_I",
          "ufrnd__f16_I",
          "ufrnd__f32_I",
          "ufrnd_swap__bf16_I",
          "ufrnd_swap__f16_I",
          "ufrnd_swap__f32_I"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.UfrndudpPipeRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object UfrndudpPipeRegReg extends Definition(
        "UfrndudpPipeRegReg",
        "UFRNDudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec(
          "ufrnd__bf16_URb",
          "ufrnd__f16_URb",
          "ufrnd__f32_URb",
          "ufrnd_swap__bf16_URb",
          "ufrnd_swap__f16_URb",
          "ufrnd_swap__f32_URb"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.UfrndudpPipeRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UfselRegRegImmPred extends Definition(
        "UfselRegRegImmPred",
        "UFSEL",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ufsel__URsIUR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UfselRegRegImmPred(dst, srcA, sb, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UfselRegRegRegPred extends Definition(
        "UfselRegRegRegPred",
        "UFSEL",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ufsel__URURUR_UUU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UfselRegRegRegPred(dst, srcA, srcB, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UfseludpPipeRegRegImmPred extends Definition(
        "UfseludpPipeRegRegImmPred",
        "UFSELudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ufsel__URsIUR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UfseludpPipeRegRegImmPred(dst, srcA, sb, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UfseludpPipeRegRegRegPred extends Definition(
        "UfseludpPipeRegRegRegPred",
        "UFSELudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ufsel__URURUR_UUU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UfseludpPipeRegRegRegPred(dst, srcA, srcB, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UfsetRegRegImm extends Definition(
        "UfsetRegRegImm",
        "UFSET",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("ufset_simple__URsIUR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.UfsetRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object UfsetRegRegImmPred extends Definition(
        "UfsetRegRegImmPred",
        "UFSET",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ufset__URsIUR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UfsetRegRegImmPred(dst, srcA, sb, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UfsetRegRegReg extends Definition(
        "UfsetRegRegReg",
        "UFSET",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("ufset_simple__URURUR_UUU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.UfsetRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UfsetRegRegRegPred extends Definition(
        "UfsetRegRegRegPred",
        "UFSET",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ufset__URURUR_UUU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UfsetRegRegRegPred(dst, srcA, srcB, uPp, modifiers, guard))
        case _ => None
      }
  }
  val definitions: Vec[Definition] = Vec(
    TldmioPipePredRegRegRegReg,
    TldmioPipePredRegRegRegRegImm,
    TldmioPipePredRegRegRegRegImmImm,
    TldmioPipePredRegRegRegRegImmImmImm,
    TldmioPipePredRegRegRegRegImmImmImmImm,
    TldmioPipePredRegRegRegRegReg,
    TldmioPipePredRegRegRegRegRegImm,
    TldmioPipePredRegRegRegRegRegImmImm,
    TldmioPipePredRegRegRegRegRegReg,
    TldmioPipePredRegRegRegRegRegRegImm,
    TldmioPipePredRegRegRegRegRegRegImmImm,
    TldmioPipeRegRegRegImm,
    TldmioPipeRegRegRegImmImm,
    TldmioPipeRegRegRegReg,
    TldmioPipeRegRegRegRegImm,
    TldmioPipeRegRegRegRegImmImm,
    TldmioPipeRegRegRegRegImmImmImm,
    TldmioPipeRegRegRegRegImmImmImmImm,
    TldmioPipeRegRegRegRegReg,
    TldmioPipeRegRegRegRegRegImm,
    TldmioPipeRegRegRegRegRegImmImm,
    TldmioPipeRegRegRegRegRegReg,
    TldmioPipeRegRegRegRegRegRegImm,
    TldmioPipeRegRegRegRegRegRegImmImm,
    TldtexPipeRegRegRegImmImm,
    TmmlRegRegImm,
    TmmlRegRegImmImm,
    TmmlRegRegImmImmImm,
    TmmlRegRegRegImm,
    TmmlRegRegRegImmImm,
    TmmlRegRegRegImmImmImm,
    TmmlRegRegRegReg,
    TmmlRegRegRegRegImm,
    TmmlRegRegRegRegImmImm,
    TmmlRegRegRegRegImmImmImm,
    TmmlRegRegRegRegImmImmImmImm,
    TmmlRegRegRegRegReg,
    TmmlRegRegRegRegRegImm,
    TmmlRegRegRegRegRegImmImm,
    TmmlmioPipeRegRegImm,
    TmmlmioPipeRegRegImmImm,
    TmmlmioPipeRegRegImmImmImm,
    TmmlmioPipeRegRegRegImm,
    TmmlmioPipeRegRegRegImmImm,
    TmmlmioPipeRegRegRegImmImmImm,
    TmmlmioPipeRegRegRegReg,
    TmmlmioPipeRegRegRegRegImm,
    TmmlmioPipeRegRegRegRegImmImm,
    TmmlmioPipeRegRegRegRegImmImmImm,
    TmmlmioPipeRegRegRegRegImmImmImmImm,
    TmmlmioPipeRegRegRegRegReg,
    TmmlmioPipeRegRegRegRegRegImm,
    TmmlmioPipeRegRegRegRegRegImmImm,
    TmmltexPipeRegRegImmImm,
    TmmltexPipeRegRegImmImmImm,
    TmmltexPipeRegRegRegImmImm,
    TmmltexPipeRegRegRegImmImmImm,
    TxaRegRegImm,
    TxaRegRegImmImm,
    TxaRegRegImmImmImm,
    TxamioPipeRegRegImm,
    TxamioPipeRegRegImmImm,
    TxamioPipeRegRegImmImmImm,
    TxatexPipeRegRegImmImm,
    TxatexPipeRegRegImmImmImm,
    TxdPredRegRegRegImm,
    TxdPredRegRegRegImmImm,
    TxdPredRegRegRegImmImmImm,
    TxdPredRegRegRegReg,
    TxdPredRegRegRegRegImm,
    TxdPredRegRegRegRegImmImm,
    TxdPredRegRegRegRegImmImmImm,
    TxdPredRegRegRegRegImmImmImmImm,
    TxdPredRegRegRegRegReg,
    TxdPredRegRegRegRegRegImm,
    TxdPredRegRegRegRegRegImmImm,
    TxdPredRegRegRegRegRegReg,
    TxdPredRegRegRegRegRegRegImm,
    TxdPredRegRegRegRegRegRegImmImm,
    TxdRegRegRegImm,
    TxdRegRegRegImmImm,
    TxdRegRegRegImmImmImm,
    TxdRegRegRegReg,
    TxdRegRegRegRegImm,
    TxdRegRegRegRegImmImm,
    TxdRegRegRegRegImmImmImm,
    TxdRegRegRegRegImmImmImmImm,
    TxdRegRegRegRegReg,
    TxdRegRegRegRegRegImm,
    TxdRegRegRegRegRegImmImm,
    TxdRegRegRegRegRegReg,
    TxdRegRegRegRegRegRegImm,
    TxdRegRegRegRegRegRegImmImm,
    TxdmioPipePredRegRegRegImm,
    TxdmioPipePredRegRegRegImmImm,
    TxdmioPipePredRegRegRegImmImmImm,
    TxdmioPipePredRegRegRegReg,
    TxdmioPipePredRegRegRegRegImm,
    TxdmioPipePredRegRegRegRegImmImm,
    TxdmioPipePredRegRegRegRegImmImmImm,
    TxdmioPipePredRegRegRegRegImmImmImmImm,
    TxdmioPipePredRegRegRegRegReg,
    TxdmioPipePredRegRegRegRegRegImm,
    TxdmioPipePredRegRegRegRegRegImmImm,
    TxdmioPipePredRegRegRegRegRegReg,
    TxdmioPipePredRegRegRegRegRegRegImm,
    TxdmioPipePredRegRegRegRegRegRegImmImm,
    TxdmioPipeRegRegRegImm,
    TxdmioPipeRegRegRegImmImm,
    TxdmioPipeRegRegRegImmImmImm,
    TxdmioPipeRegRegRegReg,
    TxdmioPipeRegRegRegRegImm,
    TxdmioPipeRegRegRegRegImmImm,
    TxdmioPipeRegRegRegRegImmImmImm,
    TxdmioPipeRegRegRegRegImmImmImmImm,
    TxdmioPipeRegRegRegRegReg,
    TxdmioPipeRegRegRegRegRegImm,
    TxdmioPipeRegRegRegRegRegImmImm,
    TxdmioPipeRegRegRegRegRegReg,
    TxdmioPipeRegRegRegRegRegRegImm,
    TxdmioPipeRegRegRegRegRegRegImmImm,
    TxdtexPipeRegRegRegImmImm,
    TxdtexPipeRegRegRegImmImmImm,
    TxqRegRegImm,
    TxqRegRegImmImm,
    TxqRegRegImmImmImm,
    TxqRegRegImmImmImmImm,
    TxqRegRegRegImm,
    TxqRegRegRegImmImmImm,
    TxqRegRegRegImmImmImmImm,
    TxqRegRegRegRegImm,
    TxqRegRegRegRegImmImm,
    TxqmioPipeRegRegImm,
    TxqmioPipeRegRegImmImm,
    TxqmioPipeRegRegImmImmImm,
    TxqmioPipeRegRegImmImmImmImm,
    TxqmioPipeRegRegRegImm,
    TxqmioPipeRegRegRegImmImmImm,
    TxqmioPipeRegRegRegImmImmImmImm,
    TxqmioPipeRegRegRegRegImm,
    TxqmioPipeRegRegRegRegImmImm,
    TxqtexPipeRegRegImmImm,
    TxqtexPipeRegRegImmImmImm,
    TxqtexPipeRegRegImmImmImmImm,
    UblkcpAddressAddressReg,
    UblkcpAddressAddressRegAddress,
    UblkcpudpPipeAddressAddressReg,
    UblkcpudpPipeAddressAddressRegAddress,
    UblkpfAddressReg,
    UblkpfAddressRegAddress,
    UblkpfudpPipeAddressReg,
    UblkpfudpPipeAddressRegAddress,
    UblkredAddressAddressReg,
    UblkredAddressAddressRegAddress,
    UblkredudpPipeAddressAddressReg,
    UblkredudpPipeAddressAddressRegAddress,
    UbmskRegRegImm,
    UbmskRegRegReg,
    UbmskudpPipeRegRegImm,
    UbmskudpPipeRegRegReg,
    UbrevRegImm,
    UbrevRegReg,
    UbrevudpPipeRegImm,
    UbrevudpPipeRegReg,
    Ucgabararv_4e92c8d663b7NoOperands,
    UcgabararvudpPipe_6660a077ec17NoOperands,
    UcgabargetReg,
    UcgabargetudpPipeReg,
    UcgabarsetReg,
    UcgabarsetudpPipeReg,
    Ucgabarwait_83a66759c33cNoOperands,
    UcgabarwaitudpPipe_e2f72cc439b3NoOperands,
    UcgabarArv_2a103ba9b90bNoOperands,
    UcgabarArvudpPipe_1cc606698836NoOperands,
    UcgabarWait_c3921c43e665NoOperands,
    UcgabarWaitudpPipe_10b5909b104bNoOperands,
    UcleaRegPredRegImmImm,
    UcleaRegPredRegRegImm,
    UcleaudpPipeRegPredRegImmImm,
    UcleaudpPipeRegPredRegRegImm,
    Uf2fRegImm,
    Uf2fRegReg,
    Uf2fpRegImm,
    Uf2fpRegImmReg,
    Uf2fpRegReg,
    Uf2fpRegRegImm,
    Uf2fpRegRegImmReg,
    Uf2fpRegRegReg,
    Uf2fpRegRegRegImm,
    Uf2fpRegRegRegReg,
    Uf2fpudpPipeRegImm,
    Uf2fpudpPipeRegImmReg,
    Uf2fpudpPipeRegReg,
    Uf2fpudpPipeRegRegImm,
    Uf2fpudpPipeRegRegImmReg,
    Uf2fpudpPipeRegRegReg,
    Uf2fpudpPipeRegRegRegImm,
    Uf2fpudpPipeRegRegRegReg,
    Uf2fudpPipeRegImm,
    Uf2fudpPipeRegReg,
    Uf2iRegImm,
    Uf2iRegReg,
    Uf2ipRegRegImmReg,
    Uf2ipRegRegRegImm,
    Uf2ipRegRegRegReg,
    Uf2ipudpPipeRegRegImmReg,
    Uf2ipudpPipeRegRegRegImm,
    Uf2ipudpPipeRegRegRegReg,
    Uf2iudpPipeRegImm,
    Uf2iudpPipeRegReg,
    UfaddRegRegImm,
    UfaddRegRegReg,
    UfaddudpPipeRegRegImm,
    UfaddudpPipeRegRegReg,
    UffmaRegRegImmReg,
    UffmaRegRegRegImm,
    UffmaRegRegRegReg,
    UffmaudpPipeRegRegImmReg,
    UffmaudpPipeRegRegRegImm,
    UffmaudpPipeRegRegRegReg,
    UfhaddRegRegReg,
    UfhaddudpPipeRegRegReg,
    UfhfmaRegRegRegReg,
    UfhfmaudpPipeRegRegRegReg,
    UfloRegImm,
    UfloRegPredImm,
    UfloRegPredReg,
    UfloRegReg,
    UfloudpPipeRegImm,
    UfloudpPipeRegPredImm,
    UfloudpPipeRegPredReg,
    UfloudpPipeRegReg,
    UfmnmxRegPredRegImmPred,
    UfmnmxRegPredRegRegPred,
    UfmnmxRegRegImmPred,
    UfmnmxRegRegRegPred,
    UfmnmxudpPipeRegPredRegImmPred,
    UfmnmxudpPipeRegPredRegRegPred,
    UfmnmxudpPipeRegRegImmPred,
    UfmnmxudpPipeRegRegRegPred,
    UfmulRegRegImm,
    UfmulRegRegReg,
    UfmuludpPipeRegRegImm,
    UfmuludpPipeRegRegReg,
    UfrndRegImm,
    UfrndRegReg,
    UfrndudpPipeRegImm,
    UfrndudpPipeRegReg,
    UfselRegRegImmPred,
    UfselRegRegRegPred,
    UfseludpPipeRegRegImmPred,
    UfseludpPipeRegRegRegPred,
    UfsetRegRegImm,
    UfsetRegRegImmPred,
    UfsetRegRegReg,
    UfsetRegRegRegPred
  )
}
