package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.isa.nvidia.VMIR.Instruction.*
import net.kurobako.gemstone.isa.nvidia.VMIR.Matrix.*
import net.kurobako.gemstone.isa.sass.Instruction.*
import net.kurobako.gemstone.isa.sass.Instruction as SASSInstruction

/** Matrix multiply-accumulate and shared matrix loads. */
private object VMTensorCompiler {

  def lower(using context: VMCompilationContext): Boolean = {
    import context.*
    (operation: @unchecked) match {
      case _: SASSInstruction.Dmma =>
        doubleMatrix()
        true
      case _: SASSInstruction.Hmma if modifiers.contains(Modifier.`884`) =>
        voltaMatrix()
        true
      case _: SASSInstruction.Hmma =>
        standardMatrix()
        true
      case _: SASSInstruction.Ldsm =>
        if (sm < 75 || operands.size != 2 || !OperandQueries.isRegister(operands.head))
          unsupported("unsupported shared matrix load operands")
        val layout = modifiers.lift(1).getOrElse(unsupported("missing shared matrix layout"))
        val count  = modifiers.drop(2) match {
          case Vec()             => 1
          case Vec(Modifier.`2`) => 2
          case Vec(Modifier.`4`) => 4
          case _                 => unsupported("unsupported shared matrix load count")
        }
        if (modifiers.headOption != Some(Modifier.`16`) || !Set(Modifier.M88, Modifier.MT88)(layout))
          unsupported("unsupported shared matrix load shape")
        val pointer = address(operands(1), global = false, sharedUniform = true, sharedAccess = true)
        emit(Tensor(Operation.LoadMatrix(
          Vec.tabulate(count)(destination(operands.head, _).slot),
          pointer,
          layout == Modifier.MT88,
          MemorySpace.Shared
        )))
        true
      case _ => false
    }
  }

  private def doubleMatrix()(using context: VMCompilationContext): Unit = {
    import context.*
    if (modifiers != Vec(Modifier.`884`) || sm < 80 || operands.size != 4 || !bareRegister(operands.head))
      unsupported("unsupported double tensor operation")
    val left   = doubleRegister(operands(1))
    val right  = doubleRegister(operands(2))
    val addend = Vec(doubleRegister(operands(3)), doubleRegister(offsetRegister(operands(3), 2)))
    val result = Vec(temp(), temp())
    emit(matrix(
      Shape(8, 8, 4),
      Element.F64,
      Element.F64,
      Element.F64,
      aColumnMajor = false,
      bColumnMajor = true,
      result,
      Vec(left),
      Vec(right),
      addend
    ))
    result.zipWithIndex.foreach((value, index) => store(offsetRegister(operands.head, index * 2), value, 64))
  }

  private def voltaMatrix()(using context: VMCompilationContext): Unit = {
    import context.*
    if (instruction.guard.nonEmpty) unsupported("predicated HMMA.884 is not implemented")
    if (sm != 70 && sm != 75) unsupported(s"HMMA.884 requires sm_70 or sm_75, found sm_$sm")
    requireOperands(4, "HMMA.884 requires destination, A fragment, B fragment and accumulator")
    val (slots, accumulator, step) = (modifiers match {
      case Vec(Modifier.`884`, Modifier.F32, Modifier.F32, value) =>
        stepNumber(value).filter(_ <= 3).map((8, Element.F32, _))
      case Vec(Modifier.`884`, Modifier.F16, Modifier.F16, value) =>
        stepNumber(value).filter(_ <= 1).map((4, Element.F16, _))
      case _ => None
    }).getOrElse(unsupported(s"unsupported HMMA.884 modifiers ${modifiers.mkString(".")}"))
    if (!bareRegister(operands.head)) unsupported("unsupported HMMA.884 destination")
    val destination = registerBase(operands.head, "HMMA.884 destination")
    if (destination.index.raw > 253)
      unsupported("HMMA.884 destination register pair exceeds the physical register file")
    val (leftBase, leftColumnMajor)   = matrixSource(operands(1), "A")
    val (rightBase, rightColumnMajor) = matrixSource(operands(2), "B")
    if (!bareRegister(operands(3), allowZero = true)) unsupported("unsupported HMMA.884 accumulator")
    val results = Vec.fill(slots)(temp())
    val zero    = temp()
    emit(Move(zero.slot, Value.Immediate(0L), 32))
    val addend = Vec.tabulate(slots) { index =>
      if (index >= step * 2 && index < step * 2 + 2 && !OperandQueries.isZero(operands(3)))
        source(offsetRegister(operands(3), index - step * 2))
      else zero
    }
    def pair(base: Operand.Register) = Vec(source(base), source(offsetRegister(base, 1)))
    emit(matrix(
      Shape(8, 8, 4),
      result = accumulator,
      input = Element.F16,
      accumulator,
      aColumnMajor = leftColumnMajor,
      bColumnMajor = rightColumnMajor,
      results,
      left = pair(leftBase),
      right = pair(rightBase),
      addend
    ))
    store(destination, results(step * 2))
    store(offsetRegister(destination, 1), results(step * 2 + 1))
  }

  private def standardMatrix()(using context: VMCompilationContext): Unit = {
    import context.*
    val shapes = modifiers.filter(Set(Modifier.`16816`, Modifier.`1688`))
    if (shapes.size != 1) unsupported("HMMA requires exactly one supported shape")
    val k: Int > 0 = if (shapes.head == Modifier.`16816`) 16 else 8
    val element    =
      if (modifiers.contains(Modifier.BF16)) Element.BF16
      else if (modifiers.contains(Modifier.TF32)) Element.TF32
      else Element.F16
    if (modifiers.contains(Modifier.F16) && modifiers.exists(Set(Modifier.F32, Modifier.BF16, Modifier.TF32)))
      unsupported("HMMA F16 accumulation cannot be combined with other type modifiers")
    requireOperands(4, "HMMA requires destination, A, B, and accumulator fragments")
    val (accumulator, words, aCount) =
      if (modifiers.contains(Modifier.F16)) {
        if (!bareRegister(operands.head) || element != Element.F16)
          unsupported("unsupported HMMA F16 accumulation operands")
        if ((k.raw == 8 && sm < 75) || (k.raw == 16 && sm < 80))
          unsupported(s"HMMA.168$k.F16 requires sm_${if (k.raw == 8) 75 else 80} or higher")
        (Element.F16, 2, if (k.raw == 16) 4 else 2)
      } else {
        if (!modifiers.contains(Modifier.F32)) unsupported("HMMA requires F32 or verified F16 accumulation")
        (Element.F32, 4, if (element == Element.TF32) k.raw / 2 else k.raw / 4)
      }
    emit(matrix(
      Shape(16, 8, k),
      accumulator,
      element,
      accumulator,
      aColumnMajor = false,
      bColumnMajor = true,
      registerSpan(operands.head, words),
      registerSpan(operands(1), aCount),
      registerSpan(operands(2), aCount / 2),
      registerSpan(operands(3), words)
    ))
  }

  private def registerSpan(operand: Operand, count: Int)(using context: VMCompilationContext): Vec[Value.Register] = {
    import context.*
    if (!bareRegister(operand, allowZero = true)) unsupported("unsupported matrix fragment register")
    val base = registerBase(operand, "matrix fragment register", allowZero = true)
    Vec.tabulate(count) { offset =>
      if (OperandQueries.isZero(operand)) {
        val zero = temp()
        emit(Move(zero.slot, Value.Immediate(0L), 32))
        zero
      } else destination(offsetRegister(base, offset))
    }
  }

  private def doubleRegister(operand: Operand)(using context: VMCompilationContext): Value = {
    import context.*
    if (!bareRegister(operand, allowZero = true)) unsupported("unsupported DMMA register")
    if (OperandQueries.isZero(operand)) {
      val zero = temp()
      emit(Move(zero.slot, Value.Immediate(0L), 64))
      zero
    } else source(registerBase(operand, "DMMA register"), 64)
  }

  private def matrixSource(operand: Operand, name: String)(using
      context: VMCompilationContext
  ): (Operand.Register, Boolean) = {
    val columnMajor = OperandQueries.qualifiers(operand) match {
      case Vec(OperandSelector.ROW) => false
      case Vec(OperandSelector.COL) => true
      case _                        => context.unsupported(s"unsupported HMMA.884 $name layout")
    }
    val register = context.registerBase(operand, s"HMMA.884 $name operand")
    if (register.index.raw > 253)
      context.unsupported(s"HMMA.884 $name register pair exceeds the physical register file")
    register -> columnMajor
  }

  private def offsetRegister(operand: Operand, offset: Int)(using context: VMCompilationContext): Operand.Register =
    context.registerBase(operand, "register pair", allowZero = true) match {
      case zero if OperandQueries.isZero(zero) => OperandQueries.Zero
      case Operand.Register(bank, index)       =>
        (index.raw + offset).refine[Int in 0 ~ 255].fold(
          _ => context.unsupported("register pair exceeds the physical register file"),
          Operand.Register(bank, _)
        )
    }

  private def stepNumber(value: Modifier): Option[Int] = value match {
    case Modifier.STEP0 => Some(0)
    case Modifier.STEP1 => Some(1)
    case Modifier.STEP2 => Some(2)
    case Modifier.STEP3 => Some(3)
    case _              => None
  }

  /** D = A * B + C with A and B sharing `input`, checked against the shared MMA rules and fragment sizes. */
  private def matrix(
      shape: Shape,
      result: Element,
      input: Element,
      accumulator: Element,
      aColumnMajor: Boolean,
      bColumnMajor: Boolean,
      destinations: Vec[Value.Register],
      left: Vec[Value],
      right: Vec[Value],
      addend: Vec[Value]
  )(using context: VMCompilationContext): VMIR.Instruction = {
    if (!Matrix.supportsMultiply(shape, input, input, accumulator, result, aColumnMajor, bColumnMajor, wmma = false))
      context.unsupported(s"unsupported MMA type/layout combination: $shape $input/$accumulator/$result")
    val a        = Fragment(shape, Role.A, input, wmma = false, columnMajor = aColumnMajor)
    val b        = Fragment(shape, Role.B, input, wmma = false, columnMajor = bColumnMajor)
    val c        = Fragment(shape, Role.C, accumulator, wmma = false)
    val d        = Fragment(shape, Role.C, result, wmma = false)
    val expected = Vec(d.registers.raw, a.registers.raw, b.registers.raw, c.registers.raw)
    val actual   = Vec(destinations.size, left.size, right.size, addend.size)
    if (actual != expected) context.unsupported(s"MMA fragment register counts $actual do not match $expected")
    Tensor(Operation.Multiply(a, b, c, d, destinations.map(_.slot), left, right, addend, saturate = false))
  }
}
