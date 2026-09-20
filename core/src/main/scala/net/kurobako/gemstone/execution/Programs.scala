package net.kurobako.gemstone.execution

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.memory.MemoryValueByteWidth

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import scala.annotation.nowarn

trait ExecutionMemory {

  /** Empty unless address aliases are enabled; consumers snapshot this once before execution. */
  def allocationAddresses: Vec[AllocationAddress] = Vec.empty

  def read(
      id: AllocationId,
      offset: Long :| Positive0,
      size: Int :| Positive0
  ): Either[ExecutionFailure, Vec[Byte]]
  def write(id: AllocationId, offset: Long :| Positive0, bytes: Vec[Byte]): Either[ExecutionFailure, Unit]

  /** Allocation-free override point for ISA memory operations up to 64 bits. */
  def readValue(
      id: AllocationId,
      offset: Long :| Positive0,
      size: MemoryValueByteWidth
  ): Either[ExecutionFailure, Long] =
    read(id, offset, size.nonNegative)
      .map(bytes => LittleEndian.decode(bytes, 0, size.scalarByteWidth))

  /** Allocation-free override point for ISA memory operations up to 64 bits. */
  def writeValue(
      id: AllocationId,
      offset: Long :| Positive0,
      value: Long,
      size: MemoryValueByteWidth
  ): Either[ExecutionFailure, Unit] =
    write(id, offset, LittleEndian.toVec(value, size.scalarByteWidth))

  /** Atomically replace a value and return its previous contents. The implementation owns the relevant backing-store
    * lock; a trait-local lock cannot make multiple views over shared storage atomic.
    */
  @nowarn("msg=unused explicit parameter")
  def atomicModifyValue(
      id: AllocationId,
      offset: Long :| Positive0,
      size: MemoryValueByteWidth
  )(update: Long => Long): Either[ExecutionFailure, Long] =
    Left(ExecutionFailure.Unsupported("memory does not support atomic modification"))

  /** Records a process-neutral pointer written into mirrored allocation memory. */
  def recordPointerRelocation(relocation: PointerRelocation): Unit
}

// Unsupported optional capabilities intentionally ignore every operation argument.
@nowarn("msg=unused explicit parameter")
trait ProgramInstance {
  def catalog: Either[ExecutionFailure, ProgramCatalog] =
    Left(ExecutionFailure.Unsupported("program catalog is unavailable"))

  def kernel(name: KernelName): Either[ExecutionFailure, KernelMetadata] =
    Left(ExecutionFailure.Unsupported("program kernel metadata is unavailable"))

  def global(name: GlobalName): Either[ExecutionFailure, ProgramGlobal] =
    Left(ExecutionFailure.Unsupported("program global metadata is unavailable"))

  def bindGlobal(
      name: GlobalName,
      allocation: AllocationId,
      offset: Long :| Positive0,
      allocationSize: Long :| Positive,
      memory: ExecutionMemory
  ): Either[ExecutionFailure, Unit] =
    Left(ExecutionFailure.Unsupported("program globals cannot be bound to allocations"))

  def writeGlobal(name: GlobalName, offset: Long :| Positive0, bytes: Vec[Byte]): Either[ExecutionFailure, Unit] =
    Left(ExecutionFailure.Unsupported("program globals are not writable"))

  def launch(
      dispatch: KernelDispatch,
      memory: ExecutionMemory,
      control: ExecutionControl
  ): Either[ExecutionFailure, Unit]
}

trait ProgramDecoder {
  def format: ProgramFormat
  def decode(image: Vec[Byte]): Either[ExecutionFailure, ProgramInstance]
}

// A registry is intentionally distinct from one decoded program instance: it owns IDs and lifecycle.
@nowarn("msg=unused explicit parameter")
trait ProgramExecutor {
  def catalog(id: ProgramId): Either[ExecutionFailure, ProgramCatalog] =
    Left(ExecutionFailure.Unsupported("program catalog is unavailable"))

  def kernel(id: ProgramId, name: KernelName): Either[ExecutionFailure, KernelMetadata] =
    Left(ExecutionFailure.Unsupported("program kernel metadata is unavailable"))

  def global(id: ProgramId, name: GlobalName): Either[ExecutionFailure, ProgramGlobal] =
    Left(ExecutionFailure.Unsupported("program global metadata is unavailable"))

  def bindGlobal(
      id: ProgramId,
      name: GlobalName,
      allocation: AllocationId,
      offset: Long :| Positive0,
      allocationSize: Long :| Positive,
      memory: ExecutionMemory
  ): Either[ExecutionFailure, Unit] =
    Left(ExecutionFailure.Unsupported("program globals cannot be bound to allocations"))

  def load(id: ProgramId, format: ProgramFormat, image: Vec[Byte]): Either[ExecutionFailure, Unit]
  def unload(id: ProgramId): Either[ExecutionFailure, Unit]

  def writeGlobal(
      id: ProgramId,
      name: GlobalName,
      offset: Long :| Positive0,
      bytes: Vec[Byte]
  ): Either[ExecutionFailure, Unit] =
    Left(ExecutionFailure.Unsupported("program globals are not writable"))

  def launch(
      dispatch: KernelDispatch,
      memory: ExecutionMemory,
      control: ExecutionControl
  ): Either[ExecutionFailure, Unit]
}
