package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchObserver.*

/** What an observed launch learns of a lane's memory accesses and asynchronous work. Every entry point is a single
  * field read and branch when no observer is attached.
  */
@SuppressWarnings(Array("scalafix:DisableSyntax.null"))
private object VMObservation {

  private def index(lane: WorkgroupLane): Int >= 0 = lane.toInt.unsafe[Int >= 0]

  private def count(bytes: Long): Int >= 0 = bytes.min(Int.MaxValue.toLong).toInt.unsafe[Int >= 0]

  /** The offset of a shared address within its CTA's shared window. */
  def sharedOffset(address: Long): Long = VMAddressSpace.sharedAddress(address) - VMAddressSpace.SharedBase

  /** An access to the space `access` resolves `address` to. */
  inline def access(
      context: VMContext,
      inline lane: WorkgroupLane,
      inline kind: AccessKind,
      inline space: MemoryAccess,
      inline address: Long,
      inline bytes: Long
  )
      : Unit =
    if (context.observer != null) resolved(context, lane, kind, space, address, bytes)

  /** An access to the shared window of some CTA of the cluster. */
  inline def shared(
      context: VMContext,
      inline lane: WorkgroupLane,
      inline kind: AccessKind,
      inline address: Long,
      inline bytes: Long
  ): Unit =
    if (context.observer != null) in(context, lane, Space.Shared, kind, sharedOffset(address), bytes)

  /** An atomic read-modify-write of the shared or global `address`. */
  inline def atomic(
      context: VMContext,
      inline lane: WorkgroupLane,
      inline address: Long,
      inline bytes: Long,
      inline shared: Boolean
  ): Unit =
    if (context.observer != null)
      if (shared) in(context, lane, Space.Shared, AccessKind.Atomic, sharedOffset(address), bytes)
      else in(context, lane, Space.Global, AccessKind.Atomic, address, bytes)

  /** An access to device memory. */
  inline def global(
      context: VMContext,
      inline lane: WorkgroupLane,
      inline kind: AccessKind,
      inline address: Long,
      inline bytes: Long
  ): Unit =
    if (context.observer != null) in(context, lane, Space.Global, kind, address, bytes)

  /** A step of asynchronous work that names no barrier. */
  inline def asynchronous(
      context: VMContext,
      inline lane: WorkgroupLane,
      inline kind: AsyncKind,
      inline bytes: Long
  ): Unit =
    if (context.observer != null) step(context, lane, kind, -1L, bytes)

  /** A transfer that completes on the mbarrier at the shared `barrier` of the CTA `cta`. */
  inline def transfer(
      context: VMContext,
      inline lane: WorkgroupLane,
      inline cta: VMWorkgroup,
      inline barrier: Long,
      inline bytes: Long
  ): Unit =
    if (context.observer != null) step(context, lane, AsyncKind.Transfer, target(cta, barrier), bytes)

  /** `kind` at the mbarrier at the shared address `address`, which `bytes` qualifies. */
  inline def barrier(
      context: VMContext,
      inline lane: WorkgroupLane,
      inline kind: AsyncKind,
      inline address: Long,
      inline bytes: Long
  ): Unit =
    if (context.observer != null) mbarrier(context, lane, kind, address, bytes)

  /** `kind` for each active lane's group of the class `bulk` names, which `bytes` qualifies. */
  inline def groups(context: VMContext, inline kind: AsyncKind, inline bulk: Boolean, inline bytes: Long): Unit =
    if (context.observer != null) context.active.foreachLane(lane => step(context, lane, kind, groupClass(bulk), bytes))

  /** The global run `run` of a tensor transfer, unless it lies outside the tensor. */
  inline def run(
      context: VMContext,
      inline lane: WorkgroupLane,
      inline kind: AccessKind,
      inline transfer: Array[Byte],
      inline run: Long
  ): Unit =
    if (context.observer != null && TensorLayout.runInside(transfer, run.toInt))
      in(
        context,
        lane,
        Space.Global,
        kind,
        TensorLayout.runAddress(transfer, run.toInt),
        TensorLayout.runBytes(transfer, run.toInt)
      )

  /** The shared chunks of a tensor transfer's box, placed from the shared address `start`. */
  inline def box(
      context: VMContext,
      inline lane: WorkgroupLane,
      inline kind: AccessKind,
      inline start: Long,
      inline transfer: Array[Byte]
  ): Unit =
    if (context.observer != null) chunks(context, lane, kind, start, transfer)

  private def chunks(
      context: VMContext,
      lane: WorkgroupLane,
      kind: AccessKind,
      start: Long,
      transfer: Array[Byte]
  ): Unit =
    TensorLayout.place(transfer, start)((offset, _, count) =>
      in(context, lane, Space.Shared, kind, sharedOffset(offset), count.toLong)
    )

  private def groupClass(bulk: Boolean): Long = if (bulk) 1L else 0L

  private def target(cta: VMWorkgroup, barrier: Long): Long =
    sharedOffset(barrier) | (cta.clusterRank.toLong << 32)

  private def in(context: VMContext, lane: WorkgroupLane, space: Space, kind: AccessKind, address: Long, bytes: Long)
      : Unit = context.observer match {
    case null                     => ()
    case observer: LaunchObserver => observer.access(index(lane), space, kind, address, count(bytes))
  }

  private def step(context: VMContext, lane: WorkgroupLane, kind: AsyncKind, target: Long, bytes: Long): Unit =
    context.observer match {
      case null                     => ()
      case observer: LaunchObserver => observer.asynchronous(index(lane), kind, target, bytes)
    }

  private def mbarrier(context: VMContext, lane: WorkgroupLane, kind: AsyncKind, address: Long, bytes: Long): Unit =
    context.workgroup.sharedOwner(address) match {
      case null               => ()
      case owner: VMWorkgroup => step(context, lane, kind, target(owner, address), bytes)
    }

  private def resolved(
      context: VMContext,
      lane: WorkgroupLane,
      kind: AccessKind,
      space: MemoryAccess,
      address: Long,
      bytes: Long
  ): Unit = VMMemoryAccess.route(context, space, address, count(bytes))(
    (_, _) => in(context, lane, Space.Constant, kind, address, bytes),
    in(context, lane, Space.Shared, kind, sharedOffset(address), bytes),
    local => in(context, lane, Space.Local, kind, local, bytes),
    in(context, lane, Space.Global, kind, address, bytes)
  )
}
