// Generated SASS instruction definitions and checked operand constructors. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.sass
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import Instruction.*
@javax.annotation.processing.Generated(Array("sbt codegen"))
object InstructionDefinitions6 {
  object ImulfmalighterPipeRegRegRegImm extends Definition(
        "ImulfmalighterPipeRegRegRegImm",
        "IMULfmalighter_pipe",
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
            ) => Some(Instruction.ImulfmalighterPipeRegRegRegImm(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object ImulfmalighterPipeRegRegRegReg extends Definition(
        "ImulfmalighterPipeRegRegRegReg",
        "IMULfmalighter_pipe",
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
            ) => Some(Instruction.ImulfmalighterPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object ImulmioPipeRegRegConst extends Definition(
        "ImulmioPipeRegRegConst",
        "IMULmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const_IMUL")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.ImulmioPipeRegRegConst(dst, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object ImulmioPipeRegRegImm extends Definition(
        "ImulmioPipeRegRegImm",
        "IMULmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("Imm_IMUL")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.ImulmioPipeRegRegImm(dst, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object ImulmioPipeRegRegReg extends Definition(
        "ImulmioPipeRegRegReg",
        "IMULmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("IMUL")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.ImulmioPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object IpaRegAddress extends Definition(
        "IpaRegAddress",
        "IPA",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address)),
        Vec("I IPA 2", "IPA 2", "ipa_ur_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Address(srcA)) =>
          Some(Instruction.IpaRegAddress(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object IpaRegAddressImm extends Definition(
        "IpaRegAddressImm",
        "IPA",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address), Slot("sb", OperandKind.Immediate)),
        Vec("ipa_ur_offset__IPA_URa_Ib")
      ) {
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
  object IpaRegAddressPred extends Definition(
        "IpaRegAddressPred",
        "IPA",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address), Slot("ps", OperandKind.Predicate)),
        Vec("IPA_2")
      ) {
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
  object IpaRegAddressReg extends Definition(
        "IpaRegAddressReg",
        "IPA",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register)),
        Vec("ipa_ur_offset__IPA_URa_Rb")
      ) {
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
  object IpaRegAddressRegReg extends Definition(
        "IpaRegAddressRegReg",
        "IPA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("I IPA 1", "IPA 1")
      ) {
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
  object IpaRegAddressRegRegPred extends Definition(
        "IpaRegAddressRegRegPred",
        "IPA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("ps", OperandKind.Predicate)
        ),
        Vec("IPA_1")
      ) {
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
  object IpaRegImm extends Definition(
        "IpaRegImm",
        "IPA",
        Vec(Slot("dst", OperandKind.Register), Slot("attr", OperandKind.Immediate)),
        Vec("I_IPA_2", "ipa_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(attr)) =>
          Some(Instruction.IpaRegImm(dst, attr, modifiers, guard))
        case _ => None
      }
  }
  object IpaRegImmConst extends Definition(
        "IpaRegImmConst",
        "IPA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("attr", OperandKind.Immediate),
          Slot("sb", OperandKind.ConstantBank)
        ),
        Vec("ipa_offset__IPA_C")
      ) {
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
  object IpaRegImmImm extends Definition(
        "IpaRegImmImm",
        "IPA",
        Vec(Slot("dst", OperandKind.Register), Slot("attr", OperandKind.Immediate), Slot("sb", OperandKind.Immediate)),
        Vec("ipa_offset__IPA_Ib", "ipa_offset__IPA_Imm")
      ) {
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
  object IpaRegImmPred extends Definition(
        "IpaRegImmPred",
        "IPA",
        Vec(Slot("dst", OperandKind.Register), Slot("uImm", OperandKind.Immediate), Slot("ps", OperandKind.Predicate)),
        Vec("I_IPA_2")
      ) {
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
  object IpaRegImmReg extends Definition(
        "IpaRegImmReg",
        "IPA",
        Vec(Slot("dst", OperandKind.Register), Slot("attr", OperandKind.Immediate), Slot("srcB", OperandKind.Register)),
        Vec("ipa_offset__IPA_Rb")
      ) {
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
  object IpaRegImmRegReg extends Definition(
        "IpaRegImmRegReg",
        "IPA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("I_IPA_1")
      ) {
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
  object IpaRegImmRegRegPred extends Definition(
        "IpaRegImmRegRegPred",
        "IPA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("ps", OperandKind.Predicate)
        ),
        Vec("I_IPA_1")
      ) {
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
  object IpaRegPredAddress extends Definition(
        "IpaRegPredAddress",
        "IPA",
        Vec(Slot("dst", OperandKind.Register), Slot("pu", OperandKind.Predicate), Slot("srcA", OperandKind.Address)),
        Vec("ipa_ur_")
      ) {
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
  object IpaRegPredAddressImm extends Definition(
        "IpaRegPredAddressImm",
        "IPA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Address),
          Slot("sb", OperandKind.Immediate)
        ),
        Vec("ipa_ur_offset__IPA_URa_Ib")
      ) {
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
  object IpaRegPredAddressReg extends Definition(
        "IpaRegPredAddressReg",
        "IPA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register)
        ),
        Vec("ipa_ur_offset__IPA_URa_Rb")
      ) {
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
  object IpaRegPredImm extends Definition(
        "IpaRegPredImm",
        "IPA",
        Vec(Slot("dst", OperandKind.Register), Slot("pu", OperandKind.Predicate), Slot("attr", OperandKind.Immediate)),
        Vec("ipa_")
      ) {
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
  object IpaRegPredImmConst extends Definition(
        "IpaRegPredImmConst",
        "IPA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("attr", OperandKind.Immediate),
          Slot("sb", OperandKind.ConstantBank)
        ),
        Vec("ipa_offset__IPA_C")
      ) {
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
  object IpaRegPredImmImm extends Definition(
        "IpaRegPredImmImm",
        "IPA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("attr", OperandKind.Immediate),
          Slot("sb", OperandKind.Immediate)
        ),
        Vec("ipa_offset__IPA_Ib", "ipa_offset__IPA_Imm")
      ) {
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
  object IpaRegPredImmReg extends Definition(
        "IpaRegPredImmReg",
        "IPA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("attr", OperandKind.Immediate),
          Slot("srcB", OperandKind.Register)
        ),
        Vec("ipa_offset__IPA_Rb")
      ) {
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
  object IpamioPipeRegAddress extends Definition(
        "IpamioPipeRegAddress",
        "IPAmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address)),
        Vec("ipa_ur_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Address(srcA)) =>
          Some(Instruction.IpamioPipeRegAddress(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object IpamioPipeRegAddressImm extends Definition(
        "IpamioPipeRegAddressImm",
        "IPAmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address), Slot("sb", OperandKind.Immediate)),
        Vec("ipa_ur_offset__IPA_URa_Ib")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.IpamioPipeRegAddressImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object IpamioPipeRegAddressPred extends Definition(
        "IpamioPipeRegAddressPred",
        "IPAmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address), Slot("ps", OperandKind.Predicate)),
        Vec("IPA_2")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Predicate(ps)
            ) => Some(Instruction.IpamioPipeRegAddressPred(dst, srcA, ps, modifiers, guard))
        case _ => None
      }
  }
  object IpamioPipeRegAddressReg extends Definition(
        "IpamioPipeRegAddressReg",
        "IPAmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register)),
        Vec("ipa_ur_offset__IPA_URa_Rb")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.IpamioPipeRegAddressReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object IpamioPipeRegAddressRegRegPred extends Definition(
        "IpamioPipeRegAddressRegRegPred",
        "IPAmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("ps", OperandKind.Predicate)
        ),
        Vec("IPA_1")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(ps)
            ) => Some(Instruction.IpamioPipeRegAddressRegRegPred(dst, srcA, srcB, srcC, ps, modifiers, guard))
        case _ => None
      }
  }
  object IpamioPipeRegImm extends Definition(
        "IpamioPipeRegImm",
        "IPAmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("attr", OperandKind.Immediate)),
        Vec("I_IPA_2", "ipa_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(attr)) =>
          Some(Instruction.IpamioPipeRegImm(dst, attr, modifiers, guard))
        case _ => None
      }
  }
  object IpamioPipeRegImmConst extends Definition(
        "IpamioPipeRegImmConst",
        "IPAmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("attr", OperandKind.Immediate),
          Slot("sb", OperandKind.ConstantBank)
        ),
        Vec("ipa_offset__IPA_C")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Immediate(attr),
              InstructionOperand.ConstantBank(sb)
            ) => Some(Instruction.IpamioPipeRegImmConst(dst, attr, sb, modifiers, guard))
        case _ => None
      }
  }
  object IpamioPipeRegImmImm extends Definition(
        "IpamioPipeRegImmImm",
        "IPAmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("attr", OperandKind.Immediate), Slot("sb", OperandKind.Immediate)),
        Vec("ipa_offset__IPA_Ib", "ipa_offset__IPA_Imm")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Immediate(attr),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.IpamioPipeRegImmImm(dst, attr, sb, modifiers, guard))
        case _ => None
      }
  }
  object IpamioPipeRegImmPred extends Definition(
        "IpamioPipeRegImmPred",
        "IPAmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("uImm", OperandKind.Immediate), Slot("ps", OperandKind.Predicate)),
        Vec("I_IPA_2")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Predicate(ps)
            ) => Some(Instruction.IpamioPipeRegImmPred(dst, uImm, ps, modifiers, guard))
        case _ => None
      }
  }
  object IpamioPipeRegImmReg extends Definition(
        "IpamioPipeRegImmReg",
        "IPAmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("attr", OperandKind.Immediate), Slot("srcB", OperandKind.Register)),
        Vec("ipa_offset__IPA_Rb")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Immediate(attr),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.IpamioPipeRegImmReg(dst, attr, srcB, modifiers, guard))
        case _ => None
      }
  }
  object IpamioPipeRegImmRegReg extends Definition(
        "IpamioPipeRegImmRegReg",
        "IPAmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("I_IPA_1")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.IpamioPipeRegImmRegReg(dst, uImm, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object IpamioPipeRegImmRegRegPred extends Definition(
        "IpamioPipeRegImmRegRegPred",
        "IPAmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("ps", OperandKind.Predicate)
        ),
        Vec("I_IPA_1")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(ps)
            ) => Some(Instruction.IpamioPipeRegImmRegRegPred(dst, uImm, srcB, srcC, ps, modifiers, guard))
        case _ => None
      }
  }
  object IpamioPipeRegPredAddress extends Definition(
        "IpamioPipeRegPredAddress",
        "IPAmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("pu", OperandKind.Predicate), Slot("srcA", OperandKind.Address)),
        Vec("ipa_ur_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Address(srcA)
            ) => Some(Instruction.IpamioPipeRegPredAddress(dst, pu, srcA, modifiers, guard))
        case _ => None
      }
  }
  object IpamioPipeRegPredAddressImm extends Definition(
        "IpamioPipeRegPredAddressImm",
        "IPAmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Address),
          Slot("sb", OperandKind.Immediate)
        ),
        Vec("ipa_ur_offset__IPA_URa_Ib")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Address(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.IpamioPipeRegPredAddressImm(dst, pu, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object IpamioPipeRegPredAddressReg extends Definition(
        "IpamioPipeRegPredAddressReg",
        "IPAmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register)
        ),
        Vec("ipa_ur_offset__IPA_URa_Rb")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.IpamioPipeRegPredAddressReg(dst, pu, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object IpamioPipeRegPredImm extends Definition(
        "IpamioPipeRegPredImm",
        "IPAmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("pu", OperandKind.Predicate), Slot("attr", OperandKind.Immediate)),
        Vec("ipa_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Immediate(attr)
            ) => Some(Instruction.IpamioPipeRegPredImm(dst, pu, attr, modifiers, guard))
        case _ => None
      }
  }
  object IpamioPipeRegPredImmConst extends Definition(
        "IpamioPipeRegPredImmConst",
        "IPAmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("attr", OperandKind.Immediate),
          Slot("sb", OperandKind.ConstantBank)
        ),
        Vec("ipa_offset__IPA_C")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Immediate(attr),
              InstructionOperand.ConstantBank(sb)
            ) => Some(Instruction.IpamioPipeRegPredImmConst(dst, pu, attr, sb, modifiers, guard))
        case _ => None
      }
  }
  object IpamioPipeRegPredImmImm extends Definition(
        "IpamioPipeRegPredImmImm",
        "IPAmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("attr", OperandKind.Immediate),
          Slot("sb", OperandKind.Immediate)
        ),
        Vec("ipa_offset__IPA_Ib", "ipa_offset__IPA_Imm")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Immediate(attr),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.IpamioPipeRegPredImmImm(dst, pu, attr, sb, modifiers, guard))
        case _ => None
      }
  }
  object IpamioPipeRegPredImmReg extends Definition(
        "IpamioPipeRegPredImmReg",
        "IPAmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("attr", OperandKind.Immediate),
          Slot("srcB", OperandKind.Register)
        ),
        Vec("ipa_offset__IPA_Rb")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Immediate(attr),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.IpamioPipeRegPredImmReg(dst, pu, attr, srcB, modifiers, guard))
        case _ => None
      }
  }
  object IpasuPipeRegAddress extends Definition(
        "IpasuPipeRegAddress",
        "IPAsu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address)),
        Vec("I IPA 2", "IPA 2")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Address(srcA)) =>
          Some(Instruction.IpasuPipeRegAddress(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object IpasuPipeRegAddressRegReg extends Definition(
        "IpasuPipeRegAddressRegReg",
        "IPAsu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("I IPA 1", "IPA 1")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.IpasuPipeRegAddressRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object IsadRegRegConstReg extends Definition(
        "IsadRegRegConstReg",
        "ISAD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Const ISAD")
      ) {
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
  object IsadRegRegImmReg extends Definition(
        "IsadRegRegImmReg",
        "ISAD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm ISAD")
      ) {
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
  object IsadRegRegRegConst extends Definition(
        "IsadRegRegRegConst",
        "ISAD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const1 ISAD")
      ) {
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
  object IsadRegRegRegReg extends Definition(
        "IsadRegRegRegReg",
        "ISAD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("ISAD")
      ) {
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
  object IsadxluPipeRegRegConstReg extends Definition(
        "IsadxluPipeRegRegConstReg",
        "ISADxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Const ISAD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.IsadxluPipeRegRegConstReg(dst, srcA, srcConst, srcC, modifiers, guard))
        case _ => None
      }
  }
  object IsadxluPipeRegRegImmReg extends Definition(
        "IsadxluPipeRegRegImmReg",
        "ISADxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm ISAD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.IsadxluPipeRegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object IsadxluPipeRegRegRegConst extends Definition(
        "IsadxluPipeRegRegRegConst",
        "ISADxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const1 ISAD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.IsadxluPipeRegRegRegConst(dst, srcA, srcB, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object IsadxluPipeRegRegRegReg extends Definition(
        "IsadxluPipeRegRegRegReg",
        "ISADxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("ISAD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.IsadxluPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object IsberdRegAddress extends Definition(
        "IsberdRegAddress",
        "ISBERD",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address)),
        Vec("ISBERD", "isberd_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Address(srcA)) =>
          Some(Instruction.IsberdRegAddress(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object IsberdmioPipeRegAddress extends Definition(
        "IsberdmioPipeRegAddress",
        "ISBERDmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address)),
        Vec("ISBERD", "isberd_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Address(srcA)) =>
          Some(Instruction.IsberdmioPipeRegAddress(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object IsbewrAddressReg extends Definition(
        "IsbewrAddressReg",
        "ISBEWR",
        Vec(Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register)),
        Vec("isbewr_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA), InstructionOperand.Register(srcB)) =>
          Some(Instruction.IsbewrAddressReg(srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object IsbewrmioPipeAddressReg extends Definition(
        "IsbewrmioPipeAddressReg",
        "ISBEWRmio_pipe",
        Vec(Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register)),
        Vec("isbewr_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA), InstructionOperand.Register(srcB)) =>
          Some(Instruction.IsbewrmioPipeAddressReg(srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object IscaddRegPredRegAddressImmImm extends Definition(
        "IscaddRegPredRegAddressImmImm",
        "ISCADD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("iscadd_noimm__RCxR_RCxR")
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
            ) =>
          Some(Instruction.IscaddRegPredRegAddressImmImm(dst, pu, srcA, srcB, sb_offset, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object IscaddRegPredRegConstImm extends Definition(
        "IscaddRegPredRegConstImm",
        "ISCADD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("iscadd_noimm__RCR_RCR")
      ) {
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
  object IscaddRegPredRegImmImm extends Definition(
        "IscaddRegPredRegImmImm",
        "ISCADD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("iscadd_imm__RIR_RIR")
      ) {
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
  object IscaddRegPredRegRegImm extends Definition(
        "IscaddRegPredRegRegImm",
        "ISCADD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("iscadd_noimm__RRR_RRR", "iscadd_noimm__RUR_RUR")
      ) {
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
  object IscaddRegRegAddressImmImm extends Definition(
        "IscaddRegRegAddressImmImm",
        "ISCADD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("iscadd_noimm__RCxR_RCxR")
      ) {
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
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("scaleU5", OperandKind.Immediate)
        ),
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
  object IscaddRegRegImmImm extends Definition(
        "IscaddRegRegImmImm",
        "ISCADD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("Imm ISCADD", "Imm_ISCADD", "iscadd_imm__RIR_RIR")
      ) {
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
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate)
        ),
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
  object Iscadd32iRegPredRegImmImm extends Definition(
        "Iscadd32iRegPredRegImmImm",
        "ISCADD32I",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("iscadd32i__RIR_RIR")
      ) {
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
  object Iscadd32iRegRegImmImm extends Definition(
        "Iscadd32iRegRegImmImm",
        "ISCADD32I",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("ISCADD32I", "iscadd32i__RIR_RIR")
      ) {
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
  object Iscadd32ifxuPipeRegRegImmImm extends Definition(
        "Iscadd32ifxuPipeRegRegImmImm",
        "ISCADD32Ifxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sImm", OperandKind.Immediate),
          Slot("shift", OperandKind.Immediate)
        ),
        Vec("ISCADD32I")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sImm),
              InstructionOperand.Immediate(shift)
            ) => Some(Instruction.Iscadd32ifxuPipeRegRegImmImm(dst, srcA, sImm, shift, modifiers, guard))
        case _ => None
      }
  }
  object Iscadd32iintPipeRegPredRegImmImm extends Definition(
        "Iscadd32iintPipeRegPredRegImmImm",
        "ISCADD32Iint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("iscadd32i__RIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.Iscadd32iintPipeRegPredRegImmImm(dst, pu, srcA, sb, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object Iscadd32iintPipeRegRegImmImm extends Definition(
        "Iscadd32iintPipeRegRegImmImm",
        "ISCADD32Iint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("iscadd32i__RIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.Iscadd32iintPipeRegRegImmImm(dst, srcA, sb, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object Iscadd32ixluPipeRegRegImmImm extends Definition(
        "Iscadd32ixluPipeRegRegImmImm",
        "ISCADD32Ixlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sImm", OperandKind.Immediate),
          Slot("shift", OperandKind.Immediate)
        ),
        Vec("ISCADD32I")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sImm),
              InstructionOperand.Immediate(shift)
            ) => Some(Instruction.Iscadd32ixluPipeRegRegImmImm(dst, srcA, sImm, shift, modifiers, guard))
        case _ => None
      }
  }
  object IscaddfxuPipeRegRegConstImm extends Definition(
        "IscaddfxuPipeRegRegConstImm",
        "ISCADDfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("shift", OperandKind.Immediate)
        ),
        Vec("Const_ISCADD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Immediate(shift)
            ) => Some(Instruction.IscaddfxuPipeRegRegConstImm(dst, srcA, srcConst, shift, modifiers, guard))
        case _ => None
      }
  }
  object IscaddfxuPipeRegRegImmImm extends Definition(
        "IscaddfxuPipeRegRegImmImm",
        "ISCADDfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("shift", OperandKind.Immediate)
        ),
        Vec("Imm_ISCADD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Immediate(shift)
            ) => Some(Instruction.IscaddfxuPipeRegRegImmImm(dst, srcA, uImm, shift, modifiers, guard))
        case _ => None
      }
  }
  object IscaddfxuPipeRegRegRegImm extends Definition(
        "IscaddfxuPipeRegRegRegImm",
        "ISCADDfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("shift", OperandKind.Immediate)
        ),
        Vec("ISCADD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(shift)
            ) => Some(Instruction.IscaddfxuPipeRegRegRegImm(dst, srcA, srcB, shift, modifiers, guard))
        case _ => None
      }
  }
  object IscaddintPipeRegPredRegAddressImmImm extends Definition(
        "IscaddintPipeRegPredRegAddressImmImm",
        "ISCADDint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("iscadd_noimm__RCxR_RCxR")
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
            ) => Some(Instruction.IscaddintPipeRegPredRegAddressImmImm(
            dst,
            pu,
            srcA,
            srcB,
            sb_offset,
            scaleU5,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object IscaddintPipeRegPredRegConstImm extends Definition(
        "IscaddintPipeRegPredRegConstImm",
        "ISCADDint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("iscadd_noimm__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.IscaddintPipeRegPredRegConstImm(dst, pu, srcA, sb, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object IscaddintPipeRegPredRegImmImm extends Definition(
        "IscaddintPipeRegPredRegImmImm",
        "ISCADDint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("iscadd_imm__RIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.IscaddintPipeRegPredRegImmImm(dst, pu, srcA, sb, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object IscaddintPipeRegPredRegRegImm extends Definition(
        "IscaddintPipeRegPredRegRegImm",
        "ISCADDint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("iscadd_noimm__RRR_RRR", "iscadd_noimm__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.IscaddintPipeRegPredRegRegImm(dst, pu, srcA, srcB, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object IscaddintPipeRegRegAddressImmImm extends Definition(
        "IscaddintPipeRegRegAddressImmImm",
        "ISCADDint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("iscadd_noimm__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Immediate(scaleU5)
            ) =>
          Some(Instruction.IscaddintPipeRegRegAddressImmImm(dst, srcA, srcB, sb_offset, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object IscaddintPipeRegRegConstImm extends Definition(
        "IscaddintPipeRegRegConstImm",
        "ISCADDint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("iscadd_noimm__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.IscaddintPipeRegRegConstImm(dst, srcA, sb, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object IscaddintPipeRegRegImmImm extends Definition(
        "IscaddintPipeRegRegImmImm",
        "ISCADDint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("iscadd_imm__RIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.IscaddintPipeRegRegImmImm(dst, srcA, sb, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object IscaddintPipeRegRegRegImm extends Definition(
        "IscaddintPipeRegRegRegImm",
        "ISCADDint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("iscadd_noimm__RRR_RRR", "iscadd_noimm__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.IscaddintPipeRegRegRegImm(dst, srcA, srcB, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object IscaddxluPipeRegRegConstImm extends Definition(
        "IscaddxluPipeRegRegConstImm",
        "ISCADDxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("shift", OperandKind.Immediate)
        ),
        Vec("Const ISCADD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Immediate(shift)
            ) => Some(Instruction.IscaddxluPipeRegRegConstImm(dst, srcA, srcConst, shift, modifiers, guard))
        case _ => None
      }
  }
  object IscaddxluPipeRegRegImmImm extends Definition(
        "IscaddxluPipeRegRegImmImm",
        "ISCADDxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("shift", OperandKind.Immediate)
        ),
        Vec("Imm ISCADD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Immediate(shift)
            ) => Some(Instruction.IscaddxluPipeRegRegImmImm(dst, srcA, uImm, shift, modifiers, guard))
        case _ => None
      }
  }
  object IscaddxluPipeRegRegRegImm extends Definition(
        "IscaddxluPipeRegRegRegImm",
        "ISCADDxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("shift", OperandKind.Immediate)
        ),
        Vec("ISCADD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(shift)
            ) => Some(Instruction.IscaddxluPipeRegRegRegImm(dst, srcA, srcB, shift, modifiers, guard))
        case _ => None
      }
  }
  object IsetRegRegConst extends Definition(
        "IsetRegRegConst",
        "ISET",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
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
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("pa", OperandKind.Predicate)
        ),
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
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
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
  object IsetRegRegImmPred extends Definition(
        "IsetRegRegImmPred",
        "ISET",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Imm ISET", "Imm ISET U", "Imm_ISET", "Imm_ISET_U")
      ) {
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
  object IsetRegRegReg extends Definition(
        "IsetRegRegReg",
        "ISET",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("NoBop ISET", "NoBop ISET U", "NoBop_ISET", "NoBop_ISET_U")
      ) {
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
  object IsetRegRegRegPred extends Definition(
        "IsetRegRegRegPred",
        "ISET",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("ISET", "ISET U", "ISET_U")
      ) {
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
  object IsetpPredPredRegAddressImmPred extends Definition(
        "IsetpPredPredRegAddressImmPred",
        "ISETP",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("isetp__RCxR_RCxR", "isetp__RCxR_RCxR_noEX")
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
            ) => Some(Instruction.IsetpPredPredRegAddressImmPred(pu, pv, srcA, srcB, sb_offset, pp, modifiers, guard))
        case _ => None
      }
  }
  object IsetpPredPredRegAddressImmPredPred extends Definition(
        "IsetpPredPredRegAddressImmPredPred",
        "ISETP",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate),
          Slot("pr", OperandKind.Predicate)
        ),
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
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("pa", OperandKind.Predicate)
        ),
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
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("pp", OperandKind.Predicate),
          Slot("pr", OperandKind.Predicate)
        ),
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
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("pa", OperandKind.Predicate)
        ),
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
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate),
          Slot("pr", OperandKind.Predicate)
        ),
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
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pa", OperandKind.Predicate)
        ),
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
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pp", OperandKind.Predicate),
          Slot("pr", OperandKind.Predicate)
        ),
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
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
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
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("pr", OperandKind.Predicate)
        ),
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
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
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
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("pr", OperandKind.Predicate)
        ),
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
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate)
        ),
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
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("pr", OperandKind.Predicate)
        ),
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
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register)
        ),
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
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pr", OperandKind.Predicate)
        ),
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
  object IsetpfauPipePredPredRegConstPred extends Definition(
        "IsetpfauPipePredPredRegConstPred",
        "ISETPfau_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Const ISETP", "Const ISETP U")
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
          Some(Instruction.IsetpfauPipePredPredRegConstPred(dstPredicate, nPd, srcA, srcConst, pa, modifiers, guard))
        case _ => None
      }
  }
  object IsetpfauPipePredPredRegImmPred extends Definition(
        "IsetpfauPipePredPredRegImmPred",
        "ISETPfau_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Imm ISETP", "Imm ISETP U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.IsetpfauPipePredPredRegImmPred(dstPredicate, nPd, srcA, uImm, pa, modifiers, guard))
        case _ => None
      }
  }
  object IsetpfauPipePredPredRegRegPred extends Definition(
        "IsetpfauPipePredPredRegRegPred",
        "ISETPfau_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("ISETP", "ISETP U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.IsetpfauPipePredPredRegRegPred(dstPredicate, nPd, srcA, srcB, pa, modifiers, guard))
        case _ => None
      }
  }
  object IsetpfauPipePredRegConst extends Definition(
        "IsetpfauPipePredRegConst",
        "ISETPfau_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("NoBop Const ISETP", "NoBop Const ISETP U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.IsetpfauPipePredRegConst(dstPredicate, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object IsetpfauPipePredRegImm extends Definition(
        "IsetpfauPipePredRegImm",
        "ISETPfau_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate)
        ),
        Vec("NoBop Imm ISETP", "NoBop Imm ISETP U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.IsetpfauPipePredRegImm(dstPredicate, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object IsetpfauPipePredRegReg extends Definition(
        "IsetpfauPipePredRegReg",
        "ISETPfau_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register)
        ),
        Vec("NoBop ISETP", "NoBop ISETP U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.IsetpfauPipePredRegReg(dstPredicate, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object IsetpfxuPipePredPredRegConstPred extends Definition(
        "IsetpfxuPipePredPredRegConstPred",
        "ISETPfxu_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Const_ISETP", "Const_ISETP_U")
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
          Some(Instruction.IsetpfxuPipePredPredRegConstPred(dstPredicate, nPd, srcA, srcConst, pa, modifiers, guard))
        case _ => None
      }
  }
  object IsetpfxuPipePredPredRegImmPred extends Definition(
        "IsetpfxuPipePredPredRegImmPred",
        "ISETPfxu_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Imm_ISETP", "Imm_ISETP_U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.IsetpfxuPipePredPredRegImmPred(dstPredicate, nPd, srcA, uImm, pa, modifiers, guard))
        case _ => None
      }
  }
  object IsetpfxuPipePredPredRegRegPred extends Definition(
        "IsetpfxuPipePredPredRegRegPred",
        "ISETPfxu_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("ISETP", "ISETP_U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.IsetpfxuPipePredPredRegRegPred(dstPredicate, nPd, srcA, srcB, pa, modifiers, guard))
        case _ => None
      }
  }
  object IsetpfxuPipePredRegConst extends Definition(
        "IsetpfxuPipePredRegConst",
        "ISETPfxu_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("NoBop_Const_ISETP", "NoBop_Const_ISETP_U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.IsetpfxuPipePredRegConst(dstPredicate, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object IsetpfxuPipePredRegImm extends Definition(
        "IsetpfxuPipePredRegImm",
        "ISETPfxu_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate)
        ),
        Vec("NoBop_Imm_ISETP", "NoBop_Imm_ISETP_U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.IsetpfxuPipePredRegImm(dstPredicate, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object IsetpfxuPipePredRegReg extends Definition(
        "IsetpfxuPipePredRegReg",
        "ISETPfxu_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register)
        ),
        Vec("NoBop_ISETP", "NoBop_ISETP_U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.IsetpfxuPipePredRegReg(dstPredicate, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object IsetpintPipePredPredRegAddressImmPred extends Definition(
        "IsetpintPipePredPredRegAddressImmPred",
        "ISETPint_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("isetp__RCxR_RCxR", "isetp__RCxR_RCxR_noEX")
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
          Some(Instruction.IsetpintPipePredPredRegAddressImmPred(pu, pv, srcA, srcB, sb_offset, pp, modifiers, guard))
        case _ => None
      }
  }
  object IsetpintPipePredPredRegAddressImmPredPred extends Definition(
        "IsetpintPipePredPredRegAddressImmPredPred",
        "ISETPint_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate),
          Slot("pr", OperandKind.Predicate)
        ),
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
            ) => Some(Instruction.IsetpintPipePredPredRegAddressImmPredPred(
            pu,
            pv,
            srcA,
            srcB,
            sb_offset,
            pp,
            pr,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object IsetpintPipePredPredRegConstPred extends Definition(
        "IsetpintPipePredPredRegConstPred",
        "ISETPint_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("isetp__RCR_RC", "isetp__RCR_RCR", "isetp__RCR_RCR_noEX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.IsetpintPipePredPredRegConstPred(pu, pv, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object IsetpintPipePredPredRegConstPredPred extends Definition(
        "IsetpintPipePredPredRegConstPredPred",
        "ISETPint_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("pp", OperandKind.Predicate),
          Slot("pr", OperandKind.Predicate)
        ),
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
            ) => Some(Instruction.IsetpintPipePredPredRegConstPredPred(pu, pv, srcA, sb, pp, pr, modifiers, guard))
        case _ => None
      }
  }
  object IsetpintPipePredPredRegImmPred extends Definition(
        "IsetpintPipePredPredRegImmPred",
        "ISETPint_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("isetp_64__RsIR_RIR_noEX", "isetp__RsIR_RI", "isetp__RsIR_RIR", "isetp__RsIR_RIR_noEX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.IsetpintPipePredPredRegImmPred(pu, pv, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object IsetpintPipePredPredRegImmPredPred extends Definition(
        "IsetpintPipePredPredRegImmPredPred",
        "ISETPint_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate),
          Slot("pr", OperandKind.Predicate)
        ),
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
            ) => Some(Instruction.IsetpintPipePredPredRegImmPredPred(pu, pv, srcA, sb, pp, pr, modifiers, guard))
        case _ => None
      }
  }
  object IsetpintPipePredPredRegRegPred extends Definition(
        "IsetpintPipePredPredRegRegPred",
        "ISETPint_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec(
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
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.IsetpintPipePredPredRegRegPred(pu, pv, srcA, srcB, pp, modifiers, guard))
        case _ => None
      }
  }
  object IsetpintPipePredPredRegRegPredPred extends Definition(
        "IsetpintPipePredPredRegRegPredPred",
        "ISETPint_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pp", OperandKind.Predicate),
          Slot("pr", OperandKind.Predicate)
        ),
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
            ) => Some(Instruction.IsetpintPipePredPredRegRegPredPred(pu, pv, srcA, srcB, pp, pr, modifiers, guard))
        case _ => None
      }
  }
  object IsetpintPipePredRegAddressImm extends Definition(
        "IsetpintPipePredRegAddressImm",
        "ISETPint_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("isetp_simple__RCxR_RCxR", "isetp_simple__RCxR_RCxR_noEX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.IsetpintPipePredRegAddressImm(pu, srcA, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object IsetpintPipePredRegAddressImmPred extends Definition(
        "IsetpintPipePredRegAddressImmPred",
        "ISETPint_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("pr", OperandKind.Predicate)
        ),
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
            ) => Some(Instruction.IsetpintPipePredRegAddressImmPred(pu, srcA, srcB, sb_offset, pr, modifiers, guard))
        case _ => None
      }
  }
  object IsetpintPipePredRegConst extends Definition(
        "IsetpintPipePredRegConst",
        "ISETPint_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank)
        ),
        Vec("isetp_simple__RCR_RC", "isetp_simple__RCR_RCR", "isetp_simple__RCR_RCR_noEX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb)
            ) => Some(Instruction.IsetpintPipePredRegConst(pu, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object IsetpintPipePredRegConstPred extends Definition(
        "IsetpintPipePredRegConstPred",
        "ISETPint_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("pr", OperandKind.Predicate)
        ),
        Vec("isetp_simple__RCR_RC", "isetp_simple__RCR_RCR", "isetp_simple__RCR_RCR_EX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Predicate(pr)
            ) => Some(Instruction.IsetpintPipePredRegConstPred(pu, srcA, sb, pr, modifiers, guard))
        case _ => None
      }
  }
  object IsetpintPipePredRegImm extends Definition(
        "IsetpintPipePredRegImm",
        "ISETPint_pipe",
        Vec(Slot("pu", OperandKind.Predicate), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec(
          "isetp_64_simple__RsIR_RIR_noEX",
          "isetp_simple__RsIR_RI",
          "isetp_simple__RsIR_RIR",
          "isetp_simple__RsIR_RIR_noEX"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.IsetpintPipePredRegImm(pu, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object IsetpintPipePredRegImmPred extends Definition(
        "IsetpintPipePredRegImmPred",
        "ISETPint_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("pr", OperandKind.Predicate)
        ),
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
            ) => Some(Instruction.IsetpintPipePredRegImmPred(pu, srcA, sb, pr, modifiers, guard))
        case _ => None
      }
  }
  object IsetpintPipePredRegReg extends Definition(
        "IsetpintPipePredRegReg",
        "ISETPint_pipe",
        Vec(Slot("pu", OperandKind.Predicate), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec(
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
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.IsetpintPipePredRegReg(pu, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object IsetpintPipePredRegRegPred extends Definition(
        "IsetpintPipePredRegRegPred",
        "ISETPint_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pr", OperandKind.Predicate)
        ),
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
            ) => Some(Instruction.IsetpintPipePredRegRegPred(pu, srcA, srcB, pr, modifiers, guard))
        case _ => None
      }
  }
  object IsetpxluPipePredPredRegConstPred extends Definition(
        "IsetpxluPipePredPredRegConstPred",
        "ISETPxlu_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Const ISETP", "Const ISETP U")
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
          Some(Instruction.IsetpxluPipePredPredRegConstPred(dstPredicate, nPd, srcA, srcConst, pa, modifiers, guard))
        case _ => None
      }
  }
  object IsetpxluPipePredPredRegImmPred extends Definition(
        "IsetpxluPipePredPredRegImmPred",
        "ISETPxlu_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Imm ISETP", "Imm ISETP U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.IsetpxluPipePredPredRegImmPred(dstPredicate, nPd, srcA, uImm, pa, modifiers, guard))
        case _ => None
      }
  }
  object IsetpxluPipePredPredRegRegPred extends Definition(
        "IsetpxluPipePredPredRegRegPred",
        "ISETPxlu_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("ISETP", "ISETP U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.IsetpxluPipePredPredRegRegPred(dstPredicate, nPd, srcA, srcB, pa, modifiers, guard))
        case _ => None
      }
  }
  object IsetpxluPipePredRegConst extends Definition(
        "IsetpxluPipePredRegConst",
        "ISETPxlu_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("NoBop Const ISETP", "NoBop Const ISETP U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.IsetpxluPipePredRegConst(dstPredicate, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object IsetpxluPipePredRegImm extends Definition(
        "IsetpxluPipePredRegImm",
        "ISETPxlu_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate)
        ),
        Vec("NoBop Imm ISETP", "NoBop Imm ISETP U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.IsetpxluPipePredRegImm(dstPredicate, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object IsetpxluPipePredRegReg extends Definition(
        "IsetpxluPipePredRegReg",
        "ISETPxlu_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register)
        ),
        Vec("NoBop ISETP", "NoBop ISETP U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.IsetpxluPipePredRegReg(dstPredicate, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object IsetfauPipeRegRegConst extends Definition(
        "IsetfauPipeRegRegConst",
        "ISETfau_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("NoBop Const ISET", "NoBop Const ISET U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.IsetfauPipeRegRegConst(dst, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object IsetfauPipeRegRegConstPred extends Definition(
        "IsetfauPipeRegRegConstPred",
        "ISETfau_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Const ISET", "Const ISET U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.IsetfauPipeRegRegConstPred(dst, srcA, srcConst, pa, modifiers, guard))
        case _ => None
      }
  }
  object IsetfauPipeRegRegImm extends Definition(
        "IsetfauPipeRegRegImm",
        "ISETfau_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("NoBop Imm ISET", "NoBop Imm ISET U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.IsetfauPipeRegRegImm(dst, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object IsetfauPipeRegRegImmPred extends Definition(
        "IsetfauPipeRegRegImmPred",
        "ISETfau_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Imm ISET", "Imm ISET U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.IsetfauPipeRegRegImmPred(dst, srcA, uImm, pa, modifiers, guard))
        case _ => None
      }
  }
  object IsetfauPipeRegRegReg extends Definition(
        "IsetfauPipeRegRegReg",
        "ISETfau_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("NoBop ISET", "NoBop ISET U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.IsetfauPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object IsetfauPipeRegRegRegPred extends Definition(
        "IsetfauPipeRegRegRegPred",
        "ISETfau_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("ISET", "ISET U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.IsetfauPipeRegRegRegPred(dst, srcA, srcB, pa, modifiers, guard))
        case _ => None
      }
  }
  object IsetfxuPipeRegRegConst extends Definition(
        "IsetfxuPipeRegRegConst",
        "ISETfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("NoBop_Const_ISET", "NoBop_Const_ISET_U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.IsetfxuPipeRegRegConst(dst, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object IsetfxuPipeRegRegConstPred extends Definition(
        "IsetfxuPipeRegRegConstPred",
        "ISETfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Const_ISET", "Const_ISET_U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.IsetfxuPipeRegRegConstPred(dst, srcA, srcConst, pa, modifiers, guard))
        case _ => None
      }
  }
  object IsetfxuPipeRegRegImm extends Definition(
        "IsetfxuPipeRegRegImm",
        "ISETfxu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("NoBop_Imm_ISET", "NoBop_Imm_ISET_U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.IsetfxuPipeRegRegImm(dst, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object IsetfxuPipeRegRegImmPred extends Definition(
        "IsetfxuPipeRegRegImmPred",
        "ISETfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Imm_ISET", "Imm_ISET_U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.IsetfxuPipeRegRegImmPred(dst, srcA, uImm, pa, modifiers, guard))
        case _ => None
      }
  }
  object IsetfxuPipeRegRegReg extends Definition(
        "IsetfxuPipeRegRegReg",
        "ISETfxu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("NoBop_ISET", "NoBop_ISET_U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.IsetfxuPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object IsetfxuPipeRegRegRegPred extends Definition(
        "IsetfxuPipeRegRegRegPred",
        "ISETfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("ISET", "ISET_U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.IsetfxuPipeRegRegRegPred(dst, srcA, srcB, pa, modifiers, guard))
        case _ => None
      }
  }
  object IsetxluPipeRegRegConst extends Definition(
        "IsetxluPipeRegRegConst",
        "ISETxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("NoBop Const ISET", "NoBop Const ISET U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.IsetxluPipeRegRegConst(dst, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object IsetxluPipeRegRegConstPred extends Definition(
        "IsetxluPipeRegRegConstPred",
        "ISETxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Const ISET", "Const ISET U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.IsetxluPipeRegRegConstPred(dst, srcA, srcConst, pa, modifiers, guard))
        case _ => None
      }
  }
  object IsetxluPipeRegRegImm extends Definition(
        "IsetxluPipeRegRegImm",
        "ISETxlu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("NoBop Imm ISET", "NoBop Imm ISET U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.IsetxluPipeRegRegImm(dst, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object IsetxluPipeRegRegImmPred extends Definition(
        "IsetxluPipeRegRegImmPred",
        "ISETxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Imm ISET", "Imm ISET U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.IsetxluPipeRegRegImmPred(dst, srcA, uImm, pa, modifiers, guard))
        case _ => None
      }
  }
  object IsetxluPipeRegRegReg extends Definition(
        "IsetxluPipeRegRegReg",
        "ISETxlu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("NoBop ISET", "NoBop ISET U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.IsetxluPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object IsetxluPipeRegRegRegPred extends Definition(
        "IsetxluPipeRegRegRegPred",
        "ISETxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("ISET", "ISET U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.IsetxluPipeRegRegRegPred(dst, srcA, srcB, pa, modifiers, guard))
        case _ => None
      }
  }
  object IsubRegConstReg extends Definition(
        "IsubRegConstReg",
        "ISUB",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("srcA", OperandKind.Register)
        ),
        Vec("Const ISUB1")
      ) {
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
  object IsubRegImmReg extends Definition(
        "IsubRegImmReg",
        "ISUB",
        Vec(Slot("dst", OperandKind.Register), Slot("uImm", OperandKind.Immediate), Slot("srcA", OperandKind.Register)),
        Vec("Imm ISUB1")
      ) {
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
  object IsubRegRegConst extends Definition(
        "IsubRegRegConst",
        "ISUB",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const ISUB")
      ) {
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
  object IsubRegRegImm extends Definition(
        "IsubRegRegImm",
        "ISUB",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("Imm ISUB")
      ) {
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
  object IsubRegRegReg extends Definition(
        "IsubRegRegReg",
        "ISUB",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("ISUB")
      ) {
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
  object IsubfauPipeRegConstReg extends Definition(
        "IsubfauPipeRegConstReg",
        "ISUBfau_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("srcA", OperandKind.Register)
        ),
        Vec("Const ISUB1")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcA)
            ) => Some(Instruction.IsubfauPipeRegConstReg(dst, srcConst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object IsubfauPipeRegImmReg extends Definition(
        "IsubfauPipeRegImmReg",
        "ISUBfau_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("uImm", OperandKind.Immediate), Slot("srcA", OperandKind.Register)),
        Vec("Imm ISUB1")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcA)
            ) => Some(Instruction.IsubfauPipeRegImmReg(dst, uImm, srcA, modifiers, guard))
        case _ => None
      }
  }
  object IsubfauPipeRegRegConst extends Definition(
        "IsubfauPipeRegRegConst",
        "ISUBfau_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const ISUB")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.IsubfauPipeRegRegConst(dst, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object IsubfauPipeRegRegImm extends Definition(
        "IsubfauPipeRegRegImm",
        "ISUBfau_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("Imm ISUB")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.IsubfauPipeRegRegImm(dst, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object IsubfauPipeRegRegReg extends Definition(
        "IsubfauPipeRegRegReg",
        "ISUBfau_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("ISUB")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.IsubfauPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object IsubxluPipeRegConstReg extends Definition(
        "IsubxluPipeRegConstReg",
        "ISUBxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("srcA", OperandKind.Register)
        ),
        Vec("Const ISUB1")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcA)
            ) => Some(Instruction.IsubxluPipeRegConstReg(dst, srcConst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object IsubxluPipeRegImmReg extends Definition(
        "IsubxluPipeRegImmReg",
        "ISUBxlu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("uImm", OperandKind.Immediate), Slot("srcA", OperandKind.Register)),
        Vec("Imm ISUB1")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcA)
            ) => Some(Instruction.IsubxluPipeRegImmReg(dst, uImm, srcA, modifiers, guard))
        case _ => None
      }
  }
  object IsubxluPipeRegRegConst extends Definition(
        "IsubxluPipeRegRegConst",
        "ISUBxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const ISUB")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.IsubxluPipeRegRegConst(dst, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object IsubxluPipeRegRegImm extends Definition(
        "IsubxluPipeRegRegImm",
        "ISUBxlu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("Imm ISUB")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.IsubxluPipeRegRegImm(dst, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object IsubxluPipeRegRegReg extends Definition(
        "IsubxluPipeRegRegReg",
        "ISUBxlu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("ISUB")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.IsubxluPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object JcalConst extends Definition(
        "JcalConst",
        "JCAL",
        Vec(Slot("srcConst", OperandKind.ConstantBank)),
        Vec("JCAL c", "JCAL_c")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(srcConst)) => Some(Instruction.JcalConst(srcConst, modifiers, guard))
        case _ => None
      }
  }
  object JcalImm extends Definition("JcalImm", "JCAL", Vec(Slot("sImm", OperandKind.Immediate)), Vec("JCAL")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sImm)) => Some(Instruction.JcalImm(sImm, modifiers, guard))
        case _ => None
      }
  }
  object JcalbruPipeConst extends Definition(
        "JcalbruPipeConst",
        "JCALbru_pipe",
        Vec(Slot("srcConst", OperandKind.ConstantBank)),
        Vec("JCAL c", "JCAL_c")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(srcConst)) =>
          Some(Instruction.JcalbruPipeConst(srcConst, modifiers, guard))
        case _ => None
      }
  }
  object JcalbruPipeImm
      extends Definition("JcalbruPipeImm", "JCALbru_pipe", Vec(Slot("sImm", OperandKind.Immediate)), Vec("JCAL")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sImm)) => Some(Instruction.JcalbruPipeImm(sImm, modifiers, guard))
        case _ => None
      }
  }
  object JmpConst extends Definition(
        "JmpConst",
        "JMP",
        Vec(Slot("sa", OperandKind.ConstantBank)),
        Vec("JMP_c", "jmp_const_", "jmp_const__CONV_DIV", "jmp_const__U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(sa)) => Some(Instruction.JmpConst(sa, modifiers, guard))
        case _ => None
      }
  }
  object JmpImm extends Definition(
        "JmpImm",
        "JMP",
        Vec(Slot("sa", OperandKind.Immediate)),
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
        Vec(Slot("pp", OperandKind.Predicate), Slot("sa", OperandKind.ConstantBank)),
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
        Vec(Slot("pp", OperandKind.Predicate), Slot("sa", OperandKind.Immediate)),
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
  object JmpPredPredConst extends Definition(
        "JmpPredPredConst",
        "JMP",
        Vec(
          Slot("pp", OperandKind.Predicate),
          Slot("uPq", OperandKind.Predicate),
          Slot("sa", OperandKind.ConstantBank)
        ),
        Vec("jmp_const_uniform_pred_")
      ) {
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
  object JmpPredPredImm extends Definition(
        "JmpPredPredImm",
        "JMP",
        Vec(Slot("pp", OperandKind.Predicate), Slot("uPq", OperandKind.Predicate), Slot("sa", OperandKind.Immediate)),
        Vec("jmp_imm_uniform_pred_", "jmp_imm_uniform_pred_rel_")
      ) {
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
  object JmpPredRegConst extends Definition(
        "JmpPredRegConst",
        "JMP",
        Vec(
          Slot("pp", OperandKind.Predicate),
          Slot("srcB", OperandKind.Register),
          Slot("sa", OperandKind.ConstantBank)
        ),
        Vec("jmp_const_uniform_")
      ) {
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
  object JmpPredRegImm extends Definition(
        "JmpPredRegImm",
        "JMP",
        Vec(Slot("pp", OperandKind.Predicate), Slot("srcB", OperandKind.Register), Slot("sa", OperandKind.Immediate)),
        Vec("jmp_imm_uniform_", "jmp_imm_uniform_rel_")
      ) {
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
  object JmpRegConst extends Definition(
        "JmpRegConst",
        "JMP",
        Vec(Slot("srcB", OperandKind.Register), Slot("sa", OperandKind.ConstantBank)),
        Vec("jmp_const_uniform_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcB), InstructionOperand.ConstantBank(sa)) =>
          Some(Instruction.JmpRegConst(srcB, sa, modifiers, guard))
        case _ => None
      }
  }
  object JmpRegImm extends Definition(
        "JmpRegImm",
        "JMP",
        Vec(Slot("srcB", OperandKind.Register), Slot("sa", OperandKind.Immediate)),
        Vec("jmp_imm_uniform_", "jmp_imm_uniform_rel_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcB), InstructionOperand.Immediate(sa)) =>
          Some(Instruction.JmpRegImm(srcB, sa, modifiers, guard))
        case _ => None
      }
  }
  object JmpSymbolConst extends Definition(
        "JmpSymbolConst",
        "JMP",
        Vec(Slot("testCC", OperandKind.Symbol), Slot("srcConst", OperandKind.ConstantBank)),
        Vec("JMP_c")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Symbol(testCC), InstructionOperand.ConstantBank(srcConst)) =>
          Some(Instruction.JmpSymbolConst(testCC, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object JmpSymbolImm extends Definition(
        "JmpSymbolImm",
        "JMP",
        Vec(Slot("testCC", OperandKind.Symbol), Slot("sImm", OperandKind.Immediate)),
        Vec("JMP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Symbol(testCC), InstructionOperand.Immediate(sImm)) =>
          Some(Instruction.JmpSymbolImm(testCC, sImm, modifiers, guard))
        case _ => None
      }
  }
  object JmpSymbolImmImm extends Definition(
        "JmpSymbolImmImm",
        "JMP",
        Vec(
          Slot("srcConst", OperandKind.Symbol),
          Slot("constBank", OperandKind.Immediate),
          Slot("immConstOffset", OperandKind.Immediate)
        ),
        Vec("JMP c")
      ) {
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
  object JmpSymbolSymbolImmImm extends Definition(
        "JmpSymbolSymbolImmImm",
        "JMP",
        Vec(
          Slot("testCC", OperandKind.Symbol),
          Slot("srcConst", OperandKind.Symbol),
          Slot("constBank", OperandKind.Immediate),
          Slot("immConstOffset", OperandKind.Immediate)
        ),
        Vec("JMP c")
      ) {
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
  object JmpbruPipeConst extends Definition(
        "JmpbruPipeConst",
        "JMPbru_pipe",
        Vec(Slot("srcConst", OperandKind.ConstantBank)),
        Vec("JMP_c")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(srcConst)) =>
          Some(Instruction.JmpbruPipeConst(srcConst, modifiers, guard))
        case _ => None
      }
  }
  object JmpbruPipeImm
      extends Definition("JmpbruPipeImm", "JMPbru_pipe", Vec(Slot("sImm", OperandKind.Immediate)), Vec("JMP")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sImm)) => Some(Instruction.JmpbruPipeImm(sImm, modifiers, guard))
        case _ => None
      }
  }
  object JmpbruPipeSymbolConst extends Definition(
        "JmpbruPipeSymbolConst",
        "JMPbru_pipe",
        Vec(Slot("testCC", OperandKind.Symbol), Slot("srcConst", OperandKind.ConstantBank)),
        Vec("JMP_c")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Symbol(testCC), InstructionOperand.ConstantBank(srcConst)) =>
          Some(Instruction.JmpbruPipeSymbolConst(testCC, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object JmpbruPipeSymbolImm extends Definition(
        "JmpbruPipeSymbolImm",
        "JMPbru_pipe",
        Vec(Slot("testCC", OperandKind.Symbol), Slot("sImm", OperandKind.Immediate)),
        Vec("JMP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Symbol(testCC), InstructionOperand.Immediate(sImm)) =>
          Some(Instruction.JmpbruPipeSymbolImm(testCC, sImm, modifiers, guard))
        case _ => None
      }
  }
  object JmpbruPipeSymbolImmImm extends Definition(
        "JmpbruPipeSymbolImmImm",
        "JMPbru_pipe",
        Vec(
          Slot("srcConst", OperandKind.Symbol),
          Slot("constBank", OperandKind.Immediate),
          Slot("immConstOffset", OperandKind.Immediate)
        ),
        Vec("JMP c")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Symbol(srcConst),
              InstructionOperand.Immediate(constBank),
              InstructionOperand.Immediate(immConstOffset)
            ) => Some(Instruction.JmpbruPipeSymbolImmImm(srcConst, constBank, immConstOffset, modifiers, guard))
        case _ => None
      }
  }
  object JmpbruPipeSymbolSymbolImmImm extends Definition(
        "JmpbruPipeSymbolSymbolImmImm",
        "JMPbru_pipe",
        Vec(
          Slot("testCC", OperandKind.Symbol),
          Slot("srcConst", OperandKind.Symbol),
          Slot("constBank", OperandKind.Immediate),
          Slot("immConstOffset", OperandKind.Immediate)
        ),
        Vec("JMP c")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Symbol(testCC),
              InstructionOperand.Symbol(srcConst),
              InstructionOperand.Immediate(constBank),
              InstructionOperand.Immediate(immConstOffset)
            ) =>
          Some(Instruction.JmpbruPipeSymbolSymbolImmImm(testCC, srcConst, constBank, immConstOffset, modifiers, guard))
        case _ => None
      }
  }
  object JmpcbuPipeConst extends Definition(
        "JmpcbuPipeConst",
        "JMPcbu_pipe",
        Vec(Slot("sa", OperandKind.ConstantBank)),
        Vec("jmp_const_", "jmp_const__CONV_DIV", "jmp_const__U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(sa)) => Some(Instruction.JmpcbuPipeConst(sa, modifiers, guard))
        case _ => None
      }
  }
  object JmpcbuPipeImm extends Definition(
        "JmpcbuPipeImm",
        "JMPcbu_pipe",
        Vec(Slot("sa", OperandKind.Immediate)),
        Vec("jmp_imm_", "jmp_imm__CONV_DIV", "jmp_imm__U", "jmp_imm_rel_", "jmp_imm_rel__CONV_DIV", "jmp_imm_rel__U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sa)) => Some(Instruction.JmpcbuPipeImm(sa, modifiers, guard))
        case _ => None
      }
  }
  object JmpcbuPipePredConst extends Definition(
        "JmpcbuPipePredConst",
        "JMPcbu_pipe",
        Vec(Slot("pp", OperandKind.Predicate), Slot("sa", OperandKind.ConstantBank)),
        Vec("jmp_const_", "jmp_const__CONV_DIV", "jmp_const__U", "jmp_const_uniform_pred_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.ConstantBank(sa)) =>
          Some(Instruction.JmpcbuPipePredConst(pp, sa, modifiers, guard))
        case _ => None
      }
  }
  object JmpcbuPipePredImm extends Definition(
        "JmpcbuPipePredImm",
        "JMPcbu_pipe",
        Vec(Slot("pp", OperandKind.Predicate), Slot("sa", OperandKind.Immediate)),
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
          Some(Instruction.JmpcbuPipePredImm(pp, sa, modifiers, guard))
        case _ => None
      }
  }
  object JmpcbuPipePredPredConst extends Definition(
        "JmpcbuPipePredPredConst",
        "JMPcbu_pipe",
        Vec(
          Slot("pp", OperandKind.Predicate),
          Slot("uPq", OperandKind.Predicate),
          Slot("sa", OperandKind.ConstantBank)
        ),
        Vec("jmp_const_uniform_pred_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pp),
              InstructionOperand.Predicate(uPq),
              InstructionOperand.ConstantBank(sa)
            ) => Some(Instruction.JmpcbuPipePredPredConst(pp, uPq, sa, modifiers, guard))
        case _ => None
      }
  }
  object JmpcbuPipePredPredImm extends Definition(
        "JmpcbuPipePredPredImm",
        "JMPcbu_pipe",
        Vec(Slot("pp", OperandKind.Predicate), Slot("uPq", OperandKind.Predicate), Slot("sa", OperandKind.Immediate)),
        Vec("jmp_imm_uniform_pred_", "jmp_imm_uniform_pred_rel_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pp),
              InstructionOperand.Predicate(uPq),
              InstructionOperand.Immediate(sa)
            ) => Some(Instruction.JmpcbuPipePredPredImm(pp, uPq, sa, modifiers, guard))
        case _ => None
      }
  }
  object JmpcbuPipePredRegConst extends Definition(
        "JmpcbuPipePredRegConst",
        "JMPcbu_pipe",
        Vec(
          Slot("pp", OperandKind.Predicate),
          Slot("srcB", OperandKind.Register),
          Slot("sa", OperandKind.ConstantBank)
        ),
        Vec("jmp_const_uniform_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pp),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(sa)
            ) => Some(Instruction.JmpcbuPipePredRegConst(pp, srcB, sa, modifiers, guard))
        case _ => None
      }
  }
  object JmpcbuPipePredRegImm extends Definition(
        "JmpcbuPipePredRegImm",
        "JMPcbu_pipe",
        Vec(Slot("pp", OperandKind.Predicate), Slot("srcB", OperandKind.Register), Slot("sa", OperandKind.Immediate)),
        Vec("jmp_imm_uniform_", "jmp_imm_uniform_rel_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pp),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sa)
            ) => Some(Instruction.JmpcbuPipePredRegImm(pp, srcB, sa, modifiers, guard))
        case _ => None
      }
  }
  object JmpcbuPipeRegConst extends Definition(
        "JmpcbuPipeRegConst",
        "JMPcbu_pipe",
        Vec(Slot("srcB", OperandKind.Register), Slot("sa", OperandKind.ConstantBank)),
        Vec("jmp_const_uniform_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcB), InstructionOperand.ConstantBank(sa)) =>
          Some(Instruction.JmpcbuPipeRegConst(srcB, sa, modifiers, guard))
        case _ => None
      }
  }
  object JmpcbuPipeRegImm extends Definition(
        "JmpcbuPipeRegImm",
        "JMPcbu_pipe",
        Vec(Slot("srcB", OperandKind.Register), Slot("sa", OperandKind.Immediate)),
        Vec("jmp_imm_uniform_", "jmp_imm_uniform_rel_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcB), InstructionOperand.Immediate(sa)) =>
          Some(Instruction.JmpcbuPipeRegImm(srcB, sa, modifiers, guard))
        case _ => None
      }
  }
  object JmxConst extends Definition("JmxConst", "JMX", Vec(Slot("srcConst", OperandKind.ConstantBank)), Vec("JMX_c")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(srcConst)) => Some(Instruction.JmxConst(srcConst, modifiers, guard))
        case _ => None
      }
  }
  object JmxPredReg extends Definition(
        "JmxPredReg",
        "JMX",
        Vec(Slot("pp", OperandKind.Predicate), Slot("srcA", OperandKind.Register)),
        Vec("jmx_", "jmx_rel_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.Register(srcA)) =>
          Some(Instruction.JmxPredReg(pp, srcA, modifiers, guard))
        case _ => None
      }
  }
  object JmxPredRegImm extends Definition(
        "JmxPredRegImm",
        "JMX",
        Vec(
          Slot("pp", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("ra_offset", OperandKind.Immediate)
        ),
        Vec("jmx_", "jmx_rel_")
      ) {
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
  object JmxReg
      extends Definition("JmxReg", "JMX", Vec(Slot("srcA", OperandKind.Register)), Vec("JMX", "jmx_", "jmx_rel_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA)) => Some(Instruction.JmxReg(srcA, modifiers, guard))
        case _ => None
      }
  }
  object JmxRegImm extends Definition(
        "JmxRegImm",
        "JMX",
        Vec(Slot("srcA", OperandKind.Register), Slot("ra_offset", OperandKind.Immediate)),
        Vec("JMX", "jmx_", "jmx_rel_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA), InstructionOperand.Immediate(ra_offset)) =>
          Some(Instruction.JmxRegImm(srcA, ra_offset, modifiers, guard))
        case _ => None
      }
  }
  object JmxSymbolConst extends Definition(
        "JmxSymbolConst",
        "JMX",
        Vec(Slot("testCC", OperandKind.Symbol), Slot("srcConst", OperandKind.ConstantBank)),
        Vec("JMX_c")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Symbol(testCC), InstructionOperand.ConstantBank(srcConst)) =>
          Some(Instruction.JmxSymbolConst(testCC, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object JmxSymbolImmAddress extends Definition(
        "JmxSymbolImmAddress",
        "JMX",
        Vec(
          Slot("srcConst", OperandKind.Symbol),
          Slot("constBank", OperandKind.Immediate),
          Slot("srcA", OperandKind.Address)
        ),
        Vec("JMX c")
      ) {
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
  object JmxSymbolReg extends Definition(
        "JmxSymbolReg",
        "JMX",
        Vec(Slot("testCC", OperandKind.Symbol), Slot("srcA", OperandKind.Register)),
        Vec("JMX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Symbol(testCC), InstructionOperand.Register(srcA)) =>
          Some(Instruction.JmxSymbolReg(testCC, srcA, modifiers, guard))
        case _ => None
      }
  }
  object JmxSymbolRegImm extends Definition(
        "JmxSymbolRegImm",
        "JMX",
        Vec(
          Slot("testCC", OperandKind.Symbol),
          Slot("srcA", OperandKind.Register),
          Slot("sImm", OperandKind.Immediate)
        ),
        Vec("JMX")
      ) {
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
  object JmxSymbolSymbolImmAddress extends Definition(
        "JmxSymbolSymbolImmAddress",
        "JMX",
        Vec(
          Slot("testCC", OperandKind.Symbol),
          Slot("srcConst", OperandKind.Symbol),
          Slot("constBank", OperandKind.Immediate),
          Slot("srcA", OperandKind.Address)
        ),
        Vec("JMX c")
      ) {
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
  object JmxuPredReg extends Definition(
        "JmxuPredReg",
        "JMXU",
        Vec(Slot("pp", OperandKind.Predicate), Slot("srcA", OperandKind.Register)),
        Vec("jmxu_", "jmxu_rel_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.Register(srcA)) =>
          Some(Instruction.JmxuPredReg(pp, srcA, modifiers, guard))
        case _ => None
      }
  }
  object JmxuPredRegImm extends Definition(
        "JmxuPredRegImm",
        "JMXU",
        Vec(
          Slot("pp", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("uR_offset", OperandKind.Immediate)
        ),
        Vec("jmxu_", "jmxu_rel_")
      ) {
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
  object JmxuReg
      extends Definition("JmxuReg", "JMXU", Vec(Slot("srcA", OperandKind.Register)), Vec("jmxu_", "jmxu_rel_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA)) => Some(Instruction.JmxuReg(srcA, modifiers, guard))
        case _ => None
      }
  }
  object JmxuRegImm extends Definition(
        "JmxuRegImm",
        "JMXU",
        Vec(Slot("srcA", OperandKind.Register), Slot("uR_offset", OperandKind.Immediate)),
        Vec("jmxu_", "jmxu_rel_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA), InstructionOperand.Immediate(uR_offset)) =>
          Some(Instruction.JmxuRegImm(srcA, uR_offset, modifiers, guard))
        case _ => None
      }
  }
  object JmxucbuPipePredReg extends Definition(
        "JmxucbuPipePredReg",
        "JMXUcbu_pipe",
        Vec(Slot("pp", OperandKind.Predicate), Slot("srcA", OperandKind.Register)),
        Vec("jmxu_", "jmxu_rel_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.Register(srcA)) =>
          Some(Instruction.JmxucbuPipePredReg(pp, srcA, modifiers, guard))
        case _ => None
      }
  }
  object JmxucbuPipePredRegImm extends Definition(
        "JmxucbuPipePredRegImm",
        "JMXUcbu_pipe",
        Vec(
          Slot("pp", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("uR_offset", OperandKind.Immediate)
        ),
        Vec("jmxu_", "jmxu_rel_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pp),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uR_offset)
            ) => Some(Instruction.JmxucbuPipePredRegImm(pp, srcA, uR_offset, modifiers, guard))
        case _ => None
      }
  }
  object JmxucbuPipeReg extends Definition(
        "JmxucbuPipeReg",
        "JMXUcbu_pipe",
        Vec(Slot("srcA", OperandKind.Register)),
        Vec("jmxu_", "jmxu_rel_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA)) => Some(Instruction.JmxucbuPipeReg(srcA, modifiers, guard))
        case _ => None
      }
  }
  object JmxucbuPipeRegImm extends Definition(
        "JmxucbuPipeRegImm",
        "JMXUcbu_pipe",
        Vec(Slot("srcA", OperandKind.Register), Slot("uR_offset", OperandKind.Immediate)),
        Vec("jmxu_", "jmxu_rel_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA), InstructionOperand.Immediate(uR_offset)) =>
          Some(Instruction.JmxucbuPipeRegImm(srcA, uR_offset, modifiers, guard))
        case _ => None
      }
  }
  object JmxaduPipeReg
      extends Definition("JmxaduPipeReg", "JMXadu_pipe", Vec(Slot("srcA", OperandKind.Register)), Vec("JMX")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA)) => Some(Instruction.JmxaduPipeReg(srcA, modifiers, guard))
        case _ => None
      }
  }
  object JmxaduPipeRegImm extends Definition(
        "JmxaduPipeRegImm",
        "JMXadu_pipe",
        Vec(Slot("srcA", OperandKind.Register), Slot("sImm", OperandKind.Immediate)),
        Vec("JMX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA), InstructionOperand.Immediate(sImm)) =>
          Some(Instruction.JmxaduPipeRegImm(srcA, sImm, modifiers, guard))
        case _ => None
      }
  }
  object JmxaduPipeSymbolImmAddress extends Definition(
        "JmxaduPipeSymbolImmAddress",
        "JMXadu_pipe",
        Vec(
          Slot("srcConst", OperandKind.Symbol),
          Slot("constBank", OperandKind.Immediate),
          Slot("srcA", OperandKind.Address)
        ),
        Vec("JMX c")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Symbol(srcConst),
              InstructionOperand.Immediate(constBank),
              InstructionOperand.Address(srcA)
            ) => Some(Instruction.JmxaduPipeSymbolImmAddress(srcConst, constBank, srcA, modifiers, guard))
        case _ => None
      }
  }
  object JmxaduPipeSymbolReg extends Definition(
        "JmxaduPipeSymbolReg",
        "JMXadu_pipe",
        Vec(Slot("testCC", OperandKind.Symbol), Slot("srcA", OperandKind.Register)),
        Vec("JMX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Symbol(testCC), InstructionOperand.Register(srcA)) =>
          Some(Instruction.JmxaduPipeSymbolReg(testCC, srcA, modifiers, guard))
        case _ => None
      }
  }
  object JmxaduPipeSymbolRegImm extends Definition(
        "JmxaduPipeSymbolRegImm",
        "JMXadu_pipe",
        Vec(
          Slot("testCC", OperandKind.Symbol),
          Slot("srcA", OperandKind.Register),
          Slot("sImm", OperandKind.Immediate)
        ),
        Vec("JMX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Symbol(testCC),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sImm)
            ) => Some(Instruction.JmxaduPipeSymbolRegImm(testCC, srcA, sImm, modifiers, guard))
        case _ => None
      }
  }
  object JmxaduPipeSymbolSymbolImmAddress extends Definition(
        "JmxaduPipeSymbolSymbolImmAddress",
        "JMXadu_pipe",
        Vec(
          Slot("testCC", OperandKind.Symbol),
          Slot("srcConst", OperandKind.Symbol),
          Slot("constBank", OperandKind.Immediate),
          Slot("srcA", OperandKind.Address)
        ),
        Vec("JMX c")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Symbol(testCC),
              InstructionOperand.Symbol(srcConst),
              InstructionOperand.Immediate(constBank),
              InstructionOperand.Address(srcA)
            ) => Some(Instruction.JmxaduPipeSymbolSymbolImmAddress(testCC, srcConst, constBank, srcA, modifiers, guard))
        case _ => None
      }
  }
  object JmxbruPipeConst extends Definition(
        "JmxbruPipeConst",
        "JMXbru_pipe",
        Vec(Slot("srcConst", OperandKind.ConstantBank)),
        Vec("JMX_c")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(srcConst)) =>
          Some(Instruction.JmxbruPipeConst(srcConst, modifiers, guard))
        case _ => None
      }
  }
  object JmxbruPipeReg
      extends Definition("JmxbruPipeReg", "JMXbru_pipe", Vec(Slot("srcA", OperandKind.Register)), Vec("JMX")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA)) => Some(Instruction.JmxbruPipeReg(srcA, modifiers, guard))
        case _ => None
      }
  }
  object JmxbruPipeRegImm extends Definition(
        "JmxbruPipeRegImm",
        "JMXbru_pipe",
        Vec(Slot("srcA", OperandKind.Register), Slot("sImm", OperandKind.Immediate)),
        Vec("JMX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA), InstructionOperand.Immediate(sImm)) =>
          Some(Instruction.JmxbruPipeRegImm(srcA, sImm, modifiers, guard))
        case _ => None
      }
  }
  object JmxbruPipeSymbolConst extends Definition(
        "JmxbruPipeSymbolConst",
        "JMXbru_pipe",
        Vec(Slot("testCC", OperandKind.Symbol), Slot("srcConst", OperandKind.ConstantBank)),
        Vec("JMX_c")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Symbol(testCC), InstructionOperand.ConstantBank(srcConst)) =>
          Some(Instruction.JmxbruPipeSymbolConst(testCC, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object JmxbruPipeSymbolImmAddress extends Definition(
        "JmxbruPipeSymbolImmAddress",
        "JMXbru_pipe",
        Vec(
          Slot("srcConst", OperandKind.Symbol),
          Slot("constBank", OperandKind.Immediate),
          Slot("srcA", OperandKind.Address)
        ),
        Vec("JMX c")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Symbol(srcConst),
              InstructionOperand.Immediate(constBank),
              InstructionOperand.Address(srcA)
            ) => Some(Instruction.JmxbruPipeSymbolImmAddress(srcConst, constBank, srcA, modifiers, guard))
        case _ => None
      }
  }
  object JmxbruPipeSymbolReg extends Definition(
        "JmxbruPipeSymbolReg",
        "JMXbru_pipe",
        Vec(Slot("testCC", OperandKind.Symbol), Slot("srcA", OperandKind.Register)),
        Vec("JMX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Symbol(testCC), InstructionOperand.Register(srcA)) =>
          Some(Instruction.JmxbruPipeSymbolReg(testCC, srcA, modifiers, guard))
        case _ => None
      }
  }
  object JmxbruPipeSymbolRegImm extends Definition(
        "JmxbruPipeSymbolRegImm",
        "JMXbru_pipe",
        Vec(
          Slot("testCC", OperandKind.Symbol),
          Slot("srcA", OperandKind.Register),
          Slot("sImm", OperandKind.Immediate)
        ),
        Vec("JMX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Symbol(testCC),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sImm)
            ) => Some(Instruction.JmxbruPipeSymbolRegImm(testCC, srcA, sImm, modifiers, guard))
        case _ => None
      }
  }
  object JmxbruPipeSymbolSymbolImmAddress extends Definition(
        "JmxbruPipeSymbolSymbolImmAddress",
        "JMXbru_pipe",
        Vec(
          Slot("testCC", OperandKind.Symbol),
          Slot("srcConst", OperandKind.Symbol),
          Slot("constBank", OperandKind.Immediate),
          Slot("srcA", OperandKind.Address)
        ),
        Vec("JMX c")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Symbol(testCC),
              InstructionOperand.Symbol(srcConst),
              InstructionOperand.Immediate(constBank),
              InstructionOperand.Address(srcA)
            ) => Some(Instruction.JmxbruPipeSymbolSymbolImmAddress(testCC, srcConst, constBank, srcA, modifiers, guard))
        case _ => None
      }
  }
  object JmxcbuPipePredReg extends Definition(
        "JmxcbuPipePredReg",
        "JMXcbu_pipe",
        Vec(Slot("pp", OperandKind.Predicate), Slot("srcA", OperandKind.Register)),
        Vec("jmx_", "jmx_rel_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.Register(srcA)) =>
          Some(Instruction.JmxcbuPipePredReg(pp, srcA, modifiers, guard))
        case _ => None
      }
  }
  object JmxcbuPipePredRegImm extends Definition(
        "JmxcbuPipePredRegImm",
        "JMXcbu_pipe",
        Vec(
          Slot("pp", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("ra_offset", OperandKind.Immediate)
        ),
        Vec("jmx_", "jmx_rel_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pp),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(ra_offset)
            ) => Some(Instruction.JmxcbuPipePredRegImm(pp, srcA, ra_offset, modifiers, guard))
        case _ => None
      }
  }
  object JmxcbuPipeReg extends Definition(
        "JmxcbuPipeReg",
        "JMXcbu_pipe",
        Vec(Slot("srcA", OperandKind.Register)),
        Vec("jmx_", "jmx_rel_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA)) => Some(Instruction.JmxcbuPipeReg(srcA, modifiers, guard))
        case _ => None
      }
  }
  object JmxcbuPipeRegImm extends Definition(
        "JmxcbuPipeRegImm",
        "JMXcbu_pipe",
        Vec(Slot("srcA", OperandKind.Register), Slot("ra_offset", OperandKind.Immediate)),
        Vec("jmx_", "jmx_rel_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA), InstructionOperand.Immediate(ra_offset)) =>
          Some(Instruction.JmxcbuPipeRegImm(srcA, ra_offset, modifiers, guard))
        case _ => None
      }
  }
  object KilNoOperands extends Definition("KilNoOperands", "KIL", Vec(), Vec("BRK", "KIL")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.KilNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object KillNoOperands extends Definition("KillNoOperands", "KILL", Vec(), Vec("kill_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.KillNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object KillPred extends Definition("KillPred", "KILL", Vec(Slot("pp", OperandKind.Predicate)), Vec("kill_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp)) => Some(Instruction.KillPred(pp, modifiers, guard))
        case _ => None
      }
  }
  object KillcbuPipeNoOperands extends Definition("KillcbuPipeNoOperands", "KILLcbu_pipe", Vec(), Vec("kill_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.KillcbuPipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object KillcbuPipePred
      extends Definition("KillcbuPipePred", "KILLcbu_pipe", Vec(Slot("pp", OperandKind.Predicate)), Vec("kill_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp)) => Some(Instruction.KillcbuPipePred(pp, modifiers, guard))
        case _ => None
      }
  }
  object KilbruPipeNoOperands extends Definition("KilbruPipeNoOperands", "KILbru_pipe", Vec(), Vec("BRK", "KIL")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.KilbruPipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object LdPredRegAddress extends Definition(
        "LdPredRegAddress",
        "LD",
        Vec(Slot("pu", OperandKind.Predicate), Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address)),
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
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("pnz", OperandKind.Predicate)
        ),
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
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address)),
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
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address), Slot("pnz", OperandKind.Predicate)),
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
  object LdcRegAddressAddress extends Definition(
        "LdcRegAddressAddress",
        "LDC",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Address)),
        Vec("ldc_ur__URRzI", "ldc_ur__URnonRzI")
      ) {
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
  val definitions: Vec[Definition] = Vec(
    ImulfmalighterPipeRegRegRegImm,
    ImulfmalighterPipeRegRegRegReg,
    ImulmioPipeRegRegConst,
    ImulmioPipeRegRegImm,
    ImulmioPipeRegRegReg,
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
    IpamioPipeRegAddress,
    IpamioPipeRegAddressImm,
    IpamioPipeRegAddressPred,
    IpamioPipeRegAddressReg,
    IpamioPipeRegAddressRegRegPred,
    IpamioPipeRegImm,
    IpamioPipeRegImmConst,
    IpamioPipeRegImmImm,
    IpamioPipeRegImmPred,
    IpamioPipeRegImmReg,
    IpamioPipeRegImmRegReg,
    IpamioPipeRegImmRegRegPred,
    IpamioPipeRegPredAddress,
    IpamioPipeRegPredAddressImm,
    IpamioPipeRegPredAddressReg,
    IpamioPipeRegPredImm,
    IpamioPipeRegPredImmConst,
    IpamioPipeRegPredImmImm,
    IpamioPipeRegPredImmReg,
    IpasuPipeRegAddress,
    IpasuPipeRegAddressRegReg,
    IsadRegRegConstReg,
    IsadRegRegImmReg,
    IsadRegRegRegConst,
    IsadRegRegRegReg,
    IsadxluPipeRegRegConstReg,
    IsadxluPipeRegRegImmReg,
    IsadxluPipeRegRegRegConst,
    IsadxluPipeRegRegRegReg,
    IsberdRegAddress,
    IsberdmioPipeRegAddress,
    IsbewrAddressReg,
    IsbewrmioPipeAddressReg,
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
    Iscadd32ifxuPipeRegRegImmImm,
    Iscadd32iintPipeRegPredRegImmImm,
    Iscadd32iintPipeRegRegImmImm,
    Iscadd32ixluPipeRegRegImmImm,
    IscaddfxuPipeRegRegConstImm,
    IscaddfxuPipeRegRegImmImm,
    IscaddfxuPipeRegRegRegImm,
    IscaddintPipeRegPredRegAddressImmImm,
    IscaddintPipeRegPredRegConstImm,
    IscaddintPipeRegPredRegImmImm,
    IscaddintPipeRegPredRegRegImm,
    IscaddintPipeRegRegAddressImmImm,
    IscaddintPipeRegRegConstImm,
    IscaddintPipeRegRegImmImm,
    IscaddintPipeRegRegRegImm,
    IscaddxluPipeRegRegConstImm,
    IscaddxluPipeRegRegImmImm,
    IscaddxluPipeRegRegRegImm,
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
    IsetpfauPipePredPredRegConstPred,
    IsetpfauPipePredPredRegImmPred,
    IsetpfauPipePredPredRegRegPred,
    IsetpfauPipePredRegConst,
    IsetpfauPipePredRegImm,
    IsetpfauPipePredRegReg,
    IsetpfxuPipePredPredRegConstPred,
    IsetpfxuPipePredPredRegImmPred,
    IsetpfxuPipePredPredRegRegPred,
    IsetpfxuPipePredRegConst,
    IsetpfxuPipePredRegImm,
    IsetpfxuPipePredRegReg,
    IsetpintPipePredPredRegAddressImmPred,
    IsetpintPipePredPredRegAddressImmPredPred,
    IsetpintPipePredPredRegConstPred,
    IsetpintPipePredPredRegConstPredPred,
    IsetpintPipePredPredRegImmPred,
    IsetpintPipePredPredRegImmPredPred,
    IsetpintPipePredPredRegRegPred,
    IsetpintPipePredPredRegRegPredPred,
    IsetpintPipePredRegAddressImm,
    IsetpintPipePredRegAddressImmPred,
    IsetpintPipePredRegConst,
    IsetpintPipePredRegConstPred,
    IsetpintPipePredRegImm,
    IsetpintPipePredRegImmPred,
    IsetpintPipePredRegReg,
    IsetpintPipePredRegRegPred,
    IsetpxluPipePredPredRegConstPred,
    IsetpxluPipePredPredRegImmPred,
    IsetpxluPipePredPredRegRegPred,
    IsetpxluPipePredRegConst,
    IsetpxluPipePredRegImm,
    IsetpxluPipePredRegReg,
    IsetfauPipeRegRegConst,
    IsetfauPipeRegRegConstPred,
    IsetfauPipeRegRegImm,
    IsetfauPipeRegRegImmPred,
    IsetfauPipeRegRegReg,
    IsetfauPipeRegRegRegPred,
    IsetfxuPipeRegRegConst,
    IsetfxuPipeRegRegConstPred,
    IsetfxuPipeRegRegImm,
    IsetfxuPipeRegRegImmPred,
    IsetfxuPipeRegRegReg,
    IsetfxuPipeRegRegRegPred,
    IsetxluPipeRegRegConst,
    IsetxluPipeRegRegConstPred,
    IsetxluPipeRegRegImm,
    IsetxluPipeRegRegImmPred,
    IsetxluPipeRegRegReg,
    IsetxluPipeRegRegRegPred,
    IsubRegConstReg,
    IsubRegImmReg,
    IsubRegRegConst,
    IsubRegRegImm,
    IsubRegRegReg,
    IsubfauPipeRegConstReg,
    IsubfauPipeRegImmReg,
    IsubfauPipeRegRegConst,
    IsubfauPipeRegRegImm,
    IsubfauPipeRegRegReg,
    IsubxluPipeRegConstReg,
    IsubxluPipeRegImmReg,
    IsubxluPipeRegRegConst,
    IsubxluPipeRegRegImm,
    IsubxluPipeRegRegReg,
    JcalConst,
    JcalImm,
    JcalbruPipeConst,
    JcalbruPipeImm,
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
    JmpbruPipeConst,
    JmpbruPipeImm,
    JmpbruPipeSymbolConst,
    JmpbruPipeSymbolImm,
    JmpbruPipeSymbolImmImm,
    JmpbruPipeSymbolSymbolImmImm,
    JmpcbuPipeConst,
    JmpcbuPipeImm,
    JmpcbuPipePredConst,
    JmpcbuPipePredImm,
    JmpcbuPipePredPredConst,
    JmpcbuPipePredPredImm,
    JmpcbuPipePredRegConst,
    JmpcbuPipePredRegImm,
    JmpcbuPipeRegConst,
    JmpcbuPipeRegImm,
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
    JmxucbuPipePredReg,
    JmxucbuPipePredRegImm,
    JmxucbuPipeReg,
    JmxucbuPipeRegImm,
    JmxaduPipeReg,
    JmxaduPipeRegImm,
    JmxaduPipeSymbolImmAddress,
    JmxaduPipeSymbolReg,
    JmxaduPipeSymbolRegImm,
    JmxaduPipeSymbolSymbolImmAddress,
    JmxbruPipeConst,
    JmxbruPipeReg,
    JmxbruPipeRegImm,
    JmxbruPipeSymbolConst,
    JmxbruPipeSymbolImmAddress,
    JmxbruPipeSymbolReg,
    JmxbruPipeSymbolRegImm,
    JmxbruPipeSymbolSymbolImmAddress,
    JmxcbuPipePredReg,
    JmxcbuPipePredRegImm,
    JmxcbuPipeReg,
    JmxcbuPipeRegImm,
    KilNoOperands,
    KillNoOperands,
    KillPred,
    KillcbuPipeNoOperands,
    KillcbuPipePred,
    KilbruPipeNoOperands,
    LdPredRegAddress,
    LdPredRegAddressPred,
    LdRegAddress,
    LdRegAddressPred,
    LdcRegAddressAddress
  )
}
