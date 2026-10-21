package net.kurobako.gemstone.isa.sass

import ModifierLowering.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.isa.nvidia.VMIR.Instruction.*
import net.kurobako.gemstone.isa.sass.Instruction.*
import net.kurobako.gemstone.isa.sass.Instruction as SASSInstruction

/** The Kepler, Maxwell and Pascal scalar video instructions ptxas lowers PTX's to: a destination sign (`UD`, or `SD`
  * for VABSDIFF, whose default is unsigned), the formats of the first and second sources (32-bit signed by default),
  * the part of each source its selector names, and a secondary operation (`ACC`, `MIN`, `MAX`) or a merge into the
  * third source (`MRG_16H`, `MRG_16L`, `MRG_8B0`, `MRG_8B2`); and Kepler's SIMD forms over two half-words or four
  * bytes.
  */
private object VMVideoCompiler {
  private val Formats = Set(Modifier.U8, Modifier.S8, Modifier.U16, Modifier.S16, Modifier.U32, Modifier.S32)
  private val Merges  = Map(
    Modifier.MRG_16H -> VideoSelect.H1,
    Modifier.MRG_16L -> VideoSelect.H0,
    Modifier.MRG_8B0 -> VideoSelect.B0,
    Modifier.MRG_8B2 -> VideoSelect.B2
  )
  private val Secondaries = Map(
    Modifier.ACC -> VideoSecondary.Add,
    Modifier.MIN -> VideoSecondary.Minimum,
    Modifier.MAX -> VideoSecondary.Maximum
  )
  private val Relations = Map(
    Modifier.EQ -> VideoOperation.Equal,
    Modifier.NE -> VideoOperation.NotEqual,
    Modifier.LT -> VideoOperation.Less,
    Modifier.LE -> VideoOperation.LessEqual,
    Modifier.GT -> VideoOperation.Greater,
    Modifier.GE -> VideoOperation.GreaterEqual
  )
  private val ByteSelects     = Vec(VideoSelect.B0, VideoSelect.B1, VideoSelect.B2, VideoSelect.B3)
  private val ScalarModifiers = Formats ++ Merges.keySet ++ Secondaries.keySet ++ Relations.keySet ++
    Set(Modifier.UD, Modifier.SD, Modifier.SAT, Modifier.MX, Modifier.MN, Modifier.W)
  private val MultiplyAddModifiers = Formats ++ Set(Modifier.SAT, Modifier.PO, Modifier.SHR_7, Modifier.SHR_15)
  private val CompareModifiers     = Formats ++ Relations.keySet ++
    Set(Modifier.F, Modifier.T, Modifier.AND, Modifier.OR, Modifier.XOR)
  private val LaneModifiers = Formats ++ Relations.keySet ++
    Set(Modifier.UD, Modifier.SD, Modifier.SAT, Modifier.ACC, Modifier.MX, Modifier.MN, Modifier.AVRG)

  def lower(using context: VMCompilationContext): Boolean = {
    import context.*
    if (sm >= 70) false
    else
      (operation.form: @unchecked) match {
        case _: Form.Vadd     => scalar(None); true
        case _: Form.Vabsdiff => scalar(Some(VideoOperation.AbsoluteDifference)); true
        case _: Form.Vmnmx    =>
          scalar(Some(if (modifiers.contains(Modifier.MX)) VideoOperation.Maximum else VideoOperation.Minimum)); true
        case _: Form.Vshl => scalar(Some(VideoOperation.ShiftLeft)); true
        case _: Form.Vshr => scalar(Some(VideoOperation.ShiftRight)); true
        case _: Form.Vset =>
          scalar(Some(modifiers.collectFirst(Relations).getOrElse(unsupported("VSET requires a comparison")))); true
        case _: Form.Vmad  => multiplyAdd(); true
        case _: Form.Vsetp => compareParts(); true
        case _: Form.Vadd2 | _: Form.Vabsdiff2 | _: Form.Vmnmx2 |
            _: Form.Vset2 => lanes(2); true
        case _: Form.Vadd4 | _: Form.Vabsdiff4 | _: Form.Vmnmx4 |
            _: Form.Vset4 if sm < 50 => lanes(4); true
        case _ => false
      }
  }

  /** A source's register, the part of it the instruction reads, whether it is negated, and its format's signedness. */
  private def part(operand: Operand, format: Modifier, binding: String)(using
      context: VMCompilationContext
  ): (value: Value, select: VideoSelect, negated: Boolean) = {
    import context.*
    val (negated, inner) = operand match {
      case Operand.Unary(value, true, false, false) => (true, value)
      case other                                    => (false, other)
    }
    // an immediate source is the part itself; its sign negates it
    OperandQueries.integer(inner).map { value =>
      val select = format match {
        case Modifier.U8 | Modifier.S8   => VideoSelect.B0
        case Modifier.U16 | Modifier.S16 => VideoSelect.H0
        case _                           => VideoSelect.Word
      }
      (Value.Immediate((value.abs & 0xffffffffL).toLong): Value, select, negated != (value < 0))
    }.getOrElse {
      // the encoded selector names a byte or a half-word by the format; nvdisasm text spells it as a suffix
      val index = instruction.machine.flatMap(_.bindings.get(binding)).map(_.toInt).getOrElse(
        OperandQueries.selectors(inner).collectFirst {
          case OperandSelector.B0 | OperandSelector.H0 => 0
          case OperandSelector.B1 | OperandSelector.H1 => 1
          case OperandSelector.B2                      => 2
          case OperandSelector.B3                      => 3
        }.getOrElse(0)
      )
      val select = format match {
        case Modifier.U8 | Modifier.S8   => ByteSelects(index & 3)
        case Modifier.U16 | Modifier.S16 => if ((index & 1) == 0) VideoSelect.H0 else VideoSelect.H1
        case _                           => VideoSelect.Word
      }
      (source(registerBaseOrZero(inner, s"${operation.mnemonic.raw} source")), select, negated)
    }
  }

  private def formats(using context: VMCompilationContext): (Modifier, Modifier) =
    context.modifiers.filter(Formats) match {
      case Vec()            => (Modifier.S32, Modifier.S32)
      case Vec(left, right) => (left, right)
      case found            => context.unsupported(s"unsupported video formats ${found.mkString(".")}")
    }

  /** The elements of the register pair a source's vendor selector names, in the order the selector writes them: `11`
    * names the 16-bit element 1 twice, `4321` the 8-bit elements 4, 3, 2 and 1.
    */
  private def elementSelector(operand: Operand): Option[Vec[Int]] =
    OperandQueries
      .selectors(OperandQueries.unaryValue(operand))
      .collectFirst { case OperandSelector.Unknown(name) => name.raw }
      .flatMap { spelling =>
        val digits = spelling.filter(_.isDigit)
        val count  = digits.length
        if (count == spelling.length && (count == 2 || count == 4))
          Some(Vec.from(digits.toVector.map(_.asDigit)))
        else None
      }

  /** Whether `operand` is a source whose vendor selector names the elements of a register pair. */
  def isElementSource(operand: Operand): Boolean = elementSelector(operand).nonEmpty

  /** Whether the instruction reads the elements of a register pair, which its vendor selector names. */
  def acceptsElementSelectors(instruction: DecodedInstruction): Boolean =
    (instruction.operation.form: @unchecked) match {
      case _: Form.Vadd2 | _: Form.Vabsdiff2 | _: Form.Vmnmx2 | _: Form.Vset2 |
          _: Form.Vadd4 | _: Form.Vabsdiff4 | _: Form.Vmnmx4 | _: Form.Vset4 => true
      case _ => false
    }

  /** The source's elements, gathered from the 64-bit register pair its selector names. */
  private def gathered(operand: Operand, indices: Vec[Int])(using context: VMCompilationContext): Value = {
    import context.*
    val (low, high) = registerPair(operand)
    val bits        = if (indices.size == 2) 16 else 8
    val mask        = (1L << bits) - 1L
    val parts       = indices.reverse.zipWithIndex.map { (element, slot) =>
      val shift = element * bits
      val word  = if (shift < 32) low else high
      val moved =
        if (shift % 32 == 0) word
        else binary(IntegerOperation.ShiftRightLogical, word, Value.Immediate((shift % 32).toLong))
      val field = binary(IntegerOperation.BitwiseAnd, moved, Value.Immediate(mask))
      if (slot * bits == 0) field
      else binary(IntegerOperation.ShiftLeft, field, Value.Immediate((slot * bits).toLong))
    }
    parts.reduceLeft((left, right) => binary(IntegerOperation.BitwiseOr, left, right))
  }

  /** The aligned register pair a source's selector reads: the even register and the odd one that follows it. */
  private def registerPair(operand: Operand)(using context: VMCompilationContext): (Value, Value) = {
    import context.*
    OperandQueries.base(OperandQueries.without(operand, OperandSelector.`reuse`)) match {
      case Operand.Register(RegisterBank.R, index) =>
        val even = (index & ~1).refine[Int in 0 ~ 255].toOption
          .getOrElse(unsupported(s"video vector source register is out of range: $operand"))
        val odd = (even + 1).refine[Int in 0 ~ 255].toOption
          .getOrElse(unsupported(s"video vector source register is out of range: $operand"))
        (source(Operand.Register(RegisterBank.R, even)), source(Operand.Register(RegisterBank.R, odd)))
      case other => unsupported(s"unsupported video vector source $other")
    }
  }

  private def signed(format: Modifier): Boolean =
    PartialFunction.cond(format) { case Modifier.S8 | Modifier.S16 | Modifier.S32 => true }

  private def scalar(chosen: Option[VideoOperation])(using context: VMCompilationContext): Unit = {
    import context.*
    requireModifiers(
      ScalarModifiers
    )
    requireOperands(4, s"${operation.mnemonic.raw} requires a destination and three sources")
    val (leftFormat, rightFormat) = formats
    val left                      = part(operands(1), leftFormat, "asel")
    val right                     = part(operands(2), rightFormat, "bsel")
    val kind                      =
      chosen.getOrElse(if (right.negated) VideoOperation.Subtract else VideoOperation.Add)
    if (left.negated || (right.negated && kind != VideoOperation.Subtract))
      unsupported("unsupported negated video source")
    val resultSigned =
      if (OperandQueries.is[Form.Vabsdiff](operation)) modifiers.contains(Modifier.SD)
      else !modifiers.contains(Modifier.UD) && !OperandQueries.is[Form.Vset](operation)
    val merge     = modifiers.collectFirst(Merges).getOrElse(VideoSelect.Word)
    val secondary = modifiers.collectFirst(Secondaries).getOrElse(VideoSecondary.Plain)
    val result    = temp()
    emit(VideoScalar(
      destination = result.slot,
      left = left.value,
      right = right.value,
      addend = source(operands(3)),
      operation = kind,
      leftSelect = left.select,
      rightSelect = right.select,
      leftSigned = signed(leftFormat),
      rightSigned = signed(rightFormat),
      resultSigned = resultSigned,
      saturate = modifiers.contains(Modifier.SAT),
      secondary = secondary,
      merge = merge,
      wrap = modifiers.contains(Modifier.W)
    ))
    store(operands.head, result)
  }

  private def multiplyAdd()(using context: VMCompilationContext): Unit = {
    import context.*
    requireModifiers(MultiplyAddModifiers)
    requireOperands(4, "VMAD requires a destination and three sources")
    val (leftFormat, rightFormat) = formats
    val left                      = part(operands(1), leftFormat, "asel")
    val right                     = part(operands(2), rightFormat, "bsel")
    val (negateAddend, addend)    = operands(3) match {
      case Operand.Unary(value, true, false, false) => (true, value)
      case other                                    => (false, other)
    }
    val shift: Int in (0, 7, 15) =
      if (modifiers.contains(Modifier.SHR_15)) 15 else if (modifiers.contains(Modifier.SHR_7)) 7 else 0
    val result = temp()
    emit(VideoMultiplyAdd(
      result.slot,
      left.value,
      right.value,
      source(addend),
      left.select,
      right.select,
      signed(leftFormat),
      signed(rightFormat),
      left.negated != right.negated,
      negateAddend,
      false,
      modifiers.contains(Modifier.PO),
      shift,
      modifiers.contains(Modifier.SAT)
    ))
    store(operands.head, result)
  }

  /** VSETP compares the selected parts like VSET and combines the comparison with a predicate as ISETP does. */
  private def compareParts()(using context: VMCompilationContext): Unit = {
    import context.*
    requireModifiers(CompareModifiers)
    val boolean = modifiers.find(_.isBitwise)
    val values  =
      if (boolean.isEmpty) Vec(operands.head, OperandQueries.True) ++ operands.drop(1) :+ OperandQueries.True
      else operands
    if (values.size != 5) unsupported("VSETP requires two sources and a predicate")
    val (leftFormat, rightFormat) = formats
    val left                      = part(values(2), leftFormat, "asel")
    val right                     = part(values(3), rightFormat, "bsel")
    if (left.negated || right.negated) unsupported("unsupported negated video source")
    val result: Value.Predicate = modifiers.collectFirst(Relations) match {
      case Some(relation) =>
        val compared = temp()
        emit(VideoScalar(
          compared.slot,
          left.value,
          right.value,
          Value.Immediate(0L),
          relation,
          left.select,
          right.select,
          signed(leftFormat),
          signed(rightFormat),
          false,
          false,
          VideoSecondary.Plain,
          VideoSelect.Word,
          false
        ))
        compare(Comparison.NotEqual, NumericFormat.Unsigned(32), compared, Value.Immediate(0L))
      case None =>
        Value.Predicate(
          if (modifiers.contains(Modifier.T)) VMRegisters.Predicate.True.slot else VMRegisters.Predicate.False.slot
        )
    }
    val operation = boolean.getOrElse(Modifier.AND).predicateOperation
    val masked    = predicateTemp()
    copyPredicate(masked, predicateSource(values(4)))
    if (!OperandQueries.isAlwaysPredicate(values(1)))
      emit(PredicateBinary(operation, predicate(values(1)).slot, invert(result).slot, masked.slot))
    if (!OperandQueries.isAlwaysPredicate(values(0)))
      emit(PredicateBinary(operation, predicate(values(0)).slot, result.slot, masked.slot))
  }

  /** Kepler's SIMD video instructions apply their operation to each of `count` lanes, signed unless their format says
    * otherwise, saturating to the destination's lanes with `.SAT` or summing the lanes onto the third source with
    * `.ACC`.
    */
  private def lanes(count: Int in (2, 4))(using context: VMCompilationContext): Unit = {
    import context.*
    requireModifiers(
      LaneModifiers
    )
    requireOperands(4, s"${operation.mnemonic.raw} requires a destination and three sources")
    val (leftFormat, rightFormat) = formats
    val absolute                  = operation.form match {
      case _: Form.Vabsdiff2 | _: Form.Vabsdiff4 => true
      case _                                     => false
    }
    val kind = operation.form match {
      case _: Form.Vadd2 | _: Form.Vadd4 =>
        if (modifiers.contains(Modifier.AVRG)) VideoOperation.Average else VideoOperation.Add
      case _: Form.Vmnmx2 | _: Form.Vmnmx4 =>
        if (modifiers.contains(Modifier.MX)) VideoOperation.Maximum else VideoOperation.Minimum
      case _: Form.Vset2 | _: Form.Vset4 =>
        modifiers.collectFirst(Relations).getOrElse(unsupported("VSET requires a comparison"))
      case _ => VideoOperation.AbsoluteDifference
    }
    def plain(operand: Operand): Value =
      if (OperandQueries.integer(operand).nonEmpty) source(operand)
      else
        elementSelector(operand) match {
          case Some(indices) => gathered(operand, indices)
          case None          => source(registerBaseOrZero(operand, s"${operation.mnemonic.raw} source"))
        }
    // a negated register source subtracts each of its lanes, as __vsub4 lowers on sm_35
    def negated(operand: Operand): (Operand, Boolean) = operand match {
      case Operand.Unary(value, true, false, false) if OperandQueries.integer(operand).isEmpty => (value, true)
      case other                                                                               => (other, false)
    }
    val (left, negateLeft)   = negated(operands(1))
    val (right, negateRight) = negated(operands(2))
    if ((negateLeft || negateRight) && (kind != VideoOperation.Add || (negateLeft && negateRight)))
      unsupported("unsupported negated video source")
    val ((first, firstFormat), (second, secondFormat)) =
      if (negateLeft) ((right, rightFormat), (left, leftFormat)) else ((left, leftFormat), (right, rightFormat))
    val resultSigned = if (absolute) modifiers.contains(Modifier.SD) else !modifiers.contains(Modifier.UD)
    val result       = temp()
    emit(VideoSimd(
      destination = result.slot,
      left = plain(first),
      right = plain(second),
      addend = source(operands(3)),
      operation = if (negateLeft || negateRight) VideoOperation.Subtract else kind,
      lanes = count,
      leftSigned = signed(firstFormat),
      rightSigned = signed(secondFormat),
      resultSigned = resultSigned,
      saturate = modifiers.contains(Modifier.SAT),
      accumulate = modifiers.contains(Modifier.ACC)
    ))
    store(operands.head, result)
  }
}
