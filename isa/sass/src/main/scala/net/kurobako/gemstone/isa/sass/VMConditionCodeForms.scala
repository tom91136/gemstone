package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.isa.nvidia.VMIR.Instruction.*
import net.kurobako.gemstone.isa.sass.Instruction.*
import net.kurobako.gemstone.isa.sass.Instruction as SASSInstruction

/** Legacy `.CC` destinations whose condition-code effects are verified, and the flag state they leave behind. Flags an
  * instruction does not define become invalid rather than stale.
  */
private object VMConditionCodeForms {
  private def zeroDestination(operand: Option[Operand]): Boolean =
    operand.contains(Operand.Qualified(VMOperand.Zero, Vec(OperandSelector.CC)))

  /** Instructions whose `.X` form adds the condition-code carry. */
  def carryConsumer(operation: SASSInstruction): Boolean = operation match {
    case _: SASSInstruction.Iadd | _: SASSInstruction.Iadd32i | _: SASSInstruction.Isub | _: SASSInstruction.Iscadd |
        _: SASSInstruction.Iscadd32i | _: SASSInstruction.Imad | _: SASSInstruction.Xmad => true
    case _ => false
  }

  /** ENVY's GK110 encoding model gives plain IMAD U32 x U24 + U32 operand types; its flags are restricted to the
    * observed 24-bit immediate form.
    */
  def allFlags(using context: VMCompilationContext): Boolean = {
    import context.*
    def notBothNegated: Boolean = !(negatedSource(operands(1)) && negatedSource(operands(2)))
    operands.headOption.exists(VMOperand.has(_, OperandSelector.`CC`)) &&
    (operation match {
      case _: SASSInstruction.Iadd => !modifiers.contains(Modifier.`X`) && operands.size == 3 && notBothNegated
      case _: SASSInstruction.Iscadd | _: SASSInstruction.Iscadd32i =>
        modifiers.isEmpty && operands.size == 4 && notBothNegated &&
        VMOperand.integer(operands(3)).exists(shift => shift >= 0 && shift < 32)
      case _: SASSInstruction.Imad =>
        modifiers.isEmpty && operands.size == 4 && !operands.tail.exists(negatedSource) &&
        VMOperand.integer(operands(2)).exists(_ <= 0xffffff)
      case _: SASSInstruction.Xmad => modifiers.isEmpty
      case _: SASSInstruction.Lea  => VMIntegerCompiler.legacyLeaUpdatesCarry
      case _                       => false
    })
  }

  /** Legacy forms whose observed consumers read only the zero flag. */
  def zeroFlag(using context: VMCompilationContext): Boolean = {
    import context.*
    VMFloatingCompiler.zeroFlagFset ||
    (Set(35, 50, 60)(sm) && zeroDestination(operands.headOption) &&
      (operation match {
        case _: SASSInstruction.I2i    => modifiers == Vec(Modifier.U32, Modifier.U32) && operands.size == 2
        case _: SASSInstruction.Lop32i =>
          sm == 35 && modifiers == Vec(Modifier.AND, Modifier.X) && operands.size == 3 &&
          VMOperand.integer(operands(2)).contains(BigInt(65535))
        case _ => false
      }))
  }

  def finish(written: Set[VMConditionCodes.Flag])(using context: VMCompilationContext): Unit = {
    import context.*
    val integer = operation match {
      case _: SASSInstruction.Iadd | _: SASSInstruction.Iadd32i | _: SASSInstruction.Isub | _: SASSInstruction.Iscadd |
          _: SASSInstruction.Iscadd32i => true
      case _ => false
    }
    val flags                                                  = written.to(scala.collection.mutable.Set)
    def write(flag: VMConditionCodes.Flag, value: Value): Unit = {
      flags += flag
      emit(WriteConditionFlag(flag, value))
    }
    if (ccFlagsSupported) {
      writeConditionFlags()
      flags ++= VMConditionCodes.Flag.values
    } else if (zeroFlag) write(VMConditionCodes.Flag.Zero, Value.Predicate(VMRegisters.Predicate.Zero.slot))
    if (!ccFlagsSupported && (integer || VMOperand.is[SASSInstruction.Imad](operation)))
      write(VMConditionCodes.Flag.Carry, carryOut())
    if (!ccFlagsSupported && integer) {
      write(VMConditionCodes.Flag.Equal, Value.Predicate(VMRegisters.Predicate.Equal.slot))
      write(VMConditionCodes.Flag.LessThan, Value.Predicate(VMRegisters.Predicate.LessThan.slot))
    }
    VMConditionCodes.Flag.values.filterNot(flags).foreach { flag =>
      if (flag != VMConditionCodes.Flag.Zero || !VMOperand.is[SASSInstruction.Iset](operation))
        emit(InvalidateConditionFlag(flag))
    }
    if (!(VMOperand.is[SASSInstruction.Iadd3](operation) && verifiedLegacy))
      emit(InvalidateAddThreeCarry)
  }
}
