package net.kurobako.gemstone.isa.nvidia

import VMExecution.*
import VMIR.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.vm.*

private object VMSemantics {

  def execute(instruction: Instruction, sinks: LaneLoop): Either[Fault, Step] = {
    val staged = NvidiaStaged.execute(instruction, sinks)
    if (staged eq NvidiaStaged.Unstaged) executeStaged(instruction, sinks.context)
    else advance(staged)
  }

  private inline def advance(result: Either[Fault, Unit]): Either[Fault, Step] = result match {
    case Left(fault) => Left(fault)
    case _           => Advanced
  }

  private def executeStaged(instruction: Instruction, context: VMContext): Either[Fault, Step] = instruction match {
    case _: (Instruction.Branch | Instruction.BranchTable | Instruction.ReturnStackPush | Instruction.ReturnStackPop |
          Instruction.ReturnStackSave | Instruction.Call) =>
      executeControl(instruction, context)
    case Instruction.NoOp | Instruction.Return | Instruction.Exit =>
      executeControl(instruction, context)
    case Instruction.Trap | Instruction.CallBuiltin(_, _, _) | Instruction.Convergence(_, _) | Instruction.Fence =>
      executeControlMachine(instruction, context)
    case Instruction.WarpReduce(operation = operation) =>
      Left(Fault.Backend(s"unsupported warp reduction operation: $operation"))
    case Instruction.InvalidateConditionFlag(flag) =>
      context.active.foreachLane(lane => context.effects.stage(VMWrite.ConditionFlag(lane, flag, None)))
      Advanced
    case Instruction.InvalidateAddThreeCarry =>
      context.active.foreachLane(lane => context.effects.stage(VMWrite.AddThreeCarry(lane, None)))
      Advanced
    case Instruction.Tensor(_: (Matrix.Operation.StoreMatrix | Matrix.Operation.Load | Matrix.Operation.Store)) =>
      executeTensorMachine(instruction, context)
    case Instruction.Tensor(operation: (Matrix.Operation.LoadMatrix | Matrix.Operation.Transpose |
          Matrix.Operation.Multiply | Matrix.Operation.Widen | Matrix.Operation.WarpgroupMultiply)) =>
      VMTensor.execute(operation, context)
    case _: (Instruction.MBarrierLoad | Instruction.MBarrierInit | Instruction.MBarrierInvalidate |
          Instruction.MBarrierArrive | Instruction.MBarrierTest | Instruction.MBarrierTransaction |
          Instruction.MBarrierPending | Instruction.WarpBarrier | Instruction.ClusterBarrier) =>
      executeBarrierMachine(instruction, context)
    case _: (Instruction.BarrierSync | Instruction.BarrierArrive | Instruction.BarrierReduce) =>
      executeWorkgroupBarrier(instruction, context)
    case _: (Instruction.SharedLockLoad | Instruction.SharedStoreUnlock) => executeLock(instruction, context)
    case _: (Instruction.ClusterMap | Instruction.ClusterRankOf | Instruction.ClusterTryCancel) =>
      executeClusterMachine(instruction, context)
    case _: (Instruction.AsyncCopy | Instruction.AsyncBulkCopy | Instruction.AsyncBulkStore |
          Instruction.AsyncBulkFill | Instruction.AsyncBulkReduce | Instruction.AsyncSharedStore |
          Instruction.AsyncSharedReduce | Instruction.TensorMapCopy) =>
      executeAsyncMachine(instruction, context)
    case _: (Instruction.AsyncTensor | Instruction.TensorMapReplace) => executeTensorMap(instruction, context)
    case _: (Instruction.StackAllocate | Instruction.StackSave | Instruction.StackRestore) =>
      executeStack(instruction, context)
    case _: (Instruction.TensorMemoryAllocate | Instruction.TensorMemoryDeallocate) |
        Instruction.TensorMemoryRelinquish =>
      executeTensorMemoryState(instruction, context)
    case _: (Instruction.TensorMemoryLoad | Instruction.TensorMemoryStore) =>
      executeTensorMemoryMove(instruction, context)
    case _: (Instruction.TensorMemoryCopy | Instruction.TensorMemoryShift | Instruction.TensorMemoryCommit) =>
      executeTensorMemoryCopy(instruction, context)
    case _: (Instruction.TensorMemoryClaim | Instruction.TensorMemoryRelease) =>
      executeTensorMemoryClaim(instruction, context)
    case other => Left(Fault.IllegalInstruction(s"unhandled ${context.kernel.isaName} instruction $other"))
  }

  private def executeControl(
      instruction: Instruction,
      context: VMContext
  ): Either[Fault, Step] =
    (instruction: @unchecked) match {
      case Instruction.Branch(target)              => VMMachineOps(context).jump(target)
      case Instruction.BranchTable(index, targets) =>
        VMMachineOps(context).branchTable(index, targets)
      case Instruction.ReturnStackPush(address) =>
        MachineSemantics.returnStackPush(Execute, VMMachineOps(context))(address)
      case Instruction.ReturnStackSave(source) =>
        context.active.foreachLane { lane =>
          context.lane(lane).returnStack.push(context.lane(lane).register(source))
        }
        Advanced
      case Instruction.ReturnStackPop(destination) =>
        advance(LaneFaults.everyLane(context) { lane =>
          val stack = context.lane(lane).returnStack
          Either.cond(
            stack.nonEmpty,
            context.effects.stageRegister(lane, destination, stack.pop()),
            Fault.Backend("return stack is empty")
          )
        })
      case Instruction.Call(function, returnParameters, parameters) =>
        VMMachineOps(context).call(function, returnParameters, parameters)
      case Instruction.NoOp   => Advanced
      case Instruction.Return => VMMachineOps(context).returnControl
      case Instruction.Exit   => VMMachineOps(context).exit
    }

  private def executeControlMachine(instruction: Instruction, context: VMContext): Either[Fault, Step] = {
    val machine = VMMachineOps(context)
    (instruction: @unchecked) match {
      case Instruction.CallBuiltin(builtin, returnParameters, parameters) =>
        machine.callBuiltin(builtin, returnParameters, parameters)(machine.advance)
      case Instruction.Convergence(token, action) => MachineSemantics.convergence(machine)(token, action)
      case Instruction.Trap                       => machine.trap
      case Instruction.Fence                      => machine.fenceHost
    }
  }

  private def executeAsyncMachine(instruction: Instruction, context: VMContext): Either[Fault, Step] = {
    val machine = VMMachineOps(context)
    (instruction: @unchecked) match {
      case Instruction.AsyncCopy(destination, source, size, sourceSize) =>
        MachineSemantics.asyncCopy(Execute, machine)(destination, source, size, sourceSize)
      case copy: Instruction.AsyncBulkCopy => MachineSemantics.bulkCopy(Execute, machine)(copy)
      case Instruction.AsyncBulkStore(destination, source, size, byteMask) =>
        MachineSemantics.bulkStore(Execute, machine)(destination, source, size, byteMask)
      case Instruction.AsyncBulkFill(address, size) => MachineSemantics.bulkFill(Execute, machine)(address, size)
      case copy: Instruction.AsyncBulkReduce        => MachineSemantics.bulkReduce(Execute, machine)(copy)
      case Instruction.AsyncSharedStore(address, sources, width, barrier) =>
        MachineSemantics.sharedStore(Execute, machine)(address, sources, width, barrier)
      case Instruction.AsyncSharedReduce(address, source, width, operation, barrier) =>
        MachineSemantics.sharedReduce(Execute, machine)(address, source, width, operation, barrier)
      case Instruction.TensorMapCopy(destination, source) =>
        MachineSemantics.tensorMapCopy(Execute, machine)(destination, source)
    }
  }

  private def executeTensorMap(instruction: Instruction, context: VMContext): Either[Fault, Step] = {
    val machine = VMMachineOps(context)
    (instruction: @unchecked) match {
      case copy: Instruction.AsyncTensor => MachineSemantics.asyncTensor(Execute, machine)(copy)
      case Instruction.TensorMapReplace(address, field, index, value) =>
        MachineSemantics.tensorMapReplace(Execute, machine)(address, field, index, value)
    }
  }

  private def executeTensorMemoryState(instruction: Instruction, context: VMContext): Either[Fault, Step] = {
    val machine = VMMachineOps(context)
    (instruction: @unchecked) match {
      case Instruction.TensorMemoryAllocate(destination, columns) =>
        MachineSemantics.tensorMemoryAllocate(Execute, machine)(destination, columns)
      case Instruction.TensorMemoryDeallocate(address, columns) =>
        MachineSemantics.tensorMemoryDeallocate(Execute, machine)(address, columns)
      case Instruction.TensorMemoryRelinquish => MachineSemantics.tensorMemoryRelinquish(machine)
    }
  }

  private def executeTensorMemoryClaim(instruction: Instruction, context: VMContext): Either[Fault, Step] = {
    val machine = VMMachineOps(context)
    (instruction: @unchecked) match {
      case Instruction.TensorMemoryClaim(destination, claimed, granules) =>
        MachineSemantics.tensorMemoryClaim(Execute, machine)(destination, claimed, granules)
      case Instruction.TensorMemoryRelease(mask) => MachineSemantics.tensorMemoryRelease(machine)(mask)
    }
  }

  private def executeTensorMemoryMove(instruction: Instruction, context: VMContext): Either[Fault, Step] = {
    val machine = VMMachineOps(context)
    (instruction: @unchecked) match {
      case Instruction.TensorMemoryLoad(movement, destinations, address, reduction) =>
        MachineSemantics.tensorMemoryLoad(Execute, machine)(movement, destinations, address, reduction)
      case Instruction.TensorMemoryStore(movement, sources, address) =>
        MachineSemantics.tensorMemoryStore(Execute, machine)(movement, sources, address)
    }
  }

  private def executeTensorMemoryCopy(instruction: Instruction, context: VMContext): Either[Fault, Step] = {
    val machine = VMMachineOps(context)
    (instruction: @unchecked) match {
      case Instruction.TensorMemoryCopy(copy, address, descriptor) =>
        MachineSemantics.tensorMemoryCopy(Execute, machine)(copy, address, descriptor)
      case Instruction.TensorMemoryShift(address, pair) =>
        MachineSemantics.tensorMemoryShift(Execute, machine)(address, pair)
      case Instruction.TensorMemoryCommit(barrier, mask) =>
        MachineSemantics.tensorMemoryCommit(Execute, machine)(barrier, mask)
    }
  }

  private def executeBarrierMachine(instruction: Instruction, context: VMContext): Either[Fault, Step] = {
    val machine = VMMachineOps(context)
    (instruction: @unchecked) match {
      case Instruction.MBarrierLoad(address)        => MachineSemantics.mbarrierLoad(machine)(address)
      case Instruction.MBarrierInit(address, count) => MachineSemantics.mbarrierInit(machine)(address, count)
      case Instruction.MBarrierInvalidate(address)  => MachineSemantics.mbarrierInvalidate(machine)(address)
      case Instruction.MBarrierArrive(destination, address, count, bytes, drop, complete) =>
        MachineSemantics.mbarrierArrive(machine)(destination, address, count, bytes, drop, complete)
      case Instruction.MBarrierTransaction(address, bytes, complete) =>
        MachineSemantics.mbarrierTransaction(machine)(address, bytes, complete)
      case Instruction.MBarrierTest(destination, address, phase, parity) =>
        MachineSemantics.mbarrierTest(machine)(destination, address, phase, parity)
      case Instruction.MBarrierPending(destination, token) =>
        MachineSemantics.mbarrierPending(Execute, machine)(destination, token)
      case Instruction.WarpBarrier(memberMask) => machine.warpSync(memberMask)
      case Instruction.ClusterBarrier(arrive)  => machine.clusterBarrier(arrive)
    }
  }

  private def executeWorkgroupBarrier(instruction: Instruction, context: VMContext): Either[Fault, Step] = {
    val machine = VMMachineOps(context)
    (instruction: @unchecked) match {
      case Instruction.BarrierSync(id, count)   => MachineSemantics.barrierSync(Execute, machine)(id, count)
      case Instruction.BarrierArrive(id, count) => MachineSemantics.barrierArrive(Execute, machine)(id, count)
      case Instruction.BarrierReduce(result, id, predicate, count, negated) =>
        MachineSemantics.barrierReduce(Execute, machine)(result, id, predicate, count, negated)
    }
  }

  private def executeLock(instruction: Instruction, context: VMContext): Either[Fault, Step] = {
    val machine = VMMachineOps(context)
    (instruction: @unchecked) match {
      case Instruction.SharedLockLoad(destination, result, address, width) =>
        MachineSemantics.sharedLockLoad(Execute, machine)(destination, result, address, width)
      case Instruction.SharedStoreUnlock(destination, address, source, width) =>
        MachineSemantics.sharedStoreUnlock(Execute, machine)(destination, address, source, width)
    }
  }

  private def executeTensorMachine(instruction: Instruction, context: VMContext): Either[Fault, Step] = {
    val machine = VMMachineOps(context)
    (instruction: @unchecked) match {
      case Instruction.Tensor(Matrix.Operation.StoreMatrix(tile, sources, address, space)) =>
        MachineSemantics.storeMatrix(Execute, machine)(tile, sources, address, space)
      case Instruction.Tensor(Matrix.Operation.Load(fragment, destinations, address, stride, columnMajor, space)) =>
        MachineSemantics.loadFragment(Execute, machine)(fragment, destinations, address, stride, columnMajor, space)
      case Instruction.Tensor(Matrix.Operation.Store(fragment, sources, address, stride, columnMajor, space)) =>
        MachineSemantics.storeFragment(Execute, machine)(fragment, sources, address, stride, columnMajor, space)
    }
  }

  private def executeClusterMachine(instruction: Instruction, context: VMContext): Either[Fault, Step] = {
    val machine = VMMachineOps(context)
    (instruction: @unchecked) match {
      case Instruction.ClusterMap(destination, address, rank) =>
        MachineSemantics.clusterMap(Execute, machine)(destination, address, rank)
      case Instruction.ClusterRankOf(destination, address) =>
        MachineSemantics.clusterRank(Execute, machine)(destination, address)
      case Instruction.ClusterTryCancel(response, barrier, multicast) =>
        MachineSemantics.clusterTryCancel(Execute, machine)(response, barrier, multicast)
    }
  }

  private def executeStack(instruction: Instruction, context: VMContext): Either[Fault, Step] = {
    val machine = VMMachineOps(context)
    (instruction: @unchecked) match {
      case Instruction.StackAllocate(destination, size, alignment) =>
        MachineSemantics.stackAllocate(Execute, machine)(destination, size, alignment)
      case Instruction.StackSave(destination) => MachineSemantics.stackSave(machine)(destination)
      case Instruction.StackRestore(source)   => MachineSemantics.stackRestore(machine)(source)
    }
  }
}
