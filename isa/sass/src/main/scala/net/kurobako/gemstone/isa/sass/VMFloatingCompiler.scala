package net.kurobako.gemstone.isa.sass

import ModifierSemantics.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.isa.nvidia.VMIR.Instruction.*
import net.kurobako.gemstone.isa.sass.Instruction.*
import net.kurobako.gemstone.isa.sass.Instruction as SASSInstruction

/** Scalar, double and packed floating-point arithmetic, comparisons that produce registers, and conversions. */
private object VMFloatingCompiler {
  private val roundings = Set(Modifier.RN, Modifier.RM, Modifier.RP, Modifier.RZ)

  def lower(using context: VMCompilationContext): Boolean = {
    import context.*
    (operation: @unchecked) match {
      case _: SASSInstruction.Fadd2 =>
        floatPair(FloatOperation.Add)
        true
      case _: SASSInstruction.Fmul2 =>
        floatPair(FloatOperation.Multiply)
        true
      case _: SASSInstruction.Hadd2
          if modifiers.isEmpty && operands.lift(1).exists(VMOperand.has(_, OperandSelector.F32)) =>
        scalarToHalf()
        true
      case _: SASSInstruction.Hadd2 if modifiers.isEmpty =>
        packedBinary(FloatOperation.Add, "HADD2")
        true
      case _: SASSInstruction.Hadd2 if modifiers == Vec(Modifier.F32) =>
        halfToScalar()
        true
      case _: SASSInstruction.Hmul2 if modifiers == Vec(Modifier.F32) =>
        halfMultiplyToScalar()
        true
      case _: SASSInstruction.Hmul2 if modifiers.isEmpty =>
        packedBinary(FloatOperation.Multiply, "HMUL2")
        true
      case _: SASSInstruction.Hadd2 | _: SASSInstruction.Hmul2 =>
        unsupported(s"unsupported ${operation.mnemonic.raw} modifiers ${modifiers.mkString(".")}")
        true
      case _: SASSInstruction.Hfma2 =>
        packedFma()
        true
      case _: SASSInstruction.Hsetp2 =>
        packedCompare()
        true
      case _: SASSInstruction.Hmnmx2 =>
        packedMinMax()
        true
      case _: SASSInstruction.F2f if modifiers == Vec(Modifier.F16, Modifier.F32) =>
        floatToHalf()
        true
      case _: SASSInstruction.F2fp =>
        packHalves()
        true
      case _: SASSInstruction.Fmnmx | _: SASSInstruction.Dmnmx =>
        minMax()
        true
      case _: SASSInstruction.Fcmp =>
        compareSelect()
        true
      case _: SASSInstruction.F2f if verifiedModern && modifiers == Vec(Modifier.F32, Modifier.F64) =>
        if (operands.size != 2 || !VMOperand.isRegister(operands.head)) unsupported("invalid F2F.F32.F64 operands")
        store(
          operands.head,
          convert(
            doubleSource(operands(1)),
            NumericFormat.IEEE(64),
            NumericFormat.IEEE(32),
            Some(ConversionRounding.RN)
          )
        )
        true
      case _: SASSInstruction.F2f if verifiedModern && modifiers == Vec(Modifier.F64, Modifier.F32) =>
        if (operands.size != 2 || !VMOperand.isRegister(operands.head)) unsupported("invalid F2F.F64.F32 operands")
        store(operands.head, convert(floatSource(operands(1)), NumericFormat.IEEE(32), NumericFormat.IEEE(64)), 64)
        true
      case _: SASSInstruction.Frnd if verifiedModern && modifiers.isEmpty =>
        requireOperands(2, "invalid FRND operands")
        store(
          operands.head,
          convert(
            floatSource(operands(1)),
            NumericFormat.IEEE(32),
            NumericFormat.IEEE(32),
            Some(ConversionRounding.RNI)
          )
        )
        true
      case _: SASSInstruction.I2f if modifiers == Vec(Modifier.F16) =>
        requireOperands(2, "invalid I2F.F16 operands")
        store(
          operands.head,
          convert(source(operands(1)), NumericFormat.Signed(32), NumericFormat.IEEE(16), Some(ConversionRounding.RN))
        )
        true
      case _: SASSInstruction.I2f | _: SASSInstruction.I2fp | _: SASSInstruction.Ui2f =>
        integerToFloat()
        true
      case _: SASSInstruction.F2i =>
        floatToInteger()
        true
      case _: SASSInstruction.Mufu if modifiers.exists(Set(Modifier.RCP64H, Modifier.RSQ64H)) =>
        doubleHighReciprocal()
        true
      case _: SASSInstruction.Rro =>
        emit(Move(
          reducedRadians.getOrElse(unsupported("unverified range reduction")).slot,
          floatSource(operands(1)),
          32
        ))
        true
      case _: SASSInstruction.Mufu =>
        special()
        true
      case _: SASSInstruction.Fchk =>
        divisionCheck()
        true
      case _: SASSInstruction.Fset | _: SASSInstruction.Dset =>
        set()
        true
      case _: SASSInstruction.Dfma | _: SASSInstruction.Dadd | _: SASSInstruction.Dmul =>
        requireModifiers(roundings)
        val inputs                             = operands.tail.map(doubleSource(_))
        val result                             = temp()
        val rounding                           = conversionRounding(modifiers.headOption.getOrElse(Modifier.RN))
        def fused(right: Value, addend: Value) =
          FusedMultiplyAdd(result.slot, inputs(0), right, addend, 64, rounding = rounding)
        def immediate(value: Double) = Value.Immediate(IEEE754.doubleToBinary64(value))
        // Directed sums and products round the exact result once through the fused form. A zero addend of this sign
        // cannot flip the sign of an exact zero product under the rounding.
        val zero = immediate(if (rounding == ConversionRounding.RM) 0.0 else -0.0)
        emit(operation match {
          case _: SASSInstruction.Dfma                                      => fused(inputs(1), inputs(2))
          case _: SASSInstruction.Dadd if rounding != ConversionRounding.RN => fused(immediate(1.0), inputs(1))
          case _: SASSInstruction.Dadd => FloatBinary(FloatOperation.Add, result.slot, inputs(0), inputs(1), 64)
          case _ if rounding != ConversionRounding.RN => fused(inputs(1), zero)
          case _ => FloatBinary(FloatOperation.Multiply, result.slot, inputs(0), inputs(1), 64)
        })
        store(operands.head, result, 64)
        true
      case _: SASSInstruction.Fmul
          if modifiers.exists(Set(Modifier.RZ, Modifier.RP, Modifier.RM)) && !modifiers.contains(Modifier.FTZ) =>
        requireModifiers(Set(Modifier.RZ, Modifier.RP, Modifier.RM))
        // The product of two binary32 significands is exact in binary64.
        def wide(operand: Operand) = convert(floatSource(operand), NumericFormat.IEEE(32), NumericFormat.IEEE(64))
        val product                = temp()
        emit(FloatBinary(FloatOperation.Multiply, product.slot, wide(operands(1)), wide(operands(2)), 64))
        store(
          operands.head,
          convert(product, NumericFormat.IEEE(64), NumericFormat.IEEE(32), Some(conversionRounding(modifiers(0))))
        )
        true
      case _: SASSInstruction.Ffma | _: SASSInstruction.Fadd | _: SASSInstruction.Ufadd | _: SASSInstruction.Fmul =>
        val fma = VMOperand.is[SASSInstruction.Ffma](operation)
        requireModifiers(if (fma) roundings + Modifier.FTZ else Set(Modifier.RN, Modifier.FTZ))
        val flush  = modifiers.contains(Modifier.FTZ)
        val inputs = operands.tail.map { raw =>
          val value = floatSource(raw)
          if (flush) flushFloat(value) else value
        }
        val result = temp()
        emit(operation match {
          case _ if fma =>
            FusedMultiplyAdd(
              result.slot,
              inputs(0),
              inputs(1),
              inputs(2),
              32,
              rounding = conversionRounding(modifiers.find(roundings).getOrElse(Modifier.RN))
            )
          case _: SASSInstruction.Fadd | _: SASSInstruction.Ufadd =>
            FloatBinary(FloatOperation.Add, result.slot, inputs(0), inputs(1), 32)
          case _ => FloatBinary(FloatOperation.Multiply, result.slot, inputs(0), inputs(1), 32)
        })
        store(operands.head, if (flush) flushFloat(result) else result)
        true
      case _ => false
    }
  }

  /** The legacy FSET.CC form whose observed consumer reads only the zero flag. */
  def zeroFlagFset(using context: VMCompilationContext): Boolean = {
    import context.*
    def input(value: Operand): Boolean = value match {
      case Operand.Unary(inner, false, false, true)             => VMOperand.isRegister(inner)
      case Operand.ConstantBank(bank, offset)                   => bank == 2 && VMOperand.integer(offset).exists(_ >= 0)
      case Operand.FloatingBits(_, width, _) if width.raw == 32 => true
      case other                                                => VMOperand.isRegister(other)
    }
    Set(35, 50, 60)(sm) && VMOperand.is[SASSInstruction.Fset](operation) &&
    (modifiers.filterNot(_ == Modifier.FTZ) match {
      case Vec(Modifier.GTU | Modifier.NEU, Modifier.AND) => true
      case _                                              => false
    }) && operands.size == 4 && operands.head == Operand.Qualified(VMOperand.Zero, Vec(OperandSelector.CC)) &&
    input(operands(1)) && input(operands(2)) && VMOperand.isPredicate(operands(3), allowNegated = true)
  }

  private def minMax()(using context: VMCompilationContext): Unit = {
    import context.*
    val wide = VMOperand.is[SASSInstruction.Dmnmx](operation)
    requireModifiers(if (wide) Set.empty else Set(Modifier.NAN))
    if ((!wide && sm < 70) || operands.size != 4) unsupported("unsupported floating min/max form")
    val bits: Int in (32, 64) = if (wide) 64 else 32
    val format                = NumericFormat.IEEE(bits)
    val extremum              = operands(3) match {
      case Operand.Predicate(None, false, false) => FloatOperation.Minimum
      case Operand.Predicate(None, false, true)  => FloatOperation.Maximum
      case _                                     => unsupported("FMNMX selector must be PT or !PT")
    }
    val left      = if (wide) doubleSource(operands(1)) else floatSource(operands(1))
    val right     = if (wide) doubleSource(operands(2)) else floatSource(operands(2))
    val leftNan   = compare(Comparison.Unordered, format, left, left)
    val rightNan  = compare(Comparison.Unordered, format, right, right)
    val nanResult =
      combine(
        if (modifiers.contains(Modifier.NAN)) PredicateOperation.Or else PredicateOperation.And,
        leftNan,
        rightNan
      )
    val result = temp()
    val nan    = if (wide) IEEE754.doubleToBinary64(Double.NaN) else VMArithmetic.CanonicalNaN32
    emit(FloatBinary(extremum, result.slot, left, right, bits))
    emit(Select(result.slot, left, result, rightNan.slot, bits))
    emit(Select(result.slot, right, result, leftNan.slot, bits))
    emit(Select(result.slot, Value.Immediate(nan), result, nanResult.slot, bits))
    store(operands.head, result, bits)
  }

  private def compareSelect()(using context: VMCompilationContext): Unit = {
    import context.*
    requireModifiers(comparisons + Modifier.FTZ)
    requireOperands(4, "FCMP requires a destination, two values and a condition")
    val selected = modifiers.filterNot(_ == Modifier.FTZ)
    if (selected.size > 1) unsupported("FCMP requires one comparison")
    // The machine format's Test field defaults to GT.
    val condition = floatSource(operands(3))
    val holds     = compare(
      comparisonCode(selected.headOption.getOrElse(Modifier.GT)),
      NumericFormat.IEEE(32),
      if (modifiers.contains(Modifier.FTZ)) flushFloat(condition) else condition,
      Value.Immediate(0L)
    )
    emit(Select(destination(operands.head).slot, floatSource(operands(1)), floatSource(operands(2)), holds.slot, 32))
  }

  private def integerToFloat()(using context: VMCompilationContext): Unit = {
    import context.*
    requireModifiers(roundings ++
      Set(Modifier.F32, Modifier.F64, Modifier.S32, Modifier.U32, Modifier.U64, Modifier.S64))
    requireOperands(2, "invalid scalar integer-to-float operands")
    if (
      VMOperand.is[SASSInstruction.I2fp](operation) &&
      (!modifiers.contains(Modifier.F32) || modifiers.exists(Set(Modifier.F64, Modifier.U64, Modifier.S64)))
    ) unsupported("only scalar 32-bit I2FP conversion is implemented")
    val rounding             = modifiers.find(roundings).getOrElse(Modifier.RN)
    val kind                 = modifiers.find(Set(Modifier.U32, Modifier.U64, Modifier.S64)).getOrElse(Modifier.S32)
    val bits: ScalarBitWidth = if (modifiers.contains(Modifier.F64)) 64 else 32
    val inputWidth: ScalarBitWidth = if (Set(Modifier.U64, Modifier.S64)(kind)) 64 else 32
    val (input, inputType)         = operands(1) match {
      case Operand.Unary(inner, false, false, true) if Set(Modifier.S32, Modifier.S64)(kind) =>
        val extended = convert(source(inner, inputWidth), integerConversion(kind), NumericFormat.Signed(64))
        (unary(IntegerUnaryOperation.Absolute, extended, 64), NumericFormat.Unsigned(64))
      case operand => (source(operand, inputWidth), integerConversion(kind))
    }
    store(
      operands.head,
      convert(input, inputType, NumericFormat.IEEE(if (bits.raw == 64) 64 else 32), Some(conversionRounding(rounding))),
      bits
    )
  }

  private def floatToInteger()(using context: VMCompilationContext): Unit = {
    import context.*
    requireModifiers(Set(
      Modifier.FTZ,
      Modifier.F32,
      Modifier.F64,
      Modifier.S32,
      Modifier.U32,
      Modifier.S64,
      Modifier.U64,
      Modifier.TRUNC,
      Modifier.FLOOR,
      Modifier.CEIL,
      Modifier.ROUND,
      Modifier.NTZ
    ))
    val kind     = modifiers.find(Set(Modifier.S32, Modifier.U32, Modifier.S64, Modifier.U64)).getOrElse(Modifier.S32)
    val rounding =
      if (modifiers.contains(Modifier.TRUNC)) ConversionRounding.RZI
      else if (modifiers.contains(Modifier.FLOOR)) ConversionRounding.RMI
      else if (modifiers.contains(Modifier.CEIL)) ConversionRounding.RPI
      else ConversionRounding.RNI
    val double = modifiers.contains(Modifier.F64)
    if (double && modifiers.contains(Modifier.FTZ)) unsupported("F2I.F64.FTZ")
    val input =
      if (double) doubleSource(operands(1))
      else if (modifiers.contains(Modifier.FTZ)) flushFloat(floatSource(operands(1)))
      else floatSource(operands(1))
    store(
      operands.head,
      convert(input, NumericFormat.IEEE(if (double) 64 else 32), integerConversion(kind), Some(rounding)),
      if (Set(Modifier.U64, Modifier.S64)(kind)) 64 else 32
    )
  }

  private def doubleHighReciprocal()(using context: VMCompilationContext): Unit = {
    import context.*
    requireModifiers(Set(Modifier.RCP64H, Modifier.RSQ64H))
    def flushHigh(value: Value): Value = {
      val exponent = binary(IntegerOperation.BitwiseAnd, value, Value.Immediate(0x7ff00000L))
      val sign     = binary(IntegerOperation.BitwiseAnd, value, Value.Immediate(0x80000000L))
      select(sign, value, compare(Comparison.Equal, NumericFormat.Unsigned(32), exponent, Value.Immediate(0L)))
    }
    def high(value: Value): Value = narrow(binary(IntegerOperation.ShiftRightLogical, value, Value.Immediate(32L), 64))
    val raw                       = VMOperand.without(operands(1), OperandSelector.`reuse`)
    val upper                     =
      if (!VMOperand.numericFloating(raw)) floatSource(raw)
      else
        doubleSource(raw) match {
          case Value.Immediate(bits) => Value.Immediate(bits >>> 32)
          case value                 => high(value)
        }
    val input  = temp()
    val result = temp()
    emit(MoveCombine(input.slot, Vec(Value.Immediate(0L), flushHigh(upper)), 32))
    emit(FloatUnary(
      if (modifiers.contains(Modifier.RCP64H)) FloatUnaryOperation.Reciprocal
      else FloatUnaryOperation.ReciprocalSquareRoot,
      result.slot,
      input,
      64
    ))
    store(operands.head, flushHigh(high(result)))
  }

  private def special()(using context: VMCompilationContext): Unit = {
    import context.*
    requireModifiers(Set(
      Modifier.RCP,
      Modifier.RSQ,
      Modifier.SQRT,
      Modifier.EX2,
      Modifier.LG2,
      Modifier.SIN,
      Modifier.COS,
      Modifier.TANH
    ))
    val input    = flushFloat(reducedRadians.getOrElse(floatSource(operands(1))))
    val argument =
      if (reducedRadians.nonEmpty || !Set(Modifier.SIN, Modifier.COS)(modifiers(0))) input
      else {
        if (legacy) unsupported("legacy MUFU trigonometric range-reduction encoding")
        // Volta and later MUFU trigonometric inputs are cycles, whereas PTX uses radians. Reduce before scaling so
        // large exactly integral binary32 inputs remain periodic.
        val integral = convert(input, NumericFormat.IEEE(32), NumericFormat.IEEE(32), Some(ConversionRounding.RNI))
        val fraction = temp()
        val radians  = temp()
        emit(FloatBinary(FloatOperation.Subtract, fraction.slot, input, integral, 32))
        emit(FloatBinary(FloatOperation.Multiply, radians.slot, fraction, Value.Immediate(0x40c90fdbL), 32))
        emit(Select(
          radians.slot,
          input,
          radians,
          compare(Comparison.Equal, NumericFormat.IEEE(32), input, Value.Immediate(0L)).slot,
          32
        ))
        radians
      }
    val result = temp()
    emit(FloatUnary(floatUnaryOperation(modifiers(0)), result.slot, argument, 32))
    store(operands.head, flushFloat(result))
  }

  private def divisionCheck()(using context: VMCompilationContext): Unit = {
    import context.*
    val legacyDivide = Set(35, 50, 60)(sm)
    requireModifiers(if (legacyDivide) Set(Modifier.DIVIDE) else Set.empty)
    if (legacyDivide && modifiers != Vec(Modifier.DIVIDE))
      unsupported("legacy FCHK requires the explicit division mode")
    if (!Set(35, 50, 60, 70, 75, 80, 86, 89, 90, 100, 103, 120)(sm))
      unsupported("FCHK range check is not supported for this architecture")
    requireOperands(3, "invalid FCHK operands")
    if (
      Set(70, 75)(sm) &&
      (instruction.guard.nonEmpty || !VMOperand.isPredicate(operands.head) ||
        !operands.tail.forall(VMOperand.isRegister(_)))
    ) unsupported("unsupported sm_70/sm_75 FCHK operand or predicate form")
    // The exponent boundaries were measured on sm_89. Earlier DIVIDE forms use the same documented operation;
    // retaining these boundaries across generations is inferred. The predicate requests the slow path; signs and
    // significands do not participate.
    def exponent(operand: Operand): Value.Register = {
      val result = temp()
      emit(BitFieldExtract(result.slot, floatSource(operand), Value.Immediate(23L), Value.Immediate(8L), 32, false))
      result
    }
    val numerator   = exponent(operands(1))
    val denominator = exponent(operands(2))
    val difference  = binary(IntegerOperation.Subtract, numerator, denominator)
    val slowPath    = Vec(
      (Comparison.LessThan, numerator, 25L),
      (Comparison.GreaterThan, numerator, 254L),
      (Comparison.LessThan, denominator, 2L),
      (Comparison.GreaterThan, denominator, 251L),
      (Comparison.LessThan, difference, -124L),
      (Comparison.GreaterThan, difference, 126L)
    ).map((comparison, value, boundary) =>
      compare(comparison, NumericFormat.Signed(32), value, Value.Immediate(boundary))
    ).reduceLeft { (left, right) =>
      emit(PredicateBinary(PredicateOperation.Or, left.slot, left.slot, right.slot))
      left
    }
    writePredicate(operands.head, slowPath)
  }

  private def set()(using context: VMCompilationContext): Unit = {
    import context.*
    requireOperands(4, s"${operation.mnemonic.raw} requires destination, two sources and a predicate")
    val double   = VMOperand.is[SASSInstruction.Dset](operation)
    val zeroFlag = !double && zeroFlagFset(using context)
    if (VMOperand.has(operands.head, OperandSelector.CC) && !zeroFlag)
      unsupported(s"${operation.mnemonic.raw} condition-code output is not implemented")
    if (double && modifiers.contains(Modifier.FTZ)) unsupported("DSET does not support FTZ")
    requireModifiers(comparisons ++ Set(Modifier.BF, Modifier.AND, Modifier.OR, Modifier.XOR, Modifier.FTZ))
    def input(operand: Operand): Value = {
      val value = if (double) doubleSource(operand) else floatSource(operand)
      if (modifiers.contains(Modifier.FTZ)) flushFloat(value) else value
    }
    val left   = input(operands(1))
    val right  = input(operands(2))
    val format = NumericFormat.IEEE(if (double) 64 else 32)
    val result = compare(comparisonCode(modifiers.find(comparisons).get), format, left, right)
    emit(PredicateBinary(
      predicateOperation(modifiers.find(Set(Modifier.AND, Modifier.OR, Modifier.XOR)).get),
      result.slot,
      result.slot,
      predicateSource(operands(3)).slot
    ))
    // The observed consumer reads the zero flag, clear when the masked comparison is true.
    if (zeroFlag) emit(PredicateUnary(VMRegisters.Predicate.Zero.slot, result.slot))
    else {
      val truth = if (modifiers.contains(Modifier.BF)) 0x3f800000L else 0xffffffffL
      store(operands.head, select(Value.Immediate(truth), Value.Immediate(0L), result))
    }
  }

  private def floatPair(kind: FloatOperation)(using context: VMCompilationContext): Unit = {
    import context.*
    if (!Set(100, 103)(sm)) unsupported("packed float32 requires sm_100 or sm_103")
    if (!modifiers.forall(Set(Modifier.RN, Modifier.FTZ)))
      unsupported("packed float32 supports RN rounding and optional FTZ")
    requireOperands(3, "packed float32 requires a register pair and two sources")
    val destination = operands.head match {
      case value @ Operand.Register(RegisterBank.R, _) => value
      case _ => unsupported("packed float32 destination must be an aligned register pair or RZ")
    }
    validateFloatPairRegister(destination, 2)
    val left   = floatPairOperand(operands(1))
    val right  = floatPairOperand(operands(2))
    val result = temp()
    emit(FloatBinaryPacked(
      kind,
      result.slot,
      left,
      right,
      PackedFloatFormat.F32x2,
      FloatControl(flushSubnormal = modifiers.contains(Modifier.FTZ))
    ))
    store(destination, result, 64)
  }

  private def floatPairOperand(operand: Operand)(using context: VMCompilationContext): Value = {
    import context.*
    operand match {
      case Operand.Unary(inner, negative, false, absolute) =>
        val signs  = 0x8000000080000000L
        val mask   = Value.Immediate(if (absolute) ~signs else -1L)
        val result = binary(IntegerOperation.BitwiseAnd, floatPairOperand(inner), mask, 64)
        if (negative) emit(IntegerBinary(IntegerOperation.BitwiseXor, result.slot, result, Value.Immediate(signs), 64))
        result
      case _: Operand.Unary => unsupported("packed float32 does not support bitwise source inversion")
      case _                =>
        val selected                                 = VMOperand.qualifiers(operand)
        val base                                     = VMOperand.base(VMOperand.without(operand, OperandSelector.reuse))
        val scalar                                   = selected == Vec(OperandSelector.F32)
        def duplicated(value: Value): Value.Register = {
          val result = temp()
          emit(MoveCombine(result.slot, Vec(value, value), 32))
          result
        }
        base match {
          case register @ Operand.Register(RegisterBank.R | RegisterBank.UR, _) =>
            if (
              !scalar && selected != Vec(OperandSelector.F32x2) &&
              selected != Vec(OperandSelector.F32x2, OperandSelector.LO_HI)
            ) unsupported("packed float32 source requires F32, F32x2 or F32x2.LO_HI")
            validateFloatPairRegister(register, if (scalar) 1 else 2)
            val value = source(base, if (scalar) 32 else 64)
            if (scalar) duplicated(value)
            else if (selected.contains(OperandSelector.LO_HI)) {
              val words  = Vec(temp(), temp())
              val result = temp()
              emit(MoveSplit(words.map(word => Some(word.slot)), value, 32))
              emit(MoveCombine(result.slot, words.reverse, 32))
              result
            } else value
          case _ if selected.isEmpty && !VMOperand.hasUnknownDomain(operand) => duplicated(floatSource(operand))
          case _ => unsupported("unsupported packed float32 source")
        }
    }
  }

  private def validateFloatPairRegister(target: Operand.Register, words: Int in (1, 2))(using
      context: VMCompilationContext
  ): Unit = {
    import context.*
    val parameter = target.bank match {
      case RegisterBank.R  => "MAX_REG_COUNT"
      case RegisterBank.UR => "MAX_UNIFORM_REG_COUNT"
      case _               => unsupported("packed float32 requires general or uniform registers")
    }
    val capacity = Target.of(module.sm).toOption.flatMap(_.database.parameters.get(parameter))
      .getOrElse(unsupported(s"missing packed float32 register limit $parameter"))
    if (
      !VMOperand.isZero(target) &&
      (BigInt(target.index.raw) + words.raw > capacity || (words.raw == 2 && target.index.raw % 2 != 0))
    ) unsupported("packed float32 source or destination exceeds its bank or is not an aligned register pair")
  }

  private def packedBinary(kind: FloatOperation, opcode: String)(using context: VMCompilationContext): Unit = {
    import context.*
    requireOperands(3, s"$opcode requires destination and two sources")
    val result = temp()
    emit(FloatBinaryPacked(
      kind,
      result.slot,
      packed(operands(1), opcode),
      packed(operands(2), opcode),
      PackedFloatFormat.F16x2
    ))
    store(operands.head, result)
  }

  private def packedFma()(using context: VMCompilationContext): Unit = {
    import context.*
    val mmaUnitScale = modifiers == Vec(Modifier.MMA) && operands.size == 5 &&
      bareRegister(operands(1), allowZero = true) &&
      bareRegister(operands(4), allowZero = true) && unitOne(operands(2)) && unitOne(operands(3))
    if (mmaUnitScale) {
      val sum = temp()
      emit(FloatBinaryPacked(
        FloatOperation.Add,
        sum.slot,
        packed(operands(1), "HFMA2.MMA"),
        packed(operands(4), "HFMA2.MMA"),
        PackedFloatFormat.F16x2
      ))
      store(operands.head, sum)
    } else if (
      (modifiers.isEmpty || modifiers == Vec(Modifier.MMA)) && operands.size == 5 && negativeZero(operands(1)) &&
      VMOperand.isZero(operands(2))
    ) store(operands.head, Value.Immediate((halfConstant(operands(3)) << 16) | halfConstant(operands(4))))
    else {
      requireOperands(4, "HFMA2 requires destination and three sources")
      val mmaAccumulator = modifiers == Vec(Modifier.MMA) && bareRegister(operands(1), allowZero = true) &&
        VMOperand.isZero(operands(2)) && operands.head == operands.last
      if (modifiers.nonEmpty && !mmaAccumulator) unsupported(s"unsupported HFMA2 modifiers ${modifiers.mkString(".")}")
      val product = temp()
      emit(FusedMultiplyAddPacked(
        product.slot,
        packed(operands(1), "HFMA2"),
        packed(operands(2), "HFMA2"),
        packed(operands(3), "HFMA2"),
        false,
        false
      ))
      store(operands.head, product)
    }
  }

  private def packedCompare()(using context: VMCompilationContext): Unit = {
    import context.*
    if (modifiers.size != 2 || !Set(Modifier.GEU, Modifier.NE)(modifiers.head) || modifiers(1) != Modifier.AND)
      unsupported(s"unsupported HSETP2 modifiers ${modifiers.mkString(".")}")
    requireOperands(5, "HSETP2 requires two predicate results, two sources and PT")
    if (operands(4) != VMOperand.True) unsupported("HSETP2 requires a PT predicate input")
    if (!supportedHalfSource(operands(2)) || !supportedHalfSource(operands(3)))
      unsupported("HSETP2 supports only register or duplicated H0_H0/H1_H1 sources")
    val first  = predicateDestination(operands.head)
    val second = predicateDestination(operands(1))
    if (second.nonEmpty && modifiers.head != Modifier.GEU) unsupported("HSETP2 dual results are verified only for GEU")
    if (second.nonEmpty && first == second) unsupported("HSETP2 dual predicate results must be distinct")
    val comparison = if (modifiers.head == Modifier.NE) Comparison.NotEqual else Comparison.GreaterThanOrEqualUnordered
    def half(offset: Int): Value.Predicate = compare(
      comparison,
      NumericFormat.IEEE(32),
      halfAsFloat(packed(operands(2), "HSETP2"), offset),
      halfAsFloat(packed(operands(3), "HSETP2"), offset)
    )
    val low = half(0)
    first.foreach(copyPredicate(_, low))
    second.foreach(copyPredicate(_, half(16)))
  }

  private def packedMinMax()(using context: VMCompilationContext): Unit = {
    import context.*
    if (modifiers.nonEmpty) unsupported(s"unsupported HMNMX2 modifiers ${modifiers.mkString(".")}")
    requireOperands(4, "HMNMX2 requires destination, two sources, and a predicate")
    val minimum = operands(3) match {
      case Operand.Predicate(None, false, false) => true
      case Operand.Predicate(None, false, true)  => false
      case _                                     => unsupported("HMNMX2 supports only PT and !PT selectors")
    }
    if (operands.slice(1, 3).exists(VMOperand.has(_, OperandSelector.H1_H1)))
      unsupported("HMNMX2 supports only raw packed registers and verified H0_H0 selectors")
    val result = temp()
    emit(FloatBinaryPacked(
      if (minimum) FloatOperation.MinimumNumber else FloatOperation.MaximumNumber,
      result.slot,
      packed(operands(1), "HMNMX2"),
      packed(operands(2), "HMNMX2"),
      PackedFloatFormat.F16x2
    ))
    store(operands.head, result)
  }

  private def toHalf(value: Value)(using context: VMCompilationContext): Value.Register =
    context.convert(value, NumericFormat.IEEE(32), NumericFormat.IEEE(16), Some(ConversionRounding.RN))

  /** `high` in the upper and `low` in the lower half of one register. */
  private def halves(high: Value, low: Value)(using context: VMCompilationContext): Value.Register =
    context.binary(
      IntegerOperation.BitwiseOr,
      context.binary(IntegerOperation.ShiftLeft, high, Value.Immediate(16L)),
      low
    )

  private def scalarToHalf()(using context: VMCompilationContext): Unit = {
    import context.*
    if (sm != 75) unsupported("HADD2 scalar-float conversion is supported only on sm_75")
    if (instruction.guard.nonEmpty)
      unsupported("HADD2 scalar-float conversion is supported only as an unpredicated sm_75 form")
    requireOperands(3, "HADD2 scalar-float form requires destination and two sources")
    if (!bareRegister(operands.head)) unsupported("HADD2 scalar-float destination must be a register")
    if (VMOperand.qualifiers(operands(1)) != Vec(OperandSelector.F32) || !VMOperand.isRegister(operands(1)))
      unsupported("HADD2 scalar-float source must be an unmodified .F32 register")
    if (!negativeZeroHalf(operands(2)))
      unsupported("HADD2 scalar-float form requires the compiler-emitted -RZ.H0_H0 zero addend")
    val converted = toHalf(floatSource(registerBase(operands(1), "HADD2 scalar-float source")))
    store(operands.head, halves(converted, converted))
  }

  private def halfToScalar()(using context: VMCompilationContext): Unit = {
    import context.*
    requireOperands(3, "HADD2.F32 requires destination and two sources")
    val selected =
      if (negativeZero(operands(1)) || negativeZeroHalf(operands(1))) operands(2)
      else if (negativeZero(operands(2)) || negativeZeroHalf(operands(2))) operands(1)
      else unsupported("HADD2.F32 supported forms require the compiler-emitted -RZ or -RZ.H0_H0 addend")
    store(operands.head, selectedHalfAsFloat(selected, "HADD2.F32"))
  }

  private def halfMultiplyToScalar()(using context: VMCompilationContext): Unit = {
    import context.*
    if (sm != 75) unsupported("HMUL2.F32 is supported only on sm_75")
    requireOperands(3, "HMUL2.F32 requires destination and two sources")
    if (instruction.guard.nonEmpty || !bareRegister(operands.head))
      unsupported("HMUL2.F32 supports only unpredicated sm_75 register destinations")
    val product = temp()
    emit(FloatBinary(
      FloatOperation.Multiply,
      product.slot,
      selectedHalfAsFloat(operands(1), "HMUL2.F32"),
      selectedHalfAsFloat(operands(2), "HMUL2.F32"),
      32
    ))
    store(operands.head, product)
  }

  private def floatToHalf()(using context: VMCompilationContext): Unit = {
    import context.*
    if (!Set(70, 75, 89)(sm)) unsupported("F2F.F16.F32 is not supported on this target")
    requireOperands(2, "F2F.F16.F32 requires destination and source")
    if (!bareRegister(operands.head) || !bareRegister(operands(1)))
      unsupported("F2F.F16.F32 requires bare register operands")
    store(operands.head, toHalf(floatSource(operands(1))))
  }

  private def packHalves()(using context: VMCompilationContext): Unit = {
    import context.*
    if (modifiers != Vec(Modifier.F16, Modifier.F32, Modifier.PACK_AB) && modifiers != Vec(Modifier.PACK_AB))
      unsupported(s"unsupported F2FP modifiers ${modifiers.mkString(".")}")
    requireOperands(3, "F2FP.PACK_AB requires destination and two sources")
    val low  = toHalf(floatSource(operands(2)))
    val high = toHalf(floatSource(operands(1)))
    store(operands.head, halves(high, low))
  }

  private def extractHalf(value: Value, shift: Int)(using context: VMCompilationContext): Value.Register = {
    val result = context.temp()
    context.emit(BitFieldExtract(result.slot, value, Value.Immediate(shift.toLong), Value.Immediate(16L), 32, false))
    result
  }

  private def packed(operand: Operand, opcode: String)(using context: VMCompilationContext): Value = {
    import context.*
    if (negativeZeroHalf(operand) && VMOperand.is[SASSInstruction.Hadd2](operation)) {
      val negativeZero = temp()
      emit(Move(negativeZero.slot, Value.Immediate(0x80008000L), 32))
      negativeZero
    } else if (VMOperand.qualifiers(operand).isEmpty) {
      if (!VMOperand.isRegister(operand, allowZero = true)) unsupported(s"unsupported $opcode half source")
      source(operand)
    } else {
      val (base, shift) = selectedHalf(operand, opcode)
      val input         = source(base)
      halves(extractHalf(input, shift), extractHalf(input, shift))
    }
  }

  private def halfAsFloat(packed: Value, offset: Int)(using context: VMCompilationContext): Value.Register =
    context.convert(extractHalf(packed, offset), NumericFormat.IEEE(16), NumericFormat.IEEE(32))

  private def selectedHalfAsFloat(operand: Operand, opcode: String)(using
      context: VMCompilationContext
  ): Value.Register = {
    val (base, shift) = selectedHalf(operand, opcode)
    halfAsFloat(context.source(base), shift)
  }

  private def selectedHalf(operand: Operand, opcode: String)(using context: VMCompilationContext): (Operand, Int) = {
    val shift = VMOperand.qualifiers(operand) match {
      case Vec(OperandSelector.H0_H0) | Vec(OperandSelector.H0_H0, OperandSelector.H0_H0) => 0
      case Vec(OperandSelector.H1_H1) | Vec(OperandSelector.H1_H1, OperandSelector.H1_H1) => 16
      case _ => context.unsupported(s"$opcode supports only duplicated H0_H0 or H1_H1 selectors")
    }
    context.registerBase(operand, s"$opcode selected-half source", allowZero = true) -> shift
  }

  private def halfConstant(operand: Operand)(using context: VMCompilationContext): Long = {
    val bits = operand match {
      case Operand.Integer(value) => IEEE754.floatToBinary16(value.toFloat).toLong
      case Operand.Floating(value, _, negativeZero) if value == BigDecimal(0) && negativeZero => 0x8000L
      case Operand.Floating(value, _, _)                      => IEEE754.floatToBinary16(value.toFloat).toLong
      case Operand.FloatingSymbol(FloatingConstant.INF)       => 0x7c00L
      case Operand.FloatingSymbol(constant) if constant.isNaN => VMArithmetic.CanonicalNaN16
      case Operand.FloatingBits(_, width, encoded)  => IEEE754.fromDouble(IEEE754.toDouble(encoded.toLong, width), 16)
      case Operand.Unary(inner, true, false, false) => halfConstant(inner) ^ 0x8000L
      case _ => context.unsupported("HFMA2.MMA constant form requires floating constants")
    }
    if (IEEE754.isNaN(bits, 16)) VMArithmetic.CanonicalNaN16 else bits
  }

  @annotation.tailrec
  private def unitOne(operand: Operand): Boolean = operand match {
    case Operand.Integer(value)                                  => value == 1
    case Operand.Floating(value, _, _)                           => value == BigDecimal(1)
    case Operand.FloatingBits(_, width, bits) if width.raw == 16 => bits == 0x3c00
    case Operand.FloatingBits(_, width, bits) if width.raw == 32 => bits == 0x3f800000
    case Operand.FloatingBits(_, width, bits) if width.raw == 64 => bits == BigInt("3ff0000000000000", 16)
    case Operand.Unary(inner, false, false, false)               => unitOne(inner)
    case _                                                       => false
  }

  private def predicateDestination(operand: Operand)(using context: VMCompilationContext): Option[Value.Predicate] =
    operand match {
      case Operand.Predicate(None, false, false)        => None
      case Operand.Predicate(Some(index), false, false) =>
        Some(Value.Predicate(VMRegisters.predicate(index = index, uniform = false)))
      case _ => context.unsupported("HSETP2 result must be a predicate register or PT")
    }

  private def supportedHalfSource(operand: Operand): Boolean =
    VMOperand.isRegister(operand, allowZero = true) ||
      (VMOperand.qualifiers(operand) match {
        case Vec(OperandSelector.H0_H0) | Vec(OperandSelector.H1_H1) =>
          VMOperand.isRegister(VMOperand.base(VMOperand.without(operand, OperandSelector.reuse)), allowZero = true)
        case _ => false
      })

  private def negativeZeroHalf(operand: Operand): Boolean = operand match {
    case Operand.Unary(inner, true, false, false) => VMOperand.isZero(inner) &&
      VMOperand.qualifiers(inner) == Vec(OperandSelector.H0_H0)
    case _ => false
  }

  private def negativeZero(operand: Operand): Boolean = operand match {
    case Operand.Unary(inner, true, false, false) => VMOperand.isZero(inner) && VMOperand.qualifiers(inner).isEmpty
    case _                                        => false
  }
}
