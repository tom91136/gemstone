// Generated SASS instruction definitions and checked operand constructors. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.sass
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import Instruction.*
@javax.annotation.processing.Generated(Array("sbt codegen"))
object InstructionDefinitions5 {
  object TxdRegRegRegRegRegImm extends Definition("TxdRegRegRegRegRegImm", "TXD", Vec("txd_b_noConst_", "txd_urc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.TxdRegRegRegRegRegImm(rd2, dst, srcA, srcB, srcC, tid8, modifiers, guard))
        case _ => None
      }
  }
  object TxdRegRegRegRegRegImmImm extends Definition("TxdRegRegRegRegRegImmImm", "TXD", Vec("txd_urc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(tid8),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TxdRegRegRegRegRegImmImm(rd2, dst, srcA, srcB, srcC, tid8, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TxdRegRegRegRegRegReg extends Definition("TxdRegRegRegRegRegReg", "TXD", Vec("txd_b_urc_", "txd_urc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe)
            ) => Some(Instruction.TxdRegRegRegRegRegReg(rd2, dst, srcA, srcB, srcC, uRe, modifiers, guard))
        case _ => None
      }
  }
  object TxdRegRegRegRegRegRegImm extends Definition("TxdRegRegRegRegRegRegImm", "TXD", Vec("txd_b_urc_", "txd_urc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.TxdRegRegRegRegRegRegImm(rd2, dst, srcA, srcB, srcC, uRe, tid8, modifiers, guard))
        case _ => None
      }
  }
  object TxdRegRegRegRegRegRegImmImm extends Definition("TxdRegRegRegRegRegRegImmImm", "TXD", Vec("txd_urc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe),
              InstructionOperand.Immediate(tid8),
              InstructionOperand.Immediate(wmsk)
            ) =>
          Some(Instruction.TxdRegRegRegRegRegRegImmImm(rd2, dst, srcA, srcB, srcC, uRe, tid8, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TxqRegRegImm extends Definition("TxqRegRegImm", "TXQ", Vec("TXQ", "TXQ_B")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(tid)
            ) => Some(Instruction.TxqRegRegImm(dst, srcA, tid, modifiers, guard))
        case _ => None
      }
  }
  object TxqRegRegImmImm
      extends Definition("TxqRegRegImmImm", "TXQ", Vec("TXQ", "TXQ.B", "TXQ_B", "TXQ_B_Imm", "TXQ_Imm")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(query),
              InstructionOperand.Immediate(tid)
            ) => Some(Instruction.TxqRegRegImmImm(dst, srcA, query, tid, modifiers, guard))
        case _ => None
      }
  }
  object TxqRegRegImmImmImm extends Definition(
        "TxqRegRegImmImmImm",
        "TXQ",
        Vec(
          "TXQ Imm",
          "TXQ Legacy Style",
          "TXQ.B Imm",
          "TXQ.B Legacy Style",
          "TXQ_B_Imm",
          "TXQ_B_Legacy_Style",
          "TXQ_Imm",
          "TXQ_Legacy_Style"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(query),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TxqRegRegImmImmImm(dst, srcA, query, tid, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TxqRegRegImmImmImmImm extends Definition(
        "TxqRegRegImmImmImmImm",
        "TXQ",
        Vec("TXQ Imm Legacy Style", "TXQ.B Imm Legacy Style", "TXQ_B_Imm_Legacy_Style", "TXQ_Imm_Legacy_Style")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(query),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(smp),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TxqRegRegImmImmImmImm(dst, srcA, query, tid, smp, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TxqRegRegRegImm extends Definition("TxqRegRegRegImm", "TXQ", Vec("txq_b_noConst_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TxqRegRegRegImm(rd2, dst, srcA, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TxqRegRegRegImmImmImm extends Definition("TxqRegRegRegImmImmImm", "TXQ", Vec("txq_", "txq_b_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(tsPtrIdx),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TxqRegRegRegImmImmImm(rd2, dst, srcA, bank5, tsPtrIdx, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TxqRegRegRegImmImmImmImm extends Definition("TxqRegRegRegImmImmImmImm", "TXQ", Vec("txq_b_tid_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(hdrtblbase6),
              InstructionOperand.Immediate(tid8),
              InstructionOperand.Immediate(wmsk)
            ) =>
          Some(Instruction.TxqRegRegRegImmImmImmImm(rd2, dst, srcA, bank5, hdrtblbase6, tid8, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TxqRegRegRegRegImm extends Definition("TxqRegRegRegRegImm", "TXQ", Vec("txq_b_urc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TxqRegRegRegRegImm(rd2, dst, srcA, srcC, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TxqRegRegRegRegImmImm extends Definition("TxqRegRegRegRegImmImm", "TXQ", Vec("txq_urc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(tid8),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TxqRegRegRegRegImmImm(rd2, dst, srcA, srcC, tid8, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object UblkcpAddressAddressReg
      extends Definition("UblkcpAddressAddressReg", "UBLKCP", Vec("ublkcp_", "ublkcp_one_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcB),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.UblkcpAddressAddressReg(srcB, srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object UblkcpAddressAddressRegAddress
      extends Definition("UblkcpAddressAddressRegAddress", "UBLKCP", Vec("ublkcp_desc_", "ublkcp_desc_one_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcB),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Address(uRe)
            ) => Some(Instruction.UblkcpAddressAddressRegAddress(srcB, srcA, srcC, uRe, modifiers, guard))
        case _ => None
      }
  }
  object UblkpfAddressReg extends Definition("UblkpfAddressReg", "UBLKPF", Vec("ublkpf__UUU", "ublkpf_one__UUU")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA), InstructionOperand.Register(srcC)) =>
          Some(Instruction.UblkpfAddressReg(srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object UblkpfAddressRegAddress
      extends Definition("UblkpfAddressRegAddress", "UBLKPF", Vec("ublkpf__UUU_desc", "ublkpf_one__UUU_desc")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Address(uRe)
            ) => Some(Instruction.UblkpfAddressRegAddress(srcA, srcC, uRe, modifiers, guard))
        case _ => None
      }
  }
  object UblkredAddressAddressReg
      extends Definition("UblkredAddressAddressReg", "UBLKRED", Vec("ublkred_", "ublkred_one_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcB),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.UblkredAddressAddressReg(srcB, srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object UblkredAddressAddressRegAddress
      extends Definition("UblkredAddressAddressRegAddress", "UBLKRED", Vec("ublkred_desc_", "ublkred_desc_one_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcB),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Address(uRe)
            ) => Some(Instruction.UblkredAddressAddressRegAddress(srcB, srcA, srcC, uRe, modifiers, guard))
        case _ => None
      }
  }
  object UbmskRegRegImm extends Definition("UbmskRegRegImm", "UBMSK", Vec("ubmsk__URuIUR_URI")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.UbmskRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object UbmskRegRegReg extends Definition("UbmskRegRegReg", "UBMSK", Vec("ubmsk__URURUR_URUR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.UbmskRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UbrevRegImm extends Definition("UbrevRegImm", "UBREV", Vec("ubrev__URuIUR_URI")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.UbrevRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object UbrevRegReg extends Definition("UbrevRegReg", "UBREV", Vec("ubrev__URURUR_URUR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.UbrevRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UcgabararvNoOperands extends Definition("UcgabararvNoOperands", "UCGABARARV", Vec("ucgabararrive_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.UcgabararvNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object UcgabargetReg extends Definition("UcgabargetReg", "UCGABARGET", Vec("ucgabarget_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst)) => Some(Instruction.UcgabargetReg(dst, modifiers, guard))
        case _ => None
      }
  }
  object UcgabarsetReg extends Definition("UcgabarsetReg", "UCGABARSET", Vec("ucgabarset_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcB)) => Some(Instruction.UcgabarsetReg(srcB, modifiers, guard))
        case _ => None
      }
  }
  object UcgabarwaitNoOperands extends Definition("UcgabarwaitNoOperands", "UCGABARWAIT", Vec("ucgabarwait_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.UcgabarwaitNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object Ucgabar_ArvNoOperands extends Definition("Ucgabar_ArvNoOperands", "UCGABAR_ARV", Vec("ucgabar_arrive_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.Ucgabar_ArvNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object Ucgabar_WaitNoOperands extends Definition("Ucgabar_WaitNoOperands", "UCGABAR_WAIT", Vec("ucgabar_wait_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.Ucgabar_WaitNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object UcleaRegPredRegImmImm extends Definition("UcleaRegPredRegImmImm", "UCLEA", Vec("uclea__Imm")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Immediate(constSizeU04)
            ) => Some(Instruction.UcleaRegPredRegImmImm(dst, uPu, srcA, sb, constSizeU04, modifiers, guard))
        case _ => None
      }
  }
  object UcleaRegPredRegRegImm extends Definition("UcleaRegPredRegRegImm", "UCLEA", Vec("uclea__URb")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(constSizeU04)
            ) => Some(Instruction.UcleaRegPredRegRegImm(dst, uPu, srcA, srcB, constSizeU04, modifiers, guard))
        case _ => None
      }
  }
  object Uf2fRegImm extends Definition(
        "Uf2fRegImm",
        "UF2F",
        Vec("uf2f_f32_downconvert__URIR_URIR", "uf2f_f32_upconvert__URIR_URIR", "uf2f_f32_upconvert_swap__URIR_URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.Uf2fRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object Uf2fRegReg extends Definition(
        "Uf2fRegReg",
        "UF2F",
        Vec("uf2f_f32_downconvert__UUU_UUU", "uf2f_f32_upconvert__UUU_UUU", "uf2f_f32_upconvert_swap__UUU_UUU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.Uf2fRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object Uf2fpRegImm extends Definition(
        "Uf2fpRegImm",
        "UF2FP",
        Vec("uf2fp_8b_upconvert__URIUR", "uf2fp_tf32__URIUR", "uf2fp_tf32_default_src__URIUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.Uf2fpRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object Uf2fpRegImmReg extends Definition(
        "Uf2fpRegImmReg",
        "UF2FP",
        Vec("uf2fp_f16_to_8b_downconvert__URIUR", "uf2fp_merge_c__URIUR", "uf2fp_merge_c_default_dst_src__URIUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Uf2fpRegImmReg(dst, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Uf2fpRegReg extends Definition(
        "Uf2fpRegReg",
        "UF2FP",
        Vec("uf2fp_8b_upconvert__URURUR", "uf2fp_tf32__URURUR", "uf2fp_tf32_default_src__URURUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.Uf2fpRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object Uf2fpRegRegImm extends Definition(
        "Uf2fpRegRegImm",
        "UF2FP",
        Vec(
          "uf2fp__URIUR",
          "uf2fp_default_dst_src__URIUR",
          "uf2fp_f16_to_8b_downconvert__URURI",
          "uf2fp_merge_c__URURI",
          "uf2fp_merge_c_default_dst_src__URURI"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.Uf2fpRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object Uf2fpRegRegImmReg extends Definition("Uf2fpRegRegImmReg", "UF2FP", Vec("uf2fp_f32_to_8b_downconvert__URIUR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Uf2fpRegRegImmReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Uf2fpRegRegReg extends Definition(
        "Uf2fpRegRegReg",
        "UF2FP",
        Vec(
          "uf2fp__URURUR",
          "uf2fp_default_dst_src__URURUR",
          "uf2fp_f16_to_8b_downconvert__URURUR",
          "uf2fp_merge_c__URURUR",
          "uf2fp_merge_c_default_dst_src__URURUR"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.Uf2fpRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object Uf2fpRegRegRegImm extends Definition("Uf2fpRegRegRegImm", "UF2FP", Vec("uf2fp_f32_to_8b_downconvert__URURI")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.Uf2fpRegRegRegImm(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object Uf2fpRegRegRegReg
      extends Definition("Uf2fpRegRegRegReg", "UF2FP", Vec("uf2fp_f32_to_8b_downconvert__URURUR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Uf2fpRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Uf2iRegImm extends Definition(
        "Uf2iRegImm",
        "UF2I",
        Vec(
          "uf2i__IU_32b",
          "uf2i__Ib_16b",
          "uf2i__Ib_bf16",
          "uf2i_swap__IU_32b",
          "uf2i_swap__Ib_16b",
          "uf2i_swap__Ib_bf16"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.Uf2iRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object Uf2iRegReg extends Definition(
        "Uf2iRegReg",
        "UF2I",
        Vec(
          "uf2i__URb_16b",
          "uf2i__URb_32b",
          "uf2i__URb_bf16",
          "uf2i_swap__URb_16b",
          "uf2i_swap__URb_32b",
          "uf2i_swap__URb_bf16"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.Uf2iRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object Uf2ipRegRegImmReg extends Definition("Uf2ipRegRegImmReg", "UF2IP", Vec("uf2ip__URIUR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Uf2ipRegRegImmReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Uf2ipRegRegRegImm extends Definition("Uf2ipRegRegRegImm", "UF2IP", Vec("uf2ip__RRI")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.Uf2ipRegRegRegImm(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object Uf2ipRegRegRegReg extends Definition("Uf2ipRegRegRegReg", "UF2IP", Vec("uf2ip__UUU")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Uf2ipRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object UfaddRegRegImm extends Definition("UfaddRegRegImm", "UFADD", Vec("ufadd__URURsI_URURI")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.UfaddRegRegImm(dst, srcA, sc, modifiers, guard))
        case _ => None
      }
  }
  object UfaddRegRegReg extends Definition("UfaddRegRegReg", "UFADD", Vec("ufadd__URURUR_UUU")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.UfaddRegRegReg(dst, srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object UffmaRegRegImmReg extends Definition("UffmaRegRegImmReg", "UFFMA", Vec("uffma__URIUR_URIUR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.UffmaRegRegImmReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object UffmaRegRegRegImm extends Definition("UffmaRegRegRegImm", "UFFMA", Vec("uffma__URURI_URURI")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.UffmaRegRegRegImm(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object UffmaRegRegRegReg extends Definition("UffmaRegRegRegReg", "UFFMA", Vec("uffma__UUU_UUU")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.UffmaRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object UfhaddRegRegReg extends Definition("UfhaddRegRegReg", "UFHADD", Vec("ufhadd__UUU")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.UfhaddRegRegReg(dst, srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object UfhfmaRegRegRegReg extends Definition("UfhfmaRegRegRegReg", "UFHFMA", Vec("ufhfma__UUU")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.UfhfmaRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object UfloRegImm extends Definition("UfloRegImm", "UFLO", Vec("uflo__URuIUR_URuIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.UfloRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object UfloRegPredImm extends Definition("UfloRegPredImm", "UFLO", Vec("uflo__URuIUR_URuIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.UfloRegPredImm(dst, uPu, sb, modifiers, guard))
        case _ => None
      }
  }
  object UfloRegPredReg extends Definition("UfloRegPredReg", "UFLO", Vec("uflo__URURUR_URURUR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.UfloRegPredReg(dst, uPu, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UfloRegReg extends Definition("UfloRegReg", "UFLO", Vec("uflo__URURUR_URURUR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.UfloRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UfmnmxRegPredRegImmPred
      extends Definition("UfmnmxRegPredRegImmPred", "UFMNMX", Vec("ufmnmx_pred__URsIUR_URIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UfmnmxRegPredRegImmPred(dst, uPu, srcA, sb, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UfmnmxRegPredRegRegPred
      extends Definition("UfmnmxRegPredRegRegPred", "UFMNMX", Vec("ufmnmx_pred__URURUR_UUU")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Predicate(uPu),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UfmnmxRegPredRegRegPred(dst, uPu, srcA, srcB, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UfmnmxRegRegImmPred extends Definition("UfmnmxRegRegImmPred", "UFMNMX", Vec("ufmnmx__URsIUR_URIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UfmnmxRegRegImmPred(dst, srcA, sb, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UfmnmxRegRegRegPred extends Definition("UfmnmxRegRegRegPred", "UFMNMX", Vec("ufmnmx__URURUR_UUU")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(uPp)
            ) => Some(Instruction.UfmnmxRegRegRegPred(dst, srcA, srcB, uPp, modifiers, guard))
        case _ => None
      }
  }
  object UfmulRegRegImm extends Definition("UfmulRegRegImm", "UFMUL", Vec("ufmul__URIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.UfmulRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object UfmulRegRegReg extends Definition("UfmulRegRegReg", "UFMUL", Vec("ufmul__UUU")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.UfmulRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UfrndRegImm extends Definition(
        "UfrndRegImm",
        "UFRND",
        Vec(
          "ufrnd__bf16_I",
          "ufrnd__f16_I",
          "ufrnd__f32_I",
          "ufrnd_swap__bf16_I",
          "ufrnd_swap__f16_I",
          "ufrnd_swap__f32_I"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.UfrndRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object UfrndRegReg extends Definition(
        "UfrndRegReg",
        "UFRND",
        Vec(
          "ufrnd__bf16_URb",
          "ufrnd__f16_URb",
          "ufrnd__f32_URb",
          "ufrnd_swap__bf16_URb",
          "ufrnd_swap__f16_URb",
          "ufrnd_swap__f32_URb"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.UfrndRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UfselRegRegImmPred extends Definition("UfselRegRegImmPred", "UFSEL", Vec("ufsel__URsIUR_URIR")) {
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
  object UfselRegRegRegPred extends Definition("UfselRegRegRegPred", "UFSEL", Vec("ufsel__URURUR_UUU")) {
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
  object UfsetRegRegImm extends Definition("UfsetRegRegImm", "UFSET", Vec("ufset_simple__URsIUR_URIR")) {
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
  object UfsetRegRegImmPred extends Definition("UfsetRegRegImmPred", "UFSET", Vec("ufset__URsIUR_URIR")) {
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
  object UfsetRegRegReg extends Definition("UfsetRegRegReg", "UFSET", Vec("ufset_simple__URURUR_UUU")) {
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
  object UfsetRegRegRegPred extends Definition("UfsetRegRegRegPred", "UFSET", Vec("ufset__URURUR_UUU")) {
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
  object UfsetpPredPredRegImmPred extends Definition("UfsetpPredPredRegImmPred", "UFSETP", Vec("ufsetp__URsIUR_URIR")) {
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
  object UfsetpPredPredRegRegPred extends Definition("UfsetpPredPredRegRegPred", "UFSETP", Vec("ufsetp__URURUR_UUU")) {
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
  object UfsetpPredRegImm extends Definition("UfsetpPredRegImm", "UFSETP", Vec("ufsetp_simple__URsIUR_URIR")) {
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
  object UfsetpPredRegReg extends Definition("UfsetpPredRegReg", "UFSETP", Vec("ufsetp_simple__URURUR_UUU")) {
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
  object UgetnextworkidAddressAddress extends Definition(
        "UgetnextworkidAddressAddress",
        "UGETNEXTWORKID",
        Vec("ugetnextworkid_", "ugetnextworkid_one_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA), InstructionOperand.Address(srcB)) =>
          Some(Instruction.UgetnextworkidAddressAddress(srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object Ui2fRegImm extends Definition("Ui2fRegImm", "UI2F", Vec("ui2f__IS_32b", "ui2f__IU_32b")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.Ui2fRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object Ui2fRegReg extends Definition("Ui2fRegReg", "UI2F", Vec("ui2f__UR_16b", "ui2f__UR_32b", "ui2f__UR_8b")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.Ui2fRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object Ui2fpRegImm extends Definition("Ui2fpRegImm", "UI2FP", Vec("ui2fp__URsIR", "ui2fp__URuIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.Ui2fpRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object Ui2fpRegReg extends Definition("Ui2fpRegReg", "UI2FP", Vec("ui2fp__UUU")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.Ui2fpRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object Ui2iRegImm extends Definition("Ui2iRegImm", "UI2I", Vec("ui2i__URsIUR_URIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.Ui2iRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object Ui2iRegReg extends Definition("Ui2iRegReg", "UI2I", Vec("ui2i__URURUR_UUU")) {
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
  object UiabsRegImm extends Definition("UiabsRegImm", "UIABS", Vec("uiabs__URsIUR_I")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.UiabsRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object UiabsRegReg extends Definition("UiabsRegReg", "UIABS", Vec("uiabs__URURUR_UR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.UiabsRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object Uiadd3RegPredPredRegImmReg
      extends Definition("Uiadd3RegPredPredRegImmReg", "UIADD3", Vec("uiadd3_64__URsIUR_RIR", "uiadd3__URsIUR_RIR")) {
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
  object Uiadd3RegPredRegImmReg
      extends Definition("Uiadd3RegPredRegImmReg", "UIADD3", Vec("uiadd3_64__URsIUR_RIR", "uiadd3__URsIUR_RIR")) {
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
  object Uiadd3RegPredRegRegReg
      extends Definition("Uiadd3RegPredRegRegReg", "UIADD3", Vec("uiadd3_64__URURUR_URURUR", "uiadd3__URURUR_URURUR")) {
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
  object Uiadd3RegRegImmReg
      extends Definition("Uiadd3RegRegImmReg", "UIADD3", Vec("uiadd3_64__URsIUR_RIR", "uiadd3__URsIUR_RIR")) {
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
  object Uiadd3RegRegRegReg
      extends Definition("Uiadd3RegRegRegReg", "UIADD3", Vec("uiadd3_64__URURUR_URURUR", "uiadd3__URURUR_URURUR")) {
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
  object UimadRegPredRegImmReg
      extends Definition("UimadRegPredRegImmReg", "UIMAD", Vec("uimad_hi__URsIUR_URIUR", "uimad_wide__URsIUR_URIUR")) {
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
  object UimadRegRegRegImm extends Definition("UimadRegRegRegImm", "UIMAD", Vec("uimad__URURsI_URURI")) {
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
  object UimadRegRegRegImmPred extends Definition("UimadRegRegRegImmPred", "UIMAD", Vec("uimad_x__URURsI_URURI")) {
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
  object UimnmxPredPredRegRegImmPred extends Definition(
        "UimnmxPredPredRegRegImmPred",
        "UIMNMX",
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
  object UimnmxPredRegRegRegPredPred
      extends Definition("UimnmxPredRegRegRegPredPred", "UIMNMX", Vec("uimnmx_64__UUU_URURUR", "uimnmx__UUU_URURUR")) {
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
  object UimnmxRegRegImmPredPred
      extends Definition("UimnmxRegRegImmPredPred", "UIMNMX", Vec("uimnmx_64__URIR_URsIUR", "uimnmx__URIR_URsIUR")) {
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
  object UimnmxRegRegRegPredPred
      extends Definition("UimnmxRegRegRegPredPred", "UIMNMX", Vec("uimnmx_64__UUU_URURUR", "uimnmx__UUU_URURUR")) {
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
  object UisetpPredPredRegImmPred
      extends Definition("UisetpPredPredRegImmPred", "UISETP", Vec("uisetp_64__URsIUR_URIR", "uisetp__URsIUR_URIR")) {
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
  object UldcRegAddress extends Definition("UldcRegAddress", "ULDC", Vec("uldc_imm_", "uldc_ur_offs_optional_upx_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Address(srcA)) =>
          Some(Instruction.UldcRegAddress(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object UldcRegAddressImm extends Definition("UldcRegAddressImm", "ULDC", Vec("uldc_const__RCxR")) {
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
  object UldcRegAddressPred extends Definition("UldcRegAddressPred", "ULDC", Vec("uldc_ur_offs_")) {
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
  object UldcRegConst extends Definition("UldcRegConst", "ULDC", Vec("uldc_const__RCR", "uldc_ur_offset_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(sa)) =>
          Some(Instruction.UldcRegConst(dst, sa, modifiers, guard))
        case _ => None
      }
  }
  object UleaRegPredRegImmImm extends Definition(
        "UleaRegPredRegImmImm",
        "ULEA",
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
  object UleaRegPredRegImmRegImm extends Definition("UleaRegPredRegImmRegImm", "ULEA", Vec("ulea_hi_imm__RuIR_URIR")) {
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
  object UleaRegPredRegImmRegImmPred
      extends Definition("UleaRegPredRegImmRegImmPred", "ULEA", Vec("ulea_hi_imm_x__RuIR_URIR")) {
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
  object UleaRegPredRegRegImmImm extends Definition("UleaRegPredRegRegImmImm", "ULEA", Vec("ulea_hi_imm__RRuI_RRI")) {
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
  object UleaRegPredRegRegImmImmPred
      extends Definition("UleaRegPredRegRegImmImmPred", "ULEA", Vec("ulea_hi_imm_x__RRuI_RRI")) {
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
  object UleaRegPredRegRegRegImm
      extends Definition("UleaRegPredRegRegRegImm", "ULEA", Vec("ulea_hi_noimm__URURUR_URURUR")) {
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
  object UleaRegPredRegRegRegImmPred
      extends Definition("UleaRegPredRegRegRegImmPred", "ULEA", Vec("ulea_hi_noimm_x__URURUR_URURUR")) {
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
  object UleaRegRegImmImm
      extends Definition("UleaRegRegImmImm", "ULEA", Vec("ulea_hi_imm_sx32__RuIR_URIUR", "ulea_lo_imm__URuIUR_URIUR")) {
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
  object UleaRegRegImmRegImm extends Definition("UleaRegRegImmRegImm", "ULEA", Vec("ulea_hi_imm__RuIR_URIR")) {
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
  object UleaRegRegImmRegImmPred
      extends Definition("UleaRegRegImmRegImmPred", "ULEA", Vec("ulea_hi_imm_x__RuIR_URIR")) {
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
  object UleaRegRegRegImmImm extends Definition("UleaRegRegRegImmImm", "ULEA", Vec("ulea_hi_imm__RRuI_RRI")) {
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
  object UleaRegRegRegImmImmPred extends Definition("UleaRegRegRegImmImmPred", "ULEA", Vec("ulea_hi_imm_x__RRuI_RRI")) {
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
  object UleaRegRegRegRegImm extends Definition("UleaRegRegRegRegImm", "ULEA", Vec("ulea_hi_noimm__URURUR_URURUR")) {
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
  object UleaRegRegRegRegImmPred
      extends Definition("UleaRegRegRegRegImmPred", "ULEA", Vec("ulea_hi_noimm_x__URURUR_URURUR")) {
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
  object UlepcReg extends Definition("UlepcReg", "ULEPC", Vec("ulepc__URURUR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst)) => Some(Instruction.UlepcReg(dst, modifiers, guard))
        case _ => None
      }
  }
  object UlepcRegImm extends Definition("UlepcRegImm", "ULEPC", Vec("ulepc__UR_I_R", "ulepc_rel_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sImm58)) =>
          Some(Instruction.UlepcRegImm(dst, sImm58, modifiers, guard))
        case _ => None
      }
  }
  object UlopPredRegRegImm extends Definition("UlopPredRegRegImm", "ULOP", Vec("ulop_imm_optionalUPp_")) {
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
  object UlopPredRegRegImmPred extends Definition("UlopPredRegRegImmPred", "ULOP", Vec("ulop_imm_")) {
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
  object UlopPredRegRegReg extends Definition("UlopPredRegRegReg", "ULOP", Vec("ulop_noimm_optionalUPp__URURUR")) {
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
  object UlopPredRegRegRegPred extends Definition("UlopPredRegRegRegPred", "ULOP", Vec("ulop_noimm__URURUR")) {
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
  object UlopRegRegImm extends Definition("UlopRegRegImm", "ULOP", Vec("ulop_imm_optionalUPp_")) {
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
  object UlopRegRegImmPred extends Definition("UlopRegRegImmPred", "ULOP", Vec("ulop_imm_")) {
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
  object UlopRegRegReg extends Definition("UlopRegRegReg", "ULOP", Vec("ulop_noimm_optionalUPp__URURUR")) {
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
  object UlopRegRegRegPred extends Definition("UlopRegRegRegPred", "ULOP", Vec("ulop_noimm__URURUR")) {
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
  object Ulop3PredRegRegImmReg
      extends Definition("Ulop3PredRegRegImmReg", "ULOP3", Vec("ulop3_imm_optionalUPp__URIR_URIR")) {
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
  object Ulop3PredRegRegImmRegImm
      extends Definition("Ulop3PredRegRegImmRegImm", "ULOP3", Vec("ulop3_lut_optionalUPp__URuIUR_URIR")) {
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
  object Ulop3PredRegRegImmRegImmPred
      extends Definition("Ulop3PredRegRegImmRegImmPred", "ULOP3", Vec("ulop3_lut__URuIUR_URIR")) {
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
  object Ulop3PredRegRegImmRegPred
      extends Definition("Ulop3PredRegRegImmRegPred", "ULOP3", Vec("ulop3_imm__URIR_URIR")) {
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
  object Ulop3PredRegRegRegReg
      extends Definition("Ulop3PredRegRegRegReg", "ULOP3", Vec("ulop3_noimm_optionalUPp__URURUR_URURUR")) {
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
  object Ulop3PredRegRegRegRegImm
      extends Definition("Ulop3PredRegRegRegRegImm", "ULOP3", Vec("ulop3_lut_optionalUPp__URURUR_URURUR")) {
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
  object Ulop3PredRegRegRegRegImmPred
      extends Definition("Ulop3PredRegRegRegRegImmPred", "ULOP3", Vec("ulop3_lut__URURUR_URURUR")) {
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
  object Ulop3PredRegRegRegRegPred
      extends Definition("Ulop3PredRegRegRegRegPred", "ULOP3", Vec("ulop3_noimm__URURUR_URURUR")) {
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
  object Ulop3RegRegImmReg extends Definition("Ulop3RegRegImmReg", "ULOP3", Vec("ulop3_imm_optionalUPp__URIR_URIR")) {
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
  object Ulop3RegRegImmRegImm
      extends Definition("Ulop3RegRegImmRegImm", "ULOP3", Vec("ulop3_lut_optionalUPp__URuIUR_URIR")) {
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
  object Ulop3RegRegImmRegImmPred
      extends Definition("Ulop3RegRegImmRegImmPred", "ULOP3", Vec("ulop3_lut__URuIUR_URIR")) {
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
  object Ulop3RegRegImmRegPred extends Definition("Ulop3RegRegImmRegPred", "ULOP3", Vec("ulop3_imm__URIR_URIR")) {
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
  object Ulop3RegRegRegReg
      extends Definition("Ulop3RegRegRegReg", "ULOP3", Vec("ulop3_noimm_optionalUPp__URURUR_URURUR")) {
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
  object Ulop3RegRegRegRegImm
      extends Definition("Ulop3RegRegRegRegImm", "ULOP3", Vec("ulop3_lut_optionalUPp__URURUR_URURUR")) {
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
  object Ulop3RegRegRegRegImmPred
      extends Definition("Ulop3RegRegRegRegImmPred", "ULOP3", Vec("ulop3_lut__URURUR_URURUR")) {
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
  object Ulop3RegRegRegRegPred extends Definition("Ulop3RegRegRegRegPred", "ULOP3", Vec("ulop3_noimm__URURUR_URURUR")) {
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
  object Ulop32iPredRegRegImm extends Definition("Ulop32iPredRegRegImm", "ULOP32I", Vec("ulop32i_optionalUPp_")) {
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
  object Ulop32iPredRegRegImmPred extends Definition("Ulop32iPredRegRegImmPred", "ULOP32I", Vec("ulop32i_")) {
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
  object Ulop32iRegRegImm extends Definition("Ulop32iRegRegImm", "ULOP32I", Vec("ulop32i_optionalUPp_")) {
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
  object Ulop32iRegRegImmPred extends Definition("Ulop32iRegRegImmPred", "ULOP32I", Vec("ulop32i_")) {
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
  object UmemsetsAddressRegReg extends Definition("UmemsetsAddressRegReg", "UMEMSETS", Vec("umemsets_")) {
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
  object UmovRegImm
      extends Definition("UmovRegImm", "UMOV", Vec("uiadd3_umov64__RIR_URsIUR", "umov__UI", "umov_imm64_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.UmovRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object UmovRegReg
      extends Definition("UmovRegReg", "UMOV", Vec("uiadd3_umov64__URURUR_URURUR", "umov_64__UR", "umov__UR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.UmovRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object Up2urReg extends Definition("Up2urReg", "UP2UR", Vec("up2ur_simple_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst)) => Some(Instruction.Up2urReg(dst, modifiers, guard))
        case _ => None
      }
  }
  object Up2urRegRegImm extends Definition("Up2urRegRegImm", "UP2UR", Vec("up2ur__Imm")) {
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
  object Up2urRegRegReg extends Definition("Up2urRegRegReg", "UP2UR", Vec("up2ur__URb")) {
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
  object Uplop3PredPredPredPred extends Definition("Uplop3PredPredPredPred", "UPLOP3", Vec("uplop3_1out_")) {
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
  object Uplop3PredPredPredPredImm extends Definition("Uplop3PredPredPredPredImm", "UPLOP3", Vec("uplop3_lut_1out_")) {
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
  object Uplop3PredPredPredPredPredImmImm
      extends Definition("Uplop3PredPredPredPredPredImmImm", "UPLOP3", Vec("uplop3_lut_2out_")) {
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
  object Uplop3PredPredPredRegPredImmImm
      extends Definition("Uplop3PredPredPredRegPredImmImm", "UPLOP3", Vec("uplop3_lut_2out_1reg__URURUR")) {
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
  object Uplop3PredPredPredRegRegImmImm
      extends Definition("Uplop3PredPredPredRegRegImmImm", "UPLOP3", Vec("uplop3_lut_2out_2reg__URURUR")) {
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
  object Uplop3PredPredRegPredImm
      extends Definition("Uplop3PredPredRegPredImm", "UPLOP3", Vec("uplop3_lut_1out_1reg__URURUR")) {
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
  object Uplop3PredPredRegRegImm
      extends Definition("Uplop3PredPredRegRegImm", "UPLOP3", Vec("uplop3_lut_1out_2reg__URURUR")) {
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
  object Uplop3PredPredRegRegRegImmImm
      extends Definition("Uplop3PredPredRegRegRegImmImm", "UPLOP3", Vec("uplop3_lut_2out_3reg__URURUR")) {
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
  object Uplop3PredRegRegRegImm
      extends Definition("Uplop3PredRegRegRegImm", "UPLOP3", Vec("uplop3_lut_1out_3reg__URURUR")) {
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
  object UpopcRegImm extends Definition("UpopcRegImm", "UPOPC", Vec("upopc__URuIUR_URIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.UpopcRegImm(dst, sb, modifiers, guard))
        case _ => None
      }
  }
  object UpopcRegReg extends Definition("UpopcRegReg", "UPOPC", Vec("upopc__URURUR_URUUR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.UpopcRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UprmtRegRegImmReg extends Definition("UprmtRegRegImmReg", "UPRMT", Vec("uprmt__URIUR")) {
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
  object UprmtRegRegRegReg extends Definition("UprmtRegRegRegReg", "UPRMT", Vec("uprmt__URURUR")) {
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
  object UpsetpPredPredPred extends Definition("UpsetpPredPredPred", "UPSETP", Vec("upsetp_simple_")) {
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
  object UpsetpPredPredPredPredPred extends Definition("UpsetpPredPredPredPredPred", "UPSETP", Vec("upsetp_")) {
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
  object Ur2upReg extends Definition("Ur2upReg", "UR2UP", Vec("ur2up__Imm")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA)) => Some(Instruction.Ur2upReg(srcA, modifiers, guard))
        case _ => None
      }
  }
  object Ur2upRegImm extends Definition("Ur2upRegImm", "UR2UP", Vec("ur2up__Imm")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.Ur2upRegImm(srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object Ur2upRegReg extends Definition("Ur2upRegReg", "UR2UP", Vec("ur2up__URb")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA), InstructionOperand.Register(srcB)) =>
          Some(Instruction.Ur2upRegReg(srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UredgrAddressReg extends Definition("UredgrAddressReg", "UREDGR", Vec("uredgr_", "uredgr_one_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA), InstructionOperand.Register(srcB)) =>
          Some(Instruction.UredgrAddressReg(srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UselRegRegImmPred extends Definition("UselRegRegImmPred", "USEL", Vec("usel_64__URIR", "usel__URuIUR_URIR")) {
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
  object UselRegRegRegPred extends Definition("UselRegRegRegPred", "USEL", Vec("usel_64__UUU", "usel__URURUR_UUU")) {
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
  object UsetmaxregImm
      extends Definition("UsetmaxregImm", "USETMAXREG", Vec("usetmaxregAlloc__Ib_dealloc", "usetmaxreg__Ib_dealloc")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb)) => Some(Instruction.UsetmaxregImm(sb, modifiers, guard))
        case _ => None
      }
  }
  object UsetmaxregPredImm
      extends Definition("UsetmaxregPredImm", "USETMAXREG", Vec("usetmaxregAlloc__Ib_alloc", "usetmaxreg__Ib_alloc")) {
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
        Vec("usetmaxregAlloc__URb_dealloc", "usetmaxreg__URb_dealloc")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcB)) => Some(Instruction.UsetmaxregReg(srcB, modifiers, guard))
        case _ => None
      }
  }
  object UsetshmszNoOperands extends Definition("UsetshmszNoOperands", "USETSHMSZ", Vec("usetshmsz__FLUSH")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.UsetshmszNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object UsetshmszImm extends Definition("UsetshmszImm", "USETSHMSZ", Vec("usetshmsz__Ib")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb)) => Some(Instruction.UsetshmszImm(sb, modifiers, guard))
        case _ => None
      }
  }
  object UsetshmszReg extends Definition("UsetshmszReg", "USETSHMSZ", Vec("usetshmsz__URb")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcB)) => Some(Instruction.UsetshmszReg(srcB, modifiers, guard))
        case _ => None
      }
  }
  object UsgxtRegRegImm extends Definition("UsgxtRegRegImm", "USGXT", Vec("usgxt__URuIUR_URIR")) {
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
  object UsgxtRegRegReg extends Definition("UsgxtRegRegReg", "USGXT", Vec("usgxt__URURUR_UUU")) {
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
  object UshfRegRegImmReg extends Definition("UshfRegRegImmReg", "USHF", Vec("ushf__URuIUR_URIR")) {
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
  object UshfRegRegRegImm extends Definition("UshfRegRegRegImm", "USHF", Vec("ushf__URURuI_URRI")) {
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
  object UshfRegRegRegReg extends Definition("UshfRegRegRegReg", "USHF", Vec("ushf__URURUR_URURUR")) {
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
  object UshlRegImmReg extends Definition("UshlRegImmReg", "USHL", Vec("ushl_imm_")) {
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
  object UshlRegRegImm extends Definition("UshlRegRegImm", "USHL", Vec("ushl__URuIUR_URIR")) {
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
  object UshlRegRegReg extends Definition("UshlRegRegReg", "USHL", Vec("ushl__URURUR_URURUR")) {
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
  object UshrRegRegImm extends Definition("UshrRegRegImm", "USHR", Vec("ushr__URIR_URIR")) {
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
  object UshrRegRegReg extends Definition("UshrRegRegReg", "USHR", Vec("ushr__URURUR_URURUR")) {
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
  object UstgrAddressReg extends Definition("UstgrAddressReg", "USTGR", Vec("ustgr_", "ustgr_one_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA), InstructionOperand.Register(srcB)) =>
          Some(Instruction.UstgrAddressReg(srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UtcatomswsPredRegReg
      extends Definition("UtcatomswsPredRegReg", "UTCATOMSWS", Vec("utcatomsws_fas_", "utcatomsws_fas_one_")) {
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
  object UtcatomswsPredRegRegReg
      extends Definition("UtcatomswsPredRegRegReg", "UTCATOMSWS", Vec("utcatomsws_cas_", "utcatomsws_cas_one_")) {
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
  object UtcatomswsRegReg
      extends Definition("UtcatomswsRegReg", "UTCATOMSWS", Vec("utcatomsws_op_", "utcatomsws_op_one_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.UtcatomswsRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UtcbarNoOperands extends Definition("UtcbarNoOperands", "UTCBAR", Vec("utcbar_flush_", "utcbar_flush_one_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.UtcbarNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object UtcbarAddressRegReg extends Definition(
        "UtcbarAddressRegReg",
        "UTCBAR",
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
  object UtccpAddressAddress extends Definition(
        "UtccpAddressAddress",
        "UTCCP",
        Vec("utccp__1CTA", "utccp__2CTA", "utccp_one__1CTA", "utccp_one__2CTA")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA), InstructionOperand.Address(srcB)) =>
          Some(Instruction.UtccpAddressAddress(srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object UtchmmaAddressAddressAddressAddressAddressRegPred extends Definition(
        "UtchmmaAddressAddressAddressAddressAddressRegPred",
        "UTCHMMA",
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
  object UtcimmaAddressAddressAddressAddressAddressRegPred extends Definition(
        "UtcimmaAddressAddressAddressAddressAddressRegPred",
        "UTCIMMA",
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
  object UtcommaAddressAddressAddressAddressAddressAddressPred extends Definition(
        "UtcommaAddressAddressAddressAddressAddressAddressPred",
        "UTCOMMA",
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
  object UtcqmmaAddressAddressAddressAddressAddressAddressPred extends Definition(
        "UtcqmmaAddressAddressAddressAddressAddressAddressPred",
        "UTCQMMA",
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
  object UtcshiftAddress extends Definition(
        "UtcshiftAddress",
        "UTCSHIFT",
        Vec("utcshift__1CTA", "utcshift__2CTA", "utcshift_one__1CTA", "utcshift_one__2CTA")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA)) => Some(Instruction.UtcshiftAddress(srcA, modifiers, guard))
        case _ => None
      }
  }
  object UtmacctlNoOperands extends Definition("UtmacctlNoOperands", "UTMACCTL", Vec("utmacctl_", "utmacctl_one_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.UtmacctlNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object UtmacctlAddress extends Definition("UtmacctlAddress", "UTMACCTL", Vec("utmacctl_URa_", "utmacctl_URa_one_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA)) => Some(Instruction.UtmacctlAddress(srcA, modifiers, guard))
        case _ => None
      }
  }
  object UtmacmdflushNoOperands extends Definition("UtmacmdflushNoOperands", "UTMACMDFLUSH", Vec("utmacmdflush_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.UtmacmdflushNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object UtmaldgAddressAddress
      extends Definition("UtmaldgAddressAddress", "UTMALDG", Vec("utmaldg__UUU", "utmaldg_one__UUU")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcB), InstructionOperand.Address(srcA)) =>
          Some(Instruction.UtmaldgAddressAddress(srcB, srcA, modifiers, guard))
        case _ => None
      }
  }
  object UtmaldgAddressAddressAddress
      extends Definition("UtmaldgAddressAddressAddress", "UTMALDG", Vec("utmaldg__UUU_desc", "utmaldg_one__UUU_desc")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcB), InstructionOperand.Address(srcA), InstructionOperand.Address(uRe)) =>
          Some(Instruction.UtmaldgAddressAddressAddress(srcB, srcA, uRe, modifiers, guard))
        case _ => None
      }
  }
  object UtmaldgAddressAddressReg
      extends Definition("UtmaldgAddressAddressReg", "UTMALDG", Vec("utmaldg_URc__UUU", "utmaldg_URc_one__UUU")) {
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
  object UtmapfAddressAddress
      extends Definition("UtmapfAddressAddress", "UTMAPF", Vec("utmapf__UUU", "utmapf_one__UUU")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcB), InstructionOperand.Address(srcA)) =>
          Some(Instruction.UtmapfAddressAddress(srcB, srcA, modifiers, guard))
        case _ => None
      }
  }
  object UtmapfAddressAddressAddress
      extends Definition("UtmapfAddressAddressAddress", "UTMAPF", Vec("utmapf__UUU_desc", "utmapf_one__UUU_desc")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcB), InstructionOperand.Address(srcA), InstructionOperand.Address(uRe)) =>
          Some(Instruction.UtmapfAddressAddressAddress(srcB, srcA, uRe, modifiers, guard))
        case _ => None
      }
  }
  object UtmapfAddressAddressReg
      extends Definition("UtmapfAddressAddressReg", "UTMAPF", Vec("utmapf_URc__UUU", "utmapf_URc_one__UUU")) {
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
  object UtmaredgAddressAddress
      extends Definition("UtmaredgAddressAddress", "UTMAREDG", Vec("utmaredg__UUU", "utmaredg_one__UUU")) {
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
        Vec("utmaredg__UUU_desc", "utmaredg_one__UUU_desc")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcB), InstructionOperand.Address(srcA), InstructionOperand.Address(uRe)) =>
          Some(Instruction.UtmaredgAddressAddressAddress(srcB, srcA, uRe, modifiers, guard))
        case _ => None
      }
  }
  object UtmastgAddressAddress
      extends Definition("UtmastgAddressAddress", "UTMASTG", Vec("utmastg__UUU", "utmastg_one__UUU")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcB), InstructionOperand.Address(srcA)) =>
          Some(Instruction.UtmastgAddressAddress(srcB, srcA, modifiers, guard))
        case _ => None
      }
  }
  object UtmastgAddressAddressAddress
      extends Definition("UtmastgAddressAddressAddress", "UTMASTG", Vec("utmastg__UUU_desc", "utmastg_one__UUU_desc")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcB), InstructionOperand.Address(srcA), InstructionOperand.Address(uRe)) =>
          Some(Instruction.UtmastgAddressAddressAddress(srcB, srcA, uRe, modifiers, guard))
        case _ => None
      }
  }
  val definitions: Vec[Definition] = Vec(
    TxdRegRegRegRegRegImm,
    TxdRegRegRegRegRegImmImm,
    TxdRegRegRegRegRegReg,
    TxdRegRegRegRegRegRegImm,
    TxdRegRegRegRegRegRegImmImm,
    TxqRegRegImm,
    TxqRegRegImmImm,
    TxqRegRegImmImmImm,
    TxqRegRegImmImmImmImm,
    TxqRegRegRegImm,
    TxqRegRegRegImmImmImm,
    TxqRegRegRegImmImmImmImm,
    TxqRegRegRegRegImm,
    TxqRegRegRegRegImmImm,
    UblkcpAddressAddressReg,
    UblkcpAddressAddressRegAddress,
    UblkpfAddressReg,
    UblkpfAddressRegAddress,
    UblkredAddressAddressReg,
    UblkredAddressAddressRegAddress,
    UbmskRegRegImm,
    UbmskRegRegReg,
    UbrevRegImm,
    UbrevRegReg,
    UcgabararvNoOperands,
    UcgabargetReg,
    UcgabarsetReg,
    UcgabarwaitNoOperands,
    Ucgabar_ArvNoOperands,
    Ucgabar_WaitNoOperands,
    UcleaRegPredRegImmImm,
    UcleaRegPredRegRegImm,
    Uf2fRegImm,
    Uf2fRegReg,
    Uf2fpRegImm,
    Uf2fpRegImmReg,
    Uf2fpRegReg,
    Uf2fpRegRegImm,
    Uf2fpRegRegImmReg,
    Uf2fpRegRegReg,
    Uf2fpRegRegRegImm,
    Uf2fpRegRegRegReg,
    Uf2iRegImm,
    Uf2iRegReg,
    Uf2ipRegRegImmReg,
    Uf2ipRegRegRegImm,
    Uf2ipRegRegRegReg,
    UfaddRegRegImm,
    UfaddRegRegReg,
    UffmaRegRegImmReg,
    UffmaRegRegRegImm,
    UffmaRegRegRegReg,
    UfhaddRegRegReg,
    UfhfmaRegRegRegReg,
    UfloRegImm,
    UfloRegPredImm,
    UfloRegPredReg,
    UfloRegReg,
    UfmnmxRegPredRegImmPred,
    UfmnmxRegPredRegRegPred,
    UfmnmxRegRegImmPred,
    UfmnmxRegRegRegPred,
    UfmulRegRegImm,
    UfmulRegRegReg,
    UfrndRegImm,
    UfrndRegReg,
    UfselRegRegImmPred,
    UfselRegRegRegPred,
    UfsetRegRegImm,
    UfsetRegRegImmPred,
    UfsetRegRegReg,
    UfsetRegRegRegPred,
    UfsetpPredPredRegImmPred,
    UfsetpPredPredRegRegPred,
    UfsetpPredRegImm,
    UfsetpPredRegReg,
    UgetnextworkidAddressAddress,
    Ui2fRegImm,
    Ui2fRegReg,
    Ui2fpRegImm,
    Ui2fpRegReg,
    Ui2iRegImm,
    Ui2iRegReg,
    Ui2ipRegRegImmReg,
    Ui2ipRegRegRegReg,
    UiabsRegImm,
    UiabsRegReg,
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
    UisetpPredPredRegImmPred,
    UisetpPredPredRegImmPredPred,
    UisetpPredPredRegRegPred,
    UisetpPredPredRegRegPredPred,
    UisetpPredRegImm,
    UisetpPredRegImmPred,
    UisetpPredRegReg,
    UisetpPredRegRegPred,
    UldcRegAddress,
    UldcRegAddressImm,
    UldcRegAddressPred,
    UldcRegConst,
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
    UlepcReg,
    UlepcRegImm,
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
    UmemsetsAddressRegReg,
    UmovRegImm,
    UmovRegReg,
    Up2urReg,
    Up2urRegRegImm,
    Up2urRegRegReg,
    Uplop3PredPredPredPred,
    Uplop3PredPredPredPredImm,
    Uplop3PredPredPredPredPredImmImm,
    Uplop3PredPredPredRegPredImmImm,
    Uplop3PredPredPredRegRegImmImm,
    Uplop3PredPredRegPredImm,
    Uplop3PredPredRegRegImm,
    Uplop3PredPredRegRegRegImmImm,
    Uplop3PredRegRegRegImm,
    UpopcRegImm,
    UpopcRegReg,
    UprmtRegRegImmReg,
    UprmtRegRegRegReg,
    UpsetpPredPredPred,
    UpsetpPredPredPredPredPred,
    Ur2upReg,
    Ur2upRegImm,
    Ur2upRegReg,
    UredgrAddressReg,
    UselRegRegImmPred,
    UselRegRegRegPred,
    UsetmaxregImm,
    UsetmaxregPredImm,
    UsetmaxregPredReg,
    UsetmaxregReg,
    UsetshmszNoOperands,
    UsetshmszImm,
    UsetshmszReg,
    UsgxtRegRegImm,
    UsgxtRegRegReg,
    UshfRegRegImmReg,
    UshfRegRegRegImm,
    UshfRegRegRegReg,
    UshlRegImmReg,
    UshlRegRegImm,
    UshlRegRegReg,
    UshrRegRegImm,
    UshrRegRegReg,
    UstgrAddressReg,
    UtcatomswsPredRegReg,
    UtcatomswsPredRegRegReg,
    UtcatomswsRegReg,
    UtcbarNoOperands,
    UtcbarAddressRegReg,
    UtccpAddressAddress,
    UtchmmaAddressAddressAddressAddressAddressRegPred,
    UtchmmaAddressAddressAddressAddressAddressRegPredImm,
    UtcimmaAddressAddressAddressAddressAddressRegPred,
    UtcommaAddressAddressAddressAddressAddressAddressPred,
    UtcqmmaAddressAddressAddressAddressAddressAddressPred,
    UtcqmmaAddressAddressAddressAddressAddressRegPred,
    UtcshiftAddress,
    UtmacctlNoOperands,
    UtmacctlAddress,
    UtmacmdflushNoOperands,
    UtmaldgAddressAddress,
    UtmaldgAddressAddressAddress,
    UtmaldgAddressAddressReg,
    UtmaldgAddressAddressRegAddress,
    UtmapfAddressAddress,
    UtmapfAddressAddressAddress,
    UtmapfAddressAddressReg,
    UtmapfAddressAddressRegAddress,
    UtmaredgAddressAddress,
    UtmaredgAddressAddressAddress,
    UtmastgAddressAddress,
    UtmastgAddressAddressAddress
  )
}
