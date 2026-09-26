package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.{NonNegativeLong, PositiveLong, Vec}
import net.kurobako.gemstone.Vec.*
import net.kurobako.gemstone.concurrent.withLock
import net.kurobako.gemstone.isa.{
  GlobalName,
  GlobalSymbol,
  KernelMetadata,
  KernelName,
  KernelSymbol,
  LinkageName,
  ProgramSymbols
}
import net.kurobako.gemstone.isa.amdgpu.CodeObject
import net.kurobako.gemstone.isa.amdgpu.CodeObject.*
import net.kurobako.gemstone.isa.amdgpu.CodeObjectMetadata
import net.kurobako.gemstone.isa.amdgpu.VMLoadedProgram.GlobalBinding
import net.kurobako.gemstone.vm.{Fault, Memory, MemoryRegion}

import cats.syntax.all.*

import java.util.concurrent.locks.ReentrantLock
import scala.collection.mutable

/** AMDGPU ELF loader with intentionally lazy full indexing. [[load]] validates the fixed ELF header immediately;
  * section tables, descriptors, target variants, and kernel instructions are validated when the first kernel is
  * resolved. Initialization successes and failures are then cached for the lifetime of the loaded program.
  */
private[amdgpu] object VMLoader {

  private[amdgpu] object AMDGPUKernelVariants {
    private object TargetArch {
      private val Marker                                           = "target_archE"
      def unapply(value: String): Option[(String, String, String)] = {
        val start = value.indexOf(Marker)
        if (start < 0) None
        else {
          val revisionStart = start + Marker.length
          val end           = value.indexOf('E', revisionStart)
          val revision      = if (end < 0) "" else value.substring(revisionStart, end)
          Option.when(revision.nonEmpty && revision.forall(character => Character.digit(character, 16) >= 0))(
            (value.substring(0, start), revision, value.substring(end + 1))
          )
        }
      }
    }
    // ROCm uses UINT_MAX as the generic (architecture-independent) dispatch
    // tag.  It is a sentinel, not a concrete architecture variant.
    private val GenericArch = "4294967295"

    private def upperAscii(value: String): String = value.toUpperCase(java.util.Locale.ROOT)

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
        if (
          target.length == 4 && target.forall(_.isDigit) &&
          ((target.startsWith("10") && target(2) >= '3') || target.startsWith("11") || target.startsWith("12"))
        )
          target.take(3) + "0"
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
      val selected = names.groupBy(logicalName(_)).view.map { case (logical, entries) =>
        val variants = entries.view.filter(name => archSuffix(name).nonEmpty).toVec
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
      selected.map(resolved => names.view.map(name => name -> resolved(logicalName(name))).toMap)
    }

  }

  private final case class AMDGPUIndexedProgram(
      index: CodeObject.Index,
      aliases: Map[String, String],
      metadata: Map[String, KernelMetadata],
      symbols: ProgramSymbols
  )

  private final class AMDGPUDeferredProgramResolver(
      initialize: () => Either[Fault, AMDGPUIndexedProgram]
  ) {
    private lazy val initialized = initialize()

    def resolveKernel(name: String): Either[Fault, CodeObjectKernel] =
      initialized.flatMap { program =>
        program.aliases.get(name)
          .toRight(Fault.InvalidTask(s"AMDGPU kernel '$name' is not present"))
          .flatMap(alias =>
            program.index.kernel(alias)
              .leftMap(error => Fault.InvalidTask(error.message))
          )
      }

    def kernelMetadata(name: String): Either[Fault, KernelMetadata] =
      initialized.flatMap { program =>
        val resolved = program.aliases.getOrElse(name, name)
        program.metadata.get(resolved).orElse(program.metadata.get(name))
          .toRight(Fault.InvalidTask(
            s"AMDGPU kernel metadata for '$name' is not present"
          ))
      }

    def symbols: Either[Fault, ProgramSymbols] = initialized.map(_.symbols)

    def global(name: GlobalName): Either[Fault, (NonNegativeLong, Vec[Byte])] =
      initialized.flatMap { program =>
        program.index.globalData(name)
          .leftMap(error => Fault.Memory(error.message))
          .map { case (_, initial) => NonNegativeLong.unsafe(initial.size.toLong) -> initial }
      }

    private final case class Bound(value: GlobalBinding, memory: Memory)
    private val bindingLock = ReentrantLock()
    private val bound       = mutable.Map.empty[GlobalName, Bound]

    def bindings: Vec[GlobalBinding] = bindingLock.withLock {
      Vec.from(bound.values.view.map(_.value)).distinct
    }

    // The covered-range scan maintains one cursor while appending disjoint gaps.
    @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
    def bindGlobal(
        name: GlobalName,
        allocation: MemoryRegion,
        offset: NonNegativeLong,
        allocationSize: PositiveLong,
        memory: Memory
    ): Either[Fault, Unit] = bindingLock.withLock {
      initialized match {
        case Left(error)    => Left(error)
        case Right(program) =>
          program.index.globalData(name)
            .leftMap(error => Fault.Memory(error.message)) match {
            case Left(error)                    => Left(error)
            case Right((guestAddress, initial)) =>
              val address = guestAddress.raw
              if (
                offset.raw > allocationSize.raw ||
                initial.size.toLong > (allocationSize.toNonNegative - offset).raw ||
                address < offset.raw || address > Long.MaxValue - initial.size.toLong
              )
                Left(Fault.InvalidTask("global binding exceeds allocation or virtual address range"))
              else {
                val value = GlobalBinding(
                  address,
                  initial.nonNegativeSize,
                  allocation,
                  offset,
                  allocationSize
                )
                val old = bound.get(name)
                if (old.exists(entry => entry.value != value || !(entry.memory eq memory)))
                  Left(Fault.InvalidTask(
                    s"global $name is already bound to different storage"
                  ))
                else if (old.nonEmpty) Right(())
                else if (
                  bound.values.view.exists { entry =>
                    val other    = entry.value
                    val overlaps = address < other.address + other.size.raw &&
                      other.address < address + initial.size
                    val sameMapping = allocation == other.allocation &&
                      address - offset.raw == other.address - other.offset.raw &&
                      allocationSize == other.allocationSize && (entry.memory eq memory)
                    (overlaps && !sameMapping) || (allocation == other.allocation && !sameMapping)
                  }
                ) Left(Fault.InvalidTask("global aliases have conflicting allocation mappings"))
                else {
                  // Adding an overlapping symbol alias must not overwrite already-live bound bytes with old initializers.
                  val covered = bound.values.view.map(_.value).filter(other =>
                    address < other.address + other.size.raw && other.address < address + initial.size
                  ).map(other =>
                    (math.max(address, other.address) - address).toInt ->
                      (math.min(address + initial.size, other.address + other.size.raw) - address).toInt
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
        offset: NonNegativeLong,
        bytes: Vec[Byte]
    ): Either[Fault, Unit] =
      bindingLock.withLock {
        initialized match {
          case Left(error)    => Left(error)
          case Right(program) =>
            program.index.global(name)
              .toRight(Fault.Memory(s"AMDGPU global $name is not present")) match {
              case Left(error)                 => Left(error)
              case Right((guestAddress, size)) =>
                val address = guestAddress.raw
                if (offset.raw > size.raw || bytes.size.toLong > size.raw.toLong - offset.raw)
                  Left(Fault.InvalidTask("global write exceeds symbol bounds"))
                else {
                  val start   = address + offset.raw
                  val covered = bound.values.view.find(entry =>
                    start >= entry.value.address &&
                      start - entry.value.address <= entry.value.size.raw.toLong - bytes.size
                  )
                  covered match {
                    case Some(entry) =>
                      entry.memory.write(
                        entry.value.allocation,
                        NonNegativeLong.unsafe(entry.value.offset.raw + start - entry.value.address),
                        bytes
                      )
                    case None
                        if bound.values.view.forall(entry =>
                          start >= entry.value.address + entry.value.size.raw ||
                            start + bytes.size <= entry.value.address
                        ) =>
                      program.index.writeGlobal(name, offset.raw, bytes).leftMap(error =>
                        Fault.InvalidTask(error.message)
                      )
                    case None =>
                      bytes.zipWithIndex.foldLeft[Either[Fault, Unit]](Right(())) {
                        case (result, (byte, index)) =>
                          result match {
                            case error @ Left(_) => error
                            case Right(_)        =>
                              bound.values.view.find(entry =>
                                start + index >= entry.value.address &&
                                  start + index < entry.value.address + entry.value.size.raw
                              ) match {
                                case Some(entry) =>
                                  entry.memory.write(
                                    entry.value.allocation,
                                    NonNegativeLong.unsafe(
                                      entry.value.offset.raw + start + index - entry.value.address
                                    ),
                                    Vec(byte)
                                  )
                                case None =>
                                  program.index.writeGlobal(name, offset.raw + index, Vec(byte))
                                    .leftMap(error => Fault.InvalidTask(error.message))
                              }
                          }
                      }
                  }
                }
            }
        }
      }

  }

  private[amdgpu] def load(program: Program): Either[Fault, VMLoadedProgram] = {
    val image = program.image
    CodeObject.validateHeader(image)
      .leftMap(error => Fault.InvalidTask(error.message)).map { _ =>
        val resolver = AMDGPUDeferredProgramResolver { () =>
          for {
            indexed <- CodeObject.index(image)
              .leftMap(error => Fault.InvalidTask(error.message))
            manifests <- CodeObjectMetadata.decodeOptional(image)
              .leftMap(error => Fault.InvalidTask(error.message))
              .map(_.getOrElse(Vec.empty[CodeObjectMetadata.KernelManifest]))
            selected <- AMDGPUKernelVariants.select(
              indexed.kernelNames,
              indexed.target.name.raw,
              name =>
                indexed.kernel(name).exists { kernel =>
                  val code = Vec.toArray(kernel.code)
                  code.length != 4 || code(0) != 0x00.toByte || code(1) != 0x00.toByte ||
                  code(2) != 0x81.toByte || code(3) != 0xbf.toByte
                },
              allowUnsupported = true
            ).leftMap(detail => Fault.InvalidTask(detail))
            metadata = manifests.view.flatMap(manifest =>
              Vec(
                manifest.name.raw                      -> manifest.metadata,
                manifest.symbol.raw                    -> manifest.metadata,
                manifest.symbol.raw.stripSuffix(".kd") -> manifest.metadata
              )
            ).toMap
            kernelSymbols <-
              if (manifests.nonEmpty) manifests.map(manifest => KernelSymbol(manifest.name, manifest.symbol)).distinct
                .asRight[Fault]
              else indexed.kernelNames.toSeq.sorted.traverse { name =>
                for {
                  kernelName <- KernelName.either(name).leftMap(_ =>
                    Fault.InvalidTask(
                      s"invalid AMDGPU kernel name '$name'"
                    )
                  )
                  linkageName <- LinkageName.either(name).leftMap(_ =>
                    Fault.InvalidTask(
                      s"invalid AMDGPU linkage name '$name'"
                    )
                  )
                } yield KernelSymbol(kernelName, linkageName)
              }.map(Vec.from)
            kernelLinkage: Set[String] = kernelSymbols.view.map(_.linkageName.raw: String).toSet
            globals <- indexed.globals.filterNot(global => kernelLinkage.contains(global.name.raw)).traverse { global =>
              GlobalSymbol.validated(
                global.name,
                Some(global.address),
                global.size.toLong,
                global.pointerOffsets
              ).leftMap(detail => Fault.InvalidTask(detail))
            }
          } yield AMDGPUIndexedProgram(indexed, selected, metadata, ProgramSymbols(kernelSymbols, globals))
        }
        VMLoadedProgram(
          resolver.resolveKernel(_),
          resolver.kernelMetadata(_),
          () => resolver.symbols,
          resolver.global(_),
          resolver.writeGlobal(_, _, _),
          resolver.bindGlobal(_, _, _, _, _),
          () => resolver.bindings
        )
      }
  }
}
