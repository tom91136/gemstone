package net.kurobako.gemstone.execution

import net.kurobako.gemstone.Col

import scala.collection.mutable.ArrayBuffer // scalafix:ok GemstoneRuntimeCollections.mutable-collection; hot write buffer
import scala.reflect.ClassTag

opaque type Pc = Int

object Pc {
  val Entry: Pc = 0

  def fromInt(value: Int): Either[ExecutionFault, Pc] =
    if (value >= 0) Right(value)
    else Left(ExecutionFault.InvalidProgram(s"negative program counter: $value"))

  def unsafe(value: Int): Pc =
    fromInt(value).fold(fault => throw IllegalArgumentException(fault.message), identity)

  extension (pc: Pc) {
    def value: Int = pc

    def next: Either[ExecutionFault, Pc] =
      if (pc == Int.MaxValue) Left(ExecutionFault.InvalidProgram("program counter overflow"))
      else Right(pc + 1)
  }
}

final class LaneMaskData(val words: Array[Long], val count: Int, val limit: Int) {
  override def equals(other: Any): Boolean = other match {
    case that: LaneMaskData => count == that.count && java.util.Arrays.equals(words, that.words)
    case _                  => false
  }

  override def hashCode(): Int = 31 * count + java.util.Arrays.hashCode(words)
}

opaque type LaneMask = LaneMaskData

@SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:GemstoneNoExplicitNew.explicit-new"))
object LaneMask {
  val MaximumLanes    = 1024
  val WordCount       = MaximumLanes / java.lang.Long.SIZE
  val Empty: LaneMask = new LaneMaskData(Array.fill(WordCount)(0L), 0, 0)

  def all(laneCount: Int): Either[ExecutionFault, LaneMask] =
    if (laneCount < 1 || laneCount > MaximumLanes)
      Left(ExecutionFault.InvalidProgram(s"lane count must be between 1 and $MaximumLanes, found $laneCount"))
    else {
      val words = Array.fill(WordCount)(0L)
      var lane  = 0
      while (lane < laneCount) {
        words(lane >>> 6) |= 1L << (lane & 63)
        lane += 1
      }
      Right(new LaneMaskData(words, laneCount, (laneCount + 63) >>> 6))
    }

  def one(lane: Int): Either[ExecutionFault, LaneMask] =
    if (lane < 0 || lane >= MaximumLanes)
      Left(ExecutionFault.InvalidProgram(s"lane index must be between 0 and ${MaximumLanes - 1}, found $lane"))
    else {
      val words = Array.fill(WordCount)(0L)
      words(lane >>> 6) = 1L << (lane & 63)
      Right(new LaneMaskData(words, 1, (lane >>> 6) + 1))
    }

  def unsafe(value: Long): LaneMask = {
    val words = Array.fill(WordCount)(0L)
    words(0) = value
    new LaneMaskData(words, java.lang.Long.bitCount(value), if (value == 0L) 0 else 1)
  }

  extension (mask: LaneMask) {
    def isEmpty: Boolean             = mask.count == 0
    def nonEmpty: Boolean            = mask.count != 0
    def size: Int                    = mask.count
    def contains(lane: Int): Boolean =
      lane >= 0 && lane < MaximumLanes && (mask.words(lane >>> 6) & (1L << (lane & 63))) != 0L

    def warpBits(lane: Int): Long =
      if (lane < 0 || lane >= MaximumLanes) 0L
      else (mask.words(lane >>> 6) >>> (lane & 32)) & 0xffffffffL

    def intersect(other: LaneMask): LaneMask =
      if (mask.count == 0 || other.count == 0) Empty
      else combine(other, (left, right) => left & right)

    def union(other: LaneMask): LaneMask =
      if (mask.count == 0) other
      else if (other.count == 0) mask
      else combine(other, (left, right) => left | right)

    def subtract(other: LaneMask): LaneMask =
      if (mask.count == 0 || other.count == 0) mask
      else combine(other, (left, right) => left & ~right)

    inline def filterLanes(inline predicate: Int => Boolean): LaneMask =
      if (mask.count == 0) Empty
      else {
        val words = Array.fill(WordCount)(0L)
        var count = 0
        foreachLane { lane =>
          if (predicate(lane)) {
            words(lane >>> 6) |= 1L << (lane & 63)
            count += 1
          }
        }
        if (count == 0) Empty
        else {
          var limit = WordCount
          while (words(limit - 1) == 0L) limit -= 1
          new LaneMaskData(words, count, limit)
        }
      }

    inline def foreachLane(inline function: Int => Unit): Unit = {
      var wordIndex = 0
      while (wordIndex < mask.limit) {
        var word = mask.words(wordIndex)
        while (word != 0L) {
          val bit = java.lang.Long.numberOfTrailingZeros(word)
          function((wordIndex << 6) + bit)
          word &= word - 1L
        }
        wordIndex += 1
      }
    }

    private def combine(other: LaneMask, operation: (Long, Long) => Long): LaneMask = {
      val words      = Array.fill(WordCount)(0L)
      var count      = 0
      var limit      = 0
      var index      = 0
      val inputLimit = Math.max(mask.limit, other.limit)
      while (index < inputLimit) {
        val word = operation(mask.words(index), other.words(index))
        words(index) = word
        count += java.lang.Long.bitCount(word)
        if (word != 0L) limit = index + 1
        index += 1
      }
      if (count == 0) Empty else new LaneMaskData(words, count, limit)
    }
  }
}

/** Immutable code shared by all lanes or waves. Architectural state owns the program counter. */
final case class ExecutableProgram[Instruction](instructions: Col[Instruction]) {
  def size: Int = instructions.size

  def at(pc: Pc): Either[ExecutionFault, Instruction] =
    instructions.lift(pc.value).toRight(
      ExecutionFault.InvalidProgram(
        s"program counter ${pc.value} is outside a ${instructions.size}-instruction program"
      )
    )
}

enum WaitScope {
  case Warp
  case Workgroup
  case Device
  case System
  case External
}

final case class WaitKey(scope: WaitScope, id: Long, phase: Long)

enum ExecutionFault {
  case Unsupported(opcode: String, detail: String)
  case InvalidProgram(detail: String)
  case InvalidState(detail: String)
  case UndefinedRegister(name: String, pc: Pc, lane: Int)
  case Memory(detail: String)
  case ArchitecturalTrap(isa: String, code: Long, pc: Pc)
  case BudgetExceeded(limit: Long)
  case Cancelled
  case Deadlock(detail: String)

  def message: String = this match {
    case Unsupported(opcode, detail)       => s"unsupported instruction $opcode: $detail"
    case InvalidProgram(detail)            => s"invalid program: $detail"
    case InvalidState(detail)              => s"invalid execution state: $detail"
    case UndefinedRegister(name, pc, lane) => s"lane $lane: undefined register $name at pc ${pc.value}"
    case Memory(detail)                    => detail
    case ArchitecturalTrap(isa, code, pc)  => s"$isa trap $code at pc ${pc.value}"
    case BudgetExceeded(limit)             => s"instruction budget of $limit was exhausted"
    case Cancelled                         => "execution was cancelled"
    case Deadlock(detail)                  => s"execution deadlocked: $detail"
  }
}

/** An instruction's control effect. Register and memory writes are staged separately. */
enum Step {
  case Advance
  case Jump(target: Pc)
  case Split(taken: LaneMask, target: Pc, fallthrough: LaneMask)
  case Suspend(key: WaitKey)
  case Exit(mask: LaneMask)
  case Trap(fault: ExecutionFault)
}

/** Marker implemented by ISA-specific architectural writes. */
trait Write extends Product with Serializable

/** Reusable two-phase write buffer. Every write is validated before any write is applied. */
@SuppressWarnings(
  Array(
    "scalafix:DisableSyntax.var",
    "scalafix:DisableSyntax.return",
    "scalafix:GemstoneRuntimeCollections.mutable-collection"
  )
)
final class Effects[W <: Write: ClassTag] {
  private val pending = ArrayBuffer.empty[W]

  def stage(write: W): Unit = pending += write
  def isEmpty: Boolean      = pending.isEmpty
  def size: Int             = pending.size
  def snapshot: Col[W]      = Col.from(pending)
  def clear(): Unit         = pending.clear()

  def commit(
      validate: W => Either[ExecutionFault, Unit],
      applyWrite: W => Unit
  ): Either[ExecutionFault, Unit] = {
    var index = 0
    while (index < pending.size) {
      validate(pending(index)) match {
        case Left(fault) =>
          pending.clear()
          return Left(fault)
        case Right(()) => ()
      }
      index += 1
    }
    index = 0
    while (index < pending.size) {
      applyWrite(pending(index))
      index += 1
    }
    pending.clear()
    Right(())
  }
}

final case class ExecutionLimits(maxInstructions: Long, quantum: Int)

/** Bounded work-item/work-group execution supplied by the hosting runtime.
  *
  * ISA interpreters keep a sequential implementation as their default so direct JVM/Native tests remain deterministic.
  * The server may provide a shared bounded implementation; the task count is deliberately independent of the pool size,
  * so a 1024-group launch never creates 1024 workers.
  */
trait ExecutionParallelism {
  def parallelism: Int
  def shouldParallel(taskCount: Long, workItemsPerTask: Long): Boolean
  def parallelFor(taskCount: Long)(task: Long => Either[ExecutionFailure, Unit]): Either[ExecutionFailure, Unit]
}

object ExecutionParallelism {
  @SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.return"))
  private object SequentialImplementation extends ExecutionParallelism {
    override val parallelism: Int = 1

    override def shouldParallel(taskCount: Long, workItemsPerTask: Long): Boolean = false

    override def parallelFor(
        taskCount: Long
    )(task: Long => Either[ExecutionFailure, Unit]): Either[ExecutionFailure, Unit] = {
      require(taskCount >= 0L, s"parallel task count must be non-negative: $taskCount")
      var index = 0L
      while (index < taskCount) {
        task(index) match {
          case Left(error) => return Left(error)
          case Right(())   => ()
        }
        index += 1L
      }
      Right(())
    }
  }

  val Sequential: ExecutionParallelism = SequentialImplementation
}

object ExecutionLimits {

  /** The default guard remains deliberately finite and applies when no profile is selected. */
  val Default: ExecutionLimits = ExecutionLimits(maxInstructions = 100000000L, quantum = 4096)

  /** Environment variables are read by the JVM server before a child process is launched. They are intentionally not
    * part of the wire protocol: a workload cannot raise its own execution limit after it has connected to an
    * already-running server.
    */
  val MaxInstructionsEnvironment = "GEMSTONE_EXECUTION_MAX_INSTRUCTIONS"
  val QuantumEnvironment         = "GEMSTONE_EXECUTION_QUANTUM"

  /** Read an opt-in execution profile from an environment map.
    *
    * An absent variable keeps the corresponding default. Invalid values fail closed rather than silently changing the
    * safety boundary. This method is kept in core so every JVM entry point can apply exactly the same validation.
    */
  def fromEnvironment(environment: Map[String, String]): Either[String, ExecutionLimits] = {
    def positiveLong(name: String, value: String): Either[String, Long] =
      value.toLongOption match {
        case Some(parsed) if parsed > 0L => Right(parsed)
        case Some(_)                     => Left(s"$name must be positive: $value")
        case None                        => Left(s"$name must be a positive integer: $value")
      }

    def positiveInt(name: String, value: String): Either[String, Int] =
      value.toIntOption match {
        case Some(parsed) if parsed > 0 => Right(parsed)
        case Some(_)                    => Left(s"$name must be positive: $value")
        case None                       => Left(s"$name must be a positive integer: $value")
      }

    for {
      maxInstructions <-
        environment.get(MaxInstructionsEnvironment).fold[Either[String, Long]](Right(Default.maxInstructions))(
          positiveLong(MaxInstructionsEnvironment, _)
        )
      quantum <- environment.get(QuantumEnvironment).fold[Either[String, Int]](Right(Default.quantum))(
        positiveInt(QuantumEnvironment, _)
      )
    } yield ExecutionLimits(maxInstructions, quantum)
  }

  def create(maxInstructions: Long, quantum: Int): Either[String, ExecutionLimits] =
    if (maxInstructions <= 0L) Left(s"maxInstructions must be positive: $maxInstructions")
    else if (quantum <= 0) Left(s"quantum must be positive: $quantum")
    else Right(ExecutionLimits(maxInstructions, quantum))
}

@SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.return"))
final class ExecutionBudget private (val limit: Long, threadSafe: Boolean) {
  private var sequentialUsed                         = 0L
  private val usedValue                              = java.util.concurrent.atomic.AtomicLong(0L)
  private val consumed: Either[ExecutionFault, Unit] = Right(())

  def used: Long      = if (threadSafe) math.min(limit, usedValue.get()) else sequentialUsed
  def remaining: Long = limit - used

  def consume(cost: Int = 1): Either[ExecutionFault, Unit] =
    if (cost <= 0) Left(ExecutionFault.InvalidState(s"instruction cost must be positive: $cost"))
    else {
      val increment = cost.toLong
      if (!threadSafe) {
        if (increment > limit - sequentialUsed) Left(ExecutionFault.BudgetExceeded(limit))
        else {
          sequentialUsed += increment
          consumed
        }
      } else if (cost == 1 && limit <= Int.MaxValue.toLong) {
        // One atomic ticket per instruction avoids retry storms between workgroups.
        // Racing tickets beyond the limit fail; they never execute an instruction.
        // Keep ample counter headroom for those tickets, and use CAS for larger limits.
        if (usedValue.get() >= limit || usedValue.getAndIncrement() >= limit)
          Left(ExecutionFault.BudgetExceeded(limit))
        else consumed
      } else {
        var current  = usedValue.get()
        var complete = false
        while (!complete) {
          if (increment > limit - current) return Left(ExecutionFault.BudgetExceeded(limit))
          complete = usedValue.compareAndSet(current, current + increment)
          if (!complete) current = usedValue.get()
        }
        consumed
      }
    }
}

object ExecutionBudget {
  def fromLong(limit: Long, threadSafe: Boolean = false): Either[String, ExecutionBudget] =
    if (limit <= 0L) Left(s"instruction budget must be positive: $limit")
    else Right(ExecutionBudget(limit, threadSafe))
}

trait CancellationToken {
  def isCancelled: Boolean
}

object CancellationToken {
  object Never extends CancellationToken {
    override val isCancelled: Boolean = false
  }

  final class Source extends CancellationToken {
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    @volatile private var cancelled = false

    override def isCancelled: Boolean = cancelled
    def cancel(): Unit                = cancelled = true
  }
}

/** Per-dispatch progress control shared by every ISA executor. A control instance must belong to exactly one launch so
  * its budget cannot leak across independent submissions.
  */
final class ExecutionControl private (
    val limits: ExecutionLimits,
    val cancellation: CancellationToken,
    private val budget: ExecutionBudget,
    val parallelism: ExecutionParallelism
) {
  def checkpoint(cost: Int = 1): Either[ExecutionFault, Unit] =
    if (cancellation.isCancelled) Left(ExecutionFault.Cancelled)
    else budget.consume(cost)

  def usedInstructions: Long = budget.used

  /** Remaining budget before any ISA executor-local, unflushed quantum. */
  private[gemstone] def remainingInstructions: Long = budget.remaining

  private[gemstone] def cancel(): Unit = cancellation match {
    case source: CancellationToken.Source => source.cancel()
    case _                                => ()
  }
}

object ExecutionControl {
  def create(
      limits: ExecutionLimits = ExecutionLimits.Default,
      cancellation: CancellationToken = CancellationToken.Never,
      parallelism: ExecutionParallelism = ExecutionParallelism.Sequential
  ): ExecutionControl =
    ExecutionControl(
      limits,
      cancellation,
      ExecutionBudget.fromLong(limits.maxInstructions, parallelism.parallelism > 1).fold(
        message => throw IllegalArgumentException(message),
        identity
      ),
      parallelism
    )

  def default: ExecutionControl = create()
}
