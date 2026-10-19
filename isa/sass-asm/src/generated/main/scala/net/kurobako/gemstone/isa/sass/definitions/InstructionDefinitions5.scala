// Generated SASS instruction decoders and checked operand constructors. Regenerate with: sbt codegen

package net.kurobako.gemstone.isa.sass
import net.kurobako.gemstone.collection.*
import Instruction.*
import InstructionOperand.*
@javax.annotation.processing.Generated(Array("sbt codegen"))
object InstructionDefinitions5 {
  val decoders: Vec[FormDecoder] = Vec(
    FormDecoder(
      Definitions5.TxdRegRegRegRegRegImm,
      Register,
      Register,
      Register,
      Register,
      Register,
      Immediate
    )(Form.TxdRegRegRegRegRegImm.apply),
    FormDecoder(
      Definitions5.TxdRegRegRegRegRegImmImm,
      Register,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Immediate
    )(Form.TxdRegRegRegRegRegImmImm.apply),
    FormDecoder(
      Definitions5.TxdRegRegRegRegRegReg,
      Register,
      Register,
      Register,
      Register,
      Register,
      Register
    )(Form.TxdRegRegRegRegRegReg.apply),
    FormDecoder(
      Definitions5.TxdRegRegRegRegRegRegImm,
      Register,
      Register,
      Register,
      Register,
      Register,
      Register,
      Immediate
    )(Form.TxdRegRegRegRegRegRegImm.apply),
    FormDecoder(
      Definitions5.TxdRegRegRegRegRegRegImmImm,
      Register,
      Register,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Immediate
    )(Form.TxdRegRegRegRegRegRegImmImm.apply),
    FormDecoder(Definitions5.TxqRegRegImm, Register, Register, Immediate)(Form.TxqRegRegImm.apply),
    FormDecoder(Definitions5.TxqRegRegImmImm, Register, Register, Immediate, Immediate)(Form.TxqRegRegImmImm.apply),
    FormDecoder(
      Definitions5.TxqRegRegImmImmImm,
      Register,
      Register,
      Immediate,
      Immediate,
      Immediate
    )(Form.TxqRegRegImmImmImm.apply),
    FormDecoder(
      Definitions5.TxqRegRegImmImmImmImm,
      Register,
      Register,
      Immediate,
      Immediate,
      Immediate,
      Immediate
    )(Form.TxqRegRegImmImmImmImm.apply),
    FormDecoder(Definitions5.TxqRegRegRegImm, Register, Register, Register, Immediate)(Form.TxqRegRegRegImm.apply),
    FormDecoder(
      Definitions5.TxqRegRegRegImmImmImm,
      Register,
      Register,
      Register,
      Immediate,
      Immediate,
      Immediate
    )(Form.TxqRegRegRegImmImmImm.apply),
    FormDecoder(
      Definitions5.TxqRegRegRegImmImmImmImm,
      Register,
      Register,
      Register,
      Immediate,
      Immediate,
      Immediate,
      Immediate
    )(Form.TxqRegRegRegImmImmImmImm.apply),
    FormDecoder(
      Definitions5.TxqRegRegRegRegImm,
      Register,
      Register,
      Register,
      Register,
      Immediate
    )(Form.TxqRegRegRegRegImm.apply),
    FormDecoder(
      Definitions5.TxqRegRegRegRegImmImm,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Immediate
    )(Form.TxqRegRegRegRegImmImm.apply),
    FormDecoder(Definitions5.UblkcpAddressAddressReg, Address, Address, Register)(Form.UblkcpAddressAddressReg.apply),
    FormDecoder(
      Definitions5.UblkcpAddressAddressRegAddress,
      Address,
      Address,
      Register,
      Address
    )(Form.UblkcpAddressAddressRegAddress.apply),
    FormDecoder(Definitions5.UblkpfAddressReg, Address, Register)(Form.UblkpfAddressReg.apply),
    FormDecoder(Definitions5.UblkpfAddressRegAddress, Address, Register, Address)(Form.UblkpfAddressRegAddress.apply),
    FormDecoder(Definitions5.UblkredAddressAddressReg, Address, Address, Register)(Form.UblkredAddressAddressReg.apply),
    FormDecoder(
      Definitions5.UblkredAddressAddressRegAddress,
      Address,
      Address,
      Register,
      Address
    )(Form.UblkredAddressAddressRegAddress.apply),
    FormDecoder(Definitions5.UbmskRegRegImm, Register, Register, Immediate)(Form.UbmskRegRegImm.apply),
    FormDecoder(Definitions5.UbmskRegRegReg, Register, Register, Register)(Form.UbmskRegRegReg.apply),
    FormDecoder(Definitions5.UbrevRegImm, Register, Immediate)(Form.UbrevRegImm.apply),
    FormDecoder(Definitions5.UbrevRegReg, Register, Register)(Form.UbrevRegReg.apply),
    FormDecoder(Definitions5.UcgabararvNoOperands)(Form.UcgabararvNoOperands.apply),
    FormDecoder(Definitions5.UcgabargetReg, Register)(Form.UcgabargetReg.apply),
    FormDecoder(Definitions5.UcgabarsetReg, Register)(Form.UcgabarsetReg.apply),
    FormDecoder(Definitions5.UcgabarwaitNoOperands)(Form.UcgabarwaitNoOperands.apply),
    FormDecoder(Definitions5.Ucgabar_ArvNoOperands)(Form.Ucgabar_ArvNoOperands.apply),
    FormDecoder(Definitions5.Ucgabar_WaitNoOperands)(Form.Ucgabar_WaitNoOperands.apply),
    FormDecoder(
      Definitions5.UcleaRegPredRegImmImm,
      Register,
      Predicate,
      Register,
      Immediate,
      Immediate
    )(Form.UcleaRegPredRegImmImm.apply),
    FormDecoder(
      Definitions5.UcleaRegPredRegRegImm,
      Register,
      Predicate,
      Register,
      Register,
      Immediate
    )(Form.UcleaRegPredRegRegImm.apply),
    FormDecoder(Definitions5.Uf2fRegImm, Register, Immediate)(Form.Uf2fRegImm.apply),
    FormDecoder(Definitions5.Uf2fRegReg, Register, Register)(Form.Uf2fRegReg.apply),
    FormDecoder(Definitions5.Uf2fpRegImm, Register, Immediate)(Form.Uf2fpRegImm.apply),
    FormDecoder(Definitions5.Uf2fpRegImmReg, Register, Immediate, Register)(Form.Uf2fpRegImmReg.apply),
    FormDecoder(Definitions5.Uf2fpRegReg, Register, Register)(Form.Uf2fpRegReg.apply),
    FormDecoder(Definitions5.Uf2fpRegRegImm, Register, Register, Immediate)(Form.Uf2fpRegRegImm.apply),
    FormDecoder(Definitions5.Uf2fpRegRegImmReg, Register, Register, Immediate, Register)(Form.Uf2fpRegRegImmReg.apply),
    FormDecoder(Definitions5.Uf2fpRegRegReg, Register, Register, Register)(Form.Uf2fpRegRegReg.apply),
    FormDecoder(Definitions5.Uf2fpRegRegRegImm, Register, Register, Register, Immediate)(Form.Uf2fpRegRegRegImm.apply),
    FormDecoder(Definitions5.Uf2fpRegRegRegReg, Register, Register, Register, Register)(Form.Uf2fpRegRegRegReg.apply),
    FormDecoder(Definitions5.Uf2iRegImm, Register, Immediate)(Form.Uf2iRegImm.apply),
    FormDecoder(Definitions5.Uf2iRegReg, Register, Register)(Form.Uf2iRegReg.apply),
    FormDecoder(Definitions5.Uf2ipRegRegImmReg, Register, Register, Immediate, Register)(Form.Uf2ipRegRegImmReg.apply),
    FormDecoder(Definitions5.Uf2ipRegRegRegImm, Register, Register, Register, Immediate)(Form.Uf2ipRegRegRegImm.apply),
    FormDecoder(Definitions5.Uf2ipRegRegRegReg, Register, Register, Register, Register)(Form.Uf2ipRegRegRegReg.apply),
    FormDecoder(Definitions5.UfaddRegRegImm, Register, Register, Immediate)(Form.UfaddRegRegImm.apply),
    FormDecoder(Definitions5.UfaddRegRegReg, Register, Register, Register)(Form.UfaddRegRegReg.apply),
    FormDecoder(Definitions5.UffmaRegRegImmReg, Register, Register, Immediate, Register)(Form.UffmaRegRegImmReg.apply),
    FormDecoder(Definitions5.UffmaRegRegRegImm, Register, Register, Register, Immediate)(Form.UffmaRegRegRegImm.apply),
    FormDecoder(Definitions5.UffmaRegRegRegReg, Register, Register, Register, Register)(Form.UffmaRegRegRegReg.apply),
    FormDecoder(Definitions5.UfhaddRegRegReg, Register, Register, Register)(Form.UfhaddRegRegReg.apply),
    FormDecoder(Definitions5.UfhfmaRegRegRegReg, Register, Register, Register, Register)(Form.UfhfmaRegRegRegReg.apply),
    FormDecoder(Definitions5.UfloRegImm, Register, Immediate)(Form.UfloRegImm.apply),
    FormDecoder(Definitions5.UfloRegPredImm, Register, Predicate, Immediate)(Form.UfloRegPredImm.apply),
    FormDecoder(Definitions5.UfloRegPredReg, Register, Predicate, Register)(Form.UfloRegPredReg.apply),
    FormDecoder(Definitions5.UfloRegReg, Register, Register)(Form.UfloRegReg.apply),
    FormDecoder(
      Definitions5.UfmnmxRegPredRegImmPred,
      Register,
      Predicate,
      Register,
      Immediate,
      Predicate
    )(Form.UfmnmxRegPredRegImmPred.apply),
    FormDecoder(
      Definitions5.UfmnmxRegPredRegRegPred,
      Register,
      Predicate,
      Register,
      Register,
      Predicate
    )(Form.UfmnmxRegPredRegRegPred.apply),
    FormDecoder(
      Definitions5.UfmnmxRegRegImmPred,
      Register,
      Register,
      Immediate,
      Predicate
    )(Form.UfmnmxRegRegImmPred.apply),
    FormDecoder(
      Definitions5.UfmnmxRegRegRegPred,
      Register,
      Register,
      Register,
      Predicate
    )(Form.UfmnmxRegRegRegPred.apply),
    FormDecoder(Definitions5.UfmulRegRegImm, Register, Register, Immediate)(Form.UfmulRegRegImm.apply),
    FormDecoder(Definitions5.UfmulRegRegReg, Register, Register, Register)(Form.UfmulRegRegReg.apply),
    FormDecoder(Definitions5.UfrndRegImm, Register, Immediate)(Form.UfrndRegImm.apply),
    FormDecoder(Definitions5.UfrndRegReg, Register, Register)(Form.UfrndRegReg.apply),
    FormDecoder(
      Definitions5.UfselRegRegImmPred,
      Register,
      Register,
      Immediate,
      Predicate
    )(Form.UfselRegRegImmPred.apply),
    FormDecoder(
      Definitions5.UfselRegRegRegPred,
      Register,
      Register,
      Register,
      Predicate
    )(Form.UfselRegRegRegPred.apply),
    FormDecoder(Definitions5.UfsetRegRegImm, Register, Register, Immediate)(Form.UfsetRegRegImm.apply),
    FormDecoder(
      Definitions5.UfsetRegRegImmPred,
      Register,
      Register,
      Immediate,
      Predicate
    )(Form.UfsetRegRegImmPred.apply),
    FormDecoder(Definitions5.UfsetRegRegReg, Register, Register, Register)(Form.UfsetRegRegReg.apply),
    FormDecoder(
      Definitions5.UfsetRegRegRegPred,
      Register,
      Register,
      Register,
      Predicate
    )(Form.UfsetRegRegRegPred.apply),
    FormDecoder(
      Definitions5.UfsetpPredPredRegImmPred,
      Predicate,
      Predicate,
      Register,
      Immediate,
      Predicate
    )(Form.UfsetpPredPredRegImmPred.apply),
    FormDecoder(
      Definitions5.UfsetpPredPredRegRegPred,
      Predicate,
      Predicate,
      Register,
      Register,
      Predicate
    )(Form.UfsetpPredPredRegRegPred.apply),
    FormDecoder(Definitions5.UfsetpPredRegImm, Predicate, Register, Immediate)(Form.UfsetpPredRegImm.apply),
    FormDecoder(Definitions5.UfsetpPredRegReg, Predicate, Register, Register)(Form.UfsetpPredRegReg.apply),
    FormDecoder(Definitions5.UgetnextworkidAddressAddress, Address, Address)(Form.UgetnextworkidAddressAddress.apply),
    FormDecoder(Definitions5.Ui2fRegImm, Register, Immediate)(Form.Ui2fRegImm.apply),
    FormDecoder(Definitions5.Ui2fRegReg, Register, Register)(Form.Ui2fRegReg.apply),
    FormDecoder(Definitions5.Ui2fpRegImm, Register, Immediate)(Form.Ui2fpRegImm.apply),
    FormDecoder(Definitions5.Ui2fpRegReg, Register, Register)(Form.Ui2fpRegReg.apply),
    FormDecoder(Definitions5.Ui2iRegImm, Register, Immediate)(Form.Ui2iRegImm.apply),
    FormDecoder(Definitions5.Ui2iRegReg, Register, Register)(Form.Ui2iRegReg.apply),
    FormDecoder(Definitions5.Ui2ipRegRegImmReg, Register, Register, Immediate, Register)(Form.Ui2ipRegRegImmReg.apply),
    FormDecoder(Definitions5.Ui2ipRegRegRegReg, Register, Register, Register, Register)(Form.Ui2ipRegRegRegReg.apply),
    FormDecoder(Definitions5.UiabsRegImm, Register, Immediate)(Form.UiabsRegImm.apply),
    FormDecoder(Definitions5.UiabsRegReg, Register, Register)(Form.UiabsRegReg.apply),
    FormDecoder(
      Definitions5.Uiadd3RegPredPredRegImmReg,
      Register,
      Predicate,
      Predicate,
      Register,
      Immediate,
      Register
    )(Form.Uiadd3RegPredPredRegImmReg.apply),
    FormDecoder(
      Definitions5.Uiadd3RegPredPredRegImmRegPredPred,
      Register,
      Predicate,
      Predicate,
      Register,
      Immediate,
      Register,
      Predicate,
      Predicate
    )(Form.Uiadd3RegPredPredRegImmRegPredPred.apply),
    FormDecoder(
      Definitions5.Uiadd3RegPredPredRegRegReg,
      Register,
      Predicate,
      Predicate,
      Register,
      Register,
      Register
    )(Form.Uiadd3RegPredPredRegRegReg.apply),
    FormDecoder(
      Definitions5.Uiadd3RegPredPredRegRegRegPredPred,
      Register,
      Predicate,
      Predicate,
      Register,
      Register,
      Register,
      Predicate,
      Predicate
    )(Form.Uiadd3RegPredPredRegRegRegPredPred.apply),
    FormDecoder(
      Definitions5.Uiadd3RegPredRegImmReg,
      Register,
      Predicate,
      Register,
      Immediate,
      Register
    )(Form.Uiadd3RegPredRegImmReg.apply),
    FormDecoder(
      Definitions5.Uiadd3RegPredRegImmRegPredPred,
      Register,
      Predicate,
      Register,
      Immediate,
      Register,
      Predicate,
      Predicate
    )(Form.Uiadd3RegPredRegImmRegPredPred.apply),
    FormDecoder(
      Definitions5.Uiadd3RegPredRegRegReg,
      Register,
      Predicate,
      Register,
      Register,
      Register
    )(Form.Uiadd3RegPredRegRegReg.apply),
    FormDecoder(
      Definitions5.Uiadd3RegPredRegRegRegPredPred,
      Register,
      Predicate,
      Register,
      Register,
      Register,
      Predicate,
      Predicate
    )(Form.Uiadd3RegPredRegRegRegPredPred.apply),
    FormDecoder(
      Definitions5.Uiadd3RegRegImmReg,
      Register,
      Register,
      Immediate,
      Register
    )(Form.Uiadd3RegRegImmReg.apply),
    FormDecoder(
      Definitions5.Uiadd3RegRegImmRegPredPred,
      Register,
      Register,
      Immediate,
      Register,
      Predicate,
      Predicate
    )(Form.Uiadd3RegRegImmRegPredPred.apply),
    FormDecoder(Definitions5.Uiadd3RegRegRegReg, Register, Register, Register, Register)(Form.Uiadd3RegRegRegReg.apply),
    FormDecoder(
      Definitions5.Uiadd3RegRegRegRegPredPred,
      Register,
      Register,
      Register,
      Register,
      Predicate,
      Predicate
    )(Form.Uiadd3RegRegRegRegPredPred.apply),
    FormDecoder(
      Definitions5.UimadRegPredRegImmReg,
      Register,
      Predicate,
      Register,
      Immediate,
      Register
    )(Form.UimadRegPredRegImmReg.apply),
    FormDecoder(
      Definitions5.UimadRegPredRegImmRegPred,
      Register,
      Predicate,
      Register,
      Immediate,
      Register,
      Predicate
    )(Form.UimadRegPredRegImmRegPred.apply),
    FormDecoder(
      Definitions5.UimadRegPredRegRegReg,
      Register,
      Predicate,
      Register,
      Register,
      Register
    )(Form.UimadRegPredRegRegReg.apply),
    FormDecoder(
      Definitions5.UimadRegPredRegRegRegPred,
      Register,
      Predicate,
      Register,
      Register,
      Register,
      Predicate
    )(Form.UimadRegPredRegRegRegPred.apply),
    FormDecoder(Definitions5.UimadRegRegImmReg, Register, Register, Immediate, Register)(Form.UimadRegRegImmReg.apply),
    FormDecoder(
      Definitions5.UimadRegRegImmRegPred,
      Register,
      Register,
      Immediate,
      Register,
      Predicate
    )(Form.UimadRegRegImmRegPred.apply),
    FormDecoder(Definitions5.UimadRegRegRegImm, Register, Register, Register, Immediate)(Form.UimadRegRegRegImm.apply),
    FormDecoder(
      Definitions5.UimadRegRegRegImmPred,
      Register,
      Register,
      Register,
      Immediate,
      Predicate
    )(Form.UimadRegRegRegImmPred.apply),
    FormDecoder(Definitions5.UimadRegRegRegReg, Register, Register, Register, Register)(Form.UimadRegRegRegReg.apply),
    FormDecoder(
      Definitions5.UimadRegRegRegRegPred,
      Register,
      Register,
      Register,
      Register,
      Predicate
    )(Form.UimadRegRegRegRegPred.apply),
    FormDecoder(
      Definitions5.UimnmxPredPredRegRegImmPred,
      Predicate,
      Predicate,
      Register,
      Register,
      Immediate,
      Predicate
    )(Form.UimnmxPredPredRegRegImmPred.apply),
    FormDecoder(
      Definitions5.UimnmxPredPredRegRegImmPredPred,
      Predicate,
      Predicate,
      Register,
      Register,
      Immediate,
      Predicate,
      Predicate
    )(Form.UimnmxPredPredRegRegImmPredPred.apply),
    FormDecoder(
      Definitions5.UimnmxPredPredRegRegRegPred,
      Predicate,
      Predicate,
      Register,
      Register,
      Register,
      Predicate
    )(Form.UimnmxPredPredRegRegRegPred.apply),
    FormDecoder(
      Definitions5.UimnmxPredPredRegRegRegPredPred,
      Predicate,
      Predicate,
      Register,
      Register,
      Register,
      Predicate,
      Predicate
    )(Form.UimnmxPredPredRegRegRegPredPred.apply),
    FormDecoder(
      Definitions5.UimnmxPredRegRegImmPred,
      Predicate,
      Register,
      Register,
      Immediate,
      Predicate
    )(Form.UimnmxPredRegRegImmPred.apply),
    FormDecoder(
      Definitions5.UimnmxPredRegRegImmPredPred,
      Predicate,
      Register,
      Register,
      Immediate,
      Predicate,
      Predicate
    )(Form.UimnmxPredRegRegImmPredPred.apply),
    FormDecoder(
      Definitions5.UimnmxPredRegRegRegPred,
      Predicate,
      Register,
      Register,
      Register,
      Predicate
    )(Form.UimnmxPredRegRegRegPred.apply),
    FormDecoder(
      Definitions5.UimnmxPredRegRegRegPredPred,
      Predicate,
      Register,
      Register,
      Register,
      Predicate,
      Predicate
    )(Form.UimnmxPredRegRegRegPredPred.apply),
    FormDecoder(
      Definitions5.UimnmxRegRegImmPred,
      Register,
      Register,
      Immediate,
      Predicate
    )(Form.UimnmxRegRegImmPred.apply),
    FormDecoder(
      Definitions5.UimnmxRegRegImmPredPred,
      Register,
      Register,
      Immediate,
      Predicate,
      Predicate
    )(Form.UimnmxRegRegImmPredPred.apply),
    FormDecoder(
      Definitions5.UimnmxRegRegRegPred,
      Register,
      Register,
      Register,
      Predicate
    )(Form.UimnmxRegRegRegPred.apply),
    FormDecoder(
      Definitions5.UimnmxRegRegRegPredPred,
      Register,
      Register,
      Register,
      Predicate,
      Predicate
    )(Form.UimnmxRegRegRegPredPred.apply),
    FormDecoder(
      Definitions5.UisetpPredPredRegImmPred,
      Predicate,
      Predicate,
      Register,
      Immediate,
      Predicate
    )(Form.UisetpPredPredRegImmPred.apply),
    FormDecoder(
      Definitions5.UisetpPredPredRegImmPredPred,
      Predicate,
      Predicate,
      Register,
      Immediate,
      Predicate,
      Predicate
    )(Form.UisetpPredPredRegImmPredPred.apply),
    FormDecoder(
      Definitions5.UisetpPredPredRegRegPred,
      Predicate,
      Predicate,
      Register,
      Register,
      Predicate
    )(Form.UisetpPredPredRegRegPred.apply),
    FormDecoder(
      Definitions5.UisetpPredPredRegRegPredPred,
      Predicate,
      Predicate,
      Register,
      Register,
      Predicate,
      Predicate
    )(Form.UisetpPredPredRegRegPredPred.apply),
    FormDecoder(Definitions5.UisetpPredRegImm, Predicate, Register, Immediate)(Form.UisetpPredRegImm.apply),
    FormDecoder(
      Definitions5.UisetpPredRegImmPred,
      Predicate,
      Register,
      Immediate,
      Predicate
    )(Form.UisetpPredRegImmPred.apply),
    FormDecoder(Definitions5.UisetpPredRegReg, Predicate, Register, Register)(Form.UisetpPredRegReg.apply),
    FormDecoder(
      Definitions5.UisetpPredRegRegPred,
      Predicate,
      Register,
      Register,
      Predicate
    )(Form.UisetpPredRegRegPred.apply),
    FormDecoder(Definitions5.UldcRegAddress, Register, Address)(Form.UldcRegAddress.apply),
    FormDecoder(Definitions5.UldcRegAddressImm, Register, Address, Immediate)(Form.UldcRegAddressImm.apply),
    FormDecoder(Definitions5.UldcRegAddressPred, Register, Address, Predicate)(Form.UldcRegAddressPred.apply),
    FormDecoder(Definitions5.UldcRegConst, Register, ConstantBank)(Form.UldcRegConst.apply),
    FormDecoder(
      Definitions5.UleaRegPredRegImmImm,
      Register,
      Predicate,
      Register,
      Immediate,
      Immediate
    )(Form.UleaRegPredRegImmImm.apply),
    FormDecoder(
      Definitions5.UleaRegPredRegImmImmPred,
      Register,
      Predicate,
      Register,
      Immediate,
      Immediate,
      Predicate
    )(Form.UleaRegPredRegImmImmPred.apply),
    FormDecoder(
      Definitions5.UleaRegPredRegImmRegImm,
      Register,
      Predicate,
      Register,
      Immediate,
      Register,
      Immediate
    )(Form.UleaRegPredRegImmRegImm.apply),
    FormDecoder(
      Definitions5.UleaRegPredRegImmRegImmPred,
      Register,
      Predicate,
      Register,
      Immediate,
      Register,
      Immediate,
      Predicate
    )(Form.UleaRegPredRegImmRegImmPred.apply),
    FormDecoder(
      Definitions5.UleaRegPredRegRegImm,
      Register,
      Predicate,
      Register,
      Register,
      Immediate
    )(Form.UleaRegPredRegRegImm.apply),
    FormDecoder(
      Definitions5.UleaRegPredRegRegImmImm,
      Register,
      Predicate,
      Register,
      Register,
      Immediate,
      Immediate
    )(Form.UleaRegPredRegRegImmImm.apply),
    FormDecoder(
      Definitions5.UleaRegPredRegRegImmImmPred,
      Register,
      Predicate,
      Register,
      Register,
      Immediate,
      Immediate,
      Predicate
    )(Form.UleaRegPredRegRegImmImmPred.apply),
    FormDecoder(
      Definitions5.UleaRegPredRegRegImmPred,
      Register,
      Predicate,
      Register,
      Register,
      Immediate,
      Predicate
    )(Form.UleaRegPredRegRegImmPred.apply),
    FormDecoder(
      Definitions5.UleaRegPredRegRegRegImm,
      Register,
      Predicate,
      Register,
      Register,
      Register,
      Immediate
    )(Form.UleaRegPredRegRegRegImm.apply),
    FormDecoder(
      Definitions5.UleaRegPredRegRegRegImmPred,
      Register,
      Predicate,
      Register,
      Register,
      Register,
      Immediate,
      Predicate
    )(Form.UleaRegPredRegRegRegImmPred.apply),
    FormDecoder(Definitions5.UleaRegRegImmImm, Register, Register, Immediate, Immediate)(Form.UleaRegRegImmImm.apply),
    FormDecoder(
      Definitions5.UleaRegRegImmImmPred,
      Register,
      Register,
      Immediate,
      Immediate,
      Predicate
    )(Form.UleaRegRegImmImmPred.apply),
    FormDecoder(
      Definitions5.UleaRegRegImmRegImm,
      Register,
      Register,
      Immediate,
      Register,
      Immediate
    )(Form.UleaRegRegImmRegImm.apply),
    FormDecoder(
      Definitions5.UleaRegRegImmRegImmPred,
      Register,
      Register,
      Immediate,
      Register,
      Immediate,
      Predicate
    )(Form.UleaRegRegImmRegImmPred.apply),
    FormDecoder(Definitions5.UleaRegRegRegImm, Register, Register, Register, Immediate)(Form.UleaRegRegRegImm.apply),
    FormDecoder(
      Definitions5.UleaRegRegRegImmImm,
      Register,
      Register,
      Register,
      Immediate,
      Immediate
    )(Form.UleaRegRegRegImmImm.apply),
    FormDecoder(
      Definitions5.UleaRegRegRegImmImmPred,
      Register,
      Register,
      Register,
      Immediate,
      Immediate,
      Predicate
    )(Form.UleaRegRegRegImmImmPred.apply),
    FormDecoder(
      Definitions5.UleaRegRegRegImmPred,
      Register,
      Register,
      Register,
      Immediate,
      Predicate
    )(Form.UleaRegRegRegImmPred.apply),
    FormDecoder(
      Definitions5.UleaRegRegRegRegImm,
      Register,
      Register,
      Register,
      Register,
      Immediate
    )(Form.UleaRegRegRegRegImm.apply),
    FormDecoder(
      Definitions5.UleaRegRegRegRegImmPred,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Predicate
    )(Form.UleaRegRegRegRegImmPred.apply),
    FormDecoder(Definitions5.UlepcReg, Register)(Form.UlepcReg.apply),
    FormDecoder(Definitions5.UlepcRegImm, Register, Immediate)(Form.UlepcRegImm.apply),
    FormDecoder(Definitions5.UlopPredRegRegImm, Predicate, Register, Register, Immediate)(Form.UlopPredRegRegImm.apply),
    FormDecoder(
      Definitions5.UlopPredRegRegImmPred,
      Predicate,
      Register,
      Register,
      Immediate,
      Predicate
    )(Form.UlopPredRegRegImmPred.apply),
    FormDecoder(Definitions5.UlopPredRegRegReg, Predicate, Register, Register, Register)(Form.UlopPredRegRegReg.apply),
    FormDecoder(
      Definitions5.UlopPredRegRegRegPred,
      Predicate,
      Register,
      Register,
      Register,
      Predicate
    )(Form.UlopPredRegRegRegPred.apply),
    FormDecoder(Definitions5.UlopRegRegImm, Register, Register, Immediate)(Form.UlopRegRegImm.apply),
    FormDecoder(Definitions5.UlopRegRegImmPred, Register, Register, Immediate, Predicate)(Form.UlopRegRegImmPred.apply),
    FormDecoder(Definitions5.UlopRegRegReg, Register, Register, Register)(Form.UlopRegRegReg.apply),
    FormDecoder(Definitions5.UlopRegRegRegPred, Register, Register, Register, Predicate)(Form.UlopRegRegRegPred.apply),
    FormDecoder(
      Definitions5.Ulop3PredRegRegImmReg,
      Predicate,
      Register,
      Register,
      Immediate,
      Register
    )(Form.Ulop3PredRegRegImmReg.apply),
    FormDecoder(
      Definitions5.Ulop3PredRegRegImmRegImm,
      Predicate,
      Register,
      Register,
      Immediate,
      Register,
      Immediate
    )(Form.Ulop3PredRegRegImmRegImm.apply),
    FormDecoder(
      Definitions5.Ulop3PredRegRegImmRegImmPred,
      Predicate,
      Register,
      Register,
      Immediate,
      Register,
      Immediate,
      Predicate
    )(Form.Ulop3PredRegRegImmRegImmPred.apply),
    FormDecoder(
      Definitions5.Ulop3PredRegRegImmRegPred,
      Predicate,
      Register,
      Register,
      Immediate,
      Register,
      Predicate
    )(Form.Ulop3PredRegRegImmRegPred.apply),
    FormDecoder(
      Definitions5.Ulop3PredRegRegRegReg,
      Predicate,
      Register,
      Register,
      Register,
      Register
    )(Form.Ulop3PredRegRegRegReg.apply),
    FormDecoder(
      Definitions5.Ulop3PredRegRegRegRegImm,
      Predicate,
      Register,
      Register,
      Register,
      Register,
      Immediate
    )(Form.Ulop3PredRegRegRegRegImm.apply),
    FormDecoder(
      Definitions5.Ulop3PredRegRegRegRegImmPred,
      Predicate,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Predicate
    )(Form.Ulop3PredRegRegRegRegImmPred.apply),
    FormDecoder(
      Definitions5.Ulop3PredRegRegRegRegPred,
      Predicate,
      Register,
      Register,
      Register,
      Register,
      Predicate
    )(Form.Ulop3PredRegRegRegRegPred.apply),
    FormDecoder(Definitions5.Ulop3RegRegImmReg, Register, Register, Immediate, Register)(Form.Ulop3RegRegImmReg.apply),
    FormDecoder(
      Definitions5.Ulop3RegRegImmRegImm,
      Register,
      Register,
      Immediate,
      Register,
      Immediate
    )(Form.Ulop3RegRegImmRegImm.apply),
    FormDecoder(
      Definitions5.Ulop3RegRegImmRegImmPred,
      Register,
      Register,
      Immediate,
      Register,
      Immediate,
      Predicate
    )(Form.Ulop3RegRegImmRegImmPred.apply),
    FormDecoder(
      Definitions5.Ulop3RegRegImmRegPred,
      Register,
      Register,
      Immediate,
      Register,
      Predicate
    )(Form.Ulop3RegRegImmRegPred.apply),
    FormDecoder(Definitions5.Ulop3RegRegRegReg, Register, Register, Register, Register)(Form.Ulop3RegRegRegReg.apply),
    FormDecoder(
      Definitions5.Ulop3RegRegRegRegImm,
      Register,
      Register,
      Register,
      Register,
      Immediate
    )(Form.Ulop3RegRegRegRegImm.apply),
    FormDecoder(
      Definitions5.Ulop3RegRegRegRegImmPred,
      Register,
      Register,
      Register,
      Register,
      Immediate,
      Predicate
    )(Form.Ulop3RegRegRegRegImmPred.apply),
    FormDecoder(
      Definitions5.Ulop3RegRegRegRegPred,
      Register,
      Register,
      Register,
      Register,
      Predicate
    )(Form.Ulop3RegRegRegRegPred.apply),
    FormDecoder(
      Definitions5.Ulop32iPredRegRegImm,
      Predicate,
      Register,
      Register,
      Immediate
    )(Form.Ulop32iPredRegRegImm.apply),
    FormDecoder(
      Definitions5.Ulop32iPredRegRegImmPred,
      Predicate,
      Register,
      Register,
      Immediate,
      Predicate
    )(Form.Ulop32iPredRegRegImmPred.apply),
    FormDecoder(Definitions5.Ulop32iRegRegImm, Register, Register, Immediate)(Form.Ulop32iRegRegImm.apply),
    FormDecoder(
      Definitions5.Ulop32iRegRegImmPred,
      Register,
      Register,
      Immediate,
      Predicate
    )(Form.Ulop32iRegRegImmPred.apply),
    FormDecoder(Definitions5.UmemsetsAddressRegReg, Address, Register, Register)(Form.UmemsetsAddressRegReg.apply),
    FormDecoder(Definitions5.UmovRegImm, Register, Immediate)(Form.UmovRegImm.apply),
    FormDecoder(Definitions5.UmovRegReg, Register, Register)(Form.UmovRegReg.apply),
    FormDecoder(Definitions5.Up2urReg, Register)(Form.Up2urReg.apply),
    FormDecoder(Definitions5.Up2urRegRegImm, Register, Register, Immediate)(Form.Up2urRegRegImm.apply),
    FormDecoder(Definitions5.Up2urRegRegReg, Register, Register, Register)(Form.Up2urRegRegReg.apply),
    FormDecoder(
      Definitions5.Uplop3PredPredPredPred,
      Predicate,
      Predicate,
      Predicate,
      Predicate
    )(Form.Uplop3PredPredPredPred.apply),
    FormDecoder(
      Definitions5.Uplop3PredPredPredPredImm,
      Predicate,
      Predicate,
      Predicate,
      Predicate,
      Immediate
    )(Form.Uplop3PredPredPredPredImm.apply),
    FormDecoder(
      Definitions5.Uplop3PredPredPredPredPredImmImm,
      Predicate,
      Predicate,
      Predicate,
      Predicate,
      Predicate,
      Immediate,
      Immediate
    )(Form.Uplop3PredPredPredPredPredImmImm.apply),
    FormDecoder(
      Definitions5.Uplop3PredPredPredRegPredImmImm,
      Predicate,
      Predicate,
      Predicate,
      Register,
      Predicate,
      Immediate,
      Immediate
    )(Form.Uplop3PredPredPredRegPredImmImm.apply),
    FormDecoder(
      Definitions5.Uplop3PredPredPredRegRegImmImm,
      Predicate,
      Predicate,
      Predicate,
      Register,
      Register,
      Immediate,
      Immediate
    )(Form.Uplop3PredPredPredRegRegImmImm.apply),
    FormDecoder(
      Definitions5.Uplop3PredPredRegPredImm,
      Predicate,
      Predicate,
      Register,
      Predicate,
      Immediate
    )(Form.Uplop3PredPredRegPredImm.apply),
    FormDecoder(
      Definitions5.Uplop3PredPredRegRegImm,
      Predicate,
      Predicate,
      Register,
      Register,
      Immediate
    )(Form.Uplop3PredPredRegRegImm.apply),
    FormDecoder(
      Definitions5.Uplop3PredPredRegRegRegImmImm,
      Predicate,
      Predicate,
      Register,
      Register,
      Register,
      Immediate,
      Immediate
    )(Form.Uplop3PredPredRegRegRegImmImm.apply),
    FormDecoder(
      Definitions5.Uplop3PredRegRegRegImm,
      Predicate,
      Register,
      Register,
      Register,
      Immediate
    )(Form.Uplop3PredRegRegRegImm.apply),
    FormDecoder(Definitions5.UpopcRegImm, Register, Immediate)(Form.UpopcRegImm.apply),
    FormDecoder(Definitions5.UpopcRegReg, Register, Register)(Form.UpopcRegReg.apply),
    FormDecoder(Definitions5.UprmtRegRegImmReg, Register, Register, Immediate, Register)(Form.UprmtRegRegImmReg.apply),
    FormDecoder(Definitions5.UprmtRegRegRegReg, Register, Register, Register, Register)(Form.UprmtRegRegRegReg.apply),
    FormDecoder(Definitions5.UpsetpPredPredPred, Predicate, Predicate, Predicate)(Form.UpsetpPredPredPred.apply),
    FormDecoder(
      Definitions5.UpsetpPredPredPredPredPred,
      Predicate,
      Predicate,
      Predicate,
      Predicate,
      Predicate
    )(Form.UpsetpPredPredPredPredPred.apply),
    FormDecoder(Definitions5.Ur2upReg, Register)(Form.Ur2upReg.apply),
    FormDecoder(Definitions5.Ur2upRegImm, Register, Immediate)(Form.Ur2upRegImm.apply),
    FormDecoder(Definitions5.Ur2upRegReg, Register, Register)(Form.Ur2upRegReg.apply),
    FormDecoder(Definitions5.UredgrAddressReg, Address, Register)(Form.UredgrAddressReg.apply),
    FormDecoder(Definitions5.UselRegRegImmPred, Register, Register, Immediate, Predicate)(Form.UselRegRegImmPred.apply),
    FormDecoder(Definitions5.UselRegRegRegPred, Register, Register, Register, Predicate)(Form.UselRegRegRegPred.apply),
    FormDecoder(Definitions5.UsetmaxregImm, Immediate)(Form.UsetmaxregImm.apply),
    FormDecoder(Definitions5.UsetmaxregPredImm, Predicate, Immediate)(Form.UsetmaxregPredImm.apply),
    FormDecoder(Definitions5.UsetmaxregPredReg, Predicate, Register)(Form.UsetmaxregPredReg.apply),
    FormDecoder(Definitions5.UsetmaxregReg, Register)(Form.UsetmaxregReg.apply),
    FormDecoder(Definitions5.UsetshmszNoOperands)(Form.UsetshmszNoOperands.apply),
    FormDecoder(Definitions5.UsetshmszImm, Immediate)(Form.UsetshmszImm.apply),
    FormDecoder(Definitions5.UsetshmszReg, Register)(Form.UsetshmszReg.apply),
    FormDecoder(Definitions5.UsgxtRegRegImm, Register, Register, Immediate)(Form.UsgxtRegRegImm.apply),
    FormDecoder(Definitions5.UsgxtRegRegReg, Register, Register, Register)(Form.UsgxtRegRegReg.apply),
    FormDecoder(Definitions5.UshfRegRegImmReg, Register, Register, Immediate, Register)(Form.UshfRegRegImmReg.apply),
    FormDecoder(Definitions5.UshfRegRegRegImm, Register, Register, Register, Immediate)(Form.UshfRegRegRegImm.apply),
    FormDecoder(Definitions5.UshfRegRegRegReg, Register, Register, Register, Register)(Form.UshfRegRegRegReg.apply),
    FormDecoder(Definitions5.UshlRegImmReg, Register, Immediate, Register)(Form.UshlRegImmReg.apply),
    FormDecoder(Definitions5.UshlRegRegImm, Register, Register, Immediate)(Form.UshlRegRegImm.apply),
    FormDecoder(Definitions5.UshlRegRegReg, Register, Register, Register)(Form.UshlRegRegReg.apply),
    FormDecoder(Definitions5.UshrRegRegImm, Register, Register, Immediate)(Form.UshrRegRegImm.apply),
    FormDecoder(Definitions5.UshrRegRegReg, Register, Register, Register)(Form.UshrRegRegReg.apply),
    FormDecoder(Definitions5.UstgrAddressReg, Address, Register)(Form.UstgrAddressReg.apply),
    FormDecoder(Definitions5.UtcatomswsPredRegReg, Predicate, Register, Register)(Form.UtcatomswsPredRegReg.apply),
    FormDecoder(
      Definitions5.UtcatomswsPredRegRegReg,
      Predicate,
      Register,
      Register,
      Register
    )(Form.UtcatomswsPredRegRegReg.apply),
    FormDecoder(Definitions5.UtcatomswsRegReg, Register, Register)(Form.UtcatomswsRegReg.apply),
    FormDecoder(Definitions5.UtcbarNoOperands)(Form.UtcbarNoOperands.apply),
    FormDecoder(Definitions5.UtcbarAddressRegReg, Address, Register, Register)(Form.UtcbarAddressRegReg.apply),
    FormDecoder(Definitions5.UtccpAddressAddress, Address, Address)(Form.UtccpAddressAddress.apply),
    FormDecoder(
      Definitions5.UtchmmaAddressAddressAddressAddressAddressRegPred,
      Address,
      Address,
      Address,
      Address,
      Address,
      Register,
      Predicate
    )(Form.UtchmmaAddressAddressAddressAddressAddressRegPred.apply),
    FormDecoder(
      Definitions5.UtchmmaAddressAddressAddressAddressAddressRegPredImm,
      Address,
      Address,
      Address,
      Address,
      Address,
      Register,
      Predicate,
      Immediate
    )(Form.UtchmmaAddressAddressAddressAddressAddressRegPredImm.apply),
    FormDecoder(
      Definitions5.UtcimmaAddressAddressAddressAddressAddressRegPred,
      Address,
      Address,
      Address,
      Address,
      Address,
      Register,
      Predicate
    )(Form.UtcimmaAddressAddressAddressAddressAddressRegPred.apply),
    FormDecoder(
      Definitions5.UtcommaAddressAddressAddressAddressAddressAddressPred,
      Address,
      Address,
      Address,
      Address,
      Address,
      Address,
      Predicate
    )(Form.UtcommaAddressAddressAddressAddressAddressAddressPred.apply),
    FormDecoder(
      Definitions5.UtcqmmaAddressAddressAddressAddressAddressAddressPred,
      Address,
      Address,
      Address,
      Address,
      Address,
      Address,
      Predicate
    )(Form.UtcqmmaAddressAddressAddressAddressAddressAddressPred.apply),
    FormDecoder(
      Definitions5.UtcqmmaAddressAddressAddressAddressAddressRegPred,
      Address,
      Address,
      Address,
      Address,
      Address,
      Register,
      Predicate
    )(Form.UtcqmmaAddressAddressAddressAddressAddressRegPred.apply),
    FormDecoder(Definitions5.UtcshiftAddress, Address)(Form.UtcshiftAddress.apply),
    FormDecoder(Definitions5.UtmacctlNoOperands)(Form.UtmacctlNoOperands.apply),
    FormDecoder(Definitions5.UtmacctlAddress, Address)(Form.UtmacctlAddress.apply),
    FormDecoder(Definitions5.UtmacmdflushNoOperands)(Form.UtmacmdflushNoOperands.apply),
    FormDecoder(Definitions5.UtmaldgAddressAddress, Address, Address)(Form.UtmaldgAddressAddress.apply),
    FormDecoder(
      Definitions5.UtmaldgAddressAddressAddress,
      Address,
      Address,
      Address
    )(Form.UtmaldgAddressAddressAddress.apply),
    FormDecoder(Definitions5.UtmaldgAddressAddressReg, Address, Address, Register)(Form.UtmaldgAddressAddressReg.apply),
    FormDecoder(
      Definitions5.UtmaldgAddressAddressRegAddress,
      Address,
      Address,
      Register,
      Address
    )(Form.UtmaldgAddressAddressRegAddress.apply),
    FormDecoder(Definitions5.UtmapfAddressAddress, Address, Address)(Form.UtmapfAddressAddress.apply),
    FormDecoder(
      Definitions5.UtmapfAddressAddressAddress,
      Address,
      Address,
      Address
    )(Form.UtmapfAddressAddressAddress.apply),
    FormDecoder(Definitions5.UtmapfAddressAddressReg, Address, Address, Register)(Form.UtmapfAddressAddressReg.apply),
    FormDecoder(
      Definitions5.UtmapfAddressAddressRegAddress,
      Address,
      Address,
      Register,
      Address
    )(Form.UtmapfAddressAddressRegAddress.apply),
    FormDecoder(Definitions5.UtmaredgAddressAddress, Address, Address)(Form.UtmaredgAddressAddress.apply),
    FormDecoder(
      Definitions5.UtmaredgAddressAddressAddress,
      Address,
      Address,
      Address
    )(Form.UtmaredgAddressAddressAddress.apply),
    FormDecoder(Definitions5.UtmastgAddressAddress, Address, Address)(Form.UtmastgAddressAddress.apply),
    FormDecoder(
      Definitions5.UtmastgAddressAddressAddress,
      Address,
      Address,
      Address
    )(Form.UtmastgAddressAddressAddress.apply)
  )
}
