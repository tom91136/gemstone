package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.Vec.*
import net.kurobako.gemstone.execution.*
import net.kurobako.gemstone.concurrent.withLock
import net.kurobako.gemstone.isa.amdgpu.CodeObject
import net.kurobako.gemstone.isa.amdgpu.CodeObject.*
import net.kurobako.gemstone.isa.amdgpu.CodeObjectMetadata
import net.kurobako.gemstone.isa.amdgpu.DispatchInterpreter
import net.kurobako.gemstone.memory.GuestAddress

import cats.syntax.all.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import java.util.concurrent.locks.ReentrantLock
import scala.collection.mutable

/** AMDGPU ELF loader with intentionally lazy full indexing. [[load]] validates the fixed ELF header immediately;
  * section tables, descriptors, target variants, and kernel instructions are validated when the first kernel is
  * resolved. Initialization successes and failures are then cached for the lifetime of the loaded program.
  */
object HSACodeObjectDecoder extends ProgramDecoder {

  private[amdgpu] object AMDGPUKernelVariants {
    private object TargetArch {
      private val Marker                                           = "target_archE"
      def unapply(value: String): Option[(String, String, String)] = {
        val start = value.indexOf(Marker)
        if start < 0 then None
        else {
          val revisionStart = start + Marker.length
          val end           = value.indexOf('E', revisionStart)
          val revision      = if end < 0 then "" else value.substring(revisionStart, end)
          Option.when(revision.nonEmpty &&
            revision.forall(character => character.isDigit || "abcdefABCDEF".contains(character)))(
            (value.substring(0, start), revision, value.substring(end + 1))
          )
        }
      }
    }
    // ROCm uses UINT_MAX as the generic (architecture-independent) dispatch
    // tag.  It is a sentinel, not a concrete architecture variant.
    private val GenericArch = "4294967295"

    private def upperAscii(value: String): String = value.map { character =>
      if (character >= 'a' && character <= 'z') (character - 'a' + 'A').toChar else character
    }

    private def logicalName(name: String): String = name match {
      case TargetArch(prefix, _, suffix) => prefix + "target_archE" + suffix
      case _                             => name
    }

    private def archSuffix(name: String): Option[String] =
      (name match {
        case TargetArch(_, revision, _) => Some(upperAscii(revision))
        case _                          => None
      }).filter(_ != GenericArch)

    /** ROCm's device code uses the coarse architecture enum in mangled helper names even when the code object target is
      * a concrete GFX revision. The code object itself remains target-specific; only the dispatch helper needs the
      * family spelling (for example, gfx1036 uses the gfx1030 enum).
      */
    private def archCandidates(targetName: String): Set[String] = {
      val target = upperAscii(targetName.drop(3))
      val family =
        if target.length == 4 && target.forall(_.isDigit) &&
          ((target.startsWith("10") && target(2) >= '3') || target.startsWith("11") || target.startsWith("12"))
        then target.take(3) + "0"
        else target
      Set(target, family)
    }

    /** Resolve the dispatch aliases emitted by HIP/ROCm. The per-architecture aliases are normally the preferred
      * implementation, but some ROCm bundles contain four-byte `s_endpgm` placeholders for those aliases and put the
      * real body in the generic (`target_archE`) symbol. The optional predicate lets the code-object loader reject
      * those placeholders without weakening the name-only helper used by its unit tests.
      */
    def select(
        names: Set[String],
        targetName: String,
        usable: String => Boolean = _ => true,
        allowUnsupported: Boolean = false
    ): Either[String, Map[String, String]] = {
      val targets  = archCandidates(targetName)
      val selected = names.groupBy(logicalName(_)).iterator.map { case (logical, entries) =>
        val variants = entries.iterator.filter(name => archSuffix(name).nonEmpty).toVec
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
      }.toVec.sequence.map(_.toMap)
      selected.map(resolved => names.iterator.map(name => name -> resolved(logicalName(name))).toMap)
    }

  }

  private final case class AMDGPUIndexedProgram(
      index: CodeObject.Index,
      aliases: Map[String, String],
      metadata: Map[String, KernelMetadata],
      catalog: ProgramCatalog
  )

  private final class AMDGPUDeferredProgramResolver(
      initialize: () => Either[ExecutionFailure, AMDGPUIndexedProgram]
  ) {
    private lazy val initialized = initialize()

    def resolveKernel(name: String): Either[ExecutionFailure, CodeObjectKernel] =
      initialized.flatMap { program =>
        program.aliases.get(name)
          .toRight(ExecutionFailure.NotFound(s"AMDGPU kernel '$name' is not present"))
          .flatMap(alias =>
            program.index.kernel(alias)
              .leftMap(error => ExecutionFailure.InvalidRequest(error.message))
          )
      }

    def kernelMetadata(name: String): Either[ExecutionFailure, KernelMetadata] =
      initialized.flatMap { program =>
        val resolved = program.aliases.getOrElse(name, name)
        program.metadata.get(resolved).orElse(program.metadata.get(name))
          .toRight(ExecutionFailure.NotFound(
            s"AMDGPU kernel metadata for '$name' is not present"
          ))
      }

    def catalog: Either[ExecutionFailure, ProgramCatalog] = initialized.map(_.catalog)

    private final case class Bound(value: DispatchInterpreter.GlobalBinding, memory: ExecutionMemory)
    private val bindingLock = ReentrantLock()
    private val bound       = mutable.Map.empty[GlobalName, Bound]

    def bindings: Vec[DispatchInterpreter.GlobalBinding] = bindingLock.withLock {
      Vec.from(bound.valuesIterator.map(_.value)).distinct
    }

    // The covered-range scan maintains one cursor while appending disjoint gaps.
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    def bindGlobal(
        name: GlobalName,
        allocation: AllocationId,
        offset: Long :| Positive0,
        allocationSize: Long :| Positive,
        memory: ExecutionMemory
    ): Either[ExecutionFailure, Unit] = bindingLock.withLock {
      initialized match {
        case Left(error)    => Left(error)
        case Right(program) =>
          program.index.globalData(name)
            .leftMap(error => ExecutionFailure.NotFound(error.message)) match {
            case Left(error)                    => Left(error)
            case Right((guestAddress, initial)) =>
              val address = guestAddress.value
              if (
                offset > allocationSize || initial.size.toLong > allocationSize - offset ||
                address < offset || address > Long.MaxValue - initial.size.toLong
              )
                Left(ExecutionFailure.InvalidRequest("global binding exceeds allocation or virtual address range"))
              else {
                val value = DispatchInterpreter.GlobalBinding(
                  address,
                  initial.nonNegativeSize,
                  allocation,
                  offset,
                  allocationSize
                )
                val old = bound.get(name)
                if (old.exists(entry => entry.value != value || !(entry.memory eq memory)))
                  Left(ExecutionFailure.InvalidRequest(
                    s"global $name is already bound to different storage"
                  ))
                else if (old.nonEmpty) Right(())
                else if (
                  bound.valuesIterator.exists { entry =>
                    val other       = entry.value
                    val overlaps    = address < other.address + other.size && other.address < address + initial.size
                    val sameMapping = allocation == other.allocation &&
                      address - offset == other.address - other.offset &&
                      allocationSize == other.allocationSize && (entry.memory eq memory)
                    (overlaps && !sameMapping) || (allocation == other.allocation && !sameMapping)
                  }
                ) Left(ExecutionFailure.InvalidRequest("global aliases have conflicting allocation mappings"))
                else {
                  // Adding an overlapping symbol alias must not overwrite already-live bound bytes with old initializers.
                  val covered = bound.valuesIterator.map(_.value).filter(other =>
                    address < other.address + other.size && other.address < address + initial.size
                  ).map(other =>
                    (math.max(address, other.address) - address).toInt ->
                      (math.min(address + initial.size, other.address + other.size) - address).toInt
                  ).toVec.sortBy(_._1)
                  val gaps       = Vec.newBuilder[(Int, Int)]
                  var coveredEnd = 0
                  covered.foreach { case (start, end) =>
                    if (start > coveredEnd) gaps += coveredEnd -> start
                    coveredEnd = math.max(coveredEnd, end)
                  }
                  if (coveredEnd < initial.size) gaps += coveredEnd -> initial.size
                  val uncovered         = gaps.result()
                  val initializedMemory = if (uncovered.isEmpty) Right(())
                  else memory.read(allocation, offset, initial.nonNegativeSize) match {
                    case Left(error)    => Left(error)
                    case Right(current) =>
                      val bytes = Vec.toArray(current)
                      uncovered.foreach { case (start, end) =>
                        val _ = initial.slice(start, end).copyToArray(bytes, start, end - start)
                      }
                      memory.write(allocation, offset, Vec.unsafeFromArray(bytes))
                  }
                  initializedMemory match {
                    case Left(error) => Left(error)
                    case Right(_)    =>
                      bound.update(name, Bound(value, memory))
                      Right(())
                  }
                }
              }
          }
      }
    }

    def writeGlobal(
        name: GlobalName,
        offset: Long :| Positive0,
        bytes: Vec[Byte]
    ): Either[ExecutionFailure, Unit] =
      bindingLock.withLock {
        initialized match {
          case Left(error)    => Left(error)
          case Right(program) =>
            program.index.global(name)
              .toRight(ExecutionFailure.NotFound(s"AMDGPU global $name is not present")) match {
              case Left(error)                 => Left(error)
              case Right((guestAddress, size)) =>
                val address = guestAddress.value
                if (offset > size || bytes.size.toLong > size.toLong - offset)
                  Left(ExecutionFailure.InvalidRequest("global write exceeds symbol bounds"))
                else {
                  val start   = address + offset
                  val covered = bound.valuesIterator.find(entry =>
                    start >= entry.value.address &&
                      start - entry.value.address <= entry.value.size.toLong - bytes.size
                  )
                  covered match {
                    case Some(entry) =>
                      entry.memory.write(
                        entry.value.allocation,
                        (entry.value.offset + start - entry.value.address).refineUnsafe[Positive0],
                        bytes
                      )
                    case None
                        if bound.valuesIterator.forall(entry =>
                          start >= entry.value.address + entry.value.size || start + bytes.size <= entry.value.address
                        ) =>
                      program.index.writeGlobal(name, offset, bytes).leftMap(error =>
                        ExecutionFailure.InvalidRequest(error.message)
                      )
                    case None =>
                      bytes.zipWithIndex.foldLeft[Either[ExecutionFailure, Unit]](Right(())) {
                        case (result, (byte, index)) =>
                          result match {
                            case error @ Left(_) => error
                            case Right(_)        =>
                              bound.valuesIterator.find(entry =>
                                start + index >= entry.value.address &&
                                  start + index < entry.value.address + entry.value.size
                              ) match {
                                case Some(entry) =>
                                  entry.memory.write(
                                    entry.value.allocation,
                                    (entry.value.offset + start + index - entry.value.address).refineUnsafe[Positive0],
                                    Vec(byte)
                                  )
                                case None =>
                                  program.index.writeGlobal(name, offset + index, Vec(byte))
                                    .leftMap(error => ExecutionFailure.InvalidRequest(error.message))
                              }
                          }
                      }
                  }
                }
            }
        }
      }

  }

  private final class AMDGPUProgramInstance(
      resolveKernel: String => Either[ExecutionFailure, CodeObjectKernel],
      describeKernel: String => Either[ExecutionFailure, KernelMetadata],
      describeCatalog: () => Either[ExecutionFailure, ProgramCatalog],
      writeGlobalFn: (GlobalName, Long :| Positive0, Vec[Byte]) => Either[ExecutionFailure, Unit],
      bindGlobalFn: (
          GlobalName,
          AllocationId,
          Long :| Positive0,
          Long :| Positive,
          ExecutionMemory
      ) => Either[ExecutionFailure, Unit],
      bindings: () => Vec[DispatchInterpreter.GlobalBinding]
  ) extends ProgramInstance {
    private val prepared = java.util.concurrent.ConcurrentHashMap[
      String,
      Either[ExecutionFailure, DispatchInterpreter.PreparedKernel]
    ]()

    override def catalog: Either[ExecutionFailure, ProgramCatalog] = describeCatalog()

    override def kernel(name: KernelName): Either[ExecutionFailure, KernelMetadata] = describeKernel(name.value)

    override def launch(
        dispatch: KernelDispatch,
        memory: ExecutionMemory,
        control: ExecutionControl
    ): Either[ExecutionFailure, Unit] = resolveKernel(dispatch.kernelName.value) match {
      case Left(error)   => Left(error)
      case Right(kernel) =>
        prepared.computeIfAbsent(kernel.name, _ => DispatchInterpreter.prepare(kernel)) match {
          case Left(error)    => Left(error)
          case Right(program) => DispatchInterpreter.execute(program, dispatch, memory, control, bindings())
        }
    }

    override def bindGlobal(
        name: GlobalName,
        allocation: AllocationId,
        offset: Long :| Positive0,
        allocationSize: Long :| Positive,
        memory: ExecutionMemory
    ): Either[ExecutionFailure, Unit] = bindGlobalFn(name, allocation, offset, allocationSize, memory)

    override def writeGlobal(
        name: GlobalName,
        offset: Long :| Positive0,
        bytes: Vec[Byte]
    ): Either[ExecutionFailure, Unit] = writeGlobalFn(name, offset, bytes)
  }

  override val format: ProgramFormat = ProgramFormat.HSACodeObjectBinary

  override def decode(image: Vec[Byte]): Either[ExecutionFailure, ProgramInstance] =
    CodeObject.validateHeader(image)
      .leftMap(error => ExecutionFailure.InvalidRequest(error.message)).map { _ =>
        val resolver = AMDGPUDeferredProgramResolver { () =>
          for {
            indexed <- CodeObject.index(image)
              .leftMap(error => ExecutionFailure.InvalidRequest(error.message))
            manifests <- CodeObjectMetadata.decodeOptional(image)
              .leftMap(error => ExecutionFailure.InvalidRequest(error.message))
              .map(_.getOrElse(Vec.empty[CodeObjectMetadata.KernelManifest]))
            selected <- AMDGPUKernelVariants.select(
              indexed.kernelNames,
              indexed.target.name,
              name =>
                indexed.kernel(name).exists { kernel =>
                  val code = Vec.toArray(kernel.code)
                  code.length != 4 || code(0) != 0x00.toByte || code(1) != 0x00.toByte ||
                  code(2) != 0x81.toByte || code(3) != 0xbf.toByte
                },
              allowUnsupported = true
            ).leftMap(detail => ExecutionFailure.InvalidRequest(detail))
            metadata = manifests.iterator.flatMap(manifest =>
              Vec(
                manifest.name.value                      -> manifest.metadata,
                manifest.symbol.value                    -> manifest.metadata,
                manifest.symbol.value.stripSuffix(".kd") -> manifest.metadata
              )
            ).toMap
            kernelSymbols <-
              if (manifests.nonEmpty) manifests.map(manifest => ProgramKernel(manifest.name, manifest.symbol)).distinct
                .asRight[ExecutionFailure]
              else indexed.kernelNames.toSeq.sorted.traverse { name =>
                for {
                  kernelName <- KernelName.either(name).leftMap(_ =>
                    ExecutionFailure.InvalidRequest(
                      s"invalid AMDGPU kernel name '$name'"
                    )
                  )
                  linkageName <- LinkageName.either(name).leftMap(_ =>
                    ExecutionFailure.InvalidRequest(
                      s"invalid AMDGPU linkage name '$name'"
                    )
                  )
                } yield ProgramKernel(kernelName, linkageName)
              }.map(Vec.from)
            kernelLinkage: Set[String] = kernelSymbols.iterator.map(_.linkageName.value: String).toSet
            globals <- indexed.globals.filterNot(global => kernelLinkage.contains(global.name.value)).traverse { global =>
              ProgramGlobalMetadata.validated(
                global.name,
                Some(global.address),
                global.size.toLong.refineUnsafe[Positive0],
                global.pointerOffsets
              ).leftMap(detail => ExecutionFailure.InvalidRequest(detail))
            }
          } yield AMDGPUIndexedProgram(indexed, selected, metadata, ProgramCatalog(kernelSymbols, globals))
        }
        AMDGPUProgramInstance(
          resolver.resolveKernel(_),
          resolver.kernelMetadata(_),
          () => resolver.catalog,
          resolver.writeGlobal(_, _, _),
          resolver.bindGlobal(_, _, _, _, _),
          () => resolver.bindings
        )
      }
}
