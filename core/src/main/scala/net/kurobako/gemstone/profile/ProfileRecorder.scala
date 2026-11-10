package net.kurobako.gemstone.profile

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.concurrent.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.flow.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchObserver.*
import scala.collection.mutable
import scala.jdk.CollectionConverters.*

object ProfileRecorder {
  import ProfileRecords.*

  /** The records one thread made. A thread runs a workgroup's issues in order, so each buffer is in issue order. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private class Buffer(val owner: Thread) {
    var records: Array[Long]                       = Array.ofDim(1024)
    var size: Int                                  = 0
    var instruction: Int                           = -1
    private var words                              = Array.ofDim[Long](16)
    private var lowest                             = Int.MaxValue
    private var highest                            = -1
    private var kernel: String                     = ""
    private var function: Option[NonEmpty[String]] = None
    private val addresses                          = mutable.LongMap.empty[Int]

    /** The instructions this thread already reported a register use for. */
    val described: mutable.BitSet = mutable.BitSet.empty

    /** The instructions this thread already reported a pipe for. */
    val resolved: mutable.BitSet = mutable.BitSet.empty

    /** The instruction index of `at`, resolved once per address of the kernel and function this thread last ran. */
    def located(at: Fault.Location, resolve: => Int): Int = at.instruction match {
      case Some(address) =>
        if (at.kernel.raw != kernel || at.function != function) {
          kernel = at.kernel.raw
          function = at.function
          addresses.clear()
        }
        addresses.getOrElseUpdate(address.raw, resolve)
      case None => resolve
    }

    private val mark: (Int >= 0) => Unit = lane => {
      val word = lane >>> 6
      if (word >= words.length) words = java.util.Arrays.copyOf(words, Integer.highestOneBit(word) * 2)
      words(word) |= 1L << (lane & 63)
      if (word < lowest) lowest = word
      if (word > highest) highest = word
    }

    private def reserve(count: Int): Unit =
      if (size + count > records.length) records = java.util.Arrays.copyOf(records, (size + count) * 2)

    def issue(state: ArchitecturalState, instruction: Int): Unit = {
      this.instruction = instruction
      state.eachLane(mark)
      val first = if (highest < 0) 0 else lowest
      val count = highest - first + 1
      reserve(5 + count)
      val unit = state.unit
      records(size) = IssueTag | (count.toLong << 4) | (first.toLong << 12) | (instruction.toLong << 32) |
        (if (state.begins) BeginsBit else 0L)
      records(size + 1) = state.issued
      records(size + 2) = (unit.workgroup.x.toLong & 0xffffffffL) | (unit.workgroup.y.toLong << 32)
      records(size + 3) = (unit.workgroup.z.toLong & 0xffffffffL) | (unit.ordinal.toLong << 32)
      records(size + 4) = state.subgroupSize.toLong
      var word = 0
      while (word < count) {
        records(size + 5 + word) = words(first + word)
        words(first + word) = 0L
        word += 1
      }
      size += 5 + count
      lowest = Int.MaxValue
      highest = -1
    }

    def access(lane: Int, space: Space, kind: AccessKind, address: Long, bytes: Int): Unit = {
      reserve(2)
      records(size) = AccessTag | (space.ordinal.toLong << 4) | (kind.ordinal.toLong << 7) | (lane.toLong << 9) |
        (bytes.toLong << 32)
      records(size + 1) = address
      size += 2
    }

    def asynchronous(lane: Int, kind: AsyncKind, target: Long, bytes: Long): Unit = {
      reserve(3)
      records(size) = AsyncTag | (kind.ordinal.toLong << 4) | (lane.toLong << 9)
      records(size + 1) = target
      records(size + 2) = bytes
      size += 3
    }

    def transition(index: Int): Unit = {
      reserve(1)
      records(size) = TransitionTag | (index.toLong << 4)
      size += 1
    }
  }

  /** Where `at` is in the program, without the workgroup and lane that executed it. */
  private def key(at: Fault.Location): Fault.Location =
    if (at.workgroup.isEmpty && at.lane.isEmpty) at else at.copy(workgroup = None, lane = None)
}

/** Records the facts of a launch for profiling: every issue with its lanes, every lane's memory access and step of
  * asynchronous work, and every change in what a unit is doing. Each thread records into its own buffer, so workgroups
  * running in parallel do not contend; [[stream]] merges them by issue ordinal.
  */
@SuppressWarnings(Array("scalafix:DisableSyntax.var"))
class ProfileRecorder extends LaunchObserver {
  import ProfileRecorder.*
  import ProfileRecords.*
  import ProfileStream.*

  private val lock                                         = ReentrantLock()
  private val buffers                                      = ConcurrentHashMap[Thread, Buffer]()
  private val instructions                                 = mutable.ArrayBuffer.empty[Instruction]
  private val located                                      = mutable.HashMap.empty[Fault.Location, Int]
  private val events                                       = mutable.ArrayBuffer.empty[MachineEvent]
  private val flows                                        = mutable.LinkedHashMap.empty[String, Graph]
  private val lost                                         = mutable.HashSet.empty[String]
  @volatile private var resources: Option[KernelResources] = None

  private var last: Option[Buffer] = None

  private def buffer: Buffer = last match {
    case Some(cached) if cached.owner eq Thread.currentThread() => cached
    case _                                                      =>
      val thread = Thread.currentThread()
      val found  = Option(buffers.get(thread)).getOrElse(buffers.computeIfAbsent(thread, Buffer(_)))
      last = Some(found)
      found
  }

  /** The index of the instruction at `where`, a location [[key]] made; the lock is held. */
  private def intern(where: Fault.Location, text: => String, schedule: => Option[Schedule]): Int =
    located.getOrElseUpdate(
      where, {
        instructions += Instruction(
          where.kernel,
          where.function,
          where.instruction,
          text,
          RegisterUse.empty,
          schedule,
          None
        )
        instructions.size - 1
      }
    )

  private def instructionAt(at: Fault.Location, state: ArchitecturalState): Int =
    lock.withLock(intern(key(at), state.text, state.schedule))

  /** Several IR instructions share a location, so the first one that reports a pipe names it. */
  private def resolvePipe(current: Buffer, instruction: Int, state: ArchitecturalState): Unit =
    state.pipe.foreach { value =>
      current.resolved += instruction
      lock.withLock {
        if (instructions(instruction).pipe.isEmpty)
          instructions(instruction) = instructions(instruction).copy(pipe = Some(value))
      }
    }

  def instruction(at: Fault.Location, state: ArchitecturalState): Either[Fault, Unit] = {
    val current     = buffer
    val instruction = current.located(at, instructionAt(at, state))
    if (!current.resolved(instruction)) resolvePipe(current, instruction, state)
    current.issue(state, instruction)
    Right(())
  }

  override def launched(resources: KernelResources): Unit = this.resources = Some(resources)

  override def controlFlow(kernel: String, graph: => ControlFlowGraph, offsets: Vec[Long]): Unit =
    lock.withLock {
      if (!flows.contains(kernel) && !lost.contains(kernel)) flows(kernel) = Graph(kernel, graph, offsets)
    }

  override def controlFlowLost(kernel: String): Unit = lock.withLock {
    lost += kernel
    flows -= kernel
    ()
  }

  /** The first non-empty use an instruction reports stands for all its issues. */
  override def registers(issued: Long, use: => RegisterUse): Unit = {
    val current     = buffer
    val instruction = current.instruction
    if (instruction >= 0 && !current.described(instruction)) {
      val value = use
      if (!value.isEmpty) {
        current.described += instruction
        lock.withLock {
          if (instructions(instruction).use.isEmpty)
            instructions(instruction) = instructions(instruction).copy(use = value)
        }
      }
    }
  }

  override def access(lane: Int >= 0, space: Space, kind: AccessKind, address: Long, bytes: Int >= 0): Unit =
    buffer.access(lane, space, kind, address, bytes)

  override def asynchronous(lane: Int >= 0, kind: AsyncKind, target: Long, bytes: Long): Unit =
    buffer.asynchronous(lane, kind, target, bytes)

  override def transition(event: MachineEvent): Unit = {
    val at    = event.at.map(key(_))
    val index = lock.withLock {
      at.foreach(intern(_, "", None))
      events += event.copy(at = at)
      events.size - 1
    }
    buffer.transition(index)
  }

  /** What the launch recorded so far, ordered by the issue each record follows. Records made before a thread's first
    * issue come first.
    */
  def stream: ProfileStream = {
    class Segment(val issued: Long, val records: Array[Long], val from: Int, val until: Int)
    val segments = mutable.ArrayBuffer.empty[Segment]
    buffers.values.asScala.foreach { buffer =>
      val records                           = buffer.records
      def push(from: Int, until: Int): Unit = segments += Segment(
        if ((records(from) & 0xfL) == IssueTag) records(from + 1) else Long.MinValue,
        records,
        from,
        until
      )
      var start = 0
      var at    = 0
      while (at < buffer.size) {
        if ((records(at) & 0xfL) == IssueTag && at > start) { push(start, at); start = at }
        at += recordLength(records(at))
      }
      if (at > start) push(start, at)
    }
    val sorted  = segments.sortBy(_.issued)
    val merged  = Array.ofDim[Long](sorted.view.map(s => s.until - s.from).sum)
    var written = 0
    sorted.foreach { segment =>
      Array.copy(segment.records, segment.from, merged, written, segment.until - segment.from)
      written += segment.until - segment.from
    }
    lock.withLock(ProfileStream(resources, Vec.from(instructions), Vec.from(events), merged, Vec.from(flows.values)))
  }
}
