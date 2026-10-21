package net.kurobako.gemstone.isa.sass

import ModifierLowering.*
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
  private val roundings               = Set(Modifier.RN, Modifier.RM, Modifier.RP, Modifier.RZ)
  private val narrowIntegers          = Set(Modifier.S8, Modifier.U8, Modifier.S16, Modifier.U16)
  private val integerToFloatModifiers = roundings ++ narrowIntegers ++
    Set(
      Modifier.F16,
      Modifier.BF16,
      Modifier.F32,
      Modifier.F64,
      Modifier.S32,
      Modifier.U32,
      Modifier.U64,
      Modifier.S64
    )
  private val integerSourceKinds = narrowIntegers ++ Set(Modifier.U32, Modifier.U64, Modifier.S64)
  private val integerKinds       =
    Set(Modifier.S8, Modifier.U8, Modifier.S16, Modifier.U16, Modifier.S32, Modifier.U32, Modifier.S64, Modifier.U64)
  private val floatToIntegerModifiers = integerKinds ++ Set(
    Modifier.FTZ,
    Modifier.F16,
    Modifier.BF16,
    Modifier.F32,
    Modifier.F64,
    Modifier.TRUNC,
    Modifier.FLOOR,
    Modifier.CEIL,
    Modifier.ROUND,
    Modifier.NTZ
  )
  private val packedByteModifiers = Set(
    Modifier.U8,
    Modifier.S8,
    Modifier.F32,
    Modifier.ROUND,
    Modifier.TRUNC,
    Modifier.NTZ,
    Modifier.RELU
  )
  private val mufuFunctions = Set(
    Modifier.RCP,
    Modifier.RSQ,
    Modifier.SQRT,
    Modifier.EX2,
    Modifier.LG2,
    Modifier.SIN,
    Modifier.COS,
    Modifier.TANH
  )
  private val mufuModifiers = mufuFunctions ++
    Set(Modifier.FTZ, Modifier.F16, Modifier.BF16, Modifier.F16x2, Modifier.BF16x2)
  private val setRelations = comparisons ++ Set(Modifier.NUM, Modifier.NAN)
  private val setCombiners = Set(Modifier.AND, Modifier.OR, Modifier.XOR)
  private val setModifiers = setRelations ++ setCombiners ++
    Set(Modifier.F, Modifier.T, Modifier.BF, Modifier.BM, Modifier.FTZ)
  private val floatFormats        = Set(Modifier.F16, Modifier.BF16, Modifier.E8M7, Modifier.F32, Modifier.F64)
  private val packHalvesModifiers = Set(
    Modifier.F16,
    Modifier.BF16,
    Modifier.E8M7,
    Modifier.F32,
    Modifier.PACK_AB,
    Modifier.MERGE_C,
    Modifier.RZ,
    Modifier.RS,
    Modifier.RELU,
    Modifier.SATFINITE
  )

  def lower(using context: VMCompilationContext): Boolean = VMHalfCompiler.lower || {
    import context.*
    (operation.form: @unchecked) match {
      case _: Form.Fadd2 =>
        floatPair(FloatOperation.Add)
        true
      case _: Form.Fmul2 =>
        floatPair(FloatOperation.Multiply)
        true
      case _: Form.Ffma2 =>
        floatPairFusedMultiplyAdd()
        true
      case _: Form.F2f | _: Form.Uf2f =>
        floatToFloat()
        true
      case _: Form.F2fp | _: Form.Uf2fp =>
        packHalves()
        true
      case _: Form.Fmnmx | _: Form.Dmnmx | _: Form.Ufmnmx =>
        minMax()
        true
      case _: Form.Fmnmx3 =>
        minMax3()
        true
      case _: Form.Fcmp =>
        compareSelect()
        true
      case _: Form.Frnd | _: Form.Ufrnd =>
        roundIntegral()
        true
      case _: Form.I2f if modifiers == Vec(Modifier.F16) =>
        requireOperands(2, "invalid I2F.F16 operands")
        store(
          operands.head,
          convert(source(operands(1)), NumericFormat.Signed(32), NumericFormat.IEEE(16), Some(ConversionRounding.RN))
        )
        true
      case _: Form.I2f | _: Form.I2fp | _: Form.Ui2f | _: Form.Ui2fp =>
        integerToFloat()
        true
      case _: Form.F2i | _: Form.Uf2i =>
        floatToInteger()
        true
      case _: Form.F2ip | _: Form.Uf2ip =>
        floatToPackedBytes()
        true
      case _: Form.Mufu
          if modifiers.exists(PartialFunction.cond(_) { case Modifier.RCP64H | Modifier.RSQ64H => true }) =>
        doubleHighReciprocal()
        true
      case _: Form.Rro =>
        emit(Move(
          reducedRadians.getOrElse(unsupported("unverified range reduction")).slot,
          floatSource(operands(1)),
          32
        ))
        true
      case _: Form.Mufu =>
        special()
        true
      case _: Form.Fchk =>
        divisionCheck()
        true
      case _: Form.Fset | _: Form.Dset | _: Form.Ufset =>
        set()
        true
      case _: Form.Dfma | _: Form.Dadd | _: Form.Dmul =>
        requireModifiers(roundings)
        val inputs                             = operands.tail.map(doubleSource(_))
        val result                             = temp()
        val rounding                           = modifiers.headOption.getOrElse(Modifier.RN).conversionRounding
        def fused(right: Value, addend: Value) =
          FusedMultiplyAdd(result.slot, inputs(0), right, addend, 64, rounding = rounding)
        def immediate(value: Double) = Value.Immediate(IEEE754.doubleToBinary64(value))
        // Directed sums and products round the exact result once through the fused form. A zero addend of this sign
        // cannot flip the sign of an exact zero product under the rounding.
        val zero = immediate(if (rounding == ConversionRounding.RM) 0.0 else -0.0)
        emit(operation.form match {
          case _: Form.Dfma                                      => fused(inputs(1), inputs(2))
          case _: Form.Dadd if rounding != ConversionRounding.RN => fused(immediate(1.0), inputs(1))
          case _: Form.Dadd => FloatBinary(FloatOperation.Add, result.slot, inputs(0), inputs(1), 64)
          case _ if rounding != ConversionRounding.RN => fused(inputs(1), zero)
          case _ => FloatBinary(FloatOperation.Multiply, result.slot, inputs(0), inputs(1), 64)
        })
        store(operands.head, result, 64)
        true
      case _: Form.Ffma | _: Form.Uffma | _: Form.Ffma32i | _: Form.Fadd |
          _: Form.Ufadd | _: Form.Fadd32i | _: Form.Fmul | _: Form.Ufmul |
          _: Form.Fmul32i =>
        arithmetic()
        true
      case _ => false
    }
  }

  private val scales = Map(
    Modifier.D8 -> 0.125,
    Modifier.D4 -> 0.25,
    Modifier.D2 -> 0.5,
    Modifier.M2 -> 2.0,
    Modifier.M4 -> 4.0,
    Modifier.M8 -> 8.0
  )

  /** Binary32 sums, products and fused multiply-adds, each rounded once as its modifier says, the product scaled by a
    * power of two before that rounding, saturated to [0, 1] under `SAT` and with subnormal inputs and results flushed
    * under `FTZ`. The legacy two-source `FFMA32I` accumulates into its destination.
    */
  private def arithmetic()(using context: VMCompilationContext): Unit = {
    import context.*
    val fma = operation.form match {
      case _: Form.Ffma | _: Form.Uffma | _: Form.Ffma32i => true
      case _                                              => false
    }
    val multiply = operation.form match {
      case _: Form.Fmul | _: Form.Ufmul | _: Form.Fmul32i => true
      case _                                              => false
    }
    requireModifiers(roundings ++ Set(Modifier.FTZ, Modifier.SAT) ++ (if (multiply) scales.keySet else Set.empty))
    val flush    = modifiers.contains(Modifier.FTZ)
    val control  = FloatControl(saturate = modifiers.contains(Modifier.SAT))
    val rounding = modifiers.find(roundings).getOrElse(Modifier.RN).conversionRounding
    val scale    = modifiers.flatMap(scales.get).headOption
    val sources  = if (fma && operands.size == 3) operands.tail :+ operands.head else operands.tail
    if (sources.size != (if (fma) 3 else 2)) unsupported(s"invalid ${operation.mnemonic.raw} operands")
    val inputs = sources.map { raw =>
      val value = floatSource(raw)
      if (flush) flushFloat(value) else value
    }
    val result = temp()
    if (fma) emit(FusedMultiplyAdd(result.slot, inputs(0), inputs(1), inputs(2), 32, control, rounding))
    else if (!multiply && rounding == ConversionRounding.RN)
      emit(FloatBinary(FloatOperation.Add, result.slot, inputs(0), inputs(1), 32, control))
    // A directed sum rounds the exact sum once through the fused form.
    else if (!multiply)
      emit(FusedMultiplyAdd(result.slot, inputs(0), Value.Immediate(0x3f800000L), inputs(1), 32, control, rounding))
    else if (rounding == ConversionRounding.RN && scale.isEmpty)
      emit(FloatBinary(FloatOperation.Multiply, result.slot, inputs(0), inputs(1), 32, control))
    else {
      // The product of two binary32 significands, and its scaling by a power of two, are exact in binary64.
      def wide(value: Value) = convert(value, NumericFormat.IEEE(32), NumericFormat.IEEE(64))
      val product            = temp()
      emit(FloatBinary(FloatOperation.Multiply, product.slot, wide(inputs(0)), wide(inputs(1)), 64))
      scale.foreach(factor =>
        emit(FloatBinary(
          FloatOperation.Multiply,
          product.slot,
          product,
          Value.Immediate(IEEE754.doubleToBinary64(factor)),
          64
        ))
      )
      emit(Convert(
        result.slot,
        product,
        NumericFormat.IEEE(64),
        NumericFormat.IEEE(32),
        Some(rounding),
        Option.when(control.saturate)(ConversionClamp.Saturate)
      ))
      // narrowing keeps a binary64 NaN's sign and payload, but the product is the canonical NaN
      emit(Select(
        result.slot,
        Value.Immediate(VMArithmetic.CanonicalNaN32),
        result,
        compare(Comparison.Unordered, NumericFormat.IEEE(32), result, result).slot,
        32
      ))
    }
    store(operands.head, if (flush) flushFloat(result) else result)
  }

  /** The legacy FSET.CC form whose observed consumer reads only the zero flag. */
  def zeroFlagFset(using context: VMCompilationContext): Boolean = {
    import context.*
    def input(value: Operand): Boolean = value match {
      case Operand.Unary(inner, false, false, true) => OperandQueries.isRegister(inner, allowZero = true)
      case Operand.ConstantBank(bank, offset)       => bank == 2 && OperandQueries.integer(offset).exists(_ >= 0)
      case Operand.FloatingBits(_, width, _) if width == 32 => true
      case other                                            => OperandQueries.isRegister(other, allowZero = true)
    }
    Set(35, 50, 60, 61)(sm) && OperandQueries.is[Form.Fset](operation) &&
    (modifiers.filterNot(_ == Modifier.FTZ) match {
      case Vec(Modifier.GTU | Modifier.NEU | Modifier.EQ, Modifier.AND) => true
      case _                                                            => false
    }) && operands.size == 4 && operands.head == Operand.Qualified(OperandQueries.Zero, Vec(OperandSelector.CC)) &&
    input(operands(1)) && input(operands(2)) && OperandQueries.isPredicateAllowingNegated(operands(3))
  }

  private def minMax()(using context: VMCompilationContext): Unit = {
    import context.*
    val wide = OperandQueries.is[Form.Dmnmx](operation)
    requireModifiers(if (wide) Set.empty else Set(Modifier.NAN, Modifier.FTZ, Modifier.XORSIGN))
    if (operands.size != 4) unsupported("unsupported floating min/max form")
    val bits: Int in (32, 64) = if (wide) 64 else 32
    val result                = extremum(input(operands(1), bits), input(operands(2), bits), operands(3), bits)
    store(operands.head, if (modifiers.contains(Modifier.XORSIGN)) xorSign(result) else result, bits)
  }

  /** `FMNMX3`: the extremum of three binary32 values, each pair as `FMNMX` takes it. */
  private def minMax3()(using context: VMCompilationContext): Unit = {
    import context.*
    requireModifiers(Set(Modifier.NAN, Modifier.FTZ))
    requireOperands(5, "FMNMX3 requires a destination, three sources and a predicate")
    val first = extremum(input(operands(1), 32), input(operands(2), 32), operands(4), 32)
    store(operands.head, extremum(first, input(operands(3), 32), operands(4), 32))
  }

  private def input(operand: Operand, bits: Int in (32, 64))(using context: VMCompilationContext): Value = {
    import context.*
    val value = if (bits == 64) doubleSource(operand) else floatSource(operand)
    if (modifiers.contains(Modifier.FTZ)) flushFloat(value) else value
  }

  /** `XORSIGN`: a result that is not NaN takes the exclusive or of the sources' register signs, read before their
    * absolute value or negation.
    */
  private def xorSign(result: Value)(using context: VMCompilationContext): Value = {
    import context.*
    def sign(operand: Operand): Value = binary(
      IntegerOperation.BitwiseAnd,
      floatSource(OperandQueries.unaryValue(OperandQueries.without(operand, OperandSelector.reuse))),
      Value.Immediate(0x80000000L)
    )
    val signed = binary(
      IntegerOperation.BitwiseOr,
      binary(IntegerOperation.BitwiseAnd, result, Value.Immediate(0x7fffffffL)),
      binary(IntegerOperation.BitwiseXor, sign(operands(1)), sign(operands(2)))
    )
    select(result, signed, compare(Comparison.Unordered, NumericFormat.IEEE(32), result, result))
  }

  /** The minimum where `selector` holds and the maximum otherwise; a NaN source yields the other source, or NaN under
    * `NAN`.
    */
  private def extremum(left: Value, right: Value, selector: Operand, bits: Int in (32, 64))(using
      context: VMCompilationContext
  ): Value.Register = {
    import context.*
    val format    = NumericFormat.IEEE(bits)
    val leftNan   = compare(Comparison.Unordered, format, left, left)
    val rightNan  = compare(Comparison.Unordered, format, right, right)
    val nanResult =
      combine(
        if (modifiers.contains(Modifier.NAN)) PredicateOperation.Or else PredicateOperation.And,
        leftNan,
        rightNan
      )
    val result = temp()
    val nan    = if (bits == 64) IEEE754.doubleToBinary64(Double.NaN) else VMArithmetic.CanonicalNaN32
    selector match {
      case Operand.Predicate(None, _, false) =>
        emit(FloatBinary(FloatOperation.Minimum, result.slot, left, right, bits))
      case Operand.Predicate(None, _, true) =>
        emit(FloatBinary(FloatOperation.Maximum, result.slot, left, right, bits))
      case _ =>
        val maximum = temp()
        emit(FloatBinary(FloatOperation.Minimum, result.slot, left, right, bits))
        emit(FloatBinary(FloatOperation.Maximum, maximum.slot, left, right, bits))
        emit(Select(result.slot, result, maximum, predicateSource(selector).slot, bits))
    }
    emit(Select(result.slot, left, result, rightNan.slot, bits))
    emit(Select(result.slot, right, result, leftNan.slot, bits))
    emit(Select(result.slot, Value.Immediate(nan), result, nanResult.slot, bits))
    result
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
      selected.headOption.getOrElse(Modifier.GT).comparisonCode,
      NumericFormat.IEEE(32),
      if (modifiers.contains(Modifier.FTZ)) flushFloat(condition) else condition,
      Value.Immediate(0L)
    )
    emit(Select(destination(operands.head).slot, floatSource(operands(1)), floatSource(operands(2)), holds.slot, 32))
  }

  private def integerToFloat()(using context: VMCompilationContext): Unit = {
    import context.*
    requireModifiers(integerToFloatModifiers)
    requireOperands(2, "invalid scalar integer-to-float operands")
    if (
      (OperandQueries.is[Form.I2fp](operation) || OperandQueries.is[Form.Ui2fp](operation)) &&
      (!modifiers.contains(Modifier.F32) ||
        modifiers.exists(PartialFunction.cond(_) { case Modifier.F64 | Modifier.U64 | Modifier.S64 => true }))
    ) unsupported("only scalar 32-bit I2FP conversion is implemented")
    // A half result is verified with the default rounding and with the rounding toward zero; the other roundings and the
    // bfloat16 results are verified only with the default rounding.
    if (modifiers.exists(_.isHalf) && modifiers.exists(roundings) && !modifiers.contains(Modifier.RZ))
      unsupported(s"unsupported I2F modifiers ${modifiers.mkString(".")}")
    val rounding = modifiers.find(roundings).getOrElse(Modifier.RN)
    val kind     =
      modifiers.find(integerSourceKinds).getOrElse(Modifier.S32)
    val target =
      if (modifiers.contains(Modifier.F64)) NumericFormat.IEEE(64)
      else if (modifiers.contains(Modifier.F16)) NumericFormat.IEEE(16)
      else if (modifiers.contains(Modifier.BF16)) NumericFormat.BF16
      else NumericFormat.IEEE(32)
    val bits: ScalarBitWidth       = if (modifiers.contains(Modifier.F64)) 64 else 32
    val inputWidth: ScalarBitWidth = if (kind.is64BitInteger) 64 else 32
    val (input, inputType)         = operands(1) match {
      case Operand.Unary(inner, false, false, true) if kind.isSigned32Or64 =>
        val extended = convert(source(inner, inputWidth), kind.integerConversion, NumericFormat.Signed(64))
        (unary(IntegerUnaryOperation.Absolute, extended, 64), NumericFormat.Unsigned(64))
      case operand => (source(operand, inputWidth), kind.integerConversion)
    }
    store(operands.head, convert(input, inputType, target, Some(rounding.conversionRounding)), bits)
  }

  private def floatToInteger()(using context: VMCompilationContext): Unit = {
    import context.*
    requireModifiers(floatToIntegerModifiers)
    val kind     = modifiers.find(integerKinds).getOrElse(Modifier.S32)
    val rounding =
      if (modifiers.contains(Modifier.TRUNC)) ConversionRounding.RZI
      else if (modifiers.contains(Modifier.FLOOR)) ConversionRounding.RMI
      else if (modifiers.contains(Modifier.CEIL)) ConversionRounding.RPI
      else ConversionRounding.RNI
    val double = modifiers.contains(Modifier.F64)
    val half   = modifiers.exists(_.isHalf)
    if ((double || half) && modifiers.contains(Modifier.FTZ)) unsupported(s"F2I.${modifiers.mkString(".")}")
    val (input, format) =
      if (double) (doubleSource(operands(1)), NumericFormat.IEEE(64))
      else if (modifiers.contains(Modifier.F16)) (source(operands(1)), NumericFormat.IEEE(16))
      else if (modifiers.contains(Modifier.BF16)) (source(operands(1)), NumericFormat.BF16)
      else if (modifiers.contains(Modifier.FTZ)) (flushFloat(floatSource(operands(1))), NumericFormat.IEEE(32))
      else (floatSource(operands(1)), NumericFormat.IEEE(32))
    store(
      operands.head,
      convert(input, format, kind.integerConversion, Some(rounding)),
      if (kind.is64BitInteger) 64 else 32
    )
  }

  /** `F2IP`: two binary32 values rounded to saturated 8-bit integers in the low two bytes, the first above, under a
    * half of the last source (its high half under `H1`). The measured sm_89 result of a NaN is 0x80, or 0 under `RELU`,
    * which also clamps negative values to zero.
    */
  private def floatToPackedBytes()(using context: VMCompilationContext): Unit = {
    import context.*
    requireModifiers(packedByteModifiers)
    requireOperands(4, s"${operation.mnemonic.raw} requires a destination and three sources")
    val signed   = modifiers.contains(Modifier.S8)
    val relu     = modifiers.contains(Modifier.RELU)
    val rounding = if (modifiers.contains(Modifier.TRUNC)) ConversionRounding.RZI else ConversionRounding.RNI
    def byte(operand: Operand): Value = {
      val value     = floatSource(operand)
      val converted = binary(
        IntegerOperation.BitwiseAnd,
        convert(
          value,
          NumericFormat.IEEE(32),
          if (signed) NumericFormat.Signed(8) else NumericFormat.Unsigned(8),
          Some(rounding)
        ),
        Value.Immediate(0xffL)
      )
      if (relu) select(
        Value.Immediate(0L),
        converted,
        compare(Comparison.LessThanUnordered, NumericFormat.IEEE(32), value, Value.Immediate(0L))
      )
      else
        select(Value.Immediate(0x80L), converted, compare(Comparison.Unordered, NumericFormat.IEEE(32), value, value))
    }
    val high = byte(operands(1))
    val low  = byte(operands(2))
    store(
      operands.head,
      binary(
        IntegerOperation.BitwiseOr,
        binary(
          IntegerOperation.ShiftLeft,
          VMHalfCompiler.scalarHalf(operands(3), bfloat16 = false),
          Value.Immediate(16L)
        ),
        binary(IntegerOperation.BitwiseOr, binary(IntegerOperation.ShiftLeft, high, Value.Immediate(8L)), low)
      )
    )
  }

  private def doubleHighReciprocal()(using context: VMCompilationContext): Unit = {
    import context.*
    requireModifiers(Set(Modifier.RCP64H, Modifier.RSQ64H))
    def flushHigh(value: Value): Value = value match {
      case Value.Immediate(bits) => Value.Immediate(if ((bits & 0x7ff00000L) == 0L) bits & 0x80000000L else bits)
      case _                     =>
        val exponent = binary(IntegerOperation.BitwiseAnd, value, Value.Immediate(0x7ff00000L))
        val sign     = binary(IntegerOperation.BitwiseAnd, value, Value.Immediate(0x80000000L))
        select(sign, value, compare(Comparison.Equal, NumericFormat.Unsigned(32), exponent, Value.Immediate(0L)))
    }
    def high(value: Value): Value = narrow(binary(IntegerOperation.ShiftRightLogical, value, Value.Immediate(32L), 64))
    val raw                       = OperandQueries.without(operands(1), OperandSelector.`reuse`)
    val upper                     =
      if (!OperandQueries.numericFloating(raw)) floatSource(raw)
      else
        doubleSource(raw) match {
          case Value.Immediate(bits) => Value.Immediate(bits >>> 32)
          case value                 => high(value)
        }
    val input  = temp()
    val result = temp()
    flushHigh(upper) match {
      case Value.Immediate(bits) => emit(Move(input.slot, Value.Immediate((bits & 0xffffffffL) << 32), 64))
      case high                  => emit(MoveCombine(input.slot, Vec(Value.Immediate(0L), high), 32))
    }
    emit(FloatUnary(
      if (modifiers.contains(Modifier.RCP64H)) FloatUnaryOperation.Reciprocal
      else FloatUnaryOperation.ReciprocalSquareRoot,
      result.slot,
      input,
      64
    ))
    store(operands.head, flushHigh(high(result)))
  }

  /** The special-function unit's function, whose polynomials differ between the architecture families, and the family's
    * flavour of them; the hyperbolic tangent has no polynomial of its own in the unit.
    *
    * The square root's own polynomial arrived with Pascal, and the trigonometric ones with Volta, which also changed
    * the argument from radians to cycles.
    */
  private def mufuFunction(modifier: Modifier)(using
      context: VMCompilationContext
  ): Option[(MufuFunction, MufuFlavour)] = {
    import context.*
    val flavour = if (legacy) MufuFlavour.Legacy else MufuFlavour.Modern
    modifier match {
      case Modifier.RCP  => Some((MufuFunction.Reciprocal, flavour))
      case Modifier.RSQ  => Some((MufuFunction.ReciprocalSquareRoot, flavour))
      case Modifier.LG2  => Some((MufuFunction.Log2, flavour))
      case Modifier.EX2  => Some((MufuFunction.Exponent2, flavour))
      case Modifier.SQRT => Some((MufuFunction.SquareRoot, if (sm < 61) MufuFlavour.Legacy else MufuFlavour.Modern))
      case Modifier.SIN  => Some((if (legacy) MufuFunction.Sine else MufuFunction.SineCycles, flavour))
      case Modifier.COS  => Some((if (legacy) MufuFunction.Cosine else MufuFunction.CosineCycles, flavour))
      case Modifier.TANH => None
      case _             => unsupported(s"unsupported MUFU function $modifier")
    }
  }

  private def special()(using context: VMCompilationContext): Unit = {
    import context.*
    requireModifiers(mufuModifiers)
    val modifier = modifiers.find(mufuFunctions).getOrElse(unsupported("MUFU requires a function"))
    if (operands.size != 2 && (operands.size != 3 || !modifiers.exists(_.isPackedHalf)))
      unsupported("MUFU requires a destination and a source")
    val approximation = mufuFunction(modifier)
    // The `.ftz` modifier flushes the subnormal operand and the subnormal result of the unit.
    val control = FloatControl(flushSubnormal = modifiers.contains(Modifier.FTZ))
    // The legacy trigonometric functions take the angle in radians, which the fused range reduction produces, whereas
    // the modern ones take it in cycles, which the unit reduces itself.
    def evaluate(raw: Value): Value = approximation match {
      case None =>
        // The unit keeps the subnormal operand and the subnormal result of the hyperbolic tangent.
        val result = temp()
        emit(FloatUnary(FloatUnaryOperation.Tanh, result.slot, raw, 32))
        result
      case Some((function, flavour)) =>
        if ((function == MufuFunction.Sine || function == MufuFunction.Cosine) && reducedRadians.isEmpty)
          unsupported("legacy MUFU trigonometric range-reduction encoding")
        val result = temp()
        emit(MufuUnary(result.slot, raw, function, flavour, control))
        // the unit's fixed-point output cannot be a subnormal value
        flushFloat(result)
    }
    // The half forms compute on the binary32 value of each half. The exponential's own unit rounds its fixed-point
    // result toward zero, whereas the other functions round their result to nearest.
    def half(value: Value, format: NumericFormat): Value = {
      val rounding =
        if (approximation.exists(_._1 == MufuFunction.Exponent2)) ConversionRounding.RZ else ConversionRounding.RN
      convert(
        evaluate(convert(value, format, NumericFormat.IEEE(32), Some(ConversionRounding.RN))),
        NumericFormat.IEEE(32),
        format,
        Some(rounding)
      )
    }
    val bfloat16 = modifiers.exists(PartialFunction.cond(_) { case Modifier.BF16 | Modifier.BF16x2 => true })
    val format   = if (bfloat16) NumericFormat.BF16 else NumericFormat.IEEE(16)
    store(
      operands.head,
      if (modifiers.exists(_.isPackedHalf)) {
        val pair = operands match {
          case Vec(_, high, low) =>
            Value.Immediate((VMHalfCompiler.halfConstant(high, bfloat16) << 16) |
              VMHalfCompiler.halfConstant(low, bfloat16))
          case _ => source(operands(1))
        }
        def part(shift: Int in (0, 16)): Value = half(VMHalfCompiler.extract(pair, shift), format)
        binary(IntegerOperation.BitwiseOr, binary(IntegerOperation.ShiftLeft, part(16), Value.Immediate(16L)), part(0))
      } else if (modifiers.exists(_.isHalf))
        half(VMHalfCompiler.scalarHalf(operands(1), bfloat16), format)
      else evaluate(reducedRadians.getOrElse(floatSource(operands(1))))
    )
  }

  private def divisionCheck()(using context: VMCompilationContext): Unit = {
    import context.*
    requireModifiers(if (legacy) Set(Modifier.DIVIDE) else Set.empty)
    if (legacy && modifiers != Vec(Modifier.DIVIDE))
      unsupported("legacy FCHK requires the explicit division mode")
    requireOperands(3, "invalid FCHK operands")
    if (Set(70, 75)(sm) && (instruction.guard.nonEmpty || !OperandQueries.isPredicate(operands.head)))
      unsupported("unsupported sm_70/sm_75 FCHK predicate form")
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
    val double   = OperandQueries.is[Form.Dset](operation)
    val zeroFlag = !double && zeroFlagFset(using context)
    if (OperandQueries.has(operands.head, OperandSelector.CC) && !zeroFlag)
      unsupported(s"${operation.mnemonic.raw} condition-code output is not implemented")
    if (double && modifiers.contains(Modifier.FTZ)) unsupported("DSET does not support FTZ")
    requireModifiers(setModifiers)
    val combiner = modifiers.find(setCombiners)
    requireOperands(
      if (combiner.nonEmpty) 4 else 3,
      s"${operation.mnemonic.raw} requires destination, two sources and, when it combines, a predicate"
    )
    def input(operand: Operand): Value = {
      val value = if (double) doubleSource(operand) else floatSource(operand)
      if (modifiers.contains(Modifier.FTZ)) flushFloat(value) else value
    }
    val left                   = input(operands(1))
    val right                  = input(operands(2))
    val format                 = NumericFormat.IEEE(if (double) 64 else 32)
    val holds: Value.Predicate = modifiers.find(setRelations ++ Set(Modifier.F, Modifier.T)) match {
      case Some(Modifier.F) => Value.Predicate(VMRegisters.Predicate.False.slot)
      case Some(Modifier.T) => Value.Predicate(VMRegisters.Predicate.True.slot)
      case Some(relation)   => compare(relation.comparisonCode, format, left, right)
      case None             => unsupported(s"${operation.mnemonic.raw} requires a comparison")
    }
    val result = combiner.fold(holds)(found => combine(found.predicateOperation, holds, predicateSource(operands(3))))
    // The observed consumer reads the zero flag, clear when the masked comparison is true.
    if (zeroFlag) emit(PredicateUnary(VMRegisters.Predicate.Zero.slot, result.slot))
    else {
      val truth = if (modifiers.contains(Modifier.BF)) 0x3f800000L else 0xffffffffL
      store(operands.head, select(Value.Immediate(truth), Value.Immediate(0L), result))
    }
  }

  private def floatPair(kind: FloatOperation)(using context: VMCompilationContext): Unit = {
    import context.*
    requireOperands(3, "packed float32 requires a register pair and two sources")
    val (destination, rounding, control) = floatPairForm()
    val left                             = floatPairOperand(operands(1))
    val right                            = floatPairOperand(operands(2))
    val result                           = temp()
    if (rounding == ConversionRounding.RN)
      emit(FloatBinaryPacked(kind, result.slot, left, right, PackedFloatFormat.F32x2, control))
    else {
      // Directed sums and products round each exact result once through the fused form; a zero addend of this sign
      // cannot flip the sign of an exact zero product under the rounding.
      val zero = Value.Immediate(if (rounding == ConversionRounding.RM) 0L else 0x80000000L)
      val one  = Value.Immediate(0x3f800000L)
      emit(MoveCombine(
        result.slot,
        words(left).zip(words(right)).map { (x, y) =>
          val element = temp()
          emit(
            if (kind == FloatOperation.Add) FusedMultiplyAdd(element.slot, x, one, y, 32, control, rounding)
            else FusedMultiplyAdd(element.slot, x, y, zero, 32, control, rounding)
          )
          element
        },
        32
      ))
    }
    store(destination, result, 64)
  }

  /** `FFMA2`: the fused multiply-add of each element of two register pairs, rounded once as the modifier says. */
  private def floatPairFusedMultiplyAdd()(using context: VMCompilationContext): Unit = {
    import context.*
    requireOperands(4, "FFMA2 requires a register pair and three sources")
    val (destination, rounding, control) = floatPairForm()
    val inputs                           = operands.slice(1, 4).map(operand => words(floatPairOperand(operand)))
    val result                           = temp()
    emit(MoveCombine(
      result.slot,
      Vec(0, 1).map { index =>
        val element = temp()
        emit(FusedMultiplyAdd(
          element.slot,
          inputs(0)(index),
          inputs(1)(index),
          inputs(2)(index),
          32,
          control,
          rounding
        ))
        element
      },
      32
    ))
    store(destination, result, 64)
  }

  /** The destination pair, rounding and subnormal flush of a packed binary32 instruction. */
  private def floatPairForm()(using
      context: VMCompilationContext
  ): (destination: Operand.Register, rounding: ConversionRounding, control: FloatControl) = {
    import context.*
    if (!PartialFunction.cond(sm) { case 100 | 103 => true }) unsupported("packed float32 requires sm_100 or sm_103")
    requireModifiers(roundings + Modifier.FTZ)
    val destination = operands.head match {
      case value @ Operand.Register(RegisterBank.R, _) => value
      case _ => unsupported("packed float32 destination must be an aligned register pair or RZ")
    }
    validateFloatPairRegister(destination, 2)
    (
      destination,
      modifiers.find(roundings).getOrElse(Modifier.RN).conversionRounding,
      FloatControl(flushSubnormal = modifiers.contains(Modifier.FTZ))
    )
  }

  /** The low and high words of a pair. */
  private def words(value: Value)(using context: VMCompilationContext): Vec[Value] = {
    val parts = Vec(context.temp(), context.temp())
    context.emit(MoveSplit(parts.map(part => Some(part.slot)), value, 32))
    parts
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
      case Operand.Unary(_, _, _, _) => unsupported("packed float32 does not support bitwise source inversion")
      case _                         =>
        val selected = OperandQueries.qualifiers(operand)
        val base     = OperandQueries.base(OperandQueries.without(operand, OperandSelector.reuse))
        val scalar   = selected == Vec(OperandSelector.F32)
        def duplicated(value: Value): Value.Register = {
          val result = temp()
          value match {
            case Value.Immediate(word) =>
              emit(Move(result.slot, Value.Immediate((word & 0xffffffffL) | (word << 32)), 64))
            case _ => emit(MoveCombine(result.slot, Vec(value, value), 32))
          }
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
          case _ if selected.isEmpty && !OperandQueries.hasUnknownDomain(operand) => duplicated(floatSource(operand))
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
      !OperandQueries.isZero(target) &&
      (BigInt(target.index) + words.raw > capacity || (words == 2 && target.index.raw % 2 != 0))
    ) unsupported("packed float32 source or destination exceeds its bank or is not an aligned register pair")
  }

  private val integral = Map(
    Modifier.ROUND -> ConversionRounding.RNI,
    Modifier.FLOOR -> ConversionRounding.RMI,
    Modifier.CEIL  -> ConversionRounding.RPI,
    Modifier.TRUNC -> ConversionRounding.RZI
  )
  private val floatToFloatModifiers = floatFormats ++ roundings ++ integral.keySet ++
    Set(Modifier.PASS, Modifier.FTZ, Modifier.SAT)
  private val roundIntegralModifiers = integral.keySet ++
    Set(Modifier.F16, Modifier.BF16, Modifier.F32, Modifier.F64, Modifier.FTZ)

  private def numeric(format: Modifier): NumericFormat = format match {
    case Modifier.F16                  => NumericFormat.IEEE(16)
    case Modifier.BF16 | Modifier.E8M7 => NumericFormat.BF16
    case Modifier.F64                  => NumericFormat.IEEE(64)
    case _                             => NumericFormat.IEEE(32)
  }

  /** A floating source of `format`: a binary64 pair, a binary32 word, or one half. */
  private def floatingSource(operand: Operand, format: NumericFormat)(using context: VMCompilationContext): Value =
    if (format == NumericFormat.IEEE(64)) context.doubleSource(operand)
    else if (format == NumericFormat.IEEE(32)) context.floatSource(operand)
    else VMHalfCompiler.scalarHalf(operand, format == NumericFormat.BF16)

  /** `value` of `format` rounded to an integral value; bfloat16 values round as their exact binary32 value. */
  private def integralValue(value: Value, format: NumericFormat, rounding: ConversionRounding)(using
      context: VMCompilationContext
  ): Value =
    if (format != NumericFormat.BF16) context.convert(value, format, format, Some(rounding))
    else {
      val single = context.convert(value, format, NumericFormat.IEEE(32))
      context.convert(
        context.convert(single, NumericFormat.IEEE(32), NumericFormat.IEEE(32), Some(rounding)),
        NumericFormat.IEEE(32),
        format,
        Some(ConversionRounding.RN)
      )
    }

  /** `F2F`: a conversion between floating formats rounded as its modifier says, or within one format a move or a
    * rounding to an integral value; `FTZ` flushes binary32 inputs and results and `SAT` clamps to [0, 1].
    */
  private def floatToFloat()(using context: VMCompilationContext): Unit = {
    import context.*
    requireModifiers(floatToFloatModifiers)
    requireOperands(2, s"${operation.mnemonic.raw} requires a destination and a source")
    val (target, from) = modifiers.filter(floatFormats).map(numeric(_)) match {
      case Vec()                    => (NumericFormat.IEEE(32), NumericFormat.IEEE(32))
      case Vec(destination, source) => (destination, source)
      case _                        => unsupported(s"${operation.mnemonic.raw} names a destination and a source format")
    }
    val same = target == from
    // Within one format the rounding modes encode the integral roundings, which nvdisasm spells ROUND to TRUNC.
    val explicit = modifiers.find(roundings).filterNot(_ => same).map(_.conversionRounding)
    val whole    = modifiers.flatMap(integral.get).headOption.orElse(modifiers.find(roundings).filter(_ => same).map {
      case Modifier.RM => ConversionRounding.RMI
      case Modifier.RP => ConversionRounding.RPI
      case Modifier.RZ => ConversionRounding.RZI
      case _           => ConversionRounding.RNI
    })
    if (!same && (whole.nonEmpty || modifiers.contains(Modifier.PASS)))
      unsupported("F2F between formats rounds with RN, RM, RP or RZ")
    val flush   = modifiers.contains(Modifier.FTZ)
    val raw     = floatingSource(operands(1), from)
    val input   = if (flush && from == NumericFormat.IEEE(32)) flushFloat(raw) else raw
    val clamp   = Option.when(modifiers.contains(Modifier.SAT))(ConversionClamp.Saturate)
    val rounded = whole.fold(input)(integralValue(input, from, _))
    val result  =
      if (same && clamp.isEmpty) rounded
      else {
        val converted = temp()
        emit(Convert(converted.slot, rounded, from, target, Some(explicit.getOrElse(ConversionRounding.RN)), clamp))
        converted
      }
    store(
      operands.head,
      if (flush && target == NumericFormat.IEEE(32)) flushFloat(result) else result,
      if (target == NumericFormat.IEEE(64)) 64 else 32
    )
  }

  /** `FRND`: a binary16, bfloat16, binary32 or binary64 value rounded to an integral value of its format. */
  private def roundIntegral()(using context: VMCompilationContext): Unit = {
    import context.*
    requireModifiers(roundIntegralModifiers)
    requireOperands(2, s"invalid ${operation.mnemonic.raw} operands")
    val format = modifiers.find(PartialFunction.cond(_) { case Modifier.F16 | Modifier.BF16 | Modifier.F64 =>
      true
    }).fold(NumericFormat.IEEE(32))(numeric(_))
    val raw    = floatingSource(operands(1), format)
    val flush  = modifiers.contains(Modifier.FTZ) && format == NumericFormat.IEEE(32)
    val result = integralValue(
      if (flush) flushFloat(raw) else raw,
      format,
      modifiers.flatMap(integral.get).headOption.getOrElse(ConversionRounding.RNI)
    )
    store(operands.head, result, if (format == NumericFormat.IEEE(64)) 64 else 32)
  }

  private def packHalves()(using context: VMCompilationContext): Unit = {
    import context.*
    val float8 = modifiers.collectFirst {
      case Modifier.E4M3 => Float8Format.E4M3
      case Modifier.E5M2 => Float8Format.E5M2
    }
    val narrow = modifiers.collectFirst {
      case Modifier.E2M1 => NarrowFormat.E2M1
      case Modifier.E2M3 => NarrowFormat.E2M3
      case Modifier.E3M2 => NarrowFormat.E3M2
      case Modifier.E8   => NarrowFormat.UE8M0
    }
    (float8, narrow) match {
      case (Some(format), _)                                 => packFloat8(format)
      case (None, Some(format))                              => packNarrow(format)
      case (None, None) if modifiers.contains(Modifier.TF32) => packTensorFloat32()
      case (None, None)                                      => packHalves16()
    }
  }

  /** The 4- and 6-bit floating conversions and those of the UE8M0 scale (`E8`): two binary32 values or a binary16 pair
    * into the low half over the upper half of the last source, or a pair widened to binary16 or, from UE8M0, bfloat16.
    */
  private def packNarrow(format: NarrowFormat)(using context: VMCompilationContext): Unit = {
    import context.*
    val result   = temp()
    val relu     = modifiers.contains(Modifier.RELU)
    val saturate = modifiers.contains(Modifier.SATFINITE)
    val rounding = modifiers.collectFirst {
      case Modifier.RZ => ConversionRounding.RZ
      case Modifier.RP => ConversionRounding.RP
    }
    val scale  = Value.Immediate(0x7f7fL)
    val scaled = format == NarrowFormat.UE8M0
    if ((scaled && relu) || (!scaled && rounding.nonEmpty))
      unsupported(s"unsupported F2FP modifiers ${modifiers.mkString(".")}")
    def merged(rest: Operand): Value = binary(
      IntegerOperation.BitwiseOr,
      binary(IntegerOperation.BitwiseAnd, source(OperandQueries.base(rest)), Value.Immediate(0xffff0000L)),
      result
    )
    def narrowed(high: Value, low: Value, halves: Boolean) = {
      if (scaled && rounding.isEmpty) unsupported("F2FP to UE8M0 requires RZ or RP")
      emit(ConvertNarrow(
        result.slot,
        high,
        low,
        scale,
        format,
        halves,
        bf16 = false,
        rounding.getOrElse(ConversionRounding.RN),
        relu,
        saturate
      ))
    }
    modifiers.filterNot(PartialFunction.cond(_) {
      case Modifier.E2M1 | Modifier.E2M3 | Modifier.E3M2 | Modifier.E8 | Modifier.RELU | Modifier.SATFINITE |
          Modifier.RZ | Modifier.RP => true
    }) match {
      case Vec(Modifier.F32, Modifier.PACK_AB_MERGE_C) =>
        requireOperands(4, "F2FP.PACK_AB_MERGE_C requires destination and three sources")
        narrowed(source(operands(1)), source(operands(2)), halves = false)
        store(operands.head, merged(operands(3)))
      case Vec(Modifier.F16, Modifier.UNPACK_B_MERGE_C) if !scaled =>
        requireOperands(3, "F2FP.UNPACK_B_MERGE_C requires destination and two sources")
        narrowed(source(operands(1)), Value.Immediate(0L), halves = true)
        store(operands.head, merged(operands(2)))
      case Vec(target @ (Modifier.F16 | Modifier.BF16), Modifier.UNPACK_B)
          if !relu && !saturate && rounding.isEmpty && (target == Modifier.BF16) == scaled =>
        requireOperands(2, "F2FP.UNPACK_B requires destination and source")
        val pair = unpackHalf(operands(1))
        emit(ConvertWiden(result.slot, pair, scale, format, bf16 = scaled, relu = false, saturate = false))
        store(operands.head, result)
      case _ => unsupported(s"unsupported F2FP modifiers ${modifiers.mkString(".")}")
    }
  }

  /** The half of the source the selector names, or the whole source when it names none. */
  private def unpackHalf(operand: Operand)(using context: VMCompilationContext): Value = {
    import context.*
    OperandQueries.qualifiers(operand) match {
      case Vec()                   => source(operand)
      case Vec(OperandSelector.H1) =>
        binary(IntegerOperation.ShiftRightLogical, source(OperandQueries.base(operand)), Value.Immediate(16L))
      case Vec(OperandSelector.H0) => source(OperandQueries.base(operand))
      case _                       => unsupported("unsupported F2FP.UNPACK_B source half")
    }
  }

  /** `cvt.rn` and `cvt.rz` to TF32 from sm_90, into one register. */
  private def packTensorFloat32()(using context: VMCompilationContext): Unit = {
    import context.*
    val options = modifiers.filterNot(PartialFunction.cond(_) { case Modifier.RELU | Modifier.SATFINITE | Modifier.RZ =>
      true
    })
    if (options != Vec(Modifier.TF32, Modifier.F32, Modifier.PACK_B) && options != Vec(Modifier.TF32, Modifier.PACK_B))
      unsupported(s"unsupported F2FP modifiers ${modifiers.mkString(".")}")
    requireOperands(2, "F2FP.TF32 requires destination and source")
    val result = temp()
    emit(ConvertTF32(
      result.slot,
      source(operands(1)),
      away = false,
      modifiers.contains(Modifier.RZ),
      modifiers.contains(Modifier.RELU),
      modifiers.contains(Modifier.SATFINITE)
    ))
    store(operands.head, result)
  }

  /** The 8-bit floating conversions: two binary32 values or a binary16 pair into the low half over the upper half of
    * the last source, or a pair of 8-bit values widened to binary16.
    */
  private def packFloat8(format: Float8Format)(using context: VMCompilationContext): Unit = {
    import context.*
    val result = temp()
    val relu   = modifiers.contains(Modifier.RELU)
    modifiers.filterNot(PartialFunction.cond(_) { case Modifier.E4M3 | Modifier.E5M2 | Modifier.RELU => true }) match {
      case Vec(Modifier.SATFINITE, Modifier.F32, Modifier.PACK_AB_MERGE_C) =>
        requireOperands(4, "F2FP.PACK_AB_MERGE_C requires destination and three sources")
        // the merge source names the high half of a register, whose low half the packed bytes replace
        emit(ConvertToFloat8(
          result.slot,
          source(operands(1)),
          source(operands(2)),
          source(OperandQueries.base(operands(3))),
          format,
          halves = false,
          relu
        ))
      case Vec(Modifier.SATFINITE, Modifier.F16, Modifier.UNPACK_B_MERGE_C) =>
        requireOperands(3, "F2FP.UNPACK_B_MERGE_C requires destination and two sources")
        val pair = source(operands(1))
        emit(ConvertToFloat8(result.slot, pair, pair, source(operands(2)), format, halves = true, relu))
      case Vec(Modifier.F16, Modifier.UNPACK_B) if !relu =>
        requireOperands(2, "F2FP.UNPACK_B requires destination and source")
        val pair = unpackHalf(operands(1))
        emit(ConvertFromFloat8(result.slot, pair, format))
      case _ => unsupported(s"unsupported F2FP modifiers ${modifiers.mkString(".")}")
    }
    store(operands.head, result)
  }

  /** Binary32 values rounded to binary16 or bfloat16: two into the halves of one register, the first above, or one into
    * the low half below a half of the last source (`MERGE_C`, its high half under `H1`); `RELU` clamps negative results
    * to zero, `SATFINITE` infinite ones to the largest finite value, and `RS` rounds stochastically with the random
    * bits of the last source.
    */
  private def packHalves16()(using context: VMCompilationContext): Unit = {
    import context.*
    requireModifiers(packHalvesModifiers)
    val bfloat16 = modifiers.exists(PartialFunction.cond(_) { case Modifier.BF16 | Modifier.E8M7 => true })
    if (
      modifiers.contains(Modifier.F32) &&
      !modifiers.exists(PartialFunction.cond(_) { case Modifier.F16 | Modifier.BF16 | Modifier.E8M7 => true })
    )
      unsupported(s"unsupported F2FP modifiers ${modifiers.mkString(".")}")
    val format                            = if (bfloat16) NumericFormat.BF16 else NumericFormat.IEEE(16)
    val relu                              = modifiers.contains(Modifier.RELU)
    val saturated                         = modifiers.contains(Modifier.SATFINITE)
    val largest                           = if (bfloat16) 0x7f7fL else 0x7bffL
    def narrowed(operand: Operand): Value = {
      val result = convert(
        floatSource(operand),
        NumericFormat.IEEE(32),
        format,
        Some(if (modifiers.contains(Modifier.RZ)) ConversionRounding.RZ else ConversionRounding.RN)
      )
      if (relu) emit(Convert(result.slot, result, format, format, None, Some(ConversionClamp.Relu)))
      if (!saturated) result
      else {
        val magnitude = binary(IntegerOperation.BitwiseAnd, result, Value.Immediate(0x7fffL))
        select(
          binary(
            IntegerOperation.BitwiseOr,
            binary(IntegerOperation.BitwiseAnd, result, Value.Immediate(0x8000L)),
            Value.Immediate(largest)
          ),
          result,
          compare(
            Comparison.Equal,
            NumericFormat.Unsigned(32),
            magnitude,
            Value.Immediate(if (bfloat16) 0x7f80L else 0x7c00L)
          )
        )
      }
    }
    store(
      operands.head,
      (modifiers.contains(Modifier.MERGE_C), modifiers.contains(Modifier.RS)) match {
        case (false, true) =>
          if (
            !modifiers.contains(Modifier.PACK_AB) || modifiers.contains(Modifier.RZ) ||
            !PartialFunction.cond(sm) { case 100 | 103 => true }
          )
            unsupported(s"unsupported F2FP modifiers ${modifiers.mkString(".")}")
          requireOperands(4, "F2FP.PACK_AB.RS requires destination, two sources and random bits")
          val result = temp()
          emit(ConvertStochastic(
            result.slot,
            floatSource(operands(1)),
            floatSource(operands(2)),
            source(operands(3)),
            Value.Immediate(0L),
            if (bfloat16) StochasticFormat.BFloat16 else StochasticFormat.Half,
            false,
            relu,
            saturated
          ))
          result
        case (true, false) if !modifiers.contains(Modifier.PACK_AB) =>
          requireOperands(3, "F2FP.MERGE_C requires destination and two sources")
          VMHalfCompiler.halves(VMHalfCompiler.scalarHalf(operands(2), bfloat16), narrowed(operands(1)))
        case (false, false) if modifiers.contains(Modifier.PACK_AB) =>
          requireOperands(3, "F2FP.PACK_AB requires destination and two sources")
          val low = narrowed(operands(2))
          VMHalfCompiler.halves(narrowed(operands(1)), low)
        case _ => unsupported(s"unsupported F2FP modifiers ${modifiers.mkString(".")}")
      }
    )
  }

}
