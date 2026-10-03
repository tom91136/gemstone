package net.kurobako.gemstone.isa.sass

import ModifierSemantics.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.isa.nvidia.VMIR.Instruction.*
import net.kurobako.gemstone.isa.sass.Instruction.*
import net.kurobako.gemstone.isa.sass.Instruction as SASSInstruction

/** Typed lowering for the verified integer SASS families. */
private object VMIntegerCompiler {
  private val comparisons =
    Set(Modifier.`EQ`, Modifier.`NE`, Modifier.`LT`, Modifier.`LE`, Modifier.`GT`, Modifier.`GE`)
  private val booleans  = Set(Modifier.`AND`, Modifier.`OR`, Modifier.`XOR`)
  private val xmadTypes = Set(Modifier.`S16`, Modifier.`U16`)

  def compile(context: VMCompilationContext): Boolean = {
    val instruction = context.instruction
    val operands    = instruction.operands
    val modifiers   = instruction.modifiers
    instruction.operation match {
      case _: SASSInstruction.I2i
          if modifiers == Vec(Modifier.`S16`, Modifier.`S8`) || modifiers == Vec(Modifier.`S16`, Modifier.`S16`) =>
        compileI2I(context, operands, modifiers)
      case _: SASSInstruction.Icmp | _: SASSInstruction.Iset =>
        compileCompareSelect(context, instruction.operation, operands, modifiers)
      case _: SASSInstruction.Iadd3 if Set(50, 60)(context.sm)  => compileLegacyIadd3(context, operands, modifiers)
      case _: SASSInstruction.Iscadd if Set(50, 60)(context.sm) => compileIscadd(context, operands, modifiers)
      case _: SASSInstruction.Bfe | _: SASSInstruction.Bfi      =>
        compileBitfield(context, instruction.operation, operands, modifiers)
      case _: SASSInstruction.Imnmx if modifiers.exists(Set(Modifier.`XHI`, Modifier.`XLO`)) =>
        compileExtendedMinMax(context, operands, modifiers)
      case _: SASSInstruction.Sgxt => compileSgxt(context, operands, modifiers)
      case _: SASSInstruction.Xmad => compileXmad(context, operands, modifiers)
      case _                       => false
    }
  }

  private def fail(reason: String): Nothing = throw IllegalArgumentException(reason)

  private def requiredOperands(operands: Vec[Operand], size: Int, description: String): Unit =
    if (operands.size != size) fail(s"$description requires $size operands")

  private def uniqueModifiers(modifiers: Vec[Modifier], allowed: Set[Modifier], description: String): Set[Modifier] = {
    val distinct = modifiers.toSet
    if (distinct.size != modifiers.size || modifiers.exists(modifier => !allowed(modifier)))
      fail(s"unsupported $description modifiers ${modifiers.mkString(".")}")
    distinct
  }

  private def writableRegister(operand: Operand, name: String): Unit =
    if (!VMOperand.isRegister(operand, RegisterBank.R)) fail(s"$name must be a general register")

  private def isLegacyIadd3Source(operand: Operand): Boolean = {
    val positive  = VMOperand.positive(operand)
    val plain     = VMOperand.base(positive)
    val supported = plain match {
      case Operand.Register(RegisterBank.R, _) => true
      case Operand.ConstantBank(_, offset)     => VMOperand.integer(offset).nonEmpty
      case Operand.Integer(value)              => value >= -0x80000 && value <= 0xfffff
      case Operand.Bits(value, _)              => value >= -0x80000 && value <= 0xfffff
      case _                                   => false
    }
    supported && (!VMOperand.negative(operand) || VMOperand.isRegister(plain, RegisterBank.R))
  }

  private def legacyIadd3Source(context: VMCompilationContext, operand: Operand): Value =
    VMOperand.integer(operand) match {
      case Some(value) if value >= 0 && value <= 0xfffff =>
        Value.Immediate((if (value >= 0x80000) value - 0x100000 else value).toLong)
      case _ => context.source(VMOperand.positive(operand))
    }

  private def compare(
      context: VMCompilationContext,
      code: Modifier,
      left: Value,
      right: Value,
      signed: Boolean
  ): Value.Register = {
    val comparison = comparisonCode(code)
    val result     = context.temp("q")
    context.emit(SetPredicate(
      result.name,
      comparison,
      if (signed) ConversionType.S32.format else ConversionType.U32.format,
      left,
      right
    ))
    result
  }

  private def compileI2I(
      context: VMCompilationContext,
      operands: Vec[Operand],
      modifiers: Vec[Modifier]
  ): Boolean = {
    requiredOperands(operands, 2, "I2I.S16")
    writableRegister(operands.head, "I2I destination")
    if (VMOperand.has(operands.head, OperandSelector.`CC`)) fail("I2I.S16 does not support condition-code destinations")
    val sourceWidth = if (modifiers(1) == Modifier.`S8`) 8 else 16
    val source      = VMOperand.positive(operands(1))
    val negated     = VMOperand.negative(operands(1))
    val supported   = VMOperand.isZero(source) || VMOperand.isRegister(source, RegisterBank.R)
    if (!supported || (negated && sourceWidth != 16)) fail("I2I.S16.S8/S16 supports register operands only")
    val inputType = if (sourceWidth == 8) ConversionType.S8 else ConversionType.S16
    val extended  = context.temp("t")
    context.emit(Convert(extended.name, context.source(source), inputType, ConversionType.S32, None))
    val result = if (negated) {
      val negative = context.temp("t")
      context.emit(IntegerUnary(IntegerUnaryOperation.Negate, negative.name, extended, 32))
      val narrowed = context.temp("t")
      context.emit(Convert(narrowed.name, negative, ConversionType.S16, ConversionType.S32, None))
      narrowed
    } else extended
    context.store(operands.head, result)
    true
  }

  private def compileCompareSelect(
      context: VMCompilationContext,
      operation: SASSInstruction,
      operands: Vec[Operand],
      modifiers: Vec[Modifier]
  ): Boolean = {
    val opcode    = operation.mnemonic.raw
    val isCompare = operation match {
      case _: SASSInstruction.Icmp => true
      case _                       => false
    }
    val mods = if (isCompare)
      uniqueModifiers(modifiers, comparisons + Modifier.`U32`, opcode)
    else uniqueModifiers(modifiers, comparisons ++ booleans ++ Set(Modifier.`U32`, Modifier.`X`), opcode)
    if (isCompare) {
      if (operands.size != 4 || !mods.exists(comparisons)) fail("ICMP requires comparison modifier and four operands")
      val predicate = compare(
        context,
        modifiers.find(comparisons).get,
        context.source(operands(3)),
        Value.Immediate(0L),
        !mods(Modifier.`U32`)
      )
      val result = context.temp("t")
      context.emit(Select(
        result.name,
        context.source(operands(1)),
        context.source(operands(2)),
        predicate.name,
        32
      ))
      context.store(operands.head, result)
      true
    } else {
      if (operands.size != 4 || !mods.exists(comparisons) || !mods.exists(booleans))
        fail("ISET requires comparison and predicate-combination modifiers")
      val comparison = modifiers.find(comparisons).get
      val combineOp  = modifiers.find(booleans).get
      if (mods(Modifier.`X`) && combineOp != Modifier.`AND`) fail("ISET.X supports only AND predicate composition")
      val left     = context.source(operands(1))
      val right    = context.source(operands(2))
      val high     = compare(context, comparison, left, right, !mods(Modifier.`U32`))
      val extended = if (mods(Modifier.`X`)) {
        val low = comparison match {
          case Modifier.`EQ` => context.readConditionFlag(VMConditionCodes.Flag.Equal)
          case Modifier.`NE` =>
            val unequal = context.temp("q")
            context.emit(PredicateUnary(
              unequal.name,
              context.readConditionFlag(VMConditionCodes.Flag.Equal).name
            ))
            unequal
          case Modifier.`LT` | Modifier.`LE` =>
            val lowLess = context.readConditionFlag(VMConditionCodes.Flag.LessThan)
            if (comparison == Modifier.`LT`) lowLess
            else {
              val lowEqual = context.readConditionFlag(VMConditionCodes.Flag.Equal)
              val result   = context.temp("q")
              context.emit(PredicateBinary(PredicateOperation.Or, result.name, lowLess.name, lowEqual.name))
              result
            }
          case Modifier.`GT` | Modifier.`GE` =>
            val less   = context.readConditionFlag(VMConditionCodes.Flag.LessThan)
            val equal  = context.readConditionFlag(VMConditionCodes.Flag.Equal)
            val result = context.temp("q")
            if (comparison == Modifier.`GT`) {
              context.emit(PredicateBinary(PredicateOperation.Or, result.name, less.name, equal.name))
              context.emit(PredicateUnary(result.name, result.name))
            } else context.emit(PredicateUnary(result.name, less.name))
            result
          case other => fail(s"unsupported ISET.X comparison ${other.name}")
        }
        val highEqual     = compare(context, Modifier.`EQ`, left, right, !mods(Modifier.`U32`))
        val highDifferent = context.temp("q")
        context.emit(PredicateUnary(highDifferent.name, highEqual.name))
        val lowWhenEqual = context.temp("q")
        context.emit(PredicateBinary(PredicateOperation.And, lowWhenEqual.name, low.name, highEqual.name))
        val highWhenDifferent = context.temp("q")
        context.emit(PredicateBinary(
          PredicateOperation.And,
          highWhenDifferent.name,
          high.name,
          highDifferent.name
        ))
        val result = context.temp("q")
        context.emit(PredicateBinary(
          PredicateOperation.Or,
          result.name,
          lowWhenEqual.name,
          highWhenDifferent.name
        ))
        result
      } else high
      val sourcePredicate    = context.predicateSource(operands(3))
      val combined           = context.temp("q")
      val predicateOperation = combineOp match {
        case Modifier.`AND` => PredicateOperation.And
        case Modifier.`OR`  => PredicateOperation.Or
        case _              => PredicateOperation.Xor
      }
      context.emit(PredicateBinary(predicateOperation, combined.name, extended.name, sourcePredicate.name))
      val result = context.temp("t")
      context.emit(Select(
        result.name,
        Value.Immediate(0xffffffffL),
        Value.Immediate(0L),
        combined.name,
        32
      ))
      context.store(operands.head, result)
      if (VMOperand.has(operands.head, OperandSelector.`CC`)) context.setZeroFlag(result)
      true
    }
  }

  private def compileLegacyIadd3(
      context: VMCompilationContext,
      operands: Vec[Operand],
      modifiers: Vec[Modifier]
  ): Boolean = {
    val rs       = modifiers == Vec(Modifier.`RS`)
    val producer = modifiers.isEmpty && operands.headOption.exists(VMOperand.has(_, OperandSelector.`CC`))
    val consumer = modifiers == Vec(Modifier.`X`)
    if (!rs && !producer && !consumer) false
    else {
      requiredOperands(operands, 4, "legacy IADD3")
      writableRegister(operands.head, "IADD3 destination")
      val destinationModifiers = VMOperand.selectors(operands.head).filterNot(_ == OperandSelector.reuse)
      if (producer && destinationModifiers != Vec(OperandSelector.CC))
        fail("legacy IADD3.CC requires only a CC destination modifier")
      if (consumer && destinationModifiers.nonEmpty) fail("legacy IADD3.X does not support destination modifiers")
      if (!operands.drop(1).forall(source => isLegacyIadd3Source(source)))
        fail("legacy IADD3 has unsupported source operand")
      if (
        rs &&
        !operands.drop(1).forall(source => VMOperand.isRegister(source, RegisterBank.R) || VMOperand.isZero(source))
      )
        fail("legacy IADD3.RS supports register sources only")
      if (rs) {
        val leftWide  = context.temp("wide")
        val rightWide = context.temp("wide")
        context.emit(Convert(
          leftWide.name,
          context.source(operands(1)),
          ConversionType.U32,
          ConversionType.U64,
          None
        ))
        context.emit(Convert(
          rightWide.name,
          context.source(operands(2)),
          ConversionType.U32,
          ConversionType.U64,
          None
        ))
        val sum = context.temp("wide")
        context.emit(IntegerBinary(IntegerOperation.Add, sum.name, leftWide, rightWide, 64))
        val shifted = context.temp("wide")
        context.emit(IntegerBinary(
          IntegerOperation.ShiftRightLogical,
          shifted.name,
          sum,
          Value.Immediate(16L),
          64
        ))
        val addend = context.temp("wide")
        context.emit(Convert(
          addend.name,
          context.source(operands(3)),
          ConversionType.U32,
          ConversionType.U64,
          None
        ))
        context.emit(IntegerBinary(IntegerOperation.Add, shifted.name, shifted, addend, 64))
        val result = context.temp("t")
        context.emit(Convert(result.name, shifted, ConversionType.U64, ConversionType.U32, None))
        context.store(operands.head, result)
      } else {
        if (operands.drop(1).count(VMOperand.negative) > 1)
          fail("legacy IADD3 supports at most one negated source per instruction")
        val sources = operands.drop(1).map { operand =>
          val negated = VMOperand.negative(operand)
          val source  = legacyIadd3Source(context, operand)
          (source, negated)
        }
        val destination = VMOperand.without(operands.head, OperandSelector.`CC`)
        context.emit(IntegerAddThree(
          destinationName(context, destination),
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
      true
    }
  }

  private def destinationName(
      context: VMCompilationContext,
      operand: Operand
  ): NonEmpty[String] = context.source(operand) match {
    case Value.Register(name) => name
    case _                    => fail("legacy IADD3 destination must be a general register")
  }

  private def compileIscadd(
      context: VMCompilationContext,
      operands: Vec[Operand],
      modifiers: Vec[Modifier]
  ): Boolean =
    if (modifiers.nonEmpty || !operands.lift(1).exists(VMOperand.negative)) false
    else {
      requiredOperands(operands, 4, "ISCADD")
      if (
        !VMOperand.isRegister(operands.head, RegisterBank.R) || !VMOperand.negative(operands(1)) ||
        !VMOperand.isRegister(VMOperand.positive(operands(1)), RegisterBank.R) ||
        !(VMOperand.isRegister(operands(2), RegisterBank.R) || VMOperand.isZero(operands(2)))
      )
        fail("unsupported negated legacy ISCADD operands")
      val shift = VMOperand.integer(operands(3)).filter(value => value >= 0 && value <= 31)
        .getOrElse(fail("ISCADD shift must be between 0 and 31"))
      val scaled = context.temp()
      context.emit(IntegerBinary(
        IntegerOperation.ShiftLeft,
        scaled.name,
        context.source(VMOperand.positive(operands(1))),
        Value.Immediate(shift.toLong),
        32
      ))
      val result = context.temp()
      context.emit(IntegerBinary(
        IntegerOperation.Subtract,
        result.name,
        context.source(operands(2)),
        scaled,
        32
      ))
      context.store(operands.head, result)
      true
    }

  private def compileBitfield(
      context: VMCompilationContext,
      operation: SASSInstruction,
      operands: Vec[Operand],
      modifiers: Vec[Modifier]
  ): Boolean = {
    val opcode = operation.mnemonic.raw
    if (operation match { case _: SASSInstruction.Bfe => true; case _ => false }) {
      val mods = uniqueModifiers(modifiers, Set(Modifier.`U32`, Modifier.`S32`, Modifier.`BREV`), opcode)
      requiredOperands(operands, 3, "BFE")
      if (mods(Modifier.`U32`) && mods(Modifier.`S32`)) fail("BFE cannot be both signed and unsigned")
      val input = if (mods(Modifier.`BREV`)) {
        val reversed = context.temp()
        context.emit(IntegerUnary(
          IntegerUnaryOperation.BitReverse,
          reversed.name,
          context.source(operands(1)),
          32
        ))
        reversed
      } else context.source(operands(1))
      val (position, width) = field(context, operands(2))
      val result            = context.temp()
      context.emit(BitFieldExtract(result.name, input, position, width, 32, !mods(Modifier.`U32`)))
      context.store(operands.head, result)
    } else {
      val _ = uniqueModifiers(modifiers, Set.empty, opcode)
      requiredOperands(operands, 4, "BFI")
      val (position, width) = field(context, operands(2))
      val result            = context.temp()
      context.emit(BitFieldInsert(
        result.name,
        context.source(operands(1)),
        context.source(operands(3)),
        position,
        width,
        32
      ))
      context.store(operands.head, result)
    }
    true
  }

  private def field(context: VMCompilationContext, operand: Operand): (Value, Value) =
    VMOperand.integer(operand) match {
      case Some(value) if value >= 0 && value <= 0xffff =>
        (Value.Immediate((value & 0xff).toLong), Value.Immediate(((value >> 8) & 0xff).toLong))
      case Some(_)                           => fail("bit-field descriptor exceeds 16 bits")
      case None if VMOperand.isZero(operand) => (Value.Immediate(0L), Value.Immediate(0L))
      case None                              =>
        val packed   = context.source(operand)
        val position = context.temp(); val width = context.temp()
        context.emit(IntegerBinary(
          IntegerOperation.BitwiseAnd,
          position.name,
          packed,
          Value.Immediate(0xffL),
          32
        ))
        context.emit(IntegerBinary(
          IntegerOperation.ShiftRightLogical,
          width.name,
          packed,
          Value.Immediate(8L),
          32
        ))
        context.emit(IntegerBinary(
          IntegerOperation.BitwiseAnd,
          width.name,
          width,
          Value.Immediate(0xffL),
          32
        ))
        (position, width)
    }

  private def compileExtendedMinMax(
      context: VMCompilationContext,
      operands: Vec[Operand],
      modifiers: Vec[Modifier]
  ): Boolean = {
    val mods = uniqueModifiers(modifiers, Set(Modifier.`U32`, Modifier.`XHI`, Modifier.`XLO`), "IMNMX")
    val high = mods(Modifier.`XHI`)
    val low  = mods(Modifier.`XLO`)
    if (high == low) fail("IMNMX requires exactly one of XHI or XLO")
    requiredOperands(operands, 4, "extended IMNMX")
    val selectA = operands(3) match {
      case Operand.Predicate(None, false, false) => true
      case Operand.Predicate(None, false, true)  => false
      case Operand.Predicate(None, true, false)  => true
      case Operand.Predicate(None, true, true)   => false
      case _                                     => fail("extended IMNMX supports only PT and !PT selection")
    }
    val signed = !(mods(Modifier.`U32`) || low)
    val a      = context.source(operands(1))
    val b      = context.source(operands(2))
    if (high) {
      if (!VMOperand.has(operands.head, OperandSelector.`CC`)) fail("IMNMX.XHI must write CC")
      val less  = compare(context, Modifier.`LT`, a, b, signed)
      val equal = compare(context, Modifier.`EQ`, a, b, signed)
      context.emit(WriteConditionFlag(VMConditionCodes.Flag.LessThan, Value.Predicate(less.name)))
      context.emit(WriteConditionFlag(VMConditionCodes.Flag.Equal, Value.Predicate(equal.name)))
    } else if (VMOperand.has(operands.head, OperandSelector.`CC`)) fail("IMNMX.XLO does not write CC")
    val chooseA = if (high) {
      val less    = compare(context, Modifier.`LT`, a, b, signed)
      val greater = context.temp("q")
      context.emit(PredicateUnary(greater.name, less.name))
      val equal    = compare(context, Modifier.`EQ`, a, b, signed)
      val notEqual = context.temp("q")
      context.emit(PredicateUnary(notEqual.name, equal.name))
      val min = context.temp("q")
      context.emit(PredicateBinary(PredicateOperation.Or, min.name, less.name, equal.name))
      if (selectA) min else greater
    } else {
      val lowCmp = context.temp("q")
      context.emit(SetPredicate(
        lowCmp.name,
        if (selectA) Comparison.LessThanOrEqual else Comparison.GreaterThanOrEqual,
        ConversionType.U32.format,
        a,
        b
      ))
      val highEqual = context.readConditionFlag(VMConditionCodes.Flag.Equal)
      val tie       = context.temp("q")
      context.emit(PredicateBinary(PredicateOperation.And, tie.name, highEqual.name, lowCmp.name))
      val highLess = context.readConditionFlag(VMConditionCodes.Flag.LessThan)
      val highPick = if (selectA) highLess
      else {
        val highGreater = context.temp("q")
        context.emit(PredicateUnary(highGreater.name, highLess.name))
        val highNotEqual = context.temp("q")
        context.emit(PredicateUnary(highNotEqual.name, highEqual.name))
        val result = context.temp("q")
        context.emit(PredicateBinary(
          PredicateOperation.And,
          result.name,
          highGreater.name,
          highNotEqual.name
        ))
        result
      }
      val result = context.temp("q")
      context.emit(PredicateBinary(PredicateOperation.Or, result.name, highPick.name, tie.name))
      result
    }
    val result = context.temp()
    context.emit(Select(result.name, a, b, chooseA.name, 32))
    context.store(VMOperand.without(operands.head, OperandSelector.`CC`), result)
    true
  }

  private def compileSgxt(
      context: VMCompilationContext,
      operands: Vec[Operand],
      modifiers: Vec[Modifier]
  ): Boolean = {
    if (context.sm != 70 || context.instruction.guard.nonEmpty)
      fail("SGXT is supported only for the observed unpredicated sm_70 form")
    val _ = uniqueModifiers(modifiers, Set(Modifier.`U32`), "SGXT")
    requiredOperands(operands, 3, "SGXT")
    val length = VMOperand.integer(operands(2)) match {
      case Some(value) if value >= 0 && value <= 0x1ff => Value.Immediate(value.toLong)
      case Some(_) => fail("SGXT immediate width exceeds the measured 9-bit operand")
      case None if VMOperand.isRegister(operands(2), RegisterBank.R) =>
        val width = context.temp()
        context.emit(IntegerBinary(
          IntegerOperation.Minimum(false),
          width.name,
          context.source(operands(2)),
          Value.Immediate(32L),
          32
        ))
        width
      case _ => fail("SGXT width must be a general register or non-negative immediate")
    }
    val bounded = context.temp()
    context.emit(IntegerBinary(
      IntegerOperation.Minimum(false),
      bounded.name,
      length,
      Value.Immediate(32L),
      32
    ))
    val result = context.temp()
    context.emit(BitFieldExtract(
      result.name,
      context.source(operands(1)),
      Value.Immediate(0L),
      bounded,
      32,
      !modifiers.contains(Modifier.`U32`)
    ))
    context.store(operands.head, result)
    true
  }

  private def compileXmad(
      context: VMCompilationContext,
      operands: Vec[Operand],
      modifiers: Vec[Modifier]
  ): Boolean = {
    if (context.sm != 50 && context.sm != 60) fail(s"unsupported XMAD architecture sm_${context.sm}")
    val allowed =
      Set(Modifier.`MRG`, Modifier.`PSL`, Modifier.`CBCC`, Modifier.`CLO`, Modifier.`CHI`, Modifier.`CSFU`) ++ xmadTypes
    val repeated = modifiers.groupBy(identity).collect {
      case (modifier, values) if values.size > 1 && modifier != Modifier.`S16` => modifier
    }
    if (repeated.nonEmpty || modifiers.count(_ == Modifier.`S16`) > 2 || modifiers.exists(m => !allowed(m)))
      fail(s"unsupported XMAD modifiers ${modifiers.mkString(".")}")
    val mods = modifiers.toSet
    requiredOperands(operands, 4, "XMAD")
    if (VMOperand.has(operands.head, OperandSelector.`CC`)) fail("XMAD does not support condition-code destinations")
    val signedTypes = modifiers.filter(xmadTypes)
    val signs       = signedTypes match {
      case Vec()                               => (false, false)
      case Vec(Modifier.`S16`, Modifier.`U16`) => (true, false)
      case Vec(Modifier.`U16`, Modifier.`S16`) => (false, true)
      case Vec(Modifier.`S16`, Modifier.`S16`) => (true, true)
      case _                                   => fail("unsupported XMAD operand signedness")
    }
    val modes = modifiers.filter(Set(Modifier.`CLO`, Modifier.`CHI`))
    if (modes.size > 1) fail("XMAD cannot combine CLO and CHI")
    if (mods(Modifier.`CBCC`) && (!VMOperand.isRegister(operands(2), RegisterBank.R) || VMOperand.isZero(operands(2))))
      fail("XMAD.CBCC requires a general-register second source")
    if (mods(Modifier.`MRG`) && modifiers.exists(Set(Modifier.`PSL`, Modifier.`CBCC`, Modifier.`CLO`, Modifier.`CHI`)))
      fail("unsupported XMAD.MRG modifier combination")
    val a = halfOperand(context, operands(1), signs._1)
    val b = halfOperand(context, operands(2), signs._2)
    if (
      mods(Modifier.`CSFU`) && (
        signedTypes != Vec(Modifier.`S16`, Modifier.`S16`) || !a.high || !b.high || !VMOperand.isZero(operands(3)) ||
          modifiers.exists(modifier =>
            Set(Modifier.`MRG`, Modifier.`PSL`, Modifier.`CBCC`, Modifier.`CLO`, Modifier.`CHI`)(modifier)
          )
      )
    ) fail("XMAD.CSFU is bounded to compiler-observed S16.S16 H1,H1,RZ form")
    val secondBase       = VMOperand.base(operands(2))
    val secondIsConstant = secondBase match {
      case Operand.ConstantBank(_, _) => true
      case _                          => false
    }
    if (
      mods(Modifier.`MRG`) && !(VMOperand.isRegister(secondBase, RegisterBank.R, allowZero = true) || secondIsConstant)
    )
      fail("XMAD.MRG requires a register or constant-bank second source")
    val product = context.temp()
    context.emit(IntegerBinary(IntegerOperation.MultiplyLow, product.name, a.selected, b.selected, 32))
    val scaled = if (mods(Modifier.`PSL`)) {
      val result = context.temp()
      context.emit(IntegerBinary(
        IntegerOperation.ShiftLeft,
        result.name,
        product,
        Value.Immediate(16L),
        32
      ))
      result
    } else product
    val c      = context.source(operands(3))
    val addend = modes.headOption match {
      case Some(Modifier.CLO) =>
        val result = context.temp()
        context.emit(IntegerBinary(
          IntegerOperation.BitwiseAnd,
          result.name,
          c,
          Value.Immediate(65535L),
          32
        ))
        result
      case Some(Modifier.CHI) =>
        val result = context.temp()
        context.emit(IntegerBinary(
          IntegerOperation.ShiftRightLogical,
          result.name,
          c,
          Value.Immediate(16L),
          32
        ))
        result
      case _ => c
    }
    val sum = context.temp()
    context.emit(IntegerBinary(IntegerOperation.Add, sum.name, scaled, addend, 32))
    val adjusted = if (mods(Modifier.`CSFU`)) csfuCorrection(context, a, b, sum) else sum
    val result   = if (mods(Modifier.`CBCC`)) {
      val bHigh = context.temp()
      context.emit(IntegerBinary(IntegerOperation.ShiftLeft, bHigh.name, b.raw, Value.Immediate(16L), 32))
      val combined = context.temp()
      context.emit(IntegerBinary(IntegerOperation.Add, combined.name, adjusted, bHigh, 32))
      combined
    } else if (mods(Modifier.`MRG`)) {
      val low = context.temp()
      context.emit(IntegerBinary(
        IntegerOperation.BitwiseAnd,
        low.name,
        adjusted,
        Value.Immediate(65535L),
        32
      ))
      val bHigh = context.temp()
      context.emit(IntegerBinary(IntegerOperation.ShiftLeft, bHigh.name, b.raw, Value.Immediate(16L), 32))
      val combined = context.temp()
      context.emit(IntegerBinary(IntegerOperation.BitwiseOr, combined.name, low, bHigh, 32))
      combined
    } else adjusted
    context.store(operands.head, result)
    true
  }

  private case class Half(selected: Value, raw: Value, high: Boolean)

  private def halfOperand(context: VMCompilationContext, operand: Operand, signed: Boolean): Half = {
    val selectors = VMOperand.selectors(VMOperand.without(operand, OperandSelector.`reuse`))
    if (selectors.exists(value => value != OperandSelector.H0 && value != OperandSelector.H1))
      fail(s"unsupported XMAD source selectors $selectors")
    val high = selectors.contains(OperandSelector.H1)
    if (selectors.size > 1) fail(s"unsupported XMAD source selectors $selectors")
    val positive = VMOperand.positive(operand)
    val base     = VMOperand.base(positive)
    val constant = base match {
      case Operand.ConstantBank(_, _) => true
      case _                          => false
    }
    val immediate = VMOperand.integer(base).nonEmpty
    if (!(VMOperand.isRegister(base, RegisterBank.R, allowZero = true) || constant || immediate))
      fail(s"unsupported XMAD source $operand")
    val sourceValue = context.source(base)
    val raw         = if (VMOperand.negative(operand)) {
      val negated = context.temp()
      context.emit(IntegerUnary(IntegerUnaryOperation.Negate, negated.name, sourceValue, 32))
      negated
    } else sourceValue
    val selected = context.temp()
    context.emit(if (high)
      IntegerBinary(IntegerOperation.ShiftRightLogical, selected.name, raw, Value.Immediate(16L), 32)
    else IntegerBinary(IntegerOperation.BitwiseAnd, selected.name, raw, Value.Immediate(65535L), 32))
    val value = if (!signed) selected
    else {
      val shifted = context.temp()
      context.emit(IntegerBinary(
        IntegerOperation.ShiftLeft,
        shifted.name,
        selected,
        Value.Immediate(16L),
        32
      ))
      val extended = context.temp()
      context.emit(IntegerBinary(
        IntegerOperation.ShiftRightArithmetic,
        extended.name,
        shifted,
        Value.Immediate(16L),
        32
      ))
      extended
    }
    Half(value, raw, high)
  }

  private def csfuCorrection(context: VMCompilationContext, a: Half, b: Half, sum: Value): Value = {
    def sign(value: Value): Value = {
      val result = context.temp()
      context.emit(IntegerBinary(
        IntegerOperation.ShiftRightLogical,
        result.name,
        value,
        Value.Immediate(31L),
        32
      ))
      result
    }
    def low(value: Value): Value = {
      val result = context.temp()
      context.emit(IntegerBinary(
        IntegerOperation.BitwiseAnd,
        result.name,
        value,
        Value.Immediate(65535L),
        32
      ))
      result
    }
    def nonzero(value: Value): Value.Register = {
      val result = context.temp("q")
      context.emit(SetPredicate(
        result.name,
        Comparison.NotEqual,
        ConversionType.U32.format,
        value,
        Value.Immediate(0L)
      ))
      result
    }
    def negative(value: Value): Value.Register = nonzero(value)
    val signA                                  = sign(a.selected)
    val signB                                  = sign(b.selected)
    val signANegative                          = negative(signA)
    val signBNegative                          = negative(signB)
    val lowANonzero                            = nonzero(low(a.raw))
    val lowBNonzero                            = nonzero(low(b.raw))
    val adjustA                                = context.temp("q")
    val adjustB                                = context.temp("q")
    context.emit(PredicateBinary(
      PredicateOperation.And,
      adjustA.name,
      signANegative.name,
      lowBNonzero.name
    ))
    context.emit(PredicateBinary(
      PredicateOperation.And,
      adjustB.name,
      signBNegative.name,
      lowANonzero.name
    ))
    def correction(predicate: Value.Register): Value = {
      val value = context.temp()
      context.emit(Select(value.name, Value.Immediate(65536L), Value.Immediate(0L), predicate.name, 32))
      value
    }
    val magnitude = context.temp()
    context.emit(IntegerBinary(
      IntegerOperation.Add,
      magnitude.name,
      correction(adjustA),
      correction(adjustB),
      32
    ))
    val negativeCorrection = context.temp()
    context.emit(IntegerBinary(
      IntegerOperation.Subtract,
      negativeCorrection.name,
      Value.Immediate(0L),
      magnitude,
      32
    ))
    val result = context.temp()
    context.emit(IntegerBinary(IntegerOperation.Add, result.name, sum, negativeCorrection, 32))
    result
  }
}
