package net.kurobako.gemstone.server

import net.kurobako.gemstone.Col
import net.kurobako.gemstone.Col.*
import net.kurobako.gemstone.execution.*
import net.kurobako.gemstone.isa.amdgpu.CodeObject
import net.kurobako.gemstone.isa.amdgpu.CodeObjectKernel
import net.kurobako.gemstone.isa.amdgpu.DispatchInterpreter

import cats.syntax.all.*

private[server] object AMDGPUKernelVariants {
  private val TargetArch = "target_archE([0-9A-Fa-f]+)E".r

  private def upperAscii(value: String): String = value.map { character =>
    if (character >= 'a' && character <= 'z') (character - 'a' + 'A').toChar else character
  }

  private def logicalName(name: String): String = TargetArch.replaceAllIn(name, "target_archE")

  private def archSuffix(name: String): Option[String] =
    TargetArch.findFirstMatchIn(name).map(matchValue => upperAscii(matchValue.group(1)))

  /** HIP code objects carry a generic dispatch symbol as well as per-target implementations. The generic symbol is a
    * valid metadata entry, but its body is an end-program stub; bind it to this code object's target body.
    */
  def select(names: Set[String], targetName: String): Either[String, Map[String, String]] = {
    val target   = upperAscii(targetName.drop(3))
    val selected = names.groupBy(logicalName).iterator.map { case (logical, entries) =>
      val variants = entries.iterator.filter(name => archSuffix(name).nonEmpty).toCol
      if (variants.isEmpty) Right(logical -> entries.min)
      else
        variants.find(name => archSuffix(name).contains(target))
          .toRight(s"AMDGPU kernel variant $logical has no $targetName implementation")
          .map(logical -> _)
    }.toCol.sequence.map(_.toMap)
    selected.map(resolved => names.iterator.map(name => name -> resolved(logicalName(name))).toMap)
  }

}

@SuppressWarnings(Array("scalafix:DisableSyntax.var"))
private[server] final class AMDGPUDeferredKernelResolver[A](
    initialize: () => Either[ExecutionFailure, String => Either[ExecutionFailure, A]]
) {
  private var pending          = initialize
  private lazy val initialized = {
    val result = pending()
    pending = null
    result
  }

  def resolve(name: String): Either[ExecutionFailure, A] = initialized.flatMap(_(name))
}

private final class AMDGPULoadedProgram(
    resolveKernel: String => Either[ExecutionFailure, CodeObjectKernel]
) extends LoadedProgram {
  private val prepared = java.util.concurrent.ConcurrentHashMap[
    String,
    Either[ExecutionFailure, DispatchInterpreter.PreparedKernel]
  ]()

  override def launch(
      dispatch: KernelDispatch,
      memory: ExecutionMemory,
      control: ExecutionControl
  ): Either[ExecutionFailure, Unit] =
    resolveKernel(dispatch.kernelName).flatMap { kernel =>
      prepared.computeIfAbsent(kernel.name, _ => DispatchInterpreter.prepare(kernel))
    }.flatMap(DispatchInterpreter.execute(_, dispatch, memory, control))
}

private final class AMDGPUPreparedProgram(kernel: DispatchInterpreter.PreparedKernel) extends LoadedProgram {
  override def launch(
      dispatch: KernelDispatch,
      memory: ExecutionMemory,
      control: ExecutionControl
  ): Either[ExecutionFailure, Unit] =
    Either.cond(
      dispatch.kernelName == kernel.kernel.name,
      kernel,
      ExecutionFailure.NotFound(s"AMDGPU kernel '${dispatch.kernelName}' is not present")
    ).flatMap(DispatchInterpreter.execute(_, dispatch, memory, control))
}

/** AMDGPU ELF loader with intentionally lazy full indexing. [[load]] validates the fixed ELF header immediately;
  * section tables, descriptors, target variants, and kernel instructions are validated when the first kernel is
  * resolved. Initialization successes and failures are then cached for the lifetime of the loaded program.
  */
object AMDGPUCodeObjectProgramLoader extends ProgramLoader {
  override val format: ProgramFormat = ProgramFormat.AMDGPUCodeObject

  override def load(image: Col[Byte]): Either[ExecutionFailure, LoadedProgram] =
    CodeObject.validateHeader(image).leftMap(error => ExecutionFailure.InvalidRequest(error.message)).map { _ =>
      val resolver = AMDGPUDeferredKernelResolver[CodeObjectKernel] { () =>
        CodeObject.index(image).leftMap(error => ExecutionFailure.InvalidRequest(error.message)).flatMap { indexed =>
          AMDGPUKernelVariants.select(indexed.kernelNames, indexed.target.name)
            .leftMap(ExecutionFailure.InvalidRequest.apply).map { selected => (requested: String) =>
              selected.get(requested)
                .toRight(ExecutionFailure.NotFound(s"AMDGPU kernel '$requested' is not present"))
                .flatMap(name => indexed.kernel(name).leftMap(error => ExecutionFailure.InvalidRequest(error.message)))
            }
        }
      }
      AMDGPULoadedProgram(resolver.resolve)
    }
}

object AMDGPURawKernelProgramLoader extends ProgramLoader {
  override val format: ProgramFormat = ProgramFormat.AMDGPURawKernel

  override def load(image: Col[Byte]): Either[ExecutionFailure, LoadedProgram] =
    CodeObject.decodeRaw(image)
      .leftMap(error => ExecutionFailure.InvalidRequest(error.message))
      .flatMap(DispatchInterpreter.prepare)
      .map(AMDGPUPreparedProgram.apply)
}
