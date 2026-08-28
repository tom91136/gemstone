package net.kurobako.gemstone.execution

import net.kurobako.gemstone.Col

import scala.collection.mutable.ArrayBuffer
import scala.collection.immutable.BitSet
import scala.reflect.ClassTag

opaque type Pc = Int

object Pc {
  val Entry: Pc = 0

  def fromInt(value: Int): Either[ExecutionFault, Pc] =
    if (value >= 0) Right(value)
    else Left(ExecutionFault.InvalidProgram(s"negative program counter: $value"))

  def unsafe(value: Int): Pc =
    fromInt(value).fold(fault => throw new IllegalArgumentException(fault.message), identity)

  extension (pc: Pc) {
    def value: Int = pc

    def next: Either[ExecutionFault, Pc] =
      if (pc == Int.MaxValue) Left(ExecutionFault.InvalidProgram("program counter overflow"))
      else Right(pc + 1)
  }
}

opaque type LaneMask = BitSet

object LaneMask {
  private val MaximumLanes = 1024
  val Empty: LaneMask      = BitSet.empty

  def all(laneCount: Int): Either[ExecutionFault, LaneMask] =
    if (laneCount < 1 || laneCount > MaximumLanes)
      Left(ExecutionFault.InvalidProgram(s"lane count must be between 1 and $MaximumLanes, found $laneCount"))
    else Right(BitSet.fromSpecific(0 until laneCount))

  def one(lane: Int): Either[ExecutionFault, LaneMask] =
    if (lane < 0 || lane >= MaximumLanes)
      Left(ExecutionFault.InvalidProgram(s"lane index must be between 0 and ${MaximumLanes - 1}, found $lane"))
    else Right(BitSet(lane))

  def unsafe(value: Long): LaneMask = BitSet.fromSpecific((0 until 64).filter(index => (value & (1L << index)) != 0L))

  extension (mask: LaneMask) {
    def bits: Long                   = mask.iterator.filter(_ < 64).foldLeft(0L)((value, lane) => value | (1L << lane))
    def isEmpty: Boolean             = mask.isEmpty
    def nonEmpty: Boolean            = mask.nonEmpty
    def size: Int                    = mask.size
    def contains(lane: Int): Boolean = mask.contains(lane)
    def intersect(other: LaneMask): LaneMask = mask & other
    def union(other: LaneMask): LaneMask     = mask | other
    def subtract(other: LaneMask): LaneMask  = mask -- other

    def foreachLane(function: Int => Unit): Unit = mask.foreach(function)
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

object ExecutionLimits {
  val Default: ExecutionLimits = ExecutionLimits(maxInstructions = 100000000L, quantum = 4096)

  def create(maxInstructions: Long, quantum: Int): Either[String, ExecutionLimits] =
    if (maxInstructions <= 0L) Left(s"maxInstructions must be positive: $maxInstructions")
    else if (quantum <= 0) Left(s"quantum must be positive: $quantum")
    else Right(ExecutionLimits(maxInstructions, quantum))
}

final class ExecutionBudget private (val limit: Long) {
  private var usedValue = 0L

  def used: Long      = usedValue
  def remaining: Long = limit - usedValue

  def consume(): Either[ExecutionFault, Unit] =
    if (usedValue >= limit) Left(ExecutionFault.BudgetExceeded(limit))
    else {
      usedValue += 1L
      Right(())
    }
}

object ExecutionBudget {
  def apply(limit: Long): Either[String, ExecutionBudget] =
    if (limit <= 0L) Left(s"instruction budget must be positive: $limit")
    else Right(new ExecutionBudget(limit))
}

trait CancellationToken {
  def isCancelled: Boolean
}

object CancellationToken {
  object Never extends CancellationToken {
    override val isCancelled: Boolean = false
  }

  final class Source extends CancellationToken {
    @volatile private var cancelled = false

    override def isCancelled: Boolean = cancelled
    def cancel(): Unit                = cancelled = true
  }
}
