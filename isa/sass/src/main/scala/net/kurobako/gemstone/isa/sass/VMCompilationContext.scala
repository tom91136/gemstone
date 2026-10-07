package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.Instruction.*
import net.kurobako.gemstone.isa.nvidia.VMIR.{Instruction as VMInstruction, *}
import net.kurobako.gemstone.isa.sass.Instruction.*
import net.kurobako.gemstone.memory.*

private object VMCompilationContext {

  /** Forms whose condition-code, carry and generic-address behaviour is verified against CUDA 11.8 sm_50/sm_60 output.
    */
  def verifiedLegacy(sm: Int): Boolean = Set(50, 60)(sm)
}

/** Instruction-local lowering facilities shared by the instruction families. Architectural state is explicit VMIR. */
private trait VMCompilationContext {
  def module: Cubin.Module
  def kernel: Cubin.Kernel
  def instruction: DecodedInstruction
  def modifiers: Vec[Modifier]
  def addresses: Vec[Int]
  def returnBases: Vec[Int]
  def boundSymbols: Map[NonEmpty[String], GuestAddress]
  def reducedRadians: Option[Value.Register]
  def textRelocation: Option[Cubin.TextRelocation]

  /** Whether this instruction's `.CC` destination writes every condition flag. */
  def ccFlagsSupported: Boolean

  def temp(): Value.Register
  def predicateTemp(): Value.Predicate
  def emit(instruction: VMInstruction): Unit
  def guarded(instruction: VMInstruction, predicate: Value.Predicate, negated: Boolean = false): Unit
  def branch(target: Long, predicate: Option[ExecutionPredicate] = None): Unit
  def dispatch(target: Value.Register, destinations: Vec[Int]): Unit

  def source(operand: Operand, bits: ScalarBitWidth = 32): Value
  def floatSource(operand: Operand): Value
  def doubleSource(operand: Operand): Value
  def fselSource(operand: Operand, index: Int): Value
  def address(
      operand: Operand,
      global: Boolean,
      sharedUniform: Boolean = false,
      sharedAccess: Boolean = false,
      allowGlobalU32UniformBase: Boolean = false
  ): Value
  def store(destination: Operand, value: Value, bits: ScalarBitWidth = 32): Unit
  def readConditionFlag(flag: VMConditionCodes.Flag): Value.Predicate
  def writeConditionFlags(): Unit
  def updateIntegerCC(lhs: Value, rhs: Value, result: Value, isSub: Boolean, carry: Option[Value]): Unit

  final def operands: Vec[Operand]  = instruction.operands
  final def sm: Int                 = module.sm.raw
  final def generation: Generation  = Generation.of(module.sm)
  final def legacy: Boolean         = generation != Generation.Modern
  final def verifiedLegacy: Boolean = VMCompilationContext.verifiedLegacy(sm)

  /** Conversions, rounding and predicate logic verified against sm_70 to sm_89 compiler output. */
  final def verifiedModern: Boolean              = Set(70, 75, 80, 86, 89)(sm)
  final def operation: Instruction               = instruction.operation
  final def unsupported(detail: String): Nothing = throw IllegalArgumentException(detail)

  final def requireModifiers(allowed: Set[Modifier]): Unit =
    if (!modifiers.forall(allowed))
      unsupported(s"unsupported modifiers ${modifiers.filterNot(allowed).map(_.name).mkString(".")}")

  final def requireOperands(count: Int, description: String): Unit =
    if (operands.size != count) unsupported(description)

  final def number(value: Operand): Long = VMOperand.integer(value)
    .filter(value => value >= Long.MinValue && value <= ((BigInt(1) << 64) - 1))
    .fold(unsupported(s"expected integer operand, found $value"))(_.toLong)

  /** A direct code address operand. */
  final def codeAddress(value: Operand): Int = {
    val raw = number(value)
    if (raw < 0 || raw > Int.MaxValue) unsupported(s"code address is out of range: $raw")
    raw.toInt
  }

  final def barrierId(value: Operand): Int >= 0 =
    number(value).toInt.refine[Int >= 0].toOption.filter(_.raw.toLong == number(value))
      .getOrElse(unsupported(s"barrier id is outside Int range: ${number(value)}"))

  final def literal(value: Operand): Value = Value.Immediate(number(value))

  /** A general register operand without selectors. */
  final def bareRegister(value: Operand, allowZero: Boolean = false): Boolean =
    VMOperand.qualifiers(value).isEmpty && VMOperand.isRegister(value, allowZero = allowZero)

  /** The general register underlying `value` once its reuse flag is dropped. */
  final def registerBase(value: Operand, name: String, allowZero: Boolean = false): Operand.Register =
    VMOperand.base(VMOperand.without(value, OperandSelector.`reuse`)) match {
      case register: Operand.Register if VMOperand.isRegister(register, allowZero = allowZero) => register
      case _ => unsupported(s"unsupported $name")
    }

  final def register(value: Operand, add: Int = 0): Value =
    if (VMOperand.isZero(value)) Value.Immediate(0L) else destination(value, add)

  final def destination(value: Operand, add: Int = 0): Value.Register = VMOperand.base(value) match {
    case Operand.Register(bank @ (RegisterBank.R | RegisterBank.UR), index) =>
      val end = (index.raw + add).refine[Int in 0 ~ 254].toOption.filter(_ => add >= 0)
        .getOrElse(unsupported(s"register range exceeds $bank bank: $value + $add")).widen[Int in 0 ~ 255]
      Value.Register(if (bank == RegisterBank.R) VMRegisters.general(end) else VMRegisters.uniform(end))
    case _ => unsupported(s"expected writable register, found $value")
  }

  /** A predicate destination; `PT` and `!PT` name the constant predicates. */
  final def predicate(value: Operand): Value.Predicate = value match {
    case Operand.Predicate(None, _, negated) =>
      Value.Predicate(if (negated) VMRegisters.Predicate.False.slot else VMRegisters.Predicate.True.slot)
    case Operand.Predicate(Some(index), uniform, false) => Value.Predicate(VMRegisters.predicate(index, uniform))
    case _                                              => unsupported(s"expected predicate register, found $value")
  }

  final def predicateSource(value: Operand): Value.Predicate = value match {
    case operand: Operand.Predicate if operand.negated => invert(predicate(operand.copy(negated = false)))
    case other                                         => predicate(other)
  }

  final def executionPredicate(value: Operand): ExecutionPredicate = value match {
    case operand: Operand.Predicate =>
      ExecutionPredicate(predicate(operand.copy(negated = false)).slot, operand.negated)
    case _ => unsupported(s"expected execution predicate, found $value")
  }

  /** Writes a predicate destination unless it is the discarded `PT`. */
  final def writePredicate(destination: Operand, value: Value.Predicate): Unit =
    if (!VMOperand.isAlwaysPredicate(destination)) copyPredicate(predicate(destination), value)

  final def negatedSource(value: Operand): Boolean = VMOperand.negative(value) && VMOperand.integer(value).isEmpty

  final def binary(
      operation: IntegerOperation,
      left: Value,
      right: Value,
      width: ScalarBitWidth = 32
  ): Value.Register = {
    val result = temp()
    emit(IntegerBinary(operation, result.slot, left, right, width))
    result
  }

  final def unary(operation: IntegerUnaryOperation, value: Value, width: ScalarBitWidth = 32): Value.Register = {
    val result = temp()
    emit(IntegerUnary(operation, result.slot, value, width))
    result
  }

  final def select(
      whenTrue: Value,
      whenFalse: Value,
      condition: Value.Predicate,
      width: ScalarBitWidth = 32
  ): Value.Register = {
    val result = temp()
    emit(Select(result.slot, whenTrue, whenFalse, condition.slot, width))
    result
  }

  /** 1 when `condition` holds, else 0. */
  final def bit(condition: Value.Predicate, width: ScalarBitWidth = 32): Value.Register =
    select(Value.Immediate(1L), Value.Immediate(0L), condition, width)

  final def compare(comparison: Comparison, format: NumericFormat, left: Value, right: Value): Value.Predicate = {
    val result = predicateTemp()
    emit(SetPredicate(result.slot, comparison, format, left, right))
    result
  }

  final def combine(operation: PredicateOperation, left: Value.Predicate, right: Value.Predicate): Value.Predicate = {
    val result = predicateTemp()
    emit(PredicateBinary(operation, result.slot, left.slot, right.slot))
    result
  }

  final def invert(value: Value.Predicate): Value.Predicate = {
    val result = predicateTemp()
    emit(PredicateUnary(result.slot, value.slot))
    result
  }

  final def copyPredicate(destination: Value.Predicate, value: Value.Predicate): Unit =
    emit(MovePredicate(destination.slot, value))

  final def convert(
      value: Value,
      from: NumericFormat,
      to: NumericFormat,
      rounding: Option[ConversionRounding] = None
  ): Value.Register = {
    val result = temp()
    emit(Convert(result.slot, value, from, to, rounding))
    result
  }

  /** Zero-extends a 32-bit value to 64 bits. */
  final def widen(value: Value): Value.Register = convert(value, NumericFormat.Unsigned(32), NumericFormat.Unsigned(64))

  /** The low 32 bits of a 64-bit value. */
  final def narrow(value: Value): Value.Register =
    convert(value, NumericFormat.Unsigned(64), NumericFormat.Unsigned(32))

  /** The register pair `operand`, `operand + 1` as one 64-bit value. */
  final def pair(operand: Operand): Value.Register = {
    val result = temp()
    emit(MoveCombine(result.slot, Vec(register(operand), register(operand, 1)), 32))
    result
  }

  /** Binary32 subnormals flushed to zero with their sign retained. */
  final def flushFloat(value: Value): Value = {
    val magnitude = binary(IntegerOperation.BitwiseAnd, value, Value.Immediate(0x7fffffffL))
    val sign      = binary(IntegerOperation.BitwiseAnd, value, Value.Immediate(0x80000000L))
    select(sign, value, compare(Comparison.LessThan, NumericFormat.Unsigned(32), magnitude, Value.Immediate(0x800000L)))
  }

  final def setZeroFlag(value: Value): Unit =
    emit(WriteConditionFlag(
      VMConditionCodes.Flag.Zero,
      compare(Comparison.Equal, NumericFormat.Unsigned(32), value, Value.Immediate(0L))
    ))

  /** The carry produced by the preceding add-with-carry, as 0 or 1. */
  final def carryOut(): Value.Register = {
    val carry = temp()
    emit(IntegerAddWithCarry(carry.slot, Value.Immediate(0L), Value.Immediate(0L), 32, true, false))
    carry
  }

  /** Loads the carry flag into the arithmetic carry consumed by the next add-with-carry. */
  final def seedCarry(condition: Value.Predicate): Unit =
    emit(IntegerAddWithCarry(temp().slot, Value.Immediate(0xffffffffL), bit(condition), 32, false, true))

  final def label(address: Long): NonEmpty[String] = s"L${address.toHexString}"
}
