package net.kurobako.gemstone.isa.ptx.v9_3

import Program.*
import VMIR.*
import VMExecution.*

import net.kurobako.gemstone.memory.AddressMemory
import net.kurobako.gemstone.vm.LaunchGeometry
import net.kurobako.gemstone.vm.LaunchGeometry.Coordinates
import net.kurobako.gemstone.vm.Scheduler
import net.kurobako.gemstone.vm.TaskControl

private[v9_3] final class VMExecutionContext(
    val kernel: Kernel,
    val block: Int,
    val group: Coordinates,
    val geometry: LaunchGeometry,
    val state: VMState,
    val pc: Pc,
    val active: VMLaneMask,
    val arguments: Map[ParameterName, Argument],
    val memory: AddressMemory,
    val control: TaskControl,
    val scheduler: Scheduler
) {
  def read(lane: Int, value: Value): Either[ExecutionFault, Long] = value match {
    // A PTX register read after a divergent path is unspecified.  Hardware
    // nevertheless carries a value for it, and CUDA reductions commonly use
    // such lanes only as inactive shuffle participants.  Use a deterministic
    // zero for that unspecified value so the active lane's identity is stable.
    case Value.Register(name)       => Right(state.registers(lane).get(name))
    case Value.RegisterSlot(index)  => Right(state.registers(lane).getSlot(index))
    case Value.Predicate(name)      => Right(if (state.predicates(lane).get(name).getOrElse(false)) 1L else 0L)
    case Value.PredicateSlot(index) => Right(if (state.predicates(lane).getSlot(index).getOrElse(false)) 1L else 0L)
    case Value.Function(name)       =>
      Left(ExecutionFault.InvalidState(s"function symbol $name is not numeric"))
    case Value.Symbol(name) =>
      state.parameterAddresses.get(
        name
      ).orElse(kernel.sharedSymbols.get(name)).orElse(kernel.globalSymbols.get(name).map(_.raw)).toRight(
        ExecutionFault.InvalidState(s"unknown shared symbol $name")
      )
    case Value.Immediate(immediate)  => Right(immediate)
    case Value.Address(base, offset) => read(lane, base).map(_ + offset)
    case Value.LaneId                => Right((lane & 31).toLong)
    case Value.ActiveMask            => Right(active.warpBits(lane))
    case Value.LaneMaskGreaterEqual  => Right((0xffffffffL << (lane & 31)) & 0xffffffffL)
    case Value.LaneMaskGreater       => Right((0xffffffffL << ((lane & 31) + 1)) & 0xffffffffL)
    case Value.LaneMaskLessEqual     => Right((1L << ((lane & 31) + 1)) - 1L)
    case Value.LaneMaskLess          => Right((1L << (lane & 31)) - 1L)
    case Value.LaneMaskEqual         => Right(1L << (lane & 31))
    case Value.ThreadId(axis)        => geometry.localCoordinates(lane)
        .left.map(error => ExecutionFault.InvalidState(error))
        .map(coordinates => axis.select(coordinates).raw.toLong)
    case Value.BlockId(axis)  => Right(axis.select(group).raw.toLong)
    case Value.BlockDim(axis) => Right(axis.select(geometry.groupSize).raw.toLong)
    case Value.GridDim(axis)  => Right(axis.select(geometry.groupCount).raw.toLong)
  }

  def functionName(lane: Int, value: Value): Either[ExecutionFault, FunctionName] = value match {
    case Value.Function(name) => Right(name)
    case Value.Register(name) => state.functionRegisters(lane).get(name).toRight(
        ExecutionFault.UndefinedRegister(name, pc, lane)
      )
    case Value.RegisterSlot(index) =>
      val name = VMRegisterFile.name(index)
      state.functionRegisters(lane).get(name).toRight(
        ExecutionFault.UndefinedRegister(name, pc, lane)
      )
    case other =>
      Left(ExecutionFault.InvalidState(s"expected a function value, found $other"))
  }
}
