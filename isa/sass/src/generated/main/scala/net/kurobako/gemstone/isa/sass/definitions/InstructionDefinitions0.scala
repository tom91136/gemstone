// Generated SASS instruction definitions and checked operand constructors. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.sass
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import Instruction.*
@javax.annotation.processing.Generated(Array("sbt codegen"))
object InstructionDefinitions0 {
  object AcqbulkNoOperands extends Definition("AcqbulkNoOperands", "ACQBULK", Vec("acqbulk_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.AcqbulkNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object AcqshminitNoOperands extends Definition("AcqshminitNoOperands", "ACQSHMINIT", Vec("acqshminit_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.AcqshminitNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object Al2pPredRegReg extends Definition("Al2pPredRegReg", "AL2P", Vec("AL2P", "I_AL2P")) {
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
  object Al2pPredRegRegImm extends Definition("Al2pPredRegRegImm", "AL2P", Vec("AL2P", "I_AL2P")) {
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
  object Al2pRegReg extends Definition("Al2pRegReg", "AL2P", Vec("AL2P", "I AL2P", "I_AL2P")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcA)) =>
          Some(Instruction.Al2pRegReg(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object Al2pRegRegImm
      extends Definition("Al2pRegRegImm", "AL2P", Vec("AL2P", "I AL2P", "I_AL2P", "al2p__RaNonRZ", "al2p__RaRZ")) {
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
  object AldRegAddressReg extends Definition(
        "AldRegAddressReg",
        "ALD",
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
  object ArrivesAddress extends Definition("ArrivesAddress", "ARRIVES", Vec("arrives_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA)) => Some(Instruction.ArrivesAddress(srcA, modifiers, guard))
        case _ => None
      }
  }
  object AstAddressRegReg extends Definition(
        "AstAddressRegReg",
        "AST",
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
  object AtomPredRegAddress extends Definition(
        "AtomPredRegAddress",
        "ATOM",
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
  object AtomsPredAddressRegReg extends Definition(
        "AtomsPredAddressRegReg",
        "ATOMS",
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
  object AtomsRegAddress
      extends Definition("AtomsRegAddress", "ATOMS", Vec("atoms_arrive__arrive", "atoms_arrive__popcinc")) {
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
  object B2rReg extends Definition(
        "B2rReg",
        "B2R",
        Vec("B2R RESULT", "B2R WARP", "B2R_RESULT", "B2R_WARP", "b2r__RESULT", "b2r__WARP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst)) => Some(Instruction.B2rReg(dst, modifiers, guard))
        case _ => None
      }
  }
  object B2rRegImm extends Definition("B2rRegImm", "B2R", Vec("B2R BAR", "B2R_BAR", "b2r__BAR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(barNum)) =>
          Some(Instruction.B2rRegImm(dst, barNum, modifiers, guard))
        case _ => None
      }
  }
  object B2rRegPred extends Definition("B2rRegPred", "B2R", Vec("B2R RESULT", "B2R_RESULT", "b2r__RESULT")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Predicate(dstPredicate)) =>
          Some(Instruction.B2rRegPred(dst, dstPredicate, modifiers, guard))
        case _ => None
      }
  }
  object BarNoOperands extends Definition(
        "BarNoOperands",
        "BAR",
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
  object BfeRegRegConst extends Definition("BfeRegRegConst", "BFE", Vec("Const BFE", "Const_BFE")) {
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
  object BfeRegRegImm extends Definition("BfeRegRegImm", "BFE", Vec("Imm BFE", "Imm_BFE")) {
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
  object BfeRegRegReg extends Definition("BfeRegRegReg", "BFE", Vec("BFE")) {
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
  object BfiRegRegConstReg extends Definition("BfiRegRegConstReg", "BFI", Vec("Const BFI", "Const_BFI")) {
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
  object BfiRegRegImmReg extends Definition("BfiRegRegImmReg", "BFI", Vec("Imm BFI", "Imm_BFI")) {
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
  object BfiRegRegRegConst extends Definition("BfiRegRegRegConst", "BFI", Vec("Const1 BFI", "Const1_BFI")) {
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
  object BfiRegRegRegReg extends Definition("BfiRegRegRegReg", "BFI", Vec("BFI")) {
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
  object BgmmaRegAddressReg extends Definition("BgmmaRegAddressReg", "BGMMA", Vec("bgmma_URa_Rc_")) {
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
  object BgmmaRegAddressRegPred extends Definition("BgmmaRegAddressRegPred", "BGMMA", Vec("bgmma_URa_Rc_")) {
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
  object BgmmaRegAddressRegReg extends Definition("BgmmaRegAddressRegReg", "BGMMA", Vec("bgmma_URa_Rb_Rc_")) {
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
  object BgmmaRegAddressRegRegPred extends Definition("BgmmaRegAddressRegRegPred", "BGMMA", Vec("bgmma_URa_Rb_Rc_")) {
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
  object BgmmaRegRegAddressReg extends Definition("BgmmaRegRegAddressReg", "BGMMA", Vec("bgmma_Ra_URb_Rc_")) {
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
  object BgmmaRegRegAddressRegPred extends Definition("BgmmaRegRegAddressRegPred", "BGMMA", Vec("bgmma_Ra_URb_Rc_")) {
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
  object BmmaRegRegRegReg extends Definition("BmmaRegRegRegReg", "BMMA", Vec("bmma_", "bmma_88128_")) {
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
  object BmmaRegRegRegRegPred extends Definition("BmmaRegRegRegRegPred", "BMMA", Vec("bmma_")) {
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
  object BmovAddressImm extends Definition("BmovAddressImm", "BMOV", Vec("bmov_dst64__CX")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcB), InstructionOperand.Immediate(sb_offset)) =>
          Some(Instruction.BmovAddressImm(srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object BmovConst extends Definition("BmovConst", "BMOV", Vec("bmov_dst64__C")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(sb)) => Some(Instruction.BmovConst(sb, modifiers, guard))
        case _ => None
      }
  }
  object BmovImm extends Definition("BmovImm", "BMOV", Vec("bmov_dst64__I")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb)) => Some(Instruction.BmovImm(sb, modifiers, guard))
        case _ => None
      }
  }
  object BmovReg extends Definition("BmovReg", "BMOV", Vec("bmov_clear_bd__Bd", "bmov_dst64__R", "bmov_dst64__UR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcB)) => Some(Instruction.BmovReg(srcB, modifiers, guard))
        case _ => None
      }
  }
  object BmovRegAddressImm extends Definition("BmovRegAddressImm", "BMOV", Vec("bmov_pquad__RCXR")) {
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
  object BmovRegConst extends Definition("BmovRegConst", "BMOV", Vec("bmov_pquad__RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(cbu_state), InstructionOperand.ConstantBank(sb)) =>
          Some(Instruction.BmovRegConst(cbu_state, sb, modifiers, guard))
        case _ => None
      }
  }
  object BmovRegImm extends Definition("BmovRegImm", "BMOV", Vec("bmov_pquad__RIR")) {
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
        Vec("bmov_clear__Rd", "bmov_clear_barrier_", "bmov_pquad__RRR", "bmov_pquad__RUR", "bmov_pquad_bar__RBR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(cbu_state)) =>
          Some(Instruction.BmovRegReg(dst, cbu_state, modifiers, guard))
        case _ => None
      }
  }
  object BmovRegSymbol extends Definition("BmovRegSymbol", "BMOV", Vec("bmov_clear__Rd")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Symbol(cbu_state)) =>
          Some(Instruction.BmovRegSymbol(dst, cbu_state, modifiers, guard))
        case _ => None
      }
  }
  object BmovSymbolAddressImm extends Definition("BmovSymbolAddressImm", "BMOV", Vec("bmov_pquad__RCXR")) {
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
  object BmovSymbolConst extends Definition("BmovSymbolConst", "BMOV", Vec("bmov_pquad__RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Symbol(cbu_state), InstructionOperand.ConstantBank(sb)) =>
          Some(Instruction.BmovSymbolConst(cbu_state, sb, modifiers, guard))
        case _ => None
      }
  }
  object BmovSymbolImm extends Definition("BmovSymbolImm", "BMOV", Vec("bmov_pquad__RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Symbol(cbu_state), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.BmovSymbolImm(cbu_state, sb, modifiers, guard))
        case _ => None
      }
  }
  object BmovSymbolReg
      extends Definition("BmovSymbolReg", "BMOV", Vec("bmov_pquad__RRR", "bmov_pquad__RUR", "bmov_pquad_bar__RBR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Symbol(cbu_state), InstructionOperand.Register(srcB)) =>
          Some(Instruction.BmovSymbolReg(cbu_state, srcB, modifiers, guard))
        case _ => None
      }
  }
  object BmskRegRegAddressImm extends Definition("BmskRegRegAddressImm", "BMSK", Vec("bmsk__RCxR_RCxR")) {
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
  object BmskRegRegConst extends Definition("BmskRegRegConst", "BMSK", Vec("bmsk__RCR_RCR")) {
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
  object BmskRegRegImm extends Definition("BmskRegRegImm", "BMSK", Vec("bmsk__RuIR_RIR")) {
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
  object BmskRegRegReg extends Definition("BmskRegRegReg", "BMSK", Vec("bmsk__RRR_RRR", "bmsk__RUR_RUR")) {
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
  object BptNoOperands extends Definition(
        "BptNoOperands",
        "BPT",
        Vec("BPT", "bpt__WAIT", "bpt__noDRAIN", "bpt__noWAIT", "bpt__onlyDRAIN")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.BptNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object BptImm
      extends Definition("BptImm", "BPT", Vec("BPT", "bpt__WAIT", "bpt__noDRAIN", "bpt__noWAIT", "bpt__onlyDRAIN")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb)) => Some(Instruction.BptImm(sb, modifiers, guard))
        case _ => None
      }
  }
  object BraConst extends Definition("BraConst", "BRA", Vec("BRA_c")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(srcConst)) => Some(Instruction.BraConst(srcConst, modifiers, guard))
        case _ => None
      }
  }
  object BraImm extends Definition(
        "BraImm",
        "BRA",
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
  object BraPredPredImm extends Definition("BraPredPredImm", "BRA", Vec("bra_uniform_pred_", "bra_uniform_pred_rel_")) {
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
  object BraPredRegImm extends Definition("BraPredRegImm", "BRA", Vec("bra_uniform_", "bra_uniform_rel_")) {
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
  object BraRegImm extends Definition("BraRegImm", "BRA", Vec("bra_uniform_", "bra_uniform_rel_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcB), InstructionOperand.Immediate(sImm)) =>
          Some(Instruction.BraRegImm(srcB, sImm, modifiers, guard))
        case _ => None
      }
  }
  object BraSymbolConst extends Definition("BraSymbolConst", "BRA", Vec("BRA_c")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Symbol(testCC), InstructionOperand.ConstantBank(srcConst)) =>
          Some(Instruction.BraSymbolConst(testCC, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object BraSymbolImm extends Definition("BraSymbolImm", "BRA", Vec("BRA")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Symbol(testCC), InstructionOperand.Immediate(sImm)) =>
          Some(Instruction.BraSymbolImm(testCC, sImm, modifiers, guard))
        case _ => None
      }
  }
  object BraSymbolImmImm extends Definition("BraSymbolImmImm", "BRA", Vec("BRA c")) {
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
  object BraSymbolSymbolImmImm extends Definition("BraSymbolSymbolImmImm", "BRA", Vec("BRA c")) {
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
  object BreakPredReg extends Definition("BreakPredReg", "BREAK", Vec("break_inst_", "break_reliability_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.Register(barReg)) =>
          Some(Instruction.BreakPredReg(pp, barReg, modifiers, guard))
        case _ => None
      }
  }
  object BreakReg extends Definition("BreakReg", "BREAK", Vec("break_inst_", "break_reliability_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(barReg)) => Some(Instruction.BreakReg(barReg, modifiers, guard))
        case _ => None
      }
  }
  object BrevRegAddressImm extends Definition("BrevRegAddressImm", "BREV", Vec("brev__RCxR_RCxR")) {
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
  object BrevRegConst extends Definition("BrevRegConst", "BREV", Vec("brev__RCR_RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(sb)) =>
          Some(Instruction.BrevRegConst(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object BrevRegImm extends Definition("BrevRegImm", "BREV", Vec("brev__RuIR_RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.BrevRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object BrevRegReg extends Definition("BrevRegReg", "BREV", Vec("brev__RRR_RRR", "brev__RUR_RUR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.BrevRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object BrkNoOperands extends Definition("BrkNoOperands", "BRK", Vec("BRK")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.BrkNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object BrxConst extends Definition("BrxConst", "BRX", Vec("BRX_c")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(srcConst)) => Some(Instruction.BrxConst(srcConst, modifiers, guard))
        case _ => None
      }
  }
  object BrxPredReg extends Definition("BrxPredReg", "BRX", Vec("brx_", "brx_rel_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.Register(srcA)) =>
          Some(Instruction.BrxPredReg(pp, srcA, modifiers, guard))
        case _ => None
      }
  }
  object BrxPredRegImm extends Definition("BrxPredRegImm", "BRX", Vec("brx_", "brx_rel_")) {
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
  object BrxReg extends Definition("BrxReg", "BRX", Vec("BRX", "brx_", "brx_rel_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA)) => Some(Instruction.BrxReg(srcA, modifiers, guard))
        case _ => None
      }
  }
  object BrxRegImm extends Definition("BrxRegImm", "BRX", Vec("BRX", "brx_", "brx_rel_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA), InstructionOperand.Immediate(ra_offset)) =>
          Some(Instruction.BrxRegImm(srcA, ra_offset, modifiers, guard))
        case _ => None
      }
  }
  object BrxSymbolConst extends Definition("BrxSymbolConst", "BRX", Vec("BRX_c")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Symbol(testCC), InstructionOperand.ConstantBank(srcConst)) =>
          Some(Instruction.BrxSymbolConst(testCC, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object BrxSymbolImmAddress extends Definition("BrxSymbolImmAddress", "BRX", Vec("BRX c")) {
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
  object BrxSymbolReg extends Definition("BrxSymbolReg", "BRX", Vec("BRX")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Symbol(testCC), InstructionOperand.Register(srcA)) =>
          Some(Instruction.BrxSymbolReg(testCC, srcA, modifiers, guard))
        case _ => None
      }
  }
  object BrxSymbolRegImm extends Definition("BrxSymbolRegImm", "BRX", Vec("BRX")) {
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
  object BrxSymbolSymbolImmAddress extends Definition("BrxSymbolSymbolImmAddress", "BRX", Vec("BRX c")) {
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
  object BrxuPredReg extends Definition("BrxuPredReg", "BRXU", Vec("brxu_", "brxu_rel_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.Register(srcA)) =>
          Some(Instruction.BrxuPredReg(pp, srcA, modifiers, guard))
        case _ => None
      }
  }
  object BrxuPredRegImm extends Definition("BrxuPredRegImm", "BRXU", Vec("brxu_", "brxu_rel_")) {
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
  object BrxuReg extends Definition("BrxuReg", "BRXU", Vec("brxu_", "brxu_rel_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA)) => Some(Instruction.BrxuReg(srcA, modifiers, guard))
        case _ => None
      }
  }
  object BrxuRegImm extends Definition("BrxuRegImm", "BRXU", Vec("brxu_", "brxu_rel_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA), InstructionOperand.Immediate(uR_offset)) =>
          Some(Instruction.BrxuRegImm(srcA, uR_offset, modifiers, guard))
        case _ => None
      }
  }
  object BssyPredRegImm extends Definition(
        "BssyPredRegImm",
        "BSSY",
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
        Vec("bssy_", "bssy_rel_", "bssy_reliability_", "bssy_reliability_rel_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(barReg), InstructionOperand.Immediate(sa)) =>
          Some(Instruction.BssyRegImm(barReg, sa, modifiers, guard))
        case _ => None
      }
  }
  object BsyncPredReg extends Definition("BsyncPredReg", "BSYNC", Vec("bsync_", "bsync_reliability_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.Register(barReg)) =>
          Some(Instruction.BsyncPredReg(pp, barReg, modifiers, guard))
        case _ => None
      }
  }
  object BsyncReg extends Definition("BsyncReg", "BSYNC", Vec("bsync_", "bsync_reliability_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(barReg)) => Some(Instruction.BsyncReg(barReg, modifiers, guard))
        case _ => None
      }
  }
  object CalConst extends Definition("CalConst", "CAL", Vec("CAL c", "CAL_c")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(srcConst)) => Some(Instruction.CalConst(srcConst, modifiers, guard))
        case _ => None
      }
  }
  object CalImm extends Definition("CalImm", "CAL", Vec("CAL")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sImm)) => Some(Instruction.CalImm(sImm, modifiers, guard))
        case _ => None
      }
  }
  object CallConst extends Definition("CallConst", "CALL", Vec("call_abs__RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(sa)) => Some(Instruction.CallConst(sa, modifiers, guard))
        case _ => None
      }
  }
  object CallImm extends Definition("CallImm", "CALL", Vec("call_abs__RIR", "call_rel__RIR", "call_rel_imm__RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sa)) => Some(Instruction.CallImm(sa, modifiers, guard))
        case _ => None
      }
  }
  object CallPredConst extends Definition("CallPredConst", "CALL", Vec("call_abs__RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.ConstantBank(sa)) =>
          Some(Instruction.CallPredConst(pp, sa, modifiers, guard))
        case _ => None
      }
  }
  object CallPredImm
      extends Definition("CallPredImm", "CALL", Vec("call_abs__RIR", "call_rel__RIR", "call_rel_imm__RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.Immediate(sa)) =>
          Some(Instruction.CallPredImm(pp, sa, modifiers, guard))
        case _ => None
      }
  }
  object CallPredReg extends Definition(
        "CallPredReg",
        "CALL",
        Vec("call_abs__RRR", "call_abs__URIR", "call_rel_reg_", "call_rel_reg__RRR", "call_rel_reg__URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.Register(srcA)) =>
          Some(Instruction.CallPredReg(pp, srcA, modifiers, guard))
        case _ => None
      }
  }
  object CallPredRegImm extends Definition(
        "CallPredRegImm",
        "CALL",
        Vec(
          "call_abs__RRR",
          "call_abs__URIR",
          "call_rel__RRR",
          "call_rel__URIR",
          "call_rel_imm__RRR",
          "call_rel_imm__URIR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pp),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(ra_offset)
            ) => Some(Instruction.CallPredRegImm(pp, srcA, ra_offset, modifiers, guard))
        case _ => None
      }
  }
  object CallReg extends Definition(
        "CallReg",
        "CALL",
        Vec("call_abs__RRR", "call_abs__URIR", "call_rel_reg_", "call_rel_reg__RRR", "call_rel_reg__URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA)) => Some(Instruction.CallReg(srcA, modifiers, guard))
        case _ => None
      }
  }
  object CallRegImm extends Definition(
        "CallRegImm",
        "CALL",
        Vec(
          "call_abs__RRR",
          "call_abs__URIR",
          "call_rel__RRR",
          "call_rel__URIR",
          "call_rel_imm__RRR",
          "call_rel_imm__URIR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA), InstructionOperand.Immediate(ra_offset)) =>
          Some(Instruction.CallRegImm(srcA, ra_offset, modifiers, guard))
        case _ => None
      }
  }
  object CctlNoOperands extends Definition(
        "CctlNoOperands",
        "CCTL",
        Vec(
          "CCTL IVALL",
          "CCTL_CI_IVALL",
          "CCTL_IVALL",
          "cctl__IVALL_WBALL_C_noSrc",
          "cctl__IVALL_WBALL_D_U_noSrc",
          "cctl__IVALL_WBALL_I_noSrc",
          "cctl_c_ivall_wball_nosrc_",
          "cctl_c_ldc_ivall_",
          "cctl_c_ldcu_ivall_",
          "cctl_i_ivall_wball_nosrc_"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.CctlNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object CctlAddress extends Definition(
        "CctlAddress",
        "CCTL",
        Vec(
          "CCTL",
          "CCTL QRY",
          "CCTL_QRY",
          "I CCTL",
          "I CCTL QRY",
          "I_CCTL",
          "I_CCTL_QRY",
          "cctl__sImmOffset",
          "cctl__sImmOffset_pf2",
          "cctl__sImmOffset_rml2",
          "cctl__sUROffset",
          "cctl__sUROffset_pf2",
          "cctl__sUROffset_rml2",
          "cctl__uImmOffset",
          "cctl__uImmOffset_pf2",
          "cctl__uImmOffset_rml2",
          "cctl__uUROffset",
          "cctl__uUROffset_pf2",
          "cctl__uUROffset_rml2",
          "cctl_c_ldc_va_",
          "cctl_c_ldcu_va_"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA)) => Some(Instruction.CctlAddress(srcA, modifiers, guard))
        case _ => None
      }
  }
  object CctlAddressAddress extends Definition(
        "CctlAddressAddress",
        "CCTL",
        Vec("cctl_c_ldc_const_bindless_", "cctl_c_ldcu_const_bindless_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA), InstructionOperand.Address(srcB)) =>
          Some(Instruction.CctlAddressAddress(srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object CctlAddressImm extends Definition(
        "CctlAddressImm",
        "CCTL",
        Vec("cctl__sImmOffset_rml2", "cctl__sUROffset_rml2", "cctl__uImmOffset_rml2", "cctl__uUROffset_rml2")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA), InstructionOperand.Immediate(sector_count)) =>
          Some(Instruction.CctlAddressImm(srcA, sector_count, modifiers, guard))
        case _ => None
      }
  }
  object CctlConst extends Definition("CctlConst", "CCTL", Vec("cctl_c_ldc_const_bound_", "cctl_c_ldcu_const_bound_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(sa)) => Some(Instruction.CctlConst(sa, modifiers, guard))
        case _ => None
      }
  }
  object CctlPredAddress extends Definition(
        "CctlPredAddress",
        "CCTL",
        Vec("cctl__sImmOffset_pf2_q", "cctl__sUROffset_pf2_q", "cctl__uImmOffset_pf2_q", "cctl__uUROffset_pf2_q")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pu), InstructionOperand.Address(srcA)) =>
          Some(Instruction.CctlPredAddress(pu, srcA, modifiers, guard))
        case _ => None
      }
  }
  object CctlRegAddress
      extends Definition("CctlRegAddress", "CCTL", Vec("CCTL QRY", "CCTL_QRY", "I CCTL QRY", "I_CCTL_QRY")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Address(srcA)) =>
          Some(Instruction.CctlRegAddress(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object CctllNoOperands extends Definition(
        "CctllNoOperands",
        "CCTLL",
        Vec("CCTLL NoOperands", "CCTLL_CRS_WBALL", "CCTLL_IVALL", "cctll__IVALL_WBALL_D_U_noSrc")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.CctllNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object CctllAddress extends Definition(
        "CctllAddress",
        "CCTLL",
        Vec(
          "CCTLL",
          "CCTLL QRY",
          "I CCTLL",
          "I CCTLL QRY",
          "I_CCTLL",
          "cctll__Ra_RZ_UR",
          "cctll__Ra_nonRz_UR",
          "cctll__sImmOffset",
          "cctll__uImmOffset"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA)) => Some(Instruction.CctllAddress(srcA, modifiers, guard))
        case _ => None
      }
  }
  object CctllRegAddress extends Definition("CctllRegAddress", "CCTLL", Vec("CCTLL QRY", "I CCTLL QRY")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Address(srcA)) =>
          Some(Instruction.CctllRegAddress(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object CctlsNoOperands extends Definition("CctlsNoOperands", "CCTLS", Vec("CCTLS")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.CctlsNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object CctltNoOperands extends Definition("CctltNoOperands", "CCTLT", Vec("CCTLT", "CCTLT_IVALL", "cctlt__IVALL")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.CctltNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object CctltImm extends Definition("CctltImm", "CCTLT", Vec("CCTLT_IDX")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(uImm)) => Some(Instruction.CctltImm(uImm, modifiers, guard))
        case _ => None
      }
  }
  object CctltImmImm extends Definition("CctltImmImm", "CCTLT", Vec("cctlt__const")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb_bank), InstructionOperand.Immediate(sb_offset)) =>
          Some(Instruction.CctltImmImm(sb_bank, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object CctltReg extends Definition("CctltReg", "CCTLT", Vec("CCTLT_REG", "cctlt__Rb", "cctlt__URb")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcB)) => Some(Instruction.CctltReg(srcB, modifiers, guard))
        case _ => None
      }
  }
  object CgaerrbarNoOperands extends Definition("CgaerrbarNoOperands", "CGAERRBAR", Vec("cgaerrbar_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.CgaerrbarNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object ClmadRegRegAddressImmReg extends Definition("ClmadRegRegAddressImmReg", "CLMAD", Vec("clmad__RCxR_RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.ClmadRegRegAddressImmReg(dst, srcA, srcB, sb_offset, srcC, modifiers, guard))
        case _ => None
      }
  }
  object ClmadRegRegConstReg extends Definition("ClmadRegRegConstReg", "CLMAD", Vec("clmad__RCR_RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.ClmadRegRegConstReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object ClmadRegRegRegAddressImm extends Definition("ClmadRegRegRegAddressImm", "CLMAD", Vec("clmad__RRCx_RRCx")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Address(srcC),
              InstructionOperand.Immediate(sc_offset)
            ) => Some(Instruction.ClmadRegRegRegAddressImm(dst, srcA, srcB, srcC, sc_offset, modifiers, guard))
        case _ => None
      }
  }
  object ClmadRegRegRegConst extends Definition("ClmadRegRegRegConst", "CLMAD", Vec("clmad__RRC_RRC")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(sc)
            ) => Some(Instruction.ClmadRegRegRegConst(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object ClmadRegRegRegReg
      extends Definition("ClmadRegRegRegReg", "CLMAD", Vec("clmad__RRR_RRR", "clmad__RRU_RRU", "clmad__RUR_RUR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.ClmadRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object ContNoOperands extends Definition("ContNoOperands", "CONT", Vec("BRK", "CONT")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.ContNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object CreduxRegReg extends Definition("CreduxRegReg", "CREDUX", Vec("credux_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcA)) =>
          Some(Instruction.CreduxRegReg(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object Cs2rRegSpecial extends Definition("Cs2rRegSpecial", "CS2R", Vec("CS2R", "cs2r_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.SpecialRegister(sR)) =>
          Some(Instruction.Cs2rRegSpecial(dst, sR, modifiers, guard))
        case _ => None
      }
  }
  object Cs2urRegSpecial extends Definition("Cs2urRegSpecial", "CS2UR", Vec("cs2ur_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.SpecialRegister(sRa)) =>
          Some(Instruction.Cs2urRegSpecial(dst, sRa, modifiers, guard))
        case _ => None
      }
  }
  object CsetRegSymbol extends Definition("CsetRegSymbol", "CSET", Vec("NoBop CSET", "NoBop_CSET")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Symbol(dummyCC)) =>
          Some(Instruction.CsetRegSymbol(dst, dummyCC, modifiers, guard))
        case _ => None
      }
  }
  object CsetRegSymbolPred extends Definition("CsetRegSymbolPred", "CSET", Vec("CSET")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Symbol(dummyCC),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.CsetRegSymbolPred(dst, dummyCC, pa, modifiers, guard))
        case _ => None
      }
  }
  object CsetpPredPredSymbolPred extends Definition("CsetpPredPredSymbolPred", "CSETP", Vec("CSETP")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Symbol(inputCC),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.CsetpPredPredSymbolPred(dstPredicate, nPd, inputCC, pa, modifiers, guard))
        case _ => None
      }
  }
  object CsetpPredSymbol extends Definition("CsetpPredSymbol", "CSETP", Vec("NoBop CSETP", "NoBop_CSETP")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(dstPredicate), InstructionOperand.Symbol(inputCC)) =>
          Some(Instruction.CsetpPredSymbol(dstPredicate, inputCC, modifiers, guard))
        case _ => None
      }
  }
  object CsmtestImm extends Definition("CsmtestImm", "CSMTEST", Vec("csmtest_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sa)) => Some(Instruction.CsmtestImm(sa, modifiers, guard))
        case _ => None
      }
  }
  object CsmtestPredImm extends Definition("CsmtestPredImm", "CSMTEST", Vec("csmtest_cmp_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pu), InstructionOperand.Immediate(sa)) =>
          Some(Instruction.CsmtestPredImm(pu, sa, modifiers, guard))
        case _ => None
      }
  }
  object CsmtestPredPredImmPred extends Definition("CsmtestPredPredImmPred", "CSMTEST", Vec("csmtest_bop_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Immediate(sa),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.CsmtestPredPredImmPred(pu, pv, sa, pp, modifiers, guard))
        case _ => None
      }
  }
  object DaddRegRegAddressImm extends Definition("DaddRegRegAddressImm", "DADD", Vec("dadd__RRCx_RCx")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcC),
              InstructionOperand.Immediate(sc_offset)
            ) => Some(Instruction.DaddRegRegAddressImm(dst, srcA, srcC, sc_offset, modifiers, guard))
        case _ => None
      }
  }
  object DaddRegRegConst
      extends Definition("DaddRegRegConst", "DADD", Vec("Const DADD", "Const_DADD", "dadd__RRC_RC")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sc)
            ) => Some(Instruction.DaddRegRegConst(dst, srcA, sc, modifiers, guard))
        case _ => None
      }
  }
  object DaddRegRegImm extends Definition("DaddRegRegImm", "DADD", Vec("Imm DADD", "Imm_DADD", "dadd__RRsI_RI")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.DaddRegRegImm(dst, srcA, sc, modifiers, guard))
        case _ => None
      }
  }
  object DaddRegRegReg extends Definition("DaddRegRegReg", "DADD", Vec("DADD", "dadd__RRR_RR", "dadd__RRU_RU")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.DaddRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object DchkPredRegConst extends Definition("DchkPredRegConst", "DCHK", Vec("Const DCHK")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.DchkPredRegConst(dstPredicate, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object DchkPredRegImm extends Definition("DchkPredRegImm", "DCHK", Vec("Imm DCHK")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.DchkPredRegImm(dstPredicate, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object DchkPredRegReg extends Definition("DchkPredRegReg", "DCHK", Vec("DCHK")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.DchkPredRegReg(dstPredicate, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object DepbarNoOperands
      extends Definition("DepbarNoOperands", "DEPBAR", Vec("DEPBAR", "DEPBAR_ALL", "depbar__noLE", "depbar_all_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.DepbarNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object DepbarGroup extends Definition("DepbarGroup", "DEPBAR", Vec("DEPBAR", "depbar__noLE")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Group(dep_scbd)) => Some(Instruction.DepbarGroup(dep_scbd, modifiers, guard))
        case _ => None
      }
  }
  object DepbarImm extends Definition("DepbarImm", "DEPBAR", Vec("DEPBAR", "depbar__noLE")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(dep_scbd)) => Some(Instruction.DepbarImm(dep_scbd, modifiers, guard))
        case _ => None
      }
  }
  object DepbarRegImm extends Definition("DepbarRegImm", "DEPBAR", Vec("DEPBAR_LE", "depbar__LE")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(sbidx), InstructionOperand.Immediate(cnt)) =>
          Some(Instruction.DepbarRegImm(sbidx, cnt, modifiers, guard))
        case _ => None
      }
  }
  object DepbarRegImmGroup extends Definition("DepbarRegImmGroup", "DEPBAR", Vec("DEPBAR_LE", "depbar__LE")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(sbidx),
              InstructionOperand.Immediate(cnt),
              InstructionOperand.Group(dep_scbd)
            ) => Some(Instruction.DepbarRegImmGroup(sbidx, cnt, dep_scbd, modifiers, guard))
        case _ => None
      }
  }
  object DepbarRegImmImm extends Definition("DepbarRegImmImm", "DEPBAR", Vec("DEPBAR_LE", "depbar__LE")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(sbidx),
              InstructionOperand.Immediate(cnt),
              InstructionOperand.Immediate(dep_scbd)
            ) => Some(Instruction.DepbarRegImmImm(sbidx, cnt, dep_scbd, modifiers, guard))
        case _ => None
      }
  }
  object DepbarRegReg extends Definition("DepbarRegReg", "DEPBAR", Vec("depbar_ur_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(sbidx), InstructionOperand.Register(srcB)) =>
          Some(Instruction.DepbarRegReg(sbidx, srcB, modifiers, guard))
        case _ => None
      }
  }
  object DepbarRegRegGroup extends Definition("DepbarRegRegGroup", "DEPBAR", Vec("depbar_ur_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(sbidx),
              InstructionOperand.Register(srcB),
              InstructionOperand.Group(scoreboard_list)
            ) => Some(Instruction.DepbarRegRegGroup(sbidx, srcB, scoreboard_list, modifiers, guard))
        case _ => None
      }
  }
  object DepbarRegRegImm extends Definition("DepbarRegRegImm", "DEPBAR", Vec("depbar_ur_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(sbidx),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(scoreboard_list)
            ) => Some(Instruction.DepbarRegRegImm(sbidx, srcB, scoreboard_list, modifiers, guard))
        case _ => None
      }
  }
  object DfmaRegRegAddressImmReg extends Definition("DfmaRegRegAddressImmReg", "DFMA", Vec("dfma__RCxR_RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.DfmaRegRegAddressImmReg(dst, srcA, srcB, sb_offset, srcC, modifiers, guard))
        case _ => None
      }
  }
  object DfmaRegRegConstReg
      extends Definition("DfmaRegRegConstReg", "DFMA", Vec("Const DFMA", "Const_DFMA", "dfma__RCR_RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.DfmaRegRegConstReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object DfmaRegRegImmReg
      extends Definition("DfmaRegRegImmReg", "DFMA", Vec("Imm DFMA", "Imm_DFMA", "dfma__RsIR_RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.DfmaRegRegImmReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object DfmaRegRegRegAddressImm extends Definition("DfmaRegRegRegAddressImm", "DFMA", Vec("dfma__RRCx_RRCx")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Address(srcC),
              InstructionOperand.Immediate(sc_offset)
            ) => Some(Instruction.DfmaRegRegRegAddressImm(dst, srcA, srcB, srcC, sc_offset, modifiers, guard))
        case _ => None
      }
  }
  object DfmaRegRegRegConst
      extends Definition("DfmaRegRegRegConst", "DFMA", Vec("Const1 DFMA", "Const1_DFMA", "dfma__RRC_RRC")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(sc)
            ) => Some(Instruction.DfmaRegRegRegConst(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object DfmaRegRegRegImm extends Definition("DfmaRegRegRegImm", "DFMA", Vec("dfma__RRsI_RRI")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.DfmaRegRegRegImm(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object DfmaRegRegRegReg
      extends Definition("DfmaRegRegRegReg", "DFMA", Vec("DFMA", "dfma__RRR_RRR", "dfma__RRU_RRU", "dfma__RUR_RUR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.DfmaRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object DmmaRegRegRegReg extends Definition("DmmaRegRegRegReg", "DMMA", Vec("dmma_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.DmmaRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object DmmaRegRegRegRegPred extends Definition("DmmaRegRegRegRegPred", "DMMA", Vec("dmma_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.DmmaRegRegRegRegPred(dst, srcA, srcB, srcC, uPp, modifiers, guard))
        case _ => None
      }
  }
  object DmnmxRegRegConstPred extends Definition("DmnmxRegRegConstPred", "DMNMX", Vec("Const DMNMX", "Const_DMNMX")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.DmnmxRegRegConstPred(dst, srcA, srcConst, pa, modifiers, guard))
        case _ => None
      }
  }
  object DmnmxRegRegImmPred extends Definition("DmnmxRegRegImmPred", "DMNMX", Vec("Imm DMNMX", "Imm_DMNMX")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.DmnmxRegRegImmPred(dst, srcA, uImm, pa, modifiers, guard))
        case _ => None
      }
  }
  object DmnmxRegRegRegPred extends Definition("DmnmxRegRegRegPred", "DMNMX", Vec("DMNMX")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.DmnmxRegRegRegPred(dst, srcA, srcB, pa, modifiers, guard))
        case _ => None
      }
  }
  object DmulRegRegAddressImm extends Definition("DmulRegRegAddressImm", "DMUL", Vec("dmul__RCxR_RCx")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.DmulRegRegAddressImm(dst, srcA, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object DmulRegRegConst
      extends Definition("DmulRegRegConst", "DMUL", Vec("Const DMUL", "Const_DMUL", "dmul__RCR_RC")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb)
            ) => Some(Instruction.DmulRegRegConst(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object DmulRegRegImm extends Definition("DmulRegRegImm", "DMUL", Vec("Imm DMUL", "Imm_DMUL", "dmul__RsIR_RI")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.DmulRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object DmulRegRegReg extends Definition("DmulRegRegReg", "DMUL", Vec("DMUL", "dmul__RRR_RR", "dmul__RUR_RU")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.DmulRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object DsetRegRegConst extends Definition("DsetRegRegConst", "DSET", Vec("NoBop Const DSET", "NoBop_Const_DSET")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.DsetRegRegConst(dst, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object DsetRegRegConstPred extends Definition("DsetRegRegConstPred", "DSET", Vec("Const DSET", "Const_DSET")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.DsetRegRegConstPred(dst, srcA, srcConst, pa, modifiers, guard))
        case _ => None
      }
  }
  object DsetRegRegImm extends Definition("DsetRegRegImm", "DSET", Vec("NoBop Imm DSET", "NoBop_Imm_DSET")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.DsetRegRegImm(dst, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object DsetRegRegImmPred extends Definition("DsetRegRegImmPred", "DSET", Vec("Imm DSET", "Imm_DSET")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.DsetRegRegImmPred(dst, srcA, uImm, pa, modifiers, guard))
        case _ => None
      }
  }
  object DsetRegRegReg extends Definition("DsetRegRegReg", "DSET", Vec("NoBop DSET", "NoBop_DSET")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.DsetRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object DsetRegRegRegPred extends Definition("DsetRegRegRegPred", "DSET", Vec("DSET")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.DsetRegRegRegPred(dst, srcA, srcB, pa, modifiers, guard))
        case _ => None
      }
  }
  object DsetpPredPredRegAddressImmPred
      extends Definition("DsetpPredPredRegAddressImmPred", "DSETP", Vec("dsetp__RRCx_RCx")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcC),
              InstructionOperand.Immediate(sc_offset),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.DsetpPredPredRegAddressImmPred(pu, pv, srcA, srcC, sc_offset, pp, modifiers, guard))
        case _ => None
      }
  }
  object DsetpPredPredRegConstPred
      extends Definition("DsetpPredPredRegConstPred", "DSETP", Vec("Const DSETP", "Const_DSETP", "dsetp__RRC_RC")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.DsetpPredPredRegConstPred(dstPredicate, nPd, srcA, srcConst, pa, modifiers, guard))
        case _ => None
      }
  }
  object DsetpPredPredRegImmPred
      extends Definition("DsetpPredPredRegImmPred", "DSETP", Vec("Imm DSETP", "Imm_DSETP", "dsetp__RRsI_RI")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.DsetpPredPredRegImmPred(dstPredicate, nPd, srcA, uImm, pa, modifiers, guard))
        case _ => None
      }
  }
  object DsetpPredPredRegRegPred
      extends Definition("DsetpPredPredRegRegPred", "DSETP", Vec("DSETP", "dsetp__RRR_RR", "dsetp__RRU_RU")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.DsetpPredPredRegRegPred(dstPredicate, nPd, srcA, srcB, pa, modifiers, guard))
        case _ => None
      }
  }
  object DsetpPredRegAddressImm extends Definition("DsetpPredRegAddressImm", "DSETP", Vec("dsetp_simple__RRCx_RCx")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcC),
              InstructionOperand.Immediate(sc_offset)
            ) => Some(Instruction.DsetpPredRegAddressImm(pu, srcA, srcC, sc_offset, modifiers, guard))
        case _ => None
      }
  }
  object DsetpPredRegConst extends Definition(
        "DsetpPredRegConst",
        "DSETP",
        Vec("NoBop Const DSETP", "NoBop_Const_DSETP", "dsetp_simple__RRC_RC")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.DsetpPredRegConst(dstPredicate, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object DsetpPredRegImm extends Definition(
        "DsetpPredRegImm",
        "DSETP",
        Vec("NoBop Imm DSETP", "NoBop_Imm_DSETP", "dsetp_simple__RRsI_RI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.DsetpPredRegImm(dstPredicate, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object DsetpPredRegReg extends Definition(
        "DsetpPredRegReg",
        "DSETP",
        Vec("NoBop DSETP", "NoBop_DSETP", "dsetp_simple__RRR_RR", "dsetp_simple__RRU_RU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.DsetpPredRegReg(dstPredicate, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object ElectPredReg extends Definition("ElectPredReg", "ELECT", Vec("elect_noURa_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pu), InstructionOperand.Register(dst)) =>
          Some(Instruction.ElectPredReg(pu, dst, modifiers, guard))
        case _ => None
      }
  }
  object ElectPredRegPred extends Definition("ElectPredRegPred", "ELECT", Vec("elect_Pp_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.ElectPredRegPred(pu, dst, pp, modifiers, guard))
        case _ => None
      }
  }
  object ElectPredRegReg extends Definition("ElectPredRegReg", "ELECT", Vec("elect_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA)
            ) => Some(Instruction.ElectPredRegReg(pu, dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object EndcollectiveNoOperands extends Definition("EndcollectiveNoOperands", "ENDCOLLECTIVE", Vec("endcollective_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.EndcollectiveNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object EndcollectivePred extends Definition("EndcollectivePred", "ENDCOLLECTIVE", Vec("endcollective_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp)) => Some(Instruction.EndcollectivePred(pp, modifiers, guard))
        case _ => None
      }
  }
  object ErrbarNoOperands extends Definition("ErrbarNoOperands", "ERRBAR", Vec("errbar_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.ErrbarNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object ExitNoOperands extends Definition("ExitNoOperands", "EXIT", Vec("EXIT", "exit_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.ExitNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object ExitPred extends Definition("ExitPred", "EXIT", Vec("exit_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp)) => Some(Instruction.ExitPred(pp, modifiers, guard))
        case _ => None
      }
  }
  object F2fRegAddressImm extends Definition(
        "F2fRegAddressImm",
        "F2F",
        Vec(
          "f2f_f32_downconvert__RCXR_RCXR",
          "f2f_f32_upconvert__RCXR_RCXR",
          "f2f_f32_upconvert_swap__RCXR_RCXR",
          "f2f_f64_downconvert__RCXR_RCXR",
          "f2f_f64_upconvert__R_CX16_R_RCXR",
          "f2f_f64_upconvert__R_CX32_R_RCXR",
          "f2f_f64_upconvert_swap__R_CX16_R_RCXR",
          "f2f_f64_upconvert_swap__R_CX32_R_RCXR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.F2fRegAddressImm(dst, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object F2fRegConst extends Definition(
        "F2fRegConst",
        "F2F",
        Vec(
          "Const F2F 1",
          "Const F2F 1 16",
          "Const F2F 1 64",
          "Const F2F 2",
          "Const F2F 2 16",
          "Const F2F 2 64",
          "Const_F2F_1",
          "Const_F2F_1_16",
          "Const_F2F_1_64",
          "Const_F2F_2",
          "Const_F2F_2_16",
          "Const_F2F_2_64",
          "Const_F2F_2_64_16",
          "Const_F2F_2_64_32",
          "f2f_f32_downconvert__RCR_RCR",
          "f2f_f32_upconvert__RCR_RCR",
          "f2f_f32_upconvert_swap__RCR_RCR",
          "f2f_f64_downconvert__RCR_RCR",
          "f2f_f64_upconvert__R_C16_R_RCR",
          "f2f_f64_upconvert__R_C32_R_RCR",
          "f2f_f64_upconvert_swap__R_C16_R_RCR",
          "f2f_f64_upconvert_swap__R_C32_R_RCR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(sb)) =>
          Some(Instruction.F2fRegConst(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object F2fRegImm extends Definition(
        "F2fRegImm",
        "F2F",
        Vec(
          "Imm F2F 1",
          "Imm F2F 1 16",
          "Imm F2F 1 64",
          "Imm F2F 2",
          "Imm F2F 2 16",
          "Imm F2F 2 64",
          "Imm_F2F_1",
          "Imm_F2F_1_16",
          "Imm_F2F_1_64",
          "Imm_F2F_2",
          "Imm_F2F_2_16",
          "Imm_F2F_2_64",
          "Imm_F2F_2_64_16",
          "Imm_F2F_2_64_32",
          "f2f_f32_downconvert__RIR_RIR",
          "f2f_f32_upconvert__RIR_RIR",
          "f2f_f32_upconvert_swap__RIR_RIR",
          "f2f_f64_downconvert__RIR_RIR",
          "f2f_f64_upconvert__R_32I_R_RIR",
          "f2f_f64_upconvert__R_I16_R_RIR",
          "f2f_f64_upconvert_swap__R_32I_R_RIR",
          "f2f_f64_upconvert_swap__R_I16_R_RIR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.F2fRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object F2fRegReg extends Definition(
        "F2fRegReg",
        "F2F",
        Vec(
          "F2F 1",
          "F2F 1 16",
          "F2F 1 64",
          "F2F 2",
          "F2F 2 16",
          "F2F 2 64",
          "F2F_1",
          "F2F_1_16",
          "F2F_1_64",
          "F2F_2",
          "F2F_2_16",
          "F2F_2_64",
          "F2F_2_64_16",
          "F2F_2_64_32",
          "f2f_f32_downconvert__RRR_RRR",
          "f2f_f32_downconvert__RUR_RUR",
          "f2f_f32_upconvert__RRR_RRR",
          "f2f_f32_upconvert__RUR_RUR",
          "f2f_f32_upconvert_swap__RRR_RRR",
          "f2f_f32_upconvert_swap__RUR_RUR",
          "f2f_f64_downconvert__RRR_RRR",
          "f2f_f64_downconvert__RUR_RUR",
          "f2f_f64_upconvert__R_R16_R_RRR",
          "f2f_f64_upconvert__R_R32_R_RRR",
          "f2f_f64_upconvert__R_UR16_R_RUR",
          "f2f_f64_upconvert__R_UR32_R_RUR",
          "f2f_f64_upconvert_swap__R_R16_R_RRR",
          "f2f_f64_upconvert_swap__R_R32_R_RRR",
          "f2f_f64_upconvert_swap__R_UR16_R_RUR",
          "f2f_f64_upconvert_swap__R_UR32_R_RUR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.F2fRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object F2fpRegAddressImm extends Definition(
        "F2fpRegAddressImm",
        "F2FP",
        Vec("f2fp_8b_upconvert__RCxR", "f2fp_tf32__RCxR", "f2fp_tf32_default_src__RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.F2fpRegAddressImm(dst, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object F2fpRegAddressImmReg extends Definition(
        "F2fpRegAddressImmReg",
        "F2FP",
        Vec("f2fp_f16_to_8b_downconvert__RCxR", "f2fp_merge_c__RCxR", "f2fp_merge_c_default_dst_src__RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.F2fpRegAddressImmReg(dst, srcB, sb_offset, srcC, modifiers, guard))
        case _ => None
      }
  }
  object F2fpRegConst extends Definition(
        "F2fpRegConst",
        "F2FP",
        Vec("f2fp_8b_upconvert__RCR", "f2fp_tf32__RCR", "f2fp_tf32_default_src__RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(sb)) =>
          Some(Instruction.F2fpRegConst(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object F2fpRegConstReg extends Definition(
        "F2fpRegConstReg",
        "F2FP",
        Vec("f2fp_f16_to_8b_downconvert__RCR", "f2fp_merge_c__RCR", "f2fp_merge_c_default_dst_src__RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.F2fpRegConstReg(dst, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object F2fpRegImm extends Definition(
        "F2fpRegImm",
        "F2FP",
        Vec(
          "f2fp_4b_upconvert__RIR",
          "f2fp_8b_upconvert__RIR",
          "f2fp_E8_upconvert__RIR",
          "f2fp_tf32__RIR",
          "f2fp_tf32_default_src__RIR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.F2fpRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object F2fpRegImmReg extends Definition(
        "F2fpRegImmReg",
        "F2FP",
        Vec(
          "f2fp_4b_upconvert_scale__RIR",
          "f2fp_8b_upconvert_scale__RIR",
          "f2fp_f16_to_4b_downconvert__RIR",
          "f2fp_f16_to_8b_downconvert__RIR",
          "f2fp_f16_to_mx8_downconvert_scale__RIR",
          "f2fp_merge_c__RIR",
          "f2fp_merge_c_default_dst_src__RIR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.F2fpRegImmReg(dst, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object F2fpRegReg extends Definition(
        "F2fpRegReg",
        "F2FP",
        Vec(
          "f2fp_4b_upconvert__RRR",
          "f2fp_4b_upconvert__RUR",
          "f2fp_8b_upconvert__RRR",
          "f2fp_8b_upconvert__RUR",
          "f2fp_E8_upconvert__RRR",
          "f2fp_E8_upconvert__RUR",
          "f2fp_tf32__RRR",
          "f2fp_tf32__RUR",
          "f2fp_tf32_default_src__RRR",
          "f2fp_tf32_default_src__RUR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.F2fpRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object F2fpRegRegAddressImm extends Definition(
        "F2fpRegRegAddressImm",
        "F2FP",
        Vec(
          "f2fp__RCxR",
          "f2fp_default_dst_src__RCxR",
          "f2fp_f16_to_8b_downconvert__RRCx",
          "f2fp_merge_c__RRCx",
          "f2fp_merge_c_default_dst_src__RRCx"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.F2fpRegRegAddressImm(dst, srcA, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object F2fpRegRegAddressImmReg
      extends Definition("F2fpRegRegAddressImmReg", "F2FP", Vec("f2fp_f32_to_8b_downconvert__RCxR", "f2fp_rs__RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.F2fpRegRegAddressImmReg(dst, srcA, srcB, sb_offset, srcC, modifiers, guard))
        case _ => None
      }
  }
  object F2fpRegRegConst extends Definition(
        "F2fpRegRegConst",
        "F2FP",
        Vec(
          "f2fp__RCR",
          "f2fp_default_dst_src__RCR",
          "f2fp_f16_to_8b_downconvert__RRC",
          "f2fp_merge_c__RRC",
          "f2fp_merge_c_default_dst_src__RRC"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb)
            ) => Some(Instruction.F2fpRegRegConst(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object F2fpRegRegConstReg
      extends Definition("F2fpRegRegConstReg", "F2FP", Vec("f2fp_f32_to_8b_downconvert__RCR", "f2fp_rs__RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.F2fpRegRegConstReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object F2fpRegRegImm extends Definition(
        "F2fpRegRegImm",
        "F2FP",
        Vec(
          "f2fp_4b_upconvert_scale__RRI",
          "f2fp_8b_upconvert_scale__RRI",
          "f2fp__RIR",
          "f2fp_default_dst_src__RIR",
          "f2fp_f16_to_4b_downconvert__RRI",
          "f2fp_f16_to_8b_downconvert__RRI",
          "f2fp_f16_to_mx8_downconvert_scale__RRI",
          "f2fp_merge_c__RRI",
          "f2fp_merge_c_default_dst_src__RRI"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.F2fpRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object F2fpRegRegImmReg extends Definition(
        "F2fpRegRegImmReg",
        "F2FP",
        Vec(
          "f2fp_f32_to_4b_downconvert__RIR",
          "f2fp_f32_to_8b_downconvert__RIR",
          "f2fp_f32_to_mx8_downconvert_scale__RIR",
          "f2fp_rs_16b__RIR",
          "f2fp_rs_8b4b__RIR",
          "f2fp_rs__RIR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.F2fpRegRegImmReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object F2fpRegRegReg extends Definition(
        "F2fpRegRegReg",
        "F2FP",
        Vec(
          "f2fp_4b_upconvert_scale__RRR",
          "f2fp_4b_upconvert_scale__RRU",
          "f2fp_4b_upconvert_scale__RUR",
          "f2fp_8b_upconvert_scale__RRR",
          "f2fp_8b_upconvert_scale__RRU",
          "f2fp_8b_upconvert_scale__RUR",
          "f2fp__RRR",
          "f2fp__RUR",
          "f2fp_default_dst_src__RRR",
          "f2fp_default_dst_src__RUR",
          "f2fp_f16_to_4b_downconvert__RRR",
          "f2fp_f16_to_4b_downconvert__RRU",
          "f2fp_f16_to_4b_downconvert__RUR",
          "f2fp_f16_to_8b_downconvert__RRR",
          "f2fp_f16_to_8b_downconvert__RRU",
          "f2fp_f16_to_8b_downconvert__RUR",
          "f2fp_f16_to_mx8_downconvert_scale__RRR",
          "f2fp_f16_to_mx8_downconvert_scale__RRU",
          "f2fp_f16_to_mx8_downconvert_scale__RUR",
          "f2fp_merge_c__RRR",
          "f2fp_merge_c__RRU",
          "f2fp_merge_c__RUR",
          "f2fp_merge_c_default_dst_src__RRR",
          "f2fp_merge_c_default_dst_src__RRU",
          "f2fp_merge_c_default_dst_src__RUR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.F2fpRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object F2fpRegRegRegAddressImm
      extends Definition("F2fpRegRegRegAddressImm", "F2FP", Vec("f2fp_f32_to_8b_downconvert__RRCx", "f2fp_rs__RRCx")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Address(srcC),
              InstructionOperand.Immediate(sc_offset)
            ) => Some(Instruction.F2fpRegRegRegAddressImm(dst, srcA, srcB, srcC, sc_offset, modifiers, guard))
        case _ => None
      }
  }
  object F2fpRegRegRegConst
      extends Definition("F2fpRegRegRegConst", "F2FP", Vec("f2fp_f32_to_8b_downconvert__RRC", "f2fp_rs__RRC")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(sc)
            ) => Some(Instruction.F2fpRegRegRegConst(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object F2fpRegRegRegImm extends Definition(
        "F2fpRegRegRegImm",
        "F2FP",
        Vec(
          "f2fp_f32_to_4b_downconvert__RRI",
          "f2fp_f32_to_8b_downconvert__RRI",
          "f2fp_f32_to_mx8_downconvert_scale__RRI",
          "f2fp_rs_16b__RRI",
          "f2fp_rs_8b4b__RRI",
          "f2fp_rs__RRI"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.F2fpRegRegRegImm(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object F2fpRegRegRegReg extends Definition(
        "F2fpRegRegRegReg",
        "F2FP",
        Vec(
          "f2fp_f32_to_4b_downconvert__RRR",
          "f2fp_f32_to_4b_downconvert__RRU",
          "f2fp_f32_to_4b_downconvert__RUR",
          "f2fp_f32_to_8b_downconvert__RRR",
          "f2fp_f32_to_8b_downconvert__RRU",
          "f2fp_f32_to_8b_downconvert__RUR",
          "f2fp_f32_to_mx8_downconvert_scale__RRR",
          "f2fp_f32_to_mx8_downconvert_scale__RRU",
          "f2fp_f32_to_mx8_downconvert_scale__RUR",
          "f2fp_rs_16b__RRR",
          "f2fp_rs_16b__RRU",
          "f2fp_rs_16b__RUR",
          "f2fp_rs_8b4b__RRR",
          "f2fp_rs_8b4b__RRU",
          "f2fp_rs_8b4b__RUR",
          "f2fp_rs__RRR",
          "f2fp_rs__RRU",
          "f2fp_rs__RUR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.F2fpRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object F2iRegAddressImm extends Definition(
        "F2iRegAddressImm",
        "F2I",
        Vec(
          "f2i_Rd64__CXb_16b",
          "f2i_Rd64__CXb_32b",
          "f2i_Rd64__CXb_64b",
          "f2i_Rd64__CXb_bf16",
          "f2i_Rd64_swap__CXb_16b",
          "f2i_Rd64_swap__CXb_32b",
          "f2i_Rd64_swap__CXb_64b",
          "f2i_Rd64_swap__CXb_bf16",
          "f2i__CXb_16b",
          "f2i__CXb_32b",
          "f2i__CXb_64b",
          "f2i__CXb_bf16",
          "f2i_swap__CXb_16b",
          "f2i_swap__CXb_32b",
          "f2i_swap__CXb_64b",
          "f2i_swap__CXb_bf16"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.F2iRegAddressImm(dst, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object F2iRegConst extends Definition(
        "F2iRegConst",
        "F2I",
        Vec(
          "Const F2I",
          "Const F2I 16",
          "Const F2I 64",
          "Const_F2I",
          "Const_F2I_16",
          "Const_F2I_64",
          "Const_F2I_I64",
          "f2i_Rd64__Cb_16b",
          "f2i_Rd64__Cb_32b",
          "f2i_Rd64__Cb_64b",
          "f2i_Rd64__Cb_bf16",
          "f2i_Rd64_swap__Cb_16b",
          "f2i_Rd64_swap__Cb_32b",
          "f2i_Rd64_swap__Cb_64b",
          "f2i_Rd64_swap__Cb_bf16",
          "f2i__Cb_16b",
          "f2i__Cb_32b",
          "f2i__Cb_64b",
          "f2i__Cb_bf16",
          "f2i_swap__Cb_16b",
          "f2i_swap__Cb_32b",
          "f2i_swap__Cb_64b",
          "f2i_swap__Cb_bf16"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(sb)) =>
          Some(Instruction.F2iRegConst(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object F2iRegImm extends Definition(
        "F2iRegImm",
        "F2I",
        Vec(
          "Imm F2I",
          "Imm F2I 16",
          "Imm F2I 64",
          "Imm_F2I",
          "Imm_F2I_16",
          "Imm_F2I_64",
          "Imm_F2I_I64",
          "f2i_Rd64__IU_32b",
          "f2i_Rd64__Ib_16b",
          "f2i_Rd64__Ib_64b",
          "f2i_Rd64__Ib_bf16",
          "f2i_Rd64_swap__IU_32b",
          "f2i_Rd64_swap__Ib_16b",
          "f2i_Rd64_swap__Ib_64b",
          "f2i_Rd64_swap__Ib_bf16",
          "f2i__IU_32b",
          "f2i__Ib_16b",
          "f2i__Ib_64b",
          "f2i__Ib_bf16",
          "f2i_swap__IU_32b",
          "f2i_swap__Ib_16b",
          "f2i_swap__Ib_64b",
          "f2i_swap__Ib_bf16"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.F2iRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object F2iRegReg extends Definition(
        "F2iRegReg",
        "F2I",
        Vec(
          "F2I",
          "F2I 16",
          "F2I 64",
          "F2I_16",
          "F2I_64",
          "F2I_I64",
          "f2i_Rd64__Rb_16b",
          "f2i_Rd64__Rb_32b",
          "f2i_Rd64__Rb_64b",
          "f2i_Rd64__Rb_bf16",
          "f2i_Rd64__URb_16b",
          "f2i_Rd64__URb_32b",
          "f2i_Rd64__URb_64b",
          "f2i_Rd64__URb_bf16",
          "f2i_Rd64_swap__Rb_16b",
          "f2i_Rd64_swap__Rb_32b",
          "f2i_Rd64_swap__Rb_64b",
          "f2i_Rd64_swap__Rb_bf16",
          "f2i_Rd64_swap__URb_16b",
          "f2i_Rd64_swap__URb_32b",
          "f2i_Rd64_swap__URb_64b",
          "f2i_Rd64_swap__URb_bf16",
          "f2i__Rb_16b",
          "f2i__Rb_32b",
          "f2i__Rb_64b",
          "f2i__Rb_bf16",
          "f2i__URb_16b",
          "f2i__URb_32b",
          "f2i__URb_64b",
          "f2i__URb_bf16",
          "f2i_swap__Rb_16b",
          "f2i_swap__Rb_32b",
          "f2i_swap__Rb_64b",
          "f2i_swap__Rb_bf16",
          "f2i_swap__URb_16b",
          "f2i_swap__URb_32b",
          "f2i_swap__URb_64b",
          "f2i_swap__URb_bf16"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.F2iRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object F2ipRegRegAddressImmReg extends Definition("F2ipRegRegAddressImmReg", "F2IP", Vec("f2ip__RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.F2ipRegRegAddressImmReg(dst, srcA, srcB, sb_offset, srcC, modifiers, guard))
        case _ => None
      }
  }
  object F2ipRegRegConstReg extends Definition("F2ipRegRegConstReg", "F2IP", Vec("f2ip__RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.F2ipRegRegConstReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object F2ipRegRegImmReg extends Definition("F2ipRegRegImmReg", "F2IP", Vec("f2ip__RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.F2ipRegRegImmReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object F2ipRegRegRegAddressImm extends Definition("F2ipRegRegRegAddressImm", "F2IP", Vec("f2ip__RRCx")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Address(srcC),
              InstructionOperand.Immediate(sc_offset)
            ) => Some(Instruction.F2ipRegRegRegAddressImm(dst, srcA, srcB, srcC, sc_offset, modifiers, guard))
        case _ => None
      }
  }
  object F2ipRegRegRegConst extends Definition("F2ipRegRegRegConst", "F2IP", Vec("f2ip__RRC")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(sc)
            ) => Some(Instruction.F2ipRegRegRegConst(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object F2ipRegRegRegImm extends Definition("F2ipRegRegRegImm", "F2IP", Vec("f2ip__RRI")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.F2ipRegRegRegImm(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object F2ipRegRegRegReg extends Definition("F2ipRegRegRegReg", "F2IP", Vec("f2ip__RRR", "f2ip__RRU", "f2ip__RUR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.F2ipRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FaddRegRegAddressImm extends Definition("FaddRegRegAddressImm", "FADD", Vec("fadd__RRCx_RCx")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcC),
              InstructionOperand.Immediate(sc_offset)
            ) => Some(Instruction.FaddRegRegAddressImm(dst, srcA, srcC, sc_offset, modifiers, guard))
        case _ => None
      }
  }
  object FaddRegRegConst
      extends Definition("FaddRegRegConst", "FADD", Vec("Const FADD", "Const_FADD", "fadd__RRC_RC")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sc)
            ) => Some(Instruction.FaddRegRegConst(dst, srcA, sc, modifiers, guard))
        case _ => None
      }
  }
  object FaddRegRegImm extends Definition("FaddRegRegImm", "FADD", Vec("Imm FADD", "Imm_FADD", "fadd__RRI_RI")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.FaddRegRegImm(dst, srcA, sc, modifiers, guard))
        case _ => None
      }
  }
  object FaddRegRegReg extends Definition("FaddRegRegReg", "FADD", Vec("FADD", "fadd__RRR_RR", "fadd__RRU_RU")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.FaddRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object Fadd2RegRegImm extends Definition("Fadd2RegRegImm", "FADD2", Vec("fadd2_imm__RRI")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.Fadd2RegRegImm(dst, srcA, sc, modifiers, guard))
        case _ => None
      }
  }
  object Fadd2RegRegReg extends Definition("Fadd2RegRegReg", "FADD2", Vec("fadd2_rc__RRR", "fadd2_urc__RRU")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Fadd2RegRegReg(dst, srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Fadd32iRegRegImm extends Definition("Fadd32iRegRegImm", "FADD32I", Vec("FADD32I", "fadd32i_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.Fadd32iRegRegImm(dst, srcA, sc, modifiers, guard))
        case _ => None
      }
  }
  object FccoPredPredRegConstReg extends Definition("FccoPredPredRegConstReg", "FCCO", Vec("Const FCCO")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.FccoPredPredRegConstReg(dstPredicate, nPd, srcA, srcConst, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FccoPredPredRegImmReg extends Definition("FccoPredPredRegImmReg", "FCCO", Vec("Imm FCCO")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.FccoPredPredRegImmReg(dstPredicate, nPd, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FccoPredPredRegRegConst extends Definition("FccoPredPredRegRegConst", "FCCO", Vec("Const1 FCCO")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.FccoPredPredRegRegConst(dstPredicate, nPd, srcA, srcB, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object FccoPredPredRegRegReg extends Definition("FccoPredPredRegRegReg", "FCCO", Vec("FCCO")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.FccoPredPredRegRegReg(dstPredicate, nPd, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FccoPredRegConstReg extends Definition("FccoPredRegConstReg", "FCCO", Vec("Const FCCO")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.FccoPredRegConstReg(dstPredicate, srcA, srcConst, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FccoPredRegImmReg extends Definition("FccoPredRegImmReg", "FCCO", Vec("Imm FCCO")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.FccoPredRegImmReg(dstPredicate, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FccoPredRegRegConst extends Definition("FccoPredRegRegConst", "FCCO", Vec("Const1 FCCO")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.FccoPredRegRegConst(dstPredicate, srcA, srcB, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object FccoPredRegRegReg extends Definition("FccoPredRegRegReg", "FCCO", Vec("FCCO")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.FccoPredRegRegReg(dstPredicate, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FchkPredRegAddressImm extends Definition("FchkPredRegAddressImm", "FCHK", Vec("fchk__RCxR_RCx")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.FchkPredRegAddressImm(pu, srcA, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object FchkPredRegConst
      extends Definition("FchkPredRegConst", "FCHK", Vec("Const FCHK", "Const_FCHK", "fchk__RCR_RC")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.FchkPredRegConst(dstPredicate, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object FchkPredRegImm extends Definition("FchkPredRegImm", "FCHK", Vec("Imm FCHK", "Imm_FCHK", "fchk__RIR_RI")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.FchkPredRegImm(dstPredicate, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object FchkPredRegReg extends Definition("FchkPredRegReg", "FCHK", Vec("FCHK", "fchk__RRR_RR", "fchk__RUR_RU")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.FchkPredRegReg(dstPredicate, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object FcmpRegRegConstReg extends Definition("FcmpRegRegConstReg", "FCMP", Vec("Const FCMP", "Const_FCMP")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.FcmpRegRegConstReg(dst, srcA, srcConst, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FcmpRegRegImmReg extends Definition("FcmpRegRegImmReg", "FCMP", Vec("Imm FCMP", "Imm_FCMP")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.FcmpRegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object FcmpRegRegRegConst extends Definition("FcmpRegRegRegConst", "FCMP", Vec("Const1 FCMP", "Const1_FCMP")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.FcmpRegRegRegConst(dst, srcA, srcB, srcConst, modifiers, guard))
        case _ => None
      }
  }
  val definitions: Vec[Definition] = Vec(
    AcqbulkNoOperands,
    AcqshminitNoOperands,
    Al2pPredRegReg,
    Al2pPredRegRegImm,
    Al2pRegReg,
    Al2pRegRegImm,
    AldRegAddressReg,
    ArrivesAddress,
    AstAddressRegReg,
    AtomPredRegAddress,
    AtomPredRegAddressReg,
    AtomPredRegAddressRegReg,
    AtomRegAddressReg,
    AtomRegAddressRegReg,
    AtomgPredRegAddressReg,
    AtomgPredRegAddressRegReg,
    AtomsPredAddressRegReg,
    AtomsRegAddress,
    AtomsRegAddressReg,
    AtomsRegAddressRegReg,
    B2rReg,
    B2rRegImm,
    B2rRegPred,
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
    BfeRegRegConst,
    BfeRegRegImm,
    BfeRegRegReg,
    BfiRegRegConstReg,
    BfiRegRegImmReg,
    BfiRegRegRegConst,
    BfiRegRegRegReg,
    BgmmaRegAddressReg,
    BgmmaRegAddressRegPred,
    BgmmaRegAddressRegReg,
    BgmmaRegAddressRegRegPred,
    BgmmaRegRegAddressReg,
    BgmmaRegRegAddressRegPred,
    BmmaRegRegRegReg,
    BmmaRegRegRegRegPred,
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
    BmskRegRegAddressImm,
    BmskRegRegConst,
    BmskRegRegImm,
    BmskRegRegReg,
    BptNoOperands,
    BptImm,
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
    BreakPredReg,
    BreakReg,
    BrevRegAddressImm,
    BrevRegConst,
    BrevRegImm,
    BrevRegReg,
    BrkNoOperands,
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
    BssyPredRegImm,
    BssyRegImm,
    BsyncPredReg,
    BsyncReg,
    CalConst,
    CalImm,
    CallConst,
    CallImm,
    CallPredConst,
    CallPredImm,
    CallPredReg,
    CallPredRegImm,
    CallReg,
    CallRegImm,
    CctlNoOperands,
    CctlAddress,
    CctlAddressAddress,
    CctlAddressImm,
    CctlConst,
    CctlPredAddress,
    CctlRegAddress,
    CctllNoOperands,
    CctllAddress,
    CctllRegAddress,
    CctlsNoOperands,
    CctltNoOperands,
    CctltImm,
    CctltImmImm,
    CctltReg,
    CgaerrbarNoOperands,
    ClmadRegRegAddressImmReg,
    ClmadRegRegConstReg,
    ClmadRegRegRegAddressImm,
    ClmadRegRegRegConst,
    ClmadRegRegRegReg,
    ContNoOperands,
    CreduxRegReg,
    Cs2rRegSpecial,
    Cs2urRegSpecial,
    CsetRegSymbol,
    CsetRegSymbolPred,
    CsetpPredPredSymbolPred,
    CsetpPredSymbol,
    CsmtestImm,
    CsmtestPredImm,
    CsmtestPredPredImmPred,
    DaddRegRegAddressImm,
    DaddRegRegConst,
    DaddRegRegImm,
    DaddRegRegReg,
    DchkPredRegConst,
    DchkPredRegImm,
    DchkPredRegReg,
    DepbarNoOperands,
    DepbarGroup,
    DepbarImm,
    DepbarRegImm,
    DepbarRegImmGroup,
    DepbarRegImmImm,
    DepbarRegReg,
    DepbarRegRegGroup,
    DepbarRegRegImm,
    DfmaRegRegAddressImmReg,
    DfmaRegRegConstReg,
    DfmaRegRegImmReg,
    DfmaRegRegRegAddressImm,
    DfmaRegRegRegConst,
    DfmaRegRegRegImm,
    DfmaRegRegRegReg,
    DmmaRegRegRegReg,
    DmmaRegRegRegRegPred,
    DmnmxRegRegConstPred,
    DmnmxRegRegImmPred,
    DmnmxRegRegRegPred,
    DmulRegRegAddressImm,
    DmulRegRegConst,
    DmulRegRegImm,
    DmulRegRegReg,
    DsetRegRegConst,
    DsetRegRegConstPred,
    DsetRegRegImm,
    DsetRegRegImmPred,
    DsetRegRegReg,
    DsetRegRegRegPred,
    DsetpPredPredRegAddressImmPred,
    DsetpPredPredRegConstPred,
    DsetpPredPredRegImmPred,
    DsetpPredPredRegRegPred,
    DsetpPredRegAddressImm,
    DsetpPredRegConst,
    DsetpPredRegImm,
    DsetpPredRegReg,
    ElectPredReg,
    ElectPredRegPred,
    ElectPredRegReg,
    EndcollectiveNoOperands,
    EndcollectivePred,
    ErrbarNoOperands,
    ExitNoOperands,
    ExitPred,
    F2fRegAddressImm,
    F2fRegConst,
    F2fRegImm,
    F2fRegReg,
    F2fpRegAddressImm,
    F2fpRegAddressImmReg,
    F2fpRegConst,
    F2fpRegConstReg,
    F2fpRegImm,
    F2fpRegImmReg,
    F2fpRegReg,
    F2fpRegRegAddressImm,
    F2fpRegRegAddressImmReg,
    F2fpRegRegConst,
    F2fpRegRegConstReg,
    F2fpRegRegImm,
    F2fpRegRegImmReg,
    F2fpRegRegReg,
    F2fpRegRegRegAddressImm,
    F2fpRegRegRegConst,
    F2fpRegRegRegImm,
    F2fpRegRegRegReg,
    F2iRegAddressImm,
    F2iRegConst,
    F2iRegImm,
    F2iRegReg,
    F2ipRegRegAddressImmReg,
    F2ipRegRegConstReg,
    F2ipRegRegImmReg,
    F2ipRegRegRegAddressImm,
    F2ipRegRegRegConst,
    F2ipRegRegRegImm,
    F2ipRegRegRegReg,
    FaddRegRegAddressImm,
    FaddRegRegConst,
    FaddRegRegImm,
    FaddRegRegReg,
    Fadd2RegRegImm,
    Fadd2RegRegReg,
    Fadd32iRegRegImm,
    FccoPredPredRegConstReg,
    FccoPredPredRegImmReg,
    FccoPredPredRegRegConst,
    FccoPredPredRegRegReg,
    FccoPredRegConstReg,
    FccoPredRegImmReg,
    FccoPredRegRegConst,
    FccoPredRegRegReg,
    FchkPredRegAddressImm,
    FchkPredRegConst,
    FchkPredRegImm,
    FchkPredRegReg,
    FcmpRegRegConstReg,
    FcmpRegRegImmReg,
    FcmpRegRegRegConst
  )
}
