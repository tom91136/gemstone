package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
import scala.runtime.LongRef

/** The machine state visible to one instruction issue: the dispatch, the workgroup and the issuing frame and lanes. */
@SuppressWarnings(Array("scalafix:DisableSyntax.var"))
private class VMContext(
    val kernel: Kernel,
    val geometry: LaunchGeometry,
    val group: Coordinates,
    val workgroup: VMWorkgroup,
    val memory: DeviceHeap,
    val control: TaskControl,
    val workgroups: Workgroups,
    val dynamicSharedBytes: Int >= 0,
    var frame: VMFrame
) {
  var pc: Pc             = Pc.Entry
  var active: VMLaneMask = VMLaneMask.Empty

  // Reused by one instruction at a time, so per-lane memory reads allocate nothing.
  val scalar: LongRef                     = LongRef(0L)
  val reads: AddressSpace.ReadRangeBuffer = AddressSpace.ReadRangeBuffer()

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
    case Value.Address(base, offset) => read(lane, base) + offset
    case Value.LocalBase             => frame.localBase
    case Value.DynamicSharedBytes    => dynamicSharedBytes.raw.toLong
    case Value.LaneId                => (lane.raw & 31).toLong
    case Value.ActiveMask            => active.warpBits(lane.raw)
    case Value.LiveMask              => frame.live.warpBits(lane.raw)
    case Value.LaneMaskGreaterEqual  => (0xffffffffL << (lane.raw & 31)) & 0xffffffffL
    case Value.LaneMaskGreater       => (0xffffffffL << ((lane.raw & 31) + 1)) & 0xffffffffL
    case Value.LaneMaskLessEqual     => (1L << ((lane.raw & 31) + 1)) - 1L
    case Value.LaneMaskLess          => (1L << (lane.raw & 31)) - 1L
    case Value.LaneMaskEqual         => 1L << (lane.raw & 31)
    case Value.ThreadId(axis)        =>
      val sizeX = geometry.groupSize.x.raw
      (axis match {
        case Axis.X => lane.raw           % sizeX
        case Axis.Y => (lane.raw / sizeX) % geometry.groupSize.y.raw
        case Axis.Z => lane.raw / sizeX / geometry.groupSize.y.raw
      }).toLong
    case Value.BlockId(axis)  => axis.select(group).raw.toLong
    case Value.BlockDim(axis) => axis.select(geometry.groupSize).raw.toLong
    case Value.GridDim(axis)  => axis.select(geometry.groupCount).raw.toLong
  }

  def predicate(lane: WorkgroupLane, predicate: Predicate): Either[Fault, Boolean] =
    frame.lanes(lane).predicate(predicate).toRight(
      Fault.Backend(s"lane $lane: undefined ${frame.routine.describe(predicate)} at pc $pc")
    )
}
