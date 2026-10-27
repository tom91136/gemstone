package net.kurobako.gemstone.isa.sass

import ModifierLowering.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.isa.nvidia.VMIR.Instruction.*
import net.kurobako.gemstone.isa.sass.Instruction.*
import net.kurobako.gemstone.isa.sass.Instruction as SASSInstruction
import scala.annotation.tailrec

/** Packed binary16 and bfloat16 arithmetic, comparisons and extrema, and binary32 arithmetic on half operands.
  *
  * A packed source is a register, uniform register or constant read whole (`H1_H0`), with one half in both (`H0_H0`,
  * `H1_H1`), with the low half above the negated high half (`H0_NH1`), or as a binary32 value rounded toward zero to
  * both halves (`F32`), which ptxas uses for `cvt.rz.f16.f32` before sm_80; or two immediates, the upper half first.
  * Negation and absolute value apply to each half.
  */
private object VMHalfCompiler {
  private val signs               = 0x80008000L
  private val arithmeticModifiers = Set(
    Modifier.F32,
    Modifier.F16_V2,
    Modifier.SAT,
    Modifier.FTZ,
    Modifier.BF16_V2,
    Modifier.E8M7_V2,
    Modifier.MRG_H0,
    Modifier.MRG_H1
  )
  private val fusedModifiers = Set(
    Modifier.MMA,
    Modifier.SAT,
    Modifier.RELU,
    Modifier.FTZ,
    Modifier.F16_V2,
    Modifier.BF16_V2,
    Modifier.E8M7_V2,
    Modifier.MRG_H0,
    Modifier.MRG_H1
  )
  private val extremumModifiers = Set(Modifier.NAN, Modifier.FTZ, Modifier.F16_V2, Modifier.BF16_V2, Modifier.E8M7_V2)
  private val mixedModifiers    = Set(Modifier.F16, Modifier.BF16, Modifier.SAT) ++ Set(
    Modifier.RN,
    Modifier.RM,
    Modifier.RP,
    Modifier.RZ
  )

  def lower(using context: VMCompilationContext): Boolean = {
    import context.*
    (operation.form: @unchecked) match {
      case _: Form.Hadd2 | _: Form.Hadd232i =>
        arithmetic(FloatOperation.Add)
        true
      case _: Form.Hmul2 | _: Form.Hmul232i =>
        arithmetic(FloatOperation.Multiply)
        true
      case _: Form.Hfma2 | _: Form.Hfma232i =>
        fusedMultiplyAdd()
        true
      case _: Form.Hset2 =>
        set()
        true
      case _: Form.Hsetp2 =>
        setPredicates()
        true
      case _: Form.Hmnmx2 | _: Form.Vhmnmx =>
        extremum()
        true
      case _: Form.Fhadd | _: Form.Ufhadd =>
        mixed(MixedForm.Add)
        true
      case _: Form.Fhfma | _: Form.Ufhfma =>
        mixed(MixedForm.FusedMultiply)
        true
      case _ => false
    }
  }

  /** An operand's base, its selectors but the reuse hint, and whether each half is negated after its absolute value. */
  private case class Half(base: Operand, selectors: Vec[OperandSelector], negative: Boolean, absolute: Boolean)

  @tailrec
  private def peel(
      operand: Operand,
      selectors: Vec[OperandSelector] = Vec.empty,
      negative: Boolean = false,
      absolute: Boolean = false
  ): Half = operand match {
    case Operand.Qualified(inner, suffixes) =>
      peel(inner, suffixes.filterNot(_ == OperandSelector.reuse) ++ selectors, negative, absolute)
    // a negation inside an absolute value has no effect
    case Operand.Unary(inner, minus, false, magnitude) =>
      peel(inner, selectors, if (absolute) negative else negative != minus, absolute || magnitude)
    case other => Half(other, selectors, negative, absolute)
  }

  private def immediate(operand: Operand): Boolean = peel(operand).base match {
    case Operand.Integer(_) | Operand.Bits(_, _) | Operand.FloatingSymbol(_) => true
    case Operand.Floating(_, _, _) | Operand.FloatingBits(_, _, _)           => true
    case _                                                                   => false
  }

  private def format(bfloat16: Boolean): NumericFormat = if (bfloat16) NumericFormat.BF16 else NumericFormat.IEEE(16)

  /** Whether the instruction computes on bfloat16 pairs. */
  private def bfloat16(using context: VMCompilationContext): Boolean =
    context.modifiers.exists(PartialFunction.cond(_) { case Modifier.BF16_V2 | Modifier.E8M7_V2 => true })

  /** `count` packed sources from operand `from` on, and the index of the operand after them. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def sources(from: Int, count: Int, bfloat16: Boolean, operands: Vec[Operand])(using
      context: VMCompilationContext
  ): (values: Vec[Value], next: Int) = {
    import context.{operands as _, *}
    val found = Vec.newBuilder[Value]
    var index = from
    for (_ <- 0 until count) {
      val operand = operands.lift(index).getOrElse(unsupported(s"${operation.mnemonic.raw} has too few sources"))
      if (immediate(operand)) {
        val low = operands.lift(index + 1).filter(immediate(_))
          .getOrElse(unsupported(s"${operation.mnemonic.raw} immediates are pairs of halves"))
        found += Value.Immediate((halfConstant(operand, bfloat16) << 16) | halfConstant(low, bfloat16))
        index += 2
      } else {
        found += packed(operand, bfloat16)
        index += 1
      }
    }
    (found.result(), index)
  }

  private def packed(operand: Operand, bfloat16: Boolean)(using context: VMCompilationContext): Value = {
    import context.*
    val Half(base, selectors, negative, absolute) = peel(operand)
    base match {
      case Operand.Register(RegisterBank.R | RegisterBank.UR, _) | Operand.ConstantBank(_, _) => ()
      case _ => unsupported(s"unsupported ${operation.mnemonic.raw} half source")
    }
    val raw      = source(base)
    val selected = base match {
      // a constant bank holds a float32, which the half instructions narrow to a half
      case Operand.ConstantBank(_, _) =>
        val narrowed = convert(raw, NumericFormat.IEEE(32), format(bfloat16), Some(ConversionRounding.RN))
        halves(narrowed, narrowed)
      case _ => selectors match {
          case Vec() | Vec(OperandSelector.H1_H0) => raw
          case Vec(OperandSelector.H0_H0)         => duplicated(raw, 0)
          case Vec(OperandSelector.H1_H1)         => duplicated(raw, 16)
          case Vec(OperandSelector.H0_NH1)        =>
            flipped(halves(lowHalf(raw), highHalf(raw)), 0x8000L)
          case Vec(OperandSelector.F32) =>
            val narrowed = convert(raw, NumericFormat.IEEE(32), format(bfloat16), Some(ConversionRounding.RZ))
            halves(narrowed, narrowed)
          case _ => unsupported(s"unsupported ${operation.mnemonic.raw} half selector ${selectors.mkString(".")}")
        }
    }
    flipped(if (absolute) masked(selected, ~signs & 0xffffffffL) else selected, if (negative) signs else 0L)
  }

  private def masked(value: Value, mask: Long)(using context: VMCompilationContext): Value = value match {
    case Value.Immediate(bits) => Value.Immediate(bits & mask)
    case _                     => context.binary(IntegerOperation.BitwiseAnd, value, Value.Immediate(mask))
  }

  private def flipped(value: Value, sign: Long)(using context: VMCompilationContext): Value = value match {
    case _ if sign == 0L       => value
    case Value.Immediate(bits) => Value.Immediate(bits ^ sign)
    case _                     => context.binary(IntegerOperation.BitwiseXor, value, Value.Immediate(sign))
  }

  private def lowHalf(value: Value)(using VMCompilationContext): Value = value match {
    case Value.Immediate(bits) => Value.Immediate(bits & 0xffffL)
    case _                     => extract(value, 0)
  }

  private def highHalf(value: Value)(using VMCompilationContext): Value = value match {
    case Value.Immediate(bits) => Value.Immediate((bits >>> 16) & 0xffffL)
    case _                     => extract(value, 16)
  }

  private def duplicated(value: Value, shift: Int in (0, 16))(using VMCompilationContext): Value = value match {
    case Value.Immediate(bits) =>
      val half = (bits >>> shift) & 0xffffL
      Value.Immediate((half << 16) | half)
    case _ =>
      val half = extract(value, shift)
      halves(half, half)
  }

  /** The 16-bit half of `value` from bit `shift`. */
  def extract(value: Value, shift: Int in (0, 16))(using context: VMCompilationContext): Value.Register = {
    val result = context.temp()
    context.emit(BitFieldExtract(result.slot, value, Value.Immediate(shift.toLong), Value.Immediate(16L), 32, false))
    result
  }

  /** `high` in the upper and `low` in the lower half of one register. */
  def halves(high: Value, low: Value)(using context: VMCompilationContext): Value = (high, low) match {
    case (Value.Immediate(upper), Value.Immediate(lower)) =>
      Value.Immediate(((upper & 0xffffL) << 16) | (lower & 0xffffL))
    case _ =>
      context.binary(
        IntegerOperation.BitwiseOr,
        context.binary(IntegerOperation.ShiftLeft, high, Value.Immediate(16L)),
        low
      )
  }

  /** The destination's other half kept under `MRG_H0` or `MRG_H1`. */
  private def merged(result: Value)(using context: VMCompilationContext): Value = {
    import context.*
    modifiers.collectFirst {
      case Modifier.MRG_H0 => 0xffffL
      case Modifier.MRG_H1 => 0xffff0000L
    }.fold(result)(kept =>
      binary(
        IntegerOperation.BitwiseOr,
        masked(result, kept),
        masked(source(OperandQueries.base(operands.head)), ~kept & 0xffffffffL)
      )
    )
  }

  private def arithmetic(kind: FloatOperation)(using context: VMCompilationContext): Unit = {
    import context.*
    requireModifiers(arithmeticModifiers)
    val narrow  = bfloat16
    val control =
      FloatControl(flushSubnormal = modifiers.contains(Modifier.FTZ), saturate = modifiers.contains(Modifier.SAT))
    if (modifiers.contains(Modifier.F32)) {
      if (modifiers.exists(PartialFunction.cond(_) { case Modifier.MRG_H0 | Modifier.MRG_H1 => true }))
        unsupported("a binary32 result cannot merge")
      requireOperands(3, s"${operation.mnemonic.raw}.F32 requires a destination and two sources")
      scalar(kind, control, narrow)
    } else {
      val (values, next) = sources(1, 2, narrow, operands)
      if (next != operands.size) unsupported(s"${operation.mnemonic.raw} requires a destination and two sources")
      val result = temp()
      emit(FloatBinaryPacked(
        kind,
        result.slot,
        values(0),
        values(1),
        if (narrow) PackedFloatFormat.BF16x2 else PackedFloatFormat.F16x2,
        control
      ))
      store(operands.head, merged(result))
    }
  }

  /** The `.F32` form: the operation of one selected half of each source, whose result the destination holds as a
    * binary32 value. A float32 source is narrowed to a half toward zero.
    *
    * The Pascal and Volta units compute the operation in binary16 and widen the rounded result; the Turing and later
    * units compute it in binary32.
    */
  private def scalar(kind: FloatOperation, control: FloatControl, bfloat16: Boolean)(using
      context: VMCompilationContext
  ): Unit = {
    import context.*
    def half(operand: Operand): Value = {
      val Half(base, selectors, negative, absolute) = peel(operand)
      val selected                                  = selectors match {
        // a source read whole contributes its low half
        case Vec() | Vec(OperandSelector.H1_H0) | Vec(OperandSelector.H0_H0) =>
          base match {
            case Operand.Register(RegisterBank.R | RegisterBank.UR, _) | Operand.ConstantBank(_, _) =>
              lowHalf(source(base))
            case _ if immediate(base) => Value.Immediate(halfConstant(base, bfloat16))
            case _                    => unsupported(s"unsupported ${operation.mnemonic.raw}.F32 source")
          }
        case Vec(OperandSelector.H1_H1) =>
          base match {
            case Operand.Register(RegisterBank.R | RegisterBank.UR, _) | Operand.ConstantBank(_, _) =>
              highHalf(source(base))
            case _ => unsupported(s"unsupported ${operation.mnemonic.raw}.F32 source")
          }
        // a float32 source is narrowed to a half toward zero
        case Vec(OperandSelector.F32) =>
          convert(floatSource(base), NumericFormat.IEEE(32), format(bfloat16), Some(ConversionRounding.RZ))
        case _ => unsupported(s"${operation.mnemonic.raw}.F32 sources select one half with H0_H0 or H1_H1")
      }
      flipped(if (absolute) masked(selected, 0x7fffL) else selected, if (negative) 0x8000L else 0L)
    }
    val left  = half(operands(1))
    val right = half(operands(2))
    if (sm >= 75) {
      // the binary32 units compute the operation of the widened halves
      val result = temp()
      emit(FloatBinary(
        kind,
        result.slot,
        convert(left, format(bfloat16), NumericFormat.IEEE(32)),
        convert(right, format(bfloat16), NumericFormat.IEEE(32)),
        32,
        control
      ))
      store(operands.head, result)
    } else {
      // the binary16 units compute the operation of the halves and widen the rounded result
      val result = temp()
      emit(FloatBinaryPacked(
        kind,
        result.slot,
        halves(left, left),
        halves(right, right),
        if (bfloat16) PackedFloatFormat.BF16x2 else PackedFloatFormat.F16x2,
        control
      ))
      store(operands.head, convert(lowHalf(result), format(bfloat16), NumericFormat.IEEE(32)))
    }
  }

  private def fusedMultiplyAdd()(using context: VMCompilationContext): Unit = {
    import context.*
    val mmaUnitScale = modifiers == Vec(Modifier.MMA) && operands.size == 5 &&
      bareRegisterOrZero(operands(1)) &&
      bareRegisterOrZero(operands(4)) && unitOne(operands(2)) && unitOne(operands(3))
    if (mmaUnitScale) {
      val sum = temp()
      emit(FloatBinaryPacked(
        FloatOperation.Add,
        sum.slot,
        source(operands(1)),
        source(operands(4)),
        PackedFloatFormat.F16x2
      ))
      store(operands.head, sum)
    } else if (
      (modifiers.isEmpty || modifiers == Vec(Modifier.MMA)) && operands.size == 5 && negativeZero(operands(1)) &&
      OperandQueries.isZero(operands(2))
    ) store(
      operands.head,
      Value.Immediate((halfConstant(operands(3), bfloat16 = false) << 16) | halfConstant(operands(4), bfloat16 = false))
    )
    else {
      requireModifiers(fusedModifiers)
      val narrow = bfloat16
      // the legacy two-source form accumulates into its destination
      val inputs =
        if (OperandQueries.is[Form.Hfma232i](operation) && operands.size == 4) operands :+ operands.head
        else operands
      val (values, next) = sources(1, 3, narrow, inputs)
      if (next != inputs.size) unsupported(s"${operation.mnemonic.raw} requires a destination and three sources")
      val result = temp()
      val format = if (narrow) PackedFloatFormat.BF16x2 else PackedFloatFormat.F16x2
      // a product with one flushes subnormal halves to zero
      def flushed(value: Value): Value =
        if (!modifiers.contains(Modifier.FTZ)) value
        else {
          val flush = temp()
          emit(FloatBinaryPacked(
            FloatOperation.Multiply,
            flush.slot,
            value,
            Value.Immediate(if (narrow) 0x3f803f80L else 0x3c003c00L),
            format,
            FloatControl(flushSubnormal = true)
          ))
          flush
        }
      // The MMA unit computes the same fused product.
      emit(FusedMultiplyAddPacked(
        destination = result.slot,
        left = flushed(values(0)),
        right = flushed(values(1)),
        addend = flushed(values(2)),
        bf16 = narrow,
        saturate = modifiers.contains(Modifier.SAT)
      ))
      if (modifiers.contains(Modifier.FTZ)) emit(Move(result.slot, flushed(result), 32))
      if (modifiers.contains(Modifier.RELU))
        emit(FloatBinaryPacked(
          FloatOperation.Maximum,
          result.slot,
          result,
          Value.Immediate(0L),
          format
        ))
      store(operands.head, merged(result))
    }
  }

  /** The comparison each half makes, `F` and `T` holding never and always. */
  private def comparison(left: Value, right: Value, bfloat16: Boolean)(using
      context: VMCompilationContext
  ): Value.Predicate = {
    import context.*
    val relations = comparisons ++ Set(Modifier.NUM, Modifier.NAN)
    modifiers.find(relations ++ Set(Modifier.F, Modifier.T)) match {
      case Some(Modifier.F) => Value.Predicate(VMRegisters.Predicate.False.slot)
      case Some(Modifier.T) => Value.Predicate(VMRegisters.Predicate.True.slot)
      case Some(relation)   =>
        val result = predicateTemp()
        emit(SetPredicate(
          result.slot,
          relation.comparisonCode,
          format(bfloat16),
          left,
          right,
          modifiers.contains(Modifier.FTZ)
        ))
        result
      case None => unsupported(s"${operation.mnemonic.raw} requires a comparison")
    }
  }

  private val combiners             = Set(Modifier.AND, Modifier.OR, Modifier.XOR)
  private val setPredicateModifiers = comparisons ++ combiners ++
    Set(Modifier.NUM, Modifier.NAN, Modifier.F, Modifier.T, Modifier.FTZ, Modifier.H_AND, Modifier.BF16_V2)
  private val setModifiers = comparisons ++ combiners ++
    Set(Modifier.NUM, Modifier.NAN, Modifier.F, Modifier.T, Modifier.FTZ, Modifier.BF, Modifier.BM, Modifier.BF16_V2)

  private def setPredicates()(using context: VMCompilationContext): Unit = {
    import context.*
    requireModifiers(setPredicateModifiers)
    val narrow         = bfloat16
    val (values, next) = sources(2, 2, narrow, operands)
    val input          = operands.drop(next) match {
      case Vec() if !modifiers.exists(combiners) => None
      case Vec(predicate)
          if modifiers.exists(combiners) && OperandQueries.isPredicateAllowingNegated(predicate) =>
        Some(saved(predicateSource(predicate)))
      case _ => unsupported("HSETP2 requires two predicate results, two sources and a predicate when it combines")
    }
    def half(shift: Int in (0, 16)): Value.Predicate = comparison(
      if (shift == 0) lowHalf(values(0)) else highHalf(values(0)),
      if (shift == 0) lowHalf(values(1)) else highHalf(values(1)),
      narrow
    )
    val low             = half(0)
    val high            = half(16)
    val (first, second) =
      if (modifiers.contains(Modifier.H_AND)) {
        val both = combine(PredicateOperation.And, low, high)
        (both, invert(both))
      } else (low, high)
    def write(target: Operand, value: Value.Predicate): Unit = input match {
      case Some(held) =>
        if (!OperandQueries.isAlwaysPredicate(target))
          emit(PredicateBinary(
            modifiers.find(combiners).get.predicateOperation,
            predicate(target).slot,
            value.slot,
            held.slot
          ))
      case None => writePredicate(target, value)
    }
    // both results are computed before either is written, which may alias a source predicate
    val results = Vec(saved(first), saved(second))
    write(operands.head, results(0))
    write(operands(1), results(1))
  }

  private def set()(using context: VMCompilationContext): Unit = {
    import context.*
    requireModifiers(setModifiers)
    val narrow         = bfloat16
    val (values, next) = sources(1, 2, narrow, operands)
    val truth          = if (!modifiers.contains(Modifier.BF)) 0xffffL else if (narrow) 0x3f80L else 0x3c00L
    val both           = Value.Immediate(truth | (truth << 16))
    val result         = modifiers.find(PartialFunction.cond(_) { case Modifier.F | Modifier.T => true }) match {
      case Some(Modifier.F) => Value.Immediate(0L)
      case Some(_)          => both
      case None             =>
        val relation = modifiers.find(comparisons ++ Set(Modifier.NUM, Modifier.NAN))
          .getOrElse(unsupported("HSET2 requires a comparison"))
        val result = temp()
        emit(CompareSet(
          result.slot,
          relation.comparisonCode,
          format(narrow),
          values(0),
          values(1),
          truth,
          packed = true,
          flushSubnormal = modifiers.contains(Modifier.FTZ)
        ))
        result
    }
    store(
      operands.head,
      (operands.drop(next), modifiers.find(combiners)) match {
        case (Vec(), None)                                                                            => result
        case (Vec(predicate), Some(Modifier.AND)) if predicate == OperandQueries.True                 => result
        case (Vec(predicate), Some(combiner)) if OperandQueries.isPredicateAllowingNegated(predicate) =>
          val holds = predicateSource(predicate)
          combiner match {
            case Modifier.AND => select(result, Value.Immediate(0L), holds)
            case Modifier.OR  => select(both, result, holds)
            case _            => select(binary(IntegerOperation.BitwiseXor, result, both), result, holds)
          }
        case _ => unsupported("HSET2 requires a destination, two sources and a predicate when it combines")
      }
    )
  }

  private def extremum()(using context: VMCompilationContext): Unit = {
    import context.*
    requireModifiers(extremumModifiers)
    val narrow         = bfloat16
    val count          = if (OperandQueries.is[Form.Vhmnmx](operation)) 3 else 2
    val (values, next) = sources(1, count, narrow, operands)
    if (next != operands.size - 1)
      unsupported(s"${operation.mnemonic.raw} requires destination, $count sources, and a predicate")
    val nan                               = modifiers.contains(Modifier.NAN)
    val format                            = if (narrow) PackedFloatFormat.BF16x2 else PackedFloatFormat.F16x2
    val flush                             = FloatControl(flushSubnormal = modifiers.contains(Modifier.FTZ))
    def extremum(minimum: Boolean): Value = values.tail.foldLeft(values.head) { (left, right) =>
      val result = temp()
      emit(FloatBinaryPacked(
        if (minimum) { if (nan) FloatOperation.Minimum else FloatOperation.MinimumNumber }
        else if (nan) FloatOperation.Maximum
        else FloatOperation.MaximumNumber,
        result.slot,
        left,
        right,
        format,
        flush
      ))
      result
    }
    // The selector picks the minimum where it holds and the maximum otherwise.
    store(
      operands.head,
      operands.last match {
        case Operand.Predicate(None, _, negated) => extremum(minimum = !negated)
        case selector                            => select(extremum(true), extremum(false), predicateSource(selector))
      }
    )
  }

  /** `FHADD` and `FHFMA`: a binary32 sum, or a product of two halves plus a binary32 addend, of one half selected with
    * `H0` or `H1` and rounded once.
    */
  private enum MixedForm derives CanEqual { case Add, FusedMultiply }

  private def mixed(form: MixedForm)(using context: VMCompilationContext): Unit = {
    import context.*
    val fused = form == MixedForm.FusedMultiply
    requireModifiers(mixedModifiers)
    requireOperands(if (fused) 4 else 3, s"${operation.mnemonic.raw} requires a destination and its sources")
    val narrow   = modifiers.contains(Modifier.BF16)
    val rounding = modifiers.find(PartialFunction.cond(_) {
      case Modifier.RN | Modifier.RM | Modifier.RP | Modifier.RZ => true
    })
      .getOrElse(Modifier.RN).conversionRounding
    def half(operand: Operand): Value = convert(scalarHalf(operand, narrow), format(narrow), NumericFormat.IEEE(32))
    val addend                        = floatSource(operands.last)
    val result                        = temp()
    emit(FusedMultiplyAdd(
      result.slot,
      half(operands(1)),
      if (fused) half(operands(2)) else Value.Immediate(0x3f800000L),
      addend,
      32,
      FloatControl(saturate = modifiers.contains(Modifier.SAT)),
      rounding
    ))
    store(operands.head, result)
  }

  /** One binary16 or bfloat16 value in the low half: the low half of a register or constant, or its high half under
    * `H1`, or an immediate, with negation and absolute value applied to its sign.
    */
  def scalarHalf(operand: Operand, bfloat16: Boolean)(using context: VMCompilationContext): Value = {
    import context.*
    val Half(base, selectors, negative, absolute) = peel(operand)
    val selected                                  = base match {
      case Operand.Register(RegisterBank.R | RegisterBank.UR, _) | Operand.ConstantBank(_, _) =>
        selectors match {
          case Vec() | Vec(OperandSelector.H0) => lowHalf(source(base))
          case Vec(OperandSelector.H1)         => highHalf(source(base))
          case _                               => unsupported(s"${operation.mnemonic.raw} selects a half with H0 or H1")
        }
      case _ if immediate(base) && selectors.isEmpty => Value.Immediate(halfConstant(base, bfloat16))
      case _ => unsupported(s"unsupported ${operation.mnemonic.raw} half source")
    }
    flipped(if (absolute) masked(selected, 0x7fffL) else selected, if (negative) 0x8000L else 0L)
  }

  /** A copy that survives a destination aliasing the source predicate. */
  private def saved(value: Value.Predicate)(using context: VMCompilationContext): Value.Predicate =
    if (value.slot == VMRegisters.Predicate.True.slot || value.slot == VMRegisters.Predicate.False.slot) value
    else {
      val copy = context.predicateTemp()
      context.copyPredicate(copy, value)
      copy
    }

  def halfConstant(operand: Operand, bfloat16: Boolean)(using context: VMCompilationContext): Long =
    if (!bfloat16) halfConstant(operand)
    else
      operand match {
        case Operand.Integer(value) => IEEE754.doubleToBfloat16(value.toDouble).toLong & 0xffffL
        case Operand.Floating(value, _, negativeZero) if value == BigDecimal(0) && negativeZero => 0x8000L
        case Operand.Floating(value, _, _)           => IEEE754.doubleToBfloat16(value.toDouble).toLong & 0xffffL
        case Operand.FloatingBits(_, width, encoded) =>
          IEEE754.doubleToBfloat16(IEEE754.toDouble(encoded.toLong, width)).toLong & 0xffffL
        case Operand.FloatingSymbol(FloatingConstant.INF)       => 0x7f80L
        case Operand.FloatingSymbol(constant) if constant.isNaN => 0x7fffL
        case Operand.Unary(inner, true, false, false)           => halfConstant(inner, bfloat16) ^ 0x8000L
        case other => context.unsupported(s"unsupported bfloat16 constant $other")
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
      case _ => context.unsupported(s"unsupported ${context.operation.mnemonic.raw} constant $operand")
    }
    if (IEEE754.isNaN(bits, 16)) VMArithmetic.CanonicalNaN16 else bits
  }

  @tailrec
  private def unitOne(operand: Operand): Boolean = operand match {
    case Operand.Integer(value)                              => value == 1
    case Operand.Floating(value, _, _)                       => value == BigDecimal(1)
    case Operand.FloatingBits(_, width, bits) if width == 16 => bits == 0x3c00
    case Operand.FloatingBits(_, width, bits) if width == 32 => bits == 0x3f800000
    case Operand.FloatingBits(_, width, bits) if width == 64 => bits == BigInt("3ff0000000000000", 16)
    case Operand.Unary(inner, false, false, false)           => unitOne(inner)
    case _                                                   => false
  }

  private def negativeZero(operand: Operand): Boolean = operand match {
    case Operand.Unary(inner, true, false, false) => OperandQueries.isZero(inner) &&
      OperandQueries.qualifiers(inner).isEmpty
    case _ => false
  }
}
