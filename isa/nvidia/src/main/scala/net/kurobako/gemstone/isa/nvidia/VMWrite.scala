package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*

enum VMWrite {
  case Register(lane: WorkgroupLane, name: NonEmpty[String], value: Long)
  case Carry(lane: WorkgroupLane, value: Boolean)
  case ConditionFlag(lane: WorkgroupLane, flag: VMConditionCodes.Flag, value: Option[Boolean])
  case AddThreeCarry(lane: WorkgroupLane, value: Option[Int])
  case Predicate(lane: WorkgroupLane, name: NonEmpty[String], value: Boolean)
  case Parameter(lane: WorkgroupLane, name: NonEmpty[String], value: Long)
  case ParameterBytes(
      lane: WorkgroupLane,
      name: NonEmpty[String],
      offset: Int >= 0,
      width: Int where (Between[8, 64] & MultipleOf[8]),
      value: Long
  )
  case ParameterBuffer(address: Long, width: Int where (Between[8, 64] & MultipleOf[8]), value: Long)
  case Shared(lane: WorkgroupLane, address: Long, width: Int where (Between[8, 64] & MultipleOf[8]), value: Long)
  case Local(lane: WorkgroupLane, address: Long, width: Int where (Between[8, 64] & MultipleOf[8]), value: Long)
  case Memory(
      lane: WorkgroupLane,
      address: GuestAddress,
      width: Int where (Between[8, 64] & MultipleOf[8]),
      value: Long
  )
  case SystemMemory(
      lane: WorkgroupLane,
      address: GuestAddress,
      width: Int where (Between[8, 64] & MultipleOf[8]),
      value: Long
  )
  case MemoryVector(
      lane: WorkgroupLane,
      address: GuestAddress,
      width: Int where (Between[8, 64] & MultipleOf[8]),
      values: Vec[Long]
  )
  case AtomicSharedAdd(
      lane: WorkgroupLane,
      destination: NonEmpty[String],
      address: Long,
      width: Int where (Between[8, 64] & MultipleOf[8]),
      value: Long
  )
  case AtomicSharedCompareExchange(
      lane: WorkgroupLane,
      destination: NonEmpty[String],
      address: Long,
      width: Int where (Between[8, 64] & MultipleOf[8]),
      comparator: Long,
      value: Long
  )
  case AtomicMemoryCompareExchange(
      lane: WorkgroupLane,
      destination: NonEmpty[String],
      address: GuestAddress,
      width: Int where (Between[8, 64] & MultipleOf[8]),
      comparator: Long,
      value: Long
  )
  case AtomicSharedExchange(
      lane: WorkgroupLane,
      destination: NonEmpty[String],
      address: Long,
      width: Int where (Between[8, 64] & MultipleOf[8]),
      value: Long
  )
  case AtomicSharedBinary(
      lane: WorkgroupLane,
      destination: NonEmpty[String],
      address: Long,
      width: Int where (Between[8, 64] & MultipleOf[8]),
      value: Long,
      operation: IntegerOperation
  )
  case AtomicMemoryAdd(
      lane: WorkgroupLane,
      destination: NonEmpty[String],
      address: GuestAddress,
      width: Int where (Between[8, 64] & MultipleOf[8]),
      value: Long
  )
  case AtomicMemoryExchange(
      lane: WorkgroupLane,
      destination: NonEmpty[String],
      address: GuestAddress,
      width: Int where (Between[8, 64] & MultipleOf[8]),
      value: Long
  )
  case AtomicMemoryBinary(
      lane: WorkgroupLane,
      destination: NonEmpty[String],
      address: GuestAddress,
      width: Int where (Between[8, 64] & MultipleOf[8]),
      value: Long,
      operation: IntegerOperation
  )
  case AtomicMemoryAddFloat(
      lane: WorkgroupLane,
      destination: NonEmpty[String],
      address: GuestAddress,
      width: Int in (16, 32, 64),
      value: Long
  )
}
