package net.kurobako.gemstone.isa.amdgpu

import cats.syntax.all.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.string.*

/** Resolves the per-architecture kernel aliases HIP/ROCm emit (`..._target_archE<revision>E...`). */
private object KernelVariants {
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
  // ROCm uses UINT_MAX as the architecture-independent dispatch tag, not a concrete variant.
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

  /** ROCm mangles the coarse architecture enum into helper names even for a concrete GFX revision (gfx1036 uses the
    * gfx1030 enum, and rocPRIM spells gfx90a as 910).
    */
  private def archCandidates(targetName: String): Vec[String] = {
    val target = if (targetName == "gfx90a") "910" else targetName.drop(3).toUpperCaseRoot
    val family =
      if (
        target.length == 4 && target.forall(_.isDigit) &&
        ((target.startsWith("10") && target(2) >= '3') || target.startsWith("11") || target.startsWith("12"))
      )
        target.take(3) + "0"
      else target
    Vec(target, family).distinct
  }

  /** Maps every name to the body it dispatches to. Some ROCm bundles carry four-byte `s_endpgm` placeholders for the
    * target aliases and the real body in a generic one; `usable` rejects placeholders. Verifier entries whose every
    * variant is a placeholder keep a deterministic fallback, so loading succeeds and only a dispatch of one faults.
    */
  def select(
      names: Set[String],
      targetName: String,
      usable: String => Boolean
  ): Either[NonEmpty[String], Map[String, String]] = {
    val targets = archCandidates(targetName)
    names.groupBy(logicalName(_)).view.map { case (logical, names) =>
      val entries  = names.toVector.sorted
      val variants = entries.view.filter(name => archSuffix(name).nonEmpty).toVec
      if (variants.isEmpty) Right(logical -> entries.min)
      else {
        val targetVariants = targets.flatMap(target => variants.filter(name => archSuffix(name).contains(target)))
        lazy val generic   = entries.find(name => archSuffix(name).isEmpty && usable(name))
        val fallback       = targetVariants.headOption.orElse(entries.find(name => archSuffix(name).isEmpty))
        targetVariants.find(usable).orElse(generic).orElse(fallback)
          .toRight[NonEmpty[String]](s"AMDGPU kernel variant $logical has no $targetName implementation")
          .map(logical -> _)
      }
    }.toVec.sequence.map(_.toMap).map(resolved => names.view.map(name => name -> resolved(logicalName(name))).toMap)
  }
}
