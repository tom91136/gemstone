// Generated SASS instruction definitions and checked operand constructors. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.sass
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import Instruction.*
@javax.annotation.processing.Generated(Array("sbt codegen"))
object InstructionDefinitions3 {
  object Fmul2RegRegReg extends Definition(
        "Fmul2RegRegReg",
        "FMUL2",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("fmul2_rb__RRR", "fmul2_urb__RUR")
      ) {
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
  object Fmul2fmalighterPipeRegRegImm extends Definition(
        "Fmul2fmalighterPipeRegRegImm",
        "FMUL2fmalighter_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("fmul2_imm__RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.Fmul2fmalighterPipeRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object Fmul2fmalighterPipeRegRegReg extends Definition(
        "Fmul2fmalighterPipeRegRegReg",
        "FMUL2fmalighter_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("fmul2_rb__RRR", "fmul2_urb__RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.Fmul2fmalighterPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object Fmul32iRegRegImm extends Definition(
        "Fmul32iRegRegImm",
        "FMUL32I",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("FMUL32I", "fmul32i_")
      ) {
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
  object Fmul32ifauPipeRegRegImm extends Definition(
        "Fmul32ifauPipeRegRegImm",
        "FMUL32Ifau_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("fImm", OperandKind.Immediate)),
        Vec("FMUL32I")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(fImm)
            ) => Some(Instruction.Fmul32ifauPipeRegRegImm(dst, srcA, fImm, modifiers, guard))
        case _ => None
      }
  }
  object Fmul32ifmaxPipeRegRegImm extends Definition(
        "Fmul32ifmaxPipeRegRegImm",
        "FMUL32IfmaX_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("fImm", OperandKind.Immediate)),
        Vec("FMUL32I")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(fImm)
            ) => Some(Instruction.Fmul32ifmaxPipeRegRegImm(dst, srcA, fImm, modifiers, guard))
        case _ => None
      }
  }
  object Fmul32ifmaxlitewPipeRegRegImm extends Definition(
        "Fmul32ifmaxlitewPipeRegRegImm",
        "FMUL32IfmaXliteW_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("fImm", OperandKind.Immediate)),
        Vec("FMUL32I")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(fImm)
            ) => Some(Instruction.Fmul32ifmaxlitewPipeRegRegImm(dst, srcA, fImm, modifiers, guard))
        case _ => None
      }
  }
  object Fmul32ifmaiPipeRegRegImm extends Definition(
        "Fmul32ifmaiPipeRegRegImm",
        "FMUL32Ifmai_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("fImm", OperandKind.Immediate)),
        Vec("FMUL32I")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(fImm)
            ) => Some(Instruction.Fmul32ifmaiPipeRegRegImm(dst, srcA, fImm, modifiers, guard))
        case _ => None
      }
  }
  object Fmul32ifmalighterPipeRegRegImm extends Definition(
        "Fmul32ifmalighterPipeRegRegImm",
        "FMUL32Ifmalighter_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("fmul32i_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.Fmul32ifmalighterPipeRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object Fmul32ifxuPipeRegRegImm extends Definition(
        "Fmul32ifxuPipeRegRegImm",
        "FMUL32Ifxu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("fImm", OperandKind.Immediate)),
        Vec("FMUL32I")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(fImm)
            ) => Some(Instruction.Fmul32ifxuPipeRegRegImm(dst, srcA, fImm, modifiers, guard))
        case _ => None
      }
  }
  object FmulfauPipeRegRegConst extends Definition(
        "FmulfauPipeRegRegConst",
        "FMULfau_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const FMUL")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.FmulfauPipeRegRegConst(dst, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object FmulfauPipeRegRegImm extends Definition(
        "FmulfauPipeRegRegImm",
        "FMULfau_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("Imm FMUL")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.FmulfauPipeRegRegImm(dst, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object FmulfauPipeRegRegReg extends Definition(
        "FmulfauPipeRegRegReg",
        "FMULfau_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("FMUL")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.FmulfauPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object FmulfmaxPipeRegRegConst extends Definition(
        "FmulfmaxPipeRegRegConst",
        "FMULfmaX_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const FMUL")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.FmulfmaxPipeRegRegConst(dst, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object FmulfmaxPipeRegRegImm extends Definition(
        "FmulfmaxPipeRegRegImm",
        "FMULfmaX_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("Imm FMUL")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.FmulfmaxPipeRegRegImm(dst, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object FmulfmaxPipeRegRegReg extends Definition(
        "FmulfmaxPipeRegRegReg",
        "FMULfmaX_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("FMUL")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.FmulfmaxPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object FmulfmaxlitewPipeRegRegConst extends Definition(
        "FmulfmaxlitewPipeRegRegConst",
        "FMULfmaXliteW_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const FMUL")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.FmulfmaxlitewPipeRegRegConst(dst, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object FmulfmaxlitewPipeRegRegImm extends Definition(
        "FmulfmaxlitewPipeRegRegImm",
        "FMULfmaXliteW_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("Imm FMUL")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.FmulfmaxlitewPipeRegRegImm(dst, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object FmulfmaxlitewPipeRegRegReg extends Definition(
        "FmulfmaxlitewPipeRegRegReg",
        "FMULfmaXliteW_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("FMUL")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.FmulfmaxlitewPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object FmulfmaiPipeRegRegConst extends Definition(
        "FmulfmaiPipeRegRegConst",
        "FMULfmai_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const_FMUL")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.FmulfmaiPipeRegRegConst(dst, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object FmulfmaiPipeRegRegImm extends Definition(
        "FmulfmaiPipeRegRegImm",
        "FMULfmai_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("Imm_FMUL")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.FmulfmaiPipeRegRegImm(dst, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object FmulfmaiPipeRegRegReg extends Definition(
        "FmulfmaiPipeRegRegReg",
        "FMULfmai_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("FMUL")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.FmulfmaiPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object FmulfmalighterPipeRegRegAddressImm extends Definition(
        "FmulfmalighterPipeRegRegAddressImm",
        "FMULfmalighter_pipe",
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
            ) => Some(Instruction.FmulfmalighterPipeRegRegAddressImm(dst, srcA, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object FmulfmalighterPipeRegRegConst extends Definition(
        "FmulfmalighterPipeRegRegConst",
        "FMULfmalighter_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank)
        ),
        Vec("fmul__RCR_RC")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb)
            ) => Some(Instruction.FmulfmalighterPipeRegRegConst(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object FmulfmalighterPipeRegRegImm extends Definition(
        "FmulfmalighterPipeRegRegImm",
        "FMULfmalighter_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("fmul__RIR_RI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.FmulfmalighterPipeRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object FmulfmalighterPipeRegRegReg extends Definition(
        "FmulfmalighterPipeRegRegReg",
        "FMULfmalighter_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("fmul__RRR_RR", "fmul__RUR_RU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.FmulfmalighterPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object FmulfxuPipeRegRegConst extends Definition(
        "FmulfxuPipeRegRegConst",
        "FMULfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const_FMUL")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.FmulfxuPipeRegRegConst(dst, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object FmulfxuPipeRegRegImm extends Definition(
        "FmulfxuPipeRegRegImm",
        "FMULfxu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("Imm_FMUL")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.FmulfxuPipeRegRegImm(dst, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object FmulfxuPipeRegRegReg extends Definition(
        "FmulfxuPipeRegRegReg",
        "FMULfxu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("FMUL")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.FmulfxuPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object FootprintPredRegRegRegReg extends Definition(
        "FootprintPredRegRegRegReg",
        "FOOTPRINT",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register)
        ),
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
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("tsPtrIdx", OperandKind.Immediate)
        ),
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
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
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
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
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
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register)
        ),
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
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("tsPtrIdx", OperandKind.Immediate)
        ),
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
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("hdrtblbase6", OperandKind.Immediate),
          Slot("tid8", OperandKind.Immediate)
        ),
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
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
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
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
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
  object FootprintmioPipePredRegRegRegReg extends Definition(
        "FootprintmioPipePredRegRegRegReg",
        "FOOTPRINTmio_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register)
        ),
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
            ) => Some(Instruction.FootprintmioPipePredRegRegRegReg(pu, rd2, dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object FootprintmioPipePredRegRegRegRegImmImm extends Definition(
        "FootprintmioPipePredRegRegRegRegImmImm",
        "FOOTPRINTmio_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("tsPtrIdx", OperandKind.Immediate)
        ),
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
            ) => Some(Instruction.FootprintmioPipePredRegRegRegRegImmImm(
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
  object FootprintmioPipePredRegRegRegRegImmImmImm extends Definition(
        "FootprintmioPipePredRegRegRegRegImmImmImm",
        "FOOTPRINTmio_pipe",
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
            ) => Some(Instruction.FootprintmioPipePredRegRegRegRegImmImmImm(
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
  object FootprintmioPipePredRegRegRegRegReg extends Definition(
        "FootprintmioPipePredRegRegRegRegReg",
        "FOOTPRINTmio_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
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
            ) => Some(Instruction.FootprintmioPipePredRegRegRegRegReg(pu, rd2, dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FootprintmioPipePredRegRegRegRegRegImm extends Definition(
        "FootprintmioPipePredRegRegRegRegRegImm",
        "FOOTPRINTmio_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
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
            ) => Some(Instruction.FootprintmioPipePredRegRegRegRegRegImm(
            pu,
            rd2,
            dst,
            srcA,
            srcB,
            srcC,
            tid8,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object FootprintmioPipeRegRegRegReg extends Definition(
        "FootprintmioPipeRegRegRegReg",
        "FOOTPRINTmio_pipe",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register)
        ),
        Vec("footprint_b_noConst_", "footprint_scr_b_noConst_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.FootprintmioPipeRegRegRegReg(rd2, dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object FootprintmioPipeRegRegRegRegImmImm extends Definition(
        "FootprintmioPipeRegRegRegRegImmImm",
        "FOOTPRINTmio_pipe",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("tsPtrIdx", OperandKind.Immediate)
        ),
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
            ) =>
          Some(Instruction.FootprintmioPipeRegRegRegRegImmImm(rd2, dst, srcA, srcB, bank5, tsPtrIdx, modifiers, guard))
        case _ => None
      }
  }
  object FootprintmioPipeRegRegRegRegImmImmImm extends Definition(
        "FootprintmioPipeRegRegRegRegImmImmImm",
        "FOOTPRINTmio_pipe",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("hdrtblbase6", OperandKind.Immediate),
          Slot("tid8", OperandKind.Immediate)
        ),
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
            ) => Some(Instruction.FootprintmioPipeRegRegRegRegImmImmImm(
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
  object FootprintmioPipeRegRegRegRegReg extends Definition(
        "FootprintmioPipeRegRegRegRegReg",
        "FOOTPRINTmio_pipe",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
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
            ) => Some(Instruction.FootprintmioPipeRegRegRegRegReg(rd2, dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FootprintmioPipeRegRegRegRegRegImm extends Definition(
        "FootprintmioPipeRegRegRegRegRegImm",
        "FOOTPRINTmio_pipe",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
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
            ) =>
          Some(Instruction.FootprintmioPipeRegRegRegRegRegImm(rd2, dst, srcA, srcB, srcC, tid8, modifiers, guard))
        case _ => None
      }
  }
  object FrndRegAddressImm extends Definition(
        "FrndRegAddressImm",
        "FRND",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
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
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.ConstantBank)),
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
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
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
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
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
  object FrndmioPipeRegAddressImm extends Definition(
        "FrndmioPipeRegAddressImm",
        "FRNDmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
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
            ) => Some(Instruction.FrndmioPipeRegAddressImm(dst, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object FrndmioPipeRegConst extends Definition(
        "FrndmioPipeRegConst",
        "FRNDmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.ConstantBank)),
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
          Some(Instruction.FrndmioPipeRegConst(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object FrndmioPipeRegImm extends Definition(
        "FrndmioPipeRegImm",
        "FRNDmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
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
          Some(Instruction.FrndmioPipeRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object FrndmioPipeRegReg extends Definition(
        "FrndmioPipeRegReg",
        "FRNDmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
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
          Some(Instruction.FrndmioPipeRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object FselRegRegAddressImmPred extends Definition(
        "FselRegRegAddressImmPred",
        "FSEL",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("fsel__RCxR_RCxR")
      ) {
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
  object FselRegRegConstPred extends Definition(
        "FselRegRegConstPred",
        "FSEL",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("fsel__RCR_RCR")
      ) {
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
  object FselRegRegImmPred extends Definition(
        "FselRegRegImmPred",
        "FSEL",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("fsel__RIR_RIR")
      ) {
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
  object FselRegRegRegPred extends Definition(
        "FselRegRegRegPred",
        "FSEL",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("fsel__RRR_RRR", "fsel__RUR_RUR")
      ) {
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
  object FselintPipeRegRegAddressImmPred extends Definition(
        "FselintPipeRegRegAddressImmPred",
        "FSELint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("fsel__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.FselintPipeRegRegAddressImmPred(dst, srcA, srcB, sb_offset, pp, modifiers, guard))
        case _ => None
      }
  }
  object FselintPipeRegRegConstPred extends Definition(
        "FselintPipeRegRegConstPred",
        "FSELint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("fsel__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.FselintPipeRegRegConstPred(dst, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object FselintPipeRegRegImmPred extends Definition(
        "FselintPipeRegRegImmPred",
        "FSELint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("fsel__RIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.FselintPipeRegRegImmPred(dst, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object FselintPipeRegRegRegPred extends Definition(
        "FselintPipeRegRegRegPred",
        "FSELint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("fsel__RRR_RRR", "fsel__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.FselintPipeRegRegRegPred(dst, srcA, srcB, pp, modifiers, guard))
        case _ => None
      }
  }
  object FsetRegRegAddressImm extends Definition(
        "FsetRegRegAddressImm",
        "FSET",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("fset_simple__RCxR_RCxR")
      ) {
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
  object FsetRegRegAddressImmPred extends Definition(
        "FsetRegRegAddressImmPred",
        "FSET",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("fset__RCxR_RCxR")
      ) {
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
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank)
        ),
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
  object FsetRegRegConstPred extends Definition(
        "FsetRegRegConstPred",
        "FSET",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("Const FSET", "Const_FSET", "fset__RCR_RCR")
      ) {
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
  object FsetRegRegImm extends Definition(
        "FsetRegRegImm",
        "FSET",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("NoBop Imm FSET", "NoBop_Imm_FSET", "fset_simple__RIR_RIR")
      ) {
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
  object FsetRegRegImmPred extends Definition(
        "FsetRegRegImmPred",
        "FSET",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("Imm FSET", "Imm_FSET", "fset__RIR_RIR")
      ) {
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
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
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
  object FsetRegRegRegPred extends Definition(
        "FsetRegRegRegPred",
        "FSET",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("FSET", "fset__RRR_RRR", "fset__RUR_RUR")
      ) {
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
  object FsetpPredPredRegAddressImmPred extends Definition(
        "FsetpPredPredRegAddressImmPred",
        "FSETP",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("fsetp__RCxR_RCxR")
      ) {
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
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("pa", OperandKind.Predicate)
        ),
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
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("pa", OperandKind.Predicate)
        ),
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
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pa", OperandKind.Predicate)
        ),
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
  object FsetpPredRegAddressImm extends Definition(
        "FsetpPredRegAddressImm",
        "FSETP",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("fsetp_simple__RCxR_RCxR")
      ) {
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
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
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
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate)
        ),
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
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register)
        ),
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
  object FsetpfauPipePredPredRegConstPred extends Definition(
        "FsetpfauPipePredPredRegConstPred",
        "FSETPfau_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Const FSETP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Predicate(pa)
            ) =>
          Some(Instruction.FsetpfauPipePredPredRegConstPred(dstPredicate, nPd, srcA, srcConst, pa, modifiers, guard))
        case _ => None
      }
  }
  object FsetpfauPipePredPredRegImmPred extends Definition(
        "FsetpfauPipePredPredRegImmPred",
        "FSETPfau_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Imm FSETP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.FsetpfauPipePredPredRegImmPred(dstPredicate, nPd, srcA, uImm, pa, modifiers, guard))
        case _ => None
      }
  }
  object FsetpfauPipePredPredRegRegPred extends Definition(
        "FsetpfauPipePredPredRegRegPred",
        "FSETPfau_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("FSETP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.FsetpfauPipePredPredRegRegPred(dstPredicate, nPd, srcA, srcB, pa, modifiers, guard))
        case _ => None
      }
  }
  object FsetpfauPipePredRegConst extends Definition(
        "FsetpfauPipePredRegConst",
        "FSETPfau_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("NoBop Const FSETP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.FsetpfauPipePredRegConst(dstPredicate, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object FsetpfauPipePredRegImm extends Definition(
        "FsetpfauPipePredRegImm",
        "FSETPfau_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate)
        ),
        Vec("NoBop Imm FSETP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.FsetpfauPipePredRegImm(dstPredicate, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object FsetpfauPipePredRegReg extends Definition(
        "FsetpfauPipePredRegReg",
        "FSETPfau_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register)
        ),
        Vec("NoBop FSETP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.FsetpfauPipePredRegReg(dstPredicate, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object FsetpfmaxPipePredPredRegConstPred extends Definition(
        "FsetpfmaxPipePredPredRegConstPred",
        "FSETPfmaX_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Const FSETP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Predicate(pa)
            ) =>
          Some(Instruction.FsetpfmaxPipePredPredRegConstPred(dstPredicate, nPd, srcA, srcConst, pa, modifiers, guard))
        case _ => None
      }
  }
  object FsetpfmaxPipePredPredRegImmPred extends Definition(
        "FsetpfmaxPipePredPredRegImmPred",
        "FSETPfmaX_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Imm FSETP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.FsetpfmaxPipePredPredRegImmPred(dstPredicate, nPd, srcA, uImm, pa, modifiers, guard))
        case _ => None
      }
  }
  object FsetpfmaxPipePredPredRegRegPred extends Definition(
        "FsetpfmaxPipePredPredRegRegPred",
        "FSETPfmaX_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("FSETP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.FsetpfmaxPipePredPredRegRegPred(dstPredicate, nPd, srcA, srcB, pa, modifiers, guard))
        case _ => None
      }
  }
  object FsetpfmaxPipePredRegConst extends Definition(
        "FsetpfmaxPipePredRegConst",
        "FSETPfmaX_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("NoBop Const FSETP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.FsetpfmaxPipePredRegConst(dstPredicate, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object FsetpfmaxPipePredRegImm extends Definition(
        "FsetpfmaxPipePredRegImm",
        "FSETPfmaX_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate)
        ),
        Vec("NoBop Imm FSETP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.FsetpfmaxPipePredRegImm(dstPredicate, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object FsetpfmaxPipePredRegReg extends Definition(
        "FsetpfmaxPipePredRegReg",
        "FSETPfmaX_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register)
        ),
        Vec("NoBop FSETP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.FsetpfmaxPipePredRegReg(dstPredicate, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object FsetpfxuPipePredPredRegConstPred extends Definition(
        "FsetpfxuPipePredPredRegConstPred",
        "FSETPfxu_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Const_FSETP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Predicate(pa)
            ) =>
          Some(Instruction.FsetpfxuPipePredPredRegConstPred(dstPredicate, nPd, srcA, srcConst, pa, modifiers, guard))
        case _ => None
      }
  }
  object FsetpfxuPipePredPredRegImmPred extends Definition(
        "FsetpfxuPipePredPredRegImmPred",
        "FSETPfxu_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Imm_FSETP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.FsetpfxuPipePredPredRegImmPred(dstPredicate, nPd, srcA, uImm, pa, modifiers, guard))
        case _ => None
      }
  }
  object FsetpfxuPipePredPredRegRegPred extends Definition(
        "FsetpfxuPipePredPredRegRegPred",
        "FSETPfxu_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("FSETP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.FsetpfxuPipePredPredRegRegPred(dstPredicate, nPd, srcA, srcB, pa, modifiers, guard))
        case _ => None
      }
  }
  object FsetpfxuPipePredRegConst extends Definition(
        "FsetpfxuPipePredRegConst",
        "FSETPfxu_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("NoBop_Const_FSETP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.FsetpfxuPipePredRegConst(dstPredicate, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object FsetpfxuPipePredRegImm extends Definition(
        "FsetpfxuPipePredRegImm",
        "FSETPfxu_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate)
        ),
        Vec("NoBop_Imm_FSETP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.FsetpfxuPipePredRegImm(dstPredicate, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object FsetpfxuPipePredRegReg extends Definition(
        "FsetpfxuPipePredRegReg",
        "FSETPfxu_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register)
        ),
        Vec("NoBop_FSETP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.FsetpfxuPipePredRegReg(dstPredicate, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object FsetpintPipePredPredRegAddressImmPred extends Definition(
        "FsetpintPipePredPredRegAddressImmPred",
        "FSETPint_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("fsetp__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Predicate(pp)
            ) =>
          Some(Instruction.FsetpintPipePredPredRegAddressImmPred(pu, pv, srcA, srcB, sb_offset, pp, modifiers, guard))
        case _ => None
      }
  }
  object FsetpintPipePredPredRegConstPred extends Definition(
        "FsetpintPipePredPredRegConstPred",
        "FSETPint_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("fsetp__RCR_RC", "fsetp__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.FsetpintPipePredPredRegConstPred(pu, pv, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object FsetpintPipePredPredRegImmPred extends Definition(
        "FsetpintPipePredPredRegImmPred",
        "FSETPint_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("fsetp__RIR_RI", "fsetp__RIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.FsetpintPipePredPredRegImmPred(pu, pv, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object FsetpintPipePredPredRegRegPred extends Definition(
        "FsetpintPipePredPredRegRegPred",
        "FSETPint_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("fsetp__RRR_RR", "fsetp__RRR_RRR", "fsetp__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.FsetpintPipePredPredRegRegPred(pu, pv, srcA, srcB, pp, modifiers, guard))
        case _ => None
      }
  }
  object FsetpintPipePredRegAddressImm extends Definition(
        "FsetpintPipePredRegAddressImm",
        "FSETPint_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("fsetp_simple__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.FsetpintPipePredRegAddressImm(pu, srcA, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object FsetpintPipePredRegConst extends Definition(
        "FsetpintPipePredRegConst",
        "FSETPint_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank)
        ),
        Vec("fsetp_simple__RCR_RC", "fsetp_simple__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb)
            ) => Some(Instruction.FsetpintPipePredRegConst(pu, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object FsetpintPipePredRegImm extends Definition(
        "FsetpintPipePredRegImm",
        "FSETPint_pipe",
        Vec(Slot("pu", OperandKind.Predicate), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("fsetp_simple__RIR_RI", "fsetp_simple__RIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.FsetpintPipePredRegImm(pu, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object FsetpintPipePredRegReg extends Definition(
        "FsetpintPipePredRegReg",
        "FSETPint_pipe",
        Vec(Slot("pu", OperandKind.Predicate), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("fsetp_simple__RRR_RR", "fsetp_simple__RRR_RRR", "fsetp_simple__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.FsetpintPipePredRegReg(pu, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object FsetfauPipeRegRegConst extends Definition(
        "FsetfauPipeRegRegConst",
        "FSETfau_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("NoBop Const FSET")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.FsetfauPipeRegRegConst(dst, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object FsetfauPipeRegRegConstPred extends Definition(
        "FsetfauPipeRegRegConstPred",
        "FSETfau_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Const FSET")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.FsetfauPipeRegRegConstPred(dst, srcA, srcConst, pa, modifiers, guard))
        case _ => None
      }
  }
  object FsetfauPipeRegRegImm extends Definition(
        "FsetfauPipeRegRegImm",
        "FSETfau_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("NoBop Imm FSET")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.FsetfauPipeRegRegImm(dst, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object FsetfauPipeRegRegImmPred extends Definition(
        "FsetfauPipeRegRegImmPred",
        "FSETfau_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Imm FSET")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.FsetfauPipeRegRegImmPred(dst, srcA, uImm, pa, modifiers, guard))
        case _ => None
      }
  }
  object FsetfauPipeRegRegReg extends Definition(
        "FsetfauPipeRegRegReg",
        "FSETfau_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("NoBop FSET")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.FsetfauPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object FsetfauPipeRegRegRegPred extends Definition(
        "FsetfauPipeRegRegRegPred",
        "FSETfau_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("FSET")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.FsetfauPipeRegRegRegPred(dst, srcA, srcB, pa, modifiers, guard))
        case _ => None
      }
  }
  object FsetfmaxPipeRegRegConst extends Definition(
        "FsetfmaxPipeRegRegConst",
        "FSETfmaX_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("NoBop Const FSET")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.FsetfmaxPipeRegRegConst(dst, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object FsetfmaxPipeRegRegConstPred extends Definition(
        "FsetfmaxPipeRegRegConstPred",
        "FSETfmaX_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Const FSET")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.FsetfmaxPipeRegRegConstPred(dst, srcA, srcConst, pa, modifiers, guard))
        case _ => None
      }
  }
  object FsetfmaxPipeRegRegImm extends Definition(
        "FsetfmaxPipeRegRegImm",
        "FSETfmaX_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("NoBop Imm FSET")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.FsetfmaxPipeRegRegImm(dst, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object FsetfmaxPipeRegRegImmPred extends Definition(
        "FsetfmaxPipeRegRegImmPred",
        "FSETfmaX_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Imm FSET")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.FsetfmaxPipeRegRegImmPred(dst, srcA, uImm, pa, modifiers, guard))
        case _ => None
      }
  }
  object FsetfmaxPipeRegRegReg extends Definition(
        "FsetfmaxPipeRegRegReg",
        "FSETfmaX_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("NoBop FSET")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.FsetfmaxPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object FsetfmaxPipeRegRegRegPred extends Definition(
        "FsetfmaxPipeRegRegRegPred",
        "FSETfmaX_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("FSET")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.FsetfmaxPipeRegRegRegPred(dst, srcA, srcB, pa, modifiers, guard))
        case _ => None
      }
  }
  object FsetfxuPipeRegRegConst extends Definition(
        "FsetfxuPipeRegRegConst",
        "FSETfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("NoBop_Const_FSET")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.FsetfxuPipeRegRegConst(dst, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object FsetfxuPipeRegRegConstPred extends Definition(
        "FsetfxuPipeRegRegConstPred",
        "FSETfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Const_FSET")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.FsetfxuPipeRegRegConstPred(dst, srcA, srcConst, pa, modifiers, guard))
        case _ => None
      }
  }
  object FsetfxuPipeRegRegImm extends Definition(
        "FsetfxuPipeRegRegImm",
        "FSETfxu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("NoBop_Imm_FSET")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.FsetfxuPipeRegRegImm(dst, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object FsetfxuPipeRegRegImmPred extends Definition(
        "FsetfxuPipeRegRegImmPred",
        "FSETfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Imm_FSET")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.FsetfxuPipeRegRegImmPred(dst, srcA, uImm, pa, modifiers, guard))
        case _ => None
      }
  }
  object FsetfxuPipeRegRegReg extends Definition(
        "FsetfxuPipeRegRegReg",
        "FSETfxu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("NoBop_FSET")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.FsetfxuPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object FsetfxuPipeRegRegRegPred extends Definition(
        "FsetfxuPipeRegRegRegPred",
        "FSETfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("FSET")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.FsetfxuPipeRegRegRegPred(dst, srcA, srcB, pa, modifiers, guard))
        case _ => None
      }
  }
  object FsetintPipeRegRegAddressImm extends Definition(
        "FsetintPipeRegRegAddressImm",
        "FSETint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("fset_simple__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.FsetintPipeRegRegAddressImm(dst, srcA, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object FsetintPipeRegRegAddressImmPred extends Definition(
        "FsetintPipeRegRegAddressImmPred",
        "FSETint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("fset__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.FsetintPipeRegRegAddressImmPred(dst, srcA, srcB, sb_offset, pp, modifiers, guard))
        case _ => None
      }
  }
  object FsetintPipeRegRegConst extends Definition(
        "FsetintPipeRegRegConst",
        "FSETint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank)
        ),
        Vec("fset_simple__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb)
            ) => Some(Instruction.FsetintPipeRegRegConst(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object FsetintPipeRegRegConstPred extends Definition(
        "FsetintPipeRegRegConstPred",
        "FSETint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("fset__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.FsetintPipeRegRegConstPred(dst, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object FsetintPipeRegRegImm extends Definition(
        "FsetintPipeRegRegImm",
        "FSETint_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("fset_simple__RIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.FsetintPipeRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object FsetintPipeRegRegImmPred extends Definition(
        "FsetintPipeRegRegImmPred",
        "FSETint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("fset__RIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.FsetintPipeRegRegImmPred(dst, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object FsetintPipeRegRegReg extends Definition(
        "FsetintPipeRegRegReg",
        "FSETint_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("fset_simple__RRR_RRR", "fset_simple__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.FsetintPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object FsetintPipeRegRegRegPred extends Definition(
        "FsetintPipeRegRegRegPred",
        "FSETint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("fset__RRR_RRR", "fset__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.FsetintPipeRegRegRegPred(dst, srcA, srcB, pp, modifiers, guard))
        case _ => None
      }
  }
  object FswzRegRegReg extends Definition(
        "FswzRegRegReg",
        "FSWZ",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("FSWZ")
      ) {
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
  object FswzaddRegRegReg extends Definition(
        "FswzaddRegRegReg",
        "FSWZADD",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("FSWZADD", "fswzadd_")
      ) {
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
  object FswzaddfmalighterPipeRegRegReg extends Definition(
        "FswzaddfmalighterPipeRegRegReg",
        "FSWZADDfmalighter_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcC", OperandKind.Register)),
        Vec("fswzadd_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.FswzaddfmalighterPipeRegRegReg(dst, srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FswzaddfxuPipeRegRegReg extends Definition(
        "FswzaddfxuPipeRegRegReg",
        "FSWZADDfxu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("FSWZADD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.FswzaddfxuPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object FswzfmaxPipeRegRegReg extends Definition(
        "FswzfmaxPipeRegRegReg",
        "FSWZfmaX_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("FSWZ")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.FswzfmaxPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object GatherRegRegRegRegImm extends Definition(
        "GatherRegRegRegRegImm",
        "GATHER",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("mdidx", OperandKind.Immediate)
        ),
        Vec("gather_")
      ) {
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
  object GatherRegRegRegRegImmImm extends Definition(
        "GatherRegRegRegRegImmImm",
        "GATHER",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("mdidx", OperandKind.Immediate),
          Slot("dstbyte", OperandKind.Immediate)
        ),
        Vec("gather_")
      ) {
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
  object GatherRegRegRegRegImmImmImm extends Definition(
        "GatherRegRegRegRegImmImmImm",
        "GATHER",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("mdidx", OperandKind.Immediate),
          Slot("dstbyte", OperandKind.Immediate),
          Slot("srchalf", OperandKind.Immediate)
        ),
        Vec("gather_")
      ) {
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
  object GatherintPipeRegRegRegRegImm extends Definition(
        "GatherintPipeRegRegRegRegImm",
        "GATHERint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("mdidx", OperandKind.Immediate)
        ),
        Vec("gather_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(mdidx)
            ) => Some(Instruction.GatherintPipeRegRegRegRegImm(dst, srcA, srcB, srcC, mdidx, modifiers, guard))
        case _ => None
      }
  }
  object GatherintPipeRegRegRegRegImmImm extends Definition(
        "GatherintPipeRegRegRegRegImmImm",
        "GATHERint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("mdidx", OperandKind.Immediate),
          Slot("dstbyte", OperandKind.Immediate)
        ),
        Vec("gather_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(mdidx),
              InstructionOperand.Immediate(dstbyte)
            ) =>
          Some(Instruction.GatherintPipeRegRegRegRegImmImm(dst, srcA, srcB, srcC, mdidx, dstbyte, modifiers, guard))
        case _ => None
      }
  }
  object GatherintPipeRegRegRegRegImmImmImm extends Definition(
        "GatherintPipeRegRegRegRegImmImmImm",
        "GATHERint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("mdidx", OperandKind.Immediate),
          Slot("dstbyte", OperandKind.Immediate),
          Slot("srchalf", OperandKind.Immediate)
        ),
        Vec("gather_")
      ) {
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
            ) => Some(Instruction.GatherintPipeRegRegRegRegImmImmImm(
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
  object GetcrsptrReg
      extends Definition("GetcrsptrReg", "GETCRSPTR", Vec(Slot("dst", OperandKind.Register)), Vec("GETCRSPTR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst)) => Some(Instruction.GetcrsptrReg(dst, modifiers, guard))
        case _ => None
      }
  }
  object GetcrsptrbruPipeReg extends Definition(
        "GetcrsptrbruPipeReg",
        "GETCRSPTRbru_pipe",
        Vec(Slot("dst", OperandKind.Register)),
        Vec("GETCRSPTR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst)) => Some(Instruction.GetcrsptrbruPipeReg(dst, modifiers, guard))
        case _ => None
      }
  }
  object GetcrsptrmioPipeReg extends Definition(
        "GetcrsptrmioPipeReg",
        "GETCRSPTRmio_pipe",
        Vec(Slot("dst", OperandKind.Register)),
        Vec("GETCRSPTR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst)) => Some(Instruction.GetcrsptrmioPipeReg(dst, modifiers, guard))
        case _ => None
      }
  }
  object GetlmembaseReg extends Definition(
        "GetlmembaseReg",
        "GETLMEMBASE",
        Vec(Slot("dst", OperandKind.Register)),
        Vec("GETLMEMBASE", "getlmembase_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst)) => Some(Instruction.GetlmembaseReg(dst, modifiers, guard))
        case _ => None
      }
  }
  object GetlmembaseaguPipeReg extends Definition(
        "GetlmembaseaguPipeReg",
        "GETLMEMBASEagu_pipe",
        Vec(Slot("dst", OperandKind.Register)),
        Vec("GETLMEMBASE")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst)) => Some(Instruction.GetlmembaseaguPipeReg(dst, modifiers, guard))
        case _ => None
      }
  }
  object GetlmembasemioPipeReg extends Definition(
        "GetlmembasemioPipeReg",
        "GETLMEMBASEmio_pipe",
        Vec(Slot("dst", OperandKind.Register)),
        Vec("GETLMEMBASE", "getlmembase_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst)) => Some(Instruction.GetlmembasemioPipeReg(dst, modifiers, guard))
        case _ => None
      }
  }
  object Hadd2NoOperands extends Definition("Hadd2NoOperands", "HADD2", Vec(), Vec("hadd2__v2")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.Hadd2NoOperands(modifiers, guard))
        case _ => None
      }
  }
  object Hadd2Const
      extends Definition("Hadd2Const", "HADD2", Vec(Slot("sb", OperandKind.ConstantBank)), Vec("hadd2__v0")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(sb)) => Some(Instruction.Hadd2Const(sb, modifiers, guard))
        case _ => None
      }
  }
  object Hadd2ImmImm extends Definition(
        "Hadd2ImmImm",
        "HADD2",
        Vec(Slot("sb", OperandKind.Immediate), Slot("sb2", OperandKind.Immediate)),
        Vec("hadd2__v1")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb), InstructionOperand.Immediate(sb2)) =>
          Some(Instruction.Hadd2ImmImm(sb, sb2, modifiers, guard))
        case _ => None
      }
  }
  object Hadd2RegRegAddressImm extends Definition(
        "Hadd2RegRegAddressImm",
        "HADD2",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcC", OperandKind.Address),
          Slot("sc_offset", OperandKind.Immediate)
        ),
        Vec("hadd2_F32__RCx", "hadd2__RCx")
      ) {
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
  object Hadd2RegRegConst extends Definition(
        "Hadd2RegRegConst",
        "HADD2",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sc", OperandKind.ConstantBank)
        ),
        Vec("hadd2_F32__RC", "hadd2__RC")
      ) {
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
  object Hadd2RegRegImm extends Definition(
        "Hadd2RegRegImm",
        "HADD2",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sc", OperandKind.Immediate)),
        Vec("hadd2_F32__RI")
      ) {
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
  object Hadd2RegRegImmImm extends Definition(
        "Hadd2RegRegImmImm",
        "HADD2",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("sc", OperandKind.Immediate)
        ),
        Vec("hadd2_F32i_", "hadd2__RI", "hadd2_fixed__RI")
      ) {
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
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcC", OperandKind.Register)),
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
  object Hadd232iImmImm extends Definition(
        "Hadd232iImmImm",
        "HADD2_32I",
        Vec(Slot("imm", OperandKind.Immediate), Slot("imm2", OperandKind.Immediate)),
        Vec("hadd2_32i_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(imm), InstructionOperand.Immediate(imm2)) =>
          Some(Instruction.Hadd232iImmImm(imm, imm2, modifiers, guard))
        case _ => None
      }
  }
  object Hadd232iRegRegImmImm extends Definition(
        "Hadd232iRegRegImmImm",
        "HADD2_32I",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sc", OperandKind.Immediate),
          Slot("sc1", OperandKind.Immediate)
        ),
        Vec("hadd2_32i_", "hadd2_32i_fixed_")
      ) {
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
  object Hadd232ifp16PipeRegRegImmImm extends Definition(
        "Hadd232ifp16PipeRegRegImmImm",
        "HADD2_32Ifp16_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sc", OperandKind.Immediate),
          Slot("sc1", OperandKind.Immediate)
        ),
        Vec("hadd2_32i_", "hadd2_32i_fixed_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sc),
              InstructionOperand.Immediate(sc1)
            ) => Some(Instruction.Hadd232ifp16PipeRegRegImmImm(dst, srcA, sc, sc1, modifiers, guard))
        case _ => None
      }
  }
  object Hadd232ifp16g0PipeImmImm extends Definition(
        "Hadd232ifp16g0PipeImmImm",
        "HADD2_32Ifp16g0_pipe",
        Vec(Slot("imm", OperandKind.Immediate), Slot("imm2", OperandKind.Immediate)),
        Vec("hadd2_32i_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(imm), InstructionOperand.Immediate(imm2)) =>
          Some(Instruction.Hadd232ifp16g0PipeImmImm(imm, imm2, modifiers, guard))
        case _ => None
      }
  }
  object Hadd232ifp16g1PipeImmImm extends Definition(
        "Hadd232ifp16g1PipeImmImm",
        "HADD2_32Ifp16g1_pipe",
        Vec(Slot("imm", OperandKind.Immediate), Slot("imm2", OperandKind.Immediate)),
        Vec("hadd2_32i_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(imm), InstructionOperand.Immediate(imm2)) =>
          Some(Instruction.Hadd232ifp16g1PipeImmImm(imm, imm2, modifiers, guard))
        case _ => None
      }
  }
  object Hadd2fp16PipeRegRegAddressImm extends Definition(
        "Hadd2fp16PipeRegRegAddressImm",
        "HADD2fp16_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcC", OperandKind.Address),
          Slot("sc_offset", OperandKind.Immediate)
        ),
        Vec("hadd2_F32__RCx", "hadd2__RCx")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcC),
              InstructionOperand.Immediate(sc_offset)
            ) => Some(Instruction.Hadd2fp16PipeRegRegAddressImm(dst, srcA, srcC, sc_offset, modifiers, guard))
        case _ => None
      }
  }
  object Hadd2fp16PipeRegRegConst extends Definition(
        "Hadd2fp16PipeRegRegConst",
        "HADD2fp16_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sc", OperandKind.ConstantBank)
        ),
        Vec("hadd2_F32__RC", "hadd2__RC")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sc)
            ) => Some(Instruction.Hadd2fp16PipeRegRegConst(dst, srcA, sc, modifiers, guard))
        case _ => None
      }
  }
  object Hadd2fp16PipeRegRegImm extends Definition(
        "Hadd2fp16PipeRegRegImm",
        "HADD2fp16_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sc", OperandKind.Immediate)),
        Vec("hadd2_F32__RI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.Hadd2fp16PipeRegRegImm(dst, srcA, sc, modifiers, guard))
        case _ => None
      }
  }
  object Hadd2fp16PipeRegRegImmImm extends Definition(
        "Hadd2fp16PipeRegRegImmImm",
        "HADD2fp16_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("sc", OperandKind.Immediate)
        ),
        Vec("hadd2_F32i_", "hadd2__RI", "hadd2_fixed__RI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.Hadd2fp16PipeRegRegImmImm(dst, srcA, sb, sc, modifiers, guard))
        case _ => None
      }
  }
  object Hadd2fp16PipeRegRegReg extends Definition(
        "Hadd2fp16PipeRegRegReg",
        "HADD2fp16_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcC", OperandKind.Register)),
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
            ) => Some(Instruction.Hadd2fp16PipeRegRegReg(dst, srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Hadd2fp16g0PipeNoOperands
      extends Definition("Hadd2fp16g0PipeNoOperands", "HADD2fp16g0_pipe", Vec(), Vec("hadd2__v2")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.Hadd2fp16g0PipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object Hadd2fp16g0PipeConst extends Definition(
        "Hadd2fp16g0PipeConst",
        "HADD2fp16g0_pipe",
        Vec(Slot("sb", OperandKind.ConstantBank)),
        Vec("hadd2__v0")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(sb)) => Some(Instruction.Hadd2fp16g0PipeConst(sb, modifiers, guard))
        case _ => None
      }
  }
  object Hadd2fp16g0PipeImmImm extends Definition(
        "Hadd2fp16g0PipeImmImm",
        "HADD2fp16g0_pipe",
        Vec(Slot("sb", OperandKind.Immediate), Slot("sb2", OperandKind.Immediate)),
        Vec("hadd2__v1")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb), InstructionOperand.Immediate(sb2)) =>
          Some(Instruction.Hadd2fp16g0PipeImmImm(sb, sb2, modifiers, guard))
        case _ => None
      }
  }
  object Hadd2fp16g1PipeNoOperands
      extends Definition("Hadd2fp16g1PipeNoOperands", "HADD2fp16g1_pipe", Vec(), Vec("hadd2__v2")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.Hadd2fp16g1PipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object Hadd2fp16g1PipeConst extends Definition(
        "Hadd2fp16g1PipeConst",
        "HADD2fp16g1_pipe",
        Vec(Slot("sb", OperandKind.ConstantBank)),
        Vec("hadd2__v0")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(sb)) => Some(Instruction.Hadd2fp16g1PipeConst(sb, modifiers, guard))
        case _ => None
      }
  }
  object Hadd2fp16g1PipeImmImm extends Definition(
        "Hadd2fp16g1PipeImmImm",
        "HADD2fp16g1_pipe",
        Vec(Slot("sb", OperandKind.Immediate), Slot("sb2", OperandKind.Immediate)),
        Vec("hadd2__v1")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb), InstructionOperand.Immediate(sb2)) =>
          Some(Instruction.Hadd2fp16g1PipeImmImm(sb, sb2, modifiers, guard))
        case _ => None
      }
  }
  object Hfma2NoOperands extends Definition("Hfma2NoOperands", "HFMA2", Vec(), Vec("hfma2__v2")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.Hfma2NoOperands(modifiers, guard))
        case _ => None
      }
  }
  object Hfma2Const
      extends Definition("Hfma2Const", "HFMA2", Vec(Slot("sb", OperandKind.ConstantBank)), Vec("hfma2__v0")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(sb)) => Some(Instruction.Hfma2Const(sb, modifiers, guard))
        case _ => None
      }
  }
  object Hfma2ImmImm extends Definition(
        "Hfma2ImmImm",
        "HFMA2",
        Vec(Slot("sb", OperandKind.Immediate), Slot("sb2", OperandKind.Immediate)),
        Vec("hfma2__v1")
      ) {
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
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
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
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
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
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
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
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
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
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("sb1", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
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
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("sb1", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
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
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Address),
          Slot("sc_offset", OperandKind.Immediate)
        ),
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
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Address),
          Slot("sc_offset", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
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
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.ConstantBank)
        ),
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
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.ConstantBank),
          Slot("pp", OperandKind.Predicate)
        ),
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
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate),
          Slot("sc1", OperandKind.Immediate)
        ),
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
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate),
          Slot("sc1", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
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
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
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
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
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
  object Hfma232iRegRegImmImm extends Definition(
        "Hfma232iRegRegImmImm",
        "HFMA2_32I",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("fImm", OperandKind.Immediate),
          Slot("fImm1", OperandKind.Immediate)
        ),
        Vec("HFMA2_32I_2")
      ) {
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
  object Hfma232iRegRegImmImmReg extends Definition(
        "Hfma232iRegRegImmImmReg",
        "HFMA2_32I",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("sb1", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("HFMA2_32I", "hfma2_32i_", "hfma2_32i_fixed_")
      ) {
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
  object Hfma232ifp16PipeRegRegImmImmReg extends Definition(
        "Hfma232ifp16PipeRegRegImmImmReg",
        "HFMA2_32Ifp16_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("sb1", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("hfma2_32i_", "hfma2_32i_fixed_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Immediate(sb1),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Hfma232ifp16PipeRegRegImmImmReg(dst, srcA, sb, sb1, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Hfma232ifp16g0PipeRegRegImmImm extends Definition(
        "Hfma232ifp16g0PipeRegRegImmImm",
        "HFMA2_32Ifp16g0_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("fImm", OperandKind.Immediate),
          Slot("fImm1", OperandKind.Immediate)
        ),
        Vec("HFMA2_32I_2")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(fImm),
              InstructionOperand.Immediate(fImm1)
            ) => Some(Instruction.Hfma232ifp16g0PipeRegRegImmImm(dst, srcA, fImm, fImm1, modifiers, guard))
        case _ => None
      }
  }
  object Hfma232ifp16g0PipeRegRegImmImmReg extends Definition(
        "Hfma232ifp16g0PipeRegRegImmImmReg",
        "HFMA2_32Ifp16g0_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("fImm", OperandKind.Immediate),
          Slot("fImm1", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("HFMA2_32I")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(fImm),
              InstructionOperand.Immediate(fImm1),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Hfma232ifp16g0PipeRegRegImmImmReg(dst, srcA, fImm, fImm1, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Hfma232ifp16g1PipeRegRegImmImm extends Definition(
        "Hfma232ifp16g1PipeRegRegImmImm",
        "HFMA2_32Ifp16g1_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("fImm", OperandKind.Immediate),
          Slot("fImm1", OperandKind.Immediate)
        ),
        Vec("HFMA2_32I_2")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(fImm),
              InstructionOperand.Immediate(fImm1)
            ) => Some(Instruction.Hfma232ifp16g1PipeRegRegImmImm(dst, srcA, fImm, fImm1, modifiers, guard))
        case _ => None
      }
  }
  object Hfma232ifp16g1PipeRegRegImmImmReg extends Definition(
        "Hfma232ifp16g1PipeRegRegImmImmReg",
        "HFMA2_32Ifp16g1_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("fImm", OperandKind.Immediate),
          Slot("fImm1", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("HFMA2_32I")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(fImm),
              InstructionOperand.Immediate(fImm1),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Hfma232ifp16g1PipeRegRegImmImmReg(dst, srcA, fImm, fImm1, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Hfma2fp16PipeRegRegAddressImmReg extends Definition(
        "Hfma2fp16PipeRegRegAddressImmReg",
        "HFMA2fp16_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("hfma2__RCxR", "hfma2_relu__RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Hfma2fp16PipeRegRegAddressImmReg(dst, srcA, srcB, sb_offset, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Hfma2fp16PipeRegRegAddressImmRegPred extends Definition(
        "Hfma2fp16PipeRegRegAddressImmRegPred",
        "HFMA2fp16_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("hfma2_relu__RCxR")
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
            ) =>
          Some(Instruction.Hfma2fp16PipeRegRegAddressImmRegPred(dst, srcA, srcB, sb_offset, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object Hfma2fp16PipeRegRegConstReg extends Definition(
        "Hfma2fp16PipeRegRegConstReg",
        "HFMA2fp16_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("hfma2__RCR", "hfma2_relu__RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Hfma2fp16PipeRegRegConstReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Hfma2fp16PipeRegRegConstRegPred extends Definition(
        "Hfma2fp16PipeRegRegConstRegPred",
        "HFMA2fp16_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("hfma2_relu__RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Hfma2fp16PipeRegRegConstRegPred(dst, srcA, sb, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object Hfma2fp16PipeRegRegImmImmReg extends Definition(
        "Hfma2fp16PipeRegRegImmImmReg",
        "HFMA2fp16_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("sb1", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec(
          "hfma2__RIR",
          "hfma2_fixed__RIR",
          "hfma2_mma__RIR",
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
            ) => Some(Instruction.Hfma2fp16PipeRegRegImmImmReg(dst, srcA, sb, sb1, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Hfma2fp16PipeRegRegImmImmRegPred extends Definition(
        "Hfma2fp16PipeRegRegImmImmRegPred",
        "HFMA2fp16_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("sb1", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("hfma2_mma_relu__RIR", "hfma2_relu__RIR", "hfma2_relu_fixed__RIR")
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
            ) => Some(Instruction.Hfma2fp16PipeRegRegImmImmRegPred(dst, srcA, sb, sb1, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object Hfma2fp16PipeRegRegRegAddressImm extends Definition(
        "Hfma2fp16PipeRegRegRegAddressImm",
        "HFMA2fp16_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Address),
          Slot("sc_offset", OperandKind.Immediate)
        ),
        Vec("hfma2__RRCx", "hfma2_relu__RRCx")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Address(srcC),
              InstructionOperand.Immediate(sc_offset)
            ) => Some(Instruction.Hfma2fp16PipeRegRegRegAddressImm(dst, srcA, srcB, srcC, sc_offset, modifiers, guard))
        case _ => None
      }
  }
  object Hfma2fp16PipeRegRegRegAddressImmPred extends Definition(
        "Hfma2fp16PipeRegRegRegAddressImmPred",
        "HFMA2fp16_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Address),
          Slot("sc_offset", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("hfma2_relu__RRCx")
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
            ) =>
          Some(Instruction.Hfma2fp16PipeRegRegRegAddressImmPred(dst, srcA, srcB, srcC, sc_offset, pp, modifiers, guard))
        case _ => None
      }
  }
  object Hfma2fp16PipeRegRegRegConst extends Definition(
        "Hfma2fp16PipeRegRegRegConst",
        "HFMA2fp16_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.ConstantBank)
        ),
        Vec("hfma2__RRC", "hfma2_relu__RRC")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(sc)
            ) => Some(Instruction.Hfma2fp16PipeRegRegRegConst(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object Hfma2fp16PipeRegRegRegConstPred extends Definition(
        "Hfma2fp16PipeRegRegRegConstPred",
        "HFMA2fp16_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.ConstantBank),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("hfma2_relu__RRC")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(sc),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Hfma2fp16PipeRegRegRegConstPred(dst, srcA, srcB, sc, pp, modifiers, guard))
        case _ => None
      }
  }
  object Hfma2fp16PipeRegRegRegImmImm extends Definition(
        "Hfma2fp16PipeRegRegRegImmImm",
        "HFMA2fp16_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate),
          Slot("sc1", OperandKind.Immediate)
        ),
        Vec(
          "hfma2__RRI",
          "hfma2_fixed__RRI",
          "hfma2_mma__RRI",
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
            ) => Some(Instruction.Hfma2fp16PipeRegRegRegImmImm(dst, srcA, srcB, sc, sc1, modifiers, guard))
        case _ => None
      }
  }
  object Hfma2fp16PipeRegRegRegImmImmPred extends Definition(
        "Hfma2fp16PipeRegRegRegImmImmPred",
        "HFMA2fp16_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate),
          Slot("sc1", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("hfma2_mma_relu__RRI", "hfma2_relu__RRI", "hfma2_relu_fixed__RRI")
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
            ) => Some(Instruction.Hfma2fp16PipeRegRegRegImmImmPred(dst, srcA, srcB, sc, sc1, pp, modifiers, guard))
        case _ => None
      }
  }
  object Hfma2fp16PipeRegRegRegReg extends Definition(
        "Hfma2fp16PipeRegRegRegReg",
        "HFMA2fp16_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec(
          "hfma2__RRR",
          "hfma2__RRU",
          "hfma2__RUR",
          "hfma2_fixed__RRR",
          "hfma2_fixed__RRU",
          "hfma2_fixed__RUR",
          "hfma2_mma__RRR",
          "hfma2_mma__RRU",
          "hfma2_mma__RUR",
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
            ) => Some(Instruction.Hfma2fp16PipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Hfma2fp16PipeRegRegRegRegPred extends Definition(
        "Hfma2fp16PipeRegRegRegRegPred",
        "HFMA2fp16_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec(
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
            ) => Some(Instruction.Hfma2fp16PipeRegRegRegRegPred(dst, srcA, srcB, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object Hfma2fp16g0PipeNoOperands
      extends Definition("Hfma2fp16g0PipeNoOperands", "HFMA2fp16g0_pipe", Vec(), Vec("hfma2__v2")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.Hfma2fp16g0PipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object Hfma2fp16g0PipeConst extends Definition(
        "Hfma2fp16g0PipeConst",
        "HFMA2fp16g0_pipe",
        Vec(Slot("sb", OperandKind.ConstantBank)),
        Vec("hfma2__v0")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(sb)) => Some(Instruction.Hfma2fp16g0PipeConst(sb, modifiers, guard))
        case _ => None
      }
  }
  object Hfma2fp16g0PipeImmImm extends Definition(
        "Hfma2fp16g0PipeImmImm",
        "HFMA2fp16g0_pipe",
        Vec(Slot("sb", OperandKind.Immediate), Slot("sb2", OperandKind.Immediate)),
        Vec("hfma2__v1")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb), InstructionOperand.Immediate(sb2)) =>
          Some(Instruction.Hfma2fp16g0PipeImmImm(sb, sb2, modifiers, guard))
        case _ => None
      }
  }
  object Hfma2fp16g0PipeRegRegRegConst extends Definition(
        "Hfma2fp16g0PipeRegRegRegConst",
        "HFMA2fp16g0_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("HFMA2_CCST")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.Hfma2fp16g0PipeRegRegRegConst(dst, srcA, srcB, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object Hfma2fp16g1PipeNoOperands
      extends Definition("Hfma2fp16g1PipeNoOperands", "HFMA2fp16g1_pipe", Vec(), Vec("hfma2__v2")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.Hfma2fp16g1PipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object Hfma2fp16g1PipeConst extends Definition(
        "Hfma2fp16g1PipeConst",
        "HFMA2fp16g1_pipe",
        Vec(Slot("sb", OperandKind.ConstantBank)),
        Vec("hfma2__v0")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(sb)) => Some(Instruction.Hfma2fp16g1PipeConst(sb, modifiers, guard))
        case _ => None
      }
  }
  object Hfma2fp16g1PipeImmImm extends Definition(
        "Hfma2fp16g1PipeImmImm",
        "HFMA2fp16g1_pipe",
        Vec(Slot("sb", OperandKind.Immediate), Slot("sb2", OperandKind.Immediate)),
        Vec("hfma2__v1")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb), InstructionOperand.Immediate(sb2)) =>
          Some(Instruction.Hfma2fp16g1PipeImmImm(sb, sb2, modifiers, guard))
        case _ => None
      }
  }
  object Hfma2fp16g1PipeRegRegRegConst extends Definition(
        "Hfma2fp16g1PipeRegRegRegConst",
        "HFMA2fp16g1_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("HFMA2_CCST")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.Hfma2fp16g1PipeRegRegRegConst(dst, srcA, srcB, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object HgmmaRegAddressReg extends Definition(
        "HgmmaRegAddressReg",
        "HGMMA",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address), Slot("srcC", OperandKind.Register)),
        Vec("hgmma_URa_Rc_")
      ) {
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
  object HgmmaRegAddressRegPred extends Definition(
        "HgmmaRegAddressRegPred",
        "HGMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("hgmma_URa_Rc_")
      ) {
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
  object HgmmaRegAddressRegPredRegImm extends Definition(
        "HgmmaRegAddressRegPredRegImm",
        "HGMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("re", OperandKind.Register),
          Slot("id", OperandKind.Immediate)
        ),
        Vec("hgmma_sparse_URa_Rc_")
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
            ) => Some(Instruction.HgmmaRegAddressRegPredRegImm(dst, srcA, srcC, uPp, re, id, modifiers, guard))
        case _ => None
      }
  }
  object HgmmaRegAddressRegReg extends Definition(
        "HgmmaRegAddressRegReg",
        "HGMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("hgmma_URa_Rb_Rc_")
      ) {
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
  object HgmmaRegAddressRegRegImm extends Definition(
        "HgmmaRegAddressRegRegImm",
        "HGMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("re", OperandKind.Register),
          Slot("id", OperandKind.Immediate)
        ),
        Vec("hgmma_sparse_URa_Rc_")
      ) {
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
  object HgmmaRegAddressRegRegPred extends Definition(
        "HgmmaRegAddressRegRegPred",
        "HGMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("hgmma_URa_Rb_Rc_")
      ) {
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
  object HgmmaRegAddressRegRegPredRegImm extends Definition(
        "HgmmaRegAddressRegRegPredRegImm",
        "HGMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("re", OperandKind.Register),
          Slot("id", OperandKind.Immediate)
        ),
        Vec("hgmma_sparse_URa_Rb_Rc_")
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
            ) => Some(Instruction.HgmmaRegAddressRegRegPredRegImm(dst, srcA, srcB, srcC, uPp, re, id, modifiers, guard))
        case _ => None
      }
  }
  object HgmmaRegAddressRegRegRegImm extends Definition(
        "HgmmaRegAddressRegRegRegImm",
        "HGMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("re", OperandKind.Register),
          Slot("id", OperandKind.Immediate)
        ),
        Vec("hgmma_sparse_URa_Rb_Rc_")
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
            ) => Some(Instruction.HgmmaRegAddressRegRegRegImm(dst, srcA, srcB, srcC, re, id, modifiers, guard))
        case _ => None
      }
  }
  object HgmmaRegRegAddressReg extends Definition(
        "HgmmaRegRegAddressReg",
        "HGMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("hgmma_Ra_URb_Rc_")
      ) {
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
  object HgmmaRegRegAddressRegPred extends Definition(
        "HgmmaRegRegAddressRegPred",
        "HGMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("hgmma_Ra_URb_Rc_")
      ) {
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
  object HgmmaRegRegAddressRegPredRegImm extends Definition(
        "HgmmaRegRegAddressRegPredRegImm",
        "HGMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("re", OperandKind.Register),
          Slot("id", OperandKind.Immediate)
        ),
        Vec("hgmma_sparse_Ra_URb_Rc_")
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
            ) => Some(Instruction.HgmmaRegRegAddressRegPredRegImm(dst, srcA, srcB, srcC, uPp, re, id, modifiers, guard))
        case _ => None
      }
  }
  object HgmmaRegRegAddressRegRegImm extends Definition(
        "HgmmaRegRegAddressRegRegImm",
        "HGMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("re", OperandKind.Register),
          Slot("id", OperandKind.Immediate)
        ),
        Vec("hgmma_sparse_Ra_URb_Rc_")
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
            ) => Some(Instruction.HgmmaRegRegAddressRegRegImm(dst, srcA, srcB, srcC, re, id, modifiers, guard))
        case _ => None
      }
  }
  object HgmmamioPipeRegAddressReg extends Definition(
        "HgmmamioPipeRegAddressReg",
        "HGMMAmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address), Slot("srcC", OperandKind.Register)),
        Vec("hgmma_URa_Rc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.HgmmamioPipeRegAddressReg(dst, srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object HgmmamioPipeRegAddressRegPred extends Definition(
        "HgmmamioPipeRegAddressRegPred",
        "HGMMAmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("hgmma_URa_Rc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.HgmmamioPipeRegAddressRegPred(dst, srcA, srcC, uPp, modifiers, guard))
        case _ => None
      }
  }
  object HgmmamioPipeRegAddressRegPredRegImm extends Definition(
        "HgmmamioPipeRegAddressRegPredRegImm",
        "HGMMAmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("re", OperandKind.Register),
          Slot("id", OperandKind.Immediate)
        ),
        Vec("hgmma_sparse_URa_Rc_")
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
            ) => Some(Instruction.HgmmamioPipeRegAddressRegPredRegImm(dst, srcA, srcC, uPp, re, id, modifiers, guard))
        case _ => None
      }
  }
  object HgmmamioPipeRegAddressRegReg extends Definition(
        "HgmmamioPipeRegAddressRegReg",
        "HGMMAmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("hgmma_URa_Rb_Rc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.HgmmamioPipeRegAddressRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object HgmmamioPipeRegAddressRegRegImm extends Definition(
        "HgmmamioPipeRegAddressRegRegImm",
        "HGMMAmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("re", OperandKind.Register),
          Slot("id", OperandKind.Immediate)
        ),
        Vec("hgmma_sparse_URa_Rc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(re),
              InstructionOperand.Immediate(id)
            ) => Some(Instruction.HgmmamioPipeRegAddressRegRegImm(dst, srcA, srcC, re, id, modifiers, guard))
        case _ => None
      }
  }
  object HgmmamioPipeRegAddressRegRegPred extends Definition(
        "HgmmamioPipeRegAddressRegRegPred",
        "HGMMAmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("hgmma_URa_Rb_Rc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.HgmmamioPipeRegAddressRegRegPred(dst, srcA, srcB, srcC, uPp, modifiers, guard))
        case _ => None
      }
  }
  object HgmmamioPipeRegAddressRegRegPredRegImm extends Definition(
        "HgmmamioPipeRegAddressRegRegPredRegImm",
        "HGMMAmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("re", OperandKind.Register),
          Slot("id", OperandKind.Immediate)
        ),
        Vec("hgmma_sparse_URa_Rb_Rc_")
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
          Some(Instruction.HgmmamioPipeRegAddressRegRegPredRegImm(dst, srcA, srcB, srcC, uPp, re, id, modifiers, guard))
        case _ => None
      }
  }
  object HgmmamioPipeRegAddressRegRegRegImm extends Definition(
        "HgmmamioPipeRegAddressRegRegRegImm",
        "HGMMAmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("re", OperandKind.Register),
          Slot("id", OperandKind.Immediate)
        ),
        Vec("hgmma_sparse_URa_Rb_Rc_")
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
            ) => Some(Instruction.HgmmamioPipeRegAddressRegRegRegImm(dst, srcA, srcB, srcC, re, id, modifiers, guard))
        case _ => None
      }
  }
  object HgmmamioPipeRegRegAddressReg extends Definition(
        "HgmmamioPipeRegRegAddressReg",
        "HGMMAmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("hgmma_Ra_URb_Rc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.HgmmamioPipeRegRegAddressReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object HgmmamioPipeRegRegAddressRegPred extends Definition(
        "HgmmamioPipeRegRegAddressRegPred",
        "HGMMAmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("hgmma_Ra_URb_Rc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.HgmmamioPipeRegRegAddressRegPred(dst, srcA, srcB, srcC, uPp, modifiers, guard))
        case _ => None
      }
  }
  object HgmmamioPipeRegRegAddressRegPredRegImm extends Definition(
        "HgmmamioPipeRegRegAddressRegPredRegImm",
        "HGMMAmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("re", OperandKind.Register),
          Slot("id", OperandKind.Immediate)
        ),
        Vec("hgmma_sparse_Ra_URb_Rc_")
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
          Some(Instruction.HgmmamioPipeRegRegAddressRegPredRegImm(dst, srcA, srcB, srcC, uPp, re, id, modifiers, guard))
        case _ => None
      }
  }
  object HgmmamioPipeRegRegAddressRegRegImm extends Definition(
        "HgmmamioPipeRegRegAddressRegRegImm",
        "HGMMAmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("re", OperandKind.Register),
          Slot("id", OperandKind.Immediate)
        ),
        Vec("hgmma_sparse_Ra_URb_Rc_")
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
            ) => Some(Instruction.HgmmamioPipeRegRegAddressRegRegImm(dst, srcA, srcB, srcC, re, id, modifiers, guard))
        case _ => None
      }
  }
  object HmmaAddressRegRegAddress extends Definition(
        "HmmaAddressRegRegAddress",
        "HMMA",
        Vec(
          Slot("dst", OperandKind.Address),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Address)
        ),
        Vec("hmma_x8_indexedRF_")
      ) {
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
  object HmmaAddressRegRegAddressPred extends Definition(
        "HmmaAddressRegRegAddressPred",
        "HMMA",
        Vec(
          Slot("dst", OperandKind.Address),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Address),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("hmma_x8_indexedRF_")
      ) {
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
  object HmmaAddressRegRegAddressPredRegImm extends Definition(
        "HmmaAddressRegRegAddressPredRegImm",
        "HMMA",
        Vec(
          Slot("dst", OperandKind.Address),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Address),
          Slot("uPp", OperandKind.Predicate),
          Slot("re", OperandKind.Register),
          Slot("id", OperandKind.Immediate)
        ),
        Vec("hmma_sparse_indexedRF_")
      ) {
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
  object HmmaAddressRegRegAddressRegImm extends Definition(
        "HmmaAddressRegRegAddressRegImm",
        "HMMA",
        Vec(
          Slot("dst", OperandKind.Address),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Address),
          Slot("re", OperandKind.Register),
          Slot("id", OperandKind.Immediate)
        ),
        Vec("hmma_sparse_indexedRF_")
      ) {
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
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
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
  object HmmaRegRegRegRegPred extends Definition(
        "HmmaRegRegRegRegPred",
        "HMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("hmma_x8_")
      ) {
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
  object HmmaRegRegRegRegPredRegImm extends Definition(
        "HmmaRegRegRegRegPredRegImm",
        "HMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("re", OperandKind.Register),
          Slot("id", OperandKind.Immediate)
        ),
        Vec("hmma_sparse_")
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
            ) => Some(Instruction.HmmaRegRegRegRegPredRegImm(dst, srcA, srcB, srcC, uPp, re, id, modifiers, guard))
        case _ => None
      }
  }
  object HmmaRegRegRegRegRegImm extends Definition(
        "HmmaRegRegRegRegRegImm",
        "HMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("re", OperandKind.Register),
          Slot("id", OperandKind.Immediate)
        ),
        Vec("hmma_sparse_")
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
            ) => Some(Instruction.HmmaRegRegRegRegRegImm(dst, srcA, srcB, srcC, re, id, modifiers, guard))
        case _ => None
      }
  }
  object Hmmafp16PipeAddressRegRegAddress extends Definition(
        "Hmmafp16PipeAddressRegRegAddress",
        "HMMAfp16_pipe",
        Vec(
          Slot("dst", OperandKind.Address),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Address)
        ),
        Vec("hmma_x8_indexedRF_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Address(srcC)
            ) => Some(Instruction.Hmmafp16PipeAddressRegRegAddress(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Hmmafp16PipeAddressRegRegAddressPred extends Definition(
        "Hmmafp16PipeAddressRegRegAddressPred",
        "HMMAfp16_pipe",
        Vec(
          Slot("dst", OperandKind.Address),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Address),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("hmma_x8_indexedRF_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Address(srcC),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.Hmmafp16PipeAddressRegRegAddressPred(dst, srcA, srcB, srcC, uPp, modifiers, guard))
        case _ => None
      }
  }
  object Hmmafp16PipeAddressRegRegAddressPredRegImm extends Definition(
        "Hmmafp16PipeAddressRegRegAddressPredRegImm",
        "HMMAfp16_pipe",
        Vec(
          Slot("dst", OperandKind.Address),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Address),
          Slot("uPp", OperandKind.Predicate),
          Slot("re", OperandKind.Register),
          Slot("id", OperandKind.Immediate)
        ),
        Vec("hmma_sparse_indexedRF_")
      ) {
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
            ) => Some(Instruction.Hmmafp16PipeAddressRegRegAddressPredRegImm(
            dst,
            srcA,
            srcB,
            srcC,
            uPp,
            re,
            id,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object Hmmafp16PipeAddressRegRegAddressRegImm extends Definition(
        "Hmmafp16PipeAddressRegRegAddressRegImm",
        "HMMAfp16_pipe",
        Vec(
          Slot("dst", OperandKind.Address),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Address),
          Slot("re", OperandKind.Register),
          Slot("id", OperandKind.Immediate)
        ),
        Vec("hmma_sparse_indexedRF_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Address(srcC),
              InstructionOperand.Register(re),
              InstructionOperand.Immediate(id)
            ) =>
          Some(Instruction.Hmmafp16PipeAddressRegRegAddressRegImm(dst, srcA, srcB, srcC, re, id, modifiers, guard))
        case _ => None
      }
  }
  val definitions: Vec[Definition] = Vec(
    Fmul2RegRegReg,
    Fmul2fmalighterPipeRegRegImm,
    Fmul2fmalighterPipeRegRegReg,
    Fmul32iRegRegImm,
    Fmul32ifauPipeRegRegImm,
    Fmul32ifmaxPipeRegRegImm,
    Fmul32ifmaxlitewPipeRegRegImm,
    Fmul32ifmaiPipeRegRegImm,
    Fmul32ifmalighterPipeRegRegImm,
    Fmul32ifxuPipeRegRegImm,
    FmulfauPipeRegRegConst,
    FmulfauPipeRegRegImm,
    FmulfauPipeRegRegReg,
    FmulfmaxPipeRegRegConst,
    FmulfmaxPipeRegRegImm,
    FmulfmaxPipeRegRegReg,
    FmulfmaxlitewPipeRegRegConst,
    FmulfmaxlitewPipeRegRegImm,
    FmulfmaxlitewPipeRegRegReg,
    FmulfmaiPipeRegRegConst,
    FmulfmaiPipeRegRegImm,
    FmulfmaiPipeRegRegReg,
    FmulfmalighterPipeRegRegAddressImm,
    FmulfmalighterPipeRegRegConst,
    FmulfmalighterPipeRegRegImm,
    FmulfmalighterPipeRegRegReg,
    FmulfxuPipeRegRegConst,
    FmulfxuPipeRegRegImm,
    FmulfxuPipeRegRegReg,
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
    FootprintmioPipePredRegRegRegReg,
    FootprintmioPipePredRegRegRegRegImmImm,
    FootprintmioPipePredRegRegRegRegImmImmImm,
    FootprintmioPipePredRegRegRegRegReg,
    FootprintmioPipePredRegRegRegRegRegImm,
    FootprintmioPipeRegRegRegReg,
    FootprintmioPipeRegRegRegRegImmImm,
    FootprintmioPipeRegRegRegRegImmImmImm,
    FootprintmioPipeRegRegRegRegReg,
    FootprintmioPipeRegRegRegRegRegImm,
    FrndRegAddressImm,
    FrndRegConst,
    FrndRegImm,
    FrndRegReg,
    FrndmioPipeRegAddressImm,
    FrndmioPipeRegConst,
    FrndmioPipeRegImm,
    FrndmioPipeRegReg,
    FselRegRegAddressImmPred,
    FselRegRegConstPred,
    FselRegRegImmPred,
    FselRegRegRegPred,
    FselintPipeRegRegAddressImmPred,
    FselintPipeRegRegConstPred,
    FselintPipeRegRegImmPred,
    FselintPipeRegRegRegPred,
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
    FsetpfauPipePredPredRegConstPred,
    FsetpfauPipePredPredRegImmPred,
    FsetpfauPipePredPredRegRegPred,
    FsetpfauPipePredRegConst,
    FsetpfauPipePredRegImm,
    FsetpfauPipePredRegReg,
    FsetpfmaxPipePredPredRegConstPred,
    FsetpfmaxPipePredPredRegImmPred,
    FsetpfmaxPipePredPredRegRegPred,
    FsetpfmaxPipePredRegConst,
    FsetpfmaxPipePredRegImm,
    FsetpfmaxPipePredRegReg,
    FsetpfxuPipePredPredRegConstPred,
    FsetpfxuPipePredPredRegImmPred,
    FsetpfxuPipePredPredRegRegPred,
    FsetpfxuPipePredRegConst,
    FsetpfxuPipePredRegImm,
    FsetpfxuPipePredRegReg,
    FsetpintPipePredPredRegAddressImmPred,
    FsetpintPipePredPredRegConstPred,
    FsetpintPipePredPredRegImmPred,
    FsetpintPipePredPredRegRegPred,
    FsetpintPipePredRegAddressImm,
    FsetpintPipePredRegConst,
    FsetpintPipePredRegImm,
    FsetpintPipePredRegReg,
    FsetfauPipeRegRegConst,
    FsetfauPipeRegRegConstPred,
    FsetfauPipeRegRegImm,
    FsetfauPipeRegRegImmPred,
    FsetfauPipeRegRegReg,
    FsetfauPipeRegRegRegPred,
    FsetfmaxPipeRegRegConst,
    FsetfmaxPipeRegRegConstPred,
    FsetfmaxPipeRegRegImm,
    FsetfmaxPipeRegRegImmPred,
    FsetfmaxPipeRegRegReg,
    FsetfmaxPipeRegRegRegPred,
    FsetfxuPipeRegRegConst,
    FsetfxuPipeRegRegConstPred,
    FsetfxuPipeRegRegImm,
    FsetfxuPipeRegRegImmPred,
    FsetfxuPipeRegRegReg,
    FsetfxuPipeRegRegRegPred,
    FsetintPipeRegRegAddressImm,
    FsetintPipeRegRegAddressImmPred,
    FsetintPipeRegRegConst,
    FsetintPipeRegRegConstPred,
    FsetintPipeRegRegImm,
    FsetintPipeRegRegImmPred,
    FsetintPipeRegRegReg,
    FsetintPipeRegRegRegPred,
    FswzRegRegReg,
    FswzaddRegRegReg,
    FswzaddfmalighterPipeRegRegReg,
    FswzaddfxuPipeRegRegReg,
    FswzfmaxPipeRegRegReg,
    GatherRegRegRegRegImm,
    GatherRegRegRegRegImmImm,
    GatherRegRegRegRegImmImmImm,
    GatherintPipeRegRegRegRegImm,
    GatherintPipeRegRegRegRegImmImm,
    GatherintPipeRegRegRegRegImmImmImm,
    GetcrsptrReg,
    GetcrsptrbruPipeReg,
    GetcrsptrmioPipeReg,
    GetlmembaseReg,
    GetlmembaseaguPipeReg,
    GetlmembasemioPipeReg,
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
    Hadd232ifp16PipeRegRegImmImm,
    Hadd232ifp16g0PipeImmImm,
    Hadd232ifp16g1PipeImmImm,
    Hadd2fp16PipeRegRegAddressImm,
    Hadd2fp16PipeRegRegConst,
    Hadd2fp16PipeRegRegImm,
    Hadd2fp16PipeRegRegImmImm,
    Hadd2fp16PipeRegRegReg,
    Hadd2fp16g0PipeNoOperands,
    Hadd2fp16g0PipeConst,
    Hadd2fp16g0PipeImmImm,
    Hadd2fp16g1PipeNoOperands,
    Hadd2fp16g1PipeConst,
    Hadd2fp16g1PipeImmImm,
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
    Hfma232ifp16PipeRegRegImmImmReg,
    Hfma232ifp16g0PipeRegRegImmImm,
    Hfma232ifp16g0PipeRegRegImmImmReg,
    Hfma232ifp16g1PipeRegRegImmImm,
    Hfma232ifp16g1PipeRegRegImmImmReg,
    Hfma2fp16PipeRegRegAddressImmReg,
    Hfma2fp16PipeRegRegAddressImmRegPred,
    Hfma2fp16PipeRegRegConstReg,
    Hfma2fp16PipeRegRegConstRegPred,
    Hfma2fp16PipeRegRegImmImmReg,
    Hfma2fp16PipeRegRegImmImmRegPred,
    Hfma2fp16PipeRegRegRegAddressImm,
    Hfma2fp16PipeRegRegRegAddressImmPred,
    Hfma2fp16PipeRegRegRegConst,
    Hfma2fp16PipeRegRegRegConstPred,
    Hfma2fp16PipeRegRegRegImmImm,
    Hfma2fp16PipeRegRegRegImmImmPred,
    Hfma2fp16PipeRegRegRegReg,
    Hfma2fp16PipeRegRegRegRegPred,
    Hfma2fp16g0PipeNoOperands,
    Hfma2fp16g0PipeConst,
    Hfma2fp16g0PipeImmImm,
    Hfma2fp16g0PipeRegRegRegConst,
    Hfma2fp16g1PipeNoOperands,
    Hfma2fp16g1PipeConst,
    Hfma2fp16g1PipeImmImm,
    Hfma2fp16g1PipeRegRegRegConst,
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
    HgmmamioPipeRegAddressReg,
    HgmmamioPipeRegAddressRegPred,
    HgmmamioPipeRegAddressRegPredRegImm,
    HgmmamioPipeRegAddressRegReg,
    HgmmamioPipeRegAddressRegRegImm,
    HgmmamioPipeRegAddressRegRegPred,
    HgmmamioPipeRegAddressRegRegPredRegImm,
    HgmmamioPipeRegAddressRegRegRegImm,
    HgmmamioPipeRegRegAddressReg,
    HgmmamioPipeRegRegAddressRegPred,
    HgmmamioPipeRegRegAddressRegPredRegImm,
    HgmmamioPipeRegRegAddressRegRegImm,
    HmmaAddressRegRegAddress,
    HmmaAddressRegRegAddressPred,
    HmmaAddressRegRegAddressPredRegImm,
    HmmaAddressRegRegAddressRegImm,
    HmmaRegRegRegReg,
    HmmaRegRegRegRegPred,
    HmmaRegRegRegRegPredRegImm,
    HmmaRegRegRegRegRegImm,
    Hmmafp16PipeAddressRegRegAddress,
    Hmmafp16PipeAddressRegRegAddressPred,
    Hmmafp16PipeAddressRegRegAddressPredRegImm,
    Hmmafp16PipeAddressRegRegAddressRegImm
  )
}
