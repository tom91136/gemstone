// Generated SASS instruction definitions and checked operand constructors. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.sass
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import Instruction.*
@javax.annotation.processing.Generated(Array("sbt codegen"))
object InstructionDefinitions9 {
  object OutmioPipeRegRegReg extends Definition(
        "OutmioPipeRegRegReg",
        "OUTmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("OUT", "out__CUT", "out__EMIT_Rb", "out__EMIT_URb", "out__FINAL")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.OutmioPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object P2rReg extends Definition("P2rReg", "P2R", Vec(Slot("dst", OperandKind.Register)), Vec("Simple_P2R")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst)) => Some(Instruction.P2rReg(dst, modifiers, guard))
        case _ => None
      }
  }
  object P2rRegRegConst extends Definition(
        "P2rRegRegConst",
        "P2R",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const_P2R")
      ) {
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
  object P2rRegRegImm extends Definition(
        "P2rRegRegImm",
        "P2R",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("Imm_P2R")
      ) {
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
  object P2rRegRegReg extends Definition(
        "P2rRegRegReg",
        "P2R",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("P2R")
      ) {
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
  object P2rRegSymbol extends Definition(
        "P2rRegSymbol",
        "P2R",
        Vec(Slot("dst", OperandKind.Register), Slot("pr", OperandKind.Symbol)),
        Vec("Simple P2R", "Simple_P2R", "p2r_simple_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Symbol(pr)) =>
          Some(Instruction.P2rRegSymbol(dst, pr, modifiers, guard))
        case _ => None
      }
  }
  object P2rRegSymbolRegAddressImm extends Definition(
        "P2rRegSymbolRegAddressImm",
        "P2R",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pr", OperandKind.Symbol),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("p2r__RCxR_RCxR")
      ) {
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
  object P2rRegSymbolRegConst extends Definition(
        "P2rRegSymbolRegConst",
        "P2R",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pr", OperandKind.Symbol),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank)
        ),
        Vec("Const P2R", "Const_P2R", "p2r__RCR_RCR")
      ) {
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
  object P2rRegSymbolRegImm extends Definition(
        "P2rRegSymbolRegImm",
        "P2R",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pr", OperandKind.Symbol),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate)
        ),
        Vec("Imm P2R", "Imm_P2R", "p2r__RsIR_RIR", "p2r__RuIR_RIR")
      ) {
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
  object P2rRegSymbolRegReg extends Definition(
        "P2rRegSymbolRegReg",
        "P2R",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pr", OperandKind.Symbol),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register)
        ),
        Vec("P2R", "p2r__RRR_RRR", "p2r__RUR_RUR")
      ) {
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
  object P2rfxuPipeReg
      extends Definition("P2rfxuPipeReg", "P2Rfxu_pipe", Vec(Slot("dst", OperandKind.Register)), Vec("Simple_P2R")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst)) => Some(Instruction.P2rfxuPipeReg(dst, modifiers, guard))
        case _ => None
      }
  }
  object P2rfxuPipeRegRegConst extends Definition(
        "P2rfxuPipeRegRegConst",
        "P2Rfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const_P2R")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.P2rfxuPipeRegRegConst(dst, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object P2rfxuPipeRegRegImm extends Definition(
        "P2rfxuPipeRegRegImm",
        "P2Rfxu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("Imm_P2R")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.P2rfxuPipeRegRegImm(dst, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object P2rfxuPipeRegRegReg extends Definition(
        "P2rfxuPipeRegRegReg",
        "P2Rfxu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("P2R")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.P2rfxuPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object P2rfxuPipeRegSymbol extends Definition(
        "P2rfxuPipeRegSymbol",
        "P2Rfxu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("pr", OperandKind.Symbol)),
        Vec("Simple_P2R")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Symbol(pr)) =>
          Some(Instruction.P2rfxuPipeRegSymbol(dst, pr, modifiers, guard))
        case _ => None
      }
  }
  object P2rfxuPipeRegSymbolRegConst extends Definition(
        "P2rfxuPipeRegSymbolRegConst",
        "P2Rfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pr", OperandKind.Symbol),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const_P2R")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Symbol(pr),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.P2rfxuPipeRegSymbolRegConst(dst, pr, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object P2rfxuPipeRegSymbolRegImm extends Definition(
        "P2rfxuPipeRegSymbolRegImm",
        "P2Rfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pr", OperandKind.Symbol),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate)
        ),
        Vec("Imm_P2R")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Symbol(pr),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.P2rfxuPipeRegSymbolRegImm(dst, pr, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object P2rfxuPipeRegSymbolRegReg extends Definition(
        "P2rfxuPipeRegSymbolRegReg",
        "P2Rfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pr", OperandKind.Symbol),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register)
        ),
        Vec("P2R")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Symbol(pr),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.P2rfxuPipeRegSymbolRegReg(dst, pr, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object P2rintPipeRegSymbol extends Definition(
        "P2rintPipeRegSymbol",
        "P2Rint_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("pr", OperandKind.Symbol)),
        Vec("p2r_simple_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Symbol(pr)) =>
          Some(Instruction.P2rintPipeRegSymbol(dst, pr, modifiers, guard))
        case _ => None
      }
  }
  object P2rintPipeRegSymbolRegAddressImm extends Definition(
        "P2rintPipeRegSymbolRegAddressImm",
        "P2Rint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pr", OperandKind.Symbol),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("p2r__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Symbol(pr),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.P2rintPipeRegSymbolRegAddressImm(dst, pr, srcA, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object P2rintPipeRegSymbolRegConst extends Definition(
        "P2rintPipeRegSymbolRegConst",
        "P2Rint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pr", OperandKind.Symbol),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank)
        ),
        Vec("p2r__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Symbol(pr),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb)
            ) => Some(Instruction.P2rintPipeRegSymbolRegConst(dst, pr, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object P2rintPipeRegSymbolRegImm extends Definition(
        "P2rintPipeRegSymbolRegImm",
        "P2Rint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pr", OperandKind.Symbol),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate)
        ),
        Vec("p2r__RsIR_RIR", "p2r__RuIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Symbol(pr),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.P2rintPipeRegSymbolRegImm(dst, pr, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object P2rintPipeRegSymbolRegReg extends Definition(
        "P2rintPipeRegSymbolRegReg",
        "P2Rint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pr", OperandKind.Symbol),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register)
        ),
        Vec("p2r__RRR_RRR", "p2r__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Symbol(pr),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.P2rintPipeRegSymbolRegReg(dst, pr, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object P2rxluPipeRegSymbol extends Definition(
        "P2rxluPipeRegSymbol",
        "P2Rxlu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("pr", OperandKind.Symbol)),
        Vec("Simple P2R")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Symbol(pr)) =>
          Some(Instruction.P2rxluPipeRegSymbol(dst, pr, modifiers, guard))
        case _ => None
      }
  }
  object P2rxluPipeRegSymbolRegConst extends Definition(
        "P2rxluPipeRegSymbolRegConst",
        "P2Rxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pr", OperandKind.Symbol),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const P2R")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Symbol(pr),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.P2rxluPipeRegSymbolRegConst(dst, pr, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object P2rxluPipeRegSymbolRegImm extends Definition(
        "P2rxluPipeRegSymbolRegImm",
        "P2Rxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pr", OperandKind.Symbol),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate)
        ),
        Vec("Imm P2R")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Symbol(pr),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.P2rxluPipeRegSymbolRegImm(dst, pr, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object P2rxluPipeRegSymbolRegReg extends Definition(
        "P2rxluPipeRegSymbolRegReg",
        "P2Rxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pr", OperandKind.Symbol),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register)
        ),
        Vec("P2R")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Symbol(pr),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.P2rxluPipeRegSymbolRegReg(dst, pr, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object PbkConst
      extends Definition("PbkConst", "PBK", Vec(Slot("srcConst", OperandKind.ConstantBank)), Vec("PBK_c", "SSY c")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(srcConst)) => Some(Instruction.PbkConst(srcConst, modifiers, guard))
        case _ => None
      }
  }
  object PbkImm extends Definition("PbkImm", "PBK", Vec(Slot("sImm", OperandKind.Immediate)), Vec("PBK", "SSY")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sImm)) => Some(Instruction.PbkImm(sImm, modifiers, guard))
        case _ => None
      }
  }
  object PbkbruPipeConst extends Definition(
        "PbkbruPipeConst",
        "PBKbru_pipe",
        Vec(Slot("srcConst", OperandKind.ConstantBank)),
        Vec("PBK_c", "SSY c")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(srcConst)) =>
          Some(Instruction.PbkbruPipeConst(srcConst, modifiers, guard))
        case _ => None
      }
  }
  object PbkbruPipeImm
      extends Definition("PbkbruPipeImm", "PBKbru_pipe", Vec(Slot("sImm", OperandKind.Immediate)), Vec("PBK", "SSY")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sImm)) => Some(Instruction.PbkbruPipeImm(sImm, modifiers, guard))
        case _ => None
      }
  }
  object PcntConst
      extends Definition("PcntConst", "PCNT", Vec(Slot("srcConst", OperandKind.ConstantBank)), Vec("PCNT_c", "SSY c")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(srcConst)) => Some(Instruction.PcntConst(srcConst, modifiers, guard))
        case _ => None
      }
  }
  object PcntImm extends Definition("PcntImm", "PCNT", Vec(Slot("sImm", OperandKind.Immediate)), Vec("PCNT", "SSY")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sImm)) => Some(Instruction.PcntImm(sImm, modifiers, guard))
        case _ => None
      }
  }
  object PcntbruPipeConst extends Definition(
        "PcntbruPipeConst",
        "PCNTbru_pipe",
        Vec(Slot("srcConst", OperandKind.ConstantBank)),
        Vec("PCNT_c", "SSY c")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(srcConst)) =>
          Some(Instruction.PcntbruPipeConst(srcConst, modifiers, guard))
        case _ => None
      }
  }
  object PcntbruPipeImm extends Definition(
        "PcntbruPipeImm",
        "PCNTbru_pipe",
        Vec(Slot("sImm", OperandKind.Immediate)),
        Vec("PCNT", "SSY")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sImm)) => Some(Instruction.PcntbruPipeImm(sImm, modifiers, guard))
        case _ => None
      }
  }
  object PexitImm extends Definition("PexitImm", "PEXIT", Vec(Slot("sImm", OperandKind.Immediate)), Vec("PEXIT")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sImm)) => Some(Instruction.PexitImm(sImm, modifiers, guard))
        case _ => None
      }
  }
  object PexitbruPipeImm
      extends Definition("PexitbruPipeImm", "PEXITbru_pipe", Vec(Slot("sImm", OperandKind.Immediate)), Vec("PEXIT")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sImm)) => Some(Instruction.PexitbruPipeImm(sImm, modifiers, guard))
        case _ => None
      }
  }
  object PixldReg extends Definition(
        "PixldReg",
        "PIXLD",
        Vec(Slot("dst", OperandKind.Register)),
        Vec("PIXLD simple", "PIXLD_simple", "pixld_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst)) => Some(Instruction.PixldReg(dst, modifiers, guard))
        case _ => None
      }
  }
  object PixldRegAddress extends Definition(
        "PixldRegAddress",
        "PIXLD",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address)),
        Vec("I PIXLD", "I_PIXLD", "PIXLD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Address(srcA)) =>
          Some(Instruction.PixldRegAddress(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object PixldRegPred extends Definition(
        "PixldRegPred",
        "PIXLD",
        Vec(Slot("dst", OperandKind.Register), Slot("dstPredicate", OperandKind.Predicate)),
        Vec("PIXLD simple", "PIXLD_simple", "pixld_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Predicate(dstPredicate)) =>
          Some(Instruction.PixldRegPred(dst, dstPredicate, modifiers, guard))
        case _ => None
      }
  }
  object PixldRegPredAddress extends Definition(
        "PixldRegPredAddress",
        "PIXLD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Address)
        ),
        Vec("I PIXLD", "I_PIXLD", "PIXLD")
      ) {
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
  object PixldfePipeReg extends Definition(
        "PixldfePipeReg",
        "PIXLDfe_pipe",
        Vec(Slot("dst", OperandKind.Register)),
        Vec("PIXLD simple")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst)) => Some(Instruction.PixldfePipeReg(dst, modifiers, guard))
        case _ => None
      }
  }
  object PixldfePipeRegAddress extends Definition(
        "PixldfePipeRegAddress",
        "PIXLDfe_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address)),
        Vec("I PIXLD", "PIXLD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Address(srcA)) =>
          Some(Instruction.PixldfePipeRegAddress(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object PixldfePipeRegPred extends Definition(
        "PixldfePipeRegPred",
        "PIXLDfe_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("dstPredicate", OperandKind.Predicate)),
        Vec("PIXLD simple")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Predicate(dstPredicate)) =>
          Some(Instruction.PixldfePipeRegPred(dst, dstPredicate, modifiers, guard))
        case _ => None
      }
  }
  object PixldfePipeRegPredAddress extends Definition(
        "PixldfePipeRegPredAddress",
        "PIXLDfe_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Address)
        ),
        Vec("I PIXLD", "PIXLD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Address(srcA)
            ) => Some(Instruction.PixldfePipeRegPredAddress(dst, dstPredicate, srcA, modifiers, guard))
        case _ => None
      }
  }
  object PixldmioPipeReg extends Definition(
        "PixldmioPipeReg",
        "PIXLDmio_pipe",
        Vec(Slot("dst", OperandKind.Register)),
        Vec("PIXLD_simple", "pixld_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst)) => Some(Instruction.PixldmioPipeReg(dst, modifiers, guard))
        case _ => None
      }
  }
  object PixldmioPipeRegAddress extends Definition(
        "PixldmioPipeRegAddress",
        "PIXLDmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address)),
        Vec("I_PIXLD", "PIXLD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Address(srcA)) =>
          Some(Instruction.PixldmioPipeRegAddress(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object PixldmioPipeRegPred extends Definition(
        "PixldmioPipeRegPred",
        "PIXLDmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("dstPredicate", OperandKind.Predicate)),
        Vec("PIXLD_simple", "pixld_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Predicate(dstPredicate)) =>
          Some(Instruction.PixldmioPipeRegPred(dst, dstPredicate, modifiers, guard))
        case _ => None
      }
  }
  object PixldmioPipeRegPredAddress extends Definition(
        "PixldmioPipeRegPredAddress",
        "PIXLDmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Address)
        ),
        Vec("I_PIXLD", "PIXLD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Address(srcA)
            ) => Some(Instruction.PixldmioPipeRegPredAddress(dst, dstPredicate, srcA, modifiers, guard))
        case _ => None
      }
  }
  object PlongjmpConst extends Definition(
        "PlongjmpConst",
        "PLONGJMP",
        Vec(Slot("srcConst", OperandKind.ConstantBank)),
        Vec("PLONGJMP_c", "SSY c")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(srcConst)) =>
          Some(Instruction.PlongjmpConst(srcConst, modifiers, guard))
        case _ => None
      }
  }
  object PlongjmpImm
      extends Definition("PlongjmpImm", "PLONGJMP", Vec(Slot("sImm", OperandKind.Immediate)), Vec("PLONGJMP", "SSY")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sImm)) => Some(Instruction.PlongjmpImm(sImm, modifiers, guard))
        case _ => None
      }
  }
  object PlongjmpbruPipeConst extends Definition(
        "PlongjmpbruPipeConst",
        "PLONGJMPbru_pipe",
        Vec(Slot("srcConst", OperandKind.ConstantBank)),
        Vec("PLONGJMP_c", "SSY c")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(srcConst)) =>
          Some(Instruction.PlongjmpbruPipeConst(srcConst, modifiers, guard))
        case _ => None
      }
  }
  object PlongjmpbruPipeImm extends Definition(
        "PlongjmpbruPipeImm",
        "PLONGJMPbru_pipe",
        Vec(Slot("sImm", OperandKind.Immediate)),
        Vec("PLONGJMP", "SSY")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sImm)) => Some(Instruction.PlongjmpbruPipeImm(sImm, modifiers, guard))
        case _ => None
      }
  }
  object Plop3PredPredAddressImmPredImm extends Definition(
        "Plop3PredPredAddressImmPredImm",
        "PLOP3",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pp", OperandKind.Predicate),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("pr", OperandKind.Predicate),
          Slot("uimm8", OperandKind.Immediate)
        ),
        Vec("plop3_lut_1out_1reg__RCxR")
      ) {
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
  object Plop3PredPredAddressImmRegImm extends Definition(
        "Plop3PredPredAddressImmRegImm",
        "PLOP3",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pp", OperandKind.Predicate),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("uimm8", OperandKind.Immediate)
        ),
        Vec("plop3_lut_1out_2reg__RCxR")
      ) {
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
  object Plop3PredPredConstPredImm extends Definition(
        "Plop3PredPredConstPredImm",
        "PLOP3",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pp", OperandKind.Predicate),
          Slot("sb", OperandKind.ConstantBank),
          Slot("pr", OperandKind.Predicate),
          Slot("uimm8", OperandKind.Immediate)
        ),
        Vec("plop3_lut_1out_1reg__RCR")
      ) {
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
  object Plop3PredPredConstRegImm extends Definition(
        "Plop3PredPredConstRegImm",
        "PLOP3",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pp", OperandKind.Predicate),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register),
          Slot("uimm8", OperandKind.Immediate)
        ),
        Vec("plop3_lut_1out_2reg__RCR")
      ) {
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
  object Plop3PredPredPredAddressImmPredImmImm extends Definition(
        "Plop3PredPredPredAddressImmPredImmImm",
        "PLOP3",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("pp", OperandKind.Predicate),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("pr", OperandKind.Predicate),
          Slot("uimm8", OperandKind.Immediate),
          Slot("vimm8", OperandKind.Immediate)
        ),
        Vec("plop3_lut_2out_1reg__RCxR")
      ) {
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
  object Plop3PredPredPredAddressImmRegImmImm extends Definition(
        "Plop3PredPredPredAddressImmRegImmImm",
        "PLOP3",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("pp", OperandKind.Predicate),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("uimm8", OperandKind.Immediate),
          Slot("vimm8", OperandKind.Immediate)
        ),
        Vec("plop3_lut_2out_2reg__RCxR")
      ) {
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
  object Plop3PredPredPredConstPredImmImm extends Definition(
        "Plop3PredPredPredConstPredImmImm",
        "PLOP3",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("pp", OperandKind.Predicate),
          Slot("sb", OperandKind.ConstantBank),
          Slot("pr", OperandKind.Predicate),
          Slot("uimm8", OperandKind.Immediate),
          Slot("vimm8", OperandKind.Immediate)
        ),
        Vec("plop3_lut_2out_1reg__RCR")
      ) {
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
  object Plop3PredPredPredConstRegImmImm extends Definition(
        "Plop3PredPredPredConstRegImmImm",
        "PLOP3",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("pp", OperandKind.Predicate),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register),
          Slot("uimm8", OperandKind.Immediate),
          Slot("vimm8", OperandKind.Immediate)
        ),
        Vec("plop3_lut_2out_2reg__RCR")
      ) {
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
  object Plop3PredPredPredPred extends Definition(
        "Plop3PredPredPredPred",
        "PLOP3",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pp", OperandKind.Predicate),
          Slot("pq", OperandKind.Predicate),
          Slot("pr", OperandKind.Predicate)
        ),
        Vec("plop3_1out_", "plop3_1out_uniform_")
      ) {
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
  object Plop3PredPredPredPredImm extends Definition(
        "Plop3PredPredPredPredImm",
        "PLOP3",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pp", OperandKind.Predicate),
          Slot("pq", OperandKind.Predicate),
          Slot("pr", OperandKind.Predicate),
          Slot("uimm8", OperandKind.Immediate)
        ),
        Vec("plop3_lut_1out_", "plop3_lut_1out_uniform_")
      ) {
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
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("pp", OperandKind.Predicate),
          Slot("pq", OperandKind.Predicate),
          Slot("pr", OperandKind.Predicate),
          Slot("uimm8", OperandKind.Immediate),
          Slot("vimm8", OperandKind.Immediate)
        ),
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
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("pp", OperandKind.Predicate),
          Slot("srcB", OperandKind.Register),
          Slot("pr", OperandKind.Predicate),
          Slot("uimm8", OperandKind.Immediate),
          Slot("vimm8", OperandKind.Immediate)
        ),
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
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("pp", OperandKind.Predicate),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uimm8", OperandKind.Immediate),
          Slot("vimm8", OperandKind.Immediate)
        ),
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
  object Plop3PredPredRegAddressImmRegImmImm extends Definition(
        "Plop3PredPredRegAddressImmRegImmImm",
        "PLOP3",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("uimm8", OperandKind.Immediate),
          Slot("vimm8", OperandKind.Immediate)
        ),
        Vec("plop3_lut_2out_3reg__RCxR")
      ) {
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
  object Plop3PredPredRegConstRegImmImm extends Definition(
        "Plop3PredPredRegConstRegImmImm",
        "PLOP3",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register),
          Slot("uimm8", OperandKind.Immediate),
          Slot("vimm8", OperandKind.Immediate)
        ),
        Vec("plop3_lut_2out_3reg__RCR")
      ) {
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
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pp", OperandKind.Predicate),
          Slot("srcB", OperandKind.Register),
          Slot("pr", OperandKind.Predicate),
          Slot("uimm8", OperandKind.Immediate)
        ),
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
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pp", OperandKind.Predicate),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uimm8", OperandKind.Immediate)
        ),
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
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uimm8", OperandKind.Immediate),
          Slot("vimm8", OperandKind.Immediate)
        ),
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
  object Plop3PredRegAddressImmRegImm extends Definition(
        "Plop3PredRegAddressImmRegImm",
        "PLOP3",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("uimm8", OperandKind.Immediate)
        ),
        Vec("plop3_lut_1out_3reg__RCxR")
      ) {
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
  object Plop3PredRegConstRegImm extends Definition(
        "Plop3PredRegConstRegImm",
        "PLOP3",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register),
          Slot("uimm8", OperandKind.Immediate)
        ),
        Vec("plop3_lut_1out_3reg__RCR")
      ) {
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
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uimm8", OperandKind.Immediate)
        ),
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
  object Plop3intPipePredPredAddressImmPredImm extends Definition(
        "Plop3intPipePredPredAddressImmPredImm",
        "PLOP3int_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pp", OperandKind.Predicate),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("pr", OperandKind.Predicate),
          Slot("uimm8", OperandKind.Immediate)
        ),
        Vec("plop3_lut_1out_1reg__RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Predicate(pr),
              InstructionOperand.Immediate(uimm8)
            ) =>
          Some(Instruction.Plop3intPipePredPredAddressImmPredImm(pu, pp, srcB, sb_offset, pr, uimm8, modifiers, guard))
        case _ => None
      }
  }
  object Plop3intPipePredPredAddressImmRegImm extends Definition(
        "Plop3intPipePredPredAddressImmRegImm",
        "PLOP3int_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pp", OperandKind.Predicate),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("uimm8", OperandKind.Immediate)
        ),
        Vec("plop3_lut_1out_2reg__RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(uimm8)
            ) =>
          Some(Instruction.Plop3intPipePredPredAddressImmRegImm(pu, pp, srcB, sb_offset, srcC, uimm8, modifiers, guard))
        case _ => None
      }
  }
  object Plop3intPipePredPredConstPredImm extends Definition(
        "Plop3intPipePredPredConstPredImm",
        "PLOP3int_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pp", OperandKind.Predicate),
          Slot("sb", OperandKind.ConstantBank),
          Slot("pr", OperandKind.Predicate),
          Slot("uimm8", OperandKind.Immediate)
        ),
        Vec("plop3_lut_1out_1reg__RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pp),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Predicate(pr),
              InstructionOperand.Immediate(uimm8)
            ) => Some(Instruction.Plop3intPipePredPredConstPredImm(pu, pp, sb, pr, uimm8, modifiers, guard))
        case _ => None
      }
  }
  object Plop3intPipePredPredConstRegImm extends Definition(
        "Plop3intPipePredPredConstRegImm",
        "PLOP3int_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pp", OperandKind.Predicate),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register),
          Slot("uimm8", OperandKind.Immediate)
        ),
        Vec("plop3_lut_1out_2reg__RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pp),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(uimm8)
            ) => Some(Instruction.Plop3intPipePredPredConstRegImm(pu, pp, sb, srcC, uimm8, modifiers, guard))
        case _ => None
      }
  }
  object Plop3intPipePredPredPredAddressImmPredImmImm extends Definition(
        "Plop3intPipePredPredPredAddressImmPredImmImm",
        "PLOP3int_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("pp", OperandKind.Predicate),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("pr", OperandKind.Predicate),
          Slot("uimm8", OperandKind.Immediate),
          Slot("vimm8", OperandKind.Immediate)
        ),
        Vec("plop3_lut_2out_1reg__RCxR")
      ) {
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
            ) => Some(Instruction.Plop3intPipePredPredPredAddressImmPredImmImm(
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
  object Plop3intPipePredPredPredAddressImmRegImmImm extends Definition(
        "Plop3intPipePredPredPredAddressImmRegImmImm",
        "PLOP3int_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("pp", OperandKind.Predicate),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("uimm8", OperandKind.Immediate),
          Slot("vimm8", OperandKind.Immediate)
        ),
        Vec("plop3_lut_2out_2reg__RCxR")
      ) {
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
            ) => Some(Instruction.Plop3intPipePredPredPredAddressImmRegImmImm(
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
  object Plop3intPipePredPredPredConstPredImmImm extends Definition(
        "Plop3intPipePredPredPredConstPredImmImm",
        "PLOP3int_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("pp", OperandKind.Predicate),
          Slot("sb", OperandKind.ConstantBank),
          Slot("pr", OperandKind.Predicate),
          Slot("uimm8", OperandKind.Immediate),
          Slot("vimm8", OperandKind.Immediate)
        ),
        Vec("plop3_lut_2out_1reg__RCR")
      ) {
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
            ) =>
          Some(Instruction.Plop3intPipePredPredPredConstPredImmImm(pu, pv, pp, sb, pr, uimm8, vimm8, modifiers, guard))
        case _ => None
      }
  }
  object Plop3intPipePredPredPredConstRegImmImm extends Definition(
        "Plop3intPipePredPredPredConstRegImmImm",
        "PLOP3int_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("pp", OperandKind.Predicate),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register),
          Slot("uimm8", OperandKind.Immediate),
          Slot("vimm8", OperandKind.Immediate)
        ),
        Vec("plop3_lut_2out_2reg__RCR")
      ) {
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
            ) =>
          Some(Instruction.Plop3intPipePredPredPredConstRegImmImm(pu, pv, pp, sb, srcC, uimm8, vimm8, modifiers, guard))
        case _ => None
      }
  }
  object Plop3intPipePredPredPredPred extends Definition(
        "Plop3intPipePredPredPredPred",
        "PLOP3int_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pp", OperandKind.Predicate),
          Slot("pq", OperandKind.Predicate),
          Slot("pr", OperandKind.Predicate)
        ),
        Vec("plop3_1out_", "plop3_1out_uniform_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Predicate(pq),
              InstructionOperand.Predicate(pr)
            ) => Some(Instruction.Plop3intPipePredPredPredPred(pu, pp, pq, pr, modifiers, guard))
        case _ => None
      }
  }
  object Plop3intPipePredPredPredPredImm extends Definition(
        "Plop3intPipePredPredPredPredImm",
        "PLOP3int_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pp", OperandKind.Predicate),
          Slot("pq", OperandKind.Predicate),
          Slot("pr", OperandKind.Predicate),
          Slot("uimm8", OperandKind.Immediate)
        ),
        Vec("plop3_lut_1out_", "plop3_lut_1out_uniform_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Predicate(pq),
              InstructionOperand.Predicate(pr),
              InstructionOperand.Immediate(uimm8)
            ) => Some(Instruction.Plop3intPipePredPredPredPredImm(pu, pp, pq, pr, uimm8, modifiers, guard))
        case _ => None
      }
  }
  object Plop3intPipePredPredPredPredPredImmImm extends Definition(
        "Plop3intPipePredPredPredPredPredImmImm",
        "PLOP3int_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("pp", OperandKind.Predicate),
          Slot("pq", OperandKind.Predicate),
          Slot("pr", OperandKind.Predicate),
          Slot("uimm8", OperandKind.Immediate),
          Slot("vimm8", OperandKind.Immediate)
        ),
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
            ) =>
          Some(Instruction.Plop3intPipePredPredPredPredPredImmImm(pu, pv, pp, pq, pr, uimm8, vimm8, modifiers, guard))
        case _ => None
      }
  }
  object Plop3intPipePredPredPredRegPredImmImm extends Definition(
        "Plop3intPipePredPredPredRegPredImmImm",
        "PLOP3int_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("pp", OperandKind.Predicate),
          Slot("srcB", OperandKind.Register),
          Slot("pr", OperandKind.Predicate),
          Slot("uimm8", OperandKind.Immediate),
          Slot("vimm8", OperandKind.Immediate)
        ),
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
            ) =>
          Some(Instruction.Plop3intPipePredPredPredRegPredImmImm(pu, pv, pp, srcB, pr, uimm8, vimm8, modifiers, guard))
        case _ => None
      }
  }
  object Plop3intPipePredPredPredRegRegImmImm extends Definition(
        "Plop3intPipePredPredPredRegRegImmImm",
        "PLOP3int_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("pp", OperandKind.Predicate),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uimm8", OperandKind.Immediate),
          Slot("vimm8", OperandKind.Immediate)
        ),
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
            ) =>
          Some(Instruction.Plop3intPipePredPredPredRegRegImmImm(pu, pv, pp, srcB, srcC, uimm8, vimm8, modifiers, guard))
        case _ => None
      }
  }
  object Plop3intPipePredPredRegAddressImmRegImmImm extends Definition(
        "Plop3intPipePredPredRegAddressImmRegImmImm",
        "PLOP3int_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("uimm8", OperandKind.Immediate),
          Slot("vimm8", OperandKind.Immediate)
        ),
        Vec("plop3_lut_2out_3reg__RCxR")
      ) {
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
            ) => Some(Instruction.Plop3intPipePredPredRegAddressImmRegImmImm(
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
  object Plop3intPipePredPredRegConstRegImmImm extends Definition(
        "Plop3intPipePredPredRegConstRegImmImm",
        "PLOP3int_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register),
          Slot("uimm8", OperandKind.Immediate),
          Slot("vimm8", OperandKind.Immediate)
        ),
        Vec("plop3_lut_2out_3reg__RCR")
      ) {
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
            ) => Some(Instruction.Plop3intPipePredPredRegConstRegImmImm(
            pu,
            pv,
            srcA,
            sb,
            srcC,
            uimm8,
            vimm8,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object Plop3intPipePredPredRegPredImm extends Definition(
        "Plop3intPipePredPredRegPredImm",
        "PLOP3int_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pp", OperandKind.Predicate),
          Slot("srcB", OperandKind.Register),
          Slot("pr", OperandKind.Predicate),
          Slot("uimm8", OperandKind.Immediate)
        ),
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
            ) => Some(Instruction.Plop3intPipePredPredRegPredImm(pu, pp, srcB, pr, uimm8, modifiers, guard))
        case _ => None
      }
  }
  object Plop3intPipePredPredRegRegImm extends Definition(
        "Plop3intPipePredPredRegRegImm",
        "PLOP3int_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pp", OperandKind.Predicate),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uimm8", OperandKind.Immediate)
        ),
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
            ) => Some(Instruction.Plop3intPipePredPredRegRegImm(pu, pp, srcB, srcC, uimm8, modifiers, guard))
        case _ => None
      }
  }
  object Plop3intPipePredPredRegRegRegImmImm extends Definition(
        "Plop3intPipePredPredRegRegRegImmImm",
        "PLOP3int_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uimm8", OperandKind.Immediate),
          Slot("vimm8", OperandKind.Immediate)
        ),
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
            ) => Some(Instruction.Plop3intPipePredPredRegRegRegImmImm(
            pu,
            pv,
            srcA,
            srcB,
            srcC,
            uimm8,
            vimm8,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object Plop3intPipePredRegAddressImmRegImm extends Definition(
        "Plop3intPipePredRegAddressImmRegImm",
        "PLOP3int_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("uimm8", OperandKind.Immediate)
        ),
        Vec("plop3_lut_1out_3reg__RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(uimm8)
            ) => Some(Instruction.Plop3intPipePredRegAddressImmRegImm(
            pu,
            srcA,
            srcB,
            sb_offset,
            srcC,
            uimm8,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object Plop3intPipePredRegConstRegImm extends Definition(
        "Plop3intPipePredRegConstRegImm",
        "PLOP3int_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register),
          Slot("uimm8", OperandKind.Immediate)
        ),
        Vec("plop3_lut_1out_3reg__RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(uimm8)
            ) => Some(Instruction.Plop3intPipePredRegConstRegImm(pu, srcA, sb, srcC, uimm8, modifiers, guard))
        case _ => None
      }
  }
  object Plop3intPipePredRegRegRegImm extends Definition(
        "Plop3intPipePredRegRegRegImm",
        "PLOP3int_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uimm8", OperandKind.Immediate)
        ),
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
            ) => Some(Instruction.Plop3intPipePredRegRegRegImm(pu, srcA, srcB, srcC, uimm8, modifiers, guard))
        case _ => None
      }
  }
  object PmtrigImm extends Definition("PmtrigImm", "PMTRIG", Vec(Slot("imm", OperandKind.Immediate)), Vec("pmtrig_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(imm)) => Some(Instruction.PmtrigImm(imm, modifiers, guard))
        case _ => None
      }
  }
  object PmtrigPredImm extends Definition(
        "PmtrigPredImm",
        "PMTRIG",
        Vec(Slot("pp", OperandKind.Predicate), Slot("imm", OperandKind.Immediate)),
        Vec("pmtrig_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.Immediate(imm)) =>
          Some(Instruction.PmtrigPredImm(pp, imm, modifiers, guard))
        case _ => None
      }
  }
  object PmtrigfePipeImm
      extends Definition("PmtrigfePipeImm", "PMTRIGfe_pipe", Vec(Slot("imm", OperandKind.Immediate)), Vec("pmtrig_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(imm)) => Some(Instruction.PmtrigfePipeImm(imm, modifiers, guard))
        case _ => None
      }
  }
  object PmtrigfePipePredImm extends Definition(
        "PmtrigfePipePredImm",
        "PMTRIGfe_pipe",
        Vec(Slot("pp", OperandKind.Predicate), Slot("imm", OperandKind.Immediate)),
        Vec("pmtrig_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.Immediate(imm)) =>
          Some(Instruction.PmtrigfePipePredImm(pp, imm, modifiers, guard))
        case _ => None
      }
  }
  object PopcRegAddressImm extends Definition(
        "PopcRegAddressImm",
        "POPC",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("popc__RCxR_RCxR")
      ) {
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
  object PopcRegConst extends Definition(
        "PopcRegConst",
        "POPC",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.ConstantBank)),
        Vec("Const_POPC", "popc__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(sb)) =>
          Some(Instruction.PopcRegConst(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object PopcRegImm extends Definition(
        "PopcRegImm",
        "POPC",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("Imm_POPC", "popc__RuIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.PopcRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object PopcRegReg extends Definition(
        "PopcRegReg",
        "POPC",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("POPC", "popc__RRR_RRR", "popc__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.PopcRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object PopcRegRegConst extends Definition(
        "PopcRegRegConst",
        "POPC",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const POPC")
      ) {
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
  object PopcRegRegImm extends Definition(
        "PopcRegRegImm",
        "POPC",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("Imm POPC")
      ) {
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
  object PopcRegRegReg extends Definition(
        "PopcRegRegReg",
        "POPC",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("POPC")
      ) {
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
  object PopcfmaxPipeRegRegConst extends Definition(
        "PopcfmaxPipeRegRegConst",
        "POPCfmaX_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const POPC")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.PopcfmaxPipeRegRegConst(dst, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object PopcfmaxPipeRegRegImm extends Definition(
        "PopcfmaxPipeRegRegImm",
        "POPCfmaX_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("Imm POPC")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.PopcfmaxPipeRegRegImm(dst, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object PopcfmaxPipeRegRegReg extends Definition(
        "PopcfmaxPipeRegRegReg",
        "POPCfmaX_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("POPC")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.PopcfmaxPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object PopcmioPipeRegAddressImm extends Definition(
        "PopcmioPipeRegAddressImm",
        "POPCmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("popc__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.PopcmioPipeRegAddressImm(dst, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object PopcmioPipeRegConst extends Definition(
        "PopcmioPipeRegConst",
        "POPCmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.ConstantBank)),
        Vec("Const_POPC", "popc__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(sb)) =>
          Some(Instruction.PopcmioPipeRegConst(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object PopcmioPipeRegImm extends Definition(
        "PopcmioPipeRegImm",
        "POPCmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("Imm_POPC", "popc__RuIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.PopcmioPipeRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object PopcmioPipeRegReg extends Definition(
        "PopcmioPipeRegReg",
        "POPCmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("POPC", "popc__RRR_RRR", "popc__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.PopcmioPipeRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object PreexitNoOperands extends Definition("PreexitNoOperands", "PREEXIT", Vec(), Vec("preexit_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.PreexitNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object PreexitcbuPipeNoOperands
      extends Definition("PreexitcbuPipeNoOperands", "PREEXITcbu_pipe", Vec(), Vec("preexit_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.PreexitcbuPipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object PretConst extends Definition(
        "PretConst",
        "PRET",
        Vec(Slot("srcConst", OperandKind.ConstantBank)),
        Vec("PRET c", "PRET_c")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(srcConst)) => Some(Instruction.PretConst(srcConst, modifiers, guard))
        case _ => None
      }
  }
  object PretImm extends Definition("PretImm", "PRET", Vec(Slot("sImm", OperandKind.Immediate)), Vec("PRET")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sImm)) => Some(Instruction.PretImm(sImm, modifiers, guard))
        case _ => None
      }
  }
  object PretbruPipeConst extends Definition(
        "PretbruPipeConst",
        "PRETbru_pipe",
        Vec(Slot("srcConst", OperandKind.ConstantBank)),
        Vec("PRET c", "PRET_c")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(srcConst)) =>
          Some(Instruction.PretbruPipeConst(srcConst, modifiers, guard))
        case _ => None
      }
  }
  object PretbruPipeImm
      extends Definition("PretbruPipeImm", "PRETbru_pipe", Vec(Slot("sImm", OperandKind.Immediate)), Vec("PRET")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sImm)) => Some(Instruction.PretbruPipeImm(sImm, modifiers, guard))
        case _ => None
      }
  }
  object PrmtRegRegAddressImmReg extends Definition(
        "PrmtRegRegAddressImmReg",
        "PRMT",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("prmt__RCxR_RCxR")
      ) {
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
  object PrmtRegRegConstReg extends Definition(
        "PrmtRegRegConstReg",
        "PRMT",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Const PRMT", "Const_PRMT", "prmt__RCR_RCR")
      ) {
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
  object PrmtRegRegImmReg extends Definition(
        "PrmtRegRegImmReg",
        "PRMT",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm PRMT", "Imm_PRMT", "prmt__RuIR_RIR")
      ) {
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
  object PrmtRegRegRegAddressImm extends Definition(
        "PrmtRegRegRegAddressImm",
        "PRMT",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Address),
          Slot("sc_offset", OperandKind.Immediate)
        ),
        Vec("prmt__RRCx_RRCx")
      ) {
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
  object PrmtRegRegRegConst extends Definition(
        "PrmtRegRegRegConst",
        "PRMT",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.ConstantBank)
        ),
        Vec("Const1 PRMT", "Const1_PRMT", "prmt__RRC_RRC")
      ) {
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
  object PrmtRegRegRegImm extends Definition(
        "PrmtRegRegRegImm",
        "PRMT",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate)
        ),
        Vec("prmt__RRuI_RRI")
      ) {
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
  object PrmtRegRegRegReg extends Definition(
        "PrmtRegRegRegReg",
        "PRMT",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("PRMT", "prmt__RRR_RRR", "prmt__RRU_RRU", "prmt__RUR_RUR")
      ) {
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
  object PrmtfauPipeRegRegConstReg extends Definition(
        "PrmtfauPipeRegRegConstReg",
        "PRMTfau_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Const PRMT")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.PrmtfauPipeRegRegConstReg(dst, srcA, srcConst, srcC, modifiers, guard))
        case _ => None
      }
  }
  object PrmtfauPipeRegRegImmReg extends Definition(
        "PrmtfauPipeRegRegImmReg",
        "PRMTfau_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm PRMT")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.PrmtfauPipeRegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object PrmtfauPipeRegRegRegConst extends Definition(
        "PrmtfauPipeRegRegRegConst",
        "PRMTfau_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const1 PRMT")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.PrmtfauPipeRegRegRegConst(dst, srcA, srcB, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object PrmtfauPipeRegRegRegReg extends Definition(
        "PrmtfauPipeRegRegRegReg",
        "PRMTfau_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("PRMT")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.PrmtfauPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object PrmtfmaiPipeRegRegConstReg extends Definition(
        "PrmtfmaiPipeRegRegConstReg",
        "PRMTfmai_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Const_PRMT")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.PrmtfmaiPipeRegRegConstReg(dst, srcA, srcConst, srcC, modifiers, guard))
        case _ => None
      }
  }
  object PrmtfmaiPipeRegRegImmReg extends Definition(
        "PrmtfmaiPipeRegRegImmReg",
        "PRMTfmai_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm_PRMT")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.PrmtfmaiPipeRegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object PrmtfmaiPipeRegRegRegConst extends Definition(
        "PrmtfmaiPipeRegRegRegConst",
        "PRMTfmai_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const1_PRMT")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.PrmtfmaiPipeRegRegRegConst(dst, srcA, srcB, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object PrmtfmaiPipeRegRegRegReg extends Definition(
        "PrmtfmaiPipeRegRegRegReg",
        "PRMTfmai_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("PRMT")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.PrmtfmaiPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object PrmtintPipeRegRegAddressImmReg extends Definition(
        "PrmtintPipeRegRegAddressImmReg",
        "PRMTint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("prmt__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.PrmtintPipeRegRegAddressImmReg(dst, srcA, srcB, sb_offset, srcC, modifiers, guard))
        case _ => None
      }
  }
  object PrmtintPipeRegRegConstReg extends Definition(
        "PrmtintPipeRegRegConstReg",
        "PRMTint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("prmt__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.PrmtintPipeRegRegConstReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object PrmtintPipeRegRegImmReg extends Definition(
        "PrmtintPipeRegRegImmReg",
        "PRMTint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("prmt__RuIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.PrmtintPipeRegRegImmReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object PrmtintPipeRegRegRegAddressImm extends Definition(
        "PrmtintPipeRegRegRegAddressImm",
        "PRMTint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Address),
          Slot("sc_offset", OperandKind.Immediate)
        ),
        Vec("prmt__RRCx_RRCx")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Address(srcC),
              InstructionOperand.Immediate(sc_offset)
            ) => Some(Instruction.PrmtintPipeRegRegRegAddressImm(dst, srcA, srcB, srcC, sc_offset, modifiers, guard))
        case _ => None
      }
  }
  object PrmtintPipeRegRegRegConst extends Definition(
        "PrmtintPipeRegRegRegConst",
        "PRMTint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.ConstantBank)
        ),
        Vec("prmt__RRC_RRC")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(sc)
            ) => Some(Instruction.PrmtintPipeRegRegRegConst(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object PrmtintPipeRegRegRegImm extends Definition(
        "PrmtintPipeRegRegRegImm",
        "PRMTint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate)
        ),
        Vec("prmt__RRuI_RRI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.PrmtintPipeRegRegRegImm(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object PrmtintPipeRegRegRegReg extends Definition(
        "PrmtintPipeRegRegRegReg",
        "PRMTint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("prmt__RRR_RRR", "prmt__RRU_RRU", "prmt__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.PrmtintPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object PrmtxluPipeRegRegConstReg extends Definition(
        "PrmtxluPipeRegRegConstReg",
        "PRMTxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Const PRMT")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.PrmtxluPipeRegRegConstReg(dst, srcA, srcConst, srcC, modifiers, guard))
        case _ => None
      }
  }
  object PrmtxluPipeRegRegImmReg extends Definition(
        "PrmtxluPipeRegRegImmReg",
        "PRMTxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm PRMT")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.PrmtxluPipeRegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object PrmtxluPipeRegRegRegConst extends Definition(
        "PrmtxluPipeRegRegRegConst",
        "PRMTxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const1 PRMT")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.PrmtxluPipeRegRegRegConst(dst, srcA, srcB, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object PrmtxluPipeRegRegRegReg extends Definition(
        "PrmtxluPipeRegRegRegReg",
        "PRMTxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("PRMT")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.PrmtxluPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object PsetRegPredPred extends Definition(
        "PsetRegPredPred",
        "PSET",
        Vec(Slot("dst", OperandKind.Register), Slot("pc", OperandKind.Predicate), Slot("pb", OperandKind.Predicate)),
        Vec("NoBop PSET", "NoBop_PSET")
      ) {
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
  object PsetRegPredPredPred extends Definition(
        "PsetRegPredPredPred",
        "PSET",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pc", OperandKind.Predicate),
          Slot("pb", OperandKind.Predicate),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("PSET")
      ) {
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
  object PsetpPredPredPred extends Definition(
        "PsetpPredPredPred",
        "PSETP",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("pc", OperandKind.Predicate),
          Slot("pb", OperandKind.Predicate)
        ),
        Vec("NoBop PSETP", "NoBop_PSETP", "psetp_simple_")
      ) {
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
  object PsetpPredPredPredPredPred extends Definition(
        "PsetpPredPredPredPredPred",
        "PSETP",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("pc", OperandKind.Predicate),
          Slot("pb", OperandKind.Predicate),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("PSETP", "psetp_", "psetp_uniform_")
      ) {
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
  object PsetpfauPipePredPredPred extends Definition(
        "PsetpfauPipePredPredPred",
        "PSETPfau_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("pc", OperandKind.Predicate),
          Slot("pb", OperandKind.Predicate)
        ),
        Vec("NoBop PSETP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(pc),
              InstructionOperand.Predicate(pb)
            ) => Some(Instruction.PsetpfauPipePredPredPred(dstPredicate, pc, pb, modifiers, guard))
        case _ => None
      }
  }
  object PsetpfauPipePredPredPredPredPred extends Definition(
        "PsetpfauPipePredPredPredPredPred",
        "PSETPfau_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("pc", OperandKind.Predicate),
          Slot("pb", OperandKind.Predicate),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("PSETP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Predicate(pc),
              InstructionOperand.Predicate(pb),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.PsetpfauPipePredPredPredPredPred(dstPredicate, nPd, pc, pb, pa, modifiers, guard))
        case _ => None
      }
  }
  object PsetpfxuPipePredPredPred extends Definition(
        "PsetpfxuPipePredPredPred",
        "PSETPfxu_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("pc", OperandKind.Predicate),
          Slot("pb", OperandKind.Predicate)
        ),
        Vec("NoBop_PSETP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(pc),
              InstructionOperand.Predicate(pb)
            ) => Some(Instruction.PsetpfxuPipePredPredPred(dstPredicate, pc, pb, modifiers, guard))
        case _ => None
      }
  }
  object PsetpfxuPipePredPredPredPredPred extends Definition(
        "PsetpfxuPipePredPredPredPredPred",
        "PSETPfxu_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("pc", OperandKind.Predicate),
          Slot("pb", OperandKind.Predicate),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("PSETP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Predicate(pc),
              InstructionOperand.Predicate(pb),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.PsetpfxuPipePredPredPredPredPred(dstPredicate, nPd, pc, pb, pa, modifiers, guard))
        case _ => None
      }
  }
  object PsetpintPipePredPredPred extends Definition(
        "PsetpintPipePredPredPred",
        "PSETPint_pipe",
        Vec(Slot("pu", OperandKind.Predicate), Slot("pp", OperandKind.Predicate), Slot("pq", OperandKind.Predicate)),
        Vec("psetp_simple_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Predicate(pq)
            ) => Some(Instruction.PsetpintPipePredPredPred(pu, pp, pq, modifiers, guard))
        case _ => None
      }
  }
  object PsetpintPipePredPredPredPredPred extends Definition(
        "PsetpintPipePredPredPredPredPred",
        "PSETPint_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("pp", OperandKind.Predicate),
          Slot("pq", OperandKind.Predicate),
          Slot("pr", OperandKind.Predicate)
        ),
        Vec("psetp_", "psetp_uniform_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Predicate(pp),
              InstructionOperand.Predicate(pq),
              InstructionOperand.Predicate(pr)
            ) => Some(Instruction.PsetpintPipePredPredPredPredPred(pu, pv, pp, pq, pr, modifiers, guard))
        case _ => None
      }
  }
  object PsetpxluPipePredPredPred extends Definition(
        "PsetpxluPipePredPredPred",
        "PSETPxlu_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("pc", OperandKind.Predicate),
          Slot("pb", OperandKind.Predicate)
        ),
        Vec("NoBop PSETP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(pc),
              InstructionOperand.Predicate(pb)
            ) => Some(Instruction.PsetpxluPipePredPredPred(dstPredicate, pc, pb, modifiers, guard))
        case _ => None
      }
  }
  object PsetpxluPipePredPredPredPredPred extends Definition(
        "PsetpxluPipePredPredPredPredPred",
        "PSETPxlu_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("pc", OperandKind.Predicate),
          Slot("pb", OperandKind.Predicate),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("PSETP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Predicate(pc),
              InstructionOperand.Predicate(pb),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.PsetpxluPipePredPredPredPredPred(dstPredicate, nPd, pc, pb, pa, modifiers, guard))
        case _ => None
      }
  }
  object PsetfauPipeRegPredPred extends Definition(
        "PsetfauPipeRegPredPred",
        "PSETfau_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("pc", OperandKind.Predicate), Slot("pb", OperandKind.Predicate)),
        Vec("NoBop PSET")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pc),
              InstructionOperand.Predicate(pb)
            ) => Some(Instruction.PsetfauPipeRegPredPred(dst, pc, pb, modifiers, guard))
        case _ => None
      }
  }
  object PsetfauPipeRegPredPredPred extends Definition(
        "PsetfauPipeRegPredPredPred",
        "PSETfau_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pc", OperandKind.Predicate),
          Slot("pb", OperandKind.Predicate),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("PSET")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pc),
              InstructionOperand.Predicate(pb),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.PsetfauPipeRegPredPredPred(dst, pc, pb, pa, modifiers, guard))
        case _ => None
      }
  }
  object PsetfxuPipeRegPredPred extends Definition(
        "PsetfxuPipeRegPredPred",
        "PSETfxu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("pc", OperandKind.Predicate), Slot("pb", OperandKind.Predicate)),
        Vec("NoBop_PSET")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pc),
              InstructionOperand.Predicate(pb)
            ) => Some(Instruction.PsetfxuPipeRegPredPred(dst, pc, pb, modifiers, guard))
        case _ => None
      }
  }
  object PsetfxuPipeRegPredPredPred extends Definition(
        "PsetfxuPipeRegPredPredPred",
        "PSETfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pc", OperandKind.Predicate),
          Slot("pb", OperandKind.Predicate),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("PSET")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pc),
              InstructionOperand.Predicate(pb),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.PsetfxuPipeRegPredPredPred(dst, pc, pb, pa, modifiers, guard))
        case _ => None
      }
  }
  object PsetxluPipeRegPredPred extends Definition(
        "PsetxluPipeRegPredPred",
        "PSETxlu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("pc", OperandKind.Predicate), Slot("pb", OperandKind.Predicate)),
        Vec("NoBop PSET")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pc),
              InstructionOperand.Predicate(pb)
            ) => Some(Instruction.PsetxluPipeRegPredPred(dst, pc, pb, modifiers, guard))
        case _ => None
      }
  }
  object PsetxluPipeRegPredPredPred extends Definition(
        "PsetxluPipeRegPredPredPred",
        "PSETxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pc", OperandKind.Predicate),
          Slot("pb", OperandKind.Predicate),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("PSET")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pc),
              InstructionOperand.Predicate(pb),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.PsetxluPipeRegPredPredPred(dst, pc, pb, pa, modifiers, guard))
        case _ => None
      }
  }
  object Qadd4RegRegImm extends Definition(
        "Qadd4RegRegImm",
        "QADD4",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sc", OperandKind.Immediate)),
        Vec("qadd4_rb_imm_")
      ) {
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
  object Qadd4RegRegReg extends Definition(
        "Qadd4RegRegReg",
        "QADD4",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcC", OperandKind.Register)),
        Vec("qadd4_rc_", "qadd4_urc_")
      ) {
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
  object Qadd4fp16PipeRegRegImm extends Definition(
        "Qadd4fp16PipeRegRegImm",
        "QADD4fp16_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sc", OperandKind.Immediate)),
        Vec("qadd4_rb_imm_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.Qadd4fp16PipeRegRegImm(dst, srcA, sc, modifiers, guard))
        case _ => None
      }
  }
  object Qadd4fp16PipeRegRegReg extends Definition(
        "Qadd4fp16PipeRegRegReg",
        "QADD4fp16_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcC", OperandKind.Register)),
        Vec("qadd4_rc_", "qadd4_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Qadd4fp16PipeRegRegReg(dst, srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Qfma4RegRegImmReg extends Definition(
        "Qfma4RegRegImmReg",
        "QFMA4",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("qfma4_imm_rc_")
      ) {
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
  object Qfma4RegRegRegImm extends Definition(
        "Qfma4RegRegRegImm",
        "QFMA4",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate)
        ),
        Vec("qfma4_rb_imm_")
      ) {
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
  object Qfma4RegRegRegReg extends Definition(
        "Qfma4RegRegRegReg",
        "QFMA4",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("qfma4_rb_rc_", "qfma4_rb_urc_", "qfma4_urb_rc_")
      ) {
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
  object Qfma4fp16PipeRegRegImmReg extends Definition(
        "Qfma4fp16PipeRegRegImmReg",
        "QFMA4fp16_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("qfma4_imm_rc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Qfma4fp16PipeRegRegImmReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Qfma4fp16PipeRegRegRegImm extends Definition(
        "Qfma4fp16PipeRegRegRegImm",
        "QFMA4fp16_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate)
        ),
        Vec("qfma4_rb_imm_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.Qfma4fp16PipeRegRegRegImm(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object Qfma4fp16PipeRegRegRegReg extends Definition(
        "Qfma4fp16PipeRegRegRegReg",
        "QFMA4fp16_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("qfma4_rb_rc_", "qfma4_rb_urc_", "qfma4_urb_rc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Qfma4fp16PipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object QgmmaRegAddressReg extends Definition(
        "QgmmaRegAddressReg",
        "QGMMA",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address), Slot("srcC", OperandKind.Register)),
        Vec("qgmma_URa_Rc_")
      ) {
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
  object QgmmaRegAddressRegPred extends Definition(
        "QgmmaRegAddressRegPred",
        "QGMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("qgmma_URa_Rc_")
      ) {
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
  object QgmmaRegAddressRegPredReg extends Definition(
        "QgmmaRegAddressRegPredReg",
        "QGMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("re", OperandKind.Register)
        ),
        Vec("qgmma_sparse_URa_Rc_")
      ) {
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
  object QgmmaRegAddressRegPredRegImm extends Definition(
        "QgmmaRegAddressRegPredRegImm",
        "QGMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("re", OperandKind.Register),
          Slot("id", OperandKind.Immediate)
        ),
        Vec("qgmma_sparse_URa_Rc_")
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
            ) => Some(Instruction.QgmmaRegAddressRegPredRegImm(dst, srcA, srcC, uPp, re, id, modifiers, guard))
        case _ => None
      }
  }
  object QgmmaRegAddressRegReg extends Definition(
        "QgmmaRegAddressRegReg",
        "QGMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("qgmma_URa_Rb_Rc_", "qgmma_sparse_URa_Rc_")
      ) {
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
  object QgmmaRegAddressRegRegImm extends Definition(
        "QgmmaRegAddressRegRegImm",
        "QGMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("re", OperandKind.Register),
          Slot("id", OperandKind.Immediate)
        ),
        Vec("qgmma_sparse_URa_Rc_")
      ) {
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
  object QgmmaRegAddressRegRegPred extends Definition(
        "QgmmaRegAddressRegRegPred",
        "QGMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("qgmma_URa_Rb_Rc_")
      ) {
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
  object QgmmaRegAddressRegRegPredReg extends Definition(
        "QgmmaRegAddressRegRegPredReg",
        "QGMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("re", OperandKind.Register)
        ),
        Vec("qgmma_sparse_URa_Rb_Rc_")
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
            ) => Some(Instruction.QgmmaRegAddressRegRegPredReg(dst, srcA, srcB, srcC, uPp, re, modifiers, guard))
        case _ => None
      }
  }
  object QgmmaRegAddressRegRegPredRegImm extends Definition(
        "QgmmaRegAddressRegRegPredRegImm",
        "QGMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("re", OperandKind.Register),
          Slot("id", OperandKind.Immediate)
        ),
        Vec("qgmma_sparse_URa_Rb_Rc_")
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
            ) => Some(Instruction.QgmmaRegAddressRegRegPredRegImm(dst, srcA, srcB, srcC, uPp, re, id, modifiers, guard))
        case _ => None
      }
  }
  object QgmmaRegAddressRegRegReg extends Definition(
        "QgmmaRegAddressRegRegReg",
        "QGMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("re", OperandKind.Register)
        ),
        Vec("qgmma_sparse_URa_Rb_Rc_")
      ) {
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
  object QgmmaRegAddressRegRegRegImm extends Definition(
        "QgmmaRegAddressRegRegRegImm",
        "QGMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("re", OperandKind.Register),
          Slot("id", OperandKind.Immediate)
        ),
        Vec("qgmma_sparse_URa_Rb_Rc_")
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
            ) => Some(Instruction.QgmmaRegAddressRegRegRegImm(dst, srcA, srcB, srcC, re, id, modifiers, guard))
        case _ => None
      }
  }
  object QgmmaRegRegAddressReg extends Definition(
        "QgmmaRegRegAddressReg",
        "QGMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("qgmma_Ra_URb_Rc_")
      ) {
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
  object QgmmaRegRegAddressRegPred extends Definition(
        "QgmmaRegRegAddressRegPred",
        "QGMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("qgmma_Ra_URb_Rc_")
      ) {
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
  object QgmmaRegRegAddressRegPredReg extends Definition(
        "QgmmaRegRegAddressRegPredReg",
        "QGMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("re", OperandKind.Register)
        ),
        Vec("qgmma_sparse_Ra_URb_Rc_")
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
            ) => Some(Instruction.QgmmaRegRegAddressRegPredReg(dst, srcA, srcB, srcC, uPp, re, modifiers, guard))
        case _ => None
      }
  }
  object QgmmaRegRegAddressRegPredRegImm extends Definition(
        "QgmmaRegRegAddressRegPredRegImm",
        "QGMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("re", OperandKind.Register),
          Slot("id", OperandKind.Immediate)
        ),
        Vec("qgmma_sparse_Ra_URb_Rc_")
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
            ) => Some(Instruction.QgmmaRegRegAddressRegPredRegImm(dst, srcA, srcB, srcC, uPp, re, id, modifiers, guard))
        case _ => None
      }
  }
  object QgmmaRegRegAddressRegReg extends Definition(
        "QgmmaRegRegAddressRegReg",
        "QGMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("re", OperandKind.Register)
        ),
        Vec("qgmma_sparse_Ra_URb_Rc_")
      ) {
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
  object QgmmaRegRegAddressRegRegImm extends Definition(
        "QgmmaRegRegAddressRegRegImm",
        "QGMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("re", OperandKind.Register),
          Slot("id", OperandKind.Immediate)
        ),
        Vec("qgmma_sparse_Ra_URb_Rc_")
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
            ) => Some(Instruction.QgmmaRegRegAddressRegRegImm(dst, srcA, srcB, srcC, re, id, modifiers, guard))
        case _ => None
      }
  }
  object QgmmamioPipeRegAddressReg extends Definition(
        "QgmmamioPipeRegAddressReg",
        "QGMMAmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address), Slot("srcC", OperandKind.Register)),
        Vec("qgmma_URa_Rc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.QgmmamioPipeRegAddressReg(dst, srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object QgmmamioPipeRegAddressRegPred extends Definition(
        "QgmmamioPipeRegAddressRegPred",
        "QGMMAmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("qgmma_URa_Rc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.QgmmamioPipeRegAddressRegPred(dst, srcA, srcC, uPp, modifiers, guard))
        case _ => None
      }
  }
  object QgmmamioPipeRegAddressRegPredReg extends Definition(
        "QgmmamioPipeRegAddressRegPredReg",
        "QGMMAmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("re", OperandKind.Register)
        ),
        Vec("qgmma_sparse_URa_Rc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Register(re)
            ) => Some(Instruction.QgmmamioPipeRegAddressRegPredReg(dst, srcA, srcC, uPp, re, modifiers, guard))
        case _ => None
      }
  }
  object QgmmamioPipeRegAddressRegPredRegImm extends Definition(
        "QgmmamioPipeRegAddressRegPredRegImm",
        "QGMMAmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("re", OperandKind.Register),
          Slot("id", OperandKind.Immediate)
        ),
        Vec("qgmma_sparse_URa_Rc_")
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
            ) => Some(Instruction.QgmmamioPipeRegAddressRegPredRegImm(dst, srcA, srcC, uPp, re, id, modifiers, guard))
        case _ => None
      }
  }
  object QgmmamioPipeRegAddressRegReg extends Definition(
        "QgmmamioPipeRegAddressRegReg",
        "QGMMAmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("qgmma_URa_Rb_Rc_", "qgmma_sparse_URa_Rc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.QgmmamioPipeRegAddressRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object QgmmamioPipeRegAddressRegRegImm extends Definition(
        "QgmmamioPipeRegAddressRegRegImm",
        "QGMMAmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("re", OperandKind.Register),
          Slot("id", OperandKind.Immediate)
        ),
        Vec("qgmma_sparse_URa_Rc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(re),
              InstructionOperand.Immediate(id)
            ) => Some(Instruction.QgmmamioPipeRegAddressRegRegImm(dst, srcA, srcC, re, id, modifiers, guard))
        case _ => None
      }
  }
  object QgmmamioPipeRegAddressRegRegPred extends Definition(
        "QgmmamioPipeRegAddressRegRegPred",
        "QGMMAmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("qgmma_URa_Rb_Rc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.QgmmamioPipeRegAddressRegRegPred(dst, srcA, srcB, srcC, uPp, modifiers, guard))
        case _ => None
      }
  }
  object QgmmamioPipeRegAddressRegRegPredReg extends Definition(
        "QgmmamioPipeRegAddressRegRegPredReg",
        "QGMMAmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("re", OperandKind.Register)
        ),
        Vec("qgmma_sparse_URa_Rb_Rc_")
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
            ) => Some(Instruction.QgmmamioPipeRegAddressRegRegPredReg(dst, srcA, srcB, srcC, uPp, re, modifiers, guard))
        case _ => None
      }
  }
  object QgmmamioPipeRegAddressRegRegPredRegImm extends Definition(
        "QgmmamioPipeRegAddressRegRegPredRegImm",
        "QGMMAmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("re", OperandKind.Register),
          Slot("id", OperandKind.Immediate)
        ),
        Vec("qgmma_sparse_URa_Rb_Rc_")
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
          Some(Instruction.QgmmamioPipeRegAddressRegRegPredRegImm(dst, srcA, srcB, srcC, uPp, re, id, modifiers, guard))
        case _ => None
      }
  }
  object QgmmamioPipeRegAddressRegRegReg extends Definition(
        "QgmmamioPipeRegAddressRegRegReg",
        "QGMMAmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("re", OperandKind.Register)
        ),
        Vec("qgmma_sparse_URa_Rb_Rc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(re)
            ) => Some(Instruction.QgmmamioPipeRegAddressRegRegReg(dst, srcA, srcB, srcC, re, modifiers, guard))
        case _ => None
      }
  }
  object QgmmamioPipeRegAddressRegRegRegImm extends Definition(
        "QgmmamioPipeRegAddressRegRegRegImm",
        "QGMMAmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("re", OperandKind.Register),
          Slot("id", OperandKind.Immediate)
        ),
        Vec("qgmma_sparse_URa_Rb_Rc_")
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
            ) => Some(Instruction.QgmmamioPipeRegAddressRegRegRegImm(dst, srcA, srcB, srcC, re, id, modifiers, guard))
        case _ => None
      }
  }
  object QgmmamioPipeRegRegAddressReg extends Definition(
        "QgmmamioPipeRegRegAddressReg",
        "QGMMAmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("qgmma_Ra_URb_Rc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.QgmmamioPipeRegRegAddressReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object QgmmamioPipeRegRegAddressRegPred extends Definition(
        "QgmmamioPipeRegRegAddressRegPred",
        "QGMMAmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("qgmma_Ra_URb_Rc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.QgmmamioPipeRegRegAddressRegPred(dst, srcA, srcB, srcC, uPp, modifiers, guard))
        case _ => None
      }
  }
  object QgmmamioPipeRegRegAddressRegPredReg extends Definition(
        "QgmmamioPipeRegRegAddressRegPredReg",
        "QGMMAmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("re", OperandKind.Register)
        ),
        Vec("qgmma_sparse_Ra_URb_Rc_")
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
            ) => Some(Instruction.QgmmamioPipeRegRegAddressRegPredReg(dst, srcA, srcB, srcC, uPp, re, modifiers, guard))
        case _ => None
      }
  }
  object QgmmamioPipeRegRegAddressRegPredRegImm extends Definition(
        "QgmmamioPipeRegRegAddressRegPredRegImm",
        "QGMMAmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("re", OperandKind.Register),
          Slot("id", OperandKind.Immediate)
        ),
        Vec("qgmma_sparse_Ra_URb_Rc_")
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
          Some(Instruction.QgmmamioPipeRegRegAddressRegPredRegImm(dst, srcA, srcB, srcC, uPp, re, id, modifiers, guard))
        case _ => None
      }
  }
  object QgmmamioPipeRegRegAddressRegReg extends Definition(
        "QgmmamioPipeRegRegAddressRegReg",
        "QGMMAmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("re", OperandKind.Register)
        ),
        Vec("qgmma_sparse_Ra_URb_Rc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(re)
            ) => Some(Instruction.QgmmamioPipeRegRegAddressRegReg(dst, srcA, srcB, srcC, re, modifiers, guard))
        case _ => None
      }
  }
  object QgmmamioPipeRegRegAddressRegRegImm extends Definition(
        "QgmmamioPipeRegRegAddressRegRegImm",
        "QGMMAmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("re", OperandKind.Register),
          Slot("id", OperandKind.Immediate)
        ),
        Vec("qgmma_sparse_Ra_URb_Rc_")
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
            ) => Some(Instruction.QgmmamioPipeRegRegAddressRegRegImm(dst, srcA, srcB, srcC, re, id, modifiers, guard))
        case _ => None
      }
  }
  object QmmaRegRegRegReg extends Definition(
        "QmmaRegRegRegReg",
        "QMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("qmma_", "qmma_rowcol_")
      ) {
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
  object QmmaRegRegRegRegPred extends Definition(
        "QmmaRegRegRegRegPred",
        "QMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("qmma_", "qmma_rowcol_")
      ) {
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
  object QmmaRegRegRegRegPredRegImm extends Definition(
        "QmmaRegRegRegRegPredRegImm",
        "QMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("re", OperandKind.Register),
          Slot("id", OperandKind.Immediate)
        ),
        Vec("qmma_sp_", "qmma_sp_rowcol_")
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
            ) => Some(Instruction.QmmaRegRegRegRegPredRegImm(dst, srcA, srcB, srcC, uPp, re, id, modifiers, guard))
        case _ => None
      }
  }
  object QmmaRegRegRegRegPredRegRegReg extends Definition(
        "QmmaRegRegRegRegPredRegRegReg",
        "QMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("re", OperandKind.Register),
          Slot("rh", OperandKind.Register),
          Slot("uRi", OperandKind.Register)
        ),
        Vec("qmma_scale_")
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
              InstructionOperand.Register(rh),
              InstructionOperand.Register(uRi)
            ) =>
          Some(Instruction.QmmaRegRegRegRegPredRegRegReg(dst, srcA, srcB, srcC, uPp, re, rh, uRi, modifiers, guard))
        case _ => None
      }
  }
  object QmmaRegRegRegRegPredRegRegRegImm extends Definition(
        "QmmaRegRegRegRegPredRegRegRegImm",
        "QMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("re", OperandKind.Register),
          Slot("rh", OperandKind.Register),
          Slot("uRi", OperandKind.Register),
          Slot("sparseId", OperandKind.Immediate)
        ),
        Vec("qmma_sp_scale_")
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
  object QmmaRegRegRegRegRegImm extends Definition(
        "QmmaRegRegRegRegRegImm",
        "QMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("re", OperandKind.Register),
          Slot("id", OperandKind.Immediate)
        ),
        Vec("qmma_sp_", "qmma_sp_rowcol_")
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
            ) => Some(Instruction.QmmaRegRegRegRegRegImm(dst, srcA, srcB, srcC, re, id, modifiers, guard))
        case _ => None
      }
  }
  object QmmaRegRegRegRegRegRegReg extends Definition(
        "QmmaRegRegRegRegRegRegReg",
        "QMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("re", OperandKind.Register),
          Slot("rh", OperandKind.Register),
          Slot("uRi", OperandKind.Register)
        ),
        Vec("qmma_scale_")
      ) {
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
  object QmmaRegRegRegRegRegRegRegImm extends Definition(
        "QmmaRegRegRegRegRegRegRegImm",
        "QMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("re", OperandKind.Register),
          Slot("rh", OperandKind.Register),
          Slot("uRi", OperandKind.Register),
          Slot("sparseId", OperandKind.Immediate)
        ),
        Vec("qmma_sp_scale_")
      ) {
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
  object Qmmafp16PipeRegRegRegReg extends Definition(
        "Qmmafp16PipeRegRegRegReg",
        "QMMAfp16_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("qmma_", "qmma_rowcol_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Qmmafp16PipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Qmmafp16PipeRegRegRegRegPred extends Definition(
        "Qmmafp16PipeRegRegRegRegPred",
        "QMMAfp16_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("qmma_", "qmma_rowcol_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.Qmmafp16PipeRegRegRegRegPred(dst, srcA, srcB, srcC, uPp, modifiers, guard))
        case _ => None
      }
  }
  object Qmmafp16PipeRegRegRegRegPredRegImm extends Definition(
        "Qmmafp16PipeRegRegRegRegPredRegImm",
        "QMMAfp16_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("re", OperandKind.Register),
          Slot("id", OperandKind.Immediate)
        ),
        Vec("qmma_sp_", "qmma_sp_rowcol_")
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
          Some(Instruction.Qmmafp16PipeRegRegRegRegPredRegImm(dst, srcA, srcB, srcC, uPp, re, id, modifiers, guard))
        case _ => None
      }
  }
  object Qmmafp16PipeRegRegRegRegPredRegRegReg extends Definition(
        "Qmmafp16PipeRegRegRegRegPredRegRegReg",
        "QMMAfp16_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("re", OperandKind.Register),
          Slot("rh", OperandKind.Register),
          Slot("uRi", OperandKind.Register)
        ),
        Vec("qmma_scale_")
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
              InstructionOperand.Register(rh),
              InstructionOperand.Register(uRi)
            ) => Some(Instruction.Qmmafp16PipeRegRegRegRegPredRegRegReg(
            dst,
            srcA,
            srcB,
            srcC,
            uPp,
            re,
            rh,
            uRi,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object Qmmafp16PipeRegRegRegRegPredRegRegRegImm extends Definition(
        "Qmmafp16PipeRegRegRegRegPredRegRegRegImm",
        "QMMAfp16_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("re", OperandKind.Register),
          Slot("rh", OperandKind.Register),
          Slot("uRi", OperandKind.Register),
          Slot("sparseId", OperandKind.Immediate)
        ),
        Vec("qmma_sp_scale_")
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
              InstructionOperand.Register(rh),
              InstructionOperand.Register(uRi),
              InstructionOperand.Immediate(sparseId)
            ) => Some(Instruction.Qmmafp16PipeRegRegRegRegPredRegRegRegImm(
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
  object Qmmafp16PipeRegRegRegRegRegImm extends Definition(
        "Qmmafp16PipeRegRegRegRegRegImm",
        "QMMAfp16_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("re", OperandKind.Register),
          Slot("id", OperandKind.Immediate)
        ),
        Vec("qmma_sp_", "qmma_sp_rowcol_")
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
            ) => Some(Instruction.Qmmafp16PipeRegRegRegRegRegImm(dst, srcA, srcB, srcC, re, id, modifiers, guard))
        case _ => None
      }
  }
  object Qmmafp16PipeRegRegRegRegRegRegReg extends Definition(
        "Qmmafp16PipeRegRegRegRegRegRegReg",
        "QMMAfp16_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("re", OperandKind.Register),
          Slot("rh", OperandKind.Register),
          Slot("uRi", OperandKind.Register)
        ),
        Vec("qmma_scale_")
      ) {
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
            ) =>
          Some(Instruction.Qmmafp16PipeRegRegRegRegRegRegReg(dst, srcA, srcB, srcC, re, rh, uRi, modifiers, guard))
        case _ => None
      }
  }
  object Qmmafp16PipeRegRegRegRegRegRegRegImm extends Definition(
        "Qmmafp16PipeRegRegRegRegRegRegRegImm",
        "QMMAfp16_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("re", OperandKind.Register),
          Slot("rh", OperandKind.Register),
          Slot("uRi", OperandKind.Register),
          Slot("sparseId", OperandKind.Immediate)
        ),
        Vec("qmma_sp_scale_")
      ) {
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
            ) => Some(Instruction.Qmmafp16PipeRegRegRegRegRegRegRegImm(
            dst,
            srcA,
            srcB,
            srcC,
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
  object Qmul4RegRegImm extends Definition(
        "Qmul4RegRegImm",
        "QMUL4",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("qmul4_imm_")
      ) {
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
  object Qmul4RegRegReg extends Definition(
        "Qmul4RegRegReg",
        "QMUL4",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("qmul4_rb_", "qmul4_urb_")
      ) {
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
  object Qmul4fp16PipeRegRegImm extends Definition(
        "Qmul4fp16PipeRegRegImm",
        "QMUL4fp16_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("qmul4_imm_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.Qmul4fp16PipeRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object Qmul4fp16PipeRegRegReg extends Definition(
        "Qmul4fp16PipeRegRegReg",
        "QMUL4fp16_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("qmul4_rb_", "qmul4_urb_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.Qmul4fp16PipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object QspcPredAddress extends Definition(
        "QspcPredAddress",
        "QSPC",
        Vec(Slot("pu", OperandKind.Predicate), Slot("srcA", OperandKind.Address)),
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
        Vec(Slot("pu", OperandKind.Predicate), Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address)),
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
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address)),
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
  object QspcmioPipePredAddress extends Definition(
        "QspcmioPipePredAddress",
        "QSPCmio_pipe",
        Vec(Slot("pu", OperandKind.Predicate), Slot("srcA", OperandKind.Address)),
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
          Some(Instruction.QspcmioPipePredAddress(pu, srcA, modifiers, guard))
        case _ => None
      }
  }
  object QspcmioPipePredRegAddress extends Definition(
        "QspcmioPipePredRegAddress",
        "QSPCmio_pipe",
        Vec(Slot("pu", OperandKind.Predicate), Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address)),
        Vec("qspc__RaNonRZ", "qspc__RaRZ", "qspc_urb__Ra32", "qspc_urb__Ra64", "qspc_urb__RaRZ")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA)
            ) => Some(Instruction.QspcmioPipePredRegAddress(pu, dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object QspcmioPipeRegAddress extends Definition(
        "QspcmioPipeRegAddress",
        "QSPCmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address)),
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
          Some(Instruction.QspcmioPipeRegAddress(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object R2bImmReg extends Definition(
        "R2bImmReg",
        "R2B",
        Vec(Slot("barname", OperandKind.Immediate), Slot("srcB", OperandKind.Register)),
        Vec("R2B", "r2b_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(barname), InstructionOperand.Register(srcB)) =>
          Some(Instruction.R2bImmReg(barname, srcB, modifiers, guard))
        case _ => None
      }
  }
  object R2bfePipeImmReg extends Definition(
        "R2bfePipeImmReg",
        "R2Bfe_pipe",
        Vec(Slot("uImm", OperandKind.Immediate), Slot("srcB", OperandKind.Register)),
        Vec("R2B")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(uImm), InstructionOperand.Register(srcB)) =>
          Some(Instruction.R2bfePipeImmReg(uImm, srcB, modifiers, guard))
        case _ => None
      }
  }
  object R2bmioPipeImmReg extends Definition(
        "R2bmioPipeImmReg",
        "R2Bmio_pipe",
        Vec(Slot("barname", OperandKind.Immediate), Slot("srcB", OperandKind.Register)),
        Vec("R2B", "r2b_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(barname), InstructionOperand.Register(srcB)) =>
          Some(Instruction.R2bmioPipeImmReg(barname, srcB, modifiers, guard))
        case _ => None
      }
  }
  object R2pReg extends Definition("R2pReg", "R2P", Vec(Slot("srcA", OperandKind.Register)), Vec("Imm_R2P")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA)) => Some(Instruction.R2pReg(srcA, modifiers, guard))
        case _ => None
      }
  }
  object R2pRegConst extends Definition(
        "R2pRegConst",
        "R2P",
        Vec(Slot("srcA", OperandKind.Register), Slot("srcConst", OperandKind.ConstantBank)),
        Vec("Const_R2P")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA), InstructionOperand.ConstantBank(srcConst)) =>
          Some(Instruction.R2pRegConst(srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object R2pRegImm extends Definition(
        "R2pRegImm",
        "R2P",
        Vec(Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("Imm_R2P")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA), InstructionOperand.Immediate(uImm)) =>
          Some(Instruction.R2pRegImm(srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object R2pRegReg extends Definition(
        "R2pRegReg",
        "R2P",
        Vec(Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("R2P")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA), InstructionOperand.Register(srcB)) =>
          Some(Instruction.R2pRegReg(srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object R2pSymbolReg extends Definition(
        "R2pSymbolReg",
        "R2P",
        Vec(Slot("pR", OperandKind.Symbol), Slot("srcA", OperandKind.Register)),
        Vec("Imm R2P", "Imm_R2P", "r2p__RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Symbol(pR), InstructionOperand.Register(srcA)) =>
          Some(Instruction.R2pSymbolReg(pR, srcA, modifiers, guard))
        case _ => None
      }
  }
  object R2pSymbolRegAddressImm extends Definition(
        "R2pSymbolRegAddressImm",
        "R2P",
        Vec(
          Slot("pR", OperandKind.Symbol),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("r2p__RCxR")
      ) {
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
  object R2pSymbolRegConst extends Definition(
        "R2pSymbolRegConst",
        "R2P",
        Vec(Slot("pR", OperandKind.Symbol), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.ConstantBank)),
        Vec("Const R2P", "Const_R2P", "r2p__RCR")
      ) {
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
  object R2pSymbolRegImm extends Definition(
        "R2pSymbolRegImm",
        "R2P",
        Vec(Slot("pR", OperandKind.Symbol), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("Imm R2P", "Imm_R2P", "r2p__RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Symbol(pR), InstructionOperand.Register(srcA), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.R2pSymbolRegImm(pR, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object R2pSymbolRegReg extends Definition(
        "R2pSymbolRegReg",
        "R2P",
        Vec(Slot("pR", OperandKind.Symbol), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("R2P", "r2p__RRR", "r2p__RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Symbol(pR), InstructionOperand.Register(srcA), InstructionOperand.Register(srcB)) =>
          Some(Instruction.R2pSymbolRegReg(pR, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object R2pfxuPipeReg
      extends Definition("R2pfxuPipeReg", "R2Pfxu_pipe", Vec(Slot("srcA", OperandKind.Register)), Vec("Imm_R2P")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA)) => Some(Instruction.R2pfxuPipeReg(srcA, modifiers, guard))
        case _ => None
      }
  }
  object R2pfxuPipeRegConst extends Definition(
        "R2pfxuPipeRegConst",
        "R2Pfxu_pipe",
        Vec(Slot("srcA", OperandKind.Register), Slot("srcConst", OperandKind.ConstantBank)),
        Vec("Const_R2P")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA), InstructionOperand.ConstantBank(srcConst)) =>
          Some(Instruction.R2pfxuPipeRegConst(srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object R2pfxuPipeRegImm extends Definition(
        "R2pfxuPipeRegImm",
        "R2Pfxu_pipe",
        Vec(Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("Imm_R2P")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA), InstructionOperand.Immediate(uImm)) =>
          Some(Instruction.R2pfxuPipeRegImm(srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object R2pfxuPipeRegReg extends Definition(
        "R2pfxuPipeRegReg",
        "R2Pfxu_pipe",
        Vec(Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("R2P")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA), InstructionOperand.Register(srcB)) =>
          Some(Instruction.R2pfxuPipeRegReg(srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object R2pfxuPipeSymbolReg extends Definition(
        "R2pfxuPipeSymbolReg",
        "R2Pfxu_pipe",
        Vec(Slot("pr", OperandKind.Symbol), Slot("srcA", OperandKind.Register)),
        Vec("Imm_R2P")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Symbol(pr), InstructionOperand.Register(srcA)) =>
          Some(Instruction.R2pfxuPipeSymbolReg(pr, srcA, modifiers, guard))
        case _ => None
      }
  }
  object R2pfxuPipeSymbolRegConst extends Definition(
        "R2pfxuPipeSymbolRegConst",
        "R2Pfxu_pipe",
        Vec(
          Slot("pr", OperandKind.Symbol),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const_R2P")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Symbol(pr),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.R2pfxuPipeSymbolRegConst(pr, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object R2pfxuPipeSymbolRegImm extends Definition(
        "R2pfxuPipeSymbolRegImm",
        "R2Pfxu_pipe",
        Vec(Slot("pr", OperandKind.Symbol), Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("Imm_R2P")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Symbol(pr),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.R2pfxuPipeSymbolRegImm(pr, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object R2pfxuPipeSymbolRegReg extends Definition(
        "R2pfxuPipeSymbolRegReg",
        "R2Pfxu_pipe",
        Vec(Slot("pr", OperandKind.Symbol), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("R2P")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Symbol(pr), InstructionOperand.Register(srcA), InstructionOperand.Register(srcB)) =>
          Some(Instruction.R2pfxuPipeSymbolRegReg(pr, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object R2pintPipeSymbolReg extends Definition(
        "R2pintPipeSymbolReg",
        "R2Pint_pipe",
        Vec(Slot("pR", OperandKind.Symbol), Slot("srcA", OperandKind.Register)),
        Vec("r2p__RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Symbol(pR), InstructionOperand.Register(srcA)) =>
          Some(Instruction.R2pintPipeSymbolReg(pR, srcA, modifiers, guard))
        case _ => None
      }
  }
  object R2pintPipeSymbolRegAddressImm extends Definition(
        "R2pintPipeSymbolRegAddressImm",
        "R2Pint_pipe",
        Vec(
          Slot("pR", OperandKind.Symbol),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("r2p__RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Symbol(pR),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.R2pintPipeSymbolRegAddressImm(pR, srcA, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object R2pintPipeSymbolRegConst extends Definition(
        "R2pintPipeSymbolRegConst",
        "R2Pint_pipe",
        Vec(Slot("pR", OperandKind.Symbol), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.ConstantBank)),
        Vec("r2p__RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Symbol(pR),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb)
            ) => Some(Instruction.R2pintPipeSymbolRegConst(pR, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  val definitions: Vec[Definition] = Vec(
    OutmioPipeRegRegReg,
    P2rReg,
    P2rRegRegConst,
    P2rRegRegImm,
    P2rRegRegReg,
    P2rRegSymbol,
    P2rRegSymbolRegAddressImm,
    P2rRegSymbolRegConst,
    P2rRegSymbolRegImm,
    P2rRegSymbolRegReg,
    P2rfxuPipeReg,
    P2rfxuPipeRegRegConst,
    P2rfxuPipeRegRegImm,
    P2rfxuPipeRegRegReg,
    P2rfxuPipeRegSymbol,
    P2rfxuPipeRegSymbolRegConst,
    P2rfxuPipeRegSymbolRegImm,
    P2rfxuPipeRegSymbolRegReg,
    P2rintPipeRegSymbol,
    P2rintPipeRegSymbolRegAddressImm,
    P2rintPipeRegSymbolRegConst,
    P2rintPipeRegSymbolRegImm,
    P2rintPipeRegSymbolRegReg,
    P2rxluPipeRegSymbol,
    P2rxluPipeRegSymbolRegConst,
    P2rxluPipeRegSymbolRegImm,
    P2rxluPipeRegSymbolRegReg,
    PbkConst,
    PbkImm,
    PbkbruPipeConst,
    PbkbruPipeImm,
    PcntConst,
    PcntImm,
    PcntbruPipeConst,
    PcntbruPipeImm,
    PexitImm,
    PexitbruPipeImm,
    PixldReg,
    PixldRegAddress,
    PixldRegPred,
    PixldRegPredAddress,
    PixldfePipeReg,
    PixldfePipeRegAddress,
    PixldfePipeRegPred,
    PixldfePipeRegPredAddress,
    PixldmioPipeReg,
    PixldmioPipeRegAddress,
    PixldmioPipeRegPred,
    PixldmioPipeRegPredAddress,
    PlongjmpConst,
    PlongjmpImm,
    PlongjmpbruPipeConst,
    PlongjmpbruPipeImm,
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
    Plop3intPipePredPredAddressImmPredImm,
    Plop3intPipePredPredAddressImmRegImm,
    Plop3intPipePredPredConstPredImm,
    Plop3intPipePredPredConstRegImm,
    Plop3intPipePredPredPredAddressImmPredImmImm,
    Plop3intPipePredPredPredAddressImmRegImmImm,
    Plop3intPipePredPredPredConstPredImmImm,
    Plop3intPipePredPredPredConstRegImmImm,
    Plop3intPipePredPredPredPred,
    Plop3intPipePredPredPredPredImm,
    Plop3intPipePredPredPredPredPredImmImm,
    Plop3intPipePredPredPredRegPredImmImm,
    Plop3intPipePredPredPredRegRegImmImm,
    Plop3intPipePredPredRegAddressImmRegImmImm,
    Plop3intPipePredPredRegConstRegImmImm,
    Plop3intPipePredPredRegPredImm,
    Plop3intPipePredPredRegRegImm,
    Plop3intPipePredPredRegRegRegImmImm,
    Plop3intPipePredRegAddressImmRegImm,
    Plop3intPipePredRegConstRegImm,
    Plop3intPipePredRegRegRegImm,
    PmtrigImm,
    PmtrigPredImm,
    PmtrigfePipeImm,
    PmtrigfePipePredImm,
    PopcRegAddressImm,
    PopcRegConst,
    PopcRegImm,
    PopcRegReg,
    PopcRegRegConst,
    PopcRegRegImm,
    PopcRegRegReg,
    PopcfmaxPipeRegRegConst,
    PopcfmaxPipeRegRegImm,
    PopcfmaxPipeRegRegReg,
    PopcmioPipeRegAddressImm,
    PopcmioPipeRegConst,
    PopcmioPipeRegImm,
    PopcmioPipeRegReg,
    PreexitNoOperands,
    PreexitcbuPipeNoOperands,
    PretConst,
    PretImm,
    PretbruPipeConst,
    PretbruPipeImm,
    PrmtRegRegAddressImmReg,
    PrmtRegRegConstReg,
    PrmtRegRegImmReg,
    PrmtRegRegRegAddressImm,
    PrmtRegRegRegConst,
    PrmtRegRegRegImm,
    PrmtRegRegRegReg,
    PrmtfauPipeRegRegConstReg,
    PrmtfauPipeRegRegImmReg,
    PrmtfauPipeRegRegRegConst,
    PrmtfauPipeRegRegRegReg,
    PrmtfmaiPipeRegRegConstReg,
    PrmtfmaiPipeRegRegImmReg,
    PrmtfmaiPipeRegRegRegConst,
    PrmtfmaiPipeRegRegRegReg,
    PrmtintPipeRegRegAddressImmReg,
    PrmtintPipeRegRegConstReg,
    PrmtintPipeRegRegImmReg,
    PrmtintPipeRegRegRegAddressImm,
    PrmtintPipeRegRegRegConst,
    PrmtintPipeRegRegRegImm,
    PrmtintPipeRegRegRegReg,
    PrmtxluPipeRegRegConstReg,
    PrmtxluPipeRegRegImmReg,
    PrmtxluPipeRegRegRegConst,
    PrmtxluPipeRegRegRegReg,
    PsetRegPredPred,
    PsetRegPredPredPred,
    PsetpPredPredPred,
    PsetpPredPredPredPredPred,
    PsetpfauPipePredPredPred,
    PsetpfauPipePredPredPredPredPred,
    PsetpfxuPipePredPredPred,
    PsetpfxuPipePredPredPredPredPred,
    PsetpintPipePredPredPred,
    PsetpintPipePredPredPredPredPred,
    PsetpxluPipePredPredPred,
    PsetpxluPipePredPredPredPredPred,
    PsetfauPipeRegPredPred,
    PsetfauPipeRegPredPredPred,
    PsetfxuPipeRegPredPred,
    PsetfxuPipeRegPredPredPred,
    PsetxluPipeRegPredPred,
    PsetxluPipeRegPredPredPred,
    Qadd4RegRegImm,
    Qadd4RegRegReg,
    Qadd4fp16PipeRegRegImm,
    Qadd4fp16PipeRegRegReg,
    Qfma4RegRegImmReg,
    Qfma4RegRegRegImm,
    Qfma4RegRegRegReg,
    Qfma4fp16PipeRegRegImmReg,
    Qfma4fp16PipeRegRegRegImm,
    Qfma4fp16PipeRegRegRegReg,
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
    QgmmamioPipeRegAddressReg,
    QgmmamioPipeRegAddressRegPred,
    QgmmamioPipeRegAddressRegPredReg,
    QgmmamioPipeRegAddressRegPredRegImm,
    QgmmamioPipeRegAddressRegReg,
    QgmmamioPipeRegAddressRegRegImm,
    QgmmamioPipeRegAddressRegRegPred,
    QgmmamioPipeRegAddressRegRegPredReg,
    QgmmamioPipeRegAddressRegRegPredRegImm,
    QgmmamioPipeRegAddressRegRegReg,
    QgmmamioPipeRegAddressRegRegRegImm,
    QgmmamioPipeRegRegAddressReg,
    QgmmamioPipeRegRegAddressRegPred,
    QgmmamioPipeRegRegAddressRegPredReg,
    QgmmamioPipeRegRegAddressRegPredRegImm,
    QgmmamioPipeRegRegAddressRegReg,
    QgmmamioPipeRegRegAddressRegRegImm,
    QmmaRegRegRegReg,
    QmmaRegRegRegRegPred,
    QmmaRegRegRegRegPredRegImm,
    QmmaRegRegRegRegPredRegRegReg,
    QmmaRegRegRegRegPredRegRegRegImm,
    QmmaRegRegRegRegRegImm,
    QmmaRegRegRegRegRegRegReg,
    QmmaRegRegRegRegRegRegRegImm,
    Qmmafp16PipeRegRegRegReg,
    Qmmafp16PipeRegRegRegRegPred,
    Qmmafp16PipeRegRegRegRegPredRegImm,
    Qmmafp16PipeRegRegRegRegPredRegRegReg,
    Qmmafp16PipeRegRegRegRegPredRegRegRegImm,
    Qmmafp16PipeRegRegRegRegRegImm,
    Qmmafp16PipeRegRegRegRegRegRegReg,
    Qmmafp16PipeRegRegRegRegRegRegRegImm,
    Qmul4RegRegImm,
    Qmul4RegRegReg,
    Qmul4fp16PipeRegRegImm,
    Qmul4fp16PipeRegRegReg,
    QspcPredAddress,
    QspcPredRegAddress,
    QspcRegAddress,
    QspcmioPipePredAddress,
    QspcmioPipePredRegAddress,
    QspcmioPipeRegAddress,
    R2bImmReg,
    R2bfePipeImmReg,
    R2bmioPipeImmReg,
    R2pReg,
    R2pRegConst,
    R2pRegImm,
    R2pRegReg,
    R2pSymbolReg,
    R2pSymbolRegAddressImm,
    R2pSymbolRegConst,
    R2pSymbolRegImm,
    R2pSymbolRegReg,
    R2pfxuPipeReg,
    R2pfxuPipeRegConst,
    R2pfxuPipeRegImm,
    R2pfxuPipeRegReg,
    R2pfxuPipeSymbolReg,
    R2pfxuPipeSymbolRegConst,
    R2pfxuPipeSymbolRegImm,
    R2pfxuPipeSymbolRegReg,
    R2pintPipeSymbolReg,
    R2pintPipeSymbolRegAddressImm,
    R2pintPipeSymbolRegConst
  )
}
