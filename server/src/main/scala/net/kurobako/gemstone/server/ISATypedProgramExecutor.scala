package net.kurobako.gemstone.server

import net.kurobako.gemstone.Col
import net.kurobako.gemstone.execution.*

import java.util.concurrent.atomic.AtomicReference

/** Stable format registry and prepared-program store. ISA parsing, marshalling, and execution belong to the individual
  * loaders so parallel ISA work does not converge on this file.
  */
final class ISATypedProgramExecutor private (loaders: Map[ProgramFormat, ProgramLoader]) extends ProgramExecutor {
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

  override def launch(
      dispatch: KernelDispatch,
      memory: ExecutionMemory,
      control: ExecutionControl
  ): Either[ExecutionFailure, Unit] =
    programs.get().get(dispatch.program).toRight(
      ExecutionFailure.NotFound(s"program ${dispatch.program.toLong} is not loaded")
    ).flatMap(_.launch(dispatch, memory, control))
}

object ISATypedProgramExecutor {
  private val defaultLoaders: Col[ProgramLoader] = Col(
    PTXProgramLoader,
    AMDGPUCodeObjectProgramLoader,
    AMDGPURawKernelProgramLoader,
    SPIRVProgramLoader
  )

  def default: ISATypedProgramExecutor =
    create(defaultLoaders).fold(message => throw IllegalStateException(message), identity)

  def create(loaders: Col[ProgramLoader]): Either[String, ISATypedProgramExecutor] = {
    val duplicates = Col.from(
      loaders.groupBy(_.format).collect { case (format, owners) if owners.size > 1 => format }
    )
    Either.cond(
      duplicates.isEmpty,
      ISATypedProgramExecutor(loaders.iterator.map(loader => loader.format -> loader).toMap),
      s"program formats have multiple loaders: ${duplicates.sortBy(_.ordinal).mkString(", ")}"
    )
  }
}
