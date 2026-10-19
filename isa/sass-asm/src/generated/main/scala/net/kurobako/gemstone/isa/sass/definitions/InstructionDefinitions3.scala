// Generated SASS instruction decoders and checked operand constructors. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.sass
import net.kurobako.gemstone.collection.*
import Instruction.*
import InstructionOperand.*
@javax.annotation.processing.Generated(Array("sbt codegen"))
object InstructionDefinitions3 {
  val decoders: Vec[FormDecoder] = Vec(
    FormDecoder(Definitions3.LeaRegRegRegImm, Register, Register, Register, Immediate)(Form.LeaRegRegRegImm.apply),
    FormDecoder(
      Definitions3.LeaRegRegRegImmImm,
      Register,
      Register,
      Register,
      Immediate,
      Immediate
    )(Form.LeaRegRegRegImmImm.apply),
    FormDecoder(
      Definitions3.LeaRegRegRegImmImmPred,
      Register,
      Register,
      Register,
      Immediate,
      Immediate,
      Predicate
    )(Form.LeaRegRegRegImmImmPred.apply),
    FormDecoder(
      Definitions3.LeaRegRegRegImmPred,
      Register,
      Register,
      Register,
      Immediate,
      Predicate
    )(Form.LeaRegRegRegImmPred.apply),
    FormDecoder(Definitions3.LeaRegRegRegReg, Register, Register, Register, Register)(Form.LeaRegRegRegReg.apply),
    FormDecoder(
      Definitions3.LeaRegRegRegRegImm,
      Register,
      Register,
      Register,
      Register,
      Immediate
    )(Form.LeaRegRegRegRegImm.apply),
    FormDecoder(
      Definitions3.LeaRegRegRegRegImmPred,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Predicate
    )(Form.LeaRegRegRegRegImmPred.apply),
    FormDecoder(Definitions3.LepcReg, Register)(Form.LepcReg.apply),
    FormDecoder(Definitions3.LepcRegImm, Register, Immediate)(Form.LepcRegImm.apply),
    FormDecoder(Definitions3.LongjmpNoOperands)(Form.LongjmpNoOperands.apply),
    FormDecoder(
      Definitions3.LopPredRegRegAddressImm,
      Predicate,
      Register,
      Register,
      Address,
      Immediate
    )(Form.LopPredRegRegAddressImm.apply),
    FormDecoder(
      Definitions3.LopPredRegRegAddressImmPred,
      Predicate,
      Register,
      Register,
      Address,
      Immediate,
      Predicate
    )(Form.LopPredRegRegAddressImmPred.apply),
    FormDecoder(
      Definitions3.LopPredRegRegConst,
      Predicate,
      Register,
      Register,
      ConstantBank
    )(Form.LopPredRegRegConst.apply),
    FormDecoder(
      Definitions3.LopPredRegRegConstPred,
      Predicate,
      Register,
      Register,
      ConstantBank,
      Predicate
    )(Form.LopPredRegRegConstPred.apply),
    FormDecoder(Definitions3.LopPredRegRegImm, Predicate, Register, Register, Immediate)(Form.LopPredRegRegImm.apply),
    FormDecoder(
      Definitions3.LopPredRegRegImmPred,
      Predicate,
      Register,
      Register,
      Immediate,
      Predicate
    )(Form.LopPredRegRegImmPred.apply),
    FormDecoder(Definitions3.LopPredRegRegReg, Predicate, Register, Register, Register)(Form.LopPredRegRegReg.apply),
    FormDecoder(
      Definitions3.LopPredRegRegRegPred,
      Predicate,
      Register,
      Register,
      Register,
      Predicate
    )(Form.LopPredRegRegRegPred.apply),
    FormDecoder(
      Definitions3.LopRegRegAddressImm,
      Register,
      Register,
      Address,
      Immediate
    )(Form.LopRegRegAddressImm.apply),
    FormDecoder(
      Definitions3.LopRegRegAddressImmPred,
      Register,
      Register,
      Address,
      Immediate,
      Predicate
    )(Form.LopRegRegAddressImmPred.apply),
    FormDecoder(Definitions3.LopRegRegConst, Register, Register, ConstantBank)(Form.LopRegRegConst.apply),
    FormDecoder(
      Definitions3.LopRegRegConstPred,
      Register,
      Register,
      ConstantBank,
      Predicate
    )(Form.LopRegRegConstPred.apply),
    FormDecoder(Definitions3.LopRegRegImm, Register, Register, Immediate)(Form.LopRegRegImm.apply),
    FormDecoder(Definitions3.LopRegRegImmPred, Register, Register, Immediate, Predicate)(Form.LopRegRegImmPred.apply),
    FormDecoder(Definitions3.LopRegRegReg, Register, Register, Register)(Form.LopRegRegReg.apply),
    FormDecoder(Definitions3.LopRegRegRegPred, Register, Register, Register, Predicate)(Form.LopRegRegRegPred.apply),
    FormDecoder(
      Definitions3.Lop3PredRegRegAddressImmReg,
      Predicate,
      Register,
      Register,
      Address,
      Immediate,
      Register
    )(Form.Lop3PredRegRegAddressImmReg.apply),
    FormDecoder(
      Definitions3.Lop3PredRegRegAddressImmRegImm,
      Predicate,
      Register,
      Register,
      Address,
      Immediate,
      Register,
      Immediate
    )(Form.Lop3PredRegRegAddressImmRegImm.apply),
    FormDecoder(
      Definitions3.Lop3PredRegRegAddressImmRegImmPred,
      Predicate,
      Register,
      Register,
      Address,
      Immediate,
      Register,
      Immediate,
      Predicate
    )(Form.Lop3PredRegRegAddressImmRegImmPred.apply),
    FormDecoder(
      Definitions3.Lop3PredRegRegAddressImmRegPred,
      Predicate,
      Register,
      Register,
      Address,
      Immediate,
      Register,
      Predicate
    )(Form.Lop3PredRegRegAddressImmRegPred.apply),
    FormDecoder(
      Definitions3.Lop3PredRegRegConstReg,
      Predicate,
      Register,
      Register,
      ConstantBank,
      Register
    )(Form.Lop3PredRegRegConstReg.apply),
    FormDecoder(
      Definitions3.Lop3PredRegRegConstRegImm,
      Predicate,
      Register,
      Register,
      ConstantBank,
      Register,
      Immediate
    )(Form.Lop3PredRegRegConstRegImm.apply),
    FormDecoder(
      Definitions3.Lop3PredRegRegConstRegImmPred,
      Predicate,
      Register,
      Register,
      ConstantBank,
      Register,
      Immediate,
      Predicate
    )(Form.Lop3PredRegRegConstRegImmPred.apply),
    FormDecoder(
      Definitions3.Lop3PredRegRegConstRegPred,
      Predicate,
      Register,
      Register,
      ConstantBank,
      Register,
      Predicate
    )(Form.Lop3PredRegRegConstRegPred.apply),
    FormDecoder(
      Definitions3.Lop3PredRegRegImmReg,
      Predicate,
      Register,
      Register,
      Immediate,
      Register
    )(Form.Lop3PredRegRegImmReg.apply),
    FormDecoder(
      Definitions3.Lop3PredRegRegImmRegImm,
      Predicate,
      Register,
      Register,
      Immediate,
      Register,
      Immediate
    )(Form.Lop3PredRegRegImmRegImm.apply),
    FormDecoder(
      Definitions3.Lop3PredRegRegImmRegImmPred,
      Predicate,
      Register,
      Register,
      Immediate,
      Register,
      Immediate,
      Predicate
    )(Form.Lop3PredRegRegImmRegImmPred.apply),
    FormDecoder(
      Definitions3.Lop3PredRegRegImmRegPred,
      Predicate,
      Register,
      Register,
      Immediate,
      Register,
      Predicate
    )(Form.Lop3PredRegRegImmRegPred.apply),
    FormDecoder(
      Definitions3.Lop3PredRegRegRegReg,
      Predicate,
      Register,
      Register,
      Register,
      Register
    )(Form.Lop3PredRegRegRegReg.apply),
    FormDecoder(
      Definitions3.Lop3PredRegRegRegRegImm,
      Predicate,
      Register,
      Register,
      Register,
      Register,
      Immediate
    )(Form.Lop3PredRegRegRegRegImm.apply),
    FormDecoder(
      Definitions3.Lop3PredRegRegRegRegImmPred,
      Predicate,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Predicate
    )(Form.Lop3PredRegRegRegRegImmPred.apply),
    FormDecoder(
      Definitions3.Lop3PredRegRegRegRegPred,
      Predicate,
      Register,
      Register,
      Register,
      Register,
      Predicate
    )(Form.Lop3PredRegRegRegRegPred.apply),
    FormDecoder(
      Definitions3.Lop3RegRegAddressImmReg,
      Register,
      Register,
      Address,
      Immediate,
      Register
    )(Form.Lop3RegRegAddressImmReg.apply),
    FormDecoder(
      Definitions3.Lop3RegRegAddressImmRegImm,
      Register,
      Register,
      Address,
      Immediate,
      Register,
      Immediate
    )(Form.Lop3RegRegAddressImmRegImm.apply),
    FormDecoder(
      Definitions3.Lop3RegRegAddressImmRegImmPred,
      Register,
      Register,
      Address,
      Immediate,
      Register,
      Immediate,
      Predicate
    )(Form.Lop3RegRegAddressImmRegImmPred.apply),
    FormDecoder(
      Definitions3.Lop3RegRegAddressImmRegPred,
      Register,
      Register,
      Address,
      Immediate,
      Register,
      Predicate
    )(Form.Lop3RegRegAddressImmRegPred.apply),
    FormDecoder(
      Definitions3.Lop3RegRegConstReg,
      Register,
      Register,
      ConstantBank,
      Register
    )(Form.Lop3RegRegConstReg.apply),
    FormDecoder(
      Definitions3.Lop3RegRegConstRegImm,
      Register,
      Register,
      ConstantBank,
      Register,
      Immediate
    )(Form.Lop3RegRegConstRegImm.apply),
    FormDecoder(
      Definitions3.Lop3RegRegConstRegImmPred,
      Register,
      Register,
      ConstantBank,
      Register,
      Immediate,
      Predicate
    )(Form.Lop3RegRegConstRegImmPred.apply),
    FormDecoder(
      Definitions3.Lop3RegRegConstRegPred,
      Register,
      Register,
      ConstantBank,
      Register,
      Predicate
    )(Form.Lop3RegRegConstRegPred.apply),
    FormDecoder(Definitions3.Lop3RegRegImmReg, Register, Register, Immediate, Register)(Form.Lop3RegRegImmReg.apply),
    FormDecoder(
      Definitions3.Lop3RegRegImmRegImm,
      Register,
      Register,
      Immediate,
      Register,
      Immediate
    )(Form.Lop3RegRegImmRegImm.apply),
    FormDecoder(
      Definitions3.Lop3RegRegImmRegImmPred,
      Register,
      Register,
      Immediate,
      Register,
      Immediate,
      Predicate
    )(Form.Lop3RegRegImmRegImmPred.apply),
    FormDecoder(
      Definitions3.Lop3RegRegImmRegPred,
      Register,
      Register,
      Immediate,
      Register,
      Predicate
    )(Form.Lop3RegRegImmRegPred.apply),
    FormDecoder(Definitions3.Lop3RegRegRegReg, Register, Register, Register, Register)(Form.Lop3RegRegRegReg.apply),
    FormDecoder(
      Definitions3.Lop3RegRegRegRegImm,
      Register,
      Register,
      Register,
      Register,
      Immediate
    )(Form.Lop3RegRegRegRegImm.apply),
    FormDecoder(
      Definitions3.Lop3RegRegRegRegImmPred,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Predicate
    )(Form.Lop3RegRegRegRegImmPred.apply),
    FormDecoder(
      Definitions3.Lop3RegRegRegRegPred,
      Register,
      Register,
      Register,
      Register,
      Predicate
    )(Form.Lop3RegRegRegRegPred.apply),
    FormDecoder(
      Definitions3.Lop32iPredRegRegImm,
      Predicate,
      Register,
      Register,
      Immediate
    )(Form.Lop32iPredRegRegImm.apply),
    FormDecoder(
      Definitions3.Lop32iPredRegRegImmPred,
      Predicate,
      Register,
      Register,
      Immediate,
      Predicate
    )(Form.Lop32iPredRegRegImmPred.apply),
    FormDecoder(Definitions3.Lop32iRegRegImm, Register, Register, Immediate)(Form.Lop32iRegRegImm.apply),
    FormDecoder(
      Definitions3.Lop32iRegRegImmPred,
      Register,
      Register,
      Immediate,
      Predicate
    )(Form.Lop32iRegRegImmPred.apply),
    FormDecoder(Definitions3.MatchPredRegReg, Predicate, Register, Register)(Form.MatchPredRegReg.apply),
    FormDecoder(Definitions3.MatchRegReg, Register, Register)(Form.MatchRegReg.apply),
    FormDecoder(Definitions3.MembarNoOperands)(Form.MembarNoOperands.apply),
    FormDecoder(Definitions3.MovAddressConst, Address, ConstantBank)(Form.MovAddressConst.apply),
    FormDecoder(Definitions3.MovAddressConstImm, Address, ConstantBank, Immediate)(Form.MovAddressConstImm.apply),
    FormDecoder(Definitions3.MovAddressImm, Address, Immediate)(Form.MovAddressImm.apply),
    FormDecoder(Definitions3.MovAddressImmImm, Address, Immediate, Immediate)(Form.MovAddressImmImm.apply),
    FormDecoder(Definitions3.MovAddressReg, Address, Register)(Form.MovAddressReg.apply),
    FormDecoder(Definitions3.MovAddressRegImm, Address, Register, Immediate)(Form.MovAddressRegImm.apply),
    FormDecoder(Definitions3.MovRegAddress, Register, Address)(Form.MovRegAddress.apply),
    FormDecoder(Definitions3.MovRegAddressImm, Register, Address, Immediate)(Form.MovRegAddressImm.apply),
    FormDecoder(
      Definitions3.MovRegAddressImmImm,
      Register,
      Address,
      Immediate,
      Immediate
    )(Form.MovRegAddressImmImm.apply),
    FormDecoder(Definitions3.MovRegConst, Register, ConstantBank)(Form.MovRegConst.apply),
    FormDecoder(Definitions3.MovRegConstImm, Register, ConstantBank, Immediate)(Form.MovRegConstImm.apply),
    FormDecoder(Definitions3.MovRegImm, Register, Immediate)(Form.MovRegImm.apply),
    FormDecoder(Definitions3.MovRegImmImm, Register, Immediate, Immediate)(Form.MovRegImmImm.apply),
    FormDecoder(Definitions3.MovRegReg, Register, Register)(Form.MovRegReg.apply),
    FormDecoder(Definitions3.MovRegRegImm, Register, Register, Immediate)(Form.MovRegRegImm.apply),
    FormDecoder(Definitions3.Mov32iRegImm, Register, Immediate)(Form.Mov32iRegImm.apply),
    FormDecoder(Definitions3.Mov32iRegImmImm, Register, Immediate, Immediate)(Form.Mov32iRegImmImm.apply),
    FormDecoder(Definitions3.Mov64iurRegImm, Register, Immediate)(Form.Mov64iurRegImm.apply),
    FormDecoder(Definitions3.Mov64iurRegReg, Register, Register)(Form.Mov64iurRegReg.apply),
    FormDecoder(Definitions3.MovmRegReg, Register, Register)(Form.MovmRegReg.apply),
    FormDecoder(Definitions3.MufuRegAddressImm, Register, Address, Immediate)(Form.MufuRegAddressImm.apply),
    FormDecoder(Definitions3.MufuRegConst, Register, ConstantBank)(Form.MufuRegConst.apply),
    FormDecoder(Definitions3.MufuRegImm, Register, Immediate)(Form.MufuRegImm.apply),
    FormDecoder(Definitions3.MufuRegImmImm, Register, Immediate, Immediate)(Form.MufuRegImmImm.apply),
    FormDecoder(Definitions3.MufuRegReg, Register, Register)(Form.MufuRegReg.apply),
    FormDecoder(Definitions3.NanosleepNoOperands)(Form.NanosleepNoOperands.apply),
    FormDecoder(Definitions3.NanosleepAddressImm, Address, Immediate)(Form.NanosleepAddressImm.apply),
    FormDecoder(Definitions3.NanosleepConst, ConstantBank)(Form.NanosleepConst.apply),
    FormDecoder(Definitions3.NanosleepImm, Immediate)(Form.NanosleepImm.apply),
    FormDecoder(Definitions3.NanosleepPred, Predicate)(Form.NanosleepPred.apply),
    FormDecoder(
      Definitions3.NanosleepPredAddressImm,
      Predicate,
      Address,
      Immediate
    )(Form.NanosleepPredAddressImm.apply),
    FormDecoder(Definitions3.NanosleepPredConst, Predicate, ConstantBank)(Form.NanosleepPredConst.apply),
    FormDecoder(Definitions3.NanosleepPredImm, Predicate, Immediate)(Form.NanosleepPredImm.apply),
    FormDecoder(Definitions3.NanosleepPredReg, Predicate, Register)(Form.NanosleepPredReg.apply),
    FormDecoder(Definitions3.NanosleepReg, Register)(Form.NanosleepReg.apply),
    FormDecoder(Definitions3.NanotrapConst, ConstantBank)(Form.NanotrapConst.apply),
    FormDecoder(Definitions3.NanotrapImm, Immediate)(Form.NanotrapImm.apply),
    FormDecoder(Definitions3.NanotrapPredConst, Predicate, ConstantBank)(Form.NanotrapPredConst.apply),
    FormDecoder(Definitions3.NanotrapPredImm, Predicate, Immediate)(Form.NanotrapPredImm.apply),
    FormDecoder(Definitions3.NanotrapPredReg, Predicate, Register)(Form.NanotrapPredReg.apply),
    FormDecoder(Definitions3.NanotrapReg, Register)(Form.NanotrapReg.apply),
    FormDecoder(Definitions3.NopNoOperands)(Form.NopNoOperands.apply),
    FormDecoder(Definitions3.NopImm, Immediate)(Form.NopImm.apply),
    FormDecoder(Definitions3.NopSymbol, Symbol)(Form.NopSymbol.apply),
    FormDecoder(Definitions3.NopSymbolImm, Symbol, Immediate)(Form.NopSymbolImm.apply),
    FormDecoder(
      Definitions3.OmmaRegRegRegRegPredRegRegReg,
      Register,
      Register,
      Register,
      Register,
      Predicate,
      Register,
      Register,
      Register
    )(Form.OmmaRegRegRegRegPredRegRegReg.apply),
    FormDecoder(
      Definitions3.OmmaRegRegRegRegPredRegRegRegImm,
      Register,
      Register,
      Register,
      Register,
      Predicate,
      Register,
      Register,
      Register,
      Immediate
    )(Form.OmmaRegRegRegRegPredRegRegRegImm.apply),
    FormDecoder(
      Definitions3.OmmaRegRegRegRegRegRegReg,
      Register,
      Register,
      Register,
      Register,
      Register,
      Register,
      Register
    )(Form.OmmaRegRegRegRegRegRegReg.apply),
    FormDecoder(
      Definitions3.OmmaRegRegRegRegRegRegRegImm,
      Register,
      Register,
      Register,
      Register,
      Register,
      Register,
      Register,
      Immediate
    )(Form.OmmaRegRegRegRegRegRegRegImm.apply),
    FormDecoder(Definitions3.OutReg, Register)(Form.OutReg.apply),
    FormDecoder(Definitions3.OutRegRegConst, Register, Register, ConstantBank)(Form.OutRegRegConst.apply),
    FormDecoder(Definitions3.OutRegRegImm, Register, Register, Immediate)(Form.OutRegRegImm.apply),
    FormDecoder(Definitions3.OutRegRegReg, Register, Register, Register)(Form.OutRegRegReg.apply),
    FormDecoder(Definitions3.P2rReg, Register)(Form.P2rReg.apply),
    FormDecoder(Definitions3.P2rRegRegConst, Register, Register, ConstantBank)(Form.P2rRegRegConst.apply),
    FormDecoder(Definitions3.P2rRegRegImm, Register, Register, Immediate)(Form.P2rRegRegImm.apply),
    FormDecoder(Definitions3.P2rRegRegReg, Register, Register, Register)(Form.P2rRegRegReg.apply),
    FormDecoder(Definitions3.P2rRegSymbol, Register, Symbol)(Form.P2rRegSymbol.apply),
    FormDecoder(
      Definitions3.P2rRegSymbolRegAddressImm,
      Register,
      Symbol,
      Register,
      Address,
      Immediate
    )(Form.P2rRegSymbolRegAddressImm.apply),
    FormDecoder(
      Definitions3.P2rRegSymbolRegConst,
      Register,
      Symbol,
      Register,
      ConstantBank
    )(Form.P2rRegSymbolRegConst.apply),
    FormDecoder(Definitions3.P2rRegSymbolRegImm, Register, Symbol, Register, Immediate)(Form.P2rRegSymbolRegImm.apply),
    FormDecoder(Definitions3.P2rRegSymbolRegReg, Register, Symbol, Register, Register)(Form.P2rRegSymbolRegReg.apply),
    FormDecoder(Definitions3.PbkConst, ConstantBank)(Form.PbkConst.apply),
    FormDecoder(Definitions3.PbkImm, Immediate)(Form.PbkImm.apply),
    FormDecoder(Definitions3.PcntConst, ConstantBank)(Form.PcntConst.apply),
    FormDecoder(Definitions3.PcntImm, Immediate)(Form.PcntImm.apply),
    FormDecoder(Definitions3.PexitImm, Immediate)(Form.PexitImm.apply),
    FormDecoder(Definitions3.PixldReg, Register)(Form.PixldReg.apply),
    FormDecoder(Definitions3.PixldRegAddress, Register, Address)(Form.PixldRegAddress.apply),
    FormDecoder(Definitions3.PixldRegPred, Register, Predicate)(Form.PixldRegPred.apply),
    FormDecoder(Definitions3.PixldRegPredAddress, Register, Predicate, Address)(Form.PixldRegPredAddress.apply),
    FormDecoder(Definitions3.PlongjmpConst, ConstantBank)(Form.PlongjmpConst.apply),
    FormDecoder(Definitions3.PlongjmpImm, Immediate)(Form.PlongjmpImm.apply),
    FormDecoder(
      Definitions3.Plop3PredPredAddressImmPredImm,
      Predicate,
      Predicate,
      Address,
      Immediate,
      Predicate,
      Immediate
    )(Form.Plop3PredPredAddressImmPredImm.apply),
    FormDecoder(
      Definitions3.Plop3PredPredAddressImmRegImm,
      Predicate,
      Predicate,
      Address,
      Immediate,
      Register,
      Immediate
    )(Form.Plop3PredPredAddressImmRegImm.apply),
    FormDecoder(
      Definitions3.Plop3PredPredConstPredImm,
      Predicate,
      Predicate,
      ConstantBank,
      Predicate,
      Immediate
    )(Form.Plop3PredPredConstPredImm.apply),
    FormDecoder(
      Definitions3.Plop3PredPredConstRegImm,
      Predicate,
      Predicate,
      ConstantBank,
      Register,
      Immediate
    )(Form.Plop3PredPredConstRegImm.apply),
    FormDecoder(
      Definitions3.Plop3PredPredPredAddressImmPredImmImm,
      Predicate,
      Predicate,
      Predicate,
      Address,
      Immediate,
      Predicate,
      Immediate,
      Immediate
    )(Form.Plop3PredPredPredAddressImmPredImmImm.apply),
    FormDecoder(
      Definitions3.Plop3PredPredPredAddressImmRegImmImm,
      Predicate,
      Predicate,
      Predicate,
      Address,
      Immediate,
      Register,
      Immediate,
      Immediate
    )(Form.Plop3PredPredPredAddressImmRegImmImm.apply),
    FormDecoder(
      Definitions3.Plop3PredPredPredConstPredImmImm,
      Predicate,
      Predicate,
      Predicate,
      ConstantBank,
      Predicate,
      Immediate,
      Immediate
    )(Form.Plop3PredPredPredConstPredImmImm.apply),
    FormDecoder(
      Definitions3.Plop3PredPredPredConstRegImmImm,
      Predicate,
      Predicate,
      Predicate,
      ConstantBank,
      Register,
      Immediate,
      Immediate
    )(Form.Plop3PredPredPredConstRegImmImm.apply),
    FormDecoder(
      Definitions3.Plop3PredPredPredPred,
      Predicate,
      Predicate,
      Predicate,
      Predicate
    )(Form.Plop3PredPredPredPred.apply),
    FormDecoder(
      Definitions3.Plop3PredPredPredPredImm,
      Predicate,
      Predicate,
      Predicate,
      Predicate,
      Immediate
    )(Form.Plop3PredPredPredPredImm.apply),
    FormDecoder(
      Definitions3.Plop3PredPredPredPredPredImmImm,
      Predicate,
      Predicate,
      Predicate,
      Predicate,
      Predicate,
      Immediate,
      Immediate
    )(Form.Plop3PredPredPredPredPredImmImm.apply),
    FormDecoder(
      Definitions3.Plop3PredPredPredRegPredImmImm,
      Predicate,
      Predicate,
      Predicate,
      Register,
      Predicate,
      Immediate,
      Immediate
    )(Form.Plop3PredPredPredRegPredImmImm.apply),
    FormDecoder(
      Definitions3.Plop3PredPredPredRegRegImmImm,
      Predicate,
      Predicate,
      Predicate,
      Register,
      Register,
      Immediate,
      Immediate
    )(Form.Plop3PredPredPredRegRegImmImm.apply),
    FormDecoder(
      Definitions3.Plop3PredPredRegAddressImmRegImmImm,
      Predicate,
      Predicate,
      Register,
      Address,
      Immediate,
      Register,
      Immediate,
      Immediate
    )(Form.Plop3PredPredRegAddressImmRegImmImm.apply),
    FormDecoder(
      Definitions3.Plop3PredPredRegConstRegImmImm,
      Predicate,
      Predicate,
      Register,
      ConstantBank,
      Register,
      Immediate,
      Immediate
    )(Form.Plop3PredPredRegConstRegImmImm.apply),
    FormDecoder(
      Definitions3.Plop3PredPredRegPredImm,
      Predicate,
      Predicate,
      Register,
      Predicate,
      Immediate
    )(Form.Plop3PredPredRegPredImm.apply),
    FormDecoder(
      Definitions3.Plop3PredPredRegRegImm,
      Predicate,
      Predicate,
      Register,
      Register,
      Immediate
    )(Form.Plop3PredPredRegRegImm.apply),
    FormDecoder(
      Definitions3.Plop3PredPredRegRegRegImmImm,
      Predicate,
      Predicate,
      Register,
      Register,
      Register,
      Immediate,
      Immediate
    )(Form.Plop3PredPredRegRegRegImmImm.apply),
    FormDecoder(
      Definitions3.Plop3PredRegAddressImmRegImm,
      Predicate,
      Register,
      Address,
      Immediate,
      Register,
      Immediate
    )(Form.Plop3PredRegAddressImmRegImm.apply),
    FormDecoder(
      Definitions3.Plop3PredRegConstRegImm,
      Predicate,
      Register,
      ConstantBank,
      Register,
      Immediate
    )(Form.Plop3PredRegConstRegImm.apply),
    FormDecoder(
      Definitions3.Plop3PredRegRegRegImm,
      Predicate,
      Register,
      Register,
      Register,
      Immediate
    )(Form.Plop3PredRegRegRegImm.apply),
    FormDecoder(Definitions3.PmtrigImm, Immediate)(Form.PmtrigImm.apply),
    FormDecoder(Definitions3.PmtrigPredImm, Predicate, Immediate)(Form.PmtrigPredImm.apply),
    FormDecoder(Definitions3.PopcRegAddressImm, Register, Address, Immediate)(Form.PopcRegAddressImm.apply),
    FormDecoder(Definitions3.PopcRegConst, Register, ConstantBank)(Form.PopcRegConst.apply),
    FormDecoder(Definitions3.PopcRegImm, Register, Immediate)(Form.PopcRegImm.apply),
    FormDecoder(Definitions3.PopcRegReg, Register, Register)(Form.PopcRegReg.apply),
    FormDecoder(Definitions3.PopcRegRegConst, Register, Register, ConstantBank)(Form.PopcRegRegConst.apply),
    FormDecoder(Definitions3.PopcRegRegImm, Register, Register, Immediate)(Form.PopcRegRegImm.apply),
    FormDecoder(Definitions3.PopcRegRegReg, Register, Register, Register)(Form.PopcRegRegReg.apply),
    FormDecoder(Definitions3.PreexitNoOperands)(Form.PreexitNoOperands.apply),
    FormDecoder(Definitions3.PretConst, ConstantBank)(Form.PretConst.apply),
    FormDecoder(Definitions3.PretImm, Immediate)(Form.PretImm.apply),
    FormDecoder(
      Definitions3.PrmtRegRegAddressImmReg,
      Register,
      Register,
      Address,
      Immediate,
      Register
    )(Form.PrmtRegRegAddressImmReg.apply),
    FormDecoder(
      Definitions3.PrmtRegRegConstReg,
      Register,
      Register,
      ConstantBank,
      Register
    )(Form.PrmtRegRegConstReg.apply),
    FormDecoder(Definitions3.PrmtRegRegImmReg, Register, Register, Immediate, Register)(Form.PrmtRegRegImmReg.apply),
    FormDecoder(
      Definitions3.PrmtRegRegRegAddressImm,
      Register,
      Register,
      Register,
      Address,
      Immediate
    )(Form.PrmtRegRegRegAddressImm.apply),
    FormDecoder(
      Definitions3.PrmtRegRegRegConst,
      Register,
      Register,
      Register,
      ConstantBank
    )(Form.PrmtRegRegRegConst.apply),
    FormDecoder(Definitions3.PrmtRegRegRegImm, Register, Register, Register, Immediate)(Form.PrmtRegRegRegImm.apply),
    FormDecoder(Definitions3.PrmtRegRegRegReg, Register, Register, Register, Register)(Form.PrmtRegRegRegReg.apply),
    FormDecoder(Definitions3.PsetRegPredPred, Register, Predicate, Predicate)(Form.PsetRegPredPred.apply),
    FormDecoder(
      Definitions3.PsetRegPredPredPred,
      Register,
      Predicate,
      Predicate,
      Predicate
    )(Form.PsetRegPredPredPred.apply),
    FormDecoder(Definitions3.PsetpPredPredPred, Predicate, Predicate, Predicate)(Form.PsetpPredPredPred.apply),
    FormDecoder(
      Definitions3.PsetpPredPredPredPredPred,
      Predicate,
      Predicate,
      Predicate,
      Predicate,
      Predicate
    )(Form.PsetpPredPredPredPredPred.apply),
    FormDecoder(Definitions3.Qadd4RegRegImm, Register, Register, Immediate)(Form.Qadd4RegRegImm.apply),
    FormDecoder(Definitions3.Qadd4RegRegReg, Register, Register, Register)(Form.Qadd4RegRegReg.apply),
    FormDecoder(Definitions3.Qfma4RegRegImmReg, Register, Register, Immediate, Register)(Form.Qfma4RegRegImmReg.apply),
    FormDecoder(Definitions3.Qfma4RegRegRegImm, Register, Register, Register, Immediate)(Form.Qfma4RegRegRegImm.apply),
    FormDecoder(Definitions3.Qfma4RegRegRegReg, Register, Register, Register, Register)(Form.Qfma4RegRegRegReg.apply),
    FormDecoder(Definitions3.QgmmaRegAddressReg, Register, Address, Register)(Form.QgmmaRegAddressReg.apply),
    FormDecoder(
      Definitions3.QgmmaRegAddressRegPred,
      Register,
      Address,
      Register,
      Predicate
    )(Form.QgmmaRegAddressRegPred.apply),
    FormDecoder(
      Definitions3.QgmmaRegAddressRegPredReg,
      Register,
      Address,
      Register,
      Predicate,
      Register
    )(Form.QgmmaRegAddressRegPredReg.apply),
    FormDecoder(
      Definitions3.QgmmaRegAddressRegPredRegImm,
      Register,
      Address,
      Register,
      Predicate,
      Register,
      Immediate
    )(Form.QgmmaRegAddressRegPredRegImm.apply),
    FormDecoder(
      Definitions3.QgmmaRegAddressRegReg,
      Register,
      Address,
      Register,
      Register
    )(Form.QgmmaRegAddressRegReg.apply),
    FormDecoder(
      Definitions3.QgmmaRegAddressRegRegImm,
      Register,
      Address,
      Register,
      Register,
      Immediate
    )(Form.QgmmaRegAddressRegRegImm.apply),
    FormDecoder(
      Definitions3.QgmmaRegAddressRegRegPred,
      Register,
      Address,
      Register,
      Register,
      Predicate
    )(Form.QgmmaRegAddressRegRegPred.apply),
    FormDecoder(
      Definitions3.QgmmaRegAddressRegRegPredReg,
      Register,
      Address,
      Register,
      Register,
      Predicate,
      Register
    )(Form.QgmmaRegAddressRegRegPredReg.apply),
    FormDecoder(
      Definitions3.QgmmaRegAddressRegRegPredRegImm,
      Register,
      Address,
      Register,
      Register,
      Predicate,
      Register,
      Immediate
    )(Form.QgmmaRegAddressRegRegPredRegImm.apply),
    FormDecoder(
      Definitions3.QgmmaRegAddressRegRegReg,
      Register,
      Address,
      Register,
      Register,
      Register
    )(Form.QgmmaRegAddressRegRegReg.apply),
    FormDecoder(
      Definitions3.QgmmaRegAddressRegRegRegImm,
      Register,
      Address,
      Register,
      Register,
      Register,
      Immediate
    )(Form.QgmmaRegAddressRegRegRegImm.apply),
    FormDecoder(
      Definitions3.QgmmaRegRegAddressReg,
      Register,
      Register,
      Address,
      Register
    )(Form.QgmmaRegRegAddressReg.apply),
    FormDecoder(
      Definitions3.QgmmaRegRegAddressRegPred,
      Register,
      Register,
      Address,
      Register,
      Predicate
    )(Form.QgmmaRegRegAddressRegPred.apply),
    FormDecoder(
      Definitions3.QgmmaRegRegAddressRegPredReg,
      Register,
      Register,
      Address,
      Register,
      Predicate,
      Register
    )(Form.QgmmaRegRegAddressRegPredReg.apply),
    FormDecoder(
      Definitions3.QgmmaRegRegAddressRegPredRegImm,
      Register,
      Register,
      Address,
      Register,
      Predicate,
      Register,
      Immediate
    )(Form.QgmmaRegRegAddressRegPredRegImm.apply),
    FormDecoder(
      Definitions3.QgmmaRegRegAddressRegReg,
      Register,
      Register,
      Address,
      Register,
      Register
    )(Form.QgmmaRegRegAddressRegReg.apply),
    FormDecoder(
      Definitions3.QgmmaRegRegAddressRegRegImm,
      Register,
      Register,
      Address,
      Register,
      Register,
      Immediate
    )(Form.QgmmaRegRegAddressRegRegImm.apply),
    FormDecoder(Definitions3.QmmaRegRegRegReg, Register, Register, Register, Register)(Form.QmmaRegRegRegReg.apply),
    FormDecoder(
      Definitions3.QmmaRegRegRegRegPred,
      Register,
      Register,
      Register,
      Register,
      Predicate
    )(Form.QmmaRegRegRegRegPred.apply),
    FormDecoder(
      Definitions3.QmmaRegRegRegRegPredRegImm,
      Register,
      Register,
      Register,
      Register,
      Predicate,
      Register,
      Immediate
    )(Form.QmmaRegRegRegRegPredRegImm.apply),
    FormDecoder(
      Definitions3.QmmaRegRegRegRegPredRegRegReg,
      Register,
      Register,
      Register,
      Register,
      Predicate,
      Register,
      Register,
      Register
    )(Form.QmmaRegRegRegRegPredRegRegReg.apply),
    FormDecoder(
      Definitions3.QmmaRegRegRegRegPredRegRegRegImm,
      Register,
      Register,
      Register,
      Register,
      Predicate,
      Register,
      Register,
      Register,
      Immediate
    )(Form.QmmaRegRegRegRegPredRegRegRegImm.apply),
    FormDecoder(
      Definitions3.QmmaRegRegRegRegRegImm,
      Register,
      Register,
      Register,
      Register,
      Register,
      Immediate
    )(Form.QmmaRegRegRegRegRegImm.apply),
    FormDecoder(
      Definitions3.QmmaRegRegRegRegRegRegReg,
      Register,
      Register,
      Register,
      Register,
      Register,
      Register,
      Register
    )(Form.QmmaRegRegRegRegRegRegReg.apply),
    FormDecoder(
      Definitions3.QmmaRegRegRegRegRegRegRegImm,
      Register,
      Register,
      Register,
      Register,
      Register,
      Register,
      Register,
      Immediate
    )(Form.QmmaRegRegRegRegRegRegRegImm.apply),
    FormDecoder(Definitions3.Qmul4RegRegImm, Register, Register, Immediate)(Form.Qmul4RegRegImm.apply),
    FormDecoder(Definitions3.Qmul4RegRegReg, Register, Register, Register)(Form.Qmul4RegRegReg.apply),
    FormDecoder(Definitions3.QspcPredAddress, Predicate, Address)(Form.QspcPredAddress.apply),
    FormDecoder(Definitions3.QspcPredRegAddress, Predicate, Register, Address)(Form.QspcPredRegAddress.apply),
    FormDecoder(Definitions3.QspcRegAddress, Register, Address)(Form.QspcRegAddress.apply),
    FormDecoder(Definitions3.R2bImmReg, Immediate, Register)(Form.R2bImmReg.apply),
    FormDecoder(Definitions3.R2pReg, Register)(Form.R2pReg.apply),
    FormDecoder(Definitions3.R2pRegConst, Register, ConstantBank)(Form.R2pRegConst.apply),
    FormDecoder(Definitions3.R2pRegImm, Register, Immediate)(Form.R2pRegImm.apply),
    FormDecoder(Definitions3.R2pRegReg, Register, Register)(Form.R2pRegReg.apply),
    FormDecoder(Definitions3.R2pSymbolReg, Symbol, Register)(Form.R2pSymbolReg.apply),
    FormDecoder(
      Definitions3.R2pSymbolRegAddressImm,
      Symbol,
      Register,
      Address,
      Immediate
    )(Form.R2pSymbolRegAddressImm.apply),
    FormDecoder(Definitions3.R2pSymbolRegConst, Symbol, Register, ConstantBank)(Form.R2pSymbolRegConst.apply),
    FormDecoder(Definitions3.R2pSymbolRegImm, Symbol, Register, Immediate)(Form.R2pSymbolRegImm.apply),
    FormDecoder(Definitions3.R2pSymbolRegReg, Symbol, Register, Register)(Form.R2pSymbolRegReg.apply),
    FormDecoder(Definitions3.R2urPredRegReg, Predicate, Register, Register)(Form.R2urPredRegReg.apply),
    FormDecoder(Definitions3.R2urRegReg, Register, Register)(Form.R2urRegReg.apply),
    FormDecoder(Definitions3.RamNoOperands)(Form.RamNoOperands.apply),
    FormDecoder(Definitions3.RedAddressReg, Address, Register)(Form.RedAddressReg.apply),
    FormDecoder(Definitions3.RedasAddressReg, Address, Register)(Form.RedasAddressReg.apply),
    FormDecoder(Definitions3.RedgAddressReg, Address, Register)(Form.RedgAddressReg.apply),
    FormDecoder(Definitions3.RedsAddressReg, Address, Register)(Form.RedsAddressReg.apply),
    FormDecoder(Definitions3.ReduxRegReg, Register, Register)(Form.ReduxRegReg.apply),
    FormDecoder(Definitions3.RetNoOperands)(Form.RetNoOperands.apply),
    FormDecoder(Definitions3.RetPredReg, Predicate, Register)(Form.RetPredReg.apply),
    FormDecoder(Definitions3.RetPredRegImm, Predicate, Register, Immediate)(Form.RetPredRegImm.apply),
    FormDecoder(Definitions3.RetReg, Register)(Form.RetReg.apply),
    FormDecoder(Definitions3.RetRegImm, Register, Immediate)(Form.RetRegImm.apply),
    FormDecoder(Definitions3.RpcmovAddressImm, Address, Immediate)(Form.RpcmovAddressImm.apply),
    FormDecoder(Definitions3.RpcmovConst, ConstantBank)(Form.RpcmovConst.apply),
    FormDecoder(Definitions3.RpcmovImm, Immediate)(Form.RpcmovImm.apply),
    FormDecoder(Definitions3.RpcmovReg, Register)(Form.RpcmovReg.apply),
    FormDecoder(Definitions3.RroRegConst, Register, ConstantBank)(Form.RroRegConst.apply),
    FormDecoder(Definitions3.RroRegImm, Register, Immediate)(Form.RroRegImm.apply),
    FormDecoder(Definitions3.RroRegReg, Register, Register)(Form.RroRegReg.apply),
    FormDecoder(Definitions3.RttNoOperands)(Form.RttNoOperands.apply),
    FormDecoder(Definitions3.S2rRegSpecial, Register, SpecialRegister)(Form.S2rRegSpecial.apply),
    FormDecoder(Definitions3.S2urRegSpecial, Register, SpecialRegister)(Form.S2urRegSpecial.apply),
    FormDecoder(Definitions3.SamNoOperands)(Form.SamNoOperands.apply),
    FormDecoder(
      Definitions3.ScatterRegRegRegRegImmImm,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Immediate
    )(Form.ScatterRegRegRegRegImmImm.apply),
    FormDecoder(
      Definitions3.SelRegRegAddressImmPred,
      Register,
      Register,
      Address,
      Immediate,
      Predicate
    )(Form.SelRegRegAddressImmPred.apply),
    FormDecoder(
      Definitions3.SelRegRegConstPred,
      Register,
      Register,
      ConstantBank,
      Predicate
    )(Form.SelRegRegConstPred.apply),
    FormDecoder(Definitions3.SelRegRegImmPred, Register, Register, Immediate, Predicate)(Form.SelRegRegImmPred.apply),
    FormDecoder(Definitions3.SelRegRegRegPred, Register, Register, Register, Predicate)(Form.SelRegRegRegPred.apply)
  )
}
