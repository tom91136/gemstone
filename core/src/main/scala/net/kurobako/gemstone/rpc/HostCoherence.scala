package net.kurobako.gemstone.rpc

import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.concurrent.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.ExecutionRPC.*
import net.kurobako.gemstone.vm.*

/** Host-owned bytes exchanged with the native client while a dispatch runs. Exchanges wait on the session state lock
  * and its condition; host atomics also serialise on their own lock and linearise in the native process.
  */
private class HostCoherence(
    lock: ReentrantLock,
    changed: Condition,
    completions: CompletionId => Option[Completion],
    closed: () => Boolean,
    hostBackedAt: (GuestAddress, Int >= 0) => Boolean,
    hostBackedAllocations: () => Boolean
) {
  private val syncLock = ReentrantLock()
  // A CAS guess only; native storage validates it after host writes or address reuse.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private var lastAtomic = Option.empty[HostMemoryAtomic]
  private val nextTicket = AtomicLong()
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  @volatile private var everything = false

  def enable(): Unit     = everything = true
  def enabled: Boolean   = everything
  def transfers: Boolean = everything || hostBackedAllocations()

  def accessible(address: GuestAddress, size: Int >= 0): Boolean = everything || hostBackedAt(address, size)

  private def available(address: GuestAddress, size: Int >= 0): Boolean =
    address.raw > 0 && address.raw <= Long.MaxValue - size.raw && accessible(address, size)

  private def record(id: CompletionId): Completion =
    completions(id).getOrElse(throw java.util.NoSuchElementException(s"completion $id does not exist"))

  private def mirror(id: CompletionId): HostMemoryMirror = record(id).hostMemory

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
                  val request        = exchange.request
                  val (writes, sync) =
                    if (request.relaxed && request.publish.isEmpty) (Vec.empty[HostMemoryBytes], Some(exchange.issue()))
                    else if (request.publish.nonEmpty) {
                      val selected = memory.takeDirty(request.publish)
                      (selected, if (memory.hasDirty(request.publish)) None else Some(exchange.issue()))
                    } else {
                      val pending = memory.takeDirty()
                      (pending, if (memory.hasDirty) None else Some(exchange.issue()))
                    }
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
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def atomic(id: CompletionId, address: GuestAddress, size: Int in 1 ~ 8)(update: Long => Long): Either[Fault, Long] =
    if ((size.raw != 4 && size.raw != 8) || address.raw % size.raw != 0)
      Left(Fault.Memory("host atomics require aligned 32- or 64-bit storage"))
    else if (!available(address, size.widen[Int >= 0]))
      Left(Fault.Memory("host atomic address is unavailable or overflows"))
    else syncLock.withLock {
      val atomicSize: Int in (1, 2, 4, 8) = if (size.raw == 4) 4 else 8
      val mask                            = if (size.raw == 4) 0xffffffffL else -1L
      var expected                        =
        lastAtomic.filter(request => request.address == address && request.size.raw == size.raw).fold(0L)(_.desired) &
          mask
      var result: Option[Either[Fault, Long]] = None
      while (result.isEmpty) {
        val request  = HostMemoryAtomic(address, atomicSize, expected, update(expected) & mask)
        val observed = synchronise(id, Some(request)).map(_ & mask)
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

  def atomicLoad(id: CompletionId, address: GuestAddress, size: Int in 1 ~ 8): Either[Fault, Long] =
    size.raw.refine[Int in (1, 2, 4, 8)].toOption.filter(width => address.raw % width.raw == 0)
      .toRight(Fault.Memory("host atomic loads require naturally aligned 8-, 16-, 32-, or 64-bit storage"))
      .flatMap { atomicSize =>
        if (!available(address, size.widen[Int >= 0]))
          Left(Fault.Memory("host atomic address is unavailable or overflows"))
        else syncLock.withLock {
          val request = HostMemoryAtomic(address, atomicSize, 0L, 0L, HostMemoryAtomicOperation.Load)
          synchronise(id, Some(request), Vec(HostMemoryRange(address, size)), relaxed = true).map { observed =>
            val value = observed & (if (size.raw == 8) -1L else (1L << (size.raw * 8)) - 1L)
            lock.withLock(mirror(id).observeAtomic(address, LittleEndian.toVec(value, size.widen[Int in 0 ~ 8])))
            value
          }
        }
      }

  /** Publish `ranges` and perform `stores` natively, then record the stored values as observed. */
  def store(id: CompletionId, ranges: Vec[HostMemoryRange], stores: Vec[HostMemoryAtomic]): Either[Fault, Unit] =
    syncLock.withLock {
      synchronise(id, None, ranges, stores, relaxed = true).map { _ =>
        lock.withLock(stores.foreach(atom =>
          mirror(id).observeAtomic(atom.address, LittleEndian.toVec(atom.desired, atom.size))
        ))
      }
    }

  def fence(id: CompletionId): Either[Fault, Unit] = syncLock.withLock(synchronise(id, None).map(_ => ()))

  private def synchronise(
      id: CompletionId,
      atomic: Option[HostMemoryAtomic],
      publish: Vec[HostMemoryRange] = Vec.empty,
      atomicStores: Vec[HostMemoryAtomic] = Vec.empty,
      relaxed: Boolean = false
  ): Either[Fault, Long] = lock.withLock {
    val exchange =
      Completion.HostSync(HostMemorySync(nextTicket.incrementAndGet(), atomic, publish, atomicStores, relaxed))
    record(id).beginHostSync(exchange)
    changed.signalAll()
    while (!closed() && exchange.observed.isEmpty) changed.await()
    record(id).endHostSync()
    if (publish.isEmpty && !relaxed) mirror(id).invalidateClean()
    // A poller can consume the acknowledgement wakeup before this worker does.
    // Removing the exchange makes other workers' pending reads eligible again.
    changed.signalAll()
    if (closed()) Left(Fault.Cancelled) else exchange.observed.toRight(Fault.Cancelled)
  }

  def flushWrites(id: CompletionId): Either[Fault, Unit] = syncLock.withLock {
    if (lock.withLock(mirror(id).hasDirty)) synchronise(id, None).map(_ => ()) else Right(())
  }

  def flushReads(id: CompletionId): Either[Fault, Unit] = lock.withLock {
    record(id).beginReadWait()
    changed.signalAll()
    while (!closed() && mirror(id).hasPending) changed.await()
    record(id).endReadWait()
    if (closed()) Left(Fault.Cancelled) else Right(())
  }

  def invalidate(id: CompletionId): Unit = lock.withLock(mirror(id).invalidateClean())

  def read(id: CompletionId, address: GuestAddress, size: Int >= 0): AddressSpace.ReadResult[Vec[Byte]] =
    lock.withLock {
      if (!available(address, size))
        Left(Fault.Memory(s"host memory read at ${address.show} (${size.raw} bytes) is unavailable or overflows"))
      else mirror(id).read(address, size).toRight(AddressSpace.Pending)
    }

  def write(id: CompletionId, address: GuestAddress, bytes: Vec[Byte]): Either[Fault, Unit] = lock.withLock {
    if (!available(address, bytes.nonNegativeSize))
      Left(Fault.Memory(s"host memory write at ${address.show} (${bytes.size} bytes) is unavailable or overflows"))
    else {
      val atomicWidth: Int in (0, 1, 2, 4, 8) = bytes.size.refine[Int in (1, 2, 4, 8)].toOption
        .filter(width => address.raw % width.raw == 0).fold[Int in (0, 1, 2, 4, 8)](0)(identity)
      mirror(id).write(address, bytes, atomicWidth)
      Right(())
    }
  }
}
