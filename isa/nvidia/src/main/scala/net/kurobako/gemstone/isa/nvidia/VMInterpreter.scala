package net.kurobako.gemstone.isa.nvidia

import VMExecution.*
import VMIR.*
import java.util.concurrent.atomic.AtomicReference
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
import scala.collection.mutable

object VMInterpreter {

  private case class Cohort(pc: Pc, lanes: VMLaneMask)
  private case class BarrierReduction(
      destination: NonEmpty[String],
      predicate: NonEmpty[String],
      operation: PredicateOperation,
      pc: Pc
  )
  private case class Waiting(cohort: Cohort, key: WaitKey, reduction: Option[BarrierReduction] = None)

  // The dispatch loop exits immediately on execution faults.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.return"))
  def execute(
      kernel: Kernel,
      geometry: LaunchGeometry,
      arguments: Map[NonEmpty[String], Argument],
      memory: AddressMemory,
      control: TaskControl = TaskControl(),
      workgroups: Workgroups = Workgroups.Sequential,
      initialParameterBuffers: Map[Long, Array[Byte]] = Map.empty,
      blockIndices: Option[Vec[Int]] = None
  ): Either[VMFault, Unit] = {
    val laneCount = geometry.groupSize.volumeInt match {
      case Left(message) => return Left(VMFault.InvalidState(message))
      case Right(value)  => value
    }
    val groupCount = geometry.groupCount.volumeInt match {
      case Left(message) => return Left(VMFault.InvalidState(message))
      case Right(value)  => value
    }
    val allLanes = VMLaneMask.all(laneCount.raw) match {
      case Left(fault)  => return Left(fault)
      case Right(value) => value
    }
    val everyGroupFull =
      geometry.globalExtent.x ==
        (geometry.groupSize.x.toLong * geometry.groupCount.x.toLong) &&
        geometry.globalExtent.y ==
        (geometry.groupSize.y.toLong * geometry.groupCount.y.toLong) &&
        geometry.globalExtent.z ==
        (geometry.groupSize.z.toLong * geometry.groupCount.z.toLong)
    def liveLanes(group: Coordinates): Either[VMFault, VMLaneMask] =
      if (everyGroupFull) Right(allLanes)
      else geometry.activeGroupSize(group) match {
        case Left(message) => Left(VMFault.InvalidState(s"${kernel.isaName} geometry failure: $message"))
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
              Left(VMFault.InvalidState(s"${kernel.isaName} geometry failure: $message"))
            case None => Right(live)
          }
      }
    def runBlock(block: Int): Either[VMFault, Unit] =
      if (block < 0 || block >= groupCount.raw)
        Left(VMFault.InvalidState(s"selected ${kernel.isaName} block is outside the launch: $block"))
      else
        geometry.groupCoordinates(block.toLong) match {
          case Left(message) =>
            Left(VMFault.InvalidState(s"${kernel.isaName} geometry failure: $message"))
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
                  workgroups,
                  initialParameterBuffers
                ).map(_ => ())
            }
        }
    val selectedCount                  = blockIndices.fold(groupCount.raw)(_.size)
    def selectedBlock(index: Int): Int = blockIndices.fold(index)(_(index))
    val selectedTasks                  = selectedCount.toLong.unsafe[Long >= 0]
    val workItems: Long >= 0           = laneCount.toLong
    val parallelBlocks                 = workgroups.shouldParallel(selectedTasks, workItems)
    val executionFailure               = AtomicReference(Option.empty[VMFault])
    workgroups.forEach(selectedTasks, parallel = parallelBlocks) { index =>
      runBlock(selectedBlock(index.raw.toInt)).left.map { error =>
        val _ = executionFailure.compareAndSet(None, Some(error))
        error.toFault
      }
    } match {
      case Left(error) => return Left(executionFailure.get().getOrElse(VMFault.fromControl(error)))
      case Right(())   => ()
    }
    Right(())
  }

  // Retain one idle block state across dispatches. The single slot bounds retained memory and transfers ownership
  // atomically when independent executions overlap.
  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  private val blockState = AtomicReference[VMBlockState | Null](null)

  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  private def borrowState(laneCount: Int, initialLive: VMLaneMask, registerChunkCount: Int): VMBlockState = {
    val cached = blockState.getAndSet(null)
    cached match {
      case state: VMBlockState if state.laneCount == laneCount && state.registerChunkCount == registerChunkCount =>
        state.reset(initialLive)
        state
      case _ => VMBlockState(laneCount, initialLive, registerChunkCount = registerChunkCount)
    }
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  private def releaseState(state: VMBlockState): Unit = {
    val _ = blockState.compareAndSet(null, state)
  }

  private def executeBlock(
      kernel: Kernel,
      block: Int,
      group: Coordinates,
      geometry: LaunchGeometry,
      initialLive: VMLaneMask,
      arguments: Map[NonEmpty[String], Argument],
      memory: AddressMemory,
      control: TaskControl,
      workgroups: Workgroups,
      initialParameterBuffers: Map[Long, Array[Byte]],
      initialState: Option[VMBlockState] = None
  ): Either[VMFault, VMBlockState] = {
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
        workgroups,
        initialParameterBuffers,
        state
      )
    finally if (reusable) releaseState(state)
  }

  // Cohort and barrier state model the SIMT machine directly; early exits preserve the first fault.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.return"))
  private def executeBlockWithState(
      kernel: Kernel,
      block: Int,
      group: Coordinates,
      geometry: LaunchGeometry,
      arguments: Map[NonEmpty[String], Argument],
      memory: AddressMemory,
      control: TaskControl,
      workgroups: Workgroups,
      initialParameterBuffers: Map[Long, Array[Byte]],
      state: VMBlockState
  ): Either[VMFault, VMBlockState] = {
    import VMSemantics.*

    state.importParameterBuffers(initialParameterBuffers)
    kernel.parameters.foreach { parameter =>
      arguments.get(parameter.name).foreach(argument =>
        state.bindParameterBuffer(parameter.name, argument.bytes.toArray)
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
    ): Either[VMFault, (VMLaneMask, VMLaneMask)] = warpMemberMask(executable.instruction) match {
      case None             => Right(cohort.lanes -> VMLaneMask.Empty)
      case Some(memberMask) =>
        val context = VMInstructionContext(
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
          workgroups
        )
        val masks                    = mutable.Map.empty[Int, Long]
        var failure: Option[VMFault] = None
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
    def advance(pc: Pc, lanes: VMLaneMask): Either[VMFault, Unit] =
      pc.next.toRight(VMFault.InvalidProgram("program counter overflow")).map(next => enqueue(next, lanes))

    def releaseBarrier(key: WaitKey): Either[VMFault, Unit] = {
      val arrived  = barrierMasks.getOrElse(key, VMLaneMask.Empty)
      val expected = key.scope match {
        case WaitScope.Workgroup => state.live
        case WaitScope.Warp      =>
          val warpBase   = key.id.toInt
          val memberMask = if (key.phase < 0) state.convergence.getOrElse((warpBase, (-key.phase - 1).toInt), 0L)
          else warpBarrierMasks.getOrElse(key, -1L)
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
        // A completed convergence token is no longer live. In particular, a
        // legacy target can be reached along a path that skipped the next SSY
        // using this slot; it must not wait for the previous token's members.
        if (key.scope == WaitScope.Warp && key.phase < 0) {
          val _ = state.convergence.remove((key.id.toInt, (-key.phase - 1).toInt))
        }
        released.view.flatMap(_.reduction).take(1).foreach { reduction =>
          var reduced: Either[VMFault, Boolean] = Right(reduction.operation match {
            case PredicateOperation.And => true
            case PredicateOperation.Or  => false
            case PredicateOperation.Xor => false
          })
          arrived.foreachLane { lane =>
            reduced = reduced.flatMap { current =>
              state.predicates(lane).get(reduction.predicate).toRight(
                VMFault.UndefinedRegister(reduction.predicate, reduction.pc, lane)
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
        val waits = waiting.map(value =>
          s"${value.key.scope}:${value.key.id}@${value.key.phase}, pc ${value.cohort.pc}"
        ).distinct.sorted
        val detail: NonEmpty[String] =
          if (waits.nonEmpty) s"${kernel.name}: ${waits.mkString(", ")}" else s"${kernel.name}: no runnable cohort"
        return Left(VMFault.Deadlock(detail))
      }
      var cohort     = ready.remove(0)
      val executable = kernel.program.at(cohort.pc) match {
        case None =>
          return Left(VMFault.InvalidProgram(
            s"program counter ${cohort.pc} is outside a ${kernel.program.size}-instruction program"
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
        control.checkpoint(cohort.lanes.size.unsafe[Int > 0]) match {
          case Left(fault) => return Left(VMFault.fromControl(fault))
          case Right(())   => ()
        }
        var active                            = cohort.lanes
        var predicateFailure: Option[VMFault] = None
        val predicateIndex                    =
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
                  VMFault.UndefinedRegister(predicate.name, cohort.pc, lane)
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
        val context = VMInstructionContext(
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
          workgroups
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
                if (
                  executable.instruction match {
                    case VMIR.Instruction.Convergence(_, VMIR.ConvergenceAction.Break) => true
                    case _                                                             => false
                  }
                ) {
                  val keys  = barrierMasks.keys.filter(_.phase < 0).toVec
                  var index = 0
                  while (index < keys.size) {
                    releaseBarrier(keys(index)) match {
                      case Left(fault) => return Left(fault)
                      case Right(())   => ()
                    }
                    index += 1
                  }
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
      caller: VMInstructionContext,
      function: DeviceFunction,
      parameters: Vec[NonEmpty[String]],
      returnParameters: Vec[NonEmpty[String]]
  ): Either[VMFault, Unit] = {
    if (parameters.size != function.parameters.size)
      return Left(VMFault.InvalidState(
        s"device function ${function.name} expects ${function.parameters.size} parameters, found ${parameters.size}"
      ))
    if (returnParameters.size != function.returnParameters.size)
      return Left(VMFault.InvalidState(
        s"device function ${function.name} returns ${function.returnParameters.size} parameters, found ${returnParameters.size}"
      ))
    val laneCount = caller.state.laneCount
    val state     = VMBlockState(laneCount, caller.active, caller.state.shared, caller.state.registerChunkCount)
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
      caller.workgroups,
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
