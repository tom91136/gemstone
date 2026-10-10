package net.kurobako.gemstone.isa.sass

import ModifierSemantics.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.isa.nvidia.VMIR.Instruction.*
import net.kurobako.gemstone.isa.sass.Instruction.*
import net.kurobako.gemstone.isa.sass.Instruction as SASSInstruction

/** Comparisons and logic producing predicates, and moves between predicates or condition codes and registers. */
private object VMPredicateCompiler {

  /** Bit order of the condition-code nibble in the CC bank and in the 0xf000 mask. */
  private val flagBits = Vec(
    VMConditionCodes.Flag.Zero,
    VMConditionCodes.Flag.Sign,
    VMConditionCodes.Flag.Carry,
    VMConditionCodes.Flag.Overflow
  )

  def lower(using context: VMCompilationContext): Boolean = {
    import context.*
    (operation: @unchecked) match {
      case _: SASSInstruction.Isetp | _: SASSInstruction.Uisetp | _: SASSInstruction.Fsetp | _: SASSInstruction.Ufsetp |
          _: SASSInstruction.Dsetp =>
        set()
        true
      case _: SASSInstruction.Psetp =>
        requireModifiers(Set(Modifier.AND, Modifier.OR, Modifier.XOR))
        if (modifiers.size != 2 || operands.size != 5) unsupported("unsupported predicate logic operands")
        val input  = saved(predicateSource(operands(4)))
        val result =
          combine(predicateOperation(modifiers(0)), predicateSource(operands(2)), predicateSource(operands(3)))
        val inverse = invert(result)
        if (operands.head != OperandQueries.True)
          emit(PredicateBinary(
            predicateOperation(modifiers(1)),
            predicate(operands.head).slot,
            result.slot,
            input.slot
          ))
        if (operands(1) != OperandQueries.True)
          emit(PredicateBinary(predicateOperation(modifiers(1)), predicate(operands(1)).slot, inverse.slot, input.slot))
        true
      case _: SASSInstruction.Plop3 | _: SASSInstruction.Uplop3 =>
        requireModifiers(Set(Modifier.LUT))
        if (operands.size != 7 || !OperandQueries.isAlwaysPredicate(operands(1)))
          unsupported("PLOP3 with two predicate destinations")
        val inputs = operands.slice(2, 5).map(operand => saved(predicateSource(operand)))
        val table  = number(operands(5)).toInt
        if (table < 0 || table > 255) unsupported("invalid predicate LUT")
        val result = VMIntegerCompiler.truthTable(table, inputs, inputs.map(invert))(
          combine(PredicateOperation.And, _, _),
          combine(PredicateOperation.Or, _, _),
          Value.Predicate(VMRegisters.Predicate.False.slot)
        )
        writePredicate(operands.head, result)
        true
      case _: SASSInstruction.P2r =>
        predicatesToRegister()
        true
      case _: SASSInstruction.R2p =>
        registerToPredicates()
        true
      case _: SASSInstruction.Pset =>
        if (
          !verifiedLegacy || modifiers != Vec(Modifier.AND, Modifier.AND) || operands.size != 4 ||
          !OperandQueries.isRegister(operands.head, allowZero = true) ||
          OperandQueries.selectors(operands.head).nonEmpty ||
          !OperandQueries.isPredicate(operands(1), allowNegated = true) ||
          operands.drop(2) != Vec(OperandQueries.True, OperandQueries.True) || instruction.guard.nonEmpty
        ) unsupported("unsupported legacy PSET form")
        store(operands.head, select(Value.Immediate(0xffffffffL), Value.Immediate(0L), predicateSource(operands(1))))
        true
      case _: SASSInstruction.Csetp if modifiers == Vec(Modifier.NE, Modifier.AND) =>
        if (
          sm != 35 || operands != Vec(
            Operand.Predicate(Some(0), uniform = false, negated = false),
            OperandQueries.True,
            Operand.Symbol("CC"),
            OperandQueries.True
          )
        )
          unsupported("unsupported legacy condition-code predicate form")
        emit(PredicateBinary(
          PredicateOperation.And,
          VMRegisters.predicate(index = 0, uniform = false),
          invert(readConditionFlag(VMConditionCodes.Flag.Zero)).slot,
          VMRegisters.Predicate.True.slot
        ))
        true
      case _ => false
    }
  }

  /** A copy that survives a destination aliasing the source predicate. */
  private def saved(value: Value.Predicate)(using context: VMCompilationContext): Value.Predicate = {
    val copy = context.predicateTemp()
    context.copyPredicate(copy, value)
    copy
  }

  private def set()(using context: VMCompilationContext): Unit = {
    import context.*
    requireModifiers(comparisons ++ Set(
      Modifier.NAN,
      Modifier.NUM,
      Modifier.U32,
      Modifier.U64,
      Modifier.S64,
      Modifier.AND,
      Modifier.OR,
      Modifier.XOR,
      Modifier.EX,
      Modifier.X,
      Modifier.FTZ,
      Modifier.MAX,
      Modifier.MIN
    ))
    val integer = operation match {
      case _: SASSInstruction.Isetp | _: SASSInstruction.Uisetp => true
      case _                                                    => false
    }
    val floating = operation match {
      case _: SASSInstruction.Fsetp | _: SASSInstruction.Ufsetp => true
      case _                                                    => false
    }
    val double = OperandQueries.is[SASSInstruction.Dsetp](operation)
    val wide   = modifiers.exists(Set(Modifier.U64, Modifier.S64))
    if (wide && (!integer || modifiers.exists(Set(Modifier.EX, Modifier.X))))
      unsupported("unsupported 64-bit predicate comparison")
    val integerWidth: ScalarBitWidth = if (wide) 64 else 32
    if (modifiers.contains(Modifier.MAX) || modifiers.contains(Modifier.MIN)) doubleExtremum()
    else {
      val extended = modifiers.contains(Modifier.EX)
      if (operands.size != (if (extended) 6 else 5) || (extended && floating))
        unsupported("extended predicate comparison")
      def input(operand: Operand): Value =
        if (floating) {
          val value = floatSource(operand)
          if (modifiers.contains(Modifier.FTZ)) flushFloat(value) else value
        } else if (double) doubleSource(operand)
        else source(operand, integerWidth)
      val format =
        if (double) NumericFormat.IEEE(64)
        else if (floating) NumericFormat.IEEE(32)
        else if (modifiers.contains(Modifier.S64)) NumericFormat.Signed(64)
        else if (wide) NumericFormat.Unsigned(64)
        else if (modifiers.contains(Modifier.U32)) NumericFormat.Unsigned(32)
        else NumericFormat.Signed(32)
      val result = compare(comparisonCode(modifiers(0)), format, input(operands(2)), input(operands(3)))
      if (extended || modifiers.contains(Modifier.X)) {
        val low = if (extended) predicateSource(operands(5)) else VMIntegerCompiler.carriedComparison(modifiers(0))
        modifiers(0) match {
          case Modifier.EQ => emit(PredicateBinary(PredicateOperation.And, result.slot, result.slot, low.slot))
          case Modifier.NE => emit(PredicateBinary(PredicateOperation.Or, result.slot, result.slot, low.slot))
          case Modifier.LT | Modifier.LE | Modifier.GT | Modifier.GE =>
            val equal = compare(Comparison.Equal, NumericFormat.Unsigned(32), source(operands(2)), source(operands(3)))
            emit(PredicateBinary(PredicateOperation.And, result.slot, result.slot, invert(equal).slot))
            emit(PredicateBinary(
              PredicateOperation.Or,
              result.slot,
              result.slot,
              combine(PredicateOperation.And, low, equal).slot
            ))
          case _ => unsupported("extended ordered comparison")
        }
      }
      val boolean =
        modifiers.find(Set(Modifier.AND, Modifier.OR, Modifier.XOR)).getOrElse(unsupported("missing boolean operation"))
      val masked = saved(predicateSource(operands(4)))
      if (!OperandQueries.isAlwaysPredicate(operands(1)))
        emit(PredicateBinary(
          predicateOperation(boolean),
          predicate(operands(1)).slot,
          invert(result).slot,
          masked.slot
        ))
      if (!OperandQueries.isAlwaysPredicate(operands(0)))
        emit(PredicateBinary(predicateOperation(boolean), predicate(operands(0)).slot, result.slot, masked.slot))
    }
  }

  /** DSETP.MAX/MIN selects whether the first source is the IEEE maximum or minimum, with signed zeros ordered. */
  private def doubleExtremum()(using context: VMCompilationContext): Unit = {
    import context.*
    if (
      !OperandQueries.is[SASSInstruction.Dsetp](operation) ||
      !(modifiers == Vec(Modifier.MAX, Modifier.AND) || modifiers == Vec(Modifier.MIN, Modifier.AND)) ||
      operands.size != 5
    ) unsupported("unsupported DSETP MAX/MIN form")
    val maximum = modifiers.contains(Modifier.MAX)
    val a       = doubleSource(operands(2))
    val b       = doubleSource(operands(3))
    val input   = saved(predicateSource(operands(4)))
    val nanA    = compare(Comparison.Unordered, NumericFormat.IEEE(64), a, a)
    val nanB    = compare(Comparison.Unordered, NumericFormat.IEEE(64), b, b)
    val ordered = compare(if (maximum) Comparison.GreaterThan else Comparison.LessThan, NumericFormat.IEEE(64), a, b)
    def zero(value: Value, negative: Boolean) =
      compare(Comparison.Equal, NumericFormat.Unsigned(64), value, Value.Immediate(if (negative) Long.MinValue else 0L))
    val zeroTie =
      if (maximum) combine(PredicateOperation.And, zero(a, negative = false), zero(b, negative = true))
      else combine(PredicateOperation.And, zero(a, negative = true), zero(b, negative = false))
    val onlyB   = combine(PredicateOperation.And, invert(nanA), nanB)
    val chooseA = combine(
      PredicateOperation.And,
      combine(PredicateOperation.Or, combine(PredicateOperation.Or, ordered, zeroTie), onlyB),
      input
    )
    writePredicate(operands.head, chooseA)
    if (!OperandQueries.isAlwaysPredicate(operands(1)))
      copyPredicate(
        predicate(operands(1)),
        combine(PredicateOperation.And, combine(PredicateOperation.And, nanA, nanB), input)
      )
  }

  private def predicatesToRegister()(using context: VMCompilationContext): Unit = {
    import context.*
    requireModifiers(Set(Modifier.B1, Modifier.B2, Modifier.B3))
    val conditionBank = operands.lift(1).contains(Operand.Symbol("CC"))
    val values        = if (conditionBank && operands.size == 4) operands.patch(1, Vec.empty[Operand], 1) else operands
    if (values.size != 3) unsupported("invalid predicate read operands")
    if (conditionBank && !verifiedLegacy) unsupported("unsupported condition register bank")
    val shift = modifiers.headOption.fold(0) {
      case Modifier.B1 => 8
      case Modifier.B2 => 16
      case Modifier.B3 => 24
      case _           => unsupported("unsupported predicate byte selector")
    }
    val mask   = number(values(2)).toInt
    val ccMask = mask == 0xf000
    if (mask < 0 || (conditionBank && (mask & ~0xf) != 0) || ((mask & ~0x7f) != 0 && !ccMask))
      unsupported("invalid predicate read mask")
    if (ccMask && (sm != 35 || shift != 0 || modifiers.nonEmpty))
      unsupported("P2R condition-code mask 0xf000 is supported only as unmodified sm_35 P2R")
    val result =
      binary(IntegerOperation.BitwiseAnd, source(values(1)), Value.Immediate((~(mask << shift)).toLong & 0xffffffffL))
    def set(position: Int, condition: Value.Predicate): Unit =
      emit(IntegerBinary(
        IntegerOperation.BitwiseOr,
        result.slot,
        result,
        select(Value.Immediate(1L << position), Value.Immediate(0L), condition),
        32
      ))
    if (ccMask || conditionBank)
      flagBits.zipWithIndex.filter((_, index) => ccMask || (mask & (1 << index)) != 0).foreach { (flag, index) =>
        set((if (ccMask) 12 else shift) + index, readConditionFlag(flag))
      }
    else
      Vec.range[0, 7].filter(index => (mask & (1 << index.raw)) != 0).foreach { index =>
        set(index.raw + shift, Value.Predicate(VMRegisters.predicate(index = index, uniform = false)))
      }
    store(operands.head, result)
  }

  private def registerToPredicates()(using context: VMCompilationContext): Unit = {
    import context.*
    requireModifiers(Set(Modifier.PR, Modifier.CC))
    val values =
      if (operands.size == 2 && modifiers.size == 1) Vec(Operand.Symbol(modifiers.head.name)) ++ operands else operands
    if (values.size != 3) unsupported("invalid predicate register operands")
    val conditionBank = values.head == Operand.Symbol("CC")
    if (values.head != Operand.Symbol("PR") && !conditionBank) unsupported("unsupported predicate register bank")
    if (conditionBank && !verifiedLegacy) unsupported("unsupported condition register bank")
    val byte = OperandQueries.selectors(values(1)).filterNot(_ == OperandSelector.reuse) match {
      case Vec()                   => 0
      case Vec(OperandSelector.B1) => 1
      case Vec(OperandSelector.B2) => 2
      case Vec(OperandSelector.B3) => 3
      case _                       => unsupported("unsupported predicate source byte selector")
    }
    val value =
      if (byte != 0)
        binary(IntegerOperation.ShiftRightLogical, source(OperandQueries.base(values(1))), Value.Immediate(byte * 8L))
      else {
        val copy = temp()
        emit(Move(copy.slot, source(values(1)), 32))
        copy
      }
    val mask   = number(values(2)).toInt
    val ccMask = mask == 0xf000
    if (mask < 0 || (conditionBank && (mask & ~0xf) != 0) || ((mask & ~0x7f) != 0 && !ccMask))
      unsupported("invalid predicate write mask")
    if (ccMask && (sm != 35 || byte != 0))
      unsupported("R2P condition-code mask 0xf000 is supported only for unmodified sm_35 register sources")
    def bitSet(position: Int): Value.Register =
      binary(IntegerOperation.BitwiseAnd, value, Value.Immediate(1L << position))
    if (conditionBank)
      flagBits.zipWithIndex.filter((_, index) => (mask & (1 << index)) != 0).foreach { (flag, index) =>
        val bit = bitSet(index)
        emit(WriteConditionFlag(flag, bit))
        if (flag == VMConditionCodes.Flag.Zero) emit(WriteConditionFlag(VMConditionCodes.Flag.Equal, bit))
        if (flag == VMConditionCodes.Flag.Carry)
          emit(WriteConditionFlag(
            VMConditionCodes.Flag.LessThan,
            compare(Comparison.Equal, NumericFormat.Unsigned(32), bit, Value.Immediate(0L))
          ))
      }
    else if (ccMask) {
      flagBits.map(flag => VMRegisters.condition(flag)).zipWithIndex.foreach { (flag, index) =>
        emit(SetPredicate(
          flag,
          Comparison.NotEqual,
          NumericFormat.Unsigned(32),
          bitSet(12 + index),
          Value.Immediate(0L)
        ))
      }
      copyPredicate(Value.Predicate(VMRegisters.Predicate.Equal.slot), Value.Predicate(VMRegisters.Predicate.Zero.slot))
      emit(PredicateUnary(VMRegisters.Predicate.LessThan.slot, VMRegisters.Predicate.Carry.slot))
      // The integer CC register also feeds later IADD.X carry-in. PTX has one carry bit, so rebuild it from the saved
      // C bit.
      seedCarry(Value.Predicate(VMRegisters.Predicate.Carry.slot))
      writeConditionFlags()
    } else
      Vec.range[0, 7].filter(index => (mask & (1 << index.raw)) != 0).foreach { index =>
        emit(SetPredicate(
          VMRegisters.predicate(index = index, uniform = false),
          Comparison.NotEqual,
          NumericFormat.Unsigned(32),
          bitSet(index.raw),
          Value.Immediate(0L)
        ))
      }
  }
}
