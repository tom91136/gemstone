package net.kurobako.gemstone.isa.sass

import ModifierLowering.*
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
  private val comparisons  = Set(Modifier.EQ, Modifier.NE, Modifier.LT, Modifier.LE, Modifier.GT, Modifier.GE)
  private val booleans     = Set(Modifier.AND, Modifier.OR, Modifier.XOR)
  private val xmadTypes    = Set(Modifier.S16, Modifier.U16)
  private val Low32        = Value.Immediate(0xffffffffL)
  private val viaddFormats = Map[Modifier, (Int, Boolean)](
    Modifier.`32`    -> (1, true),
    Modifier.`_32`   -> (1, true),
    Modifier.U32     -> (1, false),
    Modifier.S32     -> (1, true),
    Modifier.`16x2`  -> (2, true),
    Modifier.`_16x2` -> (2, true),
    Modifier.U16x2   -> (2, false),
    Modifier.S16x2   -> (2, true),
    Modifier.U8x4    -> (4, false),
    Modifier.S8x4    -> (4, true)
  )
  private val viaddModifiers       = viaddFormats.keySet + Modifier.ISAT
  private val imulWideModifiers    = Set(Modifier.WIDE, Modifier.U32, Modifier.S32)
  private val shiftModifiers       = Set(Modifier.U32, Modifier.S32, Modifier.W, Modifier.C)
  private val legacySignLogic      = Set(Vec(Modifier.AND), Vec(Modifier.OR), Vec(Modifier.XOR))
  private val legacySignOperations = Set(Modifier.AND, Modifier.OR, Modifier.XOR)
  private val logicModifiers       =
    Set(Modifier.AND, Modifier.OR, Modifier.XOR, Modifier.PASS_B, Modifier.PAND, Modifier.POR)
  private val bitfieldExtractModifiers = Set(Modifier.U32, Modifier.S32, Modifier.BREV)
  private val extendedMinMaxModifiers  = Set(Modifier.U32, Modifier.XHI, Modifier.XLO)
  private val minMaxModifiers          = Set(Modifier.U32, Modifier.S32, Modifier.U64, Modifier.S64)
  private val videoMinMaxModifiers     =
    Set(Modifier.MX, Modifier.MIN, Modifier.MAX, Modifier.ACC, Modifier.UD, Modifier.U32)
  private val signExtendModifiers = Set(Modifier.U32, Modifier.S32, Modifier.C, Modifier.W)
  private val xmadAllowed         =
    Set(Modifier.MRG, Modifier.PSL, Modifier.CBCC, Modifier.CLO, Modifier.CHI, Modifier.CSFU, Modifier.X) ++ xmadTypes
  private val multiplyAddModifiers = Set(
    Modifier.MOV,
    Modifier.SHL,
    Modifier.IADD,
    Modifier.U32,
    Modifier.WIDE,
    Modifier.HI,
    Modifier.X
  )
  private val funnelShiftModifiers = Set(
    Modifier.L,
    Modifier.R,
    Modifier.U32,
    Modifier.S32,
    Modifier.U64,
    Modifier.S64,
    Modifier.HI,
    Modifier.W
  )
  private val loadEffectiveAddressModifiers = Set(Modifier.HI, Modifier.X, Modifier.SX32)

  def lower(using VMCompilationContext): Boolean = lowerConversionsAndAdditions

  private def writeSignAndZero(value: Value)(using context: VMCompilationContext): Unit = {
    import context.*
    emit(WriteConditionFlag(
      VMConditionCodes.Flag.Sign,
      compare(Comparison.LessThan, NumericFormat.Signed(32), value, Value.Immediate(0L))
    ))
    emit(WriteConditionFlag(
      VMConditionCodes.Flag.Zero,
      compare(Comparison.Equal, NumericFormat.Unsigned(32), value, Value.Immediate(0L))
    ))
  }

  private def lowerConversionsAndAdditions(using context: VMCompilationContext): Boolean = {
    import context.*
    (operation.form: @unchecked) match {
      case _: Form.I2i
          if modifiers == Vec(Modifier.S16, Modifier.S8) || modifiers == Vec(Modifier.S16, Modifier.S16) =>
        signExtend()
        true
      // ptxas tests the sign of a word this way in its 64-bit mad.hi.s64 and mul.hi.s64 sequences on sm_50 and sm_60
      case _: Form.I2i
          if modifiers == Vec(Modifier.S32, Modifier.S32) && legacy &&
            operands.headOption.contains(Operand.Qualified(OperandQueries.Zero, Vec(OperandSelector.CC))) =>
        requireOperands(2, "unsupported integer-to-integer conversion")
        val value = source(operands(1))
        writeSignAndZero(value)
        true
      case _: Form.Imad if legacy && modifiers == Vec(Modifier.HI, Modifier.SAT) =>
        requireOperands(4, "IMAD.HI.SAT requires a destination and three sources")
        val result = temp()
        emit(MultiplyAdd(
          result.slot,
          source(operands(1)),
          source(operands(2)),
          source(operands(3)),
          IntegerMultiplyAddOperation.HighSum(32, signed = true, saturate = true)
        ))
        store(operands.head, result)
        true
      case _: Form.I2i if modifiers == Vec(Modifier.S32, Modifier.S32) =>
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
      case _: Form.I2i
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
      case _: Form.I2i if modifiers == Vec(Modifier.U32, Modifier.U32) =>
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
      case _: Form.I2i | _: Form.Ui2i =>
        integerToInteger()
        true
      case _: Form.Icmp =>
        compareSelect()
        true
      case _: Form.Iset =>
        set()
        true
      case _: Form.Iadd3
          if verifiedLegacy &&
            Set(Vec.empty[Modifier], Vec(Modifier.RS), Vec(Modifier.LS), Vec(Modifier.X))(modifiers) =>
        legacyAddThree()
        true
      // UIADD3.64.X adds its two carry inputs; from sm_100 it also names two discarded carry outputs
      case _: Form.Uiadd3 if modifiers.toSet == Set(Modifier.`64`, Modifier.X) =>
        val values = operands match {
          case Vec(d, p, q, a, b, c, x, y)
              if OperandQueries.isAlwaysPredicate(p) && OperandQueries.isAlwaysPredicate(q) =>
            Vec(d, a, b, c, x, y)
          case Vec(d, a, b, c, x, y) => Vec(d, a, b, c, x, y)
          case _                     => unsupported("UIADD3.64.X requires three sources and two carries")
        }
        val result = binary(IntegerOperation.Add, source(values(1), 64), source(values(2), 64), 64)
        emit(IntegerBinary(IntegerOperation.Add, result.slot, result, source(values(3), 64), 64))
        emit(IntegerBinary(IntegerOperation.Add, result.slot, result, bit(predicateSource(values(4)), 64), 64))
        emit(IntegerBinary(IntegerOperation.Add, result.slot, result, bit(predicateSource(values(5)), 64), 64))
        store(values.head, result, 64)
        true
      // From sm_100 the form also names its two discarded carry outputs.
      case _: Form.Uiadd3 if modifiers == Vec(Modifier.`64`) =>
        if (
          operands.size != 4 &&
          !(operands.size == 6 && operands.slice(1, 3).forall(OperandQueries.isAlwaysPredicate))
        ) unsupported("UIADD3.64 requires three sources")
        val sources = operands.takeRight(3)
        val result  = binary(IntegerOperation.Add, source(sources(0), 64), source(sources(1), 64), 64)
        emit(IntegerBinary(IntegerOperation.Add, result.slot, result, source(sources(2), 64), 64))
        store(operands.head, result, 64)
        true
      case _: Form.Iadd3 | _: Form.Uiadd3 =>
        addThree()
        true
      case _: Form.Bfe =>
        bitfieldExtract()
        true
      case _: Form.Bfi =>
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
    (operation.form: @unchecked) match {
      case _: Form.Imnmx
          if modifiers.exists(PartialFunction.cond(_) { case Modifier.XHI | Modifier.XLO => true }) =>
        extendedMinMax()
        true
      // __vminu4, __vmaxs2 and their kin: the lanes' minima or maxima as the predicate selects
      case _: Form.Vimnmx
          if modifiers.size == 1 && PartialFunction.cond(modifiers.head) {
            case Modifier.`U8x4` | Modifier.`S8x4` | Modifier.`U16x2` | Modifier.`S16x2` => true
          } =>
        val data = minMaxData
        if (data.size != 4) unsupported("VIMNMX on lanes requires a destination, two sources and a selector")
        val lanes: Int in (2, 4) =
          if (PartialFunction.cond(modifiers.head) { case Modifier.`U8x4` | Modifier.`S8x4` => true }) 4 else 2
        val signed = PartialFunction.cond(modifiers.head) { case Modifier.`S8x4` | Modifier.`S16x2` => true }
        def extremum(operation: VideoOperation) = {
          val result = temp()
          emit(VideoSimd(
            result.slot,
            source(data(1)),
            source(data(2)),
            Value.Immediate(0L),
            operation,
            lanes,
            signed,
            signed,
            signed,
            false,
            false
          ))
          result
        }
        store(
          data.head,
          select(
            extremum(VideoOperation.Minimum),
            extremum(VideoOperation.Maximum),
            predicateSource(data(3))
          )
        )
        true
      case _: Form.Imnmx | _: Form.Uimnmx | _: Form.Vimnmx | _: Form.Uvimnmx =>
        minMax()
        true
      case _: Form.Vimnmx3 =>
        requireModifiers(Set(Modifier.U32, Modifier.S32))
        requireOperands(5, "expected three integer min/max inputs and a selector")
        val signed = !modifiers.contains(Modifier.U32)
        val inputs = operands.slice(1, 4).map(source(_))
        val low    = inputs.tail.foldLeft(inputs.head)(binary(IntegerOperation.Minimum(signed), _, _))
        val high   = inputs.tail.foldLeft(inputs.head)(binary(IntegerOperation.Maximum(signed), _, _))
        store(operands.head, select(low, high, predicateSource(operands(4))))
        true
      case _: Form.Vmnmx =>
        videoMinMax()
        true
      case _: Form.Viaddmnmx =>
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
      case _: Form.Viadd | _: Form.Uviadd =>
        requireModifiers(viaddModifiers)
        requireOperands(3, "VIADD operands")
        val (lanes, signed) = modifiers.collectFirst(viaddFormats).getOrElse((1, true))
        val saturate        = modifiers.contains(Modifier.ISAT)
        if (
          saturate && !modifiers.exists(PartialFunction.cond(_) {
            case Modifier.U32 | Modifier.S32 | Modifier.U16x2 | Modifier.S16x2 | Modifier.U8x4 | Modifier.S8x4 => true
          })
        )
          unsupported("VIADD.ISAT requires a signed or unsigned format")
        if (lanes == 1) {
          if (saturate && !signed) unsupported("unsupported VIADD.U32.ISAT")
          store(
            operands.head,
            binary(
              if (saturate) IntegerOperation.AddSaturated else IntegerOperation.Add,
              source(operands(1)),
              source(operands(2))
            )
          )
        } else {
          val count = lanes.refine[Int in (2, 4)].getOrElse(unsupported("unsupported VIADD lanes"))
          def simd(operation: VideoOperation, left: Value, right: Value, clamp: Boolean): Value.Register = {
            val result = temp()
            emit(VideoSimd(
              result.slot,
              left,
              right,
              Value.Immediate(0L),
              operation,
              count,
              signed,
              signed,
              signed,
              clamp,
              false
            ))
            result
          }
          // a negated packed source negates each lane
          def lane(operand: Operand): (Value, Boolean) = OperandQueries.without(operand, OperandSelector.reuse) match {
            case Operand.Unary(inner, true, false, false) => (source(inner), true)
            case other                                    => (source(other), false)
          }
          val (a, negateA) = lane(operands(1))
          val (b, negateB) = lane(operands(2))
          val sum          = (negateA, negateB) match {
            case (false, false) => simd(VideoOperation.Add, a, b, saturate)
            case (false, true)  => simd(VideoOperation.Subtract, a, b, saturate)
            case (true, false)  => simd(VideoOperation.Subtract, b, a, saturate)
            case (true, true)   =>
              simd(VideoOperation.Subtract, simd(VideoOperation.Subtract, Value.Immediate(0L), a, false), b, saturate)
          }
          store(operands.head, sum)
        }
        true
      case _: Form.Vadd if modifiers == Vec(Modifier.U16, Modifier.U16, Modifier.MRG_16H) =>
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
    (operation.form: @unchecked) match {
      case _: Form.Sgxt | _: Form.Usgxt =>
        signExtendField()
        true
      case _: Form.Xmad =>
        halfMultiplyAdd()
        true
      case _: Form.Imad | _: Form.Uimad | _: Form.Imad32i =>
        multiplyAdd()
        true
      case _: Form.Isub =>
        subtract()
        true
      // the product's carry predicate, when written, is always clear
      case _: Form.Imul | _: Form.Imul32i if modifiers.contains(Modifier.WIDE) =>
        requireModifiers(imulWideModifiers)
        val values = operands match {
          case Vec(destination, carry: Operand.Predicate, a, b) if OperandQueries.isAlwaysPredicate(carry) =>
            Vec(destination, a, b)
          case Vec(destination, a, b) => Vec(destination, a, b)
          case _                      => unsupported("IMUL.WIDE requires a destination and two sources")
        }
        val signed = !modifiers.contains(Modifier.U32)
        store(
          values.head,
          binary(
            if (signed) IntegerOperation.MultiplyWideSigned32 else IntegerOperation.MultiplyWideUnsigned32,
            source(values(1)),
            source(values(2)),
            64
          ),
          64
        )
        true
      case _: Form.Imul | _: Form.Imul32i =>
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
      case _: Form.Iadd if modifiers.toSet == Set(Modifier.`64`, Modifier.X) =>
        val (carryOut, values) = operands match {
          case Vec(destination, carry: Operand.Predicate, a, b, carryIn) =>
            (Some(carry), Vec(destination, a, b, carryIn))
          case Vec(destination, a, b, carryIn) => (None, Vec(destination, a, b, carryIn))
          case _                               => unsupported("IADD.64.X requires two sources and a carry")
        }
        val left  = source(values(1), 64)
        val sum   = binary(IntegerOperation.Add, left, source(values(2), 64), 64)
        val total = binary(IntegerOperation.Add, sum, bit(predicateSource(values(3)), 64), 64)
        carryOut.foreach(carry =>
          writePredicate(
            carry,
            combine(
              PredicateOperation.Or,
              compare(Comparison.LessThan, NumericFormat.Unsigned(64), sum, left),
              compare(Comparison.LessThan, NumericFormat.Unsigned(64), total, sum)
            )
          )
        )
        store(values.head, total, 64)
        true
      // a negated source subtracts, so its carry out is the no-borrow bit
      case _: Form.Iadd if modifiers == Vec(Modifier.`64`) =>
        val (carryOut, values) = operands match {
          case Vec(destination, carry: Operand.Predicate, a, b) => (Some(carry), Vec(destination, a, b))
          case Vec(destination, a, b)                           => (None, Vec(destination, a, b))
          case _                                                => unsupported("IADD.64 requires two sources")
        }
        val left = source(values(1), 64)
        val sum  = binary(IntegerOperation.Add, left, source(values(2), 64), 64)
        carryOut.foreach { carry =>
          val negativeLeft  = negatedSource(values(1))
          val negativeRight = negatedSource(values(2))
          if (negativeLeft && negativeRight) unsupported("IADD.64 carry with two negated sources")
          writePredicate(
            carry,
            if (negativeLeft || negativeRight) {
              val (minuend, subtrahend) = if (negativeLeft) (values(2), values(1)) else (values(1), values(2))
              compare(
                Comparison.GreaterThanOrEqual,
                NumericFormat.Unsigned(64),
                source(minuend, 64),
                source(OperandQueries.positive(subtrahend), 64)
              )
            } else compare(Comparison.LessThan, NumericFormat.Unsigned(64), sum, left)
          )
        }
        store(values.head, sum, 64)
        true
      // the Blackwell form may also name a carry out ahead of its sources
      case _: Form.Iadd | _: Form.Iadd32i
          if !legacy && modifiers.contains(Modifier.X) && OperandQueries.isPredicateAllowingNegated(operands.last) &&
            (operands.size == 4 || operands.size == 5 && OperandQueries.isPredicateAllowingNegated(operands(1))) =>
        requireModifiers(Set(Modifier.X, Modifier.U32))
        def extended(operand: Operand): Value =
          if (negatedSource(operand)) unary(IntegerUnaryOperation.BitwiseNot, source(OperandQueries.positive(operand)))
          else source(operand)
        val sources = operands.takeRight(3)
        val left    = extended(sources(0))
        val sum     = binary(IntegerOperation.Add, left, extended(sources(1)))
        val result  = binary(IntegerOperation.Add, sum, bit(predicateSource(sources(2))))
        if (operands.size == 5)
          writePredicate(
            operands(1),
            combine(
              PredicateOperation.Or,
              compare(Comparison.LessThan, NumericFormat.Unsigned(32), sum, left),
              compare(Comparison.LessThan, NumericFormat.Unsigned(32), result, sum)
            )
          )
        store(operands.head, result)
        true
      case _: Form.Iadd if legacy && modifiers == Vec(Modifier.SAT) && operands.size == 3 =>
        packedSaturatingAdd()
        true
      case _: Form.Iadd if operands.size == 4 && OperandQueries.isPredicate(operands(1)) =>
        addCarryOut()
        true
      case _: Form.Iadd | _: Form.Iadd32i | _: Form.Iscadd | _: Form.Iscadd32i =>
        add()
        true
      case _ => lowerLogicAndBits
    }
  }

  /** The legacy IADD.SAT saturates the sum of its sources. A negated second source makes it the packed saturating add
    * of the two 16-bit halves ptxas uses for the 16-bit intrinsics: each half is added exactly, so that the negation of
    * -32768 saturates in the sum where a wrapping negation of the whole register would not, and each half is clamped to
    * the signed range of a half, with the carry out of the low half's addition added to the high half. Without a
    * negated source the instruction saturates the 32-bit sum.
    */
  private def packedSaturatingAdd()(using context: VMCompilationContext): Unit = {
    import context.*
    def negated(operand: Operand): (Operand, Boolean) =
      OperandQueries.without(operand, OperandSelector.reuse) match {
        case Operand.Unary(value, true, false, false) if OperandQueries.integer(operand).isEmpty => (value, true)
        case other                                                                               => (other, false)
      }
    val (left, negateLeft)   = negated(operands(1))
    val (right, negateRight) = negated(operands(2))
    if (negateLeft) unsupported("IADD.SAT of a negated first source")
    if (!negateRight) {
      store(operands.head, binary(IntegerOperation.AddSaturated, source(left), source(right)))
      return
    }
    val result = temp()
    emit(PackedSaturatingAdd(result.slot, source(left), source(right), 2, negateRight))
    store(operands.head, result)
  }

  private def lowerLogicAndBits(using context: VMCompilationContext): Boolean = {
    import context.*
    (operation.form: @unchecked) match {
      case _: Form.Shl | _: Form.Shr | _: Form.Ushl | _: Form.Ushr =>
        requireModifiers(shiftModifiers)
        val amount =
          if (modifiers.contains(Modifier.W))
            binary(IntegerOperation.BitwiseAnd, source(operands(2)), Value.Immediate(31L))
          else source(operands(2))
        val shift =
          if (OperandQueries.is[Form.Shl](operation) || OperandQueries.is[Form.Ushl](operation))
            IntegerOperation.ShiftLeft
          else if (modifiers.contains(Modifier.U32)) IntegerOperation.ShiftRightLogical
          else IntegerOperation.ShiftRightArithmetic
        store(operands.head, binary(shift, source(operands(1)), amount))
        true
      case _: Form.Lop32i if modifiers == Vec(Modifier.AND, Modifier.X) =>
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
      // ptxas tests the sign of a product of signed words this way in its mad.hi.s64 sequence on sm_35, sm_50 and sm_60
      case _: Form.Lop
          if legacy && legacySignLogic(modifiers) &&
            operands.size == 3 &&
            operands.head == Operand.Qualified(OperandQueries.Zero, Vec(OperandSelector.CC)) =>
        val value = binary(modifiers.head.integerOperation(), source(operands(1)), source(operands(2)))
        writeSignAndZero(value)
        true
      // the `.X` form extends the previous zero flag with the result's own, as the 128-bit compare of ptxas needs
      case _: Form.Lop
          if legacy && operands.size == 3 &&
            operands.head == Operand.Qualified(OperandQueries.Zero, Vec(OperandSelector.CC)) &&
            modifiers.count(_ == Modifier.X) == 1 &&
            modifiers.filterNot(_ == Modifier.X).size == 1 &&
            modifiers.filterNot(_ == Modifier.X).headOption.exists(legacySignOperations.contains) =>
        val operation = modifiers.filterNot(_ == Modifier.X).head
        val value     = binary(operation.integerOperation(), source(operands(1)), source(operands(2)))
        val zero      = compare(Comparison.Equal, NumericFormat.Unsigned(32), value, Value.Immediate(0L))
        val previous  = predicateTemp()
        val combined  = predicateTemp()
        copyPredicate(previous, readConditionFlag(VMConditionCodes.Flag.Zero))
        emit(PredicateBinary(PredicateOperation.And, combined.slot, zero.slot, previous.slot))
        emit(WriteConditionFlag(VMConditionCodes.Flag.Zero, combined))
        true
      case _: Form.Lop if verifiedLegacy && modifiers.exists(_.isZeroTest) =>
        legacyLogicalPredicate()
        true
      case _: Form.Lop | _: Form.Lop32i | _: Form.Ulop | _: Form.Ulop32i =>
        requireModifiers(logicModifiers)
        val logic = modifiers.find(PartialFunction.cond(_) {
          case Modifier.AND | Modifier.OR | Modifier.XOR | Modifier.PASS_B => true
        })
          .getOrElse(unsupported("missing logical operation"))
        val output = operands.headOption.exists(OperandQueries.isPredicateAllowingUniform(_))
        val body   = if (output) operands.tail else operands
        // the predicate input only combines into a predicate output
        if (
          body.size != 3 &&
          !(body.size == 4 && OperandQueries.isPredicateAllowingBoth(body(3)))
        )
          unsupported("invalid logical operands")
        val result =
          if (logic == Modifier.PASS_B) source(body(2))
          else binary(logic.integerOperation(), source(body(1)), source(body(2)))
        if (output && !OperandQueries.isAlwaysPredicate(operands.head))
          emit(PredicateBinary(
            if (modifiers.contains(Modifier.PAND)) PredicateOperation.And else PredicateOperation.Or,
            predicate(operands.head).slot,
            compare(Comparison.NotEqual, NumericFormat.Unsigned(32), result, Value.Immediate(0L)).slot,
            body.lift(3).fold(Value.Predicate(VMRegisters.Predicate.False.slot): Value.Predicate)(predicateSource).slot
          ))
        store(body.head, result)
        true
      case _: Form.Lop3 | _: Form.Ulop3 =>
        logicThree()
        true
      case _: Form.Shf | _: Form.Ushf =>
        funnelShift()
        true
      case _: Form.Lea if verifiedLegacy =>
        legacyLoadEffectiveAddress()
        true
      case _: Form.Lea | _: Form.Ulea =>
        loadEffectiveAddress()
        true
      case _: Form.Iabs | _: Form.Uiabs =>
        requireModifiers(Set.empty)
        emit(IntegerUnary(IntegerUnaryOperation.Absolute, destination(operands.head).slot, source(operands(1)), 32))
        true
      case _: Form.Popc | _: Form.Upopc =>
        requireModifiers(Set.empty)
        emit(IntegerUnary(
          IntegerUnaryOperation.PopulationCount,
          destination(operands.head).slot,
          source(operands(1)),
          32
        ))
        true
      case _: Form.Flo | _: Form.Uflo =>
        requireModifiers(Set(Modifier.U32, Modifier.SH))
        emit(BitFind(
          destination(operands.head).slot,
          source(operands(1)),
          32,
          !modifiers.contains(Modifier.U32),
          modifiers.contains(Modifier.SH)
        ))
        true
      case _: Form.Brev | _: Form.Ubrev =>
        requireModifiers(Set.empty)
        store(operands.head, unary(IntegerUnaryOperation.BitReverse, source(operands(1))))
        true
      case _: Form.Bmsk | _: Form.Ubmsk =>
        requireModifiers(Set(Modifier.W))
        emit(BitMask(
          destination(operands.head).slot,
          source(operands(1)),
          source(operands(2)),
          if (modifiers.contains(Modifier.W)) BitMaskMode.Wrap else BitMaskMode.Clamp
        ))
        true
      case _: Form.Prmt | _: Form.Uprmt =>
        val mode = modifiers.collectFirst {
          case Modifier.F4E  => BytePermuteMode.F4E
          case Modifier.B4E  => BytePermuteMode.B4E
          case Modifier.RC8  => BytePermuteMode.RC8
          case Modifier.ECL  => BytePermuteMode.ECL
          case Modifier.ECR  => BytePermuteMode.ECR
          case Modifier.RC16 => BytePermuteMode.RC16
        }.getOrElse(BytePermuteMode.Default)
        requireModifiers(Set(Modifier.F4E, Modifier.B4E, Modifier.RC8, Modifier.ECL, Modifier.ECR, Modifier.RC16))
        val result = temp()
        emit(BytePermute(
          result.slot,
          source(operands(1)),
          source(operands(3)),
          source(operands(2)),
          mode
        ))
        store(operands.head, result)
        true
      case _ => lowerMovesAndSpecialRegisters
    }
  }

  private def lowerMovesAndSpecialRegisters(using context: VMCompilationContext): Boolean = {
    import context.*
    (operation.form: @unchecked) match {
      case _: Form.Sel | _: Form.Fsel | _: Form.Usel | _: Form.Ufsel =>
        requireModifiers(Set(Modifier.`64`))
        val width: ScalarBitWidth = if (modifiers.contains(Modifier.`64`)) 64 else 32
        val floating              = operation.form match {
          case _: Form.Fsel | _: Form.Ufsel => true
          case _                            => false
        }
        if (width == 64 && floating) unsupported("FSEL.64")
        val left  = if (floating) fselSource(operands(1), 1) else source(operands(1), width)
        val right = if (floating) fselSource(operands(2), 2) else source(operands(2), width)
        store(operands.head, select(left, right, predicateSource(operands(3)), width), width)
        true
      // ptxas's loop over the distinct values of a register: the first active lane's value to the uniform register,
      // and to the predicate whether this lane's value differs from it, or with `.OR` joined to what it held
      case _: Form.R2ur
          if (modifiers.isEmpty || modifiers == Vec(Modifier.OR)) && operands.size == 3 &&
            OperandQueries.isPredicate(operands.head) =>
        val value = source(operands(2))
        val first = unary(
          IntegerUnaryOperation.LeadingZeroCount,
          unary(IntegerUnaryOperation.BitReverse, Value.ActiveMask)
        )
        val chosen = temp()
        emit(Shuffle(ShuffleMode.Index, chosen.slot, None, value, first, Value.Immediate(0x1fL), Value.ActiveMask, 32))
        val differs = compare(Comparison.NotEqual, NumericFormat.Unsigned(32), value, chosen)
        if (!OperandQueries.isZero(operands(1))) store(operands(1), chosen)
        writePredicate(
          operands.head,
          if (modifiers.isEmpty) differs else combine(PredicateOperation.Or, predicateSource(operands.head), differs)
        )
        true
      case _: Form.R2ur if modifiers == Vec(Modifier.BROADCAST) =>
        if (
          operands.size != 2 || !OperandQueries.isRegister(operands.head, RegisterBank.UR) ||
          !OperandQueries.isRegister(operands(1))
        ) unsupported("R2UR.BROADCAST requires a uniform destination and general source")
        store(operands.head, source(operands(1)))
        true
      // a 64-bit immediate or uniform pair into a register pair
      case _: Form.Mov64iur =>
        requireModifiers(Set.empty)
        requireOperands(2, "MOV64IUR requires a destination and a source")
        store(operands.head, source(operands(1), 64), 64)
        true
      // SPILL marks a move ptxas emits to spill a uniform register
      case _: Form.Mov | _: Form.Umov | _: Form.Mov32i | _: Form.R2ur =>
        requireModifiers(Set(Modifier.`64`, Modifier.SPILL))
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
      case _: Form.S2r | _: Form.S2ur =>
        requireModifiers(Set.empty)
        val name = operands(1) match {
          case Operand.SpecialRegister(name) => name
          case _                             => unsupported("expected special register")
        }
        store(operands.head, specialRegister(name))
        true
      // before sm_70 CS2R reads one 32-bit special register
      case _: Form.Cs2r | _: Form.Cs2ur =>
        requireModifiers(Set(Modifier.`32`))
        requireOperands(2, "CS2R requires a destination and a special register")
        val wide = !legacy && !modifiers.contains(Modifier.`32`)
        val name = operands(1) match {
          case Operand.SpecialRegister(name) => name
          case _                             => unsupported("expected special register")
        }
        val read = name match {
          case SpecialRegister.SRZ                                                 => Value.Immediate(0L)
          case SpecialRegister.SR_CgaSize | SpecialRegister.SR_CgaLinearMemorySlot =>
            Value.Immediate(VMConstantBank.ClusterTable)
          case SpecialRegister.SR_CLOCKLO       => Value.Clock(if (wide) CounterPart.Whole else CounterPart.Low)
          case SpecialRegister.SR_GLOBALTIMERLO => Value.GlobalTimer(if (wide) CounterPart.Whole else CounterPart.Low)
          case SpecialRegister.SR_CLOCKHI if !wide       => Value.Clock(CounterPart.High)
          case SpecialRegister.SR_GLOBALTIMERHI if !wide => Value.GlobalTimer(CounterPart.High)
          case other if !wide => specialRegisters.getOrElse(other, unsupported("unsupported special register"))
          case _              => unsupported("unsupported special register pair")
        }
        store(operands.head, read, if (wide) 64 else 32)
        true
      case _ => false
    }
  }

  /** CUDA 11.8 reads %warpid from bits 8-14 of SR_VIRTID and %smid from bits 20-28. */
  private def specialRegister(name: SpecialRegister)(using context: VMCompilationContext): Value = {
    import context.*
    name match {
      case SpecialRegister.SR_VIRTID =>
        binary(
          IntegerOperation.BitwiseOr,
          binary(IntegerOperation.ShiftLeft, Value.MultiprocessorId, Value.Immediate(20L)),
          binary(IntegerOperation.ShiftLeft, Value.WarpId, Value.Immediate(8L))
        )
      // The downward-growing stack may fill a legacy local window and keeps a modern one above the shared aperture.
      case SpecialRegister.SR_LMEMHIOFF => Value.Immediate(if (legacy) 0L else VMAddressSpace.SharedLimit)
      case _ => specialRegisters.getOrElse(name, unsupported(s"unsupported special register $name"))
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
    SpecialRegister.SR_CgaCtaId -> Value.ClusterCtaRank,
    SpecialRegister.SR_SWINHI   -> Value.Immediate(VMAddressSpace.SharedWindowBase >>> 32),
    SpecialRegister.SR_GEMASK   -> Value.LaneMaskGreaterEqual,
    SpecialRegister.SR_GTMASK   -> Value.LaneMaskGreater,
    SpecialRegister.SR_LEMASK   -> Value.LaneMaskLessEqual,
    SpecialRegister.SR_LTMASK   -> Value.LaneMaskLess,
    SpecialRegister.SR_EQMASK   -> Value.LaneMaskEqual,
    SpecialRegister.SR_CLOCKLO  -> Value.Clock(CounterPart.Low),
    SpecialRegister.SR_CLOCKHI  -> Value.Clock(CounterPart.High),
    // %smid and %warpid as the VM places workgroups and warps, as PTX reads them.
    SpecialRegister.SR_SMID        -> Value.MultiprocessorId,
    SpecialRegister.SR_VIRTUALSMID -> Value.MultiprocessorId,
    SpecialRegister.SR_WARPID      -> Value.WarpId,
    // ptxas reads %nwarpid from bits 8-15 and CUDA 11.8 %nsmid from bits 20-28; the other fields are not modelled.
    SpecialRegister.SR_VIRTCFG -> Value.Immediate(
      EmulatedDevice.Multiprocessors.toLong << 20 | EmulatedDevice.WarpsPerMultiprocessor.toLong << 8
    ),
    SpecialRegister.SR_GLOBALTIMERLO -> Value.GlobalTimer(CounterPart.Low),
    SpecialRegister.SR_GLOBALTIMERHI -> Value.GlobalTimer(CounterPart.High)
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
      code.comparisonCode,
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

  /** Legacy encodings name the unsigned relations; the encoding's own signedness field still applies. */
  private val unsignedAliases =
    Map(Modifier.LO -> Modifier.LT, Modifier.LS -> Modifier.LE, Modifier.HI -> Modifier.GT, Modifier.HS -> Modifier.GE)

  /** The relation `mods` names, with its signedness; `F` and `T` hold of no and every pair of values. */
  private def relation(mods: Set[Modifier])(using context: VMCompilationContext): (Modifier, Boolean) = {
    val named = mods.find(modifier =>
      comparisons(modifier) || unsignedAliases.contains(modifier) || modifier == Modifier.F || modifier == Modifier.T
    )
      .getOrElse(context.unsupported("missing comparison modifier"))
    val alias = unsignedAliases.get(named)
    (alias.getOrElse(named), !mods(Modifier.U32) && (alias.isEmpty || mods(Modifier.S32)))
  }

  private def holds(comparison: Modifier, left: => Value, right: => Value, signed: Boolean)(using
      VMCompilationContext
  ): Value.Predicate = comparison match {
    case Modifier.F => Value.Predicate(VMRegisters.Predicate.False.slot)
    case Modifier.T => Value.Predicate(VMRegisters.Predicate.True.slot)
    case _          => compareValues(comparison, left, right, signed)
  }

  private val relations = comparisons ++ unsignedAliases.keySet ++
    Set(Modifier.F, Modifier.T, Modifier.U32, Modifier.S32)
  private val isetModifiers = relations ++ booleans ++ Set(Modifier.X, Modifier.BM, Modifier.BF)

  private val integerFormats =
    Set(Modifier.U8, Modifier.S8, Modifier.U16, Modifier.S16, Modifier.U32, Modifier.S32)

  /** I2I converts its source, a byte or half of a register where selected, optionally negated or made absolute, to the
    * destination format, saturating with `.SAT`, and extends the result to the register.
    */
  private def integerToInteger()(using context: VMCompilationContext): Unit = {
    import context.*
    requireModifiers(integerFormats + Modifier.SAT)
    requireOperands(2, "I2I requires a destination and a source")
    val (to, from) = modifiers.filter(integerFormats) match {
      case Vec(to, from) => (to.integerConversion, from.integerConversion)
      case _             => unsupported("I2I requires destination and source formats")
    }
    if (
      !(OperandQueries.isRegister(operands.head, allowZero = true) ||
        OperandQueries.isRegister(operands.head, RegisterBank.UR, allowZero = true)) ||
      OperandQueries.selectors(operands.head).nonEmpty
    ) unsupported("I2I destination must be a register")
    val (inner, negative, absolute) = OperandQueries.without(operands(1), OperandSelector.reuse) match {
      case Operand.Unary(value, negative, false, absolute) => (value, negative, absolute)
      case Operand.Unary(_, _, true, _)                    => unsupported("I2I source complement")
      case other                                           => (other, false, false)
    }
    val offset = OperandQueries.selectors(inner) match {
      case Vec()                                        => 0
      case Vec(OperandSelector.B0 | OperandSelector.H0) => 0
      case Vec(OperandSelector.B1)                      => 8
      case Vec(OperandSelector.B2 | OperandSelector.H1) => 16
      case Vec(OperandSelector.B3)                      => 24
      case _                                            => unsupported("unsupported I2I source selector")
    }
    val word  = source(OperandQueries.base(inner))
    val field =
      if (offset == 0) word else binary(IntegerOperation.ShiftRightLogical, word, Value.Immediate(offset.toLong))
    val clamp     = Option.when(modifiers.contains(Modifier.SAT))(ConversionClamp.Saturate)
    val converted =
      if (!negative && !absolute) convert(field, from, to, clamp = clamp)
      else {
        val wide = convert(field, from, NumericFormat.Signed(64))
        if (absolute) emit(IntegerUnary(IntegerUnaryOperation.Absolute, wide.slot, wide, 64))
        if (negative) emit(IntegerUnary(IntegerUnaryOperation.Negate, wide.slot, wide, 64))
        convert(wide, NumericFormat.Signed(64), to, clamp = clamp)
      }
    val extended = to match {
      case NumericFormat.Signed(bits) if bits < 32   => convert(converted, to, NumericFormat.Signed(32))
      case NumericFormat.Unsigned(bits) if bits < 32 => convert(converted, to, NumericFormat.Unsigned(32))
      case _                                         => converted
    }
    store(operands.head, extended)
  }

  private def compareSelect()(using context: VMCompilationContext): Unit = {
    import context.*
    val mods = uniqueModifiers(relations, operation.mnemonic.raw)
    if (operands.size != 4) unsupported("ICMP requires comparison modifier and four operands")
    val (comparison, signed) = relation(mods)
    store(
      operands.head,
      select(
        source(operands(1)),
        source(operands(2)),
        holds(comparison, source(operands(3)), Value.Immediate(0L), signed)
      )
    )
  }

  private def set()(using context: VMCompilationContext): Unit = {
    import context.*
    val mods =
      uniqueModifiers(isetModifiers, operation.mnemonic.raw)
    val combineOp = modifiers.find(booleans)
    // without a Boolean operation the form tests the comparison alone
    val values = if (combineOp.isEmpty) operands :+ OperandQueries.True else operands
    if (values.size != 4) unsupported("ISET requires comparison and predicate-combination modifiers")
    val (comparison, signed) = relation(mods)
    if (mods(Modifier.X) && combineOp.exists(_ != Modifier.AND))
      unsupported("ISET.X supports only AND predicate composition")
    if (mods(Modifier.X) && (comparison == Modifier.F || comparison == Modifier.T))
      unsupported("unsupported ISET.X test")
    val left     = source(values(1))
    val right    = source(values(2))
    val high     = holds(comparison, left, right, signed)
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
    val combined = combine(combineOp.getOrElse(Modifier.AND).predicateOperation, extended, predicateSource(values(3)))
    val result   = select(if (mods(Modifier.BF)) Value.Immediate(0x3f800000L) else Low32, Value.Immediate(0L), combined)
    store(operands.head, result)
    if (OperandQueries.has(operands.head, OperandSelector.CC)) setZeroFlag(result)
  }

  private def halfSelected(operand: Operand): Boolean =
    OperandQueries.selectors(operand).exists(selector =>
      selector == OperandSelector.H0 || selector == OperandSelector.H1
    )

  // a source's H0 or H1 selector zero-extends that half
  private def legacyAddThreeSource(operand: Operand)(using context: VMCompilationContext): Value =
    OperandQueries.integer(operand) match {
      case Some(value) if value >= -0x80000 && value <= 0xfffff =>
        Value.Immediate((if (value >= 0x80000) value - 0x100000 else value).toLong)
      case _ if halfSelected(operand)                          => half(operand, signed = false).selected
      case _ if halfSelected(OperandQueries.positive(operand)) =>
        context.unsupported("legacy IADD3 negates no selected half")
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
        if (halfSelected(operand)) half(operand, signed = false).selected else source(operand)
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
      // From sm_100 the form also names its two discarded carry outputs.
      val inputs =
        if (operands.size == 8 && operands.slice(1, 3).forall(OperandQueries.isAlwaysPredicate)) operands.drop(3)
        else if (operands.size == 7) operands.drop(2)
        else if (operands.size == 6) operands.drop(1)
        else unsupported("IADD3.X carry outputs")
      // the three sources, the carry the lane adds and the carry it discards
      val wide = binary(IntegerOperation.Add, widen(source(inputs(0))), widen(source(inputs(1))), 64)
      emit(IntegerBinary(IntegerOperation.Add, wide.slot, wide, widen(source(inputs(2))), 64))
      Vec(bit(predicateSource(inputs(3))), bit(predicateSource(inputs(4)))).foreach(value =>
        emit(IntegerBinary(IntegerOperation.Add, wide.slot, wide, widen(value), 64))
      )
      val result = temp()
      emit(IntegerBinary(IntegerOperation.BitwiseAnd, result.slot, wide, Low32, 64))
      // the form that names the carry it produces at its second operand
      if (operands.size == 7)
        writePredicate(operands(1), compare(Comparison.GreaterThan, NumericFormat.Unsigned(64), wide, Low32))
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
    val mods = uniqueModifiers(bitfieldExtractModifiers, "BFE")
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
    val mods = uniqueModifiers(extendedMinMaxModifiers, "IMNMX")
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

  /** The destination and inputs of a min/max whose predicate outputs, if any, are all discarded into PT. The outputs
    * follow the destination, or precede it in the sm_120 64-bit form.
    */
  private def minMaxData(using context: VMCompilationContext): Vec[Operand] = {
    import context.*
    val predicate: Operand => Boolean = { case Operand.Predicate(_, _, _) => true; case _ => false }
    val start                         = if (predicate(operands.head)) 0 else 1
    val outputs                       = operands.drop(start).takeWhile(predicate)
    if (outputs.exists(value => !OperandQueries.isAlwaysPredicate(value)))
      unsupported("integer min/max predicate outputs")
    operands.take(start) ++ operands.drop(start + outputs.size)
  }

  private def minMax()(using context: VMCompilationContext): Unit = {
    import context.*
    requireModifiers(minMaxModifiers)
    val data = minMaxData
    if (data.size != 4 && !(data.size == 5 && OperandQueries.isNeverPredicate(data.last)))
      unsupported("integer min/max extended comparison")
    val width: ScalarBitWidth = if (modifiers.exists(_.is64BitInteger)) 64 else 32
    val signed                = !modifiers.exists(PartialFunction.cond(_) { case Modifier.U32 | Modifier.U64 => true })
    val a                     = source(data(1), width)
    val b                     = source(data(2), width)
    val low                   = binary(IntegerOperation.Minimum(signed), a, b, width)
    val high                  = binary(IntegerOperation.Maximum(signed), a, b, width)
    store(data.head, select(low, high, predicateSource(data(3)), width), width)
  }

  private def videoMinMax()(using context: VMCompilationContext): Unit = {
    import context.*
    requireModifiers(videoMinMaxModifiers)
    val legacyNested = verifiedLegacy &&
      (modifiers == Vec(Modifier.MIN) || modifiers == Vec(Modifier.MX, Modifier.MAX)) && operands.size == 4 &&
      OperandQueries.isRegister(operands.head) && operands.tail.forall(OperandQueries.isRegister(_, allowZero = true))
    if ((sm != 35 && !legacyNested) || operands.size != 4) unsupported("unsupported video min/max form")
    val unsigned = modifiers.contains(Modifier.UD)
    if (modifiers.count(_ == Modifier.U32) != (if (unsigned) 2 else 0))
      unsupported("mixed-width or mixed-sign video min/max")
    val secondary = modifiers.filter(PartialFunction.cond(_) { case Modifier.MIN | Modifier.MAX | Modifier.ACC =>
      true
    })
    if (secondary.size != 1) unsupported("unsupported video min/max secondary operation")
    val signed   = !unsigned
    val selected = binary(
      if (modifiers.contains(Modifier.MX)) IntegerOperation.Maximum(signed) else IntegerOperation.Minimum(signed),
      source(operands(1)),
      source(operands(2))
    )
    store(operands.head, binary(secondary.head.integerOperation(signed), selected, source(operands(3))))
  }

  private def signExtendField()(using context: VMCompilationContext): Unit = {
    import context.*
    if (sm < 70 || instruction.guard.nonEmpty) unsupported("SGXT is supported only in its unpredicated sm_70+ form")
    val _ = uniqueModifiers(signExtendModifiers, "SGXT")
    requireOperands(3, "SGXT requires 3 operands")
    val length = OperandQueries.integer(operands(2)) match {
      case Some(value) if value >= 0 && value <= 0x1ff => Value.Immediate(value.toLong)
      case Some(_) => unsupported("SGXT immediate width exceeds the measured 9-bit operand")
      // .W wraps the width at 32; it is clamped to 32 otherwise
      case None if modifiers.contains(Modifier.W) =>
        binary(IntegerOperation.BitwiseAnd, source(operands(2)), Value.Immediate(31L))
      case None => binary(IntegerOperation.Minimum(false), source(operands(2)), Value.Immediate(32L))
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
    val repeated = modifiers.groupBy(identity).exists((modifier, values) => values.size > 1 && modifier != Modifier.S16)
    if (repeated || modifiers.count(_ == Modifier.S16) > 2 || modifiers.exists(modifier => !xmadAllowed(modifier)))
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
    val modes = modifiers.filter(PartialFunction.cond(_) { case Modifier.CLO | Modifier.CHI => true })
    if (modes.size > 1) unsupported("XMAD cannot combine CLO and CHI")
    if (
      mods(Modifier.CBCC) &&
      (!OperandQueries.isRegister(operands(2)) || OperandQueries.isZero(operands(2)))
    )
      unsupported("XMAD.CBCC requires a general-register second source")
    if (
      mods(Modifier.MRG) && modifiers.exists(PartialFunction.cond(_) {
        case Modifier.PSL | Modifier.CBCC | Modifier.CLO | Modifier.CHI => true
      })
    )
      unsupported("unsupported XMAD.MRG modifier combination")
    val a = half(operands(1), signs._1)
    val b = half(operands(2), signs._2)
    if (
      mods(Modifier.CSFU) &&
      (signedTypes != Vec(Modifier.S16, Modifier.S16) || !a.high || !b.high ||
        modifiers.exists(PartialFunction.cond(_) {
          case Modifier.MRG | Modifier.PSL | Modifier.CBCC | Modifier.CLO | Modifier.CHI => true
        }))
    ) unsupported("XMAD.CSFU is bounded to compiler-observed S16.S16 H1,H1 form")
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
    requireModifiers(multiplyAddModifiers)
    val high                  = modifiers.contains(Modifier.HI) && !legacy
    val wide                  = modifiers.contains(Modifier.WIDE) || high
    val width: ScalarBitWidth = if (wide) 64 else 32
    val signed                = !modifiers.contains(Modifier.U32)
    val carryOutput           = operands.size >= 5 && OperandQueries.isPredicateAllowingUniform(operands(1))
    val first                 = if (carryOutput) 2 else 1
    val conditionCode         = OperandQueries.has(operands.head, OperandSelector.CC)
    // the legacy unit subtracts a negated product from the addend, whose carry is the subtraction's no-borrow
    val negatedProduct = legacy && !modifiers.contains(Modifier.X) && conditionCode &&
      OperandQueries.negative(operands(first)) != OperandQueries.negative(operands(first + 1))
    def factor(operand: Operand) = source(if (negatedProduct) OperandQueries.positive(operand) else operand)
    val a                        = factor(operands(first))
    val b                        = factor(operands(first + 1))
    val c                        = source(operands(first + 2), width)
    val result                   = binary(
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
    if (negatedProduct) {
      if (wide || modifiers.contains(Modifier.HI)) unsupported("negated extended IMAD product")
      emit(IntegerSubtractWithBorrow(result.slot, c, result, 32, false, true))
      noBorrowCarry()
    } else if (
      legacy && !modifiers.contains(Modifier.X) && conditionCode && OperandQueries.negative(operands(first + 2))
    ) {
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
    val scaled = operation.form match {
      case _: Form.Iscadd | _: Form.Iscadd32i => true
      case _                                  => false
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
      modifiers.count(_ == Modifier.X) > 1 || operation.size != 2 ||
      !PartialFunction.cond(operation(0)) { case Modifier.AND | Modifier.OR => true } ||
      !operation(1).isZeroTest
    ) unsupported("unsupported legacy logical predicate modifiers")
    if (
      operands.size != 4 || !OperandQueries.isPredicate(operands(0)) ||
      !OperandQueries.isRegister(operands(1), allowZero = true)
    )
      unsupported("invalid legacy logical predicate operands")
    val result = binary(operation(0).integerOperation(), source(operands(2)), source(operands(3)))
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

  /** The truth tables of the named three-input operations a & b & c, a | b | c, a ^ b ^ c and b. */
  private val namedLogic =
    Map(Modifier.AND -> 0x80, Modifier.OR -> 0xfe, Modifier.XOR -> 0x96, Modifier.PASS_B -> 0xcc)
  private val logicThreeModifiers = Set(Modifier.LUT, Modifier.PAND, Modifier.POR, Modifier.NZ) ++ namedLogic.keySet

  private def logicThree()(using context: VMCompilationContext): Unit = {
    import context.*
    requireModifiers(logicThreeModifiers)
    val legacyPredicate = verifiedLegacy && modifiers.contains(Modifier.NZ) && operands.size == 6
    if (modifiers.contains(Modifier.NZ) && !legacyPredicate) unsupported("legacy LOP3 predicate form")
    if (OperandQueries.is[Form.Ulop3](operation) && sm < 75)
      unsupported("uniform logic needs the sm_75 uniform datapath")
    val named  = modifiers.collectFirst(namedLogic)
    val output = legacyPredicate || operands.headOption.exists(OperandQueries.isPredicateAllowingUniform(_))
    val body   = if (output) operands.tail else operands
    val arity  = if (named.nonEmpty) 4 else 5
    if (body.size != arity && body.size != arity + 1) unsupported("invalid LOP3 operands")
    val combining = body.lift(arity).filterNot(OperandQueries.isNeverPredicate)
    if (modifiers.contains(Modifier.PAND) && !output) unsupported("unsupported LOP3 predicate AND form")
    if (combining.exists(!OperandQueries.isPredicateAllowingBoth(_)))
      unsupported("LOP3 predicate input")
    if (legacy && combining.nonEmpty && output) unsupported("unverified legacy LOP3 predicate input")
    // Preserve a negated input before an aliased predicate destination is written.
    val predicateInput =
      Option.when(output)(combining.fold[Value.Predicate](Value.Predicate(VMRegisters.Predicate.False.slot))(
        predicateSource
      ))
    val values = Vec(source(body(1)), source(body(2)), source(body(3)))
    val table  = named.getOrElse(number(body(4)).toInt).refine[Int in 0 ~ 255]
      .getOrElse(unsupported("invalid LOP3 truth table"))
    val result = temp()
    emit(Logic3(result.slot, values(0), values(1), values(2), table, 32))
    if (output && !OperandQueries.isAlwaysPredicate(operands.head))
      emit(PredicateBinary(
        if (modifiers.contains(Modifier.PAND)) PredicateOperation.And else PredicateOperation.Or,
        predicate(operands.head).slot,
        compare(Comparison.NotEqual, NumericFormat.Unsigned(32), result, Value.Immediate(0L)).slot,
        predicateInput.get.slot
      ))
    // the condition-code destination takes the result's zero flag, which the observed consumer reads
    if (OperandQueries.has(body.head, OperandSelector.`CC`))
      emit(WriteConditionFlag(
        VMConditionCodes.Flag.Zero,
        compare(Comparison.Equal, NumericFormat.Unsigned(32), result, Value.Immediate(0L))
      ))
    else store(body.head, result)
  }

  private def funnelShift()(using context: VMCompilationContext): Unit = {
    import context.*
    requireModifiers(funnelShiftModifiers)
    val wide = temp()
    emit(MoveCombine(wide.slot, Vec(source(operands(1)), source(operands(3))), 32))
    val bits   = if (modifiers.exists(_.is64BitInteger)) 64 else 32
    val amount =
      if (modifiers.contains(Modifier.W))
        binary(IntegerOperation.BitwiseAnd, source(operands(2)), Value.Immediate((bits - 1).toLong))
      else binary(IntegerOperation.Minimum(false), source(operands(2)), Value.Immediate(bits.toLong))
    emit(IntegerBinary(
      if (modifiers.contains(Modifier.L)) IntegerOperation.ShiftLeft
      else if (modifiers.exists(_.isSigned32Or64)) IntegerOperation.ShiftRightArithmetic
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
    requireModifiers(loadEffectiveAddressModifiers)
    val carryOutput = OperandQueries.isPredicateAllowingUniform(operands(1))
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
      val shifted = binary(IntegerOperation.ShiftLeft, a, source(operands(first + 2)))
      if (negate || complement) emit(IntegerUnary(IntegerUnaryOperation.BitwiseNot, shifted.slot, shifted, 32))
      emit(Convert(result.slot, shifted, NumericFormat.Unsigned(32), NumericFormat.Unsigned(64), None))
      if (negate) emit(IntegerBinary(IntegerOperation.Add, result.slot, result, Value.Immediate(1L), 64))
    }
    emit(IntegerBinary(IntegerOperation.Add, result.slot, result, widen(b), 64))
    if (modifiers.contains(Modifier.X))
      emit(IntegerBinary(IntegerOperation.Add, result.slot, result, bit(predicateSource(operands.last), 64), 64))
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
    // the shared-window test LegacyAddressRewrite verified: whether the address in Ra and Rc is global
    if (
      modifiers == Vec(Modifier.HI, Modifier.X) && operands.size == 5 && OperandQueries.isPredicate(operands.head) &&
      OperandQueries.isZero(operands(1)) && OperandQueries.isZero(operands(3))
    ) {
      val address = temp()
      emit(MoveCombine(address.slot, Vec(register(operands(2)), register(operands(4))), 32))
      val global = predicateTemp()
      emit(SpaceTest(global.slot, address, AddressKind.Global))
      writePredicate(operands.head, global)
    } else if (modifiers.isEmpty && legacyLowForm(operands).nonEmpty) {
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
      // the shift defaults to 0
      val values = if (operands.size == 4) operands :+ Operand.Integer(0) else operands
      if (values.size != 5 || !OperandQueries.isRegister(values.head) || conditionCode)
        unsupported("LEA.HI requires a general-register destination and four sources")
      if (
        !(if (carry) legacyAddressSource(OperandQueries.positive(values(1))) else legacyAddressSource(values(1))) ||
        !values.slice(2, 4).forall(legacyAddressSource(_))
      ) unsupported("LEA.HI has unsupported source operands")
      val amount = OperandQueries.integer(values(4)).filter(value => value >= 0 && value <= 31)
        .getOrElse(unsupported("LEA.HI shift must be between 0 and 31")).toInt
      val sourceLow   = source(if (carry) OperandQueries.positive(values(1)) else values(1))
      val baseHigh    = source(values(2))
      val highFromLow =
        if (amount == 0) Value.Immediate(0L)
        else binary(IntegerOperation.ShiftRightLogical, sourceLow, Value.Immediate((32 - amount).toLong))
      val sum = binary(
        IntegerOperation.Add,
        highFromLow,
        binary(IntegerOperation.ShiftLeft, source(values(3)), Value.Immediate(amount.toLong))
      )
      if (carry && OperandQueries.negative(values(1)))
        emit(IntegerUnary(IntegerUnaryOperation.BitwiseNot, sum.slot, sum, 32))
      emit(IntegerBinary(IntegerOperation.Add, sum.slot, sum, baseHigh, 32))
      if (carry) emit(IntegerBinary(
        IntegerOperation.Add,
        sum.slot,
        sum,
        bit(readConditionFlag(VMConditionCodes.Flag.Carry)),
        32
      ))
      store(values.head, sum)
    } else unsupported(s"unsupported sm_$sm LEA form")
  }
}
