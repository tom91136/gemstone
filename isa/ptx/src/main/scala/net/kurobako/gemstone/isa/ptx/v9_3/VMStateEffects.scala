package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.Constraint.IntBetween

import net.kurobako.gemstone.Constraint.NonNegative

import net.kurobako.gemstone.Constraint.NonEmptyString

import Program.*

import VMIR.*
import VMExecution.*

import net.kurobako.gemstone.binary.Binary32
import net.kurobako.gemstone.binary.Bits
import net.kurobako.gemstone.binary.IEEE754
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.memory.AddressMemory
import net.kurobako.gemstone.memory.GuestAddress
import cats.syntax.all.*

@SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
private[v9_3] final class VMStateEffects {
  private val ObjectWrite   = 0.toByte
  private val RegisterWrite = 1.toByte
  private val MemoryWrite   = 2.toByte
  private var kinds         = Array.ofDim[Byte](64)
  private var writes        = Array.ofDim[VMStateWrite | Null](64)
  private var lanes         = Array.ofDim[Int](64)
  private var registerNames = Array.ofDim[NonEmptyString](64)
  private var values        = Array.ofDim[Long](64)
  private var addresses     = Array.ofDim[Long](64)
  private var widths        = Array.ofDim[BitWidth](64)
  private var pendingSize   = 0

  def stage(write: VMStateWrite): Unit = {
    ensureCapacity()
    kinds(pendingSize) = ObjectWrite
    writes(pendingSize) = write
    pendingSize += 1
  }

  def stageRegister(lane: Int, name: NonEmptyString, value: Long): Unit = {
    ensureCapacity()
    kinds(pendingSize) = RegisterWrite
    writes(pendingSize) = null
    lanes(pendingSize) = lane
    registerNames(pendingSize) = name
    values(pendingSize) = value
    pendingSize += 1
  }

  def stageMemory(lane: Int, address: GuestAddress, width: BitWidth, value: Long): Unit = {
    ensureCapacity()
    kinds(pendingSize) = MemoryWrite
    writes(pendingSize) = null
    lanes(pendingSize) = lane
    addresses(pendingSize) = address.raw
    widths(pendingSize) = width
    values(pendingSize) = value
    pendingSize += 1
  }

  def isEmpty: Boolean = pendingSize == 0
  def size: Int        = pendingSize

  def clear(): Unit = {
    var index = 0
    while (index < pendingSize) {
      writes(index) = null
      index += 1
    }
    pendingSize = 0
  }

  private def ensureCapacity(): Unit =
    if (pendingSize == kinds.length) {
      val capacity = Math.multiplyExact(kinds.length, 2)
      kinds = java.util.Arrays.copyOf(kinds, capacity)
      writes = java.util.Arrays.copyOf(writes, capacity)
      lanes = java.util.Arrays.copyOf(lanes, capacity)
      val nextRegisterNames = Array.ofDim[NonEmptyString](capacity)
      Array.copy(registerNames, 0, nextRegisterNames, 0, registerNames.length)
      registerNames = nextRegisterNames
      values = java.util.Arrays.copyOf(values, capacity)
      addresses = java.util.Arrays.copyOf(addresses, capacity)
      val nextWidths = Array.ofDim[BitWidth](capacity)
      Array.copy(widths, 0, nextWidths, 0, widths.length)
      widths = nextWidths
    }

  @SuppressWarnings(Array("scalafix:DisableSyntax.return"))
  def commit(state: VMState, memory: AddressMemory, pc: Pc): Either[ExecutionFault, Unit] = {
    var index = 0
    while (index < pendingSize) {
      if (kinds(index) == ObjectWrite)
        VMStateEffects.validateWrite(memory, pc)(writes(index).nn) match {
          case Left(fault) =>
            clear()
            return Left(fault)
          case Right(()) => ()
        }
      else if (kinds(index) == MemoryWrite)
        memory.validateRange(
          GuestAddress(addresses(index)),
          NonNegative.unsafe(VMStateEffects.byteCount(widths(index)).raw)
        ) match {
          case Left(error) =>
            clear()
            return Left(ExecutionFault.Memory(s"pc ${pc.raw}, lane ${lanes(index)}: ${error.message}"))
          case Right(()) => ()
        }
      index += 1
    }
    index = 0
    while (index < pendingSize) {
      if (kinds(index) == RegisterWrite)
        state.registers(lanes(index))(registerNames(index)) = values(index)
      else if (kinds(index) == MemoryWrite)
        memory.writeLittleEndian(
          GuestAddress(addresses(index)),
          values(index),
          VMStateEffects.memoryByteCount(widths(index))
        ).left.foreach(error => throw IllegalStateException(error.message))
      else {
        VMStateEffects.applyWrite(state, memory)(writes(index).nn)
        writes(index) = null
      }
      index += 1
    }
    pendingSize = 0
    Right(())
  }
}

/** The write semantics shared by block execution and device-function calls. */
private object VMStateEffects {

  private inline def byteCount(width: BitWidth) = Bits.byteCount(width.asByteAlignedBitWidth)

  private inline def floatByteCount(width: FloatWidth) = Bits.byteCount(width.asByteAlignedBitWidth)

  @SuppressWarnings(Array("scalafix:GemstoneNoAssume"))
  private inline def memoryByteCount(width: BitWidth): IntBetween[1, 8] =
    IntBetween.unsafe[1, 8](byteCount(width).raw)

  @SuppressWarnings(Array("scalafix:GemstoneNoAssume"))
  private inline def floatMemoryByteCount(width: FloatWidth): IntBetween[1, 8] =
    IntBetween.unsafe[1, 8](floatByteCount(width).raw)

  def validateWrite(memory: AddressMemory, pc: Pc)(write: VMStateWrite): Either[ExecutionFault, Unit] =
    write match {
      case VMStateWrite.MemoryVector(lane, address, width, values) =>
        val size = NonNegative.unsafe(Math.multiplyExact(byteCount(width).raw, values.size))
        memory.validateRange(address, size).leftMap(error =>
          ExecutionFault.Memory(s"pc ${pc.raw}, lane $lane: ${error.message}")
        )
      case VMStateWrite.Memory(lane, address, width, _) =>
        val result = memory.validateRange(address, NonNegative.unsafe(byteCount(width).raw)).leftMap(error =>
          ExecutionFault.Memory(s"pc ${pc.raw}, lane $lane: ${error.message}")
        )
        result
      case VMStateWrite.AtomicMemoryAdd(lane, _, address, width, _) =>
        memory.validateRange(address, NonNegative.unsafe(byteCount(width).raw)).leftMap(error =>
          ExecutionFault.Memory(s"pc ${pc.raw}, lane $lane: ${error.message}")
        )
      case VMStateWrite.AtomicMemoryCompareExchange(lane, _, address, width, _, _) =>
        memory.validateRange(address, NonNegative.unsafe(byteCount(width).raw)).leftMap(error =>
          ExecutionFault.Memory(s"pc ${pc.raw}, lane $lane: ${error.message}")
        )
      case VMStateWrite.AtomicMemoryExchange(lane, _, address, width, _) =>
        memory.validateRange(address, NonNegative.unsafe(byteCount(width).raw)).leftMap(error =>
          ExecutionFault.Memory(s"pc ${pc.raw}, lane $lane: ${error.message}")
        )
      case VMStateWrite.AtomicMemoryBinary(lane, _, address, width, _, _) =>
        memory.validateRange(address, NonNegative.unsafe(byteCount(width).raw)).leftMap(error =>
          ExecutionFault.Memory(s"pc ${pc.raw}, lane $lane: ${error.message}")
        )
      case VMStateWrite.AtomicMemoryMin(lane, _, address, width, _) =>
        memory.validateRange(address, NonNegative.unsafe(byteCount(width).raw)).leftMap(error =>
          ExecutionFault.Memory(s"pc ${pc.raw}, lane $lane: ${error.message}")
        )
      case VMStateWrite.AtomicMemoryAddFloat(lane, _, address, width, _) =>
        memory.validateRange(address, NonNegative.unsafe(floatByteCount(width).raw)).leftMap(error =>
          ExecutionFault.Memory(s"pc ${pc.raw}, lane $lane: ${error.message}")
        )
      case _ => Right(())
    }

  def applyWrite(state: VMState, memory: AddressMemory)(write: VMStateWrite): Unit = write match {
    case VMStateWrite.Register(lane, name, value)                      => state.registers(lane)(name) = value
    case VMStateWrite.Carry(lane, value)                               => state.carry(lane) = value
    case VMStateWrite.FunctionRegister(lane, name, value)              => state.functionRegisters(lane)(name) = value
    case VMStateWrite.Predicate(lane, name, value)                     => state.predicates(lane)(name) = value
    case VMStateWrite.Parameter(lane, name, value)                     => state.parameters(lane)(name) = value
    case VMStateWrite.ParameterBytes(lane, name, offset, width, value) =>
      state.writeParameterBytes(lane, name, offset, width, value)
    case VMStateWrite.FunctionParameter(lane, name, value)   => state.functionParameters(lane)(name) = value
    case VMStateWrite.ParameterBuffer(address, width, value) =>
      state.parameterBufferRange(address, byteCount(width).raw).foreach { case (bytes, offset) =>
        LittleEndian.write(value, bytes, offset, byteCount(width))
      }
    case VMStateWrite.Shared(_, address, width, value)        => state.writeShared(address, width, value)
    case VMStateWrite.Local(lane, address, width, value)      => state.writeLocal(lane, address, width, value)
    case VMStateWrite.MemoryVector(_, address, width, values) =>
      memory.writeBytes(address, values.flatMap(value => LittleEndian.toVec(value, byteCount(width))))
        .left.foreach(error => throw IllegalStateException(error.message))
    case VMStateWrite.Memory(_, address, width, value) =>
      memory.writeLittleEndian(address, value, memoryByteCount(width)).left.foreach(error =>
        throw IllegalStateException(error.message)
      )
    case VMStateWrite.AtomicSharedAdd(lane, destination, address, width, value) =>
      val previous = state.readShared(address, width)
      state.registers(lane)(destination) = Bits.truncate(previous, width.asBitWidth)
      state.writeShared(address, width, previous + value)
    case VMStateWrite.AtomicSharedCompareExchange(lane, destination, address, width, comparator, value) =>
      val previous = Bits.truncate(state.readShared(address, width), width.asBitWidth)
      state.registers(lane)(destination) = previous
      if (previous == Bits.truncate(comparator, width.asBitWidth)) state.writeShared(address, width, value)
    case VMStateWrite.AtomicSharedExchange(lane, destination, address, width, value) =>
      val previous = state.readShared(address, width)
      state.registers(lane)(destination) = Bits.truncate(previous, width.asBitWidth)
      state.writeShared(address, width, value)
    case VMStateWrite.AtomicSharedBinary(lane, destination, address, width, value, operation) =>
      val previous = state.readShared(address, width)
      state.registers(lane)(destination) = Bits.truncate(previous, width.asBitWidth)
      state.writeShared(address, width, atomicBinaryResult(operation, previous, value, width))
    case VMStateWrite.AtomicSharedMin(lane, destination, address, width, value) =>
      val previous = state.readShared(address, width)
      state.registers(lane)(destination) = Bits.truncate(previous, width.asBitWidth)
      val mask   = if (width.raw >= 64) -1L else (1L << width.raw) - 1L
      val result =
        if (java.lang.Long.compareUnsigned(previous & mask, value & mask) <= 0) previous & mask else value & mask
      state.writeShared(address, width, result)
    case VMStateWrite.AtomicMemoryAdd(lane, destination, address, width, value) =>
      val previous = memory.atomicModifyLittleEndian(address, memoryByteCount(width))(_ + value).fold(
        error => throw IllegalStateException(error.message),
        value => value
      )
      state.registers(lane)(destination) = Bits.truncate(previous, width.asBitWidth)
    case VMStateWrite.AtomicMemoryCompareExchange(lane, destination, address, width, comparator, value) =>
      val previous = memory.atomicModifyLittleEndian(address, memoryByteCount(width)) { current =>
        if (Bits.truncate(current, width.asBitWidth) == Bits.truncate(comparator, width.asBitWidth)) value else current
      }.fold(error => throw IllegalStateException(error.message), value => value)
      state.registers(lane)(destination) = Bits.truncate(previous, width.asBitWidth)
    case VMStateWrite.AtomicMemoryExchange(lane, destination, address, width, value) =>
      val previous = memory.atomicModifyLittleEndian(address, memoryByteCount(width))(_ => value).fold(
        error => throw IllegalStateException(error.message),
        value => value
      )
      state.registers(lane)(destination) = Bits.truncate(previous, width.asBitWidth)
    case VMStateWrite.AtomicMemoryBinary(lane, destination, address, width, value, operation) =>
      val previous = memory.atomicModifyLittleEndian(address, memoryByteCount(width)) { current =>
        atomicBinaryResult(operation, current, value, width)
      }.fold(error => throw IllegalStateException(error.message), value => value)
      state.registers(lane)(destination) = Bits.truncate(previous, width.asBitWidth)
    case VMStateWrite.AtomicMemoryMin(lane, destination, address, width, value) =>
      val mask     = if (width.raw >= 64) -1L else (1L << width.raw) - 1L
      val previous = memory.atomicModifyLittleEndian(address, memoryByteCount(width)) { current =>
        if (java.lang.Long.compareUnsigned(current & mask, value & mask) <= 0) current & mask else value & mask
      }.fold(error => throw IllegalStateException(error.message), value => value)
      state.registers(lane)(destination) = Bits.truncate(previous, width.asBitWidth)
    case VMStateWrite.AtomicMemoryAddFloat(lane, destination, address, width, value) =>
      val previous = memory.atomicModifyLittleEndian(address, floatMemoryByteCount(width)) { current =>
        if (width.raw == 32)
          IEEE754.floatToBinary32(
            IEEE754.binary32ToFloat(Binary32.low(current)) +
              IEEE754.binary32ToFloat(Binary32.low(value))
          ).raw
        else IEEE754.doubleToBinary64(IEEE754.binary64ToDouble(current) + IEEE754.binary64ToDouble(value))
      }.fold(error => throw IllegalStateException(error.message), value => value)
      state.registers(lane)(destination) = Bits.truncate(previous, width.asBitWidth)
  }

  private def atomicBinaryResult(operation: IntegerOperation, current: Long, value: Long, width: BitWidth): Long = {
    val left   = Bits.truncate(current, width.asBitWidth)
    val right  = Bits.truncate(value, width.asBitWidth)
    val result = operation match {
      case IntegerOperation.Maximum(false) =>
        if (java.lang.Long.compareUnsigned(left, right) >= 0) left else right
      case IntegerOperation.BitwiseAnd => left & right
      case IntegerOperation.BitwiseOr  => left | right
      case IntegerOperation.BitwiseXor => left ^ right
      case other                       => throw IllegalStateException(s"unsupported atomic operation $other")
    }
    Bits.truncate(result, width.asBitWidth)
  }

}
