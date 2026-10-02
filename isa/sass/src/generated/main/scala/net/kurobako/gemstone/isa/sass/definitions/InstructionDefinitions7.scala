// Generated SASS instruction definitions and checked operand constructors. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.sass
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import Instruction.*
@javax.annotation.processing.Generated(Array("sbt codegen"))
object InstructionDefinitions7 {
  object LdcRegConst extends Definition(
        "LdcRegConst",
        "LDC",
        Vec(Slot("dst", OperandKind.Register), Slot("sa", OperandKind.ConstantBank)),
        Vec("I LDC", "I LDC 64", "I_LDC", "I_LDC_64", "LDC", "LDC 64", "LDC_64", "ldc__RaNonRZ", "ldc__RaRZ")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(sa)) =>
          Some(Instruction.LdcRegConst(dst, sa, modifiers, guard))
        case _ => None
      }
  }
  object LdcuRegAddress extends Definition(
        "LdcuRegAddress",
        "LDCU",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address)),
        Vec("ldcu_ur_offs_optional_upx_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Address(srcA)) =>
          Some(Instruction.LdcuRegAddress(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object LdcuRegAddressAddress extends Definition(
        "LdcuRegAddressAddress",
        "LDCU",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Address)),
        Vec("ldcu_const_RCxR_")
      ) {
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
  object LdcuRegAddressPred extends Definition(
        "LdcuRegAddressPred",
        "LDCU",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address), Slot("uPp", OperandKind.Predicate)),
        Vec("ldcu_ur_offs_")
      ) {
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
  object LdcuRegConst extends Definition(
        "LdcuRegConst",
        "LDCU",
        Vec(Slot("dst", OperandKind.Register), Slot("sa", OperandKind.ConstantBank)),
        Vec("ldcu_const_RCR_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(sa)) =>
          Some(Instruction.LdcuRegConst(dst, sa, modifiers, guard))
        case _ => None
      }
  }
  object LdcuRegRegAddress extends Definition(
        "LdcuRegRegAddress",
        "LDCU",
        Vec(Slot("uRd2", OperandKind.Register), Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address)),
        Vec("ldcu_256_ur_offs_optional_upx_")
      ) {
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
  object LdcuRegRegAddressAddress extends Definition(
        "LdcuRegRegAddressAddress",
        "LDCU",
        Vec(
          Slot("uRd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Address)
        ),
        Vec("ldcu_256_const_RCxR_")
      ) {
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
  object LdcuRegRegAddressAddressImm extends Definition(
        "LdcuRegRegAddressAddressImm",
        "LDCU",
        Vec(
          Slot("uRd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Address),
          Slot("word_mask", OperandKind.Immediate)
        ),
        Vec("ldcu_256_const_RCxR_")
      ) {
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
  object LdcuRegRegAddressImm extends Definition(
        "LdcuRegRegAddressImm",
        "LDCU",
        Vec(
          Slot("uRd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("word_mask", OperandKind.Immediate)
        ),
        Vec("ldcu_256_ur_offs_optional_upx_")
      ) {
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
  object LdcuRegRegAddressImmPred extends Definition(
        "LdcuRegRegAddressImmPred",
        "LDCU",
        Vec(
          Slot("uRd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("word_mask", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ldcu_256_ur_offs_")
      ) {
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
  object LdcuRegRegAddressPred extends Definition(
        "LdcuRegRegAddressPred",
        "LDCU",
        Vec(
          Slot("uRd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ldcu_256_ur_offs_")
      ) {
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
  object LdcuRegRegConst extends Definition(
        "LdcuRegRegConst",
        "LDCU",
        Vec(
          Slot("uRd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("sa", OperandKind.ConstantBank)
        ),
        Vec("ldcu_256_const_RCR_")
      ) {
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
  object LdcuRegRegConstImm extends Definition(
        "LdcuRegRegConstImm",
        "LDCU",
        Vec(
          Slot("uRd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("sa", OperandKind.ConstantBank),
          Slot("word_mask", OperandKind.Immediate)
        ),
        Vec("ldcu_256_const_RCR_")
      ) {
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
  object LdcuudpPipeRegAddress extends Definition(
        "LdcuudpPipeRegAddress",
        "LDCUudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address)),
        Vec("ldcu_ur_offs_optional_upx_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Address(srcA)) =>
          Some(Instruction.LdcuudpPipeRegAddress(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object LdcuudpPipeRegAddressAddress extends Definition(
        "LdcuudpPipeRegAddressAddress",
        "LDCUudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Address)),
        Vec("ldcu_const_RCxR_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Address(srcB)
            ) => Some(Instruction.LdcuudpPipeRegAddressAddress(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object LdcuudpPipeRegAddressPred extends Definition(
        "LdcuudpPipeRegAddressPred",
        "LDCUudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address), Slot("uPp", OperandKind.Predicate)),
        Vec("ldcu_ur_offs_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.LdcuudpPipeRegAddressPred(dst, srcA, uPp, modifiers, guard))
        case _ => None
      }
  }
  object LdcuudpPipeRegConst extends Definition(
        "LdcuudpPipeRegConst",
        "LDCUudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("sa", OperandKind.ConstantBank)),
        Vec("ldcu_const_RCR_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(sa)) =>
          Some(Instruction.LdcuudpPipeRegConst(dst, sa, modifiers, guard))
        case _ => None
      }
  }
  object LdcuudpPipeRegRegAddress extends Definition(
        "LdcuudpPipeRegRegAddress",
        "LDCUudp_pipe",
        Vec(Slot("uRd2", OperandKind.Register), Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address)),
        Vec("ldcu_256_ur_offs_optional_upx_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(uRd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA)
            ) => Some(Instruction.LdcuudpPipeRegRegAddress(uRd2, dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object LdcuudpPipeRegRegAddressAddress extends Definition(
        "LdcuudpPipeRegRegAddressAddress",
        "LDCUudp_pipe",
        Vec(
          Slot("uRd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Address)
        ),
        Vec("ldcu_256_const_RCxR_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(uRd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Address(srcB)
            ) => Some(Instruction.LdcuudpPipeRegRegAddressAddress(uRd2, dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object LdcuudpPipeRegRegAddressAddressImm extends Definition(
        "LdcuudpPipeRegRegAddressAddressImm",
        "LDCUudp_pipe",
        Vec(
          Slot("uRd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Address),
          Slot("word_mask", OperandKind.Immediate)
        ),
        Vec("ldcu_256_const_RCxR_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(uRd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(word_mask)
            ) =>
          Some(Instruction.LdcuudpPipeRegRegAddressAddressImm(uRd2, dst, srcA, srcB, word_mask, modifiers, guard))
        case _ => None
      }
  }
  object LdcuudpPipeRegRegAddressImm extends Definition(
        "LdcuudpPipeRegRegAddressImm",
        "LDCUudp_pipe",
        Vec(
          Slot("uRd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("word_mask", OperandKind.Immediate)
        ),
        Vec("ldcu_256_ur_offs_optional_upx_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(uRd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Immediate(word_mask)
            ) => Some(Instruction.LdcuudpPipeRegRegAddressImm(uRd2, dst, srcA, word_mask, modifiers, guard))
        case _ => None
      }
  }
  object LdcuudpPipeRegRegAddressImmPred extends Definition(
        "LdcuudpPipeRegRegAddressImmPred",
        "LDCUudp_pipe",
        Vec(
          Slot("uRd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("word_mask", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ldcu_256_ur_offs_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(uRd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Immediate(word_mask),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.LdcuudpPipeRegRegAddressImmPred(uRd2, dst, srcA, word_mask, uPp, modifiers, guard))
        case _ => None
      }
  }
  object LdcuudpPipeRegRegAddressPred extends Definition(
        "LdcuudpPipeRegRegAddressPred",
        "LDCUudp_pipe",
        Vec(
          Slot("uRd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ldcu_256_ur_offs_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(uRd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.LdcuudpPipeRegRegAddressPred(uRd2, dst, srcA, uPp, modifiers, guard))
        case _ => None
      }
  }
  object LdcuudpPipeRegRegConst extends Definition(
        "LdcuudpPipeRegRegConst",
        "LDCUudp_pipe",
        Vec(
          Slot("uRd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("sa", OperandKind.ConstantBank)
        ),
        Vec("ldcu_256_const_RCR_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(uRd2),
              InstructionOperand.Register(dst),
              InstructionOperand.ConstantBank(sa)
            ) => Some(Instruction.LdcuudpPipeRegRegConst(uRd2, dst, sa, modifiers, guard))
        case _ => None
      }
  }
  object LdcuudpPipeRegRegConstImm extends Definition(
        "LdcuudpPipeRegRegConstImm",
        "LDCUudp_pipe",
        Vec(
          Slot("uRd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("sa", OperandKind.ConstantBank),
          Slot("word_mask", OperandKind.Immediate)
        ),
        Vec("ldcu_256_const_RCR_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(uRd2),
              InstructionOperand.Register(dst),
              InstructionOperand.ConstantBank(sa),
              InstructionOperand.Immediate(word_mask)
            ) => Some(Instruction.LdcuudpPipeRegRegConstImm(uRd2, dst, sa, word_mask, modifiers, guard))
        case _ => None
      }
  }
  object LdcmioPipeRegAddressAddress extends Definition(
        "LdcmioPipeRegAddressAddress",
        "LDCmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Address)),
        Vec("ldc_ur__URRzI", "ldc_ur__URnonRzI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Address(srcB)
            ) => Some(Instruction.LdcmioPipeRegAddressAddress(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object LdcmioPipeRegConst extends Definition(
        "LdcmioPipeRegConst",
        "LDCmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("sa", OperandKind.ConstantBank)),
        Vec("I_LDC", "I_LDC_64", "LDC", "LDC_64", "ldc__RaNonRZ", "ldc__RaRZ")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(sa)) =>
          Some(Instruction.LdcmioPipeRegConst(dst, sa, modifiers, guard))
        case _ => None
      }
  }
  object LdcxluPipeRegConst extends Definition(
        "LdcxluPipeRegConst",
        "LDCxlu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcConst", OperandKind.ConstantBank)),
        Vec("I LDC", "I LDC 64", "LDC", "LDC 64")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(srcConst)) =>
          Some(Instruction.LdcxluPipeRegConst(dst, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object LdgPredRegAddress extends Definition(
        "LdgPredRegAddress",
        "LDG",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address)
        ),
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
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("pnz", OperandKind.Predicate)
        ),
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
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address)),
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
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address), Slot("pnz", OperandKind.Predicate)),
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
        Vec(Slot("rd2", OperandKind.Register), Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address)),
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
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("word_mask", OperandKind.Immediate)
        ),
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
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("word_mask", OperandKind.Immediate),
          Slot("pnz", OperandKind.Predicate)
        ),
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
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("pnz", OperandKind.Predicate)
        ),
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
  object LdgdepbarNoOperands extends Definition("LdgdepbarNoOperands", "LDGDEPBAR", Vec(), Vec("ldgdepbar_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.LdgdepbarNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object LdgdepbarmioPipeNoOperands
      extends Definition("LdgdepbarmioPipeNoOperands", "LDGDEPBARmio_pipe", Vec(), Vec("ldgdepbar_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.LdgdepbarmioPipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object LdgmcRegAddress extends Definition(
        "LdgmcRegAddress",
        "LDGMC",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address)),
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
  object LdgmcmioPipeRegAddress extends Definition(
        "LdgmcmioPipeRegAddress",
        "LDGMCmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address)),
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
          Some(Instruction.LdgmcmioPipeRegAddress(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object LdgstsAddressAddress extends Definition(
        "LdgstsAddressAddress",
        "LDGSTS",
        Vec(Slot("srcB", OperandKind.Address), Slot("srcA", OperandKind.Address)),
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
        Vec(Slot("srcB", OperandKind.Address), Slot("srcA", OperandKind.Address), Slot("pnz", OperandKind.Predicate)),
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
  object LdgstsmioPipeAddressAddress extends Definition(
        "LdgstsmioPipeAddressAddress",
        "LDGSTSmio_pipe",
        Vec(Slot("srcB", OperandKind.Address), Slot("srcA", OperandKind.Address)),
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
          Some(Instruction.LdgstsmioPipeAddressAddress(srcB, srcA, modifiers, guard))
        case _ => None
      }
  }
  object LdgstsmioPipeAddressAddressPred extends Definition(
        "LdgstsmioPipeAddressAddressPred",
        "LDGSTSmio_pipe",
        Vec(Slot("srcB", OperandKind.Address), Slot("srcA", OperandKind.Address), Slot("pnz", OperandKind.Predicate)),
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
            ) => Some(Instruction.LdgstsmioPipeAddressAddressPred(srcB, srcA, pnz, modifiers, guard))
        case _ => None
      }
  }
  object LdgmioPipePredRegAddress extends Definition(
        "LdgmioPipePredRegAddress",
        "LDGmio_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address)
        ),
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
            ) => Some(Instruction.LdgmioPipePredRegAddress(dstPredicate, dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object LdgmioPipePredRegAddressPred extends Definition(
        "LdgmioPipePredRegAddressPred",
        "LDGmio_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("pnz", OperandKind.Predicate)
        ),
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
            ) => Some(Instruction.LdgmioPipePredRegAddressPred(pu, dst, srcA, pnz, modifiers, guard))
        case _ => None
      }
  }
  object LdgmioPipeRegAddress extends Definition(
        "LdgmioPipeRegAddress",
        "LDGmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address)),
        Vec(
          "I_LDG",
          "LDG",
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
          Some(Instruction.LdgmioPipeRegAddress(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object LdgmioPipeRegAddressPred extends Definition(
        "LdgmioPipeRegAddressPred",
        "LDGmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address), Slot("pnz", OperandKind.Predicate)),
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
            ) => Some(Instruction.LdgmioPipeRegAddressPred(dst, srcA, pnz, modifiers, guard))
        case _ => None
      }
  }
  object LdgmioPipeRegRegAddress extends Definition(
        "LdgmioPipeRegRegAddress",
        "LDGmio_pipe",
        Vec(Slot("rd2", OperandKind.Register), Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address)),
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
            ) => Some(Instruction.LdgmioPipeRegRegAddress(rd2, dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object LdgmioPipeRegRegAddressImm extends Definition(
        "LdgmioPipeRegRegAddressImm",
        "LDGmio_pipe",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("word_mask", OperandKind.Immediate)
        ),
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
            ) => Some(Instruction.LdgmioPipeRegRegAddressImm(rd2, dst, srcA, word_mask, modifiers, guard))
        case _ => None
      }
  }
  object LdgmioPipeRegRegAddressImmPred extends Definition(
        "LdgmioPipeRegRegAddressImmPred",
        "LDGmio_pipe",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("word_mask", OperandKind.Immediate),
          Slot("pnz", OperandKind.Predicate)
        ),
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
            ) => Some(Instruction.LdgmioPipeRegRegAddressImmPred(rd2, dst, srcA, word_mask, pnz, modifiers, guard))
        case _ => None
      }
  }
  object LdgmioPipeRegRegAddressPred extends Definition(
        "LdgmioPipeRegRegAddressPred",
        "LDGmio_pipe",
        Vec(
          Slot("rd2", OperandKind.Register),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("pnz", OperandKind.Predicate)
        ),
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
            ) => Some(Instruction.LdgmioPipeRegRegAddressPred(rd2, dst, srcA, pnz, modifiers, guard))
        case _ => None
      }
  }
  object LdgtexPipeRegAddress extends Definition(
        "LdgtexPipeRegAddress",
        "LDGtex_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address)),
        Vec("LDG CG CS", "LDG CT")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Address(srcA)) =>
          Some(Instruction.LdgtexPipeRegAddress(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object LdlRegAddress extends Definition(
        "LdlRegAddress",
        "LDL",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address)),
        Vec("I LDL", "I_LDL", "LDL", "ldl__sImmOffset", "ldl__uImmOffset", "ldl_memdesc_", "ldl_uniform_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Address(srcA)) =>
          Some(Instruction.LdlRegAddress(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object LdlaguPipeRegAddress extends Definition(
        "LdlaguPipeRegAddress",
        "LDLagu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address)),
        Vec("I LDL", "LDL")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Address(srcA)) =>
          Some(Instruction.LdlaguPipeRegAddress(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object LdlmioPipeRegAddress extends Definition(
        "LdlmioPipeRegAddress",
        "LDLmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address)),
        Vec("I_LDL", "LDL", "ldl__sImmOffset", "ldl__uImmOffset", "ldl_memdesc_", "ldl_uniform_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Address(srcA)) =>
          Some(Instruction.LdlmioPipeRegAddress(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object LdsPredRegAddress extends Definition(
        "LdsPredRegAddress",
        "LDS",
        Vec(Slot("pu", OperandKind.Predicate), Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address)),
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
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address)),
        Vec("I LDS", "I_LDS", "LDS", "lds__sImmOffset", "lds__uImmOffset", "lds_uniform_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Address(srcA)) =>
          Some(Instruction.LdsRegAddress(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object LdslkPredRegAddress extends Definition(
        "LdslkPredRegAddress",
        "LDSLK",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address)
        ),
        Vec("I LDSLK", "LDSLK")
      ) {
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
  object LdslkaguPipePredRegAddress extends Definition(
        "LdslkaguPipePredRegAddress",
        "LDSLKagu_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address)
        ),
        Vec("I LDSLK", "LDSLK")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA)
            ) => Some(Instruction.LdslkaguPipePredRegAddress(dstPredicate, dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object LdsmRegAddress extends Definition(
        "LdsmRegAddress",
        "LDSM",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address)),
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
  object LdsmmioPipeRegAddress extends Definition(
        "LdsmmioPipeRegAddress",
        "LDSMmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address)),
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
          Some(Instruction.LdsmmioPipeRegAddress(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object LdsaguPipeRegAddress extends Definition(
        "LdsaguPipeRegAddress",
        "LDSagu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address)),
        Vec("I LDS", "LDS")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Address(srcA)) =>
          Some(Instruction.LdsaguPipeRegAddress(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object LdsmioPipePredRegAddress extends Definition(
        "LdsmioPipePredRegAddress",
        "LDSmio_pipe",
        Vec(Slot("pu", OperandKind.Predicate), Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address)),
        Vec("lds_zd__sImmOffset", "lds_zd__uImmOffset", "lds_zd_uniform_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA)
            ) => Some(Instruction.LdsmioPipePredRegAddress(pu, dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object LdsmioPipeRegAddress extends Definition(
        "LdsmioPipeRegAddress",
        "LDSmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address)),
        Vec("I_LDS", "LDS", "lds__sImmOffset", "lds__uImmOffset", "lds_uniform_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Address(srcA)) =>
          Some(Instruction.LdsmioPipeRegAddress(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object LdtRegAddress extends Definition(
        "LdtRegAddress",
        "LDT",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Address)),
        Vec("ldt_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Address(srcB)) =>
          Some(Instruction.LdtRegAddress(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object LdtmRegAddress extends Definition(
        "LdtmRegAddress",
        "LDTM",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Address)),
        Vec("ldtm_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Address(srcB)) =>
          Some(Instruction.LdtmRegAddress(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object LdtmRegRegAddress extends Definition(
        "LdtmRegRegAddress",
        "LDTM",
        Vec(Slot("rd2", OperandKind.Register), Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Address)),
        Vec("ldtm_stat_")
      ) {
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
  object LdtmudpPipeRegAddress extends Definition(
        "LdtmudpPipeRegAddress",
        "LDTMudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Address)),
        Vec("ldtm_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Address(srcB)) =>
          Some(Instruction.LdtmudpPipeRegAddress(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object LdtmudpPipeRegRegAddress extends Definition(
        "LdtmudpPipeRegRegAddress",
        "LDTMudp_pipe",
        Vec(Slot("rd2", OperandKind.Register), Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Address)),
        Vec("ldtm_stat_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcB)
            ) => Some(Instruction.LdtmudpPipeRegRegAddress(rd2, dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object LdtramRegAddress extends Definition(
        "LdtramRegAddress",
        "LDTRAM",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address)),
        Vec("ldtram_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Address(srcA)) =>
          Some(Instruction.LdtramRegAddress(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object LdtrammioPipeRegAddress extends Definition(
        "LdtrammioPipeRegAddress",
        "LDTRAMmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address)),
        Vec("ldtram_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Address(srcA)) =>
          Some(Instruction.LdtrammioPipeRegAddress(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object LdtudpPipeRegAddress extends Definition(
        "LdtudpPipeRegAddress",
        "LDTudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Address)),
        Vec("ldt_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Address(srcB)) =>
          Some(Instruction.LdtudpPipeRegAddress(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object LdaguPipeRegAddress extends Definition(
        "LdaguPipeRegAddress",
        "LDagu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address)),
        Vec("I LD", "LD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Address(srcA)) =>
          Some(Instruction.LdaguPipeRegAddress(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object LdmioPipePredRegAddress extends Definition(
        "LdmioPipePredRegAddress",
        "LDmio_pipe",
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
            ) => Some(Instruction.LdmioPipePredRegAddress(pu, dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object LdmioPipePredRegAddressPred extends Definition(
        "LdmioPipePredRegAddressPred",
        "LDmio_pipe",
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
            ) => Some(Instruction.LdmioPipePredRegAddressPred(pu, dst, srcA, pnz, modifiers, guard))
        case _ => None
      }
  }
  object LdmioPipeRegAddress extends Definition(
        "LdmioPipeRegAddress",
        "LDmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address)),
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
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Address(srcA)) =>
          Some(Instruction.LdmioPipeRegAddress(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object LdmioPipeRegAddressPred extends Definition(
        "LdmioPipeRegAddressPred",
        "LDmio_pipe",
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
            ) => Some(Instruction.LdmioPipeRegAddressPred(dst, srcA, pnz, modifiers, guard))
        case _ => None
      }
  }
  object LeaPredRegRegConst extends Definition(
        "LeaPredRegRegConst",
        "LEA",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("LEA_LO_CONST")
      ) {
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
  object LeaPredRegRegConstImm extends Definition(
        "LeaPredRegRegConstImm",
        "LEA",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("uImm5", OperandKind.Immediate)
        ),
        Vec("LEA_LO_CONST")
      ) {
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
  object LeaPredRegRegConstReg extends Definition(
        "LeaPredRegRegConstReg",
        "LEA",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("LEA_HI_CONST")
      ) {
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
  object LeaPredRegRegConstRegImm extends Definition(
        "LeaPredRegRegConstRegImm",
        "LEA",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register),
          Slot("uImm5", OperandKind.Immediate)
        ),
        Vec("LEA_HI_CONST")
      ) {
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
  object LeaPredRegRegImm extends Definition(
        "LeaPredRegRegImm",
        "LEA",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm20", OperandKind.Immediate)
        ),
        Vec("LEA_LO_IMM")
      ) {
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
  object LeaPredRegRegImmImm extends Definition(
        "LeaPredRegRegImmImm",
        "LEA",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm20", OperandKind.Immediate),
          Slot("uImm5", OperandKind.Immediate)
        ),
        Vec("LEA_LO_IMM")
      ) {
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
  object LeaPredRegRegReg extends Definition(
        "LeaPredRegRegReg",
        "LEA",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register)
        ),
        Vec("LEA_LO_REG")
      ) {
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
  object LeaPredRegRegRegImm extends Definition(
        "LeaPredRegRegRegImm",
        "LEA",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("uImm5", OperandKind.Immediate)
        ),
        Vec("LEA_LO_REG")
      ) {
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
  object LeaPredRegRegRegReg extends Definition(
        "LeaPredRegRegRegReg",
        "LEA",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("LEA_HI_REG")
      ) {
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
  object LeaPredRegRegRegRegImm extends Definition(
        "LeaPredRegRegRegRegImm",
        "LEA",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uImm5", OperandKind.Immediate)
        ),
        Vec("LEA_HI_REG")
      ) {
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
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("scaleU5", OperandKind.Immediate)
        ),
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
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("scaleU5", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
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
  object LeaRegPredRegAddressImmRegImm extends Definition(
        "LeaRegPredRegAddressImmRegImm",
        "LEA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("lea_hi_noimm__RCxR_RCxR")
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
  object LeaRegPredRegAddressImmRegImmPred extends Definition(
        "LeaRegPredRegAddressImmRegImmPred",
        "LEA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("lea_hi_noimm_x__RCxR_RCxR")
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
  object LeaRegPredRegConstImm extends Definition(
        "LeaRegPredRegConstImm",
        "LEA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("lea_hi_noimm_sx32__RCR_RCR", "lea_lo_noimm__RCR_RCR")
      ) {
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
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("scaleU5", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
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
  object LeaRegPredRegConstRegImm extends Definition(
        "LeaRegPredRegConstRegImm",
        "LEA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("lea_hi_noimm__RCR_RCR")
      ) {
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
  object LeaRegPredRegConstRegImmPred extends Definition(
        "LeaRegPredRegConstRegImmPred",
        "LEA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("lea_hi_noimm_x__RCR_RCR")
      ) {
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
  object LeaRegPredRegImmImm extends Definition(
        "LeaRegPredRegImmImm",
        "LEA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("lea_hi_imm_sx32__RuIR_RIR", "lea_lo_imm__RuIR_RIR")
      ) {
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
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("scaleU5", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
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
  object LeaRegPredRegImmRegImm extends Definition(
        "LeaRegPredRegImmRegImm",
        "LEA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("lea_hi_imm__RuIR_RIR")
      ) {
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
  object LeaRegPredRegImmRegImmPred extends Definition(
        "LeaRegPredRegImmRegImmPred",
        "LEA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("lea_hi_imm_x__RuIR_RIR")
      ) {
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
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate)
        ),
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
  object LeaRegPredRegRegImmImm extends Definition(
        "LeaRegPredRegRegImmImm",
        "LEA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("lea_hi_imm__RRuI_RRI")
      ) {
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
  object LeaRegPredRegRegImmImmPred extends Definition(
        "LeaRegPredRegRegImmImmPred",
        "LEA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate),
          Slot("scaleU5", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("lea_hi_imm_x__RRuI_RRI")
      ) {
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
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
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
  object LeaRegPredRegRegRegImm extends Definition(
        "LeaRegPredRegRegRegImm",
        "LEA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("lea_hi_noimm__RRR_RRR", "lea_hi_noimm__RUR_RUR")
      ) {
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
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
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
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("scaleU5", OperandKind.Immediate)
        ),
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
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("scaleU5", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
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
  object LeaRegRegAddressImmRegImm extends Definition(
        "LeaRegRegAddressImmRegImm",
        "LEA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("lea_hi_noimm__RCxR_RCxR")
      ) {
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
  object LeaRegRegAddressImmRegImmPred extends Definition(
        "LeaRegRegAddressImmRegImmPred",
        "LEA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("lea_hi_noimm_x__RCxR_RCxR")
      ) {
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
  object LeaRegRegConst extends Definition(
        "LeaRegRegConst",
        "LEA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("LEA_LO_CONST")
      ) {
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
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("scaleU5", OperandKind.Immediate)
        ),
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
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("scaleU5", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
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
  object LeaRegRegConstReg extends Definition(
        "LeaRegRegConstReg",
        "LEA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("LEA_HI_CONST")
      ) {
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
  object LeaRegRegConstRegImm extends Definition(
        "LeaRegRegConstRegImm",
        "LEA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("LEA_HI_CONST", "lea_hi_noimm__RCR_RCR")
      ) {
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
  object LeaRegRegConstRegImmPred extends Definition(
        "LeaRegRegConstRegImmPred",
        "LEA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("lea_hi_noimm_x__RCR_RCR")
      ) {
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
  object LeaRegRegImm extends Definition(
        "LeaRegRegImm",
        "LEA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm20", OperandKind.Immediate)
        ),
        Vec("LEA_LO_IMM")
      ) {
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
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("scaleU5", OperandKind.Immediate)
        ),
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
  object LeaRegRegImmImmPred extends Definition(
        "LeaRegRegImmImmPred",
        "LEA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("scaleU5", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("lea_hi_imm_sx32_x__RuIR_RIR", "lea_lo_imm_x__RuIR_RIR")
      ) {
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
  object LeaRegRegImmRegImm extends Definition(
        "LeaRegRegImmRegImm",
        "LEA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("lea_hi_imm__RuIR_RIR")
      ) {
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
  object LeaRegRegImmRegImmPred extends Definition(
        "LeaRegRegImmRegImmPred",
        "LEA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("lea_hi_imm_x__RuIR_RIR")
      ) {
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
  object LeaRegRegReg extends Definition(
        "LeaRegRegReg",
        "LEA",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("LEA_LO_REG")
      ) {
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
  object LeaRegRegRegImm extends Definition(
        "LeaRegRegRegImm",
        "LEA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate)
        ),
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
  object LeaRegRegRegImmImm extends Definition(
        "LeaRegRegRegImmImm",
        "LEA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("lea_hi_imm__RRuI_RRI")
      ) {
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
  object LeaRegRegRegImmImmPred extends Definition(
        "LeaRegRegRegImmImmPred",
        "LEA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate),
          Slot("scaleU5", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("lea_hi_imm_x__RRuI_RRI")
      ) {
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
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
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
  object LeaRegRegRegReg extends Definition(
        "LeaRegRegRegReg",
        "LEA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("LEA_HI_REG")
      ) {
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
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate)
        ),
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
  object LeaRegRegRegRegImmPred extends Definition(
        "LeaRegRegRegRegImmPred",
        "LEA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("lea_hi_noimm_x__RRR_RRR", "lea_hi_noimm_x__RUR_RUR")
      ) {
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
  object LeafxuPipePredRegRegConst extends Definition(
        "LeafxuPipePredRegRegConst",
        "LEAfxu_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("LEA_LO_CONST")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.LeafxuPipePredRegRegConst(dstPredicate, dst, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object LeafxuPipePredRegRegConstImm extends Definition(
        "LeafxuPipePredRegRegConstImm",
        "LEAfxu_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("uImm5", OperandKind.Immediate)
        ),
        Vec("LEA_LO_CONST")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Immediate(uImm5)
            ) =>
          Some(Instruction.LeafxuPipePredRegRegConstImm(dstPredicate, dst, srcA, srcConst, uImm5, modifiers, guard))
        case _ => None
      }
  }
  object LeafxuPipePredRegRegConstReg extends Definition(
        "LeafxuPipePredRegRegConstReg",
        "LEAfxu_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("LEA_HI_CONST")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC)
            ) =>
          Some(Instruction.LeafxuPipePredRegRegConstReg(dstPredicate, dst, srcA, srcConst, srcC, modifiers, guard))
        case _ => None
      }
  }
  object LeafxuPipePredRegRegConstRegImm extends Definition(
        "LeafxuPipePredRegRegConstRegImm",
        "LEAfxu_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register),
          Slot("uImm5", OperandKind.Immediate)
        ),
        Vec("LEA_HI_CONST")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(uImm5)
            ) => Some(Instruction.LeafxuPipePredRegRegConstRegImm(
            dstPredicate,
            dst,
            srcA,
            srcConst,
            srcC,
            uImm5,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object LeafxuPipePredRegRegImm extends Definition(
        "LeafxuPipePredRegRegImm",
        "LEAfxu_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm20", OperandKind.Immediate)
        ),
        Vec("LEA_LO_IMM")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm20)
            ) => Some(Instruction.LeafxuPipePredRegRegImm(dstPredicate, dst, srcA, uImm20, modifiers, guard))
        case _ => None
      }
  }
  object LeafxuPipePredRegRegImmImm extends Definition(
        "LeafxuPipePredRegRegImmImm",
        "LEAfxu_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm20", OperandKind.Immediate),
          Slot("uImm5", OperandKind.Immediate)
        ),
        Vec("LEA_LO_IMM")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm20),
              InstructionOperand.Immediate(uImm5)
            ) => Some(Instruction.LeafxuPipePredRegRegImmImm(dstPredicate, dst, srcA, uImm20, uImm5, modifiers, guard))
        case _ => None
      }
  }
  object LeafxuPipePredRegRegReg extends Definition(
        "LeafxuPipePredRegRegReg",
        "LEAfxu_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register)
        ),
        Vec("LEA_LO_REG")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.LeafxuPipePredRegRegReg(dstPredicate, dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object LeafxuPipePredRegRegRegImm extends Definition(
        "LeafxuPipePredRegRegRegImm",
        "LEAfxu_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("uImm5", OperandKind.Immediate)
        ),
        Vec("LEA_LO_REG")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(uImm5)
            ) => Some(Instruction.LeafxuPipePredRegRegRegImm(dstPredicate, dst, srcA, srcB, uImm5, modifiers, guard))
        case _ => None
      }
  }
  object LeafxuPipePredRegRegRegReg extends Definition(
        "LeafxuPipePredRegRegRegReg",
        "LEAfxu_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("LEA_HI_REG")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.LeafxuPipePredRegRegRegReg(dstPredicate, dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object LeafxuPipePredRegRegRegRegImm extends Definition(
        "LeafxuPipePredRegRegRegRegImm",
        "LEAfxu_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uImm5", OperandKind.Immediate)
        ),
        Vec("LEA_HI_REG")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(uImm5)
            ) =>
          Some(Instruction.LeafxuPipePredRegRegRegRegImm(dstPredicate, dst, srcA, srcB, srcC, uImm5, modifiers, guard))
        case _ => None
      }
  }
  object LeafxuPipeRegRegConst extends Definition(
        "LeafxuPipeRegRegConst",
        "LEAfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("LEA_LO_CONST")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.LeafxuPipeRegRegConst(dst, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object LeafxuPipeRegRegConstImm extends Definition(
        "LeafxuPipeRegRegConstImm",
        "LEAfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("uImm5", OperandKind.Immediate)
        ),
        Vec("LEA_LO_CONST")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Immediate(uImm5)
            ) => Some(Instruction.LeafxuPipeRegRegConstImm(dst, srcA, srcConst, uImm5, modifiers, guard))
        case _ => None
      }
  }
  object LeafxuPipeRegRegConstReg extends Definition(
        "LeafxuPipeRegRegConstReg",
        "LEAfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("LEA_HI_CONST")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.LeafxuPipeRegRegConstReg(dst, srcA, srcConst, srcC, modifiers, guard))
        case _ => None
      }
  }
  object LeafxuPipeRegRegConstRegImm extends Definition(
        "LeafxuPipeRegRegConstRegImm",
        "LEAfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register),
          Slot("uImm5", OperandKind.Immediate)
        ),
        Vec("LEA_HI_CONST")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(uImm5)
            ) => Some(Instruction.LeafxuPipeRegRegConstRegImm(dst, srcA, srcConst, srcC, uImm5, modifiers, guard))
        case _ => None
      }
  }
  object LeafxuPipeRegRegImm extends Definition(
        "LeafxuPipeRegRegImm",
        "LEAfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm20", OperandKind.Immediate)
        ),
        Vec("LEA_LO_IMM")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm20)
            ) => Some(Instruction.LeafxuPipeRegRegImm(dst, srcA, uImm20, modifiers, guard))
        case _ => None
      }
  }
  object LeafxuPipeRegRegImmImm extends Definition(
        "LeafxuPipeRegRegImmImm",
        "LEAfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm20", OperandKind.Immediate),
          Slot("uImm5", OperandKind.Immediate)
        ),
        Vec("LEA_LO_IMM")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm20),
              InstructionOperand.Immediate(uImm5)
            ) => Some(Instruction.LeafxuPipeRegRegImmImm(dst, srcA, uImm20, uImm5, modifiers, guard))
        case _ => None
      }
  }
  object LeafxuPipeRegRegReg extends Definition(
        "LeafxuPipeRegRegReg",
        "LEAfxu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("LEA_LO_REG")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.LeafxuPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object LeafxuPipeRegRegRegImm extends Definition(
        "LeafxuPipeRegRegRegImm",
        "LEAfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("uImm5", OperandKind.Immediate)
        ),
        Vec("LEA_LO_REG")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(uImm5)
            ) => Some(Instruction.LeafxuPipeRegRegRegImm(dst, srcA, srcB, uImm5, modifiers, guard))
        case _ => None
      }
  }
  object LeafxuPipeRegRegRegReg extends Definition(
        "LeafxuPipeRegRegRegReg",
        "LEAfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("LEA_HI_REG")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.LeafxuPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object LeafxuPipeRegRegRegRegImm extends Definition(
        "LeafxuPipeRegRegRegRegImm",
        "LEAfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uImm5", OperandKind.Immediate)
        ),
        Vec("LEA_HI_REG")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(uImm5)
            ) => Some(Instruction.LeafxuPipeRegRegRegRegImm(dst, srcA, srcB, srcC, uImm5, modifiers, guard))
        case _ => None
      }
  }
  object LeaintPipeRegPredRegAddressImmImm extends Definition(
        "LeaintPipeRegPredRegAddressImmImm",
        "LEAint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("scaleU5", OperandKind.Immediate)
        ),
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
            ) =>
          Some(Instruction.LeaintPipeRegPredRegAddressImmImm(dst, pu, srcA, srcB, sb_offset, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object LeaintPipeRegPredRegAddressImmImmPred extends Definition(
        "LeaintPipeRegPredRegAddressImmImmPred",
        "LEAint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("scaleU5", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
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
            ) => Some(Instruction.LeaintPipeRegPredRegAddressImmImmPred(
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
  object LeaintPipeRegPredRegAddressImmRegImm extends Definition(
        "LeaintPipeRegPredRegAddressImmRegImm",
        "LEAint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("lea_hi_noimm__RCxR_RCxR")
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
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.LeaintPipeRegPredRegAddressImmRegImm(
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
  object LeaintPipeRegPredRegAddressImmRegImmPred extends Definition(
        "LeaintPipeRegPredRegAddressImmRegImmPred",
        "LEAint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("lea_hi_noimm_x__RCxR_RCxR")
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
              InstructionOperand.Immediate(scaleU5),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.LeaintPipeRegPredRegAddressImmRegImmPred(
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
  object LeaintPipeRegPredRegConstImm extends Definition(
        "LeaintPipeRegPredRegConstImm",
        "LEAint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("lea_hi_noimm_sx32__RCR_RCR", "lea_lo_noimm__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.LeaintPipeRegPredRegConstImm(dst, pu, srcA, sb, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object LeaintPipeRegPredRegConstImmPred extends Definition(
        "LeaintPipeRegPredRegConstImmPred",
        "LEAint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("scaleU5", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
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
            ) => Some(Instruction.LeaintPipeRegPredRegConstImmPred(dst, pu, srcA, sb, scaleU5, pp, modifiers, guard))
        case _ => None
      }
  }
  object LeaintPipeRegPredRegConstRegImm extends Definition(
        "LeaintPipeRegPredRegConstRegImm",
        "LEAint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("lea_hi_noimm__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.LeaintPipeRegPredRegConstRegImm(dst, pu, srcA, sb, srcC, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object LeaintPipeRegPredRegConstRegImmPred extends Definition(
        "LeaintPipeRegPredRegConstRegImmPred",
        "LEAint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("lea_hi_noimm_x__RCR_RCR")
      ) {
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
            ) =>
          Some(Instruction.LeaintPipeRegPredRegConstRegImmPred(dst, pu, srcA, sb, srcC, scaleU5, pp, modifiers, guard))
        case _ => None
      }
  }
  object LeaintPipeRegPredRegImmImm extends Definition(
        "LeaintPipeRegPredRegImmImm",
        "LEAint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("lea_hi_imm_sx32__RuIR_RIR", "lea_lo_imm__RuIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.LeaintPipeRegPredRegImmImm(dst, pu, srcA, sb, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object LeaintPipeRegPredRegImmImmPred extends Definition(
        "LeaintPipeRegPredRegImmImmPred",
        "LEAint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("scaleU5", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
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
            ) => Some(Instruction.LeaintPipeRegPredRegImmImmPred(dst, pu, srcA, sb, scaleU5, pp, modifiers, guard))
        case _ => None
      }
  }
  object LeaintPipeRegPredRegImmRegImm extends Definition(
        "LeaintPipeRegPredRegImmRegImm",
        "LEAint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("lea_hi_imm__RuIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.LeaintPipeRegPredRegImmRegImm(dst, pu, srcA, sb, srcC, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object LeaintPipeRegPredRegImmRegImmPred extends Definition(
        "LeaintPipeRegPredRegImmRegImmPred",
        "LEAint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("lea_hi_imm_x__RuIR_RIR")
      ) {
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
            ) =>
          Some(Instruction.LeaintPipeRegPredRegImmRegImmPred(dst, pu, srcA, sb, srcC, scaleU5, pp, modifiers, guard))
        case _ => None
      }
  }
  object LeaintPipeRegPredRegRegImm extends Definition(
        "LeaintPipeRegPredRegRegImm",
        "LEAint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate)
        ),
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
            ) => Some(Instruction.LeaintPipeRegPredRegRegImm(dst, pu, srcA, srcB, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object LeaintPipeRegPredRegRegImmImm extends Definition(
        "LeaintPipeRegPredRegRegImmImm",
        "LEAint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("lea_hi_imm__RRuI_RRI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.LeaintPipeRegPredRegRegImmImm(dst, pu, srcA, srcB, sc, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object LeaintPipeRegPredRegRegImmImmPred extends Definition(
        "LeaintPipeRegPredRegRegImmImmPred",
        "LEAint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate),
          Slot("scaleU5", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("lea_hi_imm_x__RRuI_RRI")
      ) {
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
            ) =>
          Some(Instruction.LeaintPipeRegPredRegRegImmImmPred(dst, pu, srcA, srcB, sc, scaleU5, pp, modifiers, guard))
        case _ => None
      }
  }
  object LeaintPipeRegPredRegRegImmPred extends Definition(
        "LeaintPipeRegPredRegRegImmPred",
        "LEAint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
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
            ) => Some(Instruction.LeaintPipeRegPredRegRegImmPred(dst, pu, srcA, srcB, scaleU5, pp, modifiers, guard))
        case _ => None
      }
  }
  object LeaintPipeRegPredRegRegRegImm extends Definition(
        "LeaintPipeRegPredRegRegRegImm",
        "LEAint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("lea_hi_noimm__RRR_RRR", "lea_hi_noimm__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.LeaintPipeRegPredRegRegRegImm(dst, pu, srcA, srcB, srcC, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object LeaintPipeRegPredRegRegRegImmPred extends Definition(
        "LeaintPipeRegPredRegRegRegImmPred",
        "LEAint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
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
            ) =>
          Some(Instruction.LeaintPipeRegPredRegRegRegImmPred(dst, pu, srcA, srcB, srcC, scaleU5, pp, modifiers, guard))
        case _ => None
      }
  }
  object LeaintPipeRegRegAddressImmImm extends Definition(
        "LeaintPipeRegRegAddressImmImm",
        "LEAint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("scaleU5", OperandKind.Immediate)
        ),
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
            ) => Some(Instruction.LeaintPipeRegRegAddressImmImm(dst, srcA, srcB, sb_offset, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object LeaintPipeRegRegAddressImmImmPred extends Definition(
        "LeaintPipeRegRegAddressImmImmPred",
        "LEAint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("scaleU5", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
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
            ) =>
          Some(Instruction.LeaintPipeRegRegAddressImmImmPred(dst, srcA, srcB, sb_offset, scaleU5, pp, modifiers, guard))
        case _ => None
      }
  }
  object LeaintPipeRegRegAddressImmRegImm extends Definition(
        "LeaintPipeRegRegAddressImmRegImm",
        "LEAint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("lea_hi_noimm__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.LeaintPipeRegRegAddressImmRegImm(
            dst,
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
  object LeaintPipeRegRegAddressImmRegImmPred extends Definition(
        "LeaintPipeRegRegAddressImmRegImmPred",
        "LEAint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("lea_hi_noimm_x__RCxR_RCxR")
      ) {
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
            ) => Some(Instruction.LeaintPipeRegRegAddressImmRegImmPred(
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
  object LeaintPipeRegRegConstImm extends Definition(
        "LeaintPipeRegRegConstImm",
        "LEAint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("lea_hi_noimm_sx32__RCR_RCR", "lea_lo_noimm__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.LeaintPipeRegRegConstImm(dst, srcA, sb, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object LeaintPipeRegRegConstImmPred extends Definition(
        "LeaintPipeRegRegConstImmPred",
        "LEAint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("scaleU5", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
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
            ) => Some(Instruction.LeaintPipeRegRegConstImmPred(dst, srcA, sb, scaleU5, pp, modifiers, guard))
        case _ => None
      }
  }
  object LeaintPipeRegRegConstRegImm extends Definition(
        "LeaintPipeRegRegConstRegImm",
        "LEAint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("lea_hi_noimm__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.LeaintPipeRegRegConstRegImm(dst, srcA, sb, srcC, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object LeaintPipeRegRegConstRegImmPred extends Definition(
        "LeaintPipeRegRegConstRegImmPred",
        "LEAint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("lea_hi_noimm_x__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(scaleU5),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.LeaintPipeRegRegConstRegImmPred(dst, srcA, sb, srcC, scaleU5, pp, modifiers, guard))
        case _ => None
      }
  }
  object LeaintPipeRegRegImmImm extends Definition(
        "LeaintPipeRegRegImmImm",
        "LEAint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("lea_hi_imm_sx32__RuIR_RIR", "lea_lo_imm__RuIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.LeaintPipeRegRegImmImm(dst, srcA, sb, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object LeaintPipeRegRegImmImmPred extends Definition(
        "LeaintPipeRegRegImmImmPred",
        "LEAint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("scaleU5", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("lea_hi_imm_sx32_x__RuIR_RIR", "lea_lo_imm_x__RuIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Immediate(scaleU5),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.LeaintPipeRegRegImmImmPred(dst, srcA, sb, scaleU5, pp, modifiers, guard))
        case _ => None
      }
  }
  object LeaintPipeRegRegImmRegImm extends Definition(
        "LeaintPipeRegRegImmRegImm",
        "LEAint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("lea_hi_imm__RuIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.LeaintPipeRegRegImmRegImm(dst, srcA, sb, srcC, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object LeaintPipeRegRegImmRegImmPred extends Definition(
        "LeaintPipeRegRegImmRegImmPred",
        "LEAint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("lea_hi_imm_x__RuIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(scaleU5),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.LeaintPipeRegRegImmRegImmPred(dst, srcA, sb, srcC, scaleU5, pp, modifiers, guard))
        case _ => None
      }
  }
  object LeaintPipeRegRegRegImm extends Definition(
        "LeaintPipeRegRegRegImm",
        "LEAint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate)
        ),
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
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.LeaintPipeRegRegRegImm(dst, srcA, srcB, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object LeaintPipeRegRegRegImmImm extends Definition(
        "LeaintPipeRegRegRegImmImm",
        "LEAint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("lea_hi_imm__RRuI_RRI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.LeaintPipeRegRegRegImmImm(dst, srcA, srcB, sc, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object LeaintPipeRegRegRegImmImmPred extends Definition(
        "LeaintPipeRegRegRegImmImmPred",
        "LEAint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate),
          Slot("scaleU5", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("lea_hi_imm_x__RRuI_RRI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc),
              InstructionOperand.Immediate(scaleU5),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.LeaintPipeRegRegRegImmImmPred(dst, srcA, srcB, sc, scaleU5, pp, modifiers, guard))
        case _ => None
      }
  }
  object LeaintPipeRegRegRegImmPred extends Definition(
        "LeaintPipeRegRegRegImmPred",
        "LEAint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
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
            ) => Some(Instruction.LeaintPipeRegRegRegImmPred(dst, srcA, srcB, scaleU5, pp, modifiers, guard))
        case _ => None
      }
  }
  object LeaintPipeRegRegRegRegImm extends Definition(
        "LeaintPipeRegRegRegRegImm",
        "LEAint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("lea_hi_noimm__RRR_RRR", "lea_hi_noimm__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.LeaintPipeRegRegRegRegImm(dst, srcA, srcB, srcC, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object LeaintPipeRegRegRegRegImmPred extends Definition(
        "LeaintPipeRegRegRegRegImmPred",
        "LEAint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("lea_hi_noimm_x__RRR_RRR", "lea_hi_noimm_x__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(scaleU5),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.LeaintPipeRegRegRegRegImmPred(dst, srcA, srcB, srcC, scaleU5, pp, modifiers, guard))
        case _ => None
      }
  }
  object LepcReg
      extends Definition("LepcReg", "LEPC", Vec(Slot("dst", OperandKind.Register)), Vec("LEPC", "lepc_", "lepc__RRR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst)) => Some(Instruction.LepcReg(dst, modifiers, guard))
        case _ => None
      }
  }
  object LepcRegImm extends Definition(
        "LepcRegImm",
        "LEPC",
        Vec(Slot("dst", OperandKind.Register), Slot("sImm58", OperandKind.Immediate)),
        Vec("lepc__R_I_R", "lepc_rel_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sImm58)) =>
          Some(Instruction.LepcRegImm(dst, sImm58, modifiers, guard))
        case _ => None
      }
  }
  object LepcfxuPipeReg
      extends Definition("LepcfxuPipeReg", "LEPCfxu_pipe", Vec(Slot("dst", OperandKind.Register)), Vec("LEPC")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst)) => Some(Instruction.LepcfxuPipeReg(dst, modifiers, guard))
        case _ => None
      }
  }
  object LepcintPipeReg extends Definition(
        "LepcintPipeReg",
        "LEPCint_pipe",
        Vec(Slot("dst", OperandKind.Register)),
        Vec("lepc_", "lepc__RRR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst)) => Some(Instruction.LepcintPipeReg(dst, modifiers, guard))
        case _ => None
      }
  }
  object LepcintPipeRegImm extends Definition(
        "LepcintPipeRegImm",
        "LEPCint_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("sImm58", OperandKind.Immediate)),
        Vec("lepc__R_I_R", "lepc_rel_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sImm58)) =>
          Some(Instruction.LepcintPipeRegImm(dst, sImm58, modifiers, guard))
        case _ => None
      }
  }
  object LepcxluPipeReg
      extends Definition("LepcxluPipeReg", "LEPCxlu_pipe", Vec(Slot("dst", OperandKind.Register)), Vec("LEPC")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst)) => Some(Instruction.LepcxluPipeReg(dst, modifiers, guard))
        case _ => None
      }
  }
  object LongjmpNoOperands extends Definition("LongjmpNoOperands", "LONGJMP", Vec(), Vec("BRK", "LONGJMP")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.LongjmpNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object LongjmpbruPipeNoOperands
      extends Definition("LongjmpbruPipeNoOperands", "LONGJMPbru_pipe", Vec(), Vec("BRK", "LONGJMP")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.LongjmpbruPipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object LopPredRegRegAddressImm extends Definition(
        "LopPredRegRegAddressImm",
        "LOP",
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
            ) => Some(Instruction.LopPredRegRegAddressImm(pu, dst, srcA, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object LopPredRegRegAddressImmPred extends Definition(
        "LopPredRegRegAddressImmPred",
        "LOP",
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
            ) => Some(Instruction.LopPredRegRegAddressImmPred(pu, dst, srcA, srcB, sb_offset, pp, modifiers, guard))
        case _ => None
      }
  }
  object LopPredRegRegConst extends Definition(
        "LopPredRegRegConst",
        "LOP",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const_LOP", "lop_noimm_optionalPp__RCR_RCR")
      ) {
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
  object LopPredRegRegConstPred extends Definition(
        "LopPredRegRegConstPred",
        "LOP",
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
            ) => Some(Instruction.LopPredRegRegConstPred(pu, dst, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object LopPredRegRegImm extends Definition(
        "LopPredRegRegImm",
        "LOP",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate)
        ),
        Vec("Imm_LOP", "lop_imm_optionalPp_")
      ) {
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
  object LopPredRegRegImmPred extends Definition(
        "LopPredRegRegImmPred",
        "LOP",
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
            ) => Some(Instruction.LopPredRegRegImmPred(pu, dst, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object LopPredRegRegReg extends Definition(
        "LopPredRegRegReg",
        "LOP",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register)
        ),
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
  object LopPredRegRegRegPred extends Definition(
        "LopPredRegRegRegPred",
        "LOP",
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
            ) => Some(Instruction.LopPredRegRegRegPred(pu, dst, srcA, srcB, pp, modifiers, guard))
        case _ => None
      }
  }
  object LopRegRegAddressImm extends Definition(
        "LopRegRegAddressImm",
        "LOP",
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
            ) => Some(Instruction.LopRegRegAddressImm(dst, srcA, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object LopRegRegAddressImmPred extends Definition(
        "LopRegRegAddressImmPred",
        "LOP",
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
            ) => Some(Instruction.LopRegRegAddressImmPred(dst, srcA, srcB, sb_offset, pp, modifiers, guard))
        case _ => None
      }
  }
  object LopRegRegConst extends Definition(
        "LopRegRegConst",
        "LOP",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank)
        ),
        Vec("Const LOP", "Const_LOP", "lop_noimm_optionalPp__RCR_RCR")
      ) {
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
  object LopRegRegConstPred extends Definition(
        "LopRegRegConstPred",
        "LOP",
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
            ) => Some(Instruction.LopRegRegConstPred(dst, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object LopRegRegImm extends Definition(
        "LopRegRegImm",
        "LOP",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("Imm LOP", "Imm_LOP", "lop_imm_optionalPp_")
      ) {
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
  object LopRegRegImmPred extends Definition(
        "LopRegRegImmPred",
        "LOP",
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
            ) => Some(Instruction.LopRegRegImmPred(dst, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object LopRegRegReg extends Definition(
        "LopRegRegReg",
        "LOP",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
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
  object LopRegRegRegPred extends Definition(
        "LopRegRegRegPred",
        "LOP",
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
            ) => Some(Instruction.LopRegRegRegPred(dst, srcA, srcB, pp, modifiers, guard))
        case _ => None
      }
  }
  object Lop3PredRegRegAddressImmReg extends Definition(
        "Lop3PredRegRegAddressImmReg",
        "LOP3",
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
            ) => Some(Instruction.Lop3PredRegRegAddressImmReg(pu, dst, srcA, srcB, sb_offset, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Lop3PredRegRegAddressImmRegImm extends Definition(
        "Lop3PredRegRegAddressImmRegImm",
        "LOP3",
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
            ) =>
          Some(Instruction.Lop3PredRegRegAddressImmRegImm(pu, dst, srcA, srcB, sb_offset, srcC, imm8, modifiers, guard))
        case _ => None
      }
  }
  object Lop3PredRegRegAddressImmRegImmPred extends Definition(
        "Lop3PredRegRegAddressImmRegImmPred",
        "LOP3",
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
  object Lop3PredRegRegAddressImmRegPred extends Definition(
        "Lop3PredRegRegAddressImmRegPred",
        "LOP3",
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
            ) =>
          Some(Instruction.Lop3PredRegRegAddressImmRegPred(pu, dst, srcA, srcB, sb_offset, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object Lop3PredRegRegConstReg extends Definition(
        "Lop3PredRegRegConstReg",
        "LOP3",
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
            ) => Some(Instruction.Lop3PredRegRegConstReg(pu, dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Lop3PredRegRegConstRegImm extends Definition(
        "Lop3PredRegRegConstRegImm",
        "LOP3",
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
            ) => Some(Instruction.Lop3PredRegRegConstRegImm(pu, dst, srcA, sb, srcC, imm8, modifiers, guard))
        case _ => None
      }
  }
  object Lop3PredRegRegConstRegImmPred extends Definition(
        "Lop3PredRegRegConstRegImmPred",
        "LOP3",
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
            ) => Some(Instruction.Lop3PredRegRegConstRegImmPred(pu, dst, srcA, sb, srcC, imm8, pp, modifiers, guard))
        case _ => None
      }
  }
  object Lop3PredRegRegConstRegPred extends Definition(
        "Lop3PredRegRegConstRegPred",
        "LOP3",
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
            ) => Some(Instruction.Lop3PredRegRegConstRegPred(pu, dst, srcA, sb, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object Lop3PredRegRegImmReg extends Definition(
        "Lop3PredRegRegImmReg",
        "LOP3",
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
            ) => Some(Instruction.Lop3PredRegRegImmReg(pu, dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Lop3PredRegRegImmRegImm extends Definition(
        "Lop3PredRegRegImmRegImm",
        "LOP3",
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
            ) => Some(Instruction.Lop3PredRegRegImmRegImm(pu, dst, srcA, sb, srcC, imm8, modifiers, guard))
        case _ => None
      }
  }
  object Lop3PredRegRegImmRegImmPred extends Definition(
        "Lop3PredRegRegImmRegImmPred",
        "LOP3",
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
            ) => Some(Instruction.Lop3PredRegRegImmRegImmPred(pu, dst, srcA, sb, srcC, imm8, pp, modifiers, guard))
        case _ => None
      }
  }
  object Lop3PredRegRegImmRegPred extends Definition(
        "Lop3PredRegRegImmRegPred",
        "LOP3",
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
            ) => Some(Instruction.Lop3PredRegRegImmRegPred(pu, dst, srcA, sb, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object Lop3PredRegRegRegReg extends Definition(
        "Lop3PredRegRegRegReg",
        "LOP3",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
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
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("imm8", OperandKind.Immediate)
        ),
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
  object Lop3PredRegRegRegRegImmPred extends Definition(
        "Lop3PredRegRegRegRegImmPred",
        "LOP3",
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
            ) => Some(Instruction.Lop3PredRegRegRegRegImmPred(pu, dst, srcA, srcB, srcC, imm8, pp, modifiers, guard))
        case _ => None
      }
  }
  object Lop3PredRegRegRegRegPred extends Definition(
        "Lop3PredRegRegRegRegPred",
        "LOP3",
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
            ) => Some(Instruction.Lop3PredRegRegRegRegPred(pu, dst, srcA, srcB, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object Lop3RegRegAddressImmReg extends Definition(
        "Lop3RegRegAddressImmReg",
        "LOP3",
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
            ) => Some(Instruction.Lop3RegRegAddressImmReg(dst, srcA, srcB, sb_offset, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Lop3RegRegAddressImmRegImm extends Definition(
        "Lop3RegRegAddressImmRegImm",
        "LOP3",
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
            ) => Some(Instruction.Lop3RegRegAddressImmRegImm(dst, srcA, srcB, sb_offset, srcC, imm8, modifiers, guard))
        case _ => None
      }
  }
  object Lop3RegRegAddressImmRegImmPred extends Definition(
        "Lop3RegRegAddressImmRegImmPred",
        "LOP3",
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
            ) =>
          Some(Instruction.Lop3RegRegAddressImmRegImmPred(dst, srcA, srcB, sb_offset, srcC, imm8, pp, modifiers, guard))
        case _ => None
      }
  }
  object Lop3RegRegAddressImmRegPred extends Definition(
        "Lop3RegRegAddressImmRegPred",
        "LOP3",
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
            ) => Some(Instruction.Lop3RegRegAddressImmRegPred(dst, srcA, srcB, sb_offset, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object Lop3RegRegConstReg extends Definition(
        "Lop3RegRegConstReg",
        "LOP3",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("LOP3_Bconst", "lop3_noimm_optionalPp__RCR_RCR")
      ) {
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
  object Lop3RegRegConstRegImm extends Definition(
        "Lop3RegRegConstRegImm",
        "LOP3",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register),
          Slot("imm8", OperandKind.Immediate)
        ),
        Vec("LOP3_LUT_BConst", "lop3_lut_optionalPp__RCR_RCR")
      ) {
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
  object Lop3RegRegConstRegImmPred extends Definition(
        "Lop3RegRegConstRegImmPred",
        "LOP3",
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
            ) => Some(Instruction.Lop3RegRegConstRegImmPred(dst, srcA, sb, srcC, imm8, pp, modifiers, guard))
        case _ => None
      }
  }
  object Lop3RegRegConstRegPred extends Definition(
        "Lop3RegRegConstRegPred",
        "LOP3",
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
            ) => Some(Instruction.Lop3RegRegConstRegPred(dst, srcA, sb, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object Lop3RegRegImmReg extends Definition(
        "Lop3RegRegImmReg",
        "LOP3",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("LOP3_BImm", "lop3_imm_optionalPp__RIR_RIR")
      ) {
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
  object Lop3RegRegImmRegImm extends Definition(
        "Lop3RegRegImmRegImm",
        "LOP3",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("imm8", OperandKind.Immediate)
        ),
        Vec("LOP3_LUT_BImm", "lop3_lut_optionalPp__RuIR_RIR")
      ) {
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
  object Lop3RegRegImmRegImmPred extends Definition(
        "Lop3RegRegImmRegImmPred",
        "LOP3",
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
            ) => Some(Instruction.Lop3RegRegImmRegImmPred(dst, srcA, sb, srcC, imm8, pp, modifiers, guard))
        case _ => None
      }
  }
  object Lop3RegRegImmRegPred extends Definition(
        "Lop3RegRegImmRegPred",
        "LOP3",
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
            ) => Some(Instruction.Lop3RegRegImmRegPred(dst, srcA, sb, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object Lop3RegRegRegReg extends Definition(
        "Lop3RegRegRegReg",
        "LOP3",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
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
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("imm8", OperandKind.Immediate)
        ),
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
  object Lop3RegRegRegRegImmPred extends Definition(
        "Lop3RegRegRegRegImmPred",
        "LOP3",
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
            ) => Some(Instruction.Lop3RegRegRegRegImmPred(dst, srcA, srcB, srcC, imm8, pp, modifiers, guard))
        case _ => None
      }
  }
  object Lop3RegRegRegRegPred extends Definition(
        "Lop3RegRegRegRegPred",
        "LOP3",
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
            ) => Some(Instruction.Lop3RegRegRegRegPred(dst, srcA, srcB, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object Lop32iPredRegRegImm extends Definition(
        "Lop32iPredRegRegImm",
        "LOP32I",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate)
        ),
        Vec("lop32i_optionalPp_")
      ) {
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
  object Lop32iPredRegRegImmPred extends Definition(
        "Lop32iPredRegRegImmPred",
        "LOP32I",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("lop32i_")
      ) {
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
  object Lop32iRegRegImm extends Definition(
        "Lop32iRegRegImm",
        "LOP32I",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("LOP32I", "lop32i_optionalPp_")
      ) {
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
  object Lop32iRegRegImmPred extends Definition(
        "Lop32iRegRegImmPred",
        "LOP32I",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("lop32i_")
      ) {
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
  object Lop32ifauPipeRegRegImm extends Definition(
        "Lop32ifauPipeRegRegImm",
        "LOP32Ifau_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("LOP32I")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.Lop32ifauPipeRegRegImm(dst, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object Lop32ifmaiPipeRegRegImm extends Definition(
        "Lop32ifmaiPipeRegRegImm",
        "LOP32Ifmai_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("LOP32I")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.Lop32ifmaiPipeRegRegImm(dst, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object Lop32ifxuPipeRegRegImm extends Definition(
        "Lop32ifxuPipeRegRegImm",
        "LOP32Ifxu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("LOP32I")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.Lop32ifxuPipeRegRegImm(dst, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object Lop32iintPipePredRegRegImm extends Definition(
        "Lop32iintPipePredRegRegImm",
        "LOP32Iint_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate)
        ),
        Vec("lop32i_optionalPp_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.Lop32iintPipePredRegRegImm(pu, dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object Lop32iintPipePredRegRegImmPred extends Definition(
        "Lop32iintPipePredRegRegImmPred",
        "LOP32Iint_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("lop32i_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Lop32iintPipePredRegRegImmPred(pu, dst, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object Lop32iintPipeRegRegImm extends Definition(
        "Lop32iintPipeRegRegImm",
        "LOP32Iint_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("lop32i_optionalPp_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.Lop32iintPipeRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object Lop32iintPipeRegRegImmPred extends Definition(
        "Lop32iintPipeRegRegImmPred",
        "LOP32Iint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("lop32i_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Lop32iintPipeRegRegImmPred(dst, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object Lop32ixluPipeRegRegImm extends Definition(
        "Lop32ixluPipeRegRegImm",
        "LOP32Ixlu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("LOP32I")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.Lop32ixluPipeRegRegImm(dst, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object Lop3fmaiPipePredRegRegRegReg extends Definition(
        "Lop3fmaiPipePredRegRegRegReg",
        "LOP3fmai_pipe",
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
            ) => Some(Instruction.Lop3fmaiPipePredRegRegRegReg(dstPredicate, dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Lop3fmaiPipePredRegRegRegRegImm extends Definition(
        "Lop3fmaiPipePredRegRegRegRegImm",
        "LOP3fmai_pipe",
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
          Some(Instruction.Lop3fmaiPipePredRegRegRegRegImm(dstPredicate, dst, srcA, srcB, srcC, imm8, modifiers, guard))
        case _ => None
      }
  }
  object Lop3fmaiPipeRegRegConstReg extends Definition(
        "Lop3fmaiPipeRegRegConstReg",
        "LOP3fmai_pipe",
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
            ) => Some(Instruction.Lop3fmaiPipeRegRegConstReg(dst, srcA, srcConst, srcC, modifiers, guard))
        case _ => None
      }
  }
  val definitions: Vec[Definition] = Vec(
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
    LdcuudpPipeRegAddress,
    LdcuudpPipeRegAddressAddress,
    LdcuudpPipeRegAddressPred,
    LdcuudpPipeRegConst,
    LdcuudpPipeRegRegAddress,
    LdcuudpPipeRegRegAddressAddress,
    LdcuudpPipeRegRegAddressAddressImm,
    LdcuudpPipeRegRegAddressImm,
    LdcuudpPipeRegRegAddressImmPred,
    LdcuudpPipeRegRegAddressPred,
    LdcuudpPipeRegRegConst,
    LdcuudpPipeRegRegConstImm,
    LdcmioPipeRegAddressAddress,
    LdcmioPipeRegConst,
    LdcxluPipeRegConst,
    LdgPredRegAddress,
    LdgPredRegAddressPred,
    LdgRegAddress,
    LdgRegAddressPred,
    LdgRegRegAddress,
    LdgRegRegAddressImm,
    LdgRegRegAddressImmPred,
    LdgRegRegAddressPred,
    LdgdepbarNoOperands,
    LdgdepbarmioPipeNoOperands,
    LdgmcRegAddress,
    LdgmcmioPipeRegAddress,
    LdgstsAddressAddress,
    LdgstsAddressAddressPred,
    LdgstsmioPipeAddressAddress,
    LdgstsmioPipeAddressAddressPred,
    LdgmioPipePredRegAddress,
    LdgmioPipePredRegAddressPred,
    LdgmioPipeRegAddress,
    LdgmioPipeRegAddressPred,
    LdgmioPipeRegRegAddress,
    LdgmioPipeRegRegAddressImm,
    LdgmioPipeRegRegAddressImmPred,
    LdgmioPipeRegRegAddressPred,
    LdgtexPipeRegAddress,
    LdlRegAddress,
    LdlaguPipeRegAddress,
    LdlmioPipeRegAddress,
    LdsPredRegAddress,
    LdsRegAddress,
    LdslkPredRegAddress,
    LdslkaguPipePredRegAddress,
    LdsmRegAddress,
    LdsmmioPipeRegAddress,
    LdsaguPipeRegAddress,
    LdsmioPipePredRegAddress,
    LdsmioPipeRegAddress,
    LdtRegAddress,
    LdtmRegAddress,
    LdtmRegRegAddress,
    LdtmudpPipeRegAddress,
    LdtmudpPipeRegRegAddress,
    LdtramRegAddress,
    LdtrammioPipeRegAddress,
    LdtudpPipeRegAddress,
    LdaguPipeRegAddress,
    LdmioPipePredRegAddress,
    LdmioPipePredRegAddressPred,
    LdmioPipeRegAddress,
    LdmioPipeRegAddressPred,
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
    LeaRegRegReg,
    LeaRegRegRegImm,
    LeaRegRegRegImmImm,
    LeaRegRegRegImmImmPred,
    LeaRegRegRegImmPred,
    LeaRegRegRegReg,
    LeaRegRegRegRegImm,
    LeaRegRegRegRegImmPred,
    LeafxuPipePredRegRegConst,
    LeafxuPipePredRegRegConstImm,
    LeafxuPipePredRegRegConstReg,
    LeafxuPipePredRegRegConstRegImm,
    LeafxuPipePredRegRegImm,
    LeafxuPipePredRegRegImmImm,
    LeafxuPipePredRegRegReg,
    LeafxuPipePredRegRegRegImm,
    LeafxuPipePredRegRegRegReg,
    LeafxuPipePredRegRegRegRegImm,
    LeafxuPipeRegRegConst,
    LeafxuPipeRegRegConstImm,
    LeafxuPipeRegRegConstReg,
    LeafxuPipeRegRegConstRegImm,
    LeafxuPipeRegRegImm,
    LeafxuPipeRegRegImmImm,
    LeafxuPipeRegRegReg,
    LeafxuPipeRegRegRegImm,
    LeafxuPipeRegRegRegReg,
    LeafxuPipeRegRegRegRegImm,
    LeaintPipeRegPredRegAddressImmImm,
    LeaintPipeRegPredRegAddressImmImmPred,
    LeaintPipeRegPredRegAddressImmRegImm,
    LeaintPipeRegPredRegAddressImmRegImmPred,
    LeaintPipeRegPredRegConstImm,
    LeaintPipeRegPredRegConstImmPred,
    LeaintPipeRegPredRegConstRegImm,
    LeaintPipeRegPredRegConstRegImmPred,
    LeaintPipeRegPredRegImmImm,
    LeaintPipeRegPredRegImmImmPred,
    LeaintPipeRegPredRegImmRegImm,
    LeaintPipeRegPredRegImmRegImmPred,
    LeaintPipeRegPredRegRegImm,
    LeaintPipeRegPredRegRegImmImm,
    LeaintPipeRegPredRegRegImmImmPred,
    LeaintPipeRegPredRegRegImmPred,
    LeaintPipeRegPredRegRegRegImm,
    LeaintPipeRegPredRegRegRegImmPred,
    LeaintPipeRegRegAddressImmImm,
    LeaintPipeRegRegAddressImmImmPred,
    LeaintPipeRegRegAddressImmRegImm,
    LeaintPipeRegRegAddressImmRegImmPred,
    LeaintPipeRegRegConstImm,
    LeaintPipeRegRegConstImmPred,
    LeaintPipeRegRegConstRegImm,
    LeaintPipeRegRegConstRegImmPred,
    LeaintPipeRegRegImmImm,
    LeaintPipeRegRegImmImmPred,
    LeaintPipeRegRegImmRegImm,
    LeaintPipeRegRegImmRegImmPred,
    LeaintPipeRegRegRegImm,
    LeaintPipeRegRegRegImmImm,
    LeaintPipeRegRegRegImmImmPred,
    LeaintPipeRegRegRegImmPred,
    LeaintPipeRegRegRegRegImm,
    LeaintPipeRegRegRegRegImmPred,
    LepcReg,
    LepcRegImm,
    LepcfxuPipeReg,
    LepcintPipeReg,
    LepcintPipeRegImm,
    LepcxluPipeReg,
    LongjmpNoOperands,
    LongjmpbruPipeNoOperands,
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
    Lop32ifauPipeRegRegImm,
    Lop32ifmaiPipeRegRegImm,
    Lop32ifxuPipeRegRegImm,
    Lop32iintPipePredRegRegImm,
    Lop32iintPipePredRegRegImmPred,
    Lop32iintPipeRegRegImm,
    Lop32iintPipeRegRegImmPred,
    Lop32ixluPipeRegRegImm,
    Lop3fmaiPipePredRegRegRegReg,
    Lop3fmaiPipePredRegRegRegRegImm,
    Lop3fmaiPipeRegRegConstReg
  )
}
