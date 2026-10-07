package net.kurobako.gemstone.isa.nvidia

import VMArithmetic.*
import VMExecution.*
import VMIR.*
import cats.syntax.all.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Bits.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.string.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
import scala.collection.mutable

object VMSemantics {

  def execute(instruction: Instruction, context: VMContext, effects: VMWriteBuffer): Either[Fault, Step] =
    if (executeInteger(instruction, context) || executeFloatAndPredicate(instruction, context)) Right(Step.Advance)
    else executeStaged(instruction, context, effects)

  // Preserve the first lane fault without evaluating later lanes after failure.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private inline def forEveryLane(context: VMContext)(inline function: WorkgroupLane => Either[Fault, Unit])
      : Either[Fault, Unit] = {
    var failure: Option[Fault] = None
    context.active.foreachLane { lane =>
      if (failure.isEmpty) function(lane) match {
        case Left(fault) => failure = Some(fault)
        case Right(())   => ()
      }
    }
    failure.toLeft(())
  }

  private inline def advance(result: Either[Fault, Unit]): Either[Fault, Step] = result.map(_ => Step.Advance)

  /** Routes one access to the parameter buffers, the workgroup's shared or thread-local storage, or device memory.
    * Generic addresses select a window by aperture; explicit global accesses still see parameter buffers. Explicit
    * local addresses are relative to the executing frame, generic ones to the thread's whole local window.
    */
  private inline def route[A](context: VMContext, access: MemoryAccess, address: Long, size: Int >= 0)(
      inline buffer: (Array[Byte], Int) => A,
      inline shared: A,
      inline local: Long => A,
      inline global: A
  ): A = access match {
    case MemoryAccess.Shared => shared
    case MemoryAccess.Local  => local(context.frame.localBase + address)
    case _                   =>
      context.workgroup.buffer(address, size) match {
        case Some((bytes, offset))                                                => buffer(bytes, offset)
        case None if access.generic && VMAddressSpace.isShared(address, size.raw) => shared
        case None if access.generic && VMAddressSpace.isLocal(address)            => local(address)
        case None                                                                 => global
      }
  }

  private def load(
      context: VMContext,
      lane: WorkgroupLane,
      access: MemoryAccess,
      address: Long,
      width: ScalarBitWidth
  ): Either[Fault, Long] = {
    val size = width.toScalarByteWidth
    route(context, access, address, size.widen)(
      (bytes, offset) => Right(LittleEndian.decode(bytes, offset, size.widen[Int in 0 ~ 8])),
      Right(context.workgroup.readShared(address, width)),
      local => Right(context.workgroup.readLocal(lane, local, width)),
      (if (access.systemScoped) context.memory.readSystemValue(GuestAddress(address), size)
       else context.memory.readValue(GuestAddress(address), size)).leftMap(VMWriteBuffer.located(_, context.pc, lane))
    )
  }

  private def stageStore(
      context: VMContext,
      effects: VMWriteBuffer,
      lane: WorkgroupLane,
      access: MemoryAccess,
      address: Long,
      width: ScalarBitWidth,
      value: Long
  ): Unit = route(context, access, address, width.toScalarByteWidth.widen)(
    (_, _) => effects.stage(VMWrite.ParameterBuffer(address, width, value)),
    effects.stage(VMWrite.Shared(address, width, value)),
    local => effects.stage(VMWrite.Local(lane, local, width, value)),
    if (access.systemScoped) effects.stage(VMWrite.Memory(lane, GuestAddress(address), width, value, true))
    else effects.stageMemory(lane, GuestAddress(address), width, value)
  )

  /** Stores issued by the emulator on a lane's behalf, such as device-runtime out-parameters. */
  def storeGeneric(context: VMContext, lane: WorkgroupLane, address: Long, width: ScalarBitWidth, value: Long)
      : Either[Fault, Unit] =
    route(context, MemoryAccess.Generic(false), address, width.toScalarByteWidth.widen)(
      (bytes, offset) => Right(LittleEndian.write(value, bytes, offset, width.toScalarByteWidth)),
      Right(context.workgroup.writeShared(address, width, value)),
      local => Right(context.workgroup.writeLocal(lane, local, width, value)),
      context.memory.writeValue(GuestAddress(address), value, width.toScalarByteWidth)
    )

  private def prepareReads(context: VMContext, access: MemoryAccess, address: Value, size: Int >= 0)
      : Either[Fault, Unit] = access match {
    case MemoryAccess.Shared | MemoryAccess.Local => Right(())
    case _                                        =>
      val ranges = Vec.newBuilder[(GuestAddress, Int >= 0)]
      context.active.foreachLane { lane =>
        val raw = context.read(lane, address)
        route(context, access, raw, size)(
          (_, _) => (),
          (),
          _ => (), {
            val _ = ranges += ((GuestAddress(raw), size))
            ()
          }
        )
      }
      (if (access.systemScoped) context.memory.prepareSystemReads(ranges.result())
       else context.memory.prepareReads(ranges.result()))
        .leftMap { fault =>
          // A batch fault names no lane, so attribute it to the first lane whose device range is unmapped.
          val faulting = context.active.filterLanes { lane =>
            val raw = context.read(lane, address)
            route(context, access, raw, size)(
              (_, _) => false,
              false,
              _ => false,
              context.memory.locate(GuestAddress(raw), size).isLeft
            )
          }.headOption
          VMWriteBuffer.located(fault, context.pc, faulting.orElse(context.active.headOption).getOrElse(0))
        }
  }

  /** CUB's scan descriptors rely on coherent aligned status/value transfers, including 16-byte double descriptors. This
    * target behaviour is stronger than PTX's minimum vector ordering; it is not a general 128-bit atomic guarantee.
    */
  private def coherentVector(context: VMContext, access: MemoryAccess, address: Long, size: Int >= 0): Boolean =
    (access == MemoryAccess.Global(false) || access == MemoryAccess.Generic(false)) &&
      size.raw > 0 && size.raw <= 16 &&
      (size.raw & (size.raw - 1)) == 0 &&
      (address & (size.raw - 1).toLong) == 0L &&
      !VMAddressSpace.isShared(address) && !VMAddressSpace.isLocal(address) &&
      context.workgroup.buffer(address, size).isEmpty

  private def loadVector(
      context: VMContext,
      effects: VMWriteBuffer,
      destinations: Vec[Register],
      address: Value,
      width: ScalarBitWidth,
      signed: Boolean,
      access: MemoryAccess
  ): Either[Fault, Unit] = {
    val elementBytes = width.toScalarByteWidth
    val totalBytes   = destinations.nonNegativeSize * elementBytes
    prepareReads(context, access, address, totalBytes).flatMap(_ =>
      forEveryLane(context) { lane =>
        val base = context.read(lane, address)
        if (coherentVector(context, access, base, totalBytes)) {
          val loaded =
            if (totalBytes.raw <= 8)
              context.memory.readValue(GuestAddress(base), totalBytes.raw.unsafe[Int in 1 ~ 8]).map(packed =>
                destinations.indices.map(index => packed >>> (index * width.raw))
              )
            else context.memory.read(GuestAddress(base), totalBytes).map(bytes =>
              destinations.indices.map(index => LittleEndian.decode(bytes, index * elementBytes.raw, elementBytes))
            )
          loaded.leftMap(VMWriteBuffer.located(_, context.pc, lane)).map(_.zip(destinations).foreach {
            (value, destination) => effects.stageRegister(lane, destination, loadBits(value, width, signed))
          })
        } else destinations.zipWithIndex.traverse_ { (destination, index) =>
          load(context, lane, access, base + index.toLong * elementBytes.raw, width).map(value =>
            effects.stageRegister(lane, destination, loadBits(value, width, signed))
          )
        }
      }
    )
  }

  private def storeVector(
      context: VMContext,
      effects: VMWriteBuffer,
      address: Value,
      sources: Vec[Value],
      width: ScalarBitWidth,
      access: MemoryAccess
  ): Unit = {
    val elementBytes = width.toScalarByteWidth
    val totalBytes   = sources.nonNegativeSize * elementBytes
    context.active.foreachLane { lane =>
      val base   = context.read(lane, address)
      val values = sources.map(context.read(lane, _))
      if (!coherentVector(context, access, base, totalBytes))
        values.zipWithIndex.foreach((value, index) =>
          stageStore(context, effects, lane, access, base + index.toLong * elementBytes.raw, width, value)
        )
      else if (totalBytes.raw <= 8) {
        val packed = values.zipWithIndex.foldLeft(0L) { case (bits, (value, index)) =>
          bits | (truncate(value, width) << (index * width.raw))
        }
        effects.stage(
          VMWrite.Memory(lane, GuestAddress(base), (totalBytes.raw * 8).unsafe[ScalarBitWidth], packed, false)
        )
      } else
        effects.stage(VMWrite.MemoryBytes(
          lane,
          GuestAddress(base),
          values.flatMap(LittleEndian.toVec(_, elementBytes))
        ))
    }
  }

  private def atomicTargetsShared(space: MemorySpace, address: Long, width: ScalarBitWidth): Boolean =
    space == MemorySpace.Shared ||
      (space == MemorySpace.Generic && VMAddressSpace.isShared(address, width.toScalarByteWidth.raw))

  private def sharedBarrierAddress(address: Long): Either[Fault, Long] =
    if ((address & 7L) != 0 || !VMAddressSpace.isShared(address, 8))
      Left(Fault.Backend(s"invalid shared mbarrier address: $address"))
    else Right(VMAddressSpace.sharedAddress(address))

  private def mbarrier(context: VMContext, address: Long): Either[Fault, MBarrier] =
    sharedBarrierAddress(address).flatMap(canonical =>
      context.workgroup.mbarriers.get(canonical).toRight(
        Fault.Backend(s"uninitialised mbarrier at shared address $address")
      )
    )

  private def bulkTransfer(kind: String, destination: Long, source: Long, size: Long): Either[Fault, MBarrier.Count] =
    if ((destination & 15L) == 0 && (source & 15L) == 0 && (size & 15L) == 0)
      MBarrier.transactionBytes(size)
    else Left(Fault.Backend(s"invalid $kind: destination=$destination, source=$source, size=$size"))

  private def readDeviceBytes(context: VMContext, lane: WorkgroupLane, address: Long, size: Long)
      : Either[Fault, Vec[Byte]] =
    if (size == 0L) Right(Vec.empty)
    else size.toInt.refine[Int >= 0].leftMap(Fault.Backend(_)).flatMap(count =>
      context.memory.read(GuestAddress(address), count).leftMap(VMWriteBuffer.located(_, context.pc, lane))
    )

  private def workgroupBarrier(
      context: VMContext,
      id: Value,
      count: Option[Value],
      blocking: Boolean,
      reduction: Option[BarrierReduction]
  ): Either[Fault, Step] = {
    val arrivals = mutable.LinkedHashMap.empty[(Int, Int, Int), VMLaneMask.Builder]
    forEveryLane(context) { lane =>
      val barrier  = context.read(lane, id) & 0xffffffffL
      val expected = count.fold(0L)(context.read(lane, _) & 0xffffffffL)
      for {
        _ <- Either.cond(barrier < 16L, (), Fault.Backend(s"barrier id $barrier is outside 0..15"))
        _ <- Either.cond(
          expected <= VMLaneMask.MaximumLanes && expected % 32L == 0L && (blocking || expected > 0L),
          (),
          Fault.Backend(s"barrier thread count $expected must be a multiple of 32 within the workgroup limit")
        )
      } yield arrivals.getOrElseUpdate((barrier.toInt, expected.toInt, lane.raw & ~31), VMLaneMask.Builder()).add(lane)
    }.map(_ =>
      Step.WorkgroupBarrier(
        Vec.from(arrivals).map { case ((barrier, expected, _), builder) =>
          WorkgroupArrival(barrier.unsafe[BarrierId], expected.unsafe[BarrierCount], builder.result())
        },
        blocking,
        reduction
      )
    )
  }

  /** Reads every participating lane's predicate in `lane`'s warp, in lane order. */
  private def warpPredicates(
      context: VMContext,
      lane: WorkgroupLane,
      mask: Long,
      predicate: Predicate,
      negated: Boolean
  ): Either[Fault, Vec[(Int, Boolean)]] = {
    val warpBase = lane.raw & ~31
    (0 until 32).toVec.filter { laneInWarp =>
      val candidate = warpBase + laneInWarp
      (mask & (1L << laneInWarp)) != 0L && context.active.contains(candidate) &&
      candidate < context.workgroup.laneCount.raw
    }.traverse(laneInWarp =>
      context.predicate(WorkgroupLane.fromIndex(warpBase + laneInWarp), predicate).map(value =>
        laneInWarp -> (value != negated)
      )
    )
  }

  private def executeStaged(instruction: Instruction, context: VMContext, effects: VMWriteBuffer)
      : Either[Fault, Step] = instruction match {
    case _: (Instruction.Load | Instruction.LoadVector | Instruction.Store | Instruction.StoreVector |
          Instruction.LoadParameter | Instruction.StoreParameter | Instruction.Atomic |
          Instruction.AtomicCompareExchange) =>
      executeMemory(instruction, context, effects)
    case _: (Instruction.Branch | Instruction.BarrierSync | Instruction.BarrierArrive | Instruction.BarrierReduce |
          Instruction.ReturnStackPush | Instruction.ReturnStackPop | Instruction.Convergence | Instruction.WarpBarrier |
          Instruction.Call | Instruction.CallBuiltin) =>
      executeControl(instruction, context, effects)
    case Instruction.Fence | Instruction.NoOp | Instruction.Trap | Instruction.Return | Instruction.Exit =>
      executeControl(instruction, context, effects)
    case _: (Instruction.WriteConditionFlag | Instruction.ReadConditionFlag | Instruction.ReadConditionPredicate |
          Instruction.InvalidateConditionFlag | Instruction.IntegerAddThree) =>
      executeConditionCodes(instruction, context, effects)
    case Instruction.InvalidateAddThreeCarry => executeConditionCodes(instruction, context, effects)
    case _: (Instruction.UniformMove | Instruction.Tensor | Instruction.Shuffle | Instruction.Elect |
          Instruction.VoteBallot | Instruction.VotePredicate | Instruction.WarpReduce) =>
      executeWarp(instruction, context, effects)
    case _: (Instruction.MBarrierLoad | Instruction.MBarrierInit | Instruction.MBarrierInvalidate |
          Instruction.MBarrierArrive | Instruction.MBarrierTest) =>
      executeMBarrier(instruction, context, effects)
    case _: (Instruction.ClusterBarrier | Instruction.ClusterTryCancel | Instruction.ClusterCancelStatus |
          Instruction.ClusterCancelCta) =>
      executeCluster(instruction, context, effects)
    case _: (Instruction.AsyncBulkStore | Instruction.AsyncBulkCopy | Instruction.AsyncCopy) =>
      executeAsyncCopy(instruction, context, effects)
    case other => Left(Fault.Backend(s"unhandled ${context.kernel.isaName} instruction $other"))
  }

  private def executeMemory(instruction: Instruction, context: VMContext, effects: VMWriteBuffer): Either[Fault, Step] =
    (instruction: @unchecked) match {
      case Instruction.Load(destination, address, width, signed, access) =>
        advance(prepareReads(context, access, address, width.toScalarByteWidth.widen).flatMap(_ =>
          forEveryLane(context)(lane =>
            load(context, lane, access, context.read(lane, address), width).map(value =>
              effects.stageRegister(lane, destination, loadBits(value, width, signed))
            )
          )
        ))
      case Instruction.LoadVector(destinations, address, width, signed, access) =>
        advance(loadVector(context, effects, destinations, address, width, signed, access))
      case Instruction.Store(address, source, width, access) =>
        context.active.foreachLane(lane =>
          stageStore(context, effects, lane, access, context.read(lane, address), width, context.read(lane, source))
        )
        Right(Step.Advance)
      case Instruction.StoreVector(address, sources, width, access) =>
        storeVector(context, effects, address, sources, width, access)
        Right(Step.Advance)
      case Instruction.LoadParameter(destinations, parameter, offset, elementWidth, signed) =>
        val elementBytes = elementWidth.toScalarByteWidth
        context.active.foreachLane { lane =>
          val storage = context.lane(lane)
          destinations.zipWithIndex.foreach { (destination, index) =>
            val loaded = storage.readParameter(
              parameter,
              (offset.raw + index * elementBytes.raw).unsafe[Int >= 0],
              elementBytes.widen[Int in 0 ~ 8]
            )
            effects.stageRegister(lane, destination, loadBits(loaded, elementWidth, signed))
          }
        }
        Right(Step.Advance)
      case Instruction.StoreParameter(parameter, offset, source, elementWidth) =>
        context.active.foreachLane(lane =>
          effects.stage(
            VMWrite.Parameter(lane, parameter, offset, elementWidth, truncate(context.read(lane, source), elementWidth))
          )
        )
        Right(Step.Advance)
      case Instruction.Atomic(destination, address, source, width, space, operation) =>
        context.active.foreachLane { lane =>
          val raw = context.read(lane, address)
          effects.stage(VMWrite.Atomic(
            lane,
            destination,
            raw,
            width,
            atomicTargetsShared(space, raw, width),
            AtomicUpdate.Operation(operation, context.read(lane, source))
          ))
        }
        Right(Step.Advance)
      case Instruction.AtomicCompareExchange(destination, address, comparator, source, width, space) =>
        context.active.foreachLane { lane =>
          val raw = context.read(lane, address)
          effects.stage(VMWrite.Atomic(
            lane,
            Some(destination),
            raw,
            width,
            atomicTargetsShared(space, raw, width),
            AtomicUpdate.CompareExchange(context.read(lane, comparator), context.read(lane, source))
          ))
        }
        Right(Step.Advance)
    }

  private def executeControl(
      instruction: Instruction,
      context: VMContext,
      effects: VMWriteBuffer
  ): Either[Fault, Step] =
    (instruction: @unchecked) match {
      case Instruction.Branch(target)           => Right(Step.Jump(target))
      case Instruction.BarrierSync(id, count)   => workgroupBarrier(context, id, count, blocking = true, None)
      case Instruction.BarrierArrive(id, count) => workgroupBarrier(context, id, Some(count), blocking = false, None)
      case Instruction.BarrierReduce(destination, id, predicate, operation, count, negated) =>
        val reduction = BarrierReduction(destination, predicate, operation, negated)
        workgroupBarrier(context, id, count, blocking = true, Some(reduction))
      case Instruction.ReturnStackPush(address) =>
        context.active.foreachLane { lane =>
          val _ = context.lane(lane).returnStack += address
        }
        Right(Step.Advance)
      case Instruction.ReturnStackPop(destination) =>
        advance(forEveryLane(context) { lane =>
          val stack = context.lane(lane).returnStack
          Either.cond(
            stack.nonEmpty,
            effects.stageRegister(lane, destination, stack.remove(stack.size - 1)),
            Fault.Backend("return stack is empty")
          )
        })
      case Instruction.Convergence(token, ConvergenceAction.Setup) =>
        val masks = mutable.LongMap.empty[Long]
        context.active.foreachLane { lane =>
          val warp = (lane.raw & ~31).toLong
          masks(warp) = masks.getOrElse(warp, 0L) | (1L << (lane.raw & 31))
        }
        val tokens = masks.map((warp, mask) =>
          warp -> context.workgroup.createConvergence(warp.toInt.unsafe[WorkgroupLane.WarpBase], mask)
        )
        context.active.foreachLane(lane => context.lane(lane).setRegister(token, tokens((lane.raw & ~31).toLong)))
        Right(Step.Advance)
      case Instruction.Convergence(token, ConvergenceAction.Break) =>
        context.active.foreachLane(lane =>
          context.workgroup.leaveConvergence(
            (lane.raw & ~31).unsafe[WorkgroupLane.WarpBase],
            context.lane(lane).register(token),
            (lane.raw & 31).unsafe[WarpLane]
          )
        )
        Right(Step.Advance)
      case Instruction.Convergence(token, ConvergenceAction.Synchronise) => Right(Step.ConvergenceSync(token))
      case Instruction.WarpBarrier(memberMask)                           =>
        val masks = context.active.headOption.fold(Set.empty[Long]) { _ =>
          val values = mutable.Set.empty[Long]
          context.active.foreachLane(lane => values += context.read(lane, memberMask))
          values.toSet
        }
        if (masks.isEmpty) Left(Fault.Backend("bar.warp.sync has no active lanes"))
        else if (masks.size > 1) Left(Fault.Backend("bar.warp.sync member mask differs across active lanes"))
        else Right(Step.WarpSync(masks.head))
      case Instruction.Fence => context.memory.fenceHostMemory().map(_ => Step.Advance)
      case Instruction.NoOp  => Right(Step.Advance)
      case Instruction.Call(function, returnParameters, parameters) =>
        val groups = mutable.LinkedHashMap.empty[Long, VMLaneMask.Builder]
        context.active.foreachLane(lane =>
          groups.getOrElseUpdate(context.read(lane, function), VMLaneMask.Builder()).add(lane)
        )
        groups.toVec.traverse { (address, lanes) =>
          context.kernel.functionTable.name(address) match {
            case None =>
              Left(Fault.Backend(s"invalid function address 0x${java.lang.Long.toUnsignedString(address, 16)}"))
            case Some(name) =>
              context.kernel.functions.get(name).map(DeviceCall(_, lanes.result()))
                .toRight(Fault.Backend(s"missing device function $name"))
          }
        }.map(Step.Call(_, parameters, returnParameters))
      case Instruction.CallBuiltin(builtin, returnParameters, parameters) =>
        advance(forEveryLane(context)(VMBuiltins.execute(context, _, builtin, returnParameters, parameters)))
      case Instruction.Trap =>
        Left(Fault.Backend(s"${context.kernel.isaName.toLowerCaseRoot} trap 0 at pc ${context.pc}"))
      case Instruction.Return => Right(Step.Return(context.active))
      case Instruction.Exit   => Right(Step.Exit(context.active))
    }

  private def executeConditionCodes(
      instruction: Instruction,
      context: VMContext,
      effects: VMWriteBuffer
  ): Either[Fault, Step] =
    (instruction: @unchecked) match {
      case Instruction.WriteConditionFlag(flag, source) =>
        context.active.foreachLane(lane =>
          effects.stage(VMWrite.ConditionFlag(lane, flag, Some(context.read(lane, source) != 0L)))
        )
        Right(Step.Advance)
      case Instruction.ReadConditionFlag(destination, flag) =>
        advance(forEveryLane(context)(lane =>
          conditionFlag(context, lane, flag).map(value =>
            effects.stageRegister(lane, destination, if (value) 1L else 0L)
          )
        ))
      case Instruction.ReadConditionPredicate(destination, flag) =>
        advance(forEveryLane(context)(lane =>
          conditionFlag(context, lane, flag).map(value => effects.stage(VMWrite.Predicate(lane, destination, value)))
        ))
      case Instruction.InvalidateConditionFlag(flag) =>
        context.active.foreachLane(lane => effects.stage(VMWrite.ConditionFlag(lane, flag, None)))
        Right(Step.Advance)
      case Instruction.InvalidateAddThreeCarry =>
        context.active.foreachLane(lane => effects.stage(VMWrite.AddThreeCarry(lane, None)))
        Right(Step.Advance)
      case add: Instruction.IntegerAddThree => advance(forEveryLane(context)(addThree(context, effects, _, add)))
    }

  private def executeWarp(instruction: Instruction, context: VMContext, effects: VMWriteBuffer): Either[Fault, Step] =
    (instruction: @unchecked) match {
      case Instruction.UniformMove(destination, source) =>
        context.active.foreachLane { lane =>
          val leader = java.lang.Long.numberOfTrailingZeros(context.active.warpBits(lane.raw))
          if ((lane.raw & 31) == leader) {
            val value    = context.read(lane, source) & 0xffffffffL
            val warpBase = lane.raw & ~31
            (warpBase until math.min(warpBase + 32, context.workgroup.laneCount.raw))
              .filter(context.frame.live.contains(_))
              .foreach(target => effects.stageRegister(WorkgroupLane.fromIndex(target), destination, value))
          }
        }
        Right(Step.Advance)
      case Instruction.Tensor(operation) => VMTensor.execute(operation, context, effects)
      case Instruction.Shuffle(mode, destination, validity, source, laneValue, clamp, _, width) =>
        val laneCount = context.workgroup.laneCount.raw
        context.active.foreachLane { lane =>
          val selector        = context.read(lane, laneValue)
          val bounds          = context.read(lane, clamp)
          val laneInWarp      = lane.raw & 31
          val warpBase        = lane.raw - laneInWarp
          val (valid, target) = mode match {
            case ShuffleMode.Down =>
              val distance = (selector & 31L).toInt
              val valid    = laneInWarp + distance <= shuffleClamp(bounds) && lane.raw + distance < laneCount
              valid -> (if (valid) lane.raw + distance else lane.raw.toInt)
            case ShuffleMode.Up =>
              val distance = (selector & 31L).toInt
              val valid    = laneInWarp >= distance && laneInWarp - distance >= shuffleClamp(bounds)
              valid -> (if (valid) lane.raw - distance else lane.raw.toInt)
            case ShuffleMode.Index =>
              val segmentMask  = ((bounds >>> 8) & 31L).toInt
              val segmentBase  = laneInWarp & segmentMask
              val targetInWarp = segmentBase | ((selector & 31L).toInt & ~segmentMask)
              val maximum      = segmentBase | ((bounds & 31L).toInt & ~segmentMask)
              val valid        = targetInWarp <= maximum && warpBase + targetInWarp < laneCount
              valid -> (if (valid) warpBase + targetInWarp else lane.raw.toInt)
            case ShuffleMode.Butterfly =>
              val segmentMask  = ((bounds >>> 8) & 31L).toInt
              val targetInWarp = laneInWarp ^ (selector & 31L).toInt
              val maximum      = (laneInWarp & segmentMask) | ((bounds & 31L).toInt & ~segmentMask)
              val valid        = targetInWarp <= maximum
              valid -> (if (valid && warpBase + targetInWarp < laneCount) warpBase + targetInWarp else lane.raw.toInt)
          }
          effects.stageRegister(
            lane,
            destination,
            truncate(context.read(WorkgroupLane.fromIndex(target), source), width)
          )
          validity.foreach(predicate => effects.stage(VMWrite.Predicate(lane, predicate, valid)))
        }
        Right(Step.Advance)
      case Instruction.Elect(destination, predicate, memberMask) =>
        advance(forEveryLane(context) { lane =>
          val mask     = context.read(lane, memberMask)
          val warpBase = lane.raw & ~31
          if ((mask & (1L << (lane.raw & 31))) == 0L)
            Left(Fault.Backend(s"elect.sync lane $lane is absent from its member mask"))
          else
            (0 until 32).find(candidate =>
              (mask & (1L << candidate)) != 0L && context.active.contains(warpBase + candidate)
            )
              .toRight(Fault.Backend("elect.sync has no participating lane")).map { elected =>
                destination.foreach(effects.stageRegister(lane, _, elected.toLong))
                effects.stage(VMWrite.Predicate(lane, predicate, (lane.raw & 31) == elected))
              }
        })
      case Instruction.VoteBallot(destination, predicate, memberMask, negated) =>
        advance(forEveryLane(context)(lane =>
          warpPredicates(context, lane, context.read(lane, memberMask), predicate, negated).map { values =>
            val ballot = values.foldLeft(0L) { case (bits, (laneInWarp, set)) =>
              if (set) bits | (1L << laneInWarp) else bits
            }
            effects.stageRegister(lane, destination, ballot)
          }
        ))
      case Instruction.VotePredicate(destination, predicate, memberMask, operation, negated) =>
        advance(forEveryLane(context)(lane =>
          warpPredicates(context, lane, context.read(lane, memberMask), predicate, negated).map { values =>
            val selected = operation match {
              case VoteOperation.All     => values.nonEmpty && values.forall(_._2)
              case VoteOperation.Any     => values.exists(_._2)
              case VoteOperation.Uniform => values.map(_._2).distinct.size <= 1
            }
            effects.stage(VMWrite.Predicate(lane, destination, selected))
          }
        ))
      case Instruction.WarpReduce(destination, source, memberMask, operation, width) =>
        warpReduce(context, effects, destination, source, memberMask, operation, width)
    }

  private def executeMBarrier(
      instruction: Instruction,
      context: VMContext,
      effects: VMWriteBuffer
  ): Either[Fault, Step] =
    (instruction: @unchecked) match {
      case Instruction.MBarrierLoad(address) =>
        advance(forEveryLane(context)(lane =>
          sharedBarrierAddress(context.read(lane, address)).flatMap { canonical =>
            if (context.workgroup.invalidatedMBarriers(canonical))
              Left(Fault.Backend(s"invalidated mbarrier at shared address $canonical"))
            else if (context.workgroup.mbarriers.contains(canonical)) Right(())
            else MBarrier.fromStoredInitialState(context.workgroup.readShared(canonical, 64)).map(barrier =>
              context.workgroup.mbarriers(canonical) = barrier
            )
          }
        ))
      case Instruction.MBarrierInit(address, count) =>
        advance(forEveryLane(context)(lane =>
          for {
            canonical <- sharedBarrierAddress(context.read(lane, address))
            barrier   <- MBarrier.create(context.read(lane, count))
          } yield {
            val _ = context.workgroup.invalidatedMBarriers.remove(canonical)
            context.workgroup.mbarriers(canonical) = barrier
          }
        ))
      case Instruction.MBarrierInvalidate(address) =>
        advance(forEveryLane(context) { lane =>
          val raw = context.read(lane, address)
          for {
            canonical <- sharedBarrierAddress(raw)
            _         <- mbarrier(context, raw)
          } yield {
            val _ = context.workgroup.mbarriers.remove(canonical)
            val _ = context.workgroup.invalidatedMBarriers.add(canonical)
          }
        })
      case Instruction.MBarrierArrive(destination, address, count, expectedBytes) =>
        advance(forEveryLane(context)(lane =>
          mbarrier(context, context.read(lane, address))
            .flatMap(_.arrive(context.read(lane, count), context.read(lane, expectedBytes)))
            .map(token => destination.foreach(effects.stageRegister(lane, _, token)))
        ))
      case Instruction.MBarrierTest(destination, address, phase, parity) =>
        advance(forEveryLane(context) { lane =>
          val token = context.read(lane, phase)
          for {
            barrier <- mbarrier(context, context.read(lane, address))
            _       <- Either.cond(
              !parity || token == 0L || token == 1L,
              (),
              Fault.Backend(s"invalid mbarrier phase parity: $token")
            )
          } yield effects.stage(VMWrite.Predicate(lane, destination, barrier.complete(token, parity)))
        })
    }

  private def executeCluster(
      instruction: Instruction,
      context: VMContext,
      effects: VMWriteBuffer
  ): Either[Fault, Step] =
    (instruction: @unchecked) match {
      case Instruction.ClusterBarrier(arrive)              => Right(Step.ClusterBarrier(arrive))
      case Instruction.ClusterTryCancel(response, barrier) =>
        advance(forEveryLane(context) { lane =>
          val responseAddress = context.read(lane, response)
          val barrierAddress  = context.read(lane, barrier)
          for {
            _ <- Either.cond(
              (responseAddress & 15L) == 0 && (barrierAddress & 7L) == 0,
              (),
              Fault.Backend("unaligned cluster cancellation response or barrier")
            )
            target <- mbarrier(context, barrierAddress)
            _      <- target.completeBytes(16)
          } yield {
            // The independent-workgroup scheduler reserves every CTA at launch, so none can be cancelled.
            // The opaque failed response still completes the requested asynchronous transaction.
            effects.stage(VMWrite.Shared(responseAddress, 64, 0L))
            effects.stage(VMWrite.Shared(responseAddress + 8L, 64, 0L))
          }
        })
      case Instruction.ClusterCancelStatus(destination, response) =>
        context.active.foreachLane(lane =>
          effects.stage(VMWrite.Predicate(lane, destination, (context.read(lane, response) & 1L) != 0L))
        )
        Right(Step.Advance)
      case Instruction.ClusterCancelCta(destination, low, high, axis) =>
        advance(forEveryLane(context) { lane =>
          val lowBits  = context.read(lane, low)
          val highBits = context.read(lane, high)
          Either.cond(
            (highBits & 1L) != 0L,
            effects.stageRegister(
              lane,
              destination,
              axis match {
                case Axis.X => lowBits & 0xffffffffL
                case Axis.Y => lowBits >>> 32
                case Axis.Z => highBits >>> 32
              }
            ),
            Fault.Backend("cannot query CTA coordinates from a failed cancellation")
          )
        })
    }

  private def executeAsyncCopy(
      instruction: Instruction,
      context: VMContext,
      effects: VMWriteBuffer
  ): Either[Fault, Step] =
    (instruction: @unchecked) match {
      case Instruction.AsyncBulkStore(destination, source, size, byteMask) =>
        advance(forEveryLane(context) { lane =>
          val target = context.read(lane, destination)
          val origin = context.read(lane, source)
          val mask   = context.read(lane, byteMask)
          bulkTransfer("bulk store", target, origin, context.read(lane, size)).map { bytes =>
            (0L until bytes.raw).foreach { offset =>
              if ((mask & (1L << (offset.toInt & 15))) != 0L)
                effects.stageMemory(
                  lane,
                  GuestAddress(target + offset),
                  8,
                  context.workgroup.readShared(origin + offset, 8)
                )
            }
          }
        })
      case Instruction.AsyncBulkCopy(destination, source, size, barrier) =>
        advance(forEveryLane(context) { lane =>
          val target = context.read(lane, destination)
          val origin = context.read(lane, source)
          for {
            completion <- mbarrier(context, context.read(lane, barrier))
            bytes      <- bulkTransfer("bulk copy", target, origin, context.read(lane, size))
            copied     <- readDeviceBytes(context, lane, origin, bytes.raw)
            _          <- completion.completeBytes(bytes)
          } yield copied.zipWithIndex.foreach((byte, offset) =>
            effects.stage(VMWrite.Shared(target + offset.toLong, 8, byte.toLong))
          )
        })
      case Instruction.AsyncCopy(destination, source, size, sourceSize) =>
        advance(forEveryLane(context) { lane =>
          val target = context.read(lane, destination)
          val origin = context.read(lane, source)
          val count  = context.read(lane, sourceSize)
          for {
            _ <- Either.cond(
              count >= 0 && count <= size.raw,
              (),
              Fault.Backend(s"cp.async source size $count exceeds copy size $size")
            )
            copied <- readDeviceBytes(context, lane, origin, count)
          } yield (0 until size.raw).foreach(offset =>
            effects.stage(VMWrite.Shared(target + offset.toLong, 8, copied.lift(offset).getOrElse(0.toByte).toLong))
          )
        })
    }

  private def conditionFlag(context: VMContext, lane: WorkgroupLane, flag: VMConditionCodes.Flag)
      : Either[Fault, Boolean] =
    context.lane(lane).conditionCodes.read(flag).toRight(
      Fault.Backend(s"condition flag $flag is undefined at pc ${context.pc}, lane $lane")
    )

  private def addThree(
      context: VMContext,
      effects: VMWriteBuffer,
      lane: WorkgroupLane,
      instruction: Instruction.IntegerAddThree
  ): Either[Fault, Unit] = {
    val Instruction.IntegerAddThree(
      destination,
      first,
      second,
      third,
      width,
      mode,
      negateFirst,
      negateSecond,
      negateThird
    ) =
      instruction
    val carry = mode match {
      case AddThreeCarryMode.Consume =>
        context.lane(lane).conditionCodes.readThreeInputCarry.map(_.raw).toRight(
          Fault.Backend(s"IADD3 carry state is undefined at pc ${context.pc}, lane $lane")
        )
      case _ => Right(0)
    }
    carry.map { carryValue =>
      val mask                                         = Bits.mask(width)
      def operand(value: Value, negate: Boolean): Long = {
        val bits = truncate(context.read(lane, value), width)
        if (negate) mask - bits else bits
      }
      val (firstSum, firstCarry) =
        IntegerArithmetic.addUnsigned(operand(first, negateFirst), operand(second, negateSecond), false, width)
      val (secondSum, secondCarry) = IntegerArithmetic.addUnsigned(firstSum, operand(third, negateThird), false, width)
      val negations                = Vec(negateFirst, negateSecond, negateThird).count(identity)
      val correction               = carryValue + (if (mode == AddThreeCarryMode.Produce) negations else 0)
      val (sum, finalCarry)        = IntegerArithmetic.addUnsigned(secondSum, correction.toLong, false, width)
      effects.stageRegister(lane, destination, sum)
      mode match {
        case AddThreeCarryMode.Produce =>
          val carries = Vec(firstCarry, secondCarry, finalCarry).count(identity)
          effects.stage(VMWrite.AddThreeCarry(lane, Some(carries.unsafe[Int in 0 ~ 2])))
        case AddThreeCarryMode.Consume => effects.stage(VMWrite.AddThreeCarry(lane, None))
        case AddThreeCarryMode.None    => ()
      }
    }
  }

  private def warpReduce(
      context: VMContext,
      effects: VMWriteBuffer,
      destination: Register,
      source: Value,
      memberMask: Value,
      operation: IntegerOperation,
      width: ScalarBitWidth
  ): Either[Fault, Step] = operation match {
    case IntegerOperation.Add | IntegerOperation.BitwiseAnd | IntegerOperation.BitwiseOr | IntegerOperation.BitwiseXor |
        IntegerOperation.Minimum(_) | IntegerOperation.Maximum(_) =>
      val values = mutable.LongMap.empty[Long]
      val masks  = mutable.LongMap.empty[Long]
      context.active.foreachLane { lane =>
        values(lane.raw.toLong) = context.read(lane, source)
        masks(lane.raw.toLong) = context.read(lane, memberMask)
      }
      advance(forEveryLane(context) { lane =>
        val warpBase = lane.raw & ~31
        val mask     = masks(lane.raw.toLong)
        val members  = Vec.from((0 until 32).flatMap(laneInWarp =>
          Option.when((mask & (1L << laneInWarp)) != 0L)(values.get((warpBase + laneInWarp).toLong)).flatten
        ))
        if ((mask & (1L << (lane.raw & 31))) == 0L || members.isEmpty)
          Left(Fault.Backend(s"redux.sync lane $lane is absent from its member mask"))
        else {
          val reduced = members.tail.foldLeft(truncate(members.head, width)) { (accumulator, rawValue) =>
            val value = truncate(rawValue, width)
            operation match {
              case IntegerOperation.Add            => truncate(accumulator + value, width)
              case IntegerOperation.BitwiseAnd     => accumulator & value
              case IntegerOperation.BitwiseOr      => accumulator | value
              case IntegerOperation.BitwiseXor     => accumulator ^ value
              case IntegerOperation.Minimum(true)  => Math.min(signExtend(accumulator, width), signExtend(value, width))
              case IntegerOperation.Maximum(true)  => Math.max(signExtend(accumulator, width), signExtend(value, width))
              case IntegerOperation.Minimum(false) =>
                if (java.lang.Long.compareUnsigned(accumulator, value) < 0) accumulator else value
              case _ => if (java.lang.Long.compareUnsigned(accumulator, value) > 0) accumulator else value
            }
          }
          Right(effects.stageRegister(lane, destination, truncate(reduced, width)))
        }
      })
    case _ => Left(Fault.Backend(s"unsupported warp reduction operation: $operation"))
  }

  /** Register-only integer instructions write destinations directly: each lane reads only its own registers, before
    * writing them, so no staging is needed.
    */
  private def executeInteger(instruction: Instruction, context: VMContext): Boolean = instruction match {
    case Instruction.Move(destination, source, width) =>
      context.active.foreachLane(lane =>
        context.lane(lane).setRegister(destination, truncate(context.read(lane, source), width))
      )
      true
    case Instruction.MovePredicate(destination, source) =>
      context.active.foreachLane(lane => context.lane(lane).setPredicate(destination, context.read(lane, source) != 0L))
      true
    case Instruction.MoveVector(destinations, sources, width) =>
      context.active.foreachLane { lane =>
        val values  = sources.map(context.read(lane, _))
        val storage = context.lane(lane)
        destinations.zip(values).foreach((destination, value) =>
          storage.setRegister(destination, truncate(value, width))
        )
      }
      true
    case Instruction.MoveSplit(destinations, source, partWidth) =>
      context.active.foreachLane { lane =>
        val value   = context.read(lane, source)
        val storage = context.lane(lane)
        destinations(0).foreach(storage.setRegister(_, truncate(value, partWidth)))
        destinations(1).foreach(storage.setRegister(_, truncate(value >>> partWidth.raw, partWidth)))
      }
      true
    case Instruction.MoveCombine(destination, sources, partWidth) =>
      context.active.foreachLane { lane =>
        val low  = context.read(lane, sources(0))
        val high = context.read(lane, sources(1))
        context.lane(lane).setRegister(
          destination,
          truncate(truncate(low, partWidth) | (truncate(high, partWidth) << partWidth.raw), partWidth.combined)
        )
      }
      true
    case Instruction.IntegerUnary(operation, destination, source, width) =>
      context.active.foreachLane(lane =>
        context.lane(lane).setRegister(destination, integerUnaryResult(operation, context.read(lane, source), width))
      )
      true
    case Instruction.BitFind(destination, source, width, signed, shiftAmount) =>
      context.active.foreachLane(lane =>
        context.lane(lane).setRegister(destination, bitFind(context.read(lane, source), width, signed, shiftAmount))
      )
      true
    case Instruction.DotProduct4(destination, left, right, addend, leftSigned, rightSigned) =>
      context.active.foreachLane(lane =>
        context.lane(lane).setRegister(
          destination,
          dotProduct4(
            context.read(lane, left),
            context.read(lane, right),
            context.read(lane, addend),
            leftSigned,
            rightSigned
          )
        )
      )
      true
    case Instruction.IntegerAddWithCarry(destination, left, right, width, carryIn, carryOut) =>
      context.active.foreachLane { lane =>
        val storage             = context.lane(lane)
        val (result, nextCarry) =
          IntegerArithmetic.addUnsigned(
            context.read(lane, left),
            context.read(lane, right),
            carryIn && storage.carry,
            width
          )
        storage.setRegister(destination, result)
        if (carryOut) storage.carry = nextCarry
      }
      true
    case Instruction.IntegerSubtractWithBorrow(destination, left, right, width, borrowIn, borrowOut) =>
      context.active.foreachLane { lane =>
        val storage              = context.lane(lane)
        val (result, nextBorrow) = IntegerArithmetic.subtractUnsigned(
          context.read(lane, left),
          context.read(lane, right),
          borrowIn && storage.carry,
          width
        )
        storage.setRegister(destination, result)
        if (borrowOut) storage.carry = nextBorrow
      }
      true
    case Instruction.IntegerBinary(operation, destination, left, right, width) =>
      context.active.foreachLane(lane =>
        context.lane(lane).setRegister(
          destination,
          integerBinaryResult(operation, context.read(lane, left), context.read(lane, right), width)
        )
      )
      true
    case Instruction.IntegerSubtractAdd(destination, left, right, addend, width) =>
      context.active.foreachLane(lane =>
        context.lane(lane).setRegister(
          destination,
          truncate(context.read(lane, left) - context.read(lane, right) + context.read(lane, addend), width)
        )
      )
      true
    case Instruction.FunnelShift(destination, low, high, amount, left, clamp) =>
      context.active.foreachLane(lane =>
        context.lane(lane).setRegister(
          destination,
          funnelShiftResult(context.read(lane, low), context.read(lane, high), context.read(lane, amount), left, clamp)
        )
      )
      true
    case Instruction.Convert(destination, source, sourceFormat, destinationFormat, rounding, clamp) =>
      context.active.foreachLane(lane =>
        context.lane(lane).setRegister(
          destination,
          VMArithmetic.convert(context.read(lane, source), sourceFormat, destinationFormat, rounding, clamp)
        )
      )
      true
    case Instruction.ConvertTF32(destination, source) =>
      context.active.foreachLane { lane =>
        val bits      = context.read(lane, source)
        val magnitude = bits & 0x7fffffffL
        context.lane(lane).setRegister(
          destination,
          if (magnitude > 0x7f800000L) (bits & 0xffffe000L) | 0x00400000L
          else if (magnitude == 0x7f800000L) bits & 0xffffffffL
          else (bits + 0x1000L) & 0xffffe000L
        )
      }
      true
    case Instruction.MultiplyAdd(destination, left, right, addend, operation) =>
      context.active.foreachLane { lane =>
        val storage         = context.lane(lane)
        val (result, carry) = integerMultiplyAdd(
          context.read(lane, left),
          context.read(lane, right),
          context.read(lane, addend),
          operation,
          storage.carry
        )
        storage.setRegister(destination, result)
        operation match {
          case IntegerMultiplyAddOperation.Low(_, _, _, true) | IntegerMultiplyAddOperation.High(_, _, _, true) =>
            storage.carry = carry
          case _ => ()
        }
      }
      true
    case Instruction.BitFieldExtract(destination, source, start, widthValue, width, signed) =>
      context.active.foreachLane(lane =>
        context.lane(lane).setRegister(
          destination,
          extractBits(
            context.read(lane, source),
            context.read(lane, start),
            context.read(lane, widthValue),
            width,
            signed
          )
        )
      )
      true
    case Instruction.BitFieldInsert(destination, insertValue, baseValue, bitPosition, bitLength, width) =>
      context.active.foreachLane(lane =>
        context.lane(lane).setRegister(
          destination,
          insertBits(
            context.read(lane, insertValue),
            context.read(lane, baseValue),
            context.read(lane, bitPosition),
            context.read(lane, bitLength),
            width
          )
        )
      )
      true
    case Instruction.BitMask(destination, start, widthValue, mode) =>
      context.active.foreachLane(lane =>
        context.lane(lane).setRegister(
          destination,
          bitMask(context.read(lane, start), context.read(lane, widthValue), mode)
        )
      )
      true
    case Instruction.BytePermute(destination, left, right, control, mode) =>
      context.active.foreachLane(lane =>
        context.lane(lane).setRegister(
          destination,
          permuteBytes(context.read(lane, left), context.read(lane, right), context.read(lane, control), mode)
        )
      )
      true
    case _ => false
  }

  private def executeFloatAndPredicate(instruction: Instruction, context: VMContext): Boolean = instruction match {
    case Instruction.FloatBinary(operation, destination, left, right, width, control) =>
      context.active.foreachLane { lane =>
        val leftBits  = floatInput(context.read(lane, left), width, control)
        val rightBits = floatInput(context.read(lane, right), width, control)
        context.lane(lane).setRegister(
          destination,
          floatResult(floatBinaryBits(operation, leftBits, rightBits, width, control), width, control)
        )
      }
      true
    case Instruction.FloatBinaryPacked(operation, destination, left, right, format, control) =>
      context.active.foreachLane(lane =>
        context.lane(lane).setRegister(
          destination,
          packedBinaryBits(operation, context.read(lane, left), context.read(lane, right), format, control)
        )
      )
      true
    case Instruction.FloatUnary(operation, destination, source, width, control) =>
      context.active.foreachLane { lane =>
        val bits = floatInput(context.read(lane, source), width, control)
        context.lane(lane).setRegister(destination, floatResult(floatUnaryBits(operation, bits, width), width, control))
      }
      true
    case Instruction.FusedMultiplyAdd(destination, left, right, addend, width, control, rounding) =>
      context.active.foreachLane { lane =>
        val leftBits   = floatInput(context.read(lane, left), width, control)
        val rightBits  = floatInput(context.read(lane, right), width, control)
        val addendBits = floatInput(context.read(lane, addend), width, control)
        val fused      =
          canonical(
            IEEE754.fusedMultiplyAdd(leftBits, rightBits, addendBits, width, roundingMode(Some(rounding))),
            width
          )
        context.lane(lane).setRegister(destination, floatResult(fused, width, control))
      }
      true
    case Instruction.FusedMultiplyAddPacked(destination, left, right, addend, bf16, saturate) =>
      context.active.foreachLane(lane =>
        context.lane(lane).setRegister(
          destination,
          packedFmaBits(context.read(lane, left), context.read(lane, right), context.read(lane, addend), bf16, saturate)
        )
      )
      true
    case Instruction.FusedMultiplyAddBf16(destination, left, right, addend) =>
      context.active.foreachLane(lane =>
        context.lane(lane).setRegister(
          destination,
          bfloat16FusedMultiplyAdd(
            context.read(lane, left),
            context.read(lane, right),
            context.read(lane, addend),
            false
          )
        )
      )
      true
    case Instruction.SetPredicate(destination, comparison, format, left, right, flushSubnormal) =>
      context.active.foreachLane(lane =>
        context.lane(lane).setPredicate(
          destination,
          compare(
            flushFloat32(context.read(lane, left), flushSubnormal),
            flushFloat32(context.read(lane, right), flushSubnormal),
            comparison,
            format
          )
        )
      )
      true
    case Instruction.PredicateCompare(destination, comparison, left, right) =>
      context.active.foreachLane { lane =>
        val storage = context.lane(lane)
        storage.setPredicate(
          destination,
          comparePredicates(
            storage.predicate(left).getOrElse(false),
            storage.predicate(right).getOrElse(false),
            comparison
          )
        )
      }
      true
    case Instruction.PredicateUnary(destination, source) =>
      context.active.foreachLane { lane =>
        val storage = context.lane(lane)
        storage.setPredicate(destination, !storage.predicate(source).getOrElse(false))
      }
      true
    case Instruction.PredicateBinary(operation, destination, left, right) =>
      context.active.foreachLane { lane =>
        val storage    = context.lane(lane)
        val leftValue  = storage.predicate(left).getOrElse(false)
        val rightValue = storage.predicate(right).getOrElse(false)
        storage.setPredicate(
          destination,
          operation match {
            case PredicateOperation.And => leftValue && rightValue
            case PredicateOperation.Or  => leftValue || rightValue
            case PredicateOperation.Xor => leftValue ^ rightValue
          }
        )
      }
      true
    case Instruction.Select(destination, whenTrue, whenFalse, predicate, width) =>
      context.active.foreachLane { lane =>
        val storage  = context.lane(lane)
        val selected = context.read(lane, if (storage.predicate(predicate).getOrElse(false)) whenTrue else whenFalse)
        storage.setRegister(destination, truncate(selected, width))
      }
      true
    case _ => false
  }
}
