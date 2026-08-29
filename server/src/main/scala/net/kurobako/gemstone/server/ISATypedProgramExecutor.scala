package net.kurobako.gemstone.server

import net.kurobako.gemstone.Col
import net.kurobako.gemstone.execution.*

import java.util.concurrent.atomic.AtomicReference

/** Stable format registry and prepared-program store. ISA parsing, marshalling, and execution belong to the individual
  * loaders so parallel ISA work does not converge on this file.
  */
final class ISATypedProgramExecutor private (loaders: Map[ProgramFormat, ProgramLoader]) extends ProgramExecutor {
  private val programs = AtomicReference(Map.empty[ProgramId, LoadedProgram])

  override def load(id: ProgramId, format: ProgramFormat, image: Col[Byte]): Either[ExecutionFailure, Unit] =
    loaders.get(format).toRight(
      ExecutionFailure.Unsupported(s"no program loader owns format $format")
    ).flatMap(_.load(image)).flatMap { prepared =>
      val previous = programs.getAndUpdate(current =>
        if (current.contains(id)) current else current.updated(id, prepared)
      )
      Either.cond(
        !previous.contains(id),
        (),
        ExecutionFailure.InvalidRequest(s"program ${id.toLong} is already loaded")
      )
    }

  override def unload(id: ProgramId): Either[ExecutionFailure, Unit] = {
    val previous = programs.getAndUpdate(_.removed(id))
    Either.cond(
      previous.contains(id),
      (),
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
