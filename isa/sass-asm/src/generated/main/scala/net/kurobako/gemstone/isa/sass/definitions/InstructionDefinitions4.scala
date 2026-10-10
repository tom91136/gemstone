// Generated SASS instruction definitions and checked operand constructors. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.sass
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import Instruction.*
@javax.annotation.processing.Generated(Array("sbt codegen"))
object InstructionDefinitions4 {
  object SetcrsptrReg extends Definition("SetcrsptrReg", "SETCRSPTR", Vec("SETCRSPTR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA)) => Some(Instruction.SetcrsptrReg(srcA, modifiers, guard))
        case _ => None
      }
  }
  object SetctaidReg extends Definition("SetctaidReg", "SETCTAID", Vec("setctaid_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA)) => Some(Instruction.SetctaidReg(srcA, modifiers, guard))
        case _ => None
      }
  }
  object SetlmembaseReg extends Definition("SetlmembaseReg", "SETLMEMBASE", Vec("SETLMEMBASE", "setlmembase_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA)) => Some(Instruction.SetlmembaseReg(srcA, modifiers, guard))
        case _ => None
      }
  }
  object SgxtRegRegAddressImm extends Definition("SgxtRegRegAddressImm", "SGXT", Vec("sgxt__RCxR_RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.SgxtRegRegAddressImm(dst, srcA, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object SgxtRegRegConst extends Definition("SgxtRegRegConst", "SGXT", Vec("sgxt__RCR_RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb)
            ) => Some(Instruction.SgxtRegRegConst(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object SgxtRegRegImm extends Definition("SgxtRegRegImm", "SGXT", Vec("sgxt__RuIR_RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.SgxtRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object SgxtRegRegReg extends Definition("SgxtRegRegReg", "SGXT", Vec("sgxt__RRR_RRR", "sgxt__RUR_RUR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.SgxtRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object ShfRegRegAddressImmReg extends Definition("ShfRegRegAddressImmReg", "SHF", Vec("shf__RCxR_RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.ShfRegRegAddressImmReg(dst, srcA, srcB, sb_offset, srcC, modifiers, guard))
        case _ => None
      }
  }
  object ShfRegRegConstReg extends Definition("ShfRegRegConstReg", "SHF", Vec("shf__RCR_RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.ShfRegRegConstReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object ShfRegRegImmReg extends Definition(
        "ShfRegRegImmReg",
        "SHF",
        Vec("SHF_L imm", "SHF_L_imm", "SHF_R imm", "SHF_R_imm", "shf__RuIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.ShfRegRegImmReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object ShfRegRegRegAddressImm extends Definition("ShfRegRegRegAddressImm", "SHF", Vec("shf__RRCx_RRCx")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Address(srcC),
              InstructionOperand.Immediate(sc_offset)
            ) => Some(Instruction.ShfRegRegRegAddressImm(dst, srcA, srcB, srcC, sc_offset, modifiers, guard))
        case _ => None
      }
  }
  object ShfRegRegRegConst extends Definition("ShfRegRegRegConst", "SHF", Vec("shf__RRC_RRC")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(sc)
            ) => Some(Instruction.ShfRegRegRegConst(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object ShfRegRegRegImm extends Definition("ShfRegRegRegImm", "SHF", Vec("shf__RRuI_RRI")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.ShfRegRegRegImm(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object ShfRegRegRegReg extends Definition(
        "ShfRegRegRegReg",
        "SHF",
        Vec("SHF_L reg", "SHF_L_reg", "SHF_R reg", "SHF_R_reg", "shf__RRR_RRR", "shf__RRU_RRU", "shf__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.ShfRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object ShflPredRegRegImmImm
      extends Definition("ShflPredRegRegImmImm", "SHFL", Vec("c b SHFL", "c_b_SHFL", "shfl__RII")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm5),
              InstructionOperand.Immediate(uImm13)
            ) => Some(Instruction.ShflPredRegRegImmImm(dstPredicate, dst, srcA, uImm5, uImm13, modifiers, guard))
        case _ => None
      }
  }
  object ShflPredRegRegImmReg extends Definition("ShflPredRegRegImmReg", "SHFL", Vec("b SHFL", "b_SHFL", "shfl__RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm5),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.ShflPredRegRegImmReg(dstPredicate, dst, srcA, uImm5, srcC, modifiers, guard))
        case _ => None
      }
  }
  object ShflPredRegRegRegImm extends Definition("ShflPredRegRegRegImm", "SHFL", Vec("c SHFL", "c_SHFL", "shfl__RRI")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(uImm13)
            ) => Some(Instruction.ShflPredRegRegRegImm(dstPredicate, dst, srcA, srcB, uImm13, modifiers, guard))
        case _ => None
      }
  }
  object ShflPredRegRegRegReg extends Definition("ShflPredRegRegRegReg", "SHFL", Vec("SHFL", "shfl__RRR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.ShflPredRegRegRegReg(dstPredicate, dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object ShintImmImm extends Definition("ShintImmImm", "SHINT", Vec("SHINT")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(uImmMsb), InstructionOperand.Immediate(uImmLsb)) =>
          Some(Instruction.ShintImmImm(uImmMsb, uImmLsb, modifiers, guard))
        case _ => None
      }
  }
  object ShintImmImmImmImmImmImmImmImmImmImmImmImmImmImm
      extends Definition("ShintImmImmImmImmImmImmImmImmImmImmImmImmImmImm", "SHINT", Vec("SHINT nibbles")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Immediate(uImmA),
              InstructionOperand.Immediate(uImmB),
              InstructionOperand.Immediate(uImmC),
              InstructionOperand.Immediate(uImmD),
              InstructionOperand.Immediate(uImmE),
              InstructionOperand.Immediate(uImmF),
              InstructionOperand.Immediate(uImmG),
              InstructionOperand.Immediate(uImmH),
              InstructionOperand.Immediate(uImmI),
              InstructionOperand.Immediate(uImmJ),
              InstructionOperand.Immediate(uImmK),
              InstructionOperand.Immediate(uImmL),
              InstructionOperand.Immediate(uImmM),
              InstructionOperand.Immediate(uImmN)
            ) => Some(Instruction.ShintImmImmImmImmImmImmImmImmImmImmImmImmImmImm(
            uImmA,
            uImmB,
            uImmC,
            uImmD,
            uImmE,
            uImmF,
            uImmG,
            uImmH,
            uImmI,
            uImmJ,
            uImmK,
            uImmL,
            uImmM,
            uImmN,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object ShlRegImmReg extends Definition("ShlRegImmReg", "SHL", Vec("shl_imm_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Immediate(sa),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.ShlRegImmReg(dst, sa, srcB, modifiers, guard))
        case _ => None
      }
  }
  object ShlRegRegAddressImm extends Definition("ShlRegRegAddressImm", "SHL", Vec("shl__RCxR_RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.ShlRegRegAddressImm(dst, srcA, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object ShlRegRegConst extends Definition("ShlRegRegConst", "SHL", Vec("Const SHL", "Const_SHL", "shl__RCR_RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb)
            ) => Some(Instruction.ShlRegRegConst(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object ShlRegRegImm extends Definition("ShlRegRegImm", "SHL", Vec("Imm SHL", "Imm_SHL", "shl__RuIR_RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.ShlRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object ShlRegRegReg extends Definition("ShlRegRegReg", "SHL", Vec("SHL", "shl__RRR_RRR", "shl__RUR_RUR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.ShlRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object ShrRegRegAddressImm extends Definition("ShrRegRegAddressImm", "SHR", Vec("shr__RCxR_RCxR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.ShrRegRegAddressImm(dst, srcA, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object ShrRegRegConst extends Definition("ShrRegRegConst", "SHR", Vec("Const SHR", "Const_SHR", "shr__RCR_RCR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb)
            ) => Some(Instruction.ShrRegRegConst(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object ShrRegRegImm extends Definition("ShrRegRegImm", "SHR", Vec("Imm SHR", "Imm_SHR", "shr__RIR_RIR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.ShrRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object ShrRegRegReg extends Definition("ShrRegRegReg", "SHR", Vec("SHR", "shr__RRR_RRR", "shr__RUR_RUR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.ShrRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object SpmetadataRegRegRegImmImmImm
      extends Definition("SpmetadataRegRegRegImmImmImm", "SPMETADATA", Vec("spmetadata_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(vecidx),
              InstructionOperand.Immediate(mask),
              InstructionOperand.Immediate(destidx)
            ) =>
          Some(Instruction.SpmetadataRegRegRegImmImmImm(dst, srcB, srcC, vecidx, mask, destidx, modifiers, guard))
        case _ => None
      }
  }
  object SsyConst extends Definition("SsyConst", "SSY", Vec("SSY c", "SSY_c")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(srcConst)) => Some(Instruction.SsyConst(srcConst, modifiers, guard))
        case _ => None
      }
  }
  object SsyImm extends Definition("SsyImm", "SSY", Vec("SSY")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sImm)) => Some(Instruction.SsyImm(sImm, modifiers, guard))
        case _ => None
      }
  }
  object StAddressReg extends Definition(
        "StAddressReg",
        "ST",
        Vec(
          "I ST",
          "I_ST",
          "ST",
          "st__sImmOffset",
          "st__uImmOffset",
          "st_memdesc__Ra64",
          "st_uniform__Ra32",
          "st_uniform__Ra64",
          "st_uniform__RaRZ",
          "st_weakonly__sImmOffset",
          "st_weakonly__uImmOffset",
          "st_weakonly_uniform__Ra32",
          "st_weakonly_uniform__Ra64",
          "st_weakonly_uniform__RaRZ"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA), InstructionOperand.Register(srcB)) =>
          Some(Instruction.StAddressReg(srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object StAddressRegPred extends Definition("StAddressRegPred", "ST", Vec("I_ST", "ST")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(plg)
            ) => Some(Instruction.StAddressRegPred(srcA, srcB, plg, modifiers, guard))
        case _ => None
      }
  }
  object StasAddressReg
      extends Definition("StasAddressReg", "STAS", Vec("stas_64__Ra64", "stas_64__RaRZ", "stas__Ra32")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA), InstructionOperand.Register(srcB)) =>
          Some(Instruction.StasAddressReg(srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object StgAddressReg extends Definition(
        "StgAddressReg",
        "STG",
        Vec(
          "I_STG",
          "STG",
          "stg__sImmOffset",
          "stg__uImmOffset",
          "stg_memdesc__Ra64",
          "stg_uniform__Ra32",
          "stg_uniform__Ra64",
          "stg_uniform__RaRZ",
          "stg_weakonly__sImmOffset",
          "stg_weakonly__uImmOffset",
          "stg_weakonly_uniform__Ra32",
          "stg_weakonly_uniform__Ra64",
          "stg_weakonly_uniform__RaRZ"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA), InstructionOperand.Register(srcB)) =>
          Some(Instruction.StgAddressReg(srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object StgAddressRegReg extends Definition(
        "StgAddressRegReg",
        "STG",
        Vec("stg_256_memdesc__Ra64", "stg_256_uniform__Ra32", "stg_256_uniform__Ra64", "stg_256_uniform__RaRZ")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(rb2)
            ) => Some(Instruction.StgAddressRegReg(srcA, srcB, rb2, modifiers, guard))
        case _ => None
      }
  }
  object StgAddressRegRegImm extends Definition(
        "StgAddressRegRegImm",
        "STG",
        Vec("stg_256_memdesc__Ra64", "stg_256_uniform__Ra32", "stg_256_uniform__Ra64", "stg_256_uniform__RaRZ")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(rb2),
              InstructionOperand.Immediate(word_mask)
            ) => Some(Instruction.StgAddressRegRegImm(srcA, srcB, rb2, word_mask, modifiers, guard))
        case _ => None
      }
  }
  object StlAddressReg extends Definition(
        "StlAddressReg",
        "STL",
        Vec("I STL", "I_STL", "STL", "stl__sImmOffset", "stl__uImmOffset", "stl_memdesc_", "stl_uniform_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA), InstructionOperand.Register(srcB)) =>
          Some(Instruction.StlAddressReg(srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object StpImm extends Definition("StpImm", "STP", Vec("STP")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(uImm)) => Some(Instruction.StpImm(uImm, modifiers, guard))
        case _ => None
      }
  }
  object StsAddressReg extends Definition(
        "StsAddressReg",
        "STS",
        Vec("I STS", "I_STS", "STS", "sts__sImmOffset", "sts__uImmOffset", "sts_uniform_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA), InstructionOperand.Register(srcB)) =>
          Some(Instruction.StsAddressReg(srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object StsculPredAddressReg extends Definition("StsculPredAddressReg", "STSCUL", Vec("I STSCUL", "STSCUL")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.StsculPredAddressReg(dstPredicate, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object StsmAddressReg extends Definition("StsmAddressReg", "STSM", Vec("stsm__UR_sI_R", "stsm__sImmOffset")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA), InstructionOperand.Register(srcB)) =>
          Some(Instruction.StsmAddressReg(srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object SttAddressReg extends Definition("SttAddressReg", "STT", Vec("stt_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcC), InstructionOperand.Register(srcB)) =>
          Some(Instruction.SttAddressReg(srcC, srcB, modifiers, guard))
        case _ => None
      }
  }
  object SttmAddressReg extends Definition("SttmAddressReg", "STTM", Vec("sttm_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcC), InstructionOperand.Register(srcB)) =>
          Some(Instruction.SttmAddressReg(srcC, srcB, modifiers, guard))
        case _ => None
      }
  }
  object SuatomPredRegAddressRegImm
      extends Definition("SuatomPredRegAddressRegImm", "SUATOM", Vec("SUATOM_D_CAS_IMM")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(suid)
            ) => Some(Instruction.SuatomPredRegAddressRegImm(dstPredicate, dst, srcA, srcB, suid, modifiers, guard))
        case _ => None
      }
  }
  object SuatomPredRegAddressRegImmImm extends Definition(
        "SuatomPredRegAddressRegImmImm",
        "SUATOM",
        Vec("suatom_cas_imm_", "suatom_cas_imm_weakonly_", "suatom_imm_", "suatom_imm_weakonly_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(tsPtrIdx)
            ) => Some(Instruction.SuatomPredRegAddressRegImmImm(pu, dst, srcA, srcB, bank5, tsPtrIdx, modifiers, guard))
        case _ => None
      }
  }
  object SuatomPredRegAddressRegImmImmImm extends Definition(
        "SuatomPredRegAddressRegImmImmImm",
        "SUATOM",
        Vec("suatom_cas_tid_", "suatom_cas_tid_weakonly_", "suatom_tid_", "suatom_tid_weakonly_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(hdrtblbase6),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.SuatomPredRegAddressRegImmImmImm(
            pu,
            dst,
            srcA,
            srcB,
            bank5,
            hdrtblbase6,
            tid8,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object SuatomPredRegAddressRegReg extends Definition(
        "SuatomPredRegAddressRegReg",
        "SUATOM",
        Vec(
          "SUATOM_D_CAS_REG",
          "SUATOM_D_REG_SPARSE",
          "suatom_cas_reg_",
          "suatom_cas_reg_weakonly_",
          "suatom_cas_urc_",
          "suatom_reg_",
          "suatom_reg_weakonly_",
          "suatom_urc_"
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
            ) => Some(Instruction.SuatomPredRegAddressRegReg(dstPredicate, dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object SuatomPredRegAddressRegRegImm extends Definition(
        "SuatomPredRegAddressRegRegImm",
        "SUATOM",
        Vec("suatom_cas_urc_", "suatom_cas_urc_weakonly_", "suatom_urc_", "suatom_urc_weakonly_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.SuatomPredRegAddressRegRegImm(pu, dst, srcA, srcB, srcC, tid8, modifiers, guard))
        case _ => None
      }
  }
  object SuatomPredRegAddressRegRegReg extends Definition(
        "SuatomPredRegAddressRegRegReg",
        "SUATOM",
        Vec("suatom_cas_reg_", "suatom_cas_urc_", "suatom_reg_", "suatom_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe)
            ) => Some(Instruction.SuatomPredRegAddressRegRegReg(pu, dst, srcA, srcB, srcC, uRe, modifiers, guard))
        case _ => None
      }
  }
  object SuatomPredRegAddressRegRegRegImm
      extends Definition("SuatomPredRegAddressRegRegRegImm", "SUATOM", Vec("suatom_cas_urc_", "suatom_urc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe),
              InstructionOperand.Immediate(tid8)
            ) =>
          Some(Instruction.SuatomPredRegAddressRegRegRegImm(pu, dst, srcA, srcB, srcC, uRe, tid8, modifiers, guard))
        case _ => None
      }
  }
  object SuatomPredRegAddressRegRegRegReg
      extends Definition("SuatomPredRegAddressRegRegRegReg", "SUATOM", Vec("suatom_cas_r_urc_", "suatom_r_urc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC1),
              InstructionOperand.Register(srcC2),
              InstructionOperand.Register(uRe)
            ) =>
          Some(Instruction.SuatomPredRegAddressRegRegRegReg(pu, dst, srcA, srcB, srcC1, srcC2, uRe, modifiers, guard))
        case _ => None
      }
  }
  object SuatomRegAddressRegImm extends Definition(
        "SuatomRegAddressRegImm",
        "SUATOM",
        Vec("SUATOM_D_CAS_IMM", "SUATOM_D_IMM", "SUATOM_P_CAS_IMM", "SUATOM_P_IMM")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(suid)
            ) => Some(Instruction.SuatomRegAddressRegImm(dst, srcA, srcB, suid, modifiers, guard))
        case _ => None
      }
  }
  object SuatomRegAddressRegImmImm extends Definition(
        "SuatomRegAddressRegImmImm",
        "SUATOM",
        Vec("suatom_cas_imm_", "suatom_cas_imm_weakonly_", "suatom_imm_", "suatom_imm_weakonly_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(tsPtrIdx)
            ) => Some(Instruction.SuatomRegAddressRegImmImm(dst, srcA, srcB, bank5, tsPtrIdx, modifiers, guard))
        case _ => None
      }
  }
  object SuatomRegAddressRegImmImmImm extends Definition(
        "SuatomRegAddressRegImmImmImm",
        "SUATOM",
        Vec("suatom_cas_tid_", "suatom_cas_tid_weakonly_", "suatom_tid_", "suatom_tid_weakonly_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(hdrtblbase6),
              InstructionOperand.Immediate(tid8)
            ) =>
          Some(Instruction.SuatomRegAddressRegImmImmImm(dst, srcA, srcB, bank5, hdrtblbase6, tid8, modifiers, guard))
        case _ => None
      }
  }
  object SuatomRegAddressRegReg extends Definition(
        "SuatomRegAddressRegReg",
        "SUATOM",
        Vec(
          "SUATOM_D_CAS_REG",
          "SUATOM_D_REG",
          "SUATOM_P_CAS_REG",
          "SUATOM_P_REG",
          "suatom_cas_reg_",
          "suatom_cas_reg_weakonly_",
          "suatom_cas_urc_",
          "suatom_reg_",
          "suatom_reg_weakonly_",
          "suatom_urc_"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.SuatomRegAddressRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object SuatomRegAddressRegRegImm extends Definition(
        "SuatomRegAddressRegRegImm",
        "SUATOM",
        Vec("suatom_cas_urc_", "suatom_cas_urc_weakonly_", "suatom_urc_", "suatom_urc_weakonly_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.SuatomRegAddressRegRegImm(dst, srcA, srcB, srcC, tid8, modifiers, guard))
        case _ => None
      }
  }
  object SuatomRegAddressRegRegReg extends Definition(
        "SuatomRegAddressRegRegReg",
        "SUATOM",
        Vec("suatom_cas_reg_", "suatom_cas_urc_", "suatom_reg_", "suatom_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe)
            ) => Some(Instruction.SuatomRegAddressRegRegReg(dst, srcA, srcB, srcC, uRe, modifiers, guard))
        case _ => None
      }
  }
  object SuatomRegAddressRegRegRegImm
      extends Definition("SuatomRegAddressRegRegRegImm", "SUATOM", Vec("suatom_cas_urc_", "suatom_urc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.SuatomRegAddressRegRegRegImm(dst, srcA, srcB, srcC, uRe, tid8, modifiers, guard))
        case _ => None
      }
  }
  object SuatomRegAddressRegRegRegReg
      extends Definition("SuatomRegAddressRegRegRegReg", "SUATOM", Vec("suatom_cas_r_urc_", "suatom_r_urc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC1),
              InstructionOperand.Register(srcC2),
              InstructionOperand.Register(uRe)
            ) => Some(Instruction.SuatomRegAddressRegRegRegReg(dst, srcA, srcB, srcC1, srcC2, uRe, modifiers, guard))
        case _ => None
      }
  }
  object SubfmPredRegRegConstReg extends Definition("SubfmPredRegRegConstReg", "SUBFM", Vec("Const SUBFM")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pcl),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.SubfmPredRegRegConstReg(pcl, dst, srcA, srcConst, srcC, modifiers, guard))
        case _ => None
      }
  }
  object SubfmPredRegRegImmReg extends Definition("SubfmPredRegRegImmReg", "SUBFM", Vec("Imm SUBFM")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pcl),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.SubfmPredRegRegImmReg(pcl, dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object SubfmPredRegRegRegConst extends Definition("SubfmPredRegRegRegConst", "SUBFM", Vec("Const1 SUBFM")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pcl),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.SubfmPredRegRegRegConst(pcl, dst, srcA, srcB, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object SubfmPredRegRegRegReg extends Definition("SubfmPredRegRegRegReg", "SUBFM", Vec("SUBFM")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pcl),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.SubfmPredRegRegRegReg(pcl, dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object SuclampPredRegRegConstImm extends Definition("SuclampPredRegRegConstImm", "SUCLAMP", Vec("Const SUCLAMP")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pcl),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Immediate(sImm)
            ) => Some(Instruction.SuclampPredRegRegConstImm(pcl, dst, srcA, srcConst, sImm, modifiers, guard))
        case _ => None
      }
  }
  object SuclampPredRegRegImmImm extends Definition("SuclampPredRegRegImmImm", "SUCLAMP", Vec("Imm SUCLAMP")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pcl),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Immediate(sImm)
            ) => Some(Instruction.SuclampPredRegRegImmImm(pcl, dst, srcA, uImm, sImm, modifiers, guard))
        case _ => None
      }
  }
  object SuclampPredRegRegRegImm extends Definition("SuclampPredRegRegRegImm", "SUCLAMP", Vec("SUCLAMP")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pcl),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sImm)
            ) => Some(Instruction.SuclampPredRegRegRegImm(pcl, dst, srcA, srcB, sImm, modifiers, guard))
        case _ => None
      }
  }
  object SueauRegRegConstReg extends Definition("SueauRegRegConstReg", "SUEAU", Vec("Const SUEAU")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.SueauRegRegConstReg(dst, srcA, srcConst, srcC, modifiers, guard))
        case _ => None
      }
  }
  object SueauRegRegImmReg extends Definition("SueauRegRegImmReg", "SUEAU", Vec("Imm SUEAU")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.SueauRegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object SueauRegRegRegConst extends Definition("SueauRegRegRegConst", "SUEAU", Vec("Const1 SUEAU")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.SueauRegRegRegConst(dst, srcA, srcB, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object SueauRegRegRegReg extends Definition("SueauRegRegRegReg", "SUEAU", Vec("SUEAU")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.SueauRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object SuldPredRegAddressImm extends Definition("SuldPredRegAddressImm", "SULD", Vec("SULD_D_IMM", "SULD_P_IMM")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Immediate(suid)
            ) => Some(Instruction.SuldPredRegAddressImm(dstPredicate, dst, srcA, suid, modifiers, guard))
        case _ => None
      }
  }
  object SuldPredRegAddressImmImm extends Definition(
        "SuldPredRegAddressImmImm",
        "SULD",
        Vec("suld_d_imm_", "suld_d_imm_weakonly_", "suld_p_imm_", "suld_p_imm_weakonly_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(tsPtrIdx)
            ) => Some(Instruction.SuldPredRegAddressImmImm(pu, dst, srcA, bank5, tsPtrIdx, modifiers, guard))
        case _ => None
      }
  }
  object SuldPredRegAddressImmImmImm extends Definition(
        "SuldPredRegAddressImmImmImm",
        "SULD",
        Vec("suld_d_tid_", "suld_d_tid_weakonly_", "suld_p_tid_", "suld_p_tid_weakonly_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(hdrtblbase6),
              InstructionOperand.Immediate(tid8)
            ) =>
          Some(Instruction.SuldPredRegAddressImmImmImm(pu, dst, srcA, bank5, hdrtblbase6, tid8, modifiers, guard))
        case _ => None
      }
  }
  object SuldPredRegAddressReg extends Definition(
        "SuldPredRegAddressReg",
        "SULD",
        Vec(
          "SULD_D_REG",
          "SULD_P_REG",
          "suld_d_reg_",
          "suld_d_reg_weakonly_",
          "suld_d_urc_",
          "suld_p_reg_",
          "suld_p_reg_weakonly_",
          "suld_p_urc_"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.SuldPredRegAddressReg(dstPredicate, dst, srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object SuldPredRegAddressRegImm extends Definition(
        "SuldPredRegAddressRegImm",
        "SULD",
        Vec("suld_d_urc_", "suld_d_urc_weakonly_", "suld_p_urc_", "suld_p_urc_weakonly_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.SuldPredRegAddressRegImm(pu, dst, srcA, srcC, tid8, modifiers, guard))
        case _ => None
      }
  }
  object SuldPredRegAddressRegReg extends Definition(
        "SuldPredRegAddressRegReg",
        "SULD",
        Vec("suld_d_reg_", "suld_d_urc_", "suld_p_reg_", "suld_p_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe)
            ) => Some(Instruction.SuldPredRegAddressRegReg(pu, dst, srcA, srcC, uRe, modifiers, guard))
        case _ => None
      }
  }
  object SuldPredRegAddressRegRegImm
      extends Definition("SuldPredRegAddressRegRegImm", "SULD", Vec("suld_d_urc_", "suld_p_urc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.SuldPredRegAddressRegRegImm(pu, dst, srcA, srcC, uRe, tid8, modifiers, guard))
        case _ => None
      }
  }
  object SuldPredRegAddressRegRegReg
      extends Definition("SuldPredRegAddressRegRegReg", "SULD", Vec("suld_d_r_urc_", "suld_p_r_urc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC1),
              InstructionOperand.Register(srcC2),
              InstructionOperand.Register(uRe)
            ) => Some(Instruction.SuldPredRegAddressRegRegReg(pu, dst, srcA, srcC1, srcC2, uRe, modifiers, guard))
        case _ => None
      }
  }
  object SuldRegAddressImm extends Definition("SuldRegAddressImm", "SULD", Vec("SULD_D_IMM", "SULD_P_IMM")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Immediate(suid)
            ) => Some(Instruction.SuldRegAddressImm(dst, srcA, suid, modifiers, guard))
        case _ => None
      }
  }
  object SuldRegAddressImmImm extends Definition(
        "SuldRegAddressImmImm",
        "SULD",
        Vec("suld_d_imm_", "suld_d_imm_weakonly_", "suld_p_imm_", "suld_p_imm_weakonly_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(tsPtrIdx)
            ) => Some(Instruction.SuldRegAddressImmImm(dst, srcA, bank5, tsPtrIdx, modifiers, guard))
        case _ => None
      }
  }
  object SuldRegAddressImmImmImm extends Definition(
        "SuldRegAddressImmImmImm",
        "SULD",
        Vec("suld_d_tid_", "suld_d_tid_weakonly_", "suld_p_tid_", "suld_p_tid_weakonly_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(hdrtblbase6),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.SuldRegAddressImmImmImm(dst, srcA, bank5, hdrtblbase6, tid8, modifiers, guard))
        case _ => None
      }
  }
  object SuldRegAddressReg extends Definition(
        "SuldRegAddressReg",
        "SULD",
        Vec(
          "SULD_D_REG",
          "SULD_P_REG",
          "suld_d_reg_",
          "suld_d_reg_weakonly_",
          "suld_d_urc_",
          "suld_p_reg_",
          "suld_p_reg_weakonly_",
          "suld_p_urc_"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.SuldRegAddressReg(dst, srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object SuldRegAddressRegImm extends Definition(
        "SuldRegAddressRegImm",
        "SULD",
        Vec("suld_d_urc_", "suld_d_urc_weakonly_", "suld_p_urc_", "suld_p_urc_weakonly_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.SuldRegAddressRegImm(dst, srcA, srcC, tid8, modifiers, guard))
        case _ => None
      }
  }
  object SuldRegAddressRegReg extends Definition(
        "SuldRegAddressRegReg",
        "SULD",
        Vec("suld_d_reg_", "suld_d_urc_", "suld_p_reg_", "suld_p_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe)
            ) => Some(Instruction.SuldRegAddressRegReg(dst, srcA, srcC, uRe, modifiers, guard))
        case _ => None
      }
  }
  object SuldRegAddressRegRegImm
      extends Definition("SuldRegAddressRegRegImm", "SULD", Vec("suld_d_urc_", "suld_p_urc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.SuldRegAddressRegRegImm(dst, srcA, srcC, uRe, tid8, modifiers, guard))
        case _ => None
      }
  }
  object SuldRegAddressRegRegReg
      extends Definition("SuldRegAddressRegRegReg", "SULD", Vec("suld_d_r_urc_", "suld_p_r_urc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC1),
              InstructionOperand.Register(srcC2),
              InstructionOperand.Register(uRe)
            ) => Some(Instruction.SuldRegAddressRegRegReg(dst, srcA, srcC1, srcC2, uRe, modifiers, guard))
        case _ => None
      }
  }
  object SuldgaRegAddressConstPred extends Definition("SuldgaRegAddressConstPred", "SULDGA", Vec("SULDGA.B cimm")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Predicate(pcl)
            ) => Some(Instruction.SuldgaRegAddressConstPred(dst, srcA, srcConst, pcl, modifiers, guard))
        case _ => None
      }
  }
  object SuldgaRegAddressRegPred extends Definition("SuldgaRegAddressRegPred", "SULDGA", Vec("SULDGA.B")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pcl)
            ) => Some(Instruction.SuldgaRegAddressRegPred(dst, srcA, srcB, pcl, modifiers, guard))
        case _ => None
      }
  }
  object SuqueryPredRegAddressImmImm extends Definition("SuqueryPredRegAddressImmImm", "SUQUERY", Vec("suquery_imm_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(tsPtrIdx)
            ) => Some(Instruction.SuqueryPredRegAddressImmImm(pu, dst, srcA, bank5, tsPtrIdx, modifiers, guard))
        case _ => None
      }
  }
  object SuqueryPredRegAddressImmImmImm
      extends Definition("SuqueryPredRegAddressImmImmImm", "SUQUERY", Vec("suquery_tid_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(hdrtblbase6),
              InstructionOperand.Immediate(tid8)
            ) =>
          Some(Instruction.SuqueryPredRegAddressImmImmImm(pu, dst, srcA, bank5, hdrtblbase6, tid8, modifiers, guard))
        case _ => None
      }
  }
  object SuqueryPredRegAddressReg
      extends Definition("SuqueryPredRegAddressReg", "SUQUERY", Vec("suquery_reg_", "suquery_urc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.SuqueryPredRegAddressReg(pu, dst, srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object SuqueryPredRegAddressRegImm extends Definition("SuqueryPredRegAddressRegImm", "SUQUERY", Vec("suquery_urc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.SuqueryPredRegAddressRegImm(pu, dst, srcA, srcC, tid8, modifiers, guard))
        case _ => None
      }
  }
  object SuqueryPredRegAddressRegReg
      extends Definition("SuqueryPredRegAddressRegReg", "SUQUERY", Vec("suquery_reg_", "suquery_urc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe)
            ) => Some(Instruction.SuqueryPredRegAddressRegReg(pu, dst, srcA, srcC, uRe, modifiers, guard))
        case _ => None
      }
  }
  object SuqueryPredRegAddressRegRegImm
      extends Definition("SuqueryPredRegAddressRegRegImm", "SUQUERY", Vec("suquery_urc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.SuqueryPredRegAddressRegRegImm(pu, dst, srcA, srcC, uRe, tid8, modifiers, guard))
        case _ => None
      }
  }
  object SuqueryPredRegAddressRegRegReg
      extends Definition("SuqueryPredRegAddressRegRegReg", "SUQUERY", Vec("suquery_r_urc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC1),
              InstructionOperand.Register(srcC2),
              InstructionOperand.Register(uRe)
            ) => Some(Instruction.SuqueryPredRegAddressRegRegReg(pu, dst, srcA, srcC1, srcC2, uRe, modifiers, guard))
        case _ => None
      }
  }
  object SuqueryRegAddressImmImm extends Definition("SuqueryRegAddressImmImm", "SUQUERY", Vec("suquery_imm_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(tsPtrIdx)
            ) => Some(Instruction.SuqueryRegAddressImmImm(dst, srcA, bank5, tsPtrIdx, modifiers, guard))
        case _ => None
      }
  }
  object SuqueryRegAddressImmImmImm extends Definition("SuqueryRegAddressImmImmImm", "SUQUERY", Vec("suquery_tid_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(hdrtblbase6),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.SuqueryRegAddressImmImmImm(dst, srcA, bank5, hdrtblbase6, tid8, modifiers, guard))
        case _ => None
      }
  }
  object SuqueryRegAddressReg
      extends Definition("SuqueryRegAddressReg", "SUQUERY", Vec("suquery_reg_", "suquery_urc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.SuqueryRegAddressReg(dst, srcA, srcC, modifiers, guard))
        case _ => None
      }
  }
  object SuqueryRegAddressRegImm extends Definition("SuqueryRegAddressRegImm", "SUQUERY", Vec("suquery_urc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.SuqueryRegAddressRegImm(dst, srcA, srcC, tid8, modifiers, guard))
        case _ => None
      }
  }
  object SuqueryRegAddressRegReg
      extends Definition("SuqueryRegAddressRegReg", "SUQUERY", Vec("suquery_reg_", "suquery_urc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe)
            ) => Some(Instruction.SuqueryRegAddressRegReg(dst, srcA, srcC, uRe, modifiers, guard))
        case _ => None
      }
  }
  object SuqueryRegAddressRegRegImm extends Definition("SuqueryRegAddressRegRegImm", "SUQUERY", Vec("suquery_urc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.SuqueryRegAddressRegRegImm(dst, srcA, srcC, uRe, tid8, modifiers, guard))
        case _ => None
      }
  }
  object SuqueryRegAddressRegRegReg extends Definition("SuqueryRegAddressRegRegReg", "SUQUERY", Vec("suquery_r_urc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcC1),
              InstructionOperand.Register(srcC2),
              InstructionOperand.Register(uRe)
            ) => Some(Instruction.SuqueryRegAddressRegRegReg(dst, srcA, srcC1, srcC2, uRe, modifiers, guard))
        case _ => None
      }
  }
  object SuredAddressRegImm extends Definition("SuredAddressRegImm", "SURED", Vec("SURED_D_IMM", "SURED_P_IMM")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(suid)
            ) => Some(Instruction.SuredAddressRegImm(srcA, srcB, suid, modifiers, guard))
        case _ => None
      }
  }
  object SuredAddressRegImmImm
      extends Definition("SuredAddressRegImmImm", "SURED", Vec("sured_imm_", "sured_imm_weakonly_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(tsPtrIdx)
            ) => Some(Instruction.SuredAddressRegImmImm(srcA, srcB, bank5, tsPtrIdx, modifiers, guard))
        case _ => None
      }
  }
  object SuredAddressRegImmImmImm
      extends Definition("SuredAddressRegImmImmImm", "SURED", Vec("sured_tid_", "sured_tid_weakonly_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(hdrtblbase6),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.SuredAddressRegImmImmImm(srcA, srcB, bank5, hdrtblbase6, tid8, modifiers, guard))
        case _ => None
      }
  }
  object SuredAddressRegReg extends Definition(
        "SuredAddressRegReg",
        "SURED",
        Vec("SURED_D_REG", "SURED_P_REG", "sured_reg_", "sured_reg_weakonly_", "sured_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.SuredAddressRegReg(srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object SuredAddressRegRegImm
      extends Definition("SuredAddressRegRegImm", "SURED", Vec("sured_urc_", "sured_urc_weakonly_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.SuredAddressRegRegImm(srcA, srcB, srcC, tid8, modifiers, guard))
        case _ => None
      }
  }
  object SuredAddressRegRegReg extends Definition("SuredAddressRegRegReg", "SURED", Vec("sured_reg_", "sured_urc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe)
            ) => Some(Instruction.SuredAddressRegRegReg(srcA, srcB, srcC, uRe, modifiers, guard))
        case _ => None
      }
  }
  object SuredAddressRegRegRegImm extends Definition("SuredAddressRegRegRegImm", "SURED", Vec("sured_urc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.SuredAddressRegRegRegImm(srcA, srcB, srcC, uRe, tid8, modifiers, guard))
        case _ => None
      }
  }
  object SuredAddressRegRegRegReg extends Definition("SuredAddressRegRegRegReg", "SURED", Vec("sured_r_urc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC1),
              InstructionOperand.Register(srcC2),
              InstructionOperand.Register(uRe)
            ) => Some(Instruction.SuredAddressRegRegRegReg(srcA, srcB, srcC1, srcC2, uRe, modifiers, guard))
        case _ => None
      }
  }
  object SustAddressRegImm extends Definition("SustAddressRegImm", "SUST", Vec("SUST_D_IMM", "SUST_P_IMM")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(suid)
            ) => Some(Instruction.SustAddressRegImm(srcA, srcB, suid, modifiers, guard))
        case _ => None
      }
  }
  object SustAddressRegImmImm extends Definition(
        "SustAddressRegImmImm",
        "SUST",
        Vec("sust_d_imm_", "sust_d_imm_weakonly_", "sust_p_imm_", "sust_p_imm_weakonly_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(tsPtrIdx)
            ) => Some(Instruction.SustAddressRegImmImm(srcA, srcB, bank5, tsPtrIdx, modifiers, guard))
        case _ => None
      }
  }
  object SustAddressRegImmImmImm extends Definition(
        "SustAddressRegImmImmImm",
        "SUST",
        Vec("sust_d_tid_", "sust_d_tid_weakonly_", "sust_p_tid_", "sust_p_tid_weakonly_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(hdrtblbase6),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.SustAddressRegImmImmImm(srcA, srcB, bank5, hdrtblbase6, tid8, modifiers, guard))
        case _ => None
      }
  }
  object SustAddressRegReg extends Definition(
        "SustAddressRegReg",
        "SUST",
        Vec(
          "SUST_D_REG",
          "SUST_P_REG",
          "sust_d_reg_",
          "sust_d_reg_weakonly_",
          "sust_d_urc_",
          "sust_p_reg_",
          "sust_p_reg_weakonly_",
          "sust_p_urc_"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.SustAddressRegReg(srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object SustAddressRegRegImm extends Definition(
        "SustAddressRegRegImm",
        "SUST",
        Vec("sust_d_urc_", "sust_d_urc_weakonly_", "sust_p_urc_", "sust_p_urc_weakonly_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.SustAddressRegRegImm(srcA, srcB, srcC, tid8, modifiers, guard))
        case _ => None
      }
  }
  object SustAddressRegRegReg extends Definition(
        "SustAddressRegRegReg",
        "SUST",
        Vec("sust_d_reg_", "sust_d_urc_", "sust_p_reg_", "sust_p_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe)
            ) => Some(Instruction.SustAddressRegRegReg(srcA, srcB, srcC, uRe, modifiers, guard))
        case _ => None
      }
  }
  object SustAddressRegRegRegImm
      extends Definition("SustAddressRegRegRegImm", "SUST", Vec("sust_d_urc_", "sust_p_urc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.SustAddressRegRegRegImm(srcA, srcB, srcC, uRe, tid8, modifiers, guard))
        case _ => None
      }
  }
  object SustAddressRegRegRegReg
      extends Definition("SustAddressRegRegRegReg", "SUST", Vec("sust_d_r_urc_", "sust_p_r_urc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC1),
              InstructionOperand.Register(srcC2),
              InstructionOperand.Register(uRe)
            ) => Some(Instruction.SustAddressRegRegRegReg(srcA, srcB, srcC1, srcC2, uRe, modifiers, guard))
        case _ => None
      }
  }
  object SustgaAddressConstRegPred
      extends Definition("SustgaAddressConstRegPred", "SUSTGA", Vec("SUSTGA.B cimm", "SUSTGA.P cimm")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pcl)
            ) => Some(Instruction.SustgaAddressConstRegPred(srcA, srcConst, srcC, pcl, modifiers, guard))
        case _ => None
      }
  }
  object SustgaAddressRegRegPred extends Definition("SustgaAddressRegRegPred", "SUSTGA", Vec("SUSTGA.B", "SUSTGA.P")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Predicate(pcl)
            ) => Some(Instruction.SustgaAddressRegRegPred(srcA, srcB, srcC, pcl, modifiers, guard))
        case _ => None
      }
  }
  object SyncNoOperands extends Definition("SyncNoOperands", "SYNC", Vec("SYNC")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.SyncNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object SyncsNoOperands extends Definition("SyncsNoOperands", "SYNCS", Vec("syncs_cctl_all_", "syncs_flush_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.SyncsNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object SyncsAddress extends Definition("SyncsAddress", "SYNCS", Vec("syncs_cctl_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA)) => Some(Instruction.SyncsAddress(srcA, modifiers, guard))
        case _ => None
      }
  }
  object SyncsAddressReg extends Definition("SyncsAddressReg", "SYNCS", Vec("syncs_tcnt_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA), InstructionOperand.Register(srcB)) =>
          Some(Instruction.SyncsAddressReg(srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object SyncsPredAddressReg extends Definition("SyncsPredAddressReg", "SYNCS", Vec("syncs_phasechk_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.SyncsPredAddressReg(pu, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object SyncsRegAddress extends Definition("SyncsRegAddress", "SYNCS", Vec("syncs_ld_", "syncs_uniform_ld_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Address(srcA)) =>
          Some(Instruction.SyncsRegAddress(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object SyncsRegAddressReg
      extends Definition("SyncsRegAddressReg", "SYNCS", Vec("syncs_arrive_", "syncs_uniform_exch_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.SyncsRegAddressReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object SyncsRegAddressRegReg extends Definition("SyncsRegAddressRegReg", "SYNCS", Vec("syncs_uniform_cas_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.SyncsRegAddressRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object TexPredRegRegRegImm extends Definition("TexPredRegRegRegImm", "TEX", Vec("TEX", "TEX_B")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid)
            ) => Some(Instruction.TexPredRegRegRegImm(dstPredicate, dst, srcA, srcB, tid, modifiers, guard))
        case _ => None
      }
  }
  object TexPredRegRegRegImmImm extends Definition(
        "TexPredRegRegRegImmImm",
        "TEX",
        Vec("TEX", "TEX_B", "TEX_B_legacy_style", "TEX_legacy_style")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(smp)
            ) => Some(Instruction.TexPredRegRegRegImmImm(dstPredicate, dst, srcA, srcB, tid, smp, modifiers, guard))
        case _ => None
      }
  }
  object TexPredRegRegRegImmImmImm
      extends Definition("TexPredRegRegRegImmImmImm", "TEX", Vec("TEX_B_legacy_style", "TEX_legacy_style")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(smp),
              InstructionOperand.Immediate(wmsk)
            ) =>
          Some(Instruction.TexPredRegRegRegImmImmImm(dstPredicate, dst, srcA, srcB, tid, smp, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TexPredRegRegRegReg
      extends Definition("TexPredRegRegRegReg", "TEX", Vec("tex_b_noConst_", "tex_scr_b_noConst_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.TexPredRegRegRegReg(pu, rd2, dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object TexPredRegRegRegRegImm
      extends Definition("TexPredRegRegRegRegImm", "TEX", Vec("tex_b_noConst_", "tex_scr_b_noConst_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TexPredRegRegRegRegImm(pu, rd2, dst, srcA, srcB, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TexPredRegRegRegRegImmImm
      extends Definition("TexPredRegRegRegRegImmImm", "TEX", Vec("tex_", "tex_b_", "tex_scr_", "tex_scr_b_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(tsPtrIdx)
            ) =>
          Some(Instruction.TexPredRegRegRegRegImmImm(pu, rd2, dst, srcA, srcB, bank5, tsPtrIdx, modifiers, guard))
        case _ => None
      }
  }
  object TexPredRegRegRegRegImmImmImm extends Definition(
        "TexPredRegRegRegRegImmImmImm",
        "TEX",
        Vec("tex_", "tex_b_", "tex_b_tid_", "tex_scr_", "tex_scr_b_", "tex_scr_b_tid_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(hdrtblbase6),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.TexPredRegRegRegRegImmImmImm(
            pu,
            rd2,
            dst,
            srcA,
            srcB,
            bank5,
            hdrtblbase6,
            tid8,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object TexPredRegRegRegRegImmImmImmImm
      extends Definition("TexPredRegRegRegRegImmImmImmImm", "TEX", Vec("tex_b_tid_", "tex_scr_b_tid_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(hdrtblbase6),
              InstructionOperand.Immediate(tid8),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TexPredRegRegRegRegImmImmImmImm(
            pu,
            rd2,
            dst,
            srcA,
            srcB,
            bank5,
            hdrtblbase6,
            tid8,
            wmsk,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object TexPredRegRegRegRegReg extends Definition(
        "TexPredRegRegRegRegReg",
        "TEX",
        Vec("tex_b_noConst_", "tex_scr_b_noConst_", "tex_scr_urc_", "tex_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.TexPredRegRegRegRegReg(pu, rd2, dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object TexPredRegRegRegRegRegImm extends Definition(
        "TexPredRegRegRegRegRegImm",
        "TEX",
        Vec("tex_b_noConst_", "tex_scr_b_noConst_", "tex_scr_urc_", "tex_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.TexPredRegRegRegRegRegImm(pu, rd2, dst, srcA, srcB, srcC, tid8, modifiers, guard))
        case _ => None
      }
  }
  object TexPredRegRegRegRegRegImmImm
      extends Definition("TexPredRegRegRegRegRegImmImm", "TEX", Vec("tex_scr_urc_", "tex_urc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(tid8),
              InstructionOperand.Immediate(wmsk)
            ) =>
          Some(Instruction.TexPredRegRegRegRegRegImmImm(pu, rd2, dst, srcA, srcB, srcC, tid8, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TexPredRegRegRegRegRegReg extends Definition(
        "TexPredRegRegRegRegRegReg",
        "TEX",
        Vec("tex_b_urc_", "tex_scr_b_urc_", "tex_scr_urc_", "tex_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe)
            ) => Some(Instruction.TexPredRegRegRegRegRegReg(pu, rd2, dst, srcA, srcB, srcC, uRe, modifiers, guard))
        case _ => None
      }
  }
  object TexPredRegRegRegRegRegRegImm extends Definition(
        "TexPredRegRegRegRegRegRegImm",
        "TEX",
        Vec("tex_b_urc_", "tex_scr_b_urc_", "tex_scr_urc_", "tex_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe),
              InstructionOperand.Immediate(tid8)
            ) =>
          Some(Instruction.TexPredRegRegRegRegRegRegImm(pu, rd2, dst, srcA, srcB, srcC, uRe, tid8, modifiers, guard))
        case _ => None
      }
  }
  object TexPredRegRegRegRegRegRegImmImm
      extends Definition("TexPredRegRegRegRegRegRegImmImm", "TEX", Vec("tex_scr_urc_", "tex_urc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe),
              InstructionOperand.Immediate(tid8),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TexPredRegRegRegRegRegRegImmImm(
            pu,
            rd2,
            dst,
            srcA,
            srcB,
            srcC,
            uRe,
            tid8,
            wmsk,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object TexRegRegRegImm extends Definition("TexRegRegRegImm", "TEX", Vec("TEX", "TEX_B")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid)
            ) => Some(Instruction.TexRegRegRegImm(dst, srcA, srcB, tid, modifiers, guard))
        case _ => None
      }
  }
  object TexRegRegRegImmImm extends Definition(
        "TexRegRegRegImmImm",
        "TEX",
        Vec("TEX", "TEX B", "TEX_B", "TEX_B_legacy_style", "TEX_legacy_style")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(smp)
            ) => Some(Instruction.TexRegRegRegImmImm(dst, srcA, srcB, tid, smp, modifiers, guard))
        case _ => None
      }
  }
  object TexRegRegRegImmImmImm extends Definition(
        "TexRegRegRegImmImmImm",
        "TEX",
        Vec("TEX B legacy style", "TEX legacy style", "TEX_B_legacy_style", "TEX_legacy_style")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(smp),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TexRegRegRegImmImmImm(dst, srcA, srcB, tid, smp, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TexRegRegRegReg extends Definition("TexRegRegRegReg", "TEX", Vec("tex_b_noConst_", "tex_scr_b_noConst_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.TexRegRegRegReg(rd2, dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object TexRegRegRegRegImm
      extends Definition("TexRegRegRegRegImm", "TEX", Vec("tex_b_noConst_", "tex_scr_b_noConst_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TexRegRegRegRegImm(rd2, dst, srcA, srcB, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TexRegRegRegRegImmImm
      extends Definition("TexRegRegRegRegImmImm", "TEX", Vec("tex_", "tex_b_", "tex_scr_", "tex_scr_b_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(tsPtrIdx)
            ) => Some(Instruction.TexRegRegRegRegImmImm(rd2, dst, srcA, srcB, bank5, tsPtrIdx, modifiers, guard))
        case _ => None
      }
  }
  object TexRegRegRegRegImmImmImm extends Definition(
        "TexRegRegRegRegImmImmImm",
        "TEX",
        Vec("tex_", "tex_b_", "tex_b_tid_", "tex_scr_", "tex_scr_b_", "tex_scr_b_tid_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(hdrtblbase6),
              InstructionOperand.Immediate(tid8)
            ) =>
          Some(Instruction.TexRegRegRegRegImmImmImm(rd2, dst, srcA, srcB, bank5, hdrtblbase6, tid8, modifiers, guard))
        case _ => None
      }
  }
  object TexRegRegRegRegImmImmImmImm
      extends Definition("TexRegRegRegRegImmImmImmImm", "TEX", Vec("tex_b_tid_", "tex_scr_b_tid_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(hdrtblbase6),
              InstructionOperand.Immediate(tid8),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TexRegRegRegRegImmImmImmImm(
            rd2,
            dst,
            srcA,
            srcB,
            bank5,
            hdrtblbase6,
            tid8,
            wmsk,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object TexRegRegRegRegReg extends Definition(
        "TexRegRegRegRegReg",
        "TEX",
        Vec("tex_b_noConst_", "tex_scr_b_noConst_", "tex_scr_urc_", "tex_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.TexRegRegRegRegReg(rd2, dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object TexRegRegRegRegRegImm extends Definition(
        "TexRegRegRegRegRegImm",
        "TEX",
        Vec("tex_b_noConst_", "tex_scr_b_noConst_", "tex_scr_urc_", "tex_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.TexRegRegRegRegRegImm(rd2, dst, srcA, srcB, srcC, tid8, modifiers, guard))
        case _ => None
      }
  }
  object TexRegRegRegRegRegImmImm
      extends Definition("TexRegRegRegRegRegImmImm", "TEX", Vec("tex_scr_urc_", "tex_urc_")) {
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
            ) => Some(Instruction.TexRegRegRegRegRegImmImm(rd2, dst, srcA, srcB, srcC, tid8, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TexRegRegRegRegRegReg extends Definition(
        "TexRegRegRegRegRegReg",
        "TEX",
        Vec("tex_b_urc_", "tex_scr_b_urc_", "tex_scr_urc_", "tex_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe)
            ) => Some(Instruction.TexRegRegRegRegRegReg(rd2, dst, srcA, srcB, srcC, uRe, modifiers, guard))
        case _ => None
      }
  }
  object TexRegRegRegRegRegRegImm extends Definition(
        "TexRegRegRegRegRegRegImm",
        "TEX",
        Vec("tex_b_urc_", "tex_scr_b_urc_", "tex_scr_urc_", "tex_urc_")
      ) {
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
            ) => Some(Instruction.TexRegRegRegRegRegRegImm(rd2, dst, srcA, srcB, srcC, uRe, tid8, modifiers, guard))
        case _ => None
      }
  }
  object TexRegRegRegRegRegRegImmImm
      extends Definition("TexRegRegRegRegRegRegImmImm", "TEX", Vec("tex_scr_urc_", "tex_urc_")) {
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
          Some(Instruction.TexRegRegRegRegRegRegImmImm(rd2, dst, srcA, srcB, srcC, uRe, tid8, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TexdepbarImm extends Definition("TexdepbarImm", "TEXDEPBAR", Vec("TEXDEPBAR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(uImm)) => Some(Instruction.TexdepbarImm(uImm, modifiers, guard))
        case _ => None
      }
  }
  object TexsRegRegRegRegImm
      extends Definition("TexsRegRegRegRegImm", "TEXS", Vec("TEXS", "TEXS_F16", "TEXS_F16_RZ", "TEXS_RZ")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid)
            ) => Some(Instruction.TexsRegRegRegRegImm(rd2, dst, srcA, srcB, tid, modifiers, guard))
        case _ => None
      }
  }
  object TldPredRegRegRegImm extends Definition("TldPredRegRegRegImm", "TLD", Vec("TLD", "TLD_B")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid)
            ) => Some(Instruction.TldPredRegRegRegImm(dstPredicate, dst, srcA, srcB, tid, modifiers, guard))
        case _ => None
      }
  }
  object TldPredRegRegRegImmImm extends Definition("TldPredRegRegRegImmImm", "TLD", Vec("TLD", "TLD_B")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TldPredRegRegRegImmImm(dstPredicate, dst, srcA, srcB, tid, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TldPredRegRegRegReg
      extends Definition("TldPredRegRegRegReg", "TLD", Vec("tld_b_noConst_", "tld_scr_b_noConst_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.TldPredRegRegRegReg(pu, rd2, dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object TldPredRegRegRegRegImm
      extends Definition("TldPredRegRegRegRegImm", "TLD", Vec("tld_b_noConst_", "tld_scr_b_noConst_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TldPredRegRegRegRegImm(pu, rd2, dst, srcA, srcB, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TldPredRegRegRegRegImmImm
      extends Definition("TldPredRegRegRegRegImmImm", "TLD", Vec("tld_", "tld_b_", "tld_scr_", "tld_scr_b_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(tsPtrIdx)
            ) =>
          Some(Instruction.TldPredRegRegRegRegImmImm(pu, rd2, dst, srcA, srcB, bank5, tsPtrIdx, modifiers, guard))
        case _ => None
      }
  }
  object TldPredRegRegRegRegImmImmImm extends Definition(
        "TldPredRegRegRegRegImmImmImm",
        "TLD",
        Vec("tld_", "tld_b_", "tld_b_tid_", "tld_scr_", "tld_scr_b_", "tld_scr_b_tid_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(hdrtblbase6),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.TldPredRegRegRegRegImmImmImm(
            pu,
            rd2,
            dst,
            srcA,
            srcB,
            bank5,
            hdrtblbase6,
            tid8,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object TldPredRegRegRegRegImmImmImmImm
      extends Definition("TldPredRegRegRegRegImmImmImmImm", "TLD", Vec("tld_b_tid_", "tld_scr_b_tid_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(hdrtblbase6),
              InstructionOperand.Immediate(tid8),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TldPredRegRegRegRegImmImmImmImm(
            pu,
            rd2,
            dst,
            srcA,
            srcB,
            bank5,
            hdrtblbase6,
            tid8,
            wmsk,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object TldPredRegRegRegRegReg extends Definition(
        "TldPredRegRegRegRegReg",
        "TLD",
        Vec("tld_b_noConst_", "tld_scr_b_noConst_", "tld_scr_urc_", "tld_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.TldPredRegRegRegRegReg(pu, rd2, dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object TldPredRegRegRegRegRegImm extends Definition(
        "TldPredRegRegRegRegRegImm",
        "TLD",
        Vec("tld_b_noConst_", "tld_scr_b_noConst_", "tld_scr_urc_", "tld_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.TldPredRegRegRegRegRegImm(pu, rd2, dst, srcA, srcB, srcC, tid8, modifiers, guard))
        case _ => None
      }
  }
  object TldPredRegRegRegRegRegImmImm
      extends Definition("TldPredRegRegRegRegRegImmImm", "TLD", Vec("tld_scr_urc_", "tld_urc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(tid8),
              InstructionOperand.Immediate(wmsk)
            ) =>
          Some(Instruction.TldPredRegRegRegRegRegImmImm(pu, rd2, dst, srcA, srcB, srcC, tid8, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TldPredRegRegRegRegRegReg extends Definition(
        "TldPredRegRegRegRegRegReg",
        "TLD",
        Vec("tld_b_urc_", "tld_scr_b_urc_", "tld_scr_urc_", "tld_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe)
            ) => Some(Instruction.TldPredRegRegRegRegRegReg(pu, rd2, dst, srcA, srcB, srcC, uRe, modifiers, guard))
        case _ => None
      }
  }
  object TldPredRegRegRegRegRegRegImm extends Definition(
        "TldPredRegRegRegRegRegRegImm",
        "TLD",
        Vec("tld_b_urc_", "tld_scr_b_urc_", "tld_scr_urc_", "tld_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe),
              InstructionOperand.Immediate(tid8)
            ) =>
          Some(Instruction.TldPredRegRegRegRegRegRegImm(pu, rd2, dst, srcA, srcB, srcC, uRe, tid8, modifiers, guard))
        case _ => None
      }
  }
  object TldPredRegRegRegRegRegRegImmImm
      extends Definition("TldPredRegRegRegRegRegRegImmImm", "TLD", Vec("tld_scr_urc_", "tld_urc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe),
              InstructionOperand.Immediate(tid8),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TldPredRegRegRegRegRegRegImmImm(
            pu,
            rd2,
            dst,
            srcA,
            srcB,
            srcC,
            uRe,
            tid8,
            wmsk,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object TldRegRegRegImm extends Definition("TldRegRegRegImm", "TLD", Vec("TLD", "TLD_B")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid)
            ) => Some(Instruction.TldRegRegRegImm(dst, srcA, srcB, tid, modifiers, guard))
        case _ => None
      }
  }
  object TldRegRegRegImmImm extends Definition("TldRegRegRegImmImm", "TLD", Vec("TLD", "TLD B", "TLD_B")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TldRegRegRegImmImm(dst, srcA, srcB, tid, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TldRegRegRegReg extends Definition("TldRegRegRegReg", "TLD", Vec("tld_b_noConst_", "tld_scr_b_noConst_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.TldRegRegRegReg(rd2, dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object TldRegRegRegRegImm
      extends Definition("TldRegRegRegRegImm", "TLD", Vec("tld_b_noConst_", "tld_scr_b_noConst_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TldRegRegRegRegImm(rd2, dst, srcA, srcB, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TldRegRegRegRegImmImm
      extends Definition("TldRegRegRegRegImmImm", "TLD", Vec("tld_", "tld_b_", "tld_scr_", "tld_scr_b_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(tsPtrIdx)
            ) => Some(Instruction.TldRegRegRegRegImmImm(rd2, dst, srcA, srcB, bank5, tsPtrIdx, modifiers, guard))
        case _ => None
      }
  }
  object TldRegRegRegRegImmImmImm extends Definition(
        "TldRegRegRegRegImmImmImm",
        "TLD",
        Vec("tld_", "tld_b_", "tld_b_tid_", "tld_scr_", "tld_scr_b_", "tld_scr_b_tid_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(hdrtblbase6),
              InstructionOperand.Immediate(tid8)
            ) =>
          Some(Instruction.TldRegRegRegRegImmImmImm(rd2, dst, srcA, srcB, bank5, hdrtblbase6, tid8, modifiers, guard))
        case _ => None
      }
  }
  object TldRegRegRegRegImmImmImmImm
      extends Definition("TldRegRegRegRegImmImmImmImm", "TLD", Vec("tld_b_tid_", "tld_scr_b_tid_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(hdrtblbase6),
              InstructionOperand.Immediate(tid8),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TldRegRegRegRegImmImmImmImm(
            rd2,
            dst,
            srcA,
            srcB,
            bank5,
            hdrtblbase6,
            tid8,
            wmsk,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object TldRegRegRegRegReg extends Definition(
        "TldRegRegRegRegReg",
        "TLD",
        Vec("tld_b_noConst_", "tld_scr_b_noConst_", "tld_scr_urc_", "tld_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.TldRegRegRegRegReg(rd2, dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object TldRegRegRegRegRegImm extends Definition(
        "TldRegRegRegRegRegImm",
        "TLD",
        Vec("tld_b_noConst_", "tld_scr_b_noConst_", "tld_scr_urc_", "tld_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.TldRegRegRegRegRegImm(rd2, dst, srcA, srcB, srcC, tid8, modifiers, guard))
        case _ => None
      }
  }
  object TldRegRegRegRegRegImmImm
      extends Definition("TldRegRegRegRegRegImmImm", "TLD", Vec("tld_scr_urc_", "tld_urc_")) {
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
            ) => Some(Instruction.TldRegRegRegRegRegImmImm(rd2, dst, srcA, srcB, srcC, tid8, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TldRegRegRegRegRegReg extends Definition(
        "TldRegRegRegRegRegReg",
        "TLD",
        Vec("tld_b_urc_", "tld_scr_b_urc_", "tld_scr_urc_", "tld_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe)
            ) => Some(Instruction.TldRegRegRegRegRegReg(rd2, dst, srcA, srcB, srcC, uRe, modifiers, guard))
        case _ => None
      }
  }
  object TldRegRegRegRegRegRegImm extends Definition(
        "TldRegRegRegRegRegRegImm",
        "TLD",
        Vec("tld_b_urc_", "tld_scr_b_urc_", "tld_scr_urc_", "tld_urc_")
      ) {
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
            ) => Some(Instruction.TldRegRegRegRegRegRegImm(rd2, dst, srcA, srcB, srcC, uRe, tid8, modifiers, guard))
        case _ => None
      }
  }
  object TldRegRegRegRegRegRegImmImm
      extends Definition("TldRegRegRegRegRegRegImmImm", "TLD", Vec("tld_scr_urc_", "tld_urc_")) {
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
          Some(Instruction.TldRegRegRegRegRegRegImmImm(rd2, dst, srcA, srcB, srcC, uRe, tid8, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object Tld4PredRegRegRegImm extends Definition("Tld4PredRegRegRegImm", "TLD4", Vec("TLD4", "TLD4_B")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid)
            ) => Some(Instruction.Tld4PredRegRegRegImm(dstPredicate, dst, srcA, srcB, tid, modifiers, guard))
        case _ => None
      }
  }
  object Tld4PredRegRegRegImmImm extends Definition(
        "Tld4PredRegRegRegImmImm",
        "TLD4",
        Vec("TLD4", "TLD4_B", "TLD4_B_Legacy_Style", "TLD4_Legacy_Style")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(smp)
            ) => Some(Instruction.Tld4PredRegRegRegImmImm(dstPredicate, dst, srcA, srcB, tid, smp, modifiers, guard))
        case _ => None
      }
  }
  object Tld4PredRegRegRegImmImmImm
      extends Definition("Tld4PredRegRegRegImmImmImm", "TLD4", Vec("TLD4_B_Legacy_Style", "TLD4_Legacy_Style")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(smp),
              InstructionOperand.Immediate(wmsk)
            ) =>
          Some(Instruction.Tld4PredRegRegRegImmImmImm(dstPredicate, dst, srcA, srcB, tid, smp, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object Tld4PredRegRegRegReg
      extends Definition("Tld4PredRegRegRegReg", "TLD4", Vec("tld4_b_noConst_", "tld4_scr_b_noConst_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.Tld4PredRegRegRegReg(pu, rd2, dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object Tld4PredRegRegRegRegImm
      extends Definition("Tld4PredRegRegRegRegImm", "TLD4", Vec("tld4_b_noConst_", "tld4_scr_b_noConst_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.Tld4PredRegRegRegRegImm(pu, rd2, dst, srcA, srcB, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object Tld4PredRegRegRegRegImmImm
      extends Definition("Tld4PredRegRegRegRegImmImm", "TLD4", Vec("tld4_", "tld4_b_", "tld4_scr_", "tld4_scr_b_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(tsPtrIdx)
            ) =>
          Some(Instruction.Tld4PredRegRegRegRegImmImm(pu, rd2, dst, srcA, srcB, bank5, tsPtrIdx, modifiers, guard))
        case _ => None
      }
  }
  object Tld4PredRegRegRegRegImmImmImm extends Definition(
        "Tld4PredRegRegRegRegImmImmImm",
        "TLD4",
        Vec("tld4_", "tld4_b_", "tld4_b_tid_", "tld4_scr_", "tld4_scr_b_", "tld4_scr_b_tid_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(hdrtblbase6),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.Tld4PredRegRegRegRegImmImmImm(
            pu,
            rd2,
            dst,
            srcA,
            srcB,
            bank5,
            hdrtblbase6,
            tid8,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object Tld4PredRegRegRegRegImmImmImmImm
      extends Definition("Tld4PredRegRegRegRegImmImmImmImm", "TLD4", Vec("tld4_b_tid_", "tld4_scr_b_tid_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(hdrtblbase6),
              InstructionOperand.Immediate(tid8),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.Tld4PredRegRegRegRegImmImmImmImm(
            pu,
            rd2,
            dst,
            srcA,
            srcB,
            bank5,
            hdrtblbase6,
            tid8,
            wmsk,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object Tld4PredRegRegRegRegReg extends Definition(
        "Tld4PredRegRegRegRegReg",
        "TLD4",
        Vec("tld4_b_noConst_", "tld4_scr_b_noConst_", "tld4_scr_urc_", "tld4_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Tld4PredRegRegRegRegReg(pu, rd2, dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Tld4PredRegRegRegRegRegImm extends Definition(
        "Tld4PredRegRegRegRegRegImm",
        "TLD4",
        Vec("tld4_b_noConst_", "tld4_scr_b_noConst_", "tld4_scr_urc_", "tld4_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.Tld4PredRegRegRegRegRegImm(pu, rd2, dst, srcA, srcB, srcC, tid8, modifiers, guard))
        case _ => None
      }
  }
  object Tld4PredRegRegRegRegRegImmImm
      extends Definition("Tld4PredRegRegRegRegRegImmImm", "TLD4", Vec("tld4_scr_urc_", "tld4_urc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(tid8),
              InstructionOperand.Immediate(wmsk)
            ) =>
          Some(Instruction.Tld4PredRegRegRegRegRegImmImm(pu, rd2, dst, srcA, srcB, srcC, tid8, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object Tld4PredRegRegRegRegRegReg extends Definition(
        "Tld4PredRegRegRegRegRegReg",
        "TLD4",
        Vec("tld4_b_urc_", "tld4_scr_b_urc_", "tld4_scr_urc_", "tld4_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe)
            ) => Some(Instruction.Tld4PredRegRegRegRegRegReg(pu, rd2, dst, srcA, srcB, srcC, uRe, modifiers, guard))
        case _ => None
      }
  }
  object Tld4PredRegRegRegRegRegRegImm extends Definition(
        "Tld4PredRegRegRegRegRegRegImm",
        "TLD4",
        Vec("tld4_b_urc_", "tld4_scr_b_urc_", "tld4_scr_urc_", "tld4_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe),
              InstructionOperand.Immediate(tid8)
            ) =>
          Some(Instruction.Tld4PredRegRegRegRegRegRegImm(pu, rd2, dst, srcA, srcB, srcC, uRe, tid8, modifiers, guard))
        case _ => None
      }
  }
  object Tld4PredRegRegRegRegRegRegImmImm
      extends Definition("Tld4PredRegRegRegRegRegRegImmImm", "TLD4", Vec("tld4_scr_urc_", "tld4_urc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe),
              InstructionOperand.Immediate(tid8),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.Tld4PredRegRegRegRegRegRegImmImm(
            pu,
            rd2,
            dst,
            srcA,
            srcB,
            srcC,
            uRe,
            tid8,
            wmsk,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object Tld4RegRegRegImm extends Definition("Tld4RegRegRegImm", "TLD4", Vec("TLD4", "TLD4_B")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid)
            ) => Some(Instruction.Tld4RegRegRegImm(dst, srcA, srcB, tid, modifiers, guard))
        case _ => None
      }
  }
  object Tld4RegRegRegImmImm extends Definition(
        "Tld4RegRegRegImmImm",
        "TLD4",
        Vec("TLD4", "TLD4 B", "TLD4_B", "TLD4_B_Legacy_Style", "TLD4_Legacy_Style")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(smp)
            ) => Some(Instruction.Tld4RegRegRegImmImm(dst, srcA, srcB, tid, smp, modifiers, guard))
        case _ => None
      }
  }
  object Tld4RegRegRegImmImmImm extends Definition(
        "Tld4RegRegRegImmImmImm",
        "TLD4",
        Vec("TLD4 B Legacy Style", "TLD4 Legacy Style", "TLD4_B_Legacy_Style", "TLD4_Legacy_Style")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(smp),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.Tld4RegRegRegImmImmImm(dst, srcA, srcB, tid, smp, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object Tld4RegRegRegReg
      extends Definition("Tld4RegRegRegReg", "TLD4", Vec("tld4_b_noConst_", "tld4_scr_b_noConst_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.Tld4RegRegRegReg(rd2, dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object Tld4RegRegRegRegImm
      extends Definition("Tld4RegRegRegRegImm", "TLD4", Vec("tld4_b_noConst_", "tld4_scr_b_noConst_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.Tld4RegRegRegRegImm(rd2, dst, srcA, srcB, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object Tld4RegRegRegRegImmImm
      extends Definition("Tld4RegRegRegRegImmImm", "TLD4", Vec("tld4_", "tld4_b_", "tld4_scr_", "tld4_scr_b_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(tsPtrIdx)
            ) => Some(Instruction.Tld4RegRegRegRegImmImm(rd2, dst, srcA, srcB, bank5, tsPtrIdx, modifiers, guard))
        case _ => None
      }
  }
  object Tld4RegRegRegRegImmImmImm extends Definition(
        "Tld4RegRegRegRegImmImmImm",
        "TLD4",
        Vec("tld4_", "tld4_b_", "tld4_b_tid_", "tld4_scr_", "tld4_scr_b_", "tld4_scr_b_tid_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(hdrtblbase6),
              InstructionOperand.Immediate(tid8)
            ) =>
          Some(Instruction.Tld4RegRegRegRegImmImmImm(rd2, dst, srcA, srcB, bank5, hdrtblbase6, tid8, modifiers, guard))
        case _ => None
      }
  }
  object Tld4RegRegRegRegImmImmImmImm
      extends Definition("Tld4RegRegRegRegImmImmImmImm", "TLD4", Vec("tld4_b_tid_", "tld4_scr_b_tid_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(hdrtblbase6),
              InstructionOperand.Immediate(tid8),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.Tld4RegRegRegRegImmImmImmImm(
            rd2,
            dst,
            srcA,
            srcB,
            bank5,
            hdrtblbase6,
            tid8,
            wmsk,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object Tld4RegRegRegRegReg extends Definition(
        "Tld4RegRegRegRegReg",
        "TLD4",
        Vec("tld4_b_noConst_", "tld4_scr_b_noConst_", "tld4_scr_urc_", "tld4_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.Tld4RegRegRegRegReg(rd2, dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object Tld4RegRegRegRegRegImm extends Definition(
        "Tld4RegRegRegRegRegImm",
        "TLD4",
        Vec("tld4_b_noConst_", "tld4_scr_b_noConst_", "tld4_scr_urc_", "tld4_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.Tld4RegRegRegRegRegImm(rd2, dst, srcA, srcB, srcC, tid8, modifiers, guard))
        case _ => None
      }
  }
  object Tld4RegRegRegRegRegImmImm
      extends Definition("Tld4RegRegRegRegRegImmImm", "TLD4", Vec("tld4_scr_urc_", "tld4_urc_")) {
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
            ) => Some(Instruction.Tld4RegRegRegRegRegImmImm(rd2, dst, srcA, srcB, srcC, tid8, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object Tld4RegRegRegRegRegReg extends Definition(
        "Tld4RegRegRegRegRegReg",
        "TLD4",
        Vec("tld4_b_urc_", "tld4_scr_b_urc_", "tld4_scr_urc_", "tld4_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe)
            ) => Some(Instruction.Tld4RegRegRegRegRegReg(rd2, dst, srcA, srcB, srcC, uRe, modifiers, guard))
        case _ => None
      }
  }
  object Tld4RegRegRegRegRegRegImm extends Definition(
        "Tld4RegRegRegRegRegRegImm",
        "TLD4",
        Vec("tld4_b_urc_", "tld4_scr_b_urc_", "tld4_scr_urc_", "tld4_urc_")
      ) {
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
            ) => Some(Instruction.Tld4RegRegRegRegRegRegImm(rd2, dst, srcA, srcB, srcC, uRe, tid8, modifiers, guard))
        case _ => None
      }
  }
  object Tld4RegRegRegRegRegRegImmImm
      extends Definition("Tld4RegRegRegRegRegRegImmImm", "TLD4", Vec("tld4_scr_urc_", "tld4_urc_")) {
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
          Some(Instruction.Tld4RegRegRegRegRegRegImmImm(rd2, dst, srcA, srcB, srcC, uRe, tid8, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object Tld4sRegRegRegRegImm extends Definition("Tld4sRegRegRegRegImm", "TLD4S", Vec("TLD4S", "TLD4S_F16")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid)
            ) => Some(Instruction.Tld4sRegRegRegRegImm(rd2, dst, srcA, srcB, tid, modifiers, guard))
        case _ => None
      }
  }
  object TldsRegRegRegRegImm
      extends Definition("TldsRegRegRegRegImm", "TLDS", Vec("TLDS", "TLDS_F16", "TLDS_F16_RZ", "TLDS_RZ")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid)
            ) => Some(Instruction.TldsRegRegRegRegImm(rd2, dst, srcA, srcB, tid, modifiers, guard))
        case _ => None
      }
  }
  object TmmlRegRegImm extends Definition("TmmlRegRegImm", "TMML", Vec("TMML")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(tid)
            ) => Some(Instruction.TmmlRegRegImm(dst, srcA, tid, modifiers, guard))
        case _ => None
      }
  }
  object TmmlRegRegImmImm extends Definition("TmmlRegRegImmImm", "TMML", Vec("TMML", "TMML_Legacy_Style")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(smp)
            ) => Some(Instruction.TmmlRegRegImmImm(dst, srcA, tid, smp, modifiers, guard))
        case _ => None
      }
  }
  object TmmlRegRegImmImmImm
      extends Definition("TmmlRegRegImmImmImm", "TMML", Vec("TMML Legacy Style", "TMML_Legacy_Style")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(smp),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TmmlRegRegImmImmImm(dst, srcA, tid, smp, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TmmlRegRegRegImm extends Definition("TmmlRegRegRegImm", "TMML", Vec("TMML", "TMML_B")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid)
            ) => Some(Instruction.TmmlRegRegRegImm(dst, srcA, srcB, tid, modifiers, guard))
        case _ => None
      }
  }
  object TmmlRegRegRegImmImm extends Definition(
        "TmmlRegRegRegImmImm",
        "TMML",
        Vec("TMML", "TMML.B", "TMML_B", "TMML_B_Legacy_Style", "TMML_Legacy_Style")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(smp)
            ) => Some(Instruction.TmmlRegRegRegImmImm(dst, srcA, srcB, tid, smp, modifiers, guard))
        case _ => None
      }
  }
  object TmmlRegRegRegImmImmImm extends Definition(
        "TmmlRegRegRegImmImmImm",
        "TMML",
        Vec("TMML Legacy Style", "TMML.B Legacy Style", "TMML_B_Legacy_Style", "TMML_Legacy_Style")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(smp),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TmmlRegRegRegImmImmImm(dst, srcA, srcB, tid, smp, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TmmlRegRegRegReg extends Definition("TmmlRegRegRegReg", "TMML", Vec("tmml_b_noConst_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.TmmlRegRegRegReg(rd2, dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object TmmlRegRegRegRegImm extends Definition("TmmlRegRegRegRegImm", "TMML", Vec("tmml_b_noConst_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TmmlRegRegRegRegImm(rd2, dst, srcA, srcB, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TmmlRegRegRegRegImmImm extends Definition("TmmlRegRegRegRegImmImm", "TMML", Vec("tmml_", "tmml_b_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(tsPtrIdx)
            ) => Some(Instruction.TmmlRegRegRegRegImmImm(rd2, dst, srcA, srcB, bank5, tsPtrIdx, modifiers, guard))
        case _ => None
      }
  }
  object TmmlRegRegRegRegImmImmImm
      extends Definition("TmmlRegRegRegRegImmImmImm", "TMML", Vec("tmml_", "tmml_b_", "tmml_b_tid_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(hdrtblbase6),
              InstructionOperand.Immediate(tid8)
            ) =>
          Some(Instruction.TmmlRegRegRegRegImmImmImm(rd2, dst, srcA, srcB, bank5, hdrtblbase6, tid8, modifiers, guard))
        case _ => None
      }
  }
  object TmmlRegRegRegRegImmImmImmImm extends Definition("TmmlRegRegRegRegImmImmImmImm", "TMML", Vec("tmml_b_tid_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(hdrtblbase6),
              InstructionOperand.Immediate(tid8),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TmmlRegRegRegRegImmImmImmImm(
            rd2,
            dst,
            srcA,
            srcB,
            bank5,
            hdrtblbase6,
            tid8,
            wmsk,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object TmmlRegRegRegRegReg extends Definition("TmmlRegRegRegRegReg", "TMML", Vec("tmml_b_urc_", "tmml_urc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.TmmlRegRegRegRegReg(rd2, dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object TmmlRegRegRegRegRegImm extends Definition("TmmlRegRegRegRegRegImm", "TMML", Vec("tmml_b_urc_", "tmml_urc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.TmmlRegRegRegRegRegImm(rd2, dst, srcA, srcB, srcC, tid8, modifiers, guard))
        case _ => None
      }
  }
  object TmmlRegRegRegRegRegImmImm extends Definition("TmmlRegRegRegRegRegImmImm", "TMML", Vec("tmml_urc_")) {
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
            ) => Some(Instruction.TmmlRegRegRegRegRegImmImm(rd2, dst, srcA, srcB, srcC, tid8, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TxaRegRegImm extends Definition("TxaRegRegImm", "TXA", Vec("TXA")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(tid)
            ) => Some(Instruction.TxaRegRegImm(dst, srcA, tid, modifiers, guard))
        case _ => None
      }
  }
  object TxaRegRegImmImm extends Definition("TxaRegRegImmImm", "TXA", Vec("TXA")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TxaRegRegImmImm(dst, srcA, tid, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TxaRegRegImmImmImm
      extends Definition("TxaRegRegImmImmImm", "TXA", Vec("TXA Legacy Style", "TXA_Legacy_Style")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(smp),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TxaRegRegImmImmImm(dst, srcA, tid, smp, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TxdPredRegRegRegImm extends Definition("TxdPredRegRegRegImm", "TXD", Vec("TXD", "TXD.B")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid)
            ) => Some(Instruction.TxdPredRegRegRegImm(dstPredicate, dst, srcA, srcB, tid, modifiers, guard))
        case _ => None
      }
  }
  object TxdPredRegRegRegImmImm extends Definition(
        "TxdPredRegRegRegImmImm",
        "TXD",
        Vec("TXD", "TXD.B", "TXD_B_Legacy_Style", "TXD_Legacy_Style")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(smp)
            ) => Some(Instruction.TxdPredRegRegRegImmImm(dstPredicate, dst, srcA, srcB, tid, smp, modifiers, guard))
        case _ => None
      }
  }
  object TxdPredRegRegRegImmImmImm
      extends Definition("TxdPredRegRegRegImmImmImm", "TXD", Vec("TXD_B_Legacy_Style", "TXD_Legacy_Style")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(smp),
              InstructionOperand.Immediate(wmsk)
            ) =>
          Some(Instruction.TxdPredRegRegRegImmImmImm(dstPredicate, dst, srcA, srcB, tid, smp, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TxdPredRegRegRegReg extends Definition("TxdPredRegRegRegReg", "TXD", Vec("txd_b_noConst_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.TxdPredRegRegRegReg(pu, rd2, dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object TxdPredRegRegRegRegImm extends Definition("TxdPredRegRegRegRegImm", "TXD", Vec("txd_b_noConst_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TxdPredRegRegRegRegImm(pu, rd2, dst, srcA, srcB, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TxdPredRegRegRegRegImmImm extends Definition("TxdPredRegRegRegRegImmImm", "TXD", Vec("txd_", "txd_b_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(tsPtrIdx)
            ) =>
          Some(Instruction.TxdPredRegRegRegRegImmImm(pu, rd2, dst, srcA, srcB, bank5, tsPtrIdx, modifiers, guard))
        case _ => None
      }
  }
  object TxdPredRegRegRegRegImmImmImm
      extends Definition("TxdPredRegRegRegRegImmImmImm", "TXD", Vec("txd_", "txd_b_", "txd_b_tid_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(hdrtblbase6),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.TxdPredRegRegRegRegImmImmImm(
            pu,
            rd2,
            dst,
            srcA,
            srcB,
            bank5,
            hdrtblbase6,
            tid8,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object TxdPredRegRegRegRegImmImmImmImm
      extends Definition("TxdPredRegRegRegRegImmImmImmImm", "TXD", Vec("txd_b_tid_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(hdrtblbase6),
              InstructionOperand.Immediate(tid8),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TxdPredRegRegRegRegImmImmImmImm(
            pu,
            rd2,
            dst,
            srcA,
            srcB,
            bank5,
            hdrtblbase6,
            tid8,
            wmsk,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object TxdPredRegRegRegRegReg extends Definition("TxdPredRegRegRegRegReg", "TXD", Vec("txd_b_noConst_", "txd_urc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.TxdPredRegRegRegRegReg(pu, rd2, dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object TxdPredRegRegRegRegRegImm
      extends Definition("TxdPredRegRegRegRegRegImm", "TXD", Vec("txd_b_noConst_", "txd_urc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(tid8)
            ) => Some(Instruction.TxdPredRegRegRegRegRegImm(pu, rd2, dst, srcA, srcB, srcC, tid8, modifiers, guard))
        case _ => None
      }
  }
  object TxdPredRegRegRegRegRegImmImm extends Definition("TxdPredRegRegRegRegRegImmImm", "TXD", Vec("txd_urc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(tid8),
              InstructionOperand.Immediate(wmsk)
            ) =>
          Some(Instruction.TxdPredRegRegRegRegRegImmImm(pu, rd2, dst, srcA, srcB, srcC, tid8, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TxdPredRegRegRegRegRegReg
      extends Definition("TxdPredRegRegRegRegRegReg", "TXD", Vec("txd_b_urc_", "txd_urc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe)
            ) => Some(Instruction.TxdPredRegRegRegRegRegReg(pu, rd2, dst, srcA, srcB, srcC, uRe, modifiers, guard))
        case _ => None
      }
  }
  object TxdPredRegRegRegRegRegRegImm
      extends Definition("TxdPredRegRegRegRegRegRegImm", "TXD", Vec("txd_b_urc_", "txd_urc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe),
              InstructionOperand.Immediate(tid8)
            ) =>
          Some(Instruction.TxdPredRegRegRegRegRegRegImm(pu, rd2, dst, srcA, srcB, srcC, uRe, tid8, modifiers, guard))
        case _ => None
      }
  }
  object TxdPredRegRegRegRegRegRegImmImm extends Definition("TxdPredRegRegRegRegRegRegImmImm", "TXD", Vec("txd_urc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe),
              InstructionOperand.Immediate(tid8),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TxdPredRegRegRegRegRegRegImmImm(
            pu,
            rd2,
            dst,
            srcA,
            srcB,
            srcC,
            uRe,
            tid8,
            wmsk,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object TxdRegRegRegImm extends Definition("TxdRegRegRegImm", "TXD", Vec("TXD", "TXD.B")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid)
            ) => Some(Instruction.TxdRegRegRegImm(dst, srcA, srcB, tid, modifiers, guard))
        case _ => None
      }
  }
  object TxdRegRegRegImmImm
      extends Definition("TxdRegRegRegImmImm", "TXD", Vec("TXD", "TXD.B", "TXD_B_Legacy_Style", "TXD_Legacy_Style")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(smp)
            ) => Some(Instruction.TxdRegRegRegImmImm(dst, srcA, srcB, tid, smp, modifiers, guard))
        case _ => None
      }
  }
  object TxdRegRegRegImmImmImm extends Definition(
        "TxdRegRegRegImmImmImm",
        "TXD",
        Vec("TXD Legacy Style", "TXD.B Legacy Style", "TXD_B_Legacy_Style", "TXD_Legacy_Style")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(tid),
              InstructionOperand.Immediate(smp),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TxdRegRegRegImmImmImm(dst, srcA, srcB, tid, smp, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TxdRegRegRegReg extends Definition("TxdRegRegRegReg", "TXD", Vec("txd_b_noConst_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.TxdRegRegRegReg(rd2, dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object TxdRegRegRegRegImm extends Definition("TxdRegRegRegRegImm", "TXD", Vec("txd_b_noConst_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TxdRegRegRegRegImm(rd2, dst, srcA, srcB, wmsk, modifiers, guard))
        case _ => None
      }
  }
  object TxdRegRegRegRegImmImm extends Definition("TxdRegRegRegRegImmImm", "TXD", Vec("txd_", "txd_b_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(tsPtrIdx)
            ) => Some(Instruction.TxdRegRegRegRegImmImm(rd2, dst, srcA, srcB, bank5, tsPtrIdx, modifiers, guard))
        case _ => None
      }
  }
  object TxdRegRegRegRegImmImmImm
      extends Definition("TxdRegRegRegRegImmImmImm", "TXD", Vec("txd_", "txd_b_", "txd_b_tid_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(hdrtblbase6),
              InstructionOperand.Immediate(tid8)
            ) =>
          Some(Instruction.TxdRegRegRegRegImmImmImm(rd2, dst, srcA, srcB, bank5, hdrtblbase6, tid8, modifiers, guard))
        case _ => None
      }
  }
  object TxdRegRegRegRegImmImmImmImm extends Definition("TxdRegRegRegRegImmImmImmImm", "TXD", Vec("txd_b_tid_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(bank5),
              InstructionOperand.Immediate(hdrtblbase6),
              InstructionOperand.Immediate(tid8),
              InstructionOperand.Immediate(wmsk)
            ) => Some(Instruction.TxdRegRegRegRegImmImmImmImm(
            rd2,
            dst,
            srcA,
            srcB,
            bank5,
            hdrtblbase6,
            tid8,
            wmsk,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object TxdRegRegRegRegReg extends Definition("TxdRegRegRegRegReg", "TXD", Vec("txd_b_noConst_", "txd_urc_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(rd2),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.TxdRegRegRegRegReg(rd2, dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  val definitions: Vec[Definition] = Vec(
    SetcrsptrReg,
    SetctaidReg,
    SetlmembaseReg,
    SgxtRegRegAddressImm,
    SgxtRegRegConst,
    SgxtRegRegImm,
    SgxtRegRegReg,
    ShfRegRegAddressImmReg,
    ShfRegRegConstReg,
    ShfRegRegImmReg,
    ShfRegRegRegAddressImm,
    ShfRegRegRegConst,
    ShfRegRegRegImm,
    ShfRegRegRegReg,
    ShflPredRegRegImmImm,
    ShflPredRegRegImmReg,
    ShflPredRegRegRegImm,
    ShflPredRegRegRegReg,
    ShintImmImm,
    ShintImmImmImmImmImmImmImmImmImmImmImmImmImmImm,
    ShlRegImmReg,
    ShlRegRegAddressImm,
    ShlRegRegConst,
    ShlRegRegImm,
    ShlRegRegReg,
    ShrRegRegAddressImm,
    ShrRegRegConst,
    ShrRegRegImm,
    ShrRegRegReg,
    SpmetadataRegRegRegImmImmImm,
    SsyConst,
    SsyImm,
    StAddressReg,
    StAddressRegPred,
    StasAddressReg,
    StgAddressReg,
    StgAddressRegReg,
    StgAddressRegRegImm,
    StlAddressReg,
    StpImm,
    StsAddressReg,
    StsculPredAddressReg,
    StsmAddressReg,
    SttAddressReg,
    SttmAddressReg,
    SuatomPredRegAddressRegImm,
    SuatomPredRegAddressRegImmImm,
    SuatomPredRegAddressRegImmImmImm,
    SuatomPredRegAddressRegReg,
    SuatomPredRegAddressRegRegImm,
    SuatomPredRegAddressRegRegReg,
    SuatomPredRegAddressRegRegRegImm,
    SuatomPredRegAddressRegRegRegReg,
    SuatomRegAddressRegImm,
    SuatomRegAddressRegImmImm,
    SuatomRegAddressRegImmImmImm,
    SuatomRegAddressRegReg,
    SuatomRegAddressRegRegImm,
    SuatomRegAddressRegRegReg,
    SuatomRegAddressRegRegRegImm,
    SuatomRegAddressRegRegRegReg,
    SubfmPredRegRegConstReg,
    SubfmPredRegRegImmReg,
    SubfmPredRegRegRegConst,
    SubfmPredRegRegRegReg,
    SuclampPredRegRegConstImm,
    SuclampPredRegRegImmImm,
    SuclampPredRegRegRegImm,
    SueauRegRegConstReg,
    SueauRegRegImmReg,
    SueauRegRegRegConst,
    SueauRegRegRegReg,
    SuldPredRegAddressImm,
    SuldPredRegAddressImmImm,
    SuldPredRegAddressImmImmImm,
    SuldPredRegAddressReg,
    SuldPredRegAddressRegImm,
    SuldPredRegAddressRegReg,
    SuldPredRegAddressRegRegImm,
    SuldPredRegAddressRegRegReg,
    SuldRegAddressImm,
    SuldRegAddressImmImm,
    SuldRegAddressImmImmImm,
    SuldRegAddressReg,
    SuldRegAddressRegImm,
    SuldRegAddressRegReg,
    SuldRegAddressRegRegImm,
    SuldRegAddressRegRegReg,
    SuldgaRegAddressConstPred,
    SuldgaRegAddressRegPred,
    SuqueryPredRegAddressImmImm,
    SuqueryPredRegAddressImmImmImm,
    SuqueryPredRegAddressReg,
    SuqueryPredRegAddressRegImm,
    SuqueryPredRegAddressRegReg,
    SuqueryPredRegAddressRegRegImm,
    SuqueryPredRegAddressRegRegReg,
    SuqueryRegAddressImmImm,
    SuqueryRegAddressImmImmImm,
    SuqueryRegAddressReg,
    SuqueryRegAddressRegImm,
    SuqueryRegAddressRegReg,
    SuqueryRegAddressRegRegImm,
    SuqueryRegAddressRegRegReg,
    SuredAddressRegImm,
    SuredAddressRegImmImm,
    SuredAddressRegImmImmImm,
    SuredAddressRegReg,
    SuredAddressRegRegImm,
    SuredAddressRegRegReg,
    SuredAddressRegRegRegImm,
    SuredAddressRegRegRegReg,
    SustAddressRegImm,
    SustAddressRegImmImm,
    SustAddressRegImmImmImm,
    SustAddressRegReg,
    SustAddressRegRegImm,
    SustAddressRegRegReg,
    SustAddressRegRegRegImm,
    SustAddressRegRegRegReg,
    SustgaAddressConstRegPred,
    SustgaAddressRegRegPred,
    SyncNoOperands,
    SyncsNoOperands,
    SyncsAddress,
    SyncsAddressReg,
    SyncsPredAddressReg,
    SyncsRegAddress,
    SyncsRegAddressReg,
    SyncsRegAddressRegReg,
    TexPredRegRegRegImm,
    TexPredRegRegRegImmImm,
    TexPredRegRegRegImmImmImm,
    TexPredRegRegRegReg,
    TexPredRegRegRegRegImm,
    TexPredRegRegRegRegImmImm,
    TexPredRegRegRegRegImmImmImm,
    TexPredRegRegRegRegImmImmImmImm,
    TexPredRegRegRegRegReg,
    TexPredRegRegRegRegRegImm,
    TexPredRegRegRegRegRegImmImm,
    TexPredRegRegRegRegRegReg,
    TexPredRegRegRegRegRegRegImm,
    TexPredRegRegRegRegRegRegImmImm,
    TexRegRegRegImm,
    TexRegRegRegImmImm,
    TexRegRegRegImmImmImm,
    TexRegRegRegReg,
    TexRegRegRegRegImm,
    TexRegRegRegRegImmImm,
    TexRegRegRegRegImmImmImm,
    TexRegRegRegRegImmImmImmImm,
    TexRegRegRegRegReg,
    TexRegRegRegRegRegImm,
    TexRegRegRegRegRegImmImm,
    TexRegRegRegRegRegReg,
    TexRegRegRegRegRegRegImm,
    TexRegRegRegRegRegRegImmImm,
    TexdepbarImm,
    TexsRegRegRegRegImm,
    TldPredRegRegRegImm,
    TldPredRegRegRegImmImm,
    TldPredRegRegRegReg,
    TldPredRegRegRegRegImm,
    TldPredRegRegRegRegImmImm,
    TldPredRegRegRegRegImmImmImm,
    TldPredRegRegRegRegImmImmImmImm,
    TldPredRegRegRegRegReg,
    TldPredRegRegRegRegRegImm,
    TldPredRegRegRegRegRegImmImm,
    TldPredRegRegRegRegRegReg,
    TldPredRegRegRegRegRegRegImm,
    TldPredRegRegRegRegRegRegImmImm,
    TldRegRegRegImm,
    TldRegRegRegImmImm,
    TldRegRegRegReg,
    TldRegRegRegRegImm,
    TldRegRegRegRegImmImm,
    TldRegRegRegRegImmImmImm,
    TldRegRegRegRegImmImmImmImm,
    TldRegRegRegRegReg,
    TldRegRegRegRegRegImm,
    TldRegRegRegRegRegImmImm,
    TldRegRegRegRegRegReg,
    TldRegRegRegRegRegRegImm,
    TldRegRegRegRegRegRegImmImm,
    Tld4PredRegRegRegImm,
    Tld4PredRegRegRegImmImm,
    Tld4PredRegRegRegImmImmImm,
    Tld4PredRegRegRegReg,
    Tld4PredRegRegRegRegImm,
    Tld4PredRegRegRegRegImmImm,
    Tld4PredRegRegRegRegImmImmImm,
    Tld4PredRegRegRegRegImmImmImmImm,
    Tld4PredRegRegRegRegReg,
    Tld4PredRegRegRegRegRegImm,
    Tld4PredRegRegRegRegRegImmImm,
    Tld4PredRegRegRegRegRegReg,
    Tld4PredRegRegRegRegRegRegImm,
    Tld4PredRegRegRegRegRegRegImmImm,
    Tld4RegRegRegImm,
    Tld4RegRegRegImmImm,
    Tld4RegRegRegImmImmImm,
    Tld4RegRegRegReg,
    Tld4RegRegRegRegImm,
    Tld4RegRegRegRegImmImm,
    Tld4RegRegRegRegImmImmImm,
    Tld4RegRegRegRegImmImmImmImm,
    Tld4RegRegRegRegReg,
    Tld4RegRegRegRegRegImm,
    Tld4RegRegRegRegRegImmImm,
    Tld4RegRegRegRegRegReg,
    Tld4RegRegRegRegRegRegImm,
    Tld4RegRegRegRegRegRegImmImm,
    Tld4sRegRegRegRegImm,
    TldsRegRegRegRegImm,
    TmmlRegRegImm,
    TmmlRegRegImmImm,
    TmmlRegRegImmImmImm,
    TmmlRegRegRegImm,
    TmmlRegRegRegImmImm,
    TmmlRegRegRegImmImmImm,
    TmmlRegRegRegReg,
    TmmlRegRegRegRegImm,
    TmmlRegRegRegRegImmImm,
    TmmlRegRegRegRegImmImmImm,
    TmmlRegRegRegRegImmImmImmImm,
    TmmlRegRegRegRegReg,
    TmmlRegRegRegRegRegImm,
    TmmlRegRegRegRegRegImmImm,
    TxaRegRegImm,
    TxaRegRegImmImm,
    TxaRegRegImmImmImm,
    TxdPredRegRegRegImm,
    TxdPredRegRegRegImmImm,
    TxdPredRegRegRegImmImmImm,
    TxdPredRegRegRegReg,
    TxdPredRegRegRegRegImm,
    TxdPredRegRegRegRegImmImm,
    TxdPredRegRegRegRegImmImmImm,
    TxdPredRegRegRegRegImmImmImmImm,
    TxdPredRegRegRegRegReg,
    TxdPredRegRegRegRegRegImm,
    TxdPredRegRegRegRegRegImmImm,
    TxdPredRegRegRegRegRegReg,
    TxdPredRegRegRegRegRegRegImm,
    TxdPredRegRegRegRegRegRegImmImm,
    TxdRegRegRegImm,
    TxdRegRegRegImmImm,
    TxdRegRegRegImmImmImm,
    TxdRegRegRegReg,
    TxdRegRegRegRegImm,
    TxdRegRegRegRegImmImm,
    TxdRegRegRegRegImmImmImm,
    TxdRegRegRegRegImmImmImmImm,
    TxdRegRegRegRegReg
  )
}
