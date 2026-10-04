package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.isa.nvidia.VMIR.Instruction.*
import net.kurobako.gemstone.isa.nvidia.VMTensor.*
import net.kurobako.gemstone.isa.sass.Instruction.*
import net.kurobako.gemstone.isa.sass.Instruction as SASSInstruction

/** Typed VMIR adapters for the tensor instructions lowered from SASS. */
private object VMTensorCompiler {
  def compile(context: VMCompilationContext): Boolean = context.instruction.operation match {
    case _: SASSInstruction.Dmma =>
      lowerDoubleMatrix(context, context.instruction.modifiers, context.instruction.operands)
    case _: SASSInstruction.Hmma =>
      lowerHalfMatrix(context, context.instruction.modifiers, context.instruction.operands)
    case _ => false
  }

  private def lowerDoubleMatrix(
      context: VMCompilationContext,
      modifiers: Vec[Modifier],
      operands: Vec[Operand]
  ): Boolean = {
    if (modifiers != Vec(Modifier.`884`) || context.sm < 80 || operands.size != 4 || !generalRegister(operands.head))
      unsupported("unsupported double tensor operation")
    val left   = doubleRegister(context, operands(1))
    val right  = doubleRegister(context, operands(2))
    val addend = Vec(doubleRegister(context, operands(3)), doubleRegister(context, offsetRegister(operands(3), 2)))
    val result = Vec(context.temp("wide"), context.temp("wide"))
    context.emit(matrix(
      Shape(8, 8, 4),
      Element.F64,
      Element.F64,
      Element.F64,
      Element.F64,
      false,
      true,
      result,
      Vec(left),
      Vec(right),
      addend
    ))
    result.zipWithIndex.foreach { (value, index) =>
      context.store(offsetRegister(operands.head, index * 2), value, 64)
    }
    true
  }

  private def lowerHalfMatrix(
      context: VMCompilationContext,
      modifiers: Vec[Modifier],
      operands: Vec[Operand]
  ): Boolean =
    if (modifiers.contains(Modifier.`884`)) lowerVoltaMatrix(context, modifiers, operands)
    else lowerStandardMatrix(context, modifiers, operands)

  private def lowerVoltaMatrix(
      context: VMCompilationContext,
      modifiers: Vec[Modifier],
      operands: Vec[Operand]
  ): Boolean = {
    if (context.instruction.guard.nonEmpty) unsupported("predicated HMMA.884 is not implemented")
    if (context.sm != 70 && context.sm != 75) unsupported(s"HMMA.884 requires sm_70 or sm_75, found sm_${context.sm}")
    requireSize(operands, 4, "HMMA.884 requires destination, A fragment, B fragment and accumulator")
    val decoded = modifiers match {
      case Vec(Modifier.`884`, Modifier.F32, Modifier.F32, value) =>
        stepNumber(value).filter(_ <= 3).map(step => (8, Element.F32, Element.F32, step))
      case Vec(Modifier.`884`, Modifier.F16, Modifier.F16, value) =>
        stepNumber(value).filter(_ <= 1).map(step => (4, Element.F16, Element.F16, step))
      case _ => None
    }
    val (slots, dElement, cElement, step) = decoded.getOrElse(
      unsupported(s"unsupported HMMA.884 modifiers ${modifiers.mkString(".")}")
    )
    if (!generalRegister(operands.head)) unsupported("unsupported HMMA.884 destination")
    val destination = registerBase(operands.head, "HMMA.884 destination")
    if (destination.index.raw > 253)
      unsupported("HMMA.884 destination register pair exceeds the physical register file")
    val (leftBase, leftColumnMajor)   = matrixSource(operands(1), "A")
    val (rightBase, rightColumnMajor) = matrixSource(operands(2), "B")
    if (!bareRegisterOrZero(operands(3))) unsupported("unsupported HMMA.884 accumulator")
    val results = Vec.fill(slots)(context.temp())
    val zero    = context.temp()
    context.emit(Move(zero.name, Value.Immediate(0L), 32))
    val addend = Vec.tabulate(slots) { index =>
      if (index >= step * 2 && index < step * 2 + 2)
        if (VMOperand.isZero(operands(3))) zero
        else context.source(offsetRegister(operands(3), index - step * 2))
      else zero
    }
    val left  = Vec(context.source(leftBase), context.source(offsetRegister(leftBase, 1)))
    val right = Vec(context.source(rightBase), context.source(offsetRegister(rightBase, 1)))
    context.emit(matrix(
      Shape(8, 8, 4),
      dElement,
      Element.F16,
      Element.F16,
      cElement,
      leftColumnMajor,
      rightColumnMajor,
      results,
      left,
      right,
      addend
    ))
    context.store(destination, results(step * 2))
    context.store(offsetRegister(destination, 1), results(step * 2 + 1))
    true
  }

  private def lowerStandardMatrix(
      context: VMCompilationContext,
      modifiers: Vec[Modifier],
      operands: Vec[Operand]
  ): Boolean = {
    val shapes = modifiers.filter(Set(Modifier.`16816`, Modifier.`1688`))
    if (shapes.size != 1) unsupported("HMMA requires exactly one supported shape")
    val k: Int > 0 = if (shapes.head == Modifier.`16816`) 16 else 8
    val element    =
      if (modifiers.contains(Modifier.`BF16`)) Element.BF16
      else if (modifiers.contains(Modifier.`TF32`)) Element.TF32
      else Element.F16
    if (modifiers.contains(Modifier.`F16`) && modifiers.exists(Set(Modifier.`F32`, Modifier.`BF16`, Modifier.`TF32`)))
      unsupported("HMMA F16 accumulation cannot be combined with other type modifiers")

    requireSize(operands, 4, "HMMA requires destination, A, B, and accumulator fragments")
    if (modifiers.contains(Modifier.`F16`)) {
      if (!generalRegister(operands.head)) unsupported("unsupported HMMA F16 accumulation operands")
      if (element != Element.F16) unsupported("unsupported HMMA F16 accumulation operands")
      if ((k.raw == 8 && context.sm < 75) || (k.raw == 16 && context.sm < 80))
        unsupported(s"HMMA.168$k.F16 requires sm_${if (k.raw == 8) 75 else 80} or higher")
      val aCount = if (k.raw == 16) 4 else 2
      val bCount = if (k.raw == 16) 2 else 1
      context.emit(matrix(
        Shape(16, 8, k),
        Element.F16,
        Element.F16,
        Element.F16,
        Element.F16,
        false,
        true,
        registerSpan(context, operands.head, 2),
        registerSpan(context, operands(1), aCount),
        registerSpan(context, operands(2), bCount),
        registerSpan(context, operands(3), 2)
      ))
    } else {
      if (!modifiers.contains(Modifier.`F32`)) unsupported("HMMA requires F32 or verified F16 accumulation")
      val aCount = if (element == Element.TF32) k.raw / 2 else k.raw / 4
      val bCount = aCount / 2
      context.emit(matrix(
        Shape(16, 8, k),
        Element.F32,
        element,
        element,
        Element.F32,
        false,
        true,
        registerSpan(context, operands.head, 4),
        registerSpan(context, operands(1), aCount),
        registerSpan(context, operands(2), bCount),
        registerSpan(context, operands(3), 4)
      ))
    }
    true
  }

  private def registerSpan(context: VMCompilationContext, operand: Operand, count: Int): Vec[Value.Register] = {
    if (!bareRegisterOrZero(operand)) unsupported("unsupported matrix fragment register")
    val base = registerBase(operand, "matrix fragment register", allowZero = true)
    Vec.tabulate(count) { offset =>
      if (VMOperand.isZero(operand)) {
        val zero = context.temp()
        context.emit(Move(zero.name, Value.Immediate(0L), 32))
        zero
      } else context.physicalRegister(offsetRegister(base, offset))
    }
  }

  private def doubleRegister(context: VMCompilationContext, operand: Operand): Value = {
    if (!bareRegisterOrZero(operand)) unsupported("unsupported DMMA register")
    if (VMOperand.isZero(operand)) {
      val zero = context.temp("wide")
      context.emit(Move(zero.name, Value.Immediate(0L), 64))
      zero
    } else context.source(registerBase(operand, "DMMA register"), 64)
  }

  private def matrixSource(operand: Operand, name: String): (Operand.Register, Boolean) = {
    val selectors   = VMOperand.selectors(VMOperand.without(operand, OperandSelector.`reuse`))
    val columnMajor = selectors match {
      case Vec(OperandSelector.ROW) => false
      case Vec(OperandSelector.COL) => true
      case _                        => unsupported(s"unsupported HMMA.884 $name layout")
    }
    val register = registerBase(operand, s"HMMA.884 $name operand")
    if (register.index.raw > 253) unsupported(s"HMMA.884 $name register pair exceeds the physical register file")
    register -> columnMajor
  }

  @annotation.tailrec
  private def registerBase(operand: Operand, name: String, allowZero: Boolean = false): Operand.Register =
    VMOperand.without(operand, OperandSelector.`reuse`) match {
      case value: Operand.Register
          if value.bank == RegisterBank.R &&
            (allowZero || !VMOperand.isZero(value)) => value
      case Operand.Qualified(inner, _) => registerBase(inner, name, allowZero)
      case _                           => unsupported(s"unsupported $name")
    }

  private def offsetRegister(operand: Operand, offset: Int): Operand.Register =
    registerBase(operand, "register pair", allowZero = true) match {
      case Operand.Register(RegisterBank.R, index) if index.raw == 255 => VMOperand.Zero
      case Operand.Register(bank, index)                               =>
        (index.raw + offset).refine[Int in 0 ~ 255].fold(
          _ => unsupported("register pair exceeds the physical register file"),
          Operand.Register(bank, _)
        )
    }

  private def generalRegister(operand: Operand): Boolean =
    operandSelectors(operand).isEmpty && registerBaseIsGeneral(operand)

  private def bareRegisterOrZero(operand: Operand): Boolean =
    operandSelectors(operand).isEmpty && VMOperand.isRegister(operand, allowZero = true)

  private def operandSelectors(operand: Operand): Vec[OperandSelector] =
    VMOperand.selectors(VMOperand.without(operand, OperandSelector.`reuse`))

  @annotation.tailrec
  private def registerBaseIsGeneral(operand: Operand): Boolean =
    VMOperand.without(operand, OperandSelector.`reuse`) match {
      case Operand.Register(RegisterBank.R, index) => index.raw < 255
      case Operand.Qualified(inner, _)             => registerBaseIsGeneral(inner)
      case _                                       => false
    }

  private def stepNumber(value: Modifier): Option[Int] = value match {
    case Modifier.`STEP0` => Some(0)
    case Modifier.`STEP1` => Some(1)
    case Modifier.`STEP2` => Some(2)
    case Modifier.`STEP3` => Some(3)
    case _                => None
  }

  private def requireSize(operands: Vec[Operand], count: Int, message: String): Unit =
    if (operands.size != count) unsupported(message)

  private def unsupported(message: String): Nothing = throw IllegalArgumentException(message)

  def matrix(
      shape: Shape,
      dElement: Element,
      aElement: Element,
      bElement: Element,
      cElement: Element,
      aColumnMajor: Boolean,
      bColumnMajor: Boolean,
      destinations: Vec[Value.Register],
      left: Vec[Value],
      right: Vec[Value],
      addend: Vec[Value]
  ): Instruction = {
    if (aElement != bElement || !validMma(shape, aElement, cElement, dElement, aColumnMajor, bColumnMajor))
      throw IllegalArgumentException(
        s"unsupported MMA type/layout combination: $shape $aElement/$bElement/$cElement/$dElement"
      )
    val a        = Fragment(shape, Role.A, aElement, wmma = false, columnMajor = aColumnMajor)
    val b        = Fragment(shape, Role.B, bElement, wmma = false, columnMajor = bColumnMajor)
    val c        = Fragment(shape, Role.C, cElement, wmma = false)
    val d        = Fragment(shape, Role.C, dElement, wmma = false)
    val expected = Vec(d.registers, a.registers, b.registers, c.registers)
    val actual   = Vec(destinations.size, left.size, right.size, addend.size)
    if (actual != expected)
      throw IllegalArgumentException(s"MMA fragment register counts $actual do not match $expected")
    val operation = Operation.Multiply(
      a,
      b,
      c,
      d,
      destinations.map(_.name),
      left,
      right,
      addend,
      saturate = false
    )
    Tensor(operation, "mma.sync.aligned")
  }

  def loadMatrix(destinations: Vec[Value.Register], address: Value, count: Int, transpose: Boolean): Instruction = {
    if (!Set(1, 2, 4).contains(count) || destinations.size != count)
      throw IllegalArgumentException(s"ldmatrix destination count must be 1, 2, or 4: $count/${destinations.size}")
    Tensor(Operation.LoadMatrix(destinations.map(_.name), address, transpose, Space.Shared), "ldmatrix")
  }

  private def validMma(
      shape: Shape,
      a: Element,
      c: Element,
      d: Element,
      aColumnMajor: Boolean,
      bColumnMajor: Boolean
  ): Boolean = shape match {
    case Shape(m, n, k)
        if m.raw == 8 && n.raw == 8 && k.raw == 4 &&
          a == Element.F64 && !aColumnMajor && bColumnMajor && c == Element.F64 && d == Element.F64 => true
    case Shape(m, n, k) if m.raw == 8 && n.raw == 8 && k.raw == 4 && a == Element.F16 =>
      (a, c, d) match {
        case (Element.F16, Element.F16 | Element.F32, Element.F16 | Element.F32) => true
        case _                                                                   => false
      }
    case Shape(m, n, k) if m.raw == 16 && n.raw == 8 && !aColumnMajor && bColumnMajor =>
      (k.raw, a, c, d) match {
        case (8 | 16, Element.F16, Element.F16 | Element.F32, Element.F16 | Element.F32) if c == d => true
        case (8 | 16, Element.BF16, Element.F32, Element.F32)                                      => true
        case (4 | 8, Element.TF32, Element.F32, Element.F32)                                       => true
        case _                                                                                     => false
      }
    case _ => false
  }
}
