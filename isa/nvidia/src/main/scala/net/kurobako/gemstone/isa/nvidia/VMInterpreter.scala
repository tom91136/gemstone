package net.kurobako.gemstone.isa.nvidia

import VMExecution.*
import VMIR.*
import cats.syntax.all.*
import java.util.concurrent.atomic.AtomicReference
import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
import scala.collection.mutable
import scala.util.boundary
import scala.util.boundary.break

private[isa] object VMInterpreter {

  private case class Cohort(pc: Pc, lanes: VMLaneMask, frame: VMFrame)
  private case class ClusterWaiting(cohort: Cohort, phase: Long, arrival: Boolean)

  /** Cluster barrier phases shared by the workgroups of one cluster, which a phase completes for once every live lane
    * of every member has arrived.
    */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private class ClusterSync {
    var phase                              = 0L
    var arrived                            = false
    var members: Vec[Execution]            = Vec.empty
    def completes(own: Execution): Boolean =
      if (members.isEmpty) own.clusterArrival
      else members.exists(_.clusterArrivals) && members.forall(_.clusterArrival)
  }

  // Frames compare by identity, so warp-scoped keys never match across activations.
  private enum BarrierKey derives CanEqual {
    case Workgroup(id: BarrierId)
    case Warp(base: WorkgroupLane.WarpBase, pc: Pc, frame: VMFrame)
    case Convergence(base: WorkgroupLane.WarpBase, token: Long, frame: VMFrame)

    def describe: String = this match {
      case Workgroup(id)                   => s"barrier $id"
      case Warp(base, pc, frame)           => s"warp $base synchronisation at ${frame.routine.name} pc $pc"
      case Convergence(base, token, frame) => s"warp $base convergence $token in ${frame.routine.name}"
    }
  }

  private case class Waiting(
      cohort: Cohort,
      key: BarrierKey,
      reduction: Option[BarrierReduction] = None,
      resumeOnArrival: Boolean = false
  )

  private class Block(val laneCount: Int in 1 ~ 1024) {
    val workgroup: VMWorkgroup = VMWorkgroup(laneCount)
    val frames: VMFramePool    = VMFramePool(laneCount)
    val effects: VMWriteBuffer = VMWriteBuffer(laneCount.widen[Int > 0] * 4)

    def reset(): Unit = {
      workgroup.reset()
      frames.reset()
      effects.clear()
    }
  }

  // Idle blocks are kept per processor and chosen by thread: one at least a warp wide and one narrower, so kernels that
  // launch narrow blocks between wide ones do not evict the wide block's storage. A slot hands a block to one borrower
  // at a time, and a reused block keeps only the storage its last workgroup needed.
  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  private val idle = Array.fill(sys.runtime.availableProcessors() * 2)(AtomicReference[Block | Null](null))

  private def slot(laneCount: Int in 1 ~ 1024): AtomicReference[Block | Null] =
    idle((PlatformConcurrency.threadId() % (idle.length / 2)).toInt * 2 + (if (laneCount < 32) 1 else 0))

  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  private def borrow(laneCount: Int in 1 ~ 1024): Block = slot(laneCount).getAndSet(null) match {
    case block: Block if block.laneCount == laneCount =>
      block.reset()
      block
    case _ => Block(laneCount)
  }

  private def release(block: Block): Unit = slot(block.laneCount).set(block)

  // One idle cluster's blocks per processor, handed to one borrower at a time like the single-block slots.
  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  private val idleClusters = Array.fill(sys.runtime.availableProcessors())(AtomicReference[Vec[Block] | Null](null))

  private def clusterSlot: AtomicReference[Vec[Block] | Null] =
    idleClusters((PlatformConcurrency.threadId() % idleClusters.length).toInt)

  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  private def borrow(laneCount: Int in 1 ~ 1024, size: Int): Vec[Block] = clusterSlot.getAndSet(null) match {
    case blocks: Vec[Block] if blocks.size == size && blocks.forall(_.laneCount == laneCount) =>
      blocks.foreach(_.reset())
      blocks
    case _ => Vec.fill(size)(Block(laneCount))
  }

  private def release(blocks: Vec[Block]): Unit = clusterSlot.set(blocks)

  /** Lays out `arguments` in the entry's parameter block. A longer argument contributes only its leading bytes. */
  def parameterBlock(entry: Entry, arguments: Map[NonEmpty[String], Argument]): Either[Fault, Array[Byte]] = {
    val bytes = Array.ofDim[Byte](entry.blockSize)
    val _     = entry.parameterBlock.copyToArray(bytes)
    entry.parameters.traverse_ { parameter =>
      arguments.get(parameter.name).toRight(Fault.InvalidRequest(s"missing kernel argument ${parameter.name}")).map(
        argument => argument.bytes.copyToArray(bytes, parameter.offset, parameter.widthBytes)
      )
    }.map(_ => bytes)
  }

  def execute(
      kernel: Kernel,
      geometry: LaunchGeometry,
      arguments: Map[NonEmpty[String], Argument],
      memory: AddressSpace,
      control: TaskControl = TaskControl(),
      workgroups: Workgroups = Workgroups.Sequential,
      dynamicSharedBytes: Int >= 0 = 0,
      cluster: Option[Dimensions] = None
  ): Either[Fault, Unit] =
    parameterBlock(kernel.entry, arguments).flatMap(block =>
      launch(kernel, geometry, block, DeviceHeap(memory), control, workgroups, dynamicSharedBytes, cluster)
    )

  /** The cluster shape a launch runs with: the launch's own, which must agree with any the kernel requires. */
  def clusterShape(kernel: Kernel, geometry: LaunchGeometry, requested: Option[Dimensions])
      : Either[Fault, Option[Dimensions]] = {
    val shape = requested.orElse(kernel.cluster)
    shape match {
      case Some(dimensions) if kernel.cluster.exists(_ != dimensions) =>
        Left(Fault.InvalidConfiguration(s"cluster shape $dimensions differs from the kernel's ${kernel.cluster.get}"))
      case Some(dimensions)
          if Axis.values.exists(axis => axis.select(geometry.groupCount).raw % axis.select(dimensions) != 0) =>
        Left(Fault.InvalidConfiguration(s"grid ${geometry.groupCount} is not a whole number of $dimensions clusters"))
      case Some(dimensions) if dimensions.x.raw * dimensions.y * dimensions.z > ClusterLimit =>
        Left(Fault.InvalidConfiguration(s"clusters are limited to $ClusterLimit workgroups, found $dimensions"))
      case _ => Right(shape)
    }
  }

  /** The most workgroups a cluster may hold, as the non-portable cluster size limit allows. */
  val ClusterLimit: Int = 16

  private val launchGrid: VMContext.Launch = launch(_, _, _, _, _, _, _)

  /** Runs every workgroup of one launch, including device-side launches sharing the parent's heap. */
  def launch(
      kernel: Kernel,
      geometry: LaunchGeometry,
      parameterBlock: Array[Byte],
      memory: DeviceHeap,
      control: TaskControl,
      workgroups: Workgroups,
      dynamicSharedBytes: Int >= 0,
      cluster: Option[Dimensions] = None
  ): Either[Fault, Unit] =
    for {
      laneCount <- geometry.groupVolume.refine[Int in 1 ~ 1024]
        .left.map(_ =>
          Fault.InvalidConfiguration(s"workgroups are limited to 1024 lanes, found ${geometry.groupVolume}")
        )
      shape <- clusterShape(kernel, geometry, cluster)
      allLanes = VMLaneMask.all(laneCount)
      _ <- shape match {
        case Some(dimensions) if dimensions != Dimensions.One =>
          clusters(kernel, geometry, dimensions, parameterBlock, memory, control, workgroups, dynamicSharedBytes)
        case _ =>
          Dispatch.forEachGroup(geometry, workgroups) { (_, group) =>
            val block = borrow(laneCount)
            try
              Execution(
                kernel,
                geometry,
                group,
                liveLanes(geometry, group, allLanes),
                parameterBlock,
                memory,
                control,
                workgroups,
                dynamicSharedBytes,
                block,
                shape
              ).run().left.map(_.at(Fault.Location(kernel.name, None, None, Some(group), None)))
            finally release(block)
          }
      }
    } yield ()

  // Steps each member between checks for cluster barrier phases that others completed.
  private val ClusterSlice = 64

  /** Runs each cluster's workgroups together on one thread, sharing their shared memory and cluster barrier. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def clusters(
      kernel: Kernel,
      geometry: LaunchGeometry,
      shape: Dimensions,
      parameterBlock: Array[Byte],
      memory: DeviceHeap,
      control: TaskControl,
      workgroups: Workgroups,
      dynamicSharedBytes: Int >= 0
  ): Either[Fault, Unit] = {
    val laneCount = geometry.groupVolume.unsafe[Int in 1 ~ 1024]
    val allLanes  = VMLaneMask.all(laneCount)
    val count     = Dimensions(
      (geometry.groupCount.x.raw / shape.x).unsafe[Int > 0],
      (geometry.groupCount.y.raw / shape.y).unsafe[Int > 0],
      (geometry.groupCount.z.raw / shape.z).unsafe[Int > 0]
    )
    val size = shape.x.raw * shape.y.raw * shape.z.raw
    LaunchGeometry.forLaunch(geometry.groupSize, count).left.map(Fault.InvalidRequest(_)).flatMap { grid =>
      Dispatch.forEachGroup(grid, workgroups) { (_, origin) =>
        val blocks = borrow(laneCount, size)
        try {
          val sync   = ClusterSync()
          val groups = Vec.tabulate(size) { rank =>
            Coordinates(
              (origin.x.raw * shape.x.raw + rank                 % shape.x.raw).unsafe[Int >= 0],
              (origin.y.raw * shape.y.raw + (rank / shape.x.raw) % shape.y.raw).unsafe[Int >= 0],
              (origin.z.raw * shape.z.raw + rank / (shape.x.raw * shape.y.raw)).unsafe[Int >= 0]
            )
          }
          val shared: Array[VMWorkgroup | Null] = Array.from(blocks.map(_.workgroup))
          blocks.zipWithIndex.foreach { (block, rank) =>
            block.workgroup.cluster = shared
            block.workgroup.clusterRank = rank
            // The driver readies the reserved mbarrier ptxas's CTA-pair tcgen05.alloc hands the address over with: the
            // even CTA's has completed its first phase, so it allocates at once, and the odd CTA's awaits the hand-over.
            kernel.pairAllocationBarrier.foreach { offset =>
              MBarrier.create(1L).foreach { barrier =>
                if (rank % 2 == 0) { val _ = barrier.arrive(count = 1L, expectedBytes = 0L) }
                block.workgroup.mbarriers(VMAddressSpace.SharedBase + offset) = barrier
              }
            }
          }
          val members = groups.zip(blocks).map((group, block) =>
            Execution(
              kernel,
              geometry,
              group,
              liveLanes(geometry, group, allLanes),
              parameterBlock,
              memory,
              control,
              workgroups,
              dynamicSharedBytes,
              block,
              Some(shape),
              sync
            )
          )
          sync.members = members
          members.foreach(_.begin())
          var failure: Option[Fault] = None
          var pending                = true
          while (failure.isEmpty && pending) {
            var progressed = false
            members.foreach { member =>
              if (failure.isEmpty && !member.finished) {
                val runnable = member.runnable
                member.slice(ClusterSlice).left.foreach(fault => failure = Some(member.fault(fault)))
                progressed ||= runnable || member.runnable
              }
            }
            pending = members.exists(!_.finished)
            if (pending && !progressed && failure.isEmpty) {
              val waits = members.flatMap(_.describeWaits).distinct.sorted
              failure = Some(Fault.Hang(s"execution deadlocked: ${kernel.name}: ${
                  if (waits.nonEmpty) waits.mkString(", ") else "cluster members wait on one another"
                }"))
            }
          }
          failure.toLeft(())
        } finally release(blocks)
      }
    }
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def liveLanes(geometry: LaunchGeometry, group: Coordinates, allLanes: VMLaneMask): VMLaneMask =
    if (geometry.isFull(group)) allLanes
    else {
      val live            = VMLaneMask.Builder()
      var start: Int >= 0 = 0
      while (start < geometry.groupVolume) {
        val count = math.min(64, geometry.groupVolume.raw - start).unsafe[Int in 1 ~ 64]
        val bits  = geometry.liveLanes(group, start, count)
        (0 until count).foreach(bit =>
          if (((bits >>> bit) & 1L) != 0L) live.add(WorkgroupLane.fromIndex(start.raw + bit))
        )
        start += 64
      }
      live.result()
    }

  private def warpMemberMask(instruction: Instruction): Option[Value] = instruction match {
    case Instruction.Shuffle(memberMask = memberMask)         => Some(memberMask)
    case Instruction.VoteBallot(memberMask = memberMask)      => Some(memberMask)
    case Instruction.Elect(_, _, memberMask)                  => Some(memberMask)
    case Instruction.WarpMatch(memberMask = memberMask)       => Some(memberMask)
    case Instruction.WarpReduceFloat(memberMask = memberMask) => Some(memberMask)
    case Instruction.VotePredicate(memberMask = memberMask)   => Some(memberMask)
    case Instruction.WarpReduce(memberMask = memberMask)      => Some(memberMask)
    // .sync.aligned forms run once all of a warp's live lanes reach them
    case _: (Instruction.Tensor | Instruction.TensorMemoryAllocate | Instruction.TensorMemoryDeallocate |
          Instruction.TensorMemoryLoad | Instruction.TensorMemoryStore) | Instruction.TensorMemoryRelinquish =>
      Some(Value.Immediate(0xffffffffL))
    case _ => None
  }

  private def warpgroupWide(instruction: Instruction): Boolean = instruction match {
    case Instruction.Tensor(_: Matrix.Operation.WarpgroupMultiply) => true
    case _                                                         => false
  }

  private def inFunction(fault: Fault, name: NonEmpty[String]): Fault = fault match {
    case Fault.Memory(detail)             => Fault.Memory(s"$name: $detail")
    case Fault.Backend(detail)            => Fault.Backend(s"$name: $detail")
    case Fault.IllegalInstruction(detail) => Fault.IllegalInstruction(s"$name: $detail")
    case Fault.Trap(detail)               => Fault.Trap(s"$name: $detail")
    case Fault.Assertion(detail)          => Fault.Assertion(s"$name: $detail")
    case Fault.Misaligned(detail)         => Fault.Misaligned(s"$name: $detail")
    case other                            => other
  }

  // Cohort and barrier state model the SIMT machine directly; the first fault ends the workgroup.
  @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
  private class Execution(
      kernel: Kernel,
      geometry: LaunchGeometry,
      group: Coordinates,
      initialLive: VMLaneMask,
      parameterBlock: Array[Byte],
      memory: DeviceHeap,
      control: TaskControl,
      workgroups: Workgroups,
      dynamicSharedBytes: Int >= 0,
      block: Block,
      cluster: Option[Dimensions] = None,
      sync: ClusterSync = ClusterSync()
  ) {
    private val workgroup = block.workgroup
    private val effects   = block.effects
    private val root = VMFrame(kernel.entry.routine, block.frames.acquire(kernel.entry.routine), 0L, None, initialLive)
    private val context = VMContext(
      kernel,
      geometry,
      group,
      workgroup,
      memory,
      control,
      workgroups,
      dynamicSharedBytes,
      effects,
      root,
      launchGrid,
      cluster.getOrElse(Dimensions.One),
      cluster.nonEmpty
    )
    private var live         = initialLive
    private val ready        = mutable.ArrayDeque.empty[Cohort]
    private var stalled      = 0
    private val waiting      = mutable.ArrayBuffer.empty[Waiting]
    private val barrierMasks = mutable.Map.empty[BarrierKey, VMLaneMask]
    private val operations   = mutable.Map.empty[BarrierKey, Option[Option[PredicateOperation]]]
    private val counts       = mutable.Map.empty[BarrierKey, Int]
    private val warpMembers  = mutable.Map.empty[BarrierKey, Long]

    private val warpArrivals       = Array.ofDim[VMLaneMask.Builder | Null]((workgroup.laneCount + 31) >>> 5)
    private val warpArrivalMembers = Array.ofDim[Long](warpArrivals.length)

    private lazy val clusterWaiting     = mutable.ArrayBuffer.empty[ClusterWaiting]
    private lazy val clusterLanePhase   = Array.fill(workgroup.laneCount)(-1L)
    private lazy val clusterWaitedPhase = Array.fill(workgroup.laneCount)(-1L)
    private var clusterArrived          = VMLaneMask.Empty

    def clusterArrivals: Boolean = clusterArrived.nonEmpty
    def clusterArrival: Boolean  = clusterArrived.intersect(live) == live

    private type Run = boundary.Label[Either[Fault, Unit]]

    private def fail(fault: Fault)(using Run): Nothing = break(Left(fault))

    private val observing = control.observer.isDefined

    private def observe(frame: VMFrame, pc: Pc, active: VMLaneMask)(using Run): Unit = control.observer.foreach {
      observer =>
        val at = location(frame, pc, active)
        observer.instruction(at, View(frame, active)) match {
          case Left(fault) => fail(fault.at(at))
          case Right(())   => ()
        }
    }

    private class View(frame: VMFrame, active: VMLaneMask) extends CohortView {
      def function: NonEmpty[String] = frame.routine.name
      def lanes: Vec[Int >= 0]       = {
        val indices = Vec.newBuilder[Int >= 0]
        active.foreachLane(lane => indices += lane.toInt.unsafe[Int >= 0])
        indices.result()
      }
      def register(lane: Int >= 0, slot: Int >= 0): Long =
        frame.lanes(WorkgroupLane.fromIndex(lane)).register(Register(slot))
      def predicate(lane: Int >= 0, slot: Int >= 0): Option[Boolean] =
        frame.lanes(WorkgroupLane.fromIndex(lane)).predicate(Predicate(slot))
    }

    // A cohort executes its lanes together, so only a lone active lane identifies the faulting one.
    private def location(frame: VMFrame, pc: Pc, active: VMLaneMask): Fault.Location = Fault.Location(
      kernel.name,
      Some(frame.routine.name),
      frame.routine.program.source(pc),
      Some(group),
      active.positiveSize.filter(_ == 1).flatMap(_ => active.headOption).map(_.toInt.unsafe[Int >= 0])
    )

    private def check(result: Either[Fault, Unit])(using Run): Unit = result match {
      case Left(fault) => fail(fault)
      case _           => ()
    }

    private def enqueue(cohort: Cohort): Unit = {
      val lanes = cohort.lanes.intersect(live)
      if (lanes.nonEmpty) {
        var existing = 0
        while (
          existing < ready.size &&
          (ready(existing).pc.toInt != cohort.pc.toInt || (ready(existing).frame ne cohort.frame))
        )
          existing += 1
        if (existing == ready.size) ready += (if (lanes eq cohort.lanes) cohort else cohort.copy(lanes = lanes))
        else ready(existing) = ready(existing).copy(lanes = ready(existing).lanes.union(lanes))
      }
    }

    private def advance(cohort: Cohort, lanes: VMLaneMask)(using Run): Unit =
      if (cohort.pc.toInt == Int.MaxValue) fail(Fault.Backend("program counter overflow"))
      else {
        stalled = 0
        enqueue(cohort.copy(pc = Pc(cohort.pc.unwrap + 1), lanes = lanes))
      }

    private def enterCall(
        cohort: Cohort,
        call: DeviceCall,
        parameters: Vec[Parameter],
        returnParameters: Vec[Parameter]
    )(using Run): Unit = {
      val function = call.function
      val caller   = cohort.frame
      if (parameters.size != function.parameters.size)
        fail(Fault.Backend(
          s"device function ${function.routine.name} expects ${function.parameters.size} parameters, found ${parameters.size}"
        ))
      if (returnParameters.size != function.returnParameters.size)
        fail(Fault.Backend(
          s"device function ${function.routine.name} returns ${function.returnParameters.size} parameters, found ${returnParameters.size}"
        ))
      val lanes = block.frames.acquire(function.routine)
      call.lanes.foreachLane { lane =>
        val source = caller.lanes(lane)
        val target = lanes(lane)
        var index  = 0
        while (index < parameters.size) {
          source.copyParameter(parameters(index), target, function.parameters(index))
          index += 1
        }
      }
      // Each frame has its own window, so pointers into a caller frame stay valid.
      val localBase = caller.localBase + VMAddressSpace.LocalFrameSize +
        ((caller.routine.localBytes.toLong + 15L) & -16L)
      if (
        localBase + math.max(VMAddressSpace.LocalFrameSize, function.routine.localBytes.toLong) >
          VMAddressSpace.SharedBase
      )
        fail(Fault.OutOfResources(s"call depth exceeds the local memory window at ${function.routine.name}"))
      val frame = VMFrame(
        function.routine,
        lanes,
        localBase,
        Some(VMFrame.Continuation(caller, cohort.pc, returnParameters, function.returnParameters)),
        call.lanes
      )
      enqueue(Cohort(Pc.Entry, call.lanes, frame))
    }

    private def synchroniseWarpInstruction(memberMask: Value, cohort: Cohort): (VMLaneMask, VMLaneMask) = {
      context.frame = cohort.frame
      context.active = cohort.lanes
      val runnable = cohort.lanes.filterLanes { lane =>
        val expected = cohort.frame.live.warpBits(lane.raw) & live.warpBits(lane.raw) & context.read(lane, memberMask)
        (expected & ~cohort.lanes.warpBits(lane.raw)) == 0L
      }
      runnable -> cohort.lanes.subtract(runnable)
    }

    private def synchroniseWarpgroupInstruction(cohort: Cohort): (VMLaneMask, VMLaneMask) = {
      val runnable = cohort.lanes.filterLanes { lane =>
        val first = lane & -Warpgroup.Lanes
        var base  = first
        var ready = true
        while (ready && base < first + Warpgroup.Lanes) {
          ready = (cohort.frame.live.warpBits(base) & live.warpBits(base) & ~cohort.lanes.warpBits(base)) == 0L
          base += 32
        }
        ready
      }
      runnable -> cohort.lanes.subtract(runnable)
    }

    private def clusterPhase: Long = sync.phase

    private def releaseCluster()(using Run): Unit =
      if (sync.arrived) {
        if (sync.completes(this) && (clusterArrived.nonEmpty || sync.members.nonEmpty)) {
          sync.phase += 1L
          if (sync.members.isEmpty) clusterArrived = VMLaneMask.Empty
          else sync.members.foreach(_.clusterArrived = VMLaneMask.Empty)
        }
        val pending = clusterWaiting.toVector
        clusterWaiting.clear()
        pending.foreach { entry =>
          val participants = if (entry.arrival) clusterArrived
          else pending.filter(value => !value.arrival && value.phase == entry.phase)
            .foldLeft(VMLaneMask.Empty)((mask, value) => mask.union(value.cohort.lanes))
          val released = entry.cohort.lanes.intersect(live).filterLanes { lane =>
            val warp = live.warpBits(lane.raw)
            if (entry.arrival && entry.phase < clusterPhase) true
            else (entry.arrival || entry.phase < clusterPhase) && (participants.warpBits(lane.raw) & warp) == warp
          }
          if (!entry.arrival) released.foreachLane(lane => clusterWaitedPhase(lane.raw) = entry.phase)
          advance(entry.cohort, released)
          val remaining = entry.cohort.lanes.intersect(live).subtract(released)
          if (remaining.nonEmpty) clusterWaiting += entry.copy(cohort = entry.cohort.copy(lanes = remaining))
        }
      }

    private def expected(key: BarrierKey): VMLaneMask = {
      def warp(base: WorkgroupLane.WarpBase, frame: VMFrame, members: Long): VMLaneMask =
        frame.live.intersect(live).filterLanes(lane =>
          (lane.raw & ~31) == base.raw && ((members & 0xffffffffL) & (1L << (lane.raw & 31))) != 0L
        )
      key match {
        case BarrierKey.Workgroup(_)                    => live
        case BarrierKey.Warp(base, _, frame)            => warp(base, frame, warpMembers.getOrElse(key, -1L))
        case BarrierKey.Convergence(base, token, frame) =>
          warp(base, frame, workgroup.convergenceMask(base, token))
      }
    }

    private def participants(key: BarrierKey, arrived: VMLaneMask): Option[VMLaneMask] = {
      val count = counts.getOrElse(key, 0)
      if (count > 0) {
        var selected = VMLaneMask.Empty
        var threads  = 0
        var warp     = 0
        while (warp < workgroup.laneCount && threads < count) {
          val members = live.warpBits(warp)
          if (arrived.warpBits(warp) != 0L && (arrived.warpBits(warp) & members) == members) {
            selected = selected.union(arrived.filterLanes(lane => (lane.raw & ~31) == warp))
            threads += 32
          }
          warp += 32
        }
        Option.when(threads == count)(selected)
      } else {
        val required = expected(key)
        Option.when(arrived.intersect(required) == required)(arrived)
      }
    }

    private def reduce(released: Vec[Waiting])(using Run): Unit =
      released.view.flatMap(_.reduction).headOption.foreach { first =>
        var reduced = first.result.combination.contains(PredicateOperation.And)
        var count   = 0L
        released.foreach { waiter =>
          waiter.reduction.foreach { reduction =>
            context.frame = waiter.cohort.frame
            waiter.cohort.lanes.foreachLane { lane =>
              context.predicate(lane, reduction.predicate) match {
                case Left(fault) => fail(fault)
                case Right(set)  =>
                  val value = set != reduction.negated
                  if (value) count += 1L
                  reduced = reduction.result.combination match {
                    case Some(PredicateOperation.And) => reduced && value
                    case Some(PredicateOperation.Or)  => reduced || value
                    case Some(PredicateOperation.Xor) => reduced ^ value
                    case None                         => reduced
                  }
              }
            }
          }
        }
        released.foreach(waiter =>
          waiter.reduction.foreach(reduction =>
            reduction.result match {
              case BarrierResult.Combined(destination, _) =>
                waiter.cohort.lanes.foreachLane(lane =>
                  waiter.cohort.frame.lanes(lane).setPredicate(destination, reduced)
                )
              case BarrierResult.Count(destination) =>
                waiter.cohort.lanes.foreachLane(lane => waiter.cohort.frame.lanes(lane).setRegister(destination, count))
            }
          )
        )
      }

    private def releaseBarrier(key: BarrierKey)(using Run): Unit = {
      val arrived                            = barrierMasks.getOrElse(key, VMLaneMask.Empty)
      def canResume(value: Waiting): Boolean =
        value.key == key && value.resumeOnArrival && value.cohort.lanes.filterLanes { lane =>
          val members = live.warpBits(lane.raw)
          (arrived.warpBits(lane.raw) & members) != members
        }.isEmpty
      val resumed = Vec.from(waiting.filter(canResume(_)))
      waiting.filterInPlace(value => !canResume(value))
      resumed.foreach(value => advance(value.cohort, value.cohort.lanes))
      participants(key, arrived).foreach { participants =>
        val released =
          Vec.from(waiting.filter(value => value.key == key && value.cohort.lanes.intersect(participants).nonEmpty))
        waiting.filterInPlace(value => value.key != key || value.cohort.lanes.intersect(participants).isEmpty)
        val remaining = arrived.subtract(participants)
        if (remaining.nonEmpty) barrierMasks(key) = remaining
        else {
          val _ = barrierMasks.remove(key)
          val _ = counts.remove(key)
          val _ = operations.remove(key)
        }
        val _ = warpMembers.remove(key)
        // A completed convergence token is no longer live. A legacy target can be reached along a path that skipped
        // the next SSY using this slot; it must not wait for the previous token's members.
        key match {
          case BarrierKey.Convergence(base, token, _) =>
            workgroup.completeConvergence(base, token)
          case _ => ()
        }
        reduce(released)
        released.foreach(value => advance(value.cohort, value.cohort.lanes))
      }
    }

    private def releaseAll()(using Run): Unit =
      if (barrierMasks.nonEmpty) Vec.from(barrierMasks.keys).foreach(releaseBarrier(_))

    private def arriveWorkgroup(
        cohort: Cohort,
        arrival: WorkgroupArrival,
        reduction: Option[BarrierReduction],
        resume: Boolean
    )(
        using Run
    ): Unit = {
      val key       = BarrierKey.Workgroup(arrival.id)
      val operation = reduction.map(_.result.combination)
      counts.get(key) match {
        case Some(previous) if previous != arrival.count.raw =>
          fail(Fault.Backend(
            s"barrier ${arrival.id} thread count changed from $previous to ${arrival.count} before completion"
          ))
        case _ if operations.get(key).exists(_ != operation) =>
          fail(Fault.Backend(s"barrier ${arrival.id} mixes incompatible reduction and synchronisation operations"))
        case _ if barrierMasks.getOrElse(key, VMLaneMask.Empty).intersect(arrival.lanes).nonEmpty =>
          fail(Fault.Backend(s"barrier ${arrival.id} receives a repeated arrival before completion"))
        case _ =>
          counts(key) = arrival.count
          operations(key) = operation
          waiting += Waiting(cohort.copy(lanes = arrival.lanes), key, reduction, resume)
          barrierMasks(key) = barrierMasks.getOrElse(key, VMLaneMask.Empty).union(arrival.lanes)
          releaseBarrier(key)
      }
    }

    private def arriveWarp(cohort: Cohort, lanes: VMLaneMask, key: BarrierKey)(using Run): Unit = {
      waiting += Waiting(cohort.copy(lanes = lanes), key)
      barrierMasks(key) = barrierMasks.getOrElse(key, VMLaneMask.Empty).union(lanes)
      releaseBarrier(key)
    }

    private inline def byWarp(lanes: VMLaneMask)(inline key: WorkgroupLane => Long)
        : Vec[((WorkgroupLane.WarpBase, Long), VMLaneMask)] = {
      val groups = mutable.Map.empty[(WorkgroupLane.WarpBase, Long), VMLaneMask.Builder]
      lanes.foreachLane(lane =>
        groups.getOrElseUpdate((WorkgroupLane.warpBases(lane >>> 5), key(lane)), VMLaneMask.Builder()).add(lane)
      )
      Vec.from(groups).map((group, builder) => group -> builder.result()).sortBy((group, _) => (group._1, group._2))
    }

    private def clusterBarrier(cohort: Cohort, active: VMLaneMask, arrival: Boolean)(using Run): Unit = {
      var invalid = false
      active.foreachLane { lane =>
        if (arrival) {
          if (clusterLanePhase(lane.raw) > clusterWaitedPhase(lane.raw)) invalid = true
          clusterLanePhase(lane.raw) = clusterPhase
        } else if (clusterLanePhase(lane.raw) < 0) invalid = true
      }
      if (invalid) fail(Fault.Backend("cluster barrier requires one arrival before each wait"))
      if (arrival) {
        sync.arrived = true
        clusterArrived = clusterArrived.union(active)
        clusterWaiting += ClusterWaiting(cohort.copy(lanes = active), clusterPhase, arrival = true)
      } else {
        val phases = mutable.LongMap.empty[VMLaneMask.Builder]
        active.foreachLane(lane => phases.getOrElseUpdate(clusterLanePhase(lane.raw), VMLaneMask.Builder()).add(lane))
        phases.foreach((phase, lanes) =>
          clusterWaiting += ClusterWaiting(cohort.copy(lanes = lanes.result()), phase, false)
        )
      }
      releaseCluster()
    }

    private def complete(cohort: Cohort, mask: VMLaneMask)(using Run): Unit = {
      val frame = cohort.frame
      frame.live = frame.live.subtract(mask)
      frame.continuation match {
        case None               => live = live.subtract(mask)
        case Some(continuation) =>
          mask.foreachLane { lane =>
            val callee = frame.lanes(lane)
            val caller = continuation.caller.lanes(lane)
            var index  = 0
            while (index < continuation.sources.size) {
              callee.copyParameter(continuation.sources(index), caller, continuation.targets(index))
              index += 1
            }
          }
          // A lane is in exactly one cohort, running, ready or waiting, and leaves a frame's live set only by returning
          // or exiting from it. Once none are live, no cohort or waiter can run in or read this frame again.
          if (frame.live.isEmpty) block.frames.release(frame.lanes)
          advance(Cohort(continuation.pc, mask, continuation.caller), mask)
      }
    }

    private def step(cohort: Cohort, instruction: PredicatedInstruction)(using Run): Unit = {
      val frame = cohort.frame
      cohort.lanes.positiveSize match {
        case Some(size) => check(control.checkpoint(size.widen[Int > 0]))
        case None       => ()
      }
      val active = instruction.predication match {
        case None              => cohort.lanes
        case Some(predication) =>
          cohort.lanes.filterLanes(lane =>
            frame.lanes(lane).predicate(predication.predicate) match {
              case Some(value) => value != predication.negated
              case None        =>
                fail(Fault.Backend(
                  s"lane $lane: undefined ${frame.routine.describe(predication.predicate)} at pc ${cohort.pc}"
                ))
            }
          )
      }
      val skipped = if (instruction.predication.isEmpty) VMLaneMask.Empty else cohort.lanes.subtract(active)
      context.issued += 1L
      context.frame = frame
      context.pc = cohort.pc
      context.active = active
      if (observing && active.nonEmpty) observe(frame, cohort.pc, active)
      effects.clear()
      val outcome = if (active.isEmpty) Advanced else VMSemantics.execute(instruction.instruction, LaneLoop(context))
      val result  = outcome match {
        case Right(_) =>
          effects.commit(frame.lanes, workgroup, memory, cohort.pc) match {
            case Left(fault) => Left(fault)
            case _           => outcome
          }
        case failure => failure
      }
      result match {
        case Left(fault)         => fail(inFunction(fault, frame.routine.name).at(location(frame, cohort.pc, active)))
        case Right(Step.Advance) =>
          advance(cohort, cohort.lanes)
          instruction.instruction match {
            case Instruction.Convergence(_, ConvergenceAction.Break) =>
              Vec.from(barrierMasks.keys.collect { case key: BarrierKey.Convergence => key }).foreach(releaseBarrier(_))
            case _ => ()
          }
        case Right(Step.Jump(target)) =>
          enqueue(cohort.copy(pc = target, lanes = active))
          advance(cohort, skipped)
        case Right(Step.Branches(arms, fallThrough)) =>
          var arm = 0
          while (arm < arms.size) {
            enqueue(cohort.copy(pc = arms(arm).target, lanes = arms(arm).lanes))
            arm += 1
          }
          advance(cohort, skipped.union(fallThrough))
        case Right(Step.ClusterBarrier(arrival)) =>
          advance(cohort, skipped)
          clusterBarrier(cohort, active, arrival)
        case Right(Step.WorkgroupBarrier(arrivals, blocking, reduction)) =>
          advance(cohort, skipped)
          arrivals.foreach(arriveWorkgroup(cohort, _, reduction, !blocking))
        case Right(Step.WarpSync(memberMask)) =>
          advance(cohort, skipped)
          active.foreachLane { lane =>
            val warp = lane >>> 5
            if (warpArrivals(warp) == null) {
              warpArrivals(warp) = VMLaneMask.Builder()
              warpArrivalMembers(warp) = context.read(lane, memberMask)
            }
            warpArrivals(warp).nn.add(lane)
          }
          var warp = 0
          while (warp < warpArrivals.length) {
            if (warpArrivals(warp) != null) {
              val key = BarrierKey.Warp(WorkgroupLane.warpBases(warp), cohort.pc, frame)
              // One box per warp arrival, alongside the key allocated for it.
              warpMembers(key) = warpArrivalMembers(warp): @SuppressWarnings(Array("baumkuchen:loop-boxing"))
              val lanes = warpArrivals(warp).nn.result()
              warpArrivals(warp) = null
              arriveWarp(cohort, lanes, key)
            }
            warp += 1
          }
        case Right(Step.ConvergenceSync(token)) =>
          advance(cohort, skipped)
          byWarp(active)(frame.lanes(_).register(token)).foreach { case ((base, value), lanes) =>
            arriveWarp(cohort, lanes, BarrierKey.Convergence(base, value, frame))
          }
        case Right(Step.Call(calls, parameters, returnParameters)) =>
          advance(cohort, skipped)
          calls.foreach(enterCall(cohort, _, parameters, returnParameters))
        case Right(Step.Return(mask)) =>
          complete(cohort, mask)
          advance(cohort, skipped)
          releaseCluster()
          releaseAll()
        case Right(Step.Exit(mask)) =>
          frame.live = frame.live.subtract(mask)
          live = live.subtract(mask)
          advance(cohort, skipped)
          releaseCluster()
          releaseAll()
      }
    }

    private def start(): Unit = {
      val _ = workgroup.allocateBuffer(parameterBlock.clone())
      enqueue(Cohort(Pc.Entry, initialLive, root))
    }

    def finished: Boolean = live.isEmpty
    def runnable: Boolean = ready.nonEmpty && !stuck

    def describeWaits: Vec[String] =
      Vec.from(waiting.map(value =>
        s"${value.key.describe}, ${value.cohort.frame.routine.name} pc ${value.cohort.pc}"
      )) ++
        (if (stuck) Vec.from(ready.map(value => s"collective, ${value.frame.routine.name} pc ${value.pc}"))
         else Vec.empty)

    private def deadlock()(using Run): Nothing = {
      val waits = describeWaits.distinct.sorted
      fail(Fault.Hang(
        s"execution deadlocked: ${kernel.name}: ${if (waits.nonEmpty) waits.mkString(", ") else "no runnable cohort"}"
      ))
    }

    private def next()(using Run): Unit = {
      var cohort = ready.removeHead()
      val frame  = cohort.frame
      frame.live = frame.live.intersect(live)
      val surviving = cohort.lanes.intersect(live)
      if (surviving ne cohort.lanes) cohort = cohort.copy(lanes = surviving)
      val instructions = frame.routine.program.instructions
      if (cohort.pc.toInt >= instructions.size)
        fail(Fault.Backend(s"program counter ${cohort.pc} is outside a ${instructions.size}-instruction program"))
      val instruction = instructions(cohort.pc.toInt)
      warpMemberMask(instruction.instruction) match {
        case Some(memberMask) =>
          var index = 0
          while (index < ready.size)
            if (ready(index).pc.toInt == cohort.pc.toInt && (ready(index).frame eq frame))
              cohort = cohort.copy(lanes = cohort.lanes.union(ready.remove(index).lanes))
            else index += 1
          val (runnable, delayed) =
            if (warpgroupWide(instruction.instruction)) synchroniseWarpgroupInstruction(cohort)
            else synchroniseWarpInstruction(memberMask, cohort)
          if (delayed.nonEmpty) enqueue(cohort.copy(lanes = delayed))
          if (runnable ne cohort.lanes) cohort = cohort.copy(lanes = runnable)
        case None => ()
      }
      if (cohort.lanes.isEmpty) stalled += 1
      else {
        stalled = 0
        step(cohort, instruction)
      }
    }

    /** Every ready cohort has waited in turn for lanes that have not moved since. */
    private def stuck: Boolean = stalled > ready.size

    def run(): Either[Fault, Unit] = boundary {
      start()
      while (live.nonEmpty) {
        if (ready.isEmpty || stuck) deadlock()
        next()
      }
      Right(())
    }

    def begin(): Unit = start()

    /** Runs at most `budget` steps after releasing cluster barrier phases other members completed. */
    def slice(budget: Int): Either[Fault, Unit] = boundary {
      releaseCluster()
      var steps = 0
      while (steps < budget && live.nonEmpty && runnable) {
        next()
        steps += 1
      }
      releaseCluster()
      Right(())
    }

    def fault(error: Fault): Fault = error.at(Fault.Location(kernel.name, None, None, Some(group), None))
  }
}
