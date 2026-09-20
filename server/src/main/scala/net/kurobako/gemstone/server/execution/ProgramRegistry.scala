package net.kurobako.gemstone.server.execution

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.concurrent.withLock
import net.kurobako.gemstone.execution.*

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.locks.ReentrantLock

/** Stable decoder registry and prepared-program store. ISA parsing, marshalling, and execution belong to the individual
  * decoders so parallel ISA work does not converge on this file.
  */
private[server] final class ProgramRegistry private (
    decoders: Map[ProgramFormat, ProgramDecoder],
    catalogProjection: ProgramCatalog => ProgramCatalog
) extends ProgramExecutor {
  private val programs      = AtomicReference(Map.empty[ProgramId, ProgramInstance])
  private val lifecycleLock = ReentrantLock()

  override def load(id: ProgramId, format: ProgramFormat, image: Vec[Byte]): Either[ExecutionFailure, Unit] =
    lifecycleLock.withLock {
      if (programs.get().contains(id))
        Left(ExecutionFailure.InvalidRequest(s"program ${id.value} is already loaded"))
      else decoders.get(format) match {
        case None =>
          Left(ExecutionFailure.Unsupported(s"no program decoder owns format $format"))
        case Some(decoder) =>
          decoder.decode(image) match {
            case Right(prepared) =>
              programs.set(programs.get().updated(id, prepared))
              Right(())
            case Left(error) => Left(error)
          }
      }
    }

  override def unload(id: ProgramId): Either[ExecutionFailure, Unit] = lifecycleLock.withLock {
    val current = programs.get()
    if (current.contains(id)) {
      programs.set(current.removed(id))
      Right(())
    } else
      Left(ExecutionFailure.NotFound(s"program ${id.value} is not loaded by an ISA executor"))
  }

  override def catalog(id: ProgramId): Either[ExecutionFailure, ProgramCatalog] = programs.get().get(id) match {
    case None          => Left(ExecutionFailure.NotFound(s"program ${id.value} is not loaded"))
    case Some(program) => program.catalog.map(catalogProjection)
  }

  override def kernel(id: ProgramId, name: KernelName): Either[ExecutionFailure, KernelMetadata] =
    programs.get().get(id) match {
      case None          => Left(ExecutionFailure.NotFound(s"program ${id.value} is not loaded"))
      case Some(program) => program.kernel(name)
    }

  override def global(id: ProgramId, name: GlobalName): Either[ExecutionFailure, ProgramGlobal] =
    programs.get().get(id) match {
      case None          => Left(ExecutionFailure.NotFound(s"program ${id.value} is not loaded"))
      case Some(program) => program.global(name)
    }

  override def bindGlobal(
      id: ProgramId,
      name: GlobalName,
      allocation: AllocationId,
      offset: Long :| Positive0,
      allocationSize: Long :| Positive,
      memory: ExecutionMemory
  ): Either[ExecutionFailure, Unit] = lifecycleLock.withLock {
    programs.get().get(id) match {
      case None          => Left(ExecutionFailure.NotFound(s"program ${id.value} is not loaded"))
      case Some(program) => program.bindGlobal(name, allocation, offset, allocationSize, memory)
    }
  }

  override def writeGlobal(
      id: ProgramId,
      name: GlobalName,
      offset: Long :| Positive0,
      bytes: Vec[Byte]
  ): Either[ExecutionFailure, Unit] =
    lifecycleLock.withLock {
      programs.get().get(id) match {
        case None          => Left(ExecutionFailure.NotFound(s"program ${id.value} is not loaded"))
        case Some(program) => program.writeGlobal(name, offset, bytes)
      }
    }

  override def launch(
      dispatch: KernelDispatch,
      memory: ExecutionMemory,
      control: ExecutionControl
  ): Either[ExecutionFailure, Unit] =
    programs.get().get(dispatch.program) match {
      case None =>
        Left(ExecutionFailure.NotFound(s"program ${dispatch.program.value} is not loaded"))
      case Some(program) => program.launch(dispatch, memory, control)
    }
}

private[server] object ProgramRegistry {
  def validated(
      decoders: Vec[ProgramDecoder],
      catalogProjection: ProgramCatalog => ProgramCatalog = catalog => catalog
  ): Either[String, ProgramRegistry] = {
    val duplicates = Vec.from(
      decoders.groupBy(_.format).collect { case (format, owners) if owners.size > 1 => format }
    )
    Either.cond(
      duplicates.isEmpty,
      ProgramRegistry(decoders.iterator.map(decoder => decoder.format -> decoder).toMap, catalogProjection),
      s"program formats have multiple decoders: ${duplicates.sortBy(_.ordinal).mkString(", ")}"
    )
  }
}
