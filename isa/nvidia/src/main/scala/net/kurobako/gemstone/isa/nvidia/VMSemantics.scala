package net.kurobako.gemstone.isa.nvidia

import VMExecution.*
import VMIR.*
import cats.syntax.all.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.string.*
import net.kurobako.gemstone.vm.*
import scala.collection.mutable

private object VMSemantics {

  def execute(instruction: Instruction, sinks: LaneLoop): Either[Fault, Step] = {
    val staged = NvidiaStaged.execute(instruction, sinks)
    if (staged eq NvidiaStaged.Unstaged) executeStaged(instruction, sinks.context, sinks.effects)
    else advance(staged)
  }

  private inline def advance(result: Either[Fault, Unit]): Either[Fault, Step] = result match {
    case Left(fault) => Left(fault)
    case _           => Advanced
  }

  /** The CTA whose shared memory holds an mbarrier and the mbarrier's address within that CTA's own window. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  private def sharedBarrierAddress(context: VMContext, address: Long): Either[Fault, (VMWorkgroup, Long)] =
    if ((address & 7L) != 0 || !VMAddressSpace.isShared(address, 8))
      Left(Fault.Backend(s"invalid shared mbarrier address: $address"))
    else context.workgroup.sharedOwner(address) match {
      case null               => Left(Fault.Backend(s"mbarrier address names a CTA outside the cluster: $address"))
      case owner: VMWorkgroup => Right(owner -> VMAddressSpace.sharedAddress(address))
    }

  private def mbarrier(context: VMContext, address: Long): Either[Fault, MBarrier] = VMAsync.mbarrier(context, address)

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

  private enum BarrierMode(val blocking: Boolean) {
    case Sync   extends BarrierMode(blocking = true)
    case Arrive extends BarrierMode(blocking = false)
  }

  private def workgroupBarrier(
      context: VMContext,
      id: Value,
      count: Option[Value],
      mode: BarrierMode,
      reduction: Option[BarrierReduction]
  ): Either[Fault, Step] = (id, count) match {
    case (Value.Immediate(barrier), None) => uniformBarrier(context, barrier, 0L, mode.blocking, reduction)
    case (Value.Immediate(barrier), Some(Value.Immediate(expected))) =>
      uniformBarrier(context, barrier, expected, mode.blocking, reduction)
    case _ => divergentBarrier(context, id, count, mode.blocking, reduction)
  }

  private def barrierTarget(rawBarrier: Long, rawExpected: Long, blocking: Boolean)
      : Either[Fault, (BarrierId, BarrierCount)] = {
    val barrier  = rawBarrier & 0xffffffffL
    val expected = rawExpected & 0xffffffffL
    val id       = Option.when(barrier < 16L)(barrier.toInt).flatMap(_.refine[BarrierId].toOption)
    val count    = Option.when(expected <= VMLaneMask.MaximumLanes && (blocking || expected > 0L))(expected.toInt)
      .flatMap(_.refine[BarrierCount].toOption)
    (id, count) match {
      case (Some(barrierId), Some(threadCount)) => Right(barrierId -> threadCount)
      case (None, _)                            => Left(Fault.Backend(s"barrier id $barrier is outside 0..15"))
      case _                                    =>
        Left(Fault.Backend(s"barrier thread count $expected must be a multiple of 32 within the workgroup limit"))
    }
  }

  /** A barrier whose id and count are the same for every lane: lanes arrive grouped by warp, in warp order. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def uniformBarrier(
      context: VMContext,
      barrier: Long,
      expected: Long,
      blocking: Boolean,
      reduction: Option[BarrierReduction]
  ): Either[Fault, Step] = barrierTarget(barrier, expected, blocking).map { (id, count) =>
    val arrivals = Vec.newBuilder[WorkgroupArrival]
    var base     = 0
    while (base < context.workgroup.laneCount) {
      var remaining = context.active.warpBits(base)
      if (remaining != 0L) {
        val lanes = VMLaneMask.Builder()
        while (remaining != 0L) {
          lanes.add(WorkgroupLane.fromIndex(base + java.lang.Long.numberOfTrailingZeros(remaining)))
          remaining &= remaining - 1L
        }
        arrivals += WorkgroupArrival(id, count, lanes.result())
      }
      base += 32
    }
    Step.WorkgroupBarrier(arrivals.result(), blocking, reduction)
  }

  private def divergentBarrier(
      context: VMContext,
      id: Value,
      count: Option[Value],
      blocking: Boolean,
      reduction: Option[BarrierReduction]
  ): Either[Fault, Step] = {
    val arrivals = mutable.LinkedHashMap.empty[(BarrierId, BarrierCount, Int), VMLaneMask.Builder]
    LaneFaults.everyLane(context) { lane =>
      barrierTarget(context.read(lane, id), count.fold(0L)(context.read(lane, _)), blocking).map((id, count) =>
        arrivals.getOrElseUpdate((id, count, lane & ~31), VMLaneMask.Builder()).add(lane)
      )
    }.map(_ =>
      Step.WorkgroupBarrier(
        Vec.from(arrivals).map { case ((barrierId, threadCount, _), builder) =>
          WorkgroupArrival(barrierId, threadCount, builder.result())
        },
        blocking,
        reduction
      )
    )
  }

  private def executeStaged(instruction: Instruction, context: VMContext, effects: VMWriteBuffer)
      : Either[Fault, Step] = instruction match {
    case _: (Instruction.Branch | Instruction.BranchTable | Instruction.BarrierSync | Instruction.BarrierArrive |
          Instruction.BarrierReduce |
          Instruction.ReturnStackPush | Instruction.ReturnStackPop | Instruction.ReturnStackSave |
          Instruction.Convergence | Instruction.WarpBarrier |
          Instruction.Call | Instruction.CallBuiltin) =>
      executeControl(instruction, context, effects)
    case Instruction.Fence | Instruction.NoOp | Instruction.Trap | Instruction.Return | Instruction.Exit =>
      executeControl(instruction, context, effects)
    case Instruction.WarpReduce(operation = operation) =>
      Left(Fault.Backend(s"unsupported warp reduction operation: $operation"))
    case Instruction.InvalidateConditionFlag(flag) =>
      context.active.foreachLane(lane => effects.stage(VMWrite.ConditionFlag(lane, flag, None)))
      Advanced
    case Instruction.InvalidateAddThreeCarry =>
      context.active.foreachLane(lane => effects.stage(VMWrite.AddThreeCarry(lane, None)))
      Advanced
    case Instruction.Tensor(operation) => VMTensor.execute(operation, context, effects)
    case _: (Instruction.MBarrierLoad | Instruction.MBarrierInit | Instruction.MBarrierInvalidate |
          Instruction.MBarrierArrive | Instruction.MBarrierTest) =>
      executeMBarrier(instruction, context, effects)
    case _: (Instruction.SharedLockLoad | Instruction.SharedStoreUnlock) => executeLock(instruction, context, effects)
    case _: (Instruction.ClusterBarrier | Instruction.ClusterTryCancel) => executeCluster(instruction, context, effects)
    case _: (Instruction.AsyncBulkStore | Instruction.AsyncCopy) => executeAsyncCopy(instruction, context, effects)
    case _: (Instruction.AsyncBulkCopy | Instruction.AsyncBulkReduce | Instruction.AsyncBulkFill |
          Instruction.AsyncTensor | Instruction.TensorMapReplace | Instruction.TensorMapCopy |
          Instruction.AsyncSharedStore | Instruction.AsyncSharedReduce | Instruction.MBarrierTransaction |
          Instruction.ClusterMap | Instruction.ClusterRankOf) =>
      VMAsync.execute(instruction, context)
    case _: (Instruction.StackAllocate | Instruction.StackSave | Instruction.StackRestore) =>
      executeStack(instruction, context, effects)
    case _: (Instruction.TensorMemoryAllocate | Instruction.TensorMemoryDeallocate | Instruction.TensorMemoryClaim |
          Instruction.TensorMemoryRelease | Instruction.TensorMemoryLoad | Instruction.TensorMemoryStore |
          Instruction.TensorMemoryCopy | Instruction.TensorMemoryShift | Instruction.TensorMemoryCommit) |
        Instruction.TensorMemoryRelinquish =>
      VMTensorMemory.execute(instruction, context, effects)
    case other => Left(Fault.IllegalInstruction(s"unhandled ${context.kernel.isaName} instruction $other"))
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
  private def executeControl(
      instruction: Instruction,
      context: VMContext,
      effects: VMWriteBuffer
  ): Either[Fault, Step] =
    (instruction: @unchecked) match {
      case Instruction.Branch(target)              => Right(Step.Jump(target))
      case Instruction.BranchTable(index, targets) =>
        val lanes    = Array.ofDim[VMLaneMask.Builder | Null](targets.size)
        val past     = VMLaneMask.Builder()
        val selected = Vec.newBuilder[Arm]
        context.active.foreachLane { lane =>
          val position = context.read(lane, index) & 0xffffffffL
          if (position < targets.size) {
            if (lanes(position.toInt) == null) lanes(position.toInt) = VMLaneMask.Builder()
            lanes(position.toInt).nn.add(lane)
          } else past.add(lane)
        }
        var position = 0
        while (position < lanes.length) {
          if (lanes(position) != null) selected += Arm(targets(position), lanes(position).nn.result())
          position += 1
        }
        Right(Step.Branches(selected.result(), past.result()))
      case Instruction.BarrierSync(id, count)   => workgroupBarrier(context, id, count, BarrierMode.Sync, None)
      case Instruction.BarrierArrive(id, count) => workgroupBarrier(context, id, Some(count), BarrierMode.Arrive, None)
      case Instruction.BarrierReduce(result, id, predicate, count, negated) =>
        val reduction = BarrierReduction(result, predicate, negated)
        workgroupBarrier(context, id, count, BarrierMode.Sync, Some(reduction))
      case Instruction.ReturnStackPush(address) =>
        context.active.foreachLane { lane =>
          context.lane(lane).returnStack.push(address)
        }
        Advanced
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
            effects.stageRegister(lane, destination, stack.pop()),
            Fault.Backend("return stack is empty")
          )
        })
      case Instruction.Convergence(token, ConvergenceAction.Setup) =>
        val masks = Array.ofDim[Long]((context.workgroup.laneCount + 31) / 32)
        context.active.foreachLane { lane =>
          masks(lane >> 5) |= 1L << (lane & 31)
        }
        val tokens = Array.ofDim[Long](masks.length)
        var warp   = 0
        while (warp < masks.length) {
          if (masks(warp) != 0L) tokens(warp) = context.workgroup.createConvergence(
            (warp << 5).unsafe[WorkgroupLane.WarpBase],
            masks(warp)
          )
          warp += 1
        }
        context.active.foreachLane(lane => context.lane(lane).setRegister(token, tokens(lane >> 5)))
        Advanced
      case Instruction.Convergence(token, ConvergenceAction.Break) =>
        context.active.foreachLane(lane =>
          context.workgroup.leaveConvergence(
            (lane & ~31).unsafe[WorkgroupLane.WarpBase],
            context.lane(lane).register(token),
            (lane & 31).unsafe[WarpLane]
          )
        )
        Advanced
      case Instruction.Convergence(token, ConvergenceAction.Synchronise) => Right(Step.ConvergenceSync(token))
      case Instruction.WarpBarrier(memberMask)                           =>
        // each warp synchronises on its own mask, which its active lanes must agree on
        val masks   = context.values((context.workgroup.laneCount + 31) >>> 5)
        var seen    = 0L
        var differs = false
        context.active.foreachLane { lane =>
          val warp = lane >>> 5
          val mask = context.read(lane, memberMask)
          if ((seen & (1L << warp)) == 0L) {
            seen |= 1L << warp
            masks(warp) = mask
          } else if (masks(warp) != mask) differs = true
        }
        if (seen == 0L) Left(Fault.Backend("bar.warp.sync has no active lanes"))
        else if (differs) Left(Fault.Backend("bar.warp.sync member mask differs across active lanes"))
        else Right(Step.WarpSync(memberMask))
      case Instruction.Fence => context.memory.fenceHostMemory().map(_ => Step.Advance)
      case Instruction.NoOp  => Advanced
      case Instruction.Call(Value.Immediate(address), returnParameters, parameters) =>
        deviceCall(context, address, context.active).map(call => Step.Call(Vec(call), parameters, returnParameters))
      case Instruction.Call(function, returnParameters, parameters) =>
        val addresses = Array.ofDim[Long](context.workgroup.laneCount)
        val builders  = Array.ofDim[VMLaneMask.Builder | Null](addresses.length)
        var groups    = 0
        context.active.foreachLane { lane =>
          val address = context.read(lane, function)
          var group   = 0
          while (group < groups && addresses(group) != address) group += 1
          if (group == groups) {
            addresses(group) = address
            builders(group) = VMLaneMask.Builder()
            groups += 1
          }
          builders(group).nn.add(lane)
        }
        Vec.tabulate(groups)(identity).traverse(group =>
          deviceCall(context, addresses(group), builders(group).nn.result())
        )
          .map(Step.Call(_, parameters, returnParameters))
      case Instruction.CallBuiltin(builtin, returnParameters, parameters) =>
        advance(LaneFaults.everyLane(context)(VMBuiltins.execute(context, _, builtin, returnParameters, parameters)))
      case Instruction.Trap =>
        Left(Fault.Trap(s"${context.kernel.isaName.toLowerCaseRoot} trap 0 at pc ${context.pc}"))
      case Instruction.Return => Right(Step.Return(context.active))
      case Instruction.Exit   => Right(Step.Exit(context.active))
    }

  private def executeMBarrier(
      instruction: Instruction,
      context: VMContext,
      effects: VMWriteBuffer
  ): Either[Fault, Step] =
    (instruction: @unchecked) match {
      case Instruction.MBarrierLoad(address) =>
        advance(LaneFaults.everyLane(context)(lane =>
          sharedBarrierAddress(context, context.read(lane, address)).flatMap { (owner, canonical) =>
            if (owner.invalidatedMBarriers.contains(canonical))
              Left(Fault.Backend(s"invalidated mbarrier at shared address $canonical"))
            else if (owner.mbarriers.contains(canonical)) Right(())
            else MBarrier.fromStoredInitialState(owner.readShared(canonical, 64)).map(barrier =>
              owner.mbarriers(canonical) = barrier
            )
          }
        ))
      case Instruction.MBarrierInit(address, count) =>
        advance(LaneFaults.everyLane(context)(lane =>
          for {
            (owner, canonical) <- sharedBarrierAddress(context, context.read(lane, address))
            barrier            <- MBarrier.create(context.read(lane, count))
          } yield {
            val _ = owner.invalidatedMBarriers.subtractOne(canonical)
            owner.mbarriers(canonical) = barrier
          }
        ))
      case Instruction.MBarrierInvalidate(address) =>
        advance(LaneFaults.everyLane(context) { lane =>
          val raw = context.read(lane, address)
          for {
            (owner, canonical) <- sharedBarrierAddress(context, raw)
            _                  <- mbarrier(context, raw)
          } yield {
            val _ = owner.mbarriers.subtractOne(canonical)
            owner.invalidatedMBarriers(canonical) = ()
          }
        })
      case Instruction.MBarrierArrive(destination, address, count, expectedBytes, drop, complete) =>
        advance(LaneFaults.everyLane(context)(lane =>
          mbarrier(context, context.read(lane, address))
            .flatMap(_.arrive(context.read(lane, count), context.read(lane, expectedBytes), drop, complete))
            .map(token => destination.foreach(effects.stageRegister(lane, _, token)))
        ))
      case Instruction.MBarrierTest(destination, address, phase, parity) =>
        advance(LaneFaults.everyLane(context) { lane =>
          val token = context.read(lane, phase)
          for {
            barrier <- mbarrier(context, context.read(lane, address))
            _       <- Either.cond(
              !parity || token == 0L || token == 1L,
              (),
              Fault.Backend(s"invalid mbarrier phase parity: $token")
            )
          } yield effects.stage(
            VMWrite.Predicate(lane, destination, barrier.complete(if (parity) token else MBarrier.tokenParity(token)))
          )
        })
    }

  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  private def executeLock(instruction: Instruction, context: VMContext, effects: VMWriteBuffer): Either[Fault, Step] = {
    def owned(lane: WorkgroupLane, address: Value, width: ScalarBitWidth): Either[Fault, (VMWorkgroup, Long, Long)] = {
      val raw = context.read(lane, address)
      if (!VMAddressSpace.isShared(raw, width.toScalarByteWidth) || (raw & (width.toScalarByteWidth - 1L)) != 0)
        Left(Fault.Backend(s"invalid shared lock address: $raw"))
      else context.workgroup.sharedOwner(raw) match {
        case null               => Left(Fault.Backend(s"shared lock address names a CTA outside the cluster: $raw"))
        case owner: VMWorkgroup => Right((owner, raw, VMAddressSpace.sharedAddress(raw)))
      }
    }
    (instruction: @unchecked) match {
      case Instruction.SharedLockLoad(destination, result, address, width) =>
        advance(LaneFaults.everyLane(context)(lane =>
          owned(lane, address, width).map { (owner, raw, canonical) =>
            val free = owner.sharedLocks.get(canonical).forall(_ == lane.toInt)
            if (free) {
              owner.sharedLocks(canonical) = lane.toInt
              effects.stageRegister(lane, result, context.workgroup.readShared(raw, width))
            }
            effects.stage(VMWrite.Predicate(lane, destination, free))
          }
        ))
      case Instruction.SharedStoreUnlock(destination, address, source, width) =>
        advance(LaneFaults.everyLane(context)(lane =>
          owned(lane, address, width).map { (owner, raw, canonical) =>
            val held = owner.sharedLocks.get(canonical).contains(lane.toInt)
            if (held) {
              val _ = owner.sharedLocks.subtractOne(canonical)
              effects.stageShared(raw, width, context.read(lane, source))
            }
            effects.stage(VMWrite.Predicate(lane, destination, held))
          }
        ))
    }
  }

  private def executeCluster(
      instruction: Instruction,
      context: VMContext,
      effects: VMWriteBuffer
  ): Either[Fault, Step] =
    (instruction: @unchecked) match {
      case Instruction.ClusterBarrier(arrive)                         => Right(Step.ClusterBarrier(arrive))
      case Instruction.ClusterTryCancel(response, barrier, multicast) =>
        advance(LaneFaults.everyLane(context) { lane =>
          val responseAddress = context.read(lane, response)
          val barrierAddress  = context.read(lane, barrier)
          // The independent-workgroup scheduler reserves every CTA at launch, so none can be cancelled.
          // The opaque failed response still completes the requested asynchronous transaction.
          for {
            _ <- Either.cond(
              (responseAddress & 15L) == 0 && (barrierAddress & 7L) == 0,
              (),
              Fault.Misaligned("unaligned cluster cancellation response or barrier")
            )
            _ <- Vec.tabulate(if (multicast) context.workgroup.cluster.length else 1)(identity).traverse_ { rank =>
              val to        = if (multicast) VMAddressSpace.clusterAddress(responseAddress, rank) else responseAddress
              val signalled = if (multicast) VMAddressSpace.clusterAddress(barrierAddress, rank) else barrierAddress
              mbarrier(context, signalled).flatMap(_.completeBytes(16)).map { _ =>
                effects.stageShared(to, 64, 0L)
                effects.stageShared(to + 8L, 64, 0L)
              }
            }
          } yield ()
        })
    }

  private def executeAsyncCopy(
      instruction: Instruction,
      context: VMContext,
      effects: VMWriteBuffer
  ): Either[Fault, Step] =
    (instruction: @unchecked) match {
      case Instruction.AsyncBulkStore(destination, source, size, byteMask) =>
        advance(LaneFaults.everyLane(context) { lane =>
          val target = context.read(lane, destination)
          val origin = context.read(lane, source)
          val mask   = context.read(lane, byteMask)
          bulkTransfer("bulk store", target, origin, context.read(lane, size)).map { bytes =>
            (0L until bytes).foreach { offset =>
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
      case Instruction.AsyncCopy(destination, source, size, sourceSize) =>
        advance(LaneFaults.everyLane(context) { lane =>
          val target = context.read(lane, destination)
          val origin = context.read(lane, source)
          val count  = context.read(lane, sourceSize)
          for {
            _ <- Either.cond(
              count >= 0 && count <= size,
              (),
              Fault.Backend(s"cp.async source size $count exceeds copy size $size")
            )
            copied <- readDeviceBytes(context, lane, origin, count)
          } yield (0 until size).foreach(offset =>
            effects.stageShared(target + offset.toLong, 8, copied.lift(offset).getOrElse(0.toByte).toLong)
          )
        })
    }

  /** The dynamic stack grows down from the top of the frame's local window, which is the window's fixed size above its
    * aligned static storage, to that storage; stack pointers are frame-local addresses.
    */
  private def executeStack(
      instruction: Instruction,
      context: VMContext,
      effects: VMWriteBuffer
  ): Either[Fault, Step] = {
    val bottom                                       = (context.frame.routine.localBytes.toLong + 15L) & -16L
    val top                                          = bottom + VMAddressSpace.LocalFrameSize
    def overflow(lane: WorkgroupLane, pointer: Long) = Fault.Backend(
      s"lane $lane: stack pointer ${java.lang.Long.toUnsignedString(pointer)} is outside the " +
        s"${VMAddressSpace.LocalFrameSize}-byte stack of ${context.frame.routine.name}"
    )
    (instruction: @unchecked) match {
      case Instruction.StackAllocate(destination, size, alignment) =>
        advance(LaneFaults.everyLane(context) { lane =>
          val storage = context.lane(lane)
          val bytes   = context.read(lane, size)
          val base    = context.frame.localBase
          val pointer = ((base + top - storage.stack - bytes) & -alignment) - base
          if (java.lang.Long.compareUnsigned(bytes, VMAddressSpace.LocalFrameSize) > 0 || pointer < bottom)
            Left(overflow(lane, pointer))
          else {
            storage.stack = top - pointer
            Right(effects.stageRegister(lane, destination, pointer))
          }
        })
      case Instruction.StackSave(destination) =>
        context.active.foreachLane(lane => effects.stageRegister(lane, destination, top - context.lane(lane).stack))
        Advanced
      case Instruction.StackRestore(source) =>
        advance(LaneFaults.everyLane(context) { lane =>
          val pointer = context.read(lane, source)
          if (pointer < bottom || pointer > top) Left(overflow(lane, pointer))
          else Right(context.lane(lane).stack = top - pointer)
        })
    }
  }

  private def deviceCall(context: VMContext, address: Long, lanes: VMLaneMask): Either[Fault, DeviceCall] =
    context.kernel.functionTable.name(address) match {
      case None => Left(Fault.Backend(s"invalid function address 0x${java.lang.Long.toUnsignedString(address, 16)}"))
      case Some(name) =>
        context.kernel.functions.get(name).map(DeviceCall(
          _,
          lanes
        )).toRight(Fault.Backend(s"missing device function $name"))
    }
}
