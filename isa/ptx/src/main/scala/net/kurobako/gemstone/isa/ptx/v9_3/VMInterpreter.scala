package net.kurobako.gemstone.isa.ptx.v9_3

import Program.*

import VirtualMachine.*

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import net.kurobako.gemstone.Vec

import net.kurobako.gemstone.execution.*
import net.kurobako.gemstone.memory.AddressMemory

import scala.collection.mutable

private[v9_3] object VMInterpreter {

  private final case class Cohort(pc: Pc, lanes: LaneMask)
  private final case class BarrierReduction(
      destination: RegisterName,
      predicate: RegisterName,
      operation: PredicateOperation,
      pc: Pc
  )
  private final case class Waiting(cohort: Cohort, key: WaitKey, reduction: Option[BarrierReduction] = None)

  // The dispatch loop carries trace state and exits immediately on execution faults.
  @SuppressWarnings(
    Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.return", "scalafix:GemstoneNoAssume")
  )
  def execute(
      kernel: Kernel,
      geometry: LaunchGeometry,
      arguments: Map[ParameterName, Argument],
      memory: AddressMemory,
      control: ExecutionControl = ExecutionControl.default,
      captureTrace: Boolean = true,
      initialParameterBuffers: Map[Long, Array[Byte]] = Map.empty,
      blockIndices: Option[Vec[Int]] = None
  ): Either[Error, Result] = {
    val laneCount = geometry.groupSize.volumeInt match {
      case Left(message) => return Left(Error(message))
      case Right(value)  => value
    }
    val groupCount = geometry.groupCount.volumeInt match {
      case Left(message) => return Left(Error(message))
      case Right(value)  => value
    }
    val allLanes = LaneMask.all(laneCount) match {
      case Left(fault)  => return Left(Error(fault.message))
      case Right(value) => value
    }
    var trace                                                  = Trace.empty
    var step                                                   = 0L
    def liveLanes(group: Coordinates): Either[Error, LaneMask] = {
      var live                    = LaneMask.Empty
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
                    live = live.union(LaneMask.one(lane).fold(_ => LaneMask.Empty, value => value))
              }
          }
      }
      failure match {
        case Some(message) =>
          Left(Error(s"PTX geometry failure: $message"))
        case None => Right(live)
      }
    }
    def runBlock(block: Int): Either[Error, Unit] =
      if (block < 0 || block >= groupCount)
        Left(Error(s"selected PTX block is outside the launch: $block"))
      else
        geometry.groupCoordinates(block.toLong) match {
          case Left(message) =>
            Left(Error(s"PTX geometry failure: $message"))
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
                  captureTrace,
                  trace,
                  step,
                  initialParameterBuffers
                ) match {
                  case Left(fault)                  => Left(Error(fault.message))
                  case Right((nextTrace, nextStep)) =>
                    if (captureTrace) {
                      trace = nextTrace
                      step = nextStep
                    }
                    Right(())
                }
            }
        }
    val selectedCount                  = blockIndices.fold(groupCount)(_.size)
    def selectedBlock(index: Int): Int = blockIndices.fold(index)(_(index))
    val selectedTasks                  = selectedCount.toLong.assume[Positive0]
    val workItems                      = laneCount.toLong.assume[Positive0]
    val parallelBlocks                 = !captureTrace && control.parallelism.shouldParallel(selectedTasks, workItems)
    control.parallelism.forEach(selectedTasks, parallel = parallelBlocks) { index =>
      runBlock(selectedBlock(index.toInt)).left.map(error =>
        ExecutionFailure.Failed(error.message)
      )
    } match {
      case Left(error) => return Left(Error(error.message))
      case Right(())   => ()
    }
    Right(Result(trace))
  }

  // Cohort, barrier and trace state model the SIMT machine directly; early exits preserve the first fault.
  @SuppressWarnings(
    Array(
      "scalafix:DisableSyntax.var",
      "scalafix:DisableSyntax.return",
      "scalafix:GemstoneNoAssume"
    )
  )
  private def executeBlock(
      kernel: Kernel,
      block: Int,
      group: Coordinates,
      geometry: LaunchGeometry,
      initialLive: LaneMask,
      arguments: Map[ParameterName, Argument],
      memory: AddressMemory,
      control: ExecutionControl,
      captureTrace: Boolean,
      initialTrace: Trace,
      initialStep: Long,
      initialParameterBuffers: Map[Long, Array[Byte]]
  ): Either[ExecutionFault, (Trace, Long)] = {
    import VMSemantics.*

    val laneCount =
      geometry.groupSize.volumeInt.fold(message => throw IllegalArgumentException(message), value => value)
    val state = VMState(laneCount, initialLive)
    state.importParameterBuffers(initialParameterBuffers)
    kernel.parameters.foreach { parameter =>
      arguments.get(parameter.name).foreach(argument =>
        state.bindParameterBuffer(parameter.name, Vec.toArray(argument.bytes))
      )
    }
    val ready                                  = mutable.ArrayBuffer.empty[Cohort]
    val waiting                                = mutable.ArrayBuffer.empty[Waiting]
    val barrierMasks                           = mutable.Map.empty[WaitKey, LaneMask]
    val warpBarrierMasks                       = mutable.Map.empty[WaitKey, Long]
    val effects                                = Effects[VMStateWrite]
    var trace                                  = initialTrace
    var traceStep                              = initialStep
    def enqueue(pc: Pc, lanes: LaneMask): Unit =
      if (lanes.nonEmpty) {
        val existing = ready.indexWhere(_.pc == pc)
        if (existing < 0) ready += Cohort(pc, lanes)
        else ready(existing) = Cohort(pc, ready(existing).lanes.union(lanes))
      }

    enqueue(Pc.Entry, state.live)

    def warpMemberMask(instruction: ExecutableInstruction): Option[Value] = instruction match {
      case ExecutableInstruction.ShuffleDown(_, _, _, _, _, memberMask, _)  => Some(memberMask)
      case ExecutableInstruction.ShuffleUp(_, _, _, _, _, memberMask, _)    => Some(memberMask)
      case ExecutableInstruction.ShuffleIndex(_, _, _, _, _, memberMask, _) => Some(memberMask)
      case ExecutableInstruction.ShuffleBfly(_, _, _, _, _, memberMask, _)  => Some(memberMask)
      case ExecutableInstruction.VoteBallot(_, _, memberMask)               => Some(memberMask)
      case ExecutableInstruction.VotePredicate(_, _, memberMask, _)         => Some(memberMask)
      case ExecutableInstruction.WarpReduce(_, _, memberMask, _, _)         => Some(memberMask)
      case _                                                                => None
    }

    def synchronizeWarpInstruction(
        executable: PredicatedInstruction,
        cohort: Cohort
    ): Either[ExecutionFault, (LaneMask, LaneMask)] = warpMemberMask(executable.instruction) match {
      case None             => Right(cohort.lanes -> LaneMask.Empty)
      case Some(memberMask) =>
        val context =
          VMExecutionContext(kernel, block, group, geometry, state, cohort.pc, cohort.lanes, arguments, memory, control)
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
            var runnable = LaneMask.Empty
            var delayed  = LaneMask.Empty
            masks.foreach { case (lane, maskValue) =>
              val warpBase = lane & ~31
              val present  = cohort.lanes.filterLanes(candidate => (candidate & ~31) == warpBase)
              val expected = state.live.filterLanes { candidate =>
                (candidate & ~31) == warpBase &&
                ((maskValue & 0xffffffffL) & (1L << (candidate & 31))) != 0L
              }
              if (expected.subtract(present).nonEmpty)
                delayed = delayed.union(LaneMask.one(lane).fold(_ => LaneMask.Empty, value => value))
              else runnable = runnable.union(LaneMask.one(lane).fold(_ => LaneMask.Empty, value => value))
            }
            Right(runnable -> delayed)
        }
    }
    def advance(pc: Pc, lanes: LaneMask): Either[ExecutionFault, Unit] = pc.next.map(next => enqueue(next, lanes))

    def releaseBarrier(key: WaitKey): Either[ExecutionFault, Unit] = {
      val arrived  = barrierMasks.getOrElse(key, LaneMask.Empty)
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
        released.iterator.flatMap(_.reduction).take(1).foreach { reduction =>
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
        case Left(fault)  => return Left(fault)
        case Right(value) => value
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
        control.checkpoint(cohort.lanes.size.assume[Positive]) match {
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
        val skipped = if (executable.predication.isEmpty) LaneMask.Empty else cohort.lanes.subtract(active)
        if (captureTrace)
          cohort.lanes.foreachLane { lane =>
            trace = trace
              .append(Trace.Event.SchedulerDecision(traceStep, block, lane))
              .append(Trace.Event.Instruction(
                traceStep + 1L,
                kernel.name,
                block,
                lane,
                cohort.pc,
                executable.opcode
              ))
            traceStep += 2L
          }

        val context =
          VMExecutionContext(kernel, block, group, geometry, state, cohort.pc, active, arguments, memory, control)

        effects.clear()
        val outcome =
          if (active.isEmpty) Right(Step.Advance)
          else executable.instruction.execute(context, effects)
        outcome match {
          case Left(fault) =>
            return Left(fault)
          case Right(Step.Trap(fault)) => return Left(fault)
          case Right(control)          =>
            val writes = if (captureTrace) effects.snapshot else Vec.empty[VMStateWrite]
            effects.commit(
              VMStateEffects.validateWrite(memory, cohort.pc),
              VMStateEffects.applyWrite(state, memory)
            ) match {
              case Left(fault) =>
                return Left(fault)
              case Right(()) =>
                if (captureTrace)
                  writes.foreach {
                    case VMStateWrite.MemoryVector(lane, address, width, values) =>
                      values.zipWithIndex.foreach { case (value, index) =>
                        trace = trace.append(Trace.Event.OutputWrite(
                          traceStep,
                          block,
                          lane,
                          address.offset(index.toLong * (width.value / 8)),
                          value.toInt
                        ))
                        traceStep += 1L
                      }
                    case VMStateWrite.Memory(lane, address, _, value) =>
                      trace = trace.append(
                        Trace.Event.OutputWrite(traceStep, block, lane, address, value.toInt)
                      )
                      traceStep += 1L
                    case _ => ()
                  }
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
                  case ExecutableInstruction.BarrierReduce(destination, _, predicate, operation) =>
                    Some(BarrierReduction(destination, predicate, operation, cohort.pc))
                  case _ => None
                }
                key.scope match {
                  case WaitScope.Warp =>
                    val lanesByWarp = mutable.Map.empty[Int, LaneMask]
                    active.foreachLane { lane =>
                      val warpBase = lane & ~31
                      lanesByWarp(warpBase) = lanesByWarp.getOrElse(warpBase, LaneMask.Empty).union(
                        LaneMask.one(lane).fold(fault => throw IllegalStateException(fault.message), value => value)
                      )
                    }
                    val warps     = Vec.from(lanesByWarp).sortBy(_._1)
                    var warpIndex = 0
                    while (warpIndex < warps.size) {
                      val (warpBase, warpLanes) = warps(warpIndex)
                      val warpKey               = WaitKey(WaitScope.Warp, warpBase.toLong, key.phase)
                      waiting += Waiting(cohort.copy(lanes = warpLanes), warpKey, reduction)
                      warpBarrierMasks(warpKey) = key.id
                      barrierMasks(warpKey) = barrierMasks.getOrElse(warpKey, LaneMask.Empty).union(warpLanes)
                      releaseBarrier(warpKey) match {
                        case Left(fault) => return Left(fault)
                        case Right(())   => ()
                      }
                      warpIndex += 1
                    }
                  case _ =>
                    waiting += Waiting(cohort.copy(lanes = active), key, reduction)
                    barrierMasks(key) = barrierMasks.getOrElse(key, LaneMask.Empty).union(active)
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
    Right(trace -> traceStep)
  }

}
