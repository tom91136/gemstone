package net.kurobako.gemstone.isa.ptx.v9_3

import Program.*

import VMIR.*
import VMExecution.*

import net.kurobako.gemstone.NonNegativeLong
import net.kurobako.gemstone.PositiveInt
import net.kurobako.gemstone.Vec

import net.kurobako.gemstone.memory.AddressMemory
import net.kurobako.gemstone.vm.LaunchGeometry
import net.kurobako.gemstone.vm.LaunchGeometry.Coordinates
import net.kurobako.gemstone.vm.Scheduler
import net.kurobako.gemstone.vm.TaskControl

import java.util.concurrent.atomic.AtomicReference
import scala.collection.mutable

private[v9_3] object VMInterpreter {

  private final case class Cohort(pc: Pc, lanes: VMLaneMask)
  private final case class BarrierReduction(
      destination: RegisterName,
      predicate: RegisterName,
      operation: PredicateOperation,
      pc: Pc
  )
  private final case class Waiting(cohort: Cohort, key: WaitKey, reduction: Option[BarrierReduction] = None)

  // The dispatch loop exits immediately on execution faults.
  @SuppressWarnings(
    Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.return", "scalafix:GemstoneNoAssume")
  )
  def execute(
      kernel: Kernel,
      geometry: LaunchGeometry,
      arguments: Map[ParameterName, Argument],
      memory: AddressMemory,
      control: TaskControl = TaskControl.default,
      scheduler: Scheduler = Scheduler.Sequential,
      initialParameterBuffers: Map[Long, Array[Byte]] = Map.empty,
      blockIndices: Option[Vec[Int]] = None
  ): Either[ExecutionFault, Unit] = {
    val laneCount = geometry.groupSize.volumeInt match {
      case Left(message) => return Left(ExecutionFault.InvalidState(message))
      case Right(value)  => value
    }
    val groupCount = geometry.groupCount.volumeInt match {
      case Left(message) => return Left(ExecutionFault.InvalidState(message))
      case Right(value)  => value
    }
    val allLanes = VMLaneMask.all(laneCount.raw) match {
      case Left(fault)  => return Left(fault)
      case Right(value) => value
    }
    val everyGroupFull =
      geometry.globalExtent.x == (geometry.groupSize.x.toLong * geometry.groupCount.x.toLong) &&
        geometry.globalExtent.y == (geometry.groupSize.y.toLong * geometry.groupCount.y.toLong) &&
        geometry.globalExtent.z == (geometry.groupSize.z.toLong * geometry.groupCount.z.toLong)
    def liveLanes(group: Coordinates): Either[ExecutionFault, VMLaneMask] =
      if (everyGroupFull) Right(allLanes)
      else geometry.activeGroupSize(group) match {
        case Left(message) => Left(ExecutionFault.InvalidState(s"PTX geometry failure: $message"))
        case Right(activeSize) if activeSize == geometry.groupSize => Right(allLanes)
        case Right(_)                                              =>
          var live                    = VMLaneMask.Empty
          var failure: Option[String] = None
          allLanes.foreachLane { lane =>
            if (failure.isEmpty)
              geometry.localCoordinates(lane) match {
                case Left(message) => failure = Some(message)
                case Right(local)  =>
                  geometry.globalCoordinatesLong(group, local) match {
                    case Left(message) => failure = Some(message)
                    case Right(global) =>
                      if (geometry.containsGlobal(global))
                        live = live.union(VMLaneMask.ofLane(lane))
                  }
              }
          }
          failure match {
            case Some(message) =>
              Left(ExecutionFault.InvalidState(s"PTX geometry failure: $message"))
            case None => Right(live)
          }
      }
    def runBlock(block: Int): Either[ExecutionFault, Unit] =
      if (block < 0 || block >= groupCount.raw)
        Left(ExecutionFault.InvalidState(s"selected PTX block is outside the launch: $block"))
      else
        geometry.groupCoordinates(block.toLong) match {
          case Left(message) =>
            Left(ExecutionFault.InvalidState(s"PTX geometry failure: $message"))
          case Right(group) =>
            liveLanes(group) match {
              case Left(error) => Left(error)
              case Right(live) =>
                executeBlock(
                  kernel,
                  block,
                  group,
                  geometry,
                  live,
                  arguments,
                  memory,
                  control,
                  scheduler,
                  initialParameterBuffers
                ).map(_ => ())
            }
        }
    val selectedCount                  = blockIndices.fold(groupCount.raw)(_.size)
    def selectedBlock(index: Int): Int = blockIndices.fold(index)(_(index))
    val selectedTasks                  = NonNegativeLong.unsafe(selectedCount.toLong)
    val workItems                      = laneCount.toLong.toNonNegative
    val parallelBlocks                 = scheduler.shouldParallel(selectedTasks, workItems)
    val executionFailure               = AtomicReference(Option.empty[ExecutionFault])
    scheduler.forEach(selectedTasks, parallel = parallelBlocks) { index =>
      runBlock(selectedBlock(index.raw.toInt)).left.map { error =>
        val _ = executionFailure.compareAndSet(None, Some(error))
        error.toFault
      }
    } match {
      case Left(error) => return Left(executionFailure.get().getOrElse(ExecutionFault.fromControl(error)))
      case Right(())   => ()
    }
    Right(())
  }

  // Retain one idle block state across dispatches. The single slot bounds retained memory and transfers ownership
  // atomically when independent executions overlap.
  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  private val blockState = AtomicReference[VMState | Null](null)

  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  private def borrowState(laneCount: Int, initialLive: VMLaneMask, registerChunkCount: Int): VMState = {
    val cached = blockState.getAndSet(null)
    cached match {
      case state: VMState if state.laneCount == laneCount && state.registerChunkCount == registerChunkCount =>
        state.reset(initialLive)
        state
      case _ => VMState(laneCount, initialLive, registerChunkCount = registerChunkCount)
    }
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  private def releaseState(state: VMState): Unit = {
    val _ = blockState.compareAndSet(null, state)
  }

  private def executeBlock(
      kernel: Kernel,
      block: Int,
      group: Coordinates,
      geometry: LaunchGeometry,
      initialLive: VMLaneMask,
      arguments: Map[ParameterName, Argument],
      memory: AddressMemory,
      control: TaskControl,
      scheduler: Scheduler,
      initialParameterBuffers: Map[Long, Array[Byte]],
      initialState: Option[VMState] = None
  ): Either[ExecutionFault, VMState] = {
    val laneCount =
      geometry.groupSize.volumeInt.fold(message => throw IllegalArgumentException(message), value => value)
    val state    = initialState.getOrElse(borrowState(laneCount.raw, initialLive, kernel.registerChunkCount))
    val reusable = initialState.isEmpty
    try
      executeBlockWithState(
        kernel,
        block,
        group,
        geometry,
        arguments,
        memory,
        control,
        scheduler,
        initialParameterBuffers,
        state
      )
    finally if (reusable) releaseState(state)
  }

  // Cohort and barrier state model the SIMT machine directly; early exits preserve the first fault.
  @SuppressWarnings(
    Array(
      "scalafix:DisableSyntax.var",
      "scalafix:DisableSyntax.return",
      "scalafix:GemstoneNoAssume"
    )
  )
  private def executeBlockWithState(
      kernel: Kernel,
      block: Int,
      group: Coordinates,
      geometry: LaunchGeometry,
      arguments: Map[ParameterName, Argument],
      memory: AddressMemory,
      control: TaskControl,
      scheduler: Scheduler,
      initialParameterBuffers: Map[Long, Array[Byte]],
      state: VMState
  ): Either[ExecutionFault, VMState] = {
    import VMSemantics.*

    state.importParameterBuffers(initialParameterBuffers)
    kernel.parameters.foreach { parameter =>
      arguments.get(parameter.name).foreach(argument =>
        state.bindParameterBuffer(parameter.name, argument.bytes.toVector.toArray)
      )
    }
    val ready                                    = mutable.ArrayBuffer.empty[Cohort]
    val waiting                                  = mutable.ArrayBuffer.empty[Waiting]
    val barrierMasks                             = mutable.Map.empty[WaitKey, VMLaneMask]
    val warpBarrierMasks                         = mutable.Map.empty[WaitKey, Long]
    val effects                                  = state.effects
    def enqueue(pc: Pc, lanes: VMLaneMask): Unit =
      if (lanes.nonEmpty) {
        val existing = ready.indexWhere(_.pc == pc)
        if (existing < 0) ready += Cohort(pc, lanes)
        else ready(existing) = Cohort(pc, ready(existing).lanes.union(lanes))
      }

    enqueue(Pc.Entry, state.live)

    def warpMemberMask(instruction: VMIR.Instruction): Option[Value] = instruction match {
      case VMIR.Instruction.ShuffleDown(_, _, _, _, _, memberMask, _)  => Some(memberMask)
      case VMIR.Instruction.ShuffleUp(_, _, _, _, _, memberMask, _)    => Some(memberMask)
      case VMIR.Instruction.ShuffleIndex(_, _, _, _, _, memberMask, _) => Some(memberMask)
      case VMIR.Instruction.ShuffleBfly(_, _, _, _, _, memberMask, _)  => Some(memberMask)
      case VMIR.Instruction.VoteBallot(_, _, memberMask)               => Some(memberMask)
      case VMIR.Instruction.VotePredicate(_, _, memberMask, _)         => Some(memberMask)
      case VMIR.Instruction.WarpReduce(_, _, memberMask, _, _)         => Some(memberMask)
      case _                                                           => None
    }

    def synchronizeWarpInstruction(
        executable: PredicatedInstruction,
        cohort: Cohort
    ): Either[ExecutionFault, (VMLaneMask, VMLaneMask)] = warpMemberMask(executable.instruction) match {
      case None             => Right(cohort.lanes -> VMLaneMask.Empty)
      case Some(memberMask) =>
        val context = VMExecutionContext(
          kernel,
          block,
          group,
          geometry,
          state,
          cohort.pc,
          cohort.lanes,
          arguments,
          memory,
          control,
          scheduler
        )
        val masks                           = mutable.Map.empty[Int, Long]
        var failure: Option[ExecutionFault] = None
        cohort.lanes.foreachLane { lane =>
          if (failure.isEmpty) {
            context.read(lane, memberMask) match {
              case Left(fault)  => failure = Some(fault)
              case Right(value) => masks(lane) = value
            }
          }
        }
        failure match {
          case Some(fault) => Left(fault)
          case None        =>
            var runnable = VMLaneMask.Empty
            var delayed  = VMLaneMask.Empty
            masks.foreach { case (lane, maskValue) =>
              val warpBase = lane & ~31
              val present  = cohort.lanes.filterLanes(candidate => (candidate & ~31) == warpBase)
              val expected = state.live.filterLanes { candidate =>
                (candidate & ~31) == warpBase &&
                ((maskValue & 0xffffffffL) & (1L << (candidate & 31))) != 0L
              }
              if (expected.subtract(present).nonEmpty)
                delayed = delayed.union(VMLaneMask.ofLane(lane))
              else runnable = runnable.union(VMLaneMask.ofLane(lane))
            }
            Right(runnable -> delayed)
        }
    }
    def advance(pc: Pc, lanes: VMLaneMask): Either[ExecutionFault, Unit] =
      pc.next.toRight(ExecutionFault.InvalidProgram("program counter overflow")).map(next => enqueue(next, lanes))

    def releaseBarrier(key: WaitKey): Either[ExecutionFault, Unit] = {
      val arrived  = barrierMasks.getOrElse(key, VMLaneMask.Empty)
      val expected = key.scope match {
        case WaitScope.Workgroup => state.live
        case WaitScope.Warp      =>
          val warpBase   = key.id.toInt
          val memberMask = warpBarrierMasks.getOrElse(key, -1L)
          state.live.filterLanes { lane =>
            (lane & ~31) == warpBase &&
            ((memberMask & 0xffffffffL) & (1L << (lane & 31))) != 0L
          }
        case _ => state.live
      }
      if (arrived.intersect(expected) == expected) {
        val released = Vec.from(waiting.filter(_.key == key))
        waiting.filterInPlace(_.key != key)
        val _ = barrierMasks.remove(key)
        val _ = warpBarrierMasks.remove(key)
        released.view.flatMap(_.reduction).take(1).foreach { reduction =>
          var reduced: Either[ExecutionFault, Boolean] = Right(reduction.operation match {
            case PredicateOperation.And => true
            case PredicateOperation.Or  => false
            case PredicateOperation.Xor => false
          })
          arrived.foreachLane { lane =>
            reduced = reduced.flatMap { current =>
              state.predicates(lane).get(reduction.predicate).toRight(
                ExecutionFault.UndefinedRegister(reduction.predicate, reduction.pc, lane)
              ).map { value =>
                reduction.operation match {
                  case PredicateOperation.And => current && value
                  case PredicateOperation.Or  => current || value
                  case PredicateOperation.Xor => current ^ value
                }
              }
            }
          }
          reduced.foreach { value =>
            arrived.foreachLane(lane => state.predicates(lane)(reduction.destination) = value)
          }
        }
        var releasedIndex = 0
        while (releasedIndex < released.size) {
          val releasedCohort = released(releasedIndex).cohort
          advance(releasedCohort.pc, releasedCohort.lanes) match {
            case Left(fault) => return Left(fault)
            case Right(())   => ()
          }
          releasedIndex += 1
        }
      }
      Right(())
    }

    while (state.live.nonEmpty) {
      if (ready.isEmpty) {
        val waits  = waiting.map(value => s"${value.key.scope}:${value.key.id}@${value.key.phase}").distinct.sorted
        val detail = if (waits.nonEmpty) waits.mkString(", ") else "no runnable cohort"
        return Left(ExecutionFault.Deadlock(detail))
      }
      var cohort     = ready.remove(0)
      val executable = kernel.program.at(cohort.pc) match {
        case None =>
          return Left(ExecutionFault.InvalidProgram(
            s"program counter ${cohort.pc.raw} is outside a ${kernel.program.size}-instruction program"
          ))
        case Some(value) => value
      }
      if (warpMemberMask(executable.instruction).nonEmpty) {
        var readyIndex = 0
        while (readyIndex < ready.size)
          if (ready(readyIndex).pc == cohort.pc) {
            cohort = cohort.copy(lanes = cohort.lanes.union(ready.remove(readyIndex).lanes))
          } else readyIndex += 1
      }
      synchronizeWarpInstruction(executable, cohort) match {
        case Left(fault)                => return Left(fault)
        case Right((runnable, delayed)) =>
          enqueue(cohort.pc, delayed)
          cohort = cohort.copy(lanes = runnable)
      }
      if (cohort.lanes.nonEmpty) {
        control.checkpoint(PositiveInt.unsafe(cohort.lanes.size)) match {
          case Left(fault) => return Left(ExecutionFault.fromControl(fault))
          case Right(())   => ()
        }
        var active                                   = cohort.lanes
        var predicateFailure: Option[ExecutionFault] = None
        val predicateIndex                           =
          executable.predication.map(predicate => VMPredicateFile.index(predicate.name)).getOrElse(-1)
        executable.predication.foreach { predicate =>
          active = cohort.lanes.filterLanes { lane =>
            val value = if (predicateIndex >= 0) state.predicates(lane).getSlot(predicateIndex)
            else
              state.predicates(lane).get(predicate.name)
            value match {
              case Some(predicateValue) => predicateValue != predicate.negated
              case None                 =>
                predicateFailure = Some(
                  ExecutionFault.UndefinedRegister(predicate.name, cohort.pc, lane)
                )
                false
            }
          }
        }
        predicateFailure match {
          case Some(fault) => return Left(fault)
          case None        => ()
        }
        val skipped = if (executable.predication.isEmpty) VMLaneMask.Empty else cohort.lanes.subtract(active)
        val context = VMExecutionContext(
          kernel,
          block,
          group,
          geometry,
          state,
          cohort.pc,
          active,
          arguments,
          memory,
          control,
          scheduler
        )

        effects.clear()
        val outcome =
          if (active.isEmpty) Right(Step.Advance)
          else executable.instruction.execute(context, effects)
        outcome match {
          case Left(fault) =>
            return Left(fault)
          case Right(Step.Trap(fault)) => return Left(fault)
          case Right(control)          =>
            effects.commit(state, memory, cohort.pc) match {
              case Left(fault) =>
                return Left(fault)
              case Right(()) => ()
            }
            control match {
              case Step.Advance =>
                advance(cohort.pc, cohort.lanes) match {
                  case Left(fault) => return Left(fault)
                  case Right(())   => ()
                }
              case Step.Jump(target) =>
                enqueue(target, active)
                advance(cohort.pc, skipped) match {
                  case Left(fault) => return Left(fault)
                  case Right(())   => ()
                }
              case Step.Split(taken, target, fallthrough) =>
                enqueue(target, taken)
                advance(cohort.pc, fallthrough) match {
                  case Left(fault) => return Left(fault)
                  case Right(())   => ()
                }
              case Step.Suspend(key) =>
                advance(cohort.pc, skipped) match {
                  case Left(fault) => return Left(fault)
                  case Right(())   => ()
                }
                val reduction = executable.instruction match {
                  case VMIR.Instruction.BarrierReduce(destination, _, predicate, operation) =>
                    Some(BarrierReduction(destination, predicate, operation, cohort.pc))
                  case _ => None
                }
                key.scope match {
                  case WaitScope.Warp =>
                    val lanesByWarp = mutable.Map.empty[Int, VMLaneMask]
                    active.foreachLane { lane =>
                      val warpBase = lane & ~31
                      lanesByWarp(warpBase) = lanesByWarp.getOrElse(warpBase, VMLaneMask.Empty).union(
                        VMLaneMask.one(lane).fold(fault => throw IllegalStateException(fault.message), value => value)
                      )
                    }
                    val warps     = Vec.from(lanesByWarp).sortBy(_._1)
                    var warpIndex = 0
                    while (warpIndex < warps.size) {
                      val (warpBase, warpLanes) = warps(warpIndex)
                      val warpKey               = WaitKey(WaitScope.Warp, warpBase.toLong, key.phase)
                      waiting += Waiting(cohort.copy(lanes = warpLanes), warpKey, reduction)
                      warpBarrierMasks(warpKey) = key.id
                      barrierMasks(warpKey) = barrierMasks.getOrElse(warpKey, VMLaneMask.Empty).union(warpLanes)
                      releaseBarrier(warpKey) match {
                        case Left(fault) => return Left(fault)
                        case Right(())   => ()
                      }
                      warpIndex += 1
                    }
                  case _ =>
                    waiting += Waiting(cohort.copy(lanes = active), key, reduction)
                    barrierMasks(key) = barrierMasks.getOrElse(key, VMLaneMask.Empty).union(active)
                    releaseBarrier(key) match {
                      case Left(fault) => return Left(fault)
                      case Right(())   => ()
                    }
                }
              case Step.Exit(mask) =>
                state.live = state.live.subtract(mask)
                advance(cohort.pc, skipped) match {
                  case Left(fault) => return Left(fault)
                  case Right(())   => ()
                }
                val keys     = Vec.from(barrierMasks.keys)
                var keyIndex = 0
                while (keyIndex < keys.size) {
                  releaseBarrier(keys(keyIndex)) match {
                    case Left(fault) => return Left(fault)
                    case Right(())   => ()
                  }
                  keyIndex += 1
                }
              case Step.Trap(fault) => return Left(fault)
            }
        }
      }
    }
    Right(state)
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.return"))
  def executeDeviceFunction(
      caller: VMExecutionContext,
      function: DeviceFunction,
      parameters: Vec[ParameterName],
      returnParameters: Vec[ParameterName]
  ): Either[ExecutionFault, Unit] = {
    if (parameters.size != function.parameters.size)
      return Left(ExecutionFault.InvalidState(
        s"device function ${function.name} expects ${function.parameters.size} parameters, found ${parameters.size}"
      ))
    if (returnParameters.size != function.returnParameters.size)
      return Left(ExecutionFault.InvalidState(
        s"device function ${function.name} returns ${function.returnParameters.size} parameters, found ${returnParameters.size}"
      ))
    val laneCount = caller.state.laneCount
    val state     = VMState(laneCount, caller.active, caller.state.shared, caller.state.registerChunkCount)
    state.importParameterBuffers(caller.state.parameterBuffers.toMap)
    state.parameterAddresses ++= caller.state.parameterAddresses
    // Arity is validated above. A call operand whose param-space buffer was
    // never written holds an undefined value, so it binds as zero.
    caller.active.foreachLane { lane =>
      parameters.zip(function.parameters).foreach { case (callerName, functionName) =>
        state.parameters(lane)(functionName) = caller.state.parameters(lane).getOrElse(callerName, 0L)
      }
    }
    val kernel = caller.kernel.copy(
      parameters = Vec.empty,
      program = function.program,
      sharedSymbols = caller.kernel.sharedSymbols ++ function.sharedSymbols
    )
    executeBlock(
      kernel,
      caller.block,
      caller.group,
      caller.geometry,
      caller.active,
      Map.empty,
      caller.memory,
      caller.control,
      caller.scheduler,
      Map.empty,
      Some(state)
    ).map { completed =>
      caller.active.foreachLane { lane =>
        returnParameters.zip(function.returnParameters).foreach { case (callerName, functionName) =>
          completed.parameters(lane).get(functionName).foreach { value =>
            caller.state.parameters(lane)(callerName) = value
          }
        }
      }
    }
  }

}
