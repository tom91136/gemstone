package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import net.kurobako.gemstone.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
import scala.runtime.LongRef

private object VMContext {
  private val timerOrigin = platform.epochMillis * 1000000L - platform.monotonicNanos

  def globalTimer: Long = timerOrigin + platform.monotonicNanos

  /** Runs a grid device code launches: its kernel, geometry and parameter block, on the launching grid's device, with
    * its dynamic shared bytes.
    */
  type Launch = (Kernel, LaunchGeometry, Array[Byte], VMDevice, Int >= 0) => Either[Fault, Unit]
}

/** The machine state visible to one instruction issue: the dispatch, the workgroup, the issuing frame and lanes, and
  * the buffer that stages the instruction's effects.
  */
@SuppressWarnings(Array("scalafix:DisableSyntax.var"))
private class VMContext(
    val kernel: Kernel,
    val geometry: LaunchGeometry,
    val group: Coordinates,
    val workgroup: VMWorkgroup,
    val device: VMDevice,
    val dynamicSharedBytes: Int >= 0,
    val effects: VMWriteBuffer,
    var frame: VMFrame,
    val launch: VMContext.Launch,
    val clusterShape: Dimensions = Dimensions.One,
    val explicitCluster: Boolean = false
) {
  var pc: Pc             = Pc.Entry
  var active: VMLaneMask = VMLaneMask.Empty
  var issued: Long       = 0L

  private val sharedWindow =
    if (explicitCluster)
      VMAddressSpace.ClusterWindowBase + (workgroup.clusterRank.toLong << VMAddressSpace.ClusterRankShift)
    else VMAddressSpace.SharedBase

  // Reused by one instruction at a time, so per-lane memory reads allocate nothing.
  val scalar: LongRef                     = LongRef(0L)
  val reads: AddressSpace.ReadRangeBuffer = AddressSpace.ReadRangeBuffer()
  // The registers the current issue touches; the interpreter starts and stops its collection around the instruction.
  val use: VMRegisterUse = VMRegisterUse()
  private var scratch    = Array.ofDim[Long](4)

  def lane(lane: WorkgroupLane): VMLane = frame.lanes(lane)

  /** Every operand is resolved when lowered, so reading a value cannot fail. Registers and immediates are kept in this
    * small method so the JIT inlines it into each lane loop.
    */
  def read(lane: WorkgroupLane, value: Value): Long = value match {
    case Value.Register(register)   => frame.lanes(lane).register(register)
    case Value.Immediate(immediate) => immediate
    case other                      => readOther(lane, other)
  }

  private def readOther(lane: WorkgroupLane, value: Value): Long = (value: @unchecked) match {
    case Value.Predicate(predicate)  => if (frame.lanes(lane).predicate(predicate).getOrElse(false)) 1L else 0L
    case Value.Address(base, offset) => (if (base eq Value.SharedWindow) sharedWindow else read(lane, base)) + offset
    case Value.LocalBase             => frame.localBase
    case Value.SharedWindow          => sharedWindow
    case Value.DynamicSharedBytes    => dynamicSharedBytes.toLong
    case Value.LaneId                => (lane & 31).toLong
    case Value.ActiveMask            => active.warpBits(lane.raw)
    case Value.LiveMask              => frame.live.warpBits(lane.raw)
    case Value.LaneMaskGreaterEqual  => (0xffffffffL << (lane & 31)) & 0xffffffffL
    case Value.LaneMaskGreater       => (0xffffffffL << ((lane & 31) + 1)) & 0xffffffffL
    case Value.LaneMaskLessEqual     => (1L << ((lane & 31) + 1)) - 1L
    case Value.LaneMaskLess          => (1L << (lane & 31)) - 1L
    case Value.LaneMaskEqual         => 1L << (lane & 31)
    case Value.ThreadId(axis)        =>
      val sizeX = geometry.groupSize.x.raw
      (axis match {
        case Axis.X => lane           % sizeX
        case Axis.Y => (lane / sizeX) % geometry.groupSize.y
        case Axis.Z => lane / sizeX / geometry.groupSize.y
      }).toLong
    case Value.BlockId(axis)     => axis.select(group).toLong
    case Value.BlockDim(axis)    => axis.select(geometry.groupSize).toLong
    case Value.GridDim(axis)     => axis.select(geometry.groupCount).toLong
    case Value.Clock(part)       => part.of(issued)
    case Value.GlobalTimer(part) => part.of(VMContext.globalTimer)
    case Value.MultiprocessorId  =>
      val count = geometry.groupCount
      (group.x.toLong + count.x.toLong * (group.y.toLong + count.y.toLong * group.z.toLong)) %
        EmulatedDevice.Multiprocessors
    case Value.WarpId          => (lane >>> 5).toLong
    case Value.ClusterCtaRank  => workgroup.clusterRank.toLong
    case Value.ClusterCtaCount =>
      clusterShape.x.toLong * clusterShape.y.toLong * clusterShape.z.toLong
    case Value.ClusterCtaId(axis) => (axis.select(group).raw % axis.select(clusterShape)).toLong
    case Value.ClusterShape(axis) => axis.select(clusterShape).toLong
    case Value.ClusterId(axis)    => (axis.select(group).raw / axis.select(clusterShape)).toLong
    case Value.ClusterCount(axis) => (axis.select(geometry.groupCount) / axis.select(clusterShape)).toLong
    case Value.ExplicitCluster    => if (explicitCluster) 1L else 0L
  }

  def predicate(lane: WorkgroupLane, predicate: Predicate): Either[Fault, Boolean] =
    frame.lanes(lane).predicate(predicate).toRight(undefined(lane, predicate))

  def undefined(lane: WorkgroupLane, predicate: Predicate): Fault =
    Fault.Backend(s"lane $lane: undefined ${frame.routine.describe(predicate)} at pc $pc")

  /** Room for `size` values, reused by one instruction at a time. */
  def values(size: Int): Array[Long] = {
    if (scratch.length < size) scratch = Array.ofDim[Long](size)
    scratch
  }
}
