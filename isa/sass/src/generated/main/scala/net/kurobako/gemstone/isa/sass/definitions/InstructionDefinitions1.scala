// Generated SASS instruction definitions and checked operand constructors. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.sass
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import Instruction.*
@javax.annotation.processing.Generated(Array("sbt codegen"))
object InstructionDefinitions1 {
  object CallPredReg extends Definition(
        "CallPredReg",
        "CALL",
        Vec(Slot("pp", OperandKind.Predicate), Slot("srcA", OperandKind.Register)),
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
          Slot("pp", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("ra_offset", OperandKind.Immediate)
        ),
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
        Vec(Slot("srcA", OperandKind.Register)),
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
        Vec(Slot("srcA", OperandKind.Register), Slot("ra_offset", OperandKind.Immediate)),
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
  object CallcbuPipeConst extends Definition(
        "CallcbuPipeConst",
        "CALLcbu_pipe",
        Vec(Slot("sa", OperandKind.ConstantBank)),
        Vec("call_abs__RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(sa)) => Some(Instruction.CallcbuPipeConst(sa, modifiers, guard))
        case _ => None
      }
  }
  object CallcbuPipeImm extends Definition(
        "CallcbuPipeImm",
        "CALLcbu_pipe",
        Vec(Slot("sa", OperandKind.Immediate)),
        Vec("call_abs__RIR", "call_rel__RIR", "call_rel_imm__RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sa)) => Some(Instruction.CallcbuPipeImm(sa, modifiers, guard))
        case _ => None
      }
  }
  object CallcbuPipePredConst extends Definition(
        "CallcbuPipePredConst",
        "CALLcbu_pipe",
        Vec(Slot("pp", OperandKind.Predicate), Slot("sa", OperandKind.ConstantBank)),
        Vec("call_abs__RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.ConstantBank(sa)) =>
          Some(Instruction.CallcbuPipePredConst(pp, sa, modifiers, guard))
        case _ => None
      }
  }
  object CallcbuPipePredImm extends Definition(
        "CallcbuPipePredImm",
        "CALLcbu_pipe",
        Vec(Slot("pp", OperandKind.Predicate), Slot("sa", OperandKind.Immediate)),
        Vec("call_abs__RIR", "call_rel__RIR", "call_rel_imm__RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.Immediate(sa)) =>
          Some(Instruction.CallcbuPipePredImm(pp, sa, modifiers, guard))
        case _ => None
      }
  }
  object CallcbuPipePredReg extends Definition(
        "CallcbuPipePredReg",
        "CALLcbu_pipe",
        Vec(Slot("pp", OperandKind.Predicate), Slot("srcA", OperandKind.Register)),
        Vec("call_abs__RRR", "call_abs__URIR", "call_rel_reg_", "call_rel_reg__RRR", "call_rel_reg__URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.Register(srcA)) =>
          Some(Instruction.CallcbuPipePredReg(pp, srcA, modifiers, guard))
        case _ => None
      }
  }
  object CallcbuPipePredRegImm extends Definition(
        "CallcbuPipePredRegImm",
        "CALLcbu_pipe",
        Vec(
          Slot("pp", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("ra_offset", OperandKind.Immediate)
        ),
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
            ) => Some(Instruction.CallcbuPipePredRegImm(pp, srcA, ra_offset, modifiers, guard))
        case _ => None
      }
  }
  object CallcbuPipeReg extends Definition(
        "CallcbuPipeReg",
        "CALLcbu_pipe",
        Vec(Slot("srcA", OperandKind.Register)),
        Vec("call_abs__RRR", "call_abs__URIR", "call_rel_reg_", "call_rel_reg__RRR", "call_rel_reg__URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA)) => Some(Instruction.CallcbuPipeReg(srcA, modifiers, guard))
        case _ => None
      }
  }
  object CallcbuPipeRegImm extends Definition(
        "CallcbuPipeRegImm",
        "CALLcbu_pipe",
        Vec(Slot("srcA", OperandKind.Register), Slot("ra_offset", OperandKind.Immediate)),
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
          Some(Instruction.CallcbuPipeRegImm(srcA, ra_offset, modifiers, guard))
        case _ => None
      }
  }
  object CalbruPipeConst extends Definition(
        "CalbruPipeConst",
        "CALbru_pipe",
        Vec(Slot("srcConst", OperandKind.ConstantBank)),
        Vec("CAL c", "CAL_c")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(srcConst)) =>
          Some(Instruction.CalbruPipeConst(srcConst, modifiers, guard))
        case _ => None
      }
  }
  object CalbruPipeImm
      extends Definition("CalbruPipeImm", "CALbru_pipe", Vec(Slot("sImm", OperandKind.Immediate)), Vec("CAL")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sImm)) => Some(Instruction.CalbruPipeImm(sImm, modifiers, guard))
        case _ => None
      }
  }
  object CctlNoOperands extends Definition(
        "CctlNoOperands",
        "CCTL",
        Vec(),
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
        Vec(Slot("srcA", OperandKind.Address)),
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
        Vec(Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Address)),
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
        Vec(Slot("srcA", OperandKind.Address), Slot("sector_count", OperandKind.Immediate)),
        Vec("cctl__sImmOffset_rml2", "cctl__sUROffset_rml2", "cctl__uImmOffset_rml2", "cctl__uUROffset_rml2")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA), InstructionOperand.Immediate(sector_count)) =>
          Some(Instruction.CctlAddressImm(srcA, sector_count, modifiers, guard))
        case _ => None
      }
  }
  object CctlConst extends Definition(
        "CctlConst",
        "CCTL",
        Vec(Slot("sa", OperandKind.ConstantBank)),
        Vec("cctl_c_ldc_const_bound_", "cctl_c_ldcu_const_bound_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(sa)) => Some(Instruction.CctlConst(sa, modifiers, guard))
        case _ => None
      }
  }
  object CctlPredAddress extends Definition(
        "CctlPredAddress",
        "CCTL",
        Vec(Slot("pu", OperandKind.Predicate), Slot("srcA", OperandKind.Address)),
        Vec("cctl__sImmOffset_pf2_q", "cctl__sUROffset_pf2_q", "cctl__uImmOffset_pf2_q", "cctl__uUROffset_pf2_q")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pu), InstructionOperand.Address(srcA)) =>
          Some(Instruction.CctlPredAddress(pu, srcA, modifiers, guard))
        case _ => None
      }
  }
  object CctlRegAddress extends Definition(
        "CctlRegAddress",
        "CCTL",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address)),
        Vec("CCTL QRY", "CCTL_QRY", "I CCTL QRY", "I_CCTL_QRY")
      ) {
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
        Vec(),
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
        Vec(Slot("srcA", OperandKind.Address)),
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
  object CctllRegAddress extends Definition(
        "CctllRegAddress",
        "CCTLL",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address)),
        Vec("CCTLL QRY", "I CCTLL QRY")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Address(srcA)) =>
          Some(Instruction.CctllRegAddress(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object CctllaguPipeNoOperands
      extends Definition("CctllaguPipeNoOperands", "CCTLLagu_pipe", Vec(), Vec("CCTLL NoOperands")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.CctllaguPipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object CctllaguPipeAddress extends Definition(
        "CctllaguPipeAddress",
        "CCTLLagu_pipe",
        Vec(Slot("srcA", OperandKind.Address)),
        Vec("CCTLL", "CCTLL QRY", "I CCTLL", "I CCTLL QRY")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA)) => Some(Instruction.CctllaguPipeAddress(srcA, modifiers, guard))
        case _ => None
      }
  }
  object CctllaguPipeRegAddress extends Definition(
        "CctllaguPipeRegAddress",
        "CCTLLagu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address)),
        Vec("CCTLL QRY", "I CCTLL QRY")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Address(srcA)) =>
          Some(Instruction.CctllaguPipeRegAddress(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object CctllmioPipeNoOperands extends Definition(
        "CctllmioPipeNoOperands",
        "CCTLLmio_pipe",
        Vec(),
        Vec("CCTLL_CRS_WBALL", "CCTLL_IVALL", "cctll__IVALL_WBALL_D_U_noSrc")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.CctllmioPipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object CctllmioPipeAddress extends Definition(
        "CctllmioPipeAddress",
        "CCTLLmio_pipe",
        Vec(Slot("srcA", OperandKind.Address)),
        Vec("CCTLL", "I_CCTLL", "cctll__Ra_RZ_UR", "cctll__Ra_nonRz_UR", "cctll__sImmOffset", "cctll__uImmOffset")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA)) => Some(Instruction.CctllmioPipeAddress(srcA, modifiers, guard))
        case _ => None
      }
  }
  object CctlsNoOperands extends Definition("CctlsNoOperands", "CCTLS", Vec(), Vec("CCTLS")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.CctlsNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object CctlsaguPipeNoOperands extends Definition("CctlsaguPipeNoOperands", "CCTLSagu_pipe", Vec(), Vec("CCTLS")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.CctlsaguPipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object CctltNoOperands
      extends Definition("CctltNoOperands", "CCTLT", Vec(), Vec("CCTLT", "CCTLT_IVALL", "cctlt__IVALL")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.CctltNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object CctltImm extends Definition("CctltImm", "CCTLT", Vec(Slot("uImm", OperandKind.Immediate)), Vec("CCTLT_IDX")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(uImm)) => Some(Instruction.CctltImm(uImm, modifiers, guard))
        case _ => None
      }
  }
  object CctltImmImm extends Definition(
        "CctltImmImm",
        "CCTLT",
        Vec(Slot("sb_bank", OperandKind.Immediate), Slot("sb_offset", OperandKind.Immediate)),
        Vec("cctlt__const")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb_bank), InstructionOperand.Immediate(sb_offset)) =>
          Some(Instruction.CctltImmImm(sb_bank, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object CctltReg extends Definition(
        "CctltReg",
        "CCTLT",
        Vec(Slot("srcB", OperandKind.Register)),
        Vec("CCTLT_REG", "cctlt__Rb", "cctlt__URb")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcB)) => Some(Instruction.CctltReg(srcB, modifiers, guard))
        case _ => None
      }
  }
  object CctltmioPipeNoOperands
      extends Definition("CctltmioPipeNoOperands", "CCTLTmio_pipe", Vec(), Vec("CCTLT_IVALL", "cctlt__IVALL")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.CctltmioPipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object CctltmioPipeImm extends Definition(
        "CctltmioPipeImm",
        "CCTLTmio_pipe",
        Vec(Slot("uImm", OperandKind.Immediate)),
        Vec("CCTLT_IDX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(uImm)) => Some(Instruction.CctltmioPipeImm(uImm, modifiers, guard))
        case _ => None
      }
  }
  object CctltmioPipeImmImm extends Definition(
        "CctltmioPipeImmImm",
        "CCTLTmio_pipe",
        Vec(Slot("sb_bank", OperandKind.Immediate), Slot("sb_offset", OperandKind.Immediate)),
        Vec("cctlt__const")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb_bank), InstructionOperand.Immediate(sb_offset)) =>
          Some(Instruction.CctltmioPipeImmImm(sb_bank, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object CctltmioPipeReg extends Definition(
        "CctltmioPipeReg",
        "CCTLTmio_pipe",
        Vec(Slot("srcB", OperandKind.Register)),
        Vec("CCTLT_REG", "cctlt__Rb", "cctlt__URb")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcB)) => Some(Instruction.CctltmioPipeReg(srcB, modifiers, guard))
        case _ => None
      }
  }
  object CctlttexPipeNoOperands extends Definition("CctlttexPipeNoOperands", "CCTLTtex_pipe", Vec(), Vec("CCTLT")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.CctlttexPipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object CctlaguPipeNoOperands extends Definition("CctlaguPipeNoOperands", "CCTLagu_pipe", Vec(), Vec("CCTL IVALL")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.CctlaguPipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object CctlaguPipeAddress extends Definition(
        "CctlaguPipeAddress",
        "CCTLagu_pipe",
        Vec(Slot("srcA", OperandKind.Address)),
        Vec("CCTL", "CCTL QRY", "I CCTL", "I CCTL QRY")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA)) => Some(Instruction.CctlaguPipeAddress(srcA, modifiers, guard))
        case _ => None
      }
  }
  object CctlaguPipeRegAddress extends Definition(
        "CctlaguPipeRegAddress",
        "CCTLagu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address)),
        Vec("CCTL QRY", "I CCTL QRY")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Address(srcA)) =>
          Some(Instruction.CctlaguPipeRegAddress(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object CctlmioPipeNoOperands extends Definition(
        "CctlmioPipeNoOperands",
        "CCTLmio_pipe",
        Vec(),
        Vec(
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
        case Vec() => Some(Instruction.CctlmioPipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object CctlmioPipeAddress extends Definition(
        "CctlmioPipeAddress",
        "CCTLmio_pipe",
        Vec(Slot("srcA", OperandKind.Address)),
        Vec(
          "CCTL",
          "CCTL_QRY",
          "I_CCTL",
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
        case Vec(InstructionOperand.Address(srcA)) => Some(Instruction.CctlmioPipeAddress(srcA, modifiers, guard))
        case _ => None
      }
  }
  object CctlmioPipeAddressAddress extends Definition(
        "CctlmioPipeAddressAddress",
        "CCTLmio_pipe",
        Vec(Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Address)),
        Vec("cctl_c_ldc_const_bindless_", "cctl_c_ldcu_const_bindless_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA), InstructionOperand.Address(srcB)) =>
          Some(Instruction.CctlmioPipeAddressAddress(srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object CctlmioPipeAddressImm extends Definition(
        "CctlmioPipeAddressImm",
        "CCTLmio_pipe",
        Vec(Slot("srcA", OperandKind.Address), Slot("sector_count", OperandKind.Immediate)),
        Vec("cctl__sImmOffset_rml2", "cctl__sUROffset_rml2", "cctl__uImmOffset_rml2", "cctl__uUROffset_rml2")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA), InstructionOperand.Immediate(sector_count)) =>
          Some(Instruction.CctlmioPipeAddressImm(srcA, sector_count, modifiers, guard))
        case _ => None
      }
  }
  object CctlmioPipeConst extends Definition(
        "CctlmioPipeConst",
        "CCTLmio_pipe",
        Vec(Slot("sa", OperandKind.ConstantBank)),
        Vec("cctl_c_ldc_const_bound_", "cctl_c_ldcu_const_bound_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(sa)) => Some(Instruction.CctlmioPipeConst(sa, modifiers, guard))
        case _ => None
      }
  }
  object CctlmioPipePredAddress extends Definition(
        "CctlmioPipePredAddress",
        "CCTLmio_pipe",
        Vec(Slot("pu", OperandKind.Predicate), Slot("srcA", OperandKind.Address)),
        Vec("cctl__sImmOffset_pf2_q", "cctl__sUROffset_pf2_q", "cctl__uImmOffset_pf2_q", "cctl__uUROffset_pf2_q")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pu), InstructionOperand.Address(srcA)) =>
          Some(Instruction.CctlmioPipePredAddress(pu, srcA, modifiers, guard))
        case _ => None
      }
  }
  object CctlmioPipeRegAddress extends Definition(
        "CctlmioPipeRegAddress",
        "CCTLmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Address)),
        Vec("CCTL_QRY")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Address(srcA)) =>
          Some(Instruction.CctlmioPipeRegAddress(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object CgaerrbarNoOperands extends Definition("CgaerrbarNoOperands", "CGAERRBAR", Vec(), Vec("cgaerrbar_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.CgaerrbarNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object CgaerrbarmioPipeNoOperands
      extends Definition("CgaerrbarmioPipeNoOperands", "CGAERRBARmio_pipe", Vec(), Vec("cgaerrbar_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.CgaerrbarmioPipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object ClmadRegRegAddressImmReg extends Definition(
        "ClmadRegRegAddressImmReg",
        "CLMAD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("clmad__RCxR_RCxR")
      ) {
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
  object ClmadRegRegConstReg extends Definition(
        "ClmadRegRegConstReg",
        "CLMAD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("clmad__RCR_RCR")
      ) {
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
  object ClmadRegRegRegAddressImm extends Definition(
        "ClmadRegRegRegAddressImm",
        "CLMAD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Address),
          Slot("sc_offset", OperandKind.Immediate)
        ),
        Vec("clmad__RRCx_RRCx")
      ) {
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
  object ClmadRegRegRegConst extends Definition(
        "ClmadRegRegRegConst",
        "CLMAD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.ConstantBank)
        ),
        Vec("clmad__RRC_RRC")
      ) {
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
  object ClmadRegRegRegReg extends Definition(
        "ClmadRegRegRegReg",
        "CLMAD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("clmad__RRR_RRR", "clmad__RRU_RRU", "clmad__RUR_RUR")
      ) {
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
  object Clmadfma64litePipeRegRegAddressImmReg extends Definition(
        "Clmadfma64litePipeRegRegAddressImmReg",
        "CLMADfma64lite_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("clmad__RCxR_RCxR")
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
          Some(Instruction.Clmadfma64litePipeRegRegAddressImmReg(dst, srcA, srcB, sb_offset, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Clmadfma64litePipeRegRegConstReg extends Definition(
        "Clmadfma64litePipeRegRegConstReg",
        "CLMADfma64lite_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("clmad__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Clmadfma64litePipeRegRegConstReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Clmadfma64litePipeRegRegRegAddressImm extends Definition(
        "Clmadfma64litePipeRegRegRegAddressImm",
        "CLMADfma64lite_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Address),
          Slot("sc_offset", OperandKind.Immediate)
        ),
        Vec("clmad__RRCx_RRCx")
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
          Some(Instruction.Clmadfma64litePipeRegRegRegAddressImm(dst, srcA, srcB, srcC, sc_offset, modifiers, guard))
        case _ => None
      }
  }
  object Clmadfma64litePipeRegRegRegConst extends Definition(
        "Clmadfma64litePipeRegRegRegConst",
        "CLMADfma64lite_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.ConstantBank)
        ),
        Vec("clmad__RRC_RRC")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(sc)
            ) => Some(Instruction.Clmadfma64litePipeRegRegRegConst(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object Clmadfma64litePipeRegRegRegReg extends Definition(
        "Clmadfma64litePipeRegRegRegReg",
        "CLMADfma64lite_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("clmad__RRR_RRR", "clmad__RRU_RRU", "clmad__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Clmadfma64litePipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object ContNoOperands extends Definition("ContNoOperands", "CONT", Vec(), Vec("BRK", "CONT")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.ContNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object ContbruPipeNoOperands extends Definition("ContbruPipeNoOperands", "CONTbru_pipe", Vec(), Vec("BRK", "CONT")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.ContbruPipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object CreduxRegReg extends Definition(
        "CreduxRegReg",
        "CREDUX",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register)),
        Vec("credux_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcA)) =>
          Some(Instruction.CreduxRegReg(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object CreduxudpPipeRegReg extends Definition(
        "CreduxudpPipeRegReg",
        "CREDUXudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register)),
        Vec("credux_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcA)) =>
          Some(Instruction.CreduxudpPipeRegReg(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object Cs2rRegSpecial extends Definition(
        "Cs2rRegSpecial",
        "CS2R",
        Vec(Slot("dst", OperandKind.Register), Slot("sR", OperandKind.SpecialRegister)),
        Vec("CS2R", "cs2r_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.SpecialRegister(sR)) =>
          Some(Instruction.Cs2rRegSpecial(dst, sR, modifiers, guard))
        case _ => None
      }
  }
  object Cs2rfxuPipeRegSpecial extends Definition(
        "Cs2rfxuPipeRegSpecial",
        "CS2Rfxu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("sR", OperandKind.SpecialRegister)),
        Vec("CS2R")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.SpecialRegister(sR)) =>
          Some(Instruction.Cs2rfxuPipeRegSpecial(dst, sR, modifiers, guard))
        case _ => None
      }
  }
  object Cs2rintPipeRegSpecial extends Definition(
        "Cs2rintPipeRegSpecial",
        "CS2Rint_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("sRa", OperandKind.SpecialRegister)),
        Vec("cs2r_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.SpecialRegister(sRa)) =>
          Some(Instruction.Cs2rintPipeRegSpecial(dst, sRa, modifiers, guard))
        case _ => None
      }
  }
  object Cs2urRegSpecial extends Definition(
        "Cs2urRegSpecial",
        "CS2UR",
        Vec(Slot("dst", OperandKind.Register), Slot("sRa", OperandKind.SpecialRegister)),
        Vec("cs2ur_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.SpecialRegister(sRa)) =>
          Some(Instruction.Cs2urRegSpecial(dst, sRa, modifiers, guard))
        case _ => None
      }
  }
  object Cs2urudpPipeRegSpecial extends Definition(
        "Cs2urudpPipeRegSpecial",
        "CS2URudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("sRa", OperandKind.SpecialRegister)),
        Vec("cs2ur_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.SpecialRegister(sRa)) =>
          Some(Instruction.Cs2urudpPipeRegSpecial(dst, sRa, modifiers, guard))
        case _ => None
      }
  }
  object CsetRegSymbol extends Definition(
        "CsetRegSymbol",
        "CSET",
        Vec(Slot("dst", OperandKind.Register), Slot("dummyCC", OperandKind.Symbol)),
        Vec("NoBop CSET", "NoBop_CSET")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Symbol(dummyCC)) =>
          Some(Instruction.CsetRegSymbol(dst, dummyCC, modifiers, guard))
        case _ => None
      }
  }
  object CsetRegSymbolPred extends Definition(
        "CsetRegSymbolPred",
        "CSET",
        Vec(Slot("dst", OperandKind.Register), Slot("dummyCC", OperandKind.Symbol), Slot("pa", OperandKind.Predicate)),
        Vec("CSET")
      ) {
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
  object CsetpPredPredSymbolPred extends Definition(
        "CsetpPredPredSymbolPred",
        "CSETP",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("inputCC", OperandKind.Symbol),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("CSETP")
      ) {
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
  object CsetpPredSymbol extends Definition(
        "CsetpPredSymbol",
        "CSETP",
        Vec(Slot("dstPredicate", OperandKind.Predicate), Slot("inputCC", OperandKind.Symbol)),
        Vec("NoBop CSETP", "NoBop_CSETP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(dstPredicate), InstructionOperand.Symbol(inputCC)) =>
          Some(Instruction.CsetpPredSymbol(dstPredicate, inputCC, modifiers, guard))
        case _ => None
      }
  }
  object CsetpfxuPipePredPredSymbolPred extends Definition(
        "CsetpfxuPipePredPredSymbolPred",
        "CSETPfxu_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("inputCC", OperandKind.Symbol),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("CSETP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Symbol(inputCC),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.CsetpfxuPipePredPredSymbolPred(dstPredicate, nPd, inputCC, pa, modifiers, guard))
        case _ => None
      }
  }
  object CsetpfxuPipePredSymbol extends Definition(
        "CsetpfxuPipePredSymbol",
        "CSETPfxu_pipe",
        Vec(Slot("dstPredicate", OperandKind.Predicate), Slot("inputCC", OperandKind.Symbol)),
        Vec("NoBop_CSETP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(dstPredicate), InstructionOperand.Symbol(inputCC)) =>
          Some(Instruction.CsetpfxuPipePredSymbol(dstPredicate, inputCC, modifiers, guard))
        case _ => None
      }
  }
  object CsetpxluPipePredPredSymbolPred extends Definition(
        "CsetpxluPipePredPredSymbolPred",
        "CSETPxlu_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("inputCC", OperandKind.Symbol),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("CSETP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Symbol(inputCC),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.CsetpxluPipePredPredSymbolPred(dstPredicate, nPd, inputCC, pa, modifiers, guard))
        case _ => None
      }
  }
  object CsetpxluPipePredSymbol extends Definition(
        "CsetpxluPipePredSymbol",
        "CSETPxlu_pipe",
        Vec(Slot("dstPredicate", OperandKind.Predicate), Slot("inputCC", OperandKind.Symbol)),
        Vec("NoBop CSETP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(dstPredicate), InstructionOperand.Symbol(inputCC)) =>
          Some(Instruction.CsetpxluPipePredSymbol(dstPredicate, inputCC, modifiers, guard))
        case _ => None
      }
  }
  object CsetfxuPipeRegSymbol extends Definition(
        "CsetfxuPipeRegSymbol",
        "CSETfxu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("dummyCC", OperandKind.Symbol)),
        Vec("NoBop_CSET")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Symbol(dummyCC)) =>
          Some(Instruction.CsetfxuPipeRegSymbol(dst, dummyCC, modifiers, guard))
        case _ => None
      }
  }
  object CsetfxuPipeRegSymbolPred extends Definition(
        "CsetfxuPipeRegSymbolPred",
        "CSETfxu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("dummyCC", OperandKind.Symbol), Slot("pa", OperandKind.Predicate)),
        Vec("CSET")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Symbol(dummyCC),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.CsetfxuPipeRegSymbolPred(dst, dummyCC, pa, modifiers, guard))
        case _ => None
      }
  }
  object CsetxluPipeRegSymbol extends Definition(
        "CsetxluPipeRegSymbol",
        "CSETxlu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("dummyCC", OperandKind.Symbol)),
        Vec("NoBop CSET")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Symbol(dummyCC)) =>
          Some(Instruction.CsetxluPipeRegSymbol(dst, dummyCC, modifiers, guard))
        case _ => None
      }
  }
  object CsetxluPipeRegSymbolPred extends Definition(
        "CsetxluPipeRegSymbolPred",
        "CSETxlu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("dummyCC", OperandKind.Symbol), Slot("pa", OperandKind.Predicate)),
        Vec("CSET")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Symbol(dummyCC),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.CsetxluPipeRegSymbolPred(dst, dummyCC, pa, modifiers, guard))
        case _ => None
      }
  }
  object CsmtestImm
      extends Definition("CsmtestImm", "CSMTEST", Vec(Slot("sa", OperandKind.Immediate)), Vec("csmtest_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sa)) => Some(Instruction.CsmtestImm(sa, modifiers, guard))
        case _ => None
      }
  }
  object CsmtestPredImm extends Definition(
        "CsmtestPredImm",
        "CSMTEST",
        Vec(Slot("pu", OperandKind.Predicate), Slot("sa", OperandKind.Immediate)),
        Vec("csmtest_cmp_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pu), InstructionOperand.Immediate(sa)) =>
          Some(Instruction.CsmtestPredImm(pu, sa, modifiers, guard))
        case _ => None
      }
  }
  object CsmtestPredPredImmPred extends Definition(
        "CsmtestPredPredImmPred",
        "CSMTEST",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("sa", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("csmtest_bop_")
      ) {
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
  object CsmtestfePipeImm extends Definition(
        "CsmtestfePipeImm",
        "CSMTESTfe_pipe",
        Vec(Slot("sa", OperandKind.Immediate)),
        Vec("csmtest_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sa)) => Some(Instruction.CsmtestfePipeImm(sa, modifiers, guard))
        case _ => None
      }
  }
  object CsmtestfePipePredImm extends Definition(
        "CsmtestfePipePredImm",
        "CSMTESTfe_pipe",
        Vec(Slot("pu", OperandKind.Predicate), Slot("sa", OperandKind.Immediate)),
        Vec("csmtest_cmp_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pu), InstructionOperand.Immediate(sa)) =>
          Some(Instruction.CsmtestfePipePredImm(pu, sa, modifiers, guard))
        case _ => None
      }
  }
  object CsmtestfePipePredPredImmPred extends Definition(
        "CsmtestfePipePredPredImmPred",
        "CSMTESTfe_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("sa", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("csmtest_bop_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Immediate(sa),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.CsmtestfePipePredPredImmPred(pu, pv, sa, pp, modifiers, guard))
        case _ => None
      }
  }
  object DaddRegRegAddressImm extends Definition(
        "DaddRegRegAddressImm",
        "DADD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcC", OperandKind.Address),
          Slot("sc_offset", OperandKind.Immediate)
        ),
        Vec("dadd__RRCx_RCx")
      ) {
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
  object DaddRegRegConst extends Definition(
        "DaddRegRegConst",
        "DADD",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sc", OperandKind.ConstantBank)
        ),
        Vec("Const DADD", "Const_DADD", "dadd__RRC_RC")
      ) {
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
  object DaddRegRegImm extends Definition(
        "DaddRegRegImm",
        "DADD",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sc", OperandKind.Immediate)),
        Vec("Imm DADD", "Imm_DADD", "dadd__RRsI_RI")
      ) {
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
  object DaddRegRegReg extends Definition(
        "DaddRegRegReg",
        "DADD",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("DADD", "dadd__RRR_RR", "dadd__RRU_RU")
      ) {
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
  object Daddfma64litePipeRegRegAddressImm extends Definition(
        "Daddfma64litePipeRegRegAddressImm",
        "DADDfma64lite_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcC", OperandKind.Address),
          Slot("sc_offset", OperandKind.Immediate)
        ),
        Vec("dadd__RRCx_RCx")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcC),
              InstructionOperand.Immediate(sc_offset)
            ) => Some(Instruction.Daddfma64litePipeRegRegAddressImm(dst, srcA, srcC, sc_offset, modifiers, guard))
        case _ => None
      }
  }
  object Daddfma64litePipeRegRegConst extends Definition(
        "Daddfma64litePipeRegRegConst",
        "DADDfma64lite_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sc", OperandKind.ConstantBank)
        ),
        Vec("Const_DADD", "dadd__RRC_RC")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sc)
            ) => Some(Instruction.Daddfma64litePipeRegRegConst(dst, srcA, sc, modifiers, guard))
        case _ => None
      }
  }
  object Daddfma64litePipeRegRegImm extends Definition(
        "Daddfma64litePipeRegRegImm",
        "DADDfma64lite_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sc", OperandKind.Immediate)),
        Vec("Imm_DADD", "dadd__RRsI_RI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.Daddfma64litePipeRegRegImm(dst, srcA, sc, modifiers, guard))
        case _ => None
      }
  }
  object Daddfma64litePipeRegRegReg extends Definition(
        "Daddfma64litePipeRegRegReg",
        "DADDfma64lite_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("DADD", "dadd__RRR_RR", "dadd__RRU_RU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.Daddfma64litePipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object DaddfmaxPipeRegRegConst extends Definition(
        "DaddfmaxPipeRegRegConst",
        "DADDfmaX_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const DADD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.DaddfmaxPipeRegRegConst(dst, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object DaddfmaxPipeRegRegImm extends Definition(
        "DaddfmaxPipeRegRegImm",
        "DADDfmaX_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("Imm DADD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.DaddfmaxPipeRegRegImm(dst, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object DaddfmaxPipeRegRegReg extends Definition(
        "DaddfmaxPipeRegRegReg",
        "DADDfmaX_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("DADD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.DaddfmaxPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object DaddfmaxlitewPipeRegRegConst extends Definition(
        "DaddfmaxlitewPipeRegRegConst",
        "DADDfmaXliteW_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const DADD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.DaddfmaxlitewPipeRegRegConst(dst, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object DaddfmaxlitewPipeRegRegImm extends Definition(
        "DaddfmaxlitewPipeRegRegImm",
        "DADDfmaXliteW_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("Imm DADD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.DaddfmaxlitewPipeRegRegImm(dst, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object DaddfmaxlitewPipeRegRegReg extends Definition(
        "DaddfmaxlitewPipeRegRegReg",
        "DADDfmaXliteW_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("DADD")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.DaddfmaxlitewPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object DchkPredRegConst extends Definition(
        "DchkPredRegConst",
        "DCHK",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const DCHK")
      ) {
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
  object DchkPredRegImm extends Definition(
        "DchkPredRegImm",
        "DCHK",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate)
        ),
        Vec("Imm DCHK")
      ) {
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
  object DchkPredRegReg extends Definition(
        "DchkPredRegReg",
        "DCHK",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register)
        ),
        Vec("DCHK")
      ) {
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
  object DchkfmaxPipePredRegConst extends Definition(
        "DchkfmaxPipePredRegConst",
        "DCHKfmaX_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const DCHK")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.DchkfmaxPipePredRegConst(dstPredicate, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object DchkfmaxPipePredRegImm extends Definition(
        "DchkfmaxPipePredRegImm",
        "DCHKfmaX_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate)
        ),
        Vec("Imm DCHK")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.DchkfmaxPipePredRegImm(dstPredicate, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object DchkfmaxPipePredRegReg extends Definition(
        "DchkfmaxPipePredRegReg",
        "DCHKfmaX_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register)
        ),
        Vec("DCHK")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.DchkfmaxPipePredRegReg(dstPredicate, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object DepbarNoOperands extends Definition(
        "DepbarNoOperands",
        "DEPBAR",
        Vec(),
        Vec("DEPBAR", "DEPBAR_ALL", "depbar__noLE", "depbar_all_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.DepbarNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object DepbarGroup extends Definition(
        "DepbarGroup",
        "DEPBAR",
        Vec(Slot("dep_scbd", OperandKind.Group)),
        Vec("DEPBAR", "depbar__noLE")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Group(dep_scbd)) => Some(Instruction.DepbarGroup(dep_scbd, modifiers, guard))
        case _ => None
      }
  }
  object DepbarImm extends Definition(
        "DepbarImm",
        "DEPBAR",
        Vec(Slot("dep_scbd", OperandKind.Immediate)),
        Vec("DEPBAR", "depbar__noLE")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(dep_scbd)) => Some(Instruction.DepbarImm(dep_scbd, modifiers, guard))
        case _ => None
      }
  }
  object DepbarRegImm extends Definition(
        "DepbarRegImm",
        "DEPBAR",
        Vec(Slot("sbidx", OperandKind.Register), Slot("cnt", OperandKind.Immediate)),
        Vec("DEPBAR_LE", "depbar__LE")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(sbidx), InstructionOperand.Immediate(cnt)) =>
          Some(Instruction.DepbarRegImm(sbidx, cnt, modifiers, guard))
        case _ => None
      }
  }
  object DepbarRegImmGroup extends Definition(
        "DepbarRegImmGroup",
        "DEPBAR",
        Vec(
          Slot("sbidx", OperandKind.Register),
          Slot("cnt", OperandKind.Immediate),
          Slot("dep_scbd", OperandKind.Group)
        ),
        Vec("DEPBAR_LE", "depbar__LE")
      ) {
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
  object DepbarRegImmImm extends Definition(
        "DepbarRegImmImm",
        "DEPBAR",
        Vec(
          Slot("sbidx", OperandKind.Register),
          Slot("cnt", OperandKind.Immediate),
          Slot("dep_scbd", OperandKind.Immediate)
        ),
        Vec("DEPBAR_LE", "depbar__LE")
      ) {
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
  object DepbarRegReg extends Definition(
        "DepbarRegReg",
        "DEPBAR",
        Vec(Slot("sbidx", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("depbar_ur_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(sbidx), InstructionOperand.Register(srcB)) =>
          Some(Instruction.DepbarRegReg(sbidx, srcB, modifiers, guard))
        case _ => None
      }
  }
  object DepbarRegRegGroup extends Definition(
        "DepbarRegRegGroup",
        "DEPBAR",
        Vec(
          Slot("sbidx", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("scoreboard_list", OperandKind.Group)
        ),
        Vec("depbar_ur_")
      ) {
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
  object DepbarRegRegImm extends Definition(
        "DepbarRegRegImm",
        "DEPBAR",
        Vec(
          Slot("sbidx", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("scoreboard_list", OperandKind.Immediate)
        ),
        Vec("depbar_ur_")
      ) {
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
  object DepbarfePipeNoOperands extends Definition(
        "DepbarfePipeNoOperands",
        "DEPBARfe_pipe",
        Vec(),
        Vec("DEPBAR", "DEPBAR_ALL", "depbar__noLE", "depbar_all_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.DepbarfePipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object DepbarfePipeGroup extends Definition(
        "DepbarfePipeGroup",
        "DEPBARfe_pipe",
        Vec(Slot("dep_scbd", OperandKind.Group)),
        Vec("DEPBAR", "depbar__noLE")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Group(dep_scbd)) => Some(Instruction.DepbarfePipeGroup(dep_scbd, modifiers, guard))
        case _ => None
      }
  }
  object DepbarfePipeImm extends Definition(
        "DepbarfePipeImm",
        "DEPBARfe_pipe",
        Vec(Slot("dep_scbd", OperandKind.Immediate)),
        Vec("DEPBAR", "depbar__noLE")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(dep_scbd)) =>
          Some(Instruction.DepbarfePipeImm(dep_scbd, modifiers, guard))
        case _ => None
      }
  }
  object DepbarfePipeRegImm extends Definition(
        "DepbarfePipeRegImm",
        "DEPBARfe_pipe",
        Vec(Slot("sbidx", OperandKind.Register), Slot("cnt", OperandKind.Immediate)),
        Vec("DEPBAR_LE", "depbar__LE")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(sbidx), InstructionOperand.Immediate(cnt)) =>
          Some(Instruction.DepbarfePipeRegImm(sbidx, cnt, modifiers, guard))
        case _ => None
      }
  }
  object DepbarfePipeRegImmGroup extends Definition(
        "DepbarfePipeRegImmGroup",
        "DEPBARfe_pipe",
        Vec(
          Slot("sbidx", OperandKind.Register),
          Slot("cnt", OperandKind.Immediate),
          Slot("dep_scbd", OperandKind.Group)
        ),
        Vec("DEPBAR_LE", "depbar__LE")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(sbidx),
              InstructionOperand.Immediate(cnt),
              InstructionOperand.Group(dep_scbd)
            ) => Some(Instruction.DepbarfePipeRegImmGroup(sbidx, cnt, dep_scbd, modifiers, guard))
        case _ => None
      }
  }
  object DepbarfePipeRegImmImm extends Definition(
        "DepbarfePipeRegImmImm",
        "DEPBARfe_pipe",
        Vec(
          Slot("sbidx", OperandKind.Register),
          Slot("cnt", OperandKind.Immediate),
          Slot("dep_scbd", OperandKind.Immediate)
        ),
        Vec("DEPBAR_LE", "depbar__LE")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(sbidx),
              InstructionOperand.Immediate(cnt),
              InstructionOperand.Immediate(dep_scbd)
            ) => Some(Instruction.DepbarfePipeRegImmImm(sbidx, cnt, dep_scbd, modifiers, guard))
        case _ => None
      }
  }
  object DepbarfePipeRegReg extends Definition(
        "DepbarfePipeRegReg",
        "DEPBARfe_pipe",
        Vec(Slot("sbidx", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("depbar_ur_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(sbidx), InstructionOperand.Register(srcB)) =>
          Some(Instruction.DepbarfePipeRegReg(sbidx, srcB, modifiers, guard))
        case _ => None
      }
  }
  object DepbarfePipeRegRegGroup extends Definition(
        "DepbarfePipeRegRegGroup",
        "DEPBARfe_pipe",
        Vec(
          Slot("sbidx", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("scoreboard_list", OperandKind.Group)
        ),
        Vec("depbar_ur_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(sbidx),
              InstructionOperand.Register(srcB),
              InstructionOperand.Group(scoreboard_list)
            ) => Some(Instruction.DepbarfePipeRegRegGroup(sbidx, srcB, scoreboard_list, modifiers, guard))
        case _ => None
      }
  }
  object DepbarfePipeRegRegImm extends Definition(
        "DepbarfePipeRegRegImm",
        "DEPBARfe_pipe",
        Vec(
          Slot("sbidx", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("scoreboard_list", OperandKind.Immediate)
        ),
        Vec("depbar_ur_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(sbidx),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(scoreboard_list)
            ) => Some(Instruction.DepbarfePipeRegRegImm(sbidx, srcB, scoreboard_list, modifiers, guard))
        case _ => None
      }
  }
  object DfmaRegRegAddressImmReg extends Definition(
        "DfmaRegRegAddressImmReg",
        "DFMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("dfma__RCxR_RCxR")
      ) {
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
  object DfmaRegRegConstReg extends Definition(
        "DfmaRegRegConstReg",
        "DFMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Const DFMA", "Const_DFMA", "dfma__RCR_RCR")
      ) {
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
  object DfmaRegRegImmReg extends Definition(
        "DfmaRegRegImmReg",
        "DFMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm DFMA", "Imm_DFMA", "dfma__RsIR_RIR")
      ) {
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
  object DfmaRegRegRegAddressImm extends Definition(
        "DfmaRegRegRegAddressImm",
        "DFMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Address),
          Slot("sc_offset", OperandKind.Immediate)
        ),
        Vec("dfma__RRCx_RRCx")
      ) {
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
  object DfmaRegRegRegConst extends Definition(
        "DfmaRegRegRegConst",
        "DFMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.ConstantBank)
        ),
        Vec("Const1 DFMA", "Const1_DFMA", "dfma__RRC_RRC")
      ) {
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
  object DfmaRegRegRegImm extends Definition(
        "DfmaRegRegRegImm",
        "DFMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate)
        ),
        Vec("dfma__RRsI_RRI")
      ) {
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
  object DfmaRegRegRegReg extends Definition(
        "DfmaRegRegRegReg",
        "DFMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("DFMA", "dfma__RRR_RRR", "dfma__RRU_RRU", "dfma__RUR_RUR")
      ) {
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
  object Dfmafma64litePipeRegRegAddressImmReg extends Definition(
        "Dfmafma64litePipeRegRegAddressImmReg",
        "DFMAfma64lite_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("dfma__RCxR_RCxR")
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
          Some(Instruction.Dfmafma64litePipeRegRegAddressImmReg(dst, srcA, srcB, sb_offset, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Dfmafma64litePipeRegRegConstReg extends Definition(
        "Dfmafma64litePipeRegRegConstReg",
        "DFMAfma64lite_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Const_DFMA", "dfma__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Dfmafma64litePipeRegRegConstReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Dfmafma64litePipeRegRegImmReg extends Definition(
        "Dfmafma64litePipeRegRegImmReg",
        "DFMAfma64lite_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm_DFMA", "dfma__RsIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Dfmafma64litePipeRegRegImmReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Dfmafma64litePipeRegRegRegAddressImm extends Definition(
        "Dfmafma64litePipeRegRegRegAddressImm",
        "DFMAfma64lite_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Address),
          Slot("sc_offset", OperandKind.Immediate)
        ),
        Vec("dfma__RRCx_RRCx")
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
          Some(Instruction.Dfmafma64litePipeRegRegRegAddressImm(dst, srcA, srcB, srcC, sc_offset, modifiers, guard))
        case _ => None
      }
  }
  object Dfmafma64litePipeRegRegRegConst extends Definition(
        "Dfmafma64litePipeRegRegRegConst",
        "DFMAfma64lite_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.ConstantBank)
        ),
        Vec("Const1_DFMA", "dfma__RRC_RRC")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(sc)
            ) => Some(Instruction.Dfmafma64litePipeRegRegRegConst(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object Dfmafma64litePipeRegRegRegImm extends Definition(
        "Dfmafma64litePipeRegRegRegImm",
        "DFMAfma64lite_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate)
        ),
        Vec("dfma__RRsI_RRI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.Dfmafma64litePipeRegRegRegImm(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object Dfmafma64litePipeRegRegRegReg extends Definition(
        "Dfmafma64litePipeRegRegRegReg",
        "DFMAfma64lite_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("DFMA", "dfma__RRR_RRR", "dfma__RRU_RRU", "dfma__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Dfmafma64litePipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object DfmafmaxPipeRegRegConstReg extends Definition(
        "DfmafmaxPipeRegRegConstReg",
        "DFMAfmaX_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Const DFMA")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.DfmafmaxPipeRegRegConstReg(dst, srcA, srcConst, srcC, modifiers, guard))
        case _ => None
      }
  }
  object DfmafmaxPipeRegRegImmReg extends Definition(
        "DfmafmaxPipeRegRegImmReg",
        "DFMAfmaX_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm DFMA")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.DfmafmaxPipeRegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object DfmafmaxPipeRegRegRegConst extends Definition(
        "DfmafmaxPipeRegRegRegConst",
        "DFMAfmaX_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const1 DFMA")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.DfmafmaxPipeRegRegRegConst(dst, srcA, srcB, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object DfmafmaxPipeRegRegRegReg extends Definition(
        "DfmafmaxPipeRegRegRegReg",
        "DFMAfmaX_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("DFMA")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.DfmafmaxPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object DfmafmaxlitewPipeRegRegConstReg extends Definition(
        "DfmafmaxlitewPipeRegRegConstReg",
        "DFMAfmaXliteW_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Const DFMA")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.DfmafmaxlitewPipeRegRegConstReg(dst, srcA, srcConst, srcC, modifiers, guard))
        case _ => None
      }
  }
  object DfmafmaxlitewPipeRegRegImmReg extends Definition(
        "DfmafmaxlitewPipeRegRegImmReg",
        "DFMAfmaXliteW_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm DFMA")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.DfmafmaxlitewPipeRegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object DfmafmaxlitewPipeRegRegRegConst extends Definition(
        "DfmafmaxlitewPipeRegRegRegConst",
        "DFMAfmaXliteW_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const1 DFMA")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.DfmafmaxlitewPipeRegRegRegConst(dst, srcA, srcB, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object DfmafmaxlitewPipeRegRegRegReg extends Definition(
        "DfmafmaxlitewPipeRegRegRegReg",
        "DFMAfmaXliteW_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("DFMA")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.DfmafmaxlitewPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object DmmaRegRegRegReg extends Definition(
        "DmmaRegRegRegReg",
        "DMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("dmma_")
      ) {
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
  object DmmaRegRegRegRegPred extends Definition(
        "DmmaRegRegRegRegPred",
        "DMMA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("dmma_")
      ) {
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
  object Dmmafma64litePipeRegRegRegReg extends Definition(
        "Dmmafma64litePipeRegRegRegReg",
        "DMMAfma64lite_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("dmma_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Dmmafma64litePipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Dmmafma64litePipeRegRegRegRegPred extends Definition(
        "Dmmafma64litePipeRegRegRegRegPred",
        "DMMAfma64lite_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uPp", OperandKind.Predicate)
        ),
        Vec("dmma_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.Dmmafma64litePipeRegRegRegRegPred(dst, srcA, srcB, srcC, uPp, modifiers, guard))
        case _ => None
      }
  }
  object DmnmxRegRegConstPred extends Definition(
        "DmnmxRegRegConstPred",
        "DMNMX",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Const DMNMX", "Const_DMNMX")
      ) {
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
  object DmnmxRegRegImmPred extends Definition(
        "DmnmxRegRegImmPred",
        "DMNMX",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Imm DMNMX", "Imm_DMNMX")
      ) {
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
  object DmnmxRegRegRegPred extends Definition(
        "DmnmxRegRegRegPred",
        "DMNMX",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("DMNMX")
      ) {
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
  object Dmnmxfma64litePipeRegRegConstPred extends Definition(
        "Dmnmxfma64litePipeRegRegConstPred",
        "DMNMXfma64lite_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Const_DMNMX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.Dmnmxfma64litePipeRegRegConstPred(dst, srcA, srcConst, pa, modifiers, guard))
        case _ => None
      }
  }
  object Dmnmxfma64litePipeRegRegImmPred extends Definition(
        "Dmnmxfma64litePipeRegRegImmPred",
        "DMNMXfma64lite_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Imm_DMNMX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.Dmnmxfma64litePipeRegRegImmPred(dst, srcA, uImm, pa, modifiers, guard))
        case _ => None
      }
  }
  object Dmnmxfma64litePipeRegRegRegPred extends Definition(
        "Dmnmxfma64litePipeRegRegRegPred",
        "DMNMXfma64lite_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("DMNMX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.Dmnmxfma64litePipeRegRegRegPred(dst, srcA, srcB, pa, modifiers, guard))
        case _ => None
      }
  }
  object DmnmxfmaxPipeRegRegConstPred extends Definition(
        "DmnmxfmaxPipeRegRegConstPred",
        "DMNMXfmaX_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Const DMNMX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.DmnmxfmaxPipeRegRegConstPred(dst, srcA, srcConst, pa, modifiers, guard))
        case _ => None
      }
  }
  object DmnmxfmaxPipeRegRegImmPred extends Definition(
        "DmnmxfmaxPipeRegRegImmPred",
        "DMNMXfmaX_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Imm DMNMX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.DmnmxfmaxPipeRegRegImmPred(dst, srcA, uImm, pa, modifiers, guard))
        case _ => None
      }
  }
  object DmnmxfmaxPipeRegRegRegPred extends Definition(
        "DmnmxfmaxPipeRegRegRegPred",
        "DMNMXfmaX_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("DMNMX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.DmnmxfmaxPipeRegRegRegPred(dst, srcA, srcB, pa, modifiers, guard))
        case _ => None
      }
  }
  object DmnmxmioPipeRegRegConstPred extends Definition(
        "DmnmxmioPipeRegRegConstPred",
        "DMNMXmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Const_DMNMX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.DmnmxmioPipeRegRegConstPred(dst, srcA, srcConst, pa, modifiers, guard))
        case _ => None
      }
  }
  object DmnmxmioPipeRegRegImmPred extends Definition(
        "DmnmxmioPipeRegRegImmPred",
        "DMNMXmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Imm_DMNMX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.DmnmxmioPipeRegRegImmPred(dst, srcA, uImm, pa, modifiers, guard))
        case _ => None
      }
  }
  object DmnmxmioPipeRegRegRegPred extends Definition(
        "DmnmxmioPipeRegRegRegPred",
        "DMNMXmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("DMNMX")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.DmnmxmioPipeRegRegRegPred(dst, srcA, srcB, pa, modifiers, guard))
        case _ => None
      }
  }
  object DmulRegRegAddressImm extends Definition(
        "DmulRegRegAddressImm",
        "DMUL",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("dmul__RCxR_RCx")
      ) {
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
  object DmulRegRegConst extends Definition(
        "DmulRegRegConst",
        "DMUL",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank)
        ),
        Vec("Const DMUL", "Const_DMUL", "dmul__RCR_RC")
      ) {
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
  object DmulRegRegImm extends Definition(
        "DmulRegRegImm",
        "DMUL",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("Imm DMUL", "Imm_DMUL", "dmul__RsIR_RI")
      ) {
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
  object DmulRegRegReg extends Definition(
        "DmulRegRegReg",
        "DMUL",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("DMUL", "dmul__RRR_RR", "dmul__RUR_RU")
      ) {
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
  object Dmulfma64litePipeRegRegAddressImm extends Definition(
        "Dmulfma64litePipeRegRegAddressImm",
        "DMULfma64lite_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("dmul__RCxR_RCx")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.Dmulfma64litePipeRegRegAddressImm(dst, srcA, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object Dmulfma64litePipeRegRegConst extends Definition(
        "Dmulfma64litePipeRegRegConst",
        "DMULfma64lite_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank)
        ),
        Vec("Const_DMUL", "dmul__RCR_RC")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb)
            ) => Some(Instruction.Dmulfma64litePipeRegRegConst(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object Dmulfma64litePipeRegRegImm extends Definition(
        "Dmulfma64litePipeRegRegImm",
        "DMULfma64lite_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("Imm_DMUL", "dmul__RsIR_RI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.Dmulfma64litePipeRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object Dmulfma64litePipeRegRegReg extends Definition(
        "Dmulfma64litePipeRegRegReg",
        "DMULfma64lite_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("DMUL", "dmul__RRR_RR", "dmul__RUR_RU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.Dmulfma64litePipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object DmulfmaxPipeRegRegConst extends Definition(
        "DmulfmaxPipeRegRegConst",
        "DMULfmaX_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const DMUL")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.DmulfmaxPipeRegRegConst(dst, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object DmulfmaxPipeRegRegImm extends Definition(
        "DmulfmaxPipeRegRegImm",
        "DMULfmaX_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("Imm DMUL")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.DmulfmaxPipeRegRegImm(dst, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object DmulfmaxPipeRegRegReg extends Definition(
        "DmulfmaxPipeRegRegReg",
        "DMULfmaX_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("DMUL")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.DmulfmaxPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object DmulfmaxlitewPipeRegRegConst extends Definition(
        "DmulfmaxlitewPipeRegRegConst",
        "DMULfmaXliteW_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const DMUL")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.DmulfmaxlitewPipeRegRegConst(dst, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object DmulfmaxlitewPipeRegRegImm extends Definition(
        "DmulfmaxlitewPipeRegRegImm",
        "DMULfmaXliteW_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("Imm DMUL")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.DmulfmaxlitewPipeRegRegImm(dst, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object DmulfmaxlitewPipeRegRegReg extends Definition(
        "DmulfmaxlitewPipeRegRegReg",
        "DMULfmaXliteW_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("DMUL")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.DmulfmaxlitewPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object DsetRegRegConst extends Definition(
        "DsetRegRegConst",
        "DSET",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("NoBop Const DSET", "NoBop_Const_DSET")
      ) {
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
  object DsetRegRegConstPred extends Definition(
        "DsetRegRegConstPred",
        "DSET",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Const DSET", "Const_DSET")
      ) {
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
  object DsetRegRegImm extends Definition(
        "DsetRegRegImm",
        "DSET",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("NoBop Imm DSET", "NoBop_Imm_DSET")
      ) {
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
  object DsetRegRegImmPred extends Definition(
        "DsetRegRegImmPred",
        "DSET",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Imm DSET", "Imm_DSET")
      ) {
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
  object DsetRegRegReg extends Definition(
        "DsetRegRegReg",
        "DSET",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("NoBop DSET", "NoBop_DSET")
      ) {
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
  object DsetRegRegRegPred extends Definition(
        "DsetRegRegRegPred",
        "DSET",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("DSET")
      ) {
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
  object DsetpPredPredRegAddressImmPred extends Definition(
        "DsetpPredPredRegAddressImmPred",
        "DSETP",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcC", OperandKind.Address),
          Slot("sc_offset", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("dsetp__RRCx_RCx")
      ) {
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
  object DsetpPredPredRegConstPred extends Definition(
        "DsetpPredPredRegConstPred",
        "DSETP",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Const DSETP", "Const_DSETP", "dsetp__RRC_RC")
      ) {
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
  object DsetpPredPredRegImmPred extends Definition(
        "DsetpPredPredRegImmPred",
        "DSETP",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Imm DSETP", "Imm_DSETP", "dsetp__RRsI_RI")
      ) {
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
  object DsetpPredPredRegRegPred extends Definition(
        "DsetpPredPredRegRegPred",
        "DSETP",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("DSETP", "dsetp__RRR_RR", "dsetp__RRU_RU")
      ) {
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
  object DsetpPredRegAddressImm extends Definition(
        "DsetpPredRegAddressImm",
        "DSETP",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcC", OperandKind.Address),
          Slot("sc_offset", OperandKind.Immediate)
        ),
        Vec("dsetp_simple__RRCx_RCx")
      ) {
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
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
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
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate)
        ),
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
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register)
        ),
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
  object Dsetpfma64litePipePredPredRegAddressImmPred extends Definition(
        "Dsetpfma64litePipePredPredRegAddressImmPred",
        "DSETPfma64lite_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("pv", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcC", OperandKind.Address),
          Slot("sc_offset", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("dsetp__RRCx_RCx")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Predicate(pv),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcC),
              InstructionOperand.Immediate(sc_offset),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.Dsetpfma64litePipePredPredRegAddressImmPred(
            pu,
            pv,
            srcA,
            srcC,
            sc_offset,
            pp,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object Dsetpfma64litePipePredPredRegConstPred extends Definition(
        "Dsetpfma64litePipePredPredRegConstPred",
        "DSETPfma64lite_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Const_DSETP", "dsetp__RRC_RC")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.Dsetpfma64litePipePredPredRegConstPred(
            dstPredicate,
            nPd,
            srcA,
            srcConst,
            pa,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object Dsetpfma64litePipePredPredRegImmPred extends Definition(
        "Dsetpfma64litePipePredPredRegImmPred",
        "DSETPfma64lite_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Imm_DSETP", "dsetp__RRsI_RI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Predicate(pa)
            ) =>
          Some(Instruction.Dsetpfma64litePipePredPredRegImmPred(dstPredicate, nPd, srcA, uImm, pa, modifiers, guard))
        case _ => None
      }
  }
  object Dsetpfma64litePipePredPredRegRegPred extends Definition(
        "Dsetpfma64litePipePredPredRegRegPred",
        "DSETPfma64lite_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("DSETP", "dsetp__RRR_RR", "dsetp__RRU_RU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pa)
            ) =>
          Some(Instruction.Dsetpfma64litePipePredPredRegRegPred(dstPredicate, nPd, srcA, srcB, pa, modifiers, guard))
        case _ => None
      }
  }
  object Dsetpfma64litePipePredRegAddressImm extends Definition(
        "Dsetpfma64litePipePredRegAddressImm",
        "DSETPfma64lite_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcC", OperandKind.Address),
          Slot("sc_offset", OperandKind.Immediate)
        ),
        Vec("dsetp_simple__RRCx_RCx")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcC),
              InstructionOperand.Immediate(sc_offset)
            ) => Some(Instruction.Dsetpfma64litePipePredRegAddressImm(pu, srcA, srcC, sc_offset, modifiers, guard))
        case _ => None
      }
  }
  object Dsetpfma64litePipePredRegConst extends Definition(
        "Dsetpfma64litePipePredRegConst",
        "DSETPfma64lite_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("NoBop_Const_DSETP", "dsetp_simple__RRC_RC")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.Dsetpfma64litePipePredRegConst(dstPredicate, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object Dsetpfma64litePipePredRegImm extends Definition(
        "Dsetpfma64litePipePredRegImm",
        "DSETPfma64lite_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate)
        ),
        Vec("NoBop_Imm_DSETP", "dsetp_simple__RRsI_RI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.Dsetpfma64litePipePredRegImm(dstPredicate, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object Dsetpfma64litePipePredRegReg extends Definition(
        "Dsetpfma64litePipePredRegReg",
        "DSETPfma64lite_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register)
        ),
        Vec("NoBop_DSETP", "dsetp_simple__RRR_RR", "dsetp_simple__RRU_RU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.Dsetpfma64litePipePredRegReg(dstPredicate, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object DsetpfmaxPipePredPredRegConstPred extends Definition(
        "DsetpfmaxPipePredPredRegConstPred",
        "DSETPfmaX_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Const DSETP")
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
          Some(Instruction.DsetpfmaxPipePredPredRegConstPred(dstPredicate, nPd, srcA, srcConst, pa, modifiers, guard))
        case _ => None
      }
  }
  object DsetpfmaxPipePredPredRegImmPred extends Definition(
        "DsetpfmaxPipePredPredRegImmPred",
        "DSETPfmaX_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Imm DSETP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.DsetpfmaxPipePredPredRegImmPred(dstPredicate, nPd, srcA, uImm, pa, modifiers, guard))
        case _ => None
      }
  }
  object DsetpfmaxPipePredPredRegRegPred extends Definition(
        "DsetpfmaxPipePredPredRegRegPred",
        "DSETPfmaX_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("DSETP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.DsetpfmaxPipePredPredRegRegPred(dstPredicate, nPd, srcA, srcB, pa, modifiers, guard))
        case _ => None
      }
  }
  object DsetpfmaxPipePredRegConst extends Definition(
        "DsetpfmaxPipePredRegConst",
        "DSETPfmaX_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("NoBop Const DSETP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.DsetpfmaxPipePredRegConst(dstPredicate, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object DsetpfmaxPipePredRegImm extends Definition(
        "DsetpfmaxPipePredRegImm",
        "DSETPfmaX_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate)
        ),
        Vec("NoBop Imm DSETP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.DsetpfmaxPipePredRegImm(dstPredicate, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object DsetpfmaxPipePredRegReg extends Definition(
        "DsetpfmaxPipePredRegReg",
        "DSETPfmaX_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register)
        ),
        Vec("NoBop DSETP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.DsetpfmaxPipePredRegReg(dstPredicate, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object DsetpmioPipePredPredRegConstPred extends Definition(
        "DsetpmioPipePredPredRegConstPred",
        "DSETPmio_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Const_DSETP")
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
          Some(Instruction.DsetpmioPipePredPredRegConstPred(dstPredicate, nPd, srcA, srcConst, pa, modifiers, guard))
        case _ => None
      }
  }
  object DsetpmioPipePredPredRegImmPred extends Definition(
        "DsetpmioPipePredPredRegImmPred",
        "DSETPmio_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Imm_DSETP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.DsetpmioPipePredPredRegImmPred(dstPredicate, nPd, srcA, uImm, pa, modifiers, guard))
        case _ => None
      }
  }
  object DsetpmioPipePredPredRegRegPred extends Definition(
        "DsetpmioPipePredPredRegRegPred",
        "DSETPmio_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("nPd", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("DSETP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Predicate(nPd),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.DsetpmioPipePredPredRegRegPred(dstPredicate, nPd, srcA, srcB, pa, modifiers, guard))
        case _ => None
      }
  }
  object DsetpmioPipePredRegConst extends Definition(
        "DsetpmioPipePredRegConst",
        "DSETPmio_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("NoBop_Const_DSETP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.DsetpmioPipePredRegConst(dstPredicate, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object DsetpmioPipePredRegImm extends Definition(
        "DsetpmioPipePredRegImm",
        "DSETPmio_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate)
        ),
        Vec("NoBop_Imm_DSETP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.DsetpmioPipePredRegImm(dstPredicate, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object DsetpmioPipePredRegReg extends Definition(
        "DsetpmioPipePredRegReg",
        "DSETPmio_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register)
        ),
        Vec("NoBop_DSETP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.DsetpmioPipePredRegReg(dstPredicate, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object Dsetfma64litePipeRegRegConst extends Definition(
        "Dsetfma64litePipeRegRegConst",
        "DSETfma64lite_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("NoBop_Const_DSET")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.Dsetfma64litePipeRegRegConst(dst, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object Dsetfma64litePipeRegRegConstPred extends Definition(
        "Dsetfma64litePipeRegRegConstPred",
        "DSETfma64lite_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Const_DSET")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.Dsetfma64litePipeRegRegConstPred(dst, srcA, srcConst, pa, modifiers, guard))
        case _ => None
      }
  }
  object Dsetfma64litePipeRegRegImm extends Definition(
        "Dsetfma64litePipeRegRegImm",
        "DSETfma64lite_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("NoBop_Imm_DSET")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.Dsetfma64litePipeRegRegImm(dst, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object Dsetfma64litePipeRegRegImmPred extends Definition(
        "Dsetfma64litePipeRegRegImmPred",
        "DSETfma64lite_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Imm_DSET")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.Dsetfma64litePipeRegRegImmPred(dst, srcA, uImm, pa, modifiers, guard))
        case _ => None
      }
  }
  object Dsetfma64litePipeRegRegReg extends Definition(
        "Dsetfma64litePipeRegRegReg",
        "DSETfma64lite_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("NoBop_DSET")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.Dsetfma64litePipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object Dsetfma64litePipeRegRegRegPred extends Definition(
        "Dsetfma64litePipeRegRegRegPred",
        "DSETfma64lite_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("DSET")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.Dsetfma64litePipeRegRegRegPred(dst, srcA, srcB, pa, modifiers, guard))
        case _ => None
      }
  }
  object DsetfmaxPipeRegRegConst extends Definition(
        "DsetfmaxPipeRegRegConst",
        "DSETfmaX_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("NoBop Const DSET")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.DsetfmaxPipeRegRegConst(dst, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object DsetfmaxPipeRegRegConstPred extends Definition(
        "DsetfmaxPipeRegRegConstPred",
        "DSETfmaX_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Const DSET")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.DsetfmaxPipeRegRegConstPred(dst, srcA, srcConst, pa, modifiers, guard))
        case _ => None
      }
  }
  object DsetfmaxPipeRegRegImm extends Definition(
        "DsetfmaxPipeRegRegImm",
        "DSETfmaX_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("NoBop Imm DSET")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.DsetfmaxPipeRegRegImm(dst, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object DsetfmaxPipeRegRegImmPred extends Definition(
        "DsetfmaxPipeRegRegImmPred",
        "DSETfmaX_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Imm DSET")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.DsetfmaxPipeRegRegImmPred(dst, srcA, uImm, pa, modifiers, guard))
        case _ => None
      }
  }
  object DsetfmaxPipeRegRegReg extends Definition(
        "DsetfmaxPipeRegRegReg",
        "DSETfmaX_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("NoBop DSET")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.DsetfmaxPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object DsetfmaxPipeRegRegRegPred extends Definition(
        "DsetfmaxPipeRegRegRegPred",
        "DSETfmaX_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("DSET")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.DsetfmaxPipeRegRegRegPred(dst, srcA, srcB, pa, modifiers, guard))
        case _ => None
      }
  }
  object DsetmioPipeRegRegConst extends Definition(
        "DsetmioPipeRegRegConst",
        "DSETmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("NoBop_Const_DSET")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.DsetmioPipeRegRegConst(dst, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object DsetmioPipeRegRegConstPred extends Definition(
        "DsetmioPipeRegRegConstPred",
        "DSETmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Const_DSET")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.DsetmioPipeRegRegConstPred(dst, srcA, srcConst, pa, modifiers, guard))
        case _ => None
      }
  }
  object DsetmioPipeRegRegImm extends Definition(
        "DsetmioPipeRegRegImm",
        "DSETmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("NoBop_Imm_DSET")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.DsetmioPipeRegRegImm(dst, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object DsetmioPipeRegRegImmPred extends Definition(
        "DsetmioPipeRegRegImmPred",
        "DSETmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Imm_DSET")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.DsetmioPipeRegRegImmPred(dst, srcA, uImm, pa, modifiers, guard))
        case _ => None
      }
  }
  object DsetmioPipeRegRegReg extends Definition(
        "DsetmioPipeRegRegReg",
        "DSETmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("NoBop_DSET")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.DsetmioPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object DsetmioPipeRegRegRegPred extends Definition(
        "DsetmioPipeRegRegRegPred",
        "DSETmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("DSET")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.DsetmioPipeRegRegRegPred(dst, srcA, srcB, pa, modifiers, guard))
        case _ => None
      }
  }
  object ElectPredReg extends Definition(
        "ElectPredReg",
        "ELECT",
        Vec(Slot("pu", OperandKind.Predicate), Slot("dst", OperandKind.Register)),
        Vec("elect_noURa_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pu), InstructionOperand.Register(dst)) =>
          Some(Instruction.ElectPredReg(pu, dst, modifiers, guard))
        case _ => None
      }
  }
  object ElectPredRegPred extends Definition(
        "ElectPredRegPred",
        "ELECT",
        Vec(Slot("pu", OperandKind.Predicate), Slot("dst", OperandKind.Register), Slot("pp", OperandKind.Predicate)),
        Vec("elect_Pp_")
      ) {
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
  object ElectPredRegReg extends Definition(
        "ElectPredRegReg",
        "ELECT",
        Vec(Slot("pu", OperandKind.Predicate), Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register)),
        Vec("elect_")
      ) {
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
  object ElectcbuPipePredReg extends Definition(
        "ElectcbuPipePredReg",
        "ELECTcbu_pipe",
        Vec(Slot("pu", OperandKind.Predicate), Slot("dst", OperandKind.Register)),
        Vec("elect_noURa_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pu), InstructionOperand.Register(dst)) =>
          Some(Instruction.ElectcbuPipePredReg(pu, dst, modifiers, guard))
        case _ => None
      }
  }
  object ElectcbuPipePredRegPred extends Definition(
        "ElectcbuPipePredRegPred",
        "ELECTcbu_pipe",
        Vec(Slot("pu", OperandKind.Predicate), Slot("dst", OperandKind.Register), Slot("pp", OperandKind.Predicate)),
        Vec("elect_Pp_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.ElectcbuPipePredRegPred(pu, dst, pp, modifiers, guard))
        case _ => None
      }
  }
  object ElectcbuPipePredRegReg extends Definition(
        "ElectcbuPipePredRegReg",
        "ELECTcbu_pipe",
        Vec(Slot("pu", OperandKind.Predicate), Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register)),
        Vec("elect_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA)
            ) => Some(Instruction.ElectcbuPipePredRegReg(pu, dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object EndcollectiveNoOperands
      extends Definition("EndcollectiveNoOperands", "ENDCOLLECTIVE", Vec(), Vec("endcollective_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.EndcollectiveNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object EndcollectivePred extends Definition(
        "EndcollectivePred",
        "ENDCOLLECTIVE",
        Vec(Slot("pp", OperandKind.Predicate)),
        Vec("endcollective_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp)) => Some(Instruction.EndcollectivePred(pp, modifiers, guard))
        case _ => None
      }
  }
  object EndcollectivecbuPipeNoOperands
      extends Definition("EndcollectivecbuPipeNoOperands", "ENDCOLLECTIVEcbu_pipe", Vec(), Vec("endcollective_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.EndcollectivecbuPipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object EndcollectivecbuPipePred extends Definition(
        "EndcollectivecbuPipePred",
        "ENDCOLLECTIVEcbu_pipe",
        Vec(Slot("pp", OperandKind.Predicate)),
        Vec("endcollective_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp)) => Some(Instruction.EndcollectivecbuPipePred(pp, modifiers, guard))
        case _ => None
      }
  }
  object ErrbarNoOperands extends Definition("ErrbarNoOperands", "ERRBAR", Vec(), Vec("errbar_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.ErrbarNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object ErrbarmioPipeNoOperands
      extends Definition("ErrbarmioPipeNoOperands", "ERRBARmio_pipe", Vec(), Vec("errbar_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.ErrbarmioPipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object ExitNoOperands extends Definition("ExitNoOperands", "EXIT", Vec(), Vec("EXIT", "exit_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.ExitNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object ExitPred extends Definition("ExitPred", "EXIT", Vec(Slot("pp", OperandKind.Predicate)), Vec("exit_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp)) => Some(Instruction.ExitPred(pp, modifiers, guard))
        case _ => None
      }
  }
  object ExitbruPipeNoOperands extends Definition("ExitbruPipeNoOperands", "EXITbru_pipe", Vec(), Vec("EXIT")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.ExitbruPipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object ExitcbuPipeNoOperands extends Definition("ExitcbuPipeNoOperands", "EXITcbu_pipe", Vec(), Vec("exit_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.ExitcbuPipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object ExitcbuPipePred
      extends Definition("ExitcbuPipePred", "EXITcbu_pipe", Vec(Slot("pp", OperandKind.Predicate)), Vec("exit_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp)) => Some(Instruction.ExitcbuPipePred(pp, modifiers, guard))
        case _ => None
      }
  }
  object F2fRegAddressImm extends Definition(
        "F2fRegAddressImm",
        "F2F",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
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
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.ConstantBank)),
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
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
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
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
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
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
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
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
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
        Vec(Slot("dst", OperandKind.Register), Slot("sb", OperandKind.ConstantBank)),
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
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
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
  val definitions: Vec[Definition] = Vec(
    CallPredReg,
    CallPredRegImm,
    CallReg,
    CallRegImm,
    CallcbuPipeConst,
    CallcbuPipeImm,
    CallcbuPipePredConst,
    CallcbuPipePredImm,
    CallcbuPipePredReg,
    CallcbuPipePredRegImm,
    CallcbuPipeReg,
    CallcbuPipeRegImm,
    CalbruPipeConst,
    CalbruPipeImm,
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
    CctllaguPipeNoOperands,
    CctllaguPipeAddress,
    CctllaguPipeRegAddress,
    CctllmioPipeNoOperands,
    CctllmioPipeAddress,
    CctlsNoOperands,
    CctlsaguPipeNoOperands,
    CctltNoOperands,
    CctltImm,
    CctltImmImm,
    CctltReg,
    CctltmioPipeNoOperands,
    CctltmioPipeImm,
    CctltmioPipeImmImm,
    CctltmioPipeReg,
    CctlttexPipeNoOperands,
    CctlaguPipeNoOperands,
    CctlaguPipeAddress,
    CctlaguPipeRegAddress,
    CctlmioPipeNoOperands,
    CctlmioPipeAddress,
    CctlmioPipeAddressAddress,
    CctlmioPipeAddressImm,
    CctlmioPipeConst,
    CctlmioPipePredAddress,
    CctlmioPipeRegAddress,
    CgaerrbarNoOperands,
    CgaerrbarmioPipeNoOperands,
    ClmadRegRegAddressImmReg,
    ClmadRegRegConstReg,
    ClmadRegRegRegAddressImm,
    ClmadRegRegRegConst,
    ClmadRegRegRegReg,
    Clmadfma64litePipeRegRegAddressImmReg,
    Clmadfma64litePipeRegRegConstReg,
    Clmadfma64litePipeRegRegRegAddressImm,
    Clmadfma64litePipeRegRegRegConst,
    Clmadfma64litePipeRegRegRegReg,
    ContNoOperands,
    ContbruPipeNoOperands,
    CreduxRegReg,
    CreduxudpPipeRegReg,
    Cs2rRegSpecial,
    Cs2rfxuPipeRegSpecial,
    Cs2rintPipeRegSpecial,
    Cs2urRegSpecial,
    Cs2urudpPipeRegSpecial,
    CsetRegSymbol,
    CsetRegSymbolPred,
    CsetpPredPredSymbolPred,
    CsetpPredSymbol,
    CsetpfxuPipePredPredSymbolPred,
    CsetpfxuPipePredSymbol,
    CsetpxluPipePredPredSymbolPred,
    CsetpxluPipePredSymbol,
    CsetfxuPipeRegSymbol,
    CsetfxuPipeRegSymbolPred,
    CsetxluPipeRegSymbol,
    CsetxluPipeRegSymbolPred,
    CsmtestImm,
    CsmtestPredImm,
    CsmtestPredPredImmPred,
    CsmtestfePipeImm,
    CsmtestfePipePredImm,
    CsmtestfePipePredPredImmPred,
    DaddRegRegAddressImm,
    DaddRegRegConst,
    DaddRegRegImm,
    DaddRegRegReg,
    Daddfma64litePipeRegRegAddressImm,
    Daddfma64litePipeRegRegConst,
    Daddfma64litePipeRegRegImm,
    Daddfma64litePipeRegRegReg,
    DaddfmaxPipeRegRegConst,
    DaddfmaxPipeRegRegImm,
    DaddfmaxPipeRegRegReg,
    DaddfmaxlitewPipeRegRegConst,
    DaddfmaxlitewPipeRegRegImm,
    DaddfmaxlitewPipeRegRegReg,
    DchkPredRegConst,
    DchkPredRegImm,
    DchkPredRegReg,
    DchkfmaxPipePredRegConst,
    DchkfmaxPipePredRegImm,
    DchkfmaxPipePredRegReg,
    DepbarNoOperands,
    DepbarGroup,
    DepbarImm,
    DepbarRegImm,
    DepbarRegImmGroup,
    DepbarRegImmImm,
    DepbarRegReg,
    DepbarRegRegGroup,
    DepbarRegRegImm,
    DepbarfePipeNoOperands,
    DepbarfePipeGroup,
    DepbarfePipeImm,
    DepbarfePipeRegImm,
    DepbarfePipeRegImmGroup,
    DepbarfePipeRegImmImm,
    DepbarfePipeRegReg,
    DepbarfePipeRegRegGroup,
    DepbarfePipeRegRegImm,
    DfmaRegRegAddressImmReg,
    DfmaRegRegConstReg,
    DfmaRegRegImmReg,
    DfmaRegRegRegAddressImm,
    DfmaRegRegRegConst,
    DfmaRegRegRegImm,
    DfmaRegRegRegReg,
    Dfmafma64litePipeRegRegAddressImmReg,
    Dfmafma64litePipeRegRegConstReg,
    Dfmafma64litePipeRegRegImmReg,
    Dfmafma64litePipeRegRegRegAddressImm,
    Dfmafma64litePipeRegRegRegConst,
    Dfmafma64litePipeRegRegRegImm,
    Dfmafma64litePipeRegRegRegReg,
    DfmafmaxPipeRegRegConstReg,
    DfmafmaxPipeRegRegImmReg,
    DfmafmaxPipeRegRegRegConst,
    DfmafmaxPipeRegRegRegReg,
    DfmafmaxlitewPipeRegRegConstReg,
    DfmafmaxlitewPipeRegRegImmReg,
    DfmafmaxlitewPipeRegRegRegConst,
    DfmafmaxlitewPipeRegRegRegReg,
    DmmaRegRegRegReg,
    DmmaRegRegRegRegPred,
    Dmmafma64litePipeRegRegRegReg,
    Dmmafma64litePipeRegRegRegRegPred,
    DmnmxRegRegConstPred,
    DmnmxRegRegImmPred,
    DmnmxRegRegRegPred,
    Dmnmxfma64litePipeRegRegConstPred,
    Dmnmxfma64litePipeRegRegImmPred,
    Dmnmxfma64litePipeRegRegRegPred,
    DmnmxfmaxPipeRegRegConstPred,
    DmnmxfmaxPipeRegRegImmPred,
    DmnmxfmaxPipeRegRegRegPred,
    DmnmxmioPipeRegRegConstPred,
    DmnmxmioPipeRegRegImmPred,
    DmnmxmioPipeRegRegRegPred,
    DmulRegRegAddressImm,
    DmulRegRegConst,
    DmulRegRegImm,
    DmulRegRegReg,
    Dmulfma64litePipeRegRegAddressImm,
    Dmulfma64litePipeRegRegConst,
    Dmulfma64litePipeRegRegImm,
    Dmulfma64litePipeRegRegReg,
    DmulfmaxPipeRegRegConst,
    DmulfmaxPipeRegRegImm,
    DmulfmaxPipeRegRegReg,
    DmulfmaxlitewPipeRegRegConst,
    DmulfmaxlitewPipeRegRegImm,
    DmulfmaxlitewPipeRegRegReg,
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
    Dsetpfma64litePipePredPredRegAddressImmPred,
    Dsetpfma64litePipePredPredRegConstPred,
    Dsetpfma64litePipePredPredRegImmPred,
    Dsetpfma64litePipePredPredRegRegPred,
    Dsetpfma64litePipePredRegAddressImm,
    Dsetpfma64litePipePredRegConst,
    Dsetpfma64litePipePredRegImm,
    Dsetpfma64litePipePredRegReg,
    DsetpfmaxPipePredPredRegConstPred,
    DsetpfmaxPipePredPredRegImmPred,
    DsetpfmaxPipePredPredRegRegPred,
    DsetpfmaxPipePredRegConst,
    DsetpfmaxPipePredRegImm,
    DsetpfmaxPipePredRegReg,
    DsetpmioPipePredPredRegConstPred,
    DsetpmioPipePredPredRegImmPred,
    DsetpmioPipePredPredRegRegPred,
    DsetpmioPipePredRegConst,
    DsetpmioPipePredRegImm,
    DsetpmioPipePredRegReg,
    Dsetfma64litePipeRegRegConst,
    Dsetfma64litePipeRegRegConstPred,
    Dsetfma64litePipeRegRegImm,
    Dsetfma64litePipeRegRegImmPred,
    Dsetfma64litePipeRegRegReg,
    Dsetfma64litePipeRegRegRegPred,
    DsetfmaxPipeRegRegConst,
    DsetfmaxPipeRegRegConstPred,
    DsetfmaxPipeRegRegImm,
    DsetfmaxPipeRegRegImmPred,
    DsetfmaxPipeRegRegReg,
    DsetfmaxPipeRegRegRegPred,
    DsetmioPipeRegRegConst,
    DsetmioPipeRegRegConstPred,
    DsetmioPipeRegRegImm,
    DsetmioPipeRegRegImmPred,
    DsetmioPipeRegRegReg,
    DsetmioPipeRegRegRegPred,
    ElectPredReg,
    ElectPredRegPred,
    ElectPredRegReg,
    ElectcbuPipePredReg,
    ElectcbuPipePredRegPred,
    ElectcbuPipePredRegReg,
    EndcollectiveNoOperands,
    EndcollectivePred,
    EndcollectivecbuPipeNoOperands,
    EndcollectivecbuPipePred,
    ErrbarNoOperands,
    ErrbarmioPipeNoOperands,
    ExitNoOperands,
    ExitPred,
    ExitbruPipeNoOperands,
    ExitcbuPipeNoOperands,
    ExitcbuPipePred,
    F2fRegAddressImm,
    F2fRegConst,
    F2fRegImm,
    F2fRegReg,
    F2fpRegAddressImm,
    F2fpRegAddressImmReg,
    F2fpRegConst,
    F2fpRegConstReg
  )
}
