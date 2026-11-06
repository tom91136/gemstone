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

  // The unit a cohort runs as: its identity follows it as it splits, parks and finishes, so a timeline can draw it.
  // A cohort in a partition runs apart from the other partitions of its warp until a convergence barrier spans them.
  private case class Cohort(
      id: Int >= 0,
      pc: Pc,
      lanes: VMLaneMask,
      frame: VMFrame,
      partition: VMLaneMask = VMLaneMask.Empty
  )
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
    case Warp(base: WorkgroupLane.WarpBase, pc: Pc, frame: VMFrame, members: Long)
    case Convergence(base: WorkgroupLane.WarpBase, token: Long, frame: VMFrame)

    def describe: String = this match {
      case Workgroup(id)                   => s"barrier $id"
      case Warp(base, pc, frame, _)        => s"warp $base synchronisation at ${frame.routine.name} pc $pc"
      case Convergence(base, token, frame) => s"warp $base convergence $token in ${frame.routine.name}"
    }
  }

  private case class Waiting(
      cohort: Cohort,
      key: BarrierKey,
      reduction: Option[BarrierReduction] = None,
      resumeOnArrival: Boolean = false
  )

  /** Cohorts ready to run in arrival order, at most one per frame and pc: a cohort arriving where one is already queued
    * joins its lanes. An index by frame and pc finds that cohort without scanning the queue, and a cohort taken from
    * the middle leaves an empty slot that is dropped when it reaches the head. The index is an open-addressing table
    * that deletes by shifting entries back, so steady-state adds and removals allocate nothing; it doubles only when
    * more cohorts are queued at once than ever before.
    */
  @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
  private class ReadyQueue {
    private class Slot {
      var cohort: Cohort | Null = null
      var indexed               = true
    }

    private val order  = mutable.ArrayDeque.empty[Slot]
    private val spare  = mutable.ArrayBuffer.empty[Slot]
    private var frames = 0
    private var count  = 0

    // a key is never zero because frame handles start at one, so zero marks a free entry
    private var keys  = Array.ofDim[Long](8)
    private var tags  = Array.ofDim[Int](8)
    private var slots = Array.ofDim[Slot | Null](8)
    private var shift = 61

    def size: Int         = count
    def isEmpty: Boolean  = count == 0
    def nonEmpty: Boolean = count != 0

    private def key(pc: Pc, frame: VMFrame): Long = {
      if (frame.readyId == 0) { frames += 1; frame.readyId = frames }
      (frame.readyId.toLong << 32) | (pc.toInt.toLong & 0xffffffffL)
    }

    // cohorts of different partitions at one pc stay apart, so a partition's tag is its first lane plus one
    private def tag(partition: VMLaneMask): Int = partition.headOption.fold(0)(_.raw + 1)

    private def home(at: Long): Int = ((at * 0x9e3779b97f4a7c15L) >>> shift).toInt

    /** The table position of the key and partition tag, or -1 when it is absent. */
    private def find(at: Long, tag: Int): Int = {
      val mask = keys.length - 1
      var i    = home(at)
      while (keys(i) != 0L && (keys(i) != at || tags(i) != tag)) i = (i + 1) & mask
      if (keys(i) != 0L) i else -1
    }

    private def place(at: Long, tag: Int, slot: Slot): Unit = {
      val mask = keys.length - 1
      var i    = home(at)
      while (keys(i) != 0L) i = (i + 1) & mask
      keys(i) = at
      tags(i) = tag
      slots(i) = slot
    }

    private def put(at: Long, tag: Int, slot: Slot): Unit = {
      if ((count + 1) * 2 > keys.length) {
        val oldKeys  = keys
        val oldTags  = tags
        val oldSlots = slots
        keys = Array.ofDim[Long](oldKeys.length * 2)
        tags = Array.ofDim[Int](oldKeys.length * 2)
        slots = Array.ofDim[Slot | Null](oldKeys.length * 2)
        shift -= 1
        var i = 0
        while (i < oldKeys.length) {
          if (oldKeys(i) != 0L) place(oldKeys(i), oldTags(i), oldSlots(i).nn)
          i += 1
        }
      }
      place(at, tag, slot)
    }

    /** Frees the table position, moving back the entries after it that probe through it. */
    private def erase(position: Int): Unit = {
      val mask = keys.length - 1
      var hole = position
      var next = (position + 1) & mask
      while (keys(next) != 0L) {
        if (((next - home(keys(next))) & mask) >= ((next - hole) & mask)) {
          keys(hole) = keys(next)
          tags(hole) = tags(next)
          slots(hole) = slots(next)
          hole = next
        }
        next = (next + 1) & mask
      }
      keys(hole) = 0L
      slots(hole) = null
    }

    private def fresh(cohort: Cohort, indexed: Boolean): Slot = {
      val slot = if (spare.isEmpty) Slot() else spare.remove(spare.length - 1)
      slot.cohort = cohort
      slot.indexed = indexed
      slot
    }

    /** Queues the cohort, at the head when `first`, or adds its lanes to the cohort already queued at its pc in its
      * frame. A cohort that is `apart` does not join one already queued: that one issues first and this one follows.
      */
    def add(cohort: Cohort, first: Boolean = false, apart: Boolean = false): Unit = {
      val at        = key(cohort.pc, cohort.frame)
      val partition = tag(cohort.partition)
      val position  = find(at, partition)
      if (position < 0) {
        val queued = fresh(cohort, indexed = true)
        if (first) order.prepend(queued) else order += queued
        put(at, partition, queued)
        count += 1
      } else if (apart) {
        val queued = slots(position).nn
        val head   = fresh(queued.cohort.nn, indexed = true)
        queued.cohort = null
        slots(position) = head
        order.prepend(head)
        order.insert(1, fresh(cohort, indexed = false))
        count += 1
      } else {
        val queued   = slots(position).nn
        val existing = queued.cohort.nn
        queued.cohort = existing.copy(lanes = existing.lanes.union(cohort.lanes))
      }
    }

    def removeHead(): Cohort = {
      var slot = order.removeHead()
      while (slot.cohort eq null) {
        spare += slot
        slot = order.removeHead()
      }
      val cohort = slot.cohort.nn
      if (slot.indexed) erase(find(key(cohort.pc, cohort.frame), tag(cohort.partition)))
      slot.cohort = null
      spare += slot
      count -= 1
      cohort
    }

    /** Removes the cohort of the partition queued at the pc in the frame, or returns null when there is none. */
    def take(pc: Pc, frame: VMFrame, partition: VMLaneMask): Cohort | Null = {
      val position = find(key(pc, frame), tag(partition))
      if (position < 0) null
      else {
        val queued = slots(position).nn
        val cohort = queued.cohort
        queued.cohort = null
        erase(position)
        count -= 1
        cohort
      }
    }

    def cohorts: Iterable[Cohort] = order.map(_.cohort).collect { case cohort: Cohort => cohort }
  }

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
      cluster: Option[Dimensions] = None,
      cooperative: Boolean = false,
      heap: Option[HeapRegion] = None,
      handler: Event => Either[Fault, Unit] = _ => Right(())
  ): Either[Fault, Unit] =
    parameterBlock(kernel.entry, arguments).flatMap { block =>
      control.observer.foreach(_.launched(resources(kernel, geometry, dynamicSharedBytes)))
      launch(
        kernel,
        geometry,
        block,
        VMDevice(DeviceHeap(memory), control, workgroups, handler = handler, heap = heap),
        dynamicSharedBytes,
        cluster,
        cooperative
      )
    }

  private def resources(kernel: Kernel, geometry: LaunchGeometry, dynamicSharedBytes: Int >= 0): KernelResources =
    KernelResources(
      workgroupLanes = geometry.groupVolume,
      subgroupSize = 32,
      registersPerLane = kernel.registersPerLane,
      sharedBytes = kernel.staticSharedBytes + dynamicSharedBytes.widen[Long >= 0]
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

  private val launchGrid: VMContext.Launch = launch(_, _, _, _, _)

  /** Runs every workgroup of one launch, including device-side launches sharing the parent's heap. */
  def launch(
      kernel: Kernel,
      geometry: LaunchGeometry,
      parameterBlock: Array[Byte],
      device: VMDevice,
      dynamicSharedBytes: Int >= 0,
      cluster: Option[Dimensions] = None,
      cooperative: Boolean = false
  ): Either[Fault, Unit] =
    for {
      laneCount <- geometry.groupVolume.refine[Int in 1 ~ 1024]
        .left.map(_ =>
          Fault.InvalidConfiguration(s"workgroups are limited to 1024 lanes, found ${geometry.groupVolume}")
        )
      shape <- clusterShape(kernel, geometry, cluster)
      allLanes = VMLaneMask.all(laneCount)
      log      = PrintLog()
      _ <- shape match {
        case Some(dimensions) if dimensions != Dimensions.One =>
          if (cooperative) Left(Fault.Unsupported("a cooperative launch cannot also name a cluster shape"))
          else clusters(kernel, geometry, dimensions, parameterBlock, device, dynamicSharedBytes, log)
        case _ if cooperative => resident(kernel, geometry, parameterBlock, device, dynamicSharedBytes, log)
        case _                =>
          Dispatch.forEachGroup(geometry, device.workgroups) { (index, group, pace) =>
            val block = borrow(laneCount)
            try {
              val result = Execution(
                kernel,
                geometry,
                group,
                liveLanes(geometry, group, allLanes),
                parameterBlock,
                device,
                dynamicSharedBytes,
                block,
                shape,
                pace = pace
              ).run().left.map(_.at(Fault.Location(kernel.name, None, None, Some(group), None)))
              log.add(index, block.workgroup.prints)
              result
            } finally release(block)
          }
      }
      _ <- log.flush(device.handler)
    } yield ()

  // Steps each member between checks for cluster barrier phases that others completed.
  private val ClusterSlice = 64

  /** Runs each cluster's workgroups together on one thread, sharing their shared memory and cluster barrier. */
  private def clusters(
      kernel: Kernel,
      geometry: LaunchGeometry,
      shape: Dimensions,
      parameterBlock: Array[Byte],
      device: VMDevice,
      dynamicSharedBytes: Int >= 0,
      log: PrintLog
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
      Dispatch.forEachGroup(grid, device.workgroups) { (index, origin, pace) =>
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
              device,
              dynamicSharedBytes,
              block,
              Some(shape),
              sync,
              pace
            )
          )
          sync.members = members
          val result = interleave(kernel, members, "cluster members wait on one another")
          log.add(index, blocks.flatMap(_.workgroup.prints))
          result
        } finally release(blocks)
      }
    }
  }

  // Bytes of the workspace the driver hands a cooperative grid, whose second word is the grid barrier.
  private val GridWorkspaceBytes: Int > 0 = 256

  /** Runs every workgroup of a cooperative launch resident at once on one thread, so each can wait on the others. */
  private def resident(
      kernel: Kernel,
      geometry: LaunchGeometry,
      parameterBlock: Array[Byte],
      device: VMDevice,
      dynamicSharedBytes: Int >= 0,
      log: PrintLog
  ): Either[Fault, Unit] = {
    val laneCount = geometry.groupVolume.unsafe[Int in 1 ~ 1024]
    val allLanes  = VMLaneMask.all(laneCount)
    device.memory.allocate(GridWorkspaceBytes).flatMap { workspace =>
      val cooperative = device.copy(gridWorkspace = workspace.raw)
      val groups      =
        Vec.tabulate(geometry.groupTotal.toInt)(index => geometry.groupCoordinates(index.toLong.unsafe[Long >= 0]))
      val blocks  = groups.map(_ => Block(laneCount))
      val members = groups.zip(blocks).map((group, block) =>
        Execution(
          kernel,
          geometry,
          group,
          liveLanes(geometry, group, allLanes),
          parameterBlock,
          cooperative,
          dynamicSharedBytes,
          block
        )
      )
      val result = interleave(kernel, members, "resident workgroups wait on one another")
      blocks.zipWithIndex.foreach((block, index) => log.add(index.toLong, block.workgroup.prints))
      result
    }
  }

  /** Steps each of `members` in turn until all finish, failing when a full sweep advances none. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def interleave(kernel: Kernel, members: Vec[Execution], stuck: String): Either[Fault, Unit] = {
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
            if (waits.nonEmpty) waits.mkString(", ") else stuck
          }"))
      }
    }
    failure.toLeft(())
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
    case Instruction.Tensor(Matrix.Operation.WarpgroupMultiply(_, _, _, _, _, _, _, _)) => true
    case _                                                                              => false
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
      device: VMDevice,
      dynamicSharedBytes: Int >= 0,
      block: Block,
      cluster: Option[Dimensions] = None,
      sync: ClusterSync = ClusterSync(),
      pace: Pace = Pace.Free
  ) {
    private val workgroup = block.workgroup
    private val effects   = block.effects
    private val observing = device.control.observer.isDefined
    private val root      = VMFrame(
      kernel.entry.routine,
      block.frames.acquire(kernel.entry.routine, observing),
      0L,
      None,
      initialLive
    )
    private val context = VMContext(
      kernel,
      geometry,
      group,
      workgroup,
      device,
      dynamicSharedBytes,
      effects,
      root,
      launchGrid,
      cluster.getOrElse(Dimensions.One),
      cluster.nonEmpty
    )
    context.spin = SpinWatch((workgroup.laneCount + 31) >> 5, pace)
    private var live         = initialLive
    private val ready        = ReadyQueue()
    private var runningAhead = false
    private var runAhead     = 0
    private var sleeping     = false
    private var idle         = 0
    private val asleep       = mutable.ArrayBuffer.empty[Cohort]
    private var stalled      = 0
    private val barrierMasks = mutable.Map.empty[BarrierKey, VMLaneMask]
    private val operations   = mutable.Map.empty[BarrierKey, Option[Option[PredicateOperation]]]
    private val counts       = mutable.Map.empty[BarrierKey, Int]
    private var units        = 0

    // Waiting cohorts by barrier key, in arrival order within a key. Emptied lists are kept for the next key, and the
    // two scratch lists hold one release at a time.
    private val waiting      = mutable.HashMap.empty[BarrierKey, mutable.ArrayBuffer[Waiting]]
    private val spareWaiting = mutable.ArrayBuffer.empty[mutable.ArrayBuffer[Waiting]]
    private val resumed      = mutable.ArrayBuffer.empty[Waiting]
    private val released     = mutable.ArrayBuffer.empty[Waiting]

    private lazy val clusterWaiting     = mutable.ArrayBuffer.empty[ClusterWaiting]
    private lazy val clusterLanePhase   = Array.fill(workgroup.laneCount)(-1L)
    private lazy val clusterWaitedPhase = Array.fill(workgroup.laneCount)(-1L)
    private var clusterArrived          = VMLaneMask.Empty

    def clusterArrivals: Boolean = clusterArrived.nonEmpty
    def clusterArrival: Boolean  = clusterArrived.intersect(live) == live

    private type Run = boundary.Label[Either[Fault, Unit]]

    private def fail(fault: Fault)(using Run): Nothing = break(Left(fault))

    /** A fresh identity for a unit of execution. A cohort that splits into several gives each part its own, so a
      * timeline can draw the diverging paths apart.
      */
    private def mintUnit(): Int >= 0 = {
      val id = units
      units += 1
      id.unsafe[Int >= 0]
    }

    /** The unit a cohort runs as, which the timeline follows as it splits, parks and finishes. */
    private def unit(cohort: Cohort, state: MachineUnit.State): MachineUnit =
      MachineUnit(group, cohort.id, cohort.lanes.size.widen[Int >= 0], state)

    private def observe(frame: VMFrame, cohort: Cohort, active: VMLaneMask, issue: Long)(using Run): Unit =
      device.control.observer.foreach { observer =>
        val at = location(frame, cohort.pc, active)
        observer.instruction(at, View(frame, active, unit(cohort, MachineUnit.State.Running), issue, cohort.pc)) match {
          case Left(fault) => fail(fault.at(at))
          case Right(())   => ()
        }
      }

    /** A change in what a unit is doing, which the timeline records between the issues it surrounds. */
    private inline def transition(cohort: Cohort, inline kind: MachineEvent.Kind, inline at: Fault.Location): Unit =
      if (observing) device.control.observer.foreach { observer =>
        val event = kind
        observer.transition(MachineEvent(unit(cohort, event.state), event, device.control.issued(), Some(at)))
      }

    /** A unit whose lanes have all left the machine finishes. */
    private inline def retire(cohort: Cohort): Unit =
      transition(cohort, MachineEvent.Kind.Retired, location(cohort.frame, cohort.pc, cohort.lanes))

    private inline def resume(value: Waiting)(using Run): Unit = {
      transition(
        value.cohort,
        MachineEvent.Kind.Resumed,
        location(value.cohort.frame, value.cohort.pc, value.cohort.lanes)
      )
      advance(value.cohort, value.cohort.lanes)
    }

    private class View(frame: VMFrame, active: VMLaneMask, machine: MachineUnit, issue: Long, pc: Pc)
        extends CohortView {
      def function: NonEmpty[String]               = frame.routine.name
      def unit: MachineUnit                        = machine
      def issued: Long                             = issue
      def begins: Boolean                          = frame.routine.program.begins(pc)
      def subgroupSize: Int > 0                    = 32
      def eachLane(each: (Int >= 0) => Unit): Unit = active.foreachLane(lane => each(lane.toInt.unsafe[Int >= 0]))
      def text: String                             = frame.routine.program.sourceText(pc).getOrElse {
        val issued = frame.routine.program.instructions(pc.toInt)
        val guard  = issued.predication.fold("") { predication =>
          val name = frame.routine.predicateNames.lift(predication.predicate.raw)
          s"@${if (predication.negated) "!" else ""}${name.getOrElse(s"p${predication.predicate.raw}")} "
        }
        s"$guard${issued.instruction}"
      }
      def registerNames: Vec[NonEmpty[String]]  = frame.routine.registerNames
      def predicateNames: Vec[NonEmpty[String]] = frame.routine.predicateNames
      def lanes: Vec[Int >= 0]                  = {
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
        val queued = if (lanes eq cohort.lanes) cohort else cohort.copy(lanes = lanes)
        if (sleeping) asleep += queued else ready.add(queued, runningAhead, runningAhead && joins(cohort))
      }
    }

    // each path to a convergence barrier issues its own synchronise, which the last to arrive releases
    private def joins(cohort: Cohort): Boolean =
      cohort.frame.routine.program.instructions.lift(cohort.pc.toInt).exists(_.instruction match {
        case Instruction.Convergence(_, ConvergenceAction.Synchronise) => true
        case _                                                         => false
      })

    private def advance(cohort: Cohort, lanes: VMLaneMask)(using Run): Unit =
      if (cohort.pc.toInt == Int.MaxValue) fail(Fault.Backend("program counter overflow"))
      else { stalled = 0; enqueue(cohort.copy(pc = Pc(cohort.pc.unwrap + 1), lanes = lanes)) }

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
      val lanes = block.frames.acquire(function.routine, observing)
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
      enqueue(Cohort(mintUnit(), Pc.Entry, call.lanes, frame, cohort.partition))
    }

    private def synchroniseWarpInstruction(memberMask: Value, cohort: Cohort): (VMLaneMask, VMLaneMask) = {
      context.frame = cohort.frame
      context.active = cohort.lanes
      var parkedWarp = -1
      var parked     = 0L
      val runnable   = cohort.lanes.filterLanes { lane =>
        val expected = cohort.frame.live.warpBits(lane.raw) & live.warpBits(lane.raw) & context.read(lane, memberMask)
        val missing  = expected & ~cohort.lanes.warpBits(lane.raw)
        missing == 0L || {
          if ((lane.raw & ~31) != parkedWarp) {
            parkedWarp = lane.raw & ~31
            parked = converging(cohort.frame, lane.raw)
          }
          (missing & ~parked) == 0L
        }
      }
      runnable -> cohort.lanes.subtract(runnable)
    }

    /** The lanes of `lane`'s warp in `frame` that wait at a convergence barrier, which a warp instruction never
      * gathers.
      */
    private def converging(frame: VMFrame, lane: Int): Long = if (barrierMasks.isEmpty) 0L
    else
      barrierMasks.foldLeft(0L) {
        case (bits, (BarrierKey.Convergence(base, _, owner), arrived))
            if base.raw == (lane & ~31) && (owner eq frame) =>
          bits | arrived.warpBits(lane)
        case (bits, _) => bits
      }

    private def synchroniseWarpgroupInstruction(cohort: Cohort): (VMLaneMask, VMLaneMask) = {
      val runnable = cohort.lanes.filterLanes { lane =>
        val first = lane & -Warpgroup.Lanes
        var base  = first
        var ready = true
        while (ready && base < first + Warpgroup.Lanes) {
          ready =
            (cohort.frame.live.warpBits(base) & live.warpBits(base) & ~cohort.lanes.warpBits(base) &
              ~converging(cohort.frame, base)) == 0L
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
        val pending  = clusterWaiting.toVector
        val departed = mutable.LongMap.empty[VMLaneMask]
        pending.foreach { entry =>
          if (!entry.arrival) departed.update(
            entry.phase,
            departed.get(entry.phase).fold(entry.cohort.lanes)(_.union(entry.cohort.lanes))
          )
        }
        clusterWaiting.clear()
        pending.foreach { entry =>
          val participants = if (entry.arrival) clusterArrived
          else departed.getOrElse(entry.phase, VMLaneMask.Empty)
          val released = entry.cohort.lanes.intersect(live).filterLanes { lane =>
            val warp = live.warpBits(lane.raw)
            if (entry.arrival && entry.phase < clusterPhase) true
            else (entry.arrival || entry.phase < clusterPhase) && (participants.warpBits(lane.raw) & warp) == warp
          }
          if (!entry.arrival) released.foreachLane(lane => clusterWaitedPhase(lane.raw) = entry.phase)
          transition(
            entry.cohort,
            MachineEvent.Kind.Resumed,
            location(entry.cohort.frame, entry.cohort.pc, released)
          )
          advance(entry.cohort, released)
          val remaining = entry.cohort.lanes.intersect(live).subtract(released)
          if (remaining.nonEmpty) clusterWaiting += entry.copy(cohort = entry.cohort.copy(lanes = remaining))
        }
      }

    private def livePart(base: WorkgroupLane.WarpBase, frame: VMFrame, members: Long): Long =
      (members & 0xffffffffL) & frame.live.warpBits(base.raw) & live.warpBits(base.raw)

    private def expected(key: BarrierKey): VMLaneMask = {
      def warp(base: WorkgroupLane.WarpBase, frame: VMFrame, members: Long): VMLaneMask =
        VMLaneMask.ofWarp(base.raw, livePart(base, frame, members))
      key match {
        case BarrierKey.Workgroup(_)                    => live
        case BarrierKey.Warp(base, _, frame, members)   => warp(base, frame, members & ~converging(frame, base.raw))
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

    private def reduce(released: mutable.ArrayBuffer[Waiting])(using Run): Unit =
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

    private def park(value: Waiting): Unit = {
      val parked = waiting.getOrElse(value.key, null)
      if (parked != null) parked += value
      else {
        val fresh =
          if (spareWaiting.isEmpty) mutable.ArrayBuffer.empty[Waiting] else spareWaiting.remove(spareWaiting.length - 1)
        fresh += value
        waiting(value.key) = fresh
      }
    }

    private def canResume(value: Waiting, arrived: VMLaneMask): Boolean =
      value.resumeOnArrival && value.cohort.lanes.filterLanes { lane =>
        val members = live.warpBits(lane.raw)
        (arrived.warpBits(lane.raw) & members) != members
      }.isEmpty

    /** Moves the parked cohorts that resume on arrival to `resumed`, leaving the others in order. */
    private def takeResumable(parked: mutable.ArrayBuffer[Waiting], arrived: VMLaneMask): Unit = {
      var kept  = 0
      var index = 0
      while (index < parked.length) {
        val value = parked(index)
        if (canResume(value, arrived)) resumed += value
        else { parked(kept) = value; kept += 1 }
        index += 1
      }
      parked.dropRightInPlace(parked.length - kept)
    }

    /** Moves the parked cohorts with a participating lane to `released`, leaving the others in order. */
    private def takeParticipating(parked: mutable.ArrayBuffer[Waiting], participants: VMLaneMask): Unit = {
      var kept  = 0
      var index = 0
      while (index < parked.length) {
        val value = parked(index)
        if (value.cohort.lanes.intersect(participants).nonEmpty) released += value
        else { parked(kept) = value; kept += 1 }
        index += 1
      }
      parked.dropRightInPlace(parked.length - kept)
    }

    /** A partition ends where a convergence barrier releases lanes beyond it. */
    private def rejoinPartitions(released: mutable.ArrayBuffer[Waiting], participants: VMLaneMask): Unit = {
      var index = 0
      while (index < released.length) {
        val waiter = released(index)
        if (waiter.cohort.partition.nonEmpty && participants.subtract(waiter.cohort.partition).nonEmpty)
          released(index) = waiter.copy(cohort = waiter.cohort.copy(partition = VMLaneMask.Empty))
        index += 1
      }
    }

    private def releaseBarrier(key: BarrierKey)(using Run): Unit = {
      val arrived = barrierMasks.getOrElse(key, VMLaneMask.Empty)
      val parked  = waiting.getOrElse(key, null)
      resumed.clear()
      if (parked != null) takeResumable(parked, arrived)
      resumed.foreach(resume(_))
      participants(key, arrived).foreach { participants =>
        released.clear()
        if (parked != null) takeParticipating(parked, participants)
        val remaining = arrived.subtract(participants)
        if (remaining.nonEmpty) barrierMasks(key) = remaining
        else {
          val _ = barrierMasks.remove(key)
          val _ = counts.remove(key)
          val _ = operations.remove(key)
        }
        // A completed convergence token is no longer live. A legacy target can be reached along a path that skipped
        // the next SSY using this slot; it must not wait for the previous token's members.
        key match {
          case BarrierKey.Convergence(base, token, _) =>
            workgroup.completeConvergence(base, token)
          case _ => ()
        }
        key match {
          case BarrierKey.Convergence(_, _, _) => rejoinPartitions(released, participants)
          case _                               => ()
        }
        reduce(released)
        released.foreach(resume(_))
      }
      if (parked != null && parked.isEmpty) {
        val _ = waiting.remove(key)
        spareWaiting += parked
      }
    }

    private val convergences = mutable.ArrayBuffer.empty[BarrierKey]

    private def releaseConvergences()(using Run): Unit = if (barrierMasks.nonEmpty) {
      convergences.clear()
      barrierMasks.keys.view.foreach {
        case key: BarrierKey.Convergence if covered(key, barrierMasks(key).warpBits(key.base.raw)) =>
          convergences += key
        case _ => ()
      }
      convergences.foreach(releaseBarrier(_))
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
          park(Waiting(cohort.copy(lanes = arrival.lanes), key, reduction, resume))
          barrierMasks(key) = barrierMasks.getOrElse(key, VMLaneMask.Empty).union(arrival.lanes)
          transition(
            cohort,
            MachineEvent.Kind.Blocked(MachineUnit.Reason.Barrier(s"barrier ${arrival.id}")),
            location(cohort.frame, cohort.pc, arrival.lanes)
          )
          releaseBarrier(key)
      }
    }

    /** The live lanes of the warp that `members` names, which a WARPSYNC.EXCLUSIVE partitions the warp into. */
    private def memberLanes(base: WorkgroupLane.WarpBase, members: Long, frame: VMFrame): VMLaneMask = {
      val builder = VMLaneMask.Builder()
      var lane    = 0
      while (lane < 32 && base + lane < workgroup.laneCount) {
        if (((members >>> lane) & 1L) != 0L && frame.live.contains(base + lane))
          builder.add(WorkgroupLane.fromIndex(base + lane))
        lane += 1
      }
      builder.result()
    }

    private def parkWarp(cohort: Cohort, lanes: VMLaneMask, key: BarrierKey)(using Run): Unit = {
      park(Waiting(cohort.copy(lanes = lanes), key))
      barrierMasks(key) = barrierMasks.getOrElse(key, VMLaneMask.Empty).union(lanes)
      transition(
        cohort,
        MachineEvent.Kind.Blocked(MachineUnit.Reason.Barrier(key.describe)),
        location(cohort.frame, cohort.pc, lanes)
      )
    }

    private def arriveWarp(cohort: Cohort, lanes: VMLaneMask, key: BarrierKey)(using Run): Unit = {
      parkWarp(cohort, lanes, key)
      releaseBarrier(key)
    }

    /** A cohort arrives at the convergence barriers its lanes' tokens name, a warp's lanes at a time in order of token.
      */
    private def synchroniseConvergence(cohort: Cohort, active: VMLaneMask, token: Register)(using Run): Unit = {
      val frame = cohort.frame
      active.foreachWarp { (base, bits) =>
        var remaining = bits
        while (remaining != 0L) {
          var value = Long.MaxValue
          var scan  = remaining
          while (scan != 0L) {
            value = Math.min(
              value,
              frame.lanes(WorkgroupLane.fromIndex(base + java.lang.Long.numberOfTrailingZeros(scan))).register(token)
            )
            scan &= scan - 1L
          }
          var group = 0L
          scan = remaining
          while (scan != 0L) {
            val bit = java.lang.Long.numberOfTrailingZeros(scan)
            if (frame.lanes(WorkgroupLane.fromIndex(base + bit)).register(token) == value) group |= 1L << bit
            scan &= scan - 1L
          }
          remaining &= ~group
          val lanes = if (group == bits && active.size.raw == java.lang.Long.bitCount(group)) active
          else VMLaneMask.ofWarp(base, group)
          arriveConvergence(cohort, lanes, BarrierKey.Convergence(WorkgroupLane.warpBases(base >>> 5), value, frame))
        }
      }
    }

    private def covered(key: BarrierKey.Convergence, arrived: Long): Boolean =
      (livePart(key.base, key.frame, workgroup.convergenceMask(key.base, key.token)) & ~arrived) == 0L

    /** Lanes arriving at a convergence barrier release it once every lane it still waits for has arrived. The first to
      * arrive, finding the barrier complete, carry on without parking.
      */
    private def arriveConvergence(cohort: Cohort, lanes: VMLaneMask, key: BarrierKey.Convergence)(using Run): Unit = {
      val arrived  = barrierMasks.getOrElse(key, VMLaneMask.Empty)
      val complete = covered(key, arrived.warpBits(key.base.raw) | lanes.warpBits(key.base.raw))
      if (complete && arrived.isEmpty) {
        workgroup.completeConvergence(key.base, key.token)
        transition(
          cohort,
          MachineEvent.Kind.Blocked(MachineUnit.Reason.Barrier(key.describe)),
          location(cohort.frame, cohort.pc, lanes)
        )
        transition(cohort, MachineEvent.Kind.Resumed, location(cohort.frame, cohort.pc, lanes))
        advance(cohort, lanes)
      } else {
        parkWarp(cohort, lanes, key)
        if (complete) releaseBarrier(key)
        else releaseWarpBarriers(key.base, key.frame)
      }
    }

    private def releaseWarpBarriers(base: WorkgroupLane.WarpBase, frame: VMFrame)(using Run): Unit =
      if (barrierMasks.nonEmpty) {
        val keys =
          barrierMasks.keys.collect { case key: BarrierKey.Warp if key.base == base && (key.frame eq frame) => key }
        keys.toVector.foreach(releaseBarrier(_))
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
      transition(
        cohort,
        MachineEvent.Kind.Blocked(MachineUnit.Reason.Cluster(s"cluster phase $clusterPhase")),
        location(cohort.frame, cohort.pc, active)
      )
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
          advance(Cohort(mintUnit(), continuation.pc, mask, continuation.caller, cohort.partition), mask)
      }
    }

    private def step(cohort: Cohort, instruction: PredicatedInstruction)(using Run): Unit = {
      val frame = cohort.frame
      cohort.lanes.positiveSize match {
        case Some(size) => check(device.control.checkpoint(size.widen[Int > 0]))
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
      context.partition = cohort.partition
      // The launch's issue ordinal is its timeline's time axis; only an observed launch needs one.
      val issue = if (observing) device.control.nextIssue() else 0L
      if (observing) observe(frame, cohort, active, issue)
      if (observing) {
        // The guard is read before the issue is observed, so it is collected here rather than by the lane's own read.
        context.use.clear()
        instruction.predication.foreach(predication => context.use.readPredicate(predication.predicate))
        frame.lanes.begin(context.use)
      }
      effects.clear()
      val outcome = if (active.isEmpty) Advanced else VMSemantics.execute(instruction.instruction, LaneLoop(context))
      val result  = outcome match {
        case Right(_) =>
          effects.commit(frame.lanes, workgroup, device.memory, cohort.pc) match {
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
            case Instruction.Convergence(_, ConvergenceAction.Break) | Instruction.Yield | Instruction.Pause =>
              releaseConvergences()
            case _ => ()
          }
        case Right(Step.Jump(target)) =>
          // The lanes that jump and the lanes that fall through are separate units from here on.
          enqueue(cohort.copy(id = mintUnit(), pc = target, lanes = active))
          advance(cohort, skipped)
        case Right(Step.Branches(arms, fallThrough)) =>
          var arm = 0
          while (arm < arms.size) {
            enqueue(cohort.copy(id = mintUnit(), pc = arms(arm).target, lanes = arms(arm).lanes))
            arm += 1
          }
          advance(cohort, skipped.union(fallThrough))
        case Right(Step.ClusterBarrier(arrival)) =>
          advance(cohort, skipped)
          clusterBarrier(cohort, active, arrival)
        case Right(Step.WorkgroupBarrier(arrivals, blocking, reduction)) =>
          advance(cohort, skipped)
          arrivals.foreach(arriveWorkgroup(cohort, _, reduction, !blocking))
        case Right(Step.WarpSync(memberMask, exclusive)) =>
          advance(cohort, skipped)
          byWarp(active)(context.read(_, memberMask)).foreach { case ((base, members), lanes) =>
            val arrival = if (exclusive) cohort.copy(partition = memberLanes(base, members, frame)) else cohort
            arriveWarp(arrival, lanes, BarrierKey.Warp(base, cohort.pc, frame, members))
          }
        case Right(Step.ConvergenceSync(token)) =>
          advance(cohort, skipped)
          synchroniseConvergence(cohort, active, token)
        case Right(Step.Call(calls, parameters, returnParameters)) =>
          advance(cohort, skipped)
          calls.foreach(enterCall(cohort, _, parameters, returnParameters))
        case Right(Step.Return(mask)) =>
          complete(cohort, mask)
          advance(cohort, skipped)
          if (skipped.isEmpty) retire(cohort)
          releaseCluster()
          releaseAll()
        case Right(Step.Exit(mask)) =>
          frame.live = frame.live.subtract(mask)
          live = live.subtract(mask)
          advance(cohort, skipped)
          if (skipped.isEmpty) retire(cohort)
          releaseCluster()
          releaseAll()
      }
      if (observing) {
        frame.lanes.end()
        device.control.observer.foreach(_.registers(issue, context.use.result(frame.routine)))
      }
    }

    private def start(): Unit = {
      val _ = workgroup.allocateBuffer(parameterBlock.clone())
      enqueue(Cohort(mintUnit(), Pc.Entry, initialLive, root))
    }

    def finished: Boolean = live.isEmpty
    def runnable: Boolean = (ready.nonEmpty || asleep.nonEmpty) && !stuck

    def describeWaits: Vec[String] =
      Vec.from(waiting.values.flatMap(_.map(value =>
        s"${value.key.describe}, ${value.cohort.frame.routine.name} pc ${value.cohort.pc}"
      ))) ++
        (if (stuck) Vec.from(ready.cohorts.map(value => s"collective, ${value.frame.routine.name} pc ${value.pc}"))
         else Vec.empty)

    private def deadlock()(using Run): Nothing = {
      val waits = describeWaits.distinct.sorted
      fail(Fault.Hang(
        s"execution deadlocked: ${kernel.name}: ${if (waits.nonEmpty) waits.mkString(", ") else "no runnable cohort"}"
      ))
    }

    private def next()(using Run): Unit = {
      pace.step()
      if (asleep.nonEmpty) {
        idle += 1
        if (ready.isEmpty || stalled > ready.size || idle >= SpinBackoff) wake()
        if (ready.isEmpty) deadlock()
      }
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
          val queued = ready.take(cohort.pc, frame, cohort.partition)
          if (queued ne null) cohort = cohort.copy(lanes = cohort.lanes.union(queued.lanes))
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
        val yielding = instruction.instruction match {
          case Instruction.Yield | Instruction.Pause | Instruction.Spin => true; case _ => false
        }
        sleeping = yielding && context.spin.recording && cohort.lanes.headOption.exists(context.spin.yielded(_))
        runningAhead = frame.routine.program.runsAhead && !yielding && runAhead < RunAheadLimit &&
          diverged(cohort, frame)
        runAhead = if (runningAhead) runAhead + 1 else 0
        step(cohort, instruction)
        runningAhead = false
        sleeping = false
      }
    }

    private def wake(): Unit = {
      idle = 0
      val woken = asleep.toVector
      asleep.clear()
      woken.foreach(enqueue(_))
    }

    /** A path that has not yielded within this many steps hands over regardless, as a path spinning without a yield
      * would otherwise starve the others.
      */
    private inline val RunAheadLimit = 1 << 16

    /** A path that polls unchanged memory sleeps until the others have taken this many steps, or have none to take. */
    private inline val SpinBackoff = 1 << 8

    /** Whether the cohort is a path that split from lanes of its warp that are still live, as opposed to a whole warp,
      * which takes its turn with the other warps.
      */
    private def diverged(cohort: Cohort, frame: VMFrame): Boolean = cohort.lanes.headOption.exists { lane =>
      cohort.lanes.warpBits(lane.raw) != (frame.live.warpBits(lane.raw) & live.warpBits(lane.raw))
    }

    /** Every ready cohort has waited in turn for lanes that have not moved since. */
    private def stuck: Boolean = stalled > ready.size && asleep.isEmpty

    def run(): Either[Fault, Unit] = boundary {
      start()
      while (live.nonEmpty) {
        if ((ready.isEmpty && asleep.isEmpty) || stuck) deadlock()
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
