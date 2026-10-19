package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.isa.nvidia.VMIR.Instruction.*
import net.kurobako.gemstone.isa.sass.Instruction.*
import net.kurobako.gemstone.vm.*

/** mbarriers (SYNCS, ARRIVES), bulk and tensor copies (UBLK*, UTMA*), asynchronous cluster stores (STAS, REDAS) and
  * multimem loads (LDGMC). Uniform-datapath copies issue once per warp; their 32-bit shared operands keep the cluster
  * rank in bits 24..31, so they address the SR_SWINHI window.
  */
private object VMAsyncCompiler {
  private val phaseCheckModifiers = Set(Modifier.PHASECHK, Modifier.TRANS64, Modifier.TRYWAIT)
  private val arriveModifiers     = Set(
    Modifier.ARRIVE,
    Modifier.TRANS64,
    Modifier.RED,
    Modifier.OPTOUT,
    Modifier.A1T0,
    Modifier.ART0,
    Modifier.A0TR,
    Modifier.A0TX,
    Modifier.A0T1,
    Modifier.TMASK
  )
  private val sharedAtomicModifiers = Set(
    Modifier.ADD,
    Modifier.MIN,
    Modifier.MAX,
    Modifier.INC,
    Modifier.DEC,
    Modifier.AND,
    Modifier.OR,
    Modifier.XOR,
    Modifier.S32,
    Modifier.`64`,
    Modifier.U64
  )

  def lower(using context: VMCompilationContext): Boolean = {
    import context.*
    (operation.form: @unchecked) match {
      case _: Form.Syncs =>
        barrier()
        true
      // Before sm_90 an mbarrier is a word pair in shared memory; each lane arrives once its copies complete.
      case _: Form.Arrives if modifiers == Vec(Modifier.LDGSTSBAR, Modifier.`64`) =>
        requireOperands(1, "ARRIVES operands")
        emit(Atomic(
          None,
          address(operands.head, global = false, sharedAccess = true),
          Value.Immediate(1L),
          64,
          MemorySpace.Shared,
          AtomicOperation.BarrierArrive
        ))
        true
      case _: Form.Arrives =>
        requireOperands(1, "ARRIVES operands")
        val word = sharedWindow(operands.head)
        emit(MBarrierLoad(word))
        // Copies the LDGSTS scoreboard tracks have already completed when this arrival issues.
        if (modifiers == Vec(Modifier.LDGSTSBAR, Modifier.`64`, Modifier.TRANSCNT))
          emit(MBarrierTransaction(word, Value.Immediate(1L), complete = true))
        else if (modifiers == Vec(Modifier.LDGSTSBAR, Modifier.`64`, Modifier.ARVCNT))
          emit(MBarrierArrive(None, word, Value.Immediate(1L), Value.Immediate(0L)))
        else unsupported("unsupported copy arrival")
        true
      case _: Form.Ublkcp =>
        bulkCopy()
        true
      case _: Form.Rpcmov =>
        returnAddress()
        true
      case _: Form.Ublkred =>
        bulkReduce()
        true
      // the VM does not bound a warp's registers, so every allocation succeeds
      case _: Form.Usetmaxreg
          if operands.headOption.exists(OperandQueries.isPredicateAllowingUniform(_)) =>
        writePredicate(operands.head, Value.Predicate(VMRegisters.Predicate.True.slot))
        true
      case _: Form.Ublkpf | _: Form.Utmacctl | _: Form.Usetmaxreg => true
      // fence.proxy.tensormap::generic.acquire invalidates the tensor-map cache line; maps are read when used.
      case _: Form.Cctl
          if modifiers == Vec(Modifier.E, Modifier.C, Modifier.LDCU, Modifier.IV, Modifier.DEEP) =>
        emit(Fence)
        true
      case _: Form.Utmaldg | _: Form.Utmastg | _: Form.Utmaredg |
          _: Form.Utmapf =>
        tensor()
        true
      case _: Form.Stas | _: Form.Redas =>
        asyncStore()
        true
      case _: Form.Ldgmc =>
        multimem()
        true
      // st.bulk fills shared memory with zero in 8-byte units.
      case _: Form.Umemsets =>
        if (modifiers != Vec(Modifier.`64`) || operands.size != 3 || !OperandQueries.isZero(operands(1)))
          unsupported("unsupported shared memory fill")
        val size = binary(IntegerOperation.ShiftLeft, source(operands(2)), Value.Immediate(3L))
        once(AsyncBulkFill(sharedWindow(operands.head), size))
        true
      // ptxas waits on every scoreboard at once before a tensor-map fence.
      case _: Form.Depbar
          if modifiers.isEmpty && operands.size == 1 && scoreboardMask(operands) =>
        emit(Fence)
        true
      case _ => false
    }
  }

  /** The base register of a `[R]` or `[UR]` operand. */
  private def base(operand: Operand)(using context: VMCompilationContext): Operand.Register = operand match {
    case Operand.Address(Some(register), None, 0L, scale, None | Some(AddressMode.`64`), None) if scale == 1 =>
      register
    case other => context.unsupported(s"expected a register group operand, found $other")
  }

  private def window(value: Value)(using context: VMCompilationContext): Value = {
    import context.*
    val low = binary(IntegerOperation.BitwiseAnd, value, Value.Immediate(0xffffffffL), 64)
    binary(IntegerOperation.BitwiseOr, low, Value.Immediate(VMAddressSpace.SharedWindowBase), 64)
  }

  /** Runs `instruction` for one elected lane of each warp, as a uniform-datapath instruction issues. */
  private def once(issued: VMIR.Instruction)(using context: VMCompilationContext): Unit = {
    val elected = context.predicateTemp()
    context.emit(Elect(None, elected.slot, Value.ActiveMask))
    context.guarded(issued, elected)
  }

  private def barrier()(using context: VMCompilationContext): Unit = {
    import context.*
    val has = modifiers.toSet
    if (has(Modifier.EXCH)) {
      if (modifiers != Vec(Modifier.EXCH, Modifier.`64`) || operands.size != 3)
        unsupported("unsupported mbarrier initialisation")
      val word = sharedWindow(operands(1))
      // every lane reads the old word before any stores the new one
      val old = Option.when(!OperandQueries.isZero(operands.head)) {
        val value = temp()
        emit(Load(value.slot, word, 64, false, MemoryAccess.Generic(false)))
        value
      }
      emit(Store(word, source(operands(2), 64), 64, MemoryAccess.Generic(false)))
      emit(MBarrierLoad(word))
      old.foreach(store(operands.head, _, 64))
    } else if (has(Modifier.CCTL)) {
      if (modifiers == Vec(Modifier.CCTL, Modifier.IVALL)) {
        if (operands.nonEmpty) unsupported("barrier cache invalidation operands")
        emit(Fence)
      } else if (modifiers == Vec(Modifier.CCTL, Modifier.IV)) {
        requireOperands(1, "mbarrier invalidation operands")
        val word = sharedWindow(operands.head)
        emit(MBarrierLoad(word))
        emit(MBarrierInvalidate(word))
      } else unsupported("unsupported mbarrier cache operation")
    } else if (has(Modifier.PHASECHK)) {
      requireModifiers(phaseCheckModifiers)
      requireOperands(3, "mbarrier phase-check operands")
      val word  = sharedWindow(operands(1))
      val phase = binary(IntegerOperation.ShiftRightLogical, source(operands(2)), Value.Immediate(31L))
      val done  = predicateTemp()
      emit(MBarrierLoad(word))
      emit(MBarrierTest(destination = done.slot, address = word, phase = phase, parity = true))
      writePredicate(operands.head, done)
    } else if (has(Modifier.ARRIVE)) {
      requireModifiers(arriveModifiers)
      requireOperands(3, "mbarrier arrival operands")
      val word   = sharedWindow(operands(1))
      val amount = source(operands(2))
      emit(MBarrierLoad(word))
      if (has(Modifier.A0TR)) emit(MBarrierTransaction(word, amount, complete = false))
      else if (has(Modifier.A0TX)) emit(MBarrierTransaction(word, amount, complete = true))
      else if (has(Modifier.A0T1)) emit(MBarrierTransaction(word, Value.Immediate(1L), complete = false))
      else {
        val (count, bytes) =
          if (has(Modifier.A1T0)) (Value.Immediate(1L), Value.Immediate(0L))
          else if (has(Modifier.ART0)) (amount, Value.Immediate(0L))
          else (Value.Immediate(1L), amount)
        val token = Option.when(!OperandQueries.isZero(operands.head))(temp())
        emit(MBarrierArrive(token.map(_.slot), word, count, bytes, drop = has(Modifier.OPTOUT)))
        // TMASK returns the arriving lanes in the low word and, in the high word, the state before the warp's
        // arrivals, which ptxas advances by each lane's lower arrivals to recover that lane's own token
        token.foreach(own =>
          if (has(Modifier.TMASK)) {
            val lower = unary(
              IntegerUnaryOperation.PopulationCount,
              binary(IntegerOperation.BitwiseAnd, Value.ActiveMask, Value.LaneMaskLess)
            )
            val advance = binary(
              IntegerOperation.ShiftLeft,
              binary(IntegerOperation.MultiplyLow, lower, count),
              Value.Immediate(11L)
            )
            val high = binary(
              IntegerOperation.Subtract,
              binary(IntegerOperation.ShiftRightLogical, own, Value.Immediate(32L), 64),
              advance
            )
            val result = temp()
            emit(MoveCombine(result.slot, Vec(Value.ActiveMask, high), 32))
            store(operands.head, result, 64)
          } else store(operands.head, own, 64)
        )
      }
    } else unsupported("unsupported shared memory barrier operation")
  }

  private def bulkCopy()(using context: VMCompilationContext): Unit = {
    import context.*
    val hint = Set(Modifier.MULTICAST)
    if (operands.size < 3 || operands.size > 4) unsupported("unsupported bulk copy operands")
    modifiers.filterNot(hint) match {
      case Vec(Modifier.G, Modifier.S) | Vec(Modifier.G, Modifier.S, Modifier.DST_G_BYTE_MASK) =>
        val to            = address(operands.head, global = true)
        val from          = sharedWindow(operands(1))
        val descriptor    = source(operands(2))
        val (units, mask) =
          if (modifiers.contains(Modifier.DST_G_BYTE_MASK))
            (
              binary(IntegerOperation.BitwiseAnd, descriptor, Value.Immediate(0xffffL)),
              binary(IntegerOperation.ShiftRightLogical, descriptor, Value.Immediate(16L))
            )
          else (descriptor, Value.Immediate(0xffffL))
        once(AsyncBulkStore(to, from, binary(IntegerOperation.ShiftLeft, units, Value.Immediate(4L)), mask))
      case Vec(Modifier.S, origin @ (Modifier.G | Modifier.S)) =>
        val group      = base(operands.head)
        val to         = sharedWindow(operands.head)
        val barrier    = window(register(group, 1))
        val from       = if (origin == Modifier.G) address(operands(1), global = true) else sharedWindow(operands(1))
        val descriptor = source(operands(2))
        val multicast  = modifiers.contains(Modifier.MULTICAST)
        // Multicast copies carry the CTA mask in the high half of the size word.
        val units =
          if (multicast) binary(IntegerOperation.BitwiseAnd, descriptor, Value.Immediate(0xffffL)) else descriptor
        val mask = Option.when(multicast)(binary(IntegerOperation.ShiftRightLogical, descriptor, Value.Immediate(16L)))
        val size = binary(IntegerOperation.ShiftLeft, units, Value.Immediate(4L))
        once(AsyncBulkCopy(to, from, size, barrier, sharedSource = origin == Modifier.S, multicast = mask))
      case _ => unsupported("unsupported uniform bulk copy")
    }
  }

  private val reductions: Map[Modifier, TensorMap.Reduction] = Map(
    Modifier.ADD -> TensorMap.Reduction.Add,
    Modifier.MIN -> TensorMap.Reduction.Minimum,
    Modifier.MAX -> TensorMap.Reduction.Maximum,
    Modifier.INC -> TensorMap.Reduction.Increment,
    Modifier.DEC -> TensorMap.Reduction.Decrement,
    Modifier.AND -> TensorMap.Reduction.And,
    Modifier.OR  -> TensorMap.Reduction.Or,
    Modifier.XOR -> TensorMap.Reduction.Xor
  )

  private val elements: Map[Modifier, TensorMap.Element] = Map(
    Modifier.S32  -> TensorMap.Element.S32,
    Modifier.U64  -> TensorMap.Element.U64,
    Modifier.S64  -> TensorMap.Element.S64,
    Modifier.F32  -> TensorMap.Element.F32,
    Modifier.F64  -> TensorMap.Element.F64,
    Modifier.F16  -> TensorMap.Element.F16,
    Modifier.BF16 -> TensorMap.Element.BF16
  )

  private def bulkReduce()(using context: VMCompilationContext): Unit = {
    import context.*
    // a fourth operand is the L2 cache-policy descriptor of `.L2::cache_hint`
    if (operands.size < 3 || operands.size > 4) unsupported("unsupported bulk reduction operands")
    val operator = modifiers.collectFirst(Function.unlift(reductions.get))
      .getOrElse(unsupported("unsupported bulk reduction operation"))
    val element = modifiers.collectFirst(Function.unlift(elements.get)).getOrElse(TensorMap.Element.U32)
    val size    = binary(IntegerOperation.ShiftLeft, source(operands(2)), Value.Immediate(4L))
    modifiers.take(2) match {
      case Vec(Modifier.G, Modifier.S) =>
        once(AsyncBulkReduce(
          address(operands.head, global = true),
          sharedWindow(operands(1)),
          size,
          None,
          operator,
          element
        ))
      case Vec(Modifier.S, Modifier.S) =>
        val barrier = window(register(base(operands.head), 1))
        once(AsyncBulkReduce(
          sharedWindow(operands.head),
          sharedWindow(operands(1)),
          size,
          Some(barrier),
          operator,
          element
        ))
      case _ => unsupported("unsupported bulk reduction direction")
    }
  }

  private def tensor()(using context: VMCompilationContext): Unit = {
    import context.*
    val rank = modifiers.collectFirst {
      case Modifier.`1D` => 1
      case Modifier.`2D` => 2
      case Modifier.`3D` => 3
      case Modifier.`4D` => 4
      case Modifier.`5D` => 5
    }.getOrElse(unsupported("tensor copy without a dimensionality"))
    def named(name: String) = modifiers.exists(_.name == name)
    val wide                = named("W") || named("W128")
    val im2col              = modifiers.contains(Modifier.IM2COL) || wide
    val gather              = named("GATHER4") || named("SCATTER4")
    val pair                = named("2CTA")
    val multicast           = modifiers.contains(Modifier.MULTICAST)
    val group               = base(operands.head)
    val map                 = address(operands(1), global = true)
    val extra               = operands.lift(2).filter(OperandQueries.isRegister(_, RegisterBank.UR)).map(source(_))
    def field(packed: Value, from: Int, bits: Int): Value =
      binary(
        IntegerOperation.BitwiseAnd,
        binary(IntegerOperation.ShiftRightLogical, packed, Value.Immediate(from.toLong)),
        Value.Immediate((1L << bits) - 1L)
      )
    // im2col offsets pack into the low half of the extra word: one 16-bit, two 8-bit or three 5-bit fields; the wide
    // modes' wHalo and wOffset into its low 11 bits and the 5 above
    val offsets = (if (im2col) extra else None).fold(Vec.empty[Value]) { packed =>
      if (wide) Vec(field(packed, from = 0, bits = 11), field(packed, from = 11, bits = 5))
      else {
        val bits = rank match {
          case 3 => 16
          case 4 => 8
          case _ => 5
        }
        Vec.tabulate(rank - 2)(index => field(packed, index * bits, bits))
      }
    }
    val mask = Option.when(multicast)(
      binary(
        IntegerOperation.ShiftRightLogical,
        extra.getOrElse(unsupported("multicast without a mask")),
        Value.Immediate(16L)
      )
    )
    def coordinates(from: Int): Vec[Value] =
      Vec.tabulate(if (gather) 5 else rank)(index => register(group, from + index))
    operation.form match {
      case _: Form.Utmaldg =>
        once(AsyncTensor(
          TensorMap.Operation.Load,
          tensorMap = map,
          coordinates = coordinates(2),
          shared = sharedWindow(operands.head),
          barrier = Some(window(register(group, 1))),
          multicast = mask,
          offsets = offsets,
          im2col = im2col,
          gather = gather,
          pair = pair
        ))
      case _: Form.Utmapf =>
        once(AsyncTensor(
          TensorMap.Operation.Prefetch,
          map,
          coordinates(0),
          Value.Immediate(0L),
          None,
          None,
          offsets,
          im2col,
          gather
        ))
      case _ =>
        val kind = operation.form match {
          case _: Form.Utmastg => TensorMap.Operation.Store
          case _               =>
            TensorMap.Operation.Reduce(modifiers.collectFirst(Function.unlift(reductions.get))
              .getOrElse(unsupported("unsupported tensor reduction")))
        }
        once(AsyncTensor(kind, map, coordinates(1), sharedWindow(operands.head), None, None, Vec.empty, im2col, gather))
    }
  }

  // STAS and REDAS take the destination and its mbarrier as the two words of a register pair, the destination offset
  // by any uniform register.
  private def asyncStore()(using context: VMCompilationContext): Unit = {
    import context.*
    requireOperands(2, "asynchronous store operands")
    val (group, offset) = operands.head match {
      case Operand.Address(Some(pair), Some(index), 0L, scale, Some(AddressMode.`64`), None)
          if scale == 1 && index.bank == RegisterBank.UR =>
        (pair, Option.when(!OperandQueries.isZero(index))(register(index)))
      case other => (base(other), None)
    }
    val to      = window(offset.fold(register(group))(binary(IntegerOperation.Add, register(group), _)))
    val barrier = window(register(group, 1))
    operation.form match {
      case _: Form.Stas =>
        requireModifiers(Set(Modifier.`64`, Modifier.`128`))
        val words = if (modifiers.contains(Modifier.`128`)) 4 else if (modifiers.contains(Modifier.`64`)) 2 else 1
        emit(AsyncSharedStore(to, Vec.tabulate(words)(register(operands(1), _)), 32, barrier))
      case _ =>
        requireModifiers(sharedAtomicModifiers)
        val wide: Boolean = modifiers.exists(PartialFunction.cond(_) { case Modifier.`64` | Modifier.U64 => true })
        val width: ScalarBitWidth = if (wide) 64 else 32
        val signed                = modifiers.contains(Modifier.S32)
        val atomic                = modifiers.headOption match {
          case Some(Modifier.ADD) => AtomicOperation.Add
          case Some(Modifier.MIN) => if (signed) AtomicOperation.MinimumSigned else AtomicOperation.MinimumUnsigned
          case Some(Modifier.MAX) => if (signed) AtomicOperation.MaximumSigned else AtomicOperation.MaximumUnsigned
          case Some(Modifier.INC) => AtomicOperation.IncrementWrap
          case Some(Modifier.DEC) => AtomicOperation.DecrementWrap
          case Some(Modifier.AND) => AtomicOperation.And
          case Some(Modifier.OR)  => AtomicOperation.Or
          case Some(Modifier.XOR) => AtomicOperation.Xor
          case _                  => unsupported("unsupported asynchronous reduction")
        }
        emit(AsyncSharedReduce(to, source(operands(1), width), width, atomic, barrier))
    }
  }

  /** RPCMOV: a 32-bit half or all 64 bits of the return address register, written or read. */
  private def returnAddress()(using context: VMCompilationContext): Unit = {
    import context.*
    val rpc  = registerLayout.returnAddress
    val high = modifiers.contains(Modifier.HI) ||
      instruction.machine.exists(_.bindings.get("RpcN").contains(BigInt(1)))
    val names = modifiers.map(_.name)
    if (names.contains("64")) {
      requireOperands(1, "RPCMOV.64 operands")
      val value = operands.head match {
        case register: Operand.Register => pair(register)
        case immediate                  => literal(immediate)
      }
      emit(Move(rpc, value, 64))
    } else if (names.contains("Rpc")) {
      requireOperands(1, "RPCMOV.32 operands")
      val kept = binary(
        IntegerOperation.BitwiseAnd,
        Value.Register(rpc),
        Value.Immediate(if (high) 0xffffffffL else 0xffffffff00000000L),
        64
      )
      val placed =
        binary(IntegerOperation.ShiftLeft, widen(source(operands.head)), Value.Immediate(if (high) 32L else 0L), 64)
      emit(IntegerBinary(IntegerOperation.BitwiseOr, rpc, kept, placed, 64))
    } else {
      requireOperands(1, "RPCMOV.32 operands")
      store(
        operands.head,
        binary(IntegerOperation.ShiftRightLogical, Value.Register(rpc), Value.Immediate(if (high) 32L else 0L), 64)
      )
    }
  }

  // A multimem address on one device names that device's memory, the only copy a load-reduce combines.
  private def multimem()(using context: VMCompilationContext): Unit = {
    import context.*
    requireOperands(2, "multimem load operands")
    val wide = modifiers.exists(PartialFunction.cond(_) {
      case Modifier.`64` | Modifier.U64 | Modifier.S64 | Modifier.F64 | Modifier.F16x4 | Modifier.BF16x4 => true
    })
    val result  = temp()
    val pointer = operands(1) match {
      case operand: Operand.Address if operand.index.exists(OperandQueries.isZero) => operand.copy(index = None)
      case operand                                                                 => operand
    }
    emit(Load(
      result.slot,
      address(pointer, global = true),
      if (wide) 64 else 32,
      signed = false,
      MemoryAccess.Global(false)
    ))
    store(operands.head, result, if (wide) 64 else 32)
  }
}
