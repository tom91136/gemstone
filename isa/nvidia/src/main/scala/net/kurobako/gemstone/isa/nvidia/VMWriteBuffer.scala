package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*

object VMWriteBuffer {
  def located(fault: Fault, pc: Pc, lane: WorkgroupLane): Fault = fault match {
    case Fault.Memory(detail) => Fault.Memory(s"pc $pc, lane $lane: $detail")
    case other                => other
  }

  private def update(previous: Long, width: ScalarBitWidth, update: AtomicUpdate): Long = update match {
    case AtomicUpdate.Operation(operation, value)        => operation(previous, value, width)
    case AtomicUpdate.CompareExchange(comparator, value) =>
      if (Bits.truncate(previous, width) == Bits.truncate(comparator, width)) value else previous
  }
}

/** Effects of one instruction, staged so that every lane reads its operands before any destination changes. Register
  * and ordinary memory writes use parallel arrays to avoid allocating a [[VMWrite]] per lane.
  */
@SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var", "scalafix:DisableSyntax.return"))
class VMWriteBuffer {
  import VMWriteBuffer.*

  private val ObjectWrite   = 0.toByte
  private val RegisterWrite = 1.toByte
  private val MemoryWrite   = 2.toByte
  private var kinds         = Array.ofDim[Byte](64)
  private var writes        = Array.ofDim[VMWrite | Null](64)
  private var lanes         = Array.ofDim[WorkgroupLane](64)
  private var registers     = Array.ofDim[Register](64)
  private var values        = Array.ofDim[Long](64)
  private var addresses     = Array.ofDim[Long](64)
  private var widths        = Array.ofDim[ScalarBitWidth](64)
  private var pending       = 0
  private var system        = false

  def stage(write: VMWrite): Unit = {
    write match {
      case VMWrite.Memory(_, _, _, _, true) => system = true
      case _                                => ()
    }
    ensureCapacity()
    kinds(pending) = ObjectWrite
    writes(pending) = write
    pending += 1
  }

  def stageRegister(lane: WorkgroupLane, register: Register, value: Long): Unit = {
    ensureCapacity()
    kinds(pending) = RegisterWrite
    lanes(pending) = lane
    registers(pending) = register
    values(pending) = value
    pending += 1
  }

  def stageMemory(lane: WorkgroupLane, address: GuestAddress, width: ScalarBitWidth, value: Long): Unit = {
    val previous = pending - 1
    // Coalesce identical non-atomic stores within a warp. Repeated unlock writes
    // must not clear a lock reacquired by another workgroup between lane writes.
    val duplicate = previous >= 0 && kinds(previous) == MemoryWrite &&
      (lanes(previous).raw >>> 5) ==
      (lane.raw >>> 5) &&
      addresses(previous) == address.raw && widths(previous) == width && values(previous) == value
    if (!duplicate) {
      ensureCapacity()
      kinds(pending) = MemoryWrite
      lanes(pending) = lane
      addresses(pending) = address.raw
      widths(pending) = width
      values(pending) = value
      pending += 1
    }
  }

  def clear(): Unit = clear(pending)

  private def ensureCapacity(): Unit =
    if (pending == kinds.length) {
      val capacity = Math.multiplyExact(kinds.length, 2)
      kinds = java.util.Arrays.copyOf(kinds, capacity)
      writes = java.util.Arrays.copyOf(writes, capacity)
      lanes = Array.copyOf(lanes, capacity)
      registers = Array.copyOf(registers, capacity)
      values = java.util.Arrays.copyOf(values, capacity)
      addresses = java.util.Arrays.copyOf(addresses, capacity)
      widths = Array.copyOf(widths, capacity)
    }

  private def validate(memory: AddressSpace, address: Long, size: Int >= 0): Either[Fault, Unit] =
    memory.locate(GuestAddress(address), size).map(_ => ())

  private def validate(memory: AddressSpace, write: VMWrite, pc: Pc): Either[Fault, Unit] = write match {
    case VMWrite.Memory(lane, address, width, _, _) =>
      validate(memory, address.raw, width.toScalarByteWidth).left.map(located(_, pc, lane))
    case VMWrite.MemoryBytes(lane, address, bytes) =>
      validate(memory, address.raw, bytes.nonNegativeSize).left.map(located(_, pc, lane))
    case VMWrite.Atomic(lane, _, address, width, false, _) =>
      validate(memory, address, width.toScalarByteWidth).left.map(located(_, pc, lane))
    case _ => Right(())
  }

  private def apply(frame: VMLanes, workgroup: VMWorkgroup, memory: AddressSpace, write: VMWrite): Either[Fault, Unit] =
    write match {
      case VMWrite.Register(lane, register, value)   => Right(frame(lane).setRegister(register, value))
      case VMWrite.Predicate(lane, predicate, value) => Right(frame(lane).setPredicate(predicate, value))
      case VMWrite.ConditionFlag(lane, flag, value)  => Right(frame(lane).conditionCodes.write(flag, value))
      case VMWrite.AddThreeCarry(lane, value)        => Right(frame(lane).conditionCodes.writeThreeInputCarry(value))
      case VMWrite.Parameter(lane, parameter, offset, width, value) =>
        Right(frame(lane).writeParameter(parameter, offset, width, value))
      case VMWrite.ParameterBuffer(address, width, value) =>
        Right(workgroup.buffer(address, width.toScalarByteWidth.widen).foreach((bytes, offset) =>
          LittleEndian.write(value, bytes, offset, width.toScalarByteWidth)
        ))
      case VMWrite.Shared(address, width, value)           => Right(workgroup.writeShared(address, width, value))
      case VMWrite.Local(lane, address, width, value)      => Right(workgroup.writeLocal(lane, address, width, value))
      case VMWrite.Memory(_, address, width, value, false) => memory.writeValue(address, value, width.toScalarByteWidth)
      case VMWrite.Memory(_, _, _, _, true)                => Right(())
      case VMWrite.MemoryBytes(_, address, bytes)          => memory.write(address, bytes)
      case VMWrite.Atomic(lane, destination, address, width, true, change) =>
        val previous = Bits.truncate(workgroup.readShared(address, width), width)
        workgroup.writeShared(address, width, update(previous, width, change))
        Right(destination.foreach(frame(lane).setRegister(_, previous)))
      case VMWrite.Atomic(lane, destination, address, width, false, change) =>
        memory.atomicModifyValue(GuestAddress(address), width.toScalarByteWidth)(update(_, width, change))
          .map(previous => destination.foreach(frame(lane).setRegister(_, Bits.truncate(previous, width))))
    }

  /** Validates every memory destination before applying anything, so a faulting instruction has no effect. */
  def commit(frame: VMLanes, workgroup: VMWorkgroup, memory: AddressSpace, pc: Pc): Either[Fault, Unit] = {
    val count = pending
    pending = 0
    var index = 0
    while (index < count) {
      val result =
        if (kinds(index) == ObjectWrite) validate(memory, writes(index).nn, pc)
        else if (kinds(index) == MemoryWrite)
          validate(memory, addresses(index), widths(index).toScalarByteWidth).left.map(located(_, pc, lanes(index)))
        else Right(())
      result match {
        case Left(fault) =>
          clear(count)
          return Left(fault)
        case Right(()) => ()
      }
      index += 1
    }
    // System-scope stores publish together before any other effect of the instruction.
    val systemWritten =
      if (!system) Right(())
      else memory.writeSystemBatch(Vec.from((0 until count).flatMap { position =>
        if (kinds(position) != ObjectWrite) None
        else writes(position).nn match {
          case VMWrite.Memory(_, address, width, value, true) => Some((address, value, width.toScalarByteWidth))
          case _                                              => None
        }
      }))
    index = 0
    var result = systemWritten
    while (index < count && result.isRight) {
      result =
        if (kinds(index) == RegisterWrite) Right(frame(lanes(index)).setRegister(registers(index), values(index)))
        else if (kinds(index) == MemoryWrite)
          memory.writeValue(GuestAddress(addresses(index)), values(index), widths(index).toScalarByteWidth)
        else apply(frame, workgroup, memory, writes(index).nn)
      index += 1
    }
    clear(count)
    result
  }

  private def clear(count: Int): Unit = {
    var index = 0
    while (index < count) {
      writes(index) = null
      index += 1
    }
    pending = 0
    system = false
  }
}
