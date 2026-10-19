package net.kurobako.gemstone.isa.ptx

import Lowering.*
import cats.syntax.all.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.Tcgen05.*
import net.kurobako.gemstone.isa.nvidia.VMIR.{Instruction as _, *}

/** Lowers the tcgen05 family to the NVIDIA VM's tensor memory operations. The fences and waits order operations that
  * complete as they issue, so they order nothing further.
  */
private object VMTensorMemoryCompiler {
  private type Lowered = Either[Error, VMIR.Instruction]

  /** Qualifiers ptxas accepts that the pinned PTX syntax domains do not name. */
  val Spellings: Set[String] = Set(
    "64x128b",
    "32x128b",
    "warpx2::02_13",
    "warpx2::01_23",
    "warpx4",
    "collector::a::fill",
    "collector::a::use",
    "collector::a::lastuse",
    "collector::a::discard",
    "collector::b::fill",
    "collector::b::use",
    "collector::b::lastuse",
    "collector::b::discard"
  )

  private def spelt(modifiers: Vec[Modifier], name: String): Boolean = modifiers.exists(_.name.raw == name)

  private val counts: Map[Modifier, Int] = Map(
    Modifier.X1   -> 1,
    Modifier.X2   -> 2,
    Modifier.X4   -> 4,
    Modifier.X8   -> 8,
    Modifier.X16  -> 16,
    Modifier.X32  -> 32,
    Modifier.X64  -> 64,
    Modifier.X128 -> 128
  )

  private val shapes: Map[Modifier, DataShape] = Map(
    Modifier.N32x32b   -> DataShape.Lanes32Bits32,
    Modifier.N16x64b   -> DataShape.Lanes16Bits64,
    Modifier.N16x128b  -> DataShape.Lanes16Bits128,
    Modifier.N16x256b  -> DataShape.Lanes16Bits256,
    Modifier.N16x32bx2 -> DataShape.Lanes16Bits32x2
  )

  private val kinds: Map[Modifier, Kind] = Map(
    Modifier.KindF16      -> Kind.F16,
    Modifier.KindTf32     -> Kind.TF32,
    Modifier.KindF8f6f4   -> Kind.F8F6F4,
    Modifier.KindI8       -> Kind.I8,
    Modifier.KindMxf8f6f4 -> Kind.MXF8F6F4,
    Modifier.KindMxf4     -> Kind.MXF4,
    Modifier.KindMxf4nvf4 -> Kind.MXF4NVF4
  )

  private def immediate(operand: Operand, what: String): Either[Error, Long] =
    NumericLiteral.immediate(operand).toRight(Error.Invalid(s"$what must be an immediate, found $operand"))

  /** The data movement a tcgen05.ld or tcgen05.st names, moving `registers` registers per thread. */
  private def movement(modifiers: Vec[Modifier], registers: Int, split: Option[Operand]): Either[Error, Movement] =
    for {
      shape <- modifiers.collectFirst(Function.unlift(shapes.get)).toRight(Error.Invalid("tcgen05 data movement shape"))
      count <- modifiers.collectFirst(Function.unlift(counts.get)).toRight(Error.Invalid("tcgen05 repeat count"))
      offset <-
        if (shape == DataShape.Lanes16Bits32x2)
          split.toRight(Error.Invalid(".16x32bx2 takes a split offset")).flatMap(immediate(_, "split offset"))
        else Either.cond(split.isEmpty, 0L, Error.Invalid("only .16x32bx2 takes a split offset"))
      _ <- Either.cond(
        registers == count * shape.registers,
        (),
        Error.Invalid(s"${count * shape.registers} registers expected, found $registers")
      )
    } yield Movement(
      shape,
      registers,
      offset.toInt,
      modifiers.contains(Modifier.Pack16b) || modifiers.contains(Modifier.Unpack16b)
    )

  private def reduction(modifiers: Vec[Modifier], destination: Operand)(using
      scope: Scope
  )
      : Either[Error, Reduction] =
    for {
      target <- scope.register(destination)
      kind   <-
        if (modifiers.contains(Modifier.F32))
          Right(ReductionKind.Float(modifiers.contains(Modifier.Abs), modifiers.contains(Modifier.NaN)))
        else if (modifiers.contains(Modifier.S32)) Right(ReductionKind.Signed)
        else if (modifiers.contains(Modifier.U32)) Right(ReductionKind.Unsigned)
        else invalid("tcgen05.ld.red takes .f32, .s32 or .u32")
    } yield Reduction(target, modifiers.contains(Modifier.Max), kind)

  /** The lowering of `instruction`, or None when another family owns it. */
  def lower(instruction: Instruction, modifiers: Vec[Modifier])(using scope: Scope): Option[Lowered] = {
    def pair: Boolean = modifiers.contains(Modifier.CtaGroup2)
    instruction.form match {
      case Form.Tcgen05Alloc(destination, columns) =>
        Some((scope.address(destination), scope.value(columns)).mapN(VMIR.Instruction.TensorMemoryAllocate(_, _)))
      case Form.Tcgen05Dealloc(address, columns) =>
        Some((scope.value(address), scope.value(columns)).mapN(VMIR.Instruction.TensorMemoryDeallocate(_, _)))
      case Form.Tcgen05RelinquishAllocPermit() => Some(Right(VMIR.Instruction.TensorMemoryRelinquish))
      case Form.Tcgen05FenceBeforeThreadSync() | Form.Tcgen05FenceAfterThreadSync() =>
        Some(Right(VMIR.Instruction.Fence))
      case Form.Tcgen05WaitLd() | Form.Tcgen05WaitSt() => Some(Right(VMIR.Instruction.NoOp))
      case load: Form.Tcgen05Ld                        =>
        val operands = Vec(load.destRegs, load.reductionValue) ++ load.address.toVec ++ load.halfSplitOffset.toVec
        Some(operands.indexWhere(Operand.isAddress) match {
          case 1 =>
            for {
              destinations <- scope.registers(load.destRegs)
              shape        <- movement(modifiers, destinations.size, operands.lift(2))
              address      <- scope.address(load.reductionValue)
            } yield VMIR.Instruction.TensorMemoryLoad(shape, destinations, address, None)
          case 2 if modifiers.contains(Modifier.Red) =>
            for {
              destinations <- scope.registers(load.destRegs)
              shape        <- movement(modifiers, destinations.size, operands.lift(3))
              address      <- scope.address(operands(2))
              reduced      <- reduction(modifiers, load.reductionValue)
            } yield VMIR.Instruction.TensorMemoryLoad(shape, destinations, address, Some(reduced))
          case _ => invalid("tcgen05.ld takes its registers, an optional reduction value and an address")
        })
      case store: Form.Tcgen05St =>
        val (split, registers) = store.srcRegs.fold((None, store.halfSplitOffset))(found =>
          (Some(store.halfSplitOffset), found)
        )
        Some(for {
          sources <- scope.values(registers)
          shape   <- movement(modifiers, sources.size, split)
          address <- scope.address(store.address)
        } yield VMIR.Instruction.TensorMemoryStore(shape, sources, address))
      case Form.Tcgen05Cp(destination, descriptor) =>
        val shape =
          if (modifiers.contains(Modifier.N128x256b)) Some(CopyShape.Rows128Bits256)
          else if (modifiers.contains(Modifier.N4x256b)) Some(CopyShape.Rows4Bits256)
          else if (modifiers.contains(Modifier.N128x128b)) Some(CopyShape.Rows128Bits128)
          else if (spelt(modifiers, "64x128b")) Some(CopyShape.Rows64Bits128)
          else if (spelt(modifiers, "32x128b")) Some(CopyShape.Rows32Bits128)
          else None
        val replication =
          if (spelt(modifiers, "warpx2::02_13")) Replication.PairsOfAlternate
          else if (spelt(modifiers, "warpx2::01_23")) Replication.PairsOfAdjacent
          else if (spelt(modifiers, "warpx4")) Replication.Every
          else Replication.Once
        val packing =
          if (modifiers.contains(Modifier.B6x16P32)) Matrix.Packing.Six
          else if (modifiers.contains(Modifier.B4x16P64)) Matrix.Packing.Four
          else Matrix.Packing.Bytes
        Some(shape match {
          case Some(found)
              if (found == CopyShape.Rows64Bits128) ==
                (replication == Replication.PairsOfAlternate ||
                  replication == Replication.PairsOfAdjacent) &&
                (found == CopyShape.Rows32Bits128) ==
                (replication == Replication.Every) &&
                (packing == Matrix.Packing.Bytes) != modifiers.contains(Modifier.B8x16) =>
            (scope.address(destination), scope.value(descriptor)).mapN(
              VMIR.Instruction.TensorMemoryCopy(Copy(found, replication, packing, pair), _, _)
            )
          case _ => invalid("tcgen05.cp shape, replication or decompression formats do not match")
        })
      case Form.Tcgen05Shift(address) =>
        Some(scope.address(address).map(VMIR.Instruction.TensorMemoryShift(_, pair)))
      case Form.Tcgen05Commit(barrier, mask) =>
        Some(for {
          target <- scope.address(barrier)
          ranks  <- scope.multicastMask(mask, modifiers.contains(Modifier.MulticastCluster), "tcgen05.commit")
        } yield VMIR.Instruction.TensorMemoryCommit(target, ranks))
      case mma: Form.Tcgen05Mma =>
        val trailing = trailingOperands(mma.disabledOutputLanes.toVec ++ Vec(mma.enableInputD) ++ mma.scaleInputD.toVec)
        Some(trailing.flatMap((disabled, enable, shift) =>
          multiply(
            modifiers,
            mma.destAddr,
            mma.srcA,
            mma.srcBDesc,
            mma.instDesc,
            enable,
            scales = mma.scaleAAddr.map2(mma.scaleBAddr)((a, b) => (a = a, b = b)),
            disabled = disabled,
            shift = shift,
            metadata = None,
            zeroColumns = None,
            stationarity = Stationarity.Output
          )
        ))
      case mma: Form.Tcgen05MmaSp =>
        val trailing = trailingOperands(mma.disabledOutputLanes.toVec ++ Vec(mma.enableInputD) ++ mma.scaleInputD.toVec)
        Some(trailing.flatMap((disabled, enable, shift) =>
          multiply(
            modifiers,
            mma.destAddr,
            mma.srcA,
            mma.srcBDesc,
            mma.instDesc,
            enable,
            scales = mma.scaleAAddr.map2(mma.scaleBAddr)((a, b) => (a = a, b = b)),
            disabled = disabled,
            shift = shift,
            metadata = Some(mma.sparseMetadataAddr),
            zeroColumns = None,
            stationarity = Stationarity.Output
          )
        ))
      case mma: Form.Tcgen05MmaWs =>
        Some(multiply(
          modifiers,
          mma.destAddr,
          mma.srcA,
          mma.srcBDesc,
          mma.instDesc,
          mma.enableInputD,
          scales = None,
          disabled = None,
          shift = None,
          metadata = None,
          zeroColumns = mma.zeroColumnMaskDesc,
          stationarity = Stationarity.Weight
        ))
      case mma: Form.Tcgen05MmaWsSp =>
        Some(multiply(
          modifiers,
          mma.destAddr,
          mma.srcA,
          mma.srcBDesc,
          mma.instDesc,
          mma.enableInputD,
          scales = None,
          disabled = None,
          shift = None,
          metadata = Some(mma.sparseMetadataAddr),
          zeroColumns = mma.zeroColumnMaskDesc,
          stationarity = Stationarity.Weight
        ))
      case _ => None
    }
  }

  /** The optional disable-output-lane vector, enable-input-d and optional scale-input-d that end a tcgen05.mma, told
    * apart by kind: the generated decoder takes a six-operand form's predicate for the lane vector.
    */
  private def trailingOperands(operands: Vec[Operand])
      : Either[Error, (Option[Operand], Operand, Option[Operand])] = operands match {
    case Vec(lanes @ Operand.VectorLiteral(_), enable)        => Right((Some(lanes), enable, None))
    case Vec(lanes @ Operand.VectorLiteral(_), enable, shift) => Right((Some(lanes), enable, Some(shift)))
    case Vec(enable)                                          => Right((None, enable, None))
    case Vec(enable, shift)                                   => Right((None, enable, Some(shift)))
    case other => invalid(s"tcgen05.mma ends with lanes, enable-input-d and scale-input-d, found $other")
  }

  private enum Stationarity derives CanEqual { case Output, Weight }

  private def multiply(
      modifiers: Vec[Modifier],
      destination: Operand,
      a: Operand,
      b: Operand,
      descriptor: Operand,
      enable: Operand,
      scales: Option[(a: Operand, b: Operand)],
      disabled: Option[Operand],
      shift: Option[Operand],
      metadata: Option[Operand],
      zeroColumns: Option[Operand],
      stationarity: Stationarity
  )(using scope: Scope): Lowered =
    for {
      kind   <- modifiers.collectFirst(Function.unlift(kinds.get)).toRight(Error.Invalid("tcgen05.mma kind"))
      _      <- Either.cond(kind.scaled == scales.nonEmpty, (), Error.Invalid("only block-scaled kinds take scales"))
      target <- scope.address(destination)
      left   <- if (Operand.isAddress(a)) scope.address(a) else scope.value(a)
      right  <- scope.value(b)
      bits   <- scope.value(descriptor)
      guard  <- scope.predicateValue(enable)
      masks  <- disabled.traverse(scope.values(_)).map(_.getOrElse(Vec.empty[Value]))
      scale  <- shift.traverse(immediate(_, "scale-input-d"))
      _      <- Either.cond(
        scale.forall(value => value >= 0 && value <= 15 && (kind == Kind.F16 || kind == Kind.TF32)),
        (),
        Error.Invalid("scale-input-d is an immediate from 0 to 15 of .kind::f16 and .kind::tf32")
      )
      factors <-
        scales.traverse(found => (scope.address(found.a), scope.address(found.b)).mapN((a, b) => (a = a, b = b)))
      sparse <- metadata.traverse(scope.address(_))
      zeroes <- zeroColumns.traverse(scope.value(_))
    } yield VMIR.Instruction.TensorMemoryMultiply(Multiply(
      kind = kind,
      architecture = scope.architecture,
      pair = modifiers.contains(Modifier.CtaGroup2),
      ws = stationarity == Stationarity.Weight,
      vector =
        if (modifiers.contains(Modifier.ScaleVec1X)) 1
        else if (modifiers.contains(Modifier.ScaleVec2X)) 2
        else if (modifiers.contains(Modifier.ScaleVec4X) || modifiers.contains(Modifier.Block16)) 4
        else if (modifiers.contains(Modifier.Block32) && kind == Kind.MXF8F6F4) 1
        else if (modifiers.contains(Modifier.Block32)) 2
        else 0,
      shift = scale.fold(0)(_.toInt),
      shiftA = modifiers.contains(Modifier.Ashift),
      d = target,
      a = left,
      aInTensorMemory = Operand.isAddress(a),
      b = right,
      descriptor = bits,
      enable = guard,
      disabled = masks,
      scales = factors,
      metadata = sparse,
      zeroColumns = zeroes
    ))
}
