package net.kurobako.gemstone.isa.spirv

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.locks.ReentrantLock
import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.concurrent.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.*

/** Cooperative interleaving: at most `parallelism` workgroups run, one external memory operation at a time. */
@SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.return"))
private class Interleaving(val parallelism: Int > 0 = 4)
    extends Workgroups {
  val calls: AtomicInteger    = java.util.concurrent.atomic.AtomicInteger(0)
  private val lock            = ReentrantLock()
  private val changed         = lock.newCondition()
  private val slot            = ThreadLocal.withInitial[Int](() => -1)
  private var live            = Array.empty[Boolean]
  private var entered         = Array.empty[Boolean]
  private var turn            = 0
  private var deadline        = 0L
  private def advance(): Unit = {
    if (live.exists(value => value)) {
      turn = (turn + 1) % live.length
      while (!live(turn)) turn = (turn + 1) % live.length
    }
    changed.signalAll()
  }
  private def checkpoint(): Unit = lock.withLock {
    val current = slot.get()
    if (current >= 0) {
      if (entered(current)) advance() else entered(current) = true
      while (turn != current && platform.monotonicNanos < deadline) {
        val remaining = deadline - platform.monotonicNanos
        if (remaining > 0L) {
          val _ = changed.awaitNanos(math.min(10_000_000L, remaining))
        }
      }
      assert(turn == current, "cross-workgroup interleaving exceeded its deadline")
    }
    ()
  }
  // Writes pass straight through: the final flush holds the interpreter's shared flush lock.
  class InterleavedMemory(delegate: AddressSpace) extends ForwardingMemory(delegate) {
    override def read(id: AllocationId, offset: Long >= 0, size: Int >= 0): Either[Fault, Vec[Byte]] = {
      checkpoint()
      super.read(id, offset, size)
    }
    override def atomicModifyValue(
        id: AllocationId,
        offset: Long >= 0,
        size: Int in 1 ~ 8
    )(update: Long => Long): Either[Fault, Long] = {
      checkpoint()
      super.atomicModifyValue(id, offset, size)(update)
    }
  }

  override def shouldParallel(tasks: Long >= 0, workItems: Long >= 0): Boolean = tasks > 1L
  // Stop the deterministic schedule at the first worker failure.
  override def parallelFor(tasks: Long >= 0)(task: Long >= 0 => Either[Fault, Unit])
      : Either[Fault, Unit] = {
    calls.incrementAndGet()
    var first = 0L
    while (first < tasks) {
      val count = math.min(parallelism.toLong, tasks.raw - first).toInt
      live = Array.fill(count)(true); entered = Array.fill(count)(false); turn = 0
      deadline = platform.monotonicNanos + 10_000_000_000L
      val failure = AtomicReference(Option.empty[Fault])
      val base    = first
      val threads = Array.tabulate(count) { index =>
        Thread(
          () => {
            slot.set(index)
            // The batching loop keeps base + index in [0, tasks).
            try task((base + index).unsafe[Long >= 0]).left.foreach(x => failure.compareAndSet(None, Some(x)))
            catch {
              case error: Throwable =>
                val detail: NonEmpty[String] =
                  s"cross-workgroup worker failed: ${Option(error.getMessage).getOrElse(error.getClass.getName)}"

                failure.set(Some(Fault.Backend(detail)))
            } finally lock.withLock { live(index) = false; if (turn == index) advance(); slot.remove() }
          },
          s"spirv-test-group-${base + index}"
        )
      }
      threads.foreach(_.setDaemon(true))
      threads.foreach(_.start()); threads.foreach(_.join(12000L))
      val timedOut = threads.exists(_.isAlive)
      if (timedOut) {
        threads.foreach(_.interrupt())
        lock.withLock(changed.signalAll())
        threads.foreach(_.join(1000L))
      }
      assert(!timedOut && threads.forall(!_.isAlive), "cross-workgroup worker did not stop")
      failure.get() match { case Some(error) => return Left(error); case None => () }
      first += count
    }
    Right(())
  }
}
