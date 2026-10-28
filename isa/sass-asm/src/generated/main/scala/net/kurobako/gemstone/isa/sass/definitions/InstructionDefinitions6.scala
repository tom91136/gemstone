// Generated SASS instruction decoders and checked operand constructors. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.sass
import javax.annotation.processing.Generated

import net.kurobako.gemstone.collection.*
import Instruction.*
import InstructionOperand.*
@Generated(Array("sbt codegen"))
object InstructionDefinitions6 {
  val decoders: Vec[FormDecoder] = Vec(
    FormDecoder(Definitions6.UviaddRegRegImm, Register, Register, Immediate)(Form.UviaddRegRegImm.apply),
    FormDecoder(Definitions6.UviaddRegRegReg, Register, Register, Register)(Form.UviaddRegRegReg.apply),
    FormDecoder(
      Definitions6.UvimnmxRegRegImmPred,
      Register,
      Register,
      Immediate,
      Predicate
    )(Form.UvimnmxRegRegImmPred.apply),
    FormDecoder(
      Definitions6.UvimnmxRegRegRegPred,
      Register,
      Register,
      Register,
      Predicate
    )(Form.UvimnmxRegRegRegPred.apply),
    FormDecoder(Definitions6.UvirtcountImm, Immediate)(Form.UvirtcountImm.apply),
    FormDecoder(Definitions6.UvirtcountReg, Register)(Form.UvirtcountReg.apply),
    FormDecoder(
      Definitions6.VabsdiffRegPredRegAddressImmReg,
      Register,
      Predicate,
      Register,
      Address,
      Immediate,
      Register
    )(Form.VabsdiffRegPredRegAddressImmReg.apply),
    FormDecoder(
      Definitions6.VabsdiffRegPredRegConstReg,
      Register,
      Predicate,
      Register,
      ConstantBank,
      Register
    )(Form.VabsdiffRegPredRegConstReg.apply),
    FormDecoder(
      Definitions6.VabsdiffRegPredRegImmReg,
      Register,
      Predicate,
      Register,
      Immediate,
      Register
    )(Form.VabsdiffRegPredRegImmReg.apply),
    FormDecoder(
      Definitions6.VabsdiffRegPredRegRegAddressImm,
      Register,
      Predicate,
      Register,
      Register,
      Address,
      Immediate
    )(Form.VabsdiffRegPredRegRegAddressImm.apply),
    FormDecoder(
      Definitions6.VabsdiffRegPredRegRegConst,
      Register,
      Predicate,
      Register,
      Register,
      ConstantBank
    )(Form.VabsdiffRegPredRegRegConst.apply),
    FormDecoder(
      Definitions6.VabsdiffRegPredRegRegImm,
      Register,
      Predicate,
      Register,
      Register,
      Immediate
    )(Form.VabsdiffRegPredRegRegImm.apply),
    FormDecoder(
      Definitions6.VabsdiffRegPredRegRegReg,
      Register,
      Predicate,
      Register,
      Register,
      Register
    )(Form.VabsdiffRegPredRegRegReg.apply),
    FormDecoder(
      Definitions6.VabsdiffRegRegAddressImmReg,
      Register,
      Register,
      Address,
      Immediate,
      Register
    )(Form.VabsdiffRegRegAddressImmReg.apply),
    FormDecoder(
      Definitions6.VabsdiffRegRegConstReg,
      Register,
      Register,
      ConstantBank,
      Register
    )(Form.VabsdiffRegRegConstReg.apply),
    FormDecoder(
      Definitions6.VabsdiffRegRegImmReg,
      Register,
      Register,
      Immediate,
      Register
    )(Form.VabsdiffRegRegImmReg.apply),
    FormDecoder(
      Definitions6.VabsdiffRegRegRegAddressImm,
      Register,
      Register,
      Register,
      Address,
      Immediate
    )(Form.VabsdiffRegRegRegAddressImm.apply),
    FormDecoder(
      Definitions6.VabsdiffRegRegRegConst,
      Register,
      Register,
      Register,
      ConstantBank
    )(Form.VabsdiffRegRegRegConst.apply),
    FormDecoder(
      Definitions6.VabsdiffRegRegRegImm,
      Register,
      Register,
      Register,
      Immediate
    )(Form.VabsdiffRegRegRegImm.apply),
    FormDecoder(
      Definitions6.VabsdiffRegRegRegReg,
      Register,
      Register,
      Register,
      Register
    )(Form.VabsdiffRegRegRegReg.apply),
    FormDecoder(
      Definitions6.Vabsdiff2RegRegImmReg,
      Register,
      Register,
      Immediate,
      Register
    )(Form.Vabsdiff2RegRegImmReg.apply),
    FormDecoder(
      Definitions6.Vabsdiff2RegRegRegReg,
      Register,
      Register,
      Register,
      Register
    )(Form.Vabsdiff2RegRegRegReg.apply),
    FormDecoder(
      Definitions6.Vabsdiff4RegPredRegAddressImmReg,
      Register,
      Predicate,
      Register,
      Address,
      Immediate,
      Register
    )(Form.Vabsdiff4RegPredRegAddressImmReg.apply),
    FormDecoder(
      Definitions6.Vabsdiff4RegPredRegConstReg,
      Register,
      Predicate,
      Register,
      ConstantBank,
      Register
    )(Form.Vabsdiff4RegPredRegConstReg.apply),
    FormDecoder(
      Definitions6.Vabsdiff4RegPredRegImmReg,
      Register,
      Predicate,
      Register,
      Immediate,
      Register
    )(Form.Vabsdiff4RegPredRegImmReg.apply),
    FormDecoder(
      Definitions6.Vabsdiff4RegPredRegRegAddressImm,
      Register,
      Predicate,
      Register,
      Register,
      Address,
      Immediate
    )(Form.Vabsdiff4RegPredRegRegAddressImm.apply),
    FormDecoder(
      Definitions6.Vabsdiff4RegPredRegRegConst,
      Register,
      Predicate,
      Register,
      Register,
      ConstantBank
    )(Form.Vabsdiff4RegPredRegRegConst.apply),
    FormDecoder(
      Definitions6.Vabsdiff4RegPredRegRegImm,
      Register,
      Predicate,
      Register,
      Register,
      Immediate
    )(Form.Vabsdiff4RegPredRegRegImm.apply),
    FormDecoder(
      Definitions6.Vabsdiff4RegPredRegRegReg,
      Register,
      Predicate,
      Register,
      Register,
      Register
    )(Form.Vabsdiff4RegPredRegRegReg.apply),
    FormDecoder(
      Definitions6.Vabsdiff4RegRegAddressImmReg,
      Register,
      Register,
      Address,
      Immediate,
      Register
    )(Form.Vabsdiff4RegRegAddressImmReg.apply),
    FormDecoder(
      Definitions6.Vabsdiff4RegRegConstReg,
      Register,
      Register,
      ConstantBank,
      Register
    )(Form.Vabsdiff4RegRegConstReg.apply),
    FormDecoder(
      Definitions6.Vabsdiff4RegRegImmReg,
      Register,
      Register,
      Immediate,
      Register
    )(Form.Vabsdiff4RegRegImmReg.apply),
    FormDecoder(
      Definitions6.Vabsdiff4RegRegRegAddressImm,
      Register,
      Register,
      Register,
      Address,
      Immediate
    )(Form.Vabsdiff4RegRegRegAddressImm.apply),
    FormDecoder(
      Definitions6.Vabsdiff4RegRegRegConst,
      Register,
      Register,
      Register,
      ConstantBank
    )(Form.Vabsdiff4RegRegRegConst.apply),
    FormDecoder(
      Definitions6.Vabsdiff4RegRegRegImm,
      Register,
      Register,
      Register,
      Immediate
    )(Form.Vabsdiff4RegRegRegImm.apply),
    FormDecoder(
      Definitions6.Vabsdiff4RegRegRegReg,
      Register,
      Register,
      Register,
      Register
    )(Form.Vabsdiff4RegRegRegReg.apply),
    FormDecoder(Definitions6.VaddRegRegImmReg, Register, Register, Immediate, Register)(Form.VaddRegRegImmReg.apply),
    FormDecoder(Definitions6.VaddRegRegRegReg, Register, Register, Register, Register)(Form.VaddRegRegRegReg.apply),
    FormDecoder(Definitions6.Vadd2RegRegImmReg, Register, Register, Immediate, Register)(Form.Vadd2RegRegImmReg.apply),
    FormDecoder(Definitions6.Vadd2RegRegRegReg, Register, Register, Register, Register)(Form.Vadd2RegRegRegReg.apply),
    FormDecoder(Definitions6.Vadd4RegRegImmReg, Register, Register, Immediate, Register)(Form.Vadd4RegRegImmReg.apply),
    FormDecoder(Definitions6.Vadd4RegRegRegReg, Register, Register, Register, Register)(Form.Vadd4RegRegRegReg.apply),
    FormDecoder(
      Definitions6.VhmnmxRegRegAddressImmRegPred,
      Register,
      Register,
      Address,
      Immediate,
      Register,
      Predicate
    )(Form.VhmnmxRegRegAddressImmRegPred.apply),
    FormDecoder(
      Definitions6.VhmnmxRegRegConstRegPred,
      Register,
      Register,
      ConstantBank,
      Register,
      Predicate
    )(Form.VhmnmxRegRegConstRegPred.apply),
    FormDecoder(
      Definitions6.VhmnmxRegRegImmImmRegPred,
      Register,
      Register,
      Immediate,
      Immediate,
      Register,
      Predicate
    )(Form.VhmnmxRegRegImmImmRegPred.apply),
    FormDecoder(
      Definitions6.VhmnmxRegRegRegRegPred,
      Register,
      Register,
      Register,
      Register,
      Predicate
    )(Form.VhmnmxRegRegRegRegPred.apply),
    FormDecoder(
      Definitions6.ViaddRegRegAddressImm,
      Register,
      Register,
      Address,
      Immediate
    )(Form.ViaddRegRegAddressImm.apply),
    FormDecoder(Definitions6.ViaddRegRegConst, Register, Register, ConstantBank)(Form.ViaddRegRegConst.apply),
    FormDecoder(Definitions6.ViaddRegRegImm, Register, Register, Immediate)(Form.ViaddRegRegImm.apply),
    FormDecoder(Definitions6.ViaddRegRegReg, Register, Register, Register)(Form.ViaddRegRegReg.apply),
    FormDecoder(
      Definitions6.ViaddmnmxRegRegAddressImmRegPred,
      Register,
      Register,
      Address,
      Immediate,
      Register,
      Predicate
    )(Form.ViaddmnmxRegRegAddressImmRegPred.apply),
    FormDecoder(
      Definitions6.ViaddmnmxRegRegConstRegPred,
      Register,
      Register,
      ConstantBank,
      Register,
      Predicate
    )(Form.ViaddmnmxRegRegConstRegPred.apply),
    FormDecoder(
      Definitions6.ViaddmnmxRegRegImmRegPred,
      Register,
      Register,
      Immediate,
      Register,
      Predicate
    )(Form.ViaddmnmxRegRegImmRegPred.apply),
    FormDecoder(
      Definitions6.ViaddmnmxRegRegRegAddressImmPred,
      Register,
      Register,
      Register,
      Address,
      Immediate,
      Predicate
    )(Form.ViaddmnmxRegRegRegAddressImmPred.apply),
    FormDecoder(
      Definitions6.ViaddmnmxRegRegRegConstPred,
      Register,
      Register,
      Register,
      ConstantBank,
      Predicate
    )(Form.ViaddmnmxRegRegRegConstPred.apply),
    FormDecoder(
      Definitions6.ViaddmnmxRegRegRegImmPred,
      Register,
      Register,
      Register,
      Immediate,
      Predicate
    )(Form.ViaddmnmxRegRegRegImmPred.apply),
    FormDecoder(
      Definitions6.ViaddmnmxRegRegRegRegPred,
      Register,
      Register,
      Register,
      Register,
      Predicate
    )(Form.ViaddmnmxRegRegRegRegPred.apply),
    FormDecoder(Definitions6.VildRegAddress, Register, Address)(Form.VildRegAddress.apply),
    FormDecoder(
      Definitions6.VimnmxRegPredPredRegAddressImmPred,
      Register,
      Predicate,
      Predicate,
      Register,
      Address,
      Immediate,
      Predicate
    )(Form.VimnmxRegPredPredRegAddressImmPred.apply),
    FormDecoder(
      Definitions6.VimnmxRegPredPredRegConstPred,
      Register,
      Predicate,
      Predicate,
      Register,
      ConstantBank,
      Predicate
    )(Form.VimnmxRegPredPredRegConstPred.apply),
    FormDecoder(
      Definitions6.VimnmxRegPredPredRegImmPred,
      Register,
      Predicate,
      Predicate,
      Register,
      Immediate,
      Predicate
    )(Form.VimnmxRegPredPredRegImmPred.apply),
    FormDecoder(
      Definitions6.VimnmxRegPredPredRegRegPred,
      Register,
      Predicate,
      Predicate,
      Register,
      Register,
      Predicate
    )(Form.VimnmxRegPredPredRegRegPred.apply),
    FormDecoder(
      Definitions6.VimnmxRegRegAddressImmPred,
      Register,
      Register,
      Address,
      Immediate,
      Predicate
    )(Form.VimnmxRegRegAddressImmPred.apply),
    FormDecoder(
      Definitions6.VimnmxRegRegConstPred,
      Register,
      Register,
      ConstantBank,
      Predicate
    )(Form.VimnmxRegRegConstPred.apply),
    FormDecoder(
      Definitions6.VimnmxRegRegImmPred,
      Register,
      Register,
      Immediate,
      Predicate
    )(Form.VimnmxRegRegImmPred.apply),
    FormDecoder(
      Definitions6.VimnmxRegRegRegPred,
      Register,
      Register,
      Register,
      Predicate
    )(Form.VimnmxRegRegRegPred.apply),
    FormDecoder(
      Definitions6.Vimnmx3RegRegAddressImmRegPred,
      Register,
      Register,
      Address,
      Immediate,
      Register,
      Predicate
    )(Form.Vimnmx3RegRegAddressImmRegPred.apply),
    FormDecoder(
      Definitions6.Vimnmx3RegRegConstRegPred,
      Register,
      Register,
      ConstantBank,
      Register,
      Predicate
    )(Form.Vimnmx3RegRegConstRegPred.apply),
    FormDecoder(
      Definitions6.Vimnmx3RegRegImmRegPred,
      Register,
      Register,
      Immediate,
      Register,
      Predicate
    )(Form.Vimnmx3RegRegImmRegPred.apply),
    FormDecoder(
      Definitions6.Vimnmx3RegRegRegRegPred,
      Register,
      Register,
      Register,
      Register,
      Predicate
    )(Form.Vimnmx3RegRegRegRegPred.apply),
    FormDecoder(Definitions6.VmadRegRegImmReg, Register, Register, Immediate, Register)(Form.VmadRegRegImmReg.apply),
    FormDecoder(Definitions6.VmadRegRegRegReg, Register, Register, Register, Register)(Form.VmadRegRegRegReg.apply),
    FormDecoder(Definitions6.VmnmxRegRegImmReg, Register, Register, Immediate, Register)(Form.VmnmxRegRegImmReg.apply),
    FormDecoder(Definitions6.VmnmxRegRegRegReg, Register, Register, Register, Register)(Form.VmnmxRegRegRegReg.apply),
    FormDecoder(
      Definitions6.Vmnmx2RegRegImmReg,
      Register,
      Register,
      Immediate,
      Register
    )(Form.Vmnmx2RegRegImmReg.apply),
    FormDecoder(Definitions6.Vmnmx2RegRegRegReg, Register, Register, Register, Register)(Form.Vmnmx2RegRegRegReg.apply),
    FormDecoder(
      Definitions6.Vmnmx4RegRegImmReg,
      Register,
      Register,
      Immediate,
      Register
    )(Form.Vmnmx4RegRegImmReg.apply),
    FormDecoder(Definitions6.Vmnmx4RegRegRegReg, Register, Register, Register, Register)(Form.Vmnmx4RegRegRegReg.apply),
    FormDecoder(Definitions6.VoteImm, Immediate)(Form.VoteImm.apply),
    FormDecoder(Definitions6.VotePredPred, Predicate, Predicate)(Form.VotePredPred.apply),
    FormDecoder(Definitions6.VoteRegPredPred, Register, Predicate, Predicate)(Form.VoteRegPredPred.apply),
    FormDecoder(Definitions6.VoteuRegPredPred, Register, Predicate, Predicate)(Form.VoteuRegPredPred.apply),
    FormDecoder(Definitions6.VoteVtgImm, Immediate)(Form.VoteVtgImm.apply),
    FormDecoder(Definitions6.VoteVtgPredImm, Predicate, Immediate)(Form.VoteVtgPredImm.apply),
    FormDecoder(
      Definitions6.VoteVtgPredPredImmPred,
      Predicate,
      Predicate,
      Immediate,
      Predicate
    )(Form.VoteVtgPredPredImmPred.apply),
    FormDecoder(
      Definitions6.Vsel2RegRegImmPred,
      Register,
      Register,
      Immediate,
      Predicate
    )(Form.Vsel2RegRegImmPred.apply),
    FormDecoder(
      Definitions6.Vsel2RegRegRegPred,
      Register,
      Register,
      Register,
      Predicate
    )(Form.Vsel2RegRegRegPred.apply),
    FormDecoder(
      Definitions6.Vsel4RegRegImmPred,
      Register,
      Register,
      Immediate,
      Predicate
    )(Form.Vsel4RegRegImmPred.apply),
    FormDecoder(
      Definitions6.Vsel4RegRegRegPred,
      Register,
      Register,
      Register,
      Predicate
    )(Form.Vsel4RegRegRegPred.apply),
    FormDecoder(Definitions6.VsetRegRegImmReg, Register, Register, Immediate, Register)(Form.VsetRegRegImmReg.apply),
    FormDecoder(Definitions6.VsetRegRegRegReg, Register, Register, Register, Register)(Form.VsetRegRegRegReg.apply),
    FormDecoder(Definitions6.Vset2RegRegImmReg, Register, Register, Immediate, Register)(Form.Vset2RegRegImmReg.apply),
    FormDecoder(Definitions6.Vset2RegRegRegReg, Register, Register, Register, Register)(Form.Vset2RegRegRegReg.apply),
    FormDecoder(Definitions6.Vset4RegRegImmReg, Register, Register, Immediate, Register)(Form.Vset4RegRegImmReg.apply),
    FormDecoder(Definitions6.Vset4RegRegRegReg, Register, Register, Register, Register)(Form.Vset4RegRegRegReg.apply),
    FormDecoder(
      Definitions6.VsetpPredPredRegImmPred,
      Predicate,
      Predicate,
      Register,
      Immediate,
      Predicate
    )(Form.VsetpPredPredRegImmPred.apply),
    FormDecoder(
      Definitions6.VsetpPredPredRegRegPred,
      Predicate,
      Predicate,
      Register,
      Register,
      Predicate
    )(Form.VsetpPredPredRegRegPred.apply),
    FormDecoder(Definitions6.VsetpPredRegImm, Predicate, Register, Immediate)(Form.VsetpPredRegImm.apply),
    FormDecoder(Definitions6.VsetpPredRegReg, Predicate, Register, Register)(Form.VsetpPredRegReg.apply),
    FormDecoder(Definitions6.VshlRegRegImmReg, Register, Register, Immediate, Register)(Form.VshlRegRegImmReg.apply),
    FormDecoder(Definitions6.VshlRegRegRegReg, Register, Register, Register, Register)(Form.VshlRegRegRegReg.apply),
    FormDecoder(Definitions6.VshrRegRegImmReg, Register, Register, Immediate, Register)(Form.VshrRegRegImmReg.apply),
    FormDecoder(Definitions6.VshrRegRegRegReg, Register, Register, Register, Register)(Form.VshrRegRegRegReg.apply),
    FormDecoder(Definitions6.WarpgroupNoOperands)(Form.WarpgroupNoOperands.apply),
    FormDecoder(Definitions6.WarpgroupImm, Immediate)(Form.WarpgroupImm.apply),
    FormDecoder(Definitions6.WarpgroupsetReg, Register)(Form.WarpgroupsetReg.apply),
    FormDecoder(Definitions6.WarpsyncNoOperands)(Form.WarpsyncNoOperands.apply),
    FormDecoder(Definitions6.WarpsyncAddressImm, Address, Immediate)(Form.WarpsyncAddressImm.apply),
    FormDecoder(Definitions6.WarpsyncConst, ConstantBank)(Form.WarpsyncConst.apply),
    FormDecoder(Definitions6.WarpsyncImm, Immediate)(Form.WarpsyncImm.apply),
    FormDecoder(Definitions6.WarpsyncPred, Predicate)(Form.WarpsyncPred.apply),
    FormDecoder(Definitions6.WarpsyncPredAddressImm, Predicate, Address, Immediate)(Form.WarpsyncPredAddressImm.apply),
    FormDecoder(Definitions6.WarpsyncPredConst, Predicate, ConstantBank)(Form.WarpsyncPredConst.apply),
    FormDecoder(Definitions6.WarpsyncPredImm, Predicate, Immediate)(Form.WarpsyncPredImm.apply),
    FormDecoder(Definitions6.WarpsyncPredReg, Predicate, Register)(Form.WarpsyncPredReg.apply),
    FormDecoder(Definitions6.WarpsyncPredRegImm, Predicate, Register, Immediate)(Form.WarpsyncPredRegImm.apply),
    FormDecoder(Definitions6.WarpsyncReg, Register)(Form.WarpsyncReg.apply),
    FormDecoder(Definitions6.WarpsyncRegImm, Register, Immediate)(Form.WarpsyncRegImm.apply),
    FormDecoder(
      Definitions6.XmadRegRegConstReg,
      Register,
      Register,
      ConstantBank,
      Register
    )(Form.XmadRegRegConstReg.apply),
    FormDecoder(Definitions6.XmadRegRegImmReg, Register, Register, Immediate, Register)(Form.XmadRegRegImmReg.apply),
    FormDecoder(
      Definitions6.XmadRegRegRegConst,
      Register,
      Register,
      Register,
      ConstantBank
    )(Form.XmadRegRegRegConst.apply),
    FormDecoder(Definitions6.XmadRegRegRegReg, Register, Register, Register, Register)(Form.XmadRegRegRegReg.apply),
    FormDecoder(Definitions6.YieldNoOperands)(Form.YieldNoOperands.apply),
    FormDecoder(Definitions6.YieldPred, Predicate)(Form.YieldPred.apply),
    FormDecoder(Definitions6.Hir0x002NoOperands)(Form.Hir0x002NoOperands.apply),
    FormDecoder(Definitions6.Hir0x003NoOperands)(Form.Hir0x003NoOperands.apply),
    FormDecoder(Definitions6.Hir0x003Pred, Predicate)(Form.Hir0x003Pred.apply),
    FormDecoder(Definitions6.Hir0x004NoOperands)(Form.Hir0x004NoOperands.apply),
    FormDecoder(
      Definitions6.Hir0x005PredRegRegImm,
      Predicate,
      Register,
      Register,
      Immediate
    )(Form.Hir0x005PredRegRegImm.apply),
    FormDecoder(Definitions6.Hir0x005RegRegImm, Register, Register, Immediate)(Form.Hir0x005RegRegImm.apply),
    FormDecoder(Definitions6.Hir0x006Imm, Immediate)(Form.Hir0x006Imm.apply),
    FormDecoder(Definitions6.Hir0x007NoOperands)(Form.Hir0x007NoOperands.apply),
    FormDecoder(Definitions6.Hir0x008ImmRegReg, Immediate, Register, Register)(Form.Hir0x008ImmRegReg.apply),
    FormDecoder(Definitions6.Hir0x020NoOperands)(Form.Hir0x020NoOperands.apply),
    FormDecoder(Definitions6.Hir0x020Imm, Immediate)(Form.Hir0x020Imm.apply),
    FormDecoder(Definitions6.Hir0x120AddressImm, Address, Immediate)(Form.Hir0x120AddressImm.apply),
    FormDecoder(Definitions6.Hir0x120Const, ConstantBank)(Form.Hir0x120Const.apply),
    FormDecoder(Definitions6.Hir0x120Imm, Immediate)(Form.Hir0x120Imm.apply),
    FormDecoder(Definitions6.Hir0x120PredAddressImm, Predicate, Address, Immediate)(Form.Hir0x120PredAddressImm.apply),
    FormDecoder(Definitions6.Hir0x120PredConst, Predicate, ConstantBank)(Form.Hir0x120PredConst.apply),
    FormDecoder(Definitions6.Hir0x120PredImm, Predicate, Immediate)(Form.Hir0x120PredImm.apply),
    FormDecoder(Definitions6.Hir0x120PredReg, Predicate, Register)(Form.Hir0x120PredReg.apply),
    FormDecoder(Definitions6.Hir0x120Reg, Register)(Form.Hir0x120Reg.apply),
    FormDecoder(
      Definitions6.Hir0x130AddressAddressAddressAddressAddressAddressPred,
      Address,
      Address,
      Address,
      Address,
      Address,
      Address,
      Predicate
    )(Form.Hir0x130AddressAddressAddressAddressAddressAddressPred.apply),
    FormDecoder(
      Definitions6.Hir0x150RegRegRegRegPredRegRegReg,
      Register,
      Register,
      Register,
      Register,
      Predicate,
      Register,
      Register,
      Register
    )(Form.Hir0x150RegRegRegRegPredRegRegReg.apply),
    FormDecoder(
      Definitions6.Hir0x150RegRegRegRegRegRegReg,
      Register,
      Register,
      Register,
      Register,
      Register,
      Register,
      Register
    )(Form.Hir0x150RegRegRegRegRegRegReg.apply),
    FormDecoder(Definitions6.Hir0x160Reg, Register)(Form.Hir0x160Reg.apply),
    FormDecoder(Definitions6.Hir0x170Reg, Register)(Form.Hir0x170Reg.apply),
    FormDecoder(
      Definitions6.Hir0x1a0RegRegRegRegImmImm,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Immediate
    )(Form.Hir0x1a0RegRegRegRegImmImm.apply),
    FormDecoder(
      Definitions6.Hir0x1b0RegRegRegReg,
      Register,
      Register,
      Register,
      Register
    )(Form.Hir0x1b0RegRegRegReg.apply),
    FormDecoder(Definitions6.Hir0x1e0NoOperands)(Form.Hir0x1e0NoOperands.apply),
    FormDecoder(Definitions6.Hir0x1f0ImmReg, Immediate, Register)(Form.Hir0x1f0ImmReg.apply),
    FormDecoder(Definitions6.Hir0x1f2Reg, Register)(Form.Hir0x1f2Reg.apply),
    FormDecoder(Definitions6.Hir0x1f3Reg, Register)(Form.Hir0x1f3Reg.apply)
  )
}
