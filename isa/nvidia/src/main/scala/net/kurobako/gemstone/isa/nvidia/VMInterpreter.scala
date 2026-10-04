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

  private class Frame(
      val id: Int,
      val kernel: Kernel,
      val state: VMBlockState,
      val arguments: Map[NonEmpty[String], Argument],
      val continuation: Option[Continuation]
  )
  private case class Continuation(
      caller: Frame,
      pc: Pc,
      parameters: Vec[(NonEmpty[String], NonEmpty[String])]
  )
  private case class Cohort(pc: Pc, lanes: VMLaneMask, frame: Frame)
  private case class ClusterWaiting(cohort: Cohort, phase: Long, arrival: Boolean)
  private case class BarrierReduction(
      destination: NonEmpty[String],
      predicate: NonEmpty[String],
      operation: PredicateOperation,
      pc: Pc
  )
  private case class Waiting(
      cohort: Cohort,
      key: WaitKey,
      reduction: Option[BarrierReduction] = None,
      resumeOnArrival: Boolean = false
  )

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
        (geometry.groupSize.x.toLong.widen[Long > 0] * geometry.groupCount.x.toLong.widen[Long > 0]) &&
        geometry.globalExtent.y ==
        (geometry.groupSize.y.toLong.widen[Long > 0] * geometry.groupCount.y.toLong.widen[Long > 0]) &&
        geometry.globalExtent.z ==
        (geometry.groupSize.z.toLong.widen[Long > 0] * geometry.groupCount.z.toLong.widen[Long > 0])
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
              geometry.localCoordinates(lane.raw) match {
                case Left(message) => failure = Some(message)
                case Right(local)  =>
                  geometry.globalCoordinatesLong(group, local) match {
                    case Left(message) => failure = Some(message)
                    case Right(global) =>
                      if (geometry.containsGlobal(global))
                        live = live.union(VMLaneMask.ofLane(lane.raw))
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
    val selectedCount                  = blockIndices.fold[Int >= 0](groupCount)(_.nonNegativeSize)
    def selectedBlock(index: Int): Int = blockIndices.fold(index)(_(index))
    val selectedTasks                  = selectedCount.toLong
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
  private def borrowState(
      laneCount: Int > 0,
      initialLive: VMLaneMask,
      registerChunkCount: VMRegisterFile.ChunkCount
  ): VMBlockState = {
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
    val state    = initialState.getOrElse(borrowState(laneCount, initialLive, kernel.registerChunkCount))
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
    kernel.parameterBank.foreach { bank =>
      val bytes = bank.initial.toArray
      bank.offsets.foreach { (name, offset) =>
        arguments.get(name).foreach(argument =>
          Array.copy(argument.bytes.toArray, 0, bytes, offset.raw, argument.bytes.size)
        )
      }
      state.bindParameterBuffer(ParameterBank.Symbol, bytes)
    }
    kernel.parameters.foreach { parameter =>
      arguments.get(parameter.name).foreach(argument =>
        state.bindParameterBuffer(parameter.name, argument.bytes.toArray)
      )
    }
    val rootFrame                     = Frame(0, kernel, state, arguments, None)
    var nextFrame                     = 1
    var live                          = state.live
    val ready                         = mutable.ArrayBuffer.empty[Cohort]
    val waiting                       = mutable.ArrayBuffer.empty[Waiting]
    lazy val clusterWaiting           = mutable.ArrayBuffer.empty[ClusterWaiting]
    lazy val clusterLanePhase         = Array.fill(state.laneCount.raw)(-1L)
    lazy val clusterWaitedPhase       = Array.fill(state.laneCount.raw)(-1L)
    var clusterPhase                  = 0L
    var clusterArrived                = VMLaneMask.Empty
    val barrierMasks                  = mutable.Map.empty[WaitKey, VMLaneMask]
    val barrierOperations             = mutable.Map.empty[WaitKey, Option[PredicateOperation]]
    val barrierCounts                 = mutable.Map.empty[WaitKey, Int]
    val warpBarrierMasks              = mutable.Map.empty[WaitKey, Long]
    val warpBarrierFrames             = mutable.Map.empty[WaitKey, Frame]
    def enqueue(cohort: Cohort): Unit = {
      val lanes = cohort.lanes.intersect(live)
      if (lanes.nonEmpty) {
        val existing = ready.indexWhere(value => value.pc == cohort.pc && value.frame == cohort.frame)
        if (existing < 0) ready += (if (lanes eq cohort.lanes) cohort else cohort.copy(lanes = lanes))
        else ready(existing) = ready(existing).copy(lanes = ready(existing).lanes.union(lanes))
      }
    }

    enqueue(Cohort(Pc.Entry, state.live, rootFrame))

    def enterCall(
        cohort: Cohort,
        call: DeviceCall,
        parameters: Vec[NonEmpty[String]],
        returnParameters: Vec[NonEmpty[String]]
    ): Either[VMFault, Unit] = {
      val function = call.function
      if (parameters.size != function.parameters.size)
        return Left(VMFault.InvalidState(
          s"device function ${function.name} expects ${function.parameters.size} parameters, found ${parameters.size}"
        ))
      if (returnParameters.size != function.returnParameters.size)
        return Left(VMFault.InvalidState(
          s"device function ${function.name} returns ${function.returnParameters.size} parameters, found ${returnParameters.size}"
        ))
      val caller      = cohort.frame
      val calledState = VMBlockState(
        laneCount = caller.state.laneCount,
        initialLive = call.lanes,
        shared = caller.state.shared,
        registerChunkCount = caller.state.registerChunkCount,
        memoryBarriers = caller.state.memoryBarriers
      )
      calledState.importParameterBuffers(caller.state.parameterBuffers)
      calledState.parameterAddresses ++= caller.state.parameterAddresses
      val parameterMapping = parameters.zip(function.parameters)
      call.lanes.foreachLane { lane =>
        parameterMapping.foreach { (source, target) =>
          calledState.parameters(lane)(target) = caller.state.parameters(lane).getOrElse(source, 0L)
          caller.state.parameterBytes(lane).get(source).foreach(bytes =>
            calledState.parameterBytes(lane)(target) = bytes.clone()
          )
        }
      }
      val calledKernel = caller.kernel.copy(
        name = function.name,
        parameters = Vec.empty,
        program = function.program,
        sharedSymbols = caller.kernel.sharedSymbols ++ function.sharedSymbols,
        parameterBank = None,
        localParameters = function.localParameters
      )
      val continuation = Continuation(caller, cohort.pc, returnParameters.zip(function.returnParameters))
      val frame        = Frame(nextFrame, calledKernel, calledState, Map.empty, Some(continuation))
      nextFrame = Math.incrementExact(nextFrame)
      enqueue(Cohort(Pc.Entry, call.lanes, frame))
      Right(())
    }

    def warpMemberMask(instruction: VMIR.Instruction): Option[Value] = instruction match {
      case VMIR.Instruction.ShuffleDown(_, _, _, _, _, memberMask, _)  => Some(memberMask)
      case VMIR.Instruction.ShuffleUp(_, _, _, _, _, memberMask, _)    => Some(memberMask)
      case VMIR.Instruction.ShuffleIndex(_, _, _, _, _, memberMask, _) => Some(memberMask)
      case VMIR.Instruction.ShuffleBfly(_, _, _, _, _, memberMask, _)  => Some(memberMask)
      case VMIR.Instruction.VoteBallot(_, _, memberMask)               => Some(memberMask)
      case VMIR.Instruction.Elect(_, _, memberMask)                    => Some(memberMask)
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
        val kernel    = cohort.frame.kernel
        val state     = cohort.frame.state
        val arguments = cohort.frame.arguments
        val context   = VMInstructionContext(
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
        var failure: Option[VMFault] = None
        val runnable                 = cohort.lanes.filterLanes { lane =>
          if (failure.nonEmpty) false
          else context.read(lane, memberMask) match {
            case Left(fault) =>
              failure = Some(fault)
              false
            case Right(mask) =>
              val expected = state.live.warpBits(lane.raw) & live.warpBits(lane.raw) & mask
              (expected & ~cohort.lanes.warpBits(lane.raw)) == 0L
          }
        }
        failure match {
          case Some(fault) => Left(fault)
          case None        => Right(runnable -> cohort.lanes.subtract(runnable))
        }
    }

    def advance(cohort: Cohort, lanes: VMLaneMask): Either[VMFault, Unit] =
      cohort.pc.next.toRight(VMFault.InvalidProgram("program counter overflow")).map(next =>
        enqueue(cohort.copy(pc = next, lanes = lanes))
      )

    def releaseCluster(): Either[VMFault, Unit] = {
      if (clusterPhase == 0L && clusterArrived.isEmpty) return Right(())
      if (clusterArrived.nonEmpty && clusterArrived.intersect(live) == live) {
        clusterPhase += 1L
        clusterArrived = VMLaneMask.Empty
      }
      val pending = clusterWaiting.toVector
      clusterWaiting.clear()
      var index = 0
      while (index < pending.size) {
        val entry        = pending(index)
        val participants = if (entry.arrival) clusterArrived
        else pending.filter(value => !value.arrival && value.phase == entry.phase)
          .foldLeft(VMLaneMask.Empty)((mask, value) => mask.union(value.cohort.lanes))
        val released = entry.cohort.lanes.intersect(live).filterLanes { lane =>
          val warp = live.warpBits(lane.raw)
          if (entry.arrival && entry.phase < clusterPhase) true
          else (entry.arrival || entry.phase < clusterPhase) && (participants.warpBits(lane.raw) & warp) == warp
        }
        if (!entry.arrival) released.foreachLane(lane => clusterWaitedPhase(lane.raw) = entry.phase)
        advance(entry.cohort, released) match {
          case Left(fault) => return Left(fault)
          case Right(())   => ()
        }
        val remaining = entry.cohort.lanes.intersect(live).subtract(released)
        if (remaining.nonEmpty) clusterWaiting += entry.copy(cohort = entry.cohort.copy(lanes = remaining))
        index += 1
      }
      Right(())
    }

    def releaseBarrier(key: WaitKey): Either[VMFault, Unit] = {
      val arrived                            = barrierMasks.getOrElse(key, VMLaneMask.Empty)
      val barrierState                       = warpBarrierFrames.get(key).fold(state)(_.state)
      def canResume(value: Waiting): Boolean =
        value.key == key && value.resumeOnArrival && value.cohort.lanes.filterLanes { lane =>
          val members = live.warpBits(lane.raw)
          (arrived.warpBits(lane.raw) & members) != members
        }.isEmpty
      val resumed = Vec.from(waiting.filter(canResume(_)))
      waiting.filterInPlace(value => !canResume(value))
      var resumedIndex = 0
      while (resumedIndex < resumed.size) {
        val cohort = resumed(resumedIndex).cohort
        advance(cohort, cohort.lanes) match {
          case Left(fault) => return Left(fault)
          case Right(())   => ()
        }
        resumedIndex += 1
      }
      val expected = key.scope match {
        case WaitScope.Workgroup => live
        case WaitScope.Warp      =>
          val warpBase   = key.id.toInt
          val memberMask = if (key.phase < 0) barrierState.convergence.getOrElse((warpBase, (-key.phase - 1).toInt), 0L)
          else warpBarrierMasks.getOrElse(key, -1L)
          barrierState.live.intersect(live).filterLanes { lane =>
            (lane.raw & ~31) == warpBase &&
            ((memberMask & 0xffffffffL) & (1L << (lane.raw & 31))) != 0L
          }
        case _ => live
      }
      val count        = barrierCounts.getOrElse(key, 0)
      val participants = if (key.scope == WaitScope.Workgroup && count > 0) {
        var selected = VMLaneMask.Empty
        var threads  = 0
        var warp     = 0
        while (warp < state.laneCount.raw && threads < count) {
          val members = live.warpBits(warp)
          if (arrived.warpBits(warp) != 0L && (arrived.warpBits(warp) & members) == members) {
            selected = selected.union(arrived.filterLanes(lane => (lane.raw & ~31) == warp))
            threads += 32
          }
          warp += 32
        }
        Option.when(threads == count)(selected)
      } else Option.when(arrived.intersect(expected) == expected)(arrived)
      participants match {
        case None               => ()
        case Some(participants) =>
          val released =
            Vec.from(waiting.filter(value => value.key == key && value.cohort.lanes.intersect(participants).nonEmpty))
          waiting.filterInPlace(value => value.key != key || value.cohort.lanes.intersect(participants).isEmpty)
          val remaining = arrived.subtract(participants)
          if (remaining.nonEmpty) barrierMasks(key) = remaining
          else {
            val _ = barrierMasks.remove(key)
            val _ = barrierCounts.remove(key)
            val _ = barrierOperations.remove(key)
          }
          val _ = warpBarrierMasks.remove(key)
          val _ = warpBarrierFrames.remove(key)
          // A completed convergence token is no longer live. In particular, a
          // legacy target can be reached along a path that skipped the next SSY
          // using this slot; it must not wait for the previous token's members.
          if (key.scope == WaitScope.Warp && key.phase < 0) {
            val _ = barrierState.convergence.remove((key.id.toInt, (-key.phase - 1).toInt))
          }
          released.view.flatMap(_.reduction).headOption match {
            case None        => ()
            case Some(first) =>
              var reduced: Either[VMFault, Boolean] = Right(first.operation == PredicateOperation.And)
              released.foreach { waiter =>
                waiter.reduction.foreach { reduction =>
                  waiter.cohort.lanes.foreachLane { lane =>
                    reduced = reduced.flatMap { current =>
                      waiter.cohort.frame.state.predicates(lane).get(reduction.predicate).toRight(
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
                }
              }
              reduced match {
                case Left(fault)  => return Left(fault)
                case Right(value) =>
                  released.foreach { waiter =>
                    waiter.reduction.foreach { reduction =>
                      waiter.cohort.lanes.foreachLane(lane =>
                        waiter.cohort.frame.state.predicates(lane)(reduction.destination) = value
                      )
                    }
                  }
              }
          }
          var releasedIndex = 0
          while (releasedIndex < released.size) {
            val releasedCohort = released(releasedIndex).cohort
            advance(releasedCohort, releasedCohort.lanes) match {
              case Left(fault) => return Left(fault)
              case Right(())   => ()
            }
            releasedIndex += 1
          }
      }
      Right(())
    }

    def arriveWorkgroup(
        cohort: Cohort,
        key: WaitKey,
        count: Int,
        reduction: Option[BarrierReduction],
        resumeOnArrival: Boolean = false
    ): Either[VMFault, Unit] = {
      val operation = reduction.map(_.operation)
      barrierCounts.get(key) match {
        case Some(previous) if previous != count =>
          Left(
            VMFault.InvalidState(s"barrier ${key.id} thread count changed from $previous to $count before completion")
          )
        case _ if barrierOperations.get(key).exists(_ != operation) =>
          Left(VMFault.InvalidState(s"barrier ${key.id} mixes incompatible reduction and synchronization operations"))
        case _ if barrierMasks.getOrElse(key, VMLaneMask.Empty).intersect(cohort.lanes).nonEmpty =>
          Left(VMFault.InvalidState(s"barrier ${key.id} receives a repeated arrival before completion"))
        case _ =>
          barrierCounts(key) = count
          barrierOperations(key) = operation
          waiting += Waiting(cohort, key, reduction, resumeOnArrival)
          barrierMasks(key) = barrierMasks.getOrElse(key, VMLaneMask.Empty).union(cohort.lanes)
          releaseBarrier(key)
      }
    }

    while (live.nonEmpty) {
      if (ready.isEmpty) {
        val waits = waiting.map(value =>
          s"${value.key.scope}:${value.key.id}@${value.key.phase}, ${value.cohort.frame.kernel.name} pc ${value.cohort.pc}"
        ).distinct.sorted
        val detail: NonEmpty[String] =
          if (waits.nonEmpty) s"${rootFrame.kernel.name}: ${waits.mkString(", ")}"
          else s"${rootFrame.kernel.name}: no runnable cohort"
        return Left(VMFault.Deadlock(detail))
      }
      var cohort    = ready.remove(0)
      val state     = cohort.frame.state
      val kernel    = cohort.frame.kernel
      val arguments = cohort.frame.arguments
      val effects   = state.effects
      state.live = state.live.intersect(live)
      val surviving = cohort.lanes.intersect(live)
      if (surviving ne cohort.lanes) cohort = cohort.copy(lanes = surviving)
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
          if (ready(readyIndex).pc == cohort.pc && ready(readyIndex).frame == cohort.frame) {
            cohort = cohort.copy(lanes = cohort.lanes.union(ready.remove(readyIndex).lanes))
          } else readyIndex += 1
        synchronizeWarpInstruction(executable, cohort) match {
          case Left(fault)                => return Left(fault)
          case Right((runnable, delayed)) =>
            if (delayed.nonEmpty) enqueue(cohort.copy(lanes = delayed))
            if (runnable ne cohort.lanes) cohort = cohort.copy(lanes = runnable)
        }
      }
      val activeSize = cohort.lanes.positiveSize
      if (activeSize.nonEmpty) {
        control.checkpoint(activeSize.get.widen[Int > 0]) match {
          case Left(fault) => return Left(VMFault.fromControl(fault))
          case Right(())   => ()
        }
        var active                            = cohort.lanes
        var predicateFailure: Option[VMFault] = None
        val predicateIndex                    =
          executable.predication.map(predicate => VMPredicateFile.index(predicate.name)).getOrElse(-1)
        executable.predication.foreach { predicate =>
          active = cohort.lanes.filterLanes { lane =>
            val value = if (predicateIndex >= 0) state.predicates(lane).getSlotRaw(predicateIndex)
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
            return Left(fault.inFunction(kernel.name))
          case Right(Step.Trap(fault)) => return Left(fault.inFunction(kernel.name))
          case Right(control)          =>
            effects.commit(state, memory, cohort.pc) match {
              case Left(fault) =>
                return Left(fault.inFunction(kernel.name))
              case Right(()) => ()
            }
            control match {
              case Step.Advance =>
                advance(cohort, cohort.lanes) match {
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
                enqueue(cohort.copy(pc = target, lanes = active))
                advance(cohort, skipped) match {
                  case Left(fault) => return Left(fault)
                  case Right(())   => ()
                }
              case Step.Split(taken, target, fallthrough) =>
                enqueue(cohort.copy(pc = target, lanes = taken))
                advance(cohort, fallthrough) match {
                  case Left(fault) => return Left(fault)
                  case Right(())   => ()
                }
              case Step.ClusterBarrier(arrival) =>
                advance(cohort, skipped) match {
                  case Left(fault) => return Left(fault)
                  case Right(())   => ()
                }
                var invalid = false
                active.foreachLane { lane =>
                  if (arrival) {
                    if (clusterLanePhase(lane.raw) > clusterWaitedPhase(lane.raw)) invalid = true
                    clusterLanePhase(lane.raw) = clusterPhase
                  } else if (clusterLanePhase(lane.raw) < 0) invalid = true
                }
                if (invalid) return Left(VMFault.InvalidState("cluster barrier requires one arrival before each wait"))
                if (arrival) clusterArrived = clusterArrived.union(active)
                if (arrival) clusterWaiting += ClusterWaiting(cohort.copy(lanes = active), clusterPhase, true)
                else {
                  val phases = mutable.Map.empty[Long, VMLaneMask.Builder]
                  active.foreachLane(lane =>
                    phases.getOrElseUpdate(clusterLanePhase(lane.raw), VMLaneMask.Builder()).add(lane)
                  )
                  phases.foreach { (phase, lanes) =>
                    clusterWaiting += ClusterWaiting(cohort.copy(lanes = lanes.result()), phase, false)
                  }
                }
                releaseCluster() match {
                  case Left(fault) => return Left(fault)
                  case Right(())   => ()
                }
              case Step.WorkgroupBarrier(arrivals, blocking) =>
                advance(cohort, skipped) match {
                  case Left(fault) => return Left(fault)
                  case Right(())   => ()
                }
                val reduction = executable.instruction match {
                  case VMIR.Instruction.BarrierReduce(destination, _, predicate, operation, _) =>
                    Some(BarrierReduction(destination, predicate, operation, cohort.pc))
                  case _ => None
                }
                var index = 0
                while (index < arrivals.size) {
                  val arrival = arrivals(index)
                  val key     = WaitKey(WaitScope.Workgroup, arrival.id.toLong, 0L)
                  arriveWorkgroup(cohort.copy(lanes = arrival.lanes), key, arrival.count, reduction, !blocking) match {
                    case Left(fault) => return Left(fault)
                    case Right(())   => ()
                  }
                  index += 1
                }
              case Step.Suspend(key) =>
                advance(cohort, skipped) match {
                  case Left(fault) => return Left(fault)
                  case Right(())   => ()
                }
                val reduction: Option[BarrierReduction] = None
                key.scope match {
                  case WaitScope.Warp =>
                    val lanesByToken                    = mutable.Map.empty[(Int, Long), VMLaneMask.Builder]
                    val tokenRegister: NonEmpty[String] = s"%B${(-key.phase - 1).toInt}"
                    active.foreachLane { lane =>
                      val phase = if (key.phase < 0) -state.registers(lane).get(tokenRegister) - 1L else key.phase
                      val token = (lane.raw & ~31, phase)
                      lanesByToken.getOrElseUpdate(token, VMLaneMask.Builder()).add(lane)
                    }
                    val warps     = Vec.from(lanesByToken).sortBy(_._1)
                    var warpIndex = 0
                    while (warpIndex < warps.size) {
                      val ((warpBase, phase), builder) = warps(warpIndex)
                      val warpLanes                    = builder.result()
                      val warpKey = WaitKey(WaitScope.Warp, warpBase.toLong, phase, cohort.frame.id)
                      waiting += Waiting(cohort.copy(lanes = warpLanes), warpKey, reduction)
                      warpBarrierMasks(warpKey) = key.id
                      warpBarrierFrames(warpKey) = cohort.frame
                      barrierMasks(warpKey) = barrierMasks.getOrElse(warpKey, VMLaneMask.Empty).union(warpLanes)
                      releaseBarrier(warpKey) match {
                        case Left(fault) => return Left(fault)
                        case Right(())   => ()
                      }
                      warpIndex += 1
                    }
                  case _ =>
                    arriveWorkgroup(cohort.copy(lanes = active), key, 0, reduction) match {
                      case Left(fault) => return Left(fault)
                      case Right(())   => ()
                    }
                }
              case Step.Call(calls, parameters, returnParameters, completed) =>
                advance(cohort, skipped.union(completed)) match {
                  case Left(fault) => return Left(fault)
                  case Right(())   => ()
                }
                var callIndex = 0
                while (callIndex < calls.size) {
                  enterCall(cohort, calls(callIndex), parameters, returnParameters) match {
                    case Left(fault) => return Left(fault)
                    case Right(())   => ()
                  }
                  callIndex += 1
                }
              case Step.Return(mask) =>
                state.live = state.live.subtract(mask)
                cohort.frame.continuation match {
                  case None               => live = live.subtract(mask)
                  case Some(continuation) =>
                    mask.foreachLane { lane =>
                      continuation.parameters.foreach { (target, source) =>
                        state.parameters(lane).get(source).foreach(value =>
                          continuation.caller.state.parameters(lane)(target) = value
                        )
                        state.parameterBytes(lane).get(source).foreach(bytes =>
                          continuation.caller.state.parameterBytes(lane)(target) = bytes.clone()
                        )
                      }
                    }
                    advance(Cohort(continuation.pc, mask, continuation.caller), mask) match {
                      case Left(fault) => return Left(fault)
                      case Right(())   => ()
                    }
                }
                advance(cohort, skipped) match {
                  case Left(fault) => return Left(fault)
                  case Right(())   => ()
                }
                releaseCluster() match {
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
              case Step.Exit(mask) =>
                state.live = state.live.subtract(mask)
                live = live.subtract(mask)
                advance(cohort, skipped) match {
                  case Left(fault) => return Left(fault)
                  case Right(())   => ()
                }
                releaseCluster() match {
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
    state.live = live
    Right(state)
  }

}
