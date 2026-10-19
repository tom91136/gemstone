package net.kurobako.gemstone.isa.sass

import ModifierLowering.*
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
  private val PositiveZeroBits        = 0L
  private val NegativeZeroBits        = Long.MinValue
  private val predicateLogicModifiers = Set(Modifier.AND, Modifier.OR, Modifier.XOR)
  private val namedTruthTables        =
    Map(Modifier.AND -> 0x80L, Modifier.OR -> 0xfeL, Modifier.XOR -> 0x96L, Modifier.SEL -> 0xcaL)
  private val predicateSetModifiers         = Set(Modifier.BM, Modifier.BF, Modifier.AND, Modifier.OR, Modifier.XOR)
  private val predicatesToRegisterModifiers = Set(Modifier.B0, Modifier.B1, Modifier.B2, Modifier.B3)

  /** Bit order of the condition-code nibble in the CC bank and in the 0xf000 mask. */
  private val flagBits = Vec(
    VMConditionCodes.Flag.Zero,
    VMConditionCodes.Flag.Sign,
    VMConditionCodes.Flag.Carry,
    VMConditionCodes.Flag.Overflow
  )

  def lower(using context: VMCompilationContext): Boolean = {
    import context.*
    (operation.form: @unchecked) match {
      case _: Form.Isetp | _: Form.Uisetp | _: Form.Fsetp | _: Form.Ufsetp |
          _: Form.Dsetp =>
        set()
        true
      case _: Form.Psetp | _: Form.Upsetp =>
        requireModifiers(predicateLogicModifiers)
        // without a second operation the form writes one predicate, the first operation alone
        val (values, second) =
          if (modifiers.size == 1 && operands.size == 3)
            (Vec(operands.head, OperandQueries.True) ++ operands.tail :+ OperandQueries.True, Modifier.AND)
          else if (modifiers.size == 2 && operands.size == 5) (operands, modifiers(1))
          else unsupported("unsupported predicate logic operands")
        val input  = saved(predicateSource(values(4)))
        val result =
          combine(modifiers(0).predicateOperation, predicateSource(values(2)), predicateSource(values(3)))
        val inverse = invert(result)
        if (values.head != OperandQueries.True)
          emit(PredicateBinary(second.predicateOperation, predicate(values.head).slot, result.slot, input.slot))
        if (values(1) != OperandQueries.True)
          emit(PredicateBinary(second.predicateOperation, predicate(values(1)).slot, inverse.slot, input.slot))
        true
      case _: Form.Plop3 | _: Form.Uplop3 =>
        // the named operations are truth tables of the three inputs: a & b & c, a | b | c, a ^ b ^ c and a ? b : c
        val (outputs, sources, tables) = modifiers match {
          case Vec(Modifier.LUT) if operands.size == 7 =>
            (operands.take(2), operands.slice(2, 5), Vec(number(operands(5)), number(operands(6))))
          case Vec(Modifier.LUT) if operands.size == 5 =>
            (operands.take(1), operands.slice(1, 4), Vec(number(operands(4))))
          case Vec(operation) if namedTruthTables.contains(operation) && operands.size == 4 =>
            (operands.take(1), operands.drop(1), Vec(namedTruthTables(operation)))
          case _ => unsupported("unsupported PLOP3 form")
        }
        // a register or constant with SIGN reads as its sign bit
        val inputs = sources.map(operand =>
          if (OperandQueries.has(operand, OperandSelector.SIGN))
            compare(
              Comparison.LessThan,
              NumericFormat.Signed(32),
              source(OperandQueries.without(operand, OperandSelector.SIGN)),
              Value.Immediate(0L)
            )
          else saved(predicateSource(operand))
        )
        val results = tables.map { table =>
          if (table < 0 || table > 255) unsupported("invalid predicate LUT")
          VMIntegerCompiler.truthTable(table.toInt, inputs, inputs.map(invert))(
            combine(PredicateOperation.And, _, _),
            combine(PredicateOperation.Or, _, _),
            Value.Predicate(VMRegisters.Predicate.False.slot)
          )
        }
        outputs.zip(results).foreach(writePredicate(_, _))
        true
      case _: Form.P2r | _: Form.Up2ur =>
        predicatesToRegister()
        true
      case _: Form.R2p | _: Form.Ur2up =>
        registerToPredicates()
        true
      case _: Form.Pset =>
        requireModifiers(predicateSetModifiers)
        val operations = modifiers.filterNot(PartialFunction.cond(_) { case Modifier.BM | Modifier.BF => true })
        val values     = operations.size match {
          case 1 if operands.size == 3 => operands :+ OperandQueries.True
          case 2 if operands.size == 4 => operands
          case _                       => unsupported("unsupported PSET form")
        }
        if (!OperandQueries.isRegister(values.head, allowZero = true) || OperandQueries.selectors(values.head).nonEmpty)
          unsupported("unsupported PSET destination")
        val first  = combine(operations.head.predicateOperation, predicateSource(values(1)), predicateSource(values(2)))
        val result =
          combine(operations.lift(1).getOrElse(Modifier.AND).predicateOperation, first, predicateSource(values(3)))
        val truth = if (modifiers.contains(Modifier.BF)) 0x3f800000L else 0xffffffffL
        store(values.head, select(Value.Immediate(truth), Value.Immediate(0L), result))
        true
      case _: Form.Cset | _: Form.Csetp if legacy =>
        val csetp      = OperandQueries.is[Form.Csetp](operation)
        val operations = modifiers.filter(_.isBitwise)
        val test       =
          modifiers.filterNot(modifier =>
            modifier.isBitwise || PartialFunction.cond(modifier) {
              case Modifier.BM | Modifier.BF | Modifier.S => true
            }
          )
        val (destinations, input) = (csetp, operations, operands) match {
          case (true, Vec(_), Vec(p, q, Operand.Symbol("CC"), a)) => (Vec(p, q), a)
          case (true, Vec(), Vec(p, Operand.Symbol("CC")))        => (Vec(p, OperandQueries.True), OperandQueries.True)
          case (false, Vec(_), Vec(d, Operand.Symbol("CC"), a))   => (Vec(d), a)
          case (false, Vec(), Vec(d, Operand.Symbol("CC")))       => (Vec(d), OperandQueries.True)
          case _ => unsupported("unsupported condition-code test operands")
        }
        val tested = conditionTest(test match {
          case Vec(only) => only
          case _         => unsupported("condition-code test requires one test")
        })
        val joined = operations.headOption.getOrElse(Modifier.AND).predicateOperation
        val masked = saved(predicateSource(input))
        val result = combine(joined, tested, masked)
        if (csetp) {
          writePredicate(destinations.head, result)
          writePredicate(destinations(1), combine(joined, invert(tested), masked))
        } else {
          if (
            !OperandQueries.isRegister(destinations.head, allowZero = true) ||
            OperandQueries.selectors(destinations.head).nonEmpty
          )
            unsupported("unsupported CSET destination")
          val truth = if (modifiers.contains(Modifier.BF)) 0x3f800000L else 0xffffffffL
          store(destinations.head, select(Value.Immediate(truth), Value.Immediate(0L), result))
        }
        true
      case _ => false
    }
  }

  /** A legacy condition-code test of the flags an integer `.CC` destination writes: Z, S, C (set without borrow after a
    * subtraction) and O. Floating and coverage tests are not modelled.
    */
  private def conditionTest(test: Modifier)(using context: VMCompilationContext): Value.Predicate = {
    import context.*
    def flag(value: VMConditionCodes.Flag) = readConditionFlag(value)
    def zero                               = flag(VMConditionCodes.Flag.Zero)
    def carry                              = flag(VMConditionCodes.Flag.Carry)
    def sign                               = flag(VMConditionCodes.Flag.Sign)
    def overflow                           = flag(VMConditionCodes.Flag.Overflow)
    def less                               = combine(PredicateOperation.Xor, sign, overflow)
    test match {
      case Modifier.F | Modifier.FALSE                     => Value.Predicate(VMRegisters.Predicate.False.slot)
      case Modifier.T | Modifier.TRUE                      => Value.Predicate(VMRegisters.Predicate.True.slot)
      case Modifier.ZFT | Modifier.ZF1 | Modifier.EQ       => zero
      case Modifier.ZFF | Modifier.ZF0 | Modifier.NE       => invert(zero)
      case Modifier.SFT | Modifier.SF1 | Modifier.SIGN     => sign
      case Modifier.SFF | Modifier.SF0 | Modifier.NOT_SIGN => invert(sign)
      case Modifier.CFT | Modifier.CF1 | Modifier.CARRY | Modifier.HS => carry
      case Modifier.CFF | Modifier.NOT_CARRY | Modifier.LO            => invert(carry)
      case Modifier.OFT | Modifier.OF1 | Modifier.OVERFLOW            => overflow
      case Modifier.OFF | Modifier.NOT_OVERFLOW                       => invert(overflow)
      case Modifier.HI | Modifier.ABOVE          => combine(PredicateOperation.And, carry, invert(zero))
      case Modifier.LS | Modifier.BELOW_OR_EQUAL => combine(PredicateOperation.Or, invert(carry), zero)
      case Modifier.LT                           => less
      case Modifier.GE                           => invert(less)
      case Modifier.LE                           => combine(PredicateOperation.Or, less, zero)
      case Modifier.GT                           => combine(PredicateOperation.And, invert(less), invert(zero))
      case other                                 => unsupported(s"unsupported condition-code test ${other.name}")
    }
  }

  /** A copy that survives a destination aliasing the source predicate. */
  private def saved(value: Value.Predicate)(using context: VMCompilationContext): Value.Predicate = {
    val copy = context.predicateTemp()
    context.copyPredicate(copy, value)
    copy
  }

  /** The unsigned comparisons legacy encodings spell by name; the encoding's own signedness field still applies. */
  private val unsignedAliases =
    Map(Modifier.LO -> Modifier.LT, Modifier.LS -> Modifier.LE, Modifier.HI -> Modifier.GT, Modifier.HS -> Modifier.GE)
  private val setModifiers = comparisons ++ unsignedAliases.keySet ++ Set(
    Modifier.F,
    Modifier.T,
    Modifier.NAN,
    Modifier.NUM,
    Modifier.U32,
    Modifier.S32,
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
  )

  private def set()(using context: VMCompilationContext): Unit = {
    import context.*
    requireModifiers(setModifiers)
    val integer = operation.form match {
      case _: Form.Isetp | _: Form.Uisetp => true
      case _                              => false
    }
    val floating = operation.form match {
      case _: Form.Fsetp | _: Form.Ufsetp => true
      case _                              => false
    }
    val double = OperandQueries.is[Form.Dsetp](operation)
    val wide   = modifiers.exists(_.is64BitInteger)
    if (wide && (!integer || modifiers.contains(Modifier.X)))
      unsupported("unsupported 64-bit predicate comparison")
    val integerWidth: ScalarBitWidth = if (wide) 64 else 32
    if (modifiers.contains(Modifier.MAX) || modifiers.contains(Modifier.MIN)) doubleExtremum()
    else {
      val extended = modifiers.contains(Modifier.EX)
      val boolean  = modifiers.find(_.isBitwise)
      // without a Boolean operation the form writes one predicate, the comparison alone
      val values =
        if (boolean.nonEmpty) operands
        else Vec(operands.head, OperandQueries.True) ++ operands.slice(1, 3) ++
          (OperandQueries.True +: operands.drop(3))
      if (values.size != (if (extended) 6 else 5) || (extended && floating))
        unsupported("extended predicate comparison")
      val named      = modifiers.headOption.getOrElse(unsupported("missing comparison"))
      val comparison = unsignedAliases.getOrElse(named, named)
      val unsigned   = modifiers.contains(Modifier.U32) ||
        (unsignedAliases.contains(named) && !modifiers.contains(Modifier.S32))
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
        else if (unsigned) NumericFormat.Unsigned(32)
        else NumericFormat.Signed(32)
      lazy val left  = input(values(2))
      lazy val right = input(values(3))
      val result     = comparison match {
        // F and T ignore their sources
        case Modifier.F | Modifier.T =>
          val constant = predicateTemp()
          copyPredicate(
            constant,
            Value.Predicate(
              if (comparison == Modifier.T) VMRegisters.Predicate.True.slot else VMRegisters.Predicate.False.slot
            )
          )
          constant
        case _ => compare(comparison.comparisonCode, format, left, right)
      }
      if ((extended || modifiers.contains(Modifier.X)) && comparison != Modifier.F && comparison != Modifier.T) {
        val low = if (extended) predicateSource(values(5)) else VMIntegerCompiler.carriedComparison(comparison)
        comparison match {
          case Modifier.EQ => emit(PredicateBinary(PredicateOperation.And, result.slot, result.slot, low.slot))
          case Modifier.NE => emit(PredicateBinary(PredicateOperation.Or, result.slot, result.slot, low.slot))
          case Modifier.LT | Modifier.LE | Modifier.GT | Modifier.GE =>
            val equal =
              compare(
                Comparison.Equal,
                if (wide) NumericFormat.Unsigned(64) else NumericFormat.Unsigned(32),
                left,
                right
              )
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
      val operation = boolean.getOrElse(Modifier.AND)
      val masked    = saved(predicateSource(values(4)))
      if (!OperandQueries.isAlwaysPredicate(values(1)))
        emit(PredicateBinary(
          operation.predicateOperation,
          predicate(values(1)).slot,
          invert(result).slot,
          masked.slot
        ))
      if (!OperandQueries.isAlwaysPredicate(values(0)))
        emit(PredicateBinary(operation.predicateOperation, predicate(values(0)).slot, result.slot, masked.slot))
    }
  }

  /** DSETP.MAX/MIN selects whether the first source is the IEEE maximum or minimum, with signed zeros ordered. */
  private def doubleExtremum()(using context: VMCompilationContext): Unit = {
    import context.*
    val simple = operands.size == 3 && (modifiers == Vec(Modifier.MAX) || modifiers == Vec(Modifier.MIN))
    if (
      !OperandQueries.is[Form.Dsetp](operation) || !simple &&
      (!(modifiers == Vec(Modifier.MAX, Modifier.AND) || modifiers == Vec(Modifier.MIN, Modifier.AND)) ||
        operands.size != 5)
    ) unsupported("unsupported DSETP MAX/MIN form")
    val values =
      if (simple) Vec(operands.head, OperandQueries.True, operands(1), operands(2), OperandQueries.True) else operands
    val maximum = modifiers.contains(Modifier.MAX)
    val a       = doubleSource(values(2))
    val b       = doubleSource(values(3))
    val input   = saved(predicateSource(values(4)))
    val nanA    = compare(Comparison.Unordered, NumericFormat.IEEE(64), a, a)
    val nanB    = compare(Comparison.Unordered, NumericFormat.IEEE(64), b, b)
    val ordered = compare(if (maximum) Comparison.GreaterThan else Comparison.LessThan, NumericFormat.IEEE(64), a, b)
    def zero(value: Value, bits: Long) =
      compare(Comparison.Equal, NumericFormat.Unsigned(64), value, Value.Immediate(bits))
    val zeroTie =
      if (maximum) combine(PredicateOperation.And, zero(a, PositiveZeroBits), zero(b, NegativeZeroBits))
      else combine(PredicateOperation.And, zero(a, NegativeZeroBits), zero(b, PositiveZeroBits))
    val onlyB   = combine(PredicateOperation.And, invert(nanA), nanB)
    val chooseA = combine(
      PredicateOperation.And,
      combine(PredicateOperation.Or, combine(PredicateOperation.Or, ordered, zeroTie), onlyB),
      input
    )
    writePredicate(values.head, chooseA)
    if (!OperandQueries.isAlwaysPredicate(values(1)))
      copyPredicate(
        predicate(values(1)),
        combine(PredicateOperation.And, combine(PredicateOperation.And, nanA, nanB), input)
      )
  }

  /** The predicate a bit of the predicate file holds: P0 to P6 or UP0 to UP6, then the constant true predicate. */
  private def invalidMask(mask: Int, conditionBank: Boolean): Boolean =
    mask < 0 || (conditionBank && (mask & ~0xf) != 0) || ((mask & ~0xff) != 0 && mask != 0xf000)

  private def filePredicate(index: Int, uniform: Boolean): Value.Predicate =
    if (index == 7) Value.Predicate(VMRegisters.Predicate.True.slot)
    else Value.Predicate(VMRegisters.predicate(index = index.unsafe[Int in 0 ~ 6], uniform = uniform))

  private def predicatesToRegister()(using context: VMCompilationContext): Unit = {
    import context.*
    requireModifiers(predicatesToRegisterModifiers)
    val uniform       = OperandQueries.is[Form.Up2ur](operation)
    val conditionBank = operands.lift(1).contains(Operand.Symbol("CC"))
    val named         = conditionBank || operands.lift(1).contains(Operand.Symbol("PR"))
    val written       = if (named) operands.patch(1, Vec.empty[Operand], 1) else operands
    // without a base and mask the instruction reads the whole file
    val values =
      if (written.size == 1 && !conditionBank) written ++ Vec(OperandQueries.Zero, Operand.Integer(0xff)) else written
    if (values.size != 3) unsupported("invalid predicate read operands")
    if (conditionBank && !verifiedLegacy) unsupported("unsupported condition register bank")
    val shift = modifiers.headOption.fold(0) {
      case Modifier.B0 => 0
      case Modifier.B1 => 8
      case Modifier.B2 => 16
      case Modifier.B3 => 24
      case _           => unsupported("unsupported predicate byte selector")
    }
    def set(result: Value.Register, position: Int, condition: Value.Predicate): Unit =
      emit(IntegerBinary(
        IntegerOperation.BitwiseOr,
        result.slot,
        result,
        select(Value.Immediate(1L << position), Value.Immediate(0L), condition),
        32
      ))
    OperandQueries.integer(values(2)) match {
      case Some(raw) =>
        val mask   = raw.toInt
        val ccMask = mask == 0xf000
        if (invalidMask(mask, conditionBank)) unsupported("invalid predicate read mask")
        if (ccMask && (sm != 35 || shift != 0 || modifiers.nonEmpty))
          unsupported("P2R condition-code mask 0xf000 is supported only as unmodified sm_35 P2R")
        val result = binary(
          IntegerOperation.BitwiseAnd,
          source(values(1)),
          Value.Immediate((~(mask << shift)).toLong & 0xffffffffL)
        )
        if (ccMask || conditionBank)
          flagBits.zipWithIndex.filter((_, index) => ccMask || (mask & (1 << index)) != 0).foreach { (flag, index) =>
            set(result, (if (ccMask) 12 else shift) + index, readConditionFlag(flag))
          }
        else
          (0 to 7).filter(index => (mask & (1 << index)) != 0).foreach { index =>
            set(result, index + shift, filePredicate(index, uniform))
          }
        store(operands.head, result)
      case None =>
        if (conditionBank) unsupported("condition-code reads need an immediate mask")
        val file = temp()
        emit(Move(file.slot, Value.Immediate(0L), 32))
        (0 to 7).foreach(index => set(file, index, filePredicate(index, uniform)))
        val mask   = binary(IntegerOperation.BitwiseAnd, source(values(2)), Value.Immediate(0xffL))
        val placed = binary(IntegerOperation.ShiftLeft, mask, Value.Immediate(shift.toLong))
        val kept   =
          binary(IntegerOperation.BitwiseAnd, source(values(1)), unary(IntegerUnaryOperation.BitwiseNot, placed))
        val read = binary(
          IntegerOperation.ShiftLeft,
          binary(IntegerOperation.BitwiseAnd, file, mask),
          Value.Immediate(shift.toLong)
        )
        store(operands.head, binary(IntegerOperation.BitwiseOr, kept, read))
    }
  }

  private def registerToPredicates()(using context: VMCompilationContext): Unit = {
    import context.*
    requireModifiers(Set(Modifier.PR, Modifier.CC))
    val uniform = OperandQueries.is[Form.Ur2up](operation)
    val named   =
      if (uniform) Operand.Symbol("UPR") +: operands
      else if (operands.size == 2 && modifiers.size == 1) Vec(Operand.Symbol(modifiers.head.name)) ++ operands
      else operands
    // the mask defaults to the whole file
    val values = if (named.size == 2) named :+ Operand.Integer(0xff) else named
    if (values.size != 3) unsupported("invalid predicate register operands")
    val conditionBank = values.head == Operand.Symbol("CC")
    if (!Set(Operand.Symbol("PR"), Operand.Symbol("UPR"))(values.head) && !conditionBank)
      unsupported("unsupported predicate register bank")
    if (conditionBank && !verifiedLegacy) unsupported("unsupported condition register bank")
    val byte = OperandQueries.selectors(values(1)).filterNot(_ == OperandSelector.reuse) match {
      case Vec() | Vec(OperandSelector.B0) => 0
      case Vec(OperandSelector.B1)         => 1
      case Vec(OperandSelector.B2)         => 2
      case Vec(OperandSelector.B3)         => 3
      case _                               => unsupported("unsupported predicate source byte selector")
    }
    val value =
      if (byte != 0)
        binary(IntegerOperation.ShiftRightLogical, source(OperandQueries.base(values(1))), Value.Immediate(byte * 8L))
      else {
        val copy = temp()
        emit(Move(copy.slot, source(OperandQueries.base(values(1))), 32))
        copy
      }
    def bitSet(position: Int): Value.Register =
      binary(IntegerOperation.BitwiseAnd, value, Value.Immediate(1L << position))
    OperandQueries.integer(values(2)) match {
      case Some(raw) =>
        val mask   = raw.toInt
        val ccMask = mask == 0xf000
        if (invalidMask(mask, conditionBank)) unsupported("invalid predicate write mask")
        if (ccMask && (sm != 35 || byte != 0))
          unsupported("R2P condition-code mask 0xf000 is supported only for unmodified sm_35 register sources")
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
          copyPredicate(
            Value.Predicate(VMRegisters.Predicate.Equal.slot),
            Value.Predicate(VMRegisters.Predicate.Zero.slot)
          )
          emit(PredicateUnary(VMRegisters.Predicate.LessThan.slot, VMRegisters.Predicate.Carry.slot))
          // The integer CC register also feeds later IADD.X carry-in. PTX has one carry bit, so rebuild it from the saved
          // C bit.
          seedCarry(Value.Predicate(VMRegisters.Predicate.Carry.slot))
          writeConditionFlags()
        } else
          Vec.range[0, 7].filter(index => (mask & (1 << index)) != 0).foreach { index =>
            emit(SetPredicate(
              VMRegisters.predicate(index = index, uniform = uniform),
              Comparison.NotEqual,
              NumericFormat.Unsigned(32),
              bitSet(index),
              Value.Immediate(0L)
            ))
          }
      case None =>
        if (conditionBank) unsupported("condition-code writes need an immediate mask")
        val mask = source(values(2))
        Vec.range[0, 7].foreach { index =>
          val target   = Value.Predicate(VMRegisters.predicate(index = index, uniform = uniform)): Value.Predicate
          val selected = compare(
            Comparison.NotEqual,
            NumericFormat.Unsigned(32),
            binary(IntegerOperation.BitwiseAnd, mask, Value.Immediate(1L << index)),
            Value.Immediate(0L)
          )
          val bit = compare(Comparison.NotEqual, NumericFormat.Unsigned(32), bitSet(index), Value.Immediate(0L))
          copyPredicate(
            target,
            combine(
              PredicateOperation.Or,
              combine(PredicateOperation.And, selected, bit),
              combine(PredicateOperation.And, invert(selected), target)
            )
          )
        }
    }
  }
}
