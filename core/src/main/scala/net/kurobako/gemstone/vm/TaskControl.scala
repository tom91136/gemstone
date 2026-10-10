package net.kurobako.gemstone.vm

import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicLongArray
import java.util.concurrent.locks.ReentrantReadWriteLock
import net.kurobako.gemstone.concurrent.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import scala.annotation.tailrec

object TaskControl {
  object Limits {
    val Default: Limits = Limits(maxInstructions = 100000000L)
  }

  case class Limits(maxInstructions: Long > 0)

  private val Accepted: Either[Fault, Unit] = Right(())
  private val Cells                         = 64
  // One cell per cache line keeps workers that share no cell off each other's lines.
  private val Stride = 8
  private val Lease  = 1024L
}

/** One execution's irreversible cancellation, shared instruction allowance and optional observer. A rejected charge
  * consumes nothing; cancellation is permanent. Already-running instructions stop at their next checkpoint.
  *
  * Workers charge leases held in per-thread cells and refill them from the shared allowance in bounded chunks. A charge
  * the allowance cannot cover reclaims every unspent lease first, so rejection still means the whole allowance is used.
  */
class TaskControl(
    val limits: TaskControl.Limits = TaskControl.Limits.Default,
    val observer: Option[LaunchObserver] = None
) {
  import TaskControl.*

  private val cancelled = AtomicBoolean()
  private val granted   = AtomicLong()
  private val leases    = AtomicLongArray(Cells * Stride)
  // Refills share the read side; reclaiming takes the write side so no granted lease is still on its way to a cell.
  private val reclaim = ReentrantReadWriteLock()

  def cancel(): Unit       = cancelled.set(true)
  def isCancelled: Boolean = cancelled.get()

  def checkpoint(cost: Int > 0 = 1): Either[Fault, Unit] = checkpoint(cost.toLong)

  final def checkpoint(cost: Long > 0): Either[Fault, Unit] =
    if (cancelled.getOpaque) Left(Fault.Cancelled)
    else {
      val cell = (Thread.currentThread().threadId().toInt & (Cells - 1)) * Stride
      if (spend(cell, cost.raw) || refill(cell, cost.raw) || reclaimed(cell, cost.raw)) Accepted
      else Left(Fault.BudgetExceeded(limits.maxInstructions))
    }

  @tailrec
  private def spend(cell: Int, cost: Long): Boolean = {
    val lease = leases.get(cell)
    lease >= cost && (leases.compareAndSet(cell, lease, lease - cost) || spend(cell, cost))
  }

  private def refill(cell: Int, cost: Long): Boolean = reclaim.readLock().withLock {
    @tailrec def grant(): Boolean = {
      val current   = granted.get()
      val remaining = limits.maxInstructions.raw - current
      if (cost > remaining) false
      else {
        val lease = math.min(Lease, remaining - cost)
        if (!granted.compareAndSet(current, current + cost + lease)) grant()
        else {
          if (lease > 0) { val _ = leases.getAndAdd(cell, lease) }
          true
        }
      }
    }
    grant()
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def reclaimed(cell: Int, cost: Long): Boolean = reclaim.writeLock().withLock {
    var needed = cost
    var index  = 0
    while (needed > 0 && index < Cells) {
      val taken = take(index * Stride, needed)
      needed -= taken
      index += 1
    }
    needed -= unclaimed(needed)
    if (needed > 0) { val _ = leases.getAndAdd(cell, cost - needed) }
    needed == 0
  }

  // Only reclaiming changes the allowance while the write side is held.
  private def unclaimed(wanted: Long): Long = {
    val current = granted.get()
    val taken   = math.min(wanted, limits.maxInstructions.raw - current)
    if (taken > 0) granted.set(current + taken)
    math.max(taken, 0L)
  }

  @tailrec
  private def take(cell: Int, wanted: Long): Long = {
    val lease = leases.get(cell)
    val taken = math.min(lease, wanted)
    if (taken <= 0) 0L
    else if (leases.compareAndSet(cell, lease, lease - taken)) taken
    else take(cell, wanted)
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def usedInstructions: Long >= 0 = {
    var unspent = 0L
    var index   = 0
    while (index < Cells) { unspent += leases.get(index * Stride); index += 1 }
    // Concurrent reclaiming can move a lease between cells mid-sum; the total is exact once workers are idle.
    math.max(granted.get() - unspent, 0L).unsafe[Long >= 0]
  }
  def remainingInstructions: Long >= 0 = limits.maxInstructions - usedInstructions
}
