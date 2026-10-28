package net.kurobako.gemstone.isa.nvidia

import MachineSemantics.*
import VMIR.*
import VMIR.Matrix.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.render.*

private[isa] object MachineRendering {

  /** The machine semantics of `instruction` rendered over `machine`, or none for an instruction they do not cover. */
  def render(instruction: Instruction, machine: NvidiaMachineRender): Option[Term.Steps] =
    renderControl(instruction, machine)
      .orElse(renderAsync(instruction, machine))
      .orElse(renderBarrier(instruction, machine))
      .orElse(renderCluster(instruction, machine))
      .orElse(renderTensor(instruction, machine))
      .orElse(renderTensorMemory(instruction, machine))
      .orElse(renderTensorMemoryClaim(instruction, machine))
      .orElse(renderTensorMemoryMultiply(instruction, machine))
      .orElse(renderLock(instruction, machine))
      .orElse(renderStack(instruction, machine))

  private def renderControl(instruction: Instruction, machine: NvidiaMachineRender): Option[Term.Steps] =
    instruction match {
      case Instruction.Branch(target)                            => Some(machine.jump(target))
      case Instruction.BranchTable(index, targets)               => Some(machine.branchTable(index, targets))
      case Instruction.Call(function, returns, parameters)       => Some(machine.call(function, returns, parameters))
      case Instruction.CallBuiltin(builtin, returns, parameters) =>
        Some(machine.callBuiltin(builtin, returns, parameters)(machine.advance))
      case Instruction.Return                     => Some(machine.returnControl)
      case Instruction.Exit                       => Some(machine.exit)
      case Instruction.Trap                       => Some(machine.trap)
      case Instruction.Fence                      => Some(machine.fenceHost)
      case Instruction.Convergence(token, action) => Some(convergence(machine)(token, action))
      case Instruction.ReturnStackPush(address)   => Some(returnStackPush(Render, machine)(address))
      case _                                      => None
    }

  private def renderAsync(instruction: Instruction, machine: NvidiaMachineRender): Option[Term.Steps] =
    instruction match {
      case Instruction.AsyncCopy(destination, source, size, sourceSize) =>
        Some(asyncCopy(Render, machine)(destination, source, size, sourceSize))
      case copy: Instruction.AsyncBulkCopy                             => Some(renderBulkCopy(copy, machine))
      case Instruction.AsyncBulkStore(destination, source, size, mask) =>
        Some(bulkStore(Render, machine)(destination, source, size, mask))
      case Instruction.AsyncBulkFill(address, size) => Some(bulkFill(Render, machine)(address, size))
      case copy: Instruction.AsyncBulkReduce        => Some(bulkReduce(Render, machine)(copy))
      case Instruction.AsyncSharedStore(address, sources, width, barrier) =>
        Some(sharedStore(Render, machine)(address, sources, width, barrier))
      case Instruction.AsyncSharedReduce(address, source, width, operation, barrier) =>
        Some(sharedReduce(Render, machine)(address, source, width, operation, barrier))
      case Instruction.TensorMapCopy(destination, source) => Some(tensorMapCopy(Render, machine)(destination, source))
      case copy: Instruction.AsyncTensor                  => Some(renderTensorCopy(copy, machine))
      case Instruction.TensorMapReplace(address, field, index, value) =>
        Some(renderTensorMapReplace(address, field, index, value, machine))
      case _ => None
    }

  private def renderBarrier(instruction: Instruction, machine: NvidiaMachineRender): Option[Term.Steps] =
    instruction match {
      case Instruction.BarrierSync(id, count)   => Some(barrierSync(Render, machine)(id, count))
      case Instruction.BarrierArrive(id, count) => Some(barrierArrive(Render, machine)(id, count))
      case Instruction.BarrierReduce(result, id, predicate, count, negated) =>
        Some(barrierReduce(Render, machine)(result, id, predicate, count, negated))
      case Instruction.WarpBarrier(memberMask)      => Some(machine.warpSync(memberMask))
      case Instruction.MBarrierLoad(address)        => Some(mbarrierLoad(machine)(address))
      case Instruction.MBarrierInit(address, count) => Some(mbarrierInit(machine)(address, count))
      case Instruction.MBarrierInvalidate(address)  => Some(mbarrierInvalidate(machine)(address))
      case Instruction.MBarrierArrive(destination, address, count, bytes, drop, complete) =>
        Some(mbarrierArrive(machine)(destination, address, count, bytes, drop, complete))
      case Instruction.MBarrierTransaction(address, bytes, complete) =>
        Some(mbarrierTransaction(machine)(address, bytes, complete))
      case Instruction.MBarrierTest(destination, address, phase, parity) =>
        Some(mbarrierTest(machine)(destination, address, phase, parity))
      case Instruction.MBarrierPending(destination, token) => Some(mbarrierPending(Render, machine)(destination, token))
      case _                                               => None
    }

  private def renderCluster(instruction: Instruction, machine: NvidiaMachineRender): Option[Term.Steps] =
    instruction match {
      case Instruction.ClusterBarrier(arrive)                 => Some(machine.clusterBarrier(arrive))
      case Instruction.ClusterMap(destination, address, rank) =>
        Some(clusterMap(Render, machine)(destination, address, rank))
      case Instruction.ClusterRankOf(destination, address) => Some(clusterRank(Render, machine)(destination, address))
      case Instruction.ClusterTryCancel(response, barrier, multicast) =>
        Some(clusterTryCancel(Render, machine)(response, barrier, multicast))
      case _ => None
    }

  private def renderTensor(instruction: Instruction, machine: NvidiaMachineRender): Option[Term.Steps] =
    instruction match {
      case Instruction.Tensor(Operation.Transpose(destination, source)) =>
        Some(renderMoveMatrix(destination, source, machine))
      case Instruction.Tensor(Operation.LoadMatrix(tile, destinations, address, _)) =>
        Some(renderLoadMatrix(tile, destinations, address, machine))
      case Instruction.Tensor(Operation.StoreMatrix(tile, sources, address, space)) =>
        Some(renderStoreMatrix(tile, sources, address, space, machine))
      case Instruction.Tensor(Operation.Load(fragment, destinations, address, stride, columnMajor, space)) =>
        Some(renderLoadFragment(fragment, destinations, address, stride, columnMajor, space, machine))
      case Instruction.Tensor(Operation.Store(fragment, sources, address, stride, columnMajor, space)) =>
        Some(renderStoreFragment(fragment, sources, address, stride, columnMajor, space, machine))
      case _ => None
    }

  private def renderTensorMemory(instruction: Instruction, machine: NvidiaMachineRender): Option[Term.Steps] =
    instruction match {
      case Instruction.TensorMemoryAllocate(destination, columns) =>
        Some(renderTensorMemoryAllocate(destination, columns, machine))
      case Instruction.TensorMemoryDeallocate(address, columns) =>
        Some(renderTensorMemoryDeallocate(address, columns, machine))
      case Instruction.TensorMemoryRelinquish => Some(tensorMemoryRelinquish(machine))
      case Instruction.TensorMemoryLoad(movement, destinations, address, reduction) =>
        Some(renderTensorMemoryLoad(movement, destinations, address, reduction, machine))
      case Instruction.TensorMemoryStore(movement, sources, address) =>
        Some(renderTensorMemoryStore(movement, sources, address, machine))
      case Instruction.TensorMemoryCopy(copy, address, descriptor) =>
        Some(renderTensorMemoryCopy(copy, address, descriptor, machine))
      case Instruction.TensorMemoryShift(address, pair)  => Some(renderTensorMemoryShift(address, pair, machine))
      case Instruction.TensorMemoryCommit(barrier, mask) => Some(renderTensorMemoryCommit(barrier, mask, machine))
      case _                                             => None
    }

  private def renderTensorMemoryClaim(instruction: Instruction, machine: NvidiaMachineRender): Option[Term.Steps] =
    instruction match {
      case Instruction.TensorMemoryClaim(destination, claimed, granules) =>
        Some(tensorMemoryClaim(Render, machine)(destination, claimed, granules))
      case Instruction.TensorMemoryRelease(mask) => Some(tensorMemoryRelease(machine)(mask))
      case _                                     => None
    }

  private def renderTensorMemoryMultiply(instruction: Instruction, machine: NvidiaMachineRender): Option[Term.Steps] =
    instruction match {
      case Instruction.TensorMemoryMultiply(operation) => Some(tensorMemoryMultiply(machine)(operation))
      case _                                           => None
    }

  private def renderLock(instruction: Instruction, machine: NvidiaMachineRender): Option[Term.Steps] =
    instruction match {
      case Instruction.SharedLockLoad(destination, result, address, width) =>
        Some(sharedLockLoad(Render, machine)(destination, result, address, width))
      case Instruction.SharedStoreUnlock(destination, address, source, width) =>
        Some(sharedStoreUnlock(Render, machine)(destination, address, source, width))
      case _ => None
    }

  private def renderStack(instruction: Instruction, machine: NvidiaMachineRender): Option[Term.Steps] =
    instruction match {
      case Instruction.StackAllocate(destination, size, alignment) =>
        Some(stackAllocate(Render, machine)(destination, size, alignment))
      case Instruction.StackSave(destination) => Some(stackSave(machine)(destination))
      case Instruction.StackRestore(source)   => Some(stackRestore(machine)(source))
      case _                                  => None
    }

  private def renderBulkCopy(copy: Instruction.AsyncBulkCopy, machine: NvidiaMachineRender): Term.Steps =
    bulkCopy(Render, machine)(copy)

  private def renderTensorCopy(copy: Instruction.AsyncTensor, machine: NvidiaMachineRender): Term.Steps =
    asyncTensor(Render, machine)(copy)

  private def renderTensorMapReplace(
      address: Value,
      field: TensorMap.Field,
      index: Int,
      value: Value,
      machine: NvidiaMachineRender
  ): Term.Steps = tensorMapReplace(Render, machine)(address, field, index, value)

  private def renderTensorMemoryAllocate(destination: Value, columns: Value, machine: NvidiaMachineRender)
      : Term.Steps = tensorMemoryAllocate(Render, machine)(destination, columns)

  private def renderTensorMemoryDeallocate(address: Value, columns: Value, machine: NvidiaMachineRender): Term.Steps =
    tensorMemoryDeallocate(Render, machine)(address, columns)

  private def renderTensorMemoryLoad(
      movement: Tcgen05.Movement,
      destinations: Vec[Register],
      address: Value,
      reduction: Option[Tcgen05.Reduction],
      machine: NvidiaMachineRender
  ): Term.Steps = tensorMemoryLoad(Render, machine)(movement, destinations, address, reduction)

  private def renderTensorMemoryStore(
      movement: Tcgen05.Movement,
      sources: Vec[Value],
      address: Value,
      machine: NvidiaMachineRender
  ): Term.Steps = tensorMemoryStore(Render, machine)(movement, sources, address)

  private def renderTensorMemoryCopy(
      copy: Tcgen05.Copy,
      address: Value,
      descriptor: Value,
      machine: NvidiaMachineRender
  ): Term.Steps = tensorMemoryCopy(Render, machine)(copy, address, descriptor)

  private def renderTensorMemoryShift(address: Value, pair: Boolean, machine: NvidiaMachineRender): Term.Steps =
    tensorMemoryShift(Render, machine)(address, pair)

  private def renderTensorMemoryCommit(barrier: Value, mask: Option[Value], machine: NvidiaMachineRender): Term.Steps =
    tensorMemoryCommit(Render, machine)(barrier, mask)

  private def renderMoveMatrix(destination: Register, source: Value, machine: NvidiaMachineRender): Term.Steps =
    moveMatrix(Render, machine)(destination, source)

  private def renderLoadMatrix(
      tile: Tile,
      destinations: Vec[Register],
      address: Value,
      machine: NvidiaMachineRender
  ): Term.Steps = loadMatrix(Render, machine)(tile, destinations, address)

  private def renderStoreMatrix(
      tile: Tile,
      sources: Vec[Value],
      address: Value,
      space: MemorySpace,
      machine: NvidiaMachineRender
  ): Term.Steps = storeMatrix(Render, machine)(tile, sources, address, space)

  private def renderLoadFragment(
      fragment: Fragment,
      destinations: Vec[Register],
      address: Value,
      stride: Value,
      columnMajor: Boolean,
      space: MemorySpace,
      machine: NvidiaMachineRender
  ): Term.Steps = loadFragment(Render, machine)(fragment, destinations, address, stride, columnMajor, space)

  private def renderStoreFragment(
      fragment: Fragment,
      sources: Vec[Value],
      address: Value,
      stride: Value,
      columnMajor: Boolean,
      space: MemorySpace,
      machine: NvidiaMachineRender
  ): Term.Steps = storeFragment(Render, machine)(fragment, sources, address, stride, columnMajor, space)
}
