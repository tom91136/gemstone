package net.kurobako.gemstone.isa.amdgpu

import scala.compiletime.{constValue, error}

import CodeObject.Program

import net.kurobako.gemstone.NonNegativeLong
import net.kurobako.gemstone.PositiveLong
import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.binary.BitWidth
import net.kurobako.gemstone.binary.Bits
import net.kurobako.gemstone.isa.GlobalName
import net.kurobako.gemstone.isa.KernelMetadata
import net.kurobako.gemstone.isa.KernelName
import net.kurobako.gemstone.isa.ProgramSymbols
import net.kurobako.gemstone.vm.AddressSpace
import net.kurobako.gemstone.vm.Event
import net.kurobako.gemstone.vm.Fault
import net.kurobako.gemstone.vm.KernelTask
import net.kurobako.gemstone.vm.Memory
import net.kurobako.gemstone.vm.MemoryRegion
import net.kurobako.gemstone.vm.Observer
import net.kurobako.gemstone.vm.Result
import net.kurobako.gemstone.vm.Scheduler
import net.kurobako.gemstone.vm.TaskControl

final class VirtualMachine(
    override val observer: Observer[Event],
    override val scheduler: Scheduler
) extends net.kurobako.gemstone.vm.VirtualMachine[Program] {
  override type LoadedProgram = VMLoadedProgram

  override def load(program: Program): Either[Fault, LoadedProgram] =
    VMLoader.load(program)

  override def execute(
      program: LoadedProgram,
      task: KernelTask,
      memory: Memory,
      control: TaskControl
  ): Either[Fault, Result] =
    memory match {
      case addresses: AddressSpace =>
        program.execute(task, memory, addresses, control, scheduler).map(_ => Result(control.usedInstructions))
      case _ => Left(Fault.Unsupported("AMDGPU execution requires an address-space capable memory"))
    }

  def symbols(program: LoadedProgram): Either[Fault, ProgramSymbols] = program.symbols

  def kernel(
      program: LoadedProgram,
      name: KernelName
  ): Either[Fault, KernelMetadata] = program.kernel(name)

  def global(
      program: LoadedProgram,
      name: GlobalName
  ): Either[Fault, (NonNegativeLong, Vec[Byte])] = program.global(name)

  def bindGlobal(
      program: LoadedProgram,
      name: GlobalName,
      region: MemoryRegion,
      offset: NonNegativeLong,
      regionSize: PositiveLong,
      memory: Memory
  ): Either[Fault, Unit] =
    program.bindGlobal(name, region, offset, regionSize, memory)

  def writeGlobal(
      program: LoadedProgram,
      name: GlobalName,
      offset: NonNegativeLong,
      bytes: Vec[Byte]
  ): Either[Fault, Unit] = program.writeGlobal(name, offset, bytes)

  override def unload(program: LoadedProgram): Unit = ()
}

object VirtualMachine {
  private[amdgpu] opaque type LaneCount = Int
  private[amdgpu] object LaneCount {
    inline def either(value: Int): Either[String, LaneCount] =
      if (value >= 1 && value <= 64) Right(value) else Left(s"lane count must be between 1 and 64: $value")

    inline def unsafe(value: Int): LaneCount =
      either(value).fold(message => throw IllegalArgumentException(message), identity)

    inline def apply[A <: Int & Singleton](inline value: A): LaneCount =
      inline if constValue[A] >= 1 && constValue[A] <= 64 then value else error("lane count must be between 1 and 64")

    extension (lanes: LaneCount) {
      inline def raw: Int    = lanes
      def bitWidth: BitWidth = BitWidth.unsafe(lanes)
    }
  }

  private[amdgpu] opaque type LaneIndex = Int
  private[amdgpu] object LaneIndex {
    inline def either(value: Int): Either[String, LaneIndex] =
      if (value >= 0 && value <= 63) Right(value) else Left(s"lane index must be between 0 and 63: $value")

    inline def unsafe(value: Int): LaneIndex =
      either(value).fold(message => throw IllegalArgumentException(message), identity)

    inline def apply[A <: Int & Singleton](inline value: A): LaneIndex =
      inline if constValue[A] >= 0 && constValue[A] <= 63 then value else error("lane index must be between 0 and 63")

    extension (lane: LaneIndex) {
      inline def raw: Int = lane
    }
  }

  private[amdgpu] opaque type WaveSize = Int
  private[amdgpu] object WaveSize {
    inline def either(value: Int): Either[String, WaveSize] =
      if (value == 32 || value == 64) Right(value) else Left(s"wave size must be 32 or 64: $value")

    inline def unsafe(value: Int): WaveSize =
      either(value).fold(message => throw IllegalArgumentException(message), identity)

    inline def apply[A <: Int & Singleton](inline value: A): WaveSize =
      inline if constValue[A] == 32 || constValue[A] == 64 then value else error("wave size must be 32 or 64")

    extension (size: WaveSize) {
      inline def raw: Int = size
    }
  }

  /** Mutable architectural VCC/EXEC storage for one wave. */
  private[amdgpu] final class LaneBits(lanes: LaneCount, initial: Long) {
    import LaneCount.bitWidth
    private val laneLimit = Bits.mask(lanes.bitWidth)
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private var value = initial & laneLimit
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private var storage = initial & laneLimit
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    private var bitCount = java.lang.Long.bitCount(value)

    def bits: Long               = value
    def bits_=(next: Long): Unit = writeLanes(next)

    def register: Long               = storage
    def register_=(next: Long): Unit = {
      storage = next
      value = next & laneLimit
      bitCount = java.lang.Long.bitCount(value)
    }

    private def writeLanes(next: Long): Unit = {
      value = next & laneLimit
      storage = (storage & ~laneLimit) | value
      bitCount = java.lang.Long.bitCount(value)
    }

    def apply(lane: Int): Boolean = (value & (1L << lane)) != 0L

    def update(lane: Int, enabled: Boolean): Unit = {
      val mask       = 1L << lane
      val wasEnabled = (value & mask) != 0L
      if (enabled != wasEnabled) {
        if (enabled) {
          value |= mask
          storage |= mask
          bitCount += 1
        } else {
          value &= ~mask
          storage &= ~mask
          bitCount -= 1
        }
      }
    }

    def count: Int               = bitCount
    def isEmpty: Boolean         = bitCount == 0
    def nonEmpty: Boolean        = bitCount != 0
    def first: Option[LaneIndex] =
      Option.when(value != 0L)(LaneIndex.unsafe(java.lang.Long.numberOfTrailingZeros(value)))
  }

  /** Dense interpreter register banks are allocated on first access; null is the allocation-free hot-path sentinel. */
  private[amdgpu] final class VectorRegisters(lanes: LaneCount) {
    private val registers = Array.ofDim[Array[Long] | Null](256)

    @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
    def apply(index: Int): Array[Long] = registers(index) match {
      case null =>
        val allocated = Array.ofDim[Long](lanes)
        registers(index) = allocated
        allocated
      case value => value
    }
  }

  /** Per-lane scratch is allocated on first access; null is the allocation-free hot-path sentinel. */
  private[amdgpu] final class ScratchMemory(lanes: LaneCount, bytesPerLane: Int) {
    private val values = Array.ofDim[Array[Byte] | Null](lanes)

    @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
    def apply(lane: Int): Array[Byte] = values(lane) match {
      case null =>
        val allocated = Array.ofDim[Byte](bytesPerLane)
        values(lane) = allocated
        allocated
      case value => value
    }
  }

  private[amdgpu] final case class WaveState(
      scalar: Array[Long],
      vector: VectorRegisters,
      carry: LaneBits,
      active: LaneBits,
      scalarCarry: Boolean,
      scratch: ScratchMemory,
      nextInstruction: Int
  )
}
