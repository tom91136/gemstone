package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.binary.Bits.byteCount
import net.kurobako.gemstone.binary.Bits.truncate
import net.kurobako.gemstone.binary.IEEE754
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.execution.*
import net.kurobako.gemstone.memory.AddressMemory

import cats.syntax.all.*

/** The write contract shared by block execution and device-function calls. */
private object StateEffects {
  def validateWrite(memory: AddressMemory, pc: Pc)(write: StateWrite): Either[ExecutionFault, Unit] =
    write match {
      case StateWrite.MemoryVector(lane, address, width, values) =>
        memory.validateRange(address, byteCount(width) * values.size).leftMap(error =>
          ExecutionFault.Memory(s"pc ${pc.value}, lane $lane: ${error.message}")
        )
      case StateWrite.Memory(lane, address, width, _) =>
        val result = memory.validateRange(address, byteCount(width)).leftMap(error =>
          ExecutionFault.Memory(s"pc ${pc.value}, lane $lane: ${error.message}")
        )
        result
      case StateWrite.AtomicMemoryAdd(lane, _, address, width, _) =>
        memory.validateRange(address, byteCount(width)).leftMap(error =>
          ExecutionFault.Memory(s"pc ${pc.value}, lane $lane: ${error.message}")
        )
      case StateWrite.AtomicMemoryMin(lane, _, address, width, _) =>
        memory.validateRange(address, byteCount(width)).leftMap(error =>
          ExecutionFault.Memory(s"pc ${pc.value}, lane $lane: ${error.message}")
        )
      case StateWrite.AtomicMemoryAddFloat(lane, _, address, width, _) =>
        memory.validateRange(address, byteCount(width)).leftMap(error =>
          ExecutionFault.Memory(s"pc ${pc.value}, lane $lane: ${error.message}")
        )
      case _ => Right(())
    }

  def applyWrite(state: BlockState, memory: AddressMemory)(write: StateWrite): Unit = write match {
    case StateWrite.Register(lane, name, value)                      => state.registers(lane)(name) = value
    case StateWrite.Carry(lane, value)                               => state.carry(lane) = value
    case StateWrite.FunctionRegister(lane, name, value)              => state.functionRegisters(lane)(name) = value
    case StateWrite.Predicate(lane, name, value)                     => state.predicates(lane)(name) = value
    case StateWrite.Parameter(lane, name, value)                     => state.parameters(lane)(name) = value
    case StateWrite.ParameterBytes(lane, name, offset, width, value) =>
      state.writeParameterBytes(lane, name, offset, width, value)
    case StateWrite.FunctionParameter(lane, name, value)   => state.functionParameters(lane)(name) = value
    case StateWrite.ParameterBuffer(address, width, value) =>
      state.parameterBufferRange(address, byteCount(width)).foreach { case (bytes, offset) =>
        LittleEndian.write(value, bytes, offset, byteCount(width))
      }
    case StateWrite.Shared(_, address, width, value)        => state.writeShared(address, width, value)
    case StateWrite.Local(lane, address, width, value)      => state.writeLocal(lane, address, width, value)
    case StateWrite.MemoryVector(_, address, width, values) =>
      memory.writeBytes(address, values.flatMap(value => LittleEndian.toCol(value, byteCount(width))))
        .left.foreach(error => throw IllegalStateException(error.message))
    case StateWrite.Memory(_, address, width, value) =>
      memory.writeLittleEndian(address, value, byteCount(width)).left.foreach(error =>
        throw IllegalStateException(error.message)
      )
    case StateWrite.AtomicSharedAdd(lane, destination, address, width, value) =>
      val previous = state.readShared(address, width)
      state.registers(lane)(destination) = truncate(previous, width)
      state.writeShared(address, width, previous + value)
    case StateWrite.AtomicSharedMin(lane, destination, address, width, value) =>
      val previous = state.readShared(address, width)
      state.registers(lane)(destination) = truncate(previous, width)
      val mask   = if (width >= 64) -1L else (1L << width) - 1L
      val result =
        if (java.lang.Long.compareUnsigned(previous & mask, value & mask) <= 0) previous & mask else value & mask
      state.writeShared(address, width, result)
    case StateWrite.AtomicMemoryAdd(lane, destination, address, width, value) =>
      val previous = memory.readLittleEndian(address, byteCount(width)).fold(
        error =>
          throw IllegalStateException(error.message),
        identity
      )
      state.registers(lane)(destination) = truncate(previous, width)
      memory.writeLittleEndian(address, previous + value, byteCount(width)).left.foreach(error =>
        throw IllegalStateException(error.message)
      )
    case StateWrite.AtomicMemoryMin(lane, destination, address, width, value) =>
      val previous = memory.readLittleEndian(address, byteCount(width)).fold(
        error => throw IllegalStateException(error.message),
        identity
      )
      state.registers(lane)(destination) = truncate(previous, width)
      val mask   = if (width >= 64) -1L else (1L << width) - 1L
      val result =
        if (java.lang.Long.compareUnsigned(previous & mask, value & mask) <= 0) previous & mask else value & mask
      memory.writeLittleEndian(address, result, byteCount(width)).left.foreach(error =>
        throw IllegalStateException(error.message)
      )
    case StateWrite.AtomicMemoryAddFloat(lane, destination, address, width, value) =>
      val previous = memory.readLittleEndian(address, byteCount(width)).fold(
        error => throw IllegalStateException(error.message),
        identity
      )
      val result = if (width == 32)
        IEEE754.floatToBinary32(IEEE754.binary32ToFloat(previous) + IEEE754.binary32ToFloat(value))
      else IEEE754.doubleToBinary64(IEEE754.binary64ToDouble(previous) + IEEE754.binary64ToDouble(value))
      state.registers(lane)(destination) = truncate(previous, width)
      memory.writeLittleEndian(address, result, byteCount(width)).left.foreach(error =>
        throw IllegalStateException(error.message)
      )
  }

}
