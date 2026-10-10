package net.kurobako.gemstone.isa.sass

import ModifierSemantics.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.isa.nvidia.VMIR.Instruction.*
import net.kurobako.gemstone.isa.sass.Instruction.*
import net.kurobako.gemstone.isa.sass.Instruction as SASSInstruction

/** Memory accesses, atomics, constant loads, bulk copies, memory barriers and scoreboard waits. Accesses complete
  * synchronously, so dependency barriers only order memory.
  */
private object VMMemoryCompiler {

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
    (operation: @unchecked) match {
      case _: SASSInstruction.Ldg | _: SASSInstruction.Stg | _: SASSInstruction.Lds | _: SASSInstruction.Sts |
          _: SASSInstruction.Ld | _: SASSInstruction.St | _: SASSInstruction.Ldl | _: SASSInstruction.Stl =>
        access()
        true
      case _: SASSInstruction.Atoms | _: SASSInstruction.Atom | _: SASSInstruction.Atomg | _: SASSInstruction.Red |
          _: SASSInstruction.Redg =>
        atomic()
        true
      case _: SASSInstruction.Ldcu if modifiers == Vec(Modifier.`128`) =>
        requireOperands(2, "LDCU.128 operands")
        val (bank, offset) = operands(1) match {
          case Operand.ConstantBank(bank, offset) => (bank, number(offset))
          case _                                  => unsupported("LDCU.128 requires a constant address")
        }
        Vec.tabulate(4)(index => source(Operand.ConstantBank(bank, Operand.Integer(BigInt(offset) + index * 4))))
          .zipWithIndex.foreach((value, index) => emit(UniformMove(destination(operands.head, index).slot, value)))
        true
      case _: SASSInstruction.Uldc | _: SASSInstruction.Ldc | _: SASSInstruction.Ldcu =>
        requireModifiers(Set(Modifier.`64`, Modifier.U8, Modifier.U16, Modifier.S8, Modifier.S16))
        val bits: ScalarBitWidth =
          if (modifiers.contains(Modifier.`64`)) 64
          else if (modifiers.exists(Set(Modifier.U8, Modifier.S8))) 8
          else if (modifiers.exists(Set(Modifier.U16, Modifier.S16))) 16
          else 32
        val result = source(operands(1), bits)
        if (modifiers.exists(Set(Modifier.S8, Modifier.S16))) {
          val slot = result match {
            case value: Value.Register => value.slot
            case _                     => unsupported("signed constant load requires a register result")
          }
          val shift = Value.Immediate((32 - bits.raw).toLong)
          emit(IntegerBinary(IntegerOperation.ShiftLeft, slot, result, shift, 32))
          emit(IntegerBinary(IntegerOperation.ShiftRightArithmetic, slot, result, shift, 32))
        }
        store(operands.head, result, bits)
        true
      case _: SASSInstruction.Ldgsts =>
        requireModifiers(Set(
          Modifier.E,
          Modifier.BYPASS,
          Modifier.`128`,
          Modifier.`64`,
          Modifier.LTC128B,
          Modifier.ZFILL
        ))
        val bits = if (modifiers.contains(Modifier.`128`)) 128 else if (modifiers.contains(Modifier.`64`)) 64 else 32
        val registers = Vec.fill(bits / 32)(temp())
        val from      = address(operands(1), global = true)
        val to        = address(operands.head, global = false, sharedUniform = true, sharedAccess = true)
        if (operands.size == 3)
          zeroFilled(loadOf(registers, from, 32, false, MemoryAccess.Global(false)), registers, operands(2))
        else emit(loadOf(registers, from, 32, false, MemoryAccess.Global(false)))
        emit(storeOf(to, registers, 32, MemoryAccess.Shared))
        true
      case _: SASSInstruction.Tex if sm == 35 =>
        keplerReadOnlyLoad()
        true
      // ptxas emits this after global atomics to invalidate a potentially stale cache line. The VM's atomic and
      // normal accesses share one coherent memory image, so there is no cached copy to invalidate.
      case _: SASSInstruction.Cctl if sm == 35 && modifiers == Vec(Modifier.E, Modifier.IV) =>
        if (operands.size != 1 || !OperandQueries.plainAddress(operands.head))
          unsupported("unsupported cache-line invalidation address")
        true
      case _: SASSInstruction.Cctl if modifiers.contains(Modifier.IVALL) =>
        requireModifiers(Set(Modifier.IVALL))
        emit(Fence)
        true
      // Cache prefetch hints have no architectural result.
      case _: SASSInstruction.Cctl if modifiers.contains(Modifier.PF1) || modifiers.contains(Modifier.PF2) =>
        requireModifiers(Set(Modifier.E, Modifier.PF1, Modifier.PF2))
        true
      case _: SASSInstruction.Membar if legacy && Set(Vec(Modifier.GL), Vec(Modifier.GL, Modifier.IVALLD))(modifiers) =>
        emit(Fence)
        true
      case _: SASSInstruction.Fence =>
        if (modifiers != Vec(Modifier.VIEW, Modifier.ASYNC, Modifier.S) || operands.nonEmpty)
          unsupported("unsupported memory proxy fence")
        emit(Fence)
        true
      case _: SASSInstruction.Membar =>
        requireModifiers(Set(Modifier.CTA, Modifier.GPU, Modifier.SYS, Modifier.SC, Modifier.ALL))
        emit(Fence)
        true
      case _: SASSInstruction.Errbar | _: SASSInstruction.Cgaerrbar | _: SASSInstruction.Utmacmdflush =>
        requireModifiers(Set.empty)
        emit(Fence)
        true
      // Supported shared-memory transfers finish synchronously before this acquire.
      case _: SASSInstruction.Acqbulk | _: SASSInstruction.Acqshminit =>
        requireModifiers(Set.empty)
        if (operands.nonEmpty) unsupported("bulk acquire operands")
        emit(Fence)
        true
      case _: SASSInstruction.Ublkcp =>
        bulkCopy()
        true
      case _: SASSInstruction.Syncs =>
        sharedBarrier()
        true
      case _: SASSInstruction.Texdepbar =>
        requireModifiers(Set.empty)
        if (!legacy || operands.size != 1 || number(operands.head) < 0 || number(operands.head) > 63)
          unsupported("unsupported texture dependency barrier")
        true
      case _: SASSInstruction.Ldgdepbar =>
        requireModifiers(Set.empty)
        true
      // The legacy mask-only form waits for these scoreboards.
      case _: SASSInstruction.Depbar
          if verifiedLegacy && modifiers.isEmpty && scoreboardMask(operands) =>
        emit(Fence)
        true
      case _: SASSInstruction.Depbar =>
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

  private def scoreboardMask(values: Vec[Operand]): Boolean = values match {
    case Vec(Operand.Group(entries)) => entries.nonEmpty &&
      entries.forall(v => OperandQueries.integer(v).exists(n => n >= 0 && n <= 5))
    case Vec(value) => OperandQueries.integer(value).exists(mask => mask >= 0 && mask <= 63)
    case _          => false
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
    val shared = operation match {
      case _: SASSInstruction.Lds | _: SASSInstruction.Sts => true
      case _                                               => false
    }
    val sharedLoad = OperandQueries.is[SASSInstruction.Lds](operation)
    if (
      modifiers.exists(Set(Modifier.`16`, Modifier.`32`)) &&
      (!verifiedLegacy ||
        !(sharedLoad && Set(Vec(Modifier.U, Modifier.`16`), Vec(Modifier.U, Modifier.`32`))(modifiers)))
    ) unsupported("unsupported explicit memory width")
    requireModifiers(Set(
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
      Modifier.CONSTANT
    ))
    if (modifiers.contains(Modifier.U) && !sharedLoad) unsupported("uniform-address modifier is supported only for LDS")
    if (modifiers.contains(Modifier.P) && (!OperandQueries.is[SASSInstruction.Ldg](operation) || !legacy))
      unsupported("legacy texture load modifier on non-legacy LDG")
    val loading = operation match {
      case _: SASSInstruction.Ld | _: SASSInstruction.Ldg | _: SASSInstruction.Ldl | _: SASSInstruction.Lds => true
      case _                                                                                                => false
    }
    val global = operation match {
      case _: SASSInstruction.Ldg | _: SASSInstruction.Stg => true
      case _                                               => false
    }
    val generic = operation match {
      case _: SASSInstruction.Ld | _: SASSInstruction.St => true
      case _                                             => false
    }
    if (generic && verifiedLegacy && operands.size != 2)
      unsupported("legacy generic-memory auxiliary predicate requires a verified address sequence")
    val bits =
      if (modifiers.contains(Modifier.`128`)) 128
      else if (modifiers.contains(Modifier.`64`)) 64
      else if (modifiers.exists(Set(Modifier.`8`, Modifier.U8, Modifier.S8))) 8
      else if (modifiers.exists(Set(Modifier.`16`, Modifier.U16, Modifier.S16))) 16
      else 32
    val pointer = address(
      operands(if (loading) 1 else 0),
      global || generic,
      sharedUniform = shared,
      sharedAccess = shared,
      allowGlobalU32UniformBase = OperandQueries.is[SASSInstruction.Ldg](operation) ||
        (OperandQueries.is[SASSInstruction.Stg](operation) &&
          modifiers.filterNot(_ == Modifier.STRONG) == Vec(Modifier.E, Modifier.U8, Modifier.SYS))
    )
    val data    = operands(if (loading) 0 else 1)
    val words   = math.max(1, bits / 32)
    val results =
      if (!loading) Vec.empty[Value.Register]
      else if (OperandQueries.isZero(data)) Vec.fill(words)(temp())
      else Vec.tabulate(words)(destination(data, _))
    val system = modifiers.contains(Modifier.STRONG) && {
      if (!global && !generic) unsupported("STRONG memory scope requires a generic or global memory instruction")
      val scope = modifiers.filter(Set(Modifier.CTA, Modifier.GPU, Modifier.SYS, Modifier.SM))
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
        loadOf(results, pointer, elementWidth, bits < 32 && modifiers.exists(Set(Modifier.S8, Modifier.S16)), space)
      else storeOf(pointer, Vec.tabulate(words)(register(data, _)), elementWidth, space)
    if (loading && operands.size == 3)
      zeroFilled(memory, if (OperandQueries.isZero(data)) Vec.empty else results, operands(2))
    else {
      if (operands.size != 2) unsupported("unsupported memory operands")
      emit(memory)
    }
  }

  private def atomic()(using context: VMCompilationContext): Unit = {
    import context.*
    val operations = Set(
      Modifier.ADD,
      Modifier.MIN,
      Modifier.MAX,
      Modifier.EXCH,
      Modifier.CAS,
      Modifier.AND,
      Modifier.OR,
      Modifier.XOR
    )
    requireModifiers(operations ++ Set(
      Modifier.E,
      Modifier.U32,
      Modifier.S32,
      Modifier.U64,
      Modifier.S64,
      Modifier.`64`,
      Modifier.STRONG,
      Modifier.GPU,
      Modifier.SYS,
      Modifier.CTA
    ))
    val selected             = modifiers.find(operations).getOrElse(unsupported("unknown atomic operation"))
    val bits: ScalarBitWidth = if (modifiers.exists(Set(Modifier.`64`, Modifier.U64, Modifier.S64))) 64 else 32
    val signed               = !Set(Modifier.EXCH, Modifier.CAS, Modifier.AND, Modifier.OR, Modifier.XOR)(selected) &&
      (modifiers.exists(Set(Modifier.S32, Modifier.S64)) ||
        (!modifiers.exists(Set(Modifier.U32, Modifier.U64)) && Set(Modifier.MIN, Modifier.MAX)(selected)))
    val predicated = OperandQueries.is[SASSInstruction.Atomg](operation)
    // ATOMG adds a predicate destination; only the compiler's discarded predicate result is supported.
    if (predicated && !operands.headOption.contains(OperandQueries.True))
      unsupported("atomic predicate result is not implemented")
    val values    = if (predicated) operands.tail else operands
    val reduction = operation match {
      case _: SASSInstruction.Red | _: SASSInstruction.Redg => true
      case _                                                => false
    }
    val first   = if (reduction) 0 else 1
    val shared  = OperandQueries.is[SASSInstruction.Atoms](operation)
    val pointer = address(values(first), !shared, sharedAccess = shared)
    val value   = source(values(first + 1), bits)
    val result  = temp()
    val space   = if (shared) MemorySpace.Shared else MemorySpace.Global
    emit(
      if (selected == Modifier.CAS)
        AtomicCompareExchange(result.slot, pointer, value, source(values(first + 2), bits), bits, space)
      else Atomic(Some(result.slot), pointer, value, bits, space, atomicOperation(selected, signed))
    )
    if (!reduction) store(values.head, result, bits)
  }

  private def keplerReadOnlyLoad()(using context: VMCompilationContext): Unit = {
    import context.*
    requireModifiers(Set(Modifier.LZ, Modifier.P, Modifier.T, Modifier.NODEP))
    val fields =
      instruction.machine.map(_.bindings).getOrElse(unsupported("Kepler TEX requires encoded texture fields"))
    val size     = fields.getOrElse("tid", BigInt(-1)).toInt
    val geometry = fields.getOrElse("ParamA", BigInt(-1)).toInt
    val count    = if (size == 5) 2 else if (size == 6) 4 else 1
    if (
      size < 0 || size > 6 || !Set(0, 2)(geometry) || fields.get("LOD") != Some(BigInt(1)) ||
      fields.get("Rb") != Some(BigInt(255)) || fields.get("wmsk") != Some(BigInt((1 << count) - 1)) ||
      Vec("TOFF1", "NDV", "DC").exists(name => fields.get(name) != Some(BigInt(0)))
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

  private def bulkCopy()(using context: VMCompilationContext): Unit = {
    import context.*
    def sharedAddress(operand: Operand) = address(operand, global = false, sharedUniform = true, sharedAccess = true)
    val elected                         = predicateTemp()
    if (
      modifiers == Vec(Modifier.G, Modifier.S) || modifiers == Vec(Modifier.G, Modifier.S, Modifier.DST_G_BYTE_MASK)
    ) {
      requireOperands(3, "bulk store operands")
      val to            = address(operands.head, global = true)
      val from          = sharedAddress(operands(1))
      val descriptor    = source(operands(2))
      val (bytes, mask) =
        if (modifiers.contains(Modifier.DST_G_BYTE_MASK))
          (
            binary(IntegerOperation.BitwiseAnd, descriptor, Value.Immediate(0xffffL)),
            binary(IntegerOperation.ShiftRightLogical, descriptor, Value.Immediate(16L))
          )
        else (descriptor, Value.Immediate(0xffffL))
      val size = binary(IntegerOperation.ShiftLeft, bytes, Value.Immediate(4L))
      emit(Elect(None, elected.slot, Value.ActiveMask))
      guarded(AsyncBulkStore(to, from, size, mask), elected)
    } else {
      if (modifiers != Vec(Modifier.S, Modifier.G) || operands.size != 3) unsupported("unsupported uniform bulk copy")
      val base = operands.head match {
        case Operand.Address(Some(base), None, displacement, scale, None, None)
            if base.bank == RegisterBank.UR && displacement == 0 && scale.raw == 1 => base
        case _ => unsupported("uniform bulk copy destination descriptor")
      }
      val to   = sharedAddress(operands.head)
      val from = address(operands(1), global = true)
      // The adjacent destination word names the barrier; size is measured in 16-byte chunks.
      val size = binary(IntegerOperation.ShiftLeft, source(operands(2)), Value.Immediate(4L))
      emit(Elect(None, elected.slot, Value.ActiveMask))
      val barrier =
        binary(IntegerOperation.BitwiseOr, register(base, 1), Value.Immediate(VMAddressSpace.SharedBase), 64)
      guarded(MBarrierLoad(barrier), elected)
      guarded(AsyncBulkCopy(to, from, size, barrier), elected)
    }
  }

  private def sharedBarrier()(using context: VMCompilationContext): Unit = {
    import context.*
    def pointer(operand: Operand) = address(operand, global = false, sharedUniform = true, sharedAccess = true)
    if (modifiers == Vec(Modifier.EXCH, Modifier.`64`)) {
      if (operands.size != 3 || !OperandQueries.isZero(operands.head)) unsupported("mbarrier initialisation result")
      val barrier = pointer(operands(1))
      emit(Store(barrier, source(operands(2), 64), 64, MemoryAccess.Shared))
      emit(MBarrierLoad(barrier))
    } else if (modifiers == Vec(Modifier.CCTL, Modifier.IVALL)) {
      if (operands.nonEmpty) unsupported("barrier cache invalidation operands")
      emit(Fence)
    } else if (
      modifiers == Vec(Modifier.ARRIVE, Modifier.TRANS64) ||
      modifiers == Vec(Modifier.ARRIVE, Modifier.TRANS64, Modifier.A1T0)
    ) {
      if (operands.size != 3 || !OperandQueries.isZero(operands.head))
        unsupported("mbarrier arrival token representation")
      val barrier = pointer(operands(1))
      emit(MBarrierLoad(barrier))
      emit(MBarrierArrive(None, barrier, Value.Immediate(1L), source(operands(2))))
    } else if (modifiers == Vec(Modifier.PHASECHK, Modifier.TRANS64, Modifier.TRYWAIT)) {
      requireOperands(3, "mbarrier phase-check operands")
      val barrier = pointer(operands(1))
      val phase   = binary(IntegerOperation.ShiftRightLogical, source(operands(2)), Value.Immediate(31L))
      emit(MBarrierLoad(barrier))
      emit(MBarrierTest(destination = predicate(operands.head).slot, address = barrier, phase = phase, parity = true))
    } else if (modifiers == Vec(Modifier.CCTL, Modifier.IV)) {
      requireOperands(1, "mbarrier invalidation operands")
      val barrier = pointer(operands.head)
      emit(MBarrierLoad(barrier))
      emit(MBarrierInvalidate(barrier))
    } else unsupported("unsupported shared memory barrier operation")
  }
}
