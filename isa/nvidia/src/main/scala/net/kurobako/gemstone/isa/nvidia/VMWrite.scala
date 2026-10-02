package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*

enum VMWrite {
  case Register(lane: Int, name: NonEmpty[String], value: Long)
  case Carry(lane: Int, value: Boolean)
  case ConditionFlag(lane: Int, flag: VMConditionCodes.Flag, value: Option[Boolean])
  case AddThreeCarry(lane: Int, value: Option[Int])
  case FunctionRegister(lane: Int, name: NonEmpty[String], value: NonEmpty[String])
  case Predicate(lane: Int, name: NonEmpty[String], value: Boolean)
  case Parameter(lane: Int, name: NonEmpty[String], value: Long)
  case ParameterBytes(
      lane: Int,
      name: NonEmpty[String],
      offset: Int,
      width: Int where (Between[8, 64] & MultipleOf[8]),
      value: Long
  )
  case FunctionParameter(lane: Int, name: NonEmpty[String], value: NonEmpty[String])
  case ParameterBuffer(address: Long, width: Int where (Between[8, 64] & MultipleOf[8]), value: Long)
  case Shared(lane: Int, address: Long, width: Int where (Between[8, 64] & MultipleOf[8]), value: Long)
  case Local(lane: Int, address: Long, width: Int where (Between[8, 64] & MultipleOf[8]), value: Long)
  case Memory(lane: Int, address: GuestAddress, width: Int where (Between[8, 64] & MultipleOf[8]), value: Long)
  case SystemMemory(
      lane: Int,
      address: GuestAddress,
      width: Int where (Between[8, 64] & MultipleOf[8]),
      value: Long
  )
  case MemoryVector(
      lane: Int,
      address: GuestAddress,
      width: Int where (Between[8, 64] & MultipleOf[8]),
      values: Vec[Long]
  )
  case AtomicSharedAdd(
      lane: Int,
      destination: NonEmpty[String],
      address: Long,
      width: Int where (Between[8, 64] & MultipleOf[8]),
      value: Long
  )
  case AtomicSharedCompareExchange(
      lane: Int,
      destination: NonEmpty[String],
      address: Long,
      width: Int where (Between[8, 64] & MultipleOf[8]),
      comparator: Long,
      value: Long
  )
  case AtomicMemoryCompareExchange(
      lane: Int,
      destination: NonEmpty[String],
      address: GuestAddress,
      width: Int where (Between[8, 64] & MultipleOf[8]),
      comparator: Long,
      value: Long
  )
  case AtomicSharedExchange(
      lane: Int,
      destination: NonEmpty[String],
      address: Long,
      width: Int where (Between[8, 64] & MultipleOf[8]),
      value: Long
  )
  case AtomicSharedBinary(
      lane: Int,
      destination: NonEmpty[String],
      address: Long,
      width: Int where (Between[8, 64] & MultipleOf[8]),
      value: Long,
      operation: IntegerOperation
  )
  case AtomicMemoryAdd(
      lane: Int,
      destination: NonEmpty[String],
      address: GuestAddress,
      width: Int where (Between[8, 64] & MultipleOf[8]),
      value: Long
  )
  case AtomicMemoryExchange(
      lane: Int,
      destination: NonEmpty[String],
      address: GuestAddress,
      width: Int where (Between[8, 64] & MultipleOf[8]),
      value: Long
  )
  case AtomicMemoryBinary(
      lane: Int,
      destination: NonEmpty[String],
      address: GuestAddress,
      width: Int where (Between[8, 64] & MultipleOf[8]),
      value: Long,
      operation: IntegerOperation
  )
  case AtomicMemoryAddFloat(
      lane: Int,
      destination: NonEmpty[String],
      address: GuestAddress,
      width: Int in (16, 32, 64),
      value: Long
  )
}
