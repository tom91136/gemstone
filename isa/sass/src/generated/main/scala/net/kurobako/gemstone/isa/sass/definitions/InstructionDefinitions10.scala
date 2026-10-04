// Generated SASS instruction definitions and checked operand constructors. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.sass
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import Instruction.*
@javax.annotation.processing.Generated(Array("sbt codegen"))
object InstructionDefinitions10 {
  object R2pintPipeSymbolRegImm extends Definition(
        "R2pintPipeSymbolRegImm",
        "R2Pint_pipe",
        Vec(Slot("pR", OperandKind.Symbol), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("r2p__RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Symbol(pR), InstructionOperand.Register(srcA), InstructionOperand.Immediate(sb)) =>
          Some(Instruction.R2pintPipeSymbolRegImm(pR, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object R2pintPipeSymbolRegReg extends Definition(
        "R2pintPipeSymbolRegReg",
        "R2Pint_pipe",
        Vec(Slot("pR", OperandKind.Symbol), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("r2p__RRR", "r2p__RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Symbol(pR), InstructionOperand.Register(srcA), InstructionOperand.Register(srcB)) =>
          Some(Instruction.R2pintPipeSymbolRegReg(pR, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object R2pxluPipeSymbolReg extends Definition(
        "R2pxluPipeSymbolReg",
        "R2Pxlu_pipe",
        Vec(Slot("pr", OperandKind.Symbol), Slot("srcA", OperandKind.Register)),
        Vec("Imm R2P")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Symbol(pr), InstructionOperand.Register(srcA)) =>
          Some(Instruction.R2pxluPipeSymbolReg(pr, srcA, modifiers, guard))
        case _ => None
      }
  }
  object R2pxluPipeSymbolRegConst extends Definition(
        "R2pxluPipeSymbolRegConst",
        "R2Pxlu_pipe",
        Vec(
          Slot("pr", OperandKind.Symbol),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const R2P")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Symbol(pr),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.R2pxluPipeSymbolRegConst(pr, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object R2pxluPipeSymbolRegImm extends Definition(
        "R2pxluPipeSymbolRegImm",
        "R2Pxlu_pipe",
        Vec(Slot("pr", OperandKind.Symbol), Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("Imm R2P")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Symbol(pr),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.R2pxluPipeSymbolRegImm(pr, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object R2pxluPipeSymbolRegReg extends Definition(
        "R2pxluPipeSymbolRegReg",
        "R2Pxlu_pipe",
        Vec(Slot("pr", OperandKind.Symbol), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("R2P")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Symbol(pr), InstructionOperand.Register(srcA), InstructionOperand.Register(srcB)) =>
          Some(Instruction.R2pxluPipeSymbolRegReg(pr, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object R2urPredRegReg extends Definition(
        "R2urPredRegReg",
        "R2UR",
        Vec(Slot("pu", OperandKind.Predicate), Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register)),
        Vec("r2ur__OR", "r2ur__noOR", "r2ur_nonconformity__OR", "r2ur_nonconformity__noOR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA)
            ) => Some(Instruction.R2urPredRegReg(pu, dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object R2urRegReg extends Definition(
        "R2urRegReg",
        "R2UR",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register)),
        Vec("r2ur__noOR", "r2ur_nonconformity__noOR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcA)) =>
          Some(Instruction.R2urRegReg(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object R2urudpPipePredRegReg extends Definition(
        "R2urudpPipePredRegReg",
        "R2URudp_pipe",
        Vec(Slot("pu", OperandKind.Predicate), Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register)),
        Vec("r2ur__OR", "r2ur__noOR", "r2ur_nonconformity__OR", "r2ur_nonconformity__noOR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pu),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA)
            ) => Some(Instruction.R2urudpPipePredRegReg(pu, dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object R2urudpPipeRegReg extends Definition(
        "R2urudpPipeRegReg",
        "R2URudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register)),
        Vec("r2ur__noOR", "r2ur_nonconformity__noOR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcA)) =>
          Some(Instruction.R2urudpPipeRegReg(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object RamNoOperands extends Definition("RamNoOperands", "RAM", Vec(), Vec("RAM", "SAM")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.RamNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object RambruPipeNoOperands extends Definition("RambruPipeNoOperands", "RAMbru_pipe", Vec(), Vec("RAM", "SAM")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.RambruPipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object RedAddressReg extends Definition(
        "RedAddressReg",
        "RED",
        Vec(Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register)),
        Vec(
          "I RED",
          "I_RED",
          "RED",
          "red__RaNonRZ",
          "red__RaRZ",
          "red_fp__RaNonRZ",
          "red_fp__RaRZ",
          "red_fp_uniform__Ra32",
          "red_fp_uniform__Ra64",
          "red_fp_uniform__RaRZ",
          "red_fp_uniform__memdesc",
          "red_int__RaNonRZ",
          "red_int__RaRZ",
          "red_int_uniform__Ra32",
          "red_int_uniform__Ra64",
          "red_int_uniform__RaRZ",
          "red_int_uniform__memdesc",
          "red_uniform__Ra32",
          "red_uniform__Ra64",
          "red_uniform__RaRZ",
          "red_uniform__memdesc",
          "red_uniform_weakonly__Ra32",
          "red_uniform_weakonly__Ra64",
          "red_uniform_weakonly__RaRZ",
          "red_weakonly__RaNonRZ",
          "red_weakonly__RaRZ"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA), InstructionOperand.Register(srcB)) =>
          Some(Instruction.RedAddressReg(srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object RedasAddressReg extends Definition(
        "RedasAddressReg",
        "REDAS",
        Vec(Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register)),
        Vec("redas_64__Ra64", "redas_64__RaRZ", "redas__Ra32")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA), InstructionOperand.Register(srcB)) =>
          Some(Instruction.RedasAddressReg(srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object RedasmioPipeAddressReg extends Definition(
        "RedasmioPipeAddressReg",
        "REDASmio_pipe",
        Vec(Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register)),
        Vec("redas_64__Ra64", "redas_64__RaRZ", "redas__Ra32")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA), InstructionOperand.Register(srcB)) =>
          Some(Instruction.RedasmioPipeAddressReg(srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object RedgAddressReg extends Definition(
        "RedgAddressReg",
        "REDG",
        Vec(Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register)),
        Vec(
          "redg_fp__RaNonRZ",
          "redg_fp__RaRZ",
          "redg_fp_uniform__Ra32",
          "redg_fp_uniform__Ra64",
          "redg_fp_uniform__RaRZ",
          "redg_fp_uniform__memdesc",
          "redg_int__RaNonRZ",
          "redg_int__RaRZ",
          "redg_int_uniform__Ra32",
          "redg_int_uniform__Ra64",
          "redg_int_uniform__RaRZ",
          "redg_int_uniform__memdesc"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA), InstructionOperand.Register(srcB)) =>
          Some(Instruction.RedgAddressReg(srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object RedgmioPipeAddressReg extends Definition(
        "RedgmioPipeAddressReg",
        "REDGmio_pipe",
        Vec(Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register)),
        Vec(
          "redg_fp__RaNonRZ",
          "redg_fp__RaRZ",
          "redg_fp_uniform__Ra32",
          "redg_fp_uniform__Ra64",
          "redg_fp_uniform__RaRZ",
          "redg_fp_uniform__memdesc",
          "redg_int__RaNonRZ",
          "redg_int__RaRZ",
          "redg_int_uniform__Ra32",
          "redg_int_uniform__Ra64",
          "redg_int_uniform__RaRZ",
          "redg_int_uniform__memdesc"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA), InstructionOperand.Register(srcB)) =>
          Some(Instruction.RedgmioPipeAddressReg(srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object RedsAddressReg extends Definition(
        "RedsAddressReg",
        "REDS",
        Vec(Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register)),
        Vec("atoms_reds__RaNonRZ", "atoms_reds__RaRZ", "atoms_reds_uniform_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA), InstructionOperand.Register(srcB)) =>
          Some(Instruction.RedsAddressReg(srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object RedsmioPipeAddressReg extends Definition(
        "RedsmioPipeAddressReg",
        "REDSmio_pipe",
        Vec(Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register)),
        Vec("atoms_reds__RaNonRZ", "atoms_reds__RaRZ", "atoms_reds_uniform_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA), InstructionOperand.Register(srcB)) =>
          Some(Instruction.RedsmioPipeAddressReg(srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object ReduxRegReg extends Definition(
        "ReduxRegReg",
        "REDUX",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register)),
        Vec("redux_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcA)) =>
          Some(Instruction.ReduxRegReg(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object ReduxudpPipeRegReg extends Definition(
        "ReduxudpPipeRegReg",
        "REDUXudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register)),
        Vec("redux_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcA)) =>
          Some(Instruction.ReduxudpPipeRegReg(dst, srcA, modifiers, guard))
        case _ => None
      }
  }
  object RedaguPipeAddressReg extends Definition(
        "RedaguPipeAddressReg",
        "REDagu_pipe",
        Vec(Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register)),
        Vec("I RED", "RED")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA), InstructionOperand.Register(srcB)) =>
          Some(Instruction.RedaguPipeAddressReg(srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object RedmioPipeAddressReg extends Definition(
        "RedmioPipeAddressReg",
        "REDmio_pipe",
        Vec(Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register)),
        Vec(
          "I RED",
          "I_RED",
          "RED",
          "red__RaNonRZ",
          "red__RaRZ",
          "red_fp__RaNonRZ",
          "red_fp__RaRZ",
          "red_fp_uniform__Ra32",
          "red_fp_uniform__Ra64",
          "red_fp_uniform__RaRZ",
          "red_fp_uniform__memdesc",
          "red_int__RaNonRZ",
          "red_int__RaRZ",
          "red_int_uniform__Ra32",
          "red_int_uniform__Ra64",
          "red_int_uniform__RaRZ",
          "red_int_uniform__memdesc",
          "red_uniform__Ra32",
          "red_uniform__Ra64",
          "red_uniform__RaRZ",
          "red_uniform__memdesc",
          "red_uniform_weakonly__Ra32",
          "red_uniform_weakonly__Ra64",
          "red_uniform_weakonly__RaRZ",
          "red_weakonly__RaNonRZ",
          "red_weakonly__RaRZ"
        )
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA), InstructionOperand.Register(srcB)) =>
          Some(Instruction.RedmioPipeAddressReg(srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object RetNoOperands extends Definition("RetNoOperands", "RET", Vec(), Vec("RET")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.RetNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object RetPredReg extends Definition(
        "RetPredReg",
        "RET",
        Vec(Slot("pp", OperandKind.Predicate), Slot("srcA", OperandKind.Register)),
        Vec("ret_rel_reg_", "ret_rel_reg__RIR", "ret_rel_reg__URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.Register(srcA)) =>
          Some(Instruction.RetPredReg(pp, srcA, modifiers, guard))
        case _ => None
      }
  }
  object RetPredRegImm extends Definition(
        "RetPredRegImm",
        "RET",
        Vec(
          Slot("pp", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("ra_offset", OperandKind.Immediate)
        ),
        Vec("ret__ABS", "ret__ABS_UR", "ret__REL", "ret__REL_UR", "ret_rel_", "ret_rel__RIR", "ret_rel__URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pp),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(ra_offset)
            ) => Some(Instruction.RetPredRegImm(pp, srcA, ra_offset, modifiers, guard))
        case _ => None
      }
  }
  object RetReg extends Definition(
        "RetReg",
        "RET",
        Vec(Slot("srcA", OperandKind.Register)),
        Vec("ret_rel_reg_", "ret_rel_reg__RIR", "ret_rel_reg__URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA)) => Some(Instruction.RetReg(srcA, modifiers, guard))
        case _ => None
      }
  }
  object RetRegImm extends Definition(
        "RetRegImm",
        "RET",
        Vec(Slot("srcA", OperandKind.Register), Slot("ra_offset", OperandKind.Immediate)),
        Vec("ret__ABS", "ret__ABS_UR", "ret__REL", "ret__REL_UR", "ret_rel_", "ret_rel__RIR", "ret_rel__URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA), InstructionOperand.Immediate(ra_offset)) =>
          Some(Instruction.RetRegImm(srcA, ra_offset, modifiers, guard))
        case _ => None
      }
  }
  object RetbruPipeNoOperands extends Definition("RetbruPipeNoOperands", "RETbru_pipe", Vec(), Vec("RET")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.RetbruPipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object RetcbuPipePredReg extends Definition(
        "RetcbuPipePredReg",
        "RETcbu_pipe",
        Vec(Slot("pp", OperandKind.Predicate), Slot("srcA", OperandKind.Register)),
        Vec("ret_rel_reg_", "ret_rel_reg__RIR", "ret_rel_reg__URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Predicate(pp), InstructionOperand.Register(srcA)) =>
          Some(Instruction.RetcbuPipePredReg(pp, srcA, modifiers, guard))
        case _ => None
      }
  }
  object RetcbuPipePredRegImm extends Definition(
        "RetcbuPipePredRegImm",
        "RETcbu_pipe",
        Vec(
          Slot("pp", OperandKind.Predicate),
          Slot("srcA", OperandKind.Register),
          Slot("ra_offset", OperandKind.Immediate)
        ),
        Vec("ret__ABS", "ret__ABS_UR", "ret__REL", "ret__REL_UR", "ret_rel_", "ret_rel__RIR", "ret_rel__URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pp),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(ra_offset)
            ) => Some(Instruction.RetcbuPipePredRegImm(pp, srcA, ra_offset, modifiers, guard))
        case _ => None
      }
  }
  object RetcbuPipeReg extends Definition(
        "RetcbuPipeReg",
        "RETcbu_pipe",
        Vec(Slot("srcA", OperandKind.Register)),
        Vec("ret_rel_reg_", "ret_rel_reg__RIR", "ret_rel_reg__URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA)) => Some(Instruction.RetcbuPipeReg(srcA, modifiers, guard))
        case _ => None
      }
  }
  object RetcbuPipeRegImm extends Definition(
        "RetcbuPipeRegImm",
        "RETcbu_pipe",
        Vec(Slot("srcA", OperandKind.Register), Slot("ra_offset", OperandKind.Immediate)),
        Vec("ret__ABS", "ret__ABS_UR", "ret__REL", "ret__REL_UR", "ret_rel_", "ret_rel__RIR", "ret_rel__URIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA), InstructionOperand.Immediate(ra_offset)) =>
          Some(Instruction.RetcbuPipeRegImm(srcA, ra_offset, modifiers, guard))
        case _ => None
      }
  }
  object RpcmovAddressImm extends Definition(
        "RpcmovAddressImm",
        "RPCMOV",
        Vec(Slot("srcB", OperandKind.Address), Slot("sb_offset", OperandKind.Immediate)),
        Vec("rpcmov_dstPc64__CXb", "rpcmov_dstPc_CXb_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcB), InstructionOperand.Immediate(sb_offset)) =>
          Some(Instruction.RpcmovAddressImm(srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object RpcmovConst extends Definition(
        "RpcmovConst",
        "RPCMOV",
        Vec(Slot("sb", OperandKind.ConstantBank)),
        Vec("rpcmov_dstPc64__Const", "rpcmov_dstPc_const_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(sb)) => Some(Instruction.RpcmovConst(sb, modifiers, guard))
        case _ => None
      }
  }
  object RpcmovImm extends Definition(
        "RpcmovImm",
        "RPCMOV",
        Vec(Slot("sb", OperandKind.Immediate)),
        Vec("rpcmov_dstPc64__Imm", "rpcmov_dstPc_imm_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb)) => Some(Instruction.RpcmovImm(sb, modifiers, guard))
        case _ => None
      }
  }
  object RpcmovReg extends Definition(
        "RpcmovReg",
        "RPCMOV",
        Vec(Slot("srcB", OperandKind.Register)),
        Vec("rpcmov_dstPc64__URb", "rpcmov_dstPc_", "rpcmov_dstPc_URb_", "rpcmov_srcPc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcB)) => Some(Instruction.RpcmovReg(srcB, modifiers, guard))
        case _ => None
      }
  }
  object RpcmovintPipeAddressImm extends Definition(
        "RpcmovintPipeAddressImm",
        "RPCMOVint_pipe",
        Vec(Slot("srcB", OperandKind.Address), Slot("sb_offset", OperandKind.Immediate)),
        Vec("rpcmov_dstPc64__CXb", "rpcmov_dstPc_CXb_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcB), InstructionOperand.Immediate(sb_offset)) =>
          Some(Instruction.RpcmovintPipeAddressImm(srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object RpcmovintPipeConst extends Definition(
        "RpcmovintPipeConst",
        "RPCMOVint_pipe",
        Vec(Slot("sb", OperandKind.ConstantBank)),
        Vec("rpcmov_dstPc64__Const", "rpcmov_dstPc_const_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(sb)) => Some(Instruction.RpcmovintPipeConst(sb, modifiers, guard))
        case _ => None
      }
  }
  object RpcmovintPipeImm extends Definition(
        "RpcmovintPipeImm",
        "RPCMOVint_pipe",
        Vec(Slot("sb", OperandKind.Immediate)),
        Vec("rpcmov_dstPc64__Imm", "rpcmov_dstPc_imm_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sb)) => Some(Instruction.RpcmovintPipeImm(sb, modifiers, guard))
        case _ => None
      }
  }
  object RpcmovintPipeReg extends Definition(
        "RpcmovintPipeReg",
        "RPCMOVint_pipe",
        Vec(Slot("srcB", OperandKind.Register)),
        Vec("rpcmov_dstPc64__URb", "rpcmov_dstPc_", "rpcmov_dstPc_URb_", "rpcmov_srcPc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcB)) => Some(Instruction.RpcmovintPipeReg(srcB, modifiers, guard))
        case _ => None
      }
  }
  object RroRegConst extends Definition(
        "RroRegConst",
        "RRO",
        Vec(Slot("dst", OperandKind.Register), Slot("srcConst", OperandKind.ConstantBank)),
        Vec("Const RRO", "Const_RRO")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(srcConst)) =>
          Some(Instruction.RroRegConst(dst, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object RroRegImm extends Definition(
        "RroRegImm",
        "RRO",
        Vec(Slot("dst", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("Imm RRO", "Imm_RRO")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(uImm)) =>
          Some(Instruction.RroRegImm(dst, uImm, modifiers, guard))
        case _ => None
      }
  }
  object RroRegReg extends Definition(
        "RroRegReg",
        "RRO",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("RRO")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.RroRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object RrofmaxPipeRegConst extends Definition(
        "RrofmaxPipeRegConst",
        "RROfmaX_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcConst", OperandKind.ConstantBank)),
        Vec("Const RRO")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(srcConst)) =>
          Some(Instruction.RrofmaxPipeRegConst(dst, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object RrofmaxPipeRegImm extends Definition(
        "RrofmaxPipeRegImm",
        "RROfmaX_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("Imm RRO")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(uImm)) =>
          Some(Instruction.RrofmaxPipeRegImm(dst, uImm, modifiers, guard))
        case _ => None
      }
  }
  object RrofmaxPipeRegReg extends Definition(
        "RrofmaxPipeRegReg",
        "RROfmaX_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("RRO")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.RrofmaxPipeRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object RrofxuPipeRegConst extends Definition(
        "RrofxuPipeRegConst",
        "RROfxu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcConst", OperandKind.ConstantBank)),
        Vec("Const_RRO")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.ConstantBank(srcConst)) =>
          Some(Instruction.RrofxuPipeRegConst(dst, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object RrofxuPipeRegImm extends Definition(
        "RrofxuPipeRegImm",
        "RROfxu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("Imm_RRO")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Immediate(uImm)) =>
          Some(Instruction.RrofxuPipeRegImm(dst, uImm, modifiers, guard))
        case _ => None
      }
  }
  object RrofxuPipeRegReg extends Definition(
        "RrofxuPipeRegReg",
        "RROfxu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("RRO")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.Register(srcB)) =>
          Some(Instruction.RrofxuPipeRegReg(dst, srcB, modifiers, guard))
        case _ => None
      }
  }
  object RttNoOperands extends Definition("RttNoOperands", "RTT", Vec(), Vec("RTT", "rtt_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.RttNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object RttbruPipeNoOperands extends Definition("RttbruPipeNoOperands", "RTTbru_pipe", Vec(), Vec("RTT")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.RttbruPipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object RttcbuPipeNoOperands extends Definition("RttcbuPipeNoOperands", "RTTcbu_pipe", Vec(), Vec("rtt_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.RttcbuPipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object S2rRegSpecial extends Definition(
        "S2rRegSpecial",
        "S2R",
        Vec(Slot("dst", OperandKind.Register), Slot("sR", OperandKind.SpecialRegister)),
        Vec("S2R", "s2r_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.SpecialRegister(sR)) =>
          Some(Instruction.S2rRegSpecial(dst, sR, modifiers, guard))
        case _ => None
      }
  }
  object S2rfauPipeRegSpecial extends Definition(
        "S2rfauPipeRegSpecial",
        "S2Rfau_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("sR", OperandKind.SpecialRegister)),
        Vec("S2R")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.SpecialRegister(sR)) =>
          Some(Instruction.S2rfauPipeRegSpecial(dst, sR, modifiers, guard))
        case _ => None
      }
  }
  object S2rmioPipeRegSpecial extends Definition(
        "S2rmioPipeRegSpecial",
        "S2Rmio_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("sR", OperandKind.SpecialRegister)),
        Vec("S2R", "s2r_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.SpecialRegister(sR)) =>
          Some(Instruction.S2rmioPipeRegSpecial(dst, sR, modifiers, guard))
        case _ => None
      }
  }
  object S2rxluPipeRegSpecial extends Definition(
        "S2rxluPipeRegSpecial",
        "S2Rxlu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("sR", OperandKind.SpecialRegister)),
        Vec("S2R")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.SpecialRegister(sR)) =>
          Some(Instruction.S2rxluPipeRegSpecial(dst, sR, modifiers, guard))
        case _ => None
      }
  }
  object S2urRegSpecial extends Definition(
        "S2urRegSpecial",
        "S2UR",
        Vec(Slot("dst", OperandKind.Register), Slot("sRa", OperandKind.SpecialRegister)),
        Vec("s2ur_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.SpecialRegister(sRa)) =>
          Some(Instruction.S2urRegSpecial(dst, sRa, modifiers, guard))
        case _ => None
      }
  }
  object S2urudpPipeRegSpecial extends Definition(
        "S2urudpPipeRegSpecial",
        "S2URudp_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("sRa", OperandKind.SpecialRegister)),
        Vec("s2ur_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(dst), InstructionOperand.SpecialRegister(sRa)) =>
          Some(Instruction.S2urudpPipeRegSpecial(dst, sRa, modifiers, guard))
        case _ => None
      }
  }
  object SamNoOperands extends Definition("SamNoOperands", "SAM", Vec(), Vec("SAM")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.SamNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object SambruPipeNoOperands extends Definition("SambruPipeNoOperands", "SAMbru_pipe", Vec(), Vec("SAM")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec() => Some(Instruction.SambruPipeNoOperands(modifiers, guard))
        case _ => None
      }
  }
  object ScatterRegRegRegRegImmImm extends Definition(
        "ScatterRegRegRegRegImmImm",
        "SCATTER",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("vecidx", OperandKind.Immediate),
          Slot("mask", OperandKind.Immediate)
        ),
        Vec("scatter_", "scatter_combined_idxtype_extract_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(vecidx),
              InstructionOperand.Immediate(mask)
            ) => Some(Instruction.ScatterRegRegRegRegImmImm(dst, srcA, srcB, srcC, vecidx, mask, modifiers, guard))
        case _ => None
      }
  }
  object ScatterintPipeRegRegRegRegImmImm extends Definition(
        "ScatterintPipeRegRegRegRegImmImm",
        "SCATTERint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("vecidx", OperandKind.Immediate),
          Slot("mask", OperandKind.Immediate)
        ),
        Vec("scatter_", "scatter_combined_idxtype_extract_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(vecidx),
              InstructionOperand.Immediate(mask)
            ) =>
          Some(Instruction.ScatterintPipeRegRegRegRegImmImm(dst, srcA, srcB, srcC, vecidx, mask, modifiers, guard))
        case _ => None
      }
  }
  object SelRegRegAddressImmPred extends Definition(
        "SelRegRegAddressImmPred",
        "SEL",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("sel__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.SelRegRegAddressImmPred(dst, srcA, srcB, sb_offset, pp, modifiers, guard))
        case _ => None
      }
  }
  object SelRegRegConstPred extends Definition(
        "SelRegRegConstPred",
        "SEL",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("Const SEL", "Const_SEL", "sel__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.SelRegRegConstPred(dst, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object SelRegRegImmPred extends Definition(
        "SelRegRegImmPred",
        "SEL",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("Imm SEL", "Imm_SEL", "sel_64__RIR", "sel__RuIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.SelRegRegImmPred(dst, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object SelRegRegRegPred extends Definition(
        "SelRegRegRegPred",
        "SEL",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("SEL", "sel_64__RRR", "sel_64__RUR", "sel__RRR_RRR", "sel__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.SelRegRegRegPred(dst, srcA, srcB, pa, modifiers, guard))
        case _ => None
      }
  }
  object SelfauPipeRegRegConstPred extends Definition(
        "SelfauPipeRegRegConstPred",
        "SELfau_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Const SEL")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.SelfauPipeRegRegConstPred(dst, srcA, srcConst, pa, modifiers, guard))
        case _ => None
      }
  }
  object SelfauPipeRegRegImmPred extends Definition(
        "SelfauPipeRegRegImmPred",
        "SELfau_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Imm SEL")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.SelfauPipeRegRegImmPred(dst, srcA, uImm, pa, modifiers, guard))
        case _ => None
      }
  }
  object SelfauPipeRegRegRegPred extends Definition(
        "SelfauPipeRegRegRegPred",
        "SELfau_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("SEL")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.SelfauPipeRegRegRegPred(dst, srcA, srcB, pa, modifiers, guard))
        case _ => None
      }
  }
  object SelfxuPipeRegRegConstPred extends Definition(
        "SelfxuPipeRegRegConstPred",
        "SELfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Const_SEL")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.SelfxuPipeRegRegConstPred(dst, srcA, srcConst, pa, modifiers, guard))
        case _ => None
      }
  }
  object SelfxuPipeRegRegImmPred extends Definition(
        "SelfxuPipeRegRegImmPred",
        "SELfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Imm_SEL")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.SelfxuPipeRegRegImmPred(dst, srcA, uImm, pa, modifiers, guard))
        case _ => None
      }
  }
  object SelfxuPipeRegRegRegPred extends Definition(
        "SelfxuPipeRegRegRegPred",
        "SELfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("SEL")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.SelfxuPipeRegRegRegPred(dst, srcA, srcB, pa, modifiers, guard))
        case _ => None
      }
  }
  object SelintPipeRegRegAddressImmPred extends Definition(
        "SelintPipeRegRegAddressImmPred",
        "SELint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("sel__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.SelintPipeRegRegAddressImmPred(dst, srcA, srcB, sb_offset, pp, modifiers, guard))
        case _ => None
      }
  }
  object SelintPipeRegRegConstPred extends Definition(
        "SelintPipeRegRegConstPred",
        "SELint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("sel__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.SelintPipeRegRegConstPred(dst, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object SelintPipeRegRegImmPred extends Definition(
        "SelintPipeRegRegImmPred",
        "SELint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("sel_64__RIR", "sel__RuIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.SelintPipeRegRegImmPred(dst, srcA, sb, pp, modifiers, guard))
        case _ => None
      }
  }
  object SelintPipeRegRegRegPred extends Definition(
        "SelintPipeRegRegRegPred",
        "SELint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pp", OperandKind.Predicate)
        ),
        Vec("sel_64__RRR", "sel_64__RUR", "sel__RRR_RRR", "sel__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pp)
            ) => Some(Instruction.SelintPipeRegRegRegPred(dst, srcA, srcB, pp, modifiers, guard))
        case _ => None
      }
  }
  object SelxluPipeRegRegConstPred extends Definition(
        "SelxluPipeRegRegConstPred",
        "SELxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Const SEL")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.SelxluPipeRegRegConstPred(dst, srcA, srcConst, pa, modifiers, guard))
        case _ => None
      }
  }
  object SelxluPipeRegRegImmPred extends Definition(
        "SelxluPipeRegRegImmPred",
        "SELxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("Imm SEL")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.SelxluPipeRegRegImmPred(dst, srcA, uImm, pa, modifiers, guard))
        case _ => None
      }
  }
  object SelxluPipeRegRegRegPred extends Definition(
        "SelxluPipeRegRegRegPred",
        "SELxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("pa", OperandKind.Predicate)
        ),
        Vec("SEL")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(pa)
            ) => Some(Instruction.SelxluPipeRegRegRegPred(dst, srcA, srcB, pa, modifiers, guard))
        case _ => None
      }
  }
  object SetcrsptrReg
      extends Definition("SetcrsptrReg", "SETCRSPTR", Vec(Slot("srcA", OperandKind.Register)), Vec("SETCRSPTR")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA)) => Some(Instruction.SetcrsptrReg(srcA, modifiers, guard))
        case _ => None
      }
  }
  object SetcrsptrbruPipeReg extends Definition(
        "SetcrsptrbruPipeReg",
        "SETCRSPTRbru_pipe",
        Vec(Slot("srcA", OperandKind.Register)),
        Vec("SETCRSPTR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA)) => Some(Instruction.SetcrsptrbruPipeReg(srcA, modifiers, guard))
        case _ => None
      }
  }
  object SetcrsptrmioPipeReg extends Definition(
        "SetcrsptrmioPipeReg",
        "SETCRSPTRmio_pipe",
        Vec(Slot("srcA", OperandKind.Register)),
        Vec("SETCRSPTR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA)) => Some(Instruction.SetcrsptrmioPipeReg(srcA, modifiers, guard))
        case _ => None
      }
  }
  object SetctaidReg
      extends Definition("SetctaidReg", "SETCTAID", Vec(Slot("srcA", OperandKind.Register)), Vec("setctaid_")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA)) => Some(Instruction.SetctaidReg(srcA, modifiers, guard))
        case _ => None
      }
  }
  object SetctaidmioPipeReg extends Definition(
        "SetctaidmioPipeReg",
        "SETCTAIDmio_pipe",
        Vec(Slot("srcA", OperandKind.Register)),
        Vec("setctaid_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA)) => Some(Instruction.SetctaidmioPipeReg(srcA, modifiers, guard))
        case _ => None
      }
  }
  object SetlmembaseReg extends Definition(
        "SetlmembaseReg",
        "SETLMEMBASE",
        Vec(Slot("srcA", OperandKind.Register)),
        Vec("SETLMEMBASE", "setlmembase_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA)) => Some(Instruction.SetlmembaseReg(srcA, modifiers, guard))
        case _ => None
      }
  }
  object SetlmembaseaguPipeReg extends Definition(
        "SetlmembaseaguPipeReg",
        "SETLMEMBASEagu_pipe",
        Vec(Slot("srcA", OperandKind.Register)),
        Vec("SETLMEMBASE")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA)) => Some(Instruction.SetlmembaseaguPipeReg(srcA, modifiers, guard))
        case _ => None
      }
  }
  object SetlmembasemioPipeReg extends Definition(
        "SetlmembasemioPipeReg",
        "SETLMEMBASEmio_pipe",
        Vec(Slot("srcA", OperandKind.Register)),
        Vec("SETLMEMBASE", "setlmembase_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Register(srcA)) => Some(Instruction.SetlmembasemioPipeReg(srcA, modifiers, guard))
        case _ => None
      }
  }
  object SgxtRegRegAddressImm extends Definition(
        "SgxtRegRegAddressImm",
        "SGXT",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("sgxt__RCxR_RCxR")
      ) {
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
  object SgxtRegRegConst extends Definition(
        "SgxtRegRegConst",
        "SGXT",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank)
        ),
        Vec("sgxt__RCR_RCR")
      ) {
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
  object SgxtRegRegImm extends Definition(
        "SgxtRegRegImm",
        "SGXT",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("sgxt__RuIR_RIR")
      ) {
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
  object SgxtRegRegReg extends Definition(
        "SgxtRegRegReg",
        "SGXT",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("sgxt__RRR_RRR", "sgxt__RUR_RUR")
      ) {
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
  object SgxtintPipeRegRegAddressImm extends Definition(
        "SgxtintPipeRegRegAddressImm",
        "SGXTint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("sgxt__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.SgxtintPipeRegRegAddressImm(dst, srcA, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object SgxtintPipeRegRegConst extends Definition(
        "SgxtintPipeRegRegConst",
        "SGXTint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank)
        ),
        Vec("sgxt__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb)
            ) => Some(Instruction.SgxtintPipeRegRegConst(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object SgxtintPipeRegRegImm extends Definition(
        "SgxtintPipeRegRegImm",
        "SGXTint_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("sgxt__RuIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.SgxtintPipeRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object SgxtintPipeRegRegReg extends Definition(
        "SgxtintPipeRegRegReg",
        "SGXTint_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("sgxt__RRR_RRR", "sgxt__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.SgxtintPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object ShfRegRegAddressImmReg extends Definition(
        "ShfRegRegAddressImmReg",
        "SHF",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("shf__RCxR_RCxR")
      ) {
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
  object ShfRegRegConstReg extends Definition(
        "ShfRegRegConstReg",
        "SHF",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("shf__RCR_RCR")
      ) {
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
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
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
  object ShfRegRegRegAddressImm extends Definition(
        "ShfRegRegRegAddressImm",
        "SHF",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Address),
          Slot("sc_offset", OperandKind.Immediate)
        ),
        Vec("shf__RRCx_RRCx")
      ) {
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
  object ShfRegRegRegConst extends Definition(
        "ShfRegRegRegConst",
        "SHF",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.ConstantBank)
        ),
        Vec("shf__RRC_RRC")
      ) {
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
  object ShfRegRegRegImm extends Definition(
        "ShfRegRegRegImm",
        "SHF",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate)
        ),
        Vec("shf__RRuI_RRI")
      ) {
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
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
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
  object ShflPredRegRegImmImm extends Definition(
        "ShflPredRegRegImmImm",
        "SHFL",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm5", OperandKind.Immediate),
          Slot("uImm13", OperandKind.Immediate)
        ),
        Vec("c b SHFL", "c_b_SHFL", "shfl__RII")
      ) {
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
  object ShflPredRegRegImmReg extends Definition(
        "ShflPredRegRegImmReg",
        "SHFL",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm5", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("b SHFL", "b_SHFL", "shfl__RIR")
      ) {
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
  object ShflPredRegRegRegImm extends Definition(
        "ShflPredRegRegRegImm",
        "SHFL",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("uImm13", OperandKind.Immediate)
        ),
        Vec("c SHFL", "c_SHFL", "shfl__RRI")
      ) {
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
  object ShflPredRegRegRegReg extends Definition(
        "ShflPredRegRegRegReg",
        "SHFL",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("SHFL", "shfl__RRR")
      ) {
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
  object ShflaguPipePredRegRegImmImm extends Definition(
        "ShflaguPipePredRegRegImmImm",
        "SHFLagu_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm5", OperandKind.Immediate),
          Slot("uImm13", OperandKind.Immediate)
        ),
        Vec("c b SHFL")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm5),
              InstructionOperand.Immediate(uImm13)
            ) => Some(Instruction.ShflaguPipePredRegRegImmImm(dstPredicate, dst, srcA, uImm5, uImm13, modifiers, guard))
        case _ => None
      }
  }
  object ShflaguPipePredRegRegImmReg extends Definition(
        "ShflaguPipePredRegRegImmReg",
        "SHFLagu_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm5", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("b SHFL")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm5),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.ShflaguPipePredRegRegImmReg(dstPredicate, dst, srcA, uImm5, srcC, modifiers, guard))
        case _ => None
      }
  }
  object ShflaguPipePredRegRegRegImm extends Definition(
        "ShflaguPipePredRegRegRegImm",
        "SHFLagu_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("uImm13", OperandKind.Immediate)
        ),
        Vec("c SHFL")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(uImm13)
            ) => Some(Instruction.ShflaguPipePredRegRegRegImm(dstPredicate, dst, srcA, srcB, uImm13, modifiers, guard))
        case _ => None
      }
  }
  object ShflaguPipePredRegRegRegReg extends Definition(
        "ShflaguPipePredRegRegRegReg",
        "SHFLagu_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("SHFL")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.ShflaguPipePredRegRegRegReg(dstPredicate, dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object ShflmioPipePredRegRegImmImm extends Definition(
        "ShflmioPipePredRegRegImmImm",
        "SHFLmio_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm5", OperandKind.Immediate),
          Slot("uImm13", OperandKind.Immediate)
        ),
        Vec("c_b_SHFL", "shfl__RII")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm5),
              InstructionOperand.Immediate(uImm13)
            ) => Some(Instruction.ShflmioPipePredRegRegImmImm(dstPredicate, dst, srcA, uImm5, uImm13, modifiers, guard))
        case _ => None
      }
  }
  object ShflmioPipePredRegRegImmReg extends Definition(
        "ShflmioPipePredRegRegImmReg",
        "SHFLmio_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm5", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("b_SHFL", "shfl__RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm5),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.ShflmioPipePredRegRegImmReg(dstPredicate, dst, srcA, uImm5, srcC, modifiers, guard))
        case _ => None
      }
  }
  object ShflmioPipePredRegRegRegImm extends Definition(
        "ShflmioPipePredRegRegRegImm",
        "SHFLmio_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("uImm13", OperandKind.Immediate)
        ),
        Vec("c_SHFL", "shfl__RRI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(uImm13)
            ) => Some(Instruction.ShflmioPipePredRegRegRegImm(dstPredicate, dst, srcA, srcB, uImm13, modifiers, guard))
        case _ => None
      }
  }
  object ShflmioPipePredRegRegRegReg extends Definition(
        "ShflmioPipePredRegRegRegReg",
        "SHFLmio_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("SHFL", "shfl__RRR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.ShflmioPipePredRegRegRegReg(dstPredicate, dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object ShffmaxPipeRegRegImmReg extends Definition(
        "ShffmaxPipeRegRegImmReg",
        "SHFfmaX_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("SHF_L imm", "SHF_R imm")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.ShffmaxPipeRegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object ShffmaxPipeRegRegRegReg extends Definition(
        "ShffmaxPipeRegRegRegReg",
        "SHFfmaX_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("SHF_L reg", "SHF_R reg")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.ShffmaxPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object ShffxuPipeRegRegImmReg extends Definition(
        "ShffxuPipeRegRegImmReg",
        "SHFfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("SHF_L_imm", "SHF_R_imm")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.ShffxuPipeRegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object ShffxuPipeRegRegRegReg extends Definition(
        "ShffxuPipeRegRegRegReg",
        "SHFfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("SHF_L_reg", "SHF_R_reg")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.ShffxuPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object ShfintPipeRegRegAddressImmReg extends Definition(
        "ShfintPipeRegRegAddressImmReg",
        "SHFint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("shf__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.ShfintPipeRegRegAddressImmReg(dst, srcA, srcB, sb_offset, srcC, modifiers, guard))
        case _ => None
      }
  }
  object ShfintPipeRegRegConstReg extends Definition(
        "ShfintPipeRegRegConstReg",
        "SHFint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("shf__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.ShfintPipeRegRegConstReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object ShfintPipeRegRegImmReg extends Definition(
        "ShfintPipeRegRegImmReg",
        "SHFint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("shf__RuIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.ShfintPipeRegRegImmReg(dst, srcA, sb, srcC, modifiers, guard))
        case _ => None
      }
  }
  object ShfintPipeRegRegRegAddressImm extends Definition(
        "ShfintPipeRegRegRegAddressImm",
        "SHFint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Address),
          Slot("sc_offset", OperandKind.Immediate)
        ),
        Vec("shf__RRCx_RRCx")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Address(srcC),
              InstructionOperand.Immediate(sc_offset)
            ) => Some(Instruction.ShfintPipeRegRegRegAddressImm(dst, srcA, srcB, srcC, sc_offset, modifiers, guard))
        case _ => None
      }
  }
  object ShfintPipeRegRegRegConst extends Definition(
        "ShfintPipeRegRegRegConst",
        "SHFint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.ConstantBank)
        ),
        Vec("shf__RRC_RRC")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(sc)
            ) => Some(Instruction.ShfintPipeRegRegRegConst(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object ShfintPipeRegRegRegImm extends Definition(
        "ShfintPipeRegRegRegImm",
        "SHFint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sc", OperandKind.Immediate)
        ),
        Vec("shf__RRuI_RRI")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sc)
            ) => Some(Instruction.ShfintPipeRegRegRegImm(dst, srcA, srcB, sc, modifiers, guard))
        case _ => None
      }
  }
  object ShfintPipeRegRegRegReg extends Definition(
        "ShfintPipeRegRegRegReg",
        "SHFint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("shf__RRR_RRR", "shf__RRU_RRU", "shf__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.ShfintPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object ShfxluPipeRegRegImmReg extends Definition(
        "ShfxluPipeRegRegImmReg",
        "SHFxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("SHF_L imm", "SHF_R imm")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.ShfxluPipeRegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object ShfxluPipeRegRegRegReg extends Definition(
        "ShfxluPipeRegRegRegReg",
        "SHFxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("SHF_L reg", "SHF_R reg")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.ShfxluPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object ShintImmImm extends Definition(
        "ShintImmImm",
        "SHINT",
        Vec(Slot("uImmMsb", OperandKind.Immediate), Slot("uImmLsb", OperandKind.Immediate)),
        Vec("SHINT")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(uImmMsb), InstructionOperand.Immediate(uImmLsb)) =>
          Some(Instruction.ShintImmImm(uImmMsb, uImmLsb, modifiers, guard))
        case _ => None
      }
  }
  object ShintImmImmImmImmImmImmImmImmImmImmImmImmImmImm extends Definition(
        "ShintImmImmImmImmImmImmImmImmImmImmImmImmImmImm",
        "SHINT",
        Vec(
          Slot("uImmA", OperandKind.Immediate),
          Slot("uImmB", OperandKind.Immediate),
          Slot("uImmC", OperandKind.Immediate),
          Slot("uImmD", OperandKind.Immediate),
          Slot("uImmE", OperandKind.Immediate),
          Slot("uImmF", OperandKind.Immediate),
          Slot("uImmG", OperandKind.Immediate),
          Slot("uImmH", OperandKind.Immediate),
          Slot("uImmI", OperandKind.Immediate),
          Slot("uImmJ", OperandKind.Immediate),
          Slot("uImmK", OperandKind.Immediate),
          Slot("uImmL", OperandKind.Immediate),
          Slot("uImmM", OperandKind.Immediate),
          Slot("uImmN", OperandKind.Immediate)
        ),
        Vec("SHINT nibbles")
      ) {
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
  object ShintfePipeImmImm extends Definition(
        "ShintfePipeImmImm",
        "SHINTfe_pipe",
        Vec(Slot("uImmMsb", OperandKind.Immediate), Slot("uImmLsb", OperandKind.Immediate)),
        Vec("SHINT")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(uImmMsb), InstructionOperand.Immediate(uImmLsb)) =>
          Some(Instruction.ShintfePipeImmImm(uImmMsb, uImmLsb, modifiers, guard))
        case _ => None
      }
  }
  object ShintfePipeImmImmImmImmImmImmImmImmImmImmImmImmImmImm extends Definition(
        "ShintfePipeImmImmImmImmImmImmImmImmImmImmImmImmImmImm",
        "SHINTfe_pipe",
        Vec(
          Slot("uImmA", OperandKind.Immediate),
          Slot("uImmB", OperandKind.Immediate),
          Slot("uImmC", OperandKind.Immediate),
          Slot("uImmD", OperandKind.Immediate),
          Slot("uImmE", OperandKind.Immediate),
          Slot("uImmF", OperandKind.Immediate),
          Slot("uImmG", OperandKind.Immediate),
          Slot("uImmH", OperandKind.Immediate),
          Slot("uImmI", OperandKind.Immediate),
          Slot("uImmJ", OperandKind.Immediate),
          Slot("uImmK", OperandKind.Immediate),
          Slot("uImmL", OperandKind.Immediate),
          Slot("uImmM", OperandKind.Immediate),
          Slot("uImmN", OperandKind.Immediate)
        ),
        Vec("SHINT nibbles")
      ) {
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
            ) => Some(Instruction.ShintfePipeImmImmImmImmImmImmImmImmImmImmImmImmImmImm(
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
  object ShlRegImmReg extends Definition(
        "ShlRegImmReg",
        "SHL",
        Vec(Slot("dst", OperandKind.Register), Slot("sa", OperandKind.Immediate), Slot("srcB", OperandKind.Register)),
        Vec("shl_imm_")
      ) {
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
  object ShlRegRegAddressImm extends Definition(
        "ShlRegRegAddressImm",
        "SHL",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("shl__RCxR_RCxR")
      ) {
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
  object ShlRegRegConst extends Definition(
        "ShlRegRegConst",
        "SHL",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank)
        ),
        Vec("Const SHL", "Const_SHL", "shl__RCR_RCR")
      ) {
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
  object ShlRegRegImm extends Definition(
        "ShlRegRegImm",
        "SHL",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("Imm SHL", "Imm_SHL", "shl__RuIR_RIR")
      ) {
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
  object ShlRegRegReg extends Definition(
        "ShlRegRegReg",
        "SHL",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("SHL", "shl__RRR_RRR", "shl__RUR_RUR")
      ) {
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
  object ShlfxuPipeRegRegConst extends Definition(
        "ShlfxuPipeRegRegConst",
        "SHLfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const_SHL")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.ShlfxuPipeRegRegConst(dst, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object ShlfxuPipeRegRegImm extends Definition(
        "ShlfxuPipeRegRegImm",
        "SHLfxu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("Imm_SHL")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.ShlfxuPipeRegRegImm(dst, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object ShlfxuPipeRegRegReg extends Definition(
        "ShlfxuPipeRegRegReg",
        "SHLfxu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("SHL")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.ShlfxuPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object ShlintPipeRegImmReg extends Definition(
        "ShlintPipeRegImmReg",
        "SHLint_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("sa", OperandKind.Immediate), Slot("srcB", OperandKind.Register)),
        Vec("shl_imm_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Immediate(sa),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.ShlintPipeRegImmReg(dst, sa, srcB, modifiers, guard))
        case _ => None
      }
  }
  object ShlintPipeRegRegAddressImm extends Definition(
        "ShlintPipeRegRegAddressImm",
        "SHLint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("shl__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.ShlintPipeRegRegAddressImm(dst, srcA, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object ShlintPipeRegRegConst extends Definition(
        "ShlintPipeRegRegConst",
        "SHLint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank)
        ),
        Vec("shl__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb)
            ) => Some(Instruction.ShlintPipeRegRegConst(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object ShlintPipeRegRegImm extends Definition(
        "ShlintPipeRegRegImm",
        "SHLint_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("shl__RuIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.ShlintPipeRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object ShlintPipeRegRegReg extends Definition(
        "ShlintPipeRegRegReg",
        "SHLint_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("shl__RRR_RRR", "shl__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.ShlintPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object ShlxluPipeRegRegConst extends Definition(
        "ShlxluPipeRegRegConst",
        "SHLxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const SHL")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.ShlxluPipeRegRegConst(dst, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object ShlxluPipeRegRegImm extends Definition(
        "ShlxluPipeRegRegImm",
        "SHLxlu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("Imm SHL")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.ShlxluPipeRegRegImm(dst, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object ShlxluPipeRegRegReg extends Definition(
        "ShlxluPipeRegRegReg",
        "SHLxlu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("SHL")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.ShlxluPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object ShrRegRegAddressImm extends Definition(
        "ShrRegRegAddressImm",
        "SHR",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("shr__RCxR_RCxR")
      ) {
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
  object ShrRegRegConst extends Definition(
        "ShrRegRegConst",
        "SHR",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank)
        ),
        Vec("Const SHR", "Const_SHR", "shr__RCR_RCR")
      ) {
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
  object ShrRegRegImm extends Definition(
        "ShrRegRegImm",
        "SHR",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("Imm SHR", "Imm_SHR", "shr__RIR_RIR")
      ) {
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
  object ShrRegRegReg extends Definition(
        "ShrRegRegReg",
        "SHR",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("SHR", "shr__RRR_RRR", "shr__RUR_RUR")
      ) {
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
  object ShrfxuPipeRegRegConst extends Definition(
        "ShrfxuPipeRegRegConst",
        "SHRfxu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const_SHR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.ShrfxuPipeRegRegConst(dst, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object ShrfxuPipeRegRegImm extends Definition(
        "ShrfxuPipeRegRegImm",
        "SHRfxu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("Imm_SHR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.ShrfxuPipeRegRegImm(dst, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object ShrfxuPipeRegRegReg extends Definition(
        "ShrfxuPipeRegRegReg",
        "SHRfxu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("SHR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.ShrfxuPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object ShrintPipeRegRegAddressImm extends Definition(
        "ShrintPipeRegRegAddressImm",
        "SHRint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Address),
          Slot("sb_offset", OperandKind.Immediate)
        ),
        Vec("shr__RCxR_RCxR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Address(srcB),
              InstructionOperand.Immediate(sb_offset)
            ) => Some(Instruction.ShrintPipeRegRegAddressImm(dst, srcA, srcB, sb_offset, modifiers, guard))
        case _ => None
      }
  }
  object ShrintPipeRegRegConst extends Definition(
        "ShrintPipeRegRegConst",
        "SHRint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("sb", OperandKind.ConstantBank)
        ),
        Vec("shr__RCR_RCR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(sb)
            ) => Some(Instruction.ShrintPipeRegRegConst(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object ShrintPipeRegRegImm extends Definition(
        "ShrintPipeRegRegImm",
        "SHRint_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("sb", OperandKind.Immediate)),
        Vec("shr__RIR_RIR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(sb)
            ) => Some(Instruction.ShrintPipeRegRegImm(dst, srcA, sb, modifiers, guard))
        case _ => None
      }
  }
  object ShrintPipeRegRegReg extends Definition(
        "ShrintPipeRegRegReg",
        "SHRint_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("shr__RRR_RRR", "shr__RUR_RUR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.ShrintPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object ShrxluPipeRegRegConst extends Definition(
        "ShrxluPipeRegRegConst",
        "SHRxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const SHR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.ShrxluPipeRegRegConst(dst, srcA, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object ShrxluPipeRegRegImm extends Definition(
        "ShrxluPipeRegRegImm",
        "SHRxlu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("uImm", OperandKind.Immediate)),
        Vec("Imm SHR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm)
            ) => Some(Instruction.ShrxluPipeRegRegImm(dst, srcA, uImm, modifiers, guard))
        case _ => None
      }
  }
  object ShrxluPipeRegRegReg extends Definition(
        "ShrxluPipeRegRegReg",
        "SHRxlu_pipe",
        Vec(Slot("dst", OperandKind.Register), Slot("srcA", OperandKind.Register), Slot("srcB", OperandKind.Register)),
        Vec("SHR")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.ShrxluPipeRegRegReg(dst, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object SpmetadataRegRegRegImmImmImm extends Definition(
        "SpmetadataRegRegRegImmImmImm",
        "SPMETADATA",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("vecidx", OperandKind.Immediate),
          Slot("mask", OperandKind.Immediate),
          Slot("destidx", OperandKind.Immediate)
        ),
        Vec("spmetadata_")
      ) {
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
  object SpmetadataintPipeRegRegRegImmImmImm extends Definition(
        "SpmetadataintPipeRegRegRegImmImmImm",
        "SPMETADATAint_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("vecidx", OperandKind.Immediate),
          Slot("mask", OperandKind.Immediate),
          Slot("destidx", OperandKind.Immediate)
        ),
        Vec("spmetadata_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Immediate(vecidx),
              InstructionOperand.Immediate(mask),
              InstructionOperand.Immediate(destidx)
            ) => Some(Instruction.SpmetadataintPipeRegRegRegImmImmImm(
            dst,
            srcB,
            srcC,
            vecidx,
            mask,
            destidx,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object SsyConst
      extends Definition("SsyConst", "SSY", Vec(Slot("srcConst", OperandKind.ConstantBank)), Vec("SSY c", "SSY_c")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(srcConst)) => Some(Instruction.SsyConst(srcConst, modifiers, guard))
        case _ => None
      }
  }
  object SsyImm extends Definition("SsyImm", "SSY", Vec(Slot("sImm", OperandKind.Immediate)), Vec("SSY")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sImm)) => Some(Instruction.SsyImm(sImm, modifiers, guard))
        case _ => None
      }
  }
  object SsybruPipeConst extends Definition(
        "SsybruPipeConst",
        "SSYbru_pipe",
        Vec(Slot("srcConst", OperandKind.ConstantBank)),
        Vec("SSY c", "SSY_c")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.ConstantBank(srcConst)) =>
          Some(Instruction.SsybruPipeConst(srcConst, modifiers, guard))
        case _ => None
      }
  }
  object SsybruPipeImm
      extends Definition("SsybruPipeImm", "SSYbru_pipe", Vec(Slot("sImm", OperandKind.Immediate)), Vec("SSY")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(sImm)) => Some(Instruction.SsybruPipeImm(sImm, modifiers, guard))
        case _ => None
      }
  }
  object StAddressReg extends Definition(
        "StAddressReg",
        "ST",
        Vec(Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register)),
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
  object StAddressRegPred extends Definition(
        "StAddressRegPred",
        "ST",
        Vec(Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register), Slot("plg", OperandKind.Predicate)),
        Vec("I_ST", "ST")
      ) {
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
  object StasAddressReg extends Definition(
        "StasAddressReg",
        "STAS",
        Vec(Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register)),
        Vec("stas_64__Ra64", "stas_64__RaRZ", "stas__Ra32")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA), InstructionOperand.Register(srcB)) =>
          Some(Instruction.StasAddressReg(srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object StasmioPipeAddressReg extends Definition(
        "StasmioPipeAddressReg",
        "STASmio_pipe",
        Vec(Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register)),
        Vec("stas_64__Ra64", "stas_64__RaRZ", "stas__Ra32")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA), InstructionOperand.Register(srcB)) =>
          Some(Instruction.StasmioPipeAddressReg(srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object StgAddressReg extends Definition(
        "StgAddressReg",
        "STG",
        Vec(Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register)),
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
        Vec(Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register), Slot("rb2", OperandKind.Register)),
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
        Vec(
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("rb2", OperandKind.Register),
          Slot("word_mask", OperandKind.Immediate)
        ),
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
  object StgmioPipeAddressReg extends Definition(
        "StgmioPipeAddressReg",
        "STGmio_pipe",
        Vec(Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register)),
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
          Some(Instruction.StgmioPipeAddressReg(srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object StgmioPipeAddressRegReg extends Definition(
        "StgmioPipeAddressRegReg",
        "STGmio_pipe",
        Vec(Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register), Slot("rb2", OperandKind.Register)),
        Vec("stg_256_memdesc__Ra64", "stg_256_uniform__Ra32", "stg_256_uniform__Ra64", "stg_256_uniform__RaRZ")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(rb2)
            ) => Some(Instruction.StgmioPipeAddressRegReg(srcA, srcB, rb2, modifiers, guard))
        case _ => None
      }
  }
  object StgmioPipeAddressRegRegImm extends Definition(
        "StgmioPipeAddressRegRegImm",
        "STGmio_pipe",
        Vec(
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("rb2", OperandKind.Register),
          Slot("word_mask", OperandKind.Immediate)
        ),
        Vec("stg_256_memdesc__Ra64", "stg_256_uniform__Ra32", "stg_256_uniform__Ra64", "stg_256_uniform__RaRZ")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(rb2),
              InstructionOperand.Immediate(word_mask)
            ) => Some(Instruction.StgmioPipeAddressRegRegImm(srcA, srcB, rb2, word_mask, modifiers, guard))
        case _ => None
      }
  }
  object StlAddressReg extends Definition(
        "StlAddressReg",
        "STL",
        Vec(Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register)),
        Vec("I STL", "I_STL", "STL", "stl__sImmOffset", "stl__uImmOffset", "stl_memdesc_", "stl_uniform_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA), InstructionOperand.Register(srcB)) =>
          Some(Instruction.StlAddressReg(srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object StlaguPipeAddressReg extends Definition(
        "StlaguPipeAddressReg",
        "STLagu_pipe",
        Vec(Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register)),
        Vec("I STL", "STL")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA), InstructionOperand.Register(srcB)) =>
          Some(Instruction.StlaguPipeAddressReg(srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object StlmioPipeAddressReg extends Definition(
        "StlmioPipeAddressReg",
        "STLmio_pipe",
        Vec(Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register)),
        Vec("I_STL", "STL", "stl__sImmOffset", "stl__uImmOffset", "stl_memdesc_", "stl_uniform_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA), InstructionOperand.Register(srcB)) =>
          Some(Instruction.StlmioPipeAddressReg(srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object StpImm extends Definition("StpImm", "STP", Vec(Slot("uImm", OperandKind.Immediate)), Vec("STP")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(uImm)) => Some(Instruction.StpImm(uImm, modifiers, guard))
        case _ => None
      }
  }
  object StpfePipeImm
      extends Definition("StpfePipeImm", "STPfe_pipe", Vec(Slot("uImm", OperandKind.Immediate)), Vec("STP")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(uImm)) => Some(Instruction.StpfePipeImm(uImm, modifiers, guard))
        case _ => None
      }
  }
  object StpmioPipeImm
      extends Definition("StpmioPipeImm", "STPmio_pipe", Vec(Slot("uImm", OperandKind.Immediate)), Vec("STP")) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Immediate(uImm)) => Some(Instruction.StpmioPipeImm(uImm, modifiers, guard))
        case _ => None
      }
  }
  object StsAddressReg extends Definition(
        "StsAddressReg",
        "STS",
        Vec(Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register)),
        Vec("I STS", "I_STS", "STS", "sts__sImmOffset", "sts__uImmOffset", "sts_uniform_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA), InstructionOperand.Register(srcB)) =>
          Some(Instruction.StsAddressReg(srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object StsculPredAddressReg extends Definition(
        "StsculPredAddressReg",
        "STSCUL",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register)
        ),
        Vec("I STSCUL", "STSCUL")
      ) {
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
  object StsculaguPipePredAddressReg extends Definition(
        "StsculaguPipePredAddressReg",
        "STSCULagu_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register)
        ),
        Vec("I STSCUL", "STSCUL")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB)
            ) => Some(Instruction.StsculaguPipePredAddressReg(dstPredicate, srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object StsmAddressReg extends Definition(
        "StsmAddressReg",
        "STSM",
        Vec(Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register)),
        Vec("stsm__UR_sI_R", "stsm__sImmOffset")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA), InstructionOperand.Register(srcB)) =>
          Some(Instruction.StsmAddressReg(srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object StsmmioPipeAddressReg extends Definition(
        "StsmmioPipeAddressReg",
        "STSMmio_pipe",
        Vec(Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register)),
        Vec("stsm__UR_sI_R", "stsm__sImmOffset")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA), InstructionOperand.Register(srcB)) =>
          Some(Instruction.StsmmioPipeAddressReg(srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object StsaguPipeAddressReg extends Definition(
        "StsaguPipeAddressReg",
        "STSagu_pipe",
        Vec(Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register)),
        Vec("I STS", "STS")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA), InstructionOperand.Register(srcB)) =>
          Some(Instruction.StsaguPipeAddressReg(srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object StsmioPipeAddressReg extends Definition(
        "StsmioPipeAddressReg",
        "STSmio_pipe",
        Vec(Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register)),
        Vec("I_STS", "STS", "sts__sImmOffset", "sts__uImmOffset", "sts_uniform_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA), InstructionOperand.Register(srcB)) =>
          Some(Instruction.StsmioPipeAddressReg(srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object SttAddressReg extends Definition(
        "SttAddressReg",
        "STT",
        Vec(Slot("srcC", OperandKind.Address), Slot("srcB", OperandKind.Register)),
        Vec("stt_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcC), InstructionOperand.Register(srcB)) =>
          Some(Instruction.SttAddressReg(srcC, srcB, modifiers, guard))
        case _ => None
      }
  }
  object SttmAddressReg extends Definition(
        "SttmAddressReg",
        "STTM",
        Vec(Slot("srcC", OperandKind.Address), Slot("srcB", OperandKind.Register)),
        Vec("sttm_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcC), InstructionOperand.Register(srcB)) =>
          Some(Instruction.SttmAddressReg(srcC, srcB, modifiers, guard))
        case _ => None
      }
  }
  object SttmudpPipeAddressReg extends Definition(
        "SttmudpPipeAddressReg",
        "STTMudp_pipe",
        Vec(Slot("srcC", OperandKind.Address), Slot("srcB", OperandKind.Register)),
        Vec("sttm_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcC), InstructionOperand.Register(srcB)) =>
          Some(Instruction.SttmudpPipeAddressReg(srcC, srcB, modifiers, guard))
        case _ => None
      }
  }
  object SttudpPipeAddressReg extends Definition(
        "SttudpPipeAddressReg",
        "STTudp_pipe",
        Vec(Slot("srcC", OperandKind.Address), Slot("srcB", OperandKind.Register)),
        Vec("stt_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcC), InstructionOperand.Register(srcB)) =>
          Some(Instruction.SttudpPipeAddressReg(srcC, srcB, modifiers, guard))
        case _ => None
      }
  }
  object StaguPipeAddressReg extends Definition(
        "StaguPipeAddressReg",
        "STagu_pipe",
        Vec(Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register)),
        Vec("I ST", "ST")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(InstructionOperand.Address(srcA), InstructionOperand.Register(srcB)) =>
          Some(Instruction.StaguPipeAddressReg(srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object StmioPipeAddressReg extends Definition(
        "StmioPipeAddressReg",
        "STmio_pipe",
        Vec(Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register)),
        Vec(
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
          Some(Instruction.StmioPipeAddressReg(srcA, srcB, modifiers, guard))
        case _ => None
      }
  }
  object StmioPipeAddressRegPred extends Definition(
        "StmioPipeAddressRegPred",
        "STmio_pipe",
        Vec(Slot("srcA", OperandKind.Address), Slot("srcB", OperandKind.Register), Slot("plg", OperandKind.Predicate)),
        Vec("I_ST", "ST")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Predicate(plg)
            ) => Some(Instruction.StmioPipeAddressRegPred(srcA, srcB, plg, modifiers, guard))
        case _ => None
      }
  }
  object SuatomPredRegAddressRegImm extends Definition(
        "SuatomPredRegAddressRegImm",
        "SUATOM",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("suid", OperandKind.Immediate)
        ),
        Vec("SUATOM_D_CAS_IMM")
      ) {
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
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("tsPtrIdx", OperandKind.Immediate)
        ),
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
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("hdrtblbase6", OperandKind.Immediate),
          Slot("tid8", OperandKind.Immediate)
        ),
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
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
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
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
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
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register)
        ),
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
  object SuatomPredRegAddressRegRegRegImm extends Definition(
        "SuatomPredRegAddressRegRegRegImm",
        "SUATOM",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("suatom_cas_urc_", "suatom_urc_")
      ) {
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
  object SuatomPredRegAddressRegRegRegReg extends Definition(
        "SuatomPredRegAddressRegRegRegReg",
        "SUATOM",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC1", OperandKind.Register),
          Slot("srcC2", OperandKind.Register),
          Slot("uRe", OperandKind.Register)
        ),
        Vec("suatom_cas_r_urc_", "suatom_r_urc_")
      ) {
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
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("suid", OperandKind.Immediate)
        ),
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
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("tsPtrIdx", OperandKind.Immediate)
        ),
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
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("hdrtblbase6", OperandKind.Immediate),
          Slot("tid8", OperandKind.Immediate)
        ),
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
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
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
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
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
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register)
        ),
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
  object SuatomRegAddressRegRegRegImm extends Definition(
        "SuatomRegAddressRegRegRegImm",
        "SUATOM",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("suatom_cas_urc_", "suatom_urc_")
      ) {
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
  object SuatomRegAddressRegRegRegReg extends Definition(
        "SuatomRegAddressRegRegRegReg",
        "SUATOM",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC1", OperandKind.Register),
          Slot("srcC2", OperandKind.Register),
          Slot("uRe", OperandKind.Register)
        ),
        Vec("suatom_cas_r_urc_", "suatom_r_urc_")
      ) {
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
  object SuatommioPipePredRegAddressRegImm extends Definition(
        "SuatommioPipePredRegAddressRegImm",
        "SUATOMmio_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("suid", OperandKind.Immediate)
        ),
        Vec("SUATOM_D_CAS_IMM")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(dstPredicate),
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(suid)
            ) =>
          Some(Instruction.SuatommioPipePredRegAddressRegImm(dstPredicate, dst, srcA, srcB, suid, modifiers, guard))
        case _ => None
      }
  }
  object SuatommioPipePredRegAddressRegImmImm extends Definition(
        "SuatommioPipePredRegAddressRegImmImm",
        "SUATOMmio_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("tsPtrIdx", OperandKind.Immediate)
        ),
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
            ) =>
          Some(Instruction.SuatommioPipePredRegAddressRegImmImm(pu, dst, srcA, srcB, bank5, tsPtrIdx, modifiers, guard))
        case _ => None
      }
  }
  object SuatommioPipePredRegAddressRegImmImmImm extends Definition(
        "SuatommioPipePredRegAddressRegImmImmImm",
        "SUATOMmio_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("hdrtblbase6", OperandKind.Immediate),
          Slot("tid8", OperandKind.Immediate)
        ),
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
            ) => Some(Instruction.SuatommioPipePredRegAddressRegImmImmImm(
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
  object SuatommioPipePredRegAddressRegReg extends Definition(
        "SuatommioPipePredRegAddressRegReg",
        "SUATOMmio_pipe",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
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
            ) =>
          Some(Instruction.SuatommioPipePredRegAddressRegReg(dstPredicate, dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object SuatommioPipePredRegAddressRegRegImm extends Definition(
        "SuatommioPipePredRegAddressRegRegImm",
        "SUATOMmio_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
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
            ) =>
          Some(Instruction.SuatommioPipePredRegAddressRegRegImm(pu, dst, srcA, srcB, srcC, tid8, modifiers, guard))
        case _ => None
      }
  }
  object SuatommioPipePredRegAddressRegRegReg extends Definition(
        "SuatommioPipePredRegAddressRegRegReg",
        "SUATOMmio_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register)
        ),
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
            ) =>
          Some(Instruction.SuatommioPipePredRegAddressRegRegReg(pu, dst, srcA, srcB, srcC, uRe, modifiers, guard))
        case _ => None
      }
  }
  object SuatommioPipePredRegAddressRegRegRegImm extends Definition(
        "SuatommioPipePredRegAddressRegRegRegImm",
        "SUATOMmio_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("suatom_cas_urc_", "suatom_urc_")
      ) {
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
            ) => Some(Instruction.SuatommioPipePredRegAddressRegRegRegImm(
            pu,
            dst,
            srcA,
            srcB,
            srcC,
            uRe,
            tid8,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object SuatommioPipePredRegAddressRegRegRegReg extends Definition(
        "SuatommioPipePredRegAddressRegRegRegReg",
        "SUATOMmio_pipe",
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC1", OperandKind.Register),
          Slot("srcC2", OperandKind.Register),
          Slot("uRe", OperandKind.Register)
        ),
        Vec("suatom_cas_r_urc_", "suatom_r_urc_")
      ) {
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
            ) => Some(Instruction.SuatommioPipePredRegAddressRegRegRegReg(
            pu,
            dst,
            srcA,
            srcB,
            srcC1,
            srcC2,
            uRe,
            modifiers,
            guard
          ))
        case _ => None
      }
  }
  object SuatommioPipeRegAddressRegImm extends Definition(
        "SuatommioPipeRegAddressRegImm",
        "SUATOMmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("suid", OperandKind.Immediate)
        ),
        Vec("SUATOM_D_CAS_IMM", "SUATOM_D_IMM", "SUATOM_P_CAS_IMM", "SUATOM_P_IMM")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(suid)
            ) => Some(Instruction.SuatommioPipeRegAddressRegImm(dst, srcA, srcB, suid, modifiers, guard))
        case _ => None
      }
  }
  object SuatommioPipeRegAddressRegImmImm extends Definition(
        "SuatommioPipeRegAddressRegImmImm",
        "SUATOMmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("tsPtrIdx", OperandKind.Immediate)
        ),
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
            ) => Some(Instruction.SuatommioPipeRegAddressRegImmImm(dst, srcA, srcB, bank5, tsPtrIdx, modifiers, guard))
        case _ => None
      }
  }
  object SuatommioPipeRegAddressRegImmImmImm extends Definition(
        "SuatommioPipeRegAddressRegImmImmImm",
        "SUATOMmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("bank5", OperandKind.Immediate),
          Slot("hdrtblbase6", OperandKind.Immediate),
          Slot("tid8", OperandKind.Immediate)
        ),
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
            ) => Some(Instruction.SuatommioPipeRegAddressRegImmImmImm(
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
  object SuatommioPipeRegAddressRegReg extends Definition(
        "SuatommioPipeRegAddressRegReg",
        "SUATOMmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
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
            ) => Some(Instruction.SuatommioPipeRegAddressRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object SuatommioPipeRegAddressRegRegImm extends Definition(
        "SuatommioPipeRegAddressRegRegImm",
        "SUATOMmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
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
            ) => Some(Instruction.SuatommioPipeRegAddressRegRegImm(dst, srcA, srcB, srcC, tid8, modifiers, guard))
        case _ => None
      }
  }
  object SuatommioPipeRegAddressRegRegReg extends Definition(
        "SuatommioPipeRegAddressRegRegReg",
        "SUATOMmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register)
        ),
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
            ) => Some(Instruction.SuatommioPipeRegAddressRegRegReg(dst, srcA, srcB, srcC, uRe, modifiers, guard))
        case _ => None
      }
  }
  object SuatommioPipeRegAddressRegRegRegImm extends Definition(
        "SuatommioPipeRegAddressRegRegRegImm",
        "SUATOMmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register),
          Slot("uRe", OperandKind.Register),
          Slot("tid8", OperandKind.Immediate)
        ),
        Vec("suatom_cas_urc_", "suatom_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC),
              InstructionOperand.Register(uRe),
              InstructionOperand.Immediate(tid8)
            ) =>
          Some(Instruction.SuatommioPipeRegAddressRegRegRegImm(dst, srcA, srcB, srcC, uRe, tid8, modifiers, guard))
        case _ => None
      }
  }
  object SuatommioPipeRegAddressRegRegRegReg extends Definition(
        "SuatommioPipeRegAddressRegRegRegReg",
        "SUATOMmio_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcB", OperandKind.Register),
          Slot("srcC1", OperandKind.Register),
          Slot("srcC2", OperandKind.Register),
          Slot("uRe", OperandKind.Register)
        ),
        Vec("suatom_cas_r_urc_", "suatom_r_urc_")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Address(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC1),
              InstructionOperand.Register(srcC2),
              InstructionOperand.Register(uRe)
            ) =>
          Some(Instruction.SuatommioPipeRegAddressRegRegRegReg(dst, srcA, srcB, srcC1, srcC2, uRe, modifiers, guard))
        case _ => None
      }
  }
  object SubfmPredRegRegConstReg extends Definition(
        "SubfmPredRegRegConstReg",
        "SUBFM",
        Vec(
          Slot("pcl", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Const SUBFM")
      ) {
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
  object SubfmPredRegRegImmReg extends Definition(
        "SubfmPredRegRegImmReg",
        "SUBFM",
        Vec(
          Slot("pcl", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm SUBFM")
      ) {
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
  object SubfmPredRegRegRegConst extends Definition(
        "SubfmPredRegRegRegConst",
        "SUBFM",
        Vec(
          Slot("pcl", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const1 SUBFM")
      ) {
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
  object SubfmPredRegRegRegReg extends Definition(
        "SubfmPredRegRegRegReg",
        "SUBFM",
        Vec(
          Slot("pcl", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("SUBFM")
      ) {
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
  object SubfmxluPipePredRegRegConstReg extends Definition(
        "SubfmxluPipePredRegRegConstReg",
        "SUBFMxlu_pipe",
        Vec(
          Slot("pcl", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Const SUBFM")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pcl),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.SubfmxluPipePredRegRegConstReg(pcl, dst, srcA, srcConst, srcC, modifiers, guard))
        case _ => None
      }
  }
  object SubfmxluPipePredRegRegImmReg extends Definition(
        "SubfmxluPipePredRegRegImmReg",
        "SUBFMxlu_pipe",
        Vec(
          Slot("pcl", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm SUBFM")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pcl),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.SubfmxluPipePredRegRegImmReg(pcl, dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object SubfmxluPipePredRegRegRegConst extends Definition(
        "SubfmxluPipePredRegRegRegConst",
        "SUBFMxlu_pipe",
        Vec(
          Slot("pcl", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const1 SUBFM")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pcl),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.SubfmxluPipePredRegRegRegConst(pcl, dst, srcA, srcB, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object SubfmxluPipePredRegRegRegReg extends Definition(
        "SubfmxluPipePredRegRegRegReg",
        "SUBFMxlu_pipe",
        Vec(
          Slot("pcl", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("SUBFM")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pcl),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.SubfmxluPipePredRegRegRegReg(pcl, dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object SuclampPredRegRegConstImm extends Definition(
        "SuclampPredRegRegConstImm",
        "SUCLAMP",
        Vec(
          Slot("pcl", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("sImm", OperandKind.Immediate)
        ),
        Vec("Const SUCLAMP")
      ) {
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
  object SuclampPredRegRegImmImm extends Definition(
        "SuclampPredRegRegImmImm",
        "SUCLAMP",
        Vec(
          Slot("pcl", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("sImm", OperandKind.Immediate)
        ),
        Vec("Imm SUCLAMP")
      ) {
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
  object SuclampPredRegRegRegImm extends Definition(
        "SuclampPredRegRegRegImm",
        "SUCLAMP",
        Vec(
          Slot("pcl", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sImm", OperandKind.Immediate)
        ),
        Vec("SUCLAMP")
      ) {
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
  object SuclampxluPipePredRegRegConstImm extends Definition(
        "SuclampxluPipePredRegRegConstImm",
        "SUCLAMPxlu_pipe",
        Vec(
          Slot("pcl", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("sImm", OperandKind.Immediate)
        ),
        Vec("Const SUCLAMP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pcl),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Immediate(sImm)
            ) => Some(Instruction.SuclampxluPipePredRegRegConstImm(pcl, dst, srcA, srcConst, sImm, modifiers, guard))
        case _ => None
      }
  }
  object SuclampxluPipePredRegRegImmImm extends Definition(
        "SuclampxluPipePredRegRegImmImm",
        "SUCLAMPxlu_pipe",
        Vec(
          Slot("pcl", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("sImm", OperandKind.Immediate)
        ),
        Vec("Imm SUCLAMP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pcl),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Immediate(sImm)
            ) => Some(Instruction.SuclampxluPipePredRegRegImmImm(pcl, dst, srcA, uImm, sImm, modifiers, guard))
        case _ => None
      }
  }
  object SuclampxluPipePredRegRegRegImm extends Definition(
        "SuclampxluPipePredRegRegRegImm",
        "SUCLAMPxlu_pipe",
        Vec(
          Slot("pcl", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("sImm", OperandKind.Immediate)
        ),
        Vec("SUCLAMP")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Predicate(pcl),
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Immediate(sImm)
            ) => Some(Instruction.SuclampxluPipePredRegRegRegImm(pcl, dst, srcA, srcB, sImm, modifiers, guard))
        case _ => None
      }
  }
  object SueauRegRegConstReg extends Definition(
        "SueauRegRegConstReg",
        "SUEAU",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Const SUEAU")
      ) {
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
  object SueauRegRegImmReg extends Definition(
        "SueauRegRegImmReg",
        "SUEAU",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm SUEAU")
      ) {
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
  object SueauRegRegRegConst extends Definition(
        "SueauRegRegRegConst",
        "SUEAU",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const1 SUEAU")
      ) {
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
  object SueauRegRegRegReg extends Definition(
        "SueauRegRegRegReg",
        "SUEAU",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("SUEAU")
      ) {
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
  object SueauxluPipeRegRegConstReg extends Definition(
        "SueauxluPipeRegRegConstReg",
        "SUEAUxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Const SUEAU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.ConstantBank(srcConst),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.SueauxluPipeRegRegConstReg(dst, srcA, srcConst, srcC, modifiers, guard))
        case _ => None
      }
  }
  object SueauxluPipeRegRegImmReg extends Definition(
        "SueauxluPipeRegRegImmReg",
        "SUEAUxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("uImm", OperandKind.Immediate),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("Imm SUEAU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Immediate(uImm),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.SueauxluPipeRegRegImmReg(dst, srcA, uImm, srcC, modifiers, guard))
        case _ => None
      }
  }
  object SueauxluPipeRegRegRegConst extends Definition(
        "SueauxluPipeRegRegRegConst",
        "SUEAUxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcConst", OperandKind.ConstantBank)
        ),
        Vec("Const1 SUEAU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.ConstantBank(srcConst)
            ) => Some(Instruction.SueauxluPipeRegRegRegConst(dst, srcA, srcB, srcConst, modifiers, guard))
        case _ => None
      }
  }
  object SueauxluPipeRegRegRegReg extends Definition(
        "SueauxluPipeRegRegRegReg",
        "SUEAUxlu_pipe",
        Vec(
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Register),
          Slot("srcB", OperandKind.Register),
          Slot("srcC", OperandKind.Register)
        ),
        Vec("SUEAU")
      ) {
    def decode(modifiers: Vec[Modifier], operands: Vec[Operand], guard: Option[Guard]): Option[Instruction] =
      operands match {
        case Vec(
              InstructionOperand.Register(dst),
              InstructionOperand.Register(srcA),
              InstructionOperand.Register(srcB),
              InstructionOperand.Register(srcC)
            ) => Some(Instruction.SueauxluPipeRegRegRegReg(dst, srcA, srcB, srcC, modifiers, guard))
        case _ => None
      }
  }
  object SuldPredRegAddressImm extends Definition(
        "SuldPredRegAddressImm",
        "SULD",
        Vec(
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("suid", OperandKind.Immediate)
        ),
        Vec("SULD_D_IMM", "SULD_P_IMM")
      ) {
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
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("bank5", OperandKind.Immediate),
          Slot("tsPtrIdx", OperandKind.Immediate)
        ),
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
        Vec(
          Slot("pu", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("bank5", OperandKind.Immediate),
          Slot("hdrtblbase6", OperandKind.Immediate),
          Slot("tid8", OperandKind.Immediate)
        ),
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
          Slot("dstPredicate", OperandKind.Predicate),
          Slot("dst", OperandKind.Register),
          Slot("srcA", OperandKind.Address),
          Slot("srcC", OperandKind.Register)
        ),
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
  val definitions: Vec[Definition] = Vec(
    R2pintPipeSymbolRegImm,
    R2pintPipeSymbolRegReg,
    R2pxluPipeSymbolReg,
    R2pxluPipeSymbolRegConst,
    R2pxluPipeSymbolRegImm,
    R2pxluPipeSymbolRegReg,
    R2urPredRegReg,
    R2urRegReg,
    R2urudpPipePredRegReg,
    R2urudpPipeRegReg,
    RamNoOperands,
    RambruPipeNoOperands,
    RedAddressReg,
    RedasAddressReg,
    RedasmioPipeAddressReg,
    RedgAddressReg,
    RedgmioPipeAddressReg,
    RedsAddressReg,
    RedsmioPipeAddressReg,
    ReduxRegReg,
    ReduxudpPipeRegReg,
    RedaguPipeAddressReg,
    RedmioPipeAddressReg,
    RetNoOperands,
    RetPredReg,
    RetPredRegImm,
    RetReg,
    RetRegImm,
    RetbruPipeNoOperands,
    RetcbuPipePredReg,
    RetcbuPipePredRegImm,
    RetcbuPipeReg,
    RetcbuPipeRegImm,
    RpcmovAddressImm,
    RpcmovConst,
    RpcmovImm,
    RpcmovReg,
    RpcmovintPipeAddressImm,
    RpcmovintPipeConst,
    RpcmovintPipeImm,
    RpcmovintPipeReg,
    RroRegConst,
    RroRegImm,
    RroRegReg,
    RrofmaxPipeRegConst,
    RrofmaxPipeRegImm,
    RrofmaxPipeRegReg,
    RrofxuPipeRegConst,
    RrofxuPipeRegImm,
    RrofxuPipeRegReg,
    RttNoOperands,
    RttbruPipeNoOperands,
    RttcbuPipeNoOperands,
    S2rRegSpecial,
    S2rfauPipeRegSpecial,
    S2rmioPipeRegSpecial,
    S2rxluPipeRegSpecial,
    S2urRegSpecial,
    S2urudpPipeRegSpecial,
    SamNoOperands,
    SambruPipeNoOperands,
    ScatterRegRegRegRegImmImm,
    ScatterintPipeRegRegRegRegImmImm,
    SelRegRegAddressImmPred,
    SelRegRegConstPred,
    SelRegRegImmPred,
    SelRegRegRegPred,
    SelfauPipeRegRegConstPred,
    SelfauPipeRegRegImmPred,
    SelfauPipeRegRegRegPred,
    SelfxuPipeRegRegConstPred,
    SelfxuPipeRegRegImmPred,
    SelfxuPipeRegRegRegPred,
    SelintPipeRegRegAddressImmPred,
    SelintPipeRegRegConstPred,
    SelintPipeRegRegImmPred,
    SelintPipeRegRegRegPred,
    SelxluPipeRegRegConstPred,
    SelxluPipeRegRegImmPred,
    SelxluPipeRegRegRegPred,
    SetcrsptrReg,
    SetcrsptrbruPipeReg,
    SetcrsptrmioPipeReg,
    SetctaidReg,
    SetctaidmioPipeReg,
    SetlmembaseReg,
    SetlmembaseaguPipeReg,
    SetlmembasemioPipeReg,
    SgxtRegRegAddressImm,
    SgxtRegRegConst,
    SgxtRegRegImm,
    SgxtRegRegReg,
    SgxtintPipeRegRegAddressImm,
    SgxtintPipeRegRegConst,
    SgxtintPipeRegRegImm,
    SgxtintPipeRegRegReg,
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
    ShflaguPipePredRegRegImmImm,
    ShflaguPipePredRegRegImmReg,
    ShflaguPipePredRegRegRegImm,
    ShflaguPipePredRegRegRegReg,
    ShflmioPipePredRegRegImmImm,
    ShflmioPipePredRegRegImmReg,
    ShflmioPipePredRegRegRegImm,
    ShflmioPipePredRegRegRegReg,
    ShffmaxPipeRegRegImmReg,
    ShffmaxPipeRegRegRegReg,
    ShffxuPipeRegRegImmReg,
    ShffxuPipeRegRegRegReg,
    ShfintPipeRegRegAddressImmReg,
    ShfintPipeRegRegConstReg,
    ShfintPipeRegRegImmReg,
    ShfintPipeRegRegRegAddressImm,
    ShfintPipeRegRegRegConst,
    ShfintPipeRegRegRegImm,
    ShfintPipeRegRegRegReg,
    ShfxluPipeRegRegImmReg,
    ShfxluPipeRegRegRegReg,
    ShintImmImm,
    ShintImmImmImmImmImmImmImmImmImmImmImmImmImmImm,
    ShintfePipeImmImm,
    ShintfePipeImmImmImmImmImmImmImmImmImmImmImmImmImmImm,
    ShlRegImmReg,
    ShlRegRegAddressImm,
    ShlRegRegConst,
    ShlRegRegImm,
    ShlRegRegReg,
    ShlfxuPipeRegRegConst,
    ShlfxuPipeRegRegImm,
    ShlfxuPipeRegRegReg,
    ShlintPipeRegImmReg,
    ShlintPipeRegRegAddressImm,
    ShlintPipeRegRegConst,
    ShlintPipeRegRegImm,
    ShlintPipeRegRegReg,
    ShlxluPipeRegRegConst,
    ShlxluPipeRegRegImm,
    ShlxluPipeRegRegReg,
    ShrRegRegAddressImm,
    ShrRegRegConst,
    ShrRegRegImm,
    ShrRegRegReg,
    ShrfxuPipeRegRegConst,
    ShrfxuPipeRegRegImm,
    ShrfxuPipeRegRegReg,
    ShrintPipeRegRegAddressImm,
    ShrintPipeRegRegConst,
    ShrintPipeRegRegImm,
    ShrintPipeRegRegReg,
    ShrxluPipeRegRegConst,
    ShrxluPipeRegRegImm,
    ShrxluPipeRegRegReg,
    SpmetadataRegRegRegImmImmImm,
    SpmetadataintPipeRegRegRegImmImmImm,
    SsyConst,
    SsyImm,
    SsybruPipeConst,
    SsybruPipeImm,
    StAddressReg,
    StAddressRegPred,
    StasAddressReg,
    StasmioPipeAddressReg,
    StgAddressReg,
    StgAddressRegReg,
    StgAddressRegRegImm,
    StgmioPipeAddressReg,
    StgmioPipeAddressRegReg,
    StgmioPipeAddressRegRegImm,
    StlAddressReg,
    StlaguPipeAddressReg,
    StlmioPipeAddressReg,
    StpImm,
    StpfePipeImm,
    StpmioPipeImm,
    StsAddressReg,
    StsculPredAddressReg,
    StsculaguPipePredAddressReg,
    StsmAddressReg,
    StsmmioPipeAddressReg,
    StsaguPipeAddressReg,
    StsmioPipeAddressReg,
    SttAddressReg,
    SttmAddressReg,
    SttmudpPipeAddressReg,
    SttudpPipeAddressReg,
    StaguPipeAddressReg,
    StmioPipeAddressReg,
    StmioPipeAddressRegPred,
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
    SuatommioPipePredRegAddressRegImm,
    SuatommioPipePredRegAddressRegImmImm,
    SuatommioPipePredRegAddressRegImmImmImm,
    SuatommioPipePredRegAddressRegReg,
    SuatommioPipePredRegAddressRegRegImm,
    SuatommioPipePredRegAddressRegRegReg,
    SuatommioPipePredRegAddressRegRegRegImm,
    SuatommioPipePredRegAddressRegRegRegReg,
    SuatommioPipeRegAddressRegImm,
    SuatommioPipeRegAddressRegImmImm,
    SuatommioPipeRegAddressRegImmImmImm,
    SuatommioPipeRegAddressRegReg,
    SuatommioPipeRegAddressRegRegImm,
    SuatommioPipeRegAddressRegRegReg,
    SuatommioPipeRegAddressRegRegRegImm,
    SuatommioPipeRegAddressRegRegRegReg,
    SubfmPredRegRegConstReg,
    SubfmPredRegRegImmReg,
    SubfmPredRegRegRegConst,
    SubfmPredRegRegRegReg,
    SubfmxluPipePredRegRegConstReg,
    SubfmxluPipePredRegRegImmReg,
    SubfmxluPipePredRegRegRegConst,
    SubfmxluPipePredRegRegRegReg,
    SuclampPredRegRegConstImm,
    SuclampPredRegRegImmImm,
    SuclampPredRegRegRegImm,
    SuclampxluPipePredRegRegConstImm,
    SuclampxluPipePredRegRegImmImm,
    SuclampxluPipePredRegRegRegImm,
    SueauRegRegConstReg,
    SueauRegRegImmReg,
    SueauRegRegRegConst,
    SueauRegRegRegReg,
    SueauxluPipeRegRegConstReg,
    SueauxluPipeRegRegImmReg,
    SueauxluPipeRegRegRegConst,
    SueauxluPipeRegRegRegReg,
    SuldPredRegAddressImm,
    SuldPredRegAddressImmImm,
    SuldPredRegAddressImmImmImm,
    SuldPredRegAddressReg
  )
}
