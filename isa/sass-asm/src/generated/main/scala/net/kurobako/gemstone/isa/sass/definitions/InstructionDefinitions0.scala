// Generated SASS instruction decoders and checked operand constructors. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.sass
import javax.annotation.processing.Generated

import net.kurobako.gemstone.collection.*
import Instruction.*
import InstructionOperand.*
@Generated(Array("sbt codegen"))
object InstructionDefinitions0 {
  val decoders: Vec[FormDecoder] = Vec(
    FormDecoder(Definitions0.AcqbulkNoOperands)(Form.AcqbulkNoOperands.apply),
    FormDecoder(Definitions0.AcqshminitNoOperands)(Form.AcqshminitNoOperands.apply),
    FormDecoder(Definitions0.Al2pPredRegReg, Predicate, Register, Register)(Form.Al2pPredRegReg.apply),
    FormDecoder(Definitions0.Al2pPredRegRegImm, Predicate, Register, Register, Immediate)(Form.Al2pPredRegRegImm.apply),
    FormDecoder(Definitions0.Al2pRegReg, Register, Register)(Form.Al2pRegReg.apply),
    FormDecoder(Definitions0.Al2pRegRegImm, Register, Register, Immediate)(Form.Al2pRegRegImm.apply),
    FormDecoder(Definitions0.AldRegAddressReg, Register, Address, Register)(Form.AldRegAddressReg.apply),
    FormDecoder(Definitions0.ArrivesAddress, Address)(Form.ArrivesAddress.apply),
    FormDecoder(Definitions0.AstAddressRegReg, Address, Register, Register)(Form.AstAddressRegReg.apply),
    FormDecoder(Definitions0.AtomPredRegAddress, Predicate, Register, Address)(Form.AtomPredRegAddress.apply),
    FormDecoder(
      Definitions0.AtomPredRegAddressReg,
      Predicate,
      Register,
      Address,
      Register
    )(Form.AtomPredRegAddressReg.apply),
    FormDecoder(
      Definitions0.AtomPredRegAddressRegReg,
      Predicate,
      Register,
      Address,
      Register,
      Register
    )(Form.AtomPredRegAddressRegReg.apply),
    FormDecoder(Definitions0.AtomRegAddressReg, Register, Address, Register)(Form.AtomRegAddressReg.apply),
    FormDecoder(
      Definitions0.AtomRegAddressRegReg,
      Register,
      Address,
      Register,
      Register
    )(Form.AtomRegAddressRegReg.apply),
    FormDecoder(
      Definitions0.AtomgPredRegAddressReg,
      Predicate,
      Register,
      Address,
      Register
    )(Form.AtomgPredRegAddressReg.apply),
    FormDecoder(
      Definitions0.AtomgPredRegAddressRegReg,
      Predicate,
      Register,
      Address,
      Register,
      Register
    )(Form.AtomgPredRegAddressRegReg.apply),
    FormDecoder(
      Definitions0.AtomsPredAddressRegReg,
      Predicate,
      Address,
      Register,
      Register
    )(Form.AtomsPredAddressRegReg.apply),
    FormDecoder(Definitions0.AtomsRegAddress, Register, Address)(Form.AtomsRegAddress.apply),
    FormDecoder(Definitions0.AtomsRegAddressReg, Register, Address, Register)(Form.AtomsRegAddressReg.apply),
    FormDecoder(
      Definitions0.AtomsRegAddressRegReg,
      Register,
      Address,
      Register,
      Register
    )(Form.AtomsRegAddressRegReg.apply),
    FormDecoder(Definitions0.B2rReg, Register)(Form.B2rReg.apply),
    FormDecoder(Definitions0.B2rRegImm, Register, Immediate)(Form.B2rRegImm.apply),
    FormDecoder(Definitions0.B2rRegPred, Register, Predicate)(Form.B2rRegPred.apply),
    FormDecoder(Definitions0.BarNoOperands)(Form.BarNoOperands.apply),
    FormDecoder(Definitions0.BarImm, Immediate)(Form.BarImm.apply),
    FormDecoder(Definitions0.BarImmImm, Immediate, Immediate)(Form.BarImmImm.apply),
    FormDecoder(Definitions0.BarImmImmPred, Immediate, Immediate, Predicate)(Form.BarImmImmPred.apply),
    FormDecoder(Definitions0.BarImmPred, Immediate, Predicate)(Form.BarImmPred.apply),
    FormDecoder(Definitions0.BarImmReg, Immediate, Register)(Form.BarImmReg.apply),
    FormDecoder(Definitions0.BarImmRegPred, Immediate, Register, Predicate)(Form.BarImmRegPred.apply),
    FormDecoder(Definitions0.BarReg, Register)(Form.BarReg.apply),
    FormDecoder(Definitions0.BarRegImm, Register, Immediate)(Form.BarRegImm.apply),
    FormDecoder(Definitions0.BarRegImmPred, Register, Immediate, Predicate)(Form.BarRegImmPred.apply),
    FormDecoder(Definitions0.BarRegPred, Register, Predicate)(Form.BarRegPred.apply),
    FormDecoder(Definitions0.BarRegReg, Register, Register)(Form.BarRegReg.apply),
    FormDecoder(Definitions0.BarRegRegPred, Register, Register, Predicate)(Form.BarRegRegPred.apply),
    FormDecoder(Definitions0.BfeRegRegConst, Register, Register, ConstantBank)(Form.BfeRegRegConst.apply),
    FormDecoder(Definitions0.BfeRegRegImm, Register, Register, Immediate)(Form.BfeRegRegImm.apply),
    FormDecoder(Definitions0.BfeRegRegReg, Register, Register, Register)(Form.BfeRegRegReg.apply),
    FormDecoder(
      Definitions0.BfiRegRegConstReg,
      Register,
      Register,
      ConstantBank,
      Register
    )(Form.BfiRegRegConstReg.apply),
    FormDecoder(Definitions0.BfiRegRegImmReg, Register, Register, Immediate, Register)(Form.BfiRegRegImmReg.apply),
    FormDecoder(
      Definitions0.BfiRegRegRegConst,
      Register,
      Register,
      Register,
      ConstantBank
    )(Form.BfiRegRegRegConst.apply),
    FormDecoder(Definitions0.BfiRegRegRegReg, Register, Register, Register, Register)(Form.BfiRegRegRegReg.apply),
    FormDecoder(Definitions0.BgmmaRegAddressReg, Register, Address, Register)(Form.BgmmaRegAddressReg.apply),
    FormDecoder(
      Definitions0.BgmmaRegAddressRegPred,
      Register,
      Address,
      Register,
      Predicate
    )(Form.BgmmaRegAddressRegPred.apply),
    FormDecoder(
      Definitions0.BgmmaRegAddressRegReg,
      Register,
      Address,
      Register,
      Register
    )(Form.BgmmaRegAddressRegReg.apply),
    FormDecoder(
      Definitions0.BgmmaRegAddressRegRegPred,
      Register,
      Address,
      Register,
      Register,
      Predicate
    )(Form.BgmmaRegAddressRegRegPred.apply),
    FormDecoder(
      Definitions0.BgmmaRegRegAddressReg,
      Register,
      Register,
      Address,
      Register
    )(Form.BgmmaRegRegAddressReg.apply),
    FormDecoder(
      Definitions0.BgmmaRegRegAddressRegPred,
      Register,
      Register,
      Address,
      Register,
      Predicate
    )(Form.BgmmaRegRegAddressRegPred.apply),
    FormDecoder(Definitions0.BmmaRegRegRegReg, Register, Register, Register, Register)(Form.BmmaRegRegRegReg.apply),
    FormDecoder(
      Definitions0.BmmaRegRegRegRegPred,
      Register,
      Register,
      Register,
      Register,
      Predicate
    )(Form.BmmaRegRegRegRegPred.apply),
    FormDecoder(Definitions0.BmovAddressImm, Address, Immediate)(Form.BmovAddressImm.apply),
    FormDecoder(Definitions0.BmovConst, ConstantBank)(Form.BmovConst.apply),
    FormDecoder(Definitions0.BmovImm, Immediate)(Form.BmovImm.apply),
    FormDecoder(Definitions0.BmovReg, Register)(Form.BmovReg.apply),
    FormDecoder(Definitions0.BmovRegAddressImm, Register, Address, Immediate)(Form.BmovRegAddressImm.apply),
    FormDecoder(Definitions0.BmovRegConst, Register, ConstantBank)(Form.BmovRegConst.apply),
    FormDecoder(Definitions0.BmovRegImm, Register, Immediate)(Form.BmovRegImm.apply),
    FormDecoder(Definitions0.BmovRegReg, Register, Register)(Form.BmovRegReg.apply),
    FormDecoder(Definitions0.BmovRegSymbol, Register, Symbol)(Form.BmovRegSymbol.apply),
    FormDecoder(Definitions0.BmovSymbolAddressImm, Symbol, Address, Immediate)(Form.BmovSymbolAddressImm.apply),
    FormDecoder(Definitions0.BmovSymbolConst, Symbol, ConstantBank)(Form.BmovSymbolConst.apply),
    FormDecoder(Definitions0.BmovSymbolImm, Symbol, Immediate)(Form.BmovSymbolImm.apply),
    FormDecoder(Definitions0.BmovSymbolReg, Symbol, Register)(Form.BmovSymbolReg.apply),
    FormDecoder(
      Definitions0.BmskRegRegAddressImm,
      Register,
      Register,
      Address,
      Immediate
    )(Form.BmskRegRegAddressImm.apply),
    FormDecoder(Definitions0.BmskRegRegConst, Register, Register, ConstantBank)(Form.BmskRegRegConst.apply),
    FormDecoder(Definitions0.BmskRegRegImm, Register, Register, Immediate)(Form.BmskRegRegImm.apply),
    FormDecoder(Definitions0.BmskRegRegReg, Register, Register, Register)(Form.BmskRegRegReg.apply),
    FormDecoder(Definitions0.BptNoOperands)(Form.BptNoOperands.apply),
    FormDecoder(Definitions0.BptImm, Immediate)(Form.BptImm.apply),
    FormDecoder(Definitions0.BraConst, ConstantBank)(Form.BraConst.apply),
    FormDecoder(Definitions0.BraImm, Immediate)(Form.BraImm.apply),
    FormDecoder(Definitions0.BraPredImm, Predicate, Immediate)(Form.BraPredImm.apply),
    FormDecoder(Definitions0.BraPredPredImm, Predicate, Predicate, Immediate)(Form.BraPredPredImm.apply),
    FormDecoder(Definitions0.BraPredRegImm, Predicate, Register, Immediate)(Form.BraPredRegImm.apply),
    FormDecoder(Definitions0.BraRegImm, Register, Immediate)(Form.BraRegImm.apply),
    FormDecoder(Definitions0.BraSymbolConst, Symbol, ConstantBank)(Form.BraSymbolConst.apply),
    FormDecoder(Definitions0.BraSymbolImm, Symbol, Immediate)(Form.BraSymbolImm.apply),
    FormDecoder(Definitions0.BraSymbolImmImm, Symbol, Immediate, Immediate)(Form.BraSymbolImmImm.apply),
    FormDecoder(
      Definitions0.BraSymbolSymbolImmImm,
      Symbol,
      Symbol,
      Immediate,
      Immediate
    )(Form.BraSymbolSymbolImmImm.apply),
    FormDecoder(Definitions0.BreakPredReg, Predicate, Register)(Form.BreakPredReg.apply),
    FormDecoder(Definitions0.BreakReg, Register)(Form.BreakReg.apply),
    FormDecoder(Definitions0.BrevRegAddressImm, Register, Address, Immediate)(Form.BrevRegAddressImm.apply),
    FormDecoder(Definitions0.BrevRegConst, Register, ConstantBank)(Form.BrevRegConst.apply),
    FormDecoder(Definitions0.BrevRegImm, Register, Immediate)(Form.BrevRegImm.apply),
    FormDecoder(Definitions0.BrevRegReg, Register, Register)(Form.BrevRegReg.apply),
    FormDecoder(Definitions0.BrkNoOperands)(Form.BrkNoOperands.apply),
    FormDecoder(Definitions0.BrxConst, ConstantBank)(Form.BrxConst.apply),
    FormDecoder(Definitions0.BrxPredReg, Predicate, Register)(Form.BrxPredReg.apply),
    FormDecoder(Definitions0.BrxPredRegImm, Predicate, Register, Immediate)(Form.BrxPredRegImm.apply),
    FormDecoder(Definitions0.BrxReg, Register)(Form.BrxReg.apply),
    FormDecoder(Definitions0.BrxRegImm, Register, Immediate)(Form.BrxRegImm.apply),
    FormDecoder(Definitions0.BrxSymbolConst, Symbol, ConstantBank)(Form.BrxSymbolConst.apply),
    FormDecoder(Definitions0.BrxSymbolImmAddress, Symbol, Immediate, Address)(Form.BrxSymbolImmAddress.apply),
    FormDecoder(Definitions0.BrxSymbolReg, Symbol, Register)(Form.BrxSymbolReg.apply),
    FormDecoder(Definitions0.BrxSymbolRegImm, Symbol, Register, Immediate)(Form.BrxSymbolRegImm.apply),
    FormDecoder(
      Definitions0.BrxSymbolSymbolImmAddress,
      Symbol,
      Symbol,
      Immediate,
      Address
    )(Form.BrxSymbolSymbolImmAddress.apply),
    FormDecoder(Definitions0.BrxuPredReg, Predicate, Register)(Form.BrxuPredReg.apply),
    FormDecoder(Definitions0.BrxuPredRegImm, Predicate, Register, Immediate)(Form.BrxuPredRegImm.apply),
    FormDecoder(Definitions0.BrxuReg, Register)(Form.BrxuReg.apply),
    FormDecoder(Definitions0.BrxuRegImm, Register, Immediate)(Form.BrxuRegImm.apply),
    FormDecoder(Definitions0.BssyPredRegImm, Predicate, Register, Immediate)(Form.BssyPredRegImm.apply),
    FormDecoder(Definitions0.BssyRegImm, Register, Immediate)(Form.BssyRegImm.apply),
    FormDecoder(Definitions0.BsyncPredReg, Predicate, Register)(Form.BsyncPredReg.apply),
    FormDecoder(Definitions0.BsyncReg, Register)(Form.BsyncReg.apply),
    FormDecoder(Definitions0.CalConst, ConstantBank)(Form.CalConst.apply),
    FormDecoder(Definitions0.CalImm, Immediate)(Form.CalImm.apply),
    FormDecoder(Definitions0.CallConst, ConstantBank)(Form.CallConst.apply),
    FormDecoder(Definitions0.CallImm, Immediate)(Form.CallImm.apply),
    FormDecoder(Definitions0.CallPredConst, Predicate, ConstantBank)(Form.CallPredConst.apply),
    FormDecoder(Definitions0.CallPredImm, Predicate, Immediate)(Form.CallPredImm.apply),
    FormDecoder(Definitions0.CallPredReg, Predicate, Register)(Form.CallPredReg.apply),
    FormDecoder(Definitions0.CallPredRegImm, Predicate, Register, Immediate)(Form.CallPredRegImm.apply),
    FormDecoder(Definitions0.CallReg, Register)(Form.CallReg.apply),
    FormDecoder(Definitions0.CallRegImm, Register, Immediate)(Form.CallRegImm.apply),
    FormDecoder(Definitions0.CctlNoOperands)(Form.CctlNoOperands.apply),
    FormDecoder(Definitions0.CctlAddress, Address)(Form.CctlAddress.apply),
    FormDecoder(Definitions0.CctlAddressAddress, Address, Address)(Form.CctlAddressAddress.apply),
    FormDecoder(Definitions0.CctlAddressImm, Address, Immediate)(Form.CctlAddressImm.apply),
    FormDecoder(Definitions0.CctlConst, ConstantBank)(Form.CctlConst.apply),
    FormDecoder(Definitions0.CctlPredAddress, Predicate, Address)(Form.CctlPredAddress.apply),
    FormDecoder(Definitions0.CctlRegAddress, Register, Address)(Form.CctlRegAddress.apply),
    FormDecoder(Definitions0.CctllNoOperands)(Form.CctllNoOperands.apply),
    FormDecoder(Definitions0.CctllAddress, Address)(Form.CctllAddress.apply),
    FormDecoder(Definitions0.CctllRegAddress, Register, Address)(Form.CctllRegAddress.apply),
    FormDecoder(Definitions0.CctlsNoOperands)(Form.CctlsNoOperands.apply),
    FormDecoder(Definitions0.CctltNoOperands)(Form.CctltNoOperands.apply),
    FormDecoder(Definitions0.CctltImm, Immediate)(Form.CctltImm.apply),
    FormDecoder(Definitions0.CctltImmImm, Immediate, Immediate)(Form.CctltImmImm.apply),
    FormDecoder(Definitions0.CctltReg, Register)(Form.CctltReg.apply),
    FormDecoder(Definitions0.CgaerrbarNoOperands)(Form.CgaerrbarNoOperands.apply),
    FormDecoder(
      Definitions0.ClmadRegRegAddressImmReg,
      Register,
      Register,
      Address,
      Immediate,
      Register
    )(Form.ClmadRegRegAddressImmReg.apply),
    FormDecoder(
      Definitions0.ClmadRegRegConstReg,
      Register,
      Register,
      ConstantBank,
      Register
    )(Form.ClmadRegRegConstReg.apply),
    FormDecoder(
      Definitions0.ClmadRegRegRegAddressImm,
      Register,
      Register,
      Register,
      Address,
      Immediate
    )(Form.ClmadRegRegRegAddressImm.apply),
    FormDecoder(
      Definitions0.ClmadRegRegRegConst,
      Register,
      Register,
      Register,
      ConstantBank
    )(Form.ClmadRegRegRegConst.apply),
    FormDecoder(Definitions0.ClmadRegRegRegReg, Register, Register, Register, Register)(Form.ClmadRegRegRegReg.apply),
    FormDecoder(Definitions0.ContNoOperands)(Form.ContNoOperands.apply),
    FormDecoder(Definitions0.CreduxRegReg, Register, Register)(Form.CreduxRegReg.apply),
    FormDecoder(Definitions0.Cs2rRegSpecial, Register, SpecialRegister)(Form.Cs2rRegSpecial.apply),
    FormDecoder(Definitions0.Cs2urRegSpecial, Register, SpecialRegister)(Form.Cs2urRegSpecial.apply),
    FormDecoder(Definitions0.CsetRegSymbol, Register, Symbol)(Form.CsetRegSymbol.apply),
    FormDecoder(Definitions0.CsetRegSymbolPred, Register, Symbol, Predicate)(Form.CsetRegSymbolPred.apply),
    FormDecoder(
      Definitions0.CsetpPredPredSymbolPred,
      Predicate,
      Predicate,
      Symbol,
      Predicate
    )(Form.CsetpPredPredSymbolPred.apply),
    FormDecoder(Definitions0.CsetpPredSymbol, Predicate, Symbol)(Form.CsetpPredSymbol.apply),
    FormDecoder(Definitions0.CsmtestImm, Immediate)(Form.CsmtestImm.apply),
    FormDecoder(Definitions0.CsmtestPredImm, Predicate, Immediate)(Form.CsmtestPredImm.apply),
    FormDecoder(
      Definitions0.CsmtestPredPredImmPred,
      Predicate,
      Predicate,
      Immediate,
      Predicate
    )(Form.CsmtestPredPredImmPred.apply),
    FormDecoder(
      Definitions0.DaddRegRegAddressImm,
      Register,
      Register,
      Address,
      Immediate
    )(Form.DaddRegRegAddressImm.apply),
    FormDecoder(Definitions0.DaddRegRegConst, Register, Register, ConstantBank)(Form.DaddRegRegConst.apply),
    FormDecoder(Definitions0.DaddRegRegImm, Register, Register, Immediate)(Form.DaddRegRegImm.apply),
    FormDecoder(Definitions0.DaddRegRegReg, Register, Register, Register)(Form.DaddRegRegReg.apply),
    FormDecoder(Definitions0.DchkPredRegConst, Predicate, Register, ConstantBank)(Form.DchkPredRegConst.apply),
    FormDecoder(Definitions0.DchkPredRegImm, Predicate, Register, Immediate)(Form.DchkPredRegImm.apply),
    FormDecoder(Definitions0.DchkPredRegReg, Predicate, Register, Register)(Form.DchkPredRegReg.apply),
    FormDecoder(Definitions0.DepbarNoOperands)(Form.DepbarNoOperands.apply),
    FormDecoder(Definitions0.DepbarGroup, Group)(Form.DepbarGroup.apply),
    FormDecoder(Definitions0.DepbarImm, Immediate)(Form.DepbarImm.apply),
    FormDecoder(Definitions0.DepbarRegImm, Register, Immediate)(Form.DepbarRegImm.apply),
    FormDecoder(Definitions0.DepbarRegImmGroup, Register, Immediate, Group)(Form.DepbarRegImmGroup.apply),
    FormDecoder(Definitions0.DepbarRegImmImm, Register, Immediate, Immediate)(Form.DepbarRegImmImm.apply),
    FormDecoder(Definitions0.DepbarRegReg, Register, Register)(Form.DepbarRegReg.apply),
    FormDecoder(Definitions0.DepbarRegRegGroup, Register, Register, Group)(Form.DepbarRegRegGroup.apply),
    FormDecoder(Definitions0.DepbarRegRegImm, Register, Register, Immediate)(Form.DepbarRegRegImm.apply),
    FormDecoder(
      Definitions0.DfmaRegRegAddressImmReg,
      Register,
      Register,
      Address,
      Immediate,
      Register
    )(Form.DfmaRegRegAddressImmReg.apply),
    FormDecoder(
      Definitions0.DfmaRegRegConstReg,
      Register,
      Register,
      ConstantBank,
      Register
    )(Form.DfmaRegRegConstReg.apply),
    FormDecoder(Definitions0.DfmaRegRegImmReg, Register, Register, Immediate, Register)(Form.DfmaRegRegImmReg.apply),
    FormDecoder(
      Definitions0.DfmaRegRegRegAddressImm,
      Register,
      Register,
      Register,
      Address,
      Immediate
    )(Form.DfmaRegRegRegAddressImm.apply),
    FormDecoder(
      Definitions0.DfmaRegRegRegConst,
      Register,
      Register,
      Register,
      ConstantBank
    )(Form.DfmaRegRegRegConst.apply),
    FormDecoder(Definitions0.DfmaRegRegRegImm, Register, Register, Register, Immediate)(Form.DfmaRegRegRegImm.apply),
    FormDecoder(Definitions0.DfmaRegRegRegReg, Register, Register, Register, Register)(Form.DfmaRegRegRegReg.apply),
    FormDecoder(Definitions0.DmmaRegRegRegReg, Register, Register, Register, Register)(Form.DmmaRegRegRegReg.apply),
    FormDecoder(
      Definitions0.DmmaRegRegRegRegPred,
      Register,
      Register,
      Register,
      Register,
      Predicate
    )(Form.DmmaRegRegRegRegPred.apply),
    FormDecoder(
      Definitions0.DmnmxRegRegConstPred,
      Register,
      Register,
      ConstantBank,
      Predicate
    )(Form.DmnmxRegRegConstPred.apply),
    FormDecoder(
      Definitions0.DmnmxRegRegImmPred,
      Register,
      Register,
      Immediate,
      Predicate
    )(Form.DmnmxRegRegImmPred.apply),
    FormDecoder(
      Definitions0.DmnmxRegRegRegPred,
      Register,
      Register,
      Register,
      Predicate
    )(Form.DmnmxRegRegRegPred.apply),
    FormDecoder(
      Definitions0.DmulRegRegAddressImm,
      Register,
      Register,
      Address,
      Immediate
    )(Form.DmulRegRegAddressImm.apply),
    FormDecoder(Definitions0.DmulRegRegConst, Register, Register, ConstantBank)(Form.DmulRegRegConst.apply),
    FormDecoder(Definitions0.DmulRegRegImm, Register, Register, Immediate)(Form.DmulRegRegImm.apply),
    FormDecoder(Definitions0.DmulRegRegReg, Register, Register, Register)(Form.DmulRegRegReg.apply),
    FormDecoder(Definitions0.DsetRegRegConst, Register, Register, ConstantBank)(Form.DsetRegRegConst.apply),
    FormDecoder(
      Definitions0.DsetRegRegConstPred,
      Register,
      Register,
      ConstantBank,
      Predicate
    )(Form.DsetRegRegConstPred.apply),
    FormDecoder(Definitions0.DsetRegRegImm, Register, Register, Immediate)(Form.DsetRegRegImm.apply),
    FormDecoder(Definitions0.DsetRegRegImmPred, Register, Register, Immediate, Predicate)(Form.DsetRegRegImmPred.apply),
    FormDecoder(Definitions0.DsetRegRegReg, Register, Register, Register)(Form.DsetRegRegReg.apply),
    FormDecoder(Definitions0.DsetRegRegRegPred, Register, Register, Register, Predicate)(Form.DsetRegRegRegPred.apply),
    FormDecoder(
      Definitions0.DsetpPredPredRegAddressImmPred,
      Predicate,
      Predicate,
      Register,
      Address,
      Immediate,
      Predicate
    )(Form.DsetpPredPredRegAddressImmPred.apply),
    FormDecoder(
      Definitions0.DsetpPredPredRegConstPred,
      Predicate,
      Predicate,
      Register,
      ConstantBank,
      Predicate
    )(Form.DsetpPredPredRegConstPred.apply),
    FormDecoder(
      Definitions0.DsetpPredPredRegImmPred,
      Predicate,
      Predicate,
      Register,
      Immediate,
      Predicate
    )(Form.DsetpPredPredRegImmPred.apply),
    FormDecoder(
      Definitions0.DsetpPredPredRegRegPred,
      Predicate,
      Predicate,
      Register,
      Register,
      Predicate
    )(Form.DsetpPredPredRegRegPred.apply),
    FormDecoder(
      Definitions0.DsetpPredRegAddressImm,
      Predicate,
      Register,
      Address,
      Immediate
    )(Form.DsetpPredRegAddressImm.apply),
    FormDecoder(Definitions0.DsetpPredRegConst, Predicate, Register, ConstantBank)(Form.DsetpPredRegConst.apply),
    FormDecoder(Definitions0.DsetpPredRegImm, Predicate, Register, Immediate)(Form.DsetpPredRegImm.apply),
    FormDecoder(Definitions0.DsetpPredRegReg, Predicate, Register, Register)(Form.DsetpPredRegReg.apply),
    FormDecoder(Definitions0.ElectPredReg, Predicate, Register)(Form.ElectPredReg.apply),
    FormDecoder(Definitions0.ElectPredRegPred, Predicate, Register, Predicate)(Form.ElectPredRegPred.apply),
    FormDecoder(Definitions0.ElectPredRegReg, Predicate, Register, Register)(Form.ElectPredRegReg.apply),
    FormDecoder(Definitions0.EndcollectiveNoOperands)(Form.EndcollectiveNoOperands.apply),
    FormDecoder(Definitions0.EndcollectivePred, Predicate)(Form.EndcollectivePred.apply),
    FormDecoder(Definitions0.ErrbarNoOperands)(Form.ErrbarNoOperands.apply),
    FormDecoder(Definitions0.ExitNoOperands)(Form.ExitNoOperands.apply),
    FormDecoder(Definitions0.ExitPred, Predicate)(Form.ExitPred.apply),
    FormDecoder(Definitions0.F2fRegAddressImm, Register, Address, Immediate)(Form.F2fRegAddressImm.apply),
    FormDecoder(Definitions0.F2fRegConst, Register, ConstantBank)(Form.F2fRegConst.apply),
    FormDecoder(Definitions0.F2fRegImm, Register, Immediate)(Form.F2fRegImm.apply),
    FormDecoder(Definitions0.F2fRegReg, Register, Register)(Form.F2fRegReg.apply),
    FormDecoder(Definitions0.F2fpRegAddressImm, Register, Address, Immediate)(Form.F2fpRegAddressImm.apply),
    FormDecoder(
      Definitions0.F2fpRegAddressImmReg,
      Register,
      Address,
      Immediate,
      Register
    )(Form.F2fpRegAddressImmReg.apply),
    FormDecoder(Definitions0.F2fpRegConst, Register, ConstantBank)(Form.F2fpRegConst.apply),
    FormDecoder(Definitions0.F2fpRegConstReg, Register, ConstantBank, Register)(Form.F2fpRegConstReg.apply),
    FormDecoder(Definitions0.F2fpRegImm, Register, Immediate)(Form.F2fpRegImm.apply),
    FormDecoder(Definitions0.F2fpRegImmReg, Register, Immediate, Register)(Form.F2fpRegImmReg.apply),
    FormDecoder(Definitions0.F2fpRegReg, Register, Register)(Form.F2fpRegReg.apply),
    FormDecoder(
      Definitions0.F2fpRegRegAddressImm,
      Register,
      Register,
      Address,
      Immediate
    )(Form.F2fpRegRegAddressImm.apply),
    FormDecoder(
      Definitions0.F2fpRegRegAddressImmReg,
      Register,
      Register,
      Address,
      Immediate,
      Register
    )(Form.F2fpRegRegAddressImmReg.apply),
    FormDecoder(Definitions0.F2fpRegRegConst, Register, Register, ConstantBank)(Form.F2fpRegRegConst.apply),
    FormDecoder(
      Definitions0.F2fpRegRegConstReg,
      Register,
      Register,
      ConstantBank,
      Register
    )(Form.F2fpRegRegConstReg.apply),
    FormDecoder(Definitions0.F2fpRegRegImm, Register, Register, Immediate)(Form.F2fpRegRegImm.apply),
    FormDecoder(Definitions0.F2fpRegRegImmReg, Register, Register, Immediate, Register)(Form.F2fpRegRegImmReg.apply),
    FormDecoder(Definitions0.F2fpRegRegReg, Register, Register, Register)(Form.F2fpRegRegReg.apply),
    FormDecoder(
      Definitions0.F2fpRegRegRegAddressImm,
      Register,
      Register,
      Register,
      Address,
      Immediate
    )(Form.F2fpRegRegRegAddressImm.apply),
    FormDecoder(
      Definitions0.F2fpRegRegRegConst,
      Register,
      Register,
      Register,
      ConstantBank
    )(Form.F2fpRegRegRegConst.apply),
    FormDecoder(Definitions0.F2fpRegRegRegImm, Register, Register, Register, Immediate)(Form.F2fpRegRegRegImm.apply),
    FormDecoder(Definitions0.F2fpRegRegRegReg, Register, Register, Register, Register)(Form.F2fpRegRegRegReg.apply),
    FormDecoder(Definitions0.F2iRegAddressImm, Register, Address, Immediate)(Form.F2iRegAddressImm.apply),
    FormDecoder(Definitions0.F2iRegConst, Register, ConstantBank)(Form.F2iRegConst.apply),
    FormDecoder(Definitions0.F2iRegImm, Register, Immediate)(Form.F2iRegImm.apply),
    FormDecoder(Definitions0.F2iRegReg, Register, Register)(Form.F2iRegReg.apply),
    FormDecoder(
      Definitions0.F2ipRegRegAddressImmReg,
      Register,
      Register,
      Address,
      Immediate,
      Register
    )(Form.F2ipRegRegAddressImmReg.apply),
    FormDecoder(
      Definitions0.F2ipRegRegConstReg,
      Register,
      Register,
      ConstantBank,
      Register
    )(Form.F2ipRegRegConstReg.apply),
    FormDecoder(Definitions0.F2ipRegRegImmReg, Register, Register, Immediate, Register)(Form.F2ipRegRegImmReg.apply),
    FormDecoder(
      Definitions0.F2ipRegRegRegAddressImm,
      Register,
      Register,
      Register,
      Address,
      Immediate
    )(Form.F2ipRegRegRegAddressImm.apply),
    FormDecoder(
      Definitions0.F2ipRegRegRegConst,
      Register,
      Register,
      Register,
      ConstantBank
    )(Form.F2ipRegRegRegConst.apply),
    FormDecoder(Definitions0.F2ipRegRegRegImm, Register, Register, Register, Immediate)(Form.F2ipRegRegRegImm.apply),
    FormDecoder(Definitions0.F2ipRegRegRegReg, Register, Register, Register, Register)(Form.F2ipRegRegRegReg.apply),
    FormDecoder(
      Definitions0.FaddRegRegAddressImm,
      Register,
      Register,
      Address,
      Immediate
    )(Form.FaddRegRegAddressImm.apply),
    FormDecoder(Definitions0.FaddRegRegConst, Register, Register, ConstantBank)(Form.FaddRegRegConst.apply),
    FormDecoder(Definitions0.FaddRegRegImm, Register, Register, Immediate)(Form.FaddRegRegImm.apply),
    FormDecoder(Definitions0.FaddRegRegReg, Register, Register, Register)(Form.FaddRegRegReg.apply),
    FormDecoder(Definitions0.Fadd2RegRegImm, Register, Register, Immediate)(Form.Fadd2RegRegImm.apply),
    FormDecoder(Definitions0.Fadd2RegRegReg, Register, Register, Register)(Form.Fadd2RegRegReg.apply),
    FormDecoder(Definitions0.Fadd32iRegRegImm, Register, Register, Immediate)(Form.Fadd32iRegRegImm.apply),
    FormDecoder(
      Definitions0.FccoPredPredRegConstReg,
      Predicate,
      Predicate,
      Register,
      ConstantBank,
      Register
    )(Form.FccoPredPredRegConstReg.apply),
    FormDecoder(
      Definitions0.FccoPredPredRegImmReg,
      Predicate,
      Predicate,
      Register,
      Immediate,
      Register
    )(Form.FccoPredPredRegImmReg.apply),
    FormDecoder(
      Definitions0.FccoPredPredRegRegConst,
      Predicate,
      Predicate,
      Register,
      Register,
      ConstantBank
    )(Form.FccoPredPredRegRegConst.apply),
    FormDecoder(
      Definitions0.FccoPredPredRegRegReg,
      Predicate,
      Predicate,
      Register,
      Register,
      Register
    )(Form.FccoPredPredRegRegReg.apply),
    FormDecoder(
      Definitions0.FccoPredRegConstReg,
      Predicate,
      Register,
      ConstantBank,
      Register
    )(Form.FccoPredRegConstReg.apply),
    FormDecoder(Definitions0.FccoPredRegImmReg, Predicate, Register, Immediate, Register)(Form.FccoPredRegImmReg.apply),
    FormDecoder(
      Definitions0.FccoPredRegRegConst,
      Predicate,
      Register,
      Register,
      ConstantBank
    )(Form.FccoPredRegRegConst.apply),
    FormDecoder(Definitions0.FccoPredRegRegReg, Predicate, Register, Register, Register)(Form.FccoPredRegRegReg.apply),
    FormDecoder(
      Definitions0.FchkPredRegAddressImm,
      Predicate,
      Register,
      Address,
      Immediate
    )(Form.FchkPredRegAddressImm.apply),
    FormDecoder(Definitions0.FchkPredRegConst, Predicate, Register, ConstantBank)(Form.FchkPredRegConst.apply),
    FormDecoder(Definitions0.FchkPredRegImm, Predicate, Register, Immediate)(Form.FchkPredRegImm.apply),
    FormDecoder(Definitions0.FchkPredRegReg, Predicate, Register, Register)(Form.FchkPredRegReg.apply),
    FormDecoder(
      Definitions0.FcmpRegRegConstReg,
      Register,
      Register,
      ConstantBank,
      Register
    )(Form.FcmpRegRegConstReg.apply),
    FormDecoder(Definitions0.FcmpRegRegImmReg, Register, Register, Immediate, Register)(Form.FcmpRegRegImmReg.apply),
    FormDecoder(
      Definitions0.FcmpRegRegRegConst,
      Register,
      Register,
      Register,
      ConstantBank
    )(Form.FcmpRegRegRegConst.apply)
  )
}
