package net.kurobako.gemstone.isa.nvidia

import VMExecution.*
import VMIR.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.vm.*

private object LaneFaults {
  def undefinedFlag(context: VMContext, lane: WorkgroupLane, flag: VMConditionCodes.Flag): Fault =
    Fault.Backend(s"condition flag $flag is undefined at pc ${context.pc}, lane $lane")

  def undefinedCarry(context: VMContext, lane: WorkgroupLane): Fault =
    Fault.Backend(s"IADD3 carry state is undefined at pc ${context.pc}, lane $lane")

  def absent(instruction: String, lane: WorkgroupLane): Fault =
    Fault.Backend(s"$instruction lane $lane is absent from its member mask")

  /** The predicates of the lanes in `members` of `lane`'s warp as bits, or -1 when any is undefined. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def gather(context: VMContext, lane: WorkgroupLane, members: Long, predicate: Predicate): Long = {
    val warpBase  = lane & ~31
    var remaining = members
    var votes     = 0L
    while (remaining != 0L && votes >= 0L) {
      val index = java.lang.Long.numberOfTrailingZeros(remaining)
      context.lane(WorkgroupLane.fromIndex(warpBase + index)).predicate(predicate) match {
        case Some(true)  => votes |= 1L << index
        case Some(false) => ()
        case None        => votes = -1L
      }
      remaining &= remaining - 1L
    }
    votes
  }

  /** The fault for the first lane in `members` whose predicate is undefined. */
  def undefinedVote(context: VMContext, lane: WorkgroupLane, members: Long, predicate: Predicate): Fault = {
    val warpBase = lane & ~31
    val index    = (0 until 32).find(index =>
      (members & (1L << index)) != 0L &&
        context.lane(WorkgroupLane.fromIndex(warpBase + index)).predicate(predicate).isEmpty
    ).getOrElse(0)
    context.undefined(WorkgroupLane.fromIndex(warpBase + index), predicate)
  }

  /** An atomic read-modify-write on every active lane, which the write buffer applies when it commits, in its atomic
    * order.
    */
  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  class Atomic(
      context: VMContext,
      fetched: Option[Register],
      address: Value,
      source: Value,
      width: ScalarBitWidth,
      space: MemorySpace
  ) extends Sink.Binary[Long, Either[Fault, Unit]] {
    inline def apply(inline kernel: (Long, Long) => Long): Either[Fault, Unit] = {
      class Combined(operand: Long) extends AtomicUpdate {
        def apply(previous: Long): Long = kernel(previous, operand)
      }
      def lanes(
          context: VMContext,
          fetched: Option[Register],
          address: Value,
          source: Value,
          width: ScalarBitWidth,
          space: MemorySpace
      ): Either[Fault, Unit] = everyLane(context) { lane =>
        val raw = context.read(lane, address)
        VMMemoryAccess.misalignedAtomic(context, lane, raw, width) match {
          case null =>
            context.effects.stage(VMWrite.Atomic(
              lane,
              fetched,
              raw,
              width,
              VMMemoryAccess.atomicTargetsShared(space, raw, width),
              Combined(context.read(lane, source))
            ))
            Completed
          case fault: Fault => Left(fault)
        }
      }
      lanes(context, fetched, address, source, width, space)
    }
  }

  /** `function` on every active lane until the first fault, which it returns. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  inline def everyLane(context: VMContext)(inline function: WorkgroupLane => Either[Fault, Unit])
      : Either[Fault, Unit] = {
    var failure: Either[Fault, Unit] = Completed
    context.active.foreachLane { lane =>
      if (failure.isRight) failure = function(lane)
    }
    failure
  }
}
