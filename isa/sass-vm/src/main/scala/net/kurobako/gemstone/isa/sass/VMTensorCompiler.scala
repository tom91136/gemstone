package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.isa.nvidia.VMIR.Instruction.*
import net.kurobako.gemstone.isa.nvidia.VMIR.Matrix.*
import net.kurobako.gemstone.isa.sass.Instruction.*
import net.kurobako.gemstone.isa.sass.Instruction as SASSInstruction

/** Matrix multiply-accumulate, shared matrix loads and stores, and register matrix transposes. */
private object VMTensorCompiler {

  /** A product that names no PTX `mma` shape and element types, so ptxas never emits it. */
  class NoProduct(message: String) extends IllegalArgumentException(message)

  def lower(using context: VMCompilationContext): Boolean = {
    import context.*
    (operation.form: @unchecked) match {
      case _: Form.Dmma =>
        doubleMatrix()
        true
      case _: Form.Hmma if modifiers.contains(Modifier.`884`) =>
        voltaMatrix()
        true
      case _: Form.Hmma =>
        halfMatrix()
        true
      case _: Form.Imma =>
        integerMatrix()
        true
      case _: Form.Bmma =>
        bitMatrix()
        true
      case _: Form.Qmma =>
        narrowMatrix(NarrowMatrix.Qmma)
        true
      case _: Form.Omma =>
        narrowMatrix(NarrowMatrix.Omma)
        true
      case _: Form.Ldsm =>
        tile(TileMove.Load)
        true
      case _: Form.Stsm =>
        tile(TileMove.Store)
        true
      case _: Form.Movm if modifiers == Vec(Modifier.U4TO8, Modifier.M832) =>
        requireOperands(2, "MOVM.U4TO8.M832 requires a destination pair and a source")
        emit(Tensor(Operation.Widen(
          destination(operands.head).slot,
          destination(operands.head, 1).slot,
          source(operands(1))
        )))
        true
      case _: Form.Movm =>
        if (sm < 75 || modifiers != Vec(Modifier.`16`, Modifier.MT88) || operands.size != 2)
          unsupported("unsupported register matrix transpose")
        emit(Tensor(Operation.Transpose(destination(operands.head).slot, source(operands(1)))))
        true
      case _ => false
    }
  }

  private def shapeOf(value: Modifier): Option[Shape] = value match {
    case Modifier.`884` | Modifier.`8x8x4`     => Some(Shape(8, 8, 4))
    case Modifier.`8816`                       => Some(Shape(8, 8, 16))
    case Modifier.`8832`                       => Some(Shape(8, 8, 32))
    case Modifier.`88128`                      => Some(Shape(8, 8, 128))
    case Modifier.`1684` | Modifier.`16x8x4`   => Some(Shape(16, 8, 4))
    case Modifier.`1688` | Modifier.`16x8x8`   => Some(Shape(16, 8, 8))
    case Modifier.`16816` | Modifier.`16x8x16` => Some(Shape(16, 8, 16))
    case Modifier.`16832`                      => Some(Shape(16, 8, 32))
    case Modifier.`16864`                      => Some(Shape(16, 8, 64))
    case Modifier.`168128`                     => Some(Shape(16, 8, 128))
    case Modifier.`168256`                     => Some(Shape(16, 8, 256))
    case _                                     => None
  }

  private def elementOf(value: Modifier, packed: Boolean): Option[Element] = value match {
    case Modifier.F16                                     => Some(Element.F16)
    case Modifier.F32                                     => Some(Element.F32)
    case Modifier.BF16 | Modifier.BF16_V2 | Modifier.E8M7 => Some(Element.BF16)
    case Modifier.TF32 | Modifier.E8M10                   => Some(Element.TF32)
    case Modifier.S8                                      => Some(Element.S8)
    case Modifier.U8                                      => Some(Element.U8)
    case Modifier.S4                                      => Some(Element.S4)
    case Modifier.U4                                      => Some(Element.U4)
    case Modifier.E4M3                                    => Some(Element.E4M3)
    case Modifier.E5M2                                    => Some(Element.E5M2)
    case Modifier.E3M2                                    => Some(Element.E3M2)
    case Modifier.E2M3                                    => Some(Element.E2M3)
    case Modifier.E2M1                                    => Some(if (packed) Element.E2M1 else Element.E2M1Byte)
    case _                                                => None
  }

  /** The single shape among the modifiers. */
  private def shape(using context: VMCompilationContext): Shape =
    context.modifiers.flatMap(shapeOf(_)) match {
      case Vec(single) => single
      case _           => context.unsupported("matrix product requires exactly one supported shape")
    }

  /** The fragment registers from `operand`'s base on, row and column selectors aside, or zeros for `RZ`. */
  private def registerSpan(operand: Operand, count: Int)(using context: VMCompilationContext): Vec[Value] = {
    import context.*
    val base = registerBaseOrZero(OperandQueries.base(operand), "matrix fragment register")
    if (OperandQueries.isZero(base)) {
      val zero = temp()
      emit(Move(zero.slot, Value.Immediate(0L), 32))
      Vec.fill(count)(zero)
    } else Vec.tabulate(count)(offset => destination(base, offset))
  }

  private def destinations(operand: Operand, count: Int)(using context: VMCompilationContext): Vec[Register] = {
    if (!context.bareRegister(operand)) context.unsupported("unsupported matrix fragment destination")
    Vec.tabulate(count)(offset => context.destination(operand, offset).slot)
  }

  /** D = A * B + C over row-major A and column-major B, with each fragment's registers from its operand's base. */
  private def product(
      shape: Shape,
      a: Element,
      b: Element,
      c: Element,
      d: Element,
      saturate: Boolean = false,
      bits: Option[BitOperation] = None,
      sparsity: Option[Sparsity] = None,
      scaling: Option[Scaling] = None
  )(using context: VMCompilationContext): Unit = {
    import context.*
    if (
      !Matrix.supportsMultiply(
        shape,
        a,
        b,
        c,
        d,
        aColumnMajor = false,
        bColumnMajor = true,
        wmma = false,
        sparse = sparsity.nonEmpty
      )
    )
      throw NoProduct(s"no PTX matrix product $shape $a.$b.$c.$d")
    val packed = if (sparsity.nonEmpty) shape.copy(k = (shape.k / 2).unsafe[Int > 0]) else shape
    val left   = Fragment(packed, Role.A, a, wmma = false)
    val right  = Fragment(shape, Role.B, b, wmma = false, columnMajor = true)
    val addend = Fragment(shape, Role.C, c, wmma = false)
    val result = Fragment(shape, Role.C, d, wmma = false)
    val format = if (a.integer) d else Matrix.accumulator(c, d)
    emit(Tensor(Operation.Multiply(
      left,
      right,
      addend,
      result,
      destinations(operands.head, result.registers),
      registerSpan(operands(1), left.registers),
      registerSpan(operands(2), right.registers),
      registerSpan(operands(3), addend.registers),
      Matrix.accumulation(sm, shape, a, format, ConversionRounding.RN),
      saturate,
      bits,
      sparsity,
      scaling
    )))
  }

  private def doubleMatrix()(using context: VMCompilationContext): Unit = {
    import context.*
    if (sm < 80) unsupported("DMMA requires sm_80 or higher")
    requireOperands(4, "DMMA requires destination, A, B and accumulator")
    if (!bareRegister(operands.head)) unsupported("unsupported DMMA destination")
    val matrix   = shape
    val rounding = modifiers.collectFirst {
      case Modifier.RZ => ConversionRounding.RZ
      case Modifier.RM => ConversionRounding.RM
      case Modifier.RP => ConversionRounding.RP
    }.getOrElse(ConversionRounding.RN)
    if (
      !Matrix.supportsMultiply(
        matrix,
        Element.F64,
        Element.F64,
        Element.F64,
        Element.F64,
        aColumnMajor = false,
        bColumnMajor = true,
        wmma = false
      )
    )
      unsupported(s"unsupported DMMA shape $matrix")
    val left   = Fragment(matrix, Role.A, Element.F64, wmma = false)
    val right  = Fragment(matrix, Role.B, Element.F64, wmma = false, columnMajor = true)
    val addend = Fragment(matrix, Role.C, Element.F64, wmma = false)
    def pairs(operand: Operand, count: Int): Vec[Value] = Vec.tabulate(count) { index =>
      if (OperandQueries.isZero(operand)) {
        val zero = temp()
        emit(Move(zero.slot, Value.Immediate(0L), 64))
        zero
      } else source(offsetRegister(operand, index * 2), 64)
    }
    emit(Tensor(Operation.Multiply(
      left,
      right,
      addend,
      addend,
      destinations(operands.head, 2 * addend.registers),
      pairs(operands(1), left.registers),
      pairs(operands(2), right.registers),
      pairs(operands(3), addend.registers),
      Matrix.accumulation(sm, matrix, Element.F64, Element.F64, rounding)
    )))
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
    if (destination.index > 253)
      unsupported("HMMA.884 destination register pair exceeds the physical register file")
    val (leftBase, leftColumnMajor)   = matrixSource(operands(1), "A")
    val (rightBase, rightColumnMajor) = matrixSource(operands(2), "B")
    if (!bareRegisterOrZero(operands(3))) unsupported("unsupported HMMA.884 accumulator")
    val zero = temp()
    emit(Move(zero.slot, Value.Immediate(0L), 32))
    val addend = Vec.tabulate(slots) { index =>
      if (index >= step * 2 && index < step * 2 + 2 && !OperandQueries.isZero(operands(3)))
        source(offsetRegister(operands(3), index - step * 2))
      else zero
    }
    def pair(base: Operand.Register) = Vec(source(base), source(offsetRegister(base, 1)))
    val shape                        = Shape(8, 8, 4)
    val c                            = Fragment(shape, Role.C, accumulator, wmma = false)
    emit(Tensor(Operation.Multiply(
      Fragment(shape, Role.A, Element.F16, wmma = false, leftColumnMajor),
      Fragment(shape, Role.B, Element.F16, wmma = false, rightColumnMajor),
      c,
      c,
      Vec.tabulate(slots)(index =>
        if (index / 2 == step) context.destination(destination, index % 2).slot else temp().slot
      ),
      pair(leftBase),
      pair(rightBase),
      addend,
      Matrix.accumulation(sm, shape, Element.F16, accumulator, ConversionRounding.RN)
    )))
  }

  /** A sparse product's metadata and lane selector, which follow the four fragments. */
  private def sparsity(using context: VMCompilationContext): Sparsity = {
    import context.*
    requireOperands(6, "sparse matrix product requires D, A, B, C, metadata and selector")
    Sparsity(source(operands(4)), literal(operands(5)))
  }

  private def halfMatrix()(using context: VMCompilationContext): Unit = {
    import context.*
    val matrix = shape
    val sparse = modifiers.contains(Modifier.SP)
    val input  = modifiers.flatMap(elementOf(_, packed = false))
      .find(element => element == Element.BF16 || element == Element.TF32).getOrElse(Element.F16)
    val accumulator =
      if (modifiers.contains(Modifier.F32)) Element.F32
      else if (modifiers.contains(Modifier.F16) && input == Element.F16) Element.F16
      else unsupported("HMMA requires an F16 or F32 accumulator")
    if (sm < 75 || (sm < 80 && (matrix != Shape(16, 8, 8) || input != Element.F16 || sparse)))
      unsupported(s"HMMA.${modifiers.mkString(".")} is not available on sm_$sm")
    if (!sparse) requireOperands(4, "HMMA requires destination, A, B and accumulator fragments")
    product(matrix, input, input, accumulator, accumulator, sparsity = Option.when(sparse)(sparsity))
  }

  private def integerMatrix()(using context: VMCompilationContext): Unit = {
    import context.*
    val sparse = modifiers.contains(Modifier.SP)
    val (a, b) = modifiers.flatMap(elementOf(_, packed = false)) match {
      case Vec(a, b) => (a, b)
      case _         => unsupported("IMMA requires A and B element types")
    }
    if (sm < 75) unsupported("IMMA requires sm_75 or higher")
    if (!sparse) requireOperands(4, "IMMA requires destination, A, B and accumulator fragments")
    product(
      shape,
      a,
      b,
      Element.S32,
      Element.S32,
      saturate = modifiers.contains(Modifier.SAT),
      sparsity = Option.when(sparse)(sparsity)
    )
  }

  private def bitMatrix()(using context: VMCompilationContext): Unit = {
    import context.*
    val bits = modifiers.collectFirst {
      case Modifier.AND => BitOperation.And
      case Modifier.XOR => BitOperation.Xor
    }
    if (sm < 75 || bits.isEmpty || !modifiers.contains(Modifier.POPC)) unsupported("unsupported BMMA operation")
    requireOperands(4, "BMMA requires destination, A, B and accumulator fragments")
    product(shape, Element.B1, Element.B1, Element.S32, Element.S32, bits = bits)
  }

  /** QMMA and OMMA: FP8, FP6 and FP4 products, optionally sparse and block-scaled. A sparse block-scaled product holds
    * its metadata and A's scale factors in a register pair; the selectors are packed in a uniform register with A's
    * byte and thread in bits 0-1 and 2 and B's in bits 3-4 and 5-6.
    */
  private enum NarrowMatrix derives CanEqual { case Qmma, Omma }

  private def narrowMatrix(form: NarrowMatrix)(using context: VMCompilationContext): Unit = {
    import context.*
    val packed    = form == NarrowMatrix.Omma
    val sparse    = modifiers.contains(Modifier.SP)
    val scaled    = modifiers.contains(Modifier.SF)
    val (d, a, b) = modifiers.flatMap(elementOf(_, packed)) match {
      case Vec(d, a, b) => (d, a, b)
      case _            => unsupported(s"${operation.mnemonic} requires accumulator and operand element types")
    }
    if (sm != 89 && sm < 120) unsupported(s"${operation.mnemonic} is not available on sm_$sm")
    requireOperands(
      4 + (if (sparse) 2 else 0) + (if (scaled) 3 else 0) - (if (sparse && scaled) 1 else 0),
      s"${operation.mnemonic} takes fragments, metadata, scale factors and selectors"
    )
    val scaling = Option.when(scaled) {
      val selector                              = source(operands(6))
      def field(offset: Int, mask: Long): Value =
        binary(
          IntegerOperation.BitwiseAnd,
          binary(IntegerOperation.ShiftRightLogical, selector, Value.Immediate(offset.toLong)),
          Value.Immediate(mask)
        )
      val vector: Int in (1, 2, 4) = if (modifiers.contains(Modifier.`4X`)) 4 else if (packed) 2 else 1
      Scaling(
        format = if (modifiers.contains(Modifier.UE4M3)) ScaleFormat.UE4M3 else ScaleFormat.UE8M0,
        vector = vector,
        aData = if (sparse) register(operands(4), 1) else source(operands(4)),
        aByte = field(0, 3),
        aThread = field(2, 1),
        bData = source(operands(5)),
        bByte = field(3, 3),
        bThread = field(5, 3)
      )
    }
    product(
      shape,
      a,
      b,
      d,
      d,
      sparsity = Option.when(sparse)(Sparsity(source(operands(4)), literal(operands.last))),
      scaling = scaling
    )
  }

  private enum TileMove derives CanEqual { case Load, Store }

  private def tile(move: TileMove)(using context: VMCompilationContext): Unit = {
    import context.*
    val load = move == TileMove.Load
    if (sm < (if (load) 75 else 90) || operands.size != 2) unsupported("unsupported shared matrix move operands")
    val count: Int in (1, 2, 4) = modifiers.lastOption match {
      case Some(Modifier.`2`) => 2
      case Some(Modifier.`4`) => 4
      case _                  => 1
    }
    // the pseudo-op spelling leaves the default 16-bit 8x8 tile unnamed
    val shaped = if (modifiers.forall(PartialFunction.cond(_) { case Modifier.`2` | Modifier.`4` => true }))
      Vec(Modifier.`16`, Modifier.M88) ++ modifiers
    else modifiers
    val (tileShape, transpose, packing) = shaped.take(2) match {
      case Vec(Modifier.`16`, Modifier.M88)           => (TileShape.M8N8, false, Packing.Bytes)
      case Vec(Modifier.`16`, Modifier.MT88)          => (TileShape.M8N8, true, Packing.Bytes)
      case Vec(Modifier.`8`, Modifier.MT1616)         => (TileShape.M16N16, true, Packing.Bytes)
      case Vec(Modifier.U6x16P32TO8, Modifier.MT1616) => (TileShape.M16N16, true, Packing.Six)
      case Vec(Modifier.U4x16P64TO8, Modifier.MT1616) => (TileShape.M16N16, true, Packing.Four)
      case Vec(Modifier.U6x16P32TO8, Modifier.M816)   => (TileShape.M8N16, false, Packing.Six)
      case Vec(Modifier.U4x16P64TO8, Modifier.M816)   => (TileShape.M8N16, false, Packing.Four)
      case Vec(Modifier.`8`, Modifier.MT168)          => (TileShape.M16N8, true, Packing.Bytes)
      case _ => unsupported(s"unsupported shared matrix move ${modifiers.mkString(".")}")
    }
    if (if (load) tileShape == TileShape.M16N8 else tileShape != TileShape.M16N8 && tileShape != TileShape.M8N8)
      unsupported("unsupported shared matrix move direction")
    val moved     = Tile(tileShape, count, transpose, packing)
    val registers = count * moved.registers
    if (load) {
      if (!OperandQueries.isRegister(operands.head)) unsupported("unsupported shared matrix load destination")
      val pointer = address(operands(1), global = false, sharedUniform = true, sharedAccess = true)
      emit(Tensor(Operation.LoadMatrix(
        moved,
        Vec.tabulate(registers)(destination(operands.head, _).slot),
        pointer,
        MemorySpace.Shared
      )))
    } else {
      val pointer = address(operands.head, global = false, sharedUniform = true, sharedAccess = true)
      emit(Tensor(Operation.StoreMatrix(
        moved,
        Vec.tabulate(registers)(index => source(offsetRegister(operands(1), index))),
        pointer,
        MemorySpace.Shared
      )))
    }
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
    if (register.index > 253)
      context.unsupported(s"HMMA.884 $name register pair exceeds the physical register file")
    register -> columnMajor
  }

  private def offsetRegister(operand: Operand, offset: Int)(using context: VMCompilationContext): Operand.Register =
    context.registerBaseOrZero(operand, "register pair") match {
      case zero if OperandQueries.isZero(zero) => OperandQueries.Zero
      case Operand.Register(bank, index)       =>
        (index + offset).refine[Int in 0 ~ 255].fold(
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
}
