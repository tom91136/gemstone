// Generated SASS instruction definitions and checked operand constructors. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.sass
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import Instruction.*
@javax.annotation.processing.Generated(Array("sbt codegen"))
object InstructionDefinitions8 {
  object Lop3fmaiPipeRegRegConstRegImm extends Definition(
        "Lop3fmaiPipeRegRegConstRegImm",
        "LOP3fmai_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register),
          Slot("imm8", OperandKind.Immediate)
        ),
        Vec("LOP3_LUT_BConst")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(imm8)
            ) => Some(Instruction.Lop3fmaiPipeRegRegConstRegImm(dst, srcA, srcConst, srcC, imm8, modifiers, guard))
        case _ => None
      }
  }
  object Lop3fmaiPipeRegRegImmReg extends Definition(
        "Lop3fmaiPipeRegRegImmReg",
        "LOP3fmai_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("LOP3_BImm")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Lop3fmaiPipeRegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Lop3fmaiPipeRegRegImmRegImm extends Definition(
        "Lop3fmaiPipeRegRegImmRegImm",
        "LOP3fmai_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("imm8", OperandKind.Immediate)
        ),
        Vec("LOP3_LUT_BImm")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(imm8)
            ) => Some(Instruction.Lop3fmaiPipeRegRegImmRegImm(dst, srcA, uImm, srcC, imm8, modifiers, guard))
        case _ => None
      }
  }
  object Lop3fmaiPipeRegRegRegReg extends Definition(
        "Lop3fmaiPipeRegRegRegReg",
        "LOP3fmai_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("LOP3")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Lop3fmaiPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Lop3fmaiPipeRegRegRegRegImm extends Definition(
        "Lop3fmaiPipeRegRegRegRegImm",
        "LOP3fmai_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("imm8", OperandKind.Immediate)
        ),
        Vec("LOP3_LUT")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(imm8)
            ) => Some(Instruction.Lop3fmaiPipeRegRegRegRegImm(dst, srcA, srcB, srcC, imm8, modifiers, guard))
        case _ => None
      }
  }
  object Lop3fxuPipePredRegRegRegReg extends Definition(
        "Lop3fxuPipePredRegRegRegReg",
        "LOP3fxu_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("LOP3")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Lop3fxuPipePredRegRegRegReg(dstPredicate, dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Lop3fxuPipePredRegRegRegRegImm extends Definition(
        "Lop3fxuPipePredRegRegRegRegImm",
        "LOP3fxu_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("imm8", OperandKind.Immediate)
        ),
        Vec("LOP3_LUT")
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
            ) =>
          Some(Instruction.Lop3fxuPipePredRegRegRegRegImm(dstPredicate, dst, srcA, srcB, srcC, imm8, modifiers, guard))
        case _ => None
      }
  }
  object Lop3fxuPipeRegRegConstReg extends Definition(
        "Lop3fxuPipeRegRegConstReg",
        "LOP3fxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("LOP3_Bconst")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Lop3fxuPipeRegRegConstReg(dst, srcA, srcConst, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Lop3fxuPipeRegRegConstRegImm extends Definition(
        "Lop3fxuPipeRegRegConstRegImm",
        "LOP3fxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register),
          Slot("imm8", OperandKind.Immediate)
        ),
        Vec("LOP3_LUT_BConst")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(imm8)
            ) => Some(Instruction.Lop3fxuPipeRegRegConstRegImm(dst, srcA, srcConst, srcC, imm8, modifiers, guard))
        case _ => None
      }
  }
  object Lop3fxuPipeRegRegImmReg extends Definition(
        "Lop3fxuPipeRegRegImmReg",
        "LOP3fxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("LOP3_BImm")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Lop3fxuPipeRegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Lop3fxuPipeRegRegImmRegImm extends Definition(
        "Lop3fxuPipeRegRegImmRegImm",
        "LOP3fxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("imm8", OperandKind.Immediate)
        ),
        Vec("LOP3_LUT_BImm")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(imm8)
            ) => Some(Instruction.Lop3fxuPipeRegRegImmRegImm(dst, srcA, uImm, srcC, imm8, modifiers, guard))
        case _ => None
      }
  }
  object Lop3fxuPipeRegRegRegReg extends Definition(
        "Lop3fxuPipeRegRegRegReg",
        "LOP3fxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("LOP3")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Lop3fxuPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Lop3fxuPipeRegRegRegRegImm extends Definition(
        "Lop3fxuPipeRegRegRegRegImm",
        "LOP3fxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("imm8", OperandKind.Immediate)
        ),
        Vec("LOP3_LUT")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(imm8)
            ) => Some(Instruction.Lop3fxuPipeRegRegRegRegImm(dst, srcA, srcB, srcC, imm8, modifiers, guard))
        case _ => None
      }
  }
  object Lop3intPipePredRegRegAddressImmReg extends Definition(
        "Lop3intPipePredRegRegAddressImmReg",
        "LOP3int_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("lop3_noimm_optionalPp__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC)
            ) =>
          Some(Instruction.Lop3intPipePredRegRegAddressImmReg(pu, dst, srcA, srcB, sb_offset, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Lop3intPipePredRegRegAddressImmRegImm extends Definition(
        "Lop3intPipePredRegRegAddressImmRegImm",
        "LOP3int_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("imm8", OperandKind.Immediate)
        ),
        Vec("lop3_lut_optionalPp__RCxR_RCxR")
      ) {
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
            ) => Some(Instruction.Lop3intPipePredRegRegAddressImmRegImm(
            pu,
            dst,
            srcA,
            srcB,
            sb_offset,
            srcC,
            imm8,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object Lop3intPipePredRegRegAddressImmRegImmPred extends Definition(
        "Lop3intPipePredRegRegAddressImmRegImmPred",
        "LOP3int_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("imm8", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("lop3_lut__RCxR_RCxR")
      ) {
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
            ) => Some(Instruction.Lop3intPipePredRegRegAddressImmRegImmPred(
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
  object Lop3intPipePredRegRegAddressImmRegPred extends Definition(
        "Lop3intPipePredRegRegAddressImmRegPred",
        "LOP3int_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("lop3_noimm__RCxR_RCxR")
      ) {
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
            ) => Some(Instruction.Lop3intPipePredRegRegAddressImmRegPred(
            pu,
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
  object Lop3intPipePredRegRegConstReg extends Definition(
        "Lop3intPipePredRegRegConstReg",
        "LOP3int_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("lop3_noimm_optionalPp__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Lop3intPipePredRegRegConstReg(pu, dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Lop3intPipePredRegRegConstRegImm extends Definition(
        "Lop3intPipePredRegRegConstRegImm",
        "LOP3int_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register),
          Slot("imm8", OperandKind.Immediate)
        ),
        Vec("lop3_lut_optionalPp__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(imm8)
            ) => Some(Instruction.Lop3intPipePredRegRegConstRegImm(pu, dst, srcA, sb, srcC, imm8, modifiers, guard))
        case _ => None
      }
  }
  object Lop3intPipePredRegRegConstRegImmPred extends Definition(
        "Lop3intPipePredRegRegConstRegImmPred",
        "LOP3int_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register),
          Slot("imm8", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("lop3_lut__RCR_RCR")
      ) {
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
            ) =>
          Some(Instruction.Lop3intPipePredRegRegConstRegImmPred(pu, dst, srcA, sb, srcC, imm8, pp, modifiers, guard))
        case _ => None
      }
  }
  object Lop3intPipePredRegRegConstRegPred extends Definition(
        "Lop3intPipePredRegRegConstRegPred",
        "LOP3int_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("lop3_noimm__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Lop3intPipePredRegRegConstRegPred(pu, dst, srcA, sb, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object Lop3intPipePredRegRegImmReg extends Definition(
        "Lop3intPipePredRegRegImmReg",
        "LOP3int_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("lop3_imm_optionalPp__RIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Lop3intPipePredRegRegImmReg(pu, dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Lop3intPipePredRegRegImmRegImm extends Definition(
        "Lop3intPipePredRegRegImmRegImm",
        "LOP3int_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("imm8", OperandKind.Immediate)
        ),
        Vec("lop3_lut_optionalPp__RuIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(imm8)
            ) => Some(Instruction.Lop3intPipePredRegRegImmRegImm(pu, dst, srcA, sb, srcC, imm8, modifiers, guard))
        case _ => None
      }
  }
  object Lop3intPipePredRegRegImmRegImmPred extends Definition(
        "Lop3intPipePredRegRegImmRegImmPred",
        "LOP3int_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("imm8", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("lop3_lut__RuIR_RIR")
      ) {
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
            ) =>
          Some(Instruction.Lop3intPipePredRegRegImmRegImmPred(pu, dst, srcA, sb, srcC, imm8, pp, modifiers, guard))
        case _ => None
      }
  }
  object Lop3intPipePredRegRegImmRegPred extends Definition(
        "Lop3intPipePredRegRegImmRegPred",
        "LOP3int_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("lop3_imm__RIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Lop3intPipePredRegRegImmRegPred(pu, dst, srcA, sb, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object Lop3intPipePredRegRegRegReg extends Definition(
        "Lop3intPipePredRegRegRegReg",
        "LOP3int_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("lop3_noimm_optionalPp__RRR_RRR", "lop3_noimm_optionalPp__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Lop3intPipePredRegRegRegReg(pu, dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Lop3intPipePredRegRegRegRegImm extends Definition(
        "Lop3intPipePredRegRegRegRegImm",
        "LOP3int_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("imm8", OperandKind.Immediate)
        ),
        Vec("lop3_lut_optionalPp__RRR_RRR", "lop3_lut_optionalPp__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(imm8)
            ) => Some(Instruction.Lop3intPipePredRegRegRegRegImm(pu, dst, srcA, srcB, srcC, imm8, modifiers, guard))
        case _ => None
      }
  }
  object Lop3intPipePredRegRegRegRegImmPred extends Definition(
        "Lop3intPipePredRegRegRegRegImmPred",
        "LOP3int_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("imm8", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("lop3_lut__RRR_RRR", "lop3_lut__RUR_RUR")
      ) {
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
            ) =>
          Some(Instruction.Lop3intPipePredRegRegRegRegImmPred(pu, dst, srcA, srcB, srcC, imm8, pp, modifiers, guard))
        case _ => None
      }
  }
  object Lop3intPipePredRegRegRegRegPred extends Definition(
        "Lop3intPipePredRegRegRegRegPred",
        "LOP3int_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("lop3_noimm__RRR_RRR", "lop3_noimm__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Lop3intPipePredRegRegRegRegPred(pu, dst, srcA, srcB, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object Lop3intPipeRegRegAddressImmReg extends Definition(
        "Lop3intPipeRegRegAddressImmReg",
        "LOP3int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("lop3_noimm_optionalPp__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Lop3intPipeRegRegAddressImmReg(dst, srcA, srcB, sb_offset, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Lop3intPipeRegRegAddressImmRegImm extends Definition(
        "Lop3intPipeRegRegAddressImmRegImm",
        "LOP3int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("imm8", OperandKind.Immediate)
        ),
        Vec("lop3_lut_optionalPp__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(imm8)
            ) =>
          Some(Instruction.Lop3intPipeRegRegAddressImmRegImm(dst, srcA, srcB, sb_offset, srcC, imm8, modifiers, guard))
        case _ => None
      }
  }
  object Lop3intPipeRegRegAddressImmRegImmPred extends Definition(
        "Lop3intPipeRegRegAddressImmRegImmPred",
        "LOP3int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("imm8", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("lop3_lut__RCxR_RCxR")
      ) {
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
            ) => Some(Instruction.Lop3intPipeRegRegAddressImmRegImmPred(
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
  object Lop3intPipeRegRegAddressImmRegPred extends Definition(
        "Lop3intPipeRegRegAddressImmRegPred",
        "LOP3int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("lop3_noimm__RCxR_RCxR")
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
          Some(Instruction.Lop3intPipeRegRegAddressImmRegPred(dst, srcA, srcB, sb_offset, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object Lop3intPipeRegRegConstReg extends Definition(
        "Lop3intPipeRegRegConstReg",
        "LOP3int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("lop3_noimm_optionalPp__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Lop3intPipeRegRegConstReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Lop3intPipeRegRegConstRegImm extends Definition(
        "Lop3intPipeRegRegConstRegImm",
        "LOP3int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register),
          Slot("imm8", OperandKind.Immediate)
        ),
        Vec("lop3_lut_optionalPp__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(imm8)
            ) => Some(Instruction.Lop3intPipeRegRegConstRegImm(dst, srcA, sb, srcC, imm8, modifiers, guard))
        case _ => None
      }
  }
  object Lop3intPipeRegRegConstRegImmPred extends Definition(
        "Lop3intPipeRegRegConstRegImmPred",
        "LOP3int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register),
          Slot("imm8", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("lop3_lut__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(imm8),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Lop3intPipeRegRegConstRegImmPred(dst, srcA, sb, srcC, imm8, pp, modifiers, guard))
        case _ => None
      }
  }
  object Lop3intPipeRegRegConstRegPred extends Definition(
        "Lop3intPipeRegRegConstRegPred",
        "LOP3int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("lop3_noimm__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Lop3intPipeRegRegConstRegPred(dst, srcA, sb, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object Lop3intPipeRegRegImmReg extends Definition(
        "Lop3intPipeRegRegImmReg",
        "LOP3int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("lop3_imm_optionalPp__RIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Lop3intPipeRegRegImmReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Lop3intPipeRegRegImmRegImm extends Definition(
        "Lop3intPipeRegRegImmRegImm",
        "LOP3int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("imm8", OperandKind.Immediate)
        ),
        Vec("lop3_lut_optionalPp__RuIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(imm8)
            ) => Some(Instruction.Lop3intPipeRegRegImmRegImm(dst, srcA, sb, srcC, imm8, modifiers, guard))
        case _ => None
      }
  }
  object Lop3intPipeRegRegImmRegImmPred extends Definition(
        "Lop3intPipeRegRegImmRegImmPred",
        "LOP3int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("imm8", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("lop3_lut__RuIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(imm8),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Lop3intPipeRegRegImmRegImmPred(dst, srcA, sb, srcC, imm8, pp, modifiers, guard))
        case _ => None
      }
  }
  object Lop3intPipeRegRegImmRegPred extends Definition(
        "Lop3intPipeRegRegImmRegPred",
        "LOP3int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("lop3_imm__RIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Lop3intPipeRegRegImmRegPred(dst, srcA, sb, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object Lop3intPipeRegRegRegReg extends Definition(
        "Lop3intPipeRegRegRegReg",
        "LOP3int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("lop3_noimm_optionalPp__RRR_RRR", "lop3_noimm_optionalPp__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Lop3intPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Lop3intPipeRegRegRegRegImm extends Definition(
        "Lop3intPipeRegRegRegRegImm",
        "LOP3int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("imm8", OperandKind.Immediate)
        ),
        Vec("lop3_lut_optionalPp__RRR_RRR", "lop3_lut_optionalPp__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(imm8)
            ) => Some(Instruction.Lop3intPipeRegRegRegRegImm(dst, srcA, srcB, srcC, imm8, modifiers, guard))
        case _ => None
      }
  }
  object Lop3intPipeRegRegRegRegImmPred extends Definition(
        "Lop3intPipeRegRegRegRegImmPred",
        "LOP3int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("imm8", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("lop3_lut__RRR_RRR", "lop3_lut__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(imm8),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Lop3intPipeRegRegRegRegImmPred(dst, srcA, srcB, srcC, imm8, pp, modifiers, guard))
        case _ => None
      }
  }
  object Lop3intPipeRegRegRegRegPred extends Definition(
        "Lop3intPipeRegRegRegRegPred",
        "LOP3int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("lop3_noimm__RRR_RRR", "lop3_noimm__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Lop3intPipeRegRegRegRegPred(dst, srcA, srcB, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object LopfauPipeRegRegConst extends Definition(
        "LopfauPipeRegRegConst",
        "LOPfau_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const LOP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.LopfauPipeRegRegConst(dst, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object LopfauPipeRegRegImm extends Definition(
        "LopfauPipeRegRegImm",
        "LOPfau_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("Imm LOP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.LopfauPipeRegRegImm(dst, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object LopfauPipeRegRegReg extends Definition(
        "LopfauPipeRegRegReg",
        "LOPfau_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("LOP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.LopfauPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object LopfmaiPipePredRegRegConst extends Definition(
        "LopfmaiPipePredRegRegConst",
        "LOPfmai_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const_LOP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.LopfmaiPipePredRegRegConst(dstPredicate, dst, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object LopfmaiPipePredRegRegImm extends Definition(
        "LopfmaiPipePredRegRegImm",
        "LOPfmai_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate)
        ),
        Vec("Imm_LOP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.LopfmaiPipePredRegRegImm(dstPredicate, dst, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object LopfmaiPipePredRegRegReg extends Definition(
        "LopfmaiPipePredRegRegReg",
        "LOPfmai_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register)
        ),
        Vec("LOP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.LopfmaiPipePredRegRegReg(dstPredicate, dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object LopfmaiPipeRegRegConst extends Definition(
        "LopfmaiPipeRegRegConst",
        "LOPfmai_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const_LOP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.LopfmaiPipeRegRegConst(dst, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object LopfmaiPipeRegRegImm extends Definition(
        "LopfmaiPipeRegRegImm",
        "LOPfmai_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("Imm_LOP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.LopfmaiPipeRegRegImm(dst, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object LopfmaiPipeRegRegReg extends Definition(
        "LopfmaiPipeRegRegReg",
        "LOPfmai_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("LOP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.LopfmaiPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object LopfxuPipePredRegRegConst extends Definition(
        "LopfxuPipePredRegRegConst",
        "LOPfxu_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const_LOP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.LopfxuPipePredRegRegConst(dstPredicate, dst, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object LopfxuPipePredRegRegImm extends Definition(
        "LopfxuPipePredRegRegImm",
        "LOPfxu_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate)
        ),
        Vec("Imm_LOP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.LopfxuPipePredRegRegImm(dstPredicate, dst, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object LopfxuPipePredRegRegReg extends Definition(
        "LopfxuPipePredRegRegReg",
        "LOPfxu_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register)
        ),
        Vec("LOP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.LopfxuPipePredRegRegReg(dstPredicate, dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object LopfxuPipeRegRegConst extends Definition(
        "LopfxuPipeRegRegConst",
        "LOPfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const_LOP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.LopfxuPipeRegRegConst(dst, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object LopfxuPipeRegRegImm extends Definition(
        "LopfxuPipeRegRegImm",
        "LOPfxu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("Imm_LOP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.LopfxuPipeRegRegImm(dst, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object LopfxuPipeRegRegReg extends Definition(
        "LopfxuPipeRegRegReg",
        "LOPfxu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("LOP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.LopfxuPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object LopintPipePredRegRegAddressImm extends Definition(
        "LopintPipePredRegRegAddressImm",
        "LOPint_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("lop_noimm_optionalPp__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.LopintPipePredRegRegAddressImm(pu, dst, srcA, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object LopintPipePredRegRegAddressImmPred extends Definition(
        "LopintPipePredRegRegAddressImmPred",
        "LOPint_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("lop_noimm__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Predicate(pp)
            ) =>
          Some(Instruction.LopintPipePredRegRegAddressImmPred(pu, dst, srcA, srcB, sb_offset, pp, modifiers, guard))
        case _ => None
      }
  }
  object LopintPipePredRegRegConst extends Definition(
        "LopintPipePredRegRegConst",
        "LOPint_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank)
        ),
        Vec("lop_noimm_optionalPp__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb)
            ) => Some(Instruction.LopintPipePredRegRegConst(pu, dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object LopintPipePredRegRegConstPred extends Definition(
        "LopintPipePredRegRegConstPred",
        "LOPint_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("lop_noimm__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.LopintPipePredRegRegConstPred(pu, dst, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object LopintPipePredRegRegImm extends Definition(
        "LopintPipePredRegRegImm",
        "LOPint_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate)
        ),
        Vec("lop_imm_optionalPp_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.LopintPipePredRegRegImm(pu, dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object LopintPipePredRegRegImmPred extends Definition(
        "LopintPipePredRegRegImmPred",
        "LOPint_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("lop_imm_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.LopintPipePredRegRegImmPred(pu, dst, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object LopintPipePredRegRegReg extends Definition(
        "LopintPipePredRegRegReg",
        "LOPint_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register)
        ),
        Vec("lop_noimm_optionalPp__RRR_RRR", "lop_noimm_optionalPp__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.LopintPipePredRegRegReg(pu, dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object LopintPipePredRegRegRegPred extends Definition(
        "LopintPipePredRegRegRegPred",
        "LOPint_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("lop_noimm__RRR_RRR", "lop_noimm__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.LopintPipePredRegRegRegPred(pu, dst, srcA, srcB, pp, modifiers, guard))
        case _ => None
      }
  }
  object LopintPipeRegRegAddressImm extends Definition(
        "LopintPipeRegRegAddressImm",
        "LOPint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("lop_noimm_optionalPp__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.LopintPipeRegRegAddressImm(dst, srcA, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object LopintPipeRegRegAddressImmPred extends Definition(
        "LopintPipeRegRegAddressImmPred",
        "LOPint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("lop_noimm__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.LopintPipeRegRegAddressImmPred(dst, srcA, srcB, sb_offset, pp, modifiers, guard))
        case _ => None
      }
  }
  object LopintPipeRegRegConst extends Definition(
        "LopintPipeRegRegConst",
        "LOPint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank)
        ),
        Vec("lop_noimm_optionalPp__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb)
            ) => Some(Instruction.LopintPipeRegRegConst(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object LopintPipeRegRegConstPred extends Definition(
        "LopintPipeRegRegConstPred",
        "LOPint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("lop_noimm__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.LopintPipeRegRegConstPred(dst, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object LopintPipeRegRegImm extends Definition(
        "LopintPipeRegRegImm",
        "LOPint_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("lop_imm_optionalPp_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.LopintPipeRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object LopintPipeRegRegImmPred extends Definition(
        "LopintPipeRegRegImmPred",
        "LOPint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("lop_imm_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.LopintPipeRegRegImmPred(dst, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object LopintPipeRegRegReg extends Definition(
        "LopintPipeRegRegReg",
        "LOPint_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("lop_noimm_optionalPp__RRR_RRR", "lop_noimm_optionalPp__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.LopintPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object LopintPipeRegRegRegPred extends Definition(
        "LopintPipeRegRegRegPred",
        "LOPint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("lop_noimm__RRR_RRR", "lop_noimm__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.LopintPipeRegRegRegPred(dst, srcA, srcB, pp, modifiers, guard))
        case _ => None
      }
  }
  object LopxluPipeRegRegConst extends Definition(
        "LopxluPipeRegRegConst",
        "LOPxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const LOP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.LopxluPipeRegRegConst(dst, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object LopxluPipeRegRegImm extends Definition(
        "LopxluPipeRegRegImm",
        "LOPxlu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("Imm LOP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.LopxluPipeRegRegImm(dst, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object LopxluPipeRegRegReg extends Definition(
        "LopxluPipeRegRegReg",
        "LOPxlu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("LOP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.LopxluPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object MatchPredRegReg extends Definition(
        "MatchPredRegReg",
        "MATCH",
        Vec(Slot("pu", OperandKind.Predicate), Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register)),
        Vec("match__ALL")
      ) {
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
  object MatchRegReg extends Definition(
        "MatchRegReg",
        "MATCH",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register)),
        Vec("match__ANY")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcA)) =>
          Some(Instruction.MatchRegReg(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object MatchmioPipePredRegReg extends Definition(
        "MatchmioPipePredRegReg",
        "MATCHmio_pipe",
        Vec(Slot("pu", OperandKind.Predicate), Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register)),
        Vec("match__ALL")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA)
            ) => Some(Instruction.MatchmioPipePredRegReg(pu, dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object MatchmioPipeRegReg extends Definition(
        "MatchmioPipeRegReg",
        "MATCHmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register)),
        Vec("match__ANY")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcA)) =>
          Some(Instruction.MatchmioPipeRegReg(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object MembarNoOperands extends Definition(
        "MembarNoOperands",
        "MEMBAR",
        Vec(),
        Vec("MEMBAR", "membar_", "membar_async_", "membar_tma_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.MembarNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object MembaraguPipeNoOperands extends Definition("MembaraguPipeNoOperands", "MEMBARagu_pipe", Vec(), Vec("MEMBAR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.MembaraguPipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object MembarmioPipeNoOperands extends Definition(
        "MembarmioPipeNoOperands",
        "MEMBARmio_pipe",
        Vec(),
        Vec("MEMBAR", "membar_", "membar_async_", "membar_tma_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.MembarmioPipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object MovAddressConst extends Definition(
        "MovAddressConst",
        "MOV",
        Vec(Slot("dst", OperandKind.Address), Slot("sb", OperandKind.ConstantBank)),
        Vec("mov_indexedRF_IRFd__Cb")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(dst), InstructionOperand.ConstantBank(sb)) =>
          Some(Instruction.MovAddressConst(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object MovAddressConstImm extends Definition(
        "MovAddressConstImm",
        "MOV",
        Vec(
          Slot("dst", OperandKind.Address),
          Slot("sb", OperandKind.ConstantBank),
          Slot("pixMaskU04", OperandKind.Immediate)
        ),
        Vec("mov_indexedRF_IRFd__Cb")
      ) {
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
  object MovAddressImm extends Definition(
        "MovAddressImm",
        "MOV",
        Vec(Slot("dst", OperandKind.Address), Slot("sb", OperandKind.Immediate)),
        Vec("mov_indexedRF_IRFd__Ib")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.MovAddressImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object MovAddressImmImm extends Definition(
        "MovAddressImmImm",
        "MOV",
        Vec(
          Slot("dst", OperandKind.Address),
          Slot("sb", OperandKind.Immediate),
          Slot("pixMaskU04", OperandKind.Immediate)
        ),
        Vec("mov_indexedRF_IRFd__Ib")
      ) {
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
  object MovAddressReg extends Definition(
        "MovAddressReg",
        "MOV",
        Vec(Slot("dst", OperandKind.Address), Slot("srcB", OperandKind.Register)),
        Vec("mov_indexedRF_IRFd__Rb")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.MovAddressReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object MovAddressRegImm extends Definition(
        "MovAddressRegImm",
        "MOV",
        Vec(
          Slot("dst", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("pixMaskU04", OperandKind.Immediate)
        ),
        Vec("mov_indexedRF_IRFd__Rb")
      ) {
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
  object MovRegAddress extends Definition(
        "MovRegAddress",
        "MOV",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Address)),
        Vec("mov_indexedRF_Rd_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Address(srcB)) =>
          Some(Instruction.MovRegAddress(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object MovRegAddressImm extends Definition(
        "MovRegAddressImm",
        "MOV",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("pixMaskU04", OperandKind.Immediate)
        ),
        Vec("mov__RCx", "mov_indexedRF_Rd_")
      ) {
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
  object MovRegAddressImmImm extends Definition(
        "MovRegAddressImmImm",
        "MOV",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("pixMaskU04", OperandKind.Immediate)
        ),
        Vec("mov__RCx")
      ) {
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
  object MovRegConst extends Definition(
        "MovRegConst",
        "MOV",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.ConstantBank)),
        Vec("Const MOV", "Const_MOV", "mov__RC", "mov__RCR_RC")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(sb)) =>
          Some(Instruction.MovRegConst(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object MovRegConstImm extends Definition(
        "MovRegConstImm",
        "MOV",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("pixMaskU04", OperandKind.Immediate)
        ),
        Vec("Const MOV", "Const_MOV", "mov__RC", "mov__RCR_RC")
      ) {
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
  object MovRegImm extends Definition(
        "MovRegImm",
        "MOV",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("Imm MOV", "Imm_MOV", "mov__RI", "mov__RuIR_RI", "mov_imm64_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.MovRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object MovRegImmImm extends Definition(
        "MovRegImmImm",
        "MOV",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("pixMaskU04", OperandKind.Immediate)
        ),
        Vec("Imm MOV", "Imm_MOV", "mov__RI", "mov__RuIR_RI")
      ) {
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
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("MOV", "mov_64__RR", "mov_64__RU", "mov__RR", "mov__RRR_RR", "mov__RU", "mov_nonconformity__RU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.MovRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object MovRegRegImm extends Definition(
        "MovRegRegImm",
        "MOV",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pixMaskU04", OperandKind.Immediate)
        ),
        Vec("MOV", "mov__RR", "mov__RRR_RR", "mov__RU")
      ) {
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
  object Mov32iRegImm extends Definition(
        "Mov32iRegImm",
        "MOV32I",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("MOV32I", "mov32i_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.Mov32iRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object Mov32iRegImmImm extends Definition(
        "Mov32iRegImmImm",
        "MOV32I",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("pixMaskU04", OperandKind.Immediate)
        ),
        Vec("MOV32I", "mov32i_")
      ) {
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
  object Mov32ifauPipeRegImm extends Definition(
        "Mov32ifauPipeRegImm",
        "MOV32Ifau_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("MOV32I")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(uImm)) =>
          Some(Instruction.Mov32ifauPipeRegImm(dst, uImm, modifiers, guard))
        case _ => None
      }
  }
  object Mov32ifauPipeRegImmImm extends Definition(
        "Mov32ifauPipeRegImmImm",
        "MOV32Ifau_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("quadMask", OperandKind.Immediate)
        ),
        Vec("MOV32I")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Immediate(quadMask)
            ) => Some(Instruction.Mov32ifauPipeRegImmImm(dst, uImm, quadMask, modifiers, guard))
        case _ => None
      }
  }
  object Mov32ifmaxPipeRegImm extends Definition(
        "Mov32ifmaxPipeRegImm",
        "MOV32IfmaX_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("MOV32I")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(uImm)) =>
          Some(Instruction.Mov32ifmaxPipeRegImm(dst, uImm, modifiers, guard))
        case _ => None
      }
  }
  object Mov32ifmaxPipeRegImmImm extends Definition(
        "Mov32ifmaxPipeRegImmImm",
        "MOV32IfmaX_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("quadMask", OperandKind.Immediate)
        ),
        Vec("MOV32I")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Immediate(quadMask)
            ) => Some(Instruction.Mov32ifmaxPipeRegImmImm(dst, uImm, quadMask, modifiers, guard))
        case _ => None
      }
  }
  object Mov32ifmaxlitewPipeRegImm extends Definition(
        "Mov32ifmaxlitewPipeRegImm",
        "MOV32IfmaXliteW_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("MOV32I")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(uImm)) =>
          Some(Instruction.Mov32ifmaxlitewPipeRegImm(dst, uImm, modifiers, guard))
        case _ => None
      }
  }
  object Mov32ifmaxlitewPipeRegImmImm extends Definition(
        "Mov32ifmaxlitewPipeRegImmImm",
        "MOV32IfmaXliteW_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("quadMask", OperandKind.Immediate)
        ),
        Vec("MOV32I")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Immediate(quadMask)
            ) => Some(Instruction.Mov32ifmaxlitewPipeRegImmImm(dst, uImm, quadMask, modifiers, guard))
        case _ => None
      }
  }
  object Mov32ifmaiPipeRegImm extends Definition(
        "Mov32ifmaiPipeRegImm",
        "MOV32Ifmai_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("MOV32I")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(uImm)) =>
          Some(Instruction.Mov32ifmaiPipeRegImm(dst, uImm, modifiers, guard))
        case _ => None
      }
  }
  object Mov32ifmaiPipeRegImmImm extends Definition(
        "Mov32ifmaiPipeRegImmImm",
        "MOV32Ifmai_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("quadMask", OperandKind.Immediate)
        ),
        Vec("MOV32I")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Immediate(quadMask)
            ) => Some(Instruction.Mov32ifmaiPipeRegImmImm(dst, uImm, quadMask, modifiers, guard))
        case _ => None
      }
  }
  object Mov32ifuPipeRegImm extends Definition(
        "Mov32ifuPipeRegImm",
        "MOV32Ifu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("MOV32I")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(uImm)) =>
          Some(Instruction.Mov32ifuPipeRegImm(dst, uImm, modifiers, guard))
        case _ => None
      }
  }
  object Mov32ifuPipeRegImmImm extends Definition(
        "Mov32ifuPipeRegImmImm",
        "MOV32Ifu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("quadMask", OperandKind.Immediate)
        ),
        Vec("MOV32I")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Immediate(quadMask)
            ) => Some(Instruction.Mov32ifuPipeRegImmImm(dst, uImm, quadMask, modifiers, guard))
        case _ => None
      }
  }
  object Mov32ifxuPipeRegImm extends Definition(
        "Mov32ifxuPipeRegImm",
        "MOV32Ifxu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("MOV32I")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(uImm)) =>
          Some(Instruction.Mov32ifxuPipeRegImm(dst, uImm, modifiers, guard))
        case _ => None
      }
  }
  object Mov32ifxuPipeRegImmImm extends Definition(
        "Mov32ifxuPipeRegImmImm",
        "MOV32Ifxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("quadMask", OperandKind.Immediate)
        ),
        Vec("MOV32I")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Immediate(quadMask)
            ) => Some(Instruction.Mov32ifxuPipeRegImmImm(dst, uImm, quadMask, modifiers, guard))
        case _ => None
      }
  }
  object Mov32iintPipeRegImm extends Definition(
        "Mov32iintPipeRegImm",
        "MOV32Iint_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("mov32i_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.Mov32iintPipeRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object Mov32iintPipeRegImmImm extends Definition(
        "Mov32iintPipeRegImmImm",
        "MOV32Iint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("pixMaskU04", OperandKind.Immediate)
        ),
        Vec("mov32i_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Immediate(pixMaskU04)
            ) => Some(Instruction.Mov32iintPipeRegImmImm(dst, sb, pixMaskU04, modifiers, guard))
        case _ => None
      }
  }
  object Mov32ixluPipeRegImm extends Definition(
        "Mov32ixluPipeRegImm",
        "MOV32Ixlu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("MOV32I")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(uImm)) =>
          Some(Instruction.Mov32ixluPipeRegImm(dst, uImm, modifiers, guard))
        case _ => None
      }
  }
  object Mov32ixluPipeRegImmImm extends Definition(
        "Mov32ixluPipeRegImmImm",
        "MOV32Ixlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("quadMask", OperandKind.Immediate)
        ),
        Vec("MOV32I")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Immediate(quadMask)
            ) => Some(Instruction.Mov32ixluPipeRegImmImm(dst, uImm, quadMask, modifiers, guard))
        case _ => None
      }
  }
  object Mov64iurRegImm extends Definition(
        "Mov64iurRegImm",
        "MOV64IUR",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("mov64iur_imm_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.Mov64iurRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object Mov64iurRegReg extends Definition(
        "Mov64iurRegReg",
        "MOV64IUR",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("mov64iur_ur_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.Mov64iurRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object Mov64iurintPipeRegImm extends Definition(
        "Mov64iurintPipeRegImm",
        "MOV64IURint_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("mov64iur_imm_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.Mov64iurintPipeRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object Mov64iurintPipeRegReg extends Definition(
        "Mov64iurintPipeRegReg",
        "MOV64IURint_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("mov64iur_ur_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.Mov64iurintPipeRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object MovmRegReg extends Definition(
        "MovmRegReg",
        "MOVM",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register)),
        Vec("movm_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcA)) =>
          Some(Instruction.MovmRegReg(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object MovmintPipeRegReg extends Definition(
        "MovmintPipeRegReg",
        "MOVMint_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register)),
        Vec("movm_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcA)) =>
          Some(Instruction.MovmintPipeRegReg(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object MovfauPipeRegConst extends Definition(
        "MovfauPipeRegConst",
        "MOVfau_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcConst", OperandKind.ConstantBank)),
        Vec("Const MOV")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(srcConst)) =>
          Some(Instruction.MovfauPipeRegConst(dst, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object MovfauPipeRegConstImm extends Definition(
        "MovfauPipeRegConstImm",
        "MOVfau_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("quadMask", OperandKind.Immediate)
        ),
        Vec("Const MOV")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Immediate(quadMask)
            ) => Some(Instruction.MovfauPipeRegConstImm(dst, srcConst, quadMask, modifiers, guard))
        case _ => None
      }
  }
  object MovfauPipeRegImm extends Definition(
        "MovfauPipeRegImm",
        "MOVfau_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("Imm MOV")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(uImm)) =>
          Some(Instruction.MovfauPipeRegImm(dst, uImm, modifiers, guard))
        case _ => None
      }
  }
  object MovfauPipeRegImmImm extends Definition(
        "MovfauPipeRegImmImm",
        "MOVfau_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("quadMask", OperandKind.Immediate)
        ),
        Vec("Imm MOV")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Immediate(quadMask)
            ) => Some(Instruction.MovfauPipeRegImmImm(dst, uImm, quadMask, modifiers, guard))
        case _ => None
      }
  }
  object MovfauPipeRegReg extends Definition(
        "MovfauPipeRegReg",
        "MOVfau_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("MOV")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.MovfauPipeRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object MovfauPipeRegRegImm extends Definition(
        "MovfauPipeRegRegImm",
        "MOVfau_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("quadMask", OperandKind.Immediate)
        ),
        Vec("MOV")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(quadMask)
            ) => Some(Instruction.MovfauPipeRegRegImm(dst, srcB, quadMask, modifiers, guard))
        case _ => None
      }
  }
  object MovfmaxPipeRegConst extends Definition(
        "MovfmaxPipeRegConst",
        "MOVfmaX_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcConst", OperandKind.ConstantBank)),
        Vec("Const MOV")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(srcConst)) =>
          Some(Instruction.MovfmaxPipeRegConst(dst, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object MovfmaxPipeRegConstImm extends Definition(
        "MovfmaxPipeRegConstImm",
        "MOVfmaX_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("quadMask", OperandKind.Immediate)
        ),
        Vec("Const MOV")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Immediate(quadMask)
            ) => Some(Instruction.MovfmaxPipeRegConstImm(dst, srcConst, quadMask, modifiers, guard))
        case _ => None
      }
  }
  object MovfmaxPipeRegImm extends Definition(
        "MovfmaxPipeRegImm",
        "MOVfmaX_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("Imm MOV")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(uImm)) =>
          Some(Instruction.MovfmaxPipeRegImm(dst, uImm, modifiers, guard))
        case _ => None
      }
  }
  object MovfmaxPipeRegImmImm extends Definition(
        "MovfmaxPipeRegImmImm",
        "MOVfmaX_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("quadMask", OperandKind.Immediate)
        ),
        Vec("Imm MOV")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Immediate(quadMask)
            ) => Some(Instruction.MovfmaxPipeRegImmImm(dst, uImm, quadMask, modifiers, guard))
        case _ => None
      }
  }
  object MovfmaxPipeRegReg extends Definition(
        "MovfmaxPipeRegReg",
        "MOVfmaX_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("MOV")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.MovfmaxPipeRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object MovfmaxPipeRegRegImm extends Definition(
        "MovfmaxPipeRegRegImm",
        "MOVfmaX_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("quadMask", OperandKind.Immediate)
        ),
        Vec("MOV")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(quadMask)
            ) => Some(Instruction.MovfmaxPipeRegRegImm(dst, srcB, quadMask, modifiers, guard))
        case _ => None
      }
  }
  object MovfmaxlitewPipeRegConst extends Definition(
        "MovfmaxlitewPipeRegConst",
        "MOVfmaXliteW_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcConst", OperandKind.ConstantBank)),
        Vec("Const MOV")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(srcConst)) =>
          Some(Instruction.MovfmaxlitewPipeRegConst(dst, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object MovfmaxlitewPipeRegConstImm extends Definition(
        "MovfmaxlitewPipeRegConstImm",
        "MOVfmaXliteW_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("quadMask", OperandKind.Immediate)
        ),
        Vec("Const MOV")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Immediate(quadMask)
            ) => Some(Instruction.MovfmaxlitewPipeRegConstImm(dst, srcConst, quadMask, modifiers, guard))
        case _ => None
      }
  }
  object MovfmaxlitewPipeRegImm extends Definition(
        "MovfmaxlitewPipeRegImm",
        "MOVfmaXliteW_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("Imm MOV")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(uImm)) =>
          Some(Instruction.MovfmaxlitewPipeRegImm(dst, uImm, modifiers, guard))
        case _ => None
      }
  }
  object MovfmaxlitewPipeRegImmImm extends Definition(
        "MovfmaxlitewPipeRegImmImm",
        "MOVfmaXliteW_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("quadMask", OperandKind.Immediate)
        ),
        Vec("Imm MOV")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Immediate(quadMask)
            ) => Some(Instruction.MovfmaxlitewPipeRegImmImm(dst, uImm, quadMask, modifiers, guard))
        case _ => None
      }
  }
  object MovfmaxlitewPipeRegReg extends Definition(
        "MovfmaxlitewPipeRegReg",
        "MOVfmaXliteW_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("MOV")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.MovfmaxlitewPipeRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object MovfmaxlitewPipeRegRegImm extends Definition(
        "MovfmaxlitewPipeRegRegImm",
        "MOVfmaXliteW_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("quadMask", OperandKind.Immediate)
        ),
        Vec("MOV")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(quadMask)
            ) => Some(Instruction.MovfmaxlitewPipeRegRegImm(dst, srcB, quadMask, modifiers, guard))
        case _ => None
      }
  }
  object MovfmaiPipeRegConst extends Definition(
        "MovfmaiPipeRegConst",
        "MOVfmai_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcConst", OperandKind.ConstantBank)),
        Vec("Const_MOV")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(srcConst)) =>
          Some(Instruction.MovfmaiPipeRegConst(dst, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object MovfmaiPipeRegConstImm extends Definition(
        "MovfmaiPipeRegConstImm",
        "MOVfmai_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("quadMask", OperandKind.Immediate)
        ),
        Vec("Const_MOV")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Immediate(quadMask)
            ) => Some(Instruction.MovfmaiPipeRegConstImm(dst, srcConst, quadMask, modifiers, guard))
        case _ => None
      }
  }
  object MovfmaiPipeRegImm extends Definition(
        "MovfmaiPipeRegImm",
        "MOVfmai_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("Imm_MOV")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(uImm)) =>
          Some(Instruction.MovfmaiPipeRegImm(dst, uImm, modifiers, guard))
        case _ => None
      }
  }
  object MovfmaiPipeRegImmImm extends Definition(
        "MovfmaiPipeRegImmImm",
        "MOVfmai_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("quadMask", OperandKind.Immediate)
        ),
        Vec("Imm_MOV")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Immediate(quadMask)
            ) => Some(Instruction.MovfmaiPipeRegImmImm(dst, uImm, quadMask, modifiers, guard))
        case _ => None
      }
  }
  object MovfmaiPipeRegReg extends Definition(
        "MovfmaiPipeRegReg",
        "MOVfmai_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("MOV")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.MovfmaiPipeRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object MovfmaiPipeRegRegImm extends Definition(
        "MovfmaiPipeRegRegImm",
        "MOVfmai_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("quadMask", OperandKind.Immediate)
        ),
        Vec("MOV")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(quadMask)
            ) => Some(Instruction.MovfmaiPipeRegRegImm(dst, srcB, quadMask, modifiers, guard))
        case _ => None
      }
  }
  object MovfuPipeRegConst extends Definition(
        "MovfuPipeRegConst",
        "MOVfu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcConst", OperandKind.ConstantBank)),
        Vec("Const MOV")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(srcConst)) =>
          Some(Instruction.MovfuPipeRegConst(dst, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object MovfuPipeRegConstImm extends Definition(
        "MovfuPipeRegConstImm",
        "MOVfu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("quadMask", OperandKind.Immediate)
        ),
        Vec("Const MOV")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Immediate(quadMask)
            ) => Some(Instruction.MovfuPipeRegConstImm(dst, srcConst, quadMask, modifiers, guard))
        case _ => None
      }
  }
  object MovfuPipeRegImm extends Definition(
        "MovfuPipeRegImm",
        "MOVfu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("Imm MOV")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(uImm)) =>
          Some(Instruction.MovfuPipeRegImm(dst, uImm, modifiers, guard))
        case _ => None
      }
  }
  object MovfuPipeRegImmImm extends Definition(
        "MovfuPipeRegImmImm",
        "MOVfu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("quadMask", OperandKind.Immediate)
        ),
        Vec("Imm MOV")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Immediate(quadMask)
            ) => Some(Instruction.MovfuPipeRegImmImm(dst, uImm, quadMask, modifiers, guard))
        case _ => None
      }
  }
  object MovfuPipeRegReg extends Definition(
        "MovfuPipeRegReg",
        "MOVfu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("MOV")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.MovfuPipeRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object MovfuPipeRegRegImm extends Definition(
        "MovfuPipeRegRegImm",
        "MOVfu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("quadMask", OperandKind.Immediate)
        ),
        Vec("MOV")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(quadMask)
            ) => Some(Instruction.MovfuPipeRegRegImm(dst, srcB, quadMask, modifiers, guard))
        case _ => None
      }
  }
  object MovfxuPipeRegConst extends Definition(
        "MovfxuPipeRegConst",
        "MOVfxu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcConst", OperandKind.ConstantBank)),
        Vec("Const_MOV")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(srcConst)) =>
          Some(Instruction.MovfxuPipeRegConst(dst, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object MovfxuPipeRegConstImm extends Definition(
        "MovfxuPipeRegConstImm",
        "MOVfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("quadMask", OperandKind.Immediate)
        ),
        Vec("Const_MOV")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Immediate(quadMask)
            ) => Some(Instruction.MovfxuPipeRegConstImm(dst, srcConst, quadMask, modifiers, guard))
        case _ => None
      }
  }
  object MovfxuPipeRegImm extends Definition(
        "MovfxuPipeRegImm",
        "MOVfxu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("Imm_MOV")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(uImm)) =>
          Some(Instruction.MovfxuPipeRegImm(dst, uImm, modifiers, guard))
        case _ => None
      }
  }
  object MovfxuPipeRegImmImm extends Definition(
        "MovfxuPipeRegImmImm",
        "MOVfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("quadMask", OperandKind.Immediate)
        ),
        Vec("Imm_MOV")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Immediate(quadMask)
            ) => Some(Instruction.MovfxuPipeRegImmImm(dst, uImm, quadMask, modifiers, guard))
        case _ => None
      }
  }
  object MovfxuPipeRegReg extends Definition(
        "MovfxuPipeRegReg",
        "MOVfxu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("MOV")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.MovfxuPipeRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object MovfxuPipeRegRegImm extends Definition(
        "MovfxuPipeRegRegImm",
        "MOVfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("quadMask", OperandKind.Immediate)
        ),
        Vec("MOV")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(quadMask)
            ) => Some(Instruction.MovfxuPipeRegRegImm(dst, srcB, quadMask, modifiers, guard))
        case _ => None
      }
  }
  object MovintPipeAddressConst extends Definition(
        "MovintPipeAddressConst",
        "MOVint_pipe",
        Vec(Slot("dst", OperandKind.Address), Slot("sb", OperandKind.ConstantBank)),
        Vec("mov_indexedRF_IRFd__Cb")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(dst), InstructionOperand.ConstantBank(sb)) =>
          Some(Instruction.MovintPipeAddressConst(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object MovintPipeAddressConstImm extends Definition(
        "MovintPipeAddressConstImm",
        "MOVint_pipe",
        Vec(
          Slot("dst", OperandKind.Address),
          Slot("sb", OperandKind.ConstantBank),
          Slot("pixMaskU04", OperandKind.Immediate)
        ),
        Vec("mov_indexedRF_IRFd__Cb")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(dst),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Immediate(pixMaskU04)
            ) => Some(Instruction.MovintPipeAddressConstImm(dst, sb, pixMaskU04, modifiers, guard))
        case _ => None
      }
  }
  object MovintPipeAddressImm extends Definition(
        "MovintPipeAddressImm",
        "MOVint_pipe",
        Vec(Slot("dst", OperandKind.Address), Slot("sb", OperandKind.Immediate)),
        Vec("mov_indexedRF_IRFd__Ib")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.MovintPipeAddressImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object MovintPipeAddressImmImm extends Definition(
        "MovintPipeAddressImmImm",
        "MOVint_pipe",
        Vec(
          Slot("dst", OperandKind.Address),
          Slot("sb", OperandKind.Immediate),
          Slot("pixMaskU04", OperandKind.Immediate)
        ),
        Vec("mov_indexedRF_IRFd__Ib")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(dst),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Immediate(pixMaskU04)
            ) => Some(Instruction.MovintPipeAddressImmImm(dst, sb, pixMaskU04, modifiers, guard))
        case _ => None
      }
  }
  object MovintPipeAddressReg extends Definition(
        "MovintPipeAddressReg",
        "MOVint_pipe",
        Vec(Slot("dst", OperandKind.Address), Slot("srcB", OperandKind.Register)),
        Vec("mov_indexedRF_IRFd__Rb")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.MovintPipeAddressReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object MovintPipeAddressRegImm extends Definition(
        "MovintPipeAddressRegImm",
        "MOVint_pipe",
        Vec(
          Slot("dst", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("pixMaskU04", OperandKind.Immediate)
        ),
        Vec("mov_indexedRF_IRFd__Rb")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(dst),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(pixMaskU04)
            ) => Some(Instruction.MovintPipeAddressRegImm(dst, srcB, pixMaskU04, modifiers, guard))
        case _ => None
      }
  }
  object MovintPipeRegAddress extends Definition(
        "MovintPipeRegAddress",
        "MOVint_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Address)),
        Vec("mov_indexedRF_Rd_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Address(srcB)) =>
          Some(Instruction.MovintPipeRegAddress(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object MovintPipeRegAddressImm extends Definition(
        "MovintPipeRegAddressImm",
        "MOVint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("pixMaskU04", OperandKind.Immediate)
        ),
        Vec("mov__RCx", "mov_indexedRF_Rd_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(pixMaskU04)
            ) => Some(Instruction.MovintPipeRegAddressImm(dst, srcB, pixMaskU04, modifiers, guard))
        case _ => None
      }
  }
  object MovintPipeRegAddressImmImm extends Definition(
        "MovintPipeRegAddressImmImm",
        "MOVint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("pixMaskU04", OperandKind.Immediate)
        ),
        Vec("mov__RCx")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Immediate(pixMaskU04)
            ) => Some(Instruction.MovintPipeRegAddressImmImm(dst, srcB, sb_offset, pixMaskU04, modifiers, guard))
        case _ => None
      }
  }
  object MovintPipeRegConst extends Definition(
        "MovintPipeRegConst",
        "MOVint_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.ConstantBank)),
        Vec("mov__RC", "mov__RCR_RC")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(sb)) =>
          Some(Instruction.MovintPipeRegConst(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object MovintPipeRegConstImm extends Definition(
        "MovintPipeRegConstImm",
        "MOVint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("pixMaskU04", OperandKind.Immediate)
        ),
        Vec("mov__RC", "mov__RCR_RC")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Immediate(pixMaskU04)
            ) => Some(Instruction.MovintPipeRegConstImm(dst, sb, pixMaskU04, modifiers, guard))
        case _ => None
      }
  }
  object MovintPipeRegImm extends Definition(
        "MovintPipeRegImm",
        "MOVint_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("mov__RI", "mov__RuIR_RI", "mov_imm64_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.MovintPipeRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object MovintPipeRegImmImm extends Definition(
        "MovintPipeRegImmImm",
        "MOVint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("pixMaskU04", OperandKind.Immediate)
        ),
        Vec("mov__RI", "mov__RuIR_RI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Immediate(pixMaskU04)
            ) => Some(Instruction.MovintPipeRegImmImm(dst, sb, pixMaskU04, modifiers, guard))
        case _ => None
      }
  }
  object MovintPipeRegReg extends Definition(
        "MovintPipeRegReg",
        "MOVint_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("mov_64__RR", "mov_64__RU", "mov__RR", "mov__RRR_RR", "mov__RU", "mov_nonconformity__RU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.MovintPipeRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object MovintPipeRegRegImm extends Definition(
        "MovintPipeRegRegImm",
        "MOVint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pixMaskU04", OperandKind.Immediate)
        ),
        Vec("mov__RR", "mov__RRR_RR", "mov__RU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(pixMaskU04)
            ) => Some(Instruction.MovintPipeRegRegImm(dst, srcB, pixMaskU04, modifiers, guard))
        case _ => None
      }
  }
  object MovxluPipeRegConst extends Definition(
        "MovxluPipeRegConst",
        "MOVxlu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcConst", OperandKind.ConstantBank)),
        Vec("Const MOV")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(srcConst)) =>
          Some(Instruction.MovxluPipeRegConst(dst, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object MovxluPipeRegConstImm extends Definition(
        "MovxluPipeRegConstImm",
        "MOVxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("quadMask", OperandKind.Immediate)
        ),
        Vec("Const MOV")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Immediate(quadMask)
            ) => Some(Instruction.MovxluPipeRegConstImm(dst, srcConst, quadMask, modifiers, guard))
        case _ => None
      }
  }
  object MovxluPipeRegImm extends Definition(
        "MovxluPipeRegImm",
        "MOVxlu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("Imm MOV")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(uImm)) =>
          Some(Instruction.MovxluPipeRegImm(dst, uImm, modifiers, guard))
        case _ => None
      }
  }
  object MovxluPipeRegImmImm extends Definition(
        "MovxluPipeRegImmImm",
        "MOVxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("quadMask", OperandKind.Immediate)
        ),
        Vec("Imm MOV")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Immediate(quadMask)
            ) => Some(Instruction.MovxluPipeRegImmImm(dst, uImm, quadMask, modifiers, guard))
        case _ => None
      }
  }
  object MovxluPipeRegReg extends Definition(
        "MovxluPipeRegReg",
        "MOVxlu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("MOV")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.MovxluPipeRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object MovxluPipeRegRegImm extends Definition(
        "MovxluPipeRegRegImm",
        "MOVxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("quadMask", OperandKind.Immediate)
        ),
        Vec("MOV")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(quadMask)
            ) => Some(Instruction.MovxluPipeRegRegImm(dst, srcB, quadMask, modifiers, guard))
        case _ => None
      }
  }
  object MufuRegAddressImm extends Definition(
        "MufuRegAddressImm",
        "MUFU",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("mufu__RCxR_RCx", "mufu_fp16__RCx")
      ) {
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
  object MufuRegConst extends Definition(
        "MufuRegConst",
        "MUFU",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.ConstantBank)),
        Vec("mufu__RCR_RC", "mufu_fp16__RC")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(sb)) =>
          Some(Instruction.MufuRegConst(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object MufuRegImm extends Definition(
        "MufuRegImm",
        "MUFU",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("mufu__RIR_RI", "mufu_fp16__RI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.MufuRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object MufuRegImmImm extends Definition(
        "MufuRegImmImm",
        "MUFU",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.Immediate), Slot("sb1", OperandKind.Immediate)),
        Vec("mufu_fp16_simd__RI")
      ) {
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
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register)),
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
  object MufufuPipeRegReg extends Definition(
        "MufufuPipeRegReg",
        "MUFUfu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register)),
        Vec("MUFU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcA)) =>
          Some(Instruction.MufufuPipeRegReg(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object MufumioPipeRegAddressImm extends Definition(
        "MufumioPipeRegAddressImm",
        "MUFUmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("mufu__RCxR_RCx", "mufu_fp16__RCx")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.MufumioPipeRegAddressImm(dst, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object MufumioPipeRegConst extends Definition(
        "MufumioPipeRegConst",
        "MUFUmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.ConstantBank)),
        Vec("mufu__RCR_RC", "mufu_fp16__RC")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(sb)) =>
          Some(Instruction.MufumioPipeRegConst(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object MufumioPipeRegImm extends Definition(
        "MufumioPipeRegImm",
        "MUFUmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("mufu__RIR_RI", "mufu_fp16__RI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.MufumioPipeRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object MufumioPipeRegImmImm extends Definition(
        "MufumioPipeRegImmImm",
        "MUFUmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.Immediate), Slot("sb1", OperandKind.Immediate)),
        Vec("mufu_fp16_simd__RI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Immediate(sb1)
            ) => Some(Instruction.MufumioPipeRegImmImm(dst, sb, sb1, modifiers, guard))
        case _ => None
      }
  }
  object MufumioPipeRegReg extends Definition(
        "MufumioPipeRegReg",
        "MUFUmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register)),
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
          Some(Instruction.MufumioPipeRegReg(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object NanosleepNoOperands extends Definition("NanosleepNoOperands", "NANOSLEEP", Vec(), Vec("nanosleep_clear_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.NanosleepNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object NanosleepAddressImm extends Definition(
        "NanosleepAddressImm",
        "NANOSLEEP",
        Vec(Slot("srcB", OperandKind.Address), Slot("sb_offset", OperandKind.Immediate)),
        Vec("nanosleep__CX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcB), InstructionOperand.Immediate(sb_offset)) =>
          Some(Instruction.NanosleepAddressImm(srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object NanosleepConst extends Definition(
        "NanosleepConst",
        "NANOSLEEP",
        Vec(Slot("sb", OperandKind.ConstantBank)),
        Vec("nanosleep__C")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(sb)) => Some(Instruction.NanosleepConst(sb, modifiers, guard))
        case _ => None
      }
  }
  object NanosleepImm
      extends Definition("NanosleepImm", "NANOSLEEP", Vec(Slot("sb", OperandKind.Immediate)), Vec("nanosleep__I")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb)) => Some(Instruction.NanosleepImm(sb, modifiers, guard))
        case _ => None
      }
  }
  object NanosleepPred extends Definition(
        "NanosleepPred",
        "NANOSLEEP",
        Vec(Slot("pp", OperandKind.Predicate)),
        Vec("nanosleep_clear_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp)) => Some(Instruction.NanosleepPred(pp, modifiers, guard))
        case _ => None
      }
  }
  object NanosleepPredAddressImm extends Definition(
        "NanosleepPredAddressImm",
        "NANOSLEEP",
        Vec(
          Slot("pp", OperandKind.Predicate),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("nanosleep__CX")
      ) {
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
  object NanosleepPredConst extends Definition(
        "NanosleepPredConst",
        "NANOSLEEP",
        Vec(Slot("pp", OperandKind.Predicate), Slot("sb", OperandKind.ConstantBank)),
        Vec("nanosleep__C")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.ConstantBank(sb)) =>
          Some(Instruction.NanosleepPredConst(pp, sb, modifiers, guard))
        case _ => None
      }
  }
  object NanosleepPredImm extends Definition(
        "NanosleepPredImm",
        "NANOSLEEP",
        Vec(Slot("pp", OperandKind.Predicate), Slot("sb", OperandKind.Immediate)),
        Vec("nanosleep__I")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.NanosleepPredImm(pp, sb, modifiers, guard))
        case _ => None
      }
  }
  object NanosleepPredReg extends Definition(
        "NanosleepPredReg",
        "NANOSLEEP",
        Vec(Slot("pp", OperandKind.Predicate), Slot("srcB", OperandKind.Register)),
        Vec("nanosleep__R", "nanosleep__U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.Register(srcB)) =>
          Some(Instruction.NanosleepPredReg(pp, srcB, modifiers, guard))
        case _ => None
      }
  }
  object NanosleepReg extends Definition(
        "NanosleepReg",
        "NANOSLEEP",
        Vec(Slot("srcB", OperandKind.Register)),
        Vec("nanosleep__R", "nanosleep__U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcB)) => Some(Instruction.NanosleepReg(srcB, modifiers, guard))
        case _ => None
      }
  }
  object NanosleepcbuPipeNoOperands
      extends Definition("NanosleepcbuPipeNoOperands", "NANOSLEEPcbu_pipe", Vec(), Vec("nanosleep_clear_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.NanosleepcbuPipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object NanosleepcbuPipeAddressImm extends Definition(
        "NanosleepcbuPipeAddressImm",
        "NANOSLEEPcbu_pipe",
        Vec(Slot("srcB", OperandKind.Address), Slot("sb_offset", OperandKind.Immediate)),
        Vec("nanosleep__CX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcB), InstructionOperand.Immediate(sb_offset)) =>
          Some(Instruction.NanosleepcbuPipeAddressImm(srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object NanosleepcbuPipeConst extends Definition(
        "NanosleepcbuPipeConst",
        "NANOSLEEPcbu_pipe",
        Vec(Slot("sb", OperandKind.ConstantBank)),
        Vec("nanosleep__C")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(sb)) => Some(Instruction.NanosleepcbuPipeConst(sb, modifiers, guard))
        case _ => None
      }
  }
  object NanosleepcbuPipeImm extends Definition(
        "NanosleepcbuPipeImm",
        "NANOSLEEPcbu_pipe",
        Vec(Slot("sb", OperandKind.Immediate)),
        Vec("nanosleep__I")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb)) => Some(Instruction.NanosleepcbuPipeImm(sb, modifiers, guard))
        case _ => None
      }
  }
  object NanosleepcbuPipePred extends Definition(
        "NanosleepcbuPipePred",
        "NANOSLEEPcbu_pipe",
        Vec(Slot("pp", OperandKind.Predicate)),
        Vec("nanosleep_clear_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp)) => Some(Instruction.NanosleepcbuPipePred(pp, modifiers, guard))
        case _ => None
      }
  }
  object NanosleepcbuPipePredAddressImm extends Definition(
        "NanosleepcbuPipePredAddressImm",
        "NANOSLEEPcbu_pipe",
        Vec(
          Slot("pp", OperandKind.Predicate),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("nanosleep__CX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pp),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.NanosleepcbuPipePredAddressImm(pp, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object NanosleepcbuPipePredConst extends Definition(
        "NanosleepcbuPipePredConst",
        "NANOSLEEPcbu_pipe",
        Vec(Slot("pp", OperandKind.Predicate), Slot("sb", OperandKind.ConstantBank)),
        Vec("nanosleep__C")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.ConstantBank(sb)) =>
          Some(Instruction.NanosleepcbuPipePredConst(pp, sb, modifiers, guard))
        case _ => None
      }
  }
  object NanosleepcbuPipePredImm extends Definition(
        "NanosleepcbuPipePredImm",
        "NANOSLEEPcbu_pipe",
        Vec(Slot("pp", OperandKind.Predicate), Slot("sb", OperandKind.Immediate)),
        Vec("nanosleep__I")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.NanosleepcbuPipePredImm(pp, sb, modifiers, guard))
        case _ => None
      }
  }
  object NanosleepcbuPipePredReg extends Definition(
        "NanosleepcbuPipePredReg",
        "NANOSLEEPcbu_pipe",
        Vec(Slot("pp", OperandKind.Predicate), Slot("srcB", OperandKind.Register)),
        Vec("nanosleep__R", "nanosleep__U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.Register(srcB)) =>
          Some(Instruction.NanosleepcbuPipePredReg(pp, srcB, modifiers, guard))
        case _ => None
      }
  }
  object NanosleepcbuPipeReg extends Definition(
        "NanosleepcbuPipeReg",
        "NANOSLEEPcbu_pipe",
        Vec(Slot("srcB", OperandKind.Register)),
        Vec("nanosleep__R", "nanosleep__U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcB)) => Some(Instruction.NanosleepcbuPipeReg(srcB, modifiers, guard))
        case _ => None
      }
  }
  object NanotrapConst
      extends Definition("NanotrapConst", "NANOTRAP", Vec(Slot("sb", OperandKind.ConstantBank)), Vec("nanotrap__C")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(sb)) => Some(Instruction.NanotrapConst(sb, modifiers, guard))
        case _ => None
      }
  }
  object NanotrapImm
      extends Definition("NanotrapImm", "NANOTRAP", Vec(Slot("sb", OperandKind.Immediate)), Vec("nanotrap__I")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb)) => Some(Instruction.NanotrapImm(sb, modifiers, guard))
        case _ => None
      }
  }
  object NanotrapPredConst extends Definition(
        "NanotrapPredConst",
        "NANOTRAP",
        Vec(Slot("pp", OperandKind.Predicate), Slot("sb", OperandKind.ConstantBank)),
        Vec("nanotrap__C")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.ConstantBank(sb)) =>
          Some(Instruction.NanotrapPredConst(pp, sb, modifiers, guard))
        case _ => None
      }
  }
  object NanotrapPredImm extends Definition(
        "NanotrapPredImm",
        "NANOTRAP",
        Vec(Slot("pp", OperandKind.Predicate), Slot("sb", OperandKind.Immediate)),
        Vec("nanotrap__I")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.NanotrapPredImm(pp, sb, modifiers, guard))
        case _ => None
      }
  }
  object NanotrapPredReg extends Definition(
        "NanotrapPredReg",
        "NANOTRAP",
        Vec(Slot("pp", OperandKind.Predicate), Slot("srcB", OperandKind.Register)),
        Vec("nanotrap__R")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.Register(srcB)) =>
          Some(Instruction.NanotrapPredReg(pp, srcB, modifiers, guard))
        case _ => None
      }
  }
  object NanotrapReg
      extends Definition("NanotrapReg", "NANOTRAP", Vec(Slot("srcB", OperandKind.Register)), Vec("nanotrap__R")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcB)) => Some(Instruction.NanotrapReg(srcB, modifiers, guard))
        case _ => None
      }
  }
  object NanotrapcbuPipeConst extends Definition(
        "NanotrapcbuPipeConst",
        "NANOTRAPcbu_pipe",
        Vec(Slot("sb", OperandKind.ConstantBank)),
        Vec("nanotrap__C")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(sb)) => Some(Instruction.NanotrapcbuPipeConst(sb, modifiers, guard))
        case _ => None
      }
  }
  object NanotrapcbuPipeImm extends Definition(
        "NanotrapcbuPipeImm",
        "NANOTRAPcbu_pipe",
        Vec(Slot("sb", OperandKind.Immediate)),
        Vec("nanotrap__I")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb)) => Some(Instruction.NanotrapcbuPipeImm(sb, modifiers, guard))
        case _ => None
      }
  }
  object NanotrapcbuPipePredConst extends Definition(
        "NanotrapcbuPipePredConst",
        "NANOTRAPcbu_pipe",
        Vec(Slot("pp", OperandKind.Predicate), Slot("sb", OperandKind.ConstantBank)),
        Vec("nanotrap__C")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.ConstantBank(sb)) =>
          Some(Instruction.NanotrapcbuPipePredConst(pp, sb, modifiers, guard))
        case _ => None
      }
  }
  object NanotrapcbuPipePredImm extends Definition(
        "NanotrapcbuPipePredImm",
        "NANOTRAPcbu_pipe",
        Vec(Slot("pp", OperandKind.Predicate), Slot("sb", OperandKind.Immediate)),
        Vec("nanotrap__I")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.NanotrapcbuPipePredImm(pp, sb, modifiers, guard))
        case _ => None
      }
  }
  object NanotrapcbuPipePredReg extends Definition(
        "NanotrapcbuPipePredReg",
        "NANOTRAPcbu_pipe",
        Vec(Slot("pp", OperandKind.Predicate), Slot("srcB", OperandKind.Register)),
        Vec("nanotrap__R")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.Register(srcB)) =>
          Some(Instruction.NanotrapcbuPipePredReg(pp, srcB, modifiers, guard))
        case _ => None
      }
  }
  object NanotrapcbuPipeReg extends Definition(
        "NanotrapcbuPipeReg",
        "NANOTRAPcbu_pipe",
        Vec(Slot("srcB", OperandKind.Register)),
        Vec("nanotrap__R")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcB)) => Some(Instruction.NanotrapcbuPipeReg(srcB, modifiers, guard))
        case _ => None
      }
  }
  object NopNoOperands extends Definition("NopNoOperands", "NOP", Vec(), Vec("NOP", "NOP trig", "NOP_trig", "nop_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.NopNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object NopImm extends Definition(
        "NopImm",
        "NOP",
        Vec(Slot("uImm", OperandKind.Immediate)),
        Vec("NOP", "NOP trig", "NOP_trig")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(uImm)) => Some(Instruction.NopImm(uImm, modifiers, guard))
        case _ => None
      }
  }
  object NopSymbol extends Definition(
        "NopSymbol",
        "NOP",
        Vec(Slot("testCC", OperandKind.Symbol)),
        Vec("NOP", "NOP trig", "NOP_trig")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Symbol(testCC)) => Some(Instruction.NopSymbol(testCC, modifiers, guard))
        case _ => None
      }
  }
  object NopSymbolImm extends Definition(
        "NopSymbolImm",
        "NOP",
        Vec(Slot("testCC", OperandKind.Symbol), Slot("uImm", OperandKind.Immediate)),
        Vec("NOP", "NOP trig", "NOP_trig")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Symbol(testCC), InstructionOperand.Immediate(uImm)) =>
          Some(Instruction.NopSymbolImm(testCC, uImm, modifiers, guard))
        case _ => None
      }
  }
  object NopbruPipeNoOperands extends Definition("NopbruPipeNoOperands", "NOPbru_pipe", Vec(), Vec("NOP", "NOP trig")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.NopbruPipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object NopbruPipeImm extends Definition(
        "NopbruPipeImm",
        "NOPbru_pipe",
        Vec(Slot("uImm", OperandKind.Immediate)),
        Vec("NOP", "NOP trig")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(uImm)) => Some(Instruction.NopbruPipeImm(uImm, modifiers, guard))
        case _ => None
      }
  }
  object NopbruPipeSymbol extends Definition(
        "NopbruPipeSymbol",
        "NOPbru_pipe",
        Vec(Slot("testCC", OperandKind.Symbol)),
        Vec("NOP", "NOP trig")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Symbol(testCC)) => Some(Instruction.NopbruPipeSymbol(testCC, modifiers, guard))
        case _ => None
      }
  }
  object NopbruPipeSymbolImm extends Definition(
        "NopbruPipeSymbolImm",
        "NOPbru_pipe",
        Vec(Slot("testCC", OperandKind.Symbol), Slot("uImm", OperandKind.Immediate)),
        Vec("NOP", "NOP trig")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Symbol(testCC), InstructionOperand.Immediate(uImm)) =>
          Some(Instruction.NopbruPipeSymbolImm(testCC, uImm, modifiers, guard))
        case _ => None
      }
  }
  object NopcoupledFePipeNoOperands
      extends Definition("NopcoupledFePipeNoOperands", "NOPcoupled_fe_pipe", Vec(), Vec("NOP", "NOP_trig")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.NopcoupledFePipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object NopcoupledFePipeImm extends Definition(
        "NopcoupledFePipeImm",
        "NOPcoupled_fe_pipe",
        Vec(Slot("uImm", OperandKind.Immediate)),
        Vec("NOP", "NOP_trig")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(uImm)) => Some(Instruction.NopcoupledFePipeImm(uImm, modifiers, guard))
        case _ => None
      }
  }
  object NopcoupledFePipeSymbol extends Definition(
        "NopcoupledFePipeSymbol",
        "NOPcoupled_fe_pipe",
        Vec(Slot("testCC", OperandKind.Symbol)),
        Vec("NOP", "NOP_trig")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Symbol(testCC)) =>
          Some(Instruction.NopcoupledFePipeSymbol(testCC, modifiers, guard))
        case _ => None
      }
  }
  object NopcoupledFePipeSymbolImm extends Definition(
        "NopcoupledFePipeSymbolImm",
        "NOPcoupled_fe_pipe",
        Vec(Slot("testCC", OperandKind.Symbol), Slot("uImm", OperandKind.Immediate)),
        Vec("NOP", "NOP_trig")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Symbol(testCC), InstructionOperand.Immediate(uImm)) =>
          Some(Instruction.NopcoupledFePipeSymbolImm(testCC, uImm, modifiers, guard))
        case _ => None
      }
  }
  object NopfePipeNoOperands extends Definition("NopfePipeNoOperands", "NOPfe_pipe", Vec(), Vec("nop_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.NopfePipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object OmmaRegRegRegRegPredRegRegReg extends Definition(
        "OmmaRegRegRegRegPredRegRegReg",
        "OMMA",
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
        Vec("omma_scale_")
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
          Some(Instruction.OmmaRegRegRegRegPredRegRegReg(dst, srcA, srcB, srcC, uPp, re, rh, uRi, modifiers, guard))
        case _ => None
      }
  }
  object OmmaRegRegRegRegPredRegRegRegImm extends Definition(
        "OmmaRegRegRegRegPredRegRegRegImm",
        "OMMA",
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
        Vec("omma_sp_scale_")
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
  object OmmaRegRegRegRegRegRegReg extends Definition(
        "OmmaRegRegRegRegRegRegReg",
        "OMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("re", OperandKind.Register),
          Slot("rh", OperandKind.Register),
          Slot("uRi", OperandKind.Register)
        ),
        Vec("omma_scale_")
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
            ) => Some(Instruction.OmmaRegRegRegRegRegRegReg(dst, srcA, srcB, srcC, re, rh, uRi, modifiers, guard))
        case _ => None
      }
  }
  object OmmaRegRegRegRegRegRegRegImm extends Definition(
        "OmmaRegRegRegRegRegRegRegImm",
        "OMMA",
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
        Vec("omma_sp_scale_")
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
          Some(Instruction.OmmaRegRegRegRegRegRegRegImm(dst, srcA, srcB, srcC, re, rh, uRi, sparseId, modifiers, guard))
        case _ => None
      }
  }
  object Ommafp16PipeRegRegRegRegPredRegRegReg extends Definition(
        "Ommafp16PipeRegRegRegRegPredRegRegReg",
        "OMMAfp16_pipe",
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
        Vec("omma_scale_")
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
            ) => Some(Instruction.Ommafp16PipeRegRegRegRegPredRegRegReg(
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
  object Ommafp16PipeRegRegRegRegPredRegRegRegImm extends Definition(
        "Ommafp16PipeRegRegRegRegPredRegRegRegImm",
        "OMMAfp16_pipe",
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
        Vec("omma_sp_scale_")
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
            ) => Some(Instruction.Ommafp16PipeRegRegRegRegPredRegRegRegImm(
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
  object Ommafp16PipeRegRegRegRegRegRegReg extends Definition(
        "Ommafp16PipeRegRegRegRegRegRegReg",
        "OMMAfp16_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("re", OperandKind.Register),
          Slot("rh", OperandKind.Register),
          Slot("uRi", OperandKind.Register)
        ),
        Vec("omma_scale_")
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
          Some(Instruction.Ommafp16PipeRegRegRegRegRegRegReg(dst, srcA, srcB, srcC, re, rh, uRi, modifiers, guard))
        case _ => None
      }
  }
  object Ommafp16PipeRegRegRegRegRegRegRegImm extends Definition(
        "Ommafp16PipeRegRegRegRegRegRegRegImm",
        "OMMAfp16_pipe",
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
        Vec("omma_sp_scale_")
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
            ) => Some(Instruction.Ommafp16PipeRegRegRegRegRegRegRegImm(
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
  object OutReg
      extends Definition("OutReg", "OUT", Vec(Slot("srcA", OperandKind.Register)), Vec("out__FINAL", "out_final_rz_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA)) => Some(Instruction.OutReg(srcA, modifiers, guard))
        case _ => None
      }
  }
  object OutRegRegConst extends Definition(
        "OutRegRegConst",
        "OUT",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const OUT", "Const_OUT")
      ) {
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
  object OutRegRegImm extends Definition(
        "OutRegRegImm",
        "OUT",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("Imm OUT", "Imm_OUT", "out__EMIT_Imm")
      ) {
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
  object OutRegRegReg extends Definition(
        "OutRegRegReg",
        "OUT",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("OUT", "out__CUT", "out__EMIT_Rb", "out__EMIT_URb", "out__FINAL")
      ) {
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
  object OutaguPipeRegRegConst extends Definition(
        "OutaguPipeRegRegConst",
        "OUTagu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const OUT")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.OutaguPipeRegRegConst(dst, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object OutaguPipeRegRegImm extends Definition(
        "OutaguPipeRegRegImm",
        "OUTagu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("Imm OUT")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.OutaguPipeRegRegImm(dst, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object OutaguPipeRegRegReg extends Definition(
        "OutaguPipeRegRegReg",
        "OUTagu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("OUT")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.OutaguPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object OutmioPipeReg extends Definition(
        "OutmioPipeReg",
        "OUTmio_pipe",
        Vec(Slot("srcA", OperandKind.Register)),
        Vec("out__FINAL", "out_final_rz_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA)) => Some(Instruction.OutmioPipeReg(srcA, modifiers, guard))
        case _ => None
      }
  }
  object OutmioPipeRegRegConst extends Definition(
        "OutmioPipeRegRegConst",
        "OUTmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const_OUT")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.OutmioPipeRegRegConst(dst, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object OutmioPipeRegRegImm extends Definition(
        "OutmioPipeRegRegImm",
        "OUTmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("Imm_OUT", "out__EMIT_Imm")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.OutmioPipeRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  val definitions: Vec[Definition] = Vec(
    Lop3fmaiPipeRegRegConstRegImm,
    Lop3fmaiPipeRegRegImmReg,
    Lop3fmaiPipeRegRegImmRegImm,
    Lop3fmaiPipeRegRegRegReg,
    Lop3fmaiPipeRegRegRegRegImm,
    Lop3fxuPipePredRegRegRegReg,
    Lop3fxuPipePredRegRegRegRegImm,
    Lop3fxuPipeRegRegConstReg,
    Lop3fxuPipeRegRegConstRegImm,
    Lop3fxuPipeRegRegImmReg,
    Lop3fxuPipeRegRegImmRegImm,
    Lop3fxuPipeRegRegRegReg,
    Lop3fxuPipeRegRegRegRegImm,
    Lop3intPipePredRegRegAddressImmReg,
    Lop3intPipePredRegRegAddressImmRegImm,
    Lop3intPipePredRegRegAddressImmRegImmPred,
    Lop3intPipePredRegRegAddressImmRegPred,
    Lop3intPipePredRegRegConstReg,
    Lop3intPipePredRegRegConstRegImm,
    Lop3intPipePredRegRegConstRegImmPred,
    Lop3intPipePredRegRegConstRegPred,
    Lop3intPipePredRegRegImmReg,
    Lop3intPipePredRegRegImmRegImm,
    Lop3intPipePredRegRegImmRegImmPred,
    Lop3intPipePredRegRegImmRegPred,
    Lop3intPipePredRegRegRegReg,
    Lop3intPipePredRegRegRegRegImm,
    Lop3intPipePredRegRegRegRegImmPred,
    Lop3intPipePredRegRegRegRegPred,
    Lop3intPipeRegRegAddressImmReg,
    Lop3intPipeRegRegAddressImmRegImm,
    Lop3intPipeRegRegAddressImmRegImmPred,
    Lop3intPipeRegRegAddressImmRegPred,
    Lop3intPipeRegRegConstReg,
    Lop3intPipeRegRegConstRegImm,
    Lop3intPipeRegRegConstRegImmPred,
    Lop3intPipeRegRegConstRegPred,
    Lop3intPipeRegRegImmReg,
    Lop3intPipeRegRegImmRegImm,
    Lop3intPipeRegRegImmRegImmPred,
    Lop3intPipeRegRegImmRegPred,
    Lop3intPipeRegRegRegReg,
    Lop3intPipeRegRegRegRegImm,
    Lop3intPipeRegRegRegRegImmPred,
    Lop3intPipeRegRegRegRegPred,
    LopfauPipeRegRegConst,
    LopfauPipeRegRegImm,
    LopfauPipeRegRegReg,
    LopfmaiPipePredRegRegConst,
    LopfmaiPipePredRegRegImm,
    LopfmaiPipePredRegRegReg,
    LopfmaiPipeRegRegConst,
    LopfmaiPipeRegRegImm,
    LopfmaiPipeRegRegReg,
    LopfxuPipePredRegRegConst,
    LopfxuPipePredRegRegImm,
    LopfxuPipePredRegRegReg,
    LopfxuPipeRegRegConst,
    LopfxuPipeRegRegImm,
    LopfxuPipeRegRegReg,
    LopintPipePredRegRegAddressImm,
    LopintPipePredRegRegAddressImmPred,
    LopintPipePredRegRegConst,
    LopintPipePredRegRegConstPred,
    LopintPipePredRegRegImm,
    LopintPipePredRegRegImmPred,
    LopintPipePredRegRegReg,
    LopintPipePredRegRegRegPred,
    LopintPipeRegRegAddressImm,
    LopintPipeRegRegAddressImmPred,
    LopintPipeRegRegConst,
    LopintPipeRegRegConstPred,
    LopintPipeRegRegImm,
    LopintPipeRegRegImmPred,
    LopintPipeRegRegReg,
    LopintPipeRegRegRegPred,
    LopxluPipeRegRegConst,
    LopxluPipeRegRegImm,
    LopxluPipeRegRegReg,
    MatchPredRegReg,
    MatchRegReg,
    MatchmioPipePredRegReg,
    MatchmioPipeRegReg,
    MembarNoOperands,
    MembaraguPipeNoOperands,
    MembarmioPipeNoOperands,
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
    Mov32ifauPipeRegImm,
    Mov32ifauPipeRegImmImm,
    Mov32ifmaxPipeRegImm,
    Mov32ifmaxPipeRegImmImm,
    Mov32ifmaxlitewPipeRegImm,
    Mov32ifmaxlitewPipeRegImmImm,
    Mov32ifmaiPipeRegImm,
    Mov32ifmaiPipeRegImmImm,
    Mov32ifuPipeRegImm,
    Mov32ifuPipeRegImmImm,
    Mov32ifxuPipeRegImm,
    Mov32ifxuPipeRegImmImm,
    Mov32iintPipeRegImm,
    Mov32iintPipeRegImmImm,
    Mov32ixluPipeRegImm,
    Mov32ixluPipeRegImmImm,
    Mov64iurRegImm,
    Mov64iurRegReg,
    Mov64iurintPipeRegImm,
    Mov64iurintPipeRegReg,
    MovmRegReg,
    MovmintPipeRegReg,
    MovfauPipeRegConst,
    MovfauPipeRegConstImm,
    MovfauPipeRegImm,
    MovfauPipeRegImmImm,
    MovfauPipeRegReg,
    MovfauPipeRegRegImm,
    MovfmaxPipeRegConst,
    MovfmaxPipeRegConstImm,
    MovfmaxPipeRegImm,
    MovfmaxPipeRegImmImm,
    MovfmaxPipeRegReg,
    MovfmaxPipeRegRegImm,
    MovfmaxlitewPipeRegConst,
    MovfmaxlitewPipeRegConstImm,
    MovfmaxlitewPipeRegImm,
    MovfmaxlitewPipeRegImmImm,
    MovfmaxlitewPipeRegReg,
    MovfmaxlitewPipeRegRegImm,
    MovfmaiPipeRegConst,
    MovfmaiPipeRegConstImm,
    MovfmaiPipeRegImm,
    MovfmaiPipeRegImmImm,
    MovfmaiPipeRegReg,
    MovfmaiPipeRegRegImm,
    MovfuPipeRegConst,
    MovfuPipeRegConstImm,
    MovfuPipeRegImm,
    MovfuPipeRegImmImm,
    MovfuPipeRegReg,
    MovfuPipeRegRegImm,
    MovfxuPipeRegConst,
    MovfxuPipeRegConstImm,
    MovfxuPipeRegImm,
    MovfxuPipeRegImmImm,
    MovfxuPipeRegReg,
    MovfxuPipeRegRegImm,
    MovintPipeAddressConst,
    MovintPipeAddressConstImm,
    MovintPipeAddressImm,
    MovintPipeAddressImmImm,
    MovintPipeAddressReg,
    MovintPipeAddressRegImm,
    MovintPipeRegAddress,
    MovintPipeRegAddressImm,
    MovintPipeRegAddressImmImm,
    MovintPipeRegConst,
    MovintPipeRegConstImm,
    MovintPipeRegImm,
    MovintPipeRegImmImm,
    MovintPipeRegReg,
    MovintPipeRegRegImm,
    MovxluPipeRegConst,
    MovxluPipeRegConstImm,
    MovxluPipeRegImm,
    MovxluPipeRegImmImm,
    MovxluPipeRegReg,
    MovxluPipeRegRegImm,
    MufuRegAddressImm,
    MufuRegConst,
    MufuRegImm,
    MufuRegImmImm,
    MufuRegReg,
    MufufuPipeRegReg,
    MufumioPipeRegAddressImm,
    MufumioPipeRegConst,
    MufumioPipeRegImm,
    MufumioPipeRegImmImm,
    MufumioPipeRegReg,
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
    NanosleepcbuPipeNoOperands,
    NanosleepcbuPipeAddressImm,
    NanosleepcbuPipeConst,
    NanosleepcbuPipeImm,
    NanosleepcbuPipePred,
    NanosleepcbuPipePredAddressImm,
    NanosleepcbuPipePredConst,
    NanosleepcbuPipePredImm,
    NanosleepcbuPipePredReg,
    NanosleepcbuPipeReg,
    NanotrapConst,
    NanotrapImm,
    NanotrapPredConst,
    NanotrapPredImm,
    NanotrapPredReg,
    NanotrapReg,
    NanotrapcbuPipeConst,
    NanotrapcbuPipeImm,
    NanotrapcbuPipePredConst,
    NanotrapcbuPipePredImm,
    NanotrapcbuPipePredReg,
    NanotrapcbuPipeReg,
    NopNoOperands,
    NopImm,
    NopSymbol,
    NopSymbolImm,
    NopbruPipeNoOperands,
    NopbruPipeImm,
    NopbruPipeSymbol,
    NopbruPipeSymbolImm,
    NopcoupledFePipeNoOperands,
    NopcoupledFePipeImm,
    NopcoupledFePipeSymbol,
    NopcoupledFePipeSymbolImm,
    NopfePipeNoOperands,
    OmmaRegRegRegRegPredRegRegReg,
    OmmaRegRegRegRegPredRegRegRegImm,
    OmmaRegRegRegRegRegRegReg,
    OmmaRegRegRegRegRegRegRegImm,
    Ommafp16PipeRegRegRegRegPredRegRegReg,
    Ommafp16PipeRegRegRegRegPredRegRegRegImm,
    Ommafp16PipeRegRegRegRegRegRegReg,
    Ommafp16PipeRegRegRegRegRegRegRegImm,
    OutReg,
    OutRegRegConst,
    OutRegRegImm,
    OutRegRegReg,
    OutaguPipeRegRegConst,
    OutaguPipeRegRegImm,
    OutaguPipeRegRegReg,
    OutmioPipeReg,
    OutmioPipeRegRegConst,
    OutmioPipeRegRegImm
  )
}
