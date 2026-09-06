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
  // ROCm uses UINT_MAX as the generic (architecture-independent) dispatch
  // tag.  It is a sentinel, not a concrete architecture variant.
  private val GenericArch = "4294967295"

  private def upperAscii(value: String): String = value.map { character =>
    if (character >= 'a' && character <= 'z') (character - 'a' + 'A').toChar else character
  }

  private def logicalName(name: String): String = TargetArch.replaceAllIn(name, "target_archE")

  private def archSuffix(name: String): Option[String] =
    TargetArch.findFirstMatchIn(name)
      .map(matchValue => upperAscii(matchValue.group(1)))
      .filter(_ != GenericArch)

  /** ROCm's device code uses the coarse architecture enum in mangled helper names even when the code object target is a
    * concrete GFX revision. The code object itself remains target-specific; only the dispatch helper needs the family
    * spelling (for example, gfx1036 uses the gfx1030 enum).
    */
  private def archCandidates(targetName: String): Set[String] = {
    val target = upperAscii(targetName.drop(3))
    val family =
      if (target.matches("10[3-9][0-9]")) target.take(3) + "0"
      else if (target.matches("11[0-9][0-9]")) target.take(3) + "0"
      else if (target.matches("12[0-9][0-9]")) target.take(3) + "0"
      else target
    Set(target, family)
  }

  /** Resolve the dispatch aliases emitted by HIP/ROCm. The per-architecture aliases are normally the preferred
    * implementation, but some ROCm bundles contain four-byte `s_endpgm` placeholders for those aliases and put the real
    * body in the generic (`target_archE`) symbol. The optional predicate lets the code-object loader reject those
    * placeholders without weakening the name-only helper used by its unit tests.
    */
  def select(
      names: Set[String],
      targetName: String,
      usable: String => Boolean = _ => true,
      allowUnsupported: Boolean = false
  ): Either[String, Map[String, String]] = {
    val targets  = archCandidates(targetName)
    val selected = names.groupBy(logicalName).iterator.map { case (logical, entries) =>
      val variants = entries.iterator.filter(name => archSuffix(name).nonEmpty).toCol
      if (variants.isEmpty) Right(logical -> entries.min)
      else {
        val targetVariant = variants.find(name => archSuffix(name).exists(targets.contains))
        // There can be several generic aliases in one ROCm bundle.  Pick the
        // first usable body rather than depending on Set iteration order: some
        // aliases are four-byte s_endpgm placeholders while the UINT_MAX
        // sentinel carries the actual implementation.
        val generic = entries.find(name => archSuffix(name).isEmpty && usable(name))
        // A fat code object also carries compile-time verifier entries and
        // other deliberately unsupported kernels.  Their every variant can
        // be a stub; that must not make the entire module unloadable.  Keep a
        // deterministic fallback for those names and let execution report a
        // fault only if the application actually dispatches one.
        val fallback = targetVariant.orElse(entries.find(name => archSuffix(name).isEmpty))
        val result   = targetVariant
          .filter(usable)
          .orElse(generic.filter(usable))
          .orElse(Option.when(allowUnsupported)(fallback).flatten)
          .toRight(s"AMDGPU kernel variant $logical has no $targetName implementation")
          .map(logical -> _)
        result
      }
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

private final case class AMDGPUIndexedProgram(index: CodeObject.Index, aliases: Map[String, String])

@SuppressWarnings(Array("scalafix:DisableSyntax.var"))
private final class AMDGPUDeferredProgramResolver(
    initialize: () => Either[ExecutionFailure, AMDGPUIndexedProgram]
) {
  private var pending          = initialize
  private lazy val initialized = {
    val result = pending()
    pending = null
    result
  }

  def resolveKernel(name: String): Either[ExecutionFailure, CodeObjectKernel] =
    initialized.flatMap { program =>
      program.aliases.get(name)
        .toRight(ExecutionFailure.NotFound(s"AMDGPU kernel '$name' is not present"))
        .flatMap(alias => program.index.kernel(alias).leftMap(error => ExecutionFailure.InvalidRequest(error.message)))
    }

  def writeGlobal(name: String, offset: Long, bytes: Col[Byte]): Either[ExecutionFailure, Unit] =
    initialized.flatMap(_.index.writeGlobal(name, offset, bytes).leftMap(error =>
      ExecutionFailure.InvalidRequest(error.message)
    ))
}

private final class AMDGPULoadedProgram(
    resolveKernel: String => Either[ExecutionFailure, CodeObjectKernel],
    writeGlobalFn: (String, Long, Col[Byte]) => Either[ExecutionFailure, Unit]
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

  override def writeGlobal(name: String, offset: Long, bytes: Col[Byte]): Either[ExecutionFailure, Unit] =
    writeGlobalFn(name, offset, bytes)
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
      val resolver = AMDGPUDeferredProgramResolver { () =>
        CodeObject.index(image).leftMap(error => ExecutionFailure.InvalidRequest(error.message)).flatMap { indexed =>
          AMDGPUKernelVariants.select(
            indexed.kernelNames,
            indexed.target.name,
            name =>
              indexed.kernel(name).exists { kernel =>
                val code = Col.toArray(kernel.code)
                code.length != 4 || code(0) != 0x00.toByte || code(1) != 0x00.toByte ||
                code(2) != 0x81.toByte || code(3) != 0xbf.toByte
              },
            allowUnsupported = true
          )
            .leftMap(ExecutionFailure.InvalidRequest.apply)
            .map(selected => AMDGPUIndexedProgram(indexed, selected))
        }
      }
      AMDGPULoadedProgram(resolver.resolveKernel, resolver.writeGlobal)
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
