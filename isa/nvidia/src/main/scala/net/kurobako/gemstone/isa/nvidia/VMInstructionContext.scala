package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class VMInstructionContext(
    val kernel: Kernel,
    val block: Int,
    val group: Coordinates,
    val geometry: LaunchGeometry,
    val state: VMBlockState,
    val pc: Pc,
    val active: VMLaneMask,
    val arguments: Map[NonEmpty[String], Argument],
    val memory: AddressMemory,
    val control: TaskControl,
    val workgroups: Workgroups
) {
  def read(lane: WorkgroupLane, value: Value): Either[VMFault, Long] = value match {
    // A register read after a divergent path is unspecified. Hardware
    // nevertheless carries a value for it, and CUDA reductions commonly use
    // such lanes only as inactive shuffle participants.  Use a deterministic
    // zero for that unspecified value so the active lane's identity is stable.
    case Value.Register(name)       => Right(state.registers(lane).get(name))
    case Value.RegisterSlot(index)  => Right(state.registers(lane).getSlot(index))
    case Value.Predicate(name)      => Right(if (state.predicates(lane).get(name).getOrElse(false)) 1L else 0L)
    case Value.PredicateSlot(index) => Right(if (state.predicates(lane).getSlot(index).getOrElse(false)) 1L else 0L)
    case Value.Function(name)       =>
      kernel.functionTable.address(name).toRight(VMFault.InvalidState(s"unknown function symbol $name"))
    case Value.Symbol(name) =>
      state.parameterAddresses.get(
        name
      ).orElse(kernel.sharedSymbols.get(name)).orElse(kernel.globalSymbols.get(name).map(_.raw)).toRight(
        VMFault.InvalidState(s"unknown shared symbol $name")
      )
    case Value.Immediate(immediate)  => Right(immediate)
    case Value.Address(base, offset) => read(lane, base).map(_ + offset)
    case Value.DynamicSharedBytes    => Right(kernel.dynamicSharedBytes.raw.toLong)
    case Value.LaneId                => Right((lane.raw & 31).toLong)
    case Value.ActiveMask            => Right(active.warpBits(lane.raw))
    case Value.LiveMask              => Right(state.live.warpBits(lane.raw))
    case Value.LaneMaskGreaterEqual  => Right((0xffffffffL << (lane.raw & 31)) & 0xffffffffL)
    case Value.LaneMaskGreater       => Right((0xffffffffL << ((lane.raw & 31) + 1)) & 0xffffffffL)
    case Value.LaneMaskLessEqual     => Right((1L << ((lane.raw & 31) + 1)) - 1L)
    case Value.LaneMaskLess          => Right((1L << (lane.raw & 31)) - 1L)
    case Value.LaneMaskEqual         => Right(1L << (lane.raw & 31))
    case Value.ThreadId(axis)        => geometry.localCoordinates(lane.raw)
        .left.map(error => VMFault.InvalidState(error))
        .map(coordinates => axis.select(coordinates).raw.toLong)
    case Value.BlockId(axis)  => Right(axis.select(group).raw.toLong)
    case Value.BlockDim(axis) => Right(axis.select(geometry.groupSize).raw.toLong)
    case Value.GridDim(axis)  => Right(axis.select(geometry.groupCount).raw.toLong)
  }

  def functionName(lane: WorkgroupLane, value: Value): Either[VMFault, NonEmpty[String]] = value match {
    case Value.Function(name) => Right(name)
    case other                => read(lane, other).flatMap(address =>
        kernel.functionTable.name(address).toRight(
          VMFault.InvalidState(s"invalid function address 0x${java.lang.Long.toUnsignedString(address, 16)}")
        )
      )
  }
}
