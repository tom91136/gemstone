package net.kurobako.gemstone.isa.ptx

import Lowering.*
import cats.syntax.all.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.{Instruction as _, *}
import net.kurobako.gemstone.vm.*

/** Lowers mbarrier transactions, bulk and tensor copies, cluster addressing and the async-proxy fences to the NVIDIA
  * VM. Copies complete when they issue, so completion groups and proxy fences order nothing further.
  */
private object VMAsyncCompiler {
  private type Lowered = Either[Error, VMIR.Instruction]

  private val Orders: Set[Modifier] = Set(Modifier.Relaxed, Modifier.Acquire, Modifier.Release, Modifier.Weak)
  private val Scopes: Set[Modifier] = Set(Modifier.Cta, Modifier.Cluster, Modifier.Gpu, Modifier.Sys)
  private val Spaces: Set[Modifier] = Set(Modifier.Shared, Modifier.SharedCta, Modifier.SharedCluster)

  private val elements: Map[Modifier, TensorMap.Element] = Map(
    Modifier.U32  -> TensorMap.Element.U32,
    Modifier.B32  -> TensorMap.Element.U32,
    Modifier.S32  -> TensorMap.Element.S32,
    Modifier.U64  -> TensorMap.Element.U64,
    Modifier.B64  -> TensorMap.Element.U64,
    Modifier.S64  -> TensorMap.Element.S64,
    Modifier.F16  -> TensorMap.Element.F16,
    Modifier.Bf16 -> TensorMap.Element.BF16,
    Modifier.F32  -> TensorMap.Element.F32,
    Modifier.F64  -> TensorMap.Element.F64
  )

  private val reductions: Map[Modifier, TensorMap.Reduction] = Map(
    Modifier.Add -> TensorMap.Reduction.Add,
    Modifier.Min -> TensorMap.Reduction.Minimum,
    Modifier.Max -> TensorMap.Reduction.Maximum,
    Modifier.Inc -> TensorMap.Reduction.Increment,
    Modifier.Dec -> TensorMap.Reduction.Decrement,
    Modifier.And -> TensorMap.Reduction.And,
    Modifier.Or  -> TensorMap.Reduction.Or,
    Modifier.Xor -> TensorMap.Reduction.Xor
  )

  private val fields: Map[Modifier, TensorMap.Field] = Map(
    Modifier.GlobalAddress    -> TensorMap.Field.GlobalAddress,
    Modifier.Rank             -> TensorMap.Field.Rank,
    Modifier.BoxDim           -> TensorMap.Field.BoxDimension,
    Modifier.GlobalDim        -> TensorMap.Field.GlobalDimension,
    Modifier.GlobalStride     -> TensorMap.Field.GlobalStride,
    Modifier.ElementStride    -> TensorMap.Field.ElementStride,
    Modifier.Elemtype         -> TensorMap.Field.ElementType,
    Modifier.InterleaveLayout -> TensorMap.Field.Interleave,
    Modifier.SwizzleMode      -> TensorMap.Field.Swizzle,
    Modifier.SwizzleAtomicity -> TensorMap.Field.SwizzleAtomicity,
    Modifier.FillMode         -> TensorMap.Field.FillMode
  )

  private def unsupported(instruction: Instruction, detail: String): Lowered =
    Left(Error.Unsupported(instruction.qualifiedOpcode, detail))

  private def immediate(operand: Operand, what: String): Either[Error, Long] =
    NumericLiteral.immediate(operand).toRight(Error.Invalid(s"$what must be an immediate, found $operand"))

  /** The tensor map and coordinates of a `[map, {coordinates}]` operand. */
  private def tensor(operand: Operand)(using scope: Scope): Either[Error, (Value, Vec[Value])] = operand match {
    case Operand.Address(Vec(map, coordinates)) => (scope.value(map), scope.values(coordinates)).tupled
    case other                                  => invalid(s"expected a tensor map and coordinates, found $other")
  }

  extension (modifiers: Vec[Modifier]) {
    private def dimensions: Option[Int] = modifiers.collectFirst {
      case Modifier.N1d => 1
      case Modifier.N2d => 2
      case Modifier.N3d => 3
      case Modifier.N4d => 4
      case Modifier.N5d => 5
    }
  }

  /** The lowering of `instruction`, or None when another family owns it. */
  def lower(instruction: Instruction, modifiers: Vec[Modifier])(using scope: Scope): Option[Lowered] = {
    def has(modifier: Modifier): Boolean = modifiers.contains(modifier)
    def remote: Boolean                  = has(Modifier.SharedCluster)
    // Only qualifiers that change behaviour are checked; ordering and scope are subsumed by synchronous completion.
    def mbarrierForm(allowed: Modifier*): Boolean = {
      val accepted = Orders ++ Scopes ++ Spaces ++ allowed + Modifier.LayoutV0
      modifiers.forall(accepted)
    }
    instruction.form match {
      case Form.MbarrierArrive(state, address, count)
          if mbarrierForm(Modifier.Arrive, Modifier.ExpectTx, Modifier.NoComplete, Modifier.B64) =>
        Some(arrive(state, address, count, has(Modifier.ExpectTx), Arrival.Arrive, !has(Modifier.NoComplete), remote))
      case Form.MbarrierArriveDrop(state, address, count)
          if mbarrierForm(Modifier.ArriveDrop, Modifier.ExpectTx, Modifier.NoComplete, Modifier.B64) =>
        Some(arrive(
          state,
          address,
          count,
          has(Modifier.ExpectTx),
          Arrival.ArriveDrop,
          !has(Modifier.NoComplete),
          remote
        ))
      case Form.MbarrierExpectTx(address, count) if mbarrierForm(Modifier.ExpectTx, Modifier.B64) =>
        Some(transaction(address, count, complete = false))
      case Form.MbarrierCompleteTx(address, count) if mbarrierForm(Modifier.CompleteTx, Modifier.B64) =>
        Some(transaction(address, count, complete = true))
      case Form.MbarrierTestWait(destination, None, address, phase)
          if mbarrierForm(Modifier.TestWait, Modifier.Parity, Modifier.B64) =>
        Some(test(destination, address, phase, has(Modifier.Parity)))
      case Form.MbarrierTryWait(
            completion = destination,
            reportValue = None,
            barrierAddr = address,
            stateOrPhase = phase
          )
          if mbarrierForm(Modifier.TryWait, Modifier.Parity, Modifier.B64) =>
        Some(test(destination, address, phase, has(Modifier.Parity)))
      case Form.MbarrierPendingCount(destination, state) =>
        Some((scope.register(destination), scope.value(state)).mapN(VMIR.Instruction.MBarrierPending(_, _)))
      case Form.CpAsyncMbarrierArrive(address) =>
        // cp.async copies complete as they issue; without .noinc the arrival is balanced by its own pending count.
        Some(
          if (has(Modifier.Noinc))
            scope.address(address).map(VMIR.Instruction.MBarrierArrive(
              None,
              _,
              Value.Immediate(1L),
              Value.Immediate(0L)
            ))
          else scope.address(address).map(_ => VMIR.Instruction.NoOp)
        )
      case copy: Form.CpAsyncBulkMBarrier => Some(bulkCopy(instruction, copy, modifiers))
      case Form.CpAsyncBulkGroup(destAddr = destination, srcAddr = source, size = size, byteMask = byteMask) =>
        Some(bulkStore(destination, source, size, byteMask, modifiers))
      case Form.CpReduceAsyncBulk(destination, source, size, barrierOrPolicy) =>
        Some(reduction(modifiers).fold(unsupported(instruction, "unsupported bulk reduction operation or type")) {
          (operator, element) =>
            for {
              target <- scope.address(destination)
              origin <- scope.address(source)
              bytes  <- scope.value(size)
              // The mbarrier form's barrier operand takes the position the global form gives its cache policy.
              barrier <-
                if (!has(Modifier.MbarrierCompleteTxBytes)) Right(None)
                else barrierOrPolicy.toRight(Error.Invalid("shared::cluster bulk reduction requires an mbarrier"))
                  .flatMap(scope.address(_)).map(Some(_))
            } yield VMIR.Instruction.AsyncBulkReduce(target, origin, bytes, barrier, operator, element)
        })
      case Form.CpAsyncBulkPrefetch(srcAddr = address, size = size) =>
        Some((scope.address(address), scope.value(size)).mapN((_, _) => VMIR.Instruction.NoOp))
      case Form.CpAsyncBulkCommitGroup()    => Some(Right(VMIR.Instruction.NoOp))
      case Form.CpAsyncBulkWaitGroup(count) =>
        Some(
          NumericLiteral.immediate(count).filter(value => value >= 0 && value <= 63).map(_ => VMIR.Instruction.NoOp)
            .toRight(Error.Invalid("bulk copy pending group count must be an immediate between 0 and 63"))
        )
      case copy: Form.CpAsyncBulkTensorMBarrier =>
        Some(tensorCopy(
          tensorOperand = copy.tensorMapAndCoordinates,
          sharedOperand = copy.destAddr,
          barrierOperand = Some(copy.barrierAddr),
          maskOperand = copy.ctaMask,
          offsetsOperand = copy.im2colInfo,
          operation = TensorMap.Operation.Load,
          modifiers = modifiers
        ))
      case copy: Form.CpAsyncBulkTensorGroup =>
        Some(tensorCopy(
          tensorOperand = copy.tensorMapAndCoordinates,
          sharedOperand = copy.srcAddr,
          barrierOperand = None,
          maskOperand = None,
          offsetsOperand = None,
          operation = TensorMap.Operation.Store,
          modifiers = modifiers
        ))
      case copy: Form.CpReduceAsyncBulkTensor =>
        Some(
          modifiers.collectFirst(Function.unlift(reductions.get)).fold(unsupported(
            instruction,
            "unsupported tensor reduction"
          ))(operator =>
            tensorCopy(
              tensorOperand = copy.address,
              sharedOperand = copy.srcAddr,
              barrierOperand = None,
              maskOperand = None,
              offsetsOperand = None,
              operation = TensorMap.Operation.Reduce(operator),
              modifiers = modifiers
            )
          )
        )
      case copy: Form.CpAsyncBulkPrefetchTensor =>
        Some(tensorCopy(
          tensorOperand = copy.tensorMapAndCoordinates,
          sharedOperand = copy.tensorMapAndCoordinates,
          barrierOperand = None,
          maskOperand = None,
          offsetsOperand = copy.im2colInfo,
          operation = TensorMap.Operation.Prefetch,
          modifiers = modifiers
        ))
      case Form.StBulk(address, size, value) =>
        Some(immediate(value, "st.bulk initial value").flatMap {
          case 0L => (scope.address(address), scope.value(size)).mapN(VMIR.Instruction.AsyncBulkFill(_, _))
          case _  => invalid("st.bulk initial value must be zero")
        })
      case Form.TensorMapReplace(address, ordinalOrValue, value) =>
        Some(modifiers.collectFirst(Function.unlift(fields.get)) match {
          case Some(field) =>
            for {
              target  <- scope.address(address)
              ordinal <- value.fold(Right(0L))(_ => immediate(ordinalOrValue, "tensor map field ordinal"))
              ord     <- ordinal.toInt.refine[Int in 0 ~ 4].left.map(_ => Error.Invalid(s"invalid ordinal $ordinal"))
              source  <- scope.value(value.getOrElse(ordinalOrValue))
            } yield VMIR.Instruction.TensorMapReplace(target, field, ord, source)
          case None => unsupported(instruction, "unsupported tensor map field")
        })
      case Form.TensorMapCopyFenceProxy(destination, source, size) =>
        Some(immediate(size, "tensor map copy size").flatMap {
          case 128L  => (scope.address(destination), scope.address(source)).mapN(VMIR.Instruction.TensorMapCopy(_, _))
          case other => invalid(s"tensor map copies must be 128 bytes, found $other")
        })
      case Form.Fence(_, _)
          if has(Modifier.Proxy) || has(Modifier.MbarrierInit) || has(Modifier.SyncRestrictSharedCluster) =>
        Some(Right(VMIR.Instruction.Fence))
      case Form.Mapa(destination, address, rank) =>
        Some((
          scope.register(destination),
          scope.value(address),
          scope.value(rank)
        ).mapN(VMIR.Instruction.ClusterMap(_, _, _)))
      case Form.GetCtaRank(destination, address) =>
        Some((scope.register(destination), scope.value(address)).mapN(VMIR.Instruction.ClusterRankOf(_, _)))
      case Form.Cvta(destination, source) if has(Modifier.SharedCluster) || has(Modifier.SharedCta) =>
        Some((scope.register(destination), scope.offsetValue(source)).mapN(VMIR.Instruction.Move(_, _, 64)))
      // The completion mbarrier takes the cache policy's operand position in the generated cases.
      case Form.StAsync(address, value, barrier) if has(Modifier.MbarrierCompleteTxBytes) =>
        Some(for {
          stored <-
            if (has(Modifier.B128))
              scope.values(value).flatMap(pair(_, "st.async.b128 source")).map(64.unsafe[ScalarBitWidth] -> _)
            else (scalarWidth(modifiers), scope.values(value)).tupled
          target <- scope.address(address)
          word   <- barrier.toRight(Error.Invalid("st.async requires an mbarrier")).flatMap(scope.address(_))
        } yield VMIR.Instruction.AsyncSharedStore(target, stored._2, stored._1, word))
      // A release store or reduction completes as it issues, which orders it before every later operation.
      case Form.StAsync(address, value, None) if has(Modifier.Release) =>
        Some(releaseStore(address, value, modifiers))
      case Form.MultimemStAsync(address, value)                         => Some(releaseStore(address, value, modifiers))
      case Form.RedAsync(address, value, None) if has(Modifier.Release) =>
        Some(releaseReduction(instruction, address, value, modifiers))
      case Form.MultimemRedAsync(address, value) =>
        Some(releaseReduction(instruction, address, value, modifiers))
      case Form.MultimemCpAsyncBulk(destination, source, size, byteMask) =>
        Some(bulkStore(destination, source, size, byteMask, modifiers))
      case Form.MultimemCpReduceAsyncBulk(destination, source, size) =>
        Some(reduction(modifiers).fold(unsupported(instruction, "unsupported bulk reduction operation or type")) {
          (operator, element) =>
            (scope.address(destination), scope.address(source), scope.value(size))
              .mapN(VMIR.Instruction.AsyncBulkReduce(_, _, _, None, operator, element))
        })
      case Form.RedAsync(address, value, barrier)
          if has(Modifier.MbarrierCompleteTxBytes) || (barrier.nonEmpty && has(Modifier.Relaxed)) =>
        Some(atomicReduction(instruction, modifiers) { (operation, width) =>
          for {
            target <- scope.address(address)
            source <- scope.value(value)
            word   <- barrier.toRight(Error.Invalid("red.async requires an mbarrier")).flatMap(scope.address(_))
          } yield VMIR.Instruction.AsyncSharedReduce(target, source, width, operation, word)
        })
      // A multimem address on one device names that device's own memory, the only copy a reduction combines.
      case Form.MultimemLdReduce(destination, address) =>
        val width = modifiers.multimemWidth
        Some(scope.address(address).flatMap(target =>
          if (Operand.isVectorLiteral(destination))
            scope.registers(destination).map(VMIR.Instruction.LoadVector(
              _,
              target,
              width,
              signed = false,
              MemoryAccess.Global(false)
            ))
          else scope.register(destination).map(VMIR.Instruction.Load(
            _,
            target,
            width,
            signed = false,
            MemoryAccess.Global(false)
          ))
        ))
      case Form.MultimemSt(address, value) =>
        val width = modifiers.multimemWidth
        Some(scope.address(address).flatMap(target =>
          if (Operand.isVectorLiteral(value))
            scope.values(value).map(VMIR.Instruction.StoreVector(target, _, width, MemoryAccess.Global(false)))
          else scope.value(value).map(VMIR.Instruction.Store(target, _, width, MemoryAccess.Global(false)))
        ))
      case Form.MultimemRed(address, value) if !Operand.isVectorLiteral(value) =>
        val operation = modifiers.collectFirst(Function.unlift(reductions.get)).flatMap { operator =>
          if (has(Modifier.F16x2) && operator == TensorMap.Reduction.Add) Some(AtomicOperation.PackedHalfAdd -> 32)
          else if (has(Modifier.Bf16x2) && operator == TensorMap.Reduction.Add)
            Some(AtomicOperation.PackedBFloat16Add -> 32)
          else modifiers.reverse.collectFirst(Function.unlift(elements.get)).flatMap(atomicOperation(operator, _))
            .map((operation, width) => operation -> width.raw)
        }
        Some(operation.fold(unsupported(instruction, "unsupported multimem reduction")) { (atomic, width) =>
          (scope.address(address), scope.value(value)).mapN((target, source) =>
            VMIR.Instruction.Atomic(None, target, source, width.unsafe[ScalarBitWidth], MemorySpace.Global, atomic)
          )
        })
      case Form.SetMaxRegisters(_) => Some(Right(VMIR.Instruction.NoOp))
      case _                       => None
    }
  }

  extension (modifiers: Vec[Modifier]) {
    private def access: MemoryAccess =
      if (modifiers.contains(Modifier.Global)) MemoryAccess.Global(modifiers.contains(Modifier.Sys))
      else MemoryAccess.Generic(modifiers.contains(Modifier.Sys))
  }

  /** `st.async.release` and `multimem.st.async`: a store to global memory, on one device to its own memory. */
  private def releaseStore(address: Operand, value: Operand, modifiers: Vec[Modifier])(using scope: Scope): Lowered =
    (scalarWidth(modifiers), scope.address(address), scope.value(value)).mapN((width, target, source) =>
      VMIR.Instruction.Store(target, source, width, modifiers.access)
    )

  /** `red.async.release` and `multimem.red.async`: an atomic reduction in global memory. */
  private def releaseReduction(instruction: Instruction, address: Operand, value: Operand, modifiers: Vec[Modifier])(
      using scope: Scope
  ): Lowered =
    atomicReduction(instruction, modifiers) { (operation, width) =>
      (scope.address(address), scope.value(value)).mapN((target, source) =>
        VMIR.Instruction.Atomic(
          None,
          target,
          source,
          width,
          if (modifiers.contains(Modifier.Global)) MemorySpace.Global else MemorySpace.Generic,
          operation
        )
      )
    }

  private def reduction(modifiers: Vec[Modifier]): Option[(TensorMap.Reduction, TensorMap.Element)] =
    (
      modifiers.collectFirst(Function.unlift(reductions.get)),
      modifiers.reverse.collectFirst(Function.unlift(elements.get))
    ).tupled

  private def bulkStore(
      destination: Operand,
      source: Operand,
      size: Operand,
      byteMask: Option[Operand],
      modifiers: Vec[Modifier]
  )(using scope: Scope): Lowered = for {
    mask <-
      if (modifiers.contains(Modifier.CpMask))
        byteMask.toRight(Error.Invalid("masked bulk copy requires a byte mask")).flatMap(scope.value(_))
      else Right(Value.Immediate(0xffffL))
    lowered <- (scope.address(destination), scope.address(source), scope.value(size))
      .mapN(VMIR.Instruction.AsyncBulkStore(_, _, _, mask))
  } yield lowered

  extension (modifiers: Vec[Modifier]) {
    private def multimemWidth: ScalarBitWidth =
      if (modifiers.exists(_.is64Bit)) 64 else 32
  }

  private def atomicReduction(instruction: Instruction, modifiers: Vec[Modifier])(
      lower: (AtomicOperation, ScalarBitWidth) => Lowered
  ): Lowered = reduction(modifiers).flatMap(atomicOperation(_, _)).fold(
    unsupported(instruction, "unsupported asynchronous reduction")
  )(lower.tupled)

  /** The atomic operation and width an element reduction performs on one element. */
  private def atomicOperation(operator: TensorMap.Reduction, element: TensorMap.Element)
      : Option[(AtomicOperation, ScalarBitWidth)] = {
    import TensorMap.Element.*
    import TensorMap.Reduction.*
    val operation = (operator, element) match {
      case (Add, U32 | S32 | U64 | S64) => Some(AtomicOperation.Add)
      case (Add, F32 | F64)             => Some(AtomicOperation.FloatAdd)
      case (Minimum, U32 | U64)         => Some(AtomicOperation.MinimumUnsigned)
      case (Minimum, S32 | S64)         => Some(AtomicOperation.MinimumSigned)
      case (Maximum, U32 | U64)         => Some(AtomicOperation.MaximumUnsigned)
      case (Maximum, S32 | S64)         => Some(AtomicOperation.MaximumSigned)
      case (Increment, U32)             => Some(AtomicOperation.IncrementWrap)
      case (Decrement, U32)             => Some(AtomicOperation.DecrementWrap)
      case (And, U32 | U64)             => Some(AtomicOperation.And)
      case (Or, U32 | U64)              => Some(AtomicOperation.Or)
      case (Xor, U32 | U64)             => Some(AtomicOperation.Xor)
      case _                            => None
    }
    operation.map(_ -> element.width)
  }

  private enum Arrival derives CanEqual { case Arrive, ArriveDrop }

  private def arrive(
      state: Operand,
      address: Operand,
      count: Option[Operand],
      expectTx: Boolean,
      kind: Arrival,
      complete: Boolean,
      remote: Boolean
  )(using scope: Scope): Lowered =
    for {
      sink    <- scope.sink(state)
      _       <- Either.cond(!remote || sink.isEmpty, (), Error.Invalid("a shared::cluster arrival has no state"))
      barrier <- scope.address(address)
      amount  <- count.traverse(scope.value(_)).map(_.getOrElse(Value.Immediate(1L)))
    } yield VMIR.Instruction.MBarrierArrive(
      sink,
      barrier,
      if (expectTx) Value.Immediate(1L) else amount,
      if (expectTx) amount else Value.Immediate(0L),
      kind == Arrival.ArriveDrop,
      complete
    )

  private def transaction(address: Operand, count: Operand, complete: Boolean)(using scope: Scope): Lowered =
    (scope.address(address), scope.value(count)).mapN(VMIR.Instruction.MBarrierTransaction(_, _, complete))

  private def test(destination: Operand, address: Operand, phase: Operand, parity: Boolean)(using
      scope: Scope
  )
      : Lowered =
    (scope.predicate(destination), scope.address(address), scope.value(phase))
      .mapN(VMIR.Instruction.MBarrierTest(_, _, _, parity))

  private def bulkCopy(instruction: Instruction, copy: Form.CpAsyncBulkMBarrier, modifiers: Vec[Modifier])(using
      scope: Scope
  ): Lowered = {
    val spaces = modifiers.filter(PartialFunction.cond(_) {
      case Modifier.SharedCluster | Modifier.SharedCta | Modifier.Global => true
    })
    val ignoring = modifiers.contains(Modifier.IgnoreOob)
    if (ignoring != (copy.ignoredBytesLeft.nonEmpty && copy.ignoredBytesRight.nonEmpty))
      Left(Error.Invalid(".ignore_oob takes the bytes it ignores at each end, and only it does"))
    else if (ignoring && spaces != Vec(Modifier.SharedCta, Modifier.Global))
      Left(Error.Invalid(".ignore_oob applies to global to shared::cta copies only"))
    else spaces match {
      case Vec(Modifier.SharedCluster | Modifier.SharedCta, source @ (Modifier.Global | Modifier.SharedCta)) =>
        for {
          target  <- scope.address(copy.destAddr)
          origin  <- scope.address(copy.srcAddr)
          bytes   <- scope.value(copy.size)
          barrier <- scope.address(copy.barrierAddr)
          mask    <- scope.multicastMask(copy.ctaMask, modifiers.contains(Modifier.MulticastCluster), "bulk copy")
          left    <- copy.ignoredBytesLeft.traverse(scope.value(_))
          right   <- copy.ignoredBytesRight.traverse(scope.value(_))
        } yield VMIR.Instruction.AsyncBulkCopy(
          target,
          origin,
          bytes,
          barrier,
          source == Modifier.SharedCta,
          mask,
          (left, right).mapN((left, right) => (left = left, right = right)),
          spaces.headOption.contains(Modifier.SharedCluster)
        )
      case _ => unsupported(instruction, "unsupported bulk copy direction")
    }
  }

  private def tensorCopy(
      tensorOperand: Operand,
      sharedOperand: Operand,
      barrierOperand: Option[Operand],
      maskOperand: Option[Operand],
      offsetsOperand: Option[Operand],
      operation: TensorMap.Operation,
      modifiers: Vec[Modifier]
  )(using scope: Scope): Lowered = {
    val im2col = modifiers.exists(PartialFunction.cond(_) {
      case Modifier.Im2col | Modifier.Im2colNoOffs | Modifier.Im2colW | Modifier.Im2colW128 => true
    })
    val gather = modifiers.exists(PartialFunction.cond(_) { case Modifier.TileGather4 | Modifier.TileScatter4 => true })
    val pair   = modifiers.exists(_.name.raw == "cta_group::2")
    for {
      rank               <- modifiers.dimensions.toRight(Error.Invalid("tensor copy has no dimensionality"))
      (map, coordinates) <- tensor(tensorOperand)
      count = if (gather) 5 else rank
      _ <- Either.cond(coordinates.size == count, (), Error.Invalid(s"$rank-D tensor copy needs $count coordinates"))
      shared <-
        if (operation == TensorMap.Operation.Prefetch) Right(Value.Immediate(0L)) else scope.address(sharedOperand)
      barrier <- barrierOperand.traverse(scope.address(_))
      mask    <- scope.multicastMask(maskOperand, modifiers.contains(Modifier.MulticastCluster), "tensor copy")
      offsets <- offsetsOperand.traverse(scope.values(_)).map(_.getOrElse(Vec.empty[Value]))
    } yield VMIR.Instruction.AsyncTensor(
      operation,
      map,
      coordinates,
      shared,
      barrier,
      mask,
      offsets,
      im2col,
      gather,
      pair
    )
  }
}
