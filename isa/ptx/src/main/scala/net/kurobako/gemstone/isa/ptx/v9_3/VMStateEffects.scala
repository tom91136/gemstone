package net.kurobako.gemstone.isa.ptx.v9_3

import Program.*

import VMIR.*
import VMExecution.*

import net.kurobako.gemstone.binary.Binary32
import net.kurobako.gemstone.binary.Bits
import net.kurobako.gemstone.binary.IEEE754
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.memory.AddressMemory
import net.kurobako.gemstone.memory.MemoryValueByteWidth

import cats.syntax.all.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import scala.collection.mutable

private[v9_3] final class VMStateEffects {
  private val pending = mutable.ArrayBuffer.empty[VMStateWrite]

  def stage(write: VMStateWrite): Unit = pending += write
  def isEmpty: Boolean                 = pending.isEmpty
  def size: Int                        = pending.size
  def clear(): Unit                    = pending.clear()

  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.return"))
  def commit(
      validate: VMStateWrite => Either[ExecutionFault, Unit],
      applyWrite: VMStateWrite => Unit
  ): Either[ExecutionFault, Unit] = {
    var index = 0
    while (index < pending.size) {
      validate(pending(index)) match {
        case Left(fault) =>
          pending.clear()
          return Left(fault)
        case Right(()) => ()
      }
      index += 1
    }
    index = 0
    while (index < pending.size) {
      applyWrite(pending(index))
      index += 1
    }
    pending.clear()
    Right(())
  }
}

/** The write semantics shared by block execution and device-function calls. */
private object VMStateEffects {

  private inline def byteCount(width: BitWidth) = Bits.byteCount(width.asByteAlignedBitWidth)

  private inline def floatByteCount(width: FloatWidth) = Bits.byteCount(width.asByteAlignedBitWidth)

  @SuppressWarnings(Array("scalafix:GemstoneNoAssume"))
  private inline def memoryByteCount(width: BitWidth): MemoryValueByteWidth =
    MemoryValueByteWidth.assume(byteCount(width).value)

  @SuppressWarnings(Array("scalafix:GemstoneNoAssume"))
  private inline def floatMemoryByteCount(width: FloatWidth): MemoryValueByteWidth =
    MemoryValueByteWidth.assume(floatByteCount(width).value)

  def validateWrite(memory: AddressMemory, pc: Pc)(write: VMStateWrite): Either[ExecutionFault, Unit] =
    write match {
      case VMStateWrite.MemoryVector(lane, address, width, values) =>
        val size = Math.multiplyExact(byteCount(width).value, values.size).refineUnsafe[Positive0]
        memory.validateRange(address, size).leftMap(error =>
          ExecutionFault.Memory(s"pc ${pc.value}, lane $lane: ${error.message}")
        )
      case VMStateWrite.Memory(lane, address, width, _) =>
        val result = memory.validateRange(address, byteCount(width).value.refineUnsafe[Positive0]).leftMap(error =>
          ExecutionFault.Memory(s"pc ${pc.value}, lane $lane: ${error.message}")
        )
        result
      case VMStateWrite.AtomicMemoryAdd(lane, _, address, width, _) =>
        memory.validateRange(address, byteCount(width).value.refineUnsafe[Positive0]).leftMap(error =>
          ExecutionFault.Memory(s"pc ${pc.value}, lane $lane: ${error.message}")
        )
      case VMStateWrite.AtomicMemoryMin(lane, _, address, width, _) =>
        memory.validateRange(address, byteCount(width).value.refineUnsafe[Positive0]).leftMap(error =>
          ExecutionFault.Memory(s"pc ${pc.value}, lane $lane: ${error.message}")
        )
      case VMStateWrite.AtomicMemoryAddFloat(lane, _, address, width, _) =>
        memory.validateRange(address, floatByteCount(width).value.refineUnsafe[Positive0]).leftMap(error =>
          ExecutionFault.Memory(s"pc ${pc.value}, lane $lane: ${error.message}")
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
      state.parameterBufferRange(address, byteCount(width).value).foreach { case (bytes, offset) =>
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
    case VMStateWrite.AtomicSharedMin(lane, destination, address, width, value) =>
      val previous = state.readShared(address, width)
      state.registers(lane)(destination) = Bits.truncate(previous, width.asBitWidth)
      val mask   = if (width.value >= 64) -1L else (1L << width.value) - 1L
      val result =
        if (java.lang.Long.compareUnsigned(previous & mask, value & mask) <= 0) previous & mask else value & mask
      state.writeShared(address, width, result)
    case VMStateWrite.AtomicMemoryAdd(lane, destination, address, width, value) =>
      val previous = memory.atomicModifyLittleEndian(address, memoryByteCount(width))(_ + value).fold(
        error => throw IllegalStateException(error.message),
        value => value
      )
      state.registers(lane)(destination) = Bits.truncate(previous, width.asBitWidth)
    case VMStateWrite.AtomicMemoryMin(lane, destination, address, width, value) =>
      val mask     = if (width.value >= 64) -1L else (1L << width.value) - 1L
      val previous = memory.atomicModifyLittleEndian(address, memoryByteCount(width)) { current =>
        if (java.lang.Long.compareUnsigned(current & mask, value & mask) <= 0) current & mask else value & mask
      }.fold(error => throw IllegalStateException(error.message), value => value)
      state.registers(lane)(destination) = Bits.truncate(previous, width.asBitWidth)
    case VMStateWrite.AtomicMemoryAddFloat(lane, destination, address, width, value) =>
      val previous = memory.atomicModifyLittleEndian(address, floatMemoryByteCount(width)) { current =>
        if (width.value == 32)
          IEEE754.floatToBinary32(
            IEEE754.binary32ToFloat(Binary32.low(current)) +
              IEEE754.binary32ToFloat(Binary32.low(value))
          ).value
        else IEEE754.doubleToBinary64(IEEE754.binary64ToDouble(current) + IEEE754.binary64ToDouble(value))
      }.fold(error => throw IllegalStateException(error.message), value => value)
      state.registers(lane)(destination) = Bits.truncate(previous, width.asBitWidth)
  }

}
