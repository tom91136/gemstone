package net.kurobako.gemstone.server

import net.kurobako.gemstone.Col
import net.kurobako.gemstone.execution.*

import java.util.concurrent.atomic.AtomicReference

/** Stable format registry and prepared-program store. ISA parsing, marshalling, and execution belong to the individual
  * loaders so parallel ISA work does not converge on this file.
  */
final class RegisteredProgramExecutor private (loaders: Map[ProgramFormat, ProgramLoader]) extends ProgramExecutor {
  private val programs      = AtomicReference(Map.empty[ProgramId, LoadedProgram])
  private val lifecycleLock = Object()

  override def load(id: ProgramId, format: ProgramFormat, image: Col[Byte]): Either[ExecutionFailure, Unit] =
    lifecycleLock.synchronized {
      if (programs.get().contains(id))
        Left(ExecutionFailure.InvalidRequest(s"program ${id.toLong} is already loaded"))
      else
        loaders.get(format).toRight(
          ExecutionFailure.Unsupported(s"no program loader owns format $format")
        ).flatMap(_.load(image)).map { prepared =>
          programs.set(programs.get().updated(id, prepared))
        }
    }

  override def unload(id: ProgramId): Either[ExecutionFailure, Unit] = lifecycleLock.synchronized {
    val current = programs.get()
    Either.cond(
      current.contains(id),
      programs.set(current.removed(id)),
      ExecutionFailure.NotFound(s"program ${id.toLong} is not loaded by an ISA executor")
    )
  }

  override def bindGlobal(
      id: ProgramId,
      name: String,
      allocation: AllocationId,
      offset: Long,
      allocationSize: Long,
      memory: ExecutionMemory
  ): Either[ExecutionFailure, Unit] = lifecycleLock.synchronized {
    programs.get().get(id).toRight(ExecutionFailure.NotFound(s"program ${id.toLong} is not loaded"))
      .flatMap(_.bindGlobal(name, allocation, offset, allocationSize, memory))
  }

  override def writeGlobal(
      id: ProgramId,
      name: String,
      offset: Long,
      bytes: Col[Byte]
  ): Either[ExecutionFailure, Unit] =
    lifecycleLock.synchronized {
      programs.get().get(id).toRight(
        ExecutionFailure.NotFound(s"program ${id.toLong} is not loaded")
      ).flatMap(_.writeGlobal(name, offset, bytes))
    }

  override def launch(
      dispatch: KernelDispatch,
      memory: ExecutionMemory,
      control: ExecutionControl
  ): Either[ExecutionFailure, Unit] =
    programs.get().get(dispatch.program).toRight(
      ExecutionFailure.NotFound(s"program ${dispatch.program.toLong} is not loaded")
    ).flatMap(_.launch(dispatch, memory, control))
}

object RegisteredProgramExecutor {
  private val defaultLoaders: Col[ProgramLoader] = Col(
    PTXProgramLoader,
    AMDGPUCodeObjectProgramLoader,
    AMDGPURawKernelProgramLoader,
    SPIRVProgramLoader
  )

  def default: RegisteredProgramExecutor =
    create(defaultLoaders).fold(message => throw IllegalStateException(message), identity)

  def create(loaders: Col[ProgramLoader]): Either[String, RegisteredProgramExecutor] = {
    val duplicates = Col.from(
      loaders.groupBy(_.format).collect { case (format, owners) if owners.size > 1 => format }
    )
    Either.cond(
      duplicates.isEmpty,
      RegisteredProgramExecutor(loaders.iterator.map(loader => loader.format -> loader).toMap),
      s"program formats have multiple loaders: ${duplicates.sortBy(_.ordinal).mkString(", ")}"
    )
  }
}
