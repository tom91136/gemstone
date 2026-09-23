package net.kurobako.gemstone.isa.ptx.v9_3

import Program.*
import VMIR.*

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.memory.GuestAddress

private[v9_3] enum VMStateWrite {
  case Register(lane: Int, name: RegisterName, value: Long)
  case Carry(lane: Int, value: Boolean)
  case FunctionRegister(lane: Int, name: RegisterName, value: FunctionName)
  case Predicate(lane: Int, name: RegisterName, value: Boolean)
  case Parameter(lane: Int, name: ParameterName, value: Long)
  case ParameterBytes(lane: Int, name: ParameterName, offset: Int, width: BitWidth, value: Long)
  case FunctionParameter(lane: Int, name: ParameterName, value: FunctionName)
  case ParameterBuffer(address: Long, width: BitWidth, value: Long)
  case Shared(lane: Int, address: Long, width: BitWidth, value: Long)
  case Local(lane: Int, address: Long, width: BitWidth, value: Long)
  case Memory(lane: Int, address: GuestAddress, width: BitWidth, value: Long)
  case MemoryVector(lane: Int, address: GuestAddress, width: BitWidth, values: Vec[Long])
  case AtomicSharedAdd(lane: Int, destination: RegisterName, address: Long, width: BitWidth, value: Long)
  case AtomicSharedExchange(lane: Int, destination: RegisterName, address: Long, width: BitWidth, value: Long)
  case AtomicSharedBinary(
      lane: Int,
      destination: RegisterName,
      address: Long,
      width: BitWidth,
      value: Long,
      operation: IntegerOperation
  )
  case AtomicSharedMin(lane: Int, destination: RegisterName, address: Long, width: BitWidth, value: Long)
  case AtomicMemoryAdd(
      lane: Int,
      destination: RegisterName,
      address: GuestAddress,
      width: BitWidth,
      value: Long
  )
  case AtomicMemoryExchange(
      lane: Int,
      destination: RegisterName,
      address: GuestAddress,
      width: BitWidth,
      value: Long
  )
  case AtomicMemoryBinary(
      lane: Int,
      destination: RegisterName,
      address: GuestAddress,
      width: BitWidth,
      value: Long,
      operation: IntegerOperation
  )
  case AtomicMemoryMin(
      lane: Int,
      destination: RegisterName,
      address: GuestAddress,
      width: BitWidth,
      value: Long
  )
  case AtomicMemoryAddFloat(
      lane: Int,
      destination: RegisterName,
      address: GuestAddress,
      width: FloatWidth,
      value: Long
  )
}
