package net.kurobako.gemstone.isa.sass

import ModifierSemantics.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.isa.nvidia.VMIR.Instruction.*
import net.kurobako.gemstone.isa.sass.Instruction.*
import net.kurobako.gemstone.isa.sass.Instruction as SASSInstruction
import net.kurobako.gemstone.vm.*

/** Integer arithmetic, logic, bit manipulation, moves and register-producing comparisons. */
private object VMIntegerCompiler {
  private val comparisons = Set(Modifier.EQ, Modifier.NE, Modifier.LT, Modifier.LE, Modifier.GT, Modifier.GE)
  private val booleans    = Set(Modifier.AND, Modifier.OR, Modifier.XOR)
  private val xmadTypes   = Set(Modifier.S16, Modifier.U16)
  private val Low32       = Value.Immediate(0xffffffffL)

  def lower(using VMCompilationContext): Boolean = lowerConversionsAndAdditions

  private def lowerConversionsAndAdditions(using context: VMCompilationContext): Boolean = {
    import context.*
    (operation: @unchecked) match {
      case _: SASSInstruction.I2i
          if modifiers == Vec(Modifier.S16, Modifier.S8) || modifiers == Vec(Modifier.S16, Modifier.S16) =>
        signExtend()
        true
      case _: SASSInstruction.I2i if modifiers == Vec(Modifier.S32, Modifier.S32) =>
        requireOperands(2, "unsupported integer-to-integer conversion")
        store(
          operands.head,
          operands(1) match {
            case Operand.Unary(inner, negative, false, true) =>
              val result = unary(IntegerUnaryOperation.Absolute, source(inner))
              if (negative) emit(IntegerUnary(IntegerUnaryOperation.Negate, result.slot, result, 32))
              result
            case operand => source(operand)
          }
        )
        true
      case _: SASSInstruction.I2i
          if operands.headOption.contains(Operand.Qualified(OperandQueries.Zero, Vec(OperandSelector.CC))) =>
        if (!Set(35, 50, 60)(sm) || modifiers != Vec(Modifier.U32, Modifier.U32) || operands.size != 2)
          unsupported("unsupported legacy I2I condition-code conversion")
        emit(SetPredicate(
          VMRegisters.Predicate.Zero.slot,
          Comparison.Equal,
          NumericFormat.Unsigned(32),
          source(operands(1)),
          Value.Immediate(0L)
        ))
        emit(WriteConditionFlag(VMConditionCodes.Flag.Equal, Value.Predicate(VMRegisters.Predicate.Zero.slot)))
        true
      case _: SASSInstruction.I2i if modifiers == Vec(Modifier.U32, Modifier.U32) =>
        requireOperands(2, "unsupported integer-to-integer conversion")
        if (
          !OperandQueries.isRegister(operands.head) ||
          OperandQueries.has(operands.head, OperandSelector.CC)
        )
          unsupported("I2I.U32.U32 destination must be a general register")
        operands(1) match {
          case _: Operand.Unary => unsupported("I2I.U32.U32 source modifiers")
          case operand          => store(operands.head, source(operand))
        }
        true
      case _: SASSInstruction.Icmp =>
        compareSelect()
        true
      case _: SASSInstruction.Iset =>
        set()
        true
      case _: SASSInstruction.Iadd3
          if verifiedLegacy &&
            Set(Vec.empty[Modifier], Vec(Modifier.RS), Vec(Modifier.LS), Vec(Modifier.X))(modifiers) =>
        legacyAddThree()
        true
      case _: SASSInstruction.Uiadd3 if modifiers == Vec(Modifier.`64`) =>
        requireOperands(4, "UIADD3.64 requires three sources")
        val result = binary(IntegerOperation.Add, source(operands(1), 64), source(operands(2), 64), 64)
        emit(IntegerBinary(IntegerOperation.Add, result.slot, result, source(operands(3), 64), 64))
        store(operands.head, result, 64)
        true
      case _: SASSInstruction.Iadd3 | _: SASSInstruction.Uiadd3 =>
        addThree()
        true
      case _: SASSInstruction.Bfe =>
        bitfieldExtract()
        true
      case _: SASSInstruction.Bfi =>
        val _ = uniqueModifiers(Set.empty, "BFI")
        requireOperands(4, "BFI requires 4 operands")
        val (position, width) = field(operands(2))
        val result            = temp()
        emit(BitFieldInsert(result.slot, source(operands(1)), source(operands(3)), position, width, 32))
        store(operands.head, result)
        true
      case _ => lowerMinMaxAndVideo
    }
  }

  private def lowerMinMaxAndVideo(using context: VMCompilationContext): Boolean = {
    import context.*
    (operation: @unchecked) match {
      case _: SASSInstruction.Imnmx if modifiers.exists(Set(Modifier.XHI, Modifier.XLO)) =>
        extendedMinMax()
        true
      case _: SASSInstruction.Imnmx | _: SASSInstruction.Uimnmx | _: SASSInstruction.Vimnmx | _: SASSInstruction.Uvimnmx =>
        minMax()
        true
      case _: SASSInstruction.Vimnmx3 =>
        requireModifiers(Set(Modifier.U32, Modifier.S32))
        requireOperands(5, "expected three integer min/max inputs and a selector")
        val signed = !modifiers.contains(Modifier.U32)
        val inputs = operands.slice(1, 4).map(source(_))
        val low    = inputs.tail.foldLeft(inputs.head)(binary(IntegerOperation.Minimum(signed), _, _))
        val high   = inputs.tail.foldLeft(inputs.head)(binary(IntegerOperation.Maximum(signed), _, _))
        store(operands.head, select(low, high, predicateSource(operands(4))))
        true
      case _: SASSInstruction.Vmnmx =>
        videoMinMax()
        true
      case _: SASSInstruction.Viaddmnmx =>
        requireModifiers(Set(Modifier.U32))
        requireOperands(5, "VIADDMNMX operands")
        val signed = !modifiers.contains(Modifier.U32)
        val third  = source(operands(3))
        val sum    = binary(IntegerOperation.Add, source(operands(1)), source(operands(2)))
        emit(Select(
          destination(operands.head).slot,
          binary(IntegerOperation.Minimum(signed), sum, third),
          binary(IntegerOperation.Maximum(signed), sum, third),
          predicateSource(operands(4)).slot,
          32
        ))
        true
      case _: SASSInstruction.Viadd =>
        requireModifiers(Set.empty)
        requireOperands(3, "VIADD operands")
        store(operands.head, binary(IntegerOperation.Add, source(operands(1)), source(operands(2))))
        true
      case _: SASSInstruction.Vadd if modifiers == Vec(Modifier.U16, Modifier.U16, Modifier.MRG_16H) =>
        requireOperands(4, "VADD.U16.U16.MRG_16H requires 4 operands")
        val sum = binary(
          IntegerOperation.Add,
          half(operands(1), signed = false).selected,
          half(operands(2), signed = false).selected
        )
        emit(IntegerBinary(IntegerOperation.ShiftLeft, sum.slot, sum, Value.Immediate(16L), 32))
        store(
          operands.head,
          binary(
            IntegerOperation.BitwiseOr,
            sum,
            binary(IntegerOperation.BitwiseAnd, source(operands(3)), Value.Immediate(0xffffL))
          )
        )
        true
      case _ => lowerMultiplyAndAdd
    }
  }

  private def lowerMultiplyAndAdd(using context: VMCompilationContext): Boolean = {
    import context.*
    (operation: @unchecked) match {
      case _: SASSInstruction.Sgxt =>
        signExtendField()
        true
      case _: SASSInstruction.Xmad =>
        halfMultiplyAdd()
        true
      case _: SASSInstruction.Imad | _: SASSInstruction.Uimad =>
        multiplyAdd()
        true
      case _: SASSInstruction.Isub =>
        subtract()
        true
      case _: SASSInstruction.Imul | _: SASSInstruction.Imul32i =>
        requireModifiers(Set(Modifier.U32, Modifier.HI))
        store(
          operands.head,
          binary(
            if (modifiers.contains(Modifier.HI)) IntegerOperation.MultiplyHigh(!modifiers.contains(Modifier.U32))
            else IntegerOperation.MultiplyLow,
            source(operands(1)),
            source(operands(2))
          )
        )
        true
      case _: SASSInstruction.Iadd if modifiers == Vec(Modifier.`64`) =>
        requireOperands(3, "IADD.64 requires two sources")
        store(operands.head, binary(IntegerOperation.Add, source(operands(1), 64), source(operands(2), 64), 64), 64)
        true
      case _: SASSInstruction.Iadd
          if !legacy && modifiers.contains(Modifier.X) && operands.size == 4 &&
            OperandQueries.isPredicate(operands(3), allowNegated = true) =>
        requireModifiers(Set(Modifier.X, Modifier.U32))
        def extended(operand: Operand): Value =
          if (negatedSource(operand)) unary(IntegerUnaryOperation.BitwiseNot, source(OperandQueries.positive(operand)))
          else source(operand)
        val left   = extended(operands(1))
        val right  = extended(operands(2))
        val carry  = bit(predicateSource(operands(3)))
        val result = binary(IntegerOperation.Add, left, right)
        emit(IntegerBinary(IntegerOperation.Add, result.slot, result, carry, 32))
        store(operands.head, result)
        true
      case _: SASSInstruction.Iadd if operands.size == 4 && OperandQueries.isPredicate(operands(1)) =>
        addCarryOut()
        true
      case _: SASSInstruction.Iadd | _: SASSInstruction.Iadd32i | _: SASSInstruction.Iscadd | _: SASSInstruction.Iscadd32i =>
        add()
        true
      case _ => lowerLogicAndBits
    }
  }

  private def lowerLogicAndBits(using context: VMCompilationContext): Boolean = {
    import context.*
    (operation: @unchecked) match {
      case _: SASSInstruction.Shl | _: SASSInstruction.Shr =>
        requireModifiers(Set(Modifier.U32, Modifier.W))
        val amount =
          if (modifiers.contains(Modifier.W))
            binary(IntegerOperation.BitwiseAnd, source(operands(2)), Value.Immediate(31L))
          else source(operands(2))
        val shift =
          if (OperandQueries.is[SASSInstruction.Shl](operation)) IntegerOperation.ShiftLeft
          else if (modifiers.contains(Modifier.U32)) IntegerOperation.ShiftRightLogical
          else IntegerOperation.ShiftRightArithmetic
        store(operands.head, binary(shift, source(operands(1)), amount))
        true
      case _: SASSInstruction.Lop32i if modifiers == Vec(Modifier.AND, Modifier.X) =>
        if (
          sm != 35 || operands.size != 3 ||
          operands.head != Operand.Qualified(OperandQueries.Zero, Vec(OperandSelector.CC)) ||
          !OperandQueries.integer(operands(2)).contains(BigInt(65535))
        ) unsupported("unsupported legacy zero-flag logical extension")
        val previousZero = predicateTemp()
        copyPredicate(previousZero, readConditionFlag(VMConditionCodes.Flag.Zero))
        val highZero = compare(
          Comparison.Equal,
          NumericFormat.Unsigned(32),
          binary(IntegerOperation.BitwiseAnd, source(operands(1)), Value.Immediate(65535L)),
          Value.Immediate(0L)
        )
        emit(PredicateBinary(PredicateOperation.And, VMRegisters.Predicate.Zero.slot, previousZero.slot, highZero.slot))
        true
      case _: SASSInstruction.Lop if verifiedLegacy && modifiers.exists(Set(Modifier.Z, Modifier.NZ)) =>
        legacyLogicalPredicate()
        true
      case _: SASSInstruction.Lop | _: SASSInstruction.Lop32i =>
        requireModifiers(Set(Modifier.AND, Modifier.OR, Modifier.XOR, Modifier.PASS_B))
        val operation = modifiers.headOption.getOrElse(unsupported("missing logical operation"))
        store(
          operands.head,
          if (operation == Modifier.PASS_B) source(operands(2))
          else binary(integerOperation(operation), source(operands(1)), source(operands(2)))
        )
        true
      case _: SASSInstruction.Lop3 | _: SASSInstruction.Ulop3 =>
        logicThree()
        true
      case _: SASSInstruction.Shf | _: SASSInstruction.Ushf =>
        funnelShift()
        true
      case _: SASSInstruction.Lea if verifiedLegacy =>
        legacyLoadEffectiveAddress()
        true
      case _: SASSInstruction.Lea | _: SASSInstruction.Ulea =>
        loadEffectiveAddress()
        true
      case _: SASSInstruction.Iabs =>
        requireModifiers(Set.empty)
        emit(IntegerUnary(IntegerUnaryOperation.Absolute, destination(operands.head).slot, source(operands(1)), 32))
        true
      case _: SASSInstruction.Popc | _: SASSInstruction.Upopc =>
        requireModifiers(Set.empty)
        emit(IntegerUnary(
          IntegerUnaryOperation.PopulationCount,
          destination(operands.head).slot,
          source(operands(1)),
          32
        ))
        true
      case _: SASSInstruction.Flo | _: SASSInstruction.Uflo =>
        requireModifiers(Set(Modifier.U32, Modifier.SH))
        emit(BitFind(
          destination(operands.head).slot,
          source(operands(1)),
          32,
          !modifiers.contains(Modifier.U32),
          modifiers.contains(Modifier.SH)
        ))
        true
      case _: SASSInstruction.Brev | _: SASSInstruction.Ubrev =>
        requireModifiers(Set.empty)
        store(operands.head, unary(IntegerUnaryOperation.BitReverse, source(operands(1))))
        true
      case _: SASSInstruction.Bmsk | _: SASSInstruction.Ubmsk =>
        requireModifiers(Set(Modifier.W))
        emit(BitMask(
          destination(operands.head).slot,
          source(operands(1)),
          source(operands(2)),
          if (modifiers.contains(Modifier.W)) BitMaskMode.Wrap else BitMaskMode.Clamp
        ))
        true
      case _: SASSInstruction.Prmt | _: SASSInstruction.Uprmt =>
        requireModifiers(Set.empty)
        val result = temp()
        emit(BytePermute(
          result.slot,
          source(operands(1)),
          source(operands(3)),
          source(operands(2)),
          BytePermuteMode.Default
        ))
        store(operands.head, result)
        true
      case _ => lowerMovesAndSpecialRegisters
    }
  }

  private def lowerMovesAndSpecialRegisters(using context: VMCompilationContext): Boolean = {
    import context.*
    (operation: @unchecked) match {
      case _: SASSInstruction.Sel | _: SASSInstruction.Fsel | _: SASSInstruction.Usel | _: SASSInstruction.Ufsel =>
        requireModifiers(Set(Modifier.`64`))
        val width: ScalarBitWidth = if (modifiers.contains(Modifier.`64`)) 64 else 32
        val floating              = operation match {
          case _: SASSInstruction.Fsel | _: SASSInstruction.Ufsel => true
          case _                                                  => false
        }
        if (width.raw == 64 && floating) unsupported("FSEL.64")
        val left  = if (floating) fselSource(operands(1), 1) else source(operands(1), width)
        val right = if (floating) fselSource(operands(2), 2) else source(operands(2), width)
        store(operands.head, select(left, right, predicateSource(operands(3)), width), width)
        true
      case _: SASSInstruction.R2ur if modifiers == Vec(Modifier.BROADCAST) =>
        if (
          operands.size != 2 || !OperandQueries.isRegister(operands.head, RegisterBank.UR) ||
          !OperandQueries.isRegister(operands(1))
        ) unsupported("R2UR.BROADCAST requires a uniform destination and general source")
        store(operands.head, source(operands(1)))
        true
      case _: SASSInstruction.Mov | _: SASSInstruction.Umov | _: SASSInstruction.Mov32i | _: SASSInstruction.R2ur =>
        requireModifiers(Set(Modifier.`64`))
        val bits: ScalarBitWidth = if (modifiers.contains(Modifier.`64`)) 64 else 32
        textRelocation match {
          case Some(relocation) =>
            val address = Value.Immediate(boundSymbols.get(relocation.symbol).fold(0L)(_.raw) + relocation.addend)
            store(
              operands.head,
              if (relocation.high) binary(IntegerOperation.ShiftRightLogical, address, Value.Immediate(32L), 64)
              else address
            )
          case None => store(operands.head, source(operands(1), bits), bits)
        }
        true
      case _: SASSInstruction.S2r | _: SASSInstruction.S2ur =>
        requireModifiers(Set.empty)
        val name = operands(1) match {
          case Operand.SpecialRegister(name) => name
          case _                             => unsupported("expected special register")
        }
        store(
          operands.head,
          specialRegisters.getOrElse(name, unsupported(s"unsupported special register ${operands(1)}"))
        )
        true
      case _: SASSInstruction.Cs2r =>
        requireModifiers(Set(Modifier.`32`))
        if (operands(1) != Operand.SpecialRegister(SpecialRegister.SRZ)) unsupported("unsupported special register")
        store(operands.head, Value.Immediate(0L), if (modifiers.contains(Modifier.`32`)) 32 else 64)
        true
      case _ => false
    }
  }

  private val specialRegisters: Map[SpecialRegister, Value] = Map(
    SpecialRegister.SR_TID_X    -> Value.ThreadId(LaunchGeometry.Axis.X),
    SpecialRegister.SR_TID_Y    -> Value.ThreadId(LaunchGeometry.Axis.Y),
    SpecialRegister.SR_TID_Z    -> Value.ThreadId(LaunchGeometry.Axis.Z),
    SpecialRegister.SR_CTAID_X  -> Value.BlockId(LaunchGeometry.Axis.X),
    SpecialRegister.SR_CTAID_Y  -> Value.BlockId(LaunchGeometry.Axis.Y),
    SpecialRegister.SR_CTAID_Z  -> Value.BlockId(LaunchGeometry.Axis.Z),
    SpecialRegister.SR_LANEID   -> Value.LaneId,
    SpecialRegister.SR_CgaCtaId -> Value.Immediate(0L),
    // The downward-growing local stack cannot enter the shared-memory aperture.
    SpecialRegister.SR_LMEMHIOFF -> Value.Immediate(VMAddressSpace.SharedLimit),
    SpecialRegister.SR_SWINHI    -> Value.Immediate(VMAddressSpace.SharedWindowBase >>> 32),
    SpecialRegister.SR_GEMASK    -> Value.LaneMaskGreaterEqual,
    SpecialRegister.SR_GTMASK    -> Value.LaneMaskGreater,
    SpecialRegister.SR_LEMASK    -> Value.LaneMaskLessEqual,
    SpecialRegister.SR_LTMASK    -> Value.LaneMaskLess,
    SpecialRegister.SR_EQMASK    -> Value.LaneMaskEqual
  )

  /** The low-word result of an extended comparison, from the condition codes a preceding `.CC` comparison wrote. */
  def carriedComparison(comparison: Modifier)(using context: VMCompilationContext): Value.Predicate = {
    import context.*
    def flag(value: VMConditionCodes.Flag) = readConditionFlag(value)
    comparison match {
      case Modifier.EQ => flag(VMConditionCodes.Flag.Equal)
      case Modifier.NE => invert(flag(VMConditionCodes.Flag.Equal))
      case Modifier.LT => flag(VMConditionCodes.Flag.LessThan)
      case Modifier.GE => invert(flag(VMConditionCodes.Flag.LessThan))
      case Modifier.LE =>
        combine(PredicateOperation.Or, flag(VMConditionCodes.Flag.LessThan), flag(VMConditionCodes.Flag.Equal))
      case Modifier.GT =>
        invert(combine(PredicateOperation.Or, flag(VMConditionCodes.Flag.LessThan), flag(VMConditionCodes.Flag.Equal)))
      case other => unsupported(s"unsupported extended comparison ${other.name}")
    }
  }

  /** A three-input truth table: the OR of the minterms selected by `table`. */
  def truthTable[A](table: Int, inputs: Vec[A], inverses: Vec[A])(and: (A, A) => A, or: (A, A) => A, zero: A): A =
    (0 until 8).filter(bit => (table & (1 << bit)) != 0).foldLeft(zero) { (result, bit) =>
      val values = (0 until 3).map(index => if ((bit & (1 << (2 - index))) != 0) inputs(index) else inverses(index))
      or(result, and(and(values(0), values(1)), values(2)))
    }

  private def uniqueModifiers(allowed: Set[Modifier], description: String)(using
      context: VMCompilationContext
  ): Set[Modifier] = {
    import context.*
    val distinct = modifiers.toSet
    if (distinct.size != modifiers.size || modifiers.exists(modifier => !allowed(modifier)))
      unsupported(s"unsupported $description modifiers ${modifiers.mkString(".")}")
    distinct
  }

  private def compareValues(code: Modifier, left: Value, right: Value, signed: Boolean)(using
      context: VMCompilationContext
  ): Value.Predicate =
    context.compare(
      comparisonCode(code),
      if (signed) NumericFormat.Signed(32) else NumericFormat.Unsigned(32),
      left,
      right
    )

  private def signExtend()(using context: VMCompilationContext): Unit = {
    import context.*
    requireOperands(2, "I2I.S16 requires 2 operands")
    if (!OperandQueries.isRegister(operands.head))
      unsupported("I2I destination must be a general register")
    if (OperandQueries.has(operands.head, OperandSelector.CC))
      unsupported("I2I.S16 does not support condition-code destinations")
    val sourceWidth = if (modifiers(1) == Modifier.S8) 8 else 16
    val input       = OperandQueries.positive(operands(1))
    val negated     = OperandQueries.negative(operands(1))
    if (
      !(OperandQueries.isZero(input) || OperandQueries.isRegister(input)) ||
      (negated && sourceWidth != 16)
    )
      unsupported("I2I.S16.S8/S16 supports register operands only")
    val extended = convert(
      source(input),
      if (sourceWidth == 8) NumericFormat.Signed(8) else NumericFormat.Signed(16),
      NumericFormat.Signed(32)
    )
    store(
      operands.head,
      if (!negated) extended
      else convert(unary(IntegerUnaryOperation.Negate, extended), NumericFormat.Signed(16), NumericFormat.Signed(32))
    )
  }

  private def compareSelect()(using context: VMCompilationContext): Unit = {
    import context.*
    val mods = uniqueModifiers(comparisons + Modifier.U32, operation.mnemonic.raw)
    if (operands.size != 4 || !mods.exists(comparisons))
      unsupported("ICMP requires comparison modifier and four operands")
    val holds =
      compareValues(modifiers.find(comparisons).get, source(operands(3)), Value.Immediate(0L), !mods(Modifier.U32))
    store(operands.head, select(source(operands(1)), source(operands(2)), holds))
  }

  private def set()(using context: VMCompilationContext): Unit = {
    import context.*
    val mods = uniqueModifiers(comparisons ++ booleans ++ Set(Modifier.U32, Modifier.X), operation.mnemonic.raw)
    if (operands.size != 4 || !mods.exists(comparisons) || !mods.exists(booleans))
      unsupported("ISET requires comparison and predicate-combination modifiers")
    val comparison = modifiers.find(comparisons).get
    val combineOp  = modifiers.find(booleans).get
    if (mods(Modifier.X) && combineOp != Modifier.AND) unsupported("ISET.X supports only AND predicate composition")
    val signed   = !mods(Modifier.U32)
    val left     = source(operands(1))
    val right    = source(operands(2))
    val high     = compareValues(comparison, left, right, signed)
    val extended =
      if (!mods(Modifier.X)) high
      else {
        val low       = carriedComparison(comparison)
        val highEqual = compareValues(Modifier.EQ, left, right, signed)
        combine(
          PredicateOperation.Or,
          combine(PredicateOperation.And, low, highEqual),
          combine(PredicateOperation.And, high, invert(highEqual))
        )
      }
    val combined = combine(predicateOperation(combineOp), extended, predicateSource(operands(3)))
    val result   = select(Low32, Value.Immediate(0L), combined)
    store(operands.head, result)
    if (OperandQueries.has(operands.head, OperandSelector.CC)) setZeroFlag(result)
  }

  private def legacyAddThreeSource(operand: Operand)(using context: VMCompilationContext): Value =
    OperandQueries.integer(operand) match {
      case Some(value) if value >= -0x80000 && value <= 0xfffff =>
        Value.Immediate((if (value >= 0x80000) value - 0x100000 else value).toLong)
      case _ => context.source(OperandQueries.positive(operand))
    }

  private def legacyAddThree()(using context: VMCompilationContext): Unit = {
    import context.*
    val rs       = modifiers == Vec(Modifier.RS)
    val ls       = modifiers == Vec(Modifier.LS)
    val producer = modifiers.isEmpty && operands.headOption.exists(OperandQueries.has(_, OperandSelector.CC))
    val consumer = modifiers == Vec(Modifier.X)
    val plain    = modifiers.isEmpty && !producer
    requireOperands(4, "legacy IADD3 requires 4 operands")
    if (!OperandQueries.isRegister(operands.head, allowZero = plain))
      unsupported("IADD3 destination must be a general register")
    val destinationSelectors = OperandQueries.selectors(operands.head).filterNot(_ == OperandSelector.reuse)
    if (producer && destinationSelectors != Vec(OperandSelector.CC))
      unsupported("legacy IADD3.CC requires only a CC destination modifier")
    if ((consumer || plain) && destinationSelectors.nonEmpty)
      unsupported("legacy IADD3 does not support these destination modifiers")
    def immediate(operand: Operand) =
      OperandQueries.integer(operand).exists(value => value >= -0x80000 && value <= 0xfffff)
    def supported(operand: Operand): Boolean = {
      val plainOperand = OperandQueries.base(OperandQueries.positive(operand))
      val kind         = plainOperand match {
        case Operand.Register(RegisterBank.R, _) => true
        case Operand.ConstantBank(_, offset)     => OperandQueries.integer(offset).nonEmpty
        case other                               => immediate(other)
      }
      kind && (!OperandQueries.negative(operand) || OperandQueries.isRegister(plainOperand))
    }
    if (!operands.drop(1).forall(operand => supported(operand) || (plain && immediate(operand))))
      unsupported("legacy IADD3 has unsupported source operand")
    if (
      (rs || ls) &&
      !operands.drop(1).forall(operand =>
        OperandQueries.isRegister(operand) || OperandQueries.isZero(operand)
      )
    )
      unsupported("legacy shifted IADD3 supports register sources only")
    if (rs || ls) {
      def shifted(operand: Operand): Value = widen(
        if (
          OperandQueries.selectors(operand).exists(selector =>
            selector == OperandSelector.H0 || selector == OperandSelector.H1
          )
        )
          half(operand, signed = false).selected
        else source(operand)
      )
      val sum = binary(
        if (rs) IntegerOperation.ShiftRightLogical else IntegerOperation.ShiftLeft,
        binary(IntegerOperation.Add, shifted(operands(1)), shifted(operands(2)), 64),
        Value.Immediate(16L),
        64
      )
      emit(IntegerBinary(IntegerOperation.Add, sum.slot, sum, shifted(operands(3)), 64))
      store(operands.head, narrow(sum))
    } else if (plain) {
      val sources = operands.drop(1).map { operand =>
        val value = legacyAddThreeSource(operand)
        if (!OperandQueries.negative(operand) || OperandQueries.integer(operand).nonEmpty) value
        else unary(IntegerUnaryOperation.Negate, value)
      }
      val result = binary(IntegerOperation.Add, sources(0), sources(1))
      emit(IntegerBinary(IntegerOperation.Add, result.slot, result, sources(2), 32))
      store(operands.head, result)
    } else {
      if (operands.drop(1).count(OperandQueries.negative) > 1)
        unsupported("legacy IADD3 supports at most one negated source per instruction")
      val sources = operands.drop(1).map(operand => (legacyAddThreeSource(operand), OperandQueries.negative(operand)))
      emit(IntegerAddThree(
        destination(OperandQueries.without(operands.head, OperandSelector.CC)).slot,
        sources(0)._1,
        sources(1)._1,
        sources(2)._1,
        32,
        if (producer) AddThreeCarryMode.Produce else AddThreeCarryMode.Consume,
        sources(0)._2,
        sources(1)._2,
        sources(2)._2
      ))
    }
  }

  private def addThree()(using context: VMCompilationContext): Unit = {
    import context.*
    requireModifiers(Set(Modifier.X))
    if (modifiers.contains(Modifier.X)) {
      requireOperands(6, "IADD3.X carry outputs")
      val result = binary(IntegerOperation.Add, source(operands(1)), source(operands(2)))
      Vec(source(operands(3)), bit(predicateSource(operands(4))), bit(predicateSource(operands(5)))).foreach(value =>
        emit(IntegerBinary(IntegerOperation.Add, result.slot, result, value, 32))
      )
      store(operands.head, result)
    } else {
      val carries = operands.size - 4
      if (carries < 0 || carries > 2) unsupported("IADD3 carry operands")
      def extended(raw: Operand): Value =
        if (OperandQueries.negative(raw)) {
          val value = widen(unary(IntegerUnaryOperation.BitwiseNot, source(OperandQueries.positive(raw))))
          emit(IntegerBinary(IntegerOperation.Add, value.slot, value, Value.Immediate(1L), 64))
          value
        } else widen(source(raw))
      val first                   = 1 + carries
      val a                       = extended(operands(first))
      val b                       = extended(operands(first + 1))
      val c                       = extended(operands(first + 2))
      val sum                     = binary(IntegerOperation.Add, a, b, 64)
      def carry(index: Int): Unit =
        if (carries >= index)
          writePredicate(operands(index), compare(Comparison.GreaterThan, NumericFormat.Unsigned(64), sum, Low32))
      carry(1)
      emit(IntegerBinary(IntegerOperation.BitwiseAnd, sum.slot, sum, Low32, 64))
      emit(IntegerBinary(IntegerOperation.Add, sum.slot, sum, c, 64))
      carry(2)
      store(operands.head, narrow(sum))
    }
  }

  private def bitfieldExtract()(using context: VMCompilationContext): Unit = {
    import context.*
    val mods = uniqueModifiers(Set(Modifier.U32, Modifier.S32, Modifier.BREV), "BFE")
    requireOperands(3, "BFE requires 3 operands")
    if (mods(Modifier.U32) && mods(Modifier.S32)) unsupported("BFE cannot be both signed and unsigned")
    val input =
      if (mods(Modifier.BREV)) unary(IntegerUnaryOperation.BitReverse, source(operands(1))) else source(operands(1))
    val (position, width) = field(operands(2))
    val result            = temp()
    emit(BitFieldExtract(result.slot, input, position, width, 32, !mods(Modifier.U32)))
    store(operands.head, result)
  }

  /** The `(position, width)` bytes of a packed bit-field descriptor. */
  private def field(operand: Operand)(using context: VMCompilationContext): (Value, Value) = {
    import context.*
    OperandQueries.integer(operand) match {
      case Some(value) if value >= 0 && value <= 0xffff =>
        (Value.Immediate((value & 0xff).toLong), Value.Immediate(((value >> 8) & 0xff).toLong))
      case Some(_)                                => unsupported("bit-field descriptor exceeds 16 bits")
      case None if OperandQueries.isZero(operand) => (Value.Immediate(0L), Value.Immediate(0L))
      case None                                   =>
        val packed = source(operand)
        val width  = binary(IntegerOperation.ShiftRightLogical, packed, Value.Immediate(8L))
        emit(IntegerBinary(IntegerOperation.BitwiseAnd, width.slot, width, Value.Immediate(0xffL), 32))
        (binary(IntegerOperation.BitwiseAnd, packed, Value.Immediate(0xffL)), width)
    }
  }

  private def extendedMinMax()(using context: VMCompilationContext): Unit = {
    import context.*
    val mods = uniqueModifiers(Set(Modifier.U32, Modifier.XHI, Modifier.XLO), "IMNMX")
    val high = mods(Modifier.XHI)
    if (high == mods(Modifier.XLO)) unsupported("IMNMX requires exactly one of XHI or XLO")
    requireOperands(4, "extended IMNMX requires 4 operands")
    val selectA = operands(3) match {
      case Operand.Predicate(None, _, negated) => !negated
      case _                                   => unsupported("extended IMNMX supports only PT and !PT selection")
    }
    val signed  = !(mods(Modifier.U32) || !high)
    val a       = source(operands(1))
    val b       = source(operands(2))
    val chooseA =
      if (high) {
        if (!OperandQueries.has(operands.head, OperandSelector.CC)) unsupported("IMNMX.XHI must write CC")
        emit(WriteConditionFlag(VMConditionCodes.Flag.LessThan, compareValues(Modifier.LT, a, b, signed)))
        emit(WriteConditionFlag(VMConditionCodes.Flag.Equal, compareValues(Modifier.EQ, a, b, signed)))
        val less = compareValues(Modifier.LT, a, b, signed)
        if (selectA) combine(PredicateOperation.Or, less, compareValues(Modifier.EQ, a, b, signed)) else invert(less)
      } else {
        if (OperandQueries.has(operands.head, OperandSelector.CC)) unsupported("IMNMX.XLO does not write CC")
        val lowSelects = compare(
          if (selectA) Comparison.LessThanOrEqual else Comparison.GreaterThanOrEqual,
          NumericFormat.Unsigned(32),
          a,
          b
        )
        val highEqual = readConditionFlag(VMConditionCodes.Flag.Equal)
        val tie       = combine(PredicateOperation.And, highEqual, lowSelects)
        val highLess  = readConditionFlag(VMConditionCodes.Flag.LessThan)
        val highPick  = if (selectA) highLess else combine(PredicateOperation.And, invert(highLess), invert(highEqual))
        combine(PredicateOperation.Or, highPick, tie)
      }
    store(OperandQueries.without(operands.head, OperandSelector.CC), select(a, b, chooseA))
  }

  private def minMax()(using context: VMCompilationContext): Unit = {
    import context.*
    requireModifiers(Set(Modifier.U32, Modifier.S32, Modifier.U64, Modifier.S64))
    val outputs = operands.drop(1).takeWhile { case Operand.Predicate(_, _, _) => true; case _ => false }
    if (outputs.exists(value => !OperandQueries.isAlwaysPredicate(value)))
      unsupported("integer min/max predicate outputs")
    val data = operands.take(1) ++ operands.drop(1 + outputs.size)
    if (data.size != 4 && !(data.size == 5 && OperandQueries.isNeverPredicate(data.last)))
      unsupported("integer min/max extended comparison")
    val width: ScalarBitWidth = if (modifiers.exists(Set(Modifier.U64, Modifier.S64))) 64 else 32
    val signed                = !modifiers.exists(Set(Modifier.U32, Modifier.U64))
    val a                     = source(data(1), width)
    val b                     = source(data(2), width)
    val low                   = binary(IntegerOperation.Minimum(signed), a, b, width)
    val high                  = binary(IntegerOperation.Maximum(signed), a, b, width)
    store(data.head, select(low, high, predicateSource(data(3)), width), width)
  }

  private def videoMinMax()(using context: VMCompilationContext): Unit = {
    import context.*
    requireModifiers(Set(Modifier.MX, Modifier.MIN, Modifier.MAX, Modifier.ACC, Modifier.UD, Modifier.U32))
    val legacyNested = verifiedLegacy &&
      (modifiers == Vec(Modifier.MIN) || modifiers == Vec(Modifier.MX, Modifier.MAX)) && operands.size == 4 &&
      OperandQueries.isRegister(operands.head) && operands.tail.forall(OperandQueries.isRegister(_, allowZero = true))
    if ((sm != 35 && !legacyNested) || operands.size != 4) unsupported("unsupported video min/max form")
    val unsigned = modifiers.contains(Modifier.UD)
    if (modifiers.count(_ == Modifier.U32) != (if (unsigned) 2 else 0))
      unsupported("mixed-width or mixed-sign video min/max")
    val secondary = modifiers.filter(Set(Modifier.MIN, Modifier.MAX, Modifier.ACC))
    if (secondary.size != 1) unsupported("unsupported video min/max secondary operation")
    val signed   = !unsigned
    val selected = binary(
      if (modifiers.contains(Modifier.MX)) IntegerOperation.Maximum(signed) else IntegerOperation.Minimum(signed),
      source(operands(1)),
      source(operands(2))
    )
    store(operands.head, binary(integerOperation(secondary.head, signed), selected, source(operands(3))))
  }

  private def signExtendField()(using context: VMCompilationContext): Unit = {
    import context.*
    if (sm != 70 || instruction.guard.nonEmpty)
      unsupported("SGXT is supported only for the observed unpredicated sm_70 form")
    val _ = uniqueModifiers(Set(Modifier.U32), "SGXT")
    requireOperands(3, "SGXT requires 3 operands")
    val length = OperandQueries.integer(operands(2)) match {
      case Some(value) if value >= 0 && value <= 0x1ff => Value.Immediate(value.toLong)
      case Some(_) => unsupported("SGXT immediate width exceeds the measured 9-bit operand")
      case None if OperandQueries.isRegister(operands(2)) =>
        binary(IntegerOperation.Minimum(false), source(operands(2)), Value.Immediate(32L))
      case _ => unsupported("SGXT width must be a general register or non-negative immediate")
    }
    val result = temp()
    emit(BitFieldExtract(
      result.slot,
      source(operands(1)),
      Value.Immediate(0L),
      binary(IntegerOperation.Minimum(false), length, Value.Immediate(32L)),
      32,
      !modifiers.contains(Modifier.U32)
    ))
    store(operands.head, result)
  }

  private def halfMultiplyAdd()(using context: VMCompilationContext): Unit = {
    import context.*
    if (!verifiedLegacy) unsupported(s"unsupported XMAD architecture sm_$sm")
    val allowed =
      Set(Modifier.MRG, Modifier.PSL, Modifier.CBCC, Modifier.CLO, Modifier.CHI, Modifier.CSFU, Modifier.X) ++ xmadTypes
    val repeated = modifiers.groupBy(identity).exists((modifier, values) => values.size > 1 && modifier != Modifier.S16)
    if (repeated || modifiers.count(_ == Modifier.S16) > 2 || modifiers.exists(modifier => !allowed(modifier)))
      unsupported(s"unsupported XMAD modifiers ${modifiers.mkString(".")}")
    val mods = modifiers.toSet
    requireOperands(4, "XMAD requires 4 operands")
    val producesCarry = OperandQueries.has(operands.head, OperandSelector.CC)
    val carryIn       = mods(Modifier.X)
    if (carryIn && modifiers != Vec(Modifier.X)) unsupported("XMAD.X requires unmodified unsigned multiplication")
    if (producesCarry && modifiers.nonEmpty)
      unsupported("XMAD condition codes require unmodified unsigned multiplication")
    val signedTypes = modifiers.filter(xmadTypes)
    val signs       = signedTypes match {
      case Vec()                           => (false, false)
      case Vec(Modifier.S16, Modifier.U16) => (true, false)
      case Vec(Modifier.U16, Modifier.S16) => (false, true)
      case Vec(Modifier.S16, Modifier.S16) => (true, true)
      case _                               => unsupported("unsupported XMAD operand signedness")
    }
    val modes = modifiers.filter(Set(Modifier.CLO, Modifier.CHI))
    if (modes.size > 1) unsupported("XMAD cannot combine CLO and CHI")
    if (
      mods(Modifier.CBCC) &&
      (!OperandQueries.isRegister(operands(2)) || OperandQueries.isZero(operands(2)))
    )
      unsupported("XMAD.CBCC requires a general-register second source")
    if (mods(Modifier.MRG) && modifiers.exists(Set(Modifier.PSL, Modifier.CBCC, Modifier.CLO, Modifier.CHI)))
      unsupported("unsupported XMAD.MRG modifier combination")
    val a = half(operands(1), signs._1)
    val b = half(operands(2), signs._2)
    if (
      mods(Modifier.CSFU) &&
      (signedTypes != Vec(Modifier.S16, Modifier.S16) || !a.high || !b.high ||
        !OperandQueries.isZero(operands(3)) ||
        modifiers.exists(Set(Modifier.MRG, Modifier.PSL, Modifier.CBCC, Modifier.CLO, Modifier.CHI)))
    ) unsupported("XMAD.CSFU is bounded to compiler-observed S16.S16 H1,H1,RZ form")
    val secondBase = OperandQueries.base(operands(2))
    val constant   = secondBase match {
      case Operand.ConstantBank(_, _) => true
      case _                          => false
    }
    if (mods(Modifier.MRG) && !(OperandQueries.isRegister(secondBase, allowZero = true) || constant))
      unsupported("XMAD.MRG requires a register or constant-bank second source")
    val product = binary(IntegerOperation.MultiplyLow, a.selected, b.selected)
    val scaled  = if (mods(Modifier.PSL)) binary(IntegerOperation.ShiftLeft, product, Value.Immediate(16L)) else product
    val c       = source(operands(3))
    val addend  = modes.headOption match {
      case Some(Modifier.CLO) => binary(IntegerOperation.BitwiseAnd, c, Value.Immediate(0xffffL))
      case Some(Modifier.CHI) => binary(IntegerOperation.ShiftRightLogical, c, Value.Immediate(16L))
      case _                  => c
    }
    val sum = temp()
    if (producesCarry) {
      emit(IntegerAddWithCarry(sum.slot, scaled, addend, 32, false, true))
      updateIntegerCC(scaled, addend, sum, isSub = false, Some(carryOut()))
    } else if (carryIn) emit(IntegerAddWithCarry(sum.slot, scaled, addend, 32, true, false))
    else emit(IntegerBinary(IntegerOperation.Add, sum.slot, scaled, addend, 32))
    val adjusted = if (mods(Modifier.CSFU)) signedCorrection(a, b, sum) else sum
    val bHigh    = () => binary(IntegerOperation.ShiftLeft, b.raw, Value.Immediate(16L))
    store(
      operands.head,
      if (mods(Modifier.CBCC)) binary(IntegerOperation.Add, adjusted, bHigh())
      else if (mods(Modifier.MRG))
        binary(
          IntegerOperation.BitwiseOr,
          binary(IntegerOperation.BitwiseAnd, adjusted, Value.Immediate(0xffffL)),
          bHigh()
        )
      else adjusted
    )
  }

  private case class Half(selected: Value, raw: Value, high: Boolean)

  /** One 16-bit half of a source, selected by `.H0`/`.H1` and optionally sign-extended. */
  private def half(operand: Operand, signed: Boolean)(using context: VMCompilationContext): Half = {
    import context.*
    val selectors = OperandQueries.qualifiers(operand)
    if (selectors.size > 1 || selectors.exists(value => value != OperandSelector.H0 && value != OperandSelector.H1))
      unsupported(s"unsupported half-word source selectors $selectors")
    val high     = selectors.contains(OperandSelector.H1)
    val base     = OperandQueries.base(OperandQueries.positive(operand))
    val constant = base match {
      case Operand.ConstantBank(_, _) => true
      case _                          => false
    }
    if (
      !(OperandQueries.isRegister(base, allowZero = true) || constant ||
        OperandQueries.integer(base).nonEmpty)
    )
      unsupported(s"unsupported half-word source $operand")
    val raw = if (OperandQueries.negative(operand)) unary(IntegerUnaryOperation.Negate, source(base)) else source(base)
    val selected =
      if (high) binary(IntegerOperation.ShiftRightLogical, raw, Value.Immediate(16L))
      else binary(IntegerOperation.BitwiseAnd, raw, Value.Immediate(0xffffL))
    val value =
      if (!signed) selected
      else binary(
        IntegerOperation.ShiftRightArithmetic,
        binary(IntegerOperation.ShiftLeft, selected, Value.Immediate(16L)),
        Value.Immediate(16L)
      )
    Half(value, raw, high)
  }

  /** The XMAD.CSFU correction for signed high halves multiplied as if unsigned. */
  private def signedCorrection(a: Half, b: Half, sum: Value)(using context: VMCompilationContext): Value = {
    import context.*
    def nonzero(value: Value)  = compare(Comparison.NotEqual, NumericFormat.Unsigned(32), value, Value.Immediate(0L))
    def negative(value: Value) = nonzero(binary(IntegerOperation.ShiftRightLogical, value, Value.Immediate(31L)))
    def low(value: Value)      = binary(IntegerOperation.BitwiseAnd, value, Value.Immediate(0xffffL))
    def correction(condition: Value.Predicate) = select(Value.Immediate(65536L), Value.Immediate(0L), condition)
    val adjustA   = combine(PredicateOperation.And, negative(a.selected), nonzero(low(b.raw)))
    val adjustB   = combine(PredicateOperation.And, negative(b.selected), nonzero(low(a.raw)))
    val magnitude = binary(IntegerOperation.Add, correction(adjustA), correction(adjustB))
    binary(IntegerOperation.Add, sum, binary(IntegerOperation.Subtract, Value.Immediate(0L), magnitude))
  }

  private def multiplyAdd()(using context: VMCompilationContext): Unit = {
    import context.*
    requireModifiers(Set(
      Modifier.MOV,
      Modifier.SHL,
      Modifier.IADD,
      Modifier.U32,
      Modifier.WIDE,
      Modifier.HI,
      Modifier.X
    ))
    val high                  = modifiers.contains(Modifier.HI) && !legacy
    val wide                  = modifiers.contains(Modifier.WIDE) || high
    val width: ScalarBitWidth = if (wide) 64 else 32
    val signed                = !modifiers.contains(Modifier.U32)
    val carryOutput           = operands.size >= 5 && OperandQueries.isPredicate(operands(1), allowUniform = true)
    val first                 = if (carryOutput) 2 else 1
    val a                     = source(operands(first))
    val b                     = source(operands(first + 1))
    val c                     = source(operands(first + 2), width)
    val result                = binary(
      if (wide) { if (signed) IntegerOperation.MultiplyWideSigned32 else IntegerOperation.MultiplyWideUnsigned32 }
      else if (modifiers.contains(Modifier.HI)) IntegerOperation.MultiplyHigh(signed)
      else IntegerOperation.MultiplyLow,
      a,
      b,
      width
    )
    val product = if (carryOutput || ccFlagsSupported) {
      val copy = temp()
      emit(Move(copy.slot, result, width))
      copy
    } else result
    val conditionCode = OperandQueries.has(operands.head, OperandSelector.CC)
    if (legacy && !modifiers.contains(Modifier.X) && conditionCode && OperandQueries.negative(operands(first + 2))) {
      if (wide || modifiers.contains(Modifier.HI)) unsupported("negated extended IMAD addend")
      emit(IntegerSubtractWithBorrow(
        result.slot,
        result,
        source(OperandQueries.positive(operands(first + 2))),
        32,
        false,
        true
      ))
      noBorrowCarry()
    } else
      emit(IntegerAddWithCarry(result.slot, result, c, width, modifiers.contains(Modifier.X) && legacy, conditionCode))
    if (ccFlagsSupported) updateIntegerCC(product, c, result, isSub = false, Some(carryOut()))
    if (legacy && !ccFlagsSupported && conditionCode) {
      val carry = carryOut()
      emit(WriteConditionFlag(
        VMConditionCodes.Flag.Equal,
        compare(Comparison.Equal, NumericFormat.Unsigned(32), result, Value.Immediate(0L))
      ))
      emit(WriteConditionFlag(
        VMConditionCodes.Flag.LessThan,
        compare(Comparison.Equal, NumericFormat.Unsigned(32), carry, Value.Immediate(0L))
      ))
    }
    if (carryOutput && !OperandQueries.isAlwaysPredicate(operands(1)))
      emit(SetPredicate(
        predicate(operands(1)).slot,
        Comparison.LessThan,
        if (wide) NumericFormat.Unsigned(64) else NumericFormat.Unsigned(32),
        result,
        product
      ))
    if (modifiers.contains(Modifier.X) && !legacy) {
      if (operands.size != first + 4) unsupported("IMAD.X requires a carry predicate")
      emit(IntegerBinary(IntegerOperation.Add, result.slot, result, bit(predicateSource(operands.last), width), width))
    }
    // Volta and later IMAD.HI add a register pair before selecting the high word.
    if (high) store(operands.head, narrow(binary(IntegerOperation.ShiftRightLogical, result, Value.Immediate(32L), 64)))
    else store(operands.head, result, width)
  }

  /** Converts the borrow of the preceding subtract-with-borrow into the no-borrow carry SASS keeps. */
  private def noBorrowCarry()(using context: VMCompilationContext): Unit = {
    import context.*
    val borrow = temp()
    emit(IntegerSubtractWithBorrow(borrow.slot, Value.Immediate(0L), Value.Immediate(0L), 32, true, false))
    emit(IntegerUnary(IntegerUnaryOperation.BitwiseNot, borrow.slot, borrow, 32))
    emit(IntegerAddWithCarry(borrow.slot, borrow, Value.Immediate(1L), 32, false, true))
  }

  private def subtract()(using context: VMCompilationContext): Unit = {
    import context.*
    requireModifiers(Set(Modifier.X, Modifier.U32))
    val a             = source(operands(1))
    val b             = source(operands(2))
    val result        = temp()
    val conditionCode = OperandQueries.has(operands.head, OperandSelector.CC)
    if (modifiers.contains(Modifier.X) || conditionCode) {
      // SASS extended subtraction consumes the condition-code carry as a no-borrow bit: a - b - (1 - C) == a + ~b + C.
      // PTX subc uses the opposite borrow convention, so express it through addc.
      val inverted = unary(IntegerUnaryOperation.BitwiseNot, b)
      if (!modifiers.contains(Modifier.X))
        emit(IntegerAddWithCarry(temp().slot, Low32, Value.Immediate(1L), 32, false, true))
      emit(IntegerAddWithCarry(result.slot, a, inverted, 32, true, conditionCode))
    } else emit(IntegerBinary(IntegerOperation.Subtract, result.slot, a, b, 32))
    if (conditionCode) {
      emit(SetPredicate(VMRegisters.Predicate.LessThan.slot, Comparison.LessThan, NumericFormat.Unsigned(32), a, b))
      emit(SetPredicate(VMRegisters.Predicate.Equal.slot, Comparison.Equal, NumericFormat.Unsigned(32), a, b))
    }
    store(operands.head, result)
  }

  private def addCarryOut()(using context: VMCompilationContext): Unit = {
    import context.*
    requireModifiers(Set(Modifier.U32))
    val negativeLeft  = negatedSource(operands(2))
    val negativeRight = negatedSource(operands(3))
    if (negativeLeft && negativeRight) unsupported("IADD carry with two negated sources")
    val left   = source(if (negativeLeft) OperandQueries.positive(operands(2)) else operands(2))
    val right  = source(if (negativeRight) OperandQueries.positive(operands(3)) else operands(3))
    val result = temp()
    val carry  = temp()
    val borrow = negativeLeft || negativeRight
    if (borrow) {
      emit(IntegerSubtractWithBorrow(
        result.slot,
        if (negativeLeft) right else left,
        if (negativeLeft) left else right,
        32,
        false,
        true
      ))
      emit(IntegerSubtractWithBorrow(carry.slot, Value.Immediate(0L), Value.Immediate(0L), 32, true, false))
    } else {
      emit(IntegerAddWithCarry(result.slot, left, right, 32, false, true))
      emit(IntegerAddWithCarry(carry.slot, Value.Immediate(0L), Value.Immediate(0L), 32, true, false))
    }
    if (!OperandQueries.isAlwaysPredicate(operands(1)))
      emit(SetPredicate(
        predicate(operands(1)).slot,
        if (borrow) Comparison.Equal else Comparison.NotEqual,
        NumericFormat.Unsigned(32),
        carry,
        Value.Immediate(0L)
      ))
    store(operands.head, result)
  }

  private def add()(using context: VMCompilationContext): Unit = {
    import context.*
    val scaled = operation match {
      case _: SASSInstruction.Iscadd | _: SASSInstruction.Iscadd32i => true
      case _                                                        => false
    }
    requireOperands(if (scaled) 4 else 3, s"expected ${if (scaled) 4 else 3} operands")
    requireModifiers(Set(Modifier.X, Modifier.U32))
    val negA          = negatedSource(operands(1))
    val negB          = negatedSource(operands(2))
    val extended      = modifiers.contains(Modifier.X)
    val conditionCode = OperandQueries.has(operands.head, OperandSelector.CC)
    if (scaled && extended) unsupported("ISCADD does not consume carry")
    if (negA && negB && (extended || conditionCode))
      unsupported("IADD flags with two negated operands are not implemented")
    def operand(raw: Operand): Value =
      if (extended && negatedSource(raw)) unary(IntegerUnaryOperation.BitwiseNot, source(OperandQueries.positive(raw)))
      else source(raw)
    val a            = operand(operands(1))
    val b            = operand(operands(2))
    val result       = temp()
    val shifted      = if (scaled) binary(IntegerOperation.ShiftLeft, a, source(operands(3))) else a
    val positiveLeft =
      if (scaled && negA)
        binary(IntegerOperation.ShiftLeft, source(OperandQueries.positive(operands(1))), source(operands(3)))
      else if (negA) source(OperandQueries.positive(operands(1)))
      else shifted
    val subtractInputs =
      if (negA) Some((b, positiveLeft))
      else if (negB) Some((shifted, source(OperandQueries.positive(operands(2)))))
      else None
    val rawCarry = subtractInputs.filter(_ => !extended && conditionCode) match {
      // A negated source denotes subtraction. SASS carries no-borrow into IADD.X, whose negated source is
      // one's-complemented.
      case Some((subtractLeft, subtractRight)) =>
        emit(IntegerSubtractWithBorrow(result.slot, subtractLeft, subtractRight, 32, false, true))
        noBorrowCarry()
        None
      case None =>
        emit(IntegerAddWithCarry(result.slot, shifted, b, 32, extended, conditionCode))
        Option.when(ccFlagsSupported)(carryOut())
    }
    if (conditionCode) {
      emit(SetPredicate(
        VMRegisters.Predicate.Equal.slot,
        Comparison.Equal,
        NumericFormat.Unsigned(32),
        result,
        Value.Immediate(0L)
      ))
      if (negA)
        emit(SetPredicate(
          VMRegisters.Predicate.LessThan.slot,
          Comparison.LessThan,
          NumericFormat.Unsigned(32),
          b,
          positiveLeft
        ))
      else if (negB)
        emit(SetPredicate(
          VMRegisters.Predicate.LessThan.slot,
          Comparison.LessThan,
          NumericFormat.Unsigned(32),
          shifted,
          source(OperandQueries.positive(operands(2)))
        ))
      else
        emit(SetPredicate(
          VMRegisters.Predicate.LessThan.slot,
          Comparison.Equal,
          NumericFormat.Unsigned(32),
          carryOut(),
          Value.Immediate(0L)
        ))
      if (ccFlagsSupported) {
        val (lhs, rhs) = subtractInputs.getOrElse((shifted, b))
        updateIntegerCC(lhs, rhs, result, negA || negB, rawCarry)
      }
    }
    store(operands.head, result)
  }

  private def legacyLogicalPredicate()(using context: VMCompilationContext): Unit = {
    import context.*
    val extended  = modifiers.contains(Modifier.X)
    val operation = modifiers.filterNot(_ == Modifier.X)
    if (
      modifiers.count(_ == Modifier.X) > 1 || operation.size != 2 || !Set(Modifier.AND, Modifier.OR)(operation(0)) ||
      !Set(Modifier.Z, Modifier.NZ)(operation(1))
    ) unsupported("unsupported legacy logical predicate modifiers")
    if (
      operands.size != 4 || !OperandQueries.isPredicate(operands(0)) ||
      !OperandQueries.isRegister(operands(1), allowZero = true)
    )
      unsupported("invalid legacy logical predicate operands")
    val result = binary(integerOperation(operation(0)), source(operands(2)), source(operands(3)))
    store(operands(1), result)
    if (operands.head != OperandQueries.True) {
      val zero = compare(Comparison.Equal, NumericFormat.Unsigned(32), result, Value.Immediate(0L))
      if (extended) emit(PredicateBinary(
        PredicateOperation.And,
        zero.slot,
        zero.slot,
        readConditionFlag(VMConditionCodes.Flag.Zero).slot
      ))
      if (operation(1) == Modifier.Z) copyPredicate(predicate(operands.head), zero)
      else emit(PredicateUnary(predicate(operands.head).slot, zero.slot))
    }
  }

  private def logicThree()(using context: VMCompilationContext): Unit = {
    import context.*
    requireModifiers(Set(Modifier.LUT, Modifier.PAND, Modifier.NZ))
    val legacyPredicate = verifiedLegacy && modifiers.contains(Modifier.NZ) && operands.size == 6
    if (modifiers.contains(Modifier.NZ) && !legacyPredicate) unsupported("legacy LOP3 predicate form")
    val lop3          = OperandQueries.is[SASSInstruction.Lop3](operation)
    val withPredicate = operands.size == 7 || legacyPredicate
    val never         = OperandQueries.True.copy(negated = true)
    val inputs        =
      if (legacyPredicate) operands.tail :+ never
      else if (withPredicate) operands.tail
      else if (lop3 && verifiedLegacy && operands.size == 5 && modifiers == Vec(Modifier.LUT)) operands :+ never
      else operands
    val predicateLogic = lop3 && withPredicate && verifiedModern
    if (inputs.size != 6) unsupported("invalid LOP3 operands")
    if (modifiers.contains(Modifier.PAND) && !predicateLogic) unsupported("unsupported LOP3 predicate AND form")
    if (
      !OperandQueries.isNeverPredicate(inputs.last) &&
      !(predicateLogic && OperandQueries.isPredicate(inputs.last, allowNegated = true))
    )
      unsupported("LOP3 predicate input")
    // Preserve a negated input before an aliased predicate destination is written.
    val predicateInput = Option.when(withPredicate)(predicateSource(inputs.last))
    val values         = Vec(source(inputs(1)), source(inputs(2)), source(inputs(3)))
    val table          = number(inputs(4)).toInt
    if (table < 0 || table > 255) unsupported("invalid LOP3 truth table")
    val result = truthTable[Value](table, values, values.map(unary(IntegerUnaryOperation.BitwiseNot, _)))(
      binary(IntegerOperation.BitwiseAnd, _, _),
      binary(IntegerOperation.BitwiseOr, _, _),
      Value.Immediate(0L)
    )
    if (withPredicate && !OperandQueries.isAlwaysPredicate(operands.head))
      emit(PredicateBinary(
        if (modifiers.contains(Modifier.PAND)) PredicateOperation.And else PredicateOperation.Or,
        predicate(operands.head).slot,
        compare(Comparison.NotEqual, NumericFormat.Unsigned(32), result, Value.Immediate(0L)).slot,
        predicateInput.get.slot
      ))
    store(inputs.head, result)
  }

  private def funnelShift()(using context: VMCompilationContext): Unit = {
    import context.*
    requireModifiers(Set(
      Modifier.L,
      Modifier.R,
      Modifier.U32,
      Modifier.S32,
      Modifier.U64,
      Modifier.S64,
      Modifier.HI,
      Modifier.W
    ))
    val wide = temp()
    emit(MoveCombine(wide.slot, Vec(source(operands(1)), source(operands(3))), 32))
    val bits   = if (modifiers.exists(Set(Modifier.U64, Modifier.S64))) 64 else 32
    val amount =
      if (modifiers.contains(Modifier.W))
        binary(IntegerOperation.BitwiseAnd, source(operands(2)), Value.Immediate((bits - 1).toLong))
      else binary(IntegerOperation.Minimum(false), source(operands(2)), Value.Immediate(bits.toLong))
    emit(IntegerBinary(
      if (modifiers.contains(Modifier.L)) IntegerOperation.ShiftLeft
      else if (modifiers.exists(Set(Modifier.S32, Modifier.S64))) IntegerOperation.ShiftRightArithmetic
      else IntegerOperation.ShiftRightLogical,
      wide.slot,
      wide,
      amount,
      64
    ))
    if (modifiers.contains(Modifier.HI) || (legacy && modifiers.contains(Modifier.L)))
      emit(IntegerBinary(IntegerOperation.ShiftRightLogical, wide.slot, wide, Value.Immediate(32L), 64))
    store(operands.head, narrow(wide))
  }

  private def loadEffectiveAddress()(using context: VMCompilationContext): Unit = {
    import context.*
    requireModifiers(Set(Modifier.HI, Modifier.X, Modifier.SX32))
    val carryOutput = OperandQueries.isPredicate(operands(1), allowUniform = true)
    val first       = if (carryOutput) 2 else 1
    val negate      = OperandQueries.negative(operands(first))
    val complement  = OperandQueries.complemented(operands(first))
    val a           = source(if (negate || complement) OperandQueries.unaryValue(operands(first)) else operands(first))
    val b           = source(operands(first + 1))
    val result      = temp()
    if (modifiers.contains(Modifier.HI)) {
      val signExtended = modifiers.contains(Modifier.SX32)
      val upper        = if (signExtended) binary(IntegerOperation.ShiftRightArithmetic, a, Value.Immediate(31L))
      else source(operands(first + 2))
      val shift = operands(first + (if (signExtended) 2 else 3))
      emit(MoveCombine(result.slot, Vec(a, upper), 32))
      if (negate) emit(IntegerUnary(IntegerUnaryOperation.Negate, result.slot, result, 64))
      if (complement) emit(IntegerUnary(IntegerUnaryOperation.BitwiseNot, result.slot, result, 64))
      emit(IntegerBinary(IntegerOperation.ShiftLeft, result.slot, result, source(shift), 64))
      emit(IntegerBinary(IntegerOperation.ShiftRightLogical, result.slot, result, Value.Immediate(32L), 64))
    } else {
      if (modifiers.contains(Modifier.X)) unsupported("LEA.X without HI")
      val shifted = binary(IntegerOperation.ShiftLeft, a, source(operands(first + 2)))
      if (negate || complement) emit(IntegerUnary(IntegerUnaryOperation.BitwiseNot, shifted.slot, shifted, 32))
      emit(Convert(result.slot, shifted, NumericFormat.Unsigned(32), NumericFormat.Unsigned(64), None))
      if (negate) emit(IntegerBinary(IntegerOperation.Add, result.slot, result, Value.Immediate(1L), 64))
    }
    emit(IntegerBinary(IntegerOperation.Add, result.slot, result, widen(b), 64))
    if (modifiers.contains(Modifier.X))
      emit(IntegerBinary(IntegerOperation.Add, result.slot, result, bit(predicate(operands.last), 64), 64))
    if (carryOutput)
      writePredicate(operands(1), compare(Comparison.GreaterThan, NumericFormat.Unsigned(64), result, Low32))
    store(operands.head, narrow(result))
  }

  private def legacySignedImmediate(operand: Operand): Option[BigInt] =
    OperandQueries.integer(operand).filter(value => value >= -0x80000 && value <= 0x7ffff)

  private def legacyAddressSource(operand: Operand): Boolean =
    OperandQueries.qualifiers(operand).isEmpty &&
      (OperandQueries.base(operand) match {
        case Operand.Register(RegisterBank.R, _) => true
        case Operand.ConstantBank(_, offset)     => OperandQueries.integer(offset).nonEmpty
        case _                                   => false
      })

  /** The addend and shift of the low-word LEA forms verified on sm_50/sm_60. */
  private def legacyLowForm(operands: Vec[Operand]): Option[(Operand, Int)] = {
    def addend(operand: Operand) = legacyAddressSource(operand) || legacySignedImmediate(operand).nonEmpty
    operands match {
      case Vec(_, base, value) if legacyAddressSource(OperandQueries.positive(base)) && addend(value) =>
        Some(value -> 0)
      case Vec(_, base, value, shift) if legacyAddressSource(OperandQueries.positive(base)) && addend(value) =>
        OperandQueries.integer(shift).filter(amount => amount >= 0 && amount <= 31).map(amount => value -> amount.toInt)
      case _ => None
    }
  }

  def legacyLeaUpdatesCarry(using context: VMCompilationContext): Boolean = {
    import context.*
    verifiedLegacy && modifiers.isEmpty && operands.headOption.exists(operand =>
      OperandQueries.isRegister(operand) && OperandQueries.has(operand, OperandSelector.CC)
    ) && legacyLowForm(operands).nonEmpty
  }

  private def legacyLoadEffectiveAddress()(using context: VMCompilationContext): Unit = {
    import context.*
    val conditionCode = operands.headOption.exists(OperandQueries.has(_, OperandSelector.CC))
    if (modifiers.isEmpty && legacyLowForm(operands).nonEmpty) {
      if (!OperandQueries.isRegister(operands.head))
        unsupported("LEA destination must be a general register")
      val (addend, amount) = legacyLowForm(operands).get
      val negative         = OperandQueries.negative(operands(1))
      val shifted          =
        binary(IntegerOperation.ShiftLeft, source(OperandQueries.positive(operands(1))), Value.Immediate(amount.toLong))
      val right  = source(addend)
      val result =
        if (negative) binary(IntegerOperation.Subtract, right, shifted)
        else binary(IntegerOperation.Add, shifted, right)
      if (conditionCode && negative) updateIntegerCC(right, shifted, result, isSub = true, None)
      else if (conditionCode) {
        val sum   = binary(IntegerOperation.Add, widen(shifted), widen(right), 64)
        val carry = bit(compare(Comparison.GreaterThan, NumericFormat.Unsigned(64), sum, Low32))
        updateIntegerCC(shifted, right, result, isSub = false, Some(carry))
      }
      store(OperandQueries.without(operands.head, OperandSelector.CC), result)
    } else if (modifiers == Vec(Modifier.HI, Modifier.X) || modifiers == Vec(Modifier.HI)) {
      val carry = modifiers.contains(Modifier.X)
      if (operands.size != 5 || !OperandQueries.isRegister(operands.head) || conditionCode)
        unsupported("LEA.HI requires a general-register destination and four sources")
      if (
        !(if (carry) legacyAddressSource(OperandQueries.positive(operands(1))) else legacyAddressSource(operands(1))) ||
        !operands.slice(2, if (carry) 4 else 3).forall(legacyAddressSource(_))
      ) unsupported("LEA.HI has unsupported source operands")
      val amount = OperandQueries.integer(operands(4)).filter(value => value >= 0 && value <= 31)
        .getOrElse(unsupported("LEA.HI shift must be between 0 and 31")).toInt
      if (!carry && !OperandQueries.isZero(operands(3))) unsupported("LEA.HI without X requires RZ as its third source")
      val sourceLow   = source(if (carry) OperandQueries.positive(operands(1)) else operands(1))
      val baseHigh    = source(operands(2))
      val highFromLow =
        if (amount == 0) Value.Immediate(0L)
        else binary(IntegerOperation.ShiftRightLogical, sourceLow, Value.Immediate((32 - amount).toLong))
      if (carry) {
        val extraHigh = source(operands(3))
        val carryIn   = bit(readConditionFlag(VMConditionCodes.Flag.Carry))
        val sum       = binary(
          IntegerOperation.Add,
          highFromLow,
          binary(IntegerOperation.ShiftLeft, extraHigh, Value.Immediate(amount.toLong))
        )
        if (OperandQueries.negative(operands(1)))
          emit(IntegerUnary(IntegerUnaryOperation.BitwiseNot, sum.slot, sum, 32))
        emit(IntegerBinary(IntegerOperation.Add, sum.slot, sum, baseHigh, 32))
        emit(IntegerBinary(IntegerOperation.Add, sum.slot, sum, carryIn, 32))
        store(operands.head, sum)
      } else store(operands.head, binary(IntegerOperation.Add, highFromLow, baseHigh))
    } else unsupported(s"unsupported sm_$sm LEA form")
  }
}
