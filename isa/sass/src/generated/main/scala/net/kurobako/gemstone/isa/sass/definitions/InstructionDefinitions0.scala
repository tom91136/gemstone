// Generated SASS instruction definitions and checked operand constructors. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.sass
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import Instruction.*
@javax.annotation.processing.Generated(Array("sbt codegen"))
object InstructionDefinitions0 {
  object AcqbulkNoOperands extends Definition("AcqbulkNoOperands", "ACQBULK", Vec(), Vec("acqbulk_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.AcqbulkNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object AcqbulkcbuPipeNoOperands
      extends Definition("AcqbulkcbuPipeNoOperands", "ACQBULKcbu_pipe", Vec(), Vec("acqbulk_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.AcqbulkcbuPipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object AcqshminitNoOperands extends Definition("AcqshminitNoOperands", "ACQSHMINIT", Vec(), Vec("acqshminit_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.AcqshminitNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object AcqshminitcbuPipeNoOperands
      extends Definition("AcqshminitcbuPipeNoOperands", "ACQSHMINITcbu_pipe", Vec(), Vec("acqshminit_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.AcqshminitcbuPipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object Al2pPredRegReg extends Definition(
        "Al2pPredRegReg",
        "AL2P",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register)
        ),
        Vec("AL2P", "I_AL2P")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA)
            ) => Some(Instruction.Al2pPredRegReg(dstPredicate, dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object Al2pPredRegRegImm extends Definition(
        "Al2pPredRegRegImm",
        "AL2P",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sImm", OperandKind.Immediate)
        ),
        Vec("AL2P", "I_AL2P")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sImm)
            ) => Some(Instruction.Al2pPredRegRegImm(dstPredicate, dst, srcA, sImm, modifiers, guard))
        case _ => None
      }
  }
  object Al2pRegReg extends Definition(
        "Al2pRegReg",
        "AL2P",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register)),
        Vec("AL2P", "I AL2P", "I_AL2P")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcA)) =>
          Some(Instruction.Al2pRegReg(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object Al2pRegRegImm extends Definition(
        "Al2pRegRegImm",
        "AL2P",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("ra_offset", OperandKind.Immediate)
        ),
        Vec("AL2P", "I AL2P", "I_AL2P", "al2p__RaNonRZ", "al2p__RaRZ")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(ra_offset)
            ) => Some(Instruction.Al2pRegRegImm(dst, srcA, ra_offset, modifiers, guard))
        case _ => None
      }
  }
  object Al2pmioPipePredRegReg extends Definition(
        "Al2pmioPipePredRegReg",
        "AL2Pmio_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register)
        ),
        Vec("AL2P", "I_AL2P")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA)
            ) => Some(Instruction.Al2pmioPipePredRegReg(dstPredicate, dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object Al2pmioPipePredRegRegImm extends Definition(
        "Al2pmioPipePredRegRegImm",
        "AL2Pmio_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sImm", OperandKind.Immediate)
        ),
        Vec("AL2P", "I_AL2P")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sImm)
            ) => Some(Instruction.Al2pmioPipePredRegRegImm(dstPredicate, dst, srcA, sImm, modifiers, guard))
        case _ => None
      }
  }
  object Al2pmioPipeRegReg extends Definition(
        "Al2pmioPipeRegReg",
        "AL2Pmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register)),
        Vec("AL2P", "I_AL2P")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcA)) =>
          Some(Instruction.Al2pmioPipeRegReg(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object Al2pmioPipeRegRegImm extends Definition(
        "Al2pmioPipeRegRegImm",
        "AL2Pmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("ra_offset", OperandKind.Immediate)
        ),
        Vec("AL2P", "I_AL2P", "al2p__RaNonRZ", "al2p__RaRZ")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(ra_offset)
            ) => Some(Instruction.Al2pmioPipeRegRegImm(dst, srcA, ra_offset, modifiers, guard))
        case _ => None
      }
  }
  object Al2pxluPipeRegReg extends Definition(
        "Al2pxluPipeRegReg",
        "AL2Pxlu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register)),
        Vec("AL2P", "I AL2P")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcA)) =>
          Some(Instruction.Al2pxluPipeRegReg(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object Al2pxluPipeRegRegImm extends Definition(
        "Al2pxluPipeRegRegImm",
        "AL2Pxlu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sImm", OperandKind.Immediate)),
        Vec("AL2P", "I AL2P")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sImm)
            ) => Some(Instruction.Al2pxluPipeRegRegImm(dst, srcA, sImm, modifiers, guard))
        case _ => None
      }
  }
  object AldRegAddressReg extends Definition(
        "AldRegAddressReg",
        "ALD",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register)),
        Vec(
          "ALD",
          "ALD PHYS",
          "ALD_PHYS",
          "I ALD",
          "I_ALD",
          "I_ALD_PATCH",
          "ald_PHYS_",
          "ald_UR__LOGICAL_URa_default",
          "ald_UR__PATCH_URa_P_RbRZ",
          "ald__LOGICAL_RaRZ_default",
          "ald__PATCH_RaNonRZOffset_P_RbRZ",
          "ald__PATCH_RaRZ_P_RbRZ"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.AldRegAddressReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object AldaguPipeRegAddressReg extends Definition(
        "AldaguPipeRegAddressReg",
        "ALDagu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register)),
        Vec("ALD", "ALD PHYS", "I ALD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.AldaguPipeRegAddressReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object AldmioPipeRegAddressReg extends Definition(
        "AldmioPipeRegAddressReg",
        "ALDmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register)),
        Vec(
          "ALD",
          "ALD_PHYS",
          "I_ALD",
          "I_ALD_PATCH",
          "ald_PHYS_",
          "ald_UR__LOGICAL_URa_default",
          "ald_UR__PATCH_URa_P_RbRZ",
          "ald__LOGICAL_RaRZ_default",
          "ald__PATCH_RaNonRZOffset_P_RbRZ",
          "ald__PATCH_RaRZ_P_RbRZ"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.AldmioPipeRegAddressReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object ArrivesAddress
      extends Definition("ArrivesAddress", "ARRIVES", Vec(Slot("srcA", OperandKind.Address)), Vec("arrives_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA)) => Some(Instruction.ArrivesAddress(srcA, modifiers, guard))
        case _ => None
      }
  }
  object ArrivesmioPipeAddress extends Definition(
        "ArrivesmioPipeAddress",
        "ARRIVESmio_pipe",
        Vec(Slot("srcA", OperandKind.Address)),
        Vec("arrives_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA)) => Some(Instruction.ArrivesmioPipeAddress(srcA, modifiers, guard))
        case _ => None
      }
  }
  object AstAddressRegReg extends Definition(
        "AstAddressRegReg",
        "AST",
        Vec(Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register), Slot("srcC", OperandKind.Register)),
        Vec(
          "AST",
          "AST PHYS",
          "AST_PHYS",
          "I AST",
          "I_AST",
          "I_AST_PATCH",
          "ast_PHYS_",
          "ast_UR__LOGICAL_URa",
          "ast_UR__PATCH_RaRZ_URa",
          "ast__LOGICAL_RaRZ",
          "ast__PATCH_RaNonRZOffset",
          "ast__PATCH_RaRZ"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.AstAddressRegReg(srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object AstaguPipeAddressRegReg extends Definition(
        "AstaguPipeAddressRegReg",
        "ASTagu_pipe",
        Vec(Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register), Slot("srcC", OperandKind.Register)),
        Vec("AST", "AST PHYS", "I AST")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.AstaguPipeAddressRegReg(srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object AstmioPipeAddressRegReg extends Definition(
        "AstmioPipeAddressRegReg",
        "ASTmio_pipe",
        Vec(Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register), Slot("srcC", OperandKind.Register)),
        Vec(
          "AST",
          "AST_PHYS",
          "I_AST",
          "I_AST_PATCH",
          "ast_PHYS_",
          "ast_UR__LOGICAL_URa",
          "ast_UR__PATCH_RaRZ_URa",
          "ast__LOGICAL_RaRZ",
          "ast__PATCH_RaNonRZOffset",
          "ast__PATCH_RaRZ"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.AstmioPipeAddressRegReg(srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object AtomPredRegAddress extends Definition(
        "AtomPredRegAddress",
        "ATOM",
        Vec(Slot("pu", OperandKind.Predicate), Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address)),
        Vec(
          "atom_arrive__Ra32_arrive",
          "atom_arrive__Ra32_popcinc",
          "atom_arrive__Ra64_arrive",
          "atom_arrive__Ra64_popcinc",
          "atom_arrive__RaRZ_arrive",
          "atom_arrive__RaRZ_popcinc"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA)
            ) => Some(Instruction.AtomPredRegAddress(pu, dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object AtomPredRegAddressReg extends Definition(
        "AtomPredRegAddressReg",
        "ATOM",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register)
        ),
        Vec(
          "ATOM_SPARSE",
          "atom__RaNonRZ",
          "atom__RaRZ",
          "atom_fp__RaNonRZ",
          "atom_fp__RaRZ",
          "atom_fp_uniform__Ra32",
          "atom_fp_uniform__Ra64",
          "atom_fp_uniform__RaRZ",
          "atom_fp_uniform__memdesc",
          "atom_int__RaNonRZ",
          "atom_int__RaRZ",
          "atom_int_uniform__Ra32",
          "atom_int_uniform__Ra64",
          "atom_int_uniform__RaRZ",
          "atom_int_uniform__memdesc",
          "atom_uniform__Ra32",
          "atom_uniform__Ra64",
          "atom_uniform__RaRZ",
          "atom_uniform__memdesc",
          "atom_uniform_weakonly__Ra32",
          "atom_uniform_weakonly__Ra64",
          "atom_uniform_weakonly__RaRZ",
          "atom_weakonly__RaNonRZ",
          "atom_weakonly__RaRZ"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.AtomPredRegAddressReg(dstPredicate, dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object AtomPredRegAddressRegReg extends Definition(
        "AtomPredRegAddressRegReg",
        "ATOM",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec(
          "ATOM_CAS_RZ_and_Rc_SPARSE",
          "ATOM_CAS_Rb_and_RZ_SPARSE",
          "ATOM_CAS_SPARSE",
          "ATOM_SPARSE",
          "atom_cas__RaNonRZ_CAS",
          "atom_cas__RaNonRZ_CAST",
          "atom_cas__RaRZ_CAS",
          "atom_cas__RaRZ_CAST",
          "atom_cas_weakonly__RaNonRZ_CAS",
          "atom_cas_weakonly__RaNonRZ_CAST",
          "atom_cas_weakonly__RaRZ_CAS",
          "atom_cas_weakonly__RaRZ_CAST"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.AtomPredRegAddressRegReg(dstPredicate, dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object AtomRegAddressReg extends Definition(
        "AtomRegAddressReg",
        "ATOM",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register)),
        Vec("ATOM", "ATOM Rd is RZ", "I ATOM", "I ATOM Rd is RZ", "I_ATOM")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.AtomRegAddressReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object AtomRegAddressRegReg extends Definition(
        "AtomRegAddressRegReg",
        "ATOM",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec(
          "ATOM",
          "ATOM CAS",
          "ATOM CAS RZ and Rc",
          "ATOM CAS Rb and RZ",
          "ATOM Rd is RZ",
          "ATOM_CAS",
          "ATOM_CAS_RZ_and_Rc",
          "ATOM_CAS_Rb_and_RZ",
          "I ATOM",
          "I ATOM CAS",
          "I ATOM CAS RZ and Rc",
          "I ATOM CAS Rb and RZ",
          "I ATOM Rd is RZ",
          "I_ATOM",
          "I_ATOM_CAS",
          "I_ATOM_CAS_RZ_and_Rc",
          "I_ATOM_CAS_Rb_and_RZ"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.AtomRegAddressRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object AtomgPredRegAddressReg extends Definition(
        "AtomgPredRegAddressReg",
        "ATOMG",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register)
        ),
        Vec(
          "atomg__RaNonRZ",
          "atomg__RaRZ",
          "atomg_fp__RaNonRZ",
          "atomg_fp__RaRZ",
          "atomg_fp_uniform__Ra32",
          "atomg_fp_uniform__Ra64",
          "atomg_fp_uniform__RaRZ",
          "atomg_fp_uniform__memdesc",
          "atomg_int__RaNonRZ",
          "atomg_int__RaRZ",
          "atomg_int_uniform__Ra32",
          "atomg_int_uniform__Ra64",
          "atomg_int_uniform__RaRZ",
          "atomg_int_uniform__memdesc",
          "atomg_uniform__Ra32",
          "atomg_uniform__Ra64",
          "atomg_uniform__RaRZ",
          "atomg_uniform__memdesc",
          "atomg_uniform_weakonly__Ra32",
          "atomg_uniform_weakonly__Ra64",
          "atomg_uniform_weakonly__RaRZ",
          "atomg_weakonly__RaNonRZ",
          "atomg_weakonly__RaRZ"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.AtomgPredRegAddressReg(pu, dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object AtomgPredRegAddressRegReg extends Definition(
        "AtomgPredRegAddressRegReg",
        "ATOMG",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("atomg_cas__RaNonRZ", "atomg_cas__RaRZ", "atomg_cas_weakonly__RaNonRZ", "atomg_cas_weakonly__RaRZ")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.AtomgPredRegAddressRegReg(pu, dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object AtomgmioPipePredRegAddressReg extends Definition(
        "AtomgmioPipePredRegAddressReg",
        "ATOMGmio_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register)
        ),
        Vec(
          "atomg__RaNonRZ",
          "atomg__RaRZ",
          "atomg_fp__RaNonRZ",
          "atomg_fp__RaRZ",
          "atomg_fp_uniform__Ra32",
          "atomg_fp_uniform__Ra64",
          "atomg_fp_uniform__RaRZ",
          "atomg_fp_uniform__memdesc",
          "atomg_int__RaNonRZ",
          "atomg_int__RaRZ",
          "atomg_int_uniform__Ra32",
          "atomg_int_uniform__Ra64",
          "atomg_int_uniform__RaRZ",
          "atomg_int_uniform__memdesc",
          "atomg_uniform__Ra32",
          "atomg_uniform__Ra64",
          "atomg_uniform__RaRZ",
          "atomg_uniform__memdesc",
          "atomg_uniform_weakonly__Ra32",
          "atomg_uniform_weakonly__Ra64",
          "atomg_uniform_weakonly__RaRZ",
          "atomg_weakonly__RaNonRZ",
          "atomg_weakonly__RaRZ"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.AtomgmioPipePredRegAddressReg(pu, dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object AtomgmioPipePredRegAddressRegReg extends Definition(
        "AtomgmioPipePredRegAddressRegReg",
        "ATOMGmio_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("atomg_cas__RaNonRZ", "atomg_cas__RaRZ", "atomg_cas_weakonly__RaNonRZ", "atomg_cas_weakonly__RaRZ")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.AtomgmioPipePredRegAddressRegReg(pu, dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object AtomsPredAddressRegReg extends Definition(
        "AtomsPredAddressRegReg",
        "ATOMS",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("atoms_cast_destPu__RaNonRZ", "atoms_cast_destPu__RaRZ")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.AtomsPredAddressRegReg(pu, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object AtomsRegAddress extends Definition(
        "AtomsRegAddress",
        "ATOMS",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address)),
        Vec("atoms_arrive__arrive", "atoms_arrive__popcinc")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Address(srcA)) =>
          Some(Instruction.AtomsRegAddress(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object AtomsRegAddressReg extends Definition(
        "AtomsRegAddressReg",
        "ATOMS",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register)),
        Vec("ATOMS", "I_ATOMS", "atoms__RaNonRZ", "atoms__RaRZ", "atoms_uniform_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.AtomsRegAddressReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object AtomsRegAddressRegReg extends Definition(
        "AtomsRegAddressRegReg",
        "ATOMS",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec(
          "ATOMS",
          "ATOMS_CAS",
          "ATOMS_CAST",
          "ATOMS_CAST_RZ_and_Rc",
          "ATOMS_CAST_Rb_and_RZ",
          "ATOMS_CAS_RZ_and_Rc",
          "ATOMS_CAS_Rb_and_RZ",
          "I_ATOMS",
          "I_ATOMS_CAS",
          "I_ATOMS_CAST",
          "I_ATOMS_CAST_RZ_and_Rc",
          "I_ATOMS_CAST_Rb_and_RZ",
          "I_ATOMS_CAS_RZ_and_Rc",
          "I_ATOMS_CAS_Rb_and_RZ",
          "atoms_cas__RaNonRZ",
          "atoms_cas__RaNonRZ_CAS",
          "atoms_cas__RaNonRZ_CAST",
          "atoms_cas__RaRZ",
          "atoms_cas__RaRZ_CAS",
          "atoms_cas__RaRZ_CAST",
          "atoms_cast_destRd__RaNonRZ",
          "atoms_cast_destRd__RaRZ"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.AtomsRegAddressRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object AtomsmioPipePredAddressRegReg extends Definition(
        "AtomsmioPipePredAddressRegReg",
        "ATOMSmio_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("atoms_cast_destPu__RaNonRZ", "atoms_cast_destPu__RaRZ")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.AtomsmioPipePredAddressRegReg(pu, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object AtomsmioPipeRegAddress extends Definition(
        "AtomsmioPipeRegAddress",
        "ATOMSmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address)),
        Vec("atoms_arrive__arrive", "atoms_arrive__popcinc")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Address(srcA)) =>
          Some(Instruction.AtomsmioPipeRegAddress(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object AtomsmioPipeRegAddressReg extends Definition(
        "AtomsmioPipeRegAddressReg",
        "ATOMSmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register)),
        Vec("ATOMS", "I_ATOMS", "atoms__RaNonRZ", "atoms__RaRZ", "atoms_uniform_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.AtomsmioPipeRegAddressReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object AtomsmioPipeRegAddressRegReg extends Definition(
        "AtomsmioPipeRegAddressRegReg",
        "ATOMSmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec(
          "ATOMS",
          "ATOMS_CAS",
          "ATOMS_CAST",
          "ATOMS_CAST_RZ_and_Rc",
          "ATOMS_CAST_Rb_and_RZ",
          "ATOMS_CAS_RZ_and_Rc",
          "ATOMS_CAS_Rb_and_RZ",
          "I_ATOMS",
          "I_ATOMS_CAS",
          "I_ATOMS_CAST",
          "I_ATOMS_CAST_RZ_and_Rc",
          "I_ATOMS_CAST_Rb_and_RZ",
          "I_ATOMS_CAS_RZ_and_Rc",
          "I_ATOMS_CAS_Rb_and_RZ",
          "atoms_cas__RaNonRZ",
          "atoms_cas__RaNonRZ_CAS",
          "atoms_cas__RaNonRZ_CAST",
          "atoms_cas__RaRZ",
          "atoms_cas__RaRZ_CAS",
          "atoms_cas__RaRZ_CAST",
          "atoms_cast_destRd__RaNonRZ",
          "atoms_cast_destRd__RaRZ"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.AtomsmioPipeRegAddressRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object AtomaguPipeRegAddressReg extends Definition(
        "AtomaguPipeRegAddressReg",
        "ATOMagu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register)),
        Vec("ATOM", "ATOM Rd is RZ", "I ATOM", "I ATOM Rd is RZ")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.AtomaguPipeRegAddressReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object AtomaguPipeRegAddressRegReg extends Definition(
        "AtomaguPipeRegAddressRegReg",
        "ATOMagu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec(
          "ATOM",
          "ATOM CAS",
          "ATOM CAS RZ and Rc",
          "ATOM CAS Rb and RZ",
          "ATOM Rd is RZ",
          "I ATOM",
          "I ATOM CAS",
          "I ATOM CAS RZ and Rc",
          "I ATOM CAS Rb and RZ",
          "I ATOM Rd is RZ"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.AtomaguPipeRegAddressRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object AtommioPipePredRegAddress extends Definition(
        "AtommioPipePredRegAddress",
        "ATOMmio_pipe",
        Vec(Slot("pu", OperandKind.Predicate), Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address)),
        Vec(
          "atom_arrive__Ra32_arrive",
          "atom_arrive__Ra32_popcinc",
          "atom_arrive__Ra64_arrive",
          "atom_arrive__Ra64_popcinc",
          "atom_arrive__RaRZ_arrive",
          "atom_arrive__RaRZ_popcinc"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA)
            ) => Some(Instruction.AtommioPipePredRegAddress(pu, dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object AtommioPipePredRegAddressReg extends Definition(
        "AtommioPipePredRegAddressReg",
        "ATOMmio_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register)
        ),
        Vec(
          "ATOM_SPARSE",
          "atom__RaNonRZ",
          "atom__RaRZ",
          "atom_fp__RaNonRZ",
          "atom_fp__RaRZ",
          "atom_fp_uniform__Ra32",
          "atom_fp_uniform__Ra64",
          "atom_fp_uniform__RaRZ",
          "atom_fp_uniform__memdesc",
          "atom_int__RaNonRZ",
          "atom_int__RaRZ",
          "atom_int_uniform__Ra32",
          "atom_int_uniform__Ra64",
          "atom_int_uniform__RaRZ",
          "atom_int_uniform__memdesc",
          "atom_uniform__Ra32",
          "atom_uniform__Ra64",
          "atom_uniform__RaRZ",
          "atom_uniform__memdesc",
          "atom_uniform_weakonly__Ra32",
          "atom_uniform_weakonly__Ra64",
          "atom_uniform_weakonly__RaRZ",
          "atom_weakonly__RaNonRZ",
          "atom_weakonly__RaRZ"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.AtommioPipePredRegAddressReg(dstPredicate, dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object AtommioPipePredRegAddressRegReg extends Definition(
        "AtommioPipePredRegAddressRegReg",
        "ATOMmio_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec(
          "ATOM_CAS_RZ_and_Rc_SPARSE",
          "ATOM_CAS_Rb_and_RZ_SPARSE",
          "ATOM_CAS_SPARSE",
          "ATOM_SPARSE",
          "atom_cas__RaNonRZ_CAS",
          "atom_cas__RaNonRZ_CAST",
          "atom_cas__RaRZ_CAS",
          "atom_cas__RaRZ_CAST",
          "atom_cas_weakonly__RaNonRZ_CAS",
          "atom_cas_weakonly__RaNonRZ_CAST",
          "atom_cas_weakonly__RaRZ_CAS",
          "atom_cas_weakonly__RaRZ_CAST"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) =>
          Some(Instruction.AtommioPipePredRegAddressRegReg(dstPredicate, dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object AtommioPipeRegAddressReg extends Definition(
        "AtommioPipeRegAddressReg",
        "ATOMmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register)),
        Vec("ATOM", "I_ATOM")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.AtommioPipeRegAddressReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object AtommioPipeRegAddressRegReg extends Definition(
        "AtommioPipeRegAddressRegReg",
        "ATOMmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec(
          "ATOM",
          "ATOM_CAS",
          "ATOM_CAS_RZ_and_Rc",
          "ATOM_CAS_Rb_and_RZ",
          "I_ATOM",
          "I_ATOM_CAS",
          "I_ATOM_CAS_RZ_and_Rc",
          "I_ATOM_CAS_Rb_and_RZ"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.AtommioPipeRegAddressRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object B2rReg extends Definition(
        "B2rReg",
        "B2R",
        Vec(Slot("dst", OperandKind.Register)),
        Vec("B2R RESULT", "B2R WARP", "B2R_RESULT", "B2R_WARP", "b2r__RESULT", "b2r__WARP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst)) => Some(Instruction.B2rReg(dst, modifiers, guard))
        case _ => None
      }
  }
  object B2rRegImm extends Definition(
        "B2rRegImm",
        "B2R",
        Vec(Slot("dst", OperandKind.Register), Slot("barNum", OperandKind.Immediate)),
        Vec("B2R BAR", "B2R_BAR", "b2r__BAR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(barNum)) =>
          Some(Instruction.B2rRegImm(dst, barNum, modifiers, guard))
        case _ => None
      }
  }
  object B2rRegPred extends Definition(
        "B2rRegPred",
        "B2R",
        Vec(Slot("dst", OperandKind.Register), Slot("dstPredicate", OperandKind.Predicate)),
        Vec("B2R RESULT", "B2R_RESULT", "b2r__RESULT")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Predicate(dstPredicate)) =>
          Some(Instruction.B2rRegPred(dst, dstPredicate, modifiers, guard))
        case _ => None
      }
  }
  object B2rfePipeReg extends Definition(
        "B2rfePipeReg",
        "B2Rfe_pipe",
        Vec(Slot("dst", OperandKind.Register)),
        Vec("B2R RESULT", "B2R WARP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst)) => Some(Instruction.B2rfePipeReg(dst, modifiers, guard))
        case _ => None
      }
  }
  object B2rfePipeRegImm extends Definition(
        "B2rfePipeRegImm",
        "B2Rfe_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("barNum", OperandKind.Immediate)),
        Vec("B2R BAR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(barNum)) =>
          Some(Instruction.B2rfePipeRegImm(dst, barNum, modifiers, guard))
        case _ => None
      }
  }
  object B2rfePipeRegPred extends Definition(
        "B2rfePipeRegPred",
        "B2Rfe_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("dstPredicate", OperandKind.Predicate)),
        Vec("B2R RESULT")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Predicate(dstPredicate)) =>
          Some(Instruction.B2rfePipeRegPred(dst, dstPredicate, modifiers, guard))
        case _ => None
      }
  }
  object B2rmioPipeReg extends Definition(
        "B2rmioPipeReg",
        "B2Rmio_pipe",
        Vec(Slot("dst", OperandKind.Register)),
        Vec("B2R_RESULT", "B2R_WARP", "b2r__RESULT", "b2r__WARP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst)) => Some(Instruction.B2rmioPipeReg(dst, modifiers, guard))
        case _ => None
      }
  }
  object B2rmioPipeRegImm extends Definition(
        "B2rmioPipeRegImm",
        "B2Rmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("barNum", OperandKind.Immediate)),
        Vec("B2R_BAR", "b2r__BAR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(barNum)) =>
          Some(Instruction.B2rmioPipeRegImm(dst, barNum, modifiers, guard))
        case _ => None
      }
  }
  object B2rmioPipeRegPred extends Definition(
        "B2rmioPipeRegPred",
        "B2Rmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("dstPredicate", OperandKind.Predicate)),
        Vec("B2R_RESULT", "b2r__RESULT")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Predicate(dstPredicate)) =>
          Some(Instruction.B2rmioPipeRegPred(dst, dstPredicate, modifiers, guard))
        case _ => None
      }
  }
  object BarNoOperands extends Definition(
        "BarNoOperands",
        "BAR",
        Vec(),
        Vec("BAR syncall", "BAR_syncall", "bar__SYNCALL_dfrBlk_noSrc_II", "bar__SYNCALL_noSrc_II")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.BarNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object BarImm extends Definition(
        "BarImm",
        "BAR",
        Vec(Slot("sb", OperandKind.Immediate)),
        Vec("BAR_Sync_a_b", "a b BAR Sync", "bar__SYNC_II_optionalCount_II", "bar__SYNC_dfrBlk_II_optionalCount_II")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb)) => Some(Instruction.BarImm(sb, modifiers, guard))
        case _ => None
      }
  }
  object BarImmImm extends Definition(
        "BarImmImm",
        "BAR",
        Vec(Slot("sb", OperandKind.Immediate), Slot("sc", OperandKind.Immediate)),
        Vec(
          "BAR_Arv_imm_a",
          "BAR_Sync_a_b",
          "a BAR Arv imm",
          "a b BAR Sync",
          "bar__ARV_II_II",
          "bar__SYNC_II_optionalCount_II",
          "bar__SYNC_dfrBlk_II_optionalCount_II"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb), InstructionOperand.Immediate(sc)) =>
          Some(Instruction.BarImmImm(sb, sc, modifiers, guard))
        case _ => None
      }
  }
  object BarImmImmPred extends Definition(
        "BarImmImmPred",
        "BAR",
        Vec(Slot("sb", OperandKind.Immediate), Slot("sc", OperandKind.Immediate), Slot("pp", OperandKind.Predicate)),
        Vec(
          "BAR_Red_a_b",
          "BAR_scan_imm_a",
          "a BAR scan imm",
          "a b BAR Red",
          "bar__RED_II_optionalCount_II",
          "bar__RED_dfrBlk_II_optionalCount_II",
          "bar__SCAN_II_II"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Immediate(sb),
              InstructionOperand.Immediate(sc),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.BarImmImmPred(sb, sc, pp, modifiers, guard))
        case _ => None
      }
  }
  object BarImmPred extends Definition(
        "BarImmPred",
        "BAR",
        Vec(Slot("sb", OperandKind.Immediate), Slot("pp", OperandKind.Predicate)),
        Vec("BAR_Red_a_b", "a b BAR Red", "bar__RED_II_optionalCount_II", "bar__RED_dfrBlk_II_optionalCount_II")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb), InstructionOperand.Predicate(pp)) =>
          Some(Instruction.BarImmPred(sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object BarImmReg extends Definition(
        "BarImmReg",
        "BAR",
        Vec(Slot("sb", OperandKind.Immediate), Slot("srcC", OperandKind.Register)),
        Vec(
          "BAR_Arv_a",
          "BAR_Sync_a",
          "a BAR Arv",
          "a BAR Sync",
          "bar__ARV_IR_IR",
          "bar__SYNC_IR_optionalCount_IR",
          "bar__SYNC_dfrBlk_IR_optionalCount_IR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb), InstructionOperand.Register(srcC)) =>
          Some(Instruction.BarImmReg(sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object BarImmRegPred extends Definition(
        "BarImmRegPred",
        "BAR",
        Vec(Slot("sb", OperandKind.Immediate), Slot("srcC", OperandKind.Register), Slot("pp", OperandKind.Predicate)),
        Vec(
          "BAR_Red_a",
          "BAR_Scan_a",
          "a BAR Red",
          "a BAR Scan",
          "bar__RED_IR_optionalCount_IR",
          "bar__RED_dfrBlk_IR_optionalCount_IR",
          "bar__SCAN_IR_IR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.BarImmRegPred(sb, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object BarReg extends Definition(
        "BarReg",
        "BAR",
        Vec(Slot("srcA", OperandKind.Register)),
        Vec("BAR_Sync_b", "b BAR Sync", "bar__SYNC_RI_optionalCount_RI", "bar__SYNC_dfrBlk_RI_optionalCount_RI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA)) => Some(Instruction.BarReg(srcA, modifiers, guard))
        case _ => None
      }
  }
  object BarRegImm extends Definition(
        "BarRegImm",
        "BAR",
        Vec(Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec(
          "BAR Arv imm",
          "BAR_Arv_imm",
          "BAR_Sync_b",
          "b BAR Sync",
          "bar__ARV_RI_RI",
          "bar__SYNC_RI_optionalCount_RI",
          "bar__SYNC_dfrBlk_RI_optionalCount_RI"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA), InstructionOperand.Immediate(uImm)) =>
          Some(Instruction.BarRegImm(srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object BarRegImmPred extends Definition(
        "BarRegImmPred",
        "BAR",
        Vec(Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate), Slot("pa", OperandKind.Predicate)),
        Vec(
          "BAR scan imm",
          "BAR_Red_b",
          "BAR_scan_imm",
          "b BAR Red",
          "bar__RED_RI_optionalCount_RI",
          "bar__RED_dfrBlk_RI_optionalCount_RI",
          "bar__SCAN_RI_RI"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.BarRegImmPred(srcA, uImm, pa, modifiers, guard))
        case _ => None
      }
  }
  object BarRegPred extends Definition(
        "BarRegPred",
        "BAR",
        Vec(Slot("srcA", OperandKind.Register), Slot("pa", OperandKind.Predicate)),
        Vec("BAR_Red_b", "b BAR Red", "bar__RED_RI_optionalCount_RI", "bar__RED_dfrBlk_RI_optionalCount_RI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA), InstructionOperand.Predicate(pa)) =>
          Some(Instruction.BarRegPred(srcA, pa, modifiers, guard))
        case _ => None
      }
  }
  object BarRegReg extends Definition(
        "BarRegReg",
        "BAR",
        Vec(Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("BAR Arv", "BAR Sync", "BAR_Arv", "BAR_Sync", "bar__ARV_RR_RR", "bar__SYNC_RR_RR", "bar__SYNC_dfrBlk_RR_RR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA), InstructionOperand.Register(srcB)) =>
          Some(Instruction.BarRegReg(srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object BarRegRegPred extends Definition(
        "BarRegRegPred",
        "BAR",
        Vec(Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register), Slot("pa", OperandKind.Predicate)),
        Vec("BAR Red", "BAR Scan", "BAR_Red", "BAR_Scan", "bar__RED_RR_RR", "bar__RED_dfrBlk_RR_RR", "bar__SCAN_RR_RR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.BarRegRegPred(srcA, srcB, pa, modifiers, guard))
        case _ => None
      }
  }
  object BarfePipeNoOperands extends Definition("BarfePipeNoOperands", "BARfe_pipe", Vec(), Vec("BAR syncall")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.BarfePipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object BarfePipeImm extends Definition(
        "BarfePipeImm",
        "BARfe_pipe",
        Vec(Slot("barNum", OperandKind.Immediate)),
        Vec("a b BAR Sync")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(barNum)) => Some(Instruction.BarfePipeImm(barNum, modifiers, guard))
        case _ => None
      }
  }
  object BarfePipeImmImm extends Definition(
        "BarfePipeImmImm",
        "BARfe_pipe",
        Vec(Slot("barNum", OperandKind.Immediate), Slot("uImm", OperandKind.Immediate)),
        Vec("a BAR Arv imm", "a b BAR Sync")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(barNum), InstructionOperand.Immediate(uImm)) =>
          Some(Instruction.BarfePipeImmImm(barNum, uImm, modifiers, guard))
        case _ => None
      }
  }
  object BarfePipeImmImmPred extends Definition(
        "BarfePipeImmImmPred",
        "BARfe_pipe",
        Vec(
          Slot("barNum", OperandKind.Immediate),
          Slot("uImm", OperandKind.Immediate),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("a BAR scan imm", "a b BAR Red")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Immediate(barNum),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.BarfePipeImmImmPred(barNum, uImm, pa, modifiers, guard))
        case _ => None
      }
  }
  object BarfePipeImmPred extends Definition(
        "BarfePipeImmPred",
        "BARfe_pipe",
        Vec(Slot("barNum", OperandKind.Immediate), Slot("pa", OperandKind.Predicate)),
        Vec("a b BAR Red")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(barNum), InstructionOperand.Predicate(pa)) =>
          Some(Instruction.BarfePipeImmPred(barNum, pa, modifiers, guard))
        case _ => None
      }
  }
  object BarfePipeImmReg extends Definition(
        "BarfePipeImmReg",
        "BARfe_pipe",
        Vec(Slot("barNum", OperandKind.Immediate), Slot("srcB", OperandKind.Register)),
        Vec("a BAR Arv", "a BAR Sync")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(barNum), InstructionOperand.Register(srcB)) =>
          Some(Instruction.BarfePipeImmReg(barNum, srcB, modifiers, guard))
        case _ => None
      }
  }
  object BarfePipeImmRegPred extends Definition(
        "BarfePipeImmRegPred",
        "BARfe_pipe",
        Vec(
          Slot("barNum", OperandKind.Immediate),
          Slot("srcB", OperandKind.Register),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("a BAR Red", "a BAR Scan")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Immediate(barNum),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.BarfePipeImmRegPred(barNum, srcB, pa, modifiers, guard))
        case _ => None
      }
  }
  object BarfePipeReg
      extends Definition("BarfePipeReg", "BARfe_pipe", Vec(Slot("srcA", OperandKind.Register)), Vec("b BAR Sync")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA)) => Some(Instruction.BarfePipeReg(srcA, modifiers, guard))
        case _ => None
      }
  }
  object BarfePipeRegImm extends Definition(
        "BarfePipeRegImm",
        "BARfe_pipe",
        Vec(Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("BAR Arv imm", "b BAR Sync")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA), InstructionOperand.Immediate(uImm)) =>
          Some(Instruction.BarfePipeRegImm(srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object BarfePipeRegImmPred extends Definition(
        "BarfePipeRegImmPred",
        "BARfe_pipe",
        Vec(Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate), Slot("pa", OperandKind.Predicate)),
        Vec("BAR scan imm", "b BAR Red")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.BarfePipeRegImmPred(srcA, uImm, pa, modifiers, guard))
        case _ => None
      }
  }
  object BarfePipeRegPred extends Definition(
        "BarfePipeRegPred",
        "BARfe_pipe",
        Vec(Slot("srcA", OperandKind.Register), Slot("pa", OperandKind.Predicate)),
        Vec("b BAR Red")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA), InstructionOperand.Predicate(pa)) =>
          Some(Instruction.BarfePipeRegPred(srcA, pa, modifiers, guard))
        case _ => None
      }
  }
  object BarfePipeRegReg extends Definition(
        "BarfePipeRegReg",
        "BARfe_pipe",
        Vec(Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("BAR Arv", "BAR Sync")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA), InstructionOperand.Register(srcB)) =>
          Some(Instruction.BarfePipeRegReg(srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object BarfePipeRegRegPred extends Definition(
        "BarfePipeRegRegPred",
        "BARfe_pipe",
        Vec(Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register), Slot("pa", OperandKind.Predicate)),
        Vec("BAR Red", "BAR Scan")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.BarfePipeRegRegPred(srcA, srcB, pa, modifiers, guard))
        case _ => None
      }
  }
  object BarmioPipeNoOperands extends Definition(
        "BarmioPipeNoOperands",
        "BARmio_pipe",
        Vec(),
        Vec("BAR_syncall", "bar__SYNCALL_dfrBlk_noSrc_II", "bar__SYNCALL_noSrc_II")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.BarmioPipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object BarmioPipeImm extends Definition(
        "BarmioPipeImm",
        "BARmio_pipe",
        Vec(Slot("sb", OperandKind.Immediate)),
        Vec("BAR_Sync_a_b", "bar__SYNC_II_optionalCount_II", "bar__SYNC_dfrBlk_II_optionalCount_II")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb)) => Some(Instruction.BarmioPipeImm(sb, modifiers, guard))
        case _ => None
      }
  }
  object BarmioPipeImmImm extends Definition(
        "BarmioPipeImmImm",
        "BARmio_pipe",
        Vec(Slot("sb", OperandKind.Immediate), Slot("sc", OperandKind.Immediate)),
        Vec(
          "BAR_Arv_imm_a",
          "BAR_Sync_a_b",
          "bar__ARV_II_II",
          "bar__SYNC_II_optionalCount_II",
          "bar__SYNC_dfrBlk_II_optionalCount_II"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb), InstructionOperand.Immediate(sc)) =>
          Some(Instruction.BarmioPipeImmImm(sb, sc, modifiers, guard))
        case _ => None
      }
  }
  object BarmioPipeImmImmPred extends Definition(
        "BarmioPipeImmImmPred",
        "BARmio_pipe",
        Vec(Slot("sb", OperandKind.Immediate), Slot("sc", OperandKind.Immediate), Slot("pp", OperandKind.Predicate)),
        Vec(
          "BAR_Red_a_b",
          "BAR_scan_imm_a",
          "bar__RED_II_optionalCount_II",
          "bar__RED_dfrBlk_II_optionalCount_II",
          "bar__SCAN_II_II"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Immediate(sb),
              InstructionOperand.Immediate(sc),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.BarmioPipeImmImmPred(sb, sc, pp, modifiers, guard))
        case _ => None
      }
  }
  object BarmioPipeImmPred extends Definition(
        "BarmioPipeImmPred",
        "BARmio_pipe",
        Vec(Slot("sb", OperandKind.Immediate), Slot("pp", OperandKind.Predicate)),
        Vec("BAR_Red_a_b", "bar__RED_II_optionalCount_II", "bar__RED_dfrBlk_II_optionalCount_II")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb), InstructionOperand.Predicate(pp)) =>
          Some(Instruction.BarmioPipeImmPred(sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object BarmioPipeImmReg extends Definition(
        "BarmioPipeImmReg",
        "BARmio_pipe",
        Vec(Slot("sb", OperandKind.Immediate), Slot("srcC", OperandKind.Register)),
        Vec(
          "BAR_Arv_a",
          "BAR_Sync_a",
          "bar__ARV_IR_IR",
          "bar__SYNC_IR_optionalCount_IR",
          "bar__SYNC_dfrBlk_IR_optionalCount_IR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb), InstructionOperand.Register(srcC)) =>
          Some(Instruction.BarmioPipeImmReg(sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object BarmioPipeImmRegPred extends Definition(
        "BarmioPipeImmRegPred",
        "BARmio_pipe",
        Vec(Slot("sb", OperandKind.Immediate), Slot("srcC", OperandKind.Register), Slot("pp", OperandKind.Predicate)),
        Vec(
          "BAR_Red_a",
          "BAR_Scan_a",
          "bar__RED_IR_optionalCount_IR",
          "bar__RED_dfrBlk_IR_optionalCount_IR",
          "bar__SCAN_IR_IR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.BarmioPipeImmRegPred(sb, srcC, pp, modifiers, guard))
        case _ => None
      }
  }
  object BarmioPipeReg extends Definition(
        "BarmioPipeReg",
        "BARmio_pipe",
        Vec(Slot("srcA", OperandKind.Register)),
        Vec("BAR_Sync_b", "bar__SYNC_RI_optionalCount_RI", "bar__SYNC_dfrBlk_RI_optionalCount_RI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA)) => Some(Instruction.BarmioPipeReg(srcA, modifiers, guard))
        case _ => None
      }
  }
  object BarmioPipeRegImm extends Definition(
        "BarmioPipeRegImm",
        "BARmio_pipe",
        Vec(Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec(
          "BAR_Arv_imm",
          "BAR_Sync_b",
          "bar__ARV_RI_RI",
          "bar__SYNC_RI_optionalCount_RI",
          "bar__SYNC_dfrBlk_RI_optionalCount_RI"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA), InstructionOperand.Immediate(uImm)) =>
          Some(Instruction.BarmioPipeRegImm(srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object BarmioPipeRegImmPred extends Definition(
        "BarmioPipeRegImmPred",
        "BARmio_pipe",
        Vec(Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate), Slot("pa", OperandKind.Predicate)),
        Vec(
          "BAR_Red_b",
          "BAR_scan_imm",
          "bar__RED_RI_optionalCount_RI",
          "bar__RED_dfrBlk_RI_optionalCount_RI",
          "bar__SCAN_RI_RI"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.BarmioPipeRegImmPred(srcA, uImm, pa, modifiers, guard))
        case _ => None
      }
  }
  object BarmioPipeRegPred extends Definition(
        "BarmioPipeRegPred",
        "BARmio_pipe",
        Vec(Slot("srcA", OperandKind.Register), Slot("pa", OperandKind.Predicate)),
        Vec("BAR_Red_b", "bar__RED_RI_optionalCount_RI", "bar__RED_dfrBlk_RI_optionalCount_RI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA), InstructionOperand.Predicate(pa)) =>
          Some(Instruction.BarmioPipeRegPred(srcA, pa, modifiers, guard))
        case _ => None
      }
  }
  object BarmioPipeRegReg extends Definition(
        "BarmioPipeRegReg",
        "BARmio_pipe",
        Vec(Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("BAR_Arv", "BAR_Sync", "bar__ARV_RR_RR", "bar__SYNC_RR_RR", "bar__SYNC_dfrBlk_RR_RR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA), InstructionOperand.Register(srcB)) =>
          Some(Instruction.BarmioPipeRegReg(srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object BarmioPipeRegRegPred extends Definition(
        "BarmioPipeRegRegPred",
        "BARmio_pipe",
        Vec(Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register), Slot("pa", OperandKind.Predicate)),
        Vec("BAR_Red", "BAR_Scan", "bar__RED_RR_RR", "bar__RED_dfrBlk_RR_RR", "bar__SCAN_RR_RR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.BarmioPipeRegRegPred(srcA, srcB, pa, modifiers, guard))
        case _ => None
      }
  }
  object BfeRegRegConst extends Definition(
        "BfeRegRegConst",
        "BFE",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const BFE", "Const_BFE")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.BfeRegRegConst(dst, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object BfeRegRegImm extends Definition(
        "BfeRegRegImm",
        "BFE",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("Imm BFE", "Imm_BFE")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.BfeRegRegImm(dst, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object BfeRegRegReg extends Definition(
        "BfeRegRegReg",
        "BFE",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("BFE")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.BfeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object BfefxuPipeRegRegConst extends Definition(
        "BfefxuPipeRegRegConst",
        "BFEfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const_BFE")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.BfefxuPipeRegRegConst(dst, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object BfefxuPipeRegRegImm extends Definition(
        "BfefxuPipeRegRegImm",
        "BFEfxu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("Imm_BFE")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.BfefxuPipeRegRegImm(dst, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object BfefxuPipeRegRegReg extends Definition(
        "BfefxuPipeRegRegReg",
        "BFEfxu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("BFE")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.BfefxuPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object BfexluPipeRegRegConst extends Definition(
        "BfexluPipeRegRegConst",
        "BFExlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const BFE")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.BfexluPipeRegRegConst(dst, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object BfexluPipeRegRegImm extends Definition(
        "BfexluPipeRegRegImm",
        "BFExlu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("Imm BFE")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.BfexluPipeRegRegImm(dst, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object BfexluPipeRegRegReg extends Definition(
        "BfexluPipeRegRegReg",
        "BFExlu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("BFE")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.BfexluPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object BfiRegRegConstReg extends Definition(
        "BfiRegRegConstReg",
        "BFI",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Const BFI", "Const_BFI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.BfiRegRegConstReg(dst, srcA, srcConst, srcC, modifiers, guard))
        case _ => None
      }
  }
  object BfiRegRegImmReg extends Definition(
        "BfiRegRegImmReg",
        "BFI",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm BFI", "Imm_BFI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.BfiRegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object BfiRegRegRegConst extends Definition(
        "BfiRegRegRegConst",
        "BFI",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const1 BFI", "Const1_BFI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.BfiRegRegRegConst(dst, srcA, srcB, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object BfiRegRegRegReg extends Definition(
        "BfiRegRegRegReg",
        "BFI",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("BFI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.BfiRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object BfifxuPipeRegRegConstReg extends Definition(
        "BfifxuPipeRegRegConstReg",
        "BFIfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Const_BFI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.BfifxuPipeRegRegConstReg(dst, srcA, srcConst, srcC, modifiers, guard))
        case _ => None
      }
  }
  object BfifxuPipeRegRegImmReg extends Definition(
        "BfifxuPipeRegRegImmReg",
        "BFIfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm_BFI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.BfifxuPipeRegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object BfifxuPipeRegRegRegConst extends Definition(
        "BfifxuPipeRegRegRegConst",
        "BFIfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const1_BFI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.BfifxuPipeRegRegRegConst(dst, srcA, srcB, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object BfifxuPipeRegRegRegReg extends Definition(
        "BfifxuPipeRegRegRegReg",
        "BFIfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("BFI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.BfifxuPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object BfixluPipeRegRegConstReg extends Definition(
        "BfixluPipeRegRegConstReg",
        "BFIxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Const BFI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.BfixluPipeRegRegConstReg(dst, srcA, srcConst, srcC, modifiers, guard))
        case _ => None
      }
  }
  object BfixluPipeRegRegImmReg extends Definition(
        "BfixluPipeRegRegImmReg",
        "BFIxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm BFI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.BfixluPipeRegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object BfixluPipeRegRegRegConst extends Definition(
        "BfixluPipeRegRegRegConst",
        "BFIxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const1 BFI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.BfixluPipeRegRegRegConst(dst, srcA, srcB, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object BfixluPipeRegRegRegReg extends Definition(
        "BfixluPipeRegRegRegReg",
        "BFIxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("BFI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.BfixluPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object BgmmaRegAddressReg extends Definition(
        "BgmmaRegAddressReg",
        "BGMMA",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address), Slot("srcC", OperandKind.Register)),
        Vec("bgmma_URa_Rc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.BgmmaRegAddressReg(dst, srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object BgmmaRegAddressRegPred extends Definition(
        "BgmmaRegAddressRegPred",
        "BGMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("bgmma_URa_Rc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.BgmmaRegAddressRegPred(dst, srcA, srcC, uPp, modifiers, guard))
        case _ => None
      }
  }
  object BgmmaRegAddressRegReg extends Definition(
        "BgmmaRegAddressRegReg",
        "BGMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("bgmma_URa_Rb_Rc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.BgmmaRegAddressRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object BgmmaRegAddressRegRegPred extends Definition(
        "BgmmaRegAddressRegRegPred",
        "BGMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("bgmma_URa_Rb_Rc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.BgmmaRegAddressRegRegPred(dst, srcA, srcB, srcC, uPp, modifiers, guard))
        case _ => None
      }
  }
  object BgmmaRegRegAddressReg extends Definition(
        "BgmmaRegRegAddressReg",
        "BGMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("bgmma_Ra_URb_Rc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.BgmmaRegRegAddressReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object BgmmaRegRegAddressRegPred extends Definition(
        "BgmmaRegRegAddressRegPred",
        "BGMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("bgmma_Ra_URb_Rc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.BgmmaRegRegAddressRegPred(dst, srcA, srcB, srcC, uPp, modifiers, guard))
        case _ => None
      }
  }
  object BgmmamioPipeRegAddressReg extends Definition(
        "BgmmamioPipeRegAddressReg",
        "BGMMAmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address), Slot("srcC", OperandKind.Register)),
        Vec("bgmma_URa_Rc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.BgmmamioPipeRegAddressReg(dst, srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object BgmmamioPipeRegAddressRegPred extends Definition(
        "BgmmamioPipeRegAddressRegPred",
        "BGMMAmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("bgmma_URa_Rc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.BgmmamioPipeRegAddressRegPred(dst, srcA, srcC, uPp, modifiers, guard))
        case _ => None
      }
  }
  object BgmmamioPipeRegAddressRegReg extends Definition(
        "BgmmamioPipeRegAddressRegReg",
        "BGMMAmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("bgmma_URa_Rb_Rc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.BgmmamioPipeRegAddressRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object BgmmamioPipeRegAddressRegRegPred extends Definition(
        "BgmmamioPipeRegAddressRegRegPred",
        "BGMMAmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("bgmma_URa_Rb_Rc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.BgmmamioPipeRegAddressRegRegPred(dst, srcA, srcB, srcC, uPp, modifiers, guard))
        case _ => None
      }
  }
  object BgmmamioPipeRegRegAddressReg extends Definition(
        "BgmmamioPipeRegRegAddressReg",
        "BGMMAmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("bgmma_Ra_URb_Rc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.BgmmamioPipeRegRegAddressReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object BgmmamioPipeRegRegAddressRegPred extends Definition(
        "BgmmamioPipeRegRegAddressRegPred",
        "BGMMAmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("bgmma_Ra_URb_Rc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.BgmmamioPipeRegRegAddressRegPred(dst, srcA, srcB, srcC, uPp, modifiers, guard))
        case _ => None
      }
  }
  object BmmaRegRegRegReg extends Definition(
        "BmmaRegRegRegReg",
        "BMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("bmma_", "bmma_88128_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.BmmaRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object BmmaRegRegRegRegPred extends Definition(
        "BmmaRegRegRegRegPred",
        "BMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("bmma_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.BmmaRegRegRegRegPred(dst, srcA, srcB, srcC, uPp, modifiers, guard))
        case _ => None
      }
  }
  object BmmaintPipeRegRegRegReg extends Definition(
        "BmmaintPipeRegRegRegReg",
        "BMMAint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("bmma_", "bmma_88128_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.BmmaintPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object BmmaintPipeRegRegRegRegPred extends Definition(
        "BmmaintPipeRegRegRegRegPred",
        "BMMAint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("bmma_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.BmmaintPipeRegRegRegRegPred(dst, srcA, srcB, srcC, uPp, modifiers, guard))
        case _ => None
      }
  }
  object BmovAddressImm extends Definition(
        "BmovAddressImm",
        "BMOV",
        Vec(Slot("srcB", OperandKind.Address), Slot("sb_offset", OperandKind.Immediate)),
        Vec("bmov_dst64__CX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcB), InstructionOperand.Immediate(sb_offset)) =>
          Some(Instruction.BmovAddressImm(srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object BmovConst
      extends Definition("BmovConst", "BMOV", Vec(Slot("sb", OperandKind.ConstantBank)), Vec("bmov_dst64__C")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(sb)) => Some(Instruction.BmovConst(sb, modifiers, guard))
        case _ => None
      }
  }
  object BmovImm extends Definition("BmovImm", "BMOV", Vec(Slot("sb", OperandKind.Immediate)), Vec("bmov_dst64__I")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb)) => Some(Instruction.BmovImm(sb, modifiers, guard))
        case _ => None
      }
  }
  object BmovReg extends Definition(
        "BmovReg",
        "BMOV",
        Vec(Slot("srcB", OperandKind.Register)),
        Vec("bmov_clear_bd__Bd", "bmov_dst64__R", "bmov_dst64__UR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcB)) => Some(Instruction.BmovReg(srcB, modifiers, guard))
        case _ => None
      }
  }
  object BmovRegAddressImm extends Definition(
        "BmovRegAddressImm",
        "BMOV",
        Vec(
          Slot("cbu_state", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("bmov_pquad__RCXR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(cbu_state),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.BmovRegAddressImm(cbu_state, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object BmovRegConst extends Definition(
        "BmovRegConst",
        "BMOV",
        Vec(Slot("cbu_state", OperandKind.Register), Slot("sb", OperandKind.ConstantBank)),
        Vec("bmov_pquad__RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(cbu_state), InstructionOperand.ConstantBank(sb)) =>
          Some(Instruction.BmovRegConst(cbu_state, sb, modifiers, guard))
        case _ => None
      }
  }
  object BmovRegImm extends Definition(
        "BmovRegImm",
        "BMOV",
        Vec(Slot("cbu_state", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("bmov_pquad__RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(cbu_state), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.BmovRegImm(cbu_state, sb, modifiers, guard))
        case _ => None
      }
  }
  object BmovRegReg extends Definition(
        "BmovRegReg",
        "BMOV",
        Vec(Slot("dst", OperandKind.Register), Slot("cbu_state", OperandKind.Register)),
        Vec("bmov_clear__Rd", "bmov_clear_barrier_", "bmov_pquad__RRR", "bmov_pquad__RUR", "bmov_pquad_bar__RBR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(cbu_state)) =>
          Some(Instruction.BmovRegReg(dst, cbu_state, modifiers, guard))
        case _ => None
      }
  }
  object BmovRegSymbol extends Definition(
        "BmovRegSymbol",
        "BMOV",
        Vec(Slot("dst", OperandKind.Register), Slot("cbu_state", OperandKind.Symbol)),
        Vec("bmov_clear__Rd")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Symbol(cbu_state)) =>
          Some(Instruction.BmovRegSymbol(dst, cbu_state, modifiers, guard))
        case _ => None
      }
  }
  object BmovSymbolAddressImm extends Definition(
        "BmovSymbolAddressImm",
        "BMOV",
        Vec(
          Slot("cbu_state", OperandKind.Symbol),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("bmov_pquad__RCXR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Symbol(cbu_state),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.BmovSymbolAddressImm(cbu_state, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object BmovSymbolConst extends Definition(
        "BmovSymbolConst",
        "BMOV",
        Vec(Slot("cbu_state", OperandKind.Symbol), Slot("sb", OperandKind.ConstantBank)),
        Vec("bmov_pquad__RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Symbol(cbu_state), InstructionOperand.ConstantBank(sb)) =>
          Some(Instruction.BmovSymbolConst(cbu_state, sb, modifiers, guard))
        case _ => None
      }
  }
  object BmovSymbolImm extends Definition(
        "BmovSymbolImm",
        "BMOV",
        Vec(Slot("cbu_state", OperandKind.Symbol), Slot("sb", OperandKind.Immediate)),
        Vec("bmov_pquad__RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Symbol(cbu_state), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.BmovSymbolImm(cbu_state, sb, modifiers, guard))
        case _ => None
      }
  }
  object BmovSymbolReg extends Definition(
        "BmovSymbolReg",
        "BMOV",
        Vec(Slot("cbu_state", OperandKind.Symbol), Slot("srcB", OperandKind.Register)),
        Vec("bmov_pquad__RRR", "bmov_pquad__RUR", "bmov_pquad_bar__RBR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Symbol(cbu_state), InstructionOperand.Register(srcB)) =>
          Some(Instruction.BmovSymbolReg(cbu_state, srcB, modifiers, guard))
        case _ => None
      }
  }
  object BmovcbuPipeAddressImm extends Definition(
        "BmovcbuPipeAddressImm",
        "BMOVcbu_pipe",
        Vec(Slot("srcB", OperandKind.Address), Slot("sb_offset", OperandKind.Immediate)),
        Vec("bmov_dst64__CX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcB), InstructionOperand.Immediate(sb_offset)) =>
          Some(Instruction.BmovcbuPipeAddressImm(srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object BmovcbuPipeConst extends Definition(
        "BmovcbuPipeConst",
        "BMOVcbu_pipe",
        Vec(Slot("sb", OperandKind.ConstantBank)),
        Vec("bmov_dst64__C")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(sb)) => Some(Instruction.BmovcbuPipeConst(sb, modifiers, guard))
        case _ => None
      }
  }
  object BmovcbuPipeImm extends Definition(
        "BmovcbuPipeImm",
        "BMOVcbu_pipe",
        Vec(Slot("sb", OperandKind.Immediate)),
        Vec("bmov_dst64__I")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb)) => Some(Instruction.BmovcbuPipeImm(sb, modifiers, guard))
        case _ => None
      }
  }
  object BmovcbuPipeReg extends Definition(
        "BmovcbuPipeReg",
        "BMOVcbu_pipe",
        Vec(Slot("srcB", OperandKind.Register)),
        Vec("bmov_clear_bd__Bd", "bmov_dst64__R", "bmov_dst64__UR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcB)) => Some(Instruction.BmovcbuPipeReg(srcB, modifiers, guard))
        case _ => None
      }
  }
  object BmovcbuPipeRegAddressImm extends Definition(
        "BmovcbuPipeRegAddressImm",
        "BMOVcbu_pipe",
        Vec(
          Slot("cbu_state", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("bmov_pquad__RCXR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(cbu_state),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.BmovcbuPipeRegAddressImm(cbu_state, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object BmovcbuPipeRegConst extends Definition(
        "BmovcbuPipeRegConst",
        "BMOVcbu_pipe",
        Vec(Slot("cbu_state", OperandKind.Register), Slot("sb", OperandKind.ConstantBank)),
        Vec("bmov_pquad__RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(cbu_state), InstructionOperand.ConstantBank(sb)) =>
          Some(Instruction.BmovcbuPipeRegConst(cbu_state, sb, modifiers, guard))
        case _ => None
      }
  }
  object BmovcbuPipeRegImm extends Definition(
        "BmovcbuPipeRegImm",
        "BMOVcbu_pipe",
        Vec(Slot("cbu_state", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("bmov_pquad__RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(cbu_state), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.BmovcbuPipeRegImm(cbu_state, sb, modifiers, guard))
        case _ => None
      }
  }
  object BmovcbuPipeRegReg extends Definition(
        "BmovcbuPipeRegReg",
        "BMOVcbu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("cbu_state", OperandKind.Register)),
        Vec("bmov_clear__Rd", "bmov_clear_barrier_", "bmov_pquad__RRR", "bmov_pquad__RUR", "bmov_pquad_bar__RBR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(cbu_state)) =>
          Some(Instruction.BmovcbuPipeRegReg(dst, cbu_state, modifiers, guard))
        case _ => None
      }
  }
  object BmovcbuPipeRegSymbol extends Definition(
        "BmovcbuPipeRegSymbol",
        "BMOVcbu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("cbu_state", OperandKind.Symbol)),
        Vec("bmov_clear__Rd")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Symbol(cbu_state)) =>
          Some(Instruction.BmovcbuPipeRegSymbol(dst, cbu_state, modifiers, guard))
        case _ => None
      }
  }
  object BmovcbuPipeSymbolAddressImm extends Definition(
        "BmovcbuPipeSymbolAddressImm",
        "BMOVcbu_pipe",
        Vec(
          Slot("cbu_state", OperandKind.Symbol),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("bmov_pquad__RCXR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Symbol(cbu_state),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.BmovcbuPipeSymbolAddressImm(cbu_state, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object BmovcbuPipeSymbolConst extends Definition(
        "BmovcbuPipeSymbolConst",
        "BMOVcbu_pipe",
        Vec(Slot("cbu_state", OperandKind.Symbol), Slot("sb", OperandKind.ConstantBank)),
        Vec("bmov_pquad__RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Symbol(cbu_state), InstructionOperand.ConstantBank(sb)) =>
          Some(Instruction.BmovcbuPipeSymbolConst(cbu_state, sb, modifiers, guard))
        case _ => None
      }
  }
  object BmovcbuPipeSymbolImm extends Definition(
        "BmovcbuPipeSymbolImm",
        "BMOVcbu_pipe",
        Vec(Slot("cbu_state", OperandKind.Symbol), Slot("sb", OperandKind.Immediate)),
        Vec("bmov_pquad__RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Symbol(cbu_state), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.BmovcbuPipeSymbolImm(cbu_state, sb, modifiers, guard))
        case _ => None
      }
  }
  object BmovcbuPipeSymbolReg extends Definition(
        "BmovcbuPipeSymbolReg",
        "BMOVcbu_pipe",
        Vec(Slot("cbu_state", OperandKind.Symbol), Slot("srcB", OperandKind.Register)),
        Vec("bmov_pquad__RRR", "bmov_pquad__RUR", "bmov_pquad_bar__RBR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Symbol(cbu_state), InstructionOperand.Register(srcB)) =>
          Some(Instruction.BmovcbuPipeSymbolReg(cbu_state, srcB, modifiers, guard))
        case _ => None
      }
  }
  object BmskRegRegAddressImm extends Definition(
        "BmskRegRegAddressImm",
        "BMSK",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("bmsk__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.BmskRegRegAddressImm(dst, srcA, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object BmskRegRegConst extends Definition(
        "BmskRegRegConst",
        "BMSK",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank)
        ),
        Vec("bmsk__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb)
            ) => Some(Instruction.BmskRegRegConst(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object BmskRegRegImm extends Definition(
        "BmskRegRegImm",
        "BMSK",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("bmsk__RuIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.BmskRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object BmskRegRegReg extends Definition(
        "BmskRegRegReg",
        "BMSK",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("bmsk__RRR_RRR", "bmsk__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.BmskRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object BmskintPipeRegRegAddressImm extends Definition(
        "BmskintPipeRegRegAddressImm",
        "BMSKint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("bmsk__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.BmskintPipeRegRegAddressImm(dst, srcA, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object BmskintPipeRegRegConst extends Definition(
        "BmskintPipeRegRegConst",
        "BMSKint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank)
        ),
        Vec("bmsk__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb)
            ) => Some(Instruction.BmskintPipeRegRegConst(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object BmskintPipeRegRegImm extends Definition(
        "BmskintPipeRegRegImm",
        "BMSKint_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("bmsk__RuIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.BmskintPipeRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object BmskintPipeRegRegReg extends Definition(
        "BmskintPipeRegRegReg",
        "BMSKint_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("bmsk__RRR_RRR", "bmsk__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.BmskintPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object BptNoOperands extends Definition(
        "BptNoOperands",
        "BPT",
        Vec(),
        Vec("BPT", "bpt__WAIT", "bpt__noDRAIN", "bpt__noWAIT", "bpt__onlyDRAIN")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.BptNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object BptImm extends Definition(
        "BptImm",
        "BPT",
        Vec(Slot("sb", OperandKind.Immediate)),
        Vec("BPT", "bpt__WAIT", "bpt__noDRAIN", "bpt__noWAIT", "bpt__onlyDRAIN")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb)) => Some(Instruction.BptImm(sb, modifiers, guard))
        case _ => None
      }
  }
  object BptbruPipeNoOperands extends Definition("BptbruPipeNoOperands", "BPTbru_pipe", Vec(), Vec("BPT")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.BptbruPipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object BptbruPipeImm
      extends Definition("BptbruPipeImm", "BPTbru_pipe", Vec(Slot("sImm", OperandKind.Immediate)), Vec("BPT")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sImm)) => Some(Instruction.BptbruPipeImm(sImm, modifiers, guard))
        case _ => None
      }
  }
  object BptcbuPipeNoOperands extends Definition(
        "BptcbuPipeNoOperands",
        "BPTcbu_pipe",
        Vec(),
        Vec("bpt__WAIT", "bpt__noDRAIN", "bpt__noWAIT", "bpt__onlyDRAIN")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.BptcbuPipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object BptcbuPipeImm extends Definition(
        "BptcbuPipeImm",
        "BPTcbu_pipe",
        Vec(Slot("sb", OperandKind.Immediate)),
        Vec("bpt__WAIT", "bpt__noDRAIN", "bpt__noWAIT", "bpt__onlyDRAIN")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb)) => Some(Instruction.BptcbuPipeImm(sb, modifiers, guard))
        case _ => None
      }
  }
  object BraConst extends Definition("BraConst", "BRA", Vec(Slot("srcConst", OperandKind.ConstantBank)), Vec("BRA_c")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(srcConst)) => Some(Instruction.BraConst(srcConst, modifiers, guard))
        case _ => None
      }
  }
  object BraImm extends Definition(
        "BraImm",
        "BRA",
        Vec(Slot("sImm", OperandKind.Immediate)),
        Vec("BRA", "bra_", "bra__CONV_DIV", "bra__U", "bra_rel_", "bra_rel__CONV_DIV", "bra_rel__U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sImm)) => Some(Instruction.BraImm(sImm, modifiers, guard))
        case _ => None
      }
  }
  object BraPredImm extends Definition(
        "BraPredImm",
        "BRA",
        Vec(Slot("pp", OperandKind.Predicate), Slot("sImm", OperandKind.Immediate)),
        Vec(
          "bra_",
          "bra__CONV_DIV",
          "bra__U",
          "bra_rel_",
          "bra_rel__CONV_DIV",
          "bra_rel__U",
          "bra_uniform_pred_",
          "bra_uniform_pred_rel_"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.Immediate(sImm)) =>
          Some(Instruction.BraPredImm(pp, sImm, modifiers, guard))
        case _ => None
      }
  }
  object BraPredPredImm extends Definition(
        "BraPredPredImm",
        "BRA",
        Vec(Slot("pp", OperandKind.Predicate), Slot("uPq", OperandKind.Predicate), Slot("sImm", OperandKind.Immediate)),
        Vec("bra_uniform_pred_", "bra_uniform_pred_rel_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pp),
              InstructionOperand.Predicate(uPq),
              InstructionOperand.Immediate(sImm)
            ) => Some(Instruction.BraPredPredImm(pp, uPq, sImm, modifiers, guard))
        case _ => None
      }
  }
  object BraPredRegImm extends Definition(
        "BraPredRegImm",
        "BRA",
        Vec(Slot("pp", OperandKind.Predicate), Slot("srcB", OperandKind.Register), Slot("sImm", OperandKind.Immediate)),
        Vec("bra_uniform_", "bra_uniform_rel_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pp),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sImm)
            ) => Some(Instruction.BraPredRegImm(pp, srcB, sImm, modifiers, guard))
        case _ => None
      }
  }
  object BraRegImm extends Definition(
        "BraRegImm",
        "BRA",
        Vec(Slot("srcB", OperandKind.Register), Slot("sImm", OperandKind.Immediate)),
        Vec("bra_uniform_", "bra_uniform_rel_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcB), InstructionOperand.Immediate(sImm)) =>
          Some(Instruction.BraRegImm(srcB, sImm, modifiers, guard))
        case _ => None
      }
  }
  object BraSymbolConst extends Definition(
        "BraSymbolConst",
        "BRA",
        Vec(Slot("testCC", OperandKind.Symbol), Slot("srcConst", OperandKind.ConstantBank)),
        Vec("BRA_c")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Symbol(testCC), InstructionOperand.ConstantBank(srcConst)) =>
          Some(Instruction.BraSymbolConst(testCC, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object BraSymbolImm extends Definition(
        "BraSymbolImm",
        "BRA",
        Vec(Slot("testCC", OperandKind.Symbol), Slot("sImm", OperandKind.Immediate)),
        Vec("BRA")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Symbol(testCC), InstructionOperand.Immediate(sImm)) =>
          Some(Instruction.BraSymbolImm(testCC, sImm, modifiers, guard))
        case _ => None
      }
  }
  object BraSymbolImmImm extends Definition(
        "BraSymbolImmImm",
        "BRA",
        Vec(
          Slot("srcConst", OperandKind.Symbol),
          Slot("constBank", OperandKind.Immediate),
          Slot("immConstOffset", OperandKind.Immediate)
        ),
        Vec("BRA c")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Symbol(srcConst),
              InstructionOperand.Immediate(constBank),
              InstructionOperand.Immediate(immConstOffset)
            ) => Some(Instruction.BraSymbolImmImm(srcConst, constBank, immConstOffset, modifiers, guard))
        case _ => None
      }
  }
  object BraSymbolSymbolImmImm extends Definition(
        "BraSymbolSymbolImmImm",
        "BRA",
        Vec(
          Slot("testCC", OperandKind.Symbol),
          Slot("srcConst", OperandKind.Symbol),
          Slot("constBank", OperandKind.Immediate),
          Slot("immConstOffset", OperandKind.Immediate)
        ),
        Vec("BRA c")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Symbol(testCC),
              InstructionOperand.Symbol(srcConst),
              InstructionOperand.Immediate(constBank),
              InstructionOperand.Immediate(immConstOffset)
            ) => Some(Instruction.BraSymbolSymbolImmImm(testCC, srcConst, constBank, immConstOffset, modifiers, guard))
        case _ => None
      }
  }
  object BrabruPipeConst extends Definition(
        "BrabruPipeConst",
        "BRAbru_pipe",
        Vec(Slot("srcConst", OperandKind.ConstantBank)),
        Vec("BRA_c")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(srcConst)) =>
          Some(Instruction.BrabruPipeConst(srcConst, modifiers, guard))
        case _ => None
      }
  }
  object BrabruPipeImm
      extends Definition("BrabruPipeImm", "BRAbru_pipe", Vec(Slot("sImm", OperandKind.Immediate)), Vec("BRA")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sImm)) => Some(Instruction.BrabruPipeImm(sImm, modifiers, guard))
        case _ => None
      }
  }
  object BrabruPipeSymbolConst extends Definition(
        "BrabruPipeSymbolConst",
        "BRAbru_pipe",
        Vec(Slot("testCC", OperandKind.Symbol), Slot("srcConst", OperandKind.ConstantBank)),
        Vec("BRA_c")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Symbol(testCC), InstructionOperand.ConstantBank(srcConst)) =>
          Some(Instruction.BrabruPipeSymbolConst(testCC, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object BrabruPipeSymbolImm extends Definition(
        "BrabruPipeSymbolImm",
        "BRAbru_pipe",
        Vec(Slot("testCC", OperandKind.Symbol), Slot("sImm", OperandKind.Immediate)),
        Vec("BRA")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Symbol(testCC), InstructionOperand.Immediate(sImm)) =>
          Some(Instruction.BrabruPipeSymbolImm(testCC, sImm, modifiers, guard))
        case _ => None
      }
  }
  object BrabruPipeSymbolImmImm extends Definition(
        "BrabruPipeSymbolImmImm",
        "BRAbru_pipe",
        Vec(
          Slot("srcConst", OperandKind.Symbol),
          Slot("constBank", OperandKind.Immediate),
          Slot("immConstOffset", OperandKind.Immediate)
        ),
        Vec("BRA c")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Symbol(srcConst),
              InstructionOperand.Immediate(constBank),
              InstructionOperand.Immediate(immConstOffset)
            ) => Some(Instruction.BrabruPipeSymbolImmImm(srcConst, constBank, immConstOffset, modifiers, guard))
        case _ => None
      }
  }
  object BrabruPipeSymbolSymbolImmImm extends Definition(
        "BrabruPipeSymbolSymbolImmImm",
        "BRAbru_pipe",
        Vec(
          Slot("testCC", OperandKind.Symbol),
          Slot("srcConst", OperandKind.Symbol),
          Slot("constBank", OperandKind.Immediate),
          Slot("immConstOffset", OperandKind.Immediate)
        ),
        Vec("BRA c")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Symbol(testCC),
              InstructionOperand.Symbol(srcConst),
              InstructionOperand.Immediate(constBank),
              InstructionOperand.Immediate(immConstOffset)
            ) =>
          Some(Instruction.BrabruPipeSymbolSymbolImmImm(testCC, srcConst, constBank, immConstOffset, modifiers, guard))
        case _ => None
      }
  }
  object BracbuPipeImm extends Definition(
        "BracbuPipeImm",
        "BRAcbu_pipe",
        Vec(Slot("sImm", OperandKind.Immediate)),
        Vec("bra_", "bra__CONV_DIV", "bra__U", "bra_rel_", "bra_rel__CONV_DIV", "bra_rel__U")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sImm)) => Some(Instruction.BracbuPipeImm(sImm, modifiers, guard))
        case _ => None
      }
  }
  object BracbuPipePredImm extends Definition(
        "BracbuPipePredImm",
        "BRAcbu_pipe",
        Vec(Slot("pp", OperandKind.Predicate), Slot("sImm", OperandKind.Immediate)),
        Vec(
          "bra_",
          "bra__CONV_DIV",
          "bra__U",
          "bra_rel_",
          "bra_rel__CONV_DIV",
          "bra_rel__U",
          "bra_uniform_pred_",
          "bra_uniform_pred_rel_"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.Immediate(sImm)) =>
          Some(Instruction.BracbuPipePredImm(pp, sImm, modifiers, guard))
        case _ => None
      }
  }
  object BracbuPipePredPredImm extends Definition(
        "BracbuPipePredPredImm",
        "BRAcbu_pipe",
        Vec(Slot("pp", OperandKind.Predicate), Slot("uPq", OperandKind.Predicate), Slot("sImm", OperandKind.Immediate)),
        Vec("bra_uniform_pred_", "bra_uniform_pred_rel_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pp),
              InstructionOperand.Predicate(uPq),
              InstructionOperand.Immediate(sImm)
            ) => Some(Instruction.BracbuPipePredPredImm(pp, uPq, sImm, modifiers, guard))
        case _ => None
      }
  }
  object BracbuPipePredRegImm extends Definition(
        "BracbuPipePredRegImm",
        "BRAcbu_pipe",
        Vec(Slot("pp", OperandKind.Predicate), Slot("srcB", OperandKind.Register), Slot("sImm", OperandKind.Immediate)),
        Vec("bra_uniform_", "bra_uniform_rel_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pp),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sImm)
            ) => Some(Instruction.BracbuPipePredRegImm(pp, srcB, sImm, modifiers, guard))
        case _ => None
      }
  }
  object BracbuPipeRegImm extends Definition(
        "BracbuPipeRegImm",
        "BRAcbu_pipe",
        Vec(Slot("srcB", OperandKind.Register), Slot("sImm", OperandKind.Immediate)),
        Vec("bra_uniform_", "bra_uniform_rel_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcB), InstructionOperand.Immediate(sImm)) =>
          Some(Instruction.BracbuPipeRegImm(srcB, sImm, modifiers, guard))
        case _ => None
      }
  }
  object BreakPredReg extends Definition(
        "BreakPredReg",
        "BREAK",
        Vec(Slot("pp", OperandKind.Predicate), Slot("barReg", OperandKind.Register)),
        Vec("break_inst_", "break_reliability_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.Register(barReg)) =>
          Some(Instruction.BreakPredReg(pp, barReg, modifiers, guard))
        case _ => None
      }
  }
  object BreakReg extends Definition(
        "BreakReg",
        "BREAK",
        Vec(Slot("barReg", OperandKind.Register)),
        Vec("break_inst_", "break_reliability_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(barReg)) => Some(Instruction.BreakReg(barReg, modifiers, guard))
        case _ => None
      }
  }
  object BreakcbuPipePredReg extends Definition(
        "BreakcbuPipePredReg",
        "BREAKcbu_pipe",
        Vec(Slot("pp", OperandKind.Predicate), Slot("barReg", OperandKind.Register)),
        Vec("break_inst_", "break_reliability_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.Register(barReg)) =>
          Some(Instruction.BreakcbuPipePredReg(pp, barReg, modifiers, guard))
        case _ => None
      }
  }
  object BreakcbuPipeReg extends Definition(
        "BreakcbuPipeReg",
        "BREAKcbu_pipe",
        Vec(Slot("barReg", OperandKind.Register)),
        Vec("break_inst_", "break_reliability_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(barReg)) => Some(Instruction.BreakcbuPipeReg(barReg, modifiers, guard))
        case _ => None
      }
  }
  object BrevRegAddressImm extends Definition(
        "BrevRegAddressImm",
        "BREV",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("brev__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.BrevRegAddressImm(dst, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object BrevRegConst extends Definition(
        "BrevRegConst",
        "BREV",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.ConstantBank)),
        Vec("brev__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(sb)) =>
          Some(Instruction.BrevRegConst(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object BrevRegImm extends Definition(
        "BrevRegImm",
        "BREV",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("brev__RuIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.BrevRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object BrevRegReg extends Definition(
        "BrevRegReg",
        "BREV",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("brev__RRR_RRR", "brev__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.BrevRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object BrevmioPipeRegAddressImm extends Definition(
        "BrevmioPipeRegAddressImm",
        "BREVmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("brev__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.BrevmioPipeRegAddressImm(dst, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object BrevmioPipeRegConst extends Definition(
        "BrevmioPipeRegConst",
        "BREVmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.ConstantBank)),
        Vec("brev__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(sb)) =>
          Some(Instruction.BrevmioPipeRegConst(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object BrevmioPipeRegImm extends Definition(
        "BrevmioPipeRegImm",
        "BREVmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("brev__RuIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.BrevmioPipeRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object BrevmioPipeRegReg extends Definition(
        "BrevmioPipeRegReg",
        "BREVmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("brev__RRR_RRR", "brev__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.BrevmioPipeRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object BrkNoOperands extends Definition("BrkNoOperands", "BRK", Vec(), Vec("BRK")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.BrkNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object BrkbruPipeNoOperands extends Definition("BrkbruPipeNoOperands", "BRKbru_pipe", Vec(), Vec("BRK")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.BrkbruPipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object BrxConst extends Definition("BrxConst", "BRX", Vec(Slot("srcConst", OperandKind.ConstantBank)), Vec("BRX_c")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(srcConst)) => Some(Instruction.BrxConst(srcConst, modifiers, guard))
        case _ => None
      }
  }
  object BrxPredReg extends Definition(
        "BrxPredReg",
        "BRX",
        Vec(Slot("pp", OperandKind.Predicate), Slot("srcA", OperandKind.Register)),
        Vec("brx_", "brx_rel_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.Register(srcA)) =>
          Some(Instruction.BrxPredReg(pp, srcA, modifiers, guard))
        case _ => None
      }
  }
  object BrxPredRegImm extends Definition(
        "BrxPredRegImm",
        "BRX",
        Vec(
          Slot("pp", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("ra_offset", OperandKind.Immediate)
        ),
        Vec("brx_", "brx_rel_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pp),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(ra_offset)
            ) => Some(Instruction.BrxPredRegImm(pp, srcA, ra_offset, modifiers, guard))
        case _ => None
      }
  }
  object BrxReg
      extends Definition("BrxReg", "BRX", Vec(Slot("srcA", OperandKind.Register)), Vec("BRX", "brx_", "brx_rel_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA)) => Some(Instruction.BrxReg(srcA, modifiers, guard))
        case _ => None
      }
  }
  object BrxRegImm extends Definition(
        "BrxRegImm",
        "BRX",
        Vec(Slot("srcA", OperandKind.Register), Slot("ra_offset", OperandKind.Immediate)),
        Vec("BRX", "brx_", "brx_rel_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA), InstructionOperand.Immediate(ra_offset)) =>
          Some(Instruction.BrxRegImm(srcA, ra_offset, modifiers, guard))
        case _ => None
      }
  }
  object BrxSymbolConst extends Definition(
        "BrxSymbolConst",
        "BRX",
        Vec(Slot("testCC", OperandKind.Symbol), Slot("srcConst", OperandKind.ConstantBank)),
        Vec("BRX_c")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Symbol(testCC), InstructionOperand.ConstantBank(srcConst)) =>
          Some(Instruction.BrxSymbolConst(testCC, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object BrxSymbolImmAddress extends Definition(
        "BrxSymbolImmAddress",
        "BRX",
        Vec(
          Slot("srcConst", OperandKind.Symbol),
          Slot("constBank", OperandKind.Immediate),
          Slot("srcA", OperandKind.Address)
        ),
        Vec("BRX c")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Symbol(srcConst),
              InstructionOperand.Immediate(constBank),
              InstructionOperand.Address(srcA)
            ) => Some(Instruction.BrxSymbolImmAddress(srcConst, constBank, srcA, modifiers, guard))
        case _ => None
      }
  }
  object BrxSymbolReg extends Definition(
        "BrxSymbolReg",
        "BRX",
        Vec(Slot("testCC", OperandKind.Symbol), Slot("srcA", OperandKind.Register)),
        Vec("BRX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Symbol(testCC), InstructionOperand.Register(srcA)) =>
          Some(Instruction.BrxSymbolReg(testCC, srcA, modifiers, guard))
        case _ => None
      }
  }
  object BrxSymbolRegImm extends Definition(
        "BrxSymbolRegImm",
        "BRX",
        Vec(
          Slot("testCC", OperandKind.Symbol),
          Slot("srcA", OperandKind.Register),
          Slot("sImm", OperandKind.Immediate)
        ),
        Vec("BRX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Symbol(testCC),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sImm)
            ) => Some(Instruction.BrxSymbolRegImm(testCC, srcA, sImm, modifiers, guard))
        case _ => None
      }
  }
  object BrxSymbolSymbolImmAddress extends Definition(
        "BrxSymbolSymbolImmAddress",
        "BRX",
        Vec(
          Slot("testCC", OperandKind.Symbol),
          Slot("srcConst", OperandKind.Symbol),
          Slot("constBank", OperandKind.Immediate),
          Slot("srcA", OperandKind.Address)
        ),
        Vec("BRX c")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Symbol(testCC),
              InstructionOperand.Symbol(srcConst),
              InstructionOperand.Immediate(constBank),
              InstructionOperand.Address(srcA)
            ) => Some(Instruction.BrxSymbolSymbolImmAddress(testCC, srcConst, constBank, srcA, modifiers, guard))
        case _ => None
      }
  }
  object BrxuPredReg extends Definition(
        "BrxuPredReg",
        "BRXU",
        Vec(Slot("pp", OperandKind.Predicate), Slot("srcA", OperandKind.Register)),
        Vec("brxu_", "brxu_rel_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.Register(srcA)) =>
          Some(Instruction.BrxuPredReg(pp, srcA, modifiers, guard))
        case _ => None
      }
  }
  object BrxuPredRegImm extends Definition(
        "BrxuPredRegImm",
        "BRXU",
        Vec(
          Slot("pp", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("uR_offset", OperandKind.Immediate)
        ),
        Vec("brxu_", "brxu_rel_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pp),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uR_offset)
            ) => Some(Instruction.BrxuPredRegImm(pp, srcA, uR_offset, modifiers, guard))
        case _ => None
      }
  }
  object BrxuReg
      extends Definition("BrxuReg", "BRXU", Vec(Slot("srcA", OperandKind.Register)), Vec("brxu_", "brxu_rel_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA)) => Some(Instruction.BrxuReg(srcA, modifiers, guard))
        case _ => None
      }
  }
  object BrxuRegImm extends Definition(
        "BrxuRegImm",
        "BRXU",
        Vec(Slot("srcA", OperandKind.Register), Slot("uR_offset", OperandKind.Immediate)),
        Vec("brxu_", "brxu_rel_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA), InstructionOperand.Immediate(uR_offset)) =>
          Some(Instruction.BrxuRegImm(srcA, uR_offset, modifiers, guard))
        case _ => None
      }
  }
  object BrxucbuPipePredReg extends Definition(
        "BrxucbuPipePredReg",
        "BRXUcbu_pipe",
        Vec(Slot("pp", OperandKind.Predicate), Slot("srcA", OperandKind.Register)),
        Vec("brxu_", "brxu_rel_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.Register(srcA)) =>
          Some(Instruction.BrxucbuPipePredReg(pp, srcA, modifiers, guard))
        case _ => None
      }
  }
  object BrxucbuPipePredRegImm extends Definition(
        "BrxucbuPipePredRegImm",
        "BRXUcbu_pipe",
        Vec(
          Slot("pp", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("uR_offset", OperandKind.Immediate)
        ),
        Vec("brxu_", "brxu_rel_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pp),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uR_offset)
            ) => Some(Instruction.BrxucbuPipePredRegImm(pp, srcA, uR_offset, modifiers, guard))
        case _ => None
      }
  }
  object BrxucbuPipeReg extends Definition(
        "BrxucbuPipeReg",
        "BRXUcbu_pipe",
        Vec(Slot("srcA", OperandKind.Register)),
        Vec("brxu_", "brxu_rel_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA)) => Some(Instruction.BrxucbuPipeReg(srcA, modifiers, guard))
        case _ => None
      }
  }
  object BrxucbuPipeRegImm extends Definition(
        "BrxucbuPipeRegImm",
        "BRXUcbu_pipe",
        Vec(Slot("srcA", OperandKind.Register), Slot("uR_offset", OperandKind.Immediate)),
        Vec("brxu_", "brxu_rel_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA), InstructionOperand.Immediate(uR_offset)) =>
          Some(Instruction.BrxucbuPipeRegImm(srcA, uR_offset, modifiers, guard))
        case _ => None
      }
  }
  object BrxaduPipeReg
      extends Definition("BrxaduPipeReg", "BRXadu_pipe", Vec(Slot("srcA", OperandKind.Register)), Vec("BRX")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA)) => Some(Instruction.BrxaduPipeReg(srcA, modifiers, guard))
        case _ => None
      }
  }
  object BrxaduPipeRegImm extends Definition(
        "BrxaduPipeRegImm",
        "BRXadu_pipe",
        Vec(Slot("srcA", OperandKind.Register), Slot("sImm", OperandKind.Immediate)),
        Vec("BRX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA), InstructionOperand.Immediate(sImm)) =>
          Some(Instruction.BrxaduPipeRegImm(srcA, sImm, modifiers, guard))
        case _ => None
      }
  }
  object BrxaduPipeSymbolImmAddress extends Definition(
        "BrxaduPipeSymbolImmAddress",
        "BRXadu_pipe",
        Vec(
          Slot("srcConst", OperandKind.Symbol),
          Slot("constBank", OperandKind.Immediate),
          Slot("srcA", OperandKind.Address)
        ),
        Vec("BRX c")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Symbol(srcConst),
              InstructionOperand.Immediate(constBank),
              InstructionOperand.Address(srcA)
            ) => Some(Instruction.BrxaduPipeSymbolImmAddress(srcConst, constBank, srcA, modifiers, guard))
        case _ => None
      }
  }
  object BrxaduPipeSymbolReg extends Definition(
        "BrxaduPipeSymbolReg",
        "BRXadu_pipe",
        Vec(Slot("testCC", OperandKind.Symbol), Slot("srcA", OperandKind.Register)),
        Vec("BRX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Symbol(testCC), InstructionOperand.Register(srcA)) =>
          Some(Instruction.BrxaduPipeSymbolReg(testCC, srcA, modifiers, guard))
        case _ => None
      }
  }
  object BrxaduPipeSymbolRegImm extends Definition(
        "BrxaduPipeSymbolRegImm",
        "BRXadu_pipe",
        Vec(
          Slot("testCC", OperandKind.Symbol),
          Slot("srcA", OperandKind.Register),
          Slot("sImm", OperandKind.Immediate)
        ),
        Vec("BRX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Symbol(testCC),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sImm)
            ) => Some(Instruction.BrxaduPipeSymbolRegImm(testCC, srcA, sImm, modifiers, guard))
        case _ => None
      }
  }
  object BrxaduPipeSymbolSymbolImmAddress extends Definition(
        "BrxaduPipeSymbolSymbolImmAddress",
        "BRXadu_pipe",
        Vec(
          Slot("testCC", OperandKind.Symbol),
          Slot("srcConst", OperandKind.Symbol),
          Slot("constBank", OperandKind.Immediate),
          Slot("srcA", OperandKind.Address)
        ),
        Vec("BRX c")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Symbol(testCC),
              InstructionOperand.Symbol(srcConst),
              InstructionOperand.Immediate(constBank),
              InstructionOperand.Address(srcA)
            ) => Some(Instruction.BrxaduPipeSymbolSymbolImmAddress(testCC, srcConst, constBank, srcA, modifiers, guard))
        case _ => None
      }
  }
  object BrxbruPipeConst extends Definition(
        "BrxbruPipeConst",
        "BRXbru_pipe",
        Vec(Slot("srcConst", OperandKind.ConstantBank)),
        Vec("BRX_c")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(srcConst)) =>
          Some(Instruction.BrxbruPipeConst(srcConst, modifiers, guard))
        case _ => None
      }
  }
  object BrxbruPipeReg
      extends Definition("BrxbruPipeReg", "BRXbru_pipe", Vec(Slot("srcA", OperandKind.Register)), Vec("BRX")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA)) => Some(Instruction.BrxbruPipeReg(srcA, modifiers, guard))
        case _ => None
      }
  }
  object BrxbruPipeRegImm extends Definition(
        "BrxbruPipeRegImm",
        "BRXbru_pipe",
        Vec(Slot("srcA", OperandKind.Register), Slot("sImm", OperandKind.Immediate)),
        Vec("BRX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA), InstructionOperand.Immediate(sImm)) =>
          Some(Instruction.BrxbruPipeRegImm(srcA, sImm, modifiers, guard))
        case _ => None
      }
  }
  object BrxbruPipeSymbolConst extends Definition(
        "BrxbruPipeSymbolConst",
        "BRXbru_pipe",
        Vec(Slot("testCC", OperandKind.Symbol), Slot("srcConst", OperandKind.ConstantBank)),
        Vec("BRX_c")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Symbol(testCC), InstructionOperand.ConstantBank(srcConst)) =>
          Some(Instruction.BrxbruPipeSymbolConst(testCC, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object BrxbruPipeSymbolImmAddress extends Definition(
        "BrxbruPipeSymbolImmAddress",
        "BRXbru_pipe",
        Vec(
          Slot("srcConst", OperandKind.Symbol),
          Slot("constBank", OperandKind.Immediate),
          Slot("srcA", OperandKind.Address)
        ),
        Vec("BRX c")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Symbol(srcConst),
              InstructionOperand.Immediate(constBank),
              InstructionOperand.Address(srcA)
            ) => Some(Instruction.BrxbruPipeSymbolImmAddress(srcConst, constBank, srcA, modifiers, guard))
        case _ => None
      }
  }
  object BrxbruPipeSymbolReg extends Definition(
        "BrxbruPipeSymbolReg",
        "BRXbru_pipe",
        Vec(Slot("testCC", OperandKind.Symbol), Slot("srcA", OperandKind.Register)),
        Vec("BRX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Symbol(testCC), InstructionOperand.Register(srcA)) =>
          Some(Instruction.BrxbruPipeSymbolReg(testCC, srcA, modifiers, guard))
        case _ => None
      }
  }
  object BrxbruPipeSymbolRegImm extends Definition(
        "BrxbruPipeSymbolRegImm",
        "BRXbru_pipe",
        Vec(
          Slot("testCC", OperandKind.Symbol),
          Slot("srcA", OperandKind.Register),
          Slot("sImm", OperandKind.Immediate)
        ),
        Vec("BRX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Symbol(testCC),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sImm)
            ) => Some(Instruction.BrxbruPipeSymbolRegImm(testCC, srcA, sImm, modifiers, guard))
        case _ => None
      }
  }
  object BrxbruPipeSymbolSymbolImmAddress extends Definition(
        "BrxbruPipeSymbolSymbolImmAddress",
        "BRXbru_pipe",
        Vec(
          Slot("testCC", OperandKind.Symbol),
          Slot("srcConst", OperandKind.Symbol),
          Slot("constBank", OperandKind.Immediate),
          Slot("srcA", OperandKind.Address)
        ),
        Vec("BRX c")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Symbol(testCC),
              InstructionOperand.Symbol(srcConst),
              InstructionOperand.Immediate(constBank),
              InstructionOperand.Address(srcA)
            ) => Some(Instruction.BrxbruPipeSymbolSymbolImmAddress(testCC, srcConst, constBank, srcA, modifiers, guard))
        case _ => None
      }
  }
  object BrxcbuPipePredReg extends Definition(
        "BrxcbuPipePredReg",
        "BRXcbu_pipe",
        Vec(Slot("pp", OperandKind.Predicate), Slot("srcA", OperandKind.Register)),
        Vec("brx_", "brx_rel_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.Register(srcA)) =>
          Some(Instruction.BrxcbuPipePredReg(pp, srcA, modifiers, guard))
        case _ => None
      }
  }
  object BrxcbuPipePredRegImm extends Definition(
        "BrxcbuPipePredRegImm",
        "BRXcbu_pipe",
        Vec(
          Slot("pp", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("ra_offset", OperandKind.Immediate)
        ),
        Vec("brx_", "brx_rel_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pp),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(ra_offset)
            ) => Some(Instruction.BrxcbuPipePredRegImm(pp, srcA, ra_offset, modifiers, guard))
        case _ => None
      }
  }
  object BrxcbuPipeReg extends Definition(
        "BrxcbuPipeReg",
        "BRXcbu_pipe",
        Vec(Slot("srcA", OperandKind.Register)),
        Vec("brx_", "brx_rel_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA)) => Some(Instruction.BrxcbuPipeReg(srcA, modifiers, guard))
        case _ => None
      }
  }
  object BrxcbuPipeRegImm extends Definition(
        "BrxcbuPipeRegImm",
        "BRXcbu_pipe",
        Vec(Slot("srcA", OperandKind.Register), Slot("ra_offset", OperandKind.Immediate)),
        Vec("brx_", "brx_rel_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA), InstructionOperand.Immediate(ra_offset)) =>
          Some(Instruction.BrxcbuPipeRegImm(srcA, ra_offset, modifiers, guard))
        case _ => None
      }
  }
  object BssyPredRegImm extends Definition(
        "BssyPredRegImm",
        "BSSY",
        Vec(Slot("pp", OperandKind.Predicate), Slot("barReg", OperandKind.Register), Slot("sa", OperandKind.Immediate)),
        Vec("bssy_", "bssy_rel_", "bssy_reliability_", "bssy_reliability_rel_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pp),
              InstructionOperand.Register(barReg),
              InstructionOperand.Immediate(sa)
            ) => Some(Instruction.BssyPredRegImm(pp, barReg, sa, modifiers, guard))
        case _ => None
      }
  }
  object BssyRegImm extends Definition(
        "BssyRegImm",
        "BSSY",
        Vec(Slot("barReg", OperandKind.Register), Slot("sa", OperandKind.Immediate)),
        Vec("bssy_", "bssy_rel_", "bssy_reliability_", "bssy_reliability_rel_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(barReg), InstructionOperand.Immediate(sa)) =>
          Some(Instruction.BssyRegImm(barReg, sa, modifiers, guard))
        case _ => None
      }
  }
  object BssycbuPipePredRegImm extends Definition(
        "BssycbuPipePredRegImm",
        "BSSYcbu_pipe",
        Vec(Slot("pp", OperandKind.Predicate), Slot("barReg", OperandKind.Register), Slot("sa", OperandKind.Immediate)),
        Vec("bssy_", "bssy_rel_", "bssy_reliability_", "bssy_reliability_rel_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pp),
              InstructionOperand.Register(barReg),
              InstructionOperand.Immediate(sa)
            ) => Some(Instruction.BssycbuPipePredRegImm(pp, barReg, sa, modifiers, guard))
        case _ => None
      }
  }
  object BssycbuPipeRegImm extends Definition(
        "BssycbuPipeRegImm",
        "BSSYcbu_pipe",
        Vec(Slot("barReg", OperandKind.Register), Slot("sa", OperandKind.Immediate)),
        Vec("bssy_", "bssy_rel_", "bssy_reliability_", "bssy_reliability_rel_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(barReg), InstructionOperand.Immediate(sa)) =>
          Some(Instruction.BssycbuPipeRegImm(barReg, sa, modifiers, guard))
        case _ => None
      }
  }
  object BsyncPredReg extends Definition(
        "BsyncPredReg",
        "BSYNC",
        Vec(Slot("pp", OperandKind.Predicate), Slot("barReg", OperandKind.Register)),
        Vec("bsync_", "bsync_reliability_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.Register(barReg)) =>
          Some(Instruction.BsyncPredReg(pp, barReg, modifiers, guard))
        case _ => None
      }
  }
  object BsyncReg extends Definition(
        "BsyncReg",
        "BSYNC",
        Vec(Slot("barReg", OperandKind.Register)),
        Vec("bsync_", "bsync_reliability_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(barReg)) => Some(Instruction.BsyncReg(barReg, modifiers, guard))
        case _ => None
      }
  }
  object BsynccbuPipePredReg extends Definition(
        "BsynccbuPipePredReg",
        "BSYNCcbu_pipe",
        Vec(Slot("pp", OperandKind.Predicate), Slot("barReg", OperandKind.Register)),
        Vec("bsync_", "bsync_reliability_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.Register(barReg)) =>
          Some(Instruction.BsynccbuPipePredReg(pp, barReg, modifiers, guard))
        case _ => None
      }
  }
  object BsynccbuPipeReg extends Definition(
        "BsynccbuPipeReg",
        "BSYNCcbu_pipe",
        Vec(Slot("barReg", OperandKind.Register)),
        Vec("bsync_", "bsync_reliability_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(barReg)) => Some(Instruction.BsynccbuPipeReg(barReg, modifiers, guard))
        case _ => None
      }
  }
  object CalConst
      extends Definition("CalConst", "CAL", Vec(Slot("srcConst", OperandKind.ConstantBank)), Vec("CAL c", "CAL_c")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(srcConst)) => Some(Instruction.CalConst(srcConst, modifiers, guard))
        case _ => None
      }
  }
  object CalImm extends Definition("CalImm", "CAL", Vec(Slot("sImm", OperandKind.Immediate)), Vec("CAL")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sImm)) => Some(Instruction.CalImm(sImm, modifiers, guard))
        case _ => None
      }
  }
  object CallConst
      extends Definition("CallConst", "CALL", Vec(Slot("sa", OperandKind.ConstantBank)), Vec("call_abs__RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(sa)) => Some(Instruction.CallConst(sa, modifiers, guard))
        case _ => None
      }
  }
  object CallImm extends Definition(
        "CallImm",
        "CALL",
        Vec(Slot("sa", OperandKind.Immediate)),
        Vec("call_abs__RIR", "call_rel__RIR", "call_rel_imm__RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sa)) => Some(Instruction.CallImm(sa, modifiers, guard))
        case _ => None
      }
  }
  object CallPredConst extends Definition(
        "CallPredConst",
        "CALL",
        Vec(Slot("pp", OperandKind.Predicate), Slot("sa", OperandKind.ConstantBank)),
        Vec("call_abs__RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.ConstantBank(sa)) =>
          Some(Instruction.CallPredConst(pp, sa, modifiers, guard))
        case _ => None
      }
  }
  object CallPredImm extends Definition(
        "CallPredImm",
        "CALL",
        Vec(Slot("pp", OperandKind.Predicate), Slot("sa", OperandKind.Immediate)),
        Vec("call_abs__RIR", "call_rel__RIR", "call_rel_imm__RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.Immediate(sa)) =>
          Some(Instruction.CallPredImm(pp, sa, modifiers, guard))
        case _ => None
      }
  }
  val definitions: Vec[Definition] = Vec(
    AcqbulkNoOperands,
    AcqbulkcbuPipeNoOperands,
    AcqshminitNoOperands,
    AcqshminitcbuPipeNoOperands,
    Al2pPredRegReg,
    Al2pPredRegRegImm,
    Al2pRegReg,
    Al2pRegRegImm,
    Al2pmioPipePredRegReg,
    Al2pmioPipePredRegRegImm,
    Al2pmioPipeRegReg,
    Al2pmioPipeRegRegImm,
    Al2pxluPipeRegReg,
    Al2pxluPipeRegRegImm,
    AldRegAddressReg,
    AldaguPipeRegAddressReg,
    AldmioPipeRegAddressReg,
    ArrivesAddress,
    ArrivesmioPipeAddress,
    AstAddressRegReg,
    AstaguPipeAddressRegReg,
    AstmioPipeAddressRegReg,
    AtomPredRegAddress,
    AtomPredRegAddressReg,
    AtomPredRegAddressRegReg,
    AtomRegAddressReg,
    AtomRegAddressRegReg,
    AtomgPredRegAddressReg,
    AtomgPredRegAddressRegReg,
    AtomgmioPipePredRegAddressReg,
    AtomgmioPipePredRegAddressRegReg,
    AtomsPredAddressRegReg,
    AtomsRegAddress,
    AtomsRegAddressReg,
    AtomsRegAddressRegReg,
    AtomsmioPipePredAddressRegReg,
    AtomsmioPipeRegAddress,
    AtomsmioPipeRegAddressReg,
    AtomsmioPipeRegAddressRegReg,
    AtomaguPipeRegAddressReg,
    AtomaguPipeRegAddressRegReg,
    AtommioPipePredRegAddress,
    AtommioPipePredRegAddressReg,
    AtommioPipePredRegAddressRegReg,
    AtommioPipeRegAddressReg,
    AtommioPipeRegAddressRegReg,
    B2rReg,
    B2rRegImm,
    B2rRegPred,
    B2rfePipeReg,
    B2rfePipeRegImm,
    B2rfePipeRegPred,
    B2rmioPipeReg,
    B2rmioPipeRegImm,
    B2rmioPipeRegPred,
    BarNoOperands,
    BarImm,
    BarImmImm,
    BarImmImmPred,
    BarImmPred,
    BarImmReg,
    BarImmRegPred,
    BarReg,
    BarRegImm,
    BarRegImmPred,
    BarRegPred,
    BarRegReg,
    BarRegRegPred,
    BarfePipeNoOperands,
    BarfePipeImm,
    BarfePipeImmImm,
    BarfePipeImmImmPred,
    BarfePipeImmPred,
    BarfePipeImmReg,
    BarfePipeImmRegPred,
    BarfePipeReg,
    BarfePipeRegImm,
    BarfePipeRegImmPred,
    BarfePipeRegPred,
    BarfePipeRegReg,
    BarfePipeRegRegPred,
    BarmioPipeNoOperands,
    BarmioPipeImm,
    BarmioPipeImmImm,
    BarmioPipeImmImmPred,
    BarmioPipeImmPred,
    BarmioPipeImmReg,
    BarmioPipeImmRegPred,
    BarmioPipeReg,
    BarmioPipeRegImm,
    BarmioPipeRegImmPred,
    BarmioPipeRegPred,
    BarmioPipeRegReg,
    BarmioPipeRegRegPred,
    BfeRegRegConst,
    BfeRegRegImm,
    BfeRegRegReg,
    BfefxuPipeRegRegConst,
    BfefxuPipeRegRegImm,
    BfefxuPipeRegRegReg,
    BfexluPipeRegRegConst,
    BfexluPipeRegRegImm,
    BfexluPipeRegRegReg,
    BfiRegRegConstReg,
    BfiRegRegImmReg,
    BfiRegRegRegConst,
    BfiRegRegRegReg,
    BfifxuPipeRegRegConstReg,
    BfifxuPipeRegRegImmReg,
    BfifxuPipeRegRegRegConst,
    BfifxuPipeRegRegRegReg,
    BfixluPipeRegRegConstReg,
    BfixluPipeRegRegImmReg,
    BfixluPipeRegRegRegConst,
    BfixluPipeRegRegRegReg,
    BgmmaRegAddressReg,
    BgmmaRegAddressRegPred,
    BgmmaRegAddressRegReg,
    BgmmaRegAddressRegRegPred,
    BgmmaRegRegAddressReg,
    BgmmaRegRegAddressRegPred,
    BgmmamioPipeRegAddressReg,
    BgmmamioPipeRegAddressRegPred,
    BgmmamioPipeRegAddressRegReg,
    BgmmamioPipeRegAddressRegRegPred,
    BgmmamioPipeRegRegAddressReg,
    BgmmamioPipeRegRegAddressRegPred,
    BmmaRegRegRegReg,
    BmmaRegRegRegRegPred,
    BmmaintPipeRegRegRegReg,
    BmmaintPipeRegRegRegRegPred,
    BmovAddressImm,
    BmovConst,
    BmovImm,
    BmovReg,
    BmovRegAddressImm,
    BmovRegConst,
    BmovRegImm,
    BmovRegReg,
    BmovRegSymbol,
    BmovSymbolAddressImm,
    BmovSymbolConst,
    BmovSymbolImm,
    BmovSymbolReg,
    BmovcbuPipeAddressImm,
    BmovcbuPipeConst,
    BmovcbuPipeImm,
    BmovcbuPipeReg,
    BmovcbuPipeRegAddressImm,
    BmovcbuPipeRegConst,
    BmovcbuPipeRegImm,
    BmovcbuPipeRegReg,
    BmovcbuPipeRegSymbol,
    BmovcbuPipeSymbolAddressImm,
    BmovcbuPipeSymbolConst,
    BmovcbuPipeSymbolImm,
    BmovcbuPipeSymbolReg,
    BmskRegRegAddressImm,
    BmskRegRegConst,
    BmskRegRegImm,
    BmskRegRegReg,
    BmskintPipeRegRegAddressImm,
    BmskintPipeRegRegConst,
    BmskintPipeRegRegImm,
    BmskintPipeRegRegReg,
    BptNoOperands,
    BptImm,
    BptbruPipeNoOperands,
    BptbruPipeImm,
    BptcbuPipeNoOperands,
    BptcbuPipeImm,
    BraConst,
    BraImm,
    BraPredImm,
    BraPredPredImm,
    BraPredRegImm,
    BraRegImm,
    BraSymbolConst,
    BraSymbolImm,
    BraSymbolImmImm,
    BraSymbolSymbolImmImm,
    BrabruPipeConst,
    BrabruPipeImm,
    BrabruPipeSymbolConst,
    BrabruPipeSymbolImm,
    BrabruPipeSymbolImmImm,
    BrabruPipeSymbolSymbolImmImm,
    BracbuPipeImm,
    BracbuPipePredImm,
    BracbuPipePredPredImm,
    BracbuPipePredRegImm,
    BracbuPipeRegImm,
    BreakPredReg,
    BreakReg,
    BreakcbuPipePredReg,
    BreakcbuPipeReg,
    BrevRegAddressImm,
    BrevRegConst,
    BrevRegImm,
    BrevRegReg,
    BrevmioPipeRegAddressImm,
    BrevmioPipeRegConst,
    BrevmioPipeRegImm,
    BrevmioPipeRegReg,
    BrkNoOperands,
    BrkbruPipeNoOperands,
    BrxConst,
    BrxPredReg,
    BrxPredRegImm,
    BrxReg,
    BrxRegImm,
    BrxSymbolConst,
    BrxSymbolImmAddress,
    BrxSymbolReg,
    BrxSymbolRegImm,
    BrxSymbolSymbolImmAddress,
    BrxuPredReg,
    BrxuPredRegImm,
    BrxuReg,
    BrxuRegImm,
    BrxucbuPipePredReg,
    BrxucbuPipePredRegImm,
    BrxucbuPipeReg,
    BrxucbuPipeRegImm,
    BrxaduPipeReg,
    BrxaduPipeRegImm,
    BrxaduPipeSymbolImmAddress,
    BrxaduPipeSymbolReg,
    BrxaduPipeSymbolRegImm,
    BrxaduPipeSymbolSymbolImmAddress,
    BrxbruPipeConst,
    BrxbruPipeReg,
    BrxbruPipeRegImm,
    BrxbruPipeSymbolConst,
    BrxbruPipeSymbolImmAddress,
    BrxbruPipeSymbolReg,
    BrxbruPipeSymbolRegImm,
    BrxbruPipeSymbolSymbolImmAddress,
    BrxcbuPipePredReg,
    BrxcbuPipePredRegImm,
    BrxcbuPipeReg,
    BrxcbuPipeRegImm,
    BssyPredRegImm,
    BssyRegImm,
    BssycbuPipePredRegImm,
    BssycbuPipeRegImm,
    BsyncPredReg,
    BsyncReg,
    BsynccbuPipePredReg,
    BsynccbuPipeReg,
    CalConst,
    CalImm,
    CallConst,
    CallImm,
    CallPredConst,
    CallPredImm
  )
}
