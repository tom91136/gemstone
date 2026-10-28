package net.kurobako.gemstone.isa.nvidia

import VMExecution.*
import VMIR.*
import cats.syntax.all.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.string.*
import net.kurobako.gemstone.vm.*
import scala.collection.mutable

private[isa] object VMMachineOps {
  val constantFault: Fault = Fault.IllegalInstruction("constant memory is read through operands")

  private val unsupported: Fault = Fault.IllegalInstruction("this address space has no byte access")

  def access(space: Space): MemoryAccess = space match {
    case Space.Shared  => MemoryAccess.Shared
    case Space.Local   => MemoryAccess.Local
    case Space.Generic => MemoryAccess.Generic(false)
    case _             => MemoryAccess.Global(false)
  }

  /** `each` over the active lanes of the unit that holds `lane`. */
  def eachActive(context: VMContext, lane: WorkgroupLane)(each: WorkgroupLane => Unit): Unit = {
    val base = lane.raw - lane.raw % 32
    val bits = context.active.warpBits(base)
    (0 until 32).foreach(index => if ((bits & (1L << index)) != 0L) each(WorkgroupLane.fromIndex(base + index)))
  }

  def pcOf(target: Long): Either[Fault, Pc] =
    if (target >= 0L && target <= Int.MaxValue) Right(Pc(target.toInt.unsafe[Int >= 0]))
    else Left(Fault.Backend(s"branch target $target names no instruction"))

  /** `count` bytes at `address` of `space`; shared and generic addresses name the CTA that holds them. */
  def readBytes(context: VMContext, lane: WorkgroupLane, space: Space, address: Long, count: Int, what: String)
      : Either[Fault, Array[Byte]] = space match {
    case Space.Shared =>
      VMAsync.sharedTarget(context, address, count, what) match {
        case Left(fault)           => Left(fault)
        case Right((from, offset)) => Right(VMAsync.readShared(from, offset, count))
      }
    case Space.Global =>
      if (count == 0) Right(Array.emptyByteArray)
      else
        context.device.memory.read(GuestAddress(address), count.unsafe[Int >= 0])
          .map(Vec.toArray(_))
          .leftMap(VMWriteBuffer.located(_, context.pc, lane))
    case Space.Generic => VMAsync.readGeneric(context, lane, address, count.unsafe[Int >= 0])
    case _             => Left(unsupported)
  }

  /** `data` at `address` of `space`, at once. */
  def writeBytes(context: VMContext, lane: WorkgroupLane, space: Space, address: Long, data: Array[Byte], what: String)
      : Either[Fault, Unit] = space match {
    case Space.Shared  => writeShared(context, address, data, what)
    case Space.Global  => writeDevice(context, lane, address, data)
    case Space.Generic =>
      if (VMAddressSpace.isShared(address, data.length)) writeShared(context, address, data, what)
      else writeDevice(context, lane, address, data)
    case _ => Left(unsupported)
  }

  private def writeDevice(context: VMContext, lane: WorkgroupLane, address: Long, data: Array[Byte])
      : Either[Fault, Unit] =
    context.device.memory.write(GuestAddress(address), Vec.unsafeWrapArray(data))
      .leftMap(VMWriteBuffer.located(_, context.pc, lane))

  private def writeShared(context: VMContext, address: Long, data: Array[Byte], what: String): Either[Fault, Unit] =
    VMAsync.sharedTarget(context, address, data.length, what) match {
      case Left(fault)         => Left(fault)
      case Right((to, offset)) => Right(to.writeSharedBytes(offset, data, 0, data.length))
    }

  /** The CTA of rank `rank` in the cluster, if it is resident. */
  def ctaAt(context: VMContext, rank: Long): Option[VMWorkgroup] =
    Option.when(
      rank < context.workgroup.cluster.length
    )(context.workgroup.cluster(rank.toInt)).flatMap(Option(_)).map(_.nn)

  def deviceCall(context: VMContext, address: Long, lanes: VMLaneMask): Either[Fault, DeviceCall] =
    context.kernel.functionTable.name(address) match {
      case None => Left(Fault.Backend(s"invalid function address 0x${java.lang.Long.toUnsignedString(address, 16)}"))
      case Some(name) =>
        context.kernel.functions.get(name).map(DeviceCall(
          _,
          lanes
        )).toRight(Fault.Backend(s"missing device function $name"))
    }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private inline def groupLanes(context: VMContext, keys: Array[Long], builders: Array[VMLaneMask.Builder | Null])(
      inline keyOf: WorkgroupLane => Long
  ): Int = {
    var groups = 0
    context.active.foreachLane { lane =>
      val key   = keyOf(lane)
      var group = 0
      while (group < groups && keys(group) != key) group += 1
      if (group == groups) {
        keys(group) = key
        builders(group) = VMLaneMask.Builder()
        groups += 1
      }
      builders(group).nn.add(lane)
    }
    groups
  }

  /** Each distinct function the active lanes name, called by the lanes that name it. */
  def call(context: VMContext, function: Value, returns: Vec[Parameter], parameters: Vec[Parameter])
      : Either[Fault, Step] = function match {
    case Value.Immediate(address) =>
      deviceCall(context, address, context.active).map(call => Step.Call(Vec(call), parameters, returns))
    case _ =>
      val addresses = Array.ofDim[Long](context.workgroup.laneCount)
      val builders  = Array.ofDim[VMLaneMask.Builder | Null](addresses.length)
      val groups    = groupLanes(context, addresses, builders)(lane => context.read(lane, function))
      Vec.tabulate(groups)(identity).traverse(group =>
        deviceCall(context, addresses(group), builders(group).nn.result())
      ).map(Step.Call(_, parameters, returns))
  }

  /** Each distinct location the active lanes compute, taken by the lanes that compute it. */
  def split(context: VMContext, locations: Array[Long]): Either[Fault, Step] = {
    val targets  = Array.ofDim[Long](context.workgroup.laneCount)
    val builders = Array.ofDim[VMLaneMask.Builder | Null](targets.length)
    val groups   = groupLanes(context, targets, builders)(lane => locations(lane.raw))
    Vec.tabulate(groups)(identity).traverse(group =>
      pcOf(targets(group)).map(Arm(_, builders(group).nn.result()))
    ).map(Step.Branches(_, VMLaneMask.Empty))
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
  def branchTable(context: VMContext, index: Value, targets: Vec[Pc]): Either[Fault, Step] = {
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
  }

  def callBuiltin(context: VMContext, builtin: Builtin, returns: Vec[Parameter], parameters: Vec[Parameter])
      : Either[Fault, Unit] =
    LaneFaults.everyLane(context)(VMBuiltins.execute(context, _, builtin, returns, parameters))

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def setupConvergence(context: VMContext, token: Register): Unit = {
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
  }

  def leaveConvergence(context: VMContext, token: Register): Unit =
    context.active.foreachLane(lane =>
      context.workgroup.leaveConvergence(
        (lane & ~31).unsafe[WorkgroupLane.WarpBase],
        context.lane(lane).register(token),
        (lane & 31).unsafe[WarpLane]
      )
    )

  def barrierTarget(rawBarrier: Long, rawExpected: Long, blocking: Boolean)
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

  /** Whether every warp with an active lane has all 32 of its lanes active. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def converged(context: VMContext): Boolean = {
    var base  = 0
    var whole = true
    while (whole && base < context.workgroup.laneCount) {
      val bits = context.active.warpBits(base)
      whole = bits == 0L || (bits == 0xffffffffL && base + 32 <= context.workgroup.laneCount)
      base += 32
    }
    whole
  }

  /** Whether the 32 scratch values from index `base` are equal. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def agreed(context: VMContext, base: Int): Boolean = {
    val values = context.values(0)
    var index  = 1
    while (index < 32 && values(base + index) == values(base)) index += 1
    index == 32
  }

  /** A barrier whose id and count are the same for every lane: lanes arrive grouped by warp, in warp order. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def uniformBarrier(
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

  def divergentBarrier(
      context: VMContext,
      barriers: Array[Long],
      counts: Array[Long],
      blocking: Boolean,
      reduction: Option[BarrierReduction]
  ): Either[Fault, Step] = {
    val arrivals = mutable.LinkedHashMap.empty[(BarrierId, BarrierCount, Int), VMLaneMask.Builder]
    LaneFaults.everyLane(context) { lane =>
      barrierTarget(barriers(lane.raw), counts(lane.raw), blocking).map((id, threads) =>
        arrivals.getOrElseUpdate((id, threads, lane & ~31), VMLaneMask.Builder()).add(lane)
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

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def warpSync(context: VMContext, memberMask: Value): Either[Fault, Step] = {
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
  }

  /** The CTA whose shared memory holds an mbarrier and the mbarrier's address within that CTA's own window. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  def sharedBarrier(context: VMContext, address: Long): Either[Fault, (VMWorkgroup, Long)] =
    if ((address & 7L) != 0 || !VMAddressSpace.isShared(address, 8))
      Left(Fault.Backend(s"invalid shared mbarrier address: $address"))
    else context.workgroup.sharedOwner(address) match {
      case null               => Left(Fault.Backend(s"mbarrier address names a CTA outside the cluster: $address"))
      case owner: VMWorkgroup => Right(owner -> VMAddressSpace.sharedAddress(address))
    }

  def mbarrierInit(context: VMContext, address: Long, count: Long): Either[Fault, Unit] =
    for {
      (owner, canonical) <- sharedBarrier(context, address)
      barrier            <- MBarrier.create(count)
    } yield {
      val _ = owner.invalidatedMBarriers.subtractOne(canonical)
      owner.mbarriers(canonical) = barrier
    }

  def mbarrierInvalidate(context: VMContext, address: Long): Either[Fault, Unit] =
    for {
      (owner, canonical) <- sharedBarrier(context, address)
      _                  <- VMAsync.mbarrier(context, address)
    } yield {
      val _ = owner.mbarriers.subtractOne(canonical)
      owner.invalidatedMBarriers(canonical) = ()
    }

  def mbarrierLoad(context: VMContext, address: Long): Either[Fault, Unit] =
    sharedBarrier(context, address).flatMap { (owner, canonical) =>
      if (owner.invalidatedMBarriers.contains(canonical))
        Left(Fault.Backend(s"invalidated mbarrier at shared address $canonical"))
      else if (owner.mbarriers.contains(canonical)) Right(())
      else
        MBarrier.fromStoredInitialState(owner.readShared(canonical, 64)).map(barrier =>
          owner.mbarriers(canonical) = barrier
        )
    }

  def mbarrierTransaction(context: VMContext, address: Long, bytes: Long, complete: Boolean): Either[Fault, Unit] =
    VMAsync.mbarrier(context, address).flatMap(found =>
      MBarrier.transactionBytes(bytes).flatMap(count =>
        if (complete) found.completeBytes(count) else found.expectBytes(count)
      )
    )

  def mbarrierTest(context: VMContext, address: Long, phase: Long, parity: Boolean): Either[Fault, Boolean] =
    for {
      found <- VMAsync.mbarrier(context, address)
      _     <- Either.cond(
        !parity || phase == 0L || phase == 1L,
        (),
        Fault.Backend(s"invalid mbarrier phase parity: $phase")
      )
    } yield found.complete(if (parity) phase else MBarrier.tokenParity(phase))

  /** The dynamic stack grows down from the top of the frame's local window, which is the window's fixed size above its
    * aligned static storage, to that storage; stack pointers are frame-local addresses.
    */
  def stackBottom(context: VMContext): Long = (context.frame.routine.localBytes.toLong + 15L) & -16L

  def stackTop(context: VMContext): Long = stackBottom(context) + VMAddressSpace.LocalFrameSize

  def stackAllocate(context: VMContext, lane: WorkgroupLane, bytes: Long, alignment: Long): Either[Fault, Long] = {
    val storage = context.lane(lane)
    val top     = stackTop(context)
    val base    = context.frame.localBase
    val pointer = ((base + top - storage.stack - bytes) & -alignment) - base
    if (java.lang.Long.compareUnsigned(bytes, VMAddressSpace.LocalFrameSize) > 0 || pointer < stackBottom(context))
      Left(stackOverflow(context, lane, pointer))
    else { storage.stack = top - pointer; Right(pointer) }
  }

  def stackRestore(context: VMContext, lane: WorkgroupLane, pointer: Long): Either[Fault, Unit] = {
    val top = stackTop(context)
    if (pointer < stackBottom(context) || pointer > top) Left(stackOverflow(context, lane, pointer))
    else Right(context.lane(lane).stack = top - pointer)
  }

  private def stackOverflow(context: VMContext, lane: WorkgroupLane, pointer: Long): Fault = Fault.Backend(
    s"lane $lane: stack pointer ${java.lang.Long.toUnsignedString(pointer)} is outside the " +
      s"${VMAddressSpace.LocalFrameSize}-byte stack of ${context.frame.routine.name}"
  )
}

/** The machine operations on a context: control requests become the step the interpreter takes, and effects run on its
  * workgroup and device in order, stopping at the first fault. A value class, so running an instruction allocates no
  * machine.
  */
@SuppressWarnings(Array("scalafix:DisableSyntax.var"))
private[isa] class VMMachineOps(val context: VMContext)
    extends AnyVal with NvidiaMachine[Long, WorkgroupLane, Array[Byte], VMWorkgroup, Flow] {

  inline def pc: Long              = context.pc.toInt.toLong
  inline def explicitCluster: Long = if (context.explicitCluster) 1L else 0L

  inline def clusterSize: Long                               = context.workgroup.cluster.length.toLong
  inline def ctaRank: Long                                   = context.workgroup.clusterRank.toLong
  inline def rankOf(address: Long): Long                     = VMAddressSpace.clusterRank(address).toLong
  inline def clusterAddress(address: Long, rank: Long): Long = VMAddressSpace.clusterAddress(address, rank.toInt)

  inline def inCluster(address: Long, bytes: Long): Long = if (VMAsync.inCluster(context, address, bytes)) 1L else 0L

  inline def advance: Flow[Step] = Advanced
  inline def done: Flow[Unit]    = Completed

  inline def uniform(operand: Value): Long = context.active.headOption match {
    case Some(lane) => context.read(lane, operand)
    case None       => 0L
  }

  inline def jump(target: Pc): Flow[Step] = Right(Step.Jump(target))

  inline def jumpTo(target: Long): Flow[Step] = VMMachineOps.pcOf(target) match {
    case Left(fault)     => Left(fault)
    case Right(location) => Right(Step.Jump(location))
  }

  inline def branch(condition: Long, taken: Pc): Flow[Step] =
    if (condition != 0L) Right(Step.Jump(taken)) else advance

  inline def branch(condition: Long, taken: Pc, otherwise: Pc): Flow[Step] =
    Right(Step.Jump(if (condition != 0L) taken else otherwise))

  inline def jumpEach(inline target: WorkgroupLane => Long): Flow[Step] = {
    val locations = context.values(context.workgroup.laneCount)
    context.active.foreachLane(lane => locations(lane.raw) = target(lane))
    VMMachineOps.split(context, locations)
  }

  inline def branchEach(inline condition: WorkgroupLane => Long, taken: Pc): Flow[Step] = {
    val taking = context.active.filterLanes(lane => condition(lane) != 0L)
    val arms   = if (taking.isEmpty) Vec.empty[Arm] else Vec(Arm(taken, taking))
    Right(Step.Branches(arms, context.active.subtract(taking)))
  }

  inline def exit: Flow[Step]          = Right(Step.Exit(context.active))
  inline def returnControl: Flow[Step] = Right(Step.Return(context.active))

  inline def park(barrier: Long, count: Long, inline blocking: Boolean): Flow[Step] =
    VMMachineOps.uniformBarrier(context, barrier, count, blocking, None)

  inline def parkReduce(barrier: Long, count: Long, reduction: BarrierReduction): Flow[Step] =
    VMMachineOps.uniformBarrier(context, barrier, count, blocking = true, Some(reduction))

  inline def parkEach(
      inline barrier: WorkgroupLane => Long,
      inline count: WorkgroupLane => Long,
      inline blocking: Boolean,
      reduction: Option[BarrierReduction]
  ): Flow[Step] = {
    val barriers = Array.ofDim[Long](context.workgroup.laneCount)
    val counts   = Array.ofDim[Long](context.workgroup.laneCount)
    context.active.foreachLane { lane =>
      barriers(lane.raw) = barrier(lane)
      counts(lane.raw) = count(lane)
    }
    VMMachineOps.divergentBarrier(context, barriers, counts, blocking, reduction)
  }

  inline def warpSync(memberMask: Value): Flow[Step] = VMMachineOps.warpSync(context, memberMask)

  inline def clusterBarrier(arrive: Boolean): Flow[Step] = Right(Step.ClusterBarrier(arrive))

  inline def call(function: Value, returns: Vec[Parameter], parameters: Vec[Parameter]): Flow[Step] =
    VMMachineOps.call(context, function, returns, parameters)

  inline def branchTable(index: Value, targets: Vec[Pc]): Flow[Step] = VMMachineOps.branchTable(context, index, targets)

  inline def trap: Flow[Step] =
    Left(Fault.Trap(s"${context.kernel.isaName.toLowerCaseRoot} trap 0 at pc ${context.pc}"))

  inline def fenceHost: Flow[Step] = context.device.memory.fenceHostMemory().map(_ => Step.Advance)

  inline def callBuiltin(builtin: Builtin, returns: Vec[Parameter], parameters: Vec[Parameter])(
      inline next: => Flow[Step]
  ): Flow[Step] = VMMachineOps.callBuiltin(context, builtin, returns, parameters) match {
    case Left(fault) => Left(fault)
    case _           => next
  }

  inline def setupConvergence(token: Register)(inline next: => Flow[Step]): Flow[Step] = {
    VMMachineOps.setupConvergence(context, token)
    next
  }

  inline def synchroniseConvergence(token: Register): Flow[Step] = Right(Step.ConvergenceSync(token))

  inline def leaveConvergence(token: Register)(inline next: => Flow[Step]): Flow[Step] = {
    VMMachineOps.leaveConvergence(context, token)
    next
  }

  inline def everyLane[A](inline body: WorkgroupLane => Flow[Unit])(inline next: => Flow[A]): Flow[A] = {
    def lanes(context: VMContext): Flow[Unit] = {
      var failure: Flow[Unit] = Completed
      context.active.foreachLane { lane =>
        if (failure.isRight) failure = body(lane)
      }
      failure
    }
    lanes(context) match {
      case Left(fault) => Left(fault)
      case _           => next
    }
  }

  inline def read(lane: WorkgroupLane, operand: Value): Long = context.read(lane, operand)

  inline def write[A](lane: WorkgroupLane, target: Register, value: Long)(inline next: => Flow[A]): Flow[A] = {
    context.effects.stageRegister(lane, target, value)
    next
  }

  inline def load[A](lane: WorkgroupLane, inline space: Space, address: Long, width: ScalarBitWidth)(
      inline next: Long => Flow[A]
  ): Flow[A] =
    if (space eq Space.Constant) Left(VMMachineOps.constantFault)
    else
      VMMemoryAccess.loadValue(context, lane, VMMachineOps.access(space), address, width) match {
        case Left(fault) => Left(fault)
        case _           => next(context.scalar.elem)
      }

  inline def store[A](lane: WorkgroupLane, inline space: Space, address: Long, value: Long, width: ScalarBitWidth)(
      inline next: => Flow[A]
  ): Flow[A] =
    if (space eq Space.Constant) Left(VMMachineOps.constantFault)
    else {
      VMMemoryAccess.stageStore(context, context.effects, lane, VMMachineOps.access(space), address, width, value)
      next
    }

  inline def loadBytes[A](lane: WorkgroupLane, inline space: Space, address: Long, length: Long, what: String)(
      inline next: Array[Byte] => Flow[A]
  ): Flow[A] = VMMachineOps.readBytes(context, lane, space, address, length.toInt, what) match {
    case Left(fault) => Left(fault)
    case Right(data) => next(data)
  }

  inline def storeBytes[A](lane: WorkgroupLane, inline space: Space, address: Long, data: Array[Byte], what: String)(
      inline next: => Flow[A]
  ): Flow[A] = VMMachineOps.writeBytes(context, lane, space, address, data, what) match {
    case Left(fault) => Left(fault)
    case _           => next
  }

  inline def compute(primitive: Primitive[Primitive.Unary], first: Long): Long = primitive.function(first)

  inline def compute(primitive: Primitive[Primitive.Binary], first: Long, second: Long): Long =
    primitive.function(first, second)

  @SuppressWarnings(Array("scalafix:PublicSignatures.primitive-run"))
  inline def compute(primitive: Primitive[Primitive.Ternary], first: Long, second: Long, third: Long): Long =
    primitive.function(first, second, third)

  inline def require[A](condition: Long, label: String)(inline fault: => Fault)(inline next: => Flow[A]): Flow[A] =
    if (condition != 0L) next else Left(fault)

  inline def converged: Long = if (VMMachineOps.converged(context)) 1L else 0L

  inline def agreed(lane: WorkgroupLane, view: Long): Long =
    if (VMMachineOps.agreed(context, (view + (lane.raw - laneIndex(lane))).toInt)) 1L else 0L

  inline def unitSize: Long = 32L

  inline def laneIndex(lane: WorkgroupLane): Long = lane.raw % unitSize

  inline def gather[A](slot: UnitAlgebra.Slot, operand: Value)(inline next: Long => Flow[A]): Flow[A] = {
    val base   = slot.ordinal * context.workgroup.laneCount
    val values = context.values(UnitAlgebra.Slots * context.workgroup.laneCount)
    java.util.Arrays.fill(values, base, base + context.workgroup.laneCount, 0L)
    context.active.foreachLane(lane => values(base + lane.raw) = context.read(lane, operand))
    next(base.toLong)
  }

  inline def gatherLoad[A](
      slot: UnitAlgebra.Slot,
      lanes: Int,
      inline space: Space,
      address: Value,
      offset: Long,
      width: ScalarBitWidth
  )(inline next: Long => Flow[A]): Flow[A] = {
    val base   = slot.ordinal * context.workgroup.laneCount
    val values = context.values(UnitAlgebra.Slots * context.workgroup.laneCount)
    java.util.Arrays.fill(values, base, base + context.workgroup.laneCount, 0L)
    var failure: Flow[Unit] = Completed
    context.active.foreachLane { lane =>
      if (failure.isRight && laneIndex(lane) < lanes)
        failure = load(lane, space, context.read(lane, address) + offset, width) { value =>
          values(base + lane.raw) = value
          Completed
        }
    }
    failure match {
      case Left(fault) => Left(fault)
      case _           => next(base.toLong)
    }
  }

  inline def peer(lane: WorkgroupLane, view: Long, index: Long): Long =
    if (java.lang.Long.compareUnsigned(index, unitSize) < 0)
      context.values(0)(view.toInt + (lane.raw - laneIndex(lane)).toInt + index.toInt)
    else 0L

  inline def active(lane: WorkgroupLane, index: Long): Long =
    if (
      java.lang.Long.compareUnsigned(index, unitSize) < 0 &&
      context.active.contains((lane.raw - laneIndex(lane) + index).toInt)
    ) 1L
    else 0L

  inline def writeEach[A](targets: Vec[Register])(inline value: (WorkgroupLane, Int) => Long)(
      inline next: => Flow[A]
  ): Flow[A] = {
    context.active.foreachLane { lane =>
      var index = 0
      while (index < targets.length) {
        context.effects.stageRegister(lane, targets(index), value(lane, index))
        index += 1
      }
    }
    next
  }

  inline def sharedWindow(lane: WorkgroupLane, address: Long, bytes: Long, what: String)(
      inline next: (VMWorkgroup, Long) => Flow[Unit]
  ): Flow[Unit] = VMAsync.sharedTarget(context, address, bytes, what) match {
    case Left(fault)            => Left(fault)
    case Right((owner, offset)) => next(owner, offset)
  }

  inline def requireShared(address: Long, what: String)(inline next: => Flow[Unit]): Flow[Unit] =
    if (VMAddressSpace.isShared(address)) next else Left(Fault.Backend(s"$what 0x${address.toHexString}"))

  inline def eachDestination(lane: WorkgroupLane, cta: VMWorkgroup, multicast: Option[Value])(
      inline each: VMWorkgroup => Flow[Unit]
  )(inline next: => Flow[Unit]): Flow[Unit] = {
    def one(destination: VMWorkgroup): Flow[Unit] = each(destination)
    (multicast match {
      case None       => one(cta)
      case Some(mask) =>
        VMAsync.destinations(context, context.read(lane, mask)) match {
          case Left(fault)    => Left(fault)
          case Right(targets) =>
            var result: Flow[Unit] = Completed
            var index              = 0
            while (index < targets.size && result.isRight) {
              result = one(targets(index))
              index += 1
            }
            result
        }
    }) match {
      case Left(fault) => Left(fault)
      case _           => next
    }
  }

  inline def eachRank(inline each: Long => Flow[Unit]): Flow[Unit] = {
    var result: Flow[Unit] = Completed
    var rank               = 0
    while (rank < context.workgroup.cluster.length && result.isRight) {
      result = each(rank.toLong)
      rank += 1
    }
    result
  }

  inline def storeShared(address: Long, value: Long, width: ScalarBitWidth)(inline next: => Flow[Unit]): Flow[Unit] = {
    context.effects.stageShared(address, width, value)
    next
  }

  inline def storeSharedBytes(address: Long, data: Array[Byte], size: Long)(inline next: => Flow[Unit]): Flow[Unit] = {
    var offset = 0L
    while (offset < size) {
      context.effects.stageShared(address + offset, 8, if (offset < data.length) data(offset.toInt).toLong else 0L)
      offset += 1L
    }
    next
  }

  inline def storeCluster(cta: VMWorkgroup, offset: Long, data: Array[Byte])(inline next: => Flow[Unit]): Flow[Unit] = {
    cta.writeSharedBytes(offset, data, 0, data.length)
    next
  }

  inline def completeTx(cta: VMWorkgroup, barrier: Long, bytes: Long)(inline next: => Flow[Unit]): Flow[Unit] =
    VMAsync.complete(cta, barrier, bytes) match {
      case Left(fault) => Left(fault)
      case _           => next
    }

  inline def sameCta(first: VMWorkgroup, second: VMWorkgroup): Long = if (first eq second) 1L else 0L

  inline def clearCluster(cta: VMWorkgroup, offset: Long, bytes: Long)(inline next: => Flow[Unit]): Flow[Unit] = {
    cta.writeSharedBytes(offset, Array.ofDim(bytes.toInt), 0, bytes.toInt)
    next
  }

  inline def storeWord(cta: VMWorkgroup, offset: Long, value: Long, width: ScalarBitWidth)(
      inline next: => Flow[Unit]
  ): Flow[Unit] = {
    cta.writeShared(offset, width, value)
    next
  }

  inline def reduceWord(
      cta: VMWorkgroup,
      offset: Long,
      operand: Long,
      width: ScalarBitWidth,
      operation: AtomicOperation
  )(inline next: => Flow[Unit]): Flow[Unit] = {
    cta.writeShared(offset, width, operation(cta.readShared(offset, width), operand, width))
    next
  }

  inline def reduceBytes(
      cta: VMWorkgroup,
      offset: Long,
      data: Array[Byte],
      copy: Instruction.AsyncBulkReduce
  )(inline next: => Flow[Unit]): Flow[Unit] = VMAsync.reduceShared(cta, offset, data, copy) match {
    case Left(fault) => Left(fault)
    case _           => next
  }

  inline def reduceGlobal(
      lane: WorkgroupLane,
      address: Long,
      data: Array[Byte],
      copy: Instruction.AsyncBulkReduce
  )(inline next: => Flow[Unit]): Flow[Unit] =
    VMAsync.reduceGlobal(context, lane, address, data, copy) match {
      case Left(fault) => Left(fault)
      case _           => next
    }

  @SuppressWarnings(Array("scalafix:PublicSignatures.primitive-run"))
  inline def storeGlobalMasked(lane: WorkgroupLane, destination: Long, source: Long, bytes: Long, mask: Long)(
      inline next: => Flow[Unit]
  ): Flow[Unit] = {
    VMAsync.storeMasked(context, lane, destination, source, bytes, mask)
    next
  }

  inline def tensorTransfer(
      map: Array[Byte],
      coordinates: Vec[Long],
      offsets: Vec[Long],
      copy: Instruction.AsyncTensor
  )(inline next: Array[Byte] => Flow[Unit]): Flow[Unit] =
    next(TensorLayout.transfer(map, coordinates, offsets, copy))

  inline def tensorFact(transfer: Array[Byte], fact: TensorLayout.Fact): Long = TensorLayout.fact(transfer, fact)

  def tensorField(map: Array[Byte], kind: TensorMapping.Field, axis: Int): Long =
    TensorMap.field(
      Execute,
      (offset, bytes) => LittleEndian.decode(map, offset, bytes.unsafe[Int in 0 ~ 8]),
      kind,
      axis
    )

  inline def tensorText(transfer: Array[Byte], text: TensorLayout.Text): String = TensorLayout.text(transfer, text)

  inline def eachIndex(count: Long)(inline each: Long => Flow[Unit])(inline next: => Flow[Unit]): Flow[Unit] = {
    var result: Flow[Unit] = Completed
    var index              = 0L
    while (index < count && result.isRight) {
      result = each(index)
      index += 1L
    }
    result match {
      case Left(fault) => Left(fault)
      case _           => next
    }
  }

  inline def eachRun(transfer: Array[Byte])(inline each: Long => Flow[Unit])(inline next: => Flow[Unit]): Flow[Unit] =
    eachIndex(TensorLayout.fact(transfer, TensorLayout.Fact.Runs))(each)(next)

  inline def loadRun(lane: WorkgroupLane, transfer: Array[Byte], run: Long)(inline next: => Flow[Unit]): Flow[Unit] =
    TensorLayout.loadRun(
      context.device.memory,
      transfer,
      run.toInt
    ).leftMap(VMWriteBuffer.located(_, context.pc, lane)) match {
      case Left(fault) => Left(fault)
      case _           => next
    }

  inline def storeRun(lane: WorkgroupLane, transfer: Array[Byte], run: Long)(inline next: => Flow[Unit]): Flow[Unit] =
    TensorLayout.storeRun(
      context.device.memory,
      transfer,
      run.toInt
    ).leftMap(VMWriteBuffer.located(_, context.pc, lane)) match {
      case Left(fault) => Left(fault)
      case _           => next
    }

  inline def reduceRun(lane: WorkgroupLane, transfer: Array[Byte], run: Long, operator: TensorMap.Reduction)(
      inline next: => Flow[Unit]
  ): Flow[Unit] =
    TensorLayout.reduceRun(context.device.memory, transfer, run.toInt, operator)
      .leftMap(VMWriteBuffer.located(_, context.pc, lane)) match {
      case Left(fault) => Left(fault)
      case _           => next
    }

  inline def placeBox(cta: VMWorkgroup, start: Long, transfer: Array[Byte])(inline next: => Flow[Unit]): Flow[Unit] = {
    TensorLayout.place(transfer, start)((offset, from, count) => cta.writeSharedBytes(offset, transfer, from, count))
    next
  }

  inline def fetchBox(cta: VMWorkgroup, start: Long, transfer: Array[Byte])(inline next: => Flow[Unit]): Flow[Unit] = {
    TensorLayout.place(transfer, start)((offset, from, count) => cta.readSharedBytes(offset, transfer, from, count))
    next
  }

  inline def peerRank(cta: VMWorkgroup, signalled: VMWorkgroup): Long =
    ((cta.clusterRank & ~1) | (signalled.clusterRank & 1)).toLong

  inline def hasCta(rank: Long): Long = if (VMMachineOps.ctaAt(context, rank).isDefined) 1L else 0L

  inline def ctaAt(rank: Long): VMWorkgroup = context.workgroup.cluster(rank.toInt).nn

  inline def replaceTensorMap(map: Array[Byte], field: TensorMap.Field, index: Int, value: Long)(
      inline next: Array[Byte] => Flow[Unit]
  ): Flow[Unit] = {
    TensorMap.update(map, field, index, value)
    next(map)
  }

  inline def mbarrierInit(lane: WorkgroupLane, barrier: Long, count: Long)(inline next: => Flow[Unit]): Flow[Unit] =
    VMMachineOps.mbarrierInit(context, barrier, count) match {
      case Left(fault) => Left(fault)
      case _           => next
    }

  inline def mbarrierInvalidate(lane: WorkgroupLane, barrier: Long)(inline next: => Flow[Unit]): Flow[Unit] =
    VMMachineOps.mbarrierInvalidate(context, barrier) match {
      case Left(fault) => Left(fault)
      case _           => next
    }

  inline def mbarrierLoad(lane: WorkgroupLane, barrier: Long)(inline next: => Flow[Unit]): Flow[Unit] =
    VMMachineOps.mbarrierLoad(context, barrier) match {
      case Left(fault) => Left(fault)
      case _           => next
    }

  @SuppressWarnings(Array("scalafix:PublicSignatures.primitive-run"))
  inline def mbarrierArrive(
      lane: WorkgroupLane,
      barrier: Long,
      count: Long,
      bytes: Long,
      drop: Boolean,
      complete: Boolean
  )(inline next: Long => Flow[Unit]): Flow[Unit] =
    VMAsync.mbarrier(context, barrier).flatMap(_.arrive(count, bytes, drop, complete)) match {
      case Left(fault)  => Left(fault)
      case Right(token) => next(token)
    }

  inline def mbarrierTransaction(lane: WorkgroupLane, barrier: Long, bytes: Long, complete: Boolean)(
      inline next: => Flow[Unit]
  ): Flow[Unit] = VMMachineOps.mbarrierTransaction(context, barrier, bytes, complete) match {
    case Left(fault) => Left(fault)
    case _           => next
  }

  inline def mbarrierTest(lane: WorkgroupLane, barrier: Long, phase: Long, parity: Boolean)(
      inline next: Long => Flow[Unit]
  ): Flow[Unit] = VMMachineOps.mbarrierTest(context, barrier, phase, parity) match {
    case Left(fault)     => Left(fault)
    case Right(complete) => next(if (complete) 1L else 0L)
  }

  inline def writePredicate(lane: WorkgroupLane, target: Predicate, value: Long)(
      inline next: => Flow[Unit]
  ): Flow[Unit] = {
    context.effects.stage(VMWrite.Predicate(lane, target, value != 0L))
    next
  }

  inline def stackPointer(lane: WorkgroupLane): Long = VMMachineOps.stackTop(context) - context.lane(lane).stack

  inline def allocate(lane: WorkgroupLane, size: Long, alignment: Long)(inline next: Long => Flow[Unit]): Flow[Unit] =
    VMMachineOps.stackAllocate(context, lane, size, alignment) match {
      case Left(fault)    => Left(fault)
      case Right(pointer) => next(pointer)
    }

  inline def restoreStack(lane: WorkgroupLane, pointer: Long)(inline next: => Flow[Unit]): Flow[Unit] =
    VMMachineOps.stackRestore(context, lane, pointer) match {
      case Left(fault) => Left(fault)
      case _           => next
    }

  inline def pushReturn(lane: WorkgroupLane, address: Long)(inline next: => Flow[Unit]): Flow[Unit] = {
    context.lane(lane).returnStack.push(address)
    next
  }

  inline def unitIndex(lane: WorkgroupLane): Long = lane.raw / unitSize

  inline def when(condition: Long)(inline body: => Flow[Unit])(inline next: => Flow[Unit]): Flow[Unit] =
    (if (condition != 0L) body else Completed) match {
      case Left(fault) => Left(fault)
      case _           => next
    }

  inline def withCta[A](rank: Long)(inline next: VMWorkgroup => Flow[A]): Flow[A] = next(ctaAt(rank))

  inline def valueText(text: TensorMemoryLayout.Text, value: Long): String = TensorMemoryLayout.text(text, value)

  inline def tensorMemoryPermitted(cta: VMWorkgroup): Long = if (cta.tensorMemory.permitted) 1L else 0L

  inline def tensorMemoryTaken(cta: VMWorkgroup): Long = cta.tensorMemory.taken

  inline def relinquishTensorMemory(cta: VMWorkgroup)(inline next: => Flow[Step]): Flow[Step] = {
    cta.tensorMemory.relinquish()
    next
  }

  inline def allocateTensorMemory(cta: VMWorkgroup, granules: Long)(inline next: Long => Flow[Unit]): Flow[Unit] =
    next(cta.tensorMemory.allocate(granules.toInt).fold(TensorMemory.Columns.toLong)(start =>
      (start * TensorMemory.Granule).toLong
    ))

  inline def freeTensorMemory(cta: VMWorkgroup, mask: Long)(inline next: Long => Flow[Unit]): Flow[Unit] =
    next(if (cta.tensorMemory.free(mask)) 1L else 0L)

  inline def tensorMemoryCell(cta: VMWorkgroup, lane: Long, column: Long): Long =
    cta.tensorMemory.read(lane.toInt, column.toInt)

  @SuppressWarnings(Array("scalafix:PublicSignatures.primitive-run"))
  inline def storeTensorMemory(cta: VMWorkgroup, lane: Long, column: Long, value: Long)(
      inline next: => Flow[Unit]
  ): Flow[Unit] = {
    cta.tensorMemory.write(lane.toInt, column.toInt, value)
    next
  }

  inline def storeTensorWords(cta: VMWorkgroup, lane: Long, column: Long, data: Array[Byte])(
      inline next: => Flow[Unit]
  ): Flow[Unit] = {
    (0 until 4).foreach(word =>
      cta.tensorMemory.write(lane.toInt, column.toInt + word, LittleEndian.decode(data, 4 * word, 4))
    )
    next
  }

  inline def shiftTensorMemory(cta: VMWorkgroup, lane: Long, column: Long)(inline next: => Flow[Unit]): Flow[Unit] = {
    TensorMemory.shiftRows(cta.tensorMemory, lane.toInt, column.toInt, rows = 32, columns = 8)
    next
  }

  type Gathered = VMTensorMemory.Gathered

  inline def gatherProduct(lane: WorkgroupLane, operation: Tcgen05.Multiply)(
      inline next: VMTensorMemory.Gathered => Flow[Unit]
  ): Flow[Unit] = VMTensorMemory.gather(context, lane, operation) match {
    case Left(fault)     => Left(VMWriteBuffer.located(fault, context.pc, lane))
    case Right(gathered) => next(gathered)
  }

  inline def elementCount(product: VMTensorMemory.Gathered): Long = product.size.toLong

  inline def computeElement(product: VMTensorMemory.Gathered, index: Long)(
      inline next: Long => Flow[Unit]
  ): Flow[Unit] = {
    product.at(index.toInt)
    next(TensorMemorySemantics.element(Execute, product.product, product))
  }

  inline def storeElement(lane: WorkgroupLane, product: VMTensorMemory.Gathered, index: Long, value: Long)(
      inline next: => Flow[Unit]
  ): Flow[Unit] = product.store(index.toInt, value) match {
    case Left(fault) => Left(VMWriteBuffer.located(fault, context.pc, lane))
    case _           => next
  }

  inline def eachUnit(inline body: WorkgroupLane => Flow[Unit])(inline next: => Flow[Step]): Flow[Step] = {
    def units(context: VMContext): Flow[Unit] = {
      var failure: Flow[Unit] = Completed
      WorkgroupLane.warpBases.foreach { base =>
        if (failure.isRight && base < context.workgroup.laneCount) {
          val bits = context.active.warpBits(base)
          if (bits != 0L)
            failure = body(WorkgroupLane.fromIndex(base + java.lang.Long.numberOfTrailingZeros(bits)))
        }
      }
      failure
    }
    units(context) match {
      case Left(fault) => Left(fault)
      case _           => next
    }
  }

  inline def writeUnit(lane: WorkgroupLane, target: Register, value: Long)(inline next: => Flow[Unit]): Flow[Unit] = {
    VMMachineOps.eachActive(context, lane)(context.effects.stageRegister(_, target, value))
    next
  }

  inline def writeUnitPredicate(lane: WorkgroupLane, target: Predicate, value: Long)(
      inline next: => Flow[Unit]
  ): Flow[Unit] = {
    VMMachineOps.eachActive(context, lane)(member =>
      context.effects.stage(VMWrite.Predicate(member, target, value != 0L))
    )
    next
  }

  inline def retainTensorMemory(cta: VMWorkgroup, mask: Long)(inline next: => Flow[Unit]): Flow[Unit] = {
    cta.tensorMemory.retain(mask)
    next
  }

  inline def acquireLock(cta: VMWorkgroup, offset: Long, holder: Long)(inline next: Long => Flow[Unit]): Flow[Unit] = {
    val free = cta.sharedLocks.get(offset).forall(_ == holder.toInt)
    if (free) cta.sharedLocks(offset) = holder.toInt
    next(if (free) 1L else 0L)
  }

  inline def releaseLock(cta: VMWorkgroup, offset: Long, holder: Long)(inline next: Long => Flow[Unit]): Flow[Unit] = {
    val held = cta.sharedLocks.get(offset).contains(holder.toInt)
    if (held) cta.sharedLocks.subtractOne(offset)
    next(if (held) 1L else 0L)
  }

  inline def loadChunk(cta: VMWorkgroup, offset: Long, packing: Matrix.Packing)(
      inline next: Array[Byte] => Flow[Unit]
  ): Flow[Unit] = next(packing.unpack(VMTensorMemory.chunk(cta, offset)))
}
