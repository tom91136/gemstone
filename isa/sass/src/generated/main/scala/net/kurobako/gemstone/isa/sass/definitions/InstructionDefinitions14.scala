// Generated SASS instruction definitions and checked operand constructors. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.sass
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import Instruction.*
@javax.annotation.processing.Generated(Array("sbt codegen"))
object InstructionDefinitions14 {
  object UpopcRegImm extends Definition(
        "UpopcRegImm",
        "UPOPC",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("upopc__URuIUR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.UpopcRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object UpopcRegReg extends Definition(
        "UpopcRegReg",
        "UPOPC",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("upopc__URURUR_URUUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.UpopcRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UpopcudpPipeRegImm extends Definition(
        "UpopcudpPipeRegImm",
        "UPOPCudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("upopc__URuIUR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.UpopcudpPipeRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object UpopcudpPipeRegReg extends Definition(
        "UpopcudpPipeRegReg",
        "UPOPCudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("upopc__URURUR_URUUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.UpopcudpPipeRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UprmtRegRegImmReg extends Definition(
        "UprmtRegRegImmReg",
        "UPRMT",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("uprmt__URIUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.UprmtRegRegImmReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object UprmtRegRegRegReg extends Definition(
        "UprmtRegRegRegReg",
        "UPRMT",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("uprmt__URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.UprmtRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object UprmtudpPipeRegRegImmReg extends Definition(
        "UprmtudpPipeRegRegImmReg",
        "UPRMTudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("uprmt__URIUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.UprmtudpPipeRegRegImmReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object UprmtudpPipeRegRegRegReg extends Definition(
        "UprmtudpPipeRegRegRegReg",
        "UPRMTudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("uprmt__URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.UprmtudpPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object UpsetpPredPredPred extends Definition(
        "UpsetpPredPredPred",
        "UPSETP",
        Vec(Slot("uPu", OperandKind.Predicate), Slot("uPp", OperandKind.Predicate), Slot("uPq", OperandKind.Predicate)),
        Vec("upsetp_simple_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Predicate(uPq)
            ) => Some(Instruction.UpsetpPredPredPred(uPu, uPp, uPq, modifiers, guard))
        case _ => None
      }
  }
  object UpsetpPredPredPredPredPred extends Definition(
        "UpsetpPredPredPredPredPred",
        "UPSETP",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("uPv", OperandKind.Predicate),
          Slot("uPp", OperandKind.Predicate),
          Slot("uPq", OperandKind.Predicate),
          Slot("uPr", OperandKind.Predicate)
        ),
        Vec("upsetp_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Predicate(uPv),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Predicate(uPq),
              InstructionOperand.Predicate(uPr)
            ) => Some(Instruction.UpsetpPredPredPredPredPred(uPu, uPv, uPp, uPq, uPr, modifiers, guard))
        case _ => None
      }
  }
  object UpsetpudpPipePredPredPred extends Definition(
        "UpsetpudpPipePredPredPred",
        "UPSETPudp_pipe",
        Vec(Slot("uPu", OperandKind.Predicate), Slot("uPp", OperandKind.Predicate), Slot("uPq", OperandKind.Predicate)),
        Vec("upsetp_simple_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Predicate(uPq)
            ) => Some(Instruction.UpsetpudpPipePredPredPred(uPu, uPp, uPq, modifiers, guard))
        case _ => None
      }
  }
  object UpsetpudpPipePredPredPredPredPred extends Definition(
        "UpsetpudpPipePredPredPredPredPred",
        "UPSETPudp_pipe",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("uPv", OperandKind.Predicate),
          Slot("uPp", OperandKind.Predicate),
          Slot("uPq", OperandKind.Predicate),
          Slot("uPr", OperandKind.Predicate)
        ),
        Vec("upsetp_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Predicate(uPv),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Predicate(uPq),
              InstructionOperand.Predicate(uPr)
            ) => Some(Instruction.UpsetpudpPipePredPredPredPredPred(uPu, uPv, uPp, uPq, uPr, modifiers, guard))
        case _ => None
      }
  }
  object Ur2upReg extends Definition("Ur2upReg", "UR2UP", Vec(Slot("srcA", OperandKind.Register)), Vec("ur2up__Imm")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA)) => Some(Instruction.Ur2upReg(srcA, modifiers, guard))
        case _ => None
      }
  }
  object Ur2upRegImm extends Definition(
        "Ur2upRegImm",
        "UR2UP",
        Vec(Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("ur2up__Imm")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.Ur2upRegImm(srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object Ur2upRegReg extends Definition(
        "Ur2upRegReg",
        "UR2UP",
        Vec(Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("ur2up__URb")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA), InstructionOperand.Register(srcB)) =>
          Some(Instruction.Ur2upRegReg(srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object Ur2upudpPipeReg extends Definition(
        "Ur2upudpPipeReg",
        "UR2UPudp_pipe",
        Vec(Slot("srcA", OperandKind.Register)),
        Vec("ur2up__Imm")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA)) => Some(Instruction.Ur2upudpPipeReg(srcA, modifiers, guard))
        case _ => None
      }
  }
  object Ur2upudpPipeRegImm extends Definition(
        "Ur2upudpPipeRegImm",
        "UR2UPudp_pipe",
        Vec(Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("ur2up__Imm")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.Ur2upudpPipeRegImm(srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object Ur2upudpPipeRegReg extends Definition(
        "Ur2upudpPipeRegReg",
        "UR2UPudp_pipe",
        Vec(Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("ur2up__URb")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA), InstructionOperand.Register(srcB)) =>
          Some(Instruction.Ur2upudpPipeRegReg(srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UredgrAddressReg extends Definition(
        "UredgrAddressReg",
        "UREDGR",
        Vec(Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register)),
        Vec("uredgr_", "uredgr_one_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA), InstructionOperand.Register(srcB)) =>
          Some(Instruction.UredgrAddressReg(srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UredgrudpPipeAddressReg extends Definition(
        "UredgrudpPipeAddressReg",
        "UREDGRudp_pipe",
        Vec(Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register)),
        Vec("uredgr_", "uredgr_one_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA), InstructionOperand.Register(srcB)) =>
          Some(Instruction.UredgrudpPipeAddressReg(srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UselRegRegImmPred extends Definition(
        "UselRegRegImmPred",
        "USEL",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("usel_64__URIR", "usel__URuIUR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UselRegRegImmPred(dst, srcA, sb, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UselRegRegRegPred extends Definition(
        "UselRegRegRegPred",
        "USEL",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("usel_64__UUU", "usel__URURUR_UUU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UselRegRegRegPred(dst, srcA, srcB, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UseludpPipeRegRegImmPred extends Definition(
        "UseludpPipeRegRegImmPred",
        "USELudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("usel_64__URIR", "usel__URuIUR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UseludpPipeRegRegImmPred(dst, srcA, sb, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UseludpPipeRegRegRegPred extends Definition(
        "UseludpPipeRegRegRegPred",
        "USELudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("usel_64__UUU", "usel__URURUR_UUU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UseludpPipeRegRegRegPred(dst, srcA, srcB, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UsetmaxregImm extends Definition(
        "UsetmaxregImm",
        "USETMAXREG",
        Vec(Slot("sb", OperandKind.Immediate)),
        Vec("usetmaxregAlloc__Ib_dealloc", "usetmaxreg__Ib_dealloc")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb)) => Some(Instruction.UsetmaxregImm(sb, modifiers, guard))
        case _ => None
      }
  }
  object UsetmaxregPredImm extends Definition(
        "UsetmaxregPredImm",
        "USETMAXREG",
        Vec(Slot("uPu", OperandKind.Predicate), Slot("sb", OperandKind.Immediate)),
        Vec("usetmaxregAlloc__Ib_alloc", "usetmaxreg__Ib_alloc")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(uPu), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.UsetmaxregPredImm(uPu, sb, modifiers, guard))
        case _ => None
      }
  }
  object UsetmaxregPredReg extends Definition(
        "UsetmaxregPredReg",
        "USETMAXREG",
        Vec(Slot("uPu", OperandKind.Predicate), Slot("srcB", OperandKind.Register)),
        Vec("usetmaxregAlloc__URb_alloc", "usetmaxreg__URb_alloc")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(uPu), InstructionOperand.Register(srcB)) =>
          Some(Instruction.UsetmaxregPredReg(uPu, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UsetmaxregReg extends Definition(
        "UsetmaxregReg",
        "USETMAXREG",
        Vec(Slot("srcB", OperandKind.Register)),
        Vec("usetmaxregAlloc__URb_dealloc", "usetmaxreg__URb_dealloc")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcB)) => Some(Instruction.UsetmaxregReg(srcB, modifiers, guard))
        case _ => None
      }
  }
  object UsetmaxregudpPipeImm extends Definition(
        "UsetmaxregudpPipeImm",
        "USETMAXREGudp_pipe",
        Vec(Slot("sb", OperandKind.Immediate)),
        Vec("usetmaxregAlloc__Ib_dealloc", "usetmaxreg__Ib_dealloc")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb)) => Some(Instruction.UsetmaxregudpPipeImm(sb, modifiers, guard))
        case _ => None
      }
  }
  object UsetmaxregudpPipePredImm extends Definition(
        "UsetmaxregudpPipePredImm",
        "USETMAXREGudp_pipe",
        Vec(Slot("uPu", OperandKind.Predicate), Slot("sb", OperandKind.Immediate)),
        Vec("usetmaxregAlloc__Ib_alloc", "usetmaxreg__Ib_alloc")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(uPu), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.UsetmaxregudpPipePredImm(uPu, sb, modifiers, guard))
        case _ => None
      }
  }
  object UsetmaxregudpPipePredReg extends Definition(
        "UsetmaxregudpPipePredReg",
        "USETMAXREGudp_pipe",
        Vec(Slot("uPu", OperandKind.Predicate), Slot("srcB", OperandKind.Register)),
        Vec("usetmaxregAlloc__URb_alloc", "usetmaxreg__URb_alloc")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(uPu), InstructionOperand.Register(srcB)) =>
          Some(Instruction.UsetmaxregudpPipePredReg(uPu, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UsetmaxregudpPipeReg extends Definition(
        "UsetmaxregudpPipeReg",
        "USETMAXREGudp_pipe",
        Vec(Slot("srcB", OperandKind.Register)),
        Vec("usetmaxregAlloc__URb_dealloc", "usetmaxreg__URb_dealloc")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcB)) => Some(Instruction.UsetmaxregudpPipeReg(srcB, modifiers, guard))
        case _ => None
      }
  }
  object UsetshmszNoOperands extends Definition("UsetshmszNoOperands", "USETSHMSZ", Vec(), Vec("usetshmsz__FLUSH")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.UsetshmszNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object UsetshmszImm
      extends Definition("UsetshmszImm", "USETSHMSZ", Vec(Slot("sb", OperandKind.Immediate)), Vec("usetshmsz__Ib")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb)) => Some(Instruction.UsetshmszImm(sb, modifiers, guard))
        case _ => None
      }
  }
  object UsetshmszReg
      extends Definition("UsetshmszReg", "USETSHMSZ", Vec(Slot("srcB", OperandKind.Register)), Vec("usetshmsz__URb")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcB)) => Some(Instruction.UsetshmszReg(srcB, modifiers, guard))
        case _ => None
      }
  }
  object UsetshmszudpPipeNoOperands
      extends Definition("UsetshmszudpPipeNoOperands", "USETSHMSZudp_pipe", Vec(), Vec("usetshmsz__FLUSH")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.UsetshmszudpPipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object UsetshmszudpPipeImm extends Definition(
        "UsetshmszudpPipeImm",
        "USETSHMSZudp_pipe",
        Vec(Slot("sb", OperandKind.Immediate)),
        Vec("usetshmsz__Ib")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb)) => Some(Instruction.UsetshmszudpPipeImm(sb, modifiers, guard))
        case _ => None
      }
  }
  object UsetshmszudpPipeReg extends Definition(
        "UsetshmszudpPipeReg",
        "USETSHMSZudp_pipe",
        Vec(Slot("srcB", OperandKind.Register)),
        Vec("usetshmsz__URb")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcB)) => Some(Instruction.UsetshmszudpPipeReg(srcB, modifiers, guard))
        case _ => None
      }
  }
  object UsgxtRegRegImm extends Definition(
        "UsgxtRegRegImm",
        "USGXT",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("usgxt__URuIUR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.UsgxtRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object UsgxtRegRegReg extends Definition(
        "UsgxtRegRegReg",
        "USGXT",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("usgxt__URURUR_UUU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.UsgxtRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UsgxtudpPipeRegRegImm extends Definition(
        "UsgxtudpPipeRegRegImm",
        "USGXTudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("usgxt__URuIUR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.UsgxtudpPipeRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object UsgxtudpPipeRegRegReg extends Definition(
        "UsgxtudpPipeRegRegReg",
        "USGXTudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("usgxt__URURUR_UUU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.UsgxtudpPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UshfRegRegImmReg extends Definition(
        "UshfRegRegImmReg",
        "USHF",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("ushf__URuIUR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.UshfRegRegImmReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object UshfRegRegRegImm extends Definition(
        "UshfRegRegRegImm",
        "USHF",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate)
        ),
        Vec("ushf__URURuI_URRI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.UshfRegRegRegImm(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object UshfRegRegRegReg extends Definition(
        "UshfRegRegRegReg",
        "USHF",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("ushf__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.UshfRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object UshfudpPipeRegRegImmReg extends Definition(
        "UshfudpPipeRegRegImmReg",
        "USHFudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("ushf__URuIUR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.UshfudpPipeRegRegImmReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object UshfudpPipeRegRegRegImm extends Definition(
        "UshfudpPipeRegRegRegImm",
        "USHFudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate)
        ),
        Vec("ushf__URURuI_URRI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.UshfudpPipeRegRegRegImm(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object UshfudpPipeRegRegRegReg extends Definition(
        "UshfudpPipeRegRegRegReg",
        "USHFudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("ushf__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.UshfudpPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object UshlRegImmReg extends Definition(
        "UshlRegImmReg",
        "USHL",
        Vec(Slot("dst", OperandKind.Register), Slot("sa", OperandKind.Immediate), Slot("srcB", OperandKind.Register)),
        Vec("ushl_imm_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Immediate(sa),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.UshlRegImmReg(dst, sa, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UshlRegRegImm extends Definition(
        "UshlRegRegImm",
        "USHL",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("ushl__URuIUR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.UshlRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object UshlRegRegReg extends Definition(
        "UshlRegRegReg",
        "USHL",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("ushl__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.UshlRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UshludpPipeRegImmReg extends Definition(
        "UshludpPipeRegImmReg",
        "USHLudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("sa", OperandKind.Immediate), Slot("srcB", OperandKind.Register)),
        Vec("ushl_imm_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Immediate(sa),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.UshludpPipeRegImmReg(dst, sa, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UshludpPipeRegRegImm extends Definition(
        "UshludpPipeRegRegImm",
        "USHLudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("ushl__URuIUR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.UshludpPipeRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object UshludpPipeRegRegReg extends Definition(
        "UshludpPipeRegRegReg",
        "USHLudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("ushl__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.UshludpPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UshrRegRegImm extends Definition(
        "UshrRegRegImm",
        "USHR",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("ushr__URIR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.UshrRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object UshrRegRegReg extends Definition(
        "UshrRegRegReg",
        "USHR",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("ushr__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.UshrRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UshrudpPipeRegRegImm extends Definition(
        "UshrudpPipeRegRegImm",
        "USHRudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("ushr__URIR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.UshrudpPipeRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object UshrudpPipeRegRegReg extends Definition(
        "UshrudpPipeRegRegReg",
        "USHRudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("ushr__URURUR_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.UshrudpPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UstgrAddressReg extends Definition(
        "UstgrAddressReg",
        "USTGR",
        Vec(Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register)),
        Vec("ustgr_", "ustgr_one_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA), InstructionOperand.Register(srcB)) =>
          Some(Instruction.UstgrAddressReg(srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UstgrudpPipeAddressReg extends Definition(
        "UstgrudpPipeAddressReg",
        "USTGRudp_pipe",
        Vec(Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register)),
        Vec("ustgr_", "ustgr_one_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA), InstructionOperand.Register(srcB)) =>
          Some(Instruction.UstgrudpPipeAddressReg(srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UtcatomswsPredRegReg extends Definition(
        "UtcatomswsPredRegReg",
        "UTCATOMSWS",
        Vec(Slot("uPu", OperandKind.Predicate), Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("utcatomsws_fas_", "utcatomsws_fas_one_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.UtcatomswsPredRegReg(uPu, dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UtcatomswsPredRegRegReg extends Definition(
        "UtcatomswsPredRegRegReg",
        "UTCATOMSWS",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("utcatomsws_cas_", "utcatomsws_cas_one_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.UtcatomswsPredRegRegReg(uPu, dst, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object UtcatomswsRegReg extends Definition(
        "UtcatomswsRegReg",
        "UTCATOMSWS",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("utcatomsws_op_", "utcatomsws_op_one_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.UtcatomswsRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UtcatomswsudpPipePredRegReg extends Definition(
        "UtcatomswsudpPipePredRegReg",
        "UTCATOMSWSudp_pipe",
        Vec(Slot("uPu", OperandKind.Predicate), Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("utcatomsws_fas_", "utcatomsws_fas_one_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.UtcatomswsudpPipePredRegReg(uPu, dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UtcatomswsudpPipePredRegRegReg extends Definition(
        "UtcatomswsudpPipePredRegRegReg",
        "UTCATOMSWSudp_pipe",
        Vec(
          Slot("uPu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("utcatomsws_cas_", "utcatomsws_cas_one_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.UtcatomswsudpPipePredRegRegReg(uPu, dst, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object UtcatomswsudpPipeRegReg extends Definition(
        "UtcatomswsudpPipeRegReg",
        "UTCATOMSWSudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("utcatomsws_op_", "utcatomsws_op_one_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.UtcatomswsudpPipeRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UtcbarNoOperands
      extends Definition("UtcbarNoOperands", "UTCBAR", Vec(), Vec("utcbar_flush_", "utcbar_flush_one_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.UtcbarNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object UtcbarAddressRegReg extends Definition(
        "UtcbarAddressRegReg",
        "UTCBAR",
        Vec(Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register), Slot("srcC", OperandKind.Register)),
        Vec("utcbar__1CTA", "utcbar__2CTA", "utcbar_one__1CTA", "utcbar_one__2CTA")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.UtcbarAddressRegReg(srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object UtcbarudpPipeNoOperands extends Definition(
        "UtcbarudpPipeNoOperands",
        "UTCBARudp_pipe",
        Vec(),
        Vec("utcbar_flush_", "utcbar_flush_one_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.UtcbarudpPipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object UtcbarudpPipeAddressRegReg extends Definition(
        "UtcbarudpPipeAddressRegReg",
        "UTCBARudp_pipe",
        Vec(Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register), Slot("srcC", OperandKind.Register)),
        Vec("utcbar__1CTA", "utcbar__2CTA", "utcbar_one__1CTA", "utcbar_one__2CTA")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.UtcbarudpPipeAddressRegReg(srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object UtccpAddressAddress extends Definition(
        "UtccpAddressAddress",
        "UTCCP",
        Vec(Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Address)),
        Vec("utccp__1CTA", "utccp__2CTA", "utccp_one__1CTA", "utccp_one__2CTA")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA), InstructionOperand.Address(srcB)) =>
          Some(Instruction.UtccpAddressAddress(srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UtccpudpPipeAddressAddress extends Definition(
        "UtccpudpPipeAddressAddress",
        "UTCCPudp_pipe",
        Vec(Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Address)),
        Vec("utccp__1CTA", "utccp__2CTA", "utccp_one__1CTA", "utccp_one__2CTA")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA), InstructionOperand.Address(srcB)) =>
          Some(Instruction.UtccpudpPipeAddressAddress(srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UtchmmaAddressAddressAddressAddressAddressRegPred extends Definition(
        "UtchmmaAddressAddressAddressAddressAddressRegPred",
        "UTCHMMA",
        Vec(
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Address),
          Slot("srcC", OperandKind.Address),
          Slot("uRe", OperandKind.Address),
          Slot("uRh", OperandKind.Address),
          Slot("uRi", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec(
          "utchmma_1cta__A_gdesc",
          "utchmma_1cta__A_tmem",
          "utchmma_1cta_one__A_gdesc",
          "utchmma_1cta_one__A_tmem",
          "utchmma_2cta__A_gdesc",
          "utchmma_2cta__A_tmem",
          "utchmma_2cta_one__A_gdesc",
          "utchmma_2cta_one__A_tmem"
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
              InstructionOperand.Register(uRi),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UtchmmaAddressAddressAddressAddressAddressRegPred(
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
  object UtchmmaAddressAddressAddressAddressAddressRegPredImm extends Definition(
        "UtchmmaAddressAddressAddressAddressAddressRegPredImm",
        "UTCHMMA",
        Vec(
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Address),
          Slot("srcC", OperandKind.Address),
          Slot("uRe", OperandKind.Address),
          Slot("uRh", OperandKind.Address),
          Slot("uRi", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("scaleU4", OperandKind.Immediate)
        ),
        Vec(
          "utchmma_1cta__A_gdesc",
          "utchmma_1cta__A_tmem",
          "utchmma_1cta_one__A_gdesc",
          "utchmma_1cta_one__A_tmem",
          "utchmma_2cta__A_gdesc",
          "utchmma_2cta__A_tmem",
          "utchmma_2cta_one__A_gdesc",
          "utchmma_2cta_one__A_tmem"
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
              InstructionOperand.Register(uRi),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Immediate(scaleU4)
            ) => Some(Instruction.UtchmmaAddressAddressAddressAddressAddressRegPredImm(
            srcA,
            srcB,
            srcC,
            uRe,
            uRh,
            uRi,
            uPp,
            scaleU4,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object UtchmmaudpPipeAddressAddressAddressAddressAddressRegPred extends Definition(
        "UtchmmaudpPipeAddressAddressAddressAddressAddressRegPred",
        "UTCHMMAudp_pipe",
        Vec(
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Address),
          Slot("srcC", OperandKind.Address),
          Slot("uRe", OperandKind.Address),
          Slot("uRh", OperandKind.Address),
          Slot("uRi", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec(
          "utchmma_1cta__A_gdesc",
          "utchmma_1cta__A_tmem",
          "utchmma_1cta_one__A_gdesc",
          "utchmma_1cta_one__A_tmem",
          "utchmma_2cta__A_gdesc",
          "utchmma_2cta__A_tmem",
          "utchmma_2cta_one__A_gdesc",
          "utchmma_2cta_one__A_tmem"
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
              InstructionOperand.Register(uRi),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UtchmmaudpPipeAddressAddressAddressAddressAddressRegPred(
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
  object UtchmmaudpPipeAddressAddressAddressAddressAddressRegPredImm extends Definition(
        "UtchmmaudpPipeAddressAddressAddressAddressAddressRegPredImm",
        "UTCHMMAudp_pipe",
        Vec(
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Address),
          Slot("srcC", OperandKind.Address),
          Slot("uRe", OperandKind.Address),
          Slot("uRh", OperandKind.Address),
          Slot("uRi", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate),
          Slot("scaleU4", OperandKind.Immediate)
        ),
        Vec(
          "utchmma_1cta__A_gdesc",
          "utchmma_1cta__A_tmem",
          "utchmma_1cta_one__A_gdesc",
          "utchmma_1cta_one__A_tmem",
          "utchmma_2cta__A_gdesc",
          "utchmma_2cta__A_tmem",
          "utchmma_2cta_one__A_gdesc",
          "utchmma_2cta_one__A_tmem"
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
              InstructionOperand.Register(uRi),
              InstructionOperand.Predicate(uPp),
              InstructionOperand.Immediate(scaleU4)
            ) => Some(Instruction.UtchmmaudpPipeAddressAddressAddressAddressAddressRegPredImm(
            srcA,
            srcB,
            srcC,
            uRe,
            uRh,
            uRi,
            uPp,
            scaleU4,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object UtcimmaAddressAddressAddressAddressAddressRegPred extends Definition(
        "UtcimmaAddressAddressAddressAddressAddressRegPred",
        "UTCIMMA",
        Vec(
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Address),
          Slot("srcC", OperandKind.Address),
          Slot("uRe", OperandKind.Address),
          Slot("uRh", OperandKind.Address),
          Slot("uRi", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec(
          "utcimma_1cta__A_gdesc",
          "utcimma_1cta__A_tmem",
          "utcimma_1cta_one__A_gdesc",
          "utcimma_1cta_one__A_tmem",
          "utcimma_2cta__A_gdesc",
          "utcimma_2cta__A_tmem",
          "utcimma_2cta_one__A_gdesc",
          "utcimma_2cta_one__A_tmem"
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
              InstructionOperand.Register(uRi),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UtcimmaAddressAddressAddressAddressAddressRegPred(
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
  object UtcimmaudpPipeAddressAddressAddressAddressAddressRegPred extends Definition(
        "UtcimmaudpPipeAddressAddressAddressAddressAddressRegPred",
        "UTCIMMAudp_pipe",
        Vec(
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Address),
          Slot("srcC", OperandKind.Address),
          Slot("uRe", OperandKind.Address),
          Slot("uRh", OperandKind.Address),
          Slot("uRi", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec(
          "utcimma_1cta__A_gdesc",
          "utcimma_1cta__A_tmem",
          "utcimma_1cta_one__A_gdesc",
          "utcimma_1cta_one__A_tmem",
          "utcimma_2cta__A_gdesc",
          "utcimma_2cta__A_tmem",
          "utcimma_2cta_one__A_gdesc",
          "utcimma_2cta_one__A_tmem"
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
              InstructionOperand.Register(uRi),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UtcimmaudpPipeAddressAddressAddressAddressAddressRegPred(
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
  object UtcommaAddressAddressAddressAddressAddressAddressPred extends Definition(
        "UtcommaAddressAddressAddressAddressAddressAddressPred",
        "UTCOMMA",
        Vec(
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Address),
          Slot("srcC", OperandKind.Address),
          Slot("uRe", OperandKind.Address),
          Slot("uRh", OperandKind.Address),
          Slot("uRi", OperandKind.Address),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec(
          "utcomma_1cta__A_gdesc",
          "utcomma_1cta__A_tmem",
          "utcomma_1cta_one__A_gdesc",
          "utcomma_1cta_one__A_tmem",
          "utcomma_2cta__A_gdesc",
          "utcomma_2cta__A_tmem",
          "utcomma_2cta_one__A_gdesc",
          "utcomma_2cta_one__A_tmem"
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
            ) => Some(Instruction.UtcommaAddressAddressAddressAddressAddressAddressPred(
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
  object UtcommaudpPipeAddressAddressAddressAddressAddressAddressPred extends Definition(
        "UtcommaudpPipeAddressAddressAddressAddressAddressAddressPred",
        "UTCOMMAudp_pipe",
        Vec(
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Address),
          Slot("srcC", OperandKind.Address),
          Slot("uRe", OperandKind.Address),
          Slot("uRh", OperandKind.Address),
          Slot("uRi", OperandKind.Address),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec(
          "utcomma_1cta__A_gdesc",
          "utcomma_1cta__A_tmem",
          "utcomma_1cta_one__A_gdesc",
          "utcomma_1cta_one__A_tmem",
          "utcomma_2cta__A_gdesc",
          "utcomma_2cta__A_tmem",
          "utcomma_2cta_one__A_gdesc",
          "utcomma_2cta_one__A_tmem"
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
            ) => Some(Instruction.UtcommaudpPipeAddressAddressAddressAddressAddressAddressPred(
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
  object UtcqmmaAddressAddressAddressAddressAddressAddressPred extends Definition(
        "UtcqmmaAddressAddressAddressAddressAddressAddressPred",
        "UTCQMMA",
        Vec(
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Address),
          Slot("srcC", OperandKind.Address),
          Slot("uRe", OperandKind.Address),
          Slot("uRh", OperandKind.Address),
          Slot("uRi", OperandKind.Address),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec(
          "utcqmma_1cta_scale__A_gdesc",
          "utcqmma_1cta_scale__A_tmem",
          "utcqmma_1cta_scale_one__A_gdesc",
          "utcqmma_1cta_scale_one__A_tmem",
          "utcqmma_2cta_scale__A_gdesc",
          "utcqmma_2cta_scale__A_tmem",
          "utcqmma_2cta_scale_one__A_gdesc",
          "utcqmma_2cta_scale_one__A_tmem"
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
            ) => Some(Instruction.UtcqmmaAddressAddressAddressAddressAddressAddressPred(
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
  object UtcqmmaAddressAddressAddressAddressAddressRegPred extends Definition(
        "UtcqmmaAddressAddressAddressAddressAddressRegPred",
        "UTCQMMA",
        Vec(
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Address),
          Slot("srcC", OperandKind.Address),
          Slot("uRe", OperandKind.Address),
          Slot("uRh", OperandKind.Address),
          Slot("uRi", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec(
          "utcqmma_1cta__A_gdesc",
          "utcqmma_1cta__A_tmem",
          "utcqmma_1cta_one__A_gdesc",
          "utcqmma_1cta_one__A_tmem",
          "utcqmma_2cta__A_gdesc",
          "utcqmma_2cta__A_tmem",
          "utcqmma_2cta_one__A_gdesc",
          "utcqmma_2cta_one__A_tmem"
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
              InstructionOperand.Register(uRi),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UtcqmmaAddressAddressAddressAddressAddressRegPred(
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
  object UtcqmmaudpPipeAddressAddressAddressAddressAddressAddressPred extends Definition(
        "UtcqmmaudpPipeAddressAddressAddressAddressAddressAddressPred",
        "UTCQMMAudp_pipe",
        Vec(
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Address),
          Slot("srcC", OperandKind.Address),
          Slot("uRe", OperandKind.Address),
          Slot("uRh", OperandKind.Address),
          Slot("uRi", OperandKind.Address),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec(
          "utcqmma_1cta_scale__A_gdesc",
          "utcqmma_1cta_scale__A_tmem",
          "utcqmma_1cta_scale_one__A_gdesc",
          "utcqmma_1cta_scale_one__A_tmem",
          "utcqmma_2cta_scale__A_gdesc",
          "utcqmma_2cta_scale__A_tmem",
          "utcqmma_2cta_scale_one__A_gdesc",
          "utcqmma_2cta_scale_one__A_tmem"
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
            ) => Some(Instruction.UtcqmmaudpPipeAddressAddressAddressAddressAddressAddressPred(
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
  object UtcqmmaudpPipeAddressAddressAddressAddressAddressRegPred extends Definition(
        "UtcqmmaudpPipeAddressAddressAddressAddressAddressRegPred",
        "UTCQMMAudp_pipe",
        Vec(
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Address),
          Slot("srcC", OperandKind.Address),
          Slot("uRe", OperandKind.Address),
          Slot("uRh", OperandKind.Address),
          Slot("uRi", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec(
          "utcqmma_1cta__A_gdesc",
          "utcqmma_1cta__A_tmem",
          "utcqmma_1cta_one__A_gdesc",
          "utcqmma_1cta_one__A_tmem",
          "utcqmma_2cta__A_gdesc",
          "utcqmma_2cta__A_tmem",
          "utcqmma_2cta_one__A_gdesc",
          "utcqmma_2cta_one__A_tmem"
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
              InstructionOperand.Register(uRi),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UtcqmmaudpPipeAddressAddressAddressAddressAddressRegPred(
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
  object UtcshiftAddress extends Definition(
        "UtcshiftAddress",
        "UTCSHIFT",
        Vec(Slot("srcA", OperandKind.Address)),
        Vec("utcshift__1CTA", "utcshift__2CTA", "utcshift_one__1CTA", "utcshift_one__2CTA")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA)) => Some(Instruction.UtcshiftAddress(srcA, modifiers, guard))
        case _ => None
      }
  }
  object UtcshiftudpPipeAddress extends Definition(
        "UtcshiftudpPipeAddress",
        "UTCSHIFTudp_pipe",
        Vec(Slot("srcA", OperandKind.Address)),
        Vec("utcshift__1CTA", "utcshift__2CTA", "utcshift_one__1CTA", "utcshift_one__2CTA")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA)) => Some(Instruction.UtcshiftudpPipeAddress(srcA, modifiers, guard))
        case _ => None
      }
  }
  object UtmacctlNoOperands
      extends Definition("UtmacctlNoOperands", "UTMACCTL", Vec(), Vec("utmacctl_", "utmacctl_one_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.UtmacctlNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object UtmacctlAddress extends Definition(
        "UtmacctlAddress",
        "UTMACCTL",
        Vec(Slot("srcA", OperandKind.Address)),
        Vec("utmacctl_URa_", "utmacctl_URa_one_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA)) => Some(Instruction.UtmacctlAddress(srcA, modifiers, guard))
        case _ => None
      }
  }
  object UtmacctludpPipeNoOperands
      extends Definition("UtmacctludpPipeNoOperands", "UTMACCTLudp_pipe", Vec(), Vec("utmacctl_", "utmacctl_one_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.UtmacctludpPipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object UtmacctludpPipeAddress extends Definition(
        "UtmacctludpPipeAddress",
        "UTMACCTLudp_pipe",
        Vec(Slot("srcA", OperandKind.Address)),
        Vec("utmacctl_URa_", "utmacctl_URa_one_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA)) => Some(Instruction.UtmacctludpPipeAddress(srcA, modifiers, guard))
        case _ => None
      }
  }
  object UtmacmdflushNoOperands
      extends Definition("UtmacmdflushNoOperands", "UTMACMDFLUSH", Vec(), Vec("utmacmdflush_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.UtmacmdflushNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object UtmacmdflushudpPipeNoOperands
      extends Definition("UtmacmdflushudpPipeNoOperands", "UTMACMDFLUSHudp_pipe", Vec(), Vec("utmacmdflush_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.UtmacmdflushudpPipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object UtmaldgAddressAddress extends Definition(
        "UtmaldgAddressAddress",
        "UTMALDG",
        Vec(Slot("srcB", OperandKind.Address), Slot("srcA", OperandKind.Address)),
        Vec("utmaldg__UUU", "utmaldg_one__UUU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcB), InstructionOperand.Address(srcA)) =>
          Some(Instruction.UtmaldgAddressAddress(srcB, srcA, modifiers, guard))
        case _ => None
      }
  }
  object UtmaldgAddressAddressAddress extends Definition(
        "UtmaldgAddressAddressAddress",
        "UTMALDG",
        Vec(Slot("srcB", OperandKind.Address), Slot("srcA", OperandKind.Address), Slot("uRe", OperandKind.Address)),
        Vec("utmaldg__UUU_desc", "utmaldg_one__UUU_desc")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcB), InstructionOperand.Address(srcA), InstructionOperand.Address(uRe)) =>
          Some(Instruction.UtmaldgAddressAddressAddress(srcB, srcA, uRe, modifiers, guard))
        case _ => None
      }
  }
  object UtmaldgAddressAddressReg extends Definition(
        "UtmaldgAddressAddressReg",
        "UTMALDG",
        Vec(Slot("srcB", OperandKind.Address), Slot("srcA", OperandKind.Address), Slot("srcC", OperandKind.Register)),
        Vec("utmaldg_URc__UUU", "utmaldg_URc_one__UUU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcB),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.UtmaldgAddressAddressReg(srcB, srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object UtmaldgAddressAddressRegAddress extends Definition(
        "UtmaldgAddressAddressRegAddress",
        "UTMALDG",
        Vec(
          Slot("srcB", OperandKind.Address),
          Slot("srcA", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Address)
        ),
        Vec("utmaldg_URc__UUU_desc", "utmaldg_URc_one__UUU_desc")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcB),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Address(uRe)
            ) => Some(Instruction.UtmaldgAddressAddressRegAddress(srcB, srcA, srcC, uRe, modifiers, guard))
        case _ => None
      }
  }
  object UtmaldgudpPipeAddressAddress extends Definition(
        "UtmaldgudpPipeAddressAddress",
        "UTMALDGudp_pipe",
        Vec(Slot("srcB", OperandKind.Address), Slot("srcA", OperandKind.Address)),
        Vec("utmaldg__UUU", "utmaldg_one__UUU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcB), InstructionOperand.Address(srcA)) =>
          Some(Instruction.UtmaldgudpPipeAddressAddress(srcB, srcA, modifiers, guard))
        case _ => None
      }
  }
  object UtmaldgudpPipeAddressAddressAddress extends Definition(
        "UtmaldgudpPipeAddressAddressAddress",
        "UTMALDGudp_pipe",
        Vec(Slot("srcB", OperandKind.Address), Slot("srcA", OperandKind.Address), Slot("uRe", OperandKind.Address)),
        Vec("utmaldg__UUU_desc", "utmaldg_one__UUU_desc")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcB), InstructionOperand.Address(srcA), InstructionOperand.Address(uRe)) =>
          Some(Instruction.UtmaldgudpPipeAddressAddressAddress(srcB, srcA, uRe, modifiers, guard))
        case _ => None
      }
  }
  object UtmaldgudpPipeAddressAddressReg extends Definition(
        "UtmaldgudpPipeAddressAddressReg",
        "UTMALDGudp_pipe",
        Vec(Slot("srcB", OperandKind.Address), Slot("srcA", OperandKind.Address), Slot("srcC", OperandKind.Register)),
        Vec("utmaldg_URc__UUU", "utmaldg_URc_one__UUU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcB),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.UtmaldgudpPipeAddressAddressReg(srcB, srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object UtmaldgudpPipeAddressAddressRegAddress extends Definition(
        "UtmaldgudpPipeAddressAddressRegAddress",
        "UTMALDGudp_pipe",
        Vec(
          Slot("srcB", OperandKind.Address),
          Slot("srcA", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Address)
        ),
        Vec("utmaldg_URc__UUU_desc", "utmaldg_URc_one__UUU_desc")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcB),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Address(uRe)
            ) => Some(Instruction.UtmaldgudpPipeAddressAddressRegAddress(srcB, srcA, srcC, uRe, modifiers, guard))
        case _ => None
      }
  }
  object UtmapfAddressAddress extends Definition(
        "UtmapfAddressAddress",
        "UTMAPF",
        Vec(Slot("srcB", OperandKind.Address), Slot("srcA", OperandKind.Address)),
        Vec("utmapf__UUU", "utmapf_one__UUU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcB), InstructionOperand.Address(srcA)) =>
          Some(Instruction.UtmapfAddressAddress(srcB, srcA, modifiers, guard))
        case _ => None
      }
  }
  object UtmapfAddressAddressAddress extends Definition(
        "UtmapfAddressAddressAddress",
        "UTMAPF",
        Vec(Slot("srcB", OperandKind.Address), Slot("srcA", OperandKind.Address), Slot("uRe", OperandKind.Address)),
        Vec("utmapf__UUU_desc", "utmapf_one__UUU_desc")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcB), InstructionOperand.Address(srcA), InstructionOperand.Address(uRe)) =>
          Some(Instruction.UtmapfAddressAddressAddress(srcB, srcA, uRe, modifiers, guard))
        case _ => None
      }
  }
  object UtmapfAddressAddressReg extends Definition(
        "UtmapfAddressAddressReg",
        "UTMAPF",
        Vec(Slot("srcB", OperandKind.Address), Slot("srcA", OperandKind.Address), Slot("srcC", OperandKind.Register)),
        Vec("utmapf_URc__UUU", "utmapf_URc_one__UUU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcB),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.UtmapfAddressAddressReg(srcB, srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object UtmapfAddressAddressRegAddress extends Definition(
        "UtmapfAddressAddressRegAddress",
        "UTMAPF",
        Vec(
          Slot("srcB", OperandKind.Address),
          Slot("srcA", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Address)
        ),
        Vec("utmapf_URc__UUU_desc", "utmapf_URc_one__UUU_desc")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcB),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Address(uRe)
            ) => Some(Instruction.UtmapfAddressAddressRegAddress(srcB, srcA, srcC, uRe, modifiers, guard))
        case _ => None
      }
  }
  object UtmapfudpPipeAddressAddress extends Definition(
        "UtmapfudpPipeAddressAddress",
        "UTMAPFudp_pipe",
        Vec(Slot("srcB", OperandKind.Address), Slot("srcA", OperandKind.Address)),
        Vec("utmapf__UUU", "utmapf_one__UUU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcB), InstructionOperand.Address(srcA)) =>
          Some(Instruction.UtmapfudpPipeAddressAddress(srcB, srcA, modifiers, guard))
        case _ => None
      }
  }
  object UtmapfudpPipeAddressAddressAddress extends Definition(
        "UtmapfudpPipeAddressAddressAddress",
        "UTMAPFudp_pipe",
        Vec(Slot("srcB", OperandKind.Address), Slot("srcA", OperandKind.Address), Slot("uRe", OperandKind.Address)),
        Vec("utmapf__UUU_desc", "utmapf_one__UUU_desc")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcB), InstructionOperand.Address(srcA), InstructionOperand.Address(uRe)) =>
          Some(Instruction.UtmapfudpPipeAddressAddressAddress(srcB, srcA, uRe, modifiers, guard))
        case _ => None
      }
  }
  object UtmapfudpPipeAddressAddressReg extends Definition(
        "UtmapfudpPipeAddressAddressReg",
        "UTMAPFudp_pipe",
        Vec(Slot("srcB", OperandKind.Address), Slot("srcA", OperandKind.Address), Slot("srcC", OperandKind.Register)),
        Vec("utmapf_URc__UUU", "utmapf_URc_one__UUU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcB),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.UtmapfudpPipeAddressAddressReg(srcB, srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object UtmapfudpPipeAddressAddressRegAddress extends Definition(
        "UtmapfudpPipeAddressAddressRegAddress",
        "UTMAPFudp_pipe",
        Vec(
          Slot("srcB", OperandKind.Address),
          Slot("srcA", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Address)
        ),
        Vec("utmapf_URc__UUU_desc", "utmapf_URc_one__UUU_desc")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcB),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Address(uRe)
            ) => Some(Instruction.UtmapfudpPipeAddressAddressRegAddress(srcB, srcA, srcC, uRe, modifiers, guard))
        case _ => None
      }
  }
  object UtmaredgAddressAddress extends Definition(
        "UtmaredgAddressAddress",
        "UTMAREDG",
        Vec(Slot("srcB", OperandKind.Address), Slot("srcA", OperandKind.Address)),
        Vec("utmaredg__UUU", "utmaredg_one__UUU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcB), InstructionOperand.Address(srcA)) =>
          Some(Instruction.UtmaredgAddressAddress(srcB, srcA, modifiers, guard))
        case _ => None
      }
  }
  object UtmaredgAddressAddressAddress extends Definition(
        "UtmaredgAddressAddressAddress",
        "UTMAREDG",
        Vec(Slot("srcB", OperandKind.Address), Slot("srcA", OperandKind.Address), Slot("uRe", OperandKind.Address)),
        Vec("utmaredg__UUU_desc", "utmaredg_one__UUU_desc")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcB), InstructionOperand.Address(srcA), InstructionOperand.Address(uRe)) =>
          Some(Instruction.UtmaredgAddressAddressAddress(srcB, srcA, uRe, modifiers, guard))
        case _ => None
      }
  }
  object UtmaredgudpPipeAddressAddress extends Definition(
        "UtmaredgudpPipeAddressAddress",
        "UTMAREDGudp_pipe",
        Vec(Slot("srcB", OperandKind.Address), Slot("srcA", OperandKind.Address)),
        Vec("utmaredg__UUU", "utmaredg_one__UUU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcB), InstructionOperand.Address(srcA)) =>
          Some(Instruction.UtmaredgudpPipeAddressAddress(srcB, srcA, modifiers, guard))
        case _ => None
      }
  }
  object UtmaredgudpPipeAddressAddressAddress extends Definition(
        "UtmaredgudpPipeAddressAddressAddress",
        "UTMAREDGudp_pipe",
        Vec(Slot("srcB", OperandKind.Address), Slot("srcA", OperandKind.Address), Slot("uRe", OperandKind.Address)),
        Vec("utmaredg__UUU_desc", "utmaredg_one__UUU_desc")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcB), InstructionOperand.Address(srcA), InstructionOperand.Address(uRe)) =>
          Some(Instruction.UtmaredgudpPipeAddressAddressAddress(srcB, srcA, uRe, modifiers, guard))
        case _ => None
      }
  }
  object UtmastgAddressAddress extends Definition(
        "UtmastgAddressAddress",
        "UTMASTG",
        Vec(Slot("srcB", OperandKind.Address), Slot("srcA", OperandKind.Address)),
        Vec("utmastg__UUU", "utmastg_one__UUU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcB), InstructionOperand.Address(srcA)) =>
          Some(Instruction.UtmastgAddressAddress(srcB, srcA, modifiers, guard))
        case _ => None
      }
  }
  object UtmastgAddressAddressAddress extends Definition(
        "UtmastgAddressAddressAddress",
        "UTMASTG",
        Vec(Slot("srcB", OperandKind.Address), Slot("srcA", OperandKind.Address), Slot("uRe", OperandKind.Address)),
        Vec("utmastg__UUU_desc", "utmastg_one__UUU_desc")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcB), InstructionOperand.Address(srcA), InstructionOperand.Address(uRe)) =>
          Some(Instruction.UtmastgAddressAddressAddress(srcB, srcA, uRe, modifiers, guard))
        case _ => None
      }
  }
  object UtmastgudpPipeAddressAddress extends Definition(
        "UtmastgudpPipeAddressAddress",
        "UTMASTGudp_pipe",
        Vec(Slot("srcB", OperandKind.Address), Slot("srcA", OperandKind.Address)),
        Vec("utmastg__UUU", "utmastg_one__UUU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcB), InstructionOperand.Address(srcA)) =>
          Some(Instruction.UtmastgudpPipeAddressAddress(srcB, srcA, modifiers, guard))
        case _ => None
      }
  }
  object UtmastgudpPipeAddressAddressAddress extends Definition(
        "UtmastgudpPipeAddressAddressAddress",
        "UTMASTGudp_pipe",
        Vec(Slot("srcB", OperandKind.Address), Slot("srcA", OperandKind.Address), Slot("uRe", OperandKind.Address)),
        Vec("utmastg__UUU_desc", "utmastg_one__UUU_desc")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcB), InstructionOperand.Address(srcA), InstructionOperand.Address(uRe)) =>
          Some(Instruction.UtmastgudpPipeAddressAddressAddress(srcB, srcA, uRe, modifiers, guard))
        case _ => None
      }
  }
  object UviaddRegRegImm extends Definition(
        "UviaddRegRegImm",
        "UVIADD",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("uviadd__URuIUR_URIR")
      ) {
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
  object UviaddRegRegReg extends Definition(
        "UviaddRegRegReg",
        "UVIADD",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("uviadd__URURUR_UUU")
      ) {
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
  object UviaddudpPipeRegRegImm extends Definition(
        "UviaddudpPipeRegRegImm",
        "UVIADDudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("uviadd__URuIUR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.UviaddudpPipeRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object UviaddudpPipeRegRegReg extends Definition(
        "UviaddudpPipeRegRegReg",
        "UVIADDudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("uviadd__URURUR_UUU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.UviaddudpPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UvimnmxRegRegImmPred extends Definition(
        "UvimnmxRegRegImmPred",
        "UVIMNMX",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("uvimnmx__URIR_URsIUR")
      ) {
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
  object UvimnmxRegRegRegPred extends Definition(
        "UvimnmxRegRegRegPred",
        "UVIMNMX",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("uvimnmx__UUU_URURUR")
      ) {
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
  object UvimnmxudpPipeRegRegImmPred extends Definition(
        "UvimnmxudpPipeRegRegImmPred",
        "UVIMNMXudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("uvimnmx__URIR_URsIUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UvimnmxudpPipeRegRegImmPred(dst, srcA, sb, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UvimnmxudpPipeRegRegRegPred extends Definition(
        "UvimnmxudpPipeRegRegRegPred",
        "UVIMNMXudp_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("uvimnmx__UUU_URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UvimnmxudpPipeRegRegRegPred(dst, srcA, srcB, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UvirtcountImm extends Definition(
        "UvirtcountImm",
        "UVIRTCOUNT",
        Vec(Slot("sa", OperandKind.Immediate)),
        Vec("uvirtcount__imm", "uvirtcount_one__imm")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sa)) => Some(Instruction.UvirtcountImm(sa, modifiers, guard))
        case _ => None
      }
  }
  object UvirtcountReg extends Definition(
        "UvirtcountReg",
        "UVIRTCOUNT",
        Vec(Slot("srcA", OperandKind.Register)),
        Vec("uvirtcount__UR", "uvirtcount_one__UR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA)) => Some(Instruction.UvirtcountReg(srcA, modifiers, guard))
        case _ => None
      }
  }
  object UvirtcountudpPipeImm extends Definition(
        "UvirtcountudpPipeImm",
        "UVIRTCOUNTudp_pipe",
        Vec(Slot("sa", OperandKind.Immediate)),
        Vec("uvirtcount__imm", "uvirtcount_one__imm")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sa)) => Some(Instruction.UvirtcountudpPipeImm(sa, modifiers, guard))
        case _ => None
      }
  }
  object UvirtcountudpPipeReg extends Definition(
        "UvirtcountudpPipeReg",
        "UVIRTCOUNTudp_pipe",
        Vec(Slot("srcA", OperandKind.Register)),
        Vec("uvirtcount__UR", "uvirtcount_one__UR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA)) => Some(Instruction.UvirtcountudpPipeReg(srcA, modifiers, guard))
        case _ => None
      }
  }
  object VabsdiffRegPredRegAddressImmReg extends Definition(
        "VabsdiffRegPredRegAddressImmReg",
        "VABSDIFF",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("vabsdiff__RCxR_RCxR")
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
            ) =>
          Some(Instruction.VabsdiffRegPredRegAddressImmReg(dst, pu, srcA, srcB, sb_offset, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VabsdiffRegPredRegConstReg extends Definition(
        "VabsdiffRegPredRegConstReg",
        "VABSDIFF",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("vabsdiff__RCR_RCR")
      ) {
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
  object VabsdiffRegPredRegImmReg extends Definition(
        "VabsdiffRegPredRegImmReg",
        "VABSDIFF",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("vabsdiff__RuIR_RIR")
      ) {
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
  object VabsdiffRegPredRegRegAddressImm extends Definition(
        "VabsdiffRegPredRegRegAddressImm",
        "VABSDIFF",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Address),
          Slot("sc_offset", OperandKind.Immediate)
        ),
        Vec("vabsdiff__RRCx_RRCx")
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
            ) =>
          Some(Instruction.VabsdiffRegPredRegRegAddressImm(dst, pu, srcA, srcB, srcC, sc_offset, modifiers, guard))
        case _ => None
      }
  }
  object VabsdiffRegPredRegRegConst extends Definition(
        "VabsdiffRegPredRegRegConst",
        "VABSDIFF",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.ConstantBank)
        ),
        Vec("vabsdiff__RRC_RRC")
      ) {
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
  object VabsdiffRegPredRegRegImm extends Definition(
        "VabsdiffRegPredRegRegImm",
        "VABSDIFF",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate)
        ),
        Vec("vabsdiff__RRuI_RRI")
      ) {
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
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
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
  object VabsdiffRegRegAddressImmReg extends Definition(
        "VabsdiffRegRegAddressImmReg",
        "VABSDIFF",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("vabsdiff__RCxR_RCxR")
      ) {
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
  object VabsdiffRegRegConstReg extends Definition(
        "VabsdiffRegRegConstReg",
        "VABSDIFF",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("vabsdiff__RCR_RCR")
      ) {
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
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
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
  object VabsdiffRegRegRegAddressImm extends Definition(
        "VabsdiffRegRegRegAddressImm",
        "VABSDIFF",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Address),
          Slot("sc_offset", OperandKind.Immediate)
        ),
        Vec("vabsdiff__RRCx_RRCx")
      ) {
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
  object VabsdiffRegRegRegConst extends Definition(
        "VabsdiffRegRegRegConst",
        "VABSDIFF",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.ConstantBank)
        ),
        Vec("vabsdiff__RRC_RRC")
      ) {
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
  object VabsdiffRegRegRegImm extends Definition(
        "VabsdiffRegRegRegImm",
        "VABSDIFF",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate)
        ),
        Vec("vabsdiff__RRuI_RRI")
      ) {
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
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
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
  object Vabsdiff2RegRegImmReg extends Definition(
        "Vabsdiff2RegRegImmReg",
        "VABSDIFF2",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm VABSDIFF2")
      ) {
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
  object Vabsdiff2RegRegRegReg extends Definition(
        "Vabsdiff2RegRegRegReg",
        "VABSDIFF2",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("VABSDIFF2")
      ) {
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
  object Vabsdiff2xluPipeRegRegImmReg extends Definition(
        "Vabsdiff2xluPipeRegRegImmReg",
        "VABSDIFF2xlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm VABSDIFF2")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Vabsdiff2xluPipeRegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Vabsdiff2xluPipeRegRegRegReg extends Definition(
        "Vabsdiff2xluPipeRegRegRegReg",
        "VABSDIFF2xlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("VABSDIFF2")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Vabsdiff2xluPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Vabsdiff4RegPredRegAddressImmReg extends Definition(
        "Vabsdiff4RegPredRegAddressImmReg",
        "VABSDIFF4",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("vabsdiff4__RCxR_RCxR")
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
            ) =>
          Some(Instruction.Vabsdiff4RegPredRegAddressImmReg(dst, pu, srcA, srcB, sb_offset, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Vabsdiff4RegPredRegConstReg extends Definition(
        "Vabsdiff4RegPredRegConstReg",
        "VABSDIFF4",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("vabsdiff4__RCR_RCR")
      ) {
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
  object Vabsdiff4RegPredRegImmReg extends Definition(
        "Vabsdiff4RegPredRegImmReg",
        "VABSDIFF4",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("vabsdiff4__RuIR_RIR")
      ) {
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
  object Vabsdiff4RegPredRegRegAddressImm extends Definition(
        "Vabsdiff4RegPredRegRegAddressImm",
        "VABSDIFF4",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Address),
          Slot("sc_offset", OperandKind.Immediate)
        ),
        Vec("vabsdiff4__RRCx_RRCx")
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
            ) =>
          Some(Instruction.Vabsdiff4RegPredRegRegAddressImm(dst, pu, srcA, srcB, srcC, sc_offset, modifiers, guard))
        case _ => None
      }
  }
  object Vabsdiff4RegPredRegRegConst extends Definition(
        "Vabsdiff4RegPredRegRegConst",
        "VABSDIFF4",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.ConstantBank)
        ),
        Vec("vabsdiff4__RRC_RRC")
      ) {
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
  object Vabsdiff4RegPredRegRegImm extends Definition(
        "Vabsdiff4RegPredRegRegImm",
        "VABSDIFF4",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate)
        ),
        Vec("vabsdiff4__RRuI_RRI")
      ) {
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
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
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
  object Vabsdiff4RegRegAddressImmReg extends Definition(
        "Vabsdiff4RegRegAddressImmReg",
        "VABSDIFF4",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("vabsdiff4__RCxR_RCxR")
      ) {
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
  object Vabsdiff4RegRegConstReg extends Definition(
        "Vabsdiff4RegRegConstReg",
        "VABSDIFF4",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("vabsdiff4__RCR_RCR")
      ) {
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
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
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
  object Vabsdiff4RegRegRegAddressImm extends Definition(
        "Vabsdiff4RegRegRegAddressImm",
        "VABSDIFF4",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Address),
          Slot("sc_offset", OperandKind.Immediate)
        ),
        Vec("vabsdiff4__RRCx_RRCx")
      ) {
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
  object Vabsdiff4RegRegRegConst extends Definition(
        "Vabsdiff4RegRegRegConst",
        "VABSDIFF4",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.ConstantBank)
        ),
        Vec("vabsdiff4__RRC_RRC")
      ) {
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
  object Vabsdiff4RegRegRegImm extends Definition(
        "Vabsdiff4RegRegRegImm",
        "VABSDIFF4",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate)
        ),
        Vec("vabsdiff4__RRuI_RRI")
      ) {
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
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
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
  object Vabsdiff4fauPipeRegRegImmReg extends Definition(
        "Vabsdiff4fauPipeRegRegImmReg",
        "VABSDIFF4fau_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm VABSDIFF4")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Vabsdiff4fauPipeRegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Vabsdiff4fauPipeRegRegRegReg extends Definition(
        "Vabsdiff4fauPipeRegRegRegReg",
        "VABSDIFF4fau_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("VABSDIFF4")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Vabsdiff4fauPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Vabsdiff4fmaiPipeRegRegImmReg extends Definition(
        "Vabsdiff4fmaiPipeRegRegImmReg",
        "VABSDIFF4fmai_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm_VABSDIFF4", "SImm_VABSDIFF4")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Vabsdiff4fmaiPipeRegRegImmReg(dst, srcA, sImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Vabsdiff4fmaiPipeRegRegRegReg extends Definition(
        "Vabsdiff4fmaiPipeRegRegRegReg",
        "VABSDIFF4fmai_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("VABSDIFF4")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Vabsdiff4fmaiPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Vabsdiff4fxuPipeRegRegImmReg extends Definition(
        "Vabsdiff4fxuPipeRegRegImmReg",
        "VABSDIFF4fxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm_VABSDIFF4", "SImm_VABSDIFF4")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Vabsdiff4fxuPipeRegRegImmReg(dst, srcA, sImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Vabsdiff4fxuPipeRegRegRegReg extends Definition(
        "Vabsdiff4fxuPipeRegRegRegReg",
        "VABSDIFF4fxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("VABSDIFF4")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Vabsdiff4fxuPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Vabsdiff4intPipeRegPredRegAddressImmReg extends Definition(
        "Vabsdiff4intPipeRegPredRegAddressImmReg",
        "VABSDIFF4int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("vabsdiff4__RCxR_RCxR")
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
            ) => Some(Instruction.Vabsdiff4intPipeRegPredRegAddressImmReg(
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
  object Vabsdiff4intPipeRegPredRegConstReg extends Definition(
        "Vabsdiff4intPipeRegPredRegConstReg",
        "VABSDIFF4int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("vabsdiff4__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Vabsdiff4intPipeRegPredRegConstReg(dst, pu, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Vabsdiff4intPipeRegPredRegImmReg extends Definition(
        "Vabsdiff4intPipeRegPredRegImmReg",
        "VABSDIFF4int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("vabsdiff4__RuIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Vabsdiff4intPipeRegPredRegImmReg(dst, pu, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Vabsdiff4intPipeRegPredRegRegAddressImm extends Definition(
        "Vabsdiff4intPipeRegPredRegRegAddressImm",
        "VABSDIFF4int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Address),
          Slot("sc_offset", OperandKind.Immediate)
        ),
        Vec("vabsdiff4__RRCx_RRCx")
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
            ) => Some(Instruction.Vabsdiff4intPipeRegPredRegRegAddressImm(
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
  object Vabsdiff4intPipeRegPredRegRegConst extends Definition(
        "Vabsdiff4intPipeRegPredRegRegConst",
        "VABSDIFF4int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.ConstantBank)
        ),
        Vec("vabsdiff4__RRC_RRC")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(sc)
            ) => Some(Instruction.Vabsdiff4intPipeRegPredRegRegConst(dst, pu, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object Vabsdiff4intPipeRegPredRegRegImm extends Definition(
        "Vabsdiff4intPipeRegPredRegRegImm",
        "VABSDIFF4int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate)
        ),
        Vec("vabsdiff4__RRuI_RRI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.Vabsdiff4intPipeRegPredRegRegImm(dst, pu, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object Vabsdiff4intPipeRegPredRegRegReg extends Definition(
        "Vabsdiff4intPipeRegPredRegRegReg",
        "VABSDIFF4int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
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
            ) => Some(Instruction.Vabsdiff4intPipeRegPredRegRegReg(dst, pu, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Vabsdiff4intPipeRegRegAddressImmReg extends Definition(
        "Vabsdiff4intPipeRegRegAddressImmReg",
        "VABSDIFF4int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("vabsdiff4__RCxR_RCxR")
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
          Some(Instruction.Vabsdiff4intPipeRegRegAddressImmReg(dst, srcA, srcB, sb_offset, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Vabsdiff4intPipeRegRegConstReg extends Definition(
        "Vabsdiff4intPipeRegRegConstReg",
        "VABSDIFF4int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("vabsdiff4__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Vabsdiff4intPipeRegRegConstReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Vabsdiff4intPipeRegRegImmReg extends Definition(
        "Vabsdiff4intPipeRegRegImmReg",
        "VABSDIFF4int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("vabsdiff4__RuIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Vabsdiff4intPipeRegRegImmReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Vabsdiff4intPipeRegRegRegAddressImm extends Definition(
        "Vabsdiff4intPipeRegRegRegAddressImm",
        "VABSDIFF4int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Address),
          Slot("sc_offset", OperandKind.Immediate)
        ),
        Vec("vabsdiff4__RRCx_RRCx")
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
          Some(Instruction.Vabsdiff4intPipeRegRegRegAddressImm(dst, srcA, srcB, srcC, sc_offset, modifiers, guard))
        case _ => None
      }
  }
  object Vabsdiff4intPipeRegRegRegConst extends Definition(
        "Vabsdiff4intPipeRegRegRegConst",
        "VABSDIFF4int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.ConstantBank)
        ),
        Vec("vabsdiff4__RRC_RRC")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(sc)
            ) => Some(Instruction.Vabsdiff4intPipeRegRegRegConst(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object Vabsdiff4intPipeRegRegRegImm extends Definition(
        "Vabsdiff4intPipeRegRegRegImm",
        "VABSDIFF4int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate)
        ),
        Vec("vabsdiff4__RRuI_RRI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.Vabsdiff4intPipeRegRegRegImm(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object Vabsdiff4intPipeRegRegRegReg extends Definition(
        "Vabsdiff4intPipeRegRegRegReg",
        "VABSDIFF4int_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("vabsdiff4__RRR_RRR", "vabsdiff4__RRU_RRU", "vabsdiff4__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Vabsdiff4intPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Vabsdiff4xluPipeRegRegImmReg extends Definition(
        "Vabsdiff4xluPipeRegRegImmReg",
        "VABSDIFF4xlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm VABSDIFF4")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Vabsdiff4xluPipeRegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Vabsdiff4xluPipeRegRegRegReg extends Definition(
        "Vabsdiff4xluPipeRegRegRegReg",
        "VABSDIFF4xlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("VABSDIFF4")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Vabsdiff4xluPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VabsdifffxuPipeRegRegImmReg extends Definition(
        "VabsdifffxuPipeRegRegImmReg",
        "VABSDIFFfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec(
          "Imm_VABSDIFF",
          "SImm_VABSDIFF",
          "a32_Imm_VABSDIFF",
          "a32_SImm_VABSDIFF",
          "a8_Imm_VABSDIFF",
          "a8_SImm_VABSDIFF"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VabsdifffxuPipeRegRegImmReg(dst, srcA, sImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VabsdifffxuPipeRegRegRegReg extends Definition(
        "VabsdifffxuPipeRegRegRegReg",
        "VABSDIFFfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec(
          "VABSDIFF",
          "a32_VABSDIFF",
          "a32_b32_VABSDIFF",
          "a32_b8_VABSDIFF",
          "a8_VABSDIFF",
          "a8_b32_VABSDIFF",
          "a8_b8_VABSDIFF",
          "b32_VABSDIFF",
          "b8_VABSDIFF"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VabsdifffxuPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VabsdiffintPipeRegPredRegAddressImmReg extends Definition(
        "VabsdiffintPipeRegPredRegAddressImmReg",
        "VABSDIFFint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("vabsdiff__RCxR_RCxR")
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
            ) => Some(Instruction.VabsdiffintPipeRegPredRegAddressImmReg(
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
  object VabsdiffintPipeRegPredRegConstReg extends Definition(
        "VabsdiffintPipeRegPredRegConstReg",
        "VABSDIFFint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("vabsdiff__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VabsdiffintPipeRegPredRegConstReg(dst, pu, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VabsdiffintPipeRegPredRegImmReg extends Definition(
        "VabsdiffintPipeRegPredRegImmReg",
        "VABSDIFFint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("vabsdiff__RuIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VabsdiffintPipeRegPredRegImmReg(dst, pu, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VabsdiffintPipeRegPredRegRegAddressImm extends Definition(
        "VabsdiffintPipeRegPredRegRegAddressImm",
        "VABSDIFFint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Address),
          Slot("sc_offset", OperandKind.Immediate)
        ),
        Vec("vabsdiff__RRCx_RRCx")
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
            ) => Some(Instruction.VabsdiffintPipeRegPredRegRegAddressImm(
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
  object VabsdiffintPipeRegPredRegRegConst extends Definition(
        "VabsdiffintPipeRegPredRegRegConst",
        "VABSDIFFint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.ConstantBank)
        ),
        Vec("vabsdiff__RRC_RRC")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(sc)
            ) => Some(Instruction.VabsdiffintPipeRegPredRegRegConst(dst, pu, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object VabsdiffintPipeRegPredRegRegImm extends Definition(
        "VabsdiffintPipeRegPredRegRegImm",
        "VABSDIFFint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate)
        ),
        Vec("vabsdiff__RRuI_RRI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.VabsdiffintPipeRegPredRegRegImm(dst, pu, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object VabsdiffintPipeRegPredRegRegReg extends Definition(
        "VabsdiffintPipeRegPredRegRegReg",
        "VABSDIFFint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
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
            ) => Some(Instruction.VabsdiffintPipeRegPredRegRegReg(dst, pu, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VabsdiffintPipeRegRegAddressImmReg extends Definition(
        "VabsdiffintPipeRegRegAddressImmReg",
        "VABSDIFFint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("vabsdiff__RCxR_RCxR")
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
          Some(Instruction.VabsdiffintPipeRegRegAddressImmReg(dst, srcA, srcB, sb_offset, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VabsdiffintPipeRegRegConstReg extends Definition(
        "VabsdiffintPipeRegRegConstReg",
        "VABSDIFFint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("vabsdiff__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VabsdiffintPipeRegRegConstReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VabsdiffintPipeRegRegImmReg extends Definition(
        "VabsdiffintPipeRegRegImmReg",
        "VABSDIFFint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("vabsdiff__RuIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VabsdiffintPipeRegRegImmReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VabsdiffintPipeRegRegRegAddressImm extends Definition(
        "VabsdiffintPipeRegRegRegAddressImm",
        "VABSDIFFint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Address),
          Slot("sc_offset", OperandKind.Immediate)
        ),
        Vec("vabsdiff__RRCx_RRCx")
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
          Some(Instruction.VabsdiffintPipeRegRegRegAddressImm(dst, srcA, srcB, srcC, sc_offset, modifiers, guard))
        case _ => None
      }
  }
  object VabsdiffintPipeRegRegRegConst extends Definition(
        "VabsdiffintPipeRegRegRegConst",
        "VABSDIFFint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.ConstantBank)
        ),
        Vec("vabsdiff__RRC_RRC")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(sc)
            ) => Some(Instruction.VabsdiffintPipeRegRegRegConst(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object VabsdiffintPipeRegRegRegImm extends Definition(
        "VabsdiffintPipeRegRegRegImm",
        "VABSDIFFint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate)
        ),
        Vec("vabsdiff__RRuI_RRI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.VabsdiffintPipeRegRegRegImm(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object VabsdiffintPipeRegRegRegReg extends Definition(
        "VabsdiffintPipeRegRegRegReg",
        "VABSDIFFint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("vabsdiff__RRR_RRR", "vabsdiff__RRU_RRU", "vabsdiff__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VabsdiffintPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VabsdiffxluPipeRegRegImmReg extends Definition(
        "VabsdiffxluPipeRegRegImmReg",
        "VABSDIFFxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm VABSDIFF", "a32 Imm VABSDIFF", "a8 Imm VABSDIFF")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VabsdiffxluPipeRegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VabsdiffxluPipeRegRegRegReg extends Definition(
        "VabsdiffxluPipeRegRegRegReg",
        "VABSDIFFxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec(
          "VABSDIFF",
          "a32 VABSDIFF",
          "a32 b32 VABSDIFF",
          "a32 b8 VABSDIFF",
          "a8 VABSDIFF",
          "a8 b32 VABSDIFF",
          "a8 b8 VABSDIFF",
          "b32 VABSDIFF",
          "b8 VABSDIFF"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VabsdiffxluPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VaddRegRegReg extends Definition(
        "VaddRegRegReg",
        "VADD",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcC", OperandKind.Register)),
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
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
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
  object Vadd2RegRegReg extends Definition(
        "Vadd2RegRegReg",
        "VADD2",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcC", OperandKind.Register)),
        Vec("Imm VADD2")
      ) {
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
  object Vadd2RegRegRegReg extends Definition(
        "Vadd2RegRegRegReg",
        "VADD2",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("VADD2")
      ) {
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
  object Vadd2xluPipeRegRegReg extends Definition(
        "Vadd2xluPipeRegRegReg",
        "VADD2xlu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcC", OperandKind.Register)),
        Vec("Imm VADD2")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Vadd2xluPipeRegRegReg(dst, srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Vadd2xluPipeRegRegRegReg extends Definition(
        "Vadd2xluPipeRegRegRegReg",
        "VADD2xlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("VADD2")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Vadd2xluPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Vadd4RegRegReg extends Definition(
        "Vadd4RegRegReg",
        "VADD4",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcC", OperandKind.Register)),
        Vec("Imm VADD4")
      ) {
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
  object Vadd4RegRegRegReg extends Definition(
        "Vadd4RegRegRegReg",
        "VADD4",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("VADD4")
      ) {
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
  object Vadd4xluPipeRegRegReg extends Definition(
        "Vadd4xluPipeRegRegReg",
        "VADD4xlu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcC", OperandKind.Register)),
        Vec("Imm VADD4")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Vadd4xluPipeRegRegReg(dst, srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Vadd4xluPipeRegRegRegReg extends Definition(
        "Vadd4xluPipeRegRegRegReg",
        "VADD4xlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("VADD4")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Vadd4xluPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VaddfxuPipeRegRegReg extends Definition(
        "VaddfxuPipeRegRegReg",
        "VADDfxu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcC", OperandKind.Register)),
        Vec("Imm_VADD", "SImm_VADD", "a32_Imm_VADD", "a32_SImm_VADD", "a8_Imm_VADD", "a8_SImm_VADD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VaddfxuPipeRegRegReg(dst, srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VaddfxuPipeRegRegRegReg extends Definition(
        "VaddfxuPipeRegRegRegReg",
        "VADDfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec(
          "VADD",
          "a32_VADD",
          "a32_b32_VADD",
          "a32_b8_VADD",
          "a8_VADD",
          "a8_b32_VADD",
          "a8_b8_VADD",
          "b32_VADD",
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
            ) => Some(Instruction.VaddfxuPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VaddxluPipeRegRegReg extends Definition(
        "VaddxluPipeRegRegReg",
        "VADDxlu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcC", OperandKind.Register)),
        Vec("Imm VADD", "a32 Imm VADD", "a8 Imm VADD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VaddxluPipeRegRegReg(dst, srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VaddxluPipeRegRegRegReg extends Definition(
        "VaddxluPipeRegRegRegReg",
        "VADDxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec(
          "VADD",
          "a32 VADD",
          "a32 b32 VADD",
          "a32 b8 VADD",
          "a8 VADD",
          "a8 b32 VADD",
          "a8 b8 VADD",
          "b32 VADD",
          "b8 VADD"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.VaddxluPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object VhmnmxRegRegAddressImmRegPred extends Definition(
        "VhmnmxRegRegAddressImmRegPred",
        "VHMNMX",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("vhmnmx__RCxR")
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
            ) => Some(Instruction.VhmnmxRegRegAddressImmRegPred(dst, srcA, srcB, sb_offset, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object VhmnmxRegRegConstRegPred extends Definition(
        "VhmnmxRegRegConstRegPred",
        "VHMNMX",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("vhmnmx__RCR")
      ) {
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
  object VhmnmxRegRegImmImmRegPred extends Definition(
        "VhmnmxRegRegImmImmRegPred",
        "VHMNMX",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("sb1", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("vhmnmx__RIR")
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
            ) => Some(Instruction.VhmnmxRegRegImmImmRegPred(dst, srcA, sb, sb1, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object VhmnmxRegRegRegRegPred extends Definition(
        "VhmnmxRegRegRegRegPred",
        "VHMNMX",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("vhmnmx__RRR", "vhmnmx__RUR")
      ) {
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
  object Vhmnmxfp16PipeRegRegAddressImmRegPred extends Definition(
        "Vhmnmxfp16PipeRegRegAddressImmRegPred",
        "VHMNMXfp16_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("vhmnmx__RCxR")
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
            ) => Some(Instruction.Vhmnmxfp16PipeRegRegAddressImmRegPred(
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
  object Vhmnmxfp16PipeRegRegConstRegPred extends Definition(
        "Vhmnmxfp16PipeRegRegConstRegPred",
        "VHMNMXfp16_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("vhmnmx__RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Vhmnmxfp16PipeRegRegConstRegPred(dst, srcA, sb, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object Vhmnmxfp16PipeRegRegImmImmRegPred extends Definition(
        "Vhmnmxfp16PipeRegRegImmImmRegPred",
        "VHMNMXfp16_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("sb1", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("vhmnmx__RIR")
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
            ) => Some(Instruction.Vhmnmxfp16PipeRegRegImmImmRegPred(dst, srcA, sb, sb1, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object Vhmnmxfp16PipeRegRegRegRegPred extends Definition(
        "Vhmnmxfp16PipeRegRegRegRegPred",
        "VHMNMXfp16_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("vhmnmx__RRR", "vhmnmx__RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Vhmnmxfp16PipeRegRegRegRegPred(dst, srcA, srcB, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object ViaddRegRegAddressImm extends Definition(
        "ViaddRegRegAddressImm",
        "VIADD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("viadd__RCxR_RCxR")
      ) {
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
  object ViaddRegRegConst extends Definition(
        "ViaddRegRegConst",
        "VIADD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank)
        ),
        Vec("viadd__RCR_RCR")
      ) {
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
  object ViaddRegRegImm extends Definition(
        "ViaddRegRegImm",
        "VIADD",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("viadd__RuIR_RIR")
      ) {
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
  object ViaddRegRegReg extends Definition(
        "ViaddRegRegReg",
        "VIADD",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("viadd__RRR_RRR", "viadd__RUR_RUR")
      ) {
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
  object ViaddmnmxRegRegAddressImmRegPred extends Definition(
        "ViaddmnmxRegRegAddressImmRegPred",
        "VIADDMNMX",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("viaddmnmx__RCxR_RCxR")
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
          Some(Instruction.ViaddmnmxRegRegAddressImmRegPred(dst, srcA, srcB, sb_offset, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object ViaddmnmxRegRegConstRegPred extends Definition(
        "ViaddmnmxRegRegConstRegPred",
        "VIADDMNMX",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("viaddmnmx__RCR_RCR")
      ) {
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
  object ViaddmnmxRegRegImmRegPred extends Definition(
        "ViaddmnmxRegRegImmRegPred",
        "VIADDMNMX",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("viaddmnmx__RuIR_RIR")
      ) {
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
  object ViaddmnmxRegRegRegAddressImmPred extends Definition(
        "ViaddmnmxRegRegRegAddressImmPred",
        "VIADDMNMX",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Address),
          Slot("sc_offset", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("viaddmnmx__RRCx_RRCx")
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
          Some(Instruction.ViaddmnmxRegRegRegAddressImmPred(dst, srcA, srcB, srcC, sc_offset, pp, modifiers, guard))
        case _ => None
      }
  }
  object ViaddmnmxRegRegRegConstPred extends Definition(
        "ViaddmnmxRegRegRegConstPred",
        "VIADDMNMX",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.ConstantBank),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("viaddmnmx__RRC_RRC")
      ) {
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
  object ViaddmnmxRegRegRegImmPred extends Definition(
        "ViaddmnmxRegRegRegImmPred",
        "VIADDMNMX",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("viaddmnmx__RRuI_RRI")
      ) {
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
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
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
  object ViaddmnmxintPipeRegRegAddressImmRegPred extends Definition(
        "ViaddmnmxintPipeRegRegAddressImmRegPred",
        "VIADDMNMXint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("viaddmnmx__RCxR_RCxR")
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
            ) => Some(Instruction.ViaddmnmxintPipeRegRegAddressImmRegPred(
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
  object ViaddmnmxintPipeRegRegConstRegPred extends Definition(
        "ViaddmnmxintPipeRegRegConstRegPred",
        "VIADDMNMXint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("viaddmnmx__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.ViaddmnmxintPipeRegRegConstRegPred(dst, srcA, sb, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object ViaddmnmxintPipeRegRegImmRegPred extends Definition(
        "ViaddmnmxintPipeRegRegImmRegPred",
        "VIADDMNMXint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("viaddmnmx__RuIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.ViaddmnmxintPipeRegRegImmRegPred(dst, srcA, sb, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object ViaddmnmxintPipeRegRegRegAddressImmPred extends Definition(
        "ViaddmnmxintPipeRegRegRegAddressImmPred",
        "VIADDMNMXint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Address),
          Slot("sc_offset", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("viaddmnmx__RRCx_RRCx")
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
            ) => Some(Instruction.ViaddmnmxintPipeRegRegRegAddressImmPred(
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
  object ViaddmnmxintPipeRegRegRegConstPred extends Definition(
        "ViaddmnmxintPipeRegRegRegConstPred",
        "VIADDMNMXint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.ConstantBank),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("viaddmnmx__RRC_RRC")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(sc),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.ViaddmnmxintPipeRegRegRegConstPred(dst, srcA, srcB, sc, pp, modifiers, guard))
        case _ => None
      }
  }
  object ViaddmnmxintPipeRegRegRegImmPred extends Definition(
        "ViaddmnmxintPipeRegRegRegImmPred",
        "VIADDMNMXint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("viaddmnmx__RRuI_RRI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.ViaddmnmxintPipeRegRegRegImmPred(dst, srcA, srcB, sc, pp, modifiers, guard))
        case _ => None
      }
  }
  object ViaddmnmxintPipeRegRegRegRegPred extends Definition(
        "ViaddmnmxintPipeRegRegRegRegPred",
        "VIADDMNMXint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
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
            ) => Some(Instruction.ViaddmnmxintPipeRegRegRegRegPred(dst, srcA, srcB, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object ViaddfmalighterPipeRegRegAddressImm extends Definition(
        "ViaddfmalighterPipeRegRegAddressImm",
        "VIADDfmalighter_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("viadd__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.ViaddfmalighterPipeRegRegAddressImm(dst, srcA, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object ViaddfmalighterPipeRegRegConst extends Definition(
        "ViaddfmalighterPipeRegRegConst",
        "VIADDfmalighter_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank)
        ),
        Vec("viadd__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb)
            ) => Some(Instruction.ViaddfmalighterPipeRegRegConst(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object ViaddfmalighterPipeRegRegImm extends Definition(
        "ViaddfmalighterPipeRegRegImm",
        "VIADDfmalighter_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("viadd__RuIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.ViaddfmalighterPipeRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object ViaddfmalighterPipeRegRegReg extends Definition(
        "ViaddfmalighterPipeRegRegReg",
        "VIADDfmalighter_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("viadd__RRR_RRR", "viadd__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.ViaddfmalighterPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object VildRegAddress extends Definition(
        "VildRegAddress",
        "VILD",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address)),
        Vec("I VILD", "VILD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Address(srcA)) =>
          Some(Instruction.VildRegAddress(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object VildaguPipeRegAddress extends Definition(
        "VildaguPipeRegAddress",
        "VILDagu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address)),
        Vec("I VILD", "VILD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Address(srcA)) =>
          Some(Instruction.VildaguPipeRegAddress(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object VimnmxRegPredPredRegAddressImmPred extends Definition(
        "VimnmxRegPredPredRegAddressImmPred",
        "VIMNMX",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("vimnmx_pred__RCxR_RCxR")
      ) {
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
  object VimnmxRegPredPredRegConstPred extends Definition(
        "VimnmxRegPredPredRegConstPred",
        "VIMNMX",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("vimnmx_pred__RCR_RCR")
      ) {
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
  object VimnmxRegPredPredRegImmPred extends Definition(
        "VimnmxRegPredPredRegImmPred",
        "VIMNMX",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("vimnmx__RIR_RsIR", "vimnmx_pred__RIR_RsIR")
      ) {
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
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
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
  object VimnmxRegRegAddressImmPred extends Definition(
        "VimnmxRegRegAddressImmPred",
        "VIMNMX",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("vimnmx__RCxR_RCxR")
      ) {
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
  object VimnmxRegRegConstPred extends Definition(
        "VimnmxRegRegConstPred",
        "VIMNMX",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("vimnmx__RCR_RCR")
      ) {
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
  object VimnmxRegRegImmPred extends Definition(
        "VimnmxRegRegImmPred",
        "VIMNMX",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("vimnmx__RIR_RsIR", "vimnmx_nopred__RIR_RsIR")
      ) {
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
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
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
  object Vimnmx3RegRegAddressImmRegPred extends Definition(
        "Vimnmx3RegRegAddressImmRegPred",
        "VIMNMX3",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("vimnmx3__RCxR_RCxR")
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
          Some(Instruction.Vimnmx3RegRegAddressImmRegPred(dst, srcA, srcB, sb_offset, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object Vimnmx3RegRegConstRegPred extends Definition(
        "Vimnmx3RegRegConstRegPred",
        "VIMNMX3",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("vimnmx3__RCR_RCR")
      ) {
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
  val definitions: Vec[Definition] = Vec(
    UpopcRegImm,
    UpopcRegReg,
    UpopcudpPipeRegImm,
    UpopcudpPipeRegReg,
    UprmtRegRegImmReg,
    UprmtRegRegRegReg,
    UprmtudpPipeRegRegImmReg,
    UprmtudpPipeRegRegRegReg,
    UpsetpPredPredPred,
    UpsetpPredPredPredPredPred,
    UpsetpudpPipePredPredPred,
    UpsetpudpPipePredPredPredPredPred,
    Ur2upReg,
    Ur2upRegImm,
    Ur2upRegReg,
    Ur2upudpPipeReg,
    Ur2upudpPipeRegImm,
    Ur2upudpPipeRegReg,
    UredgrAddressReg,
    UredgrudpPipeAddressReg,
    UselRegRegImmPred,
    UselRegRegRegPred,
    UseludpPipeRegRegImmPred,
    UseludpPipeRegRegRegPred,
    UsetmaxregImm,
    UsetmaxregPredImm,
    UsetmaxregPredReg,
    UsetmaxregReg,
    UsetmaxregudpPipeImm,
    UsetmaxregudpPipePredImm,
    UsetmaxregudpPipePredReg,
    UsetmaxregudpPipeReg,
    UsetshmszNoOperands,
    UsetshmszImm,
    UsetshmszReg,
    UsetshmszudpPipeNoOperands,
    UsetshmszudpPipeImm,
    UsetshmszudpPipeReg,
    UsgxtRegRegImm,
    UsgxtRegRegReg,
    UsgxtudpPipeRegRegImm,
    UsgxtudpPipeRegRegReg,
    UshfRegRegImmReg,
    UshfRegRegRegImm,
    UshfRegRegRegReg,
    UshfudpPipeRegRegImmReg,
    UshfudpPipeRegRegRegImm,
    UshfudpPipeRegRegRegReg,
    UshlRegImmReg,
    UshlRegRegImm,
    UshlRegRegReg,
    UshludpPipeRegImmReg,
    UshludpPipeRegRegImm,
    UshludpPipeRegRegReg,
    UshrRegRegImm,
    UshrRegRegReg,
    UshrudpPipeRegRegImm,
    UshrudpPipeRegRegReg,
    UstgrAddressReg,
    UstgrudpPipeAddressReg,
    UtcatomswsPredRegReg,
    UtcatomswsPredRegRegReg,
    UtcatomswsRegReg,
    UtcatomswsudpPipePredRegReg,
    UtcatomswsudpPipePredRegRegReg,
    UtcatomswsudpPipeRegReg,
    UtcbarNoOperands,
    UtcbarAddressRegReg,
    UtcbarudpPipeNoOperands,
    UtcbarudpPipeAddressRegReg,
    UtccpAddressAddress,
    UtccpudpPipeAddressAddress,
    UtchmmaAddressAddressAddressAddressAddressRegPred,
    UtchmmaAddressAddressAddressAddressAddressRegPredImm,
    UtchmmaudpPipeAddressAddressAddressAddressAddressRegPred,
    UtchmmaudpPipeAddressAddressAddressAddressAddressRegPredImm,
    UtcimmaAddressAddressAddressAddressAddressRegPred,
    UtcimmaudpPipeAddressAddressAddressAddressAddressRegPred,
    UtcommaAddressAddressAddressAddressAddressAddressPred,
    UtcommaudpPipeAddressAddressAddressAddressAddressAddressPred,
    UtcqmmaAddressAddressAddressAddressAddressAddressPred,
    UtcqmmaAddressAddressAddressAddressAddressRegPred,
    UtcqmmaudpPipeAddressAddressAddressAddressAddressAddressPred,
    UtcqmmaudpPipeAddressAddressAddressAddressAddressRegPred,
    UtcshiftAddress,
    UtcshiftudpPipeAddress,
    UtmacctlNoOperands,
    UtmacctlAddress,
    UtmacctludpPipeNoOperands,
    UtmacctludpPipeAddress,
    UtmacmdflushNoOperands,
    UtmacmdflushudpPipeNoOperands,
    UtmaldgAddressAddress,
    UtmaldgAddressAddressAddress,
    UtmaldgAddressAddressReg,
    UtmaldgAddressAddressRegAddress,
    UtmaldgudpPipeAddressAddress,
    UtmaldgudpPipeAddressAddressAddress,
    UtmaldgudpPipeAddressAddressReg,
    UtmaldgudpPipeAddressAddressRegAddress,
    UtmapfAddressAddress,
    UtmapfAddressAddressAddress,
    UtmapfAddressAddressReg,
    UtmapfAddressAddressRegAddress,
    UtmapfudpPipeAddressAddress,
    UtmapfudpPipeAddressAddressAddress,
    UtmapfudpPipeAddressAddressReg,
    UtmapfudpPipeAddressAddressRegAddress,
    UtmaredgAddressAddress,
    UtmaredgAddressAddressAddress,
    UtmaredgudpPipeAddressAddress,
    UtmaredgudpPipeAddressAddressAddress,
    UtmastgAddressAddress,
    UtmastgAddressAddressAddress,
    UtmastgudpPipeAddressAddress,
    UtmastgudpPipeAddressAddressAddress,
    UviaddRegRegImm,
    UviaddRegRegReg,
    UviaddudpPipeRegRegImm,
    UviaddudpPipeRegRegReg,
    UvimnmxRegRegImmPred,
    UvimnmxRegRegRegPred,
    UvimnmxudpPipeRegRegImmPred,
    UvimnmxudpPipeRegRegRegPred,
    UvirtcountImm,
    UvirtcountReg,
    UvirtcountudpPipeImm,
    UvirtcountudpPipeReg,
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
    Vabsdiff2xluPipeRegRegImmReg,
    Vabsdiff2xluPipeRegRegRegReg,
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
    Vabsdiff4fauPipeRegRegImmReg,
    Vabsdiff4fauPipeRegRegRegReg,
    Vabsdiff4fmaiPipeRegRegImmReg,
    Vabsdiff4fmaiPipeRegRegRegReg,
    Vabsdiff4fxuPipeRegRegImmReg,
    Vabsdiff4fxuPipeRegRegRegReg,
    Vabsdiff4intPipeRegPredRegAddressImmReg,
    Vabsdiff4intPipeRegPredRegConstReg,
    Vabsdiff4intPipeRegPredRegImmReg,
    Vabsdiff4intPipeRegPredRegRegAddressImm,
    Vabsdiff4intPipeRegPredRegRegConst,
    Vabsdiff4intPipeRegPredRegRegImm,
    Vabsdiff4intPipeRegPredRegRegReg,
    Vabsdiff4intPipeRegRegAddressImmReg,
    Vabsdiff4intPipeRegRegConstReg,
    Vabsdiff4intPipeRegRegImmReg,
    Vabsdiff4intPipeRegRegRegAddressImm,
    Vabsdiff4intPipeRegRegRegConst,
    Vabsdiff4intPipeRegRegRegImm,
    Vabsdiff4intPipeRegRegRegReg,
    Vabsdiff4xluPipeRegRegImmReg,
    Vabsdiff4xluPipeRegRegRegReg,
    VabsdifffxuPipeRegRegImmReg,
    VabsdifffxuPipeRegRegRegReg,
    VabsdiffintPipeRegPredRegAddressImmReg,
    VabsdiffintPipeRegPredRegConstReg,
    VabsdiffintPipeRegPredRegImmReg,
    VabsdiffintPipeRegPredRegRegAddressImm,
    VabsdiffintPipeRegPredRegRegConst,
    VabsdiffintPipeRegPredRegRegImm,
    VabsdiffintPipeRegPredRegRegReg,
    VabsdiffintPipeRegRegAddressImmReg,
    VabsdiffintPipeRegRegConstReg,
    VabsdiffintPipeRegRegImmReg,
    VabsdiffintPipeRegRegRegAddressImm,
    VabsdiffintPipeRegRegRegConst,
    VabsdiffintPipeRegRegRegImm,
    VabsdiffintPipeRegRegRegReg,
    VabsdiffxluPipeRegRegImmReg,
    VabsdiffxluPipeRegRegRegReg,
    VaddRegRegReg,
    VaddRegRegRegReg,
    Vadd2RegRegReg,
    Vadd2RegRegRegReg,
    Vadd2xluPipeRegRegReg,
    Vadd2xluPipeRegRegRegReg,
    Vadd4RegRegReg,
    Vadd4RegRegRegReg,
    Vadd4xluPipeRegRegReg,
    Vadd4xluPipeRegRegRegReg,
    VaddfxuPipeRegRegReg,
    VaddfxuPipeRegRegRegReg,
    VaddxluPipeRegRegReg,
    VaddxluPipeRegRegRegReg,
    VhmnmxRegRegAddressImmRegPred,
    VhmnmxRegRegConstRegPred,
    VhmnmxRegRegImmImmRegPred,
    VhmnmxRegRegRegRegPred,
    Vhmnmxfp16PipeRegRegAddressImmRegPred,
    Vhmnmxfp16PipeRegRegConstRegPred,
    Vhmnmxfp16PipeRegRegImmImmRegPred,
    Vhmnmxfp16PipeRegRegRegRegPred,
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
    ViaddmnmxintPipeRegRegAddressImmRegPred,
    ViaddmnmxintPipeRegRegConstRegPred,
    ViaddmnmxintPipeRegRegImmRegPred,
    ViaddmnmxintPipeRegRegRegAddressImmPred,
    ViaddmnmxintPipeRegRegRegConstPred,
    ViaddmnmxintPipeRegRegRegImmPred,
    ViaddmnmxintPipeRegRegRegRegPred,
    ViaddfmalighterPipeRegRegAddressImm,
    ViaddfmalighterPipeRegRegConst,
    ViaddfmalighterPipeRegRegImm,
    ViaddfmalighterPipeRegRegReg,
    VildRegAddress,
    VildaguPipeRegAddress,
    VimnmxRegPredPredRegAddressImmPred,
    VimnmxRegPredPredRegConstPred,
    VimnmxRegPredPredRegImmPred,
    VimnmxRegPredPredRegRegPred,
    VimnmxRegRegAddressImmPred,
    VimnmxRegRegConstPred,
    VimnmxRegRegImmPred,
    VimnmxRegRegRegPred,
    Vimnmx3RegRegAddressImmRegPred,
    Vimnmx3RegRegConstRegPred
  )
}
