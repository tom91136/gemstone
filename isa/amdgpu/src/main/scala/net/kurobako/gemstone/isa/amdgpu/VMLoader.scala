package net.kurobako.gemstone.isa.amdgpu

import ELF.*
import cats.syntax.all.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.string.*
import net.kurobako.gemstone.vm.*

/** Indexes AMDGPU ELF sections, descriptors, and target variants. [[LoadedProgram]] owns lazy initialization and caches
  * both successful indexes and failures after validating the ELF header at load time.
  */
private object VMLoader {

  object AMDGPUKernelVariants {
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

    private def logicalName(name: String): String = name match {
      case TargetArch(prefix, _, suffix) => prefix + "target_archE" + suffix
      case _                             => name
    }

    private def archSuffix(name: String): Option[String] =
      (name match {
        case TargetArch(_, revision, _) => Some(revision.toUpperCaseRoot)
        case _                          => None
      }).filter(_ != GenericArch)

    /** ROCm's device code uses the coarse architecture enum in mangled helper names even when the code object target is
      * a concrete GFX revision. The code object itself remains target-specific; only the dispatch helper needs the
      * family spelling (for example, gfx1036 uses the gfx1030 enum).
      */
    private def archCandidates(targetName: String): Vec[String] = {
      val target = targetName.drop(3).toUpperCaseRoot
      val family =
        if (
          target.length == 4 && target.forall(_.isDigit) &&
          ((target.startsWith("10") && target(2) >= '3') || target.startsWith("11") || target.startsWith("12"))
        )
          target.take(3) + "0"
        else target
      Vec(target, family).distinct
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
    ): Either[NonEmpty[String], Map[String, String]] = {
      val targets  = archCandidates(targetName)
      val selected = names.groupBy(logicalName(_)).view.map { case (logical, names) =>
        val entries  = names.toVector.sorted
        val variants = entries.view.filter(name => archSuffix(name).nonEmpty).toVec
        if (variants.isEmpty) Right(logical -> entries.min)
        else {
          val targetVariants = targets.flatMap(target => variants.filter(name => archSuffix(name).contains(target)))
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
          val fallback = targetVariants.headOption.orElse(entries.find(name => archSuffix(name).isEmpty))
          val result   = targetVariants.find(usable)
            .orElse(generic)
            .orElse(Option.when(allowUnsupported)(fallback).flatten)
            .toRight[NonEmpty[String]](s"AMDGPU kernel variant $logical has no $targetName implementation")
            .map(logical -> _)
          result
        }
      }.toVec.sequence.map(_.toMap)
      selected.map(resolved => names.view.map(name => name -> resolved(logicalName(name))).toMap)
    }

  }

  case class IndexedProgram(
      index: ELF.Index,
      aliases: Map[String, String],
      metadata: Map[String, KernelMetadata],
      symbols: ProgramSymbols
  )

  def index(program: CodeObject): Either[Fault, IndexedProgram] = {
    val image = program.image
    for {
      indexed <- ELF.index(image)
        .leftMap(error => Fault.InvalidTask(error.message))
      manifests <- CodeObjectMetadata.decodeNotes(indexed.notes)
        .leftMap(error => Fault.InvalidTask(error.message))
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
      ).leftMap(detail => Fault.InvalidTask(detail))
      metadata = manifests.view.flatMap(manifest =>
        Vec(
          manifest.name                      -> manifest.metadata,
          manifest.symbol                    -> manifest.metadata,
          manifest.symbol.stripSuffix(".kd") -> manifest.metadata
        )
      ).toMap
      kernelSymbols <-
        if (manifests.nonEmpty) manifests.map(manifest => KernelSymbol(manifest.name, manifest.symbol)).distinct
          .asRight[Fault]
        else indexed.kernelNames.toSeq.sorted.traverse { name =>
          for {
            kernelName <- name.refine[NonEmpty[String]].leftMap(_ =>
              Fault.InvalidTask(
                s"invalid AMDGPU kernel name '$name'"
              )
            )
          } yield KernelSymbol(kernelName, kernelName)
        }.map(Vec.from)
      kernelLinkage: Set[String] = kernelSymbols.view.map(_.linkageName: String).toSet
      globals <- indexed.globals.filterNot(global => kernelLinkage.contains(global.name)).traverse { global =>
        GlobalSymbol.validated(
          global.name,
          Some(global.address),
          global.size.toLong,
          global.pointerOffsets
        ).leftMap(detail => Fault.InvalidTask(detail))
      }
    } yield IndexedProgram(indexed, selected, metadata, ProgramSymbols(kernelSymbols, globals))
  }
}
