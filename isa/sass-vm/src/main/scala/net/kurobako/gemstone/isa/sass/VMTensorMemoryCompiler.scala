package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.isa.nvidia.VMIR.Instruction.*
import net.kurobako.gemstone.isa.nvidia.VMIR.Tcgen05.*
import net.kurobako.gemstone.vm.*

/** The tcgen05 instructions: tensor memory loads and stores (LDTM, STTM), its allocation state (UTCATOMSWS,
  * UVIRTCOUNT), copies, shifts and products (UTCCP, UTCSHIFT, UTC*MMA) and their mbarrier commits (UTCBAR), and the
  * release stores and reductions of st.async and red.async (USTGR, UREDGR). The uniform-datapath instructions issue
  * once per warp; their operands name tensor memory addresses in uniform registers and shared memory descriptors and
  * instruction descriptors in uniform register pairs.
  */
private object VMTensorMemoryCompiler {

  def lower(using context: VMCompilationContext): Boolean = {
    import context.*
    (operation.form: @unchecked) match {
      case _: Form.Ldtm =>
        load()
        true
      case _: Form.Sttm =>
        storeTensor()
        true
      case _: Form.Utcatomsws =>
        allocation()
        true
      // ptxas lowers tcgen05.relinquish_alloc_permit to UVIRTCOUNT.DEALLOC.SMPOOL
      case _: Form.Uvirtcount =>
        emit(TensorMemoryRelinquish)
        true
      case _: Form.Utcbar =>
        commit()
        true
      case _: Form.Utccp =>
        copy()
        true
      case _: Form.Utcshift =>
        requireOperands(1, "UTCSHIFT operands")
        once(TensorMemoryShift(tensor(operands.head), named("2CTA")))
        true
      case _: (Form.Utchmma | Form.Utcimma | Form.Utcqmma |
            Form.Utcomma) =>
        multiply()
        true
      case _: Form.Ustgr =>
        requireOperands(2, "USTGR operands")
        once(Store(address(operands.head, global = true), source(operands(1), width), width, access))
        true
      case _: Form.Uredgr =>
        requireOperands(2, "UREDGR operands")
        if (!named("ADD")) unsupported("only additive UREDGR reductions are emitted")
        once(Atomic(
          None,
          address(operands.head, global = true),
          source(operands(1), width),
          width,
          MemorySpace.Global,
          AtomicOperation.Add
        ))
        true
      case _ => false
    }
  }

  private def named(name: String)(using context: VMCompilationContext): Boolean =
    context.modifiers.exists(_.name == name)

  /** The width a uniform store or reduction moves, from its size modifier. */
  private def width(using VMCompilationContext): ScalarBitWidth =
    if (named("U8") || named("S8")) 8
    else if (named("U16") || named("S16")) 16
    else if (named("64") || named("U64") || named("S64")) 64
    else 32

  private def access(using VMCompilationContext): MemoryAccess =
    MemoryAccess.Global(named("SYS") || named("MMIO"))

  /** Runs `instruction` for one elected lane of each warp, as a uniform-datapath instruction issues. */
  private def once(instruction: VMIR.Instruction)(using context: VMCompilationContext): Unit = {
    val elected = context.predicateTemp()
    context.emit(Elect(None, elected.slot, Value.ActiveMask))
    context.guarded(instruction, elected)
  }

  /** A `tmem[URa+offset]` tensor memory address. */
  private def tensor(operand: Operand)(using context: VMCompilationContext): Value =
    context.address(OperandQueries.base(operand), global = false)

  /** The register a `[URa]` operand, or a plain one, names. */
  private def named(operand: Operand): Operand = OperandQueries.base(operand) match {
    case Operand.Address(base = Some(register)) => register
    case other                                  => other
  }

  /** The 64-bit value of the uniform register pair a `gdesc[URa]` or `URa` operand names. */
  private def wide(operand: Operand)(using context: VMCompilationContext): Value =
    if (OperandQueries.isZero(named(operand))) Value.Immediate(0L) else context.pair(named(operand))

  private def word(operand: Operand, add: Int = 0)(using context: VMCompilationContext): Value =
    context.register(named(operand), add)

  private val counts = (0 to 7).map(power => s"x${1 << power}" -> (1 << power)).toMap

  private def movement(registers: Int => Int)(using context: VMCompilationContext): Movement = {
    val shape =
      if (named("16dp64bit")) DataShape.Lanes16Bits64
      else if (named("16dp128bit")) DataShape.Lanes16Bits128
      else if (named("16dp256bit")) DataShape.Lanes16Bits256
      else if (named("16dp32bit_t0_t15") || named("16dp32bit_t16_t31")) DataShape.Lanes16Bits32x2
      else DataShape.Lanes32Bits32
    val count = context.modifiers.collectFirst(Function.unlift(modifier => counts.get(modifier.name))).getOrElse(1)
    Movement(
      shape,
      registers(count * shape.registers),
      0,
      named("PACK16BIT") || named("EXPAND16BIT"),
      if (named("16dp32bit_t0_t15")) Threads.Low else if (named("16dp32bit_t16_t31")) Threads.High else Threads.All
    )
  }

  private def load()(using context: VMCompilationContext): Unit = {
    import context.*
    val statistic = operands.size == 3
    if (operands.size != 2 && !statistic) unsupported("LDTM operands")
    val first        = if (statistic) 1 else 0
    val shape        = movement(identity)
    val destinations = Vec.tabulate(shape.registers)(index => destination(operands(first), index).slot)
    val reduction    = Option.when(statistic) {
      val kind =
        if (named("F32")) ReductionKind.Float(named("MINABS") || named("MAXABS"), named("NAN"))
        else if (named("S32")) ReductionKind.Signed
        else ReductionKind.Unsigned
      Reduction(destination(operands.head).slot, named("MAX") || named("MAXABS"), kind)
    }
    emit(TensorMemoryLoad(shape, destinations, tensor(operands(first + 1)), reduction))
  }

  private def storeTensor()(using context: VMCompilationContext): Unit = {
    import context.*
    requireOperands(2, "STTM operands")
    val shape = movement(identity)
    emit(TensorMemoryStore(shape, Vec.tabulate(shape.registers)(register(operands(1), _)), tensor(operands.head)))
  }

  // FIND_AND_SET claims an aligned run of free 32-column granules and AND clears them, as tcgen05.alloc and dealloc
  private def allocation()(using context: VMCompilationContext): Unit = {
    import context.*
    if (named("FIND_AND_SET")) {
      requireOperands(3, "UTCATOMSWS.FIND_AND_SET operands")
      val start = temp()
      val found = predicateTemp()
      emit(TensorMemoryClaim(start.slot, found.slot, source(operands(2))))
      store(operands(1), start)
      writePredicate(operands.head, found)
    } else if (named("AND")) {
      requireOperands(2, "UTCATOMSWS.AND operands")
      emit(TensorMemoryRelease(source(operands(1))))
    } else unsupported("only the allocation forms ptxas emits of UTCATOMSWS are implemented")
  }

  private def commit()(using context: VMCompilationContext): Unit = {
    import context.*
    if (operands.isEmpty) emit(Fence)
    else {
      requireOperands(3, "UTCBAR operands")
      val barrier = sharedWindow(operands.head)
      emit(MBarrierLoad(barrier))
      once(TensorMemoryCommit(barrier, Option.when(named("MULTICAST"))(source(operands(2)))))
    }
  }

  private def copy()(using context: VMCompilationContext): Unit = {
    import context.*
    requireOperands(2, "UTCCP operands")
    val (shape, replication) =
      if (named("4x32dp128bit")) (CopyShape.Rows32Bits128, Replication.Every)
      else if (named("2x64dp128bit_lw02_lw13")) (CopyShape.Rows64Bits128, Replication.PairsOfAlternate)
      else if (named("2x64dp128bit_lw01_lw23")) (CopyShape.Rows64Bits128, Replication.PairsOfAdjacent)
      else if (named("128dp128bit")) (CopyShape.Rows128Bits128, Replication.Once)
      else if (named("4dp256bit")) (CopyShape.Rows4Bits256, Replication.Once)
      else (CopyShape.Rows128Bits256, Replication.Once)
    val packing = if (named("U6x16P32")) Matrix.Packing.Six
    else if (named("U4x16P64")) Matrix.Packing.Four else Matrix.Packing.Bytes
    once(TensorMemoryCopy(Copy(shape, replication, packing, named("2CTA")), tensor(operands.head), wide(operands(1))))
  }

  /** UTC*MMA's operands: A, B and D, the sparse metadata address and the instruction descriptor in a register pair,
    * then the block scale pair, or the disabled-lane quad or zero-column pair, and enable-input-d with any
    * scale-input-d.
    */
  private def multiply()(using context: VMCompilationContext): Unit = {
    import context.*
    val guard = operands.indexWhere(OperandQueries.isPredicateAllowingBoth(_))
    if (guard != 6) unsupported("UTC*MMA operands")
    val extra  = operands(5)
    val scaled = extra match {
      case _: Operand.Address => true
      case _                  => false
    }
    val pair       = named("2CTA")
    val ws         = named("WS")
    val fromShared = modifiers.map(_.name).find(Set("GDESC", "TMEM")).contains("GDESC")
    val kind       = (operation.form: @unchecked) match {
      case _: Form.Utchmma => Kind.F16
      case _: Form.Utcimma => Kind.I8
      case _: Form.Utcqmma => if (scaled) Kind.MXF8F6F4 else Kind.F8F6F4
      case _: Form.Utcomma => Kind.MXF4NVF4
    }
    val lanes =
      if (scaled || ws || OperandQueries.isZero(named(extra))) Vec.empty[Value]
      else Vec.tabulate(if (pair) 8 else 4)(word(extra, _))
    once(TensorMemoryMultiply(Multiply(
      kind = kind,
      architecture = sm,
      pair = pair,
      ws = ws,
      vector = if (named("4X") || named("BLOCK16")) 4 else if (named("2X")) 2 else if (named("1X")) 1 else 0,
      shift = operands.lift(7).fold(0)(number(_).toInt),
      shiftA = named("ASHIFT"),
      d = tensor(operands(2)),
      a = if (fromShared) wide(operands.head) else tensor(operands.head),
      aInTensorMemory = !fromShared,
      b = wide(operands(1)),
      descriptor = word(operands(4)),
      enable = predicateSource(operands(6)),
      disabled = lanes,
      scales = Option.when(scaled)((a = word(extra), b = word(extra, 1))),
      metadata = Some(word(operands(3))),
      zeroColumns = Option.when(ws && !OperandQueries.isZero(named(extra)))(wide(extra)),
      sparseByDescriptor = true
    )))
  }
}
