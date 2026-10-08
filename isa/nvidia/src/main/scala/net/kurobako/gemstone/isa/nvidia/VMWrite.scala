package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*

/** An effect staged by one instruction and applied once every lane has read its operands. */
enum VMWrite {
  case Predicate(lane: WorkgroupLane, predicate: VMIR.Predicate, value: Boolean)
  case ConditionFlag(lane: WorkgroupLane, flag: VMConditionCodes.Flag, value: Option[Boolean])
  case AddThreeCarry(lane: WorkgroupLane, value: Option[Int in 0 ~ 2])
  case ParameterBuffer(address: Long, width: ScalarBitWidth, value: Long)
  case Memory(lane: WorkgroupLane, address: GuestAddress, width: ScalarBitWidth, value: Long, system: Boolean)
  case MemoryBytes(lane: WorkgroupLane, address: GuestAddress, bytes: Vec[Byte])
  case Atomic(
      lane: WorkgroupLane,
      destination: Option[VMIR.Register],
      address: Long,
      width: ScalarBitWidth,
      shared: Boolean,
      update: AtomicUpdate
  )
}
