// Generated SASS instruction decoders and checked operand constructors. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.sass
import javax.annotation.processing.Generated

import net.kurobako.gemstone.collection.*
import Instruction.*
import InstructionOperand.*
@Generated(Array("sbt codegen"))
object InstructionDefinitions4 {
  val decoders: Vec[FormDecoder] = Vec(
    FormDecoder(Definitions4.SelRegRegRegPred, Register, Register, Register, Predicate)(Form.SelRegRegRegPred.apply),
    FormDecoder(Definitions4.SetcrsptrReg, Register)(Form.SetcrsptrReg.apply),
    FormDecoder(Definitions4.SetctaidReg, Register)(Form.SetctaidReg.apply),
    FormDecoder(Definitions4.SetlmembaseReg, Register)(Form.SetlmembaseReg.apply),
    FormDecoder(
      Definitions4.SgxtRegRegAddressImm,
      Register,
      Register,
      Address,
      Immediate
    )(Form.SgxtRegRegAddressImm.apply),
    FormDecoder(Definitions4.SgxtRegRegConst, Register, Register, ConstantBank)(Form.SgxtRegRegConst.apply),
    FormDecoder(Definitions4.SgxtRegRegImm, Register, Register, Immediate)(Form.SgxtRegRegImm.apply),
    FormDecoder(Definitions4.SgxtRegRegReg, Register, Register, Register)(Form.SgxtRegRegReg.apply),
    FormDecoder(
      Definitions4.ShfRegRegAddressImmReg,
      Register,
      Register,
      Address,
      Immediate,
      Register
    )(Form.ShfRegRegAddressImmReg.apply),
    FormDecoder(
      Definitions4.ShfRegRegConstReg,
      Register,
      Register,
      ConstantBank,
      Register
    )(Form.ShfRegRegConstReg.apply),
    FormDecoder(Definitions4.ShfRegRegImmReg, Register, Register, Immediate, Register)(Form.ShfRegRegImmReg.apply),
    FormDecoder(
      Definitions4.ShfRegRegRegAddressImm,
      Register,
      Register,
      Register,
      Address,
      Immediate
    )(Form.ShfRegRegRegAddressImm.apply),
    FormDecoder(
      Definitions4.ShfRegRegRegConst,
      Register,
      Register,
      Register,
      ConstantBank
    )(Form.ShfRegRegRegConst.apply),
    FormDecoder(Definitions4.ShfRegRegRegImm, Register, Register, Register, Immediate)(Form.ShfRegRegRegImm.apply),
    FormDecoder(Definitions4.ShfRegRegRegReg, Register, Register, Register, Register)(Form.ShfRegRegRegReg.apply),
    FormDecoder(
      Definitions4.ShflPredRegRegImmImm,
      Predicate,
      Register,
      Register,
      Immediate,
      Immediate
    )(Form.ShflPredRegRegImmImm.apply),
    FormDecoder(
      Definitions4.ShflPredRegRegImmReg,
      Predicate,
      Register,
      Register,
      Immediate,
      Register
    )(Form.ShflPredRegRegImmReg.apply),
    FormDecoder(
      Definitions4.ShflPredRegRegRegImm,
      Predicate,
      Register,
      Register,
      Register,
      Immediate
    )(Form.ShflPredRegRegRegImm.apply),
    FormDecoder(
      Definitions4.ShflPredRegRegRegReg,
      Predicate,
      Register,
      Register,
      Register,
      Register
    )(Form.ShflPredRegRegRegReg.apply),
    FormDecoder(Definitions4.ShintImmImm, Immediate, Immediate)(Form.ShintImmImm.apply),
    FormDecoder(
      Definitions4.ShintImmImmImmImmImmImmImmImmImmImmImmImmImmImm,
      Immediate,
      Immediate,
      Immediate,
      Immediate,
      Immediate,
      Immediate,
      Immediate,
      Immediate,
      Immediate,
      Immediate,
      Immediate,
      Immediate,
      Immediate,
      Immediate
    )(Form.ShintImmImmImmImmImmImmImmImmImmImmImmImmImmImm.apply),
    FormDecoder(Definitions4.ShlRegImmReg, Register, Immediate, Register)(Form.ShlRegImmReg.apply),
    FormDecoder(
      Definitions4.ShlRegRegAddressImm,
      Register,
      Register,
      Address,
      Immediate
    )(Form.ShlRegRegAddressImm.apply),
    FormDecoder(Definitions4.ShlRegRegConst, Register, Register, ConstantBank)(Form.ShlRegRegConst.apply),
    FormDecoder(Definitions4.ShlRegRegImm, Register, Register, Immediate)(Form.ShlRegRegImm.apply),
    FormDecoder(Definitions4.ShlRegRegReg, Register, Register, Register)(Form.ShlRegRegReg.apply),
    FormDecoder(
      Definitions4.ShrRegRegAddressImm,
      Register,
      Register,
      Address,
      Immediate
    )(Form.ShrRegRegAddressImm.apply),
    FormDecoder(Definitions4.ShrRegRegConst, Register, Register, ConstantBank)(Form.ShrRegRegConst.apply),
    FormDecoder(Definitions4.ShrRegRegImm, Register, Register, Immediate)(Form.ShrRegRegImm.apply),
    FormDecoder(Definitions4.ShrRegRegReg, Register, Register, Register)(Form.ShrRegRegReg.apply),
    FormDecoder(
      Definitions4.SpmetadataRegRegRegImmImmImm,
      Register,
      Register,
      Register,
      Immediate,
      Immediate,
      Immediate
    )(Form.SpmetadataRegRegRegImmImmImm.apply),
    FormDecoder(Definitions4.SpmetadataRegRegRegImmImmImm, Register, Register, Immediate, Immediate, Immediate)(
      (p0, p1, p2, p3, p4) =>
        Form.SpmetadataRegRegRegImmImmImm.apply(
          dst = p0,
          srcB = p1,
          srcC = InstructionOperand.Register.unsafe(OperandQueries.Zero),
          vecidx = p2,
          mask = p3,
          destidx = p4
        )
    ),
    FormDecoder(Definitions4.SsyConst, ConstantBank)(Form.SsyConst.apply),
    FormDecoder(Definitions4.SsyImm, Immediate)(Form.SsyImm.apply),
    FormDecoder(Definitions4.StAddressReg, Address, Register)(Form.StAddressReg.apply),
    FormDecoder(Definitions4.StAddressRegPred, Address, Register, Predicate)(Form.StAddressRegPred.apply),
    FormDecoder(Definitions4.StasAddressReg, Address, Register)(Form.StasAddressReg.apply),
    FormDecoder(Definitions4.StgAddressReg, Address, Register)(Form.StgAddressReg.apply),
    FormDecoder(Definitions4.StgAddressRegReg, Address, Register, Register)(Form.StgAddressRegReg.apply),
    FormDecoder(
      Definitions4.StgAddressRegRegImm,
      Address,
      Register,
      Register,
      Immediate
    )(Form.StgAddressRegRegImm.apply),
    FormDecoder(Definitions4.StlAddressReg, Address, Register)(Form.StlAddressReg.apply),
    FormDecoder(Definitions4.StpImm, Immediate)(Form.StpImm.apply),
    FormDecoder(Definitions4.StsAddressReg, Address, Register)(Form.StsAddressReg.apply),
    FormDecoder(Definitions4.StsculPredAddressReg, Predicate, Address, Register)(Form.StsculPredAddressReg.apply),
    FormDecoder(Definitions4.StsmAddressReg, Address, Register)(Form.StsmAddressReg.apply),
    FormDecoder(Definitions4.SttAddressReg, Address, Register)(Form.SttAddressReg.apply),
    FormDecoder(Definitions4.SttmAddressReg, Address, Register)(Form.SttmAddressReg.apply),
    FormDecoder(
      Definitions4.SuatomPredRegAddressRegImm,
      Predicate,
      Register,
      Address,
      Register,
      Immediate
    )(Form.SuatomPredRegAddressRegImm.apply),
    FormDecoder(
      Definitions4.SuatomPredRegAddressRegImmImm,
      Predicate,
      Register,
      Address,
      Register,
      Immediate,
      Immediate
    )(Form.SuatomPredRegAddressRegImmImm.apply),
    FormDecoder(
      Definitions4.SuatomPredRegAddressRegImmImmImm,
      Predicate,
      Register,
      Address,
      Register,
      Immediate,
      Immediate,
      Immediate
    )(Form.SuatomPredRegAddressRegImmImmImm.apply),
    FormDecoder(
      Definitions4.SuatomPredRegAddressRegReg,
      Predicate,
      Register,
      Address,
      Register,
      Register
    )(Form.SuatomPredRegAddressRegReg.apply),
    FormDecoder(
      Definitions4.SuatomPredRegAddressRegRegImm,
      Predicate,
      Register,
      Address,
      Register,
      Register,
      Immediate
    )(Form.SuatomPredRegAddressRegRegImm.apply),
    FormDecoder(
      Definitions4.SuatomPredRegAddressRegRegReg,
      Predicate,
      Register,
      Address,
      Register,
      Register,
      Register
    )(Form.SuatomPredRegAddressRegRegReg.apply),
    FormDecoder(
      Definitions4.SuatomPredRegAddressRegRegRegImm,
      Predicate,
      Register,
      Address,
      Register,
      Register,
      Register,
      Immediate
    )(Form.SuatomPredRegAddressRegRegRegImm.apply),
    FormDecoder(
      Definitions4.SuatomPredRegAddressRegRegRegReg,
      Predicate,
      Register,
      Address,
      Register,
      Register,
      Register,
      Register
    )(Form.SuatomPredRegAddressRegRegRegReg.apply),
    FormDecoder(
      Definitions4.SuatomRegAddressRegImm,
      Register,
      Address,
      Register,
      Immediate
    )(Form.SuatomRegAddressRegImm.apply),
    FormDecoder(
      Definitions4.SuatomRegAddressRegImmImm,
      Register,
      Address,
      Register,
      Immediate,
      Immediate
    )(Form.SuatomRegAddressRegImmImm.apply),
    FormDecoder(
      Definitions4.SuatomRegAddressRegImmImmImm,
      Register,
      Address,
      Register,
      Immediate,
      Immediate,
      Immediate
    )(Form.SuatomRegAddressRegImmImmImm.apply),
    FormDecoder(
      Definitions4.SuatomRegAddressRegReg,
      Register,
      Address,
      Register,
      Register
    )(Form.SuatomRegAddressRegReg.apply),
    FormDecoder(
      Definitions4.SuatomRegAddressRegRegImm,
      Register,
      Address,
      Register,
      Register,
      Immediate
    )(Form.SuatomRegAddressRegRegImm.apply),
    FormDecoder(
      Definitions4.SuatomRegAddressRegRegReg,
      Register,
      Address,
      Register,
      Register,
      Register
    )(Form.SuatomRegAddressRegRegReg.apply),
    FormDecoder(
      Definitions4.SuatomRegAddressRegRegRegImm,
      Register,
      Address,
      Register,
      Register,
      Register,
      Immediate
    )(Form.SuatomRegAddressRegRegRegImm.apply),
    FormDecoder(
      Definitions4.SuatomRegAddressRegRegRegReg,
      Register,
      Address,
      Register,
      Register,
      Register,
      Register
    )(Form.SuatomRegAddressRegRegRegReg.apply),
    FormDecoder(
      Definitions4.SubfmPredRegRegConstReg,
      Predicate,
      Register,
      Register,
      ConstantBank,
      Register
    )(Form.SubfmPredRegRegConstReg.apply),
    FormDecoder(
      Definitions4.SubfmPredRegRegImmReg,
      Predicate,
      Register,
      Register,
      Immediate,
      Register
    )(Form.SubfmPredRegRegImmReg.apply),
    FormDecoder(
      Definitions4.SubfmPredRegRegRegConst,
      Predicate,
      Register,
      Register,
      Register,
      ConstantBank
    )(Form.SubfmPredRegRegRegConst.apply),
    FormDecoder(
      Definitions4.SubfmPredRegRegRegReg,
      Predicate,
      Register,
      Register,
      Register,
      Register
    )(Form.SubfmPredRegRegRegReg.apply),
    FormDecoder(
      Definitions4.SuclampPredRegRegConstImm,
      Predicate,
      Register,
      Register,
      ConstantBank,
      Immediate
    )(Form.SuclampPredRegRegConstImm.apply),
    FormDecoder(
      Definitions4.SuclampPredRegRegImmImm,
      Predicate,
      Register,
      Register,
      Immediate,
      Immediate
    )(Form.SuclampPredRegRegImmImm.apply),
    FormDecoder(
      Definitions4.SuclampPredRegRegRegImm,
      Predicate,
      Register,
      Register,
      Register,
      Immediate
    )(Form.SuclampPredRegRegRegImm.apply),
    FormDecoder(
      Definitions4.SueauRegRegConstReg,
      Register,
      Register,
      ConstantBank,
      Register
    )(Form.SueauRegRegConstReg.apply),
    FormDecoder(Definitions4.SueauRegRegImmReg, Register, Register, Immediate, Register)(Form.SueauRegRegImmReg.apply),
    FormDecoder(
      Definitions4.SueauRegRegRegConst,
      Register,
      Register,
      Register,
      ConstantBank
    )(Form.SueauRegRegRegConst.apply),
    FormDecoder(Definitions4.SueauRegRegRegReg, Register, Register, Register, Register)(Form.SueauRegRegRegReg.apply),
    FormDecoder(
      Definitions4.SuldPredRegAddressImm,
      Predicate,
      Register,
      Address,
      Immediate
    )(Form.SuldPredRegAddressImm.apply),
    FormDecoder(
      Definitions4.SuldPredRegAddressImmImm,
      Predicate,
      Register,
      Address,
      Immediate,
      Immediate
    )(Form.SuldPredRegAddressImmImm.apply),
    FormDecoder(
      Definitions4.SuldPredRegAddressImmImmImm,
      Predicate,
      Register,
      Address,
      Immediate,
      Immediate,
      Immediate
    )(Form.SuldPredRegAddressImmImmImm.apply),
    FormDecoder(
      Definitions4.SuldPredRegAddressReg,
      Predicate,
      Register,
      Address,
      Register
    )(Form.SuldPredRegAddressReg.apply),
    FormDecoder(
      Definitions4.SuldPredRegAddressRegImm,
      Predicate,
      Register,
      Address,
      Register,
      Immediate
    )(Form.SuldPredRegAddressRegImm.apply),
    FormDecoder(
      Definitions4.SuldPredRegAddressRegReg,
      Predicate,
      Register,
      Address,
      Register,
      Register
    )(Form.SuldPredRegAddressRegReg.apply),
    FormDecoder(
      Definitions4.SuldPredRegAddressRegRegImm,
      Predicate,
      Register,
      Address,
      Register,
      Register,
      Immediate
    )(Form.SuldPredRegAddressRegRegImm.apply),
    FormDecoder(
      Definitions4.SuldPredRegAddressRegRegReg,
      Predicate,
      Register,
      Address,
      Register,
      Register,
      Register
    )(Form.SuldPredRegAddressRegRegReg.apply),
    FormDecoder(Definitions4.SuldRegAddressImm, Register, Address, Immediate)(Form.SuldRegAddressImm.apply),
    FormDecoder(
      Definitions4.SuldRegAddressImmImm,
      Register,
      Address,
      Immediate,
      Immediate
    )(Form.SuldRegAddressImmImm.apply),
    FormDecoder(
      Definitions4.SuldRegAddressImmImmImm,
      Register,
      Address,
      Immediate,
      Immediate,
      Immediate
    )(Form.SuldRegAddressImmImmImm.apply),
    FormDecoder(Definitions4.SuldRegAddressReg, Register, Address, Register)(Form.SuldRegAddressReg.apply),
    FormDecoder(
      Definitions4.SuldRegAddressRegImm,
      Register,
      Address,
      Register,
      Immediate
    )(Form.SuldRegAddressRegImm.apply),
    FormDecoder(
      Definitions4.SuldRegAddressRegReg,
      Register,
      Address,
      Register,
      Register
    )(Form.SuldRegAddressRegReg.apply),
    FormDecoder(
      Definitions4.SuldRegAddressRegRegImm,
      Register,
      Address,
      Register,
      Register,
      Immediate
    )(Form.SuldRegAddressRegRegImm.apply),
    FormDecoder(
      Definitions4.SuldRegAddressRegRegReg,
      Register,
      Address,
      Register,
      Register,
      Register
    )(Form.SuldRegAddressRegRegReg.apply),
    FormDecoder(
      Definitions4.SuldgaRegAddressConstPred,
      Register,
      Address,
      ConstantBank,
      Predicate
    )(Form.SuldgaRegAddressConstPred.apply),
    FormDecoder(
      Definitions4.SuldgaRegAddressRegPred,
      Register,
      Address,
      Register,
      Predicate
    )(Form.SuldgaRegAddressRegPred.apply),
    FormDecoder(
      Definitions4.SuqueryPredRegAddressImmImm,
      Predicate,
      Register,
      Address,
      Immediate,
      Immediate
    )(Form.SuqueryPredRegAddressImmImm.apply),
    FormDecoder(
      Definitions4.SuqueryPredRegAddressImmImmImm,
      Predicate,
      Register,
      Address,
      Immediate,
      Immediate,
      Immediate
    )(Form.SuqueryPredRegAddressImmImmImm.apply),
    FormDecoder(
      Definitions4.SuqueryPredRegAddressReg,
      Predicate,
      Register,
      Address,
      Register
    )(Form.SuqueryPredRegAddressReg.apply),
    FormDecoder(
      Definitions4.SuqueryPredRegAddressRegImm,
      Predicate,
      Register,
      Address,
      Register,
      Immediate
    )(Form.SuqueryPredRegAddressRegImm.apply),
    FormDecoder(
      Definitions4.SuqueryPredRegAddressRegReg,
      Predicate,
      Register,
      Address,
      Register,
      Register
    )(Form.SuqueryPredRegAddressRegReg.apply),
    FormDecoder(
      Definitions4.SuqueryPredRegAddressRegRegImm,
      Predicate,
      Register,
      Address,
      Register,
      Register,
      Immediate
    )(Form.SuqueryPredRegAddressRegRegImm.apply),
    FormDecoder(
      Definitions4.SuqueryPredRegAddressRegRegReg,
      Predicate,
      Register,
      Address,
      Register,
      Register,
      Register
    )(Form.SuqueryPredRegAddressRegRegReg.apply),
    FormDecoder(
      Definitions4.SuqueryRegAddressImmImm,
      Register,
      Address,
      Immediate,
      Immediate
    )(Form.SuqueryRegAddressImmImm.apply),
    FormDecoder(
      Definitions4.SuqueryRegAddressImmImmImm,
      Register,
      Address,
      Immediate,
      Immediate,
      Immediate
    )(Form.SuqueryRegAddressImmImmImm.apply),
    FormDecoder(Definitions4.SuqueryRegAddressReg, Register, Address, Register)(Form.SuqueryRegAddressReg.apply),
    FormDecoder(
      Definitions4.SuqueryRegAddressRegImm,
      Register,
      Address,
      Register,
      Immediate
    )(Form.SuqueryRegAddressRegImm.apply),
    FormDecoder(
      Definitions4.SuqueryRegAddressRegReg,
      Register,
      Address,
      Register,
      Register
    )(Form.SuqueryRegAddressRegReg.apply),
    FormDecoder(
      Definitions4.SuqueryRegAddressRegRegImm,
      Register,
      Address,
      Register,
      Register,
      Immediate
    )(Form.SuqueryRegAddressRegRegImm.apply),
    FormDecoder(
      Definitions4.SuqueryRegAddressRegRegReg,
      Register,
      Address,
      Register,
      Register,
      Register
    )(Form.SuqueryRegAddressRegRegReg.apply),
    FormDecoder(Definitions4.SuredAddressRegImm, Address, Register, Immediate)(Form.SuredAddressRegImm.apply),
    FormDecoder(
      Definitions4.SuredAddressRegImmImm,
      Address,
      Register,
      Immediate,
      Immediate
    )(Form.SuredAddressRegImmImm.apply),
    FormDecoder(
      Definitions4.SuredAddressRegImmImmImm,
      Address,
      Register,
      Immediate,
      Immediate,
      Immediate
    )(Form.SuredAddressRegImmImmImm.apply),
    FormDecoder(Definitions4.SuredAddressRegReg, Address, Register, Register)(Form.SuredAddressRegReg.apply),
    FormDecoder(
      Definitions4.SuredAddressRegRegImm,
      Address,
      Register,
      Register,
      Immediate
    )(Form.SuredAddressRegRegImm.apply),
    FormDecoder(
      Definitions4.SuredAddressRegRegReg,
      Address,
      Register,
      Register,
      Register
    )(Form.SuredAddressRegRegReg.apply),
    FormDecoder(
      Definitions4.SuredAddressRegRegRegImm,
      Address,
      Register,
      Register,
      Register,
      Immediate
    )(Form.SuredAddressRegRegRegImm.apply),
    FormDecoder(
      Definitions4.SuredAddressRegRegRegReg,
      Address,
      Register,
      Register,
      Register,
      Register
    )(Form.SuredAddressRegRegRegReg.apply),
    FormDecoder(Definitions4.SustAddressRegImm, Address, Register, Immediate)(Form.SustAddressRegImm.apply),
    FormDecoder(
      Definitions4.SustAddressRegImmImm,
      Address,
      Register,
      Immediate,
      Immediate
    )(Form.SustAddressRegImmImm.apply),
    FormDecoder(
      Definitions4.SustAddressRegImmImmImm,
      Address,
      Register,
      Immediate,
      Immediate,
      Immediate
    )(Form.SustAddressRegImmImmImm.apply),
    FormDecoder(Definitions4.SustAddressRegReg, Address, Register, Register)(Form.SustAddressRegReg.apply),
    FormDecoder(
      Definitions4.SustAddressRegRegImm,
      Address,
      Register,
      Register,
      Immediate
    )(Form.SustAddressRegRegImm.apply),
    FormDecoder(
      Definitions4.SustAddressRegRegReg,
      Address,
      Register,
      Register,
      Register
    )(Form.SustAddressRegRegReg.apply),
    FormDecoder(
      Definitions4.SustAddressRegRegRegImm,
      Address,
      Register,
      Register,
      Register,
      Immediate
    )(Form.SustAddressRegRegRegImm.apply),
    FormDecoder(
      Definitions4.SustAddressRegRegRegReg,
      Address,
      Register,
      Register,
      Register,
      Register
    )(Form.SustAddressRegRegRegReg.apply),
    FormDecoder(
      Definitions4.SustgaAddressConstRegPred,
      Address,
      ConstantBank,
      Register,
      Predicate
    )(Form.SustgaAddressConstRegPred.apply),
    FormDecoder(
      Definitions4.SustgaAddressRegRegPred,
      Address,
      Register,
      Register,
      Predicate
    )(Form.SustgaAddressRegRegPred.apply),
    FormDecoder(Definitions4.SyncNoOperands)(Form.SyncNoOperands.apply),
    FormDecoder(Definitions4.SyncsNoOperands)(Form.SyncsNoOperands.apply),
    FormDecoder(Definitions4.SyncsAddress, Address)(Form.SyncsAddress.apply),
    FormDecoder(Definitions4.SyncsAddressReg, Address, Register)(Form.SyncsAddressReg.apply),
    FormDecoder(Definitions4.SyncsPredAddressReg, Predicate, Address, Register)(Form.SyncsPredAddressReg.apply),
    FormDecoder(Definitions4.SyncsRegAddress, Register, Address)(Form.SyncsRegAddress.apply),
    FormDecoder(Definitions4.SyncsRegAddressReg, Register, Address, Register)(Form.SyncsRegAddressReg.apply),
    FormDecoder(
      Definitions4.SyncsRegAddressRegReg,
      Register,
      Address,
      Register,
      Register
    )(Form.SyncsRegAddressRegReg.apply),
    FormDecoder(
      Definitions4.TexPredRegRegRegImmImmSymbol,
      Predicate,
      Register,
      Register,
      Register,
      Immediate,
      Immediate,
      Symbol
    )(Form.TexPredRegRegRegImmImmSymbol.apply),
    FormDecoder(Definitions4.TexPredRegRegRegImmImmSymbol, Predicate, Register, Register, Immediate, Immediate, Symbol)(
      (p0, p1, p2, p3, p4, p5) =>
        Form.TexPredRegRegRegImmImmSymbol.apply(
          dstPredicate = p0,
          dst = p1,
          srcA = p2,
          srcB = InstructionOperand.Register.unsafe(OperandQueries.Zero),
          tid = p3,
          smp = p4,
          paramA = p5
        )
    ),
    FormDecoder(
      Definitions4.TexPredRegRegRegImmImmSymbolImm,
      Predicate,
      Register,
      Register,
      Register,
      Immediate,
      Immediate,
      Symbol,
      Immediate
    )(Form.TexPredRegRegRegImmImmSymbolImm.apply),
    FormDecoder(
      Definitions4.TexPredRegRegRegImmImmSymbolImm,
      Predicate,
      Register,
      Register,
      Immediate,
      Immediate,
      Symbol,
      Immediate
    )((p0, p1, p2, p3, p4, p5, p6) =>
      Form.TexPredRegRegRegImmImmSymbolImm.apply(
        dstPredicate = p0,
        dst = p1,
        srcA = p2,
        srcB = InstructionOperand.Register.unsafe(OperandQueries.Zero),
        tid = p3,
        smp = p4,
        paramA = p5,
        wmsk = p6
      )
    ),
    FormDecoder(
      Definitions4.TexPredRegRegRegImmSymbol,
      Predicate,
      Register,
      Register,
      Register,
      Immediate,
      Symbol
    )(Form.TexPredRegRegRegImmSymbol.apply),
    FormDecoder(Definitions4.TexPredRegRegRegImmSymbol, Predicate, Register, Register, Immediate, Symbol)(
      (p0, p1, p2, p3, p4) =>
        Form.TexPredRegRegRegImmSymbol.apply(
          dstPredicate = p0,
          dst = p1,
          srcA = p2,
          srcB = InstructionOperand.Register.unsafe(OperandQueries.Zero),
          tid = p3,
          paramA = p4
        )
    ),
    FormDecoder(
      Definitions4.TexPredRegRegRegImmSymbolImm,
      Predicate,
      Register,
      Register,
      Register,
      Immediate,
      Symbol,
      Immediate
    )(Form.TexPredRegRegRegImmSymbolImm.apply),
    FormDecoder(Definitions4.TexPredRegRegRegImmSymbolImm, Predicate, Register, Register, Immediate, Symbol, Immediate)(
      (p0, p1, p2, p3, p4, p5) =>
        Form.TexPredRegRegRegImmSymbolImm.apply(
          dstPredicate = p0,
          dst = p1,
          srcA = p2,
          srcB = InstructionOperand.Register.unsafe(OperandQueries.Zero),
          tid = p3,
          paramA = p4,
          wmsk = p5
        )
    ),
    FormDecoder(
      Definitions4.TexPredRegRegRegRegImmImmImmSymbol,
      Predicate,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Immediate,
      Immediate,
      Symbol
    )(Form.TexPredRegRegRegRegImmImmImmSymbol.apply),
    FormDecoder(
      Definitions4.TexPredRegRegRegRegImmImmImmSymbolImm,
      Predicate,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Immediate,
      Immediate,
      Symbol,
      Immediate
    )(Form.TexPredRegRegRegRegImmImmImmSymbolImm.apply),
    FormDecoder(
      Definitions4.TexPredRegRegRegRegImmImmSymbol,
      Predicate,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Immediate,
      Symbol
    )(Form.TexPredRegRegRegRegImmImmSymbol.apply),
    FormDecoder(
      Definitions4.TexPredRegRegRegRegImmImmSymbolImm,
      Predicate,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Immediate,
      Symbol,
      Immediate
    )(Form.TexPredRegRegRegRegImmImmSymbolImm.apply),
    FormDecoder(
      Definitions4.TexPredRegRegRegRegRegImmSymbol,
      Predicate,
      Register,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Symbol
    )(Form.TexPredRegRegRegRegRegImmSymbol.apply),
    FormDecoder(
      Definitions4.TexPredRegRegRegRegRegImmSymbolImm,
      Predicate,
      Register,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Symbol,
      Immediate
    )(Form.TexPredRegRegRegRegRegImmSymbolImm.apply),
    FormDecoder(
      Definitions4.TexPredRegRegRegRegRegRegImmSymbol,
      Predicate,
      Register,
      Register,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Symbol
    )(Form.TexPredRegRegRegRegRegRegImmSymbol.apply),
    FormDecoder(
      Definitions4.TexPredRegRegRegRegRegRegImmSymbolImm,
      Predicate,
      Register,
      Register,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Symbol,
      Immediate
    )(Form.TexPredRegRegRegRegRegRegImmSymbolImm.apply),
    FormDecoder(
      Definitions4.TexPredRegRegRegRegRegRegSymbol,
      Predicate,
      Register,
      Register,
      Register,
      Register,
      Register,
      Register,
      Symbol
    )(Form.TexPredRegRegRegRegRegRegSymbol.apply),
    FormDecoder(
      Definitions4.TexPredRegRegRegRegRegRegSymbolImm,
      Predicate,
      Register,
      Register,
      Register,
      Register,
      Register,
      Register,
      Symbol,
      Immediate
    )(Form.TexPredRegRegRegRegRegRegSymbolImm.apply),
    FormDecoder(
      Definitions4.TexPredRegRegRegRegRegSymbol,
      Predicate,
      Register,
      Register,
      Register,
      Register,
      Register,
      Symbol
    )(Form.TexPredRegRegRegRegRegSymbol.apply),
    FormDecoder(
      Definitions4.TexPredRegRegRegRegRegSymbolImm,
      Predicate,
      Register,
      Register,
      Register,
      Register,
      Register,
      Symbol,
      Immediate
    )(Form.TexPredRegRegRegRegRegSymbolImm.apply),
    FormDecoder(
      Definitions4.TexPredRegRegRegRegSymbol,
      Predicate,
      Register,
      Register,
      Register,
      Register,
      Symbol
    )(Form.TexPredRegRegRegRegSymbol.apply),
    FormDecoder(
      Definitions4.TexPredRegRegRegRegSymbolImm,
      Predicate,
      Register,
      Register,
      Register,
      Register,
      Symbol,
      Immediate
    )(Form.TexPredRegRegRegRegSymbolImm.apply),
    FormDecoder(
      Definitions4.TexRegRegRegImmImmSymbol,
      Register,
      Register,
      Register,
      Immediate,
      Immediate,
      Symbol
    )(Form.TexRegRegRegImmImmSymbol.apply),
    FormDecoder(Definitions4.TexRegRegRegImmImmSymbol, Register, Register, Immediate, Immediate, Symbol)(
      (p0, p1, p2, p3, p4) =>
        Form.TexRegRegRegImmImmSymbol.apply(
          dst = p0,
          srcA = p1,
          srcB = InstructionOperand.Register.unsafe(OperandQueries.Zero),
          tid = p2,
          smp = p3,
          paramA = p4
        )
    ),
    FormDecoder(
      Definitions4.TexRegRegRegImmImmSymbolImm,
      Register,
      Register,
      Register,
      Immediate,
      Immediate,
      Symbol,
      Immediate
    )(Form.TexRegRegRegImmImmSymbolImm.apply),
    FormDecoder(Definitions4.TexRegRegRegImmImmSymbolImm, Register, Register, Immediate, Immediate, Symbol, Immediate)(
      (p0, p1, p2, p3, p4, p5) =>
        Form.TexRegRegRegImmImmSymbolImm.apply(
          dst = p0,
          srcA = p1,
          srcB = InstructionOperand.Register.unsafe(OperandQueries.Zero),
          tid = p2,
          smp = p3,
          paramA = p4,
          wmsk = p5
        )
    ),
    FormDecoder(
      Definitions4.TexRegRegRegImmSymbol,
      Register,
      Register,
      Register,
      Immediate,
      Symbol
    )(Form.TexRegRegRegImmSymbol.apply),
    FormDecoder(Definitions4.TexRegRegRegImmSymbol, Register, Register, Immediate, Symbol)((p0, p1, p2, p3) =>
      Form.TexRegRegRegImmSymbol.apply(
        dst = p0,
        srcA = p1,
        srcB = InstructionOperand.Register.unsafe(OperandQueries.Zero),
        tid = p2,
        paramA = p3
      )
    ),
    FormDecoder(
      Definitions4.TexRegRegRegImmSymbolImm,
      Register,
      Register,
      Register,
      Immediate,
      Symbol,
      Immediate
    )(Form.TexRegRegRegImmSymbolImm.apply),
    FormDecoder(Definitions4.TexRegRegRegImmSymbolImm, Register, Register, Immediate, Symbol, Immediate)(
      (p0, p1, p2, p3, p4) =>
        Form.TexRegRegRegImmSymbolImm.apply(
          dst = p0,
          srcA = p1,
          srcB = InstructionOperand.Register.unsafe(OperandQueries.Zero),
          tid = p2,
          paramA = p3,
          wmsk = p4
        )
    ),
    FormDecoder(
      Definitions4.TexRegRegRegRegImmImmImmSymbol,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Immediate,
      Immediate,
      Symbol
    )(Form.TexRegRegRegRegImmImmImmSymbol.apply),
    FormDecoder(
      Definitions4.TexRegRegRegRegImmImmImmSymbolImm,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Immediate,
      Immediate,
      Symbol,
      Immediate
    )(Form.TexRegRegRegRegImmImmImmSymbolImm.apply),
    FormDecoder(
      Definitions4.TexRegRegRegRegImmImmSymbol,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Immediate,
      Symbol
    )(Form.TexRegRegRegRegImmImmSymbol.apply),
    FormDecoder(
      Definitions4.TexRegRegRegRegImmImmSymbolImm,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Immediate,
      Symbol,
      Immediate
    )(Form.TexRegRegRegRegImmImmSymbolImm.apply),
    FormDecoder(
      Definitions4.TexRegRegRegRegRegImmSymbol,
      Register,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Symbol
    )(Form.TexRegRegRegRegRegImmSymbol.apply),
    FormDecoder(
      Definitions4.TexRegRegRegRegRegImmSymbolImm,
      Register,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Symbol,
      Immediate
    )(Form.TexRegRegRegRegRegImmSymbolImm.apply),
    FormDecoder(
      Definitions4.TexRegRegRegRegRegRegImmSymbol,
      Register,
      Register,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Symbol
    )(Form.TexRegRegRegRegRegRegImmSymbol.apply),
    FormDecoder(
      Definitions4.TexRegRegRegRegRegRegImmSymbolImm,
      Register,
      Register,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Symbol,
      Immediate
    )(Form.TexRegRegRegRegRegRegImmSymbolImm.apply),
    FormDecoder(
      Definitions4.TexRegRegRegRegRegRegSymbol,
      Register,
      Register,
      Register,
      Register,
      Register,
      Register,
      Symbol
    )(Form.TexRegRegRegRegRegRegSymbol.apply),
    FormDecoder(
      Definitions4.TexRegRegRegRegRegRegSymbolImm,
      Register,
      Register,
      Register,
      Register,
      Register,
      Register,
      Symbol,
      Immediate
    )(Form.TexRegRegRegRegRegRegSymbolImm.apply),
    FormDecoder(
      Definitions4.TexRegRegRegRegRegSymbol,
      Register,
      Register,
      Register,
      Register,
      Register,
      Symbol
    )(Form.TexRegRegRegRegRegSymbol.apply),
    FormDecoder(
      Definitions4.TexRegRegRegRegRegSymbolImm,
      Register,
      Register,
      Register,
      Register,
      Register,
      Symbol,
      Immediate
    )(Form.TexRegRegRegRegRegSymbolImm.apply),
    FormDecoder(
      Definitions4.TexRegRegRegRegSymbol,
      Register,
      Register,
      Register,
      Register,
      Symbol
    )(Form.TexRegRegRegRegSymbol.apply),
    FormDecoder(
      Definitions4.TexRegRegRegRegSymbolImm,
      Register,
      Register,
      Register,
      Register,
      Symbol,
      Immediate
    )(Form.TexRegRegRegRegSymbolImm.apply),
    FormDecoder(Definitions4.TexdepbarImm, Immediate)(Form.TexdepbarImm.apply),
    FormDecoder(
      Definitions4.TexsRegRegRegRegImmSymbolSymbol,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Symbol,
      Symbol
    )(Form.TexsRegRegRegRegImmSymbolSymbol.apply),
    FormDecoder(Definitions4.TexsRegRegRegRegImmSymbolSymbol, Register, Register, Register, Immediate, Symbol, Symbol)(
      (p0, p1, p2, p3, p4, p5) =>
        Form.TexsRegRegRegRegImmSymbolSymbol.apply(
          rd2 = p0,
          dst = p1,
          srcA = p2,
          srcB = InstructionOperand.Register.unsafe(OperandQueries.Zero),
          tid = p3,
          paramA = p4,
          wmsk = p5
        )
    ),
    FormDecoder(
      Definitions4.TldPredRegRegRegImmSymbol,
      Predicate,
      Register,
      Register,
      Register,
      Immediate,
      Symbol
    )(Form.TldPredRegRegRegImmSymbol.apply),
    FormDecoder(Definitions4.TldPredRegRegRegImmSymbol, Predicate, Register, Register, Immediate, Symbol)(
      (p0, p1, p2, p3, p4) =>
        Form.TldPredRegRegRegImmSymbol.apply(
          dstPredicate = p0,
          dst = p1,
          srcA = p2,
          srcB = InstructionOperand.Register.unsafe(OperandQueries.Zero),
          tid = p3,
          paramA = p4
        )
    ),
    FormDecoder(
      Definitions4.TldPredRegRegRegImmSymbolImm,
      Predicate,
      Register,
      Register,
      Register,
      Immediate,
      Symbol,
      Immediate
    )(Form.TldPredRegRegRegImmSymbolImm.apply),
    FormDecoder(Definitions4.TldPredRegRegRegImmSymbolImm, Predicate, Register, Register, Immediate, Symbol, Immediate)(
      (p0, p1, p2, p3, p4, p5) =>
        Form.TldPredRegRegRegImmSymbolImm.apply(
          dstPredicate = p0,
          dst = p1,
          srcA = p2,
          srcB = InstructionOperand.Register.unsafe(OperandQueries.Zero),
          tid = p3,
          paramA = p4,
          wmsk = p5
        )
    ),
    FormDecoder(
      Definitions4.TldPredRegRegRegRegImmImmImmSymbol,
      Predicate,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Immediate,
      Immediate,
      Symbol
    )(Form.TldPredRegRegRegRegImmImmImmSymbol.apply),
    FormDecoder(
      Definitions4.TldPredRegRegRegRegImmImmImmSymbolImm,
      Predicate,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Immediate,
      Immediate,
      Symbol,
      Immediate
    )(Form.TldPredRegRegRegRegImmImmImmSymbolImm.apply),
    FormDecoder(
      Definitions4.TldPredRegRegRegRegImmImmSymbol,
      Predicate,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Immediate,
      Symbol
    )(Form.TldPredRegRegRegRegImmImmSymbol.apply),
    FormDecoder(
      Definitions4.TldPredRegRegRegRegImmImmSymbol,
      Predicate,
      Register,
      Register,
      Register,
      Immediate,
      Immediate,
      Symbol
    )((p0, p1, p2, p3, p4, p5, p6) =>
      Form.TldPredRegRegRegRegImmImmSymbol.apply(
        pu = p0,
        rd2 = p1,
        dst = p2,
        srcA = p3,
        srcB = InstructionOperand.Register.unsafe(OperandQueries.Zero),
        bank5 = p4,
        tsPtrIdx = p5,
        paramA = p6
      )
    ),
    FormDecoder(
      Definitions4.TldPredRegRegRegRegImmImmSymbolImm,
      Predicate,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Immediate,
      Symbol,
      Immediate
    )(Form.TldPredRegRegRegRegImmImmSymbolImm.apply),
    FormDecoder(
      Definitions4.TldPredRegRegRegRegImmImmSymbolImm,
      Predicate,
      Register,
      Register,
      Register,
      Immediate,
      Immediate,
      Symbol,
      Immediate
    )((p0, p1, p2, p3, p4, p5, p6, p7) =>
      Form.TldPredRegRegRegRegImmImmSymbolImm.apply(
        pu = p0,
        rd2 = p1,
        dst = p2,
        srcA = p3,
        srcB = InstructionOperand.Register.unsafe(OperandQueries.Zero),
        bank5 = p4,
        tsPtrIdx = p5,
        paramA = p6,
        wmsk = p7
      )
    ),
    FormDecoder(
      Definitions4.TldPredRegRegRegRegRegImmSymbol,
      Predicate,
      Register,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Symbol
    )(Form.TldPredRegRegRegRegRegImmSymbol.apply),
    FormDecoder(
      Definitions4.TldPredRegRegRegRegRegImmSymbolImm,
      Predicate,
      Register,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Symbol,
      Immediate
    )(Form.TldPredRegRegRegRegRegImmSymbolImm.apply),
    FormDecoder(
      Definitions4.TldPredRegRegRegRegRegRegImmSymbol,
      Predicate,
      Register,
      Register,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Symbol
    )(Form.TldPredRegRegRegRegRegRegImmSymbol.apply),
    FormDecoder(
      Definitions4.TldPredRegRegRegRegRegRegImmSymbolImm,
      Predicate,
      Register,
      Register,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Symbol,
      Immediate
    )(Form.TldPredRegRegRegRegRegRegImmSymbolImm.apply),
    FormDecoder(
      Definitions4.TldPredRegRegRegRegRegRegSymbol,
      Predicate,
      Register,
      Register,
      Register,
      Register,
      Register,
      Register,
      Symbol
    )(Form.TldPredRegRegRegRegRegRegSymbol.apply),
    FormDecoder(
      Definitions4.TldPredRegRegRegRegRegRegSymbolImm,
      Predicate,
      Register,
      Register,
      Register,
      Register,
      Register,
      Register,
      Symbol,
      Immediate
    )(Form.TldPredRegRegRegRegRegRegSymbolImm.apply),
    FormDecoder(
      Definitions4.TldPredRegRegRegRegRegSymbol,
      Predicate,
      Register,
      Register,
      Register,
      Register,
      Register,
      Symbol
    )(Form.TldPredRegRegRegRegRegSymbol.apply),
    FormDecoder(
      Definitions4.TldPredRegRegRegRegRegSymbolImm,
      Predicate,
      Register,
      Register,
      Register,
      Register,
      Register,
      Symbol,
      Immediate
    )(Form.TldPredRegRegRegRegRegSymbolImm.apply),
    FormDecoder(
      Definitions4.TldPredRegRegRegRegSymbol,
      Predicate,
      Register,
      Register,
      Register,
      Register,
      Symbol
    )(Form.TldPredRegRegRegRegSymbol.apply),
    FormDecoder(
      Definitions4.TldPredRegRegRegRegSymbolImm,
      Predicate,
      Register,
      Register,
      Register,
      Register,
      Symbol,
      Immediate
    )(Form.TldPredRegRegRegRegSymbolImm.apply),
    FormDecoder(
      Definitions4.TldRegRegRegImmSymbol,
      Register,
      Register,
      Register,
      Immediate,
      Symbol
    )(Form.TldRegRegRegImmSymbol.apply),
    FormDecoder(Definitions4.TldRegRegRegImmSymbol, Register, Register, Immediate, Symbol)((p0, p1, p2, p3) =>
      Form.TldRegRegRegImmSymbol.apply(
        dst = p0,
        srcA = p1,
        srcB = InstructionOperand.Register.unsafe(OperandQueries.Zero),
        tid = p2,
        paramA = p3
      )
    ),
    FormDecoder(
      Definitions4.TldRegRegRegImmSymbolImm,
      Register,
      Register,
      Register,
      Immediate,
      Symbol,
      Immediate
    )(Form.TldRegRegRegImmSymbolImm.apply),
    FormDecoder(Definitions4.TldRegRegRegImmSymbolImm, Register, Register, Immediate, Symbol, Immediate)(
      (p0, p1, p2, p3, p4) =>
        Form.TldRegRegRegImmSymbolImm.apply(
          dst = p0,
          srcA = p1,
          srcB = InstructionOperand.Register.unsafe(OperandQueries.Zero),
          tid = p2,
          paramA = p3,
          wmsk = p4
        )
    ),
    FormDecoder(
      Definitions4.TldRegRegRegRegImmImmImmSymbol,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Immediate,
      Immediate,
      Symbol
    )(Form.TldRegRegRegRegImmImmImmSymbol.apply),
    FormDecoder(
      Definitions4.TldRegRegRegRegImmImmImmSymbolImm,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Immediate,
      Immediate,
      Symbol,
      Immediate
    )(Form.TldRegRegRegRegImmImmImmSymbolImm.apply),
    FormDecoder(
      Definitions4.TldRegRegRegRegImmImmSymbol,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Immediate,
      Symbol
    )(Form.TldRegRegRegRegImmImmSymbol.apply),
    FormDecoder(Definitions4.TldRegRegRegRegImmImmSymbol, Register, Register, Register, Immediate, Immediate, Symbol)(
      (p0, p1, p2, p3, p4, p5) =>
        Form.TldRegRegRegRegImmImmSymbol.apply(
          rd2 = p0,
          dst = p1,
          srcA = p2,
          srcB = InstructionOperand.Register.unsafe(OperandQueries.Zero),
          bank5 = p3,
          tsPtrIdx = p4,
          paramA = p5
        )
    ),
    FormDecoder(
      Definitions4.TldRegRegRegRegImmImmSymbolImm,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Immediate,
      Symbol,
      Immediate
    )(Form.TldRegRegRegRegImmImmSymbolImm.apply),
    FormDecoder(
      Definitions4.TldRegRegRegRegImmImmSymbolImm,
      Register,
      Register,
      Register,
      Immediate,
      Immediate,
      Symbol,
      Immediate
    )((p0, p1, p2, p3, p4, p5, p6) =>
      Form.TldRegRegRegRegImmImmSymbolImm.apply(
        rd2 = p0,
        dst = p1,
        srcA = p2,
        srcB = InstructionOperand.Register.unsafe(OperandQueries.Zero),
        bank5 = p3,
        tsPtrIdx = p4,
        paramA = p5,
        wmsk = p6
      )
    ),
    FormDecoder(
      Definitions4.TldRegRegRegRegRegImmSymbol,
      Register,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Symbol
    )(Form.TldRegRegRegRegRegImmSymbol.apply),
    FormDecoder(
      Definitions4.TldRegRegRegRegRegImmSymbolImm,
      Register,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Symbol,
      Immediate
    )(Form.TldRegRegRegRegRegImmSymbolImm.apply),
    FormDecoder(
      Definitions4.TldRegRegRegRegRegRegImmSymbol,
      Register,
      Register,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Symbol
    )(Form.TldRegRegRegRegRegRegImmSymbol.apply),
    FormDecoder(
      Definitions4.TldRegRegRegRegRegRegImmSymbolImm,
      Register,
      Register,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Symbol,
      Immediate
    )(Form.TldRegRegRegRegRegRegImmSymbolImm.apply),
    FormDecoder(
      Definitions4.TldRegRegRegRegRegRegSymbol,
      Register,
      Register,
      Register,
      Register,
      Register,
      Register,
      Symbol
    )(Form.TldRegRegRegRegRegRegSymbol.apply),
    FormDecoder(
      Definitions4.TldRegRegRegRegRegRegSymbolImm,
      Register,
      Register,
      Register,
      Register,
      Register,
      Register,
      Symbol,
      Immediate
    )(Form.TldRegRegRegRegRegRegSymbolImm.apply),
    FormDecoder(
      Definitions4.TldRegRegRegRegRegSymbol,
      Register,
      Register,
      Register,
      Register,
      Register,
      Symbol
    )(Form.TldRegRegRegRegRegSymbol.apply),
    FormDecoder(
      Definitions4.TldRegRegRegRegRegSymbolImm,
      Register,
      Register,
      Register,
      Register,
      Register,
      Symbol,
      Immediate
    )(Form.TldRegRegRegRegRegSymbolImm.apply),
    FormDecoder(
      Definitions4.TldRegRegRegRegSymbol,
      Register,
      Register,
      Register,
      Register,
      Symbol
    )(Form.TldRegRegRegRegSymbol.apply),
    FormDecoder(
      Definitions4.TldRegRegRegRegSymbolImm,
      Register,
      Register,
      Register,
      Register,
      Symbol,
      Immediate
    )(Form.TldRegRegRegRegSymbolImm.apply),
    FormDecoder(
      Definitions4.Tld4PredRegRegRegImmImmSymbol,
      Predicate,
      Register,
      Register,
      Register,
      Immediate,
      Immediate,
      Symbol
    )(Form.Tld4PredRegRegRegImmImmSymbol.apply),
    FormDecoder(
      Definitions4.Tld4PredRegRegRegImmImmSymbol,
      Predicate,
      Register,
      Register,
      Immediate,
      Immediate,
      Symbol
    )((p0, p1, p2, p3, p4, p5) =>
      Form.Tld4PredRegRegRegImmImmSymbol.apply(
        dstPredicate = p0,
        dst = p1,
        srcA = p2,
        srcB = InstructionOperand.Register.unsafe(OperandQueries.Zero),
        tid = p3,
        smp = p4,
        paramA = p5
      )
    ),
    FormDecoder(
      Definitions4.Tld4PredRegRegRegImmImmSymbolImm,
      Predicate,
      Register,
      Register,
      Register,
      Immediate,
      Immediate,
      Symbol,
      Immediate
    )(Form.Tld4PredRegRegRegImmImmSymbolImm.apply),
    FormDecoder(
      Definitions4.Tld4PredRegRegRegImmImmSymbolImm,
      Predicate,
      Register,
      Register,
      Immediate,
      Immediate,
      Symbol,
      Immediate
    )((p0, p1, p2, p3, p4, p5, p6) =>
      Form.Tld4PredRegRegRegImmImmSymbolImm.apply(
        dstPredicate = p0,
        dst = p1,
        srcA = p2,
        srcB = InstructionOperand.Register.unsafe(OperandQueries.Zero),
        tid = p3,
        smp = p4,
        paramA = p5,
        wmsk = p6
      )
    ),
    FormDecoder(
      Definitions4.Tld4PredRegRegRegImmSymbol,
      Predicate,
      Register,
      Register,
      Register,
      Immediate,
      Symbol
    )(Form.Tld4PredRegRegRegImmSymbol.apply),
    FormDecoder(Definitions4.Tld4PredRegRegRegImmSymbol, Predicate, Register, Register, Immediate, Symbol)(
      (p0, p1, p2, p3, p4) =>
        Form.Tld4PredRegRegRegImmSymbol.apply(
          dstPredicate = p0,
          dst = p1,
          srcA = p2,
          srcB = InstructionOperand.Register.unsafe(OperandQueries.Zero),
          tid = p3,
          paramA = p4
        )
    ),
    FormDecoder(
      Definitions4.Tld4PredRegRegRegImmSymbolImm,
      Predicate,
      Register,
      Register,
      Register,
      Immediate,
      Symbol,
      Immediate
    )(Form.Tld4PredRegRegRegImmSymbolImm.apply),
    FormDecoder(
      Definitions4.Tld4PredRegRegRegImmSymbolImm,
      Predicate,
      Register,
      Register,
      Immediate,
      Symbol,
      Immediate
    )((p0, p1, p2, p3, p4, p5) =>
      Form.Tld4PredRegRegRegImmSymbolImm.apply(
        dstPredicate = p0,
        dst = p1,
        srcA = p2,
        srcB = InstructionOperand.Register.unsafe(OperandQueries.Zero),
        tid = p3,
        paramA = p4,
        wmsk = p5
      )
    ),
    FormDecoder(
      Definitions4.Tld4PredRegRegRegRegImmImmImmSymbol,
      Predicate,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Immediate,
      Immediate,
      Symbol
    )(Form.Tld4PredRegRegRegRegImmImmImmSymbol.apply),
    FormDecoder(
      Definitions4.Tld4PredRegRegRegRegImmImmImmSymbolImm,
      Predicate,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Immediate,
      Immediate,
      Symbol,
      Immediate
    )(Form.Tld4PredRegRegRegRegImmImmImmSymbolImm.apply),
    FormDecoder(
      Definitions4.Tld4PredRegRegRegRegImmImmSymbol,
      Predicate,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Immediate,
      Symbol
    )(Form.Tld4PredRegRegRegRegImmImmSymbol.apply),
    FormDecoder(
      Definitions4.Tld4PredRegRegRegRegImmImmSymbolImm,
      Predicate,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Immediate,
      Symbol,
      Immediate
    )(Form.Tld4PredRegRegRegRegImmImmSymbolImm.apply),
    FormDecoder(
      Definitions4.Tld4PredRegRegRegRegRegImmSymbol,
      Predicate,
      Register,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Symbol
    )(Form.Tld4PredRegRegRegRegRegImmSymbol.apply),
    FormDecoder(
      Definitions4.Tld4PredRegRegRegRegRegImmSymbolImm,
      Predicate,
      Register,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Symbol,
      Immediate
    )(Form.Tld4PredRegRegRegRegRegImmSymbolImm.apply),
    FormDecoder(
      Definitions4.Tld4PredRegRegRegRegRegRegImmSymbol,
      Predicate,
      Register,
      Register,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Symbol
    )(Form.Tld4PredRegRegRegRegRegRegImmSymbol.apply),
    FormDecoder(
      Definitions4.Tld4PredRegRegRegRegRegRegImmSymbolImm,
      Predicate,
      Register,
      Register,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Symbol,
      Immediate
    )(Form.Tld4PredRegRegRegRegRegRegImmSymbolImm.apply),
    FormDecoder(
      Definitions4.Tld4PredRegRegRegRegRegRegSymbol,
      Predicate,
      Register,
      Register,
      Register,
      Register,
      Register,
      Register,
      Symbol
    )(Form.Tld4PredRegRegRegRegRegRegSymbol.apply),
    FormDecoder(
      Definitions4.Tld4PredRegRegRegRegRegRegSymbolImm,
      Predicate,
      Register,
      Register,
      Register,
      Register,
      Register,
      Register,
      Symbol,
      Immediate
    )(Form.Tld4PredRegRegRegRegRegRegSymbolImm.apply),
    FormDecoder(
      Definitions4.Tld4PredRegRegRegRegRegSymbol,
      Predicate,
      Register,
      Register,
      Register,
      Register,
      Register,
      Symbol
    )(Form.Tld4PredRegRegRegRegRegSymbol.apply),
    FormDecoder(
      Definitions4.Tld4PredRegRegRegRegRegSymbolImm,
      Predicate,
      Register,
      Register,
      Register,
      Register,
      Register,
      Symbol,
      Immediate
    )(Form.Tld4PredRegRegRegRegRegSymbolImm.apply),
    FormDecoder(
      Definitions4.Tld4PredRegRegRegRegSymbol,
      Predicate,
      Register,
      Register,
      Register,
      Register,
      Symbol
    )(Form.Tld4PredRegRegRegRegSymbol.apply),
    FormDecoder(
      Definitions4.Tld4PredRegRegRegRegSymbolImm,
      Predicate,
      Register,
      Register,
      Register,
      Register,
      Symbol,
      Immediate
    )(Form.Tld4PredRegRegRegRegSymbolImm.apply),
    FormDecoder(
      Definitions4.Tld4RegRegRegImmImmSymbol,
      Register,
      Register,
      Register,
      Immediate,
      Immediate,
      Symbol
    )(Form.Tld4RegRegRegImmImmSymbol.apply),
    FormDecoder(Definitions4.Tld4RegRegRegImmImmSymbol, Register, Register, Immediate, Immediate, Symbol)(
      (p0, p1, p2, p3, p4) =>
        Form.Tld4RegRegRegImmImmSymbol.apply(
          dst = p0,
          srcA = p1,
          srcB = InstructionOperand.Register.unsafe(OperandQueries.Zero),
          tid = p2,
          smp = p3,
          paramA = p4
        )
    ),
    FormDecoder(
      Definitions4.Tld4RegRegRegImmImmSymbolImm,
      Register,
      Register,
      Register,
      Immediate,
      Immediate,
      Symbol,
      Immediate
    )(Form.Tld4RegRegRegImmImmSymbolImm.apply),
    FormDecoder(Definitions4.Tld4RegRegRegImmImmSymbolImm, Register, Register, Immediate, Immediate, Symbol, Immediate)(
      (p0, p1, p2, p3, p4, p5) =>
        Form.Tld4RegRegRegImmImmSymbolImm.apply(
          dst = p0,
          srcA = p1,
          srcB = InstructionOperand.Register.unsafe(OperandQueries.Zero),
          tid = p2,
          smp = p3,
          paramA = p4,
          wmsk = p5
        )
    ),
    FormDecoder(
      Definitions4.Tld4RegRegRegImmSymbol,
      Register,
      Register,
      Register,
      Immediate,
      Symbol
    )(Form.Tld4RegRegRegImmSymbol.apply),
    FormDecoder(Definitions4.Tld4RegRegRegImmSymbol, Register, Register, Immediate, Symbol)((p0, p1, p2, p3) =>
      Form.Tld4RegRegRegImmSymbol.apply(
        dst = p0,
        srcA = p1,
        srcB = InstructionOperand.Register.unsafe(OperandQueries.Zero),
        tid = p2,
        paramA = p3
      )
    ),
    FormDecoder(
      Definitions4.Tld4RegRegRegImmSymbolImm,
      Register,
      Register,
      Register,
      Immediate,
      Symbol,
      Immediate
    )(Form.Tld4RegRegRegImmSymbolImm.apply),
    FormDecoder(Definitions4.Tld4RegRegRegImmSymbolImm, Register, Register, Immediate, Symbol, Immediate)(
      (p0, p1, p2, p3, p4) =>
        Form.Tld4RegRegRegImmSymbolImm.apply(
          dst = p0,
          srcA = p1,
          srcB = InstructionOperand.Register.unsafe(OperandQueries.Zero),
          tid = p2,
          paramA = p3,
          wmsk = p4
        )
    ),
    FormDecoder(
      Definitions4.Tld4RegRegRegRegImmImmImmSymbol,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Immediate,
      Immediate,
      Symbol
    )(Form.Tld4RegRegRegRegImmImmImmSymbol.apply),
    FormDecoder(
      Definitions4.Tld4RegRegRegRegImmImmImmSymbolImm,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Immediate,
      Immediate,
      Symbol,
      Immediate
    )(Form.Tld4RegRegRegRegImmImmImmSymbolImm.apply),
    FormDecoder(
      Definitions4.Tld4RegRegRegRegImmImmSymbol,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Immediate,
      Symbol
    )(Form.Tld4RegRegRegRegImmImmSymbol.apply),
    FormDecoder(
      Definitions4.Tld4RegRegRegRegImmImmSymbolImm,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Immediate,
      Symbol,
      Immediate
    )(Form.Tld4RegRegRegRegImmImmSymbolImm.apply),
    FormDecoder(
      Definitions4.Tld4RegRegRegRegRegImmSymbol,
      Register,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Symbol
    )(Form.Tld4RegRegRegRegRegImmSymbol.apply),
    FormDecoder(
      Definitions4.Tld4RegRegRegRegRegImmSymbolImm,
      Register,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Symbol,
      Immediate
    )(Form.Tld4RegRegRegRegRegImmSymbolImm.apply),
    FormDecoder(
      Definitions4.Tld4RegRegRegRegRegRegImmSymbol,
      Register,
      Register,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Symbol
    )(Form.Tld4RegRegRegRegRegRegImmSymbol.apply),
    FormDecoder(
      Definitions4.Tld4RegRegRegRegRegRegImmSymbolImm,
      Register,
      Register,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Symbol,
      Immediate
    )(Form.Tld4RegRegRegRegRegRegImmSymbolImm.apply),
    FormDecoder(
      Definitions4.Tld4RegRegRegRegRegRegSymbol,
      Register,
      Register,
      Register,
      Register,
      Register,
      Register,
      Symbol
    )(Form.Tld4RegRegRegRegRegRegSymbol.apply),
    FormDecoder(
      Definitions4.Tld4RegRegRegRegRegRegSymbolImm,
      Register,
      Register,
      Register,
      Register,
      Register,
      Register,
      Symbol,
      Immediate
    )(Form.Tld4RegRegRegRegRegRegSymbolImm.apply),
    FormDecoder(
      Definitions4.Tld4RegRegRegRegRegSymbol,
      Register,
      Register,
      Register,
      Register,
      Register,
      Symbol
    )(Form.Tld4RegRegRegRegRegSymbol.apply),
    FormDecoder(
      Definitions4.Tld4RegRegRegRegRegSymbolImm,
      Register,
      Register,
      Register,
      Register,
      Register,
      Symbol,
      Immediate
    )(Form.Tld4RegRegRegRegRegSymbolImm.apply),
    FormDecoder(
      Definitions4.Tld4RegRegRegRegSymbol,
      Register,
      Register,
      Register,
      Register,
      Symbol
    )(Form.Tld4RegRegRegRegSymbol.apply),
    FormDecoder(
      Definitions4.Tld4RegRegRegRegSymbolImm,
      Register,
      Register,
      Register,
      Register,
      Symbol,
      Immediate
    )(Form.Tld4RegRegRegRegSymbolImm.apply),
    FormDecoder(
      Definitions4.Tld4sRegRegRegRegImm,
      Register,
      Register,
      Register,
      Register,
      Immediate
    )(Form.Tld4sRegRegRegRegImm.apply),
    FormDecoder(
      Definitions4.TldsRegRegRegRegImmSymbolSymbol,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Symbol,
      Symbol
    )(Form.TldsRegRegRegRegImmSymbolSymbol.apply),
    FormDecoder(Definitions4.TldsRegRegRegRegImmSymbolSymbol, Register, Register, Register, Immediate, Symbol, Symbol)(
      (p0, p1, p2, p3, p4, p5) =>
        Form.TldsRegRegRegRegImmSymbolSymbol.apply(
          rd2 = p0,
          dst = p1,
          srcA = p2,
          srcB = InstructionOperand.Register.unsafe(OperandQueries.Zero),
          tid = p3,
          paramA = p4,
          wmsk = p5
        )
    ),
    FormDecoder(
      Definitions4.TmmlRegRegImmImmSymbol,
      Register,
      Register,
      Immediate,
      Immediate,
      Symbol
    )(Form.TmmlRegRegImmImmSymbol.apply),
    FormDecoder(
      Definitions4.TmmlRegRegImmImmSymbolImm,
      Register,
      Register,
      Immediate,
      Immediate,
      Symbol,
      Immediate
    )(Form.TmmlRegRegImmImmSymbolImm.apply),
    FormDecoder(
      Definitions4.TmmlRegRegImmSymbol,
      Register,
      Register,
      Immediate,
      Symbol
    )(Form.TmmlRegRegImmSymbol.apply),
    FormDecoder(
      Definitions4.TmmlRegRegImmSymbolImm,
      Register,
      Register,
      Immediate,
      Symbol,
      Immediate
    )(Form.TmmlRegRegImmSymbolImm.apply),
    FormDecoder(
      Definitions4.TmmlRegRegRegImmImmSymbol,
      Register,
      Register,
      Register,
      Immediate,
      Immediate,
      Symbol
    )(Form.TmmlRegRegRegImmImmSymbol.apply),
    FormDecoder(
      Definitions4.TmmlRegRegRegImmImmSymbolImm,
      Register,
      Register,
      Register,
      Immediate,
      Immediate,
      Symbol,
      Immediate
    )(Form.TmmlRegRegRegImmImmSymbolImm.apply),
    FormDecoder(
      Definitions4.TmmlRegRegRegImmSymbol,
      Register,
      Register,
      Register,
      Immediate,
      Symbol
    )(Form.TmmlRegRegRegImmSymbol.apply),
    FormDecoder(
      Definitions4.TmmlRegRegRegImmSymbolImm,
      Register,
      Register,
      Register,
      Immediate,
      Symbol,
      Immediate
    )(Form.TmmlRegRegRegImmSymbolImm.apply),
    FormDecoder(
      Definitions4.TmmlRegRegRegRegImmImmImmSymbol,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Immediate,
      Immediate,
      Symbol
    )(Form.TmmlRegRegRegRegImmImmImmSymbol.apply),
    FormDecoder(
      Definitions4.TmmlRegRegRegRegImmImmImmSymbolImm,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Immediate,
      Immediate,
      Symbol,
      Immediate
    )(Form.TmmlRegRegRegRegImmImmImmSymbolImm.apply),
    FormDecoder(
      Definitions4.TmmlRegRegRegRegImmImmSymbol,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Immediate,
      Symbol
    )(Form.TmmlRegRegRegRegImmImmSymbol.apply),
    FormDecoder(
      Definitions4.TmmlRegRegRegRegImmImmSymbolImm,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Immediate,
      Symbol,
      Immediate
    )(Form.TmmlRegRegRegRegImmImmSymbolImm.apply),
    FormDecoder(
      Definitions4.TmmlRegRegRegRegRegImmSymbol,
      Register,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Symbol
    )(Form.TmmlRegRegRegRegRegImmSymbol.apply),
    FormDecoder(Definitions4.TmmlRegRegRegRegRegImmSymbol, Register, Register, Register, Register, Immediate, Symbol)(
      (p0, p1, p2, p3, p4, p5) =>
        Form.TmmlRegRegRegRegRegImmSymbol.apply(
          rd2 = p0,
          dst = p1,
          srcA = p2,
          srcB = InstructionOperand.Register.unsafe(OperandQueries.Zero),
          srcC = p3,
          tid8 = p4,
          paramA = p5
        )
    ),
    FormDecoder(
      Definitions4.TmmlRegRegRegRegRegImmSymbolImm,
      Register,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Symbol,
      Immediate
    )(Form.TmmlRegRegRegRegRegImmSymbolImm.apply),
    FormDecoder(
      Definitions4.TmmlRegRegRegRegRegImmSymbolImm,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Symbol,
      Immediate
    )((p0, p1, p2, p3, p4, p5, p6) =>
      Form.TmmlRegRegRegRegRegImmSymbolImm.apply(
        rd2 = p0,
        dst = p1,
        srcA = p2,
        srcB = InstructionOperand.Register.unsafe(OperandQueries.Zero),
        srcC = p3,
        tid8 = p4,
        paramA = p5,
        wmsk = p6
      )
    ),
    FormDecoder(
      Definitions4.TmmlRegRegRegRegRegSymbol,
      Register,
      Register,
      Register,
      Register,
      Register,
      Symbol
    )(Form.TmmlRegRegRegRegRegSymbol.apply),
    FormDecoder(
      Definitions4.TmmlRegRegRegRegRegSymbolImm,
      Register,
      Register,
      Register,
      Register,
      Register,
      Symbol,
      Immediate
    )(Form.TmmlRegRegRegRegRegSymbolImm.apply),
    FormDecoder(
      Definitions4.TmmlRegRegRegRegSymbol,
      Register,
      Register,
      Register,
      Register,
      Symbol
    )(Form.TmmlRegRegRegRegSymbol.apply)
  )
}
