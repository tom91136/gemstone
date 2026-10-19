package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.isa.nvidia.VMIR.Instruction.*
import net.kurobako.gemstone.isa.sass.Instruction.*

/** Byte and half-word lane arithmetic, dot products of packed lanes, carry-less products and address-space queries. */
private object VMLaneCompiler {
  private val matchModifiers = Set(Modifier.ANY, Modifier.ALL, Modifier.U32, Modifier.U64)

  def lower(using context: VMCompilationContext): Boolean = {
    import context.*
    def ternary(make: (Register, Value, Value, Value) => Instruction): Unit = {
      requireOperands(4, s"${operation.mnemonic.raw} requires a destination and three sources")
      val result = temp()
      emit(make(result.slot, source(operands(1)), source(operands(2)), source(operands(3))))
      store(operands.head, result)
    }
    def dotProduct4(left: Modifier, right: Modifier): Unit =
      ternary(DotProduct4(_, _, _, _, left == Modifier.S8, right == Modifier.S8))
    (operation.form: @unchecked) match {
      case _: Form.Vabsdiff4
          if modifiers.forall(PartialFunction.cond(_) { case Modifier.U8 | Modifier.S8 | Modifier.ACC => true }) =>
        val signed = modifiers.contains(Modifier.S8)
        ternary(VideoSimd(
          _,
          _,
          _,
          _,
          VideoOperation.AbsoluteDifference,
          4,
          signed,
          signed,
          false,
          false,
          modifiers.contains(Modifier.ACC)
        ))
        true
      case _: Form.Vabsdiff if modifiers == Vec(Modifier.U32) || modifiers.isEmpty =>
        ternary(SumAbsoluteDifference(_, _, _, _, 32, modifiers.isEmpty))
        true
      // Kepler's __sad and __usad
      case _: Form.Isad if modifiers.forall(PartialFunction.cond(_) { case Modifier.U32 | Modifier.S32 =>
            true
          }) && modifiers.size <= 1 =>
        ternary(SumAbsoluteDifference(_, _, _, _, 32, !modifiers.contains(Modifier.U32)))
        true
      case _: Form.Idp | _: Form.Idp4a =>
        modifiers match {
          case Vec(Modifier.`4A`, left @ (Modifier.S8 | Modifier.U8), right @ (Modifier.S8 | Modifier.U8)) =>
            dotProduct4(left, right)
          // IDP4A names only the formats
          case Vec(left @ (Modifier.S8 | Modifier.U8), right @ (Modifier.S8 | Modifier.U8))
              if OperandQueries.is[Form.Idp4a](operation) =>
            dotProduct4(left, right)
          case Vec(
                Modifier.`2A`,
                mode @ (Modifier.LO | Modifier.HI),
                left @ (Modifier.S16 | Modifier.U16),
                right @ (Modifier.S8 | Modifier.U8)
              ) =>
            ternary(DotProduct2(_, _, _, _, mode == Modifier.HI, left == Modifier.S16, right == Modifier.S8))
          case _ => unsupported(s"unsupported IDP modifiers ${modifiers.mkString(".")}")
        }
        true
      case _: Form.Match =>
        matchLanes()
        true
      // clmad.lo and clmad.hi: a half of the carry-less product of two register pairs, exclusive-ored with a third
      case _: Form.Clmad =>
        requireOperands(4, "CLMAD requires a destination and three sources")
        val high = modifiers match {
          case Vec(Modifier.HI) => true
          case Vec(Modifier.LO) => false
          case _                => unsupported(s"unsupported CLMAD modifiers ${modifiers.mkString(".")}")
        }
        val product =
          binary(IntegerOperation.CarrylessMultiply(high), source(operands(1), 64), source(operands(2), 64), 64)
        store(operands.head, binary(IntegerOperation.BitwiseXor, product, source(operands(3), 64), 64), 64)
        true
      case _: Form.I2ip | _: Form.Ui2ip =>
        packIntegers()
        true
      case _: Form.Qspc =>
        val space = modifiers.filterNot(_ == Modifier.E) match {
          case Vec(Modifier.G)                   => AddressKind.Global
          case Vec(Modifier.S) | Vec(Modifier.D) => AddressKind.Shared
          case Vec(Modifier.L)                   => AddressKind.Local
          case _ => unsupported(s"unsupported QSPC modifiers ${modifiers.mkString(".")}")
        }
        val pointer = address(operands.last, global = true)
        val result  = predicateTemp()
        emit(SpaceTest(result.slot, pointer, space))
        // The register result names the window whatever space is queried: 1 global, 2 shared or 4 local, as an RTX
        // 4060 Ti reports.
        val code = operands.dropRight(1).collectFirst {
          case register: Operand.Register if !OperandQueries.isZero(register) => register
        }
        code.foreach { register =>
          val kinds =
            addressSpaceBits.map { (kind, bit) =>
              val in = predicateTemp()
              emit(SpaceTest(in.slot, pointer, kind))
              select(Value.Immediate(bit), Value.Immediate(0L), in)
            }
          store(register, kinds.reduce((left, right) => binary(IntegerOperation.BitwiseOr, left, right)))
        }
        operands.headOption.foreach {
          case predicate: Operand.Predicate => writePredicate(predicate, result)
          case _                            => ()
        }
        true
      case _ => false
    }
  }

  private val packedFormats = Map(
    Modifier.S16 -> (16, true),
    Modifier.U16 -> (16, false),
    Modifier.S8  -> (8, true),
    Modifier.U8  -> (8, false),
    Modifier.S4  -> (4, true),
    Modifier.U4  -> (4, false),
    Modifier.S2  -> (2, true),
    Modifier.U2  -> (2, false)
  )
  private val addressSpaceBits      = Vec(AddressKind.Global -> 1L, AddressKind.Shared -> 2L, AddressKind.Local -> 4L)
  private val packIntegersModifiers = packedFormats.keySet ++
    Set(Modifier.S32, Modifier.SAT, Modifier.RELU, Modifier.SATRELU)

  /** I2IP packs its first source into the lane above its second, each converted to the destination format, over the
    * third source shifted up past both lanes (its selected half for bytes), as `cvt.pack` does. `.SAT` clamps each lane
    * to the format's range and `.RELU` negative values to zero; otherwise the lanes wrap.
    */
  private def packIntegers()(using context: VMCompilationContext): Unit = {
    import context.*
    requireModifiers(packIntegersModifiers)
    requireOperands(4, "I2IP requires a destination and three sources")
    val (bits, signed) = modifiers.collectFirst(packedFormats).getOrElse(unsupported("I2IP requires a packed format"))
    val saturate       = modifiers.exists(PartialFunction.cond(_) { case Modifier.SAT | Modifier.SATRELU => true })
    val relu           = modifiers.exists(PartialFunction.cond(_) { case Modifier.RELU | Modifier.SATRELU => true })
    // the shift of the third source, written `.24` or `.28` for nibbles and crumbs, is fixed by the format
    val (rest, upperHalf) = OperandQueries.selectors(operands(3)).filterNot(_ == OperandSelector.reuse) match {
      case Vec() | Vec(OperandSelector.H0)      => (OperandQueries.base(operands(3)), false)
      case Vec(OperandSelector.H1) if bits == 8 => (OperandQueries.base(operands(3)), true)
      case Vec(OperandSelector.Unknown(width)) if width.raw == (32 - 2 * bits).toString =>
        (OperandQueries.base(operands(3)), false)
      case _ => unsupported("unsupported I2IP third source")
    }
    val width = bits.refine[Int in 1 ~ 16].getOrElse(unsupported("unsupported I2IP format"))
    if (saturate && !relu && !upperHalf) {
      val result = temp()
      emit(PackSaturated(result.slot, source(operands(1)), source(operands(2)), source(rest), width, signed))
      store(operands.head, result)
    } else {
      val mask                                   = (1L << bits) - 1L
      def lane(operand: Operand): Value.Register = {
        val value   = source(operand)
        val lowest  = if (relu) 0L else if (signed) -(1L << (bits - 1)) else 0L
        val highest = if (signed) (1L << (bits - 1)) - 1L else mask
        val bounded =
          if (saturate)
            binary(
              IntegerOperation.Minimum(true),
              binary(IntegerOperation.Maximum(true), value, Value.Immediate(lowest & 0xffffffffL)),
              Value.Immediate(highest)
            )
          else if (relu) binary(IntegerOperation.Maximum(true), value, Value.Immediate(0L))
          else value
        binary(IntegerOperation.BitwiseAnd, bounded, Value.Immediate(mask))
      }
      val packed = binary(
        IntegerOperation.BitwiseOr,
        lane(operands(2)),
        binary(IntegerOperation.ShiftLeft, lane(operands(1)), Value.Immediate(bits.toLong))
      )
      val above =
        if (bits == 16) Value.Immediate(0L)
        else if (upperHalf) binary(IntegerOperation.BitwiseAnd, source(rest), Value.Immediate(0xffff0000L))
        else binary(IntegerOperation.ShiftLeft, source(rest), Value.Immediate(2L * bits))
      store(operands.head, binary(IntegerOperation.BitwiseOr, packed, above))
    }
  }

  /** match.sync over the active lanes, which SASS's MATCH takes part with, as no member mask is named: the lanes whose
    * value equals this lane's, read lane by lane with shuffles; under `.ALL` every active lane where all are equal and
    * none otherwise, with the predicate saying whether they are.
    */
  private def matchLanes()(using context: VMCompilationContext): Unit = {
    import context.*
    requireModifiers(matchModifiers)
    val all                   = modifiers.contains(Modifier.ALL)
    val wide                  = modifiers.contains(Modifier.U64)
    val (flag, result, value) = operands match {
      case Vec(output @ Operand.Predicate(_, _, _), destination, source) if all => (Some(output), destination, source)
      case Vec(destination, source)                                             => (None, destination, source)
      case _ => unsupported("unsupported MATCH operands")
    }
    val members = temp()
    emit(Move(members.slot, Value.ActiveMask, 32))
    val words = (if (wide) Vec(0, 1) else Vec(0)).map { word =>
      val copy = temp()
      emit(Move(
        copy.slot,
        register(OperandQueries.base(OperandQueries.without(value, OperandSelector.reuse)), word),
        32
      ))
      copy
    }
    val matched  = temp()
    val shuffled = temp()
    val chosen   = temp()
    val valid    = predicateTemp()
    val equal    = predicateTemp()
    val half     = predicateTemp()
    emit(Move(matched.slot, Value.Immediate(0L), 32))
    val intersectHalf  = PredicateBinary(PredicateOperation.And, equal.slot, equal.slot, half.slot)
    val intersectValid = PredicateBinary(PredicateOperation.And, equal.slot, equal.slot, valid.slot)
    val accumulate     = IntegerBinary(IntegerOperation.BitwiseOr, matched.slot, matched, chosen, 32)
    (0 until 32).foreach { lane =>
      words.zipWithIndex.foreach { (own, index) =>
        emit(Shuffle(
          ShuffleMode.Index,
          shuffled.slot,
          Some(valid.slot),
          own,
          Value.Immediate(lane.toLong),
          Value.Immediate(0x1fL),
          members,
          32
        ))
        emit(SetPredicate(
          if (index == 0) equal.slot else half.slot,
          Comparison.Equal,
          NumericFormat.Unsigned(32),
          shuffled,
          own
        ))
        if (index != 0) emit(intersectHalf)
        emit(intersectValid)
      }
      emit(Select(chosen.slot, Value.Immediate(1L << lane), Value.Immediate(0L), equal.slot, 32))
      emit(accumulate)
    }
    emit(IntegerBinary(IntegerOperation.BitwiseAnd, matched.slot, matched, members, 32))
    if (all) {
      emit(SetPredicate(equal.slot, Comparison.Equal, NumericFormat.Unsigned(32), matched, members))
      emit(Select(matched.slot, members, Value.Immediate(0L), equal.slot, 32))
      flag.foreach(output => writePredicate(output, equal))
    }
    store(result, matched)
  }
}
