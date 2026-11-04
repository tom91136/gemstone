package net.kurobako.gemstone.rpc

import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.LongAdder
import java.util.concurrent.locks.ReentrantLock
import net.kurobako.gemstone.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.concurrent.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.ExecutionRPC.*
import net.kurobako.gemstone.vm.*
import scala.collection.mutable

/** Host-owned bytes exchanged with the native client while a dispatch runs. Reads and writes of the mirrored bytes take
  * only the mirror's shard locks. Exchanges wait on the session state lock and its condition; host atomics also
  * serialise on their own lock and linearise in the native process.
  *
  * A dispatch keeps its workgroup-scope atomics in its own mirror only while it is the sole dispatch using atomics on
  * host memory. When a second one starts, the first publishes its mirror and both perform every atomic natively until a
  * later dispatch finds the others finished.
  */
private class HostCoherence(
    lock: ReentrantLock,
    changed: PlatformConcurrency.Condition,
    completions: CompletionId => Option[Completion],
    closed: () => Boolean,
    hostBackedAt: (GuestAddress, Int >= 0) => Boolean,
    allocationAt: GuestAddress => Option[HostMemoryRange],
    hostBackedAllocations: () => Boolean
) {
  private enum Strictness derives CanEqual {
    case Strict, Relaxed
  }

  private val syncLock = ReentrantLock()
  // A CAS guess only; native storage validates it after host writes or address reuse.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private var lastAtomic = Option.empty[HostMemoryAtomic]
  private val nextTicket = AtomicLong()
  private val engagement = ReentrantLock()
  private val engaged    = mutable.ArrayBuffer.empty[Completion]
  private val mirroring  = LongAdder()
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  @volatile private var shared = false
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  @volatile private var everything = false

  def enable(): Unit     = everything = true
  def enabled: Boolean   = everything
  def isClosed: Boolean  = closed()
  def transfers: Boolean = everything || hostBackedAllocations()

  def accessible(address: GuestAddress, size: Int >= 0): Boolean = everything || hostBackedAt(address, size)

  private def unavailable(address: GuestAddress, size: Int >= 0): Boolean =
    address.raw <= 0 || address.raw > Long.MaxValue - size || !accessible(address, size)

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def poll(id: CompletionId): Either[ExecutionFailure, HostMemoryBatch] = lock.withLock {
    var result: Option[Either[ExecutionFailure, HostMemoryBatch]] = None
    while (result.isEmpty)
      completions(id) match {
        case None             => result = Some(Left(ExecutionFailure.NotFound(s"completion $id does not exist")))
        case Some(completion) =>
          val memory = completion.hostMemory
          completion.status match {
            case CompletionStatus.Failed(error) => result = Some(Left(error))
            case CompletionStatus.Succeeded     =>
              val writes = memory.takeDirty()
              result = Some(Right(HostMemoryBatch(Vec.empty, writes, !memory.hasDirty)))
            case CompletionStatus.Pending if closed() =>
              result = Some(Left(ExecutionFailure.Failed("execution session is closed")))
            case CompletionStatus.Pending =>
              completion.hostSync match {
                case Some(exchange) if exchange.awaitingIssue =>
                  val request     = exchange.request
                  val relaxedOnly = request.relaxed && request.publish.isEmpty
                  val writes      =
                    if (relaxedOnly) Vec.empty[HostMemoryBytes]
                    else if (request.publish.nonEmpty) memory.takeDirty(request.publish)
                    else memory.takeDirty()
                  val dirty = !relaxedOnly &&
                    (if (request.publish.nonEmpty) memory.hasDirty(request.publish) else memory.hasDirty)
                  val sync = if (dirty) None else Some(exchange.issue())
                  result = Some(Right(HostMemoryBatch(Vec.empty, writes, false, sync)))
                case Some(_) => changed.await()
                case None    =>
                  val reads = memory.requests()
                  if (completion.hasReadWaiters && reads.nonEmpty)
                    result = Some(Right(HostMemoryBatch(reads, Vec.empty, false)))
                  else changed.await()
              }
          }
      }
    result.get
  }

  def supply(id: CompletionId, ranges: Vec[HostMemoryBytes]): Either[ExecutionFailure, Unit] = lock.withLock {
    completions(id) match {
      case None                                     => Left(ExecutionFailure.NotFound(s"completion $id does not exist"))
      case Some(completion) if completion.isPending =>
        completion.hostMemory.supply(ranges).left.map(ExecutionFailure.InvalidRequest(_)).map(_ => changed.signalAll())
      case Some(_) => Left(ExecutionFailure.InvalidRequest("host memory completion is no longer pending"))
    }
  }

  def supplySync(id: CompletionId, ticket: Long, observed: Long): Either[ExecutionFailure, Unit] = lock.withLock {
    completions(id).flatMap(_.hostSync) match {
      case Some(exchange) if exchange.acknowledge(ticket, observed) =>
        changed.signalAll()
        Right(())
      case _ => Left(ExecutionFailure.InvalidRequest("host synchronisation reply has no matching outstanding request"))
    }
  }

  // All host atomics linearise in the native process with CPU atomics. CAS also
  // supports arbitrary interpreter RMW functions without serialising Scala closures.
  def atomic(
      completion: Completion,
      address: GuestAddress,
      size: Int in 1 ~ 8
  )(update: Long => Long): Either[Fault, Long] =
    if ((size != 1 && size != 2 && size != 4 && size != 8) || address.raw % size != 0)
      Left(Fault.Memory("host atomics require aligned 8-, 16-, 32- or 64-bit storage"))
    else if (size < 4) subword(completion, address, size)(update)
    else exchange(completion, address, size)(update)

  // 8- and 16-bit atomics compare-exchange the aligned 32-bit word containing them, keeping the neighbouring bytes
  private def subword(completion: Completion, address: GuestAddress, size: Int in 1 ~ 8)(
      update: Long => Long
  ): Either[Fault, Long] = {
    val word  = GuestAddress(address.raw & ~3L)
    val shift = ((address.raw - word.raw) * 8).toInt
    val mask  = ((1L << (size * 8)) - 1) << shift
    if (
      allocationAt(address).exists(range =>
        word.raw < range.address.raw || word.raw + 4 > range.address.raw + range.size.raw
      )
    )
      Left(Fault.Memory("host atomic storage shares its 32-bit word with bytes outside the allocation"))
    else
      exchange(completion, word, 4)(old => (old & ~mask) | ((update((old & mask) >>> shift) << shift) & mask))
        .map(old => (old & mask) >>> shift)
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def exchange(
      completion: Completion,
      address: GuestAddress,
      size: Int in 1 ~ 8
  )(update: Long => Long): Either[Fault, Long] =
    if (unavailable(address, size.widen[Int >= 0]))
      Left(Fault.Memory("host atomic address is unavailable or overflows"))
    else {
      engage(completion)
      syncLock.withLock {
        val atomicSize: Int in (1, 2, 4, 8) = if (size == 4) 4 else 8
        val mask                            = if (size == 4) 0xffffffffL else -1L
        var expected                        =
          lastAtomic.filter(request => request.address == address && request.size == size).fold(0L)(_.desired) &
            mask
        var result: Option[Either[Fault, Long]] = None
        while (result.isEmpty) {
          val request  = HostMemoryAtomic(address, atomicSize, expected, update(expected) & mask)
          val observed = synchronise(completion, Some(request)).map(_ & mask)
          observed match {
            case Left(error)                       => result = Some(Left(error))
            case Right(value) if value == expected =>
              lastAtomic = Some(request)
              result = Some(Right(value))
            case Right(value) => expected = value
          }
        }
        result.get
      }
    }

  /** Publishes the mirror of a finishing dispatch that used atomics, so that no later dispatch's atomic can be
    * overwritten by it.
    */
  def retire(completion: Completion): Unit = if (completion.engaged) { val _ = flushWrites(completion) }

  private def engage(completion: Completion): Unit =
    if (!completion.engaged) engagement.withLock {
      if (!completion.engaged) {
        val others = lock.withLock {
          engaged.filterInPlace(_.isPending)
          engaged.toVector
        }
        shared = others.nonEmpty
        if (shared) { while (mirroring.sum() != 0L) PlatformConcurrency.yieldThread(); others.foreach(flushWrites(_)) }
        engaged += completion
        completion.engage()
      }
    }

  /** Publish `ranges` and perform `stores` natively, then record the stored values as observed. */
  def store(completion: Completion, ranges: Vec[HostMemoryRange], stores: Vec[HostMemoryAtomic]): Either[Fault, Unit] =
    syncLock.withLock {
      val since = lock.withLock(completion.hostMemory.generation)
      synchronise(completion, None, ranges, stores, Strictness.Relaxed).map { _ =>
        lock.withLock(draining(completion)(stores.foreach(atom =>
          completion.hostMemory.observeAtomic(atom.address, LittleEndian.toVec(atom.desired, atom.size), since)
        )))
      }
    }

  def fence(completion: Completion): Either[Fault, Unit] = syncLock.withLock(synchronise(completion, None).map(_ => ()))

  private def synchronise(
      completion: Completion,
      atomic: Option[HostMemoryAtomic],
      publish: Vec[HostMemoryRange] = Vec.empty,
      atomicStores: Vec[HostMemoryAtomic] = Vec.empty,
      strictness: Strictness = Strictness.Strict
  ): Either[Fault, Long] = lock.withLock {
    val relaxed  = strictness == Strictness.Relaxed
    val exchange =
      Completion.HostSync(HostMemorySync(nextTicket.incrementAndGet(), atomic, publish, atomicStores, relaxed))
    completion.beginHostSync(exchange)
    changed.signalAll()
    while (!closed() && completion.isPending && exchange.observed.isEmpty) changed.await()
    completion.endHostSync()
    if (publish.isEmpty && !relaxed) completion.hostMemory.invalidateClean()
    // A poller can consume the acknowledgement wakeup before this worker does.
    // Removing the exchange makes other workers' pending reads eligible again.
    changed.signalAll()
    if (closed()) Left(Fault.Cancelled) else exchange.observed.toRight(Fault.Cancelled)
  }

  // a store satisfies pending reads of the bytes it covers, which wakes the workers waiting on them
  private inline def draining(completion: Completion)(inline mutation: Unit): Unit = {
    val drains = completion.hostMemory.drains
    mutation
    if (completion.hostMemory.drains != drains) lock.withLock(changed.signalAll())
  }

  // with nothing dirty and no exchange in flight every earlier write is already published
  def flushWrites(completion: Completion): Either[Fault, Unit] =
    if (!completion.hostMemory.hasDirty && completion.hostSync.isEmpty) Right(())
    else syncLock.withLock {
      if (completion.hostMemory.hasDirty) synchronise(completion, None).map(_ => ()) else Right(())
    }

  def flushReads(completion: Completion): Either[Fault, Unit] = lock.withLock {
    completion.beginReadWait()
    changed.signalAll()
    while (!closed() && completion.hostMemory.hasPending) changed.await()
    completion.endReadWait()
    if (closed()) Left(Fault.Cancelled) else Right(())
  }

  def invalidate(completion: Completion): Unit = completion.hostMemory.invalidateClean()

  def invalidate(completion: Completion, ranges: Vec[HostMemoryRange]): Unit =
    completion.hostMemory.invalidateClean(ranges)

  private def unreadable(completion: Completion, address: GuestAddress, size: Int >= 0, kind: String): Option[Fault] =
    if (unavailable(address, size))
      Some(Fault.Memory(s"host memory $kind at ${address.show} ($size bytes) is unavailable or overflows"))
    else if (completion.hostMemory.isUnreadable(address, size))
      Some(Fault.Memory(s"host memory $kind at ${address.show} ($size bytes) is not readable in the client"))
    else None

  def read(completion: Completion, address: GuestAddress, size: Int >= 0): AddressSpace.ReadResult[Vec[Byte]] =
    unreadable(completion, address, size, "read") match {
      case Some(fault) => Left(fault)
      case None        => completion.hostMemory.read(address, size, allocationAt(address)).toRight(AddressSpace.Pending)
    }

  /** Demands the host bytes of `count` reads, returning whether any still has to arrive. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def stage(completion: Completion, addresses: Array[Long], sizes: Array[Int], count: Int): Either[Fault, Boolean] = {
    var fault = Option.empty[Fault]
    var at    = 0
    while (at < count && fault.isEmpty) {
      val address = GuestAddress(addresses(at))
      val size    = sizes(at).unsafe[Int >= 0]
      fault = unreadable(completion, address, size, "read")
      at += 1
    }
    fault.toLeft(()).map(_ => completion.hostMemory.stage(addresses, sizes, count, allocationAtRaw))
  }

  private val allocationAtRaw: Long => Option[HostMemoryRange] = raw => allocationAt(GuestAddress(raw))

  /** An atomic update that only this dispatch's lanes can observe until the bytes are published, for scopes that do not
    * include the host or other dispatches. It is a host atomic while another dispatch uses atomics on host memory.
    */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def modify(completion: Completion, address: GuestAddress, size: Int in (1, 2, 4, 8))(
      update: Long => Long
  ): AddressSpace.ReadResult[Long] =
    if (address.raw % size != 0) Left(Fault.Memory("atomics require aligned storage"))
    else unreadable(completion, address, size.widen[Int >= 0], "atomic") match {
      case Some(fault) => Left(fault)
      case None        =>
        engage(completion)
        mirroring.increment()
        if (shared) {
          mirroring.decrement()
          atomic(completion, address, size.widen[Int in 1 ~ 8])(update)
        } else {
          var previous = Option.empty[Long]
          try
            draining(completion) {
              previous = completion.hostMemory.modify(address, size, allocationAt(address))(update)
            }
          finally mirroring.decrement()
          previous.toRight(AddressSpace.Pending)
        }
    }

  def write(completion: Completion, address: GuestAddress, bytes: Vec[Byte]): Either[Fault, Unit] =
    if (unavailable(address, bytes.nonNegativeSize))
      Left(Fault.Memory(s"host memory write at ${address.show} (${bytes.size} bytes) is unavailable or overflows"))
    else {
      val atomicWidth: Int in (0, 1, 2, 4, 8) = bytes.size.refine[Int in (1, 2, 4, 8)].toOption
        .filter(width => address.raw % width == 0).fold[Int in (0, 1, 2, 4, 8)](0)(identity)
      draining(completion)(completion.hostMemory.write(address, bytes, atomicWidth))
      Right(())
    }
}
