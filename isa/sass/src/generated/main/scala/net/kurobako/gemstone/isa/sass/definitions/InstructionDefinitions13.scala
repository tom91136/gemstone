// Generated SASS instruction definitions and checked operand constructors. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.sass
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import Instruction.*
@javax.annotation.processing.Generated(Array("sbt codegen"))
object InstructionDefinitions13 {
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
  object UfsetpPredPredRegImmPred extends Definition(
        "UfsetpPredPredRegImmPred",
        "UFSETP",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("uPv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ufsetp__URsIUR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Predicate(uPv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UfsetpPredPredRegImmPred(uPu, uPv, srcA, sb, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UfsetpPredPredRegRegPred extends Definition(
        "UfsetpPredPredRegRegPred",
        "UFSETP",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("uPv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ufsetp__URURUR_UUU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Predicate(uPv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UfsetpPredPredRegRegPred(uPu, uPv, srcA, srcB, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UfsetpPredRegImm extends Definition(
        "UfsetpPredRegImm",
        "UFSETP",
        Vec(Slot("uPu", OperandKind.Predicate), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("ufsetp_simple__URsIUR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.UfsetpPredRegImm(uPu, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object UfsetpPredRegReg extends Definition(
        "UfsetpPredRegReg",
        "UFSETP",
        Vec(Slot("uPu", OperandKind.Predicate), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("ufsetp_simple__URURUR_UUU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.UfsetpPredRegReg(uPu, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UfsetpudpPipePredPredRegImmPred extends Definition(
        "UfsetpudpPipePredPredRegImmPred",
        "UFSETPudp_pipe",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("uPv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ufsetp__URsIUR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Predicate(uPv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UfsetpudpPipePredPredRegImmPred(uPu, uPv, srcA, sb, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UfsetpudpPipePredPredRegRegPred extends Definition(
        "UfsetpudpPipePredPredRegRegPred",
        "UFSETPudp_pipe",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("uPv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ufsetp__URURUR_UUU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Predicate(uPv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UfsetpudpPipePredPredRegRegPred(uPu, uPv, srcA, srcB, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UfsetpudpPipePredRegImm extends Definition(
        "UfsetpudpPipePredRegImm",
        "UFSETPudp_pipe",
        Vec(Slot("uPu", OperandKind.Predicate), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("ufsetp_simple__URsIUR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.UfsetpudpPipePredRegImm(uPu, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object UfsetpudpPipePredRegReg extends Definition(
        "UfsetpudpPipePredRegReg",
        "UFSETPudp_pipe",
        Vec(Slot("uPu", OperandKind.Predicate), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("ufsetp_simple__URURUR_UUU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.UfsetpudpPipePredRegReg(uPu, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UfsetudpPipeRegRegImm extends Definition(
        "UfsetudpPipeRegRegImm",
        "UFSETudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("ufset_simple__URsIUR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.UfsetudpPipeRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object UfsetudpPipeRegRegImmPred extends Definition(
        "UfsetudpPipeRegRegImmPred",
        "UFSETudp_pipe",
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
            ) => Some(Instruction.UfsetudpPipeRegRegImmPred(dst, srcA, sb, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UfsetudpPipeRegRegReg extends Definition(
        "UfsetudpPipeRegRegReg",
        "UFSETudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("ufset_simple__URURUR_UUU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.UfsetudpPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UfsetudpPipeRegRegRegPred extends Definition(
        "UfsetudpPipeRegRegRegPred",
        "UFSETudp_pipe",
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
            ) => Some(Instruction.UfsetudpPipeRegRegRegPred(dst, srcA, srcB, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UgetnextworkidAddressAddress extends Definition(
        "UgetnextworkidAddressAddress",
        "UGETNEXTWORKID",
        Vec(Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Address)),
        Vec("ugetnextworkid_", "ugetnextworkid_one_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA), InstructionOperand.Address(srcB)) =>
          Some(Instruction.UgetnextworkidAddressAddress(srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UgetnextworkidudpPipeAddressAddress extends Definition(
        "UgetnextworkidudpPipeAddressAddress",
        "UGETNEXTWORKIDudp_pipe",
        Vec(Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Address)),
        Vec("ugetnextworkid_", "ugetnextworkid_one_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA), InstructionOperand.Address(srcB)) =>
          Some(Instruction.UgetnextworkidudpPipeAddressAddress(srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object Ui2fRegImm extends Definition(
        "Ui2fRegImm",
        "UI2F",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("ui2f__IS_32b", "ui2f__IU_32b")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.Ui2fRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object Ui2fRegReg extends Definition(
        "Ui2fRegReg",
        "UI2F",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("ui2f__UR_16b", "ui2f__UR_32b", "ui2f__UR_8b")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.Ui2fRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object Ui2fpRegImm extends Definition(
        "Ui2fpRegImm",
        "UI2FP",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("ui2fp__URsIR", "ui2fp__URuIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.Ui2fpRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object Ui2fpRegReg extends Definition(
        "Ui2fpRegReg",
        "UI2FP",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("ui2fp__UUU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.Ui2fpRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object Ui2fpudpPipeRegImm extends Definition(
        "Ui2fpudpPipeRegImm",
        "UI2FPudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("ui2fp__URsIR", "ui2fp__URuIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.Ui2fpudpPipeRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object Ui2fpudpPipeRegReg extends Definition(
        "Ui2fpudpPipeRegReg",
        "UI2FPudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("ui2fp__UUU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.Ui2fpudpPipeRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object Ui2fudpPipeRegImm extends Definition(
        "Ui2fudpPipeRegImm",
        "UI2Fudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("ui2f__IS_32b", "ui2f__IU_32b")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.Ui2fudpPipeRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object Ui2fudpPipeRegReg extends Definition(
        "Ui2fudpPipeRegReg",
        "UI2Fudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("ui2f__UR_16b", "ui2f__UR_32b", "ui2f__UR_8b")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.Ui2fudpPipeRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object Ui2iRegImm extends Definition(
        "Ui2iRegImm",
        "UI2I",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("ui2i__URsIUR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.Ui2iRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object Ui2iRegReg extends Definition(
        "Ui2iRegReg",
        "UI2I",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("ui2i__URURUR_UUU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.Ui2iRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object Ui2ipRegRegImmReg extends Definition(
        "Ui2ipRegRegImmReg",
        "UI2IP",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("ui2ip_24__URsIUR_URRI", "ui2ip_28__URsIUR_URRI", "ui2ip__URsIUR_URRI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Ui2ipRegRegImmReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Ui2ipRegRegRegReg extends Definition(
        "Ui2ipRegRegRegReg",
        "UI2IP",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("ui2ip_24__URURUR_UUU", "ui2ip_28__URURUR_UUU", "ui2ip__URURUR_UUU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Ui2ipRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Ui2ipudpPipeRegRegImmReg extends Definition(
        "Ui2ipudpPipeRegRegImmReg",
        "UI2IPudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("ui2ip_24__URsIUR_URRI", "ui2ip_28__URsIUR_URRI", "ui2ip__URsIUR_URRI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Ui2ipudpPipeRegRegImmReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Ui2ipudpPipeRegRegRegReg extends Definition(
        "Ui2ipudpPipeRegRegRegReg",
        "UI2IPudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("ui2ip_24__URURUR_UUU", "ui2ip_28__URURUR_UUU", "ui2ip__URURUR_UUU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Ui2ipudpPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Ui2iudpPipeRegImm extends Definition(
        "Ui2iudpPipeRegImm",
        "UI2Iudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("ui2i__URsIUR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.Ui2iudpPipeRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object Ui2iudpPipeRegReg extends Definition(
        "Ui2iudpPipeRegReg",
        "UI2Iudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("ui2i__URURUR_UUU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.Ui2iudpPipeRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UiabsRegImm extends Definition(
        "UiabsRegImm",
        "UIABS",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("uiabs__URsIUR_I")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.UiabsRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object UiabsRegReg extends Definition(
        "UiabsRegReg",
        "UIABS",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("uiabs__URURUR_UR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.UiabsRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UiabsudpPipeRegImm extends Definition(
        "UiabsudpPipeRegImm",
        "UIABSudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("uiabs__URsIUR_I")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.UiabsudpPipeRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object UiabsudpPipeRegReg extends Definition(
        "UiabsudpPipeRegReg",
        "UIABSudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("uiabs__URURUR_UR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.UiabsudpPipeRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object Uiadd3RegPredPredRegImmReg extends Definition(
        "Uiadd3RegPredPredRegImmReg",
        "UIADD3",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uPu", OperandKind.Predicate),
          Slot("uPv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("uiadd3_64__URsIUR_RIR", "uiadd3__URsIUR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Predicate(uPv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Uiadd3RegPredPredRegImmReg(dst, uPu, uPv, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Uiadd3RegPredPredRegImmRegPredPred extends Definition(
        "Uiadd3RegPredPredRegImmRegPredPred",
        "UIADD3",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uPu", OperandKind.Predicate),
          Slot("uPv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("uPq", OperandKind.Predicate)
        ),
        Vec("uiadd3_64_x__URsIUR_RIR", "uiadd3_x__URsIUR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Predicate(uPv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Predicate(uPq)
            ) => Some(Instruction.Uiadd3RegPredPredRegImmRegPredPred(
            dst,
            uPu,
            uPv,
            srcA,
            sb,
            srcC,
            uPp,
            uPq,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object Uiadd3RegPredPredRegRegReg extends Definition(
        "Uiadd3RegPredPredRegRegReg",
        "UIADD3",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uPu", OperandKind.Predicate),
          Slot("uPv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("uiadd3_64__URURUR_URURUR", "uiadd3__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Predicate(uPv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Uiadd3RegPredPredRegRegReg(dst, uPu, uPv, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Uiadd3RegPredPredRegRegRegPredPred extends Definition(
        "Uiadd3RegPredPredRegRegRegPredPred",
        "UIADD3",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uPu", OperandKind.Predicate),
          Slot("uPv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("uPq", OperandKind.Predicate)
        ),
        Vec("uiadd3_64_x__URURUR_URURUR", "uiadd3_x__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Predicate(uPv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Predicate(uPq)
            ) => Some(Instruction.Uiadd3RegPredPredRegRegRegPredPred(
            dst,
            uPu,
            uPv,
            srcA,
            srcB,
            srcC,
            uPp,
            uPq,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object Uiadd3RegPredRegImmReg extends Definition(
        "Uiadd3RegPredRegImmReg",
        "UIADD3",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uPu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("uiadd3_64__URsIUR_RIR", "uiadd3__URsIUR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Uiadd3RegPredRegImmReg(dst, uPu, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Uiadd3RegPredRegImmRegPredPred extends Definition(
        "Uiadd3RegPredRegImmRegPredPred",
        "UIADD3",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uPu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("uPq", OperandKind.Predicate)
        ),
        Vec("uiadd3_64_x__URsIUR_RIR", "uiadd3_x__URsIUR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Predicate(uPq)
            ) => Some(Instruction.Uiadd3RegPredRegImmRegPredPred(dst, uPu, srcA, sb, srcC, uPp, uPq, modifiers, guard))
        case _ => None
      }
  }
  object Uiadd3RegPredRegRegReg extends Definition(
        "Uiadd3RegPredRegRegReg",
        "UIADD3",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uPu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("uiadd3_64__URURUR_URURUR", "uiadd3__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Uiadd3RegPredRegRegReg(dst, uPu, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Uiadd3RegPredRegRegRegPredPred extends Definition(
        "Uiadd3RegPredRegRegRegPredPred",
        "UIADD3",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uPu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("uPq", OperandKind.Predicate)
        ),
        Vec("uiadd3_64_x__URURUR_URURUR", "uiadd3_x__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Predicate(uPq)
            ) =>
          Some(Instruction.Uiadd3RegPredRegRegRegPredPred(dst, uPu, srcA, srcB, srcC, uPp, uPq, modifiers, guard))
        case _ => None
      }
  }
  object Uiadd3RegRegImmReg extends Definition(
        "Uiadd3RegRegImmReg",
        "UIADD3",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("uiadd3_64__URsIUR_RIR", "uiadd3__URsIUR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Uiadd3RegRegImmReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Uiadd3RegRegImmRegPredPred extends Definition(
        "Uiadd3RegRegImmRegPredPred",
        "UIADD3",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("uPq", OperandKind.Predicate)
        ),
        Vec("uiadd3_64_x__URsIUR_RIR", "uiadd3_x__URsIUR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Predicate(uPq)
            ) => Some(Instruction.Uiadd3RegRegImmRegPredPred(dst, srcA, sb, srcC, uPp, uPq, modifiers, guard))
        case _ => None
      }
  }
  object Uiadd3RegRegRegReg extends Definition(
        "Uiadd3RegRegRegReg",
        "UIADD3",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("uiadd3_64__URURUR_URURUR", "uiadd3__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Uiadd3RegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Uiadd3RegRegRegRegPredPred extends Definition(
        "Uiadd3RegRegRegRegPredPred",
        "UIADD3",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("uPq", OperandKind.Predicate)
        ),
        Vec("uiadd3_64_x__URURUR_URURUR", "uiadd3_x__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Predicate(uPq)
            ) => Some(Instruction.Uiadd3RegRegRegRegPredPred(dst, srcA, srcB, srcC, uPp, uPq, modifiers, guard))
        case _ => None
      }
  }
  object Uiadd3udpPipeRegPredPredRegImmReg extends Definition(
        "Uiadd3udpPipeRegPredPredRegImmReg",
        "UIADD3udp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uPu", OperandKind.Predicate),
          Slot("uPv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("uiadd3__URsIUR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Predicate(uPv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Uiadd3udpPipeRegPredPredRegImmReg(dst, uPu, uPv, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Uiadd3udpPipeRegPredPredRegImmRegPredPred extends Definition(
        "Uiadd3udpPipeRegPredPredRegImmRegPredPred",
        "UIADD3udp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uPu", OperandKind.Predicate),
          Slot("uPv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("uPq", OperandKind.Predicate)
        ),
        Vec("uiadd3_x__URsIUR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Predicate(uPv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Predicate(uPq)
            ) => Some(Instruction.Uiadd3udpPipeRegPredPredRegImmRegPredPred(
            dst,
            uPu,
            uPv,
            srcA,
            sb,
            srcC,
            uPp,
            uPq,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object Uiadd3udpPipeRegPredPredRegRegReg extends Definition(
        "Uiadd3udpPipeRegPredPredRegRegReg",
        "UIADD3udp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uPu", OperandKind.Predicate),
          Slot("uPv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("uiadd3__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Predicate(uPv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Uiadd3udpPipeRegPredPredRegRegReg(dst, uPu, uPv, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Uiadd3udpPipeRegPredPredRegRegRegPredPred extends Definition(
        "Uiadd3udpPipeRegPredPredRegRegRegPredPred",
        "UIADD3udp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uPu", OperandKind.Predicate),
          Slot("uPv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("uPq", OperandKind.Predicate)
        ),
        Vec("uiadd3_x__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Predicate(uPv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Predicate(uPq)
            ) => Some(Instruction.Uiadd3udpPipeRegPredPredRegRegRegPredPred(
            dst,
            uPu,
            uPv,
            srcA,
            srcB,
            srcC,
            uPp,
            uPq,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object Uiadd3udpPipeRegPredRegImmReg extends Definition(
        "Uiadd3udpPipeRegPredRegImmReg",
        "UIADD3udp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uPu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("uiadd3__URsIUR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Uiadd3udpPipeRegPredRegImmReg(dst, uPu, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Uiadd3udpPipeRegPredRegImmRegPredPred extends Definition(
        "Uiadd3udpPipeRegPredRegImmRegPredPred",
        "UIADD3udp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uPu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("uPq", OperandKind.Predicate)
        ),
        Vec("uiadd3_x__URsIUR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Predicate(uPq)
            ) =>
          Some(Instruction.Uiadd3udpPipeRegPredRegImmRegPredPred(dst, uPu, srcA, sb, srcC, uPp, uPq, modifiers, guard))
        case _ => None
      }
  }
  object Uiadd3udpPipeRegPredRegRegReg extends Definition(
        "Uiadd3udpPipeRegPredRegRegReg",
        "UIADD3udp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uPu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("uiadd3__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Uiadd3udpPipeRegPredRegRegReg(dst, uPu, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Uiadd3udpPipeRegPredRegRegRegPredPred extends Definition(
        "Uiadd3udpPipeRegPredRegRegRegPredPred",
        "UIADD3udp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uPu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("uPq", OperandKind.Predicate)
        ),
        Vec("uiadd3_x__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Predicate(uPq)
            ) => Some(Instruction.Uiadd3udpPipeRegPredRegRegRegPredPred(
            dst,
            uPu,
            srcA,
            srcB,
            srcC,
            uPp,
            uPq,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object Uiadd3udpPipeRegRegImmReg extends Definition(
        "Uiadd3udpPipeRegRegImmReg",
        "UIADD3udp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("uiadd3__URsIUR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Uiadd3udpPipeRegRegImmReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Uiadd3udpPipeRegRegImmRegPredPred extends Definition(
        "Uiadd3udpPipeRegRegImmRegPredPred",
        "UIADD3udp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("uPq", OperandKind.Predicate)
        ),
        Vec("uiadd3_x__URsIUR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Predicate(uPq)
            ) => Some(Instruction.Uiadd3udpPipeRegRegImmRegPredPred(dst, srcA, sb, srcC, uPp, uPq, modifiers, guard))
        case _ => None
      }
  }
  object Uiadd3udpPipeRegRegRegReg extends Definition(
        "Uiadd3udpPipeRegRegRegReg",
        "UIADD3udp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("uiadd3__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Uiadd3udpPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Uiadd3udpPipeRegRegRegRegPredPred extends Definition(
        "Uiadd3udpPipeRegRegRegRegPredPred",
        "UIADD3udp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("uPq", OperandKind.Predicate)
        ),
        Vec("uiadd3_x__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Predicate(uPq)
            ) => Some(Instruction.Uiadd3udpPipeRegRegRegRegPredPred(dst, srcA, srcB, srcC, uPp, uPq, modifiers, guard))
        case _ => None
      }
  }
  object UimadRegPredRegImmReg extends Definition(
        "UimadRegPredRegImmReg",
        "UIMAD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uPu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("uimad_hi__URsIUR_URIUR", "uimad_wide__URsIUR_URIUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.UimadRegPredRegImmReg(dst, uPu, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object UimadRegPredRegImmRegPred extends Definition(
        "UimadRegPredRegImmRegPred",
        "UIMAD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uPu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("uimad_hi_x__URsIUR_URIUR", "uimad_wide_x__URsIUR_URIUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UimadRegPredRegImmRegPred(dst, uPu, srcA, sb, srcC, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UimadRegPredRegRegReg extends Definition(
        "UimadRegPredRegRegReg",
        "UIMAD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uPu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("uimad_hi__URURUR_URURUR", "uimad_wide__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.UimadRegPredRegRegReg(dst, uPu, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object UimadRegPredRegRegRegPred extends Definition(
        "UimadRegPredRegRegRegPred",
        "UIMAD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uPu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("uimad_hi_x__URURUR_URURUR", "uimad_wide_x__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UimadRegPredRegRegRegPred(dst, uPu, srcA, srcB, srcC, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UimadRegRegImmReg extends Definition(
        "UimadRegRegImmReg",
        "UIMAD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("uimad__URsIUR_URIUR", "uimad_hi__URsIUR_URIUR", "uimad_wide__URsIUR_URIUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.UimadRegRegImmReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object UimadRegRegImmRegPred extends Definition(
        "UimadRegRegImmRegPred",
        "UIMAD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("uimad_hi_x__URsIUR_URIUR", "uimad_wide_x__URsIUR_URIUR", "uimad_x__URsIUR_URIUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UimadRegRegImmRegPred(dst, srcA, sb, srcC, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UimadRegRegRegImm extends Definition(
        "UimadRegRegRegImm",
        "UIMAD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate)
        ),
        Vec("uimad__URURsI_URURI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.UimadRegRegRegImm(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object UimadRegRegRegImmPred extends Definition(
        "UimadRegRegRegImmPred",
        "UIMAD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("uimad_x__URURsI_URURI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UimadRegRegRegImmPred(dst, srcA, srcB, sc, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UimadRegRegRegReg extends Definition(
        "UimadRegRegRegReg",
        "UIMAD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("uimad__URURUR_URURUR", "uimad_hi__URURUR_URURUR", "uimad_wide__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.UimadRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object UimadRegRegRegRegPred extends Definition(
        "UimadRegRegRegRegPred",
        "UIMAD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("uimad_hi_x__URURUR_URURUR", "uimad_wide_x__URURUR_URURUR", "uimad_x__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UimadRegRegRegRegPred(dst, srcA, srcB, srcC, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UimadudpPipeRegPredRegImmReg extends Definition(
        "UimadudpPipeRegPredRegImmReg",
        "UIMADudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uPu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("uimad_hi__URsIUR_URIUR", "uimad_wide__URsIUR_URIUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.UimadudpPipeRegPredRegImmReg(dst, uPu, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object UimadudpPipeRegPredRegImmRegPred extends Definition(
        "UimadudpPipeRegPredRegImmRegPred",
        "UIMADudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uPu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("uimad_hi_x__URsIUR_URIUR", "uimad_wide_x__URsIUR_URIUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UimadudpPipeRegPredRegImmRegPred(dst, uPu, srcA, sb, srcC, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UimadudpPipeRegPredRegRegReg extends Definition(
        "UimadudpPipeRegPredRegRegReg",
        "UIMADudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uPu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("uimad_hi__URURUR_URURUR", "uimad_wide__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.UimadudpPipeRegPredRegRegReg(dst, uPu, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object UimadudpPipeRegPredRegRegRegPred extends Definition(
        "UimadudpPipeRegPredRegRegRegPred",
        "UIMADudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uPu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("uimad_hi_x__URURUR_URURUR", "uimad_wide_x__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UimadudpPipeRegPredRegRegRegPred(dst, uPu, srcA, srcB, srcC, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UimadudpPipeRegRegImmReg extends Definition(
        "UimadudpPipeRegRegImmReg",
        "UIMADudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("uimad__URsIUR_URIUR", "uimad_hi__URsIUR_URIUR", "uimad_wide__URsIUR_URIUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.UimadudpPipeRegRegImmReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object UimadudpPipeRegRegImmRegPred extends Definition(
        "UimadudpPipeRegRegImmRegPred",
        "UIMADudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("uimad_hi_x__URsIUR_URIUR", "uimad_wide_x__URsIUR_URIUR", "uimad_x__URsIUR_URIUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UimadudpPipeRegRegImmRegPred(dst, srcA, sb, srcC, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UimadudpPipeRegRegRegImm extends Definition(
        "UimadudpPipeRegRegRegImm",
        "UIMADudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate)
        ),
        Vec("uimad__URURsI_URURI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.UimadudpPipeRegRegRegImm(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object UimadudpPipeRegRegRegImmPred extends Definition(
        "UimadudpPipeRegRegRegImmPred",
        "UIMADudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("uimad_x__URURsI_URURI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UimadudpPipeRegRegRegImmPred(dst, srcA, srcB, sc, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UimadudpPipeRegRegRegReg extends Definition(
        "UimadudpPipeRegRegRegReg",
        "UIMADudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("uimad__URURUR_URURUR", "uimad_hi__URURUR_URURUR", "uimad_wide__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.UimadudpPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object UimadudpPipeRegRegRegRegPred extends Definition(
        "UimadudpPipeRegRegRegRegPred",
        "UIMADudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("uimad_hi_x__URURUR_URURUR", "uimad_wide_x__URURUR_URURUR", "uimad_x__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UimadudpPipeRegRegRegRegPred(dst, srcA, srcB, srcC, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UimnmxPredPredRegRegImmPred extends Definition(
        "UimnmxPredPredRegRegImmPred",
        "UIMNMX",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("uPv", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("uimnmx_64_nopred__URIR_URsIUR", "uimnmx_nopred__URIR_URsIUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Predicate(uPv),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UimnmxPredPredRegRegImmPred(uPu, uPv, dst, srcA, sb, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UimnmxPredPredRegRegImmPredPred extends Definition(
        "UimnmxPredPredRegRegImmPredPred",
        "UIMNMX",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("uPv", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate),
          Slot("uPq", OperandKind.Predicate)
        ),
        Vec("uimnmx_64__URIR_URsIUR", "uimnmx__URIR_URsIUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Predicate(uPv),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Predicate(uPq)
            ) => Some(Instruction.UimnmxPredPredRegRegImmPredPred(uPu, uPv, dst, srcA, sb, uPp, uPq, modifiers, guard))
        case _ => None
      }
  }
  object UimnmxPredPredRegRegRegPred extends Definition(
        "UimnmxPredPredRegRegRegPred",
        "UIMNMX",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("uPv", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("uimnmx_64_nopred__UUU_URURUR", "uimnmx_nopred__UUU_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Predicate(uPv),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UimnmxPredPredRegRegRegPred(uPu, uPv, dst, srcA, srcB, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UimnmxPredPredRegRegRegPredPred extends Definition(
        "UimnmxPredPredRegRegRegPredPred",
        "UIMNMX",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("uPv", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("uPq", OperandKind.Predicate)
        ),
        Vec("uimnmx_64__UUU_URURUR", "uimnmx__UUU_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Predicate(uPv),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Predicate(uPq)
            ) =>
          Some(Instruction.UimnmxPredPredRegRegRegPredPred(uPu, uPv, dst, srcA, srcB, uPp, uPq, modifiers, guard))
        case _ => None
      }
  }
  object UimnmxPredRegRegImmPred extends Definition(
        "UimnmxPredRegRegImmPred",
        "UIMNMX",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("uimnmx_64_nopred__URIR_URsIUR", "uimnmx_nopred__URIR_URsIUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UimnmxPredRegRegImmPred(uPu, dst, srcA, sb, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UimnmxPredRegRegImmPredPred extends Definition(
        "UimnmxPredRegRegImmPredPred",
        "UIMNMX",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate),
          Slot("uPq", OperandKind.Predicate)
        ),
        Vec("uimnmx_64__URIR_URsIUR", "uimnmx__URIR_URsIUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Predicate(uPq)
            ) => Some(Instruction.UimnmxPredRegRegImmPredPred(uPu, dst, srcA, sb, uPp, uPq, modifiers, guard))
        case _ => None
      }
  }
  object UimnmxPredRegRegRegPred extends Definition(
        "UimnmxPredRegRegRegPred",
        "UIMNMX",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("uimnmx_64_nopred__UUU_URURUR", "uimnmx_nopred__UUU_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UimnmxPredRegRegRegPred(uPu, dst, srcA, srcB, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UimnmxPredRegRegRegPredPred extends Definition(
        "UimnmxPredRegRegRegPredPred",
        "UIMNMX",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("uPq", OperandKind.Predicate)
        ),
        Vec("uimnmx_64__UUU_URURUR", "uimnmx__UUU_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Predicate(uPq)
            ) => Some(Instruction.UimnmxPredRegRegRegPredPred(uPu, dst, srcA, srcB, uPp, uPq, modifiers, guard))
        case _ => None
      }
  }
  object UimnmxRegRegImmPred extends Definition(
        "UimnmxRegRegImmPred",
        "UIMNMX",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("uimnmx_64_nopred__URIR_URsIUR", "uimnmx_nopred__URIR_URsIUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UimnmxRegRegImmPred(dst, srcA, sb, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UimnmxRegRegImmPredPred extends Definition(
        "UimnmxRegRegImmPredPred",
        "UIMNMX",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate),
          Slot("uPq", OperandKind.Predicate)
        ),
        Vec("uimnmx_64__URIR_URsIUR", "uimnmx__URIR_URsIUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Predicate(uPq)
            ) => Some(Instruction.UimnmxRegRegImmPredPred(dst, srcA, sb, uPp, uPq, modifiers, guard))
        case _ => None
      }
  }
  object UimnmxRegRegRegPred extends Definition(
        "UimnmxRegRegRegPred",
        "UIMNMX",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("uimnmx_64_nopred__UUU_URURUR", "uimnmx_nopred__UUU_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UimnmxRegRegRegPred(dst, srcA, srcB, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UimnmxRegRegRegPredPred extends Definition(
        "UimnmxRegRegRegPredPred",
        "UIMNMX",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("uPq", OperandKind.Predicate)
        ),
        Vec("uimnmx_64__UUU_URURUR", "uimnmx__UUU_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Predicate(uPq)
            ) => Some(Instruction.UimnmxRegRegRegPredPred(dst, srcA, srcB, uPp, uPq, modifiers, guard))
        case _ => None
      }
  }
  object UimnmxudpPipePredPredRegRegImmPred extends Definition(
        "UimnmxudpPipePredPredRegRegImmPred",
        "UIMNMXudp_pipe",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("uPv", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("uimnmx_64_nopred__URIR_URsIUR", "uimnmx_nopred__URIR_URsIUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Predicate(uPv),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UimnmxudpPipePredPredRegRegImmPred(uPu, uPv, dst, srcA, sb, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UimnmxudpPipePredPredRegRegImmPredPred extends Definition(
        "UimnmxudpPipePredPredRegRegImmPredPred",
        "UIMNMXudp_pipe",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("uPv", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate),
          Slot("uPq", OperandKind.Predicate)
        ),
        Vec("uimnmx_64__URIR_URsIUR", "uimnmx__URIR_URsIUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Predicate(uPv),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Predicate(uPq)
            ) =>
          Some(Instruction.UimnmxudpPipePredPredRegRegImmPredPred(uPu, uPv, dst, srcA, sb, uPp, uPq, modifiers, guard))
        case _ => None
      }
  }
  object UimnmxudpPipePredPredRegRegRegPred extends Definition(
        "UimnmxudpPipePredPredRegRegRegPred",
        "UIMNMXudp_pipe",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("uPv", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("uimnmx_64_nopred__UUU_URURUR", "uimnmx_nopred__UUU_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Predicate(uPv),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UimnmxudpPipePredPredRegRegRegPred(uPu, uPv, dst, srcA, srcB, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UimnmxudpPipePredPredRegRegRegPredPred extends Definition(
        "UimnmxudpPipePredPredRegRegRegPredPred",
        "UIMNMXudp_pipe",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("uPv", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("uPq", OperandKind.Predicate)
        ),
        Vec("uimnmx_64__UUU_URURUR", "uimnmx__UUU_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Predicate(uPv),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Predicate(uPq)
            ) => Some(Instruction.UimnmxudpPipePredPredRegRegRegPredPred(
            uPu,
            uPv,
            dst,
            srcA,
            srcB,
            uPp,
            uPq,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object UimnmxudpPipePredRegRegImmPred extends Definition(
        "UimnmxudpPipePredRegRegImmPred",
        "UIMNMXudp_pipe",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("uimnmx_64_nopred__URIR_URsIUR", "uimnmx_nopred__URIR_URsIUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UimnmxudpPipePredRegRegImmPred(uPu, dst, srcA, sb, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UimnmxudpPipePredRegRegImmPredPred extends Definition(
        "UimnmxudpPipePredRegRegImmPredPred",
        "UIMNMXudp_pipe",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate),
          Slot("uPq", OperandKind.Predicate)
        ),
        Vec("uimnmx_64__URIR_URsIUR", "uimnmx__URIR_URsIUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Predicate(uPq)
            ) => Some(Instruction.UimnmxudpPipePredRegRegImmPredPred(uPu, dst, srcA, sb, uPp, uPq, modifiers, guard))
        case _ => None
      }
  }
  object UimnmxudpPipePredRegRegRegPred extends Definition(
        "UimnmxudpPipePredRegRegRegPred",
        "UIMNMXudp_pipe",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("uimnmx_64_nopred__UUU_URURUR", "uimnmx_nopred__UUU_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UimnmxudpPipePredRegRegRegPred(uPu, dst, srcA, srcB, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UimnmxudpPipePredRegRegRegPredPred extends Definition(
        "UimnmxudpPipePredRegRegRegPredPred",
        "UIMNMXudp_pipe",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("uPq", OperandKind.Predicate)
        ),
        Vec("uimnmx_64__UUU_URURUR", "uimnmx__UUU_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Predicate(uPq)
            ) => Some(Instruction.UimnmxudpPipePredRegRegRegPredPred(uPu, dst, srcA, srcB, uPp, uPq, modifiers, guard))
        case _ => None
      }
  }
  object UimnmxudpPipeRegRegImmPred extends Definition(
        "UimnmxudpPipeRegRegImmPred",
        "UIMNMXudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("uimnmx_64_nopred__URIR_URsIUR", "uimnmx_nopred__URIR_URsIUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UimnmxudpPipeRegRegImmPred(dst, srcA, sb, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UimnmxudpPipeRegRegImmPredPred extends Definition(
        "UimnmxudpPipeRegRegImmPredPred",
        "UIMNMXudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate),
          Slot("uPq", OperandKind.Predicate)
        ),
        Vec("uimnmx_64__URIR_URsIUR", "uimnmx__URIR_URsIUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Predicate(uPq)
            ) => Some(Instruction.UimnmxudpPipeRegRegImmPredPred(dst, srcA, sb, uPp, uPq, modifiers, guard))
        case _ => None
      }
  }
  object UimnmxudpPipeRegRegRegPred extends Definition(
        "UimnmxudpPipeRegRegRegPred",
        "UIMNMXudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("uimnmx_64_nopred__UUU_URURUR", "uimnmx_nopred__UUU_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UimnmxudpPipeRegRegRegPred(dst, srcA, srcB, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UimnmxudpPipeRegRegRegPredPred extends Definition(
        "UimnmxudpPipeRegRegRegPredPred",
        "UIMNMXudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("uPq", OperandKind.Predicate)
        ),
        Vec("uimnmx_64__UUU_URURUR", "uimnmx__UUU_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Predicate(uPq)
            ) => Some(Instruction.UimnmxudpPipeRegRegRegPredPred(dst, srcA, srcB, uPp, uPq, modifiers, guard))
        case _ => None
      }
  }
  object UisetpPredPredRegImmPred extends Definition(
        "UisetpPredPredRegImmPred",
        "UISETP",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("uPv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("uisetp_64__URsIUR_URIR", "uisetp__URsIUR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Predicate(uPv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UisetpPredPredRegImmPred(uPu, uPv, srcA, sb, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UisetpPredPredRegImmPredPred extends Definition(
        "UisetpPredPredRegImmPredPred",
        "UISETP",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("uPv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate),
          Slot("uPr", OperandKind.Predicate)
        ),
        Vec("uisetp_64_optional_upr__URsIUR_URIR", "uisetp_optional_upr__URsIUR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Predicate(uPv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Predicate(uPr)
            ) => Some(Instruction.UisetpPredPredRegImmPredPred(uPu, uPv, srcA, sb, uPp, uPr, modifiers, guard))
        case _ => None
      }
  }
  object UisetpPredPredRegRegPred extends Definition(
        "UisetpPredPredRegRegPred",
        "UISETP",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("uPv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("uisetp_64__URURUR_URURUR", "uisetp__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Predicate(uPv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UisetpPredPredRegRegPred(uPu, uPv, srcA, srcB, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UisetpPredPredRegRegPredPred extends Definition(
        "UisetpPredPredRegRegPredPred",
        "UISETP",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("uPv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("uPr", OperandKind.Predicate)
        ),
        Vec("uisetp_64_optional_upr__URURUR_URURUR", "uisetp_optional_upr__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Predicate(uPv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Predicate(uPr)
            ) => Some(Instruction.UisetpPredPredRegRegPredPred(uPu, uPv, srcA, srcB, uPp, uPr, modifiers, guard))
        case _ => None
      }
  }
  object UisetpPredRegImm extends Definition(
        "UisetpPredRegImm",
        "UISETP",
        Vec(Slot("uPu", OperandKind.Predicate), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("uisetp_64_simple__URsIUR_URIR", "uisetp_simple__URsIUR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.UisetpPredRegImm(uPu, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object UisetpPredRegImmPred extends Definition(
        "UisetpPredRegImmPred",
        "UISETP",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("uPr", OperandKind.Predicate)
        ),
        Vec("uisetp_64_simple_optional_upr__URsIUR_URIR", "uisetp_simple_optional_upr__URsIUR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(uPr)
            ) => Some(Instruction.UisetpPredRegImmPred(uPu, srcA, sb, uPr, modifiers, guard))
        case _ => None
      }
  }
  object UisetpPredRegReg extends Definition(
        "UisetpPredRegReg",
        "UISETP",
        Vec(Slot("uPu", OperandKind.Predicate), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("uisetp_64_simple__URURUR_URURUR", "uisetp_simple__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.UisetpPredRegReg(uPu, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UisetpPredRegRegPred extends Definition(
        "UisetpPredRegRegPred",
        "UISETP",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("uPr", OperandKind.Predicate)
        ),
        Vec("uisetp_64_simple_optional_upr__URURUR_URURUR", "uisetp_simple_optional_upr__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(uPr)
            ) => Some(Instruction.UisetpPredRegRegPred(uPu, srcA, srcB, uPr, modifiers, guard))
        case _ => None
      }
  }
  object UisetpudpPipePredPredRegImmPred extends Definition(
        "UisetpudpPipePredPredRegImmPred",
        "UISETPudp_pipe",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("uPv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("uisetp_64__URsIUR_URIR", "uisetp__URsIUR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Predicate(uPv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UisetpudpPipePredPredRegImmPred(uPu, uPv, srcA, sb, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UisetpudpPipePredPredRegImmPredPred extends Definition(
        "UisetpudpPipePredPredRegImmPredPred",
        "UISETPudp_pipe",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("uPv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate),
          Slot("uPr", OperandKind.Predicate)
        ),
        Vec("uisetp_64_optional_upr__URsIUR_URIR", "uisetp_optional_upr__URsIUR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Predicate(uPv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Predicate(uPr)
            ) => Some(Instruction.UisetpudpPipePredPredRegImmPredPred(uPu, uPv, srcA, sb, uPp, uPr, modifiers, guard))
        case _ => None
      }
  }
  object UisetpudpPipePredPredRegRegPred extends Definition(
        "UisetpudpPipePredPredRegRegPred",
        "UISETPudp_pipe",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("uPv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("uisetp_64__URURUR_URURUR", "uisetp__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Predicate(uPv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UisetpudpPipePredPredRegRegPred(uPu, uPv, srcA, srcB, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UisetpudpPipePredPredRegRegPredPred extends Definition(
        "UisetpudpPipePredPredRegRegPredPred",
        "UISETPudp_pipe",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("uPv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("uPr", OperandKind.Predicate)
        ),
        Vec("uisetp_64_optional_upr__URURUR_URURUR", "uisetp_optional_upr__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Predicate(uPv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Predicate(uPr)
            ) => Some(Instruction.UisetpudpPipePredPredRegRegPredPred(uPu, uPv, srcA, srcB, uPp, uPr, modifiers, guard))
        case _ => None
      }
  }
  object UisetpudpPipePredRegImm extends Definition(
        "UisetpudpPipePredRegImm",
        "UISETPudp_pipe",
        Vec(Slot("uPu", OperandKind.Predicate), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("uisetp_64_simple__URsIUR_URIR", "uisetp_simple__URsIUR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.UisetpudpPipePredRegImm(uPu, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object UisetpudpPipePredRegImmPred extends Definition(
        "UisetpudpPipePredRegImmPred",
        "UISETPudp_pipe",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("uPr", OperandKind.Predicate)
        ),
        Vec("uisetp_64_simple_optional_upr__URsIUR_URIR", "uisetp_simple_optional_upr__URsIUR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(uPr)
            ) => Some(Instruction.UisetpudpPipePredRegImmPred(uPu, srcA, sb, uPr, modifiers, guard))
        case _ => None
      }
  }
  object UisetpudpPipePredRegReg extends Definition(
        "UisetpudpPipePredRegReg",
        "UISETPudp_pipe",
        Vec(Slot("uPu", OperandKind.Predicate), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("uisetp_64_simple__URURUR_URURUR", "uisetp_simple__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.UisetpudpPipePredRegReg(uPu, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UisetpudpPipePredRegRegPred extends Definition(
        "UisetpudpPipePredRegRegPred",
        "UISETPudp_pipe",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("uPr", OperandKind.Predicate)
        ),
        Vec("uisetp_64_simple_optional_upr__URURUR_URURUR", "uisetp_simple_optional_upr__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(uPr)
            ) => Some(Instruction.UisetpudpPipePredRegRegPred(uPu, srcA, srcB, uPr, modifiers, guard))
        case _ => None
      }
  }
  object UldcRegAddress extends Definition(
        "UldcRegAddress",
        "ULDC",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address)),
        Vec("uldc_imm_", "uldc_ur_offs_optional_upx_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Address(srcA)) =>
          Some(Instruction.UldcRegAddress(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object UldcRegAddressImm extends Definition(
        "UldcRegAddressImm",
        "ULDC",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("sa_offset", OperandKind.Immediate)
        ),
        Vec("uldc_const__RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Immediate(sa_offset)
            ) => Some(Instruction.UldcRegAddressImm(dst, srcA, sa_offset, modifiers, guard))
        case _ => None
      }
  }
  object UldcRegAddressPred extends Definition(
        "UldcRegAddressPred",
        "ULDC",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address), Slot("uPx", OperandKind.Predicate)),
        Vec("uldc_ur_offs_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Predicate(uPx)
            ) => Some(Instruction.UldcRegAddressPred(dst, srcA, uPx, modifiers, guard))
        case _ => None
      }
  }
  object UldcRegConst extends Definition(
        "UldcRegConst",
        "ULDC",
        Vec(Slot("dst", OperandKind.Register), Slot("sa", OperandKind.ConstantBank)),
        Vec("uldc_const__RCR", "uldc_ur_offset_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(sa)) =>
          Some(Instruction.UldcRegConst(dst, sa, modifiers, guard))
        case _ => None
      }
  }
  object UldcudpPipeRegAddress extends Definition(
        "UldcudpPipeRegAddress",
        "ULDCudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address)),
        Vec("uldc_imm_", "uldc_ur_offs_optional_upx_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Address(srcA)) =>
          Some(Instruction.UldcudpPipeRegAddress(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object UldcudpPipeRegAddressImm extends Definition(
        "UldcudpPipeRegAddressImm",
        "ULDCudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("sa_offset", OperandKind.Immediate)
        ),
        Vec("uldc_const__RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Immediate(sa_offset)
            ) => Some(Instruction.UldcudpPipeRegAddressImm(dst, srcA, sa_offset, modifiers, guard))
        case _ => None
      }
  }
  object UldcudpPipeRegAddressPred extends Definition(
        "UldcudpPipeRegAddressPred",
        "ULDCudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address), Slot("uPx", OperandKind.Predicate)),
        Vec("uldc_ur_offs_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Predicate(uPx)
            ) => Some(Instruction.UldcudpPipeRegAddressPred(dst, srcA, uPx, modifiers, guard))
        case _ => None
      }
  }
  object UldcudpPipeRegConst extends Definition(
        "UldcudpPipeRegConst",
        "ULDCudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("sa", OperandKind.ConstantBank)),
        Vec("uldc_const__RCR", "uldc_ur_offset_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(sa)) =>
          Some(Instruction.UldcudpPipeRegConst(dst, sa, modifiers, guard))
        case _ => None
      }
  }
  object UleaRegPredRegImmImm extends Definition(
        "UleaRegPredRegImmImm",
        "ULEA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uPu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("ulea_hi_imm_sx32__RuIR_URIUR", "ulea_lo_imm__URuIUR_URIUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.UleaRegPredRegImmImm(dst, uPu, srcA, sb, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object UleaRegPredRegImmImmPred extends Definition(
        "UleaRegPredRegImmImmPred",
        "ULEA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uPu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("scaleU5", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ulea_hi_imm_sx32_x__RuIR_URIUR", "ulea_lo_imm_x__URuIUR_URIUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Immediate(scaleU5),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UleaRegPredRegImmImmPred(dst, uPu, srcA, sb, scaleU5, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UleaRegPredRegImmRegImm extends Definition(
        "UleaRegPredRegImmRegImm",
        "ULEA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uPu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("ulea_hi_imm__RuIR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.UleaRegPredRegImmRegImm(dst, uPu, srcA, sb, srcC, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object UleaRegPredRegImmRegImmPred extends Definition(
        "UleaRegPredRegImmRegImmPred",
        "ULEA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uPu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ulea_hi_imm_x__RuIR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(scaleU5),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UleaRegPredRegImmRegImmPred(dst, uPu, srcA, sb, srcC, scaleU5, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UleaRegPredRegRegImm extends Definition(
        "UleaRegPredRegRegImm",
        "ULEA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uPu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("ulea_hi_noimm_sx32__URURUR_URURUR", "ulea_lo_noimm__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.UleaRegPredRegRegImm(dst, uPu, srcA, srcB, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object UleaRegPredRegRegImmImm extends Definition(
        "UleaRegPredRegRegImmImm",
        "ULEA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uPu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("ulea_hi_imm__RRuI_RRI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.UleaRegPredRegRegImmImm(dst, uPu, srcA, srcB, sc, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object UleaRegPredRegRegImmImmPred extends Definition(
        "UleaRegPredRegRegImmImmPred",
        "ULEA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uPu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate),
          Slot("scaleU5", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ulea_hi_imm_x__RRuI_RRI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc),
              InstructionOperand.Immediate(scaleU5),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UleaRegPredRegRegImmImmPred(dst, uPu, srcA, srcB, sc, scaleU5, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UleaRegPredRegRegImmPred extends Definition(
        "UleaRegPredRegRegImmPred",
        "ULEA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uPu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ulea_hi_noimm_sx32_x__URURUR_URURUR", "ulea_lo_noimm_x__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(scaleU5),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UleaRegPredRegRegImmPred(dst, uPu, srcA, srcB, scaleU5, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UleaRegPredRegRegRegImm extends Definition(
        "UleaRegPredRegRegRegImm",
        "ULEA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uPu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("ulea_hi_noimm__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.UleaRegPredRegRegRegImm(dst, uPu, srcA, srcB, srcC, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object UleaRegPredRegRegRegImmPred extends Definition(
        "UleaRegPredRegRegRegImmPred",
        "ULEA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uPu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ulea_hi_noimm_x__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(scaleU5),
              InstructionOperand.Predicate(uPp)
            ) =>
          Some(Instruction.UleaRegPredRegRegRegImmPred(dst, uPu, srcA, srcB, srcC, scaleU5, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UleaRegRegImmImm extends Definition(
        "UleaRegRegImmImm",
        "ULEA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("ulea_hi_imm_sx32__RuIR_URIUR", "ulea_lo_imm__URuIUR_URIUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.UleaRegRegImmImm(dst, srcA, sb, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object UleaRegRegImmImmPred extends Definition(
        "UleaRegRegImmImmPred",
        "ULEA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("scaleU5", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ulea_hi_imm_sx32_x__RuIR_URIUR", "ulea_lo_imm_x__URuIUR_URIUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Immediate(scaleU5),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UleaRegRegImmImmPred(dst, srcA, sb, scaleU5, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UleaRegRegImmRegImm extends Definition(
        "UleaRegRegImmRegImm",
        "ULEA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("ulea_hi_imm__RuIR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.UleaRegRegImmRegImm(dst, srcA, sb, srcC, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object UleaRegRegImmRegImmPred extends Definition(
        "UleaRegRegImmRegImmPred",
        "ULEA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ulea_hi_imm_x__RuIR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(scaleU5),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UleaRegRegImmRegImmPred(dst, srcA, sb, srcC, scaleU5, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UleaRegRegRegImm extends Definition(
        "UleaRegRegRegImm",
        "ULEA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("ulea_hi_noimm_sx32__URURUR_URURUR", "ulea_lo_noimm__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.UleaRegRegRegImm(dst, srcA, srcB, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object UleaRegRegRegImmImm extends Definition(
        "UleaRegRegRegImmImm",
        "ULEA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("ulea_hi_imm__RRuI_RRI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.UleaRegRegRegImmImm(dst, srcA, srcB, sc, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object UleaRegRegRegImmImmPred extends Definition(
        "UleaRegRegRegImmImmPred",
        "ULEA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate),
          Slot("scaleU5", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ulea_hi_imm_x__RRuI_RRI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc),
              InstructionOperand.Immediate(scaleU5),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UleaRegRegRegImmImmPred(dst, srcA, srcB, sc, scaleU5, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UleaRegRegRegImmPred extends Definition(
        "UleaRegRegRegImmPred",
        "ULEA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ulea_hi_noimm_sx32_x__URURUR_URURUR", "ulea_lo_noimm_x__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(scaleU5),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UleaRegRegRegImmPred(dst, srcA, srcB, scaleU5, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UleaRegRegRegRegImm extends Definition(
        "UleaRegRegRegRegImm",
        "ULEA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("ulea_hi_noimm__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.UleaRegRegRegRegImm(dst, srcA, srcB, srcC, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object UleaRegRegRegRegImmPred extends Definition(
        "UleaRegRegRegRegImmPred",
        "ULEA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ulea_hi_noimm_x__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(scaleU5),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UleaRegRegRegRegImmPred(dst, srcA, srcB, srcC, scaleU5, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UleaudpPipeRegPredRegImmImm extends Definition(
        "UleaudpPipeRegPredRegImmImm",
        "ULEAudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uPu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("ulea_hi_imm_sx32__RuIR_URIUR", "ulea_lo_imm__URuIUR_URIUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.UleaudpPipeRegPredRegImmImm(dst, uPu, srcA, sb, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object UleaudpPipeRegPredRegImmImmPred extends Definition(
        "UleaudpPipeRegPredRegImmImmPred",
        "ULEAudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uPu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("scaleU5", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ulea_hi_imm_sx32_x__RuIR_URIUR", "ulea_lo_imm_x__URuIUR_URIUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Immediate(scaleU5),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UleaudpPipeRegPredRegImmImmPred(dst, uPu, srcA, sb, scaleU5, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UleaudpPipeRegPredRegImmRegImm extends Definition(
        "UleaudpPipeRegPredRegImmRegImm",
        "ULEAudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uPu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("ulea_hi_imm__RuIR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.UleaudpPipeRegPredRegImmRegImm(dst, uPu, srcA, sb, srcC, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object UleaudpPipeRegPredRegImmRegImmPred extends Definition(
        "UleaudpPipeRegPredRegImmRegImmPred",
        "ULEAudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uPu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ulea_hi_imm_x__RuIR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(scaleU5),
              InstructionOperand.Predicate(uPp)
            ) =>
          Some(Instruction.UleaudpPipeRegPredRegImmRegImmPred(dst, uPu, srcA, sb, srcC, scaleU5, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UleaudpPipeRegPredRegRegImm extends Definition(
        "UleaudpPipeRegPredRegRegImm",
        "ULEAudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uPu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("ulea_hi_noimm_sx32__URURUR_URURUR", "ulea_lo_noimm__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.UleaudpPipeRegPredRegRegImm(dst, uPu, srcA, srcB, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object UleaudpPipeRegPredRegRegImmImm extends Definition(
        "UleaudpPipeRegPredRegRegImmImm",
        "ULEAudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uPu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("ulea_hi_imm__RRuI_RRI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.UleaudpPipeRegPredRegRegImmImm(dst, uPu, srcA, srcB, sc, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object UleaudpPipeRegPredRegRegImmImmPred extends Definition(
        "UleaudpPipeRegPredRegRegImmImmPred",
        "ULEAudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uPu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate),
          Slot("scaleU5", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ulea_hi_imm_x__RRuI_RRI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc),
              InstructionOperand.Immediate(scaleU5),
              InstructionOperand.Predicate(uPp)
            ) =>
          Some(Instruction.UleaudpPipeRegPredRegRegImmImmPred(dst, uPu, srcA, srcB, sc, scaleU5, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UleaudpPipeRegPredRegRegImmPred extends Definition(
        "UleaudpPipeRegPredRegRegImmPred",
        "ULEAudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uPu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ulea_hi_noimm_sx32_x__URURUR_URURUR", "ulea_lo_noimm_x__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(scaleU5),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UleaudpPipeRegPredRegRegImmPred(dst, uPu, srcA, srcB, scaleU5, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UleaudpPipeRegPredRegRegRegImm extends Definition(
        "UleaudpPipeRegPredRegRegRegImm",
        "ULEAudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uPu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("ulea_hi_noimm__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.UleaudpPipeRegPredRegRegRegImm(dst, uPu, srcA, srcB, srcC, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object UleaudpPipeRegPredRegRegRegImmPred extends Definition(
        "UleaudpPipeRegPredRegRegRegImmPred",
        "ULEAudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("uPu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ulea_hi_noimm_x__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(scaleU5),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UleaudpPipeRegPredRegRegRegImmPred(
            dst,
            uPu,
            srcA,
            srcB,
            srcC,
            scaleU5,
            uPp,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object UleaudpPipeRegRegImmImm extends Definition(
        "UleaudpPipeRegRegImmImm",
        "ULEAudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("ulea_hi_imm_sx32__RuIR_URIUR", "ulea_lo_imm__URuIUR_URIUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.UleaudpPipeRegRegImmImm(dst, srcA, sb, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object UleaudpPipeRegRegImmImmPred extends Definition(
        "UleaudpPipeRegRegImmImmPred",
        "ULEAudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("scaleU5", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ulea_hi_imm_sx32_x__RuIR_URIUR", "ulea_lo_imm_x__URuIUR_URIUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Immediate(scaleU5),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UleaudpPipeRegRegImmImmPred(dst, srcA, sb, scaleU5, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UleaudpPipeRegRegImmRegImm extends Definition(
        "UleaudpPipeRegRegImmRegImm",
        "ULEAudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("ulea_hi_imm__RuIR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.UleaudpPipeRegRegImmRegImm(dst, srcA, sb, srcC, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object UleaudpPipeRegRegImmRegImmPred extends Definition(
        "UleaudpPipeRegRegImmRegImmPred",
        "ULEAudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ulea_hi_imm_x__RuIR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(scaleU5),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UleaudpPipeRegRegImmRegImmPred(dst, srcA, sb, srcC, scaleU5, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UleaudpPipeRegRegRegImm extends Definition(
        "UleaudpPipeRegRegRegImm",
        "ULEAudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("ulea_hi_noimm_sx32__URURUR_URURUR", "ulea_lo_noimm__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.UleaudpPipeRegRegRegImm(dst, srcA, srcB, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object UleaudpPipeRegRegRegImmImm extends Definition(
        "UleaudpPipeRegRegRegImmImm",
        "ULEAudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("ulea_hi_imm__RRuI_RRI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.UleaudpPipeRegRegRegImmImm(dst, srcA, srcB, sc, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object UleaudpPipeRegRegRegImmImmPred extends Definition(
        "UleaudpPipeRegRegRegImmImmPred",
        "ULEAudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate),
          Slot("scaleU5", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ulea_hi_imm_x__RRuI_RRI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc),
              InstructionOperand.Immediate(scaleU5),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UleaudpPipeRegRegRegImmImmPred(dst, srcA, srcB, sc, scaleU5, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UleaudpPipeRegRegRegImmPred extends Definition(
        "UleaudpPipeRegRegRegImmPred",
        "ULEAudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ulea_hi_noimm_sx32_x__URURUR_URURUR", "ulea_lo_noimm_x__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(scaleU5),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UleaudpPipeRegRegRegImmPred(dst, srcA, srcB, scaleU5, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UleaudpPipeRegRegRegRegImm extends Definition(
        "UleaudpPipeRegRegRegRegImm",
        "ULEAudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate)
        ),
        Vec("ulea_hi_noimm__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(scaleU5)
            ) => Some(Instruction.UleaudpPipeRegRegRegRegImm(dst, srcA, srcB, srcC, scaleU5, modifiers, guard))
        case _ => None
      }
  }
  object UleaudpPipeRegRegRegRegImmPred extends Definition(
        "UleaudpPipeRegRegRegRegImmPred",
        "ULEAudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("scaleU5", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ulea_hi_noimm_x__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(scaleU5),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UleaudpPipeRegRegRegRegImmPred(dst, srcA, srcB, srcC, scaleU5, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UlepcReg
      extends Definition("UlepcReg", "ULEPC", Vec(Slot("dst", OperandKind.Register)), Vec("ulepc__URURUR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst)) => Some(Instruction.UlepcReg(dst, modifiers, guard))
        case _ => None
      }
  }
  object UlepcRegImm extends Definition(
        "UlepcRegImm",
        "ULEPC",
        Vec(Slot("dst", OperandKind.Register), Slot("sImm58", OperandKind.Immediate)),
        Vec("ulepc__UR_I_R", "ulepc_rel_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sImm58)) =>
          Some(Instruction.UlepcRegImm(dst, sImm58, modifiers, guard))
        case _ => None
      }
  }
  object UlepcudpPipeReg extends Definition(
        "UlepcudpPipeReg",
        "ULEPCudp_pipe",
        Vec(Slot("dst", OperandKind.Register)),
        Vec("ulepc__URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst)) => Some(Instruction.UlepcudpPipeReg(dst, modifiers, guard))
        case _ => None
      }
  }
  object UlepcudpPipeRegImm extends Definition(
        "UlepcudpPipeRegImm",
        "ULEPCudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("sImm58", OperandKind.Immediate)),
        Vec("ulepc__UR_I_R", "ulepc_rel_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sImm58)) =>
          Some(Instruction.UlepcudpPipeRegImm(dst, sImm58, modifiers, guard))
        case _ => None
      }
  }
  object UlopPredRegRegImm extends Definition(
        "UlopPredRegRegImm",
        "ULOP",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate)
        ),
        Vec("ulop_imm_optionalUPp_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.UlopPredRegRegImm(uPu, dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object UlopPredRegRegImmPred extends Definition(
        "UlopPredRegRegImmPred",
        "ULOP",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ulop_imm_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UlopPredRegRegImmPred(uPu, dst, srcA, sb, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UlopPredRegRegReg extends Definition(
        "UlopPredRegRegReg",
        "ULOP",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register)
        ),
        Vec("ulop_noimm_optionalUPp__URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.UlopPredRegRegReg(uPu, dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UlopPredRegRegRegPred extends Definition(
        "UlopPredRegRegRegPred",
        "ULOP",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ulop_noimm__URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UlopPredRegRegRegPred(uPu, dst, srcA, srcB, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UlopRegRegImm extends Definition(
        "UlopRegRegImm",
        "ULOP",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("ulop_imm_optionalUPp_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.UlopRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object UlopRegRegImmPred extends Definition(
        "UlopRegRegImmPred",
        "ULOP",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ulop_imm_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UlopRegRegImmPred(dst, srcA, sb, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UlopRegRegReg extends Definition(
        "UlopRegRegReg",
        "ULOP",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("ulop_noimm_optionalUPp__URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.UlopRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UlopRegRegRegPred extends Definition(
        "UlopRegRegRegPred",
        "ULOP",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ulop_noimm__URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UlopRegRegRegPred(dst, srcA, srcB, uPp, modifiers, guard))
        case _ => None
      }
  }
  object Ulop3PredRegRegImmReg extends Definition(
        "Ulop3PredRegRegImmReg",
        "ULOP3",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("ulop3_imm_optionalUPp__URIR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Ulop3PredRegRegImmReg(uPu, dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Ulop3PredRegRegImmRegImm extends Definition(
        "Ulop3PredRegRegImmRegImm",
        "ULOP3",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("imm8", OperandKind.Immediate)
        ),
        Vec("ulop3_lut_optionalUPp__URuIUR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(imm8)
            ) => Some(Instruction.Ulop3PredRegRegImmRegImm(uPu, dst, srcA, sb, srcC, imm8, modifiers, guard))
        case _ => None
      }
  }
  object Ulop3PredRegRegImmRegImmPred extends Definition(
        "Ulop3PredRegRegImmRegImmPred",
        "ULOP3",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("imm8", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ulop3_lut__URuIUR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(imm8),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.Ulop3PredRegRegImmRegImmPred(uPu, dst, srcA, sb, srcC, imm8, uPp, modifiers, guard))
        case _ => None
      }
  }
  object Ulop3PredRegRegImmRegPred extends Definition(
        "Ulop3PredRegRegImmRegPred",
        "ULOP3",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ulop3_imm__URIR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.Ulop3PredRegRegImmRegPred(uPu, dst, srcA, sb, srcC, uPp, modifiers, guard))
        case _ => None
      }
  }
  object Ulop3PredRegRegRegReg extends Definition(
        "Ulop3PredRegRegRegReg",
        "ULOP3",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("ulop3_noimm_optionalUPp__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Ulop3PredRegRegRegReg(uPu, dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Ulop3PredRegRegRegRegImm extends Definition(
        "Ulop3PredRegRegRegRegImm",
        "ULOP3",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("imm8", OperandKind.Immediate)
        ),
        Vec("ulop3_lut_optionalUPp__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(imm8)
            ) => Some(Instruction.Ulop3PredRegRegRegRegImm(uPu, dst, srcA, srcB, srcC, imm8, modifiers, guard))
        case _ => None
      }
  }
  object Ulop3PredRegRegRegRegImmPred extends Definition(
        "Ulop3PredRegRegRegRegImmPred",
        "ULOP3",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("imm8", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ulop3_lut__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(imm8),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.Ulop3PredRegRegRegRegImmPred(uPu, dst, srcA, srcB, srcC, imm8, uPp, modifiers, guard))
        case _ => None
      }
  }
  object Ulop3PredRegRegRegRegPred extends Definition(
        "Ulop3PredRegRegRegRegPred",
        "ULOP3",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ulop3_noimm__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.Ulop3PredRegRegRegRegPred(uPu, dst, srcA, srcB, srcC, uPp, modifiers, guard))
        case _ => None
      }
  }
  object Ulop3RegRegImmReg extends Definition(
        "Ulop3RegRegImmReg",
        "ULOP3",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("ulop3_imm_optionalUPp__URIR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Ulop3RegRegImmReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Ulop3RegRegImmRegImm extends Definition(
        "Ulop3RegRegImmRegImm",
        "ULOP3",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("imm8", OperandKind.Immediate)
        ),
        Vec("ulop3_lut_optionalUPp__URuIUR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(imm8)
            ) => Some(Instruction.Ulop3RegRegImmRegImm(dst, srcA, sb, srcC, imm8, modifiers, guard))
        case _ => None
      }
  }
  object Ulop3RegRegImmRegImmPred extends Definition(
        "Ulop3RegRegImmRegImmPred",
        "ULOP3",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("imm8", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ulop3_lut__URuIUR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(imm8),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.Ulop3RegRegImmRegImmPred(dst, srcA, sb, srcC, imm8, uPp, modifiers, guard))
        case _ => None
      }
  }
  object Ulop3RegRegImmRegPred extends Definition(
        "Ulop3RegRegImmRegPred",
        "ULOP3",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ulop3_imm__URIR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.Ulop3RegRegImmRegPred(dst, srcA, sb, srcC, uPp, modifiers, guard))
        case _ => None
      }
  }
  object Ulop3RegRegRegReg extends Definition(
        "Ulop3RegRegRegReg",
        "ULOP3",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("ulop3_noimm_optionalUPp__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Ulop3RegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Ulop3RegRegRegRegImm extends Definition(
        "Ulop3RegRegRegRegImm",
        "ULOP3",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("imm8", OperandKind.Immediate)
        ),
        Vec("ulop3_lut_optionalUPp__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(imm8)
            ) => Some(Instruction.Ulop3RegRegRegRegImm(dst, srcA, srcB, srcC, imm8, modifiers, guard))
        case _ => None
      }
  }
  object Ulop3RegRegRegRegImmPred extends Definition(
        "Ulop3RegRegRegRegImmPred",
        "ULOP3",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("imm8", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ulop3_lut__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(imm8),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.Ulop3RegRegRegRegImmPred(dst, srcA, srcB, srcC, imm8, uPp, modifiers, guard))
        case _ => None
      }
  }
  object Ulop3RegRegRegRegPred extends Definition(
        "Ulop3RegRegRegRegPred",
        "ULOP3",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ulop3_noimm__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.Ulop3RegRegRegRegPred(dst, srcA, srcB, srcC, uPp, modifiers, guard))
        case _ => None
      }
  }
  object Ulop32iPredRegRegImm extends Definition(
        "Ulop32iPredRegRegImm",
        "ULOP32I",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate)
        ),
        Vec("ulop32i_optionalUPp_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.Ulop32iPredRegRegImm(uPu, dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object Ulop32iPredRegRegImmPred extends Definition(
        "Ulop32iPredRegRegImmPred",
        "ULOP32I",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ulop32i_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.Ulop32iPredRegRegImmPred(uPu, dst, srcA, sb, uPp, modifiers, guard))
        case _ => None
      }
  }
  object Ulop32iRegRegImm extends Definition(
        "Ulop32iRegRegImm",
        "ULOP32I",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("ulop32i_optionalUPp_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.Ulop32iRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object Ulop32iRegRegImmPred extends Definition(
        "Ulop32iRegRegImmPred",
        "ULOP32I",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ulop32i_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.Ulop32iRegRegImmPred(dst, srcA, sb, uPp, modifiers, guard))
        case _ => None
      }
  }
  object Ulop32iudpPipePredRegRegImm extends Definition(
        "Ulop32iudpPipePredRegRegImm",
        "ULOP32Iudp_pipe",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate)
        ),
        Vec("ulop32i_optionalUPp_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.Ulop32iudpPipePredRegRegImm(uPu, dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object Ulop32iudpPipePredRegRegImmPred extends Definition(
        "Ulop32iudpPipePredRegRegImmPred",
        "ULOP32Iudp_pipe",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ulop32i_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.Ulop32iudpPipePredRegRegImmPred(uPu, dst, srcA, sb, uPp, modifiers, guard))
        case _ => None
      }
  }
  object Ulop32iudpPipeRegRegImm extends Definition(
        "Ulop32iudpPipeRegRegImm",
        "ULOP32Iudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("ulop32i_optionalUPp_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.Ulop32iudpPipeRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object Ulop32iudpPipeRegRegImmPred extends Definition(
        "Ulop32iudpPipeRegRegImmPred",
        "ULOP32Iudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ulop32i_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.Ulop32iudpPipeRegRegImmPred(dst, srcA, sb, uPp, modifiers, guard))
        case _ => None
      }
  }
  object Ulop3udpPipePredRegRegImmReg extends Definition(
        "Ulop3udpPipePredRegRegImmReg",
        "ULOP3udp_pipe",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("ulop3_imm_optionalUPp__URIR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Ulop3udpPipePredRegRegImmReg(uPu, dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Ulop3udpPipePredRegRegImmRegImm extends Definition(
        "Ulop3udpPipePredRegRegImmRegImm",
        "ULOP3udp_pipe",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("imm8", OperandKind.Immediate)
        ),
        Vec("ulop3_lut_optionalUPp__URuIUR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(imm8)
            ) => Some(Instruction.Ulop3udpPipePredRegRegImmRegImm(uPu, dst, srcA, sb, srcC, imm8, modifiers, guard))
        case _ => None
      }
  }
  object Ulop3udpPipePredRegRegImmRegImmPred extends Definition(
        "Ulop3udpPipePredRegRegImmRegImmPred",
        "ULOP3udp_pipe",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("imm8", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ulop3_lut__URuIUR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(imm8),
              InstructionOperand.Predicate(uPp)
            ) =>
          Some(Instruction.Ulop3udpPipePredRegRegImmRegImmPred(uPu, dst, srcA, sb, srcC, imm8, uPp, modifiers, guard))
        case _ => None
      }
  }
  object Ulop3udpPipePredRegRegImmRegPred extends Definition(
        "Ulop3udpPipePredRegRegImmRegPred",
        "ULOP3udp_pipe",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ulop3_imm__URIR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.Ulop3udpPipePredRegRegImmRegPred(uPu, dst, srcA, sb, srcC, uPp, modifiers, guard))
        case _ => None
      }
  }
  object Ulop3udpPipePredRegRegRegReg extends Definition(
        "Ulop3udpPipePredRegRegRegReg",
        "ULOP3udp_pipe",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("ulop3_noimm_optionalUPp__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Ulop3udpPipePredRegRegRegReg(uPu, dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Ulop3udpPipePredRegRegRegRegImm extends Definition(
        "Ulop3udpPipePredRegRegRegRegImm",
        "ULOP3udp_pipe",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("imm8", OperandKind.Immediate)
        ),
        Vec("ulop3_lut_optionalUPp__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(imm8)
            ) => Some(Instruction.Ulop3udpPipePredRegRegRegRegImm(uPu, dst, srcA, srcB, srcC, imm8, modifiers, guard))
        case _ => None
      }
  }
  object Ulop3udpPipePredRegRegRegRegImmPred extends Definition(
        "Ulop3udpPipePredRegRegRegRegImmPred",
        "ULOP3udp_pipe",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("imm8", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ulop3_lut__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(imm8),
              InstructionOperand.Predicate(uPp)
            ) =>
          Some(Instruction.Ulop3udpPipePredRegRegRegRegImmPred(uPu, dst, srcA, srcB, srcC, imm8, uPp, modifiers, guard))
        case _ => None
      }
  }
  object Ulop3udpPipePredRegRegRegRegPred extends Definition(
        "Ulop3udpPipePredRegRegRegRegPred",
        "ULOP3udp_pipe",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ulop3_noimm__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.Ulop3udpPipePredRegRegRegRegPred(uPu, dst, srcA, srcB, srcC, uPp, modifiers, guard))
        case _ => None
      }
  }
  object Ulop3udpPipeRegRegImmReg extends Definition(
        "Ulop3udpPipeRegRegImmReg",
        "ULOP3udp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("ulop3_imm_optionalUPp__URIR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Ulop3udpPipeRegRegImmReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Ulop3udpPipeRegRegImmRegImm extends Definition(
        "Ulop3udpPipeRegRegImmRegImm",
        "ULOP3udp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("imm8", OperandKind.Immediate)
        ),
        Vec("ulop3_lut_optionalUPp__URuIUR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(imm8)
            ) => Some(Instruction.Ulop3udpPipeRegRegImmRegImm(dst, srcA, sb, srcC, imm8, modifiers, guard))
        case _ => None
      }
  }
  object Ulop3udpPipeRegRegImmRegImmPred extends Definition(
        "Ulop3udpPipeRegRegImmRegImmPred",
        "ULOP3udp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("imm8", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ulop3_lut__URuIUR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(imm8),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.Ulop3udpPipeRegRegImmRegImmPred(dst, srcA, sb, srcC, imm8, uPp, modifiers, guard))
        case _ => None
      }
  }
  object Ulop3udpPipeRegRegImmRegPred extends Definition(
        "Ulop3udpPipeRegRegImmRegPred",
        "ULOP3udp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ulop3_imm__URIR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.Ulop3udpPipeRegRegImmRegPred(dst, srcA, sb, srcC, uPp, modifiers, guard))
        case _ => None
      }
  }
  object Ulop3udpPipeRegRegRegReg extends Definition(
        "Ulop3udpPipeRegRegRegReg",
        "ULOP3udp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("ulop3_noimm_optionalUPp__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Ulop3udpPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Ulop3udpPipeRegRegRegRegImm extends Definition(
        "Ulop3udpPipeRegRegRegRegImm",
        "ULOP3udp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("imm8", OperandKind.Immediate)
        ),
        Vec("ulop3_lut_optionalUPp__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(imm8)
            ) => Some(Instruction.Ulop3udpPipeRegRegRegRegImm(dst, srcA, srcB, srcC, imm8, modifiers, guard))
        case _ => None
      }
  }
  object Ulop3udpPipeRegRegRegRegImmPred extends Definition(
        "Ulop3udpPipeRegRegRegRegImmPred",
        "ULOP3udp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("imm8", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ulop3_lut__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(imm8),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.Ulop3udpPipeRegRegRegRegImmPred(dst, srcA, srcB, srcC, imm8, uPp, modifiers, guard))
        case _ => None
      }
  }
  object Ulop3udpPipeRegRegRegRegPred extends Definition(
        "Ulop3udpPipeRegRegRegRegPred",
        "ULOP3udp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ulop3_noimm__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.Ulop3udpPipeRegRegRegRegPred(dst, srcA, srcB, srcC, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UlopudpPipePredRegRegImm extends Definition(
        "UlopudpPipePredRegRegImm",
        "ULOPudp_pipe",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate)
        ),
        Vec("ulop_imm_optionalUPp_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.UlopudpPipePredRegRegImm(uPu, dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object UlopudpPipePredRegRegImmPred extends Definition(
        "UlopudpPipePredRegRegImmPred",
        "ULOPudp_pipe",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ulop_imm_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UlopudpPipePredRegRegImmPred(uPu, dst, srcA, sb, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UlopudpPipePredRegRegReg extends Definition(
        "UlopudpPipePredRegRegReg",
        "ULOPudp_pipe",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register)
        ),
        Vec("ulop_noimm_optionalUPp__URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.UlopudpPipePredRegRegReg(uPu, dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UlopudpPipePredRegRegRegPred extends Definition(
        "UlopudpPipePredRegRegRegPred",
        "ULOPudp_pipe",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ulop_noimm__URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UlopudpPipePredRegRegRegPred(uPu, dst, srcA, srcB, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UlopudpPipeRegRegImm extends Definition(
        "UlopudpPipeRegRegImm",
        "ULOPudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("ulop_imm_optionalUPp_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.UlopudpPipeRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object UlopudpPipeRegRegImmPred extends Definition(
        "UlopudpPipeRegRegImmPred",
        "ULOPudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ulop_imm_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UlopudpPipeRegRegImmPred(dst, srcA, sb, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UlopudpPipeRegRegReg extends Definition(
        "UlopudpPipeRegRegReg",
        "ULOPudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("ulop_noimm_optionalUPp__URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.UlopudpPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UlopudpPipeRegRegRegPred extends Definition(
        "UlopudpPipeRegRegRegPred",
        "ULOPudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("ulop_noimm__URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UlopudpPipeRegRegRegPred(dst, srcA, srcB, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UmemsetsAddressRegReg extends Definition(
        "UmemsetsAddressRegReg",
        "UMEMSETS",
        Vec(Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register), Slot("srcC", OperandKind.Register)),
        Vec("umemsets_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.UmemsetsAddressRegReg(srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object UmemsetsudpPipeAddressRegReg extends Definition(
        "UmemsetsudpPipeAddressRegReg",
        "UMEMSETSudp_pipe",
        Vec(Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register), Slot("srcC", OperandKind.Register)),
        Vec("umemsets_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.UmemsetsudpPipeAddressRegReg(srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object UmovRegImm extends Definition(
        "UmovRegImm",
        "UMOV",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("uiadd3_umov64__RIR_URsIUR", "umov__UI", "umov_imm64_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.UmovRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object UmovRegReg extends Definition(
        "UmovRegReg",
        "UMOV",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("uiadd3_umov64__URURUR_URURUR", "umov_64__UR", "umov__UR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.UmovRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UmovudpPipeRegImm extends Definition(
        "UmovudpPipeRegImm",
        "UMOVudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("umov__UI", "umov_imm64_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.UmovudpPipeRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object UmovudpPipeRegReg extends Definition(
        "UmovudpPipeRegReg",
        "UMOVudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("umov_64__UR", "umov__UR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.UmovudpPipeRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object Up2urReg
      extends Definition("Up2urReg", "UP2UR", Vec(Slot("dst", OperandKind.Register)), Vec("up2ur_simple_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst)) => Some(Instruction.Up2urReg(dst, modifiers, guard))
        case _ => None
      }
  }
  object Up2urRegRegImm extends Definition(
        "Up2urRegRegImm",
        "UP2UR",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("up2ur__Imm")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.Up2urRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object Up2urRegRegReg extends Definition(
        "Up2urRegRegReg",
        "UP2UR",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("up2ur__URb")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.Up2urRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object Up2urudpPipeReg extends Definition(
        "Up2urudpPipeReg",
        "UP2URudp_pipe",
        Vec(Slot("dst", OperandKind.Register)),
        Vec("up2ur_simple_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst)) => Some(Instruction.Up2urudpPipeReg(dst, modifiers, guard))
        case _ => None
      }
  }
  object Up2urudpPipeRegRegImm extends Definition(
        "Up2urudpPipeRegRegImm",
        "UP2URudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("up2ur__Imm")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.Up2urudpPipeRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object Up2urudpPipeRegRegReg extends Definition(
        "Up2urudpPipeRegRegReg",
        "UP2URudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("up2ur__URb")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.Up2urudpPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object Uplop3PredPredPredPred extends Definition(
        "Uplop3PredPredPredPred",
        "UPLOP3",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("uPp", OperandKind.Predicate),
          Slot("uPq", OperandKind.Predicate),
          Slot("uPr", OperandKind.Predicate)
        ),
        Vec("uplop3_1out_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Predicate(uPq),
              InstructionOperand.Predicate(uPr)
            ) => Some(Instruction.Uplop3PredPredPredPred(uPu, uPp, uPq, uPr, modifiers, guard))
        case _ => None
      }
  }
  object Uplop3PredPredPredPredImm extends Definition(
        "Uplop3PredPredPredPredImm",
        "UPLOP3",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("uPp", OperandKind.Predicate),
          Slot("uPq", OperandKind.Predicate),
          Slot("uPr", OperandKind.Predicate),
          Slot("uimm8", OperandKind.Immediate)
        ),
        Vec("uplop3_lut_1out_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Predicate(uPq),
              InstructionOperand.Predicate(uPr),
              InstructionOperand.Immediate(uimm8)
            ) => Some(Instruction.Uplop3PredPredPredPredImm(uPu, uPp, uPq, uPr, uimm8, modifiers, guard))
        case _ => None
      }
  }
  object Uplop3PredPredPredPredPredImmImm extends Definition(
        "Uplop3PredPredPredPredPredImmImm",
        "UPLOP3",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("uPv", OperandKind.Predicate),
          Slot("uPp", OperandKind.Predicate),
          Slot("uPq", OperandKind.Predicate),
          Slot("uPr", OperandKind.Predicate),
          Slot("uimm8", OperandKind.Immediate),
          Slot("vimm8", OperandKind.Immediate)
        ),
        Vec("uplop3_lut_2out_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Predicate(uPv),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Predicate(uPq),
              InstructionOperand.Predicate(uPr),
              InstructionOperand.Immediate(uimm8),
              InstructionOperand.Immediate(vimm8)
            ) =>
          Some(Instruction.Uplop3PredPredPredPredPredImmImm(uPu, uPv, uPp, uPq, uPr, uimm8, vimm8, modifiers, guard))
        case _ => None
      }
  }
  object Uplop3PredPredPredRegPredImmImm extends Definition(
        "Uplop3PredPredPredRegPredImmImm",
        "UPLOP3",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("uPv", OperandKind.Predicate),
          Slot("uPp", OperandKind.Predicate),
          Slot("srcB", OperandKind.Register),
          Slot("uPr", OperandKind.Predicate),
          Slot("uimm8", OperandKind.Immediate),
          Slot("vimm8", OperandKind.Immediate)
        ),
        Vec("uplop3_lut_2out_1reg__URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Predicate(uPv),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(uPr),
              InstructionOperand.Immediate(uimm8),
              InstructionOperand.Immediate(vimm8)
            ) =>
          Some(Instruction.Uplop3PredPredPredRegPredImmImm(uPu, uPv, uPp, srcB, uPr, uimm8, vimm8, modifiers, guard))
        case _ => None
      }
  }
  object Uplop3PredPredPredRegRegImmImm extends Definition(
        "Uplop3PredPredPredRegRegImmImm",
        "UPLOP3",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("uPv", OperandKind.Predicate),
          Slot("uPp", OperandKind.Predicate),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uimm8", OperandKind.Immediate),
          Slot("vimm8", OperandKind.Immediate)
        ),
        Vec("uplop3_lut_2out_2reg__URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Predicate(uPv),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(uimm8),
              InstructionOperand.Immediate(vimm8)
            ) =>
          Some(Instruction.Uplop3PredPredPredRegRegImmImm(uPu, uPv, uPp, srcB, srcC, uimm8, vimm8, modifiers, guard))
        case _ => None
      }
  }
  object Uplop3PredPredRegPredImm extends Definition(
        "Uplop3PredPredRegPredImm",
        "UPLOP3",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("uPp", OperandKind.Predicate),
          Slot("srcB", OperandKind.Register),
          Slot("uPr", OperandKind.Predicate),
          Slot("uimm8", OperandKind.Immediate)
        ),
        Vec("uplop3_lut_1out_1reg__URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(uPr),
              InstructionOperand.Immediate(uimm8)
            ) => Some(Instruction.Uplop3PredPredRegPredImm(uPu, uPp, srcB, uPr, uimm8, modifiers, guard))
        case _ => None
      }
  }
  object Uplop3PredPredRegRegImm extends Definition(
        "Uplop3PredPredRegRegImm",
        "UPLOP3",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("uPp", OperandKind.Predicate),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uimm8", OperandKind.Immediate)
        ),
        Vec("uplop3_lut_1out_2reg__URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(uimm8)
            ) => Some(Instruction.Uplop3PredPredRegRegImm(uPu, uPp, srcB, srcC, uimm8, modifiers, guard))
        case _ => None
      }
  }
  object Uplop3PredPredRegRegRegImmImm extends Definition(
        "Uplop3PredPredRegRegRegImmImm",
        "UPLOP3",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("uPv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uimm8", OperandKind.Immediate),
          Slot("vimm8", OperandKind.Immediate)
        ),
        Vec("uplop3_lut_2out_3reg__URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Predicate(uPv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(uimm8),
              InstructionOperand.Immediate(vimm8)
            ) =>
          Some(Instruction.Uplop3PredPredRegRegRegImmImm(uPu, uPv, srcA, srcB, srcC, uimm8, vimm8, modifiers, guard))
        case _ => None
      }
  }
  object Uplop3PredRegRegRegImm extends Definition(
        "Uplop3PredRegRegRegImm",
        "UPLOP3",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uimm8", OperandKind.Immediate)
        ),
        Vec("uplop3_lut_1out_3reg__URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(uimm8)
            ) => Some(Instruction.Uplop3PredRegRegRegImm(uPu, srcA, srcB, srcC, uimm8, modifiers, guard))
        case _ => None
      }
  }
  object Uplop3udpPipePredPredPredPred extends Definition(
        "Uplop3udpPipePredPredPredPred",
        "UPLOP3udp_pipe",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("uPp", OperandKind.Predicate),
          Slot("uPq", OperandKind.Predicate),
          Slot("uPr", OperandKind.Predicate)
        ),
        Vec("uplop3_1out_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Predicate(uPq),
              InstructionOperand.Predicate(uPr)
            ) => Some(Instruction.Uplop3udpPipePredPredPredPred(uPu, uPp, uPq, uPr, modifiers, guard))
        case _ => None
      }
  }
  val definitions: Vec[Definition] = Vec(
    UfselRegRegImmPred,
    UfselRegRegRegPred,
    UfseludpPipeRegRegImmPred,
    UfseludpPipeRegRegRegPred,
    UfsetRegRegImm,
    UfsetRegRegImmPred,
    UfsetRegRegReg,
    UfsetRegRegRegPred,
    UfsetpPredPredRegImmPred,
    UfsetpPredPredRegRegPred,
    UfsetpPredRegImm,
    UfsetpPredRegReg,
    UfsetpudpPipePredPredRegImmPred,
    UfsetpudpPipePredPredRegRegPred,
    UfsetpudpPipePredRegImm,
    UfsetpudpPipePredRegReg,
    UfsetudpPipeRegRegImm,
    UfsetudpPipeRegRegImmPred,
    UfsetudpPipeRegRegReg,
    UfsetudpPipeRegRegRegPred,
    UgetnextworkidAddressAddress,
    UgetnextworkidudpPipeAddressAddress,
    Ui2fRegImm,
    Ui2fRegReg,
    Ui2fpRegImm,
    Ui2fpRegReg,
    Ui2fpudpPipeRegImm,
    Ui2fpudpPipeRegReg,
    Ui2fudpPipeRegImm,
    Ui2fudpPipeRegReg,
    Ui2iRegImm,
    Ui2iRegReg,
    Ui2ipRegRegImmReg,
    Ui2ipRegRegRegReg,
    Ui2ipudpPipeRegRegImmReg,
    Ui2ipudpPipeRegRegRegReg,
    Ui2iudpPipeRegImm,
    Ui2iudpPipeRegReg,
    UiabsRegImm,
    UiabsRegReg,
    UiabsudpPipeRegImm,
    UiabsudpPipeRegReg,
    Uiadd3RegPredPredRegImmReg,
    Uiadd3RegPredPredRegImmRegPredPred,
    Uiadd3RegPredPredRegRegReg,
    Uiadd3RegPredPredRegRegRegPredPred,
    Uiadd3RegPredRegImmReg,
    Uiadd3RegPredRegImmRegPredPred,
    Uiadd3RegPredRegRegReg,
    Uiadd3RegPredRegRegRegPredPred,
    Uiadd3RegRegImmReg,
    Uiadd3RegRegImmRegPredPred,
    Uiadd3RegRegRegReg,
    Uiadd3RegRegRegRegPredPred,
    Uiadd3udpPipeRegPredPredRegImmReg,
    Uiadd3udpPipeRegPredPredRegImmRegPredPred,
    Uiadd3udpPipeRegPredPredRegRegReg,
    Uiadd3udpPipeRegPredPredRegRegRegPredPred,
    Uiadd3udpPipeRegPredRegImmReg,
    Uiadd3udpPipeRegPredRegImmRegPredPred,
    Uiadd3udpPipeRegPredRegRegReg,
    Uiadd3udpPipeRegPredRegRegRegPredPred,
    Uiadd3udpPipeRegRegImmReg,
    Uiadd3udpPipeRegRegImmRegPredPred,
    Uiadd3udpPipeRegRegRegReg,
    Uiadd3udpPipeRegRegRegRegPredPred,
    UimadRegPredRegImmReg,
    UimadRegPredRegImmRegPred,
    UimadRegPredRegRegReg,
    UimadRegPredRegRegRegPred,
    UimadRegRegImmReg,
    UimadRegRegImmRegPred,
    UimadRegRegRegImm,
    UimadRegRegRegImmPred,
    UimadRegRegRegReg,
    UimadRegRegRegRegPred,
    UimadudpPipeRegPredRegImmReg,
    UimadudpPipeRegPredRegImmRegPred,
    UimadudpPipeRegPredRegRegReg,
    UimadudpPipeRegPredRegRegRegPred,
    UimadudpPipeRegRegImmReg,
    UimadudpPipeRegRegImmRegPred,
    UimadudpPipeRegRegRegImm,
    UimadudpPipeRegRegRegImmPred,
    UimadudpPipeRegRegRegReg,
    UimadudpPipeRegRegRegRegPred,
    UimnmxPredPredRegRegImmPred,
    UimnmxPredPredRegRegImmPredPred,
    UimnmxPredPredRegRegRegPred,
    UimnmxPredPredRegRegRegPredPred,
    UimnmxPredRegRegImmPred,
    UimnmxPredRegRegImmPredPred,
    UimnmxPredRegRegRegPred,
    UimnmxPredRegRegRegPredPred,
    UimnmxRegRegImmPred,
    UimnmxRegRegImmPredPred,
    UimnmxRegRegRegPred,
    UimnmxRegRegRegPredPred,
    UimnmxudpPipePredPredRegRegImmPred,
    UimnmxudpPipePredPredRegRegImmPredPred,
    UimnmxudpPipePredPredRegRegRegPred,
    UimnmxudpPipePredPredRegRegRegPredPred,
    UimnmxudpPipePredRegRegImmPred,
    UimnmxudpPipePredRegRegImmPredPred,
    UimnmxudpPipePredRegRegRegPred,
    UimnmxudpPipePredRegRegRegPredPred,
    UimnmxudpPipeRegRegImmPred,
    UimnmxudpPipeRegRegImmPredPred,
    UimnmxudpPipeRegRegRegPred,
    UimnmxudpPipeRegRegRegPredPred,
    UisetpPredPredRegImmPred,
    UisetpPredPredRegImmPredPred,
    UisetpPredPredRegRegPred,
    UisetpPredPredRegRegPredPred,
    UisetpPredRegImm,
    UisetpPredRegImmPred,
    UisetpPredRegReg,
    UisetpPredRegRegPred,
    UisetpudpPipePredPredRegImmPred,
    UisetpudpPipePredPredRegImmPredPred,
    UisetpudpPipePredPredRegRegPred,
    UisetpudpPipePredPredRegRegPredPred,
    UisetpudpPipePredRegImm,
    UisetpudpPipePredRegImmPred,
    UisetpudpPipePredRegReg,
    UisetpudpPipePredRegRegPred,
    UldcRegAddress,
    UldcRegAddressImm,
    UldcRegAddressPred,
    UldcRegConst,
    UldcudpPipeRegAddress,
    UldcudpPipeRegAddressImm,
    UldcudpPipeRegAddressPred,
    UldcudpPipeRegConst,
    UleaRegPredRegImmImm,
    UleaRegPredRegImmImmPred,
    UleaRegPredRegImmRegImm,
    UleaRegPredRegImmRegImmPred,
    UleaRegPredRegRegImm,
    UleaRegPredRegRegImmImm,
    UleaRegPredRegRegImmImmPred,
    UleaRegPredRegRegImmPred,
    UleaRegPredRegRegRegImm,
    UleaRegPredRegRegRegImmPred,
    UleaRegRegImmImm,
    UleaRegRegImmImmPred,
    UleaRegRegImmRegImm,
    UleaRegRegImmRegImmPred,
    UleaRegRegRegImm,
    UleaRegRegRegImmImm,
    UleaRegRegRegImmImmPred,
    UleaRegRegRegImmPred,
    UleaRegRegRegRegImm,
    UleaRegRegRegRegImmPred,
    UleaudpPipeRegPredRegImmImm,
    UleaudpPipeRegPredRegImmImmPred,
    UleaudpPipeRegPredRegImmRegImm,
    UleaudpPipeRegPredRegImmRegImmPred,
    UleaudpPipeRegPredRegRegImm,
    UleaudpPipeRegPredRegRegImmImm,
    UleaudpPipeRegPredRegRegImmImmPred,
    UleaudpPipeRegPredRegRegImmPred,
    UleaudpPipeRegPredRegRegRegImm,
    UleaudpPipeRegPredRegRegRegImmPred,
    UleaudpPipeRegRegImmImm,
    UleaudpPipeRegRegImmImmPred,
    UleaudpPipeRegRegImmRegImm,
    UleaudpPipeRegRegImmRegImmPred,
    UleaudpPipeRegRegRegImm,
    UleaudpPipeRegRegRegImmImm,
    UleaudpPipeRegRegRegImmImmPred,
    UleaudpPipeRegRegRegImmPred,
    UleaudpPipeRegRegRegRegImm,
    UleaudpPipeRegRegRegRegImmPred,
    UlepcReg,
    UlepcRegImm,
    UlepcudpPipeReg,
    UlepcudpPipeRegImm,
    UlopPredRegRegImm,
    UlopPredRegRegImmPred,
    UlopPredRegRegReg,
    UlopPredRegRegRegPred,
    UlopRegRegImm,
    UlopRegRegImmPred,
    UlopRegRegReg,
    UlopRegRegRegPred,
    Ulop3PredRegRegImmReg,
    Ulop3PredRegRegImmRegImm,
    Ulop3PredRegRegImmRegImmPred,
    Ulop3PredRegRegImmRegPred,
    Ulop3PredRegRegRegReg,
    Ulop3PredRegRegRegRegImm,
    Ulop3PredRegRegRegRegImmPred,
    Ulop3PredRegRegRegRegPred,
    Ulop3RegRegImmReg,
    Ulop3RegRegImmRegImm,
    Ulop3RegRegImmRegImmPred,
    Ulop3RegRegImmRegPred,
    Ulop3RegRegRegReg,
    Ulop3RegRegRegRegImm,
    Ulop3RegRegRegRegImmPred,
    Ulop3RegRegRegRegPred,
    Ulop32iPredRegRegImm,
    Ulop32iPredRegRegImmPred,
    Ulop32iRegRegImm,
    Ulop32iRegRegImmPred,
    Ulop32iudpPipePredRegRegImm,
    Ulop32iudpPipePredRegRegImmPred,
    Ulop32iudpPipeRegRegImm,
    Ulop32iudpPipeRegRegImmPred,
    Ulop3udpPipePredRegRegImmReg,
    Ulop3udpPipePredRegRegImmRegImm,
    Ulop3udpPipePredRegRegImmRegImmPred,
    Ulop3udpPipePredRegRegImmRegPred,
    Ulop3udpPipePredRegRegRegReg,
    Ulop3udpPipePredRegRegRegRegImm,
    Ulop3udpPipePredRegRegRegRegImmPred,
    Ulop3udpPipePredRegRegRegRegPred,
    Ulop3udpPipeRegRegImmReg,
    Ulop3udpPipeRegRegImmRegImm,
    Ulop3udpPipeRegRegImmRegImmPred,
    Ulop3udpPipeRegRegImmRegPred,
    Ulop3udpPipeRegRegRegReg,
    Ulop3udpPipeRegRegRegRegImm,
    Ulop3udpPipeRegRegRegRegImmPred,
    Ulop3udpPipeRegRegRegRegPred,
    UlopudpPipePredRegRegImm,
    UlopudpPipePredRegRegImmPred,
    UlopudpPipePredRegRegReg,
    UlopudpPipePredRegRegRegPred,
    UlopudpPipeRegRegImm,
    UlopudpPipeRegRegImmPred,
    UlopudpPipeRegRegReg,
    UlopudpPipeRegRegRegPred,
    UmemsetsAddressRegReg,
    UmemsetsudpPipeAddressRegReg,
    UmovRegImm,
    UmovRegReg,
    UmovudpPipeRegImm,
    UmovudpPipeRegReg,
    Up2urReg,
    Up2urRegRegImm,
    Up2urRegRegReg,
    Up2urudpPipeReg,
    Up2urudpPipeRegRegImm,
    Up2urudpPipeRegRegReg,
    Uplop3PredPredPredPred,
    Uplop3PredPredPredPredImm,
    Uplop3PredPredPredPredPredImmImm,
    Uplop3PredPredPredRegPredImmImm,
    Uplop3PredPredPredRegRegImmImm,
    Uplop3PredPredRegPredImm,
    Uplop3PredPredRegRegImm,
    Uplop3PredPredRegRegRegImmImm,
    Uplop3PredRegRegRegImm,
    Uplop3udpPipePredPredPredPred
  )
}
