package net.kurobako.gemstone.rpc

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.graphics.GraphicsProtocol.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.ExecutionRPC.*
import net.kurobako.gemstone.vm.*
import scala.collection.mutable
import scala.jdk.CollectionConverters.*

private object Completion {
  type Execute = TaskContext => Either[Fault, Option[GraphicsStatistics]]

  private enum HostSyncState derives CanEqual {
    case AwaitingIssue
    case AwaitingReply
    case Acknowledged(value: Long)
  }

  /** One host synchronisation request, issued to the client once and acknowledged by ticket. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  class HostSync(val request: HostMemorySync) {
    private var state: HostSyncState = HostSyncState.AwaitingIssue
    def awaitingIssue: Boolean       = state == HostSyncState.AwaitingIssue
    def observed: Option[Long]       = state match {
      case HostSyncState.Acknowledged(value) => Some(value)
      case _                                 => None
    }
    def issue(): HostMemorySync = {
      require(awaitingIssue, "host synchronisation request was already issued")
      state = HostSyncState.AwaitingReply
      request
    }
    def acknowledge(ticket: Long, value: Long): Boolean =
      if (state != HostSyncState.AwaitingReply || request.ticket != ticket) false
      else { state = HostSyncState.Acknowledged(value); true }
  }

  private val DirtyChunkBytes: Int > 0 = 65536
  val DirtyGranuleBytes: Int > 0       = 4096

  private def tracking(entry: AllocationAddressIndex.Entry): WrittenExtent =
    if (entry.allocation.hostBacked) WrittenRanges() else WrittenGranules(entry.size, DirtyGranuleBytes)

  /** One worker's written extents, exact for host-visible allocations (a republished byte the kernel did not write
    * could overwrite a newer host value) and one bit per granule for device-only ones. Consecutive writes usually
    * target the same allocation.
    */
  private class DirtyWrites {
    val extents: mutable.HashMap[AllocationId, WrittenExtent] = mutable.HashMap.empty
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private var last: Option[(AllocationId, WrittenExtent)] = None

    def add(entry: AllocationAddressIndex.Entry, offset: Long >= 0, size: Int >= 0): Unit = last match {
      case Some((lastId, target)) if lastId == entry.id => target.add(offset, size)
      case _                                            =>
        val selected = extents.getOrElseUpdate(entry.id, tracking(entry))
        last = Some(entry.id -> selected)
        selected.add(offset, size)
    }
  }

  private def page[A](values: Vec[A], offset: Int >= 0, limit: Int > 0): Vec[A] =
    values.slice(offset, offset.raw + math.min(limit, values.length - offset))
}

/** One dispatch with its recorded side effects and host exchanges, guarded by the session state lock. */
@SuppressWarnings(Array("scalafix:DisableSyntax.var"))
private class Completion(
    val id: CompletionId,
    val program: Option[ProgramId],
    val execute: Completion.Execute,
    val allocations: Set[AllocationId],
    val control: TaskControl,
    val device: Int,
    maxPrintEvents: Int > 0
) {
  private var currentGraphicsStatistics: Option[GraphicsStatistics] = None
  private var currentStatus: CompletionStatus                       = CompletionStatus.Pending
  private val relocations                                           =
    mutable.LinkedHashMap.empty[(KernelTask.RelocationSource, Long >= 0), KernelTask.PointerRelocation]
  private val output = mutable.ArrayBuffer.empty[Event.Print]
  // Read only after successful completion, like the dirty-range snapshot.
  private lazy val allPointerRelocations = Vec.from(relocations.values)

  def graphicsStatistics: Option[GraphicsStatistics] = currentGraphicsStatistics
  def status: CompletionStatus                       = currentStatus
  def isPending: Boolean                             = currentStatus == CompletionStatus.Pending
  def prints: Vec[Event.Print]                       = Vec.from(output)

  def pointerRelocations(offset: Int >= 0, limit: Int > 0): Vec[KernelTask.PointerRelocation] =
    Completion.page(allPointerRelocations, offset, limit)

  def finish(result: CompletionStatus, statistics: Option[GraphicsStatistics] = None): Unit = {
    require(result != CompletionStatus.Pending, "completion cannot finish as pending")
    if (isPending) { currentGraphicsStatistics = statistics; currentStatus = result }
  }

  def record(relocation: KernelTask.PointerRelocation): Unit = {
    val key = (relocation.source, relocation.sourceOffset)
    // Replacing a pointer keeps the order of its final store.
    val _ = relocations.remove(key)
    relocations.update(key, relocation)
  }

  def record(event: Event.Print): Either[ExecutionFailure, Unit] =
    if (output.size >= maxPrintEvents) Left(ExecutionFailure.Failed("device printf event limit exceeded"))
    else { output += event; Right(()) }

  val hostMemory: HostMemoryMirror = HostMemoryMirror()
  // Each worker owns its intervals; merge only after all workgroups complete.
  private val dirtyWrites = java.util.concurrent.ConcurrentHashMap[Thread, Completion.DirtyWrites]()
  @volatile private var exchange: Option[Completion.HostSync] = None
  @volatile private var engagedHost                           = false
  private var readWaiters                                     = 0
  private var issuedReads                                     = 0L
  private var answeredReads                                   = 0L
  @volatile private var unappliedWrites                       = false
  private var due                                             = false
  def hostSync: Option[Completion.HostSync]                   = exchange
  def engaged: Boolean                                        = engagedHost
  def engage(): Unit                                          = engagedHost = true
  def hasReadWaiters: Boolean                                 = readWaiters > 0
  def beginHostSync(value: Completion.HostSync): Unit         = {
    require(exchange.isEmpty, "host synchronisation is already pending")
    exchange = Some(value)
  }
  def endHostSync(): Unit = exchange = None

  def beginReadWait(): Long = {
    readWaiters += 1
    issuedReads
  }
  def issueReads(): Unit                       = issuedReads += 1
  def answerReads(): Unit                      = answeredReads = issuedReads
  def answeredReadsAfter(batch: Long): Boolean = answeredReads > batch

  def publishing: Boolean                    = unappliedWrites
  def beginPublication(): Unit               = unappliedWrites = true
  def endPublication(): Unit                 = unappliedWrites = false
  def publicationDue: Boolean                = due
  def publicationDue_=(value: Boolean): Unit = due = value
  def endReadWait(): Unit                    = {
    require(readWaiters > 0, "no host read waiter to release")
    readWaiters -= 1
  }

  def markDirty(entry: AllocationAddressIndex.Entry, offset: Long >= 0, size: Int >= 0): Unit =
    workerWrites().add(entry, offset, size)

  def dirtyBytesRetained: Long =
    dirtyWrites.values().asScala.iterator.flatMap(_.extents.values).map(_.retainedBytes).sum

  // A plain lookup first: computeIfAbsent locks the bin whenever the key is not its first node.
  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  private def workerWrites(): Completion.DirtyWrites = {
    val thread   = Thread.currentThread()
    val existing = dirtyWrites.get(thread)
    if (existing != null) existing else dirtyWrites.computeIfAbsent(thread, _ => Completion.DirtyWrites())
  }

  private lazy val allDirtyRanges: Vec[DirtyRange] = {
    val merged = mutable.HashMap.empty[AllocationId, WrittenExtent]
    dirtyWrites.values().forEach { writes =>
      writes.extents.foreach { (id, ranges) =>
        val target = merged.getOrElseUpdate(id, ranges.fresh)
        ranges.chunks(Completion.DirtyChunkBytes).foreach((offset, size) => target.add(offset, size))
      }
    }
    Vec.from(merged.toArray.sortBy(_._1.raw).view.flatMap { (allocationId, written) =>
      written.chunks(Completion.DirtyChunkBytes).map((offset, size) => DirtyRange(allocationId, offset, size))
    })
  }

  def dirtyRanges(offset: Int >= 0, limit: Int > 0): Vec[DirtyRange] = Completion.page(allDirtyRanges, offset, limit)
}
