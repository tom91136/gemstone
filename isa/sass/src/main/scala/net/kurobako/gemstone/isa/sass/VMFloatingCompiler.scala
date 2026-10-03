package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.isa.nvidia.VMIR.Instruction.*
import net.kurobako.gemstone.isa.sass.Instruction.*
import net.kurobako.gemstone.isa.sass.Instruction as SASSInstruction

/** Typed lowering for packed-half arithmetic and the associated SASS conversion forms. */
private object VMFloatingCompiler {
  private val HalfMask = 0xffffL

  def compile(context: VMCompilationContext): Boolean = {
    val instruction = context.instruction
    val operands    = instruction.operands
    val modifiers   = instruction.modifiers

    instruction.operation match {
      case _: SASSInstruction.Hadd2
          if modifiers.isEmpty && operands.lift(1).exists(VMOperand.has(_, OperandSelector.`F32`)) =>
        lowerScalarToHalf(context, operands)
      case _: SASSInstruction.Hadd2 if modifiers.isEmpty =>
        lowerPackedBinary(context, operands, FloatOperation.Add, "HADD2")
      case _: SASSInstruction.Hadd2 if modifiers == Vec(Modifier.`F32`) => lowerHalfToScalar(context, operands)
      case _: SASSInstruction.Hadd2 => unsupported(s"unsupported HADD2 modifiers ${modifiers.mkString(".")}")
      case _: SASSInstruction.Hmul2 if modifiers == Vec(Modifier.`F32`) => lowerHalfMultiplyToScalar(context, operands)
      case _: SASSInstruction.Hmul2 if modifiers.isEmpty                =>
        lowerPackedBinary(context, operands, FloatOperation.Multiply, "HMUL2")
      case _: SASSInstruction.Hmul2  => unsupported(s"unsupported HMUL2 modifiers ${modifiers.mkString(".")}")
      case _: SASSInstruction.Hfma2  => lowerPackedFma(context, modifiers, operands)
      case _: SASSInstruction.Hsetp2 => lowerPackedCompare(context, modifiers, operands)
      case _: SASSInstruction.Hmnmx2 => lowerPackedMinMax(context, modifiers, operands)
      case _: SASSInstruction.F2f if modifiers == Vec(Modifier.`F16`, Modifier.`F32`) =>
        lowerFloatToHalf(context, operands)
      case _: SASSInstruction.F2fp => lowerPackHalves(context, modifiers, operands)
      case _                       => false
    }
  }

  private def lowerPackedBinary(
      context: VMCompilationContext,
      operands: Vec[Operand],
      operation: FloatOperation,
      opcode: String
  ): Boolean = {
    requireSize(operands, 3, s"$opcode requires destination and two sources")
    val result = context.temp()
    context.emit(FloatBinaryPacked(
      operation,
      result.name,
      packedOperand(context, operands(1), opcode),
      packedOperand(context, operands(2), opcode),
      false
    ))
    context.store(operands.head, canonicalizeNaNs(context, result))
    true
  }

  private def lowerPackedFma(
      context: VMCompilationContext,
      modifiers: Vec[Modifier],
      operands: Vec[Operand]
  ): Boolean = {
    val mmaUnitScaleAlias = modifiers == Vec(Modifier.`MMA`) && operands.size == 5 &&
      isBareGeneralOrZero(operands(1)) && isBareGeneralOrZero(operands(4)) &&
      isUnitOne(operands(2)) && isUnitOne(operands(3))

    if (mmaUnitScaleAlias) {
      val sum = context.temp()
      context.emit(FloatBinaryPacked(
        FloatOperation.Add,
        sum.name,
        packedOperand(context, operands(1), "HFMA2.MMA"),
        packedOperand(context, operands(4), "HFMA2.MMA"),
        false
      ))
      context.store(operands.head, canonicalizeNaNs(context, sum))
      true
    } else if (
      modifiers == Vec(Modifier.`MMA`) && operands.size == 5 &&
      isNegativeZero(operands(1)) && VMOperand.isZero(operands(2))
    ) {
      val high = halfConstant(operands(3))
      val low  = halfConstant(operands(4))
      context.store(operands.head, Value.Immediate((high << 16) | low))
      true
    } else {
      requireSize(operands, 4, "HFMA2 requires destination and three sources")
      val mmaAccumulatorAlias = modifiers == Vec(Modifier.`MMA`) && isBareGeneralOrZero(operands(1)) &&
        VMOperand.isZero(operands(2)) && operands.head == operands.last
      if (modifiers.nonEmpty && !mmaAccumulatorAlias)
        unsupported(s"unsupported HFMA2 modifiers ${modifiers.mkString(".")}")

      val product = context.temp()
      context.emit(FusedMultiplyAddPacked(
        product.name,
        packedOperand(context, operands(1), "HFMA2"),
        packedOperand(context, operands(2), "HFMA2"),
        packedOperand(context, operands(3), "HFMA2"),
        false,
        false
      ))
      context.store(operands.head, canonicalizeNaNs(context, product))
      true
    }
  }

  private def lowerPackedCompare(
      context: VMCompilationContext,
      modifiers: Vec[Modifier],
      operands: Vec[Operand]
  ): Boolean = {
    if (modifiers.size != 2 || !Set(Modifier.`GEU`, Modifier.`NE`)(modifiers.head) || modifiers(1) != Modifier.`AND`)
      unsupported(s"unsupported HSETP2 modifiers ${modifiers.mkString(".")}")
    requireSize(operands, 5, "HSETP2 requires two predicate results, two sources and PT")
    if (operands(4) != VMOperand.True) unsupported("HSETP2 requires a PT predicate input")
    if (!supportedHalfSource(operands(2)) || !supportedHalfSource(operands(3)))
      unsupported("HSETP2 supports only register or duplicated H0_H0/H1_H1 sources")
    val first  = predicateDestination(operands.head)
    val second = predicateDestination(operands(1))
    if (second.nonEmpty && modifiers.head != Modifier.`GEU`)
      unsupported("HSETP2 dual results are verified only for GEU")
    if (second.nonEmpty && first == second)
      unsupported("HSETP2 dual predicate results must be distinct")

    val comparison =
      if (modifiers.head == Modifier.`NE`) Comparison.NotEqual else Comparison.GreaterThanOrEqualUnordered
    val low = context.temp("q")
    context.emit(SetPredicate(
      low.name,
      comparison,
      ConversionType.F32.format,
      halfAsFloat(context, operands(2), 0),
      halfAsFloat(context, operands(3), 0)
    ))
    first.foreach(destination => writePredicate(context, destination, low))
    second.foreach { destination =>
      val high = context.temp("q")
      context.emit(SetPredicate(
        high.name,
        comparison,
        ConversionType.F32.format,
        halfAsFloat(context, operands(2), 16),
        halfAsFloat(context, operands(3), 16)
      ))
      writePredicate(context, destination, high)
    }
    true
  }

  private def lowerPackedMinMax(
      context: VMCompilationContext,
      modifiers: Vec[Modifier],
      operands: Vec[Operand]
  ): Boolean = {
    if (modifiers.nonEmpty) unsupported(s"unsupported HMNMX2 modifiers ${modifiers.mkString(".")}")
    requireSize(operands, 4, "HMNMX2 requires destination, two sources, and a predicate")
    val isMin = operands(3) match {
      case Operand.Predicate(None, false, false) => true
      case Operand.Predicate(None, false, true)  => false
      case _                                     => unsupported("HMNMX2 supports only PT and !PT selectors")
    }
    if (operands.slice(1, 3).exists(VMOperand.has(_, OperandSelector.`H1_H1`)))
      unsupported("HMNMX2 supports only raw packed registers and verified H0_H0 selectors")
    val left   = packedOperand(context, operands(1), "HMNMX2")
    val right  = packedOperand(context, operands(2), "HMNMX2")
    val halves = Vec(0, 16).map { offset =>
      val a       = halfAsFloat(context, left, offset)
      val b       = halfAsFloat(context, right, offset)
      val aNan    = context.temp("q")
      val bNan    = context.temp("q")
      val bothNan = context.temp("q")
      context.emit(SetPredicate(aNan.name, Comparison.Unordered, ConversionType.F32.format, a, a))
      context.emit(SetPredicate(bNan.name, Comparison.Unordered, ConversionType.F32.format, b, b))
      context.emit(PredicateBinary(PredicateOperation.And, bothNan.name, aNan.name, bNan.name))
      val value = context.temp()
      context.emit(FloatBinary(
        if (isMin) FloatOperation.Minimum else FloatOperation.Maximum,
        value.name,
        a,
        b,
        32
      ))
      context.emit(Select(value.name, a, value, bNan.name, 32))
      context.emit(Select(value.name, b, value, aNan.name, 32))
      val rounded = context.temp()
      context.emit(Convert(
        rounded.name,
        value,
        ConversionType.F32,
        ConversionType.F16,
        Some(ConversionRounding.RN)
      ))
      val canonical = context.temp()
      context.emit(Select(canonical.name, Value.Immediate(0x7fffL), rounded, bothNan.name, 32))
      val masked = context.temp()
      context.emit(IntegerBinary(
        IntegerOperation.BitwiseAnd,
        masked.name,
        canonical,
        Value.Immediate(HalfMask),
        32
      ))
      masked
    }
    val high = context.temp()
    context.emit(IntegerBinary(IntegerOperation.ShiftLeft, high.name, halves(1), Value.Immediate(16L), 32))
    val packed = context.temp()
    context.emit(IntegerBinary(IntegerOperation.BitwiseOr, packed.name, halves.head, high, 32))
    context.store(operands.head, packed)
    true
  }

  private def lowerScalarToHalf(context: VMCompilationContext, operands: Vec[Operand]): Boolean = {
    requireSm(context, 75, "HADD2 scalar-float conversion is supported only on sm_75")
    if (context.instruction.guard.nonEmpty)
      unsupported("HADD2 scalar-float conversion is supported only as an unpredicated sm_75 form")
    requireSize(operands, 3, "HADD2 scalar-float form requires destination and two sources")
    if (!bareGeneralRegister(operands.head)) unsupported("HADD2 scalar-float destination must be a register")
    val scalar = if (operandSelectors(operands(1)) == Vec(OperandSelector.F32) && VMOperand.isRegister(operands(1)))
      operandBase(operands(1))
    else unsupported("HADD2 scalar-float source must be an unmodified .F32 register")
    if (!isNegativeZeroHalf(operands(2)))
      unsupported("HADD2 scalar-float form requires the compiler-emitted -RZ.H0_H0 zero addend")
    val value     = context.floatSource(scalar)
    val converted = context.temp()
    context.emit(Convert(
      converted.name,
      value,
      ConversionType.F32,
      ConversionType.F16,
      Some(ConversionRounding.RN)
    ))
    val isNan = context.temp("q")
    context.emit(SetPredicate(isNan.name, Comparison.Unordered, ConversionType.F32.format, value, value))
    val canonical = context.temp()
    context.emit(Select(canonical.name, Value.Immediate(0x7fffL), converted, isNan.name, 32))
    val half = context.temp()
    context.emit(IntegerBinary(
      IntegerOperation.BitwiseAnd,
      half.name,
      canonical,
      Value.Immediate(HalfMask),
      32
    ))
    val upper = context.temp()
    context.emit(IntegerBinary(IntegerOperation.ShiftLeft, upper.name, half, Value.Immediate(16L), 32))
    val result = context.temp()
    context.emit(IntegerBinary(IntegerOperation.BitwiseOr, result.name, upper, half, 32))
    context.store(operands.head, result)
    true
  }

  private def lowerHalfToScalar(context: VMCompilationContext, operands: Vec[Operand]): Boolean = {
    if (context.instruction.modifiers != Vec(Modifier.`F32`))
      unsupported("unsupported HADD2 modifiers")
    requireSize(operands, 3, "HADD2.F32 requires destination and two sources")
    val selected = if (isAnyNegativeZero(operands(1))) operands(2)
    else if (isAnyNegativeZero(operands(2))) operands(1)
    else unsupported("HADD2.F32 supported forms require the compiler-emitted -RZ or -RZ.H0_H0 addend")
    val (_, shift) = selectedHalf(selected, "HADD2.F32")
    val input      = selectedBase(selected)
    val extracted  = context.temp()
    context.emit(BitFieldExtract(
      extracted.name,
      context.source(input),
      Value.Immediate(shift.toLong),
      Value.Immediate(16L),
      32,
      false
    ))
    val converted = context.temp()
    context.emit(Convert(converted.name, extracted, ConversionType.F16, ConversionType.F32, None))
    val magnitude = context.temp()
    context.emit(IntegerBinary(
      IntegerOperation.BitwiseAnd,
      magnitude.name,
      extracted,
      Value.Immediate(0x7fffL),
      32
    ))
    val isNan = context.temp("q")
    context.emit(SetPredicate(
      isNan.name,
      Comparison.GreaterThan,
      ConversionType.U32.format,
      magnitude,
      Value.Immediate(0x7c00L)
    ))
    val result = context.temp()
    context.emit(Select(result.name, Value.Immediate(0x7fffffffL), converted, isNan.name, 32))
    context.store(operands.head, result)
    true
  }

  private def lowerHalfMultiplyToScalar(context: VMCompilationContext, operands: Vec[Operand]): Boolean = {
    requireSm(context, 75, "HMUL2.F32 is supported only on sm_75")
    requireSize(operands, 3, "HMUL2.F32 requires destination and two sources")
    if (context.instruction.guard.nonEmpty || !bareGeneralRegister(operands.head))
      unsupported("HMUL2.F32 supports only unpredicated sm_75 register destinations")
    val left    = selectedHalfAsFloat(context, operands(1), "HMUL2.F32")
    val right   = selectedHalfAsFloat(context, operands(2), "HMUL2.F32")
    val product = context.temp()
    context.emit(FloatBinary(FloatOperation.Multiply, product.name, left, right, 32))
    val isNan = context.temp("q")
    context.emit(SetPredicate(
      isNan.name,
      Comparison.Unordered,
      ConversionType.F32.format,
      product,
      product
    ))
    val result = context.temp()
    context.emit(Select(result.name, Value.Immediate(0x7fffffffL), product, isNan.name, 32))
    context.store(operands.head, result)
    true
  }

  private def lowerFloatToHalf(context: VMCompilationContext, operands: Vec[Operand]): Boolean = {
    if (!Set(70, 75, 89)(context.sm)) unsupported("F2F.F16.F32 is not supported on this target")
    requireSize(operands, 2, "F2F.F16.F32 requires destination and source")
    if (!bareGeneralRegister(operands.head)) unsupported("F2F.F16.F32 destination must be a register")
    if (!bareGeneralRegister(operands(1)))
      unsupported("F2F.F16.F32 source must be a bare register")
    val input     = context.floatSource(operands(1))
    val converted = context.temp()
    context.emit(Convert(
      converted.name,
      input,
      ConversionType.F32,
      ConversionType.F16,
      Some(ConversionRounding.RN)
    ))
    val isNan = context.temp("q")
    context.emit(SetPredicate(isNan.name, Comparison.Unordered, ConversionType.F32.format, input, input))
    val canonical = context.temp()
    context.emit(Select(canonical.name, Value.Immediate(0x7fffL), converted, isNan.name, 32))
    val zeroExtended = context.temp()
    context.emit(IntegerBinary(
      IntegerOperation.BitwiseAnd,
      zeroExtended.name,
      canonical,
      Value.Immediate(HalfMask),
      32
    ))
    context.store(operands.head, zeroExtended)
    true
  }

  private def lowerPackHalves(
      context: VMCompilationContext,
      modifiers: Vec[Modifier],
      operands: Vec[Operand]
  ): Boolean = {
    if (modifiers != Vec(Modifier.`F16`, Modifier.`F32`, Modifier.`PACK_AB`) && modifiers != Vec(Modifier.`PACK_AB`))
      unsupported(s"unsupported F2FP modifiers ${modifiers.mkString(".")}")
    requireSize(operands, 3, "F2FP.PACK_AB requires destination and two sources")
    val low   = convertScalarHalf(context, context.floatSource(operands(2)))
    val high  = convertScalarHalf(context, context.floatSource(operands(1)))
    val upper = context.temp()
    context.emit(IntegerBinary(IntegerOperation.ShiftLeft, upper.name, high, Value.Immediate(16L), 32))
    val result = context.temp()
    context.emit(IntegerBinary(IntegerOperation.BitwiseOr, result.name, upper, low, 32))
    context.store(operands.head, result)
    true
  }

  private def convertScalarHalf(context: VMCompilationContext, value: Value): Value = {
    val converted = context.temp()
    context.emit(Convert(
      converted.name,
      value,
      ConversionType.F32,
      ConversionType.F16,
      Some(ConversionRounding.RN)
    ))
    val isNan = context.temp("q")
    context.emit(SetPredicate(isNan.name, Comparison.Unordered, ConversionType.F32.format, value, value))
    val canonical = context.temp()
    context.emit(Select(canonical.name, Value.Immediate(0x7fffL), converted, isNan.name, 32))
    val masked = context.temp()
    context.emit(IntegerBinary(
      IntegerOperation.BitwiseAnd,
      masked.name,
      canonical,
      Value.Immediate(HalfMask),
      32
    ))
    masked
  }

  private def packedOperand(context: VMCompilationContext, operand: Operand, opcode: String): Value =
    if (
      isNegativeZeroHalf(operand) &&
      (context.instruction.operation match { case _: SASSInstruction.Hadd2 => true; case _ => false })
    ) {
      val negativeZero = context.temp()
      context.emit(Move(negativeZero.name, Value.Immediate(0x80008000L), 32))
      negativeZero
    } else {
      val selected = operandSelectors(operand)
      if (selected.isEmpty) {
        if (!VMOperand.isRegister(operandBase(operand), allowZero = true))
          unsupported(s"unsupported $opcode half source")
        context.source(operand)
      } else {
        val (base, shift) = selectedHalf(operand, opcode)
        val input         = context.source(base)
        val low           = context.temp()
        context.emit(BitFieldExtract(
          low.name,
          input,
          Value.Immediate(shift.toLong),
          Value.Immediate(16L),
          32,
          false
        ))
        val high = context.temp()
        context.emit(BitFieldExtract(
          high.name,
          input,
          Value.Immediate(shift.toLong),
          Value.Immediate(16L),
          32,
          false
        ))
        val upper = context.temp()
        context.emit(IntegerBinary(IntegerOperation.ShiftLeft, upper.name, high, Value.Immediate(16L), 32))
        val result = context.temp()
        context.emit(IntegerBinary(IntegerOperation.BitwiseOr, result.name, low, upper, 32))
        result
      }
    }

  private def canonicalizeNaNs(context: VMCompilationContext, packed: Value): Value = {
    val halves = Vec(0, 16).map { offset =>
      val bits = context.temp()
      context.emit(BitFieldExtract(
        bits.name,
        packed,
        Value.Immediate(offset.toLong),
        Value.Immediate(16L),
        32,
        false
      ))
      val exponent = context.temp()
      context.emit(IntegerBinary(
        IntegerOperation.BitwiseAnd,
        exponent.name,
        bits,
        Value.Immediate(0x7c00L),
        32
      ))
      val fraction = context.temp()
      context.emit(IntegerBinary(
        IntegerOperation.BitwiseAnd,
        fraction.name,
        bits,
        Value.Immediate(0x03ffL),
        32
      ))
      val special = context.temp("q")
      val payload = context.temp("q")
      val isNan   = context.temp("q")
      context.emit(SetPredicate(
        special.name,
        Comparison.Equal,
        ConversionType.U32.format,
        exponent,
        Value.Immediate(0x7c00L)
      ))
      context.emit(SetPredicate(
        payload.name,
        Comparison.NotEqual,
        ConversionType.U32.format,
        fraction,
        Value.Immediate(0L)
      ))
      context.emit(PredicateBinary(PredicateOperation.And, isNan.name, special.name, payload.name))
      val canonical = context.temp()
      context.emit(Select(canonical.name, Value.Immediate(0x7fffL), bits, isNan.name, 32))
      canonical
    }
    val high = context.temp()
    context.emit(IntegerBinary(IntegerOperation.ShiftLeft, high.name, halves(1), Value.Immediate(16L), 32))
    val result = context.temp()
    context.emit(IntegerBinary(IntegerOperation.BitwiseOr, result.name, halves.head, high, 32))
    result
  }

  private def halfAsFloat(context: VMCompilationContext, operand: Operand, offset: Int): Value = {
    val packed = packedOperand(context, operand, "HSETP2")
    halfAsFloat(context, packed, offset)
  }

  private def halfAsFloat(context: VMCompilationContext, packed: Value, offset: Int): Value = {
    val bits = context.temp()
    context.emit(BitFieldExtract(
      bits.name,
      packed,
      Value.Immediate(offset.toLong),
      Value.Immediate(16L),
      32,
      false
    ))
    val value = context.temp()
    context.emit(Convert(value.name, bits, ConversionType.F16, ConversionType.F32, None))
    value
  }

  private def selectedHalf(operand: Operand, opcode: String): (Operand, Int) = {
    val selectors = operandSelectors(operand)
    val shift     = selectors match {
      case Vec(OperandSelector.H0_H0)                        => 0
      case Vec(OperandSelector.H1_H1)                        => 16
      case Vec(OperandSelector.H0_H0, OperandSelector.H0_H0) => 0
      case Vec(OperandSelector.H1_H1, OperandSelector.H1_H1) => 16
      case _ => unsupported(s"$opcode supports only duplicated H0_H0 or H1_H1 selectors")
    }
    val base = selectedBase(operand)
    if (!VMOperand.isRegister(base, allowZero = true)) unsupported(s"unsupported $opcode selected-half source")
    base -> shift
  }

  private def selectedBase(operand: Operand): Operand.Register = operandBase(operand)

  private def halfConstant(operand: Operand): Long = {
    val bits = operand match {
      case Operand.Integer(value) => IEEE754.floatToBinary16(value.toFloat).raw & HalfMask
      case Operand.Floating(value, _, negativeZero) if value == BigDecimal(0) && negativeZero => 0x8000L
      case Operand.Floating(value, _, _) => IEEE754.floatToBinary16(value.toFloat).raw & HalfMask
      case Operand.FloatingSymbol(FloatingConstant.`INF` | FloatingConstant.`+INF`) => 0x7c00L
      case Operand.FloatingSymbol(FloatingConstant.`-INF`)                          => 0xfc00L
      case Operand.FloatingSymbol(
            FloatingConstant.`QNAN` | FloatingConstant.`+QNAN` | FloatingConstant.`-QNAN` | FloatingConstant.`SNAN` | FloatingConstant.`+SNAN` | FloatingConstant.`-SNAN`
          ) => 0x7fffL
      case Operand.FloatingBits(_, width, bits) if width.raw == 16 => bits.toLong & HalfMask
      case Operand.FloatingBits(_, width, bits) if width.raw == 32 =>
        IEEE754.floatToBinary16(java.lang.Float.intBitsToFloat(bits.toInt)).raw & HalfMask
      case Operand.FloatingBits(_, width, bits) if width.raw == 64 =>
        IEEE754.doubleToBinary16(java.lang.Double.longBitsToDouble(bits.toLong)).raw & HalfMask
      case Operand.Unary(inner, true, false, false) => inner match {
          case Operand.FloatingSymbol(
                FloatingConstant.`QNAN` | FloatingConstant.`+QNAN` | FloatingConstant.`-QNAN` | FloatingConstant.`SNAN` | FloatingConstant.`+SNAN` | FloatingConstant.`-SNAN` | FloatingConstant.`NAN`
              ) => 0x7fffL
          case _ => halfConstant(inner) ^ 0x8000L
        }
      case _ => unsupported("HFMA2.MMA constant form requires floating constants")
    }
    if ((bits & 0x7c00L) == 0x7c00L && (bits & 0x03ffL) != 0) 0x7fffL else bits
  }

  @annotation.tailrec
  private def isUnitOne(operand: Operand): Boolean = operand match {
    case Operand.Integer(value)                                  => value == 1
    case Operand.Floating(value, _, _)                           => value == BigDecimal(1)
    case Operand.FloatingBits(_, width, bits) if width.raw == 16 => bits == 0x3c00
    case Operand.FloatingBits(_, width, bits) if width.raw == 32 => bits == 0x3f800000
    case Operand.FloatingBits(_, width, bits) if width.raw == 64 => bits == BigInt("3ff0000000000000", 16)
    case Operand.Unary(inner, false, false, false)               => isUnitOne(inner)
    case _                                                       => false
  }

  private def predicateDestination(operand: Operand): Option[Value.Register] = operand match {
    case Operand.Predicate(None, false, false)        => None
    case Operand.Predicate(Some(index), false, false) =>
      Some(Value.Register(s"%P$index"))
    case _ => unsupported("HSETP2 result must be a predicate register or PT")
  }

  private def writePredicate(context: VMCompilationContext, destination: Value.Register, value: Value.Register): Unit =
    context.emit(PredicateBinary(PredicateOperation.Or, destination.name, value.name, "%false"))

  private def supportedHalfSource(operand: Operand): Boolean =
    VMOperand.isRegister(operand, allowZero = true) ||
      (operandSelectors(operand) match {
        case Vec(OperandSelector.H0_H0) | Vec(OperandSelector.H1_H1) =>
          VMOperand.isRegister(operandBase(operand), allowZero = true)
        case _ => false
      })

  private def isBareGeneralOrZero(operand: Operand): Boolean =
    VMOperand.isRegister(operand, allowZero = true) && operandSelectors(operand).isEmpty

  private def bareGeneralRegister(operand: Operand): Boolean =
    VMOperand.isRegister(operand) && operandSelectors(operand).isEmpty

  private def isNegativeZeroHalf(operand: Operand): Boolean = operand match {
    case Operand.Unary(inner, true, false, false) => VMOperand.isZero(inner) &&
      operandSelectors(inner) == Vec(OperandSelector.H0_H0)
    case _ => false
  }

  private def isNegativeZero(operand: Operand): Boolean = operand match {
    case Operand.Unary(inner, true, false, false) => VMOperand.isZero(inner) && operandSelectors(inner).isEmpty
    case _                                        => false
  }

  private def isAnyNegativeZero(operand: Operand): Boolean =
    isNegativeZero(operand) || isNegativeZeroHalf(operand)

  private def selectedHalfAsFloat(context: VMCompilationContext, operand: Operand, opcode: String): Value = {
    val (base, shift) = selectedHalf(operand, opcode)
    val bits          = context.temp()
    context.emit(BitFieldExtract(
      bits.name,
      context.source(base),
      Value.Immediate(shift.toLong),
      Value.Immediate(16L),
      32,
      false
    ))
    val value = context.temp()
    context.emit(Convert(value.name, bits, ConversionType.F16, ConversionType.F32, None))
    value
  }

  private def operandSelectors(operand: Operand): Vec[OperandSelector] =
    VMOperand.selectors(VMOperand.without(operand, OperandSelector.`reuse`))

  @annotation.tailrec
  private def operandBase(operand: Operand): Operand.Register =
    VMOperand.without(operand, OperandSelector.`reuse`) match {
      case Operand.Qualified(inner, _) => operandBase(inner)
      case value: Operand.Register     => value
      case _                           => unsupported("expected a physical register operand")
    }

  private def requireSize(operands: Vec[Operand], size: Int, message: String): Unit =
    if (operands.size != size) unsupported(message)

  private def requireSm(context: VMCompilationContext, sm: Int, message: String): Unit =
    if (context.sm != sm) unsupported(message)

  private def unsupported(detail: String): Nothing = throw IllegalArgumentException(detail)
}
