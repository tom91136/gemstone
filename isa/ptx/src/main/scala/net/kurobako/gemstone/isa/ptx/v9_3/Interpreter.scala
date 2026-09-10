package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.Col

import net.kurobako.gemstone.execution.*
import net.kurobako.gemstone.memory.AddressMemory
import net.kurobako.gemstone.trace.Trace
import net.kurobako.gemstone.trace.TraceEvent

import scala.collection.mutable // scalafix:ok GemstoneRuntimeCollections.mutable-collection; architectural state
import scala.collection.mutable.ArrayBuffer // scalafix:ok GemstoneRuntimeCollections.mutable-collection; scheduler queue

@SuppressWarnings(
  Array(
    "scalafix:DisableSyntax.var",
    "scalafix:DisableSyntax.return",
    "scalafix:GemstoneRuntimeCollections.mutable-collection"
  )
)
object Interpreter {
  private final case class Cohort(pc: Pc, lanes: LaneMask)
  private final case class BarrierReduction(
      destination: String,
      predicate: String,
      operation: PredicateOperation,
      pc: Pc
  )
  private final case class Waiting(cohort: Cohort, key: WaitKey, reduction: Option[BarrierReduction] = None)

  private def containsAtomics(kernel: Kernel): Boolean = {
    def atomic(program: ExecutableProgram[PredicatedInstruction]): Boolean = program.instructions.exists {
      case PredicatedInstruction(_: ExecutableInstruction.AtomicAdd, _)      => true
      case PredicatedInstruction(_: ExecutableInstruction.AtomicMin, _)      => true
      case PredicatedInstruction(_: ExecutableInstruction.AtomicAddFloat, _) => true
      case _                                                                 => false
    }
    atomic(kernel.program) || kernel.functions.valuesIterator.exists(function => atomic(function.program))
  }

  def execute(
      kernel: Kernel,
      geometry: LaunchGeometry,
      arguments: Map[String, Argument],
      memory: AddressMemory,
      control: ExecutionControl = ExecutionControl.default,
      captureTrace: Boolean = true,
      initialParameterBuffers: Map[Long, Array[Byte]] = Map.empty,
      blockIndices: Option[Col[Int]] = None
  ): Either[ExecutionError, ExecutionResult] = {
    val laneCount = geometry.groupSize.volumeInt match {
      case Left(message) => return Left(ExecutionError(message))
      case Right(value)  => value
    }
    val groupCount = geometry.groupCount.volumeInt match {
      case Left(message) => return Left(ExecutionError(message))
      case Right(value)  => value
    }
    val allLanes = LaneMask.all(laneCount) match {
      case Left(fault)  => return Left(ExecutionError(fault.message))
      case Right(value) => value
    }
    var trace                                              = Trace.empty
    var step                                               = 0L
    def runBlock(block: Int): Either[ExecutionError, Unit] =
      if (block < 0 || block >= groupCount) Left(ExecutionError(s"selected PTX block is outside the launch: $block"))
      else {
        val group = geometry.groupCoordinates(block.toLong)
        val live  = allLanes.filterLanes(lane =>
          geometry.containsGlobal(geometry.globalCoordinatesLong(group, geometry.localCoordinates(lane)))
        )
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
          case Left(fault)                  => Left(ExecutionError(fault.message))
          case Right((nextTrace, nextStep)) =>
            if (captureTrace) {
              trace = nextTrace
              step = nextStep
            }
            Right(())
        }
      }
    val selectedCount                  = blockIndices.fold(groupCount)(_.size)
    def selectedBlock(index: Int): Int = blockIndices.fold(index)(_(index))
    val parallelBlocks                 = !captureTrace &&
      control.parallelism.shouldParallel(selectedCount.toLong, laneCount.toLong) &&
      !containsAtomics(kernel)
    if (parallelBlocks) {
      val result = control.parallelism.parallelFor(selectedCount.toLong) { index =>
        runBlock(selectedBlock(index.toInt)).left.map(error => ExecutionFailure.Failed(error.message))
      }
      result match {
        case Left(error) => return Left(ExecutionError(error.message))
        case Right(())   => ()
      }
    } else {
      var index = 0
      while (index < selectedCount) {
        runBlock(selectedBlock(index)) match {
          case Left(error) => return Left(error)
          case Right(_)    => ()
        }
        index += 1
      }
    }
    Right(ExecutionResult(trace))
  }

  private def executeBlock(
      kernel: Kernel,
      block: Int,
      group: Coordinates,
      geometry: LaunchGeometry,
      initialLive: LaneMask,
      arguments: Map[String, Argument],
      memory: AddressMemory,
      control: ExecutionControl,
      captureTrace: Boolean,
      initialTrace: Trace,
      initialStep: Long,
      initialParameterBuffers: Map[Long, Array[Byte]]
  ): Either[ExecutionFault, (Trace, Long)] = {
    import Semantics.*

    val laneCount = geometry.groupSize.volumeInt.fold(message => throw IllegalArgumentException(message), identity)
    val state     = BlockState(laneCount, initialLive)
    state.importParameterBuffers(initialParameterBuffers)
    kernel.parameters.foreach { parameter =>
      arguments.get(parameter.name).foreach(argument =>
        state.bindParameterBuffer(parameter.name, Col.toArray(argument.bytes))
      )
    }
    val ready                                  = ArrayBuffer.empty[Cohort]
    val waiting                                = ArrayBuffer.empty[Waiting]
    val barrierMasks                           = mutable.Map.empty[WaitKey, LaneMask]
    val warpBarrierMasks                       = mutable.Map.empty[WaitKey, Long]
    val effects                                = Effects[StateWrite]
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
          ExecutionContext(kernel, block, group, geometry, state, cohort.pc, cohort.lanes, arguments, memory, control)
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
                delayed = delayed.union(LaneMask.one(lane).fold(_ => LaneMask.Empty, identity))
              else runnable = runnable.union(LaneMask.one(lane).fold(_ => LaneMask.Empty, identity))
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
        val released = Col.from(waiting.filter(_.key == key))
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
        val waits = waiting.map(value => s"${value.key.scope}:${value.key.id}@${value.key.phase}").distinct.sorted
        return Left(ExecutionFault.Deadlock(waits.mkString(", ")))
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
        control.checkpoint(cohort.lanes.size) match {
          case Left(fault) => return Left(fault)
          case Right(())   => ()
        }
        var active                                   = cohort.lanes
        var predicateFailure: Option[ExecutionFault] = None
        val predicateIndex = executable.predication.map(predicate => PredicateFile.index(predicate.name)).getOrElse(-1)
        executable.predication.foreach { predicate =>
          active = cohort.lanes.filterLanes { lane =>
            val value = if (predicateIndex >= 0) state.predicates(lane).getSlot(predicateIndex)
            else
              state.predicates(lane).get(predicate.name)
            value match {
              case Some(predicateValue) => predicateValue != predicate.negated
              case None                 =>
                predicateFailure = Some(ExecutionFault.UndefinedRegister(predicate.name, cohort.pc, lane))
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
              .append(TraceEvent.SchedulerDecision(traceStep, block, lane))
              .append(TraceEvent.Instruction(
                traceStep + 1L,
                kernel.name,
                block,
                lane,
                cohort.pc.value,
                executable.opcode
              ))
            traceStep += 2L
          }

        val context =
          ExecutionContext(kernel, block, group, geometry, state, cohort.pc, active, arguments, memory, control)

        effects.clear()
        val outcome =
          if (active.isEmpty) Right(Step.Advance)
          else executable.instruction.execute(context, effects)
        outcome match {
          case Left(fault) =>
            return Left(fault)
          case Right(Step.Trap(fault)) => return Left(fault)
          case Right(control)          =>
            val writes = if (captureTrace) effects.snapshot else Col.empty[StateWrite]
            effects.commit(
              StateEffects.validateWrite(memory, cohort.pc),
              StateEffects.applyWrite(state, memory)
            ) match {
              case Left(fault) =>
                return Left(fault)
              case Right(()) =>
                if (captureTrace)
                  writes.foreach {
                    case StateWrite.MemoryVector(lane, address, width, values) =>
                      values.zipWithIndex.foreach { case (value, index) =>
                        trace = trace.append(TraceEvent.OutputWrite(
                          traceStep,
                          block,
                          lane,
                          address.offset(index.toLong * (width / 8)).value,
                          value.toInt
                        ))
                        traceStep += 1L
                      }
                    case StateWrite.Memory(lane, address, _, value) =>
                      trace = trace.append(TraceEvent.OutputWrite(traceStep, block, lane, address.value, value.toInt))
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
                        LaneMask.one(lane).fold(fault => throw IllegalStateException(fault.message), identity)
                      )
                    }
                    val warps     = Col.from(lanesByWarp).sortBy(_._1)
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
                val keys     = Col.from(barrierMasks.keys)
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
