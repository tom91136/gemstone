package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.isa.nvidia.VMIR.Instruction.*
import net.kurobako.gemstone.isa.sass.Instruction.*
import net.kurobako.gemstone.isa.sass.Instruction as SASSInstruction

/** Typed lowering for the verified sm_50/sm_60 address-generation forms. */
private object VMAddressCompiler {
  private def fail(reason: String): Nothing = throw IllegalArgumentException(reason)

  private def signedImmediate(operand: Operand): Option[BigInt] =
    VMOperand.integer(operand).filter(value => value >= -0x80000 && value <= 0x7ffff)

  private def source(operand: Operand): Boolean =
    VMOperand.selectors(VMOperand.without(operand, OperandSelector.`reuse`)).isEmpty &&
      (VMOperand.base(operand) match {
        case Operand.Register(RegisterBank.R, _) => true
        case Operand.ConstantBank(_, offset)     => VMOperand.integer(offset).nonEmpty
        case _                                   => false
      })

  private def offsetSource(operand: Operand): Boolean = source(VMOperand.positive(operand))

  private def addressAddend(operand: Operand): Boolean = source(operand) || signedImmediate(operand).nonEmpty

  private def lowForm(operands: Vec[Operand]): Option[(Operand, Int)] = operands match {
    case Vec(_, base, addend) if offsetSource(base) && addressAddend(addend) => Some(addend -> 0)
    case Vec(_, base, addend, shift)
        if offsetSource(base) && addressAddend(addend) =>
      VMOperand.integer(shift).filter(value => value >= 0 && value <= 31).map(amount => addend -> amount.toInt)
    case _ => None
  }

  private def destination(operand: Operand): Boolean = VMOperand.isRegister(operand, RegisterBank.R)

  def updatesCarry(sm: Int, operation: SASSInstruction, modifiers: Vec[Modifier], operands: Vec[Operand]): Boolean =
    Set(50, 60)(sm) && (operation match { case _: SASSInstruction.Lea => true; case _ => false }) &&
      modifiers.isEmpty &&
      operands.headOption.exists { operand =>
        destination(operand) && VMOperand.has(operand, OperandSelector.`CC`) && lowForm(operands).nonEmpty
      }

  def compile(context: VMCompilationContext): Boolean = {
    val instruction = context.instruction
    val modifiers   = instruction.modifiers
    val operands    = instruction.operands
    instruction.operation match {
      case _: SASSInstruction.Lea if Set(50, 60)(context.sm) =>
        if (modifiers.isEmpty && lowForm(operands).nonEmpty) compileLow(context, operands)
        else if (modifiers == Vec(Modifier.`HI`, Modifier.`X`)) compileHighCarry(context, operands)
        else if (modifiers == Vec(Modifier.`HI`)) compileHigh(context, operands)
        else fail(s"unsupported sm_${context.sm} LEA form")
      case _ => false
    }
  }

  private def compileLow(context: VMCompilationContext, operands: Vec[Operand]): Boolean = {
    if (operands.size != 3 && operands.size != 4) fail("LEA requires destination, source, and addend")
    if (!destination(operands.head)) fail("LEA destination must be a general register")
    val (addend, amount) = lowForm(operands).getOrElse(fail("unsupported LEA low form"))
    val negative         = VMOperand.negative(operands(1))
    val left             = context.source(VMOperand.positive(operands(1)))
    val shifted          = context.temp()
    context.emit(IntegerBinary(
      IntegerOperation.ShiftLeft,
      shifted.name,
      left,
      Value.Immediate(amount.toLong),
      32
    ))
    val right  = context.source(addend)
    val result = context.temp()
    context.emit(IntegerBinary(
      if (negative) IntegerOperation.Subtract else IntegerOperation.Add,
      result.name,
      if (negative) right else shifted,
      if (negative) shifted else right,
      32
    ))
    if (VMOperand.has(operands.head, OperandSelector.`CC`) && negative)
      context.updateIntegerCC(right, shifted, result, isSub = true, None)
    else if (VMOperand.has(operands.head, OperandSelector.`CC`)) {
      val wideLeft  = context.temp("wide")
      val wideRight = context.temp("wide")
      val wideSum   = context.temp("wide")
      context.emit(Convert(wideLeft.name, shifted, ConversionType.U32, ConversionType.U64, None))
      context.emit(Convert(wideRight.name, right, ConversionType.U32, ConversionType.U64, None))
      context.emit(IntegerBinary(IntegerOperation.Add, wideSum.name, wideLeft, wideRight, 64))
      val carry = context.temp("q")
      context.emit(SetPredicate(
        carry.name,
        Comparison.GreaterThan,
        ConversionType.U64.format,
        wideSum,
        Value.Immediate(0xffffffffL)
      ))
      val carryValue = context.temp()
      context.emit(Select(carryValue.name, Value.Immediate(1L), Value.Immediate(0L), carry.name, 32))
      context.updateIntegerCC(shifted, right, result, isSub = false, Some(carryValue))
    }
    context.store(VMOperand.without(operands.head, OperandSelector.`CC`), result)
    true
  }

  private def compileHighCarry(context: VMCompilationContext, operands: Vec[Operand]): Boolean = {
    validateHigh(operands, carry = true)
    val negative       = VMOperand.negative(operands(1))
    val sourceLow      = context.source(VMOperand.positive(operands(1)))
    val baseHigh       = context.source(operands(2))
    val extraHigh      = context.source(operands(3))
    val amount         = VMOperand.integer(operands(4)).get.toInt
    val carryPredicate = context.readConditionFlag(VMConditionCodes.Flag.Carry)
    val carry          = context.temp()
    context.emit(Select(carry.name, Value.Immediate(1L), Value.Immediate(0L), carryPredicate.name, 32))
    val highFromLow = if (amount == 0) Value.Immediate(0L)
    else {
      val result = context.temp()
      context.emit(IntegerBinary(
        IntegerOperation.ShiftRightLogical,
        result.name,
        sourceLow,
        Value.Immediate((32 - amount).toLong),
        32
      ))
      result
    }
    val shiftedExtra = context.temp()
    context.emit(IntegerBinary(
      IntegerOperation.ShiftLeft,
      shiftedExtra.name,
      extraHigh,
      Value.Immediate(amount.toLong),
      32
    ))
    val sum = context.temp()
    context.emit(IntegerBinary(IntegerOperation.Add, sum.name, highFromLow, shiftedExtra, 32))
    if (negative) context.emit(IntegerUnary(IntegerUnaryOperation.BitwiseNot, sum.name, sum, 32))
    context.emit(IntegerBinary(IntegerOperation.Add, sum.name, sum, baseHigh, 32))
    context.emit(IntegerBinary(IntegerOperation.Add, sum.name, sum, carry, 32))
    context.store(operands.head, sum)
    true
  }

  private def compileHigh(context: VMCompilationContext, operands: Vec[Operand]): Boolean = {
    validateHigh(operands, carry = false)
    if (!VMOperand.isZero(operands(3))) fail("LEA.HI without X requires RZ as its third source")
    val sourceLow   = context.source(operands(1))
    val baseHigh    = context.source(operands(2))
    val amount      = VMOperand.integer(operands(4)).get.toInt
    val highFromLow = if (amount == 0) Value.Immediate(0L)
    else {
      val result = context.temp()
      context.emit(IntegerBinary(
        IntegerOperation.ShiftRightLogical,
        result.name,
        sourceLow,
        Value.Immediate((32 - amount).toLong),
        32
      ))
      result
    }
    val result = context.temp()
    context.emit(IntegerBinary(IntegerOperation.Add, result.name, highFromLow, baseHigh, 32))
    context.store(operands.head, result)
    true
  }

  private def validateHigh(operands: Vec[Operand], carry: Boolean): Unit = {
    if (operands.size != 5 || !destination(operands.head) || VMOperand.has(operands.head, OperandSelector.`CC`))
      fail("LEA.HI requires a general-register destination and four sources")
    if (
      !(if (carry) offsetSource(operands(1)) else source(operands(1))) ||
      !operands.slice(2, if (carry) 4 else 3).forall(operand => source(operand))
    )
      fail("LEA.HI has unsupported source operands")
    if (VMOperand.integer(operands(4)).forall(value => value < 0 || value > 31))
      fail("LEA.HI shift must be between 0 and 31")
  }
}
