package net.kurobako.gemstone.isa.nvidia

import VMExecution.*
import VMIR.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*

private object VMWriteBuffer {
  private final val ObjectWrite                                 = 0
  private final val RegisterWrite                               = 1
  private final val MemoryWrite                                 = 2
  private final val SharedWrite                                 = 3
  private final val LocalWrite                                  = 4
  private final val ParameterWrite                              = 5
  def located(fault: Fault, pc: Pc, lane: WorkgroupLane): Fault = fault match {
    case Fault.Memory(detail) => Fault.Memory(s"pc $pc, lane $lane: $detail")
    case other                => other
  }
}

/** Effects of one instruction, staged so that every lane reads its operands before any destination changes. Register
  * and ordinary memory writes use parallel arrays to avoid allocating a [[VMWrite]] per lane.
  */
@SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var", "scalafix:DisableSyntax.return"))
private class VMWriteBuffer(capacity: Int > 0 = 64) {
  import VMWriteBuffer.*

  private var kinds      = Array.ofDim[Byte](capacity)
  private var writes     = Array.ofDim[VMWrite | Null](capacity)
  private var lanes      = Array.ofDim[WorkgroupLane](capacity)
  private var registers  = Array.ofDim[Register](capacity)
  private var parameters = Array.ofDim[Parameter](capacity)
  private var offsets    = Array.ofDim[Int >= 0](capacity)
  private var values     = Array.ofDim[Long](capacity)
  private var addresses  = Array.ofDim[Long](capacity)
  private var widths     = Array.ofDim[ScalarBitWidth](capacity)
  private var pending    = 0
  private var system     = false
  // Object writes staged since the last clear; only they hold references that clearing must drop.
  private var objects = 0

  def stage(write: VMWrite): Unit = {
    write match {
      case VMWrite.Memory(system = true) => system = true
      case _                             => ()
    }
    ensureCapacity()
    kinds(pending) = ObjectWrite
    writes(pending) = write
    objects += 1
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
      (lanes(previous) >>> 5) ==
      (lane >>> 5) &&
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

  def stageShared(address: Long, width: ScalarBitWidth, value: Long): Unit = {
    ensureCapacity()
    kinds(pending) = SharedWrite
    addresses(pending) = address
    widths(pending) = width
    values(pending) = value
    pending += 1
  }

  def stageLocal(lane: WorkgroupLane, address: Long, width: ScalarBitWidth, value: Long): Unit = {
    ensureCapacity()
    kinds(pending) = LocalWrite
    lanes(pending) = lane
    addresses(pending) = address
    widths(pending) = width
    values(pending) = value
    pending += 1
  }

  def stageParameter(lane: WorkgroupLane, parameter: Parameter, offset: Int >= 0, width: ScalarBitWidth, value: Long)
      : Unit = {
    ensureCapacity()
    kinds(pending) = ParameterWrite
    lanes(pending) = lane
    parameters(pending) = parameter
    offsets(pending) = offset
    widths(pending) = width
    values(pending) = value
    pending += 1
  }

  def clear(): Unit = clear(pending)

  private def ensureCapacity(): Unit =
    if (pending == kinds.length) {
      val capacity = Math.multiplyExact(kinds.length, 2)
      kinds = java.util.Arrays.copyOf(kinds, capacity)
      writes = java.util.Arrays.copyOf(writes, capacity)
      lanes = Array.copyOf(lanes, capacity)
      registers = Array.copyOf(registers, capacity)
      parameters = Array.copyOf(parameters, capacity)
      offsets = Array.copyOf(offsets, capacity)
      values = java.util.Arrays.copyOf(values, capacity)
      addresses = java.util.Arrays.copyOf(addresses, capacity)
      widths = Array.copyOf(widths, capacity)
    }

  private def validate(memory: DeviceHeap, address: Long, size: Int >= 0): Either[Fault, Unit] =
    memory.locate(GuestAddress(address), size) match {
      case Left(fault) => Left(fault)
      case _           => Completed
    }

  private def validate(memory: DeviceHeap, write: VMWrite, pc: Pc): Either[Fault, Unit] = write match {
    case VMWrite.Memory(lane, address, width, _, _) =>
      validate(memory, address.raw, width.toScalarByteWidth).left.map(located(_, pc, lane))
    case VMWrite.MemoryBytes(lane, address, bytes) =>
      validate(memory, address.raw, bytes.nonNegativeSize).left.map(located(_, pc, lane))
    case VMWrite.Atomic(lane, _, address, width, false, _) =>
      validate(memory, address, width.toScalarByteWidth).left.map(located(_, pc, lane))
    case _ => Completed
  }

  private def apply(frame: VMLanes, workgroup: VMWorkgroup, memory: DeviceHeap, write: VMWrite): Either[Fault, Unit] =
    write match {
      case VMWrite.Memory(_, address, width, value, false) => memory.writeValue(address, value, width.toScalarByteWidth)
      case VMWrite.MemoryBytes(_, address, bytes)          => memory.write(address, bytes)
      case VMWrite.Atomic(lane, destination, address, width, false, change) =>
        memory.atomicModifyValue(GuestAddress(address), width.toScalarByteWidth)(change(_)) match {
          case Left(fault)     => Left(fault)
          case Right(previous) =>
            destination.foreach(frame(lane).setRegister(_, Bits.truncate(previous, width)))
            Completed
        }
      case _ =>
        applyOnChip(frame, workgroup, write)
        Completed
    }

  private def applyOnChip(frame: VMLanes, workgroup: VMWorkgroup, write: VMWrite): Unit = write match {
    case VMWrite.Predicate(lane, predicate, value)      => frame(lane).setPredicate(predicate, value)
    case VMWrite.ConditionFlag(lane, flag, value)       => frame(lane).conditionCodes.write(flag, value)
    case VMWrite.AddThreeCarry(lane, value)             => frame(lane).conditionCodes.writeThreeInputCarry(value)
    case VMWrite.ParameterBuffer(address, width, value) =>
      workgroup.withBuffer(address, width.toScalarByteWidth.widen)((bytes, offset) =>
        LittleEndian.write(value, bytes, offset, width.toScalarByteWidth)
      )(())
    case VMWrite.Atomic(lane, destination, address, width, true, change) =>
      val previous = Bits.truncate(workgroup.readShared(address, width), width)
      workgroup.writeShared(address, width, change(previous))
      destination.foreach(frame(lane).setRegister(_, previous))
    case VMWrite.Memory(_, _, _, _, _) | VMWrite.MemoryBytes(_, _, _) | VMWrite.Atomic(shared = false) => ()
  }

  /** Validates every memory destination before applying anything, so a faulting instruction has no effect. */
  def commit(frame: VMLanes, workgroup: VMWorkgroup, memory: DeviceHeap, pc: Pc): Either[Fault, Unit] = {
    val count = pending
    pending = 0
    var index = 0
    while (index < count) {
      val result =
        if (kinds(index) == ObjectWrite) validate(memory, writes(index).nn, pc)
        else if (kinds(index) == MemoryWrite)
          validate(memory, addresses(index), widths(index).toScalarByteWidth) match {
            case Left(fault) => Left(located(fault, pc, lanes(index)))
            case valid       => valid
          }
        else Completed
      if (result.isLeft) {
        clear(count)
        return result
      }
      index += 1
    }
    // System-scope stores publish together before any other effect of the instruction.
    val systemWritten =
      if (!system) Completed
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
        if (kinds(index) == RegisterWrite) {
          frame(lanes(index)).setRegister(registers(index), values(index))
          Completed
        } else if (kinds(index) == MemoryWrite)
          memory.writeValue(GuestAddress(addresses(index)), values(index), widths(index).toScalarByteWidth)
        else if (kinds(index) == ObjectWrite) apply(frame, workgroup, memory, writes(index).nn)
        else {
          if (kinds(index) == SharedWrite) workgroup.writeShared(addresses(index), widths(index), values(index))
          else if (kinds(index) == LocalWrite)
            workgroup.writeLocal(lanes(index), addresses(index), widths(index), values(index))
          else frame(lanes(index)).writeParameter(parameters(index), offsets(index), widths(index), values(index))
          Completed
        }
      index += 1
    }
    clear(count)
    result
  }

  private def clear(count: Int): Unit = {
    if (objects > 0) {
      var index = 0
      while (index < count) {
        writes(index) = null
        index += 1
      }
      objects = 0
    }
    pending = 0
    system = false
  }

}
