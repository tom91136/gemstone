package net.kurobako.gemstone.isa.sass

import ModifierLowering.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.isa.nvidia.VMIR.Instruction.*
import net.kurobako.gemstone.isa.sass.Instruction.*
import net.kurobako.gemstone.vm.*

/** Memory accesses, atomics, constant loads, bulk copies, memory barriers and scoreboard waits. Accesses complete
  * synchronously, so dependency barriers only order memory.
  */
private object VMMemoryCompiler {
  private val constantLoadModifiers = Set(Modifier.`64`, Modifier.U8, Modifier.U16, Modifier.S8, Modifier.S16)
  private val asyncCopyModifiers    = Set(
    Modifier.E,
    Modifier.BYPASS,
    Modifier.`128`,
    Modifier.`64`,
    Modifier.LTC128B,
    Modifier.ZFILL
  )
  private val prefetchModifiers     = Set(Modifier.E, Modifier.PF1, Modifier.PF2)
  private val legacyMembarModifiers = Set(
    Vec(Modifier.GL),
    Vec(Modifier.GL, Modifier.IVALLD),
    Vec(Modifier.SYS, Modifier.IVALLD),
    Vec(Modifier.CTA, Modifier.IVALLD)
  )
  private val proxyViews      = Set(Modifier.S, Modifier.G, Modifier.T)
  private val membarModifiers = Set(
    Modifier.CTA,
    Modifier.GPU,
    Modifier.SYS,
    Modifier.SC,
    Modifier.ALL,
    Modifier.VC,
    Modifier.ASYNC,
    Modifier.MULTICAST
  )
  private val sharedLoadWidths = Set(Vec(Modifier.U, Modifier.`16`), Vec(Modifier.U, Modifier.`32`))
  private val memoryModifiers  = Set(
    Modifier.E,
    Modifier.`8`,
    Modifier.`16`,
    Modifier.`32`,
    Modifier.`64`,
    Modifier.`128`,
    Modifier.U,
    Modifier.U8,
    Modifier.S8,
    Modifier.U16,
    Modifier.S16,
    Modifier.STRONG,
    Modifier.GPU,
    Modifier.SYS,
    Modifier.CTA,
    Modifier.SM,
    Modifier.EF,
    Modifier.T,
    Modifier.P,
    Modifier.LU,
    Modifier.CI,
    Modifier.CG,
    Modifier.CS,
    Modifier.CV,
    Modifier.WT,
    Modifier.EL,
    Modifier.LTC128B,
    Modifier.CONSTANT,
    Modifier.NA,
    Modifier.EU,
    Modifier.LTC64B,
    Modifier.LTC256B,
    Modifier.MMIO
  )
  private val octetModifiers = Set(
    Modifier.E,
    Modifier.`256`,
    Modifier.EFL2,
    Modifier.ENL2,
    Modifier.ELL2,
    Modifier.DML2,
    Modifier.PML2,
    Modifier.RML2,
    Modifier.EF,
    Modifier.EL,
    Modifier.LU,
    Modifier.EU,
    Modifier.NA,
    Modifier.LTC64B,
    Modifier.LTC128B,
    Modifier.LTC256B,
    Modifier.CONSTANT,
    Modifier.STRONG,
    Modifier.CTA,
    Modifier.SM,
    Modifier.GPU,
    Modifier.SYS
  )
  private val popcCountModifiers   = Set(Modifier.E, Modifier.POPC, Modifier.INC, Modifier.`32`)
  private val arriveCountModifiers = Set(Modifier.E, Modifier.ARRIVE, Modifier.`64`)
  private val atomicOperations     = Set(
    Modifier.ADD,
    Modifier.MIN,
    Modifier.MAX,
    Modifier.EXCH,
    Modifier.CAS,
    Modifier.CAST,
    Modifier.AND,
    Modifier.OR,
    Modifier.XOR,
    Modifier.INC,
    Modifier.DEC
  )
  private val operandModifiers = atomicOperations ++ Set(
    Modifier.E,
    Modifier.U32,
    Modifier.S32,
    Modifier.U64,
    Modifier.S64,
    Modifier.`64`,
    Modifier.F32,
    Modifier.F64,
    Modifier.F16x2,
    Modifier.BF16x2,
    Modifier.FTZ,
    Modifier.RN,
    Modifier.SPIN,
    Modifier.STRONG,
    Modifier.GPU,
    Modifier.SYS,
    Modifier.CTA,
    Modifier.SM,
    Modifier.VC
  )
  private val textureLoadModifiers = Set(Modifier.LZ, Modifier.P, Modifier.T, Modifier.NODEP)
  private val ignoredTextureFields = Vec("TOFF1", "NDV", "DC")

  def loadOf(
      destinations: Vec[Value.Register],
      address: Value,
      width: ScalarBitWidth,
      signed: Boolean,
      access: MemoryAccess
  ): VMIR.Instruction = destinations.size match {
    case 1     => Load(destinations.head.slot, address, width, signed, access)
    case 2 | 4 => LoadVector(destinations.map(_.slot), address, width, signed, access)
    case size  => throw IllegalArgumentException(s"load destination count is unsupported: $size")
  }

  def storeOf(address: Value, values: Vec[Value], width: ScalarBitWidth, access: MemoryAccess): VMIR.Instruction =
    values.size match {
      case 1     => Store(address, values.head, width, access)
      case 2 | 4 => StoreVector(address, values, width, access)
      case size  => throw IllegalArgumentException(s"store value count is unsupported: $size")
    }

  def lower(using context: VMCompilationContext): Boolean = {
    import context.*
    (operation.form: @unchecked) match {
      case _: Form.Ldg | _: Form.Stg if modifiers.contains(Modifier.`256`) =>
        octet()
        true
      case _: Form.Ldg | _: Form.Stg | _: Form.Lds | _: Form.Sts |
          _: Form.Ld | _: Form.St | _: Form.Ldl | _: Form.Stl =>
        access()
        true
      case _: Form.Atoms | _: Form.Atom | _: Form.Atomg | _: Form.Red |
          _: Form.Redg | _: Form.Reds =>
        atomic()
        true
      case _: Form.Ldcu if modifiers == Vec(Modifier.`128`) =>
        requireOperands(2, "LDCU.128 operands")
        // four words from a constant offset, or from a uniform register's offset into the bank
        def word(index: Int): Operand = operands(1) match {
          case Operand.ConstantBank(bank, offset: Operand.Address) =>
            Operand.ConstantBank(bank, offset.copy(displacement = offset.displacement + index * 4))
          case Operand.ConstantBank(bank, offset) =>
            Operand.ConstantBank(bank, Operand.Integer(BigInt(number(offset)) + index * 4))
          case _ => unsupported("LDCU.128 requires a constant address")
        }
        Vec.tabulate(4)(index => source(word(index)))
          .zipWithIndex.foreach((value, index) => emit(UniformMove(destination(operands.head, index).slot, value)))
        true
      case _: Form.Uldc | _: Form.Ldc | _: Form.Ldcu =>
        requireModifiers(constantLoadModifiers)
        val bits: ScalarBitWidth =
          if (modifiers.contains(Modifier.`64`)) 64
          else if (modifiers.exists(PartialFunction.cond(_) { case Modifier.U8 | Modifier.S8 => true })) 8
          else if (modifiers.exists(PartialFunction.cond(_) { case Modifier.U16 | Modifier.S16 => true })) 16
          else 32
        val result = source(operands(1), bits)
        if (modifiers.exists(_.isSigned8Or16)) {
          val slot = result match {
            case value: Value.Register => value.slot
            case _                     => unsupported("signed constant load requires a register result")
          }
          val shift = Value.Immediate((32 - bits).toLong)
          emit(IntegerBinary(IntegerOperation.ShiftLeft, slot, result, shift, 32))
          emit(IntegerBinary(IntegerOperation.ShiftRightArithmetic, slot, result, shift, 32))
        }
        store(operands.head, result, bits)
        true
      // Kepler's shared atomics retry LDSLK and a guarded STSCUL until the store succeeds.
      case _: Form.Ldslk | _: Form.Stscul =>
        requireModifiers(Set(Modifier.`64`))
        requireOperands(3, "shared lock operands")
        val bits: ScalarBitWidth = if (modifiers.contains(Modifier.`64`)) 64 else 32
        val locking              = OperandQueries.is[Form.Ldslk](operation)
        val done                 = predicateTemp()
        val pointer              = address(operands(if (locking) 2 else 1), global = false, sharedAccess = true)
        if (locking) {
          val loaded = temp()
          emit(SharedLockLoad(done.slot, loaded.slot, pointer, bits))
          if (!OperandQueries.isZero(operands(1))) store(operands(1), loaded, bits)
        } else emit(SharedStoreUnlock(done.slot, pointer, source(operands(2), bits), bits))
        writePredicate(operands.head, done)
        true
      case _: Form.Ldgsts =>
        requireModifiers(asyncCopyModifiers)
        val bits = if (modifiers.contains(Modifier.`128`)) 128 else if (modifiers.contains(Modifier.`64`)) 64 else 32
        val registers = Vec.fill(bits / 32)(temp())
        val from      = address(operands(1), global = true)
        val to        = address(operands.head, global = false, sharedUniform = true, sharedAccess = true)
        if (operands.size == 3)
          zeroFilled(loadOf(registers, from, 32, false, MemoryAccess.Global(false)), registers, operands(2))
        else emit(loadOf(registers, from, 32, false, MemoryAccess.Global(false)))
        emit(storeOf(to, registers, 32, MemoryAccess.Shared))
        true
      case _: Form.Tex if sm == 35 =>
        keplerReadOnlyLoad()
        true
      // ptxas emits this after global atomics to invalidate a potentially stale cache line. The VM's atomic and
      // normal accesses share one coherent memory image, so there is no cached copy to invalidate.
      case _: Form.Cctl if sm == 35 && modifiers == Vec(Modifier.E, Modifier.IV) =>
        if (
          operands.size != 1 ||
          (operands.head match {
            case Operand.Address(Some(base), None, _, scale, None, None) => scale != 1 ||
              !OperandQueries.isRegister(base)
            case _ => true
          })
        ) unsupported("unsupported cache-line invalidation address")
        true
      case _: Form.Cctl if modifiers.contains(Modifier.IVALL) =>
        requireModifiers(Set(Modifier.IVALL))
        emit(Fence)
        true
      // Cache prefetch hints have no architectural result.
      case _: Form.Cctl if modifiers.contains(Modifier.PF1) || modifiers.contains(Modifier.PF2) =>
        requireModifiers(prefetchModifiers)
        true
      case _: Form.Membar
          if legacy && legacyMembarModifiers(modifiers) =>
        emit(Fence)
        true
      // fence.proxy.async on shared, global or tensor-map views; the VM keeps one view of memory.
      case _: Form.Fence =>
        if (
          !proxyViews.exists(view =>
            modifiers == Vec(Modifier.VIEW, Modifier.ASYNC, view)
          ) ||
          operands.nonEmpty
        ) unsupported("unsupported memory proxy fence")
        emit(Fence)
        true
      // Every form orders memory, including the asynchronous and multicast proxies, which the VM keeps coherent.
      case _: Form.Membar =>
        requireModifiers(membarModifiers)
        emit(Fence)
        true
      case _: Form.Errbar | _: Form.Cgaerrbar | _: Form.Utmacmdflush =>
        requireModifiers(Set.empty)
        emit(Fence)
        true
      // Supported shared-memory transfers finish synchronously before this acquire.
      case _: Form.Acqbulk | _: Form.Acqshminit =>
        requireModifiers(Set.empty)
        if (operands.nonEmpty) unsupported("bulk acquire operands")
        emit(Fence)
        true
      case _: Form.Texdepbar =>
        requireModifiers(Set.empty)
        if (operands.size != 1 || number(operands.head) < 0 || number(operands.head) > 63)
          unsupported("unsupported texture dependency barrier")
        true
      case _: Form.Ldgdepbar =>
        requireModifiers(Set.empty)
        true
      // The legacy mask-only form waits for these scoreboards.
      case _: Form.Depbar
          if verifiedLegacy && modifiers.isEmpty && scoreboardMask(operands) =>
        emit(Fence)
        true
      // A wait on every scoreboard, or on one until a count held in a uniform register.
      case _: Form.Depbar
          if operands.isEmpty || operands.lift(1).exists(OperandQueries.isRegister(_, RegisterBank.UR)) =>
        requireModifiers(Set(Modifier.LE, Modifier.ALL))
        if (operands.nonEmpty && (operands.size != 2 || !OperandQueries.isRegister(operands.head, RegisterBank.SB)))
          unsupported("unsupported dependency scoreboard")
        emit(Fence)
        true
      case _: Form.Depbar =>
        requireModifiers(Set(Modifier.LE))
        if (
          operands.size < 2 || !OperandQueries.isRegister(operands.head, RegisterBank.SB) || number(operands(1)) < 0 ||
          number(operands(1)) > 63
        ) unsupported("unsupported dependency scoreboard")
        if (operands.size > 2 && !scoreboardMask(operands.drop(2))) unsupported("unsupported dependency wait mask")
        emit(Fence)
        true
      case _ => false
    }
  }

  /** Runs `operation` when `enable` holds and zero-fills `registers` otherwise. */
  private def zeroFilled(memory: VMIR.Instruction, registers: Vec[Value.Register], enable: Operand)(using
      context: VMCompilationContext
  ): Unit = {
    import context.*
    val enabled = predicateSource(enable)
    guarded(memory, enabled)
    registers.foreach(register => guarded(Move(register.slot, Value.Immediate(0L), 32), enabled, true))
  }

  private def access()(using context: VMCompilationContext): Unit = {
    import context.*
    val shared = operation.form match {
      case _: Form.Lds | _: Form.Sts => true
      case _                         => false
    }
    val sharedLoad = OperandQueries.is[Form.Lds](operation)
    if (
      modifiers.exists(PartialFunction.cond(_) { case Modifier.`16` | Modifier.`32` => true }) &&
      (!legacy || sm < 50 ||
        !(sharedLoad && sharedLoadWidths(modifiers)))
    ) unsupported("unsupported explicit memory width")
    requireModifiers(memoryModifiers)
    // ptxas selects the U.128 size of LD and LDG only for a warp-uniform address, which reads as a plain 128-bit load
    val uniformWide = modifiers.contains(Modifier.`128`) &&
      (operation.form match {
        case _: Form.Ld | _: Form.Ldg => true
        case _                        => false
      })
    if (modifiers.contains(Modifier.U) && !sharedLoad && !uniformWide)
      unsupported("uniform-address modifier is supported only for LDS and the U.128 size of LD and LDG")
    if (modifiers.contains(Modifier.P) && (!OperandQueries.is[Form.Ldg](operation) || !legacy))
      unsupported("legacy texture load modifier on non-legacy LDG")
    val loading = operation.form match {
      case _: Form.Ld | _: Form.Ldg | _: Form.Ldl | _: Form.Lds => true
      case _                                                    => false
    }
    val global = operation.form match {
      case _: Form.Ldg | _: Form.Stg => true
      case _                         => false
    }
    val generic = operation.form match {
      case _: Form.Ld | _: Form.St => true
      case _                       => false
    }
    if (generic && verifiedLegacy && operands.size != 2)
      unsupported("legacy generic-memory auxiliary predicate requires a verified address sequence")
    val bits =
      if (modifiers.contains(Modifier.`128`)) 128
      else if (modifiers.contains(Modifier.`64`)) 64
      else if (modifiers.exists(PartialFunction.cond(_) { case Modifier.`8` | Modifier.U8 | Modifier.S8 => true })) 8
      else if (modifiers.exists(PartialFunction.cond(_) { case Modifier.`16` | Modifier.U16 | Modifier.S16 => true }))
        16
      else 32
    val pointer = address(
      operands(if (loading) 1 else 0),
      global || generic,
      sharedUniform = shared,
      sharedAccess = shared,
      localAccess = !(shared || global || generic)
    )
    val data    = operands(if (loading) 0 else 1)
    val words   = math.max(1, bits / 32)
    val results =
      if (!loading) Vec.empty[Value.Register]
      else if (OperandQueries.isZero(data)) Vec.fill(words)(temp())
      else Vec.tabulate(words)(destination(data, _))
    // ld.mmio and st.mmio are relaxed system-scope accesses
    val system = modifiers.exists(PartialFunction.cond(_) { case Modifier.STRONG | Modifier.MMIO => true }) && {
      if (!global && !generic) unsupported("STRONG memory scope requires a generic or global memory instruction")
      val scope = modifiers.filter(PartialFunction.cond(_) {
        case Modifier.CTA | Modifier.GPU | Modifier.SYS | Modifier.SM => true
      })
      if (scope.size != 1) unsupported("STRONG memory instruction requires exactly one scope")
      scope.head == Modifier.SYS
    }
    val space =
      if (generic) MemoryAccess.Generic(system)
      else if (global) MemoryAccess.Global(system)
      else if (shared) MemoryAccess.Shared
      else MemoryAccess.Local
    val elementWidth = math.min(bits, 32).refine[ScalarBitWidth].fold(unsupported(_), identity)
    val memory       =
      if (loading)
        loadOf(results, pointer, elementWidth, bits < 32 && modifiers.exists(_.isSigned8Or16), space)
      else storeOf(pointer, Vec.tabulate(words)(register(data, _)), elementWidth, space)
    if (loading && operands.size == 3)
      zeroFilled(memory, if (OperandQueries.isZero(data)) Vec.empty else results, operands(2))
    else {
      if (operands.size != 2) unsupported("unsupported memory operands")
      emit(memory)
    }
  }

  /** A 256-bit global access, as `ld.global.v8.b32` and `st.global.v8.b32` from sm_100: words 0 to 3 in the group a
    * load names second and a store first, words 4 to 7 in the other, and only the words a trailing mask selects. A load
    * reads every selected word before it writes any register, as its group may hold its own address.
    */
  private def octet()(using context: VMCompilationContext): Unit = {
    import context.*
    requireModifiers(octetModifiers)
    val loading = OperandQueries.is[Form.Ldg](operation)
    if (operands.size < 3 || operands.size > 4)
      unsupported("256-bit access requires two register groups and an address")
    val mask        = operands.lift(3).fold(0xffL)(number(_))
    val pointer     = address(operands(if (loading) 2 else 0), global = true)
    val access      = MemoryAccess.Global(modifiers.contains(Modifier.STRONG) && modifiers.contains(Modifier.SYS))
    val (low, high) = if (loading) (operands(1), operands.head) else (operands(1), operands(2))
    def word(index: Int): (Operand, Int) = if (index < 4) (low, index) else (high, index - 4)
    val selected                         = (0 until 8).filter(index => (mask & (1L << index)) != 0)
    if (mask == 0xffL) {
      // the whole 256-bit group is one access, which the hardware aligns to 32 bytes
      if (loading) {
        val loaded = Vec.fill(8)(temp())
        emit(LoadVector(loaded.map(_.slot), pointer, 32, false, access))
        selected.foreach { index =>
          val (group, offset) = word(index)
          if (!OperandQueries.isZero(group)) emit(Move(destination(group, offset).slot, loaded(index), 32))
        }
      } else
        emit(StoreVector(
          pointer,
          Vec.from(selected.map(index => register(word(index)._1, word(index)._2))),
          32,
          access
        ))
    } else if (loading) {
      val loaded = selected.map { index =>
        val value = temp()
        emit(Load(value.slot, Value.Address(pointer, index * 4L), 32, false, access))
        index -> value
      }
      loaded.foreach { (index, value) =>
        val (group, offset) = word(index)
        if (!OperandQueries.isZero(group)) emit(Move(destination(group, offset).slot, value, 32))
      }
    } else
      selected.foreach { index =>
        val (group, offset) = word(index)
        emit(Store(Value.Address(pointer, index * 4L), register(group, offset), 32, access))
      }
  }

  private def atomic()(using context: VMCompilationContext): Unit =
    if (context.modifiers.exists(PartialFunction.cond(_) { case Modifier.POPC | Modifier.ARRIVE => true })) counted()
    else operand()

  /** `POPC.INC` adds one for each lane, as ptxas emits `atomicAdd(p, 1)` on shared memory from sm_80, and `ARRIVE`
    * arrives once for each lane on an sm_80 to sm_89 mbarrier, whose arrivals and phase live in its high word; both
    * return the old value. Lanes arrive one after another, so each sees the arrivals of the lanes before it.
    */
  /** Whether the first operand is a predicate destination. */
  private def predicateFirst(operands: Vec[Operand]): Boolean = operands.headOption.exists {
    case Operand.Predicate(_, _, _) => true
    case _                          => false
  }

  private def counted()(using context: VMCompilationContext): Unit = {
    import context.*
    val counting: (AtomicOperation, ScalarBitWidth) =
      if (modifiers.contains(Modifier.POPC)) {
        requireModifiers(popcCountModifiers)
        (AtomicOperation.Add, 32)
      } else {
        requireModifiers(arriveCountModifiers)
        (AtomicOperation.BarrierArrive, 64)
      }
    val (arithmetic, bits) = counting
    val shared             = OperandQueries.is[Form.Atoms](operation)
    val predicated         = predicateFirst(operands)
    if (predicated) writePredicate(operands.head, Value.Predicate(VMRegisters.Predicate.True.slot))
    val values = if (predicated) operands.tail else operands
    if (values.size != 2) unsupported("counted atomic requires a destination and an address")
    val result = temp()
    emit(Atomic(
      Some(result.slot),
      address(values(1), !shared, sharedAccess = shared),
      Value.Immediate(1L),
      bits,
      if (shared) MemorySpace.Shared else MemorySpace.Generic,
      arithmetic
    ))
    store(values.head, result, bits)
  }

  private def operand()(using context: VMCompilationContext): Unit = {
    import context.*
    requireModifiers(operandModifiers)
    val selected = modifiers.find(atomicOperations).getOrElse(unsupported("unknown atomic operation"))
    val packed   = modifiers.find(_.isPackedHalf)
    val floating = modifiers.exists(PartialFunction.cond(_) { case Modifier.F32 | Modifier.F64 => true }) ||
      packed.nonEmpty
    if (floating && selected != Modifier.ADD) unsupported("floating atomics other than ADD are not implemented")
    // CAST stores and reports as CAST.SPIN does on an RTX 4060 Ti; SPIN only hints at contention
    if (modifiers.contains(Modifier.SPIN) && selected != Modifier.CAST) unsupported("SPIN requires CAST")
    val bits: ScalarBitWidth =
      if (
        modifiers.exists(PartialFunction.cond(_) { case Modifier.`64` | Modifier.U64 | Modifier.S64 | Modifier.F64 =>
          true
        })
      ) 64
      else 32
    // ptxas spells signed minima and maxima .S32 and .S64 and leaves unsigned ones untyped or .64
    val signed     = modifiers.exists(_.isSigned32Or64)
    val predicated = predicateFirst(operands)
    val reduction  = operation.form match {
      case _: Form.Red | _: Form.Redg | _: Form.Reds => true
      case _                                         => false
    }
    // ATOMG, and ATOM from sm_90, lead with a predicate that holds once the operation is performed, as it always is:
    // the VM performs a generic atomic on whichever window its address falls in. CAST.SPIN stores the new value only
    // over the expected one and reports whether it did in its register or, for ATOMS from sm_90, a lone predicate.
    val spin      = selected == Modifier.CAST
    val performed = predicated && (!spin || operands.size == 5)
    if (performed) writePredicate(operands.head, Value.Predicate(VMRegisters.Predicate.True.slot))
    val values = if (performed) operands.tail else operands
    val first  = if (reduction) 0 else 1
    val shared = operation.form match {
      case _: Form.Atoms | _: Form.Reds => true
      case _                            => false
    }
    val pointer = address(values(first), !shared, sharedAccess = shared)
    val value   = source(values(first + 1), bits)
    val result  = temp()
    // ATOM and RED take generic addresses, which may name shared memory of this or another cluster CTA.
    val space = operation.form match {
      case _: Form.Atoms | _: Form.Reds => MemorySpace.Shared
      case _: Form.Atom | _: Form.Red   => MemorySpace.Generic
      case _                            => MemorySpace.Global
    }
    emit(
      if (selected == Modifier.CAS || spin)
        AtomicCompareExchange(result.slot, pointer, value, source(values(first + 2), bits), bits, space)
      else
        Atomic(
          Some(result.slot),
          pointer,
          value,
          bits,
          space,
          packed match {
            case Some(Modifier.F16x2) => AtomicOperation.PackedHalfAdd
            case Some(_)              => AtomicOperation.PackedBFloat16Add
            case None if floating     => AtomicOperation.FloatAdd
            case None                 => selected.atomicOperation(signed)
          }
        )
    )
    if (spin) {
      val stored = compare(Comparison.Equal, NumericFormat.Bits(if (bits == 64) 64 else 32), result, value)
      if (!performed && predicated) writePredicate(values.head, stored) else store(values.head, bit(stored, bits), bits)
    } else if (!reduction) store(values.head, result, bits)
  }

  private def keplerReadOnlyLoad()(using context: VMCompilationContext): Unit = {
    import context.*
    requireModifiers(textureLoadModifiers)
    val fields =
      instruction.machine.map(_.bindings).getOrElse(unsupported("Kepler TEX requires encoded texture fields"))
    val size     = fields.getOrElse("tid", BigInt(-1)).toInt
    val geometry = fields.getOrElse("ParamA", BigInt(-1)).toInt
    val count    = if (size == 5) 2 else if (size == 6) 4 else 1
    if (
      size < 0 || size > 6 || !Set(0, 2)(geometry) || fields.get("LOD") != Some(BigInt(1)) ||
      fields.get("Rb") != Some(BigInt(255)) || fields.get("wmsk") != Some(BigInt((1 << count) - 1)) ||
      ignoredTextureFields.exists(name => fields.get(name) != Some(BigInt(0)))
    ) unsupported("texture operation is not a Kepler read-only global load")
    val width: ScalarBitWidth = if (size < 2) 8 else if (size < 4) 16 else 32
    emit(loadOf(
      Vec.tabulate(count)(destination(operands.head, _)),
      source(operands(1), if (geometry == 2) 64 else 32),
      width,
      size == 1 || size == 3,
      MemoryAccess.Global(false)
    ))
  }
}
